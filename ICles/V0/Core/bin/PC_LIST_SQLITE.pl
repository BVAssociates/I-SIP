#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_SQLITE - Liste le contenu d'une table sqlite

=head1 SYNOPSIS

 PC_LIST_SQLITE.pl [-h][-v] [-s separateur] [-c where_clause] environnement tablename
 
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

=item -c : condition de selection

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
getopts('hvs:c:', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=',';
$separator=$opts{s} if exists $opts{s};

my $where_clause=$opts{c};

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
my $table=$env->open_local_table($table_name);

if ( $where_clause ) {
	$table->query_condition($where_clause);
}

die "unable to open local $table_name\_INFO " if not defined $table;

while (my @line=$table->fetch_row_array()) {
	print join($separator,@line)."\n";
}




