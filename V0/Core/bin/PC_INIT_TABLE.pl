#!/usr/bin/perl

use strict;

use IKOS::SIP;


my $environnement=shift or die;
my $tablename=shift or die;

my $ikos_sip = SIP->new($environnement);

print $ikos_sip->SQL_drop($tablename);
print $ikos_sip->SQL_create($tablename);

exit;
