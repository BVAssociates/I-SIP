/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/UpdateProcessor.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de mise � jour de la Console
* DATE:        17/10/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.update
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: UpdateProcessor.java,v $
* Revision 1.10  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.9  2008/02/21 12:10:00  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.8  2008/02/13 16:02:08  tz
* Suppression de la m�thode buildLocalFileName().
* Correction d'un bug lors de la v�rification d'un fichier.
*
* Revision 1.7  2008/01/31 16:59:27  tz
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.6  2006/11/03 10:31:31  tz
* Utilisation de FileSystemEntry au lieu de StoreEntry.
*
* Revision 1.5  2006/08/11 13:34:26  tz
* Adaptation � corba-R2_1_0-AL-1_0.
*
* Revision 1.4  2006/03/13 15:14:47  tz
* Suppression de commentaires inutiles.
*
* Revision 1.3  2006/03/09 14:17:25  tz
* Le test sur les dates de derni�re modification est effectu� avec une
* tol�rance de 5 mn.
*
* Revision 1.2  2006/03/08 15:59:13  tz
* Passage en mode production
*
* Revision 1.1  2005/12/23 13:23:43  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.update;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.config.ConfigurationAPI;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.abs.processor.DownloadProgressInterface;
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.com.PortalInterfaceProxy;
import com.bv.isis.console.com.FileDownloader;
import com.bv.isis.console.core.gui.NonEditableTextPane;
import com.bv.isis.corbacom.FileReaderInterface;
import com.bv.isis.corbacom.FileSystemEntry;

/*----------------------------------------------------------
* Nom: UpdateProcessor
* 
* Description:
* Cette classe impl�mente le processeur charg� de la mise � jour de la Console 
* I-SIS. La mise � jour de la Console consiste � t�l�charger depuis le Portail 
* tous les fichiers nouveaux ou ayant chang�, � en lire le fichier de 
* description, puis � appliquer les modifications (installation de nouveaux 
* fichiers, modification de fichiers, modification des fichiers de 
* configuration ou de d�marrage...).
* Elle red�finit la classe ProcessorFrame car il s'agit d'un processeur 
* graphique, impl�mente l'interface DownloadProgressInterface afin de suivre 
* les t�l�chargements, ainsi que l'interface UpdateProgressInterface. 
* ----------------------------------------------------------*/
public class UpdateProcessor
	extends ProcessorFrame
	implements DownloadProgressInterface,
			   UpdateProgressInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: UpdateProcessor
	* 
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. Elle n'est 
	* pr�sent�e que pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public UpdateProcessor()
	{
		super(false);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "UpdateProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e afin d'effectuer tout pr�-chargement de donn�es.
	* Elle charge le fichier de messages du processeur.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		MessageManager.loadFile("update.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* permet de savoir si le processeur peut �tre invoqu� depuis l'arbre 
	* d'exploration.
	* Dans le cas du processeur de mise � jour, seule l'invocation depuis le 
	* menu "Outils" est possible.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "isTreeCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* permet de savoir si le processeur peut �tre invoqu� depuis un affichage 
	* sous forme de tableau.
	* Dans le cas du processeur de mise � jour, seule l'invocation depuis le 
	* menu "Outils" est possible.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "isTableCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* permet de savoir si le processeur peut �tre invoqu� depuis le menu 
	* "Outils".
	* Dans le cas du processeur de mise � jour, seule l'invocation depuis le 
	* menu "Outils" est possible.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "isGlobalCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* permet de r�cup�rer une description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&UpdateProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* permet de r�cup�rer un intitul� de l'�l�ment de menu dans le cas de 
	* processeurs globaux.
	* 
	* Retourne: L'intitul� de l'�l�ment de menu.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "getMenuLabel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return "Update";
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e lorsque l'ex�cution du processeur est requise.
	* La sous-fen�tre du processeur est construite via la m�thode makePanel(), 
	* puis elle est affich�e. Ensuite, la pr�sence de fichiers � t�l�charger 
	* et leur traitement �ventuel est assur� par la m�thode checkStore().
	* 
	* Si un probl�me survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - menuItem: Une r�f�rence sur l'objet JMenuItem par lequel le 
	*    processeur a �t� invoqu�,
	*  - parameters: Les arguments �ventuels d'ex�cution du processeur 
	*   (ignor�s dans le cas pr�sent),
	*  - preprocessing: Une cha�ne de pr�-processing (ignor�e dans le cas 
	*    pr�sent),
	*  - postprocessing: Une cha�ne de post-processing (ignor�e dans le cas
	*    pr�sent),
	*  - selectedNode: Une r�f�rence sur un objet DefaultMutableTreeNode 
	*    repr�sentant le noeud s�lectionn� (ignor� dans le cas pr�sent).
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
			"UpdateProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// On v�rifie la validit� des arguments
		if(windowInterface == null || menuItem == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On v�rifie que la Console est connect�e � un Portail
		if(windowInterface.isConnected() == false)
		{
			trace_errors.writeTrace(
				"La Console n'est pas connect�e � un Portail");
			// On va afficher un message � l'utilisateur
			windowInterface.showPopup("Information",
				MessageManager.getMessage("&ERR_MustBeConnectedFirst"), null);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On appelle la m�thode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On positionne le titre de la sous-fen�tre
		setTitle(MessageManager.getMessage("&Update_Title"));
		// On construit le panneau de la fen�tre
		makePanel();
		// On positionne les dimensions de la fen�tre
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(new Dimension(450, 300));
		// On affiche la fen�tre
		display();
		// On lance la v�rification du d�p�t
		checkStore();
		// Une fois ici, on peut activer le bouton "Fermer"
		_closeButton.setEnabled(true);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e lorsque la sous-fen�tre doit �tre ferm�e.
	* Elle lib�re toutes les ressources allou�es par l'objet.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "close");

		trace_methods.beginningOfMethod();
		// On ferme la fen�tre en appelant la m�thode de la super-classe.
		super.close();
		// On lib�re les r�f�rences
		_textPane = null;
		_operationLabel = null;
		_fileLabel = null;
		_fileProgressBar = null;
		_globalProgressBar = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorFrame. Elle permet 
	* de retourner un objet �tant un double de l'objet courant.
	* 
	* Retourne: Une r�f�rence sur un nouvel objet UpdateProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new UpdateProcessor();
	}

	/*----------------------------------------------------------
	* Nom: setProgress
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface DownloadProgressInterface. 
	* Elle permet de fixer la progression de l'op�ration de t�l�chargement.
	* 
	* Arguments:
	*  - progress: La progression du t�l�chargement, exprim�e en pourcentage 
	*    (0 � 100).
 	* ----------------------------------------------------------*/
 	public void setProgress(
 		int progress
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "setProgress");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("progress=" + progress);
		// On positionne la progression sur les deux barres
		updateProgress(null, null, progress, progress);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: updateProgress
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface UpdateProgressInterface. 
	* Elle permet de mettre � jour les champs de la zone de progression � 
	* partir des informations pass�es en argument.
	*  - L'intitul� de l'op�ration est mis � jour si l'argument operation est 
	*    non null,
	*  - Le nom du fichier en cours de traitement est mis � jour si l'argument 
	*    file est non null,
	*  - La progression du fichier est mise � jour,
	*  - La progression globale est mise � jour.
	* 
	* Arguments:
	*  - operation: L'intitul� de l'op�ration en cours, ou null,
	*  - file: Le nom du fichier en cours de traitement, ou null,
	*  - fileProgress: La progression du traitement du fichier,
	*  - globalProgress: La progression globale.
 	* ----------------------------------------------------------*/
 	public void updateProgress(
 		final String operation,
 		final String file,
 		final int fileProgress,
 		final int globalProgress
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "updateProgress");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("operation=" + operation);
		trace_arguments.writeTrace("file=" + file);
		trace_arguments.writeTrace("fileProgress=" + fileProgress);
		trace_arguments.writeTrace("globalProgress=" + globalProgress);
		// On s'assure d'effectuer la mise � jour dans le thread Swing
		if(SwingUtilities.isEventDispatchThread() == true)
		{
			if(operation != null)
			{
				_operationLabel.setText(operation);
			}
			if(file != null)
			{
				_fileLabel.setText(file);
			}
			_fileProgressBar.setValue(fileProgress);
			if(globalProgress != -1)
			{
				_globalProgressBar.setValue(globalProgress);
			}
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					public void run()
					{
						if(operation != null)
						{
							_operationLabel.setText(operation);
						}
						if(file != null)
						{
							_fileLabel.setText(file);
						}
						_fileProgressBar.setValue(fileProgress);
						if(globalProgress != -1)
						{
							_globalProgressBar.setValue(globalProgress);
						}
					}
				});
			}
			catch(Exception e)
			{
				// On ne fait rien
			}
		}
		trace_methods.endOfMethod();
 	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _textPane
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet NonEditableTextPane 
	* charg� d'afficher les �tapes de la mise � jour.
	* ----------------------------------------------------------*/
	private NonEditableTextPane _textPane;

	/*----------------------------------------------------------
	* Nom: _operationLabel
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JLabel charg� de 
	* l'affichage de l'op�ration en cours.
	* ----------------------------------------------------------*/
	private JLabel _operationLabel;

	/*----------------------------------------------------------
	* Nom: _fileLabel
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JLabel charg� de 
	* l'affichage du nom du fichier sur lequel l'op�ration en cours est 
	* effectu�e.
	* ----------------------------------------------------------*/
	private JLabel _fileLabel;

	/*----------------------------------------------------------
	* Nom: _fileProgressBar
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JProgressBar charg� 
	* d'afficher la progression de l'op�ration en cours sur le fichier.
	* ----------------------------------------------------------*/
	private JProgressBar _fileProgressBar;

	/*----------------------------------------------------------
	* Nom: _globalProgressBar
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JProgressBar charg� 
	* d'afficher la progression de l'installation du fichier de mise � jour 
	* courant.
	* ----------------------------------------------------------*/
	private JProgressBar _globalProgressBar;

	/*----------------------------------------------------------
	* Nom: _closeButton
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JButton permettant de 
	* fermer la sous-fen�tre.
	* ----------------------------------------------------------*/
	private JButton _closeButton;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette m�thode permet de construire la sous-fen�tre du processeur.
	* Celle-ci est constitu� de deux zones principale : Une zone de texte 
	* permettant de suivre l'ensemble des t�ches effectu�es, puis une zone de 
	* progression permettant de suivre pas � pas la progression de 
	* l'installation d'une mise � jour.
	* La zone de progression permet de suivre la progression � la fois du 
	* t�l�chargement des fichiers de mise � jour, et du traitement de ceux-ci, 
	* via des zones d'affichage et des barres de progression.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "makePanel");

		trace_methods.beginningOfMethod();
		// On commence par construire le panneau avec les deux zones
		JPanel central_panel = new JPanel(new BorderLayout());
		// On va constuire la zone d'affichage centrale
		_textPane = new NonEditableTextPane(new DefaultStyledDocument());
		_textPane.setCaretPosition(0);
		_textPane.setFont(new JLabel().getFont());
		// On va la placer dans un JScrollPane
		JScrollPane scroll_pane = new JScrollPane(_textPane);
		scroll_pane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll_pane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// On ajoute le scroll pane dans la zone centrale
		central_panel.add(scroll_pane, BorderLayout.CENTER);
		// On va cr�er le panneau de progression
		GridBagLayout progress_layout = new GridBagLayout();
		GridBagConstraints progress_constraints = 
			new GridBagConstraints(0, 0, 1, 1, 0, 0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(1, 2, 1, 2),	0, 0);
		JPanel progress_panel = new JPanel(progress_layout);
		// On construit le libell� d'op�ration
		JLabel operation_label = 
			new JLabel(MessageManager.getMessage("&Update_Operation"));
		progress_layout.setConstraints(operation_label, progress_constraints);
		progress_panel.add(operation_label);
		// On construit le libell� de fichier
		JLabel file_label = 
			new JLabel(MessageManager.getMessage("&Update_File"));
		progress_constraints.gridy ++;
		progress_layout.setConstraints(file_label, progress_constraints);
		progress_panel.add(file_label);
		// On construit le libell� de progression de fichier
		JLabel file_progress = 
			new JLabel(MessageManager.getMessage("&Update_FileProgress"));
		progress_constraints.gridy ++;
		progress_layout.setConstraints(file_progress, progress_constraints);
		progress_panel.add(file_progress, progress_constraints);
		// On construit le libell� de progression globale
		JLabel global_progress =
			new JLabel(MessageManager.getMessage("&Update_GlobalProgress"));
		progress_constraints.gridy ++;
		progress_layout.setConstraints(global_progress, progress_constraints);
		progress_panel.add(global_progress, progress_constraints);
		// On construit le libell� de l'op�ration
		_operationLabel = new JLabel();
		progress_constraints.gridx ++;
		progress_constraints.gridy = 0;
		progress_constraints.weightx = 1;
		progress_layout.setConstraints(_operationLabel, progress_constraints);
		progress_panel.add(_operationLabel);
		// On construit le libell� du fichier
		_fileLabel = new JLabel();
		progress_constraints.gridy ++;
		progress_layout.setConstraints(_fileLabel, progress_constraints);
		progress_panel.add(_fileLabel);
		// On construit la barre de progression du fichier
		_fileProgressBar = new JProgressBar(0, 100);
		_fileProgressBar.setValue(0);
		progress_constraints.gridy ++;
		progress_layout.setConstraints(_fileProgressBar, progress_constraints);
		progress_panel.add(_fileProgressBar);
		// On construit la barre de progression globale
		_globalProgressBar = new JProgressBar(0, 100);
		_globalProgressBar.setValue(0);
		progress_constraints.gridy ++;
		progress_layout.setConstraints(_globalProgressBar, progress_constraints);
		progress_panel.add(_globalProgressBar);
		// On ajoute une bordure au panneau de progression
		progress_panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Update_Progress")));
		// On ajoute le panneau de progression au panneau central
		central_panel.add(progress_panel, BorderLayout.SOUTH);
		// On ajoute le panneau central au milieu de la sous-fen�tre
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(central_panel, BorderLayout.CENTER);
		// Maintenant, on va cr�er le bouton Fermer
		_closeButton = new JButton(MessageManager.getMessage("&Update_Close"));
		_closeButton.setEnabled(false);
		// On ajoute le callback sur le bouton
		_closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode de fermeture
				close();
			}
		});
		// On cr�e un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 1, 
			1, GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		layout.setConstraints(_closeButton, constraints);
		button_panel.add(_closeButton);
		// On place ce panneau dans la zone sud
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkStore
	* 
	* Description:
	* Cette m�thode permet de v�rifier dans le d�p�t des fichiers Console du 
	* Portail la pr�sence de nouveaux fichiers ou de fichiers modifi�s.
	* Si de tels fichiers sont trouv�s, une boite de dialogue est affich�e, 
	* pour chaque fichier, � l'utilisateur afin de lui demander si celui-ci 
	* doit �tre t�l�charg� et install�.
	* Si l'utilisateur r�pond par l'affirmative, le fichier est trait� via la 
	* m�thode processFile().
	* Le r�pertoire local de d�p�t est d�fini par le param�tre de 
	* configuration "Console.StoreDirectory".
	* Le r�pertoire temporaire est d�fini par le param�tre 
	* "Console.TemporaryDirectory".
	*
	* Si une erreur survient, l'exception InnerException est lev�e.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private void checkStore()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "checkStore");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean at_least_one_file_processed = false;
		boolean should_reboot = false;
		String store_directory = "store";
		String temporary_directory = "temp";

		trace_methods.beginningOfMethod();
		appendToTextPane("&Update_Initializing");
		// On commence par r�cup�rer la configuration
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			store_directory = configuration.getString("Console",
				"StoreDirectory");
			temporary_directory = configuration.getString("Console",
				"TemporaryDirectory");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Impossible de r�cup�rer la configuration: " +
				exception.getMessage());
			// On continue quand m�me
		}
		trace_debug.writeTrace("store_directory=" + store_directory);
		trace_debug.writeTrace("temporary_directory=" + temporary_directory);
		// On v�rifie que les r�pertoires existent et sont inscriptibles
		checkDirectory(store_directory);
		checkDirectory(temporary_directory);
		// On s'assure de nettoyer le r�pertoire temporaire
		clearTemporaryDirectory(temporary_directory);
		// On va r�cup�rer le proxy de Portail
		appendToTextPane("&Update_CheckingStore");
		PortalInterfaceProxy portal_proxy = PortalInterfaceProxy.getInstance();
		// On va r�cup�rer la liste des fichiers du d�p�t Console sur le 
		// Portail
		FileSystemEntry[] remote_files = portal_proxy.getStoreFileList();
		try
		{
			// On va v�rifier fichier par fichier la n�cessit� d'un 
			// t�l�chargement
			for(int index = 0 ; index < remote_files.length ; index ++)
			{
				String remote_file = remote_files[index].entryName;
				String remote_path = remote_files[index].entryPath;
				String[] extra_info = { remote_path };
				trace_debug.writeTrace("remote_file=" + remote_file);
				trace_debug.writeTrace("remote_path=" + remote_path);
				String message = MessageManager.fillInMessage(
					MessageManager.getMessage("&Update_ProcessingFile"), 
					extra_info);
				appendToTextPane(message);
				// On v�rifie qu'il s'agit d'un fichier archive au format zip
				if(remote_file.endsWith(".zip") == false && 
					remote_file.endsWith(".ZIP") == false)
				{
					trace_debug.writeTrace(
						"Le format du fichier n'est pas support� !");
					appendToTextPane("&ERR_FileHasUnsupportedFormat");
					continue;
				}
				// On v�rifie si le fichier a chang� ou est nouveau et que 
				// l'utilisateur veut l'installer
				if(checkFile(remote_file, remote_path, 
					store_directory) == false)
				{
					continue;
				}
				// On va traiter le fichier
				should_reboot |= processFile(remote_file, remote_path, 
					store_directory, temporary_directory);
				// On va indiquer qu'au moins un fichier a �t� trait�
				at_least_one_file_processed = true;
			}
			appendToTextPane("&Update_Success");
		}
		catch(InnerException exception)
		{
			// Si aucun fichier n'a �t� trait� ou qu'il n'est pas n�cessaire
			// de red�marrer la Console, on renvoie l'exception
			if(at_least_one_file_processed == false || should_reboot == false)
			{
				trace_methods.endOfMethod();
				throw exception;
			}
			// Sinon, il faut afficher un message � l'utilisateur concernant
			// l'erreur
			appendToTextPane("&Update_Failed");
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotInstallUpdateFile", exception);
			// On continue (voir ci dessous)
		}
		if(should_reboot == true)
		{
			// On va afficher un message indiquant que la Console va �tre ferm�e
			getMainWindowInterface().showPopup("Information", 
				"&Update_ShouldReboot", null);
			// On va quitter
			getMainWindowInterface().exitWindow(false, false);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkDirectory
	* 
	* Description:
	* Cette m�thode est charg�e de v�rifier que le r�pertoire dont le nom est 
	* pass� en argument existe, n'est pas un fichier et est inscriptible.
	* Si ce n'est pas le cas, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - directory: Le r�pertoire � v�rifier.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private void checkDirectory(
		String directory
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "checkDirectory");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("directory=" + directory);
		// On v�rifie la validit� de l'argument
		if(directory == null || directory.equals("") == true)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On v�rifie que le r�pertoire existe
		File the_directory = new File(directory);
		if(the_directory.exists() == false || 
			the_directory.isDirectory() == false)
		{
			trace_errors.writeTrace("Le r�pertoire " + directory + 
				" n'existe pas ou est un fichier !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException(MessageManager.getMessage(
				"&ERR_DirectoryNotExistOrIsFile"), directory, null);
		}
		// On v�rifie que le fichier est inscriptible
		if(the_directory.canWrite() == false)
		{
			trace_errors.writeTrace("Le r�pertoire " + directory +
				" n'est pas inscriptible !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException(MessageManager.getMessage(
				"&ERR_DirectoryNotWritable"), directory, null);
		}
		// On va r�cup�rer la liste des fichiers
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkFile
	* 
	* Description:
	* Cette m�thode est charg�e de v�rifier si le fichier distant pass� en 
	* argument doit �tre t�l�charg� et install� ou non.
	* Pour que le fichier distant soit t�l�charg� et install�, il faut :
	*  - que celui-ci soit nouveau,
	*  - ou que sa date de modification soit post�rieure � celle de la copie 
	*    locale,
	*  - et que l'utilisateur accepte son t�l�chargement et son installation.
	* 
	* La copie locale est un fichier de m�me nom que le fichier distant situ� 
	* dans le d�p�t local, repr�sent� par le second argument.
	* 
	* Arguments:
	*  - remoteFile: Le nom du fichier distant,
	*  - remoteFilePath: Le chemin absolu du fichier distant,
	*  - storeDirectory: Le chemin du d�p�t local.
	* 
	* Retourne: true si le fichier doit �tre t�l�charg� et install�, false 
	* sinon.
	* ----------------------------------------------------------*/
	private boolean checkFile(
		String remoteFile,
		String remoteFilePath,
		String storeDirectory
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "checkFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String local_file_name = null;
		boolean do_install = false;
		String first_message = null;
		String second_message = null;
		String[] extra_info = { remoteFile };

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("remoteFile=" + remoteFile);
		trace_arguments.writeTrace("remoteFilePath=" + remoteFilePath);
		trace_arguments.writeTrace("storeDirectory=" + storeDirectory);
		// On va construire le nom du fichier local
		local_file_name = storeDirectory + File.separatorChar + remoteFile;
		// On v�rifie la validit� des arguments
		if(local_file_name == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// On va regarder si le fichier existe
		File local_file = new File(local_file_name);
		if(local_file.exists() == false)
		{
			trace_debug.writeTrace("Le fichier local n'existe pas !");
			first_message = MessageManager.getMessage("&Update_FileIsNew");
			// On va demander � l'utilisateur s'il souhaite proc�der � son
			// t�l�chargement et son installation
			if(getMainWindowInterface().showPopup("YesNoQuestion", 
				"&Update_NewFileDownload", extra_info) ==
				JOptionPane.NO_OPTION)
			{
				trace_debug.writeTrace(
					"L'utilisateur a refus� l'installation du fichier: " +
					remoteFile);
				second_message = MessageManager.getMessage("&Update_Skipped");
				do_install = false;  
			}
			else
			{
				trace_debug.writeTrace(
					"L'utilisateur a accept� l'installation du fichier: " +
					remoteFile);
				second_message = MessageManager.getMessage("&Update_Validated");
				do_install = true;
			}
			// On affiche un message et on sort
			appendToTextPane(MessageManager.getMessage(first_message) + " " +
				MessageManager.getMessage(second_message));
			return do_install;
		}
		trace_debug.writeTrace("Le fichier local existe !");
		// On v�rifie que le fichier local est bien un fichier
		if(local_file.isFile() == false)
		{
			trace_errors.writeTrace("Le fichier local " + local_file_name +
				" est un r�pertoire !");
			// On va afficher un message d'erreur et sortir
			appendToTextPane("&ERR_LocalFileIsDirectory");
			trace_methods.endOfMethod();
			return false;
		}
		// Est-ce que la date de modification a chang�. Pour le savoir, il
		// faut r�cup�rer une interface de lecture
		try
		{
			PortalInterfaceProxy portal_proxy =
				PortalInterfaceProxy.getInstance();
			FileReaderInterface file_interface = 
				portal_proxy.getFileReader(remoteFilePath);
			// Est-ce que la copie locale est plus r�cente ?
			long local_file_modification_time = 
				local_file.lastModified() / 1000; // Temps en millisecondes
			long remote_file_modification_time = 
				file_interface.getFileModificationTime();
			trace_debug.writeTrace("local_file_modification_time=" +
				local_file_modification_time);
			trace_debug.writeTrace("remote_file_modification_time=" +
				remote_file_modification_time);
			// On regarde � 5 mn pr�s
			if(local_file_modification_time >= 
				remote_file_modification_time - (5 * 60))
			{
				trace_debug.writeTrace("La copie locale est plus r�cente !");
				// On va afficher un message et sortir
				appendToTextPane("&Update_FileHasNotChanged");
				trace_methods.endOfMethod();
				return false; 
			}
			trace_debug.writeTrace("La copie distante est plus r�cente !");
			first_message = "&Update_FileHasChanged";
			// On va demander � l'utilisateur s'il souhaite proc�der � son
			// t�l�chargement et son installation
			if(getMainWindowInterface().showPopup("YesNoQuestion", 
				"&Update_ModifiedFileDownload", extra_info) ==
				JOptionPane.NO_OPTION)
			{
				trace_debug.writeTrace(
					"L'utilisateur a refus� l'installation du fichier: " +
					remoteFile);
				second_message = MessageManager.getMessage("&Update_Skipped");
				do_install = false;  
			}
			else
			{
				trace_debug.writeTrace(
					"L'utilisateur a accept� l'installation du fichier: " +
					remoteFile);
				second_message = MessageManager.getMessage("&Update_Validated");
				do_install = true;
			}
			// On va afficher un message dans la zone
			appendToTextPane(MessageManager.getMessage(first_message) + " " +
				MessageManager.getMessage(second_message));
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la v�rification du fichier " +
				remoteFile + ": " + exception.getMessage());
			// On va afficher un message d'erreur
			appendToTextPane(MessageManager.getMessage("&ERR_CannotCheckFile") +
				" " + exception.getMessage());
			getMainWindowInterface().showPopupForException("&ERR_CannotCheckFile", exception);
			do_install = false;
		}
		trace_methods.endOfMethod();
		return do_install;
	}

	/*----------------------------------------------------------
	* Nom: processFile
	* 
	* Description:
	* Cette m�thode permet d'effectuer le traitement d'un fichier de mise � 
	* jour, dont le chemin dans le d�p�t Console du Portail est pass� en 
	* argument.
	* Le traitement du fichier consiste dans un premier temps � le t�l�charger 
	* dans le d�p�t local, dont le chemin est pass� en second argument. Cela 
	* est fait via la classe FileDownloader.
	* Ensuite, le fichier est d�compress� dans le r�pertoire temporaire pass� 
	* en argument.
	* Le fichier de description de la mise � jour est lu, avec v�rification de 
	* la compatibilit� de la mise � jour avec la version de la Console, puis, 
	* pour chaque �l�ment, la mise � jour est appliqu�e suivant la description 
	* (mise � jour, cr�ation, etc.).
	* 
	* Si un probl�me survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - remoteFileName: Le nom du fichier de mise � jour dans le d�p�t des 
	*    Consoles sur le Portail 
	*  - remoteFilePath: Le chemin du fichier de mise � jour,
 	*  - storeDirectory: Le r�pertoire local de d�p�t des fichiers de mise � 
	*    jour,
	*  - temporaryDirectory: Le chemin du r�pertoire temporaire.
	* 
	* Retourne: true si la Console doit �tre red�marr�e � l'issue de 
	* l'installation, false sinon.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private boolean processFile(
		String remoteFileName,
		String remoteFilePath,
		String storeDirectory,
		String temporaryDirectory
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "processFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		boolean reboot_afterwards = false;
		String local_file_name = null;
		File local_file = null;
		// La taille maximale de t�l�chargement est de 50 Mo
		long maximum_size = 50 * 1024 * 1024;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("remoteFileName=" + remoteFileName);
		trace_arguments.writeTrace("remoteFilePath=" + remoteFilePath);
		trace_arguments.writeTrace("storeDirectory=" + storeDirectory);
		trace_arguments.writeTrace("temporaryDirectory=" + temporaryDirectory);
		// On va construire le nom du fichier local
		local_file_name = storeDirectory + File.separatorChar + remoteFileName; 
		// On v�rifie la validit� des arguments
		if(local_file_name == null || temporaryDirectory == null || 
			temporaryDirectory.equals("") == true)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		local_file = new File(local_file_name);
		// Il faut commencer par proc�der au t�l�chargement du fichier
		// On va r�cup�rer une interface de lecture du fichier
		PortalInterfaceProxy portal_proxy = 
			PortalInterfaceProxy.getInstance();
		FileReaderInterface reader_interface = 
			portal_proxy.getFileReader(remoteFilePath);
		try
		{
			appendToTextPane("&Update_DownloadingFile");
			_operationLabel.setText(MessageManager.getMessage(
				"&Update_DownloadingFile"));
			_fileLabel.setText(remoteFileName);
			// On va lancer le t�l�chargement du fichier
			int done = FileDownloader.downloadFile(reader_interface, 
				maximum_size, local_file, this);
			if(done == -1)
			{
				trace_errors.writeTrace("Le fichier " + remoteFileName +					"a une taille nulle !");
				appendToTextPane("&Update_FileHasNullSize");
				// On sort
				trace_methods.endOfMethod();
				return false;
			}
			else if(done == 1)
			{
				trace_errors.writeTrace("Le fichier " + remoteFileName +
					"a une taille trop grande !");
				appendToTextPane("&Update_FileIsTooLarge");
				// On sort
				trace_methods.endOfMethod();
				return false;
			}
			// On peut remettre � z�ro les zones de progression
			updateProgress("", "", 0, 0);
			// On va lancer l'extraction de l'archive
			appendToTextPane("&Update_UnzippingFile");
			EntryProcessorInterface processor_interface = 
				EntryProcessorFactory.getProcessorForEntry(
				local_file.getName());
			processor_interface.processEntry(local_file.getName(), local_file,
				temporaryDirectory, this);
			// On peut remettre � z�ro les zones de progression
			updateProgress("", "", 0, 0);
			// On va lancer le traitement de la mise � jour
			appendToTextPane("&Update_InstallingUpdate");
			UpdateDefinitionFileReader definition_reader =
				new UpdateDefinitionFileReader();
			reboot_afterwards = 
				definition_reader.processFile(temporaryDirectory,
				System.getProperty("user.dir"), this);
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors du traitement du fichier " +
				remoteFileName + ": " + exception.getMessage());
			// On va supprimer le fichier t�l�charg�
			if(local_file.exists() == true)
			{
				trace_debug.writeTrace("Destruction du fichier t�l�charg�");
				local_file.delete();
			}
			// On va proc�der au nettoyage du r�pertoire temporaire
			clearTemporaryDirectory(temporaryDirectory);
			// On a termin�
			updateProgress("", "", 0, 0);
			appendToTextPane("&Update_TerminatedFailed");
			// On renvoie l'exception
			throw exception;
		}
		// On a termin�
		updateProgress("", "", 0, 0);
		appendToTextPane("&Update_TerminatedSuccess");
		// On va proc�der au nettoyage du r�pertoire temporaire
		clearTemporaryDirectory(temporaryDirectory);
		appendToTextPane("");
		trace_methods.endOfMethod();
		return reboot_afterwards;
	}

	/*----------------------------------------------------------
	* Nom: clearTemporaryDirectory
	* 
	* Description:
	* Cette m�thode permet de nettoyer le r�pertoire temporaire dont le chemin 
	* est pass� en argument. Le nettoyage consiste � supprimer tout fichier ou 
	* r�pertoire se trouvant dans ce r�pertoire temporaire.
	* 
	* Arguments:
	*  - temporaryDirectory: Le chemin du r�pertoire temporaire.
 	* ----------------------------------------------------------*/
 	private void clearTemporaryDirectory(
 		String temporaryDirectory
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "clearTemporaryDirectory");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("temporaryDirectory=" + temporaryDirectory);
		// On v�rifie la validit� de l'argument
		if(temporaryDirectory == null || temporaryDirectory.equals("") == true)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va r�cup�rer la liste des enfants du r�pertoire
		File directory = new File(temporaryDirectory);
		File[] files = directory.listFiles();
		// On va traiter enfant par enfant
		for(int index = 0 ; index < files.length ; index ++)
		{
			trace_debug.writeTrace("file=" + files[index].getAbsolutePath());
			// S'il s'agit d'un fichier, il faut tout simplement le supprimer
			if(files[index].isFile() == true)
			{
				trace_debug.writeTrace("Il s'agit d'un fichier");
				if(files[index].delete() == false)
				{
					trace_errors.writeTrace(
						"Impossible de supprimer le fichier: " +
						files[index].getAbsolutePath());
					continue;
				}
				trace_debug.writeTrace("Fichier supprim�");
			}
			else
			{
				trace_debug.writeTrace("Il s'agit d'un r�pertoire");
				// On va d�clencher une suppression r�cursive
				clearTemporaryDirectory(files[index].getAbsolutePath());
				// Puis on supprime le r�pertoire
				if(files[index].delete() == false)
				{
					trace_errors.writeTrace(
						"Impossible de supprimer le r�pertoire: " +
						files[index].getAbsolutePath());
					continue;
				}
				trace_debug.writeTrace("R�pertoire supprim�");
			}
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: appendToTextPane
	* 
	* Description:
	* Cette m�thode est charg�e d'ins�rer le message pass� en argument dans 
	* la zone d'affichage. Si le message est un symbole (commen�ant par '&'), 
	* il est pr�alablement traduit.
	* 
	* Arguments:
	*  - message: Le message � ins�rer dans la zone d'affichage.
 	* ----------------------------------------------------------*/
 	private void appendToTextPane(
 		String message
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "appendToTextPane");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("message=" + message);
		// Est-ce que le message est valide ?
		if(message == null || message.equals("") == true)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// Est-ce que le message doit �tre traduit ?
		if(message.startsWith("&") == true)
		{
			message = MessageManager.getMessage(message);
		}
		try
		{
			_textPane.getDocument().insertString(
				_textPane.getDocument().getLength(), message + "\n", null);
			_textPane.setCaretPosition(_textPane.getDocument().getLength());
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Erreur lors de l'insertion du message: " +
				exception.getMessage());
		}
		trace_methods.endOfMethod();
 	}
}
