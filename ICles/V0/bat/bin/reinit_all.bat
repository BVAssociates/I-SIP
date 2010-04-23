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

reinit_all - lance une réinitialisation complète

=head1 SYNOPSIS

 reinit_all.pl [-h][-v] 
 
=head1 DESCRIPTION

Lance une réinitialisation complète des informations de table et d'environnement dans
les bases Sqlite

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
getopts('hvc', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV != 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::IsipConfig;
use Isip::Environnement;
require 'pc_init_env.pl';
require 'pc_init_table.pl';
require 'pc_generate_menu.pl';
require 'pc_update_cache.pl';


my $config=IsipConfig->new();

foreach my $environ ($config->get_environnement_list) {

	
	my $datasource=$config->get_odbc_datasource_name($environ);
	
	$bv_severite+=pc_init_env::run($datasource,$environ);

	my $sip=Environnement->new($environ);
	foreach my $table_name ( $sip->get_table_list() ) {
		$bv_severite+=pc_init_table::run("-M",$environ,$table_name);
	}
	
}

$bv_severite+=pc_generate_menu::run($_) foreach ($config->get_environnement_list);
$bv_severite+=pc_update_cache::run($_) foreach ($config->get_environnement_list);
__END__
:endofperl
