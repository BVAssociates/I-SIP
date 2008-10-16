#!/usr/bin/perl

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

 PC_LIST_FIELD_HISTO.pl [-h] [-v] environnement tablename
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement à la date courante

=head1 ENVIRONNEMENT

=over 4

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS 

=over 4

=item * environnement à utiliser

=item * table a décrire

=back

=head1 AUTHOR

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
my $environnement=shift;
my $tablename=shift;

#  Corps du script
###########################################################

use IKOS::SIP;

my $bv_severite=0;


my $ikos_sip = SIP->new($environnement);
my $table = $ikos_sip->open_local_table($tablename, {debug => 0 });

if (not defined $table) {
	die "error opening $tablename in env $environnement";
}

print join("\n",$table->field)."\n";

sortie($bv_severite);