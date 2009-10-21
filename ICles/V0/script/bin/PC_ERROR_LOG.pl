#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

upgrade_column_info - 

=head1 SYNOPSIS

 upgrade_column_info.pl [-h][-v] [-m resp] log_file
 
=head1 DESCRIPTION

Met à jour les bases de données ISIP avec la nouvelle colonne

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -m : envoi un Mail de rapport aux utilisateur ayant la responsabilité donnée

=back

=head1 ARGUMENTS

=over

=item log_file : log à analyser

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
getopts('hvm:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $mail_to_resp = $opts{m};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

#my $log_file=shift @ARGV;

#  Corps du script
###########################################################

use Isip::IsipConfig;

my @error_list;
while(<>)  {
	if ( m{Collecte de l\'environnement (\w+)$} ) {
		# decommente si la log n'est pas parallelisée
		#push @error_list, $_;
	}
	
	if ( m{\d+/\d+/\d+ \d+:\d+:\d+:(ERROR|CRITICAL)} ) {
		push @error_list, $_;
	}
}

# affiche les erreurs à l'ecran
print @error_list;

# Envoi les erreurs dans un Mail
if ( $mail_to_resp and grep { /(ERROR|CRITICAL)/ } @error_list ) {

	unshift @error_list, "Voici la liste des erreurs detectés pendant la dernière collecte : \n\n";
	
	my $config = IsipConfig->new();
	$config->send_mail("I-SIP : Erreurs de collecte", join('', @error_list), $mail_to_resp);
}
