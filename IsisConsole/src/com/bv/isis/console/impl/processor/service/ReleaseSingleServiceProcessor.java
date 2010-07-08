/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/service/ReleaseSingleServiceProcessor.java,v $
* $Revision: 1.4 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de lib�ration de l'environnement d'un service
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
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.2  2006/10/13 15:13:03  tz
* Correction d'un tag CVS.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.service;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur de t�che charg� de la lib�ration d'un 
* environnement de service non utilis� sur un Agent. Cette lib�ration consiste 
* � appeler la m�thode releaseService() sur l'interface AgentInterface de 
* l'Agent, � partir du nom du I-CLES, du nom et du type du service.
* Comme le processeur n'est pas graphique, la classe impl�mente l'interface 
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
	* Cette m�thode est le constructeur par d�faut. Elle n'est pr�sent�e que 
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e afin que le processeur effectue un pr�-chargement de ses 
	* donn�es.
	* Pour ce processeur, aucun pr�-chargement n'est n�cessaire.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processor a �t� configur�, si besoin est.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
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
			"ReleaseSingleServiceProcessor", "getConfigurationPanels");
		
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
	* Pour ce processeur, seule l'invocation via le menu "Outils" n'est pas 
	* autoris�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processor peut �tre invoqu� via un �l�ment 
	* de tableau.
	* Pour ce processeur, seule l'invocation via le menu "Outils" n'est pas 
	* autoris�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processor peut �tre invoqu� via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via le menu "Outils" n'est pas 
	* autoris�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle est 
	* appel�e pour r�cup�rer la description du processeur.
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
			"ReleaseSingleServiceProcessor", "getMenuLabel");
		
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
	* Pour ce processeur, l'ex�cution consiste � appeler la m�thode 
	* releaseService() sur l'interface AgentInterface de l'Agent concern� avec 
	* le nom du I-CLES, le nom et le type du service (r�cup�r�s depuis les 
	* param�tres du noeud s�lectionn�).
	* 
	* Si un probl�me est d�tect� lors de l'ex�cution du processeur, 
	* l'exception InnerException est lev�e.
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
		// Tout d'abord, on v�rifie l'int�grit� des param�tres
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
		// On r�cup�re le nom de l'agent
		agent_name = selected_node.getAgentName();
		// Il faut r�cup�rer les noms des param�tres indiquant:
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
			// Il y a eu une erreur, il faut afficher l'erreur � l'utilisateur
			InnerException inner_exception = CommonFeatures.processException(
				"de la r�cup�ration de la configuration", exception);
			windowInterface.showPopupForException(
				"&ERR_CannotReleaseSingleService", inner_exception);
			// On sort
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re les param�tres du noeud s�lectionn�
		IndexedList context = selected_node.getContext(true);
		// On r�cup�re les valeurs des param�tres
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
				CommonFeatures.processException("la lib�ration de " +
				"l'environnement du service", exception);
			// On va afficher un message � l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotReleaseSingleService",
				inner_exception);
			trace_methods.endOfMethod();
			return;
		}
		if(service_released == false)
		{
			// Si l'environnement n'a pas �t� lib�r�, on va afficher un message
			String message = MessageManager.getMessage("&MSG_SingleServiceNotReleased");
			// On affiche la bo�te de dialogue � l'utilisateur
			windowInterface.showPopup("Information", message, null);
		}
		else
		{
			// Si l'environnement a �t� lib�r�, on va afficher un message
			String message = MessageManager.getMessage("&MSG_SingleServiceReleased");
			// On affiche la bo�te de dialogue � l'utilisateur
			windowInterface.showPopup("Information", message, null);
		}
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
			"ReleaseSingleServiceProcessor", "close");
		
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
