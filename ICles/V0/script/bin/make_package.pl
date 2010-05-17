#!/usr/bin/env perl

use strict;
use File::Spec::Functions qw/splitpath catpath canonpath/;
use File::Basename;


############
# CONFIG
############


my $package_name="IsipPackage.par";
my $package_dir=canonpath ("$ENV{ISIP_HOME}/V0/batch/bin");

my @bin_dir=("$ENV{ISIP_HOME}/V0/Core/bin");

my @force_include=("ITable::ITools::Ft");

my $filter="Bleach";
my @filter_pattern=("Isip.*","ITable.*");

my $option="";

# don't work
my %meta;
#Comments        CompanyName     FileDescription FileVersion
#InternalName    LegalCopyright  LegalTrademarks OriginalFilename
#ProductName     ProductVersion

$meta{license}="BV Associates 2008";


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

my $filter_option=join(' ',map {"-F $filter=$_"} @filter_pattern) if $filter;

my $myself=basename($0);
my $file_option=join(' ', map {"\"$_\""} grep {/\.pl/} @file_list);

my $include_option=join(' ', map {"-M $_"} @force_include);

my $meta_option=join(' ',map {"-N=\"$_=$meta{$_}\""} keys %meta);
my $cmd="pp -p $option $meta_option $include_option $filter_option -o \"$package_dir/$package_name\" $file_option";

print "execute : $cmd\n";
system($cmd);
my $code=$?>>8;

die "something wrong with : $cmd" if $code;

my $script_template='require PAR;
use File::Spec::Functions qw/splitpath catpath/;
my ($lecteur,$path,$script)=splitpath($0);
my $par=catpath($lecteur,$path,"IsipPackage.par" );
PAR->import( { file => $par, run => "%s" } );
';


foreach my $script (grep {!/$myself/} grep {/\.pl/} @file_list) {
	my ($vol,$dir,$script_name)=splitpath($script);
	print "writing $package_dir/$script_name\n";
	open(SCRIPT,">$package_dir/$script_name") or die $!;
	my $script_launcher=sprintf($script_template,$script_name);
	print SCRIPT $script_launcher;
	close(SCRIPT);
}
