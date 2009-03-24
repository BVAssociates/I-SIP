#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_ROOT_TABLE.pl - Liste les table racines

=head1 SYNOPSIS

 PC_LIST_ROOT_TABLE.pl [-h][-v] [-s separateur] environnement module
 
=head1 DESCRIPTION

Liste les table racines en prenant en compte les tables "virtuelles"
utilisées pour simuler des arborescences

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -s : modifie le separateur de sortie

=back

=head1 ARGUMENTS

=over

=item environnement

=item module : table du module

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


my %opts;
getopts('hvs:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=',';
$separator=$opts{s} if exists $opts{s};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $module=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;
use Isip::Cache::CacheStatus;

my $env=Environnement->new($environnement);

my $cache=CacheStatus->new($env);

my $link_obj=$env->get_links_menu();

my %list_table_uniq;
foreach my $table ($env->get_table_list_module($module)) {
	next if not $env->is_root_table($table);
	my $parent =($link_obj->get_parent_tables($table,1))[-1];
	if ($parent) {
		$list_table_uniq{$parent}++;
	}
	else {
		$list_table_uniq{$table}++;	
	}
}

foreach my $table (keys %list_table_uniq) {
	my $def_name=$table;
	$table =~ s/^(.+)__.+/$1/;
	my %table_info=$env->get_table_info($table);
	my $icon="valide";
	#$icon="dirty" if $cache->is_dirty_table($table);
	print join($separator,($icon,$table,$def_name,$module,$table_info{description},$table_info{type_source}))."\n";
}


