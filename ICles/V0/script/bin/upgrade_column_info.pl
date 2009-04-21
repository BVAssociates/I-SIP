#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

upgrade_column_info - 

=head1 SYNOPSIS

 upgrade_column_info.pl [-h][-v] 
 
=head1 DESCRIPTION

Met à jour les bases de données ISIP avec la nouvelle colonne

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

=item environnement : environnement à utiliser

=item tablename : table a ouvrir

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
getopts('hv', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}


#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::IsipConfig;
use Isip::Environnement;
use Isip::HistoColumns;

my $config=IsipConfig->new();

my @environnement_list=$config->get_environnement_list();

foreach (@environnement_list) {
	my $env=Environnement->new($_);
	
	foreach my $table ($env->get_table_list) {
		my $table_source=$env->open_local_table("COLUMN_INFO");
		$table_source->query_condition("TABLE_NAME = '$table'");
		$table_source->query_sort("COLNO");
		
		$env->create_database_histo($table);
		my $columns=$env->get_columns($table);
		
		while (my %field=$table_source->fetch_row() ) {
			my %new_field;
			$new_field{description}=$field{"TEXT"};
			
			$new_field{size}=$field{"DATA_TYPE"};
			$new_field{size}.='('.$field{"DATA_LENGTH"}.')';
			
			$new_field{colno}=$field{"COLNO"};
			$new_field{key}=1 if $field{"PRIMARY_KEY"};
			$new_field{foreign_table}=$field{"FOREIGN_TABLE"};
			$new_field{foreign_field}=$field{"FOREIGN_KEY"};
			$new_field{key}=1 if $field{"PRIMARY_KEY"};
			
			$columns->add_column($field{"FIELD_NAME"}, \%new_field) if not $columns->has_field($field{"FIELD_NAME"});
		}
	}
}