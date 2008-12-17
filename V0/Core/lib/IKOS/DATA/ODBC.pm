# Special Class for the Text ODBC Driver
package ODBC_TXT;
@ISA = ("ODBC");

# get table name depending the driver
sub _set_tablename() {
	my $self = shift;

	$self->_debug("$self->{table_name} -> $self->{table_name}.txt");
	$self->{table_name}=$self->{table_name}.".txt";
}

sub table_name {
    my $self = shift;
    if (@_) { croak("'table_name' member is read-only") }
	my $temp_name=$self->{table_name};
	$temp_name =~ s/\.txt$//;
    return $temp_name;
}


1;

package ODBC;

require IKOS::DATA::abstract::DBI_interface;
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
	
	# set the table name depending the ODBC Driver
	$self->_set_tablename();
	
	# put fields list in memory
	$self->_debug("Get info for table : ",$self->{table_name});
	$self->_set_columns_info();
	
	$self->_debug("Fields : ", join("|",$self->field()));
	my %temp_hash=$self->field_txt();
	$self->_debug("Fields TXT: ", join("|",values %temp_hash));
	$self->_debug("Keys : ", join("|",$self->key()));
	$self->_debug("Not NULL : ", join("|",$self->not_null()));
	%temp_hash=$self->size();
	$self->_debug("Size : ", join("|",values %temp_hash ));
	
	# set defaut for query_field
	$self->{query_field} = [ $self->field() ];
	
	# free any LOCK
	$self->_close_database();
	
    return $self;
}


sub key {
    my $self = shift;
    if (@_) { 
		@{ $self->{key} } = @_ ;
		$self->_debug("New Keys : ", join("|",@{$self->{key}}));
	}
    return @{ $self->{key} };
}

# get table name depending the driver
sub _set_tablename() {
	my $self = shift;

	# keep name 
}

# Get information from database
# Need "$self->{database_handle}" to be connected !
sub _set_columns_info() {
	my $self = shift;
	
	croak("$self->{database_name} need to be opened before execute _set_columns_info") if not defined $self->{database_handle};
	
	my $table_info;
	
	eval { $table_info=$self->{database_handle}->prepare("SELECT * from QSYS2.SYSCOLUMNS where SYSTEM_TABLE_SCHEMA='IKGLFIC' AND TABLE_NAME='".$self->{table_name}."'  ORDER BY ORDINAL_POSITION") };
	confess "Error in prepare : "."SELECT * from QSYS2.SYSCOLUMNS where SYSTEM_TABLE_SCHEMA='IKGLFIC' AND TABLE_NAME='".$self->{table_name}."'" if $@;
	
	$self->_debug("Get column info for $self->{table_name}");
	eval {  $table_info->execute() };
	confess "Error in prepare : SELECT * from syscolumns_".$self->{table_name} if $@;
	
	while (my @col=$table_info->fetchrow_array) {
		#print Dumper @col;
		push (@{$self->{field}},       $col[0]);
		my $size ="VARCHAR($col[5])" if $col[4] =~ /^CHAR\s*/;
		$size="INTEGER($col[5])"     if $col[4] =~ /^NUMERIC\s*/;
		$size="DECIMAL($col[5])"        if $col[4] =~ /^DECIMAL\s*/;
		$self->{size}->{$col[0]}=       $size;
		
		#$col[21] =~ s/\s+/_/g;
		$self->{field_txt}->{$col[0]}=       $col[21];
		
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
	$self->{database_handle} = DBI->connect("dbi:ODBC:DSN=$self->{database_name}","TGILLON","TGILLON",{ RaiseError => 1});

	# remove trailing spaces in CHAR fields
	$self->{database_handle}->{ChopBlanks}=1;
	
	# not used for now
	my $driver_name=$self->{database_handle}->func(6, 'GetInfo');
}

# quirk to get only key which contains number (or not)
# arg :
# true  (arg>0) : return only keys with numbers
# false (arg=0) : return only keys without numbers
sub query_condition_has_numeric() {
	my $self = shift;
	
	my $bool_is_numeric=shift;
	croak "query_condition_has_numeric take 1 arg : boolean" if not defined $bool_is_numeric;
	
	my @temp_condition;
	my $joined_temp_condition;
	
	if ($bool_is_numeric) {
		foreach ($self->key()) {
			push @temp_condition,"($_ != TRANSLATE($_,'+','0123456789'))";
		}
		$joined_temp_condition=join(" OR ",@temp_condition)
	} else {
		foreach ($self->key()) {
			push @temp_condition,"($_ = TRANSLATE($_,'+','0123456789'))";
		}
		$joined_temp_condition=join(" AND ",@temp_condition)
	}
	push @{$self->{query_condition}}, $joined_temp_condition;
}

# Create an SQL query with specific ODBC TXT syntax
sub get_query()
{
	my $self = shift;
	
	# return the user's SQL query
	return $self->{custom_select_query} if defined $self->{custom_select_query};
	
	# construct SQL from "query_*" members
	my $query;
	$query = "SELECT ".join(', ',$self->query_field())." FROM ".$self->{table_name};
	$query = $query." WHERE ".join(' AND ',$self->query_condition()) if $self->query_condition() != 0;
	$query = $query." ORDER BY ".join(', ',$self->query_sort()) if $self->query_sort() != 0;
	
	return $query;
}

sub execute() {
	my $self = shift;
	
	croak("execute() not implemented in ".ref($self));
}

sub insert_row() {
	my $self = shift;
	
	croak("insert_row() not implemented in ".ref($self));
}

sub update_row() {
	my $self = shift;
	
	croak("update_row() not implemented in ".ref($self));
}

1;

