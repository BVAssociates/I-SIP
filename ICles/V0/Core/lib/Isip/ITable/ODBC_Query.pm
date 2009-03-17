package ODBC_Query;


require ITable::abstract::DATA_interface;
@ISA = ("DATA_interface");

use Carp qw(carp croak );
use strict;
use Scalar::Util qw(blessed);

use SQL::Statement;
use ITable::ODBC;


#use Data::Dumper;

##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	
	# mandatory parameter
	if (@_ < 3) {
		croak ($class.'->open : take 3 mandatory argument: ${class}->open( database_name, table_name, "SQL QUERY"  [ ,{ timeout => $sec, debug => $num} ])')
	}
	
	my $database_name=shift;
	my $table_name=shift;
	my $sql_query=shift;
	my $options=shift;

	my $self=$class->SUPER::open($table_name, $options);

	$self->{database_name}=$database_name;
	$self->{ODBC_options}=$options;
	
	# array : tables
	$self->{table_list}=[];
	
	# reference on ITable reference on the first table
	$self->{reference_table}=undef;
	
	# hash : field => tablename
	$self->{field_table}={};
	
	$self->{sql_string}=$sql_query;
	
	$self->{sql_statement_obj}=$self->_sql_parse();
	
	# get info from each table
	foreach ( @{$self->{table_list}} ) {
		$self->_set_columns_info($_);
	}
	# check every field is known
	foreach ( $self->field ) {
		if ( not defined $self->{field_table}->{$_} ) {
			croak("field <$_> are unknown in SQL : $self->{sql_string}");
		}
	}
	
	$self->query_field($self->field);
	
	$self->custom_select_query($self->{sql_string});
	
	# We bless the object with the new class
	bless ($self, $class);
	
	$self->_debug("initialisation");
	
		
    return $self;
}


sub _sql_parse() {
	my $self=shift;
	
	my $parser = SQL::Parser->new();
	$parser->{RaiseError}=0;
	$parser->{PrintError}=0;
	my $stmt_obj = SQL::Statement->new($self->{sql_string},$parser);
	
	if (my $error=$stmt_obj->errstr) {
		$error =~ tr/\!\n//d;
		croak ($error,": $self->{sql_string}");
	}
	
	# store tables
	foreach ($stmt_obj->tables) {
		push ( @{$self->{table_list}}, $_->name);
	}
	
	# initialize fields
	foreach ($stmt_obj->columns) {
		# put in field list
		push ( @{$self->{field}}, $_->name);
		# set to field => undef
		$self->{field_table}->{$_->name}=$_->table;
	}
	
	croak "<*> not allowed in SQL : $self->{sql_string}" if grep ( /\*/,@{$self->{field}});
	
	return $stmt_obj;
}

# set information relative to one table from the query
sub _set_columns_info() {
	my $self=shift;
	
	my $tablename=shift or croak("usage : __PACKAGE__->_set_columns_info(tablename)");
	
	my $table_obj=ODBC->open($self->{database_name},$tablename, $self->{ODBC_options});
	my %table_size=$table_obj->size();
	my %table_field_txt=$table_obj->field_txt();
	
	# save first table object for further use
	$self->{reference_table}=$table_obj if not defined $self->{reference_table};
	
	my @current_fields=$self->field();
	
	# look for owned columns in field list
	foreach my $field ( @current_fields ) {
		if ($table_obj->has_fields($field)) {
			if (not defined $self->{field_table}->{$field}) {
				$self->{field_table}->{$field}=$tablename;
			}
			$self->{size}->{$field} = $table_size{$field};
			$self->{field_txt}->{$field} = $table_field_txt{$field};
		}
	}
}

##################################################
##  public methods delegated to the target table ##
##################################################

sub key {
    my $self = shift;
    if (@_) { 
		@{ $self->{key} } = @_ ;
		$self->_debug("New Keys : ", join("|",@{$self->{key}}));
	}
    return @{ $self->{key} };
}

sub query_sort {
    my $self = shift;
	
	croak("Unable to sort rows");
	
    return @{ $self->{query_sort} };
}

sub get_query() {
	my $self = shift;
	
	my $query=$self->{sql_string};
	my $where_hash=$self->{sql_statement_obj}->where_hash();
	
	my $conditions = join (' AND ', $self->query_condition());
	
	if ($self->query_field != $self->field) {
		my $query_fields=join(',',$self->query_field);
		$query =~ s/SELECT\s+(.+)\s+FROM/SELECT $query_fields FROM/i;
	}
	
	if ($self->query_distinct) {
		$query =~ s/SELECT\s/SELECT DISTINCT /;
	}
	
	if ( $self->query_condition()) {
		if ($self->{sql_statement_obj}->where() ) {
			if ($self->{sql_statement_obj}->order()) {
				$query =~ s/WHERE\s+(.+)\s+(ORDER)/WHERE ($1) AND $conditions $2/i;
			}
			elsif ($self->{sql_statement_obj}->limit()) {
				$query =~ s/WHERE\s+(.+)\s+(LIMIT)/WHERE ($1) AND $conditions $2/i;
			}
			else {
				$query =~ s/WHERE\s+(.+)$/WHERE ($1) AND $conditions/i;
			}
		}
		else {
			if ($self->{sql_statement_obj}->order()) {
				$query =~ s/ORDER/ WHERE $conditions ORDER/i;
			}
			elsif ($self->{sql_statement_obj}->limit()) {
				$query =~ s/LIMIT/ WHERE $conditions LIMIT/i;
			}
			else {
				$query =~ s/$/ WHERE $conditions/i;
			}
		}
	}
	#return $self->print_recursive_where($where_hash);
	
	return $query;
}


# reconstruct WHERE clause with SQL::Statement object
# (may not be useful)
sub print_recursive_where;

sub print_recursive_where() {
	my $self=shift;
	my $where_hash=shift;
	
	my $value;
	
	if (exists $where_hash->{op}) {
		my $arg1=$where_hash->{arg1};
		my $arg2=$where_hash->{arg2};
		my $op=$where_hash->{op};
	
		my $neg_op="";
		$neg_op="NOT" if $where_hash->{neg};
		
		$value= "$neg_op";
		$value.= " ( ";
		$value.=$self->print_recursive_where($arg1);
		$value.= "$op";
		$value.=$self->print_recursive_where($arg2);
		$value.= " ) ";
	} else {
		$value=$where_hash->{value};
		if ( $where_hash->{type} eq 'string' ) {
			$value="'$value'";
		}
	}
	
	return $value;
}


sub fetch_row_array() {
	my $self = shift;
	
	$self->{reference_table}->custom_select_query($self->get_query());
	return $self->{reference_table}->fetch_row_array();
}

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
