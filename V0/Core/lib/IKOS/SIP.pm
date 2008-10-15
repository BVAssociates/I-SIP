package SIP;

use strict;

use IKOS::DATA::ODBC_TXT;
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




# change this methods to configure Database Access
sub open_local_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename'";
	
	return Sqlite->open("IKOS_".$self->{environnement} , $table_name, @_);
}

sub open_local_from_histo_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_histo_table() wait args : 'tablename'";
	
	return Histo->open("IKOS_".$self->{environnement} , $table_name, @_);
}

sub open_ikos_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_ikos_table() wait args : 'tablename'";
	
	return ODBC_TXT->open("IKOS_DEV" , $table_name, @_);
}

sub open_histo_table() {
	my $self = shift;
	
	my $table_name=shift or croak "open_histo_table() wait args : 'tablename'";
	
	return Sqlite->open("IKOS_".$self->{environnement} , $table_name."_HISTO", @_);
}


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

	warn "WARNING: $tablename n'a pas de clef primaire d�finie dans INFO_TABLE" if not $defined_table{PRIMARY_KEY};
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

	# sp�cials columns
	push @create_statements, "TIMESTAMP_COLLECTE VARCHAR(20) DEFAULT CURRENT_TIMESTAMP";

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

1;