package FieldDiff;


require Isip::ITable::DataDiff;
@ISA = ("DataDiff");

use Carp qw(carp cluck confess croak );
use strict;
use Scalar::Util qw(blessed);

#use Data::Dumper;

##################################################
##  constructor  ##
##################################################

# open an existing table on a Sqlite Database
sub open() {
	my $proto = shift;
    my $class = ref($proto) || $proto;	
	
	# mandatory parameter
	if (@_ < 2) {
		croak ($class.'->open : take 2 mandatory argument: ${class}->open( $data_ref1, $date_ref2[ ,{ timeout => $sec, debug => $num} ])')
	}
	
	my $self=bless( $class->SUPER::open(@_), $class);
	
	
	$self->{dynamic_field} = [ $self->dynamic_field ,"OLD_FIELD_VALUE"];
	
	return $self;
}

# Only FIELD_VALUE must be compare
sub compare_exclude() {
	my $self=shift;
	
	return grep(!/^FIELD_VALUE|FIELD_NAME$/,$self->query_field);
}

sub fetch_row() {
	my $self=shift;

	croak(__PACKAGE__."->compare_init() must be called before ".__PACKAGE__."->fetch_row()") if not blessed $self->{diff};
	
	$self->{compare_diff}=1;
	$self->{compare_fetch}=1;
	
	# first, printing lines only in source
	my $current_row=$self->compare_next();
	
	# table_target return no lines, so we return
	if (not $current_row) {
		$self->{compare_diff}=0;
		$self->{compare_fetch}=0;
		return ();
	}
	

	
	# compute internal dynamic fields
	if (grep ('^ICON$', $self->query_field()) ) {
	
		if (not blessed $self->{isip_rules}) {
			carp ("no IsipRules set");
			$current_row->{ICON}="isip_error";
		}
		else {
		
			my %line_diff_icon=$self->{isip_rules}->enum_line_icon();
			my $return_status="";
			
			#dont check excluded fields
			if (not grep {$current_row->{FIELD_NAME} eq $_} $self->compare_exclude())
			{
				$return_status=$self->{isip_rules}->get_field_icon(%$current_row);
			}
			
			$current_row->{ICON}=$return_status;
		}
	}

	# get only requested fields
	my %query_field_row;
	@query_field_row{$self->query_field} = @$current_row{$self->query_field};
	
	if (exists $query_field_row{OLD_FIELD_VALUE}) {
		my %tmp=%$current_row;
		my $key=join(',',@tmp{$self->key});
		
		if ($query_field_row{ICON} eq "different") {
			my %update=$self->{diff}->get_source_update($key);
			$query_field_row{OLD_FIELD_VALUE}=$update{FIELD_VALUE};
		}
		elsif ($query_field_row{ICON} eq "supprime") {
			my %update=$self->{diff}->get_source_only($key);
			$query_field_row{OLD_FIELD_VALUE}=$update{FIELD_VALUE};
			$query_field_row{FIELD_VALUE}="";
		}
	}
	
	return %query_field_row;
}
