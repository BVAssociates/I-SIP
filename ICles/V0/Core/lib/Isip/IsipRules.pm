package IsipRules;

use fields qw(
	line_icon
	field_icon
	
	environnement
	field_status
	column_info
	type
	table_name
);


use Carp qw(carp croak );
use strict;
use Scalar::Util qw(blessed);

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
	
	my $self= fields::new($class);
	
	# member initializations
	$self->{column_info}={};
	
	# constants identifiers enumeration
	# TODO : import them from a configuration file
	%{ $self->{type} } = $class->enum_type;
	%{ $self->{field_status} } = $class->enum_field_status;

	$self->{line_icon} = { $class->enum_line_icon };
	$self->{field_icon} = { $class->enum_field_icon };

	# mandatory parameter
	if (@_ < 1) {
		croak ('\'new\' take 2 mandatory argument: '.$class.'->new(table, environnement )')
	}

	$self->{table_name}=shift;
	$self->{environnement}=shift;
	
	croak ('\'new\' take 2 mandatory argument: '.$class.'->new(table, environnement )') if not blessed $self->{environnement};
	

	
	# options
	my $options=shift;


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
	
	
	# narrow query if needed
	#$table_info->query_field("FIELD_NAME","DATE_UPDATE","DATA_TYPE","DATA_LENGTH","TABLE_SCHEMA","TEXT","DESCRIPTION","OWNER","TYPE");
	
	$self->{column_info}={ $self->{environnement}->get_column_info($self->{table_name}) };
	
}

##################################################
##  static methods to get constants enumeration ##
##################################################

sub enum_type () {
	my $self=shift;
	
	return (KEY => "clef", DATA => "fonctionnel", CONFIG => "technique", MANUAL => "manuel", STAMP => "administratif", HIDDEN => "exclus");
}

sub enum_field_status () {
	my $self=shift;
	
	return (UPDATED => "",  OK => "Valide", TEST => "Test", SEEN => "Attente", UNKNOWN => "Inconnu");
}

sub enum_field_icon () {
	my $self=shift;
	
	return (NEW => "nouveau", UPDATED => "modifie",  OK => "valide", TEST => "test", SEEN => "attente", UNKNOWN => "invalide", STAMP => "stamp", HIDDEN => "cache", ERROR => "erreur");
}

sub enum_line_icon () {
	my $self=shift;
	
	return (NEW => "nouveau", UPDATED => "modifie",  OK => "valide", SEEN => "edit", UNKNOWN => "invalide", ERROR => "erreur");
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
	
	my $type = $self->{column_info}->{$col_name}->{type};
	$type="" if not defined $type;
	return $type;
}

sub get_field_description() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_type(col_name)");
	
	my $desc=$self->{column_info}->{$col_name}->{description};
	
	$desc="" if not defined $desc;
	return $desc;
}

sub is_field_hidden() {
	my $self=shift;
	
	my %line=@_;
	my $name=$line{FIELD_NAME};
	
	my $type=$self->get_field_type($name);
		
	if ($type eq "HIDDEN") {
		return 1;
	}
	else {
		return 0;
	}
}



# compute the validation status of a field
# param type : type of the field
# param status : current status from histo
# param comment : current comment from histo
# return status : new computed status
sub get_field_icon () {
	my $self=shift;
	
	my %line=@_;
	
	my $name=$line{FIELD_NAME};
	my $status_desc=$line{STATUS};
	my $project=$line{PROJECT};
	my $comment=$line{COMMENT};
	
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
	# case of new line
	elsif ($type eq "KEY" and $status eq "UPDATED") {
		$return_status=$self->{field_icon}{NEW};
	}
	elsif ($type eq "EXCLUDE") {
		$return_status=$self->{field_icon}{OK};
	}
	else {
		if ($status eq "OK") {
			# PROJECT must be filled to be OK
			# except at base creation
			if ($project or $comment eq "Creation") {
				$return_status=$self->{field_icon}{OK};
			}
			else {
				$return_status=$self->{field_icon}{UNKNOWN};
			}
		}
		elsif ($status eq "UPDATED") {
			$return_status=$self->{field_icon}{UPDATED};
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
	
	my %counter=(NEW => 0, UPDATED => 0, UNKNOWN => 0, SEEN => 0, TEST => 0, OK => 0);
	
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
	
	return $self->{line_icon}{NEW} if $counter{NEW} > 0;
	return $self->{line_icon}{UPDATED} if $counter{UPDATED} > 0;
	return $self->{line_icon}{UNKNOWN} if $counter{UNKNOWN} > 0;
	return $self->{line_icon}{SEEN} if $counter{TEST} > 0;
	return $self->{line_icon}{SEEN} if $counter{SEEN} > 0;
	return $self->{line_icon}{OK} if $counter{OK} > 0;

}


1;


