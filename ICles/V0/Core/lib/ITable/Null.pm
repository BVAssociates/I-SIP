package Null;

# code import
use strict;
use Carp;
use Scalar::Util qw(blessed);

use ITable::abstract::DATA_interface;
our @ISA=("DATA_interface");

use Isip::IsipLog '$logger';

##################################################
##  constructor  ##
##################################################

# open($Define_obj)
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	my $self={};
	bless($self, $class);
	$self=$self->SUPER::open(@_);
	
	############
	# Bless Object. Amen.
	############

    bless($self, $class);
	
		
    return $self;
}

sub field {
    my $self = shift;
    if (@_) { @{ $self->{field} } = @_ }
    return @{ $self->{field} };
}

sub key {
    my $self = shift;
    if (@_) { @{ $self->{key} } = @_ }
    return @{ $self->{key} };
}

sub query_key_value() {
	return;
}

sub fetch_row_array(){
	return ();
}
sub fetch_row()
{
	return ();
}

# Insert list  as a row
sub insert_row_array() {
	return;
}

# Insert hash  as a row
sub insert_row() {
	return;
}

# add new field
sub add_field() {
	return;
}

sub remove_field() {
	return;
}

# update a row on a primary key
sub update_row_array() {
	return;
}
# update a row on a primary key
sub update_row() {
	my $self = shift;
	
	croak("update_row() not implemented in ".ref($self));
}

# update a row on a primary key
sub delete_row() {
	return;
}


##############################################
## Destructor        ##
##############################################

#sub DESTROY () {
#	my $self = shift;
#	
#}

1;  # so the require or use succeeds



=head1 NAME

 ITable::Null - Null table 

=head1 SYNOPSIS


=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
 
