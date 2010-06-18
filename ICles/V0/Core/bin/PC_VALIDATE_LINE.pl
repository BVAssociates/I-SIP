#!/usr/bin/env perl

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

###########################################################
=head1 NAME

PC_VALIDATE_LINE

=head1 SYNOPSIS

 PC_VALIDATE_LINE.pl [-t] [-r] [-p projet] environnement table_name commentaires a ajouter
 
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

sub log_erreur {
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
my $global_comment;
my $global_project;

# pools de connexions reutilisables
my $connection_cache_status;
my %connection_pool_field;


# Met à jour les commentaires d'une ligne (cad tous les champs)
sub update_line {
	my $current_table = shift;
	my $set_testing   = shift;
	my $key_value     = shift;
	
	if ( ! $current_table
			or ! $key_value ) {
		croak('usage: update_line($current_table,$set_testing,$key_value)');
	}
	
	# recuperation des nom des etats
	my %status_label=IsipRules::enum_field_status();

	# decide du nouvel etat à attribuer
	my $status_string = $status_label{OK};
	$status_string = $status_label{TEST} if $set_testing;

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
			"DATE_UPDATE",
			"USER_UPDATE",
			"PROJECT",
			"COMMENT",
		);
	
	# tableau des champs à modifier
	my @field_refs;
	
	# recupération des champs à modifier
	while( my %field_to_update=$table_field->fetch_row() ) {
		
		# Si le champ est Valide et que le commentaire
		# est présent, on passe
		if ($field_to_update{STATUS} eq 'Valide'
			and $field_to_update{COMMENT} ) {
			
			next;
		}
		
		# met le champ en mémoire
		push @field_refs, \%field_to_update;
	}

	# modification effective des champs
	log_info("UPDATING FIELD $current_table:$key_value");
	$table_field->begin_transaction();
	foreach my $field ( @field_refs ) {
		my %field_to_update = %{$field};
		
		#use Data::Dumper;warn Dumper($field);
		
		$field_to_update{PROJECT} = $global_project if defined $global_project;
		$field_to_update{COMMENT} = $global_comment;
		$field_to_update{STATUS}  = $status_string;
		
		$table_field->update_row( %field_to_update );
	}
	$table_field->commit_transaction();
}

# fonction de recursion dans les lignes liées
sub recurse_into_table {
	my $current_table = shift;
	my $set_testing   = shift;
	my %value_of_key  = @_;
	
	if ( ! $current_table 
		or ! %value_of_key ) {
		croak('usage: recurse_into_table($current_table,$set_testing,%value_of_key)');
	}
	
	# descente dans les tables dependantes
	foreach my $child_table ( $global_links->get_child_tables($current_table) ) {
		
		my %foreign_fields=$global_links->get_foreign_fields($child_table,$current_table);
		
		my $table = $global_env->open_local_from_histo_table($child_table);
		my $type_rules = IsipRules->new($child_table, $global_env);
		$table->isip_rules($type_rules);
		
		# pour chaque champ on recupère la valeur de la clef étrangère
		my @query_condition;
		foreach my $foreign_field (keys %foreign_fields) {
		
			my $var=$foreign_fields{$foreign_field};
			if ( $value_of_key{$var} ) {
				# construction de la condition de selection sur la table liée
				push @query_condition, "$foreign_field = ".$table->quote($value_of_key{$var});
				
			}
		}
		
		# prepare la requete de selection sur la table fille
		$table->query_condition(@query_condition);
		$table->query_field('ICON','PROJECT',$table->query_field());
		
		my @key_field=$table->key();
		
		$connection_cache_status->load_cache($child_table);
		
		# parcours des lignes liées
		my %line_refs;
		while ( my %row = $table->fetch_row() ) {
			
			# construit la clef
			my %keys_child;
			@keys_child{@key_field} = @row{@key_field};
			
			my $keys_child_string = join(',', @keys_child{@key_field});
			
			if( $row{ICON} ne "valide" or $connection_cache_status->is_dirty_key($child_table,$keys_child_string) ) {
				#log_info("UPDATE ROW $child_table:".join(',',$row{ICON},@row{@key_field}) );
				
				# garde en mémoire les lignes à traiter+les lignes où descendre
				# pour eviter le LOCK de la table
				$line_refs{$keys_child_string} = \%row;
			}

		}
		
		# il existe des lignes à mettre à jour
		if ( %line_refs ) {
			# mise à jour effective des lignes rencontrées
			foreach my $line ( values %line_refs ) {
				my %row = %{$line};
				
				# met à jour les lignes non-valides
				if( $row{ICON} ne "valide" ) {
					update_line($child_table, $set_testing, join(',', @row{@key_field}) );
				}
			}
			
			# mise à jour du cache si l'icone à changé
			$table->query_key_value( keys %line_refs );
			while ( my %row = $table->fetch_row() ) {
				
				# appel recursif sur la clef 
				recurse_into_table($child_table,$set_testing,%row);
				
				my $keys_child_string = join(',', @row{@key_field});
				
				die("La ligne <$keys_child_string> de la table $child_table n'existe plus après mise à jour") if not exists $line_refs{$keys_child_string};
				
				{
					#met à jour le cache
					local $row{OLD_ICON} = $line_refs{$keys_child_string}->{ICON};
					local $row{OLD_PROJECT} = $line_refs{$keys_child_string}->{PROJECT};
					
					# si un élément est changé, il faut mettre le cache à jour
					if( $row{ICON} ne $row{OLD_ICON}
						or $row{PROJECT} ne $row{OLD_PROJECT} )
					{
						
						#log_info("UPDATING CACHE ($row{ICON}:$row{PROJECT}) $child_table:$keys_child_string" );
						
						my $cache=IsipTreeCache->new($global_env);
						$cache->add_dispatcher($connection_cache_status) if $row{ICON} ne $row{OLD_ICON};
						$cache->add_dispatcher(CacheProject->new($global_env)) if $row{PROJECT} ne $row{OLD_PROJECT};
						
						$cache->recurse_line($child_table, \%row);

						$cache->save_cache();
					}
				}

			}
		}
	}

}


#  Traitement des Options
###########################################################


my %opts;
getopts('Thvrtp:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};


my $set_testing = $opts{t};
my $recursive   = $opts{r};
$global_project = $opts{p};

#  Traitement des arguments
###########################################################

if ( @ARGV < 3) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $global_environnement=shift;
my $table_name=shift;

$global_comment=join(' ', @ARGV);

use Encode;
$global_comment=encode("cp850",$global_comment) if $^O eq 'MSWin32';

if ( ! $global_comment ) {
	usage($debug_level);
}


#  Corps du script
###########################################################

my $bv_severite=0;

## DEBUG ONLY
if (exists $opts{T}) { $ENV{FHCDTRAIT}='ACH750'; $bv_severite=202 };
## DEBUG ONLY

# Initialise variables globales
$global_env = Environnement->new($global_environnement, {debug => $debug_level});
$global_links = $global_env->get_links();

# recuperation de la clef primaine de la table
my @table_key = $global_env->get_table_key($table_name);
my $table_key_value = join(',', @ENV{@table_key});

if (not @table_key) {
	log_erreur("pas de clef primaine pour la table $table_name");
	sortie(202);
}

# met à jour la ligne en cours
update_line($table_name, $set_testing, $table_key_value);

# met à jour recursivement les tables filles
if ( $recursive ) {
	
	$connection_cache_status = CacheStatus->new($global_env);
	
	recurse_into_table($table_name, $set_testing, %ENV);
}

