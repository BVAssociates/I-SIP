#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_XML_COPY - Liste les fichiers XML supplementaire

=head1 SYNOPSIS

 PC_LIST_XML_COPY.pl [-h][-v] [-s separateur] [-m] environnement table_name
 
=head1 DESCRIPTION

Liste les fichiers XML répliqué d'un fichier XML maitre

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -s : modifie le separateur de sortie

=item -m : affiche également les tables maitres

=back

=head1 ARGUMENTS

=over

=item environnement

=item tablename : table a ouvrir

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
getopts('hvs:m', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=',';
$separator=$opts{s} if exists $opts{s};

my $master=$opts{m} if exists $opts{m};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $table_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;

my $env=Environnement->new($environnement);

my $table_xml=$env->open_local_table("XML_INFO");

$table_xml->query_condition("XML_NAME = '$table_name'") if $table_name;

while ( my %row=$table_xml->fetch_row() ) {
	print join($separator, $row{XML_NAME}, $row{XML_PATH}, $row{MASTER})."\n" if $master or not $row{MASTER};
}

