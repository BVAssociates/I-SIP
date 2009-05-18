package HistoBaseline;


use Isip::ITable::Histo;
@ISA = ("Histo");


use Carp qw(carp cluck confess croak );
use Scalar::Util qw(blessed);
use strict;



##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;	
	
	# mandatory parameter
	if (@_ < 3) {
		croak ('\'new\' take 2 mandatory argument: ${class}->open("databasename","tablename"[ ,{ timeout => $sec, debug => $num} ])')
	}
	# virtual informations
    my $database_name = shift;
	my $table_name = shift;
	my $baseline_date = shift;
	my $options=shift;
	
	# Call Histo constructor
	my $self = $class->SUPER::open($database_name,$table_name, $options);
	
	# set Histo date
	$self->SUPER::query_date($baseline_date);
	
	# sanitize date format
	$self->{table_name_histo} = $self->get_baseline_name($table_name,$baseline_date);
	
	$self->_info("Open baseline for $table_name at ".$self->{query_date});
	
	return $self;
}

##################################################
##  static methods  ##
##################################################

sub get_baseline_name() {
	my $self=shift;
	
	my $table_name=shift;
	my $baseline_date=shift or croak("usage : get_baseline_name(baseline_date)");
	
	$baseline_date =~ tr/-://d;
	return $table_name."_".$baseline_date;
}

##################################################
##  public methods  ##
##################################################



# @OVERRIDE
sub query_date {
    my $self = shift;
    if (@_) {
		croak("Unable to set query_date on a baseline")
	}
    return $self->{query_date} ;
}



# Construct SQL query to get last inserted value for each field
# @OVERRIDE
sub get_query() {

	my $self = shift;
		
	my $select_histo;
	my @select_conditions;
	my @query_conditions;
	
	my @field_key=$self->key();
	my %query_key;
	
	foreach my $condition ($self->query_condition()) {
		if ($condition =~ /^\s*(\w+)\s*([=]+|like)\s*\'(.*)\'\s*$/) {
			if ($1 eq "CATEGORY") {
				#Special case of CATEGORY filter
				if ($3 eq 'vide') {
					push @select_conditions, "TABLE_KEY NOT IN (SELECT TABLE_KEY FROM ".$self->table_name."_CATEGORY WHERE CATEGORY IS NOT NULL )\n"
				} else {
					push @select_conditions, "TABLE_KEY IN (SELECT TABLE_KEY FROM ".$self->table_name."_CATEGORY WHERE $condition )\n"
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
	push @select_conditions, "TABLE_KEY = '".join(',',$self->query_key_value())."'" if $self->query_key_value();
	
	if (%query_key) {
		# put joker on unknown keys
		foreach (@field_key) {
			$query_key{$_}='%' if not $query_key{$_};
		}
		push @select_conditions, "TABLE_KEY like '".join(',',@query_key{@field_key})."'" ;
	}
		
	## TO DISCUSS: we must get all field to know the status of whole line!
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
	
	# Add a condition
	$select_histo.= "	WHERE ".join(" AND ", @select_conditions)."\n" if @select_conditions;
	# FILTER FIELD_NAME
	$select_histo.= "	AND (".join(' OR ', @query_conditions).")\n" if @query_conditions;
	# ORDER
	
	$select_histo.= "ORDER BY HISTO1.TABLE_KEY ASC, FIELD_NAME DESC;";

	return $select_histo;
}


# Insert hash  as a rows (one rows per field)
# @OVERRIDE
sub insert_row() {
	my $self = shift;
		
	croak("unable to insert a row in a baseline");
}


# delete a row on a primary key
# ->in fact, only add "_delete" tag
sub delete_row() {
	my $self = shift;
		
	croak("unable to delete a row in a baseline");
}
	


# add new field
sub add_field() {
	my $self = shift;
		
	croak("unable to add a field in a baseline");
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

 Isip::ITable::HistoBaseline - Special Histo table
 
=head1 SYNOPSIS

Like Sqlite, Histo get lines from a table TABLE, using the last entries from TABLE_HISTO.

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
