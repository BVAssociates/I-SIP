@rem = '--*-Perl-*--
@echo off
if "%OS%" == "Windows_NT" goto WinNT
perl -x -S "%0" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto endofperl
:WinNT
perl -x -S %0 %*
if NOT "%COMSPEC%" == "%SystemRoot%\system32\cmd.exe" goto endofperl
if %errorlevel% == 9009 echo You do not have Perl in your PATH.
if errorlevel 1 goto script_failed_so_exit_with_non_zero_val 2>nul
goto endofperl
@rem ';
#!/usr/bin/perl
#line 15

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog qw($logger);

#  Documentation
###########################################################
=head1 NAME

PC_PURGE_FILES - supprime les fichiers temporaires et journaux

=head1 SYNOPSIS

 PC_PURGE_FILES.pl [-h][-v] -d nbjours
 
=head1 DESCRIPTION

Supprime les fichiers temporaires et journaux plus vieux que N jours.

=over

=item journaux d'execution

=item historique d'execution

=item fichier générés

=back

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -d : nombre de jours à garder

=back

=head1 ARGUMENTS


=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	exit shift;
}

sub usage($) {
	my $verbosity=shift;
	pod2usage(-verbose => $verbosity, -noperldoc => 1);
	sortie(202); 
}

sub log_erreur {
	#print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	$logger->error(@_);
	sortie(202);
}

sub log_info {
	#print STDERR "INFO: ".join(" ",@_)."\n"; 
	$logger->notice(@_);
}


#  Traitement des Options
###########################################################


my %opts;
getopts('hvd:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

usage($debug_level+1) if $opts{d} !~ /^\d+$/;
my $day_keep=$opts{d};

#  Traitement des arguments
###########################################################

if ( @ARGV < 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}


#  Corps du script
###########################################################

use Isip::IsipConfig;
use Isip::Environnement;

use Isis::Jobstat;
use Isip::IsipLog qw(delete_log);
use Date::Calc qw(Now Today Add_Delta_Days);

my @timestamp_log;
my @file_name;

my $sqlite_stat_file=$ENV{ISIP_DATA}.'/tab/SCRIPT_STAT.sqlite';
my $sqlite_stat=JobStat->open($sqlite_stat_file,'SCRIPT_STAT');
# remove dead process entries
$sqlite_stat->clean_dead_process();

my $timestamp_back;
if ($day_keep >= 0) {
	# take now time
	my @date=Now();
	# take yesterday date
	unshift @date,Add_Delta_Days(Today(),-$day_keep);
	$timestamp_back=sprintf("%d%02d%02dT%02d%02d%02d", @date);
	$sqlite_stat->query_condition("TIMESTAMP < '$timestamp_back'");
}
else {
	log_erreur("veuillez spécifier un nombre de jours > 1");
}

while (my %row=$sqlite_stat->fetch_row()) {
	push @timestamp_log, $row{TIMESTAMP};
	push @file_name, $row{OUTPUT_FILE} if $row{OUTPUT_FILE};
}

# delete logs
foreach my $time (@timestamp_log) {
	delete_log($time);
}

# delete files
foreach my $file (@file_name) {
	my $filepath = $ENV{ISIP_DATA}.'/export/'.$file;
	if (-r $filepath) {
		$logger->notice("supprime $filepath");
		unlink($filepath);
	}
	else {
		warn("impossible de retrouver le fichier généré : $file");
	}
}

#delete history
$logger->notice("supprime historique < $timestamp_back");
$sqlite_stat->purge($timestamp_back);

# clean DATE_UPDATE
$logger->notice("nettoyage des locks de baselines");
foreach my $environnement ( IsipConfig->new()->get_environnement_list() ) {
	my $env = Environnement->new($environnement);
	
	my $date_update_table = $env->open_local_table("DATE_UPDATE");
	$date_update_table->execute("DELETE FROM DATE_UPDATE WHERE DIFF_VALUE IS NULL AND DIFF_STRUCT IS NULL");
}
__END__
:endofperl
