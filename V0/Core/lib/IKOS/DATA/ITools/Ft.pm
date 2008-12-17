package Ft;
#require IKOS::DATA::ITools::Legacy;
@ISA = ("Legacy");

1;  # so the require or use succeeds

=head1 NAME

IKOS::DATA::ITools::Ft is a wrapper class to ITools::DATA::ITools::Legacy

=head1 SYNOPSIS

 See IKOS::DATA::ITools::Legacy
=cut
 
 
 
package Legacy;
require IKOS::DATA::abstract::ITools_interface;
@ISA = ("ITools_interface");

use IKOS::DATA::ITools::Define;

use Carp qw(carp cluck confess croak );
use Data::Dumper;
use strict;

##################################################
##  constructor  ##
##################################################

# new("table_name")
sub open (){
    my $proto = shift;
    my $class = ref($proto) || $proto;

	# mandatory parameter
	if (@_ < 1) {
		croak ("'new' take 1 argument")
	}
	
	# call the base constructor
    my $self  = $class->SUPER::open(@_);
	
	# add private members
	$self->{select_descriptor} = undef;
	
    bless($self, $class);
    return $self;
}


##############################################
## Virtual methods provided by Interface       ##
##############################################

# print the query being processed (ITools like syntax)
sub get_query()
{
	my $self = shift;
	
	my $query;
	$query = "Select -s ".join(', ',$self->query_field())." FROM ".$self->table_name();
	$query = $query." WHERE ".join(' AND ',$self->query_condition()) if $self->query_condition()	!= 0;
	$query = $query." ORDER_BY ".join(', ',$self->query_sort()) if $self->query_sort()	!= 0;
	
	return $query;
}

# open pipe on a Itools Select command
sub _open_select() {
	my $self = shift;
	
	my $select_command=$self->get_query();
	$self->_debug("Exec ITools : ",$select_command);
	## Core::open != $self->open
	CORE::open($self->{select_descriptor},"$select_command |") or croak "Error running $select_command : $!";
	#print STDERR "DEBUG: Opening $select_command |\n";
}

sub _close_select() {
	my $self = shift;
	close $self->{select_descriptor};
	$self->{select_descriptor}= undef;
	$self->_debug("Closing select_descriptor");
	return 1;
}

# get row one by one based on query
#return array
sub fetch_row_array()
{
	my $self = shift;
	my $separator=$self->output_separator();
	my @temp_return;
	
	$self->_open_select() if not defined $self->{select_descriptor};
	my $select_output=readline($self->{select_descriptor});
	$self-> _close_select() if not defined $select_output;
	
	if (defined $select_output) {
		chomp $select_output;
		
		@temp_return=split($separator,$select_output);
		
		## ITools BUG : don't return end separators if fields are NULL
		my $field_num_diff=0;
		$field_num_diff= $self->query_field() - @temp_return if @temp_return;
		if ( $field_num_diff != 0 ) {
			push  @temp_return, (undef) x $field_num_diff;
		}
		##
	}
	
	return @temp_return
}


# abort current request being processed
sub finish() {
	my $self = shift;
	
	$self-> _close_select() if defined $self->{select_descriptor}
}



1;  # so the require or use succeeds


=head1 NAME

ITools::Table::Legacy - class to use an ITools Table with the standard ITools Executables

=head1 SYNOPSIS

 use ITools::Table::Legacy;

 #################
 # class methods #
 #################
 
 $ob    = Table->open("table_name");

 #######################
 # object data methods #
 #######################

 $ob->fields("toto","tata");
 print join(',',$ob->fields);
 
 $ob->conditions("toto > 0");
 print  join(',',$ob->conditions);

 ########################
 # other object methods #
 ########################

 while ( @line=$ob->fetch_row_array() ) {
	print "field0: ".$line[0];
 }

=head1 DESCRIPTION

An Itools Table...

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut