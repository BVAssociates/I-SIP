package Isip::IsipLog;

use strict;
use Carp qw(carp cluck confess croak );
$Carp::MaxArgNums=0;
$Carp::MaxArgLen=0;

use Log::Handler;
use POSIX qw(strftime);

our $logger;

# BEGIN STATISTIC LOGGER
my $start;
my $log;
my $stat_sep;
my $args;
END {
	my $user_name=$ENV{IsisUser};
	$user_name=$ENV{USERDOMAIN}."\\".$ENV{USERNAME} if not $user_name;
	$log = Log::Handler->new();
	$log->add(file => {
		filename => $ENV{ISIP_DATA}.'/tab/SCRIPT_STAT',
		mode     => 'append',
		autoflush => 1,
		maxlevel => 'debug',
		minlevel => 'warning',
		message_layout => join($stat_sep,'%T',$user_name,'%S','%P','%r','%m'),
		timeformat      => '%Y%m%dT%H%M%S',
		newline  => 1,
	});
	$log->info(join($stat_sep,$args,$?,$start));
}

BEGIN {
	$args=join(' ',@ARGV);
	$stat_sep="££";

	$start=strftime "%Y%m%dT%H%M%S", localtime;
	
    #$log->info($args.$stat_sep.$stat_sep."starting");
}
# END STATISTIC LOGGER

BEGIN {
	use Exporter   ();
	our (@ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS);


	@ISA         = qw(Exporter);
	@EXPORT      = qw(log_screen_only);
	%EXPORT_TAGS = ( );     # eg: TAG => [ qw!name1 name2! ],

	# your exported package globals go here,
	# as well as any optionally exported functions
	@EXPORT_OK   = qw($logger no_log);
	
	# DIE and WARN trap
	
	#  Save current __WARN__ setting
	#  Replace it with a sub that
	#   If there is a dispatcher
	#    Remembers the last parameters
	#    Dispatches a warning message
	#   Executes the standard system warn() or whatever was there before

	my $WARN = $SIG{__WARN__};
	$SIG{__WARN__} = sub {
		if ($logger) {
			$logger->warning( @_ );
		}
		#$WARN ? $WARN->( @_ ) : CORE::warn( @_ );
	};

	#  Save current __DIE__ setting
	#  Replace it with a sub that
	#   If there is a dispatcher
	#    Remembers the last parameters
	#    Dispatches a critical message
	#   Executes the standard system die() or whatever was there before

	my $DIE = $SIG{__DIE__};
	$SIG{__DIE__} = sub {
	
		# check if we are in eval { }
		return if $^S;
		
		if ($logger) {
			$logger->critical( @_ );
		}
		
		#$DIE ? $DIE->() : CORE::die();
		exit 202;
	};

	$Carp::Internal{__PACKAGE__} = 1;

}
our @EXPORT_OK;


#Check if global logger already initialized
$logger=eval { Log::Handler->get_logger('logger') };

# if not, create it
if ($@) {
	$logger=Log::Handler->create_logger('logger');
	$logger->add(screen => {
		log_to   => 'STDERR',
		newline  => 1,
		maxlevel => 'notice',
		timeformat      => '%Y/%m/%d %H:%M:%S',
		message_layout  => '%T:%L:%m',
		alias    => 'screen-out',
		});
	$logger->add(file => {
		#fileopen => 0,
		#reopen => 0,
		autoflush => 0,
		newline  => 1,
		maxlevel => 'info',
		timeformat      => '%Y/%m/%d %H:%M:%S',
		message_layout  => '%T:%L:%S:%m',
		filename        => $ENV{ISIP_LOG}.'/Isip.log',
		mode            => 'append',
		alias    => 'file-out',
		}) if exists $ENV{ISIP_LOG};
	
	
	#Log::WarnDie may be used, but it put everything from STDERR
	#in $logger->error() on STDOUT. So we must be aware of bad effect on output...
	##Log::WarnDie->dispatcher( $logger );
}

sub log_screen_only() {
	$logger=Log::Handler->create_logger('logger');
	$logger->add(screen => {
		log_to   => 'STDERR',
		newline  => 1,
		maxlevel => 'notice',
		timeformat      => '%Y/%m/%d %H:%M:%S',
		message_layout  => '%T:%L:%m',
		alias    => 'screen-out',
		});
}

sub no_log() {
	$logger=Log::Handler->create_logger('logger');
	$logger->add(screen => {
		log_to   => 'STDERR',
		newline  => 1,
		maxlevel => 'warning',
		timeformat      => '%Y/%m/%d %H:%M:%S',
		message_layout  => '%T:%L:%m',
		alias    => 'screen-out',
		});
}

1;