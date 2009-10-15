#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

upgrade_TABLE_INFO - 

=head1 SYNOPSIS

 upgrade_TABLE_INFO.pl [-h][-v]
 
=head1 DESCRIPTION

Met à jour les bases de données ISIP avec la nouvelle table TABLE_INFO (see #137) 

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

# get update info
use POSIX qw(strftime);
my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
my $current_user=$ENV{IsisUser};
$current_user = "local" if not $current_user;

foreach (@environnement_list) {
	my $env=Environnement->new($_);
	
	# la table FIELD_LABEL sera créée dans la meme base que TABLE_INFO
	my $table_info = $env->open_local_table("TABLE_INFO");
	
	$table_info->execute('ALTER TABLE "main"."TABLE_INFO" RENAME TO "__temp__TABLE_INFO"');
	
	$table_info->execute('CREATE TABLE "main"."TABLE_INFO" (
	"ROOT_TABLE" NUMERIC,
	"TABLE_NAME" VARCHAR(30) PRIMARY KEY ,
	"TYPE_SOURCE" VARCHAR(30),
	"PARAM_SOURCE" VARCHAR(30),
	"MODULE" VARCHAR(30),
	"LABEL_FIELD" VARCHAR(30),
	"DESCRIPTION" VARCHAR(50),
	"ALLOW_IGNORE" BOOL DEFAULT 0
	)') ;
	
	$table_info->execute('INSERT INTO "main"."TABLE_INFO" SELECT "ROOT_TABLE","TABLE_NAME","TYPE_SOURCE","PARAM_SOURCE","MODULE","LABEL_FIELD","DESCRIPTION",0 FROM "main"."__temp__TABLE_INFO"') ; 
	
	$table_info->execute('DROP TABLE "main"."__temp__TABLE_INFO"');

}