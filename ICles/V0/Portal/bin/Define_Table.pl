#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

define_table - réimplemente Define_Table.exe pour I-SIP

=head1 SYNOPSIS

 Define_Table.pl [-h][-v] tablename
 
=head1 DESCRIPTION

Lit et affiche un fichier de définition.
Modifie certaines variables de la définition à la volée.

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

=item tablename : table a définir

=back

=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits réservés.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	exit shift;
}

sub usage($) {
	my $verbosity=shift;
	die("Define_Table [-h][-v] tablename");
}

sub log_erreur {
	die("ERREUR: ",@_);
}

sub log_info {
	warn("INFO: ",@_);
}

#  Traitement des Options
###########################################################


my %opts;
getopts('hv', \%opts) or usage(0);

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

my $table=shift;
my $date_explore=shift;

$date_explore=$ENV{DATE_EXPLORE} if not $date_explore;

#  Corps du script
###########################################################
my $bv_severite=0;


use ITable::ITools;

my $table_itools=ITools->open($table);

my $definition=$table_itools->define();

my @output;
push @output, "DEFFILE=".$definition->def_file();
push @output, "OBJECT=".$definition->name();
push @output, "TABLE=".$definition->name();
push @output, "HEADER=".$definition->header();
push @output, "TYPE=".$definition->type();
push @output, "SEP=".$definition->separator();
push @output, "FILE=".$definition->file();
push @output, "COMMAND=".$definition->command();
push @output, "FORMAT=".join($definition->separator(),$definition->field());
my %size=$definition->size();
push @output, "SIZE=".join($definition->separator(),@size{$definition->field()});
my @not_null=$definition->not_null();
push @output, "NOTNULL=".join($definition->separator(),@not_null);
my %row=$definition->row();
push @output, "ROW=".join($definition->separator(),@row{$definition->field()});
push @output, "KEY=".join($definition->separator(),$definition->key());
push @output, "SORT=".join($definition->separator(),$definition->sort());

# cas spécial des FKEY0N
my $fkey_count=0;
foreach my $fkey ( $definition->fkey() ) {
	$fkey_count++;
	my $temp_output=sprintf("FKEY%02d=%2s",$fkey_count, $fkey );
	push @output, $temp_output;
}


# Modification de la définition à la volée dans le cas d'exploration temporelle
use Isip::Environnement;

my $new_format;
my $new_size;
my $new_row;
my ($env_name,$table_real) = $table =~ /^IKOS_TABLE_([A-Za-z0-9]+)_(\w+)$/;
if ($env_name and $table_real and $date_explore) {
	log_info("modification de la définition à la volée");
	
	require "PC_GENERATE_MENU.pl";
	log_erreur("<Environnement> n'est pas défini") if not $env_name;
	my $env=Environnement->new($env_name);
	
	my @definition=split(/\n/,pc_generate_menu::get_def_table_string($env,$table_real,$date_explore));
	
	# recalcul des definitions
	($new_format,$new_size)=map {s/^SIZE=\"//;s/^FORMAT=\"//;s/\"$//;$_} grep {/^(FORMAT|SIZE)/} @definition;
	$new_row=join('@', map {"%".$_."%"} split(/@/,$new_format));
}

foreach (@output) {
	if ($new_format and /^FORMAT=/) {
		s/FORMAT=.+/FORMAT=$new_format/;
	}
	elsif ($new_row and /^ROW=/) {
		s/ROW=.+/ROW=$new_row/;
	}
	elsif ($new_size and /^()?SIZE=/) {
		s/SIZE=.+/SIZE=$new_size/;
	}
	
	if ( $^O eq 'MSWin32' ) {
		printf("set %s\n",$_);
	}
	else {
		my ($var,$value) = split(/=/);
		printf("%s='%s' ; export %s\n",$var,$value,$var);
	}
}
