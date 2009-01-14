#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';


#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_STATUS - Affiche les champs d'une ligne et y ajoute une colonne ICON

=head1 SYNOPSIS

 PC_LIST_FIELD_STATUS.pl [-c environnement_source@date_source] environnement_cible table_name date_cible
 
=head1 DESCRIPTION

Affiche les champs d'une ligne et y ajoute une colonne ICON.

Par défaut, la colonne ICON contient l'état du commentaire.

Avec l'option -c, ICON contient 
la différence avec un autre environnement ou une autre date.
Dans ce mode, les informations affichés sont celles de la cible.
L'icone correspond à la différence de donnée de la source vers la cible.

=head1 ENVIRONNEMENT

=over

=item Environnement : Environnement en cours d'exploration

=item CLE=VALEUR : l'environnement doit contenir la valeur de la clef de la ligne à afficher

exemple : RDNPRCOD=VTS

=item DATE_EXPLORE : Date en cours d'exploration

=item ENV_COMPARE : Utilise cette valeur pour environnement_source si non spécifié

=item DATE_COMPARE : Utilise cette valeur pour date_source si non spécifié

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -c environnement_source@date_source : force le mode COMPARE

=back

=head1 ARGUMENTS

=over

=item environnement_cible : environnement la destination

=item table_name : table a afficher

=item date_cible : date utilisée pour la destination

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

log_info("Mode d'exploration : $explore_mode");

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use ITable::ITools;
use Isip::ITable::DataDiff;

use POSIX qw(strftime);

## DEBUG ONLY
if (exists $opts{T}) {$ENV{RDNPRCOD}='CDE'; $bv_severite=202 };
## DEBUG ONLY

# New SIP Object instance
my $ikos_sip = Environnement->new($environnement, {debug => $debug_level});

# recuperation de la clef primaine de la table
my $table_key = $ikos_sip->get_table_key($table_name);

if (not $table_key) {
	log_erreur("pas de clef primaine configurée pour la table $table_name");
	sortie(202);
}

my @table_key_list=split(',',$table_key);
my @table_key_list_value;

log_info("recherche de la clef primaire $table_key dans l'environnement");
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

# recupere à liste de champ à afficher
use ITable::ITools;
my $itools_table=ITools->open("IKOS_FIELD_".$environnement."_".$table_name);
my $separator=$itools_table->output_separator;
my @query_field=$itools_table->field;

my $rules=$ikos_sip->get_isip_rules($table_name);

my %memory_row;

if ($explore_mode eq "compare") {

	my $env_sip_from = Environnement->new($env_compare);
	my $env_sip_to = $ikos_sip;
	
	# open first table
	my $table_from = $env_sip_from->open_histo_field_table($table_name, {debug => $debug_level});
	$table_from->query_key_value($table_key_value);
	$table_from->query_date($date_compare) if $date_compare;
	
	# open second table
	my $table_to = $env_sip_to->open_histo_field_table($table_name, {debug => $debug_level});
	$table_to->query_key_value($table_key_value);
	$table_to->query_date($date_explore) if $date_explore;
	
	# open DataDiff table from two table
	my $table_status=DataDiff->open($table_from, $table_to, {debug => $debug_level});
	
	# Only FIELD_VALUE must be compare
	$table_status->compare_exclude(grep(!/^FIELD_VALUE$/,$table_status->query_field));
	
	# compute diff
	$table_status->compare();
	
	# declare some additionnal blank fields
	# (ICON field will be computed into DataDiff)
	$table_status->dynamic_field("ICON","TYPE","TEXT");
	$table_status->query_field(@query_field);

	# Assign a IsipRules to compute ICON field
	$table_status->isip_rules($rules);
	
	# put row in memory
	while (my %row=$table_status->fetch_row) {
		$row{TYPE}=$rules->get_field_type_txt($row{FIELD_NAME}) if $table_status->has_fields("TYPE");
		$row{TEXT}=$rules->get_field_description($row{FIELD_NAME}) if $table_status->has_fields("TEXT");
		
		# don't display ignored fields
		if ($row{TYPE} ne "exclus") {
			$memory_row{$row{FIELD_NAME}}= join($separator,$table_status->hash_to_array(%row))."\n";
		}
	}
	
}
elsif ($explore_mode eq "explore") {

	# open histo table
	my $table_status = $ikos_sip->open_histo_field_table($table_name, {debug => $debug_level});
	
	$table_status->query_date($date_explore) if $date_explore;
	$table_status->query_key_value($table_key_value);
	$table_status->query_field(@query_field);
	
	$table_status->output_separator('@');
	
	# put row in memory
	while (my %row=$table_status->fetch_row) {
	
		# compute dynamic fields
		$row{ICON}=$rules->get_field_icon($row{FIELD_NAME},$row{STATUS}, $row{COMMENT}) if exists $row{ICON};
			
		$row{TYPE}=$rules->get_field_type_txt($row{FIELD_NAME}) if $table_status->has_fields("TYPE");
		$row{TEXT}=$rules->get_field_description($row{FIELD_NAME}) if $table_status->has_fields("TEXT");
		
		# don't display ignored fields
		if ($row{TYPE} ne "exclus") {
			$memory_row{$row{FIELD_NAME}}= join($separator,$table_status->hash_to_array(%row))."\n";
		}
	}
}



# order the lines in the order of table field
my @field_order=$ikos_sip->get_table_field($table_name);
for (@field_order) {
	print $memory_row{$_} if exists $memory_row{$_};
}

sortie($bv_severite);