package ITools_interface;
@ISA=(DATA_interface);

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
    
	# mandatory parameter
	if (@_ < 1) {
		croak ("'new' take 1 argument: new(\$define_obj)")
	}
    my $define_ref = shift;
	my $options = shift;
	my $self  = $class->SUPER::open($define_ref->name(), $options);
	
	$self->{define} = $define_ref;

	# internal description overiding "define" members
	$self->{key} = [ $self->{define}->key ];
	$self->{field}= [ $self->{define}->field ];
	$self->{size}= { $self->{define}->size };
	$self->{not_null}= [ $self->{define}->not_null ];
	
	# user query
	## anon array reference of 
	$self->{query_field}  = [ @{ $self->{field} } ];


    bless($self, $class);
		
    return $self;
}

##############################################
## accessor methods         ##
##############################################

##Read Only Values


sub type {
    my $self = shift;
    if (@_) { croak("'type' member is read-only") }
    return $self->{define}->{type};
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




# return the Define object
sub define {
    my $self = shift;
    if (@_) { $self->{define} = shift }
    return $self->{define};
}



##############################################
## Virtual methods provided by Interface       ##
##############################################





# get information on Table's definition
sub describe()
{
	my $self = shift;
	
	return $self->{define}->describe();
}

##############################################
## methods to access Table data        ##
##############################################



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