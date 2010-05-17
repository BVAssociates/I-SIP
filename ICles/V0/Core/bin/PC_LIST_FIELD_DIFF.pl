#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_DIFF - Affiche les deux versions d'un champ d'une ligne d'une table

=head1 SYNOPSIS

 PC_LIST_FIELD_DIFF.pl [-c environnement_source@date_source] environnement_cible table_name [date_cible]
 
=head1 DESCRIPTION

Affiche les deux versions d'un champ d'une ligne d'une table.

La premiere ligne affichée est la version d'un champ de l'environnement à une date donnée spécifié par
l'option -c, ou bien par l'environnement (voir partie ENVIRONNEMENT).

La seconde ligne affichée est la version du champ de l'environnement à une date donnée spécifié par
les arguments environnement_cible et date_cible.

Si date_source ou date_cible est omis, alors la date est la date de la dernière collecte.

Une option cachée -T existe pour mettre valider le script avec une valeur de CLE de test.


=head1 ENVIRONNEMENT

=over 4

=item Environnement : Environnement en cours d'exploration

=item CLE=VALEUR : l'environnement doit contenir la valeur de la clef de la ligne à afficher

exemple : RDNPRCOD=VTS

=item FIELD_NAME : nom du champ à afficher

=item DATE_EXPLORE : Date en cours d'exploration

=item ENV_COMPARE : Utilise cette valeur pour environnement_source si non spécifié

=item DATE_COMPARE : Utilise cette valeur pour date_source si non spécifié

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -c : environnement_source@date_source : force le mode COMPARE

Un des deux paramètres "environnement_source"
ou "date_source" peut être vide

=back

=head1 ARGUMENTS

=over 4

=item environnement_cible : environnement de la destination

=item table_name : table a afficher

=item date_cible : date de la destination

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut


#  Fonctions
###########################################################

my $bv_severite=0;


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

#use Data::Dumper;
#die Dumper(@ARGV);

# quirk! because Windows leave "%VAR%" when VAR empty in args
map {s/%\w+%//g} @ARGV;
@ARGV=grep $_,@ARGV;

my %opts;
getopts('Thvc:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

#recuperation de l'environnement
my $env_compare=$ENV{ENV_COMPARE};
my $date_compare=$ENV{DATE_COMPARE};
my $date_explore=$ENV{DATE_EXPLORE};
my $field_name=$ENV{FIELD_NAME};

if ( @ARGV < 2 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $table_name=shift;
my $temp_date_explore=shift;
$date_explore=$temp_date_explore if $temp_date_explore;

if (exists $opts{c}) {
	if ($opts{c} =~ /@/) {
		($env_compare,$date_compare) = split(/@/,$opts{c});
	}
	else {
		$env_compare=$opts{c};
	}
}

$env_compare=$environnement if $date_compare and not $env_compare;


## DEBUG ONLY
if (exists $opts{T}) { $ENV{RDNPRCOD}='VTS'; $field_name='RDGDNDOS'; $bv_severite=202 };
## DEBUG ONLY


#  Verifications
###########################################################

if (not $field_name) {
	log_erreur("FIELD_NAME non défini dans l'environnement");
	sortie(202);
}

if (not $env_compare and not $date_compare) {
	log_erreur("ENV_COMPARE ou DATE_COMPARE non défini dans l'environnement");
	sortie(202);
}


#  Corps du script
###########################################################

use Isip::Environnement;
use ITable::ITools;
use Isip::IsipRules;

# New SIP Object instance
my $ikos_sip = Environnement->new($environnement, {debug => $debug_level});

# recuperation de la clef primaine de la table
my $table_key = $ikos_sip->get_table_key($table_name);

if (not $table_key) {
	log_erreur("pas de clef primaine pour la table $table_name");
	sortie(202);
}

my @table_key_list=split(',',$table_key);
my @table_key_list_value;

# recherche de la clef dans l'environnement
foreach (@table_key_list) {
	if (exists $ENV{$_}) {
		push @table_key_list_value, $ENV{$_};
	}
	else {
		$logger->warning("Clef primaine <$_> n'est pas definie dans l'environnement");
		push @table_key_list_value, "";
	}
}

my $table_key_value=join(',',@table_key_list_value);

## DEBUG
#print STDERR "KEY= $table_key\n";
#print STDERR "KEY_VAL=$table_key_value\n";
## DEBUG

# recupere à liste de champ à afficher
use ITable::ITools;
my $itools_table=ITools->open("FIELD_DIFF");
my $separator=$itools_table->output_separator;
my @query_field=$itools_table->field;
undef $itools_table;

# construit un object IsipRules
my $rules=IsipRules->new($table_name,$ikos_sip);


# interroge table source
my $env_sip_from = Environnement->new($env_compare);
my $table_from = $env_sip_from->open_histo_field_table($table_name, {debug => $debug_level});
$table_from->query_key_value($table_key_value);
$table_from->query_date($date_compare) if $date_compare;
$table_from->query_condition("FIELD_NAME='$field_name'");

$table_from->dynamic_field("ICON","TYPE","TEXT","ENVIRONNEMENT","DATE");
$table_from->query_field(@query_field);
$table_from->isip_rules($rules);

my %row_from=$table_from->fetch_row();

# aucune ligne renvoyé, ce champ/ligne n'existait pas encore
# on créé une ligne vide
if (not %row_from) {
	foreach (@query_field) {
		$row_from{$_}="";
	}
	$row_from{ID}=0;
	$row_from{DATE_HISTO}=$date_compare if $date_compare;
	$row_from{ENVIRONNEMENT}=$env_compare if $table_from->has_fields("ENVIRONNEMENT");
	#$row_from{TABLE_NAME}=$table_name;
	#$row_from{TABLE_KEY}=$table_key_value;
	#$row_from{FIELD_NAME}=$field_name;
} else {
	$row_from{ENVIRONNEMENT}=$env_compare if $table_from->has_fields("ENVIRONNEMENT");
}

print join($separator,$table_from->hash_to_array(%row_from))."\n";
undef $table_from;

# interroge table cible
my $table_to = $ikos_sip->open_histo_field_table($table_name, {debug => $debug_level});
$table_to->query_key_value($table_key_value);
$table_to->query_date($date_explore) if $date_explore;
$table_to->query_condition("FIELD_NAME='$field_name'");

$table_to->dynamic_field("ICON","TYPE","TEXT","ENVIRONNEMENT");
$table_to->query_field(@query_field);
$table_to->isip_rules($rules);

my %row_to=$table_to->fetch_row();

# aucune ligne renvoyé, ce champ/ligne n'existait pas encore
# on créé une ligne vide
if (not %row_to) {
	foreach (@query_field) {
		$row_to{$_}="";
	}
	$row_to{ID}=0;
	$row_to{DATE_HISTO}=$date_explore;
	$row_to{ENVIRONNEMENT}=$environnement if $table_to->has_fields("ENVIRONNEMENT");
	#$row_to{TABLE_NAME}=$table_name;
	#$row_to{TABLE_KEY}=$table_key_value;
	#$row_to{FIELD_NAME}=$field_name;
} else {
	$row_to{ENVIRONNEMENT}=$environnement if $table_to->has_fields("ENVIRONNEMENT");
}
print join($separator,$table_to->hash_to_array(%row_to))."\n";

	
sortie($bv_severite);
