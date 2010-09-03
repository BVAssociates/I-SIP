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

# try to retrive timestamp from approx date and pid
sub _guess_timestamp {
	my $self=shift;
	
	my $timestamp_min=shift;
	my $pid=shift;
	
	$self->select_custom_query("SELECT FROM SCRIPT_STAT"
		."	WHERE TIMESTAMP > '$timestamp_min'"
		."	AND PID=$pid"
		."	ORDER BY TIMESTAMP DESC LIMIT 1"
		);
		
	my $quess_timestamp;
	while (my %proc=$self->fetch_row()) {
		$quess_timestamp=proc{$proc{TIMESTAMP}};
	}
	$self->select_custom_query(undef);
	
	return $quess_timestamp;
}

##################################################
##  public methods  ##
##################################################

# remove non running perl.exe
# be aware that new perl.exe process can take an old killed PID! 
sub clean_dead_process {
	my $self=shift;
	
	my %process_to_check;
	
	my @field_save=$self->query_field();
	my @condition_save=$self->query_condition();
	
	$self->query_field($self->field);
	$self->query_condition("CODE IS NULL");
	while (my %proc=$self->fetch_row()) {
		$process_to_check{$proc{PID}}=\%proc;
	}
	$self->query_condition(@condition_save);
	$self->query_field(@field_save);
	
	return if not %process_to_check;
	
	my $no_tasklist;
	#my @process_list = `tasklist /FO CSV /NH /FI "IMAGENAME eq perl.exe"`;
	my @process_list = `pslist -accepteula perl -accepteula`;
	if ($? == -1 || ($?>>8) ne 0) {
		die("<pslist> n'est pas disponible. Accès aux processus impossible.");
	}
	
	# nettoyage de la sortie
	chomp(@process_list);
	shift @process_list for (1..3);
	
	my %process_found;
	foreach my $process (@process_list) {
		my (undef,$pid) = split(/\s+/ , $process);
		
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

sub purge {
	my $self=shift;
	
	my $limit_timestamp=shift or croak("usage : purge(limit_timestamp)");
	
	$self->execute("DELETE FROM ".$self->table_name." WHERE TIMESTAMP < '$limit_timestamp'");
	
}

sub create_database {
	my $self=shift;
	
	my $obj=$self->SUPER::create_database(@_);
	
	$obj->execute('CREATE TABLE "SCRIPT_STAT" ("TIMESTAMP" TEXT NOT NULL ,"PID" INTEGER,"USER" TEXT,"PROGRAM" TEXT,"TIME" FLOAT,"ARGV" TEXT,"CODE" INTEGER,"OUTPUT_FILE" TEXT,"BACKGROUND" INTEGER, PRIMARY KEY ("TIMESTAMP","PID"))');
	return $obj;
}

1;


