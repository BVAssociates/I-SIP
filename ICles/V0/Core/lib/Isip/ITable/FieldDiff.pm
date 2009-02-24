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
	
	
	$self->{dynamic_field} = ["OLD_FIELD_VALUE","DIFF"];
	
	# Only FIELD_VALUE must be compare
	$self->compare_exclude(grep(!/^FIELD_VALUE|FIELD_NAME$/,$self->query_field));
	
	return $self;
}

sub fetch_row() {
	my $self=shift;

	my %query_field_row=$self->_fetch_row_static();
	
	return () if not %query_field_row;

	my $key=join(',',@query_field_row{sort $self->key()});
	
	# compute internal dynamic fields
	if (grep ('^ICON$', $self->query_field()) ) {
	
		if (not blessed $self->{isip_rules}) {
			carp ("no IsipRules set");
			$query_field_row{ICON}="isip_error";
		}
		else {
		
			my %line_diff_icon=$self->{isip_rules}->enum_line_icon();
			my $return_status="";
			
			#dont check excluded fields
			if (not grep {$query_field_row{FIELD_NAME} eq $_} $self->compare_exclude())
			{
				$query_field_row{DIFF} = $self->{diff}->get_field_status($key,"FIELD_VALUE");
				$return_status=$self->{isip_rules}->get_field_icon(%query_field_row);
			}
			
			$query_field_row{ICON}=$return_status;
		}
	}

	
	
	if ($query_field_row{ICON} eq "different") {
		my %update=$self->{diff}->get_source_update($key);
		$query_field_row{OLD_FIELD_VALUE}=$update{FIELD_VALUE};
	}
	
	return %query_field_row;
}