package SIP;

use strict;

use IKOS::DATA::ODBC;
use IKOS::DATA::Sqlite;
use IKOS::DATA::ITools;
use IKOS::DATA::Histo;

use Carp qw(carp cluck confess croak );

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self= {};
	
	$self->{environnement} = shift or die;
	$self->{options} = {};
	
	return bless($self, $class);
}

#found the table primary key
sub get_table_key() {
	my $self = shift;
	my $tablename = shift or croak "get_table_key() wait args : 'tablename'";
	my $debug_level = 0;
	my $key_found;
	
	# some different way to get the infos :
	#   - from INFO_TABLE, 
	#   - from local table
	#   - from ITools definition file
	#
	# For now, we'll use INFO_TABLE
	
	my $table=ITools->open("INFO_TABLE", {debug => $debug_level});
	$table->query_condition("TABLE_NAME = '$tablename'");
	$table->query_field("PRIMARY_KEY");

	($key_found) = $table->fetch_row_array();
	return $key_found;
}

sub get_table_field() {
	my $self = shift;
	my $tablename = shift or croak "get_table_field() wait args : 'tablename'";
	my $debug_level = 0;
	my $key_found;
	
	# some different way to get the infos :
	#   - from INFO_TABLE, 
	#   - from local table
	#   - from ITools definition file
	#
	# For now, we'll use ITools definition
	
	my $table=ITools->open($tablename, {debug => $debug_level});
	return $table->field;
}

# change this methods to configure Database Access
sub exist_local_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename'";
	
	my $return_value=0;
	
	# verification on sqlite_master
	my $master_table=Sqlite->open("IKOS_".$self->{environnement} , 'sqlite_master', @_);
	$master_table->query_condition("type='table' AND name='$table_name'");
	
	if ($master_table->fetch_row_array) {
		$return_value=1;
	}
	$master_table->close();
	
	return $return_value;
}

sub open_local_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename'";
	
	my $tmp_return = eval {Sqlite->open("IKOS_".$self->{environnement} , $table_name, @_)};
	croak "Error opening $table_name : $@" if $@;
	return $tmp_return;
}

sub open_local_from_histo_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_histo_table() wait args : 'tablename'";
	
	my $table_histo = Histo->open("IKOS_".$self->{environnement} , $table_name, @_);
	
	# we must set the primary key manually
	$table_histo->key($self->get_table_key($table_name));

	return $table_histo
}

sub open_ikos_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_ikos_table() wait args : 'tablename'";
	
	return ODBC_TXT->open("IKOS_DEV" , $table_name, @_);
}

=begin comment : may be confusing

sub open_histo_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_histo_table() wait args : 'tablename'";
	
	return Sqlite->open("IKOS_".$self->{environnement} , $table_name."_HISTO", @_);
}

=end

=cut

sub get_histo_line () {
	my $self = shift;
	
	my $key = shift;
	my $date = shift;
	
	croak "not implemented";
}

sub SQL_create() {
	my $self=shift;
	
	my $tablename=shift or die;

	# Get infos from IKOS tables via ODBC
	my $table = $self->open_ikos_table($tablename, {debug => 0 });
	if (not defined $table) {
		die "error opening $table in IKOS";
	}

	my $table_def=ITools->open("INFO_TABLE",{debug => 0 });
	$table_def->query_condition("TABLE_NAME = '$tablename'");
	my %defined_table=$table_def->fetch_row();
	$table_def->finish;

	warn "WARNING: $tablename n'a pas de clef primaire définie dans INFO_TABLE" if not $defined_table{PRIMARY_KEY};
	#$defined_table{F_KEY};
	#$defined_table{F_TABLE};

	my %size_hash=$table->size();
	my @create_statements;
	foreach my $field ($table->field) {
		my $temp;
		$temp.=$field." ".$size_hash{$field};
		
		$temp.=" PRIMARY KEY" if $field eq $defined_table{PRIMARY_KEY};
		$temp.=" NOT NULL" if grep (/$field/,$table->not_null);
			
		push @create_statements,$temp;
	}

	# spécials columns
	#push @create_statements, "TIMESTAMP_COLLECTE VARCHAR(20) DEFAULT CURRENT_TIMESTAMP";

	my $create_query="CREATE TABLE $tablename (\n";
	$create_query .= join(",\n",@create_statements);
	$create_query .= "\n);\n";

	return $create_query;
}

sub SQL_drop() {
	my $self=shift;
	
	my $tablename=shift or die;
	
	return "DROP $tablename;";
}

sub compare_table() {
	my $self=shift;
	
	my ($table1, $table2) = @_;
	my @key;
	my %result;
	
	if ( join(',',sort $table1->key()) ne  join(',',sort $table1->key())) {
		croak("The 2 tables have not the same keys");
	}
	
	if ( $table1->table_name() ne $table2->table_name() ) {
		croak("The 2 tables have not the same name");
	}
	
	@key=$self->get_table_key($table1->table_name());
	
	if ( join(',',sort $table1->field()) ne  join(',',sort $table1->field())) {
		croak("The 2 tables have not the same fields");
	}
	
	$table1->query_sort(@key);
	
	#first pass to get primary keys which are on one table
	## after 2 loops :
	##	$seen_keys{keys} = 1 if only one table have it
	##	$seen_keys{keys} = 2 if the two tables have it
	##	my %seen_keys;
	##	my @row;
	##	$table1->query_field(@key);
	##	$table2->query_field(@key);
	##	while (@row=$table1->fetch_row_array()) {
	##		$seen_keys{join(',',@row)}++
	##	}
	##	while (@row=$table2->fetch_row_array()) {
	##		$seen_keys{join(',',@row)}++
	##	}
	
	my %row_table1=$table1->fetch_row;
	my %row_table2=$table2->fetch_row;
	while (%row_table1 or %row_table2) {
	
		# New lines
		## TODO
		## %result = (%result,%row_table1) if not %row_table1 or $seen_keys{join(',',@row} = ;
		## %result = (%result,%row_table2) if not %row_table2;
		die "table1 and table2 have different row number" if not %row_table1 or not %row_table2;
		
		##if ( $row_table1{join(',',@row)} ne $row_table1{join(',',@row)} ) {
		##	if ($seen{join(',',@row})
		##}
		my @key_values1=@row_table1{@key};
		my @key_values2=@row_table2{@key};
		die "table1 and table2 have different primary keys" if join(',',@key_values1) ne join(',',@key_values1);
		foreach my $field1 (keys %row_table1) {
			if ($row_table1{$field1} ne $row_table2{$field1}) {
				print STDERR "Found update : Key (".join(',',@key_values1).") $field1=$row_table1{$field1}\n";
				$result{join(',',@key_values1)}{$field1}=$row_table1{$field1};
			}
		}
		%row_table1=$table1->fetch_row;
		%row_table2=$table2->fetch_row;
	}
	
	return %result;
}

1;