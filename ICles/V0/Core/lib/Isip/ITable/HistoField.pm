package HistoField;

require ITable::Sqlite;
@ISA = ("Sqlite");

use Carp qw(carp cluck confess croak );
use strict;

use Isip::IsipRules;
use Scalar::Util qw(blessed);
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
		croak ('\'open\' take 2 mandatory argument: ${class}->open("databasename","tablename"[ ,{ timeout => $sec, debug => $num} ])')
	}
	# virtual informations
    my $database_name = shift;
	my $table_name = shift;
	my $options=shift;
		
	my $self  = $class->SUPER::open($database_name, $table_name."_HISTO", $options);
	
	$self->{dynamic_field}= [ "TYPE", "TEXT","ICON"];
	$self->{query_field}  = [ $self->field() ];
	$self->{query_date}=$options->{date};
	
	# force primary key
	$self->{key}=["FIELD_NAME","TABLE_KEY"];
	
	# get object handling columns
	$self->{column_histo} = HistoColumns->new($self->{database_name}, $table_name, $options);
	
	$self->{query_key_value}=[];
	$self->{isip_rules} = {};
	
	$self->{meta_filter}= [];
	
	return bless($self,$class);
}


sub query_key_value() {
	my $self = shift;
    if (@_) { @{$self->{query_key_value}} = @_ }
    return @{$self->{query_key_value}};
}

sub query_date {
    my $self = shift;
    if (@_) {
		my $datetime=shift;
		# ISO 8601 format : 1977-04-22T06:00 or 19770422T0600
		if ( $datetime !~ /\d{4}-?\d{2}-?\d{2}T\d{2}:?\d{2}/) {
			$self->_error("datetime must be like 1977-04-22T06:00 or 19770422T0600 (ISO 8601)");
			croak("usage : query_date(datetime)")
		}
		
		# reformat date
		$datetime =~ s/(\d{4})-?(\d{2})-?(\d{2})T(\d{2}):?(\d{2})/$1-$2-$3T$4:$5/;
		
		# set date
		$self->{query_date} = $datetime;

		# set date for HistoColums object
		$self->{column_histo}->query_date($datetime);
	}
    return $self->{query_date} ;
}

sub metadata_condition() {
	my $self = shift;
	
	if (@_) {
		$self->{meta_filter} = [@_];
	}
    return @{$self->{meta_filter}} ;
}

# Construct SQL query to get last inserted value for each field
sub get_query()
{
	my $self = shift;
	
	my $select_histo;
	my @select_conditions;
	
	push @select_conditions, "FIELD_NAME IN (".join(',',map {"'".$_."'"} $self->{column_histo}->get_field_list()).")";
	
	my $date_format = "%Y-%m-%dT%H:%M";
	push @select_conditions, "strftime('$date_format',DATE_HISTO) <= '".$self->query_date()."'" if $self->query_date();

	my @table_key_list=$self->query_key_value();
	push @select_conditions, "TABLE_KEY IN (".join (',',map {'\''.$_.'\''} @table_key_list).")" if @table_key_list;
	
	push @select_conditions, $self->query_condition() if $self->query_condition;
	
	my @real_query_field;
	foreach my $field_condition ($self->query_field()) {
		push @real_query_field, $field_condition if not grep ($field_condition eq $_, $self->dynamic_field());
	}
	
	
	# SQL join to get last inserted KEY/NAME/VALUE
	## INNER or OUTER ??
	$select_histo= "SELECT ".join(',',@real_query_field)." FROM
		$self->{table_name} INNER JOIN (
			SELECT
			TABLE_KEY as TABLE_KEY_2,
			FIELD_NAME as FIELD_NAME_2,
			max(DATE_HISTO) AS DATE_MAX
			FROM
			$self->{table_name}";
	
	# Add a condition
	$select_histo.= " WHERE ".join(" AND ", @select_conditions) if @select_conditions;
	# GROUP BY
	$select_histo.= " GROUP BY FIELD_NAME_2, TABLE_KEY_2";
	# HAVING metadata
	$select_histo.= " HAVING ".join(" AND ", @{$self->{meta_filter}}) if @{$self->{meta_filter}};
	
	$select_histo.= ")
		ON  (TABLE_KEY = TABLE_KEY_2) AND (FIELD_NAME = FIELD_NAME_2) AND (DATE_HISTO = DATE_MAX)
		WHERE FIELD_VALUE != '__delete'
		ORDER BY TABLE_KEY;";

	return $select_histo;
}

 
 
=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
