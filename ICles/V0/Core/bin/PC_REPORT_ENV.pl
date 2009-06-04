#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';


#  Documentation
###########################################################
=head1 NAME

PC_REPORT_COMPARE - Affiche une exploration sous forme CSV

=head1 SYNOPSIS

 PC_REPORT_COMPARE.pl -c environnement_source@date_source [-m module] environnement_cible date_cible
 
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

=item -m module : uniquement sur les tables de module

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
	log_info("sortie du programme ".__FILE__);
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
getopts('m:hvc:e', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $module=$opts{m};
my $compare_option=$opts{c};
my $export=$opts{e};

#  Traitement des arguments
###########################################################

log_info("Debut du programme : ".__FILE__." ".join(" ",@ARGV));


if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
}

my $environnement=shift @ARGV;
my $table_name_arg=shift @ARGV;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use Isip::IsipConfig;
require "PC_LIST_FIELD_STATUS.pl";

# New SIP Object instance
my $env_sip = Environnement->new($environnement, {debug => $debug_level});
my @table_list=($table_name_arg) || $env_sip->get_table_list_module($module);

my $add_option="";
my $export_file;
if ($export) {
	my $config=IsipConfig->new();
	$export_file =$config->{export_dir}.'/rapport';
	
	$compare_option =~ s/\@$//;
	$export_file .="_differentiel_".$compare_option if $compare_option;
	
	$export_file .="_".$environnement;
	if ($module) {
		$export_file .="_$module";
	}
	elsif($table_name_arg) {
		$export_file .="_".$table_name_arg;
	}
	
	my @date=localtime();
	$date[5] += 1900;
	$export_file .="_".sprintf('%04d-%02d-%02dT%02dh%02dm%02ds',@date[5,4,6,2,1,0]);
	
	$export_file .='.csv';
	
	

	open (XLS, '>',$export_file) or die($export_file,' : ',$!);
	select(XLS);
	
	# recupere à liste de champ à afficher
	use ITable::ITools;
	my $itools_table;
	if ($compare_option) {
		$itools_table=ITools->open("FIELD_REPORT_COMPARE", {debug => $debug_level});
	}
	else {
		$itools_table=ITools->open("FIELD_REPORT_HISTO", {debug => $debug_level});
	}
	my @query_field=$itools_table->field;
	
	print(join(';',@query_field),"\n");
	
	$add_option="s;";
}

my $counter=0;
foreach my  $table_name (@table_list) {
	log_info(int(100 * $counter / @table_list),'%');
	$counter++;
	if ($compare_option) {
		pc_list_field_status::run("-r","-a$add_option","-c".$compare_option,$environnement,$table_name);
	}
	else {
		pc_list_field_status::run("-r","-a$add_option",$environnement,$table_name);
	}
}

log_info(int(100 * $counter / @table_list),'%');

if ($export) {
	close(XLS);
	
	open (XLS, '<',$export_file) or die($export_file,' : ',$!);
	my $line_counter=0;
	while(<XLS>) {
		$line_counter++;
	}
	close(XLS);
	
	log_info("nom du fichier généré:",$export_file);
	log_info($line_counter,"lignes écrites");
	if ($line_counter > 65536) {
		log_erreur("Le fichier contient trop de ligne pour être ouvert avec MS Excel");
	}
}

sortie($bv_severite);
