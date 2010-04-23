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
#!/usr/bin/perl
#line 15

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_IKOS_ENV - Liste les environnements disponibles dans IKOS

=head1 SYNOPSIS

 PC_LIST_IKOS_ENV.pl [-h][-v] odbc_name
 
=head1 DESCRIPTION

Liste les datasources ODBC connues

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over

=item odbc_name : nom odbc

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	exit shift;
}

sub usage($) {
	my $verbosity=shift;
	pod2usage(-verbose => $verbosity, -noperldoc => 1);
	sortie(202); 
}

sub log_erreur {
	#print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	$logger->error(@_);
	sortie(202);
}

sub log_info {
	#print STDERR "INFO: ".join(" ",@_)."\n"; 
	$logger->notice(@_);
}


#  Traitement des Options
###########################################################


my %opts;
getopts('hvs:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=',';
$separator=$opts{s} if exists $opts{s};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $odbc_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

#use ITable::ODBC;
use Isip::ITable::ODBC_Query;
use Isip::IsipConfig;

my $config=IsipConfig->new();

# manually set options
my $options={
	%{$config->{defaut_odbc_options}} ,
	odbc_name => $odbc_name
	};

my $table=ODBC_Query->open("IKGSENV","ENVIRON","SELECT ENVENVP.ABCDENV, ENVENVP.ABLBLENV, ENVBIBP.ADBIBLIOT
FROM ENVBIBP INNER JOIN ENVENVP ON ENVBIBP.ADCDENV = ENVENVP.ABCDENV
WHERE ENVBIBP.ADTYPBIB='FL'",$options);


use Data::Dumper;
#die Dumper($table->field);


while (my @line=$table->fetch_row_array()) {
	print join($separator,@line)."\n";
}

__END__
:endofperl
