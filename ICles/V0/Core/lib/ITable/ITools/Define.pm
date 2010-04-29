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
	$self->{separator} = "\t";
	$self->{command} = undef;
	$self->{file} = undef;
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
	
	#($line)=`echo $line`;
	#chomp $line;
	
	$line =~s/\$\{(\w+)\}/$ENV{$1}/g;
	$line =~s/\$(\w+)/$ENV{$1}/g;
	$line =~s/%(\w+)%/$ENV{$1}/g;
	
	return $line;
}

# "Search_File" implementation
sub get_bv_file() {
	my $self = shift;

	use File::Spec;
	my $file_name = shift or croak("usage: search_def(file_name [,extension] )");
	my $file_extension = shift;
	
	$file_extension="" if not defined $file_extension;
	
	my %path_for_extension = (
			".def" => $ENV{BV_DEFPATH},
			"" => $ENV{BV_TABPATH},
			".pci" => $ENV{BV_PCIPATH},
		);
	
	my @bv_path = split( /;/, $path_for_extension{$file_extension} );
	
	foreach my $dir ( @bv_path ) {
	
		my $found_file = File::Spec->catfile($dir, $file_name.$file_extension);
		if ( -r $found_file ) {
			return $found_file;
		}
	}
	
	return;
}

# compute definition of Table by the .def file
sub define()
{
    my $self = shift;

	$self->_debug("Define_Table $self->{name}");
	my $def_file=$self->get_bv_file($self->{name}, ".def");
	open DEFINE_FILE, '<', $def_file  or croak "Error reading $def_file : $!";
	
	
	# Temp vars
	my @size_array;
	my @row_array;
	
	my $rex_before=qr/^(?:|.*\s)/;
	my $rex_after=qr/\s*=\s*(['"])(.*)\1\s*$/;
	#my $rex_after=qr/\s*=\s*(.*)$/;
	
	# parse output
	while (<DEFINE_FILE>) {
		# Comments
		next if /^#/;
		
		# remove EOL (OS dependant)
		chomp;

		# parse the file
				
		$self->{header} = $2 if /${rex_before}HEADER${rex_after}/;
		$self->{type} = $2 if /${rex_before}TYPE${rex_after}/;
		$self->{separator} = $2 if /${rex_before}SEP${rex_after}/;
		
		$self->{file} = $2 if /${rex_before}FILE${rex_after}/;
		$self->{command} = $2 if /${rex_before}COMMAND${rex_after}/;
		$self->{sort} = $2 if /${rex_before}SORT${rex_after}/;
		$self->{key} = [ split(/\Q$self->{separator}/, $2) ] if /${rex_before}KEY${rex_after}/;
		$self->{not_null} = [ split(/\Q$self->{separator}/, $2) ] if /${rex_before}NOT_NULL${rex_after}/;
		
		# prepare I-TOOLS separator
		my $separator=qr/\s*\Q$self->{separator}\E\s*/;
		
		$self->{field} = [ split(/$separator/, $2) ] if /${rex_before}FORMAT${rex_after}/;
		@size_array =  split(/$separator/, $2)  if /${rex_before}SIZE${rex_after}/;
		@row_array =  split(/$separator/, $2)  if /${rex_before}ROW${rex_after}/;
	}
	
	# convert Array into Dictonary
	foreach (@{ $self->{field} }) {
		$self->{size}->{$_} = shift @size_array;
		$self->{row}->{$_} = shift @row_array;
	}
	
	# optimisation COMMAND="Select -s from Table"
	if ( $self->{command} and $self->{command} =~ /^\s*Select\s+\-s\s+\*?\s*from\s+(\S+)\s*$/i ) {
		$self->{file}    = $self->get_bv_file($1);
		$self->{command} = undef;
	}
	
	
	# default table when no COMMAND neither FILE
	if ( ! $self->{command} and ! $self->{file} ) {
		$self->{file}=$self->get_bv_file($self->{name});
	}
	
	croak("Impossible d'obtenir la source de donnée de $self->{name}") if ! $self->{file} and ! $self->{command};
	
	close DEFINE_FILE or croak "Error closing $def_file : $!";
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

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut