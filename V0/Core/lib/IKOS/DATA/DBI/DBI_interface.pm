package DBI_interface;

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
    $self->{database_name} = shift;
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

	# internal DB descriptor
	$self->{database_handle}  = undef;
	$self->{database_statement}  = undef;
	
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

##Read Only Values

sub database_name {
    my $self = shift;
    if (@_) { croak("'database_name' member is read-only") }
    return $self->{database_name};
}

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
	
    return grep {defined $_ } @{ $self->{query_condition} };
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
	print STDERR "DEBUG:DBI:".$self->{database_name}.'.'.$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

# 
sub _open_database() {
	my $self = shift;
	
	croak '$self->_open_database not implemented';
}

sub _close_database() {
	my $self = shift;
	
	$self->close();
	
	return 1;
}


sub _execute_select_query() {
	my $self = shift;
	
	# open database
	$self->_open_database() if not defined $self->{database_handle};
	
	# prepare query
	$self->_debug('Prepare SQL : ',$self->get_query);
	eval { $self->{database_statement}=$self->{database_handle}->prepare( $self->get_query() ) };
	confess  "Error in prepare : ".$self->get_query() if $@;
		
	# execute query
	eval { $self->{database_statement}->execute() };
	confess  "Error in execute : ".$self->get_query() if $@;
}

sub _begin_work() {
	my $self=shift;
	
	$self->{database_statement}->begin_work();
}

sub _commit() {
	my $self=shift;
	
	$self->{database_statement}->commit();
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

# reset the current statement
sub finish() {
	my $self = shift;
	# finish current statement if any
	if (defined $self->{database_statement}) {
		$self->_debug("Finish current statement for ",$self->{table_name});
		$self->{database_statement}->finish() ;
	}
	undef $self->{database_statement};
	
	return 1;
}

# force the current statement to stop and disconnecte from database
sub close() {
	my $self = shift;
	$self->finish();
	
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

	# connect to database and execute the query
	$self->_execute_select_query() if not defined $self->{database_statement};
	
	#return one row
	my @return_line = $self->{database_statement}->fetchrow_array();
	map { $_='' if not defined $_ } @return_line;
	
	$self->_close_database() if not @return_line;
	return @return_line;
}

#get hash of row by one based on query
sub fetch_row()
{
	my $self = shift;

	my %row_object;
	my @row=$self->fetch_row_array();
	my @fields=$self->query_field();
	
	return () if @row == 0;
	
	# debug  test
	die "fetch_row_array did return wrong number of values" if  @row != @fields;

	for (my $i=0; $i < @fields; $i++) {
		$row_object{$fields[$i]}=$row[$i];
	}
	
	
	return %row_object;
}


sub execute() {
	my $self = shift;
	
	my $query=shift;
	
	$self->_open_database;
	$self->_debug('Excute SQL : ',$query);
	$self->{database_handle}->do($query);
	$self->_close_database;
}

# get information on Table's definition
sub describe()
{
	my $self = shift;
	
	croak("describe() not implemented");
	return undef;
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
	
	my %row = @_;
	
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

=begin comment 

  $dbh->{AutoCommit} = 0;  # enable transactions, if possible
  $dbh->{RaiseError} = 1;
  eval {
      foo(...)        # do lots of work here
      bar(...)        # including inserts
      baz(...)        # and updates
      $dbh->commit;   # commit the changes if we get this far
  };
  if ($@) {
      warn "Transaction aborted because $@";
      # now rollback to undo the incomplete changes
      # but do it in an eval{} as it may also fail
      eval { $dbh->rollback };
      # add other application on-error-clean-up code here
  }
  
=end comment
=cut



sub has_fields() {
	my $self = shift;
	my @fields_requested = @_;
	my @field_found;
	
	foreach my $field (@fields_requested) {
		push (@field_found, grep {uc($field) eq uc($_)} $self->field) ;
	}
	return @field_found;
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
 
 
 