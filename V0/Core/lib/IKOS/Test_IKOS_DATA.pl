#!/usr/bin/perl

use Test::More tests => 49;
use Data::Dumper;

use strict;
use warnings;

################################################
# IKOS::Sqlite
################################################

use IKOS::DATA::Sqlite;

# Open
############
my $base = Sqlite->open("IKOS_TEST","TEST", { debug => 0, timeout => 10000});

# Table infos
############
ok(defined($base),			'Sqlite->open() is defined');
is($base->field, $base->query_field,'Sqlite->field() Sqlite->and query_field() are identical');
is(join('@@',$base->query_field('AAPTYCOD','AAPTYLIB')), 'AAPTYCOD@@AAPTYLIB', 'Sqlite->query_field()');
is(join('@@',$base->query_sort('AAPTYCOD','AAPTYLIB')), 'AAPTYCOD@@AAPTYLIB', 'Sqlite->query_sort()');
is(join('@@',$base->query_condition("AANOSQCPST < 10")), "AANOSQCPST < 10", 'Sqlite->query_condition()');

# Table data Select
############
eval { $base->fetch_row_array() };
ok(! $@, 'Sqlite->fetch_row_array()');

ok($base->finish, 'Sqlite->finish()');
is($base->query_condition(undef), 0, 'Sqlite->query_condition() : reset to undef');
is(join('@@',$base->query_field('AAPTYCOD','AANPRCOD')), 'AAPTYCOD@@AANPRCOD', 'Sqlite->query_field() : redefine to another field');
my $sqlite_count=0;
my @sqlite_last_row;
while (my @row=$base->fetch_row_array()) {
	$sqlite_count++;
	@sqlite_last_row=@row;
}
ok(! $@, 'Sqlite->fetch_row_array() after reading all rows');
ok($base->finish, 'Sqlite->finish() again');

# Table Data Insert/Select/delete
############

my $last_id;
# insert data
$last_id=$base->insert_row( AAPTYCOD => "TEST1", AAPTYLIB=> "TEST2", AANPRCOD=> "TEST3", AAUTILCPST=> "TEST", AADTECPST=> "TEST");
ok($last_id > 0, 'Sqlite->insert_row()');
ok($last_id, 'insert_row() return last_id='.$last_id);
# query inserted data
is(join('@@',$base->query_condition("ROWID = $last_id")), "ROWID = $last_id", 'Sqlite->query_condition()');
my @last_inserted=$base->fetch_row_array();
$base->_debug("LINE=",join(',',@last_inserted));
is(join('@@',@last_inserted), 'TEST1@@TEST3', 'Sqlite->fetch_row_array() return last inserted data');
# delete inserted data
ok($base->execute("DELETE from TEST where ROWID = $last_id;"), 'Sqlite->execute("DELETE ...") on last ROWID');
is(undef $base, undef, 'Destroy object');


################################################
# IKOS::Histo
################################################

use IKOS::DATA::Histo;

# Open
############
my $histo_table= Histo->open("IKOS_TEST","TEST", { debug => 0, timeout => 10000});

# Table infos
############
ok(defined($histo_table), 'Histo->open() is defined');
is($histo_table->field, $histo_table->query_field,'Histo->field() and Histo->query_field() are identical');
is(join('@@',$histo_table->query_sort('AAPTYCOD','AAPTYLIB')), 'AAPTYCOD@@AAPTYLIB', 'Histo->query_sort()');
#is(join('@@',$histo_table->query_condition("ENVIRONNEMENT = 'TEST'")), "ENVIRONNEMENT = 'TEST'", 'Histo->query_condition()');

# Table data Select
############
ok($histo_table->fetch_row_array(),'Histo->fetch_row_array()');
ok($histo_table->finish, 'Histo->finish()');

is(join('@@',$histo_table->query_field('AAPTYCOD','AANPRCOD')), 'AAPTYCOD@@AANPRCOD', 'Histo->query_field()');
my $histo_count=0;
my @histo_last_row;
while (my @row=$histo_table->fetch_row_array()) {
	$histo_count++;
	@histo_last_row=@row;
}
ok($histo_count, 'Histo->fetch_row_array() : reading all rows');
ok($histo_table->finish, 'Histo->finish() again');

# The More Important Test
is($histo_count, $sqlite_count, "Sqlite and Histo have the same rows number");
is(join(',',@histo_last_row), join(',',@sqlite_last_row), "Sqlite and Histo have same last row");

##TODO
# $base->update(...)
# $Histo->update(...)
# verify same number of data

################################################
# IKOS::ODBC
################################################

use IKOS::DATA::ODBC_TXT;

undef $base;
$base = ODBC_TXT->open("IKOS_DEV","ACTCOCP", { debug => 0});

ok(defined($base),			'ODBC_TXT->open() is defined');


# Table infos
############

is($base->field, $base->query_field,'ODBC_TXT->field() and ODBC_TXT->query_field() are identical');
is(join('@@',$base->query_field('AIPTYCOD','AICDDECORG')), 'AIPTYCOD@@AICDDECORG', 'ODBC_TXT->query_field()');
is(join('@@',$base->query_sort('AIPTYCOD','AICDETTYP')), 'AIPTYCOD@@AICDETTYP', 'ODBC_TXT->query_sort()');
is(join('@@',$base->query_condition("AIUTILCPST = 'T281'")), "AIUTILCPST = 'T281'", 'ODBC_TXT->query_condition()');


# Table data Select
############

eval { $base->fetch_row_array() };
ok(! $@, 'ODBC_TXT->fetch_row_array()');

ok($base->finish, 'ODBC_TXT->finish()');

eval { $base->fetch_row_array()};
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



