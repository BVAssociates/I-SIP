#!/usr/bin/perl

use Test::Simple tests => 12;
use Data::Dumper;

use strict;
use warnings;

################################################
# IKOS::Environnement
################################################

use Isip::Environnement;
use Isip::ITable::DataDiff;

my $odbc="SCF1";
my $env_name="PRD";
my $table_name="NATPROP";

use Isip::IsipLog qw'$logger no_log';
no_log();

my $env;


my $table_histo;
my $table_histo_field;
my $table_ikos;
my $table_diff;

my @temp;

require "PC_INIT_ENV.pl";
ok(pc_init_env::run($odbc,$env_name), "pc_init_env");
ok($env = Environnement->new($env_name), "Environnement->new()");

require "PC_INIT_TABLE.pl";
ok(pc_init_table::run($env_name,$table_name), "pc_init_table");

ok($env->exist_local_table($table_name."_HISTO"), "Environnement->exist_local_table()");
ok($env->get_table_key("NATPROP"), "Environnement->get_table_key()");
ok(@temp=$env->get_table_field("NATPROP"), "Environnement->get_table_field()");
ok($table_histo = $env->open_local_from_histo_table("NATPROP", {debug => 0} ), "Histo->open_local_from_histo_table()");
ok($table_histo_field = $env->open_histo_field_table("NATPROP", {debug => 0} ), "Histo->open_histo_field_table()");
ok(eval { my $count; while ($table_histo->fetch_row) {$count++};return $count}, "Histo->fetch_row()");


ok($table_ikos = $env->open_source_table("NATPROP", {debug => 0} ), "Environnement->open_source_table()");
ok($table_diff = DataDiff->open($table_ikos,$table_histo), "DataDiff->open()");
ok($table_diff->compare(), "DataDiff->compare()");


