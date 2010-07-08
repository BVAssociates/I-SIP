/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/define/DefineProcessor.java,v $
* $Revision: 1.19 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage de la définition d'une table
* DATE:        26/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.define
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DefineProcessor.java,v $
* Revision 1.19  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.18  2008/02/21 12:07:27  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.17  2008/01/31 16:54:11  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.16  2006/03/13 15:14:12  tz
* Utilisation de la classe IconCellRenderer.
*
* Revision 1.15  2005/12/23 13:18:10  tz
* Correction mineure.
*
* Revision 1.14  2005/10/07 08:30:30  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.13  2005/07/01 12:19:38  tz
* Modification du composant pour les traces
*
* Revision 1.12  2004/11/02 08:56:09  tz
* Gestion des leasings sur les définitions.
*
* Revision 1.11  2004/10/22 15:40:14  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.10  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.9  2004/10/13 13:56:36  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.8  2004/10/06 07:37:07  tz
* Amélioration de la dimension de la frame,
* Correction de la fiche Inuit/138,
* Utilisation du SortCellRenderer pour un rendu du sens de tri.
*
* Revision 1.7  2004/07/29 12:15:50  tz
* Mise à jour de la documentation
*
* Revision 1.6  2002/11/22 15:28:41  tz
* Cloture IT1.0.7
* Ajout du champ "Non null ?"
*
* Revision 1.5  2002/11/19 08:43:47  tz
* Correction de la fiche Inuit/77.
*
* Revision 1.4  2002/04/05 15:50:25  tz
* Cloture itération IT1.2
*
* Revision 1.3  2002/03/27 09:51:11  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture itération IT1.0.1
*
* Revision 1.1  2001/12/26 16:57:51  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.define;

//
// Imports système
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
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import com.bv.core.util.UtilStringTokenizer;
import javax.swing.table.TableCellRenderer;

//
// Imports du projet
//
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.corbacom.IsisTableColumn;
import com.bv.isis.corbacom.IsisForeignKey;
import com.bv.isis.corbacom.IsisNodeLabel;
import com.bv.isis.console.core.gui.NonEditableTable;
import com.bv.isis.console.impl.processor.display.BooleanCellRenderer;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.IconCellRenderer;

/*----------------------------------------------------------
* Nom: DefineProcessor
*
* Description:
* Cette classe implémente le processeur de tâche chargé de l'affichage de la
* définition de la table associée au noeud sélectionné.
* La définition d'une table contient les informations suivantes:
*  - Le chemin du fichier dictionnaire sur la plate-forme,
*  - Le nom de la table,
*  - Le séparateur des colonnes,
*  - La source des données,
*  - L'entête de la table,
*  - Le type de table,
*  - Le propriétaire de la table,
*  - La liste des colonnes,
*  - La liste des colonnes constituant la clé primaire,
*  - La liste des colonnes participant au tri,
*  - La liste des clés étrangères,
*  - La liste des libellés.
*
* Ces informations sont présentées par onglet, suivant un regroupement logique
* (informations générales, sur les colonnes, sur les liens, sur les libellés).
* ----------------------------------------------------------*/
public class DefineProcessor
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DefineProcessor
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présentée que
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public DefineProcessor()
	{
		super(true);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "DefineProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette méthode redéfini celle de la classe ProcessorFrame. Elle est appelée
	* par le ProcessManager afin d'initialiser et de d'exécuter le processeur.
	* Le panneau est construit, via la méthode makePanel(), puis la sous-fenêtre
	* est affichée.
	*
	* Si un problème est détecté durant la phase d'initialisation, l'exception
	* InnerException doit être levée.
	*
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface
	*    permettant au processeur d'interagir avec la fenêtre principale,
	*  - menuItem: Une référence sur l'objet JMenuItem par lequel le processeur
	*    a été exécuté. Cet argument peut être nul,
	*  - parameters: Une chaîne de caractère contenant des paramètres
	*    spécifiques au processeur. Cet argument peut être nul,
	*  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	*  - selectedNode: Une référence sur le noeud sélectionné. Cet argument
	*    peut être nul.
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
			"DefineProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Tout d'abord, on vérifie l'intégrité des arguments
		if(windowInterface == null || selectedNode == null ||
		   !(selectedNode instanceof GenericTreeObjectNode))
		{
			// C'est une erreur.
			trace_errors.writeTrace("Un des arguments n'est pas valide !");
			// On lève une exception
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On appelle la méthode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On caste le noeud
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		if(selected_node instanceof GenericTreeClassNode)
		{
			setTitle(MessageManager.getMessage("&Define_Title") +
				selected_node.getLabel().label);
		}
		else
		{
			GenericTreeObjectNode parent_node =
				(GenericTreeObjectNode)selectedNode.getParent();
			if(parent_node != null &&
				parent_node instanceof GenericTreeClassNode &&
				parent_node.getTableName().equals(selected_node.getTableName()) == true)
			{
				setTitle(MessageManager.getMessage("&Define_Title") +
					parent_node.getLabel().label);
			}
			else
			{
				setTitle(MessageManager.getMessage("&Define_Title") +
					selected_node.getTableName());
			}
		}
		// On construit le panneau
		makePanel();
		// On l'affiche
		display();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle
	* est appelée pour effectuer un pré-chargement du processeur.
	* Elle charge le fichier de messages du processeur.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("define.mdb", "UTF8");
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
			"DefineProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&DefineProcessorDescription");
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
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new DefineProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette méthode est appelée par la méthode run() pour construire le
	* panneau de la sous-fenêtre. Le panneau est construit en plusieurs onglets,
	* afin d'afficher les informations de définition de la table correspondant
	* au noeud sélectionné.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "makePanel");

		trace_methods.beginningOfMethod();
		// La première chose à faire est de récupérer le noeud sélectionné
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)getSelectedNode();
		// Ensuite, il faut récupérer la définition de la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IsisTableDefinition table_definition =
			manager.getTableDefinition(selected_node.getAgentName(),
			selected_node.getIClesName(), selected_node.getServiceType(),
			selected_node.getDefinitionFilePath());
		// On vérifie qu'il y a bien une définition
		if(table_definition == null)
		{
			// C'est une erreur
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Il n'y a pas de définition pour la table !");
			// On affiche un message d'erreur à l'utilisateur
			InnerException exception =
				new InnerException("&ERR_NoDefinitionForTable", null, null);
			getMainWindowInterface().showPopupForException(
				"&ERR_ProcessorInitialisationError", exception);
			// On sort
			trace_methods.endOfMethod();
			close();
			return;
		}
		// On crée la barre à onglets
		JTabbedPane tabbed_pane = new JTabbedPane();
		// On crée l'onglet général
		JPanel general_panel = makeGeneralPanel(table_definition);
		tabbed_pane.add(MessageManager.getMessage("&Define_General"),
			general_panel);
		// On crée l'onglet des colonnes
		JPanel columns_panel = makeColumnsPanel(table_definition);
		tabbed_pane.add(MessageManager.getMessage("&Define_Columns"),
			columns_panel);
		// On crée l'onglet des liens
		JPanel links_panel = makeLinksPanel(table_definition);
		tabbed_pane.add(MessageManager.getMessage("&Define_Links"),
			links_panel);
		// On crée l'onglet des libellés
		JPanel methods_panel = makeLabelsPanel(table_definition);
		tabbed_pane.add(MessageManager.getMessage("&Define_Labels"),
			methods_panel);
		// On libère l'utilisation de la définition
		manager.releaseTableDefinitionLeasing(table_definition);

		// On place la barre à onglet dans la zone centrale de la fenêtre
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabbed_pane, BorderLayout.CENTER);

		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Define_Close"));
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
		setMinimumSize(new Dimension(565, 222));
		setPreferredSize(getMinimumSize());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makeGeneralPanel
	*
	* Description:
	* Cette méthode est appelée par la méthode makePanel() afin de construire
	* l'onglet présentant les informations générales de la table.
	* Ces informations sont:
	*  - Le chemin du fichier dictionnaire sur la plate-forme,
	*  - Le nom de la table,
	*  - Le séparateur des colonnes,
	*  - La source des données,
	*  - L'entête de la table,
	*  - Le type de table,
	*  - Le propriétaire de la table.
	*
	* Arguments:
	*  - tableDefinition: Une référence sur l'objet IsisTableDefinition qui
	*    contient la définition de la table.
	*
	* Retourne: Une instance de JPanel représentant le contenu de l'onglet
	* Général.
	* ----------------------------------------------------------*/
	private JPanel makeGeneralPanel(
		IsisTableDefinition tableDefinition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "makeGeneralPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		// Le panneau général ne contient qu'un tableau affichant les
		// informations générales
		// On crée les colonnes
		String[] columns = {
			MessageManager.getMessage("&Define_Name"),
			MessageManager.getMessage("&Define_Value")
		};
		// On crée les données
		String[][] data = new String[7][2];
		// Chemin du dictionnaire
		data[0][0] = MessageManager.getMessage("&Define_FilePath");
		data[0][1] = tableDefinition.definitionFilePath ;
		// Le nom de la table
		data[1][0] = MessageManager.getMessage("&Define_TableName");
		data[1][1] = tableDefinition.tableName;
		// Le séparateur des colonnes
		data[2][0] = MessageManager.getMessage("&Define_Separator");
		if(tableDefinition.separator.equals(" ") == true)
		{
			data[2][1] = MessageManager.getMessage("&Define_Space");
		}
		else if(tableDefinition.separator.equals("\t") == true)
		{
			data[2][1] = MessageManager.getMessage("&Define_Tabulation");
		}
		else
		{
		    data[2][1] = tableDefinition.separator;
		}
		// La source des données
		data[3][0] = MessageManager.getMessage("&Define_Source");
		data[3][1] = tableDefinition.source;
		// L'entête de la table
		data[4][0] = MessageManager.getMessage("&Define_Header");
		data[4][1] = tableDefinition.header;
		// Le type de table
		data[5][0] = MessageManager.getMessage("&Define_Type");
		data[5][1] = tableDefinition.type;
		// Le propriétaire de la table
		data[6][0] = MessageManager.getMessage("&Define_Owner");
		data[6][1] = tableDefinition.owner;
		// On crée le tableau
		NonEditableTable table = new NonEditableTable(data, columns, ";");
		// On règle quelques paramètres
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(400);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// On place la table dans une zone à défilement
		JScrollPane scroll = new JScrollPane(table);
		// On crée le panneau
		JPanel general_panel = new JPanel(new BorderLayout());
		// On ajoute la zone de défilement dans le panneau
		general_panel.add(scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return general_panel;
	}

	/*----------------------------------------------------------
	* Nom: makeColumnsPanel
	*
	* Description:
	* Cette méthode est appelée par la méthode makePanel() afin de construire
	* l'onglet présentant les informations sur les colonnes de la table.
	* Ces informations sont:
	*  - La liste des colonnes,
	*  - La liste des colonnes constituant la clé primaire,
	*  - La liste des colonnes participant au tri,
	*  - La liste des colonnes non nulles.
	*
	* Arguments:
	*  - tableDefinition: Une référence sur l'objet IsisTableDefinition qui
	*    contient la définition de la table.
	*
	* Retourne: Une instance de JPanel représentant le contenu de l'onglet
	* Colonnes.
	* ----------------------------------------------------------*/
	private JPanel makeColumnsPanel(
		IsisTableDefinition tableDefinition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "makeColumnsPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		// La liste des colonnes est affichée dans un tableau
		// Il va présenter les informations suivantes:
		// - Nom de la colonne,
		// - Taille (et type),
		// - Clé ?
		// - Ordre de tri.
		// On crée la liste des colonnes du tableau
		String[] columns = {
			MessageManager.getMessage("&Define_Name"),
			MessageManager.getMessage("&Define_Size"),
			MessageManager.getMessage("&Define_IsKey"),
			MessageManager.getMessage("&Define_SortOrder"),
			MessageManager.getMessage("&Define_IsNotNull")
		};
		// On crée la liste des données
		Object[][] data = new Object[tableDefinition.columns.length][5];
		//String[][] data = new String[tableDefinition.columns.length][5];
		// On remplit la table colonne par colonne
		for(int index = 0 ; index < tableDefinition.columns.length ; index ++)
		{
			IsisTableColumn column = tableDefinition.columns[index];
			data[index][0] = column.name;
			data[index][1] = "" + column.size + column.type;
			data[index][2] = new Boolean(isColumnInKey(column.name, tableDefinition));
			data[index][3] = new Integer(getColumnSortIndex(column.name, tableDefinition));
			data[index][4] = new Boolean(!canColumnBeNull(column.name, tableDefinition));
		}

		final BooleanCellRenderer boolean_renderer = new BooleanCellRenderer();
		final SortCellRenderer sort_renderer = new SortCellRenderer();
		// On crée le tableau
		NonEditableTable table = new NonEditableTable(data, columns, ";"){
			public TableCellRenderer getCellRenderer(int row, int column)
			{
				if(column == 2 || column == 4)
				{
					return boolean_renderer;
				}
				else if(column == 3)
				{
					return sort_renderer;
				}
				else
				{
					return super.getCellRenderer(row, column);
				}
			}
		};
		// On règle quelques paramètres
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// On place la table dans une zone à défilement
		JScrollPane scroll = new JScrollPane(table);
		// On crée le panneau
		JPanel columns_panel = new JPanel(new BorderLayout());
		// On ajoute la zone de défilement dans le panneau
		columns_panel.add(scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return columns_panel;
	}

	/*----------------------------------------------------------
	* Nom: makeLinksPanel
	*
	* Description:
	* Cette méthode est appelée par la méthode makePanel() afin de construire
	* l'onglet présentant les informations sur les liens de la table.
	* Ces informations sont:
	*  - La liste des clés étrangères.
	*
	* Arguments:
	*  - tableDefinition: Une référence sur l'objet IsisTableDefinition qui
	*    contient la définition de la table.
	*
	* Retourne: Une instance de JPanel représentant le contenu de l'onglet
	* Liens.
	* ----------------------------------------------------------*/
	private JPanel makeLinksPanel(
		IsisTableDefinition tableDefinition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "makeLinksPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		// Tout d'abord, créer le tableau des clés étrangères
		String[] columns = { "" };
		// Les données sont construites à partir des clés étrangères
		String[][] data = new String[tableDefinition.foreignKeys.length][1];
		for(int index = 0 ; index < tableDefinition.foreignKeys.length ;
			index ++)
		{
			// On remplit le tableau de données
			data[index][0] = getForeignKeyString(
				tableDefinition.foreignKeys[index]);
		}
		// Maintenant, on crée le tableau
		NonEditableTable foreign_table = new NonEditableTable(data, columns, ";");
		// On règle les paramètres du tableau
		foreign_table.getColumnModel().getColumn(0).setPreferredWidth(500);
		foreign_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		foreign_table.setTableHeader(null);
		// Placement du tableau dans une zone de défilement
		JScrollPane foreign_scroll = new JScrollPane(foreign_table);
		// Ajout d'une bordure à la zone de défilement
		foreign_scroll.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Define_ForeignKeys")));

		// Maintenant, on crée le panneau qui contiendra le tableau
		JPanel links_panel = new JPanel(new BorderLayout());
		links_panel.add(foreign_scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return links_panel;
	}

	/*----------------------------------------------------------
	* Nom: makeLabelsPanel
	*
	* Description:
	* Cette méthode est appelée par la méthode makePanel() afin de construire
	* l'onglet présentant les informations sur les libellés de la table.
	*
	* Arguments:
	*  - tableDefinition: Une référence sur l'objet IsisTableDefinition qui
	*    contient la définition de la table.
	*
	* Retourne: Une instance de JPanel représentant le contenu de l'onglet
	* Libellés.
	* ----------------------------------------------------------*/
	private JPanel makeLabelsPanel(
		IsisTableDefinition tableDefinition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "makeLabelsPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		IconCellRenderer renderer = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		// On construit le gestionnaire d'affichage des icônes
		renderer = new IconCellRenderer();
		// On construit la liste des colonnes
		String[] columns = {
			MessageManager.getMessage("&Define_NodeIdentity"),
			MessageManager.getMessage("&Define_Label"),
			MessageManager.getMessage("&Define_Icon")
		};
		// On crée les données
		String[][] data = new String[tableDefinition.labels.length][3];
		// On remplit le tableau des données
		for(int index = 0 ; index < tableDefinition.labels.length ; index ++)
		{
			IsisNodeLabel label = tableDefinition.labels[index];
			data[index][0] = label.nodeIdentity;
			data[index][1] = label.label;
			data[index][2] = label.icon;
		}

		// Maintenant on crée le tableau
		NonEditableTable table = new NonEditableTable(data, columns, ";");
		// On règle quelques paramètres
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(25);
		table.getColumnModel().getColumn(2).setCellRenderer(renderer);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// On place la table dans une zone à défilement
		JScrollPane scroll = new JScrollPane(table);
		// On crée le panneau
		JPanel labels_panel = new JPanel(new BorderLayout());
		// On ajoute la zone de défilement dans le panneau
		labels_panel.add(scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return labels_panel;
	}

	/*----------------------------------------------------------
	* Nom: isColumnInKey
	*
	* Description:
	* Cette méthode permet de savoir si une colonne, dont le nom est passé en
	* argument, fait partie de la clé de la table.
	* La clé de la table (attribut key de l'objet IsisTableDefinition) est
	* constitué d'une liste de colonnes.
	*
	* Arguments:
	*  - columnName: Le nom de la colonne dont on veut connaître l'appartenance
	*    à la clé,
	*  - tableDefinition: Une référence sur l'objet IsisTableDefinition
	*    contenant la définition de la table.
	*
	* Retourne: true si la colonne fait partie de la clé, false sinon.
	* ----------------------------------------------------------*/
	private boolean isColumnInKey(
		String columnName,
		IsisTableDefinition tableDefinition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "isColumnInKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("columnName=" + columnName);
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		// Il faut scruter la liste des colonnes constituant la clé pour savoir
		// si la colonne passée en argument en fait partie
		for(int index = 0 ; index < tableDefinition.key.length ; index ++)
		{
			if(tableDefinition.key[index].equals(columnName) == true)
			{
				trace_methods.endOfMethod();
				return true;
			}
		}
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: getColumnSortIndex
	*
	* Description:
	* Cette méthode permet de récupérer la position d'une colonne, dont le nom
	* est passé en argument, dans l'ordre de tri. Si la colonne ne fait pas
	* partie des colonnes de tri, la méthode retourne 0.
	* La colonne la plus discriminante aura la position +/-1, sachant que le signe
	* indique le caractère ascendant (+) ou descendant (-) du tri.
	*
	* Arguments:
	*  - columnName: Le nom de la colonne dont on veut la position dans l'ordre
	*    de tri,
	*  - tableDefinition: Une référence sur l'objet IsisTableDefinition
	*    contenant la définition de la table.
	*
	* Retourne: La position de la colonne dans l'ordre de tri, commençant à 1,
	* ou 0 si la colonne ne fait pas partie de l'ordre de tri.
	* ----------------------------------------------------------*/
	private int getColumnSortIndex(
		String columnName,
		IsisTableDefinition tableDefinition
		)
	{
		int return_value = 0;
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "getColumnSortIndex");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("columnName=" + columnName);
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		if(tableDefinition.sort == null)
		{
			trace_methods.endOfMethod();
			return return_value;
		}
		// Il faut scruter la liste des colonnes constituant le tri pour savoir
		// si la colonne passée en argument en fait partie
		for(int index = 0 ; index < tableDefinition.sort.length ; index ++)
		{
            String sort_column = tableDefinition.sort[index];

            while (sort_column.charAt(0) == ' ') {
                sort_column = sort_column.substring(1);
            }

			// On va découper l'ordre de tri sur " "
			UtilStringTokenizer tokenizer =
				new UtilStringTokenizer(sort_column, " ");
			// Si la première sous-chaîne vaut la colonne, c'est bon
			if(tokenizer.getToken(0).equals(columnName) == false)
			{
				continue;
			}
			// Il s'agit de la bonne colonne. Est-elle ascendante ou
			// descendante ?
			if(tokenizer.getTokensCount() == 2 &&
				tokenizer.getToken(1).equalsIgnoreCase("DESC") == true)
			{
				// Il s'agit d'un champ descandant, on retourne la position
				// en négatif
				return_value = -1 * (index + 1);
			}
			else
			{
				// Il s'agit d'un champ ascendant, on retourne la position
				return_value = index + 1;
			}
		}
		trace_methods.endOfMethod();
		return return_value;
	}

	/*----------------------------------------------------------
	* Nom: canColumnBeNull
	*
	* Description:
	* Cette méthode permet de savoir si la colonne passée en argument
	* peut être nulle ou non.
	* La colonne peut être nulle si elle ne figure pas dans le champ
	* notNull de la définition de la table.
	*
	* Arguments:
	*  - columnName: Le nom de la colonne dont on connaître le caractère
	*    non nul,
	*  - tableDefinition: Une référence sur l'objet IsisTableDefinition
	*    contenant la définition de la table.
	*
	* Retourne: true si la colonne peut être nulle, ou false.
	* ----------------------------------------------------------*/
	private boolean canColumnBeNull(
		String columnName,
		IsisTableDefinition tableDefinition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "canColumnBeNull");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("columnName=" + columnName);
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		if(tableDefinition.notNull == null)
		{
			trace_methods.endOfMethod();
			return true;
		}
		// Il faut scruter la liste des colonnes du champ notnull
		for(int index = 0 ; index < tableDefinition.notNull.length ; index ++)
		{
			if(tableDefinition.notNull[index].equals(columnName) == true)
			{
				trace_methods.endOfMethod();
				return false;
			}
		}
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getForeignKeyString
	*
	* Description:
	* Cette méthode permet de convertir une clé étrangère passée en argument en
	* une chaîne représentant cette clé avec la syntaxe utilisée dans les
	* dictionnaires.
	* La syntaxe est du type:
	* [<lcol1>,...] on <table>[<fcol1>,...].
	*
	* Cette information est affichée dans l'onglet Liens de la sous-fenêtre de
	* définition de table.
	*
	* Arguments:
	*  - foreignKey: Une référence sur la clé étrangère, sous forme de
	*    IsisForeignKey.
	*
	* Retourne: Une chaîne de caractères contenant la clé étrangère.
	* ----------------------------------------------------------*/
	private String getForeignKeyString(IsisForeignKey foreignKey)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "getForeignKeyString");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		StringBuffer local_buffer = new StringBuffer();
		StringBuffer foreign_buffer = new StringBuffer();

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("foreignKey=" + foreignKey);
		// On reconstruit la syntaxe de clé étrangère
		local_buffer.append("[");
		foreign_buffer.append("[");
		// On ajoute les colonnes de la clé
		for(int count = 0 ; count < foreignKey.links.length ; count ++)
		{
			if(count > 0)
			{
				local_buffer.append(",");
				foreign_buffer.append(",");
			}
			local_buffer.append(foreignKey.links[count].localColumnName);
			foreign_buffer.append(
				foreignKey.links[count].foreignColumnName);
		}
		local_buffer.append("]");
		foreign_buffer.append("]");
		// On remplit le tableau de données
		trace_methods.endOfMethod();
		return local_buffer.toString() + " on " +
			foreignKey.foreignTableName + foreign_buffer.toString();
	}
}