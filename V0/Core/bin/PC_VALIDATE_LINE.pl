#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;


###########################################################
=head1 NAME

PC_VALIDATE_LINE

=head1 SYNOPSIS

 PC_VALIDATE_LINE.pl environnement table
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement à la date courante

=head2 ENVIRONNEMENT

=over 4

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head2 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head2 ARGUMENTS 

=over 4

=item * environnement à utiliser

=item * nom de la table a décrire

=item * nom du champ

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
	pod2usage(-verbose => $verbosity+1, -noperldoc => 1);
	sortie(202); 
}

sub log_erreur {
	print STDERR "ERREUR: ".join(" ",@_)."\n";
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

# quirk to test
#$ENV{Environnement}=$environnement;
#$ENV{GSL_FILE}=$tablename;

##for debug
##$ENV{AAPTYCOD}='AFFEXT';


#  Corps du script
###########################################################
use IKOS::DATA::ITools;
use IKOS::SIP;

my $bv_severite=0;

# New SIP Object instance
my $ikos_sip = SIP->new($environnement, {debug => $debug_level});

# recuperation de la clef primaine de la table
my $table_key = $ikos_sip->get_table_key($tablename);

if (not $table_key) {
	log_erreur("pas de clef primaine pour la table $tablename");
	sortie(202);
}

my $table_key_value = $ENV{$table_key} if exists $ENV{$table_key};
if (not $table_key_value) {
	log_erreur("Clef primaine <$table_key> n'est pas definie dans l'environnement");
	sortie(202);
}

# fetch selected row from histo table
my $table_histo = $ikos_sip->open_local_from_histo_table($tablename, {debug => $debug_level});

print "Validate all field for key $table_key_value\n";
$table_histo->validate_row_by_key($table_key_value);

# update all field for key

