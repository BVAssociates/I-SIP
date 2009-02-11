package ILink;

use strict;
use Carp ('croak');

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self= {};
	
	if (@_ > 0) {
		croak ("'new' take 0 mandatory argument: $class->new()")
	}
	
	# store the same information in 2 structs
	$self->{table_child}= {};
	$self->{table_parent}= {};
	
	return bless ($self, $class);
}

# Add a foreign key link :
# - "table_name.field_name" has is value in "table_foreign.field_foreign"
# - "table_foreign"'s primary keys must contains "table_foreign.field_foreign"
sub add_link() {
	my $self=shift;
	
	my $table_name=shift;
	my $field_name=shift;
	my $table_foreign=shift;
	my $field_foreign=shift or croak "usage: add_link (table_name, field_name, table_foreign, field_foreign)";
	
	# store the same information in 2 structs
	$self->{table_parent}->{$table_name}->{$table_foreign}->{$field_name} = $field_foreign;
	$self->{table_child}->{$table_foreign}->{$table_name}->{$field_foreign} = $field_name;
	
	
}


# for a table having some foreign_key
# return ($field1, $field2)
sub get_foreign_fields() {
	my $self=shift;
	my $table=shift;
	my $f_table=shift or croak ("usage : get_child_fields(table,f_table)");
	
	return %{$self->{table_parent}->{$table}->{$f_table}};
}

# for a table having some foreign_key
# return ($foreign_key1, $foreign_key2) corresponding to get_child_fields()
sub get_parent_fields_OBSOLETE() {
	my $self=shift;
	my $table=shift;
	my $f_table=shift or croak ("usage : get_child_fields(table,f_table)");
	
	my @field=$self->get_child_fields($table,$f_table);
	
	if (@field) {
		my %temp_fkey=%{ $self->{table_parent}->{$table}->{$f_table} };
		return @temp_fkey{@field};
	} else {
		return undef;
	}
}

sub get_parent_tables() {
	my $self=shift;
	my $table=shift or croak ("usage : get_parent_tables(table)");
	
	return keys %{ $self->{table_parent}->{$table} };

}

# recursive function
# return field list linked by foreign key 
# from child to parents
sub get_parent_path() {
	my $self=shift;

	my $current_table=shift or croak("usage: parent_list(table)");


	my %parent_hash=%{ $self->{table_parent}->{$current_table} } if exists $self->{table_parent}->{$current_table} ;

	croak("Unable to get ancestor for :$current_table : more than 1 parent") if keys %parent_hash > 1;

	return ($current_table) if keys %parent_hash == 0;
	return ($current_table,$self->parent_list(keys %parent_hash));
}

sub get_child_tables() {
	my $self=shift;
	my $table=shift or croak ("usage : get_child_tables(table)");
	
	return keys %{$self->{table_child}->{$table} };

}


1;

=head1 NAME

Package ILink
 
=head1 DESCRIPTION

Stocke l'ensemble des relations de clefs étrangères entre les tables.

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut