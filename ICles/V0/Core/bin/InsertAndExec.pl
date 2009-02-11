#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

InsertAndExec - Insert une ligne dans un processeur Administrate

=head1 SYNOPSIS

 InsertAndExec [-h] [-f] [-v] INTO <Table> VALUES <-|Values>
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement � la date courante

=head1 ENVIRONNEMENT

=over 4

=item ITOOLS : L'environnement des ITools doit �tre charg�

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne (combiner avec -v pour l'aide compl�te)

=item -v : Mode verbeux

=item -f pour forcer la cr�ation du fichier de donn�es

=back

=head1 ARGUMENTS

=over 4

=item Table : Nom de la table dans laquelle ins�rer les valeurs

=item - : Utiliser les valeurs provenant de l'entr�e standard

=item Values : Valeurs � ins�rer dans la table

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

my $INTO_WORD=shift;
my $table_name=shift;
my $VALUES_WORD=shift;
my @values=@ARGV;

if (uc($INTO_WORD) ne 'INTO' or uc($VALUES_WORD) ne 'VALUES') {
	log_info("Ligne de commande incorrect");
	usage($debug_level);
	sortie(202);
}

#  Corps du script
###########################################################
my $bv_severite=0;

# if we administrate a table other than FIELD_*, we use the original script
if ($table_name !~ /^IKOS_FIELD/) {
	my $values=join('',@values);
	system("Insert INTO $table_name VALUES \"$values\"");
	exit $? >> 8;
}

log_info("Cannot insert lines");

sortie(202);