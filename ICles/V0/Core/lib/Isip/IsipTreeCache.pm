package IsipTreeCache;
use fields qw(isip_env links dirty_child);

use strict;
use Isip::IsipLog '$logger';

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

	# store list of table keys whom childs are dirty (diff or uncommented...)
	# format :
	#   $self->{dirty_child}->{"table_name"}={key1 => 6, key2 => 2}
	$self->{dirty_child}={};
	
	
	#return bless($self, $class);
	return $self;
}


sub add_dirty_key() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift or croak("usage: add_dirty_key(table_name, key_string [, value])");
	my $dirty_value=shift;
	
	if (not exists $self->{isip_env}->{info_table}->{$table_name}) {
		$logger->error("$table_name does not exists");
		return 0;
	}
	
	if (exists $self->{dirty_child}->{$table_name}->{$key_string}) {
		#$logger->notice("$table_name => $key_string : already in cache");
		#return;
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
	
	$self->add_dirty_line($table_name,\%return_row,$dirty_value);
}

# compute key from parent table dirty by their child
# arg1 : table_name
# arg2 : hash ref of line (contain at least foreign key)

sub add_dirty_line() {
	my $self=shift;
	
	my $table_name=shift;
	
	my $line_hash_ref=shift or croak("usage : add_dirty_line(table_name, {field1 => 'value',field2 => 'value',...} [, int])");
	my $dirty_value=shift;
	
	if (defined $dirty_value and $dirty_value !~ /^[+-]?\d+$/) {
		croak("value must be an integer in : add_dirty_line(table_name, {field1 => 'value',field2 => 'value',...} [, int])");
	}
	$dirty_value=1 if not defined $dirty_value;
	
	my %line_hash=%{$line_hash_ref};
	
	if (not exists $self->{isip_env}->{info_table}->{$table_name}) {
		#$logger->error("$table_name does not exists");
		#return 0;
	}
	
	my @table_key=sort split(',',$self->{isip_env}->{info_table}->{$table_name}->{key} );
	my $current_key_string=@line_hash{@table_key};
	
	# we are already in the cache, we stop recursion
	if (exists $self->{dirty_child}->{$table_name}->{$current_key_string}) {
		#$logger->notice("$table_name => $current_key_string : already in cache");
		#return;
	}	
	
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
	
	# parent already in the cache, we stop recursion
	if (exists $self->{dirty_child}->{$parent_table}->{$key_string}) {
		#$logger->notice("$table_name => $key_string : already in cache");
		#return;
	}
	
	$logger->notice("add $dirty_value to modified count of $parent_table:'$key_string' because of $table_name:".join(',', @line_hash{@child_field}));
	$self->set_dirty_key($parent_table,$key_string,$dirty_value);
	
	# go deep
	$logger->info("recurse into $parent_table:'$key_string' because of $table_name:".join(',', @line_hash{@child_field}));
	$self->add_dirty_line($parent_table,\%parent_line,$dirty_value);
	
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
sub set_dirty_key() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift or croak("usage: set_dirty_key(table_name,key_string [, value])");
	my $dirty_value=shift;
	
	$dirty_value=1 if not defined $dirty_value;
	
	$self->{dirty_child}->{$table_name}->{$key_string} += $dirty_value;
}


# return true if line of table is dirty (ie: one of its child was modified)
# arg1 : table_name
# arg2 : hash ref of line (contain at least primary key)
sub is_dirty_line() {
	my $self=shift;
	
	my $table_name=shift;
	my $line_hash_ref=shift or croak("usage : is_dirty_line(table_name, {field1 => 'value',field2 => 'value'},...)");
	my %line_hash=%{$line_hash_ref};
	
	if (not exists $self->{isip_env}->{info_table}->{$table_name}) {
		$logger->error("$table_name does not exists");
		return 0;
	}
	my @table_key_field=split(',',$self->{isip_env}->{info_table}->{$table_name}->{key});
	
	foreach (@table_key_field) {
		croak ("is_dirty_line : wait at least all keys in arg2") if not exists $line_hash{$_};
	}
	my $table_key_value=join(',', @line_hash{@table_key_field});
	
	# check in current object
	if (exists $self->{dirty_child}->{$table_name}->{$table_key_value}) {
		return $self->{dirty_child}->{$table_name}->{$table_key_value};
	}
	
	# check on disk	
	my $table=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	$table->query_condition("TABLE_NAME ='$table_name'","TABLE_KEY ='$table_key_value'");
	
	my $count=0;
	while (my %row=$table->fetch_row) {
		$count += $row{NUM_CHILD};
	}

	return $count;
}

# write table to disk with informations from $self->{dirty_child}
sub rewrite_dirty_cache() {
	my $self=shift;
	
	my $table=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	
	$table->begin_transaction();
	
	$table->execute("DELETE from CHILD_TO_COMMENT");
	
	my %dirty_temp=%{$self->{dirty_child}};
	foreach my $dirty_table (keys %dirty_temp) {
		my %dirty_keys=%{$dirty_temp{$dirty_table}};
		foreach my $dirty_key ( keys %dirty_keys ) {
			# DELETE AND INSERT (aka INSERT OR IGNORE)
			#$table->execute("DELETE from CHILD_TO_COMMENT where TABLE_NAME='$dirty_table' AND TABLE_KEY ='$dirty_key'");
			eval {$table->insert_row(TABLE_NAME => $dirty_table, TABLE_KEY => $dirty_key, NUM_CHILD => $dirty_keys{$dirty_key})};
			$@ =~ /(are not unique)/;
			if ($1) {
				$logger->warn("$dirty_table,$dirty_key,$dirty_keys{$dirty_key} already in CHILD_TO_COMMENT");
			}
			elsif ($@) {
				$logger->error("Erreur while inserting $dirty_table,$dirty_key,$dirty_keys{$dirty_key}  in CHILD_TO_COMMENT");
				die;
			}
		}
	}
	$table->commit_transaction();
	
	# flush memroy
	$self->{dirty_child}={};
}

sub update_dirty_cache() {
	my $self=shift;

	my $table=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	
	$table->begin_transaction();
	
	#$table->execute("DELETE from CHILD_TO_COMMENT");
	
	
	my %dirty_temp=%{$self->{dirty_child}};
	foreach my $dirty_table (keys %dirty_temp) {
		my %dirty_keys=%{$dirty_temp{$dirty_table}};
		
		
		foreach my $dirty_key ( keys %dirty_keys ) {
		
			$table->query_condition("TABLE_NAME = '$dirty_table'","TABLE_KEY = '$dirty_key'");
			
			my $num_child;
			while (my %row=$table->fetch_row()) {
				$num_child=$row{NUM_CHILD};
			}
			
			
			if (not defined $num_child) {
				$logger->debug("insert $dirty_table,$dirty_key,$dirty_keys{$dirty_key}");
				$table->insert_row(TABLE_NAME => $dirty_table, TABLE_KEY => $dirty_key, NUM_CHILD => $dirty_keys{$dirty_key});
			}
			else {
				my $sum_dirty=$num_child+$dirty_keys{$dirty_key};
				if ($sum_dirty > 0) {
					$logger->debug("insert $dirty_table,$dirty_key,$num_child+$dirty_keys{$dirty_key}");
					$table->update_row(TABLE_NAME => $dirty_table, TABLE_KEY => $dirty_key, NUM_CHILD => $num_child+$dirty_keys{$dirty_key});
				}
				else {
					$logger->debug("insert $dirty_table,$dirty_key,$num_child+$dirty_keys{$dirty_key}");
					$table->delete_row(TABLE_NAME => $dirty_table, TABLE_KEY => $dirty_key);
				
				}
			}
		}
	}
	$table->commit_transaction();
	
	# flush memroy
	$self->{dirty_child}={};
}

sub clear_dirty_cache() {
	my $self=shift;

	my $table_name=shift or croak("usage: clear_dirty_cache(table)");
	
	my $cache=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	
	$logger->notice("clear cache for $table_name");
	$cache->execute("DELETE from CHILD_TO_COMMENT where TABLE_NAME='$table_name'");
	
}

sub clear_dirty_cache_parent() {
	my $self=shift;

	my $table_name=shift or croak("usage: clear_dirty_cache_parent(table)");
	
	my @liste_table=$self->{links}->parent_list($table_name);
	
	#remove leadf table
	shift @liste_table;
	
	my $cache=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	
	foreach (@liste_table) {
		$self->clear_dirty_cache($_);
	}
	
}

# for class testing only
if (!caller) {
	require Isip::Environnement;
	my $test=IsipTreeCache->new(Environnement->new("DEV"));
	#$test->clear_dirty_cache("TRAITP");
	$test->add_dirty_key("CROEXPP2", 'SAB,CBLCA,26,13');
	$test->add_dirty_key("CROEXPP2", 'SAB,CBLCA,26,13',-1);
	$test->add_dirty_line("CROEXPP2", { 'FNCDTRAIT' => 'ACH920','FNTYPTRAIT' => 'IC', 'FNCDOGA' => 'ICF' });
	$test->add_dirty_line("CROEXPP2", { 'FNCDTRAIT' => 'ACH920','FNTYPTRAIT' => 'IC', 'FNCDOGA' => 'ICF' }, -1);
	#$test->write_dirty_cache();
	#$test->update_dirty_cache();
	
	use Data::Dumper;
	print Dumper($test->{dirty_child});
	
	print "TEST:".$test->is_dirty_line('TRAITP', {FHCDTRAIT => 'ACH920'} )."\n";
	print "TEST:".$test->is_dirty_line('TRAITP', {FHCDTRAIT => 'ACH920x'} )."\n";
	print "TEST:".$test->is_dirty_line('TRAITP2', {FHCDTRAIT => 'ACH920'} )."\n";
}

1;

=head1 NAME

 Isip::Environnement - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
