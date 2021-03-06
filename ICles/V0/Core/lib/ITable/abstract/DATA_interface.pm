package DATA_interface;

# code import
use strict;
use Carp qw(carp cluck confess croak );
use Scalar::Util qw(blessed);

use Isip::IsipLog '$logger';

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
	
	# stocke un tableau index� pour une recherche rapide
	#ex: (field1=>undef, field2=>undef)
	$self->{dynamic_field} = {};
	
	# user query
	$self->{query_field}  = [];
	$self->{query_condition} = [];
	$self->{query_sort}  = [];
	$self->{query_distinct}  = 0;
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

# open a new table, keeping connexion, query and whatever
sub reopen() {
	my $self=shift;
	
	my $new_table=shift or croak("usage : reopen(new_table)");
	
	$self->{table_name}=$new_table;
	
	croak("reopen() not implemented in ".ref($self));
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
    if (@_) {
		foreach my $field (@_) {
			$self->{dynamic_field}->{$field} = undef;
		}
	}
    return keys %{ $self->{dynamic_field} };
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
    if (@_) { %{ $self->{size} } = @_ }
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
		my @field_found=$self->has_fields(@fields);
		
		my %seen_field;
		foreach (@field_found, @fields) {
			$seen_field{$_}++;
		}
		
		my @error_fields;
		foreach (keys %seen_field) {
			push @error_fields, $_ if $seen_field{$_} < 2;
		}
		
		if (@error_fields) {
			croak("error querying fields <@error_fields>");
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

sub query_distinct {
	my $self = shift;
	
	my $enable=shift;
	$self->{query_distinct}=$enable if $enable and $enable =~ /^1|0$/;
	
	return $self->{query_distinct};
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
	
	# if debugging mode, promote debug messages to info messages
	if ($self->debugging()) {
		$logger->info(ref($self).".".$self->{table_name}.",",@_);
	} else {
		$logger->debug(ref($self).".".$self->{table_name}.",",@_);
	}
}

# simple print method
sub _info() {
	my $self = shift;
	
	# if debugging mode, promote info messages to notice messages
	if ($self->debugging()) {
		$logger->notice(ref($self).".".$self->{table_name}.",",@_);
	} else {
		$logger->info(ref($self).".".$self->{table_name}.",",@_);
	}
}

# simple print method
sub _error() {
	my $self = shift;
	$logger->error(ref($self).".".$self->{table_name}.",",@_);
}


##############################################
##  public methods        ##
##############################################

# quote and escape special char
sub quote()
{
	my $self=shift;
	my $string=shift;
	
	$string =~ s/\'/\\'/;
	return "'".$string."'";
}

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
	
	my $distinct="";
	$distinct="DISTINCT" if $self->query_distinct;
	
	# construct SQL from "query_*" members
	my $query;
	$query = "SELECT ".$distinct." ".join(', ',$self->query_field())." FROM ".$self->table_name();
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

	my @row=$self->fetch_row_array();
	my %row_object;
	
	return () if not @row;
	
	foreach my $temp_field (@{ $self->{query_field} }) {
		if (exists $self->{dynamic_field}->{$temp_field}) {
			$row_object{$temp_field}="";
		}
		else {
			croak "fetch_row_array returned wrong number of values (need more field)" if not @row;
			$row_object{$temp_field}=shift @row;
		}
	}
	
	# internal test
	croak "fetch_row_array returned wrong number of values (too much field)" if  @row;

	#for (my $i=0; $i < @real_fields; $i++) {
	#	$row_object{$real_fields[$i]}=$row[$i];
	#}
	#for (@dyna_fields) {
	#	$row_object{$_}="";
	#}
		
	return %row_object;
}

# simple procedure to display a table
sub display_table {
	my $self=shift;
	
	while (my %row=$self->fetch_row()) {
		my @print_row;
		foreach my $field ($self->query_field) {
			my $sep=$self->output_separator();
			if (defined $row{$field}) {
				$row{$field} =~ s/($sep)/?/g;
				push @print_row,$row{$field};
			}
			else {
				push @print_row,"";
			}
		}
		print(join($self->output_separator,@print_row),"\n");
	}

}

sub define_table {
	my $self=shift;

	my %definition;
	
	$definition{SEP}=$self->output_separator;
	$definition{FORMAT}=join($self->output_separator,$self->field);
	$definition{SIZE}=join($self->output_separator,("10s") x scalar $self->field);
	$definition{KEY}=join($self->output_separator,$self->key);
	$definition{NOT_NULL}=join($self->output_separator,$self->not_null) if $self->not_null;

	while(my ($var,$value)=each %definition) {
		print $var."="."\"".$value."\"\n";
	}
}

sub execute() {
	my $self = shift;
	
	croak "execute() not implemented";
}


# convert an array to hash following query_field() fields
sub array_to_hash() {
	my $self = shift;
	
	my @row = @_;
	my @query_field=@{ $self->{query_field} };
	croak "Wrong number of fields (has: ".@row.", expected: ".@query_field.")" if @row != @query_field ;
	
	# convert array into hash
	my %row_hash;
	@row_hash{ @query_field} = @row;
	return %row_hash;
}

# convert a hash to array following query_field() fields
sub hash_to_array() {
	my $self = shift;
	
	my %row = @_;
	croak "Wrong number of fields (has: ".scalar(keys %row).", expected: ".$self->query_field().")" if (keys %row) < $self->query_field() ;
	
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

sub remove_field() {
	my $self = shift;
	
	croak("remove_field() not implemented in ".ref($self));
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
	#$self->_debug("has fields : ",join(',',@field_found));
	return @field_found;
}

sub equals_struct() {
	my $self=shift;
	
	my $data_ref=shift;
	
	croak("argument must be a DATA_interface object") if not (blessed($data_ref) and $data_ref->isa("DATA_interface"));
	
	if ($self->table_name() ne $data_ref->table_name()) {
		$self->_debug("different table_name : ".$data_ref->table_name()." , ".$self->table_name());
	}
	elsif (join(',',$self->key()) ne join(',',$data_ref->key()) ) {
		$self->_error("different keys");
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

 ITable::DATA_Interface - Abstract Interface to access data of IKOS SIP 

=head1 SYNOPSIS

This Class cannnot be instancied as is.
You must use one of the derived Class which implement the open() method and some
method to access data like fetch_row() or insert_row().

As Class sample, you can study the ITable::Sqlite or ITable::ODBC Classes.

 use ITable::Sqlite;
 use ITable::ODBC;

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

=item ITable::Sqlite

Access Data from a Sqlite database.

 $obj = Sqlite->open($database_name, $table_name);

=item ITable::ODBC

Access Data from an ODBC database.

 $obj = ODBC->open($DSN, $table_name);

=item ITable::ODBC_TXT

Access Data from a database using the ODBC Text Driver.

 $obj = ODBC_TXT->open($DSN, $table_name);
 
=item ITable::ITools

Access Data from an ITools table.
The table name must be in $BV_TABPATH.

 $obj = ITools->open($table_name);

=item Isip::ITable::Histo

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

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

=cut
 
