package IsipReport;


use Carp qw(carp croak );
use strict;

use Isip::IsipRules;

use List::Util qw(reduce);

##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self={};
	
	# mandatory parameter
	if (@_ < 1) {
		croak ('usage: new(environnement_ref)');
	}
	
	$self->{environnement_ref} = shift;

	
	bless ($self, $class);
	
    return $self;
}

##################################################
##  pivate methods  ##
##################################################


##################################################
##  public methods  ##
##################################################

sub get_update_histo_count() {
	my $self=shift;

	return $self->get_histo_count(@_,"COMMENT != 'Creation'");
}

sub get_update_comment_count() {
	my $self=shift;

	return $self->get_histo_count(@_,"COMMENT != 'Creation'","(COMMENT NOT LIKE 'Baseline%' AND STATUS != 'VALIDE')");
}

sub get_update_invalid_count() {
	my $self=shift;

	return $self->get_histo_count(@_,"STATUS != 'Valide'");
}

sub get_histo_count_test() {
	my $self=shift;
	
	my $date_begin_txt = shift @_;
	my $date_end_txt   = shift @_;
	my @query_where    = @_;
	
		
	push @query_where, "DATE_HISTO >= '$date_begin_txt'" if $date_begin_txt;
	push @query_where, "DATE_HISTO <= '$date_end_txt'" if $date_end_txt;
	
	
}

sub get_histo_count() {
	my $self=shift;
	
	my $date_begin_txt = shift @_;
	my $date_end_txt   = shift @_;
	my @query_where    = @_;
	
		
	push @query_where, "DATE_HISTO >= '$date_begin_txt'" if $date_begin_txt;
	push @query_where, "DATE_HISTO <= '$date_end_txt'" if $date_end_txt;
	
	my @return_list;
	
	my @table_list=$self->{environnement_ref}->get_table_list();
	foreach my $table_name ( @table_list ) {
		
		my $table = $self->{environnement_ref}->open_local_table($table_name."_HISTO");
		
		my $rules = IsipRules->new( $table_name, $self->{environnement_ref} );
		
		my @hidden_field_list=$rules->get_hidden_field_list();
		if ( @hidden_field_list ) {
			push @query_where,"FIELD_NAME NOT IN (".join(',', map {"'".$_."'"} @hidden_field_list).")";
		}
		
		my %hidden_key_for=$rules->get_hidden_key_hash();
		if ( %hidden_key_for ) {
			foreach my $key_field ( keys %hidden_key_for ) {
				push @query_where, "NOT (TABLE_KEY = '$key_field' AND FIELD_NAME = '$hidden_key_for{$key_field}' )";
			}
		}
		
			
		my $query_where_txt="";
		$query_where_txt="WHERE ".join(' AND ', @query_where) if @query_where;
		
		$table->custom_select_query(
			qq{SELECT count(*) FROM $table_name\_HISTO
				$query_where_txt
				}
		);
		
		while( my @row = $table->fetch_row_array() ) {
			#print join(',', $table_name,@row)."\n" if $row[0];
			push @return_list, [ $table_name,@row ] ;
		}
	}
	
	# suivant le résultat attendu, on renvoie le tableau
	# ou la somme des compteurs
	if ( wantarray ) {
		return @return_list;
	}
	else {
		my $total=0E0;
		foreach (@return_list) {
			$total += $_->[1];
		}
		return $total;
	}
}


1;

=head1 NAME

 Isip::IsipReport - Classe calculant des indicateurs sur I-SIP
 
=head1 SYNOPSIS



=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits réservés.

=cut