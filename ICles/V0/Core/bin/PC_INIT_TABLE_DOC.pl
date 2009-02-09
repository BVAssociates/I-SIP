#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_INIT_TABL_DOCE - Initalise les tables Information d'une table

=head1 SYNOPSIS

 PC_INIT_TABLE.pl [-h] [-v ] tablename
 
=head1 DESCRIPTION

Lit les informations de l'environnement et initalise les tables Information d'une table.

Suivant l'implementation, créer égalemement la base associée.


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
getopts('hvic:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#defaut value
my $group_commit=1000;
$group_commit = $opts{c} if exists $opts{c};
my $populate=1 if exists $opts{i};

#  Traitement des arguments
###########################################################

if ( @ARGV != 1 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $table_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::IsipConfig;
use Isip::Environnement;
use Isip::ITable::DataDiff;
use ITable::ITools;


my $config_sip = IsipConfig->new();

my %table_info = $config_sip->get_table_info();
log_erreur("la table $table_name n'est pas connue, veuiller la configurer d'abord") if not exists $table_info{$table_name};

my %env_info=$config_sip->get_env_info();

# we take first env arbitrary
my $environnement=(sort keys %env_info)[0];
my $env_sip=Environnement->new($environnement);

$logger->notice("Create DOC database for table, based on $environnement informations : ",$table_name);
my $current_table=$env_sip->open_source_table($table_name, {debug => $debug_level});

##################################
# create table FIELD_INFO
$config_sip->initialize_database_documentation($current_table, {debug => $debug_level});

##################################
# update description in TABLE_INFO

if (not $table_info{$table_name}{description} and $table_info{$table_name}{type_source} eq "ODBC" ) {
	log_info("$table_name n'a pas de description, mise à jour depuis IKOS");
	
	# get description from source
	$current_table->custom_select_query("select DISTINCT TABLE_TEXT from QSYS2.SYSTABLES where TABLE_SCHEMA='IKGLFIC' AND TABLE_NAME='$table_name'");
	my ($table_description)=$current_table->fetch_row_array();
	
	# get current line
	my $table_info=ITools->open("TABLE_INFO", {debug => $debug_level});
	$table_info->query_condition("TABLE_NAME=$table_name");
	my %row=$table_info->fetch_row;
	
	# update modified line
	$row{DESCRIPTION}=$table_description;
	$table_info->update_row(%row);
}

sortie($bv_severite);