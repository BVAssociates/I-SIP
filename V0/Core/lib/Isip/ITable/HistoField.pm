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
	
	$self->{dynamic_field}= [ "TYPE", "TEXT","ICON","DESCRIPTION"];
	$self->{query_field}  = [ $self->field() ];
	$self->{query_date}=$options->{date};
	
	$self->{field_doc}= {};
	$self->_set_field_description();
	
	# force primary key
	$self->{key}=["FIELD_NAME"];
	
	$self->{isip_rules} = {};
	
	return bless($self,$class);
}


# Get documentation from database
sub _set_field_description() {
	my $self = shift;

	
	if ($self->{field_doc}) {
		
		#TODO better way to get database path!
		
		my $database_doc=$self->{database_name};
		my $table_name_doc=$self->{table_name};
		
		$table_name_doc =~ s#_HISTO##;
		$database_doc =~ s#/\w+(_$table_name_doc.sqlite)$#/ISIP_DOC$1#;
		
		my $table_doc=eval{
			Sqlite->open($database_doc, $table_name_doc."_DOC", {debug => $self->debugging()})
		};
		
		return if $@;
		
		$self->_debug("Get field doc $table_name_doc");
		
		while (my %field_doc=$table_doc->fetch_row) {
			$self->{field_doc}->{$field_doc{TABLE_KEY}}{$field_doc{FIELD_NAME}}=$field_doc{DESCRIPTION};
		}
	}
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
	$select_histo.= " GROUP BY FIELD_NAME_2, TABLE_KEY_2)
		ON  (TABLE_KEY = TABLE_KEY_2) AND (FIELD_NAME = FIELD_NAME_2) AND (DATE_HISTO = DATE_MAX)
		WHERE FIELD_VALUE != '__delete'
		ORDER BY TABLE_KEY;";

	return $select_histo;
}


sub fetch_row() {
	my $self=shift;
	
	my %row=$self->SUPER::fetch_row();
	
	if ( %row and grep($_ eq "DESCRIPTION",$self->query_field()) ) {
		$row{DESCRIPTION}="";
	
		$row{DESCRIPTION}=$self->{field_doc}->{$row{TABLE_KEY}}{$row{FIELD_NAME}}
			if exists $self->{field_doc}->{$row{TABLE_KEY}}{$row{FIELD_NAME}};
	}
	
	return %row;
}
 
 
 
=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
