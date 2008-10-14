use Cwd;
#foreach $key (sort(keys %ENV)) {
#  print "$key = $ENV{$key}\n";
#}
$IT_TABLE=@ARGV[0];
$IT_NODE=@ARGV[1];
$IT_LEAVES="$ENV{'CLES_HOME'}"."\\"."$ENV{'ICLENAME'}"."\\"."_Services"."\\"."$ENV{'SERVICENAME'}"."\\"."tab"."\\"."$IT_TABLE"."\\"."$IT_NODE"."\.nd";
chdir($IT_LEAVES) || die "impossible de s'attacher au repertoire $IT_LEAVES";
#$HERE=getcwd;
#print "HERE=$HERE\n";
#opendir(DIR,'.') || die "impossible d'ouvrir le repertoire $IT_NODES";
#@fic= readdir(DIR);
while (<*.lf>) {
  open (LF,"$_");
#  $horodate="$_";
#  $horodate=~ s/.lf$//;
#  $date=substr($horodate,0,8);
#  $heure=substr($horodate,8,6);
#  print "Node=$_\n";
  while (<LF>) {
     print "$_\n";
  }
  close(LF);
}
