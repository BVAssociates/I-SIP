#!/usr/bin/perl

# Inclusions obligatoires
use strict;
use Pod::Usage;
use Getopt::Std;

#  Documentation
###########################################################
=head1 NAME

ReplaceAndExec - Met � jour une ligne dans un processeur Administrate

=head1 SYNOPSIS

 InsertAndExec [-h] [-v] INTO <Table> VALUES <-|Values> [WHERE <Condition>]
 
=head1 DESCRIPTION

Liste les champs d'une table dans un environnement � la date courante

=head1 ENVIRONNEMENT

=over 4

=item ITOOLS : L'environnement des ITools doit �tre charg�

=back

=head1 OPTIONS

=over 4

=item -h : Affiche l'aide en ligne (combiner avec -v pour l'aide compl�te)

=item -v : Mode verbeux

=item -f pour forcer la cr�ation du fichier de donn�es

=back

=head1 ARGUMENTS

=over 4

=item Table : Nom de la table dans laquelle ins�rer les valeurs

=item - : Utiliser les valeurs provenant de l'entr�e standard

=item Values : Valeurs � ins�rer dans la table

=item Condition : Condition de s�lection de la ligne � modifier

=back

=head1 AUTHOR

BV Associates, 16/10/2008

=cut


#  Fonctions
###########################################################

sub sortie ($) {
	exit shift;
}

sub usage($) {
	my $verbosity=shift;
	pod2usage(-verbose => $verbosity, -noperldoc => 1);
	sortie(202); 
}

sub log_erreur {
	@_=grep {defined $_} @_;
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	sortie(202);
}

sub log_info {
	@_=grep {defined $_} @_;
	print STDERR "INFO: ".join(" ",@_)."\n"; 
}


#  Traitement des Options
###########################################################

my @argv_save=@ARGV;

my %opts;
getopts('hv', \%opts);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 4) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

log_info(join(' ',@ARGV));

my $INTO_WORD=shift;
my $table_name=shift;
my $VALUES_WORD=shift;

my $values;
my $conditions;
foreach (@ARGV) {
	if (uc eq 'WHERE') {
		$conditions=join(' ',shift);
		last;
	} else {
		$values .= $_;
	}
}

log_info("VALUES: ",$values);
log_info("CONDITION: ",$conditions);

if (uc($INTO_WORD) ne 'INTO' or uc($VALUES_WORD) ne 'VALUES') {
	log_info("Ligne de commande incorrect");
	usage($debug_level);
	sortie(202);
}


#  Corps du script
###########################################################
my $bv_severite=0;

use File::Spec::Functions qw/path splitpath catfile/;

my ($current_vol,$current_dir,$current_script)=splitpath($0);

if ($table_name =~ /^IKOS_FIELD/) {
	system "perl",("$current_vol/$current_dir/ReplaceAndExec_IKOS_FIELD.pl",@argv_save);
	if ($? == -1) {
		die "failed to execute: $!\n";
	}
	elsif (($? >> 8) != 0) {
		die sprintf ("'ReplaceAndExec_IKOS_FIELD.pl' died with signal %d, %s",($?  >> 8))
	};
}
else {
	# otherwise,  we use the original script
	if ($table_name !~ /^FIELD/) {
		
		# routine to find the next ReplaceAndExec in Path
		my $count=1;
		my $next_script;
		foreach my $dir (path()) {
			$next_script=catfile($dir,$current_script);
			if (-r $next_script) {
				if ($count-- <= 0) {
					log_info("$table_name : exec official script : $next_script ");
					system "perl",($next_script,@argv_save);
					if ($? == -1) {
						die "failed to execute: $!\n";
				    }
				    elsif (($? >> 8) != 0) {
						die sprintf ("'$next_script' died with signal %d, %s",($?  >> 8))
				    };
				}
			}
		}
	}
}


sortie($bv_severite);