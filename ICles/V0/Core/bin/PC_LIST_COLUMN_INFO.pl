#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_COLUMN_INFO - Liste les colonnes d'une table

=head1 SYNOPSIS

 PC_LIST_COLUMN_INFO.pl [-h][-v] [-s separateur] environnement table
 
=head1 DESCRIPTION

Affiche le contenu d'une table de la base de donnée d'information de l'environnement

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -s : modifie le separateur de sortie

=back

=head1 ARGUMENTS

=over

=item environnement

=item table_name : table a ouvrir

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
getopts('hvs:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=',';
$separator=$opts{s} if exists $opts{s};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift @ARGV;
my $table_name=shift @ARGV;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;

#my @query_field=("TABLE_NAME", "FIELD_NAME", "DATE_HISTO", "DATE_UPDATE", "USER_UPDATE", "DATA_TYPE", "DATA_LENGTH", "TEXT", "TYPE", "PRIMARY_KEY", "FOREIGN_TABLE", "FOREIGN_KEY", "COLNO");
my @query_field=("DATE_HISTO","TABLE_NAME", "FIELD_NAME",  "TEXT", "TYPE", "PRIMARY_KEY", "FOREIGN_TABLE", "FOREIGN_KEY");

my $env=Environnement->new($environnement);

my $column_table=$env->open_local_table($table_name."_COLUMN");
$column_table->query_condition("COLNO > 0");
$column_table->query_sort("COLNO");
$column_table->query_field(@query_field);

while (my %row=$column_table->fetch_row()) {
	print join($separator, @row{@query_field})."\n";
}
