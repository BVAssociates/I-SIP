#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_XML_COPY - Liste les fichiers XML supplementaire

=head1 SYNOPSIS

 PC_LIST_XML_COPY.pl [-h][-v] environnement table_name
 
=head1 DESCRIPTION

Liste les fichiers XML r�pliqu� d'un fichier XML maitre

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

=item environnement

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
getopts('hvs:m', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator=',';
$separator=$opts{s} if exists $opts{s};

my $master=$opts{m} if exists $opts{m};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
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
use Isip::ITable::FieldDiff;
use Text::Diff;
use Carp;

my $env=Environnement->new($environnement);

my $table_xml=$env->open_local_table("XML_INFO");

$table_xml->query_condition("XML_NAME = '$table_name'") if $table_name;

my $xml_master;
my @xml_copy_list;
while ( my %row=$table_xml->fetch_row() ) {
	if ($row{MASTER}) {
		$xml_master=$row{XML_PATH};
	}
	else {
		push @xml_copy_list, $row{XML_PATH};
	}
}

if (not $xml_master) {
	croak("Impossible de retrouver le chemin du XML de r�f�rence");
}

$logger->notice("XML r�f�rence : $xml_master");


my $table_master=XmlFile->open($xml_master, $table_name);

foreach my $file (@xml_copy_list) {


	my $table_copy=XmlFile->open($file, $table_name);
	
	my $table_diff=DataDiff->open($table_master, $table_copy);
	
	$table_diff->set_old_field_name("value","old_value");
	
	$table_diff->query_field("DIFF","xml_path","old_value","value");
	$table_diff->compare_init;
	
	while (my %row=$table_diff->fetch_row) {
	
		# hard coded value to ignore
		next if $row{xml_path} =~ /sysnetServerName$/;
		next if $row{xml_path} =~ /sysnetCodeBase$/;
		next if $row{xml_path} =~ /lieuPC$/;
		
		print join($separator,($file,@row{$table_diff->query_field}))."\n" if $row{DIFF} ne "OK";
	}
}
