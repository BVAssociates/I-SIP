package IsipRulesFieldDiff;

use Isip::IsipRulesDiff;
use base qw(IsipRulesDiff);


use Carp qw(carp croak );
use strict;

use Isip::IsipLog '$logger';

=head1 NAME

 Isip::IsipRulesFieldDiff - Class to handle type and status
 
=head1 SYNOPSIS

Class to handle type and status.

=author

BV Associates, 2008

=cut

##################################################
##  constructor  ##
##################################################


#create a new IsipRulesDiff object
sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;

	my $self = fields::new($class);

    return $self->SUPER::new(@_);
}



##################################################
##  pivate methods  ##
##################################################



##################################################
##  methods to compute status from a Diff line ##
##################################################


sub get_field_icon () {
	my $self=shift;
	
	my %line=@_;
	
	my $field=$line{FIELD_NAME};
	my $value=$line{FIELD_VALUE};
	my $diff=$line{STATUS};

	# mmh, don't clear but ok, because we are on a list of Histo fields
	if ($field eq "FIELD_NAME") {
		my $type=$self->get_field_type($value);

		# TODO write display rules?
		if ($type eq "STAMP") {
			return $self->{field_icon}->{STAMP_UPDATE};
		}
		elsif ($type eq "HIDDEN") {
			return $self->{field_icon}->{OK};
		}
		else {
			return $self->{field_icon}->{$diff};
		}
	}
	else {
		return $self->{field_icon}->{$diff};
	}
}

# if a field was a stamp, we ignore line
sub get_line_icon() {
	my $self=shift;
	
	my @icon_list=@_;
	
	if (shift @icon_list eq $self->{field_icon}->{STAMP_UPDATE}) {
		return $self->{line_icon}{OK};
	}
	else {
		return shift @icon_list;
	}
}

1;


