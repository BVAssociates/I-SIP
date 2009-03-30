#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_SET_LABEL - force le label d'une clef et d'un champ

=head1 SYNOPSIS

 PC_SET_LABEL.pl [-h] [-v] environnement tablename clef champ label
 
=head1 DESCRIPTION

Force l'icone d'un champ d'une ligne dans un �tat invariable.

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

=item tablename

=item clef

=item champ

=item label : code de l'icone (Voir IsipRules)

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

if ( @ARGV < 4) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $table_name=shift;
my $key=shift;
my $field=shift;
my $icon=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

use Isip::Environnement;

my $env=Environnement->new($environnement);
my $table=$env->open_local_table($table_name."_LABEL");


$table->begin_transaction;

$table->delete_row(TABLE_KEY => $key, FIELD_NAME => $field);
if ($icon) {
	$table->insert_row(TABLE_KEY => $key, , FIELD_NAME => $field, LABEL => $icon);
	log_info("$field pour la clef $key de $table_name labellis� $icon");
}
else {
	log_info("Label retir� de $field pour la clef $key de $table_name");
}

$table->commit_transaction;
