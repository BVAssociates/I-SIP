package CacheInterface;
use fields qw(isip_env memory_cache loaded_table);

use strict;

use Carp qw(carp croak );
use Scalar::Util qw(blessed);

use Isip::IsipLog '$logger';

sub new() {
	my $proto = shift;
    my $class = ref($proto) || $proto;

	my $self = fields::new($class);
	
	$self->{isip_env} = shift or croak("usage: CacheInterface->new(env_ref)");
	
	$self->{loaded_table}={};
	
	return $self;
}


sub add_row_cache() {
	croak("cannot use CacheInterface");
}

sub remove_row_cache() {
	croak("cannot use CacheInterface");
}

sub is_dirty_key() {
	croak("cannot use CacheInterface");
}


sub load_cache() {
	croak("cannot use CacheInterface");
}


sub save_cache() {
	croak("cannot use CacheInterface");
}

sub clear_cache() {
	croak("cannot use CacheInterface");
}

1;