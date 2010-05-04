#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

define_table - r�implemente Define_Table.exe pour I-SIP

=head1 SYNOPSIS

 Define_Table.pl [-h][-v] tablename
 
=head1 DESCRIPTION

Lit et affiche un fichier de d�finition.
Modifie certaines variables de la d�finition � la vol�e.

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

=item tablename : table a d�finir

=back

=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits r�serv�s.

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
push @output, "set DEFFILE=".$definition->def_file();
push @output, "set OBJECT=".$definition->name();
push @output, "set TABLE=".$definition->name();
push @output, "set HEADER=".$definition->header();
push @output, "set TYPE=".$definition->type();
push @output, "set SEP=".$definition->separator();
push @output, "set FILE=".$definition->file();
push @output, "set COMMAND=".$definition->command();
push @output, "set FORMAT=".join($definition->separator(),$definition->field());
my %size=$definition->size();
push @output, "set SIZE=".join($definition->separator(),@size{$definition->field()});
my %row=$definition->row();
push @output, "set ROW=".join($definition->separator(),@row{$definition->field()});
push @output, "set KEY=".join($definition->separator(),$definition->key());
push @output, "set SORT=".join($definition->separator(),$definition->sort());

# cas sp�cial des FKEY0N
my $fkey_count=0;
foreach my $fkey ( $definition->fkey() ) {
	$fkey_count++;
	my $temp_output=sprintf("set FKEY%02d=%2s",$fkey_count, $fkey );
	push @output, $temp_output;
}


# Modification de la d�finition � la vol�e dans le cas d'exploration temporelle
use Isip::Environnement;

my $new_format;
my $new_size;
my $new_row;
my ($env_name,$table_real) = $table =~ /^IKOS_TABLE_([A-Za-z0-9]+)_(\w+)$/;
if ($env_name and $table_real and $date_explore) {
	log_info("modification de la d�finition � la vol�e");
	
	require "PC_GENERATE_MENU.pl";
	log_erreur("<Environnement> n'est pas d�fini") if not $env_name;
	my $env=Environnement->new($env_name);
	
	my @definition=split(/\n/,pc_generate_menu::get_def_table_string($env,$table_real,$date_explore));
	
	# recalcul des definitions
	($new_format,$new_size)=map {s/^SIZE=\"//;s/^FORMAT=\"//;s/\"$//;$_} grep {/^(FORMAT|SIZE)/} @definition;
	$new_row=join('@', map {"%".$_."%"} split(/@/,$new_format));
}

foreach (@output) {
	if ($new_format and /^(set )?FORMAT=/) {
		s/FORMAT=.+/FORMAT=$new_format/;
	}
	elsif ($new_row and /^(set )?ROW=/) {
		s/ROW=.+/ROW=$new_row/;
	}
	elsif ($new_size and /^(set )?SIZE=/) {
		s/SIZE=.+/SIZE=$new_size/;
	}
	
	print $_."\n";
}
