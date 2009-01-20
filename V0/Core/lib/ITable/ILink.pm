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
sub get_child_fields() {
	my $self=shift;
	my $table=shift;
	my $f_table=shift or croak ("usage : get_child_fields(table,f_table)");
	
	return sort keys %{$self->{table_parent}->{$table}->{$f_table}};
}

# for a table having some foreign_key
# return ($foreign_key1, $foreign_key2) correspinding to get_child_fields()
sub get_parent_fields() {
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

sub get_child_tables() {
	my $self=shift;
	my $table=shift or croak ("usage : get_child_tables(table)");
	
	return keys %{$self->{table_child}->{$table} };

}

# return list of foreign fields of a foreign table
# in same order that get_linked_fields()
sub get_foreign_fields() {
	my $self=shift;
	
	my $table_foreign=shift or croak "usage: get_foreign_fields (table_foreign)";
	
	my @field_list;
	
	foreach my $field ( $self->get_linked_fields() ) {
		if (exists $self->{table_link}->{$field}->{table_foreign} ) {
			push @field_list, $self->{table_link}->{$field}->{table_foreign}
		}
	}
	
	return @field_list;

}

1;
