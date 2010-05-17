#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_MAIL_GROUP - Liste des groupes d'un utilisateur

=head1 SYNOPSIS

 PC_LIST_MAIL_GROUP.pl [-h] [-v] environnement user
 
=head1 DESCRIPTION

Liste les etats possibles

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head1 ARGUMENTS

=over

=item environnemnet 

=item utilisateur

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

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
getopts('hvs:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $separator = $opts{s};
$separator    = ',' if not $separator;

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $user=shift;

#  Corps du script
###########################################################

use ITable::ITools;
use Isip::Environnement;

my $user_table=ITools->open("PortalAccess");

$user_table->query_field("Groups");
$user_table->query_condition("IsisUser=".$user);

my ($group_list_txt) = $user_table->fetch_row_array();
$group_list_txt =~ s/^\s*//;
$group_list_txt =~ s/\s*$//;

my @group_list;
@group_list=split( /\s*,\s*/, $group_list_txt) if $group_list_txt;

my $env = Environnement->new($environnement);

my $field_mail_table = $env->open_local_table("FIELD_MAIL");

if ( @group_list ) {
	$logger->notice("recherche pour les groupes ",join('/',@group_list));
	$field_mail_table->query_condition(join( ' OR ', map {"MAIL_GROUP = '$_'"} @group_list) );
}
else {
	$field_mail_table->query_condition("MAIL_GROUP = ''");
}

while (my @row = $field_mail_table->fetch_row_array() ) {
	print join($separator, @row)."\n";
}

