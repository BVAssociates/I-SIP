package DATA_interface;

# code import
use strict;
use Carp qw(carp cluck confess croak );
use Scalar::Util;

# project import
use IKOS::DATA::TableDiff;

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
	print STDERR "DEBUG:".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

# simple print method
sub _info() {
	my $self = shift;
	print "INFO:".$self->{table_name}.":".join(' ',@_)."\n";
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
	die "fetch_row_array returned wrong number of values (got ".@row." instead of ".@fields.")" if  @row != @fields;

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

sub has_fields() {
	my $self = shift;
	my @fields_requested = @_;
	my @field_found;
	
	my @field_avaiable=($self->field, $self->dynamic_field);
		
	foreach my $field (@fields_requested) {
		push (@field_found, grep {$field eq $_} @field_avaiable) ;
	}
	return @field_found;
}

sub reset_compare() {
	my $self=shift;
	
	$self->{diff_update} = {};
	$self->{diff_new} = {};
	$self->{diff_delete} = {};
}

# add field list not to compare
sub compare_exclude() {
    my $self = shift;

	my @fields=@_;
	if (@fields) {
		if ( $self->has_fields(@fields) != @fields) {
			carp("compare_exclude : field not found <@fields>");
		}
		@{ $self->{diff_exclude} } =  @fields;
	}

	return @{ $self->{diff_exclude} }
}


##########################
# Old compare_from() method based on the sorted Primary Key
# Problem : different ORDER BY behavior between 2 databases type
# -> cannot be used on 2 different base
# -> use new compare_from() instead (need more memory)
##########################
# compare a table to $self
#  $self->{diff_update}{key_value}{field1}="field_value"
#  $$self->{diff_new}{key_value}{field1}="field_value"
#  $self->{diff_delete}{key_value}{field1}="field_value"
# return the number of differences found
sub compare_from_sql_based() {
	my $self=shift;
	
	my $table_from = shift;
	my @key;

	# store the result in a TableDiff object
	my $diff_object=TableDiff->new();
	
	if ( join(',',sort $self->key()) ne  join(',',sort $table_from->key())) {
		croak("The 2 tables have not the same keys : ".join(',',sort $self->key())." => ".join(',',sort $table_from->key()));
	}
	
	if ( $self->table_name() ne $table_from->table_name() ) {
		croak("The 2 tables have not the same name : ".$self->table_name()." => ".$table_from->table_name());
	}
	
	
	if ( join(',',sort $self->field()) ne  join(',',sort $table_from->field())) {
		#croak("The 2 tables have not the same fields");
	}
	
	@key=$self->key();
	$self->query_sort(@key);
	$table_from->query_sort(@key);
	
	#first pass to get primary keys which are on one table
	## after 2 loops :
	##	$seen_keys{keys} = 1 if only me have it
	##	$seen_keys{keys} = 0 if the two table have it
	##	$seen_keys{keys} = -1 if only $table_from have it
	my %seen_keys;
	my @row;
	$self->query_field(@key);
	$table_from->query_field(@key);
	while (@row=$self->fetch_row_array()) {
		$seen_keys{join(',',@row)}++
	}
	while (@row=$table_from->fetch_row_array()) {
		$seen_keys{join(',',@row)}--
	}
	
	
	$self->query_field($self->field);
	$table_from->query_field($table_from->field);
	
	my %row_table1;
	my %row_table2;
	
	# main loop
	# We supprose here that the 2 tables are ordered by their Primary Keys
	foreach my $current_keys (sort keys %seen_keys) {
		my $new_keys = $seen_keys{$current_keys};
		# this key does not exist anymore
		if ($new_keys < 0) {
			$self->_info("Found new line : Key (".$current_keys.")");
			%row_table1=$table_from->fetch_row;
			
			# something wrong appens !
			confess "FATAL:bad line key : ".join(',',@row_table1{@key})." (intended : $current_keys)" if join(',',@row_table1{@key}) ne $current_keys;
			
			# remove excluded fields
			foreach my $field ( $self->compare_exclude ) {
				delete $self->{diff_new}{$current_keys}{$field};
			}
			
			#put whole row
			$diff_object->add_new($current_keys,{ %row_table1});
		}
		# this key are new in the table
		elsif ($new_keys > 0) {
			$self->_info("Found deleted line : Key (".$current_keys.")");
			%row_table2=$self->fetch_row;
			
			confess "FATAL:bad line key : ".join(',',@row_table2{@key})." (intended : $current_keys)" if join(',',@row_table2{@key}) ne $current_keys;
			
			# remove excluded fields
			foreach my $field ($self->compare_exclude) {
				delete $self->{diff_delete}{$current_keys}{$field};
			}
			
			$diff_object->add_delete($current_keys,{ %row_table2});
			
		}
		# this key exist in the 2 tables
		# find the differences
		else {
			%row_table1=$table_from->fetch_row;
			%row_table2=$self->fetch_row;
			
			confess "FATAL:bad line key : ".join(',',@row_table1{@key})." (intended : $current_keys)" if join(',',@row_table1{@key}) ne $current_keys;
			confess "FATAL:bad line key : ".join(',',@row_table2{@key})." (intended : $current_keys)" if join(',',@row_table2{@key}) ne $current_keys;
			
			foreach my $field1 (keys %row_table1) {
			
				next if grep(/^$field1$/, $self->compare_exclude);
				
				if (not exists $row_table2{$field1}) {
					$self->_info("Found new column : Key (".$current_keys.") $field1 : $row_table1{$field1}");
					push (@{$self->{diff_new_field}},$field1) if not grep(/^$field1$/,@{$self->{diff_new_field}});
					$diff_object->add_update($current_keys, $field1, $row_table1{$field1});
					
				} elsif ($row_table1{$field1} ne $row_table2{$field1}) {
					$self->_info("Found update : Key (".$current_keys.") $field1 :",$row_table2{$field1}," -> ",$row_table1{$field1} );
					$diff_object->add_update($current_keys, $field1, $row_table1{$field1});
					
				}
			}
		}
	}
	$self->finish;
	$table_from->finish;
	
	return $diff_object;
}

# compare the table in arg to $self
#  return a TableDiff object
# /!\ put the 2 tables in memory
# use compare_from_sql_based() if table and $self use the same driver/database
sub compare_from() {
	my $self=shift;
	
	my $table_from = shift;
	my @key;

	# store the result in a TableDiff object
	my $diff_object=TableDiff->new();
	
	if (not $self->key()) {
		croak("No key defined");
	}
	if ( join(',',sort $self->key()) ne  join(',',sort $table_from->key())) {
		croak("The 2 tables have not the same keys : ".join(',',sort $self->key())." => ".join(',',sort $table_from->key()));
	}
	
	if ( $self->table_name() ne $table_from->table_name() ) {
		croak("The 2 tables have not the same name : ".$self->table_name()." => ".$table_from->table_name());
	}
	
	#if ( join(',',sort $self->field()) ne  join(',',sort $table_from->field())) {
	#	croak("The 2 tables have not the same fields");
	#}
	
	@key=$self->key();
	$self->query_sort(@key);
	$table_from->query_sort(@key);
	
	# Slurp the tables in memory
	my %in_memory_table1;
	my %in_memory_table2;
	my %row;
	while (%row=$table_from->fetch_row()) {
		$in_memory_table1{join(',',@row{@key})}={ %row };
	}
	while (%row=$self->fetch_row()) {
		$in_memory_table2{join(',',@row{@key})}={ %row };
	}
	undef %row;
	

	#first pass to get primary keys which are on one table
	## after 2 loops :
	##	$seen_keys{keys} = 1 if only me have it
	##	$seen_keys{keys} = 0 if the two table have it
	##	$seen_keys{keys} = -1 if only $table_from have it
	my %seen_keys;
	
	foreach (keys %in_memory_table1) {
		$seen_keys{$_}--;
	}
	foreach (keys %in_memory_table2) {
		$seen_keys{$_}++;
	}

	# main loop
	foreach my $current_keys (sort keys %seen_keys) {
		my $new_keys = $seen_keys{$current_keys};
		# this key does not exist anymore
		if ($new_keys < 0) {
			$self->_info("Found new line : Key (".$current_keys.")");
			
			# remove excluded fields
			foreach my $field ( $self->compare_exclude ) {
				delete $in_memory_table1{$current_keys}{$field};
			}
			
			#put whole row
			$diff_object->add_new($current_keys,$in_memory_table1{$current_keys});
		}
		# this key are new in the table
		elsif ($new_keys > 0) {
			$self->_info("Found deleted line : Key (".$current_keys.")");
			
			# remove excluded fields
			foreach my $field ($self->compare_exclude) {
				delete $self->{diff_delete}{$current_keys}{$field};
			}

			#put whole row
			$diff_object->add_delete($current_keys,$in_memory_table2{$current_keys});

		}
		# this key exist in the 2 tables
		# find the differences
		else {
			my %row_table1=%{ $in_memory_table1{$current_keys} };
			my %row_table2=%{ $in_memory_table2{$current_keys} };
			
			foreach my $field1 (keys %row_table1) {
			
				next if grep(/^$field1$/, $self->compare_exclude);
				
				if (not exists $row_table2{$field1}) {
					$self->_info("Found new column : Key (".$current_keys.") $field1 : $row_table1{$field1}");
					$diff_object->add_new_field($field1) if not grep(/^$field1$/,@{$self->{diff_new_field}});
					$diff_object->add_update($current_keys,$field1,$row_table1{$field1});
					
				} elsif ($row_table1{$field1} ne $row_table2{$field1}) {
					$self->_info("Found update : Key (".$current_keys.") $field1 : '",$row_table2{$field1},"' -> '",$row_table1{$field1} ,"'");
					$diff_object->add_update($current_keys,$field1,$row_table1{$field1});
					
				}
			}
		}
	}
	
	return $diff_object;
}

# apply the data in a TableDiff oject in the table
sub update_from() {
	my $self = shift;
	
	my $diff_object = shift;
	croak "update_from take 1 arg : DiffTable".ref($diff_object) if ref($diff_object) ne "TableDiff";
	
	my $request_number=0;
	$self->begin_transaction();
	
	# insert new lines
	my %key_new_hash=$diff_object->get_new();
	foreach my $key_new (keys %key_new_hash ) {
		$self->insert_row( %{ $key_new_hash{$key_new} } );
		$request_number++;
	}
	undef %key_new_hash;
	
	# delete removed lines
	my %key_delete_hash=$diff_object->get_delete();
	foreach my $key_delete (keys %key_delete_hash) {
		#$self->delete_row(%{ $self->{diff_delete}{$key_delete} });
		#$request_number++;
	}
	undef %key_delete_hash;
	
	# add new field
	my @key_new_field_hash=$diff_object->get_new_field();
	foreach my $new_field (@key_new_field_hash) {
		$self->add_field($new_field);
		$request_number++;
	}
	undef @key_new_field_hash;
	
	# update modified lines
	my @table_key=sort $self->key();
	my %key_update_hash=$diff_object->get_update();
	foreach my $key_update (keys %key_update_hash ) {
	
		#Check new fields
		my @update_field=keys %{ $key_update_hash{$key_update} };
			
		# get tables keys
		my @table_key_value=split(/,/,$key_update);
		
		# update_row() need the keys to be defined
		# -> add tables keys to rows needed for update
		foreach (@table_key) {
			$key_update_hash{$key_update}{$_} = shift @table_key_value;
		}
		
		$self->update_row(%{ $key_update_hash{$key_update} });
		$request_number++;
	}
	$self->commit_transaction();
	$self->_info("Les changements ont été appliqués ($request_number)");
	
	return $request_number;
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
 
