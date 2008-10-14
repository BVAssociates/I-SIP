#!/usr/bin/perl

use strict;

use IKOS::SIP;


my $environnement=shift or die;
my $tablename=shift or die;

my $ikos_sip = SIP->new($environnement);

my $table = $ikos_sip->open_local_table($tablename, {debug => 0 });

if (not defined $table) {
	die "error opening $tablename in env $environnement";
}

print join("\n",$table->field)."\n";