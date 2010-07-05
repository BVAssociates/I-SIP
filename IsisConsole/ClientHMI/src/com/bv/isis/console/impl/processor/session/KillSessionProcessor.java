/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/session/KillSessionProcessor.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de fermeture forc�e de session
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
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.3  2005/10/07 08:23:54  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
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
// D�claration du package
package com.bv.isis.console.impl.processor.session;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur de t�che ex�cut� lorsqu'une session de 
* service ou une session Agent doit �tre ferm�e (d�truite).
* Le processeur v�rifie que le noeud concern� correspond bien � une session de 
* service ou une session Agent (en v�rifiant que le dictionnaire correspond � 
* la table des sessions du Portail ou d'un Agent), puis il commande la 
* fermeture de celle-ci.
* Ce processeur n'�tant pas un processeur graphique, il impl�mente l'interface 
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
	* Cette m�thode est le constructeur par d�faut. Elle n'est pr�sent�e que 
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e afin que le processeur effectue un pr�-chargement de ses 
	* donn�es.
	* Pour ce processeur, aucun pr�-chargement n'est n�cessaire.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processor a �t� configur�, si besoin 
	* est.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e r�cup�rer les panneaux de configuration du processeur, si 
	* n�cessaire.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processor peut �tre invoqu� via un noeud 
	* de l'arbre d'exploration.
	* Pour ce processeur, seule l'invocation via un �l�ment de tableau est 
	* autoris�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processor peut �tre invoqu� via un �l�ment 
	* de tableau.
	* Pour ce processeur, seule l'invocation via un �l�ment de tableau est 
	* autoris�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processor peut �tre invoqu� via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via un �l�ment de tableau est 
	* autoris�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle est 
	* appel�e pour r�cup�rer la description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer le libell� de l'�l�ment du menu "Outils" 
	* associ�.
	* Ce processeur n'�tant pas global, il n'y a pas d'�l�ment de menu associ�.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour d�clencher l'ex�cution du processeur de t�che.
	* Pour ce processeur, l'ex�cution consiste � v�rifier que le noeud 
	* concern� correspond bien � un �l�ment de la table des sessions actives 
	* sur le Portail ou sur un Agent (nom des tables provenant de la 
	* configuration). Ensuite, l'interface de l'Agent est r�cup�r�e via la 
	* classe PortalInterfaceProxy, et la m�thode killSession() est appel�e en 
	* passant l'identifiant unique de la session comme argument.
	* 
	* Si un probl�me est d�tect� lors de l'ex�cution du processeur, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - menuItem: Une r�f�rence sur l'�l�ment de menu par lequel le 
	*    processeur a �t� lanc�,
	*  - parameters: Une cha�ne contenant des param�tres optionnels du 
	*    processeur de t�che,
	*  - preprocessing: Une cha�ne contenant des instructions de pr�processing,
	*  - postprocessing: Une cha�ne contenant des instructions de postprocessing,
	*  - selectedNode: Une r�f�rence sur le noeud � explorer.
	* 
	* L�ve: InnerException.
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
		// Tout d'abord, on v�rifie l'int�grit� des param�tres
		if(windowInterface == null || selectedNode == null ||
		   !(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		selected_node = (GenericTreeObjectNode)selectedNode;
		// On r�cup�re les noms des tables des sessions actives et celui du
		// param�tre correspondant � l'identifiant de la session
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
			// Il y a eu une erreur, il faut afficher l'erreur � l'utilisateur
			InnerException inner_exception = CommonFeatures.processException(
				"de la r�cup�ration de la configuration", exception);
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
		// On va r�cup�rer le nom de la table associ�e au noeud
		agent_name = selected_node.getAgentName();
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IsisTableDefinition table_definition = 
			manager.getTableDefinition(agent_name, 
			selected_node.getIClesName(), selected_node.getServiceType(),
			selected_node.getDefinitionFilePath());
		// La d�finition doit avoir �t� pr�alablement charg�e
		if(table_definition == null)
		{
			trace_errors.writeTrace("La d�finition de la table n'est pas " +
				"charg�e !");
			// On va afficher un message � l'utilisateur
			InnerException inner_exception = 
				new InnerException("&ERR_NoDefinitionForTable", null, null);
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		trace_debug.writeTrace("tableName=" + table_definition.tableName);
		// La table doit correspondre � l'une des tables de sessions actives
		if(table_definition.tableName.equals(portal_sessions_table) == false &&
			table_definition.tableName.equals(agent_sessions_table) == false)
		{
			trace_errors.writeTrace("La table ne correspond pas � une table " +
				"de sessions actives !");
			// On va afficher un message � l'utilisateur
			InnerException inner_exception = 
				new InnerException("&ERR_TableIsNotActiveSessions", null, null);
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			// On lib�re la d�finition de la table
			manager.releaseTableDefinitionLeasing(table_definition);
			trace_methods.endOfMethod();
			return;
		}
		// On lib�re la d�finition de la table
		manager.releaseTableDefinitionLeasing(table_definition);
		// On va r�cup�rer la valeur du param�tre contenant l'identifiant de 
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
			// On n'a pas trouv� l'identifiant de la session, on va afficher
			// une erreur
			trace_errors.writeTrace("Impossible de trouver l'identifiant de " +
				"la session !");
			// On va afficher un message � l'utilisateur
			InnerException inner_exception = 
				new InnerException("&ERR_CannotFindSessionId", null, null);
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re l'interface de l'Agent
		try
		{
			PortalInterfaceProxy portal_proxy = 
				PortalInterfaceProxy.getInstance();
			agent_interface = portal_proxy.getAgentInterface(agent_name);
		}
		catch(Exception exception)
		{
			// On va afficher un message � l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On va g�n�rer un message de log
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
			// On g�n�re un log d'erreur
			message_data = new String[2];
			message_data[0] = MessageManager.getMessage("&LOG_KillingSessionResult");
			message_data[1] = MessageManager.getMessage("&LOG_SessionClosingFailed") +
				inner_exception.getMessage(); 
			LogServiceProxy.addMessageForAction(action_id, message_data);
			// On va afficher un message � l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotKillSession",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On g�n�re un log de succ�s
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour fermer le processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
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
