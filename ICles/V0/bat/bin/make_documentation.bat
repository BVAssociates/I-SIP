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
use Pod::Simple::HTMLBatch;


my @search_dirs=("$ENV{ISIP_HOME}/V0/Core/");
my $output_dir='D:\ISIP\trac_server\htdocs\isip-doc';

my $batchconv = Pod::Simple::HTMLBatch->new;
$batchconv->add_css("/trac_server/chrome/common/css/trac.css",1);
$batchconv->batch_convert( \@search_dirs, $output_dir );
__END__
:endofperl
