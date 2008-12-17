#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_VALIDATE_TABLE - Valide une table entiere

=head1 SYNOPSIS

 PC_VALIDATE_TABLE.pl environnement tablename [commentaire]
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement à la date courante

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head1 ARGUMENTS

=head2 * environnement à utiliser

=head2 * table a valider

=head2 * commentaire à ajouter (par defaut : "validation globale")

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
getopts('hv', \%opts);

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
my $environnement=shift;
my $table_name=shift;
my $commentaire=shift or $commentaire="Validation globale";

#  Corps du script
###########################################################
my $bv_severite=0;

use IKOS::SIP;
$sip = SIP->new($environnement);

$histo_table=$sip->open_local_table($table_name."_HISTO", {debug => $debug_level);

$histo_table->execute("UPDATE $table_name\_HISTO
		SET STATUS='".$histo_table->{valid_keyword}."'");

sortie($bv_severite);