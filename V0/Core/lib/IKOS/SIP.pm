package SIP;

use strict;

use IKOS::DATA::ODBC;
use IKOS::DATA::Sqlite;
use IKOS::DATA::ITools;
use IKOS::DATA::Histo;
use IKOS::DATA::HistoField;
use IKOS::IsipRules;

use Carp qw(carp croak );

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self= {};
	
	# Arguments
	$self->{environnement} = shift or croak "SIP->new take 1 argument";
	$self->{options} = shift;
	
	$self->{options}->{debug} = 0 if not exists $self->{options}->{debug};
	
	my $environ_table=ITools->open("ENVIRON",$self->{options});
	$environ_table->query_condition("Environnement = $self->{environnement}");
	
	my %env_config=$environ_table->fetch_row();
	croak "Unable to get information of Environnement : $self->{environnement}" if not %env_config;
	
	$self->{description}=$env_config{Description};
	$self->{datasource}=$env_config{Datasource};
	
	croak "Datasource field cannot be null" if not $env_config{Datasource};
	
	return bless($self, $class);
}

sub get_histo_field() {
	my $self = shift;
	
	return ("ID","DATE_HISTO","DATE_UPDATE","USER_UPDATE","TABLE_NAME","TABLE_KEY","FIELD_NAME","FIELD_VALUE","COMMENT","STATUS");
}

#found the table primary key
sub get_table_key() {
	my $self = shift;
	my $tablename = shift or croak "get_table_key() wait args : 'tablename'";
	my $debug_level = $self->{options}->{debug};
	my $key_found;
	
	# some different way to get the infos :
	#   - from INFO_TABLE, 
	#   - from local table
	#   - from ITools definition file
	#
	# For now, we'll use INFO_TABLE
	
	# quirk because INFO_TABLE use %Environnement%
	$ENV{Environnement}=$self->{environnement};
	
	my $table=ITools->open("INFO_TABLE", {debug => $debug_level});
	$table->query_condition("TABLE_NAME = '$tablename'");
	$table->query_field("PRIMARY_KEY");

	($key_found) = $table->fetch_row_array();
	$table->finish;
	return $key_found;
}

sub get_table_field() {
	my $self = shift;
	my $tablename = shift or croak "get_table_field() wait args : 'tablename'";
	my $debug_level = 0;
	my $key_found;
	
	# some different way to get the infos :
	#   - from INFO_TABLE, 
	#   - from local table
	#   - from ITools definition file
	#
	# For now, we'll use ITools definition
	
	my $table=ITools->open("IKOS_TABLE_".$self->{environnement}."_".$tablename, {debug => $debug_level});
	return $table->field;
}

sub get_sqlite_path() {
	my $self = shift;
	
	my $table_name=shift or croak "get_local_database() wait args : 'tablename'";
	
	my $filename;
	my $database_path;
	
	# table suffixed with _* are in the same database
	$table_name =~ s/_\w+$//;
	$filename = "IKOS_".$self->{environnement}."_".$table_name.".sqlite";
	
	if (not exists $ENV{BV_TABPATH}) {
		croak('Environnement variable "BV_TABPATH" does not exist');
	}
	
	use Config;
	my $env_separator = $Config{path_sep};
	
	my $filepath;
	foreach my $path (split ($env_separator,$ENV{BV_TABPATH})) {
		return $path."/".$filename if -r $path."/".$filename;
	}
	
	#not found
	carp "$filename not found in BV_TABPATH";
	return undef;
}

# change this methods to configure Database Access
sub exist_local_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename'";
	
	
	my $database_path=$self->get_sqlite_path($table_name);
	return 0 if not $database_path;
	
	# verification on sqlite_master
	my $master_table=Sqlite->open($database_path, 'sqlite_master', @_);
	$master_table->query_condition("type='table' AND name='$table_name'");
	
	my $return_value=0;
	if ($master_table->fetch_row_array) {
		$return_value=1;
	}
	$master_table->close();
	
	return $return_value;
}

sub get_isip_rules() {
	my $self = shift;
	
	my $table_name=shift or croak "open_isip_rules() wait args : 'tablename'";
	
	my $tmp_return = eval {IsipRules->new($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	return $tmp_return;
}

sub open_local_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename'";
	
	my $tmp_return = eval {Sqlite->open($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	return $tmp_return;
}

sub open_local_from_histo_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_histo_table() wait args : 'tablename'";
	
	croak "Database not initialized for table $table_name in environnement ".$self->{environnement} if not $self->exist_local_table($table_name.'_HISTO');
	
	my $table_histo = eval {Histo->open($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	
	# we must set the primary key manually
	$table_histo->key(split(/,/,$self->get_table_key($table_name),-1));

	return $table_histo
}

sub open_histo_field_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_histo_field_table() wait args : 'tablename'";
	
	croak "Database not initialized for table $table_name in environnement ".$self->{environnement} if not $self->exist_local_table($table_name.'_HISTO');
	
	my $table_histo = eval {HistoField->open($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	
		return $table_histo
}

sub open_ikos_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_ikos_table() wait args : 'tablename'";
	
	my $table_ikos = eval { ODBC->open($self->{datasource} , $table_name, @_); };
	croak "Error opening $table_name : $@" if $@;

	# we must set the primary key manually
	my $table_key=$self->get_table_key($table_name);
	croak "primary key is not set for $table_name" if not $table_key;
	$table_ikos->key(split(/,/,$table_key));
	
	return $table_ikos;
}

=begin comment : may be confusing

sub open_histo_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_histo_table() wait args : 'tablename'";
	
	return Sqlite->open("IKOS_".$self->{environnement} , $table_name."_HISTO", @_);
}

=end

=cut

sub initialize_database() {
	my $self=shift;
	
	my $tablename=shift or die "bad arguments";
	
	my $options=shift;
	die "bad arguments" if $options and ref($options) ne "HASH";

	# Get infos from IKOS tables via ODBC
	my $table = $self->open_ikos_table($tablename, {debug => 0 });
	if (not defined $table) {
		die "error opening $table in IKOS";
	}

	my $table_def=ITools->open("INFO_TABLE",$options);
	$table_def->query_condition("TABLE_NAME = '$tablename'");
	my %defined_table=$table_def->fetch_row();
	$table_def->finish;
	
	# compute path of database file
	my $database_path=$ENV{CLES_HOME}."/".$ENV{ICleName}."/_Services/".$ENV{ServiceName}."/tab/IKOS_".$self->{environnement}."_".$tablename.".sqlite";
	
	die "file <$database_path> already exist" if -e $database_path;
	
	print "Creating empty file : $database_path\n";
	#create empty file
	open DATABASEFILE,">$database_path" or die "unable to create file : $!";
	close DATABASEFILE;
	
	die "Impossible de retrouver le fichier créé" if not $self->get_sqlite_path($tablename);
	
	# opening master table
	my $master_table=Sqlite->open($database_path, 'sqlite_master', $options);
	
	print "Create table $tablename\_HISTO\n";
	$master_table->execute("CREATE TABLE $tablename\_HISTO (
	ID INTEGER PRIMARY KEY,
	DATE_HISTO VARCHAR(30),
	USER_UPDATE VARCHAR(30),
	DATE_UPDATE VARCHAR(30),
	TABLE_NAME VARCHAR(30),
	TABLE_KEY VARCHAR(30),
	FIELD_NAME VARCHAR(30),
	FIELD_VALUE VARCHAR(30),
	COMMENT VARCHAR(50),
	STATUS VARCHAR(30))");

	print "Create table $tablename\_INFO\n";
	$master_table->execute("CREATE TABLE $tablename\_INFO (
	FIELD_NAME VARCHAR(30) PRIMARY KEY,
	DATE_UPDATE VARCHAR(30) ,
	DATA_TYPE VARCHAR(30) NOT NULL,
	DATA_LENGTH VARCHAR(30) NOT NULL,
	TABLE_SCHEMA VARCHAR(30) ,
	TEXT VARCHAR(30) ,
	DESCRIPTION VARCHAR(30) ,
	TYPE VARCHAR(30) )");
	
	$master_table->close();
	
	my $info_table=Sqlite->open($database_path,"$tablename\_INFO",$options);
	
	warn "WARNING: $tablename n'a pas de clef primaire définie dans INFO_TABLE" if not $defined_table{PRIMARY_KEY};

	my %size_hash=$table->size();
	my %field_txt_hash=$table->field_txt();
	
	print "Populate $tablename\_INFO with information from IKOS table\n";
	
	$info_table->begin_transaction;
	foreach my $field ($table->field) {
		# extract type and size from format "type(size)"
		my ($type,$size) = $size_hash{$field} =~ /(\w+)\((\d+)\)/;
		
		# construct the line to insert
		my %row;
		$row{FIELD_NAME}=$field;
		$row{DATA_TYPE}=$type;
		$row{DATA_LENGTH}=$size;
		$row{TEXT}=$field_txt_hash{$field};
		
		$info_table->insert_row(%row);
	}
	$info_table->commit_transaction;

}

sub SQL_drop() {
	my $self=shift;
	
	my $tablename=shift or die;
	
	return "DROP $tablename;";
}

1;


=head1 NAME

 IKOS::SIP - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
