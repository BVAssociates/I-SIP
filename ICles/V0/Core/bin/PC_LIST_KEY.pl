#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_KEY - Liste les clefs primaire des tables

=head1 SYNOPSIS

 PC_LIST_KEY.pl [-h][-v] [-s separateur] [-f table] environnement
 
=head1 DESCRIPTION

Liste les clefs primaire de toutes les tables dans un environnement

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -s : modifie le separateur de sortie

=item -f table : si "table" est une table racine et quelle contient d�j� une clef
�trang�re, n'affiche que les clefs primaires de celle-ci

=back

=head1 ARGUMENTS

=over

=item environnement

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

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

# quirk! because Windows leave "%VAR%" when VAR empty in args
map {s/%\w+%//g} @ARGV;

my %opts;
getopts('hvs:f:', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=',';
$separator=$opts{s} if exists $opts{s};

my $table_foreign;
$table_foreign=$opts{f} if exists $opts{f};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;
use ITable::ITools;

my $env=Environnement->new($environnement);

# recup�re les tables qui ont une clef �trang�re sur la table
# restricton utile seulement sur les ROOT_TABLE
my %parent_tables;
if ($table_foreign && $env->is_root_table($table_foreign) ) {
	my $column_table=$env->open_local_table($table_foreign."_COLUMN");
	
	while (my %row=$column_table->fetch_row) {
		if ($row{FOREIGN_TABLE}) {
			$parent_tables{$row{FOREIGN_TABLE}}++;
		}
	}
}

# calcul la liste des tables
my @table_list;
if (%parent_tables) {
	@table_list = keys %parent_tables;
}
else {
	@table_list = $env->get_table_list();
}

# affiche les clefs primaire de la liste des tables
print join($separator, ('') x 2)."\n";
foreach my $table (@table_list) {
	my $column_table=eval { $env->open_local_table($table."_COLUMN") };
	# traitement d'une exception
	if ($@) {
		$logger->warning("R�ference � une table inconnue : $table_foreign");
		next;
	}
	
	while (my %row=$column_table->fetch_row) {		
		print join($separator,@row{$column_table->query_field})."\n" if $row{PRIMARY_KEY};
	}
}
