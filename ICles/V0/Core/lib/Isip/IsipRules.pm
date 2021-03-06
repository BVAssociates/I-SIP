package IsipRules;

use fields qw(
	line_icon
	field_icon
	
	environnement
	field_status
	column_info
	label
	type
	type_reverse
	table_name
);


use Carp qw(carp croak );
use strict;
use Scalar::Util qw(blessed);
use POSIX qw(mktime);

use Isip::IsipLog '$logger';
use ITable::Sqlite;

=head1 NAME

 Isip::IsipRules - Class to handle type and status
 
=head1 SYNOPSIS

Class to handle type and status.

=head1 AUTHOR

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
	$self->{column_info}={};  # ref object HistoColumns
	$self->{label}={};
	
	# constants identifiers enumeration
	# TODO : import them from a configuration file
	%{ $self->{type} } = $class->enum_type;
	%{ $self->{type_reverse} } = reverse $class->enum_type;
	%{ $self->{field_status} } = $class->enum_field_status;

	$self->{line_icon} = { $class->enum_line_icon };
	$self->{field_icon} = { $class->enum_field_icon };

	# mandatory parameter
	if (@_ < 1) {
		croak ('\'new\' take 2 mandatory argument: '.$class.'->new(table, Environnement_ref )')
	}

	$self->{table_name}=shift;
	$self->{environnement}=shift;
	
	croak ('\'new\' take 2 mandatory argument: '.$class.'->new(table, Environnement_ref )') if not blessed $self->{environnement};
	

	
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
	
	# get HistoColumns ref object
	$self->{column_info}=$self->{environnement}->get_columns($self->{table_name});
	
	# compute the labels
	if ($self->{environnement}->exist_local_table("FIELD_LABEL")) {
		my $table_status=$self->{environnement}->open_local_table("FIELD_LABEL");
		
		$table_status->query_condition("TABLE_NAME = '$self->{table_name}'");
		
		while (my %row=$table_status->fetch_row()) {
			
			if ( $row{"FIELD_NAME"} and $row{"FIELD_NAME"} ne '*' ) {
				$self->{label}->{ $row{"TABLE_KEY"} }->{ $row{"FIELD_NAME"} }=$row{LABEL};
			}
			else {
				# if FIELD_NAME doesn't exists, whole line
				$self->{label}->{ $row{"TABLE_KEY"} }->{ '*' } =$row{LABEL};
			}
		}
	}
	
}

##################################################
##  static methods to get constants enumeration ##
##################################################

sub enum_type () {
	my $self=shift;
	
	return (KEY => "clef", DATA => "", STAMP => "toujours valide", HIDDEN => "exclus");
}

sub enum_field_status () {
	my $self=shift;
	
	return (UPDATED => "",  OK => "Valide", TEST => "Test");
}

sub enum_field_icon () {
	my $self=shift;
	
	return (
			NEW     => "nouveau",
			UPDATED => "modifie",
			OK      => "valide",
			TEMPO   => "valide_tempo",
			IGNORE  => "valide_label",
			TEST    => "test",
			STAMP   => "stamp",
			HIDDEN  => "cache",
			ERROR   => "inconnu",
		);
}

sub enum_line_icon () {
	my $self=shift;
	
	return (
			NEW     => "nouveau",
			UPDATED => "modifie",
			OK      => "valide",
			TEMPO   => "valide_tempo",
			IGNORE  => "valide_label",
			TEST    => "test",
			ERROR   => "erreur",
		);
}

##################################################
##  methods to compute status from a Histo line ##
##################################################

#get type keyword of a column
sub get_field_type() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_field_type(col_name)");
	
	my $type_txt=lc $self->get_field_type_txt($col_name);
	
	my $type;
	if ($type_txt eq '') {
		$type='';
	}
	elsif (not exists $self->{type_reverse}->{$type_txt}) {
		$type='ERROR';
		$logger->error($type_txt." n'est pas un type valide") 
	} else {
		$type=$self->{type_reverse}->{$type_txt};
	}
	return $type;
	
}

#get type full descruption of a column
sub get_field_type_txt() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_field_type_txt(col_name)");
	
	my $type = $self->{column_info}->get_type($col_name);
	$type='' if not defined $type;
	return $type;
}

sub get_field_description() {
	my $self=shift;
	
	my $col_name=shift or croak("usage get_type(col_name)");
	
	my $desc=$self->{column_info}->get_field_txt($col_name);
	
	$desc="" if not defined $desc;
	return $desc;
}

# retourne vrai si le champ est dans une p�riode de temporisation
sub is_field_tempo() {
	my $self=shift;
	my %line=@_;
	
	my $date_update=$line{DATE_UPDATE};
	
	return if ! $date_update;
	
	my $date_limit=$self->{environnement}->get_last_date_histo();
	
	my $unix_date_update = $self->{environnement}->date_to_unix($date_update);
	my $unix_date_limit  = $self->{environnement}->date_to_unix($date_limit);
	
	if( ($unix_date_update - $unix_date_limit) > 0) {
		return 1;
	}
	else {
		return;
	}
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

sub get_hidden_field_list() {
	my $self=shift;
	
	my @field_list=$self->{column_info}->get_field_list();
	my @hidden_list;
	foreach my $field ( @field_list ) {
	
		my $type=$self->get_field_type($field);
		push @hidden_list, $field if grep { $type eq $_ } ('HIDDEN','STAMP');
	}
	
	return @hidden_list;
}

sub get_hidden_key_hash() {
	my $self=shift;
	
	my %hidden_field_for;
	while (my ($key,$hash) = each %{$self->{label}} ) {
		while (my ($field,$label) = each %{$hash} ) {
			$hidden_field_for{$key}=$field if $label eq 'OK';
		}
	}
	return %hidden_field_for;
}

# compute the validation status of a field
# param type : type of the field
# param status : current status from histo
# param comment : current comment from histo
# return status : new computed status
sub get_field_icon () {
	my $self=shift;
	
	my %line=@_;
	
	my $key=$line{TABLE_KEY};
	my $name=$line{FIELD_NAME};
	my $status_desc=$line{STATUS};
	my $project=$line{PROJECT};
	my $comment=$line{COMMENT};
	
	if ( grep { not defined $_ } ($key, $name, $status_desc) ) {
		$logger->error("Impossible de determiner le status de la ligne");
		return $self->{field_icon}{ERROR};
	}
	
	
	my %status_by_name= reverse %{$self->{field_status}};
	
	#$logger->debug("get type of ",$name);
	my $type=$self->get_field_type((split(',',$name))[0]);
	
	my $status;
	if (not defined $status_by_name{$status_desc}) {
		$status="";
		$logger->error($self->{table_name}.":<".$status_desc."> n'est pas un status valide") 
	} else {
		$status=$status_by_name{$status_desc};
	}
	
	# new status
	my $return_status;
	
	# Use label from table _LABEL
	if ( exists $self->{label}->{$key}->{ '*' } ) {
		# all field are labeled
		my $label_value=$self->{label}->{$key}->{ '*' };
		
		$return_status=$self->{field_icon}{$label_value}."_label";
	}
	elsif ( exists $self->{label}->{$key}->{$name} ) {
		# check if field has a label
		my $label_value=$self->{label}->{$key}->{$name};
				
		$return_status=$self->{field_icon}{$label_value}."_label";
	}
	# case of new line
	elsif ($type eq "STAMP") {
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
	elsif ($type eq "ERROR") {
		$return_status=$self->{field_icon}{ERROR};
	}
	else {
		if ($status eq "OK") {
			# PROJECT must be filled to be OK
			# except at base creation
			
			#if ($project or $comment eq "Creation") {
			if ( not $comment ) {
				$return_status=$self->{field_icon}{TEST};
			}
			else {
				$return_status=$self->{field_icon}{OK};
				
				if ( $self->is_field_tempo(%line) ) {
					$return_status .= '_tempo';
				}
			}
		}
		elsif ($status eq "UPDATED") {
			$return_status=$self->{field_icon}{UPDATED};
		}
		elsif ($status eq "TEST") {
			$return_status=$self->{field_icon}{TEST};
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
	
	# initialise le compteur des etats
	my %counter;
	foreach my $count ( values %counter ) {
		#$count = 0;
	}
	
	foreach my $field_icon (@icon_list) {
	
		# remove label information
		
		if ( defined $field_icon and $field_icon =~ /_label$/ ) {
			$field_icon =~ s/_label$//;
			$counter{IGNORE}++;
		}
	
		my $line_icon;
		if ( not defined $field_icon ) {
			$logger->critical("Impossible de determiner l'icone de la ligne, car un champ n'a pas d'icone") ;
			return $self->{line_icon}{ERROR};
			last;
		}
		elsif (not defined $icon_by_name{$field_icon}) {
			
			$logger->critical("Impossible de determiner l'icone de la ligne, car $field_icon n'est pas un icone valide") ;
			return $self->{line_icon}{ERROR};
			last;
		} else {
			$line_icon=$icon_by_name{$field_icon};
		}
		
		$counter{$line_icon}++;
		
	}
	
	if ( (exists $counter{IGNORE}) && ($counter{IGNORE} == @icon_list)) {
		return $self->{line_icon}{IGNORE};
	}
	
	return $self->{line_icon}{NEW} if $counter{NEW};
	return $self->{line_icon}{UPDATED} if $counter{UPDATED};
	return $self->{line_icon}{TEST} if $counter{TEST};
	return $self->{line_icon}{TEMPO} if $counter{TEMPO};
	return $self->{line_icon}{OK} if $counter{OK};
	return $self->{line_icon}{ERROR};

}


1;


