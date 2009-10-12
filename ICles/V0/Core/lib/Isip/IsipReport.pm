package IsipReport;


use Carp qw(carp croak );
use strict;

use Isip::IsipRules;

use Date::Calc qw(:all);
use List::Util qw(sum);

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

sub _get_date_interval {
	my $self=shift;
	
	my $day_count=shift or croak("usage: _get_date_interval(day_count [,day_back])");
	my $day_offset=shift;
	
	my @today=Today_and_Now();
	
	my @date_begin;
	my @date_end=@today;
	
	@date_begin=Add_Delta_DHMS(@date_end, -$day_count, 0,0,0);
	@date_end=Add_Delta_DHMS(@today, -$day_offset, 0,0,0) if $day_offset;
	
	my $date_begin_txt= sprintf("%d-%02d-%02dT%02d:%02d:%02d", @date_begin);
	my $date_end_txt= sprintf("%d-%02d-%02dT%02d:%02d:%02d", @date_end);
	
	return ($date_begin_txt, $date_end_txt);
}

##################################################
##  public methods  ##
##################################################

sub get_update_histo_count() {
	my $self=shift;
	
	my $day_count=shift or croak("usage: get_update_histo_rate(day_count [,day_back])");
	my $day_offset=shift;
	
	my ($date_begin_txt, $date_end_txt) = $self->_get_date_interval($day_count, $day_offset);
	
	
	my @table_list=$self->{environnement_ref}->get_table_list();
	
	my $total_count=0E0;
	foreach my $table_name ( @table_list ) {
		
		my $table = $self->{environnement_ref}->open_local_table($table_name."_HISTO");
		
		my $rules = IsipRules->new( $table_name, $self->{environnement_ref} );
		
		my @hidden_field_list=$rules->get_hidden_field_list();
		my $field_query="";
		if ( @hidden_field_list ) {
			$field_query="AND FIELD_NAME NOT IN (".join(',', map {"'".$_."'"} @hidden_field_list).") ";
		}
		
		my %hidden_key_for=$rules->get_hidden_key_hash();
		my $key_query="";
		if ( %hidden_key_for ) {
			foreach my $key_field ( keys %hidden_key_for ) {
				$key_query .= " AND NOT (TABLE_KEY = '$key_field' AND FIELD_NAME = '$hidden_key_for{$key_field}' ) ";
			}
		}
		
		
		
		$table->custom_select_query(
			qq{SELECT count(*), max(date_histo) FROM $table_name\_HISTO
				WHERE DATE_HISTO >= '$date_begin_txt' AND DATE_HISTO <= '$date_end_txt'
				AND COMMENT != 'Creation'
				$field_query
				$key_query
				--GROUP BY DATE_HISTO}
		);
		
		while( my @row = $table->fetch_row_array() ) {
			#print join(',', $table_name,@row)."\n" if $row[0];
			$total_count += $row[0];
		}
	}
	
	print "Nombre de modification de valeur de champs : ".$total_count."\n";
	
	return $total_count;
}

sub get_update_comment_count() {
	my $self=shift;
	
	my $day_count=shift or croak("usage: get_update_histo_rate(day_count [,day_back])");
	my $day_offset=shift;
	
	my ($date_begin_txt, $date_end_txt) = $self->_get_date_interval($day_count, $day_offset);
	
	my @table_list=$self->{environnement_ref}->get_table_list();
	
	my $total_count=0E0;
	foreach my $table_name ( @table_list ) {
		
		my @hidden_field_list=IsipRules->new( $table_name, $self->{environnement_ref} )->get_hidden_field_list();
		my $table = $self->{environnement_ref}->open_local_table($table_name."_HISTO");
		my $field_query="";
		if ( @hidden_field_list ) {
			$field_query="AND FIELD_NAME NOT IN (".join(',', map {"'".$_."'"} @hidden_field_list).") ";
		}

		$table->custom_select_query(
			qq{SELECT count(*), USER_UPDATE FROM $table_name\_HISTO
				WHERE DATE_UPDATE >= '$date_begin_txt' AND DATE_UPDATE <= '$date_end_txt'
				AND USER_UPDATE not like '%isip_service'
				$field_query
				GROUP BY USER_UPDATE}
		);
		
		while( my @row = $table->fetch_row_array() ) {
			#print join(',', $table_name,@row)."\n" if $row[0];
			$total_count += $row[0];
		}
	}
	
	print "Nombre de modification de commentaires : ".$total_count."\n";
	
	return $total_count;
}


1;

=head1 NAME

 Isip::IsipReport - Classe calculant des indicateurs sur I-SIP
 
=head1 SYNOPSIS



=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits réservés.

=cut