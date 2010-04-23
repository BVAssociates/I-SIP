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

use Test::More tests => 55;
use Data::Dumper;
use Carp;
$Carp::Verbose=1;

use strict;
use warnings;

################################################
# IKOS::Sqlite
################################################

use ITable::Sqlite;

# Open
############
my $table_sqlite = Sqlite->open('c:\program files\BV Associates\I-SIS V2.0.2\Portal\ICles\ISIP\_Services\tab\IKOS_PROD_PROTYPP.sqlite',"PROTYPP_HISTO", { debug => 0, timeout => 1000});

# Table infos
############
ok(defined($table_sqlite),			'Sqlite->open() is defined');
is($table_sqlite->field, $table_sqlite->query_field,'Sqlite->field() Sqlite->and query_field() are identical');
is(join('@@',$table_sqlite->query_field('FIELD_NAME','FIELD_VALUE')), 'FIELD_NAME@@FIELD_VALUE', 'Sqlite->query_field()');
is(join('@@',$table_sqlite->query_sort('TABLE_KEY','STATUS')), 'TABLE_KEY@@STATUS', 'Sqlite->query_sort()');
is(join('@@',$table_sqlite->query_condition("STATUS is NULL")), "STATUS is NULL", 'Sqlite->query_condition()');

# Table data Select
############
eval { $table_sqlite->fetch_row_array() };
ok(! $@, 'Sqlite->fetch_row_array()');

ok($table_sqlite->finish, 'Sqlite->finish()');
is($table_sqlite->query_condition(undef), 0, 'Sqlite->query_condition() : reset to undef');
is(join('@@',$table_sqlite->query_field('TABLE_KEY','STATUS')), 'TABLE_KEY@@STATUS', 'Sqlite->query_field() : redefine to another field');
my $sqlite_count=0;
my @sqlite_last_row;
while (my @row=$table_sqlite->fetch_row_array()) {
	$sqlite_count++;
	@sqlite_last_row=@row;
}
ok(! $@, 'Sqlite->fetch_row_array() after reading all rows');
# same thing with hashes
my $sqlite_count_hash=0;
my %sqlite_last_hash;
while (my %row=$table_sqlite->fetch_row()) {
	$sqlite_count_hash++;
	%sqlite_last_hash=%row;
}
is($sqlite_count_hash , $sqlite_count, "Sqlite->fetch_row and Sqlite->fetch_row_array return same number of rows");
ok($table_sqlite->finish, 'Sqlite->finish() again');


# Table Data Insert/Select/delete
############

my $last_id;
# insert data
$last_id=$table_sqlite->insert_row( TABLE_KEY => "TEST1", FIELD_NAME=> "TEST2", STATUS=> "TEST3");
ok($last_id > 0, 'Sqlite->insert_row()');
ok($last_id, 'insert_row() return last_id='.$last_id);
# query inserted data
is(join('@@',$table_sqlite->query_condition("ROWID = $last_id")), "ROWID = $last_id", 'Sqlite->query_condition()');
my @last_inserted=$table_sqlite->fetch_row_array();
$table_sqlite->_debug("LINE=",join(',',@last_inserted));
is(join('@@',@last_inserted), 'TEST1@@TEST3', 'Sqlite->fetch_row_array() return last inserted data');
# delete inserted data
ok($table_sqlite->execute("DELETE from PROTYPP_HISTO where ROWID = $last_id;"), 'Sqlite->execute("DELETE ...") on last ROWID');
is(undef $table_sqlite, undef, 'Destroy object');


################################################
# IKOS::Histo
################################################

use Isip::ITable::Histo;

# Open
############
my $histo_table= Histo->open('c:\program files\BV Associates\I-SIS V2.0.2\Portal\ICles\ISIP\_Services\tab\IKOS_PROD_PROTYPP.sqlite',"PROTYPP", { debug => 0, timeout => 10000});
ok($histo_table->key("AAPTYCOD"),"set KEY AAPTYCOD for table");
# Table infos
############
ok(defined($histo_table), 'Histo->open() is defined');
is($histo_table->field, $histo_table->query_field,'Histo->field() and Histo->query_field() are identical');
is(join('@@',$histo_table->query_sort()), join('@@',$histo_table->key()), 'Histo->query_sort() equal to Histo->key()');
#is(join('@@',$histo_table->query_condition("ENVIRONNEMENT = 'TEST'")), "ENVIRONNEMENT = 'TEST'", 'Histo->query_condition()');

# Table data Select
############
ok($histo_table->fetch_row_array(),'Histo->fetch_row_array()');
ok($histo_table->finish, 'Histo->finish()');

is(join('@@',$histo_table->query_field('AAPTYCOD','AAPTYLIB')), 'AAPTYCOD@@AAPTYLIB', 'Histo->query_field()');
my $histo_count=0;
my @histo_last_row;
while (my @row=$histo_table->fetch_row_array()) {
	$histo_count++;
	@histo_last_row=@row;
}
ok($histo_count, 'Histo->fetch_row_array() : reading all rows as arrays');

# OBSOLETE
# The More Important Test
#is($histo_count, $sqlite_count, "Sqlite and Histo have the same rows number");
#is(join(',',@histo_last_row), join(',',@sqlite_last_row), "Sqlite and Histo have same last row");


# same as above with Hash instead of Array
my $histo_count_hash=0;
my %histo_last_hash;
while (my %row=$histo_table->fetch_row()) {
	$histo_count_hash++;
	%histo_last_hash=%row;
}
is($histo_count, $histo_count_hash, 'Histo->fetch_row_array() and Histo->fetch_row() return same number of rows');

my $histo_diff;
ok($histo_diff=$histo_table->compare_from($histo_table),'Histo->compare_from(myself) return ok');
is($histo_diff->count,0,'Histo->compare_from(myself) return no difference');

#OBSOLETE
#is($histo_count, $sqlite_count, "Sqlite and Histo have the same rows number");


#ok($histo_table->insert_row(%histo_last_hash),"Histo->insert_row() work");

##TODO
# $table_sqlite->update(...)
# $Histo->update(...)



################################################
# IKOS::ODBC
################################################

use ITable::ODBC;

my $table_odbc;
$table_odbc = ODBC->open("SCF1_IKGLFIC","PROTYPP", { debug => 0 });

ok(defined($table_odbc),'ODBC->open() is defined');
is(join('@@',$table_odbc->key($histo_table->key())), join('@@',$histo_table->key()), 'ODBC->key()');

# Table infos
############

is($table_odbc->field, $table_odbc->query_field,'ODBC->field() and ODBC->query_field() are identical');
is(join('@@',$table_odbc->query_field('AAPTYCOD','AAPTYLIB')), 'AAPTYCOD@@AAPTYLIB', 'ODBC->query_field()');
is(join('@@',$table_odbc->query_sort('AAPTYLIB','AAPTYCOD')), 'AAPTYLIB@@AAPTYCOD', 'ODBC->query_sort()');
is(join('@@',$table_odbc->query_condition("AANOSQCPST > 5")), "AANOSQCPST > 5", 'ODBC->query_condition()');


# Table data Select
############

eval { $table_odbc->fetch_row_array() };
ok(! $@, 'ODBC->fetch_row_array()');

ok($table_odbc->finish, 'ODBC->finish()');

is(join('@@',$table_odbc->query_condition(undef)), "", 'ODBC->query_condition()');


#eval { $table_odbc->fetch_row_array()};
#ok(! $@, 'ODBC->fetch_row_array() after ODBC->finish()');

my $odbc_diff;
$odbc_diff=$histo_table->compare_from($table_odbc);
ok($odbc_diff,'Histo->compare_from(table_odbc) return something');
is($odbc_diff->count,0,'Histo->compare_from(table_odbc) return no difference');


################################################
# IKOS::ITools
################################################

use ITable::ITools;

my $test_table = ITools->open("ps", {debug => 0});
ok( defined($test_table),'ITools->open_table()' );
ok($test_table->fetch_row_array(), 'ITools->fetch_row_array() fetch one row and stop');
#print "the more little process number owned by VOISINS\\vb is : $process[1] (Name: $process[0])\n";
ok($test_table->finish(), 'ITools->finish() finish the current request');
is (join("@@",$test_table->define->describe), 'Pid@@Owner@@Name@@PPid@@Pri@@StartDate@@KernelTime@@UserTime@@SizeKb@@ExecutablePath' , 'ITools->define->describe()');
is (join("@@",$test_table->query_field("Name","Pid")) , 'Name@@Pid' , 'ITools->query_field()');
is (join("@@",$test_table->query_sort("Pid","Owner")) , 'Pid@@Owner', 'ITools->query_sort()');
is (join("@@",$test_table->query_condition('Owner = AUTORITE NT\SYSTEM')), 'Owner = AUTORITE NT\SYSTEM','ITools->query_condition()');
is ($test_table->get_query() , 'Select -s Name, Pid FROM ps WHERE Owner = AUTORITE NT\SYSTEM ORDER_BY Pid, Owner', 'ITools->get_query()');


ok(my @process=$test_table->fetch_row_array(), 'ITools->fetch_row_array() fetch one row and stop');
is(@process,2, 'ITools->fetch_row_array() return 2 fields');
#print "the more little process number owned by VOISINS\\vb is : $process[1] (Name: $process[0])\n";
ok($test_table->finish(), 'ITools->finish() finish the current request');
is($test_table->{select_descriptor}, undef,'undef after ITools->finish()');

my $count=0;
while (my @line=$test_table->fetch_row_array()) {
	$count++
}
#print STDERR "DEBUG: $count lines returned\n";
ok($count > 0, "ITools->fetch_row_array() return something");
is($test_table->{select_descriptor}, undef,'undef after reading all lines');

my $count2=0;
my @test_array;
while (my %line=$test_table->fetch_row()) {
	foreach my $field (keys %line) {
		push @test_array,$line{$field};
		$count2++
	}
}
ok(@test_array > 0, "ITools->fetch_row() return something");


__END__
:endofperl
