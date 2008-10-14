#!/usr/bin/perl

use strict;

use IKOS::ODBC_TXT;


my $environ=shift or die;
my $table=shift or die;

my $database_name="IKOS_DEV";

my $table = ODBC_TXT->open($database_name, $table, {debug => 0 });

if (not defined $table) {
	die "error opening $database_name.$table";
}

while (my %line=$table->fetch_row() ) {
	print join('|',values %line)."\n";
}
