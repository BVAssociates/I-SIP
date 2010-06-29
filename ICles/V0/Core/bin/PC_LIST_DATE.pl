#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_DATE - Liste les date de mise à jour d'une table dans un environnement

=head1 SYNOPSIS

 PC_LIST_DATE.pl [-h][-v] [-s sep] [-n] [-b] environnement
 
=head1 DESCRIPTION

Liste les date de collecte d'une table dans un environnement

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -n : affiche la date courante à la fin

=item -b : uniquement les baselines

=item -a : affiche les dates sans modifications

=back

=head1 ARGUMENTS

=over 4

=item environnement : environnement à utiliser

=item tablename : table a ouvrir

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
getopts('hvns:ba', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $print_now=$opts{n} if exists $opts{n};
my $baseline=$opts{b} if exists $opts{b};
my $all_date=$opts{a} if exists $opts{a};

my $separator=',';
$separator=$opts{s} if exists $opts{s};




#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environ=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;

my $sip=Environnement->new($environ);
my $table=$sip->open_local_table("DATE_UPDATE", {debug => $debug_level });

$table->query_field("DATE_HISTO","DESCRIPTION","BASELINE");
$table->query_condition("DIFF_VALUE+DIFF_STRUCT >0 AND FULL_UPDATE=1") if not $all_date;
$table->query_condition("BASELINE = 1") if $baseline;

die "unable to open local DATE_UPDATE in env $environ" if not defined $table;

while (my %line=$table->fetch_row()) {
	print join($separator,@line{$table->query_field})."\n";
}

if ($print_now) {
	# special date
	use POSIX qw(strftime);
	my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
	print join($separator,($timestamp,"maintenant",0))."\n";
}
