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

upgrade_date_update - 

=head1 SYNOPSIS

 upgrade_date_update.pl [-h][-v]
 
=head1 DESCRIPTION

Met � jour la table DATE_UPDATE pour la version 0.9 (ticket #110)

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

=item old_type : type � remplacer

=item new_type : type de remplacement

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
use ITable::Sqlite;

my $config=IsipConfig->new();

my @environnement_list=$config->get_environnement_list();

foreach (@environnement_list) {
	my $env=Environnement->new($_);
	
	my $table=$env->open_local_table("DATE_UPDATE");
	
	eval {$table->execute("ALTER TABLE DATE_UPDATE ADD COLUMN FULL_UPDATE INTEGER")};
	$table->execute("UPDATE DATE_UPDATE SET FULL_UPDATE=1");
}
__END__
:endofperl
