#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_INFO - Liste les informations d'une table

=head1 SYNOPSIS

 PC_LIST_LOCAL.pl [-h][-v] environnement tablename
 
=head1 DESCRIPTION

Liste les informations d'une table contenue dans la table suffix�e _INFO de la base

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over

=item environnement : environnement � utiliser

=item tablename : table a ouvrir

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
getopts('hv', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environ=shift;
my $table_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use Isip::Environnement;

#my $sip=Environnement->new($environ);
#my $table=$sip->open_local_table($table_name."_INFO", {debug => 0 });
my $table=Environnement->open_local_table($table_name."_INFO", {debug => 0 });

die "unable to open local $table_name\_INFO in env $environ" if not defined $table;

while (my @line=$table->fetch_row_array()) {
	print join(',',@line)."\n";
}
