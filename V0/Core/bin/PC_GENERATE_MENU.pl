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

 PC_GENERATE_MENU.pl [-h] [-v] [-c] environnement [tablename]
 
=head1 DESCRIPTION

Créer les PCI et DEF nécéssaires à la declaration d'une table dans SIP IKOS

=head1 ENVIRONNEMENT

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=head1 OPTIONS

=head2 -h : Affiche l'aide en ligne

=head2 -v : Mode verbeux

=head2 -c : decrit uniquement les colonnes utiles des tables (key, label)

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
getopts('hvc', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

my $only_used_fields = 1 if $opts{c};

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


my $separator='@';

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
SEP="@"
FORMAT="ID@DATE_HISTO@DATE_UPDATE@USER_UPDATE@TABLE_NAME@TABLE_KEY@FIELD_NAME@FIELD_VALUE@COMMENT@STATUS"
SIZE="10n@20s@20s@20s@20s@20s@20s@20s@20s@20s"
KEY="FIELD_NAME"

FKEY="[STATUS] on ETAT[Name]"
';

my $pci_filename="%s/IKOS_TABLE_%s.pci";
my $pci_template='Item~~Explore Champs~expl~~~Explore~IKOS_FIELD_%s~0~~Expand
#Item~~Afficher Ligne~expl~~~DisplayTable~IKOS_FIELD_%s~0~~Expand
Item~~Editer ligne~expl~~~Administrate~IKOS_FIELD_%s~0~~Expand
Item~Special~Valider la ligne~expl~~~ExecuteProcedure~PC_VALIDATE_LINE.pl %%Environnement%% %s~1~~Run
';
my $pci_fkey_template='Item~Tables liées~%s~expl~~~Explore~%s~0~~Expand
';

my $pci_field_filename="%s/IKOS_FIELD_%s.pci";
my $pci_field_template='Item~~Historique~expl~~GSL_FILE=%s~DisplayTable~FIELD_HISTO~0~~Display';

my $label_table_template='IKOS_TABLE_%s.Table;key_go;Clefs de %s (%s)';
my $label_item_template='IKOS_TABLE_%s.Item;line_%%[STATUS];%s %s';
my $label_field_table_template='IKOS_FIELD_%s.Table;page_white_key;Liste des champs';
my $label_field_item_template='IKOS_FIELD_%s.Item;field_%%[STATUS];';

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

# INFO_TABLE verification

# quirk because INFO_TABLE use %Environnement%
$ENV{Environnement}=$environnement;
my $list_table = ITools->open("INFO_TABLE", {debug => $bv_debug });

my @condition;
push @condition,"TABLE_NAME = '$table_name'" if defined $table_name;
push @condition,"Active = 1";
# only table with defined primary key
push @condition,"PRIMARY_KEY != ''";
$list_table->query_condition(join(' AND ',@condition));

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

# access to SIP data
my $sip = SIP->new($environnement);

# we are ready, so begin with cleanup old files


if ($table_name) {
	print "Cleaning old labels for $table_name\n";
	system('Delete from ICleLabels where NodeId ~ IKOS_TABLE_$table_name_');
	system('Delete from ICleLabels where NodeId ~ IKOS_FIELD_$table_name_');
}
else {
	print "Cleaning all old labels\n";
	system('Delete from ICleLabels where NodeId ~ IKOS_');
}
while (my %info = $list_table->fetch_row() ) {

	#if ( not $sip->exist_local_table($info{TABLE_NAME}, { debug => $bv_debug }) ) {
	#	warn "$info{TABLE_NAME} don't exist in local tables\n";
	#	next;
	#}
	# open DATA table
	my $ikos_data = $sip->open_ikos_table($info{TABLE_NAME}, { debug => $bv_debug });
	my @field_list=(@virtual_field,$ikos_data->field() );
	
	my $ikos_data_table=$environnement."_".$info{TABLE_NAME};
	
	my $string;	
	my $filename;

##### CREATE DEF
	
	if ($only_used_fields) {
		# reset field list
		@field_list=(@virtual_field);
		#add only used fields
		push @field_list,split(',',$info{PRIMARY_KEY});
		push @field_list,$info{LIBELLE_KEY} if $info{LIBELLE_KEY};
		push @field_list,split(',',$info{F_KEY});
	}
	
	my $ikos_data_size = join($separator,('20s') x @field_list ) ;
	my $ikos_data_field = join($separator, @field_list);
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
			"IKOS_TABLE_$environnement\_".$info{F_TABLE},
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
		$string .= sprintf($pci_fkey_template,$_,"IKOS_TABLE_$environnement\_$_");
	}
	
	$filename=sprintf($pci_filename,$pci_path,$ikos_data_table);
	
	write_file($filename,$string);
	
##### CREATE PCI FIELD
	
	$string = sprintf ($pci_field_template,$info{TABLE_NAME});
	
	$filename=sprintf($pci_field_filename,$pci_path,$ikos_data_table);
	
	write_file($filename,$string);
	
##### CREATE/update Label

	foreach ($label_table_template,
				$label_field_item_template,
				$label_field_table_template)
	{
		my $label_desc="";
		$label_desc= "(".$info{Description}.")" if $info{Description};
		my $label_string = sprintf($_,$ikos_data_table,$info{TABLE_NAME},$info{Description});
		print "Insert $ikos_data_table into ICleLabels\n";
		system('Insert -f into ICleLabels values',$label_string);
	}
	
	#specifique
	my $label_desc=" ";
	$label_desc= "(%[".$info{LIBELLE_KEY}."])" if $info{LIBELLE_KEY};
	my $label_string = sprintf($label_item_template,$ikos_data_table,"%[".$info{PRIMARY_KEY}."]",$label_desc);
	print "Insert $ikos_data_table into ICleLabels\n";
	system('Insert -f into ICleLabels values',$label_string);
	
	# CREATE/update table INFO
	##TODO

	# CREATE/update table HISTO
	##TODO
}

system('AgentCollect -s AgentICleLabels ICleLabels');

