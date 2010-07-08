/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/UpdateProcessor.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de mise à jour de la Console
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
* Suppression de la méthode buildLocalFileName().
* Correction d'un bug lors de la vérification d'un fichier.
*
* Revision 1.7  2008/01/31 16:59:27  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.6  2006/11/03 10:31:31  tz
* Utilisation de FileSystemEntry au lieu de StoreEntry.
*
* Revision 1.5  2006/08/11 13:34:26  tz
* Adaptation à corba-R2_1_0-AL-1_0.
*
* Revision 1.4  2006/03/13 15:14:47  tz
* Suppression de commentaires inutiles.
*
* Revision 1.3  2006/03/09 14:17:25  tz
* Le test sur les dates de dernière modification est effectué avec une
* tolérance de 5 mn.
*
* Revision 1.2  2006/03/08 15:59:13  tz
* Passage en mode production
*
* Revision 1.1  2005/12/23 13:23:43  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.update;

//
// Imports système
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
* Cette classe implémente le processeur chargé de la mise à jour de la Console 
* I-SIS. La mise à jour de la Console consiste à télécharger depuis le Portail 
* tous les fichiers nouveaux ou ayant changé, à en lire le fichier de 
* description, puis à appliquer les modifications (installation de nouveaux 
* fichiers, modification de fichiers, modification des fichiers de 
* configuration ou de démarrage...).
* Elle redéfinit la classe ProcessorFrame car il s'agit d'un processeur 
* graphique, implémente l'interface DownloadProgressInterface afin de suivre 
* les téléchargements, ainsi que l'interface UpdateProgressInterface. 
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
	* Cette méthode est le constructeur par défaut de la classe. Elle n'est 
	* présentée que pour des raisons de lisibilité.
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
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée afin d'effectuer tout pré-chargement de données.
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
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* permet de savoir si le processeur peut être invoqué depuis l'arbre 
	* d'exploration.
	* Dans le cas du processeur de mise à jour, seule l'invocation depuis le 
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
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* permet de savoir si le processeur peut être invoqué depuis un affichage 
	* sous forme de tableau.
	* Dans le cas du processeur de mise à jour, seule l'invocation depuis le 
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
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* permet de savoir si le processeur peut être invoqué depuis le menu 
	* "Outils".
	* Dans le cas du processeur de mise à jour, seule l'invocation depuis le 
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* permet de récupérer une description du processeur.
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
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* permet de récupérer un intitulé de l'élément de menu dans le cas de 
	* processeurs globaux.
	* 
	* Retourne: L'intitulé de l'élément de menu.
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
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée lorsque l'exécution du processeur est requise.
	* La sous-fenêtre du processeur est construite via la méthode makePanel(), 
	* puis elle est affichée. Ensuite, la présence de fichiers à télécharger 
	* et leur traitement éventuel est assuré par la méthode checkStore().
	* 
	* Si un problème survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface,
	*  - menuItem: Une référence sur l'objet JMenuItem par lequel le 
	*    processeur a été invoqué,
	*  - parameters: Les arguments éventuels d'exécution du processeur 
	*   (ignorés dans le cas présent),
	*  - preprocessing: Une chaîne de pré-processing (ignorée dans le cas 
	*    présent),
	*  - postprocessing: Une chaîne de post-processing (ignorée dans le cas
	*    présent),
	*  - selectedNode: Une référence sur un objet DefaultMutableTreeNode 
	*    représentant le noeud sélectionné (ignoré dans le cas présent).
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
		// On vérifie la validité des arguments
		if(windowInterface == null || menuItem == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On vérifie que la Console est connectée à un Portail
		if(windowInterface.isConnected() == false)
		{
			trace_errors.writeTrace(
				"La Console n'est pas connectée à un Portail");
			// On va afficher un message à l'utilisateur
			windowInterface.showPopup("Information",
				MessageManager.getMessage("&ERR_MustBeConnectedFirst"), null);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On appelle la méthode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On positionne le titre de la sous-fenêtre
		setTitle(MessageManager.getMessage("&Update_Title"));
		// On construit le panneau de la fenêtre
		makePanel();
		// On positionne les dimensions de la fenêtre
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(new Dimension(450, 300));
		// On affiche la fenêtre
		display();
		// On lance la vérification du dépôt
		checkStore();
		// Une fois ici, on peut activer le bouton "Fermer"
		_closeButton.setEnabled(true);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée lorsque la sous-fenêtre doit être fermée.
	* Elle libère toutes les ressources allouées par l'objet.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateProcessor", "close");

		trace_methods.beginningOfMethod();
		// On ferme la fenêtre en appelant la méthode de la super-classe.
		super.close();
		// On libère les références
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
	* Cette méthode redéfinit celle de l'interface ProcessorFrame. Elle permet 
	* de retourner un objet étant un double de l'objet courant.
	* 
	* Retourne: Une référence sur un nouvel objet UpdateProcessor.
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
	* Cette méthode redéfinit celle de l'interface DownloadProgressInterface. 
	* Elle permet de fixer la progression de l'opération de téléchargement.
	* 
	* Arguments:
	*  - progress: La progression du téléchargement, exprimée en pourcentage 
	*    (0 à 100).
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
	* Cette méthode redéfinit celle de l'interface UpdateProgressInterface. 
	* Elle permet de mettre à jour les champs de la zone de progression à 
	* partir des informations passées en argument.
	*  - L'intitulé de l'opération est mis à jour si l'argument operation est 
	*    non null,
	*  - Le nom du fichier en cours de traitement est mis à jour si l'argument 
	*    file est non null,
	*  - La progression du fichier est mise à jour,
	*  - La progression globale est mise à jour.
	* 
	* Arguments:
	*  - operation: L'intitulé de l'opération en cours, ou null,
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
		// On s'assure d'effectuer la mise à jour dans le thread Swing
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
	* Cet attribut maintient une référence sur un objet NonEditableTextPane 
	* chargé d'afficher les étapes de la mise à jour.
	* ----------------------------------------------------------*/
	private NonEditableTextPane _textPane;

	/*----------------------------------------------------------
	* Nom: _operationLabel
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JLabel chargé de 
	* l'affichage de l'opération en cours.
	* ----------------------------------------------------------*/
	private JLabel _operationLabel;

	/*----------------------------------------------------------
	* Nom: _fileLabel
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JLabel chargé de 
	* l'affichage du nom du fichier sur lequel l'opération en cours est 
	* effectuée.
	* ----------------------------------------------------------*/
	private JLabel _fileLabel;

	/*----------------------------------------------------------
	* Nom: _fileProgressBar
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JProgressBar chargé 
	* d'afficher la progression de l'opération en cours sur le fichier.
	* ----------------------------------------------------------*/
	private JProgressBar _fileProgressBar;

	/*----------------------------------------------------------
	* Nom: _globalProgressBar
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JProgressBar chargé 
	* d'afficher la progression de l'installation du fichier de mise à jour 
	* courant.
	* ----------------------------------------------------------*/
	private JProgressBar _globalProgressBar;

	/*----------------------------------------------------------
	* Nom: _closeButton
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JButton permettant de 
	* fermer la sous-fenêtre.
	* ----------------------------------------------------------*/
	private JButton _closeButton;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode permet de construire la sous-fenêtre du processeur.
	* Celle-ci est constitué de deux zones principale : Une zone de texte 
	* permettant de suivre l'ensemble des tâches effectuées, puis une zone de 
	* progression permettant de suivre pas à pas la progression de 
	* l'installation d'une mise à jour.
	* La zone de progression permet de suivre la progression à la fois du 
	* téléchargement des fichiers de mise à jour, et du traitement de ceux-ci, 
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
		// On va créer le panneau de progression
		GridBagLayout progress_layout = new GridBagLayout();
		GridBagConstraints progress_constraints = 
			new GridBagConstraints(0, 0, 1, 1, 0, 0, 
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(1, 2, 1, 2),	0, 0);
		JPanel progress_panel = new JPanel(progress_layout);
		// On construit le libellé d'opération
		JLabel operation_label = 
			new JLabel(MessageManager.getMessage("&Update_Operation"));
		progress_layout.setConstraints(operation_label, progress_constraints);
		progress_panel.add(operation_label);
		// On construit le libellé de fichier
		JLabel file_label = 
			new JLabel(MessageManager.getMessage("&Update_File"));
		progress_constraints.gridy ++;
		progress_layout.setConstraints(file_label, progress_constraints);
		progress_panel.add(file_label);
		// On construit le libellé de progression de fichier
		JLabel file_progress = 
			new JLabel(MessageManager.getMessage("&Update_FileProgress"));
		progress_constraints.gridy ++;
		progress_layout.setConstraints(file_progress, progress_constraints);
		progress_panel.add(file_progress, progress_constraints);
		// On construit le libellé de progression globale
		JLabel global_progress =
			new JLabel(MessageManager.getMessage("&Update_GlobalProgress"));
		progress_constraints.gridy ++;
		progress_layout.setConstraints(global_progress, progress_constraints);
		progress_panel.add(global_progress, progress_constraints);
		// On construit le libellé de l'opération
		_operationLabel = new JLabel();
		progress_constraints.gridx ++;
		progress_constraints.gridy = 0;
		progress_constraints.weightx = 1;
		progress_layout.setConstraints(_operationLabel, progress_constraints);
		progress_panel.add(_operationLabel);
		// On construit le libellé du fichier
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
		// On ajoute le panneau central au milieu de la sous-fenêtre
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(central_panel, BorderLayout.CENTER);
		// Maintenant, on va créer le bouton Fermer
		_closeButton = new JButton(MessageManager.getMessage("&Update_Close"));
		_closeButton.setEnabled(false);
		// On ajoute le callback sur le bouton
		_closeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture
				close();
			}
		});
		// On crée un panneau avec un GridBagLayout
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
	* Cette méthode permet de vérifier dans le dépôt des fichiers Console du 
	* Portail la présence de nouveaux fichiers ou de fichiers modifiés.
	* Si de tels fichiers sont trouvés, une boite de dialogue est affichée, 
	* pour chaque fichier, à l'utilisateur afin de lui demander si celui-ci 
	* doit être téléchargé et installé.
	* Si l'utilisateur répond par l'affirmative, le fichier est traité via la 
	* méthode processFile().
	* Le répertoire local de dépôt est défini par le paramètre de 
	* configuration "Console.StoreDirectory".
	* Le répertoire temporaire est défini par le paramètre 
	* "Console.TemporaryDirectory".
	*
	* Si une erreur survient, l'exception InnerException est levée.
	* 
	* Lève: InnerException.
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
		// On commence par récupérer la configuration
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
				"Impossible de récupérer la configuration: " +
				exception.getMessage());
			// On continue quand même
		}
		trace_debug.writeTrace("store_directory=" + store_directory);
		trace_debug.writeTrace("temporary_directory=" + temporary_directory);
		// On vérifie que les répertoires existent et sont inscriptibles
		checkDirectory(store_directory);
		checkDirectory(temporary_directory);
		// On s'assure de nettoyer le répertoire temporaire
		clearTemporaryDirectory(temporary_directory);
		// On va récupérer le proxy de Portail
		appendToTextPane("&Update_CheckingStore");
		PortalInterfaceProxy portal_proxy = PortalInterfaceProxy.getInstance();
		// On va récupérer la liste des fichiers du dépôt Console sur le 
		// Portail
		FileSystemEntry[] remote_files = portal_proxy.getStoreFileList();
		try
		{
			// On va vérifier fichier par fichier la nécessité d'un 
			// téléchargement
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
				// On vérifie qu'il s'agit d'un fichier archive au format zip
				if(remote_file.endsWith(".zip") == false && 
					remote_file.endsWith(".ZIP") == false)
				{
					trace_debug.writeTrace(
						"Le format du fichier n'est pas supporté !");
					appendToTextPane("&ERR_FileHasUnsupportedFormat");
					continue;
				}
				// On vérifie si le fichier a changé ou est nouveau et que 
				// l'utilisateur veut l'installer
				if(checkFile(remote_file, remote_path, 
					store_directory) == false)
				{
					continue;
				}
				// On va traiter le fichier
				should_reboot |= processFile(remote_file, remote_path, 
					store_directory, temporary_directory);
				// On va indiquer qu'au moins un fichier a été traité
				at_least_one_file_processed = true;
			}
			appendToTextPane("&Update_Success");
		}
		catch(InnerException exception)
		{
			// Si aucun fichier n'a été traité ou qu'il n'est pas nécessaire
			// de redémarrer la Console, on renvoie l'exception
			if(at_least_one_file_processed == false || should_reboot == false)
			{
				trace_methods.endOfMethod();
				throw exception;
			}
			// Sinon, il faut afficher un message à l'utilisateur concernant
			// l'erreur
			appendToTextPane("&Update_Failed");
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotInstallUpdateFile", exception);
			// On continue (voir ci dessous)
		}
		if(should_reboot == true)
		{
			// On va afficher un message indiquant que la Console va être fermée
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
	* Cette méthode est chargée de vérifier que le répertoire dont le nom est 
	* passé en argument existe, n'est pas un fichier et est inscriptible.
	* Si ce n'est pas le cas, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - directory: Le répertoire à vérifier.
	* 
	* Lève: InnerException.
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
		// On vérifie la validité de l'argument
		if(directory == null || directory.equals("") == true)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On vérifie que le répertoire existe
		File the_directory = new File(directory);
		if(the_directory.exists() == false || 
			the_directory.isDirectory() == false)
		{
			trace_errors.writeTrace("Le répertoire " + directory + 
				" n'existe pas ou est un fichier !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException(MessageManager.getMessage(
				"&ERR_DirectoryNotExistOrIsFile"), directory, null);
		}
		// On vérifie que le fichier est inscriptible
		if(the_directory.canWrite() == false)
		{
			trace_errors.writeTrace("Le répertoire " + directory +
				" n'est pas inscriptible !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException(MessageManager.getMessage(
				"&ERR_DirectoryNotWritable"), directory, null);
		}
		// On va récupérer la liste des fichiers
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkFile
	* 
	* Description:
	* Cette méthode est chargée de vérifier si le fichier distant passé en 
	* argument doit être téléchargé et installé ou non.
	* Pour que le fichier distant soit téléchargé et installé, il faut :
	*  - que celui-ci soit nouveau,
	*  - ou que sa date de modification soit postérieure à celle de la copie 
	*    locale,
	*  - et que l'utilisateur accepte son téléchargement et son installation.
	* 
	* La copie locale est un fichier de même nom que le fichier distant situé 
	* dans le dépôt local, représenté par le second argument.
	* 
	* Arguments:
	*  - remoteFile: Le nom du fichier distant,
	*  - remoteFilePath: Le chemin absolu du fichier distant,
	*  - storeDirectory: Le chemin du dépôt local.
	* 
	* Retourne: true si le fichier doit être téléchargé et installé, false 
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
		// On vérifie la validité des arguments
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
			// On va demander à l'utilisateur s'il souhaite procéder à son
			// téléchargement et son installation
			if(getMainWindowInterface().showPopup("YesNoQuestion", 
				"&Update_NewFileDownload", extra_info) ==
				JOptionPane.NO_OPTION)
			{
				trace_debug.writeTrace(
					"L'utilisateur a refusé l'installation du fichier: " +
					remoteFile);
				second_message = MessageManager.getMessage("&Update_Skipped");
				do_install = false;  
			}
			else
			{
				trace_debug.writeTrace(
					"L'utilisateur a accepté l'installation du fichier: " +
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
		// On vérifie que le fichier local est bien un fichier
		if(local_file.isFile() == false)
		{
			trace_errors.writeTrace("Le fichier local " + local_file_name +
				" est un répertoire !");
			// On va afficher un message d'erreur et sortir
			appendToTextPane("&ERR_LocalFileIsDirectory");
			trace_methods.endOfMethod();
			return false;
		}
		// Est-ce que la date de modification a changé. Pour le savoir, il
		// faut récupérer une interface de lecture
		try
		{
			PortalInterfaceProxy portal_proxy =
				PortalInterfaceProxy.getInstance();
			FileReaderInterface file_interface = 
				portal_proxy.getFileReader(remoteFilePath);
			// Est-ce que la copie locale est plus récente ?
			long local_file_modification_time = 
				local_file.lastModified() / 1000; // Temps en millisecondes
			long remote_file_modification_time = 
				file_interface.getFileModificationTime();
			trace_debug.writeTrace("local_file_modification_time=" +
				local_file_modification_time);
			trace_debug.writeTrace("remote_file_modification_time=" +
				remote_file_modification_time);
			// On regarde à 5 mn près
			if(local_file_modification_time >= 
				remote_file_modification_time - (5 * 60))
			{
				trace_debug.writeTrace("La copie locale est plus récente !");
				// On va afficher un message et sortir
				appendToTextPane("&Update_FileHasNotChanged");
				trace_methods.endOfMethod();
				return false; 
			}
			trace_debug.writeTrace("La copie distante est plus récente !");
			first_message = "&Update_FileHasChanged";
			// On va demander à l'utilisateur s'il souhaite procéder à son
			// téléchargement et son installation
			if(getMainWindowInterface().showPopup("YesNoQuestion", 
				"&Update_ModifiedFileDownload", extra_info) ==
				JOptionPane.NO_OPTION)
			{
				trace_debug.writeTrace(
					"L'utilisateur a refusé l'installation du fichier: " +
					remoteFile);
				second_message = MessageManager.getMessage("&Update_Skipped");
				do_install = false;  
			}
			else
			{
				trace_debug.writeTrace(
					"L'utilisateur a accepté l'installation du fichier: " +
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
				"Erreur lors de la vérification du fichier " +
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
	* Cette méthode permet d'effectuer le traitement d'un fichier de mise à 
	* jour, dont le chemin dans le dépôt Console du Portail est passé en 
	* argument.
	* Le traitement du fichier consiste dans un premier temps à le télécharger 
	* dans le dépôt local, dont le chemin est passé en second argument. Cela 
	* est fait via la classe FileDownloader.
	* Ensuite, le fichier est décompressé dans le répertoire temporaire passé 
	* en argument.
	* Le fichier de description de la mise à jour est lu, avec vérification de 
	* la compatibilité de la mise à jour avec la version de la Console, puis, 
	* pour chaque élément, la mise à jour est appliquée suivant la description 
	* (mise à jour, création, etc.).
	* 
	* Si un problème survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - remoteFileName: Le nom du fichier de mise à jour dans le dépôt des 
	*    Consoles sur le Portail 
	*  - remoteFilePath: Le chemin du fichier de mise à jour,
 	*  - storeDirectory: Le répertoire local de dépôt des fichiers de mise à 
	*    jour,
	*  - temporaryDirectory: Le chemin du répertoire temporaire.
	* 
	* Retourne: true si la Console doit être redémarrée à l'issue de 
	* l'installation, false sinon.
	*
	* Lève: InnerException.
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
		// La taille maximale de téléchargement est de 50 Mo
		long maximum_size = 50 * 1024 * 1024;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("remoteFileName=" + remoteFileName);
		trace_arguments.writeTrace("remoteFilePath=" + remoteFilePath);
		trace_arguments.writeTrace("storeDirectory=" + storeDirectory);
		trace_arguments.writeTrace("temporaryDirectory=" + temporaryDirectory);
		// On va construire le nom du fichier local
		local_file_name = storeDirectory + File.separatorChar + remoteFileName; 
		// On vérifie la validité des arguments
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
		// Il faut commencer par procéder au téléchargement du fichier
		// On va récupérer une interface de lecture du fichier
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
			// On va lancer le téléchargement du fichier
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
			// On peut remettre à zéro les zones de progression
			updateProgress("", "", 0, 0);
			// On va lancer l'extraction de l'archive
			appendToTextPane("&Update_UnzippingFile");
			EntryProcessorInterface processor_interface = 
				EntryProcessorFactory.getProcessorForEntry(
				local_file.getName());
			processor_interface.processEntry(local_file.getName(), local_file,
				temporaryDirectory, this);
			// On peut remettre à zéro les zones de progression
			updateProgress("", "", 0, 0);
			// On va lancer le traitement de la mise à jour
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
			// On va supprimer le fichier téléchargé
			if(local_file.exists() == true)
			{
				trace_debug.writeTrace("Destruction du fichier téléchargé");
				local_file.delete();
			}
			// On va procéder au nettoyage du répertoire temporaire
			clearTemporaryDirectory(temporaryDirectory);
			// On a terminé
			updateProgress("", "", 0, 0);
			appendToTextPane("&Update_TerminatedFailed");
			// On renvoie l'exception
			throw exception;
		}
		// On a terminé
		updateProgress("", "", 0, 0);
		appendToTextPane("&Update_TerminatedSuccess");
		// On va procéder au nettoyage du répertoire temporaire
		clearTemporaryDirectory(temporaryDirectory);
		appendToTextPane("");
		trace_methods.endOfMethod();
		return reboot_afterwards;
	}

	/*----------------------------------------------------------
	* Nom: clearTemporaryDirectory
	* 
	* Description:
	* Cette méthode permet de nettoyer le répertoire temporaire dont le chemin 
	* est passé en argument. Le nettoyage consiste à supprimer tout fichier ou 
	* répertoire se trouvant dans ce répertoire temporaire.
	* 
	* Arguments:
	*  - temporaryDirectory: Le chemin du répertoire temporaire.
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
		// On vérifie la validité de l'argument
		if(temporaryDirectory == null || temporaryDirectory.equals("") == true)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va récupérer la liste des enfants du répertoire
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
				trace_debug.writeTrace("Fichier supprimé");
			}
			else
			{
				trace_debug.writeTrace("Il s'agit d'un répertoire");
				// On va déclencher une suppression récursive
				clearTemporaryDirectory(files[index].getAbsolutePath());
				// Puis on supprime le répertoire
				if(files[index].delete() == false)
				{
					trace_errors.writeTrace(
						"Impossible de supprimer le répertoire: " +
						files[index].getAbsolutePath());
					continue;
				}
				trace_debug.writeTrace("Répertoire supprimé");
			}
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: appendToTextPane
	* 
	* Description:
	* Cette méthode est chargée d'insérer le message passé en argument dans 
	* la zone d'affichage. Si le message est un symbole (commençant par '&'), 
	* il est préalablement traduit.
	* 
	* Arguments:
	*  - message: Le message à insérer dans la zone d'affichage.
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
		// Est-ce que le message doit être traduit ?
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
