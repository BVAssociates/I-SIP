package JobExec;

use strict;

use fields qw(
	current_process
	is_running
	start_date
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
	
	# verification de la plateforme
	if ( $^O ne 'MSWin32' ) {
		die("Classe non compatible avec le système d'exploitation");
	}

	$self->{current_process}={};
	
	$self->{is_running}=0;
	$self->{start_date}=undef;

    return $self;
}



##################################################
##  accessors  ##
##################################################

sub is_running {
	my $self=shift;
	
	if($self->{is_running}) {
		return $self->{current_process}->GetProcessID();
	}
	else {
		return;
	}
}

sub start_date {
	my $self=shift;
	
	return $self->{start_date};
}


##################################################
##  public methods  ##
##################################################

sub exec_script {
	my $self=shift;
	
	my $progname=shift or croak("usage: exec_script(progname,(args,...))");
	my $args=join(' ',map {'"'.$_.'"'} @_);

	my @search_path = split(/;/ , $ENV{PATH});
	my @search_extension = split(/;/ , $ENV{PATHEXT});
	# ajoute l'extension vide à la recherche
	unshift @search_extension, '';
	
	my $perl_interpreter;
	PERL_SEARCH:
	foreach my $path ( @search_path ) {
		if ( -r $path.'\perl.exe' ) {
			$perl_interpreter = $path.'\perl.exe';
			last PERL_SEARCH;
		}
	}
	
	SCRIPT_SEARCH:
	foreach my $path ( @search_path ) {
		foreach my $extension ( @search_extension ) {
			if ( -r $path.'/'.$progname.$extension ) {
				$progname = $progname.$extension;
				last SCRIPT_SEARCH;
			}
		}
	}

	
	my $full_command='perl.exe -S -MIsis::JobStatHook=background "'.$progname.'" '.$args;
	
	use POSIX 'strftime';
	$self->{start_date}=strftime("%Y%m%dT%H%M%S", localtime);
	
	Win32::Process::Create($self->{current_process},
								$perl_interpreter,
								$full_command,
								0,
								#CREATE_NO_WINDOW||CREATE_NEW_CONSOLE||DETACHED_PROCESS,
								DETACHED_PROCESS,
								".")|| die(Win32::FormatMessage( Win32::GetLastError() ));



	# for debugging purpose
	#print Win32::Process::GetCurrentProcessID()."->".$self->{current_process}->GetProcessID()."\n";

	$self->{current_process}->Wait(2000);
	
	my $exitcode;
	$self->{current_process}->GetExitCode($exitcode);
	
	
	if ($exitcode == STILL_ACTIVE) {
		print("Lancement en tâche de fond du programme : $progname $args\n");
		$self->{is_running}=1;
	}
	else {
		print("Le programme s'est terminé immédiatement avec le code $exitcode\n");
		return $exitcode;
	}
}


sub kill_pid {
	my $self=shift;
	
	my $pid=shift or croak("usage: kill_pid(pid)");
	Win32::Process::Open($self->{current_process},
								$pid,
								0,
								)|| die(Win32::FormatMessage( Win32::GetLastError() ));


	warn("Annulation du processus : $pid");
	$self->{current_process}->Kill(201);
	$self->{is_running}=0;
	$self->{start_date}=undef;
}

1;


