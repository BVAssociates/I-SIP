/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/icons/DisplayIconsProcessor.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage des icônes disponibles
* DATE:        29/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.icons
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DisplayIconsProcessor.java,v $
* Revision 1.8  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.7  2008/02/21 12:09:04  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.6  2008/01/31 16:57:10  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.5  2006/03/13 15:14:25  tz
* Utilisation de la classe IconCellRenderer.
*
* Revision 1.4  2005/12/23 13:22:17  tz
* Correction mineure.
*
* Revision 1.3  2005/07/01 12:13:19  tz
* Modification du composant pour les traces
*
* Revision 1.2  2004/11/03 15:18:13  tz
* Ajustement des hauteurs des lignes pour les autres icônes.
*
* Revision 1.1  2004/11/02 08:54:02  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.icons;

//
//Imports système
//
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import java.util.Vector;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.gui.NonEditableTable;
import com.bv.isis.console.core.common.ConsoleIconsManager;
import com.bv.isis.console.core.common.IconCellRenderer;

/*----------------------------------------------------------
* Nom: DisplayIconsProcessor
* 
* Description:
* Cette classe implémente le processeur de tâche chargé de l'affichage des 
* icônes disponibles. Etant un processeur graphique, la classe spécialise 
* la classe ProcessorFrame. Ce processeur n'est destiné à être appelé que 
* depuis le menu "Outils" de la Console.
* La fenêtre du processeur va afficher un zone à onglet composée de trois 
* onglets, le premier présentant les icônes de noeuds, le deuxième les icônes 
* de méthodes et, enfin, le dernier les autres icônes.
* ----------------------------------------------------------*/
public class DisplayIconsProcessor 
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DisplayIconsProcessor
	* 
	* Description:
	* Cette méthode est le constructeur par défaut. Elle n'est présentée que 
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public DisplayIconsProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayIconsProcessor", "DisplayIconsProcessor");
			
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
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayIconsProcessor", "preLoad");
			
		trace_methods.beginningOfMethod();
		MessageManager.loadFile("icons.mdb", "UTF8");
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
			"DisplayIconsProcessor", "isTreeCapable");
			
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
			"DisplayIconsProcessor", "isTableCapable");
			
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
			"DisplayIconsProcessor", "isGlobalCapable");
			
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
			"DisplayIconsProcessor", "getMenuLabel");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return "Icons";
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour déclencher l'exécution du processeur.
	* La méthode construit la fenêtre via un appel à la méthode makePanel(), 
	* puis s'affiche à l'écran.
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
			"DisplayIconsProcessor", "run");
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
		// On positionne le titre de la fenêtre
		setTitle(MessageManager.getMessage("&Icons_Title"));
		windowInterface.setProgressMaximum(2);
		// On construit le panneau
		makePanel();
		// On l'affiche
		setMinimumSize(new Dimension(200, 150));
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
			"DisplayIconsProcessor", "getDescription");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&IconsProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance de DisplayIconsProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayIconsProcessor", "duplicate");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new DisplayIconsProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de la fenêtre du processeur. 
	* Elle va construire une zone à onglet constituée de trois onglets. Chaque 
	* onglet va contenir un tableau à deux colonnes, l'une pour l'icône, 
	* l'autre pour l'identifiant de l'icône.
	* Les onglets sont les suivants:
	*  - Icônes de noeuds: Pour les icônes de format 13x13, utilisables pour 
	*    les noeuds d'exploration,
	*  - Icônes de méthodes: Pour les icônes de format 24x24, utilisables pour 
	*    les méthodes des menus contextuels,
	*  - Autres: Pour les icônes de format différent de ceux cités ci-dessus.
	* 
	* Les trois listes d'icônes sont récupérées grâce à la méthode 
	* getAllIconIdentifiers().
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayIconsProcessor", "makePanel");
		Vector node_icons = new Vector();
		Vector method_icons = new Vector();
		Vector other_icons = new Vector();
		Vector heights = new Vector();
		Vector columns = new Vector();

		trace_methods.beginningOfMethod();
		// On commence par récupérer les listes d'icônes
		getMainWindowInterface().setStatus("&Icons_GettingList", null, 0);
		ConsoleIconsManager.getAllIcons(node_icons, method_icons, other_icons, heights);
		getMainWindowInterface().setStatus("&Icons_BuildingTable", null, 1);
		// On crée le gestionnaire d'affichage d'icône
		IconCellRenderer renderer = new IconCellRenderer();
		// On va positionner le gestionnaire de layout
		getContentPane().setLayout(new BorderLayout());
		// On va créer la zone à onglets
		JTabbedPane tabbed_pane = new JTabbedPane();
		// Création de la liste des colonnes, valable pour tous tableaux
		columns.add(MessageManager.getMessage("&Icons_Icon"));
		columns.add(MessageManager.getMessage("&Icons_Id"));
		// On va créer l'onglet des icônes de noeuds
		JPanel panel = new JPanel(new BorderLayout());
		// On va créer le tableau
		NonEditableTable table = 
			new NonEditableTable(node_icons, columns, ";");
		// On règle quelques paramètres
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setPreferredWidth(280);
		table.setRowHeight(17);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_LAST_COLUMN);
		// On place la table dans une zone à défilement
		JScrollPane scroll = new JScrollPane(table);
		// On place le tableau au mileu de la fenêtre
		panel.add(scroll, BorderLayout.CENTER);
		// On ajoute le panneau en tant qu'onglet
		tabbed_pane.addTab(MessageManager.getMessage("&Icons_NodeIcons"), panel);
		// On va créer l'onglet des icônes de méthodes
		panel = new JPanel(new BorderLayout());
		// On va créer le tableau
		table = new NonEditableTable(method_icons, columns, ";");
		// On règle quelques paramètres
		table.getColumnModel().getColumn(0).setPreferredWidth(35);
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setPreferredWidth(265);
		table.setRowHeight(28);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_LAST_COLUMN);
		// On place la table dans une zone à défilement
		scroll = new JScrollPane(table);
		// On place le tableau au mileu de la fenêtre
		panel.add(scroll, BorderLayout.CENTER);
		// On ajoute le panneau en tant qu'onglet
		tabbed_pane.addTab(MessageManager.getMessage("&Icons_MethodIcons"), panel);
		// On va créer l'onglet des autres icônes
		panel = new JPanel(new BorderLayout());
		// On va créer le tableau
		table = new NonEditableTable(other_icons, columns, ";");
		// On règle quelques paramètres
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		for(int index = 0 ; index < heights.size() ; index ++)
		{
			table.setRowHeight(index, 
				((Integer)heights.elementAt(index)).intValue() + 4);
		}
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_LAST_COLUMN);
		// On place la table dans une zone à défilement
		scroll = new JScrollPane(table);
		// On place le tableau au mileu de la fenêtre
		panel.add(scroll, BorderLayout.CENTER);
		// On ajoute le panneau en tant qu'onglet
		tabbed_pane.addTab(MessageManager.getMessage("&Icons_OtherIcons"), panel);
		// On ajoute la zone à onglets dans la partie centrale
		getContentPane().add(tabbed_pane, BorderLayout.CENTER);

		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Icons_Close"));
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
		trace_methods.endOfMethod();
	}
}
