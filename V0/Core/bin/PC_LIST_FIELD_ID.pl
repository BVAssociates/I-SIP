#!/usr/bin/perl

#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_ID - Liste le champ avec un ID

=head1 SYNOPSIS

 PC_LIST_FIELD_HISTO.pl [-h] [-v] environnement tablename id
 
=head1 DESCRIPTION

Liste le champ avec un ID

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

=item * ID du champ dans la table

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

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $tablename=shift;
my $field_id=shift;

#  Corps du script
###########################################################

use Isip::Environnement;

my $bv_severite=0;

# New SIP Object instance
my $ikos_sip = Environnement->new($environnement, {debug => $debug_level});

# recupere à liste de champ à afficher
use ITable::ITools;
my $itools_table=ITools->open("IKOS_FIELD_".$environnement."_".$tablename);
my $separator=$itools_table->output_separator;
my @query_field=$itools_table->field;

# fetch info from info table
my $table_info = $ikos_sip->open_local_table($tablename."_INFO", {debug => $debug_level});

my %field_label;
my %field_type;
while (my %info_line = $table_info->fetch_row) {
	$field_label{$info_line{FIELD_NAME}}=$info_line{TEXT};
	$field_type{$info_line{FIELD_NAME}}=$info_line{TYPE};
}

# fetch selected row from histo table
my $table_histo = $ikos_sip->open_local_table($tablename."_HISTO", {debug => $debug_level});

$table_histo->query_condition("ID = $field_id");


while (my %line=$table_histo->fetch_row() ) {
	$line{TEXT}=$field_label{$line{FIELD_NAME}};
	$line{TYPE}=$field_type{$line{FIELD_NAME}};
	print join($separator,@line{@query_field})."\n";
}
