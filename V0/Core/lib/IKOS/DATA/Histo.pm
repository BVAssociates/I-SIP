package Histo;


require IKOS::DATA::DATA_interface;
@ISA = ("DATA_interface");

use IKOS::DATA::Sqlite;

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
	# virtual informations
    my $database_name = shift;
	my $table_name = shift;
	my $options=shift;
	
	my $self = $class->SUPER::open($table_name, $options);
	
	# real informations (Aggregation)
	$self->{table_name_histo} = $self->{table_name}."_HISTO";
	$self->{database_name} = $database_name;
	
	## We suppose TABLE and TABLE_HISTO are in the same database  /!\
	$self->{table_histo} = Sqlite->open($self->{database_name}, $self->{table_name_histo}, $options);
	
	bless ($self, $class);
	
	$self->_set_columns_info_histo();
	$self->_debug("Virtual Fields : ", join("|",$self->field()));
	$self->_debug("Virtual Keys : ", join("|",$self->key()));
	$self->_debug("Virtual Not NULL : ", join("|",$self->not_null()));
	my %temp_hash=$self->size();
	$self->_debug("Virtual Size : ", join("|",values %temp_hash ));
	
	
	# user query
	$self->{query_field}  = [ $self->field() ];
	# add query option
	$self->{query_date} = $options->{date};
	$self->_debug("query date : ", join("|",$self->query_date())) if defined $self->{query_date};
	# instance temp values
	$self->{temp_next_row} = {};
	
	# flag for end of fetch_row
	$self->{end_of_data} = 0;
	

	
    return $self;
}

##################################################
##  pivate methods  ##
##################################################

# Get information from database
# Need "$self->{database_handle}" to be connected !
sub _set_columns_info_histo() {
	my $self = shift;

=begin comment : how to know "real" key name ??
	#get Real informations for fields
	my $table_info=$self->{database_handle}->prepare("PRAGMA table_info($self->{table_name})");
	$self->_debug("Get columns info for $self->{table_name_real}");
	$table_info->execute();
	
	while (my @col=$table_info->fetchrow_array) {
		push (@{$self->{field_real}},       $col[1]);
		$self->{size}->{$col[1]}=       $col[2];
		push (@{$self->{not_null}},     $col[1]) if $col[3];
		push (@{$self->{key_real}},          $col[1]) if $col[5];
	}
	
	if (not @{$self->{field_real} }) {
		croak("Error reading information of table : $self->{table_name}");
	}
=begin comment
=cut
	
	# get histo informations
	$self->_debug("Get virtual columns info for $self->{table_name} in the HISTO table");
	$self->{table_histo}->custom_select_query("select distinct FIELD_NAME from $self->{table_name_histo}");
	
	while (my ($column_name)=$self->{table_histo}->fetch_row_array) {
		push (@{$self->{field}}, $column_name);
	}
	
	#$self->{table_histo}->finish();
	
	if (not $self->field()) {
		warn "This table contains 0 field"
	}
}

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

sub query_date {
    my $self = shift;
    if (@_) { $self->{query_date} = shift }
    return $self->{query_date} ;
}

sub query_sort {
    my $self = shift;
    if (@_) { croak("query_sort is read-only"); }
    return @{ $self->{query_sort} };
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
	my $self = shift;
	
	my $select_histo;
	my @select_conditions;
	
	
	push @select_conditions, "DATE_HISTO = '".$self->query_date()."'" if $self->query_date();
	#push @select_conditions, "FIELD_NAME = '".$self->query_field()."'" if $self->query_field() == $self->field();
	
	# SQL join to get last inserted KEY/NAME/VALUE
	## INNER or OUTER ??
	$select_histo= "SELECT ID,DATE_HISTO, TABLE_KEY, FIELD_NAME, FIELD_VALUE
		FROM
		$self->{table_name_histo} INNER JOIN (
			SELECT
			TABLE_KEY as TABLE_KEY_2,
			FIELD_NAME as FIELD_NAME_2,
			max(DATE_HISTO) AS DATE_MAX
			FROM
			$self->{table_name_histo}";
	
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
	
	my %field_line;
	my %return_line;
	my $current_key;
	
	# if last fetch was end of DATA, return empty line 
	if ($self->{end_of_data}) {
		$self->{temp_next_row} = {};
		$self->{end_of_data} = 0;
		return ();
	}
	
	$self->{table_histo}->custom_select_query ($self->get_query() );

	# if a temp_next_row exist from previous call, we add the FIELD_VALUE to the return hash
	if ( %{ $self->{temp_next_row} } ) {
		my %temp_next_row= %{ $self->{temp_next_row} };
		$return_line{$temp_next_row{FIELD_NAME}}=$temp_next_row{FIELD_VALUE};
		$current_key=$temp_next_row{TABLE_KEY};
	}
	
	$self->{table_histo}->query_field("ID","DATE_HISTO", "TABLE_KEY", "FIELD_NAME", "FIELD_VALUE");
	
	#return every row until TABLE_KEY change
	# ID,DATE_HISTO, TABLE_KEY, FIELD_NAME, FIELD_VALUE
	while (%field_line = $self->{table_histo}->fetch_row ) {

		$current_key = $field_line{TABLE_KEY} if not defined $current_key;
		
		# add the FIELD_VALUE to the return hash
		$return_line{$field_line{FIELD_NAME} } = $field_line{FIELD_VALUE} ;
		
		# if TABLE_KEY changed, we save the current line and exit
		if ( defined $current_key and $current_key ne $field_line{TABLE_KEY}) {
			$self->{temp_next_row} = { %field_line };
			last;
		}	
	}
	
	$self->{end_of_data} = 1 if not %field_line;
	
	return %return_line;
}

sub fetch_row_array() {
	my $self = shift;
	
	#warn "WARNING : unable to find the colums order";
	my @return_line;
	
	my %hash_line = $self->fetch_row;
	
	return () if not %hash_line;
	
	foreach my $field_name ($self->query_field() ) {
		push @return_line, $hash_line{$field_name};
	}
	
	return @return_line;
}

sub begin_transaction() {
	my $self=shift;
	
	$self->{table_histo}->begin_transaction();
}

sub commit_transaction() {
	my $self=shift;
	
	$self->{table_histo}->commit_transaction();
}

# Insert hash  as a rows (one rows per field)
sub insert_row() {
	my $self = shift;
		
	my (%row) = @_;
	my $transaction_running=0;
	
	use POSIX qw(strftime);
	my $date_current = strftime "%Y-%m-%d %H:%M:%S", localtime;
	
	# active transaction mode if not already done
	if ( $self->{table_histo}->active_transaction() > 0 ) {
		$transaction_running = 1 ;
	} else {
		$self->{table_histo}->begin_transaction();
	}
	
	# quote the fields with the apropriate 
	foreach my $field (keys %row) {
		
	
		my $last_id=$self->{table_histo}->insert_row(
				"DATE_HISTO" => $date_current,
				"TABLE_NAME" => $self->table_name,
				"TABLE_KEY" => $row{join(',',$self->key())},
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

sub finish() {
	my $self=shift;
	
	# finish virtual statement
	$self->{temp_next_row} = {};
	$self->{end_of_data} = 0;
	
	# finish real statement
	$self->{table_histo}->finish();
}

=head1 NAME

 IKOS::DATA::Histo - Computed IKOS::DATA::DATA_interface
 
=head1 SYNOPSIS

Like Sqlite, Histo get lines from a table TABLE, using the last entries from TABLE_HISTO.
