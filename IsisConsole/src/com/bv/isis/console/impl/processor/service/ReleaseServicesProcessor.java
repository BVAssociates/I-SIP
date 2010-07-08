/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/service/ReleaseServicesProcessor.java,v $
* $Revision: 1.4 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de libération des environnements de service
* DATE:        25/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.service
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ReleaseServicesProcessor.java,v $
* Revision 1.4  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.3  2008/01/31 16:58:19  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.2  2005/07/01 12:11:35  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/11/02 08:53:08  tz
* Ajout de la classe.
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
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.AgentInterface;

/*----------------------------------------------------------
* Nom: ReleaseServicesProcessor
* 
* Description:
* Cette classe implémente le processeur de tâche chargé de la libération des 
* environnements de service non utilisés sur un Agent. Cette libération 
* consiste à appeler la méthode releaseUnusedServices() sur l'interface 
* AgentInterface de l'Agent.
* Comme le processeur n'est pas graphique, la classe implémente l'interface 
* ProcessorInterface.
* ----------------------------------------------------------*/
public class ReleaseServicesProcessor 
	implements ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ReleaseServicesProcessor
	* 
	* Description:
	* Cette méthode est le constructeur par défaut. Elle n'est présentée que 
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public ReleaseServicesProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ReleaseServicesProcessor", "ReleaseServicesProcessor");
		
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
			"ReleaseServicesProcessor", "preLoad");
		
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
			"ReleaseServicesProcessor", "isConfigured");
		
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
			"ReleaseServicesProcessor", "getConfigurationPanels");
		
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
			"ReleaseServicesProcessor", "isTreeCapable");
		
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
			"ReleaseServicesProcessor", "isTableCapable");
		
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
			"ReleaseServicesProcessor", "isGlobalCapable");
		
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
			"ReleaseServicesProcessor", "getDescription");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return 
			MessageManager.getMessage("&ReleaseServicesProcessorDescription");
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
			"ReleaseServicesProcessor", "getMenuLabel");
		
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
	* releaseUnusedServices() sur l'interface AgentInterface de l'Agent 
	* concerné (l'identité de l'Agent est récupérée depuis les paramètres du 
	* noeud sélectionné).
	* La liste des environnements de service libérés est affichée à 
	* l'utilisateur.
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
			"ReleaseServicesException", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		String agent_name = null;
		GenericTreeObjectNode selected_node = null;
		AgentInterface agent_interface = null;
		String[] released_services = null;
		StringBuffer message = new StringBuffer();

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
		trace_debug.writeTrace("agent_name=" + agent_name);
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
			windowInterface.showPopupForException("&ERR_CannotReleaseServices",
				exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// On va tenter la fermeture
		try
		{
			released_services = agent_interface.releaseUnusedServices();
		}
		catch(Exception exception)
		{
			InnerException inner_exception = 
				CommonFeatures.processException("la libération des " +
				"environnements de service", exception);
			// On va afficher un message à l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotReleaseServices",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// Si aucun environnement n'a été libéré, on créer un fausse liste
		if(released_services == null || released_services.length == 0)
		{
			released_services = new String[1];
			released_services[0] = 
				MessageManager.getMessage("&MSG_NoServiceReleased");
		}
		// On va construire le message de la boîte de dialogue
		message.append(
			MessageManager.getMessage("&MSG_FollowingServicesReleased"));
		for(int index = 0 ; index < released_services.length ; index ++)
		{
			message.append("\n");
			message.append(released_services[index]);
		}
		// On affiche la boîte de dialogue à l'utilisateur
		windowInterface.showPopup("Information", message.toString(), null);
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
			"ReleaseServicesProcessor", "close");
		
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
			"ReleaseServicesProcessor", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new ReleaseServicesProcessor();
	}
}
