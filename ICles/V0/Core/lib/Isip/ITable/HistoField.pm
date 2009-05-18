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
	
	$self->dynamic_field("TYPE", "TEXT","ICON");
	$self->{query_field}  = [ $self->field() ];
	$self->{query_date}=$options->{date};
	
	# force primary key
	$self->{key}=["FIELD_NAME","TABLE_KEY"];
	
	# get object handling columns
	$self->{column_histo} = HistoColumns->new($self->{database_name}, $table_name, $options);
	
	$self->{query_key_value}=[];
	$self->{isip_rules} = undef;
		
	return bless($self,$class);
}


sub query_key_value() {
	my $self = shift;
    if (@_) { @{$self->{query_key_value}} = @_ }
    return @{$self->{query_key_value}};
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


# Construct SQL query to get last inserted value for each field
sub get_query()
{
	my $self = shift;
	
	my $select_histo;
	my @select_conditions;
	my @having_conditions;
	
	# aware of concatened keys
	my @query_field=$self->{column_histo}->get_field_list();
	my @key_field=$self->{column_histo}->get_key_list();
	foreach my $field (@key_field) {
		@query_field = grep {$_ ne $field} @query_field;
	}
	push @query_field, join(',',@key_field);
	
	push @select_conditions, "FIELD_NAME IN (".join(',',map {"'".$_."'"} @query_field).")";
	
	my $date_format = "%Y-%m-%dT%H:%M";
	push @select_conditions, "strftime('$date_format',DATE_HISTO) <= '".$self->query_date()."'" if $self->query_date();

	# select only wanted TABLE_KEY 
	my @table_key_list=$self->query_key_value();
	push @select_conditions, "TABLE_KEY IN (".join (',',map {'\''.$_.'\''} @table_key_list).")" if @table_key_list;
	
	foreach my $condition ($self->query_condition()) {
		if ($condition =~ /^PROJECT/) {
			# preselect TABLE_KEY matching meta_filter in their history 
			push @select_conditions, "TABLE_KEY IN (SELECT TABLE_KEY FROM ".$self->{table_name}." WHERE ".join(' AND ', $condition)." )";
			
			push @having_conditions, $condition
		}
		elsif($condition =~ /^ICON/) {
			# skip
		}
		else {
			# add condition
			push @select_conditions, $condition ;
		}
	}
	
	#my @real_query_field=map {(/$self->{_dynamic_field_re}/)?"'' AS $_":$_ } $self->query_field();
	my @real_query_field=grep {!/$self->{_dynamic_field_re}/} $self->query_field();
		
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
	$select_histo.= " HAVING ".join(" AND ", @having_conditions) if @having_conditions;
	
	$select_histo.= ")
		ON  (TABLE_KEY = TABLE_KEY_2) AND (FIELD_NAME = FIELD_NAME_2) AND (DATE_HISTO = DATE_MAX)
		WHERE FIELD_VALUE != '__delete'
		ORDER BY TABLE_KEY;";

	return $select_histo;
}

# /!\ functionnal bug : field "ICON" not set if you call fetch_row_array()

sub fetch_row() {
	my $self=shift;
	
	my %row=$self->SUPER::fetch_row();
	
	if (exists $row{ICON} and $self->{isip_rules}) {
		if ($self->query_date) {
			$row{ICON}='none';
		}
		else {
			$row{ICON}=$self->{isip_rules}->get_field_icon(%row);
		}
	}
	
	if (exists $row{TEXT} and $self->{isip_rules}) {
		$row{TEXT}=$self->{isip_rules}->get_field_description($row{FIELD_NAME});
	}
	
	if (exists $row{TYPE} and $self->{isip_rules}) {
		$row{TYPE}=$self->{isip_rules}->get_field_type_txt($row{FIELD_NAME});
	}
	
	return %row;
}
 
=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
