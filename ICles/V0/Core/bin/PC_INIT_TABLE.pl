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

 PC_INIT_TABLE.pl [-h] [-v ] [-m master] [-c] environnement tablename
 
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

=item -m master : Le fichier est une copie d'un fichier "maître"

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

log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

my %opts;
getopts('hvcm:i:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $create=$opts{c};
my $master=$opts{m};
my $import=$opts{i};

#  Traitement des arguments
###########################################################

if ( @ARGV != 2 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $table_name=shift;

if ($table_name =~ /__/) {
	log_erreur("Le nom de la table ne peut pas contenir la suite de caracteres '__'");
}

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use Isip::ITable::DataDiff;


my $env_sip = Environnement->new($environnement);
my $env_sip_from=Environnement->new($import) if $import;


#######################
# mise à jour TABLE_INFO
#######################

my $table_desc="";
if ($create) {

	my %new_line;
	if ($import) {
		my %table_info_from=$env_sip_from->get_table_info($table_name);
		
		$new_line{TABLE_NAME}=$table_name;
		$new_line{MODULE}=$table_info_from{module};
		$new_line{TYPE_SOURCE}=$table_info_from{type_source};
		$new_line{LABEL_FIELD}=$table_info_from{label_field};
		$new_line{DESCRIPTION}=$table_info_from{description};
		$new_line{ACTIVE}=1;
		$new_line{ROOT_TABLE}=$table_info_from{root_table};
	}
	else {
	
		log_info("verification de l'environnement");
		if (not ($ENV{TABLE_MODULE}
				and $ENV{TABLE_TYPE}))
		{
			log_erreur("L'environnement n'est pas positionné (TABLE_MODULE,TABLE_TYPE) pour l'ajout de table");
		}
		
		if ($ENV{TABLE_TYPE} eq "ODBC") {
			
			$ENV{"Environnement"}=$environnement;
			my $table_list=ITools->open("TABLE_ODBC");
			$table_list->query_condition("TABLE_NAME = '$table_name'");
			$table_list->query_field("TABLE_TEXT");
			($table_desc)=$table_list->fetch_row_array();
		}
		elsif ($ENV{TABLE_TYPE} eq "XML") {
			
			log_info("verification de l'environnement");
			if (not $ENV{XML_PATH})
			{
				log_erreur("L'environnement n'est pas positionné (XML_PATH) pour l'ajout de table");
			}
			
			$table_desc="Fichier XML $table_name";
			
			if (not -r $ENV{XML_PATH}) {
				log_erreur("impossible d'acceder au fichier XML <$ENV{XML_PATH}>");
			}
			
			# test loading file
			use ITable::XmlFile;
			my $test_xml=XmlFile->open($ENV{XML_PATH},$table_name);
			
			my $xml_info=$env_sip->open_local_table("XML_INFO");
			my %xml_entry;
			$xml_entry{XML_NAME}=$table_name;
			$xml_entry{XML_PATH}=$ENV{XML_PATH};
					
			# defaut to 1 if master table
			$xml_entry{MASTER}=$master;
			$xml_info->insert_row(%xml_entry);
		}
		else {
			log_erreur("<TABLE_TYPE=$ENV{TABLE_TYPE}> non reconnu");
		}
		
		$new_line{TABLE_NAME}=$table_name;
		$new_line{MODULE}=$ENV{TABLE_MODULE};
		$new_line{TYPE_SOURCE}=$ENV{TABLE_TYPE};
		$new_line{LABEL_FIELD}=$ENV{TABLE_LABEL};
		$new_line{DESCRIPTION}=$table_desc;
		$new_line{ACTIVE}=1;
		$new_line{ROOT_TABLE}=1 if not $master;
	}
	my $table=$env_sip->open_local_table("TABLE_INFO");
	foreach ($table->field) {
		$new_line{$_}="" if not exists $new_line{$_};
	}
	
	$table->delete_row(%new_line);
	$table->insert_row(%new_line);
}

#######################
# mise à jour COLUMN_INFO
#######################

$env_sip = Environnement->new($environnement);
my %table_info = $env_sip->get_table_info($table_name);

if (not %table_info) {
	log_info("la table $table_name n'est pas connue ou désactivée");
	sortie(202);
}
else {
	$logger->notice("Create database for table",$table_name);
	
	my $current_table;
	my $links;
	if ($import) {
		
		$current_table=$env_sip_from->open_local_from_histo_table($table_name);
		$links=$env_sip_from->get_links();
	}
	else {
		$current_table=$env_sip->open_source_table($table_name);
	}
	
	$env_sip->initialize_column_info($current_table,$links);

	$env_sip->create_database_histo($table_name);
	
	$logger->notice("La table $table_name a été ajoutée dans l'environnement $environnement");
}

if ($create) {
	# reload environnement
	$env_sip = Environnement->new($environnement);
	
	if (not $env_sip->get_table_key($table_name)) {
		$logger->notice("###############################################");
		$logger->notice("Il n'y a pas de CLEF PRIMAIRE configurée pour $table_name");
		$logger->notice("Veuillez configurer les colonnes (menu \"Configurer Colonnes\"), puis lancer une vérification");
		$logger->notice("###############################################");
	}
}

#######################
# mise à jour MENU
#######################

if (not $create or $import) {
	require "PC_GENERATE_MENU.pl";
	pc_generate_menu::run($environnement,$table_name);
}

sortie($bv_severite);