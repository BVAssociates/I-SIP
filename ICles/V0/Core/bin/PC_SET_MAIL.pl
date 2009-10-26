#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_SET_MAIL - ajouter le champ dans la table FIELD_MAIL

=head1 SYNOPSIS

 PC_SET_MAIL.pl [-h] [-v] (-d|-a) environnement tablename clef champ groupe
 
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

=item -d : supprime l'entr�e (delete) 

=item -a : ajoute l'entr�e � un groupe (add) 

=back

=head1 ARGUMENTS

=over

=item environnement

=item tablename

=item clef

=item champ

=item groupe

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

sub log_error {
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
getopts('hvda', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

usage($debug_level+1) if not ($opts{a} xor $opts{d} );

my $add_group=$opts{a};

#  Traitement des arguments
###########################################################

if ( @ARGV < 5) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $table_name=shift;
my $key=shift;
my $field=shift;
my $group=shift;

#  Corps du script
###########################################################
my $bv_severite=0;

use ITable::ITools;
use Isip::Environnement;

my $env=Environnement->new($environnement);

my $alert_table=$env->open_local_table("FIELD_MAIL");


my %alert_table_line=(
		MAIL_GROUP => $group,
		TABLE_NAME => $table_name,
		TABLE_KEY => $key,
		FIELD_NAME => $field,
	);

$alert_table->delete_row(%alert_table_line);

if ( $add_group ) {

	log_info("Ajout de la ligne au groupe : ".$group);
	# add update info
	use POSIX qw(strftime);
	my $timestamp=strftime "%Y-%m-%dT%H:%M", localtime;
	my $current_user=$ENV{IsisUser};
	
	$alert_table_line{DATE_UPDATE} = $timestamp;
	$alert_table_line{USER_UPDATE} = $current_user;
	
	$alert_table->insert_row(%alert_table_line);
}




