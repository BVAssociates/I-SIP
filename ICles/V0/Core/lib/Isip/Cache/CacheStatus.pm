package CacheStatus;

use Isip::Cache::CacheInterface;
use base 'CacheInterface';
use fields qw(action current_table);

use strict;
use Carp qw(carp croak );

use Isip::IsipLog '$logger';

sub new() {
	my $proto = shift;
    my $class = ref($proto) || $proto;

	my $self = fields::new($class);
	$self=$self->SUPER::new(@_);
	
	# store list of table keys whom childs are dirty (diff or uncommented...)
	# format :
	#   $self->{memory_cache}->{"table_name"}={key1 => 6, key2 => 2}
	
	$self->{action}=0;
	$self->{current_table}="";
	
	return $self;
}

sub check_before_cache() {
	my $self=shift;
	
	my $table=shift;
	my $value_ref=shift;
	
	$self->{action}=0;
	if (exists $value_ref->{ICON} and $value_ref->{ICON}) {
		if (exists $value_ref->{OLD_ICON} and $value_ref->{OLD_ICON}) {
			if ($value_ref->{ICON} ne $value_ref->{OLD_ICON} ) {
				$self->{action}=1 if $value_ref->{OLD_ICON} =~ /^valide/;
				$self->{action}=-1 if $value_ref->{ICON} =~ /^valide/;
					# note : match 'valide' and 'valide_label'
			}
		}
		else {
			$self->{action}=1 if $value_ref->{ICON} !~ /^valide|exclus$/;
		}
	}
	
	my $dirty=abs($self->{action});
	$self->{current_table}=$table if $dirty;

	#add table itself in cache
	my @key=$self->{isip_env}->get_table_key($table);
	my $key_string=join(',',@{$value_ref}{@key});
	$self->add_row_cache($table,$key_string,$value_ref);
	
	return $dirty;
}

sub add_row_cache() {
	my $self=shift;
	
	return if not $self->{action};
	
	my $table_name=shift;
	my $key_string=shift;
	my $value_ref=shift or croak("usage: add_row_cache(table_name,key_string , value)");
	
	
	$logger->info("add $table_name:$key_string in CacheStatus");
	
	croak("check_before_cache must be called before add_row_cache") if not $self->{current_table};
	my $table_fired=$self->{current_table};
	
	my $old_value=$self->{memory_cache}->{$table_name}->{$table_fired}->{$key_string};
	$old_value=0 if not defined $old_value;
	
	my $new_value=$old_value + $self->{action};
	$self->{memory_cache}->{$table_name}->{$table_fired}->{$key_string} = $new_value;
}

sub get_dirty_key {
	my $self=shift;
	my $table_name=shift or croak("usage : get_dirty_key(table_name)");
	
	my @return_list;
	
	# if key was not in dirty childs and table was preloaded, key is OK
	if ($self->{loaded_table}->{$table_name}) {
		if (exists $self->{memory_cache}->{$table_name}) {
			my %tables=%{$self->{memory_cache}->{$table_name}};
			foreach my $source_name (keys %tables) {
				foreach my $table_key (keys %{$tables{$source_name}}) {
					push @return_list, $table_key;
				}
			}
		}
		return @return_list;
	}
	
	
	
	# check on disk	
	my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
	$table->query_field("TABLE_KEY");
	$table->query_distinct(1);
	
	my @condition;
	push @condition, "TABLE_NAME='$table_name' ";
	$table->query_condition(@condition);
	
	while (my ($table_key)=$table->fetch_row_array()) {
		push @return_list, $table_key;
	}
	
	return @return_list;
}

sub is_dirty_key() {
	my $self=shift;
	
	my $table_name=shift;
	my $table_key=shift or croak("usage : is_dirty_key(table_name, table_key");
	my $table_source=shift;
	
	# check in current object
	if (exists $self->{memory_cache}->{$table_name}) {
		# if source provided, we check only for this table
		if ($table_source) {
			if (exists $self->{memory_cache}->{$table_name}->{$table_source}->{$table_key}) {
				return $self->{memory_cache}->{$table_name}->{$table_source}->{$table_key};
			}
		}
		# else we check for all table
		else {
			my $counter=0;
			my %tables=%{$self->{memory_cache}->{$table_name}};
			foreach my $source_name (keys %tables) {
			
				# only childs
				# exclude root_table
				if ($source_name eq $table_name or $self->{isip_env}->is_root_table($source_name)) {
					next;
				}
				
				if (exists $tables{$source_name}->{$table_key}) {
					$counter+=$tables{$source_name}->{$table_key};
				}
			}
			
			if (%tables) {
				return $counter;
			}
		}
	}
	
	# if key was not in dirty childs and table was preloaded, key is OK
	if ($self->{loaded_table}->{$table_name}) {
		return 0;
	}
	
	# check on disk	
	my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
	$table->query_field("TABLE_NAME","TABLE_SOURCE","TABLE_KEY","NUM_CHILD");
	
	my @condition;
	push @condition, "TABLE_NAME='$table_name' ";
	push @condition, "TABLE_KEY='$table_key'";
	push @condition, "TABLE_SOURCE <> '$table_name'";
	if ($table_source) {
		push @condition, " TABLE_SOURCE = '$table_source'";
	}
	
	$table->query_condition(@condition);
	
	my $count=0;
	while (my %row=$table->fetch_row) {
		# exclude root_table from result
		if (not $table_source or $table_source ne $row{TABLE_SOURCE}) {
			if ($self->{isip_env}->is_root_table($row{TABLE_SOURCE})) {
				next;
			}
		}
		$count += $row{NUM_CHILD};
	}

	if ($count > 0) {
		return $count;
	}
	else {
		return 0;
	}
}

sub is_dirty_table() {
	my $self=shift;
	
	my $table_name=shift or croak("usage : is_dirty_table(table_name)");
	

	# check on disk	
	my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
	#$table->query_condition("TABLE_NAME ='$table_name'","TABLE_KEY ='$table_key'");
	$table->query_field("TABLE_SOURCE","NUM_CHILD");
	
	my $cache_select="SELECT TABLE_SOURCE,sum(NUM_CHILD) as NUM_CHILD
			FROM CACHE_ICON
			WHERE TABLE_NAME='$table_name'
			GROUP BY TABLE_SOURCE";

	$table->custom_select_query($cache_select);
	
	my $count=0;
	while (my %row=$table->fetch_row) {
		# exclude root_table from result
		if ($table_name ne $row{TABLE_SOURCE} and $self->{isip_env}->is_root_table($row{TABLE_SOURCE})) {
			next;
		}
		$count += $row{NUM_CHILD} if $row{NUM_CHILD};
	}

	if ($count > 0) {
		return $count;
	}
	else {
		return 0;
	}
}

sub load_cache() {
	my $self=shift;
	
	my $table_name=shift or croak("usage : load_cache(table_name");
	
	# check on disk	
	my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
	#$table->query_condition("TABLE_NAME = '$table_name'","TABLE_SOURCE <> '$table_name'");
	$table->query_condition("TABLE_NAME = '$table_name'");
	
	my $count=0;
	while (my %row=$table->fetch_row) {
		$count ++;
		$self->{memory_cache}->{$table_name}->{$row{TABLE_SOURCE}}->{$row{TABLE_KEY}}=$row{NUM_CHILD};
	}
	
	$self->{loaded_table}->{$table_name}++;
	
	return $count
}


sub save_cache() {
	my $self=shift;
	
	return if not %{$self->{memory_cache}};

	my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
	
	$table->begin_transaction();
	
	
	
	my %dirty_temp=%{$self->{memory_cache}};
	foreach my $dirty_table (keys %dirty_temp) {
		my %dirty_source=%{$dirty_temp{$dirty_table}};
		
		foreach my $table_source (keys %dirty_source) {
		my %dirty_keys=%{$dirty_source{$table_source}};
		
			foreach my $dirty_key ( keys %dirty_keys ) {
			
				$table->query_condition("TABLE_NAME = '$dirty_table'","TABLE_KEY = '$dirty_key'","TABLE_SOURCE = '$table_source'");
				
				my $num_child;
				while (my %row=$table->fetch_row()) {
					$num_child=$row{NUM_CHILD};
				}
				
				my $memory_num_child=0;
				if (exists $dirty_keys{$dirty_key}) {
					$memory_num_child=$dirty_keys{$dirty_key};
				}
				
				if (not defined $num_child) {
					if ($memory_num_child > 0) {
						$logger->debug("insert $dirty_table,$dirty_key,memory_num_child");
						$table->insert_row(TABLE_NAME => $dirty_table, TABLE_KEY => $dirty_key, NUM_CHILD => $memory_num_child, TABLE_SOURCE => $table_source);
					}
				}
				else {
					my $sum_dirty=$num_child+$memory_num_child;
					if ($sum_dirty > 0) {
						$logger->debug("insert $dirty_table,$dirty_key,$num_child+$dirty_keys{$dirty_key}");
						$table->update_row(TABLE_NAME => $dirty_table, TABLE_KEY => $dirty_key, NUM_CHILD => $sum_dirty, TABLE_SOURCE => $table_source);
					}
					else {
						$logger->debug("remove $dirty_table,$dirty_key");
						$table->delete_row(TABLE_NAME => $dirty_table, TABLE_KEY => $dirty_key, TABLE_SOURCE => $table_source);
					
					}
				}
			}
		}
	}
	
	$table->commit_transaction();
	
	# flush memroy
	$self->{memory_cache}={};

}

sub clear_cache() {
	my $self=shift;
	
	my @tables=@_;
	
	my $where_condition="";
	$where_condition=" WHERE ".join(" OR ",map {"TABLE_NAME = '$_'"} @tables) if @tables;

	my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
	$table->execute("DELETE from CACHE_ICON".$where_condition);
}

1;
