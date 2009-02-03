#!/bin/perl

use strict;
use File::Spec::Functions;
use File::Basename;


############
# CONFIG
############


my $package_name="IsipPackage.par";
my $package_dir=canonpath ("$ENV{ISIP_HOME}/V0/batch/bin");

my @bin_dir=("$ENV{ISIP_HOME}/V0/Core/bin");

my @force_include=("ITable::ITools::Ft");

my $filter="Bleach";
my @filter_pattern=("\\.pl\$","Isip.*","ITable.*");


############
# CHECKS
############

if (not exists $ENV{BV_HOME} or not exists $ENV{ISIP_HOME}) {
	die "BV_HOME n'est pas instancié. Avez-vous charger le profile?";
}

if (! -w $package_dir or ! -d $package_dir) {
	die "$package_dir must be a writable directory";
}

############
# MAIN
############

my @file_list;
foreach my $dir (@bin_dir) {
	opendir(BIN_DIR, $dir) || die("Cannot open directory"); 
	push @file_list, map {"$dir/$_"} readdir(BIN_DIR); 
	closedir(BIN_DIR);
}


if (not grep {m[V0/Core/lib]} @INC) {
	push @INC, "$ENV{ISIP_HOME}/V0/Core/lib/";
}

my $filter_option=join(' ',map {"-F $filter=$_"} @filter_pattern);

my $myself=basename($0);
my $file_option=join(' ', map {"\"$_\""} grep {/\.pl/} @file_list);

my $include_option=join(' ', map {"-M $_"} @force_include);
my $cmd="pp -p $include_option $filter_option -o \"$package_dir/$package_name\" $file_option";

print "execute : $cmd";
system($cmd);
my $code=$?>>8;

die "something wrong with : $cmd" if $code;


foreach my $script (grep {!/$myself/} grep {/\.pl/} @file_list) {
	$script=basename($script);
	print "writing $package_dir/$script\n";
	open(BAT,">$package_dir/$script\.bat") or die $!;
	my $bat_launcher='@perl -MPAR "%~dp0'.$package_name.'" "%~n0" %*';
	print BAT $bat_launcher;
	close(BAT);
}
