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

define_table - 

=head1 SYNOPSIS

 Define_Table.pl [-h][-v] encapsuleur Define_Table.exe
 
=head1 DESCRIPTION

Appelle Define_Table.exe et modifie certaines variables de la définition
à la volée.

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

=item tablename : table a définir

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

my $table=shift;
my $date_explore=shift;

$date_explore=$ENV{DATE_EXPLORE} if not $date_explore;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;

my @output=`define_table.exe $table`;

my $new_format;
my $new_size;
my ($env_name,$table_real) = $table =~ /^IKOS_TABLE_([A-Za-z0-9]+)_(\w+)$/;
if ($env_name and $table_real and $date_explore) {
	log_info("modification de la définition à la volée");
	
	require "PC_GENERATE_MENU.pl";
	log_erreur("<Environnement> n'est pas défini") if not $env_name;
	my $env=Environnement->new($env_name);
	
	my @definition=split(/\n/,pc_generate_menu::get_def_table_string($env,$table_real,$date_explore));
	($new_format,$new_size)=map {s/^SIZE=\"//;s/^FORMAT=\"//;s/\"$//;$_} grep {/^(FORMAT|SIZE)/} @definition;

	
}

foreach (@output) {
	if ($new_format and /^(set )?FORMAT=/) {
		s/FORMAT=.+/FORMAT=$new_format/;
	}
	elsif ($new_format and /^(set )?ROW=/) {
		my $new_row=join('@', map {"%".$_."%"} split(/@/,$new_format));
		s/ROW=.+/ROW=$new_row/;
	}
	elsif ($new_size and /^(set )?SIZE=/) {
		s/SIZE=.+/SIZE=$new_size/;
	}
	
	print;
}

__END__
:endofperl
