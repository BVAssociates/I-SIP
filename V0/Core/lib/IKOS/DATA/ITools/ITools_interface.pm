package ITools_interface;

use IKOS::DATA::ITools::Define;

use Carp qw(carp cluck confess croak );
use strict;

##################################################
##  constructor  ##
##################################################

# open($Define_obj)
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my $self  = {};

	# mandatory parameter
	if (@_ < 1) {
		croak ("'new' take 1 argument: new(\$define_obj)")
	}
    $self->{define} = shift;
	my $option = shift;

	# internal description overiding "define" members
	$self->{key} = [ $self->{define}->key ];
	$self->{field}= [ $self->{define}->field ];
	$self->{size}= { $self->{define}->size };
	$self->{not_null}= [ $self->{define}->not_null ];
	
	# user query
	## anon array reference of 
	$self->{query_field}  = [ @{ $self->{field} } ];
	$self->{query_condition} = [];
	$self->{query_sort}  = [];

	# computed values from "define" members
	$self->{output_separator}  = $self->{define}->separator;
	
	# debug
	$self->{debugging}=0;
	$self->{debugging}=$option->{debug} if exists $option->{debug};
	
    bless($self, $class);
		
    return $self;
}

##############################################
## accessor methods         ##
##############################################

##Read Only Values

sub name {
    my $self = shift;
    if (@_) { croak("'name' member is read-only") }
    return $self->{define}->{name};
}

sub type {
    my $self = shift;
    if (@_) { croak("'type' member is read-only") }
    return $self->{define}->{type};
}

sub field {
    my $self = shift;
    if (@_) { croak("'field' member is read-only") }
    return @{ $self->{field} };
}

sub key {
    my $self = shift;
    if (@_) { croak("'key' member is read-only") }
    return @{ $self->{key} };
}

sub sort {
    my $self = shift;
    if (@_) { @{ $self->{sort} } = @_ }
    return @{ $self->{sort} };
}

sub size {
    my $self = shift;
    if (@_) { @{ $self->{size} } = @_ }
    return %{ $self->{size} };
}

sub row {
    my $self = shift;
    if (@_) { croak("'type' member is read-only") }
    return $self->{define}->row ;
}

sub header {
    my $self = shift;
    if (@_) { croak("'header' member is read-only") }
    return $self->{define}->header;
}

##Query Values

sub output_separator {
    my $self = shift;
    if (@_) { $self->{output_separator} = shift }
    return $self->{output_separator};
}

sub query_field {
    my $self = shift;

	my @fields=@_;
	if (@fields) {
	if ( $self->has_fields(@fields) != @fields) {
			croak("error querying fields <@fields>");
		} else {
			$self->{query_field} =  [ @fields ];
		}
	}
	
	return @{ $self->{query_field} }
}

sub query_condition {
    my $self = shift;
    if (@_) { @{ $self->{query_condition} } = @_ }
    return @{ $self->{query_condition} };
}

sub query_sort {
    my $self = shift;
	
	my @fields=@_;
	if (@fields) {
	if ( $self->has_fields(@fields) != @fields) {
			croak("error whith sort fields <@fields>");
		} else {
			@{ $self->{query_sort} } =  @fields;
		}
	}
	
    return @{ $self->{query_sort} };
}

# return the Define object
sub define {
    my $self = shift;
    if (@_) { $self->{define} = shift }
    return $self->{define};
}

sub debugging {
    my $self = shift;
    if (@_) { $self->{debugging} = shift }
    return $self->{debugging};
}

##############################################
## Virtual methods provided by Interface       ##
##############################################

# get row  by one based on query
sub fetch_row_array()
{
	my $self = shift;

	croak("fetch_row_array() not implemented");
	return undef;
}


# get row on by one based on query
#return object
sub fetch_row()
{
	my $self = shift;
	
	my %row_object;
	my @row=$self->fetch_row_array();
	my @fields=$self->query_field();
	
	return () if @row == 0;
	
	# internal test
	die "fetch_row_array returned wrong number of values (got ".@row." instead of ".@fields.")" if  @row != @fields;

	for (my $i=0; $i < @fields; $i++) {
		$row_object{$fields[$i]}=$row[$i];
	}
	
	
	return %row_object;
}

# get information on Table's definition
sub describe()
{
	my $self = shift;
	
	return $self->{define}->describe();
}

##############################################
## methods to access Table data        ##
##############################################

sub has_fields() {
	my $self = shift;
	my @fields_requested = @_;
	my @field_found;
	
	foreach my $field (@fields_requested) {
		push (@field_found, grep {$field eq $_} $self->field) ;
	}
	return @field_found;
}


#SEP='§'@@FORMAT='Uid§Pid§PPid§Cpu§STime§Tty§Time§Command'@@ROW='$Uid§$Pid§$PPid§$Cpu§$STime§$Tty§$Time§$Command'@@SIZE='10s§7n§7n§4n§9s§7s§9s§40s'@@HEADER='Processus - ps -ef - de dyson.voisins.bvassociates.fr'@@KEY='Pid'
sub get_header() {
	my $self = shift;
	
	my @header;
	push (@header,"SEP='".$self->output_separator()."'");
	push (@header,"FORMAT='".join($self->output_separator(),$self->query_field() )."'");
	my %temp_row=$self->row();
	push (@header,"ROW='".join($self->output_separator(),  @temp_row{$self->query_field()} )."'");
	my %temp_size=$self->size();
	push (@header,"SIZE='".join($self->output_separator(), @temp_size{$self->query_field()} )."'");
	push (@header,"HEADER='".$self->header()."'");
	
	return join("@@", @header);
}

##############################################
## Private methods         ##
##############################################

sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:ITools.".$self->{define}->name().":".join(' ',@_)."\n" if $self->debugging();
}

1;  # so the require or use succeeds



=head1 NAME

ITools::Table - class to use an ITools Table 

=head1 SYNOPSIS

 use ITools::Table;

 #################
 # class methods #
 #################
 
 $ob    = Table->new("table_name");

 #######################
 # object data methods #
 #######################

 $ob->field("toto","tata");
 print join(',',$ob->field);
 
 $ob->conditions("toto > 0");
 print  join(',',$ob->conditions);

 ########################
 # other object methods #
 ########################


=head1 DESCRIPTION

An Itools Table...