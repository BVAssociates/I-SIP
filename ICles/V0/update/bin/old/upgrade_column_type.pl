#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

upgrade_colno - 

=head1 SYNOPSIS

 upgrade_colno.pl [-h][-v] old_type new_type
 
=head1 DESCRIPTION

Met à jour les bases de données ISIP en remplaçant 

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

=item old_type : type à remplacer

=item new_type : type de remplacement

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
getopts('hv', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $old_type=shift @ARGV;
my $new_type=shift @ARGV;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::IsipConfig;
use Isip::Environnement;
use ITable::Sqlite;

my $config=IsipConfig->new();

my @environnement_list=$config->get_environnement_list();

foreach (@environnement_list) {
	my $env=Environnement->new($_);
	
	foreach my $table ($env->get_table_list()) {
		my $table=$env->open_local_table($table."_COLUMN");
		
		$table->execute("UPDATE ".$table->table_name." SET TYPE='".$new_type."' WHERE TYPE='".$old_type."'");
	}
}