package FieldDiff;


require IKOS::DATA::DataDiff;
@ISA = ("DataDiff");

use Carp qw(carp cluck confess croak );
use strict;
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
		croak ($class.'->open : take 2 mandatory argument: ${class}->open( $data_ref1, $date_ref2[ ,{ timeout => $sec, debug => $num} ])')
	}
	
	my $self=bless( $class->SUPER::open(@_), $class);
	
	
	$self->{field} = ["OLD_VALUE","NEW_VALUE"];

}