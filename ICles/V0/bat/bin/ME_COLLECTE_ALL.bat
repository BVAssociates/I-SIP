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

use Isip::IsipLog qw($logger log_screen_only);

#  Documentation
###########################################################
=head1 NAME

ME_COLLECTE_ALL - Collecte tous les environnements

=head1 SYNOPSIS

 ME_COLLECTE_ALL.pl [-h][-v][-p]
 
=head1 DESCRIPTION

Collecte tous les environnements

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -p : utilise un processus par environnement (fork)

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
getopts('hvp', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $fork=$opts{p} if exists $opts{p};


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
require "PC_UPDATE_HISTO.pl";
require "PC_CLEAN_BASELINE.pl";

my $config=IsipConfig->new();
my @env_list=$config->get_environnement_list();

log_screen_only() if $fork;

#autoflush STDOUT
$| = 1;

#autoflush STDERR
my $old_fh = select(STDERR);
$| = 1;
select($old_fh);

my $return_code=0;
my $pid=0;
foreach my $env (@env_list) {
	$pid = fork() if $fork;
	if (!$pid) {
		# child process
		log_info("nettoyage avant collecte de l'environnement $env");
		eval { pc_clean_baseline::run($env) };
		if ($@) {
			$return_code = 202;
			log_info("Problème lors du nettoyage des bases de $env");
		}
		
		log_info("Collecte de l'environnement $env");
		eval { pc_update_histo::run("-e",$env) };
		if ($@) {
			$logger->error($@);
			$return_code = 202;
		}
		log_info("Terminé pour l'environnement $env avec le code $return_code");
		last if $fork;
	}
	sleep 2;
}

if ($fork and $pid) {
	log_info("Attente que tous les process se termine");
	
	while (wait != -1) {
		log_info("Processus terminé avec le code $?");
		$return_code += $?;
	}
	log_info("Collecte complète avec le code $return_code");
}

exit $return_code;
__END__
:endofperl
