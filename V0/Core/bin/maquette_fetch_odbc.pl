#!/usr/bin/perl

use Data::Dumper;

foreach (@ARGV){
	$TABLE=$_;
}
use Win32::ODBC;
$DSN="DSN=IKOS_DEV";
$db=new Win32::ODBC($DSN);
$db->Sql("Select * FROM $TABLE");
#$db->DumpData;
$db->FetchRow();
foreach $fd ($db->FieldNames())
{
   printf("%s\t",$fd);
}
   printf("\n");
foreach $fd ($db->FieldNames())
{
   printf("%s\t",$db->Data($fd));
}
printf("\n");
while ($db->FetchRow()) {
   foreach $fd ($db->FieldNames())
   {
      #printf("%s=%s\n",$fd,$db->Data($fd));
      printf("%s\t",$db->Data($fd));
   }
   printf("\n");
}
$db->Close();	
