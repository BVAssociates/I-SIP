#!/usr/bin/perl

use Test::More tests => 54;
use Data::Dumper;

use strict;
use warnings;

################################################
# IKOS::Sqlite
################################################

use IKOS::DATA::Sqlite;

# Open
############
my $table_sqlite = Sqlite->open('c:\program files\BV Associates\I-SIS V2.0.2\Agent\ICles\IKOS\_Services\test\tab\IKOS_TEST_TEST.sqlite',"TEST", { debug => 0, timeout => 1000});

# Table infos
############
ok(defined($table_sqlite),			'Sqlite->open() is defined');
is($table_sqlite->field, $table_sqlite->query_field,'Sqlite->field() Sqlite->and query_field() are identical');
is(join('@@',$table_sqlite->query_field('AAPTYCOD','AAPTYLIB')), 'AAPTYCOD@@AAPTYLIB', 'Sqlite->query_field()');
is(join('@@',$table_sqlite->query_sort('AAPTYCOD','AAPTYLIB')), 'AAPTYCOD@@AAPTYLIB', 'Sqlite->query_sort()');
is(join('@@',$table_sqlite->query_condition("AANOSQCPST < 10")), "AANOSQCPST < 10", 'Sqlite->query_condition()');

# Table data Select
############
eval { $table_sqlite->fetch_row_array() };
ok(! $@, 'Sqlite->fetch_row_array()');

ok($table_sqlite->finish, 'Sqlite->finish()');
is($table_sqlite->query_condition(undef), 0, 'Sqlite->query_condition() : reset to undef');
is(join('@@',$table_sqlite->query_field('AAPTYCOD','AAPTYLIB')), 'AAPTYCOD@@AAPTYLIB', 'Sqlite->query_field() : redefine to another field');
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
$last_id=$table_sqlite->insert_row( AAPTYCOD => "TEST1", AAPTYLIB=> "TEST2", AANPRCOD=> "TEST3", AAUTILCPST=> "TEST", AADTECPST=> "TEST");
ok($last_id > 0, 'Sqlite->insert_row()');
ok($last_id, 'insert_row() return last_id='.$last_id);
# query inserted data
is(join('@@',$table_sqlite->query_condition("ROWID = $last_id")), "ROWID = $last_id", 'Sqlite->query_condition()');
my @last_inserted=$table_sqlite->fetch_row_array();
$table_sqlite->_debug("LINE=",join(',',@last_inserted));
is(join('@@',@last_inserted), 'TEST1@@TEST2', 'Sqlite->fetch_row_array() return last inserted data');
# delete inserted data
ok($table_sqlite->execute("DELETE from TEST where ROWID = $last_id;"), 'Sqlite->execute("DELETE ...") on last ROWID');
is(undef $table_sqlite, undef, 'Destroy object');


################################################
# IKOS::Histo
################################################

use IKOS::DATA::Histo;

# Open
############
my $histo_table= Histo->open('c:\program files\BV Associates\I-SIS V2.0.2\Agent\ICles\IKOS\_Services\test\tab\IKOS_TEST_TEST.sqlite',"TEST", { debug => 0, timeout => 10000});
ok($histo_table->key("AAPTYCOD"),"set KEY AAPTYCOD for table TEST");
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

# The More Important Test
is($histo_count, $sqlite_count, "Sqlite and Histo have the same rows number");
is(join(',',@histo_last_row), join(',',@sqlite_last_row), "Sqlite and Histo have same last row");


# same as above with Hash instead of Array
my $histo_count_hash=0;
my %histo_last_hash;
while (my %row=$histo_table->fetch_row()) {
	$histo_count_hash++;
	%histo_last_hash=%row;
}
is($histo_count, $histo_count_hash, 'Histo->fetch_row_array() and Histo->fetch_row() return same number of rows');
is($histo_count, $sqlite_count, "Sqlite and Histo have the same rows number");


ok($histo_table->insert_row(%histo_last_hash),"Histo->insert_row() work");

##TODO
# $table_sqlite->update(...)
# $Histo->update(...)



################################################
# IKOS::ODBC
################################################

use IKOS::DATA::ODBC;

my $table_odbc;
$table_odbc = ODBC_TXT->open("IKOS_DEV","ACTCOCP", { debug => 0 });

ok(defined($table_odbc),			'ODBC_TXT->open() is defined');


# Table infos
############

is($table_odbc->field, $table_odbc->query_field,'ODBC_TXT->field() and ODBC_TXT->query_field() are identical');
is(join('@@',$table_odbc->query_field('AIPTYCOD','AICDDECORG')), 'AIPTYCOD@@AICDDECORG', 'ODBC_TXT->query_field()');
is(join('@@',$table_odbc->query_sort('AIPTYCOD','AICDETTYP')), 'AIPTYCOD@@AICDETTYP', 'ODBC_TXT->query_sort()');
is(join('@@',$table_odbc->query_condition("AIUTILCPST = 'T281'")), "AIUTILCPST = 'T281'", 'ODBC_TXT->query_condition()');


# Table data Select
############

eval { $table_odbc->fetch_row_array() };
ok(! $@, 'ODBC_TXT->fetch_row_array()');

ok($table_odbc->finish, 'ODBC_TXT->finish()');

eval { $table_odbc->fetch_row_array()};
ok(! $@, 'ODBC_TXT->fetch_row_array() after ODBC_TXT->finish()');


################################################
# IKOS::ITools
################################################

use IKOS::DATA::ITools;

my $test_table = ITools->open("ps", {debug => 0});
ok( defined($test_table),'ITools->open_table()' );
ok($test_table->fetch_row_array(), 'ITools->fetch_row_array() fetch one row and stop');
#print "the more little process number owned by VOISINS\\vb is : $process[1] (Name: $process[0])\n";
ok($test_table->finish(), 'ITools->finish() finish the current request');
is (join("@@",$test_table->define->describe), 'Pid@@Owner@@Name@@PPid@@Pri@@StartDate@@KernelTime@@UserTime@@SizeKb@@ExecutablePath' , 'ITools->define->describe()');
is (join("@@",$test_table->query_field("Name","Pid")) , 'Name@@Pid' , 'ITools->query_field()');
is (join("@@",$test_table->query_sort("Pid","Owner")) , 'Pid@@Owner', 'ITools->query_sort()');
is (join("@@",$test_table->query_condition('Owner = VOISINS\vb')), 'Owner = VOISINS\vb','ITools->query_condition()');
is ($test_table->get_query() , 'Select -s Name, Pid FROM ps WHERE Owner = VOISINS\vb ORDER_BY Pid, Owner', 'ITools->get_query()');


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

