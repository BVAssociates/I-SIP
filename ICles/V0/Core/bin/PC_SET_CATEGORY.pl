#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_SET_CATEGORY - Liste les etats possibles

=head1 SYNOPSIS

 PC_SET_CATEGORY.pl [-h] [-v] environnement tablename clef categorie
 
=head1 DESCRIPTION

Liste les etats possibles

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

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

=item clef

=item categorie

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


my %opts;
getopts('hv', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

use Encode;
map {$_=encode("cp850",$_)} @ARGV if $^O eq 'MSWin32';

if ( @ARGV < 3) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $table_name=shift;
my $key=shift;
my $category=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;

my $env=Environnement->new($environnement);
my $table=$env->open_local_table($table_name."_CATEGORY");


$table->begin_transaction;

$table->delete_row(TABLE_KEY => $key);
if ($category) {
	$table->insert_row(TABLE_KEY => $key, CATEGORY => $category) if $category and $category ne 'vide';
	log_info("$key de $table_name affect� au groupe $category");
}
else {
	log_info("$key de $table_name affect� au groupe vide");
}

$table->commit_transaction;
