#!/usr/bin/perl

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

 ME_COLLECTE_ALL.pl [-h][-v]
 
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
require "PC_UPDATE_HISTO.pl";

my $config=IsipConfig->new();
my @env_list=$config->get_environnement_list();

log_screen_only();

my $return_code;
my $pid;
foreach my $env (@env_list) {
	$pid = fork();
	if (!$pid) {
		# child process
		log_info("Collecte de l'environnement $env");
		$return_code += pc_update_histo::run("-d",$env);
		log_info("Terminé pour l'environnement $env");
		last;
	}
}

if ($pid) {
	log_info("Attente que tous les process se termine");
	$return_code=wait;
}

exit $return_code;