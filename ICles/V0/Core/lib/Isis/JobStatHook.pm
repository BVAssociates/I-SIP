package Isis::JobStatHook;

use strict;

use POSIX 'strftime';

use DBI;
use File::Spec;


# don't work every time!
use sigtrap qw(die normal-signals);

=head1 NAME

 JobStat - Module spécial stockant des informations sur les processus
 
=head1 SYNOPSIS

 use Isis::JobStat;

ou bien

 perl -MIsis::JobStat script.pl
 
=head1 DESCRIPTION


=head1 AUTHOR

BV Associates, 2009

=cut

# stuff to make standard module
my $background;
my $start_date;
BEGIN {
    use Exporter   ();
    our (@ISA, @EXPORT_OK);
    @ISA         = qw(Exporter);
    @EXPORT_OK   = qw(background timestamp);
}


# BEGIN STATISTIC LOGGER
my $stat_base;
my $start;
my $pid;

BEGIN {
	$stat_base=$ENV{ISIP_DATA}.'/tab/SCRIPT_STAT.sqlite';
	if (-r $stat_base) {

		my $args=join(' ',@ARGV);
		my (undef,undef,$progname)=File::Spec->splitpath( $0 );
		$pid=$$;
		
		$start=time();
		$start_date=strftime "%Y%m%dT%H%M%S", localtime($start);
		
		my $user_name;
		if (exists $ENV{IsisUser}) {
			$user_name=$ENV{IsisUser};
		}
		else {
			$user_name=$ENV{USERDOMAIN}."\\".$ENV{USERNAME};
		}
		
		warn ("background!") if $background;
		my $database=DBI->connect("dbi:SQLite:dbname=$stat_base","","");
		my $req=$database->prepare('insert into SCRIPT_STAT (TIMESTAMP, USER, PROGRAM, PID, ARGV)'
							.' values (?,?,?,?,?)');
		if ($req) {
			$req->execute($start_date,$user_name,$progname,$pid,$args);
		}
		$database->disconnect;
		undef $database;
	}
}



END {
	
	if ($start_date) {
		
		my $database=DBI->connect("dbi:SQLite:dbname=$stat_base","","");
		my $req=$database->prepare('update SCRIPT_STAT set TIME=?, CODE=?'
							." where TIMESTAMP='$start_date' AND PID=$pid");
		if ($req) {
			my $elapsed=time()-$start;
			my $exit_code=$?;
			$req->execute($elapsed,$exit_code);
		}
		
		$database->disconnect;
		undef $database;
	}
}
# END STATISTIC LOGGER


# special import method
sub import() {
	my @params=@_;

	# leave the Exporter working
	Isis::JobStatHook->export_to_level(1, @_);
	
	shift @params;
	if (grep {$_ eq 'background'} @params) {
		# import has param, it must be a backgrounding process
		
		my $database=DBI->connect("dbi:SQLite:dbname=$stat_base","","");
		my $req=$database->prepare('update SCRIPT_STAT set BACKGROUND=?'
							." where TIMESTAMP=? AND PID=?");
		$req->execute(1,$start_date, $pid);
		$database->disconnect;
		undef $database;
		
		close STDIN;
		close STDOUT;
		close STDERR;
	}
}

# public accessor
sub timestamp {
	return $start_date;
}

1;