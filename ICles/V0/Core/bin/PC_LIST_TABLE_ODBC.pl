#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_TABLE_ODBC - Liste les tables IKOS par ODBC

=head1 SYNOPSIS

 PC_LIST_TABLE_ODBC.pl environnement module
 
=head1 DESCRIPTION

Liste les tables IKOS en utilisant le driver ODBC

=head2 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head2 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head2 ARGUMENTS 

=over

=item environnement : environnement à utiliser

=item module

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
getopts('hvs:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=",";
$separator=$opts{s} if exists $opts{s};

#  Traitement des arguments
###########################################################

log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

if ( @ARGV != 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $module=shift;

#  Corps du script
###########################################################
use Isip::IsipConfig;
use ITable::ITools;
use ITable::ODBC;

my $config=IsipConfig->new();
my $table_info = ODBC->open("QSYS2",
				"SYSTABLES",
				$config->get_odbc_option($environnement) );

if (not defined $table_info) {
	die "error opening SYSTABLES";
}

my $schema=$config->get_odbc_database_name($module,$environnement);
$table_info->query_condition("TABLE_SCHEMA='$schema' AND TABLE_TYPE='P'");

#my $field_def=ITools->open("TABLE_ODBC");

while (my %row=$table_info->fetch_row) {
	$row{TABLE_TEXT} =~ s/\s+(Fichier Physique|Physical File).*$//i;
	print join($separator,@row{"TABLE_NAME","TABLE_TEXT"}),"\n";
}

sortie(0);
