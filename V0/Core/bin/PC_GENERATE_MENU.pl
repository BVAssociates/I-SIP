#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

PC_GENERATE_MENU - les champs les champs d'une table Historique depuis la r�f�rence

=head1 SYNOPSIS

 PC_GENERATE_MENU.pl [-h] [-a | environnement [tablename]]
 
=head1 DESCRIPTION

Cr�er les PCI et DEF n�c�ssaires � la declaration d'une table dans SIP IKOS

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head2 -a : Genere tous les environnements

=head1 ARGUMENTS

=head2 * environnement � utiliser (utiliser -a pour tous)

=head2 * table a d�crire

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

sub write_file($$) {
	die "write_file prend 2 paramtres" if @_ != 2;
	my $filename=shift;
	my $string=shift;
	
	print "writing to $filename\n";
	open (FILE,">$filename") or die "error opening $filename : $!";
	print FILE $string."\n";
	close FILE;	
	
}

#  Traitement des Options
###########################################################


my %opts;
getopts('hva', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( (not $opts{a} and @ARGV < 1) or ($opts{a} and @ARGV != 0) ) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}
my $environnement_param=shift;
my $table_name=shift;

#  Corps du script
###########################################################

use IKOS::SIP;
use IKOS::DATA::ITools;


my $bv_debug=0;


my $separator=',';

my @virtual_field=("STATUS");

#####  BEGIN TEMPLATES ##### 

my $def_filename="%s/IKOS_TABLE_%s.def";
my $def_template = 'COMMAND="PC_LIST_TAB.pl %s %s %%DATE_HISTO%%"
SEP="%s"
FORMAT="%s"
SIZE="%s"
KEY="%s"
';
my $fkey_def_template='FKEY="[%s] on %s[%s]"
';

my $def_field_filename="%s/IKOS_FIELD_%s.def";
my $def_field_template = 'COMMAND="PC_LIST_FIELD.pl %s %s"
SEP="%%"
FORMAT="ID%%DATE_HISTO%%DATE_UPDATE%%USER_UPDATE%%TABLE_NAME%%TABLE_KEY%%FIELD_NAME%%FIELD_VALUE%%COMMENT%%TYPE%%STATUS"
SIZE="10n%%20s%%20s%%20s%%20s%%20s%%20s%%20s%%20s%%20s%%20s"
KEY="FIELD_NAME"

FKEY="[TYPE] on TYPE[Name]"
FKEY="[STATUS] on ETAT[Name]"
';

my $pci_filename="%s/IKOS_TABLE_%s.pci";
my $pci_template='Item~~Explore Champs~expl~~~Explore~IKOS_FIELD_%s~0~~Expand
#Item~~Afficher Ligne~expl~~~DisplayTable~IKOS_FIELD_%s~0~~Expand
Item~~Editer ligne~expl~~~Administrate~IKOS_FIELD_%s~0~~Expand
Item~Special~Valider la ligne~expl~~~ExecuteProcedure~PC_VALIDATE_LINE.pl %%Environnement%% %s~1~~Run
';
my $pci_fkey_template='Item~Tables li�es~%s~expl~~~Explore~%s~0~~Expand
';

my $pci_field_filename="%s/IKOS_FIELD_%s.pci";
my $pci_field_template='Item~~Historique~expl~~GSL_FILE=%s~DisplayTable~FIELD_HISTO~0~~Display';

my $label_item_template='IKOS_TABLE_%s.Item;line_%%[STATUS];';
my $label_table_template='IKOS_TABLE_%s.Table;line_%%[STATUS];Clefs';
my $label_field_item_template='IKOS_FIELD_%s.Item;field_%%[STATUS];';
my $label_field_table_template='IKOS_FIELD_%s.Table;;Liste des champs';

##### END TEMPLATES ##### 

# ENV verification

my $def_path=sprintf('%s\%s\_Services\%s\def',$ENV{CLES_HOME},$ENV{ICleName},$ENV{ServiceName});
die "$def_path not readable" if not -r $def_path;
die "$def_path not writable" if not -w $def_path;
die "$def_path not in BV_DEFPATH" if $ENV{BV_DEFPATH} !~ /\Q$def_path\E/;

my $pci_path=sprintf('%s\%s\_Services\%s\pci',$ENV{CLES_HOME},$ENV{ICleName},$ENV{ServiceName});
die "$pci_path not readable" if not -r $pci_path;
die "$pci_path not writable" if not -w $pci_path;
die "$pci_path not in BV_PCIPATH" if $ENV{BV_PCIPATH} !~ /\Q$pci_path\E/;

# ENVIRON table access
my $environ_table = ITools->open("ENVIRON", {debug => $bv_debug });
$environ_table->query_field("Environnement");
$environ_table->query_condition("Environnement = '$environnement_param'") if $environnement_param;

if (not defined $environ_table) {
	die "error opening ENVIRON";
}

# access to SIP
my $count=0;
while ( my ($environnement) = $environ_table->fetch_row_array() ) {
	$count++;
	my $sip = SIP->new($environnement);


	# INFO_TABLE access
	my $list_table = ITools->open("INFO_TABLE", {debug => $bv_debug });

	my @condition;
	push @condition,"TABLE_NAME = '$table_name'" if defined $table_name;
	# only table with defined primary key
	push @condition,"PRIMARY_KEY != ''";
	$list_table->query_condition(join(' AND ',@condition));

	if (not defined $list_table) {
		die "error opening INFO_TABLE";
	}

	# mise en m�moire des primary key
	my %table_key;
	my %table_fkey;
	while (my %info_key = $list_table->fetch_row() ) {
		$table_key {$info_key{TABLE_NAME}} = $info_key{PRIMARY_KEY};
		$table_fkey {$info_key{TABLE_NAME}} = $info_key{F_TABLE};
	}
	$list_table->finish();

	# we are ready, so begin with cleanup old files

	print "Cleaning old files\n";
	unlink <$def_path/IKOS_*.def>;
	unlink <$pci_path/IKOS_*.pci>;
	system('Delete from ICleLabels where NodeId = IKOS_*');

	while (my %info = $list_table->fetch_row() ) {

		#if ( not $sip->exist_local_table($info{TABLE_NAME}, { debug => $bv_debug }) ) {
		#	warn "$info{TABLE_NAME} don't exist in local tables\n";
		#	next;
		#}
		# open DATA table
		my $ikos_data = $sip->open_ikos_table($info{TABLE_NAME}, { debug => $bv_debug });
		my @field_list=(@virtual_field,$ikos_data->field() );
		my $ikos_data_field = join($separator, @field_list);
		my $ikos_data_size = join($separator,('20s') x @field_list ) ;
		
		my $ikos_data_table=$environnement."_".$info{TABLE_NAME};
		
		my $string;	
		my $filename;

	##### CREATE DEF
		
		$string = sprintf ($def_template,
				$environnement,
				$info{TABLE_NAME},
				$separator,
				$ikos_data_field,
				$ikos_data_size, 
				$info{PRIMARY_KEY});
				
		if ($info{F_KEY} and $info{F_TABLE}) {
			$string .= sprintf($fkey_def_template,
				$info{F_KEY},
				$info{F_TABLE},
				$table_key{$info{F_TABLE}});
		}
		
		$filename=sprintf($def_filename,$def_path,$ikos_data_table);
		
		write_file($filename,$string);


	##### CREATE DEF FIELD
		
		$string = sprintf ($def_field_template,$environnement,$info{TABLE_NAME});
					
		$filename=sprintf($def_field_filename,$def_path,$ikos_data_table);
		
		write_file($filename,$string);


	##### CREATE PCI
		
		$string = sprintf ($pci_template,
				($ikos_data_table) x 3,
				$info{TABLE_NAME});
		
		# get all table having current table as F_KEY
		my @fkey_list;
		for my $name (keys %table_fkey) {
			push @fkey_list, $name if $table_fkey{$name} and ($table_fkey{$name} eq $info{TABLE_NAME});
		}
		
		foreach (@fkey_list) {
			$string .= sprintf($pci_fkey_template,$_,$_);
		}
		
		$filename=sprintf($pci_filename,$pci_path,$ikos_data_table);
		
		write_file($filename,$string);
		
	##### CREATE PCI FIELD
		
		$string = sprintf ($pci_field_template,$info{TABLE_NAME});
		
		$filename=sprintf($pci_field_filename,$pci_path,$ikos_data_table);
		
		write_file($filename,$string);
		
	##### CREATE/update Label

		foreach ($label_item_template,$label_table_template,$label_field_item_template,$label_field_table_template) {
			my $label_string = sprintf($_,$ikos_data_table);
			print "Insert $ikos_data_table into ICleLabels\n";
			system('Insert -f into ICleLabels values',$label_string);
		}
		# CREATE/update table INFO
		##TODO

		# CREATE/update table HISTO
		##TODO
	}
}


system('AgentCollect -s AgentICleLabels ICleLabels');

print "$count environnements mis � jour\n";
