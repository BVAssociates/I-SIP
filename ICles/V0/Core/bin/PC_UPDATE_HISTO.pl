#!/usr/bin/perl

package pc_update_histo;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_UPDATE_HISTO - Met à jour les champs d'une table Histo depuis la référence

=head1 SYNOPSIS

 PC_UPDATE_HISTO.pl [-h] [-v] [-d] [-m module] environnement [tablename]
 
=head1 DESCRIPTION

Met à jour les champs d'une table suffixée par _HISTO depuis une table IKOS par ODBC.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -n : Mode simulation (aucune modification)

=item -d : enregistre la date

=item -m module : n'effectue la collecte que sur les tables de "module"

=back

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

sub run {
	local @ARGV=@_;
	
	
	#  Traitement des Options
	###########################################################


	log_info("Debut du programme : ".__PACKAGE__." ".join(" ",@ARGV));

	my %opts;
	getopts('hvnm:d', \%opts) or usage(0);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	my $module_name=$opts{m};
	my $save_date=$opts{d};

	#  Traitement des arguments
	###########################################################

	if ( @ARGV < 1 ) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}
	my $environnement=shift @ARGV;
	my $table_name=shift @ARGV;

	#  Corps du script
	###########################################################


	
	my $bv_severite=0;

	use Isip::Environnement;
	use ITable::ITools;
	use Isip::ITable::DataDiff;
	use Isip::IsipTreeCache;

	my $env_sip = Environnement->new($environnement);

	my @list_table;
	if (not $table_name) {
		@list_table=$env_sip->get_table_list();
	} else {
		if ($env_sip->get_table_info($table_name)) {
			@list_table=($table_name);
		}
		else {
			log_erreur("$table_name n'est pas connue");
		}
	}

	if ($module_name) {
		@list_table=$env_sip->get_table_list_module($module_name);
		log_erreur("no table in module $module_name") if not @list_table;
		if ($table_name) {
			if (!grep {$_ eq $table_name} @list_table) {
				log_erreur("no table $table_name in module $module_name");
			}
			@list_table=($table_name);
		}
	}


	# set global timestamp for update
	use POSIX qw(strftime);
	my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
	log_info("Date de collecte utilisée : $timestamp");


	# create cache object (save parent state)
	my $cache=IsipTreeCache->new($env_sip);
	# empty disk cache
	foreach my $current_table (@list_table) {
		# TODO problem here, because of deleting data from other tables
		#$cache->clear_dirty_cache($current_table);
	}

	my $total_diff_counter=0;
	my $counter=0;
	my $source_table;
	my %diff_list;
	foreach my $current_table (@list_table) {
		
		if ( not $env_sip->exists_histo_table($current_table) )  {
			$logger->error("$current_table n'a pas été initialisée");
			next;
		}
		
		$counter++;
		
		log_info("Connexion à la table source : $current_table");
		# open source table depending on TYPE_SOURCE
		$source_table=$env_sip->open_source_table($current_table);
		
		
		log_info("Connexion à la base d'historisation");
		my $histo_table=$env_sip->open_local_from_histo_table($current_table, {debug => $debug_level, timeout => 100000});
		 $histo_table->query_field($source_table->query_field);
		
		my $force_comment;
		if ($histo_table->is_empty) {
			
			$force_comment=1;
			log_info("la table $current_table n'a jamais été collectée");
		}
		
		# set timestamp for all update/insert operation
		$histo_table->set_update_timestamp($timestamp);
		
		my $table_diff=DataDiff->open($source_table, $histo_table, {debug => $debug_level});

		log_info("Debut de la comparaison de $current_table");
		my $diff_obj=$table_diff->compare();
		log_info("Nombre de différences : ".$diff_obj->count);

		if (exists $opts{n}) {
			log_info("Simulation : les changements n'ont pas été appliqués");
		} else {
		
			#write changes on disk
			my $diff_counter;
			$diff_counter= $table_diff->update_compare_target();
			$total_diff_counter += $diff_counter;
			if ($diff_counter) {
				$histo_table->{table_histo}->execute("ANALYZE");
				log_info("Les changements ont ete appliqués sur $current_table (lignes mises à jour : $diff_counter)");
				
						
				#my ($current_vol,$current_dir,$current_script)=splitpath($0);
				#my $cmd=catpath($current_vol,$current_dir,"PC_UPDATE_CACHE.pl");
				#@ARGV=($environnement);
				#do $cmd or die "erreur pendant la mise à jour du cache";
				#compute new cache
				
				#store diff and continue.
				$diff_list{$current_table}=$diff_obj;
				
				if ($force_comment) {
					# execute special query on table backend
					$logger->notice("Set STATUS to Valide");
					$histo_table->{table_histo}->execute("UPDATE $current_table\_HISTO
						SET STATUS='Valide',
							COMMENT='Creation'");
				}
			} else {
				log_info("Aucune mise à jour sur $current_table");
			}
			
		}

	}

	#write date in baselines
	if ($save_date and $total_diff_counter) {
		log_info("Sauvegarde de la date de collecte");
		my $table_date=ITools->open("DATE_UPDATE", {debug => $debug_level});
		$table_date->insert_row(ENVIRON => $environnement,
								DATE_UPDATE => $timestamp,
								DESCRIPTION => "",
								BASELINE => 0);
	}

	#log_info("Mise à jour du cache");
	#foreach (keys %diff_list) {	
	#	$cache->add_dirty_diff($_,$diff_list{$_});
	#}
	# flush cache to disk
	#$cache->update_dirty_cache();

	log_info("Nombre de lignes mises à jour effectuées au total : $total_diff_counter");

	if (not exists $opts{n}) {
		log_info("mise à jour du cache");
		require "pc_update_cache.pl";
		my @args=($environnement,$table_name);
		unshift @args,("-m",$module_name) if $module_name;
		$bv_severite+=pc_update_cache::run(@args);
	}

	return $bv_severite;
}

exit run(@ARGV) if !caller;

1;