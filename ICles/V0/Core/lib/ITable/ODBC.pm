# Special Class for the Text ODBC Driver
package ODBC_TXT;
@ISA = ("ODBC");

use Carp qw(carp croak );

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

sub _set_columns_info() {
	my $self = shift;
	
	croak("$self->{database_name} need to be opened before execute _set_columns_info") if not defined $self->{database_handle};
	
	my $table_info;
	
	eval { $table_info=$self->{database_handle}->prepare("SELECT \* from SYSCOLUMNS_".$self->{table_name}) };
	croak "Error in prepare" if $@;
	
	$self->_debug("Get column info for $self->{table_name}");
	eval {  $table_info->execute() };
	croak "Error in prepare" if $@;
	
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

1;

package ODBC;

require ITable::abstract::DBI_interface;
@ISA = ("DBI_interface");

use strict;

use Carp qw(carp croak );

##################################################
##  constructor  ##
##################################################

# open
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;	
	
	my $options = pop @_;
	
	my $self  = $class->SUPER::open(@_, $options);
	
	
	############
	# Options to overide default settings
	############
	
	#$self->{timeout}=$options->{timeout} if exists $options->{timeout};
	$self->{odbc_username} = "";
	$self->{odbc_password} = "";
	$self->{odbc_name} = "";
	$self->{odbc_username} = $options->{username} if exists $options->{username};
	$self->{odbc_password} = $options->{password} if exists $options->{password};
	$self->{odbc_name} = $options->{odbc_name} if exists $options->{odbc_name};

	############
	# Opening Database
	############
	
	$self->_open_database();
	
	# Some features of TXT driver differ from others
	if ($self->{driver_name} eq 'odbcjt32.dll') {
		$self=bless($self,'ODBC_TXT');
	} else {
		$self=bless($self,$class);
	}

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
	
	# get data base type by its driver
	$self->{driver_name}="";
	
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
	
	eval { $table_info=$self->{database_handle}->prepare("SELECT * from QSYS2.SYSCOLUMNS where SYSTEM_TABLE_SCHEMA='$self->{database_name}' AND TABLE_NAME='".$self->{table_name}."'  ORDER BY ORDINAL_POSITION") };
	croak "Error in prepare : "."SELECT * from QSYS2.SYSCOLUMNS where SYSTEM_TABLE_SCHEMA='$self->{database_name}' AND TABLE_NAME='".$self->{table_name}."'" if $@;
	
	$self->_debug("Get column info for $self->{table_name}");
	eval {  $table_info->execute() };
	croak "Error in prepare : SELECT * from syscolumns_".$self->{table_name} if $@;
	
	while (my @col=$table_info->fetchrow_array) {
		#print Dumper @col;
		push (@{$self->{field}},       $col[0]);
		my $size ="";
		$size="VARCHAR($col[5])"        if $col[4] =~ /^(VAR)?CHAR\s*/;
		$size="INTEGER($col[5])"        if $col[4] =~ /^(NUMERIC|INTEGER|TIMESTMP|SMALLINT)\s*/;
		$size="DECIMAL($col[5])"        if $col[4] =~ /^DECIMAL\s*/;
		$self->{size}->{$col[0]}=       $size;
		warn "unknown SIZE format : $col[4]" if not $size;
		#$col[21] =~ s/\s+/_/g;
		$self->{field_txt}->{$col[0]}="";
		$self->{field_txt}->{$col[0]}=       $col[21] if $col[21];
		
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
	
	my $odbc_dsn="dbi:ODBC:DSN=$self->{odbc_name};DBQ=$self->{database_name}";
	
	$self->_debug("Open database DSN : ",$odbc_dsn);
	# use RaiseError exception to stop the script at first error
	$self->{database_handle} = DBI->connect($odbc_dsn,$self->{odbc_username} ,$self->{odbc_password} ,{ RaiseError => 1});

	# remove trailing spaces in CHAR fields
	$self->{database_handle}->{ChopBlanks}=1;
	
	$self->{driver_name}=$self->{database_handle}->func(6, 'GetInfo');
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

# Create an SQL query with specific ODBC syntax
sub get_query()
{
	my $self = shift;
	
	# return the user's SQL query
	return $self->{custom_select_query} if defined $self->{custom_select_query};
	
	my $distinct="";
	$distinct="DISTINCT" if $self->query_distinct;
	
	# construct SQL from "query_*" members
	my $query;
	$query = "SELECT ".$distinct." ".join(', ',$self->query_field())." FROM ".$self->{table_name};
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

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut

