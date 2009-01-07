package DataDiff;


require IKOS::DATA::abstract::DATA_interface;
@ISA = ("DATA_interface");

use Carp qw(carp cluck confess croak );
use strict;
use Scalar::Util qw(blessed);

use IKOS::IsipDiff;

#use Data::Dumper;

##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;	
	
	# mandatory parameter
	if (@_ < 2) {
		croak ($class.'->open : take 2 mandatory argument: ${class}->open( $data_ref1, $date_ref2[ ,{ timeout => $sec, debug => $num} ])')
	}

	my $data_ref1 = shift;
	my $data_ref2 = shift;
	my $options=shift;
	
	croak("$class->open : arg1 must be an DATA_interface object") if not ( blessed ($data_ref1) and $data_ref1->isa("DATA_interface"));
	croak("$class->open : arg2 must be an DATA_interface object") if not ( blessed ($data_ref2) and $data_ref2->isa("DATA_interface"));
	croak("$class->open : arg1 et arg2 have different structure") if not $data_ref1->equals_struct($data_ref2);
	
	# uncomment to warn that caller already set query fields
	#carp("$class->open : arg1 et arg2 have different query fields") if $data_ref1->query_field() ne $data_ref2->query_field();
	

	my $self=$class->SUPER::open($data_ref1->table_name(), $options);
	
	# DataDiff table related

	$self->{table_1}=$data_ref1;
	$self->{table_2}=$data_ref2;
	
	# this object will store differences
	$self->{diff}={};
	
	# static value
	$self->{current_source_only_key}=[];
	$self->{fetch_source_only_running}=0;
	
	# We bless the object with the new class
	bless ($self, $class);
	
	# user query
	$self->{query_field}  = [ $self->field() ];
	$self->{dynamic_field}  = [ "ICON" ];
	
	$self->_debug("initialisation");
	
    return $self;
}

##################################################
##  public methods delegated to the first table ##
##################################################


sub table_name {
    my $self = shift;
    if (@_) { croak("'table_name' member is read-only") }
    return $self->{table_1}->{table_name};
}

sub field {
    my $self = shift;
    if (@_) { croak("'field' member is read-only") }
    return @{ $self->{table_1}->{field} };
}

sub field_txt {
    my $self = shift;
    if (@_) { croak("'field_txt' member is read-only") }
    return %{ $self->{table_1}->{field_txt} };
}

sub field_desc {
    my $self = shift;
    if (@_) { croak("'field_desc' member is read-only") }
    return %{ $self->{table_1}->{field_desc} };
}

sub key {
    my $self = shift;
    if (@_) { croak("'key' member is read-only") }
    return @{ $self->{table_1}->{key} };
}

sub not_null {
    my $self = shift;
    if (@_) { croak("'not_null' member is read-only") }
    return @{ $self->{table_1}->{not_null} };
}


sub size {
    my $self = shift;
    if (@_) { croak("'size' member is read-only") }
    return %{ $self->{table_1}->{size} };
}


##################################################
##  public methods  ##
##################################################

# return the IsipDiff that store the diff
sub get_diff_object() {
	my $self=shift;

	return $self->{diff};
}

# can only sort by table primary keys
sub query_sort {
    my $self = shift;
    if (@_) { 
		if (join(',',@_) eq join(',',$self->key)) {
			@{ $self->{query_sort} } = @_;
		} else {
			croak("unable to set query_sort to ".join(',',@_)); 
		}
	}
    return @{ $self->{query_sort} };
}


sub query_condition() {
	my $self = shift;

    if (@_) { 
		croak("Unable to set condition on ".ref($self));
	}
    return @{ $self->{query_condition} };
}

# utility sub for fetch_row
sub _next_source_only() {
	my $self = shift;
	
	# if no deleted keys, return nothing
	if (not $self->{diff}->get_source_only()) {
		return undef;
	}
	
	# if 
	if (not ($self->{fetch_source_only_running} or @{$self->{current_source_only_key}} )) {
		my %temp=$self->{diff}->get_source_only();
		@{$self->{current_source_only_key}} = sort (keys %temp);
	}
	
	my $return_key=shift @{ $self->{current_source_only_key} }
}

# get row  by one based on query
sub fetch_row() {
	my $self = shift;
	
	my %current_row;
	
	#$self->compare() if not defined $self->{diff};
	
	# first, printing lines only in source
	my $source_only_key=$self->_next_source_only();
	$self->{fetch_source_only_running}=1;
	
	# We use table_2 to print the target table
	# Then we add informations about the difference with table_1
	if ($source_only_key) {
		%current_row = $self->{diff}->get_source_only_by_key($source_only_key);
	}
	else {
		%current_row = $self->{table_2}->fetch_row();
	}
	
	# table_2 return no lines, so we return
	if (not %current_row) {
		$self->{fetch_source_only_running}=0;
		return ();
	}
	
	my $key=join(',',sort @current_row{$self->{table_2}->key()});
	
	if (grep ('^ICON$', $self->query_field()) ) {
		$current_row{ICON}=$self->{diff}->get_row_status($key);
	}
	
	return %current_row;
}

sub fetch_row_array() {
	my $self = shift;
	
	#warn "WARNING : unable to find the colums order";
	my @return_line;
	
	my %hash_line = $self->fetch_row;
	
	
	return () if not %hash_line;
	return @hash_line{$self->query_field()};
}

# TO DELETE ?
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
sub compare_order_based() {
	my $self=shift;
	
	my $table_from = $self->{table_1};
	my $table_to = $self->{table_2};
	my @key;

	# store the result in a IsipDiff object
	my $diff_object=IsipDiff->new();
	
	if ( join(',',sort $table_to->key()) ne  join(',',sort $table_from->key())) {
		croak("The 2 tables have not the same keys : ".join(',',sort $table_to->key())." => ".join(',',sort $table_from->key()));
	}
	
	if ( $table_to->table_name() ne $table_from->table_name() ) {
		croak("The 2 tables have not the same name : ".$table_to->table_name()." => ".$table_from->table_name());
	}
	
	
	if ( join(',',sort $table_to->field()) ne  join(',',sort $table_from->field())) {
		#croak("The 2 tables have not the same fields");
	}
	
	@key=$self->key();
	$table_to->query_sort(@key);
	$table_from->query_sort(@key);
	
	#first pass to get primary keys which are on one table
	## after 2 loops :
	##	$seen_keys{keys} = 1 if only me have it
	##	$seen_keys{keys} = 0 if the two table have it
	##	$seen_keys{keys} = -1 if only $table_from have it
	my %seen_keys;
	my @row;
	$table_to->query_field(@key);
	$table_from->query_field(@key);
	while (@row=$table_to->fetch_row_array()) {
		$seen_keys{join(',',@row)}++
	}
	while (@row=$table_from->fetch_row_array()) {
		$seen_keys{join(',',@row)}--
	}
	
	
	$table_to->query_field($table_to->field);
	$table_from->query_field($table_from->field);
	
	my %row_table1;
	my %row_table2;
	
	# main loop
	# We supprose here that the 2 tables are ordered by their Primary Keys
	foreach my $current_keys (sort keys %seen_keys) {
		my $new_keys = $seen_keys{$current_keys};
		# this key does not exist anymore
		if ($new_keys < 0) {
			$self->_debug("Found new line : Key (".$current_keys.")");
			%row_table1=$table_from->fetch_row;
			
			# something wrong appens !
			confess "FATAL:bad line key : ".join(',',@row_table1{@key})." (expected : $current_keys)" if join(',',@row_table1{@key}) ne $current_keys;
			
			# remove excluded fields
			foreach my $field ( $self->compare_exclude ) {
				delete $row_table1{$field};
			}
			
			#put whole row
			$diff_object->add_target_only($current_keys,{ %row_table1});
		}
		# this key are new in the table
		elsif ($new_keys > 0) {
			$self->_debug("Found deleted line : Key (".$current_keys.")");
			%row_table2=$self->fetch_row;
			
			confess "FATAL:bad line key : ".join(',',@row_table2{@key})." (expected : $current_keys)" if join(',',@row_table2{@key}) ne $current_keys;
			
			# remove excluded fields
			foreach my $field ($self->compare_exclude) {
				delete $row_table2{$field};
			}
			
			$diff_object->add_source_only($current_keys,{ %row_table2});
			
		}
		# this key exist in the 2 tables
		# find the differences
		else {
			%row_table1=$table_from->fetch_row;
			%row_table2=$self->fetch_row;
			
			confess "FATAL:bad line key : ".join(',',@row_table1{@key})." (expected : $current_keys)" if join(',',@row_table1{@key}) ne $current_keys;
			confess "FATAL:bad line key : ".join(',',@row_table2{@key})." (expected : $current_keys)" if join(',',@row_table2{@key}) ne $current_keys;
			
			foreach my $field1 (keys %row_table1) {
			
				next if grep(/^$field1$/, $self->compare_exclude);
				
				if (not exists $row_table2{$field1}) {
					$self->_debug("Found new column : Key (".$current_keys.") $field1 : $row_table1{$field1}");
					$diff_object->add_source_only_field($field1) if not grep(/^$field1$/,@{$self->{diff_new_field}});
					$diff_object->add_source_update($current_keys, $field1, $row_table1{$field1});
					
				} elsif ($row_table1{$field1} ne $row_table2{$field1}) {
					$self->_debug("Found update : Key (".$current_keys.") $field1 :",$row_table2{$field1}," -> ",$row_table1{$field1} );
					$diff_object->add_source_update($current_keys, $field1, $row_table1{$field1});
					
				}
			}
		}
	}
	$self->finish;
	$table_from->finish;
	
	return $diff_object;
}

# compare the table in arg to $self
# /!\ put the 2 tables in memory
# use compare_from_sql_based() if table and $self use the same driver/database
sub compare() {
	my $self=shift;
	
	my $table_from = $self->{table_1};
	my $table_to = $self->{table_2};
	my @key;

	# store the result in a IsipDiff object
	$self->{diff}=IsipDiff->new();
	
	if (not $table_to->key()) {
		croak("No key defined");
	}
	if ( join(',',sort $table_to->key()) ne  join(',',sort $table_from->key())) {
		croak("The 2 tables have not the same keys : ".join(',',sort $table_to->key())." => ".join(',',sort $table_from->key()));
	}
	
	if ( $table_to->table_name() ne $table_from->table_name() ) {
		croak("The 2 tables have not the same name : ".$table_to->table_name()." => ".$table_from->table_name());
	}
	
	#if ( join(',',sort $self->field()) ne  join(',',sort $table_from->field())) {
	#	croak("The 2 tables have not the same fields");
	#}
	
	@key=$table_to->key();
	$table_to->query_sort(@key);
	$table_from->query_sort(@key);
	
	# Slurp the tables in memory
	my %in_memory_table1;
	my %in_memory_table2;
	my %row;
	while (%row=$table_from->fetch_row()) {
		$in_memory_table1{join(',',@row{@key})}={ %row };
	}
	while (%row=$table_to->fetch_row()) {
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
		# this key does not exist on the target
		if ($new_keys < 0) {
			$self->_info("Line only in source table : Key (".$current_keys.")");
			
			# remove excluded fields
			foreach my $field ( $self->compare_exclude ) {
				delete $in_memory_table1{$current_keys}{$field};
			}
			
			#put whole row
			$self->{diff}->add_source_only($current_keys,$in_memory_table1{$current_keys});
		}
		# this key are new in the table table
		elsif ($new_keys > 0) {
			$self->_info("Line only in target table : Key (".$current_keys.")");
			
			# remove excluded fields
			foreach my $field ($self->compare_exclude) {
				delete $in_memory_table2{$current_keys}{$field};
			}

			#put whole row
			$self->{diff}->add_target_only($current_keys,$in_memory_table2{$current_keys});

		}
		# this key exist in the 2 tables
		# find the differences
		else {
			my %row_table1=%{ $in_memory_table1{$current_keys} };
			my %row_table2=%{ $in_memory_table2{$current_keys} };

			foreach my $field1 (keys %row_table2) {
			
				next if grep(/^$field1$/, $self->compare_exclude);
				
				if (not exists $row_table2{$field1}) {
					$self->_info("Column only in target : Key (".$current_keys.") $field1 : $row_table1{$field1}");
					$self->{diff}->add_source_only_field($field1);
					$self->{diff}->add_source_update($current_keys,$field1,$row_table1{$field1});
				
				} elsif (not defined $row_table1{$field1}) {
					$self->_info("Column only in source (ignore) : Key (".$current_keys.") $field1");
					
				} elsif ($row_table2{$field1} ne $row_table1{$field1}) {
					$self->_info("Field modified in target table : Key (".$current_keys.") $field1 : '",$row_table1{$field1},"' -> '",$row_table2{$field1} ,"'");
					$self->{diff}->add_source_update($current_keys,$field1,$row_table1{$field1});
					
				}
			}
		}
	}
	
	return $self->{diff};
}

# apply the data in a IsipDiff oject in the table
sub update_compare_target() {
	my $self = shift;
	
	$self->compare() if not defined $self->{diff};
	
	my $diff_object = $self->{diff};
	
	my $request_number=0;
	$self->{table_2}->begin_transaction();
	
	# remove lines only in source
	my %key_new_hash=$diff_object->get_target_only();
	foreach my $key_new (keys %key_new_hash ) {
		$self->{table_2}->delete_row( %{ $key_new_hash{$key_new} } );
		$request_number++;
	}
	undef %key_new_hash;
	
	# add missing lines
	my %key_delete_hash=$diff_object->get_source_only();
	foreach my $key_delete (keys %key_delete_hash) {
		$self->{table_2}->insert_row( %{ $key_delete_hash{$key_delete} } );
		$request_number++;
	}
	undef %key_delete_hash;
	
	# add new field
	my @key_new_field_hash=$diff_object->get_source_only_field();
	foreach my $new_field (@key_new_field_hash) {
		$self->{table_2}->add_field($new_field);
		$request_number++;
	}
	undef @key_new_field_hash;
	
	# update modified lines
	my @table_key=sort $self->key();
	my %key_update_hash=$diff_object->get_source_update();
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
		
		$self->{table_2}->update_row(%{ $key_update_hash{$key_update} });
		$request_number++;
	}
	$self->{table_2}->commit_transaction();
	$self->_debug("Les changements ont été appliqués ($request_number)");
	
	return $request_number;
}


=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut