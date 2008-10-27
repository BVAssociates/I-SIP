#!/usr/bin/perl

use Test::Simple tests => 9;
use Data::Dumper;

use strict;
use warnings;

################################################
# IKOS::SIP
################################################

use IKOS::SIP;

my $sip;
my $table_sqlite;
my $table_histo;
my $table_ikos;

my @temp;

ok($sip = SIP->new("TEST"), "SIP->new()");
ok($sip->exist_local_table("TEST"), "SIP->exist_local_table()");
ok($sip->get_table_key("TEST"), "SIP->get_table_key()");
ok(@temp=$sip->get_table_field("TEST"), "SIP->get_table_field()");
#print Dumper @temp;
ok($table_sqlite = $sip->open_local_table("TEST"), "SIP->open_local_table()");
ok($table_histo = $sip->open_local_from_histo_table("TEST", {debug => 0} ), "SIP->open_local_from_histo_table()");
ok($table_ikos = $sip->open_ikos_table("TEST", {debug => 0} ), "SIP->open_ikos_table()");

my %differences;
ok( %differences = $table_ikos->compare_table($table_histo),"SIP ->compare_table() bitween IKOS and Histo" );
ok( %differences = $table_ikos->compare_table($table_sqlite),"SIP ->compare_table() bitween IKOS and Sqlite" );
print Dumper(%differences);