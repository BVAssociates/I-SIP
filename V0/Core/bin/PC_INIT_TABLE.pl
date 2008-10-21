#!/usr/bin/perl

use strict;

use IKOS::SIP;

my $tablename=shift or die;

print SIP->SQL_drop($tablename);
print "\n";
print SIP->SQL_create($tablename);

exit;
