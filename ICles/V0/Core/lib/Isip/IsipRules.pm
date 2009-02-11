package IsipRules;


use Carp qw(carp croak );
use strict;

use Isip::IsipLog '$logger';
use ITable::Sqlite;

=head1 NAME

 Isip::IsipRules - Class to handle type and status
 
=head1 SYNOPSIS

Class to handle type and status.

=author

BV Associates, 2008

=cut

##################################################
##  constructor  ##
##################################################


#create a new IsipRules object
sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	my $self={};
	
	# member initializations
	$self->{current_type}={};
	$self->{current_owner}={};
	$self->{current_description}={};
	
	$self->{debugging}=0;
	
	# constants identifiers enumeration
	# TODO : import them from a configuration file
	%{ $self->{type} } = $class->enum_type;
	%{ $self->{field_status} } = $class->enum_field_status;

	%{ $self->{line_icon} } = $class->enum_line_icon;
	%{ $self->{field_icon} } = $class->enum_field_icon;
	
	%{ $self->{field_diff_icon} } = $class->enum_field_diff_icon;
	%{ $self->{line_diff_icon} } = $class->enum_line_diff_icon;


	# Amen
	bless ($self, $class);
	
	# mandatory parameter
	if (@_ < 2) {
		croak ('\'new\' take 1 mandatory argument: '.$class.'->new(database_name, table_name [, { diff => $TableDiff_ref, debug => \$num} ) ] )')
	}
	
	$self->{database_name}=shift;
	$self->{table_name}=shift;
	
	# To discuss : what table_name is passed to contructor?
	$self->{table_info_name}=$self->{table_name}."_INFO";
	
	# options
	my $options=shift;
	$self->{table_diff}=$options->{diff} if exists $options->{diff};
	$self->debugging($options->{debug}) if exists $options->{debug};


	# load informations
	$self->_init_info();

    return $self;
}



##################################################
##  pivate methods  ##
##################################################

# load the table TABLE_INFO and get type of each column
sub _init_info() {
	my $self=shift;
	
	my $table_info=Sqlite->open(Environnement->get_sqlite_path($self->{table_info_name}),$self->{table_info_name}, { debug => $self->debugging() } );
	
	# narrow query if needed
	#$table_info->query_field("FIELD_NAME","DATE_UPDATE","DATA_TYPE","DATA_LENGTH","TABLE_SCHEMA","TEXT","DESCRIPTION","OWNER","TYPE");
	
	
	while(my %row=$table_info->fetch_row()) {
		$self->{current_type}->{$row{FIELD_NAME}}=$row{TYPE};
		$self->{current_owner}->{$row{FIELD_NAME}}=$row{OWNER};
		$self->{current_description}->{$row{FIELD_NAME}}=$row{TEXT};
	}
}

sub debugging {
    my $self = shift;
    if (@_) { $self->{debugging} = shift}
    return $self->{debugging};
}

##################################################
##  static methods to get constants enumeration ##
##################################################

sub enum_type () {
	my $self=shift;
	
	return (DATA => "fonctionnel", CONFIG => "technique", MANUAL => "manuel", STAMP => "administratif", HIDDEN => "exclus");
}


sub enum_field_status () {
	my $self=shift;
	
	return (EMPTY => "",  OK => "Valide", TEST => "Test", SEEN => "Attente", UNKNOWN => "Inconnu");
}

sub enum_field_icon () {
	my $self=shift;
	
	return (EMPTY => "nouveau",  OK => "valide", TEST => "test", SEEN => "attente", UNKNOWN => "invalide", STAMP => "stamp", HIDDEN => "cache", ERROR => "erreur");
}

sub enum_line_icon () {
	my $self=shift;
	
	return (NEW => "nouveau",  OK => "valide", SEEN => "edit", UNKNOWN => "invalide", ERROR => "erreur");
}

sub enum_field_diff_icon() {
	my $self=shift;
	
	return (NEW => "ajoute", UPDATE => "different", OK => "egal", DELETE => "supprime", STAMP_UPDATE => "stamp_update");
}

sub enum_line_diff_icon() {
	my $self=shift;
	
	return (NEW => "ajoute", UPDATE => "different", OK => "egal", DELETE => "supprime", ERROR => "erreur");
}

##################################################
##  methods to compute status from a Histo line ##
##################################################

#get type keyword of a column
sub get_field_type() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_field_type(col_name)");
	
	my %type_by_name= reverse %{$self->{type}};
	my $type_txt=lc $self->get_field_type_txt($col_name);
	
	my $type;
	if ($type_txt eq "") {
		$type="";
	}
	elsif (not defined $type_by_name{$type_txt}) {
		$type="";
		$logger->error($type_txt." n'est pas un type valide") 
	} else {
		$type=$type_by_name{$type_txt};
	}
	return $type;
	
}

#get type full descruption of a column
sub get_field_type_txt() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_field_type_txt(col_name)");
	
	return $self->{current_type}->{$col_name};
	
}

sub get_field_description() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_type(col_name)");
	
	return $self->{current_description}->{$col_name};
}

# compute the validation status of a field
# param type : type of the field
# param status : current status from histo
# param comment : current comment from histo
# return status : new computed status
sub get_field_icon () {
	my $self=shift;
	
	my $name=shift;
	my $status_desc=shift;
	my $comment=shift;
	
	my %status_by_name= reverse %{$self->{field_status}};
	
	#$logger->debug("get type of ",$name);
	my $type=$self->get_field_type($name);
	
	my $status;
	if (not defined $status_by_name{$status_desc}) {
		$status="";
		$logger->error($self->{table_name}.":<".$status_desc."> n'est pas un status valide") 
	} else {
		$status=$status_by_name{$status_desc};
	}
	
	# new status
	my $return_status;
	
	if ($type eq "STAMP") {
	# "Administratif always OK
		$return_status=$self->{field_icon}{STAMP};
		#$return_status=$self->{field_status}{HIDDEN};
	}
	elsif ($type eq "EXCLUDE" or $type eq "HIDDEN") {
		$return_status=$self->{field_icon}{OK};
	}
	else {
		if ($status eq "OK") {
			$return_status=$self->{field_icon}{OK};
		}
		elsif ($status eq "EMPTY") {
			$return_status=$self->{field_icon}{EMPTY};
		}
		elsif ($status eq "TEST") {
			$return_status=$self->{field_icon}{TEST};
		}
		elsif ($status eq "SEEN") {
			$return_status=$self->{field_icon}{SEEN};
		}
		elsif ($status eq "UNKNOWN") {
			$return_status=$self->{field_icon}{UNKNOWN};
		}
	}
	
	return $return_status;
}

# return the computed status of a line
#  - if set_diff has been called before, it will
# use it to return the "diff" status
#  - if no set_diff, return status  
# param icon_list : list of status of each field
# return status : computed status
sub get_line_icon () {
	my $self=shift;
	
	my @icon_list=@_;
	my $return_icon;
	
	my %icon_by_name= reverse %{$self->{field_icon}};
	
	my %counter=(EMPTY => 0, UNKNOWN => 0, SEEN => 0, TEST => 0, OK => 0);
	
	foreach (@icon_list) {
		my $icon;
		if (not defined $_) {
			$logger->critical("Impossible de determiner l'icone de la ligne, car un champ n'a pas d'icone") ;
			return $self->{line_icon}{ERROR};
			last;
		}
		elsif (not defined $icon_by_name{$_}) {
			
			$logger->critical("Impossible de determiner l'icone de la ligne, car $_ n'est pas un icone valide") ;
			return $self->{line_icon}{ERROR};
			last;
		} else {
			$icon=$icon_by_name{$_};
		}
		
		$counter{$icon}++;
		
	}
	
	return $self->{line_icon}{NEW} if $counter{EMPTY} > 0;
	return $self->{line_icon}{UNKNOWN} if $counter{UNKNOWN} > 0;
	return $self->{line_icon}{SEEN} if $counter{TEST} > 0;
	return $self->{line_icon}{SEEN} if $counter{SEEN} > 0;
	return $self->{line_icon}{OK} if $counter{OK} > 0;

}


sub get_field_diff_icon () {
	my $self=shift;
	
	my $name=shift;
	my $diff=shift;
	
	my $type=$self->get_field_type($name);
	
	if ($type eq "HIDDEN") {
		return $self->{field_diff_icon}->{OK};
	}
	
	# TODO write display rules?
	return $self->{field_diff_icon}->{$diff};
}

sub get_line_diff_icon() {
	my $self=shift;
	
	my @icon_list=@_;
	my $return_icon;
	
	my %icon_by_name= reverse %{$self->{field_diff_icon}};
	
	my %counter;
	foreach (keys %{$self->{field_diff_icon}} ) {
		$counter{$_}=0;
	}
	
	foreach (@icon_list) {
		my $icon;
		if (not defined $_) {
			$logger->critical("Impossible de determiner l'icone de la ligne, car un champ n'a pas d'icone") ;
			return $self->{line_diff_icon}{ERROR};
			last;
		}
		elsif (not defined $icon_by_name{$_}) {
			
			$logger->critical("Impossible de determiner l'icone de la ligne, car $_ n'est pas un icone valide") ;
			return $self->{line_diff_icon}{ERROR};
			last;
		} else {
			$icon=$icon_by_name{$_};
		}
		
		$counter{$icon}++;
		
	}
	
	return $self->{line_diff_icon}{UPDATE} if $counter{UPDATE} > 0;
	return $self->{line_diff_icon}{NEW} if $counter{NEW} > 0;
	return $self->{line_diff_icon}{DELETE} if $counter{DELETE} > 0;
	return $self->{line_diff_icon}{OK} if $counter{OK} > 0;
}

1;


