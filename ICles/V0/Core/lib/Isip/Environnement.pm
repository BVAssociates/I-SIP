package Environnement;

use Isip::IsipConfig;
@ISA=(IsipConfig);

use strict;

#use ITable::ITools;
use Carp qw(carp croak);
use Isip::IsipLog '$logger';

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	
	# Arguments
	my $environnement = shift or croak "Environnement->new take 1 argument";
	my $options= shift;
	
	my $self= $class->SUPER::new($options);
	
	$self->{options}= $options;
	$self->{environnement} = $environnement;
	
	# store global info about environnement 
	if (not exists $self->{info_env}->{$environnement} ) {
		croak "Unable to get information of Environnement : $self->{environnement}";
	}
	
	$self->{description}=$self->{info_env}->{$environnement}->{description};
	$self->{defaut_datasource}=$self->{info_env}->{$environnement}->{defaut_datasource};
	
	$logger->warning( "Defaut Datasource should not be null" ) if not $self->{defaut_datasource};
	
	
	# store global info about tables
	$self->{isip_config}= IsipConfig->new();

	
	# add environnement specific info about tables
	my $source_info=ITools->open("SOURCE_ENV", $self->{options});
	$source_info->query_condition("ENVIRONNEMENT = $self->{environnement}");
	
	my %sources;
	while (my %row=$source_info->fetch_row) {
		#overide datasource with specific
		$sources{$row{TABLE_NAME}}=$row{SOURCE};

	}
	
	foreach my $table ($self->get_table_list) {
		if (exists $sources{$table}) {
			$self->set_datasource($table,$sources{$table});
		} else {
			$self->set_datasource($table,$self->{defaut_datasource});
		}
	}

	$logger->info("Environnement $self->{environnement} opened");
	
	return bless($self, $class);
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
	
		croak("no environnement defined for table type : HISTO")
			if not $self->{environnement};
		
		$filename = "ISIP_".$self->{environnement}."_".$table_real.".sqlite";
	}
	elsif ($table_extension and $table_extension eq "DOC" or $table_extension eq "INFO") {
		$filename = "ISIP_DOC_".$table_real.".sqlite";
	}
	
	return $filename;
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
	
	my $table_histo = eval {Histo->open($self->get_sqlite_path($table_name,$self->{environnement}), $table_name, @_)};
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
	
	my $table_histo = eval {HistoField->open($self->get_sqlite_path($table_name,$self->{environnement}), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	
	return $table_histo
}

sub open_cache_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
	my $table_name=shift or croak "open_cache_table() wait args : 'tablename'";
	
	my $tmp_return = eval {Sqlite->open($self->get_sqlite_path("GLOBAL"), $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	return $tmp_return;
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
		
		if ($self->{info_table}->{$table_name}->{param_source}) {
			use Isip::ITable::ODBC_Query;
			$logger->info("Connexion à ODBC : $self->{info_table}->{$table_name}->{source}");
			$return_table=ODBC_Query->open($self->{info_table}->{$table_name}->{source}, $table_name, $self->{info_table}->{$table_name}->{param_source}, @options);
		}
		else {
			use ITable::ODBC;
			$logger->info("Connexion à ODBC : $self->{info_table}->{$table_name}->{source}");
			$return_table=ODBC->open($self->{info_table}->{$table_name}->{source}, $table_name, @options);
		}
		
		
		#manually set KEY
		if ($self->{info_table}->{$table_name}->{key}) {
			$return_table->key(sort split(/,/,$self->{info_table}->{$table_name}->{key}));
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
		$return_table=XmlFile->open($self->{info_table}->{$table_name}->{source}, $table_name, @options);
	}
	
	return $return_table;
}

sub initialize_database_histo() {
	my $self=shift;
	
	my $itable_obj=shift or die "bad arguments";
	
	my $options=shift;
	croak "bad arguments" if $options and ref($options) ne "HASH";

	my $tablename=$itable_obj->table_name();
	$logger->error("$tablename n'a pas de clef primaire définie") if not $itable_obj->{key};
	
	# compute path of database file
	croak("CLES_HOME n'est pas dans l'environnement") if not exists $ENV{CLES_HOME};
	croak("ICleName n'est pas dans l'environnement") if not exists $ENV{ICleName};
	
	my $database_filename=$self->get_sqlite_filename($tablename);
	
	#get first tab path
	my $database_dir=$self->_find_file($ENV{BV_TABPATH});
	croak("$database_dir not writable") if not -w $database_dir;
	
	my $database_path=$database_dir."/".$database_filename;
	
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
	STATUS VARCHAR(30),
	MEMO VARCHAR(30))");
	
	$master_table->close();
	
}

1;


=head1 NAME

 Isip::Environnement - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
