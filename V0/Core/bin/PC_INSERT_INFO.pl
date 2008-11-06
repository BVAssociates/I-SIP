#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_INSERT_INFO.pl - Insert toutes les donnes de la table Reference dans la table Info

=head1 SYNOPSIS

 PC_INSERT_INFO.pl.pl environnement tablename
 
=head1 DESCRIPTION

Insert toutes les donnes de la table Reference dans la table Info.

Si des données sont déjà présentes dans la table, elles sont écrasées.

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head1 ARGUMENTS

=head2 * environnement à utiliser

=head2 * table a traiter

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

if ( @ARGV != 2 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $table_name=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use IKOS::SIP;
use IKOS::DATA::ITools;

use POSIX qw(strftime);
    

my $env_sip = SIP->new($environnement);

my $db2_table = ITools->open("INFO_TABLE");

$db2_table->query_condition("TABLE_NAME = '$table_name'") if $table_name;

while (my %db2_table_line = $db2_table->fetch_row() ) {

	my $table_name=$db2_table_line{TABLE_NAME};
	
	#open IKOS table for DATA
	my $current_table=$env_sip->open_ikos_table($table_name, {debug => 1});
	my $histo_table=$env_sip->open_local_table($table_name."_INFO", {debug => 1});
	#my $table_key= $db2_table_line{PRIMARY_KEY} ;
	

	my $date_current = strftime "%Y-%m-%d %H:%M:%S", localtime;
	my %size=$current_table->size();
	my %field_txt=$current_table->field_txt();
	my @row_list;
	$histo_table->begin_transaction();
	foreach my $field ( $current_table->field() ) {
	
		my %info_line;
		$info_line{FIELD_NAME} = $field;
		
		#$info_line{DATE_UPDATE} = $date_current;

		if ($size{$field} =~ /^(\w+)\((\d+)\)$/) {
			$info_line{DATA_TYPE} = $1;
			$info_line{DATA_LENGTH} = $2;
		} else {
			$info_line{DATA_LENGTH} = $size{$field};
		}
		$info_line{TEXT} = $field_txt{$field};
		
		$histo_table->insert_row(%info_line);
	}
	$histo_table->commit_transaction();
		
}

sortie($bv_severite);