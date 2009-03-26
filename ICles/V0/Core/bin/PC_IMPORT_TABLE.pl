#!/usr/bin/perl
package pc_import_table;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_IMPORT_TABLE - Initalise la table de données d'historique d'une table

=head1 SYNOPSIS

 PC_IMPORT_TABLE.pl -i environnement_source environnement
 
=head1 DESCRIPTION

Ajoute les informations de colonnes d'une table dans l'environnement
Creer une base de donnée historique.

Si l'option -i est utilisée, une première collecte sera effecutée


=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -i environnement_source : nom de l'environnement qui contient les table à importer

=back

=head1 ARGUMENTS

=over

=item environnement : environnement à utiliser

=item tablename : table dont la base sera créé

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

sub run {
local @ARGV=@_;
# BEGIN RUN

#  Traitement des Options
###########################################################

log_info("Debut du programme : ".__PACKAGE__." ".join(" ",@ARGV));

my %opts;
getopts('hvi:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $import=$opts{i};
usage(1) if not $import;

#  Traitement des arguments
###########################################################

if ( @ARGV != 1 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift @ARGV;


#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
require 'pc_init_table.pl';
require 'pc_generate_menu.pl';

my $env_sip_from=Environnement->new($import);

my @table_list=$env_sip_from->get_table_list();

foreach my $table (@table_list) {
	my %table_info=$env_sip_from->get_table_info($table);
	next if $table_info{type_source} eq "XML";
	
	$bv_severite+=pc_init_table::run("-Mci".$import,$environnement,$table);
}

$bv_severite+=pc_generate_menu::run($environnement);

return $bv_severite;
# END RUN
}

exit run(@ARGV) if !caller;