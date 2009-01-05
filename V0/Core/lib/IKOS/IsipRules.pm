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


#create a new IsipRules object
sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self={};
	
	# member initializations
	$self->{current_type}={};
	$self->{current_owner}={};
	$self->{current_description}={};
	
	$self->{debugging}=0;
	
	# constants identifiers enumeration
	# TODO : import them from a configuration file
	%{ $self->{type} } = $class->enum_type;
	%{ $self->{field_status} } = $class->enum_field_status;
	%{ $self->{line_status} } = $class->enum_line_status;
	
	%{ $self->{field_diff_status} } = $class->enum_field_diff_status;
	%{ $self->{line_diff_status} } = $class->enum_line_diff_status;


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
	$self->_init_info();

    return $self;
}



##################################################
##  pivate methods  ##
##################################################

# load the table TABLE_INFO and get type of each column
sub _init_info() {
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

sub debugging {
    my $self = shift;
    if (@_) { $self->{debugging} = shift}
    return $self->{debugging};
}

# simple debug method
sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:RULES.".$self->{table_name}.":".join(' ',@_)."\n" if $self->debugging();
}

##################################################
##  static methods to get constants enumeration ##
##################################################

sub enum_type () {
	my $self=shift;
	
	return ("fonctionnel","technique","manuel","administratif","exclus");
}


sub enum_field_status () {
	my $self=shift;
	
	return (EMPTY => "nouveau",  OK => "valide", TEST => "test", SEEN => "acquite", UNKNOWN => "inconnu", HIDDEN => "cache");
}

sub enum_line_status () {
	my $self=shift;
	
	return (EMPTY => "nouveau",  OK => "valide", TEST => "test", SEEN => "acquite", UNKNOWN => "inconnu");
}

sub enum_field_diff_status() {
	my $self=shift;
	
	return {NEW => "nouveau", UPDATE => "modifie", OK => "valide", DELETE => "supprime"}
}

sub enum_line_diff_status() {
	my $self=shift;
	
	return {NEW => "nouveau", UPDATE => "modifie", OK => "valide", DELETE => "supprime"}
}

##################################################
##  methods to compute status from a Histo line ##
##################################################

sub get_field_type() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_type(col_name)");
	
	return $self->{current_type}->{$col_name};
}

sub get_field_description() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_type(col_name)");
	
	return $self->{current_description}->{$col_name};
}

# compute the validation status of a field
# param type : type of the field
# param status : current status from histo
# param comment : current comment from histo
# return status : new computed status
sub get_field_status () {
	my $self=shift;
	
	my $name=shift;
	my $status=lc shift;
	my $comment=shift;
	
	my $type=$self->get_field_type($name);
	
	my %status_by_name= reverse %{$self->{field_status}};
	
	# new status
	my $return_status;
	
	if ($type eq "Administratif") {
	# "Administratif always OK
		$return_status=$self->{field_status}{OK};
		#$return_status=$self->{field_status}{HIDDEN};
	}
	elsif ($type eq "exclus") {
		$return_status=$self->{field_status}{HIDDEN};
	}
	elsif ($status eq "") {
		$return_status=$self->{field_status}{EMPTY};
	}
	else {
	# other are returned as is
		$return_status=$self->{field_status}{$status_by_name{$status}};
		$return_status="ERROR" if not exists $status_by_name{$status};
	}
	
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


