#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_CATEGORY - Liste les etats possibles

=head1 SYNOPSIS

 PC_LIST_CATEGORY.pl [-h] [-v] [-s sep] environnement tablename
 
=head1 DESCRIPTION

Liste les etats possibles

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -s : separateur de champ en sortie

=back

=head1 ARGUMENTS

=over

=item environnement

=item tablename

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
getopts('hvs:', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=',';
$separator=$opts{s} if $opts{s};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $table_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use Isip::Cache::CacheStatus;
use Isip::IsipFilter;

my $env=Environnement->new($environnement);

my $cache = CacheStatus->new($env);
$cache->load_cache($table_name);

my $filter=IsipFilter->new();

my $table=$env->open_local_table($table_name."_CATEGORY");

#$table->custom_select_query("SELECT DISTINCT * FROM ".$table_name."_CATEGORY");
#$table->query_field("CATEGORY");
#$table->query_distinct(1);

my %dirty_for_category;

while (my %row=$table->fetch_row()) {
	
	# add category to the list
	if ( not exists $dirty_for_category{ $row{CATEGORY} } ){
		$dirty_for_category{ $row{CATEGORY} } = 0;
	}
	
	# look for dirtyness of key in table
	if ( $cache->is_dirty_key($table_name, $row{TABLE_KEY})  #look for line's childs
	   or $cache->is_dirty_key($table_name, $row{TABLE_KEY},$table_name) ) {  #look for line
		
		$dirty_for_category{ $row{CATEGORY} }++ ;
	}	
}

my $icon_root="category";

foreach my $category ( sort keys %dirty_for_category ) {
	
	my $icon = $icon_root;
	if ( $dirty_for_category{$category} ) {
		$icon .= '_dirty';
	}
	
	if ( $filter->is_display_line(ICON => $icon) ) {
		print join($separator, $icon, $category)."\n";
	}
}


#affiche le groupe vide
print join($separator, $icon_root.'_other', 'vide')."\n";

