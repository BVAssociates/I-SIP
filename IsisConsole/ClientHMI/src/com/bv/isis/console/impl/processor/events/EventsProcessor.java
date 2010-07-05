/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/events/EventsProcessor.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage des �v�nements I-SIS
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
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.7  2005/12/23 13:19:53  tz
* Correction mineure.
*
* Revision 1.6  2005/10/07 13:40:13  tz
* Modification du mode de redimensionnement automatique.
*
* Revision 1.5  2005/10/07 08:28:03  tz
* Impl�mentation de l'interface ConsoleIsisEventsListenerInterface.
*
* Revision 1.4  2005/07/01 12:14:58  tz
* Modification du composant pour les traces
*
* Revision 1.3  2004/11/23 15:41:27  tz
* Correction du probl�me d'affichage dans le menu "Fen�tres"
*
* Revision 1.2  2004/11/09 15:24:21  tz
* Modification de la section pour le param�tre MaxEvents.
*
* Revision 1.1  2004/11/03 15:18:39  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.events;

//
//Imports syst�me
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
* Cette classe impl�mente le processeur de t�che d'affichage des �v�nements 
* I-SIS. Ce processeur �tant un processeur graphique, elle sp�cialise la 
* classe ProcessorFrame. Afin de permettre la r�ception des �v�nements I-SIS, 
* cette classe impl�mente l'interface ConsoleIsisEventsListenerInterface.
* Elle impl�mente �galement l'interface MouseListener pour permettre 
* l'affichage d'un menu contextuel.
* Ce processeur n'est destin� � �tre invoqu� que via le menu "Outils" de la 
* Console.
* 
* Ce processeur a un fonctionnement particulier, puisqu'il doit pouvoir 
* recevoir les �v�nements m�me lorsque sa fen�tre n'est pas visible � 
* l'utilisateur. C'est pourquoi la fen�tre est construite d�s la construction 
* de la premi�re et unique instance, et que la m�thode duplicate() ne retourne 
* pas de double de l'instance, mais l'instance elle-m�me.
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
	* Cette m�thode est le constructeur par d�faut de la classe. Elle n'est
	* pr�sent�e que pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour effectuer le pr�-chargement du processeur.
	* Elle d�clenche le chargement du fichier de messages du processeur.
	* Contrairement aux autres processeurs graphiques, la classe construit la 
	* fen�tre, via la m�thode makePanel(), puis s'enregistre en tant que 
	* receveur des notifications, via la classe IsisEventsListenerImpl.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "preLoad");
		
		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("events.mdb", "UTF8");
		// Construction de la fen�tre
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un noeud 
	* d'exploration.
	* Pour ce processeur, seule l'invocation via le menu "Outils" est 
	* autoris�e.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un 
	* �l�ment de tableau.
	* Pour ce processeur, seule l'invocation via le menu "Outils" est 
	* autoris�e.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via le menu "Outils" est 
	* autoris�e.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour r�cup�rer l'intitul� de l'�l�ment du menu "Outils" 
	* associ� � ce processeur.
	* 
	* Retourne: L'intitul� de l'�l�ment de menu.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour d�clencher l'ex�cution du processeur.
	* La m�thode ne fait qu'afficher la fen�tre qui a �t� construite par la 
	* m�thode makePanel().
	* 
	* Si un probl�me est d�tect� durant la phase d'ex�cution, l'exception 
	* InnerException doit �tre lev�e.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface 
	*    permettant au processeur d'interagir avec la fen�tre principale,
	*  - menuItem: Une r�f�rence sur l'objet JMenuItem par lequel le 
	*    processeur a �t� ex�cut�,
	*  - parameters: Une cha�ne de caract�re contenant des param�tres 
	*    sp�cifiques au processeur,
	*  - preprocessing: Une cha�ne de caract�res contenant des instructions 
	*    de pr�processing,
	*  - postprocessing: Une cha�ne de caract�res contenant des instructions 
	*    de postprocessing,
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn�.
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
			"EventsProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// On appelle la m�thode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On affiche la fen�tre
		if(_hasBeenAdded == false)
		{
			display();
			_hasBeenAdded = true;
		}
		else
		{
			setVisible(true);
			// On va forcer le rafra�chissement du menu des fen�tres
			fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_ACTIVATED);
		}
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Au lieu 
	* de d�truire la fen�tre du processeur, elle ne fait que de la masquer.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "close");
		
		trace_methods.beginningOfMethod();
		// On masque la fen�tre
		setVisible(false);
		// On r�active l'item de menu
		setMenuItemState(true);
		// On va forcer le rafra�chissement du menu des fen�tres
		fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
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
			"EventsProcessor", "getDescription");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&EventsProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
	* Pour ce processeur, aucun double n'est retourn�. L'instance est 
	* retourn�e.
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
	* Cette m�thode red�finit celle de l'interface 
	* ConsoleIsisEventsListenerInterface. Elle est appel�e � chaque fois 
	* qu'un �v�nement I-SIS survient.
	* La m�thode ajoute une cha�ne de caract�res repr�sentant l'�v�nement au 
	* mod�le des donn�es d'�v�nement, puis v�rifie que le nombre d'�v�nements 
	* contenus dans le mod�le ne d�passe pas la limite fix�e par le param�tre 
	* de configuration "Console.MaxEvents".
	* 
	* Arguments:
	*  - eventType: Le type d'�v�nement,
	*  - eventInformation: Un tableau de cha�nes de caract�res contenant les 
	*    informations de l'�v�nement.
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
		// On r�cup�re le nombre max d'�v�nements
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			max_events = configuration.getInt("Console", "MaxEvents");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Erreur lors de la r�cup�ration de la " +
				"configuration: " + exception.getMessage());
		}
		// On va composer la date
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		date = format.format(new Date());
		// On va traiter le type d'�v�nement
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
		// On construit la donn�e des informations
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

		// On ajoute le tout au mod�le
		String[] data = new String[3];
		data[0] = date;
		data[1] = type;
		data[2] = information.toString();
		if(data[2].endsWith(",") == true)
		{
			data[2] = data[2].substring(0, data[2].length() - 1);
		}
		_model.addRow(data);
		// On v�rifie que le nombre d'�v�nements de d�passe pas la limite
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
	* Cette m�thode red�finit celle de l'interface 
	* ConsoleIsisEventsListenerInterface. Elle est appel�e lorsque les 
	* �v�nements pr�c�demment enregistr�s doivent �tre supprim�s.
	* Elle supprime toutes les donn�es du mod�le, afin de supprimer tous les 
	* �v�nements contenus dans celui-ci.
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
	* Cette m�thode red�finit celle de l'interface MouseListener. Elle est 
	* appel�e lorsque l'utilisateur a relach� la pression sur l'un des boutons 
	* de la souris.
	* La m�thode v�rifie que l'utilisateur a utilis� le bouton droit, et 
	* qu'une ligne de donn�es a �t� s�lectionn�e.
	* Puis, un menu contextuel est construit, et est affich� � l'�cran.
	* 
	* Arguments:
	*  - event: L'�v�nement de d�clenchement de la m�thode.
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
		// Si l'�v�nement est null ou qu'il ne s'agit pas d'un click sur le
		// bouton droit, on sort
		if(event == null || SwingUtilities.isRightMouseButton(event) == false)
		{
			trace_methods.endOfMethod();
			return;
		}
		// Ensuite, on regarde s'il y a une ligne s�lectionn�e dans
		// le tableau
		NonEditableTable table = (NonEditableTable)event.getSource();
		final int selected_row = table.getSelectedRow();
		if(selected_row == -1)
		{
			trace_debug.writeTrace(
				"Il n'y a pas de ligne s�lectionn�e !");
			// Il n'y a pas de ligne s�lectionn�e, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On va construire le menu contextuel associ�
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
		// On affiche le menu dans le tableau aux coordonn�es du click
		contextual_menu.show(table, event.getX(), event.getY());
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: mouseClicked
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface MouseListener. Elle est 
	* appel�e lorsque l'utilisateur a cliqu�.
	* La m�thode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilis�.
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
	* Cette m�thode red�finit celle de l'interface MouseListener. Elle est 
	* appel�e lorsque la souris est entr�e dans la zone d'affichage du 
	* composant.
	* La m�thode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilis�.
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
	* Cette m�thode red�finit celle de l'interface MouseListener. Elle est 
	* appel�e lorsque la souris est sortie de la zone d'affichage du composant.
	* La m�thode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilis�.
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
	* Cette m�thode red�finit celle de l'interface MouseListener. Elle est 
	* appel�e lorsque l'utilisateur a press� sur l'un des boutons de la souris.
	* La m�thode ne fait rien.
	* 
	* Arguments:
	*  - event: Non utilis�.
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
	* Cet attribut maintient une r�f�rence sur un objet DefaultTableModel 
	* correspondant au mod�les des donn�es du tableau des �v�nements I-SIS.
	* ----------------------------------------------------------*/
	private DefaultTableModel _model;
	
	private boolean _hasBeenAdded = false;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette m�thode est charg�e de la construction de la fen�tre du 
	* processeur. Elle va construire un tableau ne disposant pas d'ent�te de 
	* colonnes, avec un mod�le par d�faut. La r�f�rence sur le mod�le est 
	* r�cup�r�e et est affect�e � l'attribut _model.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "makePanel");
		
		trace_methods.beginningOfMethod();
		setTitle(MessageManager.getMessage("&Events_Title"));
		// On va positionner le gestionnaire de layout
		getContentPane().setLayout(new BorderLayout());
		// On cr�e le mod�le des donn�es
		_model = new DefaultTableModel();
		_model.addColumn(MessageManager.getMessage("&Events_Date"));
		_model.addColumn(MessageManager.getMessage("&Events_Type"));
		_model.addColumn(MessageManager.getMessage("&Events_Information"));
		// On va cr�er le tableau
		NonEditableTable table = new NonEditableTable(_model, null, ";");
		// On r�gle quelques param�tres
		table.addMouseListener(this);
		table.setAutoCreateColumnsFromModel(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		// On place la table dans une zone � d�filement
		JScrollPane scroll = new JScrollPane(table);
		// On place le tableau au mileu de la fen�tre
		getContentPane().add(scroll, BorderLayout.CENTER);

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
		setMinimumSize(new Dimension(200, 150));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: detailEvent
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur l'un des 
	* boutons de d�tail d'un �v�nement du tableau.
	* La m�thode cr�e un objet GenericTreeObjectNode temporaire qui est 
	* utilis� pour appeler le processeur de d�tail DetailProcessor.
	* 
	* Arguments:
	*  - eventRow: L'indice de l'�v�nement � d�tailler dans le tableau.
 	* ----------------------------------------------------------*/
 	private void detailEvent(
 		int eventRow
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EventsProcessor", "detailEvent");
		
		trace_methods.beginningOfMethod();
		// On va commencer par r�cup�rer les informations de la ligne de
		// donn�es
		String date = (String)_model.getValueAt(eventRow, 0);
		String event_type = (String)_model.getValueAt(eventRow, 1);
		String event_information = (String)_model.getValueAt(eventRow, 2);
		// On va d�couper les informations d'�v�nement en sous-cha�nes
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
		// On n'a plus qu'� appeler le processeur de d�tail
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
