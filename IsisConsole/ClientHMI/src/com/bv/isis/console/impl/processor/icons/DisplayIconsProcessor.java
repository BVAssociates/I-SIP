/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/icons/DisplayIconsProcessor.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage des ic�nes disponibles
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
* Ajout de l'argument postprocessing � la m�thode run().
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
* Ajustement des hauteurs des lignes pour les autres ic�nes.
*
* Revision 1.1  2004/11/02 08:54:02  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.icons;

//
//Imports syst�me
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
* Cette classe impl�mente le processeur de t�che charg� de l'affichage des 
* ic�nes disponibles. Etant un processeur graphique, la classe sp�cialise 
* la classe ProcessorFrame. Ce processeur n'est destin� � �tre appel� que 
* depuis le menu "Outils" de la Console.
* La fen�tre du processeur va afficher un zone � onglet compos�e de trois 
* onglets, le premier pr�sentant les ic�nes de noeuds, le deuxi�me les ic�nes 
* de m�thodes et, enfin, le dernier les autres ic�nes.
* ----------------------------------------------------------*/
public class DisplayIconsProcessor 
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DisplayIconsProcessor
	* 
	* Description:
	* Cette m�thode est le constructeur par d�faut. Elle n'est pr�sent�e que 
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour effectuer le pr�-chargement du processeur.
	* Elle d�clenche le chargement du fichier de messages du processeur.
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
			"DisplayIconsProcessor", "isTreeCapable");
			
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
			"DisplayIconsProcessor", "isTableCapable");
			
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
			"DisplayIconsProcessor", "isGlobalCapable");
			
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
			"DisplayIconsProcessor", "getMenuLabel");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return "Icons";
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle 
	* est appel�e pour d�clencher l'ex�cution du processeur.
	* La m�thode construit la fen�tre via un appel � la m�thode makePanel(), 
	* puis s'affiche � l'�cran.
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
			"DisplayIconsProcessor", "run");
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
		// On positionne le titre de la fen�tre
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer la description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
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
	* Cette m�thode est charg�e de la construction de la fen�tre du processeur. 
	* Elle va construire une zone � onglet constitu�e de trois onglets. Chaque 
	* onglet va contenir un tableau � deux colonnes, l'une pour l'ic�ne, 
	* l'autre pour l'identifiant de l'ic�ne.
	* Les onglets sont les suivants:
	*  - Ic�nes de noeuds: Pour les ic�nes de format 13x13, utilisables pour 
	*    les noeuds d'exploration,
	*  - Ic�nes de m�thodes: Pour les ic�nes de format 24x24, utilisables pour 
	*    les m�thodes des menus contextuels,
	*  - Autres: Pour les ic�nes de format diff�rent de ceux cit�s ci-dessus.
	* 
	* Les trois listes d'ic�nes sont r�cup�r�es gr�ce � la m�thode 
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
		// On commence par r�cup�rer les listes d'ic�nes
		getMainWindowInterface().setStatus("&Icons_GettingList", null, 0);
		ConsoleIconsManager.getAllIcons(node_icons, method_icons, other_icons, heights);
		getMainWindowInterface().setStatus("&Icons_BuildingTable", null, 1);
		// On cr�e le gestionnaire d'affichage d'ic�ne
		IconCellRenderer renderer = new IconCellRenderer();
		// On va positionner le gestionnaire de layout
		getContentPane().setLayout(new BorderLayout());
		// On va cr�er la zone � onglets
		JTabbedPane tabbed_pane = new JTabbedPane();
		// Cr�ation de la liste des colonnes, valable pour tous tableaux
		columns.add(MessageManager.getMessage("&Icons_Icon"));
		columns.add(MessageManager.getMessage("&Icons_Id"));
		// On va cr�er l'onglet des ic�nes de noeuds
		JPanel panel = new JPanel(new BorderLayout());
		// On va cr�er le tableau
		NonEditableTable table = 
			new NonEditableTable(node_icons, columns, ";");
		// On r�gle quelques param�tres
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setPreferredWidth(280);
		table.setRowHeight(17);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_LAST_COLUMN);
		// On place la table dans une zone � d�filement
		JScrollPane scroll = new JScrollPane(table);
		// On place le tableau au mileu de la fen�tre
		panel.add(scroll, BorderLayout.CENTER);
		// On ajoute le panneau en tant qu'onglet
		tabbed_pane.addTab(MessageManager.getMessage("&Icons_NodeIcons"), panel);
		// On va cr�er l'onglet des ic�nes de m�thodes
		panel = new JPanel(new BorderLayout());
		// On va cr�er le tableau
		table = new NonEditableTable(method_icons, columns, ";");
		// On r�gle quelques param�tres
		table.getColumnModel().getColumn(0).setPreferredWidth(35);
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setPreferredWidth(265);
		table.setRowHeight(28);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_LAST_COLUMN);
		// On place la table dans une zone � d�filement
		scroll = new JScrollPane(table);
		// On place le tableau au mileu de la fen�tre
		panel.add(scroll, BorderLayout.CENTER);
		// On ajoute le panneau en tant qu'onglet
		tabbed_pane.addTab(MessageManager.getMessage("&Icons_MethodIcons"), panel);
		// On va cr�er l'onglet des autres ic�nes
		panel = new JPanel(new BorderLayout());
		// On va cr�er le tableau
		table = new NonEditableTable(other_icons, columns, ";");
		// On r�gle quelques param�tres
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		for(int index = 0 ; index < heights.size() ; index ++)
		{
			table.setRowHeight(index, 
				((Integer)heights.elementAt(index)).intValue() + 4);
		}
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_LAST_COLUMN);
		// On place la table dans une zone � d�filement
		scroll = new JScrollPane(table);
		// On place le tableau au mileu de la fen�tre
		panel.add(scroll, BorderLayout.CENTER);
		// On ajoute le panneau en tant qu'onglet
		tabbed_pane.addTab(MessageManager.getMessage("&Icons_OtherIcons"), panel);
		// On ajoute la zone � onglets dans la partie centrale
		getContentPane().add(tabbed_pane, BorderLayout.CENTER);

		// Maintenant, on va cr�er le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Icons_Close"));
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
		trace_methods.endOfMethod();
	}
}
