package CacheProject;

use Isip::Cache::CacheInterface;
use base 'CacheInterface';
use fields qw(dirty_project current_project);

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
	#   $self->{memory_cache}->{"table_name"}={key1 => {"project 1" => 1}, key2 => {"project 3" => 1}}
	
	$self->{dirty_project}=undef;
	$self->{current_project}={};
	
	return $self;
}

sub set_dirty_project() {
	my $self=shift;
	
	$self->{dirty_project}=shift or croak("usage: set_dirty_project(project)");
}

sub check_before_cache() {
	my $self=shift;
	
	my $table=shift;
	my $value_ref=shift;
	$self->{current_project}=[];
	my @new_project=split(',',$value_ref->{PROJECT}) if $value_ref->{PROJECT};
	my @old_project=split(',',$value_ref->{OLD_PROJECT}) if $value_ref->{OLD_PROJECT};
	
	my %project_action;
	
	if (@new_project) {
		if (@old_project) {
			#exclusion of 2 lists
			my %seen_project;
			foreach (@old_project) {
				$seen_project{$_}--;
			}
			foreach (@new_project) {
				$seen_project{$_}++;
			}
			foreach (keys %seen_project) {
				# add only project which appear one time
				$project_action{$_}=$seen_project{$_} if $seen_project{$_} != 0;
			}
		}
		else {
			%project_action = map {$_ => 1} @new_project;
		}
	}
	
	$self->{current_project}=\%project_action;
	
	return scalar keys(%project_action);
}

sub add_row_cache() {
	my $self=shift;
	
	my $table_name=shift;
	my $key_string=shift;
	my $value_ref=shift or croak("usage: add_row_cache(table_name,key_string , {PROJECT => value})");
	
	$logger->info("add $key_string in CacheProject");
	
	my %current_project=%{$self->{current_project}};
	foreach my $proj (keys %current_project) {
		my $old_value = $self->{memory_cache}->{$table_name}->{$key_string}->{$proj};
		$old_value=0 if not defined $old_value;
		
		my $new_value= $old_value + $current_project{$proj};

		$self->{memory_cache}->{$table_name}->{$key_string}->{$proj} = $new_value;
	}
}

sub is_dirty_key() {
	my $self=shift;
	
	my $table_name=shift;
	my $table_key=shift or croak("usage : is_dirty_key(table_name, table_key");

	if (not defined $self->{dirty_project}) {
		croak("project must be defined by set_dirty_project(project)");
	}
		
	my $project=$self->{dirty_project};
	
	# check in current object
	if (exists $self->{memory_cache}->{$table_name}->{$table_key}->{$project}) {
		my $return=$self->{memory_cache}->{$table_name}->{$table_key}->{$project};
		if ($return > 0 ) {
			return $return;
		}
		else {
			return 0;
		}
	}
	
	# if key was not in dirty childs and table was preloaded, key is OK
	if ($self->{loaded_table}->{$table_name}) {
		return 0;
	}
	
	# check on disk	
	my $table=$self->{isip_env}->open_cache_table("PROJECT_CACHE");
	$table->query_condition("TABLE_NAME ='$table_name'",
							"TABLE_KEY ='$table_key'",
							"PROJECT_CHILD ='$project'");
	
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
	my $table=$self->{isip_env}->open_cache_table("PROJECT_CACHE");
	$table->query_condition("TABLE_NAME ='$table_name'");
	
	my $count=0;
	while (my %row=$table->fetch_row) {
		$count ++;
		$self->{memory_cache}->{$table_name}->{$row{TABLE_KEY}}->{$row{PROJECT_CHILD}}=$row{NUM_CHILD};
	}
	
	$self->{loaded_table}->{$table_name}++;
	
	return $count
}


sub save_cache() {
	my $self=shift;

	return if not $self->{memory_cache};
	
	my $table=$self->{isip_env}->open_cache_table("PROJECT_CACHE");
	
	$table->begin_transaction();
	
	
	# foreach table
	my %dirty_temp=%{$self->{memory_cache}};
	foreach my $dirty_table (keys %dirty_temp) {
		my %dirty_keys=%{$dirty_temp{$dirty_table}};
		
		#foreach key
		foreach my $dirty_key ( keys %dirty_keys ) {
			my %projets=%{$dirty_keys{$dirty_key}};

			# foreach project
			foreach my $project (keys %projets) {
				$table->query_condition("TABLE_NAME = '$dirty_table'",
										"TABLE_KEY = '$dirty_key'",
										"PROJECT_CHILD = '$project'");
				
				my $num_child;
				while (my %row=$table->fetch_row()) {
					$num_child=$row{NUM_CHILD};
				}
				
				my $memory_num_child=0;
				if (exists $projets{$project}) {
					$memory_num_child=$projets{$project};
				}
				
				if (not defined $num_child) {
					if ($memory_num_child > 0) {
						$logger->debug("insert $dirty_table,$dirty_key,memory_num_child");
						$table->insert_row(TABLE_NAME => $dirty_table,
										TABLE_KEY => $dirty_key,
										PROJECT_CHILD => $project,
										NUM_CHILD => $memory_num_child);
					}
				}
				else {
					my $sum_dirty=$num_child+$memory_num_child;
					if ($sum_dirty > 0) {
						$logger->debug("insert $dirty_table,$dirty_key,$sum_dirty");
						$table->update_row(TABLE_NAME => $dirty_table,
										TABLE_KEY => $dirty_key,
										PROJECT_CHILD => $project,
										NUM_CHILD => $sum_dirty);
					}
					else {
						$logger->debug("remove $dirty_table,$dirty_key");
						$table->delete_row(TABLE_NAME => $dirty_table,
										PROJECT_CHILD => $project,
										TABLE_KEY => $dirty_key);
					
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
	
	my $table=$self->{isip_env}->open_cache_table("PROJECT_CACHE");
	$table->execute("DELETE from PROJECT_CACHE".$where_condition);
}

1;