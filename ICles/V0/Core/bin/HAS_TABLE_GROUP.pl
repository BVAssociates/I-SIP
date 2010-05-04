#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

HAS_TABLE_GROUP - sors avec un code retour 0 si une table contient des groupes

=head1 SYNOPSIS

 HAS_TABLE_GROUP.pl [-h][-v] environnement table_name
 
=head1 DESCRIPTION

Verifie dans la base que la table contient des groupes

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over

=item environnement : nom de l'environnement

=item table_name : nom de la table

=back

=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits réservés.

=cut


#  Fonctions
###########################################################

sub sortie {
	exit shift;
}

sub usage {
	my $verbosity=shift;
	die("HAS_TABLE_GROUP.pl [-h][-v] environnement table_name");
}

sub log_erreur {
	die ("ERREUR: ".join(" ",@_));
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
}


#  Traitement des Options
###########################################################

my %opts;
getopts('hv', \%opts) or usage();

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
}

my $environnement=shift @ARGV;
my $tablename=shift @ARGV;

#  Corps du script
###########################################################

use Isip::Environnement;

my $count;

# check case where no GROUP possible
my $no_group;
$no_group++ if exists $ENV{TYPE_SOURCE} and $ENV{TYPE_SOURCE} eq "XML";
$no_group++ if exists $ENV{ENV_COMPARE};
$no_group++ if exists $ENV{DATE_COMPARE};


if ($no_group)
{
	$count=0;
}
else {
	my $env=Environnement->new($environnement);

	my $table=$env->open_local_table($tablename."_CATEGORY");

	$table->custom_select_query("SELECT count(*) from ".$tablename."_CATEGORY");
	($count)=$table->fetch_row_array();
}

#return 0 if group > 0
exit not $count;
