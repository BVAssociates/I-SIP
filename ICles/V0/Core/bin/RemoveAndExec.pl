#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

RemoveAndExec - Supprime une ligne dans un processeur Administrate

=head1 SYNOPSIS

 RemoveAndExec [-h] [-v] FROM <Table> VALUES <-|Values>
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement à la date courante

=head1 ENVIRONNEMENT

=over 4

=item ITOOLS : L'environnement des ITools doit être chargé

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne (combiner avec -v pour l'aide complète)

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over 4

=item Table : Nom de la table dans laquelle supprimer les valeurs

=item - : Utiliser les valeurs provenant de l'entrée standard

=item Values : Valeurs à insérer dans la table

=back

=head1 AUTHOR

BV Associates, 16/10/2008

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

use Encode;
map {$_=encode("cp850",$_)} @ARGV;

my @argv_save=@ARGV;

my %opts;
getopts('hv', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 4) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

log_info(join(' ',@ARGV));

my $FROM_WORD=shift;
my $table_name=shift;
my $VALUES_WORD=shift;
my @values=@ARGV;

if (uc($FROM_WORD) ne 'FROM' or uc($VALUES_WORD) ne 'VALUES') {
	log_info("Ligne de commande incorrect");
	usage($debug_level);
	sortie(202);
}

#  Corps du script
###########################################################
my $bv_severite=0;
use ReplaceAndExec_ISIP;

my $values=join('',@values);

# use lib to access Sqlite
if ($table_name =~ /^TABLE_INFO|XML_INFO|CACHE_.*$/i) {
	delete_info($table_name,$values);
}
else {
	# otherwise, we use the original script
	system("Remove FROM $table_name VALUES \"$values\"");
	exit $? >> 8;
}

sortie(0);