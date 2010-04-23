@rem = '--*-Perl-*--
@echo off
if "%OS%" == "Windows_NT" goto WinNT
perl -x -S "%0" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto endofperl
:WinNT
perl -x -S %0 %*
if NOT "%COMSPEC%" == "%SystemRoot%\system32\cmd.exe" goto endofperl
if %errorlevel% == 9009 echo You do not have Perl in your PATH.
if errorlevel 1 goto script_failed_so_exit_with_non_zero_val 2>nul
goto endofperl
@rem ';
#!/bin/perl
#line 15

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
		
		my $bat_name=$1.".bat";
		
		if ( $already_done{$_} ) {
			warn "duplicate $_!";
		}
		else {
			$already_done{$_}++;
		}
		print $File::Find::name."\n";
		system('pl2bat',$File::Find::name);
		
		move($bat_name, $bat_dir) or die "$bat_dir : $!";
	}
	
}

my $glob_dir=$bat_dir;
$glob_dir =~ s/\\/\//g;
$glob_dir =~ s/(\s)/\\$1/g;
unlink $_  or die $! foreach glob("$glob_dir/*.bat");

find( { wanted => \&wanted}, @find_dir);

__END__
:endofperl
