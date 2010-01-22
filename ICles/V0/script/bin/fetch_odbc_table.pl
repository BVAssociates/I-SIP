#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';
use Carp;

#  Documentation
###########################################################
=head1 NAME

fetch_odbc_table - Sauvegarde les tables utilisées dans I -SIP IKOS

=head1 SYNOPSIS

 fetch_odbc_table.pl [-h][-v] repertoire
 
=head1 DESCRIPTION

Sauvegarde toutes les tables utilisées dans I -SIP IKOS dans une table
SQLite locale.

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

=item repertoire : repertoire de sauvegarde des fichiers SQLite

=back

=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits réservés.

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

sub get_create_query {
	my $table = shift;
	
	my $query='CREATE TABLE '.$table->table_name." (\n";
	
	my $sep="";
	foreach my $field_name ($table->field) {
		$query.=$sep.$field_name." ".$table->{size}->{$field_name};
		$query.=" NOT NULL" if grep{$_ eq $field_name} ($table->not_null(),$table->key());
		$sep=",\n";
	}

	$query.=",\n PRIMARY KEY (".join(',',$table->key).")\n" if $table->key;

	$query.="\n)\n";
	
	return $query;
}

sub get_sqlite_file {
	my $source=shift;
	
	my $database_name;
	my $schema_name;

	croak if not exists $source->{odbc_name};
	
	$database_name=$source->{odbc_name};
	$schema_name=$source->{database_name};
	
	return $database_name.'.'.$schema_name.'.sqlite';
}

sub clone_table {
	my $source=shift;
	my $data_dir=shift;
	

	my $sqlite_file=File::Spec->catfile($data_dir,get_sqlite_file($source));
	
	log_info("clone to Sqlite->".$source->table_name);
	
	my $sqlite_master=Sqlite->open($sqlite_file, 'sqlite_master');
	foreach my $create_query (get_create_query($source)) {
		
		eval { $sqlite_master->execute($create_query) };
		log_info($create_query, ' : ', $@) if $@;
	}
	undef $sqlite_master;

	my $sqlite_table=Sqlite->open($sqlite_file,$source->table_name);
	
	log_info("copy to Sqlite->".$source->table_name);
	$sqlite_table->begin_transaction();
	my $counter;
	while (my %row=$source->fetch_row()) {
		$counter++;
		$sqlite_table->insert_row(%row);
		if (! ($counter % 50000)) {
			log_info("Commit at $counter");
			$sqlite_table->{database_handle}->commit();
			$sqlite_table->{database_handle}->begin_work();
		}
	}
	$sqlite_table->commit_transaction();
	log_info("copy to Sqlite->".$source->table_name." : OK");
	
	return;
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

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $data_dir=shift @ARGV;

if (not -d $data_dir or not -w $data_dir) {
	die("$data_dir n'est pas accessible en écriture");
}

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::IsipConfig;
use Isip::Environnement;
use ITable::Sqlite;
use ITable::ODBC;
use File::Spec;

# "SCHEMA.TABLE" => "SCHEMA field"
my %system_table=(
	"QSYS.QADBKFLD" => "DBKLIB",
	"QSYS2.SYSCOLUMNS" => "SYSTEM_TABLE_SCHEMA",
	"QSYS2.SYSTABLES" => "SYSTEM_TABLE_SCHEMA",
	"IKGSENV.ENVBIBP" => "",
	"IKGSENV.ENVENVP" => "",
);



my $config=IsipConfig->new();

my %env_info=$config->get_env_info();

my %database_list;
my %schema_list;
foreach my $env_name (keys %env_info) {
	$database_list{$env_info{$env_name}->{defaut_datasource}}=$env_name;
		
	foreach my $module_name ($config->get_module_list()) {
		$schema_list{$config->get_odbc_database_name($module_name,$env_name)}++;
	}
}
foreach (keys %system_table) {
	$schema_list{(split('\.'))[0]}++;
}

foreach my $database_name (keys %database_list) {
	
	foreach my $schema_name (keys %schema_list) {
		
		log_info("create database for $database_name/$schema_name");
		my $sqlite_file=File::Spec->catfile($data_dir,$database_name.'.'.$schema_name.'.sqlite');
		open(NEW,'>',$sqlite_file) or die ($sqlite_file,' : ',$!);
		close(NEW);
	}
	
	# copy system tables
	foreach my $table_name (keys %system_table) {
	
	
		log_info("open ODBC->$table_name");
		my $odbc_table=ODBC->open(split('\.',$table_name),$config->get_odbc_option($database_list{$database_name}) );
		
		
		if ($system_table{$table_name}) {
			my $schema_condition;
			
			$schema_condition=$system_table{$table_name}." IN (".join(',', map {"'".$_."'"} keys %schema_list).")";
			
			$odbc_table->query_condition($schema_condition);
		}
		
		clone_table($odbc_table,$data_dir);
	}
}
	
#copy data tables
foreach my $env_name (keys %env_info) {
	
	my $env=Environnement->new($env_name);

	my @already_done;
	foreach my $table_name ($env->get_table_list()) {
	
		my $odbc_table=$env->open_source_table($table_name);
		
		next if exists $odbc_table->{xml_file};
		
		if (exists $odbc_table->{table_list}) {
		
			foreach my $table_query (@{$odbc_table->{table_list}}) {
				log_info("split $table_name into $table_query");
				next if grep {$_ eq $table_query} @already_done;
				my $odbc_table_query=ODBC->open($odbc_table->{database_name},$table_query, $odbc_table->{ODBC_options});
				clone_table($odbc_table_query,$data_dir);
				push @already_done, $table_query;
			}
		}
		else {
			if (not grep {$_ eq $table_name} @already_done) {
				clone_table($odbc_table,$data_dir);
				push @already_done, $table_name;
			}
		}
	}
}


