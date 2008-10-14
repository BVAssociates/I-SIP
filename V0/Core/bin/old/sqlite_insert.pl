#!/usr/bin/perl

use strict;

my $module=$ARGV[0] or die;

sub find_bv_tabpath ($) {
	my $file=shift;
	
	my @bv_tabpath=split(";",$ENV{BV_TABPATH});
	
	foreach my $path (@bv_tabpath) {
		return $path."\\".$file if -r $path."\\".$file ;
	}
	return undef
}

my $table_name;

my $sql_path=find_bv_tabpath("sql");
my $data_path="$sql_path\\$module\\DATA";
my $insert_sql_path="$sql_path\\$module\\INSERT";

my @db2_table=`Select -s TABLE_NAME from DB2_TABLE where MODULE=$module`;

chomp @db2_table;

	$\="\n";
foreach $table_name (@db2_table) {
	print "reading $data_path\\$table_name.out";
	print "writing $insert_sql_path\\$table_name\_CREATE.sql";
	open(SQL , ">$insert_sql_path\\$table_name\_INSERT.sql") or die("can't open $insert_sql_path\\$table_name\_INSERT.sql : $!");
	open(DATA, "<$data_path\\$table_name.out") or die("can't open $data_path\\$table_name.out : $!");

	print SQL "BEGIN;";

	my $first=1;
	my $limit=0;
	my @set;
	while (<DATA>) {
		chomp;
		s/\r$//;
		my @line=split(/\t/,$_,$limit);
		if ($first) {
			@set=@line;

			# $limit is use to determine if last field must be take if NULL
			$limit=@line+1;
		}
		else {
			# quirks to pop away the last NULL field
			pop @line;

			# transform "" into NULL
			@line=map {($_ eq "")? "NULL": "\"$_\""} @line; 
			printf SQL ("INSERT INTO %s (%s) VALUES (%s);\n",$table_name,join(',',@set),join(',',@line));
		}
		$first=0;
		
	}

	print SQL "COMMIT;";

	close DATA;
	close SQL;
	
	
	
	
	
}