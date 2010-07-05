/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/file/FileProcessor.java,v $
* $Revision: 1.16 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage de fichier
* DATE:        06/03/2003
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.file
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: FileProcessor.java,v $
* Revision 1.16  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.15  2008/02/21 12:08:47  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.14  2008/02/19 15:57:16  tz
* Appel du processeur de t�l�chargement si le fichier est trop gros.
*
* Revision 1.13  2008/01/31 16:56:59  tz
* Classe PreprocessingHandler renomm�e.
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.12  2007/12/07 10:33:11  tz
* Ajout de la gestion du pr�-processing.
*
* Revision 1.11  2005/12/23 13:22:02  tz
* Correction mineure.
*
* Revision 1.10  2005/10/07 08:26:28  tz
* Limitation de la taille du fichier t�l�charg� (20 Mo).
* Visualisation interne uniquement pour les fichiers <1 Mo.
* Modification de la m�thode de chargement dans la zone d'affichage.
*
* Revision 1.9  2005/07/06 10:05:22  tz
* Gestion du cas o� le fichier est vide.
*
* Revision 1.8  2005/07/05 15:09:53  tz
* Refonte compl�te du processeur.
*
* Revision 1.7  2005/07/01 12:13:32  tz
* Modification du composant pour les traces
*
* Revision 1.6  2004/11/23 15:40:24  tz
* Utilisation d'un buffer de 10 ko pour les communications.
*
* Revision 1.5  2004/10/22 15:38:08  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.4  2004/10/13 13:55:48  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.3  2004/10/06 07:31:02  tz
* Mise � jour par rapport � ExecutionProcessor.
*
* Revision 1.2  2004/07/29 12:05:01  tz
* Suppression d'imports inutiles
*
* Revision 1.1  2003/03/07 16:20:57  tz
* Ajout du processeur d'affichage de fichier
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.file;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.prefs.PreferencesAPI;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultStyledDocument;
import java.io.FileReader;

//
// Imports du projet
//
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.abs.processor.DownloadProgressInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.LabelFactory;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.FileDownloader;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.gui.NonEditableTextPane;
import com.bv.isis.console.core.gui.NonEditableTextArea;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.console.processor.ProcessorManager;
import com.bv.isis.corbacom.FileReaderInterface;
import com.bv.isis.corbacom.IsisParameter;

/*----------------------------------------------------------
* Nom: FileProcessor
* 
* Description:
* Cette classe impl�mente le processeur de t�che charg� de l'affichage d'un 
* fichier d'un agent. Elle d�rive de la classe ProcessorFrame. Cette classe va 
* utiliser la fonctionnalit� de t�l�chargement de fichier, gr�ce � la classe 
* FileDownloader du package download.
* Pour suivre le t�l�chargement, la classe impl�mente l'interface 
* DownloadProgressInterface.
* ----------------------------------------------------------*/
public class FileProcessor
	extends ProcessorFrame
	implements DownloadProgressInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: FileProcessor
	*
	* Description:
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
	* ----------------------------------------------------------*/
	public FileProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "FileProcessor", "FileProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette m�thode red�finit celle de la classe ProcessorFrame. Elle est 
	* appel�e par le ProcessManager afin d'initialiser et de d'ex�cuter le 
	* processeur.
	* Le fichier � afficher est t�l�charg� vers un fichier local temporaire, 
	* gr�ce � la classe FileDownloader, puis la fen�tre est construite, via la 
	* m�thode makePanel(), et affich�e.
	*
	* Si un probl�me est d�tect� durant la phase d'initialisation, l'exception
	* InnerException doit �tre lev�e.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface
	*    permettant au processeur d'interagir avec la fen�tre principale,
	*  - menuItem: Une r�f�rence sur l'objet JMenuItem par lequel le processeur
	*    a �t� ex�cut�. Cet argument peut �tre nul,
	*  - parameters: Une cha�ne de caract�re contenant des param�tres
	*    sp�cifiques au processeur. Cet argument peut �tre nul,
	*  - preprocessing: Une cha�ne contenant des instructions de pr�processing,
	*  - postprocessing: Une cha�ne contenant des instructions de postprocessing,
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn�. Cet argument
	*    peut �tre nul.
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
		    "FileProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String temporary_directory = null;
		int download_result = 0;
		// La taille maximale du fichier � afficher est de 1 Mo
		long maximum_file_size = 1024 * 1024;

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
		// On cast le noeud en GenericTreeObjectNode
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		// On appelle la m�thode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On positionne le titre de la fen�tre (� partir de l'item de menu)
		setTitle(menuItem.getText());
		windowInterface.setProgressMaximum(102);
		try
		{
			// On va r�cup�rer le r�pertoire temporaire depuis la configuration
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			temporary_directory =
				configuration_api.getString("Console", "TemporaryDirectory");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la r�cup�ration de la " +
				"configuration: " + exception);
			// On continue quand m�me
		}
		// On va r�soudre les �ventuelles r�f�rences aux pr�-processing
		IndexedList context = selected_node.getContext(true);
		String agent_name = selected_node.getAgentName();
		IsisParameter[] preprocessing_parameters = 
			ProcessingHandler.handleProcessingStatement(preprocessing, 
			context, windowInterface, 
			AgentSessionManager.getInstance().getAgentLayerMode(agent_name), 
			selected_node.getServiceSession(), this, true);
		parameters = LabelFactory.evaluate(parameters, preprocessing_parameters,
			context, agent_name);
		// On commence par r�cup�rer un num�ro d'action
		String message =
			MessageManager.getMessage("&LOG_FileDisplay");
		String action_id = 
			LogServiceProxy.getActionIdentifier(selected_node.getAgentName(),
			message, PasswordManager.getInstance().getUserName(),
			selected_node.getServiceName(), selected_node.getServiceType(),
			selected_node.getIClesName());
		// On va loguer le message d'affichage du fichier
		String[] message_data = new String[3];
		message_data[0] = message;
		message_data[1] = MessageManager.getMessage("&LOG_MenuItem") +
			menuItem.getText();
		message_data[2] = MessageManager.getMessage("&LOG_FileName") +
			parameters;
		LogServiceProxy.addMessageForAction(action_id, message_data);
		// On va r�cup�rer le r�pertoire d'accueil de l'utilisateur
		temporary_directory = System.getProperty("user.home");
		try
		{
			// On va d�finir le nom du fichier temporaire
			_temporaryFile = File.createTempFile("fptf", ".txt",
				new File(temporary_directory));
			trace_debug.writeTrace("_temporaryFile=" + _temporaryFile);
			// On construit un ServiceSessionProxy
			ServiceSessionProxy proxy = 
				new ServiceSessionProxy(selected_node.getServiceSession());
			// On r�cup�re l'interface de lecture du fichier
			FileReaderInterface reader = proxy.getFileReader(parameters);
			// Si le fichier est trop gros, on propose un t�l�chargement
			if(reader.getFileSize() > maximum_file_size) {
				if(JOptionPane.showConfirmDialog((JFrame)windowInterface,
					MessageManager.getMessage("&FileTooLargeDownload"),
					MessageManager.getMessage("&LOG_FileDisplay"),
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					// On va appeler le processeur de t�l�chargement
					ProcessorManager.executeProcessor("DownloadFile", 
						windowInterface, menuItem, parameters, preprocessing, 
						postprocessing, selectedNode, false, false);
				}
				// On sort
				close();
				trace_methods.endOfMethod();
				return;
			}
			// On lance le t�l�chargement du fichier
			download_result = FileDownloader.downloadFile(reader, 
				maximum_file_size, _temporaryFile, this);
			// On va logguer un message de succ�s
			message_data = new String[2];
			message_data[0] = 
				MessageManager.getMessage("&LOG_FileDisplayResult");
			message_data[1] = 
				MessageManager.getMessage("&LOG_DisplaySuccessful");
			LogServiceProxy.addMessageForAction(action_id, message_data);
			// Si le fichier n'a pas �t� t�l�charg� car il a une taille nulle, on
			// affiche un message
			if(download_result < 0)
			{
				windowInterface.showPopup("Information", "&FileSizeIsNull", 
					null);
				windowInterface.setStatus(null, null, 0);
				// On peut sortir
				_temporaryFile = null;
				close();
				trace_methods.endOfMethod();
				return;
			}
			// Si le fichier n'a pas �t� t�l�charg� car il a une taille trop 
			// grande, on affiche un message
			if(download_result > 0)
			{
				windowInterface.showPopup("Information", "&FileSizeTooLarge", 
					null);
				windowInterface.setStatus(null, null, 0);
				// On peut sortir
				close();
				trace_methods.endOfMethod();
				return;
			}
			// Si le fichier fait plus de 1 Mo, on affiche une boite de dialogue
			// indiquant � l'utilisateur d'utiliser un programme extern
			if(_temporaryFile.length() > 1024 * 1024)
			{
				String[] extra_information = { _temporaryFile.getAbsolutePath() };
				windowInterface.showPopup("Information", "&FileUseExternalViewer", 
					extra_information);
				windowInterface.setStatus(null, null, 0);
				// On peut sortir
				close();
				trace_methods.endOfMethod();
				return;
			}
			getMainWindowInterface().setStatus("&FileProcessorBuilding", null,
				101);
			// On construit le panneau de la fen�tre
			makePanel();
			getMainWindowInterface().setStatus("&FileProcessorBuilding", null,
				102);
			// On remplit la zone avec le contenu du fichier
			FileReader file_reader = 
				new FileReader(_temporaryFile);
			// On va lire par blocs de 10 Ko
			char[] buffer = new char[10 * 1024];
			while(true)
			{
				int n_read = file_reader.read(buffer);
				if(n_read <= 0)
				{
					break;
				}
				String text = new String(buffer, 0, n_read);
				_textArea.getDocument().insertString(
					_textArea.getDocument().getLength(), text, null);
			}
			file_reader.close();			
		}
		catch(InnerException exception)
		{
			// On va logguer un message d'erreur
			message_data = new String[3];
			message_data[0] = 
				MessageManager.getMessage("&LOG_FileDisplayResult");
			message_data[1] = MessageManager.getMessage("&LOG_DisplayFailed");
			message_data[2] = exception.getMessage();
			LogServiceProxy.addMessageForAction(action_id, message_data);
			// On affiche un message d'erreur
			windowInterface.showPopupForException("&ERR_FileDisplayNotPossible",
				exception);
			windowInterface.setStatus(null, null, 0);
			// On sort
			close();
			trace_methods.endOfMethod();
			return;
		}
		catch(Exception exception)
		{
			InnerException inner_exception = CommonFeatures.processException(
				"de la cr�ation du fichier temporaire", exception);
			// On va logguer un message d'erreur
			message_data = new String[3];
			message_data[0] = 
				MessageManager.getMessage("&LOG_FileDisplayResult");
			message_data[1] = MessageManager.getMessage("&LOG_DisplayFailed");
			message_data[2] = exception.getMessage();
			LogServiceProxy.addMessageForAction(action_id, message_data);
			// On affiche un message d'erreur
			windowInterface.showPopupForException("&ERR_FileDisplayNotPossible",
				inner_exception);
			windowInterface.setStatus(null, null, 0);
			// On sort
			close();
			trace_methods.endOfMethod();
			return;
		}
		// On positionne les dimensions de la fen�tre
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(new Dimension(450, 300));
		// On affiche la fen�tre
		display();
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette m�thode r�d�finit celle de la super classe. Elle est appel�e 
	* lorsque la sous-fen�tre du processeur doit �tre ferm�e (par 
	* l'utilisateur ou par la fermeture de l'application).
	* Le fichier temporaire cr�� lors du t�l�chargement du fichier distant 
	* est supprim�.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "FileProcessor", "close");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// On vide la zone d'affichage
		if(_textArea != null)
		{
			_textArea.setText("");
			_textArea = null;
		}
		// Si le fichier temporaire existe, on le d�truit
		if(_temporaryFile != null)
		{
			try
			{
				if(_temporaryFile.delete() == false)
				{
					trace_errors.writeTrace("Fichier temporaire non " +
						"supprim� !"); 
				}
				trace_debug.writeTrace("Fichier temporaire supprim�");
			}
			catch(Exception e)
			{
				trace_errors.writeTrace("Erreur lors de la suppression du" +
					" fichier temporaire: " + e.getMessage());
				// On ne fait rien
			}
			_temporaryFile = null;
		}
		// Appel � la m�thode de la super-classe
		super.close();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour effectuer un pr�-chargement du processeur.
	* Elle charge le fichier de messages du processeur.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("file.mdb", "UTF8");
		trace_methods.endOfMethod();
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
			"FileProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&FileProcessorDescription");
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
			"FileProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new FileProcessor();
	}

	/*----------------------------------------------------------
	* Nom: setProgress
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface DownloadProgressInterface. 
	* Elle est appel�e par la classe FileDownloader pour informer de la 
	* progression du t�l�chargement du fichier.
	* 
	* Arguments:
	*  - progress: Le taux de progression du t�l�chargement.
 	* ----------------------------------------------------------*/
	public void setProgress(
		int progress
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileProcessor", "setProgress");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("progress=" + progress);
		String[] extra_info = { "" + progress };
		getMainWindowInterface().setStatus("&FileDownloadInProgress",
			extra_info, progress);
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _temporaryFile
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet File correspondant 
	* au fichier temporaire contenant les donn�es du fichier distant.
	* ----------------------------------------------------------*/
	private File _temporaryFile;

	/*----------------------------------------------------------
	* Nom: _textArea
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet NonEditableTextPane 
	* correspondant � la zone o� est affich� le contenu du fichier.
	* ----------------------------------------------------------*/
	private NonEditableTextPane _textArea;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette m�thode construit le panneau de la sous-fen�tre en construisant 
	* la zone destin�e � l'affichage du contenu du fichier.
	* La zone d'affichage est remplie � partir du contenu du fichier 
	* repr�sent� par l'attribut _temporaryFile.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileProcessor", "makePanel");
		int background = Color.white.getRGB();
		int foreground = Color.black.getRGB();
		Color foreground_color = null;
		Color background_color = null;

		trace_methods.beginningOfMethod();
		// Le layout manager du panneau est un BorderLayout
		getContentPane().setLayout(new BorderLayout());
		// On r�cup�re les donn�es de configuration
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("Execution");
			background = preferences.getInt("Background");
			foreground = preferences.getInt("Output");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		background_color = new Color(background);
		// La couleur d'avant plan est l'inverse de la couleur de fond
		foreground_color = new Color(foreground);
		// On construit la zone d'affichage du fichier
		_textArea = new NonEditableTextPane(new DefaultStyledDocument());
		_textArea.setCaretPosition(0);
		_textArea.setFont((new NonEditableTextArea()).getFont());
		_textArea.setContentType("text/plain");
		// On fixe les couleurs
		_textArea.setBackground(background_color);
		_textArea.setForeground(foreground_color);
		_textArea.setSelectedTextColor(background_color);
		_textArea.setSelectionColor(foreground_color);
		// On place la zone dans un scroll pane
		JScrollPane output_scroll = new JScrollPane(_textArea);
		getContentPane().add(output_scroll, BorderLayout.CENTER);
		// Maintenant, on va cr�er le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Events_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode de fermeture
				close();
			}
		});
		// On cr�e un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		layout.setConstraints(close_button, constraints);
		button_panel.add(close_button);
		// On place ce panneau dans la zone sud
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		// On redimensionne la fen�tre
		setSize(400, 400);
		trace_methods.endOfMethod();
	}
}