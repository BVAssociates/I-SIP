/*------------------------------------------------------------
* Copyright (c) 2008 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/openexe/OpenExeProcessor.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'ex�cution de commande externe
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
// D�claration du package
package com.bv.isis.console.impl.processor.openexe;

//
//Imports syst�me
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
* Cette classe impl�mente le processeur de t�che charg� de l'ex�cution 
* d'une commande quelconque au niveau du poste de travail.
* La commande et ses arguments sont pass�s en tant qu'arguments au niveau 
* du processor. Les arguments peuvent faire r�f�rence � des variables du 
* contexte du noeud, qui seront remplac�es avant l'invocation de la 
* commande.
----------------------------------------------------------*/
public class OpenExeProcessor 
	implements ProcessorInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: OpenExeProcessor
	* 
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sente 
	* que pour des raisons de lisibilit�.
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
	* Cette m�thode red�fini celle de l'interface ProcessorInterface. Elle est 
	* appel�e lors de l'initialisation et de l'ex�cution du processeur.
	* Cette m�thode est celle qui est charg�e d'effectuer les t�ches 
	* n�cessaires � la pr�paration de la commande, puis l'ex�cute.
	* Le processeur accepte que la commande soit issue du pr�-processing 
	* (valeur calcul�e ou saisie lors de l'appel).
	* 
	* Si une erreur est d�tect�e pendant la phase d'initialisation, 
	* l'exception InnerException doit �tre lev�e.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface 
	*    permettant au processeur de communiquer avec la fen�tre principale 
	*    de l'application. Cet argument ne doit pas �tre nul,
	*  - menuItem: Une r�f�rence sur l'option de menu qui a d�clench� 
	*    l'ex�cution du processeur de t�che. Cet attribut peut �tre nul,
	*  - parameters: Une cha�ne de caract�res contenant les param�tres 
	*    d'ex�cution du processeur. Cet attribut peut �tre nul,
	*  - preprocessing: Une cha�ne contenant des instructions de 
	*    pr�processing,
	*  - postprocessing: Une cha�ne contenant des instructions de 
	*    postprocessing,
	*  - selectedNode: Une r�f�rence sur l'objet graphique sur lequel le 
	*    processeur doit ex�cuter son traitement. Cet attribut peut �tre nul.
	* 
	* L�ve: InnerException.
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
		// Tout d'abord, on v�rifie l'int�grit� des param�tres
		if(windowInterface == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Un des arguments n'est pas valide !");
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On d�finit les �tapes
		windowInterface.setProgressMaximum(2);
		windowInterface.setStatus("&OpenExe_SM_PreparingExecution", null, 1);
		// Si aucun noeud n'est s�lectionn�, on demande � l'utilisateur de saisir
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
				// L'utilisateur a annul�, on sort
				windowInterface.setStatus(null, null, 0);
				trace_methods.endOfMethod();
				return;
			}
			parameters = the_command;
		}
		else
		{
			// On r�cup�re le contexte
			GenericTreeObjectNode selected_node = 
				(GenericTreeObjectNode)selectedNode; 
			context = selected_node.getContext(true);
			agent_name = selected_node.getAgentName();
			// On traite le pr�processing
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
		// On �value �ventuellement les param�tres dans la commande
		parameters = LabelFactory.evaluate(parameters, 
			preprocessing_parameters, context, agent_name);
		trace_debug.writeTrace("parameters=" + parameters);
		windowInterface.setStatus("&OpenExe_SM_StartingExecution", null, 2);
		Runtime runtime = Runtime.getRuntime();
		try
		{
			Trace trace_events = TraceAPI.declareTraceEvents("Console");
			
			trace_events.writeTrace("Ex�cution de la commande: " + parameters);
			runtime.exec(parameters);
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			trace_errors.writeTrace("Erreur lors de l'ex�cution de la commande :" +
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
	* Cette m�thode red�fini celle de l'interface ProcessorInterface. Elle 
	* est appel�e lorsque l'ex�cution du processeur doit �tre arr�t�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour effectuer le pr�-chargement du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur a �t� configur� ou s'il a 
	* besoin d'une configuration.
	* Le processeur n'ayant pas besoin de configuration, cette m�thode 
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer les panneaux de configuration du processeur.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un noeud 
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un 
	* �l�ment d'un tableau.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� hors d'un 
	* noeud d'exploration ou d'un �l�ment d'un tableau.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer la description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer l'intitul� de l'�l�ment de menu associ�.
	* 
	* Retourne: L'intitul� de l'�l�ment de menu.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
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
