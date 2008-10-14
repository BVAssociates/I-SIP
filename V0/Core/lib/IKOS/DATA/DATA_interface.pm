package DATA_interface;

use Carp qw(carp cluck confess croak );
use strict;


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
		croak ('\'new\' take 2 mandatory argument: ${class}->open("tablename" [, {debug => $num}] )')
	}
	$self->{table_name} = shift;
	$options=shift;

	############
	# Data members
	############
	
	# internal description
	$self->{key} = [];
	$self->{field}= [];
	$self->{size}= {};
	$self->{not_null}= [];
	
	# user query
	$self->{query_field}  = [];
	$self->{query_condition} = [];
	$self->{query_sort}  = [];
	#$self->{output_separator}  = '|';
	$self->{custom_select_query} = undef;

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
    return @{ $self->{query_condition} };
}

sub query_sort {
    my $self = shift;
	
	my @fields=@_;
	if (@fields) {
	if ( $self->has_fields(@fields) != @fields) {
			croak("error whith sort fields <@fields>");
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
	print STDERR "DEBUG:".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
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

# force the current statement to stop
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
	die "fetch_row_array did return wrong number of values" if  @row != @fields;

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
	
	croak("describe() not implemented");
}

# Insesrt list  as a row
sub insert_row_array() {
	my $self = shift;
	
	my @row = @_;
	croak "Wrong number of fields (has: ".@row.", expected: ".$self->field().")" if @row != $self->field() ;
	
	# convert array into hash
	my %row_hash;
	foreach my $field ($self->field()) {
		$row_hash{$field} = shift @row ;
	}
	
	$self->insert_row(%row_hash);
}

# Insesrt hash  as a row
sub insert_row() {
	my $self = shift;
	
	croak("insert_row() not implemented");
}

sub has_fields() {
	my $self = shift;
	my @fields_requested = @_;
	my @field_found;
	
	foreach my $field (@fields_requested) {
		push (@field_found, grep {$field eq $_} $self->field) ;
	}
	return @field_found;
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
 
 
 