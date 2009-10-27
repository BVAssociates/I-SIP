#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Date::Calc qw(:all);

use Isip::IsipLog '$logger';
use Mail::Sender;

my $debug_level = 0;

#  Documentation
###########################################################
=head1 NAME

PC_REPORT_ACTIVITY - Liste le contenu d'une table sqlite

=head1 SYNOPSIS

 PC_REPORT_ACTIVITY.pl [-h][-v] [-m] [-l] [-t [-s sep] ] [-a|environnement]
 
=head1 DESCRIPTION

Affiche des compte-rendu d'activité sur la base I-SIP

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -m : Effectue l'envoie du courriel

=item -l : affiche la liste des tables à commenter

=item -t : sortie sous forme de table ITools

=back

=head1 ARGUMENTS

=over

=item environnement (ou -a pour tous les environnements)

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

sub log_error {
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
getopts('hvmalts:', \%opts) or usage($debug_level+1);

$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};


my $send_mail=$opts{m};

my $long_display=$opts{l};

my $table_display=$opts{t};

my $separator=$opts{s};
$separator = ',' if not $separator;

#  Traitement des arguments
###########################################################

if ( @ARGV < 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement_arg = shift;

if ( not ($environnement_arg xor $opts{a} ) ) {
	usage($debug_level);
}

#  Corps du script
###########################################################

#use Isip::IsipReport;
use List::Util qw(sum);

use Isip::Cache::CacheStatus;
use Isip::Environnement;
use Isip::IsipConfig;


my $config = IsipConfig->new();


my @environnement_list;
if ( $environnement_arg ) {
	@environnement_list = ( $environnement_arg );
}
else {
	@environnement_list = $config->get_environnement_list();
}

my $mail_subject;
my $mail_message;

my $table_max_length=0;
my $module_max_length=0;

foreach my $environnement (@environnement_list) {
	
	my $env = Environnement->new($environnement);

	my $cache_icon = CacheStatus->new($env);
	
	my %total_lines_for_table;
	my %dirty_lines_for_table;
	
	my %total_lines_for_module;
	my %dirty_lines_for_module;

	foreach my $table_name ($env->get_table_list() ) {

		# recupère la taille max des noms de table
		if ( $table_max_length < length $table_name ) {
			$table_max_length = length $table_name;
		}
		
		#
		# get lines count
		#

		# get key field
		my $key_fields_txt;
		my $table_temp = $env->open_local_from_histo_table($table_name);
		$key_fields_txt= join(',', sort $table_temp->key());

		my $table_histo = $env->open_local_table($table_name."_HISTO");
		$table_histo->custom_select_query(
		"SELECT  count(*)
		FROM $table_name\_HISTO as HISTO1
		INNER JOIN (
		SELECT
		max(ID) as ID2,
		HISTO2.TABLE_KEY as TABLE_KEY_2,
		HISTO2.FIELD_NAME as FIELD_NAME_2
		FROM $table_name\_HISTO as HISTO2
			WHERE FIELD_NAME = '$key_fields_txt'
			GROUP BY FIELD_NAME_2, TABLE_KEY_2
			HAVING FIELD_VALUE != '__delete'
		) ON  (ID= ID2)"
			);
			
		( $total_lines_for_table{$table_name} ) = $table_histo->fetch_row_array();

		#
		# get dirty lines count
		#

		$dirty_lines_for_table{$table_name} = $cache_icon->get_dirty_key($table_name, $table_name);

	}

	# stocke les statistiques par module
	foreach my $module ( $config->get_module_list() ) {
	
		# recupère la taille max des noms de module
		if ( $module_max_length < length $module ) {
			$module_max_length = length $module;
		}
		
		my @tables = $env->get_table_list_module($module);
		$total_lines_for_module{ $module } = sum( @total_lines_for_table{ @tables } );
		
		my $dirty_lines_temp = sum( @dirty_lines_for_table{ @tables } );
		$dirty_lines_for_module{ $module } = $dirty_lines_temp if $dirty_lines_temp;
	}

	# stocke les statistiques globales
	my $dirty_lines = sum( values %dirty_lines_for_table );
	my $total_lines = sum( values %total_lines_for_table );

	
	#
	# construction du message
	#
	
	if ( $table_display ) {
		$mail_message .= join( $separator, ($environnement,'', $dirty_lines,$total_lines))."\n";
		
		foreach my $module ( keys %dirty_lines_for_module) {
			
			$mail_message .= join( $separator, ('', $module, $dirty_lines_for_module{$module}, $total_lines_for_module{$module}) )."\n";
			if ( $long_display ) {
				foreach my $table ( $env->get_table_list_module($module) ) {
					if ($dirty_lines_for_table{$table} ) {
						$mail_message .= join( $separator, ('',$table,$dirty_lines_for_table{$table},$total_lines_for_table{$table}) )."\n";
					}
				}
			}
		}
	}
	else {
		$mail_message .= "Environnement $environnement\n\n";
		
		foreach my $module ( keys %dirty_lines_for_module) {
			$mail_message .= sprintf (" * Module %-${module_max_length}s : $dirty_lines_for_module{$module}\n", $module);
			
			if ( $long_display ) {
				foreach my $table ( $env->get_table_list_module($module) ) {
					if ($dirty_lines_for_table{$table} ) {
						$mail_message .= sprintf ("    - Table %-${table_max_length}s : $dirty_lines_for_table{$table}\n",$table);
					}
				}
				$mail_message .= "\n";
			}
		}
		
		
		#print "Nombre de lignes à commenter pour $table_name: $dirty_lines_for_table{$table_name} (sur $total_lines_for_table{$table_name})\n" if $dirty_lines_for_table{$table_name};


		#$mail_message .= "Nombre de modification de valeur de champs : ".$histo_count."\n";
		#$mail_message .= "Nombre de modification validés : ".$comment_count."\n";
		$mail_message .= "Nombre de lignes totales  à commenter : $dirty_lines lignes (sur un total de $total_lines lignes)\n";
		
		$mail_message .= "\n------------------------\n";
	}
}


print $mail_message."\n";

if ( $send_mail ) {
	
}