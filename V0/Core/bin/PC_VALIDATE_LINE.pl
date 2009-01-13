#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

###########################################################
=head1 NAME

PC_VALIDATE_LINE

=head1 SYNOPSIS

 PC_VALIDATE_LINE.pl environnement table_name
 
=head1 DESCRIPTION

Met le status "Valide" sur tous les champs d'une ligne

=head2 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head2 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head2 ARGUMENTS 

=over

=item environnement : environnement à utiliser

=item table_name : nom de la table a décrire

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
	pod2usage(-verbose => $verbosity+1, -noperldoc => 1);
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
getopts('Thv', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV != 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $tablename=shift;


#  Corps du script
###########################################################
use ITable::ITools;
use Isip::Environnement;

my $bv_severite=0;

## DEBUG ONLY
if (exists $opts{T}) { $ENV{RDNPRCOD}='VTS'; $bv_severite=202 };
## DEBUG ONLY

# New SIP Object instance
my $ikos_sip = Environnement->new($environnement, {debug => $debug_level});

# recuperation de la clef primaine de la table
my $table_key = $ikos_sip->get_table_key($tablename);

if (not $table_key) {
	log_erreur("pas de clef primaine pour la table $tablename");
	sortie(202);
}

log_info("recherche de la clef primaire <$table_key>");
my $table_key_value = $ENV{$table_key} if exists $ENV{$table_key};
if (not $table_key_value) {
	log_erreur("Clef primaine <$table_key> n'est pas definie dans l'environnement");
	sortie(202);
}

# fetch selected row from histo table
my $table_histo = $ikos_sip->open_local_from_histo_table($tablename, {debug => $debug_level});

log_info("Validate all field for key $table_key_value");
$table_histo->validate_row_by_key($table_key_value);

# update all field for key

