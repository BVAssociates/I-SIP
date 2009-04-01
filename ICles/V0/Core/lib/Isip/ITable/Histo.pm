package Histo;


use ITable::abstract::DATA_interface;
@ISA = ("DATA_interface");

use ITable::Sqlite;

use Carp qw(carp cluck confess croak );
use Scalar::Util qw(blessed);
use strict;

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
		croak ('\'new\' take 2 mandatory argument: ${class}->open("databasename","tablename"[ ,{ timeout => $sec, debug => $num} ])')
	}
	# virtual informations
    my $database_name = shift;
	my $table_name = shift;
	my $options=shift;
	
	my $self = $class->SUPER::open($table_name, $options);
	
	$self->_info("Create new Histo object for : $table_name");
	
	# real informations (Aggregation)
	$self->{table_name_histo} = $self->{table_name}."_HISTO";
	$self->{table_name_histo_view} = $self->{table_name}."_HISTO";
	$self->{database_name} = $database_name;
	
	# Open real table on database
	$self->{table_histo} = Sqlite->open($self->{database_name}, $self->{table_name_histo}, $options);
	
	bless ($self, $class);
	
	$self->_debug("Virtual Fields : ", join("|",$self->field()));
	$self->_debug("Virtual Keys : ", join("|",$self->key()));
	$self->_debug("Virtual Not NULL : ", join("|",$self->not_null()));
	my %temp_hash=$self->size();
	$self->_debug("Virtual Size : ", join("|",values %temp_hash ));
	
	
	# user query
	$self->{query_field}  = [ $self->field() ];
	
	# fields that Histo is able to compute
	$self->{dynamic_field}  = [ "ICON" , "PROJECT" ];
	# add query option
	$self->{query_date} = $options->{date};
	$self->_debug("query date : ", join("|",$self->query_date())) if defined $self->{query_date};
	
	## internal members
	
	# constant timestamp when updating a row (otherwise locatime())
	$self->{update_timestamp}=undef;
	
	# instance temp values
	$self->{temp_next_row} = {};
	# flag for end of fetch_row
	$self->{end_of_data} = 0;
	
	# rules applied to dynamic fields
	$self->{isip_rules}= {};
	
	return $self;
}

##################################################
##  pivate methods  ##
##################################################



##################################################
##  public methods  ##
##################################################

# modifying primary key is now avaibable
# query_sort always by key
sub key {
    my $self = shift;
    if (@_) { 
		@{ $self->{key} } = @_ ;
		@{ $self->{query_sort} } = @_ ;
		$self->_debug("New Virtual Keys : ", join("|",@{$self->{key}}));
	}
    return @{ $self->{key} };
}

sub field {
    my $self = shift;
    if (@_) { 
		@{ $self->{field} } = @_ ;
		@{ $self->{query_field} } = @_ ;
		$self->_debug("New fields : ", join("|",@{$self->{field}}));
	}
    return @{ $self->{field} };
}

sub query_date {
    my $self = shift;
    if (@_) {
		my $datetime=shift;
		# ISO 8601 format : 1977-04-22T06:00
		if ( $datetime !~ /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}/) {
			$self->_error("datetime must be like 1977-04-22T06:00 (ISO 8601)");
			croak("usage : query_date(datetime)")
		}	
		$self->{query_date} = $datetime;
	}
    return $self->{query_date} ;
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

# force using a timestamp when update_row()
sub set_update_timestamp() {
    my $self = shift;
    
	my $timestamp=shift or croak ("usage : set_update_timestamp(timestamp)");
	
	# ISO 8601 format : 1977-04-22T06:00
	if ( $timestamp !~ /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}/) {
		$self->_error("timestamp must be like 1977-04-22T06:00 (ISO 8601)");
		croak("usage : set_update_timestamp(timestamp)")
	}
	
	$self->{update_timestamp}=$timestamp;
}


# special case of query_condition
# can use internal field TABLE_KEY to speed up queries
sub query_key_value() {
	my $self = shift;
    if (@_) { $self->{query_key_value} = shift }
    return $self->{query_key_value} ;
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


# set custom SQL query
sub custom_select_query()
{
	my $self = shift;
	
	croak("custom_select_query not implemented");

}

# Construct SQL query to get last inserted value for each field
sub get_query()
{
	my $self=shift;

	my $select_histo;
	my @select_conditions;
	
	my $provided_date_format = "%Y-%m-%dT%H:%M";
	
	push @select_conditions, "strftime('$provided_date_format',DATE_HISTO) <= '".$self->query_date()."'" if $self->query_date();
	
	my @field_key=$self->key();
	my %query_key;
	
	foreach my $condition ($self->query_condition()) {
		if ($condition =~ /^\s*(\w+)\s*([=]+|like)\s*\'(.*)\'\s*$/) {
			if ($1 eq "CATEGORY") {
				#Special case of CATEGORY filter
				if ($3 eq 'vide') {
					push @select_conditions, "TABLE_KEY_2 NOT IN (SELECT TABLE_KEY FROM ".$self->table_name."_CATEGORY WHERE CATEGORY IS NOT NULL )\n"
				} else {
					push @select_conditions, "TABLE_KEY_2 IN (SELECT TABLE_KEY FROM ".$self->table_name."_CATEGORY WHERE $condition )\n"
				}
			}
			else {
			# check if condition is on one of the keys
				if (grep {$1 eq $_} @field_key ) {
					$query_key{$1}=$3;
				}
				else {
					# else we use request on FIELD_NAME and FIELD_VALUE
					push @select_conditions, "TABLE_KEY IN (SELECT table_key FROM $self->{table_name_histo} where FIELD_NAME='$1' and FIELD_VALUE $2 '$3')";
				}
			}
		}
		else {
			croak ("something wrong with condition : $condition");
		}
	}

	#TODO : is it useful?
	push @select_conditions, "TABLE_KEY = '".$self->{query_key_value}."'" if $self->{query_key_value};
	
	if (%query_key) {
		# put joker on unknown keys
		foreach (@field_key) {
			$query_key{$_}='%' if not $query_key{$_};
		}
		push @select_conditions, "TABLE_KEY like '".join(',',@query_key{@field_key})."'" ;
	}
		
	## TO DISCUSS: we must get all field to know the status of whole line!
	my @query_conditions;
	#if ($self->query_field() ne ($self->field() + $self->dynamic_field())) {
	#	foreach ($self->query_field()) {
	#		push @query_conditions, "FIELD_NAME = '".$_."'";
	#	}
	#}
	#push @select_conditions, '('.join(' OR ',@query_conditions).')';
	
	my $distinct="";
	$distinct="DISTINCT" if $self->query_distinct;
	
	# SQL join to get last inserted KEY/NAME/VALUE
	$select_histo= "SELECT ".$distinct." ".join(',',$self->{table_histo}->query_field)."\n";
	$select_histo.= "FROM $self->{table_name_histo} as HISTO1\n";
	$select_histo.= "INNER JOIN (
	SELECT
	max(ID) as ID2,
	HISTO2.TABLE_KEY as TABLE_KEY_2,
	HISTO2.FIELD_NAME as FIELD_NAME_2,
	max(DATE_HISTO) AS DATE_MAX
	FROM
	$self->{table_name_histo_view} as HISTO2\n";
	
	# Add a condition
	$select_histo.= "	WHERE ".join(" AND ", @select_conditions)."\n" if @select_conditions;
	# GROUP BY
	$select_histo.= "	GROUP BY FIELD_NAME_2, TABLE_KEY_2\n";
	$select_histo.= "	) ON  (ID= ID2)\n";
	$select_histo.= "WHERE FIELD_VALUE != '__delete'\n";
	# FILTER FIELD_NAME
	$select_histo.= "	AND (".join(' OR ', @query_conditions).")\n" if @query_conditions;
	# ORDER
	
	$select_histo.= "ORDER BY HISTO1.TABLE_KEY ASC, FIELD_NAME DESC;";

	return $select_histo;
}

# get row one by one based on query
sub fetch_row() {
	my $self = shift;
	
	my %field_line;
	my %return_line;
	my $current_key;
	my @field_icon;
	my %field_project;
	
	# if last fetch was end of DATA, return empty line 
	if ($self->{end_of_data}) {
		$self->{temp_next_row} = {};
		$self->{end_of_data} = 0;
		$self->{table_histo}->custom_select_query (undef);
		return ();
	}

	#TO BE OPTIMIZED (should be called only once)
	$self->{table_histo}->query_field("ID","DATE_HISTO", "TABLE_KEY", "FIELD_NAME", "FIELD_VALUE","STATUS","PROJECT","COMMENT");
	$self->{table_histo}->custom_select_query ($self->get_query()) if not $self->{table_histo}->{custom_select_query};

	# store the higher field status
	#my $line_has_new=0;
	#my $line_not_valid=0;
	
	# if a temp_next_row exist from previous call, we add the FIELD_VALUE to the return hash
	if ( %{ $self->{temp_next_row} } ) {
		my %field_line= %{ $self->{temp_next_row} };
		
		# split FIELD_NAME when one row contains more than one real field
		if (grep {/,/} $field_line{FIELD_NAME}) {
			# case when one row contains several fields
			my @field_list=split(',',$field_line{FIELD_NAME});
			my @value_list=split(',',$field_line{FIELD_VALUE});
			
			# sanity check
			croak ("Incorrect value in base for ".$field_line{ID}) if @field_list ne @value_list;
			
			foreach my $field_name (@field_list) { 
				# add the FIELD_VALUE to the return hash
				$return_line{$field_name}=shift @value_list;
			}
			
		}
		else {
				$return_line{$field_line{FIELD_NAME}}=$field_line{FIELD_VALUE};
		}
		
		# line is modified if one field have no status
		if (blessed $self->{isip_rules} and not $self->{isip_rules}->is_field_hidden(%field_line)) {
			push @field_icon,$self->{isip_rules}->get_field_icon(%field_line);
		}
		$field_project{$field_line{PROJECT}}++ if $field_line{PROJECT};
		
		$current_key=$field_line{TABLE_KEY};
	}
	
	#return every row until TABLE_KEY change
	# ID,DATE_HISTO, TABLE_KEY, FIELD_NAME, FIELD_VALUE
	while (%field_line = $self->{table_histo}->fetch_row ) {
				
		# if no current key, it's a new row
		$current_key = $field_line{TABLE_KEY} if not defined $current_key;
		
		# if TABLE_KEY changed, we save the current line and exit
		if ( defined $current_key and $current_key ne $field_line{TABLE_KEY}) {
			$self->{temp_next_row} = { %field_line };
			last;
		}
		
		# split FIELD_NAME when one row contains more than one real field
		if (grep {/,/} $field_line{FIELD_NAME}) {
			# case when one row contains several fields
			my @field_list=split(',',$field_line{FIELD_NAME});
			my @value_list=split(',',$field_line{FIELD_VALUE});
			
			# sanity check
			croak ("FIELD_VALUE and FIELD_NAME does not match in HISTO for ID=".$field_line{ID}) if @field_list ne @value_list;
			
			foreach my $field_name (@field_list) { 
				# add the FIELD_VALUE to the return hash
				$return_line{$field_name}=shift @value_list;
			}
			
		}
		else {
				$return_line{$field_line{FIELD_NAME}}=$field_line{FIELD_VALUE};
		}
		
		# add status
		if (blessed $self->{isip_rules} and not $self->{isip_rules}->is_field_hidden(%field_line)) {
			push @field_icon,$self->{isip_rules}->get_field_icon(%field_line);
		}
		
		#add project
		$field_project{$field_line{PROJECT}}++ if $field_line{PROJECT};
		
	}
	
	$self->{end_of_data} = 1 if not %field_line;
	
	if ($self->{end_of_data} and not %return_line) {
		$self->{table_histo}->custom_select_query (undef);
		return () ;
	}
	
	my $missing_key;
	foreach ($self->key() ) {
		if (not exists $return_line{$_}) {
			$self->_error("field $_ cannot be null (should be one of : $field_line{TABLE_KEY})");
			$missing_key=1;
		}
	}
	#@return_line{$self->key()}=split(',',$current_key) if $missing_key;
	croak("unable to get primary key") if $missing_key;
	
	foreach ($self->not_null() ) {
		if (not exists $return_line{$_}) {
			$self->_debug("field $_ should not be null)");
		}
	}
	
	# add dynamic field if requested
	#TODO : use IsipRules
	if (grep (/^ICON$/, $self->query_field() )) {
		$return_line{ICON}=$self->{isip_rules}->get_line_icon(@field_icon) if blessed $self->{isip_rules};
	}
	
	if (grep (/^PROJECT$/, $self->query_field() )) {
		$return_line{PROJECT}=join(',',sort keys %field_project);
	}
	
	#debug
	##$return_line{ICON}=$line_has_new."/".$line_not_valid;
	
	# now the status has been computed, we remove unwanted field
	my %return_query_line;
	foreach ($self->query_field()) {
		$return_query_line{$_}=$return_line{$_} if exists $return_line{$_};
	}
	
	return %return_query_line;
}

sub fetch_row_array() {
	my $self = shift;
	
	#warn "WARNING : unable to find the colums order";
	my @return_line;
	
	my %hash_line = $self->fetch_row;
	
	
	return () if not %hash_line;
	return @hash_line{$self->query_field()};
}

sub begin_transaction() {
	my $self=shift;
	
	$self->{table_histo}->begin_transaction();
}

sub commit_transaction() {
	my $self=shift;
	
	$self->{table_histo}->commit_transaction();
}

sub is_empty() {
	my $self = shift;
	
	$self->{table_histo}->custom_select_query("select ID from ".$self->{table_histo}->table_name." LIMIT 1");
	
	my $bool=$self->{table_histo}->fetch_row_array();
	$self->{table_histo}->finish;
	
	$self->{table_histo}->custom_select_query (undef);
	
	return not $bool;
}

# Insert hash  as a rows (one rows per field)
sub insert_row() {
	my $self = shift;
		
	my (%row) = @_;
	my $transaction_running=0;
	
	#don't add dynamic field
	foreach ($self->dynamic_field) {
		delete $row{$_};
	}
	
	my $date_current;
	if ($self->{update_timestamp}) {
		$date_current = $self->{update_timestamp};
	}
	else {
		use POSIX qw(strftime);
		$date_current = strftime "%Y-%m-%dT%H:%M", localtime;
	}
	
	# activate transaction mode if not already done
	if ( $self->{table_histo}->active_transaction() > 0 ) {
		$transaction_running = 1 ;
	} else {
		$self->{table_histo}->begin_transaction();
	}
	
	# save key values
	my @key_value;
	foreach (sort $self->key()) {
		push @key_value, $row{$_};
		delete $row{$_};
	}
	# group keys on one field
	my $key_values=join(',',@key_value);
	$row{join(',',sort $self->key())}=$key_values;
	
	# quote the fields with the apropriate 
	foreach my $field (keys %row) {
		
		my $last_id=$self->{table_histo}->insert_row(
				"DATE_HISTO" => $date_current,
				"TABLE_NAME" => $self->table_name,
				"TABLE_KEY" => $key_values,
				"FIELD_NAME" => $field,
				"FIELD_VALUE" => $row{$field}
		);
		
		$self->_debug("Insert : $field ");

		
	}
	
	# commit this rows if no transaction was running before
	$self->{table_histo}->commit_transaction() if not $transaction_running;
	#$self->{table_histo}->{database_handle}->rollback();
	
	#return $last_id;
}

# update a row on a primary key
## very similar to insert_row()
sub update_row() {
	my $self = shift;
	
	my (%row) = @_;
	
	#don't add dynamic field
	foreach ($self->dynamic_field) {
		delete $row{$_};
	}
	
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
	
	my $date_current;
	if ($self->{update_timestamp}) {
		$date_current = $self->{update_timestamp};
	}
	else {
		use POSIX qw(strftime);
		$date_current = strftime "%Y-%m-%dT%H:%M", localtime;
	}
	
	# concat key values
	my @key_value;
	foreach (sort $self->key()) {
		push @key_value, $row{$_};
		
		# don't update key
		delete $row{$_};
	}
	
	foreach my $field (keys %row) {
		
		my $last_id=$self->{table_histo}->insert_row(
				"DATE_HISTO" => $date_current,
				"TABLE_NAME" => $self->table_name,
				"TABLE_KEY" => join(',',@key_value),
				"FIELD_NAME" => $field,
				"FIELD_VALUE" => $row{$field}
		);
		
		$self->_debug("Insert : $field ");
	}
}

# delete a row on a primary key
# ->in fact, only add "_delete" tag
sub delete_row() {
	my $self = shift;
	
	my (%row) = @_;
	
	#don't add dynamic field
	foreach ($self->dynamic_field) {
		delete $row{$_};
	}
	
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
	
	
	use POSIX qw(strftime);
	my $date_current = strftime "%Y-%m-%dT%H:%M", localtime;
	
	# concat key values
	my @key_value;
	foreach (sort $self->key()) {
		push @key_value, $row{$_};
		delete $row{$_};
	}
	# group keys on one field
	my $key_values=join(',',@key_value);
	$row{join(',',sort $self->key())}=$key_values;
	
	foreach my $field (keys %row) {
		
		my $last_id=$self->{table_histo}->insert_row(
				"DATE_HISTO" => $date_current,
				"TABLE_NAME" => $self->table_name,
				"TABLE_KEY" => $key_values,
				"FIELD_NAME" => $field,
				"FIELD_VALUE" => "__delete"
		);
		
		$self->_debug("Insert deleted : $field ");
	}
}

# update internal field to set whole row to VALIDE
sub validate_row_by_key() {
	my $self = shift;

	my $key=shift or croak ('validate_row take 1 argument : key');
	# update line STATUS to VALIDE
	
	# need to open to get a valid database_handle
	$self->{table_histo}->_open_database;
	$key=$self->{table_histo}->{database_handle}->quote($key);
	my $updated_value=$self->{table_histo}->{database_handle}->quote($self->{valid_keyword});
	my $date_update = $self->{table_histo}->{database_handle}->quote(strftime "%Y-%m-%dT%H:%M", localtime);
	my $user_update = $self->{table_histo}->{database_handle}->quote($ENV{ISIS_USER});
	
	my $update_sql="UPDATE ".$self->{table_histo}->table_name." SET STATUS=$updated_value, USER_UPDATE=$user_update, DATE_UPDATE=$date_update where TABLE_KEY=$key";
	$self->{table_histo}->execute($update_sql);
}

# add new field
sub add_field() {
	my $self = shift;
	
	my $field_name = shift or croak ('add_field take 1 argument : field_name');
	
	$self->_debug("Add field $field_name");
	
	# just add field name in field member
	push ( @{$self->{field}} , $field_name);
	push ( @{$self->{query_field}} , $field_name);
	
	#TODO : add field definition in TABLE_INFO
}

sub finish() {
	my $self=shift;
	
	# finish virtual statement
	$self->{temp_next_row} = {};
	$self->{end_of_data} = 0;
	
	# finish real statement
	$self->{table_histo}->finish();
}


=head1 NAME

 Isip::ITable::Histo - Computed ITable::DATA_interface
 
=head1 SYNOPSIS

Like Sqlite, Histo get lines from a table TABLE, using the last entries from TABLE_HISTO.

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

=cut
