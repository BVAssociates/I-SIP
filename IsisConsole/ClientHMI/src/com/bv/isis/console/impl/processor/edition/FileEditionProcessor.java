/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/edition/FileEditionProcessor.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'�dition de fichiers
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
* Classe PreprocessingHandler renomm�e.
* Ajout de l'argument postprocessing � la m�thode run().
* Caract�re de retour chariot forc� pour les fichiers vides.
*
* Revision 1.2  2006/11/09 12:12:49  tz
* Ajout de la m�thode getTitlePrefix().
* Modification des m�thodes loadFile() et saveFile() pour une meilleure gestion
* des retours chariot.
*
* Revision 1.1  2006/11/03 10:30:40  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.edition;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur d'�dition d'un fichier. Ce processeur 
* est charg� de t�l�charger le contenu d'un fichier sp�cifi�, d'afficher 
* celui-ci dans une zone d'�dition, puis de r�-�crire le contenu du fichier 
* avec les donn�es de la zone d'�dition.
* Il red�finit la classe ProcessorFrame car il s'agit d'un processeur 
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
	* Il s'agit du constructeur par d�faut. Il n'est pr�sent� que pour des 
	* raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e afin de permettre au processeur d'effectuer un pr�-
	* chargement de donn�es.
	* Dans le cas pr�sent, le fichier de messages est charg�.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorInterface. 
	* Elle est appel�e lorsque le processeur doit d�marrer son ex�cution, ou 
	* plut�t son traitement.
	* Dans le cas pr�sent, la m�thode effectue la construction de la sous-
	* fen�tre via la m�thode makePanel(), r�soud la r�f�rence au fichier dont 
	* le nom doit �tre pass� en argument via la m�thode 
	* resolveFileReference(), puis effectue un premier chargement du contenu 
	* du fichier via la m�thode loadFile().
	* 
	* Si une erreur survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowsInterface 
	*    repr�sentant la fen�tre principale de la Console,
	*  - menuItem: Une r�f�rence sur un objet JMenuItem correspondant � 
	*    l'�l�ment de menu ayant d�clench� l'ex�cution de ce processeur,
	*  - parameters: Les arguments �ventuels d'ex�cution du processeur,
	*  - preprocessing: Les informations �ventuelles de pr�-processing,
	*  - postprocessing: Les informations �ventuelles de post-processing,
	*  - selectedNode: Une r�f�rence �ventuelle sur un objet 
	*    DefaultMutableTreeNode correspondant au noeud s�lectionn�.
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
		// Tout d'abord, il faut v�rifier l'int�grit� des arguments
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
		// On appelle la m�thode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		windowInterface.setCurrentCursor(Cursor.WAIT_CURSOR, this);
		windowInterface.setProgressMaximum(3);
		// On caste le noeud en GenericTreeObjectNode
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		IndexedList context = selected_node.getContext(true);
		String agent_name = selected_node.getAgentName();
		// On va g�rer le pr�-processing
		IsisParameter[] preprocessing_parameters = 
			ProcessingHandler.handleProcessingStatement(preprocessing, 
			context, windowInterface, 
			AgentSessionManager.getInstance().getAgentLayerMode(agent_name), 
			selected_node.getServiceSession(), this, true);
		// On va r�soudre la r�f�rence au fichier
		windowInterface.setStatus("&Edit_ManagingFileReference", null, 0);
		_editedFilePath = manageFileReference(
			LabelFactory.evaluate(parameters, preprocessing_parameters,
			context, agent_name));
		trace_debug.writeTrace("_editedFilePath=" + _editedFilePath);
		// On va r�cup�rer le num�ro de l'action d'�dition
		String user_name = PasswordManager.getInstance().getUserName();
		String message = MessageManager.getMessage("&LOG_FileEdition");
		_actionId = 
				LogServiceProxy.getActionIdentifier(selected_node.getAgentName(),
				message, user_name, selected_node.getServiceName(),
				selected_node.getServiceType(), selected_node.getIClesName());
		// On va loguer le message d'�dition du fichier
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
		// On va construire la sous-fen�tre
		windowInterface.setStatus("&Edit_BuildingEditorPane", null, 1);
		makePanel();
		setDocumentModified(false);
		// On affiche la fen�tre
		display();
		// On charge le fichier
		windowInterface.setStatus("&Edit_LoadingFile", null, 2);
		windowInterface.setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
		boolean load_ok = loadFile();
		windowInterface.setStatus(null, null, 0);
		// Si le chargement s'est mal pass�, en sort
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
	* Cette m�thode permet de r�cup�rer le d�but de la cha�ne composant le 
	* titre de la sous-fen�tre.
	* Dans le cas pr�sent, elle retourne "Edition de fichier:".
	* 
	* Retourne: Le d�but de la cha�ne de titre.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. 
	* Elle est appel�e afin de r�cup�rer une description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* permet de retourner un objet �tant un double de l'objet courant.
	* 
	* Retourne: Une r�f�rence sur un nouvel objet FileEditionProcessor.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e lorsque la sous-fen�tre doit �tre ferm�e.
	* Elle lib�re toutes les ressources allou�es par l'objet.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "close");

		trace_methods.beginningOfMethod();
		// On v�rifie si le fichier a �t� modifi�
		if(_documentIsModified == true)
		{
			// On va demander � l'utilisateur s'il veut l'enregistrer
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
		// On ferme la fen�tre en appelant la m�thode de la super-classe.
		super.close();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getEditedFilePath
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le chemin du fichier � �diter.
	* 
	* Retourne: Le chemin du fichier � �diter.
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
	* Cette m�thode permet de r�cup�rer le num�ro d'action unique associ� � 
	* l'action d'�dition de fichier actuelle.
	* 
	* Retourne: Le num�ro d'action unique.
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
	* Cette m�thode permet de savoir si le document (le fichier � �diter) a 
	* �t� modifi�, ou non.
	* 
	* Retourne: true si le document a �t� modifi�, false sinon.
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
	* Cette m�thode est charg�e de g�rer la r�f�rence au fichier devant �tre 
	* �dit�, celle-ci �tant pass�e en param�tre.
	* Dans le cas pr�sent, aucune gestion n'est n�cessaire.
	* 
	* Si une erreur survient lors de la gestion de la r�f�rence, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - fileReference: La r�f�rence au fichier devant �tre �dit�.
	* 
	* Retourne: La r�f�rence au fichier devant �tre �dit�.
	* 
	* L�ve: InnerException.
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
		// On v�rifie que la r�f�rence est valide, c-a-d qu'elle existe
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
	* Cette m�thode permet de fixer le caract�re ex�cutable qui doit �tre 
	* donn� au fichier lors de sa cr�ation.
	* 
	* Arguments:
	*  - setFileAsExecutable: Un bool�en indiquant si le fichier doit �tre 
	*    positionn� en tant qu'ex�cutable (true) ou non (false).
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
	* Cet attribut maintient le num�ro d'action correspondant � l'action 
	* d'�dition du fichier.
	* ----------------------------------------------------------*/
	private String _actionId;

	/*----------------------------------------------------------
	* Nom: _editedFilePath
	* 
	* Description:
	* Cet attribut maintient une cha�ne contenant le chemin du fichier devant 
	* �tre �dit�.
	* ----------------------------------------------------------*/
	private String _editedFilePath;

	/*----------------------------------------------------------
	* Nom: _editorPane
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet UndoableTextPane 
	* correspondant � la zone d'�dition du fichier.
	* ----------------------------------------------------------*/
	private UndoableTextPane _editorPane;

	/*----------------------------------------------------------
	* Nom: _documentIsModified
	* 
	* Description:
	* Cet attribut maintient un bool�en indiquant si le document (le fichier 
	* � �diter) a �t� modifi�, ou non.
	* ----------------------------------------------------------*/
	private boolean _documentIsModified;

	/*----------------------------------------------------------
	* Nom: _fileIsLoading
	* 
	* Description:
	* Cet attribut maintient un bool�en indiquant si le fichier est en cours 
	* de chargement, ou non.
	* ----------------------------------------------------------*/
	private boolean _fileIsLoading;

	/*----------------------------------------------------------
	* Nom: _saveButton
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JButton correspondant 
	* au bouton "Enregistrer".
	* ----------------------------------------------------------*/
	private JButton _saveButton;

	/*----------------------------------------------------------
	* Nom: setFileAsExecutable
	* 
	* Description:
	* Cet attribut maitient un bool�en indiquant si le fichier doit �tre 
	* positionn� en tant qu'ex�cutable lors de sa cr�ation (true) ou non 
	* (false).
	* ----------------------------------------------------------*/
	private boolean _setFileAsExecutable;

	/*----------------------------------------------------------
	* Nom: _fileIsNew
	* 
	* Description:
	* Cet attribut maintient un bool�en indiquant si le fichier va �tre cr�� 
	* (true) ou non (false).
	* ----------------------------------------------------------*/
	private boolean _fileIsNew;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette m�thode est charg�e de la construction de la sous-fen�tre 
	* d'�dition de fichier.
	* Cette sous-fen�tre est constitu�e d'une zone d'�dition, correspondant 
	* � une instance de SearchableTextPane, ainsi qu'� une zone de boutons 
	* contenant un bouton de rechargement de fichier, un bouton de sauvegarde, 
	* et un bouton de fermeture de la sous-fen�tre.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileEditionProcessor", "makePanel");

		trace_methods.beginningOfMethod();
		// On commence par positionner le gestionnaire de Layout
		getContentPane().setLayout(new BorderLayout());
		// On va construire la zone d'�dition
		DefaultStyledDocument document = new DefaultStyledDocument();
		document.addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent e)
			{
				// On ne fait rien
			}
			
			public void insertUpdate(DocumentEvent e)
			{
				// On appelle la m�thode setDocumentModified
				setDocumentModified(true);
			}
			
			public void removeUpdate(DocumentEvent e)
			{
				// On appelle la m�thode setDocumentModified
				setDocumentModified(true);
			}
		});
		_editorPane = new UndoableTextPane(document);
		_editorPane.setCaretPosition(0);
		_editorPane.setFont(new SearchableTextArea().getFont());
		// On place la zone dans un scroll pane
		JScrollPane editor_scroll = new JScrollPane(_editorPane);
		editor_scroll.setBorder(BorderFactory.createEtchedBorder());
		// On l'ajoute � la fen�tre
		getContentPane().add(editor_scroll, BorderLayout.CENTER);
		
		// On va cr�er le panneau de boutons
		JPanel panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On cr�e le bouton "Recharger"
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
		// On cr�e le bouton "Enregistrer"
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
		// Maintenant, on va cr�er le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Edit_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode de fermeture
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
		// On cr�e un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		layout.setConstraints(panel, constraints);
		button_panel.add(panel);
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		// Il ne reste plus qu'� fixer la dimension pr�f�r�e
		// On positionne les dimensions de la fen�tre
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(new Dimension(450, 300));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: loadFile
	* 
	* Description:
	* Cette m�thode est charg�e du chargement, ou du rechargement, du fichier 
	* � �diter dans la zone d'�dition.
	* Le chargement consiste � vider la zone d'�dition, � r�cup�rer une 
	* interface de lecture sur le fichier � �diter, � lire le contenu du 
	* fichier et mettre � jour la zone d'�dition � partir de ce qui est lu.
	* Si une erreur survient lors de la lecture du fichier, un message est 
	* affich� � l'utilisateur l'informant de la nature de l'erreur.
	* 
	* Retourne: true si le chargement s'est bien pass�, false sinon.
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
		// On commence par vider la zone d'�dition
		_editorPane.setText("");
		// On va logguer un message de lecture du fichier
		String[] message_data = new String[1];
		message_data[0] = MessageManager.getMessage("&LOG_ReadingFile");
		LogServiceProxy.addMessageForAction(getActionId(), message_data);
		// On va maintenant proc�der � la r�cup�ration de l'interface
		// de lecture sur le fichier
		try
		{
			session_proxy = new ServiceSessionProxy(
				selected_node.getServiceSession());
			reader_interface = 
				session_proxy.getFileReader(getEditedFilePath());
			// On va r�cup�rer la taille du fichier
			file_size = reader_interface.getFileSize();
			// Si le fichier n'est pas vide, on va le lire
			if(file_size != 0) {
				// On va ouvrir le fichier en lecture
				reader_interface.open();
				while(size_read < file_size)
				{
					// On r�gle la taille du buffer de lecture
					if((file_size - size_read) < buffer_size)
					{
						buffer_size = file_size - size_read;
					}
					// On va proc�der � la lecture
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
								"l'insertion de donn�es dans la zone " +
								"d'�dition: " + exception);
						}
						size_read += read_blocks[index].size;
					}
				}
			}
			else {
				// Si le fichier est vide, on va positionner un s�parateur 
				// de lignes d�pendant de la couche d'ex�cution de l'Agent
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
			// On va demander � l'utilisateur s'il souhaite cr�er le fichier
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
				"lors de l'acc�s au fichier", reach_exception);
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
			// S'il y a une exception, on affiche un message � l'utilisateur
			if(inner_exception != null)
			{
				// On va logguer un message
				message_data = new String[2];
				message_data[0] = 
					MessageManager.getMessage("&LOG_FileReadingFailed");
				message_data[1] = inner_exception.getReason();
				LogServiceProxy.addMessageForAction(getActionId(), 
					message_data);
				// On affiche le message � l'utilisateur
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
			// On va effacer toutes les �ditions pouvant �tre
			// annul�es
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
	* Cette m�thode est charg�e de l'enregistrement du fichier � �diter � 
	* partir du contenu de la zone d'�dition.
	* La sauvegarde consiste � r�cup�rer une interface d'�criture sur le 
	* fichier � �diter, et � en �crire le contenu � partir des donn�es de la 
	* zone d'�dition.
	* Si une erreur survient lors de l'�criture du fichier, un message est 
	* affich� � l'utilisateur l'informant de la nature de l'erreur.
	* 
	* Retourne: true si l'enregistrement s'est bien pass�, false sinon.
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
		// Si le fichier est nouveau, on va positionner un s�parateur de lignes
		// d�pendant de la couche d'ex�cution de l'Agent
		if(_fileIsNew == true)
		{
			String agent_layer_mode = 
				AgentSessionManager.getInstance().getAgentLayerMode(
				((GenericTreeObjectNode)getSelectedNode()).getAgentName());
			_editorPane.getDocument().putProperty(
				DefaultEditorKit.EndOfLineStringProperty, 
				AgentLayerAbstractor.getLineSeparator(agent_layer_mode));
		}
		// On va logguer un message d'�criture du fichier
		String[] message_data = new String[1];
		message_data[0] = MessageManager.getMessage("&LOG_WritingFile");
		LogServiceProxy.addMessageForAction(getActionId(), message_data);
		// On va maintenant proc�der � la r�cup�ration de l'interface
		// d'�criture sur le fichier
		try
		{
			session_proxy = new ServiceSessionProxy(
				selected_node.getServiceSession());
			writer_interface = 
				session_proxy.getFileWriter(getEditedFilePath());
			// On va r�cup�rer la taille du document
			document_size = document.getLength();
			// On va ouvrir le fichier en �criture
			writer_interface.open(false);
			while(size_written < document_size)
			{
				int number_of_blocks = 0;
				ShortBlock write_blocks[] = null;
				ByteArrayOutputStream output_stream = 
					new ByteArrayOutputStream();

				// On r�gle la taille du buffer de lecture
				if((document_size - size_written) < buffer_size)
				{
					buffer_size = document_size - size_written;
				}
				try
				{
					// On va proc�der � la lecture depuis le document
					// Le nombre d'octets lus peut �tre diff�rent
					editor_kit.write(output_stream, document, size_written, 
						buffer_size);
				}
				catch(Throwable e)
				{
					trace_errors.writeTrace("Erreur lors de la lecture " +
						"des donn�es du document");
				}
				byte document_bytes[] = output_stream.toByteArray();
				// On va calculer le nombre de blocks n�cessaires
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
					
					// Le dernier block peut �tre plus petit 
					if(index == (write_blocks.length - 1))
					{
						block_size = document_bytes.length - (1024 * index);
					}
					block.data = new byte[1024];
					// On va copier les bytes de la cha�ne dans
					// les donn�es du block
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
				"lors de l'�criture du fichier", write_exception);
		}
		catch(FileNotReachableException reach_exception)
		{
			trace_errors.writeTrace("Le fichier " + getEditedFilePath() +
				" n'est pas accessible !");
			inner_exception = CommonFeatures.processException(
				"lors de l'acc�s au fichier", reach_exception);
		}
		catch(FileNotCreatableException create_exception)
		{
			trace_errors.writeTrace("Le fichier " + getEditedFilePath() +
				" ne peut �tre cr�� !");
			inner_exception = CommonFeatures.processException(
				"lors de la cr�ation du fichier", create_exception);
		}
		catch(UnknownException unknown_exception)
		{
			trace_errors.writeTrace(
				"Erreur inconnue lors de l'enregistrement du fichier " + 
				getEditedFilePath());
			inner_exception = CommonFeatures.processException(
				"lors de l'�criture du fichier", unknown_exception);
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
			// S'il y a une exception, on affiche un message � l'utilisateur
			if(inner_exception != null)
			{
				// On va logguer un message
				message_data = new String[2];
				message_data[0] = 
					MessageManager.getMessage("&LOG_FileWritingFailed");
				message_data[1] = inner_exception.getReason();
				LogServiceProxy.addMessageForAction(getActionId(), 
					message_data);
				// On affiche le message � l'utilisateur
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
				// On va positionner le caract�re ex�cutable du fichier
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
	* Cette m�thode permet de d�finir si le document (le fichier � �diter) 
	* a �t� modifi� ou non.
	* 
	* Arguments:
	*  - documentIsModified: Un bool�en indiquant si le document a �t� modifi� 
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
