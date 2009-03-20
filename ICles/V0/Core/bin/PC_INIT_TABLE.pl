#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_INIT_TABLE - Initalise la table de données d'historique d'une table

=head1 SYNOPSIS

 PC_INIT_TABLE.pl [-h] [-v ] [-c] environnement tablename
 
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

=item -c : Creation complète

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


#  Traitement des Options
###########################################################

my %opts;
getopts('hvc', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $create=$opts{c};

#  Traitement des arguments
###########################################################

log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

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
use Isip::ITable::DataDiff;


my $env_sip = Environnement->new($environnement);

if ($create) {
	$ENV{"Environnement"}=$environnement;
	my $table_list=ITools->open("TABLE_ODBC");
	$table_list->query_condition("TABLE_NAME = '$table_name'");
	$table_list->query_field("TABLE_TEXT");
	my ($table_desc)=$table_list->fetch_row_array();
	
	if (not ($ENV{TABLE_MODULE}
			and $ENV{TABLE_TYPE}
			and $ENV{TABLE_LABEL}))
	{
		log_erreur("L'environnement n'est pas positionné (TABLE_MODULE,TABLE_TYPE,TABLE_LABEL) pour l'ajout de table");
	}
	
	
	
	my %new_line;
	$new_line{TABLE_NAME}=$table_name;
	$new_line{MODULE}=$ENV{TABLE_MODULE};
	$new_line{TYPE_SOURCE}=$ENV{TABLE_TYPE};
	$new_line{LABEL_FIELD}=$ENV{TABLE_LABEL};
	$new_line{DESCRIPTION}=$table_desc;
	$new_line{ACTIVE}=1;
	$new_line{ROOT_TABLE}=1;
	
	my $table=$env_sip->open_local_table("TABLE_INFO");
	foreach ($table->field) {
		$new_line{$_}="" if not exists $new_line{$_};
	}
	$table->insert_row(%new_line);
}

$env_sip = Environnement->new($environnement);
my %table_info = $env_sip->get_table_info($table_name);

if (not %table_info) {
	log_info("la table $table_name n'est pas connue ou désactivée");
	$bv_severite=202;
}
else {
	$logger->notice("Create database for table",$table_name);
	my $current_table=$env_sip->open_source_table($table_name, {debug => $debug_level});
	
	$env_sip->initialize_column_info($current_table);

	$env_sip->create_database_histo($table_name);
}

if (not $create) {
	require "PC_GENERATE_MENU.pl";
	pc_generate_menu::run($environnement,$table_name);
}

sortie($bv_severite);