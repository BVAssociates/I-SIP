/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/MainWindow.java,v $
* $Revision: 1.38 $
*
* ------------------------------------------------------------
* DESCRIPTION: Fen�tre principale de l'application
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
* Ic�ne de la fen�tre : ISIS.gif
*
* Revision 1.33  2008/02/15 14:13:55  tz
* Prise en compte de la m�thode getFileFilters() dans la m�thode saveData().
*
* Revision 1.32  2008/01/31 16:40:28  tz
* Prise en compte de l'ajout d'un argument �
* ProcessorManager.executeProcessor().
*
* Revision 1.31  2007/09/24 10:40:31  tz
* Gestion de l'orientation des zones.
*
* Revision 1.30  2007/04/12 15:15:38  ml
* Nombre de profil en param�tre dans Console_config.ini
*
* Revision 1.29  2006/11/03 10:28:28  tz
* Ajout des m�thodes doUndo() et doRedo(), et gestion de l'annulation/
* r�tablissement des actions d'�dition.
*
* Revision 1.28  2006/03/08 16:20:12  tz
* Enregistrement et restitution de la position du diviseur
*
* Revision 1.27  2006/03/07 09:27:13  tz
* Attente d'un �tat OPENED ou CLOSED avant la fermeture de la Console.
*
* Revision 1.26  2005/12/23 13:17:18  tz
* M�thode exitWindow() publique (cf interface MainWindowInterface).
*
* Revision 1.25  2005/10/07 08:44:13  tz
* Gestion du profil de connexion au Portail par les propri�t�s syst�me.
*
* Revision 1.24  2005/10/07 08:41:51  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
* Impl�mentation de l'interface ConsoleIsisEventsListenerInterface.
* Utilisation des interfaces SearchableComponentInterface pour la recherche
* de texte et la sauvegarde.
*
* Revision 1.23  2005/07/01 12:27:23  tz
* Modification du composant pour les traces
* Suppression de la fonctionnalit� d'impression
* Correction des fonctionnalit� de copie/collage
* Ajout de la fonctionnalit� de recherche
*
* Revision 1.22  2004/11/09 15:26:19  tz
* Ajout de IORFinder.cleanBeforeExit() et de
* IsisEventsListenerImpl.cleanBeforeExit().
*
* Revision 1.21  2004/11/03 15:20:59  tz
* Suppression de l'affichage des �v�nements sur System.out.
*
* Revision 1.20  2004/11/02 09:08:08  tz
* Traitement du cas de la fermeture de la session Portail.
*
* Revision 1.19  2004/10/22 15:44:22  tz
* Gestion des �v�nements I-SIS,
* Modification de la v�rification et de la modification de la configuration.
*
* Revision 1.18  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.17  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.16  2004/10/06 07:41:27  tz
* Fermeture de l'application dans le thread courant,
* Gestion de la barre de s�paration.
*
* Revision 1.15  2004/07/29 12:21:08  tz
* Suppression d'imports inutiles
* Traitement de la destruction des noeuds en cas d'arr�t du portail
* Affichage du nom de l'utilisateur et du portail dans la barre d'�tat
*
* Revision 1.14  2003/06/10 13:58:20  tz
* Utilisation du nouveau vocabulaire (pour l'aide)
*
* Revision 1.13  2003/03/10 15:42:43  tz
* Ajout de la m�thode isConnected()
*
* Revision 1.12  2003/03/07 16:22:14  tz
* Ajout du m�canisme de log m�tier.
* Les m�thodes masterStopped() sont d�clar�es oneway.
*
* Revision 1.11  2002/12/26 12:57:30  tz
* Correction de la fiche Inuit/84
*
* Revision 1.10  2002/11/19 08:46:32  tz
* Gestion de la progression de la t�che.
*
* Revision 1.9  2002/09/20 10:40:12  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.8  2002/08/26 09:51:26  tz
* Utilisation du nouveau MasterInterfaceProxy.
*
* Revision 1.7  2002/08/13 13:16:08  tz
* Utilisation des pr�f�rences.
* Modification de la m�thode setCurrentCursor
*
* Revision 1.6  2002/06/19 12:18:40  tz
* Traitement du masterStopped() dans un thread s�par�
*
* Revision 1.5  2002/04/05 15:47:21  tz
* Cloture it�ration IT1.2
*
* Revision 1.4  2002/03/27 09:42:06  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.3  2002/02/04 10:54:24  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.2  2001/12/19 09:58:49  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1  2001/11/19 17:07:54  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.gui;

//
// Imports syst�me
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
* Cette classe repr�sente la fen�tre principale qui est affich�e �
* l'utilisateur. Il s'agit de la fen�tre de travail dans laquelle seront
* pr�sent�s les objets suivants:
*  - une barre de menu,
*  - un arbre d'exploration,
*  - une zone d'affichage des sous-fen�tres,
*  - une barre d'�tat.
*
* Elle est charg�e de traiter tous les �v�nements relatifs aux �l�ments de la
* barre de menu, et toutes les notifications en provenance du processus Portail
* (elle impl�mente l'interface ConsoleIsisEventsListenerInterface). Elle 
* impl�mente �galement l'interface MainWindowInterface qui est utilis�e, entre 
* autres, par le m�canisme de processeur de t�che.
*
* Les �v�nements relatifs � la manipulation de l'arbre d'exploration ainsi
* qu'aux sous-fen�tres sont d�l�gu�s aux objets correspondants (MainWindowTree
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
	* Cette m�thode est le seul constructeur de la classe. Il permet de cr�er
	* une instance de MainWindow en passant une r�f�rence sur l'interface
	* ProcessInterface (n�cessaire � l'arr�t de l'application) en argument.
	* Elle cr�e la fen�tre en appelant la m�thode makePanel.
	* Ensuite, elle affiche la fen�tre � l'utilisateur.

	* Arguments:
	*  - processInterface: Une r�f�rence sur l'interface ProcessInterface
	*    permettant � la fen�tre principale d'arr�ter l'application.
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
		// Ajout du callback sur la fermeture de la fen�tre
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				// Appel de la m�thode de sortie
				exitWindow(true, false);
			}
		});
		// Suppression du comportement par d�faut de la fen�tre
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Positionnement du titre de la fen�tre
		setTitle(MessageManager.getMessage("&AD_ProductName") + " " +
			MessageManager.getMessage("&AD_ApplicationName"));
		// Fabrication de la fen�tre
		makePanel();
		// On va tenter de r�cup�rer les informations de pr�f�rences
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
		// R�cup�ration de la taille de l'�cran
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen_size = toolkit.getScreenSize();
		// On v�rifie les valeurs
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
		// Dimensionnement de la fen�tre
		setSize(width, height);
		// Les positions x et y sont elles valables ?
		if(x_position == -1000 || y_position == -1000)
		{
			// Centrage de la fen�tre au milieu de l'�cran
			// R�cup�ration de la taille de la fen�tre
			Dimension window_size = getSize();
			// Calcul des coordonn�es de la fen�tre
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
		// D�placement de la fen�tre
		setLocation(x_position, y_position);
		// On ajoute l'ic�ne de la fen�tre
		if(IconLoader.getIcon("ISIS") != null)
		{
		    setIconImage(IconLoader.getIcon("ISIS").getImage());
		}
		// Affichage de la fen�tre
		show();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: openSession
	*
	* Description:
	* Cette m�thode est charg�e d'ouvrir une session sur le syst�me I-SIS. Elle
	* est appel�e au d�marrage de l'application et lorsque l'utilisateur a
	* cliqu� sur l'�l�ment de menu "Session/Ouvrir...". En fait, il s'agit
	* principalement de construire le noeud racine de l'arbre avec des
	* informations et d'ex�cuter le processeur d'ouverture de session (qui
	* effectuera l'ouverture de la session sur le Portail).
	* C'est par le noeud cr�� par le processeur d'ouverture de session que
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
		// On essaye de savoir si la Console a d�j� �t� configur�e
		if(checkConfiguration() == false)
		{
			// La Console n'a pas encore �t� configur�e, il faut le faire
			// On va afficher un message � l'utilisateur pour lui indiquer
			// qu'il doit configurer l'interface
			showPopup("Information", "&ConsoleMustBeConfigured", null);
			// On appelle la m�thode de modification des pr�f�rences
			boolean is_validated = setPreferences();
			if(is_validated == false)
			{
				// L'utilisateur n'a pas positionn� les pr�f�rences, on sort
				trace_methods.endOfMethod();
				return;
			}
		}
		// On va lire les profils depuis le fichier de propri�t�s
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
			// Il faut afficher un message � l'utilisateur
			showPopupForException("&ERR_InitialDataLoadingFailure", exception);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		
		//On va tenter de r�cup�rer le nombre de profils maximum dans le fichier Console_config.ini
		try
		{
			ConfigurationAPI config_api = new ConfigurationAPI();
			number_of_profiles = config_api.getInt("Console","NumberOfProfiles");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la r�cup�ration du nombre de profils maximum: " +
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
			trace_errors.writeTrace("Aucun profil n'est renseign� !");
			trace_methods.endOfMethod();
			return;
		}
		// S'il y a plus d'un profil, on demande � l'utilisateur de choisir
		if(profile_labels.size() > 1)
		{
			preferred_profile = System.getProperty("Profile.Label", "");
			// On va r�cup�rer l'indice du profil pr�c�dent, s'il y en a un
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
				trace_debug.writeTrace("L'utilisateur a annul�");
				// L'utilisateur a annul�, on sort
				trace_methods.endOfMethod();
				return;
			}
			// On va r�cup�rer l'indice du profil s�lectionn�
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
			// On a chang� de Portail
			// On efface tous les �v�nements du processeur d'affichage
			// des �v�nements I-SIS
			IsisEventsListenerImpl.getInstance().fireClearEvents();
			// Il faut �galement supprimer le cache des dictionnaires
			TableDefinitionManager.getInstance().clear();
		}
		// On va r�cup�rer les informations du profil s�lectionn�, et les 
		// stocker dans les propri�t�s syst�me
		System.getProperties().put("Profile.Label", 
			(String)profile_labels.elementAt(chosen_profile));
		System.getProperties().put("Profile.Configuration",
			(String)configurations.elementAt(chosen_profile));
		setConnected(false, true);
		// On change le curseur -> sablier
		setCurrentCursor(Cursor.WAIT_CURSOR, null);
		// Affichage de l'�tat
		setProgressMaximum(2);
		setStatus("&Status_ConnectingToPortal", null, 0);
		try
		{
			// Enregistrement aupr�s du proxy d'interface Portail
			PortalInterfaceProxy portal_proxy =
				PortalInterfaceProxy.getInstance();
			IsisEventsListenerImpl.getInstance().addIsisEventsListener(this);
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'enregistrement en tant que listener");
			// On s'assure de bien nettoyer la r�f�rence au Portail
			closeSession(true);
			// Il y a eu une erreur lors de l'enregistrement
			// Il faut afficher un message � l'utilisateur
			showPopupForException("&ERR_InitialDataLoadingFailure", exception);
			// On remet le curseur par d�faut
			setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
			// On sort
			setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		setStatus("&Status_ConnectingToPortal", null, 1);
		// On va r�cup�rer les informations permettant de construire le noeud
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
			// Il y a eu une erreur, il faut afficher l'erreur � l'utilisateur
			InnerException inner_exception = CommonFeatures.processException(
				"de la r�cup�ration de la configuration", exception);
			showPopupForException("&ERR_InitialDataLoadingFailure",
				inner_exception);
			// On s'assure de bien nettoyer la r�f�rence au Portail
			closeSession(false);
			// On remet le curseur par d�faut
			setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
			setStatus(null, null, 0);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On cr�e le tableau d'IsisParameter � partir des donn�es
		// pr�c�dentes
		IsisParameter[] parameters = new IsisParameter[1];
		// Nom de l'Agent
		parameters[0] =
			new IsisParameter(agent_parameter_name, "Portal", '"');
		// On cr�e le noeud
		GenericTreeObjectNode root_node =
			new GenericTreeObjectNode(parameters, "", "", "", "", "", "");
		// On positionne le noeud en tant que noeud racine de l'arbre
		((DefaultTreeModel)_tree.getModel()).setRoot(root_node);
		setStatus("&Status_ConnectingToPortal", null, 2);
		// On ex�cute le processeur d'ouverture de session sur ce noeud
		try
		{
		    ProcessorManager.executeProcessor("OpenAgentSession", this, null,
				null, null, null, root_node, false, false);
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors de l'ex�cution du processeur" +
				" d'ouverture de session: " + exception.getMessage());
			// On affiche un message � l'utilisateur
			showPopupForException("&ERR_InitialDataLoadingFailure", exception);
			// On s'assure de bien nettoyer la r�f�rence au Portail
			closeSession(false);
		}
		setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getDesktopPane
	*
	* Description:
	* Cette m�thode red�finit la m�thode de l'interface MainWindowInterface.
	* Elle permet � une classe de r�cup�rer la r�f�rence sur l'objet
	* correspondant � la zone d'affichage des sous-fen�tres.
	*
	* Retourne: Une r�f�rence sur un objet JDesktopPane.
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
	* Cette m�thode red�finit la m�thode de l'interface MainWindowInterface. Elle
	* permet � une classe de l'application de r�cup�rer une r�f�rence sur
	* l'interface TreeInterface permettant d'interagir avec l'arbre
	* d'exploration.
	*
	* Retourne: Une r�f�rence sur l'interface TreeInterface.
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
	* Cette m�thode red�finit la m�thode de l'interface MainWindowInterface. 
	* Elle permet � une classe de l'application d'afficher des informations 
	* dans la barre d'�tat de la fen�tre principale.
	*
	* Arguments:
	*  - status: Les donn�es � afficher dans la barre d'�tat,
	*  - extraInformation: Des informations compl�mentaires � ajouter dans la
	*    cha�ne d'�tat,
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
					// On traduit les informations compl�mentaires
					extraInformation[index] =
						MessageManager.getMessage(extraInformation[index]);
				}
				// On ins�re les informations suppl�mentaires dans le message
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
			// On est d�j� dans le thread swing, on appelle directement la
			// m�thode setCursor()
			_statusBar.setText(status_message);
			_statusBar.repaint();
			_statusBar.validate();
			_progressBar.setValue(progress);
			_progressBar.repaint();
			_progressBar.validate();
		}
		else
		{
			// On n'est pas dans le thread swing, on va appeler la m�thode dans
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
	* Cette m�thode red�finit celle de l'interface MainWindowInterface.
	* Elle est appel�e pour positionner la valeur maximale de la
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
	* Cette m�thode red�finit la m�thode de l'interface MainWindowInterface.
	* Elle permet � une classe de l'application d'afficher une bo�te de dialogue
	* � l'utilisateur. Elle utilise la classe DialogManager.
	*
	* Argument:
	*  - popupType: Le type de bo�te de dialogue � afficher. Ce type correspond
	*    aux types d�finis pour la m�thode displayDialog() de la classe
	*    MessageManager de la librairie BVCore/Java,
	*  - message: Le message � afficher dans la bo�te de dialogue,
	*  - extraInfo: Un tableau de String contenant des informations
	*    compl�mentaires.
	*
	* Retourne: Un entier repr�sentant le bouton sur lequel l'utilisateur a
	* cliqu� pour fermer la bo�te de dialogue. Cette valeur correspond aux types
	* d�finis dans la classe JOptionPane.
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
	* Cette m�thode red�finit celle de l'interface MainWindowInterface. Elle est
	* appel�e lorsqu'un message d'erreur doit �tre affich� � l'utilisateur suite
	* � la capture d'une exception. Elle utilise la classe DialogManager.
	*
	* Arguments:
	*  - message: Le message principal de l'erreur,
	*  - exception: Une r�f�rence sur l'exception qui a �t� captur�e.
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
	* Cette m�thode red�finit celle de l'interface MainWindowInterface. Elle est
	* appel�e pour indiquer si la Console est connect�e ou non.
	*
	* Arguments:
	*  - isConnected: Indique si la Console est connect�e (true) ou non (false),
	*  - showProfileLabel: Indique si l'intitul� du profil doit �tre affich� ou 
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
		// On r�cup�re le nom du profil de connexion
		current_profile_info = System.getProperty("Profile.Label"); 
		// On stocke l'information
		_isConnected = isConnected;
		// On modifie l'ic�ne de la barre d'�tat
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
		// Positionnement du titre de la fen�tre
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
	* Cette m�thode red�finit la m�thode de l'interface
	* ConsoleIsisEventsListenerInterface. Elle est appel�e lorsqu'un
	* �v�nement I-SIS survient.
	* S'il s'agit de l'arr�t du Portail, elle appelle la m�thode closeSession() 
	* et affiche une bo�te de dialogue signalant l'arr�t de l'application.
	* 
	* Arguments:
	*  - eventType: Le type d'�v�nement,
	*  - eventInformation: Un tableau contenant les informations d'�v�nement.
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
		// On r�cup�re le noeud racine
		DefaultMutableTreeNode real_root_node =
			(DefaultMutableTreeNode)_tree.getModel().getRoot();
		if(!(real_root_node instanceof GenericTreeObjectNode)) {
			// Le noeud racine n'est pas du bon type, on peut sortir
			trace_methods.endOfMethod();
			return;
		}
		GenericTreeObjectNode root_node = 
			(GenericTreeObjectNode)real_root_node;
		// S'il s'agit de l'arr�t du Portail on informe l'utilisateur et on
		// sort
		if(eventType == IsisEventTypeEnum.PORTAL_STOPPED)
		{
			// On ferme la session
			closeSession(true);
			// Il faut afficher un message � l'utilisateur
			showPopup("Information", "&PortalStopped", null);
			trace_methods.endOfMethod();
			return;
		}
		else if(eventType == IsisEventTypeEnum.AGENT_STOPPED)
		{
			// Un Agent s'est arr�t�, on va effectuer un nettoyage
			// des noeuds qui en d�pendent
			event_processed = 
				root_node.forwardEvent(eventType, eventInformation, _tree);
			// On va �galement nettoyer les leasings des sessions Agent
			AgentSessionManager.getInstance().releaseAgentSessionNoClose(
				eventInformation[0]);
			message = "&AgentStopped";
		}
		else if(eventType == IsisEventTypeEnum.A_SESSION_CLOSED ||
			eventType == IsisEventTypeEnum.S_SESSION_CLOSED)
		{
			// Une session Agent ou de service a �t� ferm�e, on va
			// effectuer un nettoyage des noeuds qui en d�pendent
			event_processed = 
				root_node.forwardEvent(eventType, eventInformation, _tree);
			if(eventType == IsisEventTypeEnum.A_SESSION_CLOSED)
			{
				message = "&AgentSessionClosed";
				// On v�rifie que le noeud ferm� n'est pas le noeud de la
				// session Portail (le noeud racine n'a plus d'enfant)
				if(root_node.getChildCount() == 0 && 
					eventInformation[0].equals("Portal") == true &&
					event_processed == true)
				{
					message = "&PortalSessionClosed";
					eventInformation = null;
					// On d�connecte l'utilisateur
					((DefaultTreeModel)_tree.getModel()).setRoot(
						new DefaultMutableTreeNode());
					// On change l'�tat de la session
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
		// Si l'�v�nement a �t� trait�, il faut afficher un message �
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
	* Cette m�thode red�finit la m�thode de l'interface 
	* ConsoleIsisEventsListenerInterface. Elle est appel�e lorsque les 
	* �v�nements I-SIS pr�c�demment enregistr�s doivent �tre supprim�s.
	* La m�thode n'a aucun comportement.
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
	* Cette m�thode permet de modifier le curseur de la souris. Elle est
	* surtout utilis�e pour mettre le curseur d'attente lors de l'ex�cution d'un
	* processeur, et de replacer le curseur normal ensuite.
	* Si l'argument component est non nul, le curseur est �galement positionn� 
	* sur celui-ci.
	*
	* Argument:
	*  - cursor: Un entier indiquant le type de cursor a utiliser,
	*  - component: Une r�f�rence sur un composant optionnel sur lequel le 
	*    curseur va �tre positionn�.
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
		// On r�cup�re le curseur correspondant
		final Cursor the_cursor = Cursor.getPredefinedCursor(cursor);
		// On regarde si l'on est dans le thread swing
		if(SwingUtilities.isEventDispatchThread() == true)
		{
			// On est d�j� dans le thread swing, on appelle directement la
			// m�thode setCursor()
			_tree.setCursor(the_cursor);
			_desktopPane.setCursor(the_cursor);
			if(component != null)
			{
				component.setCursor(the_cursor);
			}
		}
		else
		{
			// On n'est pas dans le thread swing, on va appeler la m�thode dans
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
	* Cette m�thode red�finit celle de l'interface MainWindowInterface. Elle
	* permet de savoir si la session a �t� ouverte sur le Portail.
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
	* Cette m�thode red�finit celle de l'interface MainWindowInterface. Elle 
	* permet de fermer la session en d�truisant tous les noeuds de l'arbre 
	* d'exploration, de supprimer les sessions Agent et de se d�senregistrer 
	* du Portail.
	*
	* Arguments:
	*  - portalStopped: Indique si la fermeture est d�e � un arr�t du Portail
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
		// Pour d�truire tous les noeuds, il faut commencer par r�cup�rer
		// le noeud racine
		if(_isConnected == true)
		{
			GenericTreeObjectNode root_node =
				(GenericTreeObjectNode)_tree.getModel().getRoot();
			// On va s'assurer que le noeud est dans un �tat stable
			while(true)
			{
				int root_node_state = root_node.getNodeState();
				trace_debug.writeTrace("root_node_state=" + root_node_state);
				if(root_node_state == GenericTreeObjectNode.NodeStateEnum.CLOSED ||
					root_node_state == GenericTreeObjectNode.NodeStateEnum.OPENED)
				{
					// On peut d�truire le noeud
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
			// On appelle la m�thode destroy sur le noeud racine
			root_node.destroy(portalStopped);
			((DefaultTreeModel)_tree.getModel()).setRoot(
				new DefaultMutableTreeNode());
		}
		// On supprime les sessions agents
		AgentSessionManager.cleanBeforeExit();
		// On se d�senregistre du Portail
		PortalInterfaceProxy.cleanBeforeExit(portalStopped);
		// Il faut supprimer les mots de passe
		PasswordManager.cleanBeforeExit();
		// On change l'�tat de la session
		setConnected(false, false);
		setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: exitWindow
	*
	* Description:
	* Cette m�thode red�finit celle de l'interface MainWindowInterface. Elle 
	* est appel�e lorsque la fen�tre principale doit �tre ferm�e, et donc que 
	* l'application doit �tre arr�t�e.
	* Elle masque la fen�tre principale et appelle la m�thode stop() de
	* l'interface ProcessInterface.
	* Si l'argument confirm vaut true, une confirmation de l'arr�t de
	* l'application est demand�e � l'utilisateur.
	*
	* Arguments:
	*  - confirm: Indique si une confirmation doit �tre demand�e (true) ou non
	*    (false).
	*  - portalStopped: Indique si la fermeture est d�e � un arr�t du Portail
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
			// On demande d'abord la confirmation � l'utilisateur
			int reply = showPopup("YesNoQuestion", "&Question_ReallyQuit", null);
			if(reply != JOptionPane.YES_OPTION)
			{
				// L'utilisateur n'a pas valid� la sortie
				trace_methods.endOfMethod();
				return;
			}
		}
		// On ferme la session
		closeSession(portalStopped);
		// On ferme toutes les sous-fen�tres ouvertes
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
		// position de la fen�tre seulement si elle n'est pas maximis�e
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
		// On masque la fen�tre et on la d�truit
		hide();
		dispose();
		// Lib�ration des ressources allou�es
		_tree.cleanBeforeExit();
		_desktopPane.cleanBeforeExit();
		_tree = null;
		_desktopPane = null;
		_statusBar = null;
		// On appelle la m�thode d'arr�t du processus
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
	* Cet attribut est un drapeau indiquant si la Console est connect�e au
	* syst�me I-SIS ou non. Il est mis � jour par la m�thode setConnected().
	* ----------------------------------------------------------*/
	private boolean _isConnected;

	/*----------------------------------------------------------
	* Nom: _tree
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'instance de MainWindowTree
	* correspondant � l'arbre d'exploration affich� dans la fen�tre principale.
	* ----------------------------------------------------------*/
	private MainWindowTree _tree;

	/*----------------------------------------------------------
	* Nom: _desktopPane
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'instance de
	* MainWindowDesktopPane correspondant � la zone d'affichage des
	* sous-fen�tres affich�e dans la fen�tre principale.
	* ----------------------------------------------------------*/
	private MainWindowDesktopPane _desktopPane;

	/*----------------------------------------------------------
	* Nom: _processInterface
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'interface ProcessInterface
	* permettant � l'instance de MainWindow d'interagir avec le processus de
	* l'application (arr�t).
	* ----------------------------------------------------------*/
	private ProcessInterface _processInterface;

	/*----------------------------------------------------------
	* Nom: _statusBar
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur une instance de JLabel
	* correspondant � la barre d'�tat de la fen�tre principale.
	* ----------------------------------------------------------*/
	private JLabel _statusBar;

	/*----------------------------------------------------------
	* Nom: _copyItem
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu "Edition/
	* Copier". Il est n�cessaire afin de modifier l'�tat de cet �l�ment.
	* ----------------------------------------------------------*/
	private JMenuItem _copyItem;

	/*----------------------------------------------------------
	* Nom: _cutItem
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu "Edition/
	* Couper". Il est n�cessaire afin de modifier l'�tat de cet �l�ment.
	* ----------------------------------------------------------*/
	private JMenuItem _cutItem;

	/*----------------------------------------------------------
	* Nom: _pasteItem
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu "Edition/
	* Coller". Il est n�cessaire afin de modifier l'�tat de cet �l�ment.
	* ----------------------------------------------------------*/
	private JMenuItem _pasteItem;

	/*----------------------------------------------------------
	* Nom: _saveItem
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu "Edition/
	* Sauver". Il est n�cessaire afin de modifier l'�tat de cet �l�ment.
	* ----------------------------------------------------------*/
	private JMenuItem _saveItem;

	/*----------------------------------------------------------
	* Nom: _connectItem
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu
	* "Session/Ouvrir..." dont l'�tat doit �tre modifi� en fonction des
	* ouvertures/fermetures de session sur le syst�me I-SIS.
	* ----------------------------------------------------------*/
	private JMenuItem _connectItem;

	/*----------------------------------------------------------
	* Nom: _disconnectItem
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu
	* "Session/Fermer" dont l'�tat doit �tre modifi� en fonction des
	* ouvertures/fermetures de session sur le syst�me I-SIS.
	* ----------------------------------------------------------*/
	private JMenuItem _disconnectItem;

	/*----------------------------------------------------------
	* Nom: _progressBar
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur la barre de progression
	* de la barre d'�tat utilis�e pour indiquer le niveau de 
	* progression de la t�che en cours.
	* ----------------------------------------------------------*/
	private JProgressBar _progressBar;
	
	/*----------------------------------------------------------
	* Nom: _userName
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JLabel permettant
	* d'indiquer le nom de l'utilisateur ayant ouvert la session dans la
	* barre d'�tat.
	* ----------------------------------------------------------*/
	private JLabel _userName;
	
	/*----------------------------------------------------------
	* Nom: _searchString
	* 
	* Description:
	* Cet attribut contient la cha�ne utilis�e lors de la derni�re recherche 
	* dans une zone de texte ou dans un tableau.
	* ----------------------------------------------------------*/
	private String _searchString;

	/*----------------------------------------------------------
	* Nom: _searchItem
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu 
	* "Edition/Rechercher". Il est n�cessaire afin de modifier l'�tat de cet 
	* �l�ment.
	* ----------------------------------------------------------*/
	private JMenuItem _searchItem;

	/*----------------------------------------------------------
	* Nom: _againItem
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu 
	* "Edition/Rechercher le suivant". Il est n�cessaire afin de modifier 
	* l'�tat de cet �l�ment.
	* ----------------------------------------------------------*/
	private JMenuItem _againItem;

	/*----------------------------------------------------------
	* Nom: _splitPane
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JSplitPane 
	* correspondant � la barre de s�paration entre la zone d'exploration et la 
	* zone d'affichage des sous-fen�tres.
	* ----------------------------------------------------------*/
	private JSplitPane _splitPane;

	/*----------------------------------------------------------
	* Nom: _undoItem
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu 
	* "Edition/Annuler". Il est n�cessaire afin de modifier l'�tat de cet 
	* �l�ment.
	* ----------------------------------------------------------*/
	private JMenuItem _undoItem;

	/*----------------------------------------------------------
	* Nom: _redoItem
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur l'�l�ment de menu 
	* "Edition/R�tablir". Il est n�cessaire afin de modifier l'�tat de cet 
	* �l�ment.
	* ----------------------------------------------------------*/
	private JMenuItem _redoItem;
	
	/*----------------------------------------------------------
	* Nom: copyData
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur s�lectionne l'�l�ment de
	* menu "Edition/Copier".
	* Elle copie dans le presse-papier de l'application du texte ayant �t�
	* pr�alablement s�lectionn� dans une zone de texte.
	* ----------------------------------------------------------*/
	private void copyData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "copyData");

		trace_methods.beginningOfMethod();
		// La premi�re chose � faire est de v�rifier l'�tat de l'�l�ment de menu
		setEditionItemStates();
		if(_copyItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le composant dans lequel il faut copier
		Component component = _desktopPane.getFocusOwner();
		// Il faut que le composant soit de type JTextComponent
		if(!(component instanceof JTextComponent))
		{ 
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le composant depuis lequel il faut copier
		JTextComponent text_component = (JTextComponent)component;
		// On r�cup�re le texte s�lectionn�
		String selected_text = text_component.getSelectedText();
		// Si il n'y a rien, on sort
		if(selected_text == null || selected_text.equals("") == true)
		{
			// Il n'y a rien � copier, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le Toolkit puis le Clipboard
		Toolkit toolkit = component.getToolkit();
		Clipboard system_clipboard = toolkit.getSystemClipboard();
		// On cr�e un objet StringSelection puis on le place dans le Clipboard
		StringSelection selection = new StringSelection(selected_text);
		system_clipboard.setContents(selection, selection);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: cutData
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur s�lectionne l'�l�ment de
	* menu "Edition/Couper".
	* Elle copie dans le presse-papier de l'application du texte ayant �t�
	* pr�alablement s�lectionn� dans une zone de texte, et supprime le texte
	* s�lectionn�, si cela est possible.
	* ----------------------------------------------------------*/
	private void cutData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "cutData");

		trace_methods.beginningOfMethod();
		// La premi�re chose � faire est de v�rifier l'�tat de l'�l�ment de menu
		setEditionItemStates();
		if(_cutItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le composant dans lequel il faut couper
		Component component = _desktopPane.getFocusOwner();
		// Le composant doit �tre de type JTextComponent
		if(!(component instanceof JTextComponent))
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le composant depuis lequel il faut couper
		JTextComponent text_component = (JTextComponent)component;
		// On r�cup�re le texte s�lectionn�
		String selected_text = text_component.getSelectedText();
		// Si il n'y a rien, on sort
		if(selected_text == null || selected_text.equals("") == true)
		{
			// Il n'y a rien � copier, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On efface la s�lection (couper)
		text_component.replaceSelection("");
		// On r�cup�re le Toolkit puis le Clipboard
		Toolkit toolkit = component.getToolkit();
		Clipboard system_clipboard = toolkit.getSystemClipboard();
		// On cr�e un objet StringSelection puis on le place dans le Clipboard
		StringSelection selection = new StringSelection(selected_text);
		system_clipboard.setContents(selection, selection);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: pasteData
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur s�lectionne l'�l�ment de
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
		// La premi�re chose � faire est de v�rifier l'�tat de l'�l�ment de menu
		setEditionItemStates();
		if(_pasteItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le composant dans lequel il faut coller
		Component component = _desktopPane.getFocusOwner();
		// Le composant doit �tre de type JTextComponent
		if(!(component instanceof JTextComponent))
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		JTextComponent text_component = (JTextComponent)component;
		// On r�cup�re le Toolkit puis le Clipboard
		Toolkit toolkit = text_component.getToolkit();
		Clipboard system_clipboard = toolkit.getSystemClipboard();
		// On r�cup�re la donn�e depuis le Clipboard
		Transferable clipboard_data = system_clipboard.getContents(null);
		// S'il n'y a rien, on sort
		if(clipboard_data == null)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On va essayer de convertir dans tous les formats support�s
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
		// S'il n'y a rien � coller, on sort
		if(clipboard_text == null || clipboard_text.equals("") == true)
		{
			trace_methods.endOfMethod();
			return;
		}
		// Est-ce un remplacement d'une s�lection
		if(text_component.getSelectionStart() == 
			text_component.getSelectionEnd())
		{
			// On effectue une insertion du texte � la position du curseur
			// en positionnant une fausse s�lection
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
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur l'�l�ment 
	* de menu "Rechercher...".
	* Une zone de saisie de la cha�ne � rechercher est affich�e, puis celle-ci 
	* est recherch�e dans la zone de texte qui a le focus, ou dans le tableau 
	* qui a le focus. Lorsque la cha�ne a �t� trouv�e, celle-ci est 
	* s�lectionn�e, ou la ligne du tableau correspondant est s�lectionn�e.
	* Si la cha�ne n'a pas pu �tre trouv�e, une boite de dialogue est affich�e.
	* ----------------------------------------------------------*/
	private void searchData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "searchData");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean found = false;

		trace_methods.beginningOfMethod();
		// On r�cup�re le composant dans lequel il faut rechercher
		Component component = _desktopPane.getFocusOwner();
		// On v�rifie que le composant correspond � une zone o� l'on peut
		// rechercher
		if(!(component instanceof SearchableComponentInterface))
		{
			trace_errors.writeTrace("Composant incompatible avec la " + 
				"recherche !");
			// On commande la mise � jour des �tats
			setEditionItemStates();
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re la cha�ne � rechercher
		String search_string = JOptionPane.showInputDialog(this, 
			MessageManager.getMessage("&SearchString"), _searchString);
		trace_debug.writeTrace("search_string=" + search_string);
		// Si la cha�ne est nulle, c'est que l'utilisateur a annul�, on sort
		if(search_string == null || search_string.equals("") == true)
		{
			trace_methods.endOfMethod();
			return;
		}
		_searchString = search_string;
		// On appelle la m�thode searchData sur l'objet
		// SearchableComponentInterface
		SearchableComponentInterface searchable_component =
			(SearchableComponentInterface)component;
		found = searchable_component.searchData(_searchString);
		if(found == false)
		{
			// Si on n'a pas trouv� la cha�ne, on va afficher un message 
			// disant que la cha�ne n'a pas �t� trouv�e, et on sort
			JOptionPane.showMessageDialog(this, 
				MessageManager.getMessage("&StringNotFound"));
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: searchAgain
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur l'�l�ment 
	* "Rechercher le suivant" du menu "Edition".
	* La recherche est bas�e sur la derni�re position de la recherche et sur 
	* la cha�ne de recherche.
	* ----------------------------------------------------------*/
	private void searchAgain()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "searchAgain");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean found = false;
		
		trace_methods.beginningOfMethod();
		// Y a-t-il une cha�ne de recherche ?
		if(_searchString == null)
		{
			trace_errors.writeTrace("Il n'y a pas de cha�ne de recherche !");
			// On met � jour les �tats des �l�ments du menu et on sort
			setEditionItemStates();
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le composant dans lequel il faut rechercher
		Component component = _desktopPane.getFocusOwner();
		// On v�rifie que le composant correspond � une zone o� l'on peut
		// rechercher
		if(!(component instanceof SearchableComponentInterface))
		{
			trace_errors.writeTrace("Composant incompatible avec la " + 
				"recherche !");
			// On commande la mise � jour des �tats
			setEditionItemStates();
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On appelle la m�thode searchAgain sur l'objet
		// SearchableComponentInterface
		SearchableComponentInterface searchable_component =
			(SearchableComponentInterface)component;
		found = searchable_component.searchAgain(_searchString);
		if(found == false)
		{
			// Si on n'a pas trouv� la cha�ne, on va afficher un message 
			// disant que la cha�ne n'a pas �t� trouv�e, et on sort
			JOptionPane.showMessageDialog(this, 
				MessageManager.getMessage("&StringNotFound"));
		}
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: saveData
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur s�lectionne l'�l�ment de
	* menu "Edition/Sauvegarder...".
	* Elle sauvegarde le contenu d'une zone de texte, ou d'une s�lection, vers
	* un fichier choisi par l'utilisateur.
	* ----------------------------------------------------------*/
	private void saveData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "saveData");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// On r�cup�re le composant contenant les informations � sauvegarder
		Component component = _desktopPane.getFocusOwner();
		// On v�rifie que le composant correspond � une zone que l'on peut
		// sauvegarder
		if(!(component instanceof SearchableComponentInterface))
		{
			trace_errors.writeTrace("Composant incompatible avec la " + 
				"sauvegarde !");
			// On commande la mise � jour des �tats
			setEditionItemStates();
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		setCurrentCursor(Cursor.WAIT_CURSOR, null);
		setProgressMaximum(2);
		setStatus(MessageManager.getMessage("&Status_PreparingToSave"), null, 0);
		// On va afficher une bo�te de dialogue de s�lection du fichier
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
			// L'utilisateur a annul�, on sort
			trace_debug.writeTrace("L'utilisateur a annul�");
			setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
			setStatus(null, null, 0);
			trace_methods.endOfMethod();
			return;
		}
		File choosen_file = file_chooser.getSelectedFile();
		String file_name = choosen_file.getAbsolutePath();
		trace_debug.writeTrace("file_name=" + file_name);
		// On v�rifie que le fichier correspond au filtre s�lectionn�
		FileFilter choosen_filter = file_chooser.getFileFilter();
		if(choosen_filter.accept(choosen_file) == false &&
			choosen_filter instanceof FileNameExtensionFilter) {
			file_name = file_name.concat(
				((FileNameExtensionFilter)choosen_filter).getExtension());
			choosen_file = new File(file_name);
		}
		// On va v�rifier si le fichier existe d�j�
		if(choosen_file.exists() == true)
		{
			// On va afficher un message � l'utilisateur pour savoir s'il
			// veut �craser l'ancien fichier
			if(JOptionPane.showConfirmDialog(this,
				MessageManager.getMessage("&FileExists"),
				MessageManager.getMessage("&SaveData"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			{
				// L'utilisateur a annul�, on sort
				trace_debug.writeTrace("L'utilisateur a annul� !");
				trace_methods.endOfMethod();
				return; 
			}
		}
		setStatus(MessageManager.getMessage("&Status_SavingToFile"), null, 1);
		// On appelle la m�thode saveDataToFile sur l'objet
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
	* Cette m�thode est appel�e lorsque l'utilisateur s�lectionne l'�l�ment 
	* de menu "Edition/Annuler".
	* Elle appelle la m�thode undo() de la zone ayant le focus, afin que 
	* celle-ci proc�de � l'annulation de la derni�re t�che d'�dition 
	* enregistr�e.
	* ----------------------------------------------------------*/
	private void doUndo()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "doUndo");

		trace_methods.beginningOfMethod();
		// La premi�re chose � faire est de v�rifier l'�tat de l'�l�ment de menu
		setEditionItemStates();
		if(_undoItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le composant g�rant les actions d'annulation/
		// r�tablissement
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
	* Cette m�thode est appel�e lorsque l'utilisateur s�lectionne l'�l�ment 
	* de menu "Edition/R�tablir".
	* Elle appelle la m�thode redo() de la zone ayant le focus, afin que 
	* celle-ci proc�de au r�tablissement de la derni�re annulation effectu�e.
	* ----------------------------------------------------------*/
	private void doRedo()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "doRedo");

		trace_methods.beginningOfMethod();
		// La premi�re chose � faire est de v�rifier l'�tat de l'�l�ment de menu
		setEditionItemStates();
		if(_redoItem.isEnabled() == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le composant g�rant les actions d'annulation/
		// r�tablissement
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
		// On commande le r�tablissement
		undoable_component.redo();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: openAbout
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur s�lectionne l'�l�ment de
	* menu "?/A propos de...".
	* Elle ouvre une bo�te de dialogue pr�sentant des informations relatives �
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
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur un des items
	* de menu correspondant � un des processeurs globaux (un item du menu
	* "Outils"). Elle appelle la m�thode executeProcessor de la classe
	* ProcessorManager.
	*
	* Arguments:
	*  - processorInterface: Une r�f�fence sur le processeur � ex�cuter,
	*  - menuItem: L'item de menu ayant d�clench� l'ex�cution du processeur.
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
			// Appel de la m�thode executeProcessor de la classe
			// ProcessorManager. Il n'y a pas de param�tres, ni de noeud
			// s�lectionn�.
			ProcessorManager.executeProcessor(processorInterface, this, 
				menuItem, null, null, null, null, false, false);
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Erreur lors de l'ex�cution du processeur: " +
				exception);
			// On remet le curseur par d�faut
			setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
			repaint();
			// Affichage d'une bo�te d'erreur
		    DialogManager.displayDialogForException("&ERR_CannotOpenTool",
				this, exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette m�thode est utilis�e pour instancier et ajouter tous les objets
	* graphiques qui font partie de la fen�tre principale. Elle est appel�e par
	* le constructeur de la classe.
	*
	* Elle cr�e la barre de menu en appelant la m�thode makeMenuBar(), l'arbre
	* d'exploration en cr�ant une instance de MainWindowTree, la zone
	* d'affichage des sous-fen�tres en instanciant la classe
	* MainWindowDesktopPane.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		GenericTreeClassNode root_node = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "makePanel");
		int orientation = 0;

		trace_methods.beginningOfMethod();
		// On va essayer de r�cup�rer la position du s�parateur
		// depuis les pr�f�rences, ainsi que l'orientation
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
		// Cr�ation du noeud racine bidon, de type GenericTreeClassNode
		// Ce noeud racine sera remplac� par un autre dans la m�thode
		// loadInitialData().
		root_node = new GenericTreeClassNode(null, null, null, null, null, 
			null);
		// Cr�ation de l'arbre d'exploration
		_tree = new MainWindowTree(this, root_node);
		// On ajoute une rubrique d'aide pour l'arbre
		CSH.setHelpIDString(_tree, "ExplorationTree");
		// Cr�ation de la zone d'affichage des sous-fen�tres
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
		// Cr�ation de la barre de menu
		JMenuBar menu_bar = makeMenuBar();
		// On ajoute une rubrique d'aide pour la barre de menu
		CSH.setHelpIDString(menu_bar, "MenuBar");

		// Ajout de la barre de menu � la fen�tre
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
			// Cr�ation du split pane avec l'arbre dans la zone de gauche et
			// la zone d'affichage des sous-fen�tre dans la zone de droite.
			_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					tree_scroll, _desktopPane);
			_splitPane.setDividerLocation(300);
		}
		else
		{
			// Cr�ation du split pane avec l'arbre dans la zone sup�rieure et
			// la zone d'affichage des sous-fen�tre dans la zone inf�rieure.
			_splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					tree_scroll, _desktopPane);
			_splitPane.setDividerLocation(200);
		}
		// Ajout du split pane � la fen�tre
		getContentPane().add(_splitPane, BorderLayout.CENTER);

		// Cr�ation du panneau de la barre d'�tat
		JPanel status_panel = new JPanel(new BorderLayout());
		// On ajoute une rubrique d'aide pour la barre d'�tat
		CSH.setHelpIDString(status_panel, "StatusBar");
		// Cr�ation de la zone d'affichage du nom de l'utilisateur
		_userName = new JLabel();
		// Ajout d'une bordure � la barre d'�tat
		_userName.setBorder(BorderFactory.createLoweredBevelBorder());
		// Ajout de la barre d'�tat au panneau
		status_panel.add(_userName, BorderLayout.WEST);
		// Cr�ation de la barre d'�tat
		_statusBar = new JLabel();
		// Ajout d'une bordure � la barre d'�tat
		_statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		// Ajout de la barre d'�tat au panneau
		status_panel.add(_statusBar, BorderLayout.CENTER);
		// Cr�ation de la barre de progression
		_progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 1);
		// Ajout d'une bordure � la barre de progression
		_progressBar.setBorder(BorderFactory.createLoweredBevelBorder());
		// Ajout de la barre de progression au panneau
		status_panel.add(_progressBar, BorderLayout.EAST);
		// Ajout dpanneau � la fen�tre
		getContentPane().add(status_panel, BorderLayout.SOUTH);
		// Ajout du message par d�faut
		setConnected(false, false);
		setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makeMenuBar
	*
	* Description:
	* Cette m�thode est charg�e de construire la barre de menu de la fen�tre
	* principale.
	* Cette barre de menu contient les menu et �l�ments suivants:
	*  - Menu "Session"
	*    - El�ment "Quitter"
	*  - Menu "Edition"
	*    - El�ment "Copier"
	*    - El�ment "Couper"
	*    - El�ment "Coller"
	*    - El�ment "Imprimer..."
	*    - El�ment "Sauvegarder..."
	*  - Menu "Outils" (construit � partir de la liste des processeurs globaux
	*    fourni par la classe ProcessorManager),
	*  - Menu "Fen�tres" (construit et g�r� par l'instance de
	*    MainWindowDesktopPane)
	*  - Menu "?"
	*    - El�ment "Aide"
	*    - El�ment "A propos de..."
	*
	* Tous ces �l�ments de menu, hormis ceux du menu Fen�tres, appellent une
	* m�thode correspondante de la classe MainWindow.
	*
	* Retourne: Une instance de JMenuBar correspondant � la barre de menu cr��e.
	* ----------------------------------------------------------*/
	private JMenuBar makeMenuBar()
	{
		JMenuBar menu_bar = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "makeMenuBar");

		trace_methods.beginningOfMethod();
		// Cr�ation de la barre de menu
		menu_bar = new JMenuBar();

		// Cr�ation du menu Session
		JMenu session_menu = new MainWindowMenu("Session");
		// Cr�ation de l'item Ouvrir
		_connectItem = new MainWindowMenuItem("Connect");
		// Ajout du callback sur l'item
		_connectItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode d'ouverture de session
				openSession();
			}
		});
		// Ajout de l'item au menu
		session_menu.add(_connectItem);
		// Cr�ation de l'item Fermer
		_disconnectItem = new MainWindowMenuItem("Disconnect");
		// Ajout du callback sur l'item
		_disconnectItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode de fermeture de session dans un
				// thread s�par�
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
		// On ajoute un s�parateur
		session_menu.addSeparator();
		// Cr�ation de l'item Configurer
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
						// Appel de la m�thode exitWindow
						setPreferences();
					}
				});
				thread.start();
			}
		});
		// Ajout de l'item au menu
		session_menu.add(configure_item);
		// On ajoute un s�parateur
		session_menu.addSeparator();
		// Cr�ation de l'item Quitter
		JMenuItem quit_item = new MainWindowMenuItem("Quit");
		// Ajout du callback sur l'item
		quit_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode exitWindow dans un thread s�par�
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
		// Ajout du menu � la barre de menu
		menu_bar.add(session_menu);

		// Cr�ation du menu Edition
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
		// Cr�ation de l'item Annuler
		_undoItem = new MainWindowMenuItem("Undo");
		// Ajout du callback sur l'item
		_undoItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode doUndo
				doUndo();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_undoItem);
		// Cr�ation de l'item R�tablir
		_redoItem = new MainWindowMenuItem("Redo");
		// Ajout du callback sur l'item
		_redoItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode doRedo
				doRedo();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_redoItem);
		// Ajout d'une s�paration
		edition_menu.addSeparator();
		// Cr�ation de l'item Copier
		_copyItem = new MainWindowMenuItem("Copy");
		// Ajout du callback sur l'item
		_copyItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode copyData
				copyData();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_copyItem);
		// Cr�ation de l'item Couper
		_cutItem = new MainWindowMenuItem("Cut");
		// Ajout du callback sur l'item
		_cutItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode cutData
				cutData();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_cutItem);
		// Cr�ation de l'item Coller
		_pasteItem = new MainWindowMenuItem("Paste");
		// Ajout du callback sur l'item
		_pasteItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode pasteData
				pasteData();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_pasteItem);
		// Ajout d'une s�paration
		edition_menu.addSeparator();
		// Cr�ation de l'item Rechercher
		_searchItem = new MainWindowMenuItem("Search");
		// Ajout du callback sur l'item
		_searchItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode searchData
				searchData();
			}
		});
		edition_menu.add(_searchItem);
		// Cr�ation de l'item Rechercher le suivante
		_againItem = new MainWindowMenuItem("Again");
		// Ajout du callback sur l'item
		_againItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode searchData
				searchAgain();
			}
		});
		edition_menu.add(_againItem);
		// Ajout d'une s�paration
		edition_menu.addSeparator();
		// Cr�ation de l'item Sauvegarder
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
						// Appel de la m�thode saveData
						saveData();
					}
				});
				thread.start();
			}
		});
		// Ajout de l'item au menu
		edition_menu.add(_saveItem);
		// Ajout du menu � la barre de menu
		menu_bar.add(edition_menu);

		// Cr�ation du menu Outils
		JMenu tools_menu = new MainWindowMenu("Tools");
		// R�cup�ration de la liste des processeurs globaux
		ProcessorInterface[] processors = 
			ProcessorManager.getGlobalProcessors();
		if(processors != null && processors.length > 0)
		{
			// Cr�ation des items de menu pour les processeurs
			for(int index = 0 ; index < processors.length ; index ++)
			{
				final ProcessorInterface processor_interface = 
					processors[index];
				// On cr�e l'item de menu
				final JMenuItem tool_item =
					new MainWindowMenuItem(processor_interface.getMenuLabel());
				// Ajout du callback de click sur l'item
				tool_item.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						// Appel de la m�thode d'ex�cution du processeur
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
		// Ajout du menu � la barre de menu
		menu_bar.add(tools_menu);

		// Ajout du menu fen�tre � la barre de menu
		menu_bar.add(_desktopPane.getMenu());

		// Cr�ation du menu ? (aide)
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
			// On cr�e le help broker
			final HelpBroker help_broker = help_set.createHelpBroker();
			// Cr�ation de l'item Aide
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
			// Cr�ation de l'item d'aide contextuelle
			JMenuItem contextual_help_item =
				new MainWindowMenuItem("ContextualHelp");
			contextual_help_item.addActionListener(
				new CSH.DisplayHelpAfterTracking(help_broker));
			contextual_help_item.setAccelerator(KeyStroke.getKeyStroke("F1"));
			help_broker.enableHelpKey(this, "top", help_set);
			// Ajout de l'item au menu
			help_menu.add(contextual_help_item);
			// Ajout d'un s�parateur
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
		// Cr�ation de l'item A propos
		JMenuItem about_item = new MainWindowMenuItem("About");
		// Ajout du callback sur l'item
		about_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la m�thode openAbout
				openAbout();
			}
		});
		// Ajout de l'item au menu
		help_menu.add(about_item);
		// Ajout du menu � la barre de menu en tant que menu d'aide
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
	* Cette m�thode est appel�e lorsque le menu "Edition" est s�lectionn�
	* (activ�) par l'utilisateur. Elle permet de mettre � jour l'�tat des
	* diff�rents �l�ments du menu en fonction du propri�taire du focus au moment
	* de l'appel, et de la pr�sence ou non de texte s�lectionn�.
	* ----------------------------------------------------------*/
	private void setEditionItemStates()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "setEditionItemStates");

		trace_methods.beginningOfMethod();
		// Par d�faut, ils sont tous d�sactiv�s
		_undoItem.setEnabled(false);
		_redoItem.setEnabled(false);
		_copyItem.setEnabled(false);
		_cutItem.setEnabled(false);
		_pasteItem.setEnabled(false);
		_searchItem.setEnabled(false);
		_againItem.setEnabled(false);
		_saveItem.setEnabled(false);
		// On r�cup�re le composant qui a le focus
		Component focus_owner = _desktopPane.getFocusOwner();
		// S'il n'y a pas de composant, on sort
		if(focus_owner == null)
		{
			trace_methods.endOfMethod();
			return;
		}
		// Le composant peut �tre de type JTextComponent
		if(focus_owner instanceof JTextComponent)
		{
			JTextComponent text_component = (JTextComponent)focus_owner;
			// R�cup�ration du texte s�lectionn�
			String selected_text = text_component.getSelectedText();
			if(selected_text != null && selected_text.equals("") == false)
			{
				// Il y a du texte s�lectionn�, on peut copier
				_copyItem.setEnabled(true);
			}
			if(text_component.isEditable() == true && 
				!(text_component instanceof NonEditableTextArea ||
				text_component instanceof NonEditableTextPane))
			{
				// Si l'item copier est actif, on peut aussi couper
				_cutItem.setEnabled(_copyItem.isEnabled());
				// La zone est �ditable, on peut coller
				_pasteItem.setEnabled(true);
			}
		}
		// Le composant peut-�tre de type SearchableComponentInterface
		if(focus_owner instanceof SearchableComponentInterface)
		{
			// L'�l�ment de recherche peut �tre activ�
			_searchItem.setEnabled(true);
			// L'�l�ment d'enregistrement est actif
			_saveItem.setEnabled(true);
		}
		// Le composant peut-�tre de type UndoableComponentInterface
		if(focus_owner instanceof UndoableComponentInterface)
		{
			// On va activer les �l�ments d'annulation/r�tablissement
			// en fonction des possibilit�s du composant
			UndoableComponentInterface undoable_component = 
				(UndoableComponentInterface)focus_owner;
			_undoItem.setEnabled(undoable_component.canUndo());
			_redoItem.setEnabled(undoable_component.canRedo());
		}
		// S'il y a une cha�ne de recherche et que l'item de
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
	* Cette m�thode est appel�e automatiquement lors de la tentative
	* d'ouverture de session si la Console n'a pas encore �t� configur�e, ou
	* lorsque l'utilisateur a cliqu� sur le menu "Connexion/Configurer...".
	*
	* Retourne: Un bool�en indiquant si l'utilisateur a valid� (true) ou non
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
				// Si l'utilisateur a valid� ses changements, et qu'une
				// session est ouverte, il faut l'informer que ceux-ci seront
				// pris en compte lors de la prochaine ouverture de session
			    showPopup("Information", "&MustReconnect", null);
			}
			else if(PortalInterfaceProxy.isInitialized() == true)
			{
				// Si l'utilisateur a valid� ses changements, qu'aucune session
				// n'est ouverte, mais que la connexion avec le Portail a d�j�
				// �t� r�alis�e au moins une fois, on doit r�initialiser
				// le PortalInterfaceProxy pour prendre en compte les nouveaux
				// param�tres.
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
	* Cette m�thode est permet de contr�ler si tous les processeurs de la 
	* Console ont �t� correctement configur�s ou non.
	* Elle va appeler la m�thode isConfigured() pour chaque ProcessorInterface 
	* de la liste retourn�e par la m�thode getAllProcessors() de la classe 
	* ProcessorManager.
	* 
	* Retourne: true si tous les processeurs ont �t� correctement configur�s, 
	* false sinon.
	* ----------------------------------------------------------*/
	private boolean checkConfiguration()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindow", "checkConfiguration");

		trace_methods.beginningOfMethod();
		// On va r�cup�rer la liste des processeurs
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