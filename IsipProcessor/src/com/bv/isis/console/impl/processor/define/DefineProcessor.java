/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/define/DefineProcessor.java,v $
* $Revision: 1.19 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'affichage de la d�finition d'une table
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
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.16  2006/03/13 15:14:12  tz
* Utilisation de la classe IconCellRenderer.
*
* Revision 1.15  2005/12/23 13:18:10  tz
* Correction mineure.
*
* Revision 1.14  2005/10/07 08:30:30  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.13  2005/07/01 12:19:38  tz
* Modification du composant pour les traces
*
* Revision 1.12  2004/11/02 08:56:09  tz
* Gestion des leasings sur les d�finitions.
*
* Revision 1.11  2004/10/22 15:40:14  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.10  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.9  2004/10/13 13:56:36  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.8  2004/10/06 07:37:07  tz
* Am�lioration de la dimension de la frame,
* Correction de la fiche Inuit/138,
* Utilisation du SortCellRenderer pour un rendu du sens de tri.
*
* Revision 1.7  2004/07/29 12:15:50  tz
* Mise � jour de la documentation
*
* Revision 1.6  2002/11/22 15:28:41  tz
* Cloture IT1.0.7
* Ajout du champ "Non null ?"
*
* Revision 1.5  2002/11/19 08:43:47  tz
* Correction de la fiche Inuit/77.
*
* Revision 1.4  2002/04/05 15:50:25  tz
* Cloture it�ration IT1.2
*
* Revision 1.3  2002/03/27 09:51:11  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.1  2001/12/26 16:57:51  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.define;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur de t�che charg� de l'affichage de la
* d�finition de la table associ�e au noeud s�lectionn�.
* La d�finition d'une table contient les informations suivantes:
*  - Le chemin du fichier dictionnaire sur la plate-forme,
*  - Le nom de la table,
*  - Le s�parateur des colonnes,
*  - La source des donn�es,
*  - L'ent�te de la table,
*  - Le type de table,
*  - Le propri�taire de la table,
*  - La liste des colonnes,
*  - La liste des colonnes constituant la cl� primaire,
*  - La liste des colonnes participant au tri,
*  - La liste des cl�s �trang�res,
*  - La liste des libell�s.
*
* Ces informations sont pr�sent�es par onglet, suivant un regroupement logique
* (informations g�n�rales, sur les colonnes, sur les liens, sur les libell�s).
* ----------------------------------------------------------*/
public class DefineProcessor
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DefineProcessor
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�fini celle de la classe ProcessorFrame. Elle est appel�e
	* par le ProcessManager afin d'initialiser et de d'ex�cuter le processeur.
	* Le panneau est construit, via la m�thode makePanel(), puis la sous-fen�tre
	* est affich�e.
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
		// Tout d'abord, on v�rifie l'int�grit� des arguments
		if(windowInterface == null || selectedNode == null ||
		   !(selectedNode instanceof GenericTreeObjectNode))
		{
			// C'est une erreur.
			trace_errors.writeTrace("Un des arguments n'est pas valide !");
			// On l�ve une exception
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On appelle la m�thode de la super-classe
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
	* Cette m�thode red�finit celle de la super-classe ProcessorFrame. Elle
	* est appel�e pour effectuer un pr�-chargement du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle
	* est appel�e pour r�cup�rer la description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle
	* est appel�e pour r�cup�rer un double du processeur.
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
	* Cette m�thode est appel�e par la m�thode run() pour construire le
	* panneau de la sous-fen�tre. Le panneau est construit en plusieurs onglets,
	* afin d'afficher les informations de d�finition de la table correspondant
	* au noeud s�lectionn�.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DefineProcessor", "makePanel");

		trace_methods.beginningOfMethod();
		// La premi�re chose � faire est de r�cup�rer le noeud s�lectionn�
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)getSelectedNode();
		// Ensuite, il faut r�cup�rer la d�finition de la table
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IsisTableDefinition table_definition =
			manager.getTableDefinition(selected_node.getAgentName(),
			selected_node.getIClesName(), selected_node.getServiceType(),
			selected_node.getDefinitionFilePath());
		// On v�rifie qu'il y a bien une d�finition
		if(table_definition == null)
		{
			// C'est une erreur
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Il n'y a pas de d�finition pour la table !");
			// On affiche un message d'erreur � l'utilisateur
			InnerException exception =
				new InnerException("&ERR_NoDefinitionForTable", null, null);
			getMainWindowInterface().showPopupForException(
				"&ERR_ProcessorInitialisationError", exception);
			// On sort
			trace_methods.endOfMethod();
			close();
			return;
		}
		// On cr�e la barre � onglets
		JTabbedPane tabbed_pane = new JTabbedPane();
		// On cr�e l'onglet g�n�ral
		JPanel general_panel = makeGeneralPanel(table_definition);
		tabbed_pane.add(MessageManager.getMessage("&Define_General"),
			general_panel);
		// On cr�e l'onglet des colonnes
		JPanel columns_panel = makeColumnsPanel(table_definition);
		tabbed_pane.add(MessageManager.getMessage("&Define_Columns"),
			columns_panel);
		// On cr�e l'onglet des liens
		JPanel links_panel = makeLinksPanel(table_definition);
		tabbed_pane.add(MessageManager.getMessage("&Define_Links"),
			links_panel);
		// On cr�e l'onglet des libell�s
		JPanel methods_panel = makeLabelsPanel(table_definition);
		tabbed_pane.add(MessageManager.getMessage("&Define_Labels"),
			methods_panel);
		// On lib�re l'utilisation de la d�finition
		manager.releaseTableDefinitionLeasing(table_definition);

		// On place la barre � onglet dans la zone centrale de la fen�tre
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabbed_pane, BorderLayout.CENTER);

		// Maintenant, on va cr�er le bouton Fermer
		JButton close_button =
			new JButton(MessageManager.getMessage("&Define_Close"));
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
		setMinimumSize(new Dimension(565, 222));
		setPreferredSize(getMinimumSize());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makeGeneralPanel
	*
	* Description:
	* Cette m�thode est appel�e par la m�thode makePanel() afin de construire
	* l'onglet pr�sentant les informations g�n�rales de la table.
	* Ces informations sont:
	*  - Le chemin du fichier dictionnaire sur la plate-forme,
	*  - Le nom de la table,
	*  - Le s�parateur des colonnes,
	*  - La source des donn�es,
	*  - L'ent�te de la table,
	*  - Le type de table,
	*  - Le propri�taire de la table.
	*
	* Arguments:
	*  - tableDefinition: Une r�f�rence sur l'objet IsisTableDefinition qui
	*    contient la d�finition de la table.
	*
	* Retourne: Une instance de JPanel repr�sentant le contenu de l'onglet
	* G�n�ral.
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
		// Le panneau g�n�ral ne contient qu'un tableau affichant les
		// informations g�n�rales
		// On cr�e les colonnes
		String[] columns = {
			MessageManager.getMessage("&Define_Name"),
			MessageManager.getMessage("&Define_Value")
		};
		// On cr�e les donn�es
		String[][] data = new String[7][2];
		// Chemin du dictionnaire
		data[0][0] = MessageManager.getMessage("&Define_FilePath");
		data[0][1] = tableDefinition.definitionFilePath ;
		// Le nom de la table
		data[1][0] = MessageManager.getMessage("&Define_TableName");
		data[1][1] = tableDefinition.tableName;
		// Le s�parateur des colonnes
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
		// La source des donn�es
		data[3][0] = MessageManager.getMessage("&Define_Source");
		data[3][1] = tableDefinition.source;
		// L'ent�te de la table
		data[4][0] = MessageManager.getMessage("&Define_Header");
		data[4][1] = tableDefinition.header;
		// Le type de table
		data[5][0] = MessageManager.getMessage("&Define_Type");
		data[5][1] = tableDefinition.type;
		// Le propri�taire de la table
		data[6][0] = MessageManager.getMessage("&Define_Owner");
		data[6][1] = tableDefinition.owner;
		// On cr�e le tableau
		NonEditableTable table = new NonEditableTable(data, columns, ";");
		// On r�gle quelques param�tres
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(400);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// On place la table dans une zone � d�filement
		JScrollPane scroll = new JScrollPane(table);
		// On cr�e le panneau
		JPanel general_panel = new JPanel(new BorderLayout());
		// On ajoute la zone de d�filement dans le panneau
		general_panel.add(scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return general_panel;
	}

	/*----------------------------------------------------------
	* Nom: makeColumnsPanel
	*
	* Description:
	* Cette m�thode est appel�e par la m�thode makePanel() afin de construire
	* l'onglet pr�sentant les informations sur les colonnes de la table.
	* Ces informations sont:
	*  - La liste des colonnes,
	*  - La liste des colonnes constituant la cl� primaire,
	*  - La liste des colonnes participant au tri,
	*  - La liste des colonnes non nulles.
	*
	* Arguments:
	*  - tableDefinition: Une r�f�rence sur l'objet IsisTableDefinition qui
	*    contient la d�finition de la table.
	*
	* Retourne: Une instance de JPanel repr�sentant le contenu de l'onglet
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
		// La liste des colonnes est affich�e dans un tableau
		// Il va pr�senter les informations suivantes:
		// - Nom de la colonne,
		// - Taille (et type),
		// - Cl� ?
		// - Ordre de tri.
		// On cr�e la liste des colonnes du tableau
		String[] columns = {
			MessageManager.getMessage("&Define_Name"),
			MessageManager.getMessage("&Define_Size"),
			MessageManager.getMessage("&Define_IsKey"),
			MessageManager.getMessage("&Define_SortOrder"),
			MessageManager.getMessage("&Define_IsNotNull")
		};
		// On cr�e la liste des donn�es
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
		// On cr�e le tableau
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
		// On r�gle quelques param�tres
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// On place la table dans une zone � d�filement
		JScrollPane scroll = new JScrollPane(table);
		// On cr�e le panneau
		JPanel columns_panel = new JPanel(new BorderLayout());
		// On ajoute la zone de d�filement dans le panneau
		columns_panel.add(scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return columns_panel;
	}

	/*----------------------------------------------------------
	* Nom: makeLinksPanel
	*
	* Description:
	* Cette m�thode est appel�e par la m�thode makePanel() afin de construire
	* l'onglet pr�sentant les informations sur les liens de la table.
	* Ces informations sont:
	*  - La liste des cl�s �trang�res.
	*
	* Arguments:
	*  - tableDefinition: Une r�f�rence sur l'objet IsisTableDefinition qui
	*    contient la d�finition de la table.
	*
	* Retourne: Une instance de JPanel repr�sentant le contenu de l'onglet
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
		// Tout d'abord, cr�er le tableau des cl�s �trang�res
		String[] columns = { "" };
		// Les donn�es sont construites � partir des cl�s �trang�res
		String[][] data = new String[tableDefinition.foreignKeys.length][1];
		for(int index = 0 ; index < tableDefinition.foreignKeys.length ;
			index ++)
		{
			// On remplit le tableau de donn�es
			data[index][0] = getForeignKeyString(
				tableDefinition.foreignKeys[index]);
		}
		// Maintenant, on cr�e le tableau
		NonEditableTable foreign_table = new NonEditableTable(data, columns, ";");
		// On r�gle les param�tres du tableau
		foreign_table.getColumnModel().getColumn(0).setPreferredWidth(500);
		foreign_table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		foreign_table.setTableHeader(null);
		// Placement du tableau dans une zone de d�filement
		JScrollPane foreign_scroll = new JScrollPane(foreign_table);
		// Ajout d'une bordure � la zone de d�filement
		foreign_scroll.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Define_ForeignKeys")));

		// Maintenant, on cr�e le panneau qui contiendra le tableau
		JPanel links_panel = new JPanel(new BorderLayout());
		links_panel.add(foreign_scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return links_panel;
	}

	/*----------------------------------------------------------
	* Nom: makeLabelsPanel
	*
	* Description:
	* Cette m�thode est appel�e par la m�thode makePanel() afin de construire
	* l'onglet pr�sentant les informations sur les libell�s de la table.
	*
	* Arguments:
	*  - tableDefinition: Une r�f�rence sur l'objet IsisTableDefinition qui
	*    contient la d�finition de la table.
	*
	* Retourne: Une instance de JPanel repr�sentant le contenu de l'onglet
	* Libell�s.
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
		// On construit le gestionnaire d'affichage des ic�nes
		renderer = new IconCellRenderer();
		// On construit la liste des colonnes
		String[] columns = {
			MessageManager.getMessage("&Define_NodeIdentity"),
			MessageManager.getMessage("&Define_Label"),
			MessageManager.getMessage("&Define_Icon")
		};
		// On cr�e les donn�es
		String[][] data = new String[tableDefinition.labels.length][3];
		// On remplit le tableau des donn�es
		for(int index = 0 ; index < tableDefinition.labels.length ; index ++)
		{
			IsisNodeLabel label = tableDefinition.labels[index];
			data[index][0] = label.nodeIdentity;
			data[index][1] = label.label;
			data[index][2] = label.icon;
		}

		// Maintenant on cr�e le tableau
		NonEditableTable table = new NonEditableTable(data, columns, ";");
		// On r�gle quelques param�tres
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(25);
		table.getColumnModel().getColumn(2).setCellRenderer(renderer);
		table.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// On place la table dans une zone � d�filement
		JScrollPane scroll = new JScrollPane(table);
		// On cr�e le panneau
		JPanel labels_panel = new JPanel(new BorderLayout());
		// On ajoute la zone de d�filement dans le panneau
		labels_panel.add(scroll, BorderLayout.CENTER);
		trace_methods.endOfMethod();
		return labels_panel;
	}

	/*----------------------------------------------------------
	* Nom: isColumnInKey
	*
	* Description:
	* Cette m�thode permet de savoir si une colonne, dont le nom est pass� en
	* argument, fait partie de la cl� de la table.
	* La cl� de la table (attribut key de l'objet IsisTableDefinition) est
	* constitu� d'une liste de colonnes.
	*
	* Arguments:
	*  - columnName: Le nom de la colonne dont on veut conna�tre l'appartenance
	*    � la cl�,
	*  - tableDefinition: Une r�f�rence sur l'objet IsisTableDefinition
	*    contenant la d�finition de la table.
	*
	* Retourne: true si la colonne fait partie de la cl�, false sinon.
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
		// Il faut scruter la liste des colonnes constituant la cl� pour savoir
		// si la colonne pass�e en argument en fait partie
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
	* Cette m�thode permet de r�cup�rer la position d'une colonne, dont le nom
	* est pass� en argument, dans l'ordre de tri. Si la colonne ne fait pas
	* partie des colonnes de tri, la m�thode retourne 0.
	* La colonne la plus discriminante aura la position +/-1, sachant que le signe
	* indique le caract�re ascendant (+) ou descendant (-) du tri.
	*
	* Arguments:
	*  - columnName: Le nom de la colonne dont on veut la position dans l'ordre
	*    de tri,
	*  - tableDefinition: Une r�f�rence sur l'objet IsisTableDefinition
	*    contenant la d�finition de la table.
	*
	* Retourne: La position de la colonne dans l'ordre de tri, commen�ant � 1,
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
		// si la colonne pass�e en argument en fait partie
		for(int index = 0 ; index < tableDefinition.sort.length ; index ++)
		{
            String sort_column = tableDefinition.sort[index];

            while (sort_column.charAt(0) == ' ') {
                sort_column = sort_column.substring(1);
            }

			// On va d�couper l'ordre de tri sur " "
			UtilStringTokenizer tokenizer =
				new UtilStringTokenizer(sort_column, " ");
			// Si la premi�re sous-cha�ne vaut la colonne, c'est bon
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
				// en n�gatif
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
	* Cette m�thode permet de savoir si la colonne pass�e en argument
	* peut �tre nulle ou non.
	* La colonne peut �tre nulle si elle ne figure pas dans le champ
	* notNull de la d�finition de la table.
	*
	* Arguments:
	*  - columnName: Le nom de la colonne dont on conna�tre le caract�re
	*    non nul,
	*  - tableDefinition: Une r�f�rence sur l'objet IsisTableDefinition
	*    contenant la d�finition de la table.
	*
	* Retourne: true si la colonne peut �tre nulle, ou false.
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
	* Cette m�thode permet de convertir une cl� �trang�re pass�e en argument en
	* une cha�ne repr�sentant cette cl� avec la syntaxe utilis�e dans les
	* dictionnaires.
	* La syntaxe est du type:
	* [<lcol1>,...] on <table>[<fcol1>,...].
	*
	* Cette information est affich�e dans l'onglet Liens de la sous-fen�tre de
	* d�finition de table.
	*
	* Arguments:
	*  - foreignKey: Une r�f�rence sur la cl� �trang�re, sous forme de
	*    IsisForeignKey.
	*
	* Retourne: Une cha�ne de caract�res contenant la cl� �trang�re.
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
		// On reconstruit la syntaxe de cl� �trang�re
		local_buffer.append("[");
		foreign_buffer.append("[");
		// On ajoute les colonnes de la cl�
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
		// On remplit le tableau de donn�es
		trace_methods.endOfMethod();
		return local_buffer.toString() + " on " +
			foreignKey.foreignTableName + foreign_buffer.toString();
	}
}