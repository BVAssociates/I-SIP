#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

ReplaceAndExec - Met à jour une ligne dans un processeur Administrate

=head1 SYNOPSIS

 InsertAndExec [-h] [-v] INTO <Table> VALUES <-|Values> [WHERE <Condition>]
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement à la date courante

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

log_info("VALUES: ",$values);
log_info("CONDITION: ",$conditions);

if (uc($INTO_WORD) ne 'INTO' or uc($VALUES_WORD) ne 'VALUES') {
	log_info("Ligne de commande incorrect");
	usage($debug_level);
	sortie(202);
}
# DEBUG
$ENV{GSL_FILE}="TEST";
$ENV{ENVIRON}="TEST";

log_erreur("GSL_FILE n'est pas defini dans l'environnement" ) if not exists $ENV{GSL_FILE};

log_erreur("ENVIRON n'est pas defini dans l'environnement" ) if not exists $ENV{ENVIRON};



#  Corps du script
###########################################################
my $bv_severite=0;

# if we administrate a table other than FIELD_HISTO, we use the original script
if ($table_name ne "FIELD_HISTO") {
	log_info("$table_name : exec official script");
	# routine to find the next ReplaceAndExec in Path
	use File::Spec::Functions qw/:ALL/;
	my (undef,undef,$current_script)=splitpath($0);
	my $count=1;
	my $next_script;
	foreach my $dir (path()) {
		$next_script=catfile($dir,$current_script);
		if (-r $next_script) {
			if ($count-- <= 0) {
				exec "perl",'"'.$next_script.'"',@argv_save;
			}
		}
	}
}

use IKOS::SIP;

my $env_sip=SIP->new($ENV{ENVIRON });

my $local_table=$env_sip->open_local_table($ENV{GSL_FILE}."_HISTO");

$local_table->update_row_array( split(/\s*,\s*/, $values));

sortie($bv_severite);