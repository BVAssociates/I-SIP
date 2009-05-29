package JobStat;

use strict;

use ITable::Sqlite;
our @ISA=("Sqlite");


use Carp qw(carp croak );
use Scalar::Util qw(blessed);


=head1 NAME

 JobStat - Gere des scripts Perl en tâche de fond
 
=head1 SYNOPSIS

 
 
=head1 DESCRIPTION


=head1 AUTHOR

BV Associates, 2009

=cut

##################################################
##  constructor  ##
##################################################

sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	
	my $usage="usage : ".__PACKAGE__.'->open()';
	
	
	# appelle SuperClass's constructor avec les mêmes arguments
	my $self=$class->SUPER::open(@_);
	
	# verification des parametre
	if (@_ < 0) {
		croak ($usage);
	}

	# créé object
	bless($self,$class);
	
    return $self;
}



##################################################
##  pivate methods  ##
##################################################


##################################################
##  public methods  ##
##################################################

# remove non running perl.exe
# be aware that new perl.exe process can take an old killed PID! 
sub clean_dead_process {
	my $self=shift;
	
	my %process_to_check;
	
	my @condition_save=$self->query_condition();
	$self->query_condition("CODE IS NULL");
	while (my %proc=$self->fetch_row()) {
		$process_to_check{$proc{PID}}=\%proc;
	}
	$self->query_condition(@condition_save);
	
	return if not %process_to_check;
	
	my %process_found;
	foreach(`tasklist /FO CSV /NH /FI "IMAGENAME eq perl.exe"`) {
		my ($pid)=/^\"perl.exe\",\"(\d+)\"/;
		$process_found{$pid}++ if $pid;
	}
	
	while (my ($pid,$proc)=each %process_to_check) {
		if (not exists $process_found{$pid}) {
			$self->_info("process $pid seems to be dead, cleaning.");
			$proc->{CODE}=-1;
			$self->update_row(%$proc);
		}
	}

}

sub create_database {
	my $self=shift;
	
	my $obj=$self->SUPER::create_database(@_);
	
	$obj->execute('CREATE TABLE "SCRIPT_STAT" ("TIMESTAMP" TEXT NOT NULL ,"PID" INTEGER,"USER" TEXT,"PROGRAM" TEXT,"TIME" FLOAT,"ARGV" TEXT,"CODE" INTEGER,"BACKGROUND" INTEGER, PRIMARY KEY ("TIMESTAMP","PID"))');
	return $obj;
}

1;


