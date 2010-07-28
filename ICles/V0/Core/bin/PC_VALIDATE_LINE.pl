#!/usr/bin/env perl
package pc_validate_line;

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;
use Carp;

use Isip::IsipLog '$logger';

use ITable::ITools;
use Isip::Environnement;
use Isip::IsipRules;

use Isip::IsipTreeCache;
use Isip::Cache::CacheStatus;
use Isip::Cache::CacheProject;
use Isip::Cache::CacheTempo;

###########################################################
=head1 NAME

PC_VALIDATE_LINE

=head1 SYNOPSIS

 PC_VALIDATE_LINE.pl [-t etat] [-r] [-p projet] [-m memo-base64] environnement table_name commentaires a ajouter
 
=head1 DESCRIPTION

Met l'état à "Valide" ou "Test" sur tous les champs etant dans l'etat non "Valide".
Ajoute le commentaires obligatoire.

=head2 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head2 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -t : applique l'etat "Test" au lieu de "Valide"

=item -r : recursif sur tous les lignes liées par clef étrangère

=item -p : affecte un nom de projet

=back

=head2 ARGUMENTS 

=over

=item environnement : environnement à utiliser

=item table_name : nom de la table a décrire

=item commentaires a ajouter : commentaires à affecté à chaque ligne

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
	pod2usage(-verbose => $verbosity+1, -noperldoc => 1);
	sortie(202); 
}

sub log_error {
	#print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	$logger->error(@_);
	sortie(202);
}

sub log_info {
	#print STDERR "INFO: ".join(" ",@_)."\n"; 
	$logger->notice(@_);
}


# variables communes utilisées pour la recursion
my $global_links;
my $global_env;
my $cache_manager;

# pools de connexions reutilisables
my $connection_cache_status;
my $connection_cache_tempo;
my %connection_pool_field;


# Met à jour les commentaires d'une ligne (cad tous les champs)
sub update_line {
	my $current_table = shift;
	my $comment_edit_ref  = shift;
	my %comment_edit = %{ $comment_edit_ref };
	my $key_value     = shift;
	
	if ( ! $current_table
			or ! $key_value ) {
		croak('usage: update_line($current_table,$comment_edit_ref,$key_value)');
	}
	
	my $table_field;
	if ( exists $connection_pool_field{$current_table} ) {
		# reutilise une connexion existante
		$table_field = $connection_pool_field{$current_table};
		$table_field->finish();
	}
	else {
		$table_field = $global_env->open_histo_field_table($current_table);
		
		# garde la connexion pour reutilisation
		$connection_pool_field{$current_table} = $table_field;
	}
	
	# valorise la clef à rechercher
	$table_field->query_key_value($key_value);
	$table_field->query_field(
			"ID",
			"STATUS",
			"FIELD_NAME",
			"TABLE_KEY",
			"DATE_UPDATE",
			"USER_UPDATE",
			"PROJECT",
			"COMMENT",
			"MEMO",
		);
	
	# tableau des champs à modifier
	my @field_refs;
	
	# recupération des champs à modifier
	while( my %field_to_update=$table_field->fetch_row() ) {
		
		# Si le champ est Valide et que le commentaire
		# est présent, on passe
		if ($field_to_update{STATUS} eq 'Valide'
			and $field_to_update{COMMENT} ) {
			
			#log_info("PASS FIELD $current_table:$key_value:$field_to_update{FIELD_NAME}");
			next;
		}
		
		# met le champ en mémoire
		push @field_refs, \%field_to_update;
	}

	$table_field->begin_transaction();
	foreach my $field ( @field_refs ) {
		my %field_to_update = %{$field};
		
		$field_to_update{PROJECT} = $comment_edit{"PROJECT"} if defined $comment_edit{"PROJECT"};
		$field_to_update{COMMENT} = $comment_edit{"COMMENT"} if defined $comment_edit{"COMMENT"};;
		$field_to_update{STATUS}  = $comment_edit{"STATUS"}  if defined $comment_edit{"STATUS"};
		$field_to_update{MEMO}    = $comment_edit{"MEMO"}    if defined $comment_edit{"MEMO"};
		
		log_info("UPDATING FIELD $current_table:$key_value:$field_to_update{FIELD_NAME}");
		$table_field->update_row( %field_to_update );
		
	}
	$table_field->commit_transaction();
}

# fonction de recursion dans les lignes liées
sub recurse_into_table {
	my $table_name = shift;
	my $comment_edit_ref  = shift;
	my $value_of_key_ref = shift;
	my %value_of_key  = %{ $value_of_key_ref };
	
	if ( ! $table_name 
		or ! %value_of_key ) {
		croak('usage: recurse_into_table($table_name,$set_testing,%value_of_key)');
	}
	
	# mise à jour de la ligne en cours
	
	my $table = $global_env->open_local_from_histo_table($table_name);
	
	my @key_field=$table->key();
	my $table_key_string = join(',', @value_of_key{@key_field});
	$table->query_key_value($table_key_string);
	
	$table->query_field('ICON','PROJECT',$table->query_field());
	my $type_rules = IsipRules->new($table_name, $global_env);
	$table->isip_rules($type_rules);
	
	my %row = $table->fetch_row();
	$table->finish();
	
	if (not %row ) {
		die("La ligne <$table_key_string> de la table $table_name n'existe plus après mise à jour");
	}

	if ( $row{ICON} !~ /^valide/ ) {
		update_line($table_name, $comment_edit_ref, $table_key_string );
	
		my %row_after = $table->fetch_row();
		$table->finish();
		
		$row{OLD_ICON}    = $row{ICON};
		$row{ICON}        = $row_after{ICON};
		$row{OLD_PROJECT} = $row{PROJECT};
		$row{PROJECT}        = $row_after{PROJECT};
		
		log_info("UPDATING CACHE ($row{OLD_ICON}->$row{ICON},$row{OLD_PROJECT}->$row{PROJECT}) $table_name:$table_key_string" );
		
		$cache_manager->recurse_line($table_name, \%row);
	}

	
	# descente dans les tables dependantes
	foreach my $child_table_name ( $global_links->get_child_tables($table_name) ) {
		
		my %foreign_fields=$global_links->get_foreign_fields($child_table_name,$table_name);
		
		my $child_table = $global_env->open_local_from_histo_table($child_table_name);
		my $type_rules = IsipRules->new($child_table_name, $global_env);
		$child_table->isip_rules($type_rules);
		
		# pour chaque champ on recupère la valeur de la clef étrangère
		my @query_condition;
		foreach my $foreign_field (keys %foreign_fields) {
		
			my $var=$foreign_fields{$foreign_field};
			if ( $value_of_key{$var} ) {
				# construction de la condition de selection sur la table liée
				push @query_condition, "$foreign_field = ".$child_table->quote($value_of_key{$var});
				
			}
		}
		
		# prepare la requete de selection sur la table fille
		$child_table->query_condition(@query_condition);
		$child_table->query_field('ICON','PROJECT',$child_table->query_field());
		
		
		$connection_cache_status->load_cache($child_table_name);
		
		# parcours des lignes liées
		my @key_field=$child_table->key();
		my %line_refs;
		while ( my %row = $child_table->fetch_row() ) {
			
			# construit la clef
			my %keys_child;
			@keys_child{@key_field} = @row{@key_field};
			
			my $keys_child_string = join(',', @keys_child{@key_field});
			
			log_info("$row{ICON} - $child_table_name - $keys_child_string - dirty=".$connection_cache_status->is_dirty_key($child_table_name,$keys_child_string));
			if( $row{ICON} !~ /^valide/ or $connection_cache_status->is_dirty_key($child_table_name,$keys_child_string) ) {
				
				# garde en mémoire les lignes à traiter+les lignes où descendre
				# pour eviter le LOCK de la table
				$line_refs{$keys_child_string} = \%row;
			}

		}
		
		# mise à jour effective des lignes rencontrées
		foreach my $line ( values %line_refs ) {
			my %line=%{$line};
			#log_info("GO DEEP $child_table_name:".join(',',@line{@key_field}) );
			recurse_into_table($child_table_name, $comment_edit_ref, $line);
		}
	}

}

sub run {
	local @ARGV=@_;

#  Traitement des Options
###########################################################

log_info("Debut du programme : ".$0." ".join(" ",@ARGV));

my %opts;
getopts('Thvrt:p:m:', \%opts) or usage(1);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};


my %comment_edit;
$comment_edit{"STATUS"}  = $opts{t};
$comment_edit{"MEMO"}    = $opts{m};
$comment_edit{"PROJECT"} = $opts{p};

my $recursive = $opts{r};


# vérification du nom de l'etat
my %status_label=IsipRules::enum_field_status();
if ( ! grep {$_ eq $comment_edit{"STATUS"}}  values %status_label){
	log_error($comment_edit{"STATUS"}." n'est pas un status valide");
}

#  Traitement des arguments
###########################################################

if ( @ARGV < 3) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $global_environnement = shift @ARGV;
my $table_name           = shift @ARGV;

# tout le reste est le commentaire
$comment_edit{"COMMENT"} = join(' ', @ARGV);

if ( ! $comment_edit{"COMMENT"} ) {
	usage($debug_level);
}


#  Corps du script
###########################################################

my $bv_severite=0;

## DEBUG ONLY
if (exists $opts{T}) { $ENV{FHCDTRAIT}='ACHTRI'; $bv_severite=202 };
## DEBUG ONLY

# Initialise variables globales
$global_env = Environnement->new($global_environnement, {debug => $debug_level});
$global_links = $global_env->get_links_menu();

# recuperation de la clef primaine de la table
my @table_key = $global_env->get_table_key($table_name);
my $table_key_value = join(',', @ENV{@table_key});

if (@table_key) {
	log_info("Validation de la clef $table_name:$table_key_value");
}
else {
	log_error("pas de clef primaine pour la table $table_name");
	sortie(202);
}

# creation du gestionnaire de cache
$connection_cache_status = CacheStatus->new($global_env);
$connection_cache_tempo = CacheTempo->new($global_env);

$cache_manager=IsipTreeCache->new($global_env);
$cache_manager->add_dispatcher($connection_cache_status);
$cache_manager->add_dispatcher($connection_cache_tempo);
$cache_manager->add_dispatcher(CacheProject->new($global_env));

# met à jour recursivement les tables filles
if ( $recursive ) {
	
	$connection_cache_status = CacheStatus->new($global_env);
	$connection_cache_tempo = CacheTempo->new($global_env);
	
	recurse_into_table($table_name, \%comment_edit, \%ENV);
}
else {
	# met à jour la ligne en cours
	update_line($table_name, \%comment_edit, $table_key_value);
}

$cache_manager->save_cache();

# fin de la fonction principale
return 1;
}
exit run(@ARGV) if !caller;
1;