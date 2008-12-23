package IsipDiff;


use Carp qw(carp croak );
use strict;

##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self={};
	
	# mandatory parameter
	if (@_ < 0) {
		croak ('\'open\' take 0 mandatory argument: ${class}->open( [ { debug => $num} ) ] )')
	}
	my $options=shift;
	
	# comparison variables
	$self->{diff_source_update} = {};
	$self->{diff_target_only} = {};
	$self->{diff_source_only} = {};
	$self->{diff_target_only_field} = {};
	$self->{diff_source_only_field} = {};
	
	$self->{diff_nb}=0;
	
	# Constants
	$self->{avaiable_status} = {NEW => "nouveau", UPDATE => "modifie", OK => "valide", DELETE => "supprime"};

	bless ($self, $class);
	
    return $self;
}

##################################################
##  pivate methods  ##
##################################################

# simple debug method
sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:HISTO.".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

##################################################
##  public methods  ##
##################################################

sub report_diff() {
	my $self=shift;
	
	die "TODO";
	
	
	my $delete_key_nb=scalar(keys %{$self->{diff_target_only}} );
	$self->_info("$delete_key_nb row deleted");
	
	#my $new_key_nb=grep { $_ < 0 } values %seen_keys;
	#$self->_info("$new_key_nb row added");

	#$self->_info(scalar @{ $self->{diff_target_only_field} } ,"field added");
	
	#$self->_info(scalar keys %{ $self->{diff_source_update} } ,"row updated");
}

sub add_source_update() {
	my $self=shift;
	
	my $key=shift;
	my $field=shift;
	my $data=shift;
	croak("add_update take 2 arg : \$key,\$field,\$data") if not defined $data;
	
	$self->{diff_nb} ++;
	$self->{diff_source_update}->{$key}{$field} = $data;
}

sub get_source_update() {
	my $self=shift;
	
	return %{ $self->{diff_source_update} };
}

sub get_row_status() {
	my $self=shift;
	
	my $key=shift or croak("usage : line_get_status(key)");
	
	my $return_status="OK";
	
	if (exists $self->{diff_target_only}->{$key}) {
		$return_status="NEW";
	}
	elsif (exists $self->{diff_source_only}->{$key}) {
		$return_status="DELETE";
	}
	elsif (exists $self->{diff_source_update}->{$key}) {
		$return_status="UPDATE";
	}
	
	return $self->{avaiable_status}->{$return_status};
}

sub add_target_only() {
	my $self=shift;
	
	my $key=shift;
	my $data=shift or croak("add_target_only take 2 arg : $key,{\%data}");
	croak("2nd arg must be a ref on HASH") if ref($data) ne "HASH";
	
	$self->{diff_nb} += scalar(keys %{$data});
	$self->{diff_target_only}->{$key} = $data;
}

sub get_target_only() {
	my $self=shift;
	
	return %{ $self->{diff_target_only} };
}

sub get_target_only_by_key() {
	my $self=shift;
	
	my $key=shift or croak("get_target_only_key take 1 arg : \$key");
	
	if (not exists $self->{diff_target_only}->{$key}) {
		return {};
	}
	else {
		return %{ $self->{diff_target_only}->{$key} };
	}
}

sub add_source_only() {
	my $self=shift;
	
	my $key=shift;
	my $data=shift or croak("add_source_only take 2 arg : $key,{\%data}");
	croak("2nd arg must be a ref on HASH") if ref($data) ne "HASH";
	
	$self->{diff_nb} += scalar(keys %{$data});

	$self->{diff_source_only}->{$key} = $data
}

sub get_source_only() {
	my $self=shift;
	
	return %{ $self->{diff_source_only} };
}

sub get_source_only_by_key() {
	my $self=shift;
	
	my $key=shift or croak("get_source_only_key take 1 arg : \$key");
	
	return %{ $self->{diff_source_only}->{$key} };
}

sub add_target_only_field() {
	my $self=shift;
	
	my $field=shift;

	$self->{diff_nb}++;
	$self->{diff_target_only_field}->{$field}++;
}

sub get_target_only_field() {
	my $self=shift;
	
	return keys %{$self->{diff_target_only_field}};
}

sub add_source_only_field() {
	my $self=shift;
	
	my $field=shift;

	$self->{diff_nb}++;
	$self->{diff_source_only_field}->{$field}++;
}

sub get_source_only_field() {
	my $self=shift;
	
	return keys %{$self->{diff_source_only_field}};
}

sub count() {
	my $self=shift;
	
	return $self->{diff_nb};
}

1;

=head1 NAME

 IKOS::DATA::TableDiff - Class to store diff between 2 tables
 
=head1 SYNOPSIS

Class to store diff between 2 tables.

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut