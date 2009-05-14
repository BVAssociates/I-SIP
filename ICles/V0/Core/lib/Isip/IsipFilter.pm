package IsipFilter;

use strict;

use fields qw(
	filter_register
	filter_current
	filter_multi
	
);


use Carp qw(carp croak );
use Scalar::Util qw(blessed);

use Isip::IsipLog '$logger';

=head1 NAME

 Isip::IsipFilter - 
 
=head1 SYNOPSIS



=head1 AUTHOR

BV Associates, 2009

=cut

##################################################
##  constructor  ##
##################################################

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	my $self= fields::new($class);
	
	# mandatory parameter
	if (@_ < 0) {
		croak ("usage ; ".__PACKAGE__.'->new()');
	}

	# ENV_VAR => corresponding field
	$self->{filter_register} = { 
		FILTER_PROJECT => "PROJECT",
		FILTER_ICON => "ICON",
		FILTER_DIFF => "DIFF",
	};
	
	# a value can contains multiple value
	$self->{filter_multi} = {FILTER_PROJECT => ','};
	
	$self->_log_filter();
	
    return $self;
}



##################################################
##  pivate methods  ##
##################################################

sub _log_filter {
	my $self=shift;
	
	while ( my ($filter, $field) = each %{$self->{filter_register}}) {
		if (exists $ENV{$filter} and $ENV{$filter}) {
			$logger->notice("Filtrage activ� pour $filter=$ENV{$filter}");
		}
	}
}

##################################################
##  public methods  ##
##################################################


sub is_display_line() {
	my $self=shift;
	my %line=@_;
	
	my $total_match=keys %{$self->{filter_register}};
	while ( my ($filter, $field) = each %{$self->{filter_register}}) {
		if (exists $ENV{$filter} and $ENV{$filter} and exists $line{$field}) {
			
			my $filter_value=$ENV{$filter};
			if (not exists $self->{filter_multi}->{$filter}) {
				# simple value => simple compare
				if ($filter_value =~ s/^!//) {
					if ($filter_value ne $line{$field}) {
						$total_match--;
					}
				}
				else {
					if ($filter_value eq $line{$field}) {
						$total_match--;
					}
				}
			}
			else {
				# potential multiple value, use grep
				my $sep=$self->{filter_multi}->{$filter};
				
				if ($filter_value =~ s/^!//) {
					if (not grep {$_ eq $filter_value} split(/$sep/,$line{$field})) {
						$total_match--;
					}
				}
				else {
					if (grep {$_ eq $filter_value} split(/$sep/,$line{$field})) {
						$total_match--;
					}
				}
			}
		}
		else {
			$total_match--;
		}
	}
	
	return not $total_match ;
}

sub get_query_condition() {
	my $self=shift;	
	
	my @condition;
	while ( my ($filter, $field) = each %{$self->{filter_register}}) {
		if (exists $ENV{$filter} and $ENV{$filter}) {
			
			my $filter_value=$ENV{$filter};
			my $neg = $filter_value =~ s/^!//;

			if (not exists $self->{filter_multi}->{$filter}) {
				
				my $op=' = ';
				$op=' <> ' if $neg;
				
				push @condition, $field.$op."'".$filter_value."'";
			}
			else {
				
				my $op=' LIKE ';
				croak ("la syntaxe de $filter n'est support�e") if $neg;
				
				push @condition, $field.$op."'%".$filter_value."%'";
			}
		}
	}
	
	return @condition;
}

sub get_field_value() {
	my $self=shift;
	my $wanted_field=shift or croak("usage: get_field_value(field)");
	
	while ( my ($filter, $field) = each %{$self->{filter_register}}) {
		if ($field eq $wanted_field) {
			if (exists $ENV{$filter} and $ENV{$filter} ) {
				return $ENV{$filter};
			}
			return;
		}
	}
	return;
}

#tests
if (!caller) {
	$ENV{FILTER_PROJECT}="projet de test";
	$ENV{FILTER_ICON}="!valide";
	my $test_filter=IsipFilter->new();
	print $test_filter->is_display_line(PROJECT => "projet de test", ICON => "diff")."\n";
	print join(',',$test_filter->get_query_condition())."\n";
	print $test_filter->get_field_value("PROJECT")."\n";
}


1;


