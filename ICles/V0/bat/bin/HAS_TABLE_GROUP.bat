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

HAS_TABLE_GROUP - sors avec un code retour 0 si une table contient des groupes

=head1 SYNOPSIS

 HAS_TABLE_GROUP.pl [-h][-v] environnement table_name
 
=head1 DESCRIPTION

Verifie dans la base que la table contient des groupes

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

=item environnement : nom de l'environnement

=item table_name : nom de la table

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
getopts('hv', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift @ARGV;
my $tablename=shift @ARGV;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;

my $count;

# check case where no GROUP possible
my $no_group;
$no_group++ if exists $ENV{TYPE_SOURCE} and $ENV{TYPE_SOURCE} eq "XML";
$no_group++ if exists $ENV{ENV_COMPARE};
$no_group++ if exists $ENV{DATE_COMPARE};


if ($no_group)
{
	$count=0;
}
else {
	my $env=Environnement->new($environnement);

	my $table=$env->open_local_table($tablename."_CATEGORY");

	$table->custom_select_query("SELECT count(*) from ".$tablename."_CATEGORY");
	($count)=$table->fetch_row_array();
}

log_info("$count groupes dans $tablename de $environnement");

#return 0 if group > 0
exit not $count;

__END__
:endofperl
