#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

ReplaceAndExec_IKOS_FIELD - Met à jour une ligne dans un processeur Administrate

=head1 SYNOPSIS

 ReplaceAndExec_IKOS_FIELD [-h] [-v] INTO <Table> VALUES <Values>
 
=head1 DESCRIPTION

Met à jour une ligne dans un processeur Administrate.

Spécifique aux tables du type IKOS_FIELD_*


=head1 ENVIRONNEMENT

=over 4

=item ITOOLS : L'environnement des ITools doit être chargé

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne (combiner avec -v pour l'aide complète)

=item -v : Mode verbeux

=item -f pour forcer la création du fichier de données

=back

=head1 ARGUMENTS

=over 4

=item Table : Nom de la table dans laquelle insérer les valeurs

=item - : Utiliser les valeurs provenant de l'entrée standard

=item Values : Valeurs à insérer dans la table

=item Condition : Condition de sélection de la ligne à modifier

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	my $exit_value=shift;
	$logger->notice("Sortie du programme, code $exit_value");
	exit $exit_value;
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

my @argv_save=@ARGV;

log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

my %opts;
getopts('hvd', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};
my $dont_decode=$opts{d};

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
$values=encode("cp850",$values) if not $dont_decode;

log_info("VALUES: ",$values);
log_info("CONDITION: ",$conditions);

if (uc($INTO_WORD) ne 'INTO' or uc($VALUES_WORD) ne 'VALUES') {
	log_info("Ligne de commande incorrect");
	usage($debug_level);
	sortie(202);
}

log_erreur("Conditions non gérées") if $conditions;

#  Corps du script
###########################################################
my $bv_severite=0;

if (not $table_name =~ /^IKOS_FIELD_([[:alpha:]]+)_(\w+)$/) {
	log_erreur("Table $table_name non géré par $0");
}
my ($environnement,$table_ikos) = ($1,$2);

use ITable::ITools;
my $itools_table=ITools->open($table_name);
my $separator=$itools_table->output_separator;
my @field=$itools_table->field;

use Isip::Environnement;
my $env_sip=Environnement->new($environnement);
my $local_table;
my %row;

$local_table=$env_sip->open_local_table($table_ikos."_HISTO", {timeout => 30000, debug => $debug_level});

# add dynamic field. Needed for array_to_hash()
$local_table->dynamic_field("TEXT","TYPE","ICON");
#$local_table->dynamic_field("DOCUMENTATION");
#$local_table->query_field("ID","COMMENT","STATUS","DOCUMENTATION");
#$local_table->query_field("ID","COMMENT","STATUS","MEMO");

$local_table->query_field(@field);
%row=$local_table->array_to_hash(split(/$separator/, $values, -1));

# keep DOCUMENTATION field
#my $description=$row{DOCUMENTATION};

#delete dynamic field from line to insert
foreach ($local_table->dynamic_field()) {
	delete $row{$_};
}

use POSIX qw(strftime);
my $current_date=strftime "%Y-%m-%dT%H:%M", localtime;
my $current_user=$ENV{IsisUser};
#Update comment
$row{DATE_UPDATE} = $current_date;
$row{USER_UPDATE} = $current_user;
$local_table->update_row( %row );

#Update documentation
#$doc_table=$env_sip->open_documentation_table($table_ikos, {timeout => 10000, debug => $debug_level});

sortie($bv_severite);