#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

HAS_FIELD_MENU - sors avec un code retour 0 si une table doit afficher le menu
d'exploration des champs

=head1 SYNOPSIS

 HAS_FIELD_MENU.pl [-h][-v]
 
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
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	#$logger->error(@_);
	sortie(202);
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
	#$logger->notice(@_);
}


#  Traitement des Options
###########################################################

my %opts;
getopts('hv', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

# take info from ENV
log_erreur("ICON n'est pas defini dans l'environnement") if not exists $ENV{ICON};
my $icon=$ENV{ICON};

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::IsipFilter;

my $filter=IsipFilter->new();

#remove dirty information and re-check icon
$icon =~ s/_dirty//;

my $display_field_menu=0;
if ($filter->is_display_line(ICON => $icon)) {
	$display_field_menu=1;
}


exit not $display_field_menu;