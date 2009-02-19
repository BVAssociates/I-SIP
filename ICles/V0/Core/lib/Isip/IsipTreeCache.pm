package IsipTreeCache;
use fields qw(isip_env links cache_class preload);

use strict;
use Isip::IsipLog '$logger';
use Isip::Cache::CacheInterface;

use Carp qw(carp croak );
use Scalar::Util qw(blessed);


sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	# Arguments
	my $environnement = shift or croak "usage: IsipTreeCache->new(env or env_ref,{options})";
	my $options= shift;
	
	my $env_class=blessed($environnement);
	if (not ($env_class and $env_class eq "Environnement") ) {
		croak "usage: IsipTreeCache->new(Environnement,{options})";
	}
	
	my $self = fields::new($class);

	$self->{isip_env}=$environnement;
	$self->{links}=$self->{isip_env}->get_links();

	## remember :
	#$self->{links}->{table_parent}->{$table_name}->{$table_foreign}->{$field_name} = $field_foreign;
	#$self->{links}->{table_child}->{$table_foreign}->{$table_name}->{$field_foreign} = $field_name;

	# list of classes to call during the run
	$self->{cache_class}= [];
	
	# if preloaded table, fields which are not dirty are OK
	$self->{preload}={};
	
	#return bless($self, $class);
	return $self;
}

sub add_cache_class() {
	my $self=shift;
	
	my $action_class=shift;
	
	if (not blessed $action_class or not $action_class->isa("CacheInterface")) {
		croak("usage add_cache_class(CacheInterface)");
	}
	
	push @{$self->{cache_class}}, $action_class;
}

sub add_dirty_key() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift or croak("usage: add_dirty_key(table_name, key_string)");
	
	if (not exists $self->{isip_env}->{info_table}->{$table_name}) {
		$logger->error("$table_name does not exists");
		return 0;
	}
	

	my @table_key_field=split(',',$self->{isip_env}->{info_table}->{$table_name}->{key});
	my @table_key_value=split(',',$key_string);
	
	if (@table_key_field != @table_key_value) {
		croak("key must have ",scalar @table_key_field," fields (get ".scalar @table_key_value.")");
	}
	
	# construct table line
	my @fetch_condition;
	foreach my $field (@table_key_field) {
		push @fetch_condition, $field ." = '". shift(@table_key_value)."'";
	}
	
	my $table=$self->{isip_env}->open_local_from_histo_table($table_name);
	$table->query_condition(@fetch_condition);
	
	my $count=0;
	my %return_row;
	while(my %row=$table->fetch_row) {
		$count++;
		%return_row=%row;
	}
	
	croak("unable to find line for $key_string in $table_name : $count lines found") if not $count;
	
	$self->add_dirty_line($table_name,\%return_row);
}

# compute key from parent table dirty by their child
# arg1 : table_name
# arg2 : hash ref of line (contain at least foreign key)

sub add_dirty_line() {
	my $self=shift;
	
	my $table_name=shift;
	my $line_hash_ref=shift or croak("usage : add_dirty_line(table_name, {field1 => 'value',field2 => 'value',...})");
	
	
	my %line_hash=%{$line_hash_ref};
	
	if (not exists $self->{isip_env}->{info_table}->{$table_name}) {
		#$logger->error("$table_name does not exists");
		#return 0;
	}
	
	my @table_key=sort split(',',$self->{isip_env}->{info_table}->{$table_name}->{key} );
	my $current_key_string=@line_hash{@table_key};
	
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
	my $count=0;
	my %parent_line;
	while (my %row=$table->fetch_row) {
		%parent_line=%row;
		croak ("too many lines linked!") if $count++;
	}
	if (not $count) {
		carp("unable to find key in $parent_table for : ",join(',',@condition_array));
		return;
	}
	
	# store information in memory
	
	my $key_string=join(',',@parent_line{sort $table->key});
	
	
	$logger->notice("add to cache : $parent_table:'$key_string' because of $table_name:".join(',', @line_hash{@child_field}));
	$self->add_row_cache($parent_table,$key_string,\%parent_line);
	
	# go deep
	$logger->info("recurse into $parent_table:'$key_string' because of $table_name:".join(',', @line_hash{@child_field}));
	$self->add_dirty_line($parent_table,\%parent_line);
	
}

# compute cache from IsipDiff object
sub add_dirty_diff() {
	my $self=shift;
	
	my $table_name=shift;
	my $diff_ref=shift;
	
	my $diff_class=blessed($diff_ref);
	if (not ($diff_class and $diff_ref->isa("IsipDiff") )) {
		croak "usage: IsipTreeCache->add_dirty_diff(IsipDiff class)";
	}
	
	# remove lines only in source
	my %key_new_hash=$diff_ref->get_target_only();
	foreach my $key_new (keys %key_new_hash ) {
		#$self->add_dirty_line($table_name, $key_new_hash{$key_new} );
	}
	undef %key_new_hash;
	
	# add missing lines
	my %key_delete_hash=$diff_ref->get_source_only();
	foreach my $key_delete (keys %key_delete_hash) {
		$self->add_dirty_line($table_name,$key_delete_hash{$key_delete} );
	}
	undef %key_delete_hash;
	
	# add new field
	my @key_new_field_hash=$diff_ref->get_source_only_field();
	foreach my $new_field (@key_new_field_hash) {
		#TODO ?!
	}
	undef @key_new_field_hash;
	
	# update modified lines
	my %key_update_hash=$diff_ref->get_source_update();
	foreach my $key_update (keys %key_update_hash ) {
		$self->add_dirty_key($table_name,$key_update);
	}
}

# only set value of a key in memory (not ancestor)
sub add_row_cache() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift or croak("usage: set_dirty_key(table_name,key_string [, value])");
	my $line_ref=shift;
	
	if (ref($line_ref) ne "HASH") {
		croak("usage: action(table_name,key_string,line_hash_ref)");
	}
	
	foreach my $cache (@{$self->{cache_class}}) {
		$cache->add_row($table_name,$key_string,$line_ref);
	}
	
}

#load dirty information in memory for a table
# hint: use before many is_dirty_line()
# arg1 : table_name
sub preload_cache() {
	my $self=shift;
	
	my $table_name=shift or croak("usage : preload(table_name");
	
	my $count;
	foreach my $cache (@{$self->{cache_class}}) {
		$count += $cache->load_cache($table_name);
	}
	
	return $count;
}

# return true if line of table is dirty (ie: one of its child was modified)
# hint : use preload() in script to avoid many request on DB
# arg1 : table_name
# arg2 : hash ref of line (contain at least primary key)
sub is_dirty_line() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift or croak("usage : is_dirty_line(table_name, key_string");
	
	my $dirty;
	foreach my $cache (@{$self->{cache_class}}) {
		$dirty += $cache->is_dirty($table_name,$key_string);
	}
	
	return $dirty;
}

# write table to disk with informations from $self->{dirty_child}
sub clear_dirty_cache() {
	my $self=shift;
	
	foreach my $cache (@{$self->{cache_class}}) {
		$cache->clear_cache();
	}
	
}

sub save_dirty_cache() {
	my $self=shift;

	foreach my $cache (@{$self->{cache_class}}) {
		$cache->save_cache();
	}
}


# for class testing only
if (!caller) {
	require Isip::Environnement;
	use Isip::Cache::CacheStatus;
	my $env=Environnement->new("DEV");
	my $test=IsipTreeCache->new($env);
	
	my $cache=CacheStatus->new($env);
	$test->add_cache_class($cache);
	
	#$test->clear_dirty_cache();
	$test->add_dirty_key("CROEXPP2", 'SAB,CBLCA,26,13');
	$test->add_dirty_key("CROEXPP2", 'SAB,CBLCA,26,13');
	$test->add_dirty_line("CROEXPP2", { 'FNCDTRAIT' => 'ACH920','FNTYPTRAIT' => 'IC', 'FNCDOGA' => 'ICF' });
	$test->save_dirty_cache();
	$test->preload_cache("CROEXPP2");
	
	print "TEST:".$test->is_dirty_line('TRAITP', 'ACH920' )."\n";
	print "TEST:".$test->is_dirty_line('TRAITP', 'ACH920x' )."\n";
	print "TEST:".$test->is_dirty_line('TRAITP2', 'ACH920' )."\n";
}

1;

=head1 NAME

 Isip::Environnement - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
