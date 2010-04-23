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

use Date::Calc qw(:all);

use Isip::IsipLog '$logger';
use Mail::Sender;

#  Documentation
###########################################################
=head1 NAME

PC_REPORT_UPDATE - Verification de valeurs particulières

=head1 SYNOPSIS

 PC_REPORT_UPDATE.pl [-h][-v] [-m] [-g groupe] [-a | Environnement]
 
=head1 DESCRIPTION

Verification de lignes/champs particuliers et envoi de mail en cas de non-validation.

=head1 ENVIRONNEMENT

=over

=item ITOOLS : L'environnement du service de l'ICles IKOS doit être chargé

=back

=head1 OPTIONS

=over

=item -h : Affiche l'aide en ligne

=item -v : Mode verbeux

=item -m : Effectue l'envoie du courriel

=item -g : verifie uniquement pour le groupe spécifié

=item -a : tous les environnements

=back

=head1 ARGUMENTS

=over

=item environnement : l'environnement à verifier

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
	pod2usage(-verbose => $verbosity, -noperldoc => 1);
	sortie(202); 
}

sub log_error {
	#print STDERR "ERREUR: ".join(" ",@_)."\n"; 
	$logger->error(@_);
	sortie(202);
}

sub log_info {
	#print STDERR "INFO: ".join(" ",@_)."\n"; 
	$logger->notice(@_);
}

sub swrite {
	log_error "usage: swrite PICTURE ARGS" unless @_;
	my $format = shift;
	$^A = "";
	formline($format,@_);
	return $^A;
    }

#  Traitement des Options
###########################################################

my $debug_level = 0;

my %opts;
getopts('hvmg:a', \%opts) or usage($debug_level+1);

$debug_level = 1 if $opts{v};

usage($debug_level+1) if $opts{h};


my $send_mail=$opts{m};

my $check_group = $opts{g};

#  Traitement des arguments
###########################################################

if ( @ARGV < 0) {
	log_info("Nombre d'argument incorrect (".@ARGV.")");
	usage($debug_level);
}

my $environnement_arg = shift;

if ( not ($environnement_arg xor $opts{a} ) ) {
	usage($debug_level);
}

#  Corps du script
###########################################################

use Isip::IsipConfig;
use Isip::IsipRules;
use Isip::Environnement;


my $config = IsipConfig->new();

my @environnement_list;
if ( $environnement_arg ) {
	@environnement_list = ( $environnement_arg );
}
else {
	@environnement_list = $config->get_environnement_list();
}

foreach my $environnement (@environnement_list) {

	my $env = Environnement->new($environnement);

	my %row_array_for;
	
	my $mail_table = eval { $env->open_local_table("FIELD_MAIL") };
	next if $@;

	if ( $check_group ) {
		$mail_table->query_condition("MAIL_GROUP = '$check_group'");
	}

	while ( my %row = $mail_table->fetch_row() ) {
		
		my $group      = $row{MAIL_GROUP};
		my $table_name = $row{TABLE_NAME};
		my $table_key  = $row{TABLE_KEY};
		my $field_name = $row{FIELD_NAME};
		
		my $table_to_check = $env->open_histo_field_table($table_name);
		$table_to_check->query_key_value($table_key);
		$table_to_check->query_condition("FIELD_NAME = ".$table_to_check->quote($field_name));
		$table_to_check->query_field("ICON", $table_to_check->query_field() );

		$table_to_check->isip_rules( IsipRules->new($table_name, $env) );
		
		while ( my %row_to_check = $table_to_check->fetch_row() ) {
			
			if ( $row_to_check{ICON} !~ /^valide/ ) {
				log_info("Dans la table $table_name, le champ $field_name  de la clef $table_key n'est pas validé");
				
				# garde la ligne en mémoire
				push @{ $row_array_for{$group} } , \%row_to_check;
			}
		}
	}

	# première passe pour recuperer la taille des colonnes du tableau
	my %max_length_of;
	while (my ($group, $rows_ref) = each %row_array_for) {
		
		my %message_for;
		foreach my $row_hash_ref (@$rows_ref) {
			my %row = %$row_hash_ref;
			
			foreach my $field ("TABLE_NAME", "TABLE_KEY", "FIELD_NAME", "FIELD_VALUE") {
				
				# max()
				if ( not exists $max_length_of{$field} or (length $row{$field}) > $max_length_of{$field} ) {
					$max_length_of{$field} = length $row{$field};
				}
			}
		}
	}
	
	# entete
	my %title_for=(
		"TABLE_NAME" => "Table",
		"TABLE_KEY" => "Clef",
		"FIELD_NAME" => "Champ",
		"FIELD_VALUE" => "Valeur",
	);
	my $space_between=4;

	while (my ($group, $rows_ref) = each %row_array_for) {
		
		log_info("Verification pour le groupe $group");
		
		my %message_for;
		foreach my $row_hash_ref (@$rows_ref) {
			my %row = %$row_hash_ref;
			
			# entete pour chaque date de collecte
			my $total_padding=0;
			if ( not $message_for{ $row{DATE_HISTO} } ) {
				foreach my $field ("TABLE_NAME" ,"TABLE_KEY", "FIELD_NAME", "FIELD_VALUE") {
					my $padding = $max_length_of{$field} - length $title_for{$field};
					$message_for{ $row{DATE_HISTO} } .= $title_for{$field}. ' ' x ($padding+$space_between);
					
					$total_padding += $max_length_of{$field}+$space_between;
				}
				$message_for{ $row{DATE_HISTO} } .= "\n";
				$message_for{ $row{DATE_HISTO} } .= "-" x $total_padding;
				$message_for{ $row{DATE_HISTO} } .= "\n";
			}
			
			# valeur
			foreach my $field ("TABLE_NAME" ,"TABLE_KEY", "FIELD_NAME", "FIELD_VALUE" ) {
				my $padding = $max_length_of{$field} - length $row{$field};
				$message_for{ $row{DATE_HISTO} } .= $row{$field}. ' ' x ($padding+$space_between);
			}
			$message_for{ $row{DATE_HISTO} } .= "\n";
			
		}
		
		
			
			my @full_message;
			my $send_ok;
			
			push @full_message, "\nLes changements suivants doivent être validées sur $environnement : \n";
			
			foreach my $date_histo (sort {$a lt $b} keys %message_for) {
				$send_ok++;
				
				my $date_histo_format = $date_histo;
				$date_histo_format =~ s/T/ à /;
				push @full_message, " * Collecté le $date_histo_format :\n";
				
				push @full_message, $message_for{$date_histo}
				
			}
			
			print join("\n",@full_message)."\n";
			
		if ( $send_mail and $send_ok) {
			log_info("Envoi de l'email");
			$config->send_mail("Alertes I-SIP pour le groupe $group sur $environnement", join("\n",@full_message), { group => $group } );
		}
		
	}
}



__END__
:endofperl
