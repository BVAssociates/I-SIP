#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_INIT_TABLE - Initalise la table de donn�es d'historique d'une table

=head1 SYNOPSIS

 PC_INIT_TABLE.pl [-h] [-v ] [-i [-c nombre]] environnement tablename
 
=head1 DESCRIPTION

Ajoute les informations de colonnes d'une table dans l'environnement
Creer une base de donn�e historique.

Si l'option -i est utilis�e, une premi�re collecte sera effecut�e


=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -i : Insert les donn�es de la table IKOS

=item -c nombre : Commit tous les n insertions

=back

=head1 ARGUMENTS

=over

=item environnement : environnement � utiliser

=item tablename : table dont la base sera cr��

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
getopts('hvic:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#defaut value
my $group_commit=1000;
$group_commit = $opts{c} if exists $opts{c};
my $populate=1 if exists $opts{i};

#  Traitement des arguments
###########################################################

if ( @ARGV != 2 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $table_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use Isip::ITable::DataDiff;


my $env_sip = Environnement->new($environnement);

my %table_info = $env_sip->get_table_info();
log_erreur("la table $table_name n'est pas connue, veuiller la configurer d'abord") if not exists $table_info{$table_name};

$logger->notice("Create database for table",$table_name);
my $current_table=$env_sip->open_source_table($table_name, {debug => $debug_level});

$env_sip->initialize_column_info($current_table);

$env_sip->create_database_histo($table_name);

sortie($bv_severite);