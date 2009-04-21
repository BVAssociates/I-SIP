package HistoColumns;

use strict;

use ITable::Sqlite;
use Isip::IsipLog '$logger';

use Carp qw(carp cluck confess croak );
use Scalar::Util qw(blessed);

use ITable::IColumns;
use base qw(IColumns);
use fields qw(
	date_histo
	column_table
	database_name
	query_date
);

#use Data::Dumper;

##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;	
	
	# mandatory parameter
	if (@_ < 2) {
		croak ('\'new\' take 2 mandatory argument: ${class}->open("databasename","tablename"[ ,{ timeout => $sec, debug => $num} ])')
	}
	
	my $self= fields::new($class);
	
	# virtual informations
    my $database_name = shift;
	my $table_name = shift;
	my $options=shift;
	
	$self->SUPER::new($table_name,$options);
	
	# members initialization
	$self->{database_name} = $database_name;
		
	$self->{date_histo} = {};
	
	$self->{query_date} = $options->{date} if exists $options->{date};
	
	$self->{column_table}=Sqlite->open($self->{database_name}, $self->{table_name}."_COLUMN");
	
	$self->_load_columns();
	
	return $self;
}

##################################################
##  pivate methods  ##
##################################################

sub get_query() {
	my $self=shift;
	
	my @where_clause;
	
	push @where_clause, "TABLE_NAME='".$self->{table_name}."'";
	push @where_clause, "DATE_HISTO <= '".$self->query_date()."'" if $self->query_date();
	
	# SQL join to get last inserted FIELD_NAME
	my $select_histo="SELECT * FROM ".$self->{table_name}."_COLUMN INNER JOIN
	(SELECT max(DATE_HISTO) AS DATE_HISTO, FIELD_NAME FROM ".$self->{table_name}."_COLUMN";
	
	# Add where clause
	$select_histo .= "
	WHERE ".join(" AND ", @where_clause);
	
	$select_histo .= "GROUP BY FIELD_NAME ) USING (DATE_HISTO,FIELD_NAME)\n"
		."WHERE COLNO > 0\n"
		."ORDER BY COLNO";
		
	return $select_histo;
}

# reset and reload definition from Sqlite
sub _load_columns() {
	my $self=shift;
	
	$self->{column_table}->custom_select_query($self->get_query());
	
	$self->SUPER::new($self->{table_name});
	
	while (my %row=$self->{column_table}->fetch_row) {
		
		my %field_option;
		
		$self->{type}->{$row{FIELD_NAME}}=$row{TYPE};
		$self->{date_histo}->{$row{FIELD_NAME}}=$row{DATE_HISTO};
		
		$field_option{description}=$row{TEXT} if $row{TEXT};
		$field_option{size} = $row{DATA_TYPE} if $row{DATA_TYPE};
		$field_option{size} .= "(".$row{DATA_LENGTH}.")" if $row{DATA_LENGTH};
		
		if ($row{PRIMARY_KEY}) {
			$field_option{key}=1;
			$self->{type}->{$row{FIELD_NAME}}="clef";
		}
		
		$self->SUPER::add_column($row{FIELD_NAME}, \%field_option);
			
		if ($row{FOREIGN_TABLE} and $row{FOREIGN_KEY}) {
			$self->{ilink_obj}->add_link($row{TABLE_NAME},$row{FIELD_NAME},$row{FOREIGN_TABLE},$row{FOREIGN_KEY});
		}
	}
}

##################################################
##  param methods  ##
##################################################

sub query_date {
    my $self = shift;
    if (@_) {
		my $datetime=shift;
		# ISO 8601 format : 1977-04-22T06:00 or 19770422T0600
		if ( $datetime !~ /\d{4}-?\d{2}-?\d{2}T\d{2}:?\d{2}/) {
			$self->_error("datetime must be like 1977-04-22T06:00 or 19770422T0600 (ISO 8601)");
			croak("usage : query_date(datetime)")
		}
		
		# reformat date
		$datetime =~ s/(\d{4})-?(\d{2})-?(\d{2})T(\d{2}):?(\d{2})/$1-$2-$3T$4:$5/;
		
		$self->{query_date} = $datetime;
		
		# reload columns with new param
		$self->_load_columns();
	}
    return $self->{query_date} ;
}

##################################################
##  get methods  ##
##################################################



##################################################
##  structure modify methods  ##
##################################################

# add field and set
sub update_column() {
	my $self = shift;
	
	my $field=shift or croak('usage : update_column(field [, {size => x, date => x, colno => x, foreign_table => x, foreign_field => x, type => x, key => x, description => x} ])' );
	my $field_option=shift;
	
	if (not grep {$_ eq $field} $self->get_field_list()) {
		croak("impossible de mettre à jour le champ $field, car il n'existe pas");
	}
	else {
		$logger->info("champ $field : MISE A JOUR");
	}
	
	my %new_field;
	$new_field{FIELD_NAME}=$field;
	$new_field{TABLE_NAME}=$self->{table_name};
	
	if ($field_option->{date}) {
		$new_field{DATE_HISTO}=$field_option->{date};
	}
	else {
		$new_field{DATE_HISTO}=$self->{date_histo}->{$field};
	}
	
	if ($field_option->{colno}) {
		# TODO : reorg all COLNO after each update
		#$new_field{COLNO}=$field_option->{colno};
	}
	
	$new_field{TEXT}=$field_option->{description} if $field_option->{description};
	$new_field{FOREIGN_TABLE}=$field_option->{foreign_table} if $field_option->{foreign_table};
	$new_field{FOREIGN_KEY}=$field_option->{foreign_field} if $field_option->{foreign_field};
	$new_field{PRIMARY_KEY}=1 if $field_option->{key};
	
	if (exists $field_option->{size} and $field_option->{size} =~ /(\w+)\((\d+)\)/) {
		$new_field{DATA_TYPE}=$1;
		$new_field{DATA_LENGTH}=$2;
	}
	
	$self->{column_table}->begin_transaction();
	$self->{column_table}->update_row(%new_field);

	$self->SUPER::add_column($field,$field_option);
	$self->{type}->{$field}=$field_option->{type} if $field_option->{type};
	
	$self->{column_table}->commit_transaction();
	
	return 1;
}

# add field and set
sub add_column() {
	my $self = shift;
	
	my $field=shift or croak('usage : add_column(field [, {size => x, date => x, colno => x, foreign_table => x, foreign_field => x, type => x, key => x, description => x} ])' );
	my $field_option=shift;
	
	if (grep {$_ eq $field} $self->get_field_list()) {
		croak("impossible d'ajouter le champ $field, car il existe déjà");
		return;
	}
	else {
		$logger->notice("champ $field : AJOUT");
	}
	
	my %new_field;
	$new_field{FIELD_NAME}=$field;
	$new_field{TABLE_NAME}=$self->{table_name};
	
	if ($field_option->{date}) {
		$new_field{DATE_HISTO}=$field_option->{date};
	}
	else {
		use POSIX qw(strftime);
		$new_field{DATE_HISTO}=strftime "%Y-%m-%dT%H:%M", localtime;
	}
	
	$new_field{TEXT}=$field_option->{description};
	$new_field{FOREIGN_TABLE}=$field_option->{foreign_table};
	$new_field{FOREIGN_KEY}=$field_option->{foreign_field};
	$new_field{PRIMARY_KEY}=1 if $field_option->{key};
	
	if (exists $field_option->{size} and $field_option->{size} =~ /(\w+)\((\d+)\)/) {
		$new_field{DATA_TYPE}=$1;
		$new_field{DATA_LENGTH}=$2;
	}
	

	$self->SUPER::add_column($field,$field_option);
	$self->{type}->{$field}=$field_option->{type} if $field_option->{type};
	$self->{date_histo}->{$field}=$new_field{DATE_HISTO};
	
	if ($field_option->{colno}) {
		$new_field{COLNO}=$field_option->{colno};
		# shift COLNO of other lines
		$self->{column_table}->execute("UPDATE ".$self->{table_name}."_COLUMN SET COLNO=COLNO+1 WHERE COLNO >= ".$new_field{COLNO}
									." AND EXISTS (SELECT * FROM ".$self->{table_name}."_COLUMN WHERE COLNO = ".$new_field{COLNO}.")");
	}
	else {
		$new_field{COLNO}="!(SELECT coalesce(MAX(COLNO),0)+1 from ".$self->{table_name}."_COLUMN)";
	}
	
	# update row
	$self->{column_table}->insert_row(%new_field);
	
	return 1;
}

sub remove_column() {
	my $self = shift;
	my $field=shift or croak("usage : remove_field(field)");
	my $field_option=shift;
	
	if (grep {$_ eq $field} $self->get_field_list()) {
		$logger->notice("champ $field : SUPPRESSION");
	}
	else {
		croak("impossible de supprimer le champ $field, car il n'existe pas");
		return;
	}
	
	if (grep {$_ eq $field} $self->get_key_list()) {
		croak("impossible de supprimer le champ $field car c'est une clef primaire");
	}
	
	my %delete_field;
	$delete_field{FIELD_NAME}=$field;
	$delete_field{TABLE_NAME}=$self->{table_name};
	$delete_field{COLNO}=0;
	
	if ($field_option->{date}) {
		$delete_field{DATE_HISTO}=$field_option->{date};
	}
	else {
		use POSIX qw(strftime);
		$delete_field{DATE_HISTO}=strftime "%Y-%m-%dT%H:%M", localtime;
	}
	
	$self->SUPER::remove_column($field,$field_option);
	delete $self->{type}->{$field};

	$self->{column_table}->insert_row(%delete_field);
	
	return 1;
}


=head1 NAME

 Isip::HistoColumns - Handle "field" member of an Histo object
 
=head1 SYNOPSIS


=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut

# test case
if (!caller) {
	# gruick to to reload lib again
	$INC{"Isip/HistoColumns.pm"}++;
	
	require Isip::Environnement;
	my $env=Environnement->new("PRD");
	my $test=HistoColumns->new($env->get_sqlite_path("NATPROP_COLUMN"), "NATPROP");
	
	#$test->query_date("2009-04-11T11:00");
	#$test->add_column("test", {foreign_table => "toto", foreign_field => "tata", type => "nope", key => 0});
	#$test->update_column("test", {foreign_table => "toto2", foreign_field => "tata", type => "nope", key => 0});
	#$test->remove_column("test");
	use Data::Dumper;
	my @fields=$test->get_field_list;
	my %field_txt=$test->get_field_txt_hash;
	my $links=$test->get_links;
	die Dumper(\@fields, \%field_txt, $links);
}
1;
