#! /bin/perl
#
#	I-SIS, 2006
#
#@I-SIS	APP : I-SIS					$Revision: 1.3 $
#
#@I-SIS	I-CLES : I-TOOLS
#
#@I-SIS	Date : 04/10/2006				Auteur : T. Zumbiehl
#								(BV Associates)
#	Historique des modifications
#
#	-------------------------------------------------------------------
#	|     Date	|    Auteur   |		 Description		  |
#	-------------------------------------------------------------------
#	| 20/07/2007	|	TZ    | Utilisation de Get_PCI		  |
#	-------------------------------------------------------------------
#
#@I-SIS	FONCTION : Modification dans une table avec post-action
#
#@I-SIS	USAGE :  ReplaceAndExec [-h] INTO <Table> VALUES <-|Values> \
#		[WHERE <Condition>]
#
#@I-SIS	OPT : "-h pour avoir l'utilisation de la ligne de commande"
#@ISIS	OPT : "-f pour forcer la création du fichier de données"
#
#@I-SIS	ARG : "Table : Nom de la table dans laquelle insérer les valeurs"
#@I-SIS	ARG : "- : Utiliser les valeurs provenant de l'entrée standard"
#@I-SIS	ARG : "Values : Valeurs à insérer dans la table"
#@I-SIS	ARG : "Condition: Condition de sélection de la ligne à modifier"
#
#@ISIS	ENV : "TOOLS_HOME : Répertoire d'installation des I-TOOLS"
#@ISIS	ENV : "BV_DEFPATH : Liste des chemins de recherche des dictionnaires"
#@ISIS	ENV : "BV_TABPATH : Liste des chemins de recherche des fichiers de 
#			  données"
#
##########################################################################

##########################
#  IMPORTS DE LIBRAIRIE  #
##########################

$TOOLS_HOME = $ENV{"TOOLS_HOME"};
if($TOOLS_HOME ne "")
{
	unshift(@INC, "$TOOLS_HOME\\lib");
}
require ("CommonFunctions.pl");

########################
#  FONCTIONS INTERNES  #
########################

# Fonction Trigger_Action
# Cette fonction est chargée de récupérer une commande liée au processus
# (Insertion, Modification ou Suppression), et à l'action en cours (Pré-action
# ou Post-action).
# Le processus est passé en 1er argument
# L'action est passée en second argument
sub Trigger_Action
{
	my @Commands = ();
	my ($Process, $Action) = @_;

	# On va récupérer la commande liée au processus et à l'action courants 
	# depuis le panneau de commandes de la table
	if(!open(INPUT, "Get_PCI -e -t For $TableName | Select -s Arguments ".
		"from - where \"Group='$Process' and Label='$Action' and ".
		"Processor='ExecuteProcedure'\" 2>nul |"))
	{
		$BV_SEVERITE = 202;
		&Export_Variables("BV_SEVERITE");
		system("Log_Error 0308 \"Select from pci\"");
		&Exit_Procedure;
	}
	@Commands = <INPUT>;
	close(INPUT);
	$RETURN_CODE = $? >> 8;
	&Check_ReturnCode("Select from pci");

	# S'il n'y a aucune ligne, on sort
	if(@Commands == 0)
	{
		return;
	}
	# On va vérifier qu'il n'y a qu'une seule ligne
	if(@Commands != 1)
	{
		$BV_SEVERITE = 202;
		&Export_Variables("BV_SEVERITE");
		system("Log_Error 0100 \"Il ne doit y avoir qu'une seule méthode liée ".
			"au processus $Process pour l'action $Action\"");
		&Exit_Procedure;
	}
	# Il ne reste plus qu'à exécuter la commande
	system("$Commands[0]");
	$RETURN_CODE = $? >> 8;
	&Check_ReturnCode("$Command[0]");
}

##########################
# Variables Standard BV  #
##########################

$ENV{"BV_DOMAIN"} ne "" ? $BV_DOMAIN = $ENV{"BV_DOMAIN"} : $BV_DOMAIN = "I-TOOLS";
&Export_Variables("BV_DOMAIN");

#Syntaxe du script
$USAGE="USAGE: $FONCTION [-h] INTO <Table> VALUES <-|Values> [WHERE <Condition>]";

#BV_DEBUG=TRUE => Log_Start/Log_End pour toute exécution
if($BV_DEBUG eq "TRUE")
{
	system("Log_Start");
}

########################################
# Initialisation des Variables Locales #
########################################

$TableName = "";
$Values = "";
$Condition = "";

@Commands = ();

##############################
# Decodification des Options #
##############################

while(&Get_Options("h", opt))
{
	if($opt eq "h")
	{
		$BV_SEVERITE = 0;
		&Export_Variables("BV_SEVERITE");
		system("Log_Info -s \"$USAGE\"");
		&Exit_Procedure;
	}
	elsif($opt eq "*")
	{
		$BV_SEVERITE = 202;
		&Export_Variables("BV_SEVERITE");
		system("Log_Error 0020 $BADOPT");
		system("Log_Info -s \"$USAGE\"");
		&Exit_Procedure;
	}
}

#############################
# Traitement des Arguments  #
#############################

if(@ARGV < 4)
{
	$BV_SEVERITE = 202;
	&Export_Variables("BV_SEVERITE");
	system("Log_Error 0021");
	system("Log_Info -s \"$USAGE\"");
	&Exit_Procedure;
}
# On vérifie que le 1er argument vaut INTO
if(uc($ARGV[0]) ne "INTO")
{
	$BV_SEVERITE = 202;
	&Export_Variables("BV_SEVERITE");
	system("Log_Error 0024");
	system("Log_Info -s \"$USAGE\"");
	&Exit_Procedure;
}
# On vérifie que le 3eme argument vaut VALUES
if(uc($ARGV[2]) ne "VALUES")
{
	$BV_SEVERITE = 202;
	&Export_Variables("BV_SEVERITE");
	system("Log_Error 0024");
	system("Log_Info -s \"$USAGE\"");
	&Exit_Procedure;
}
# S'il y a plus de quatre arguments, le 5eme doit valoir WHERE
if(@ARGV > 4 && uc($ARGV[4]) ne "WHERE")
{
	$BV_SEVERITE = 202;
	&Export_Variables("BV_SEVERITE");
	system("Log_Error 0024");
	system("Log_Info -s \"$USAGE\"");
	&Exit_Procedure;
}
# Le nom de la table est en 2nd argument
$TableName = $ARGV[1];
&Export_Variables("TableName");
# Les valeurs sont passées en 4eme argument
$Values = $ARGV[3];
# Si la chaine de valeurs vaut -, on prend depuis l'entrée standard
if($Values eq "-")
{
	$Values = <STDIN>;
	chomp($Values);
	$Values =~ s/ $//g;
}
# S'il y a plus de 4 arguments, la condition est en 6eme argument
if(@ARGV > 4)
{
	$Condition = "WHERE \"$ARGV[5]\"";
}

##############################
# Interpretation des Options #
##############################


##############################################
# Verification des Variables d'environnement #
##############################################

# Liste de variables d'environnement devant etre positionnees
@XVARS = ("TOOLS_HOME", "BV_DEFPATH", "BV_TABPATH");

# Verification des variables (Repertoire, Donnee numerique, ...)
foreach my $XVAR (@XVARS)
{
	if($ENV{$XVAR} eq "")
	{
		$BV_SEVERITE = 202;
		&Export_Variables("BV_SEVERITE");
		system("Log_Error 0091 \"$XVAR\"");
		&Exit_Procedure;
	}
}

##########################
# MAIN : Corps du Script #
##########################
# On va évaluer le dictionnaire de la table
&Eval_Define_Table($TableName);

# On va évaluer les données
@READ_ROW_ARRAY = ("$Values");
&Read_Row(1);

# On va exécuter la commande de pré-action
&Trigger_Action("Replace", "PreAction");

# On va exécuter la commande de remplacement normale
system("Replace INTO $TableName VALUES \"$Values\" $Condition 2>nul");
$RETURN_CODE = $? >> 8;
&Check_ReturnCode("Replace INTO $TableName");

# On va exécuter la commande de post-action
&Trigger_Action("Replace", "PostAction");

#Fin (sortie sur BV_SEVERITE)
&Exit_Procedure
