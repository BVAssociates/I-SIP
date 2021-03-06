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

	bless ($self, $class);
	
    return $self;
}

##################################################
##  pivate methods  ##
##################################################

# simple debug method
sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:DIFF.".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
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
	if (@_) {
		my $key=shift;
		return () if not exists $self->{diff_source_update}->{$key};
		return %{ $self->{diff_source_update}->{$key} }
	}
	return %{ $self->{diff_source_update} };
}

# calcul l'etat d'une ligne en fonction de l'etat de ses champs

sub get_field_status() {
	my $self=shift;
	
	my $key=shift or croak("usage : line_get_status(key,field)");
	my $field=shift or croak("usage : line_get_status(key,field)");
	
	my $return_status="OK";
	
	if (exists $self->{diff_target_only}->{$key}) {
		$return_status="NEW";
	}
	elsif (exists $self->{diff_source_only}->{$key}) {
		$return_status="DELETE";
	}
	elsif (exists $self->{diff_source_update}->{$key}->{$field}) {
		$return_status="UPDATE";
	}
	
	return $return_status;
}

sub add_target_only() {
	my $self=shift;
	
	my $key=shift;
	my $data=shift or croak("add_target_only take 2 arg : $key,{\%data}");
	croak("2nd arg must be a ref on HASH") if ref($data) ne "HASH";
	
	$self->{diff_nb} += scalar(keys %{$data});
	$self->{diff_target_only}->{$key} = $data;
}

# return data that are only on target
# arg1 : primary key
# return : a hash of line (hash)
# return : a line (hash) if arg1
sub get_target_only() {
	my $self=shift;
	if (@_) {
		my $key=shift;
		return () if not exists $self->{diff_target_only}->{$key};
		return %{ $self->{diff_target_only}->{$key} }
	}
	return %{ $self->{diff_target_only} };
}


sub add_source_only() {
	my $self=shift;
	
	my $key=shift;
	my $data=shift or croak("add_source_only take 2 arg : $key,{\%data}");
	croak("2nd arg must be a ref on HASH") if ref($data) ne "HASH";
	
	$self->{diff_nb} += scalar(keys %{$data});

	$self->{diff_source_only}->{$key} = $data
}

# return data that are only on source
# arg1 : primary key
# return : a hash of line (hash)
# return : a line (hash) if arg1
sub get_source_only() {
	my $self=shift;
	if (@_) {
		my $key=shift;
		return () if not exists $self->{diff_source_only}->{$key};
		return %{ $self->{diff_source_only}->{$key} };
	}
	return %{ $self->{diff_source_only} };
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

 ITable::TableDiff - Class to store diff between 2 tables
 
=head1 SYNOPSIS

Class to store diff between 2 tables.

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

=cut