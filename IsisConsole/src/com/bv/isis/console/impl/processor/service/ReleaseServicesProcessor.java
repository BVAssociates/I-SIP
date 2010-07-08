/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/service/ReleaseServicesProcessor.java,v $
* $Revision: 1.4 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de lib�ration des environnements de service
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
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.2  2005/07/01 12:11:35  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/11/02 08:53:08  tz
* Ajout de la classe.
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
* Cette classe impl�mente le processeur de t�che charg� de la lib�ration des 
* environnements de service non utilis�s sur un Agent. Cette lib�ration 
* consiste � appeler la m�thode releaseUnusedServices() sur l'interface 
* AgentInterface de l'Agent.
* Comme le processeur n'est pas graphique, la classe impl�mente l'interface 
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
	* Cette m�thode est le constructeur par d�faut. Elle n'est pr�sent�e que 
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e afin que le processeur effectue un pr�-chargement de ses 
	* donn�es.
	* Pour ce processeur, aucun pr�-chargement n'est n�cessaire.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processor a �t� configur�, si besoin est.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
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
			"ReleaseServicesProcessor", "getConfigurationPanels");
		
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
			"ReleaseServicesProcessor", "isTreeCapable");
		
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
			"ReleaseServicesProcessor", "isTableCapable");
		
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
			"ReleaseServicesProcessor", "isGlobalCapable");
		
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
			"ReleaseServicesProcessor", "getMenuLabel");
		
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
	* releaseUnusedServices() sur l'interface AgentInterface de l'Agent 
	* concern� (l'identit� de l'Agent est r�cup�r�e depuis les param�tres du 
	* noeud s�lectionn�).
	* La liste des environnements de service lib�r�s est affich�e � 
	* l'utilisateur.
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
		trace_debug.writeTrace("agent_name=" + agent_name);
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
				CommonFeatures.processException("la lib�ration des " +
				"environnements de service", exception);
			// On va afficher un message � l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotReleaseServices",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		// Si aucun environnement n'a �t� lib�r�, on cr�er un fausse liste
		if(released_services == null || released_services.length == 0)
		{
			released_services = new String[1];
			released_services[0] = 
				MessageManager.getMessage("&MSG_NoServiceReleased");
		}
		// On va construire le message de la bo�te de dialogue
		message.append(
			MessageManager.getMessage("&MSG_FollowingServicesReleased"));
		for(int index = 0 ; index < released_services.length ; index ++)
		{
			message.append("\n");
			message.append(released_services[index]);
		}
		// On affiche la bo�te de dialogue � l'utilisateur
		windowInterface.showPopup("Information", message.toString(), null);
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
			"ReleaseServicesProcessor", "close");
		
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
			"ReleaseServicesProcessor", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new ReleaseServicesProcessor();
	}
}
