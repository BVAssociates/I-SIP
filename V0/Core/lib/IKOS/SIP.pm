package SIP;

use strict;

use IKOS::DATA::ODBC;
use IKOS::DATA::Sqlite;
use IKOS::DATA::ITools;
use IKOS::DATA::Histo;

use Carp qw(carp cluck confess croak );

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self= {};
	
	$self->{environnement} = shift or die;
	$self->{options} = shift;
	
	$self->{options}->{debug} = 0 if not exists $self->{options}->{debug};
	
	return bless($self, $class);
}

sub get_histo_field() {
	my $self = shift;
	
	return ("ID","DATE_HISTO","DATE_UPDATE","USER_UPDATE","TABLE_NAME","TABLE_KEY","FIELD_NAME","FIELD_VALUE","COMMENT","TYPE","STATUS");
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
	
	my $table=ITools->open($tablename, {debug => $debug_level});
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
	
	croak "No database found for table $table_name in ".$self->{environnement} if not $self->exist_local_table($table_name.'_HISTO');
	
	my $table_histo = Histo->open($self->get_sqlite_path($table_name), $table_name, @_);
	
	# we must set the primary key manually
	$table_histo->key(split(/,/,$self->get_table_key($table_name)));

	return $table_histo
}

sub open_ikos_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_ikos_table() wait args : 'tablename'";
	
	my $table_ikos = eval { ODBC_TXT->open("IKOS_DEV" , $table_name, @_); };
	croak "Error opening $table_name : $@" if $@;

	# we must set the primary key manually
	$table_ikos->key(split(/,/,$self->get_table_key($table_name)));
	
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


sub SQL_create() {
	my $self=shift;
	
	my $tablename=shift or die;

	# Get infos from IKOS tables via ODBC
	my $table = $self->open_ikos_table($tablename, {debug => 0 });
	if (not defined $table) {
		die "error opening $table in IKOS";
	}

	my $table_def=ITools->open("INFO_TABLE",{debug => 0 });
	$table_def->query_condition("TABLE_NAME = '$tablename'");
	my %defined_table=$table_def->fetch_row();
	$table_def->finish;

	warn "WARNING: $tablename n'a pas de clef primaire définie dans INFO_TABLE" if not $defined_table{PRIMARY_KEY};
	#$defined_table{F_KEY};
	#$defined_table{F_TABLE};

	my %size_hash=$table->size();
	my @create_statements;
	foreach my $field ($table->field) {
		my $temp;
		$temp.=$field." ".$size_hash{$field};
		
		$temp.=" PRIMARY KEY" if $field eq $defined_table{PRIMARY_KEY};
		$temp.=" NOT NULL" if grep (/$field/,$table->not_null);
			
		push @create_statements,$temp;
	}

	# spécials columns
	#push @create_statements, "TIMESTAMP_COLLECTE VARCHAR(20) DEFAULT CURRENT_TIMESTAMP";

	my $create_query="CREATE TABLE $tablename (\n";
	$create_query .= join(",\n",@create_statements);
	$create_query .= "\n);\n";

	return $create_query;
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
