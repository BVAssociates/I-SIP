package Histo;

require IKOS::DATA::Sqlite;
@ISA = ("Sqlite");

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
	
		# mandatory parameter
	if (@_ < 2) {
		croak ('\'new\' take 2 mandatory argument: ${class}->open("databasename","tablename"[ ,{ timeout => $sec, debug => $num} ])')
	}
	my $database_name = shift;
	my $table_name = shift;
	my $options=shift;
	
	# open "Histo" table instead of "Real" table
	my $self = $class->SUPER::open($database_name, $table_name."_HISTO", $options);
	
	# Reconsecrate
	bless ($self, $class);
	
	# real informations
	$self->{table_name_real} = $self->{table_name};
	$self->{field_real} = $self->{field};
	$self->{key_real} = $self->{key};
	
	# set virtual informations
	$self->{database_name} = $database_name;
	$self->{table_name} = $table_name;
	$self->_open_database();
	$self->_set_columns_info_histo();
	$self->_close_database();
	
	# add query option
	$self->{query_date} = undef;
	
	# instance temp values
	$self->{temp_next_row} = {};
	

	
    return $self;
}

##################################################
##  pivate methods  ##
##################################################

# Get information from database
# Need "$self->{database_handle}" to be connected !
sub _set_columns_info_histo() {
	my $self = shift;
	
	croak("$self->{database_name} need to be opened before execute _set_columns_info") if not defined $self->{database_handle};
	
	# get histo informations
	my $table_histo=$self->{database_handle}->prepare("select distinct FIELD_NAME from $self->{table_name_real}");
	$self->_debug("Get virtual columns info for $self->{table_name} in the HISTO table");
	$table_histo->execute();
	
	while (my ($column_name)=$table_histo->fetchrow_array) {
		push (@{$self->{field}}, $column_name);
	}
	
	if (not $self->field()) {
		croak("Error reading information of table : $self->{table_name}");
	}
}

##################################################
##  public methods  ##
##################################################

sub key {
    my $self = shift;
    if (@_) { @{ $self->{key} } = @_ }
    return @{ $self->{key} };
}

sub query_date {
    my $self = shift;
    if (@_) { $self->{query_date} = shift }
    return $self->{query_date} ;
}

# set custom SQL query
sub custom_select_query()
{
	my $self = shift;
	
	croak("custom_select_query not implemented");

}

# Create an SQL query
sub get_query()
{
	my $self = shift;
	
	my $select_histo;
	my @select_conditions;
	
	return "" if not $self->query_field();
	
	push @select_conditions, "DATE_HISTO = '".$self->query_date()."'" if $self->query_date();
	push @select_conditions, "FIELD_NAME = '".$self->query_field()."'" if $self->query_field() eq $self->field();
	
	# SQL join to get last inserted KEY/NAME/VALUE
	## INNER or OUTER ??
	$select_histo= "SELECT ID,DATE_HISTO, TABLE_KEY, FIELD_NAME, FIELD_VALUE
		FROM
		$self->{table_name_real} INNER JOIN (
			SELECT
			TABLE_KEY as TABLE_KEY_2,
			FIELD_NAME as FIELD_NAME_2,
			max(DATE_HISTO) AS DATE_MAX
			FROM
			PROTYPP_HISTO";
	
	# Add a condition
	$select_histo.= " WHERE ".join(" AND ", @select_conditions) if @select_conditions;
	# GROUP BY
	$select_histo.= " GROUP BY FIELD_NAME_2, TABLE_KEY_2)
		ON  (TABLE_KEY = TABLE_KEY_2) AND (FIELD_NAME = FIELD_NAME_2) AND (DATE_HISTO = DATE_MAX)
		ORDER BY TABLE_KEY;";

	return $select_histo;
}

# get row  by one based on query
sub fetch_row() {
	my $self = shift;
	
	my %return_line;
	my $current_key;

	# connect to database and execute the query
	if (not defined $self->{database_statement} ) {
		# new request, clear the temp_next_row;
		$self->{temp_next_row} = {};
		$self->_execute_select_query() ;
	}
	
	# if a temp_next_row exist from previous call, we add the FIELD_VALUE to the return hash
	if ( %{ $self->{temp_next_row} } ) {
		my %temp_next_row= %{ $self->{temp_next_row} };
		$return_line{$temp_next_row{FIELD_NAME}}=$temp_next_row{FIELD_VALUE};
		$current_key=$temp_next_row{TABLE_KEY};
	}
	
	#return every row until TABLE_KEY change
	# ID,DATE_HISTO, TABLE_KEY, FIELD_NAME, FIELD_VALUE
	while (my $field_line = $self->{database_statement}->fetchrow_hashref() ) {
		
		# add the FIELD_VALUE to the return hash
		$return_line{$field_line->{FIELD_NAME} } = $field_line->{FIELD_VALUE} ;
		
		# if TABLE_KEY changed, we save the current line and exit
		if ( $current_key and $current_key != $field_line->{TABLE_KEY}) {
			$self->{temp_next_row} = $field_line;
			last;
		}	
	}
	
	
	
	$self->_close_database() unless %return_line;
	return %return_line;
}

sub fetch_row_array() {
	my $self = shift;
	
	warn "WARNING : unable to find the colums order";
	
	my %return_line = $self->fetch_row;
	return values %return_line;
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
	
	$self->_begin_work();
	
	# quote the fields with the apropriate 
	foreach my $key (keys %row) {
		$row{$key} = $self->{database_handle}->quote($row{$key});
	}
	my $insert_query = sprintf("INSERT INTO %s (%s) VALUES (%s);",$self->{table_name},join(',',keys %row),join(',', values %row));
	
	# prepare query
	$self->_debug('Prepare SQL : ',$insert_query);
	$self->{database_statement}=$self->{database_handle}->prepare( $insert_query );
		
	# execute query
	$self->_debug('Execute SQL');
	$self->{database_statement}->execute();
	
	$self->_commit();
	
	my $last_id = $self->{database_handle}->last_insert_id('', '', $self->{table_name});
	
	$self->_close_database();
	
	return $last_id;
}
