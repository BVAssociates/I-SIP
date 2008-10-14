#!/usr/bin/perl

use strict;

use IKOS::SIP;
use IKOS::DATA::ITools;

use POSIX qw(strftime);
    


my $environnement=shift or die;
my $table_name=shift;

my $env_sip = SIP->new($environnement);

my $db2_table = ITools->open("INFO_TABLE");
$db2_table->query_condition("TABLE_NAME = '$table_name'");


while (my %db2_table_line = $db2_table->fetch_row() ) {

	my $table_name=$db2_table_line{TABLE_NAME};
	
	#open IKOS table for DATA
	my $current_table=$env_sip->open_local_table($table_name);
	my $histo_table=$env_sip->open_histo_table($table_name);
	my $table_key= $db2_table_line{PRIMARY_KEY} ;
	
	if (not $table_key) {
		warn "No KEY defined for $table_name\n";
		next;
	}
	
	#$current_table->debugging(1);
	#$histo_table->debugging(1);

	my $date_current = strftime "%Y-%m-%d %H:%M:%S", localtime;
	my @field_list=$current_table->query_field();
	my @row_list;
	while (my %data_line=$current_table->fetch_row() ) {
		my $current_line_key=$data_line{$table_key};
		foreach my $field (keys %data_line) {
			push @row_list, { DATE_HISTO => $date_current,
										ENVIRONNEMENT => $environnement,
			                            TABLE_NAME => $table_name,
										TABLE_KEY => $current_line_key,
										FIELD_NAME => $field,
										FIELD_VALUE => $data_line{$field} };
			
		}
	}
	
	foreach my $row (@row_list) {
		$histo_table->insert_row( %$row );
	}
	
}