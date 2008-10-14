package Define;

use Carp qw(carp cluck confess croak );
use strict;

##################################################
##  configurator  ##
##################################################

##################################################
##  constructor  ##
##################################################

sub new (){
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my $self  = {};

	# constructor mandatory parameter
	if (@_ < 1) {
		croak ("'new' take 1 argument")
	}
    $self->{name} = shift;
	my $option = shift;

	#internal description from definition file and default values
	$self->{deffile} = undef;
	$self->{header} = undef;
	$self->{type} = "FT";
	$self->{separator} = undef;
	$self->{command} = undef;
	$self->{file} = $self->{name};
	$self->{key} = [];
	$self->{field}= [];
	$self->{size}= {} ;
	$self->{row} = {};
	$self->{not_null}= [];
	$self->{sort}= [];
	$self->{fkey}= [];
	
	# debug
	$self->{debugging}=0;
	$self->{debugging}=$option->{debug} if exists $option->{debug};
	
   	bless($self, $class);
	
	# Initialize the values
	$self->define();
	
	$self->_debug("Sep : ", $self->separator());
	$self->_debug("Fields : ", join($self->separator(),$self->field()));
	$self->_debug("Keys : ", join($self->separator(),$self->key())) if $self->key();
	$self->_debug("Not NULL : ", join($self->separator(),$self->not_null()))  if $self->not_null();
	my %temp_hash=$self->size();
	$self->_debug("Size : ", join($self->separator(),values %temp_hash )) if %temp_hash;

	return $self;
}

##############################################
## accessor methods         ##
##############################################

sub header {
    my $self = shift;
    if (@_) { $self->{header} = shift }
    return $self->_eval_vars($self->{header});
}

sub name {
    my $self = shift;
    if (@_) { croak("'name' member is read-only") }
    return $self->{name};
}

sub type {
    my $self = shift;
    if (@_) { croak("'type' member is read-only") }
    return $self->{type};
}

sub separator {
    my $self = shift;
    if (@_) { $self->{separator} = shift }
    return $self->{separator};
}

sub table {
    my $self = shift;
    if (@_) { $self->{table} = shift }
    return $self->{name};
}

sub command {
    my $self = shift;
    if (@_) { croak("'command' member is read-only") }
    return $self->{command};
}

sub file {
    my $self = shift;
    if (@_) { croak("'file' member is read-only") }
    return $self->{file};
}

sub key {
    my $self = shift;
    if (@_) { @{ $self->{key} } = @_ }
    return @{ $self->{key} };
}

sub field {
    my $self = shift;
    if (@_) { @{ $self->{field} } = @_ }
    return @{ $self->{field} };
}

sub size {
    my $self = shift;
    if (@_) { @{ $self->{size} } = @_ }
    return %{ $self->{size} };
}

sub row {
    my $self = shift;
    if (@_) { @{ $self->{row} } = @_ }
    return %{ $self->{row} };
}

sub not_null {
    my $self = shift;
    if (@_) { @{ $self->{not_null} } = @_ }
    return @{ $self->{not_null} };
}

sub fkey {
    my $self = shift;
    if (@_) { @{ $self->{fkey} } = @_ }
    return @{ $self->{fkey} };
}

sub debugging {
    my $self = shift;
    if (@_) { $self->{debugging} = shift }
    return $self->{debugging};
}

##############################################
## public methods        ##
##############################################

# use "echo" as OS level to resolve environnement vars
sub _eval_vars() {
    my $self = shift;
	
	my $line=shift;
	($line)=`echo $line`;
	chomp $line;
	return $line;
}

# compute definition of Table by the .def file
sub define()
{
    my $self = shift;
	
=begin comment opening DEF file
	
	# look for the DEF file with the Search_file command
	my @search_output;
	my $search_command="Search_File -d " . $self->{name};

	@search_output=`$search_command `;
	if ($? == -1) {
		croak "failed to execute: $!\n";
    }
    elsif (($? >> 8) != 0) {
		croak sprintf ("'$search_command' died with signal %d, %s",($?  >> 8))
    }
	
	$self->{deffile}=$search_output[0];

	open DEFINE_FILE, "$self->{deffile}"  or croak "Error reading $self->{deffile} : $!";
	
=end comment
=cut
	
	$self->_debug("Define_Table $self->{name}");
	open DEFINE_FILE, "Define_Table $self->{name} |"  or croak "Error reading Define_Table $self->{name} | : $!";
	
	# Temp vars
	my @size_array;
	my @row_array;
	
	my $rex_before=qr/^(?:|.*\s)/;
	#my $rex_after=qr/\s*=\s*[\'\"]?(.*)[\'\"]?(?:|;.*)$/;
	my $rex_after=qr/\s*=\s*(.*)$/;
	
	# parse output
	while (<DEFINE_FILE>) {
		# Comments
		next if /^#/;
		
		# remove EOL (OS dependant)
		chomp;

		# parse the file
				
		$self->{header} = $1 if /${rex_before}HEADER${rex_after}/;
		$self->{type} = $1 if /${rex_before}TYPE${rex_after}/;
		$self->separator($1) if /${rex_before}SEP${rex_after}/;
		
		$self->{file} = $1 if /${rex_before}FILE${rex_after}/;
		$self->{sort} = $1 if /${rex_before}SORT${rex_after}/;
		$self->{key} = [ split(/\Q$self->{separator}/,$1) ] if /${rex_before}KEY${rex_after}/;
		$self->{not_null} = [ split(/\Q$self->{separator}/,$1) ] if /${rex_before}NOT_NULL${rex_after}/;
		
		$self->{field} = [ split(/\Q$self->{separator}/,$1) ] if /${rex_before}FORMAT${rex_after}/;
		@size_array =  split(/\Q$self->{separator}/,$1)  if /${rex_before}SIZE${rex_after}/;
		@row_array =  split(/\Q$self->{separator}/,$1)  if /${rex_before}ROW${rex_after}/;
	}
	
	# convert Array into Dictonary
	foreach (@{ $self->{field} }) {
		$self->{size}->{$_} = shift @size_array;
		$self->{row}->{$_} = shift @row_array;
	}
	
	close DEFINE_FILE;
}


# get information on Table's definition
sub describe()
{
	my $self = shift;
	return @{ $self->{field} };
	
	croak("describe() not implemented");

	return undef;
}

sub _debug() {
	my $self = shift;
	print STDERR "DEBUG:Define.$self->{name}:".join(' ',@_)."\n" if $self->debugging();
}

1;  # so the require or use succeeds



=head1 NAME

ITools::Define - class to use an ITools Definition file 

=head1 SYNOPSIS

 use ITools::Define;

 #################
 # class methods #
 #################
 $ob    = Define->new("table_name");

 #######################
 # object data methods #
 #######################


 ########################
 # other object methods #
 ########################


=head1 DESCRIPTION

An Itools Definition...