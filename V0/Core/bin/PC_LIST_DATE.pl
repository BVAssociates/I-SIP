#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_DATE - Liste les date de mise à jour d'une table dans un environnement

=head1 SYNOPSIS

 PC_LIST_DATE.pl [-h][-v] environnement tablename
 
=head1 DESCRIPTION

Liste les date de collecte d'une table dans un environnement

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over 4

=item environnement : environnement à utiliser

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
my $environ=shift;
my $table_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;

my $sip=Environnement->new($environ);
my $table=$sip->open_local_table($table_name."_HISTO", {debug => $debug_level });

$table->query_field("DATE_HISTO");
$table->custom_select_query("select distinct strftime(\"%Y-%m-%d_%H:%M\",DATE_HISTO) from $table_name\_HISTO ORDER BY DATE_HISTO DESC");


die "unable to open local $table_name in env $environ" if not defined $table;

while (my @line=$table->fetch_row_array()) {
	print join(',',@line)."\n";
}
