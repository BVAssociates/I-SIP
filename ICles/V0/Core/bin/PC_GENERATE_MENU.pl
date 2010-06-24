#!/usr/bin/env perl
package pc_generate_menu;

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

 PC_GENERATE_MENU.pl [-h] [-v] [-c] [-m module] (-a | environnement) table_name
 
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

=item -m module : utilise les tables du module si table_name n'est pas spécifié

=back

=head1 ARGUMENTS

=over

=item -a : Tous les environnements

=item environnement : environnement à utiliser

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

sub find_file($$) {
	
	my $path_list = shift;
	my $filename = shift;
	
	use Config;
	my $env_separator = $Config{path_sep};
	
	
	foreach my $path (split ($env_separator,$path_list)) {
		my $filepath = $path;
		$filepath .= "/".$filename if $filename;
		return $filepath if -r $filepath;
	}
	
	return undef;
}

sub get_source_field ($$) {
	my $env_obj=shift;
	my $current_table=shift;
	
	return $env_obj->get_table_field($current_table);
}

sub get_def_table_string($$) {

	my $env_obj=shift;
	my $table_name=shift;
	my $query_date=shift;
	
	my $environnement=$env_obj->{environnement};
	my $display_table=$env_obj->get_display_table($table_name);
	
	my $columns_ref=$env_obj->get_columns($display_table,$query_date);
	
	my @keys=$columns_ref->get_key_list();
	my @field=$columns_ref->get_field_list();

	my $no_icon_option="";
	$no_icon_option="-n" if $display_table ne $table_name;
	
	return if not @field;
	
	my $separator='@';
	my @virtual_field=("ICON","PROJECT");
	
	my $string;

	my $def_template = 'COMMAND="PC_LIST_STATUS [% option %] -c %ENV_COMPARE%@%DATE_COMPARE% [% environnement %] [% table %] %DATE_EXPLORE%"
SEP="[% separator %]"
FORMAT="[% format %]"
SIZE="[% size %]"
KEY="[% keys %]"
SORT="[% sort %]"
';
	my $fkey_def_template='#FKEY="[[% fkeys %]] on [% foreign_table %][[% foreign_field %]]"
';

	# fill standards value of DEF
	my @field_list=(@virtual_field,@field);
	my $format = join($separator, @field_list);
	my $keys = join($separator, @keys);
	
	my @size_list;
	foreach my $field (@field_list) {
		my $size_tmp=$columns_ref->get_size($field);
		
		if (not defined $size_tmp) {
			$size_tmp='20s' ;
		}
		elsif ($size_tmp =~ /^(?:DECIMAL|INTEGER)\((\d+)\)$/) {
			$size_tmp=$1.'n' ;
		}
		elsif ($size_tmp =~ /\((\d+)\)$/) {
			$size_tmp=$1.'s' ;
		}
		else {
			$size_tmp='20s' ;
		}
		push @size_list, $size_tmp
	}

	my $size = join($separator, @size_list ) ;
	my $sort=join($separator,$columns_ref->get_key_list());
	
	$string .= $def_template;
	
	$string =~ s/\[% environnement %\]/$environnement/g;
	$string =~ s/\[% table %\]/$table_name/g;
	$string =~ s/\[% separator %\]/$separator/g;
	$string =~ s/\[% format %\]/$format/g;
	$string =~ s/\[% size %\]/$size/g;
	$string =~ s/\[% keys %\]/$keys/g;
	$string =~ s/\[% sort %\]/$sort/g;
	$string =~ s/\[% option %\]/$no_icon_option/g;
	
	return $string;
}

sub get_def_field_string($$) {
	
	my $env_obj=shift;
	my $table_name=shift;
	my $display_table=$env_obj->get_display_table($table_name);
	
	my $def_field_template = 'COMMAND="PC_LIST_FIELD_STATUS [% environnement %] [% table %] %DATE_EXPLORE%"
SEP="@"
FORMAT="ID@DATE_HISTO@DATE_UPDATE@USER_UPDATE@TABLE_NAME@TABLE_KEY@FIELD_NAME@FIELD_VALUE@COMMENT@STATUS@ICON@TYPE@TEXT@PROJECT@MEMO"
SIZE="10n@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s@20s"
KEY="ID"

FKEY="[STATUS] on ETAT[Name]"
FKEY="[PROJECT] on PROJECT_INFO[PROJECT_NAME]"
';

	my $environnement=$env_obj->{environnement};
	
	my $string=$def_field_template;
	$string =~ s/\[% environnement %\]/$environnement/g;
	$string =~ s/\[% table %\]/$display_table/g;
	
	return $string;
}
sub get_pci_table_string {

	my $env_obj=shift;
	my $link_obj=shift;
	my $table_name=shift or croak("usage: get_pci_table_string(env_obj,table_name)");
	my $display_table=$env_obj->get_display_table($table_name);
		
	# TEMPLATES
	my $pci_template='Table~~Explore~user~~~Explore~~0~~Expand
';
	
	if ($display_table eq $table_name) {
		$pci_template.='Item~~Détailler les champs~user~perl -S HAS_FIELD_MENU~~Explore~IKOS_FIELD_[% environnement %]_[% table %]~0~~Expand
Item~Surveillance~Etre alerter par courriel~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE}"~{GROUP_ALERT=getListValue("Choisir un groupe de destinataire des courriels",MY_GROUPS)}~ExecuteProcedure~PC_SET_MAIL -a "[% environnement %]" "[% table %]" "%GROUP_ALERT%"~0~~Configure
';
	}
	my $pci_template_root='Item~Groupe~Déplacer dans un groupe existant~expl~~NEW_CATEGORY=getListValue("modifier groupe",CATEGORY)~ExecuteProcedure~PC_SET_CATEGORY [% environnement %] [% table %] [% key_var %] "%NEW_CATEGORY%"~0~~Configure
Item~Groupe~Déplacer dans un nouveau groupe~expl~~NEW_CATEGORY=getValue("Nouveaux groupe")~ExecuteProcedure~PC_SET_CATEGORY [% environnement %] [% table %] [% key_var %] "%NEW_CATEGORY%"~0~~Configure
';
	my $pci_template_ignore='Item~Surveillance~Ignorer l\'état cette clef et de ses sous-tables~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE} or $ENV{ICON} eq "valide_label""~~ExecuteProcedure~ME_EXEC_JOB PC_SET_LABEL [% environnement %] [% table %] [% key_var %] OK~1~~Configure
Item~Surveillance~Ne plus ignorer l\état de cette clef et de ses sous-tables~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE} or $ENV{ICON} ne "valide_label""~~ExecuteProcedure~ME_EXEC_JOB PC_SET_LABEL [% environnement %] [% table %] [% key_var %]~1~~Configure
';
	my $pci_template_validate='Item~Validation~Valider cette ligne et ses sous-tables~expl~~{VALIDATE_PROJECT=getListValue("Projet associé",PROJECT_INFO)}{VALIDATE_COMMENT=getValue("Commentaire de validation")}~ExecuteProcedure~PC_VALIDATE_LINE -r -p "%VALIDATE_PROJECT%" [% environnement %] [% table %] %VALIDATE_COMMENT%~1~~Configure
';
	#my $pci_fkey_template='Item~~[% child_table %]~expl~~~Explore~[% table_list %]~0~~Expand
	my $pci_fkey_template='Item~~Explorer sous-tables~user~~~Explore~[% table_list %]~0~~Expand
';

	# TEMPLATE ASSEMBLY
	my $string = $pci_template;
	
	if ($env_obj->is_root_table($table_name)) {
		$string .= $pci_template_root;
	}
	if ($env_obj->can_table_ignore($table_name)) {
		$string .= $pci_template_ignore;
	}
	
	$string .= $pci_template_validate;
	
	
	# get all table having current table as F_KEY
	my @child_table=$link_obj->get_child_tables($table_name);
	
	$string .= $pci_fkey_template if @child_table;
	
	# TEMPLATE REPLACEMENT

	my $environnement=$env_obj->{environnement};
	my $key_var=join(',',map {'%'.$_.'%'} $env_obj->get_table_key($display_table));
	my $child_table=join (',',map { s/.+_([^_]+)$/$1/ } @child_table);
	my $table_list=join (',',map {"IKOS_TABLE_$environnement\_$_"} @child_table);
	
	$string =~ s/\[% environnement %\]/$environnement/g;
	$string =~ s/\[% table %\]/$display_table/g;
	$string =~ s/\[% key_var %\]/$key_var/g;
	$string =~ s/\[% child_table %\]/$child_table/g;
	$string =~ s/\[% table_list %\]/$table_list/g;
	
	return $string;
}

sub get_pci_field_string($$) {

	my $env_obj=shift;
	my $table_name=shift;
	my $display_table=$env_obj->get_display_table($table_name);
	
	my $pci_field_template='Table~~Champs~user~~~Explore~~0~~Expand
Table~~Information ligne complète~user~~{FILTER_ICON=}{FILTER_PROJECT=}~DisplayTable~~0~~Display
Item~~Historique Complet~user~~GSL_FILE=[% table %]~DisplayTable~FIELD_HISTO@DATE_HISTO,FIELD_VALUE,PROJECT,STATUS,COMMENT,BASELINE_TXT~0~~Display
Item~~Editer Commentaire~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE} or $ENV{ICON} eq "stamp""~~IsipProcessorLine~FORM_CONFIG~0~~Configure
Item~~Afficher Difference~expl~perl -e "exit 1 if not exists $ENV{ENV_COMPARE} and not exists $ENV{DATE_COMPARE}"~~DisplayTable~FIELD_DIFF@ENVIRONNEMENT,DATE_HISTO,FIELD_NAME,FIELD_VALUE,TYPE,COMMENT,STATUS,TEXT~0~~Configure

#Item~Surveillance~Ne plus surveiller~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE} or $ENV{ICON} eq "valide_label""~~ExecuteProcedure~PC_SET_LABEL -f "%FIELD_NAME%" "[% environnement %]" "[% table %]" "%TABLE_KEY%" OK~0~~Configure
#Item~Surveillance~Surveiller à nouveau~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE} or $ENV{ICON} ne "valide_label""~~ExecuteProcedure~PC_SET_LABEL -f "%FIELD_NAME%" "[% environnement %]" "[% table %]" "%TABLE_KEY%"~0~~Configure
#Item~Surveillance~Etre alerter par Email~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE}"~{GROUP_ALERT=getListValue("Choisir un groupe de destinataire",MY_GROUPS)}~ExecuteProcedure~PC_SET_MAIL -a -f "%FIELD_NAME%" -k "%TABLE_KEY%" "[% environnement %]" "[% table %]" "%GROUP_ALERT%"~0~~Configure
';

	my $environnement=$env_obj->{environnement};
	
	my $string = $pci_field_template;
	$string =~ s/\[% environnement %\]/$environnement/g;
	$string =~ s/\[% table %\]/$display_table/g;
	
	return $string;
}

sub get_label_table_hash($$) {

	my $env_obj=shift;
	my $table_name=shift;
	my $display_table=$env_obj->get_display_table($table_name);
	
	my $environnement=$env_obj->{environnement};

	my $label_table_name='IKOS_TABLE_[% environnement %]_[% table %].Table';
	my $label_table_icon='isip_table_open';
	my $label_table_desc='Clefs de [% table %] [% description %]';
	
	my $label_desc=$env_obj->get_table_description($display_table);
	$label_desc= "(".$label_desc.")" if $label_desc;
	
	$label_table_name =~ s/\[% table %\]/$table_name/g;
	$label_table_name =~ s/\[% environnement %\]/$environnement/g;
	
	$label_table_desc =~ s/\[% table %\]/$display_table/g;
	$label_table_desc =~ s/\[% description %\]/$label_desc/g;
	
	return (NodeId => $label_table_name,
		Icon => $label_table_icon,
		Label => $label_table_desc);
}

sub get_label_table_item_hash {
	
	my $env_obj=shift;
	my $link_obj=shift;
	my $table_name=shift or croak("usage: get_label_table_item_hash(env_obj,link_obj,table_name)");
	my $display_table=$env_obj->get_display_table($table_name);
	
	my @pkey_list=$env_obj->get_table_key($display_table);
	my $environnement=$env_obj->{environnement};

	my %table_info=$env_obj->get_table_info($display_table);
	my $label_field=$table_info{label_field};
	
	my $label_table_name='IKOS_TABLE_[% environnement %]_[% table %].Item';
	my $label_table_icon='isip_%[ICON]';
	my $label_table_desc='[% keys %] [% description %]';
	
	my $label_desc=" ";
	$label_desc= "(%[".$label_field."])" if $label_field;
	
	
	# suppression de l'affichage des clefs primaire qui sont des clef etrangere d'autres tables
	my @parent_tables=$link_obj->get_parent_tables($display_table);
	my %fkey_seen;
	foreach my $parent (@parent_tables) {
		my %foreign_key=$link_obj->get_foreign_fields($display_table,$parent);
		foreach (keys %foreign_key) {
			$fkey_seen{$_}++;
		}
	}
	
	my @new_pkey_list;
	foreach my $pkey (@pkey_list) {
		push @new_pkey_list, $pkey if not exists $fkey_seen{$pkey} ;
	}
	
	map {$_='%['.$_.']'} @new_pkey_list;
	my $keys=join(',',@new_pkey_list);
	
	
	$label_table_name =~ s/\[% table %\]/$table_name/g;
	$label_table_name =~ s/\[% environnement %\]/$environnement/g;
	
	$label_table_desc =~ s/\[% keys %\]/$keys/g;
	$label_table_desc =~ s/\[% description %\]/$label_desc/g;
	
	return (NodeId => $label_table_name,
		Icon => $label_table_icon,
		Label => $label_table_desc);
}

sub get_label_field_hash($$) {

	my $env_obj=shift;
	my $table_name=shift;
	my $display_table=$env_obj->get_display_table($table_name);
	
	my $environnement=$env_obj->{environnement};

	my $label_table_name='IKOS_FIELD_[% environnement %]_[% table %].Table';
	my $label_table_icon='isip_table_key';
	my $label_table_desc='Liste des champs';
		
	$label_table_name =~ s/\[% table %\]/$display_table/g;
	$label_table_name =~ s/\[% environnement %\]/$environnement/g;
	
	
	return (NodeId => $label_table_name,
		Icon => $label_table_icon,
		Label => $label_table_desc);
}

sub get_label_field_item_hash($$) {

	my $env_obj=shift;
	
	my $table_name=shift;
	my $environnement=$env_obj->{environnement};
	my $display_table=$env_obj->get_display_table($table_name);

	my $label_table_name='IKOS_FIELD_[% environnement %]_[% table %].Item';
	my $label_table_icon='isip_%[ICON]';
	my $label_table_desc='%[FIELD_NAME] (%[TEXT])';
	
	$label_table_name =~ s/\[% table %\]/$table_name/g;
	$label_table_name =~ s/\[% environnement %\]/$environnement/g;
	
	return (NodeId => $label_table_name,
		Icon => $label_table_icon,
		Label => $label_table_desc);
}

#  Traitement des Options
###########################################################

sub run {
	local @ARGV=@_;

	my %opts;
	getopts('hvcam:', \%opts) or usage(0);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};

	my $only_used_fields = 1 if $opts{c};
	
	my $module=$opts{m};

	#  Traitement des arguments
	###########################################################

	if ( not exists $opts{a} and @ARGV < 1 ) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}
	my $env_arg=shift @ARGV if not exists $opts{a};
	my $table_name=shift @ARGV;
	
	
	my @environnement_list=($env_arg) if $env_arg;

	if (@environnement_list and $opts{a}) {
		log_info("Option -a incompatible");
		usage($debug_level);
		sortie(202);
	}

	if (not (@environnement_list or $opts{a})) {
		log_info("You must specify environnement or -a");
		usage($debug_level);
		sortie(202);
	}

#  Corps du script
###########################################################

	use Isip::IsipConfig;
	use Isip::Environnement;
	use ITable::ITools;

	my $bv_severite=0;
	my $bv_debug=0;

	#####  BEGIN FILENAME TEMPLATES ##### 

my $def_filename="%s/IKOS_TABLE_%s.def";

my $def_field_filename="%s/IKOS_FIELD_%s.def";

my $pci_filename="%s/IKOS_TABLE_%s.pci";

my $pci_field_filename="%s/IKOS_FIELD_%s.pci";


	##### BEGIN CLEANUP ##### 

	my $labels=ITools->open("ICleLabels");
	
	if ($opts{a}) {
		$logger->notice("Purge des labels");
		system(qq{Delete from ICleLabels});
		
		# insère les icones fixes
		
		my @header = split(/\n/,
'PortalAccess.Item;I_Home;Portail (%[Role])
ETAT.Table;;Etats disponibles
ETAT.Item;field_%[Name];%[Name]
ENVIRON.Item;I_Group;%[Environnement] (%[Description])
CONF_ENVIRON.Item;isip_environ_conf;%[Environnement] (%[Description])
MODULE.Item;I_AgentICles;%[Module] (%[Description])
CONF_MODULE.Item;isip_module_conf;%[Module] (%[Description])
ROOT_TABLE.Item;isip_table_%[TYPE_SOURCE]_%[ICON];%[TABLE_NAME] (%[DESCRIPTION])
TABLE_INFO.Item;isip_table_conf_%[ICON];%[TABLE_NAME] (%[DESCRIPTION])
DATE_HISTO.Item;isip_date;%[DATE_EXPLORE] %[DESCRIPTION]
DATE_HISTO_BASELINE.Item;isip_date;%[DATE_EXPLORE] (%[DESCRIPTION])
FILTER.Item;I_Transitions;%[FILTER_DESC]
FILTER_COMPARE.Item;I_Transitions;%[FILTER_DESC]
CATEGORY.Item;isip_%[ICON];'
			);
		
		foreach my $header_line ( @header ) {
		
			my %line=$labels->array_to_hash( split(/;/ , $header_line, -1) );
			$labels->insert_row_pp(%line);
		}
	}

	#find first def path
	my $def_path=find_file($ENV{BV_DEFPATH},'');
	die "$def_path not readable" if not -r $def_path;
	die "$def_path not writable" if not -w $def_path;

	#find first pci path
	my $pci_path=find_file($ENV{BV_PCIPATH},'');
	die "$pci_path not readable" if not -r $pci_path;
	die "$pci_path not writable" if not -w $pci_path;

	##### BEGIN GETTING INFO ##### 

	my $config=IsipConfig->new();

	if (not @environnement_list) {
		@environnement_list=$config->get_environnement_list();
	}
	
	
	##### BEGIN CREATE FILE #####


	foreach my $environnement (@environnement_list) {

		# access to SIP data
		log_info("opening environnement",$environnement);
		my $env = Environnement->new($environnement);
		
		# update links cache
		$env->update_links_cache();
		
		log_info("Get tables informations");
		# get table info
		my $link_obj=$env->get_links_menu();
		
		# first, compute which table will be process	
		my @list_table;
		if (not $table_name) {
			if ($module) {
				# @list_table= module's tables + their parents
				my %list_table_uniq;
				foreach my $table ($env->get_table_list_module($module)) {
					$list_table_uniq{$table}++;
					foreach my $parent ($link_obj->get_parent_tables($table,1)) {
						$list_table_uniq{$parent}++;
					}
				}
				@list_table=keys %list_table_uniq;
			}
			else {
				# @list_table= all tables + their parents
				my %list_table_uniq;
				foreach my $table ($env->get_table_list()) {
					$list_table_uniq{$table}++;
					foreach my $parent ($link_obj->get_parent_tables($table,1)) {
						$list_table_uniq{$parent}++;
					}
				}
				@list_table=keys %list_table_uniq;
			}
		} else {
			@list_table=($table_name,$link_obj->get_parent_tables($table_name,1));
		}
		
		
		foreach my $current_table (sort @list_table) {
		
			# check if table is a menu table (virtual)
			my $display_table=$current_table;
			$current_table =$env->get_display_table($display_table);
			
			my %table_info=$env->get_table_info($current_table);
			log_erreur("Impossible d'obtenir des informations sur $current_table") if not %table_info;
			
			my $table_key=$env->get_table_key($current_table);
			
			if (not $table_key) {
				log_erreur("Aucune PRIMARY_KEY n'est définie pour la table $current_table (environnement $environnement). Vous devez les definir dans le menu \"Configurer Colonnes\"");
			}

			#if ( not $env->exist_local_table($current_table, { debug => $bv_debug }) ) {
			#	warn "$current_table don't exist in local tables\n";
			#	next;
			#}
			# open DATA table
			log_info("Mise à jour des menus de ",$display_table);
			
			
			my $source_data_table=$environnement."_".$display_table;
			
			my $string;	
			my $filename;

		##### CREATE DEF
			
			$string=get_def_table_string($env,$display_table);
			
			next if not $string;
			
			$filename=sprintf($def_filename,$def_path,$source_data_table);
			
			write_file($filename,$string);


		##### CREATE DEF FIELD
			
			$string = get_def_field_string($env,$display_table);
						
			$filename=sprintf($def_field_filename,$def_path,$source_data_table);
			
			write_file($filename,$string);


		##### CREATE PCI
			
			
			$string = get_pci_table_string($env,$link_obj,$display_table);
			
			$filename=sprintf($pci_filename,$pci_path,$source_data_table);
			
			write_file($filename,$string);
			
		##### CREATE PCI FIELD
			
			$string = get_pci_field_string($env,$display_table);
			
			$filename=sprintf($pci_field_filename,$pci_path,"$environnement\_$current_table");
			
			write_file($filename,$string);
			
		##### CREATE/update Label
			
			my %label_hash;
			
			%label_hash=get_label_table_hash($env,$display_table);
			$labels->insert_row_pp(%label_hash);
			
			%label_hash=get_label_table_item_hash($env,$link_obj,$display_table);
			$labels->insert_row_pp(%label_hash);
			
			%label_hash=get_label_field_hash($env,$display_table);
			$labels->insert_row_pp(%label_hash);
			
			%label_hash=get_label_field_item_hash($env,$display_table);
			$labels->insert_row_pp(%label_hash);
		}

	}

	log_info("Prise en compte des descriptions sur le portail");
	my @collect_messages=`AgentCollect -s PortalICleLabels ICleLabels`;
	log_info($_) foreach @collect_messages;
	
	return 0;
}

exit run(@ARGV) if !caller;

1;
