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
my $insert_sql_path="$sql_path\\$module\\HISTO";
mkdir($insert_sql_path);

my @db2_table=`Select -s TABLE_NAME, TABLE_KEY from DB2_TABLE where MODULE=$module`;

chomp @db2_table;

	$\="\n";
foreach $table_name (@db2_table) {

	my $table_key;
	($table_name,$table_key) = split(';',$table_name);
	next if not $table_key;
	
	print "reading $data_path\\$table_name.out";
	print "writing $insert_sql_path\\$table_name\_HISTO.sql";
	open(SQL , ">$insert_sql_path\\$table_name\_HISTO.sql") or die("can't open $insert_sql_path\\$table_name\_HISTO.sql : $!");
	open(DATA, "<$data_path\\$table_name.out") or die("can't open $data_path\\$table_name.out : $!");

	#look for the KEY
	my @result_define=`Define_Table $table_name`;
	my ($table_key)= grep(/KEY=\w+/,@result_define);
	
	$table_key=$1 if $table_key=~/KEY=(\w+)/;
	if (not $table_key) {
		print STDERR "No KEY found for $table_name\n";
		next;
	}
	
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
			
			for (my $i=0;$i < @set; $i++) {
				printf SQL ("insert into $table_name\_HISTO (ENVIRONNEMENT,TABLE_NAME, TABLE_KEY, FIELD_NAME, FIELD_VALUE) VALUES (\"TEST\",\"%s\",\"%s\",\"%s\",%s);\n",$table_name,$table_key,$set[$i],$line[$i]);
			}
		}
		$first=0;
		
	}

	print SQL "COMMIT;";

	close DATA;
	close SQL;
	
	
	
	
	
}