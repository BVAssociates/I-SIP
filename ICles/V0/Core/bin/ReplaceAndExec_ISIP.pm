package ReplaceAndExec_ISIP;

use strict;
use warnings;

# use applicatifs
use Isip::Environnement;


our (@ISA, @EXPORT);
BEGIN {
 require Exporter;
 @ISA = qw(Exporter);
 @EXPORT = qw(update_info update_comment);  # symbols to export
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

	$local_table->update_row( %row );
}

sub update_comment(%) {
	
}