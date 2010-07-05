/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/execution/ExecutionProcessor.java,v $
* $Revision: 1.23 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'ex�cution de proc�dure
* DATE:        24/01/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.execution
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ExecutionProcessor.java,v $
* Revision 1.23  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.22  2008/02/21 12:08:34  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.21  2008/01/31 16:56:30  tz
* Classe PreprocessingHandler renomm�e.
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.20  2008/01/29 15:53:02  tz
* Correction de l'appel � la m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.19  2006/11/09 12:10:20  tz
* Adaptation � la nouvelle m�thode
* PreprocessingHandler.handlePreprocessing().
*
* Revision 1.18  2006/08/11 13:34:07  tz
* Affichage d'un message m�me lors d'un abort m�me sur une exception
* InvalidProcessStateException.
*
* Revision 1.17  2005/12/23 13:21:40  tz
* Suppression des options n�cessaires lorsque l'affichage de fichier �tait
* r�alis� par une sous-classe.
*
* Revision 1.16  2005/10/07 08:27:30  tz
* Affichage du r�sultat de fin d'ex�cution dans la m�thode run() et plus dans
* la m�thode executionTerminated().
*
* Revision 1.15  2005/07/01 12:14:23  tz
* Modification du composant pour les traces
*
* Revision 1.14  2004/11/23 15:40:56  tz
* Utilisation d'un buffer de 1 octet pour les communications.
*
* Revision 1.13  2004/10/22 15:39:09  tz
* Adaptation pour la nouvelle interface ProcessorInterface,
* Affichage de tous les messages dans la m�me zone.
*
* Revision 1.12  2004/10/13 13:56:07  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.11  2004/10/06 07:32:16  tz
* Am�lioration de la dimension de la frame,
* Pas d'abort lors de la fermeture de la frame,
* Changement des couleurs d'affichage des infos.
*
* Revision 1.10  2004/07/29 12:07:31  tz
* Mise � jour de la documentation
* Suppression d'imports inutiles
* Support de l'abort
* Masquage de la zone d'erreur
*
* Revision 1.9  2003/12/08 15:13:52  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.8.2.1  2003/11/13 10:37:39  tz
* Correction de la fiche inuit/134.
*
* Revision 1.8  2003/03/07 16:21:18  tz
* Prise en compte du m�canisme de log m�tier
*
* Revision 1.7  2002/12/26 12:54:40  tz
* Passage du focus � la zone de saisie
*
* Revision 1.6  2002/09/20 10:46:57  tz
* Le bouton "Annuler" est gris�.
*
* Revision 1.5  2002/08/13 13:05:31  tz
* Utilisation de JacORB.
* D�sactivation de la zone de saisie � la fin de l'ex�cution.
* Passage en mode fermeture sur ProcessStateException.
*
* Revision 1.4  2002/04/05 15:50:40  tz
* Cloture it�ration IT1.2
*
* Revision 1.3  2002/03/27 09:50:54  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.1  2002/01/25 16:52:50  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.execution;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import java.awt.Cursor;
import com.bv.core.message.MessageManager;
import javax.swing.JScrollPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Document;
import javax.swing.text.DefaultStyledDocument;
import com.bv.core.prefs.PreferencesAPI;

//
// Imports du projet
//
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.corbacom.ExecutionListenerInterfaceOperations;
import com.bv.isis.corbacom.ExecutionContextInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.impl.com.ExecutionListenerImpl;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.gui.NonEditableTextArea;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.corbacom.ExecutionListenerInterfaceHelper;
import com.bv.isis.corbacom.ExecutionListenerInterface;
import com.bv.isis.console.com.IORFinder;
import com.bv.isis.corbacom.ProcessStateException;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.corbacom.UnknownException;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.gui.NonEditableTextPane;
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;

/*----------------------------------------------------------
* Nom: ExecutionProcessor
*
* Description:
* Cette classe impl�mente le processeur de t�che charg� de l'ex�cution d'une
* proc�dure sur un Agent. Son but est de d�clencher l'ex�cution de la proc�dure,
* d'afficher les messages et les erreurs d'ex�cution de celle-ci, et enfin de
* permettre � l'utilisateur de transmettre des informations � la proc�dure.
*
* Les messages d'ex�cution de la proc�dure proviennent de la sortie standard de
* celle-ci, et sont affich�s dans une zone sp�cifique.
* Les erreurs d'ex�cution de la proc�dure proviennent de la sortie d'erreur de
* celle-ci, et sont affich�s dans une zone sp�cifique.
* L'utilisateur a la possibilit� de transmettre des donn�es � la proc�dure en
* saisissant celles-ci dans une zone d�di�e, et en tapant sur la touche "Entr�e"
* ou en cliquant sur le bouton "Envoyer".
* ----------------------------------------------------------*/
public class ExecutionProcessor
	extends ProcessorFrame
	implements ExecutionListenerInterfaceOperations
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ExecutionProcessor
	*
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. Elle n'est
	* pr�sent�e que pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public ExecutionProcessor()
	{
		super(false);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ExecutionProcessor", "ExecutionProcessor");

		trace_methods.beginningOfMethod();
		// On consid�re que la proc�dure est termin�e (pour l'arr�t du
		// processeur).
		_terminated = true;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette m�thode red�finit celle de la classe ProcessorFrame. Elle est appel�e
	* par le ProcessManager afin d'initialiser et de d'ex�cuter le processeur.
	* La sous-fen�tre est construite, affich�e, et l'ex�cution de la proc�dure est
	* d�clench�e.
	* A la fin de l'ex�cution, un message indiquant cette fin, et le code de 
	* retour de la proc�dure est affich� � l'utilisateur.
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
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn�. Cet argument peut
	*    �tre nul.
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
		    "ExecutionProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");
		boolean show_popup = true;
		int counter = 0;

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
		// On r�cup�re les donn�es de configuration
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("Execution");
			show_popup = preferences.getBoolean("ShowPopup");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		// On construit le panneau de la fen�tre
		makePanel();
		// On cr�e le proxy de session
		ServiceSessionProxy session_proxy =
			new ServiceSessionProxy(selected_node.getServiceSession());
		// On cr�e un listener pour l'ex�cution de la proc�dure
		ExecutionListenerImpl execution_listener =
			new ExecutionListenerImpl();
		execution_listener.setExecutionListener(this);
		// On r�cup�re le contexte du noeud
		IndexedList context = selected_node.getContext(true);
		// On traite le pr�processing
		ProcessingHandler.handleProcessingStatement(preprocessing, context,
			windowInterface, AgentSessionManager.getInstance().getAgentLayerMode(
			selected_node.getAgentName()), selected_node.getServiceSession(), 
			this, true);
		// On cr�e une r�f�rence sur le listener d'ex�cution
		ExecutionListenerInterface listener =
			ExecutionListenerInterfaceHelper.narrow(
			IORFinder.servantToReference(execution_listener));
		String user_name = PasswordManager.getInstance().getUserName();
		String message =
			MessageManager.getMessage("&LOG_ProcedureExecutionStarting") +
			"'" + menuItem.getText() + "'";
		if(_actionId == null)
		{
			// On commence par r�cup�rer un num�ro d'action
			_actionId = 
				LogServiceProxy.getActionIdentifier(selected_node.getAgentName(),
				message, user_name, selected_node.getServiceName(),
				selected_node.getServiceType(), selected_node.getIClesName());
		}
		// On va loguer le message d'ex�cution de la proc�dure
		String[] message_data = new String[10 + context.size()];
		message_data[counter++] = message;
		message_data[counter++] = MessageManager.getMessage("&LOG_AgentName") +
			selected_node.getAgentName();
		message_data[counter++] = MessageManager.getMessage("&LOG_UserName") +
			user_name;
		message_data[counter++] =
			MessageManager.getMessage("&LOG_ServiceSessionId") +
			selected_node.getServiceSessionId();
		message_data[counter++] =
			MessageManager.getMessage("&LOG_ProcedureCommand") + parameters;
		message_data[counter++] = MessageManager.getMessage("&LOG_NodeType") +
			selected_node.getTableName();
		message_data[counter++] = MessageManager.getMessage("&LOG_NodeId") +
			selected_node.getKey();
		message_data[counter++] = MessageManager.getMessage("&LOG_NodeContext");
		message_data[counter++] = MessageManager.getMessage("&LOG_OpeningQuote");
		IsisParameter[] context_parameters =
			(IsisParameter[])context.toArray(new IsisParameter[0]);
		for(int index = 0 ; index < context_parameters.length ; index ++)
		{
			message_data[counter++] = "\t" + context_parameters[index].name
				+ "=" + context_parameters[index].value;
		}
		context_parameters = null;
		message_data[counter++] = MessageManager.getMessage("&LOG_ClosingQuote");
		LogServiceProxy.addMessageForAction(_actionId, message_data);
		// On cr�e un contexte d'ex�cution
		_executionContext = session_proxy.getExecutionContext(_actionId,
			listener, parameters, context);
		trace_events.writeTrace("L'utilisateur " + user_name +
			" a ex�cut� la proc�dure " + parameters);
		// On positionne les dimensions de la fen�tre
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(new Dimension(450, 300));
		// On affiche la fen�tre
		display();
		try
		{
			// On positionne le drapeau d'ex�cution de la proc�dure
			_terminated = false;
			if(_inputArea != null)
			{
				// On valide la zone de saisie
				_inputArea.setEnabled(true);
				_inputArea.requestFocus();
			}
			windowInterface.setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
			// On d�marre l'ex�cution de la proc�dure
		    _executionContext.execute(_actionId, 1);
		}
		catch(Exception exception)
		{
			counter = 0;
			trace_errors.writeTrace("Erreur lors de l'ex�cution de la proc�dure"
				+ exception);

			// On va logguer le message d'erreur
		    String[] error =
				CommonFeatures.buildArrayFromString(exception.getMessage());
			message_data = new String[error.length + 3];
			message_data[counter++] =
				MessageManager.getMessage("&LOG_ProcedureExecutionResult");
			message_data[counter++] =
				MessageManager.getMessage("&LOG_ExecutionFailed");
			message_data[counter++] =
				MessageManager.getMessage("&LOG_ExecutionError");
			for(int index = 0 ; index < error.length ; index ++)
			{
				message_data[counter++] = error[index];
			}
			LogServiceProxy.addMessageForAction(_actionId, message_data);
			// On d�positionne le drapeau d'ex�cution de la proc�dure
			_terminated = true;
			if(_inputArea != null)
			{
				// On invalide la zone de saisie
				_inputArea.setEnabled(false);
			}
			// Il y a eu une erreur lors de la tentative d'ex�cution de la
			// proc�dure, on renvoie une erreur
			throw new InnerException("&ERR_CannotExecuteProcedure",
				parameters, exception);
		}
		// Si on arrive ici, et que l'ex�cution ne s'est pas termin�e, on sort
		if(_terminated == false)
		{
			trace_methods.endOfMethod();
			return;
		}
		trace_events.writeTrace("Une proc�dure ex�cut�e par l'utilisateur " +
			PasswordManager.getInstance().getUserName() +
			" s'est termin�e avec un code retour " + _exitValue);
		// On va logguer l'information de fin d'ex�cution
		String[] output =
			CommonFeatures.buildArrayFromString(_outputArea.getText());
		message_data = new String[output.length + 4];
		counter = 0;
		message_data[counter++] =
			MessageManager.getMessage("&LOG_ProcedureExecutionResult");
		if(_exitValue == 0 || _exitValue == 201)
		{
			message_data[counter++] =
				MessageManager.getMessage("&LOG_ExecutionSuccessful");
		}
		else
		{
			message_data[counter++] =
				MessageManager.getMessage("&LOG_ExecutionFailed");
		}
		message_data[counter++] = MessageManager.getMessage("&LOG_ReturnCode") +
			_exitValue;
		message_data[counter++] =
			MessageManager.getMessage("&LOG_CompleteOutput");
		for(int index = 0 ; index < output.length ; index ++)
		{
			message_data[counter++] = output[index];
		}
		LogServiceProxy.addMessageForAction(_actionId, message_data);
		// On modifie l'�tat de la zone de saisie et du bouton "Envoyer"
		_inputArea.setEnabled(false);
		_sendButton.setEnabled(false);
		// On modifie le texte du bouton situ� en bas de la fen�tre
		_closeCancelButton.setText(
			MessageManager.getMessage("&Execution_Close"));
		_closeCancelButton.setEnabled(true);
		// On affiche une bo�te de dialogue � l'utilisateur afin de lui indiquer
		// la valeur du code de sortie de la proc�dure.
		String[] extra_info = { "" + _exitValue };
		String the_message = 
			MessageManager.fillInMessage(
			MessageManager.getMessage("&Execution_ProcedureHasTerminated"), 
			extra_info);
		if(show_popup == true)
		{
			getMainWindowInterface().showPopup("Information", the_message, 
				null);
		}
		else
		{
			Document document = _outputArea.getDocument();
			try
			{
				// Il faut ajouter la cha�ne dans la zone de texte
				document.insertString(document.getLength(), the_message, 
					_displayAttributes[2]);
				// On place le curseur � la fin du texte (assure le d�filement de
				// la zone).
				_outputArea.setCaretPosition(document.getLength());
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace("Erreur lors de l'ajout des donn�es " +
					"dans la zone d'affichage: " + exception);
				// On ne fait rien
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette m�thode r�d�finit celle de la super classe. Elle est appel�e lorsque
	* la sous-fen�tre du processeur doit �tre ferm�e (par l'utilisateur ou par
	* la fermeture de l'application).
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ExecutionProcessor", "close");

		trace_methods.beginningOfMethod();
		// On ferme la fen�tre en appelant la m�thode de la super-classe.
		super.close();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: receiveDataFromOutputStream
	*
	* Description:
	* Cette m�thode impl�mente celle de l'interface
	* ExecutionListenerInterfaceOperations. Elle est appel�e lorsque des 
	* donn�es ont �t� �mises par la proc�dure sur sa sortie standard.
	* Ces donn�es sont affich�es dans la zone de texte.
	*
	* Arguments:
	*  - data: Les donn�es �mises sur la sortie standard.
	* ----------------------------------------------------------*/
	public void receiveDataFromOutputStream(
		String data
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ExecutionProcessor", "receiveDataFromOutputStream");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);
		if(data != null && data.equals("") == false)
		{
			Document document = _outputArea.getDocument();
			try
			{
				// Il faut ajouter la cha�ne dans la zone de texte
				document.insertString(document.getLength(), data, _displayAttributes[0]);
				// On place le curseur � la fin du texte (assure le d�filement de
				// la zone).
				_outputArea.setCaretPosition(document.getLength());
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace("Erreur lors de l'ajout des donn�es " +
					"dans la zone d'affichage: " + exception);
				// On ne fait rien
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: receiveDataFromErrorStream
	*
	* Description:
	* Cette m�thode impl�mente celle de l'interface
	* ExecutionListenerInterfaceOperations. Elle est appel�e lorsque des 
	* donn�es ont �t� �mises par la proc�dure sur sa sortie d'erreur.
	* Ces donn�es sont affich�es dans la zone de texte.
	*
	* Arguments:
	*  - data: Les donn�es �mises sur la sortie d'erreur.
	* ----------------------------------------------------------*/
	public void receiveDataFromErrorStream(
		String data
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ExecutionProcessor", "receiveDataFromErrorStream");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);
		if(data != null && data.equals("") == false)
		{
			Document document = _outputArea.getDocument();
			try
			{
				// Il faut ajouter la cha�ne dans la zone de texte
				document.insertString(document.getLength(), data, _displayAttributes[1]);
				// On place le curseur � la fin du texte (assure le d�filement de
				// la zone).
				_outputArea.setCaretPosition(document.getLength());
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace("Erreur lors de l'ajout des donn�es " +
					"dans la zone d'affichage: " + exception);
				// On ne fait rien
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: executionTerminated
	*
	* Description:
	* Cette m�thode impl�mente celle de l'interface
	* ExecutionListenerInterfaceOperations. Elle est appel�e lorsque 
	* l'ex�cution de la proc�dure s'est termin�e.
	* La valeur de sortie de la proc�dure est stock�e dans l'attribut 
	* _exitValue.
	*
	* Arguments:
	*  - exitValue: La valeur de sortie de la proc�dure.
	* ----------------------------------------------------------*/
	public void executionTerminated(
		int exitValue
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ExecutionProcessor", "executionTerminated");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("exitValue=" + exitValue);
		_exitValue = exitValue;
		// On enregistre aussi le fait que l'ex�cution est termin�e
		_terminated = true;
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
			"ExecutionProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("execution.mdb", "UTF8");
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
			"ExecutionProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&ExecutionProcessorDescription");
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
			"ExecutionProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new ExecutionProcessor();
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer les panneaux de configuration du processeur.
	* Une instance de ExecutionConfigurationPanel est cr��e et est retourn�e.
	* 
	* Retourne: Un tableau ne contenant qu'une instance de 
	* ExecutionConfigurationPanel.
	* ----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionProcessor", "getConfigurationPanels");
		ConfigurationPanelInterface[] panels = 
			new ConfigurationPanelInterface[1];

		trace_methods.beginningOfMethod();
		panels[0] = new ExecutionConfigurationPanel();
		trace_methods.endOfMethod();
		return panels;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _terminated
	*
	* Description:
	* Cet attribut permet de savoir si l'ex�cution de la proc�dure est termin�e
	* ou non. Il est surtout utile lors du traitement du click sur le bouton de
	* la fen�tre (fermeture dans un cas, et annulation de l'ex�cution dans
	* l'autre cas).
	* ----------------------------------------------------------*/
	private boolean _terminated;

	/*----------------------------------------------------------
	* Nom: _actionId
	*
	* Description:
	* Cet attribut maintient le num�ro unique d'action correspondant �
	* l'ex�cution de la proc�dure. Ce num�ro est n�cessaire pour tracer
	* toutes les activit�s relatives � cette ex�cution.
	* ----------------------------------------------------------*/
	private String _actionId;

	/*----------------------------------------------------------
	* Nom: _exitValue
	* 
	* Description:
	* Cet attribut maintient une valeur enti�re destin�e � contenir la valeur 
	* de retour de la proc�dure ex�cut�e.
	* ----------------------------------------------------------*/
	private int _exitValue;

	/*----------------------------------------------------------
	* Nom: _closeCancelButton
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JButton repr�sentant le
	* bouton situ� en bas de la fen�tre d'ex�cution. Cette r�f�rence est
	* n�cessaire afin de pouvoir modifier le libell� du bouton en cours
	* d'ex�cution.
	* Le libell� est "Annuler" tant que la proc�dure n'est pas termin�e, et
	* "Fermer" une fois que la proc�dure est termin�e.
	* ----------------------------------------------------------*/
	private JButton _closeCancelButton;

	/*----------------------------------------------------------
	* Nom: _executionContext
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur une interface
	* ExecutionContextInterface qui est utilis�e pour transmettre des 
	* informations � la proc�dure, pour annuler l'ex�cution, ou encore pour 
	* d�marrer l'ex�cution.
	* ----------------------------------------------------------*/
	private ExecutionContextInterface _executionContext;

	/*----------------------------------------------------------
	* Nom: _sendButton
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JButton correspondant au
	* bouton "Envoyer". Il est n�cessaire afin d'inhiber ou non le bouton en
	* fonction de la pr�sence ou non de donn�es dans la zone de saisie.
	* ----------------------------------------------------------*/
	private JButton _sendButton;

	/*----------------------------------------------------------
	* Nom: _outputArea
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet NonEditableTextPane 
	* correspondant � la zone d'affichage des sorties standard et d'erreur 
	* de la proc�dure.
	* Cet attribut est n�cessaire afin de pouvoir mettre � jour cette zone 
	* avec les informations transmises par la proc�dure.
	* ----------------------------------------------------------*/
	private NonEditableTextPane _outputArea;

	/*----------------------------------------------------------
	* Nom: _inputArea
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JTextField correspondant
	* � la zone de saisie permettant de transmettre des informations sur
	* l'entr�e standard de la proc�dure.
	* Cet attribut est n�cessaire afin de pouvoir r�cup�rer les informations
	* saisies dans cette zone.
	* ----------------------------------------------------------*/
	private JTextField _inputArea;

	/*----------------------------------------------------------
	* Nom: _displayAttributes
	* 
	* Description:
	* Cet attribut maintient un tableau de SimpleAttributeSet n�cessaire au 
	* stockage des attributs de mise en forme des donn�es afin de les 
	* diff�rencier dans la zone d'affichage des sorties de la proc�dure.
	* ----------------------------------------------------------*/
	private SimpleAttributeSet[] _displayAttributes;

	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette m�thode construit le panneau de la sous-fen�tre en construisant 
	* les zones destin�es � l'affichage des messages d'ex�cution et 
	* d'erreur, ainsi qu'une zone destin�e � permettre la saisie de donn�es 
	* � transmettre � la proc�dure.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionProcessor", "makePanel");
		int background = Color.white.getRGB();
		int output = Color.black.getRGB();
		int error = Color.red.getRGB();
		Color foreground_color = null;
		Color background_color = null;
		int termination = Color.green.getRGB();

		trace_methods.beginningOfMethod();
		// Le layout manager du panneau est un GridBagLayout
		GridBagLayout frame_layout = new GridBagLayout();
		GridBagConstraints frame_constraints = new GridBagConstraints(0, 0, 1,
			1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(1, 2, 1, 2),	0, 0);
		getContentPane().setLayout(frame_layout);

		// On r�cup�re les donn�es de configuration
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("Execution");
			background = preferences.getInt("Background");
			output = preferences.getInt("Output");
			error = preferences.getInt("Error");
			termination = preferences.getInt("Termination");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		background_color = new Color(background);
		// La couleur d'avant plan est l'inverse de la couleur de fond
		foreground_color = new Color(255 - background_color.getRed(),
			255 - background_color.getGreen(), 
			255 - background_color.getBlue());
		// On commence par construire la zone d'affichage de la sortie standard
		_outputArea = new NonEditableTextPane(new DefaultStyledDocument());
		_outputArea.setCaretPosition(0);
		_outputArea.setFont((new NonEditableTextArea()).getFont());
		// On fixe les couleurs
		_outputArea.setBackground(background_color);
		_outputArea.setForeground(foreground_color);
		_outputArea.setSelectedTextColor(background_color);
		_outputArea.setSelectionColor(foreground_color);
		// On place la zone dans un scroll pane
		JScrollPane output_scroll = new JScrollPane(_outputArea);
		// On construit une bordure � la zone
		output_scroll.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Execution_OutputArea")));
		frame_layout.setConstraints(output_scroll, frame_constraints);
		getContentPane().add(output_scroll);
		// On d�finit les attributs
		_displayAttributes = new SimpleAttributeSet[3];
		_displayAttributes[0] = new SimpleAttributeSet();
		StyleConstants.setForeground(_displayAttributes[0], 
			new Color(output));
		_displayAttributes[1] = new SimpleAttributeSet();
		StyleConstants.setForeground(_displayAttributes[1], 
			new Color(error));
		_displayAttributes[2] = new SimpleAttributeSet();
		StyleConstants.setForeground(_displayAttributes[2], 
			new Color(termination));

		frame_constraints.weighty = 0;
		// On construit la zone de saisie
		JPanel input_panel = new JPanel();
		// On va �galement utiliser un GridBagLayout
		GridBagLayout panel_layout = new GridBagLayout();
		GridBagConstraints panel_constraints = new GridBagConstraints(0, 0, 1,
			1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 1, 0, 1),	0, 0);
		input_panel.setLayout(panel_layout);
		// On positionne une bordure pour le panneau
		input_panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Execution_InputArea")));
		// On cr�e une zone de saisie
		_inputArea = new JTextField();
		_inputArea.setEnabled(false);
		// On fixe les couleurs
		_inputArea.setBackground(Color.white);
		_inputArea.setForeground(Color.black);
		_inputArea.setCaretColor(Color.black);
		_inputArea.setSelectedTextColor(Color.white);
		_inputArea.setSelectionColor(Color.black);
		// On positionne les callbacks
		_inputArea.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Il faut envoyer les donn�es � la proc�dure
				sendData();
			}
		});
		// On positionne les contraintes pour cette zone
		panel_layout.setConstraints(_inputArea, panel_constraints);
		// On l'ajoute au panneau
		input_panel.add(_inputArea);
		// On cr�e le bouton "Envoyer"
		_sendButton = new JButton(MessageManager.getMessage("&Execution_Send"));
		//_sendButton.setEnabled(false);
		// On positionne le callback
		_sendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Il faut envoyer les donn�es � la proc�dure
				sendData();
			}
		});
		// On positionne les contraintes pour cette zone
		panel_constraints.gridx++;
		panel_constraints.weightx = 0;
		panel_layout.setConstraints(_sendButton, panel_constraints);
		// On l'ajoute au panneau
		input_panel.add(_sendButton);
		// On positionne les contraintes sur cette zone
		frame_constraints.gridy++;
		frame_layout.setConstraints(input_panel, frame_constraints);
		// On l'ajoute au panneau principal
		getContentPane().add(input_panel);

		// On construit le bouton "Annuler"
		_closeCancelButton =
			new JButton(MessageManager.getMessage("&Execution_Cancel"));
		_closeCancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode buttonPressed
				buttonPressed();
			}
		});
		// On positionne les contraintes pour le bouton
		frame_constraints.fill = GridBagConstraints.NONE;
		frame_constraints.gridy++;
		frame_layout.setConstraints(_closeCancelButton, frame_constraints);
		// On l'ajoute au panneau principal
		getContentPane().add(_closeCancelButton);

		// On redimensionne la fen�tre
		setSize(400, 400);
		// On passe le focus � la zone de saisie
		_inputArea.grabFocus();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: buttonPressed
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur clique sur le bouton situ�
	* en bas de la sous-fen�tre.
	* Si l'ex�cution de la proc�dure n'est pas termin�e, celle-ci est annul�e.
	* Dans le cas contraire, la fen�tre d'ex�cution est ferm�e.
	* ----------------------------------------------------------*/
	private void buttonPressed()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ExecutionProcessor", "buttonPressed");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// Le comportement n'est pas le m�me si la proc�dure a termin� ou non
		// son ex�cution
		trace_debug.writeTrace("_terminated=" + _terminated);
		if(_terminated == true || _executionContext == null)
		{
			// La proc�dure est termin�e, il faut fermer la fen�tre
			close();
			trace_methods.endOfMethod();
			return;
		}
		// La proc�dure n'est pas termin�e, il faut donc annuler son
		// ex�cution.
		try
		{
			String[] message = new String[1];
			message[0] = MessageManager.getMessage("&LOG_ExecutionAbort");
			LogServiceProxy.addMessageForAction(_actionId, message);
			// On envoie la commande d'abort
			_executionContext.abortExecution(_actionId);
			trace_events.writeTrace("L'utilisateur " +
				PasswordManager.getInstance().getUserName() +
				" a annul� l'ex�cution d'une proc�dure");
		}
		catch(ProcessStateException exception)
		{
			trace_errors.writeTrace("Erreur lors de l'annulation de " +
				"l'ex�cution de la proc�dure: " + exception);
			// Il y a eu une erreur lors de l'annulation de la proc�dure
			// Il faut afficher un message � l'utilisateur
			InnerException inner_exception = 
				new InnerException(exception.reason, null, exception);
			getMainWindowInterface().showPopupForException(
				"&ERR_ErrorWhileAborting", inner_exception);
			// Dans ce cas pr�cis, on va consid�rer que le processus
			// a termin� son ex�cution
			// On positionne le drapeau de fin d'ex�cution
			_terminated = true;
			// On modifie l'�tat de la zone de saisie et du bouton "Envoyer"
			_inputArea.setEnabled(false);
			_sendButton.setEnabled(false);
			// On modifie le texte du bouton situ� en bas de la fen�tre
			_closeCancelButton.setText(MessageManager.getMessage("&Execution_Close"));
			_closeCancelButton.setEnabled(true);
		}
		catch(UnknownException exception)
		{
			trace_errors.writeTrace("Erreur lors de l'annulation de " +
				"l'ex�cution de la proc�dure: " + exception.reason);
			// Il y a eu une erreur lors de l'annulation de la proc�dure
			// Il faut afficher un message � l'utilisateur
			InnerException inner_exception = 
				new InnerException(exception.reason, null, exception);
			getMainWindowInterface().showPopupForException(
				"&ERR_ErrorWhileAborting", inner_exception);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de l'annulation de " +
				"l'ex�cution de la proc�dure: " + exception);
			// Il y a eu une erreur lors de l'annulation de la proc�dure
			// Il faut afficher un message � l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_ErrorWhileAborting", exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: sendData
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur clique sur le bouton
	* "Envoyer", ou a tap� sur la touche "Entr�e" dans la zone de saisie.
	* Elle transmet les informations qui ont �t� entr�es dans la zone de saisie
	* � la proc�dure en cours d'ex�cution.
	* ----------------------------------------------------------*/
	private void sendData()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ExecutionProcessor", "sendData");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		// D'abord, il faut v�rifier que la proc�dure n'a pas termin� son
		// ex�cution, et que le contexte est valide
		if(_terminated == true || _executionContext == null)
		{
			// Il faut invalider les zones
			_inputArea.setEnabled(false);
			_sendButton.setEnabled(false);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re le contenu de la zone de saisie
		String data = _inputArea.getText();
		trace_debug.writeTrace("data=" + data);
		// On va logguer l'envoi des donn�es
		String[] output =
			CommonFeatures.buildArrayFromString(_outputArea.getText());
		String[] message = new String[output.length + 3];
		int counter = 0;
		message[counter++] =
			MessageManager.getMessage("&LOG_SendingExtraInformation");
		message[counter++] =
			MessageManager.getMessage("&LOG_PartialOutput");
		for(int index = 0 ; index < output.length ; index ++)
		{
			message[counter++] = output[index];
		}
		message[counter++] = MessageManager.getMessage("&LOG_DataSent") +
			data;
		LogServiceProxy.addMessageForAction(_actionId, message);
		// On tente de transmettre la donn�e � la proc�dure
		try
		{
			_executionContext.sendDataToInputStream(_actionId, data);
			trace_events.writeTrace("L'utilisateur " +
				PasswordManager.getInstance().getUserName() +
				" a transmis les donn�es '" + data + "' a une proc�dure");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			InnerException inner_exception = CommonFeatures.processException(
				"l'envoi des informations � la proc�dure", exception);
			counter = 0;
			// On va logguer le message d'erreur
		    String[] error =
				CommonFeatures.buildArrayFromString(inner_exception.getMessage());
			message = new String[error.length + 2];
			message[counter++] =
				MessageManager.getMessage("&LOG_DataSendingFailed");
			message[counter++] =
				MessageManager.getMessage("&LOG_ExecutionError");
			for(int index = 0 ; index < error.length ; index ++)
			{
				message[counter++] = error[index];
			}
			LogServiceProxy.addMessageForAction(_actionId, message);
			trace_errors.writeTrace("Erreur lors de l'envoi des informations � "
				+ "l'entr�e standard de la proc�dure: " + inner_exception);
			// Il y a eu une erreur lors de l'envoi des informations � l'entr�e
			// standard de la proc�dure, il faut en avertir l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_ErrorWhileSendingData", inner_exception);
		}
		// De toute mani�re, on efface le contenu de la zone de saisie
		_inputArea.setText("");
		trace_methods.endOfMethod();
	}
}