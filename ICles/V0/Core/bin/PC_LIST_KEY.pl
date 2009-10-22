#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_KEY - Liste les clefs primaire des tables

=head1 SYNOPSIS

 PC_LIST_KEY.pl [-h][-v] [-s separateur] environnement
 
=head1 DESCRIPTION

Liste les clefs primaire de toutes les tables dans un environnement

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

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;
use ITable::ITools;

my $env=Environnement->new($environnement);

print join($separator, ('') x 2)."\n";
foreach my $table ($env->get_table_list()) {
	my $column_table=$env->open_local_table($table."_COLUMN");
	while (my %row=$column_table->fetch_row) {
		print join($separator,@row{$column_table->query_field})."\n" if $row{PRIMARY_KEY};
	}
}
