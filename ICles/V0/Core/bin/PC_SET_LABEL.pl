#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_SET_LABEL - force le label d'une clef et d'un champ

=head1 SYNOPSIS

 PC_SET_LABEL.pl [-h] [-v] [-f champ] environnement tablename clef label
 
=head1 DESCRIPTION

Force l'icone d'un champ d'une ligne dans un �tat invariable.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -f : champ sp�cifique

=back

=head1 ARGUMENTS

=over

=item environnement

=item tablename

=item clef

=item label : code de l'icone (Voir IsipRules)

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
getopts('hvf:', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $field=$opts{f};

$field='*' if not $field;

#  Traitement des arguments
###########################################################

if ( @ARGV < 3) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $table_name=shift;
my $key=shift;
my $icon=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use Isip::IsipRules;
use Isip::IsipTreeCache;
use Isip::Cache::CacheStatus;
use Isip::Cache::CacheStatusDismiss;

my $env=Environnement->new($environnement);
my $table=$env->open_local_table("FIELD_LABEL");


$table->begin_transaction;

$table->delete_row(TABLE_NAME => $table_name, TABLE_KEY => $key, FIELD_NAME => $field);
if ($icon) {

	# get update info
	use POSIX qw(strftime);
	my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
	my $current_user=$ENV{IsisUser};
	
	$current_user = "unknown" if ! $current_user;
	
	$table->insert_row(
		DATE_UPDATE => $timestamp,
		USER_UPDATE => $current_user,
		TABLE_NAME  => $table_name,
		TABLE_KEY   => $key,
		FIELD_NAME  => $field,
		LABEL       => $icon
	);
	log_info("$field pour la clef $key de $table_name labellis� $icon");
}
else {
	log_info("Label retir� de $field pour la clef $key de $table_name");
}

$table->commit_transaction;

# update cache
if ( $field eq '*' ) {

	if ( $icon eq 'OK' ) {
		log_info("Mise � jour du cache des icones pour prendre en compte les lignes ignor�es");

		# utilisation de la classe CacheStatusDismiss pour enlever les valeurs du cache recursivement
		my $cache=IsipTreeCache->new($env);
		$cache->add_dispatcher(CacheStatusDismiss->new($env));
		
		$cache->recurse_key($table_name, $key);
		$cache->save_cache();
	}
	else {
		$logger->warning("Le cache des icones sera mis � jour � la prochaine collecte.");
	}
}
else {

	# reconstruct the line
	my %new_line;
	my $table_ikos=$env->open_local_from_histo_table($table_name);
	
	$table_ikos->isip_rules(IsipRules->new($table_name,$env));
	$table_ikos->query_field("ICON", $table_ikos->query_field() );

	$table_ikos->query_key_value($key);
	%new_line=$table_ikos->fetch_row();
	log_error("Probl�me lors de la r�cup�ration de la ligne $key") if $table_ikos->fetch_row() or not %new_line;
	undef $table_ikos;

	my %icon_list=IsipRules->enum_field_icon();

	if (not $ENV{ICON}) {
		log_info("impossible de mettre � jour le cache car ICON n'est pas dans l'environnement");
		$ENV{ICON}="valide_label";
	}

	$new_line{OLD_ICON}=$ENV{ICON};
	#@new_line{$env->get_table_key($table_name)}=split(',',$key);

	my $cache=IsipTreeCache->new($env);
	$cache->add_dispatcher(CacheStatus->new($env));

	log_info("Mise � jour des ic�nes pour $key : $new_line{OLD_ICON} -> $new_line{ICON}");
	$cache->recurse_line($table_name, \%new_line);
	eval {$cache->save_cache() };
	if ($@) {
		log_error("Mise � jour des ic�nes impossible : $@");
	}

}
