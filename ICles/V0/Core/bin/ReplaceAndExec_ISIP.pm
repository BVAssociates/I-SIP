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
 @EXPORT = qw(update_info insert_info delete_info);  # symbols to export
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