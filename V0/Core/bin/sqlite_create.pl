#!/usr/bin/perl

use strict;

my $delete_before=shift  if $ARGV[0] =~ /^--delete$/;
#my $histo_only;
my $module=shift or die;
die if $module =~ /^--/;

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
my $create_sql_path="$sql_path\\$module\\CREATE";

my @db2_table=`Select -s TABLE_NAME from INFO_TABLE where MODULE=$module`;
chomp @db2_table;

$\="\n";

foreach $table_name (@db2_table) {
	print "reading $data_path\\syscolumns_$table_name.out";
	print "writing $create_sql_path\\$table_name\_CREATE.sql";
	open(SQL , ">$create_sql_path\\$table_name\_CREATE.sql") or die("can't open $create_sql_path\\$table_name\_CREATE.sql : $!");
	open(DATA, "<$data_path\\syscolumns_$table_name.out") or die("can't open $data_path\\syscolumns_$table_name.out : $!");

	my $num=1;
	my @set;
	my $create_sql;
	my $statement;
	my $table_name;
	my @create_sql_col;
	
	# static paramerters
	push @create_sql_col,"DATE_COLLECTE VARCHAR(30) DEFAULT CURRENT_DATE";
	push @create_sql_col,"TIME_COLLECTE VARCHAR(30) DEFAULT CURRENT_TIME";
	
	while (<DATA>) {
	        chomp;
	        s/\r$//;
	        my @line=split("\t");
	        if ($num == 1) {
	                @set=@line;
	        }
	        else {
			# get the table name on the second line
			$table_name=$line[1] if $num == 2;

			$statement=$line[0];
			
			$statement=$statement." VARCHAR($line[5])" if $line[4] eq "CHAR";
			$statement=$statement." INTEGER($line[5])" if $line[4] eq "NUMERIC";
			$statement=$statement." REAL($line[5])" if $line[4] eq "DECIMAL";

			$statement=$statement." NOT NULL" if  $line[7] eq 'Y';
			$statement=$statement." PRIMARY KEY" if $line[27] eq 'Y';
			push @create_sql_col,"$statement";
	        }
	        $num++;
	}
	# insert the create statement at the beginning
	my $create_header="CREATE TABLE IF NOT EXISTS $table_name (\n";
	
	$create_sql=$create_header.join(",\n",@create_sql_col)."\n);\n";
	# print the CREATE statement for DATA
	print SQL "DROP TABLE $table_name;" if defined $delete_before;
	print SQL $create_sql;
	
	# print the CREATE statement for HISTO
	print SQL "DROP TABLE $table_name\_HISTO;" if defined $delete_before;
	print SQL "CREATE TABLE IF NOT EXISTS $table_name\_HISTO (
ID INTEGER PRIMARY KEY AUTOINCREMENT,
DATE_HISTO VARCHAR(30) DEFAULT CURRENT_DATE,
TIME_HISTO VARCHAR(30) DEFAULT CURRENT_TIME,
ENVIRONNEMENT VARCHAR(30) NOT NULL,
TABLE_NAME VARCHAR(30) NOT NULL,
TABLE_KEY VARCHAR(30) NOT NULL,
FIELD_NAME VARCHAR(30) NOT NULL,
FIELD_VALUE VARCHAR(30),
COMMENT VARCHAR(50),
TYPE VARCHAR(30),
STATUS VARCHAR(30)
);";
	
	close DATA;
	close SQL;
}

