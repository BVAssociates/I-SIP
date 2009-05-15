package HistoFieldBaseline;

use Isip::ITable::HistoField;
@ISA = ("HistoField");

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
	if (@_ < 3) {
		croak ('\'open\' take 2 mandatory argument: ${class}->open("databasename","tablename"[ ,{ timeout => $sec, debug => $num} ])')
	}
	# virtual informations
    my $database_name = shift;
	my $table_name = shift;
	my $baseline_date = shift;
	my $options=shift;
		
	my $self  = $class->SUPER::open($database_name, $table_name, $options);
	
	$self->{table_name} = $self->get_baseline_name($table_name,$baseline_date);
	$self->{query_date} = $baseline_date;
	
		
	return bless($self,$class);
}


sub query_date {
    my $self = shift;
    if (@_) { croak("cannot change query_date on a baseline") }
    return $self->{query_date} ;
}

sub get_baseline_name() {
	my $self=shift;
	
	my $table_name=shift;
	my $baseline_date=shift or croak("usage : get_baseline_name(baseline_date)");
	
	$baseline_date =~ tr/-://d;
	return $table_name."_".$baseline_date;
}



# Construct SQL query to get last inserted value for each field
sub get_query()
{
	my $self = shift;
	
	my $select_histo;
	my @select_conditions;
	
	my $date_format = "%Y-%m-%dT%H:%M";
	push @select_conditions, "TABLE_KEY IN (".join (',',map {'\''.$_.'\''} $self->query_key_value()).")" if $self->query_key_value;
	push @select_conditions, $self->query_condition() if $self->query_condition;
	
	my @real_query_field;
	foreach my $field_condition ($self->query_field()) {
		push @real_query_field, $field_condition if not grep ($field_condition eq $_, $self->dynamic_field());
	}
	
	# SQL join to get last inserted KEY/NAME/VALUE
	## INNER or OUTER ??
	$select_histo= "SELECT ".join(',',@real_query_field)." FROM $self->{table_name}\n";
	
	# Add a condition
	$select_histo.= " WHERE ".join(" AND ", @select_conditions) if @select_conditions;

	return $select_histo;
}

 
 
=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
