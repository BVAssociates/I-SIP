package Isip::IsipLog;

use strict;
use Carp qw(carp cluck confess croak );

use Log::Handler;


our $logger;

BEGIN {
	use Exporter   ();
	our (@ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS);


	@ISA         = qw(Exporter);
	@EXPORT      = qw();
	%EXPORT_TAGS = ( );     # eg: TAG => [ qw!name1 name2! ],

	# your exported package globals go here,
	# as well as any optionally exported functions
	@EXPORT_OK   = qw($logger);
	
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
		if ($logger) {
			$logger->critical( @_ );
		}
		$DIE ? $DIE->() : CORE::die();
	};

	$Carp::Internal{__PACKAGE__} = 1;

}
our @EXPORT_OK;


#Check if global logger already initialized
$logger=eval { Log::Handler->get_logger('logger') };

# if not create it
if ($@) {
	$logger=Log::Handler->create_logger('logger');
	$logger->add(screen => {
		log_to   => 'STDERR',
		newline  => 1,
		maxlevel => 'error',
		timeformat      => '%Y/%m/%d %H:%M:%S',
		message_layout  => '%T:%L:%p:%m'
		});
	$logger->add(file => {
		newline  => 1,
		maxlevel => 'debug',
		timeformat      => '%Y/%m/%d %H:%M:%S',
		message_layout  => '%T:%L:%p:%m',
		filename        => $ENV{ISIP_HOME}.'/logs/Isip.log',
		mode            => 'append',
		});
	
	
	#Log::WarnDie may be used, but it put everything from STDERR
	#in $logger->error() on STDOUT. So we must be aware of bad effect on output...
	##Log::WarnDie->dispatcher( $logger );
}

1;