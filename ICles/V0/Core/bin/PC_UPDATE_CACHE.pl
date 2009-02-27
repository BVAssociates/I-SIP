#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_UPDATE_CACHE - Met à jour le cache

=head1 SYNOPSIS

 PC_UPDATE_HISTO.pl [-h] [-v] [-m module] environnement tablename
 
=head1 DESCRIPTION

Met à jour le cache.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head2 -n : Mode simulation

=head2 -m : uniquement sur les tables d'un module

=head1 ARGUMENTS

=head2 environnement : environnement à utiliser

=head2 tablename : table a décrire

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	my $exit_value=shift;
	$logger->notice("Sortie du programme, code $exit_value");
	exit $exit_value;
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
getopts('hvnm:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

my $module=$opts{m};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use ITable::ITools;
use Isip::ITable::DataDiff;
use Isip::IsipTreeCache;
use Isip::Cache::CacheStatus;
use Isip::Cache::CacheProject;

my $env_sip = Environnement->new($environnement);

my %table_info = $env_sip->get_table_info();

my @list_table;
if ($module) {
	@list_table=$env_sip->get_table_list_module($module);
}
else {
	@list_table=keys %table_info;
}


my $counter=0;
my $source_table;

my $cache=IsipTreeCache->new($env_sip);
$cache->add_dispatcher(CacheStatus->new($env_sip));
$cache->add_dispatcher(CacheProject->new($env_sip));

foreach my $current_table (@list_table) {
	
	if ( not ($env_sip->exists_doc_table($current_table)
				and $env_sip->exists_histo_table($current_table)) ) {
		$logger->error("$current_table n'a pas été initialisée");
		next;
	}
	
	$counter++;
	
	
	log_erreur("la table $current_table n'est pas connue") if not exists $table_info{$current_table};
	
	if ($table_info{$current_table}->{root_table}) {
		log_info("passe $current_table (root_table)");
		next;
	}
	
	log_info("Connexion à la base d'historisation de $current_table");
	my $histo_table=$env_sip->open_local_from_histo_table($current_table, {debug => $debug_level, timeout => 100000});
	
	my $type_rules = IsipRules->new($table_name,$env_sip);

	$histo_table->isip_rules($type_rules);

	$histo_table->output_separator('@');
	$histo_table->query_field("ICON","PROJECT",$histo_table->field);

	while (my %row=$histo_table->fetch_row) {
		$cache->recurse_line($current_table, \%row);
	}
}

$cache->clear_cache(@list_table);
$cache->save_cache;


sortie($bv_severite);