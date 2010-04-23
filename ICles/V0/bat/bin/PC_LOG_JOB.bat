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
package pc_log_job;


# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';
#use sigtrap qw(handler sortie normal-signals);
 

#  Documentation
###########################################################
=head1 NAME

ME_LOG_JOB - Annule script Perl en tâche de fond

=head1 SYNOPSIS

 ME_LOG_JOB.pl [-h][-v] TIMESTAMP
 
=head1 DESCRIPTION

Affiche log d'un programme Perl en tâche de fond

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=item TIMESTAMP : identifiant du programme par son TIMESTAMP

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

sub ErrorReport {
    return "windows erreur: ",Win32::FormatMessage( Win32::GetLastError() );
}

#  Traitement des Options
###########################################################

sub run {
	local @ARGV=@_;
	
	
my %opts;
getopts('hvfn:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

my $follow = 1 if $opts{f};
my $nb_last = $opts{n} if $opts{n};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $timestamp=shift @ARGV;


#  Corps du script
###########################################################

use Isip::IsipLog qw(tail_log);
use Isis::JobStat;

if ($follow) {
	my $sqlite_stat_file=$ENV{ISIP_DATA}.'/tab/SCRIPT_STAT.sqlite';
	my $status=JobStat->open($sqlite_stat_file,'SCRIPT_STAT');
	
	my $clean_counter=0;
	my $check_counter=0;
	my $alive=1;
	
	while($alive) {
		tail_log($timestamp,$nb_last);
		sleep 1;
		
		if (not ($check_counter++ % 5)) {
			$status->query_field("TIMESTAMP");
			$status->query_condition("TIMESTAMP = '$timestamp'","CODE IS NULL");
			$alive=0;
			while ($status->fetch_row()) {
				$alive++
			}
		}
		
		if (not ($clean_counter++ % 10)) {
			$logger->info("cleaning dead processes");
			$status->clean_dead_process();
		}
	}
}
else {
	tail_log($timestamp,$nb_last);
}

	return 1;
}

exit !run(@ARGV) if !caller;
1;
__END__
:endofperl
