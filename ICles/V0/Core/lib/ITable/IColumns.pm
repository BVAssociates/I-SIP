package IColumns;

use strict;

use ITable::ILink;
use Isip::IsipLog '$logger';

use Carp qw(carp cluck confess croak );
use Scalar::Util qw(blessed);

use fields qw(
	table_name
	field
	field_txt
	size
	type
	key
	ilink_obj
);

#use Data::Dumper;

##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub new() {
    my $self = shift;
    #my $class = ref($proto) || $proto;	
	
	# mandatory parameter
	if (@_ < 1) {
		croak ("usage : ".__PACKAGE__."->new(table_name)");
	}
	
	$self=fields::new($self) if not ref($self);
	
	# virtual informations
	my $table_name = shift;
	my $options=shift;
	
	# members initialization
	$self->{table_name} = $table_name;
	
	$self->{field}= [];
	$self->{field_txt}= {};
	$self->{size}= {};
	$self->{type}= {};
	$self->{key}= [];
		
	$self->{ilink_obj} = ILink->new();
	return $self;
}

##################################################
##  pivate methods  ##
##################################################

##################################################
##  param methods  ##
##################################################

##################################################
##  get methods  ##
##################################################


sub get_key_list() {
	my $self = shift;
	
	return sort @{$self->{key}};
}

sub get_field_list() {
	my $self = shift;
	
	return @{$self->{field}};
}

sub get_size() {
	my $self = shift;
	my $field=shift or croak("usage : get_size(field)");
	
	return $self->{size}->{$field};
}

sub get_size_hash() {
	my $self = shift;
	
	return map {$_ => $self->{size}->{$_}} $self->get_field_list();
}

sub get_type() {
	my $self = shift;
	my $field=shift or croak("usage : get_type(field)");
	
	return $self->{type}->{$field};
}

sub get_field_txt() {
	my $self = shift;
	my $field=shift or croak("usage : get_field_txt(field)");
	
	return $self->{field_txt}->{$field};
}

sub get_field_txt_hash() {
	my $self = shift;
	
	return map {$_ => $self->{field_txt}->{$_}} $self->get_field_list();
}

sub get_links() {
	my $self = shift;
	
	return $self->{ilink_obj};
}

sub has_field() {
	my $self = shift;
	my $field=shift or croak("usage : has_field(field)");
	
	return scalar grep {$_ eq $field} @{$self->{field}};
}

##################################################
##  structure modify methods  ##
##################################################

sub update_column() {
	my $self = shift;
	
	my $field=shift or croak('usage : update_column(field [, {size => x, date => x, colno => x, foreign_table => x, foreign_field => x, key => x, description => x} ])' );
	my $field_option=shift;
	
	if (not grep {$_ eq $field} $self->get_field_list()) {
		if ($field_option->{colno} and $field_option->{colno} <= 0) {
			croak("Impossible d'ajouter un champ à la position $field_option->{colno}");
		}
		elsif ($field_option->{colno} and $field_option->{colno} <= @{$self->{field}}) {
			
			splice(@{$self->{field}}, $field_option->{colno}-1, 0, $field);
		}
		else {
			push @{$self->{field}} , $field;
		}
	}
	
	$self->{field_txt}->{$field}=$field_option->{description} if $field_option->{description};
	
	$self->{size}->{$field}=$field_option->{size} if $field_option->{size} and $field_option->{size} =~ /^\w+(\(\d+\))?$/;
	
	if ($field_option->{key} and not grep {$_ eq $field} $self->get_key_list()) {
		push @{$self->{key}}, $field_option->{key} ;
	}
		
	if ($field_option->{foreign_table} and $field_option->{foreign_field}) {
		$self->{ilink_obj}->remove_link($self->{table_name},$field_option->{foreign_table});
		$self->{ilink_obj}->add_link($self->{table_name},$field,$field_option->{foreign_table},$field_option->{foreign_field});
	}
	return 1;
}

# add field and set
sub add_column() {
	my $self = shift;
	
	my $field=shift or croak('usage : add_field(field [, {size => x, date => x, colno => x, foreign_table => x, foreign_field => x, key => x, description => x} ])' );
	my $field_option=shift;
	
	if ($field_option->{colno} and $field_option->{colno} <= 0) {
		croak("Impossible d'ajouter un champ à la position $field_option->{colno}");
	}
	elsif ($field_option->{colno} and $field_option->{colno} <= @{$self->{field}}) {
		
		splice(@{$self->{field}}, $field_option->{colno}-1, 0, $field);
	}
	else {
		push @{$self->{field}} , $field;
	}
	$self->{field_txt}->{$field}=$field_option->{description} if $field_option->{description};
	
	$self->{size}->{$field}=$field_option->{size} if $field_option->{size} and $field_option->{size} =~ /^\w+(\(\d+\))?$/;
	
	if ($field_option->{key}) {
		push @{$self->{key}}, $field ;
	}
		
	if ($field_option->{foreign_table} and $field_option->{foreign_field}) {
		$self->{ilink_obj}->add_link($self->{table_name},$field,$field_option->{foreign_table},$field_option->{foreign_field});
	}
	return 1;
}

sub remove_column() {
	my $self = shift;
	my $field=shift or croak("usage : remove_field(field)");
	my $field_option=shift;
	
	@{$self->{field}}=grep {$_ ne $field} @{$self->{field}};
	
	delete $self->{field_txt}->{$field};
	delete $self->{size}->{$field};
	@{$self->{key}}=grep {$_ ne $field} @{$self->{key}};
			
	foreach my $parent ($self->{ilink_obj}->get_parent_tables($self->{table_name})) {
		$self->{ilink_obj}->remove_link($self->{table_name},$parent);
	}
	
	return 1;
}

1;
__END__
=head1 NAME

 ITable::IColumns - Handle columns of an ITable object
 
=head1 SYNOPSIS


=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
