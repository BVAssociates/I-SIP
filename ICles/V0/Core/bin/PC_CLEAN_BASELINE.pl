#!/usr/bin/perl
package pc_clean_baseline;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog qw'$logger log_screen_only';

#  Documentation
###########################################################
=head1 NAME

PC_CLEAN_BASELINE - Supprime les table de baseline qui ne sont pas déclarés comme baseline

=head1 SYNOPSIS

 PC_CLEAN_BASELINE.pl [-h] [-v] environnement
 
=head1 DESCRIPTION

Utiliser cette commande pour nettoyer les bases en supprimant les tables de baselines
qui ne correpsondent pas à une baseline de la liste des baselines

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

=item environnement

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

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
	getopts('hvdm:', \%opts);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	
	my $drop_baseline=$opts{d};
	my $message=$opts{m};

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
	
	my $baseline_list=ITools->open("DATE_UPDATE");
	$baseline_list->query_condition("ENVIRON=$environnement");
	
	my %baseline;
	while (my %baseline_info=$baseline_list->fetch_row()) {
		$baseline_info{DATE_UPDATE} =~ tr/:-//d;
		$baseline{$baseline_info{DATE_UPDATE}}++ if $baseline_info{BASELINE};
	}
	
	foreach my $table_name ($env->get_table_list) {
	
		#manualy open database which is storing _HISTO table
		my $database_path=$env->get_sqlite_path($table_name."_HISTO");
		
		log_erreur("Impossible d'acceder à $database_path pour nettoyage") if not -s $database_path;
		
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
	}
	#wait;
	
	$logger->notice("Nettoyage terminé pour $environnement");
	
	return 1;
}

exit !run(@ARGV) if not caller;
1;
