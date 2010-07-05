/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/edition/FileEditionProcessor.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'édition de fichiers
* DATE:        18/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.edition
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: FileEditionProcessor.java,v $
* Revision 1.5  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.4  2008/02/21 12:08:15  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.3  2008/01/31 16:56:03  tz
* Classe PreprocessingHandler renommée.
* Ajout de l'argument postprocessing à la méthode run().
* Caractère de retour chariot forcé pour les fichiers vides.
*
* Revision 1.2  2006/11/09 12:12:49  tz
* Ajout de la méthode getTitlePrefix().
* Modification des méthodes loadFile() et saveFile() pour une meilleure gestion
* des retours chariot.
*
* Revision 1.1  2006/11/03 10:30:40  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.edition;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.text.Document;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DefaultEditorKit;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

//
//Imports du projet
//
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.gui.UndoableTextPane;
import com.bv.isis.console.core.gui.SearchableTextArea;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.LabelFactory;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.AgentLayerAbstractor;
import com.bv.isis.corbacom.ShortBlock;
import com.bv.isis.corbacom.FileReaderInterface;
import com.bv.isis.corbacom.FileWriterInterface;
import com.bv.isis.corbacom.FileNotFoundException;
import com.bv.isis.corbacom.FileNotReadableException;
import com.bv.isis.corbacom.FileNotWritableException;
import com.bv.isis.corbacom.FileNotReachableException;
import com.bv.isis.corbacom.FileNotCreatableException;
import com.bv.isis.corbacom.UnknownException;
import com.bv.isis.corbacom.IsisParameter;

/*----------------------------------------------------------
* Nom: FileEditionProcessor
* 
* Description:
* Cette classe implémente le processeur d'édition d'un fichier. Ce processeur 
* est chargé de télécharger le contenu d'un fichier spécifié, d'afficher 
* celui-ci dans une zone d'édition, puis de ré-écrire le contenu du fichier 
* avec les données de la zone d'édition.
* Il redéfinit la classe ProcessorFrame car il s'agit d'un processeur 
* graphique.
* ----------------------------------------------------------*/
public class FileEditionProcessor
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: FileEditionProcessor
	* 
	* Description:
	* Il s'agit du constructeur par défaut. Il n'est présenté que pour des 
	* raisons de lisibilité.
	* ----------------------------------------------------------*/
	public FileEditionProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "FileEditionProcessor");

		trace_methods.beginningOfMethod();
		_setFileAsExecutable = false;
		_fileIsNew = false;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée afin de permettre au processeur d'effectuer un pré-
	* chargement de données.
	* Dans le cas présent, le fichier de messages est chargé.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("edition.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorInterface. 
	* Elle est appelée lorsque le processeur doit démarrer son exécution, ou 
	* plutôt son traitement.
	* Dans le cas présent, la méthode effectue la construction de la sous-
	* fenêtre via la méthode makePanel(), résoud la référence au fichier dont 
	* le nom doit être passé en argument via la méthode 
	* resolveFileReference(), puis effectue un premier chargement du contenu 
	* du fichier via la méthode loadFile().
	* 
	* Si une erreur survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowsInterface 
	*    représentant la fenêtre principale de la Console,
	*  - menuItem: Une référence sur un objet JMenuItem correspondant à 
	*    l'élément de menu ayant déclenché l'exécution de ce processeur,
	*  - parameters: Les arguments éventuels d'exécution du processeur,
	*  - preprocessing: Les informations éventuelles de pré-processing,
	*  - postprocessing: Les informations éventuelles de post-processing,
	*  - selectedNode: Une référence éventuelle sur un objet 
	*    DefaultMutableTreeNode correspondant au noeud sélectionné.
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
			"FileEditionProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String title = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Tout d'abord, il faut vérifier l'intégrité des arguments
		if(windowInterface == null || selectedNode == null ||
			!(selectedNode instanceof GenericTreeObjectNode) ||
			parameters == null || parameters.equals("") == true ||
			menuItem == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On appelle la méthode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		windowInterface.setCurrentCursor(Cursor.WAIT_CURSOR, this);
		windowInterface.setProgressMaximum(3);
		// On caste le noeud en GenericTreeObjectNode
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		IndexedList context = selected_node.getContext(true);
		String agent_name = selected_node.getAgentName();
		// On va gérer le pré-processing
		IsisParameter[] preprocessing_parameters = 
			ProcessingHandler.handleProcessingStatement(preprocessing, 
			context, windowInterface, 
			AgentSessionManager.getInstance().getAgentLayerMode(agent_name), 
			selected_node.getServiceSession(), this, true);
		// On va résoudre la référence au fichier
		windowInterface.setStatus("&Edit_ManagingFileReference", null, 0);
		_editedFilePath = manageFileReference(
			LabelFactory.evaluate(parameters, preprocessing_parameters,
			context, agent_name));
		trace_debug.writeTrace("_editedFilePath=" + _editedFilePath);
		// On va récupérer le numéro de l'action d'édition
		String user_name = PasswordManager.getInstance().getUserName();
		String message = MessageManager.getMessage("&LOG_FileEdition");
		_actionId = 
				LogServiceProxy.getActionIdentifier(selected_node.getAgentName(),
				message, user_name, selected_node.getServiceName(),
				selected_node.getServiceType(), selected_node.getIClesName());
		// On va loguer le message d'édition du fichier
		String[] message_data = new String[5];
		message_data[0] = message;
		message_data[1] = MessageManager.getMessage("&LOG_AgentName") +
			selected_node.getAgentName();
		message_data[2] = MessageManager.getMessage("&LOG_UserName") +
			user_name;
		message_data[3] =
			MessageManager.getMessage("&LOG_ServiceSessionId") +
			selected_node.getServiceSessionId();
		message_data[4] = MessageManager.getMessage("&LOG_EditedFile") +
			_editedFilePath;
		LogServiceProxy.addMessageForAction(_actionId, message_data);
		// On va construire le titre
		String extra_info[] = { getEditedFilePath() };
		title = MessageManager.fillInMessage(getTitlePrefix(), extra_info);
		setTitle(title);
		// On va construire la sous-fenêtre
		windowInterface.setStatus("&Edit_BuildingEditorPane", null, 1);
		makePanel();
		setDocumentModified(false);
		// On affiche la fenêtre
		display();
		// On charge le fichier
		windowInterface.setStatus("&Edit_LoadingFile", null, 2);
		windowInterface.setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
		boolean load_ok = loadFile();
		windowInterface.setStatus(null, null, 0);
		// Si le chargement s'est mal passé, en sort
		if(load_ok == false)
		{
			close();
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTitlePrefix
	* 
	* Description:
	* Cette méthode permet de récupérer le début de la chaîne composant le 
	* titre de la sous-fenêtre.
	* Dans le cas présent, elle retourne "Edition de fichier:".
	* 
	* Retourne: Le début de la chaîne de titre.
	* ----------------------------------------------------------*/
	public String getTitlePrefix()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "getTitlePrefix");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&EditFile_Title");
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. 
	* Elle est appelée afin de récupérer une description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&EditionProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* permet de retourner un objet étant un double de l'objet courant.
	* 
	* Retourne: Une référence sur un nouvel objet FileEditionProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new FileEditionProcessor();
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
			"FileEditionProcessor", "close");

		trace_methods.beginningOfMethod();
		// On vérifie si le fichier a été modifié
		if(_documentIsModified == true)
		{
			// On va demander à l'utilisateur s'il veut l'enregistrer
			// d'abord
			int reply = DialogManager.displayDialog("YesNoQuestion", 
				"&Edit_ModifiedFileNotSaved", null, this);
			if(reply == JOptionPane.YES_OPTION)
			{
				saveFile();
			}
		}
		_editedFilePath = null;
		if(_editorPane != null)
		{
			_editorPane.setText("");
			_editorPane = null;
		}
		// On ferme la fenêtre en appelant la méthode de la super-classe.
		super.close();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getEditedFilePath
	* 
	* Description:
	* Cette méthode permet de récupérer le chemin du fichier à éditer.
	* 
	* Retourne: Le chemin du fichier à éditer.
	* ----------------------------------------------------------*/
	public String getEditedFilePath()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "getEditedFilePath");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _editedFilePath;
	}

	/*----------------------------------------------------------
	* Nom: getActionId
	* 
	* Description:
	* Cette méthode permet de récupérer le numéro d'action unique associé à 
	* l'action d'édition de fichier actuelle.
	* 
	* Retourne: Le numéro d'action unique.
	* ----------------------------------------------------------*/
	public String getActionId()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "getActionId");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _actionId;
	}

	/*----------------------------------------------------------
	* Nom: isDocumentModified
	* 
	* Description:
	* Cette méthode permet de savoir si le document (le fichier à éditer) a 
	* été modifié, ou non.
	* 
	* Retourne: true si le document a été modifié, false sinon.
	* ----------------------------------------------------------*/
	public boolean isDocumentModified()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "isDocumentModified");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _documentIsModified;
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: manageFileReference
	* 
	* Description:
	* Cette méthode est chargée de gérer la référence au fichier devant être 
	* édité, celle-ci étant passée en paramètre.
	* Dans le cas présent, aucune gestion n'est nécessaire.
	* 
	* Si une erreur survient lors de la gestion de la référence, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - fileReference: La référence au fichier devant être édité.
	* 
	* Retourne: La référence au fichier devant être édité.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	protected String manageFileReference(
		String fileReference
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "manageFileReference");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("fileReference=" + fileReference);
		// On vérifie que la référence est valide, c-a-d qu'elle existe
		if(fileReference == null || fileReference.equals("") == true)
		{
			// On va lever une exception
			throw new InnerException("&ERR_NoFileReference", null, null);
		}
		trace_methods.endOfMethod();
		return fileReference;
	}

	/*----------------------------------------------------------
	* Nom: setFileAsExecutable
	* 
	* Description:
	* Cette méthode permet de fixer le caractère exécutable qui doit être 
	* donné au fichier lors de sa création.
	* 
	* Arguments:
	*  - setFileAsExecutable: Un booléen indiquant si le fichier doit être 
	*    positionné en tant qu'exécutable (true) ou non (false).
 	* ----------------------------------------------------------*/
 	protected void setFileAsExecutable(
 		boolean setFileAsExecutable
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "setFileAsExecutable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("setFileAsExecutable=" + 
			setFileAsExecutable);
		_setFileAsExecutable = setFileAsExecutable;
		trace_methods.endOfMethod();
 	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _actionId
	* 
	* Description:
	* Cet attribut maintient le numéro d'action correspondant à l'action 
	* d'édition du fichier.
	* ----------------------------------------------------------*/
	private String _actionId;

	/*----------------------------------------------------------
	* Nom: _editedFilePath
	* 
	* Description:
	* Cet attribut maintient une chaîne contenant le chemin du fichier devant 
	* être édité.
	* ----------------------------------------------------------*/
	private String _editedFilePath;

	/*----------------------------------------------------------
	* Nom: _editorPane
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet UndoableTextPane 
	* correspondant à la zone d'édition du fichier.
	* ----------------------------------------------------------*/
	private UndoableTextPane _editorPane;

	/*----------------------------------------------------------
	* Nom: _documentIsModified
	* 
	* Description:
	* Cet attribut maintient un booléen indiquant si le document (le fichier 
	* à éditer) a été modifié, ou non.
	* ----------------------------------------------------------*/
	private boolean _documentIsModified;

	/*----------------------------------------------------------
	* Nom: _fileIsLoading
	* 
	* Description:
	* Cet attribut maintient un booléen indiquant si le fichier est en cours 
	* de chargement, ou non.
	* ----------------------------------------------------------*/
	private boolean _fileIsLoading;

	/*----------------------------------------------------------
	* Nom: _saveButton
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JButton correspondant 
	* au bouton "Enregistrer".
	* ----------------------------------------------------------*/
	private JButton _saveButton;

	/*----------------------------------------------------------
	* Nom: setFileAsExecutable
	* 
	* Description:
	* Cet attribut maitient un booléen indiquant si le fichier doit être 
	* positionné en tant qu'exécutable lors de sa création (true) ou non 
	* (false).
	* ----------------------------------------------------------*/
	private boolean _setFileAsExecutable;

	/*----------------------------------------------------------
	* Nom: _fileIsNew
	* 
	* Description:
	* Cet attribut maintient un booléen indiquant si le fichier va être créé 
	* (true) ou non (false).
	* ----------------------------------------------------------*/
	private boolean _fileIsNew;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de la sous-fenêtre 
	* d'édition de fichier.
	* Cette sous-fenêtre est constituée d'une zone d'édition, correspondant 
	* à une instance de SearchableTextPane, ainsi qu'à une zone de boutons 
	* contenant un bouton de rechargement de fichier, un bouton de sauvegarde, 
	* et un bouton de fermeture de la sous-fenêtre.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "makePanel");

		trace_methods.beginningOfMethod();
		// On commence par positionner le gestionnaire de Layout
		getContentPane().setLayout(new BorderLayout());
		// On va construire la zone d'édition
		DefaultStyledDocument document = new DefaultStyledDocument();
		document.addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
				// On ne fait rien
			}
			
			public void insertUpdate(DocumentEvent e)
			{
				// On appelle la méthode setDocumentModified
				setDocumentModified(true);
			}
			
			public void removeUpdate(DocumentEvent e)
			{
				// On appelle la méthode setDocumentModified
				setDocumentModified(true);
			}
		});
		_editorPane = new UndoableTextPane(document);
		_editorPane.setCaretPosition(0);
		_editorPane.setFont(new SearchableTextArea().getFont());
		// On place la zone dans un scroll pane
		JScrollPane editor_scroll = new JScrollPane(_editorPane);
		editor_scroll.setBorder(BorderFactory.createEtchedBorder());
		// On l'ajoute à la fenêtre
		getContentPane().add(editor_scroll, BorderLayout.CENTER);
		
		// On va créer le panneau de boutons
		JPanel panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On crée le bouton "Recharger"
		JButton reload_button =
			new JButton(MessageManager.getMessage("&Edit_Reload"));
		// On ajoute le callback sur le bouton
		reload_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Thread processing_thread = new Thread(new Runnable()
				{
					public void run()
					{
						getMainWindowInterface().setStatus(
							"&Edit_LoadingFile", null, 0);
						loadFile();
						getMainWindowInterface().setStatus(null, null, 0);
					}
				});
				processing_thread.start();
			}
		});
		// On ajoute le bouton au panneau
		panel.add(reload_button);
		// On crée le bouton "Enregistrer"
		_saveButton =
			new JButton(MessageManager.getMessage("&Edit_Save"));
		// On ajoute le callback sur le bouton
		_saveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Thread processing_thread = new Thread(new Runnable()
				{
					public void run()
					{
						saveFile();
					}
				});
				processing_thread.start();
			}
		});
		// On ajoute le bouton au panneau
		panel.add(_saveButton);
		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Edit_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture
				close();
			}
		});
		// On ajoute le bouton
		panel.add(close_button);
		// On calcule la dimension de tous les boutons
		int button_width = Math.max(reload_button.getPreferredSize().width,
			Math.max(close_button.getPreferredSize().width,
			_saveButton.getPreferredSize().width));
		Dimension size = new Dimension(button_width,
			reload_button.getPreferredSize().height);
		reload_button.setPreferredSize(size);
		close_button.setPreferredSize(size);
		_saveButton.setPreferredSize(size);
		// On crée un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		layout.setConstraints(panel, constraints);
		button_panel.add(panel);
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		// Il ne reste plus qu'à fixer la dimension préférée
		// On positionne les dimensions de la fenêtre
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(new Dimension(450, 300));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: loadFile
	* 
	* Description:
	* Cette méthode est chargée du chargement, ou du rechargement, du fichier 
	* à éditer dans la zone d'édition.
	* Le chargement consiste à vider la zone d'édition, à récupérer une 
	* interface de lecture sur le fichier à éditer, à lire le contenu du 
	* fichier et mettre à jour la zone d'édition à partir de ce qui est lu.
	* Si une erreur survient lors de la lecture du fichier, un message est 
	* affiché à l'utilisateur l'informant de la nature de l'erreur.
	* 
	* Retourne: true si le chargement s'est bien passé, false sinon.
	* ----------------------------------------------------------*/
	private boolean loadFile()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "loadFile");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		ServiceSessionProxy  session_proxy = null;
		FileReaderInterface reader_interface = null;
		long file_size = 0;
		long size_read = 0;
		long buffer_size = 10240;
		boolean load_ok = false;
		InnerException inner_exception = null;
		Document document = null;
		DefaultEditorKit editor_kit = null;
		boolean display_error = true;
		String agent_layer_mode = null;

		trace_methods.beginningOfMethod();
		getMainWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, this);
		GenericTreeObjectNode selected_node = 
			(GenericTreeObjectNode)getSelectedNode();
		document = _editorPane.getDocument();
		editor_kit = (DefaultEditorKit)_editorPane.getEditorKit();
		_fileIsLoading = true;
		// On commence par vider la zone d'édition
		_editorPane.setText("");
		// On va logguer un message de lecture du fichier
		String[] message_data = new String[1];
		message_data[0] = MessageManager.getMessage("&LOG_ReadingFile");
		LogServiceProxy.addMessageForAction(getActionId(), message_data);
		// On va maintenant procéder à la récupération de l'interface
		// de lecture sur le fichier
		try
		{
			session_proxy = new ServiceSessionProxy(
				selected_node.getServiceSession());
			reader_interface = 
				session_proxy.getFileReader(getEditedFilePath());
			// On va récupérer la taille du fichier
			file_size = reader_interface.getFileSize();
			// Si le fichier n'est pas vide, on va le lire
			if(file_size != 0) {
				// On va ouvrir le fichier en lecture
				reader_interface.open();
				while(size_read < file_size)
				{
					// On règle la taille du buffer de lecture
					if((file_size - size_read) < buffer_size)
					{
						buffer_size = file_size - size_read;
					}
					// On va procéder à la lecture
					ShortBlock read_blocks[] = 
						reader_interface.readBlock(size_read, buffer_size);
					// On va "injecter" les blocks un par un dans le document
					for(int index = 0 ; index < read_blocks.length ; 
						index ++)
					{
						ByteArrayInputStream input_stream =
							new ByteArrayInputStream(read_blocks[index].data, 
							0, read_blocks[index].size);
						try
						{
							editor_kit.read(input_stream, document, 
								document.getLength());
						}
						catch(Exception exception)
						{
							trace_errors.writeTrace("Erreur lors de " +
								"l'insertion de données dans la zone " +
								"d'édition: " + exception);
						}
						size_read += read_blocks[index].size;
					}
				}
			}
			else {
				// Si le fichier est vide, on va positionner un séparateur 
				// de lignes dépendant de la couche d'exécution de l'Agent
				_editorPane.getDocument().putProperty(
					DefaultEditorKit.EndOfLineStringProperty, 
					AgentLayerAbstractor.getLineSeparator(
					AgentSessionManager.getInstance().getAgentLayerMode(
					selected_node.getAgentName())));
			}
			load_ok = true;
			_fileIsLoading = false;
			// On va enlever le marqueur de modification
			setDocumentModified(false);
		}
		catch(FileNotFoundException found_exception)
		{
			trace_errors.writeTrace("Le fichier " + getEditedFilePath() +
				" n'existe pas !");
			// On va demander à l'utilisateur s'il souhaite créer le fichier
			int reply = DialogManager.displayDialog("YesNoQuestion", 
				"&Edit_NoFileCreate", null, this);
			if(reply == JOptionPane.YES_OPTION)
			{ 
				load_ok = true;
				_fileIsLoading = false;
				_fileIsNew = true;
				// On va positionner le marqueur de modification
				setDocumentModified(true);
			}
			else
			{
				inner_exception = CommonFeatures.processException(
					"lors de la lecture du fichier", found_exception); 
				display_error = false;
			}
		}
		catch(FileNotReadableException read_exception)
		{
			trace_errors.writeTrace("Le fichier " + getEditedFilePath() + 
				" n'est pas lisible !");
			inner_exception = CommonFeatures.processException(
				"lors de lecture du fichier", read_exception);
		}
		catch(FileNotReachableException reach_exception)
		{
			trace_errors.writeTrace("Le fichier " + getEditedFilePath() +
				" n'est pas accessible !");
			inner_exception = CommonFeatures.processException(
				"lors de l'accès au fichier", reach_exception);
		}
		catch(UnknownException unknown_exception)
		{
			trace_errors.writeTrace(
				"Erreur inconnue lors du chargement du fichier " + 
				getEditedFilePath());
			inner_exception = CommonFeatures.processException(
				"lors de lecture du fichier", unknown_exception);
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors du chargement du fichier " +
				getEditedFilePath() + ": " + exception.getMessage());
			inner_exception = exception;
		}
		finally
		{
			// S'il y a une exception, on affiche un message à l'utilisateur
			if(inner_exception != null)
			{
				// On va logguer un message
				message_data = new String[2];
				message_data[0] = 
					MessageManager.getMessage("&LOG_FileReadingFailed");
				message_data[1] = inner_exception.getReason();
				LogServiceProxy.addMessageForAction(getActionId(), 
					message_data);
				// On affiche le message à l'utilisateur
				if(display_error == true)
				{
					DialogManager.displayDialogForException(
						"&ERR_CannotLoadFile", this, inner_exception);
				}
			}
		}
		if(reader_interface != null)
		{
			try
			{
				reader_interface.close();
			}
			catch(Throwable e)
			{
				// On ne fait rien
			}
			reader_interface = null;
		}
		_editorPane.setCaretPosition(0);
		if(load_ok == true)
		{
			// On va effacer toutes les éditions pouvant être
			// annulées
			_editorPane.discardAllEdits();
		}
		getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
		trace_methods.endOfMethod();
		return load_ok;
	}

	/*----------------------------------------------------------
	* Nom: saveFile
	* 
	* Description:
	* Cette méthode est chargée de l'enregistrement du fichier à éditer à 
	* partir du contenu de la zone d'édition.
	* La sauvegarde consiste à récupérer une interface d'écriture sur le 
	* fichier à éditer, et à en écrire le contenu à partir des données de la 
	* zone d'édition.
	* Si une erreur survient lors de l'écriture du fichier, un message est 
	* affiché à l'utilisateur l'informant de la nature de l'erreur.
	* 
	* Retourne: true si l'enregistrement s'est bien passé, false sinon.
	* ----------------------------------------------------------*/
	private boolean saveFile()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "saveFile");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		ServiceSessionProxy  session_proxy = null;
		FileWriterInterface writer_interface = null;
		int document_size = 0;
		int size_written = 0;
		int buffer_size = 10240;
		boolean save_ok = false;
		InnerException inner_exception = null;
		Document document = null;
		DefaultEditorKit editor_kit = null;

		trace_methods.beginningOfMethod();
		getMainWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, this);
		GenericTreeObjectNode selected_node = 
			(GenericTreeObjectNode)getSelectedNode();
		document = _editorPane.getDocument();
		editor_kit = (DefaultEditorKit)_editorPane.getEditorKit();
		// Si le fichier est nouveau, on va positionner un séparateur de lignes
		// dépendant de la couche d'exécution de l'Agent
		if(_fileIsNew == true)
		{
			String agent_layer_mode = 
				AgentSessionManager.getInstance().getAgentLayerMode(
				((GenericTreeObjectNode)getSelectedNode()).getAgentName());
			_editorPane.getDocument().putProperty(
				DefaultEditorKit.EndOfLineStringProperty, 
				AgentLayerAbstractor.getLineSeparator(agent_layer_mode));
		}
		// On va logguer un message d'écriture du fichier
		String[] message_data = new String[1];
		message_data[0] = MessageManager.getMessage("&LOG_WritingFile");
		LogServiceProxy.addMessageForAction(getActionId(), message_data);
		// On va maintenant procéder à la récupération de l'interface
		// d'écriture sur le fichier
		try
		{
			session_proxy = new ServiceSessionProxy(
				selected_node.getServiceSession());
			writer_interface = 
				session_proxy.getFileWriter(getEditedFilePath());
			// On va récupérer la taille du document
			document_size = document.getLength();
			// On va ouvrir le fichier en écriture
			writer_interface.open(false);
			while(size_written < document_size)
			{
				int number_of_blocks = 0;
				ShortBlock write_blocks[] = null;
				ByteArrayOutputStream output_stream = 
					new ByteArrayOutputStream();

				// On règle la taille du buffer de lecture
				if((document_size - size_written) < buffer_size)
				{
					buffer_size = document_size - size_written;
				}
				try
				{
					// On va procéder à la lecture depuis le document
					// Le nombre d'octets lus peut être différent
					editor_kit.write(output_stream, document, size_written, 
						buffer_size);
				}
				catch(Throwable e)
				{
					trace_errors.writeTrace("Erreur lors de la lecture " +
						"des données du document");
				}
				byte document_bytes[] = output_stream.toByteArray();
				// On va calculer le nombre de blocks nécessaires
				number_of_blocks = document_bytes.length / 1024;
				if((document_bytes.length % 1024) != 0)
				{
					number_of_blocks ++; 
				}
				write_blocks = new ShortBlock[number_of_blocks];
				// On va remplir les blocks un par un
				for(int index = 0 ; index < write_blocks.length ; index ++)
				{
					int block_size = 1024;
					ShortBlock block = new ShortBlock();
					
					// Le dernier block peut être plus petit 
					if(index == (write_blocks.length - 1))
					{
						block_size = document_bytes.length - (1024 * index);
					}
					block.data = new byte[1024];
					// On va copier les bytes de la chaîne dans
					// les données du block
					for(int loop = 0 ; loop < 1024 ; loop ++)
					{
						if(loop < block_size)
						{
							// On a un offset puisque l'on travaille sur un 
							// tableau d'au moins 10240 octets
							block.data[loop] = document_bytes[loop + 
								(1024 * index)];
						}
						else
						{
							block.data[loop] = 0;
						}
					}
					block.size = block_size;
					write_blocks[index] = block;
				}
				writer_interface.appendBlock(write_blocks);
				size_written += buffer_size;
			}
			save_ok = true;
		}
		catch(FileNotWritableException write_exception)
		{
			trace_errors.writeTrace("Le fichier " + getEditedFilePath() + 
				" n'est pas inscriptible !");
			inner_exception = CommonFeatures.processException(
				"lors de l'écriture du fichier", write_exception);
		}
		catch(FileNotReachableException reach_exception)
		{
			trace_errors.writeTrace("Le fichier " + getEditedFilePath() +
				" n'est pas accessible !");
			inner_exception = CommonFeatures.processException(
				"lors de l'accès au fichier", reach_exception);
		}
		catch(FileNotCreatableException create_exception)
		{
			trace_errors.writeTrace("Le fichier " + getEditedFilePath() +
				" ne peut être créé !");
			inner_exception = CommonFeatures.processException(
				"lors de la création du fichier", create_exception);
		}
		catch(UnknownException unknown_exception)
		{
			trace_errors.writeTrace(
				"Erreur inconnue lors de l'enregistrement du fichier " + 
				getEditedFilePath());
			inner_exception = CommonFeatures.processException(
				"lors de l'écriture du fichier", unknown_exception);
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors de l'enregistrement du " +
				"fichier " + getEditedFilePath() + ": " + 
				exception.getMessage());
			inner_exception = exception;
		}
		finally
		{
			// S'il y a une exception, on affiche un message à l'utilisateur
			if(inner_exception != null)
			{
				// On va logguer un message
				message_data = new String[2];
				message_data[0] = 
					MessageManager.getMessage("&LOG_FileWritingFailed");
				message_data[1] = inner_exception.getReason();
				LogServiceProxy.addMessageForAction(getActionId(), 
					message_data);
				// On affiche le message à l'utilisateur
				DialogManager.displayDialogForException("&ERR_CannotWriteFile",
					this, inner_exception);
			}
			else
			{
				// On va enlever le marqueur de modification
				setDocumentModified(false);
			}
		}
		if(writer_interface != null)
		{
			try
			{
				// On va positionner le caractère exécutable du fichier
				if(_fileIsNew == true && _setFileAsExecutable == true)
				{
					writer_interface.setExecutable(true);
				}
				writer_interface.close();
			}
			catch(Throwable e)
			{
				// On ne fait rien
				e.printStackTrace();
			}
			writer_interface = null;
		}
		getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
		trace_methods.endOfMethod();
		return save_ok;
	}

	/*----------------------------------------------------------
	* Nom: setDocumentModified
	* 
	* Description:
	* Cette méthode permet de définir si le document (le fichier à éditer) 
	* a été modifié ou non.
	* 
	* Arguments:
	*  - documentIsModified: Un booléen indiquant si le document a été modifié 
	*    (true) ou non (false).
 	* ----------------------------------------------------------*/
 	private void setDocumentModified(
 		boolean documentIsModified
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "setDocumentIsModified");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("documentIsModified=" + documentIsModified);
		if(_fileIsLoading == false)
		{
			_documentIsModified = documentIsModified;
			_saveButton.setEnabled(_documentIsModified);
		}
		trace_methods.endOfMethod();
 	}
}
