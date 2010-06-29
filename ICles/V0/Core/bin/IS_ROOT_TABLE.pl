#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

IS_ROOT_TABLE - sors avec un code retour 0 si une table est dans ROOT_TABLE

=head1 SYNOPSIS

 IS_ROOT_TABLE.pl [-h][-v] environnement table_name
 
=head1 DESCRIPTION

Verifie que la table "table_name" est une table "racine"

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

=over

=item environnement : nom de l'environnement

=item table_name : nom de la table

=back

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
getopts('hv', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift @ARGV;
my $tablename=shift @ARGV;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;

my $env=Environnement->new($environnement);

if ($env->is_root_table($tablename)) {
	log_info("$tablename est dans ROOT_TABLE");
	exit 0;
}
else {
	log_info("$tablename n'est pas dans ROOT_TABLE");
	exit 1;
}
