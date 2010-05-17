#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_STAT_SCRIPT.pl - Calcul des statistiques d'execution des scripts

=head1 SYNOPSIS

 PC_STAT_SCRIPT.pl [-h][-v] [-a] [-i] [-m nb] [-s nb] [-u user]
 
=head1 DESCRIPTION

Calcul des statistiques d'execution des scripts.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -a : affiche les statistques en fonction des arguments

=item -m nb : affiche seulement ceux dont la moyenne est supérieur à nb

=item -s nb : calcul la moyenne flottante à partir des nb dernières executions

=item -u user : filtre sur un utilisateur

=back

=head1 ARGUMENTS

=over

=item environnement

=item module : table du module

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
getopts('hvaim:s:u:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $by_args=$opts{a} if exists $opts{a};
my $ignore_script_case=$opts{i} if exists $opts{i};

my $min_script_average=0;
$min_script_average=$opts{m} if exists $opts{m};

my $last_script_average_nb=10;
$last_script_average_nb=$opts{s} if exists $opts{s};

my $user=$opts{u} if exists $opts{u};

#  Traitement des arguments
###########################################################

if ( @ARGV < 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}


#  Corps du script
###########################################################
my $bv_severite=0;

use ITable::ITools;
use ITable::Sqlite;
use File::Spec;
use List::Util qw(sum);

my $stat_tablename="SCRIPT_STAT";
my ($sqlite_path)=grep {$_=File::Spec->catfile($_,$stat_tablename.".sqlite");-e $_} split(/;/,$ENV{BV_TABPATH});
my $stats=Sqlite->open($sqlite_path, $stat_tablename);

my $dest=ITools->open("SCRIPT_STAT_AVERAGE");
my $sep=$dest->output_separator;
undef $dest;

my %script_exec_all;
my %script_exec_fail;
my %script_time_last;

while (my %script=$stats->fetch_row() ) {
	# don't care of line about starting
	next if $script{CODE} eq "";
	
	next if $user and $script{USER} ne $user;
	
	$script{PROGRAM} = lc($script{PROGRAM}) if $ignore_script_case;
	
	my $script_id;
	if ($by_args) {
		$script_id=$script{PROGRAM}." ".$script{ARGV};
	}
	else {
		$script_id=$script{PROGRAM};
	}
	
	$script_exec_all{$script_id}++;
	
	$script_exec_fail{$script_id}=0;
	$script_exec_fail{$script_id}++ if $script{CODE} != 0;
	
	$script{TIME}=0 if not $script{TIME};
	push @{$script_time_last{$script_id}}, $script{TIME} ;
}


foreach (keys %script_exec_all) {

	my $median_time;
	my $average_last_time;
	my $average_total_time;
	my $total_time;
	
	$total_time=sum(@{$script_time_last{$_}});
	$average_total_time= $total_time/ $script_exec_all{$_};
	
	if (int($average_total_time)) {
		if (@{$script_time_last{$_}} > $last_script_average_nb) {
			$average_last_time= sum(@{$script_time_last{$_}}[-$last_script_average_nb..-1]) / $last_script_average_nb;
		}
		else {
			$average_last_time=$average_total_time;
		}

		$median_time=(sort @{$script_time_last{$_}})[@{$script_time_last{$_}}/2];

		next if $average_last_time < $min_script_average;
		
		print join($sep,$_,$script_exec_all{$_},$script_exec_fail{$_},int($total_time),int($average_total_time),int($average_last_time),int($median_time))."\n";
	}
}

 