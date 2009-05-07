#!/usr/bin/perl
package pc_remove_env;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_REMOVE_ENV - supprime un environnement complet

=head1 SYNOPSIS

 PC_REMOVE_ENV.pl [-h] [-v ] [-f] environnement
 
=head1 DESCRIPTION




=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -f : supprime également les fichiers de données (recupération impossible)

=back

=head1 ARGUMENTS

=over

=item environnement : environnement à utiliser

=item tablename : table dont la base sera créé

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
# BEGIN RUN

#  Traitement des Options
###########################################################

log_info("Debut du programme : ".__PACKAGE__." ".join(" ",@ARGV));

my %opts;
getopts('hvf', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $force=$opts{f};

#  Traitement des arguments
###########################################################

if ( @ARGV != 1 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift @ARGV;


#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;
use ITable::ITools;

my $env_sip = Environnement->new($environnement);

my @table_list=$env_sip->get_table_list();

#######################
# suppression table
#######################

require "pc_remove_table.pl";
	foreach my $table (@table_list) {
	log_info("suppression définition de la table $table de l'$environnement");
	my $return;
	if ($force) {
		$return=pc_remove_table::run("-fn",$environnement,$table);
	}
	else {
		$return=pc_remove_table::run("-n",$environnement,$table);
	}
	if (not $return) {
		log_erreur("Erreur lors de la suppression de la table $table dans $environnement");
	}
}

#######################
# suppression entrée environnement
#######################

my $table=ITools->open("CONF_ENVIRON");

my %line_to_remove;
$line_to_remove{Environnement}=$environnement;

log_info("suppression définition de l'$environnement");
$table->delete_row(%line_to_remove);

return 0;
# END RUN
}

exit run(@ARGV) if !caller;

1;