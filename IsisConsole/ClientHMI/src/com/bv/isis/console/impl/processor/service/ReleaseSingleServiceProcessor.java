/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/service/ReleaseSingleServiceProcessor.java,v $
* $Revision: 1.4 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de libération de l'environnement d'un service
* DATE:        04/07/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.service
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ReleaseSingleServiceProcessor.java,v $
* Revision 1.4  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.3  2008/01/31 16:58:26  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.2  2006/10/13 15:13:03  tz
* Correction d'un tag CVS.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.service;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.config.ConfigurationAPI;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.PortalInterfaceProxy;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.AgentInterface;

/*----------------------------------------------------------
* Nom: ReleaseSingleServiceProcessor
* 
* Description:
* Cette classe implémente le processeur de tâche chargé de la libération d'un 
* environnement de service non utilisé sur un Agent. Cette libération consiste 
* à appeler la méthode releaseService() sur l'interface AgentInterface de 
* l'Agent, à partir du nom du I-CLES, du nom et du type du service.
* Comme le processeur n'est pas graphique, la classe implémente l'interface 
* ProcessorInterface.
* ----------------------------------------------------------*/
public class ReleaseSingleServiceProcessor 
	implements ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ReleaseSingleServiceProcessor
	* 
	* Description:
	* Cette méthode est le constructeur par défaut. Elle n'est présentée que 
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public ReleaseSingleServiceProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ReleaseSingleServiceProcessor", "ReleaseSingleServiceProcessor");
		
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
			"ReleaseSingleServiceProcessor", "preLoad");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processor a été configuré, si besoin est.
	* Pour ce processeur, aucune configuration n'est nécessaire.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isConfigured()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ReleaseSingleServiceProcessor", "isConfigured");
		
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
			"ReleaseSingleServiceProcessor", "getConfigurationPanels");
		
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
	* Pour ce processeur, seule l'invocation via le menu "Outils" n'est pas 
	* autorisée.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ReleaseSingleServiceProcessor", "isTreeCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processor peut être invoqué via un élément 
	* de tableau.
	* Pour ce processeur, seule l'invocation via le menu "Outils" n'est pas 
	* autorisée.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ReleaseSingleServiceProcessor", "isTableCapable");
		
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
	* Pour ce processeur, seule l'invocation via le menu "Outils" n'est pas 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ReleaseSingleServiceProcessor", "isGlobalCapable");
		
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
			"ReleaseSingleServiceProcessor", "getDescription");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage(
			"&ReleaseSingleServiceProcessorDescription");
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
			"ReleaseSingleServiceProcessor", "getMenuLabel");
		
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
	* Pour ce processeur, l'exécution consiste à appeler la méthode 
	* releaseService() sur l'interface AgentInterface de l'Agent concerné avec 
	* le nom du I-CLES, le nom et le type du service (récupérés depuis les 
	* paramètres du noeud sélectionné).
	* 
	* Si un problème est détecté lors de l'exécution du processeur, 
	* l'exception InnerException est levée.
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
			"ReleaseSingleServiceProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String service_parameter_name = "ServiceName";
		String type_parameter_name = "ServiceType";
		String icle_parameter_name = "ICleName";
		String agent_name = null;
		String icles_name = null;
		String service_name = null;
		String service_type = null;
		GenericTreeObjectNode selected_node = null;
		AgentInterface agent_interface = null;
		boolean service_released = false;

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
		// On caste le noeud en GenericTreeObjectNode
		selected_node = (GenericTreeObjectNode)selectedNode;
		// On récupère le nom de l'agent
		agent_name = selected_node.getAgentName();
		// Il faut récupérer les noms des paramètres indiquant:
		// - Le nom du service,
		// - Le type du service,
		// - Le nom du I-CLES
		try
		{
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			service_parameter_name =
				configuration_api.getString("Parameter.ServiceName");
			type_parameter_name =
				configuration_api.getString("Parameter.ServiceType");
			icle_parameter_name =
				configuration_api.getString("Parameter.IClesName");
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur, il faut afficher l'erreur à l'utilisateur
			InnerException inner_exception = CommonFeatures.processException(
				"de la récupération de la configuration", exception);
			windowInterface.showPopupForException(
				"&ERR_CannotReleaseSingleService", inner_exception);
			// On sort
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On récupère les paramètres du noeud sélectionné
		IndexedList context = selected_node.getContext(true);
		// On récupère les valeurs des paramètres
		service_name = TreeNodeFactory.getValueOfParameter(context,
			service_parameter_name);
		service_type = TreeNodeFactory.getValueOfParameter(context,
			type_parameter_name);
		icles_name = TreeNodeFactory.getValueOfParameter(context, 
			icle_parameter_name);
		trace_debug.writeTrace("agent_name=" + agent_name);
		trace_debug.writeTrace("service_name=" + service_name);
		trace_debug.writeTrace("service_type=" + service_type);
		trace_debug.writeTrace("icle_name=" + icles_name);
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
			windowInterface.showPopupForException(
				"&ERR_CannotReleaseSingleService", exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On va tenter la fermeture
		try
		{
			service_released = agent_interface.releaseService(icles_name,
				service_type, service_name);
		}
		catch(Exception exception)
		{
			InnerException inner_exception = 
				CommonFeatures.processException("la libération de " +
				"l'environnement du service", exception);
			// On va afficher un message à l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotReleaseSingleService",
				inner_exception);
			trace_methods.endOfMethod();
			return;
		}
		if(service_released == false)
		{
			// Si l'environnement n'a pas été libéré, on va afficher un message
			String message = MessageManager.getMessage("&MSG_SingleServiceNotReleased");
			// On affiche la boîte de dialogue à l'utilisateur
			windowInterface.showPopup("Information", message, null);
		}
		else
		{
			// Si l'environnement a été libéré, on va afficher un message
			String message = MessageManager.getMessage("&MSG_SingleServiceReleased");
			// On affiche la boîte de dialogue à l'utilisateur
			windowInterface.showPopup("Information", message, null);
		}
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
			"ReleaseSingleServiceProcessor", "close");
		
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
	* Retourne: Une nouvelle instance de ReleaseServicesProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ReleaseSingleServiceProcessor", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new ReleaseSingleServiceProcessor();
	}
}
