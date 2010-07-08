/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/session/ServiceSessionProcessor.java,v $
* $Revision: 1.20 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'ouverture de session de service
* DATE:        22/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.session
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ServiceSessionProcessor.java,v $
* Revision 1.20  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.19  2008/06/27 09:44:20  tz
* Les donn�es relatives � l'Agent, et au service peuvent provenir du
* contexte, et plus seulement du noeud s�lectionn�.
* Traitement des informations depuis le pr�-processing.
*
* Revision 1.18  2008/01/31 16:58:49  tz
* Classe PreprocessingHandler renomm�e.
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.17  2008/01/29 15:54:03  tz
* Correction de l'appel � la m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.16  2006/11/09 12:11:27  tz
* Adaptation � la nouvelle m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.15  2006/03/07 09:32:34  tz
* Mise � jour par rapport aux �tats du noeud.
*
* Revision 1.14  2005/10/07 08:23:03  tz
* Changement mineur.
*
* Revision 1.13  2005/07/01 12:09:43  tz
* Modification du composant pour les traces
*
* Revision 1.12  2004/11/02 08:51:59  tz
* Gestion de l'�tat du noeud,
* Ajout de la m�thode duplicate().
*
* Revision 1.11  2004/10/22 15:37:15  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.10  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.9  2004/10/13 13:55:26  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.8  2004/07/29 12:02:39  tz
* Suppression d'imports inutiles
*
* Revision 1.7  2003/12/08 14:35:16  tz
* Am�lioration de l'auto-exploration.
*
* Revision 1.6  2003/03/17 16:49:42  tz
* Correction de la fiche Inuit/105
*
* Revision 1.5  2003/03/07 16:20:07  tz
* Prise en compte du m�canisme de log m�tier
* Ajout de l'auto-exploration
*
* Revision 1.4  2002/11/19 08:40:24  tz
* Gestion de la progression de la t�che.
*
* Revision 1.3  2002/09/20 10:47:07  tz
* Utilisation du nom commercial I-SIS
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
import java.awt.Component;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.console.node.MenuFactory;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;

/*----------------------------------------------------------
* Nom: ServiceSessionProcessor
*
* Description:
* Cette classe impl�mente le processeur de t�che ex�cut� lorsqu'une session de
* service doit �tre ouverte sans changement d'Agent.
* Le processeur va tenter d'ouvrir une session sur le service de production.
* Finalement, le processeur va charger les informations relatives � l'acc�s de
* l'utilisateur sur le service.
* Ces informations seront utilis�es pour cr�er un noeud graphique qui sera
* ajout� au noeud s�lectionn� (comme une exploration).
* ----------------------------------------------------------*/
public class ServiceSessionProcessor
	extends AgentSessionProcessor
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ServiceSessionProcessor
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public ServiceSessionProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProcessor", "ServiceSessionProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette m�thode red�fini celle de la classe AgentSessionProcessor. Elle est
	* appel�e afin d'initialiser et d'ex�cuter le processeur.
	* Dans le cas du processeur d'ouverture de session de service, la m�thode
	* commence par v�rifier si le noeud n'a pas d�j� �t� explor�. Si le noeud
	* n'a pas �t� explor�, il ne doit avoir aucun un noeud fils.
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
	*  - parameters: Une cha�ne contenant des param�tres optionnels
	*    du processeur de t�che,
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
		String service_parameter_name = "ServiceName";
		String type_parameter_name = "ServiceType";
		String icles_parameter_name = "ICleName";
		String service_name = null;
		String service_type = null;
		String icles_name = null;
		GenericTreeObjectNode selected_node;
		ServiceSessionInterface service_session = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProcessor", "run");
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
		    service_parameter_name =
				configuration_api.getString("Parameter.ServiceName");
		    type_parameter_name =
				configuration_api.getString("Parameter.ServiceType");
			icles_parameter_name =
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
			// On traite le pr�processing
			preprocessing_parameters =
				ProcessingHandler.handleProcessingStatement(preprocessing, 
				node_context, windowInterface, 
				AgentSessionManager.getInstance().getAgentLayerMode(
				selected_node.getAgentName()), 
				selected_node.getServiceSession(), (Component)windowInterface,
				true);
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
			// On sort
			windowInterface.setStatus(null, null, 0);
			// On positionne l'�tat du noeud
			selected_node.setNodeState(
				GenericTreeObjectNode.NodeStateEnum.CLOSED);
			trace_methods.endOfMethod();
			return;
		}
		// On va ensuite extraire le tableau d'objet du contexte
		IsisParameter[] object_parameters =
			(IsisParameter[])node_context.toArray(new IsisParameter[0]);
		// On r�cup�re les valeurs des param�tres
		service_name = TreeNodeFactory.getValueOfParameter(object_parameters,
			service_parameter_name);
		service_type = TreeNodeFactory.getValueOfParameter(object_parameters,
			type_parameter_name);
		icles_name = TreeNodeFactory.getValueOfParameter(object_parameters, 
			icles_parameter_name);
		trace_debug.writeTrace("service_name=" + service_name);
		trace_debug.writeTrace("service_type=" + service_type);
		trace_debug.writeTrace("icle_name=" + icles_name);
		// On v�rifie que les informations n�cessaire � l'ouverture d'une
		// session de service sont pr�sentes
		if(icles_name == null || icles_name.equals("") == true ||
			service_name == null || service_name.equals("") == true ||
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
		// On positionne l'�tat du noeud
		selected_node.setNodeState(
			GenericTreeObjectNode.NodeStateEnum.STATE_CHANGING);
		// Il faut ouvrir la session sur le service
		windowInterface.setProgressMaximum(2);
		String[] extra_info = { service_name };
		windowInterface.setStatus("&Status_OpeningServiceSession",
			extra_info, 0);
		trace_debug.writeTrace("Ouverture de session de service");
		trace_debug.writeTrace("I-CLES name=" + icles_name);
		trace_debug.writeTrace("ServiceName=" + service_name);
		trace_debug.writeTrace("ServiceType=" + service_type);
		windowInterface.setStatus("&Status_OpeningServiceSession",
			extra_info, 1);
		try
		{
			// On ouvre la session sur le service
			service_session = openServiceSession(selected_node.getAgentName(),
				selected_node.getServiceSession(), icles_name, service_name,
				service_type);
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
			// On sort
			windowInterface.setStatus(null, null, 0);
			// On positionne l'�tat du noeud
			selected_node.setNodeState(
				GenericTreeObjectNode.NodeStateEnum.CLOSED);
			trace_methods.endOfMethod();
			return;
		}
		// On va maintenant cr�er le noeud correspondant au service
		try
		{
			makeServiceNode(selected_node.getAgentName(), service_name,
				service_type, icles_name, service_session, selected_node, true,
				false, preprocessing_parameters, windowInterface,
				selected_node.getAgentSessionId(), node_context);
			windowInterface.setStatus("&Status_OpeningServiceSession",
				extra_info, 2);
			// Il faut avertir l'arbre d'exploration que la structure du noeud p�re
			// a chang�
			windowInterface.getTreeInterface().nodeStructureChanged(
				selected_node);
			// On va �tendre l'arbre
			windowInterface.getTreeInterface().expandNode(selected_node);
			// Appel de la m�thode d'exploration automatique
			if(selected_node.getChildCount() == 1)
			{
			    MenuFactory.doAutomaticExplore(windowInterface,
					(GenericTreeObjectNode)selected_node.getChildAt(0), true, 
					1);
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
			// Il faut fermer la session de service
			try
			{
				ServiceSessionProxy session_proxy =
					new ServiceSessionProxy(service_session);
				session_proxy.close(selected_node.getAgentName(),
					selected_node.getServiceName(),
					selected_node.getServiceType(),
					selected_node.getIClesName());
			}
			catch(Exception another_exception)
			{
				trace_errors.writeTrace(
					"Erreur lors de la fermeture de la session: " +
					another_exception.getMessage());
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
		selected_node.setNodeState(GenericTreeObjectNode.NodeStateEnum.OPENED);
		windowInterface.setConnected(true, false);
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe AgentSessionProcessor.
	* Elle retourne true car le processeur n'a pas besoin de configuration.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isConfigured()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProcessor", "isConfigured");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe AgentSessionProcessor.
	* Elle retourne null car le processeur n'a pas besoin de configuration.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProcessor", "getConfigurationPanels");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe AgentSessionProcessor. 
	* Elle est appel�e pour r�cup�rer un double du processeur.
	* 
	* Retourne: Une nouvelle instance du processeur.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new ServiceSessionProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: openServiceSession
	*
	* Description:
	* Cette m�thode permet d'ouvrir une session de service � partir d'une autre
	* session de service. L'ouverture de session de service est effectu�e via
	* la m�thode openServiceSession() de la classe ServiceSessionProxy.
	*
	* Si un probl�me survient lors de l'ouverture de la session de service,
	* l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - agentName: Le nom de l'agent,
	*  - serviceSession: Une r�f�rence sur la session de service � partir de
	*    laquelle la nouvelle session de service doit �tre ouverte,
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
		ServiceSessionInterface serviceSession,
		String iClesName,
		String serviceName,
		String serviceType
		)
		throws
			InnerException
	{
		ServiceSessionInterface service_session = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProcessor", "openServiceSession");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceName=" + serviceName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		// On va ouvrir un log pour l'action
		String action_id = LogServiceProxy.getActionIdentifier(agentName,
			"&LOG_ServiceSessionOpening", null, serviceName, serviceType,
			iClesName);
		ServiceSessionProxy session_proxy =
			new ServiceSessionProxy(serviceSession);
		// On va ouvrir le log d'action
		String[] message_data = new String[7];
		message_data[0] =
			MessageManager.getMessage("&LOG_ServiceSessionOpening");
		message_data[1] = MessageManager.getMessage("&LOG_AgentName") +
			agentName;
		message_data[2] = MessageManager.getMessage("&LOG_UserName") +
			PasswordManager.getInstance().getUserName();
		message_data[3] = MessageManager.getMessage("&LOG_ServiceSessionId") +
			session_proxy.getSessionIdentifier();
		message_data[4] = MessageManager.getMessage("&LOG_ServiceName") +
			serviceName;
		message_data[5] = MessageManager.getMessage("&LOG_IClesName") +
			iClesName;
		message_data[6] = MessageManager.getMessage("&LOG_ServiceType") +
			serviceType;
		LogServiceProxy.addMessageForAction(action_id, message_data);
		// On va ouvrir la session via la m�thode openServiceSession de
		// la classe ServiceSessionProxy
		try
		{
			service_session = session_proxy.openServiceSession(action_id,
				iClesName, serviceName, serviceType);
			// On va logguer le r�sultat
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
		catch(InnerException exception)
		{
			// On va logguer l'erreur
			// On r�cup�re le tableau du d�tail de l'erreur
			String[] error_message = CommonFeatures.buildArrayFromString(
				exception.getDetails());
			message_data = new String[error_message.length + 3];
			message_data[0] =
				MessageManager.getMessage("&LOG_SessionOpeningResult");
			message_data[1] =
				MessageManager.getMessage("&LOG_SessionOpeningFailed");
			message_data[2] = MessageManager.getMessage(exception.getReason());
			for(int index = 0 ; index < error_message.length ; index ++)
			{
				message_data[index + 3] = error_message[index];
			}
			// On log le message
			LogServiceProxy.addMessageForAction(action_id, message_data);
			throw exception;
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