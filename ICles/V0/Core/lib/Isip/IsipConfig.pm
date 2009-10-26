package IsipConfig;
use fields qw(
	options
	info_env
	info_module
	defaut_odbc_options
	export_dir
	isip_config
);

use strict;

use ITable::ITools;
# commented lines, because modules loaded on demand
use ITable::ODBC;
#use ITable::Sqlite;
#use Isip::ITable::Histo;
#use Isip::ITable::HistoField;
#use Isip::IsipRules;

use Carp qw(carp croak );
use Scalar::Util qw(blessed);
use File::Copy;

use Isip::IsipLog '$logger';

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	my $self=fields::new($class);
	
	# Arguments
	$self->{options} = shift;
	croak "bad arguments" if $self->{options} and ref($self->{options}) ne "HASH";
	
	$self->{defaut_odbc_options}={
		username => "ETUDEGF",
		password => "GFETUDE05",
	};
	
	$self->{isip_config}={
		smtp_host => $ENV{SMTP_HOST},
		smtp_from => 'isip@groupeicf.fr',
	};
	
	$self->{export_dir}= $ENV{ISIP_DATA}.'/export';
	
	# store global info about tables
	$self->{info_env}= {};

	#store information about Environnements and ODBC Datasouces
	my $table_environ=ITools->open("ENVIRON",$self->{options});
	while (my %row=$table_environ->fetch_row) {
		$self->{info_env}->{$row{Environnement}}->{description}=$row{Description};
		$self->{info_env}->{$row{Environnement}}->{defaut_datasource}=$row{DEFAUT_ODBC};
		$self->{info_env}->{$row{Environnement}}->{defaut_library}=$row{DEFAUT_LIBRARY};
	}
	

	#store information about Modules and DB2 Libraries
	my $table_module=ITools->open("MODULE",$self->{options});
	while (my %row=$table_module->fetch_row) {
		$self->{info_module}->{$row{Module}}->{description}=$row{Description};
		$self->{info_module}->{$row{Module}}->{library_type}=$row{BIBTYP};
	}
	
	return $self;
}

#return odbc datasource name
sub get_odbc_database_name() {
	my $self = shift;
	
	my $module=shift;
	my $environnement=shift or die "usage:get_odbc_database_name(module,environnement)";
	
	if (not exists $self->{info_env}->{$environnement}) {
		croak("Environnement $environnement non configuré");
	}
	
	if (not exists $self->{info_module}->{$module}) {
		croak("Module $module non configuré");
	}
	
	my $library_type=$self->{info_module}->{$module}->{library_type};
	
	my $module_table=ODBC->open("IKGSENV","ENVBIBP", $self->get_odbc_option($environnement));
	
	$module_table->query_condition(
		"ADCDENV = '".$environnement."'",
		"ADTYPBIB = '".$library_type."'");
	
	$module_table->query_field("ADBIBLIOT");
	
	my ($library)=$module_table->fetch_row_array();
	
	if ($module_table->fetch_row() or not $library) {
		# more than one row or no row at all
		croak("Impossible de determiner la librairie de type $library_type pour le module $module dans l'environnement $environnement");
	}
	
	return $library;
}

#return odbc datasource name
sub get_odbc_datasource_name() {
	my $self = shift;
	
	my $environnement=shift or croak "usage : get_odbc_datasource_name(environnement)";
	
	if (not exists $self->{info_env}->{$environnement}) {
		croak("Environnement $environnement non configuré");
	}
	
	return $self->{info_env}->{$environnement}->{defaut_datasource};
}

# return odbc option reference in format intended by ODBC->open()
sub get_odbc_option() {
	my $self = shift;
	
	my $environnement=shift or die "bad arguments";
	
	if (not exists $self->{info_env}->{$environnement}) {
		croak("Environnement $environnement non configuré");
	}
	
	my $options;
	$options->{username}=$self->{defaut_odbc_options}->{username};
	$options->{password}=$self->{defaut_odbc_options}->{password};
	$options->{odbc_name}=$self->{info_env}->{$environnement}->{defaut_datasource};
	
	return $options;
}

sub exists_odbc_table() {
	my $self = shift;
	
	my $environnement=shift or die "bad arguments";
	
	die "not implemented yet";
}

sub get_env_info() {
	my $self = shift;
	
	return %{$self->{info_env}};
}


sub get_environnement_list() {
	my $self = shift;

	return keys %{$self->{info_env}};
}

sub get_module_list() {
	my $self = shift;

	return keys %{$self->{info_module}};
}

# provide file path of Sqlite database depending on architecture, environnement and table name
sub get_data_dir() {
	my $self = shift;
	
	my $data_path;
	if (exists $ENV{ISIP_DATA}) {
		$data_path=$ENV{ISIP_DATA}."/tab";
		if (not (-d $data_path and -r $data_path and -w $data_path) ) {
			croak("ISIP_DATA=\"".$ENV{ISIP_DATA}."\" is not a valid directory");
		}
	}
	
	#remove last slash if needed
	$data_path =~ s|[\\/]$||;
	
	return $data_path;
}

sub get_config_var() {
	my $self=shift;
	
	my $config_var=shift or croak("usage: get_config_var (varname)");
	
	return $self->{isip_config}->{$config_var};
}

sub exists_database_environnement() {
	my $self=shift;
	
	my $environnement=shift or die "bad arguments";
	
	my $database_path=$self->get_data_dir()."ISIP_".$environnement."_INFO.sqlite";
	
	return -s $database_path;
}

# copy environnement datafiles from existing one to new one
sub copy_environnement() {
	my $self=shift;
	
	my $environnement_from=shift;
	my $environnement_to=shift or die "bad arguments";
	
	if (! grep {$environnement_from eq $_} $self->get_environnement_list) {
		croak("$environnement_from is not a valid environnement");
	}
	
	if (! grep {$environnement_to eq $_} $self->get_environnement_list) {
		croak("$environnement_to is not a valid environnement");
	}
	
	my $database_path_from=$self->get_data_dir()."/ISIP_".$environnement_from."_INFO.sqlite";
	my $database_path_to=$self->get_data_dir()."/ISIP_".$environnement_to."_INFO.sqlite";
	
	$logger->notice("recopie de l'environnement $environnement_from vers $environnement_to");
	#copy($database_path_from,$database_path_to) or croak("Problème lors de la recopie de l'environnement $environnement_from vers $environnement_to :",$!);

}

sub create_database_environnement() {
	my $self=shift;
	
	my $environnement=shift or croak("usage : create_database_environnement(environnement)");
	
	if (! grep {$environnement eq $_} $self->get_environnement_list) {
		croak("$environnement is not a valid environnement");
	}
	
	my $database_path=$self->get_data_dir()."/ISIP_".$environnement."_INFO.sqlite";
	
	$logger->info("database already exist at <$database_path>") if -s $database_path;
	
	if (! -e $database_path) {
		$logger->notice("Creating empty file : $database_path");
		#create empty file
		open DATABASEFILE,">$database_path" or die "unable to create file : $!";
		close DATABASEFILE;
	}
	
	# opening master table
	my $master_table=Sqlite->open($database_path, 'sqlite_master');
	
	
	$logger->notice("Create table TABLE_INFO");
	$master_table->execute('CREATE TABLE IF NOT EXISTS TABLE_INFO (
	"ROOT_TABLE" NUMERIC,
	"TABLE_NAME" VARCHAR(30) NOT NULL,
	"TYPE_SOURCE" VARCHAR(30),
	"PARAM_SOURCE" VARCHAR(30),
	"MODULE" VARCHAR(30),
	"LABEL_FIELD" VARCHAR(30),
	"DESCRIPTION" VARCHAR(50),
	"ALLOW_IGNORE" BOOL DEFAULT 0,
	PRIMARY KEY (TABLE_NAME) 
	)');

	$logger->notice("Create table XML_INFO");
	$master_table->execute('CREATE TABLE IF NOT EXISTS "XML_INFO" (
		"XML_NAME" TEXT NOT NULL ,
		"XML_PATH" TEXT NOT NULL ,
		"MASTER" TEXT ,
		PRIMARY KEY ("XML_NAME", "XML_PATH")
	)');
	
	#$logger->notice("Drop table CACHE_ICON");
	#$master_table->execute('DROP TABLE "CACHE_ICON"');
	$logger->notice("Create table CACHE_ICON");
	$master_table->execute('CREATE TABLE IF NOT EXISTS "CACHE_ICON" (
		"TABLE_NAME" VARCHAR NOT NULL,
		"TABLE_SOURCE" VARCHAR NOT NULL,
		"TABLE_KEY" VARCHAR NOT NULL,
		"NUM_CHILD" INTEGER,
		PRIMARY KEY ("TABLE_KEY", "TABLE_NAME", "TABLE_SOURCE")
	)');
	
	#$logger->notice("Drop table CACHE_PROJECT");
	#$master_table->execute('DROP TABLE IF EXISTS "CACHE_PROJECT"');
	$logger->notice("Create table CACHE_PROJECT");
	$master_table->execute('CREATE TABLE IF NOT EXISTS "CACHE_PROJECT" (
		"TABLE_NAME" VARCHAR NOT NULL,
		"TABLE_KEY" VARCHAR NOT NULL,
		"PROJECT_CHILD" VARCHAR NOT NULL,
		"NUM_CHILD" INTEGER,
		PRIMARY KEY ("TABLE_KEY", "TABLE_NAME", "PROJECT_CHILD")
	)');
	
	$logger->notice("Create table DATE_UPDATE");
	$master_table->execute('CREATE TABLE IF NOT EXISTS "DATE_UPDATE" (
		"DATE_HISTO" VARCHAR NOT NULL,
		"DESCRIPTION" VARCHAR,
		"DIFF_VALUE" INTEGER,
		"DIFF_STRUCT" INTEGER,
		"BASELINE" INTEGER,
		PRIMARY KEY ("DATE_HISTO")
	)');
	
	$logger->notice("Create table PROJECT_INFO");
	$master_table->execute('CREATE TABLE IF NOT EXISTS "PROJECT_INFO" (
		"PROJECT_NAME" TEXT NOT NULL ,
		"PROJECT_CREATE" TEXT,
		"PROJECT_CLOSE" TEXT,
		"DESCRIPTION" TEXT,
		PRIMARY KEY ("PROJECT_NAME")
	)');
	
	
	$logger->info("Create table FIELD_LABEL");
	$master_table->execute('CREATE TABLE IF NOT EXISTS FIELD_LABEL (
		"DATE_UPDATE" TEXT,
		"USER_UPDATE" TEXT NOT NULL ,
		"TABLE_NAME" TEXT NOT NULL ,
		"TABLE_KEY" TEXT,
		"FIELD_NAME" TEXT,
		"LABEL" TEXT, PRIMARY KEY ("TABLE_NAME","TABLE_KEY","FIELD_NAME") 
	)');
	
	$logger->info("Create table FIELD_MAIL");
	$master_table->execute('CREATE TABLE IF NOT EXISTS FIELD_MAIL (
		"DATE_UPDATE" TEXT,
		"USER_UPDATE" TEXT NOT NULL,
		"TABLE_NAME" TEXT NOT NULL,
		"TABLE_KEY" TEXT,
		"FIELD_NAME" TEXT,
		PRIMARY KEY ("USER_UPDATE","TABLE_NAME","TABLE_KEY","FIELD_NAME")
	)');
	
	$master_table->close();
}

# methode d'envoi de mail
sub send_mail {
	my $self=shift;
	
	require Mail::Sender;

	my $subject     = shift;
	my $message     = shift;
	my $dest_option = shift or croak("usage: send_mail(subject,message, {responsablity => 'resp', user => 'user', group => 'group'} )");
	
	my $mailto_responsability = $dest_option->{responsablity};
	my $mailto_user           = $dest_option->{user};
	my $mailto_group          = $dest_option->{group};
	
	if ( 3 == grep {not defined} values %{ $dest_option } ) {
		croak("send_mail() attend au moins une option de destination");
	}
	
	my $smtp_host=$self->get_config_var("smtp_host");
	my $smtp_from=$self->get_config_var("smtp_from");
	
	# collecte roles associés à responsabilité
	my @mailto_roles;
	if ( $mailto_responsability ) {
		my $roles_table=ITools->open("respRoles");
		while ( my %right = $roles_table->fetch_row() ) {
			if ( $right{Responsability} eq $mailto_responsability ) {
				push @mailto_roles, $right{Role};
			}
		}
	}
	
	# collecte email associés au roles,groupe et user
	my @mailto_email;
	my $user_table=ITools->open("PortalAccess");
	
	my %mailto_email_uniq;
	while (my %user = $user_table->fetch_row() ) {
		
		if ( $mailto_responsability ) {
			if ( $user{Email} and grep { $_ eq $user{Role} } @mailto_roles) {
				$mailto_email_uniq{$user{Email}}++;
			}
		}
		
		if ( $mailto_group ) {
			if ( $user{Groups} =~ /(^|,)$mailto_group(,|$)/ ) {
				$mailto_email_uniq{$user{Email}}++;
			}
		}
		
		if ( $mailto_user ) {
			if ( $user{IsisUser} eq  $mailto_user ) {
				$mailto_email_uniq{$user{Email}}++;
			}
		}
	}
	@mailto_email = keys %mailto_email_uniq;
	
	if ( not @mailto_email ) {
		$logger->error("Aucun email correspondant")
	}

	$logger->info("Connexion SMTP : $smtp_host");
	my $sender = Mail::Sender->new(  {smtp => $smtp_host, from => $smtp_from});
	if (not ref $sender) {
		$logger->error("Probleme de connexion à $smtp_host");
	}
		
	$logger->info("Envoi du mail à : ", join(',', @mailto_email) );
	my $success = $sender->MailMsg({to => \@mailto_email,
	  subject => $subject,
	  msg => $message}
	  );
	  
	if (not $success) {
		$logger->error("Probleme lors de l'envoi du mail");
	}
	else {
		$logger->info("Mail envoyé");
	}
}


sub _find_file() {
	my $self=shift;
	
	my $path_list = shift;
	my $filename = shift;
	
	use Config;
	my $env_separator = $Config{path_sep};
	
	
	foreach my $path (split ($env_separator,$path_list)) {
		my $filepath = $path;
		$filepath .= "/".$filename if $filename;
		return $filepath if -r $filepath;
	}
	
	return undef;
}

1;


=head1 NAME

Isip::Isipconfig - Accede au information globale à un service d'ICle I-SIP

=head1 SYNOPSIS

 use Isip::Isipconfig;
 
 my $config = Isipconfig->new();
 my @env_list = $config->get_environnement_list();

=head1 DESCRIPTION

La classe IsipConfig gère les données globales à une instance I-SIP.

Voici les informations gérées dans un objet IsipConfig :

=over

=item * Gestion des Environnement : Création/suppression/copie.

=item * Gestion des Modules.

=item * Gestion des informations sur la base source de l'historisation (ODBC).

=back

=head1 METHODS

=over 12

=item C<new ()>

Instancie un objet de type IsipConfig.

=item C<get_data_dir ()>

Renvoie le répertoire de stockage des base Sqlite.

=item C<get_odbc_* ()>

Renvoie les différent paramètre de connexion à la base ODBC.

=item C<get_environnement_list()>

Renvoie la liste des environnement déclarés.

=item C<get_module_list()>

Renvoie la liste des modules déclarés.

La définition des modules est commune à tous les environnements.

=item C<exists_database_environnement()>

Test l'existence des données nécessaires à un environnement.

=item C<create_database_environnement()>

Créer les données nécessaires à un environnement.

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits réservés.

=cut
