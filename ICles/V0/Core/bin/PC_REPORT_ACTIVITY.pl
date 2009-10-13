#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Date::Calc qw(:all);

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_REPORT_ACTIVITY - Liste le contenu d'une table sqlite

=head1 SYNOPSIS

 PC_REPORT_ACTIVITY.pl [-h][-v] [-b nb_jours_fin] -c nb_jours environnement
 
=head1 DESCRIPTION

Affiche des compte-rendu d'activité sur la base I-SIP

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -b : spécifie le nombre de jours à partir duquel est fait le calcul

=item -c : spécifie le nombre de jours de l'intervale de calcul

=back

=head1 ARGUMENTS

=over

=item environnement

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

sub get_date_interval {
	
	my $day_count=shift or croak("usage: _get_date_interval(day_count [,day_back])");
	my $day_offset=shift;
	
	my @today=Today_and_Now();
	
	my @date_begin;
	my @date_end=@today;
	
	@date_begin=Add_Delta_DHMS(@date_end, -$day_count, 0,0,0);
	@date_end=Add_Delta_DHMS(@today, -$day_offset, 0,0,0) if $day_offset;
	
	my $date_begin_txt= sprintf("%d-%02d-%02dT%02d:%02d", @date_begin);
	my $date_end_txt= sprintf("%d-%02d-%02dT%02d:%02d", @date_end);
	
	return ($date_begin_txt, $date_end_txt);
}


#  Traitement des Options
###########################################################


my %opts;
getopts('hvc:b:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $day_count=$opts{c} or usage($debug_level+1);
my $day_offset=$opts{b};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::IsipReport;
use Isip::Environnement;

my ($date_begin,$date_end)=get_date_interval($day_count, $day_offset);

my $date_begin_txt;
my $date_end_txt;
Language(2);
if ($date_begin =~ /^(\d+)-(\d+)-(\d+)T(\d+):(\d+)$/) {
	$date_begin_txt=Date_to_Text_Long($1,$2,$3)." ($4h$5)";
}
if ($date_end =~ /^(\d+)-(\d+)-(\d+)T(\d+):(\d+)$/) {
	$date_end_txt=Date_to_Text_Long($1,$2,$3)." ($4h$5)";
}
print "Calcul pour les dates comprises entre : ".$date_begin_txt." et ".$date_end_txt."\n";


my $env=Environnement->new($environnement);

my $report=IsipReport->new($env);
my $histo_count=$report->get_update_histo_count($date_begin, $date_end);
my $comment_count=$report->get_update_comment_count($date_begin, $date_end);
my $invalid_count=$report->get_update_invalid_count($date_begin, $date_end);

print "Taux de mise à jour utilisateur : ". sprintf ('%.2f %%',100 * $comment_count / $histo_count)."\n";
print "Taux de mise à jour global : ". sprintf ('%.2f %%',100 * ($histo_count - $invalid_count) / $histo_count)."\n";


