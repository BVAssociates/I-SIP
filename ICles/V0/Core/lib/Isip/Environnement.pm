package Environnement;

use Isip::IsipConfig;

use fields qw(
	options
	environnement
	info_table
	description
	isip_config
	defaut_odbc_options
);

use strict;
use Scalar::Util qw(blessed);
use POSIX qw(strftime);

#use ITable::ITools;
use Carp qw(carp croak);
use Isip::IsipLog '$logger';
use ITable::ILink;

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	$Carp::Internal{ __PACKAGE__ }++;
	
	# Arguments
	my $environnement = shift or croak "Environnement->new take 1 argument";
	my $options= shift;
	
	my Environnement $self= fields::new($class);

	# store global info about tables
	$self->{isip_config}= IsipConfig->new();
	
	$self->{options}= $options;
	$self->{environnement} = $environnement;
	
	# store global info about environnement 
	if (not exists $self->{isip_config}->{info_env}->{$environnement} ) {
		croak "Unable to get information of Environnement : $self->{environnement}";
	}
	
	$logger->info("Chargement de l'environnement $environnement");
	
	$self->{description}=$self->{isip_config}->{info_env}->{$environnement}->{description};
	
	# constants
	#TODO : use confuguration file
	
	# populate class members
	
	my $table_info=$self->open_local_table("TABLE_INFO", $self->{options});
	
	while (my %row=$table_info->fetch_row) {		
		$self->{info_table}->{$row{TABLE_NAME}}->{module}=$row{MODULE};
		$self->{info_table}->{$row{TABLE_NAME}}->{root_table}=$row{ROOT_TABLE};
		$self->{info_table}->{$row{TABLE_NAME}}->{type_source}=$row{TYPE_SOURCE};
		$self->{info_table}->{$row{TABLE_NAME}}->{param_source}=$row{PARAM_SOURCE};
		$self->{info_table}->{$row{TABLE_NAME}}->{label_field}=$row{LABEL_FIELD};
		$self->{info_table}->{$row{TABLE_NAME}}->{description}=$row{DESCRIPTION};
		$self->{info_table}->{$row{TABLE_NAME}}->{allow_ignore}=$row{ALLOW_IGNORE};

		# init structure for columns 
		$self->{info_table}->{$row{TABLE_NAME}}->{column}={};
		
		# init private structure for method get_table_field()
		$self->{info_table}->{$row{TABLE_NAME}}->{_column_order}=[];
	}
	
		
	# add environnement specific info about tables
	my $source_info=$self->open_local_table("XML_INFO", $self->{options});
	
	my %sources;
	while (my %row=$source_info->fetch_row) {
	    next if not exists $self->{info_table}->{$row{XML_NAME}};
	    
		if (not exists $self->{info_table}->{$row{XML_NAME}}->{xml_copy_list}) {
			$self->{info_table}->{$row{XML_NAME}}->{xml_copy_list}=[];
		}
	    
		# if master already set, put the path in copy_list
	    if ($row{MASTER} and not $self->{info_table}->{$row{XML_NAME}}->{xml_path})
		{
			$self->{info_table}->{$row{XML_NAME}}->{xml_path}=$row{XML_PATH};
	    }
	    else
		{
			push @{$self->{info_table}->{$row{XML_NAME}}->{xml_copy_list}},$row{XML_PATH};
	    }
	}
	
	foreach my $xml_name ($self->get_table_list) {
		# if no master, we take the first XML
		if (exists $self->{info_table}->{$xml_name}->{xml_copy_list}
				and @{$self->{info_table}->{$xml_name}->{xml_copy_list}}
				and not $self->{info_table}->{$xml_name}->{xml_path})
		{
			$self->{info_table}->{$xml_name}->{xml_path}=shift @{$self->{info_table}->{$xml_name}->{xml_copy_list}};
		}
	}
	
	$logger->info("Environnement $self->{environnement} opened");
	
	return $self;
}

sub get_columns() {
	my $self = shift;
	
	use Isip::HistoColumns;
	
	my $table_name=shift or croak "usage : get_columns(tablename[,date])";
	my $query_date=shift;
	
	if (not $query_date) {
		# look in memory
		my $temp_obj=$self->{info_table}->{$table_name}->{column};
		if ($temp_obj and blessed $temp_obj) {
			return $temp_obj;
		}
	}
	
	my $table_column=$table_name."_COLUMN";
	
	my $sqlite_path=$self->get_sqlite_path($table_column,$self->{environnement});
	if (not $sqlite_path) {
		croak ("Impossible de trouver la base contenant $table_column dans $self->{environnement}");
	}
	if (not -e $sqlite_path) {
		croak ("Impossible d'acceder � la base contenant $table_column dans $self->{environnement}");
	}
	
	my $options = {date => $query_date} if $query_date;
	
	my $tmp_return = eval {HistoColumns->new($sqlite_path, $table_name, $options)};
	$logger->warning("Impossible d'obtenir les informations de colonnes pour $table_name dans ".$self->{environnement}." : $@") if $@;
	
	# put object in memory for next calls
	if (not $query_date) {
		$self->{info_table}->{$table_name}->{column}=$tmp_return;
	}
	
	return $tmp_return;
}

#renvoie vrai si la table est une table racine
sub is_root_table() {
	my $self = shift;
	
	my $table_name=shift or croak("usage : is_root_table(table)");
	
	return undef if not exists $self->{info_table}->{$table_name};
	return $self->{info_table}->{$table_name}->{root_table};
}

# renvoie vrai si la table peut ignor�e certaines lignes
sub can_table_ignore() {
	my $self = shift;
	
	my $table_name=shift or croak("usage : can_table_ignore(table)");
	
	return undef if not exists $self->{info_table}->{$table_name};
	return $self->{info_table}->{$table_name}->{allow_ignore};
}

sub get_table_info() {
	my $self = shift;
	
	my $table_name=shift or croak("usage : get_table_info(table)");
	
	return () if not exists $self->{info_table}->{$table_name};
	return %{$self->{info_table}->{$table_name} };
}


sub get_table_list() {
	my $self = shift;

	return keys %{$self->{info_table}};
}

sub get_table_list_module() {
	my $self = shift;
	
	my $module_name= shift;

	return $self->get_table_list() if not $module_name;
	
	my @table_list;
	my %temp_hash=%{$self->{info_table}};
	foreach (keys %temp_hash ) {
		push @table_list, $_ if $self->{info_table}->{$_}->{module} eq $module_name;
	}
	
	return @table_list;
}

# return ILink object
sub get_links() {
	my $self = shift;
		
	my $links=ILink->new();
	
	# get links for all table
	foreach ($self->get_table_list) {
		my $columns=eval { $self->get_columns($_) };
		next if $@;
		
		my $link_add=$columns->get_links();
		$links->add_link($link_add);
	}


	
	
	return $links;
}

# return new ILink object with new virtual ROOT tables (see #56)
# ex, si TTARIFP est indiqu� comme ROOT_TABLE :
# OGAP
# |- TTARIFP
# |- TACHMNP
#
# devient :
# TTARIFP_OGAP
# |- TTARIFP
# TACHMNP__OGAP
# |- TACHMNP
sub get_links_menu() {
	my $self = shift;
			
	my ILink $link_clone = $self->get_links()->clone();
	
	my $separator='__';
	
	foreach my $table ($self->get_table_list) {
	
		# on ne va modifier que les tables racines
		next if not $self->{info_table}->{$table}->{root_table};
		
		my %parents=$link_clone->get_parent_tables_hash($table,1);
		
		next if not %parents;
		
		if ( @{ $parents{$table} } > 1 ) {
			$logger->warning("Table $table : I-SIP ne sait pas g�rer une table racine avec 2 clef �trang�res");
			# on ne garde que la premi�re table
			$parents{$table}= [ $parents{$table}->[0] ]
		}
		my @parents_menu=@{ $parents{$table} };
		
		my $child=$table;
		my $child_ext="";
		foreach my $parent ( @parents_menu ) {
			my %field=$link_clone->get_foreign_fields($child,$parent);
			$link_clone->remove_link($child,$parent);
			foreach my $pkey (keys %field) {
				# on construit une nouvelle relation avec une table parente d�di�e
				$link_clone->add_link($child_ext.$child,$pkey,$table.$separator.$parent,$field{$pkey});
			}
			#$child_ext=$table.$separator if not $child_ext;
			#$child=$parent;
		}
	}
	
	return $link_clone;
}

#found the table primary key
sub get_table_key() {
	my $self = shift;
	my $tablename = shift or croak "get_table_key() wait args : 'tablename'";
	
	my @key;
	
	# fixed key for XML sources
	if ($self->{info_table}->{$tablename}->{type_source} eq "XML") {
		@key= "xml_path";
	} 
	else {
		my $col_obj=eval { $self->get_columns($tablename) };
		return if $@;
		
		@key=$col_obj->get_key_list();
	}
	
	if (wantarray) {
		return @key;
	}
	else {
		return join(',',@key);
	}
}

sub get_table_description() {
	my $self = shift;
	my $tablename = shift or croak "get_table_description() wait args : 'tablename'";

	return undef if not exists $self->{info_table}->{$tablename};
	return $self->{info_table}->{$tablename}->{description};
}

sub get_table_size() {
	my $self = shift;
	my $tablename = shift or croak "get_table_description() wait args : 'tablename'";

	return undef if not exists $self->{info_table}->{$tablename};
	
	die "function not implemented";
	my %column_info=%{$self->{info_table}->{$tablename}->{column}};
	
	my %size_info;
	foreach my $col (keys %column_info) {
		$size_info{$col}=$column_info{$col}->{data_type}."(".$column_info{$col}->{data_size}.")";
	}
	
	return %size_info;
}

sub get_table_field() {
	my $self = shift;
	my $tablename = shift or croak "get_table_field() wait args : 'tablename'";
	my $query_date=shift;
	
	return $self->get_columns($tablename,$query_date)->get_field_list();
}

# provide file name of Sqlite database depending on table name and environnement
sub get_sqlite_filename() {
	my $self = shift;
	
	my $table_name=shift or croak "get_sqlite_filename() wait args : 'tablename[,environnement]'";
	my $environnement=shift;
	
	$environnement=$self->{environnement} if not $environnement;
	
	my $filename;
	my $database_path;
	
	
	if ($table_name =~ /^FIELD_MAIL|FIELD_LABEL|PROJECT_INFO|DATE_UPDATE|TABLE_INFO|XML_INFO|CACHE_.*$/i) {
		$filename = "ISIP_".$environnement."_INFO.sqlite";
	}
	else {
		croak("no environnement defined for table type : HISTO") if not $environnement;
		
		my $table_real;
		my $table_extension;
		
		# table are in format TABLENAME_EXTENSION ou TABLENAME
		($table_real,$table_extension) = ($table_name =~ /^(\w+)_(COLUMN|HISTO|HISTO_CATEGORY|INFO|LABEL|\d+T\d+)$/);
		($table_real,$table_extension) = ($table_name =~ /^(\w+)_(CATEGORY)$/) if not $table_extension;
		($table_real) = ($table_name =~ /^(\w+)$/) if not $table_real;

		croak("$table_name n'est pas un nom de table valide dans ".$self->{environnement}) if not $table_real;
	
		$filename = "ISIP_".$environnement."_".$table_real.".sqlite";
	}
	
	return $filename;
}

# provide file path of Sqlite database depending on architecture, environnement and table name
sub get_sqlite_path() {
	my $self = shift;
	
	my $table_name=shift or croak "get_sqlite_path() wait args : 'tablename[,environnement]'";
	my $environnement=shift;
	
	my $filename=$self->get_sqlite_filename($table_name,$environnement);
	
	
	my $dir=$self->{isip_config}->get_data_dir();
	
	my $filepath=$dir."/".$filename;
	
	if ($dir) {
		if (not -e $filepath) {
			$logger->info("$filepath n'existe pas");
			$filepath=undef;
		}
	}
	else {
		# if ISIP_DATA is not provided, we use first dir of BV_TABPATH
		$filepath=$self->_find_file($ENV{BV_TABPATH},$filename);
		#not found
		$logger->error("table $table_name : $filename not found in BV_TABPATH")	if not $filepath;
	}
		
	return $filepath;
}


sub exists_histo_table() {
	my $self = shift;
	
	my $table_name=shift or croak("usage : exists_doc_table(tablename)");
	
	return 1 if $self->get_sqlite_path($table_name."_HISTO");
}

# retourne vrai si la date est d�clar�e comme une baseline
sub is_baseline_date() {
	my $self=shift;
	
	my $date=shift;
	
	if ( $date !~ /\d{4}-?\d{2}-?\d{2}T\d{2}:?\d{2}/) {
			croak("is_baseline_date($date):La date n'est pas au format 1977-04-22T06:00 (ISO 8601)");
	}
	
	my %baseline_info;
	my $baseline_list=eval { $self->open_local_table("DATE_UPDATE") };
	if ( not $@ ) {
		$baseline_list->query_condition("DATE_HISTO='$date'");
		
		%baseline_info=$baseline_list->fetch_row();
		$baseline_list->finish;
	}
	
	if (not %baseline_info) {
		$logger->debug("$date:La date n'est pas une date de collecte");
		return 0;
	}
	
	if ($baseline_info{BASELINE}) {
		return 1;
	}
	else {
		return 0;
	}
}

# return an object on Histo or HistoBaseline
sub open_local_from_histo_table() {
	my $self = shift;
	
	use Isip::ITable::Histo;
	use Isip::ITable::HistoBaseline;
	
	my $table_name=shift or croak "usage : open_histo_table( table_name [,date_explore] )";
	my $date_explore=shift;
	
	# needed for backwards compatibility
	if (ref $date_explore eq "HASH") {
		unshift @_, $date_explore;
		undef $date_explore;
	}
	
	my $options=shift;
	$options->{columns}=$self->get_columns($table_name);
	if ( $@ ) {
		croak("Impossible d'ouvrir $table_name dans ".$self->{environnement}.": $@");
	}
	
	my $table_histo;
	
	if ($date_explore and $self->is_baseline_date($date_explore)) {
		my $baseline_name=HistoBaseline->get_baseline_name($table_name,$date_explore);
		if ($self->exist_local_table($baseline_name)) {
			$table_histo = eval {HistoBaseline->open($self->get_sqlite_path($table_name), $table_name, $date_explore, @_)};
			croak("Impossible d'ouvrir $table_name dans ".$self->{environnement}.": $@") if $@;
		}
		else {
			$logger->warning("$date_explore est indiqu� comme une baseline, mais les donn�es n'existent pas pour ".$self->{environnement}.".$table_name ");
			return;
		}
	}
	else {
		if (0 and not $self->exist_local_table($table_name.'_HISTO')) {
			$logger->error("La table $table_name\_HISTO est manquante dans ".$self->{environnement}) ;
			return;
		}
		
		$table_histo = eval {Histo->open($self->get_sqlite_path($table_name), $table_name, $options)};
		if ($@) {
			$logger->error("Impossible d'ouvrir la table $table_name dans ".$self->{environnement}." : $@") ;
			return;
		}

		$table_histo->query_date($date_explore) if $date_explore;
	}
	
	
	return $table_histo
}

sub open_histo_field_table() {
	my $self = shift;
	
	use Isip::ITable::HistoField;
	use Isip::ITable::HistoFieldBaseline;
	
	my $table_name=shift or croak "usage : open_histo_field_table(tablename [,date_explore])";
	my $date_explore=shift;
	
	# needed for backwards compatibility
	if (ref $date_explore eq "HASH") {
		unshift @_, $date_explore;
		undef $date_explore;
	}
	
	my $table_histo;
	
	if ($date_explore and $self->is_baseline_date($date_explore)) {
		my $baseline_name=HistoFieldBaseline->get_baseline_name($table_name,$date_explore);
		if ($self->exist_local_table($baseline_name)) {
			$table_histo = eval {HistoFieldBaseline->open($self->get_sqlite_path($table_name), $table_name, $date_explore, @_)};
			if ($@) {
				$logger->error("Impossible d'ouvrir $table_name dans ".$self->{environnement}." : $@");
				return;
			}
		}
		else {
			$logger->warning("$date_explore est indiqu� comme une baseline, mais les donn�es n'existent pas pour ".$self->{environnement}.".$table_name ");
			return;
		}
	}
	else {
		croak "La table $table_name\_HISTO est manquante dans ".$self->{environnement} if not $self->exist_local_table($table_name.'_HISTO');
		
		$table_histo = eval {HistoField->open($self->get_sqlite_path($table_name,$self->{environnement}), $table_name, @_)};
		if ($@) {
			$logger->error("Impossible d'ouvrir $table_name dans ".$self->{environnement}." : $@");
			return;
		}

		$table_histo->query_date($date_explore) if $date_explore;
	}
		
	return $table_histo;
}

sub open_cache_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
	my $table_name=shift or croak "open_cache_table() wait args : 'tablename'";
	
	my $tmp_return = eval {Sqlite->open($self->get_sqlite_path($table_name), $table_name, @_)};
	croak "Impossible d'ouvrir $table_name dans ".$self->{environnement}." : $@" if $@;
	return $tmp_return;
}


# change this methods to configure Database Access
sub exist_local_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename[,environnement]'";
	my $environnement=shift;
	
	
	my $database_path=$self->get_sqlite_path($table_name,$environnement);
	return 0 if not $database_path;
	
	# verification on sqlite_master
	my $master_table=eval {Sqlite->open($database_path, 'sqlite_master', @_)};
	return 0 if $@;
	
	$master_table->query_condition("type='table' AND name='$table_name'");
	
	my $return_value=0;
	if ($master_table->fetch_row_array) {
		$return_value=1;
	}
	$master_table->close();
	
	return $return_value;
}

sub open_local_table() {
	my $self = shift;
	
	use ITable::Sqlite;
	
	my $table_name=shift or croak "open_local_table() wait args : 'tablename'";
	
	my $sqlite_path=$self->get_sqlite_path($table_name,$self->{environnement});
	if (not defined $sqlite_path) {
		croak("Impossible de trouver la table SQLite $table_name dans $self->{environnement}");
	}
	elsif (not -r $sqlite_path) {
		croak("Impossible d'ouvrir le fichier $sqlite_path dans $self->{environnement}");
	}
	
	my $tmp_return = eval {Sqlite->open($sqlite_path, $table_name, @_)};
	croak "Impossible d'ouvrir la table SQLite $table_name dans $self->{environnement} : $@" if $@;
	return $tmp_return;
}

sub open_source_table() {
	my $self=shift;
	my $table_name=shift or croak "open_source_table() wait args : 'table_name'";
	my $options=shift;
	
	# merge odbc_options with additionnal options
	my $odbc_options=$self->{isip_config}->get_odbc_option($self->{environnement});
	foreach (keys %$options) {
		$odbc_options->{$_}=$options->{$_};
	}
	$options=$odbc_options;
	
	my $return_table;
	
	if (not exists $self->{info_table}->{$table_name}) {
		$logger->error("La table $table_name n'est pas d�clar�e dans ".$self->{environnement});
		return;
	}
	
	my $library=$self->{isip_config}->get_odbc_database_name(
		$self->{info_table}->{$table_name}->{module},
		$self->{environnement});
	
	if (not $library) {
		$logger->error("Librairie non configur� pour ".$self->{info_table}->{$table_name}->{module}." dans ".$self->{environnement});
		return;
	}
	
	# open source table depending on TYPE_SOURCE
	if ($self->{info_table}->{$table_name}->{type_source} eq "ODBC") {
		
		
		if ($self->{info_table}->{$table_name}->{param_source}) {
			use Isip::ITable::ODBC_Query;
			$logger->info("Connexion � ODBC : $library");
			$return_table=ODBC_Query->open($library, $table_name, $self->{info_table}->{$table_name}->{param_source}, $options);

			#manually set KEY
			if (not $return_table->key($self->get_table_key($table_name))) {
				carp ("PRIMARY KEY not defined for $table_name dans ".$self->{environnement}) ;
			}
		}
		else {
			use ITable::ODBC;
			$logger->info("Connexion � ODBC : $library");
			$return_table=ODBC->open($library, $table_name, $options);
			
			#manually set KEY
			if (not $return_table->key($self->get_table_key($table_name))) {
				my $table_logical=$table_name;
				$table_logical =~ s/P$/L0/;
				$logger->warning ("PRIMARY KEY non d�fini pour $table_name dans ".$self->{environnement}) ;
				
				# connect to DB2 catalog
				$logger->notice ("R�cupration PRIMARY KEY depuis la table $table_logical") ;
				my $sys_table=ODBC->open("QSYS","QADBKFLD",$self->{isip_config}->get_odbc_option($self->{environnement}));
				$sys_table->query_condition("DBKLIB='$library' AND DBKFIL = '$table_logical'");
				$sys_table->query_field("DBKFLD");
				my @keys;
				while (my ($key)=$sys_table->fetch_row_array) {
					push @keys,$key
				}
				$return_table->key(sort @keys);
			}
		}
		
		
		
	}
	elsif ($self->{info_table}->{$table_name}->{type_source} eq "XML") {
	
		my $xml_path=$self->{info_table}->{$table_name}->{xml_path};
		if (not $xml_path) {
			$logger->error("fichier source manquant $table_name dans ".$self->{environnement});
			return;
		}
		
		use ITable::XmlFile;
		
		$logger->info("Connexion � XML : <$xml_path> dans ".$self->{environnement}."");
		$return_table=XmlFile->open($xml_path, $table_name, $options);
	}
	else {
		$logger->error("Type de source <".$self->{info_table}->{$table_name}->{type_source}."> non reconnu pour $table_name dans ".$self->{environnement});
		return;
	}
	
	return $return_table;
}

sub initialize_column_info() {
	my $self=shift;
	
	my $itable_obj=shift or croak("usage initialize_column_info(DATA_interface)");
	my $links=shift;
	
	if (not blessed($itable_obj) or not $itable_obj->isa("DATA_interface")) {
		croak("usage initialize_column_info(DATA_interface)");
	}
	
	if ($itable_obj->isa("XmlFile")) {
		warn("Les fichiers de type XML ne peuvent pas �tre import�s");
	}

	
	my %size_hash=$itable_obj->size();
	my %field_txt_hash=$itable_obj->field_txt();
	my @key=$itable_obj->key();
	
	my $i=1;
	my %colno_hash = map {$_ => $i++} $itable_obj->field();
	
	$logger->notice("Update table structure from source table: ".$itable_obj->table_name);	
	
	# caclul des clefs �trang�re pour cette table si un 2e argument est pass�
	my %foreign_key_info;
	my %foreign_table_info;
	if ($links) {
		croak("usage initialize_column_info(DATA_interface)") if not blessed($links) and not $links->isa("ILink");
		my @foreign_table=$links->get_parent_tables($itable_obj->table_name);
		foreach my $f_tab (@foreign_table) {
			my %foreign_key=$links->get_foreign_fields($itable_obj->table_name,$f_tab);
			
			foreach my $f_key (keys  %foreign_key) {
				if (exists $foreign_key_info{$f_key}) {
					croak("Probl�me avec les liens : un champ ne peux avoir qu'une clef �trang�re pour  $f_tab dans ".$self->{environnement});
				}
				$foreign_key_info{$f_key}=$foreign_key{$f_key};
				$foreign_table_info{$f_key}=$f_tab;
			}
		}
	}		
	
	my %all_fields;
	foreach my $field ($itable_obj->field) {
		$all_fields{$field}++;
	}
	
	# check champs qui existent d�j�
	my $column_info=eval { $self->get_columns($itable_obj->table_name) };
	if (not $@) {
		foreach my $field ($column_info->get_field_list) {
			$all_fields{$field}--;
		}
	}
	
	foreach my $field (keys %all_fields) {

		my %source_col;
		$source_col{description}=$field_txt_hash{$field} if $field_txt_hash{$field};
		$source_col{size}=$size_hash{$field} if $size_hash{$field};
		$source_col{colno}=$colno_hash{$field} if $colno_hash{$field};
		$source_col{key}=1 if grep {$_ eq $field} @key;
		$source_col{foreign_table}=$foreign_table_info{$field} if $foreign_table_info{$field};
		$source_col{foreign_field}=$foreign_key_info{$field} if $foreign_key_info{$field};
		
		if ($all_fields{$field} > 0) {
			$column_info->add_column($field, \%source_col);
		}
		elsif ($all_fields{$field} < 0) {
			$column_info->remove_column($field);
		}
		else {
			$column_info->update_column($field, \%source_col);
		}
	}
}

sub create_database_histo() {
	my $self=shift;
	
	my $tablename=shift or croak("usage : create_database_histo(tablename)");
	
	my $database_filename=$self->get_sqlite_filename($tablename);
	
	#get first tab path
	my $database_dir=$self->{isip_config}->get_data_dir();
	
	my $database_path=$database_dir."/".$database_filename;
	
	$logger->notice("database already exist at <$database_path>, mise � jour de la structure") if -s $database_path;
	
	if (! -e $database_path) {
		$logger->notice("Creating empty file : $database_path");
		#create empty file
		open DATABASEFILE,">$database_path" or croak "unable to create file : $!";
		close DATABASEFILE;
	}
	
	croak "Impossible de retrouver le fichier cr��" if not $self->get_sqlite_path($tablename);
	
	# opening master table
	my $master_table=Sqlite->open($database_path, 'sqlite_master');
	
	$logger->info("Create table $tablename\_HISTO");
	$master_table->execute("CREATE TABLE IF NOT EXISTS $tablename\_HISTO (
	ID INTEGER PRIMARY KEY,
	DATE_HISTO DATETIME,
	USER_UPDATE VARCHAR(30),
	DATE_UPDATE DATETIME,
	TABLE_NAME VARCHAR(30),
	TABLE_KEY VARCHAR(30),
	FIELD_NAME VARCHAR(30),
	FIELD_VALUE VARCHAR(30),
	PROJECT VARCHAR(30),
	COMMENT VARCHAR(50),
	STATUS VARCHAR(30),
	MEMO VARCHAR(30))");
	
	$logger->info("Create table $tablename\_COLUMN");
	$master_table->execute("CREATE TABLE IF NOT EXISTS $tablename\_COLUMN (
		TABLE_NAME VARCHAR(30) NOT NULL,
		FIELD_NAME VARCHAR(30) NOT NULL,
		DATE_HISTO VARCHAR(30) NOT NULL,
		DATE_UPDATE VARCHAR(30),
		USER_UPDATE VARCHAR(30),
		DATA_TYPE VARCHAR(30),
		DATA_LENGTH VARCHAR(30),
		TEXT VARCHAR(30),
		TYPE VARCHAR(30),
		PRIMARY_KEY NUMERIC,
		FOREIGN_TABLE VARCHAR(30),
		FOREIGN_KEY VARCHAR(30),
		COLNO NUMERIC,
		PRIMARY KEY (TABLE_NAME,FIELD_NAME,DATE_HISTO)
	)");
	
	$logger->info("Create table $tablename\_CATEGORY");
	$master_table->execute("CREATE TABLE IF NOT EXISTS $tablename\_CATEGORY (
	TABLE_KEY VARCHAR(30) PRIMARY KEY NOT NULL,
	CATEGORY VARCHAR(30))");
	
	$logger->info("Replace view $tablename\_HISTO_CATEGORY");
	$master_table->execute("DROP VIEW IF EXISTS $tablename\_HISTO_CATEGORY");
	
	$logger->info("Create indexes");
	$master_table->execute("CREATE INDEX IF NOT EXISTS IDX_TABLE_KEY ON $tablename\_HISTO (TABLE_KEY ASC)");
	$master_table->execute("CREATE INDEX IF NOT EXISTS IDX_TABLE_FIELD ON $tablename\_HISTO (FIELD_NAME ASC)");

	
	$master_table->close();
	
}

sub create_histo_baseline() {
	my $self=shift;

	my $table_name=shift;
	my $baseline_date=shift or croak("usage : create_histo_baseline(table_name,baseline_date)");
	
	my $explore_date=$baseline_date;
	$baseline_date =~ tr/-://d;
	
	my $baseline_name=$table_name.'_'.$baseline_date;
	
	if ( $self->exist_local_table($baseline_name) ) {
		# baseline already created, we reuse it
		return 0;
	}
	
	my $table_histo = $self->open_local_from_histo_table($table_name);
	$table_histo->query_date($explore_date);
	
	
	
	my $select_query=$table_histo->get_query();
	undef $table_histo;
	
	my $baseline_table=$self->open_local_table($table_name."_HISTO");
	
	$logger->info("Create $baseline_name dans ".$self->{environnement});
	$baseline_table->execute("CREATE TABLE IF NOT EXISTS $baseline_name (
	ID INTEGER PRIMARY KEY,
	DATE_HISTO DATETIME,
	USER_UPDATE VARCHAR(30),
	DATE_UPDATE DATETIME,
	TABLE_NAME VARCHAR(30),
	TABLE_KEY VARCHAR(30),
	FIELD_NAME VARCHAR(30),
	FIELD_VALUE VARCHAR(30),
	PROJECT VARCHAR(30),
	COMMENT VARCHAR(50),
	STATUS VARCHAR(30),
	MEMO VARCHAR(30))");
	
	$baseline_table->execute("INSERT INTO $baseline_name ".$select_query);
	$logger->info("Create indexes on $baseline_name dans ".$self->{environnement});
	$baseline_table->execute("CREATE INDEX IF NOT EXISTS IDX_TABLE_KEY_$baseline_date ON $baseline_name (TABLE_KEY ASC)");
	
	return 1;
}

sub drop_histo_baseline() {
	my $self=shift;

	my $table_name=shift;
	my $baseline_date=shift or croak("usage : create_histo_baseline(table_name,baseline_date)");
	
	my $explore_date=$baseline_date;
	$baseline_date =~ tr/-://d;
	
	my $baseline_name=$table_name.'_'.$baseline_date;
	
	my $database_path=$self->get_sqlite_path($table_name."_HISTO");
	return 0 if not $database_path;
	
	# ouverture sqlite_master
	my $master_table=Sqlite->open($database_path, 'sqlite_master', @_);
	
	$master_table->execute("DROP TABLE $baseline_name");
	#$master_table->execute("VACUUM");
}

# method to delete a _HISTO table
#  only move HISTO to a fake baseline, to be clean later
#  return the fake baseline date
sub drop_histo() {
	my $self=shift;

	my $table_name=shift or croak("usage : drop_histo_rename(table_name)");
	
	my $counter=0;
	my $fake_baseline_date = "00000000T";
	my $new_histo_name=$table_name.'_'.$fake_baseline_date.sprintf("%04d",$counter);
	while ($self->exist_local_table($new_histo_name)) {
		$counter++;
		$new_histo_name=$table_name.'_'.$fake_baseline_date.sprintf("%04d",$counter);
	}

	my $master_table=$self->open_local_table($table_name."_HISTO");
	
	$logger->info("rename ".$table_name."_HISTO to ".$new_histo_name." dans ".$self->{environnement});
	$master_table->execute("ALTER TABLE ".$table_name."_HISTO RENAME TO ".$new_histo_name);
	$logger->info("delete obsolete indexes");
	$master_table->execute("DROP INDEX IDX_TABLE_KEY");
	$master_table->execute("DROP INDEX IDX_TABLE_FIELD");
	
	# recreate empty HISTO
	$self->create_database_histo($table_name);
	
	return $fake_baseline_date.sprintf("%04d",$counter);;
}

1;


=head1 NAME

Isip::Environnement - Classe g�rant les donn�es d'un environnement I-SIP 

=head1 SYNOPSIS

 use Isip::Environnement;
 my $env = Environnement->new('PRD');
 
 my $table      = $env->open_local_from_histo_table('NATPROP');
 my @table_list = $env->get_table_list();

=head1 DESCRIPTION

La classe Environnement contient les informations d'un environnement.

Voici les informations g�r�es dans un objet Environnement :

=over

=item * Gestion des bases Sqlite.

=item * Information �tendues sur les colonnes des tables.

=item * Liens entre les tables.

=item * Gestion des I<baselines>.

=back

=head2 METHODS

=over 12

=item C<new ($name)>

M�thode d'initialisation. Prend en param�tre le nom de l'environnement.

=item C<open_local_table ($table_name)>

Renvoie un objet de type ITable. La table pass�e en param�tre peut-�tre toute table
physique contenu dans une des bases de l'environnement.

=item C<open_sourcetable ($table_name)>

Renvoie un objet de type ITable. La table pass�e en param�tre doit �tre une table
contenu dans le syst�me d'information source (ODBC).

=item C<open_local_from_histo_table ($table_name [,$date_explore])>

Renvoie un objet de type ITable. La table pass�e en param�tre doit �tre une
table contenu dans le syst�me d'information source. La table renvoy�e sera
la version historis�e dans I-SIP de cette table.

Si une date est fourni en param�tre, la table renvoy�e sera la version de la table
source telle qu'elle a �t� historis�e � cette date. Si une I<baseline> existe � cette date
pr�cise, ce sont les donn�es de cette I<baseline> qui seront renvoy�es.

=item C<get_links ($table_name)>

Renvoie un objet de class ITable::ILink, repr�sentant les relations entre les tables.

=item C<get_links_menu ($table_name)>

Renvoie un objet de class ITable::ILink pour �tre utilis�s dans la g�n�ration des
menus et des fichiers de d�finition de I-SIP. Certaines tables sont renomm�es dans
l'objet renvoy�e, afin de les rendre unique et de faire transparaitre certaines
relations dans leur nom.

Par exemple, si la table TTARIFP est une sous table de OGAP, mais que TTARIFP est marqu�
comme �tant une table racine, TTARIFP est renomm� en TTARIFP__OGAP

=item C<is_root_table ($table_name)>

Renvoie vrai si la table est configur�e comme �tant une table "racine", c'est � dire
devant appara�tre directement sous l'arborescence du module.

=item C<create_database_histo ($table_name)>

Cr�er la base Sqlite qui contiendra les informations d'une table et ses valeurs versionn�es.

=item C<initialize_column_info ($table_name)>

Met � jour une liste versionn�e des colonnes d'une table historis�e.

=item C<create_histo_baseline ($table_name,$date)>

Recopie la version d'une table � une date donn�e dans une table baseline.

=item C<drop_histo ($table_name)>

Supprime toute les donn�es d'historisation d'une table et renvoie une date.

Les donn�es ne sont pas r�ellement supprim�e, mais d�plac�e dans une
baseline, dont la date est cette renvoy�e par la m�thode.
Pour la supprimer d�finitivement, il faut utiliser ensuite C<drop_histo_baseline> sur
cette date.


=item C<drop_histo_baseline ($table_name,$date)>

Supprime une table de baseline � une date donn�es.

=back

=head1 AUTHOR

Copyright (c) 2008 BV Associates. Tous droits r�serv�s.

=cut
