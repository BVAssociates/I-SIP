package FieldDiff;


use Isip::ITable::DataDiff;
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
	
	$self->{comment_field} = ["COMMENT","STATUS","MEMO","PROJECT"];
	
	$self->{update_timestamp}= "";
	$self->{update_user}= "";
	
	return $self;
}

# Only FIELD_VALUE must be compare
sub compare_exclude() {
	my $self=shift;
	
	return grep(!/^FIELD_VALUE$/,$self->field);
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

	# initialize dynamic fields
	foreach ($self->dynamic_field) {
		$current_row->{$_}="" if not exists $current_row->{$_};
	}
	
	# get only requested fields
	my %query_field_row;
	@query_field_row{$self->query_field} = @$current_row{$self->query_field};
	
	if (exists $query_field_row{OLD_FIELD_VALUE}) {
		my %tmp=%$current_row;
		my $key=join(',',@tmp{$self->key});
		if ($query_field_row{DIFF} eq "UPDATE") {		
			my %update=$self->{diff}->get_source_update($key);
			$query_field_row{OLD_FIELD_VALUE}=$update{FIELD_VALUE};
		}
		elsif ($query_field_row{DIFF} eq "DELETE") {
			my %update=$self->{diff}->get_source_only($key);
			$query_field_row{OLD_FIELD_VALUE}=$update{FIELD_VALUE};
			$query_field_row{FIELD_VALUE}="";
		}
	}
	
	return %query_field_row;
}

#@override
sub dispatch_equal() {
	my $self=shift;
	
	my $current_key=shift;
	my $source_row=shift;
	my $target_row=shift or croak("usage:dispatch_source_update(current_key,field,source_row,target_row)");
	
	if ($self->{compare_diff}) {
		# nothing to do
	}
	
	if ($self->{compare_fetch}) {
		$target_row->{DIFF}="OK";
	}
	
	if ($self->{update_comment}) {
		my %new_comment;
		foreach my $field (@{$self->{comment_field}}) {
			if ($source_row->{$field} ne $target_row->{$field}) {
				$new_comment{$field}=$source_row->{$field};
			}
		}
		
		# some comment needs update
		if (%new_comment) {
			my @table_key=sort $self->{table_target}->key();
			
			$self->_debug("Comment updated : Key (".$current_key.") : ".join(",",values %new_comment));
	
		
			# assign current key, needed for updating
			@new_comment{@table_key}=@{$target_row}{@table_key};
			
			# set special fields
			$new_comment{DATE_UPDATE}=$self->{update_timestamp};
			$new_comment{USER_UPDATE}=$self->{update_user};
			
			$self->{table_target}->update_row(%new_comment);
		}
	}
	
	return $target_row;
}

sub set_update_timestamp() {
    my $self = shift;
    
	my $timestamp=shift or croak ("usage : set_update_timestamp(timestamp)");
	
	# ISO 8601 format : 1977-04-22T06:00
	if ( $timestamp !~ /\d{4}-\d{2}-\d{2}T\d{2}:\d{2}/) {
		$self->_error("timestamp must be like 1977-04-22T06:00 (ISO 8601)");
		croak("usage : set_update_timestamp(timestamp)")
	}
	
	$self->{update_timestamp}=$timestamp;
}

sub set_update_user() {
    my $self = shift;
    
	my $user=shift or croak ("usage : set_update_user(user)");
	
	$self->{update_user}=$user;
}


sub update_comment_target() {
	my $self = shift;
	
	$self->{update_comment}=1;
	
	$self->{table_target}->begin_transaction();
	$self->compare();
	$self->{table_target}->commit_transaction();
	
	return;
}