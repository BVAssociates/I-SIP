package IsipTreeCache;
use fields qw(isip_env links dispacher_list);

use strict;
use Isip::IsipLog '$logger';
use Isip::IsipRules;
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
	$self->{links}=$self->{isip_env}->get_links_menu();
		
	## remember :
	#$self->{links}->{table_parent}->{$table_name}->{$table_foreign}->{$field_name} = $field_foreign;
	#$self->{links}->{table_child}->{$table_foreign}->{$table_name}->{$field_foreign} = $field_name;

	# list of classes to call during the run
	$self->{dispacher_list}= [];
	
	
	#return bless($self, $class);
	return $self;
}


##############################
#  Tree walk through Methods #
##############################

# from a key, get whole line and apply recurse_line();
sub recurse_key() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift or croak("usage: add_dirty_key(table_name, key_string, action)");
	
	if (not exists $self->{isip_env}->{info_table}->{$table_name}) {
		$logger->error("$table_name does not exists");
		return 0;
	}
	

	my @table_key_field=split(',',$self->{isip_env}->{info_table}->{$table_name}->{key});
	my @table_key_value=split(',',$key_string);
	
	if (@table_key_field != @table_key_value) {
		croak("key must have ",scalar @table_key_field," fields (get ".scalar @table_key_value.")");
	}
	
	my $table=$self->{isip_env}->open_local_from_histo_table($table_name);
	
	# construct table line
	my @fetch_condition;
	foreach my $field (@table_key_field) {
		push @fetch_condition, $field ." = ". $table->quote(shift(@table_key_value));
	}
	
	$table->isip_rules(IsipRules->new($table_name,$self->{isip_env}));
	$table->query_condition(@fetch_condition);
	
	my $count=0;
	my %return_row;
	while(my %row=$table->fetch_row) {
		$count++;
		%return_row=%row;
	}
	
	croak("unable to find line for $key_string in $table_name : $count lines found") if not $count;
	
	$self->recurse_line($table_name,\%return_row);
}

# compute key from parent table dirty by their child
# arg1 : table_name
# arg2 : hash ref of line (contain at least foreign key)
sub recurse_line() {
	my $self=shift;
	
	my $table_name=shift;
	my $line_hash_ref=shift or croak("usage : recurse_line(table_name, {field1 => 'value',field2 => 'value',...})");
	
	my $checked=0;
	foreach my $dispatcher (@{$self->{dispacher_list}}) {
		$checked += $dispatcher->check_before_cache($table_name,$line_hash_ref);
	}
	
	if ( $checked) {
		
		# get key string from current line
		my %line_hash=%{$line_hash_ref};
		my @table_key=$self->{isip_env}->get_table_key($table_name);
		my $current_key_string=@line_hash{@table_key};
		
		# dispatch action for current line
		$self->dispatch_action($table_name,$current_key_string,$line_hash_ref);
		
		# dispatch action for parents line
		$self->_recurse_line_action($table_name,$line_hash_ref);
	}
}

sub _recurse_line_action() {
	my $self=shift;
	
	my $table_name=shift;
	my $line_hash_ref=shift or croak("usage : add_dirty_line(table_name, {field1 => 'value',field2 => 'value',...})");

	my $env_name=$self->{isip_env}->{environnement};
	
	my %line_hash=%{$line_hash_ref};
	
	if (not exists $self->{isip_env}->{info_table}->{$table_name}) {
		#$logger->error("$table_name does not exists");
		#return 0;
	}
	
	my @table_key=$self->{isip_env}->get_table_key($table_name);
	my $current_key_string=@line_hash{@table_key};
	
	my %parent_hash=%{ $self->{links}->{table_parent}->{$table_name} } if exists $self->{links}->{table_parent}->{$table_name} ;
	
	# stop recursion
	return if keys %parent_hash == 0;
	
	foreach my $parent_table ( keys %parent_hash ) {
		
		#get information about parent table name
		#my ($parent_table)=(keys %parent_hash);
		#get information about parent table relation
		my %foreign_keys=%{$parent_hash{$parent_table}};
		
		
		my $table= eval { $self->{isip_env}->open_local_from_histo_table($parent_table) };
		if ($@) {
			$logger->warning("$table_name : Impossible d'acceder à la table parente $parent_table, veuillez verifier la configuration");
			next;
		}
		
		$table->isip_rules(IsipRules->new($parent_table,$self->{isip_env}));
		$table->query_field("ICON",$table->query_field());
		
		# get field from parent (primary keys, I hope!)
		my @child_field=keys %foreign_keys;
		my %condition_hash;
		@condition_hash{@foreign_keys{@child_field}}=@line_hash{@child_field};
		
		croak("$env_name : $table_name (clef: $current_key_string) : cette ligne ne contient pas de clef étrangère") if not %condition_hash;
		
		# We want to retrieve parent line related to current line
		my @condition_array;
		foreach my $primary_field (keys %condition_hash) {
			if (not defined $condition_hash{$primary_field}
					or $condition_hash{$primary_field} eq '') {
				carp("$env_name : $table_name (clef: $current_key_string) : $primary_field n'est pas défini dans la table $parent_table");
				return;
			}
			else {
				push (@condition_array, "$primary_field = ".$table->quote($condition_hash{$primary_field}) );
			}
		}

		$table->query_condition(@condition_array);
		
		#get line from parent table
		my $count=0;
		my %parent_line;
		while (my %row=$table->fetch_row) {
			%parent_line=%row;
			croak ("$env_name : $table_name (clef: $current_key_string) : plusieurs valeurs correspondent dans $parent_table (",join(',',@condition_array),")") if $count++;
		}
		if (not $count) {
			carp("$env_name : $table_name (clef: $current_key_string) n'a pas de correspondance dans la table $parent_table (".join(',',@condition_array).")");
			return;
		}
		
		# store information in memory
		
		my $key_string=join(',',@parent_line{sort $table->key});
		
		
		$logger->info("add in cache : $env_name->$parent_table:'$key_string' because of $table_name:".join(',', @line_hash{@child_field}));
		$self->dispatch_action($parent_table,$key_string,\%parent_line);
		
		# go deep
		$logger->info("recurse into $env_name->$parent_table:'$key_string' because of $table_name:".join(',', @line_hash{@child_field}));
		$self->_recurse_line_action($parent_table,\%parent_line);
	}
	
	return;
	
}

# compute cache from IsipDiff object
sub recurse_diff() {
	my $self=shift;
	
	my $table_name=shift;
	my $diff_ref=shift;
	
	my $diff_class=blessed($diff_ref);
	if (not ($diff_class and $diff_ref->isa("IsipDiff") )) {
		croak "usage: add_dirty_diff(IsipDiff class)";
	}
	
	# remove lines only in source
	my %key_new_hash=$diff_ref->get_target_only();
	foreach my $key_new (keys %key_new_hash ) {
		#TODO : what to do of delete lines?
		#$self->recurse_line($table_name, $key_new_hash{$key_new} );
	}
	undef %key_new_hash;
	
	# add missing lines
	my %key_delete_hash=$diff_ref->get_source_only();
	foreach my $key_delete (keys %key_delete_hash) {
		$self->recurse_line($table_name,$key_delete_hash{$key_delete} );
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
		$self->recurse_key($table_name,$key_update);
	}
}


#########################
#  Dispatching Methods  #
#########################

sub add_dispatcher() {
	my $self=shift;
	
	my $dispatcher=shift;
	
	if (not blessed $dispatcher or not $dispatcher->isa("CacheInterface")) {
		croak("usage add_dispatcher(CacheInterface)");
	}
	
	push @{$self->{dispacher_list}},$dispatcher;
}

# only set value of a key in memory (not ancestor)
sub dispatch_action() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift or croak("usage: add_row_cache(table_name,key_string [, value])");
	my $line_ref=shift;
	
	if (ref($line_ref) ne "HASH") {
		croak("usage: add_row_cache(table_name,key_string,line_hash_ref)");
	}

	foreach my $dispatcher (@{$self->{dispacher_list}}) {
		if (defined $dispatcher->can("add_row_cache")) {
			$dispatcher->add_row_cache($table_name,$key_string,$line_ref);
		}
		else {
			$logger->error("Unable to do add_row_cache() on ".ref($dispatcher));
		}
	}
	
}



#load dirty information in memory for a table
# hint: use before many is_dirty_line()
# arg1 : table_name
sub load_cache() {
	my $self=shift;
	
	my $table_name=shift or croak("usage : load_cache(table_name");
	
	my $count;
	foreach my $dispatcher (@{$self->{dispacher_list}}) {
		$count += $dispatcher->load_cache($table_name);
	}
	
	return $count;
}

# return true if line of table is dirty (ie: one of its child was modified)
# hint : use preload() in script to avoid many request on DB
# arg1 : table_name
# arg2 : hash ref of line (contain at least primary key)
sub is_dirty_key() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift or croak("usage : is_dirty_key(table_name, key_string");
	
	my $dirty;
	foreach my $dispatcher (@{$self->{dispacher_list}}) {
		$dirty += $dispatcher->is_dirty_key($table_name,$key_string);
	}
	
	return $dirty;
}

# write table to disk with informations from $self->{dirty_child}
sub clear_cache() {
	my $self=shift;
	
	my @tables=@_;
	
	foreach my $cache (@{$self->{dispacher_list}}) {
		$cache->clear_cache(@tables);
	}
	
}

sub save_cache() {
	my $self=shift;

	foreach my $cache (@{$self->{dispacher_list}}) {
		$cache->save_cache();
	}
}


# for class testing only
if (!caller) {
	require Isip::Environnement;
	use Isip::Cache::CacheStatus;
	use Isip::Cache::CacheProject;
	
	my $env=Environnement->new("PRD");
	my $test=IsipTreeCache->new($env);
	
	#$test->add_dispatcher(CacheStatus->new($env));
	my $project_cache=CacheProject->new($env);
	$project_cache->set_dirty_project("test 5");
	$test->add_dispatcher($project_cache);
	
	#$test->clear_cache();
	#$test->recurse_key("CROEXPP2", 'SAB,CBLCA,26,13');
	#$test->recurse_key("CROEXPP2", 'SAB,CBLCA,26,13');
	$test->recurse_line("CROEXPP2", { OLD_ICON => "valide", ICON => "burp" ,OLD_PROJECT=> "test 6", PROJECT => "test 5,test 6",'FNCDTRAIT' => 'ACH920','FNTYPTRAIT' => 'IC', 'FNCDOGA' => 'ICF' });
	$test->recurse_line("CROEXPP2", { OLD_ICON => "burp", ICON => "valide" , PROJECT => '','FNCDTRAIT' => 'ACH920','FNTYPTRAIT' => 'IC', 'FNCDOGA' => 'ICF' });
	#$test->save_cache();
	#$test->load_cache("CROEXPP2");
	
	print "ACH920:".$test->is_dirty_key('TRAITP', 'ACH920' )."\n";
	print "CBLCA:".$test->is_dirty_key('TRAITP', 'CBLCA' )."\n";
	print "ACH920x:".$test->is_dirty_key('TRAITP', 'ACH920x' )."\n";
	print "ACH920(TRAITP2):".$test->is_dirty_key('TRAITP2', 'ACH920' )."\n";
}

1;

=head1 NAME

 Isip::Environnement - Class to access data from IKOS SIP 

=head1 SYNOPSIS

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
