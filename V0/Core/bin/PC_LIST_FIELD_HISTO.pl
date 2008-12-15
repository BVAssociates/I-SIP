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
$ENV{Environnement}=$environnement;
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

my @table_key_list=split(',',$table_key);
my @table_key_list_value;


foreach (@table_key_list) {
	push @table_key_list_value, $ENV{$_} if exists $ENV{$_};
	if (not $ENV{$_}) {
		log_erreur("Clef primaine <$ENV{$_}> n'est pas definie dans l'environnement");
		sortie(202);
	}
}

my $table_key_value=join(',',@table_key_list_value);

print STDERR "KEY= $table_key\n";
print STDERR "KEY_VAL=$table_key_value\n";

# fetch selected row from histo table
my $table_histo = $ikos_sip->open_local_table($tablename."_HISTO", {debug => $debug_level});
$table_histo->query_field($ikos_sip->get_histo_field());
$table_histo->query_condition("TABLE_KEY = '$table_key_value' AND FIELD_NAME ='$field'");

while (my @line=$table_histo->fetch_row_array() ) {
	print join(',',@line)."\n";
}



#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_HISTO - Liste l'historique d'un champs d'une table dans un environnement

=head1 SYNOPSIS

 PC_LIST_FIELD_HISTO.pl environnement table champ
 
=head1 DESCRIPTION

Liste l'historique d'un champs d'une table dans un environnement

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