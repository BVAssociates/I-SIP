package IsipConfig;

use strict;

use ITable::ITools;
# commented lines, because modules loaded on demand
#use ITable::ODBC;
#use ITable::Sqlite;
#use Isip::ITable::Histo;
#use Isip::ITable::HistoField;
#use Isip::IsipRules;

use Carp qw(carp croak );

use Isip::IsipLog '$logger';

use ITable::ILink;

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self= {};
	
	# Arguments
	$self->{options} = shift;
	
	
	
	# store global info about tables
	$self->{info_env}= {};
	$self->{info_table}= {};
	$self->{link_table}=ILink->new();

	#BEGIN of information retrieval
	my $table_environ=ITools->open("ENVIRON",$self->{options});
	while (my %row=$table_environ->fetch_row) {
		$self->{info_env}->{$row{Environnement}}->{description}=$row{Description};
		$self->{info_env}->{$row{Environnement}}->{defaut_datasource}=$row{DEFAUT_ODBC};
	}
	
	my $table_info=ITools->open("TABLE_INFO", $self->{options});
	$table_info->query_condition("ACTIVE = 1");
	while (my %row=$table_info->fetch_row) {		
		$self->{info_table}->{$row{TABLE_NAME}}->{module}=$row{MODULE};
		$self->{info_table}->{$row{TABLE_NAME}}->{root_table}=$row{ROOT_TABLE};
		$self->{info_table}->{$row{TABLE_NAME}}->{type_source}=$row{TYPE_SOURCE};
		$self->{info_table}->{$row{TABLE_NAME}}->{param_source}=$row{PARAM_SOURCE};
		$self->{info_table}->{$row{TABLE_NAME}}->{label_field}=$row{LABEL_FIELD};
		$self->{info_table}->{$row{TABLE_NAME}}->{description}=$row{DESCRIPTION};
	}
	
	
	# store global info about primary keys
	my $key_info=ITools->open("FIELD_KEY", $self->{options});
	my %temp_key;
	while (my %row=$key_info->fetch_row) {
		# filter
		next if not exists $self->{info_table}->{$row{TABLE_NAME}};
		
		push @{$temp_key{$row{TABLE_NAME}}},$row{FIELD_KEY};
	}
	
	foreach my $table (keys %{ $self->{info_table} }) {
		
		# fixed key for XML sources
		if ($self->{info_table}->{$table}->{type_source} eq "XML") {
			$self->{info_table}->{$table}->{key}="xml_path";
		} else {
			if ($temp_key{$table}) {
				my $string_key=join(',',sort @{ $temp_key{$table} });
				$self->{info_table}->{$table}->{key}=$string_key;
			} else {
				$logger->info("No PRIMARY KEY for $table") ;
			}
		}
	}
	

	# store global info about relations between tables
	my $link_info=ITools->open("FIELD_LINK", $self->{options});
	while (my %row=$link_info->fetch_row) {
		# filter only on known tables
		next if not exists $self->{info_table}->{$row{TABLE_LINK}};
		next if not exists $self->{info_table}->{$row{F_TABLE}};

		$self->{link_table}->add_link($row{TABLE_LINK},$row{FIELD_LINK},$row{F_TABLE},$row{F_KEY})
	}
	
	

	
	return bless($self, $class);
}

sub set_datasource() {
	my $self = shift;
	
	my $table=shift;
	my $datasource=shift or croak("usage : set_datasource(table,datasource)");
	
	# datasource
	$self->{info_table}->{$table}->{source}=$datasource;
}

sub get_table_info() {
	my $self = shift;
	
	return %{$self->{info_table}};
}

sub get_table_list() {
	my $self = shift;

	return keys %{$self->{info_table}};
}

sub get_environnement_list() {
	my $self = shift;

	return keys %{$self->{info_env}};
}

sub get_links() {
	my $self = shift;
		
	return $self->{link_table};
}

sub get_histo_field() {
	my $self = shift;
	
	croak "OBSOLETE method. Please update script";
	
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
	
	return $self->{info_table}->{$tablename}->{key};
}


# provide file name of Sqlite database depending on table name
sub get_sqlite_filename() {
	my $self = shift;
	
	my $table_name=shift or croak "get_sqlite_filename() wait args : 'tablename[,environnement]'";
	my $environnement=shift;
	
	my $filename;
	my $database_path;
	
	my $table_real;
	my $table_extension;
	
	# table are in format TABLENAME_EXTENSION ou TABLENAME
	($table_real,$table_extension) = ($table_name =~ /^(\w+)_(HISTO|INFO|DOC)$/);
	($table_real) = ($table_name =~ /^(\w+)$/) if not $table_real;
	
	if (not $table_extension  or $table_extension eq "HISTO") {
	
		croak("use Class Environnement to get a HISTO table")
	}
	elsif ($table_extension and $table_extension eq "DOC" or $table_extension eq "INFO") {
		$filename = "ISIP_DOC_".$table_real.".sqlite";
	}
	
	return $filename;
}

# provide file path of Sqlite database depending on architecture, environnement and table name
sub get_sqlite_path() {
	my $self = shift;
	
	my $table_name=shift or croak "get_sqlite_path() wait args : 'tablename[,environnement]'";
	my $environnement=shift;
	
	my $filename=$self->get_sqlite_filename($table_name,$environnement);
	
	my $filepath=$self->_find_file($ENV{BV_TABPATH},$filename);
	
	#not found
	$logger->error("table $table_name : $filename not found in BV_TABPATH")	if not $filepath;
	return $filepath;
}

# change this methods to configure Database Access
sub exist_local_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename[,environnement]'";
	my $environnement=shift;
	
	
	my $database_path=$self->get_sqlite_path($table_name,$environnement);
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

sub exists_doc_table() {
	my $self = shift;
	
	my $table_name=shift or croak("usage : exists_doc_table(tablename)");
	
	return 1 if $self->get_sqlite_path($table_name."_DOC");
}


sub get_isip_rules() {
	my $self = shift;
	
	use Isip::IsipRules;
	
	my $table_name=shift or croak "open_isip_rules() wait args : 'tablename'";
	
	my $tmp_return = eval {IsipRules->new($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	return $tmp_return;
}

sub open_documentation_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
	my $table_name=shift or croak "open_info_table() wait args : 'tablename'";
	
	my $tmp_return = eval {Sqlite->open($self->get_sqlite_path($table_name."_DOC"), $table_name."_DOC", @_)};
	croak "Error opening $table_name\_DOC : $@" if $@;
	return $tmp_return;
}

sub open_local_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename'";
	
	my $tmp_return = eval {Sqlite->open($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	return $tmp_return;
}



sub initialize_database_documentation() {
	my $self=shift;
	
	my $itable_obj=shift or die "bad arguments";
	
	my $options=shift;
	croak "bad arguments" if $options and ref($options) ne "HASH";
	$logger->error("$itable_obj->table_name() n'a pas de clef primaire définie") if not $itable_obj->{key};

	my $tablename_doc=$itable_obj->table_name()."_DOC";
	my $tablename_info=$itable_obj->table_name()."_INFO";
	
	# compute path of database file
	croak("CLES_HOME n'est pas dans l'environnement") if not exists $ENV{CLES_HOME};
	croak("ICleName n'est pas dans l'environnement") if not exists $ENV{ICleName};
	
	my $database_filename=$self->get_sqlite_filename($tablename_info);
	#get first tab path
	my $database_dir=$self->_find_file($ENV{BV_TABPATH});
	croak("$database_dir not writable") if not -w $database_dir;
	
	my $database_path=$database_dir."/".$database_filename;
	
	die "database already exist at <$database_path>" if -e $database_path;
	
	$logger->notice("Creating empty file : $database_path");
	#create empty file
	open DATABASEFILE,">$database_path" or die "unable to create file : $!";
	close DATABASEFILE;
	
	die "Impossible de retrouver le fichier créé" if not $self->get_sqlite_path($tablename_info);
	
	# opening master table
	my $master_table=Sqlite->open($database_path, 'sqlite_master', $options);
	
	$logger->notice("Create table $tablename_doc");
	$master_table->execute("CREATE TABLE \"$tablename_doc\" (
		USER_UPDATE VARCHAR(30),
		DATE_UPDATE VARCHAR(30),
		TABLE_NAME VARCHAR(30),
		TABLE_KEY VARCHAR(30),
		FIELD_NAME VARCHAR(30),
		DOCUMENTATION VARCHAR(30),
		PRIMARY KEY (TABLE_NAME, TABLE_KEY, FIELD_NAME) )");

	$logger->notice("Create table $tablename_info");
	$master_table->execute("CREATE TABLE $tablename_info (
	FIELD_NAME VARCHAR(30) PRIMARY KEY,
	DATE_UPDATE VARCHAR(30) ,
	USER_UPDATE VARCHAR(30),
	DATA_TYPE VARCHAR(30) NOT NULL,
	DATA_LENGTH VARCHAR(30) NOT NULL,
	-- TABLE_SCHEMA VARCHAR(30) ,
	TEXT VARCHAR(30) ,
	DESCRIPTION VARCHAR(30) ,
	TYPE VARCHAR(30) )");
	
	$master_table->close();
	
	my $info_table=$self->open_local_table("$tablename_info",$options);
	

	my %size_hash=$itable_obj->size();
	my %field_txt_hash=$itable_obj->field_txt();
	
	$logger->notice("Populate $tablename_info with information from source table");	
	
	$info_table->begin_transaction;
	foreach my $field ($itable_obj->field) {
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
