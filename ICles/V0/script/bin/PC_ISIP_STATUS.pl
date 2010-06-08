#!/usr/bin/env perl

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

 PC_ISIP_STATUS.pl [-h] [-v] [-s win_service] [-t [-f] ]
 
=head1 DESCRIPTION



=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -s : verifie l'etat du service

=item -t : diagnostic base locale

=item -f : diagnostic connexion ODBC

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
	getopts('hvtfs:', \%opts) or usage($debug_level+1);

	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};

	my $winservice_name=$opts{s};
	my $check_local=$opts{t};
	my $check_remote=$opts{f};

	if ( $check_remote and ! $check_local ) {
		usage($debug_level);
		sortie(202);
	}

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
	my $service_off=0;

	use Isip::Environnement;
	use Isip::IsipConfig;
	
	$logger->notice("Debut de la verification");
	
	# verification des modules disponibles
	eval { require Mail::Sender };
	if ($@) {
		$logger->warning("Module Perl absent : Mail::Sender");
	}
	eval { require Date::Calc };
	if ($@) {
		$logger->warning("Module Perl absent : Date::Calc");
	}
	eval { require DBD::ODBC };
	if ($@) {
		$logger->error("Module Perl absent : DBD::ODBC");
	}
	
	if ( $winservice_name ) {
		# test du portail
		my $winservice_check=`net start |findstr $winservice_name`;
		
		if ( $winservice_check ) {
			$logger->notice("Service Portail $winservice_name\t: ON");
		}
		else{
			$logger->error("Service Portail $winservice_name\t: OFF");
			$error_count++;
		}
	}
	

	if ( $check_local ) {
		my $config_sip = IsipConfig->new();
		my @environnement_list = $config_sip->get_environnement_list();

		
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
			
			# lance la vérification de connexion
			my $tmp_error_count = $env->check_bad_table($check_remote);
			
			# tente de construire le menu
			eval { $env->get_links_menu() };
			if ( $@ ) {
				$logger->error($@);
				$logger->error( "$environnement \t: ERROR" );
				$error_count++;
				next;
			}
			
			if ( $tmp_error_count ) {
				$logger->error("tables de $environnement \t: KO");
				$error_count += $tmp_error_count
			}
			else {
				$logger->error("tables de $environnement \t: OK");
			}
		}
	}
	
	
	if ( $error_count ) {
		$logger->notice("---------------------------------");
		$logger->notice("Etat de I-SIP \t: ERROR ($error_count)");
	}
	else {
		$logger->notice("---------------------------------");
		$logger->notice("Etat de I-SIP \t: OK");
	}
	
return !$error_count;
# END RUN
}

exit !run(@ARGV) if !caller;
