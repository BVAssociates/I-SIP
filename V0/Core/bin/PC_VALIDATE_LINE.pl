#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

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

if ( @ARGV != 3) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $tablename=shift;
my $field=shift;

# quirk to test
$ENV{ENVIRON}=$environnement;
$ENV{GSL_FILE}=$tablename;

##for debug
#$ENV{AAPTYCOD}='AFFEXT';


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

print STDERR "KEY= $table_key\n";
print STDERR "KEY_VAL=$table_key_value\n";

# fetch selected row from histo table
my $table_histo = $ikos_sip->open_local_table($tablename."_HISTO", {debug => $debug_level});


# update all field for key

#  Documentation
###########################################################
=head1 NAME

PC_VALIDATE_LINE

=head1 SYNOPSIS

 PC_VALIDATE_LINE.pl environnement table clef
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement � la date courante

=head2 ENVIRONNEMENT

=over 4

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head2 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head2 ARGUMENTS 

=over 4

=item * environnement � utiliser

=item * nom de la table a d�crire

=item * nom du champ

=back

=head2 AUTHOR

BV Associates, 16/10/2008

=cut