#!/usr/bin/perl

use strict;

my $ENV=shift or die;
my $module=shift or die;

sub find_bv_tabpath ($) {
	my $file=shift;
	
	my @bv_tabpath=split(";",$ENV{BV_TABPATH});
	
	foreach my $path (@bv_tabpath) {
		return $path."/".$file if -r $path."/".$file ;
	}
	return undef
}

my $database_name="IKOS_$ENV.sqlite3";
my $database=find_bv_tabpath($database_name);

# Get list of Table to survey
my @db2_table=`Select -s TABLE_NAME from DB2_TABLE where MODULE=$module`;
chomp @db2_table;


foreach $table_name (@db2_table) {
	my @line_snapshot;
	my $table_name;
	my @result_define;
	my $table_key;
	
	
	@result_define=`Define_Table $table_name`;
	($table_key)= grep(/KEY=\w+/,@result_define);
	
	$table_key=$1 if $table_key=~/KEY=(\w+)/;
	if (not $table_key) {
		print STDERR "No KEY found for $table_name\n";
		next;
	}
	
	@line_snapshot=`sqlite3 "$database" "SELECT * from  $table_name"`;
	my $row_num=0
	foreach (@line_snapshot) {
		@fields=split(/\|/);
		
		for my $field (@fields) {
			@result=`sqlite3 "$database" "insert into $table_name\HISTO (ENVIRONNEMENT,TABLE_NAME,TABLE_KEY,FIELD_NAME,FIELD_VALUE) VALUES ($ENV,$table_name,$table_key,)`;
			printf("insert into FIELD_HISTO (TABLE_NAME, FIELD_NAME, VALUE) VALUES (\"%s\",\"%s\",%s);\n",$table_name,$set[$i],$line[$i]);
		}

		
	}

	
	
	
	
}