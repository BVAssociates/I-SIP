/*------------------------------------------------------------
* Copyright (c) 2008 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/download/DownloadProcessor.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de téléchargement de fichier
* DATE:        15/02/2008
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.download
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DownloadProcessor.java,v $
* Revision 1.3  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2008/02/21 12:08:06  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.1  2008/02/19 15:56:29  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.download;

//
//Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.abs.processor.DownloadProgressInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.LabelFactory;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.FileDownloader;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.AgentLayerAbstractor;
import com.bv.isis.corbacom.FileReaderInterface;
import com.bv.isis.corbacom.IsisParameter;


/*----------------------------------------------------------
* Nom: DownloadProcessor
* 
* Description:
* Cette classe est une classe technique chargée du téléchargement d'un 
* fichier depuis une machine distante vers le poste de travail de 
* l'utilisateur.
* Elle implémente l'interface ProcessorInterface afin de permettre son 
* invocation en tant que processeur de la Console.
* Elle implémente l'interface DownloadProgressInterface afin de suivre la 
* progression du téléchargement du fichier.
* ----------------------------------------------------------*/
public class DownloadProcessor
	implements
		ProcessorInterface,
		DownloadProgressInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DownloadProcessor
	* 
	* Description:
	* Il s'agit du constructeur par défaut. Il n'est présenté que pour des 
	* raisons de lisibilité.
	* ----------------------------------------------------------*/
	public DownloadProcessor() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DownloadProcessor", "DownloadProcessor");
		
		trace_methods.beginningOfMethod();
		_windowInterface = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour effectuer un pré-chargement du processeur.
	* Elle charge le fichier de messages du processeur.
	* ----------------------------------------------------------*/
	public void preLoad() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		MessageManager.loadFile("download.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée afin de savoir si le processeur est configuré.
	* Ce processeur ne nécessite aucune configuration.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isConfigured() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "isConfigured");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer les panneaux de configuration du processeur.
	* Pour ce processeur, aucune configuration n'est nécessaire.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "getConfigurationPanels");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* appelée afin de savoir si le processeur peut être invoqué depuis 
	* l'arbre d'exploration.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "isTreeCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* appelée afin de savoir si le processeur peut être invoqué depuis 
	* l'affichage sous forme de tableau.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTableCapable() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "isTableCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Descrition:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée afin de savoir si le processeur peut être invoqué depuis le 
	* menu "Outils" de la fenêtre principale.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "isGlobalCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer la description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&DownloadProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée afin de récupérer le libellé de l'entrée de menu, dans le 
	* cas d'un processeur global.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public String getMenuLabel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "getMenuLabel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée par le ProcessManager afin d'initialiser et de d'exécuter 
	* le processeur.
	* Une boite de sélection du répertoire de téléchargement est affichée à 
	* l'utilisateur, puis le fichier est téléchargé.
	* 
	* Si un problème est détecté durant la phase d'initialisation, l'exception 
	* InnerException doit être levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    permettant au processeur d'interagir avec la fenêtre principale,
	*  - menuItem: Une référence sur l'objet JMenuItem par lequel le processeur 
	*    a été exécuté. Cet argument peut être nul,
	*  - parameters: Une chaîne de caractère contenant des paramètres spécifiques 
	*    au processeur. Cet argument peut être nul,
	*  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	*  - selectedNode: Une référence sur le noeud sélectionné. Cet argument peut 
	*    être nul.
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
		) throws
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "DownloadProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int download_result = 0;
		String agent_layer_mode = null;
		String remote_file_path;
		String local_file_path;
		// La taille maximale du fichier à télécharger est de 1 Go
		long maximum_file_size = 1024 * 1024 * 1024;

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
		_windowInterface = windowInterface;
		// On cast le noeud en GenericTreeObjectNode
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		windowInterface.setProgressMaximum(103);
		windowInterface.setStatus("&Download_PreparingDownload", null, 0);
		// On va résoudre les éventuelles références aux pré-processing
		IndexedList context = selected_node.getContext(true);
		String agent_name = selected_node.getAgentName();
		agent_layer_mode = AgentSessionManager.getInstance().getAgentLayerMode(
			agent_name);
		IsisParameter[] preprocessing_parameters = 
			ProcessingHandler.handleProcessingStatement(preprocessing, 
			context, windowInterface, agent_layer_mode, 
			selected_node.getServiceSession(), (JFrame)windowInterface, true);
		remote_file_path = LabelFactory.evaluate(parameters, 
			preprocessing_parameters, context, agent_name);
		trace_debug.writeTrace("remote_file_path=" + remote_file_path);
		// On va extraire le nom du fichier du chemin du fichier distant
		local_file_path = AgentLayerAbstractor.getFileName(remote_file_path, 
			agent_layer_mode);
		trace_debug.writeTrace("local_file_path=" + local_file_path);
		// On va demander à l'utilisateur de sélectionner le fichier local
		windowInterface.setStatus("&Download_PreparingDownload", null, 1);
		JFileChooser file_chooser = new JFileChooser();
		file_chooser.setDialogTitle(MessageManager.getMessage(
			"&Download_DownloadFileTo"));
		file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		file_chooser.setSelectedFile(new File(local_file_path));
		int result = file_chooser.showSaveDialog((JFrame)windowInterface);
		// On vérifie que l'utilisateur a validé
		if(result != JFileChooser.APPROVE_OPTION) {
			// On sort
			trace_debug.writeTrace("L'utilisateur a annulé");
			trace_methods.endOfMethod();
			return;
		}
		// On vérifie si le fichier existe
		File selected_file = file_chooser.getSelectedFile();
		if(selected_file.exists() == true)
		{
			// On va afficher un message à l'utilisateur pour savoir s'il
			// veut écraser l'ancien fichier
			if(JOptionPane.showConfirmDialog((JFrame)windowInterface,
				MessageManager.getMessage("&FileExists"),
				MessageManager.getMessage("&Download_DownloadFileTo"),
				JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			{
				// L'utilisateur a annulé, on sort
				trace_debug.writeTrace("L'utilisateur a annulé !");
				trace_methods.endOfMethod();
				return; 
			}
		}
		local_file_path = selected_file.getAbsolutePath();
		String[] extra_info = { "0" };
		windowInterface.setStatus("&Download_Downloading", extra_info, 2);
		// On lance le téléchargement
		try {
			// On construit un ServiceSessionProxy
			ServiceSessionProxy proxy = 
				new ServiceSessionProxy(selected_node.getServiceSession());
			// On récupère l'interface de lecture du fichier
			FileReaderInterface reader = proxy.getFileReader(remote_file_path);
			// On lance le téléchargement du fichier
			download_result = FileDownloader.downloadFile(reader, 
				maximum_file_size, selected_file, this);
			extra_info[0] = "100";
			windowInterface.setStatus("&Download_Downloading", extra_info, 103);
			String message = "";
			extra_info = null;
			if(download_result < 0) {
				// Si le fichier n'a pas été téléchargé car il a une taille 
				// nulle, on affiche un message
				message = "&FileSizeIsNull";
			}
			else if(download_result > 0) {
				// Si le fichier n'a pas été téléchargé car il a une taille 
				// trop  grande, on affiche un message
				message = "&FileSizeTooLarge";
			}
			else {
				// Le fichier a été téléchargé, on affiche un message
				message = "&Download_Success";
				extra_info = new String[1];
				extra_info[0] = local_file_path;
			}
			windowInterface.showPopup("Information", message, extra_info);
		}
		catch(InnerException exception)
		{
			// On affiche un message d'erreur
			windowInterface.showPopupForException(
				"&ERR_FileDownloadNotPossible", exception);
		}
		catch(Exception exception)
		{
			InnerException inner_exception = CommonFeatures.processException(
				"de la création du fichier temporaire", exception);
			// On affiche un message d'erreur
			windowInterface.showPopupForException(
				"&ERR_FileDownloadNotPossible", inner_exception);
		}
		windowInterface.setStatus(null, null, 0);
		close();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée lorsque le processeur doit être fermé.
	* ----------------------------------------------------------*/
	public void close() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "close");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance du processeur.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new DownloadProcessor();
	}

	/*----------------------------------------------------------
	* Nom: setProgress
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* DownloadProgressInterface. Elle est appelée par la classe 
	* FileDownloader pour informer de la progression du téléchargement du 
	* fichier.
	* 
	* Arguments:
	*  - progress: Le taux de progression du téléchargement.
 	* ----------------------------------------------------------*/
	public void setProgress(
		int progress
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DownloadProcessor", "setProgress");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String[] extra_info = new String[1];

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("progress=" + progress);
		extra_info[0] = "" + progress;
		_windowInterface.setStatus("&Download_Downloading", extra_info, 2 + progress);
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _windowInterface
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet MainWindowInterface 
	* correspondant à la fenêtre principale de l'application.
 	* ----------------------------------------------------------*/
	private MainWindowInterface _windowInterface;
}
