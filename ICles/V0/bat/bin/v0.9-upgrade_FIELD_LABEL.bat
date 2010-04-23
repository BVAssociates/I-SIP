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

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

upgrade_FIELD_LABEL - 

=head1 SYNOPSIS

 upgrade_FIELD_LABEL.pl [-h][-v]
 
=head1 DESCRIPTION

Met � jour les bases de donn�es ISIP avec la nouvelle table FIELD_LABEL (see #26) 

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over

=item environnement : environnement � utiliser

=item tablename : table a ouvrir

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

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
getopts('hv', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}


#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::IsipConfig;
use Isip::Environnement;
use Isip::HistoColumns;

my $config=IsipConfig->new();

my @environnement_list=$config->get_environnement_list();

# get update info
use POSIX qw(strftime);
my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
my $current_user=$ENV{IsisUser};
$current_user = "local" if not $current_user;

foreach (@environnement_list) {
	my $env=Environnement->new($_);
	
	# la table FIELD_LABEL sera cr��e dans la meme base que TABLE_INFO
	my $table_info = $env->open_local_table("TABLE_INFO");
	eval { $table_info->execute(
	q{CREATE TABLE IF NOT EXISTS "FIELD_LABEL" (
		"DATE_UPDATE" TEXT,
		"USER_UPDATE" TEXT NOT NULL ,
		"TABLE_NAME" TEXT NOT NULL ,
		"TABLE_KEY" TEXT,
		"FIELD_NAME" TEXT,
		"LABEL" TEXT,
		PRIMARY KEY ("TABLE_NAME","TABLE_KEY","FIELD_NAME") )
		} 
	)};
	
	my $table_label = $env->open_local_table("FIELD_LABEL");
	
	foreach my $table_name ( $env->get_table_list() ) {
		my $sqlite_path = $env->get_sqlite_path($table_name."_HISTO");
		my $table = eval { Sqlite->open($sqlite_path, $table_name."_LABEL") };
		next if $@;
		
		# import des donn�es de l'ancienne table
		while (my %row = $table->fetch_row() ) {

				$table_label->insert_row(
					DATE_UPDATE => $timestamp,
					USER_UPDATE => $current_user,
					TABLE_NAME  => $table_name,
					TABLE_KEY   => $row{"TABLE_KEY"},
					FIELD_NAME  => $row{"FIELD_NAME"},
					LABEL       => $row{"LABEL"},
				);
		}
		
		# suppression de l'ancienne table
		$table->execute("DROP TABLE ".$table_name."_LABEL");
	}
}
__END__
:endofperl
