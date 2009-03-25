package Environnement;

use Isip::IsipConfig;

use fields qw(
	options
	environnement
	info_table
	description
	defaut_library
	link_table
	isip_config
	defaut_odbc_options
);

use strict;
use Scalar::Util qw/blessed/;

#use ITable::ITools;
use Carp qw(carp croak);
use Isip::IsipLog '$logger';
use ITable::ILink;

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	
	# Arguments
	my $environnement = shift or croak "Environnement->new take 1 argument";
	my $options= shift;
	
	my Environnement $self= fields::new($class);

	# store global info about tables
	my ILink $link=ILink->new();
	$self->{isip_config}= IsipConfig->new();
	$self->{link_table}=$link;
	
	$self->{options}= $options;
	$self->{environnement} = $environnement;
	
	# store global info about environnement 
	if (not exists $self->{isip_config}->{info_env}->{$environnement} ) {
		croak "Unable to get information of Environnement : $self->{environnement}";
	}
	
	$self->{description}=$self->{isip_config}->{info_env}->{$environnement}->{description};
	$self->{defaut_library}=$self->{isip_config}->{info_env}->{$environnement}->{defaut_library};
	
	# constants
	#TODO : use confuguration file
	
	$logger->warning( "Defaut Datasource should not be null" ) if not $self->{defaut_library};
	
	
	
	# populate class members
	
	my $table_info=$self->open_local_table("TABLE_INFO", $self->{options});
	$table_info->query_condition("ACTIVE = 1");
	while (my %row=$table_info->fetch_row) {		
		$self->{info_table}->{$row{TABLE_NAME}}->{module}=$row{MODULE};
		$self->{info_table}->{$row{TABLE_NAME}}->{root_table}=$row{ROOT_TABLE};
		$self->{info_table}->{$row{TABLE_NAME}}->{type_source}=$row{TYPE_SOURCE};
		$self->{info_table}->{$row{TABLE_NAME}}->{param_source}=$row{PARAM_SOURCE};
		$self->{info_table}->{$row{TABLE_NAME}}->{label_field}=$row{LABEL_FIELD};
		$self->{info_table}->{$row{TABLE_NAME}}->{description}=$row{DESCRIPTION};

		# init structure for columns 
		$self->{info_table}->{$row{TABLE_NAME}}->{column}={};
		
		# init private structure for method get_table_field()
		$self->{info_table}->{$row{TABLE_NAME}}->{_column_order}=[];
	}
	
	my $column_info=$self->open_local_table("COLUMN_INFO", $self->{options});
	
	while (my %row=$column_info->fetch_row) {
		next if not exists $self->{info_table}->{$row{TABLE_NAME}};
		
		$self->{info_table}->{$row{TABLE_NAME}}->{column}->{$row{FIELD_NAME}}->{type}=$row{TYPE};
		$self->{info_table}->{$row{TABLE_NAME}}->{column}->{$row{FIELD_NAME}}->{description}=$row{TEXT};
		$self->{info_table}->{$row{TABLE_NAME}}->{column}->{$row{FIELD_NAME}}->{data_type}=$row{DATA_TYPE};
		$self->{info_table}->{$row{TABLE_NAME}}->{column}->{$row{FIELD_NAME}}->{data_size}=$row{DATA_LENGTH};

		if ($row{PRIMARY_KEY}) {
			push @{$self->{info_table}->{$row{TABLE_NAME}}->{key}}, $row{FIELD_NAME} ;
			$self->{info_table}->{$row{TABLE_NAME}}->{column}->{$row{FIELD_NAME}}->{type}="clef";
		}
		
		if ($row{FOREIGN_TABLE} and $row{FOREIGN_KEY} and exists $self->{info_table}->{$row{FOREIGN_TABLE}}) {
			$self->{link_table}->add_link($row{TABLE_NAME},$row{FIELD_NAME},$row{FOREIGN_TABLE},$row{FOREIGN_KEY});
		}
	}
	

	foreach my $table (keys %{ $self->{info_table} }) {
		
		# fixed key for XML sources
		if ($self->{info_table}->{$table}->{type_source} eq "XML") {
			$self->{info_table}->{$table}->{key}=[ "xml_path" ];
		} 
		
		if (not $self->{info_table}->{$table}->{key}) {
			$logger->warning("No PRIMARY KEY for $table") ;
		}
	}
	
	
	# add environnement specific info about tables
	my $source_info=$self->open_local_table("XML_INFO", $self->{options});
	
	my %sources;
	while (my %row=$source_info->fetch_row) {
		next if not exists $self->{info_table}->{$row{XML_NAME}};
	
		#overide datasource with specific
		#$sources{$row{XML_NAME}}=$row{XML_PATH};
		$self->set_datasource($row{XML_NAME},$row{XML_PATH});

	}
	
	# set defaut value if datasource not specified
	foreach my $table ($self->get_table_list) {
		if (not $self->{info_table}->{$table}->{source}) {
			$self->set_datasource($table,$self->{defaut_library});
		}
	}

	$logger->info("Environnement $self->{environnement} opened");
	
	return $self;
}

sub set_datasource() {
	my $self = shift;
	
	my $table=shift;
	my $datasource=shift or croak("usage : set_datasource(table,datasource)");
	
	# datasource
	$self->{info_table}->{$table}->{source}=$datasource;
}

sub get_column_info() {
	my $self = shift;
	
	my $table_name=shift or croak("usage : get_column_info(table)");
	
	return %{$self->{info_table}->{$table_name}->{column}};
}

sub is_root_table() {
	my $self = shift;
	
	my $table_name=shift or croak("usage : is_root_table(table)");
	
	return undef if not exists $self->{info_table}->{$table_name};
	return $self->{info_table}->{$table_name}->{root_table};
}

sub get_table_info() {
	my $self = shift;
	
	my $table_name=shift or croak("usage : get_table_info(table)");
	
	return () if not exists $self->{info_table}->{$table_name};
	return %{$self->{info_table}->{$table_name} };
}


sub get_table_list() {
	my $self = shift;

	return keys %{$self->{info_table}};
}

sub get_table_list_module() {
	my $self = shift;
	
	my $module_name= shift or croak("usage : get_table_list_module(module_name)");

	my @table_list;
	my %temp_hash=%{$self->{info_table}};
	foreach (keys %temp_hash ) {
		push @table_list, $_ if $self->{info_table}->{$_}->{module} eq $module_name;
	}
	
	return @table_list;
}

# return ILink object
sub get_links() {
	my $self = shift;
		
	return $self->{link_table};
}

# return new ILink object with new virtual ROOT tables (see #56)
sub get_links_menu() {
	my $self = shift;
		
	my ILink $link_clone = $self->{link_table}->clone();
	
	my $separator='__';
	
	foreach my $table ($self->get_table_list) {
		next if not $self->{info_table}->{$table}->{root_table};
		
		# on recherche la liste des parents d'une table ROOT
		my @parents=grep {!/$separator/} $link_clone->get_parent_tables($table,1);
		next if not @parents;
		
		my $child=$table;
		my $child_ext="";
		foreach my $parent (@parents) {
			my %field=$link_clone->get_foreign_fields($child,$parent);
			foreach my $pkey (keys %field) {
				# on construit une nouvelle relation avec une table parente dédiée
				$link_clone->add_link($child_ext.$child,$pkey,$table.$separator.$parent,$field{$pkey});
			}
			$child_ext=$table.$separator if not $child_ext;
			$child=$parent;
		}
	}
	
	return $link_clone;
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
	if (exists $self->{info_table}->{$tablename}->{key}) {
		if (wantarray) {
			return sort @{$self->{info_table}->{$tablename}->{key}};
		}
		else {
			return join(',',sort @{$self->{info_table}->{$tablename}->{key}});
		}
	}
	else {
		carp("no PRIMARY KEY defined for $tablename");
		return undef;
	}
}

sub get_table_description() {
	my $self = shift;
	my $tablename = shift or croak "get_table_description() wait args : 'tablename'";

	return undef if not exists $self->{info_table}->{$tablename};
	return $self->{info_table}->{$tablename}->{description};
}

sub get_table_size() {
	my $self = shift;
	my $tablename = shift or croak "get_table_description() wait args : 'tablename'";

	return undef if not exists $self->{info_table}->{$tablename};
	
	my %column_info=%{$self->{info_table}->{$tablename}->{column}};
	
	my %size_info;
	foreach my $col (keys %column_info) {
		$size_info{$col}=$column_info{$col}->{data_type}."(".$column_info{$col}->{data_size}.")";
	}
	
	return %size_info;
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
	# For now, we'll use ITools definition beacause it keep order of field
	
	my @cols=@{$self->{info_table}->{$tablename}->{_column_order}};
	if ( not @cols ) {
		my $table=ITools->open("IKOS_TABLE_".$self->{environnement}."_".$tablename, {debug => $debug_level});
		@cols=@{$self->{info_table}->{$tablename}->{_column_order}}=$table->field;
	}
		
	return @cols;
	
	#my %cols=%{$self->{info_table}->{$tablename}->{column}};
	#return keys %cols;
}

# provide file name of Sqlite database depending on table name and environnement
sub get_sqlite_filename() {
	my $self = shift;
	
	my $table_name=shift or croak "get_sqlite_filename() wait args : 'tablename[,environnement]'";
	my $environnement=shift;
	
	$environnement=$self->{environnement} if not $environnement;
	
	my $filename;
	my $database_path;
	
	my $table_real;
	my $table_extension;
	
	# table are in format TABLENAME_EXTENSION ou TABLENAME
	($table_real,$table_extension) = ($table_name =~ /^(\w+)_(HISTO|HISTO_CATEGORY|INFO)$/);
	($table_real,$table_extension) = ($table_name =~ /^(\w+)_(CATEGORY)$/) if not $table_extension;
	($table_real) = ($table_name =~ /^(\w+)$/) if not $table_real;
	
	if ($table_name =~ /^TABLE_INFO|XML_INFO|COLUMN_INFO|CACHE_.*$/i) {
		$filename = "ISIP_".$environnement."_INFO.sqlite";
	}
	else {
	
		croak("no environnement defined for table type : HISTO")
			if not $environnement;
		
		$filename = "ISIP_".$environnement."_".$table_real.".sqlite";
	}
	
	return $filename;
}

# provide file path of Sqlite database depending on architecture, environnement and table name
sub get_sqlite_path() {
	my $self = shift;
	
	my $table_name=shift or croak "get_sqlite_path() wait args : 'tablename[,environnement]'";
	my $environnement=shift;
	
	my $filename=$self->get_sqlite_filename($table_name,$environnement);
	
	
	my $dir=$self->{isip_config}->get_data_dir();
	
	my $filepath=$dir."/".$filename;
	
	if ($dir) {
		if (not -e $filepath) {
			carp("Unable to access to file : $filepath");
		}
	}
	else {
		# if ISIP_DATA is not provided, we use first dir of BV_TABPATH
		$filepath=$self->_find_file($ENV{BV_TABPATH},$filename);
		#not found
		$logger->error("table $table_name : $filename not found in BV_TABPATH")	if not $filepath;
	}
		
	return $filepath;
}


sub exists_histo_table() {
	my $self = shift;
	
	my $table_name=shift or croak("usage : exists_doc_table(tablename)");
	
	return 1 if $self->get_sqlite_path($table_name."_HISTO");
}

sub open_local_from_histo_table() {
	my $self = shift;
	
	use Isip::ITable::Histo;
	
	my $table_name=shift or croak "open_histo_table() wait args : 'tablename'";
	
	croak "Database not initialized for table $table_name in environnement ".$self->{environnement} if not $self->exist_local_table($table_name.'_HISTO');
	
	#my $table_histo = eval {Histo->open($self->get_sqlite_path($table_name), $table_name, @_)};
	my $table_histo = eval {Histo->open($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	
	# we must set the primary key manually
	my $dyn_exluded_re = join '|', $table_histo->dynamic_field;
	$table_histo->field(grep {!/\b$dyn_exluded_re\b/} $self->get_table_field($table_name));
	
	$table_histo->size($self->get_table_size($table_name));
	
	my @key_string=$self->get_table_key($table_name);
	$table_histo->key(@key_string) if @key_string;

	return $table_histo
}

sub open_histo_field_table() {
	my $self = shift;
	
	use Isip::ITable::HistoField;
	
	my $table_name=shift or croak "open_histo_field_table() wait args : 'tablename'";
	
	croak "Database not initialized for table $table_name in environnement ".$self->{environnement} if not $self->exist_local_table($table_name.'_HISTO');
	
	my $table_histo = eval {HistoField->open($self->get_sqlite_path($table_name,$self->{environnement}), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	
	return $table_histo
}

sub open_cache_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
	my $table_name=shift or croak "open_cache_table() wait args : 'tablename'";
	
	my $tmp_return = eval {Sqlite->open($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	return $tmp_return;
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

sub open_local_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename'";
	
	my $sqlite_path=$self->get_sqlite_path($table_name,$self->{environnement});
	if (not -e $sqlite_path) {
		croak ("Unable to access $table_name for $self->{environnement}");
	}
	my $tmp_return = eval {Sqlite->open($sqlite_path, $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	return $tmp_return;
}

sub open_source_table() {
	my $self=shift;
	my $table_name=shift or croak "open_source_table() wait args : 'table_name'";
	my $options=shift;
	
	# merge odbc_options with additionnal options
	my $odbc_options=$self->{isip_config}->get_odbc_option($self->{environnement});
	foreach (keys %$options) {
		$odbc_options->{$_}=$options->{$_};
	}
	$options=$odbc_options;
	
	my $return_table;
	
	# open source table depending on TYPE_SOURCE
	if ($self->{info_table}->{$table_name}->{type_source} eq "ODBC") {
		if (not $self->{info_table}->{$table_name}->{source}) {
			$logger->error("SOURCE missing for $table_name");
		}
		
		if ($self->{info_table}->{$table_name}->{param_source}) {
			use Isip::ITable::ODBC_Query;
			$logger->info("Connexion à ODBC : $self->{info_table}->{$table_name}->{source}");
			$return_table=ODBC_Query->open($self->{info_table}->{$table_name}->{source}, $table_name, $self->{info_table}->{$table_name}->{param_source}, $options);
		}
		else {
			use ITable::ODBC;
			$logger->info("Connexion à ODBC : $self->{info_table}->{$table_name}->{source}");
			$return_table=ODBC->open($self->{info_table}->{$table_name}->{source}, $table_name, $options);
		}
		
		
		#manually set KEY
		if ($self->{info_table}->{$table_name}->{key}) {
			$return_table->key(sort split(/,/,$self->get_table_key($table_name)));
		} else {
			carp ("PRIMARY KEY not defined for $table_name") ;
		}
	}
	elsif ($self->{info_table}->{$table_name}->{type_source} eq "XML") {
		if (not $self->{info_table}->{$table_name}->{source}) {
			$logger->error("SOURCE missing for $table_name");
		}
		
		use ITable::XmlFile;
		
		$logger->info("Connexion à XML : $self->{info_table}->{$table_name}->{source}");
		$return_table=XmlFile->open($self->{info_table}->{$table_name}->{source}, $table_name, $options);
	}
	
	return $return_table;
}

sub initialize_column_info() {
	my $self=shift;
	
	my $itable_obj=shift or croak "bad arguments";
	my $links=shift;
	
	if (not blessed($itable_obj) or not $itable_obj->isa("DATA_interface")) {
		croak("usage initialize_column_info(DATA_interface)")
	}

	my $column_info=$self->open_local_table("COLUMN_INFO");
	
	my %size_hash=$itable_obj->size();
	my %field_txt_hash=$itable_obj->field_txt();
	my @key=$itable_obj->key();
	
	$logger->notice("Populate TABLE_INFO with information from source table: ".$itable_obj->table_name);	
	
	my %column_info=$self->get_column_info($itable_obj->table_name);
	
	# caclul des clefs étrangère pour cette table si un 2e argument est passé
	my %foreign_key_info;
	my %foreign_table_info;
	if ($links) {
		croak "bad arguments" if not blessed($links) and not $links->isa("ILink");
		my @foreign_table=$links->get_parent_tables($itable_obj->table_name);
		foreach my $f_tab (@foreign_table) {
			my %foreign_key=$links->get_foreign_fields($itable_obj->table_name,$f_tab);
			
			foreach my $f_key (keys  %foreign_key) {
				if (exists $foreign_key_info{$f_key}) {
					croak "Problème avec les liens : un champ ne peux avoir qu'une clef étrangère";
				}
				$foreign_key_info{$f_key}=$foreign_key{$f_key};
				$foreign_table_info{$f_key}=$f_tab;
			}
		}
	}		
	
	$column_info->begin_transaction;
	foreach my $field ($itable_obj->field) {
		
		if (exists $column_info{$field}) {
			$logger->warning("$field already known : PASS");
			next;
		}
		
		# extract type and size from format "type(size)"
		my ($type,$size) = $size_hash{$field} =~ /(\w+)\((\d+)\)/;
		
		# construct the line to insert
		my %row;
		$row{TABLE_NAME}=$itable_obj->table_name;
		$row{FIELD_NAME}=$field;
		$row{DATA_TYPE}=$type;
		$row{DATA_LENGTH}=$size;
		$row{TEXT}=$field_txt_hash{$field};
		
		$row{FOREIGN_TABLE}=$foreign_table_info{$field} if exists $foreign_table_info{$field};
		$row{FOREIGN_KEY}=$foreign_key_info{$field} if exists $foreign_key_info{$field};
		
		$row{PRIMARY_KEY}=1 if grep {$field eq $_} @key;
		
		$column_info->insert_row(%row);
	}
	$column_info->commit_transaction;

}

sub create_database_histo() {
	my $self=shift;
	
	my $tablename=shift or croak("usage : create_database_histo(tablename)");
	
	my $database_filename=$self->get_sqlite_filename($tablename);
	
	#get first tab path
	my $database_dir=$self->{isip_config}->get_data_dir();
	
	my $database_path=$database_dir."/".$database_filename;
	
	$logger->info("no PRIMARY KEY defined for $tablename") if not $self->get_table_key($tablename);
	carp "database already exist at <$database_path>" if -e $database_path;
	
	if (! -e $database_path) {
		$logger->notice("Creating empty file : $database_path");
		#create empty file
		open DATABASEFILE,">$database_path" or croak "unable to create file : $!";
		close DATABASEFILE;
	}
	
	croak "Impossible de retrouver le fichier créé" if not $self->get_sqlite_path($tablename);
	
	# opening master table
	my $master_table=Sqlite->open($database_path, 'sqlite_master');
	
	$logger->notice("Create table $tablename\_HISTO");
	$master_table->execute("CREATE TABLE IF NOT EXISTS $tablename\_HISTO (
	ID INTEGER PRIMARY KEY,
	DATE_HISTO DATETIME,
	USER_UPDATE VARCHAR(30),
	DATE_UPDATE DATETIME,
	TABLE_NAME VARCHAR(30),
	TABLE_KEY VARCHAR(30),
	FIELD_NAME VARCHAR(30),
	FIELD_VALUE VARCHAR(30),
	PROJECT VARCHAR(30),
	COMMENT VARCHAR(50),
	STATUS VARCHAR(30),
	MEMO VARCHAR(30))");
	
	$logger->notice("Create table $tablename\_CATEGORY");
	$master_table->execute("CREATE TABLE IF NOT EXISTS $tablename\_CATEGORY (
	TABLE_KEY VARCHAR(30) PRIMARY KEY NOT NULL,
	CATEGORY VARCHAR(30))");
	
	$logger->notice("Replace view $tablename\_HISTO_CATEGORY");
	$master_table->execute("DROP VIEW IF EXISTS $tablename\_HISTO_CATEGORY");
	$master_table->execute("CREATE VIEW $tablename\_HISTO_CATEGORY AS
	SELECT ID,
		DATE_HISTO,
		USER_UPDATE,
		DATE_UPDATE,
		TABLE_NAME,
		$tablename\_HISTO.TABLE_KEY as TABLE_KEY,
		FIELD_NAME,
		FIELD_VALUE,
		COMMENT,
		STATUS,
		MEMO,
		PROJECT,
		coalesce($tablename\_CATEGORY.CATEGORY,'vide') as CATEGORY
	FROM $tablename\_HISTO
	LEFT JOIN $tablename\_CATEGORY
		ON ($tablename\_HISTO.TABLE_KEY=$tablename\_CATEGORY.TABLE_KEY )
	WHERE CATEGORY IS NULL OR CATEGORY != 'HIDDEN'");
	
	$logger->notice("Create indexes");
	$master_table->execute("CREATE INDEX IF NOT EXISTS IDX_TABLE_KEY ON $tablename\_HISTO (TABLE_KEY ASC)");
	$master_table->execute("CREATE INDEX IF NOT EXISTS IDX_TABLE_FIELD ON $tablename\_HISTO (FIELD_NAME ASC)");

	
	$master_table->close();
	
}

1;


=head1 NAME

 Isip::Environnement - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
