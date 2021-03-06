#!/usr/bin/env perl

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

 PC_LIST_FIELD_ODBC.pl environnement module tablename
 
=head1 DESCRIPTION

Liste les champs d'une table IKOS � la date courante en utilisant le driver ODBC

=head2 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head2 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head2 ARGUMENTS 

=over

=item environnement : environnement � utiliser

=item module : module dans lequel se trouve la table

=item tablename : table a d�crire

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

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
getopts('hvs:', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=",";
$separator=$opts{s} if exists $opts{s};

#  Traitement des arguments
###########################################################

if ( @ARGV != 3) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $module=shift;
my $table=shift;

#  Corps du script
###########################################################

use Isip::IsipConfig;
use Isip::ITable::ODBC_Query;
use ITable::ODBC;

my $config=IsipConfig->new();

my $table_source;

if ($ENV{PARAM_SOURCE}) {
	$logger->info("Connexion � ODBC : $ENV{PARAM_SOURCE}");
	eval { $table_source = ODBC_Query->open($config->get_odbc_database_name($module,$environnement),
				$table,
				$ENV{PARAM_SOURCE},
				$config->get_odbc_option($environnement) );
	};
}
else {
	$logger->info("Connexion � ODBC : $table");
	eval {$table_source = ODBC->open($config->get_odbc_database_name($module,$environnement),
				$table,
				$config->get_odbc_option($environnement) );
	};
}

if ($@) {
	log_erreur("Impossible d'acceder � la table $table depuis ce module");
}

if (not defined $table_source) {
	die "error opening $table";
}

print join($separator,("","aucun")),"\n";

my %field_txt=$table_source->field_txt();
foreach ($table_source->field) {
	print join($separator,($_,$field_txt{$_})),"\n";
};