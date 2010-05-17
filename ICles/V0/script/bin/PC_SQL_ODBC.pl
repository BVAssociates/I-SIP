#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_EXEC_ODBC - execute une requete sur un environnement

=head1 SYNOPSIS

 reinit_all.pl [-h][-v] environnement module table ["SELECT * FROM ..."]
 
=head1 DESCRIPTION

Execute une requete arbritraire sur la base DB2 correspondant aux arguments.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over

=item environnement : nom de l'environnement au sens IKOS

=item module : nom du module au sens IKOS

=item table : table à ouvrir

=item requete : requete SQL à executer

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
getopts('hvd', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $desc=$opts{d} if $opts{d};

#  Traitement des arguments
###########################################################

if ( @ARGV < 3) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift @ARGV;
my $module=shift @ARGV;
my $table=shift @ARGV;
my $query=shift @ARGV;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::IsipConfig;
use ITable::ODBC;

my $config=IsipConfig->new();

if (grep {$module eq $_} $config->get_module_list()) {
	$module=$config->get_odbc_database_name($module,$environnement);
}

my $db2=ODBC->open($module,
			$table,
			$config->get_odbc_option($environnement));

$db2->custom_select_query($query) if $query =~ /^\s*select/i;

if ($desc) {			
	print  join("\n",$db2->field)."\n";
}
else {
	while (my @row=$db2->fetch_row_array) {
		print join(',',@row)."\n";
	}
}