package DataDiff;


use ITable::abstract::DATA_interface;
@ISA = ("DATA_interface");

use Carp qw(carp cluck confess croak );
use strict;
use Scalar::Util qw(blessed);

use Isip::IsipDiff;
use Isip::IsipRulesDiff;

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

	$self->{table_source}=$data_ref1;
	$self->{table_target}=$data_ref2;
	
	# member where whole table will be saved while compare
	$self->{in_memory_table}={};
	
	# this object will store differences
	$self->{diff}={};
	
	#this object will store display rules
	$self->{isip_rules}={};
		
	# static value
	$self->{current_target_only_key}=[];
	$self->{fetch_target_only_running}=0;
	
	# var to affect behavior of compare_next
	$self->{compare_fetch}=0;
	$self->{compare_diff}=0;
	
	# We bless the object with the new class
	bless ($self, $class);
	
	# default query
	$self->{field}  = [ $self->{table_target}->field() ];
	$self->{query_field}  = [ $self->{table_target}->query_field() ];
	$self->{dynamic_field}  = [ "DIFF",$self->{table_target}->dynamic_field() ];
	
	$self->_debug("initialisation");
	
    return $self;
}

##################################################
##  public methods delegated to the target table ##
##################################################


sub table_name {
    my $self = shift;
    if (@_) { croak("'table_name' member is read-only") }
    return $self->{table_target}->{table_name};
}

sub field {
    my $self = shift;
    if (@_) { croak("'field' member is read-only") }
    return @{ $self->{table_target}->{field} };
}

sub field_txt {
    my $self = shift;
    if (@_) { croak("'field_txt' member is read-only") }
    return %{ $self->{table_target}->{field_txt} };
}

sub field_desc {
    my $self = shift;
    if (@_) { croak("'field_desc' member is read-only") }
    return %{ $self->{table_target}->{field_desc} };
}

sub key {
    my $self = shift;
    if (@_) { croak("'key' member is read-only") }
    return @{ $self->{table_target}->{key} };
}

sub not_null {
    my $self = shift;
    if (@_) { croak("'not_null' member is read-only") }
    return @{ $self->{table_target}->{not_null} };
}


sub size {
    my $self = shift;
    if (@_) { croak("'size' member is read-only") }
    return %{ $self->{table_target}->{size} };
}

sub isip_rules() {
	my $self = shift;
	
	my $isip_rules_ref;
	if (@_) {
		$isip_rules_ref = shift;
		croak("arg1 of isip_rules must be a object ref") if not blessed $isip_rules_ref;
		$self->{isip_rules}=$isip_rules_ref;
	}
    return $self->{isip_rules} ;
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


sub fetch_row() {
	my $self=shift;

	croak(__PACKAGE__."->compare_init() must be called before ".__PACKAGE__."->fetch_row()") if not blessed $self->{diff};
	
	$self->{compare_fetch}=1;
	
	# first, printing lines only in source
	my $current_row=$self->compare_next();
	
	# table_target return no lines, so we return
	if (not $current_row) {
		$self->{compare_fetch}=0;
		return ();
	}
	
	# initialize dynamic fields
	foreach ($self->dynamic_field) {
		$current_row->{$_}="" if not exists $current_row->{$_};
	}
	
	# compute internal dynamic fields
	if (grep {/^ICON$/} $self->query_field() ) {
	
		if (not blessed $self->{isip_rules}) {
			carp ("no IsipRules set");
			$current_row->{ICON}="isip_error";
		}
		else {
		
			my %line_diff_icon=$self->{isip_rules}->enum_line_icon();
			my $return_status="ERROR";
			my $key=join(',',@$current_row{sort $self->key()});
			
			my @diff_list;
			
			# compute icon field by field
			foreach my $field (keys %$current_row) {
				#dont check excluded fields
				if (not grep {$field eq $_} $self->compare_exclude()
						and not $self->{isip_rules}->is_field_hidden(FIELD_NAME => $field) )
				{
					push @diff_list,$self->{isip_rules}->get_field_icon(FIELD_NAME => $field ,FIELD_VALUE => $current_row->{$field}, DIFF => $current_row->{DIFF});
				}
			}
			
			# compute icon of whole line
			$return_status=$self->{isip_rules}->get_line_icon(@diff_list);
			
			$current_row->{ICON}=$return_status;
		}
	}
	
	my %query_field_row;
	@query_field_row{$self->query_field} = @$current_row{$self->query_field};
	
	return %query_field_row;
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
			@{ $self->{diff_exclude} } =  @fields;
		}
		
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

# this sub go thourgh the 2 tables and compute an IsipDiff object
sub compare() {
	my $self=shift;
	
	$self->compare_init();
	
	$self->{compare_diff}=1;
	
	while ($self->compare_next()) {
		#nothing to do
	}
	
	$self->{compare_diff}=0;
	
	return $self->{diff};
}

# init Iterator by slurping first table in memory
# can be memory hungry !
sub compare_init() {
	my $self=shift;
	
	my $table_from = $self->{table_source};
	my $table_to = $self->{table_target};
	my @key;

	# store the result in a IsipDiff object
	$self->{diff}=IsipDiff->new();
	
	if (not $table_to->key()) {
		croak("No key defined");
	}
	if ( $table_to->key() ne  $table_from->key()) {
		croak("The 2 tables have not the same keys : ".$table_to->key()." => ".$table_from->key());
	}
	
	if ( $table_to->table_name() ne $table_from->table_name() ) {
		carp("The 2 tables have not the same name : ".$table_to->table_name()." => ".$table_from->table_name());
	}
	
	#if ( join(',',sort $self->field()) ne  join(',',sort $table_from->field())) {
	#	croak("The 2 tables have not the same fields");
	#}
	
	foreach my $key_field ($table_to->key()) {
		croak("query fields does not contains the key fields") if not grep (/^$key_field$/, $table_to->query_field() );
	}
	
	@key=$self->key();
	
	# preallocate hash buckets
	#keys(%{$self->{in_memory_table}})=100000;
	
	# Slurp the first table in memory
	my %row;
	while (%row=$table_to->fetch_row()) {
		$self->{in_memory_table}->{join(',',@row{@key})}={ %row };
	}
	
	$self->{fetch_target_only_running}=0;
	
	$self->_debug("PERF: buckets : ", scalar(%{$self->{in_memory_table}}));
}

sub compare_next() {
	my $self=shift;
	
	my $table_from=$self->{table_source};
	
	my @key=sort $table_from->key();
	
	# get a row 
	my %row_source=$table_from->fetch_row() if not $self->{fetch_target_only_running};
	my $row_target_ref;
	
	if (not %row_source) {
		# no more data in source
		
		$self->{fetch_target_only_running}=1;
		
		if (%{$self->{in_memory_table}}) {
			# data remaining in target
			
			if (not @{$self->{current_target_only_key}}) {
				# compute remaining keys if not already done
				$self->{current_target_only_key}=[ sort keys %{$self->{in_memory_table}} ];
			}
			
			my $current_keys=shift @{$self->{current_target_only_key}};
			
			if( not defined $current_keys) {
				# no more key to return
				return undef;
			}
			else {
				return $self->dispatch_target_only($current_keys,delete $self->{in_memory_table}->{$current_keys});
			}
		}
		else {
			#no data in source neither target
			return undef;
		}
	}
	else {
		my $current_keys=join(',',@row_source{@key});
		$row_target_ref=delete $self->{in_memory_table}->{$current_keys};

		if (not $row_target_ref) {
			# key exists only in source
			return $self->dispatch_source_only($current_keys,\%row_source);
		}
		else {
			# key exists in the 2 tables
			my %row_target=%{ $row_target_ref };
			
			my %all_fields;
			foreach my $field (keys %row_source,keys %row_target) {
				$all_fields{$field}++;
			}
			
			foreach my $field1 (keys %all_fields) {
				
				if (grep(/^$field1$/, $self->compare_exclude)) {
					#field excluded from compare, aka equal
					delete $row_source{$field1};
					next;
				}
				
				if (not exists $row_source{$field1}) {
					$self->dispatch_target_only_field($current_keys,$field1);
				} elsif (not exists $row_target{$field1}) {
					$row_target{$field1}='__delete';
					$self->dispatch_source_only_field($current_keys,$field1);
				} elsif ($row_source{$field1} eq $row_target{$field1}) {
					# field are the same on the 2 rows, we keep only one
					delete $row_source{$field1};
				}
			}
			
			if (%row_source) {
				return $self->dispatch_source_update($current_keys,\%row_source,\%row_target);
			}
			else {
				return $self->dispatch_equal($current_keys,\%row_target);
			}
		}
	}
	
	die "Oops, you should not be here!";
}

##########################
# Dispatcher methods to trigger actions
##########################

sub dispatch_equal() {
	my $self=shift;
	
	my $current_key=shift;
	my $target_row=shift or croak("usage:dispatch_source_update(current_key,field,target_row)");
	
	if ($self->{compare_diff}) {
		# nothing to do
	}
	
	if ($self->{compare_fetch}) {
		$target_row->{DIFF}="OK";
		return $target_row;
	}
	
	return 1;
}

sub dispatch_source_update() {
	my $self=shift;
	
	my $current_key=shift;
	my $source_row=shift;
	my $target_row=shift or croak("usage:dispatch_source_update(current_key,field,source_row)");

	$self->_debug("Line updated : Key (".$current_key.") : ".join(",",values %{$source_row}));
	
	if ($self->{compare_diff}) {
		while (my %field=each %$source_row) {
			$self->{diff}->add_source_update($current_key,%field);
		}
	}
	
	if ($self->{compare_fetch}) {
		#my %new_row=%$target_row;
		#@new_row{keys %$source_row}=values %$source_row;
		$target_row->{DIFF}="UPDATE";
		return $target_row;
	}
	
	return 1;
}

sub dispatch_target_only() {
	my $self=shift;
	
	my $current_key=shift;
	my $target_row=shift or croak("usage:dispatch_target_only(current_key,target_row)");

	$self->_debug("Line only in target : Key (".$current_key.")");
	
	if ($self->{compare_diff}) {
		$self->{diff}->add_target_only($current_key,$target_row);
	}
	
	if ($self->{compare_fetch}) {
		$target_row->{DIFF}="NEW";
		return $target_row;
	}
	
	return 1;
}

sub dispatch_source_only() {
	my $self=shift;
	
	my $current_key=shift;
	my $source_row=shift or croak("usage:dispatch_source_only(current_key,source_row)");

	$self->_debug("Line only in source : Key (".$current_key.")");
	
	if ($self->{compare_diff}) {
		$self->{diff}->add_source_only($current_key,$source_row);
	}
	
	if ($self->{compare_fetch}) {
		$source_row->{DIFF}="DELETE";
		return $source_row;
	}
	
	return 1;
}

sub dispatch_source_only_field() {
	my $self=shift;

	my $current_key=shift;
	my $field=shift;
	my $field_value=shift or croak("usage:dispatch_source_update(current_key,field,field_value)");
	
	$self->_debug("Column only in source : Key (".$current_key.") $field : $field_value");
	
	if ($self->{compare_diff}) {
		$self->{diff}->add_source_only_field($current_key,$field);
	}
	
}

sub dispatch_target_only_field() {
	my $self=shift;
	
	my $current_key=shift;
	my $field=shift or croak("usage:dispatch_target_only_field(current_key,field)");
	
	$self->_debug("Column only in target : Key (".$current_key.") $field");
	
	if ($self->{compare_diff}) {
		$self->{diff}->add_target_only_field($current_key,$field);
	}
	
}


# apply the data in a IsipDiff oject in the table
sub update_compare_target() {
	my $self = shift;
	
	$self->compare() if not defined $self->{diff};
	
	my $diff_object = $self->{diff};
	
	my $request_number=0;
	$self->{table_target}->begin_transaction();
	
	# remove lines only in source
	my %key_new_hash=$diff_object->get_target_only();
	foreach my $key_new (keys %key_new_hash ) {
		$self->{table_target}->delete_row( %{ $key_new_hash{$key_new} } );
		$request_number++;
	}
	undef %key_new_hash;
	
	# add missing lines
	my %key_delete_hash=$diff_object->get_source_only();
	foreach my $key_delete (keys %key_delete_hash) {
		$self->{table_target}->insert_row( %{ $key_delete_hash{$key_delete} } );
		$request_number++;
	}
	undef %key_delete_hash;
	
	# add new field on line
	my @key_new_field_hash=$diff_object->get_source_only_field();
	foreach my $new_field (@key_new_field_hash) {
		$self->{table_target}->add_field($new_field);
		$request_number++;
	}
	undef @key_new_field_hash;
	
	# remove missing field on line
	my @key_delete_field_hash=$diff_object->get_target_only_field();
	foreach my $delete_field (@key_delete_field_hash) {
		$self->{table_target}->remove_field($delete_field);
		$request_number++;
	}
	undef @key_delete_field_hash;
	
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
		
		$self->{table_target}->update_row(%{ $key_update_hash{$key_update} });
		$request_number++;
	}
	$self->{table_target}->commit_transaction();
	$self->_debug("Les changements ont été appliqués ($request_number)");
	
	return $request_number;
}


=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut