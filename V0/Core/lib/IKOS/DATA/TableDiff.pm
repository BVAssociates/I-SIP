package TableDiff;


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
	$self->{diff_update} = {};
	$self->{diff_new} = {};
	$self->{diff_delete} = {};
	$self->{diff_new_field} = [];
	$self->{diff_delete_field} = {};
	
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
	print STDERR "DEBUG:HISTO.".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

##################################################
##  public methods  ##
##################################################

sub report_info() {
	my $self=shift;
	
	die "TODO";
	
	
	my $delete_key_nb=scalar(keys %{$self->{diff_new}} );
	$self->_info("$delete_key_nb row deleted");
	
	#my $new_key_nb=grep { $_ < 0 } values %seen_keys;
	#$self->_info("$new_key_nb row added");

	#$self->_info(scalar @{ $self->{diff_new_field} } ,"field added");
	
	#$self->_info(scalar keys %{ $self->{diff_update} } ,"row updated");
}

sub add_update() {
	my $self=shift;
	
	my $key=shift;
	my $field=shift;
	my $data=shift or croak("add_update take 2 arg : \$key,\$field,\$data");
	
	$self->{diff_nb} ++;
	$self->{diff_update}{$key} = $data;
}

sub get_update() {
	my $self=shift;
	
	return %{ $self->{diff_update} };
}

sub add_new() {
	my $self=shift;
	
	my $key=shift;
	my $data=shift or croak("add_new take 2 arg : $key,{\%data}");
	croak("2nd arg must be a ref on HASH") if ref($data) ne "HASH";
	
	$self->{diff_nb} += scalar(keys %{$data});
	$self->{diff_new}{$key} = $data;
}

sub get_new() {
	my $self=shift;
	
	return %{ $self->{diff_new} };
}

sub add_delete() {
	my $self=shift;
	
	my $key=shift;
	my $data=shift or croak("add_delete take 2 arg : $key,\%data");
	croak("add_update take 2 arg : $key,\%data") if ref($data) ne "HASH";
	
	$self->{diff_nb} += scalar(keys %{$data});
	$self->{diff_delete}{$key} = $data;
}

sub get_delete() {
	my $self=shift;
	
	return %{ $self->{diff_delete} };
}


sub add_new_field() {
	my $self=shift;
	
	my $field=shift;
	
	# TODO : a verifier
	push @{$self->{diff_new_field}}, $field;
}

sub get_new_field() {
	my $self=shift;
	
	return @{ $self->{diff_new_field} };
}


1;

=head1 NAME

 IKOS::DATA::TableDiff - Class to store diff between 2 tables
 
=head1 SYNOPSIS

Class to store diff between 2 tables.
