package HistoRules;


use Carp qw(carp croak );
use strict;

use IKOS::SIP;
use IKOS::DATA::Sqlite;
use IKOS::DATA::TableDiff;

=head1 NAME

 IKOS::IsipRules - Class to handle type and status
 
=head1 SYNOPSIS

Class to handle type and status.

=author

BV Associates, 2008

=cut

##################################################
##  constructor  ##
##################################################


# open an existing table on a Sqlite Database
sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self={};
	
	# member initializations
	$self->{table_name};
	$self->{current_type}={};
	$self->{table_diff}={};
	$self->{debugging}=0;
	
	# constants identifiers enumeration
	# TODO : import them from a configuration file
	$self->{type} = ["fonctionnel","technique","manuel","administratif","securite"];
	
	$self->{field_diff} = ["nouveau","modifie","valide","supprime"];
	$self->{field_status} = ["","aquite","test","valide","inconnu"];
	
	$self->{line_diff} = ["contient_nouveau","contient_modif","valide","",""];
	$self->{line_status} = ["nouveau","en_cours","valide","supprime"];
	
	# mandatory parameter
	if (@_ < 1) {
		croak ('\'new\' take 1 mandatory argument: ${class}->new( table_name[, { diff => $TableDiff_ref, debug => \$num} ) ] )')
	}
	
	$self->{table_name}=shift;
	
	# options
	my $options=shift;
	$self->{table_diff}=$options->{diff} if exists $options->{diff};
	$self->debugging($options->{debug}) if exists $options->{debug};

	# Amen
	bless ($self, $class);

	# load informations
	$self->_init();

    return $self;
}


sub set_diff() {
	my $self=shift;
	
	my $diff_ref=
}

##################################################
##  pivate methods  ##
##################################################

sub _init() {
	my $self=shift;
	
	$self->{current_type}=$self->get_current_type();
}

sub debugging {
    my $self = shift;
    if (@_) { $self->{debugging} = shift}
    return $self->{debugging};
}

# simple debug method
sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:Rules.".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

##################################################
##  methods to get constants enumeration ##
##################################################

sub enum_type () {
	my $self=shift;

	return @{$self->{type}};
}

sub enum_field_diff () {
	my $self=shift;

	return @{$self->{field_diff}};
}

sub enum_line_diff () {
	my $self=shift;

	return @{$self->{line_diff}};
}

sub enum_field_status () {
	my $self=shift;

	return @{$self->{field_status}};
}

sub enum_line_status () {
	my $self=shift;

	return @{$self->{line_status}};
}


##################################################
##  methods to get information of current state ##
##################################################

# load the table TABLE_INFO and get type of each column
sub get_current_type () {
	my $self=shift;
	
	my $table_info=Sqlite->open($self->{line_status}
}

##################################################
##  methods to compute status from a Histo line ##
##################################################

# return the computed status of a field
#  - if set_diff has been called before, it will
# use it to return the "diff" status
#  - if no set_diff, return status  
# param type : type of the field
# param status : current status from histo
# param comment : current comment from histo
# return status : new computed status
sub get_field_status () {
	my $self=shift;
	
	my $type=shift;
	my $status=shift;
	my $comment=shift;
	
	# compute new status
	#$return_status=
	
	return $return_status;
}

# return the computed status of a line
#  - if set_diff has been called before, it will
# use it to return the "diff" status
#  - if no set_diff, return status  
# param status_list : list of status of each field
# return status : computed status
sub get_line_status () {
	my $self=shift;
	
	my @status_list=@_;
		
	return $return_status
}

1;


