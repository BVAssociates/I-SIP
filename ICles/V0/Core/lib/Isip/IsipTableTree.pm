package IsipTableTree;

use strict;
use Isip::IsipConfig;
use Isip::IsipLog '$logger';

use Carp qw(carp croak );


sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self= {};
	
	# Arguments
	$self->{options} = shift;
	
	my $isip_config=IsipConfig->new($self->{options});
	$self->{link_table}=$isip_config->get_links();

	undef $isip_config;

	return bless($self, $class);
}

sub get_ancestor_tree() {
	my $self=shift;

	my $current_table=shift or croak("usage: get_all_parent(table)");

	## remember :
	#$self->{table_parent}->{$table_name}->{$table_foreign}->{$field_name} = $field_foreign;
	#$self->{table_child}->{$table_foreign}->{$table_name}->{$field_foreign} = $field_name;

	my %parent_hash=%{ $self->{link_table}->{table_parent}->{$current_table} } if exists $self->{link_table}->{table_parent}->{$current_table} ;

	croak("Unable to get ancestor for :$current_table : more than 1 parent") if keys %parent_hash > 1;

	return ($current_table) if keys %parent_hash == 0;
	return ($self->get_ancestor_tree(keys %parent_hash),$current_table);
}

1;


=head1 NAME

 Isip::Environnement - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
