#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_INSERT_HISTO.pl - Insert toutes les donnes de la table Reference dans la table Historique

=head1 SYNOPSIS

 PC_INSERT_HISTO.pl.pl environnement tablename
 
=head1 DESCRIPTION

Insert toutes les donnes de la table Reference dans la table Historique.

Si des données sont déjà présentes dans la table, elles sont écrasées.
Les anciennes modifications restent cependant accessible par accès direct à la table Historique.

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head1 ARGUMENTS

=head2 * environnement à utiliser

=head2 * table a traiter

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


my %opts;
getopts('hv', \%opts);

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
use IKOS::SIP;
use IKOS::DATA::ITools;

use POSIX qw(strftime);
    

my $env_sip = SIP->new($environnement);

my $ikos_table = ITools->open("INFO_TABLE");

$ikos_table->query_condition("TABLE_NAME = '$table_name'") if $table_name;

while (my %ikos_table_line = $ikos_table->fetch_row() ) {

	my $table_name=$ikos_table_line{TABLE_NAME};
	
	#open IKOS table for DATA
	my $current_table=$env_sip->open_ikos_table($table_name, {debug => $debug_level});
	my $histo_table=$env_sip->open_local_from_histo_table($table_name, {debug => $debug_level});
	my $table_key= $ikos_table_line{PRIMARY_KEY} ;
	
	if (not $table_key) {
		warn "No KEY defined for $table_name\n";
		next;
	}
	

	my $date_current = strftime "%Y-%m-%d %H:%M:%S", localtime;
	my @field_list=$current_table->query_field();
	my @row_list;
	$histo_table->begin_transaction();
	while (my %data_line=$current_table->fetch_row() ) {
		$histo_table->insert_row(%data_line);
	}
	$histo_table->commit_transaction();
		
}

sortie($bv_severite);