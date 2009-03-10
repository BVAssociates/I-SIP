#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_INIT_TABLE - Initalise la table de donn�es d'historique d'une table

=head1 SYNOPSIS

 PC_INIT_TABLE.pl [-h] [-v ] [-i [-c nombre]] environnement tablename
 
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

=item -i : Insert les donn�es de la table IKOS

=item -c nombre : Commit tous les n insertions

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


#  Traitement des Options
###########################################################

my %opts;
getopts('hvic:', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#defaut value
my $group_commit=1000;
$group_commit = $opts{c} if exists $opts{c};
my $populate=1 if exists $opts{i};

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
use Isip::ITable::DataDiff;


my $env_sip = Environnement->new($environnement);

my %table_info = $env_sip->get_table_info($table_name);
log_erreur("la table $table_name n'est pas connue, veuiller la configurer d'abord") if not %table_info;

$logger->notice("Create database for table",$table_name);
my $current_table=$env_sip->open_source_table($table_name, {debug => $debug_level});

$env_sip->initialize_column_info($current_table);

$env_sip->create_database_histo($table_name);




if ($populate) {

	my $histo_table=$env_sip->open_local_from_histo_table($table_name, {debug => $debug_level});
	my $current_table=$env_sip->open_source_table($table_name, {debug => $debug_level}); 
	
	# set global timestamp for update
	use POSIX qw(strftime);
	my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
	log_info("Date de collecte utilis�e : $timestamp");
	$histo_table->set_update_timestamp($timestamp);
	
	#open IKOS table for DATA
	
	my $count=0;

	$logger->notice("Populate $table_name\_HISTO with data from IKOS table");
	$|=1;
	
	my $diff=DataDiff->open($current_table, $histo_table, {debug => $debug_level});
	$diff->compare();
	$diff->update_compare_target();
	undef $diff;
	#$histo_table->begin_transaction();
	#while (my %data_line=$current_table->fetch_row() ) {
	#       if (not ($count % $group_commit)) {
	#               print "Commit $count lines\n" if $count;
	#               $histo_table->commit_transaction() ;
	#               $histo_table->begin_transaction();
	#               
	#       }
	#       $histo_table->insert_row(%data_line);
	#       $count++
	#}
	
	#print "$count lines inserted\n";
	#$histo_table->commit_transaction();
	
	# execute special query on table backend
	$logger->notice("Set STATUS to Valide");
	$histo_table->{table_histo}->execute("UPDATE $table_name\_HISTO
			SET STATUS='Valide',
					COMMENT='Creation'");
}


sortie($bv_severite);