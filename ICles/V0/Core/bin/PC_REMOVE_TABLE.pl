#!/usr/bin/env perl
package pc_remove_table;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_REMOVE_TABLE - supprime la table de donn�es d'historique d'une table

=head1 SYNOPSIS

 PC_REMOVE_TABLE.pl [-h] [-v ] [-f] [-n] [-r] environnement tablename
 
=head1 DESCRIPTION

Ajoute les informations de colonnes d'une table dans l'environnement
Creer une base de donn�e historique.

Si l'option -i est utilis�e, une premi�re collecte sera effecut�e


=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -f : supprime �galement les fichiers de donn�es (recup�ration impossible)

=item -r : supprime m�me si des d�pendances existent

=item -n : ne regenere pas le menu

=back

=head1 ARGUMENTS

=over

=item environnement : environnement � utiliser

=item tablename : table dont la base sera cr��

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
getopts('hvfnr', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $force=$opts{f};
my $no_menu=$opts{n};
my $no_deps=$opts{r};

#  Traitement des arguments
###########################################################

if ( @ARGV != 2 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift @ARGV;
my $table_name=shift @ARGV;


#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;


my $env_sip = Environnement->new($environnement);

my %table_info_save=$env_sip->get_table_info($table_name);

log_erreur("$table_name est inconnue dans $environnement") if not %table_info_save;

if ( ! $no_deps ) {
	my @dependant_table = $env_sip->get_links()->get_child_tables($table_name);

	if ( @dependant_table ) {
		log_erreur("Impossible de supprimer $table_name car les tables suivantes ont une clef �trang�re sur cette table : <".join(', ', @dependant_table).">. Retirez ces dependances et r�essayez.");
	}
}


#######################
# suppression fichier physique
#######################

if ($force) {
	my $file_to_remove=$env_sip->get_sqlite_path($table_name."_HISTO");
	if (-e $file_to_remove) {
		log_info("suppression du fichier de donn�es : $file_to_remove");
		unlink $file_to_remove or die "Impossible de supprimer $file_to_remove : $!";
	}
}


#######################
# mise � jour TABLE_INFO
#######################

my $table=$env_sip->open_local_table("TABLE_INFO");

my %line_to_remove;
$line_to_remove{TABLE_NAME}=$table_name;

log_info("suppression d�finition de $table_name dans l'environnement $environnement");
$table->delete_row(%line_to_remove);


#######################
# mise � jour MENU
#######################

if (not $no_menu) {
	require "PC_GENERATE_MENU.pl";

	log_info("reg�n�ration des menu du module $table_info_save{module}");
	pc_generate_menu::run("-m",$table_info_save{module},$environnement);
}

return 1;
# END RUN
}

exit !run(@ARGV) if !caller;

1;