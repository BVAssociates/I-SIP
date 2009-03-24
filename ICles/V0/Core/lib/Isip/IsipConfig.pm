package IsipConfig;
use fields qw(
	options
	info_env
	defaut_odbc_options
);

use strict;

use ITable::ITools;
# commented lines, because modules loaded on demand
use ITable::ODBC;
#use ITable::Sqlite;
#use Isip::ITable::Histo;
#use Isip::ITable::HistoField;
#use Isip::IsipRules;

use Carp qw(carp croak );
use Scalar::Util qw(blessed);

use Isip::IsipLog '$logger';

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	my $self=fields::new($class);
	
	# Arguments
	$self->{options} = shift;
	croak "bad arguments" if $self->{options} and ref($self->{options}) ne "HASH";
	
	$self->{defaut_odbc_options}={
		username => "ETUDEGF",
		password => "GFETUDE05",
	};
	
	# store global info about tables
	$self->{info_env}= {};

	#BEGIN of information retrieval
	my $table_environ=ITools->open("ENVIRON",$self->{options});
	while (my %row=$table_environ->fetch_row) {
		$self->{info_env}->{$row{Environnement}}->{description}=$row{Description};
		$self->{info_env}->{$row{Environnement}}->{defaut_datasource}=$row{DEFAUT_ODBC};
		$self->{info_env}->{$row{Environnement}}->{defaut_library}=$row{DEFAUT_LIBRARY};
	}
	
	
	return $self;
}

#return odbc datasource name
sub get_odbc_database_name() {
	my $self = shift;
	
	my $environnement=shift or die "bad arguments";
	
	if (not exists $self->{info_env}->{$environnement}) {
		croak("Environnement $environnement non configuré");
	}
	
	return $self->{info_env}->{$environnement}->{defaut_library};
}

# return odbc option reference in format intended by ODBC->open()
sub get_odbc_option() {
	my $self = shift;
	
	my $environnement=shift or die "bad arguments";
	
	if (not exists $self->{info_env}->{$environnement}) {
		croak("Environnement $environnement non configuré");
	}
	
	my $options;
	$options->{username}=$self->{defaut_odbc_options}->{username};
	$options->{password}=$self->{defaut_odbc_options}->{password};
	$options->{odbc_name}=$self->{info_env}->{$environnement}->{defaut_datasource};
	
	return $options;
}

sub exists_odbc_table() {
	my $self = shift;
	
	my $environnement=shift or die "bad arguments";
	
	die "not implemented yet";
}

sub get_env_info() {
	my $self = shift;
	
	return %{$self->{info_env}};
}


sub get_environnement_list() {
	my $self = shift;

	return keys %{$self->{info_env}};
}

# provide file path of Sqlite database depending on architecture, environnement and table name
sub get_data_dir() {
	my $self = shift;
	
	my $data_path;
	if (exists $ENV{ISIP_DATA}) {
		$data_path=$ENV{ISIP_DATA}."/tab";
		if (not (-d $data_path and -r $data_path and -w $data_path) ) {
			croak("ISIP_DATA=\"".$ENV{ISIP_DATA}."\" is not a valid directory");
		}
	}
	
	#remove last slash if needed
	$data_path =~ s|[\\/]$||;
	
	return $data_path;
}

sub exists_database_environnement() {
	my $self=shift;
	
	my $environnement=shift or die "bad arguments";
	
	my $database_path=$self->get_data_dir()."ISIP_".$environnement."_INFO.sqlite";
	
	return -r $database_path;
}

sub create_database_environnement() {
	my $self=shift;
	
	my $environnement=shift or die "bad arguments";
	
	if (! grep {$environnement eq $_} $self->get_environnement_list) {
		croak("$environnement is not a valid environnement");
	}
	
	my $database_path=$self->get_data_dir()."/ISIP_".$environnement."_INFO.sqlite";
	
	croak "database already exist at <$database_path>" if -e $database_path;
	
	$logger->notice("Creating empty file : $database_path");
	#create empty file
	open DATABASEFILE,">$database_path" or die "unable to create file : $!";
	close DATABASEFILE;
	
	# opening master table
	my $master_table=Sqlite->open($database_path, 'sqlite_master');
	
	
	$logger->notice("Create table TABLE_INFO");
	$master_table->execute('CREATE TABLE TABLE_INFO (
	"ROOT_TABLE" NUMERIC,
	"ACTIVE" NUMERIC,
	"TABLE_NAME" VARCHAR(30),
	"TYPE_SOURCE" VARCHAR(30),
	"PARAM_SOURCE" VARCHAR(30),
	"MODULE" VARCHAR(30),
	"LABEL_FIELD" VARCHAR(30),
	"DESCRIPTION" VARCHAR(50),
	PRIMARY KEY (TABLE_NAME) 
	)');

	$logger->notice("Create table COLUMN_INFO");
	$master_table->execute('CREATE TABLE "COLUMN_INFO" (
		"TABLE_NAME" VARCHAR(30),
		"FIELD_NAME" VARCHAR(30),
		"DATE_UPDATE" VARCHAR(30),
		"USER_UPDATE" VARCHAR(30),
		"DATA_TYPE" VARCHAR(30),
		"DATA_LENGTH" VARCHAR(30),
		"TEXT" VARCHAR(30),
		"TYPE" VARCHAR(30),
		"PRIMARY_KEY" NUMERIC,
		"FOREIGN_TABLE" VARCHAR(30),
		"FOREIGN_KEY" VARCHAR(30),
		PRIMARY KEY ("TABLE_NAME","FIELD_NAME")
	)');
	
	$logger->notice("Create table CACHE_ICON");
	$master_table->execute('CREATE TABLE "CACHE_ICON" (
		"TABLE_NAME" VARCHAR,
		"TABLE_SOURCE" VARCHAR,
		"TABLE_KEY" VARCHAR,
		"NUM_CHILD" INTEGER,
		PRIMARY KEY ("TABLE_KEY", "TABLE_NAME", "TABLE_SOURCE")
	)');
	
	$logger->notice("Create table CACHE_PROJECT");
	$master_table->execute('CREATE TABLE "CACHE_PROJECT" (
		"TABLE_NAME" VARCHAR,
		"TABLE_KEY" VARCHAR,
		"PROJECT_CHILD" VARCHAR,
		"NUM_CHILD" INTEGER,
		PRIMARY KEY ("TABLE_KEY", "TABLE_NAME", "PROJECT_CHILD")
	)');
	
	$master_table->close();
}


sub _find_file() {
	my $self=shift;
	
	my $path_list = shift;
	my $filename = shift;
	
	use Config;
	my $env_separator = $Config{path_sep};
	
	
	foreach my $path (split ($env_separator,$path_list)) {
		my $filepath = $path;
		$filepath .= "/".$filename if $filename;
		return $filepath if -r $filepath;
	}
	
	return undef;
}

1;


=head1 NAME

 Isip::Environnement - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
