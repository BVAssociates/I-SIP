package DATA_interface;

# code import
use strict;
use Carp qw(carp cluck confess croak );
use Scalar::Util qw(blessed);;

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
	if (@_ < 1) {
		croak ('\'new\' take 1 mandatory argument: ${class}->open("tablename" [, {debug => $num}] )')
	}
	$self->{table_name} = shift;
	$options=shift;

	############
	# Data members
	############
	
	# internal description
	$self->{key} = [];
	$self->{field}= [];
	$self->{field_txt}= {};
	$self->{field_desc}= {};
	$self->{size}= {};
	$self->{not_null}= [];
	$self->{dynamic_field} = [];
	
	# user query
	$self->{query_field}  = [];
	$self->{query_condition} = [];
	$self->{query_sort}  = [];
	$self->{output_separator}  = ',';
	$self->{custom_select_query} = undef;
	
	# comparison variables
	$self->{diff_exclude} = [];

	# other internal members
	$self->{debugging} = 0;
	
	############
	# Bless Object. Amen.
	############

    bless($self, $class);
	
	$self->{debugging}=$options->{debug} if exists $options->{debug};
	
	
    return $self;
}

##############################################
## accessor methods         ##
##############################################

sub table_name {
    my $self = shift;
    if (@_) { croak("'table_name' member is read-only") }
    return $self->{table_name};
}

sub field {
    my $self = shift;
    if (@_) { croak("'field' member is read-only") }
    return @{ $self->{field} };
}

sub dynamic_field {
    my $self = shift;
    if (@_) { @{ $self->{dynamic_field} } = @_ };
    return @{ $self->{dynamic_field} };
}

sub field_txt {
    my $self = shift;
    if (@_) { croak("'field_txt' member is read-only") }
    return %{ $self->{field_txt} };
}

sub field_desc {
    my $self = shift;
    if (@_) { croak("'field_desc' member is read-only") }
    return %{ $self->{field_desc} };
}

sub key {
    my $self = shift;
    if (@_) { croak("'key' member is read-only") }
    return @{ $self->{key} };
}

sub not_null {
    my $self = shift;
    if (@_) { croak("'not_null' member is read-only") }
    return @{ $self->{not_null} };
}

sub sort {
    my $self = shift;
    if (@_) { @{ $self->{sort} } = @_ }
    return @{ $self->{sort} };
}

sub size {
    my $self = shift;
    if (@_) { @{ $self->{size} } = @_ }
    return %{ $self->{size} };
}


##Query Values

sub output_separator {
    my $self = shift;
    if (@_) { $self->{output_separator} = shift }
    return $self->{output_separator};
}

sub query_field {
    my $self = shift;

	my @fields=@_;
	if (@fields) {
	if ( $self->has_fields(@fields) != @fields) {
			croak("error querying fields <@fields>");
		} else {
			@{ $self->{query_field} } =  @fields;
		}
	}
	
	return @{ $self->{query_field} }
}

sub query_condition {
    my $self = shift;
    if (@_) { @{ $self->{query_condition} } = @_ }
	
    return grep {defined $_ } @{ $self->{query_condition} };
}

sub query_sort {
    my $self = shift;
	
	my @fields=@_;
	if (@fields) {
	if ( $self->has_fields(@fields) != @fields) {
			croak("error with sort fields <@fields>");
		} else {
			@{ $self->{query_sort} } =  @fields;
		}
	}
	
    return @{ $self->{query_sort} };
}

# other accessors

sub debugging {
    my $self = shift;
    if (@_) { $self->{debugging} = shift}
    return $self->{debugging};
}

##############################################
##  private methods        ##
##############################################

# simple debug method
sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:".ref($self).":".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

# simple print method
sub _info() {
	my $self = shift;
	print STDERR "INFO:".ref($self).":".$self->{table_name}.":".join(' ',@_)."\n";
}


##############################################
##  public methods        ##
##############################################

# set custom SQL query
sub custom_select_query()
{
	my $self = shift;
	
	
	
	# return the user's SQL query
	$self->{custom_select_query} = shift;
}

# Create an SQL query
sub get_query()
{
	my $self = shift;
	
	# return the user's SQL query
	return $self->{custom_select_query} if $self->{custom_select_query};
	
	# construct SQL from "query_*" members
	my $query;
	$query = "SELECT ".join(', ',$self->query_field())." FROM ".$self->table_name();
	$query = $query." WHERE ".join(' AND ',$self->query_condition()) if $self->query_condition() != 0;
	$query = $query." ORDER BY ".join(', ',$self->query_sort()) if $self->query_sort() != 0;
	
	return $query;
}


# explicitly reset the current statement
sub finish() {
	my $self = shift;
	
	croak "finish() not implemented";
}

# force the current statement to stop and disconnecte from database
sub close() {
	my $self = shift;
	
	croak "close() not implemented";
}

# get row  by one based on query
sub fetch_row_array()
{
	my $self = shift;

	croak "fetch_row_array() not implemented";
}

#get hash of row by one based on query
sub fetch_row()
{
	my $self = shift;

	my %row_object;
	my @row=$self->fetch_row_array();
	my @fields=$self->query_field();
	
	return () if @row == 0;
	
	# internal test
	croak "fetch_row_array returned wrong number of values (got ".@row." instead of ".@fields.")" if  @row != @fields;

	for (my $i=0; $i < @fields; $i++) {
		$row_object{$fields[$i]}=$row[$i];
	}
		
	return %row_object;
}


sub execute() {
	my $self = shift;
	
	croak "execute() not implemented";
}

# get information on Table's definition
sub describe()
{
	my $self = shift;
	
	croak("describe() not implemented in ".ref($self));
	return undef;
}

# convert an array to hash following query_field() fields
sub array_to_hash() {
	my $self = shift;
	
	my @row = @_;
	croak "Wrong number of fields (has: ".@row.", expected: ".$self->query_field().")" if @row != $self->query_field() ;
	
	# convert array into hash
	my %row_hash;
	foreach my $field ($self->query_field()) {
		$row_hash{$field} = shift @row ;
	}
	
	return %row_hash;
}

# convert a hash to array following query_field() fields
sub hash_to_array() {
	my $self = shift;
	
	my %row = @_;
	croak "Wrong number of fields (has: ".scalar(keys %row).", expected: ".$self->query_field().")" if (keys %row) != $self->query_field() ;
	
	# convert array into hash
	my @row_array;
	foreach my $field ($self->query_field()) {
		push @row_array, $row{$field} ;
	}
	
	return @row_array;
}

# Insert list  as a row
sub insert_row_array() {
	my $self = shift;

	my %row_hash = $self->array_to_hash(@_);
	
	$self->insert_row(%row_hash);
}

# Insert hash  as a row
sub insert_row() {
	my $self = shift;
	
	croak("insert_row() not implemented in ".ref($self));
}

# add new field
sub add_field() {
	my $self = shift;
	
	croak("add_field() not implemented in ".ref($self));
}

# update a row on a primary key
sub update_row_array() {
	my $self = shift;
	
	my @row = @_;
	croak "Wrong number of fields (has: ".@row.", expected: ".$self->field().")" if @row != $self->field() ;
	
	# convert array into hash
	my %row_hash;
	foreach my $field ($self->field()) {
		$row_hash{$field} = shift @row ;
	}
	
	$self->update_row(%row_hash);
}
# update a row on a primary key
sub update_row() {
	my $self = shift;
	
	croak("update_row() not implemented in ".ref($self));
}

# update a row on a primary key
sub delete_row() {
	my $self = shift;
	
	croak("delete_row() not implemented in ".ref($self));
}

sub has_fields() {
	my $self = shift;
	my @fields_requested = @_;
	my @field_found;
	
	my @field_avaiable=($self->field, $self->dynamic_field);
		
	foreach my $field (@fields_requested) {
		push (@field_found, grep {$field eq $_} @field_avaiable) ;
	}
	$self->_debug("has fields : ",join(',',@field_found));
	return @field_found;
}

sub equals_struct() {
	my $self=shift;
	
	my $data_ref=shift;
	
	croak("argument must be a DATA_interface object") if not (blessed($data_ref) and $data_ref->isa("DATA_interface"));
	
	if ($self->table_name() ne $data_ref->table_name()) {
		$self->_debug("different table_name : $data_ref->table_name() , expected $self->table_name()");
		return 0;
	}
	elsif (join(',',$self->key()) ne join(',',$data_ref->key()) ) {
		$self->_debug("different keys");
		return 0;
	}
	## not necessary!
	#elsif (not $self->has_fields($data_ref->field()) ) {
	#	$self->_debug("different fields");
	#	return 0;
	#}
	else {
		$self->_debug("Table are similar");
		return 1;
	}
}

##############################################
## Destructor        ##
##############################################

#sub DESTROY () {
#	my $self = shift;
#	
#}

1;  # so the require or use succeeds



=head1 NAME

 IKOS::DATA::DATA_Interface - Abstract Interface to access data of IKOS SIP 

=head1 SYNOPSIS

This Class cannnot be instancied as is.
You must use one of the derived Class which implement the open() method and some
method to access data like fetch_row() or insert_row().

As Class sample, you can study the IKOS::DATA::Sqlite or IKOS::DATA::ODBC Classes.

 use IKOS::DATA::Sqlite;
 use IKOS::DATA::ODBC;

 #################
 # class methods #
 #################
 
 $obj = Sqlite->open($database_specification, "tablename"[ ,{ debug => $num, timeout => $sec } ]);

 #######################
 # object data methods #
 #######################

 # data from table
 my @field = $obj->field();
 my @key = $obj->key();
 my %size = $obj->size();
 my @not_null = $obj->not_null();
 
 # set the query
 $obj->query_field("field1","field2");
 $obj->query_sort("field1","field2");
 $obj->query_condition("field1 = 'VALUE'","field2 = 'VALUE'");
 
 # get one line from the results as an array
 my @line=$obj->fetch_row_array();
 
 #reinitialize the result of the query
 $obj->finish();

 # get all the results as objects
 while (my %line=$obj->fetch_row()) {
	print $line{field1};
 }

 # insert row as object
 $obj->insert_row(field1 => "VALUE2", field2 => "VALUE2");
 
 # insert row as array
 $obj->insert_row("VALUE2", "VALUE2");

=head1 DERIVED CLASSES

=over 4

=item IKOS::DATA::Sqlite

Access Data from a Sqlite database.

 $obj = Sqlite->open($database_name, $table_name);

=item IKOS::DATA::ODBC

Access Data from an ODBC database.

 $obj = ODBC->open($DSN, $table_name);

=item IKOS::DATA::ODBC_TXT

Access Data from a database using the ODBC Text Driver.

 $obj = ODBC_TXT->open($DSN, $table_name);
 
=item IKOS::DATA::ITools

Access Data from an ITools table.
The table name must be in $BV_TABPATH.

 $obj = ITools->open($table_name);

=item IKOS::DATA::Histo

Access Data from a special Sqlite database.

In this special table, the fields are versionned and commented.

 $obj = Histo->open($table_name);

=back 

=head1 DESCRIPTION

Please note the following behaviors :

=over 4

=item *

The class opens the database at object creation to get information about columns, and close it immediatly after this.

=item *

The class opens the database at the first call of a "fetch_row*()" method.

=item *

The class closes the database after the last line of query is retrieved.

=item *

The class closes the database with the "finish()" method.

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
 
