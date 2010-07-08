/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/LabelFactory.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de construction de libellé
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
* Correction d'un problème lors de la recherche du noeud parent.
*
* Revision 1.12  2008/06/12 15:50:46  tz
* Prise en compte des explorations récursives (le noeud parent est de
* même type).
*
* Revision 1.11  2006/11/09 12:06:43  tz
* Méthodes evaluate() et getParameterValue() en zone publique.
*
* Revision 1.10  2005/10/07 08:19:05  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.9  2005/07/01 12:07:40  tz
* Modification du composant pour les traces
* Evaluation du nom de l'icône associé au libellé
*
* Revision 1.8  2004/11/02 08:49:50  tz
* Gestion des leasings sur les définitions.
*
* Revision 1.7  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.6  2004/10/13 13:55:00  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.5  2002/11/19 08:39:34  tz
* Correction de la fiche Inuit/76.
*
* Revision 1.4  2002/09/20 10:47:28  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.3  2002/04/05 15:49:49  tz
* Cloture itération IT1.2
*
* Revision 1.2  2002/03/27 09:42:20  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.1  2001/12/28 16:31:19  tz
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
* Cette classe abstraite a la responsabilité de générer des libellés pour les
* noeuds graphiques.
* Le libellé du noeud graphique est créé à partir d'un objet IsisNodeLabel,
* contenu dans la définition de la table (voir IsisTableDefinition).
* Plusieurs cas possibles sont utilisables. Les différents cas, avec leur ordre
* de recherche sont exposés ci-dessous:
*  1. <ParentTableName>.<TableName>.<Suffix>
*  2. <TableName>.<Suffix>
*  3. <Default>
*
* Où:
*  - ParentTableName correspond au nom de la table parente du noeud concerné,
*  - TableName correspond au nom de la table du noeud concerné,
*  - Suffix correspond au type de noeud ("Table" ou "Item"),
*  - Default correspond à la valeur par défaut (la clé dans le cas d'un noeud
*    instance, ou le nom de la table dans l'autre cas).
* ----------------------------------------------------------*/
public abstract class LabelFactory
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: createLabel
	*
	* Description:
	* Cette méthode statique permet de générer un object IsisNodeLabel pour
	* un noeud passé en argument. L'objet est récupéré à partir d'une des
	* formules décrites dans la description de la classe, en fonction de ce
	* que la définition de la table propose comme possibilités.
	*
	* Si un problème survient lors de la création du libellé, l'exception
	* InnerException est levée.
	*
	* Arguments:
	*  - agentName: Le nom de l'agent d'où a été extrait le noeud,
	*  - iClesName: Le nom du I-CLES auquel est apparenté le noeud,
	*  - serviceType: Le type du service auquel est apparenté le noeud,
 	*  - objectNode: Une référence sur l'objet GenericTreeObjectNode pour
	*    lequel il faut générer un libellé,
	*  - context: Une référence sur une liste indexée contenant le contexte du
	*    noeud graphique.
	*
	* Lève: InnerException.
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
			// On récupère les paramètres de config définissant les types
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
				"Erreur lors de la récupération de paramètres: " +
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
		// On va récupérer la définition de la table
		TableDefinitionManager definition_manager =
			TableDefinitionManager.getInstance();
		IsisTableDefinition definition =
			definition_manager.getTableDefinition(agentName,
			iClesName, serviceType, objectNode.getDefinitionFilePath());
		if(definition == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Le dictionnaire de la table n'est pas chargé!");
			// Le dictionnaire de la table n'est pas chargé, il faut utiliser
			// les valeurs par défaut
			if(objectNode instanceof GenericTreeClassNode)
			{
				// Le libellé par défaut est le nom de la table
				node_label =
					new IsisNodeLabel(null, objectNode.getTableName(), null);
			}
			else
			{
				// Le libellé par défaut est la clé de l'objet
				node_label =
					new IsisNodeLabel(null, objectNode.getKey(), null);
			}
			objectNode.setLabel(node_label);
			trace_methods.endOfMethod();
			return;
		}
		// Maintenant, on récupère le message du libellé
		node_label = getTableLabel(parent_table_name, objectNode.getTableName(),
			suffix,	definition);
		trace_debug.writeTrace("node_label=" + node_label);
		// On libère l'utilisatio de la définition
		definition_manager.releaseTableDefinitionLeasing(definition);
		// On regarde s'il y a un message
		if(node_label == null)
		{
			// Il faut utiliser les valeurs par défaut
			if(objectNode instanceof GenericTreeClassNode)
			{
				// Le libellé par défaut est le nom de la table
				node_label =
					new IsisNodeLabel(null, objectNode.getTableName(), null);
			}
			else
			{
				// Le libellé par défaut est la clé de l'objet
				node_label =
					new IsisNodeLabel(null, objectNode.getKey(), null);
			}
		}
		else
		{
			// On récupère le message évalué
			node_label.label = evaluate(node_label.label, 
				objectNode.getObjectParameters(), context, agentName);
			// On va également évaluer l'identifiant de l'icône
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
	* Cette méthode statique permet de générer un libellé pour un élément de
	* menu correspondant au détail d'un noeud lié. Le libellé est construit à
	* partir du nom de la table étrangère avec le suffixe "Table".
	*
	* Si un problème survient lors de la création du label, l'exception
	* InnerException est levée.
	*
	* Arguments:
	*  - node: Une référence sur l'objet GenericTreeObjectNode pour lequel il
	*    faut générer le libellé de menu,
	*  - foreignTableName: Le nom de la table étrangère.
	*
	* Retourne: Le libellé de l'élément de menu.
	*
	* Lève: InnerException.
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
			// On récupère les paramètres de config définissant les types
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			class_object_type = configuration_api.getString("I-SIS",
				"TableObjectType");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la récupération de paramètres: " +
				exception.getMessage());
		}
		// On récupère la définition de la table
		TableDefinitionManager definition_manager =
			TableDefinitionManager.getInstance();
		IsisTableDefinition definition =
			definition_manager.getTableDefinition(node.getAgentName(),
			node.getIClesName(), node.getServiceType(), foreignTableName, 
			node.getContext(true), node.getServiceSession());
		// Maintenant, on récupère le message du libellé
		node_label = getTableLabel(null, foreignTableName, class_object_type,
			definition);
		trace_debug.writeTrace("node_label=" + node_label);
		// On libère l'utilisation de la définition
		definition_manager.releaseTableDefinitionLeasing(definition);
		// On regarde s'il y a un message
		if(node_label == null)
		{
			// Il faut utiliser les valeurs par défaut
			node_label = new IsisNodeLabel(null, foreignTableName, null);
		}
		else
		{
			// On récupère le message évalué
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
	* Cette méthode statique permet de substituer toutes les références à des 
	* paramètres d'environnement dans la chaîne passé en argument par leurs 
	* valeurs. Les références à des paramètres est indiqué par la syntaxe 
	* %[<ParameterName>].
	* 
	* Arguments:
	*  - stringToEvaluate: La chaîne à évaluer,
	*  - objectParameters: Un tableau d'IsisParameter à utiliser pour trouver 
	*    la valeur du paramètre,
	*  - context: Une référence sur une liste indexée contenant un contexte,
	*  - agentName: Le nom éventuel de l'agent.
	* 
	* Retourne: La chaîne évaluée.
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
			// Si la chaîne n'est pas terminée, on ignore
			if(end < 0)
			{
				break;
			}
			// Le nom de la variable est contenu entre %[ et ]
			String variable_name = string.substring(start + 2, end);
			if(variable_name.equals("HOSTNAME") == true)
			{
				// Dans le cas de l'hôte, on utilise l'hôte du noeud
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
	* Cette méthode statique permet de récupérer la valeur d'un paramètre 
	* passé en argument. Le paramètre est tout d'abord recherché parmis les 
	* paramètres du noeud, ensuite dans le contexte.
	* 
	* Arguments:
	*  - parameterName: Le nom du paramètre,
	*  - objectParameters: Un tableau d'IsisParameter à utiliser pour trouver 
	*    la valeur du paramètre,
	*  - context: Une référence sur une liste indexée à utiliser pour trouver 
	*    la valeur du paramètre.
	* 
	* Retourne: La valeur du paramètre, s'il a été trouvé, ou une chaîne vide.
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
		// On commence par rechercher le paramètre dans ceux du noeud
		if(objectParameters != null && objectParameters.length > 0)
		{
			for(int index = 0 ; index < objectParameters.length ; index ++)
			{
				if(objectParameters[index].name.equals(parameterName) == true)
				{
					// Le paramètre a été trouvé
					trace_debug.writeTrace("Value=" +
						objectParameters[index].value);
					trace_methods.endOfMethod();
					return objectParameters[index].value;
				}
			}
		}
		// Ensuite, on cherche le paramètre dans le contexte
		if(context != null)
		{
			IsisParameter parameter =
				(IsisParameter)context.get(parameterName);
			if(parameter != null)
			{
				// Le paramètre a été trouvé
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
	* Cette méthode statique permet de récupérer une instance de IsisNodeLabel
	* pour une table spécifiée en fonction d'une table parent, du nom de la
	* table, d'un suffixe et d'une définition de la table.
	*
	* Si un problème survient lors de la récupération des libellés, l'exception
	* InnerException est levée.
	*
	* Arguments:
	*  - parentTableName: Le nom de la table parent,
	*  - tableName: Le nom de la table pour laquelle le libellé doit être
	*    récupéré,
	*  - suffix: Le suffixe du type de noeud (Table ou Instance),
	*  - definition: Une référence sur la définition de la table.
	*
	* Retourne: L'objet IsisNodeLabel correspondant à la table.
	*
	* Lève: InnerException.
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
		// Le premier test est à faire avec
		// parentTableName.tableName.suffix
		// si parentTableName est non null
		if(parentTableName != null)
		{
			identifier = parentTableName + "." + tableName + "." + suffix;
			label = lookForLabel(identifier, definition.labels);
			// On regarde si le message a été trouvé
			if(label != null)
			{
				// Le message a été trouvé
				trace_methods.endOfMethod();
				return label;
			}
		}
		// Le second test est à faire avec
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
	* Cette méthode permet de rechercher dans la liste des libellés de table
	* (passée en second argument) une occurence correspondant à l'identifiant
	* passé en premier argument. Si cette occurence est trouvée, une copie de
	* l'objet est retournée.
	*
	* Arguments:
	*  - identifier: L'identifiant à utiliser pour trouver le libellé dans la
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
				trace_debug.writeTrace("Identifiant trouvé");
				// L'identifiant a été trouvé
				trace_methods.endOfMethod();
				return new IsisNodeLabel(labels[index].nodeIdentity,
					labels[index].label, labels[index].icon);
			}
		}
		trace_debug.writeTrace("Identifiant non trouvé");
		trace_methods.endOfMethod();
		return null;
	}
}