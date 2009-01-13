#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_GENERATE_MENU - les champs les champs d'une table Historique depuis la référence

=head1 SYNOPSIS

 PC_GENERATE_MENU.pl [-h] [-v] [-c] environnement [tablename]
 
=head1 DESCRIPTION

Créer les PCI et DEF nécéssaires à la declaration d'une table dans SIP IKOS

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -c : decrit uniquement les colonnes utiles des tables (key, label)

=back

=head1 ARGUMENTS

=over

=item environnement : environnement à utiliser

=item tablename : table a décrire

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

sub log_erreur {
	#print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	$logger->error(@_);
	sortie(202);
}

sub log_info {
	#print STDERR "INFO: ".join(" ",@_)."\n"; 
	$logger->notice(@_);
}

sub write_file($$) {
	log_erreur("write_file prend 2 paramètres") if @_ != 2;
	my $filename=shift;
	my $string=shift;
	
	log_info("writing to",$filename);
	open (FILE,">$filename") or log_erreur("error opening $filename :",$!);
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

use Isip::Environnement;
use ITable::ITools;


my $bv_debug=0;


my $separator='@';

my @virtual_field=("ICON");

#####  BEGIN TEMPLATES ##### 

my $def_filename="%s/IKOS_TABLE_%s.def";
my $def_template = 'COMMAND="PC_LIST_STATUS.pl -c %%ENV_COMPARE%%@%%DATE_COMPARE%% %s %s %%DATE_EXPLORE%%"
SEP="%s"
FORMAT="%s"
SIZE="%s"
KEY="%s"
';
my $fkey_def_template='FKEY="[%s] on %s[%s]"
';

my $def_field_filename="%s/IKOS_FIELD_%s.def";
my $def_field_template = 'COMMAND="PC_LIST_FIELD_STATUS.pl %s %s %%DATE_EXPLORE%%"
SEP="@"
FORMAT="ID@DATE_HISTO@DATE_UPDATE@USER_UPDATE@TABLE_NAME@TABLE_KEY@FIELD_NAME@FIELD_VALUE@COMMENT@STATUS@ICON@TYPE@TEXT"
SIZE="10n@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s"
KEY="FIELD_NAME"

FKEY="[STATUS] on ETAT[Name]"
';

my $pci_filename="%s/IKOS_TABLE_%s.pci";
my $pci_template='Item~~Explore Champs~expl~~~Explore~IKOS_FIELD_%s~0~~Expand
#Item~~Afficher Ligne~expl~~~DisplayTable~IKOS_FIELD_%s~0~~Expand
#Item~~Editer Commentaire~expl~~~Administrate~IKOS_FIELD_%s~0~~Expand
#Item~Special~Valider la ligne~expl~~~ExecuteProcedure~PC_VALIDATE_LINE.pl %%Environnement%% %s~1~~Run
';
my $pci_fkey_template='Item~Tables liées~Explorer~expl~~~Explore~%s~0~~Expand
';

my $pci_field_filename="%s/IKOS_FIELD_%s.pci";
my $pci_field_template='Item~~Historique Complet~expl~~GSL_FILE=%s~DisplayTable~FIELD_HISTO@DATE_HISTO,FIELD_VALUE,STATUS,COMMENT~0~~Display
Item~~Editer Commentaire~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE}"~~IsipProcessor~~0~~Configure
Item~~Afficher Difference~expl~perl -e "exit 1 if not exists $ENV{ENV_COMPARE} and not exists $ENV{DATE_COMPARE}"~~DisplayTable~FIELD_DIFF@ENVIRONNEMENT,DATE_HISTO,FIELD_NAME,FIELD_VALUE,TYPE,COMMENT,STATUS,TEXT~0~~Configure
';

my $label_table_template='IKOS_TABLE_%s.Table;key_go;Clefs de %s (%s)';
my $label_item_template='IKOS_TABLE_%s.Item;line_%%[ICON];%s %s';
my $label_field_table_template='IKOS_FIELD_%s.Table;page_white_key;Liste des champs';
my $label_field_item_template='IKOS_FIELD_%s.Item;field_%%[ICON];%%[FIELD_NAME] (%%[TEXT])';

##### END TEMPLATES ##### 

# ENV verification

my $def_path=sprintf('%s\%s\_Services\def',$ENV{CLES_HOME},"ISIP");
die "$def_path not readable" if not -r $def_path;
die "$def_path not writable" if not -w $def_path;
die "$def_path not in BV_DEFPATH" if $ENV{BV_DEFPATH} !~ /\Q$def_path\E/;

my $pci_path=sprintf('%s\%s\_Services\pci',$ENV{CLES_HOME},"ISIP");
die "$pci_path not readable" if not -r $pci_path;
die "$pci_path not writable" if not -w $pci_path;
die "$pci_path not in BV_PCIPATH" if $ENV{BV_PCIPATH} !~ /\Q$pci_path\E/;

# INFO_TABLE verification

# quirk because INFO_TABLE use %Environnement%
$ENV{Environnement}=$environnement;
my $list_table = ITools->open("INFO_TABLE", {debug => $bv_debug });

if (not defined $list_table) {
	die "error opening INFO_TABLE";
}

# mise en mémoire des primary/foreign key
my %table_key;
my %table_fkey;
my %table_info;
while (my %info_key = $list_table->fetch_row() ) {
	$table_key {$info_key{TABLE_NAME}} = $info_key{PRIMARY_KEY};
	$table_fkey {$info_key{TABLE_NAME}} = $info_key{F_TABLE};
	$table_info {$info_key{TABLE_NAME}} = $info_key{Description};
}
$list_table->finish();

# access to SIP data
log_info("opening environnement",$environnement);
my $sip = Environnement->new($environnement);

# we are ready, so begin with cleanup old files


if ($table_name) {
	log_info("Cleaning old labels for", $table_name);
	system('Delete from ICleLabels where NodeId ~ IKOS_TABLE_$table_name_$environnement');
	system('Delete from ICleLabels where NodeId ~ IKOS_FIELD_$table_name_$environnement');
}
else {
	log_info("Cleaning all old labels");
	system("Delete from ICleLabels where NodeId ~ IKOS_$environnement");
}

my @condition;
push @condition,"TABLE_NAME = '$table_name'" if defined $table_name;
push @condition,"Active = 1";
# only table with defined primary key
push @condition,"PRIMARY_KEY != ''";
$list_table->query_condition(join(' AND ',@condition));

while (my %info = $list_table->fetch_row() ) {

	#if ( not $sip->exist_local_table($info{TABLE_NAME}, { debug => $bv_debug }) ) {
	#	warn "$info{TABLE_NAME} don't exist in local tables\n";
	#	next;
	#}
	# open DATA table
	log_info("Get information from",$info{TABLE_NAME});
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
		push @fkey_list, "IKOS_TABLE_$environnement\_".$name if $table_fkey{$name} and ($table_fkey{$name} eq $info{TABLE_NAME});
	}

	$string .= sprintf($pci_fkey_template,join(',',@fkey_list)) if @fkey_list;

	
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
		log_info("Insert $ikos_data_table into ICleLabels");
		system('Insert -f into ICleLabels values',$label_string);
		if ($? == -1) {
			die "failed to execute: $!\n";
		}
		elsif (($? >> 8) != 0) {
			die sprintf ("'Insert' died with signal %d, %s",($?  >> 8))
		};
	}
	
	#specifique
	my $label_desc=" ";
	$label_desc= "(%[".$info{LIBELLE_KEY}."])" if $info{LIBELLE_KEY};
	my @pkey_list=split(',',$info{PRIMARY_KEY});
	map {$_='%['.$_.']'} @pkey_list;
	my $label_string = sprintf($label_item_template,$ikos_data_table,join(',',@pkey_list),$label_desc);
	log_info("Insert $ikos_data_table into ICleLabels");
	system('Insert -f into ICleLabels values',$label_string);
	if ($? == -1) {
		log_erreur("failed to execute:",$!);
	}
	elsif (($? >> 8) != 0) {
		log_erreur(sprintf ("'Insert' died with signal %d, %s",($?  >> 8)));
	};
	
	# CREATE/update table INFO
	##TODO

	# CREATE/update table HISTO
	##TODO
}

log_info("Collecte des données sur le portail");
system('AgentCollect -s PortalICleLabels ICleLabels');
