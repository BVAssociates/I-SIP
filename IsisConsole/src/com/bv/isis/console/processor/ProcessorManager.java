/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/processor/ProcessorManager.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des processeurs de t�che
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
* Ajout de l'argument postprocessing � la m�thode run().
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
* Mise au propre des traces de m�thodes.
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
* Cloture it�ration IT1.2
* 
*
* Revision 1.3  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.2  2001/12/19 09:57:59  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1  2001/12/12 09:58:32  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.processor;

//
// Imports syst�me
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
* Cette classe abstraite est une classe technique charg� d'instancier et de 
* lancer les processeurs de t�che. Elle est accessible par n'importe quelle 
* classe de l'application.
* L'instanciation et l'ex�cution d'un processeur de t�che est effectu� via la 
* m�thode executeProcessor().
* Elle pr�sente �galement des m�thodes afin de:
*  - pr�-charger les processeurs,
*  - r�cup�rer la liste des processeurs globaux (qui ne d�pendent pas de la 
*    s�lection d'un noeud dans l'arbre d'exploration),
*  - savoir si un processeur peut �tre invoqu� depuis un noeud d'exploration 
*    ou depuis un �l�ment d'un tableau,
*  - r�cup�rer la liste de tous les processeurs.
* ----------------------------------------------------------*/
public abstract class ProcessorManager
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: preLoadProcessors
	* 
	* Description:
	* Cette m�thode statique est charg�e d'effectuer le pr�-chargement de tous 
	* les processeurs d�clar�s sur la Console. La liste des processeurs 
	* d�clar�s est r�cup�r�e depuis la configuration, section "Processors".
	* Pour chaque processeur d�clar�, une instance va �tre cr��e, et la 
	* m�thode preLoad() de cette instance va �tre appel�e. Chaque instance va 
	* �tre ajout�e � une table de Hash dont la cl� sera le nom du processeur 
	* et la valeur l'instance.
	* 
	* Si un probl�me survient lors de la r�cup�ration de la liste des 
	* processeurs ou de l'instanciation de l'un d'eux, l'exception 
	* InnerException est lev�e.
	* 
	* L�ve: InnerException.
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
		// On va r�cup�rer la liste des processeurs d�clar�s via la 
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
			trace_errors.writeTrace("Erreur lors de la r�cup�ration de la " +
				"liste des processeurs depuis la configuration: " + exception);
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_CannotLoadProcessorList", exception.getMessage(), 
				exception);
		}
		// Pour chaque processeur, on va essayer de cr�er une instance
		for(int index = 0 ; index < processor_list.size() ; index ++)
		{
			String implementation_class = null;
			
			String processor_name = (String)processor_list.elementAt(index);
			trace_debug.writeTrace("Instanciation du processeur: " + 
				processor_name);
			// On r�cup�re la classe d'impl�mentation du processeur
			try
			{
				implementation_class = 
					configuration.getString("Processors", processor_name);
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace("Impossible de r�cup�rer la classe " +
					"d'impl�mentation du processeur " + processor_name + ": " +
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
			// Maintenant, on va essayer d'instancier la classe d'impl�mentation
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
			// On v�rifie que l'objet est du bon type
			if(!(processor_object instanceof ProcessorInterface))
			{
				trace_errors.writeTrace("La classe d'impl�mentation n'impl�mente" +
					" pas l'interface ProcessorInterface");
				// Construction du message d'erreur
				String[] extra_info = { processor_name };
				String reason = MessageManager.fillInMessage(
					MessageManager.getMessage("&ERR_ProcessorNotOfCorrectType"),
					extra_info);
				trace_methods.endOfMethod();
				throw new InnerException(reason, null, null);
			}
			trace_debug.writeTrace("Pr�-chargement du processeur: " + 
				processor_name);
			try
			{
				// On va effectuer le pr�-chargement du processeur
				((ProcessorInterface)processor_object).preLoad();
			}
			catch(Throwable throwable)
			{
				trace_errors.writeTrace("Erreur lors du pr�-chargement du " +
					"processeur: " + processor_name);
			}
			// On ajoute le processeur � la liste des processeurs d�clar�s
			_processors.put(processor_name, processor_object);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: executeProcessor
	* 
	* Description:
	* Cette m�thode statique est appel�e par une classe de l'application afin 
	* de dupliquer et d'ex�cuter un processeur de t�che pass� en argument.
	* Elle cr�e un double de cette interface, et appelle la m�thode run (voir 
	* l'interface ProcessorInterface).
	* 
	* Si l'ex�cution �choue, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - processorInterface: Une r�f�rence sur l'interface ProcessorInterface 
	*    du processeur � ex�cuter,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface, 
	*    n�cessaire � l'ex�cution du processeur,
	*  - menuItem: Une r�f�rence sur l'item de menu ayant d�clench� l'ex�cution 
	*    du processeur. Cet argument peut �tre nul,
	*  - parameters: Une cha�ne de caract�res contenant des param�tres 
	*    d'ex�cution du processeur. Cet argument peut �tre nul. Sa syntaxe 
	*    d�pend du processeur,
	*  - preprocessing: Une cha�ne de caract�res contenant des instructions 
	*    de pr�processing. Cet argument peut �tre nul,
 	*  - postprocessing: Une cha�ne de caract�res contenant des instructions 
 	*    de postprocessing. Cet argument peut �tre nul,
 	*  - selectedNode: Une r�f�rence sur un objet DefaultMutableTreeNode 
	*    correspondant au noeud s�lectionn�. Cet argument peut �tre nul,
	*  - confirm: Un bool�en indiquant si l'utilisateur doit confirmer 
	*    l'ex�cution (true) ou non (false),
	*  - waitForCompletion: Un bool�en indiquant si la m�thode doit attendre 
	*    la fin de l'ex�cution ou non.
	* 
	* L�ve: InnerException.
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
		// On v�rifie l'int�grit� des arguments
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
				trace_debug.writeTrace("L'utilisateur a annul�.");
				// On remet le curseur par d�faut
				windowInterface.setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
				trace_methods.endOfMethod();
				return;
			}
		}
		// On duplique et on ex�cute le processeur, l'exception sera
		// trait�e localement
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
				// On remet le curseur par d�faut
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
	* Cette m�thode statique est appel�e par une classe de l'application afin 
	* de dupliquer et d'ex�cuter un processeur de t�che � partir d'un 
	* identifiant pass� en param�tre.
	* L'identifiant du processeur doit correspondre � une classe 
	* d'impl�mentation, ayant �t� pr�alablement pr�-charg�e. Ensuite, elle 
	* cr�e un double de cette classe, et appelle la m�thode run (voir 
	* l'interface ProcessorInterface).
	* 
	* Si le processeur n'est pas connu, ou que l'ex�cution �choue, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - processorName: L'identifiant du processeur de t�che � ex�cuter,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface, 
	*    n�cessaire � l'initialisation du processeur,
	*  - menuItem: Une r�f�rence sur l'item de menu ayant d�clench� 
	*    l'ex�cution du processeur. Cet argument peut �tre nul,
	*  - parameters: Une cha�ne de caract�res contenant des param�tres 
	*    d'ex�cution du processeur. Cet argument peut �tre nul. Sa syntaxe 
	*    d�pend du processeur,
	*  - preprocessing: Une cha�ne de caract�res contenant des instructions de 
	*    pr�processing. Cet argument peut �tre nul,
	*  - postprocessing: Une cha�ne de caract�res contenant des instructions 
	*    de postprocessing. Cet argument peut �tre nul,
	*  - selectedNode: Une r�f�rence sur un objet DefaultMutableTreeNode 
	*    correspondant au noeud s�lectionn�. Cet argument peut �tre nul,
	*  - confirm: Un bool�en indiquant si l'utilisateur doit confirmer 
	*    l'ex�cution (true) ou non (false),
	*  - waitForCompletion: Un bool�en indiquant si la m�thode doit attendre 
	*    la fin de l'ex�cution ou non.
	* 
	* L�ve: InnerException.
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
		// On v�rifie l'int�grit� des arguments
		if(processorName == null || processorName.equals("") == true ||
		   windowInterface == null)
		{
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// Le processeur est-il dans la liste des processeurs d�clar�s
		if(_processors == null || 
			_processors.containsKey(processorName) == false)
		{
			trace_errors.writeTrace(
				"Le processeur n'est pas d�clar�: " + processorName);
			// Construction du message d'erreur
			String[] extra_info = { processorName };
			String reason = MessageManager.fillInMessage(
				MessageManager.getMessage("&ERR_ProcessorNotAvailable"),
				extra_info);
			trace_methods.endOfMethod();
			throw new InnerException(reason, null, null);
		}
		// On r�cup�re le processeur correspondant
		ProcessorInterface processor = 
			(ProcessorInterface)_processors.get(processorName);
		// On appelle l'autre m�thode execute()
		executeProcessor(processor, windowInterface, menuItem, parameters, 
			preprocessing, postprocessing, selectedNode, confirm, 
			waitForCompletion);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getGlobalProcessors
	* 
	* Description:
	* Cette m�thode statique permet de r�cup�rer les processeurs d�clar�s 
	* comme globaux. Un processeur global est un processeur dont l'ex�cution 
	* ne d�pend pas d'un noeud d'exploration ou d'un �l�ment de tableau (voir 
	* la m�thode isGlobalCapable() de l'interface ProcessorInterface).
	* 
	* Retourne: Un tableau de ProcessorInterface correspondant � la liste des 
	* processeurs globaux.
	* ----------------------------------------------------------*/
	public static ProcessorInterface[] getGlobalProcessors()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "getGlobalProcessors");
		Vector global_processors = new Vector();

		trace_methods.beginningOfMethod();
		// On va regarder dans la liste des processeurs d�clar�s ceux qui
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
	* Cette m�thode statique permet de r�cup�rer tous les processeurs 
	* d�clar�s. Cela est surtout n�cessaire � la fen�tre principale pour savoir 
	* si tous les processeurs ont �t� configur�s et pour construire la fen�tre 
	* de configuration de l'application.
	* 
	* Retourne: Un tableau de ProcessorInterface correspondant � la liste des 
	* processeurs d�clar�s.
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
	* Cette m�thode permet de savoir si le processeur dont l'identifiant pass� 
	* en argument est un processeur pouvant �tre invoqu� depuis un noeud 
	* d'exploration.
	* Si le processeur n'est pas d�clar�, la m�thode retourne false. Sinon, 
	* la m�thode retourne la valeur de retour de la m�thode isTreeCapable() 
	* du processeur.
	* 
	* Arguments:
	*  - processorName: L'identifiant du processeur � tester. 
	* 
	* Retourne: true si le processeur est d�clar� et qu'il peut �tre invoqu� 
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
		// On v�rifie l'int�grit� des arguments
		if(processorName == null || processorName.equals("") == true)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// Le processeur est-il dans la liste des processeurs d�clar�s
		if(_processors == null || 
			_processors.containsKey(processorName) == false)
		{
			trace_errors.writeTrace(
				"Le processeur n'est pas d�clar�: " + processorName);
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// On r�cup�re le processeur correspondant
		ProcessorInterface processor = 
			(ProcessorInterface)_processors.get(processorName);
		trace_methods.endOfMethod();
		return processor.isTreeCapable();
	}

	/*----------------------------------------------------------
	* Nom: isProcessorTableCapable
	* 
	* Description:
	* Cette m�thode permet de savoir si le processeur dont l'identifiant pass� 
	* en argument est un processeur pouvant �tre invoqu� depuis un �l�ment de 
	* tableau.
	* Si le processeur n'est pas d�clar�, la m�thode retourne false. Sinon, 
	* la m�thode retourne la valeur de retour de la m�thode isTableCapable() 
	* du processeur.
	* 
	* Arguments:
	*  - processorName: L'identifiant du processeur � tester. 
	* 
	* Retourne: true si le processeur est d�clar� et qu'il peut �tre invoqu� 
	* depuis un �l�ment de tableau, false sinon.
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
		// On v�rifie l'int�grit� des arguments
		if(processorName == null || processorName.equals("") == true)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// Le processeur est-il dans la liste des processeurs d�clar�s
		if(_processors == null || 
			_processors.containsKey(processorName) == false)
		{
			trace_errors.writeTrace(
				"Le processeur n'est pas d�clar�: " + processorName);
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// On r�cup�re le processeur correspondant
		ProcessorInterface processor = 
			(ProcessorInterface)_processors.get(processorName);
		trace_methods.endOfMethod();
		return processor.isTableCapable();
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	* 
	* Description:
	* Cette m�thode statique permet de lib�rer les ressources allou�es par la 
	* classe. Elle doit �tre appel�e � la fin de l'ex�cution de l'application.
	* ----------------------------------------------------------*/
	public static void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorManager", "cleanBeforeExit");

		trace_methods.beginningOfMethod();
		// On supprime le contenu de la liste des processeurs et la liste
		// elle-m�me
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
	* Cet attribut statique maintient une r�f�rence sur une table de Hash 
	* contenant tous les processeurs d�clar�s. Cette table de Hash est 
	* instanci�e et remplie par la m�thode preLoadProcessors().
	* La cl� de la table de Hash est l'identifiant du processeur, tandis 
	* que la valeur est une instance de classe d'impl�mentation de 
	* l'interface ProcessorInterface.
	* ----------------------------------------------------------*/
	private static Hashtable _processors;
}