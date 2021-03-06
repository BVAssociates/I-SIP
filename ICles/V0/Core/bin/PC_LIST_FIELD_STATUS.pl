#!/usr/bin/env perl
package pc_list_field_status;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Encode;

use Isip::IsipLog '$logger';
use ITable::ITools;
use ITable::Null;
use Isip::Environnement;
use Isip::ITable::FieldDiff;
use Isip::IsipRulesDiff;
use Isip::IsipFilter;


#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_STATUS - Affiche les champs d'une ligne et y ajoute une colonne ICON

=head1 SYNOPSIS

 PC_LIST_FIELD_STATUS.pl [-r] [-s separateur] [-a [-x] |-k clef] [-c environnement_source@date_source] environnement_cible table_name date_cible
 
=head1 DESCRIPTION

Affiche les champs d'une ligne et y ajoute une colonne ICON.

Par d�faut, la colonne ICON contient l'�tat du commentaire.

Avec l'option -c, ICON contient 
la diff�rence avec un autre environnement ou une autre date.
Dans ce mode, les informations affich�s sont celles de la cible.
L'icone correspond � la diff�rence de donn�e de la source vers la cible.

=head1 ENVIRONNEMENT

=over

=item Environnement : Environnement en cours d'exploration

=item CLE=VALEUR : l'environnement doit contenir la valeur de la clef de la ligne � afficher

exemple : RDNPRCOD=VTS

=item DATE_EXPLORE : Date en cours d'exploration

=item ENV_COMPARE : Utilise cette valeur pour environnement_source si non sp�cifi�

=item DATE_COMPARE : Utilise cette valeur pour date_source si non sp�cifi�

=item ITOOLS : L'environnement du service de l'ICles IKOS doit �tre charg�

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -s separateur : force le separateur de champ

=item -r : Formatte la sortie pour un REPORT

=item -k clef : affiche les champs de la ligne correspondant � la clef

=item -a : affiche tous les champs de toutes les lignes

=item -x : divise la clef primaire multi-colonnes en champs distincts

=item -c environnement_source@date_source : force le mode COMPARE

=back

=head1 ARGUMENTS

=over

=item environnement_cible : environnement la destination

=item table_name : table a afficher

=item date_cible : date utilis�e pour la destination

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

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

sub decode_memo {
	my $memo=shift or die("usage : decode_memo(memo)");
	
	# decoding routine
	# commented because MEMO field has many lines
	use IO::Uncompress::Gunzip qw(gunzip);
	use MIME::Base64;
	my $input=decode_base64($memo);
	my $output;
	gunzip(\$input=>\$output);
	#excel wait for <LF> only
	##FIXME: don't work on win32,
	## because PerlIO convert \n into \r\n when writing to the file
	$output =~ s/\r//g;
	
	$output =~ s/^/"/;
	$output =~ s/$/"/;
	
	return $output;
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
	getopts('s:k:hvc:arx', \%opts) or usage(1);

	my $debug_level = 0;
	$debug_level = 1 if $opts{v};

	usage($debug_level+1) if $opts{h};

	my $table_key_value=$opts{k};
	my $all_key=$opts{a};
	my $report_mode=$opts{r};
	my $explode_key=$opts{x};
	
	if ( $explode_key and ! $all_key ) {
		usage($debug_level);
	}
	
	my $separator=$opts{s} if $opts{s};

	#  Traitement des arguments
	###########################################################

	#recuperation de l'environnement
	my $env_compare=$ENV{ENV_COMPARE};
	my $date_compare=$ENV{DATE_COMPARE};
	my $date_explore=$ENV{DATE_EXPLORE};

	if ( @ARGV < 2 ) {
		log_info("Nombre d'argument incorrect (".@ARGV.")");
		usage($debug_level);
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

	my $log_command=" -c $env_compare" if $env_compare;
	$log_command .="\@$date_compare" if $date_compare;
	$log_command .=" $environnement $table_name";
	$log_command .=" $date_explore" if $date_explore;
	log_info("Debut du programme : ".__FILE__.$log_command);

	# deduction du contenu de la colonne ICON
	#  explore par defaut
	#  compare si une source est trouv�e
	my $explore_mode="explore";
	$explore_mode="compare" if $env_compare or $date_compare;

	log_info("Mode d'exploration : $explore_mode");

	#  Corps du script
	###########################################################
	my $bv_severite=0;

	# New SIP Object instance
	my $env_sip = Environnement->new($environnement, {debug => $debug_level});

	# IsipFilter instance
	my $filter=IsipFilter->new( {no_decode => 1} );
	
	# recuperation de la clef primaine de la table
	my $table_key = $env_sip->get_table_key($table_name);

	if (not $table_key) {
		log_erreur("pas de clef primaine configur�e pour la table $table_name");
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

	# recupere � liste de champ � afficher
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
	$separator=$itools_table->output_separator if not $separator;
	my @query_field=$itools_table->field;

	# Create IsipRule object
	my $rules=IsipRules->new($table_name, $env_sip, {debug => $debug_level});

	my %memory_row;
	my @histo_table_field = $env_sip->get_table_field($table_name);

	if ($explore_mode eq "compare") {

		my $env_sip_from = Environnement->new($env_compare);
		my $env_sip_to = $env_sip;
		
		# open first table
		my $table_from = eval { $env_sip_from->open_histo_field_table($table_name, $date_compare,{debug => $debug_level}) };

		my $table_to = eval { $env_sip_to->open_histo_field_table($table_name, $date_explore,{debug => $debug_level}) };
		
		# simulate empty table
		#  (ie. if table is missing in an Env)
		if (not $table_from and $table_to) {
			$table_from=Null->open($table_name);
			$table_from->field($table_to->field);
			$table_from->query_field($table_to->query_field);
			$table_from->key($table_to->key);
			$table_from->dynamic_field($table_to->dynamic_field);
		}
		elsif ($table_from and not $table_to) {
			$table_to=Null->open($table_name);
			$table_to->field($table_from->field);
			$table_to->query_field($table_from->query_field);
			$table_to->key($table_from->key);
			$table_to->dynamic_field($table_from->dynamic_field);
		}
		elsif (not $table_from and not $table_to) {
			return 1;
		}
		
		if (not $table_from) {
			if ($date_compare) {
				log_info("$table_name n'existe pas � la date $date_compare dans $env_compare");
			}
			else {
				log_info("$table_name n'existe pas dans $env_compare");
			}
			return 1;
		}
		
		
		# open second table
		if (not $table_to) {
			if ($date_explore) {
				log_info("$table_name n'existe pas � la date $date_explore dans $environnement");
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
			# pre-select list of keys based on condition (not ICON field)
			if (my @pre_condition = grep { !/^ICON/ } $filter->get_query_condition) {
				
				my @ori_condition = $table_to->query_condition();
				$table_to->query_condition(@pre_condition);
				
				my %target_key_condition;
				
				while (my %row=$table_to->fetch_row) {
					$target_key_condition{$row{TABLE_KEY}}++;
				}
				
				# restore old condition
				if ( @ori_condition ) {
					$table_to->query_condition(@ori_condition);
				}
				else {
					$table_to->query_condition(undef);
				}
				
				if (%target_key_condition) {
					$table_from->query_key_value(keys %target_key_condition);
					$table_to->query_key_value(keys %target_key_condition);
					
					
				}
				else {
					$logger->info("Aucune ligne ne correspond au filtre");
					return 0;
				}
			}
		}
		
		# additionnals fields
		$table_from->dynamic_field("TYPE","TEXT",$table_from->dynamic_field);
		$table_from->query_field("TYPE","TEXT",$table_from->query_field);
		
		$table_to->dynamic_field("TYPE","TEXT",$table_to->dynamic_field);
		$table_to->query_field("TYPE","TEXT",$table_to->query_field);
		
		# only target table need rules
		$table_to->isip_rules($rules);
		
		# open DataDiff table from two table
		my $table_status=FieldDiff->open($table_from, $table_to, {debug => $debug_level});
		
		# declare some additionnal blank fields
		# (ICON field will be computed into DataDiff)
		$table_status->dynamic_field("DIFF","ICON",$table_status->dynamic_field);
		#$table_status->query_field(@query_field,"OLD_FIELD_VALUE","DIFF");
		$table_status->query_field(@query_field);
		
		# compute diff
		$table_status->compare_init();

		# Assign a IsipRules to compute ICON field
		my $diff_rules=IsipRulesDiff->new($table_name,$env_sip);
		$table_status->isip_rules($diff_rules);
		
		# put row in memory
		$logger->notice("query fields: ",$table_status->query_field());
		while (my %row=$table_status->fetch_row) {
			
			if ($row{MEMO}) {
				$row{MEMO}=decode_memo($row{MEMO});
			}
			
			# don't show hidden fields
			next if $diff_rules->is_field_hidden(%row);
			
			if ($filter->is_display_line(%row)) {
				
				# eclate la ligne si ligne de la clef
				my @lines;
				if ( $explode_key and $row{FIELD_NAME} ) {
					
					my @fields = split( /,/, $row{FIELD_NAME}  );
					my @values = split( /,/, $row{FIELD_VALUE} );
					
					while ( @fields ) {
						my %temp_row = %row;
						$temp_row{FIELD_NAME} = shift @fields;
						$temp_row{FIELD_VALUE} = shift @values;
						
						push @lines, join($separator, $table_status->hash_to_array(%temp_row));
					}
				}
				else {
					push @lines, join($separator,$table_status->hash_to_array(%row));
				}
				
				foreach my $line_to_print ( @lines ) {
				
					if ( $report_mode ) {
						# corrige le codage des caract�res pour Excel
						$line_to_print = decode('cp1250', $line_to_print);
					}
					if ($all_key) {
						print $line_to_print."\n";
					}
					else {
						$memory_row{$row{FIELD_NAME}}= $line_to_print."\n";
					}
				}
			}
		}
		
	}
	elsif ($explore_mode eq "explore") {

		# open histo table
		my $table_status = $env_sip->open_histo_field_table($table_name, $date_explore, {debug => $debug_level});
		
		$table_status->query_key_value($table_key_value) if $table_key_value;
		$table_status->query_field(@query_field);
		$table_status->isip_rules($rules);
		
		$table_status->query_condition($filter->get_query_condition);
		
		$table_status->output_separator('@');
		
		# put row in memory
		while (my %row=$table_status->fetch_row) {
		
			if ($row{MEMO}) {
				# done in Processor
				#$row{MEMO}=decode_memo($row{MEMO});
			}
			
			if (not $date_explore) {
				# don't show hidden fields
				next if $rules->is_field_hidden(%row);
			}
			if ($filter->is_display_line(%row)) {
			
				# eclate la ligne si ligne de la clef
				my @lines;
				if ( $explode_key and $row{FIELD_NAME} eq $table_key) {
					
					my @fields = split( /,/, $row{FIELD_NAME}  );
					my @values = split( /,/, $row{FIELD_VALUE} );
					
					while ( @fields ) {
						my %temp_row = %row;
						$temp_row{FIELD_NAME} = shift @fields;
						$temp_row{FIELD_VALUE} = shift @values;
						
						push @lines, join($separator,$table_status->hash_to_array(%temp_row));
					}
				}
				else {
					push @lines, join($separator,$table_status->hash_to_array(%row));
				}
				
				foreach my $line_to_print ( @lines ) {
					if ($all_key) {
						if (grep {$_ eq $row{FIELD_NAME}} @histo_table_field) {
							print $line_to_print."\n";
						}
					}
					else {
						$memory_row{$row{FIELD_NAME}} = $line_to_print."\n";
					}
				}
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