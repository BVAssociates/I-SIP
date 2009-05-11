#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

upgrade_date_update - 

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

foreach my $env_name (@environnement_list) {

	my $env=Environnement->new($env_name);
	
	my $new_table=$env->open_local_table("DATE_UPDATE");
	$new_table->execute("DROP TABLE IF EXISTS DATE_UPDATE");
	
	$config->create_database_environnement($env_name);
	
	$new_table=$env->open_local_table("DATE_UPDATE");
	
	open(my $old_date_file, $ENV{ISIP_DATA}."/tab/DATE_UPDATE")
		or die "Impossible de retrouver la table DATE_UPDATE initiale";
	while (<$old_date_file>) {
		chomp;
		my ($env,$date,$desc,$baseline)=split(/@/);
		
		next if $env ne $env_name;
		
		$new_table->insert_row_array($date,$desc,1,0,$baseline);
	}
	close($old_date_file);
}