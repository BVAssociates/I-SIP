#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

 v1.3-upgrade - Met à jour les structures de données I-SIP pour cette version

=head1 SYNOPSIS

 v1.3-upgrade.pl [-h][-v]
 
=head1 DESCRIPTION

Met à jour les structures de données I-SIP pour cette version

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
use Isip::Environnement;

my $config=IsipConfig->new();

my @environnement_list=$config->get_environnement_list();

# get update info
#use POSIX qw(strftime);
#my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
#my $current_user=$ENV{IsisUser};
#$current_user = "local" if not $current_user;

foreach my $env_name (@environnement_list) {
	# relance l'intialisation des bases de config pour ajouter les nouvelles tables
	$config->create_database_environnement($env_name);
	
	my $env = Environnement->new($env_name);
	
	# relance l'intialisation des bases de tables
	foreach my $table_name ($env->get_table_list() ) {
		$env->create_database_histo($table_name);
	}
	# regenere les menus
	require 'PC_GENERATE_MENU.pl';
	pc_generate_menu::run($env_name);
}

warn '-' x 80 ."\n";
warn "Action non-réalisée : Créer la variable SMTP_FROM dans le chargement du portail\n";
warn "Action non-réalisée : Mise à jour de la console I-SIP\n";
