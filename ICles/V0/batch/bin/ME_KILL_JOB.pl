require PAR;
use File::Spec::Functions qw/splitpath catpath/;
my ($lecteur,$path,$script)=splitpath($0);
my $par=catpath($lecteur,$path,"IsipPackage.par" );
PAR->import( { file => $par, run => "ME_KILL_JOB.pl" } );
