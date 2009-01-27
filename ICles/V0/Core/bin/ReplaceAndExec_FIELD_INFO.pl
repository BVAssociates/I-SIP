#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

ReplaceAndExec_FIELD_INFO - Met � jour une ligne dans un processeur Administrate

=head1 SYNOPSIS

 ReplaceAndExec_FIELD_INFO [-h] [-v] INTO <Table> VALUES <Values>
 
=head1 DESCRIPTION

Met � jour une ligne dans un processeur Administrate.

Sp�cifique � la table FIELD_INFO


=head1 ENVIRONNEMENT

=over 4

=item ITOOLS : L'environnement des ITools doit �tre charg�

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne (combiner avec -v pour l'aide compl�te)

=item -v : Mode verbeux

=item -f pour forcer la cr�ation du fichier de donn�es

=back

=head1 ARGUMENTS

=over 4

=item Table : Nom de la table dans laquelle ins�rer les valeurs

=item - : Utiliser les valeurs provenant de l'entr�e standard

=item Values : Valeurs � ins�rer dans la table

=item Condition : Condition de s�lection de la ligne � modifier

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
	@_=grep {defined $_} @_;
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	sortie(202);
}

sub log_info {
	@_=grep {defined $_} @_;
	print STDERR "INFO: ".join(" ",@_)."\n"; 
}


#  Traitement des Options
###########################################################

my @argv_save=@ARGV;

my %opts;
getopts('hv', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 4) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

log_info(join(' ',@ARGV));

my $INTO_WORD=shift;
my $table_name=shift;
my $VALUES_WORD=shift;

my $values;
my $conditions;
foreach (@ARGV) {
	if (uc eq 'WHERE') {
		$conditions=join(' ',shift);
		last;
	} else {
		$values .= $_;
	}
}

use Encode;
$values=encode("cp850",$values);

log_info("VALUES: ",$values);
log_info("CONDITION: ",$conditions);

if (uc($INTO_WORD) ne 'INTO' or uc($VALUES_WORD) ne 'VALUES') {
	log_info("Ligne de commande incorrect");
	usage($debug_level);
	sortie(202);
}

log_erreur("Conditions non g�r�es") if $conditions;

#  Corps du script
###########################################################
my $bv_severite=0;

if ($table_name ne $table_name) {
	log_erreur("Table $table_name non g�r� par $0");
}

#my $environnement=$ENV{Environnement};
#die "Variable <Environnement> not set" if not $environnement;

my $table_ikos=$ENV{TABLE_NAME};
die "Variable <TABLE_NAME> not set" if not $table_ikos;

use ITable::ITools;
my $itools_table=ITools->open($table_name);
my $separator=$itools_table->output_separator;
my @field=$itools_table->field;

use Isip::Environnement;
#my $env_sip=Environnement->new($environnement);
my $local_table;
my %row;

#$local_table=$env_sip->open_local_table($table_ikos."_INFO", {timeout => 10000, debug => $debug_level});
$local_table=Environnement->open_local_table($table_ikos."_INFO", {timeout => 10000, debug => $debug_level});

$local_table->query_field(@field);
%row=$local_table->array_to_hash(split(/$separator/, $values, -1));

use POSIX qw(strftime);
$row{DATE_UPDATE} = strftime "%Y-%m-%d %H:%M", localtime if exists $row{DATE_UPDATE};
#$row{USER_UPDATE} = $ENV{IsisUser} if exists $row{USER_UPDATE};
$local_table->update_row( %row );

sortie($bv_severite);