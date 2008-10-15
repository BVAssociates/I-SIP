#!/usr/bin/perl

use strict;

use IKOS::SIP;
use IKOS::DATA::ITools;

my $environnement=shift or die "$0 ENV";

my $bv_debug=0;
my $separator=',';

# ENV info

my $def_path=sprintf("%s/%s/_Services/%s/def",$ENV{CLES_HOME},$ENV{ICleName},$ENV{ServiceName});
die "$def_path not readable" if not -r $def_path;
die "$def_path not writable" if not -w $def_path;
die "$def_path not in BV_DEFPATH" if $ENV{BV_DEFPATH} !~ /\Q$def_path\E/;

my $list_table = ITools->open("INFO_TABLE", {debug => $bv_debug });
# only table with defined primary key
$list_table->query_condition("PRIMARY_KEY != ''");
my $sip = SIP->new($environnement);

if (not defined $list_table) {
	die "error opening INFO_TABLE";
}

my $def_template = 'COMMAND="PC_LIST_TAB.pl %%ENVIRON%% %s %%DATE%%"
SEP="%s"
FORMAT="%s"
SIZE="%s"
KEY="%s"
';
my $fkey_template='FKEY="[%s] on %s[%s]"
';

# mise en mémoire des primary key
my %table_key;
while (my %info = $list_table->fetch_row() ) {
	$table_key {$info{TABLE_NAME}} = $info{PRIMARY_KEY};
}
$list_table->finish();

# creation des DEF
while (my %info = $list_table->fetch_row() ) {
	if ( not $sip->exist_local_table($info{TABLE_NAME}, { debug => $bv_debug }) ) {
		print STDERR "$info{TABLE_NAME} don't exist in local tables\n";
		next;
	}
	# open DATA table
	my $ikos_data = $sip->open_local_table($info{TABLE_NAME}, { debug => $bv_debug });
	my $ikos_data_field = join($separator,$ikos_data->field() );
	my $ikos_data_size = join($separator,('20s') x $ikos_data->field() ) ;
	
	my $def_string = sprintf ($def_template,
			$info{TABLE_NAME},
			$separator,
			$ikos_data_field,
			$ikos_data_size, 
			$info{PRIMARY_KEY});
			
	if ($info{F_KEY} and $info{F_TABLE}) {
		$def_string .= sprintf($fkey_template,
			$info{F_KEY},
			$info{F_TABLE},
			$table_key{$info{F_TABLE}});
	}
	
	print "writing to $def_path/$info{TABLE_NAME}.def\n";
	open (DEFFILE,">$def_path/$info{TABLE_NAME}.def") or die "error opening $def_path/$info{TABLE_NAME}.def : $!";
	print DEFFILE $def_string."\n";
	close DEFFILE;
}

