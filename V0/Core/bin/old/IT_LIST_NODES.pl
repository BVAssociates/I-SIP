use Cwd;
#foreach $key (sort(keys %ENV)) {
#  print "$key = $ENV{$key}\n";
#}
$IT_TABLE=@ARGV[0];
$IT_NODES="$ENV{'CLES_HOME'}"."\\"."$ENV{'ICLENAME'}"."\\"."_Services"."\\"."$ENV{'SERVICENAME'}"."\\"."tab"."\\"."$IT_TABLE";
chdir($IT_NODES) || die "impossible de s'attacher au repertoire $IT_NODES";
#$HERE=getcwd;
#print "HERE=$HERE\n";
#opendir(DIR,'.') || die "impossible d'ouvrir le repertoire $IT_NODES";
#@fic= readdir(DIR);
while (<*.nd>) {
  $_=~ s/\.nd$//;
  print "$_\n";
}
