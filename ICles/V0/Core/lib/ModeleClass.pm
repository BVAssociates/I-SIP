package ModeleClass;

use strict;

use base qw(SuperClass);

use fields qw(
	var1
	var2
	obj1
	
	_var3
);


use Carp qw(carp croak );
use Scalar::Util qw(blessed);

=head1 NAME

 ModeleClass - Modele de creation d'une classe avec les modules base et fields
 
=head1 SYNOPSIS

 use ModeleClass;
 my $object=ModeleClass->new("texte", $object2);
 $object->get_var3("autre texte");
 print $object->get_var3();
 print $object->{var2};
 
=head1 DESCRIPTION

Dans cet exemple, nous cr�ons la Class ModeleClass,
qui h�rite de la class SuperClass.

Le constructeur prend 2 parametre, dont le 2e doit �tre une
r�f�rence sur un objet de type AnotherClass.

Cette class poss�de 3 membres publics et un membre priv�.

=head1 AUTHOR

BV Associates, 2009

=cut

##################################################
##  constructor  ##
##################################################

sub new() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	# cr�� object
	my $self= fields::new($class);
	
	# appelle SuperClass's constructor avec les m�mes arguments
	$self->SUPER::new(@_);
	
	# verification des parametre
	if (@_ < 2) {
		croak ("usage ; ".__PACKAGE__.'->new(var1,obj1)');
	}

	$self->{var1}=shift;
	my $param2=shift;
	
	# le parametre 2 doit �tre un object de type AnotherClass
	if (not blessed($param2) or ref $param2 ne "AnotherClass") {
		croak ("usage ; ".__PACKAGE__.'->new(var1,obj1)');
	}
	$self->{obj1}=$param2;
	
	# initialisation var2 en tableau
	$self->{var2}=[];
	
	# initialisation des membres priv�s
	$self->{_var3}="constante";

    return $self;
}



##################################################
##  pivate methods  ##
##################################################

sub _something {
	my $self=shift;
	
	# recup�ration du tableau par sa r�f�rence
	push @{$self->{var2}}, "something";
}

##################################################
##  public methods  ##
##################################################

sub get_var3 {
	my $self=shift;
	
	return $self->{_var3};
}

sub set_var3 {
	my $self=shift;
	
	my $var3_value=shift or croak("usage ".__PACKAGE__."->set_var3(var3_value)");
	
	$self->{_var3}=$var3_value;
}


1;


