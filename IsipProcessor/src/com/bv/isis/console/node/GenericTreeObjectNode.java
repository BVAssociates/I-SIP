/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/GenericTreeObjectNode.java,v $
* $Revision: 1.22 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de représentation d'un noeud instance
* DATE:        04/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: GenericTreeObjectNode.java,v $
* Revision 1.22  2009/01/23 17:28:41  tz
* Pas de setParent(null) dans la méthode destroy().
*
* Revision 1.21  2009/01/14 14:22:25  tz
* Prise en compte de la modification des packages.
*
* Revision 1.20  2008/08/11 10:46:52  tz
* Prise en compte du cas du noeud racine.
*
* Revision 1.19  2006/03/07 09:28:34  tz
* Modification des états des noeuds
*
* Revision 1.18  2005/10/07 11:25:45  tz
* Changement mineur.
*
* Revision 1.17  2005/10/07 08:19:14  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.16  2005/07/01 12:08:08  tz
* Modification du composant pour les traces
*
* Revision 1.15  2004/11/09 15:22:20  tz
* Déplacement de la mise à null du menu dans la méthode destroy().
*
* Revision 1.14  2004/11/02 08:50:32  tz
* Gestion de l'état du noeud,
* Gestion des leasings sur les définitions.
*
* Revision 1.13  2004/10/22 15:36:26  tz
* Ajout des méthodes getAgentSessionId(), getServiceSessionId() et
* forwardEvent() et destroyContextualMenu().
*
* Revision 1.12  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.11  2004/10/13 13:55:09  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.10  2004/07/29 12:01:50  tz
* Traitement de la fermeture et de la destruction sur arrêt du Portail
*
* Revision 1.9  2003/03/07 16:19:56  tz
* Prise en compte du mécanisme de log métier
* Ajout de l'auto-exploration
*
* Revision 1.8  2002/11/19 08:39:59  tz
* Suppression vieux code.
*
* Revision 1.7  2002/09/20 10:47:33  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.6  2002/08/13 12:59:45  tz
* Suppression de l'identifiant de la méthode d'exploration lors de la fermeture du noeud.
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.util.Vector;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.config.ConfigurationAPI;

//
// Imports du projet
//
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisNodeLabel;
import com.bv.isis.corbacom.IsisEventTypeEnum;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.abs.gui.TreeInterface;
import com.bv.isis.console.node.ContextualMenuItem;

/*----------------------------------------------------------
* Nom: GenericTreeObjectNode
*
* Description:
* Cette classe correspond à un noeud graphique représentant une instance
* d'objet de production extrait d'une table (voir la méthode getSelectResult()
* de la classe ServiceSessionInterface).
* Elle dérive de la classe DefaultMutableTreeNode afin de ne pas avoir à
* redéfinir une gestion des relations hiérarchiques (père et enfants). Cette
* classe ne fait que la spécialiser dans le contexte d'I-SIS.
*
* Elle permet de gérer les paramètres des noeuds (provenant de la ligne de
* résultat de Select correspondante), ainsi que le contexte (agrégation des
* paramètres "exportables" des noeuds parents et de tous les paramètres du
* noeud lui-même).
* ----------------------------------------------------------*/
public class GenericTreeObjectNode
	extends DefaultMutableTreeNode
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NodeStateEnum
	* 
	* Description:
	* Cet énuméré permet de définir les différents états du noeud.
	* Un noeud peut être dans l'état:
	*  - CLOSED: Lorsqu'il est fermé ou n'a jamais été exploré,
	*  - STATE_CHANGING: Lorsqu'il est en cours d'exploration ou de fermeture,
	*  - OPENED: Lorsqu'il a été exploré,
	*  - DESTROYING: Lorsqu'il est en cours de destruction,
	*  - CHILD_CHANGING: Lorsqu'un noeud enfant est en cours de changement 
	*    d'état.
 	* ----------------------------------------------------------*/
 	public final class NodeStateEnum
 	{
		public static final int CLOSED = 0;
		public static final int STATE_CHANGING = 1;
		public static final int OPENED = 2;
		public static final int DESTROYING = 3;
		public static final int CHILD_CHANGING = 4;
 	}

	/*----------------------------------------------------------
	* Nom: GenericTreeObjectNode
	*
	* Description:
	* Cette méthode est le seul constructeur de la classe. Elle permet de
	* définir la valeur de la clé, le chemin du dictionnaire de la table, le
	* nom de la table ainsi que les paramètres résultant du Select.
	*
	* Arguments:
	*  - objectParameters: Un tableau d'IsisParameter contenant toutes
	*    les données correspondant à une ligne de résultat de Select,
	*  - key: Une chaîne contenant la valeur de la clé pour cet objet,
	*  - agentName: Le nom de l'Agent à partir duquel l'objet a été construit,
	*  - iClesName: Le nom du I-CLES auquel est apparenté le noeud,
	*  - serviceType: Le type du service auquel est apparenté le noeud,
	*  - definitionFilePath: Le chemin complet d'accès au fichier dictionnaire
	*    de la table d'où a été extrait l'objet,
	*  - tableName: Le nom de la table d'où ont été extraites les informations.
	* ----------------------------------------------------------*/
	public GenericTreeObjectNode(
		IsisParameter[] objectParameters,
		String key,
		String agentName,
		String iClesName,
		String serviceType,
		String definitionFilePath,
		String tableName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "GenericTreeObjectNode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("objectParameters=" + objectParameters);
		trace_arguments.writeTrace("key=" + key);
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		trace_arguments.writeTrace("tableName=" + tableName);
		// Enregistrement des informations
		_definitionFilePath = definitionFilePath;
		_key = key;
		_objectParameters = objectParameters;
		_tableName = tableName;
		_menu = null;
		_label = null;
		_preprocessingData = null;
		_nodeState = NodeStateEnum.CLOSED; 
		// On va forcer l'utilisation d'un leasing sur la définition
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		manager.getTableDefinition(agentName, iClesName,
			serviceType, _definitionFilePath);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getObjectParameters
	*
	* Description:
	* Cette méthode permet de récupérer la liste des IsisParameter
	* correspondants au noeud graphique (tels qu'ils ont été fournis lors de la
	* construction du noeud).
	*
	* Retourne: Un tableau d'IsisParameter contenant tous les paramètres
	* de l'objet.
	* ----------------------------------------------------------*/
	public IsisParameter[] getObjectParameters()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getObjectParameters");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _objectParameters;
	}

	/*----------------------------------------------------------
	* Nom: getTableName
	*
	* Description:
	* Cette méthode permet de récupérer le nom de la table d'où a été extraites
	* les données correspindants au noeud graphique.
	*
	* Retourne: Le nom de la table source de l'objet.
	* ----------------------------------------------------------*/
	public String getTableName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getTableName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _tableName;
	}

	/*----------------------------------------------------------
	* Nom: getKey
	*
	* Description:
	* Cette méthode permet de récupérer la valeur de la clé de l'objet. Cette
	* clé permet d'identifier de manière unique un objet dans une liste
	* d'objets provenant de la même table.
	*
	* Retourne: La clé de l'objet, sous forme de chaîne de caractères.
	* ----------------------------------------------------------*/
	public String getKey()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getKey");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _key;
	}

	/*----------------------------------------------------------
	* Nom: getContext
	*
	* Description:
	* Cette méthode permet de récupérer une liste indexée contenant le contexte
	* de l'objet concerné. Ce contexte correspond à l'ensemble des paramètres
	* "exportables" (qui constituent la clé) des noeuds traversés pour
	* atteindre le noeud concerné. A cet ensemble sont ajoutés tous les
	* paramètres du noeud lui-même.
	* L'argument permet d'indiquer au noeud s'il est le noeud concerné ou non.
	* Suivant sa valeur, soit seuls les paramètres "exportables", soit tous les
	* paramètres, sont ajoutés à la liste.
	* S'il s'agit du noeud concerné, des paramètres correspondants à la clé et
	* au nom de la table sont ajoutés à la liste (les noms de ces
	* paramètres sont récupérés depuis le fichier de configuration, dans la
	* section "I-SIS", respectivement via les paramètres "KeyParameterName" et
	* "TableParameterName").
	*
	* Arguments:
	*  - isFirstObject: Un booléen indiquant si le noeud est le noeud concerné
	*    (true) ou non (false).
	*
	* Retourne: Une liste indexée contenant le contexte du noeud concerné.
	* ----------------------------------------------------------*/
	public IndexedList getContext(
		boolean isFirstObject
		)
	{
		IndexedList context_table = null;
		IsisParameter[] parameters = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getContext");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("isFirstObject=" + isFirstObject);
		// Récupération du noeud père
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remontée des appels jusqu'au noeud racine (pas de père)
		if(parent_node != null)
		{
			context_table = parent_node.getContext(false);
		}
		else
		{
			trace_debug.writeTrace("Noeud racine atteint !");
			// Il s'agit du noeud racine, création de la table de hash
			context_table = new IndexedList();
		}
		// On regarde s'il s'agit du premier noeud
		if(isFirstObject == false)
		{
			// Ce n'est pas le premier objet, on ne récupère que les paramètres
			// "exportables"
			trace_debug.writeTrace("Récupération des paramètres exportables");
			parameters = getExportableParameters();
		}
		else
		{
			// C'est le premier objet, on récupère tous les paramètres
			trace_debug.writeTrace("Récupération de tous les paramètres");
			parameters = getObjectParameters();
		}
		// On ajoute tous les paramètres à la table de hash
		trace_debug.writeTrace("parameters=" + parameters);
		if(parameters != null)
		{
			for(int index = 0 ; index < parameters.length ; index ++)
			{
				context_table.put(parameters[index].name, parameters[index]);
			}
		}
		// On ajoute les paramètres de préprocessing
		parameters = getPreprocessingData();
		if(parameters != null)
		{
			for(int index = 0 ; index < parameters.length ; index ++)
			{
				context_table.put(parameters[index].name, parameters[index]);
			}
		}
		// S'il s'agit du premier objet, il faut également ajouter des
		// paramètres relatifs à la clé et au nom de la table.
		if(isFirstObject == true)
		{
			String key_parameter_name = "KEY";
			String table_parameter_name = "TABLE";

			// Récupération des noms des paramètres depuis le fichier de
			// configuration
			try
			{
			    ConfigurationAPI configuration_api = new ConfigurationAPI();
				configuration_api.useSection("I-SIS");
				key_parameter_name =
					configuration_api.getString("KeyParameterName");
				table_parameter_name =
					configuration_api.getString("TableParameterName");
			}
			catch(Exception exception)
			{
				Trace trace_errors = TraceAPI.declareTraceErrors("Console");

				trace_errors.writeTrace(
					"Erreurs lors de la récupération de paramètres: " +
					exception);
			}
			// Construction et ajout des paramètres additionnels
			IsisParameter key_parameter =
				new IsisParameter(key_parameter_name, getKey(), '"');
			IsisParameter table_parameter =
				new IsisParameter(table_parameter_name, getTableName(), '"');
			context_table.put(key_parameter_name, key_parameter);
			context_table.put(table_parameter_name, table_parameter);
		}
		trace_methods.endOfMethod();
		// Retourne la table de hash
		return context_table;
	}

	/*----------------------------------------------------------
	* Nom: getExportableParameters
	*
	* Description:
	* Cette méthode permet de récupérer les paramètres du noeud graphique qui
	* sont "exportables" (contrairement à la méthode getObjectParameters() qui
	* retourne tous les paramètres).
	* Un paramètre exportable est un paramètre correspondant à une colonne de
	* la définition de la table constituant la clé. Ces paramètres
	* correspondent à des variables devant être héritées tout au long de la
	* navigation.
	*
	* Retourne: Un tableau d'IsisParameter contenant l'ensemble des
	* paramètres exportables de l'objet.
	* ----------------------------------------------------------*/
	public IsisParameter[] getExportableParameters()
	{
		Vector exportable_parameters = new Vector();
		IsisParameter[] parameters = null;
		String definition_path = getDefinitionFilePath();

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getExportableParameters");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// La première chose à faire est de récupérer les paramètres
		parameters = getObjectParameters();
		if(parameters == null || parameters.length == 0)
		{
			trace_debug.writeTrace("Il n'y a pas de paramètres !");
			trace_methods.endOfMethod();
			return null;
		}
		// Si le chemin du fichier de définition est vide, c'est qu'il 
		// n'y a pas de définition, on sort
		if(definition_path == null || definition_path.equals("") == true) {
			trace_methods.endOfMethod();
			return null;
		}
		// La deuxième chose à faire est de récupérer la définition de la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IsisTableDefinition table_definition = manager.getTableDefinition(
			getAgentName(), getIClesName(), getServiceType(),
			getDefinitionFilePath());
		if(table_definition == null)
		{
			// Il n'y a pas de définition de table, c'est une erreur
			trace_errors.writeTrace("La définition de la table " +
				getDefinitionFilePath() + " n'est pas chargée!");
			// On sort
			trace_methods.endOfMethod();
			return null;
		}
		// On va examiner la clé afin de déterminer quelles colonnes doivent
		// être "exportées".
		for(int index = 0 ; index < table_definition.key.length ; index ++)
		{
			// Récupération du nom du champ de la clé correspondant à l'index
			String field = table_definition.key[index];
			// On récupère le paramètre de même nom dans la table des paramètres
			for(int count = 0 ; count < parameters.length ; count ++)
			{
				if(parameters[count].name.equals(field) == true)
				{
				    exportable_parameters.add(parameters[count]);
					break;
				}
			}
		}
		// On libère l'utilisation de la définition
		manager.releaseTableDefinitionLeasing(table_definition);
		trace_methods.endOfMethod();
		// On retourne le tableau des informations contenues dans le vecteur
		return (IsisParameter[])exportable_parameters.toArray(
			new IsisParameter[0]);
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette méthode permet de fermer un objet graphique, et par conséquent une
	* portion de l'arbre d'exploration partant de cet objet. La fermeture d'un
	* noeud implique que le noeud doit être de nouveau exploré lors de son
	* expansion.
	* Cette méthode va supprimer tous ses noeuds fils et appeler leur méthode
	* destroy(), afin de supprimer toute l'arborescence descendante.
	* 
	* Arguments:
	*  - portalStopped: Un booléen indiquant si la fermeture est causée par
	*    l'arrêt du Portail (true) ou non (false).
	* ----------------------------------------------------------*/
	public void close(
		boolean portalStopped
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "close");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
								

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("portalStopped=" + portalStopped);
		// On positionne l'état
		setNodeState(NodeStateEnum.STATE_CHANGING);
		// Pour chaque noeud fils, destruction de celui-ci
		while(getChildCount() > 0)
		{
			// Récupération du noeud fils
			GenericTreeObjectNode child_node =
				(GenericTreeObjectNode)getChildAt(0);
			// On le détruit
			child_node.destroy(portalStopped);
			// On supprime le noeud fils
			remove(0);
			// On retire l'identifiant de la méthode d'exploration
			setUserObject(null);
		}
		// On positionne l'état
		setNodeState(NodeStateEnum.CLOSED);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: destroy
	*
	* Description:
	* Cette méthode permet de détruire un noeud graphique, ainsi que toute
	* l'arboresence descendante en dépendant. Elle libère toutes les ressources
	* allouées par l'objet et appelle la méthode destroy() sur tous ses
	* descendants, avant de les supprimer.
	* 
	* Arguments:
	*  - portalStopped: Un booléen indiquant si la destruction est causée par
	*    l'arrêt du Portail (true) ou non (false).
	* ----------------------------------------------------------*/
	public void destroy(
		boolean portalStopped
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "destroy");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("portalStopped=" + portalStopped);
		// On positionne l'état
		setNodeState(NodeStateEnum.DESTROYING);
		// On libère une utilisation de la définition de la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		if(_definitionFilePath != null)
		{
			manager.releaseTableDefinitionLeasing(getAgentName(), 
				getIClesName(), getServiceType(), _definitionFilePath);
		}
		// On commence par fermer le noeud (et destruction de tous les
		// descendants).
		close(portalStopped);
		// On libère les ressources
		_definitionFilePath = null;
		_key = null;
		_objectParameters = null;
		_tableName = null;
		_label = null;
		_preprocessingData = null;
		// On va déclencher la destruction du menu contextuel
		destroyContextualMenu(_menu);
		_menu = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getAgentName
	*
	* Description:
	* Cette méthode permet de récupérer le nom de l'Agent d'où sont extraites
	* les informations ayant servi à la construction du noeud graphique.
	* Cette méthode est récursive. C'est à dire qu'elle remonte dans
	* l'arborescence jusqu'à arriver à un noeud fournissant une valeur (un
	* objet ServiceTreeObjectNode).
	*
	* Retourne: Le nom de l'Agent source des informations.
	* ----------------------------------------------------------*/
	public String getAgentName()
	{
		String agent_name = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getAgentName");

		trace_methods.beginningOfMethod();
		// Récupération du noeud père
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remontée des appels jusqu'au noeud racine (pas de père)
		if(parent_node != null)
		{
			agent_name = parent_node.getAgentName();
		}
		trace_methods.endOfMethod();
		return agent_name;
	}

	/*----------------------------------------------------------
	* Nom: getDefinitionFilePath
	*
	* Description:
	* Cette méthode permet de récupérer le chemin complet d'accès au fichier
	* dictionnaire de la table d'où a été extrait l'objet correspondant au
	* noeud graphique.
	* Ce chemin est nécessaire pour récupérer la définition de la table (voir
	* la méthode getTableDefinition() de la classe TableDefinitionManager).
	*
	* Retourne: Le chemin complet d'accès au fichier dictionnaire de la table
	* source.
	* ----------------------------------------------------------*/
	public String getDefinitionFilePath()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getDefinitionFilePath");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _definitionFilePath;
	}

	/*----------------------------------------------------------
	* Nom: setLabel
	*
	* Description:
	* Cette méthode permet de fournir un libellé au noeud graphique. Ce libellé
	* sera utilisé lors de l'affichage du noeud dans l'arbre d'exploration.
	*
	* Arguments:
	*  - label: Le nouveau libellé du noeud graphique.
	* ----------------------------------------------------------*/
	public void setLabel(IsisNodeLabel label)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "setLabel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("label=" + label);
		_label = label;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getLabel
	*
	* Description:
	* Cette méthode permet de fixer l'objet IsisNodeLabel associé au noeud
	* graphique. Cet objet contient le libellé du noeud à afficher, ainsi que
	* le nom de l'icône à utiliser pour l'affichage.
	*
	* Retourne: L'objet IsisNodeLabel associé au noeud.
	* ----------------------------------------------------------*/
	public IsisNodeLabel getLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getLabel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _label;
	}

	/*----------------------------------------------------------
	* Nom: getServiceSession
	*
	* Description:
	* Cette méthode permet de récupérer la référence sur l'interface
	* ServiceSessionInterface à travers laquelle toute requête doit être
	* transmise.
	* Cette méthode est récursive. C'est à dire qu'elle remonte dans
	* l'arborescence jusqu'à arriver à un noeud fournissant une valeur (un objet
	* ServiceTreeObjectNode).
	*
	* Retourne: Une référence sur une interface ServiceSessionInterface, ou
	* null.
	* ----------------------------------------------------------*/
	public ServiceSessionInterface getServiceSession()
	{
		ServiceSessionInterface service_session = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getServiceSession");

		trace_methods.beginningOfMethod();
		// Récupération du noeud père
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remontée des appels jusqu'au noeud racine (pas de père)
		if(parent_node != null)
		{
			service_session = parent_node.getServiceSession();
		}
		trace_methods.endOfMethod();
		return service_session;
	}

	/*----------------------------------------------------------
	* Nom: getMenu
	*
	* Description:
	* Cette méthode permet de récupérer le menu contextuel associé au noeud
	* graphique s'il y en a un.
	*
	* Retourne: Une instance de JMenu contenant le menu contextuel du noeud
	* graphique, ou null.
	* ----------------------------------------------------------*/
	public JMenu getMenu()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getMenu");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _menu;
	}

	/*----------------------------------------------------------
	* Nom: setMenu
	*
	* Description:
	* Cette méthode permet de positionner le menu contextuel associé au noeud
	* graphique.
	*
	* Arguments:
	*  - menu: Une référence sur un JMenu contenant le menu contextuel du noeud
	*    graphique.
	* ----------------------------------------------------------*/
	public void setMenu(
		JMenu menu
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "setMenu");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menu=" + menu);
		_menu = menu;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setPreprocessingData
	*
	* Description:
	* Cette méthode permet de fournir au noeud un ensemble de paramètres qui
	* sont issus du traitement des données de préprocessing spécifiée dans la
	* méthode qui a construit le noeud.
	*
	* Arguments:
	*  - preprocessingData: Un tableau d'IsisParameter contenant les
	*    paramètres de préprocessing.
	* ----------------------------------------------------------*/
	public void setPreprocessingData(
		IsisParameter[] preprocessingData
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "setPreprocessingData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("preprocessingData=" + preprocessingData);
		_preprocessingData = preprocessingData;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getPreprocessingData
	*
	* Description:
	* Cette méthode permet de récupérer les paramètres de préprocessing qui ont
	* été associés au noeud (par la méthode setPreprocessingData()).
	*
	* Retourne: Un tableau d'IsisParameter contenant les paramètres de
	* préprocessing.
	* ----------------------------------------------------------*/
	public IsisParameter[] getPreprocessingData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getPreprocessingData");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _preprocessingData;
	}

	/*----------------------------------------------------------
	* Nom: toString
	*
	* Description:
	* Cette méthode permet de récupérer l'identification du noeud sous forme
	* de chaîne de caractères. Elle est composée du nom de la classe, du
	* caractère '@' et du hash-code du noeud.
	*
	* Retourne: L'identification du noeud sous forme de chaîne de caractères.
	* ----------------------------------------------------------*/
	public String toString()
	{
		return getClass().getName() + "@" + hashCode();
	}

	/*----------------------------------------------------------
	* Nom: getServiceName
	*
	* Description:
	* Cette méthode permet de récupérer le nom du service à partir duquel
	* le noeud a été créé.
	* Cette méthode est récursive. C'est à dire qu'elle remonte dans
	* l'arborescence jusqu'à arriver à un noeud fournissant une valeur (un
	* objet ServiceTreeObjectNode).
	*
	* Retourne: Le nom du service.
	* ----------------------------------------------------------*/
	public String getServiceName()
	{
		String service_name = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "GenericTreeObjectNode", "getServiceName");

		trace_methods.beginningOfMethod();
		// Récupération du noeud père
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remontée des appels jusqu'au noeud racine (pas de père)
		if(parent_node != null)
		{
			service_name = parent_node.getServiceName();
		}
		trace_methods.endOfMethod();
		return service_name;
	}

	/*----------------------------------------------------------
	* Nom: getServiceType
	*
	* Description:
	* Cette méthode permet de récupérer le type du service à partir duquel
	* le noeud a été créé.
	* Cette méthode est récursive. C'est à dire qu'elle remonte dans
	* l'arborescence jusqu'à arriver à un noeud fournissant une valeur (un
	* objet ServiceTreeObjectNode).
	*
	* Retourne: Le type du service.
	* ----------------------------------------------------------*/
	public String getServiceType()
	{
		String service_type = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "GenericTreeObjectNode", "getServiceType");

		trace_methods.beginningOfMethod();
		// Récupération du noeud père
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remontée des appels jusqu'au noeud racine (pas de père)
		if(parent_node != null)
		{
			service_type = parent_node.getServiceType();
		}
		trace_methods.endOfMethod();
		return service_type;
	}

	/*----------------------------------------------------------
	* Nom: getIClesName
	*
	* Description:
	* Cette méthode permet de récupérer le nom du I-CLES à partir duquel
	* le noeud a été créé.
	* Cette méthode est récursive. C'est à dire qu'elle remonte dans
	* l'arborescence jusqu'à arriver à un noeud fournissant une valeur (un
	* objet ServiceTreeObjectNode).
	*
	* Retourne: Le nom du I-CLES.
	* ----------------------------------------------------------*/
	public String getIClesName()
	{
		String icles_name = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "GenericTreeObjectNode", "getIClesName");

		trace_methods.beginningOfMethod();
		// Récupération du noeud père
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remontée des appels jusqu'au noeud racine (pas de père)
		if(parent_node != null)
		{
			icles_name = parent_node.getIClesName();
		}
		trace_methods.endOfMethod();
		return icles_name;
	}

	/*----------------------------------------------------------
	* Nom: getAgentSessionId
	* 
	* Description:
	* Cette méthode permet de récupérer l'identifiant de la session Agent à 
	* partir de laquelle le noeud a été créé.
	* Cette méthode est récursive. C'est à dire qu'elle remonte dans 
	* l'arborescence jusqu'à arriver à un noeud fournissant une valeur (un 
	* objet ServiceTreeObjectNode).
	* 
	* Retourne: L'identifiant de la session Agent.
	* ----------------------------------------------------------*/
	public String getAgentSessionId()
	{
		String agent_session_id = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getAgentSessionId");

		trace_methods.beginningOfMethod();
		// Récupération du noeud père
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remontée des appels jusqu'au noeud racine (pas de père)
		if(parent_node != null)
		{
			agent_session_id = parent_node.getAgentSessionId();
		}
		trace_methods.endOfMethod();
		return agent_session_id;
	}

	/*----------------------------------------------------------
	* Nom: getServiceSessionId
	* 
	* Description:
	* Cette méthode permet de récupérer l'identifiant de la session de service 
	* à partir de laquelle le noeud a été créé.
	* Cette méthode est récursive. C'est à dire qu'elle remonte dans 
	* l'arborescence jusqu'à arriver à un noeud fournissant une valeur (un 
	* objet ServiceTreeObjectNode).
	* 
	* Retourne: L'identifiant de la session de service.
	* ----------------------------------------------------------*/
	public String getServiceSessionId()
	{
		String service_session_id = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getServiceSessionId");

		trace_methods.beginningOfMethod();
		// Récupération du noeud père
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remontée des appels jusqu'au noeud racine (pas de père)
		if(parent_node != null)
		{
			service_session_id = parent_node.getServiceSessionId();
		}
		trace_methods.endOfMethod();
		return service_session_id;
	}

	/*----------------------------------------------------------
	* Nom: forwardEvent
	* 
	* Description:
	* Cette méthode permet de transmettre, et éventuellement de traiter, un 
	* événement I-SIS reçu par la Console. Seuls les événements concernant 
	* l'arrêt d'un Agent ou la fermeture d'une session (Agent ou de service) 
	* doivent être transmis par ce biais.
	* Si le noeud est capable de traiter l'événement, elle le fait et sort. 
	* Sinon, l'événement est retransmis à tous les noeuds enfants du noeud 
	* courant.
	* 
	* Arguments:
	*  - eventType: Le type de l'événement I-SIS,
	*  - eventInformation: Un tableau de chaînes de caractères contenant les 
	*    informations sur l'événement,
	*  - treeInterface: Une référence sur l'interface TreeInterface.
	* 
	* Retourne: true si l'événement a été traité, false sinon.
	* ----------------------------------------------------------*/
	public boolean forwardEvent(
		IsisEventTypeEnum eventType,
		String[] eventInformation,
		TreeInterface treeInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "forwardEvent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		boolean event_processed = false;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("eventType=" + eventType);
		trace_arguments.writeTrace("eventInformation=" + eventInformation);
		// Si l'état courant est la destruction, on sort
		if(getNodeState() == NodeStateEnum.DESTROYING)
		{
			trace_methods.endOfMethod();
			return false;
		}
		// Tout d'abord, on vérifie que l'événement est du bon type
		if(eventType != IsisEventTypeEnum.A_SESSION_CLOSED &&
			eventType != IsisEventTypeEnum.S_SESSION_CLOSED &&
			eventType != IsisEventTypeEnum.AGENT_STOPPED ||
			treeInterface == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Cet événement n'est pas traité !");
			// On sort
			trace_methods.endOfMethod();
			return event_processed;
		}
		// On va retransmettre l'événement à tous les enfants seulement si
		// le noeud est exploré ou en cours d'exploration
		int node_state = getNodeState();
		if(node_state != NodeStateEnum.OPENED && 
			node_state != NodeStateEnum.STATE_CHANGING)
		{
			trace_debug.writeTrace("Le noeud n'est pas dans l'état" +
				" 'exploré' ou 'en exploration'");
			trace_methods.endOfMethod();
			return false;
		}
		for(int index = 0 ; index < getChildCount() ; index ++)
		{
			GenericTreeObjectNode child_node = 
				(GenericTreeObjectNode)getChildAt(index);
			event_processed |= child_node.forwardEvent(eventType, 
				eventInformation, treeInterface);
		}
		trace_methods.endOfMethod();
		return event_processed;
	}

	/*----------------------------------------------------------
	* Nom: getNodeState
	* 
	* Description:
	* Cette méthode permet de connaître l'état dans lequel se trouve le noeud 
	* au moment précis de l'appel. L'état du noeud dépend de son état propre, 
	* et éventuellement de l'état de ses noeuds enfants s'il y en a.
	* Si le noeud est dans l'état CLOSED, STATE_CHANGING ou DESTROYING, son 
	* état est directement retourné.
	* Si le noeud est dans l'état OPENED, l'état des noeuds enfants est 
	* récupéré. Si un des noeuds enfants est dans l'état STATE_CHANGING ou 
	* DESTROYING, l'état du noeud courant sera CHILD_CHANGING. Dans les autres 
	* cas, son état sera OPENED.
	* 
	* Retourne: L'état du noeud.
	* ----------------------------------------------------------*/
	public int getNodeState()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getNodeState");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int node_state = _nodeState;
		
		trace_methods.beginningOfMethod();
		trace_debug.writeTrace("_nodeState=" + _nodeState);
		// On vérifie l'état du noeud
		if(_nodeState != NodeStateEnum.OPENED)
		{
			// Le noeud est en cours de changement d'état, il est fermé,
			// ou il est en cours de destruction, il n'y a pas besoin de
			// chercher plus loin
			trace_methods.endOfMethod();
			return _nodeState; 
		}
		// On va aller interroger tous les enfants pour connaître leur
		// état
		for(int index = 0 ; index < getChildCount() ; index ++)
		{
			GenericTreeObjectNode child =
				(GenericTreeObjectNode)getChildAt(index);
			int child_state = child.getNodeState();
			trace_debug.writeTrace("Etat du noeud enfant n°" + index +
				": " + child_state);
			// Si le noeud enfant est STATE_CHANGING ou DESTROYING, on
			// retourne CHILD_CHANGING, sinon on continue
			if(child_state == NodeStateEnum.STATE_CHANGING ||
				child_state == NodeStateEnum.DESTROYING)
			{
				trace_methods.endOfMethod();
				return NodeStateEnum.CHILD_CHANGING; 
			}
		}
		trace_methods.endOfMethod();
		return _nodeState;
	}
	
	/*----------------------------------------------------------
	* Nom: setNodeState
	* 
	* Description:
	* Cette méthode permet de positionner l'état dans lequel se trouve le 
	* noeud.
	* 
	* Arguments:
	*  - nodeState: L'état du noeud.
 	* ----------------------------------------------------------*/
 	public void setNodeState(
 		int nodeState
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "setNodeState");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("nodeState=" + nodeState);
		// Certains états sont prédomminants sur les autres
		// Si le noeud était en cours de destruction, aucun nouvel état n'est
		// possible
		if(_nodeState == NodeStateEnum.DESTROYING)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On ne peut positionner CLOSED ou OPENED que si le noeud est en
		// cours de changement d'état
		if(_nodeState != NodeStateEnum.STATE_CHANGING && 
			(nodeState == NodeStateEnum.CLOSED && 
			nodeState == NodeStateEnum.OPENED))
		{
			trace_methods.endOfMethod();
			return;
		}
		// Pour le reste, c'est bon
		_nodeState = nodeState;
		trace_methods.endOfMethod();
	}
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _definitionFilePath
	*
	* Description:
	* Cet attribut contient le chemin complet d'accès au fichier dictionnaire
	* de la table d'où a été extrait l'objet de production sur la plate-forme
	* concernée. Il permet de récupérer la définition de la table (voir la
	* classe TableDefinitionManager).
	* ----------------------------------------------------------*/
	private String _definitionFilePath;

	/*----------------------------------------------------------
	* Nom: _key
	*
	* Description:
	* Cet attribut contient la chaîne représentant la valeur de la clé de la
	* table, d'où ont été extraites les données de cet objet, pour cet objet.
	* ----------------------------------------------------------*/
	private String _key;

	/*----------------------------------------------------------
	* Nom: _tableName
	*
	* Description:
	* Cet attribut contient le nom de la table à laquelle correspond le noeud.
	* ----------------------------------------------------------*/
	private String _tableName;

	/*----------------------------------------------------------
	* Nom: _objectParameters
	*
	* Description:
	* Cet attribut maintient une référence sur un tableau
	* d'IsisParameter contenant toutes les données extraites du résultat
	* d'un Select correspondant au noeud.
	* ----------------------------------------------------------*/
	private IsisParameter[] _objectParameters;

	/*----------------------------------------------------------
	* Nom: _menu
	*
	* Description:
	* Cet attribut maintient une référence sur un objet JMenu correspondant au
	* menu contextuel du noeud graphique.
	* Les méthodes d'un noeud graphique étant associées à une table et stockées
	* sur les Agents, celles-ci ne peuvent raisonnablement être récupérées à
	* chaque fois qu'un menu contextuel doit être affiché, cela prendrait trop
	* de temps. Cet attribut permet d'obtenir une sorte de cache de menus.
	* ----------------------------------------------------------*/
	private JMenu _menu;

	/*----------------------------------------------------------
	* Nom: _label
	*
	* Description:
	* Cet attribut maintient une référence sur un objet IsisNodeLabel associé
	* au noeud graphique. Cet objet contient le libellé du noeud graphique,
	* ainsi que le nom de l'icône à afficher avec le noeud.
	* ----------------------------------------------------------*/
	private IsisNodeLabel _label;

	/*----------------------------------------------------------
	* Nom: _preprocessingData
	*
	* Description:
	* Cet attribut maintient une référence sur un tableau
	* d'IsisParameter contenant toutes les données extraites du
	* traitement des données de préprocessing de la méthode ayant construit le
	* noeud.
	* ----------------------------------------------------------*/
	private IsisParameter[] _preprocessingData;

	/*----------------------------------------------------------
	* Nom: _nodeState
	* 
	* Description:
	* Cet attribut maintient est un énuméré de type NodeStateEnum permettant 
	* de définir l'état courant du noeud.
	* ----------------------------------------------------------*/
	private int _nodeState;

	/*----------------------------------------------------------
	* Nom: destroyContextualMenu
	* 
	* Description:
	* Cette méthode est chargée de détruire le menu contextuel passé en 
	* argument. Pour chaque élément de type ContextualMenuItem, 
	* on va appeler la méthode closeAttachedProcessor().
	* 
	* Arguments:
	*  - menu: Le menu à détruire.
	* ----------------------------------------------------------*/
	private void destroyContextualMenu(
		JMenu menu
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "destroyContextualMenu");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menu=" + menu);
		if(menu == null)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On va traiter élément par élément
		for(int index = 0 ; index < menu.getItemCount() ; index ++)
		{
			JMenuItem menu_item = menu.getItem(index);
			// L'item est-il de type ContextualMenuItem ?
			if(menu_item instanceof ContextualMenuItem)
			{
				trace_debug.writeTrace("Fermeture de l'élément de menu: " +
					menu_item.getText() + "(" + menu_item.getClass().getName() +
					")");
				// On appelle la méthode closeAttachedProcessor() dessus
				((ContextualMenuItem)menu_item).closeAttachedProcessor();
			}
			// L'item est-il de type JMenu ?
			else if(menu_item instanceof JMenu)
			{
				// On va appeler la méthode destroyContextualMenu avec le menu
				// en argument
				destroyContextualMenu((JMenu)menu_item);
			}
		}
		// On retire tous les éléments
		menu.removeAll();
		trace_methods.endOfMethod();
	}
}