/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/events/EventsProcessor.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage des événements I-SIS
* DATE:        29/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.events
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: EventsProcessor.java,v $
* Revision 1.10  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.9  2008/02/21 12:08:26  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.8  2008/01/31 16:56:18  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.7  2005/12/23 13:19:53  tz
* Correction mineure.
*
* Revision 1.6  2005/10/07 13:40:13  tz
* Modification du mode de redimensionnement automatique.
*
* Revision 1.5  2005/10/07 08:28:03  tz
* Implémentation de l'interface ConsoleIsisEventsListenerInterface.
*
* Revision 1.4  2005/07/01 12:14:58  tz
* Modification du composant pour les traces
*
* Revision 1.3  2004/11/23 15:41:27  tz
* Correction du problème d'affichage dans le menu "Fenêtres"
*
* Revision 1.2  2004/11/09 15:24:21  tz
* Modification de la section pour le paramètre MaxEvents.
*
* Revision 1.1  2004/11/03 15:18:39  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.events;

//
//Imports système
//
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.util.UtilStringTokenizer;
import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.event.InternalFrameEvent;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.abs.processor.ConsoleIsisEventsListenerInterface;
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.gui.NonEditableTable;
import com.bv.isis.console.processor.ProcessorManager;
import com.bv.isis.console.impl.com.IsisEventsListenerImpl;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.IsisEventTypeEnum;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisNodeLabel;

/*----------------------------------------------------------
* Nom: EventsProcessor
* 
* Description:
* Cette classe implémente le processeur de tâche d'affichage des événements 
* I-SIS. Ce processeur étant un processeur graphique, elle spécialise la 
* classe ProcessorFrame. Afin de permettre la réception des événements I-SIS, 
* cette classe implémente l'interface ConsoleIsisEventsListenerInterface.
* Elle implémente également l'interface MouseListener pour permettre 
* l'affichage d'un menu contextuel.
* Ce processeur n'est destiné à être invoqué que via le menu "Outils" de la 
* Console.
* 
* Ce processeur a un fonctionnement particulier, puisqu'il doit pouvoir 
* recevoir les événements même lorsque sa fenêtre n'est pas visible à 
* l'utilisateur. C'est pourquoi la fenêtre est construite dès la construction 
* de la première et unique instance, et que la méthode duplicate() ne retourne 
* pas de double de l'instance, mais l'instance elle-même.
* ----------------------------------------------------------*/
public class EventsProcessor
	extends ProcessorFrame
	implements 
		ConsoleIsisEventsListenerInterface,
		MouseListener
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: EventsProcessor
	* 
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Elle n'est
	* présentée que pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public EventsProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "EventsProcessor");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour effectuer le pré-chargement du processeur.
	* Elle déclenche le chargement du fichier de messages du processeur.
	* Contrairement aux autres processeurs graphiques, la classe construit la 
	* fenêtre, via la méthode makePanel(), puis s'enregistre en tant que 
	* receveur des notifications, via la classe IsisEventsListenerImpl.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "preLoad");
		
		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("events.mdb", "UTF8");
		// Construction de la fenêtre
		makePanel();
		// On s'enregistre en tant que receveur des notifications
		IsisEventsListenerImpl listener = IsisEventsListenerImpl.getInstance();
		listener.addIsisEventsListener(this); 
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un noeud 
	* d'exploration.
	* Pour ce processeur, seule l'invocation via le menu "Outils" est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "isTreeCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un 
	* élément de tableau.
	* Pour ce processeur, seule l'invocation via le menu "Outils" est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "isTableCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via le menu "Outils" est 
	* autorisée.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "isGlobalCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour récupérer l'intitulé de l'élément du menu "Outils" 
	* associé à ce processeur.
	* 
	* Retourne: L'intitulé de l'élément de menu.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "getMenuLabel");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return "Events";
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour déclencher l'exécution du processeur.
	* La méthode ne fait qu'afficher la fenêtre qui a été construite par la 
	* méthode makePanel().
	* 
	* Si un problème est détecté durant la phase d'exécution, l'exception 
	* InnerException doit être levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    permettant au processeur d'interagir avec la fenêtre principale,
	*  - menuItem: Une référence sur l'objet JMenuItem par lequel le 
	*    processeur a été exécuté,
	*  - parameters: Une chaîne de caractère contenant des paramètres 
	*    spécifiques au processeur,
	*  - preprocessing: Une chaîne de caractères contenant des instructions 
	*    de préprocessing,
	*  - postprocessing: Une chaîne de caractères contenant des instructions 
	*    de postprocessing,
	*  - selectedNode: Une référence sur le noeud sélectionné.
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
			"EventsProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// On appelle la méthode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On affiche la fenêtre
		if(_hasBeenAdded == false)
		{
			display();
			_hasBeenAdded = true;
		}
		else
		{
			setVisible(true);
			// On va forcer le rafraîchissement du menu des fenêtres
			fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_ACTIVATED);
		}
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Au lieu 
	* de détruire la fenêtre du processeur, elle ne fait que de la masquer.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "close");
		
		trace_methods.beginningOfMethod();
		// On masque la fenêtre
		setVisible(false);
		// On réactive l'item de menu
		setMenuItemState(true);
		// On va forcer le rafraîchissement du menu des fenêtres
		fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
		trace_methods.endOfMethod();
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
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "getDescription");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&EventsProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* Pour ce processeur, aucun double n'est retourné. L'instance est 
	* retournée.
	* 
	* Retourne: L'instance de EventsProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return this;
	}

	/*----------------------------------------------------------
	* Nom: eventOccured
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* ConsoleIsisEventsListenerInterface. Elle est appelée à chaque fois 
	* qu'un événement I-SIS survient.
	* La méthode ajoute une chaîne de caractères représentant l'événement au 
	* modèle des données d'événement, puis vérifie que le nombre d'événements 
	* contenus dans le modèle ne dépasse pas la limite fixée par le paramètre 
	* de configuration "Console.MaxEvents".
	* 
	* Arguments:
	*  - eventType: Le type d'événement,
	*  - eventInformation: Un tableau de chaînes de caractères contenant les 
	*    informations de l'événement.
 	* ----------------------------------------------------------*/
	public void eventOccured(
		IsisEventTypeEnum eventType, 
		String[] eventInformation
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "eventOccured");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		int max_events = 150;
		String date;
		String type = null;
		StringBuffer information = new StringBuffer();
		String agent_name = null;
		String agent_ip = null;
		String console_ip = null;
		String user_name = null;
		String icles_name = null;
		String service_name = null;
		String service_type = null;
		String session_id = null;
		String action_id = null;
		String command = null;
		int length;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("eventType=" + eventType);
		trace_arguments.writeTrace("eventInformation=" + eventInformation);
		// On récupère le nombre max d'événements
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			max_events = configuration.getInt("Console", "MaxEvents");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Erreur lors de la récupération de la " +
				"configuration: " + exception.getMessage());
		}
		// On va composer la date
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		date = format.format(new Date());
		// On va traiter le type d'événement
		switch(eventType.value())
		{
			case IsisEventTypeEnum._A_SESSION_CLOSED:
				type = MessageManager.getMessage("&Events_AgentSessionClosed");
				agent_name = eventInformation[0];
				console_ip = eventInformation[1];
				user_name = eventInformation[2];
				session_id = eventInformation[3];
				action_id = eventInformation[4];
				break;
			case IsisEventTypeEnum._A_SESSION_OPENED:
				type = MessageManager.getMessage("&Events_AgentSessionOpened");
				agent_name = eventInformation[0];
				console_ip = eventInformation[1];
				user_name = eventInformation[2];
				session_id = eventInformation[3];
				action_id = eventInformation[4];
				break;
			case IsisEventTypeEnum._AGENT_STARTED:
				type = MessageManager.getMessage("&Events_AgentStarted");
				agent_name = eventInformation[0];
				agent_ip = eventInformation[1];
				break;
			case IsisEventTypeEnum._AGENT_STOPPED:
				type = MessageManager.getMessage("&Events_AgentStopped");
				agent_name = eventInformation[0];
				agent_ip = eventInformation[1];
				break;
			case IsisEventTypeEnum._CONSOLE_STARTED:
				type = MessageManager.getMessage("&Events_ConsoleStarted");
				console_ip = eventInformation[0];
				break;
			case IsisEventTypeEnum._CONSOLE_STOPPED:
				type = MessageManager.getMessage("&Events_ConsoleStopped");
				console_ip = eventInformation[0];
				break;
			case IsisEventTypeEnum._EXECUTION_STARTED:
				type = MessageManager.getMessage("&Events_ExecutionStarted");
				length = eventInformation.length;
				agent_name = eventInformation[0];
				console_ip = eventInformation[1];
				user_name = eventInformation[2];
				icles_name = eventInformation[length - 6];
				service_name = eventInformation[length - 5];
				service_type = eventInformation[length - 4];
				session_id = eventInformation[length - 3];
				command = eventInformation[length - 2];
				action_id = eventInformation[length - 1];
				break;
			case IsisEventTypeEnum._EXECUTION_STOPPED:
				type = MessageManager.getMessage("&Events_ExecutionStopped");
				length = eventInformation.length;
				agent_name = eventInformation[0];
				console_ip = eventInformation[1];
				user_name = eventInformation[2];
				icles_name = eventInformation[length - 6];
				service_name = eventInformation[length - 5];
				service_type = eventInformation[length - 4];
				session_id = eventInformation[length - 3];
				command = eventInformation[length - 2];
				action_id = eventInformation[length - 1];
				break;
			case IsisEventTypeEnum._PORTAL_STOPPED:
				type = MessageManager.getMessage("&Events_PortalStopped");
				break;
			case IsisEventTypeEnum._S_SESSION_CLOSED:
				type = MessageManager.getMessage("&Events_ServiceSessionClosed");
				length = eventInformation.length;
				agent_name = eventInformation[0];
				console_ip = eventInformation[1];
				user_name = eventInformation[2];
				icles_name = eventInformation[length - 5];
				service_name = eventInformation[length - 4];
				service_type = eventInformation[length - 3];
				session_id = eventInformation[length - 2];
				action_id = eventInformation[length - 1];
				break;
			case IsisEventTypeEnum._S_SESSION_OPENED:
				type = MessageManager.getMessage("&Events_ServiceSessionOpened");
				length = eventInformation.length;
				agent_name = eventInformation[0];
				console_ip = eventInformation[1];
				user_name = eventInformation[2];
				icles_name = eventInformation[length - 5];
				service_name = eventInformation[length - 4];
				service_type = eventInformation[length - 3];
				session_id = eventInformation[length - 2];
				action_id = eventInformation[length - 1];
				break;
		}
		// On construit la donnée des informations
		if(agent_name != null)
		{
			information.append(
				MessageManager.getMessage("&Events_AgentName") + '=' + 
				agent_name + ',');
		}
		if(agent_ip != null)
		{
			information.append(
				MessageManager.getMessage("&Events_AgentIP") + '=' +
				agent_ip + ',');
		}
		if(console_ip != null)
		{
			information.append(
				MessageManager.getMessage("&Events_ConsoleIP") + '=' +
				console_ip + ',');
		}
		if(user_name != null)
		{
			information.append(
				MessageManager.getMessage("&Events_UserName") + '=' +
				user_name + ',');
		}
		if(icles_name != null)
		{
			information.append(
				MessageManager.getMessage("&Events_IClesName") + '=' +
				icles_name + ',');
		}
		if(service_name != null)
		{
			information.append(
				MessageManager.getMessage("&Events_ServiceName") + '=' +
				service_name + ',');
		}
		if(service_type != null)
		{
			information.append(
				MessageManager.getMessage("&Events_ServiceType") + '=' +
				service_type + ',');
		}
		if(session_id != null)
		{
			information.append(
				MessageManager.getMessage("&Events_SessionId") + '=' +
				session_id + ',');
		}
		if(command != null)
		{
			information.append(
				MessageManager.getMessage("&Events_Command") + '=' +
				command + ',');
		}
		if(action_id != null)
		{
			information.append(
				MessageManager.getMessage("&Events_ActionId") + '=' +
				action_id);
		}

		// On ajoute le tout au modèle
		String[] data = new String[3];
		data[0] = date;
		data[1] = type;
		data[2] = information.toString();
		if(data[2].endsWith(",") == true)
		{
			data[2] = data[2].substring(0, data[2].length() - 1);
		}
		_model.addRow(data);
		// On vérifie que le nombre d'événements de dépasse pas la limite
		while(_model.getRowCount() > max_events)
		{
			_model.removeRow(0);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: clearEvents
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* ConsoleIsisEventsListenerInterface. Elle est appelée lorsque les 
	* événements précédemment enregistrés doivent être supprimés.
	* Elle supprime toutes les données du modèle, afin de supprimer tous les 
	* événements contenus dans celui-ci.
	* ----------------------------------------------------------*/
	public void clearEvents()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "clearEvents");

		trace_methods.beginningOfMethod();
		while(_model.getRowCount() > 0)
		{
			_model.removeRow(0);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: mouseReleased
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque l'utilisateur a relaché la pression sur l'un des boutons 
	* de la souris.
	* La méthode vérifie que l'utilisateur a utilisé le bouton droit, et 
	* qu'une ligne de données a été sélectionnée.
	* Puis, un menu contextuel est construit, et est affiché à l'écran.
	* 
	* Arguments:
	*  - event: L'événement de déclenchement de la méthode.
 	* ----------------------------------------------------------*/
 	public void mouseReleased(
 		MouseEvent event
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "tableDefinitionUseChanged");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		// Si l'événement est null ou qu'il ne s'agit pas d'un click sur le
		// bouton droit, on sort
		if(event == null || SwingUtilities.isRightMouseButton(event) == false)
		{
			trace_methods.endOfMethod();
			return;
		}
		// Ensuite, on regarde s'il y a une ligne sélectionnée dans
		// le tableau
		NonEditableTable table = (NonEditableTable)event.getSource();
		final int selected_row = table.getSelectedRow();
		if(selected_row == -1)
		{
			trace_debug.writeTrace(
				"Il n'y a pas de ligne sélectionnée !");
			// Il n'y a pas de ligne sélectionnée, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On va construire le menu contextuel associé
		JMenu menu = new JMenu();
		JMenuItem detail_item = 
			new JMenuItem(MessageManager.getMessage("&Events_Detail"));
		detail_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				detailEvent(selected_row);
			}
		});
		menu.add(detail_item);
		final JPopupMenu contextual_menu = menu.getPopupMenu();
		// On affiche le menu dans le tableau aux coordonnées du click
		contextual_menu.show(table, event.getX(), event.getY());
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: mouseClicked
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque l'utilisateur a cliqué.
	* La méthode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilisé.
 	* ----------------------------------------------------------*/
 	public void mouseClicked(
 		MouseEvent event
 		)
 	{
		// On ne fait rien
 	}

	/*----------------------------------------------------------
	* Nom: mouseEntered
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque la souris est entrée dans la zone d'affichage du 
	* composant.
	* La méthode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilisé.
 	* ----------------------------------------------------------*/
 	public void mouseEntered(
 		MouseEvent event
 		)
 	{
		// On ne fait rien
 	}

	/*----------------------------------------------------------
	* Nom: mouseExited
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque la souris est sortie de la zone d'affichage du composant.
	* La méthode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilisé.
 	* ----------------------------------------------------------*/
 	public void mouseExited(
 		MouseEvent event
 		)
 	{
		// On ne fait rien
 	}

	/*----------------------------------------------------------
	* Nom: mousePressed
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MouseListener. Elle est 
	* appelée lorsque l'utilisateur a pressé sur l'un des boutons de la souris.
	* La méthode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilisé.
 	* ----------------------------------------------------------*/
 	public void mousePressed(
 		MouseEvent event
 		)
 	{
		// On ne fait rien
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _model
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet DefaultTableModel 
	* correspondant au modèles des données du tableau des événements I-SIS.
	* ----------------------------------------------------------*/
	private DefaultTableModel _model;
	
	private boolean _hasBeenAdded = false;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de la fenêtre du 
	* processeur. Elle va construire un tableau ne disposant pas d'entête de 
	* colonnes, avec un modèle par défaut. La référence sur le modèle est 
	* récupérée et est affectée à l'attribut _model.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "makePanel");
		
		trace_methods.beginningOfMethod();
		setTitle(MessageManager.getMessage("&Events_Title"));
		// On va positionner le gestionnaire de layout
		getContentPane().setLayout(new BorderLayout());
		// On crée le modèle des données
		_model = new DefaultTableModel();
		_model.addColumn(MessageManager.getMessage("&Events_Date"));
		_model.addColumn(MessageManager.getMessage("&Events_Type"));
		_model.addColumn(MessageManager.getMessage("&Events_Information"));
		// On va créer le tableau
		NonEditableTable table = new NonEditableTable(_model, null, ";");
		// On règle quelques paramètres
		table.addMouseListener(this);
		table.setAutoCreateColumnsFromModel(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		// On place la table dans une zone à défilement
		JScrollPane scroll = new JScrollPane(table);
		// On place le tableau au mileu de la fenêtre
		getContentPane().add(scroll, BorderLayout.CENTER);

		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Events_Close"));
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture
				close();
			}
		});
		// On crée un panneau avec un GridBagLayout
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
		setMinimumSize(new Dimension(200, 150));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: detailEvent
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'un des 
	* boutons de détail d'un événement du tableau.
	* La méthode crée un objet GenericTreeObjectNode temporaire qui est 
	* utilisé pour appeler le processeur de détail DetailProcessor.
	* 
	* Arguments:
	*  - eventRow: L'indice de l'événement à détailler dans le tableau.
 	* ----------------------------------------------------------*/
 	private void detailEvent(
 		int eventRow
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "detailEvent");
		
		trace_methods.beginningOfMethod();
		// On va commencer par récupérer les informations de la ligne de
		// données
		String date = (String)_model.getValueAt(eventRow, 0);
		String event_type = (String)_model.getValueAt(eventRow, 1);
		String event_information = (String)_model.getValueAt(eventRow, 2);
		// On va découper les informations d'événement en sous-chaînes
		UtilStringTokenizer tokenizer = 
			new UtilStringTokenizer(event_information, ",");
		IsisParameter[] parameters = 
			new IsisParameter[tokenizer.getTokensCount() + 1];
		parameters[0] = new IsisParameter();
		parameters[0].name = MessageManager.getMessage("&Events_Date");
		parameters[0].value = date;
		for(int index = 0 ; index < tokenizer.getTokensCount() ; index ++)
		{
			UtilStringTokenizer param_tokenizer = 
				new UtilStringTokenizer(tokenizer.getToken(index), "=");
			parameters[index + 1] = new IsisParameter();
			parameters[index + 1].name = param_tokenizer.getToken(0);
			parameters[index + 1].value = param_tokenizer.getToken(1);
		}
		// On construit un faux label
		IsisNodeLabel label = new IsisNodeLabel();
		label.label = event_type;
		// On construit un faux noeud
		GenericTreeObjectNode fake_node = 
			new GenericTreeObjectNode(parameters, null, null, null, null, 
			null, null);
		fake_node.setLabel(label);
		// On n'a plus qu'à appeler le processeur de détail
		try
		{
			ProcessorManager.executeProcessor("Detail", getMainWindowInterface(), 
				null, null, null, null, fake_node, false, false);
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Erreur lors de l'invocation du " +
				"processeur Detail: " + exception);
		}
		trace_methods.endOfMethod();
 	}
}
