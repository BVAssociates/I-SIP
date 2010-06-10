#!/usr/bin/env perl
package me_isip_version;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

ME_ISIP_VERSION.pl - renvoie la version ISIP

=head1 SYNOPSIS

Dans un shell :

 Modele.pl [-h] [-v]

 
=head1 DESCRIPTION

renvoie la version ISIP ainsi que le numero de revision

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

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
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	sortie(202);
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
}

#  Programme Principal
###########################################################
sub run {
	local @ARGV=@_;

#  Traitement des Options
###########################################################

	my %opts;
	getopts('hvn:', \%opts);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};

	my $option_n=$opts{n} if $opts{n};

	#  Traitement des arguments
	###########################################################

	if ( @ARGV < 0) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}

	#  Corps du script
	###########################################################

	print "I-SIP, Version 1.3\n";
}

exit !run(@ARGV) if !caller;

1;