#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_UPDATE_HISTO - Met � jour les champs d'une table Histo depuis la r�f�rence

=head1 SYNOPSIS

 PC_UPDATE_HISTO.pl [-h] [-v] environnement tablename
 
=head1 DESCRIPTION

Met � jour les champs d'une table suffix�e par _HISTO depuis une table IKOS par ODBC.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head2 -n : Mode simulation

=head1 ARGUMENTS

=head2 environnement : environnement � utiliser

=head2 tablename : table a d�crire

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	my $exit_value=shift;
	$logger->notice("Sortie du programme, code $exit_value");
	exit $exit_value;
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


log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

my %opts;
getopts('hvn', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV != 2 ) {
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
use ITable::ITools;
use Isip::ITable::DataDiff;

my $env_sip = Environnement->new($environnement);

my %table_info = $env_sip->get_table_info();
log_erreur("la table $table_name n'est pas connue") if not exists $table_info{$table_name};

my @list_table;
if (not $table_name) {
	@list_table=keys %table_info;
} else {
	@list_table=($table_name);
}

my $counter=0;
my $source_table;
foreach my $current_table (@list_table) {
	$counter++;
	
	log_info("Connexion � la table source : $current_table");
	# open source table depending on TYPE_SOURCE
	$source_table=$env_sip->open_source_table($current_table);
	
	
	log_info("Connexion � la base d'historisation");
	my $histo_table=$env_sip->open_local_from_histo_table($table_name, {debug => $debug_level, timeout => 100000});
	my $table_diff=DataDiff->open($source_table, $histo_table, {debug => $debug_level});

	log_info("Debut de la comparaison de $table_name");
	$table_diff->compare();

	if (exists $opts{n}) {
		log_info("Option -n : les changements n'ont pas �t� appliqu�s");
	} else {
		my $diff_counter = $table_diff->update_compare_target();
		if ($diff_counter) {
			log_info("Les changements ont ete appliqu�s ($diff_counter)");
			$histo_table->{table_histo}->execute("ANALYZE");
		} else {
			log_info("Aucune mise � jour");
		}
	}

}

log_erreur("No table found for",$table_name) if not $counter;

sortie($bv_severite);