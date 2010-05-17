#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

ME_EXEC_JOB - Execute un script Perl en tâche de fond

=head1 SYNOPSIS

 ME_EXEC_JOB.pl [-h][-v] [-o fichier] script[.pl] [arg1 arg2 ...]
 
=head1 DESCRIPTION

Execute un script Perl en tâche de fond

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -o : redirige la sortie du programme dans un fichier

=back

=head1 ARGUMENTS

=item script : nom du script Perl

=item arg1 arg2 ... : argument du script Perl

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

sub ErrorReport {
    return "windows erreur: ",Win32::FormatMessage( Win32::GetLastError() );
}

#  Traitement des Options
###########################################################


my %opts;
getopts('hvo:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

my $output_file = $opts{o} if $opts{o};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $progname=shift;
my @args=@ARGV;

#  Corps du script
###########################################################

use Isis::JobExec;
use Isis::JobStat;

my $jobs=JobExec->new();

if ($output_file) {
	# touch the file
	#open (OUTPUT_FILE, '>',$output_file) or die($output_file,' : ',$!);
	#close(OUTPUT_FILE);
	
	$ENV{OUTPUT_FILE}=$output_file;
}

$jobs->exec_script($progname,@args);

if ($jobs->is_running) {
	$logger->notice("Veuillez consulter les Jobs pour voir l'avancement du programme");
}