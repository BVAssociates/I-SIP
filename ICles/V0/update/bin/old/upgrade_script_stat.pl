#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

upgrade_script_stat - upgrade schema  

=head1 SYNOPSIS

 upgrade_script_stat.pl [-h][-v] 
 
=head1 DESCRIPTION

Met à jour les bases de données ISIP avec la nouvelle colonne

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
getopts('hv', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}


#  Corps du script
###########################################################
my $bv_severite=0;

use ITable::Sqlite;
use ITable::ITools;

my $new_table=Sqlite->open($ENV{ISIP_DATA}.'/tab/SCRIPT_STAT.sqlite','SCRIPT_STAT');
$new_table->begin_transaction;
$new_table->execute("DELETE FROM SCRIPT_STAT");

my $old_table=ITools->open("SCRIPT_STAT");

while (my %row=$old_table->fetch_row) {

	eval {$new_table->insert_row(%row)};
	print $@ if $@;
}

$new_table->commit_transaction;