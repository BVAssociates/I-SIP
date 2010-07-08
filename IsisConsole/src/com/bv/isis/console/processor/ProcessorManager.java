/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/processor/ProcessorManager.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des processeurs de tâche
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ProcessorManager.java,v $
* Revision 1.14  2009/01/14 14:23:03  tz
* Prise en compte de la modification des packages.
*
* Revision 1.13  2008/01/31 16:42:26  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.12  2005/10/07 08:13:36  tz
* Changement non fonctionnel
*
* Revision 1.11  2005/07/01 12:01:48  tz
* Modification du composant pour les traces
*
* Revision 1.10  2004/10/22 15:32:17  tz
* Modification de la gestion des processeurs
*
* Revision 1.9  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.8  2004/10/13 13:52:08  tz
* Renommage du package inuit -> isis
*
* Revision 1.7  2003/03/07 16:18:31  tz
* Ajout de l'auto-exploration
*
* Revision 1.6  2002/11/22 15:27:35  tz
* Cloture IT1.0.7
* Ajout setStatus(null, null, 0) sur erreur.
*
* Revision 1.5  2002/08/13 12:56:22  tz
* Ajout fermeture du processeur sur echec
*
* Revision 1.4  2002/04/05 15:50:07  tz
* Cloture itération IT1.2
* 
*
* Revision 1.3  2002/02/04 10:54:25  tz
* Cloture itération IT1.0.1
*
* Revision 1.2  2001/12/19 09:57:59  tz
* Cloture itération IT1.0.0
*
* Revision 1.1  2001/12/12 09:58:32  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.processor;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.message.MessageManager;
import javax.swing.JOptionPane;
import java.awt.Cursor;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: ProcessorManager
* 
* Description:
* Cette classe abstraite est une classe technique chargé d'instancier et de 
* lancer les processeurs de tâche. Elle est accessible par n'importe quelle 
* classe de l'application.
* L'instanciation et l'exécution d'un processeur de tâche est effectué via la 
* méthode executeProcessor().
* Elle présente également des méthodes afin de:
*  - pré-charger les processeurs,
*  - récupérer la liste des processeurs globaux (qui ne dépendent pas de la 
*    sélection d'un noeud dans l'arbre d'exploration),
*  - savoir si un processeur peut être invoqué depuis un noeud d'exploration 
*    ou depuis un élément d'un tableau,
*  - récupérer la liste de tous les processeurs.
* ----------------------------------------------------------*/
public abstract class ProcessorManager
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: preLoadProcessors
	* 
	* Description:
	* Cette méthode statique est chargée d'effectuer le pré-chargement de tous 
	* les processeurs déclarés sur la Console. La liste des processeurs 
	* déclarés est récupérée depuis la configuration, section "Processors".
	* Pour chaque processeur déclaré, une instance va être créée, et la 
	* méthode preLoad() de cette instance va être appelée. Chaque instance va 
	* être ajoutée à une table de Hash dont la clé sera le nom du processeur 
	* et la valeur l'instance.
	* 
	* Si un problème survient lors de la récupération de la liste des 
	* processeurs ou de l'instanciation de l'un d'eux, l'exception 
	* InnerException est levée.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static void preLoadProcessors()
		throws InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "preLoadProcessors");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		ConfigurationAPI configuration = null;
		Vector processor_list = new Vector();
		Object processor_object = null; 

		trace_methods.beginningOfMethod();
		// On instancie la table de Hash
		_processors = new Hashtable();
		// On va récupérer la liste des processeurs déclarés via la 
		// configuration, section "Processors"
		try
		{
			configuration = new ConfigurationAPI();
			Enumeration parameters_names = 
				configuration.getParameterNames("Processors");
			while(parameters_names.hasMoreElements() == true)
			{
				processor_list.add(parameters_names.nextElement());
			}
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la récupération de la " +
				"liste des processeurs depuis la configuration: " + exception);
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_CannotLoadProcessorList", exception.getMessage(), 
				exception);
		}
		// Pour chaque processeur, on va essayer de créer une instance
		for(int index = 0 ; index < processor_list.size() ; index ++)
		{
			String implementation_class = null;
			
			String processor_name = (String)processor_list.elementAt(index);
			trace_debug.writeTrace("Instanciation du processeur: " + 
				processor_name);
			// On récupère la classe d'implémentation du processeur
			try
			{
				implementation_class = 
					configuration.getString("Processors", processor_name);
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace("Impossible de récupérer la classe " +
					"d'implémentation du processeur " + processor_name + ": " +
					exception);
				// Construction du message d'erreur
				String[] extra_info = { processor_name };
				String reason = MessageManager.fillInMessage(
					MessageManager.getMessage("&ERR_ProcessorNotAvailable"),
					extra_info);
				// On va lever une exception
				trace_methods.endOfMethod();
				throw new InnerException(reason, exception.getMessage(), exception);
			}
			trace_debug.writeTrace("implementation_class=" + implementation_class);
			// Maintenant, on va essayer d'instancier la classe d'implémentation
			// du processeur
			try
			{
				Class processor_class = Class.forName(implementation_class);
				processor_object = processor_class.newInstance();
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace(
					"Erreur lors de l'instanciation de la classe: " + exception);
				// Construction du message d'erreur
				String[] extra_info = { processor_name };
				String reason = MessageManager.fillInMessage(
					MessageManager.getMessage("&ERR_CannotInstanciateProcessor"),
					extra_info);
				trace_methods.endOfMethod();
				throw new InnerException(reason, exception.toString(), exception);
			}
			// On vérifie que l'objet est du bon type
			if(!(processor_object instanceof ProcessorInterface))
			{
				trace_errors.writeTrace("La classe d'implémentation n'implémente" +
					" pas l'interface ProcessorInterface");
				// Construction du message d'erreur
				String[] extra_info = { processor_name };
				String reason = MessageManager.fillInMessage(
					MessageManager.getMessage("&ERR_ProcessorNotOfCorrectType"),
					extra_info);
				trace_methods.endOfMethod();
				throw new InnerException(reason, null, null);
			}
			trace_debug.writeTrace("Pré-chargement du processeur: " + 
				processor_name);
			try
			{
				// On va effectuer le pré-chargement du processeur
				((ProcessorInterface)processor_object).preLoad();
			}
			catch(Throwable throwable)
			{
				trace_errors.writeTrace("Erreur lors du pré-chargement du " +
					"processeur: " + processor_name);
			}
			// On ajoute le processeur à la liste des processeurs déclarés
			_processors.put(processor_name, processor_object);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: executeProcessor
	* 
	* Description:
	* Cette méthode statique est appelée par une classe de l'application afin 
	* de dupliquer et d'exécuter un processeur de tâche passé en argument.
	* Elle crée un double de cette interface, et appelle la méthode run (voir 
	* l'interface ProcessorInterface).
	* 
	* Si l'exécution échoue, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - processorInterface: Une référence sur l'interface ProcessorInterface 
	*    du processeur à exécuter,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface, 
	*    nécessaire à l'exécution du processeur,
	*  - menuItem: Une référence sur l'item de menu ayant déclenché l'exécution 
	*    du processeur. Cet argument peut être nul,
	*  - parameters: Une chaîne de caractères contenant des paramètres 
	*    d'exécution du processeur. Cet argument peut être nul. Sa syntaxe 
	*    dépend du processeur,
	*  - preprocessing: Une chaîne de caractères contenant des instructions 
	*    de préprocessing. Cet argument peut être nul,
 	*  - postprocessing: Une chaîne de caractères contenant des instructions 
 	*    de postprocessing. Cet argument peut être nul,
 	*  - selectedNode: Une référence sur un objet DefaultMutableTreeNode 
	*    correspondant au noeud sélectionné. Cet argument peut être nul,
	*  - confirm: Un booléen indiquant si l'utilisateur doit confirmer 
	*    l'exécution (true) ou non (false),
	*  - waitForCompletion: Un booléen indiquant si la méthode doit attendre 
	*    la fin de l'exécution ou non.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static void executeProcessor(
		ProcessorInterface processorInterface,
		final MainWindowInterface windowInterface,
		final JMenuItem menuItem,
		final String parameters,
		final String preprocessing,
		final String postprocessing,
		final DefaultMutableTreeNode selectedNode,
		boolean confirm,
		boolean waitForCompletion
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "executeProcessor");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("processorInterface=" + processorInterface);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("confirm=" + confirm);
		trace_arguments.writeTrace("waitForCompletion=" + waitForCompletion);
		// On vérifie l'intégrité des arguments
		if(processorInterface == null || windowInterface == null)
		{
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// Si l'utilisateur doit confirmer, on demande la confirmation
		if(confirm == true && windowInterface != null)
		{
			trace_debug.writeTrace(
				"La confirmation de l'utilisateur est requise");
			// Construction de la question la commande (avec le label du
			// menu)
			String[] extra_info = { menuItem.getText() };
			int reply = windowInterface.showPopup("YesNoQuestion",
				"&Question_ExecuteCommand", extra_info);
			if(reply == JOptionPane.NO_OPTION)
			{
				trace_debug.writeTrace("L'utilisateur a annulé.");
				// On remet le curseur par défaut
				windowInterface.setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
				trace_methods.endOfMethod();
				return;
			}
		}
		// On duplique et on exécute le processeur, l'exception sera
		// traitée localement
		final ProcessorInterface processor_interface =
			processorInterface.duplicate();
		Thread initialization_thread = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					processor_interface.run(windowInterface, menuItem, 
						parameters, preprocessing, postprocessing, 
						selectedNode);
				}
				catch(Exception exception)
				{
					windowInterface.showPopupForException(
						"&ERR_CannotExecuteProcessor", exception);
					// On force la fermeture du processeur
					processor_interface.close();
					windowInterface.setStatus(null, null, 0);
				}
				// On remet le curseur par défaut
				windowInterface.setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
			}
		});
		initialization_thread.start();
		if(waitForCompletion == true)
		{
			try
			{
				initialization_thread.join();
			}
			catch(Exception e)
			{
				// On se fiche de cette exception
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: executeProcessor
	* 
	* Description:
	* Cette méthode statique est appelée par une classe de l'application afin 
	* de dupliquer et d'exécuter un processeur de tâche à partir d'un 
	* identifiant passé en paramètre.
	* L'identifiant du processeur doit correspondre à une classe 
	* d'implémentation, ayant été préalablement pré-chargée. Ensuite, elle 
	* crée un double de cette classe, et appelle la méthode run (voir 
	* l'interface ProcessorInterface).
	* 
	* Si le processeur n'est pas connu, ou que l'exécution échoue, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - processorName: L'identifiant du processeur de tâche à exécuter,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface, 
	*    nécessaire à l'initialisation du processeur,
	*  - menuItem: Une référence sur l'item de menu ayant déclenché 
	*    l'exécution du processeur. Cet argument peut être nul,
	*  - parameters: Une chaîne de caractères contenant des paramètres 
	*    d'exécution du processeur. Cet argument peut être nul. Sa syntaxe 
	*    dépend du processeur,
	*  - preprocessing: Une chaîne de caractères contenant des instructions de 
	*    préprocessing. Cet argument peut être nul,
	*  - postprocessing: Une chaîne de caractères contenant des instructions 
	*    de postprocessing. Cet argument peut être nul,
	*  - selectedNode: Une référence sur un objet DefaultMutableTreeNode 
	*    correspondant au noeud sélectionné. Cet argument peut être nul,
	*  - confirm: Un booléen indiquant si l'utilisateur doit confirmer 
	*    l'exécution (true) ou non (false),
	*  - waitForCompletion: Un booléen indiquant si la méthode doit attendre 
	*    la fin de l'exécution ou non.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static void executeProcessor(
		String processorName,
		final MainWindowInterface windowInterface,
		final JMenuItem menuItem,
		final String parameters,
		final String preprocessing,
		final String postprocessing,
		final DefaultMutableTreeNode selectedNode,
		boolean confirm,
		boolean waitForCompletion
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "executeProcessor");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("processorName=" + processorName);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("confirm=" + confirm);
		trace_arguments.writeTrace("waitForCompletion=" + waitForCompletion);
		// On vérifie l'intégrité des arguments
		if(processorName == null || processorName.equals("") == true ||
		   windowInterface == null)
		{
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// Le processeur est-il dans la liste des processeurs déclarés
		if(_processors == null || 
			_processors.containsKey(processorName) == false)
		{
			trace_errors.writeTrace(
				"Le processeur n'est pas déclaré: " + processorName);
			// Construction du message d'erreur
			String[] extra_info = { processorName };
			String reason = MessageManager.fillInMessage(
				MessageManager.getMessage("&ERR_ProcessorNotAvailable"),
				extra_info);
			trace_methods.endOfMethod();
			throw new InnerException(reason, null, null);
		}
		// On récupère le processeur correspondant
		ProcessorInterface processor = 
			(ProcessorInterface)_processors.get(processorName);
		// On appelle l'autre méthode execute()
		executeProcessor(processor, windowInterface, menuItem, parameters, 
			preprocessing, postprocessing, selectedNode, confirm, 
			waitForCompletion);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getGlobalProcessors
	* 
	* Description:
	* Cette méthode statique permet de récupérer les processeurs déclarés 
	* comme globaux. Un processeur global est un processeur dont l'exécution 
	* ne dépend pas d'un noeud d'exploration ou d'un élément de tableau (voir 
	* la méthode isGlobalCapable() de l'interface ProcessorInterface).
	* 
	* Retourne: Un tableau de ProcessorInterface correspondant à la liste des 
	* processeurs globaux.
	* ----------------------------------------------------------*/
	public static ProcessorInterface[] getGlobalProcessors()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "getGlobalProcessors");
		Vector global_processors = new Vector();

		trace_methods.beginningOfMethod();
		// On va regarder dans la liste des processeurs déclarés ceux qui
		// sont globaux
		Iterator declared_processors = _processors.values().iterator();
		while(declared_processors.hasNext() == true)
		{
			ProcessorInterface processor_interface = 
				(ProcessorInterface)declared_processors.next();
			if(processor_interface.isGlobalCapable() == true)
			{
				global_processors.add(processor_interface);
			}
		}
		trace_methods.endOfMethod();
		return (ProcessorInterface[])global_processors.toArray(
			new ProcessorInterface[0]);
	}

	/*----------------------------------------------------------
	* Nom: getAllProcessors
	* 
	* Description:
	* Cette méthode statique permet de récupérer tous les processeurs 
	* déclarés. Cela est surtout nécessaire à la fenêtre principale pour savoir 
	* si tous les processeurs ont été configurés et pour construire la fenêtre 
	* de configuration de l'application.
	* 
	* Retourne: Un tableau de ProcessorInterface correspondant à la liste des 
	* processeurs déclarés.
	* ----------------------------------------------------------*/
	public static ProcessorInterface[] getAllProcessors()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "getAllProcessors");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return (ProcessorInterface[])_processors.values().toArray(
			new ProcessorInterface[0]);
	}

	/*----------------------------------------------------------
	* Nom: isProcessorTreeCapable
	* 
	* Description:
	* Cette méthode permet de savoir si le processeur dont l'identifiant passé 
	* en argument est un processeur pouvant être invoqué depuis un noeud 
	* d'exploration.
	* Si le processeur n'est pas déclaré, la méthode retourne false. Sinon, 
	* la méthode retourne la valeur de retour de la méthode isTreeCapable() 
	* du processeur.
	* 
	* Arguments:
	*  - processorName: L'identifiant du processeur à tester. 
	* 
	* Retourne: true si le processeur est déclaré et qu'il peut être invoqué 
	* depuis un noeud d'exploration, false sinon.
	* ----------------------------------------------------------*/
	public static boolean isProcessorTreeCapable(
		String processorName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "isProcessorTreeCapable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("processorName=" + processorName);
		// On vérifie l'intégrité des arguments
		if(processorName == null || processorName.equals("") == true)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// Le processeur est-il dans la liste des processeurs déclarés
		if(_processors == null || 
			_processors.containsKey(processorName) == false)
		{
			trace_errors.writeTrace(
				"Le processeur n'est pas déclaré: " + processorName);
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// On récupère le processeur correspondant
		ProcessorInterface processor = 
			(ProcessorInterface)_processors.get(processorName);
		trace_methods.endOfMethod();
		return processor.isTreeCapable();
	}

	/*----------------------------------------------------------
	* Nom: isProcessorTableCapable
	* 
	* Description:
	* Cette méthode permet de savoir si le processeur dont l'identifiant passé 
	* en argument est un processeur pouvant être invoqué depuis un élément de 
	* tableau.
	* Si le processeur n'est pas déclaré, la méthode retourne false. Sinon, 
	* la méthode retourne la valeur de retour de la méthode isTableCapable() 
	* du processeur.
	* 
	* Arguments:
	*  - processorName: L'identifiant du processeur à tester. 
	* 
	* Retourne: true si le processeur est déclaré et qu'il peut être invoqué 
	* depuis un élément de tableau, false sinon.
	* ----------------------------------------------------------*/
	public static boolean isProcessorTableCapable(
		String processorName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "isProcessorTableCapable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("processorName=" + processorName);
		// On vérifie l'intégrité des arguments
		if(processorName == null || processorName.equals("") == true)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// Le processeur est-il dans la liste des processeurs déclarés
		if(_processors == null || 
			_processors.containsKey(processorName) == false)
		{
			trace_errors.writeTrace(
				"Le processeur n'est pas déclaré: " + processorName);
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// On récupère le processeur correspondant
		ProcessorInterface processor = 
			(ProcessorInterface)_processors.get(processorName);
		trace_methods.endOfMethod();
		return processor.isTableCapable();
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	* 
	* Description:
	* Cette méthode statique permet de libérer les ressources allouées par la 
	* classe. Elle doit être appelée à la fin de l'exécution de l'application.
	* ----------------------------------------------------------*/
	public static void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "cleanBeforeExit");

		trace_methods.beginningOfMethod();
		// On supprime le contenu de la liste des processeurs et la liste
		// elle-même
		_processors.clear();
		_processors = null;
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _processors
	* 
	* Description:
	* Cet attribut statique maintient une référence sur une table de Hash 
	* contenant tous les processeurs déclarés. Cette table de Hash est 
	* instanciée et remplie par la méthode preLoadProcessors().
	* La clé de la table de Hash est l'identifiant du processeur, tandis 
	* que la valeur est une instance de classe d'implémentation de 
	* l'interface ProcessorInterface.
	* ----------------------------------------------------------*/
	private static Hashtable _processors;
}