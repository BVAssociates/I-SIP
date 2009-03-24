package XmlFile;

require ITable::abstract::DATA_interface;
@ISA = ("DATA_interface");

use Carp qw(carp cluck confess croak );
use strict;

use Xml::Simple;
$XML::Simple::PREFERRED_PARSER='XML::Parser';


# open()
sub open() {
    my $proto = shift;
    my $class = ref($proto) || $proto;
	
	# mandatory parameter
	if (@_ < 2) {
		croak ('\'new\' take 1 mandatory argument: ${class}->open(xml_path,tablename [, {debug => $num}] )')
	}
	
	my $options= pop @_ if @_ > 2;
	my $xml_file=shift;
	my $table_name=shift;
	
	my $self  = $class->SUPER::open($table_name, $options);
	
	$self->{xml_file} = $xml_file;

	############
	# Data members
	############
	
	# internal description
	$self->{key} = ["xml_path"];
	$self->{field} = [ "xml_path","value" ];
	$self->{size} = {xml_path => "VARCHAR(50)", value => "VARCHAR(50)" };
	$self->{field_txt} = {xml_path => "XML_path", value => "leaf_value" };

	# user query
	$self->{query_field}  = [ "xml_path","value" ];
	
	############
	# Bless Object Again. Amen.
	############

    bless($self, $class);
		
	# put XML into memory
	$self->{node_hash}={};
	$self->_load_xml();
	
	# store the key while fetching
	$self->{remaining_keys}= [ keys %{$self->{node_hash}} ];
	
    return $self;
}

##############################################
## private methods         ##
##############################################

sub _load_xml() {
	my $self=shift;
	
	my $xml_simple_hash=eval { XML::Simple::XMLin($self->{xml_file}) };
	croak("Impossible de charger le fichier XML <$self->{xml_file}>. Erreur:",$@) if($@);
	
	$self->_push_node($xml_simple_hash,"");
}

# recursive methode to 
# transform XML struct into a flat hash structure :
#   <a attribut="att">
#		<b>value</b>
#   </a>
#
# into :
#
#	$self->{node_hash}->{/a/attribut}="att"
#	$self->{node_hash}->{/a/b}="value"
#
sub _push_node() {
	my $self=shift;
	
	my $node=shift;
	my $current_tree=shift;
  
	# we are on a leaf
	if (!ref($node)) {
		$node =~ s/\n\s+//g;
		
		# Simple::Xml use UTF-8 as internal representation
		# So we must decode back it before use
		utf8::decode($node) ;
		
		$self->{node_hash}->{$current_tree}=$node;
	}
	# we are on a node
	elsif (ref($node) eq "HASH") {
		# empty HASH means an empty value
		if (not %{$node}) {
			$self->{node_hash}->{$current_tree}="";
		} else {	
			foreach my $cle (keys %{$node}) {
				$self->_push_node($node->{$cle},$current_tree.'/'.$cle);
			}
		}
	}
	# we are on a mutliple entry node
	# TODO find a way to store multiple value for a key
	elsif (ref($node) eq "ARRAY") {
		die  "multiple leaf entry not implemented";
	}
	
}


sub fetch_row_array() {
	my $self=shift;
	
	if (not @{ $self->{remaining_keys} }) {
		# repopulate for next call
		$self->{remaining_keys}= [ keys %{$self->{node_hash}} ];
		
		#return nothing
		return ();
	} else {
	
		my $fetch_key=shift @{ $self->{remaining_keys} };
		my $fetch_value=$self->{node_hash}->{$fetch_key};
		chomp ($fetch_key);
		chomp ($fetch_value);

		use Data::Dumper;
		#die Dumper($fetch_value) if $fetch_key eq '/immobilier/service/business/transformers/serviceCriterionTextTransformer/convertCharacters/from';
		
		return ($fetch_key, $fetch_value);
	}
}