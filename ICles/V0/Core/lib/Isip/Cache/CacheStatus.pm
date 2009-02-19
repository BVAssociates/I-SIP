package CacheStatus;

use Isip::Cache::CacheInterface;
use base 'CacheInterface';
use fields qw(dirty_value);

use strict;

use Isip::IsipLog '$logger';

sub new() {
	my $proto = shift;
    my $class = ref($proto) || $proto;

	my $self = fields::new($class);
	$self=$self->SUPER::new(@_);
	
	# store list of table keys whom childs are dirty (diff or uncommented...)
	# format :
	#   $self->{memory_cache}->{"table_name"}={key1 => 6, key2 => 2}
	
	$self->{dirty_value}=1;
	
	return $self;
}

sub set_status() {
	my $self=shift;
	$self->{dirty_value}=shift;
}

sub add_row() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift or croak("usage: set_dirty_key(table_name,key_string [, value])");
	
	$logger->info("add $key_string in CacheStatus");
	$self->{memory_cache}->{$table_name}->{$key_string} += $self->{dirty_value};
}

sub is_dirty() {
	my $self=shift;
	
	my $table_name=shift;
	my $table_key=shift or croak("usage : is_dirty(table_name, table_key");


	
	# check in current object
	if (exists $self->{memory_cache}->{$table_name}->{$table_key}) {
		return $self->{memory_cache}->{$table_name}->{$table_key};
	}
	
	# if key was not in dirty childs and table was preloaded, key is OK
	if ($self->{loaded_table}->{$table_name}) {
		return 0;
	}
	
	# check on disk	
	my $table=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	$table->query_condition("TABLE_NAME ='$table_name'","TABLE_KEY ='$table_key'");
	
	my $count=0;
	while (my %row=$table->fetch_row) {
		$count += $row{NUM_CHILD};
	}

	return $count;
}


sub load_cache() {
	my $self=shift;
	
	my $table_name=shift or croak("usage : preload(table_name");
	
	# check on disk	
	my $table=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	$table->query_condition("TABLE_NAME ='$table_name'");
	
	my $count=0;
	while (my %row=$table->fetch_row) {
		$count ++;
		$self->{memory_cache}->{$table_name}->{$row{TABLE_KEY}}=$row{NUM_CHILD};
	}
	
	$self->{loaded_table}->{$table_name}++;
	
	return $count
}


sub save_cache() {
	my $self=shift;

	my $table=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	
	$table->begin_transaction();
	
	
	
	my %dirty_temp=%{$self->{memory_cache}};
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
	$self->{memory_cache}={};

}

sub clear_cache() {
	my $self=shift;

	my $table=$self->{isip_env}->open_cache_table("CHILD_TO_COMMENT");
	$table->execute("DELETE from CHILD_TO_COMMENT");
}

1;