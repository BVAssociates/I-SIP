#!/usr/bin/perl
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

sub get_display_table($) {
	my $current_table=shift;

	$current_table =~ s/.+__(.+)$/$1/;
	
	return $current_table;
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
	my $display_table=get_display_table($table_name);
	
	my $columns_ref=$env_obj->get_columns($display_table,$query_date);
	
	my $keys=$columns_ref->get_key_list();
	my @field=$columns_ref->get_field_list();

	my $no_icon_option="";
	$no_icon_option="-n" if $display_table ne $table_name;
	
	return if not @field;
	
	my $separator='@';
	my @virtual_field=("ICON","PROJECT");
	
	my $string;

	my $def_template = 'COMMAND="PC_LIST_STATUS.pl [% option %] -c %ENV_COMPARE%@%DATE_COMPARE% [% environnement %] [% table %] %DATE_EXPLORE%"
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
	$string =~ s/\[% table %\]/$display_table/g;
	$string =~ s/\[% separator %\]/$separator/g;
	$string =~ s/\[% format %\]/$format/g;
	$string =~ s/\[% size %\]/$size/g;
	$string =~ s/\[% keys %\]/$keys/g;
	$string =~ s/\[% sort %\]/$sort/g;
	$string =~ s/\[% option %\]/$no_icon_option/g;
	
=begin comment
	# add one FKEY entry per foreign tables
	my $link_obj=$env_obj->get_links();
	foreach my $f_table ($link_obj->get_parent_tables($display_table)) {
	
		my %foreign_field=$link_obj->get_foreign_fields($display_table,$f_table);
		my @sorted_keys=sort keys %foreign_field;
		
		# create FKEY entry
		my $fkeys=join(',', @sorted_keys);
		my $foreign_table="IKOS_TABLE_$environnement\_".$f_table;
		my $foreign_field=join(',', @foreign_field{@sorted_keys} );
		
		my $string_fkey = $fkey_def_template;
		$string_fkey =~ s/\[% fkeys %\]/$fkeys/g;
		$string_fkey =~ s/\[% foreign_table %\]/$foreign_table/g;
		$string_fkey =~ s/\[% foreign_field %\]/$foreign_field/g;
		
		$string .= $string_fkey;
	}
=cut
	
	return $string;
	
}
sub get_def_field_string($$) {
	
	my $env_obj=shift;
	my $table_name=shift;
	my $display_table=get_display_table($table_name);
	
	my $def_field_template = 'COMMAND="PC_LIST_FIELD_STATUS.pl [% environnement %] [% table %] %DATE_EXPLORE%"
SEP="@"
FORMAT="ID@DATE_HISTO@DATE_UPDATE@USER_UPDATE@TABLE_NAME@TABLE_KEY@FIELD_NAME@FIELD_VALUE@COMMENT@STATUS@ICON@TYPE@TEXT@MEMO@PROJECT"
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
sub get_pci_table_string($$) {

	my $env_obj=shift;
	my $table_name=shift;
	my $display_table=get_display_table($table_name);
		
	# TEMPLATES
	my $pci_template='Table~~Explore~expl~~~Explore~~0~~Expand
';
	
	if ($display_table eq $table_name) {
		$pci_template.='Item~Ligne~Explorer les champs~expl~~~Explore~IKOS_FIELD_[% environnement %]_[% table %]~0~~Expand
';
	}
	my $pci_template_root='Item~Groupe~Déplacer dans un groupe existant~adm~~NEW_CATEGORY=getListValue("modifier groupe",CATEGORY)~ExecuteProcedure~PC_SET_CATEGORY [% environnement %] [% table %] [% key_var %] "%NEW_CATEGORY%"~0~~Configure
Item~Groupe~Déplacer dans un nouveau groupe~adm~~NEW_CATEGORY=getValue("Nouveaux groupe")~ExecuteProcedure~PC_SET_CATEGORY [% environnement %] [% table %] [% key_var %] "%NEW_CATEGORY%"~0~~Configure
';
	#my $pci_fkey_template='Item~~[% child_table %]~expl~~~Explore~[% table_list %]~0~~Expand
	my $pci_fkey_template='Item~~Explorer sous-tables~expl~~~Explore~[% table_list %]~0~~Expand
';
	
	# TEMPLATE ASSEMBLY
	my $string = $pci_template;
	
	if ($env_obj->is_root_table($table_name)) {
		$string .= $pci_template_root;
	}
	
	# get all table having current table as F_KEY
	my $link_obj=$env_obj->get_links_menu();
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
	my $display_table=get_display_table($table_name);
	
	my $pci_field_template='Table~~Explore~expl~~~Explore~~0~~Expand
Item~~Historique Complet~expl~~GSL_FILE=[% table %]~DisplayTable~FIELD_HISTO@DATE_HISTO,FIELD_VALUE,PROJECT,STATUS,COMMENT,BASELINE_TXT~0~~Display
Item~~Editer Commentaire~expl~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE} or $ENV{ICON} eq "stamp""~~IsipProcessorLine~FORM_CONFIG~0~~Configure
Item~~Afficher Difference~expl~perl -e "exit 1 if not exists $ENV{ENV_COMPARE} and not exists $ENV{DATE_COMPARE}"~~DisplayTable~FIELD_DIFF@ENVIRONNEMENT,DATE_HISTO,FIELD_NAME,FIELD_VALUE,TYPE,COMMENT,STATUS,TEXT~0~~Configure

Item~Surveillance~Ne plus surveiller~adm~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE} or $ENV{ICON} eq "valide_label"~~ExecuteProcedure~PC_SET_LABEL [% environnement %] [% table %] %TABLE_KEY% %FIELD_NAME% OK~0~~Configure
Item~Surveillance~Surveiller à nouveau~adm~perl -e "exit 1 if exists $ENV{ENV_COMPARE} or exists $ENV{DATE_COMPARE} or exists $ENV{DATE_EXPLORE} or $ENV{ICON} ne "valide_label""~~ExecuteProcedure~PC_SET_LABEL [% environnement %] [% table %] %TABLE_KEY% %FIELD_NAME%~0~~Configure
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
	my $display_table=get_display_table($table_name);
	
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

sub get_label_table_item_hash($$) {
	
	my $env_obj=shift;
	my $table_name=shift;
	my $display_table=get_display_table($table_name);
	
	my @pkey_list=$env_obj->get_table_key($display_table);
	my $environnement=$env_obj->{environnement};

	my %table_info=$env_obj->get_table_info($display_table);
	my $label_field=$table_info{label_field};
	
	my $link_obj=$env_obj->get_links();

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
	my $display_table=get_display_table($table_name);
	
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
	my $display_table=get_display_table($table_name);

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
		
		
		
		

		if (@list_table) {
			foreach (@list_table) {
				$logger->info("Cleaning old labels for", $_);
				system('Delete from ICleLabels where NodeId ~ IKOS_TABLE_'.$_.'_'.$environnement);
				system('Delete from ICleLabels where NodeId ~ IKOS_FIELD_'.$_.'_'.$environnement);
			}
		}
		else {
			$logger->info("Cleaning all old labels");
			system("Delete from ICleLabels where NodeId ~ IKOS_$environnement");
		}

		foreach my $current_table (sort @list_table) {
		
			# check if table is a menu table (virtual)
			my $display_table=$current_table;
			$current_table =get_display_table($display_table);
			
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
			
			
			$string = get_pci_table_string($env,$display_table);
			
			$filename=sprintf($pci_filename,$pci_path,$source_data_table);
			
			write_file($filename,$string);
			
		##### CREATE PCI FIELD
			
			$string = get_pci_field_string($env,$display_table);
			
			$filename=sprintf($pci_field_filename,$pci_path,"$environnement\_$current_table");
			
			write_file($filename,$string);
			
		##### CREATE/update Label

			my $labels=ITools->open("ICleLabels");
			my %label_hash;
			
			%label_hash=get_label_table_hash($env,$display_table);
			$labels->insert_row(%label_hash);
			
			%label_hash=get_label_table_item_hash($env,$display_table);
			$labels->insert_row(%label_hash);
			
			%label_hash=get_label_field_hash($env,$display_table);
			$labels->insert_row(%label_hash);
			
			%label_hash=get_label_field_item_hash($env,$display_table);
			$labels->insert_row(%label_hash);			
		}

	}

	log_info("Prise en compte des descriptions sur le portail");
	my @collect_messages=`AgentCollect -s PortalICleLabels ICleLabels`;
	log_info($_) foreach @collect_messages;
	
	return 0;
}

exit run(@ARGV) if !caller;

1;