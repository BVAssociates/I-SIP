#!/usr/bin/perl

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

 ME_EXEC_JOB.pl [-h][-v] [-c] [-r] [-d jours] [-u user]
 
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

=item -c : creer la base

=item -r : affiche uniquement les jobs en cours

=item -d n : affiche les jobs terminés depuis n jours

=item -u user : affiche les jobs appartenant à un user

=back

=head1 ARGUMENTS

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
getopts('hvu:d:rcs:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

my $username = $opts{u} if $opts{u};
my $dayback = $opts{d} if $opts{d};
my $only_running = $opts{r} if $opts{r};
my $create_base = $opts{c} if $opts{c};

my $separator=',';
$separator=$opts{s} if $opts{s};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}


#  Corps du script
###########################################################

use Isis::JobStat;
use Date::Calc qw(Now Today Add_Delta_Days Delta_DHMS Today_and_Now);


my $sqlite_stat_file=$ENV{ISIP_DATA}.'/tab/SCRIPT_STAT.sqlite';
if ($create_base) {
	log_info("Creation de la base de donnée des jobs");
	JobStat->create_database($sqlite_stat_file);
}
my $sqlite_stat=JobStat->open($sqlite_stat_file,'SCRIPT_STAT');

# remove dead process entries
$sqlite_stat->clean_dead_process();

my @condition;
push @condition, ("(BACKGROUND IS NOT NULL AND BACKGROUND != '')");

if ($dayback) {
	# take now time
	my @date=Now();
	# take yesterday date
	unshift @date,Add_Delta_Days(Today(),-$dayback);
	my $timestamp_back=sprintf("%d%02d%02dT%02d%02d%02d", @date);
	push @condition, ("TIMESTAMP > '$timestamp_back'");
}

push @condition, ("USER = '$username'") if $username;
push @condition, ("CODE IS NULL") if $only_running;
$sqlite_stat->query_condition(@condition);

while (my %proc=$sqlite_stat->fetch_row()) {
	
	my $is_done;
	
	if ($proc{CODE} eq "") {
		$proc{CODE}="EN COURS";
	}
	elsif ($proc{CODE} eq "-1") {
		$proc{CODE}="ANNULE";
		$is_done++;
	}
	elsif ($proc{CODE} eq "0") {
		$proc{CODE}="TERMINE";
		$is_done++;
	}
	else {
		$proc{CODE}="ERREUR";
		$is_done++;
	}
	
	my ($days,$hour,$min,$sec,$month);
	if ($is_done) {
		($sec,$min,$hour,undef,$month,undef,undef,$days,undef) = gmtime($proc{TIME});	
	}
	else {
		my @start_date=$proc{TIMESTAMP} =~ /^(\d{4})-?(\d{2})-?(\d{2})T(\d{2}):?(\d{2}):?(\d{2})$/;
		
		($days,$hour,$min,$sec) = Delta_DHMS(@start_date, Today_and_Now());
	}
	
	# Convert time in humean readable time
	$proc{TIME}='';
	$proc{TIME}.=sprintf("%dd",$days) if $days;
	$proc{TIME}.=sprintf("%dh",$hour) if $hour or $days;
	$proc{TIME}.=sprintf("%dm",$min) if $min or $hour or $days;
	$proc{TIME}.=sprintf("%ds",$sec);
	$proc{TIME} ='erreur' if $month or $proc{CODE} eq "ANNULE"; # no script runs for month!
	
	print(join($separator,@proc{"TIMESTAMP","PID","USER","PROGRAM","TIME","ARGV","CODE","OUTPUT_FILE"}),"\n");
}


