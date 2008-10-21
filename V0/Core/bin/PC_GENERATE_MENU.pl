#!/usr/bin/perl

use strict;

use IKOS::SIP;
use IKOS::DATA::ITools;

my $environnement=shift or die "ARG1: ENV";

my $bv_debug=0;


#####  BEGIN TEMPLATES ##### 
my $separator=',';

my $def_template = 'COMMAND="PC_LIST_TAB.pl %%ENVIRON%% %s %%DATE%%"
SEP="%s"
FORMAT="%s"
SIZE="%s"
KEY="%s"
';
my $fkey_def_template='FKEY="[%s] on %s[%s]"
';

my $pci_template='Item~~Champs~expl~~GSL_FILE=%s~Explore~FIELD~0~~Expand
';
my $pci_fkey_template='Item~Tables liées~%s~expl~~~Explore~%s~0~~Expand
';

##### END TEMPLATES ##### 

# ENV info

my $def_path=sprintf('%s\%s\_Services\%s\def',$ENV{CLES_HOME},$ENV{ICleName},$ENV{ServiceName});
die "$def_path not readable" if not -r $def_path;
die "$def_path not writable" if not -w $def_path;
die "$def_path not in BV_DEFPATH" if $ENV{BV_DEFPATH} !~ /\Q$def_path\E/;

my $pci_path=sprintf('%s\%s\_Services\%s\pci',$ENV{CLES_HOME},$ENV{ICleName},$ENV{ServiceName});
die "$pci_path not readable" if not -r $pci_path;
die "$pci_path not writable" if not -w $pci_path;
die "$pci_path not in BV_PCIPATH" if $ENV{BV_PCIPATH} !~ /\Q$pci_path\E/;

my $list_table = ITools->open("INFO_TABLE", {debug => $bv_debug });
# only table with defined primary key
$list_table->query_condition("PRIMARY_KEY != ''");
my $sip = SIP->new($environnement);

if (not defined $list_table) {
	die "error opening INFO_TABLE";
}

# mise en mémoire des primary key
my %table_key;
my %table_fkey;
while (my %info = $list_table->fetch_row() ) {
	$table_key {$info{TABLE_NAME}} = $info{PRIMARY_KEY};
	$table_fkey {$info{TABLE_NAME}} = $info{F_TABLE};
}
$list_table->finish();

# CREATE DEF
while (my %info = $list_table->fetch_row() ) {
	if ( not $sip->exist_local_table($info{TABLE_NAME}, { debug => $bv_debug }) ) {
		warn "$info{TABLE_NAME} don't exist in local tables\n";
		#next;
	}
	# open DATA table
	my $ikos_data = $sip->open_ikos_table($info{TABLE_NAME}, { debug => $bv_debug });
	my $ikos_data_field = join($separator,$ikos_data->field() );
	my $ikos_data_size = join($separator,('20s') x $ikos_data->field() ) ;
	
	my $def_string = sprintf ($def_template,
			$info{TABLE_NAME},
			$separator,
			$ikos_data_field,
			$ikos_data_size, 
			$info{PRIMARY_KEY});
			
	if ($info{F_KEY} and $info{F_TABLE}) {
		$def_string .= sprintf($fkey_def_template,
			$info{F_KEY},
			$info{F_TABLE},
			$table_key{$info{F_TABLE}});
	}
	
	print "writing to $def_path\\$info{TABLE_NAME}.def\n";
	open (DEFFILE,">$def_path/$info{TABLE_NAME}.def") or die "error opening $def_path\\$info{TABLE_NAME}.def : $!";
	print DEFFILE $def_string."\n";
	close DEFFILE;
}

# CREATE PCI
while (my %info = $list_table->fetch_row() ) {
	if ( not $sip->exist_local_table($info{TABLE_NAME}, { debug => $bv_debug }) ) {
		print STDERR "$info{TABLE_NAME} don't exist in local tables\n";
		next;
	}
	# open DATA table
	my $ikos_data = $sip->open_local_table($info{TABLE_NAME}, { debug => $bv_debug });
	my $ikos_data_field = join($separator,$ikos_data->field() );
	my $ikos_data_size = join($separator,('20s') x $ikos_data->field() ) ;
	
	my $pci_string = sprintf ($pci_template,
			$info{TABLE_NAME});
	
	# get all table having current table as F_KEY
	my @fkey_list;
	for my $name (keys %table_fkey) {
		push @fkey_list, $name if $table_fkey{$name} and ($table_fkey{$name} eq $info{TABLE_NAME});
	}
	
	foreach (@fkey_list) {
		$pci_string .= sprintf($pci_fkey_template,$_,$_);
	}
	
	print "writing to $pci_path\\$info{TABLE_NAME}.pci\n";
	open (DEFFILE,">$pci_path/$info{TABLE_NAME}.pci") or die "error opening $pci_path\\$info{TABLE_NAME}.def : $!";
	print DEFFILE $pci_string."\n";
	close DEFFILE;
}
=begin comment
# PCI racine
my $pci_string_module;
my @fkey_list;
	for my $name (keys %table_fkey) {
		push @fkey_list, $name if not $table_fkey{$name};
}
foreach (@fkey_list) {
		$pci_string_module .= sprintf($pci_fkey_template,$_,$_);
}
print "writing to $pci_path\\Modules.pci\n";
open (DEFFILE,">$pci_path/Modules.pci") or die "error opening $pci_path\\Modules.def : $!";
print DEFFILE $pci_string_module."\n";
close DEFFILE;