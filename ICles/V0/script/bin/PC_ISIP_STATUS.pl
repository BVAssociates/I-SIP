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
	getopts('hvtf', \%opts);

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
	my $bv_severite=0;

	use Isip::Environnement;
	use Isip::IsipConfig;

	# test du portail
	my $winservice_name="IsisPortalV202_ISIP";
	my $winservice_check=`net start |findstr $winservice_name`;
	if ( not $winservice_check ) {
		die("Service Portail \t: OFF");
	}
	else {
		log_info("Service Portail \t: ON");
	}
	
	my $config_sip = IsipConfig->new();
	my @environnement_list = $config_sip->get_environnement_list();

	my $error_count=0;
	if ( $check_connection or $check_all_table ) {
		foreach my $environnement ( @environnement_list ) {
			
			my $env = Environnement->new($environnement);
			my @list_table=$env->get_table_list();
			
			TABLE:
			foreach my $table_name (@list_table) {
				
				my $source_table=eval { $env->open_source_table($table_name) };
				if ( $@ ) {
					$logger->warning( "$environnement.$table_name \t: ERROR" );
					$error_count++;
				}
				else {
					log_info( "$environnement.$table_name \t: OK" );
				}

				# on ne verifie que la première table si pas de check complet
				if ( not $check_all_table ) {
					last TABLE;
				}
			}
		}
	}
	
	if ( $error_count ) {
		$logger->notice("Etat de I-SIP \t: ERROR");
	}
	else {
		$logger->notice("Etat de I-SIP \t: OK");
	}
	
return $return;
# END RUN
}

exit !run(@ARGV) if !caller;
