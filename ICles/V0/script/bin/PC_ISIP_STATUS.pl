#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

my $debug_level = 0;

#  Documentation
###########################################################
=head1 NAME

PC_EXEC_SQL - Execute une requete SQL

=head1 SYNOPSIS

 PC_ISIP_STATUS.pl [-h] [-v] [-f] [-t]
 
=head1 DESCRIPTION



=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -t : diagnostic technique

=item -f : diagnostic fonctionnel

=back

=head1 ARGUMENTS



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

sub log_error {
	#print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	$logger->error(@_);
	sortie(202);
}

sub log_info {
	#print STDERR "INFO: ".join(" ",@_)."\n"; 
	if ( $debug_level ) {
		$logger->notice(@_);
	}
	else {
		$logger->info(@_);
	}
}


#  Traitement des Options
###########################################################

sub run {
	local @ARGV=@_;
	my $return=1;
	# BEGIN RUN

	log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

	my %opts;
	getopts('hvtf', \%opts) or usage($debug_level+1);

	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};

	my $check_connection=$opts{t};
	my $check_all_table=$opts{f};


	#  Traitement des arguments
	###########################################################

	if ( @ARGV < 0 ) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}



	#  Corps du script
	###########################################################
	my $error_count=0;

	use Isip::Environnement;
	use Isip::IsipConfig;

	# test du portail
	my $winservice_name="IsisPortalV202_ISIP";
	my $winservice_check=`net start |findstr $winservice_name`;
	if ( not $winservice_check ) {
		$logger->error("Service Portail $winservice_name\t: OFF");
		$error_count++;
	}
	else {
		$logger->notice("Service Portail $winservice_name\t: ON");
	}
	
	my $config_sip = IsipConfig->new();
	my @environnement_list = $config_sip->get_environnement_list();

	if ( $check_connection or $check_all_table ) {
		
		foreach my $environnement ( sort @environnement_list ) {
			
			my $env = eval { Environnement->new($environnement) };
			if ( $@ ) {
				$logger->error($@);
				$logger->error( "$environnement \t: ERROR" );
				$error_count++;
				next;
			}
			else {
				$logger->info( "$environnement \t: OK" );
			}
			
			my @list_table=$env->get_table_list();
			
			TABLE:
			foreach my $table_name (sort @list_table) {
				
				if ( $check_all_table ) {
					$logger->notice("test de connexion ODBC pour $table_name sur $environnement");
				}
				else {
					$logger->notice("test de connexion ODBC sur $environnement");
				}
				
				my $source_table=eval { $env->open_source_table($table_name) };
				if ( not $source_table) {
					$logger->error($@) if $@;
					$logger->error( "$environnement.$table_name (ODBC) \t: ERROR" );
					$error_count++;
				}
				else {
					$logger->info( "$environnement.$table_name (ODBC) \t: OK" );
				}
				
				if ( $check_all_table ) {
					$logger->notice("test de la base locale pour $table_name sur $environnement");
				}
				else {
					$logger->notice("test de la base locale sur $environnement");
				}
				
				my $histo_table=eval { $env->open_local_from_histo_table($table_name) };
				if ( not $histo_table) {
					$logger->error($@) if $@;
					$logger->warning( "$environnement.$table_name (local) \t: ERROR" );
					$error_count++;
				}
				else {
					$logger->info( "$environnement.$table_name (local) \t: OK" );
				}

				# on ne verifie que la première table si pas de check complet
				if ( not $check_all_table ) {
					last TABLE;
				}
			}
		}
	}
	
	
	if ( $error_count ) {
		$logger->notice("---------------------------------");
		$logger->notice("Etat de I-SIP \t: ERROR");
	}
	else {
		$logger->notice("---------------------------------");
		$logger->notice("Etat de I-SIP \t: OK");
	}
	
return $return;
# END RUN
}

exit !run(@ARGV) if !caller;
