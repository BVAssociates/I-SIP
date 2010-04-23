@rem = '--*-Perl-*--
@echo off
if "%OS%" == "Windows_NT" goto WinNT
perl -x -S "%0" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto endofperl
:WinNT
perl -x -S %0 %*
if NOT "%COMSPEC%" == "%SystemRoot%\system32\cmd.exe" goto endofperl
if %errorlevel% == 9009 echo You do not have Perl in your PATH.
if errorlevel 1 goto script_failed_so_exit_with_non_zero_val 2>nul
goto endofperl
@rem ';
#!/usr/bin/perl
#line 15

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

Force l'icone d'un champ d'une ligne dans un état invariable.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -f : champ spécifique

=back

=head1 ARGUMENTS

=over

=item environnement

=item tablename

=item clef

=item label : code de l'icone (Voir IsipRules)

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
getopts('hvf:', \%opts);

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

my $env=Environnement->new($environnement);
my $table=$env->open_local_table("FIELD_LABEL");


$table->begin_transaction;

$table->delete_row(TABLE_NAME => $table_name, TABLE_KEY => $key, FIELD_NAME => $field);
if ($icon) {

	# get update info
	use POSIX qw(strftime);
	my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
	my $current_user=$ENV{IsisUser};
	
	$table->insert_row(
		DATE_UPDATE => $timestamp,
		USER_UPDATE => $current_user,
		TABLE_NAME  => $table_name,
		TABLE_KEY   => $key,
		FIELD_NAME  => $field,
		LABEL       => $icon
	);
	log_info("$field pour la clef $key de $table_name labellisé $icon");
}
else {
	log_info("Label retiré de $field pour la clef $key de $table_name");
}

$table->commit_transaction;

if ( $field eq '*' ) {

	my $module=$ENV{Module};
	log_info("Le cache des icônes doit être reconstruit pour le module $module");
	
	# Suppression immédiate de la ligne correspondante afin de ne pas perturber
	# l'affichage
	my $cache_table = $env->open_local_table("CACHE_ICON");
	$cache_table->execute("DELETE FROM CACHE_ICON
	WHERE TABLE_NAME ='$table_name'
	AND TABLE_KEY='$key'");
	undef $cache_table;
	
	require 'PC_UPDATE_CACHE.pl';
	pc_update_cache::run("-m",$module,$environnement);
	
}
else {
	# update cache
	use Isip::IsipTreeCache;
	use Isip::IsipRules;
	use Isip::Cache::CacheStatus;

	# reconstruct the line
	my %new_line;
	my $table_ikos=$env->open_local_from_histo_table($table_name);
	
	$table_ikos->isip_rules(IsipRules->new($table_name,$env));
	$table_ikos->query_field("ICON", $table_ikos->query_field() );

	$table_ikos->query_key_value($key);
	%new_line=$table_ikos->fetch_row();
	log_error("Problème lors de la récupération de la ligne $key") if $table_ikos->fetch_row() or not %new_line;
	undef $table_ikos;

	my %icon_list=IsipRules->enum_field_icon();

	if (not $ENV{ICON}) {
		log_info("impossible de mettre à jour le cache car ICON n'est pas dans l'environnement");
		$ENV{ICON}="valide_label";
	}

	$new_line{OLD_ICON}=$ENV{ICON};
	#@new_line{$env->get_table_key($table_name)}=split(',',$key);

	my $cache=IsipTreeCache->new($env);
	$cache->add_dispatcher(CacheStatus->new($env));

	log_info("Mise à jour des icônes pour $key : $new_line{OLD_ICON} -> $new_line{ICON}");
	$cache->recurse_line($table_name, \%new_line);
	eval {$cache->save_cache() };
	if ($@) {
		log_error("Mise à jour des icônes impossible : $@");
	}

}

__END__
:endofperl
