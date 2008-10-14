use Cwd;
#foreach $key (sort(keys %ENV)) {
#  print "$key = $ENV{$key}\n";
#}
$IT_TABLE=@ARGV[0];
$IT_NODE=@ARGV[1];
$IT_DIR_NODES="$ENV{'CLES_HOME'}"."\\"."$ENV{'ICLENAME'}"."\\"."_Services"."\\"."$ENV{'SERVICENAME'}"."\\"."tab"."\\"."$IT_TABLE";
chdir($IT_DIR_NODES) || die "impossible de s'attacher au repertoire $IT_NODES";
#$HERE=getcwd;
#print "HERE=$HERE\n";
#opendir(DIR,'.') || die "impossible d'ouvrir le repertoire $IT_NODES";
#@fic= readdir(DIR);
if (!defined($IT_NODE)) {
   while (<*.lf>) {
     open (LF,"$_");
     while (<LF>) {
        print "$_\n";
     }
     close(LF);
   }
} else {
  $FNODE="$IT_NODE"."\.lf";
  open (LF,"$FNODE");
  while (<LF>) {
     print "$_\n";
  }
  close(LF);
}
