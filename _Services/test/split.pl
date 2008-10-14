open (input,"<ETPTYPP.OUT");
while (<input>) {
  chomp $_;
  @raw = split(/\t/, $_);
  open (lfio,">@raw[0].lf");
  $_=~ s/\t/;/g;
  print lfio "20080716;140502;$_\n";
  close(lfio);
  open (hstio,">@raw[0].hst");
  @datelf=('20080704090503','20080704100002','20080709110000','20080715101500');
  foreach $lf (@datelf) {
     $date=substr($lf,0,8);
     $heure=substr($lf,8,6);
     print hstio "$date;$heure;$_\n";
  }
  close(hstio);
}
close(input);

