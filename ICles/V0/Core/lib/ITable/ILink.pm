package ILink;

use strict;
use Carp ('croak');
use Storable qw(dclone);
use Scalar::Util qw(blessed);



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
	
	# static var for get_depth_table_path()
	$self->{depth_path_seen}= {};
	
	return bless ($self, $class);
}

sub clone() {
	my $self=shift;
	
	return dclone($self);
}

# Add a foreign key link :
# - "table_name.field_name" has is value in "table_foreign.field_foreign"
# - "table_foreign"'s primary keys must contains "table_foreign.field_foreign"
sub add_link() {
	my $self=shift;
	
	my $first_arg=shift or croak "usage: add_link(table_name, field_name, table_foreign, field_foreign) or add_link(ILink_ref)";
	
	if (blessed $first_arg and $first_arg->isa("ILink")) {
		my $self_add=$first_arg;
		
		foreach my $parent (keys %{ $self_add->{table_parent} }) {

			foreach my $table_foreign (keys %{ $self_add->{table_parent}->{$parent} }) {

				foreach my $field_name (keys %{ $self_add->{table_parent}->{$parent}->{$table_foreign} }) {
					my $field_foreign=$self_add->{table_parent}->{$parent}->{$table_foreign}->{$field_name};
					$self->add_link($parent, $field_name, $table_foreign ,$field_foreign);
				}
			}
		}		
	}
	else {	
		my $table_name=$first_arg;
		my $field_name=shift;
		my $table_foreign=shift;
		my $field_foreign=shift or croak "usage: add_link(table_name, field_name, table_foreign, field_foreign) or add_link(ILink_ref)";
		
		# check for loop in graph by looking for inverted path
		if ( my @path=$self->find_path($table_name, $table_foreign)) {
			croak("Erreur lors de l'ajout de $table_foreign comme table parente de $table_name, car un cycle à été détécté : ".join(',',@path));
		}

		# store the same information in 2 structs
		$self->{table_parent}->{$table_name}->{$table_foreign}->{$field_name} = $field_foreign;
		$self->{table_child}->{$table_foreign}->{$table_name}->{$field_foreign} = $field_name;
		
		return $self;
	}
	
	
}

sub remove_link() {
	my $self=shift;
	
	my $table_name=shift;
	my $table_foreign=shift or croak "usage: add_link (table_name, table_foreign)";;
	
	delete $self->{table_parent}->{$table_name}->{$table_foreign};
	delete $self->{table_child}->{$table_foreign}->{$table_name}; 
	
	if ( not %{ $self->{table_parent}->{$table_name} } ) {
		delete $self->{table_parent}->{$table_name};
	}
	
	if ( not %{ $self->{table_child}->{$table_foreign} } ) {
		delete $self->{table_child}->{$table_foreign};
	}
}


# for a table having some foreign_key
# return ($field1, $field2)
sub get_foreign_fields() {
	my $self=shift;
	my $table=shift;
	my $f_table=shift or croak ("usage : get_child_fields(table,f_table)");
	
	use Data::Dumper;
	croak Dumper($self->{table_parent}) if not exists $self->{table_parent}->{$table}->{$f_table};
	
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

# renvoie la liste des paires (parent,enfant) parente de la table donnée
# param table (string)
# param depth (integer) : 1 pour renvoyer tous les ascendants, 0 pour juste le père
# return (hash) : %father_of{$table}
sub get_parent_tables_hash() {
	my $self=shift;
	my $table=shift or croak ("usage : get_parent_tables_hash(table)");
	my $depth=shift;
	
	return () if not exists $self->{table_parent}->{$table};
	
	my %father_of;
	
	my %parent_tables=%{ $self->{table_parent}->{$table}};
	
	if ($depth) {

		$father_of{$table} = [ keys %parent_tables ];
		foreach (keys %parent_tables) {
			# appel recursif
			my %deep_father_of = $self->get_parent_tables_hash($_,$depth);
			%father_of= ( %father_of, %deep_father_of );
		}
	}
	else {
		$father_of{$table} = [ keys %parent_tables ];
	}
	
	return %father_of;
}

# renvoie la liste des tables dont la table $table possède une clef etrangère
sub get_parent_tables() {
	my $self=shift;
	my $table=shift or croak ("usage : get_parent_tables(table)");
	my $depth=shift;
	
	return () if not exists $self->{table_parent}->{$table};
	
	my @return;
	
	my %parent_table=%{ $self->{table_parent}->{$table}};
	
	if ($depth) {
		foreach (keys %parent_table) {
			push @return, $_;
			push @return, $self->get_parent_tables($_,$depth);
		}
	}
	else {
		@return = keys %parent_table;
	}
	
	return @return;
}

# renvoie la liste des tables qui ont une clef etrangère vers la table $table
sub get_child_tables() {
	my $self=shift;
	my $table=shift or croak ("usage : get_child_tables(table)");
	
	return keys %{$self->{table_child}->{$table} };

}

# recursive function
# return field list linked by foreign key 
# from child to parents
sub get_parent_field_path() {
	my $self=shift;

	my $current_table=shift or croak("usage: get_parent_field_path(table)");


	my %parent_hash=%{ $self->{table_parent}->{$current_table} } if exists $self->{table_parent}->{$current_table} ;

	croak("Unable to get ancestor for :$current_table : more than 1 parent") if keys %parent_hash > 1;

	return ($current_table) if keys %parent_hash == 0;
	return ($current_table,$self->parent_list(keys %parent_hash));
}

# recursive function to get a deep path (deepest first)
# return table list
sub get_depth_table_path() {
	my $self=shift;
	
	my $current_table=shift or croak("usage: get_depth_table_path(table)");
	
	if (exists $self->{depth_path_seen}->{$current_table}) {
		croak("Detection de cycle sur la table $current_table.
Veuiller vérifier ses liens (F_KEY).");
	}
	else {
		$self->{depth_path_seen}->{$current_table}++;
	}
	
	die "TODO!";
	
	my %parent_node=%{ $self->{table_child}->{current_table}};
	my @child_list;
	foreach my $adjacent (grep {$_ ne $current_table} keys %parent_node) {
		@child_list=$self->get_depth_table_path($adjacent);
	}
	
	return (@child_list,$current_table);
}

# return a path of table between 2 tables
sub find_path() {
	my $self=shift;
	
	my $table_first = shift;
	my $table_last  = shift;
	
	if (not defined $table_first) {
		croak('usage: find_path(table_first ,table_last)');
	}
	
	if (not defined $table_last) {
		return;
	}
	
	my @path_list = ($table_first);
	
	foreach my $child ( $self->get_child_tables($table_first) ) {
		
		if ( $child eq $table_last) {
			return (@path_list, $child);
		}
		else {
			# appel recursif aux enfants
			my @partial_path = $self->find_path( $child, $table_last);
			if ( @partial_path ) {
				return (@path_list, @partial_path);
			}
		}
	}
	
	return;
}

1;

=head1 NAME

Package ILink
 
=head1 DESCRIPTION

Stocke l'ensemble des relations de clefs étrangères entre les tables.

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut