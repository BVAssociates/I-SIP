#!/usr/bin/env perl
package pc_list_status;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_STATUS - Affiche une table et y ajoute une colonne de ICON

=head1 SYNOPSIS

 PC_LIST_STATUS.pl [-c environnement_source@date_source] environnement_cible table_name [date_cible]

=head1 DESCRIPTION

Affiche une table et y ajoute une colonne ICON.

Par d�faut, la colonne ICON contient l'�tat du commentaire.

Avec l'option -c, ICON contient 
la diff�rence avec un autre environnement ou une autre date.

=head1 ENVIRONNEMENT

=over 4

=item Environnement : Environnement en cours d'exploration

=item DATE_EXPLORE : Date en cours d'exploration

=item ENV_COMPARE : Utilise cette valeur pour environnement_source si non sp�cifi�

=item DATE_COMPARE : Utilise cette valeur pour date_source si non sp�cifi�

=item FILTER_FIELD : exclus de l'affichage un type d'icone (format "[table.]champ")

=item FILTER_VALUE : valeur � exclure

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -c environnement_source@date_source : force le mode COMPARE

=item -x icone : n'affiche pas les lignes contenant "icone"

=back

=head1 ARGUMENTS

=over

=item environnement_cible : environnement de destination

=item table_name : table a afficher

=item date_cible : date d'exploration de la table cible

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	my $exit_value=shift;
	$logger->notice("Sortie du programme, code $exit_value");
	exit $exit_value;
}

sub usage($) {
	my $verbosity=shift;
	pod2usage(-verbose => $verbosity, -noperldoc => 1, -exitvat => "NOEXIT");
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


sub run {
	local @ARGV=@_;
	
	#  Traitement des Options
	###########################################################

	#use Data::Dumper;
	#die Dumper(@ARGV);

	# quirk! because Windows leave "%VAR%" when VAR empty in args
	map {s/%\w+%//g} @ARGV;
	@ARGV=grep $_,@ARGV;
	
	log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

	my %opts;
	getopts('hvc:n', \%opts) or usage(0);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};

	my $no_icon=$opts{n} if $opts{n};

	#  Traitement des arguments
	###########################################################

	#recuperation de l'environnement
	my $env_compare=$ENV{ENV_COMPARE};
	my $date_compare=$ENV{DATE_COMPARE};
	my $date_explore=$ENV{DATE_EXPLORE};

	
	if ( @ARGV < 2 ) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
		sortie(202);
	}

	my $environnement=shift @ARGV;
	my $table_name=shift @ARGV;
	my $temp_date_explore=shift @ARGV;
	$date_explore=$temp_date_explore if $temp_date_explore;

	if (exists $opts{c}) {
		if ($opts{c} =~ /@/) {
			($env_compare,$date_compare) = split(/@/,$opts{c});
		}
		else {
			$env_compare=$opts{c};
		}
	}

	$env_compare=$environnement if $date_compare and not $env_compare;

	# deduction du contenu de la colonne ICON
	#  explore par defaut
	#  compare si une source est trouv�e
	my $explore_mode="explore";
	$explore_mode="compare" if $env_compare or $date_compare;

	log_info("mode $explore_mode activ�");

	# on d�duit la "ROOT_TABLE"
	# $ENV{TableName}=fichier de definition
	# $ENV{TABLE_NAME}=table ROOT
	my $table_source;
	$table_source=$1 if $ENV{TableName} and $ENV{TableName} =~ /$environnement\_(.+)__/;

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::Environnement;
	use ITable::ITools;
	use Isip::ITable::DataDiff;
	use Isip::Cache::CacheStatus;
	use Isip::Cache::CacheProject;
	use Isip::Cache::CacheTempo;
	use Isip::IsipFilter;
	
	use Isip::IsipRulesDiff;

	my $env_sip = Environnement->new($environnement);

	# recuperation de la structure des liens
	my $links = $env_sip->get_links();

	my @query_condition;
	my @comment_condition;
	
	# creation d'un object IsipFilter
	my $filter=IsipFilter->new();
	# add conditions from ENV
	#push @query_condition, $filter->get_query_condition();
	
	# on essaye de retrouver toutes les clefs etrangere dans l'environnement
	# on ne poss�de pas l'information de l'arbre d'exploration, donc on cherche
	#	les clefs etrang�res de toutes les tables parentes
	##DEBUG
	#$ENV{RDNPRCOD}='AFF' ; $bv_severite=202;
	#$ENV{FLCDTRAIT}='ACH750' ; $bv_severite=202;
	#$ENV{FLTYPTRAIT}='17' ; $bv_severite=202;
	#$filter_field='FHCDTRAIT' ; $bv_severite=202;
	#$filter_value='ACH%' ; $bv_severite=202;
	##DEBUG

	# table qui sera affich�e
	my $table_explore;
	my $dirty_cache;
	my $tempo_cache;
	
	
	# ouverture de la table en cours d'exploration
	my $table_current=$env_sip->open_local_from_histo_table($table_name,$date_explore);
	if (not $table_current) {
		log_info("aucune table � afficher");
		return 1;
	}
	
	foreach my $parent_table ($links->get_parent_tables($table_name) ) {
		my %foreign_fields=$links->get_foreign_fields($table_name,$parent_table);
		
		foreach my $foreign_field (keys %foreign_fields) {
			my $var=$foreign_fields{$foreign_field};
			if ( exists $ENV{$var} ) {
				$logger->info("Clef etrang�re pour filtrage : $foreign_field = '$ENV{$var}'");
				push @query_condition, "$foreign_field = ".$table_current->quote($ENV{$var});
			}
		}
	}

	my $project_cache;
	if (my $filter_value=$filter->get_field_value("PROJECT")) {
		# load Cache to know project of child lines
		$project_cache=CacheProject->new($env_sip);
		$project_cache->load_cache($table_name);
		$project_cache->set_dirty_project($filter_value);
		
	}
	
	$table_current->query_condition(@query_condition);

	# recuperation des colonnes � afficher
	my @query_field=("ICON","PROJECT",$env_sip->get_table_field($table_name,$date_explore));

	if ($explore_mode eq "compare") {
		my $env_sip_from = Environnement->new($env_compare);
		
		#open IKOS table for DATA
		my $table_from=$env_sip_from->open_local_from_histo_table($table_name,$date_compare);

		if (not $table_from and $table_current) {
			use ITable::Null;
			$table_from=Null->open($table_name);
			$table_from->field($table_current->field);
			$table_from->query_field($table_current->query_field);
			$table_from->key($table_current->key);
			$table_from->dynamic_field($table_current->dynamic_field);
		}
		elsif ($table_from and not $table_current) {
			use ITable::Null;
			$table_current=Null->open($table_name);
			$table_current->field($table_from->field);
			$table_current->query_field($table_from->query_field);
			$table_current->key($table_from->key);
			$table_current->dynamic_field($table_from->dynamic_field);
		}
		elsif (not $table_from and not $table_current) {
			return 1;
		}
		
		$table_from->query_condition(@query_condition);

		$table_from->query_field("PROJECT",$table_from->query_field);
		$table_current->query_field("PROJECT",$table_current->query_field);
		
		$table_explore=DataDiff->open($table_from, $table_current, {debug => $debug_level});

		$table_explore->compare_exclude("PROJECT");
		$table_explore->compare_init();
		
		my $diff_rules = IsipRulesDiff->new($table_name, $env_sip, {debug => $debug_level});
		$table_explore->isip_rules($diff_rules);

	}
	elsif ($explore_mode eq "explore") {
		$table_explore=$table_current;
		
		$table_current->query_field("PROJECT",$table_current->query_field);
		
		if (not $date_explore) {
			log_info("pr�-charge les informations de modification des sous-tables");
			# load Cache to know state of child lines
			$dirty_cache=CacheStatus->new($env_sip);
			$dirty_cache->load_cache($table_name);
			
			# load Tempo Cache
			$tempo_cache=CacheTempo->new($env_sip);
			$tempo_cache->load_cache($table_name);
			
			# load Rules to know state of current lines
			my $type_rules = IsipRules->new($table_name, $env_sip, {debug => $debug_level});
			$table_explore->isip_rules($type_rules) if not $no_icon;
		}
		
			
		# special optimization
		my $icon_filter=$filter->get_field_value("ICON");
		if ($icon_filter and ($icon_filter eq '!valide' or $icon_filter eq '!valide,!stamp,!none')) {
			# load Cache to know project of child lines
			$table_explore->query_key_value($dirty_cache->get_dirty_key($table_name));
		}
		
		if ($ENV{CATEGORY}) {

			my $category=$ENV{CATEGORY};
		
			#quirk! bad encoding in ENV by I-SIS
			use Encode;
			$category=encode("cp850",$category) if $^O eq 'MSWin32';
			
			my %info=$env_sip->get_table_info($table_name);
			if ($info{root_table} and $ENV{TABLE_NAME} and $ENV{TABLE_NAME} eq $table_name) {
				log_info("affiche les lignes de la categorie $category");
				$table_explore->query_condition("CATEGORY = ".$table_explore->quote($category),$table_explore->query_condition());
			}
		}
	}

	# champs � afficher
	$table_explore->query_field(@query_field);

	$table_explore->output_separator('@');

	my @keys=$table_explore->key;
	while (my %row=$table_explore->fetch_row) {
		my $string_key=join(',',@row{@keys});
		
		# force PROJECT if a child has this PROJECT
		if ($project_cache
			and $project_cache->is_dirty_key($table_name, $string_key))
		{
			$row{PROJECT}=$filter->get_field_value("PROJECT");
		}
		
		# append _dirty to get special icon
		if ($dirty_cache and $dirty_cache->is_dirty_key($table_name, $string_key, $table_source)) {
			$row{ICON}=$row{ICON}."_dirty";
		}
		elsif ($tempo_cache and $tempo_cache->is_dirty_key($table_name, $string_key, $table_source)) {
			$row{ICON}=$row{ICON}."_tempochild";
		}
		
		if ($filter->is_display_line(%row)) {
			print join($table_explore->output_separator,@row{@query_field})."\n"
		}
	}
	
	return 1;
}


exit !run(@ARGV) if !caller;
1;
