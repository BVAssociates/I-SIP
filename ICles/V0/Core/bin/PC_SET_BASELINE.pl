#!/usr/bin/perl
package pc_set_baseline;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog qw'$logger log_screen_only';

#  Documentation
###########################################################
=head1 NAME

PC_SET_BASELINE - Construit une baseline

=head1 SYNOPSIS

 PC_SET_BASELINE.pl [-h] [-v] [-d | -m message] environnement date
 
=head1 DESCRIPTION

Construit une baseline sur un environnement

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -d : supprime une baseline

=item -m message : Ajoute une description � la baseline

=back

=head1 ARGUMENTS

=over

=item environnement

=item date

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
	
	#  Traitement des Options
	###########################################################


	my %opts;
	getopts('hvdm:', \%opts);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	
	my $drop_baseline=$opts{d};
	my $message=$opts{m};

	#  Traitement des arguments
	###########################################################

	if ( @ARGV < 2) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}

	my $environnement=shift @ARGV;
	my $date=shift @ARGV;

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::Environnement;

	$logger->notice("cr�ation d'une baseline � la date $date");
	my $env=Environnement->new($environnement);
	
	#log_screen_only();
	
	my $baseline_list=$env->open_local_table("DATE_UPDATE");
	$baseline_list->query_condition("DATE_HISTO='$date'");
	
	my %baseline_info=$baseline_list->fetch_row();
	$baseline_list->finish;
	
	if (not %baseline_info) {
		log_erreur("la date $date n'est pas une date de collecte valide");
	}
	else {
		if (not $drop_baseline and $baseline_info{BASELINE}) {
			log_erreur("la date $date est d�j� une date de baseline");
		}
		elsif ($drop_baseline and not $baseline_info{BASELINE}) {
			log_erreur("la date $date n'est pas une date de baseline");
		}
	}
	
	foreach my $table_name ($env->get_table_list) {
		#my $pid = fork();
		#if (!$pid) {
			if ($drop_baseline) {
				$logger->notice("suppression d'une baseline pour $table_name � la date $date");
				
				# suppression r�elle diff�r�e possible (ex : avant la collecte)
				#my $table=$env->drop_histo_baseline($table_name,$date);
				
				$baseline_info{BASELINE}=0;
			}
			else {
				$logger->notice("cr�ation d'une baseline pour $table_name � la date $date");
				my $table=$env->create_histo_baseline($table_name,$date);
				$baseline_info{BASELINE}=1;
			}
			$logger->notice("termin� pour $table_name");
		#	last;
		#}
	}
	#wait;
	
	$baseline_info{DESCRIPTION}=$message if $message;
	
	$baseline_list->update_row(%baseline_info);
	
	if ($drop_baseline) {
		$logger->notice("Baseline supprim�e pour $environnement � la date $date");
	}
	else {
		$logger->notice("Baseline cr�� pour $environnement � la date $date");
	}
}

exit !run(@ARGV) if not caller;