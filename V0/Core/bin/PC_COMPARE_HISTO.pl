#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_COMPARE_HISTO - les champs les champs d'une table Historique depuis la référence

=head1 SYNOPSIS

 PC_COMPARE_HISTO.pl [-e environnement_source ] environnement_cible tablename
 
=head1 DESCRIPTION

Compare 2 objets de type DATA_interface et affiche le resultat sous forme de table

=head1 ENVIRONNEMENT

=over 4

=item Environnement : Environnement en cours d'exploration

=item DATE_EXPLORE : Date en cours d'exploration

=item ENVRION : Utilise cette valeur pour environnement_source si non spécifié

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -e environnement : environnement source

=back

=head1 ARGUMENTS

=over 4

=item * environnement de destination

=item * table a comparer

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
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	sortie(202);
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
}


#  Traitement des Options
###########################################################


my %opts;
getopts('hve:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV != 2 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
#my $environnement_from=shift;
my $environnement_to=shift;
my $table_name=shift;

#recuperation de l'environnement
my $environnement_from=$ENV{Environnement};
$environnement_from=$opts{e} if exists $opts{e};
die "Environnement does not exist in env, please use option -e" if not defined $environnement_from;
my $date_explore=$ENV{DATE_EXPLORE};

#  Corps du script
###########################################################
my $bv_severite=0;

use IKOS::SIP;
use IKOS::DATA::ITools;

use POSIX qw(strftime);

my $env_sip_from = SIP->new($environnement_from);
my $env_sip_to = SIP->new($environnement_to);
	
#open IKOS table for DATA
my $table_from=$env_sip_from->open_local_from_histo_table($table_name, {debug => $debug_level});
$table_from->query_date($date_explore) if $date_explore;

my $table_to=$env_sip_to->open_local_from_histo_table($table_name, {debug => $debug_level});

$table_from->compare_from($table_to);

sortie($bv_severite);