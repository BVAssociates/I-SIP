@rem = '--*-Perl-*--
@echo off
if "%OS%" == "Windows_NT" goto WinNT
perl -x -S "%0" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto endofperl
:WinNT
perl -x -S %0 %*
if NOT "%COMSPEC%" == "%SystemRoot%\system32\cmd.exe" goto endofperl
if %errorlevel% == 9009 echo You do not have Perl in your PATH.
if errorlevel 1 goto script_failed_so_exit_with_non_zero_val 2>nul
goto endofperl
@rem ';
#!/usr/bin/perl
#line 15

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

use Isip::IsipLog '$logger';

#  Documentation
###########################################################
=head1 NAME

PC_LIST_FIELD_HISTO - Liste l'historique d'un champs d'une table dans un environnement

=head1 SYNOPSIS

 PC_LIST_FIELD_HISTO.pl environnement table champ
 
=head1 DESCRIPTION

Liste l'historique d'un champs d'une table dans un environnement

=head2 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head2 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=back

=head2 ARGUMENTS 

=over 4

=item environnement : environnement à utiliser

=item table : nom de la table a décrire

=item champ : nom du champ

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut

#  Fonctions
###########################################################

sub sortie ($) {
	exit shift;
}

sub usage($) {
	my $verbosity=shift;
	pod2usage(-verbose => $verbosity+1, -noperldoc => 1);
	sortie(202); 
}

sub log_erreur {
	#print STDERR "ERREUR: ".join(" ",@_)."\n";
	$logger->error(@_);
	sortie(202);
}

sub log_info {
	#print STDERR "INFO: ".join(" ",@_)."\n";
	$logger->notice(@_);
}


#  Traitement des Options
###########################################################


my %opts;
getopts('hvk:s:', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

my $table_key_value=$opts{k};

my $separator=',';
$separator=$opts{s} if $opts{s};
	
usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV != 3) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $environnement=shift;
my $tablename=shift;
my $field=shift;

# quirk to test
$ENV{Environnement}=$environnement;
$ENV{GSL_FILE}=$tablename;

#  Corps du script
###########################################################
use ITable::ITools;
use Isip::Environnement;

my $bv_severite=0;

# New SIP Object instance
my $env = Environnement->new($environnement, {debug => $debug_level});

# recuperation de la clef primaine de la table
my $table_key = $env->get_table_key($tablename);

if (not $table_key) {
	log_erreur("pas de clef primaine pour la table $tablename");
	sortie(202);
}

my @table_key_list=split(',',$table_key);
my @table_key_list_value;

if (not $table_key_value) {
	log_info("deduction de la clef primaire depuis l'environnement");
	foreach (@table_key_list) {
		if (exists $ENV{$_}) {
			push @table_key_list_value, $ENV{$_};
		}
		else {
			$logger->warning("Clef primaine <$_> n'est pas definie dans l'environnement");
			push @table_key_list_value, "";
		}
	}
	
	$table_key_value=join(',',@table_key_list_value);
}


log_info("KEY= $table_key");
log_info("KEY_VAL=$table_key_value");

# fetch selected row from histo table
my $table_histo = $env->open_local_table($tablename."_HISTO", {debug => $debug_level});
$table_histo->query_condition("TABLE_KEY = ".$table_histo->quote($table_key_value)." AND FIELD_NAME = ".$table_histo->quote($field));

my %field_histo;

my $counter=0;
while (my %line=$table_histo->fetch_row() ) {
	$counter++;
	$line{BASELINE_DESC}="";
	$field_histo{ $line{DATE_HISTO} }=\%line;
}

# fetch selected row from baselines
my $date_update=$env->open_local_table("DATE_UPDATE");
$date_update->query_field("DATE_HISTO","DESCRIPTION");
$date_update->query_condition("BASELINE=1");

my %baselines;
while (my ($date,$text)=$date_update->fetch_row_array) {
	$baselines{$date}=$text;
}

while ( my ($date,$text)=each %baselines) {

	my $baseline_name=HistoBaseline->get_baseline_name($tablename,$date);
	
	next if not $env->exist_local_table($baseline_name);
	
	my $table_baseline = $env->open_local_table($baseline_name, {debug => $debug_level});
	$table_baseline->query_condition("TABLE_KEY = ".$table_baseline->quote($table_key_value)." AND FIELD_NAME =".$table_baseline->quote($field) );

	log_info("récupération de la valeur dans la baseline $date");

	while (my %line=$table_baseline->fetch_row() ) {
		$counter++;
		$line{BASELINE_DESC}=$text;
		$field_histo{ $line{DATE_HISTO} }=\%line;
	}
}

foreach my $line(sort {$a->{DATE_HISTO} cmp $b->{DATE_HISTO}} values %field_histo) {
	print(join($separator,@$line{$table_histo->query_field(),"BASELINE_DESC"})."\n");
}

log_erreur("No history found for:",$field) if not $counter;


__END__
:endofperl
