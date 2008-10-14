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


my $environnement=$ENV{ENVIRON};
my $table=$ENV{GSL_FILE};
my $field=$ENV{Key};

my $database_name="IKOS_$environnement.sqlite3";

my @define=`Define_Table $table`;
my ($key_field)=grep { $_=$1 if /KEY=(\w+)/ } @define;
print STDERR $key_field;

my $database=find_bv_tabpath($database_name);

my @result;
if (defined $database) {
	print STDERR "found : $database";
	print STDERR "select * from $table\_HISTO where FIELD_NAME=\"$field\" AND TABLE_KEY=\"$ENV{$key_field}\";";
	@result=`sqlite3 "$database" "select * from $table\_HISTO where FIELD_NAME=\"$field\" AND TABLE_KEY=\"$ENV{$key_field}\";"`;
	print STDERR $@;
	print @result;
} else {
	die "unable to find $database_name"
}

