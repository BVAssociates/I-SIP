#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_GENERATE_MENU - les champs les champs d'une table Historique depuis la référence

=head1 SYNOPSIS

 PC_GENERATE_MENU.pl environnement tablename
 
=head1 DESCRIPTION

Créer les PCI et DEF nécéssaires à la declaration d'une table dans SIP IKOS

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head1 ARGUMENTS

=head2 * environnement à utiliser

=head2 * table a décrire

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
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	sortie(202);
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
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

if ( @ARGV < 1 ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement=shift;
my $table_name=shift;

#  Corps du script
###########################################################

use IKOS::SIP;
use IKOS::DATA::ITools;


my $bv_debug=0;


#####  BEGIN TEMPLATES ##### 
my $separator=',';

my @virtual_field=("STATUS");


my $def_template = 'COMMAND="PC_LIST_TAB.pl %%ENVIRON%% %s %%DATE_HISTO%%"
SEP="%s"
FORMAT="%s"
SIZE="%s"
KEY="%s"
';
my $fkey_def_template='FKEY="[%s] on %s[%s]"
';

my $pci_template='Item~~Afficher Ligne~expl~~GSL_FILE=%s~DisplayTable~FIELD~0~~Expand
Item~~Editer ligne~expl~~GSL_FILE=%s~Administrate~FIELD~0~~Expand
Item~~Explore Champs~expl~~GSL_FILE=%s~Explore~FIELD~0~~Expand
';
my $pci_fkey_template='Item~Tables liées~%s~expl~~~Explore~%s~0~~Expand
';

my $label_template='%s.Item;line_%%[STATUS];';

##### END TEMPLATES ##### 

# ENV info

my $def_path=sprintf('%s\%s\_Services\%s\def',$ENV{CLES_HOME},$ENV{ICleName},$ENV{ServiceName});
die "$def_path not readable" if not -r $def_path;
die "$def_path not writable" if not -w $def_path;
die "$def_path not in BV_DEFPATH" if $ENV{BV_DEFPATH} !~ /\Q$def_path\E/;

my $pci_path=sprintf('%s\%s\_Services\%s\pci',$ENV{CLES_HOME},$ENV{ICleName},$ENV{ServiceName});
die "$pci_path not readable" if not -r $pci_path;
die "$pci_path not writable" if not -w $pci_path;
die "$pci_path not in BV_PCIPATH" if $ENV{BV_PCIPATH} !~ /\Q$pci_path\E/;

my $list_table = ITools->open("INFO_TABLE", {debug => $bv_debug });

my @condition;
push @condition,"TABLE_NAME = '$table_name'" if defined $table_name;
# only table with defined primary key
push @condition,"PRIMARY_KEY != ''";

$list_table->query_condition(join(' AND ',@condition));

my $sip = SIP->new($environnement);

if (not defined $list_table) {
	die "error opening INFO_TABLE";
}

# mise en mémoire des primary key
my %table_key;
my %table_fkey;
while (my %info_key = $list_table->fetch_row() ) {
	$table_key {$info_key{TABLE_NAME}} = $info_key{PRIMARY_KEY};
	$table_fkey {$info_key{TABLE_NAME}} = $info_key{F_TABLE};
}
$list_table->finish();


while (my %info = $list_table->fetch_row() ) {

	# CREATE DEF

	#if ( not $sip->exist_local_table($info{TABLE_NAME}, { debug => $bv_debug }) ) {
	#	warn "$info{TABLE_NAME} don't exist in local tables\n";
	#	next;
	#}
	# open DATA table
	my $ikos_data = $sip->open_ikos_table($info{TABLE_NAME}, { debug => $bv_debug });
	my @field_list=(@virtual_field,$ikos_data->field() );
	my $ikos_data_field = join($separator, @field_list);
	my $ikos_data_size = join($separator,('20s') x @field_list ) ;
	
	my $def_string = sprintf ($def_template,
			$info{TABLE_NAME},
			$separator,
			$ikos_data_field,
			$ikos_data_size, 
			$info{PRIMARY_KEY});
			
	if ($info{F_KEY} and $info{F_TABLE}) {
		$def_string .= sprintf($fkey_def_template,
			$info{F_KEY},
			$info{F_TABLE},
			$table_key{$info{F_TABLE}});
	}
	
	print "writing to $def_path\\$info{TABLE_NAME}.def\n";
	open (DEFFILE,">$def_path/$info{TABLE_NAME}.def") or die "error opening $def_path\\$info{TABLE_NAME}.def : $!";
	print DEFFILE $def_string."\n";
	close DEFFILE;


	# CREATE PCI
	
	my $pci_string = sprintf ($pci_template,
			($info{TABLE_NAME}) x 3);
	
	# get all table having current table as F_KEY
	my @fkey_list;
	for my $name (keys %table_fkey) {
		push @fkey_list, $name if $table_fkey{$name} and ($table_fkey{$name} eq $info{TABLE_NAME});
	}
	
	foreach (@fkey_list) {
		$pci_string .= sprintf($pci_fkey_template,$_,$_);
	}
	
	print "writing to $pci_path\\$info{TABLE_NAME}.pci\n";
	open (DEFFILE,">$pci_path/$info{TABLE_NAME}.pci") or die "error opening $pci_path\\$info{TABLE_NAME}.def : $!";
	print DEFFILE $pci_string."\n";
	close DEFFILE;
	
	# CREATE/update Label
	my $label_string = sprintf($label_template,$info{TABLE_NAME});
	print "Insert $info{TABLE_NAME} into ICleLabels\n";
	system('Insert -f into ICleLabels values',$label_string);
	
	# CREATE/update table INFO
	##TODO

	# CREATE/update table HISTO
	##TODO
}

system('AgentCollect -s AgentICleLabels ICleLabels');


=begin comment
# PCI racine
my $pci_string_module;
my @fkey_list;
	for my $name (keys %table_fkey) {
		push @fkey_list, $name if not $table_fkey{$name};
}
foreach (@fkey_list) {
		$pci_string_module .= sprintf($pci_fkey_template,$_,$_);
}
print "writing to $pci_path\\Modules.pci\n";
open (DEFFILE,">$pci_path/Modules.pci") or die "error opening $pci_path\\Modules.def : $!";
print DEFFILE $pci_string_module."\n";
close DEFFILE;