/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/session/KillSessionProcessor.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de fermeture forcée de session
* DATE:        25/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.session
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: KillSessionProcessor.java,v $
* Revision 1.5  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.4  2008/01/31 16:58:58  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.3  2005/10/07 08:23:54  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.2  2005/07/01 12:10:25  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/11/02 08:52:11  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.session;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.message.MessageManager;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.com.PortalInterfaceProxy;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.AgentInterface;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisParameter;

/*----------------------------------------------------------
* Nom: KillSessionProcessor
* 
* Description:
* Cette classe implémente le processeur de tâche exécuté lorsqu'une session de 
* service ou une session Agent doit être fermée (détruite).
* Le processeur vérifie que le noeud concerné correspond bien à une session de 
* service ou une session Agent (en vérifiant que le dictionnaire correspond à 
* la table des sessions du Portail ou d'un Agent), puis il commande la 
* fermeture de celle-ci.
* Ce processeur n'étant pas un processeur graphique, il implémente l'interface 
* ProcessorInterface.
* ----------------------------------------------------------*/
public class KillSessionProcessor 
	implements ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: KillSessionProcessor
	* 
	* Description:
	* Cette méthode est le constructeur par défaut. Elle n'est présentée que 
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public KillSessionProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "KillSessionProcessor");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée afin que le processeur effectue un pré-chargement de ses 
	* données.
	* Pour ce processeur, aucun pré-chargement n'est nécessaire.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "preLoad");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processor a été configuré, si besoin 
	* est.
	* Pour ce processeur, aucune configuration n'est nécessaire.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isConfigured()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "isConfigured");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée récupérer les panneaux de configuration du processeur, si 
	* nécessaire.
	* Pour ce processeur, aucune configuration n'est nécessaire.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "getConfigurationPanels");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processor peut être invoqué via un noeud 
	* de l'arbre d'exploration.
	* Pour ce processeur, seule l'invocation via un élément de tableau est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "isTreeCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processor peut être invoqué via un élément 
	* de tableau.
	* Pour ce processeur, seule l'invocation via un élément de tableau est 
	* autorisée.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "isTableCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processor peut être invoqué via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via un élément de tableau est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "isGlobalCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle est 
	* appelée pour récupérer la description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "getDescription");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&KillSessionProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer le libellé de l'élément du menu "Outils" 
	* associé.
	* Ce processeur n'étant pas global, il n'y a pas d'élément de menu associé.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "getMenuLabel");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour déclencher l'exécution du processeur de tâche.
	* Pour ce processeur, l'exécution consiste à vérifier que le noeud 
	* concerné correspond bien à un élément de la table des sessions actives 
	* sur le Portail ou sur un Agent (nom des tables provenant de la 
	* configuration). Ensuite, l'interface de l'Agent est récupérée via la 
	* classe PortalInterfaceProxy, et la méthode killSession() est appelée en 
	* passant l'identifiant unique de la session comme argument.
	* 
	* Si un problème est détecté lors de l'exécution du processeur, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface,
	*  - menuItem: Une référence sur l'élément de menu par lequel le 
	*    processeur a été lancé,
	*  - parameters: Une chaîne contenant des paramètres optionnels du 
	*    processeur de tâche,
	*  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	*  - selectedNode: Une référence sur le noeud à explorer.
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
			"KillSessionProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		String portal_sessions_table = "PortalActiveServiceSessions";
		String agent_sessions_table = "ActiveServiceSessions";
		String session_id_parameter = "SessionId";
		String icles_name_parameter = "IClesName";
		String service_name_parameter = "ServiceName";
		String service_type_parameter = "IClesType";
		String user_name_parameter = "IsisUser";
		GenericTreeObjectNode selected_node = null;
		AgentInterface agent_interface = null;
		String session_identifier = null;
		String icles_name = null;
		String service_name = null;
		String service_type = null;
		String user_name = null;
		String agent_name = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Tout d'abord, on vérifie l'intégrité des paramètres
		if(windowInterface == null || selectedNode == null ||
		   !(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		selected_node = (GenericTreeObjectNode)selectedNode;
		// On récupère les noms des tables des sessions actives et celui du
		// paramètre correspondant à l'identifiant de la session
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			portal_sessions_table = configuration.getString("I-SIS",
				"PortalActiveSessions.Table");
			agent_sessions_table = 
				configuration.getString("AgentActiveSessions.Table");
			session_id_parameter = 
				configuration.getString("ActiveSessions.SessionIdField");
			icles_name_parameter = 
				configuration.getString("ActiveSessions.IClesNameField");
			service_name_parameter =  
				configuration.getString("ActiveSessions.ServiceNameField");
			service_type_parameter =  
				configuration.getString("ActiveSessions.ServiceTypeField");
			user_name_parameter =  
				configuration.getString("ActiveSessions.UserNameField");
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur, il faut afficher l'erreur à l'utilisateur
			InnerException inner_exception = CommonFeatures.processException(
				"de la récupération de la configuration", exception);
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				inner_exception);
			// On sort
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		trace_debug.writeTrace("portal_sessions_table=" + 
			portal_sessions_table);
		trace_debug.writeTrace("agent_sessions_table=" + 
			agent_sessions_table);
		trace_debug.writeTrace("session_id_parameter=" + session_id_parameter);
		// On va récupérer le nom de la table associée au noeud
		agent_name = selected_node.getAgentName();
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IsisTableDefinition table_definition = 
			manager.getTableDefinition(agent_name, 
			selected_node.getIClesName(), selected_node.getServiceType(),
			selected_node.getDefinitionFilePath());
		// La définition doit avoir été préalablement chargée
		if(table_definition == null)
		{
			trace_errors.writeTrace("La définition de la table n'est pas " +
				"chargée !");
			// On va afficher un message à l'utilisateur
			InnerException inner_exception = 
				new InnerException("&ERR_NoDefinitionForTable", null, null);
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		trace_debug.writeTrace("tableName=" + table_definition.tableName);
		// La table doit correspondre à l'une des tables de sessions actives
		if(table_definition.tableName.equals(portal_sessions_table) == false &&
			table_definition.tableName.equals(agent_sessions_table) == false)
		{
			trace_errors.writeTrace("La table ne correspond pas à une table " +
				"de sessions actives !");
			// On va afficher un message à l'utilisateur
			InnerException inner_exception = 
				new InnerException("&ERR_TableIsNotActiveSessions", null, null);
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			// On libère la définition de la table
			manager.releaseTableDefinitionLeasing(table_definition);
			trace_methods.endOfMethod();
			return;
		}
		// On libère la définition de la table
		manager.releaseTableDefinitionLeasing(table_definition);
		// On va récupérer la valeur du paramètre contenant l'identifiant de 
		// la session
		IsisParameter[] object_parameters =
			selected_node.getObjectParameters();
		session_identifier = 
			TreeNodeFactory.getValueOfParameter(object_parameters, 
			session_id_parameter);
		icles_name = 
			TreeNodeFactory.getValueOfParameter(object_parameters, 
			icles_name_parameter);
		service_name = 
			TreeNodeFactory.getValueOfParameter(object_parameters, 
			service_name_parameter);
		service_type = 
			TreeNodeFactory.getValueOfParameter(object_parameters, 
			service_type_parameter);
		user_name = 
			TreeNodeFactory.getValueOfParameter(object_parameters, 
			user_name_parameter);
		trace_debug.writeTrace("session_identifier=" + session_identifier);
		trace_debug.writeTrace("icles_name=" + icles_name);
		trace_debug.writeTrace("service_name=" + service_name);
		trace_debug.writeTrace("service_type=" + service_type);
		trace_debug.writeTrace("user_name=" + user_name);
		if(session_identifier == null)
		{
			// On n'a pas trouvé l'identifiant de la session, on va afficher
			// une erreur
			trace_errors.writeTrace("Impossible de trouver l'identifiant de " +
				"la session !");
			// On va afficher un message à l'utilisateur
			InnerException inner_exception = 
				new InnerException("&ERR_CannotFindSessionId", null, null);
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On récupère l'interface de l'Agent
		try
		{
			PortalInterfaceProxy portal_proxy = 
				PortalInterfaceProxy.getInstance();
			agent_interface = portal_proxy.getAgentInterface(agent_name);
		}
		catch(Exception exception)
		{
			// On va afficher un message à l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On va générer un message de log
		String action_id = LogServiceProxy.getActionIdentifier(agent_name,
			"&LOG_KillingSession", PasswordManager.getInstance().getUserName(), 
			service_name, service_type, icles_name);
		String[] message_data = new String[8];
		message_data[0] =
			MessageManager.getMessage("&LOG_KillingSession");
		message_data[1] = MessageManager.getMessage("&LOG_AgentName") +
			agent_name;
		message_data[2] = MessageManager.getMessage("&LOG_ServiceSessionId") +
			session_identifier;
		message_data[3] = MessageManager.getMessage("&LOG_IClesName") +
			icles_name;
		message_data[4] = MessageManager.getMessage("&LOG_ServiceName") +
			service_name;
		message_data[5] = MessageManager.getMessage("&LOG_ServiceType") +
			service_type;
		message_data[6] = MessageManager.getMessage("&LOG_UserName") +
			user_name;
		message_data[7] = MessageManager.getMessage("&LOG_KillingUserName") +
			PasswordManager.getInstance().getUserName();
		LogServiceProxy.addMessageForAction(action_id, message_data);
		// On va tenter la fermeture
		try
		{
			agent_interface.killSession(action_id, session_identifier);
		}
		catch(Exception exception)
		{
			InnerException inner_exception = 
				CommonFeatures.processException("la fermeture de la session",
				exception);
			// On génère un log d'erreur
			message_data = new String[2];
			message_data[0] = MessageManager.getMessage("&LOG_KillingSessionResult");
			message_data[1] = MessageManager.getMessage("&LOG_SessionClosingFailed") +
				inner_exception.getMessage(); 
			LogServiceProxy.addMessageForAction(action_id, message_data);
			// On va afficher un message à l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On génère un log de succès
		message_data = new String[2];
		message_data[0] = MessageManager.getMessage("&LOG_KillingSessionResult");
		message_data[1] = MessageManager.getMessage("&LOG_SessionClosingSuccessfull"); 
		LogServiceProxy.addMessageForAction(action_id, message_data);
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour fermer le processeur.
	* Pour ce processeur, la fermeture n'a aucun effet.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "close");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance de KillSessionProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"KillSessionProcessor", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new KillSessionProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
