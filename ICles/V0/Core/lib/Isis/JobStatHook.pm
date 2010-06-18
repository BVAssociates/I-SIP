package Isis::JobStatHook;

use strict;

#use POSIX 'strftime';

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
my $start_date;
BEGIN {
    use Exporter   ();
    our (@ISA, @EXPORT_OK);
    @ISA         = qw(Exporter);
    @EXPORT_OK   = qw(background timestamp);
}


# BEGIN STATISTIC LOGGER
my $output_file_path;
my $stat_base;
my $start;
my $pid;

BEGIN {
	my @excluded_progname = ("Define_Table", "Get_PCI");
	
	$stat_base=$ENV{ISIP_DATA}.'/tab/SCRIPT_STAT.sqlite';
	if (-r $stat_base) {

		my $args=join(' ',@ARGV);
		my (undef,undef,$progname)=File::Spec->splitpath( $0 );
		
		# sauf pour les programmes exclus
		if ( ! grep { $progname =~ /^$_/i } @excluded_progname ) {
			
			$pid=abs($$);
			
			$start=time();
			
			my ($sec,$min,$hour,$mday,$mon,$year)=localtime($start);
			$start_date=sprintf("%04d%02d%02dT%02d%02d%02d-%d", $year+1900, ++$mon, $mday, $hour, $min, $sec, $pid );
			
			my $user_name;
			if (exists $ENV{IsisUser}) {
				$user_name=$ENV{IsisUser};
			}
			else {
				$user_name=$ENV{USERDOMAIN}."\\".$ENV{USERNAME};
			}

			my $output_file;
			if ($ENV{OUTPUT_FILE}) {
				if (not -w $ENV{ISIP_DATA}.'/export') {
					die("impossible d'ecrire dans le répertoire ".$ENV{ISIP_DATA}.'/export'," : ",$!);
				}
				
				my ($vol,$dir,undef)=File::Spec->splitpath($ENV{ISIP_DATA}.'/export',1);
				$output_file=$start_date.'_'.$ENV{OUTPUT_FILE};
				$output_file =~ s/[:-]//g;
				
				$output_file_path=File::Spec->catpath($vol,$dir,$output_file);
			}
			
			require DBI;
			my $database=DBI->connect("dbi:SQLite:dbname=$stat_base","","");
			my $req=$database->prepare('insert into SCRIPT_STAT (TIMESTAMP, USER, PROGRAM, PID, ARGV, OUTPUT_FILE)'
								.' values (?,?,?,?,?,?)');
			if ($req) {
				$req->execute($start_date,$user_name,$progname,$pid,$args,$output_file);
			}
			$database->disconnect;
			undef $database;
		}
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
		
		if ($output_file_path) {
			# "touch" the file
			print "touch $output_file_path";
			open TOUCH, '>', $output_file_path or die ($output_file_path, ' ; ',$!);
			close TOUCH;
			
			if ( -r $output_file_path ) {
				close STDOUT;
				open STDOUT, '>', $output_file_path or die ($output_file_path, ' ; ',$!);
			}
			else {
				die ("Probleme lors de la création de $output_file_path");
			}
			
		}
		else {
			close STDOUT;
		}

		close STDIN;
		close STDERR;
	}
}

# public accessor
sub timestamp {
	return $start_date;
}

1;