package Ft;
#require ITable::ITools::Legacy;
our @ISA = ("Legacy");

use strict;

use Carp qw(carp cluck confess croak );
use Fcntl qw(:DEFAULT :flock);

use Isip::IsipLog '$logger';


####################################################
# private methods
####################################################

# open and lock
sub _open_table_file {
	my $self=shift;
	
	my $mode=shift;
	$mode = O_RDONLY if not $mode;
	
	my $lock_mode=LOCK_SH;
	$lock_mode= LOCK_EX if $mode == O_RDWR;
	
	# recupere le chemin de la table
	my $table_file = $self->{define}->file();
	
	# interprets vars
	$table_file =~s/\$\{(\w+)\}/$ENV{$1}/g;
	$table_file =~s/\$(\w+)/$ENV{$1}/g;
	$table_file =~s/%(\w+)%/$ENV{$1}/g;
	
	if ( not $table_file) {
		croak("fichier tab non défini ou table virtuelle : ".$self->table_name);
	}
	
	if ( not -e $table_file) {
		croak("fichier tab introuvable : ".$table_file);
	}
	
	$logger->info("opening ITools table ".$self->table_name);
	
	sysopen(my $table_fh, $table_file, $mode)
	##remplace ouverture simple par idiome Perl de lock
	#open( my $table_fh, "+< $table_file")
        or die "can't open $table_file: $!";
    
	# autoflush $table_fh (idiome Perl)
    my $stdout = select($table_fh); # STDOUT->$table_fh
	$| = 1;                         # autoflush STDOUT
	select ($stdout);               # restore STDOUT
	
	# lock exclusif avec attente
    flock($table_fh, $lock_mode)
        or die "can't write-lock ".$self->table_name().": ".$!;
	seek($table_fh, 0, 0)
        or die "can't rewind ".$self->table_name().": ".$!;
	
	$self->{select_descriptor}=$table_fh;
}

sub _write_table_file {
	my $self=shift;
	
	my @content=@_;

	if ( not $self->{select_descriptor} ) {
		croak("Aucune table ouverte");
	}

	print { $self->{select_descriptor} } @content
        or die "can't write ".$self->table_name().": ".$!;
}

sub _empty_table_file {
	my $self=shift;

	if ( not $self->{select_descriptor} ) {
		croak("Aucune table ouverte");
	}

	# vide la table
	truncate($self->{select_descriptor}, 0)
        or die "can't truncate ".$self->table_name().": ".$!;
	# retour au debut
	seek($self->{select_descriptor}, 0, 0)
        or die "can't rewind ".$self->table_name().": ".$!;
}

sub _close_table_file {
	my $self=shift;
	
	if ( not $self->{select_descriptor} ) {
		croak("Aucune table ouverte");
	}
	
	close($self->{select_descriptor})
        or die "can't close ".$self->table_name().": ".$!;
}

####################################################
# public methods
####################################################

# recupere les données de la table ligne à ligne
# renvoie un tableau vide à la fin du fichier
sub fetch_row_array_pp {
	my $self = shift;
	
	my $separator=$self->output_separator();
	my @temp_return;
	
	if (not defined $self->{select_descriptor} ) {
		$self->_open_table_file(O_RDONLY);
	}
	
	my $select_output=readline( $self->{select_descriptor} );
	
	$self->_close_table_file() if not defined $select_output;
	
	if (defined $select_output) {
		chomp $select_output;
		
		@temp_return=split($separator,$select_output,-1);
		
		## ITools BUG : don't return end separators if fields are NULL
		my $field_num_diff=0;
		$field_num_diff= $self->query_field() - @temp_return if @temp_return;
		if ( $field_num_diff != 0 ) {
			push  @temp_return, (undef) x $field_num_diff;
		}
		##
	}
	
	## TODO: QUERY FIELD
	## TODO: CONDITION
	## TODO: SORT
	
	return @temp_return
}

# ITools' Insert remplacement en pure Perl
# fonctionne en INSERT OR REPLACE
# param list : hash de la forme CHAMP => VALEUR
# note : seul les champs présent dans le hash seront mis à jour
sub insert_row_pp {
	my $self=shift;
	
	my %row = @_;

	# declare le filehandle de la table
	$self->_open_table_file(O_RDWR);
	
	
    # avale toute la table dans la mémoire
	my @table_lines;
	while ( my $line = readline($self->{select_descriptor}) ) {
		push @table_lines, $line;
	}
	
	# nettoie la ligne à inserer
	foreach my $field ( %row ) {
		# transforme les undef en chaine vide (pas de NULL en I-TOOLS)
		$field='' if not defined $field;
	}
	
	# transforme texte en tableau de dictionnaire
	my @table_lines_hash = map {
			if (! /^(?:#|\s*$)/) {
				# enleve les fin de ligne
				chomp;
				
				# tranforme la ligne en tableau de valeur
				my @fields = split ( $self->output_separator , $_ , -1);
				
				# transforme les undef en chaine vide (pas de NULL en I-TOOLS)
				map {$_='' if not defined $_} @fields;
				
				# sauvegarde du hash sous forme d'une référence
				$_ = { $self->array_to_hash( @fields )};
			}
			else {
				# les commentaires et les lignes vides ne sont pas transformés
				$_;
			}
		} @table_lines;
	
	my $update_key=0;  # >0 si clef trouvée (mode UPDATE)
	my $touch_file=0;  # >0 si des valeurs ont été modifiées
	# cherche si clef déjà présente
	ROW:
	foreach my $line_hash ( @table_lines_hash ) {
		if (ref $line_hash) {
		
			my $key_match=0;
			
			# sur chaque ligne, verifie toutes les clefs
			foreach my $key_field ( $self->key() ) {
				if ( $line_hash->{$key_field} eq $row{$key_field} ) {
					$key_match++;
				}
			}
			
			# si on a trouvé toutes les clefs, on met à jour
			if ( $key_match == $self->key() ) {
				
				# on est en mode mise à jour
				$update_key++;
				
				FIELD:
				foreach my $set_field ( keys %row ) {
				
					if ( ! grep { /^$set_field$/ } $self->field() ) {
						$logger->debug("$set_field est un champ inconnu");
						next FIELD;
					}
					
					
					# gère les cas ou la valeur est undef
					# normalement pas nécéssaire
					my $defined_vars=0;
					$defined_vars++ if defined $line_hash->{$set_field};
					$defined_vars++ if defined $row{$set_field};
					
					if ( $defined_vars != 0) {
						# au moins 1 des 2 est défini
						
						if ( $defined_vars != 2
							or $line_hash->{$set_field} ne $row{$set_field} )
						{
							$logger->debug("update $set_field : $line_hash->{$set_field} = $row{$set_field} ");
							# 1 des 2 n'est pas défini ou les 2 sont differents
							$line_hash->{$set_field} = $row{$set_field};
							$touch_file++;
						}
					}
				}
				
				# une clef est unique, on sort de la boucle
				last ROW;
			}
		}
	}
	
	# Si pas de mise à jour, alors insertion simple
	if (not $update_key) {
		push @table_lines_hash, \%row;
		$touch_file++;
	}
	
	# le fichier doit être modifié
	if ( $touch_file ) {
	
		# retransforme les dictionnaires en texte
		@table_lines = map {
			# si c'est une référence, ce sont des données
			if (ref $_) {
				# transforme le hash en tableau
				my @fields = $self->hash_to_array( %{$_} );
				
				# transforme les undef en chaine vide
				map {$_='' if not defined $_} @fields;
				
				# tranforme les champs en ligne
				$_ = join( $self->output_separator , @fields );
				
				# supprime et recree fin de ligne au cas où
				chomp;
				$_.="\n";
			}
			# sinon, on garde tel quel
			else {
				$_;
			}
		} @table_lines_hash;

		
		# réécrit entierment le fichier avec la table modifiée
		$self->_empty_table_file();
		
		$self->_write_table_file( @table_lines );
	}

	# fermeture du fichier
	$self->_close_table_file();
	
	return;
}

1;  # so the require or use succeeds

=head1 NAME

ITable::ITools::Ft is a wrapper class to ITools::DATA::ITools::Legacy

=head1 SYNOPSIS

 See ITable::ITools::Legacy
 
=cut
 
 
 
package Legacy;
use ITable::abstract::ITools_interface;
our @ISA = ("ITools_interface");

use ITable::ITools::Define;

use Carp qw(carp cluck confess croak );
use strict;

##################################################
##  constructor  ##
##################################################

# new("table_name")
sub open (){
    my $proto = shift;
    my $class = ref($proto) || $proto;

	# mandatory parameter
	if (@_ < 1) {
		croak ("'new' take 1 argument")
	}
	
	# call the base constructor
    my $self  = $class->SUPER::open(@_);
	
	# add private members
	$self->{select_descriptor} = undef;
	
    bless($self, $class);
    return $self;
}


##############################################
## Virtual methods provided by Interface       ##
##############################################

# print the query being processed (ITools like syntax)
sub get_query()
{
	my $self = shift;
	
	my $query;
	$query = "Select -s ".join(', ',$self->query_field())." FROM ".$self->table_name();
	$query = $query." WHERE ".join(' AND ',$self->query_condition()) if $self->query_condition()	!= 0;
	$query = $query." ORDER_BY ".join(', ',$self->query_sort()) if $self->query_sort()	!= 0;
	
	return $query;
}

# open pipe on a Itools Select command
sub _open_select() {
	my $self = shift;
	
	my $select_command=$self->get_query();
	$self->_debug("Exec ITools : ",$select_command);
	## Core::open != $self->open
	CORE::open($self->{select_descriptor},"$select_command |") or croak "Error running $select_command : $!";
	#print STDERR "DEBUG: Opening $select_command |\n";
}

sub _close_select() {
	my $self = shift;
	close $self->{select_descriptor};
	$self->{select_descriptor}= undef;
	$self->_debug("Closing select_descriptor");
	return 1;
}

# get row one by one based on query
#return array
sub fetch_row_array()
{
	my $self = shift;
	my $separator=$self->output_separator();
	my @temp_return;
	
	$self->_open_select() if not defined $self->{select_descriptor};
	my $select_output=readline($self->{select_descriptor});
	$self-> _close_select() if not defined $select_output;
	
	if (defined $select_output) {
		chomp $select_output;
		
		@temp_return=split($separator,$select_output);
		
		## ITools BUG : don't return end separators if fields are NULL
		my $field_num_diff=0;
		$field_num_diff= $self->query_field() - @temp_return if @temp_return;
		if ( $field_num_diff != 0 ) {
			push  @temp_return, (undef) x $field_num_diff;
		}
		##
	}
	
	return @temp_return
}

sub insert_row_array() {
	my $self=shift;
	
	my @row = @_;
	
	my $insert_cmd="Insert -f INTO ".$self->table_name()." VALUES \"".join($self->output_separator,@row).'"';	
	my @return=`$insert_cmd 2>&1`;
	my $return = $? >> 8;
	
	if ($return) {
		warn $_ foreach grep {s/^Message ://} @return;
		croak("Error $return while insert");
	}
	return $return;
}

sub insert_row() {
	my $self=shift;
	
	my %row = @_;
	
	my @array=$self->hash_to_array(%row);
	$self->insert_row_array(@array);
}

sub update_row_array() {
	my $self=shift;
	
	my @row = map {"\"$_\""} @_;
	
	my $update_cmd="Replace INTO ".$self->table_name()." VALUES ".join(' ',@row);	
	my @return=`$update_cmd`;
	my $return = $? >> 8;
	
	if ($return) {
		croak("Error $return while executing : $update_cmd");
	}
	return $return;
}

sub update_row() {
	my $self=shift;
	
	my %row = @_;
	
	my @array=$self->hash_to_array(%row);
	$self->update_row_array(@array);
}

sub delete_row_array() {
	my $self=shift;
	
	my %hash=$self->array_to_hash(@_);
	
	return $self->delete_row_array(%hash);
}

sub delete_row() {
	my $self=shift;
	
	my %row = @_;
	
	my @where_condition;
	while (my ($field,$value)=each %row) {
		push @where_condition, $field."='".$value."'";
	}
	
	my $delete_cmd="Delete FROM ".$self->table_name()." WHERE ".join(' AND ',@where_condition);	
	my @return=`$delete_cmd`;
	my $return = $? >> 8;
	
	if ($return) {
		croak("Error $return while executing : $delete_cmd");
	}
}

# abort current request being processed
sub finish() {
	my $self = shift;
	
	$self-> _close_select() if defined $self->{select_descriptor}
}



1;  # so the require or use succeeds


=head1 NAME

ITools::Table::Legacy - class to use an ITools Table with the standard ITools Executables

=head1 SYNOPSIS

 use ITools::Table::Legacy;

 #################
 # class methods #
 #################
 
 $ob    = Table->open("table_name");

 #######################
 # object data methods #
 #######################

 $ob->fields("toto","tata");
 print join(',',$ob->fields);
 
 $ob->conditions("toto > 0");
 print  join(',',$ob->conditions);

 ########################
 # other object methods #
 ########################

 while ( @line=$ob->fetch_row_array() ) {
	print "field0: ".$line[0];
 }

=head1 DESCRIPTION

An Itools Table...

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut