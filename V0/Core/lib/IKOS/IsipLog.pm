package IKOS::IsipLog;

use strict;
use Carp qw(carp cluck confess croak );

use Log::Handler;

BEGIN {
	use Exporter   ();
	our (@ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS);


	@ISA         = qw(Exporter);
	@EXPORT      = qw();
	%EXPORT_TAGS = ( );     # eg: TAG => [ qw!name1 name2! ],

	# your exported package globals go here,
	# as well as any optionally exported functions
	@EXPORT_OK   = qw($logger);
}
our @EXPORT_OK;

our $logger;

#Check if global logger already initialized
$logger=eval { Log::Handler->get_logger('logger') };

# if not create it
if ($@) {
	$logger=Log::Handler->create_logger('logger');
	$logger->add(screen => {
		log_to   => 'STDERR',
		newline  => 1,
		maxlevel => 'info',
		timeformat      => '%Y/%m/%d %H:%M:%S',
		message_layout  => '%T:%L:%p:%m'
		});
	$logger->add(file => {
		newline  => 1,
		maxlevel => 'debug',
		timeformat      => '%Y/%m/%d %H:%M:%S',
		message_layout  => '%T:%L:%p:%m',
		filename        => 'file.log',
		mode            => 'append'
		});
}

1;