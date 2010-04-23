@rem = '--*-Perl-*--
@echo off
if "%OS%" == "Windows_NT" goto WinNT
perl -x -S "%0" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto endofperl
:WinNT
perl -x -S %0 %*
if NOT "%COMSPEC%" == "%SystemRoot%\system32\cmd.exe" goto endofperl
if %errorlevel% == 9009 echo You do not have Perl in your PATH.
if errorlevel 1 goto script_failed_so_exit_with_non_zero_val 2>nul
goto endofperl
@rem ';
#!/usr/bin/perl
#line 15

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

 PC_UPDATE_HISTO.pl [-h] [-v] [-e] [-c] [-n|-u] [-m module] [-b environnement_source[@date]] environnement [tablename]
 
=head1 DESCRIPTION

Met à jour les champs d'une table suffixée par _HISTO depuis une table IKOS par ODBC.

Si aucun module, ni aucune table n'est spécifié, la mise à jour est faite sur
tout l'environnement.
Dans ce cas, la date de collecte est enregistré dans DATE_UPDATE

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -n : Mode simulation (aucune modification)

=item -k : compacte la base après collecte

=item -e : réinitialise l'environnement (description, structure)

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
	getopts('hvnm:kc:ue', \%opts) or usage(0);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	my $module_name=$opts{m};
	my $force_vacuum=$opts{k};
	my $no_update_histo=$opts{n};
	my $no_update_cache=$opts{u};
	my $update_env=$opts{e};
	
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
	
	my $full_update;
	$full_update=1 if not $module_name and not $table_name;

	#  Corps du script
	###########################################################


	
	my $bv_severite=0;

	use Isip::Environnement;
	use Isip::IsipConfig;
	use ITable::ITools;
	use Isip::ITable::DataDiff;
	use Isip::IsipTreeCache;

	if ($update_env) {
		my $config=IsipConfig->new();
	
		require "PC_INIT_ENV.pl";
		pc_init_env::run($config->get_odbc_datasource_name($environnement), $environnement);
	}
	
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
	
	
	log_info("Verification de la date de collecte dans $environnement");
	my $table_date=$env_sip->open_local_table("DATE_UPDATE", {debug => $debug_level});
	#write line to "lock" date
	eval {
		$table_date->insert_row(DATE_HISTO  => $timestamp,
								DESCRIPTION => undef,
								DIFF_VALUE  => undef,
								DIFF_STRUCT => undef,
								BASELINE    => 0,
								FULL_UPDATE => 0,
								);
	};
	
	if ($@) {
		if ( $@ =~ /DATE_HISTO is not unique/ ) {
			log_erreur("Un collecte semble déjà en cours, veuillez réessayer dans quelques minutes");
		}
		else {
			#$@ =~ s{^\d+/\d+/\d+ \d+:\d+:\d+:(ERROR|CRITICAL)}{};
			log_erreur($@);
		}
	}
	
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
			$logger->error("$current_table n'a pas été initialisée dans $environnement");
			next;
		}
		
		$counter++;
		
		if (not $env_compare) {
			log_info("Connexion à la table source dans $environnement : $current_table");
			# open source table depending on TYPE_SOURCE
			$source_table=eval { $env_sip->open_source_table($current_table)};
			if ($@) {
				$logger->error($@);
				next;
			}
			if (not $source_table or not $source_table->key) {
				$logger->error("la table <$current_table> a besoin d'être configurée dans $environnement");
				next;
			}
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
		
		if (not $histo_table) {
			log_erreur("La table $current_table n'a pas été initialisée");
		}
		
		my $histo_is_empty;
		if ($histo_table->is_empty) {
			
			$histo_is_empty=1;
			log_info("la table $current_table n'a jamais été collectée pour $environnement");
		}
		
		# set timestamp for all update/insert operation
		$histo_table->set_update_timestamp($timestamp);
		
		my $table_diff=eval { DataDiff->open($source_table, $histo_table) };
		if ($@) {
			$logger->error($@);
			next;
		}

		log_info("Debut de la comparaison de $current_table");
		my $diff_obj=eval { $table_diff->compare() };
		if ($@) {
			$logger->error($@);
			next;
		}
		
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
			
			my @new_fields=$table_diff->get_diff_object()->get_source_only_field();
			if (not $histo_is_empty and @new_fields) {
				$logger->notice("force le status à VALIDE pour les nouvelles colonnes");
				$histo_table->{table_histo}->execute("UPDATE $current_table\_HISTO
						SET STATUS='Valide',
							COMMENT='Creation'
						WHERE FIELD_NAME IN (".join(',', map {"\'".$_."\'"} @new_fields).")");
			}
			
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
				
				if ($histo_is_empty) {
					# execute special query on table backend
					$logger->notice("force le status à VALIDE");
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

	log_info("Sauvegarde de la date de collecte dans $environnement");
	$table_date=$env_sip->open_local_table("DATE_UPDATE", {debug => $debug_level});
	
	if ($full_update) {
		log_info("collecte complète : recupératon des statistiques des collecte partielles");
		
		# recuperation des données depuis la dernière complète
		$table_date->custom_select_query(
			q{ SELECT sum(DIFF_VALUE), sum(DIFF_STRUCT) FROM DATE_UPDATE
			   WHERE DATE_HISTO > (SELECT DATE_HISTO  FROM DATE_UPDATE WHERE FULL_UPDATE=1 ORDER BY DATE_HISTO DESC LIMIT 1)
			});
			
		my ($partial_diff_counter, $partial_struct_diff_counter) = $table_date->fetch_row_array();
		
		$total_diff_counter += $partial_diff_counter;
		$partial_struct_diff_counter += $total_struct_diff_counter;
	}
	
	# on sauvegarde la date des collecte complète
	# on sauvegarde les dates des collectes partielles si modifs > 0
	if ( $full_update or $total_diff_counter + $total_struct_diff_counter) {
		
		my $desc='';
		if ($env_compare) {
			$desc="Recopie de $env_compare";
			$desc .=" à la date $date_compare" if $date_compare;
		}
		
		#write date (insert or update)
		$table_date->delete_row(DATE_HISTO  => $timestamp);
		$table_date->insert_row(DATE_HISTO  => $timestamp,
								DESCRIPTION => $desc,
								DIFF_VALUE  => $total_diff_counter,
								DIFF_STRUCT => $total_struct_diff_counter,
								BASELINE    => 0,
								FULL_UPDATE => $full_update,
								);
	}
	else {
		$table_date->delete_row(DATE_HISTO  => $timestamp);
	}

	#log_info("Mise à jour du cache");
	#foreach (keys %diff_list) {	
	#	$cache->add_dirty_diff($_,$diff_list{$_});
	#}
	# flush cache to disk
	#$cache->update_dirty_cache();

	log_info("----------------------------------");
	log_info("Nombre de modification de structure au total dans $environnement : $total_struct_diff_counter");
	log_info("Nombre de lignes mises à jour au total dans $environnement : $total_diff_counter");

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
__END__
:endofperl
