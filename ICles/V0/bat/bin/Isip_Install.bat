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

use File::Spec::Functions qw(splitpath catpath splitdir catdir);
use Cwd qw(abs_path);


#  Documentation
###########################################################
=head1 NAME

Isip_Install - Installe une instance de I-SIP sur un portail

=head1 SYNOPSIS

 Isip_Install.pl [-h][-v] [-p portail_base_rep] isip_data_rep
 
=head1 DESCRIPTION



=head1 ENVIRONNEMENT

=over

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -p portal_base_rep : chemin du repertoire d'installation du portail.
S'il n'est pas spécifier, utilise le repertoire courant


=back

=head1 ARGUMENTS

=over

=item isip_data_rep : chemin du repertoire qui contiendra les données

=back

=head1 AUTHOR

Copyright (c) 2009 BV Associates. Tous droits réservés.

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
	print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	#$logger->error(@_);
	sortie(202);
}

sub log_info {
	print STDERR "INFO: ".join(" ",@_)."\n"; 
	#$logger->notice(@_);
}


#  Traitement des Options
###########################################################


my %opts;
getopts('hvp:f', \%opts) or usage(0);

my $debug_level = 0;
$debug_level = 1 if $opts{v};

my $force=$opts{f};

my $portail_base=$opts{p} if exists $opts{p};
if (not $portail_base) {
	# deduce portail_base if not provided
	my ($drive,$dirname,undef)=splitpath(abs_path($0));	
	$dirname =~ s#ICles[\\/]ISIP[\\/]V0[\\/]Portal[\\/]bin[\\/]?$##;
	$portail_base=catpath($drive,$dirname,"");
}


usage($debug_level+1) if $opts{h};

#  Traitement des arguments
###########################################################

if ( @ARGV < 1) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
	sortie(202);
}

my $isip_data=shift @ARGV;

#  Verifications
###########################################################

if (not -d $portail_base or not -r $portail_base) {
	die("$portail_base :  n'est pas un repertoire valide");
}


if (not -d $isip_data or not -r $isip_data) {
	die("$isip_data :  n'est pas un repertoire valide");
}
else {
	if (not $force) {
		my $dir;
		opendir($dir,$isip_data) or die("$isip_data : $!");
		if(grep {$_ ne '.' and $_ ne '..'} readdir($dir)) {
			die("isip_data : le repertoire n'est pas vide, verifier ou utiliser -f pour installer quand même");
		}
		closedir($dir);
	}
}

#  Corps du script
###########################################################

$portail_base =~ s#[\\/]?$##;
$isip_data =~ s#[\\/]?$##;

my $portal_ini=catdir($portail_base,'Portal\product\conf','IsisPortal_WIN32.ini');
if (not -r $portal_ini) {
	die("$portal_ini : n'est pas un accessible en lecture")
}

open(my $portal_ini_fh, "+<",$portal_ini);
close($portal_ini_fh);

my $portal_ini_append='

# config for ISIP
ICleName=ISIP
ISIP_HOME=%CLES_HOME%\%ICleName%
ISIP_DATA=
PERL_PATH=%ISIP_HOME%\V0\Core\bin;%ISIP_HOME%\V0\Portal\bin

# edit with care!
ISIP_LOG=%ISIP_DATA%\log
PATH=%ISIP_HOME%\V0\script\bin;%ISIP_HOME%\V0\Portal\bin;%ISIP_HOME%\V0\Core\bin;%ISIP_HOME%\V0\batch\bin;%PATH%
BV_DEFPATH=%ISIP_DATA%\def;%ISIP_HOME%\V0\Portal\def;%ISIP_HOME%\V0\Core\def;%BV_DEFPATH%
BV_TABPATH=%ISIP_DATA%\tab;%ISIP_HOME%\V0\Portal\tab;%ISIP_HOME%\V0\Core\tab;%BV_TABPATH%
BV_PCIPATH=%ISIP_DATA%\pci;%ISIP_HOME%\V0\Portal\pci;%ISIP_HOME%\V0\Core\pci;%BV_PCIPATH%

# commenter si utilisation de PAR
PERL5LIB=%PERL5LIB%;%ISIP_HOME%\V0\Core\lib

PERL5LIB=%PERL5LIB%;%PERL_PATH%
';

$portal_ini_append =~ s/^(ISIP_DATA=).*$/$1$isip_data/m;
die $portal_ini_append;



__END__
:endofperl
