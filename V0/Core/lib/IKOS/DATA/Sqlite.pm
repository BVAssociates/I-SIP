package Sqlite;

require IKOS::DATA::abstract::DBI_interface;
@ISA = ("DBI_interface");

use Carp qw(carp cluck confess croak );
use strict;

#use Data::Dumper;

##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;	
	
	my $options= pop @_ if @_ == 3;
	
	my $self  = $class->SUPER::open(@_, $options);
	
	$self->{database_path} = $self->database_name;
	$self->{timeout}=10000;
	
	############
	# Options to overide default settings
	############
	
	$self->{timeout}=$options->{timeout} if exists $options->{timeout};

	############
	# Opening Database
	############
	
	$self->_open_database();

	############
	# Get Database's infos
	############
	
	# quick test
	croak "Error openning sqlite database : $self->{database_path}" unless $self->{database_handle}->ping();
	
	# put fields list in memory
	$self->_debug("Get info for table : ",$self->{table_name});
	$self->_set_columns_info();
	
	$self->_debug("Fields : ", join("|",$self->field()));
	$self->_debug("Keys : ", join("|",$self->key()));
	$self->_debug("Not NULL : ", join("|",$self->not_null()));
	my %temp_hash=$self->size();
	$self->_debug("Size : ", join("|",values %temp_hash ));
	
	# set defaut for query_field
	$self->{query_field} = [ $self->field() ];
	
	# free any LOCK
	$self->_close_database();
	
    return bless($self,$class);
}

=begin comment

#static method, don't return object
# create table on a Sqlite Database
sub execute() {
    my $self = shift;
	
	croak "Sqlite->Create is a Class method" if not ref($self);
	
	my $options= pop @_ if @_ == 4;
	
	# mandatory parameter
	if (@_ < 3) {
		croak ('\'new\' take 3 mandatory argument: Sqlite->open("database_path","tablename"[ ,{ debug => $num} ])')
	}
    $self->{database_name} = shift;
	$self->{table_name} = shift;
	$self->{create_request} = shift;
	$options=shift;
	
	$self->{database_path};
	$self->{timeout}=60000;
	
	############
	# Options to overide default settings
	############
	
	$self->{timeout}=$options->{timeout} if exists $options->{timeout};

	############
	# Create temprorary instance of class
	############
	my $temp_obj=bless($self,$class);
	
	
	############
	# Opening Database
	############
	
	
	if (not exists $ENV{BV_TABPATH}) {
		croak('Environnament variable "BV_TABPATH" does not exist');
	}
	
	# look for the database name in BV_TABPATH
	$temp_obj->{database_path} = $temp_obj->_find_file($ENV{BV_TABPATH},$temp_obj->{database_name}.".sqlite3");
	
	if (not defined $temp_obj->{database_path}) {
		croak("Error opening sqlite database : $temp_obj->{database_name}");
	}
	$temp_obj->_open_database();

	############
	# Get Database's infos
	############
	
	# quick test
	croak "Error openning sqlite database : $temp_obj->{database_path}" unless $temp_obj->{database_handle}->ping();
	
	
	
	# free any LOCK
	$self->_close_database();
	
	# Creation done. Open created table
	return Sqlite->open($temp_obj->{database_name},$temp_obj->{table_name}, $options);
}
=end comment
=cut

##################################################
##  pivate methods  ##
##################################################

# Get information from database
# Need "$self->{database_handle}" to be connected !
sub _set_columns_info() {
	my $self = shift;
	
	die("$self->{database_name} need to be opened before execute _set_columns_info") if not defined $self->{database_handle};
	
	my $table_info=$self->{database_handle}->prepare("PRAGMA table_info($self->{table_name})");
	$self->_debug("Get columns info for $self->{table_name}");
	$table_info->execute();
	
	while (my @col=eval { $table_info->fetchrow_array } ) {
		push (@{$self->{field}},       $col[1]);
		$self->{size}->{$col[1]}=       $col[2];
		push (@{$self->{not_null}},     $col[1]) if $col[3];
		push (@{$self->{key}},          $col[1]) if $col[5];
	}
	croak $@ if $@;
	
	if (not $self->field()) {
		croak("Error reading information of table : $self->{table_name}");
	}
}

sub _set_timeout() {
	my $self = shift;
	
	# set BUSY timeout (ms to wait for a LOCK)
	$self->{database_handle}->func( $self->{timeout}, 'busy_timeout' );
	$self->_debug( "Timeout : ".$self->{database_handle}->func('busy_timeout'));
}

sub _find_file() {
	my $self = shift;
	
	my $path_list = shift;
	my $filename = shift;
	
	use Config;
	my $env_separator = $Config{path_sep};
	
	my $filepath;
	foreach my $path (split ($env_separator,$path_list)) {
		return $path."/".$filename if -r $path."/".$filename;
	}
	
	$self->_debug("$filename not found in $path_list");
	
	return undef;
}

sub _open_database() {
	my $self = shift;
	
	croak 'no database_path defined' if not defined $self->{database_path};
	
	return if defined $self->{database_handle};
	
	$self->_debug("Open database file : ",$self->{database_path});
	# use RaiseError exception to stop the script at first error
	$self->{database_handle} = DBI->connect("dbi:SQLite:dbname=$self->{database_path}","","",{ RaiseError => 1});
	
	# set the busy timeout
	$self->_set_timeout();
	
	# register external sqlite function
	$self->_register_external_function();
}

sub _register_external_function() {
	my $self = shift;
	
	# register has_numeric(field)
	$self->{database_handle}->func( 'has_numeric', 1, sub { return shift =~ /\d/; }, 'create_function' );
}

##################################################
##  public methods  ##
##################################################


=head1 NAME

 IKOS::Sqlite - Implement abstract Class IKOS::DBI_Interface for a Sqlite Backend

=head1 SYNOPSIS

 use IKOS::Sqlite;

 #################
 # class methods #
 #################
 
 $obj = Sqlite->open("databasename","tablename"[ ,{ debug => $num, timeout => $sec } ]);


=head1 DESCRIPTION

 The IKOS::Sqlite class is a simplified interface to DBD::Sqlite.
 
 However, please note the following behaviors :
  * The class opens the database at object creation to get information about columns, and close it immediatly after this.
  * The class opens the database at the first call of a "fetch_row*()" method.
  * The class closes the database after the last line of query is retrieved.
  * The class closes the database with the "finish()" method.
 
 
=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

=cut
