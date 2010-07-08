/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/session/AgentSessionProcessor.java,v $
* $Revision: 1.34 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'ouverture de session sur un Agent
* DATE:        19/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.session
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: AgentSessionProcessor.java,v $
* Revision 1.34  2009/02/11 15:12:13  tz
* Plus d'obligation de chargement automatique des menus lors de
* la suppression des noeuds interm�diaires.
*
* Revision 1.33  2009/01/23 17:26:50  tz
* Chargement automatique du menu apr�s ouverture de la session
* suivant les pr�f�rences de l'utilisateur.
*
* Revision 1.32  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.31  2008/08/11 10:47:58  tz
* Ajout d'une trace d'erreur.
*
* Revision 1.30  2008/06/27 09:44:16  tz
* Les donn�es relatives � l'Agent, et au service peuvent provenir du
* contexte, et plus seulement du noeud s�lectionn�.
* Traitement des informations depuis le pr�-processing.
*
* Revision 1.29  2008/01/31 16:58:39  tz
* Classe PreprocessingHandler renomm�e.
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.28  2008/01/29 15:54:03  tz
* Correction de l'appel � la m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.27  2007/04/12 15:15:01  ml
* Nombre de profil en param�tre dans Console_config.ini
*
* Revision 1.26  2006/11/09 12:11:11  tz
* Adaptation � la nouvelle m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.25  2006/10/13 15:13:19  tz
* Gestion du mode de fonctionnement de l'Agent.
*
* Revision 1.24  2006/08/11 13:35:41  tz
* Stockage du bon nom de service pour la session Agent.
*
* Revision 1.23  2006/03/07 09:32:22  tz
* Mise � jour par rapport aux �tats du noeud.
*
* Revision 1.22  2005/10/07 11:27:07  tz
* Modification de la m�thode setAgentAndSession().
*
* Revision 1.21  2005/10/07 08:24:03  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.20  2005/07/01 12:11:18  tz
* Modification du composant pour les traces
* Utilisation de l'utilisateur de connexion pour la r�cup�ration de l'abonnement
*
* Revision 1.19  2004/11/24 16:24:07  tz
* Utilisation du nouveau constructeur de LoginDialog.
*
* Revision 1.18  2004/11/23 15:46:22  tz
* Adaptation pour corba-R1_1_2-AL-1_0.
*
* Revision 1.17  2004/11/02 08:52:48  tz
* Gestion de l'�tat du noeud,
* Gestion des leasings sur les d�finitions.
*
* Revision 1.16  2004/10/22 15:37:20  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.15  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.14  2004/10/13 13:55:27  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.13  2004/07/29 12:03:58  tz
* Suppression d'imports inutiles
* Remplacement de Master par Portal
*
* Revision 1.12  2003/12/08 15:13:52  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.11  2003/12/08 14:35:22  tz
* Am�lioration de l'auto-exploration.
*
* Revision 1.10.2.1  2003/10/27 16:52:45  tz
* Support du domaine d'authentification
*
* Revision 1.10  2003/05/15 12:47:31  tz
* Correction de la fiche Inuit/110.
*
* Revision 1.9  2003/03/17 16:49:42  tz
* Correction de la fiche Inuit/105
*
* Revision 1.8  2003/03/07 16:20:07  tz
* Prise en compte du m�canisme de log m�tier
* Ajout de l'auto-exploration
*
* Revision 1.7  2002/11/19 08:41:41  tz
* Gestion de la progression de la t�che.
* Correction de la fiche Inuit/79.
*
* Revision 1.6  2002/09/20 10:47:13  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.5  2002/08/13 13:02:22  tz
* Correction du traitement des erreurs sur mauvais mot de passe
*
* Revision 1.4  2002/06/19 12:15:52  tz
* Correction de la fiche Inuit/24
*
* Revision 1.3  2002/05/29 09:16:10  tz
* Correction fiche Inuit/20
* Cloture R1.0.3
*
* Revision 1.2  2002/04/05 15:52:24  tz
* Cloture it�ration IT1.2
*
* Revision 1.1  2002/03/27 09:49:19  tz
* Import initial
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
import com.bv.core.prefs.PreferencesAPI;

import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import java.util.Properties;
import java.io.FileInputStream;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.AgentLayerAbstractor;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.SessionTreeObjectNode;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.com.PortalInterfaceProxy;
import com.bv.isis.corbacom.AgentSessionInterface;
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.console.node.MenuFactory;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.com.TableDefinitionManager;

/*----------------------------------------------------------
* Nom: AgentSessionProcessor
*
* Description:
* Cette classe impl�mente le processeur de t�che ex�cut� lorsqu'une session de
* service doit �tre ouverte. Le m�canisme d'ouverture de session de service
* diff�re l�g�rement suivant qu'il s'agit d'ouvrir une session sur un service
* de production ou sur le service de l'Agent.
* Dans tous les cas, le processeur va demander � l'utilisateur de s�lectionner
* un identifiant et de saisir un mot de passe. Avec ces informations, le
* processeur va tenter d'ouvrir une session Agent.
* Ensuite, le processeur va tenter d'ouvrir une session sur le service de
* l'Agent, afin de contr�ler que l'utilisateur a le droit de se connecter �
* l'Agent.
* Si le service concern� est un service de production, le processeur va en plus
* tenter d'ouvrir une session sur le service de production.
* Finalement, le processeur va charger les informations relatives � l'acc�s de
* l'utilisateur sur le service.
* Ces informations seront utilis�es pour cr�er un noeud graphique qui sera
* ajout� au noeud s�lectionn� (comme une exploration).
* ----------------------------------------------------------*/
public class AgentSessionProcessor
	implements ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: AgentSessionProcessor
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public AgentSessionProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentSessionProcessor", "AgentSessionProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette m�thode red�fini celle de l'interface ProcessorInterface. Elle est
	* appel�e afin d'initialiser et d'ex�cuter le processeur.
	* Dans le cas du processeur d'ouverture de session, la m�thode commence par
	* v�rifier si le noeud n'a pas d�j� �t� explor�. Si le noeud n'a pas �t�
	* explor�, il ne doit avoir aucun un noeud fils.
	* Dans ce cas, le processeur lance la proc�dure d'ouverture de session de
	* service.
	*
	* Si une erreur est d�tect�e lors de l'initialization, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - menuItem: Une r�f�rence sur l'�l�ment de menu par lequel le processeur
	*    a �t� lanc�,
	*  - parameters: Une cha�ne contenant des fparam�tres optionnels du
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
		String agent_parameter_name = "AgentName";
		String service_parameter_name = "ServiceName";
		String type_parameter_name = "ServiceType";
		String icle_parameter_name = "ICleName";
		String agent_name = null;
		String service_name = null;
		String service_type = null;
		String icle_name = null;
		GenericTreeObjectNode selected_node;
		AgentSessionInterface agent_session = null;
		ServiceSessionInterface service_session = null;
		boolean is_service_session = false;
		String[] extra_info = null;
		String status_message = null;
		String agent_session_id = null;
		boolean remove_unnecessary_nodes = true;
		boolean preload_menus = true;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentSessionProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

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
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// Tout d'abord, il faut v�rifier que le noeud n'a pas �t� explor�
		if(selectedNode.getChildCount() > 0)
		{
			trace_debug.writeTrace("Le noeud a d�j� �t� explor�");
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On va r�cup�rer des pr�f�rences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			// On utilise la section AUTO-EXPLORE
			preferences.useSection("AUTO-EXPLORE");
			remove_unnecessary_nodes = 
				preferences.getBoolean("RemoveUnnecessaryNodes");
			preload_menus = preferences.getBoolean("PreloadMenus");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		// On caste le noeud en GenericTreeObjectNode
		selected_node = (GenericTreeObjectNode)selectedNode;
		// Il faut r�cup�rer les noms des param�tres indiquant:
		// - Le nom de l'agent,
		// - Le nom du service Agent,
		// - Le nom du service de production
		// - Le type du service de production
		// - Le nom du I-CLES
		try
		{
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
		    agent_parameter_name =
				configuration_api.getString("Parameter.AgentName");
		    service_parameter_name =
				configuration_api.getString("Parameter.ServiceName");
		    type_parameter_name =
				configuration_api.getString("Parameter.ServiceType");
			icle_parameter_name =
				configuration_api.getString("Parameter.IClesName");
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur, il faut afficher l'erreur � l'utilisateur
			InnerException inner_exception = CommonFeatures.processException(
				"de la r�cup�ration de la configuration", exception);
			windowInterface.showPopupForException("&ERR_CannotOpenSession",
				inner_exception);
			// On sort
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le contexte du noeud s�lectionn�
		IndexedList node_context = selected_node.getContext(true);
		// On traite le pr�processing
		IsisParameter[] preprocessing_parameters = null;
		try
		{
			preprocessing_parameters =
				ProcessingHandler.handleProcessingStatement(preprocessing, 
				node_context, windowInterface, 
				AgentSessionManager.getInstance().getAgentLayerMode(
				selected_node.getAgentName()), 
				selected_node.getServiceSession(), (Component)windowInterface,
				true);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors du traitement du pr�processing: " +
				exception.getMessage());
			// Il y a eu une erreur lors du traitement du pr�processing, on va
			// afficher un message � l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotOpenSession",
				exception);
			// On positionne l'�tat du noeud
			selected_node.setNodeState(
				GenericTreeObjectNode.NodeStateEnum.CLOSED);
			// On sort
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On va ensuite extraire le tableau d'objet du contexte
		IsisParameter[] object_parameters =
			(IsisParameter[])node_context.toArray(new IsisParameter[0]);
		// On r�cup�re les valeurs des param�tres
		agent_name = TreeNodeFactory.getValueOfParameter(object_parameters,
			agent_parameter_name);
		service_name = TreeNodeFactory.getValueOfParameter(object_parameters,
			service_parameter_name);
		service_type = TreeNodeFactory.getValueOfParameter(object_parameters,
			type_parameter_name);
		icle_name = TreeNodeFactory.getValueOfParameter(object_parameters, 
			icle_parameter_name);
		trace_debug.writeTrace("agent_name=" + agent_name);
		trace_debug.writeTrace("service_name=" + service_name);
		trace_debug.writeTrace("service_type=" + service_type);
		trace_debug.writeTrace("icle_name=" + icle_name);
		// On v�rifie que les param�tres minimums sont pr�sents
		if(agent_name == null || agent_name.equals("") == true)
		{
			trace_errors.writeTrace(
				"Au moins une information n�cessaire n'est pas pr�sente.");
			// Au moins une des informations n'est pas pr�sente
			InnerException exception =
				new InnerException("&ERR_MandatoryInformationNotPresent", null,
				null);
			// On affiche l'erreur
			windowInterface.showPopupForException("&ERR_CannotOpenSession",
				exception);
			// On sort
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// Est-ce qu'il s'agit d'une session de service ?
		if(service_name != null && service_name.equals("") == false)
		{
			is_service_session = true;
			// On v�rifie que les informations n�cessaire � l'ouverture d'une
			// session de service sont pr�sentes
			if(icle_name == null || icle_name.equals("") == true ||
				service_type == null || service_type.equals("") == true)
			{
				trace_errors.writeTrace(
					"Au moins une information n�cessaire n'est pas pr�sente.");
				// Au moins une des informations n'est pas pr�sente
				InnerException exception =
					new InnerException("&ERR_MandatoryInformationNotPresent",
					null, null);
				// On affiche l'erreur
				windowInterface.showPopupForException("&ERR_CannotOpenSession",
					exception);
				// On sort
				windowInterface.setStatus(null, null, 0);
				trace_methods.endOfMethod();
				return;
			}
		}
		windowInterface.setProgressMaximum(3);
		// On positionne l'�tat du noeud
		selected_node.setNodeState(
			GenericTreeObjectNode.NodeStateEnum.STATE_CHANGING);
		trace_debug.writeTrace("is_service_session=" + is_service_session);
		// Affichage de l'�tat
		if(agent_name.equals("Portal") == false)
		{
		    extra_info = new String[1];
			extra_info[0] = agent_name;
		    status_message = "&Status_ConnectingToAgent";
		}
		else
		{
			status_message = "&Status_ConnectingToPortal";
		}
		windowInterface.setStatus(status_message, extra_info, 0);
		// On commence, quoi qu'il en soit, par ouvrir la session sur l'agent
		try
		{
			agent_session = openAgentSession(agent_name, windowInterface);
			// Si l'utilisateur a annul� l'ouverture de la session, la valeur
			// retourn�e est null, on peut alors sortir
			if(agent_session == null)
			{
				windowInterface.setStatus(null, null, 0);
				// On positionne l'�tat du noeud
				selected_node.setNodeState(
					GenericTreeObjectNode.NodeStateEnum.CLOSED);
				if(agent_name.equals("Portal") == true)
				{
					windowInterface.closeSession(false);
				}
				trace_methods.endOfMethod();
				return;
			}
			agent_session_id = agent_session.getSessionIdentifier();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de l'ouverture de la " +
				"session sur l'Agent " + agent_name + " : " + exception);
			// On affiche l'erreur
			windowInterface.showPopupForException("&ERR_CannotOpenAgentSession",
				exception);
			// On sort
			windowInterface.setStatus(null, null, 0);
			// On positionne l'�tat du noeud
			selected_node.setNodeState(
				GenericTreeObjectNode.NodeStateEnum.CLOSED);
			trace_methods.endOfMethod();
			return;
		}
		// Est-ce qu'il y a une session Agent (l'utilisateur a-t-il annul�) ?
		if(agent_session == null)
		{
			// On sort
			windowInterface.setStatus(null, null, 0);
			// On positionne l'�tat du noeud
			selected_node.setNodeState(
				GenericTreeObjectNode.NodeStateEnum.CLOSED);
			trace_methods.endOfMethod();
			return;
		}
		windowInterface.setConnected(true, false);
		// S'il s'agit d'ouvrir une session de service (autre que Agent), il
		// faut ouvrir la session sur ce service
		if(is_service_session == true)
		{
			extra_info[0] = service_name;
			status_message = "&Status_OpeningServiceSession";
			windowInterface.setStatus(status_message, extra_info, 1);
			trace_debug.writeTrace("Ouverture de session de service");
			trace_debug.writeTrace("I-CLES name=" + icle_name);
			trace_debug.writeTrace("ServiceName=" + service_name);
			trace_debug.writeTrace("ServiceType=" + service_type);
		    try
			{
				service_session = openServiceSession(agent_name, agent_session,
					icle_name, service_name, service_type);
			}
			catch(InnerException exception)
			{
				trace_errors.writeTrace(
					"Erreur lors de l'ouverture de la session sur le service " +
					service_name + ": "  + exception.getMessage());
				// Il y a eu une erreur lors de l'ouverture de la session sur le
				// service, il faut afficher un message � l'utilisateur
				windowInterface.showPopupForException(
					"&ERR_CannotOpenServiceSession", exception);
				// Il faut lib�rer le leasing sur la session Agent
				AgentSessionManager agent_manager =
					AgentSessionManager.getInstance();
				agent_manager.releaseAgentSession(agent_name, service_name,
					service_type, icle_name);
				// On sort
				windowInterface.setStatus(null, null, 0);
				// On positionne l'�tat du noeud
				selected_node.setNodeState(
					GenericTreeObjectNode.NodeStateEnum.CLOSED);
				trace_methods.endOfMethod();
				return;
			}
		}
		else
		{
		    windowInterface.setStatus(status_message, extra_info, 1);
			try
			{
				// La session de service est celle de la session Agent
				service_session = agent_session.getAgentServiceSession();
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace(
					"Erreur lors de la r�cup�ration de la session de service " +
					"de l'Agent: "  + exception.getMessage());
				// Il y a eu une erreur lors de l'ouverture de la session sur le
				// service, il faut afficher un message � l'utilisateur
				windowInterface.showPopupForException(
					"&ERR_CannotOpenServiceSession", exception);
				// Il faut lib�rer le leasing sur la session Agent
				AgentSessionManager agent_manager =
					AgentSessionManager.getInstance();
				if(agent_name.equals("Portal") == true)
				{
				    agent_manager.releaseAgentSession(agent_name, "I-SIS",
						"Portal", "I-SIS");
				}
				else
				{
				    agent_manager.releaseAgentSession(agent_name, "I-SIS",
						"Agent", "I-SIS");
				}
				// On sort
				windowInterface.setStatus(null, null, 0);
				// On positionne l'�tat du noeud
				selected_node.setNodeState(
					GenericTreeObjectNode.NodeStateEnum.CLOSED);
				trace_methods.endOfMethod();
				return;
			}
		}
		windowInterface.setStatus(status_message, extra_info, 2);
		// On va maintenant cr�er le noeud correspondant au service
		try
		{
			makeServiceNode(agent_name, service_name, service_type, icle_name,
				service_session, selected_node, is_service_session, true,
				preprocessing_parameters, windowInterface, agent_session_id,
				node_context);
			// Il faut avertir l'arbre d'exploration que la structure du noeud p�re
			// a chang�
			windowInterface.getTreeInterface().nodeStructureChanged(
				selected_node);
			// On va �tendre l'arbre
			windowInterface.getTreeInterface().expandNode(selected_node);
		    windowInterface.setStatus(status_message, extra_info, 3);
		    // On charge automatiquement le menu contextuel du noeud
			// On va charger automatiquement le menu contextuel du
			// noeud
			if(preload_menus == true) {
			    MenuFactory.createContextualMenu(
			    	(GenericTreeObjectNode)selected_node.getChildAt(0), 
			    	true, windowInterface);
			}
			// Appel de la m�thode d'exploration automatique
			if(selected_node.getChildCount() == 1)
			{
			    MenuFactory.doAutomaticExplore(windowInterface,
					(GenericTreeObjectNode)selected_node.getChildAt(0), 
					true, 1);
			}
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la cr�ation du noeud de service: " +
				exception.getMessage());
			// Il y a eu une erreur, on affiche le message � l'utilisateur
			windowInterface.showPopupForException(
				"&ERR_CannotOpenServiceSession", exception);
			if(is_service_session == true)
			{
				// Il faut fermer la session de service
				try
				{
					ServiceSessionProxy session_proxy =
						new ServiceSessionProxy(service_session);
					session_proxy.close(agent_name, service_name, service_type,
						icle_name);
				}
				catch(Exception another_exception)
				{
					trace_errors.writeTrace(
						"Erreur lors de la fermeture de la session: " +
						another_exception.getMessage());
				}
			}
			// Il faut lib�rer le leasing sur la session Agent
			AgentSessionManager agent_manager =
				AgentSessionManager.getInstance();
			if(service_name != null && service_name.equals("") == false)
			{
			    agent_manager.releaseAgentSession(agent_name, service_name,
				    service_type, icle_name);
			}
			else
			{
				if(agent_name.equals("Portal") == true)
				{
					agent_manager.releaseAgentSession(agent_name, "I-SIS",
						"Portal", "I-SIS");
				}
				else
				{
					agent_manager.releaseAgentSession(agent_name, "I-SIS",
						"Agent", "I-SIS");
				}
			}
			// On sort
			windowInterface.setStatus(null, null, 0);
			// On positionne l'�tat du noeud
			selected_node.setNodeState(
				GenericTreeObjectNode.NodeStateEnum.CLOSED);
			trace_methods.endOfMethod();
			return;
		}
		// On positionne l'�tat du noeud
		selected_node.setNodeState(
			GenericTreeObjectNode.NodeStateEnum.OPENED);
		windowInterface.setConnected(true, false);
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette m�thode red�fini celle de l'interface ProcessorInterface. Elle est
	* appel�e pour arr�ter l'ex�cution du processor.
	* Dans le cas pr�sent, elle n'effectue aucun traitement particulier.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentSessionProcessor", "close");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour effectuer le pr�-chargement du processeur.
	* Pour ce processeur, la m�thode ne fait rien.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur a �t� configur� ou s'il a 
	* besoin d'une configuration.
	* Le processeur a �t� configur� si au moins un profil a �t� renseign� dans 
	* le fichier de propri�t�s point� par la propri�t� "PROFILES".
	* 
	* Retourne: true si le processeur a �t� configur�, false sinon.
	* ----------------------------------------------------------*/
	public boolean isConfigured()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionProcessor", "isConfigured");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		boolean configured = false;
		int number_of_profiles = 5;

		trace_methods.beginningOfMethod();
		// On va tenter de r�cup�rer les anciennes valeurs, s'il y en a
		String properties_file = System.getProperty("PROFILES");
		trace_debug.writeTrace("properties_file=" + properties_file);
		Properties properties = new Properties();
		try
		{
			FileInputStream input_stream = new FileInputStream(properties_file);
			properties.load(input_stream);
			input_stream.close();
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors du chargement du fichier de propri�t�s: " +
				exception);
			// On peut sortir
			trace_methods.endOfMethod();
			return configured;
		}
		
		//On va r�cup�rer le nombre de profils maximum dans le fichier Console_config.ini
		try
		{
			ConfigurationAPI config_api = new ConfigurationAPI();
			number_of_profiles = config_api.getInt("Console","NumberOfProfiles");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace(
					"Erreur lors de la lecture du nombre de profils maximum: " +
					exception);
		}
		
		// Le processeur est configur� uniquement si au moins un des profils a
		// �t� renseign�
		for(int index = 0 ; index < number_of_profiles ; index ++)
		{
			trace_debug.writeTrace("index=" + index);
			String label = properties.getProperty("Profile" + index +
				".Label");
			String configuration = properties.getProperty("Profile" + index +
				".Configuration");
			trace_debug.writeTrace("label=" + label);
			trace_debug.writeTrace("configuration=" + configuration);
			if(label != null && label.equals("") == false &&
				configuration != null && configuration.equals("") == false)
			{
				configured = true;
				break;
			}
		}
		trace_methods.endOfMethod();
		return configured;
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer les panneaux de configuration du processeur.
	* Une instance de PortalConfigurationPanel est cr��e et retourn�e.
	* 
	* Retourne: Un tableau contenant un seul �l�ment.
	* ----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionProcessor", "getConfigurationPanels");
		ConfigurationPanelInterface[] panels = 
			new ConfigurationPanelInterface[1];

		trace_methods.beginningOfMethod();
		panels[0] = new PortalConfigurationPanel();
		trace_methods.endOfMethod();
		return panels;
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un noeud 
	* d'exploration.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* possible.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionProcessor", "isTreeCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un �l�ment 
	* d'un tableau.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* possible.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionProcessor", "isTableCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� hors d'un 
	* noeud d'exploration ou d'un �l�ment d'un tableau.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* possible.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionProcessor", "isGlobalCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer la description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&AgentSessionProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer l'intitul� de l'�l�ment de menu associ�.
	* Ce processeur n'�tant pas global, cette m�thode ne sera pas appel�e.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionProcessor", "getMenuLabel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
	* 
	* Retourne: Une nouvelle instance du processeur.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new AgentSessionProcessor();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: makeServiceNode
	*
	* Description:
	* Cette m�thode permet de construire le noeud de service (instance de
	* ServiceTreeObjectNode) � partir d'un noeud construit � partir des
	* informations d'acc�s de l'utilisateur au service.
	*
	* Si un probl�me survient lors de la cr�ation du noeud de service,
	* l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent sur lequel la session a �t� ouverte,
	*  - serviceName: Le nom du service sur lequel la session a �t� ouverte,
	*  - serviceType: Le type du service sur lequel la session a �t� ouverte,
	*  - iClesName: Le nom du I-CLES sur lequel la session a �t� ouverte,
	*  - serviceSession: Une r�f�rence sur la session de service,
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn� lors de
	*    l'ex�cution du processeur,
	*  - closeSessionOnDestroy: Indique si la session attach�e au noeud
	*    doit �tre ferm�e � la destruction du noeud ou non,
	*  - releaseAgentLeasingOnDestroy: Indique si le noeud doit lib�rer un
	*    leasing sur la session Agent lors de sa destruction,
	*  - preprocessingParameters: Un tableau d'IsisParameter �
	*    ajouter au noeud,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - agentSessionId: L'identifiant de la session Agent,
	*  - context: Le contexte d'ouverture de la session.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	protected void makeServiceNode(
		String agentName,
		String serviceName,
		String serviceType,
		String iClesName,
		ServiceSessionInterface serviceSession,
		GenericTreeObjectNode selectedNode,
		boolean closeSessionOnDestroy,
		boolean releaseAgentLeasingOnDestroy,
		IsisParameter[] preprocessingParameters,
		MainWindowInterface windowInterface,
		String agentSessionId,
		IndexedList context
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentSessionProcessor", "makeServiceNode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String user_field_name = "IsisUser";
		String service_parameter_name = "ServiceName";
		String type_parameter_name = "ServiceType";
		String and_operator = "AND";
		String agent_layer_mode = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("serviceName=" + serviceName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("closeSessionOnDestroy=" +
			closeSessionOnDestroy);
		trace_arguments.writeTrace("releaseAgentLeasingOnDestroy=" +
			releaseAgentLeasingOnDestroy);
		trace_arguments.writeTrace("preprocessingParameters=" +
			preprocessingParameters);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("agentSessionId=" + agentSessionId);
		trace_arguments.writeTrace("context=" + context);
		// On va tout d'abord r�cup�rer le nom de la table fournissant les
		// informations initiales
		try
		{
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			user_field_name =
				configuration_api.getString("ServiceInitialTable.UsernameField");
		    service_parameter_name =
				configuration_api.getString("ServiceInitialTable.ServiceName");
		    type_parameter_name =
				configuration_api.getString("ServiceInitialTable.ServiceType");
			and_operator = configuration_api.getString("I-TOOLS",
				"Request.AndOperator");
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur, on va g�n�rer une InnerException
			InnerException inner_exception = CommonFeatures.processException(
				"de la r�cup�ration de la configuration", exception);
			// On la l�ve
			trace_methods.endOfMethod();
			throw inner_exception;
		}
		trace_debug.writeTrace("user_field_name=" + user_field_name);
		trace_debug.writeTrace("service_parameter_name=" +
			service_parameter_name);
		trace_debug.writeTrace("type_parameter_name=" +
			type_parameter_name);
		// On va r�cup�rer le mode de la couche d'ex�cution de l'Agent
		agent_layer_mode = 
			AgentSessionManager.getInstance().getAgentLayerMode(agentName);
		trace_debug.writeTrace("agent_layer_mode=" + agent_layer_mode);
		// On va cr�er la condition sur la table (permettant de ne r�cup�rer
		// que les informations concernant l'utilisateur
		StringBuffer condition = 
			new StringBuffer(user_field_name);
		condition.append("='");
		condition.append(AgentLayerAbstractor.getVariableReference(
			agent_layer_mode, "ISIS_CNXUSR"));
		condition.append("'");
		// Si les param�tres ServiceName et ServiceType sont fournis,
		// on va les utiliser pour construire la condition
		if(serviceName != null && serviceName.equals("") == false &&
			serviceType != null && serviceType.equals("") == false)
		{
			condition.append(" " + and_operator + " ");
			condition.append(service_parameter_name + "='" +
				serviceName + "'");
			condition.append(" " + and_operator + " ");
			condition.append(type_parameter_name + "='" +
				serviceType + "'");
		}
		// Si serviceType est null, on va utiliser I-SIS
		if(serviceType == null || serviceType.equals("") == true)
		{
			serviceType = "..I-SIS";
		}
		// Si serviceName est null, on va utiliser Agent ou Portail, suivant
		// le nom de l'agent
		if(serviceName == null || serviceName.equals("") == true)
		{
			if(agentName.equals("Portal") == true)
			{
				serviceName = "Portal";
			}
			else
			{
				serviceName = "Agent";
			}
		}
		// Si iclesName est null, on va utiliser I-SIS
		if(iClesName == null || iClesName.equals("") == true)
		{
			iClesName = "I-SIS";
		}
		// On va r�cup�rer les noeuds r�sultant de l'ex�cution du Select sur
		// la table
		GenericTreeObjectNode[] result =
			TreeNodeFactory.makeTreeObjectNodes(serviceSession, agentName,
			iClesName, serviceType, serviceSession.getInitialTable(), 
			condition.toString(), selectedNode, context, null, "Session");
		// Il ne doit y avoir qu'un seul noeud graphique
		if(result == null || result.length != 1)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			if(result == null)
			{
				trace_errors.writeTrace(
					"Erreur lors de la cr�ation du noeud de service, le nombre" +
					" de noeuds r�sulats est incorrect: null");
			}
			else
			{
				trace_errors.writeTrace(
					"Erreur lors de la cr�ation du noeud de service, le nombre" +
					" de noeuds r�sulats est incorrect: " + result.length);
			}
			// Le nombre de noeud est incorrect, on va afficher un message
			// � l'utilisateur
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_IncorrectNumberOfValues", null, null);
		}
		// On va convertir le noeud r�sultat en un noeud de type
		// SessionTreeObjectNode
		SessionTreeObjectNode session_node =
			new SessionTreeObjectNode(agentName, iClesName, serviceType, 
			result[0]);
		// On lib�re l'utilisation de la d�finition
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		if(result[0].getAgentName() == null && agentName != null)
		{
			// On lib�re une utilisation
			manager.releaseTableDefinitionLeasing(agentName, iClesName, 
				serviceType, session_node.getDefinitionFilePath());
		}
		// On positionne le nom de l'agent et la r�f�rence sur la session
		session_node.setAgentAndSession(serviceSession, serviceName, 
			agentSessionId);
		session_node.setCloseSessionOnDestroy(closeSessionOnDestroy);
		session_node.setReleaseAgentLeasingOnDestroy(
			releaseAgentLeasingOnDestroy);
		// On ajoute les param�tres de pr�processing
		session_node.setPreprocessingData(preprocessingParameters);
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: openAgentSession
	*
	* Description:
	* Cette m�thode permet d'ouvrir une session sur l'agent d�fini par
	* l'argument agentName.
	* Pour cela, la m�thode va tout d'abord v�rifier si une session agent n'a
	* pas d�j� �t� ouverte, via la classe AgentSessionManager.
	* Si ce n'est pas le cas, la m�thode va utiliser les classes LoginDialog et
	* PasswordManager afin de r�cup�rer l'identifiant et le mot de passe de
	* l'utilisateur. La session agent sera ouverte via la classe
	* AgentSessionManager.
	*
	* Si un probl�me survient lors de l'ouverture de la session Agent,
	* l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - agentName: Nom de l'Agent sur lequel il faut ouvrir une session,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface.
	*
	* Retourne: Une r�f�rence sur la session Agent.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private AgentSessionInterface openAgentSession(
		String agentName,
		MainWindowInterface windowInterface
		)
		throws
			InnerException
	{
		boolean try_auto = false;
		AgentSessionInterface agent_session = null;
		String user_name;
		String[] users_names;
		String previous_password = "";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentSessionProcessor", "openAgentSession");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		// Tout d'abord, on va regarder s'il n'y a pas d�j� une session
		// ouverte sur l'Agent
		AgentSessionManager session_manager = AgentSessionManager.getInstance();
		agent_session = session_manager.getAgentSession(agentName);
		if(agent_session != null)
		{
			trace_debug.writeTrace("Il y a d�j� une session ouverte !");
			// Il y a une session d�j� ouverte, on va l'utiliser
			trace_methods.endOfMethod();
			return agent_session;
		}
		// On va r�cup�rer la r�f�rence sur le PasswordManager
		PasswordManager password_manager = PasswordManager.getInstance();
		// On va r�cup�rer le nom de l'utilisateur
		user_name = password_manager.getUserName();
		// Est-ce qu'il y a un nom d'utilisateur ?
		if(user_name == null || user_name.equals("") == true)
		{
			// Il va falloir aller r�cup�rer la liste des mots de passe depuis
			// le Portail
			PortalInterfaceProxy portal_proxy =
				PortalInterfaceProxy.getInstance();
			users_names = portal_proxy.getUsersNames();
			// On ne peut pas tenter l'auto-loggin
			try_auto = false;
		}
		else
		{
			// La liste des noms d'utilisateurs ne contient que le nom
			// pr�lablement utilis�
			users_names = new String[1];
			users_names[0] = user_name;
			// On peut tenter l'auto-loggin
			try_auto = true;
		}
		// On demande l'identification jusqu'� ce que l'utilisateur
		// annule
		while(true)
		{
			// On va r�cup�rer le mot de passe pr�c�dent
			previous_password = password_manager.getPassword(agentName);
			if(try_auto == false)
			{
				// On cr�e la bo�te d'identification
				LoginDialog login_dialog = new LoginDialog(windowInterface);
				if(login_dialog.getLogin(users_names, previous_password,
					(Component)windowInterface) == false)
				{
					trace_debug.writeTrace(
						"L'utilisateur a annul� l'identification");
					login_dialog.dispose();
					// On sort en retournant null
					trace_methods.endOfMethod();
					return null;
				}
				trace_debug.writeTrace("L'utilisateur a valid� l'identification");
				// On r�cup�re le nom de l'utilisateur et son mot de passe
				user_name = login_dialog.getUserName();
				previous_password = login_dialog.getPassword();
				login_dialog.dispose();
			}
			// On va g�n�rer un message de log
			String service_name;
			if(agentName.equals("Portal") == true)
			{
				service_name = "Portal";
			}
			else
			{
				service_name = "Agent";
			}
			String action_id = LogServiceProxy.getActionIdentifier(agentName,
				"&LOG_AgentSessionOpening", user_name, service_name, "..I-SIS",
				"I-SIS");
			// On va g�n�rer le premier message
			String[] message_data = new String[3];
			message_data[0] =
				MessageManager.getMessage("&LOG_AgentSessionOpening");
			message_data[1] = MessageManager.getMessage("&LOG_AgentName") +
				agentName;
			message_data[2] = MessageManager.getMessage("&LOG_UserName") +
				user_name;
			LogServiceProxy.addMessageForAction(action_id, message_data);
		    // On tente d'ouvrir une session sur l'agent
			try
			{
		        agent_session = session_manager.openAgentSession(action_id,
					agentName, user_name, previous_password);
				// On g�n�re le log de bonne fermeture
				message_data = new String[3];
				message_data[0] =
					MessageManager.getMessage("&LOG_SessionOpeningResult");
				message_data[1] =
					MessageManager.getMessage("&LOG_SessionOpeningSuccessfull");
				try
				{
					message_data[2] =
						MessageManager.getMessage("&LOG_AgentSessionId") +
						agent_session.getSessionIdentifier();
				}
				catch(Exception e)
				{
					message_data[2] =
						MessageManager.getMessage("&LOG_AgentSessionId") +
						"undefined";
				}
				LogServiceProxy.addMessageForAction(action_id, message_data);
			}
			catch(InnerException exception)
			{
				// On r�cup�re le tableau du d�tail de l'erreur
				String[] error_message = CommonFeatures.buildArrayFromString(
					exception.getDetails());
				message_data = new String[error_message.length + 3];
				message_data[0] =
					MessageManager.getMessage("&LOG_SessionOpeningResult");
				message_data[1] =
					MessageManager.getMessage("&LOG_SessionOpeningFailed");
				message_data[2] =
					MessageManager.getMessage(exception.getReason());
				for(int index = 0 ; index < error_message.length ; index ++)
				{
					message_data[index + 3] = error_message[index];
				}
				// On logue le message
				LogServiceProxy.addMessageForAction(action_id, message_data);
				trace_errors.writeTrace("L'ouverture de session sur l'agent " +
					agentName + " a �chou�e: " + exception);
				// On affiche l'erreur
				windowInterface.showPopupForException(
					"&ERR_CannotOpenAgentSession", exception);
				// Il y a eu une erreur, on repart dans la boucle, seulement
				// s'il s'agit d'un probl�me de mot de passe (pour un agent), et
				// un probl�me d'identifiant pour le Portail
				try_auto = false;
				String original_exception =
					exception.getOriginalExceptionClassName();
				// Dans tous les cas, s'il s'agit du Portail, on retente
				if(agentName.equals("Portal") == true)
				{
					continue;
				}
				// S'il s'agit d'un mauvais mot de passe, on retente
				if(original_exception.equals("InvalidPasswordException") == true)
				{
					continue;
				}
			}
		    // On g�n�re la trace d'ouverture de session
		    trace_events.writeTrace("L'utilisateur " + user_name +
			    " a ouvert une session sur l'agent " + agentName);
		    // On enregistre ces informations dans le PasswordManager
		    password_manager.setUserName(user_name);
		    password_manager.addPassword(agentName, previous_password);
			// Si on arrive ici, on peut sortir
			break;
		}
		trace_methods.endOfMethod();
		return agent_session;
	}

	/*----------------------------------------------------------
	* Nom: openServiceSession
	*
	* Description:
	* Cette m�thode permet d'ouvrir une session de service � partir d'une
	* session Agent. L'ouverture de session de service est effectu�e via la
	* m�thode openServiceSession() de l'interface AgentSessionInterface.
	*
	* Si un probl�me survient lors de l'ouverture de la session de service,
	* l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - agentName: Le nom de l'agent,
	*  - agentSession: Une r�f�rence sur la session Agent � partir de laquelle
	*    la session de service doit �tre ouverte,
	*  - iClesName: Le nom du I-CLES o� est d�fini le service,
	*  - serviceName: Le nom du service,
	*  - serviceType: Le type du service.
	*
	* Retourne: Une r�f�rence sur la nouvelle session de service.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private ServiceSessionInterface openServiceSession(
		String agentName,
		AgentSessionInterface agentSession,
		String iClesName,
		String serviceName,
		String serviceType
		)
		throws
			InnerException
	{
		ServiceSessionInterface service_session = null;
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentSessionProcessor", "openServiceSession");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("agentSession=" + agentSession);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceName=" + serviceName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		// On va ouvrir un log pour l'action
		String action_id = LogServiceProxy.getActionIdentifier(agentName,
			"&LOG_ServiceSessionOpening", null, serviceName, serviceType,
			iClesName);
		// On va ouvrir le log d'action
		String[] message_data = new String[7];
		message_data[0] =
			MessageManager.getMessage("&LOG_ServiceSessionOpening");
		message_data[1] = MessageManager.getMessage("&LOG_AgentName") +
			agentName;
		message_data[2] = MessageManager.getMessage("&LOG_UserName") +
			PasswordManager.getInstance().getUserName();
		try
		{
		    message_data[3] = MessageManager.getMessage("&LOG_AgentSessionId") +
				agentSession.getSessionIdentifier();
		}
		catch(Exception e)
		{
		    message_data[3] = MessageManager.getMessage("&LOG_AgentSessionId") +
				"undefined";
		}
		message_data[4] = MessageManager.getMessage("&LOG_ServiceName") +
			serviceName;
		message_data[5] = MessageManager.getMessage("&LOG_IClesName") +
			iClesName;
		message_data[6] = MessageManager.getMessage("&LOG_ServiceType") +
			serviceType;
		LogServiceProxy.addMessageForAction(action_id, message_data);
		// On va ouvrir la session via la m�thode openServiceSession de
		// l'interface AgentSessionInterface
		try
		{
			service_session = agentSession.openServiceSession(action_id,
				iClesName, serviceName, serviceType);
			ServiceSessionProxy proxy =
				new ServiceSessionProxy(service_session);
			message_data = new String[3];
			message_data[0] =
				MessageManager.getMessage("&LOG_SessionOpeningResult");
			message_data[1] =
				MessageManager.getMessage("&LOG_SessionOpeningSuccessfull");
			message_data[2] =
				MessageManager.getMessage("&LOG_ServiceSessionId") +
				proxy.getSessionIdentifier();
			LogServiceProxy.addMessageForAction(action_id, message_data);
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur, on va la transformer en InnerException et
			// la lever
			InnerException inner_exception = CommonFeatures.processException(
				"l'ouverture de session de service", exception);
			// On r�cup�re le tableau du d�tail de l'erreur
			String[] error_message = CommonFeatures.buildArrayFromString(
				inner_exception.getDetails());
			message_data = new String[error_message.length + 3];
			message_data[0] =
				MessageManager.getMessage("&LOG_SessionOpeningResult");
			message_data[1] =
				MessageManager.getMessage("&LOG_SessionOpeningFailed");
			message_data[2] =
				MessageManager.getMessage(inner_exception.getReason());
			for(int index = 0 ; index < error_message.length ; index ++)
			{
				message_data[index + 3] = error_message[index];
			}
			// On log le message
			LogServiceProxy.addMessageForAction(action_id, message_data);
			trace_methods.endOfMethod();
			throw inner_exception;
		}
		// On g�n�re la trace d'ouverture de session
		trace_events.writeTrace("L'utilisateur " +
			PasswordManager.getInstance().getUserName() +
			" a ouvert une session sur le service " + serviceName +
			" de type " + serviceType + " dans le I-CLES " + iClesName);
		trace_methods.endOfMethod();
		return service_session;
	}
}
