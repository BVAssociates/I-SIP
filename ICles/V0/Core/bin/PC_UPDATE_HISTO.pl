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

 PC_UPDATE_HISTO.pl [-h] [-v] [-d] [-c] [-n|-u] [-m module] [-b environnement_source[@date]] environnement [tablename]
 
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

=item -k : compacte la base après collecte

=item -m module : n'effectue la collecte que sur les tables de "module"

=item -c env@date : utilise un environnement à une date comme source de donnée

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
	getopts('hvnm:dkc:u', \%opts) or usage(0);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	my $module_name=$opts{m};
	my $save_date=$opts{d};
	my $force_vacuum=$opts{k};
	my $no_update_histo=$opts{n};
	my $no_update_cache=$opts{u};
	
	my $env_compare;
	my $date_compare;
	if (exists $opts{c}) {
		if ($opts{c} =~ /@/) {
			($env_compare,$date_compare) = split(/@/,$opts{c});
		}
		else {
			$env_compare=$opts{c};
		}
	}
	

	#  Traitement des arguments
	###########################################################

	if ( @ARGV < 1 ) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}
	my $environnement=shift @ARGV;
	my $table_name=shift @ARGV;
	
	if ($env_compare and $env_compare eq $environnement) {
		log_erreur("Environnement source est identique à l'environnement cible");
	}

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
	log_info("$environnement Date de collecte utilisée : $timestamp");


	# create cache object (save parent state)
	my $cache=IsipTreeCache->new($env_sip);
	# empty disk cache
	foreach my $current_table (@list_table) {
		# TODO problem here, because of deleting data from other tables
		#$cache->clear_dirty_cache($current_table);
	}

	my $total_diff_counter=0;
	my $total_struct_diff_counter=0;
	my $counter=0;
	my $source_table;
	my %diff_list;
	
	# shuffle table for load balancing when forking
	use List::Util 'shuffle';
	@list_table = shuffle(@list_table);
	
	foreach my $current_table (@list_table) {
		
		if ( not $env_sip->exists_histo_table($current_table) )  {
			$logger->error("$current_table n'a pas été initialisée");
			next;
		}
		
		$counter++;
		
		if (not $env_compare) {
			log_info("Connexion à la table source dans $environnement : $current_table");
			# open source table depending on TYPE_SOURCE
			$source_table=$env_sip->open_source_table($current_table);
		}
		elsif ($env_compare ne $environnement) {
			log_info("Connexion à la table $env_compare : $current_table");
			my $env_from=Environnement->new($env_compare);
			$source_table=$env_from->open_local_from_histo_table($current_table, $date_compare);
		}
		else {
			die "cas impossible";
		}
		
		log_info("Connexion à la base d'historisation dans $environnement : $current_table");
		my $histo_table=$env_sip->open_local_from_histo_table($current_table);
		#$histo_table->query_field($source_table->query_field);
		
		my $force_comment;
		if ($histo_table->is_empty) {
			
			$force_comment=1;
			log_info("la table $current_table n'a jamais été collectée pour $environnement");
		}
		
		# set timestamp for all update/insert operation
		$histo_table->set_update_timestamp($timestamp);
		
		my $table_diff=DataDiff->open($source_table, $histo_table);

		log_info("Debut de la comparaison de $current_table");
		my $diff_obj=$table_diff->compare();
		log_info("Nombre de différences : ".$diff_obj->count);

		if ($no_update_histo) {
			log_info("Simulation : les changements n'ont pas été appliqués");
			
			use Data::Dumper;
			print Dumper($diff_obj);
			
		} else {
		
			#write changes on disk
			log_info("Debut de la mise à jour des données");
			my $diff_counter;
			my $diff_counter_struct;
			($diff_counter,$diff_counter_struct)= $table_diff->update_compare_target();
			$total_diff_counter += $diff_counter;
			$total_struct_diff_counter += $diff_counter_struct;
			if ($diff_counter) {
				$histo_table->{table_histo}->execute("ANALYZE");
				log_info("Les changements ont ete appliqués sur $current_table dans $environnement (lignes mises à jour : $diff_counter)");
				
						
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
				if ($force_vacuum) {
					$histo_table->{table_histo}->execute("VACUUM");
				}
			} else {
				log_info("Aucune mise à jour sur $current_table dans $environnement");
			}
			
		}

	}

	#write date in baselines
	if ($save_date and $total_diff_counter+$total_struct_diff_counter) {
		log_info("Sauvegarde de la date de collecte dans $environnement");
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

	log_info("Nombre de modification de structure effectuées au total dans $environnement : $total_struct_diff_counter");
	log_info("Nombre de lignes mises à jour effectuées au total dans $environnement : $total_diff_counter");

	if (not $no_update_cache and not $no_update_histo and $total_diff_counter+$total_struct_diff_counter) {
		log_info("mise à jour du cache pour $environnement");
		require "pc_update_cache.pl";
		my @args=($environnement);
		unshift @args,("-m",$module_name) if $module_name;
		pc_update_cache::run(@args);
	}
	return 1;
}

exit !run(@ARGV) if !caller;

1;