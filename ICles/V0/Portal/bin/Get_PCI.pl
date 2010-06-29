#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

Get_PCI - Affiche le panneau de commande I-SIS d'une table

=head1 SYNOPSIS

 Get_PCI.pl [-h][-v] [-s] [-e] [-t] FOR table
 
=head1 DESCRIPTION

Verifie dans la base que la table contient des groupes

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement des I-TOOLS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -s : n'affiche pas l'entête I-TOOLS

=item -e : Evalue les variables du type %[var]

=item -t : Evalue les tests de conditions

=back

=head1 ARGUMENTS

=item table : affiche le menu pour la table

=head1 AUTHOR

Copyright (c) 2010 BV Associates. Tous droits réservés.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	exit shift;
}

sub usage($) {
	my $verbosity=shift;
	#pod2usage(-verbose => $verbosity, -noperldoc => 1);
	sortie(202); 
}

sub log_erreur {
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	#sortie(202);
	die("ERREUR: ".join(" ",@_)."\n");
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
}


#  Traitement des Options
###########################################################

my %opts;
getopts('hvesta', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $all_nodetype=$opts{a};

my $no_header=$opts{s};
my $eval_vars=$opts{e};
my $eval_condition=$opts{t};

#  Traitement des arguments
###########################################################

if ( @ARGV < 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $for_keyword = shift;
my $table_name  = shift;

usage($debug_level) if uc($for_keyword) ne 'FOR' ;


#  Corps du script
###########################################################

use ITable::ITools;

# test l'accès à la table
# print header
my $def_table = ITools->open($table_name);

$ENV{TableName}=$table_name;
my $pci_table = ITools->open("pci");

if ( ! $no_header ) {
	print $pci_table->get_header()."\n";
}

# cherche les entrées de menu (erreur non bloquante)
while ( my %menu_item = eval { $pci_table->fetch_row_pp() } ) {
	
	if ( ! $all_nodetype ) {
		# detect if called from Table node or Item node
		if ( $ENV{Key} ) {
			next if $menu_item{NodeType} eq 'Table';
		}
		else {
			#TODO probleme si Key est vide
			#next if $menu_item{NodeType} eq 'Item';
		}
	}
	
	if ( $eval_vars ) {
		foreach my $field ( "Group", "Label", "PreProcessing", "Arguments", "PostProcessing", "Icon" ) {
			$menu_item{$field} = $pci_table->evaluate_variables($menu_item{$field});
		}
	}
	
	if ( $eval_condition ) {
		
		if ( $menu_item{Condition} =~ /^perl -e ([\"\'])(.*)\1$/ ) {
			my $perl_condition = $2;
			$perl_condition =~ s/\bexit\b/return/g;

			# TODO utilisation de Safe pour éviter les problèmes de sécurité (+lent)
			#use Safe;
			#my $sandbox = Safe->new();
			#$sandbox->share("%ENV");
			#my $result=$sandbox->reval($perl_condition);
			my $result=eval($perl_condition);
			if ($@) {
				die("Problème pendant l'execution de la condition du menu <$menu_item{Label}> : $@");
			}
			$menu_item{Condition}=($result)?"false":"true";
		}
		elsif ( $menu_item{Condition} =~ /^perl/ ) {
			
			# recherche du chemin de Perl pour eviter l'appel au shell
			my $path_sep;
			my $perl_bin;
			my $script_extension;
			if ( $^O eq 'MSWin32') {
				$path_sep         = ';';
				$perl_bin         = 'perl.exe';
				$script_extension = '.bat';
			}
			else {
				$path_sep         = ':';
				$perl_bin         = 'perl';
				$script_extension = '';
			}
			my ($perl_path) = grep { -r "$_/$perl_bin"} split ( /$path_sep/, $ENV{PATH} );
			
			# le module WindowsArgsHook interprete les arguments à la place du Shell
			$menu_item{Condition} =~ s{^perl\b}{$perl_path/$perl_bin -MIsis::WindowsArgsHook};
			$menu_item{Condition} = $menu_item{Condition}.$script_extension;
			
			system($menu_item{Condition});
			if ( $? == -1 ) {
				die("Erreur au lancement de $menu_item{Condition}");
			}
			my $result=$? >> 8;
			
			$menu_item{Condition}=($result)?"false":"true";
		}
		elsif ( $menu_item{Condition} ) {
			# execution directe du script dans le shell
			
			system($menu_item{Condition});
			if ( $? == -1 ) {
				die("Erreur au lancement de $menu_item{Condition}");
			}
			my $result=$? >> 8;
			
			$menu_item{Condition}=($result)?"false":"true";
		}			
		else {
			$menu_item{Condition}="true";
		}
		
	}
	
	# print resulting row
	print join( $pci_table->output_separator(), @menu_item{$pci_table->field()})."\n";
}




