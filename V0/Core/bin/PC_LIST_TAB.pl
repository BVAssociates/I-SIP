#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_LIST_TAB - Liste le contenu d'une table dans un environnement

=head1 SYNOPSIS

 PC_LIST_TAB.pl [-h][-v] environnement tablename
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement � la date courante

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=head1 OPTIONS

=over4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over4

=item environnement : environnement � utiliser

=item tablename : table a ouvrir

=back

=head1 AUTHOR

BV Associates, 16/10/2008

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
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	sortie(202);
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
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
use IKOS::SIP;

my $sip=SIP->new($environ);
my $table=$sip->open_local_from_histo_table($table_name, {debug => 0 });

my @query_field=$sip->get_table_field($table_name);
$table->query_field(@query_field);

die "unable to open local $table_name in env $environ" if not defined $table;

while (my @line=$table->fetch_row_array()) {
	print join(',',@line)."\n";
}
