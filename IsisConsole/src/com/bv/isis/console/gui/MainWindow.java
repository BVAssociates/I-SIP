/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/MainWindow.java,v $
* $Revision: 1.38 $
*
* ------------------------------------------------------------
* DESCRIPTION: Fenêtre principale de l'application
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MainWindow.java,v $
* Revision 1.38  2009/02/05 15:55:05  tz
* Correction de la fiche FS#591.
*
* Revision 1.37  2009/01/14 14:22:11  tz
* Prise en compte de la modification des packages.
*
* Revision 1.36  2008/08/18 15:45:39  tz
* Correction de la fiche FS#510.
*
* Revision 1.35  2008/08/11 10:46:22  tz
* Test si le noeud racine est du type GenericTreeObjectNode.
*
* Revision 1.34  2008/03/03 12:08:40  tz
* Icône de la fenêtre : ISIS.gif
*
* Revision 1.33  2008/02/15 14:13:55  tz
* Prise en compte de la méthode getFileFilters() dans la méthode saveData().
*
* Revision 1.32  2008/01/31 16:40:28  tz
* Prise en compte de l'ajout d'un argument à
* ProcessorManager.executeProcessor().
*
* Revision 1.31  2007/09/24 10:40:31  tz
* Gestion de l'orientation des zones.
*
* Revision 1.30  2007/04/12 15:15:38  ml
* Nombre de profil en paramètre dans Console_config.ini
*
* Revision 1.29  2006/11/03 10:28:28  tz
* Ajout des méthodes doUndo() et doRedo(), et gestion de l'annulation/
* rétablissement des actions d'édition.
*
* Revision 1.28  2006/03/08 16:20:12  tz
* Enregistrement et restitution de la position du diviseur
*
* Revision 1.27  2006/03/07 09:27:13  tz
* Attente d'un état OPENED ou CLOSED avant la fermeture de la Console.
*
* Revision 1.26  2005/12/23 13:17:18  tz
* Méthode exitWindow() publique (cf interface MainWindowInterface).
*
* Revision 1.25  2005/10/07 08:44:13  tz
* Gestion du profil de connexion au Portail par les propriétés système.
*
* Revision 1.24  2005/10/07 08:41:51  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
* Implémentation de l'interface ConsoleIsisEventsListenerInterface.
* Utilisation des interfaces SearchableComponentInterface pour la recherche
* de texte et la sauvegarde.
*
* Revision 1.23  2005/07/01 12:27:23  tz
* Modification du composant pour les traces
* Suppression de la fonctionnalité d'impression
* Correction des fonctionnalité de copie/collage
* Ajout de la fonctionnalité de recherche
*
* Revision 1.22  2004/11/09 15:26:19  tz
* Ajout de IORFinder.cleanBeforeExit() et de
* IsisEventsListenerImpl.cleanBeforeExit().
*
* Revision 1.21  2004/11/03 15:20:59  tz
* Suppression de l'affichage des événements sur System.out.
*
* Revision 1.20  2004/11/02 09:08:08  tz
* Traitement du cas de la fermeture de la session Portail.
*
* Revision 1.19  2004/10/22 15:44:22  tz
* Gestion des événements I-SIS,
* Modification de la vérification et de la modification de la configuration.
*
* Revision 1.18  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.17  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.16  2004/10/06 07:41:27  tz
* Fermeture de l'application dans le thread courant,
* Gestion de la barre de séparation.
*
* Revision 1.15  2004/07/29 12:21:08  tz
* Suppression d'imports inutiles
* Traitement de la destruction des noeuds en cas d'arrêt du portail
* Affichage du nom de l'utilisateur et du portail dans la barre d'état
*
* Revision 1.14  2003/06/10 13:58:20  tz
* Utilisation du nouveau vocabulaire (pour l'aide)
*
* Revision 1.13  2003/03/10 15:42:43  tz
* Ajout de la méthode isConnected()
*
* Revision 1.12  2003/03/07 16:22:14  tz
* Ajout du mécanisme de log métier.
* Les méthodes masterStopped() sont déclarées oneway.
*
* Revision 1.11  2002/12/26 12:57:30  tz
* Correction de la fiche Inuit/84
*
* Revision 1.10  2002/11/19 08:46:32  tz
* Gestion de la progression de la tâche.
*
* Revision 1.9  2002/09/20 10:40:12  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.8  2002/08/26 09:51:26  tz
* Utilisation du nouveau MasterInterfaceProxy.
*
* Revision 1.7  2002/08/13 13:16:08  tz
* Utilisation des préférences.
* Modification de la méthode setCurrentCursor
*
* Revision 1.6  2002/06/19 12:18:40  tz
* Traitement du masterStopped() dans un thread séparé
*
* Revision 1.5  2002/04/05 15:47:21  tz
* Cloture itération IT1.2
*
* Revision 1.4  2002/03/27 09:42:06  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.3  2002/02/04 10:54:24  tz
* Cloture itération IT1.0.1
*
* Revision 1.2  2001/12/19 09:58:49  tz
* Cloture itération IT1.0.0
*
* Revision 1.1  2001/11/19 17:07:54  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.gui;

//
// Imports système
//
import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.BorderFactory;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.gui.IconLoader;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.bv.core.gui.AboutDialog;
import java.awt.Cursor;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import javax.help.HelpSet;
import javax.help.HelpBroker;
import javax.help.CSH;
import java.net.URL;
import java.io.File;
import javax.swing.KeyStroke;
import com.bv.core.prefs.PreferencesAPI;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JFileChooser;
import java.util.Properties;
import java.io.FileInputStream;
import java.util.Vector;
import javax.swing.filechooser.FileFilter;

//
// Imports du projet
//
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisEventTypeEnum;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.main.ProcessInterface;
import com.bv.isis.console.core.abs.gui.TreeInterface;
import com.bv.isis.console.com.PortalInterfaceProxy;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.processor.ProcessorManager;
import com.bv.isis.console.core.gui.NonEditableTextArea;
import com.bv.isis.console.core.gui.NonEditableTextPane;
import com.bv.isis.console.core.gui.FileNameExtensionFilter;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.impl.com.IsisEventsListenerImpl;
import com.bv.isis.console.com.IORFinder;
import com.bv.isis.console.core.abs.processor.SearchableComponentInterface;
import com.bv.isis.console.core.abs.processor.ConsoleIsisEventsListenerInterface;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.core.abs.processor.UndoableComponentInterface;

/*----------------------------------------------------------
* Nom: MainWindow
*
* Description:
* Cette classe représente la fenêtre principale qui est affichée à
* l'utilisateur. Il s'agit de la fenêtre de travail dans laquelle seront
* présentés les objets suivants:
*  - une barre de menu,
*  - un arbre d'exploration,
*  - une zone d'affichage des sous-fenêtres,
*  - une barre d'état.
*
* Elle est chargée de traiter tous les évènements relatifs aux éléments de la
* barre de menu, et toutes les notifications en provenance du processus Portail
* (elle implémente l'interface ConsoleIsisEventsListenerInterface). Elle 
* implémente également l'interface MainWindowInterface qui est utilisée, entre 
* autres, par le mécanisme de processeur de tâche.
*
* Les évènements relatifs à la manipulation de l'arbre d'exploration ainsi
* qu'aux sous-fenêtres sont délégués aux objets correspondants (MainWindowTree
* et MainWindowDesktopPane).
* ----------------------------------------------------------*/
public class MainWindow
	extends JFrame
	implements ConsoleIsisEventsListenerInterface,
		       MainWindowInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: MainWindow
	*
	* Description:
	* Cette méthode est le seul constructeur de la classe. Il permet de créer
	* une instance de MainWindow en passant une référence sur l'interface
	* ProcessInterface (nécessaire à l'arrêt de l'application) en argument.
	* Elle crée la fenêtre en appelant la méthode makePanel.
	* Ensuite, elle affiche la fenêtre à l'utilisateur.

	* Arguments:
	*  - processInterface: Une référence sur l'interface ProcessInterface
	*    permettant à la fenêtre principale d'arrêter l'application.
	* ----------------------------------------------------------*/
	public MainWindow(
		ProcessInterface processInterface
		)
	{
		int x_position = -1000;
		int y_position = -1000;
		int width = 0;
		int height = 0;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "MainWindow");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("processInterface=" + processInterface);
		_processInterface = processInterface;
		// Ajout du callback sur la fermeture de la fenêtre
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				// Appel de la méthode de sortie
				exitWindow(true, false);
			}
		});
		// Suppression du comportement par défaut de la fenêtre
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Positionnement du titre de la fenêtre
		setTitle(MessageManager.getMessage("&AD_ProductName") + " " +
			MessageManager.getMessage("&AD_ApplicationName"));
		// Fabrication de la fenêtre
		makePanel();
		// On va tenter de récupérer les informations de préférences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("GUI");
			x_position = preferences.getInt("Frame.X");
			y_position = preferences.getInt("Frame.Y");
			width = preferences.getInt("Frame.Width");
			height = preferences.getInt("Frame.Height");
		}
		catch(Exception exception)
		{
			// On s'en fiche
		}
		// Récupération de la taille de l'écran
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen_size = toolkit.getScreenSize();
		// On vérifie les valeurs
		if(width == 0 || height == 0)
		{
			width = 500;
			height = 400;
		}
		if(width > screen_size.width)
		{
			width = screen_size.width;
		}
		if(height > screen_size.height)
		{
			height = screen_size.height;
		}
		// Dimensionnement de la fenêtre
		setSize(width, height);
		// Les positions x et y sont elles valables ?
		if(x_position == -1000 || y_position == -1000)
		{
			// Centrage de la fenêtre au milieu de l'écran
			// Récupération de la taille de la fenêtre
			Dimension window_size = getSize();
			// Calcul des coordonnées de la fenêtre
			x_position = (screen_size.width - window_size.width) / 2;
			y_position = (screen_size.height - window_size.height) / 2;
		}
		if(x_position < 0)
		{
			x_position = 0;
		}
		if(y_position < 0)
		{
			y_position = 0;
		}
		// Déplacement de la fenêtre
		setLocation(x_position, y_position);
		// On ajoute l'icône de la fenêtre
		if(IconLoader.getIcon("ISIS") != null)
		{
		    setIconImage(IconLoader.getIcon("ISIS").getImage());
		}
		// Affichage de la fenêtre
		show();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: openSession
	*
	* Description:
	* Cette méthode est chargée d'ouvrir une session sur le système I-SIS. Elle
	* est appelée au démarrage de l'application et lorsque l'utilisateur a
	* cliqué sur l'élément de menu "Session/Ouvrir...". En fait, il s'agit
	* principalement de construire le noeud racine de l'arbre avec des
	* informations et d'exécuter le processeur d'ouverture de session (qui
	* effectuera l'ouverture de la session sur le Portail).
	* C'est par le noeud créé par le processeur d'ouverture de session que
	* l'utilisateur pourra effectuer son exploration.
	* ----------------------------------------------------------*/
	public void openSession()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "openSession");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String agent_parameter_name = "AgentName";
		String properties_file;
		Properties profiles = new Properties();
		Vector profile_labels = new Vector();
		Vector configurations = new Vector();
		String preferred_profile = "";
		int chosen_profile = 0;
		int number_of_profiles = 5;

		trace_methods.beginningOfMethod();
		// On essaye de savoir si la Console a déjà été configurée
		if(checkConfiguration() == false)
		{
			// La Console n'a pas encore été configurée, il faut le faire
			// On va afficher un message à l'utilisateur pour lui indiquer
			// qu'il doit configurer l'interface
			showPopup("Information", "&ConsoleMustBeConfigured", null);
			// On appelle la méthode de modification des préférences
			boolean is_validated = setPreferences();
			if(is_validated == false)
			{
				// L'utilisateur n'a pas positionné les préférences, on sort
				trace_methods.endOfMethod();
				return;
			}
		}
		// On va lire les profils depuis le fichier de propriétés
		properties_file = System.getProperty("PROFILES");
		try
		{
			FileInputStream input_stream = 
				new FileInputStream(properties_file);
			profiles.load(input_stream);
			input_stream.close();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors du chargement des profils");
			// Il y a eu une erreur lors du chargement des profils
			// Il faut afficher un message à l'utilisateur
			showPopupForException("&ERR_InitialDataLoadingFailure", exception);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		
		//On va tenter de récupérer le nombre de profils maximum dans le fichier Console_config.ini
		try
		{
			ConfigurationAPI config_api = new ConfigurationAPI();
			number_of_profiles = config_api.getInt("Console","NumberOfProfiles");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la récupération du nombre de profils maximum: " +
				exception);
		}
		
		// On va construire la liste des profils
		for(int index = 0 ; index < number_of_profiles; index ++)
		{
			String label = profiles.getProperty("Profile" + index + ".Label");
			String configuration = 
				profiles.getProperty("Profile" + index + ".Configuration");
			if(label == null || label.equals("") == true || 
				configuration == null || configuration.equals("") == true)
			{
				continue;
			}
			profile_labels.add(label);
			configurations.add(configuration);
		}
		// S'il n'y a aucun profil, on sort
		if(profile_labels.size() == 0)
		{
			trace_errors.writeTrace("Aucun profil n'est renseigné !");
			trace_methods.endOfMethod();
			return;
		}
		// S'il y a plus d'un profil, on demande à l'utilisateur de choisir
		if(profile_labels.size() > 1)
		{
			preferred_profile = System.getProperty("Profile.Label", "");
			// On va récupérer l'indice du profil précédent, s'il y en a un
			for(chosen_profile = 0 ; chosen_profile < profile_labels.size() ; 
				chosen_profile ++)
			{
				if(preferred_profile.equals(
					profile_labels.elementAt(chosen_profile)) == true)
				{
					preferred_profile = 
						(String)profile_labels.elementAt(chosen_profile);
					break;
				}
			}
			preferred_profile = (String)JOptionPane.showInputDialog(this, 
				MessageManager.getMessage("&ChooseProfile"), 
				MessageManager.getMessage("&ChooseProfileTitle"),
				JOptionPane.QUESTION_MESSAGE, null, 
				(String[])profile_labels.toArray(new String[0]),
				preferred_profile);
			if(preferred_profile == null)
			{
				trace_debug.writeTrace("L'utilisateur a annulé");
				// L'utilisateur a annulé, on sort
				trace_methods.endOfMethod();
				return;
			}
			// On va récupérer l'indice du profil sélectionné
			for(chosen_profile = 0 ; chosen_profile < profile_labels.size() ; 
				chosen_profile ++)
			{
				if(preferred_profile.equals(
					profile_labels.elementAt(chosen_profile)) == true)
				{
					break;
				}
			}
		}
		preferred_profile = (String)profile_labels.elementAt(chosen_profile); 
		if(preferred_profile.equals(
			System.getProperty("Profile.Label")) == false)
		{
			// On a changé de Portail
			// On efface tous les événements du processeur d'affichage
			// des événements I-SIS
			IsisEventsListenerImpl.getInstance().fireClearEvents();
			// Il faut également supprimer le cache des dictionnaires
			TableDefinitionManager.getInstance().clear();
		}
		// On va récupérer les informations du profil sélectionné, et les 
		// stocker dans les propriétés système
		System.getProperties().put("Profile.Label", 
			(String)profile_labels.elementAt(chosen_profile));
		System.getProperties().put("Profile.Configuration",
			(String)configurations.elementAt(chosen_profile));
		setConnected(false, true);
		// On change le curseur -> sablier
		setCurrentCursor(Cursor.WAIT_CURSOR, null);
		// Affichage de l'état
		setProgressMaximum(2);
		setStatus("&Status_ConnectingToPortal", null, 0);
		try
		{
			// Enregistrement auprès du proxy d'interface Portail
			PortalInterfaceProxy portal_proxy =
				PortalInterfaceProxy.getInstance();
			IsisEventsListenerImpl.getInstance().addIsisEventsListener(this);
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'enregistrement en tant que listener");
			// On s'assure de bien nettoyer la référence au Portail
			closeSession(true);
			// Il y a eu une erreur lors de l'enregistrement
			// Il faut afficher un message à l'utilisateur
			showPopupForException("&ERR_InitialDataLoadingFailure", exception);
			// On remet le curseur par défaut
			setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
			// On sort
			setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		setStatus("&Status_ConnectingToPortal", null, 1);
		// On va récupérer les informations permettant de construire le noeud
		// racine.
		try
		{
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
		    agent_parameter_name =
				configuration_api.getString("Parameter.AgentName");
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur, il faut afficher l'erreur à l'utilisateur
			InnerException inner_exception = CommonFeatures.processException(
				"de la récupération de la configuration", exception);
			showPopupForException("&ERR_InitialDataLoadingFailure",
				inner_exception);
			// On s'assure de bien nettoyer la référence au Portail
			closeSession(false);
			// On remet le curseur par défaut
			setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
			setStatus(null, null, 0);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On crée le tableau d'IsisParameter à partir des données
		// précédentes
		IsisParameter[] parameters = new IsisParameter[1];
		// Nom de l'Agent
		parameters[0] =
			new IsisParameter(agent_parameter_name, "Portal", '"');
		// On crée le noeud
		GenericTreeObjectNode root_node =
			new GenericTreeObjectNode(parameters, "", "", "", "", "", "");
		// On positionne le noeud en tant que noeud racine de l'arbre
		((DefaultTreeModel)_tree.getModel()).setRoot(root_node);
		setStatus("&Status_ConnectingToPortal", null, 2);
		// On exécute le processeur d'ouverture de session sur ce noeud
		try
		{
		    ProcessorManager.executeProcessor("OpenAgentSession", this, null,
				null, null, null, root_node, false, false);
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors de l'exécution du processeur" +
				" d'ouverture de session: " + exception.getMessage());
			// On affiche un message à l'utilisateur
			showPopupForException("&ERR_InitialDataLoadingFailure", exception);
			// On s'assure de bien nettoyer la référence au Portail
			closeSession(false);
		}
		setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getDesktopPane
	*
	* Description:
	* Cette méthode redéfinit la méthode de l'interface MainWindowInterface.
	* Elle permet à une classe de récupérer la référence sur l'objet
	* correspondant à la zone d'affichage des sous-fenêtres.
	*
	* Retourne: Une référence sur un objet JDesktopPane.
	* ----------------------------------------------------------*/
	public JDesktopPane getDesktopPane()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "getDesktopPane");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _desktopPane;
	}

	/*----------------------------------------------------------
	* Nom: getTreeInterface
	*
	* Description:
	* Cette méthode redéfinit la méthode de l'interface MainWindowInterface. Elle
	* permet à une classe de l'application de récupérer une référence sur
	* l'interface TreeInterface permettant d'interagir avec l'arbre
	* d'exploration.
	*
	* Retourne: Une référence sur l'interface TreeInterface.
	* ----------------------------------------------------------*/
	public TreeInterface getTreeInterface()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "getTreeInterface");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _tree;
	}

	/*----------------------------------------------------------
	* Nom: setStatus
	*
	* Description:
	* Cette méthode redéfinit la méthode de l'interface MainWindowInterface. 
	* Elle permet à une classe de l'application d'afficher des informations 
	* dans la barre d'état de la fenêtre principale.
	*
	* Arguments:
	*  - status: Les données à afficher dans la barre d'état,
	*  - extraInformation: Des informations complémentaires à ajouter dans la
	*    chaîne d'état,
	*  - progress: La valeur de progression.
	* ----------------------------------------------------------*/
	public void setStatus(
		String status,
		String[] extraInformation,
		int progress
		)
	{
		String status_message = "";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "setStatus");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("status=" + status);
		trace_arguments.writeTrace("extraInformation=" + extraInformation);
		trace_arguments.writeTrace("progress=" + progress);
		if(status != null)
		{
			// Affichage du status fourni
			// On traduit le message
			status_message = MessageManager.getMessage(status);
			if(extraInformation != null && extraInformation.length > 0)
			{
				for(int index = 0 ; index < extraInformation.length ; index ++)
				{
					if(extraInformation[index].startsWith("&") == false)
					{
						continue;
					}
					// On traduit les informations complémentaires
					extraInformation[index] =
						MessageManager.getMessage(extraInformation[index]);
				}
				// On insère les informations supplémentaires dans le message
				status_message = MessageManager.fillInMessage(status_message,
					extraInformation);
			}
		}
		else
		{
			if(_isConnected == false)
			{
				status_message =
					MessageManager.getMessage("&Status_NotConnected");
			}
			else
			{
				status_message =
					MessageManager.getMessage("&Status_Connected");
			}
		}
		// On regarde si l'on est dans le thread swing
		if(SwingUtilities.isEventDispatchThread() == true)
		{
			// On est déjà dans le thread swing, on appelle directement la
			// méthode setCursor()
			_statusBar.setText(status_message);
			_statusBar.repaint();
			_statusBar.validate();
			_progressBar.setValue(progress);
			_progressBar.repaint();
			_progressBar.validate();
		}
		else
		{
			// On n'est pas dans le thread swing, on va appeler la méthode dans
			// celui-ci
			final String the_status_message = status_message;
			final JLabel the_status_bar = _statusBar;
			final JProgressBar the_progress_bar = _progressBar;
			final int the_progress = progress;
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						the_status_bar.setText(the_status_message);
						the_status_bar.repaint();
						the_progress_bar.setValue(the_progress);
						the_progress_bar.repaint();
					}
				});
			}
			catch(Exception exception)
			{
				// Cette exception ne peut pas arriver
				trace_errors.writeTrace("Erreur: " + exception);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setProgressMaximum
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface MainWindowInterface.
	* Elle est appelée pour positionner la valeur maximale de la
	* barre de progression.
	*
	* Arguments:
	*  - maximum: La valeur maximale de la barre de progression.
	* ----------------------------------------------------------*/
	public void setProgressMaximum(
		int maximum
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "setProgressMaximum");

		trace_methods.beginningOfMethod();
		_progressBar.setMaximum(maximum);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: showPopup
	*
	* Description:
	* Cette méthode redéfinit la méthode de l'interface MainWindowInterface.
	* Elle permet à une classe de l'application d'afficher une boîte de dialogue
	* à l'utilisateur. Elle utilise la classe DialogManager.
	*
	* Argument:
	*  - popupType: Le type de boîte de dialogue à afficher. Ce type correspond
	*    aux types définis pour la méthode displayDialog() de la classe
	*    MessageManager de la librairie BVCore/Java,
	*  - message: Le message à afficher dans la boîte de dialogue,
	*  - extraInfo: Un tableau de String contenant des informations
	*    complémentaires.
	*
	* Retourne: Un entier représentant le bouton sur lequel l'utilisateur a
	* cliqué pour fermer la boîte de dialogue. Cette valeur correspond aux types
	* définis dans la classe JOptionPane.
	* ----------------------------------------------------------*/
	public int showPopup(
		String popupType,
		String message,
		String[] extraInfo
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "showPopup");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("popupType=" + popupType);
		trace_arguments.writeTrace("message=" + message);
		trace_arguments.writeTrace("extraInfo=" + extraInfo);
		// Utilisation du service de message
		int reply = DialogManager.displayDialog(popupType, message, extraInfo,
			this);
		trace_debug.writeTrace("reply=" + reply);
		trace_methods.endOfMethod();
		return reply;
	}

	/*----------------------------------------------------------
	* Nom: showPopupForException
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface MainWindowInterface. Elle est
	* appelée lorsqu'un message d'erreur doit être affiché à l'utilisateur suite
	* à la capture d'une exception. Elle utilise la classe DialogManager.
	*
	* Arguments:
	*  - message: Le message principal de l'erreur,
	*  - exception: Une référence sur l'exception qui a été capturée.
	* ----------------------------------------------------------*/
	public void showPopupForException(
		String message,
		Exception exception
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "showPopupForException");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("message=" + message);
		trace_arguments.writeTrace("exception=" + exception);
		// Utilisation de la classe DialogManager
		DialogManager.displayDialogForException(message, this, exception);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setConnected
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface MainWindowInterface. Elle est
	* appelée pour indiquer si la Console est connectée ou non.
	*
	* Arguments:
	*  - isConnected: Indique si la Console est connectée (true) ou non (false),
	*  - showProfileLabel: Indique si l'intitulé du profil doit être affiché ou 
	*    non.
	* ----------------------------------------------------------*/
	public void setConnected(
		boolean isConnected,
		boolean showProfileLabel
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "setConnected");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String current_profile_info = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("isConnected=" + isConnected);
		trace_arguments.writeTrace("showProfileLabel=" + showProfileLabel);
		// On récupère le nom du profil de connexion
		current_profile_info = System.getProperty("Profile.Label"); 
		// On stocke l'information
		_isConnected = isConnected;
		// On modifie l'icône de la barre d'état
		if(_isConnected == true)
		{
			_userName.setIcon(IconLoader.getIcon("Connect"));
			// On va stocker le nom de l'utilisateur
			current_profile_info = 
				PasswordManager.getInstance().getUserName() +
				" - " + current_profile_info;
			_userName.setText(current_profile_info);
		}
		else
		{
			_userName.setIcon(IconLoader.getIcon("Disconnect"));
			if(showProfileLabel == false)
			{
				_userName.setText("? - ?");
				current_profile_info = null;
			}
			else
			{
				current_profile_info = "? - " + current_profile_info;
				_userName.setText(current_profile_info); 
			}
		}
		_connectItem.setEnabled(!_isConnected);
		_disconnectItem.setEnabled(_isConnected);
		// Positionnement du titre de la fenêtre
		if(current_profile_info == null) {
			setTitle(MessageManager.getMessage("&AD_ProductName") + " " +
				MessageManager.getMessage("&AD_ApplicationName"));
		}
		else {
			setTitle(MessageManager.getMessage("&AD_ProductName") + " " +
				MessageManager.getMessage("&AD_ApplicationName") +
				" (" + current_profile_info + ")");
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: eventOccured
	*
	* Description:
	* Cette méthode redéfinit la méthode de l'interface
	* ConsoleIsisEventsListenerInterface. Elle est appelée lorsqu'un
	* événement I-SIS survient.
	* S'il s'agit de l'arrêt du Portail, elle appelle la méthode closeSession() 
	* et affiche une boîte de dialogue signalant l'arrêt de l'application.
	* 
	* Arguments:
	*  - eventType: Le type d'événement,
	*  - eventInformation: Un tableau contenant les informations d'événement.
	* ----------------------------------------------------------*/
	public void eventOccured(
		IsisEventTypeEnum eventType,
		String[] eventInformation
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "eventOccured");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean event_processed = false;
		String message = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("eventType=" + eventType);
		if(eventInformation != null)
		{
			for(int index = 0 ; index < eventInformation.length ; index ++)
			{
				trace_arguments.writeTrace("eventInformation[" + index + "]=" +
					eventInformation[index]);
			}
		}
		// On récupère le noeud racine
		DefaultMutableTreeNode real_root_node =
			(DefaultMutableTreeNode)_tree.getModel().getRoot();
		if(!(real_root_node instanceof GenericTreeObjectNode)) {
			// Le noeud racine n'est pas du bon type, on peut sortir
			trace_methods.endOfMethod();
			return;
		}
		GenericTreeObjectNode root_node = 
			(GenericTreeObjectNode)real_root_node;
		// S'il s'agit de l'arrêt du Portail on informe l'utilisateur et on
		// sort
		if(eventType == IsisEventTypeEnum.PORTAL_STOPPED)
		{
			// On ferme la session
			closeSession(true);
			// Il faut afficher un message à l'utilisateur
			showPopup("Information", "&PortalStopped", null);
			trace_methods.endOfMethod();
			return;
		}
		else if(eventType == IsisEventTypeEnum.AGENT_STOPPED)
		{
			// Un Agent s'est arrêté, on va effectuer un nettoyage
			// des noeuds qui en dépendent
			event_processed = 
				root_node.forwardEvent(eventType, eventInformation, _tree);
			// On va également nettoyer les leasings des sessions Agent
			AgentSessionManager.getInstance().releaseAgentSessionNoClose(
				eventInformation[0]);
			message = "&AgentStopped";
		}
		else if(eventType == IsisEventTypeEnum.A_SESSION_CLOSED ||
			eventType == IsisEventTypeEnum.S_SESSION_CLOSED)
		{
			// Une session Agent ou de service a été fermée, on va
			// effectuer un nettoyage des noeuds qui en dépendent
			event_processed = 
				root_node.forwardEvent(eventType, eventInformation, _tree);
			if(eventType == IsisEventTypeEnum.A_SESSION_CLOSED)
			{
				message = "&AgentSessionClosed";
				// On vérifie que le noeud fermé n'est pas le noeud de la
				// session Portail (le noeud racine n'a plus d'enfant)
				if(root_node.getChildCount() == 0 && 
					eventInformation[0].equals("Portal") == true &&
					event_processed == true)
				{
					message = "&PortalSessionClosed";
					eventInformation = null;
					// On déconnecte l'utilisateur
					((DefaultTreeModel)_tree.getModel()).setRoot(
						new DefaultMutableTreeNode());
					// On change l'état de la session
					setConnected(false, false);
					// On va tout nettoyer
					closeSession(false);
					setStatus(null, null, 0);
				}
			}
			else
			{
				message = "&ServiceSessionClosed";
			}
		}
		// Si l'événement a été traité, il faut afficher un message à
		// l'utilisateur
		if(event_processed == true)
		{
			showPopup("Information", message, eventInformation);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: clearEvents
	* 
	* Description:
	* Cette méthode redéfinit la méthode de l'interface 
	* ConsoleIsisEventsListenerInterface. Elle est appelée lorsque les 
	* événements I-SIS précédemment enregistrés doivent être supprimés.
	* La méthode n'a aucun comportement.
	* ----------------------------------------------------------*/
	public void clearEvents()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "clearEvents");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: setCurrentCursor
	*
	* Description:
	* Cette méthode permet de modifier le curseur de la souris. Elle est
	* surtout utilisée pour mettre le curseur d'attente lors de l'exécution d'un
	* processeur, et de replacer le curseur normal ensuite.
	* Si l'argument component est non nul, le curseur est également positionné 
	* sur celui-ci.
	*
	* Argument:
	*  - cursor: Un entier indiquant le type de cursor a utiliser,
	*  - component: Une référence sur un composant optionnel sur lequel le 
	*    curseur va être positionné.
	* ----------------------------------------------------------*/
	public void setCurrentCursor(
		int cursor,
		Component component
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "setCurrentCursor");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("cursor=" + cursor);
		trace_arguments.writeTrace("component=" + component);
		// On récupère le curseur correspondant
		final Cursor the_cursor = Cursor.getPredefinedCursor(cursor);
		// On regarde si l'on est dans le thread swing
		if(SwingUtilities.isEventDispatchThread() == true)
		{
			// On est déjà dans le thread swing, on appelle directement la
			// méthode setCursor()
			_tree.setCursor(the_cursor);
			_desktopPane.setCursor(the_cursor);
			if(component != null)
			{
				component.setCursor(the_cursor);
			}
		}
		else
		{
			// On n'est pas dans le thread swing, on va appeler la méthode dans
			// celui-ci
			final MainWindowTree the_tree = _tree;
			final MainWindowDesktopPane the_pane = _desktopPane;
			final Component the_component = component;
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						the_tree.setCursor(the_cursor);
						the_pane.setCursor(the_cursor);
						if(the_component != null)
						{
							the_component.setCursor(the_cursor);
						}
					}
				});
			}
			catch(Exception exception)
			{
				Trace trace_errors = TraceAPI.declareTraceErrors("Console");
				// Cette exception ne peut pas arriver
				trace_errors.writeTrace("Erreur: " + exception);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConnected
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface MainWindowInterface. Elle
	* permet de savoir si la session a été ouverte sur le Portail.
	*
	* Retourne: true si la session est ouverte, false sinon.
	* ----------------------------------------------------------*/
	public boolean isConnected()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "isConnected");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _isConnected;
	}

	/*----------------------------------------------------------
	* Nom: closeSession
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface MainWindowInterface. Elle 
	* permet de fermer la session en détruisant tous les noeuds de l'arbre 
	* d'exploration, de supprimer les sessions Agent et de se désenregistrer 
	* du Portail.
	*
	* Arguments:
	*  - portalStopped: Indique si la fermeture est dûe à un arrêt du Portail
	*    (true) ou non (false).
	* ----------------------------------------------------------*/
	public void closeSession(
		boolean portalStopped
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "closeSession");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// Destruction de tous les noeuds
		// Pour détruire tous les noeuds, il faut commencer par récupérer
		// le noeud racine
		if(_isConnected == true)
		{
			GenericTreeObjectNode root_node =
				(GenericTreeObjectNode)_tree.getModel().getRoot();
			// On va s'assurer que le noeud est dans un état stable
			while(true)
			{
				int root_node_state = root_node.getNodeState();
				trace_debug.writeTrace("root_node_state=" + root_node_state);
				if(root_node_state == GenericTreeObjectNode.NodeStateEnum.CLOSED ||
					root_node_state == GenericTreeObjectNode.NodeStateEnum.OPENED)
				{
					// On peut détruire le noeud
					break;
				}
				// On va patienter 1 seconde
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception e)
				{
					// On ne fait rien
				}
			}
			// On appelle la méthode destroy sur le noeud racine
			root_node.destroy(portalStopped);
			((DefaultTreeModel)_tree.getModel()).setRoot(
				new DefaultMutableTreeNode());
		}
		// On supprime les sessions agents
		AgentSessionManager.cleanBeforeExit();
		// On se désenregistre du Portail
		PortalInterfaceProxy.cleanBeforeExit(portalStopped);
		// Il faut supprimer les mots de passe
		PasswordManager.cleanBeforeExit();
		// On change l'état de la session
		setConnected(false, false);
		setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: exitWindow
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface MainWindowInterface. Elle 
	* est appelée lorsque la fenêtre principale doit être fermée, et donc que 
	* l'application doit être arrêtée.
	* Elle masque la fenêtre principale et appelle la méthode stop() de
	* l'interface ProcessInterface.
	* Si l'argument confirm vaut true, une confirmation de l'arrêt de
	* l'application est demandée à l'utilisateur.
	*
	* Arguments:
	*  - confirm: Indique si une confirmation doit être demandée (true) ou non
	*    (false).
	*  - portalStopped: Indique si la fermeture est dûe à un arrêt du Portail
	*    (true) ou non (false).
	* ----------------------------------------------------------*/
	public void exitWindow(
		boolean confirm,
		boolean portalStopped
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "exitWindow");

		trace_methods.beginningOfMethod();
		if(confirm == true && _isConnected == true)
		{
			// On demande d'abord la confirmation à l'utilisateur
			int reply = showPopup("YesNoQuestion", "&Question_ReallyQuit", null);
			if(reply != JOptionPane.YES_OPTION)
			{
				// L'utilisateur n'a pas validé la sortie
				trace_methods.endOfMethod();
				return;
			}
		}
		// On ferme la session
		closeSession(portalStopped);
		// On ferme toutes les sous-fenêtres ouvertes
		_desktopPane.closeOpenedFrames();
		// On supprime toute connexion avec le Portail
		try
		{
			IsisEventsListenerImpl.getInstance().removeIsisEventsListener(
				this);
		}
		catch(Exception e)
		{
			// On ne fait rien
		}
		IsisEventsListenerImpl.cleanBeforeExit();
		IORFinder.cleanBeforeExit();
		// On va tenter d'enregistrer les informations sur la taille et la
		// position de la fenêtre seulement si elle n'est pas maximisée
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen_size = toolkit.getScreenSize();
		if(getX() > 10 && getY() > 10 && 
			getWidth() < (screen_size.width - 10) && 
			getHeight() < (screen_size.height - 10))
		{
			try
			{
				PreferencesAPI preferences = new PreferencesAPI();
				preferences.addSection("GUI");
				preferences.set("Frame.X", getX());
				preferences.set("Frame.Y", getY());
				preferences.set("Frame.Width", getWidth());
				preferences.set("Frame.Height", getHeight());
				preferences.write();
			}
			catch(Exception exception)
			{
				// On s'en fiche
			}
		}
		// On masque la fenêtre et on la détruit
		hide();
		dispose();
		// Libération des ressources allouées
		_tree.cleanBeforeExit();
		_desktopPane.cleanBeforeExit();
		_tree = null;
		_desktopPane = null;
		_statusBar = null;
		// On appelle la méthode d'arrêt du processus
		if(_processInterface != null)
		{
			_processInterface.stop();
		}
		_processInterface = null;
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _isConnected
	*
	* Description:
	* Cet attribut est un drapeau indiquant si la Console est connectée au
	* système I-SIS ou non. Il est mis à jour par la méthode setConnected().
	* ----------------------------------------------------------*/
	private boolean _isConnected;

	/*----------------------------------------------------------
	* Nom: _tree
	*
	* Description:
	* Cet attribut maintient une référence sur l'instance de MainWindowTree
	* correspondant à l'arbre d'exploration affiché dans la fenêtre principale.
	* ----------------------------------------------------------*/
	private MainWindowTree _tree;

	/*----------------------------------------------------------
	* Nom: _desktopPane
	*
	* Description:
	* Cet attribut maintient une référence sur l'instance de
	* MainWindowDesktopPane correspondant à la zone d'affichage des
	* sous-fenêtres affichée dans la fenêtre principale.
	* ----------------------------------------------------------*/
	private MainWindowDesktopPane _desktopPane;

	/*----------------------------------------------------------
	* Nom: _processInterface
	*
	* Description:
	* Cet attribut maintient une référence sur l'interface ProcessInterface
	* permettant à l'instance de MainWindow d'interagir avec le processus de
	* l'application (arrêt).
	* ----------------------------------------------------------*/
	private ProcessInterface _processInterface;

	/*----------------------------------------------------------
	* Nom: _statusBar
	*
	* Description:
	* Cet attribut maintient une référence sur une instance de JLabel
	* correspondant à la barre d'état de la fenêtre principale.
	* ----------------------------------------------------------*/
	private JLabel _statusBar;

	/*----------------------------------------------------------
	* Nom: _copyItem
	*
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu "Edition/
	* Copier". Il est nécessaire afin de modifier l'état de cet élément.
	* ----------------------------------------------------------*/
	private JMenuItem _copyItem;

	/*----------------------------------------------------------
	* Nom: _cutItem
	*
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu "Edition/
	* Couper". Il est nécessaire afin de modifier l'état de cet élément.
	* ----------------------------------------------------------*/
	private JMenuItem _cutItem;

	/*----------------------------------------------------------
	* Nom: _pasteItem
	*
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu "Edition/
	* Coller". Il est nécessaire afin de modifier l'état de cet élément.
	* ----------------------------------------------------------*/
	private JMenuItem _pasteItem;

	/*----------------------------------------------------------
	* Nom: _saveItem
	*
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu "Edition/
	* Sauver". Il est nécessaire afin de modifier l'état de cet élément.
	* ----------------------------------------------------------*/
	private JMenuItem _saveItem;

	/*----------------------------------------------------------
	* Nom: _connectItem
	*
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu
	* "Session/Ouvrir..." dont l'état doit être modifié en fonction des
	* ouvertures/fermetures de session sur le système I-SIS.
	* ----------------------------------------------------------*/
	private JMenuItem _connectItem;

	/*----------------------------------------------------------
	* Nom: _disconnectItem
	*
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu
	* "Session/Fermer" dont l'état doit être modifié en fonction des
	* ouvertures/fermetures de session sur le système I-SIS.
	* ----------------------------------------------------------*/
	private JMenuItem _disconnectItem;

	/*----------------------------------------------------------
	* Nom: _progressBar
	*
	* Description:
	* Cet attribut maintient une référence sur la barre de progression
	* de la barre d'état utilisée pour indiquer le niveau de 
	* progression de la tâche en cours.
	* ----------------------------------------------------------*/
	private JProgressBar _progressBar;
	
	/*----------------------------------------------------------
	* Nom: _userName
	*
	* Description:
	* Cet attribut maintient une référence sur un objet JLabel permettant
	* d'indiquer le nom de l'utilisateur ayant ouvert la session dans la
	* barre d'état.
	* ----------------------------------------------------------*/
	private JLabel _userName;
	
	/*----------------------------------------------------------
	* Nom: _searchString
	* 
	* Description:
	* Cet attribut contient la chaîne utilisée lors de la dernière recherche 
	* dans une zone de texte ou dans un tableau.
	* ----------------------------------------------------------*/
	private String _searchString;

	/*----------------------------------------------------------
	* Nom: _searchItem
	* 
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu 
	* "Edition/Rechercher". Il est nécessaire afin de modifier l'état de cet 
	* élément.
	* ----------------------------------------------------------*/
	private JMenuItem _searchItem;

	/*----------------------------------------------------------
	* Nom: _againItem
	* 
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu 
	* "Edition/Rechercher le suivant". Il est nécessaire afin de modifier 
	* l'état de cet élément.
	* ----------------------------------------------------------*/
	private JMenuItem _againItem;

	/*----------------------------------------------------------
	* Nom: _splitPane
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JSplitPane 
	* correspondant à la barre de séparation entre la zone d'exploration et la 
	* zone d'affichage des sous-fenêtres.
	* ----------------------------------------------------------*/
	private JSplitPane _splitPane;

	/*----------------------------------------------------------
	* Nom: _undoItem
	* 
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu 
	* "Edition/Annuler". Il est nécessaire afin de modifier l'état de cet 
	* élément.
	* ----------------------------------------------------------*/
	private JMenuItem _undoItem;

	/*----------------------------------------------------------
	* Nom: _redoItem
	* 
	* Description:
	* Cet attribut maintient une référence sur l'élément de menu 
	* "Edition/Rétablir". Il est nécessaire afin de modifier l'état de cet 
	* élément.
	* ----------------------------------------------------------*/
	private JMenuItem _redoItem;
	
	/*----------------------------------------------------------
	* Nom: copyData
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur sélectionne l'élément de
	* menu "Edition/Copier".
	* Elle copie dans le presse-papier de l'application du texte ayant été
	* préalablement sélectionné dans une zone de texte.
	* ----------------------------------------------------------*/
	private void copyData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "copyData");

		trace_methods.beginningOfMethod();
		// La première chose à faire est de vérifier l'état de l'élément de menu
		setEditionItemStates();
		if(_copyItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le composant dans lequel il faut copier
		Component component = _desktopPane.getFocusOwner();
		// Il faut que le composant soit de type JTextComponent
		if(!(component instanceof JTextComponent))
		{ 
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le composant depuis lequel il faut copier
		JTextComponent text_component = (JTextComponent)component;
		// On récupère le texte sélectionné
		String selected_text = text_component.getSelectedText();
		// Si il n'y a rien, on sort
		if(selected_text == null || selected_text.equals("") == true)
		{
			// Il n'y a rien à copier, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le Toolkit puis le Clipboard
		Toolkit toolkit = component.getToolkit();
		Clipboard system_clipboard = toolkit.getSystemClipboard();
		// On crée un objet StringSelection puis on le place dans le Clipboard
		StringSelection selection = new StringSelection(selected_text);
		system_clipboard.setContents(selection, selection);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: cutData
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur sélectionne l'élément de
	* menu "Edition/Couper".
	* Elle copie dans le presse-papier de l'application du texte ayant été
	* préalablement sélectionné dans une zone de texte, et supprime le texte
	* sélectionné, si cela est possible.
	* ----------------------------------------------------------*/
	private void cutData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "cutData");

		trace_methods.beginningOfMethod();
		// La première chose à faire est de vérifier l'état de l'élément de menu
		setEditionItemStates();
		if(_cutItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le composant dans lequel il faut couper
		Component component = _desktopPane.getFocusOwner();
		// Le composant doit être de type JTextComponent
		if(!(component instanceof JTextComponent))
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le composant depuis lequel il faut couper
		JTextComponent text_component = (JTextComponent)component;
		// On récupère le texte sélectionné
		String selected_text = text_component.getSelectedText();
		// Si il n'y a rien, on sort
		if(selected_text == null || selected_text.equals("") == true)
		{
			// Il n'y a rien à copier, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On efface la sélection (couper)
		text_component.replaceSelection("");
		// On récupère le Toolkit puis le Clipboard
		Toolkit toolkit = component.getToolkit();
		Clipboard system_clipboard = toolkit.getSystemClipboard();
		// On crée un objet StringSelection puis on le place dans le Clipboard
		StringSelection selection = new StringSelection(selected_text);
		system_clipboard.setContents(selection, selection);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: pasteData
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur sélectionne l'élément de
	* menu "Edition/Coller".
	* Elle copie dans une zone de texte ayant le focus le contenu du
	* presse-papier de l'application.
	* ----------------------------------------------------------*/
	private void pasteData()
	{
		String clipboard_text = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "pasteData");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// La première chose à faire est de vérifier l'état de l'élément de menu
		setEditionItemStates();
		if(_pasteItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le composant dans lequel il faut coller
		Component component = _desktopPane.getFocusOwner();
		// Le composant doit être de type JTextComponent
		if(!(component instanceof JTextComponent))
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		JTextComponent text_component = (JTextComponent)component;
		// On récupère le Toolkit puis le Clipboard
		Toolkit toolkit = text_component.getToolkit();
		Clipboard system_clipboard = toolkit.getSystemClipboard();
		// On récupère la donnée depuis le Clipboard
		Transferable clipboard_data = system_clipboard.getContents(null);
		// S'il n'y a rien, on sort
		if(clipboard_data == null)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On va essayer de convertir dans tous les formats supportés
		DataFlavor[] flavors = clipboard_data.getTransferDataFlavors();
		for(int index = 0 ; index < flavors.length ; index ++)
		{
			trace_debug.writeTrace("Essai avec: " +
				flavors[index].getHumanPresentableName());
			try
			{
			    clipboard_text = clipboard_data.getTransferData(
					flavors[index]).toString();
			}
			catch(Exception exception)
			{
				trace_debug.writeTrace("Echec de l'essai");
				continue;
			}
			// Y a-t-il eu une conversion ?
			if(clipboard_text != null && clipboard_text.equals("") == false)
			{
				// On peut sortir de la boucle
				break;
			}
		}
		// S'il n'y a rien à coller, on sort
		if(clipboard_text == null || clipboard_text.equals("") == true)
		{
			trace_methods.endOfMethod();
			return;
		}
		// Est-ce un remplacement d'une sélection
		if(text_component.getSelectionStart() == 
			text_component.getSelectionEnd())
		{
			// On effectue une insertion du texte à la position du curseur
			// en positionnant une fausse sélection
			text_component.setSelectionStart(
				text_component.getCaretPosition());
			text_component.setSelectionEnd(text_component.getCaretPosition());
		}
		text_component.replaceSelection(clipboard_text);
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: searchData
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'élément 
	* de menu "Rechercher...".
	* Une zone de saisie de la chaîne à rechercher est affichée, puis celle-ci 
	* est recherchée dans la zone de texte qui a le focus, ou dans le tableau 
	* qui a le focus. Lorsque la chaîne a été trouvée, celle-ci est 
	* sélectionnée, ou la ligne du tableau correspondant est sélectionnée.
	* Si la chaîne n'a pas pu être trouvée, une boite de dialogue est affichée.
	* ----------------------------------------------------------*/
	private void searchData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "searchData");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean found = false;

		trace_methods.beginningOfMethod();
		// On récupère le composant dans lequel il faut rechercher
		Component component = _desktopPane.getFocusOwner();
		// On vérifie que le composant correspond à une zone où l'on peut
		// rechercher
		if(!(component instanceof SearchableComponentInterface))
		{
			trace_errors.writeTrace("Composant incompatible avec la " + 
				"recherche !");
			// On commande la mise à jour des états
			setEditionItemStates();
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère la chaîne à rechercher
		String search_string = JOptionPane.showInputDialog(this, 
			MessageManager.getMessage("&SearchString"), _searchString);
		trace_debug.writeTrace("search_string=" + search_string);
		// Si la chaîne est nulle, c'est que l'utilisateur a annulé, on sort
		if(search_string == null || search_string.equals("") == true)
		{
			trace_methods.endOfMethod();
			return;
		}
		_searchString = search_string;
		// On appelle la méthode searchData sur l'objet
		// SearchableComponentInterface
		SearchableComponentInterface searchable_component =
			(SearchableComponentInterface)component;
		found = searchable_component.searchData(_searchString);
		if(found == false)
		{
			// Si on n'a pas trouvé la chaîne, on va afficher un message 
			// disant que la chaîne n'a pas été trouvée, et on sort
			JOptionPane.showMessageDialog(this, 
				MessageManager.getMessage("&StringNotFound"));
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: searchAgain
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'élément 
	* "Rechercher le suivant" du menu "Edition".
	* La recherche est basée sur la dernière position de la recherche et sur 
	* la chaîne de recherche.
	* ----------------------------------------------------------*/
	private void searchAgain()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "searchAgain");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean found = false;
		
		trace_methods.beginningOfMethod();
		// Y a-t-il une chaîne de recherche ?
		if(_searchString == null)
		{
			trace_errors.writeTrace("Il n'y a pas de chaîne de recherche !");
			// On met à jour les états des éléments du menu et on sort
			setEditionItemStates();
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le composant dans lequel il faut rechercher
		Component component = _desktopPane.getFocusOwner();
		// On vérifie que le composant correspond à une zone où l'on peut
		// rechercher
		if(!(component instanceof SearchableComponentInterface))
		{
			trace_errors.writeTrace("Composant incompatible avec la " + 
				"recherche !");
			// On commande la mise à jour des états
			setEditionItemStates();
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On appelle la méthode searchAgain sur l'objet
		// SearchableComponentInterface
		SearchableComponentInterface searchable_component =
			(SearchableComponentInterface)component;
		found = searchable_component.searchAgain(_searchString);
		if(found == false)
		{
			// Si on n'a pas trouvé la chaîne, on va afficher un message 
			// disant que la chaîne n'a pas été trouvée, et on sort
			JOptionPane.showMessageDialog(this, 
				MessageManager.getMessage("&StringNotFound"));
		}
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: saveData
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur sélectionne l'élément de
	* menu "Edition/Sauvegarder...".
	* Elle sauvegarde le contenu d'une zone de texte, ou d'une sélection, vers
	* un fichier choisi par l'utilisateur.
	* ----------------------------------------------------------*/
	private void saveData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "saveData");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// On récupère le composant contenant les informations à sauvegarder
		Component component = _desktopPane.getFocusOwner();
		// On vérifie que le composant correspond à une zone que l'on peut
		// sauvegarder
		if(!(component instanceof SearchableComponentInterface))
		{
			trace_errors.writeTrace("Composant incompatible avec la " + 
				"sauvegarde !");
			// On commande la mise à jour des états
			setEditionItemStates();
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		setCurrentCursor(Cursor.WAIT_CURSOR, null);
		setProgressMaximum(2);
		setStatus(MessageManager.getMessage("&Status_PreparingToSave"), null, 0);
		// On va afficher une boîte de dialogue de sélection du fichier
		// de destination de la sauvegarde
		JFileChooser file_chooser = new JFileChooser();
		// On va ajouter les filtres de fichier
		SearchableComponentInterface searchable_component =
			(SearchableComponentInterface)component;
		FileFilter[] filters = searchable_component.getFileFilters();
		if(filters != null && filters.length > 0) {
			for(int index = 0 ; index < filters.length ; index ++) {
				file_chooser.addChoosableFileFilter(filters[index]);
			}
		}
		int returned = file_chooser.showSaveDialog(this);
		if(returned == JFileChooser.CANCEL_OPTION)
		{
			// L'utilisateur a annulé, on sort
			trace_debug.writeTrace("L'utilisateur a annulé");
			setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
			setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		File choosen_file = file_chooser.getSelectedFile();
		String file_name = choosen_file.getAbsolutePath();
		trace_debug.writeTrace("file_name=" + file_name);
		// On vérifie que le fichier correspond au filtre sélectionné
		FileFilter choosen_filter = file_chooser.getFileFilter();
		if(choosen_filter.accept(choosen_file) == false &&
			choosen_filter instanceof FileNameExtensionFilter) {
			file_name = file_name.concat(
				((FileNameExtensionFilter)choosen_filter).getExtension());
			choosen_file = new File(file_name);
		}
		// On va vérifier si le fichier existe déjà
		if(choosen_file.exists() == true)
		{
			// On va afficher un message à l'utilisateur pour savoir s'il
			// veut écraser l'ancien fichier
			if(JOptionPane.showConfirmDialog(this,
				MessageManager.getMessage("&FileExists"),
				MessageManager.getMessage("&SaveData"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			{
				// L'utilisateur a annulé, on sort
				trace_debug.writeTrace("L'utilisateur a annulé !");
				trace_methods.endOfMethod();
				return; 
			}
		}
		setStatus(MessageManager.getMessage("&Status_SavingToFile"), null, 1);
		// On appelle la méthode saveDataToFile sur l'objet
		// SearchableComponentInterface
		searchable_component.saveDataToFile(choosen_file, this);
		setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
		setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: doUndo
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur sélectionne l'élément 
	* de menu "Edition/Annuler".
	* Elle appelle la méthode undo() de la zone ayant le focus, afin que 
	* celle-ci procède à l'annulation de la dernière tâche d'édition 
	* enregistrée.
	* ----------------------------------------------------------*/
	private void doUndo()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "doUndo");

		trace_methods.beginningOfMethod();
		// La première chose à faire est de vérifier l'état de l'élément de menu
		setEditionItemStates();
		if(_undoItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le composant gérant les actions d'annulation/
		// rétablissement
		Component component = _desktopPane.getFocusOwner();
		// Il faut que le composant soit de type UndoableComponentInterface
		if(!(component instanceof UndoableComponentInterface))
		{ 
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		UndoableComponentInterface undoable_component = 
			(UndoableComponentInterface)component;
		// On commande l'annulation
		undoable_component.undo();
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: doRedo
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur sélectionne l'élément 
	* de menu "Edition/Rétablir".
	* Elle appelle la méthode redo() de la zone ayant le focus, afin que 
	* celle-ci procède au rétablissement de la dernière annulation effectuée.
	* ----------------------------------------------------------*/
	private void doRedo()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "doRedo");

		trace_methods.beginningOfMethod();
		// La première chose à faire est de vérifier l'état de l'élément de menu
		setEditionItemStates();
		if(_redoItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le composant gérant les actions d'annulation/
		// rétablissement
		Component component = _desktopPane.getFocusOwner();
		// Il faut que le composant soit de type UndoableComponentInterface
		if(!(component instanceof UndoableComponentInterface))
		{ 
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		UndoableComponentInterface undoable_component = 
			(UndoableComponentInterface)component;
		// On commande le rétablissement
		undoable_component.redo();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: openAbout
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur sélectionne l'élément de
	* menu "?/A propos de...".
	* Elle ouvre une boîte de dialogue présentant des informations relatives à
	* l'application, via l'utilisation du service GUI de la librairie
	* BVCore/Java.
	* ----------------------------------------------------------*/
	private void openAbout()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "openAbout");

		trace_methods.beginningOfMethod();
		AboutDialog dialog = new AboutDialog(this,
			MessageManager.getMessage("&MW_Menu_About"));
		dialog.show();
		repaint();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: executeProcessor
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur un des items
	* de menu correspondant à un des processeurs globaux (un item du menu
	* "Outils"). Elle appelle la méthode executeProcessor de la classe
	* ProcessorManager.
	*
	* Arguments:
	*  - processorInterface: Une réféfence sur le processeur à exécuter,
	*  - menuItem: L'item de menu ayant déclenché l'exécution du processeur.
	* ----------------------------------------------------------*/
	private void executeProcessor(
		ProcessorInterface processorInterface,
		JMenuItem menuItem
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "executeProcessor");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("processorInterface=" + processorInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		// On change le curseur -> sablier
		setCurrentCursor(Cursor.WAIT_CURSOR, null);
		try
		{
			// Appel de la méthode executeProcessor de la classe
			// ProcessorManager. Il n'y a pas de paramètres, ni de noeud
			// sélectionné.
			ProcessorManager.executeProcessor(processorInterface, this, 
				menuItem, null, null, null, null, false, false);
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Erreur lors de l'exécution du processeur: " +
				exception);
			// On remet le curseur par défaut
			setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
			repaint();
			// Affichage d'une boîte d'erreur
		    DialogManager.displayDialogForException("&ERR_CannotOpenTool",
				this, exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette méthode est utilisée pour instancier et ajouter tous les objets
	* graphiques qui font partie de la fenêtre principale. Elle est appelée par
	* le constructeur de la classe.
	*
	* Elle crée la barre de menu en appelant la méthode makeMenuBar(), l'arbre
	* d'exploration en créant une instance de MainWindowTree, la zone
	* d'affichage des sous-fenêtres en instanciant la classe
	* MainWindowDesktopPane.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		GenericTreeClassNode root_node = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "makePanel");
		int orientation = 0;

		trace_methods.beginningOfMethod();
		// On va essayer de récupérer la position du séparateur
		// depuis les préférences, ainsi que l'orientation
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("GUI");
			orientation = preferences.getInt("Orientation");
		}
		catch(Exception exception)
		{
			// On s'en fiche
		}
		// Création du noeud racine bidon, de type GenericTreeClassNode
		// Ce noeud racine sera remplacé par un autre dans la méthode
		// loadInitialData().
		root_node = new GenericTreeClassNode(null, null, null, null, null, 
			null);
		// Création de l'arbre d'exploration
		_tree = new MainWindowTree(this, root_node);
		// On ajoute une rubrique d'aide pour l'arbre
		CSH.setHelpIDString(_tree, "ExplorationTree");
		// Création de la zone d'affichage des sous-fenêtres
		_desktopPane = new MainWindowDesktopPane();
		if(orientation == 0)
		{
			_desktopPane.setMinimumSize(new Dimension(100, 200));
		}
		else
		{
			_desktopPane.setMinimumSize(new Dimension(200, 100));
		}
		_desktopPane.putClientProperty("JDesktopPane.dragMode", "outline");
		// On ajoute une rubrique d'aide pour la zone d'affichage
		CSH.setHelpIDString(_desktopPane, "DisplayArea");
		// Création de la barre de menu
		JMenuBar menu_bar = makeMenuBar();
		// On ajoute une rubrique d'aide pour la barre de menu
		CSH.setHelpIDString(menu_bar, "MenuBar");

		// Ajout de la barre de menu à la fenêtre
		getContentPane().add(menu_bar, BorderLayout.NORTH);

		// Placement de l'arbre de navigation dans un scroll pane
		JScrollPane tree_scroll = new JScrollPane(_tree,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		if(orientation == 0)
		{
			tree_scroll.setMinimumSize(new Dimension(100, 200));
		}
		else
		{
			tree_scroll.setMinimumSize(new Dimension(200, 100));
		}

		if(orientation == 0)
		{
			// Création du split pane avec l'arbre dans la zone de gauche et
			// la zone d'affichage des sous-fenêtre dans la zone de droite.
			_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					tree_scroll, _desktopPane);
			_splitPane.setDividerLocation(300);
		}
		else
		{
			// Création du split pane avec l'arbre dans la zone supérieure et
			// la zone d'affichage des sous-fenêtre dans la zone inférieure.
			_splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					tree_scroll, _desktopPane);
			_splitPane.setDividerLocation(200);
		}
		// Ajout du split pane à la fenêtre
		getContentPane().add(_splitPane, BorderLayout.CENTER);

		// Création du panneau de la barre d'état
		JPanel status_panel = new JPanel(new BorderLayout());
		// On ajoute une rubrique d'aide pour la barre d'état
		CSH.setHelpIDString(status_panel, "StatusBar");
		// Création de la zone d'affichage du nom de l'utilisateur
		_userName = new JLabel();
		// Ajout d'une bordure à la barre d'état
		_userName.setBorder(BorderFactory.createLoweredBevelBorder());
		// Ajout de la barre d'état au panneau
		status_panel.add(_userName, BorderLayout.WEST);
		// Création de la barre d'état
		_statusBar = new JLabel();
		// Ajout d'une bordure à la barre d'état
		_statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		// Ajout de la barre d'état au panneau
		status_panel.add(_statusBar, BorderLayout.CENTER);
		// Création de la barre de progression
		_progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 1);
		// Ajout d'une bordure à la barre de progression
		_progressBar.setBorder(BorderFactory.createLoweredBevelBorder());
		// Ajout de la barre de progression au panneau
		status_panel.add(_progressBar, BorderLayout.EAST);
		// Ajout dpanneau à la fenêtre
		getContentPane().add(status_panel, BorderLayout.SOUTH);
		// Ajout du message par défaut
		setConnected(false, false);
		setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makeMenuBar
	*
	* Description:
	* Cette méthode est chargée de construire la barre de menu de la fenêtre
	* principale.
	* Cette barre de menu contient les menu et éléments suivants:
	*  - Menu "Session"
	*    - Elément "Quitter"
	*  - Menu "Edition"
	*    - Elément "Copier"
	*    - Elément "Couper"
	*    - Elément "Coller"
	*    - Elément "Imprimer..."
	*    - Elément "Sauvegarder..."
	*  - Menu "Outils" (construit à partir de la liste des processeurs globaux
	*    fourni par la classe ProcessorManager),
	*  - Menu "Fenêtres" (construit et géré par l'instance de
	*    MainWindowDesktopPane)
	*  - Menu "?"
	*    - Elément "Aide"
	*    - Elément "A propos de..."
	*
	* Tous ces éléments de menu, hormis ceux du menu Fenêtres, appellent une
	* méthode correspondante de la classe MainWindow.
	*
	* Retourne: Une instance de JMenuBar correspondant à la barre de menu créée.
	* ----------------------------------------------------------*/
	private JMenuBar makeMenuBar()
	{
		JMenuBar menu_bar = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "makeMenuBar");

		trace_methods.beginningOfMethod();
		// Création de la barre de menu
		menu_bar = new JMenuBar();

		// Création du menu Session
		JMenu session_menu = new MainWindowMenu("Session");
		// Création de l'item Ouvrir
		_connectItem = new MainWindowMenuItem("Connect");
		// Ajout du callback sur l'item
		_connectItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode d'ouverture de session
				openSession();
			}
		});
		// Ajout de l'item au menu
		session_menu.add(_connectItem);
		// Création de l'item Fermer
		_disconnectItem = new MainWindowMenuItem("Disconnect");
		// Ajout du callback sur l'item
		_disconnectItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture de session dans un
				// thread séparé
				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						closeSession(false);
					}
				});
				thread.start();
			}
		});
		// Ajout de l'item au menu
		session_menu.add(_disconnectItem);
		// On ajoute un séparateur
		session_menu.addSeparator();
		// Création de l'item Configurer
		JMenuItem configure_item = new MainWindowMenuItem("Configure");
		// Ajout du callback sur l'item
		configure_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						// Appel de la méthode exitWindow
						setPreferences();
					}
				});
				thread.start();
			}
		});
		// Ajout de l'item au menu
		session_menu.add(configure_item);
		// On ajoute un séparateur
		session_menu.addSeparator();
		// Création de l'item Quitter
		JMenuItem quit_item = new MainWindowMenuItem("Quit");
		// Ajout du callback sur l'item
		quit_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode exitWindow dans un thread séparé
				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						exitWindow(true, false);
					}
				});
				thread.start();
			}
		});
		// Ajout de l'item au menu
		session_menu.add(quit_item);
		// Ajout du menu à la barre de menu
		menu_bar.add(session_menu);

		// Création du menu Edition
		JMenu edition_menu = new MainWindowMenu("Edition");
		edition_menu.addMenuListener(new javax.swing.event.MenuListener()
		{
			public void menuSelected(javax.swing.event.MenuEvent event)
			{
				setEditionItemStates();
			}

			public void menuDeselected(javax.swing.event.MenuEvent event)
			{
			}

			public void menuCanceled(javax.swing.event.MenuEvent event)
			{
			}
		});
		// Création de l'item Annuler
		_undoItem = new MainWindowMenuItem("Undo");
		// Ajout du callback sur l'item
		_undoItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode doUndo
				doUndo();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_undoItem);
		// Création de l'item Rétablir
		_redoItem = new MainWindowMenuItem("Redo");
		// Ajout du callback sur l'item
		_redoItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode doRedo
				doRedo();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_redoItem);
		// Ajout d'une séparation
		edition_menu.addSeparator();
		// Création de l'item Copier
		_copyItem = new MainWindowMenuItem("Copy");
		// Ajout du callback sur l'item
		_copyItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode copyData
				copyData();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_copyItem);
		// Création de l'item Couper
		_cutItem = new MainWindowMenuItem("Cut");
		// Ajout du callback sur l'item
		_cutItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode cutData
				cutData();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_cutItem);
		// Création de l'item Coller
		_pasteItem = new MainWindowMenuItem("Paste");
		// Ajout du callback sur l'item
		_pasteItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode pasteData
				pasteData();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_pasteItem);
		// Ajout d'une séparation
		edition_menu.addSeparator();
		// Création de l'item Rechercher
		_searchItem = new MainWindowMenuItem("Search");
		// Ajout du callback sur l'item
		_searchItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode searchData
				searchData();
			}
		});
		edition_menu.add(_searchItem);
		// Création de l'item Rechercher le suivante
		_againItem = new MainWindowMenuItem("Again");
		// Ajout du callback sur l'item
		_againItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode searchData
				searchAgain();
			}
		});
		edition_menu.add(_againItem);
		// Ajout d'une séparation
		edition_menu.addSeparator();
		// Création de l'item Sauvegarder
		_saveItem = new MainWindowMenuItem("Save");
		// Ajout du callback sur l'item
		_saveItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						// Appel de la méthode saveData
						saveData();
					}
				});
				thread.start();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_saveItem);
		// Ajout du menu à la barre de menu
		menu_bar.add(edition_menu);

		// Création du menu Outils
		JMenu tools_menu = new MainWindowMenu("Tools");
		// Récupération de la liste des processeurs globaux
		ProcessorInterface[] processors = 
			ProcessorManager.getGlobalProcessors();
		if(processors != null && processors.length > 0)
		{
			// Création des items de menu pour les processeurs
			for(int index = 0 ; index < processors.length ; index ++)
			{
				final ProcessorInterface processor_interface = 
					processors[index];
				// On crée l'item de menu
				final JMenuItem tool_item =
					new MainWindowMenuItem(processor_interface.getMenuLabel());
				// Ajout du callback de click sur l'item
				tool_item.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						// Appel de la méthode d'exécution du processeur
						executeProcessor(processor_interface, tool_item);
					}
				});
				// Ajout de l'item au menu
				tools_menu.add(tool_item);
			}
		}
		else
		{
			tools_menu.setEnabled(false);
		}
		// Ajout du menu à la barre de menu
		menu_bar.add(tools_menu);

		// Ajout du menu fenêtre à la barre de menu
		menu_bar.add(_desktopPane.getMenu());

		// Création du menu ? (aide)
		JMenu help_menu = new MainWindowMenu("?");
		// Initialisation de l'API JavaHelp
		HelpSet help_set;
		try
		{
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			String help_repository =
				configuration_api.getString("CORE", "Help.File.Repository");
			String helpset_file = help_repository + File.separator +
				MessageManager.getCurrentLanguage() + File.separator +
				"Explorer.hs";
			URL help_url = new File(helpset_file).toURL();
			help_set = new HelpSet(null, help_url);
			// On crée le help broker
			final HelpBroker help_broker = help_set.createHelpBroker();
			// Création de l'item Aide
			JMenuItem general_help_item = new MainWindowMenuItem("GeneralHelp");
			general_help_item.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					help_broker.setCurrentID("top");
					help_broker.setDisplayed(true);
				}
			});
			// Ajout de l'item au menu
			help_menu.add(general_help_item);
			// Création de l'item d'aide contextuelle
			JMenuItem contextual_help_item =
				new MainWindowMenuItem("ContextualHelp");
			contextual_help_item.addActionListener(
				new CSH.DisplayHelpAfterTracking(help_broker));
			contextual_help_item.setAccelerator(KeyStroke.getKeyStroke("F1"));
			help_broker.enableHelpKey(this, "top", help_set);
			// Ajout de l'item au menu
			help_menu.add(contextual_help_item);
			// Ajout d'un séparateur
			help_menu.addSeparator();
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de l'initialisation de JavaHelp: " +
				exception.getMessage());
			showPopupForException("&ERR_CannotInitializeHelpSystem", exception);
		}
		// Création de l'item A propos
		JMenuItem about_item = new MainWindowMenuItem("About");
		// Ajout du callback sur l'item
		about_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode openAbout
				openAbout();
			}
		});
		// Ajout de l'item au menu
		help_menu.add(about_item);
		// Ajout du menu à la barre de menu en tant que menu d'aide
		// TBD - A modifier avec une prochaine version du JDK
		//menu_bar.setHelpMenu(help_menu);
		menu_bar.add(help_menu);

		trace_methods.endOfMethod();
		return menu_bar;
	}

	/*----------------------------------------------------------
	* Nom: setEditionItemStates
	*
	* Description:
	* Cette méthode est appelée lorsque le menu "Edition" est sélectionné
	* (activé) par l'utilisateur. Elle permet de mettre à jour l'état des
	* différents éléments du menu en fonction du propriétaire du focus au moment
	* de l'appel, et de la présence ou non de texte sélectionné.
	* ----------------------------------------------------------*/
	private void setEditionItemStates()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "setEditionItemStates");

		trace_methods.beginningOfMethod();
		// Par défaut, ils sont tous désactivés
		_undoItem.setEnabled(false);
		_redoItem.setEnabled(false);
		_copyItem.setEnabled(false);
		_cutItem.setEnabled(false);
		_pasteItem.setEnabled(false);
		_searchItem.setEnabled(false);
		_againItem.setEnabled(false);
		_saveItem.setEnabled(false);
		// On récupère le composant qui a le focus
		Component focus_owner = _desktopPane.getFocusOwner();
		// S'il n'y a pas de composant, on sort
		if(focus_owner == null)
		{
			trace_methods.endOfMethod();
			return;
		}
		// Le composant peut être de type JTextComponent
		if(focus_owner instanceof JTextComponent)
		{
			JTextComponent text_component = (JTextComponent)focus_owner;
			// Récupération du texte sélectionné
			String selected_text = text_component.getSelectedText();
			if(selected_text != null && selected_text.equals("") == false)
			{
				// Il y a du texte sélectionné, on peut copier
				_copyItem.setEnabled(true);
			}
			if(text_component.isEditable() == true && 
				!(text_component instanceof NonEditableTextArea ||
				text_component instanceof NonEditableTextPane))
			{
				// Si l'item copier est actif, on peut aussi couper
				_cutItem.setEnabled(_copyItem.isEnabled());
				// La zone est éditable, on peut coller
				_pasteItem.setEnabled(true);
			}
		}
		// Le composant peut-être de type SearchableComponentInterface
		if(focus_owner instanceof SearchableComponentInterface)
		{
			// L'élément de recherche peut être activé
			_searchItem.setEnabled(true);
			// L'élément d'enregistrement est actif
			_saveItem.setEnabled(true);
		}
		// Le composant peut-être de type UndoableComponentInterface
		if(focus_owner instanceof UndoableComponentInterface)
		{
			// On va activer les éléments d'annulation/rétablissement
			// en fonction des possibilités du composant
			UndoableComponentInterface undoable_component = 
				(UndoableComponentInterface)focus_owner;
			_undoItem.setEnabled(undoable_component.canUndo());
			_redoItem.setEnabled(undoable_component.canRedo());
		}
		// S'il y a une chaîne de recherche et que l'item de
		// recherche est actif, on peut activer le bouton de
		// recherche du suivant
		if(_searchString != null && _searchString.equals("") == false &&
			_searchItem.isEnabled() == true)
		{
			_againItem.setEnabled(true);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setPreferences
	*
	* Description:
	* Cette méthode est appelée automatiquement lors de la tentative
	* d'ouverture de session si la Console n'a pas encore été configurée, ou
	* lorsque l'utilisateur a cliqué sur le menu "Connexion/Configurer...".
	*
	* Retourne: Un booléen indiquant si l'utilisateur a validé (true) ou non
	* (false) sa configuration.
	* ----------------------------------------------------------*/
	private boolean setPreferences()
	{
		boolean return_value;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "setPreferences");

		trace_methods.beginningOfMethod();
		setCurrentCursor(Cursor.WAIT_CURSOR, null);
		ConfigurationDialog dialog = new ConfigurationDialog(this);
		setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
		return_value = dialog.getConfiguration();
		setConnected(_isConnected, false);
		if(return_value == true)
		{
			if(_isConnected == true)
			{
				// Si l'utilisateur a validé ses changements, et qu'une
				// session est ouverte, il faut l'informer que ceux-ci seront
				// pris en compte lors de la prochaine ouverture de session
			    showPopup("Information", "&MustReconnect", null);
			}
			else if(PortalInterfaceProxy.isInitialized() == true)
			{
				// Si l'utilisateur a validé ses changements, qu'aucune session
				// n'est ouverte, mais que la connexion avec le Portail a déjà
				// été réalisée au moins une fois, on doit réinitialiser
				// le PortalInterfaceProxy pour prendre en compte les nouveaux
				// paramètres.
				PortalInterfaceProxy.cleanBeforeExit(false);
			}
		}
		trace_methods.endOfMethod();
		return return_value;
	}

	/*----------------------------------------------------------
	* Nom: checkConfiguration
	* 
	* Description:
	* Cette méthode est permet de contrôler si tous les processeurs de la 
	* Console ont été correctement configurés ou non.
	* Elle va appeler la méthode isConfigured() pour chaque ProcessorInterface 
	* de la liste retournée par la méthode getAllProcessors() de la classe 
	* ProcessorManager.
	* 
	* Retourne: true si tous les processeurs ont été correctement configurés, 
	* false sinon.
	* ----------------------------------------------------------*/
	private boolean checkConfiguration()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "checkConfiguration");

		trace_methods.beginningOfMethod();
		// On va récupérer la liste des processeurs
		ProcessorInterface[] processors = ProcessorManager.getAllProcessors();
		for(int index = 0 ; index < processors.length ; index ++)
		{
			if(processors[index].isConfigured() == false)
			{
				// On va retourner false
				trace_methods.endOfMethod();
				return false;
			}
		}
		trace_methods.endOfMethod();
		return true;
	}
}