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
	
	$logger->info("writing to",$filename);
	open (FILE,">$filename") or log_erreur("error opening $filename :",$!);
	print FILE $string."\n";
	close FILE;	
	
}

#  Traitement des Options
###########################################################


my %opts;
getopts('hvc', \%opts) or usage(0);

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

my $bv_severite=0;
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
FORMAT="ID@DATE_HISTO@DATE_UPDATE@USER_UPDATE@TABLE_NAME@TABLE_KEY@FIELD_NAME@FIELD_VALUE@COMMENT@STATUS@ICON@TYPE@TEXT@DOCUMENTATION"
SIZE="10n@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s"
KEY="ID"

FKEY="[STATUS] on ETAT[Name]"
';

my $pci_filename="%s/IKOS_TABLE_%s.pci";
my $pci_template='Item~~Explore Champs~expl~~~Explore~IKOS_FIELD_%s~0~~Expand
#Item~Special~Valider la ligne~expl~~~ExecuteProcedure~PC_VALIDATE_LINE.pl %%Environnement%% %s~1~~Run
';
my $pci_fkey_template='Item~Tables liées~%s~expl~~~Explore~%s~0~~Expand
';

my $pci_field_filename="%s/IKOS_FIELD_%s.pci";
my $pci_field_template='Item~~Historique Complet~expl~~GSL_FILE=%s~DisplayTable~FIELD_HISTO@DATE_HISTO,FIELD_VALUE,STATUS,COMMENT~0~~Display
Item~~Editer Commentaire~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE}"~~IsipProcessor~FORM_CONFIG~0~~Configure
Item~~Afficher Difference~expl~perl -e "exit 1 if not exists $ENV{ENV_COMPARE} and not exists $ENV{DATE_COMPARE}"~~DisplayTable~FIELD_DIFF@ENVIRONNEMENT,DATE_HISTO,FIELD_NAME,FIELD_VALUE,TYPE,COMMENT,STATUS,TEXT~0~~Configure
';

my $label_table_template='IKOS_TABLE_%s.Table;key_go;Clefs de %s (%s)';
my $label_item_template='IKOS_TABLE_%s.Item;isip_%%[ICON];%s %s';
my $label_field_table_template='IKOS_FIELD_%s.Table;page_white_key;Liste des champs';
my $label_field_item_template='IKOS_FIELD_%s.Item;isip_%%[ICON];%%[FIELD_NAME] (%%[TEXT])';

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


# access to SIP data
log_info("opening environnement",$environnement);
my $env = Environnement->new($environnement);

# get table info
my %table_info_all=$env->get_table_info();
my $link_obj=$env->get_links();

# we are ready, so begin with cleanup old files


if ($table_name) {
	$logger->info("Cleaning old labels for", $table_name);
	system('Delete from ICleLabels where NodeId ~ IKOS_TABLE_$table_name_$environnement');
	system('Delete from ICleLabels where NodeId ~ IKOS_FIELD_$table_name_$environnement');
}
else {
	$logger->info("Cleaning all old labels");
	system("Delete from ICleLabels where NodeId ~ IKOS_$environnement");
}



my @list_table;
if (not $table_name) {
	@list_table=keys %table_info_all;
} else {
	@list_table=($table_name);
}


foreach my $current_table (@list_table) {

	my %table_info=%{ $table_info_all{$current_table} };

	#if ( not $env->exist_local_table($current_table, { debug => $bv_debug }) ) {
	#	warn "$current_table don't exist in local tables\n";
	#	next;
	#}
	# open DATA table
	log_info("Traitement de ",$current_table);
	my $source_data = eval { $env->open_source_table($current_table, { debug => $bv_debug }) };
	if ($@) {
		$bv_severite=202;
		next;
	}
	
	my @field_list=(@virtual_field,$source_data->field() );
	
	my $source_data_table=$environnement."_".$current_table;
	
	my $string;	
	my $filename;

##### CREATE DEF
	
	if ($only_used_fields) {
		# reset field list
		@field_list=(@virtual_field);
		#add only used fields
		push @field_list,split(',',$table_info{key});
		push @field_list,$table_info{label_field} if $table_info{label_field};
		push @field_list,split(',',$link_obj->get_parent_fields());
		die "TODO"
	}
	
	my $source_data_size = join($separator,('20s') x @field_list ) ;
	my $source_data_field = join($separator, @field_list);
	$string = sprintf ($def_template,
			$environnement,
			$current_table,
			$separator,
			$source_data_field,
			$source_data_size, 
			$table_info{key});
		
	foreach my $f_table ($link_obj->get_parent_tables($current_table)) {
		$string .= sprintf($fkey_def_template,
			join(',',$link_obj->get_child_fields($current_table,$f_table)) ,
			"IKOS_TABLE_$environnement\_".$f_table,
			join(',',$link_obj->get_parent_fields($current_table,$f_table)) ,   );
	}
	
	$filename=sprintf($def_filename,$def_path,$source_data_table);
	
	write_file($filename,$string);


##### CREATE DEF FIELD
	
	$string = sprintf ($def_field_template,$environnement,$current_table);
				
	$filename=sprintf($def_field_filename,$def_path,$source_data_table);
	
	write_file($filename,$string);


##### CREATE PCI
	
	$string = sprintf ($pci_template,
			($source_data_table) x 3,
			$current_table);
	
	# get all table having current table as F_KEY
	my @child_table=$link_obj->get_child_tables($current_table);

	foreach (@child_table) {
		$string .= sprintf($pci_fkey_template,"$_ ($table_info_all{$_}{description})","IKOS_TABLE_$environnement\_$_") if @child_table;
	}

	
	$filename=sprintf($pci_filename,$pci_path,$source_data_table);
	
	write_file($filename,$string);
	
##### CREATE PCI FIELD
	
	$string = sprintf ($pci_field_template,$current_table);
	
	$filename=sprintf($pci_field_filename,$pci_path,$source_data_table);
	
	write_file($filename,$string);
	
##### CREATE/update Label

	foreach ($label_table_template,
				$label_field_item_template,
				$label_field_table_template)
	{
		my $label_desc="";
		$label_desc= "(".$table_info{description}.")" if $table_info{description};
		my $label_string = sprintf($_,$source_data_table,$current_table,$table_info{description});
		$logger->info("Insert $source_data_table into ICleLabels");
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
	$label_desc= "(%[".$table_info{label_field}."])" if $table_info{label_field};
	my @pkey_list=split(',',$table_info{key});
	map {$_='%['.$_.']'} @pkey_list;
	my $label_string = sprintf($label_item_template,$source_data_table,join(',',@pkey_list),$label_desc);
	$logger->info("Insert $source_data_table into ICleLabels");
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

log_info("Prise en compte des descriptions sur le portail");
system('AgentCollect -s PortalICleLabels ICleLabels');

sortie($bv_severite);