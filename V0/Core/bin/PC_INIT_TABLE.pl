#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_INIT_TABLE - Initalise les tables Historique et Information d'une table

=head1 SYNOPSIS

 PC_INIT_TABLE.pl [-h] [-v ] [-i [-c nombre]] environnement tablename
 
=head1 DESCRIPTION

Lit les informations de l'environnement et initalise les tables Historique et Information d'une table.

Suivant l'implementation, créer égalemement la base associée.


=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -i : Insert les données de la table IKOS

=item -c nombre : Commit tous les n insertions

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


#  Traitement des Options
###########################################################


my %opts;
getopts('hvic:', \%opts);

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

# quirk because INFO_TABLE use %Environnement%
$ENV{Environnement}=$environnement;
my $db2_table = ITools->open("INFO_TABLE");

$db2_table->query_condition("TABLE_NAME = '$table_name'") if $table_name;

# db2_table_line  return one row
my %db2_table_line = $db2_table->fetch_row();
log_erreur("la table $table_name n'est pas connue, veuiller la configurer d'abord") if not %db2_table_line;

$logger->notice("Create database for table",$table_name);
$env_sip->initialize_database($table_name, {debug => $debug_level});

if ($populate) {
	#open IKOS table for DATA
	my $current_table=$env_sip->open_ikos_table($table_name, {debug => $debug_level});
	my $histo_table=$env_sip->open_local_from_histo_table($table_name, {debug => $debug_level});
	
	my $count=0;

	$logger->notice("Populate $table_name\_HISTO with data from IKOS table");
	$|=1;
	
	my $diff=DataDiff->open($current_table, $histo_table, {debug => $debug_level});
	$diff->compare();
	$diff->update_compare_target();
	#$histo_table->begin_transaction();
	#while (my %data_line=$current_table->fetch_row() ) {
	#	if (not ($count % $group_commit)) {
	#		print "Commit $count lines\n" if $count;
	#		$histo_table->commit_transaction() ;
	#		$histo_table->begin_transaction();
	#		
	#	}
	#	$histo_table->insert_row(%data_line);
	#	$count++
	#}
	
	#print "$count lines inserted\n";
	#$histo_table->commit_transaction();
	
	# execute special query on table backend
	$logger->notice("Set STATUS to Valide");
	$histo_table->{table_histo}->execute("UPDATE $table_name\_HISTO
		SET STATUS='".$histo_table->{valid_keyword}."',
			COMMENT='Creation'");
			
	$logger->notice("Create indexes");
	$histo_table->{table_histo}->execute("CREATE INDEX IDX_TABLE_KEY ON $table_name\_HISTO (TABLE_KEY ASC)");
	$histo_table->{table_histo}->execute("CREATE INDEX IDX_TABLE_FIELD ON $table_name\_HISTO (FIELD_NAME ASC)");
	$histo_table->{table_histo}->execute("ANALYZE");
}

sortie($bv_severite);