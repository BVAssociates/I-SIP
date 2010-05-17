#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

ME_CLEAN_PAR - Supprime le cache des PAR

=head1 SYNOPSIS

 ME_CLEAN_PAR.pl
 
=head1 DESCRIPTION

Ce script doit être executé après chaque mise à jour de l'ICles

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
getopts('hv', \%opts) or usage(0);

my $debug_level = 0;
my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};



#  Traitement des arguments
###########################################################

if ( @ARGV < 0 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

#  Corps du script
###########################################################
my $bv_severite=0;

use File::Path 'rmtree';
log_info("Cleaning PAR temp files");

my $par_temp=$ENV{TEMP};

if (-r $par_temp."/par-isip_service") {
	rmtree($par_temp."/par-isip_service", {verbose => $debug_level});
}
else {
	log_erreur("unable to find a PAR Cache at $par_temp");
}

sortie($bv_severite);