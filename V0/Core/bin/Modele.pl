#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_HISTO - Liste les champs d'une table dans un environnement

=head1 SYNOPSIS

 PC_LIST_FIELD_HISTO.pl environnement tablename
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement � la date courante

=head2 ENVIRONNEMENT

=over 4

=item ITOOLS

L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head2 OPTIONS

=over 4

=item -h

Affiche l'aide en ligne

=item -v

Mode verbeux

=back

=head2 ARGUMENTS 

=over 4

=item *

environnement � utiliser

=item *

table a d�crire

=back

=head2 AUTHOR

BV Associates, 16/10/2008

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


#  Traitement des Options
###########################################################


my %opts;
getopts('hv', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV != 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $arg1=shift;
my $arg2=shift;

#  Corps du script
###########################################################
my $bv_severite=0;



sortie($bv_severite);