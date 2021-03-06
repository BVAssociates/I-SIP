package CacheDiff;

use Isip::Cache::CacheInterface;
use base 'CacheInterface';
use fields qw(action current_table current_diff);

use strict;
use Carp qw(carp croak );

use Isip::IsipLog '$logger';

sub new() {
	my $proto = shift;
    my $class = ref($proto) || $proto;

	my $environnement_ref=shift;
	
	my $self = fields::new($class);
	$self=$self->SUPER::new($environnement_ref);
	
	my $source=shift;
	my $target=shift or croak("usage CacheDiff->new(env_ref,current_source,current_target)");
	$self->{current_diff}=$source."_".$target;
	
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
	if ($value_ref->{ICON}) {
		if ($value_ref->{OLD_ICON}) {
			if ($value_ref->{ICON} ne $value_ref->{OLD_ICON} ) {
				$self->{action}=1 if $value_ref->{OLD_ICON} eq 'valide';
				$self->{action}=-1 if $value_ref->{ICON} eq 'valide';
			}
		}
		else {
			$self->{action}=1 if $value_ref->{ICON} !~ /^valide|exclus$/;
		}
	}
		
	my $dirty=abs($self->{action});
	$self->{current_table}=$table if $dirty;
	
	return $dirty;
}

sub add_row_cache() {
	my $self=shift;
	
	return if not $self->{action};
	
	my $table_name=shift;
	my $key_string=shift;
	my $value_ref=shift or croak("usage: add_row_cache(table_name,key_string , value)");
	
	
	$logger->info("add $key_string in CacheStatus");
	my $table_fired=$self->{current_table};
	
	my $old_value=$self->{memory_cache}->{$table_name}->{$table_fired}->{$key_string};
	$old_value=0 if not defined $old_value;
	
	my $new_value=$old_value + $self->{action};
	if ($new_value > 0) {
		$self->{memory_cache}->{$table_name}->{$table_fired}->{$key_string} = $new_value;
	}
	else {
		$self->{memory_cache}->{$table_name}->{$table_fired}->{$key_string} = 0;
	}
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
			my $found=0;
			my $counter=0;
			my %tables=%{$self->{memory_cache}->{$table_name}};
			foreach my $source_name (keys %tables) {
				if (exists $self->{memory_cache}->{$table_name}->{$source_name}->{$table_key}) {
					$found=1;
					$counter+=$self->{memory_cache}->{$table_name}->{$source_name}->{$table_key};
				}
			}
			
			if ($found) {
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
	#$table->query_condition("TABLE_NAME ='$table_name'","TABLE_KEY ='$table_key'");
	$table->custom_select_query("SELECT TABLE_NAME,TABLE_KEY, sum(NUM_CHILD) as NUM_CHILD
			FROM CACHE_ICON
			GROUP BY TABLE_NAME,TABLE_KEY
			WHERE
			TABLE_NAME="$table->quote($table_name)."
			AND TABLE_KEY="$table->quote($table_key));
	
	my $count=0;
	while (my %row=$table->fetch_row) {
		$count += $row{NUM_CHILD};
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
	$table->query_condition("TABLE_NAME =".$table->quote($table_name));
	
	my $count=0;
	while (my %row=$table->fetch_row) {
		$count ++;
		$self->{memory_cache}->{$table_name}->{$row{SOURCE}."_".$row{TARGET}}->{$row{TABLE_KEY}}=$row{NUM_CHILD};
	}
	
	$self->{loaded_table}->{$table_name}++;
	
	return $count
}


sub save_cache() {
	my $self=shift;
	
	return if not $self->{memory_cache};

	my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
	
	$table->begin_transaction();
	
	
	
	my %dirty_temp=%{$self->{memory_cache}};
	foreach my $dirty_table (keys %dirty_temp) {
		my %dirty_source=%{$dirty_temp{$dirty_table}};
		
		foreach my $table_source (keys %dirty_source) {
		my %dirty_keys=%{$dirty_source{$table_source}};
		
			foreach my $dirty_key ( keys %dirty_keys ) {
			
				$table->query_condition("TABLE_NAME = ".$table->quote($dirty_table),
							"TABLE_KEY = ".$table->quote($dirty_key),
							"TABLE_SOURCE = ".$table->quote($table_source),
						);
				
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
	
	my $table_condition = "(".join(" OR ",map {"TABLE_NAME = '$_'"} @tables).")" if @tables;
	my $env_condition = "(SOURCE = $self->{current_source} AND TARGET = $self->{current_target})";
	
	my $where_condition=" WHERE ".$env_condition;
	$where_condition .= " AND ".$table_condition if $table_condition;

	my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
	$table->execute("DELETE from CACHE_ICON".$where_condition);
}

1;