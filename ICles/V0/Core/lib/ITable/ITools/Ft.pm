package Ft;
require ITable::ITools::Legacy;
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
	
	if ( not $table_file) {
		#croak("fichier tab non défini ou table virtuelle : ".$self->table_name);
		
		croak("fichier tab ou command manquant") if not $self->{define}->command();
		
		my $command = $self->{define}->command();
		# interprets vars
		$command =~s/\$\{(\w+)\}/$ENV{$1}/g;
		$command =~s/\$(\w+)/$ENV{$1}/g;
		$command =~s/%(\w+)%/$ENV{$1}/g;
		
		my $command_pipe;
		open ( $command_pipe, "$command |") or die "can't fork $command : $!";
		
		$self->{select_descriptor}=$command_pipe;
	}
	else {
		
		# interprets vars
		$table_file =~s/\$\{(\w+)\}/$ENV{$1}/g;
		$table_file =~s/\$(\w+)/$ENV{$1}/g;
		$table_file =~s/%(\w+)%/$ENV{$1}/g;
		
		if ( not -e $table_file) {
			croak("fichier tab introuvable : ".$table_file);
		}
		
		$logger->info("opening ITools table ".$self->table_name." ($table_file)");
		
		sysopen(my $table_fh, $table_file, $mode)
		##remplace ouverture simple par idiome Perl de lock
		#open( my $table_fh, "+< $table_file")
			or die "can't open $table_file: $!";
		
		# lock exclusif avec attente
		flock($table_fh, $lock_mode)
			or die "can't write-lock ".$self->table_name().": ".$!;
		seek($table_fh, 0, 0)
			or die "can't rewind ".$self->table_name().": ".$!;
		
		# autoflush $table_fh (idiome Perl)
		#my $stdout = select($table_fh); # STDOUT->$table_fh
		#$| = 1;                         # autoflush STDOUT
		#select ($stdout);               # restore STDOUT
		
		$self->{select_descriptor}=$table_fh;
	}
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


#get hash of row by one based on query
sub fetch_row_pp()
{
	my $self = shift;

	my @row=$self->fetch_row_array_pp();
	my %row_object;
	
	return () if not @row;
	
	foreach my $temp_field (@{ $self->{query_field} }) {
		if (exists $self->{dynamic_field}->{$temp_field}) {
			$row_object{$temp_field}="";
		}
		else {
			croak "fetch_row_array returned wrong number of values (need more field)" if not @row;
			$row_object{$temp_field}=shift @row;
		}
	}
	
	# internal test
	croak "fetch_row_array returned wrong number of values (too much field)" if  @row;

	#for (my $i=0; $i < @real_fields; $i++) {
	#	$row_object{$real_fields[$i]}=$row[$i];
	#}
	#for (@dyna_fields) {
	#	$row_object{$_}="";
	#}
		
	return %row_object;
}


# recupere les données de la table ligne à ligne
# renvoie un tableau vide à la fin du fichier
sub fetch_row_array_pp {
	my $self = shift;
	
	my $separator=$self->output_separator();
	my @temp_return;
	
	if (not defined $self->{select_descriptor} ) {
		$self->_open_table_file(O_RDONLY);
	}
	
	my $select_output;
	while ( defined (
				$select_output=readline( $self->{select_descriptor} ))
			)
	{
		
		if ( $select_output =~ /^#/ or $select_output =~ /^\s*$/) {
		
			$logger->debug("DROP: $select_output");
			
			# get one more line and retry
			next;
		}
		else {
			
			chomp $select_output;
			
			@temp_return=split($separator,$select_output,-1);
			
			## ITools BUG : don't return end separators if fields are NULL
			my $field_num_diff=0;
			$field_num_diff= $self->query_field() - @temp_return if @temp_return;
			if ( $field_num_diff != 0 ) {
				push  @temp_return, (undef) x $field_num_diff;
			}
			##
			
			# quit on valid line
			last;
		}
	}
	
	$self->_close_table_file() if not defined $select_output;
	
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
	my @table_lines_hash;
	my $last_line;
	foreach my $line ( @table_lines ) {
		
		# sauvegarde de la dernière ligne
		$last_line = $line;
		
		# les commentaires et les lignes vides ne sont pas transformés
		if ( $line !~ /^#/ and $line !~ /^\s*$/ ) {
			# enleve les fin de ligne
			chomp $line;
			
			# tranforme la ligne en tableau de valeur
			my @fields = split($self->output_separator(), $line, -1);
			
			# transforme les undef en chaine vide (pas de NULL en I-TOOLS)
			map {$_='' if not defined $_} @fields;
			
			# sauvegarde du hash sous forme d'une référence
			$line = { $self->array_to_hash( @fields )};
		}
		
		# ajout de la ligne transformée (texte ou hash)
		push @table_lines_hash, $line;
	}
	
	my $update_key=0;  # >0 si clef trouvée (mode UPDATE)
	my $touched=0;  # >0 si valeur modifiée (mode UPDATE)
	# cherche si clef déjà présente
	ROW:
	foreach my $line_hash ( @table_lines_hash ) {
		if (ref $line_hash) {
		
			my $key_match=0;
			my @key_value;
			
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
							
							# valeur modifiée
							$touched++;
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
		
		my $new_line=join( $self->output_separator , $self->hash_to_array(%row) );
		if ( ! grep { /\n/ } $last_line ) {
			$new_line = "\n".$new_line;
		}
		
		$self->_write_table_file($new_line."\n");
	}
	# le fichier doit être modifié en entier
	elsif ( $touched ) {
	
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
	else {
		#aucune valeur n'est modifiée
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