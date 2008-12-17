package HistoRules;


use Carp qw(carp croak );
use strict;

use IKOS::DATA::TableDiff;

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
		croak ('\'new\' take 1 mandatory argument: ${class}->new( TableDiff \$ref, [ { debug => \$num} ) ] )')
	}
	
	$self->{table_diff}=shift;
	croak("usage: ${class}->new( TableDiff \$ref, [ { debug => \$num} ) ] )") if ref($self->{table_diff}) ne "TableDiff";
	
	my $options=shift;
	
	$self->debugging() = $options->{debug};
	
	# Fixed identifiers
	$self->{type} = ["fonctionnel","technique","manuel","administratif","securite"];
	
	$self->{field_status} = ["nouveau","modifie","valide","supprime"];
	$self->{field_comment} = ["","aquite","test","valide","inconnu"];
	
	$self->{line_status} = ["contient_nouveau","contient_modif","valide","",""];
	$self->{line_comment} = ["nouveau","en_cours","valide","supprime"];
	
	bless ($self, $class);
	
    return $self;
}

##################################################
##  pivate methods  ##
##################################################

# simple debug method
sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:HISTO.".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

##################################################
##  public methods  ##
##################################################

sub get_type_list () {
	my $self=shift;

	return @{$self->{type}};
}

sub get_field_status_list () {
	my $self=shift;

	return @{$self->{field_status}};
}

sub get_line_status_list () {
	my $self=shift;

	return @{$self->{line_status}};
}

sub get_field_comment_list () {
	my $self=shift;

	return @{$self->{field_status}};
}

sub get_line_comment_list () {
	my $self=shift;

	return @{$self->{line_status}};
}

sub get_field_status () {
	my $self=shift;
	
	my $type=shift;
	my $status=shift;
	my $comment=shift;
	
	# compute new status
	#$status=
	
	return $status;
}

1;

=head1 NAME

 IKOS::IsipRules - Static Class to handle type and status
 
=head1 SYNOPSIS

Static Class to handle type and status.

=author

BV Associates, 2008
