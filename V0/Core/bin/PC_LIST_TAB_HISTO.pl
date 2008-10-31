#!/usr/bin/perl

use strict;

use IKOS::SIP;
use IKOS::DATA::ITools;

use Data::Dumper;

my $environ=shift or die;
my $table_name=shift or die;
my $query_date=shift;

my $sip=SIP->new($environ);
my $table=$sip->open_local_from_histo_table($table_name, {debug => 1 });

die "unable to open local $table_name in env $environ" if not defined $table;

#my $itools_table=ITools->open($table_name."_HISTO",{debug => 1 });
#$table->query_field($itools_table->field);

#$table->query_field(split(/\Q|/,"DATE_COLLECTE|TIME_COLLECTE|AAPTYCOD|AAPTYLIB|AANPRCOD|AAUTILCPST|AADTECPST|AAHRECPST|AANOSQCPST"));

while (my @line=$table->fetch_row_array) {
	#print Dumper(@line);
	print join('|',@line)."\n";
}
