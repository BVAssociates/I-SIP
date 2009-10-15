
use strict;

sub load_profile($) {
	my $profile=shift;
	
	open (PROFILE,$profile) or die $!;
	while(<PROFILE>) {
		next if /^#/;
		next if /^$/;

		s/%(\w+)%/$ENV{$1}/g;
		/(?:set)?\s*([^=]+)=(.*)/;
		$ENV{$1}=$2;
		
		#print "ENV{$1}=$2\n";
	}
	close (PROFILE);

	push @INC, split(';',$ENV{PERL5LIB});
}

my $last_run_code;

sub run_cmd2($) {

    use IPC::Open3;

    my $cmd=shift;

    my($in, $out, $err);
    my $pid = open3($in, $out, $err,$cmd);

    waitpid( $pid, 0 );
	print <$out>;
    $last_run_code=$? >> 8;
    return <$out>;
}

sub run_cmd($) {

    my $cmd=shift;

    my @return=`$cmd`;
    $last_run_code=$? >> 8;

    return @return;
}

load_profile('C:\Program Files\BV Associates\I-SIS V2.0.3\Portal_test\Portal\product\conf\IsisPortal_WIN32.ini');


exec("cmd.exe");
