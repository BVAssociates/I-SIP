package ReplaceAndExec_ISIP;

use strict;
use warnings;

use Carp;

# use applicatifs
use Isip::Environnement;
use Isip::IsipLog '$logger';
use Isip::IsipTreeCache;
use Isip::Cache::CacheStatus;
use Isip::Cache::CacheProject;
use Isip::Cache::CacheTempo;

use ITable::ITools;


our (@ISA, @EXPORT);
BEGIN {
 require Exporter;
 @ISA = qw(Exporter);
 
 # symbols to export
 @EXPORT = qw(
		update_column_info
		update_info
		insert_info
		delete_info
		update_field
		validate_line
	);
}


sub update_column_info($$) {
	
	my $table_name=shift;
	my $values=shift;


	my $environnement=$ENV{Environnement};
	die "Variable <Environnement> not set" if not $environnement;

	my $table_uptade=$ENV{TABLE_NAME}.'_COLUMN';


	my $itools_table=ITools->open($table_name);
	my $separator=$itools_table->output_separator;
	my @field=$itools_table->field;

	my $env_sip=Environnement->new($environnement);
	my $local_table;

	#recuperation des valeurs dans un hash
	my %row;
	@row{@field}=split(/$separator/, $values, -1);
	
	$local_table=$env_sip->open_local_table($table_uptade, {timeout => 10000});

	# nettoie les champs inconnus
	my %unknown_field;
	$unknown_field{$_}++ foreach @field;
	delete $unknown_field{$_} foreach $local_table->field;
	delete @row{keys %unknown_field};

	use POSIX qw(strftime);
	$row{DATE_UPDATE} = strftime "%Y-%m-%dT%H:%M", localtime if exists $row{DATE_UPDATE};
	$row{USER_UPDATE} = $ENV{IsisUser} if exists $row{USER_UPDATE};

	$logger->info("updating $values into $table_uptade");
	$local_table->update_row( %row );
}

sub update_info($$) {
	
	my $table_name=shift;
	my $values=shift;
	

	my $environnement=$ENV{Environnement};
	die "Variable <Environnement> not set" if not $environnement;

	my $itools_table=ITools->open($table_name);
	my $separator=$itools_table->output_separator;
	my @field=$itools_table->field;

	my $env_sip=Environnement->new($environnement);
	my $local_table;

	#recuperation des valeurs dans un hash
	my %row;
	@row{@field}=split(/$separator/, $values, -1);
	
	$local_table=$env_sip->open_local_table($table_name, {timeout => 10000});

	# nettoie les champs inconnus
	my %unknown_field;
	$unknown_field{$_}++ foreach @field;
	delete $unknown_field{$_} foreach $local_table->field;
	delete @row{keys %unknown_field};

	use POSIX qw(strftime);
	$row{DATE_UPDATE} = strftime "%Y-%m-%dT%H:%M", localtime if exists $row{DATE_UPDATE};
	$row{USER_UPDATE} = $ENV{IsisUser} if exists $row{USER_UPDATE};

	$logger->info("updating $values into $table_name");
	$local_table->update_row( %row );
}

sub insert_info($$) {
	
	my $table_name=shift;
	my $values=shift;
	

	my $environnement=$ENV{Environnement};
	die "Variable <Environnement> not set" if not $environnement;


	my $itools_table=ITools->open($table_name);
	my $separator=$itools_table->output_separator;
	my @field=$itools_table->field;

	my $env_sip=Environnement->new($environnement);
	my $local_table;
	my %row;

	$local_table=$env_sip->open_local_table($table_name, {timeout => 10000});

	$local_table->query_field(@field);
	%row=$local_table->array_to_hash(split(/$separator/, $values, -1));

	use POSIX qw(strftime);
	$row{DATE_UPDATE} = strftime "%Y-%m-%dT%H:%M", localtime if exists $row{DATE_UPDATE};
	$row{USER_UPDATE} = $ENV{IsisUser} if exists $row{USER_UPDATE};

	$logger->notice("inserting $values into $table_name");
	$local_table->insert_row( %row );
}

sub delete_info($$) {
	
	my $table_name=shift;
	my $values=shift;
	

	my $environnement=$ENV{Environnement};
	die "Variable <Environnement> not set" if not $environnement;


	my $itools_table=ITools->open($table_name);
	my $separator=$itools_table->output_separator;
	my @field=$itools_table->field;

	my $env_sip=Environnement->new($environnement);
	my $local_table;
	my %row;

	$local_table=$env_sip->open_local_table($table_name, {timeout => 10000});

	$local_table->query_field(@field);
	%row=$local_table->array_to_hash(split(/$separator/, $values, -1));

	$logger->info("deleting $values into $table_name");
	$local_table->delete_row( %row );
}

sub update_field($$) {
	my $table_name=shift;
	my $values=shift;
	
	if (not $table_name =~ /^IKOS_FIELD_([\w\d]+)_(\w+)$/) {
		croak("Table $table_name non géré par $0");
	}
	my ($environnement,$table_ikos) = ($1,$2);

	
	my $env_sip=Environnement->new($environnement);
	
	my $itools_table=ITools->open($table_name);
	my $separator=$itools_table->output_separator;
	my @field=$itools_table->field;
	
	
	my $local_table=$env_sip->open_local_table($table_ikos."_HISTO", {timeout => 30000});
	# add dynamic field. Needed for array_to_hash()
	$local_table->dynamic_field("TEXT","TYPE","ICON");
	$local_table->query_field(@field);
	
	my %row=$local_table->array_to_hash(split(/$separator/, $values, -1));
	
	my %row_update=%row;
	#delete non-updatable field from line to update
	foreach ($local_table->dynamic_field(),"DATE_HISTO","DATE_UPDATE","USER_UPDATE","TABLE_NAME","FIELD_NAME","FIELD_VALUE") {
		delete $row_update{$_};
	}
	
	use POSIX qw(strftime);
	my $current_date=strftime "%Y-%m-%dT%H:%M", localtime;
	my $current_user=$ENV{IsisUser};
	
	#on prepare la date
	$row_update{DATE_UPDATE} = $current_date;
	$row_update{USER_UPDATE} = $current_user;
	
	# on met à jour le champ seulement
	update_field_comment($env_sip,$local_table,$table_ikos,\%row_update);
	
	# on recherche si la ligne en cours contient les clefs primaires
	my $key_field=$env_sip->get_table_key($table_ikos);
	if ( ( $row{FIELD_NAME} eq $key_field ) and ($row{TABLE_KEY} eq $row{FIELD_VALUE} )) {

		# for other field, we don't update timestamp
		foreach ("DATE_UPDATE","USER_UPDATE") {
			delete $row_update{$_};
		}
	
		# look for field with same key, which was never updated
		my $field_table=$env_sip->open_histo_field_table($table_ikos);
		$field_table->query_key_value($row{TABLE_KEY});
		$field_table->query_condition("DATE_UPDATE IS NULL OR DATE_UPDATE = ''","FIELD_VALUE != '$row{TABLE_KEY}'");
		
		my @id_list;
		while (my %row_field=$field_table->fetch_row) {
			push @id_list, $row_field{ID};
		}
		
		#mise à jour automatique des champ de la ligne qui ne sont pas commentés
		foreach my $id (@id_list) {
			$row_update{ID}=$id;
			update_field_comment($env_sip,$local_table,$table_ikos,\%row_update);
		}
	}

}


sub update_field_comment($$$$) {
	my $env_sip=shift;
	my $local_table=shift;
	my $table_ikos=shift;
	my $row_ref=shift;

	

	my %row=%{$row_ref};
	my %old_line;
	my %new_line;

	my $histo_table=$env_sip->open_local_from_histo_table($table_ikos, {timeout => 100000});
	$histo_table->isip_rules(IsipRules->new($table_ikos,$env_sip));


	$histo_table->query_key_value($row{TABLE_KEY});
	$histo_table->query_field("ICON","PROJECT",$histo_table->field);

	# check line before
	%old_line=$histo_table->fetch_row;
	$histo_table->finish;
	$logger->notice("Ancien status de la ligne : $old_line{ICON}|$old_line{PROJECT}");


	# update field
	$logger->notice("Mise à jour du champ no $row{ID}");
	$local_table->update_row( %row );

	# check line after
	%new_line=$histo_table->fetch_row;
	$histo_table->finish;
	$logger->notice("Nouveau status de la ligne : $new_line{ICON}|$new_line{PROJECT}");

	# needed for CacheStatus
	$new_line{OLD_ICON}=$old_line{ICON};
	# needed for CacheProject
	$new_line{OLD_PROJECT}=$old_line{PROJECT};

	my $cache=IsipTreeCache->new($env_sip);
	$cache->add_dispatcher(CacheStatus->new($env_sip));
	$cache->add_dispatcher(CacheProject->new($env_sip));
	$cache->add_dispatcher(CacheTempo->new($env_sip));

	$cache->recurse_line($table_ikos, \%new_line);
	$cache->save_cache();
}

# transforme l'insertion en un appel de script à PC_VALIDATE_LINE
sub validate_line {
	my $table_name=shift;
	my $values=shift;
	
	if (not $table_name =~ /^IKOS_TABLE_([\w\d]+)_(\w+)$/) {
		croak("Table $table_name non géré par $0");
	}
	my ($environnement,$table_ikos) = ($1,$2);

	# champs "virtuels" ajouté par le Processeur IsipFormProcessorLine
	my @special_fields = ("STATUS","COMMENT","MEMO");
	
	my $env_sip=Environnement->new($environnement);
	
	my $itools_table=ITools->open($table_name);
	my $separator=$itools_table->output_separator;
	my @field=$itools_table->field;
	push @field, @special_fields;
	$itools_table->dynamic_field("PROJECT","ICON",@special_fields);
	$itools_table->query_field(@field);
	
	my %line_value=$itools_table->array_to_hash(split(/$separator/, $values, -1));
	
	# validation des champs obligatoires
	if ( ! $line_value{COMMENT} ) {
		croak("Un commentaire est obligatoire");
	}
	
	my @script_args = ($environnement, $table_ikos, $line_value{COMMENT});
	unshift @script_args, "-p".$line_value{PROJECT} if $line_value{PROJECT};
	unshift @script_args, "-m".$line_value{MEMO} if $line_value{MEMO};
	unshift @script_args, "-t".$line_value{STATUS} if $line_value{STATUS};
	unshift @script_args, "-r"; # recursive
	
	# le reste des valeur n'est pas traité (déjà le contexte et non modifé)
	require "PC_VALIDATE_LINE.pl";
	pc_validate_line::run(@script_args);
	#system( "PC_VALIDATE_LINE",@script_args );
	
}