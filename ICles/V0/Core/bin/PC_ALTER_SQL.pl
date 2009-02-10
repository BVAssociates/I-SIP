#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_ALTER_SQL - Execute une requete SQL

=head1 SYNOPSIS

 PC_ALTER_SQL.pl [-h] [-v] [-t tablename [-e environnement]] type [ADD|REMOVE] column_name
 
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

if ( @ARGV < 3 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $type_table=shift;

my $type_query=shift;
my $column_name=shift;

if ($type_table eq "HISTO") {
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
	
	my $table_obj=$config_sip->open_histo_field_table($current_table);
	
	
	my @local_sql;
	if ($type_query eq "ADD") {
		push @local_sql, 'ALTER TABLE "'.$current_table.'_HISTO" ADD COLUMN "'.$column_name.'" VARCHAR(30)';
	}
	else {
		my @field=grep {$_ ne $column_name} $table_obj->field;
		die "$column_name n'existe pas" if @field == $table_obj->field;
		push @local_sql, 'ALTER TABLE "main"."CROEXPP2_HISTO" RENAME TO "__temp__'.$current_table.'_HISTO"';
		push @local_sql, 'create table '.$current_table.'_HISTO as select *,\'\' as '.$column_name.' from "main"."__temp__'.$current_table.'_HISTO"';
		push @local_sql, 'DROP TABLE "main"."__temp__CROEXPP2_HISTO"';
		die "not implemented";
	}
	
	foreach my $sql (@local_sql) {
		print ($sql,"\n");
		$table_obj->execute($sql);
	}
}



sortie($bv_severite);