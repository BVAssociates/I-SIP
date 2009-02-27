#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;



sub usage($) {
	my $verbosity=shift;
	pod2usage(-verbose => $verbosity, -noperldoc => 1);
	exit 1;
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

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;

#  Corps du script
###########################################################
my $bv_severite=0;
use ITable::ITools;
use Isip::Environnement;

my @key;

my $table_key=ITools->open("FIELD_LINK");
while (my @row=$table_key->fetch_row_array()) {
	push @key, join(',',@row);
}

my $env_sip=Environnement->new($environnement);
my $table_info=$env_sip->open_local_table("COLUMN_INFO");

foreach my $tuple (@key) {

	my ($table, $key, $ftable, $fkey) = split(',',$tuple);
	
	my %new_row=(TABLE_NAME => $table, FIELD_NAME => $key, FOREIGN_TABLE => $ftable, FOREIGN_KEY => $fkey );
	
	$table_info->update_row(%new_row);
}







