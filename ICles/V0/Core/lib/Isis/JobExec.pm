package JobExec;

use strict;
use Isis::JobStat;

use fields qw(
	current_process

	);


use Carp qw(carp croak );
use Scalar::Util qw(blessed);

use Win32::Process qw(STILL_ACTIVE CREATE_NEW_CONSOLE DETACHED_PROCESS CREATE_NO_WINDOW);
use Win32;

=head1 NAME

 JobExec - Gere des scripts Perl en tâche de fond
 
=head1 SYNOPSIS

 
 
=head1 DESCRIPTION


=head1 AUTHOR

BV Associates, 2009

=cut

##################################################
##  constructor  ##
##################################################

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	my $usage="usage : ".__PACKAGE__.'->new(exec_table,stat_table,log_table)';
	# créé object
	my $self= fields::new($class);
	
	# verification des parametre
	if (@_ < 0) {
		croak ($usage);
	}

	$self->{current_process}={};

    return $self;
}



##################################################
##  pivate methods  ##
##################################################


##################################################
##  public methods  ##
##################################################

sub exec_script {
	my $self=shift;
	
	my $progname=shift or croak("usage: exec_script(progname,(args,...))");
	my $args=join(' ',map {'"'.$_.'"'} @_);
	
	my $perl_interpreter='c:\Perl\bin\perl.exe';
	

	my $full_commant='perl -S -MIsis::JobStatHook=background "'.$progname.'" '.$args;
	warn("execution en tache de fond : $full_commant","\n");

	use POSIX 'strftime';
	my $timestamp=strftime("%Y%m%dT%H%M%S", localtime);
	
	Win32::Process::Create($self->{current_process},
								$perl_interpreter,
								$full_commant,
								1,
								CREATE_NO_WINDOW||CREATE_NEW_CONSOLE||DETACHED_PROCESS,
								#CREATE_NEW_CONSOLE||DETACHED_PROCESS,
								".")|| _raise_error();



	print Win32::Process::GetCurrentProcessID()."->".$self->{current_process}->GetProcessID()."\n";

	$self->{current_process}->Wait(2000);
	
	my $exitcode;
	$self->{current_process}->GetExitCode($exitcode);
	
	if ($exitcode == STILL_ACTIVE) {
		warn("Lancement en tâche de fond");
	}
	else {
		warn("Le programme s'est terminé immédiatement avec le code ".$exitcode);
		return $exitcode;
	}
	
}


1;


