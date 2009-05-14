#!/usr/bin/perl
package pc_list_field_status;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';


#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_STATUS - Affiche les champs d'une ligne et y ajoute une colonne ICON

=head1 SYNOPSIS

 PC_LIST_FIELD_STATUS.pl [-r] [-a|-k clef] [-c environnement_source@date_source] environnement_cible table_name date_cible
 
=head1 DESCRIPTION

Affiche les champs d'une ligne et y ajoute une colonne ICON.

Par défaut, la colonne ICON contient l'état du commentaire.

Avec l'option -c, ICON contient 
la différence avec un autre environnement ou une autre date.
Dans ce mode, les informations affichés sont celles de la cible.
L'icone correspond à la différence de donnée de la source vers la cible.

=head1 ENVIRONNEMENT

=over

=item Environnement : Environnement en cours d'exploration

=item CLE=VALEUR : l'environnement doit contenir la valeur de la clef de la ligne à afficher

exemple : RDNPRCOD=VTS

=item DATE_EXPLORE : Date en cours d'exploration

=item ENV_COMPARE : Utilise cette valeur pour environnement_source si non spécifié

=item DATE_COMPARE : Utilise cette valeur pour date_source si non spécifié

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -r : Formatte la sortie pour un REPORT

=item -k clef : affiche les champs de la ligne correspondant à la clef

=item -a : affiche tous les champs de toutes les lignes

=item -c environnement_source@date_source : force le mode COMPARE

=back

=head1 ARGUMENTS

=over

=item environnement_cible : environnement la destination

=item table_name : table a afficher

=item date_cible : date utilisée pour la destination

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

sub run {
	local @ARGV=@_;
	
	#  Traitement des Options
	###########################################################

	#use Data::Dumper;
	#die Dumper(@ARGV);

	# quirk! because Windows leave "%VAR%" when VAR empty in args
	map {s/%\w+%//g} @ARGV;
	@ARGV=grep $_,@ARGV;

	my %opts;
	getopts('k:hvc:ar', \%opts);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};

	my $table_key_value=$opts{k};
	my $all_key=$opts{a};
	my $report_mode=$opts{r};

	#  Traitement des arguments
	###########################################################

	#recuperation de l'environnement
	my $env_compare=$ENV{ENV_COMPARE};
	my $date_compare=$ENV{DATE_COMPARE};
	my $date_explore=$ENV{DATE_EXPLORE};

	my $filter_field=$ENV{FILTER_FIELD};
	my $filter_value=$ENV{FILTER_VALUE};
	my $filter_exclude;

	if ($filter_value and $filter_value =~ /^!(.+)/) {
		$filter_value=$1;
		$filter_exclude=1;
	}

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

	log_info("Debut du programme : ".__FILE__." -c $env_compare\@$date_compare $environnement $table_name $date_explore");

	# deduction du contenu de la colonne ICON
	#  explore par defaut
	#  compare si une source est trouvée
	my $explore_mode="explore";
	$explore_mode="compare" if $env_compare or $date_compare;

	# pas de filtre en mode "explore" sur une date
	undef $filter_field if $date_explore;
	undef $filter_value if $date_explore;

	log_info("Mode d'exploration : $explore_mode");

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	use Isip::Environnement;
	use ITable::ITools;
	use Isip::ITable::FieldDiff;
	use Isip::IsipRulesDiff;

	# New SIP Object instance
	my $env_sip = Environnement->new($environnement, {debug => $debug_level});

	# recuperation de la clef primaine de la table
	my $table_key = $env_sip->get_table_key($table_name);

	if (not $table_key) {
		log_erreur("pas de clef primaine configurée pour la table $table_name");
		sortie(202);
	}

	my @table_key_list=split(',',$table_key);
	my @table_key_list_value;
	
	if (not $table_key_value and not $all_key) {
		log_info("recherche de la clef primaire $table_key dans l'environnement");
		foreach (@table_key_list) {
			if (exists $ENV{$_}) {
				push @table_key_list_value, $ENV{$_};
			}
			else {
				$logger->warning("Clef primaine <$_> n'est pas definie dans l'environnement");
				push @table_key_list_value, "";
			}
		}

		$table_key_value=join(',',@table_key_list_value);
		
		log_info("KEY= $table_key");
		log_info("KEY_VAL=$table_key_value");
	}

	# Check if it is an SQL filter
	my @comment_condition;
	if ($filter_field and $filter_field ne 'ICON' ) {

		$filter_value =~ s/\*/%/g;
		my $comp_operator;
		if ($filter_exclude) {
			$comp_operator='<>';
			$comp_operator='not like' if $filter_value =~ /%/;
		}
		else {
			$comp_operator='=' ;
			$comp_operator='like' if $filter_value =~ /%/;
		}
		
		# Check where is the clause
		if ($filter_field eq 'PROJECT') {
			push @comment_condition, "$filter_field $comp_operator '$filter_value'";
		}
	}

	# recupere à liste de champ à afficher
	use ITable::ITools;
	my $itools_table;
	if ($report_mode) {
		if ($explore_mode eq "compare") {
			$itools_table=ITools->open("FIELD_REPORT_COMPARE", {debug => $debug_level});
		}
		else {
			$itools_table=ITools->open("FIELD_REPORT_HISTO", {debug => $debug_level});
		}
	}
	else {
		$itools_table=ITools->open("IKOS_FIELD_".$environnement."_".$table_name, {debug => $debug_level});
	}
	my $separator=$itools_table->output_separator;
	my @query_field=$itools_table->field;
	

	# Create IsipRule object
	my $rules=IsipRules->new($table_name, $env_sip, {debug => $debug_level});

	my %memory_row;
	my @histo_table_field = $env_sip->get_table_field($table_name);

	if ($explore_mode eq "compare") {

		my $env_sip_from = Environnement->new($env_compare);
		my $env_sip_to = $env_sip;
		
		# open first table
		my $table_from = $env_sip_from->open_histo_field_table($table_name, $date_compare,{debug => $debug_level});
		if (not $table_from) {
			if ($date_compare) {
				log_info("$table_name n'existe pas à la date $date_compare dans $env_compare");
			}
			else {
				log_info("$table_name n'existe pas dans $env_compare");
			}
			return 1;
		}
		
		
		# open second table
		my $table_to = $env_sip_to->open_histo_field_table($table_name, $date_explore,{debug => $debug_level});
		if (not $table_to) {
			if ($date_explore) {
				log_info("$table_name n'existe pas à la date $date_explore dans $environnement");
			}
			else {
				log_info("$table_name n'existe pas dans $environnement");
			}
			return 1;
		}
		
		if ($table_key_value) {
			$table_from->query_key_value($table_key_value);
			$table_to->query_key_value($table_key_value);
		}
		else {
			if ($filter_field and $filter_field eq 'PROJECT') {
				my $project=$filter_value;
				
				$table_from->query_condition("PROJECT = '$project'");
				my %target_key_condition;
				
				while (my %row=$table_from->fetch_row) {
					$target_key_condition{$row{TABLE_KEY}}++;
				}
				
				if (%target_key_condition) {
					$table_from->query_key_value(keys %target_key_condition);
					$table_to->query_key_value(keys %target_key_condition);
					
					
				}
				else {
					$logger->info("Aucune ligne ne correspond au projet $project");
					return 0;
				}
			}
		}
		
		# open DataDiff table from two table
		my $table_status=FieldDiff->open($table_from, $table_to, {debug => $debug_level});
		
		# declare some additionnal blank fields
		# (ICON field will be computed into DataDiff)
		$table_status->dynamic_field("ICON","TYPE","TEXT","DOCUMENTATION",$table_status->dynamic_field);
		#$table_status->query_field(@query_field,"OLD_FIELD_VALUE","DIFF");
		$table_status->query_field(@query_field);
		
		# compute diff
		$table_status->compare_init();

		# Assign a IsipRules to compute ICON field
		my $diff_rules=IsipRulesDiff->new($table_name,$env_sip);
		$table_status->isip_rules($diff_rules);
		
		# put row in memory
		while (my %row=$table_status->fetch_row) {
			$row{TYPE}=$diff_rules->get_field_type_txt($row{FIELD_NAME}) if $table_status->has_fields("TYPE");
			$row{TEXT}=$diff_rules->get_field_description($row{FIELD_NAME}) if $table_status->has_fields("TEXT");
			
			my $display=1;
			# don't display filtered fields
			if ($filter_field and exists $row{$filter_field}) {
				if ($filter_exclude and $row{$filter_field} eq $filter_value) {
					$display=0;
				}
				elsif (not $filter_exclude and $row{$filter_field} ne $filter_value) {
					$display=0;
				}
			}
			
			# don't display ignored fields
			if ($row{TYPE} eq "exclus") {
				$display=0;
			}

			if ($display) {
				if ($all_key) {
					print join($separator,$table_status->hash_to_array(%row))."\n";
				}
				else {
					$memory_row{$row{FIELD_NAME}}= join($separator,$table_status->hash_to_array(%row))."\n";
				}
			}
		}
		
	}
	elsif ($explore_mode eq "explore") {

		# open histo table
		my $table_status = $env_sip->open_histo_field_table($table_name, $date_explore, {debug => $debug_level});
		
		$table_status->query_key_value($table_key_value) if $table_key_value;;
		$table_status->dynamic_field($table_status->dynamic_field,"DOCUMENTATION");
		$table_status->query_field(@query_field);
		
		$table_status->metadata_condition(@comment_condition);
		
		$table_status->output_separator('@');
		
		# put row in memory
		while (my %row=$table_status->fetch_row) {
		
			## decoding routine
			## commented because MEMO field has many lines
			#if ($row{MEMO}) {
			#	use IO::Uncompress::Gunzip qw(gunzip);
			#	use MIME::Base64;
			#	my $input=decode_base64($row{MEMO});
			#	my $output;
			#	gunzip(\$input=>\$output);
			#	#excel wait for <LF> only
			#	$output =~ s/\r//gm;
			#	$row{MEMO}=$output;
			#}
			
			if (not $date_explore) {
				# don't show hidden fields
				next if $rules->is_field_hidden(%row);
				
				# compute dynamic fields
				$row{ICON}=$rules->get_field_icon(%row) if exists $row{ICON};
			}
			else {
				$row{ICON}="none" if exists $row{ICON};
			}
					
			$row{TYPE}=$rules->get_field_type_txt($row{FIELD_NAME}) if $table_status->has_fields("TYPE");
			$row{TEXT}=$rules->get_field_description($row{FIELD_NAME}) if $table_status->has_fields("TEXT");
			
			if ($all_key) {
				if (grep {$_ eq $row{FIELD_NAME}} @histo_table_field) {
					print join($separator,$table_status->hash_to_array(%row))."\n";
				}
			}
			else {
				$memory_row{$row{FIELD_NAME}}= join($separator,$table_status->hash_to_array(%row))."\n";
			}
		}
	}



	# order the lines in the order of table field
	my @field_order=grep {$_} @memory_row{@histo_table_field};
	delete @memory_row{$env_sip->get_table_field($table_name)};
	for (keys %memory_row) {
		print $memory_row{$_};
	}
	for (@field_order) {
		print;
	}
	
	$logger->info("Fin du programme ".__FILE__);
}

exit !run(@ARGV) if !caller;

1;