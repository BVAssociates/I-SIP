require PAR;
use File::Spec::Functions qw/splitpath catpath/;
my ($lecteur,$path,$script)=splitpath($0);
my $par=catpath($lecteur,$path,"IsipPackage.par" );
PAR->import( { file => $par, run => "IS_ROOT_TABLE.pl" } );
