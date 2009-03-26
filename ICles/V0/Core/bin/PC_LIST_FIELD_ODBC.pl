#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_ODBC - Liste les champs d'une table IKOS par ODBC

=head1 SYNOPSIS

 PC_LIST_FIELD_ODBC.pl environnement tablename
 
=head1 DESCRIPTION

Liste les champs d'une table IKOS à la date courante en utilisant le driver ODBC

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

=item tablename : table a décrire

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

if ( @ARGV != 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $table=shift;

#  Corps du script
###########################################################

use Isip::IsipConfig;
use Isip::ITable::ODBC_Query;
use ITable::ODBC;

my $config=IsipConfig->new();

my $table_source;
if ($ENV{param_source}) {
	$logger->info("Connexion à ODBC : $ENV{param_source}");
	$table_source = ODBC_Query->open($config->get_odbc_database_name($environnement),
				$table,
				$ENV{param_source},
				$config->get_odbc_option($environnement) );
}
else {
	$logger->info("Connexion à ODBC : $table");
	$table_source = ODBC->open($config->get_odbc_database_name($environnement),
				$table,
				$config->get_odbc_option($environnement) );
}

if (not defined $table_source) {
	die "error opening $table";
}

print join($separator,("","aucun")),"\n";

my %field_txt=$table_source->field_txt();
foreach ($table_source->field) {
	print join($separator,($_,$field_txt{$_})),"\n";
};