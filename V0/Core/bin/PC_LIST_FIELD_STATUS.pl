#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_STATUS - Affiche les champs d'une ligne et y ajoute une colonne de ICON

=head1 SYNOPSIS

 PC_LIST_FIELD_STATUS.pl [-c environnement_source@date_source] environnement_cible date_cible table
 
=head1 DESCRIPTION

Affiche les champs d'une ligne et y ajoute une colonne ICON.

Par défaut, la colonne ICON contient l'état du commentaire.

Avec l'option -c, ICON contient 
la différence avec un autre environnement ou une autre date.

=head1 ENVIRONNEMENT

=over 4

=item Environnement : Environnement en cours d'exploration

=item CLE=VALEUR : l'environnement doit contenir la valeur de la clef de la ligne à afficher

=item DATE_EXPLORE : Date en cours d'exploration

=item ENV_COMPARE : Utilise cette valeur pour environnement_source si non spécifié

=item DATE_COMPARE : Utilise cette valeur pour date_source si non spécifié

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -c environnement_source@date_source : force le mode COMPARE

=back

=head1 ARGUMENTS

=over 4

=item * environnement de destination

=item * table a afficher

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

#use Data::Dumper;
#die Dumper(@ARGV);

# quirk! because Windows leave "%VAR%" when VAR empty in args
map {s/%\w+%//g} @ARGV;
@ARGV=grep $_,@ARGV;

my %opts;
getopts('hvc:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

#recuperation de l'environnement
my $env_compare=$ENV{ENV_COMPARE};
my $date_compare=$ENV{DATE_COMPARE};
my $date_explore=$ENV{DATE_EXPLORE};

if ( @ARGV < 2 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $table_name=shift;
my $temp_date_explore=shift;
my $temp_time_explore=shift;
$date_explore=$temp_date_explore if $temp_date_explore;
$date_explore .= " ".$temp_time_explore if $temp_time_explore;

if (exists $opts{c}) {
	if ($opts{c} =~ /@/) {
		($env_compare,$date_compare) = split(/@/,$opts{c});
	}
	else {
		$env_compare=$opts{c};
	}
}

$env_compare=$environnement if $date_compare and not $env_compare;

# deduction du contenu de la colonne ICON
#  explore par defaut
#  compare si une source est trouvée
my $explore_mode="explore";
$explore_mode="compare" if $env_compare or $date_compare;


#  Corps du script
###########################################################
my $bv_severite=0;

use IKOS::SIP;
use IKOS::DATA::ITools;
use IKOS::DATA::DataDiff;

use POSIX qw(strftime);

# New SIP Object instance
my $ikos_sip = SIP->new($environnement, {debug => $debug_level});

# recuperation de la clef primaine de la table
my $table_key = $ikos_sip->get_table_key($table_name);

if (not $table_key) {
	log_erreur("pas de clef primaine pour la table $table_name");
	sortie(202);
}

## DEBUG ONLY
#$ENV{AAPTYCOD}='HCPC'; $bv_severite=202;
## DEBUG ONLY

my @table_key_list=split(',',$table_key);
my @table_key_list_value;

# recherche de la clef dans l'environnement
foreach (@table_key_list) {
	push @table_key_list_value, $ENV{$_} if exists $ENV{$_};
	if (not $ENV{$_}) {
		log_erreur("Clef primaine <$_> n'est pas definie dans l'environnement");
		sortie(202);
	}
}

my $table_key_value=join(',',@table_key_list_value);

## DEBUG
#print STDERR "KEY= $table_key\n";
#print STDERR "KEY_VAL=$table_key_value\n";
## DEBUG

my $table_status;

if ($explore_mode eq "compare") {
	my $env_sip_from = SIP->new($env_compare);
	my $env_sip_to = $ikos_sip;
	
	# fetch selected row from histo table
	my $table_from = $env_sip_from->open_histo_field_table($table_name, {debug => $debug_level});
	$table_from->query_key_value($table_key_value);
	$table_from->query_date($date_explore) if $date_explore;
	
	my $table_to = $env_sip_to->open_histo_field_table($table_name, {debug => $debug_level});
	$table_to->query_key_value($table_key_value);
	$table_to->query_date($date_explore) if $date_explore;
	
	$table_status=DataDiff->open($table_from, $table_to, {debug => $debug_level});

	$table_status->compare();
	
	
}
elsif ($explore_mode eq "explore") {

	
	
	# recupere à liste de champ à afficher
	use IKOS::DATA::ITools;
	my $itools_table=ITools->open("IKOS_FIELD_".$environnement."_".$table_name);
	my $separator=$itools_table->output_separator;
	my @query_field=$itools_table->field;

	my $type_rules = IsipRules->new($ikos_sip->get_sqlite_path($table_name),$table_name, {debug => $debug_level});

	# fetch selected row from histo table
	$table_status = $ikos_sip->open_histo_field_table($table_name, {debug => $debug_level});
	
	$table_status->isip_rules($type_rules);
	$table_status->query_date($date_explore) if $date_explore;
	$table_status->query_key_value($table_key_value);
	$table_status->query_field(@query_field);
}

$table_status->output_separator('@');

while (my @row=$table_status->fetch_row_array) {
	print join($table_status->output_separator,@row)."\n";
}

sortie($bv_severite);