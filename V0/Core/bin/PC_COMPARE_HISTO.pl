#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_COMPARE_HISTO - les champs les champs d'une table Historique depuis la r�f�rence

=head1 SYNOPSIS

 PC_COMPARE_HISTO.pl environnement tablename
 
=head1 DESCRIPTION

Compare 2 objets de type DATA_interface et affiche le resultat sous forme de table

=head1 ENVIRONNEMENT

=over 4

=item Environnement : Environnement en cours d'exploration

=item DATE_EXPLORE : Date en cours d'exploration

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head1 ARGUMENTS

=head2 * environnement de destination

=head2 * table a comparer

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

if ( @ARGV != 2 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
#my $environnement_from=shift;
my $environnement_to=shift;
my $table_name=shift;

#recuperation de l'environnement
my $environnement_from=$ENV{Environnement} or die "Environnement does not exist in env";
my $date_explore=$ENV{DATE_EXPLORE};

#  Corps du script
###########################################################
my $bv_severite=0;

use IKOS::SIP;
use IKOS::DATA::ITools;

use POSIX qw(strftime);

my $env_sip_from = SIP->new($environnement_from);
my $env_sip_to = SIP->new($environnement_to);
	
#open IKOS table for DATA
my $table_from=$env_sip_from->open_local_from_histo_table($table_name, {debug => $debug_level});
$table_from->query_date($date_explore) if $date_explore;

my $table_to=$env_sip_to->open_local_from_histo_table($table_name, {debug => $debug_level});

$table_from->compare_from($table_to);

sortie($bv_severite);