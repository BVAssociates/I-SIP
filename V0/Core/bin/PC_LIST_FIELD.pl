#!/usr/bin/perl

#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_HISTO - Liste les champs d'une table dans un environnement

=head1 SYNOPSIS

 PC_LIST_FIELD_HISTO.pl [-h] [-v] environnement tablename
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement à la date courante

=head1 ENVIRONNEMENT

=over 4

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS 

=over 4

=item * environnement à utiliser

=item * table a décrire

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

if ( @ARGV != 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $tablename=shift;

# quirk to test
$ENV{Environnement}=$environnement;
$ENV{GSL_FILE}=$tablename;
#$ENV{AAPTYCOD}='HCPC';

#  Corps du script
###########################################################
use IKOS::DATA::ITools;
use IKOS::SIP;

my $bv_severite=0;

# New SIP Object instance
my $ikos_sip = SIP->new($environnement, {debug => $debug_level});

# recuperation de la clef primaine de la table
my $table_key = $ikos_sip->get_table_key($tablename);

if (not $table_key) {
	log_erreur("pas de clef primaine pour la table $tablename");
	sortie(202);
}

my @table_key_list=split(',',$table_key);
my @table_key_list_value;


foreach (@table_key_list) {
	push @table_key_list_value, $ENV{$_} if exists $ENV{$_};
	if (not $ENV{$_}) {
		log_erreur("Clef primaine <$ENV{$_}> n'est pas definie dans l'environnement");
		sortie(202);
	}
}

my $table_key_value=join(',',@table_key_list_value);

print STDERR "KEY= $table_key\n";
print STDERR "KEY_VAL=$table_key_value\n";

# fetch selected row from histo table
my $table_histo = $ikos_sip->open_local_table($tablename."_HISTO", {debug => 1});

my $select_histo= "SELECT ID,DATE_HISTO, DATE_UPDATE,USER_UPDATE, TABLE_NAME, TABLE_KEY, FIELD_NAME, FIELD_VALUE, COMMENT, STATUS
	FROM
	$tablename\_HISTO INNER JOIN (
		SELECT
		TABLE_KEY as TABLE_KEY_2,
		FIELD_NAME as FIELD_NAME_2,
		max(DATE_HISTO) AS DATE_MAX
		FROM
		$tablename\_HISTO
		WHERE TABLE_KEY = '$table_key_value'
		GROUP BY FIELD_NAME_2, TABLE_KEY_2)
	ON  (TABLE_KEY = TABLE_KEY_2) AND (FIELD_NAME = FIELD_NAME_2) AND (DATE_HISTO = DATE_MAX)
	ORDER BY TABLE_KEY;";
	
$table_histo->custom_select_query($select_histo);

while (my @line=$table_histo->fetch_row_array() ) {
	print join('%',@line)."\n";
}
