package Isip::IsipLog;

# special module to trace running programs
use Isis::JobStatHook;

use strict;
use Carp qw(carp cluck confess croak );
$Carp::MaxArgNums=0;
$Carp::MaxArgLen=0;
my $package=__PACKAGE__;
$Carp::Internal{$package} = 1;

use Log::Handler;
use POSIX qw(strftime);

our $logger;

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
		
		$DIE ? $DIE->() : CORE::die(@_);
		exit 202;
	};
}


#Check if global logger already initialized
$logger=eval { Log::Handler->get_logger('logger') };

my %screen_definition=(screen => {
		log_to   => 'STDERR',
		newline  => 1,
		maxlevel => 'notice',
		timeformat      => '%Y/%m/%d %H:%M:%S',
		message_layout  => '%T:%L:%m',
		alias    => 'screen-out',
		die_on_errors => 0,
		});

my %dbi_definition=(dbi => {
				# database connection
				#data_source     => 'dbi:SQLite:dbname='.$ENV{ISIP_LOG}.'/Isip.log.sqlite',
				dbname     => $ENV{ISIP_LOG}.'/Isip.log.sqlite',
				driver     => 'SQLite',
				debug      => 0,
				table      => 'messages',
				columns    => [ qw/level cdate pid caller progname mtime message/ ],
				values     => [ qw/%level %time %pid %caller %progname %mtime %message/ ],
				persistent => 1,
				maxlevel   => 'notice',
				message_layout => '%m',
				message_pattern => '%L %T %D %P %H %C %S %t %m',
				timeformat      => '%Y/%m/%d %H:%M:%S',
				alias    => 'sqlite-out',
			});

my %file_definition=(file => {
				#fileopen => 0,
				#reopen => 0,
				autoflush => 0,
				newline  => 1,
				maxlevel => 'debug',
				timeformat      => '%Y/%m/%d %H:%M:%S',
				message_layout  => '%T:%L:%S:%m',
				filename        => $ENV{ISIP_LOG}.'/Isip.log',
				mode            => 'append',
				alias    => 'file-out',
			});
		
# if not, create it
if ($@) {
	$logger=Log::Handler->create_logger('logger');
	
	# test STDERR before using it
	$logger->add(%screen_definition) if print STDERR "";
	
	$logger->add(%file_definition) if exists $ENV{ISIP_LOG};

	#$logger->add(%dbi_definition) if exists $ENV{ISIP_LOG};

	#Log::WarnDie may be used, but it put everything from STDERR
	#in $logger->error() on STDOUT. So we must be aware of bad effect on output...
	##Log::WarnDie->dispatcher( $logger );
}


sub log_verbose() {
	$logger->set_level('screen-out' => { maxlevel => 'info'});
}

sub log_screen_only() {
	$logger=Log::Handler->create_logger('logger');
	$logger->add(%screen_definition) if print STDERR "";
	$logger->add(%dbi_definition);
}

sub no_log() {
	$logger=Log::Handler->create_logger('logger');
	$logger->add(%screen_definition) if print STDERR "";
}

if (not caller){
	$logger->notice("it is a test");
}

1;