#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_UPDATE_HISTO - Met à jour les champs d'une table Historique depuis la référence

=head1 SYNOPSIS

 PC_UPDATE_HISTO.pl environnement tablename
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement à la date courante

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head2 -n : Mode simulation

=head1 ARGUMENTS

=head2 * environnement à utiliser

=head2 * table a décrire

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
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	sortie(202);
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
}


#  Traitement des Options
###########################################################


my %opts;
getopts('hvn', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV != 2 ) {
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
use ITable::ITools::ITools;
use Isip::ITable::DataDiff;

use POSIX qw(strftime);

my $env_sip = Environnement->new($environnement);

# quirk because INFO_TABLE use %Environnement%
$ENV{Environnement}=$environnement;
my $db2_table = ITools->open("INFO_TABLE" ,{debug => $debug_level});

$db2_table->query_condition("TABLE_NAME = '$table_name'") if $table_name;

while (my %db2_table_line = $db2_table->fetch_row() ) {

	my $table_name=$db2_table_line{TABLE_NAME};
	
	#open IKOS table for DATA
	my $current_table=$env_sip->open_ikos_table($table_name, {debug => $debug_level});
	my $histo_table=$env_sip->open_local_from_histo_table($table_name, {debug => $debug_level, timeout => 100000});
	
	my $table_diff=DataDiff->open($current_table, $histo_table, {debug => $debug_level});
	$table_diff->compare();

	if (not exists $opts{n}) {
		$table_diff->update_compare_target();
		$table_diff->_info("Les changements ont ete appliqués");
	}

}

sortie($bv_severite);