#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_STATUS - Affiche une table et y ajoute une colonne de ICON

=head1 SYNOPSIS

 PC_LIST_STATUS.pl [-x icon] [-c environnement_source@date_source] environnement_cible table_name [date_cible]
 
=head1 DESCRIPTION

Affiche une table et y ajoute une colonne ICON.

Par d�faut, la colonne ICON contient l'�tat du commentaire.

Avec l'option -c, ICON contient 
la diff�rence avec un autre environnement ou une autre date.

=head1 ENVIRONNEMENT

=over 4

=item Environnement : Environnement en cours d'exploration

=item DATE_EXPLORE : Date en cours d'exploration

=item ENV_COMPARE : Utilise cette valeur pour environnement_source si non sp�cifi�

=item DATE_COMPARE : Utilise cette valeur pour date_source si non sp�cifi�

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -c environnement_source@date_source : force le mode COMPARE

=item -x icone : n'affiche pas les lignes contenant "icone"

=back

=head1 ARGUMENTS

=over

=item environnement_cible : environnement de destination

=item table_name : table a afficher

=item date_cible : date d'exploration de la table cible

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	my $exit_value=shift;
	$logger->notice("Sortie du programme, code $exit_value");
	exit $exit_value;
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

log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

my %opts;
getopts('x:hvc:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $exlude_icon=$opts{x};

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

# deduction du contenu de la colonne ICON
#  explore par defaut
#  compare si une source est trouv�e
my $explore_mode="explore";
$explore_mode="compare" if $env_compare or $date_compare;

log_info("mode $explore_mode activ�");

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use ITable::ITools;
use Isip::ITable::DataDiff;

my $env_sip = Environnement->new($environnement);

# recuperation de la structure des liens
my $links = $env_sip->get_links();
my @query_condition;

# on essaye de retrouver toutes les clefs etrangere dans l'environnement
# on ne poss�de pas l'information de l'arbre d'exploration, donc on cherche
#	les clefs etrang�res de toutes les tables parentes
##DEBUG
#$ENV{RDNPRCOD}='AFF' ; $bv_severite=202;
#$ENV{FLCDTRAIT}='ACH750' ; $bv_severite=202;
#$ENV{FLTYPTRAIT}='17' ; $bv_severite=202;
##DEBUG
foreach my $parent_table ($links->get_parent_tables($table_name) ) {
	my %foreign_fields=$links->get_foreign_fields($table_name,$parent_table);
	
	foreach my $foreign_field (keys %foreign_fields) {
		my $var=$foreign_fields{$foreign_field};
		if ( exists $ENV{$var} ) {
			$logger->info("Clef etrang�re pour filtrage : $foreign_field = '$ENV{$var}'");
			push @query_condition, "$foreign_field = '$ENV{$var}'";
		}
	}
}

# table qui sera affich�e
my $table_explore;
# ouverture de la table en cours d'exploration
my $table_current=$env_sip->open_local_from_histo_table($table_name, {debug => $debug_level});
$table_current->query_date($date_explore) if $date_explore;

$table_current->query_condition(@query_condition);

# recuperation des colonnes � afficher
my @query_field=$env_sip->get_table_field($table_name);

if ($explore_mode eq "compare") {
	my $env_sip_from = Environnement->new($env_compare);
		
	#open IKOS table for DATA
	my $table_from=$env_sip_from->open_local_from_histo_table($table_name, {debug => $debug_level});
	$table_from->query_date($date_compare) if $date_compare;

	$table_from->query_condition(@query_condition);

	$table_explore=DataDiff->open($table_from, $table_current, {debug => $debug_level});

	$table_explore->compare();

}
elsif ($explore_mode eq "explore") {
	
	$table_explore=$table_current;
}

# champs � afficher
$table_explore->query_field(@query_field);

my $type_rules = IsipRules->new($env_sip->get_sqlite_path($table_name),$table_name, {debug => $debug_level});
$table_explore->isip_rules($type_rules);

$table_explore->output_separator('@');

while (my %row=$table_explore->fetch_row) {
	print join($table_explore->output_separator,@row{@query_field})."\n" if not ($exlude_icon and $row {ICON} eq $exlude_icon);
}

sortie($bv_severite);
