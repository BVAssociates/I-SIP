#!/usr/bin/perl
package pc_set_baseline;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog qw'$logger log_screen_only';

#  Documentation
###########################################################
=head1 NAME

PC_SET_BASELINE - Construit une baseline

=head1 SYNOPSIS

 PC_SET_BASELINE.pl [-h] [-v] [-d|-m message [-p] [-a]] environnement date
 
=head1 DESCRIPTION

Construit une baseline sur un environnement

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -d : supprime une baseline

=item -m message : Ajoute une description à la baseline

=item -p : ferme les projets associés à l'environnement

=item -a : valide tous les changements non validés avec le message

=back

=head1 ARGUMENTS

=over

=item environnement

=item date

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

sub run {
	local @ARGV=@_;
	
	#  Traitement des Options
	###########################################################


	my %opts;
	getopts('hvdm:pa', \%opts);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	
	my $drop_baseline=$opts{d};
	
	my $message=$opts{m};
	usage($debug_level) if not $drop_baseline and not $message;
	
	my $close_project;
	$close_project=1 if exists $opts{p};
	
	my $auto_validate=1 if exists $opts{a};

	#  Traitement des arguments
	###########################################################

	if ( @ARGV < 2) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}

	my $environnement=shift @ARGV;
	my $date=shift @ARGV;

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::Environnement;
	use Isip::Cache::CacheStatus;

	$logger->notice("création d'une baseline à la date $date");
	my $env=Environnement->new($environnement);
	
	#log_screen_only();
	
	# PRE ACTION

	my $baseline_list=$env->open_local_table("DATE_UPDATE");
	$baseline_list->query_condition("DATE_HISTO='$date'");
	
	my %baseline_info=$baseline_list->fetch_row();
	$baseline_list->finish;
	
	if (not %baseline_info) {
		log_erreur("la date $date n'est pas une date de collecte valide");
	}
	else {
		if (not $drop_baseline and $baseline_info{BASELINE}) {
			log_erreur("la date $date est déjà une date de baseline");
		}
		elsif ($drop_baseline and not $baseline_info{BASELINE}) {
			log_erreur("la date $date n'est pas une date de baseline");
		}
	}
	
	# ACTION
	
	foreach my $table_name ($env->get_table_list) {
		#my $pid = fork();
		#if (!$pid) {
			if ($drop_baseline) {
				$logger->notice("suppression d'une baseline pour $table_name à la date $date");
				
				# suppression réelle différée possible (ex : avant la collecte)
				#my $table=$env->drop_histo_baseline($table_name,$date);
				
				$baseline_info{BASELINE}=0;
				$baseline_info{DESCRIPTION}="";
			}
			else {
				if ( $auto_validate )  {
					$logger->notice("Validation automatique de toutes les modifications");
					
					my $table = $env->open_local_from_histo_table($table_name);
					
					use POSIX qw(strftime);
					my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
					$table->set_update_timestamp($timestamp);
					
					$table->validate_table('Valide',$message);
					
					# vidange du cache des icones
					CacheStatus->new($env)->clear_cache();
				}
				
				$logger->notice("création d'une baseline pour $table_name à la date $date");
				my $table=$env->create_histo_baseline($table_name,$date);
				
				$baseline_info{BASELINE}=1;
				$baseline_info{DESCRIPTION}=$message if $message;
			}
			$logger->notice("terminé pour $table_name");
		#	last;
		#}
	}
	#wait;
	
	
	$baseline_list->update_row(%baseline_info);
	
	# POST ACTION
	
	if ($drop_baseline) {
		$logger->notice("Baseline supprimée pour $environnement à la date $date");
	}
	else {
		$logger->notice("Baseline créé pour $environnement à la date $date");
		
		if ($close_project) {
			$logger->notice("Clôture des projets dans $environnement");
		
			use POSIX qw(strftime);
			my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
			
			my $table=$env->open_local_table("PROJECT_INFO");
			$table->execute("UPDATE PROJECT_INFO SET PROJECT_CLOSE='$timestamp' WHERE PROJECT_CLOSE IS NULL OR PROJECT_CLOSE = ''");
		}
	}
	
	return 1;
}

exit !run(@ARGV) if not caller;