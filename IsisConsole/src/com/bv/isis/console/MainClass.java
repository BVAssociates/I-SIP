/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/MainClass.java,v $
* $Revision: 1.19 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe principale de l'application
* DATE:        13/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MainClass.java,v $
* Revision 1.19  2009/01/14 14:22:35  tz
* Prise en compte de la modification des packages.
*
* Revision 1.18  2008/02/21 12:04:57  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.17  2007/10/23 11:53:31  tz
* Suppression d'un import inutile.
*
* Revision 1.16  2005/10/07 08:45:52  tz
* Changement mineur.
*
* Revision 1.15  2005/07/01 12:29:53  tz
* Modification du composant pour les traces
*
* Revision 1.14  2004/11/09 15:18:56  tz
* Ajout de IORFinder.cleanBeforeExit() et de
* IsisEventsListenerImpl.cleanBeforeExit().
*
* Revision 1.13  2004/10/22 15:47:00  tz
* Initialisation des processeurs au démarrage,
* Récupération du look-and-feel depuis les préférences.
*
* Revision 1.12  2004/10/14 07:09:22  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.11  2004/10/13 14:04:05  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.10  2004/10/06 07:44:28  tz
* Test des look-and-feel disponibles.
*
* Revision 1.9  2004/07/29 12:26:09  tz
* Suppression d'imports inutiles
*
* Revision 1.8  2002/08/26 09:48:06  tz
* Utilisation du nouvel MasterInterfaceProxy.
*
* Revision 1.7  2002/06/19 12:12:59  tz
* Langue forcée à "fr"
*
* Revision 1.6  2002/04/17 07:59:20  tz
* Suppression de l'initialisation de l'API d'aide.
*
* Revision 1.5  2002/04/05 15:46:38  tz
* Cloture itération IT1.2
*
* Revision 1.4  2002/03/27 09:51:39  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.3  2002/02/04 10:54:24  tz
* Cloture itération IT1.0.1
*
* Revision 1.2  2001/12/19 09:57:54  tz
* Cloture itération IT1.0.0
*
* Revision 1.1.1.1  2001/11/14 08:41:01  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console;

//
// Imports système
//
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import com.bv.core.gui.IconLoader;
import com.bv.core.help.HelpManager;
import com.bv.core.message.MessageManager;
import javax.swing.UIManager;
import com.bv.core.prefs.PreferencesAPI;
import com.bv.core.config.ConfigurationAPI;
import java.text.SimpleDateFormat;
//
// Imports du projet
//
import com.bv.isis.console.core.abs.main.ProcessInterface;
import com.bv.isis.console.gui.StartupWindow;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.PortalInterfaceProxy;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.IORFinder;
import com.bv.isis.console.gui.MainWindow;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.processor.ProcessorManager;
import com.bv.isis.console.impl.com.IsisEventsListenerImpl;

/*----------------------------------------------------------
* Nom: MainClass
*
* Description:
* Cette classe est la classe principale de l'application. Elle gère le
* cycle de vie de l'application et implémente l'interface ProcessInterface
* afin de permettre à la fenêtre principale d'arrêter l'application.
*
* Elle contient la méthode main appelée par la machine virtuelle java
* lors du lancement de l'application.
* ----------------------------------------------------------*/
public class MainClass
	implements ProcessInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: MainClass
	*
	* Description:
	* Cette méthode est le constructeur par défaut de la classe MainClass.
	* Elle permet d'en créer une instance. Il s'agit du seul constructeur
	* utilisable.
	* ----------------------------------------------------------*/
	public MainClass()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"MainClass", "MainClass");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: main
	*
	* Description:
	* Cette méthode statique est la méthode appelée par la machine
	* virtuelle java lors du lancement de l'application. Elle va
	* instancier la classe MainClass et appeler la méthode start() afin
	* de démarrer l'exécution de l'application.
	*
	* Arguments:
	*  - args: Un tableau de chaînes de caractères contenant les
	*    paramètres de la ligne de commande de l'application.
	* ----------------------------------------------------------*/
    public static void main(
		String[] args
		)
    {
		String laf_name = "Metal";
		String laf_impl;

		// Initialisation de l'API de traces
		TraceAPI.initialize("Console");
		
		// Création des traces
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"MainClass", "main");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		for(int index = 0 ; index < args.length ; index ++)
		{
			trace_arguments.writeTrace("args[" + index + "]=" + args[index]);
		}
		trace_events.writeTrace("Début du processus " +
		    TraceAPI.getProcessName());

		// On récupère le look-and-feel depuis les préférences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			// Ajout de la section GUI
			preferences.useSection("GUI");
			laf_name = preferences.getString("LookAndFeel");
			if(laf_name.equals("System") == true)
			{
				laf_impl = UIManager.getSystemLookAndFeelClassName();
			}
			else if(laf_name.equals("Metal") == true)
			{
				laf_impl = UIManager.getCrossPlatformLookAndFeelClassName();
			}
			else
			{
				ConfigurationAPI configuration = new ConfigurationAPI();
				laf_impl = configuration.getString("LAF", laf_name + ".Impl");
			}
			UIManager.setLookAndFeel(laf_impl);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors du positionnement du " +
				"look and feel: " + exception);
			// On ne fait rien
		}

		// Instanciation de la classe principale et démarrage
		MainClass main_class = new MainClass();
		main_class.start();
		trace_methods.endOfMethod();
    }

	/*----------------------------------------------------------
	* Nom: stop
	*
	* Description:
	* Cette méthode redéfini celle de l'interface ProcessInterface. Elle
	* est appelée afin de terminer proprement l'exécution de l'application.
	* Elle libère les ressources allouées pendant l'exécution de
	* l'application, en appelant les méthodes cleanBeforeExit d'un certain
	* nombre de classes, et termine l'exécution de la machine virtuelle
	* java.
	* ----------------------------------------------------------*/
	public void stop()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainClass", "stop");

		trace_methods.beginningOfMethod();
		// Libération des ressources allouées
		ProcessorManager.cleanBeforeExit();
		MessageManager.cleanBeforeExit();
		IconLoader.cleanBeforeExit();
		HelpManager.cleanBeforeExit();
		AgentSessionManager.cleanBeforeExit();
		PortalInterfaceProxy.cleanBeforeExit(false);
		IORFinder.cleanBeforeExit();
		IsisEventsListenerImpl.cleanBeforeExit();
		PasswordManager.cleanBeforeExit();
		TableDefinitionManager.cleanBeforeExit();
		// Exécution de la procédure d'arrêt dans un thread séparé (assure la
		// libération des ressources)
		Thread stop_thread = new Thread(new Runnable()
		{
			public void run()
			{
				// Suspension du thread pour 1 seconde
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception exception)
				{
				}

				Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"MainClass", "stopThread");
				Trace trace_events = TraceAPI.declareTraceEvents("Console");

				trace_methods.beginningOfMethod();

				// Assure la libération des ressources
				System.gc();

				// Attente pour la fermeture en mode debug uniquement
				String debug = System.getProperty("DEBUG");
				if(debug != null && debug.equalsIgnoreCase("TRUE") == true)
				{
					javax.swing.JOptionPane.showMessageDialog(null, "Cliquer pour arrêter");
				}
				// Arrêt du processus
				trace_events.writeTrace("Fin du processus " +
					TraceAPI.getProcessName());
				trace_methods.endOfMethod();
				TraceAPI.cleanBeforeExit();
				System.exit(0);
			}
		});
		stop_thread.start();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: start
	*
	* Description:
	* Cette méthode est appelée pour démarrer l'exécution de l'application.
	* Pour cela, elle initialize l'API de trace, affiche la fenêtre d'accueil
	* de l'application, et lance la procédure d'identification de l'utilisateur.
	* ----------------------------------------------------------*/
	public void start()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"MainClass", "start");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// Chargement des fichiers de messages de l'application
		MessageManager.loadFile("bvcore.mdb", "UTF8");
		MessageManager.loadFile("console.mdb", "UTF8");
		// Affichage de la fenêtre de démarrage
		final StartupWindow startup_window = new StartupWindow();
		startup_window.show();
		trace_debug.writeTrace("Pré-chargement des processeurs");
		// On effectue le pré-chargement des processeurs
		try
		{
			ProcessorManager.preLoadProcessors();
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors du pré-chargement des " +
				"processeurs: " + exception);
			startup_window.hide();
			startup_window.dispose();
			// On va afficher l'erreur
			DialogManager.displayDialog("Error", exception.getMessage(), null, null);
			// On va sortir
			trace_methods.endOfMethod();
			stop();
			return;
		}
		trace_debug.writeTrace(
			"Ouverture de la fenêtre principale.");
		MainWindow main_window = new MainWindow(this);
		// On masque la fenêtre de démarrage
		startup_window.hide();
		startup_window.dispose();
		// Chargement des données initiales
		main_window.openSession();
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
