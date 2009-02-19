package IsipRulesDiff;

use Isip::IsipRules;
use base qw(IsipRules);


use Carp qw(carp croak );
use strict;

use Isip::IsipLog '$logger';

=head1 NAME

 Isip::IsipRulesDiff - Class to handle type and status
 
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
##  static methods to get constants enumeration ##
##################################################


sub enum_field_icon() {
	my $self=shift;
	
	return (NEW => "ajoute", UPDATE => "different", OK => "egal", DELETE => "supprime", STAMP_UPDATE => "stamp_update");
}

sub enum_line_icon() {
	my $self=shift;
	
	return (NEW => "ajoute", UPDATE => "different", OK => "egal", DELETE => "supprime", ERROR => "erreur");
}

##################################################
##  methods to compute status from a Diff line ##
##################################################


sub get_field_icon () {
	my $self=shift;
	
	my %line=@_;
	
	my $name=$line{FIELD_NAME};
	my $value=$line{FIELD_VALUE};
	my $diff=$line{STATUS};
	
	my $type=$self->get_field_type($name);

	# TODO write display rules?
	if ($type eq "STAMP") {
		return $self->{field_icon}->{OK};
	}
	else {
		return $self->{field_icon}->{$diff};
	}
}

sub get_line_icon() {
	my $self=shift;
	
	my @icon_list=@_;
	my $return_icon;
	
	my %icon_by_name= reverse %{$self->{field_icon}};
	
	my %counter;
	foreach (keys %{$self->{field_icon}} ) {
		$counter{$_}=0;
	}
	
	foreach (@icon_list) {
		my $icon;
		if (not defined $_) {
			$logger->critical("Impossible de determiner l'icone de la ligne, car un champ n'a pas d'icone") ;
			return $self->{line_icon}{ERROR};
			last;
		}
		elsif (not defined $icon_by_name{$_}) {
			
			$logger->critical("Impossible de determiner l'icone de la ligne, car $_ n'est pas un icone valide") ;
			return $self->{line_icon}{ERROR};
			last;
		} else {
			$icon=$icon_by_name{$_};
		}
		
		$counter{$icon}++;
		
	}
	
	return $self->{line_icon}{UPDATE} if $counter{UPDATE} > 0;
	return $self->{line_icon}{NEW} if $counter{NEW} > 0;
	return $self->{line_icon}{DELETE} if $counter{DELETE} > 0;
	return $self->{line_icon}{OK} if $counter{OK} > 0;
}

1;


