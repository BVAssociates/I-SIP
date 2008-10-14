package ODBC_TXT;

require IKOS::DATA::DBI::DBI_interface;
@ISA = ("DBI_interface");

use Carp qw(carp cluck confess croak );
use strict;

use Data::Dumper;

##################################################
##  constructor  ##
##################################################

# open($Define_obj)
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;	
	
	my $options = pop @_;
	
	my $self  = $class->SUPER::open(@_, $options);
	
	
	############
	# Options to overide default settings
	############
	
	#$self->{timeout}=$options->{timeout} if exists $options->{timeout};

	############
	# Opening Database
	############
	
	$self->_open_database();

	############
	# Get Database's infos
	############
	
	# quick test
	croak "Error openning sqlite database : $self->{database_path}" unless $self->{database_handle}->ping();
	
	# put fields list in memory
	$self->_debug("Get info for table : ",$self->{table_name});
	$self->_set_columns_info();
	
	$self->_debug("Fields : ", join("|",$self->field()));
	$self->_debug("Keys : ", join("|",$self->key()));
	$self->_debug("Not NULL : ", join("|",$self->not_null()));
	my %temp_hash=$self->size();
	$self->_debug("Size : ", join("|",values %temp_hash ));
	
	# set defaut for query_field
	$self->{query_field} = [ $self->field() ];
	
	# free any LOCK
	$self->_close_database();
	
    return $self;
}

# Get information from database
# Need "$self->{database_handle}" to be connected !
sub _set_columns_info() {
	my $self = shift;
	
	croak("$self->{database_name} need to be opened before execute _set_columns_info") if not defined $self->{database_handle};
	
	my $table_info=$self->{database_handle}->prepare("SELECT * from syscolumns_".$self->{table_name}.".txt");
	$self->_debug("Get column info for $self->{table_name}");
	$table_info->execute();
	
	while (my @col=$table_info->fetchrow_array) {
		#print Dumper @col;
		push (@{$self->{field}},       $col[0]);
		my $size ="VARCHAR($col[5])" if $col[4] eq "CHAR";
		$size="INTEGER($col[5])"     if $col[4] eq "NUMERIC";
		$size="DECIMAL($col[5])"        if $col[4] eq "DECIMAL";
		$self->{size}->{$col[0]}=       $size;
		
		push (@{$self->{not_null}},     $col[0]) if $col[7] eq 'Y';
		push (@{$self->{key}},          $col[0]) if $col[27] eq 'Y';
	}
	
	if (not $self->field() or $self->field() == 1) {
		croak("Error reading information of table : $self->{table_name}");
	}
}


sub _open_database() {
	my $self = shift;
	
	croak '$self->{database_name} not defined' if not defined $self->{database_name};
	
	$self->_debug("Open database DSN : ",$self->{database_name});
	# use RaiseError exception to stop the script at first error
	$self->{database_handle} = DBI->connect("dbi:ODBC:DSN=$self->{database_name}","","",{ RaiseError => 1});

}

# Create an SQL query with specific ODBC TXT syntax
sub get_query()
{
	my $self = shift;
	
	# return the user's SQL query
	return $self->{custom_query} if defined $self->{custom_query};
	
	# construct SQL from "query_*" members
	my $query;
	$query = "SELECT ".join(', ',$self->query_field())." FROM ".$self->table_name().".txt";
	$query = $query." WHERE ".join(' AND ',$self->query_condition()) if $self->query_condition() != 0;
	$query = $query." ORDER BY ".join(', ',$self->query_sort()) if $self->query_sort() != 0;
	
	return $query;
}
