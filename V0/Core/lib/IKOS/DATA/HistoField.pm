package HistoField;

require IKOS::DATA::Sqlite;
@ISA = ("Sqlite");

use Carp qw(carp cluck confess croak );
use strict;

use IKOS::IsipRules;
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
	
	$self->{isip_rules} = {};
	
	return bless($self,$class);
}

# overide Sqlite
# Add dynamic field if necessary
sub fetch_row_array() {
	my $self = shift;
	
	my %temp_line;
	
	# loop if hidden fields
	do {
		my @return_line=$self->SUPER::fetch_row_array();
		
		# nothing to return, exit
		return () if not @return_line;
		
		# save query_field
		my @query_field_save=$self->query_field();
		
		# move dynamic field to the end
		my @used_dynamic_fields;
		foreach my $field (@query_field_save) {
			if (grep ($_ eq $field, $self->dynamic_field) ) {
				push @used_dynamic_fields, $field ;
			}
		}
		$self->query_field($self->field,@used_dynamic_fields);
		
		# put line into hash
		%temp_line=$self->array_to_hash(@return_line, ("") x scalar @used_dynamic_fields);

		# now handle rules if defined
		if (blessed $self->{isip_rules}) {
			$temp_line{TYPE}=$self->{isip_rules}->get_field_type($temp_line{FIELD_NAME}) if exists $temp_line{TYPE};
			$temp_line{ICON}=$self->{isip_rules}->get_field_status($temp_line{FIELD_NAME},$temp_line{STATUS}, $temp_line{COMMENT}) if exists $temp_line{ICON};
			
			$temp_line{TEXT}=$self->{isip_rules}->get_field_description($temp_line{FIELD_NAME}) if exists $temp_line{TEXT};
		}
		# restore query_field
		$self->query_field(@query_field_save);
		
	} while  ($temp_line{ICON} eq "cache");
	
	
	return $self->hash_to_array(%temp_line);
}

sub isip_rules() {
	my $self = shift;
	
	if (@_) { $self->{isip_rules} = shift }
    return $self->{isip_rules} ;
}

sub query_key_value() {
	my $self = shift;
    if (@_) { $self->{query_key_value} = shift }
    return $self->{query_key_value} ;
}

sub query_date {
    my $self = shift;
    if (@_) { $self->{query_date} = shift }
    return $self->{query_date} ;
}

# Construct SQL query to get last inserted value for each field
sub get_query()
{
	my $self = shift;
	
	my $select_histo;
	my @select_conditions;
	
	my $date_format = "%Y-%m-%d %H:%M";
	push @select_conditions, "strftime('$date_format',DATE_HISTO) <= '".$self->query_date()."'" if $self->query_date();
	push @select_conditions, "TABLE_KEY ='".$self->query_key_value()."'" if $self->query_key_value;
	
	# SQL join to get last inserted KEY/NAME/VALUE
	## INNER or OUTER ??
	$select_histo= "SELECT ".join(',',$self->field)." FROM
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
	$select_histo.= " GROUP BY FIELD_NAME_2, TABLE_KEY_2)
		ON  (TABLE_KEY = TABLE_KEY_2) AND (FIELD_NAME = FIELD_NAME_2) AND (DATE_HISTO = DATE_MAX)
		WHERE FIELD_VALUE != '__delete'
		ORDER BY TABLE_KEY;";

	return $select_histo;
}
 
 
 
=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
