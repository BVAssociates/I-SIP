package Environnement;

use strict;

use ITable::ITools;
# commented lines, because module loaded on demand
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
	$self->{environnement} = shift or croak "Environnement->new take 1 argument";
	$self->{options} = shift;
	
	$self->{options}->{debug} = 0 if not exists $self->{options}->{debug};
	
	# store global info about environnement 
	my $environ_table=ITools->open("ENVIRON",$self->{options});
	$environ_table->query_condition("Environnement = $self->{environnement}");
	
	my %env_config=$environ_table->fetch_row();
	croak "Unable to get information of Environnement : $self->{environnement}" if not %env_config;
	
	$self->{description}=$env_config{Description};
	$self->{datasource}=$env_config{DEFAUT_ODBC};
	
	$logger->warning( "Datasource field should not be null" ) if not $self->{datasource};
	
	
	# store global info about tables
	$self->{info_table}= {};
	$self->{link_table}=ILink->new();

	
	my $table_info=ITools->open("TABLE_INFO", $self->{options});
	$table_info->query_condition("COLLECTE = 1");
	while (my %row=$table_info->fetch_row) {		
		$self->{info_table}->{$row{TABLE_NAME}}->{module}=$row{MODULE};
		$self->{info_table}->{$row{TABLE_NAME}}->{type_source}=$row{TYPE_SOURCE};
		$self->{info_table}->{$row{TABLE_NAME}}->{label_field}=$row{LABEL_FIELD};
		$self->{info_table}->{$row{TABLE_NAME}}->{description}=$row{DESCRIPTION};
		# default datasource
		$self->{info_table}->{$row{TABLE_NAME}}->{source}=$self->{datasource};
		
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
				$logger->warning("Vous devez definir une cle primaire pour $table") ;
			}
		}
	}
	

	# store global info about relations between tables
	my $link_info=ITools->open("FIELD_LINK", $self->{options});
	while (my %row=$link_info->fetch_row) {
		# filter on known tables
		next if not exists $self->{info_table}->{$row{TABLE_LINK}};

		$self->{link_table}->add_link($row{TABLE_LINK},$row{FIELD_LINK},$row{F_TABLE},$row{F_KEY})
	}
	
	# store specific info about tables
	my $source_info=ITools->open("SOURCE_ENV", $self->{options});
	while (my %row=$source_info->fetch_row) {
		# filter on known tables
		next if not exists $self->{info_table}->{$row{TABLE_NAME}};

		#overide datasource with specific
		$self->{info_table}->{$row{TABLE_NAME}}->{source}=$row{SOURCE};

	}


	$logger->info("Environnement $self->{environnement} opened");
	
	return bless($self, $class);
}

sub get_table_info() {
	my $self = shift;
	
	return %{$self->{info_table}};
}

sub get_links() {
	my $self = shift;
		
	return $self->{link_table};
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
	
	return $self->{info_table}->{$tablename}->{key};
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


# provide file path of Sqlite database depending on architecture, environnement and table name
sub get_sqlite_path() {
	my $self = shift;
	
	my $table_name=shift or croak "get_local_database() wait args : 'tablename'";
	
	my $filename;
	my $database_path;
	
	my $table_real;
	my $table_extension;
	
	# table are in format TABLENAME_EXTENSION ou TABLENAME
	($table_real,$table_extension) = ($table_name =~ /^(\w+)_(HISTO|INFO|DOC)$/);
	($table_real) = ($table_name =~ /^(\w+)$/) if not $table_real;
	
	if (not $table_extension or $table_extension eq "INFO" or $table_extension eq "HISTO") {
		$filename = "ISIP_".$self->{environnement}."_".$table_real.".sqlite";
	}
	elsif ($table_extension and $table_extension eq "DOC") {
		$filename = "ISIP_DOC_".$table_real.".sqlite";
	}
	
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
	$logger->error("table $table_name : $filename not found in BV_TABPATH");
	return undef;
}

# change this methods to configure Database Access
sub exist_local_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
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

sub open_local_from_histo_table() {
	my $self = shift;
	
	use Isip::ITable::Histo;
	
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
	
	use Isip::ITable::HistoField;
	
	my $table_name=shift or croak "open_histo_field_table() wait args : 'tablename'";
	
	croak "Database not initialized for table $table_name in environnement ".$self->{environnement} if not $self->exist_local_table($table_name.'_HISTO');
	
	my $table_histo = eval {HistoField->open($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	
		return $table_histo
}

#@OBSOLETE
sub open_ikos_table() {
	die "use open_source_table instead";
	my $self = shift;
	
	use ITable::ODBC;
	
	my $table_name=shift or croak "open_ikos_table() wait args : 'tablename'";
	
	my $table_ikos = eval { ODBC->open($self->{datasource} , $table_name, @_); };
	croak "Error opening $table_name : $@" if $@;

	# we must set the primary key manually
	my $table_key=$self->get_table_key($table_name);
	croak "primary key is not set for $table_name" if not $table_key;
	$table_ikos->key(split(/,/,$table_key));
	
	return $table_ikos;
}

sub open_source_table() {
	my $self=shift;
	my $table_name=shift or croak "open_source_table() wait args : 'table_name'";
	my @options=@_;
	
	my $return_table;
	
	# open source table depending on TYPE_SOURCE
	if ($self->{info_table}->{$table_name}->{type_source} eq "ODBC") {
		if (not $self->{info_table}->{$table_name}->{source}) {
			$logger->error("SOURCE missing for $table_name");
		}
		
		use ITable::ODBC;

		$logger->info("Connexion à ODBC : $self->{info_table}->{$table_name}->{source}");
		$return_table=ODBC->open($self->{info_table}->{$table_name}->{source}, $table_name, @options);
		
		#manually set KEY
		croak ("PRIMARY KEY not defined for $table_name") if (not $self->{info_table}->{$table_name}->{key});
		$return_table->key(sort split(/,/,$self->{info_table}->{$table_name}->{key}));
	}
	elsif ($self->{info_table}->{$table_name}->{type_source} eq "XML") {
		if (not $self->{info_table}->{$table_name}->{source}) {
			$logger->error("SOURCE missing for $table_name");
		}
		
		use ITable::XmlFile;
		
		$logger->info("Connexion à XML : $self->{info_table}->{$table_name}->{source}");
		$return_table=XmlFile->open($self->{info_table}->{$table_name}->{source}, $table_name, @options);
	}
	
	return $return_table;
}

sub initialize_database() {
	my $self=shift;
	
	my $itable_obj=shift or die "bad arguments";
	
	my $options=shift;
	croak "bad arguments" if $options and ref($options) ne "HASH";

	my $tablename=$itable_obj->table_name();
	$logger->error("$tablename n'a pas de clef primaire définie") if not $itable_obj->{key};
	
	# compute path of database file
	croak("CLES_HOME n'est pas dans l'environnement") if not exists $ENV{CLES_HOME};
	croak("ICleName n'est pas dans l'environnement") if not exists $ENV{ICleName};
	my $database_path=$ENV{CLES_HOME}."/".$ENV{ICleName}."/_Services/tab/ISIP_".$self->{environnement}."_".$tablename.".sqlite";
	
	die "database already exist at <$database_path>" if -e $database_path;
	
	$logger->notice("Creating empty file : $database_path");
	#create empty file
	open DATABASEFILE,">$database_path" or die "unable to create file : $!";
	close DATABASEFILE;
	
	die "Impossible de retrouver le fichier créé" if not $self->get_sqlite_path($tablename);
	
	# opening master table
	my $master_table=Sqlite->open($database_path, 'sqlite_master', $options);
	
	$logger->notice("Create table $tablename\_HISTO");
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

	$logger->notice("Create table $tablename\_INFO");
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
	

	my %size_hash=$itable_obj->size();
	my %field_txt_hash=$itable_obj->field_txt();
	
	$logger->notice("Populate $tablename\_INFO with information from source table");	
	
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

1;


=head1 NAME

 Isip::Environnement - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
