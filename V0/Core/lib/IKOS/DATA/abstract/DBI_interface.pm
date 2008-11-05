package DBI_interface;

require IKOS::DATA::abstract::DATA_interface;
@ISA=("DATA_interface");

use Carp qw(carp cluck confess croak );
use strict;

use DBI;

##################################################
##  constructor  ##
##################################################

# open($Define_obj)
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my $self  = {};
	
	my $options;
	
	# mandatory parameter
	if (@_ < 2) {
		croak ('\'new\' take 2 mandatory argument: ${class}->open("databasename","tablename"[ ,{ timeout => $sec, debug => $num} ])')
	}
    my $database_name = shift;
	my $table_name = shift;
	$options=shift;

	$self=$class->SUPER::open($table_name,$options);

	# internal DB descriptor
	$self->{database_name} = $database_name;
	$self->{database_handle}  = undef;
	$self->{database_statement}  = undef;
	$self->{_active_select} = 0;
	$self->{_active_transaction} = 0;
	
	
	############
	# Bless Object. Amen.
	############

    bless($self, $class);
	
    return $self;
}

##############################################
## accessor methods         ##
##############################################

##Read Only Values

sub database_name {
    my $self = shift;
    if (@_) { croak("'database_name' member is read-only") }
    return $self->{database_name};
}


sub active_transaction {
    my $self = shift;
    if (@_) { croak("'active_transaction' member is read-only") }
    return $self->{_active_transaction};
}

##############################################
##  private methods        ##
##############################################

# simple debug method
sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:DBI:".$self->{database_name}.'.'.$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

# open if needed
## don't open if already opened
sub _open_database() {
	my $self = shift;
	
	croak '$self->_open_database not implemented';
}

# close if needed
## Don't close if active transaction running
sub _close_database() {
	my $self = shift;
	
	$self->finish();
	
	$self->close() if not $self->{_active_transaction};
	
	return 1;
}


sub _execute_select_query() {
	my $self = shift;
		
	# prepare query
	$self->_debug('Prepare SQL : ',$self->get_query);
	eval { $self->{database_statement}=$self->{database_handle}->prepare( $self->get_query() ) };
	confess  "Error in prepare : ".$self->get_query() if $@;
		
	# execute query
	eval { $self->{database_statement}->execute() };
	confess  "Error in execute : ".$self->get_query() if $@;
	
	$self->{_active_select} = 1;
}

##############################################
##  public methods        ##
##############################################

# 
sub begin_transaction() {
	my $self=shift;
	
	$self->_debug("BEGIN transaction");
	$self->{_active_transaction} = 1;
	$self->_open_database();
	$self->{database_handle}->begin_work();
}

sub commit_transaction() {
	my $self=shift;
	
	$self->_debug("COMMIT transaction");
	$self->{_active_transaction} = 0;
	$self->{database_handle}->commit();
	$self->_close_database();
}


# explicitly reset the current statement
sub finish() {
	my $self = shift;
	# finish current statement if any
	if (defined $self->{database_statement}) {
		$self->_debug("Finish current statement for ",$self->{table_name});
		$self->{database_statement}->finish() ;
	}
	#undef $self->{database_statement};
	$self->{_active_select}=0;
	
	return 1;
}

# force the current statement to stop and disconnecte from database
sub close() {
	my $self = shift;
	$self->finish();
	undef $self->{database_statement};
	
	# force close the database
	if (defined $self->{database_handle}) {
		$self->_debug("Close database",$self->{database_name});
		$self->{database_handle}->disconnect();
	}
	undef $self->{database_handle};
}

# get row  by one based on query
sub fetch_row_array()
{
	my $self = shift;

	# open database if needed
	$self->_open_database() if not defined $self->{database_handle};
	
	# connect to database and execute the query
	$self->_execute_select_query() if not defined $self->{database_statement} or not $self->{_active_select};
	
	#return one row
	my @return_line = $self->{database_statement}->fetchrow_array();
	map { $_='' if not defined $_ } @return_line;
	
	# close database if needed
	$self->_close_database() if not @return_line;
	
	return @return_line;
}


sub execute() {
	my $self = shift;
	
	my $query=shift;
	
	$self->_open_database;
	$self->_debug('Excute SQL : ',$query);
	$self->{database_handle}->do($query);
	$self->_close_database;
}


# Insert hash  as a row
sub insert_row() {
	my $self = shift;
	
	my (%row) = @_;
	
	my @error_field;
	foreach my $not_null_field ($self->not_null) {
		if (not defined $row{$not_null_field}) {
			push @error_field, $not_null_field;
		}
	}
	croak join(',',@error_field)." cannot be undef" if @error_field;
	
	# open base
	$self->_open_database;
	
	# quote the fields with the apropriate 
	foreach my $key (keys %row) {
		$row{$key} = $self->{database_handle}->quote($row{$key});
	}
	my $insert_query = sprintf("INSERT INTO %s (%s) VALUES (%s);",$self->{table_name},join(',',keys %row),join(',', values %row));
	
	# prepare query
	$self->_debug('Prepare SQL : ',$insert_query);
	eval { $self->{database_statement}=$self->{database_handle}->prepare( $insert_query ) };
	croak "Error in : ".$insert_query if $@;	
	# execute query
	$self->_debug('Execute SQL');
	eval { $self->{database_statement}->execute() };
	croak "Error while : ".$insert_query if $@;
	
	my $last_id = $self->{database_handle}->last_insert_id('', '', $self->{table_name}, "ID");
	
	$self->_close_database();
	
	return $last_id;
}


# update a row on a primary key
sub update_row() {
	my $self = shift;
	
	my (%row) = @_;
	
	# check if fields exist
	my @error_field;
	my @fields=$self->field();
	foreach my $field (keys %row) {
		if (not grep(/^$field$/, @fields)) {
			push @error_field, $field;
		}
	}
	croak join(',',@error_field)." don't exist in fields" if @error_field;
	
	# check if primary key is valued
	croak "primary key not defined for ".$self->table_name if not $self->key;
	@error_field=();
	foreach my $key_field ($self->key) {
		if (not defined $row{$key_field}) {
			push @error_field, $key_field;
		}
	}
	croak join(',',@error_field)." cannot be undef (PRIMARY KEY)" if @error_field;
	
	# open base
	$self->_open_database;
	
	# don't update primary keys
	# 
	my @primary_keys=$self->key();
	my @updated_fields;
	my @conditions;
	foreach my $field (keys %row) {
		if ( grep(/^$field$/,@primary_keys) ) {
			push @conditions,$field."=".$self->{database_handle}->quote($row{$field});
		} else {
			push @updated_fields,$field."=".$self->{database_handle}->quote($row{$field});
		}
	}
	my $insert_query = sprintf("UPDATE %s SET %s WHERE %s;",$self->{table_name},join(',',@updated_fields),join(',', @conditions));
	
	# prepare query
	$self->_debug('Prepare SQL : ',$insert_query);
	eval { $self->{database_statement}=$self->{database_handle}->prepare( $insert_query ) };
	croak "Error in : ".$insert_query if $@;	
	# execute query
	$self->_debug('Execute SQL');
	eval { $self->{database_statement}->execute() };
	croak "Error while : ".$insert_query if $@;
	
	my $last_id = $self->{database_handle}->last_insert_id('', '', $self->{table_name}, "ID");
	
	$self->_close_database();
	
	return $last_id;
}




sub has_fields() {
	my $self = shift;
	my @fields_requested = @_;
	my @field_found;
	
	foreach my $field (@fields_requested) {
		push (@field_found, grep {uc($field) eq uc($_)} $self->field) ;
	}
	return @field_found;
}

# use transaction
# call update_from() from super class
sub update_from() {
	my $self = shift;
	
	$self->begin_transaction();
	my $update_number = $self->SUPER::update_from(@_);
	$self->commit_transaction();
	
	return $update_number;
}


##############################################
## Destructor        ##
##############################################

sub DESTROY () {
	my $self = shift;
	
	$self->close();
}

1;  # so the require or use succeeds



=head1 NAME

 IKOS::DBI_Interface - Interface to access data of IKOS SIP 

=head1 SYNOPSIS

 use IKOS::Sqlite;
 use IKOS::ODBC;

 #################
 # class methods #
 #################
 
 $obj = Sqlite->open("databasename","tablename"[ ,{ debug => $num, timeout => $sec } ]);
 $obj = ODBC->open("databasename","tablename"[ {  debug => $num } ]);

 #######################
 # object data methods #
 #######################

 # data from table
 @field = $obj->field();
 @key = $obj->key();
 
 # query the table
 $obj->query_field("field1","field2");
 $obj->query_sort("field1","field2");
 $obj->query_condition("condition1","condition2");
 
 # get the results in arrays
 while (my @line=$obj->fetch_row_array()) {
	print $line[0];
 }
 
 #reinitialize the query
 $obj->finish();

 # get the results in objects
 while (my %line=$obj->fetch_row()) {
	print $line{field1};
 }

 ########################
 # other object methods #
 ########################

 $obj->get_query();

=head1 DESCRIPTION

The IKOS::Sqlite class is a simplified interface to DBD::Sqlite.
 
However, please note the following behaviors :
=item *
The class opens the database at object creation to get information about columns, and close it immediatly after this.
=item *
The class opens the database at the first call of a "fetch_row*()" method.
=item *
The class closes the database after the last line of query is retrieved.
=item *
The class closes the database with the "finish()" method.
 
 
 