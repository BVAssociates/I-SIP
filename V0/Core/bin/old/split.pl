open (input,"<NATPROP.OUT");
while (<input>) {
  chomp $_;
  @raw = split(/\t/, $_);
  open (output,">@raw[0].nd");
  print output "$_\n";
  close(output);
}
close(input);

