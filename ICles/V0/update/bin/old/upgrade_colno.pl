#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

upgrade_colno - 

=head1 SYNOPSIS

 upgrade_colno.pl [-h][-v] 
 
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
use ITable::Sqlite;

my $config=IsipConfig->new();

my @environnement_list=$config->get_environnement_list();

foreach (@environnement_list) {
	my $env=Environnement->new($_);
	
	my $table=$env->open_local_table("COLUMN_INFO");
	
	eval {$table->execute("ALTER TABLE COLUMN_INFO ADD COLUMN COLNO NUMERIC")};
	
	$table->begin_transaction();
	$table->execute('ALTER TABLE "main"."COLUMN_INFO" RENAME TO "__temp__COLUMN_INFO"');
	$table->execute('CREATE TABLE "main"."COLUMN_INFO" ("TABLE_NAME" VARCHAR(30),"FIELD_NAME" VARCHAR(30),"DATE_HISTO" VARCHAR(30),"DATE_UPDATE" VARCHAR(30),"USER_UPDATE" VARCHAR(30),"DATA_TYPE" VARCHAR(30),"DATA_LENGTH" VARCHAR(30),"TEXT" VARCHAR(30),"TYPE" VARCHAR(30),"PRIMARY_KEY" NUMERIC,"FOREIGN_TABLE" VARCHAR(30),"FOREIGN_KEY" VARCHAR(30),"COLNO" NUMERIC, PRIMARY KEY ("TABLE_NAME","FIELD_NAME") )');
	$table->execute('INSERT INTO "main"."COLUMN_INFO" SELECT "TABLE_NAME","FIELD_NAME","DATE_UPDATE","DATE_UPDATE","USER_UPDATE","DATA_TYPE","DATA_LENGTH","TEXT","TYPE","PRIMARY_KEY","FOREIGN_TABLE","FOREIGN_KEY","COLNO" FROM "main"."__temp__COLUMN_INFO"');
	$table->execute('DROP TABLE "main"."__temp__COLUMN_INFO"');
	$table->commit_transaction();
}