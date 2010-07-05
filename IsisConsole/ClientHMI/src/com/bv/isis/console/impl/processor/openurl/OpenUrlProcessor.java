/*------------------------------------------------------------
* Copyright (c) 2003 par BV Associates. Tous droits r�serv�s.
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
* Classe PreprocessingHandler renomm�e.
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.9  2008/01/29 15:53:41  tz
* Correction de l'appel � la m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.8  2006/11/09 12:10:56  tz
* Adaptation � la nouvelle m�thode
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
* Mise � jour du mod�le.
*
* Revision 1.4  2004/07/29 11:40:22  tz
* R�int�gration du composant dans ClientHMI
*
* Revision 1.3  2004/05/24 10:14:39  tz
* Correction fiche Inuit/153
*
* Revision 1.2  2004/02/10 11:09:15  tz
* Modification du traitement du pr�-processing.
* Utilisation du caract�re ! au lieu de @.
*
* Revision 1.1.1.1  2004/02/09 14:38:00  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.openurl;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur de t�che charg� de la r�cup�ration et
* de l'affichage de pages web.
* L'URL de la page � affich� est fournie via le param�tre. Il peut s'agir d'une
* URL de type http (web), file (locale) ou m�me ftp. Voir la description de la
* m�thode run() pour plus de d�tails.
* Ce processeur a besoin d'�tre configur� pour fonctionner. De ce fait, il v�rifie
* qu'il a �t� configur� � chaque fois qu'il est appel�. S'il n'est pas configur�, 
* il effectue les t�ches de configuration relatives au type de plate-forme sur laquelle
* il est ex�cut� (voir la m�thode setConfiguration()).
* ----------------------------------------------------------*/
public class OpenUrlProcessor 
	implements ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: OpenUrlProcessor
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sente que
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�fini celle de l'interface ProcessorInterface. Elle est
	* appel�e lors de l'initialisation et de l'ex�cution du processeur.
	* Cette m�thode est celle qui est charg�e d'effectuer les t�ches n�cessaires
	* � l'affichage de la page correspondant � l'URL pass�e en argument.
	* La m�thode appelle tout d'abord la m�thode getConfiguration() pour r�cup�rer
	* la commande qui doit �tre ex�cut�e pour afficher la page, puis ex�cute la
	* commande.
	* Le processeur accepte que l'URL soit issue du pr�-processing (valeur calcul�e
	* ou saisie lors de l'appel).
	*
	* Si une erreur est d�tect�e pendant la phase d'initialisation, l'exception
	* InnerException doit �tre lev�e.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface
	*    permettant au processeur de communiquer avec la fen�tre principale de
	*    l'application. Cet argument ne doit pas �tre nul,
	*  - menuItem: Une r�f�rence sur l'option de menu qui a d�clench�
	*    l'ex�cution du processeur de t�che. Cet attribut peut �tre nul,
	*  - parameters: Une cha�ne de caract�res contenant les param�tres
	*    d'ex�cution du processeur. Cet attribut peut �tre nul,
	*  - preprocessing: Une cha�ne contenant des instructions de pr�processing,
	*  - postprocessing: Une cha�ne contenant des instructions de postprocessing,
	*  - selectedNode: Une r�f�rence sur l'objet graphique sur lequel le
	*    processeur doit ex�cuter son traitement. Cet attribut peut �tre nul.
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
		// Tout d'abord, on v�rifie l'int�grit� des param�tres
		if(windowInterface == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Un des arguments n'est pas valide !");
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On d�finit les �tapes
		windowInterface.setProgressMaximum(3);
		windowInterface.setStatus("&OpenUrl_SM_PreparingExecution", null, 1);
		IsisParameter[] preprocessing_parameters = null;
		// Si aucun noeud n'est s�lectionn�, on demande � l'utilisateur de saisir
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
				// L'utilisateur a annul�, on sort
				windowInterface.setStatus(null, null, 0);
				trace_methods.endOfMethod();
				return;
			}
			parameters = the_url;
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
				selected_node.getAgentName()), 
				selected_node.getServiceSession(), (Component)windowInterface,
				true);
		}
		// On �value les variables dans la chaine
		parameters = LabelFactory.evaluate(parameters, 
			preprocessing_parameters, context, agent_name);
		trace_debug.writeTrace("parameters=" + parameters);
		// On remplace toutes les occurences de '!' par ':'
		while(parameters.indexOf('!') >= 0)
		{
			parameters = parameters.replace('!', ':');
		}
		trace_debug.writeTrace("parameters=" + parameters);
		// On r�cup�re la commande � ex�cuter
		windowInterface.setStatus("&OpenUrl_SM_LoadingConfiguration", null, 2);
		String command = getConfiguration(parameters);
		if(command == null || command.equals("") == true)
		{
			trace_debug.writeTrace("Aucune commande a ex�cuter");
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
			
			trace_events.writeTrace("Ex�cution de la commande: " + command);
			runtime.exec(command);
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			trace_errors.writeTrace("Erreur lors de l'ex�cution de la commande :" +
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
	* Cette m�thode red�fini celle de l'interface ProcessorInterface. Elle est
	* appel�e lorsque l'ex�cution du processeur doit �tre arr�t�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour effectuer le pr�-chargement du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur a �t� configur� ou s'il a 
	* besoin d'une configuration.
	* La m�thode retourne true si la commande � ex�cuter pour ouvrir un 
	* navigateur externe a �t� d�finie.
	* 
	* Retourne: true si le processeur a �t� configur�, false sinon.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer les panneaux de configuration du processeur.
	* La m�thode va cr�er une instance de OpenUrlPanel et la retourner.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un noeud 
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un �l�ment 
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� hors d'un 
	* noeud d'exploration ou d'un �l�ment d'un tableau.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer la description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer l'intitul� de l'�l�ment de menu associ�.
	* 
	* Retourne: L'intitul� de l'�l�ment de menu.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
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
	* Cette m�thode retourne la commande devant �tre ex�cut�e afin d'afficher
	* la page dont l'URL est pass�e en argument.
	* Elle commence par v�rifier que la commande a �t� configur�e, via la classe
	* PreferencesAPI. Ensuite, elle recherche et remplace la cha�ne '%[url]' 
	* par la valeur de l'argument dans la ligne de commande.
	* 
	* Arguments:
	*  - url: L'URL a afficher.
	* 
	* Retourne: La commande a ex�cuter, ou null si la commande n'a pas �t�
	* configur�e.
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
		// On teste la validit� des arguments
		if(url == null || url.equals("") == true)
		{
			trace_errors.writeTrace("L'argument est null !");
			trace_methods.endOfMethod();
			return null;
		}
		// Lecture des pr�f�rences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("OpenURL");
			command = preferences.getString("Browser.Command");
		}
		catch(Exception exception)
		{
			// Si on arrive ici, c'est que les pr�f�rences n'ont pas �t�
			// d�finies, on retourne null.
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
