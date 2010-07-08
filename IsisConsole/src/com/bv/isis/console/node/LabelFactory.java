/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/LabelFactory.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de construction de libell�
* DATE:        27/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: LabelFactory.java,v $
* Revision 1.14  2009/01/14 14:22:26  tz
* Prise en compte de la modification des packages.
*
* Revision 1.13  2008/07/17 15:59:17  tz
* Correction d'un probl�me lors de la recherche du noeud parent.
*
* Revision 1.12  2008/06/12 15:50:46  tz
* Prise en compte des explorations r�cursives (le noeud parent est de
* m�me type).
*
* Revision 1.11  2006/11/09 12:06:43  tz
* M�thodes evaluate() et getParameterValue() en zone publique.
*
* Revision 1.10  2005/10/07 08:19:05  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.9  2005/07/01 12:07:40  tz
* Modification du composant pour les traces
* Evaluation du nom de l'ic�ne associ� au libell�
*
* Revision 1.8  2004/11/02 08:49:50  tz
* Gestion des leasings sur les d�finitions.
*
* Revision 1.7  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.6  2004/10/13 13:55:00  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.5  2002/11/19 08:39:34  tz
* Correction de la fiche Inuit/76.
*
* Revision 1.4  2002/09/20 10:47:28  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.3  2002/04/05 15:49:49  tz
* Cloture it�ration IT1.2
*
* Revision 1.2  2002/03/27 09:42:20  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.1  2001/12/28 16:31:19  tz
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
import com.bv.core.config.ConfigurationAPI;

//
// Imports du projet
//
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisNodeLabel;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;

/*----------------------------------------------------------
* Nom: LabelFactory
*
* Description:
* Cette classe abstraite a la responsabilit� de g�n�rer des libell�s pour les
* noeuds graphiques.
* Le libell� du noeud graphique est cr�� � partir d'un objet IsisNodeLabel,
* contenu dans la d�finition de la table (voir IsisTableDefinition).
* Plusieurs cas possibles sont utilisables. Les diff�rents cas, avec leur ordre
* de recherche sont expos�s ci-dessous:
*  1. <ParentTableName>.<TableName>.<Suffix>
*  2. <TableName>.<Suffix>
*  3. <Default>
*
* O�:
*  - ParentTableName correspond au nom de la table parente du noeud concern�,
*  - TableName correspond au nom de la table du noeud concern�,
*  - Suffix correspond au type de noeud ("Table" ou "Item"),
*  - Default correspond � la valeur par d�faut (la cl� dans le cas d'un noeud
*    instance, ou le nom de la table dans l'autre cas).
* ----------------------------------------------------------*/
public abstract class LabelFactory
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: createLabel
	*
	* Description:
	* Cette m�thode statique permet de g�n�rer un object IsisNodeLabel pour
	* un noeud pass� en argument. L'objet est r�cup�r� � partir d'une des
	* formules d�crites dans la description de la classe, en fonction de ce
	* que la d�finition de la table propose comme possibilit�s.
	*
	* Si un probl�me survient lors de la cr�ation du libell�, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - agentName: Le nom de l'agent d'o� a �t� extrait le noeud,
	*  - iClesName: Le nom du I-CLES auquel est apparent� le noeud,
	*  - serviceType: Le type du service auquel est apparent� le noeud,
 	*  - objectNode: Une r�f�rence sur l'objet GenericTreeObjectNode pour
	*    lequel il faut g�n�rer un libell�,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte du
	*    noeud graphique.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public static void createLabel(
		String agentName,
		String iClesName,
		String serviceType,
		GenericTreeObjectNode objectNode,
		IndexedList context
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelFactory", "createLabel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String parent_table_name = null;
		String suffix = null;
		IsisNodeLabel node_label = null;
		String class_object_type = "Table";
		String instance_object_type = "Instance";

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("objectNode=" + objectNode);
		trace_arguments.writeTrace("context=" + context);
		// Tout d'abord, il faut regarder quel est le type de noeud
		try
		{
			// On r�cup�re les param�tres de config d�finissant les types
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			class_object_type = configuration_api.getString("I-SIS",
				"TableObjectType");
			instance_object_type = configuration_api.getString("I-SIS",
				"InstanceObjectType");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la r�cup�ration de param�tres: " +
				exception.getMessage());
		}
		if(objectNode instanceof GenericTreeClassNode)
		{
			suffix = class_object_type;
		}
		else
		{
			suffix = instance_object_type;
		}
		// On cherche le nom de la table parent
		GenericTreeObjectNode parent_node =
			(GenericTreeObjectNode)objectNode.getParent();
		while(true)
		{
			if(parent_node == null)
			{
				break;
			}
			if(parent_node instanceof GenericTreeClassNode)
			{
				parent_node = (GenericTreeObjectNode)parent_node.getParent();
				continue;
			}
			parent_table_name = parent_node.getTableName();
			break;
		}
		trace_debug.writeTrace("suffix=" + suffix);
		trace_debug.writeTrace("parent_table_name=" + parent_table_name);
		// On va r�cup�rer la d�finition de la table
		TableDefinitionManager definition_manager =
			TableDefinitionManager.getInstance();
		IsisTableDefinition definition =
			definition_manager.getTableDefinition(agentName,
			iClesName, serviceType, objectNode.getDefinitionFilePath());
		if(definition == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Le dictionnaire de la table n'est pas charg�!");
			// Le dictionnaire de la table n'est pas charg�, il faut utiliser
			// les valeurs par d�faut
			if(objectNode instanceof GenericTreeClassNode)
			{
				// Le libell� par d�faut est le nom de la table
				node_label =
					new IsisNodeLabel(null, objectNode.getTableName(), null);
			}
			else
			{
				// Le libell� par d�faut est la cl� de l'objet
				node_label =
					new IsisNodeLabel(null, objectNode.getKey(), null);
			}
			objectNode.setLabel(node_label);
			trace_methods.endOfMethod();
			return;
		}
		// Maintenant, on r�cup�re le message du libell�
		node_label = getTableLabel(parent_table_name, objectNode.getTableName(),
			suffix,	definition);
		trace_debug.writeTrace("node_label=" + node_label);
		// On lib�re l'utilisatio de la d�finition
		definition_manager.releaseTableDefinitionLeasing(definition);
		// On regarde s'il y a un message
		if(node_label == null)
		{
			// Il faut utiliser les valeurs par d�faut
			if(objectNode instanceof GenericTreeClassNode)
			{
				// Le libell� par d�faut est le nom de la table
				node_label =
					new IsisNodeLabel(null, objectNode.getTableName(), null);
			}
			else
			{
				// Le libell� par d�faut est la cl� de l'objet
				node_label =
					new IsisNodeLabel(null, objectNode.getKey(), null);
			}
		}
		else
		{
			// On r�cup�re le message �valu�
			node_label.label = evaluate(node_label.label, 
				objectNode.getObjectParameters(), context, agentName);
			// On va �galement �valuer l'identifiant de l'ic�ne
			node_label.icon = evaluate(node_label.icon,
				objectNode.getObjectParameters(), context, agentName);
		}
		trace_debug.writeTrace("node_label=" + node_label.label);
		trace_debug.writeTrace("node_icon=" + node_label.icon);
		// On le positionne pour le noeud
		objectNode.setLabel(node_label);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: createLabelForForeignKey
	*
	* Description:
	* Cette m�thode statique permet de g�n�rer un libell� pour un �l�ment de
	* menu correspondant au d�tail d'un noeud li�. Le libell� est construit �
	* partir du nom de la table �trang�re avec le suffixe "Table".
	*
	* Si un probl�me survient lors de la cr�ation du label, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - node: Une r�f�rence sur l'objet GenericTreeObjectNode pour lequel il
	*    faut g�n�rer le libell� de menu,
	*  - foreignTableName: Le nom de la table �trang�re.
	*
	* Retourne: Le libell� de l'�l�ment de menu.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public static String createLabelForForeignKey(
		GenericTreeObjectNode node,
		String foreignTableName
		)
		throws
			InnerException
	{
		IsisNodeLabel node_label = null;
		String class_object_type = "Table";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelFactory", "createLabelForForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("node=" + node);
		trace_arguments.writeTrace("foreignTableName=" + foreignTableName);
		try
		{
			// On r�cup�re les param�tres de config d�finissant les types
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			class_object_type = configuration_api.getString("I-SIS",
				"TableObjectType");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la r�cup�ration de param�tres: " +
				exception.getMessage());
		}
		// On r�cup�re la d�finition de la table
		TableDefinitionManager definition_manager =
			TableDefinitionManager.getInstance();
		IsisTableDefinition definition =
			definition_manager.getTableDefinition(node.getAgentName(),
			node.getIClesName(), node.getServiceType(), foreignTableName, 
			node.getContext(true), node.getServiceSession());
		// Maintenant, on r�cup�re le message du libell�
		node_label = getTableLabel(null, foreignTableName, class_object_type,
			definition);
		trace_debug.writeTrace("node_label=" + node_label);
		// On lib�re l'utilisation de la d�finition
		definition_manager.releaseTableDefinitionLeasing(definition);
		// On regarde s'il y a un message
		if(node_label == null)
		{
			// Il faut utiliser les valeurs par d�faut
			node_label = new IsisNodeLabel(null, foreignTableName, null);
		}
		else
		{
			// On r�cup�re le message �valu�
			node_label.label = evaluate(node_label.label,
				node.getObjectParameters(), node.getContext(true), 
				node.getAgentName());
		}
		trace_debug.writeTrace("node_label.label=" + node_label.label);
		trace_methods.endOfMethod();
		return node_label.label;
	}

	/*----------------------------------------------------------
	* Nom: evaluate
	* 
	* Description:
	* Cette m�thode statique permet de substituer toutes les r�f�rences � des 
	* param�tres d'environnement dans la cha�ne pass� en argument par leurs 
	* valeurs. Les r�f�rences � des param�tres est indiqu� par la syntaxe 
	* %[<ParameterName>].
	* 
	* Arguments:
	*  - stringToEvaluate: La cha�ne � �valuer,
	*  - objectParameters: Un tableau d'IsisParameter � utiliser pour trouver 
	*    la valeur du param�tre,
	*  - context: Une r�f�rence sur une liste index�e contenant un contexte,
	*  - agentName: Le nom �ventuel de l'agent.
	* 
	* Retourne: La cha�ne �valu�e.
	* ----------------------------------------------------------*/
	public static String evaluate(
		String stringToEvaluate,
		IsisParameter[] objectParameters,
		IndexedList context,
		String agentName
		)
	{
		StringBuffer evaluated_string = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelFactory", "evaluate");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("stringToEvaluate=" + stringToEvaluate);
		trace_arguments.writeTrace("objectParameters=" + objectParameters);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("agentName=" + agentName);
		// On initialise le buffer avec le label
		evaluated_string = new StringBuffer(stringToEvaluate);
		while(true)
		{
			String string = evaluated_string.toString();
			// On recherche la position de %[
			int start = string.indexOf("%[");
			if(start == -1)
			{
				break;
			}
			// On recherche la position de ]
			int end = string.indexOf(']', start);
			// Si la cha�ne n'est pas termin�e, on ignore
			if(end < 0)
			{
				break;
			}
			// Le nom de la variable est contenu entre %[ et ]
			String variable_name = string.substring(start + 2, end);
			if(variable_name.equals("HOSTNAME") == true)
			{
				// Dans le cas de l'h�te, on utilise l'h�te du noeud
				evaluated_string.replace(start, end + 1, agentName);
			}
			else
			{
				// On remplace la variable par sa valeur
				evaluated_string.replace(start, end + 1,
					getParameterValue(variable_name, objectParameters,
					context));
			}
		}
		trace_methods.endOfMethod();
		return evaluated_string.toString();
	}

	/*----------------------------------------------------------
	* Nom: getParameterValue
	* 
	* Description:
	* Cette m�thode statique permet de r�cup�rer la valeur d'un param�tre 
	* pass� en argument. Le param�tre est tout d'abord recherch� parmis les 
	* param�tres du noeud, ensuite dans le contexte.
	* 
	* Arguments:
	*  - parameterName: Le nom du param�tre,
	*  - objectParameters: Un tableau d'IsisParameter � utiliser pour trouver 
	*    la valeur du param�tre,
	*  - context: Une r�f�rence sur une liste index�e � utiliser pour trouver 
	*    la valeur du param�tre.
	* 
	* Retourne: La valeur du param�tre, s'il a �t� trouv�, ou une cha�ne vide.
	* ----------------------------------------------------------*/
	public static String getParameterValue(
		String parameterName,
		IsisParameter[] objectParameters,
		IndexedList context
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelFactory", "getParameterValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parameterName=" + parameterName);
		trace_arguments.writeTrace("objectParameters=" + objectParameters);
		trace_arguments.writeTrace("context=" + context);
		// On commence par rechercher le param�tre dans ceux du noeud
		if(objectParameters != null && objectParameters.length > 0)
		{
			for(int index = 0 ; index < objectParameters.length ; index ++)
			{
				if(objectParameters[index].name.equals(parameterName) == true)
				{
					// Le param�tre a �t� trouv�
					trace_debug.writeTrace("Value=" +
						objectParameters[index].value);
					trace_methods.endOfMethod();
					return objectParameters[index].value;
				}
			}
		}
		// Ensuite, on cherche le param�tre dans le contexte
		if(context != null)
		{
			IsisParameter parameter =
				(IsisParameter)context.get(parameterName);
			if(parameter != null)
			{
				// Le param�tre a �t� trouv�
				trace_debug.writeTrace("Value=" + parameter.value);
				trace_methods.endOfMethod();
				return parameter.value;
			}
		}
		trace_methods.endOfMethod();
		return "";
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: getTableLabel
	*
	* Description:
	* Cette m�thode statique permet de r�cup�rer une instance de IsisNodeLabel
	* pour une table sp�cifi�e en fonction d'une table parent, du nom de la
	* table, d'un suffixe et d'une d�finition de la table.
	*
	* Si un probl�me survient lors de la r�cup�ration des libell�s, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - parentTableName: Le nom de la table parent,
	*  - tableName: Le nom de la table pour laquelle le libell� doit �tre
	*    r�cup�r�,
	*  - suffix: Le suffixe du type de noeud (Table ou Instance),
	*  - definition: Une r�f�rence sur la d�finition de la table.
	*
	* Retourne: L'objet IsisNodeLabel correspondant � la table.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private static IsisNodeLabel getTableLabel(
		String parentTableName,
		String tableName,
		String suffix,
		IsisTableDefinition definition
		)
		throws
			InnerException
	{
		String identifier = null;
		IsisNodeLabel label = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelFactory", "getTableLabel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parentTableName=" + parentTableName);
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("suffix=" + suffix);
		trace_arguments.writeTrace("definition=" + definition);
		// Le premier test est � faire avec
		// parentTableName.tableName.suffix
		// si parentTableName est non null
		if(parentTableName != null)
		{
			identifier = parentTableName + "." + tableName + "." + suffix;
			label = lookForLabel(identifier, definition.labels);
			// On regarde si le message a �t� trouv�
			if(label != null)
			{
				// Le message a �t� trouv�
				trace_methods.endOfMethod();
				return label;
			}
		}
		// Le second test est � faire avec
		// tableName.suffix
		identifier = tableName + "." + suffix;
		label = lookForLabel(identifier, definition.labels);
		trace_methods.endOfMethod();
		return label;
	}

	/*----------------------------------------------------------
	* Nom: lookForLabel
	*
	* Description:
	* Cette m�thode permet de rechercher dans la liste des libell�s de table
	* (pass�e en second argument) une occurence correspondant � l'identifiant
	* pass� en premier argument. Si cette occurence est trouv�e, une copie de
	* l'objet est retourn�e.
	*
	* Arguments:
	*  - identifier: L'identifiant � utiliser pour trouver le libell� dans la
	*    table,
	*  - labels: Un tableau de IsisNodeLabel dans lequel il faut chercher.
	*
	* Retourne: Un objet IsisNodeLabel ou null.
	* ----------------------------------------------------------*/
	private static IsisNodeLabel lookForLabel(
		String identifier,
		IsisNodeLabel[] labels
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelFactory", "lookForLabel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("identifier=" + identifier);
		trace_arguments.writeTrace("labels=" + labels);
		// On regarde chaque label
		for(int index = 0 ; index < labels.length ; index ++)
		{
			trace_debug.writeTrace("labels[" + index + "]=" +
				labels[index].nodeIdentity);
			if(identifier.equals(labels[index].nodeIdentity) == true)
			{
				trace_debug.writeTrace("Identifiant trouv�");
				// L'identifiant a �t� trouv�
				trace_methods.endOfMethod();
				return new IsisNodeLabel(labels[index].nodeIdentity,
					labels[index].label, labels[index].icon);
			}
		}
		trace_debug.writeTrace("Identifiant non trouv�");
		trace_methods.endOfMethod();
		return null;
	}
}