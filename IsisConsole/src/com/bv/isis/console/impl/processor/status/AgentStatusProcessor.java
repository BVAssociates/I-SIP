/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/status/AgentStatusProcessor.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'état détaillé d'un Agent
* DATE:        26/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.status
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: AgentStatusProcessor.java,v $
* Revision 1.8  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.7  2008/02/21 12:09:47  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.6  2008/01/31 16:59:08  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.5  2005/12/23 13:22:53  tz
* Correction mineure.
*
* Revision 1.4  2005/10/07 08:19:55  tz
* Appel à la méthode close.
*
* Revision 1.3  2005/07/01 12:09:27  tz
* Modification du composant pour les traces
*
* Revision 1.2  2004/11/05 10:41:50  tz
* Modification de la dimension préférée.
*
* Revision 1.1  2004/11/02 08:51:09  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.status;

//
// Imports système
//
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.bv.core.message.MessageManager;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.gui.NonEditableTable;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.com.PortalInterfaceProxy;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.corbacom.AgentInterface;
import com.bv.isis.corbacom.IsisServerStatus;

/*----------------------------------------------------------
* Nom: AgentStatusProcessor
* 
* Description:
* Cette classe implémente le processeur de tâche chargé de l'affichage de 
* l'état complet d'un Agent. Il s'agit de récupérer les informations d'état 
* au niveau de l'Agent et de les afficher dans une fenêtre.
* Pour cela, le processeur spécialise la classe ProcessorFrame.
* ----------------------------------------------------------*/
public class AgentStatusProcessor 
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: AgentStatusProcessor
	* 
	* Description:
	* Cette méthode est le constructeur par défaut. Elle n'est présentée que 
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public AgentStatusProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentStatusProcessor", "AgentStatusProcessor");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée afin d'effectuer le pré-chargement du processeur.
	* Elle effectue le chargement du fichier de messages du processeur.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentStatusProcessor", "preLoad");
			
		trace_methods.beginningOfMethod();
		MessageManager.loadFile("status.mdb", "UTF8");
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour déclencher l'exécution du processeur.
	* L'exécution du processeur consiste à récupérer l'interface de l'Agent 
	* concerné, via la méthode getAgentInterface() de la classe 
	* PortalInterfaceProxy, et d'appeller la méthode getStatus() sur cette 
	* interface.
	* Ensuite, le panneau d'affichage de l'état détaillé est construit par un 
	* appel à la méthode makePanel().
	* 
	* Si un problème est détecté lors de l'exécution du processeur, 
	* l'exception InnerException est levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface,
	*  - menuItem: Une référence sur l'élément de menu par lequel le 
	*    processeur a été lancé,
	*  - parameters: Une chaîne contenant des paramètres optionnels du 
	*    processeur de tâche,
	*  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	*  - selectedNode: Une référence sur le noeud à explorer.
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
			"AgentStatusProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		IsisServerStatus agent_status = null;
		String agent_name = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Si le noeud sélectionné est nul, c'est une erreur
		if(selectedNode == null || windowInterface == null ||
			!(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Pas de noeud sélectionné !");
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On appelle la méthode de la super classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		GenericTreeObjectNode selected_node = 
			(GenericTreeObjectNode)selectedNode;
		// Le nom de l'Agent provient des arguments du processeur ou du noeud
		if(parameters != null && parameters.equals("") == false)
		{
			agent_name = parameters;
		}
		else
		{
			agent_name = selected_node.getAgentName();
		}
		// On va récupérer l'interface de l'Agent puis son état
		windowInterface.setProgressMaximum(2);
		windowInterface.setStatus("&StatusProcessor_GettingAgentStatus", 
			null, 0);
		try
		{
			PortalInterfaceProxy portal_proxy = 
				PortalInterfaceProxy.getInstance();
			AgentInterface agent_interface = 
				portal_proxy.getAgentInterface(agent_name);
			agent_status = agent_interface.getStatus();
		}
		catch(Exception exception)
		{
			InnerException inner_exception =
				CommonFeatures.processException("la récupération de l'état " +
				"de l'Agent", exception);
			// On va afficher un message d'erreur à l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotGetAgentStatus", 
				inner_exception);
			// On sort
			windowInterface.setStatus(null, null, 0);
			// On appelle la méthode close()
			close();
			trace_methods.endOfMethod();
			return;
		}
		// On va constuire le panneau
		windowInterface.setStatus("&StatusProcessor_BuildingFrame", null, 1);
		setTitle(MessageManager.getMessage("&StatusProcessor_Title") +
			agent_name);
		makePanel(agent_status);
		// On positionne les dimensions du panneau
		setMinimumSize(new Dimension(200, 150));
		setPreferredSize(new Dimension(300, 200));
		// On affiche le panneau
		display();
		windowInterface.setStatus(null, null, 0);
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
			"AgentStatusProcessor", "getDescription");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&StatusProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance de AgentStatusProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentStatusProcessor", "duplicate");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new AgentStatusProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de la fenêtre d'affichage 
	* de l'état détaillé d'un Agent. Pour cela, elle construit un tableau en 
	* utilisant l'objet IsisServerStatus passé en argument pour extraire les 
	* valeurs.
	* Le tableau permettra d'afficher tous les champs dans une fenêtre de 
	* taille relativement réduite.
	* 
	* Arguments:
	*  - agentStatus: Une référence sur un objet IsisServerStatus contenant 
	*    toutes les informations d'état de l'Agent.
 	* ----------------------------------------------------------*/
 	private void makePanel(
 		IsisServerStatus agentStatus
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentStatusProcessor", "makePanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentStatus=" + agentStatus);
		// On positionne le layout de la sous-fenêtre
		getContentPane().setLayout(new BorderLayout());
		// Les données à afficher sont contenues dans un tableau à 2 dimensions
		String[] columns = {
			MessageManager.getMessage("&StatusProcessor_Data"),
			MessageManager.getMessage("&StatusProcessor_Value")
			};
		int user_time = agentStatus.userTime.seconds * 1000 + 
			(agentStatus.userTime.useconds / 1000);
		int system_time = agentStatus.systemTime.seconds * 1000 +
			(agentStatus.systemTime.useconds / 1000);
		String[][] data = {
			{ MessageManager.getMessage("&StatusProcessor_UserTime"),
				"" + user_time + 
				MessageManager.getMessage("&StatusProcessor_ms") },
			{ MessageManager.getMessage("&StatusProcessor_SystemTime"),
				"" + system_time  + 
				MessageManager.getMessage("&StatusProcessor_ms")},
			{ MessageManager.getMessage("&StatusProcessor_PageRequests"),
				"" + agentStatus.pageRequests },
			{ MessageManager.getMessage("&StatusProcessor_pageFaults"),
				"" + agentStatus.pageFaults },
			{ MessageManager.getMessage("&StatusProcessor_AgentLoad"),
				"" + ((100 * agentStatus.currentOperations) /
				agentStatus.maxOperations) + "% (" + 
				agentStatus.currentOperations + "/" +
				agentStatus.maxOperations + ")" },
			{ MessageManager.getMessage("&StatusProcessor_ActiveSessions"),
				"" + agentStatus.activeSessions },
			{ MessageManager.getMessage("&StatusProcessor_ServicesEnvironments"),
				"" + agentStatus.iClesServices }
		};
		// Création du tableau
		NonEditableTable table = new NonEditableTable(data, columns, ";");
		table.getColumnModel().getColumn(0).setPreferredWidth(320);
		table.getColumnModel().getColumn(1).setPreferredWidth(70);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// Placement du tableau dans une zone de défilement
		JScrollPane scroll = new JScrollPane(table);
		getContentPane().add(scroll, BorderLayout.CENTER);
		// On ajoute un bouton pour permettre la fermeture de la sous-fenêtre
		JButton close_button = new JButton(
			MessageManager.getMessage("&StatusProcessor_Close"));
		// On ajoute le listener sur le click
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// Appel de la méthode close
				close();
			}
		});
		// On crée un panneau pour le bouton
		JPanel button_panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(0, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		button_panel.setLayout(layout);
		// On ajoute le bouton dans le panneau des boutons
		layout.setConstraints(close_button, constraints);
		button_panel.add(close_button);
		// Le bouton est placé dans la zone inférieure
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		trace_methods.endOfMethod();
 	}
}

