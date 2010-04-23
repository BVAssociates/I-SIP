@rem = '--*-Perl-*--
@echo off
if "%OS%" == "Windows_NT" goto WinNT
perl -x -S "%0" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto endofperl
:WinNT
perl -x -S %0 %*
if NOT "%COMSPEC%" == "%SystemRoot%\system32\cmd.exe" goto endofperl
if %errorlevel% == 9009 echo You do not have Perl in your PATH.
if errorlevel 1 goto script_failed_so_exit_with_non_zero_val 2>nul
goto endofperl
@rem ';
#!/usr/bin/perl
#line 15
package pc_purge_histo;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog qw'$logger log_screen_only';

#  Documentation
###########################################################
=head1 NAME

PC_PURGE_HISTO - Supprime les entrées des tables HISTO

=head1 SYNOPSIS

 PC_PURGE_HISTO.pl [-h] [-v] [-d | -k] environnement
 
=head1 DESCRIPTION

Supprime les entrées des tables HISTO. Par défaut, le script ne fait que renommer
les tables HISTO pour qu'elles soient ensuite réellement supprimer par le script 
PC_CLEAN_BASELINE.pl .

Avec l'option "-d", la table renommée est immédiatement supprimée.

Avec l'option "-k", on insert dans la nouvelle table les valeurs et commentaires
dans leur dernière valeur connues.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -d : supprime reellement au lieu de renommer (DROP)

=item -k : garder la dernière collecte

=back

=head1 ARGUMENTS

=over

=item environnement

=item date

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
	
	#  Traitement des Options
	###########################################################


	my %opts;
	getopts('hvdk', \%opts);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};
	
	my $drop_histo=$opts{d};
	my $keep_last=$opts{k};
	
	#  Traitement des arguments
	###########################################################

	if ( @ARGV < 1) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}

	my $environnement=shift @ARGV;

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::Environnement;

	my $env=Environnement->new($environnement);
	
	#log_screen_only();
	
	foreach my $table_name ($env->get_table_list) {
		#my $pid = fork();
		#if (!$pid) {

			$logger->notice("deplacement vers la corbeille de $table_name\_HISTO");
			my $date=$env->drop_histo($table_name);
			if ($keep_last) {
				$logger->notice("réinsertion de la dernière collecte dans $table_name\_HISTO");
				
				# little quirk to get the query of the last update data set
				my $histo_table=$env->open_local_from_histo_table($table_name);
				my $select_query=$histo_table->get_query();
				undef $histo_table;
				$select_query =~ s/$table_name\_HISTO/$table_name\_$date/g;
				
				my $local_table=$env->open_local_table($table_name."_HISTO");
				$local_table->execute("INSERT INTO ".$table_name."_HISTO ".$select_query);
			}
			if ($drop_histo) {
				$logger->notice("suppression complète de $table_name\_HISTO");
				$env->drop_histo_baseline($table_name,$date);
			}

			$logger->notice("terminé pour $table_name");
		#	last;
		#}
	}
	#wait;
	
	my $date_update=$env->open_local_table("DATE_UPDATE");
	# delete all but baselines
	if ($keep_last) {
		$date_update->execute("DELETE FROM DATE_UPDATE WHERE DATE_HISTO NOT IN (SELECT DATE_HISTO FROM DATE_UPDATE ORDER BY DATE_HISTO DESC  LIMIT 1) AND BASELINE=0");
	}
	else {
		$date_update->execute("DELETE FROM DATE_UPDATE WHERE BASELINE=0");
	}
	
	require "PC_UPDATE_CACHE.pl";
	pc_update_cache::run($environnement);
	
	return 1;
}

exit !run(@ARGV) if not caller;
1;
__END__
:endofperl
