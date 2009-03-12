package ReplaceAndExec_ISIP;

use strict;
use warnings;

# use applicatifs
use Isip::Environnement;
use Isip::IsipLog '$logger';

our (@ISA, @EXPORT);
BEGIN {
 require Exporter;
 @ISA = qw(Exporter);
 @EXPORT = qw(update_info insert_info delete_info update_field);  # symbols to export
}


sub update_info($$) {
	
	my $table_name=shift;
	my $values=shift;
	

	my $environnement=$ENV{Environnement};
	die "Variable <Environnement> not set" if not $environnement;



	use ITable::ITools;
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

	$logger->info("updating $values into $table_name");
	$local_table->update_row( %row );
}

sub insert_info($$) {
	
	my $table_name=shift;
	my $values=shift;
	

	my $environnement=$ENV{Environnement};
	die "Variable <Environnement> not set" if not $environnement;



	use ITable::ITools;
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

	$logger->info("inserting $values into $table_name");
	$local_table->insert_row( %row );
}

sub delete_info($$) {
	
	my $table_name=shift;
	my $values=shift;
	

	my $environnement=$ENV{Environnement};
	die "Variable <Environnement> not set" if not $environnement;



	use ITable::ITools;
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
	
	if (not $table_name =~ /^IKOS_FIELD_([[:alpha:]]+)_(\w+)$/) {
		croak("Table $table_name non g�r� par $0");
	}
	my ($environnement,$table_ikos) = ($1,$2);

	use Isip::Environnement;
	my $env_sip=Environnement->new($environnement);
	
	use ITable::ITools;
	my $itools_table=ITools->open($table_name);
	my $separator=$itools_table->output_separator;
	my @field=$itools_table->field;
	
	
	my $local_table=$env_sip->open_local_table($table_ikos."_HISTO", {timeout => 30000});
	# add dynamic field. Needed for array_to_hash()
	$local_table->dynamic_field("TEXT","TYPE","ICON");
	$local_table->query_field(@field);
	
	my %row=$local_table->array_to_hash(split(/$separator/, $values, -1));
	
	#delete dynamic field from line to insert
	foreach ($local_table->dynamic_field()) {
		delete $row{$_};
	}
	
	use POSIX qw(strftime);
	my $current_date=strftime "%Y-%m-%dT%H:%M", localtime;
	my $current_user=$ENV{IsisUser};
	
	#on prepare la date
	$row{DATE_UPDATE} = $current_date;
	$row{USER_UPDATE} = $current_user;
	
	# on met � jour le champ seulement
	update_field_comment($env_sip,$local_table,$table_ikos,\%row);
	
	# on recherche si la ligne en cours contient les clefs primaires
	if ( $row{TABLE_KEY} eq $row{FIELD_VALUE} ) {
		
		# don't update this fields
		foreach ("DATE_HISTO","DATE_UPDATE","USER_UPDATE","FIELD_NAME","FIELD_VALUE") {
			delete $row{$_};
		}
		
		# look for field with same key, which was never updated
		my $field_table=$env_sip->open_histo_field_table($table_ikos);
		$field_table->query_key_value($row{TABLE_KEY});
		$field_table->query_condition("DATE_UPDATE IS NULL OR DATE_UPDATE = ''","FIELD_VALUE != '$row{TABLE_KEY}'");
		
		my @id_list;
		while (my %row_field=$field_table->fetch_row) {
			push @id_list, $row_field{ID};
		}
		
		#mise � jour automatique des champ de la ligne qui ne sont pas comment�s
		foreach my $id (@id_list) {
			$row{ID}=$id;
			update_field_comment($env_sip,$local_table,$table_ikos,\%row);
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
	$logger->notice("Mise � jour du champ no $row{ID}");
	$local_table->update_row( %row );

	# check line after
	%new_line=$histo_table->fetch_row;
	$histo_table->finish;
	$logger->notice("Nouveau status de la ligne : $new_line{ICON}|$new_line{PROJECT}");

	
	# update cache
	use Isip::IsipTreeCache;
	use Isip::Cache::CacheStatus;

	# needed for CacheStatus
	$new_line{OLD_ICON}=$old_line{ICON};
	# needed for CacheProject
	$new_line{OLD_PROJECT}=$old_line{PROJECT};

	my $cache=IsipTreeCache->new($env_sip);
	$cache->add_dispatcher(CacheStatus->new($env_sip));
	$cache->add_dispatcher(CacheProject->new($env_sip));

	$cache->recurse_line($table_ikos, \%new_line);
	$cache->save_cache();
}
