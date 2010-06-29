#!/usr/bin/env perl
package pc_update_comment;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';


#  Documentation
###########################################################
=head1 NAME

Pc_UPDATE_COMMENT - Met à jour les commentaires d'un environnent à l'autre à valeur égales

=head1 SYNOPSIS

 Pc_UPDATE_COMMENT.pl [-m module] [-p projet] -c environnement_source@date environnement_cible table_name
 
=head1 DESCRIPTION

Met à jour les commentaires d'un environnent à l'autre à valeur égales.


=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -m module

=item -c environnement_source@date : Environnement qui contient les commentaires à copier

à une date donnée

=item -p projet : Uniquement les lignes de la table_source qui appartiennent au projet

=back

=head1 ARGUMENTS

=over

=item environnement_cible : Environnement où les commentaires seront copiés

=item table_name : table a mettre à jour

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

	log_info("Debut du programme : ".__FILE__.join(" ",@ARGV));
	#use Data::Dumper;
	#die Dumper(@ARGV);

	# quirk! because Windows leave "%VAR%" when VAR empty in args
	map {s/%\w+%//g} @ARGV;
	@ARGV=grep $_,@ARGV;

	my %opts;
	getopts('hvc:m:p:', \%opts) or usage(1);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	
	my $environnement_from=$opts{c};
	my $module=$opts{m} if exists $opts{m};
	my $project=$opts{p} if exists $opts{p};

	#  Traitement des arguments
	###########################################################


	if ( @ARGV < 1 ) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}

	my $environnement=shift @ARGV;
	my $table_name=shift @ARGV;


	usage($debug_level) if not $environnement_from;
	
	my $date_from;
	($environnement_from,$date_from) = $environnement_from =~ /^([^@]+)(?:@(.+))?/;
	
	usage($debug_level) if not $environnement_from;

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::Environnement;
	use Isip::ITable::FieldDiff;

	# New SIP Object instance
	my $env_from = Environnement->new($environnement_from, {debug => $debug_level});
	my $env_to  = Environnement->new($environnement, {debug => $debug_level});

	my @table_list_from;
	my @table_list_to;
	if (not $table_name) {
		@table_list_from=$env_from->get_table_list_module($module);
		@table_list_to=$env_to->get_table_list_module($module);
	}
	else {
		@table_list_from=($table_name);
		@table_list_to=($table_name);
	}

	foreach my $current_table (@table_list_to) {
		my $table_to = $env_to->open_histo_field_table($current_table);
		my $table_from;
		if (grep {$current_table} @table_list_from) {
			$table_from = $env_from->open_histo_field_table($current_table, $date_from);
			if (not $table_from) {
				log_info("pas de mise à jour pour $current_table dans $environnement car elle n'existe pas à cette date dans $environnement_from");
				next;
			}
		}
		else {
			$logger->warning("pas de mise à jour pour $current_table dans $environnement car elle n'existe pas dans $environnement_from");
			next;
		}
		
		
		log_info("Recopie des commentaires à valeur égales de $current_table, de $environnement_from vers $environnement");
		
		if ($project) {
			$table_from->metadata_condition("PROJECT = '$project'");
			my %target_key_condition;
			while (my %row=$table_from->fetch_row) {
				$target_key_condition{$row{TABLE_KEY}}++;
			}
			
			if (%target_key_condition) {
				$table_from->query_key_value(keys %target_key_condition);
				$table_to->query_key_value(keys %target_key_condition);
				
				
			}
			else {
				$logger->info("Aucune ligne ne correspond au projet $project");
				next;
			}
		}
		else {
			if (grep {$current_table} @table_list_to) {
				$table_to = $env_to->open_histo_field_table($current_table);
			}
			else {
				next;
			}
		}
		
		my $comment_diff=FieldDiff->open($table_from,$table_to);
		$comment_diff->debugging(1);
		
		use POSIX qw(strftime);
		my $current_date=strftime "%Y-%m-%dT%H:%M", localtime;
		my $current_user=$ENV{IsisUser};
		$current_user="Unknown" if not $current_user;
		
		$comment_diff->set_update_timestamp($current_date);
		$comment_diff->set_update_user($current_user);
		
		$comment_diff->update_comment_target();
	}
	
	$logger->info("Fin du programme ".__FILE__);
}

exit !run(@ARGV) if !caller;

1;