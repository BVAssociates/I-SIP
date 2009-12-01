#!/usr/bin/perl

package pc_init_env;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;
use Carp;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_INIT_ENV - Initalise les tables Information d'une table

=head1 SYNOPSIS

 PC_INIT_ENV.pl [-h] [-v ][-i environnement_source] datasource environnement
 
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

=item -i environnement_source : importe les informations des tables d'un environnement existant

=back

=head1 ARGUMENTS

=over

=item datasource : nom de la source ODBC

=item environnement : table dont la base sera créé

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

sub run {
	local @ARGV=@_;

	#  Traitement des Options
	###########################################################


	my %opts;
	getopts('hvi:', \%opts) or usage(0);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};

	my $environnement_from=$opts{i};


	#  Traitement des arguments
	###########################################################

	if ( @ARGV != 2 ) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}

	my $odbc_name=shift;
	my $environnement=shift;

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::IsipConfig;
	use Isip::Environnement;
	use ITable::ITools;

	log_info("Recupération des informations de l'environnement $environnement");

	$ENV{ODBC_NAME}=$odbc_name;
	my $source_environ=ITools->open("SOURCE_ENVIRON");
	$source_environ->query_condition("ENVIRON = '$environnement'");

	my %env_row=$source_environ->fetch_row();
	undef $source_environ;

	if (not %env_row) {
		croak("l'environnement $environnement est inconnu dans $odbc_name");
	}
	
	log_info("Ajout de l'environnement $environnement");
	my %new_row;
	@new_row{"Environnement","Description","DEFAUT_LIBRARY"}=(
			@env_row{"ENVIRON","DESCRIPTION","ODBC_SOURCE"} );
	$new_row{DEFAUT_ODBC}=$odbc_name;

	my $conf_environ=ITools->open("CONF_ENVIRON");
	$conf_environ->insert_row_pp(%new_row);

	my $config_sip = IsipConfig->new();

	if ($environnement_from) {
		$config_sip->copy_environnement($environnement_from,$environnement);
		
		my Environnement $env=Environnement->new($environnement);
		my $table_info=$env->open_local_table("TABLE_INFO");
		
		while (my %table=$table_info->fetch_row) {
			$env->create_database_histo($table{TABLE_NAME});
		}
		
		require 'PC_GENERATE_MENU.pl';
		pc_generate_menu::run($environnement);
	}
	else {
		$config_sip->create_database_environnement($environnement);
	}
	
	return 1;
}

exit !run(@ARGV) if !caller;

1;