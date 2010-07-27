#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;


#  Documentation
###########################################################
=head1 NAME

Select_Sqlite - Affiche une table Sqlite à la manière d'un Select

=head1 SYNOPSIS

 Select_Sqlite.pl [-h][-v] [-s sep] [-r nb] [-d] [-f [/path/to/]base.sqlite"] tablename
 
=head1 DESCRIPTION

Cherche un fichier nommé "tablename.sqlite" dans les chemin de BV_TABPATH,
et affiche la table nommée tablename dans cette base de donnée

Il est possible de spécifier un nom de fichier Sqlite, ou son chemin complet,
si celui-ci porte un nom different.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -s : specifie le separator (',' par défaut)

=item -d : affiche la définition au lieu des données

=item -f [/path/to/]fichier.sqlite : nom ou chemin du fichier Sqlite

=item -r nb : n'affiche que les nb première lignes

=back

=head1 ARGUMENTS

=over

=item tablename : table a définir

=back

=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits réservés.

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
	#$logger->error(@_);
	sortie(202);
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
	#$logger->notice(@_);
}


#  Traitement des Options
###########################################################


my %opts;
getopts('hvs:df:r:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

my $separator=',';
$separator=$opts{s} if $opts{s};

my $definition=$opts{d};
my $sqlite_file=$opts{f};
my $row_limit=$opts{r};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $table_name=shift;


#  Corps du script
###########################################################

use ITable::Sqlite;
use File::Spec;


if (not $sqlite_file) {
	$sqlite_file=$table_name.".sqlite";
}


my $sqlite_path;

my ($volume,$directory,$file)=File::Spec->splitpath($sqlite_file);
if (not $directory) {
	if (not exists $ENV{BV_TABPATH}) {
		log_erreur("BV_TABPATH n'est pas défini dans l'environnement");
	}
	else {
		my $sep;
		if ( $^O eq 'MSWin32' ) {
			$sep=';'
		}
		else {
			$sep=':'
		}
		($sqlite_path)=grep {-e File::Spec->catfile($_,$sqlite_file)} split(/[$sep]/,$ENV{BV_TABPATH});
		if (not $sqlite_path) {
			log_erreur("Impossible de trouver <$sqlite_file> dans BV_TABPATH");
		}
		else {
			$sqlite_path=File::Spec->catfile($sqlite_path,$sqlite_file);
		}
	}
}
else {
	$sqlite_path=$sqlite_file;
}

my $itable=Sqlite->open($sqlite_path, $table_name);
$itable->output_separator($separator);

if (not $definition) {

	# modification directe de la requete
	my $tmp_query=$itable->get_query();
	$tmp_query .= " LIMIT $row_limit" if $row_limit;
	$itable->custom_select_query($tmp_query);
	
	$itable->display_table;
}
else {
	my (undef,undef,$basename)=File::Spec->splitpath($0);
	my $command=$basename;
	$command .= " -s".$separator;
	$command .= " -f ".$opts{f} if $opts{f};
	$command .= " ".$table_name;
	
	print "COMMAND=\"".$command."\"\n";
	$itable->define_table;
}

