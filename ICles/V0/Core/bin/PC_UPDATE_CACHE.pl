#!/usr/bin/env perl

package pc_update_cache;

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

 PC_UPDATE_CACHE.pl [-h] [-v] [-m module] environnement
 
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

sub run {
	local @ARGV=@_;

	log_info("Debut du programme : ".__PACKAGE__." ".join(" ",@ARGV));

	my %opts;
	getopts('hvnm:', \%opts);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	my $module=$opts{m};

	usage($debug_level+1) if $opts{h};

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
	use ITable::ITools;
	use Isip::ITable::DataDiff;
	use Isip::IsipTreeCache;
	use Isip::Cache::CacheStatus;
	use Isip::Cache::CacheProject;

	my $env_sip = Environnement->new($environnement);

	my @list_table;
	if ($module) {
		my @temp_list_table=$env_sip->get_table_list_module($module);
		
		# add dependants tables from other module to table list
		my $links=$env_sip->get_links();
		my %table_uniq;
		foreach (@temp_list_table) {
			$table_uniq{$_}++;
			my @depend=$links->get_parent_tables($_,1);
			foreach (@depend) {
				$table_uniq{$_}++;
			}
		}
		@list_table=sort keys %table_uniq;
	}
	else {
		@list_table=$env_sip->get_table_list();
	}

	
	

	my $counter=0;
	my $source_table;

	my $cache=IsipTreeCache->new($env_sip);
	$cache->add_dispatcher(CacheStatus->new($env_sip));
	$cache->add_dispatcher(CacheProject->new($env_sip));

	foreach my $current_table (@list_table) {
		
		if ( not $env_sip->exists_histo_table($current_table) ) {
			$logger->error("$current_table n'a pas été initialisée");
			next;
		}
		
		$counter++;
		
			
		log_info("Mise à jour du cache des icones de $current_table");
		my $histo_table=$env_sip->open_local_from_histo_table($current_table, {debug => $debug_level, timeout => 100000});
		
		my $type_rules = IsipRules->new($current_table,$env_sip);

		$histo_table->isip_rules($type_rules);

		$histo_table->output_separator('@');
		$histo_table->query_field("ICON","PROJECT",$histo_table->field);

		# optimisation : preselect lines
		$histo_table->query_condition( q{PROJECT IS NOT NULL OR PROJECT !='' OR STATUS != 'Valide' OR STATUS IS NULL} );
		
		while (my %row=$histo_table->fetch_row) {
			$cache->recurse_line($current_table, \%row);
		}
	}

	if ($module) {
		$cache->clear_cache(@list_table);
	}
	else {
		$cache->clear_cache();
	}
	$cache->save_cache;
	
	return 1;
}


exit !run(@ARGV) if !caller;
1;