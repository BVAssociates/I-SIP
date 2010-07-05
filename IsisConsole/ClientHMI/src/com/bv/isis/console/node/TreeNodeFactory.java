/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/TreeNodeFactory.java,v $
* $Revision: 1.18 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de construction de noeuds graphiques
* DATE:        10/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: TreeNodeFactory.java,v $
* Revision 1.18  2009/01/14 14:22:26  tz
* Prise en compte de la modification des packages.
*
* Revision 1.17  2008/06/27 09:41:02  tz
* Utilisation du contexte fourni en argument.
*
* Revision 1.16  2006/10/13 15:09:49  tz
* Adaptation aux modifications de la classe ServiceSessionProxy.
*
* Revision 1.15  2005/10/07 08:18:39  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.14  2005/07/06 10:04:07  tz
* Ajout d'une deuxième méthode getValueOfParameter().
*
* Revision 1.13  2005/07/01 12:04:24  tz
* Modification du composant pour les traces
*
* Revision 1.12  2004/11/23 15:37:33  tz
* Utilisation de la méthode getWideSelectResult().
*
* Revision 1.11  2004/11/09 15:21:18  tz
* Positionnement du caractère quote pour les paramètres.
*
* Revision 1.10  2004/11/02 08:44:56  tz
* Gestion des leasings sur les définitions,
* Passage de la méthode getValueOfParameter.
*
* Revision 1.9  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.8  2004/10/13 13:54:02  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1.
*
* Revision 1.7  2002/11/19 08:37:52  tz
* Gestion de la progression de la tâche.
*
* Revision 1.6  2002/06/19 12:14:22  tz
* Correction de la fiche Inuit/25
*
* Revision 1.5  2002/04/05 15:49:49  tz
* Cloture itération IT1.2
*
* Revision 1.4  2002/03/27 09:42:20  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.3  2002/02/04 10:54:25  tz
* Cloture itération IT1.0.1
*
* Revision 1.2  2001/12/19 09:58:13  tz
* Cloture itération IT1.0.0
*
* Revision 1.1  2001/12/12 09:58:23  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.node;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.config.ConfigurationAPI;
import java.util.Vector;

//
// Imports du projet
//
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.corbacom.IsisForeignKey;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisTableColumn;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: TreeNodeFactory
*
* Description:
* Cette classe abstraite a la responsabilité de la construction des noeuds
* graphiques qui doivent être affichés dans l'arbre d'exploration. Elle permet
* de charger ou construire les informations nécessaires au fonctionnement du
* noeud (label via la classe LabelFactory, condition de requête via la classe
* RequestFactory ou chargement de dictionnaire via la classe
* TableDefinitionManager).
* Suivant le type d'objet à construire, l'une des méthode makeTreeXXXXNode()
* doit être appelée (voir le détail des différentes méthodes).
* ----------------------------------------------------------*/
public abstract class TreeNodeFactory
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: makeTreeObjectNodes
	*
	* Description:
	* Cette méthode statique permet de construire un ensemble de noeuds
	* graphiques correspondant à un ensemble d'objets de production (des
	* instances de GenericTreeObjectNode) à partir, entre autres, du résultat
	* d'une requête Select.
	* Les libellés des noeuds sont construits par la classe LabelFactory.
	* Les noeuds eux-mêmes sont créés à partir du résultat d'un Select, défini
	* par le nom de la table et la condition, et du chemin du dictionnaire de
	* la table.
	*
	* Si un problème survient lors de l'exécution de la requête, l'exception
	* InnerException est levée.
	*
	* Arguments:
	*  - serviceSession: Une référence sur la session de service à utiliser
	*    pour exécuter la requête,
	*  - agentName: Nom de l'agent sur lequel la session a été ouverte,
	*  - iClesName: Nom du I-CLES auquel sont apparentés les noeuds,
	*  - serviceType: Type du service auquel sont apparentés les noeuds,
	*  - tableName: Nom de la table sur laquelle la requête va être exécutée,
	*  - condition: La condition de la requête permettant de construire les
	*    objets,
	*  - parentNode: Une référence sur le noeud graphique père,
	*  - context: Le contexte d'exécution de la requête,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface,
	*  - label: Le libellé du noeud parent.
	*
	* Retourne: Un tableau d'instances de GenericTreeObjectNode correspondants
	* à l'ensemble des objets de production.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static GenericTreeObjectNode[] makeTreeObjectNodes(
		ServiceSessionInterface serviceSession,
		String agentName,
		String iClesName,
		String serviceType,
		String tableName,
		String condition,
		GenericTreeObjectNode parentNode,
		IndexedList context,
		MainWindowInterface windowInterface,
		String label
		)
		throws
			InnerException
	{
		Vector nodes = null;
		String[] extra_information = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TreeNodeFactory", "makeTreeObjectNodes");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("condition=" + condition);
		trace_arguments.writeTrace("parentNode=" + parentNode);
		trace_arguments.writeTrace("context=" + context);
		// On affiche le message dans la barre d'état
		if(windowInterface != null)
		{
			extra_information = new String[1];
		    extra_information[0] = label;
		    windowInterface.setStatus("&Status_BuildingObjectNodes",
			    extra_information, 0);
		}
		// Si le contexte est null, il faut fournir une table vide
		if(context == null)
		{
		    context = new IndexedList();
		}
		// Il faut exécuter le Select sur la session
		ServiceSessionProxy session_proxy =
			new ServiceSessionProxy(serviceSession);
		String[] selected_columns = { "" };
		String[] select_result = session_proxy.getWideSelectResult(
			agentName, tableName, selected_columns, condition, "", context);
		// Y a-t-il un résultat ?
		if(select_result == null || select_result.length <= 1)
		{
			trace_debug.writeTrace("Aucun résultat au Select");
			trace_methods.endOfMethod();
			return null;
		}
		// On fixe le maximum
		if(windowInterface != null)
		{
			windowInterface.setProgressMaximum(select_result.length + 1);
			windowInterface.setStatus("&Status_BuildingObjectNodes",
				extra_information, 1);
		}
		// On va récupérer le dictionnaire correspondant à la table
		TableDefinitionManager definition_manager =
			TableDefinitionManager.getInstance();
		IsisTableDefinition definition =
			definition_manager.getTableDefinition(agentName, iClesName, 
			serviceType, tableName, context, serviceSession);
		nodes = new Vector();
		// On va créer un objet graphique par ligne de données
		for(int index = 1 ; index < select_result.length ; index ++)
		{
			// On extrait le tableau d'IsisParameter
			IsisParameter[] object_parameters =
				buildParametersFromSelectResult(select_result, index,
				definition);
			// On récupère la clé de l'objet
			String key = buildKeyFromSelectResult(object_parameters, definition);
			// On construit le GenericTreeObjectNode
			GenericTreeObjectNode node =
				new GenericTreeObjectNode(object_parameters, key, agentName, 
				iClesName, serviceType, definition.definitionFilePath, 
				tableName);
			if(windowInterface != null)
			{
				windowInterface.setStatus("&Status_BuildingObjectNodes",
					extra_information, index + 1);
			}
			// On l'ajoute à la table parent, si elle existe
			if(parentNode != null)
			{
			    parentNode.add(node);
			}
			// On génère le label
			LabelFactory.createLabel(agentName, iClesName, serviceType, node, 
				context);
			nodes.add(node);
		}
		// On libère l'utilisation de la définition
		definition_manager.releaseTableDefinitionLeasing(definition);
		trace_methods.endOfMethod();
		return (GenericTreeObjectNode[])nodes.toArray(
			new GenericTreeObjectNode[0]);
	}

	/*----------------------------------------------------------
	* Nom: makeTreeClassNode
	*
	* Description:
	* Cette méthode statique permet de construire un noeud graphique
	* correspondant à une table de production (une instance de
	* GenericTreeClassNode) à partir, entre autres, d'un nom de table et d'une
	* condition de requête construite à partir de l'argument link.
	* Le libélé du noeud est construit par la classe LabelFactory.
	* La condition de requête est construite par la classe RequestFactory.
	* Le noeud lui-même est créé à partir du chemin du dictionnaire de la table
	* sur l'hôte et de la condition de requête.
	* La définition de la table est chargée depuis l'hôte, via la classe
	* TableDefinitionManager.
	*
	* Si un problème survient lors de la récupération de la définition de la
	* table, l'exception InnerException est levée.
	*
	* Arguments:
	*  - serviceSession: Une référence sur la session de service à travers
	*    laquelle les requêtes sont exécutées,
	*  - agentName: Le nom de l'Agent sur lequel la session a été ouverte,
	*  - iClesName: Nom du I-CLES auquel est apparenté le noeud,
	*  - serviceType: Type du service auquel est apparenté le noeud,
 	*  - tableName: Le nom de la table correspondante,
	*  - link: Un lien sur une autre table à convertir en condition de requête,
	*  - parentNode: Une référence sur le noeud graphique père,
	*  - context: Le contexte permettant de récupérer la définition de la table.
	*
	* Retourne: Une instance de GenericTreeClassNode correspondant à la table
	* de production.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static GenericTreeClassNode makeTreeClassNode(
		ServiceSessionInterface serviceSession,
		String agentName,
		String iClesName,
		String serviceType,
		String tableName,
		IsisForeignKey link,
		GenericTreeObjectNode parentNode,
		IndexedList context
		)
		throws
			InnerException
	{
		IsisTableDefinition table_definition = null;
		GenericTreeClassNode class_node = null;
		String condition = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TreeNodeFactory", "makeTreeClassNode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("link=" + link);
		trace_arguments.writeTrace("parentNode=" + parentNode);
		trace_arguments.writeTrace("context=" + context);
		// Si le contexte est null, il faut fournir une table vide
		if(context == null)
		{
		    context = new IndexedList();
		}
		// Maintenant, on récupère la définition de la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		table_definition =
			manager.getTableDefinition(agentName, iClesName, serviceType, 
			tableName, context, serviceSession);
		// Création de la requête correspondant au lien
		condition = ConditionFactory.getConditionFromForeignKey(
			agentName, link, table_definition);
		// Création de l'objet graphique
		class_node =
			new GenericTreeClassNode(agentName, iClesName, serviceType, 
			table_definition.definitionFilePath, tableName, condition);
		// On libère l'utilisation de la définition
		manager.releaseTableDefinitionLeasing(table_definition);
		// On ajoute le noeud au noeud parent, s'il existe
		if(parentNode != null)
		{
		    parentNode.add(class_node);
		}
		// On génère le label du noeud
		LabelFactory.createLabel(agentName, iClesName, serviceType, class_node,
			class_node.getContext(true));
		trace_methods.endOfMethod();
		return class_node;
	}

	/*----------------------------------------------------------
	* Nom: buildDefinitionFromSelectResult
	*
	* Description:
	* Cette méthode statique permet de construire une instance de
	* IsisTableDefinition (définition de table) à partir du résultat brut
	* d'exécution d'un Select (voir la méthode getSelectResult() de la classe
	* ServiceSessionProxy).
	* Il s'agit de décoder la première ligne du résultat du Select, qui
	* contient l'entête de définition du format des données. Chaque champ de
	* cette définition est séparé par la chaîne '@@'. Elle contient les champs
	* SEP (séparateur), FORMAT (intitulé des champs), SIZE (dimensions des
	* champs), ROW (non utilisé) et KEY (liste des champs constituant la clé).
	*
	* Si un problème survient lors du décodage de l'entête de définition,
	* l'exception InnerException est levée.
	*
	* Arguments:
	*  - selectResult: Un tableau de chaînes correspondant au résultat brut
	*    d'exécution du Select,
	*  - tableName: Le nom de la table sur laquelle a été exécutée la requête.
	*
	* Retourne: Une instance de IsisTableDefinition.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static IsisTableDefinition buildDefinitionFromSelectResult(
		String[] selectResult,
		String tableName
		)
		throws
			InnerException
	{
		IsisTableDefinition definition = null;
		String header_separator = "@@";
		String separator_field = "SEP";
		String format_field = "FORMAT";
		String size_field = "SIZE";
		String key_field = "KEY";
		int number_of_fields = 6;
		String field_value_separator = "=";
		String format = null;
		String size = null;
		String key = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TreeNodeFactory", "buildDefinitionFromSelectResult");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectResult=" + selectResult);
		trace_arguments.writeTrace("tableName=" + tableName);
		// Tout d'abord, il faut vérifier qu'il y a au moins la ligne d'entête
		// dans le résultat du Select
		if(selectResult == null || selectResult.length < 1)
		{
			trace_errors.writeTrace(
				"Il n'y a pas de ligne d'entête dans le résultat");
			// Il n'y a pas de ligne d'entête, c'est une erreur
			throw new InnerException("&ERR_CannotExtractDefinitionFromSelect",
				"null", null);
		}
		// On va récupérer les noms des champs et le séparateur d'entête
		try
		{
			ConfigurationAPI configuration_api = new ConfigurationAPI();
		    configuration_api.useSection("I-TOOLS");
		    header_separator =
				configuration_api.getString("SelectHeader.Separator");
		    separator_field =
				configuration_api.getString("SelectHeader.SeparatorField");
		    format_field =
				configuration_api.getString("SelectHeader.FormatField");
		    size_field =
				configuration_api.getString("SelectHeader.SizeField");;
		    key_field =
				configuration_api.getString("SelectHeader.KeyField");
		    number_of_fields =
				configuration_api.getInt("SelectHeader.NumberOfFields");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception.getMessage());
			// On va lever une nouvelle exception
			throw new InnerException("&ERR_CannotExtractDefinitionFromSelect",
				exception.getMessage(), exception);
		}
		// On va récupérer la première ligne et regarder si elle peut être
		// découpée avec le séparateur d'entête
		UtilStringTokenizer header_tokenizer =
			new UtilStringTokenizer(selectResult[0], header_separator);
		// On vérifie que l'entête contient le bon nombre de champs
		if(header_tokenizer.getTokensCount() != number_of_fields)
		{
			trace_errors.writeTrace(
				"L'entête n'a pas le format correct: " +
				selectResult[0]);
			// L'entête n'a pas le format correct
			throw new InnerException("&ERR_CannotExtractDefinitionFromSelect",
				selectResult[0], null);
		}
		// On crée une instance de IsisTableDefinition que l'on va remplir
		definition = new IsisTableDefinition();
		// On positionne le nom de la table
		definition.tableName = tableName;
		// On va récupérer les champs de l'entête un par un
		while(header_tokenizer.hasMoreTokens() == true)
		{
			UtilStringTokenizer field_tokenizer =
				new UtilStringTokenizer(header_tokenizer.nextToken(),
				field_value_separator);
			if(field_tokenizer.getTokensCount() != 2)
			{
				trace_errors.writeTrace(
					"L'entête n'a pas le format correct: " +
					selectResult[0]);
				// L'entête n'a pas le format correct
				throw new InnerException("&ERR_CannotExtractDefinitionFromSelect",
					selectResult[0], null);
			}
			String field_name = field_tokenizer.nextToken();
			trace_debug.writeTrace("field_name=" + field_name);
			// On regarde si le nom du champ correspond à un nom connu
			if(field_name.equals(separator_field) == true)
			{
				definition.separator = getValue(field_tokenizer.nextToken());
				continue;
			}
			if(field_name.equals(format_field) == true)
			{
				format = getValue(field_tokenizer.nextToken());
				continue;
			}
			if(field_name.equals(size_field) == true)
			{
				size = getValue(field_tokenizer.nextToken());
				continue;
			}
			if(field_name.equals(key_field) == true)
			{
				key = getValue(field_tokenizer.nextToken());
				continue;
			}
		}
		UtilStringTokenizer format_tokenizer =
			new UtilStringTokenizer(format, definition.separator);
		UtilStringTokenizer size_tokenizer =
			new UtilStringTokenizer(size, definition.separator);
		if(format_tokenizer.getTokensCount() != size_tokenizer.getTokensCount())
		{
			trace_errors.writeTrace(
				"L'entête n'a pas le format correct: " +
				selectResult[0]);
			// L'entête n'a pas le format correct
			throw new InnerException("&ERR_CannotExtractDefinitionFromSelect",
				selectResult[0], null);
		}
		// On instancie le tableau de colonnes
		definition.columns =
			new IsisTableColumn[format_tokenizer.getTokensCount()];
		// On traite chaque colonne
		for(int index = 0 ; index < format_tokenizer.getTokensCount() ;
			index ++)
		{
			definition.columns[index] = new IsisTableColumn();
			definition.columns[index].name = format_tokenizer.nextToken();
			String size_string = size_tokenizer.nextToken();
			trace_debug.writeTrace("name=" + definition.columns[index].name);
			trace_debug.writeTrace("size_string=" + size_string);
			// On récupère le type et la taille
			definition.columns[index].type =
				size_string.charAt(size_string.length() - 1);
			try
			{
				definition.columns[index].size =
					Integer.parseInt(size_string.substring(0,
					size_string.length() - 1));
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace(
					"Erreur lors de la conversion de la taille: " +
					exception.getMessage());
				// Il y a une erreur de conversion
				throw new InnerException(
					"&ERR_CannotExtractDefinitionFromSelect", selectResult[0],
					exception);
			}
		}
		// Il ne reste plus qu'à décomposer la clé
		if(key != null && key.equals("") == false)
		{
			UtilStringTokenizer key_tokenizer =
				new UtilStringTokenizer(key, definition.separator);
			definition.key = new String[key_tokenizer.getTokensCount()];
			for(int index = 0 ; index < key_tokenizer.getTokensCount() ; index ++)
			{
				definition.key[index] = key_tokenizer.nextToken();
			}
		}
		else
		{
			definition.key = new String[0];
		}
		trace_methods.endOfMethod();
		return definition;
	}

	/*----------------------------------------------------------
	* Nom: buildParametersFromSelectResult
	*
	* Description:
	* Cette méthode statique permet de construire un tableau
	* d'IsisParameter (nécessaire à la construction d'objets
	* GenericTreeObjectNode, par exemple) à partir du résultat brut d'exécution
	* d'un Select, d'un numéro de ligne et de la définition des champs.
	* Mis à part la première ligne de résultat d'exécution du Select (voir la
	* méthode buildDefinitionFromSelectResult()), chaque ligne de résultat
	* correspond aux valeurs des différents champs pour un objet. C'est une de
	* ces lignes qui va être transformée en tableau d'IsisParameter.
	* Le découpage se fait suivant le séparateur des champs, les champs étant
	* extraits dans l'ordre pour être associés au nom du champ (récupéré depuis
	* la définition de la table).
	*
	* Si un problème survient lors de la construction du tableau, l'exception
	* InnerException est levée.
	*
	* Arguments:
	*  - selectResult: Un tableau de chaînes correspondant au résultat brut
	*    d'exécution d'un Select,
	*  - rowIndex: Numéro de la ligne de résultat à extraire (valeur comprise
	*    entre 1 et selectResult.length() - 1),
	*  - definition: Le dictionnaire de la table, permettant de récupérer les
	*    noms des champs.
	*
	* Retourne: Un tableau d'IsisParameter correspondant à la
	* décomposition d'une ligne de données.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static IsisParameter[] buildParametersFromSelectResult(
		String[] selectResult,
		int rowIndex,
		IsisTableDefinition definition
		)
		throws
			InnerException
	{
		IsisParameter[] parameters = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TreeNodeFactory", "buildParametersFromSelectResult");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectResult=" + selectResult);
		trace_arguments.writeTrace("rowIndex=" + rowIndex);
		trace_arguments.writeTrace("definition=" + definition);
		// On vérifie la validité de l'argument
		if(selectResult == null || rowIndex < 1 ||
			rowIndex >= selectResult.length)
		{
			trace_errors.writeTrace("Index hors limites");
			// L'index est hors limites, c'est une erreur
			throw new InnerException("&ERR_CannotExtractObjectFromSelect",
				"&ERR_IndexOutOfBounds", null);
		}
		// On décompose la ligne suivant le séparateur
		UtilStringTokenizer line_tokenizer =
			new UtilStringTokenizer(selectResult[rowIndex],
			definition.separator);
		// On vérifie que le nombre de champs est identique
		if(line_tokenizer.getTokensCount() != definition.columns.length)
		{
			trace_errors.writeTrace(
				"Nombre de champs différents entre les valeurs et la définition");
			// Le nombre de champs est différent
			throw new InnerException("&ERR_CannotExtractObjectFromSelect",
				"&ERR_NoSameNumberOfFields", null);
		}
		// On instancie le tableau de paramètres
		parameters = new IsisParameter[line_tokenizer.getTokensCount()];
		// On va créer les paramètres un par un
		for(int index = 0 ; index < line_tokenizer.getTokensCount() ; index ++)
		{
			parameters[index] = new IsisParameter();
			parameters[index].name = definition.columns[index].name;
			parameters[index].value = line_tokenizer.nextToken();
			parameters[index].quoteCharacter = '"';
		}
		trace_methods.endOfMethod();
		return parameters;
	}

	/*----------------------------------------------------------
	* Nom: buildKeyFromSelectResult
	*
	* Description:
	* Cette méthode statique permet de construire la clé d'une ligne de donnée
	* à partir d'un tableau de IsisParameter et de la définition des
	* champs.
	* Cette clé permet d'identifier de manière unique une ligne de donnée dans
	* une table.
	*
	* Si un problème survient lors de la construction de la clé, l'exception
	* InnerException est levée.
	*
	* Arguments:
	*  - objectParameters: Un tableau de IsisParameter contenant les
	*    valeurs des champs,
	*  - definition: Le dictionnaire de la table, permettant de récupérer les
	*    noms des champs.
	*
	* Retourne: Une chaîne contenant la valeur de la clé.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static String buildKeyFromSelectResult(
		IsisParameter[] objectParameters,
		IsisTableDefinition definition
		)
		throws
			InnerException
	{
		StringBuffer key = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TreeNodeFactory", "buildKeyFromSelectResult");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("objectParameters=" + objectParameters);
		trace_arguments.writeTrace("definition=" + definition);
		// On vérifie la validité des arguments
		if(objectParameters == null || objectParameters.length == 0 ||
			definition == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// Au moins un des arguments n'est pas valide, on lève une exception
			throw new InnerException("&ERR_CannotExtractKeyFromSelect",
				"&ERR_InvalidArgument", null);
		}
		key = new StringBuffer();
		// On va récupérer chaque nom de champ constituant la clé
		for(int index = 0 ; index < definition.key.length ; index ++)
		{
			boolean found = false;

			trace_debug.writeTrace("Champ de clé: " + definition.key[index]);
			// On va rechercher la valeur du champ de même nom
			for(int count = 0 ; count < objectParameters.length ; count ++)
			{
				if(objectParameters[count].name.equals(
					definition.key[index]) == true)
				{
					if(index != 0)
					{
						key.append(definition.separator);
					}
					key.append(objectParameters[count].value);
					found = true;
					break;
				}
			}
			if(found == false)
			{
				// Si on arrive ici, c'est que l'on n'a pas trouvé le champ
				// C'est une erreur
				trace_errors.writeTrace("Champ non trouvable: " +
					definition.key[index]);
				throw new InnerException("&ERR_CannotExtractKeyFromSelect",
					definition.key[index], null);
			}
		}
		trace_methods.endOfMethod();
		return key.toString();
	}

	/*----------------------------------------------------------
	* Nom: getValueOfParameter
	*
	* Description:
	* Cette méthode statique permet de récupérer la valeur contenue dans 
	* l'objet passé en paramètre, si celui n'est pas nul, qui doit être de 
	* type IsisParameter.
	*
	* Arguments:
	*  - parameters: Un tableau de IsisParameter dans lequel la valeur du
	*    paramètre est recherchée,
	*  - parameterName: Le paramètre dont on veut récupérer la valeur.
	*
	* Retourne: La valeur contenue dans le paramètre, ou null.
	* ----------------------------------------------------------*/
	public static String getValueOfParameter(
		IsisParameter[] parameters,
		String parameterName
		)
	{
		String value = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"TreeNodeFactory", "getValueOfParameter");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("parameterName=" + parameterName);
		if(parameters == null)
		{
			// Il n'y a pas de paramètre, on sort
			trace_methods.endOfMethod();
			return value;
		}
		for(int index = 0 ; index < parameters.length ; index ++)
		{
			if(parameters[index].name.equals(parameterName) == true)
			{
				value = parameters[index].value;
			}
		}
		trace_methods.endOfMethod();
		return value;
	}


	/*----------------------------------------------------------
	* Nom: getValueOfParameter
	* 
	* Description:
	* Cette méthode statique permet de récupérer la valeur d'un paramètre, 
	* dont le nom est passé en argument, s'il existe dans la liste indexée 
	* passée en argument.
	* 
	* Arguments:
	*  - context: Une référence sur un objet IndexedList dans laquelle 
	*    chercher le paramètre,
	*  - parameterName: Le paramètre dont on veut récupérer la valeur.
	* 
	* Retourne: La valeur contenue dans le paramètre, ou null.
	* ----------------------------------------------------------*/
	public static String getValueOfParameter(
		IndexedList context,
		String parameterName
		)
	{
		String value = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"TreeNodeFactory", "getValueOfParameter");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("parameterName=" + parameterName);
		if(context == null)
		{
			// Il n'y a pas de paramètre, on sort
			trace_methods.endOfMethod();
			return value;
		}
		// On récupère le paramètre associé au nom
		IsisParameter parameter = (IsisParameter)context.get(parameterName);
		// Si le paramètre est non nul, on récupère sa valeur
		if(parameter != null)
		{
			value = parameter.value;
		}
		trace_methods.endOfMethod();
		return value;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: getValue
	*
	* Description:
	* Cette méthode permet de récupérer la valeur contenue dans la chaîne
	* "valueChain". Elle gère les cas où la valeur est placée entre simples-
	* quotes ou entre doubles-quotes.
	*
	* Arguments:
	*  - valueChain: La valeur dont il faut éventuellement retirer les quotes.
	*
	* Retourne: La valeur contenue dans la chaîne valueChain.
	* ----------------------------------------------------------*/
	private static String getValue(
		String valueChain
		)
	{
		String value;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TreeNodeFactory", "getValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("valueChain=" + valueChain);
		if(valueChain.startsWith("'") == true ||
			valueChain.startsWith("\"") == true)
		{
			value = valueChain.substring(1, valueChain.length() - 1);
		}
		else
		{
			value = valueChain;
		}
		trace_methods.endOfMethod();
		return value;
	}
}