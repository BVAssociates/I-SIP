#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_LOCAL - Liste le contenu d'une table Sqlite

=head1 SYNOPSIS

 PC_LIST_LOCAL.pl [-h][-v] environnement tablename
 
=head1 DESCRIPTION

Liste les champs d'une table Sqlite

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

if ( @ARGV != 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environ=shift;
my $table_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;

# DEBUG
use Isip::ITable::ODBC_Query;


#$table_query->query_condition("FNCDTRAIT like 'FACP%'");
#die $table_query->get_query;

my $sip=Environnement->new($environ);

foreach $table_name ( $sip->get_table_list() ) {

	my $key_string=$sip->get_table_key($table_name);
	my @key=sort split(',',$key_string);
	my $first_key=shift @key;

	my $table=$sip->open_local_table($table_name."_HISTO", {debug => $debug_level });


	die "unable to open local $table_name in env $environ" if not defined $table;

	$logger->notice("begin for $table_name");
	$table->begin_transaction();

	$logger->notice("merge primary keys into first primary key");
	$table->execute("UPDATE ".$table_name."_HISTO SET FIELD_NAME='$key_string' 
		WHERE FIELD_NAME='$first_key'");
	$table->execute("UPDATE ".$table_name."_HISTO SET FIELD_VALUE=TABLE_KEY 
		WHERE FIELD_NAME='$first_key' AND FIELD_VALUE != '__delete'");

	$logger->notice("delete other primary keys");
	$table->execute("DELETE FROM ".$table_name."_HISTO WHERE FIELD_NAME IN ('".join("','",@key)."')");

	$table->commit_transaction();
	$logger->notice("compact database $table_name");
	$table->execute("VACUUM");
	$table->execute("ANALYZE ".$table_name."_HISTO");
	$logger->notice("end for $table_name");
}