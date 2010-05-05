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
	die "BV_HOME n'est pas instancié. Avez-vous charger le profile?";
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

# par defaut
$converter='pl2bat';

if ( ! grep { $_ eq $converter } keys %extension_for_converter)  {
	die( "usage: ".basename($0)." (".join('|', keys %extension_for_converter).")" );
}

my $bat_dir=canonpath ("$ENV{ISIP_HOME}/V0/bat/bin");

my @find_dir=("$ENV{ISIP_HOME}/V0/");

my @exclude=("/batch/", "/old/", "/.svn");

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
		my $bat_dest=$bat_dir."/".basename($1).$extension_for_converter{$converter};
		
		if ( $already_done{$_} ) {
			warn "duplicate $_!";
		}
		else {
			$already_done{$_}++;
		}
		
		if ( -r $bat_dest ) {
			my $time_ori = (stat($File::Find::name))[9];
			my $time_new = (stat($bat_dest))[9];
			
			if ( $time_ori < $time_new ) {
				return;
			}
		}
				
		print $File::Find::name."\n";
		system($converter,$File::Find::name);
		
		move($bat_name, $bat_dest) or die "$bat_dest : $!";
	}
	
	return;
}

find( { wanted => \&wanted}, @find_dir);
