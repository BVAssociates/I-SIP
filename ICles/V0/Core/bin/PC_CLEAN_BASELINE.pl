#!/usr/bin/env perl
package pc_clean_baseline;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog qw'$logger log_screen_only';

#  Documentation
###########################################################
=head1 NAME

PC_CLEAN_BASELINE - Supprime les table de baseline qui ne sont pas d�clar�s comme baseline

=head1 SYNOPSIS

 PC_CLEAN_BASELINE.pl [-h] [-v] [-c] environnement
 
=head1 DESCRIPTION

Utiliser cette commande pour nettoyer les bases en supprimant les tables de baselines
qui ne correpsondent pas � une baseline de la liste des baselines

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -c : compacte la base apr�s les suppressions (VACUUM)

=back

=head1 ARGUMENTS

=over

=item environnement

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	die shift;
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


	my %opts;
	getopts('hvc', \%opts) or usage(1);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	
	my $vacuum_after=$opts{c};

	#  Traitement des arguments
	###########################################################

	if ( @ARGV < 1) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}

	my $environnement=shift @ARGV;

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::Environnement;

	$logger->notice("Nettoyage de l'environnement $environnement");
	my $env=Environnement->new($environnement);
	
	#log_screen_only();
	
	my $baseline_list=$env->open_local_table("DATE_UPDATE");
	$baseline_list->query_condition("BASELINE=1");
	
	my %baseline;
	while (my %baseline_info=$baseline_list->fetch_row()) {
		$baseline_info{DATE_HISTO} =~ tr/:-//d;
		$baseline{$baseline_info{DATE_HISTO}}++;
	}
	
	if (not %baseline) {
		$logger->info("Pas de baseline pour cet environnement");
	}
	
	foreach my $table_name ($env->get_table_list) {
	
		#manualy open database which is storing _HISTO table
		my $database_path=$env->get_sqlite_path($table_name."_HISTO");
		
		log_erreur("Impossible d'acceder � $database_path pour nettoyage") if not -s $database_path;
		
		my $master_table=Sqlite->open($database_path, 'sqlite_master');
		$master_table->query_field("name");
		
		#put list in memory to avoid locking
		my @base_table;
		while (my ($histo_table)=$master_table->fetch_row_array) {
			push @base_table, $histo_table;
		}
		
		foreach my $histo_table (@base_table) {
			my $found_baseline=$histo_table;
			
			# table not a baseline table
			next if not $found_baseline =~ s/^$table_name\_(\d{4}\d{2}\d{2}T\d{2}\d{2})$/$1/;
			
			# table is a known baseline
			next if $baseline{$found_baseline};
			
			# table is an unknown baseline
			log_info("$table_name : $found_baseline n'est pas une baseline connue. Suppression.");
			$env->drop_histo_baseline($table_name,$found_baseline);
			
		}
		
		if ($vacuum_after) {
			log_info("Compactage de la base contenant $table_name");
			$master_table->execute("VACUUM");
		}
	}
	#wait;
	
	# if VACUUM, take the occasion to compact TABLE_INFO too
	if ($vacuum_after) {
			log_info("Compactage de la base contenant TABLE_INFO");
			my $database_path=$env->get_sqlite_path("TABLE_INFO");
			my $info_table=Sqlite->open($database_path, 'sqlite_master');
			$info_table->execute("VACUUM");
		}
	
	$logger->notice("Nettoyage termin� pour $environnement");
	
	return 1;
}

exit !run(@ARGV) if not caller;
1;
