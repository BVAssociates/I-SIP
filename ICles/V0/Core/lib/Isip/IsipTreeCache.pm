package IsipTreeCache;
use fields qw(isip_env links dirty_child);

use strict;
use Isip::Environnement;
use Isip::IsipLog '$logger';

use Carp qw(carp croak );


sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self= {};
	
	# Arguments
	my $environnement = shift or croak "usage: IsipTreeCache->new(env or env_ref,{options})";
	my $options= shift;
	
	$self = fields::new($class);

	$self->{isip_env}=Environnement->new($environnement,$options);
	$self->{links}=$self->{isip_env}->get_links();

	## remember :
	#$self->{links}->{table_parent}->{$table_name}->{$table_foreign}->{$field_name} = $field_foreign;
	#$self->{links}->{table_child}->{$table_foreign}->{$table_name}->{$field_foreign} = $field_name;

	# store list of table keys whom childs are dirty (diff or uncommented...)
	# format :
	#   $self->{dirty_child}->{"table_name"}=[key1,key2]
	$self->{dirty_child}={};
	
	
	#return bless($self, $class);
	return $self;
}

# compute key from parent table dirty by their child
sub add_dirty_line() {
	my $self=shift;
	
	my $table_name=shift;
	
	my $line_hash_ref=shift or croak("usage : add_dirty_line(table_name,field1,field2,...)");
	my %line_hash=%{$line_hash_ref};
	
	my %parent_hash=%{ $self->{links}->{table_parent}->{$table_name} } if exists $self->{links}->{table_parent}->{$table_name} ;

	croak("Unable to get ancestor for :$table_name : more than 1 parent") if keys %parent_hash > 1;
	
	# stop recursion
	return if keys %parent_hash == 0;
	
	#get information about parent table name
	my ($parent_table)=(keys %parent_hash);
	#get information about parent table relation
	my %foreign_keys=%{$parent_hash{$parent_table}};
	
	
	my $table=$self->{isip_env}->open_local_from_histo_table($parent_table);
	
	# get field from parent (primary keys, I hope!)
	my @child_field=keys %foreign_keys;
	my %condition_hash;
	@condition_hash{@foreign_keys{@child_field}}=@line_hash{@child_field};
	
	croak("provided line do not contains any foreign key") if not %condition_hash;
	
	# We want to retrieve parent line related to current line
	my @condition_array;
	foreach my $primary_field (keys %condition_hash) {
		push (@condition_array, "$primary_field = '$condition_hash{$primary_field}'");
	}

	$table->query_condition(@condition_array);
	
	#get line from parent table
	my $count;
	my %parent_line;
	while (my %row=$table->fetch_row) {
		%parent_line=%row;
		croak ("too many lines linked!") if $count++;
	}
	croak ("to line linked") if not $count;
	
	# store information in memory
	
	my $key_string=join(',',@parent_line{sort $table->key});
	$logger->notice("set dirty $parent_table:'$key_string'");
	push @{$self->{dirty_child}->{$parent_table}}, $key_string;
	
	# go deep
	$self->add_dirty_line($parent_table,\%parent_line);
	
}


sub is_dirty_key() {
	my $self=shift;
	
	die "not implemented yet";

	return 0;
}

# update cache table with informations from $self->{dirty_child}
sub write_dirty_parent() {
	my $self=shift;
	
	my $table=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	
	$table->begin_transaction();
	
	my %dirty_temp=%{$self->{dirty_child}};
	foreach my $dirty_table (keys %dirty_temp) {
		foreach my $dirty_key ( @{ $dirty_temp{$dirty_table} }) {
			eval {$table->insert_row(TABLE_NAME => $dirty_table, TABLE_KEY => $dirty_key)};
			$@ =~ /(are not unique)/;
			if ($1) {
				$logger->warn("$dirty_table,$dirty_key already in CHILD_TO_COMMENT");
			}
			elsif ($@) {
				$logger->error("Erreur while inserting $dirty_table,$dirty_key  in CHILD_TO_COMMENT");
				die;
			}
			undef $@;
		}
	}

	$table->commit_transaction();
}


# recursive function
# return field list linked by foreign key 
# from child to parents
sub parent_list() {
	my $self=shift;

	my $current_table=shift or croak("usage: parent_list(table)");


	my %parent_hash=%{ $self->{links}->{table_parent}->{$current_table} } if exists $self->{links}->{table_parent}->{$current_table} ;

	croak("Unable to get ancestor for :$current_table : more than 1 parent") if keys %parent_hash > 1;

	return ($current_table) if keys %parent_hash == 0;
	return ($current_table,$self->parent_list(keys %parent_hash));
}


# for class testing only
if (!caller) {
	my $test=IsipTreeCache->new("DEV");
	$test->add_dirty_line("CROEXPP2", { 'FNCDTRAIT' => 'ACH920',
        'FNTYPTRAIT' => 'IC',
        'FNCDOGA' => 'ICF' });
	$test->write_dirty_parent();
	
	use Data::Dumper;
	die Dumper($test->{dirty_child});
	
}

1;

=head1 NAME

 Isip::Environnement - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
