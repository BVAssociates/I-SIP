#!/usr/bin/env perl

use strict;
use File::Spec::Functions qw/splitpath catpath canonpath/;
use File::Basename;
use File::Find;
use File::Copy;

sub convert_unix {
	my $filename=shift;

	if ( ! $filename ) {
		die ('usage:convert_unix(filename)');
	}

	my $new_filename = $filename;
	$new_filename =~ s/\.pl$//;

	#copy($filename, $new_filename);
	open my $file_fd, $filename or die $filename,$!;
	open my $new_file_fd, '>', $new_filename or die $new_filename,$!;

	while (my $line = <$file_fd> ) {
		$line =~ s/\r$//;
		print $new_file_fd $line or die $!;
	}

	close $file_fd;
	close $new_file_fd;
	chmod 0755, $new_filename;

	return;
}

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
		unix  => "",
	);

my %converter_for_type=(
		perl2exe  => "perl2exe -I",
		pl2bat  => "pl2bat",
		unix  => \&convert_unix,
	);

my %default_converter_for_os=(
		MSWin32  => 'pl2bat',
		linux    => 'unix',
		aix      => 'unix',
	);
	
my $converter_type=shift;

if ( ! defined $converter_type ) {
	$converter_type=$default_converter_for_os{$^O};
	
	if ( $converter_type ) {
		print "Utilisation du convertisseur par default : $converter_type\n";
	}
}

if ( ! grep { $_ eq $converter_type } keys %extension_for_converter)  {
	die( "usage: ".basename($0)." (".join('|', keys %extension_for_converter).")" );
}

my $converter=$converter_for_type{$converter_type};

my $bat_dir=canonpath ("$ENV{ISIP_HOME}/V0/bat/bin");

my @find_dir=("$ENV{ISIP_HOME}/V0/");

my @exclude=("/batch/", "/old/", "/.svn");

############
# MAIN
############


my %already_done;
sub wanted {
	#$File::Find::dir = /some/path/
	#$_ = foo.ext
	#$File::Find::name = /some/path/foo.ext
	
	return if grep { $File::Find::name =~ /\Q$_\E/ } @exclude;
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

		if ( ref($converter) ) {
			$converter->($File::Find::name);
		}
		else {
			system($converter,$File::Find::name);
		}
		
		move($bat_name, $bat_dest) or die "$bat_dest : $!";
	}
	
	return;
}

find( { wanted => \&wanted}, @find_dir);
