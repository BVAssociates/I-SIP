#!/bin/perl

use strict;
use File::Spec::Functions qw/splitpath catpath canonpath/;
use File::Basename;
use File::Find;
use File::Copy;


############
# CHECKS
############

if (not exists $ENV{BV_HOME} or not exists $ENV{ISIP_HOME}) {
	die "BV_HOME n'est pas instanciť. Avez-vous charger le profile?";
}

############
# CONFIG
############

my %extension_for_converter=(
		perl2exe  => ".exe",
		pl2bat  => ".bat",
	);

my %param_for_converter=(
		perl2exe  => "-I",
		pl2bat  => "",
	);
	
my $converter=shift;
if ( ! grep { $_ eq $converter } keys %extension_for_converter)  {
	die( "usage: ".basename($0)." (".join('|', keys %extension_for_converter).")" );
}

my $bat_dir=canonpath ("$ENV{ISIP_HOME}/V0/bat/bin");

my @find_dir=("$ENV{ISIP_HOME}/V0/");

my @exclude=("/batch/", "/old/");

############
# MAIN
############


sub wanted {
	#$File::Find::dir = /some/path/
	#$_ = foo.ext
	#$File::Find::name = /some/path/foo.ext
	
	return if grep { $File::Find::name =~ /\Q$_\E/ } @exclude;
	my %already_done;
	if ( $File::Find::name =~ /^(.+)\.pl$/i ) {
		
		my $bat_name=$1.$extension_for_converter{$converter};
		
		if ( $already_done{$_} ) {
			warn "duplicate $_!";
		}
		else {
			$already_done{$_}++;
		}
		print $File::Find::name."\n";
		system($converter,$File::Find::name);
		
		move($bat_name, $bat_dir) or die "$bat_dir : $!";
	}
	
}

# nettoyage
my $glob_dir=$bat_dir;
$glob_dir =~ s/\\/\//g;
$glob_dir =~ s/(\s)/\\$1/g;
foreach my $extension ( values %extension_for_converter) {
	unlink $_  or die $! foreach glob("$glob_dir/*".$extension);
}

find( { wanted => \&wanted}, @find_dir);
