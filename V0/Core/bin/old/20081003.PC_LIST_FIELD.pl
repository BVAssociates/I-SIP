#!/usr/bin/perl

use strict;

sub find_bv_tabpath ($) {
	my $file=shift;
	
	my @bv_tabpath=split(";",$ENV{BV_TABPATH});
	
	foreach my $path (@bv_tabpath) {
		return $path."/".$file if -r $path."/".$file ;
	}
	return undef
}


my $ENV=shift or die;
my $table=shift or die;

my $database_name="IKOS_$ENV.sqlite3";

my $database=find_bv_tabpath($database_name);

my @result;
if (defined $database) {
	print STDERR "found : $database";
	@result=`sqlite3 -batch "$database" "select sql from sqlite_master where tbl_name=\'$table\';"`;
	print STDERR $@;
	foreach (@result) {
		next if /^CREATE|DATE_COLLECTE|TIME_COLLECTE|HEURE_COLLECTE/;
		print "$1\n" if /^(\w+)/;
	}
} else {
	die "unable to find $database_name"
}

