/*------------------------------------------------------------
* Copyright (c) 2003 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/openurl/OpenUrlProcessor.java,v $
* $Revision: 1.13 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage de pages web
* DATE:        06/02/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.openurl
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: OpenUrlProcessor.java,v $
* Revision 1.13  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.12  2008/06/12 15:49:20  tz
* Utilisation de LabelFactory.evaluate().
*
* Revision 1.11  2008/02/21 12:09:16  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.10  2008/01/31 16:57:33  tz
* Classe PreprocessingHandler renommée.
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.9  2008/01/29 15:53:41  tz
* Correction de l'appel à la méthode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.8  2006/11/09 12:10:56  tz
* Adaptation à la nouvelle méthode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.7  2005/07/01 12:12:29  tz
* Modification du composant pour les traces
*
* Revision 1.6  2004/10/22 15:37:48  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.5  2004/10/13 13:55:41  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2004/07/29 11:40:22  tz
* Réintégration du composant dans ClientHMI
*
* Revision 1.3  2004/05/24 10:14:39  tz
* Correction fiche Inuit/153
*
* Revision 1.2  2004/02/10 11:09:15  tz
* Modification du traitement du pré-processing.
* Utilisation du caractère ! au lieu de @.
*
* Revision 1.1.1.1  2004/02/09 14:38:00  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.openurl;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.prefs.PreferencesAPI;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import javax.swing.JOptionPane;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.LabelFactory;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;

/*----------------------------------------------------------
* Nom: OpenUrlProcessor
*
* Description:
* Cette classe implémente le processeur de tâche chargé de la récupération et
* de l'affichage de pages web.
* L'URL de la page à affiché est fournie via le paramètre. Il peut s'agir d'une
* URL de type http (web), file (locale) ou même ftp. Voir la description de la
* méthode run() pour plus de détails.
* Ce processeur a besoin d'être configuré pour fonctionner. De ce fait, il vérifie
* qu'il a été configuré à chaque fois qu'il est appelé. S'il n'est pas configuré, 
* il effectue les tâches de configuration relatives au type de plate-forme sur laquelle
* il est exécuté (voir la méthode setConfiguration()).
* ----------------------------------------------------------*/
public class OpenUrlProcessor 
	implements ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: OpenUrlProcessor
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présente que
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public OpenUrlProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "OpenUrlProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette méthode redéfini celle de l'interface ProcessorInterface. Elle est
	* appelée lors de l'initialisation et de l'exécution du processeur.
	* Cette méthode est celle qui est chargée d'effectuer les tâches nécessaires
	* à l'affichage de la page correspondant à l'URL passée en argument.
	* La méthode appelle tout d'abord la méthode getConfiguration() pour récupérer
	* la commande qui doit être exécutée pour afficher la page, puis exécute la
	* commande.
	* Le processeur accepte que l'URL soit issue du pré-processing (valeur calculée
	* ou saisie lors de l'appel).
	*
	* Si une erreur est détectée pendant la phase d'initialisation, l'exception
	* InnerException doit être levée.
	*
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface
	*    permettant au processeur de communiquer avec la fenêtre principale de
	*    l'application. Cet argument ne doit pas être nul,
	*  - menuItem: Une référence sur l'option de menu qui a déclenché
	*    l'exécution du processeur de tâche. Cet attribut peut être nul,
	*  - parameters: Une chaîne de caractères contenant les paramètres
	*    d'exécution du processeur. Cet attribut peut être nul,
	*  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	*  - selectedNode: Une référence sur l'objet graphique sur lequel le
	*    processeur doit exécuter son traitement. Cet attribut peut être nul.
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
			"OpenUrlProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		IndexedList context = null;
		String agent_name = null;
		
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
		windowInterface.setProgressMaximum(3);
		windowInterface.setStatus("&OpenUrl_SM_PreparingExecution", null, 1);
		IsisParameter[] preprocessing_parameters = null;
		// Si aucun noeud n'est sélectionné, on demande à l'utilisateur de saisir
		// une URL
		if(selectedNode == null)
		{
			String the_url = 
				JOptionPane.showInputDialog((Component)windowInterface,
				MessageManager.getMessage("&OpenUrl_Url_Prompt"),
				MessageManager.getMessage("&OpenUrl_DB_Title"),
				JOptionPane.QUESTION_MESSAGE);
			if(the_url == null || the_url.equals("") == true)
			{
				// L'utilisateur a annulé, on sort
				windowInterface.setStatus(null, null, 0);
				trace_methods.endOfMethod();
				return;
			}
			parameters = the_url;
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
				selected_node.getAgentName()), 
				selected_node.getServiceSession(), (Component)windowInterface,
				true);
		}
		// On évalue les variables dans la chaine
		parameters = LabelFactory.evaluate(parameters, 
			preprocessing_parameters, context, agent_name);
		trace_debug.writeTrace("parameters=" + parameters);
		// On remplace toutes les occurences de '!' par ':'
		while(parameters.indexOf('!') >= 0)
		{
			parameters = parameters.replace('!', ':');
		}
		trace_debug.writeTrace("parameters=" + parameters);
		// On récupère la commande à exécuter
		windowInterface.setStatus("&OpenUrl_SM_LoadingConfiguration", null, 2);
		String command = getConfiguration(parameters);
		if(command == null || command.equals("") == true)
		{
			trace_debug.writeTrace("Aucune commande a exécuter");
			windowInterface.showPopup("Error", "&ERR_BrowserNotConfigured", 
				null);
			windowInterface.setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		windowInterface.setStatus("&OpenUrl_SM_StartingExecution", null, 4);
		Runtime runtime = Runtime.getRuntime();
		try
		{
			Trace trace_events = TraceAPI.declareTraceEvents("Console");
			
			trace_events.writeTrace("Exécution de la commande: " + command);
			runtime.exec(command);
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			trace_errors.writeTrace("Erreur lors de l'exécution de la commande :" +
				exception);
			InnerException inner_exception = 
				CommonFeatures.processException("&ERR_CannotExecuteBrowser",
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
	* Cette méthode redéfini celle de l'interface ProcessorInterface. Elle est
	* appelée lorsque l'exécution du processeur doit être arrêtée.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "close");

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
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "preLoad");
			
		trace_methods.beginningOfMethod();
		// On charge le fichier de messages
		MessageManager.loadFile("openurl.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur a été configuré ou s'il a 
	* besoin d'une configuration.
	* La méthode retourne true si la commande à exécuter pour ouvrir un 
	* navigateur externe a été définie.
	* 
	* Retourne: true si le processeur a été configuré, false sinon.
	* ----------------------------------------------------------*/
	public boolean isConfigured()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "isConfigured");
			
		trace_methods.beginningOfMethod();
		String command = getConfiguration("dummy");
		if(command == null || command.equals("") == true)
		{
			trace_methods.endOfMethod();
			return false;
		}
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer les panneaux de configuration du processeur.
	* La méthode va créer une instance de OpenUrlPanel et la retourner.
	* 
	* Retourne: Un tableau contenant uniquement une instance de OpenUrlPanel.
	* ----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "getConfigurationPanels");
		ConfigurationPanelInterface[] panels = new ConfigurationPanelInterface[1];
			
		trace_methods.beginningOfMethod();
		panels[0] = new OpenUrlPanel();
		trace_methods.endOfMethod();
		return panels;
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
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "isTreeCapable");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un élément 
	* d'un tableau.
	* Pour ce processeur, tout type d'invocation est possible.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "isTableCapable");
			
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
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "isGlobalCapable");
			
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
	* ----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "getDescription");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&OpenUrlProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer l'intitulé de l'élément de menu associé.
	* 
	* Retourne: L'intitulé de l'élément de menu.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "getMenuLabel");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&OpenUrlProcessorItemLabel");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance du processeur.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "duplicate");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new OpenUrlProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: getConfiguration
	*
	* Description:
	* Cette méthode retourne la commande devant être exécutée afin d'afficher
	* la page dont l'URL est passée en argument.
	* Elle commence par vérifier que la commande a été configurée, via la classe
	* PreferencesAPI. Ensuite, elle recherche et remplace la chaîne '%[url]' 
	* par la valeur de l'argument dans la ligne de commande.
	* 
	* Arguments:
	*  - url: L'URL a afficher.
	* 
	* Retourne: La commande a exécuter, ou null si la commande n'a pas été
	* configurée.
	* ----------------------------------------------------------*/
	private String getConfiguration(
		String url
		)
	{
		String command = null;
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlProcessor", "getConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("url=" + url);
		// On teste la validité des arguments
		if(url == null || url.equals("") == true)
		{
			trace_errors.writeTrace("L'argument est null !");
			trace_methods.endOfMethod();
			return null;
		}
		// Lecture des préférences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("OpenURL");
			command = preferences.getString("Browser.Command");
		}
		catch(Exception exception)
		{
			// Si on arrive ici, c'est que les préférences n'ont pas été
			// définies, on retourne null.
			trace_methods.endOfMethod();
			return null;
		}
		trace_debug.writeTrace("command=" + command);
		if(command != null)
		{
			// On remplace '%[url]' par l'URL fournie en argument
			int position = command.indexOf("%[url]"); 
			if(position >= 0)
			{
				command = command.substring(0, position) +
					url + command.substring(position + 6);
			}
		}
		trace_methods.endOfMethod();
		return command;
	}
}
