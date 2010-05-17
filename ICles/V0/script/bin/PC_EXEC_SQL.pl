#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_EXEC_SQL - Execute une requete SQL

=head1 SYNOPSIS

 PC_EXEC_SQL.pl [-h] [-v] [-e environnement] -t table "SQL QUERY"
 
=head1 DESCRIPTION

Execute une requete SQL sur toutes les tables "logiques" d'un ou tous les environnements.

La requete SQL est executé sur toutes les tables des environnements. Le nom de la table physique
est calculée à partir du nom de la table logique representée par "{tab}", et l'environnement
par "{env}".

La forme SQL "SELECT * FROM ..." peut être remplacée par "SELECT FROM ..." pour eviter les
problèmes d'interpretations du Shell.

Par exemple, pour affiche toutes les colonnes de toutes les tables :

 PC_EXEC_SQL.pl -t {tab}_COLUMN "SELECT '{env}',* FROM {tab}_COLUMN"

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -e : uniquement sur cet environnement

=item -t : uniquement sur cette table

=back

=head1 ARGUMENTS

=head2 SQL : requete SQL à executer sur chaque table. Le nom de la table est représenté par {}

=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits réservés.

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
	$logger->info(@_);
}


#  Traitement des Options
###########################################################

sub run {
	local @ARGV=@_;
	my $return=1;
	# BEGIN RUN

	log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

	my %opts;
	getopts('hve:t:', \%opts);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};

	my $environnement=$opts{e};
	my $table_name=$opts{t} or usage($debug_level);


	#  Traitement des arguments
	###########################################################

	if ( @ARGV < 1 ) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}

	my $SQL=join(' ',@ARGV);


	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::Environnement;
	use Isip::IsipConfig;


	my @environnement_list;
	if ($environnement) {
		@environnement_list = $environnement;
	}
	else {
		my $config_sip = IsipConfig->new($environnement);
		@environnement_list = $config_sip->get_environnement_list();
	}

	foreach my $environnement ( @environnement_list ) {

		my $env = Environnement->new($environnement);

		my @list_table;
		if ( $table_name =~ /\{tab\}/ ) {
			@list_table=$env->get_table_list();
		}
		else {
			@list_table=($table_name);
		}
		
		foreach my $logical_table (@list_table) {
		
			my $physical_table =  $table_name;
			my $SQL_generated =  $SQL;
			
			# remplace {} par le nom logique de la table
			$physical_table =~ s/\{tab\}/$logical_table/g;
			$SQL_generated  =~ s/\{tab\}/$logical_table/g;
			$SQL_generated  =~ s/\{env\}/$environnement/g;
			
			if ( not ($env->exist_local_table($physical_table)) ) {
				$logger->error("$physical_table n'existe pas dans $environnement");
				next;
			}
			$logger->info("execution sur ${environnement}::$physical_table");
			
			my $table = $env->open_local_table($physical_table);
			
			if ( $SQL_generated =~/^SELECT/i ) {
			
				$SQL_generated =~ s/SELECT\s+FROM/SELECT * FROM/i;
				
				$table->custom_select_query($SQL_generated);
				while ( my @result = $table->fetch_row_array() ) {
					print join(';',@result)."\n";
				}
			}
			else {
				my $nb = $table->execute($SQL_generated);
				if ( $nb > 0) {
					$logger->notice("$physical_table: ",$nb+0," lignes modifiés");
				}
			}
		}
	}
	
return $return;
# END RUN
}

exit !run(@ARGV) if !caller;
