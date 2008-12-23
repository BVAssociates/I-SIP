package IsipRules;


use Carp qw(carp croak );
use strict;

use IKOS::DATA::Sqlite;

=head1 NAME

 IKOS::IsipRules - Class to handle type and status
 
=head1 SYNOPSIS

Class to handle type and status.

=author

BV Associates, 2008

=cut

##################################################
##  constructor  ##
##################################################


# open an existing table on a Sqlite Database
sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self={};
	
	# member initializations
	$self->{table_name};
	$self->{current_type}={};
	$self->{current_owner}={};
	$self->{current_description}={};
	
	$self->{debugging}=0;
	
	# constants identifiers enumeration
	# TODO : import them from a configuration file
	$self->{type} = ["fonctionnel","technique","manuel","administratif","securite"];
	$self->{field_status} = ["","aquite","test","valide","inconnu"];
	$self->{line_status} = ["nouveau","en_cours","valide","supprime"];

	# Amen
	bless ($self, $class);
	
	# mandatory parameter
	if (@_ < 2) {
		croak ('\'new\' take 1 mandatory argument: ${class}->new(database_name, table_name [, { diff => $TableDiff_ref, debug => \$num} ) ] )')
	}
	
	$self->{database_name}=shift;
	$self->{table_name}=shift;
	
	# To discuss : what table_name is passed to contructor?
	$self->{table_info_name}=$self->{table_name}."_INFO";
	
	# options
	my $options=shift;
	$self->{table_diff}=$options->{diff} if exists $options->{diff};
	$self->debugging($options->{debug}) if exists $options->{debug};


	# load informations
	$self->_init();

    return $self;
}



##################################################
##  pivate methods  ##
##################################################

sub _init() {
	my $self=shift;
	
	$self->load_table_info();
}

sub debugging {
    my $self = shift;
    if (@_) { $self->{debugging} = shift}
    return $self->{debugging};
}

# simple debug method
sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:Rules.".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

##################################################
##  methods to get constants enumeration ##
##################################################

sub enum_type () {
	my $self=shift;

	return @{$self->{type}};
}


sub enum_field_status () {
	my $self=shift;

	return @{$self->{field_status}};
}

sub enum_line_status () {
	my $self=shift;

	return @{$self->{line_status}};
}

##################################################
##  methods to get information of current state ##
##################################################

# load the table TABLE_INFO and get type of each column
sub load_table_info () {
	my $self=shift;
	
	my $table_info=Sqlite->open($self->{database_name},$self->{table_info_name}, { debug => $self->debugging() } );
	
	# narrow query if needed
	#$table_info->query_field("FIELD_NAME","DATE_UPDATE","DATA_TYPE","DATA_LENGTH","TABLE_SCHEMA","TEXT","DESCRIPTION","OWNER","TYPE");
	
	
	while(my %row=$table_info->fetch_row()) {
		$self->{current_type}->{$row{FIELD_NAME}}=$row{TYPE};
		$self->{current_owner}->{$row{FIELD_NAME}}=$row{OWNER};
		$self->{current_description}->{$row{FIELD_NAME}}=$row{TEXT};
	}
}

##################################################
##  methods to compute status from a Histo line ##
##################################################

sub get_type() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_type(col_name)");
	
	return $self->{current_type}->{$col_name};
}

sub get_description() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_type(col_name)");
	
	return $self->{current_description}->{$col_name};
}

# return the computed status of a field
#  - if set_diff has been called before, it will
# use it to return the "diff" status
#  - if no set_diff, return status  
# param type : type of the field
# param status : current status from histo
# param comment : current comment from histo
# return status : new computed status
sub get_field_status () {
	my $self=shift;
	
	my $type=shift;
	my $status=shift;
	my $comment=shift;
	
	# compute new status
	my $return_status;
	
	return $return_status;
}

# return the computed status of a line
#  - if set_diff has been called before, it will
# use it to return the "diff" status
#  - if no set_diff, return status  
# param status_list : list of status of each field
# return status : computed status
sub get_line_status () {
	my $self=shift;
	
	my @status_list=@_;
	
	my $return_status;
		
	return $return_status;
}

1;


