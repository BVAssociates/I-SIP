#!/usr/bin/perl

use strict;

use IKOS::SIP;

my $environ=shift or die;
my $table_name=shift or die;

my $sip=SIP->new($environ);
my $table=$sip->open_local_table($table_name, {debug => 0 });

die "unable to open local $table_name in env $environ" if not defined $table;

while (my @line=$table->fetch_row_array()) {
	print join(',',@line)."\n";
}
