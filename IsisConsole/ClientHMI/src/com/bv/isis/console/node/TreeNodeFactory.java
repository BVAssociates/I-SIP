/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
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
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.14  2005/07/06 10:04:07  tz
* Ajout d'une deuxi�me m�thode getValueOfParameter().
*
* Revision 1.13  2005/07/01 12:04:24  tz
* Modification du composant pour les traces
*
* Revision 1.12  2004/11/23 15:37:33  tz
* Utilisation de la m�thode getWideSelectResult().
*
* Revision 1.11  2004/11/09 15:21:18  tz
* Positionnement du caract�re quote pour les param�tres.
*
* Revision 1.10  2004/11/02 08:44:56  tz
* Gestion des leasings sur les d�finitions,
* Passage de la m�thode getValueOfParameter.
*
* Revision 1.9  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.8  2004/10/13 13:54:02  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1.
*
* Revision 1.7  2002/11/19 08:37:52  tz
* Gestion de la progression de la t�che.
*
* Revision 1.6  2002/06/19 12:14:22  tz
* Correction de la fiche Inuit/25
*
* Revision 1.5  2002/04/05 15:49:49  tz
* Cloture it�ration IT1.2
*
* Revision 1.4  2002/03/27 09:42:20  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.3  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.2  2001/12/19 09:58:13  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1  2001/12/12 09:58:23  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.node;

//
// Imports syst�me
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
* Cette classe abstraite a la responsabilit� de la construction des noeuds
* graphiques qui doivent �tre affich�s dans l'arbre d'exploration. Elle permet
* de charger ou construire les informations n�cessaires au fonctionnement du
* noeud (label via la classe LabelFactory, condition de requ�te via la classe
* RequestFactory ou chargement de dictionnaire via la classe
* TableDefinitionManager).
* Suivant le type d'objet � construire, l'une des m�thode makeTreeXXXXNode()
* doit �tre appel�e (voir le d�tail des diff�rentes m�thodes).
* ----------------------------------------------------------*/
public abstract class TreeNodeFactory
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: makeTreeObjectNodes
	*
	* Description:
	* Cette m�thode statique permet de construire un ensemble de noeuds
	* graphiques correspondant � un ensemble d'objets de production (des
	* instances de GenericTreeObjectNode) � partir, entre autres, du r�sultat
	* d'une requ�te Select.
	* Les libell�s des noeuds sont construits par la classe LabelFactory.
	* Les noeuds eux-m�mes sont cr��s � partir du r�sultat d'un Select, d�fini
	* par le nom de la table et la condition, et du chemin du dictionnaire de
	* la table.
	*
	* Si un probl�me survient lors de l'ex�cution de la requ�te, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - serviceSession: Une r�f�rence sur la session de service � utiliser
	*    pour ex�cuter la requ�te,
	*  - agentName: Nom de l'agent sur lequel la session a �t� ouverte,
	*  - iClesName: Nom du I-CLES auquel sont apparent�s les noeuds,
	*  - serviceType: Type du service auquel sont apparent�s les noeuds,
	*  - tableName: Nom de la table sur laquelle la requ�te va �tre ex�cut�e,
	*  - condition: La condition de la requ�te permettant de construire les
	*    objets,
	*  - parentNode: Une r�f�rence sur le noeud graphique p�re,
	*  - context: Le contexte d'ex�cution de la requ�te,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - label: Le libell� du noeud parent.
	*
	* Retourne: Un tableau d'instances de GenericTreeObjectNode correspondants
	* � l'ensemble des objets de production.
	*
	* L�ve: InnerException.
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
		// On affiche le message dans la barre d'�tat
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
		// Il faut ex�cuter le Select sur la session
		ServiceSessionProxy session_proxy =
			new ServiceSessionProxy(serviceSession);
		String[] selected_columns = { "" };
		String[] select_result = session_proxy.getWideSelectResult(
			agentName, tableName, selected_columns, condition, "", context);
		// Y a-t-il un r�sultat ?
		if(select_result == null || select_result.length <= 1)
		{
			trace_debug.writeTrace("Aucun r�sultat au Select");
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
		// On va r�cup�rer le dictionnaire correspondant � la table
		TableDefinitionManager definition_manager =
			TableDefinitionManager.getInstance();
		IsisTableDefinition definition =
			definition_manager.getTableDefinition(agentName, iClesName, 
			serviceType, tableName, context, serviceSession);
		nodes = new Vector();
		// On va cr�er un objet graphique par ligne de donn�es
		for(int index = 1 ; index < select_result.length ; index ++)
		{
			// On extrait le tableau d'IsisParameter
			IsisParameter[] object_parameters =
				buildParametersFromSelectResult(select_result, index,
				definition);
			// On r�cup�re la cl� de l'objet
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
			// On l'ajoute � la table parent, si elle existe
			if(parentNode != null)
			{
			    parentNode.add(node);
			}
			// On g�n�re le label
			LabelFactory.createLabel(agentName, iClesName, serviceType, node, 
				context);
			nodes.add(node);
		}
		// On lib�re l'utilisation de la d�finition
		definition_manager.releaseTableDefinitionLeasing(definition);
		trace_methods.endOfMethod();
		return (GenericTreeObjectNode[])nodes.toArray(
			new GenericTreeObjectNode[0]);
	}

	/*----------------------------------------------------------
	* Nom: makeTreeClassNode
	*
	* Description:
	* Cette m�thode statique permet de construire un noeud graphique
	* correspondant � une table de production (une instance de
	* GenericTreeClassNode) � partir, entre autres, d'un nom de table et d'une
	* condition de requ�te construite � partir de l'argument link.
	* Le lib�l� du noeud est construit par la classe LabelFactory.
	* La condition de requ�te est construite par la classe RequestFactory.
	* Le noeud lui-m�me est cr�� � partir du chemin du dictionnaire de la table
	* sur l'h�te et de la condition de requ�te.
	* La d�finition de la table est charg�e depuis l'h�te, via la classe
	* TableDefinitionManager.
	*
	* Si un probl�me survient lors de la r�cup�ration de la d�finition de la
	* table, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - serviceSession: Une r�f�rence sur la session de service � travers
	*    laquelle les requ�tes sont ex�cut�es,
	*  - agentName: Le nom de l'Agent sur lequel la session a �t� ouverte,
	*  - iClesName: Nom du I-CLES auquel est apparent� le noeud,
	*  - serviceType: Type du service auquel est apparent� le noeud,
 	*  - tableName: Le nom de la table correspondante,
	*  - link: Un lien sur une autre table � convertir en condition de requ�te,
	*  - parentNode: Une r�f�rence sur le noeud graphique p�re,
	*  - context: Le contexte permettant de r�cup�rer la d�finition de la table.
	*
	* Retourne: Une instance de GenericTreeClassNode correspondant � la table
	* de production.
	*
	* L�ve: InnerException.
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
		// Maintenant, on r�cup�re la d�finition de la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		table_definition =
			manager.getTableDefinition(agentName, iClesName, serviceType, 
			tableName, context, serviceSession);
		// Cr�ation de la requ�te correspondant au lien
		condition = ConditionFactory.getConditionFromForeignKey(
			agentName, link, table_definition);
		// Cr�ation de l'objet graphique
		class_node =
			new GenericTreeClassNode(agentName, iClesName, serviceType, 
			table_definition.definitionFilePath, tableName, condition);
		// On lib�re l'utilisation de la d�finition
		manager.releaseTableDefinitionLeasing(table_definition);
		// On ajoute le noeud au noeud parent, s'il existe
		if(parentNode != null)
		{
		    parentNode.add(class_node);
		}
		// On g�n�re le label du noeud
		LabelFactory.createLabel(agentName, iClesName, serviceType, class_node,
			class_node.getContext(true));
		trace_methods.endOfMethod();
		return class_node;
	}

	/*----------------------------------------------------------
	* Nom: buildDefinitionFromSelectResult
	*
	* Description:
	* Cette m�thode statique permet de construire une instance de
	* IsisTableDefinition (d�finition de table) � partir du r�sultat brut
	* d'ex�cution d'un Select (voir la m�thode getSelectResult() de la classe
	* ServiceSessionProxy).
	* Il s'agit de d�coder la premi�re ligne du r�sultat du Select, qui
	* contient l'ent�te de d�finition du format des donn�es. Chaque champ de
	* cette d�finition est s�par� par la cha�ne '@@'. Elle contient les champs
	* SEP (s�parateur), FORMAT (intitul� des champs), SIZE (dimensions des
	* champs), ROW (non utilis�) et KEY (liste des champs constituant la cl�).
	*
	* Si un probl�me survient lors du d�codage de l'ent�te de d�finition,
	* l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - selectResult: Un tableau de cha�nes correspondant au r�sultat brut
	*    d'ex�cution du Select,
	*  - tableName: Le nom de la table sur laquelle a �t� ex�cut�e la requ�te.
	*
	* Retourne: Une instance de IsisTableDefinition.
	*
	* L�ve: InnerException.
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
		// Tout d'abord, il faut v�rifier qu'il y a au moins la ligne d'ent�te
		// dans le r�sultat du Select
		if(selectResult == null || selectResult.length < 1)
		{
			trace_errors.writeTrace(
				"Il n'y a pas de ligne d'ent�te dans le r�sultat");
			// Il n'y a pas de ligne d'ent�te, c'est une erreur
			throw new InnerException("&ERR_CannotExtractDefinitionFromSelect",
				"null", null);
		}
		// On va r�cup�rer les noms des champs et le s�parateur d'ent�te
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
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception.getMessage());
			// On va lever une nouvelle exception
			throw new InnerException("&ERR_CannotExtractDefinitionFromSelect",
				exception.getMessage(), exception);
		}
		// On va r�cup�rer la premi�re ligne et regarder si elle peut �tre
		// d�coup�e avec le s�parateur d'ent�te
		UtilStringTokenizer header_tokenizer =
			new UtilStringTokenizer(selectResult[0], header_separator);
		// On v�rifie que l'ent�te contient le bon nombre de champs
		if(header_tokenizer.getTokensCount() != number_of_fields)
		{
			trace_errors.writeTrace(
				"L'ent�te n'a pas le format correct: " +
				selectResult[0]);
			// L'ent�te n'a pas le format correct
			throw new InnerException("&ERR_CannotExtractDefinitionFromSelect",
				selectResult[0], null);
		}
		// On cr�e une instance de IsisTableDefinition que l'on va remplir
		definition = new IsisTableDefinition();
		// On positionne le nom de la table
		definition.tableName = tableName;
		// On va r�cup�rer les champs de l'ent�te un par un
		while(header_tokenizer.hasMoreTokens() == true)
		{
			UtilStringTokenizer field_tokenizer =
				new UtilStringTokenizer(header_tokenizer.nextToken(),
				field_value_separator);
			if(field_tokenizer.getTokensCount() != 2)
			{
				trace_errors.writeTrace(
					"L'ent�te n'a pas le format correct: " +
					selectResult[0]);
				// L'ent�te n'a pas le format correct
				throw new InnerException("&ERR_CannotExtractDefinitionFromSelect",
					selectResult[0], null);
			}
			String field_name = field_tokenizer.nextToken();
			trace_debug.writeTrace("field_name=" + field_name);
			// On regarde si le nom du champ correspond � un nom connu
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
				"L'ent�te n'a pas le format correct: " +
				selectResult[0]);
			// L'ent�te n'a pas le format correct
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
			// On r�cup�re le type et la taille
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
		// Il ne reste plus qu'� d�composer la cl�
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
	* Cette m�thode statique permet de construire un tableau
	* d'IsisParameter (n�cessaire � la construction d'objets
	* GenericTreeObjectNode, par exemple) � partir du r�sultat brut d'ex�cution
	* d'un Select, d'un num�ro de ligne et de la d�finition des champs.
	* Mis � part la premi�re ligne de r�sultat d'ex�cution du Select (voir la
	* m�thode buildDefinitionFromSelectResult()), chaque ligne de r�sultat
	* correspond aux valeurs des diff�rents champs pour un objet. C'est une de
	* ces lignes qui va �tre transform�e en tableau d'IsisParameter.
	* Le d�coupage se fait suivant le s�parateur des champs, les champs �tant
	* extraits dans l'ordre pour �tre associ�s au nom du champ (r�cup�r� depuis
	* la d�finition de la table).
	*
	* Si un probl�me survient lors de la construction du tableau, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - selectResult: Un tableau de cha�nes correspondant au r�sultat brut
	*    d'ex�cution d'un Select,
	*  - rowIndex: Num�ro de la ligne de r�sultat � extraire (valeur comprise
	*    entre 1 et selectResult.length() - 1),
	*  - definition: Le dictionnaire de la table, permettant de r�cup�rer les
	*    noms des champs.
	*
	* Retourne: Un tableau d'IsisParameter correspondant � la
	* d�composition d'une ligne de donn�es.
	*
	* L�ve: InnerException.
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
		// On v�rifie la validit� de l'argument
		if(selectResult == null || rowIndex < 1 ||
			rowIndex >= selectResult.length)
		{
			trace_errors.writeTrace("Index hors limites");
			// L'index est hors limites, c'est une erreur
			throw new InnerException("&ERR_CannotExtractObjectFromSelect",
				"&ERR_IndexOutOfBounds", null);
		}
		// On d�compose la ligne suivant le s�parateur
		UtilStringTokenizer line_tokenizer =
			new UtilStringTokenizer(selectResult[rowIndex],
			definition.separator);
		// On v�rifie que le nombre de champs est identique
		if(line_tokenizer.getTokensCount() != definition.columns.length)
		{
			trace_errors.writeTrace(
				"Nombre de champs diff�rents entre les valeurs et la d�finition");
			// Le nombre de champs est diff�rent
			throw new InnerException("&ERR_CannotExtractObjectFromSelect",
				"&ERR_NoSameNumberOfFields", null);
		}
		// On instancie le tableau de param�tres
		parameters = new IsisParameter[line_tokenizer.getTokensCount()];
		// On va cr�er les param�tres un par un
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
	* Cette m�thode statique permet de construire la cl� d'une ligne de donn�e
	* � partir d'un tableau de IsisParameter et de la d�finition des
	* champs.
	* Cette cl� permet d'identifier de mani�re unique une ligne de donn�e dans
	* une table.
	*
	* Si un probl�me survient lors de la construction de la cl�, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - objectParameters: Un tableau de IsisParameter contenant les
	*    valeurs des champs,
	*  - definition: Le dictionnaire de la table, permettant de r�cup�rer les
	*    noms des champs.
	*
	* Retourne: Une cha�ne contenant la valeur de la cl�.
	*
	* L�ve: InnerException.
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
		// On v�rifie la validit� des arguments
		if(objectParameters == null || objectParameters.length == 0 ||
			definition == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// Au moins un des arguments n'est pas valide, on l�ve une exception
			throw new InnerException("&ERR_CannotExtractKeyFromSelect",
				"&ERR_InvalidArgument", null);
		}
		key = new StringBuffer();
		// On va r�cup�rer chaque nom de champ constituant la cl�
		for(int index = 0 ; index < definition.key.length ; index ++)
		{
			boolean found = false;

			trace_debug.writeTrace("Champ de cl�: " + definition.key[index]);
			// On va rechercher la valeur du champ de m�me nom
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
				// Si on arrive ici, c'est que l'on n'a pas trouv� le champ
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
	* Cette m�thode statique permet de r�cup�rer la valeur contenue dans 
	* l'objet pass� en param�tre, si celui n'est pas nul, qui doit �tre de 
	* type IsisParameter.
	*
	* Arguments:
	*  - parameters: Un tableau de IsisParameter dans lequel la valeur du
	*    param�tre est recherch�e,
	*  - parameterName: Le param�tre dont on veut r�cup�rer la valeur.
	*
	* Retourne: La valeur contenue dans le param�tre, ou null.
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
			// Il n'y a pas de param�tre, on sort
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
	* Cette m�thode statique permet de r�cup�rer la valeur d'un param�tre, 
	* dont le nom est pass� en argument, s'il existe dans la liste index�e 
	* pass�e en argument.
	* 
	* Arguments:
	*  - context: Une r�f�rence sur un objet IndexedList dans laquelle 
	*    chercher le param�tre,
	*  - parameterName: Le param�tre dont on veut r�cup�rer la valeur.
	* 
	* Retourne: La valeur contenue dans le param�tre, ou null.
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
			// Il n'y a pas de param�tre, on sort
			trace_methods.endOfMethod();
			return value;
		}
		// On r�cup�re le param�tre associ� au nom
		IsisParameter parameter = (IsisParameter)context.get(parameterName);
		// Si le param�tre est non nul, on r�cup�re sa valeur
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
	* Cette m�thode permet de r�cup�rer la valeur contenue dans la cha�ne
	* "valueChain". Elle g�re les cas o� la valeur est plac�e entre simples-
	* quotes ou entre doubles-quotes.
	*
	* Arguments:
	*  - valueChain: La valeur dont il faut �ventuellement retirer les quotes.
	*
	* Retourne: La valeur contenue dans la cha�ne valueChain.
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