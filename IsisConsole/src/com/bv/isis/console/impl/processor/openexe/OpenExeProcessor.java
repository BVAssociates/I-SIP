/*------------------------------------------------------------
* Copyright (c) 2008 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/openexe/OpenExeProcessor.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'exécution de commande externe
* DATE:        23/05/2008
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.openexe
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: OpenExeProcessor.java,v $
* Revision 1.2  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.1  2008/06/12 15:47:49  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.openexe;

//
//Imports système
//
import java.awt.Component;

import com.bv.core.message.MessageManager;
import com.bv.core.util.StringCODEC;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.LabelFactory;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.corbacom.IsisParameter;

/*----------------------------------------------------------
* Nom: OpenExeProcessor
* 
* Description:
* Cette classe implémente le processeur de tâche chargé de l'exécution 
* d'une commande quelconque au niveau du poste de travail.
* La commande et ses arguments sont passés en tant qu'arguments au niveau 
* du processor. Les arguments peuvent faire référence à des variables du 
* contexte du noeud, qui seront remplacées avant l'invocation de la 
* commande.
----------------------------------------------------------*/
public class OpenExeProcessor 
	implements ProcessorInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: OpenExeProcessor
	* 
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présente 
	* que pour des raisons de lisibilité.
	----------------------------------------------------------*/
	public OpenExeProcessor() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "OpenExeProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfini celle de l'interface ProcessorInterface. Elle est 
	* appelée lors de l'initialisation et de l'exécution du processeur.
	* Cette méthode est celle qui est chargée d'effectuer les tâches 
	* nécessaires à la préparation de la commande, puis l'exécute.
	* Le processeur accepte que la commande soit issue du pré-processing 
	* (valeur calculée ou saisie lors de l'appel).
	* 
	* Si une erreur est détectée pendant la phase d'initialisation, 
	* l'exception InnerException doit être levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    permettant au processeur de communiquer avec la fenêtre principale 
	*    de l'application. Cet argument ne doit pas être nul,
	*  - menuItem: Une référence sur l'option de menu qui a déclenché 
	*    l'exécution du processeur de tâche. Cet attribut peut être nul,
	*  - parameters: Une chaîne de caractères contenant les paramètres 
	*    d'exécution du processeur. Cet attribut peut être nul,
	*  - preprocessing: Une chaîne contenant des instructions de 
	*    préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de 
	*    postprocessing,
	*  - selectedNode: Une référence sur l'objet graphique sur lequel le 
	*    processeur doit exécuter son traitement. Cet attribut peut être nul.
	* 
	* Lève: InnerException.
	----------------------------------------------------------*/
	public void run(
		MainWindowInterface windowInterface,
		JMenuItem menuItem,
		String parameters,
		String preprocessing,
		String postprocessing,
		DefaultMutableTreeNode selectedNode
		)
		throws
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		IndexedList context = null;
		String agent_name = null;
		IsisParameter[] preprocessing_parameters = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Tout d'abord, on vérifie l'intégrité des paramètres
		if(windowInterface == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Un des arguments n'est pas valide !");
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On définit les étapes
		windowInterface.setProgressMaximum(2);
		windowInterface.setStatus("&OpenExe_SM_PreparingExecution", null, 1);
		// Si aucun noeud n'est sélectionné, on demande à l'utilisateur de saisir
		// une URL
		if(selectedNode == null)
		{
			String the_command = 
				JOptionPane.showInputDialog((Component)windowInterface,
				MessageManager.getMessage("&OpenExe_Exe_Prompt"),
				MessageManager.getMessage("&OpenExe_DB_Title"),
				JOptionPane.QUESTION_MESSAGE);
			if(the_command == null || the_command.equals("") == true)
			{
				// L'utilisateur a annulé, on sort
				windowInterface.setStatus(null, null, 0);
				trace_methods.endOfMethod();
				return;
			}
			parameters = the_command;
		}
		else
		{
			// On récupère le contexte
			GenericTreeObjectNode selected_node = 
				(GenericTreeObjectNode)selectedNode; 
			context = selected_node.getContext(true);
			agent_name = selected_node.getAgentName();
			// On traite le préprocessing
			preprocessing_parameters =
				ProcessingHandler.handleProcessingStatement(preprocessing, 
				context, windowInterface, 
				AgentSessionManager.getInstance().getAgentLayerMode(
				agent_name), selected_node.getServiceSession(), 
				(Component)windowInterface, true);
		}
		// On ajoute le mot de passe sous forme de variable
		PasswordManager manager = PasswordManager.getInstance();
		context.put("Password", new IsisParameter("Password",
			StringCODEC.decodeString(manager.getPassword(agent_name)), '"'));
		// On évalue éventuellement les paramètres dans la commande
		parameters = LabelFactory.evaluate(parameters, 
			preprocessing_parameters, context, agent_name);
		trace_debug.writeTrace("parameters=" + parameters);
		windowInterface.setStatus("&OpenExe_SM_StartingExecution", null, 2);
		Runtime runtime = Runtime.getRuntime();
		try
		{
			Trace trace_events = TraceAPI.declareTraceEvents("Console");
			
			trace_events.writeTrace("Exécution de la commande: " + parameters);
			runtime.exec(parameters);
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			trace_errors.writeTrace("Erreur lors de l'exécution de la commande :" +
				exception);
			InnerException inner_exception = 
				CommonFeatures.processException("&ERR_CannotExecuteCommand",
				exception);
			windowInterface.setStatus(null, null, 0);
			throw inner_exception;
		}
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette méthode redéfini celle de l'interface ProcessorInterface. Elle 
	* est appelée lorsque l'exécution du processeur doit être arrêtée.
	----------------------------------------------------------*/
	public void close() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "close");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour effectuer le pré-chargement du processeur.
	* Elle charge un fichier de messages.
	----------------------------------------------------------*/
	public void preLoad() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// On charge le fichier de messages
		MessageManager.loadFile("openexe.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur a été configuré ou s'il a 
	* besoin d'une configuration.
	* Le processeur n'ayant pas besoin de configuration, cette méthode 
	* retourne true.
	* 
	* Retourne: true.
	----------------------------------------------------------*/
	public boolean isConfigured() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "isConfigured");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer les panneaux de configuration du processeur.
	* Pour ce processeur, aucune configuration n'est nécessaire.
	* 
	* Retourne: null.
	----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "getConfigurationPanels");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un noeud 
	* d'exploration.
	* Pour ce processeur, tout type d'invocation est possible.
	* 
	* Retourne: true.
	----------------------------------------------------------*/
	public boolean isTreeCapable() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "isTreeCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un 
	* élément d'un tableau.
	* Pour ce processeur, tout type d'invocation est possible.
	* 
	* Retourne: true.
	----------------------------------------------------------*/
	public boolean isTableCapable() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "isTableCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur peut être invoqué hors d'un 
	* noeud d'exploration ou d'un élément d'un tableau.
	* Pour ce processeur, tout type d'invocation est possible.
	* 
	* Retourne: true.
	----------------------------------------------------------*/
	public boolean isGlobalCapable() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "isGlobalCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer la description du processeur.
	* 
	* Retourne: La description du processeur.
	----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&OpenExeProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer l'intitulé de l'élément de menu associé.
	* 
	* Retourne: L'intitulé de l'élément de menu.
	----------------------------------------------------------*/
	public String getMenuLabel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "getMenuLabel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&OpenExeProcessorItemLabel");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance du processeur.
	----------------------------------------------------------*/
	public ProcessorInterface duplicate() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenExeProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new OpenExeProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
