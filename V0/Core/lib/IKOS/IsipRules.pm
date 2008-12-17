package IsipRules;


use Carp qw(carp croak );
use strict;

##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self={};
	
	# mandatory parameter
	if (@_ < 0) {
		croak ('\'new\' take 0 mandatory argument: ${class}->open( [ { debug => $num} ) ] )')
	}
	my $options=shift;
	
	
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


1;

=head1 NAME

 IKOS::IsipRules - Static Class to handle type and status
 
=head1 SYNOPSIS

Static Class to handle type and status

=author

BV Associates, 2008
