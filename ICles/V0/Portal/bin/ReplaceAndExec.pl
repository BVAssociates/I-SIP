#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

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
	$logger->info(@_);
}


# gestion des triggers PCI
my %execute_when;

sub init_trigger {
	my $table_name = shift or log_erreur("usage: init_trigger(table_name)");
	
	my @replace_actions=`Get_PCI -s for $table_name where Group=Replace`;
	if ($? >> 8) {
		log_info("Erreur lors de la recuperation du PCI");
		sortie(0);
	}
	
	foreach my $action ( @replace_actions ) {
		my @field_action=split('~', $action);
		$execute_when{ $field_action[2] } = $field_action[7];
	}
	
	return;
}

sub execute_trigger {
	my $action = shift or log_erreur("usage: execute_trigger(action)");
	
	if ( exists $execute_when{$action} ) {
		my $command=$execute_when{$action};
		
		if ( $command ) {
			log_info("Execution de la Post Action : ",$command);
			
			system($command);
			if ($? >> 8) {
				log_info("Erreur lors de l'execution de la PostAction");
				sortie(202);
			}
		}
		else {
			log_info("Trigger $action ne contient pas de commande");
		}
	}
	
	return;
}

#  Traitement des Options
###########################################################
use Encode;
map {$_=encode("cp850",$_)} @ARGV if $^O eq 'MSWin32';

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
		$conditions=join(' ',@ARGV);
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


#  Corps du script
###########################################################
my $bv_severite=0;

use ReplaceAndExec_ISIP;

init_trigger($table_name);

if ($table_name =~ /^ISIP_FIELD|IKOS_FIELD/) {
	execute_trigger('PreAction');
	$logger->info("use library ReplaceAndExec_ISIP::update_field");
	update_field($table_name,$values);
	execute_trigger('PostAction');
}
elsif ($table_name =~ /^COLUMN_INFO$/i) {
	execute_trigger('PreAction');
	$logger->info("use library ReplaceAndExec_ISIP::update_column_info");
	update_column_info($table_name,$values);
	execute_trigger('PostAction');
}
elsif ($table_name =~ /^TABLE_INFO|XML_INFO|CACHE_.*|FIELD_.*$/i) {
	execute_trigger('PreAction');
	$logger->info("use library ReplaceAndExec_ISIP::update_info");
	update_info($table_name,$values);
	execute_trigger('PostAction');
}
else {
	# otherwise,  we use the original script
	$logger->info("use Legacy ReplaceAndExec");
	system("Replace INTO $table_name VALUES \"$values\"");
	exit $? >> 8;

}


sortie($bv_severite);
