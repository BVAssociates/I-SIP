package CacheStatusDismiss;

use Isip::Cache::CacheInterface;
use base 'CacheInterface';
use fields qw(
		table_source_numchild
	);

use strict;
use Carp qw(carp croak );

use Isip::IsipLog '$logger';

sub new() {
	my $proto = shift;
    my $class = ref($proto) || $proto;

	my $self = fields::new($class);
	$self=$self->SUPER::new(@_);
	
	$self->{table_source_numchild}= {};
	
	return $self;
}

sub check_before_cache() {
	my $self=shift;
	
	my $table_name=shift;
	my $value_ref=shift;
	
	# reset current action
	$self->{table_source_numchild}= {};
	
	my @key=$self->{isip_env}->get_table_key($table_name);
	my $key_string=join(',',@{$value_ref}{@key});
	
	my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
	$table->query_condition(
					"TABLE_NAME = ".$table->quote($table_name),
					"TABLE_KEY = ".$table->quote($key_string)
				);
	
	my $dirty=0;
	while(my %row=$table->fetch_row()) {
		$self->{table_source_numchild}->{$row{TABLE_SOURCE}} = $row{NUM_CHILD};
		$dirty += $row{NUM_CHILD};
	}
	

	return $dirty;
}


#####################################

sub add_row_cache() {
	my $self=shift;
	
	return if not $self->{table_source_numchild};
	
	my $table_name=shift;
	my $key_string=shift;
	my $value_ref=shift or croak("usage: add_row_cache(table_name,key_string , value)");
	
	my %numchild_for_source_table = %{ $self->{table_source_numchild} };
	
	foreach my $source_table ( keys %numchild_for_source_table ) { 
	
		# rien à faire
		next if not $numchild_for_source_table{$source_table};
	
		my $table=$self->{isip_env}->open_cache_table("CACHE_ICON");
		$table->query_condition(
						"TABLE_NAME = ".$table->quote($table_name),
						"TABLE_KEY = ".$table->quote($key_string),
						"TABLE_SOURCE = ".$table->quote($source_table),
					);
		
		my %row=$table->fetch_row();
		
		my $old_value = $row{NUM_CHILD};
		my $new_value = $old_value - $numchild_for_source_table{$source_table};
		
		if ( $new_value ) {
			$table->execute("UPDATE CACHE_ICON SET NUM_CHILD = $new_value"
								." WHERE TABLE_NAME = ".$table->quote($table_name)
								." AND TABLE_KEY = ".$table->quote($key_string)
								." AND TABLE_SOURCE = ".$table->quote($source_table)
								);
		}
		else {
			$table->execute("DELETE FROM CACHE_ICON"
								." WHERE TABLE_NAME = ".$table->quote($table_name)
								." AND TABLE_KEY = ".$table->quote($key_string)
								." AND TABLE_SOURCE = ".$table->quote($source_table)
								);
		}
		
		$logger->info("add $table_name:$key_string in CacheStatusDismiss : $old_value->$new_value");
	}
}

sub save_cache {
	my $self=shift;
	
	$logger->info("Cache déjà sauvegardé");
}

1;