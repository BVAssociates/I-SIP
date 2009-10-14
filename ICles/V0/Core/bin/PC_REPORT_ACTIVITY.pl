#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Date::Calc qw(:all);

use Isip::IsipLog '$logger';
use Mail::Sender;

#  Documentation
###########################################################
=head1 NAME

PC_REPORT_ACTIVITY - Liste le contenu d'une table sqlite

=head1 SYNOPSIS

 PC_REPORT_ACTIVITY.pl [-h][-v] [-m] [-b nb_jours_fin] -c nb_jours environnement
 
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

=item -m : Effectue l'envoie du courriel

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

sub log_error {
	#print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	$logger->error(@_);
	sortie(202);
}

sub log_info {
	#print STDERR "INFO: ".join(" ",@_)."\n"; 
	$logger->notice(@_);
}

sub get_date_interval {
	
	my $day_count=shift;
	my $day_offset=shift;
	
	my @today=Today_and_Now();
	
	my @date_begin;
	my @date_end=@today;
	
	if ($day_offset ) {
		@date_end=Add_Delta_DHMS(@today, -$day_offset, 0,0,0);
	}
	
	if ($day_count ) {
		@date_begin=Add_Delta_DHMS(@date_end, -$day_count, 0,0,0);
	}
	
	
	my $date_begin_txt;
	$date_begin_txt= sprintf("%d-%02d-%02dT%02d:%02d", @date_begin) if @date_begin;
	
	my $date_end_txt;
	$date_end_txt= sprintf("%d-%02d-%02dT%02d:%02d", @date_end);
	
	return ($date_begin_txt, $date_end_txt);
}


#  Traitement des Options
###########################################################


my %opts;
getopts('hvc:b:m', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $day_count=$opts{c};
my $day_offset=$opts{b};

my $send_mail=$opts{m};

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
use Isip::IsipConfig;
use Isip::Environnement;

# dates en francais
Language(2);

my @date_today=Today;
my $date_today_txt=Date_to_Text_Long(@date_today[0..2]);

my $mail_subject = q{Rapport d'activité du }.$date_today_txt;
my $mail_message = $mail_subject."\n\n";

my ($date_begin,$date_end)=get_date_interval($day_count, $day_offset);

my $date_begin_txt;
my $date_end_txt;
if ($date_begin and $date_begin =~ /^(\d+)-(\d+)-(\d+)T(\d+):(\d+)$/) {
	$date_begin_txt=Date_to_Text_Long($1,$2,$3)." ($4h$5)";
}
if ($date_end =~ /^(\d+)-(\d+)-(\d+)T(\d+):(\d+)$/) {
	$date_end_txt=Date_to_Text_Long($1,$2,$3)." ($4h$5)";
}

if ( $date_begin ) {
	$mail_message .= "Calculs pour la période comprises entre le ".$date_begin_txt." et le ".$date_end_txt."\n\n";
}
else {
	$mail_message .= "Calculs pour la période avant le ".$date_end_txt."\n\n";
}


my $env=Environnement->new($environnement);

my $report=IsipReport->new($env);
my $histo_count=$report->get_update_histo_count($date_begin, $date_end);
my $comment_count=$report->get_update_comment_count($date_begin, $date_end);
my $invalid_count=$report->get_update_invalid_count($date_begin, $date_end);

$mail_message .= "Nombre de modification de valeur de champs : ".$histo_count."\n";
$mail_message .= "Nombre de modification validés : ".$comment_count."\n";
$mail_message .= "Nombre de modification à commenter : ".$invalid_count."\n";

if ( $histo_count ) {
	$mail_message .= "Taux de mise à jour utilisateur : ". sprintf ('%.2f %%',100 * $comment_count / $histo_count)."\n";	
}

$mail_message .= "Taux de mise à jour validées : ". sprintf ('%.2f %%',100 * ($histo_count - $invalid_count) / $histo_count)."\n";

print $mail_message."\n";

if ( $send_mail ) {

	my $config=IsipConfig->new();
	my $smtp_host=$config->get_config_var("smtp_host");
	my $smtp_from=$config->get_config_var("smtp_from");

	log_info("Connexion SMTP : $smtp_host");
	my $sender = Mail::Sender->new(  {smtp => $smtp_host, from => $smtp_from});
	if (not ref $sender) {
		log_error("Probleme de connexion à $smtp_host");
	}
	
	log_info("Envoi du mail");
	my $success = $sender->MailMsg({to => 'bauchart@bvassociates.fr',
	  subject => $mail_subject,
	  msg => $mail_message}
	  );
	  
	if (not $success) {
		log_error("Probleme lors de l'envoi du mail");
	}
	else {
		log_info("Mail envoyé");
	}
}



sub swrite {
	die "usage: swrite PICTURE ARGS" unless @_;
	my $format = shift;
	$^A = "";
	formline($format,@_);
	return $^A;
}

my $string = swrite(<<'END', 10000, 2, 3);
Check me out
@<<<  @|||
@>>>
END
print $string;