#!/usr/bin/perl

use strict;

use IKOS::SIP;

my $debug_level = 0;

my $environnement=shift or die;
my $tablename=shift or die;
my $field =shift or die;

# New SIP Object instance
my $ikos_sip = SIP->new($environnement, {debug => $debug_level});

# Get information about the table Primary Key
my $table = $ikos_sip->open_local_table($tablename);

if (not defined $table) {
	die "error opening $tablename in env $environnement";
}


my ($table_key) = $table->key();
undef $table;

my $table_key_value = $ENV{$table_key};
print STDERR "KEY= $table_key\n";
print STDERR "KEY_VAL=$table_key_value\n";

# fetch selected row from histo table
my $table_histo = $ikos_sip->open_histo_table($tablename);
$table_histo->query_field("DATE_HISTO","TIME_HISTO","TABLE_NAME","TABLE_KEY","FIELD_NAME","FIELD_VALUE","COMMENT");
$table_histo->query_condition("TABLE_KEY = '$table_key_value' AND FIELD_NAME ='$field'");

while (my @line=$table_histo->fetch_row_array() ) {
	print join('|',@line)."\n";
}