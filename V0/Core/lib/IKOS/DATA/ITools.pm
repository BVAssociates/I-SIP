package ITools;

use IKOS::DATA::ITools::Define;
use Carp qw(carp cluck confess croak );

use strict;


##################################################
##  Table object Factory  ##
##################################################
sub open (){
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my $self  = {};
	
	bless($self, $class);


    my $table_name = shift;
	my $options = shift;
	my $required_module;
	
	my $define_obj = Define->new($table_name, $options );
	
	return undef if not defined $define_obj;
	
	if (not defined $define_obj->type() ) {
		croak("Define->type not defined for $table_name");
	}
	
	$required_module = ucfirst(lc($define_obj->type() ) );
	
	# Load the correct object for TYPE
	eval "require IKOS::DATA::ITools::$required_module";
	if ($@) {
		croak("$@\nUnable to find the module for definition TYPE : ".$define_obj->type());
	}
	
    return $required_module->open($define_obj,$options);
}

1;  # so the require or use succeeds



=head1 NAME

 Package ITools
 
 Provide functions to access ITools table (See Class Table)
 Also provide useful fonctions used in "ICles" scripts


=head1 SYNOPSIS


 my $table = open_table("table_name");
 
=head1 DESCRIPTION

See ITools::Table::Abstract for more information on Table object.