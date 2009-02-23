#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';


#  Documentation
###########################################################
=head1 NAME

PC_REPORT_STATUS - Affiche une exploration sous forme CSV

=head1 SYNOPSIS

 PC_REPORT_STATUS.pl [-c environnement_source@date_source] environnement_cible date_cible
 
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
getopts('m:hvc:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

#recuperation de l'environnement
my $env_compare=$ENV{ENV_COMPARE};
my $date_compare=$ENV{DATE_COMPARE};
my $date_explore=$ENV{DATE_EXPLORE};
my $module_explore=$ENV{Module} || $opts{m} || usage($debug_level);

my $filter_field=$ENV{FILTER_FIELD}="ICON";
my $filter_value=$ENV{FILTER_VALUE}="!valide";
my $filter_exclude;

if ($filter_value and $filter_value =~ /^!(.+)/) {
	$filter_value=$1;
	$filter_exclude=1;
}

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
}

my $environnement=shift;
my $table_name_arg=shift;
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
use Isip::IsipRules;

# New SIP Object instance
my $env_sip = Environnement->new($environnement, {debug => $debug_level});
my @table_list=($table_name_arg) || $env_sip->get_table_list_module($module_explore);

# recupere à liste de champ à afficher
use ITable::ITools;
my $itools_table=ITools->open("FIELD_REPORT_HISTO", {debug => $debug_level});
my $separator=$itools_table->output_separator;
my @query_field=$itools_table->field;


# Check if it is an SQL filter
my @comment_condition;
if ($filter_field and $filter_field ne 'ICON' ) {

	$filter_value =~ s/\*/%/g;
	my $comp_operator;
	if ($filter_exclude) {
		$comp_operator='<>';
		$comp_operator='not like' if $filter_value =~ /%/;
	}
	else {
		$comp_operator='=' ;
		$comp_operator='like' if $filter_value =~ /%/;
	}
	
	# Check where is the clause
	if ($filter_field eq 'PROJECT') {
		push @comment_condition, "$filter_field $comp_operator '$filter_value'";
	}
}


foreach my  $table_name (@table_list) {

	my $rules=IsipRules->new($table_name);
	my @field_order=$env_sip->get_table_field($table_name);
	my %memory_row;

	my $table_status;
	if ($explore_mode eq "compare") {
		
		my $env_sip_from = Environnement->new($env_compare);
		my $env_sip_to = $env_sip;
		
		# open first table
		my $table_from = $env_sip_from->open_histo_field_table($table_name, {debug => $debug_level});
		
		$table_from->query_date($date_compare) if $date_compare;
		
		# open second table
		my $table_to = $env_sip_to->open_histo_field_table($table_name, {debug => $debug_level});
		
		$table_to->query_date($date_explore) if $date_explore;
		
		# open DataDiff table from two table
		$table_status=DataDiff->open($table_from, $table_to, {debug => $debug_level});
		
		# Only FIELD_VALUE must be compare
		$table_status->compare_exclude(grep(!/^FIELD_VALUE$/,$table_status->query_field));
		
		# declare some additionnal blank fields
		# (ICON field will be computed into DataDiff)
		$table_status->dynamic_field("ICON","TYPE","TEXT");
		$table_status->query_field(@query_field);
		
		# compute diff
		$table_status->compare();
		
		# Assign a IsipRules to compute ICON field
		$table_status->isip_rules($rules);
		
		
	}
	elsif ($explore_mode eq "explore") {

		# open histo table
		$table_status = $env_sip->open_histo_field_table($table_name, {debug => $debug_level});
		
		$table_status->query_date($date_explore) if $date_explore;
		
		$table_status->query_field(@query_field);
		
	}
	
	# put row in memory
	while (my %row=$table_status->fetch_row) {
	
		# compute dynamic fields
		$row{ICON}=$rules->get_field_icon(%row) if exists $row{ICON} and $explore_mode eq "explore";
			
		$row{TYPE}=$rules->get_field_type_txt($row{FIELD_NAME}) if exists $row{TYPE};
		$row{TEXT}=$rules->get_field_description($row{FIELD_NAME}) if exists $row{TEXT};
		
		#TODO treat CSV mutli-line
		#if ($row{MEMO}) {
		#	use IO::Uncompress::Gunzip qw(gunzip);
		#	use MIME::Base64;
		#	my $input=decode_base64($row{MEMO});
		#	my $output;
		#	gunzip(\$input=>\$output);
		#	#excel wait for <LF> only
		#	$output =~ s/\r//gm;
		#	$row{MEMO}=$output;
		#}
				
		# don't display ignored fields
		my $display_line=1;
		if ($filter_field eq 'ICON') {
			if ($filter_exclude and $row{ICON} eq $filter_value) {
				$display_line = 0;
			}
			elsif (not $filter_exclude and $row{ICON} ne $filter_value) {
				$display_line = 0;
			}
		}
		
		if ($row{TYPE} and $row{TYPE} eq "exclus"
				or $row{ICON} and $row{ICON} eq "stamp") {
				$display_line = 0;
		}

		if ($display_line) {
			print join($separator,map {$_} $table_status->hash_to_array(%row))."\n";
		}
	}
	
	#exit 1; #DEBUG
	

}


# order the lines in the order of table field
#my @field_order=$env_sip->get_table_field($table_name);
#open (XLS, '> D:\ISIP\exports\test.csv');
#for (@field_order) {
#	print XLS $memory_row{$_} if exists $memory_row{$_};
#}
#close XLS;

sortie($bv_severite);
