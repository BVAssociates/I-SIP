#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_DOC - Liste la documentation d'un champs d'une table dans un environnement

=head1 SYNOPSIS

 PC_LIST_FIELD_DOC.pl environnement table
 
=head1 DESCRIPTION

Liste la documentation d'un champs d'une table dans un environnement

=head2 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head2 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head2 ARGUMENTS 

=over 4

=item environnement : environnement à utiliser

=item table : nom de la table a décrire

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
	pod2usage(-verbose => $verbosity+1, -noperldoc => 1);
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
getopts('Thv', \%opts);

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
$ENV{Environnement}=$environnement;
$ENV{GSL_FILE}=$tablename;

##for debug
#$ENV{AAPTYCOD}='AFFEXT';

#  Corps du script
###########################################################
use ITable::ITools;
use Isip::Environnement;

my $bv_severite=0;

## DEBUG ONLY
if (exists $opts{T}) { $ENV{RDNPRCOD}='VTS'; $bv_severite=202 };
## DEBUG ONLY

# New SIP Object instance
my $ikos_sip = Environnement->new($environnement, {debug => $debug_level});

# recuperation de la clef primaine de la table
my $table_key = $ikos_sip->get_table_key($tablename);

if (not $table_key) {
	log_erreur("pas de clef primaine pour la table $tablename");
	sortie(202);
}

my @table_key_list=split(',',$table_key);
my @table_key_list_value;

log_info("deduction de la clef primaire depuis l'environnement");
foreach (@table_key_list) {
	push @table_key_list_value, $ENV{$_} if exists $ENV{$_};
	if (not $ENV{$_}) {
		log_erreur("Clef primaine <$table_key> n'est pas definie dans l'environnement");
		sortie(202);
	}
}

my $table_key_value=join(',',@table_key_list_value);

log_info("KEY= $table_key");
log_info("KEY_VAL=$table_key_value");

# fetch selected row from doc table
my $table_doc = $ikos_sip->open_documentation_table($tablename, {debug => $debug_level});
$table_doc->query_condition("TABLE_KEY = '$table_key_value'");

my $counter=0;
while (my @line=$table_doc->fetch_row_array() ) {
	$counter++;
	print join('@',@line)."\n";
}

log_info("No documention found for:",$table_key_value) if not $counter;

