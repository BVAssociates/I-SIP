#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_CATEGORY - Liste les etats possibles

=head1 SYNOPSIS

 PC_LIST_CATEGORY.pl [-h] [-v] environnement tablename
 
=head1 DESCRIPTION

Liste les etats possibles

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over

=item environnement

=item tablename

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

my $environnement=shift;
my $table_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;

my $env=Environnement->new($environnement);
my $table=$env->open_local_table($table_name."_CATEGORY");

#$table->custom_select_query("SELECT DISTINCT * FROM ".$table_name."_CATEGORY");
$table->query_field("CATEGORY");
$table->query_distinct(1);

while (my %row=$table->fetch_row()) {
	print $row{"CATEGORY"}."\n" if $row{"CATEGORY"} and $row{"CATEGORY"} ne 'vide';
}
#affiche le groupe vide
print "vide\n";

