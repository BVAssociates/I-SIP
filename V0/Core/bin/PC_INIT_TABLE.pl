#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_INIT_TABLE - Initalise les tables Historique et Information d'une table

=head1 SYNOPSIS

 PC_INIT_TABLE.pl environnement tablename
 
=head1 DESCRIPTION

Lit les informations de l'environnement et initalise les tables Historique et Information d'une table.

Suivant l'implementation, cr�er �galemement la base associ�e.


=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head1 ARGUMENTS

=head2 * environnement � utiliser

=head2 * table a d�crire

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


my $env_sip = SIP->new($environnement);

my $db2_table = ITools->open("INFO_TABLE");

$db2_table->query_condition("TABLE_NAME = '$table_name'") if $table_name;

# db2_table_line  return one row
my %db2_table_line = $db2_table->fetch_row();
log_erreur("la table $table_name n'est pas configur�e") if not %db2_table_line;

$env_sip->initialize_database($table_name, {debug => 1});

sortie($bv_severite);