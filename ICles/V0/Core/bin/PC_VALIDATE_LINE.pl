#!/usr/bin/env perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

use ITable::ITools;
use Isip::Environnement;
use Isip::IsipRules;

###########################################################
=head1 NAME

PC_VALIDATE_LINE

=head1 SYNOPSIS

 PC_VALIDATE_LINE.pl [-t] [-r] environnement table_name commentaires a ajouter
 
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


# variables globales utilisées pour la recursion
my $global_links;
my $global_env;

my %connection_pool_table;
my %connection_pool_field;

my $global_status;
my $global_comment;

sub update_line {
	my $current_table = shift;
	my $key_value=shift;
	
	my $table_field;
	if ( exists $connection_pool_field{$current_table} ) {
		# reutilise une connexion existante
		$table_field = $connection_pool_field{$current_table};
		$table_field->query_field(
				"ID",
				"STATUS",
				"DATE_UPDATE",
				"USER_UPDATE",
				"PROJECT",
				"COMMENT",
			);
		$table_field->finish();
	}
	else {
		$table_field = $global_env->open_histo_field_table($current_table);
		
		# garde la connexion pour reutilisation
		$connection_pool_field{$current_table} = $table_field;
	}
	$table_field->query_key_value($key_value);
	
	my @field_refs;
	while( my %field_to_update=$table_field->fetch_row() ) {
		next if $field_to_update{STATUS} eq 'Valide';
	
		push @field_refs, \%field_to_update;
	}
	
	foreach my $field ( @field_refs ) {
		my %field_to_update = %{$field};
		log_info("UPDATE FIELD $current_table:$key_value");
		
		$table_field->update_row( %{$field} );
	}
}

sub recurse_into_table {
	my $current_table = shift;
	my %value_of_key=@_;
	
	foreach my $child_table ( $global_links->get_child_tables($current_table) ) {
		
		my %foreign_fields=$global_links->get_foreign_fields($child_table,$current_table);
		
		my $table;
		if ( exists $connection_pool_table{$child_table} ) {
			$table = $connection_pool_table{$child_table};
			$table->finish();
		}
		else {
			$table = $global_env->open_local_from_histo_table($child_table);
			my $type_rules = IsipRules->new($child_table, $global_env);
			$table->isip_rules($type_rules);
			
			$connection_pool_table{$child_table} = $table;
		}
		
		my @query_condition;
		foreach my $foreign_field (keys %foreign_fields) {
			my $var=$foreign_fields{$foreign_field};
			if ( $value_of_key{$var} ) {
				push @query_condition, "$foreign_field = ".$table->quote($value_of_key{$var});
				$value_of_key{$foreign_field}=$value_of_key{$var};
			}
		}
		
		$table->query_condition(@query_condition);
		$table->query_field('ICON',$table->query_field());
		
		my @key_field=$table->key();
		
		my %line_refs;
		while ( my %row = $table->fetch_row() ) {
			
			my %keys_child;
			@keys_child{@key_field} = @row{@key_field};
			
			my $keys_child_string = join(',', @keys_child{@key_field});
			
			if( $row{ICON} ne "valide" ) {
				#log_info("UPDATE ROW $child_table:".join(',',$row{ICON},@row{@key_field}) );
				
				$line_refs{$keys_child_string} = \%row;
			}

			recurse_into_table($child_table,%keys_child);
		}
		
		foreach my $line ( values %line_refs ) {
			my %row = %{$line};
			update_line($child_table, join(',', @row{@key_field}) );
		}
		
		# mise à jour du cache si l'icone à changé
		while ( my %row = $table->fetch_row() ) {
			
			my $keys_child_string = join(',', @row{@key_field});
			
			next if not exists $line_refs{$keys_child_string};
			
			if( $row{ICON} ne $line_refs{$keys_child_string}->{ICON} ) {
				log_info("UPDATE CACHE ($row{ICON}) $child_table:$keys_child_string" );
				#TODO update cache
			}

			@value_of_key{@key_field} = @row{@key_field};
			recurse_into_table($child_table,%value_of_key);
		}
	}

}


#  Traitement des Options
###########################################################


my %opts;
getopts('Thvrt', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

# recuperation des nom des etats
my %status_label=IsipRules::enum_field_status();

$global_status = $status_label{OK};
$global_status = $status_label{TEST} if $opts{t};

my $recursive = $opts{r};

#  Traitement des arguments
###########################################################

if ( @ARGV != 2) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $global_environnement=shift;
my $table_name=shift;

$global_comment=join(' ', @ARGV);


#  Corps du script
###########################################################

my $bv_severite=0;

## DEBUG ONLY
if (exists $opts{T}) { $ENV{FHCDTRAIT}='BDGCLT'; $bv_severite=202 };
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
update_line($table_name, $table_key_value);

# met à jour recursivement les tables filles
if ( $recursive ) {
	recurse_into_table($table_name, %ENV);
}

