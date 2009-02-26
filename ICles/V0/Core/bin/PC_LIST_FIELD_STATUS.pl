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

Par d�faut, la colonne ICON contient l'�tat du commentaire.

Avec l'option -c, ICON contient 
la diff�rence avec un autre environnement ou une autre date.
Dans ce mode, les informations affich�s sont celles de la cible.
L'icone correspond � la diff�rence de donn�e de la source vers la cible.

=head1 ENVIRONNEMENT

=over

=item Environnement : Environnement en cours d'exploration

=item CLE=VALEUR : l'environnement doit contenir la valeur de la clef de la ligne � afficher

exemple : RDNPRCOD=VTS

=item DATE_EXPLORE : Date en cours d'exploration

=item ENV_COMPARE : Utilise cette valeur pour environnement_source si non sp�cifi�

=item DATE_COMPARE : Utilise cette valeur pour date_source si non sp�cifi�

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

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

=item date_cible : date utilis�e pour la destination

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

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
getopts('k:hvc:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $table_key_value=$opts{k};

#  Traitement des arguments
###########################################################

#recuperation de l'environnement
my $env_compare=$ENV{ENV_COMPARE};
my $date_compare=$ENV{DATE_COMPARE};
my $date_explore=$ENV{DATE_EXPLORE};

my $filter_field=$ENV{FILTER_FIELD};
my $filter_value=$ENV{FILTER_VALUE};
my $filter_exclude;

if ($filter_value and $filter_value =~ /^!(.+)/) {
	$filter_value=$1;
	$filter_exclude=1;
}

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

# pas de filtre en mode "explore" sur une date
undef $filter_field if $date_explore;
undef $filter_value if $date_explore;

log_info("Mode d'exploration : $explore_mode");

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use ITable::ITools;
use Isip::ITable::FieldDiff;
use Isip::IsipRulesDiff;

# New SIP Object instance
my $ikos_sip = Environnement->new($environnement, {debug => $debug_level});

# recuperation de la clef primaine de la table
my $table_key = $ikos_sip->get_table_key($table_name);

if (not $table_key) {
	log_erreur("pas de clef primaine configur�e pour la table $table_name");
	sortie(202);
}

my @table_key_list=split(',',$table_key);
my @table_key_list_value;

if (not $table_key_value) {
	log_info("recherche de la clef primaire $table_key dans l'environnement");
	foreach (@table_key_list) {
		push @table_key_list_value, $ENV{$_} if exists $ENV{$_};
		if (not $ENV{$_}) {
			log_erreur("Clef primaine <$table_key> n'est pas definie dans l'environnement");
			sortie(202);
		}
	}

	$table_key_value=join(',',@table_key_list_value);
}

log_info("KEY= $table_key");
log_info("KEY_VAL=$table_key_value");

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

# recupere � liste de champ � afficher
use ITable::ITools;
my $itools_table=ITools->open("IKOS_FIELD_".$environnement."_".$table_name, {debug => $debug_level});
my $separator=$itools_table->output_separator;
my @query_field=$itools_table->field;

# Create IsipRule object
my $rules=IsipRules->new($table_name, {debug => $debug_level});

# fetch documentation of fields for key
my %field_doc;
my $table_doc=eval {$ikos_sip->open_documentation_table($table_name, {debug => $debug_level}) };
if ($@) {
	warn $@;
}
else {
	$table_doc->query_condition("TABLE_KEY = '$table_key_value'");
	while (my %row_doc=$table_doc->fetch_row) {
		$field_doc{$row_doc{FIELD_NAME}}=$row_doc{DOCUMENTATION};
	}
}

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
	my $table_status=FieldDiff->open($table_from, $table_to, {debug => $debug_level});
	
	# declare some additionnal blank fields
	# (ICON field will be computed into DataDiff)
	$table_status->dynamic_field("ICON","TYPE","TEXT","DOCUMENTATION",$table_status->dynamic_field);
	#$table_status->query_field(@query_field,"OLD_FIELD_VALUE","DIFF");
	$table_status->query_field(@query_field);
	
	# compute diff
	$table_status->compare();

	# Assign a IsipRules to compute ICON field
	my $diff_rules=IsipRulesDiff->new($table_name);
	$table_status->isip_rules($diff_rules);
	
	# put row in memory
	while (my %row=$table_status->fetch_row) {
		$row{DOCUMENTATION}=$field_doc{$row{FIELD_NAME}} if exists $field_doc{$row{FIELD_NAME}} and $table_status->has_fields("DOCUMENTATION");
		$row{TYPE}=$diff_rules->get_field_type_txt($row{FIELD_NAME}) if $table_status->has_fields("TYPE");
		$row{TEXT}=$diff_rules->get_field_description($row{FIELD_NAME}) if $table_status->has_fields("TEXT");
		
		my $display=1;
		# don't display filtered fields
		if ($filter_field and exists $row{$filter_field}) {
			if ($filter_exclude and $row{$filter_field} eq $filter_value) {
				$display=0;
			}
			elsif (not $filter_exclude and $row{$filter_field} ne $filter_value) {
				$display=0;
			}
		}
		
		# don't display ignored fields
		if ($row{TYPE} eq "exclus") {
			$display=0;
		}

		if ($display) {
			$memory_row{$row{FIELD_NAME}}= join($separator,$table_status->hash_to_array(%row))."\n";
		}
	}
	
}
elsif ($explore_mode eq "explore") {

	# open histo table
	my $table_status = $ikos_sip->open_histo_field_table($table_name, {debug => $debug_level});
	
	$table_status->query_date($date_explore) if $date_explore;
	$table_status->query_key_value($table_key_value);
	$table_status->dynamic_field($table_status->dynamic_field,"DOCUMENTATION");
	$table_status->query_field(@query_field);
	
	$table_status->metadata_condition(@comment_condition);
	
	$table_status->output_separator('@');
	
	# put row in memory
	while (my %row=$table_status->fetch_row) {
	
		# don't show hidden fields
		next if $rules->is_field_hidden(%row);
		
		# compute dynamic fields
		$row{DOCUMENTATION}=$field_doc{$row{FIELD_NAME}} if exists $field_doc{$row{FIELD_NAME}} and $table_status->has_fields("DOCUMENTATION");
		$row{ICON}=$rules->get_field_icon(%row) if exists $row{ICON};
			
		$row{TYPE}=$rules->get_field_type_txt($row{FIELD_NAME}) if $table_status->has_fields("TYPE");
		$row{TEXT}=$rules->get_field_description($row{FIELD_NAME}) if $table_status->has_fields("TEXT");

		$memory_row{$row{FIELD_NAME}}= join($separator,$table_status->hash_to_array(%row))."\n";
	}
}



# order the lines in the order of table field
my @field_order=@memory_row{$ikos_sip->get_table_field($table_name)};
delete @memory_row{$ikos_sip->get_table_field($table_name)};
for (keys %memory_row) {
	print $memory_row{$_};
}
for (@field_order) {
	print;
}


sortie($bv_severite);