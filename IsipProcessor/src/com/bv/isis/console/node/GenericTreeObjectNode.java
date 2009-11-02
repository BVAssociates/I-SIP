/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/GenericTreeObjectNode.java,v $
* $Revision: 1.22 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de repr�sentation d'un noeud instance
* DATE:        04/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: GenericTreeObjectNode.java,v $
* Revision 1.22  2009/01/23 17:28:41  tz
* Pas de setParent(null) dans la m�thode destroy().
*
* Revision 1.21  2009/01/14 14:22:25  tz
* Prise en compte de la modification des packages.
*
* Revision 1.20  2008/08/11 10:46:52  tz
* Prise en compte du cas du noeud racine.
*
* Revision 1.19  2006/03/07 09:28:34  tz
* Modification des �tats des noeuds
*
* Revision 1.18  2005/10/07 11:25:45  tz
* Changement mineur.
*
* Revision 1.17  2005/10/07 08:19:14  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.16  2005/07/01 12:08:08  tz
* Modification du composant pour les traces
*
* Revision 1.15  2004/11/09 15:22:20  tz
* D�placement de la mise � null du menu dans la m�thode destroy().
*
* Revision 1.14  2004/11/02 08:50:32  tz
* Gestion de l'�tat du noeud,
* Gestion des leasings sur les d�finitions.
*
* Revision 1.13  2004/10/22 15:36:26  tz
* Ajout des m�thodes getAgentSessionId(), getServiceSessionId() et
* forwardEvent() et destroyContextualMenu().
*
* Revision 1.12  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.11  2004/10/13 13:55:09  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.10  2004/07/29 12:01:50  tz
* Traitement de la fermeture et de la destruction sur arr�t du Portail
*
* Revision 1.9  2003/03/07 16:19:56  tz
* Prise en compte du m�canisme de log m�tier
* Ajout de l'auto-exploration
*
* Revision 1.8  2002/11/19 08:39:59  tz
* Suppression vieux code.
*
* Revision 1.7  2002/09/20 10:47:33  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.6  2002/08/13 12:59:45  tz
* Suppression de l'identifiant de la m�thode d'exploration lors de la fermeture du noeud.
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
* Cette classe correspond � un noeud graphique repr�sentant une instance
* d'objet de production extrait d'une table (voir la m�thode getSelectResult()
* de la classe ServiceSessionInterface).
* Elle d�rive de la classe DefaultMutableTreeNode afin de ne pas avoir �
* red�finir une gestion des relations hi�rarchiques (p�re et enfants). Cette
* classe ne fait que la sp�cialiser dans le contexte d'I-SIS.
*
* Elle permet de g�rer les param�tres des noeuds (provenant de la ligne de
* r�sultat de Select correspondante), ainsi que le contexte (agr�gation des
* param�tres "exportables" des noeuds parents et de tous les param�tres du
* noeud lui-m�me).
* ----------------------------------------------------------*/
public class GenericTreeObjectNode
	extends DefaultMutableTreeNode
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NodeStateEnum
	* 
	* Description:
	* Cet �num�r� permet de d�finir les diff�rents �tats du noeud.
	* Un noeud peut �tre dans l'�tat:
	*  - CLOSED: Lorsqu'il est ferm� ou n'a jamais �t� explor�,
	*  - STATE_CHANGING: Lorsqu'il est en cours d'exploration ou de fermeture,
	*  - OPENED: Lorsqu'il a �t� explor�,
	*  - DESTROYING: Lorsqu'il est en cours de destruction,
	*  - CHILD_CHANGING: Lorsqu'un noeud enfant est en cours de changement 
	*    d'�tat.
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
	* Cette m�thode est le seul constructeur de la classe. Elle permet de
	* d�finir la valeur de la cl�, le chemin du dictionnaire de la table, le
	* nom de la table ainsi que les param�tres r�sultant du Select.
	*
	* Arguments:
	*  - objectParameters: Un tableau d'IsisParameter contenant toutes
	*    les donn�es correspondant � une ligne de r�sultat de Select,
	*  - key: Une cha�ne contenant la valeur de la cl� pour cet objet,
	*  - agentName: Le nom de l'Agent � partir duquel l'objet a �t� construit,
	*  - iClesName: Le nom du I-CLES auquel est apparent� le noeud,
	*  - serviceType: Le type du service auquel est apparent� le noeud,
	*  - definitionFilePath: Le chemin complet d'acc�s au fichier dictionnaire
	*    de la table d'o� a �t� extrait l'objet,
	*  - tableName: Le nom de la table d'o� ont �t� extraites les informations.
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
		// On va forcer l'utilisation d'un leasing sur la d�finition
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		manager.getTableDefinition(agentName, iClesName,
			serviceType, _definitionFilePath);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getObjectParameters
	*
	* Description:
	* Cette m�thode permet de r�cup�rer la liste des IsisParameter
	* correspondants au noeud graphique (tels qu'ils ont �t� fournis lors de la
	* construction du noeud).
	*
	* Retourne: Un tableau d'IsisParameter contenant tous les param�tres
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
	* Cette m�thode permet de r�cup�rer le nom de la table d'o� a �t� extraites
	* les donn�es correspindants au noeud graphique.
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
	* Cette m�thode permet de r�cup�rer la valeur de la cl� de l'objet. Cette
	* cl� permet d'identifier de mani�re unique un objet dans une liste
	* d'objets provenant de la m�me table.
	*
	* Retourne: La cl� de l'objet, sous forme de cha�ne de caract�res.
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
	* Cette m�thode permet de r�cup�rer une liste index�e contenant le contexte
	* de l'objet concern�. Ce contexte correspond � l'ensemble des param�tres
	* "exportables" (qui constituent la cl�) des noeuds travers�s pour
	* atteindre le noeud concern�. A cet ensemble sont ajout�s tous les
	* param�tres du noeud lui-m�me.
	* L'argument permet d'indiquer au noeud s'il est le noeud concern� ou non.
	* Suivant sa valeur, soit seuls les param�tres "exportables", soit tous les
	* param�tres, sont ajout�s � la liste.
	* S'il s'agit du noeud concern�, des param�tres correspondants � la cl� et
	* au nom de la table sont ajout�s � la liste (les noms de ces
	* param�tres sont r�cup�r�s depuis le fichier de configuration, dans la
	* section "I-SIS", respectivement via les param�tres "KeyParameterName" et
	* "TableParameterName").
	*
	* Arguments:
	*  - isFirstObject: Un bool�en indiquant si le noeud est le noeud concern�
	*    (true) ou non (false).
	*
	* Retourne: Une liste index�e contenant le contexte du noeud concern�.
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
		// R�cup�ration du noeud p�re
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remont�e des appels jusqu'au noeud racine (pas de p�re)
		if(parent_node != null)
		{
			context_table = parent_node.getContext(false);
		}
		else
		{
			trace_debug.writeTrace("Noeud racine atteint !");
			// Il s'agit du noeud racine, cr�ation de la table de hash
			context_table = new IndexedList();
		}
		// On regarde s'il s'agit du premier noeud
		if(isFirstObject == false)
		{
			// Ce n'est pas le premier objet, on ne r�cup�re que les param�tres
			// "exportables"
			trace_debug.writeTrace("R�cup�ration des param�tres exportables");
			parameters = getExportableParameters();
		}
		else
		{
			// C'est le premier objet, on r�cup�re tous les param�tres
			trace_debug.writeTrace("R�cup�ration de tous les param�tres");
			parameters = getObjectParameters();
		}
		// On ajoute tous les param�tres � la table de hash
		trace_debug.writeTrace("parameters=" + parameters);
		if(parameters != null)
		{
			for(int index = 0 ; index < parameters.length ; index ++)
			{
				context_table.put(parameters[index].name, parameters[index]);
			}
		}
		// On ajoute les param�tres de pr�processing
		parameters = getPreprocessingData();
		if(parameters != null)
		{
			for(int index = 0 ; index < parameters.length ; index ++)
			{
				context_table.put(parameters[index].name, parameters[index]);
			}
		}
		// S'il s'agit du premier objet, il faut �galement ajouter des
		// param�tres relatifs � la cl� et au nom de la table.
		if(isFirstObject == true)
		{
			String key_parameter_name = "KEY";
			String table_parameter_name = "TABLE";

			// R�cup�ration des noms des param�tres depuis le fichier de
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
					"Erreurs lors de la r�cup�ration de param�tres: " +
					exception);
			}
			// Construction et ajout des param�tres additionnels
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
	* Cette m�thode permet de r�cup�rer les param�tres du noeud graphique qui
	* sont "exportables" (contrairement � la m�thode getObjectParameters() qui
	* retourne tous les param�tres).
	* Un param�tre exportable est un param�tre correspondant � une colonne de
	* la d�finition de la table constituant la cl�. Ces param�tres
	* correspondent � des variables devant �tre h�rit�es tout au long de la
	* navigation.
	*
	* Retourne: Un tableau d'IsisParameter contenant l'ensemble des
	* param�tres exportables de l'objet.
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
		// La premi�re chose � faire est de r�cup�rer les param�tres
		parameters = getObjectParameters();
		if(parameters == null || parameters.length == 0)
		{
			trace_debug.writeTrace("Il n'y a pas de param�tres !");
			trace_methods.endOfMethod();
			return null;
		}
		// Si le chemin du fichier de d�finition est vide, c'est qu'il 
		// n'y a pas de d�finition, on sort
		if(definition_path == null || definition_path.equals("") == true) {
			trace_methods.endOfMethod();
			return null;
		}
		// La deuxi�me chose � faire est de r�cup�rer la d�finition de la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IsisTableDefinition table_definition = manager.getTableDefinition(
			getAgentName(), getIClesName(), getServiceType(),
			getDefinitionFilePath());
		if(table_definition == null)
		{
			// Il n'y a pas de d�finition de table, c'est une erreur
			trace_errors.writeTrace("La d�finition de la table " +
				getDefinitionFilePath() + " n'est pas charg�e!");
			// On sort
			trace_methods.endOfMethod();
			return null;
		}
		// On va examiner la cl� afin de d�terminer quelles colonnes doivent
		// �tre "export�es".
		for(int index = 0 ; index < table_definition.key.length ; index ++)
		{
			// R�cup�ration du nom du champ de la cl� correspondant � l'index
			String field = table_definition.key[index];
			// On r�cup�re le param�tre de m�me nom dans la table des param�tres
			for(int count = 0 ; count < parameters.length ; count ++)
			{
				if(parameters[count].name.equals(field) == true)
				{
				    exportable_parameters.add(parameters[count]);
					break;
				}
			}
		}
		// On lib�re l'utilisation de la d�finition
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
	* Cette m�thode permet de fermer un objet graphique, et par cons�quent une
	* portion de l'arbre d'exploration partant de cet objet. La fermeture d'un
	* noeud implique que le noeud doit �tre de nouveau explor� lors de son
	* expansion.
	* Cette m�thode va supprimer tous ses noeuds fils et appeler leur m�thode
	* destroy(), afin de supprimer toute l'arborescence descendante.
	* 
	* Arguments:
	*  - portalStopped: Un bool�en indiquant si la fermeture est caus�e par
	*    l'arr�t du Portail (true) ou non (false).
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
		// On positionne l'�tat
		setNodeState(NodeStateEnum.STATE_CHANGING);
		// Pour chaque noeud fils, destruction de celui-ci
		while(getChildCount() > 0)
		{
			// R�cup�ration du noeud fils
			GenericTreeObjectNode child_node =
				(GenericTreeObjectNode)getChildAt(0);
			// On le d�truit
			child_node.destroy(portalStopped);
			// On supprime le noeud fils
			remove(0);
			// On retire l'identifiant de la m�thode d'exploration
			setUserObject(null);
		}
		// On positionne l'�tat
		setNodeState(NodeStateEnum.CLOSED);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: destroy
	*
	* Description:
	* Cette m�thode permet de d�truire un noeud graphique, ainsi que toute
	* l'arboresence descendante en d�pendant. Elle lib�re toutes les ressources
	* allou�es par l'objet et appelle la m�thode destroy() sur tous ses
	* descendants, avant de les supprimer.
	* 
	* Arguments:
	*  - portalStopped: Un bool�en indiquant si la destruction est caus�e par
	*    l'arr�t du Portail (true) ou non (false).
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
		// On positionne l'�tat
		setNodeState(NodeStateEnum.DESTROYING);
		// On lib�re une utilisation de la d�finition de la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		if(_definitionFilePath != null)
		{
			manager.releaseTableDefinitionLeasing(getAgentName(), 
				getIClesName(), getServiceType(), _definitionFilePath);
		}
		// On commence par fermer le noeud (et destruction de tous les
		// descendants).
		close(portalStopped);
		// On lib�re les ressources
		_definitionFilePath = null;
		_key = null;
		_objectParameters = null;
		_tableName = null;
		_label = null;
		_preprocessingData = null;
		// On va d�clencher la destruction du menu contextuel
		destroyContextualMenu(_menu);
		_menu = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getAgentName
	*
	* Description:
	* Cette m�thode permet de r�cup�rer le nom de l'Agent d'o� sont extraites
	* les informations ayant servi � la construction du noeud graphique.
	* Cette m�thode est r�cursive. C'est � dire qu'elle remonte dans
	* l'arborescence jusqu'� arriver � un noeud fournissant une valeur (un
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
		// R�cup�ration du noeud p�re
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remont�e des appels jusqu'au noeud racine (pas de p�re)
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
	* Cette m�thode permet de r�cup�rer le chemin complet d'acc�s au fichier
	* dictionnaire de la table d'o� a �t� extrait l'objet correspondant au
	* noeud graphique.
	* Ce chemin est n�cessaire pour r�cup�rer la d�finition de la table (voir
	* la m�thode getTableDefinition() de la classe TableDefinitionManager).
	*
	* Retourne: Le chemin complet d'acc�s au fichier dictionnaire de la table
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
	* Cette m�thode permet de fournir un libell� au noeud graphique. Ce libell�
	* sera utilis� lors de l'affichage du noeud dans l'arbre d'exploration.
	*
	* Arguments:
	*  - label: Le nouveau libell� du noeud graphique.
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
	* Cette m�thode permet de fixer l'objet IsisNodeLabel associ� au noeud
	* graphique. Cet objet contient le libell� du noeud � afficher, ainsi que
	* le nom de l'ic�ne � utiliser pour l'affichage.
	*
	* Retourne: L'objet IsisNodeLabel associ� au noeud.
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
	* Cette m�thode permet de r�cup�rer la r�f�rence sur l'interface
	* ServiceSessionInterface � travers laquelle toute requ�te doit �tre
	* transmise.
	* Cette m�thode est r�cursive. C'est � dire qu'elle remonte dans
	* l'arborescence jusqu'� arriver � un noeud fournissant une valeur (un objet
	* ServiceTreeObjectNode).
	*
	* Retourne: Une r�f�rence sur une interface ServiceSessionInterface, ou
	* null.
	* ----------------------------------------------------------*/
	public ServiceSessionInterface getServiceSession()
	{
		ServiceSessionInterface service_session = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getServiceSession");

		trace_methods.beginningOfMethod();
		// R�cup�ration du noeud p�re
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remont�e des appels jusqu'au noeud racine (pas de p�re)
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
	* Cette m�thode permet de r�cup�rer le menu contextuel associ� au noeud
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
	* Cette m�thode permet de positionner le menu contextuel associ� au noeud
	* graphique.
	*
	* Arguments:
	*  - menu: Une r�f�rence sur un JMenu contenant le menu contextuel du noeud
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
	* Cette m�thode permet de fournir au noeud un ensemble de param�tres qui
	* sont issus du traitement des donn�es de pr�processing sp�cifi�e dans la
	* m�thode qui a construit le noeud.
	*
	* Arguments:
	*  - preprocessingData: Un tableau d'IsisParameter contenant les
	*    param�tres de pr�processing.
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
	* Cette m�thode permet de r�cup�rer les param�tres de pr�processing qui ont
	* �t� associ�s au noeud (par la m�thode setPreprocessingData()).
	*
	* Retourne: Un tableau d'IsisParameter contenant les param�tres de
	* pr�processing.
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
	* Cette m�thode permet de r�cup�rer l'identification du noeud sous forme
	* de cha�ne de caract�res. Elle est compos�e du nom de la classe, du
	* caract�re '@' et du hash-code du noeud.
	*
	* Retourne: L'identification du noeud sous forme de cha�ne de caract�res.
	* ----------------------------------------------------------*/
	public String toString()
	{
		return getClass().getName() + "@" + hashCode();
	}

	/*----------------------------------------------------------
	* Nom: getServiceName
	*
	* Description:
	* Cette m�thode permet de r�cup�rer le nom du service � partir duquel
	* le noeud a �t� cr��.
	* Cette m�thode est r�cursive. C'est � dire qu'elle remonte dans
	* l'arborescence jusqu'� arriver � un noeud fournissant une valeur (un
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
		// R�cup�ration du noeud p�re
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remont�e des appels jusqu'au noeud racine (pas de p�re)
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
	* Cette m�thode permet de r�cup�rer le type du service � partir duquel
	* le noeud a �t� cr��.
	* Cette m�thode est r�cursive. C'est � dire qu'elle remonte dans
	* l'arborescence jusqu'� arriver � un noeud fournissant une valeur (un
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
		// R�cup�ration du noeud p�re
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remont�e des appels jusqu'au noeud racine (pas de p�re)
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
	* Cette m�thode permet de r�cup�rer le nom du I-CLES � partir duquel
	* le noeud a �t� cr��.
	* Cette m�thode est r�cursive. C'est � dire qu'elle remonte dans
	* l'arborescence jusqu'� arriver � un noeud fournissant une valeur (un
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
		// R�cup�ration du noeud p�re
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remont�e des appels jusqu'au noeud racine (pas de p�re)
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
	* Cette m�thode permet de r�cup�rer l'identifiant de la session Agent � 
	* partir de laquelle le noeud a �t� cr��.
	* Cette m�thode est r�cursive. C'est � dire qu'elle remonte dans 
	* l'arborescence jusqu'� arriver � un noeud fournissant une valeur (un 
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
		// R�cup�ration du noeud p�re
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remont�e des appels jusqu'au noeud racine (pas de p�re)
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
	* Cette m�thode permet de r�cup�rer l'identifiant de la session de service 
	* � partir de laquelle le noeud a �t� cr��.
	* Cette m�thode est r�cursive. C'est � dire qu'elle remonte dans 
	* l'arborescence jusqu'� arriver � un noeud fournissant une valeur (un 
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
		// R�cup�ration du noeud p�re
		GenericTreeObjectNode parent_node = (GenericTreeObjectNode)getParent();
		// Remont�e des appels jusqu'au noeud racine (pas de p�re)
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
	* Cette m�thode permet de transmettre, et �ventuellement de traiter, un 
	* �v�nement I-SIS re�u par la Console. Seuls les �v�nements concernant 
	* l'arr�t d'un Agent ou la fermeture d'une session (Agent ou de service) 
	* doivent �tre transmis par ce biais.
	* Si le noeud est capable de traiter l'�v�nement, elle le fait et sort. 
	* Sinon, l'�v�nement est retransmis � tous les noeuds enfants du noeud 
	* courant.
	* 
	* Arguments:
	*  - eventType: Le type de l'�v�nement I-SIS,
	*  - eventInformation: Un tableau de cha�nes de caract�res contenant les 
	*    informations sur l'�v�nement,
	*  - treeInterface: Une r�f�rence sur l'interface TreeInterface.
	* 
	* Retourne: true si l'�v�nement a �t� trait�, false sinon.
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
		// Si l'�tat courant est la destruction, on sort
		if(getNodeState() == NodeStateEnum.DESTROYING)
		{
			trace_methods.endOfMethod();
			return false;
		}
		// Tout d'abord, on v�rifie que l'�v�nement est du bon type
		if(eventType != IsisEventTypeEnum.A_SESSION_CLOSED &&
			eventType != IsisEventTypeEnum.S_SESSION_CLOSED &&
			eventType != IsisEventTypeEnum.AGENT_STOPPED ||
			treeInterface == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Cet �v�nement n'est pas trait� !");
			// On sort
			trace_methods.endOfMethod();
			return event_processed;
		}
		// On va retransmettre l'�v�nement � tous les enfants seulement si
		// le noeud est explor� ou en cours d'exploration
		int node_state = getNodeState();
		if(node_state != NodeStateEnum.OPENED && 
			node_state != NodeStateEnum.STATE_CHANGING)
		{
			trace_debug.writeTrace("Le noeud n'est pas dans l'�tat" +
				" 'explor�' ou 'en exploration'");
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
	* Cette m�thode permet de conna�tre l'�tat dans lequel se trouve le noeud 
	* au moment pr�cis de l'appel. L'�tat du noeud d�pend de son �tat propre, 
	* et �ventuellement de l'�tat de ses noeuds enfants s'il y en a.
	* Si le noeud est dans l'�tat CLOSED, STATE_CHANGING ou DESTROYING, son 
	* �tat est directement retourn�.
	* Si le noeud est dans l'�tat OPENED, l'�tat des noeuds enfants est 
	* r�cup�r�. Si un des noeuds enfants est dans l'�tat STATE_CHANGING ou 
	* DESTROYING, l'�tat du noeud courant sera CHILD_CHANGING. Dans les autres 
	* cas, son �tat sera OPENED.
	* 
	* Retourne: L'�tat du noeud.
	* ----------------------------------------------------------*/
	public int getNodeState()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"GenericTreeObjectNode", "getNodeState");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int node_state = _nodeState;
		
		trace_methods.beginningOfMethod();
		trace_debug.writeTrace("_nodeState=" + _nodeState);
		// On v�rifie l'�tat du noeud
		if(_nodeState != NodeStateEnum.OPENED)
		{
			// Le noeud est en cours de changement d'�tat, il est ferm�,
			// ou il est en cours de destruction, il n'y a pas besoin de
			// chercher plus loin
			trace_methods.endOfMethod();
			return _nodeState; 
		}
		// On va aller interroger tous les enfants pour conna�tre leur
		// �tat
		for(int index = 0 ; index < getChildCount() ; index ++)
		{
			GenericTreeObjectNode child =
				(GenericTreeObjectNode)getChildAt(index);
			int child_state = child.getNodeState();
			trace_debug.writeTrace("Etat du noeud enfant n�" + index +
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
	* Cette m�thode permet de positionner l'�tat dans lequel se trouve le 
	* noeud.
	* 
	* Arguments:
	*  - nodeState: L'�tat du noeud.
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
		// Certains �tats sont pr�domminants sur les autres
		// Si le noeud �tait en cours de destruction, aucun nouvel �tat n'est
		// possible
		if(_nodeState == NodeStateEnum.DESTROYING)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On ne peut positionner CLOSED ou OPENED que si le noeud est en
		// cours de changement d'�tat
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
	* Cet attribut contient le chemin complet d'acc�s au fichier dictionnaire
	* de la table d'o� a �t� extrait l'objet de production sur la plate-forme
	* concern�e. Il permet de r�cup�rer la d�finition de la table (voir la
	* classe TableDefinitionManager).
	* ----------------------------------------------------------*/
	private String _definitionFilePath;

	/*----------------------------------------------------------
	* Nom: _key
	*
	* Description:
	* Cet attribut contient la cha�ne repr�sentant la valeur de la cl� de la
	* table, d'o� ont �t� extraites les donn�es de cet objet, pour cet objet.
	* ----------------------------------------------------------*/
	private String _key;

	/*----------------------------------------------------------
	* Nom: _tableName
	*
	* Description:
	* Cet attribut contient le nom de la table � laquelle correspond le noeud.
	* ----------------------------------------------------------*/
	private String _tableName;

	/*----------------------------------------------------------
	* Nom: _objectParameters
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un tableau
	* d'IsisParameter contenant toutes les donn�es extraites du r�sultat
	* d'un Select correspondant au noeud.
	* ----------------------------------------------------------*/
	private IsisParameter[] _objectParameters;

	/*----------------------------------------------------------
	* Nom: _menu
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JMenu correspondant au
	* menu contextuel du noeud graphique.
	* Les m�thodes d'un noeud graphique �tant associ�es � une table et stock�es
	* sur les Agents, celles-ci ne peuvent raisonnablement �tre r�cup�r�es �
	* chaque fois qu'un menu contextuel doit �tre affich�, cela prendrait trop
	* de temps. Cet attribut permet d'obtenir une sorte de cache de menus.
	* ----------------------------------------------------------*/
	private JMenu _menu;

	/*----------------------------------------------------------
	* Nom: _label
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet IsisNodeLabel associ�
	* au noeud graphique. Cet objet contient le libell� du noeud graphique,
	* ainsi que le nom de l'ic�ne � afficher avec le noeud.
	* ----------------------------------------------------------*/
	private IsisNodeLabel _label;

	/*----------------------------------------------------------
	* Nom: _preprocessingData
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un tableau
	* d'IsisParameter contenant toutes les donn�es extraites du
	* traitement des donn�es de pr�processing de la m�thode ayant construit le
	* noeud.
	* ----------------------------------------------------------*/
	private IsisParameter[] _preprocessingData;

	/*----------------------------------------------------------
	* Nom: _nodeState
	* 
	* Description:
	* Cet attribut maintient est un �num�r� de type NodeStateEnum permettant 
	* de d�finir l'�tat courant du noeud.
	* ----------------------------------------------------------*/
	private int _nodeState;

	/*----------------------------------------------------------
	* Nom: destroyContextualMenu
	* 
	* Description:
	* Cette m�thode est charg�e de d�truire le menu contextuel pass� en 
	* argument. Pour chaque �l�ment de type ContextualMenuItem, 
	* on va appeler la m�thode closeAttachedProcessor().
	* 
	* Arguments:
	*  - menu: Le menu � d�truire.
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
		// On va traiter �l�ment par �l�ment
		for(int index = 0 ; index < menu.getItemCount() ; index ++)
		{
			JMenuItem menu_item = menu.getItem(index);
			// L'item est-il de type ContextualMenuItem ?
			if(menu_item instanceof ContextualMenuItem)
			{
				trace_debug.writeTrace("Fermeture de l'�l�ment de menu: " +
					menu_item.getText() + "(" + menu_item.getClass().getName() +
					")");
				// On appelle la m�thode closeAttachedProcessor() dessus
				((ContextualMenuItem)menu_item).closeAttachedProcessor();
			}
			// L'item est-il de type JMenu ?
			else if(menu_item instanceof JMenu)
			{
				// On va appeler la m�thode destroyContextualMenu avec le menu
				// en argument
				destroyContextualMenu((JMenu)menu_item);
			}
		}
		// On retire tous les �l�ments
		menu.removeAll();
		trace_methods.endOfMethod();
	}
}