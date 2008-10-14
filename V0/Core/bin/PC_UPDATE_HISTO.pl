#!/usr/bin/perl

use strict;

use IKOS::Sqlite;


my $environ=shift or die;
my $tablename=shift or die;

# Connection to local database
my $referentiel=Sqlite->open("IKOS_".$environ,$tablename, {debug => 1, timeout => 0});

$|=1;
print join(":",$referentiel->fetch_row_array)."\n";

#print $referentiel->insert_row_array( (undef) x 3, ("ok") x 8)."\n";
my $last_id=$referentiel->insert_row( ID=> undef, ENVIRONNEMENT => $environ,  TABLE_NAME => $tablename, TABLE_KEY => "AL'TEST" ,FIELD_NAME => 10)."\n";

$referentiel->execute("DELETE from $tablename where ID = $last_id;");