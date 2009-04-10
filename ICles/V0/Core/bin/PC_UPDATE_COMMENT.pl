#!/usr/bin/perl
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

 Pc_UPDATE_COMMENT.pl [-r] -c environnement_source environnement_cible table_name
 
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

=item -r : Formatte la sortie pour un REPORT

=item -k clef : affiche les champs de la ligne correspondant à la clef

=item -a : affiche tous les champs de toutes les lignes

=item -c environnement_source : Environnement qui contient les commentaires à copier

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
	getopts('hvc:m:', \%opts);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	
	my $environnement_from=$opts{c};
	my $module=$opts{m} if exists $opts{m};

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
	

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::Environnement;
	use Isip::ITable::FieldDiff;

	# New SIP Object instance
	my $env_from = Environnement->new($environnement_from, {debug => $debug_level});
	my $env_to  = Environnement->new($environnement, {debug => $debug_level});

	my @table_list;
	if (not $table_name) {
		@table_list=$env_from->get_table_list_module($module);
	}
	else {
		@table_list=($table_name);
	}

	foreach my $current_table (@table_list) {
		my $table_from = $env_from->open_histo_field_table($current_table);
		my $table_to = $env_to->open_histo_field_table($current_table);
		
		log_info("Recopie des commentaires à valeur égales de $current_table, de $environnement_from vers $environnement");
		my $comment_diff=FieldDiff->open($table_from,$table_to);
		
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