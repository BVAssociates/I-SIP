@rem = '--*-Perl-*--
@echo off
if "%OS%" == "Windows_NT" goto WinNT
perl -x -S "%0" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto endofperl
:WinNT
perl -x -S %0 %*
if NOT "%COMSPEC%" == "%SystemRoot%\system32\cmd.exe" goto endofperl
if %errorlevel% == 9009 echo You do not have Perl in your PATH.
if errorlevel 1 goto script_failed_so_exit_with_non_zero_val 2>nul
goto endofperl
@rem ';
#!/usr/bin/perl
#line 15

use Test::Simple tests => 21;
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
ok(eval { my $count; while ($table_histo->fetch_row) {$count++};return $count}, "Histo->fetch_row()");
ok($table_histo_field = $env->open_histo_field_table("NATPROP", {debug => 0} ), "Histo->open_histo_field_table()");
ok(eval { my $count; while ($table_histo_field->fetch_row) {$count++};return $count}, "HistoField->fetch_row()");
ok($table_ikos = $env->open_source_table("NATPROP", {debug => 0} ), "Environnement->open_source_table()");
ok($table_diff = DataDiff->open($table_ikos,$table_histo), "DataDiff->open()");
ok($table_diff->compare(), "DataDiff->compare()");

ok($table_histo = $env->open_local_from_histo_table("NATPROP", "2010-04-04T09:33"), "Histo->open_local_from_histo_table() + date");
ok(eval { my $count; while ($table_histo->fetch_row) {$count++};return $count}, "Histo->fetch_row() + date");
ok($table_histo_field = $env->open_histo_field_table("NATPROP", "2010-04-04T09:33" ), "Histo->open_histo_field_table() + date");
ok(eval { my $count; while ($table_histo->fetch_row) {$count++};return $count}, "HistoField->fetch_row() + date");

ok($table_histo = $env->open_local_from_histo_table("NATPROP", "2008-04-04T09:33"), "Histo->open_local_from_histo_table() + date = empty");
ok(eval { my $count=0; while ($table_histo->fetch_row) {$count++};return $count == 0}, "Histo->fetch_row() + date = empty");
ok($table_histo_field = $env->open_histo_field_table("NATPROP", "2008-04-04T09:33" ), "Histo->open_histo_field_table() = empty");
ok(eval { my $count=0; while ($table_histo_field->fetch_row) {$count++};return $count == 0}, "HistoField->fetch_row() + date = empty");

__END__
:endofperl
