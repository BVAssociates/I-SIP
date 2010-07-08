/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/AdministrationProcessor.java,v $
* $Revision: 1.27 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'administration d'I-SIS
* DATE:        04/06/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: AdministrationProcessor.java,v $
* Revision 1.27  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.26  2009/01/08 15:25:36  tz
* Suppression de la méthode isTableCapable().
* Le processeur devient invocable depuis un tableau.
*
* Revision 1.25  2008/05/23 10:48:19  tz
* Prise en compte du type DB dans l'administration des tables.
*
* Revision 1.24  2008/02/21 12:06:00  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.23  2008/01/31 16:46:31  tz
* Classe PreprocessingHandler renommée.
* Ajout de l'argument postprocessing à la méthode run().
* Gestion du postprocessing.
*
* Revision 1.22  2008/01/08 17:36:28  tz
* Gestion du pré-processing pour le paramètre.
*
* Revision 1.21  2007/12/28 17:39:36  tz
* Prise en compte de la clé étrangère lors de la construction du
* tableau (filtrage des données affichées).
*
* Revision 1.20  2007/12/07 10:29:23  tz
* Mise à jour pour le nouveau processeur d'administration.
*
* Revision 1.19  2006/10/13 15:10:25  tz
* Rechargement des données dans tous les cas.
*
* Revision 1.18  2006/03/20 15:56:06  tz
* Administration d'une table différente de celle du noeud sélectionné,
* Administration des libellés (I-CLES et I-SIS),
* Ajout des méthodes getTheParameters() et getCellRendererForColumn().
*
* Revision 1.17  2006/03/08 15:59:40  tz
* Méthode getTheMainWindowInterface() non commentée.
*
* Revision 1.16  2006/03/08 14:08:36  tz
* Réintégration du processeur d'administration de toute table.
*
* Revision 1.15  2005/10/07 13:40:57  tz
* Affichage de la fenêtre popup pour la table AgentICles.
*
* Revision 1.14  2005/10/07 08:32:35  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.13  2005/07/01 12:22:57  tz
* Modification du composant pour les traces
* Utilisation de la fonctionnalité de rechargement de la super-classe
*
* Revision 1.12  2004/11/24 16:25:19  tz
* Ajout de la boîte de dialogue des zones.
*
* Revision 1.11  2004/11/23 15:44:05  tz
* Ajout de la boîte d'administration des zones de mappage.
*
* Revision 1.10  2004/11/02 08:58:08  tz
* Annulation de la boite de dialogue sur close().
*
* Revision 1.9  2004/10/22 15:40:41  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.8  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.7  2004/10/13 14:00:07  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.6  2004/07/29 12:18:33  tz
* Suppression d'imports inutiles
* Remplacement de Master* par Portal*
*
* Revision 1.5  2003/03/07 16:21:39  tz
* Prise en compte du mécanisme de log métier
*
* Revision 1.4  2002/11/22 15:30:57  tz
* Cloture IT1.0.7
* Ajout de l'administration des applications et de leurs composants
*
* Revision 1.3  2002/08/13 13:07:53  tz
* Ajout des boîtes de dialogue pour ICleAccess et ICleServices
*
* Revision 1.2  2002/06/27 14:13:04  tz
* Ajout des processeurs d'administration des I-CLE et des Agents
*
* Revision 1.1  2002/06/19 12:17:52  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.config.ConfigurationAPI;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.TableCellRenderer;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import com.bv.core.message.MessageManager;
import java.awt.Dimension;
import java.awt.Cursor;

//
// Imports du projet
//
import com.bv.isis.console.impl.processor.display.DisplayProcessor;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.display.TablePopupTrigger;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.ConditionFactory;
import com.bv.isis.console.node.LabelFactory;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisTableColumn;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.IconCellRenderer;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;

/*----------------------------------------------------------
* Nom: AdministrationProcessor
*
* Description:
* Cette classe est la classe façade du package d'administration d'I-SIS. Elle
* dérive de la classe DisplayProcessor afin de permettre une gestion d'un type
* de données par le biais d'un affichage sous forme de tableau.
* Elle redéfinit la méthode getPopupTrigger() de sorte à utiliser un objet de
* déclenchement de menu popup spécifique à l'administration d'I-SIS.
* Cette classe implémente également l'interface DialogCaller, afin de 
* permettre la récupération du nom de la table administrée.
* ----------------------------------------------------------*/
public class AdministrationProcessor
	extends DisplayProcessor
	implements DialogCallerInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: AdministrationProcessor
	*
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Elle n'est
	* présentée que pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
    public AdministrationProcessor()
    {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "AdministrationProcessor");

		trace_methods.beginningOfMethod();
		_currentDialog = null;
		trace_methods.endOfMethod();
    }

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe DisplayProcessor. Elle
	* permet d'initialiser et de démarrer le processeur de tâche. Le contenu des
	* informations qui seront affichées dans la table résultera d'une sélection
	* sans critère dans la table représentée par le noeud sélectionné. Par
	* conséquent, ce processeur est destiné à être utilisé par une méthode
	* de table uniquement.
	*
	* Si une erreur est détectée pendant la phase d'initialisation, l'exception
	* InnerException doit être levée.
	*
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface
	*    permettant au processeur de communiquer avec la fenêtre principale de
	*    l'application,
	*  - menuItem: Une référence sur l'option de menu qui a déclenché
	*    l'exécution du processeur de tâche,
	*  - parameters: Une chaîne de caractères contenant les paramètres
	*    d'exécution du processeur,
	*  - preprocessing: Une chaîne de caractères contenant les instructions de
	*    préprocessing,
	*  - postprocessing: Une chaîne de caractères contenant des instructions 
	*    de postprocessing,
	*  - selectedNode: Une référence sur l'objet graphique sur lequel le
	*    processeur doit exécuter son traitement.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public void run(
		MainWindowInterface windowInterface,
		JMenuItem menuItem,
		String parameters,
		String preprocessing,
		String postprocessing,
		DefaultMutableTreeNode selectedNode
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		IsisTableDefinition definition = null;
		String parent_parameters = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Tout d'abord, il faut vérifier l'intégrité des arguments
		if(windowInterface == null || selectedNode == null ||
			!(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On caste le noeud en GenericTreeObjectNode
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IndexedList context = selected_node.getContext(true);
		String agent_name = selected_node.getAgentName();
		IsisParameter[] preprocessing_parameters = 
			ProcessingHandler.handleProcessingStatement(preprocessing, 
			context, windowInterface, 
			AgentSessionManager.getInstance().getAgentLayerMode(agent_name), 
			selected_node.getServiceSession(), this, true);
		parameters = LabelFactory.evaluate(parameters, preprocessing_parameters,
			context, agent_name);
		_originalParameters = parameters;
		_postProcessing = postprocessing;
		if(parameters != null && parameters.equals("") == false)
		{
			String table_name = null;
			boolean restricted = false;
			int at_position = -1;
			
			// On regarde si les données à afficher sont restreintes
			at_position = parameters.indexOf("@");
			if(at_position > -1)
			{
				table_name = parameters.substring(0, at_position);
				// La restriction ne s'applique que sur les tables des libellés
				if(table_name.equals("IsisLabels") == true ||
					table_name.equals("ICleLabels") == true)
				{
					restricted = true;
				}
			}
			else
			{
				table_name = parameters;
			}
			// On récupère le dictionnaire de la table
			definition = manager.getTableDefinition(
				agent_name, selected_node.getIClesName(), 
				selected_node.getServiceType(), table_name, context, 
				selected_node.getServiceSession());
			setTableName(table_name);
			// On construit la liste des paramètres de la classe parent
			if(restricted == false)
			{
				String condition = "";
				// On doit aller fouiller la définition de la table à administrer
				// pour voir s'il y a une clé étrangère vers la table courante
				for(int index = 0 ; index < definition.foreignKeys.length ; 
					index ++) {
					if(definition.foreignKeys[index].foreignTableName.equals(
						selected_node.getTableName()) == true) {
						// On doit construire la condition inverse via la 
						// classe ConditionFactory
						condition = 
							ConditionFactory.getConditionFromForeignKey(
							agent_name, ConditionFactory.revertForeignKey(
							definition.foreignKeys[index], 
							selected_node.getTableName()), definition);
						break;
					}
				}
				parent_parameters = table_name + "@@" + condition + "@";
			}
			else
			{
				String class_object_type = "Table";
				String instance_object_type = "Instance";
				String node_id = "NodeId";
				StringBuffer condition = new StringBuffer();

				// Tout d'abord, il faut récupérer les types de noeuds
				try
				{
					// On récupère les informations depuis la configuration
					ConfigurationAPI configuration_api = 
						new ConfigurationAPI();
					class_object_type = configuration_api.getString("I-SIS",
						"TableObjectType");
					instance_object_type = configuration_api.getString("I-SIS",
						"InstanceObjectType");
					node_id = configuration_api.getString("I-SIS", 
						"Labels.NodeIdField");
				}
				catch(Exception exception)
				{
					trace_errors.writeTrace(
						"Erreur lors de la récupération de paramètres: " +
						exception.getMessage());
				}
				// On va créer la condition de sélection
				condition.append(node_id);
				condition.append("~'");
				condition.append(selected_node.getTableName());
				condition.append(".");
				condition.append(class_object_type);
				condition.append("' OR ");
				condition.append(node_id);
				condition.append("~'");
				condition.append(selected_node.getTableName());
				condition.append(".");
				condition.append(instance_object_type);
				condition.append("'");
				// Il faut créer une clause de limitation des données à la 
				// table correspondant au noeud sélectionné
				parent_parameters = table_name + "@@" +
					condition.toString() + "@";
			}
		}
		else
		{
			// On récupère le dictionnaire de la table
			definition = manager.getTableDefinition(agent_name,
				selected_node.getIClesName(), selected_node.getServiceType(),
				selected_node.getDefinitionFilePath());
			setTableName(selected_node.getTableName());
		}
		// On vérifie que la table est de type "FT" ou "DB", les seuls qui 
		// soient supportés
		if(definition.type.equals("FT") == false && 
				definition.type.equals("DB") == false)
		{
			trace_errors.writeTrace("La table n'est pas de type FT ou DB !");
			// On va lever une erreur
			//throw new InnerException("&ERR_InvalidTableType", null, null);
		}
		// On libère l'utilisation du dictionnaire
		manager.releaseTableDefinitionLeasing(definition);
		// On appelle l'initialisateur de la super-classe
		super.run(windowInterface, menuItem, parent_parameters,
			preprocessing, postprocessing, selectedNode);
		// On recherche le libellé de la table
		String table_label;
		if(selected_node instanceof GenericTreeClassNode)
		{
			// Le libellé de la table est le libellé du noeud
			table_label = selected_node.getLabel().label;
		}
		else
		{
			// On va créer un noeud table de niveau inférieur au noeud
			// courant
			GenericTreeClassNode temporary_node =
				TreeNodeFactory.makeTreeClassNode(
				selected_node.getServiceSession(), agent_name,
				selected_node.getIClesName(), selected_node.getServiceType(),
				getTableName(), null, null, context);
			// On récupère le libellé du noeud
			table_label = temporary_node.getLabel().label;
			// On libère l'utilisation de la définition
			manager.releaseTableDefinitionLeasing(
				agent_name,
				selected_node.getIClesName(),
				selected_node.getServiceType(), 
				temporary_node.getDefinitionFilePath());
			// On détruit le noeud temporaire
			temporary_node.destroy(true);
		}
		// On positionne le titre de la fenêtre
		setTitle(MessageManager.getMessage("&Admin_Title") + table_label);
		// On va enregistrer un log d'administration
		int counter = 0;
		String action_message =
			MessageManager.getMessage("&LOG_ISISTableAdministration");
		_actionId = 
			LogServiceProxy.getActionIdentifier(agent_name,
			action_message, null, selected_node.getServiceName(),
			selected_node.getServiceType(), selected_node.getIClesName());
		String[] message = new String[9 + context.size()];
		message[counter++] = action_message;
		message[counter++] = MessageManager.getMessage("&LOG_AgentName") +
			selected_node.getAgentName();
		message[counter++] = MessageManager.getMessage("&LOG_UserName") +
			PasswordManager.getInstance().getUserName();
		message[counter++] = MessageManager.getMessage("&LOG_ServiceSessionId") +
			selected_node.getServiceSessionId();
		message[counter++] = MessageManager.getMessage("&LOG_TableName") +
			getTableName();
		message[counter++] = MessageManager.getMessage("&LOG_TableLabel") +
			table_label;
		message[counter++] = MessageManager.getMessage("&LOG_NodeContext");
		message[counter++] = MessageManager.getMessage("&LOG_OpeningQuote");
		IsisParameter[] context_parameters =
			CommonFeatures.buildParametersArray(context);
		for(int index = 0 ; index < context_parameters.length ; index ++)
		{
			message[counter++] = "\t" + context_parameters[index].name +
				"=" + context_parameters[index].value;
		}
		message[counter++] = MessageManager.getMessage("&LOG_ClosingQuote");
		LogServiceProxy.addMessageForAction(_actionId, message);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: addItem
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'élément
	* "Ajouter..." du menu contextuel. Elle instancie puis affiche une boîte de
	* dialogue spécialisée permettant la saisie des données de l'élément à
	* ajouter en fonction de son type (accès, agents...). C'est la boîte de
	* dialogue elle-même qui est chargée de l'exécution et du suivi de la
	* commande de mise à jour de la table.
	* ----------------------------------------------------------*/
	public void addItem()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "addItem");

		trace_methods.beginningOfMethod();
		// On instancie la boîte de dialogue correspondant à la table
		openDialog("Insert", (GenericTreeObjectNode)getSelectedNode());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: modifyItem
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'élément
	* "Modifier..." du menu contextuel. Elle instancie puis affiche une boîte
	* de dialogue spécialisée permettant la modification des données de
	* l'élément à modifier en fonction de son type (accès, agents...). C'est la
	* boîte de dialogue elle-même qui est chargée de l'exécution et du suivi de
	* la commande de mise à jour de la table.
	*
	* Arguments:
	*  - selectedNode: Le noeud qui doit être modifié.
	* ----------------------------------------------------------*/
	public void modifyItem(
		GenericTreeObjectNode selectedNode
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "modifyItem");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// On instancie la boîte de dialogue correspondant à la table
		openDialog("Replace", selectedNode);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: removeItem
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'élément
	* "Supprimer..." du menu contextuel. Elle instancie puis affiche une boîte
	* de dialogue spécialisée permettant l'affichage de l'élément à supprimer
	* en fonction de son type (accès, agents...). C'est la boîte de dialogue
	* elle-même qui est chargée de l'exécution et du suivi de la commande de
	* mise à jour de la table.
	*
	* Arguments:
	*  - selectedNode: Le noeud qui doit être supprimé.
	* ----------------------------------------------------------*/
	public void removeItem(
		GenericTreeObjectNode selectedNode
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "removeItem");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// On instancie la boîte de dialogue correspondant à la table
		openDialog("Remove", selectedNode);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette méthode est appelée lors de la fermeture de la fenêtre. Elle permet
	* de générer un message de log indiquant la fin de l'administration.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "close");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		String[] message = new String[1];
		message[0] = MessageManager.getMessage("&LOG_EndOfAdministration");
		LogServiceProxy.addMessageForAction(_actionId, message);
		// S'il y a une boite de dialogue, on la ferme
		if(_currentDialog != null)
		{
			_currentDialog.cancel();
			_currentDialog = null;
		}
		// On traite le postprocessing
		try {
			ProcessingHandler.handleProcessingStatement(_postProcessing, null, 
				getMainWindowInterface(), null, null, this, false);
		}
		catch(InnerException exception) {
			trace_errors.writeTrace("Erreur lors du traitement du " +
				"postprocessing: " + exception.getMessage());
		}
		// On appelle la méthode de la super-classe
		super.close();
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour effectuer un pré-chargement du processeur.
	* Elle charge le fichier de messages du processeur.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("admin.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer la description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&AdministrationProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance du processeur.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new AdministrationProcessor();
	}

	/*----------------------------------------------------------
	* Nom: getTheTableName
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface DialogCallerInterface. Elle 
	* permet de récupérer le nom de la table administrée.
	* Elle retourne le résultat de la méthode getTableName() définie dans la 
	* super-classe.
	* 
	* Retourne: Le nom de la table administrée.
	* ----------------------------------------------------------*/
	public String getTheTableName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "getTheTableName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return getTableName();
	}

	/*----------------------------------------------------------
	* Nom: getTheMainWindowInterface
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface DialogCallerInterface. Elle 
	* permet de récupérer une référence sur l'interface MainWindowInterface 
	* représentant la fenêtre principale de l'application.
	* Elle retourne le résultat de la méthode getMainWindowInterface() définie 
	* dans la super-classe.
	* 
	* Retourne: Une référence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public MainWindowInterface getTheMainWindowInterface()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "getTheMainWindowInterface");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return getMainWindowInterface();
	}

	/*----------------------------------------------------------
	* Nom: getTheActionId
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface DialogCallerInterface. 
	* Elle permet de récupérer le numéro unique de l'action d'administration 
	* de la table.
	* Elle retourne la valeur contenue dans l'attribut _actionId.
	* 
	* Retourne: Le numéro unique de l'action d'administration.
	* ----------------------------------------------------------*/
	public String getTheActionId()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "getTheActionId");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _actionId;
	}

	/*----------------------------------------------------------
	* Nom: getTheParameters
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface DialogCallerInterface. Elle 
	* permet de récupérer les paramètres d'exécution du processeur.
	* 
	* Retourne: Les paramètres d'exécution du processeur.
	* ----------------------------------------------------------*/
	public String getTheParameters()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "getTheParameters");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _originalParameters;
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: getPopupTrigger
	*
	* Description:
	* Cette méthode redéfini celle de la super-classe. Elle instancie et
	* retourne un objet AdministratorPopupTrigger lequel construit un menu
	* contextuel affichant les éléments "Ajouter...", "Modifier..." et
	* "Supprimer...".
	*
	* Retourne: Une instance de TablePopupTrigger.
	* ----------------------------------------------------------*/
	protected TablePopupTrigger getPopupTrigger()
	{
		TablePopupTrigger popup_trigger = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "getPopupTrigger");

		trace_methods.beginningOfMethod();
		// On instancie le popup trigger d'administration
		popup_trigger = new AdministratorPopupTrigger(this);
		trace_methods.endOfMethod();
		return popup_trigger;
	}

	/*----------------------------------------------------------
	* Nom: makeButtonsPanel
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée
	* lorsque la barre des boutons de la fenêtre du processeur doit être
	* construite. Dans le cas du processeur d'administration, il faut présenter
	* des boutons "Ajouter...", "Recharger" et "Fermer".
	*
	* Retourne: Une référence sur le panneau contenant les boutons.
	* ----------------------------------------------------------*/
	protected JPanel makeButtonsPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "makeButtonsPanel");

		trace_methods.beginningOfMethod();
		// On crée un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		// On crée un autre panneau avec un flow layout
		JPanel panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On crée le bouton Ajouter...
		JButton add_button =
			new JButton(MessageManager.getMessage("&Admin_Add"));
		// On ajoute le callback sur le bouton
		add_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Thread processing_thread = new Thread(new Runnable()
				{
					public void run()
					{
						addItem();
					}
				});
				processing_thread.start();
			}
		});
		// On ajoute le bouton
		panel.add(add_button);
		// On crée le bouton "Recharger"
		JButton reload_button =
			new JButton(MessageManager.getMessage("&Admin_Reload"));
		// On ajoute le callback sur le bouton
		reload_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Thread processing_thread = new Thread(new Runnable()
				{
					public void run()
					{
						reloadData(false);
					}
				});
				processing_thread.start();
			}
		});
		// On ajoute le bouton
		panel.add(reload_button);
		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Admin_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture
				close();
			}
		});
		// On ajoute le bouton
		panel.add(close_button);
		// On calcule la dimension de tous les boutons
		int button_width = Math.max(add_button.getPreferredSize().width,
			Math.max(reload_button.getPreferredSize().width,
			close_button.getPreferredSize().width));
		Dimension size = new Dimension(button_width,
			add_button.getPreferredSize().height);
		add_button.setPreferredSize(size);
		reload_button.setPreferredSize(size);
		close_button.setPreferredSize(size);
		// On ajoute la panneau dans le panneau général
		layout.setConstraints(panel, constraints);
		button_panel.add(panel);
		trace_methods.endOfMethod();
		return button_panel;
	}

	/*----------------------------------------------------------
	* Nom: getCellRendererForColumn
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée lors 
	* de la création des colonnes de la table. Elle permet de fournir un 
	* gestionnaire d'affichage spécifique pour une colonne.
	* La nécessité d'un gestionnaire d'affichage spécifique dépend de la table 
	* administrée, et de la colonne.
	* Dans le cas des tables des libellés (IsisLabels et ICleLabels), un 
	* gestionnaire spécifique (IconCellRenderer) est associé à la colonne Icon.
	* 
	* Arguments:
	*  - index: L'indice de la colonne,
	*  - column: Une référence sur un objet IsisTableColumn correspondant à la 
	*    colonne.
	* 
	* Retourne: Une référence sur un objet TableCellRenderer ou null.
	* ----------------------------------------------------------*/
	protected TableCellRenderer getCellRendererForColumn(
		int index,
		IsisTableColumn column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "getCellRendererForColumn");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		TableCellRenderer renderer = null;
		String table_name;
		String icon_field = "Icon";

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("index=" + index);
		trace_arguments.writeTrace("column=" + column);
		// Si la colonne est nulle, on sort
		if(column == null)
		{
			trace_methods.endOfMethod();
			return renderer;
		}
		// On récupère le nom du champ correspondant à l'icône depuis
		// la configuration
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			icon_field = configuration.getString("I-SIS", "Labels.IconField");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception.getMessage());
		}
		// On récupère le nom de la table
		table_name = getTableName();
		trace_debug.writeTrace("table_name=" + table_name);
		// S'agit-il d'une des tables de libellés
		if(table_name.equals("IsisLabels") == true ||
			table_name.equals("ICleLabels") == true)
		{
			// On regarde si la colonne correspond à celle de l'icône
			if(column.name.equals(icon_field) == true)
			{
				// Il s'agit de la colonne icône, on va créer un gestionnaire
				// d'affichage des icônes
				renderer = new IconCellRenderer();
			}
		}
		trace_methods.endOfMethod();
		return renderer;
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _actionId
	*
	* Description:
	* Cette attribut maintient l'identifiant de l'action correspondant
	* à l'administration d'une table I-SIS.
	* ----------------------------------------------------------*/
	private String _actionId;

	/*----------------------------------------------------------
	* Nom: _currentDialog
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet BaseDialog correspond 
	* à la boite de dialogue ouverte.
	* ----------------------------------------------------------*/
	private BaseDialog _currentDialog;

	/*----------------------------------------------------------
	* Nom: _originalParameters
	* 
	* Description:
	* Cet attribut maintient une chaîne contenant les paramètres originaux 
	* d'exécution du processeur.
	* ----------------------------------------------------------*/
	private String _originalParameters;

	/*----------------------------------------------------------
	* Nom: _postProcessing
	* 
	* Description:
	* Cette attribut maintient les éventuelles instructions de postprocessing 
	* qui ont été fournies lors de l'appel à la méthode run().
	* ----------------------------------------------------------*/
	private String _postProcessing;

	/*----------------------------------------------------------
	* Nom: openDialog
	*
	* Description:
	* Cette méthode est chargée de l'instanciation et de l'affichage de la
	* boîte de dialogue de mise à jour de la table. La classe à instancier
	* dépend de la table qui doit être mise à jour.
	*
	* Arguments:
	*  - action: L'action qui devra être effectuée sur la table,
	*  - selectedNode: Une référence sur le noeud à passer à la boîte de
	*    dialogue.
	* ----------------------------------------------------------*/
	private void openDialog(
		final String action,
		final GenericTreeObjectNode selectedNode
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationProcessor", "openDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("action=" + action);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		String table_name = getTableName();
		trace_debug.writeTrace("table_name=" + table_name);
		// On modifie le curseur -> sablier
		getMainWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, this);
		try
		{
			// Si la table concernée est une table de libellés (IsisLabels ou
			// ICleLabels), on instancie la boîte de dialogue LabelsDialog
			if(table_name.equals("IsisLabels") == true ||
				table_name.equals("ICleLabels") == true)
			{
				_currentDialog = new LabelsDialog(selectedNode, action, this);
			}
			// Dans les autres cas, on instancie la boîte de dialogue
			// SimpleDialog
			if(_currentDialog == null)
			{
				_currentDialog = new SimpleDialog(selectedNode, action, this);
			}
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la création de la boîte de dialogue: " +
				exception);
			// Il y a eu une erreur, il faut afficher un message à
			// l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotBuildForm", exception);
		}
		// On remet le curseur normal
		getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
		// Si la boîte de dialogue n'est pas nulle, on l'affiche
		if(_currentDialog != null)
		{
			boolean was_ok = _currentDialog.showDialog(this);
			_currentDialog = null;
			// On recharge les données
			reloadData(false);
		}
		repaint();
		trace_methods.endOfMethod();
	}
}