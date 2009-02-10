#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_EXEC_SQL - Execute une requete SQL

=head1 SYNOPSIS

 PC_EXEC_SQL.pl [-h] [-v] [-t tablename [-e environnement]] type "SQL QUERY"
 
=head1 DESCRIPTION

Execute une requete SQL.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -i : table_INFO

=back

=head1 ARGUMENTS

=head2 environnement : environnement à utiliser

=head2 tablename : FIELD_INFO ou HISTO

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	my $exit_value=shift;
	$logger->notice("Sortie du programme, code $exit_value");
	exit $exit_value;
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


log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

my %opts;
getopts('hve:t:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $environnement=$opts{e};
my $table_name=$opts{t};


#  Traitement des arguments
###########################################################

if ( @ARGV < 1 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $type_table=shift;

my $SQL=join(' ',@ARGV);

if ($type_table eq "FIELD_INFO") {
	usage($debug_level) if $environnement;
}
elsif ($type_table eq "HISTO") {
	usage($debug_level) if not $environnement;
}
else {
	usage($debug_level);
}

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use Isip::IsipConfig;
use ITable::ITools;
use Isip::ITable::DataDiff;
use Isip::IsipTreeCache;



my $config_sip;
if ($environnement) {
	$config_sip = Environnement->new($environnement);
}
else {
	$config_sip = IsipConfig->new($environnement);
}

my %table_info = $config_sip->get_table_info();
my @list_table;
if (not $table_name) {
	@list_table=keys %table_info;
} else {
	@list_table=($table_name);
}



foreach my $current_table (@list_table) {
	
	if ( not ($config_sip->exists_doc_table($current_table)) ) {
		$logger->error("$current_table n'a pas été initialisée");
		next;
	}
	
	my $table_obj;
	my $local_sql=$SQL;
	if ($type_table eq "FIELD_INFO") {
		$table_obj=$config_sip->open_documentation_table($current_table);
		$local_sql =~ s/{}/$current_table\_INFO/g;
	}
	elsif ($type_table eq "HISTO") {
		$table_obj=$config_sip->open_histo_field_table($current_table);
	}
	
	print ($local_sql,"\n");
	$table_obj->execute($local_sql);
}



sortie($bv_severite);