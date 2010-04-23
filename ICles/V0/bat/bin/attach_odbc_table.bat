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
use Carp;

#  Documentation
###########################################################
=head1 NAME

attach_odbc_table - construit une base Sqlite pour simuler une base IKOS

=head1 SYNOPSIS

 attach_odbc_table.pl [-h][-v] repertoire
 
=head1 DESCRIPTION

Sauvegarde toutes les tables utilisées dans I -SIP IKOS dans une table
SQLite locale.

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

=item repertoire : repertoire de sauvegarde des fichiers SQLite

=back

=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits réservés.

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

sub log_error {
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
getopts('hv', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $data_dir=shift @ARGV;
#$data_dir =~ s{\\}{/}g;
#$data_dir =~ s{\s}{\\ }g;

if (not -d $data_dir or not -w $data_dir) {
	die("$data_dir n'est pas accessible en écriture");
}

#  Corps du script
###########################################################
my $bv_severite=0;

use ITable::Sqlite;
use File::Spec;

if ( ! -r $data_dir or ! -d $data_dir) {
	usage($debug_level);
	sortie(202);
}

my @sqlite_list = glob ("'".$data_dir."/*.sqlite'");

foreach my $sqlite_file (@sqlite_list) {
	
	if ( ! -r $sqlite_file) {
		log_error("non accessible en lecture : $sqlite_file");
	}
	$sqlite_file =~ /(\w+)\.(\w+)\.sqlite/;
	
	my ($host, $database_name) = ($1 , $2);
	next if not $database_name;
	
	my $sqlite_master_file = "$data_dir/$host.sqlite";
	
	if ( ! -e $sqlite_master_file) {
		log_info("touch $sqlite_master_file");
		open(NEW,'>',$sqlite_master_file) or die ($sqlite_master_file,' : ',$!);
		close(NEW);		
	}
	
	my $sqlite_host = Sqlite->open($sqlite_master_file, "sqlite_master");
	
	log_info("ATTACH DATABASE '$sqlite_file' as $database_name");
	$sqlite_host->execute("ATTACH DATABASE '$sqlite_file' as $database_name");
	
	$sqlite_host->custom_select_query("PRAGMA database_list");
	while (my (undef,$name,$base) = $sqlite_host->fetch_row_array())
	{
		print "$name:$base\n";
	}
	$sqlite_host->close();
}
__END__
:endofperl
