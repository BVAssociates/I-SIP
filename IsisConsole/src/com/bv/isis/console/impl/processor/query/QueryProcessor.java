/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/query/QueryProcessor.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de création de requête
* DATE:        28/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.query
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: QueryProcessor.java,v $
* Revision 1.14  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.13  2008/02/21 12:09:26  tz
* Chargement des fichiers de message en UTF-8.
*
* Revision 1.12  2008/01/31 16:57:48  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.11  2005/12/23 13:22:36  tz
* Correction mineure.
*
* Revision 1.10  2005/10/07 08:24:44  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.9  2005/07/01 12:12:12  tz
* Modification du composant pour les traces
*
* Revision 1.8  2004/11/23 15:39:35  tz
* Affichage du libellé de la table seulement si disponible.
*
* Revision 1.7  2004/11/02 08:53:32  tz
* Gestion des leasings sur les définitions.
*
* Revision 1.6  2004/10/22 15:37:40  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.5  2004/10/13 13:55:35  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2004/10/06 07:30:09  tz
* Amélioration de la dimension de la frame
*
* Revision 1.3  2004/07/29 12:04:45  tz
* Suppression d'imports inutiles
*
* Revision 1.2  2002/05/29 09:16:43  tz
* Correction fiches Inuit/21, Inuit/22 et Inuit/23
* Cloture R1.0.3
*
* Revision 1.1  2002/04/05 15:51:02  tz
* Cloture itération IT1.2
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.query;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import java.util.Hashtable;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//
// Imports du projet
//
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.console.core.gui.NonEditableTextArea;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;

/*----------------------------------------------------------
* Nom: QueryProcessor
*
* Description:
* Cette classe implémente le processeur de tâche exécuté lorsque l'utilisateur
* requiert un assistant de création de requête. Il spécialise la classe
* ProcessorFrame de sorte à ce que le processeur soit un processeur graphique.
* Le processeur permet à l'utilisateur de sélectionner les colonnes qui seront
* affichées, de générer la condition de sélection des données, et de régler le
* tri des données.
* Ce processeur permettra ensuite à l'utilisateur de visualiser le résultat de
* l'exécution de la requête, par le biais du processeur "Display", et de
* visualiser la commande de la requête.
* ----------------------------------------------------------*/
public class QueryProcessor
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: QueryProcessor
	*
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Elle n'est
	* présentée que pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public QueryProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "QueryProcessor");

		trace_methods.beginningOfMethod();
		// On crée la table des composants graphiques
		_frameComponents = new Hashtable();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette méthode redéfini celle de la classe ProcessorFrame. Elle est
	* appelée par le ProcessManager afin d'initialiser et de d'exécuter le
	* processeur. Le panneau est construit, puis la sous-fenêtre est affichée.
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
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

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
		// On appelle la méthode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On positionne le titre de la fenêtre (à partir de l'item de menu)
		setTitle(MessageManager.getMessage("&Query_Title"));
		// On récupère la définition de la table, on considère qu'elle est
		// déjà chargée
		TableDefinitionManager definition_manager =
			TableDefinitionManager.getInstance();
		_tableDefinition = definition_manager.getTableDefinition(
			selected_node.getAgentName(), selected_node.getIClesName(),
			selected_node.getServiceType(),
			selected_node.getDefinitionFilePath());
		// On vérifie qu'il y a bien une définition
		if(_tableDefinition == null)
		{
			trace_errors.writeTrace(
				"Il n'y a pas de définition pour la table: " +
				selected_node.getTableName());
				// On sort
				close();
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_NoDefinitionForTable", null,
					null);
		}
		// On construit le panneau de la fenêtre
		makePanel();
		// On fixe la taille minimale
		setMinimumSize(new Dimension(400, 300));
		// On affiche la fenêtre
		display();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette méthode est appelée lorsque le processeur graphique doit être fermé.
	* Elle libère les ressources allouées par celui-ci.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "close");

		trace_methods.beginningOfMethod();
		// On libère l'utilisation de la définition
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		manager.releaseTableDefinitionLeasing(_tableDefinition);
		// On vide la table des composants graphiques et on libère les
		// références
		_frameComponents.clear();
		_frameComponents = null;
		_tableDefinition = null;
		// On appelle la méthode de la super-classe
		super.close();
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
			"QueryProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		// Chargement du fichier de messages
		MessageManager.loadFile("query.mdb", "UTF8");
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
			"QueryProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&QueryProcessorDescription");
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
			"QueryProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new QueryProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _tableDefinition
	*
	* Description:
	* Cet attribut maintient une référence sur un objet IsisTableDefinition
	* correspondant à la définition de la table sur laquelle la requête doit
	* être exécutée.
	* ----------------------------------------------------------*/
	private IsisTableDefinition _tableDefinition;

	/*----------------------------------------------------------
	* Nom: _frameComponents
	*
	* Description:
	* Cet attribut maintient une liste des composants graphiques construits et
	* utilisés dans la fenêtre du processeur. La liste est implémentée par une
	* table de hash dont la clé est un identifiant (unique) du composant.
	* ----------------------------------------------------------*/
	private Hashtable _frameComponents;

	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette méthode est chargée de la construction de la fenêtre. La fenêtre
	* est composée d'une zone indiquant le nom de la table sur laquelle la
	* requête sera exécutée, un barre à onglets permettant de sélectionner les
	* colonnes, de composer la condition de la requête et de composer le tri
	* des données.
	* Elle appelle la méthode makeColumnsPanel() pour la construction de
	* l'onglet des colonnes, la méthode makeConditionPanel() pour la
	* construction de l'onglet de condition de tri, et la méthode
	* makeSortPanel() pour la construction de l'onglet de tri.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makePanel");

		trace_methods.beginningOfMethod();
		GenericTreeObjectNode table_node =
			(GenericTreeObjectNode)getSelectedNode();
		// On va construire la première partie de la sous-fenêtre.
		// Il s'agit d'une zone ou est affiché le nom de la table
		GridBagLayout table_layout = new GridBagLayout();
		GridBagConstraints table_constraints =
			new GridBagConstraints(0, 0, 1, 1, 0, 0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0);
		JPanel table_panel = new JPanel(table_layout);
		// On crée le label pour le nom de la table
		JLabel table_name_label =
			new JLabel(MessageManager.getMessage("&Query_TableName"));
		table_layout.setConstraints(table_name_label, table_constraints);
		table_panel.add(table_name_label);
		if(table_node.getLabel() != null)
		{
			// On crée le label pour le libellé de la table
			JLabel table_label_label =
				new JLabel(MessageManager.getMessage("&Query_TableLabel"));
			table_constraints.gridy++;
			table_layout.setConstraints(table_label_label, table_constraints);
			table_panel.add(table_label_label);
		}
		// On crée la zone de texte qui contiendra le nom de la table
		JTextField table_name_field = new JTextField(table_node.getTableName());
		table_name_field.setEnabled(false);
		table_constraints.gridx++;
		table_constraints.gridy = 0;
		table_constraints.weightx = 1;
		table_layout.setConstraints(table_name_field, table_constraints);
		table_panel.add(table_name_field);
		if(table_node.getLabel() != null)
		{
			// On crée la zone de texte qui contiendra le libellé de la table
			JTextField table_label_field = 
				new JTextField(table_node.getLabel().label);
			table_label_field.setEnabled(false);
			table_constraints.gridy++;
			table_layout.setConstraints(table_label_field, table_constraints);
			table_panel.add(table_label_field);
		}
		// On ajoute une bordure à la zone
		table_panel.setBorder(BorderFactory.createTitledBorder(
			MessageManager.getMessage("&Query_Table")));
		// On ajoute ce panneau dans la zone nord de la fenêtre
		getContentPane().add(table_panel, BorderLayout.NORTH);

		// On construit la deuxième zone de la sous-fenêtre: la barre à onglets
		JTabbedPane tabbed_pane = new JTabbedPane();
		_frameComponents.put("TabbedPane", tabbed_pane);
		getContentPane().add(tabbed_pane, BorderLayout.CENTER);
		// On ajoute le panneau correspondant à la sélection des colonnes
		tabbed_pane.addTab(MessageManager.getMessage("&Query_Selection"),
			makeColumnsPanel());
		// On ajoute le panneau correspondant à la condition de requête
		tabbed_pane.addTab(MessageManager.getMessage("&Query_Condition"),
			makeConditionPanel());
		// On ajoute le panneau correspondant à l'ordre de tri
		tabbed_pane.addTab(MessageManager.getMessage("&Query_Sort"),
			makeSortPanel());
		// On ajoute le panneau contenant la commande
		tabbed_pane.addTab(MessageManager.getMessage("&Query_Command"),
			makeCommandPanel());
		// On ajoute un listener sur la barre à onglets
		tabbed_pane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent event)
			{
				tabChanged();
			}
		});

		// On construit la troisième zone de la sous-fenêtre: la barre des
		// boutons
		JPanel buttons_panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On crée le bouton d'exécution de la commande
		JButton execute_button =
			new JButton(MessageManager.getMessage("&Query_Test"));
		buttons_panel.add(execute_button);
		// On ajoute un listener sur le bouton
		execute_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				executeRequest();
			}
		});
		// On ajoute le bouton à la table des composants graphiques
		_frameComponents.put("ExecuteButton", execute_button);
		// On crée le bouton de fermeture de la sous-fenêtre
		JButton close_button =
			new JButton(MessageManager.getMessage("&Query_Close"));
		buttons_panel.add(close_button);
		// On ajoute un listener sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				close();
			}
		});
		// On va fixer la dimension des boutons
		Dimension execute_button_size = execute_button.getPreferredSize();
		Dimension close_button_size = close_button.getPreferredSize();
		Dimension buttons_size = new Dimension(execute_button_size);
		buttons_size.width = Math.max(execute_button_size.width,
			close_button_size.width);
		execute_button.setPreferredSize(buttons_size);
		close_button.setPreferredSize(buttons_size);
		// On crée un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
		layout.setConstraints(buttons_panel, new GridBagConstraints(0, 0, 1, 1,
			1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(2, 2, 2, 2), 0, 0));
		panel.add(buttons_panel);
		// On ajoute la barre de boutons dans la zone sud de la fenêtre
		getContentPane().add(panel, BorderLayout.SOUTH);

		// On redéfinit la dimension de la fenêtre
		pack();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makeColumnsPanel
	*
	* Description:
	* Cette méthode est chargée de la construction du panneau qui sera placé
	* dans l'onglet "Colonnes" de la fenêtre de création de requête. Ce panneau
	* présente deux listes, une présentant les colonnes qui peuvent être
	* sélectionnées, et une autre présentant les colonnes sélectionnées.
	* Quatre boutons permettent à l'utilisateur de sélectionner ou de
	* désélectionner des colonnes, et de modifier l'ordre des colonnes dans la
	* liste des colonnes sélectionnées.
	*
	* Retourne: Un objet JPanel contenant les éléments nécessaire à la
	* sélection des colonnes.
	* ----------------------------------------------------------*/
	private JPanel makeColumnsPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makeColumnsPanel");

		trace_methods.beginningOfMethod();
		// Création du panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(1, 0, 1, 1, 0,
			0.25, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(2, 2, 2, 2), 0, 0);
		JPanel panel = new JPanel(layout);

		// On construit les boutons
		// Button "Ajouter"
		JButton add_button = new JButton(MessageManager.getMessage("&Query_Add"));
		add_button.setEnabled(false);
		// On ajoute un listener sur le bouton
		add_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				addColumns();
			}
		});
		layout.setConstraints(add_button, constraints);
		panel.add(add_button);
		// On ajoute le bouton dans la table des composants graphiques
		_frameComponents.put("AddButton", add_button);
		// Button "Retirer"
		JButton remove_button =
			new JButton(MessageManager.getMessage("&Query_Remove"));
		remove_button.setEnabled(false);
		// On ajoute un listener sur le bouton
		remove_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				removeColumns();
			}
		});
		constraints.gridy++;
		layout.setConstraints(remove_button, constraints);
		panel.add(remove_button);
		// On ajoute le bouton dans la table des composants graphiques
		_frameComponents.put("RemoveButton", remove_button);
		// Button "Monter"
		JButton move_up_button =
			new JButton(MessageManager.getMessage("&Query_MoveUp"));
		move_up_button.setEnabled(false);
		// On ajoute un listener sur le bouton
		move_up_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				moveColumn(true);
			}
		});
		constraints.gridy++;
		layout.setConstraints(move_up_button, constraints);
		panel.add(move_up_button);
		// On ajoute le bouton dans la table des composants graphiques
		_frameComponents.put("MoveUpButton", move_up_button);
		// Button "Descendre"
		JButton move_down_button =
			new JButton(MessageManager.getMessage("&Query_MoveDown"));
		move_down_button.setEnabled(false);
		// On ajoute un listener sur le bouton
		move_down_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				moveColumn(false);
			}
		});
		constraints.gridy++;
		layout.setConstraints(move_down_button, constraints);
		panel.add(move_down_button);
		// On ajoute le bouton dans la table des composants graphiques
		_frameComponents.put("MoveDownButton", move_down_button);

		// On fabrique la liste des colonnes disponibles
		DefaultListModel list_model = new DefaultListModel();
		for(int index = 0 ; index < _tableDefinition.columns.length ; index ++)
		{
			list_model.addElement(_tableDefinition.columns[index].name);
		}
		JList available_list = new JList(list_model);
		available_list.setSelectionMode(
			ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// On ajoute un listener sur la sélection
		available_list.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent event)
			{
				columnSelected();
			}
		});
		// On place la liste dans un scroll pane, puis dans un panneau
		JScrollPane available_scroll = new JScrollPane(available_list);
		JPanel available_panel = new JPanel(new BorderLayout());
		available_panel.add(available_scroll);
		available_panel.setBorder(BorderFactory.createTitledBorder(
			MessageManager.getMessage("&Query_Available")));
		// On ajoute la liste au panneau
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0.5;
		constraints.weighty = 1;
		constraints.gridheight = 4;
		constraints.fill = GridBagConstraints.BOTH;
		layout.setConstraints(available_panel, constraints);
		panel.add(available_panel);
		// On ajoute le bouton dans la table des composants graphiques
		_frameComponents.put("AvailableList", available_list);
		JList selected_list = new JList(new DefaultListModel());
		selected_list.setSelectionMode(
			ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// On ajoute un listener sur la sélection
		selected_list.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent event)
			{
				columnSelected();
			}
		});
		// On place la liste dans un scroll pane, puis dans un panneau
		JScrollPane selected_scroll = new JScrollPane(selected_list);
		JPanel selected_panel = new JPanel(new BorderLayout());
		selected_panel.add(selected_scroll);
		selected_panel.setBorder(BorderFactory.createTitledBorder(
			MessageManager.getMessage("&Query_Selected")));
		// On ajoute la liste au panneau
		constraints.gridx = 2;
		layout.setConstraints(selected_panel, constraints);
		panel.add(selected_panel);
		// On ajoute le bouton dans la table des composants graphiques
		_frameComponents.put("SelectedList", selected_list);
		// On va régler les dimensions des listes
		selected_scroll.setPreferredSize(available_scroll.getPreferredSize());
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: makeConditionPanel
	*
	* Description:
	* Cette méthode est chargée de la construction du panneau qui sera placé
	* dans l'onglet "Condition" de la fenêtre de création de requête. Ce
	* panneau présente quatre lignes permettant de composer une condition à
	* quatre critères. Chaque critère permet de sélectionner la colonne
	* concernée, une opérande de test, une valeur, et, pour les trois premiers
	* critères, une opérande de liaison.
	* Lorsqu'une ligne de critère est composée, et que l'opérande de liaison a
	* été sélectionné, la ligne suivante est validée de sorte que l'utilisateur
	* puisse construire un nouveau critère.
	*
	* Retourne: Un objet JPanel contenant les éléments nécessaire à la
	* sélection des colonnes.
	* ----------------------------------------------------------*/
	private JPanel makeConditionPanel()
	{
		String[] links;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makeConditionPanel");

		trace_methods.beginningOfMethod();
		// On crée la liste des colonnes
		String[] columns = new String[_tableDefinition.columns.length + 1];
		columns[0] = "";
		for(int index = 1 ; index < columns.length ; index ++)
		{
			columns[index] = _tableDefinition.columns[index - 1].name;
		}

		// On construit le tableau des opérateurs de liaison
		links = new String[3];
		links[0] = "";
		links[1] = MessageManager.getMessage("&Query_AndLink");
		links[2] = MessageManager.getMessage("&Query_OrLink");

		// Création du panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 0,
			0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(2, 2, 2, 2), 0, 0);
		JPanel panel = new JPanel(layout);

		// On construit les labels des champs
		// Label de liaison
		JLabel link_label =
			new JLabel(MessageManager.getMessage("&Query_LinkOperator"));
		layout.setConstraints(link_label, constraints);
		panel.add(link_label);
		// Label de colonne
		JLabel column_label =
			new JLabel(MessageManager.getMessage("&Query_Column"));
		constraints.gridx++;
		constraints.weightx = 0.5;
		layout.setConstraints(column_label, constraints);
		panel.add(column_label);
		// Label d'opérateur
		JLabel operator_label =
			new JLabel(MessageManager.getMessage("&Query_Operator"));
		constraints.weightx = 0;
		constraints.gridx++;
		layout.setConstraints(operator_label, constraints);
		panel.add(operator_label);
		// Label de valeur
		JLabel value_label =
			new JLabel(MessageManager.getMessage("&Query_Value"));
		constraints.gridx++;
		constraints.weightx = 0.5;
		layout.setConstraints(value_label, constraints);
		panel.add(value_label);

		// On va maintenant créer les quatres lignes de critère
		// On modifie les contraintes
		for(int index = 1 ; index < 5 ; index ++)
		{
			final int the_index = index;
		    constraints.gridx = 0;
			constraints.gridy = index;
			constraints.weightx = 0;
			// Si l'index vaut 1, on ne crée pas de combo de liaison
			JComboBox link_combo = null;
			if(index == 1)
			{
				JLabel fake = new JLabel();
				layout.setConstraints(fake, constraints);
				panel.add(fake);
			}
			else
			{
				link_combo = new JComboBox(links);
				// On ajoute un listener sur la sélection
				link_combo.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						criteriaLinkChanged(the_index);
					}
				});
				// On l'ajoute au panneau
				layout.setConstraints(link_combo, constraints);
				panel.add(link_combo);
				// On ajoute le composant dans la table
				_frameComponents.put("CriteriaLink" + index, link_combo);
			}
			// On crée le combo box des colonnes
			JComboBox columns_combo = new JComboBox(columns);
			// On ajoute un listener sur modification de la valeur (changement
			// de sélection)
		    columns_combo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					criteriaColumnChanged(the_index);
				}
			});
			// On ajoute le combo au panneau
			constraints.gridx++;
			constraints.weightx = 0.5;
			layout.setConstraints(columns_combo, constraints);
			panel.add(columns_combo);
			// On ajoute le combo à la table des composants graphiques
			_frameComponents.put("CriteriaColumn" + index, columns_combo);
			// On crée le combo des opérateurs
			JComboBox operators_combo =
				new JComboBox(new DefaultComboBoxModel());
			// On ajoute le combo au panneau
			constraints.gridx++;
			constraints.weightx = 0;
			layout.setConstraints(operators_combo, constraints);
			panel.add(operators_combo);
			// On ajoute le combo à la table des composants graphiques
			_frameComponents.put("CriteriaOperator" + index, operators_combo);
			// On crée la zone de saisie des valeurs
			JTextField value_field = new JTextField(15);
			// On l'ajoute au panneau
			constraints.gridx++;
			constraints.weightx = 0.5;
			layout.setConstraints(value_field, constraints);
			panel.add(value_field);
			// On ajoute le combo à la table des composants graphiques
			_frameComponents.put("CriteriaValue" + index, value_field);
			// On modifie l'état des champs
			operators_combo.setEnabled(false);
			value_field.setEnabled(false);
			if(index > 1)
			{
				link_combo.setEnabled(false);
				columns_combo.setEnabled(false);
			}
		}
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: makeSortPanel
	*
	* Description:
	* Cette méthode est chargée de la construction du panneau qui sera placé
	* dans l'onglet "Tri" de la fenêtre de création de requête. Ce panneau
	* présente quatre lignes permettant de positionner jusqu'à quatre ordres de
	* tri. Chaque ligne d'ordre de tri présente une liste permettant de
	* sélectionner la colonne participant au tri, et le mode de tri (ascendant
	* ou descendant).
	*
	* Retourne: Un objet JPanel contenant les éléments nécessaire à la sélection
	* des colonnes.
	* ----------------------------------------------------------*/
	private JPanel makeSortPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makeSortPanel");

		trace_methods.beginningOfMethod();
		// On crée la liste des colonnes
		String[] columns = new String[_tableDefinition.columns.length + 1];
		columns[0] = "";
		for(int index = 1 ; index < columns.length ; index ++)
		{
			columns[index] = _tableDefinition.columns[index - 1].name;
		}

		// Création du panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 1,
			0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(2, 2, 2, 2), 0, 0);
		JPanel panel = new JPanel(layout);

		// On construit les labels des champs
		// Label de colonne
		JLabel column_label =
			new JLabel(MessageManager.getMessage("&Query_Column"));
		layout.setConstraints(column_label, constraints);
		panel.add(column_label);
		// Label de mode
		JLabel mode_label =
			new JLabel(MessageManager.getMessage("&Query_Mode"));
		constraints.weightx = 0;
		constraints.gridx++;
		layout.setConstraints(mode_label, constraints);
		panel.add(mode_label);

		// On va maintenant créer les quatres lignes de critère
		// On modifie les contraintes
		for(int index = 1 ; index < 5 ; index ++)
		{
			final int the_index = index;
		    constraints.gridx = 0;
			constraints.gridy = index;
			constraints.weightx = 1;
			// On crée le combo box des colonnes
			JComboBox columns_combo = new JComboBox(columns);
			// On ajoute un listener sur modification de la valeur (changement
			// de sélection)
		    columns_combo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					sortColumnChanged(the_index);
				}
			});
			// On ajoute le combo au panneau
			layout.setConstraints(columns_combo, constraints);
			panel.add(columns_combo);
			// On ajoute le combo à la table des composants graphiques
			_frameComponents.put("SortColumn" + index, columns_combo);
			// On crée la case à cocher d'ascendance
			JCheckBox ascending_box =
				new JCheckBox(MessageManager.getMessage("&Query_Ascending"), true);
			// On ajoute la case au panneau
			constraints.gridx++;
			constraints.weightx = 0;
			layout.setConstraints(ascending_box, constraints);
			panel.add(ascending_box);
			// On ajoute le combo à la table des composants graphiques
			_frameComponents.put("SortAscending" + index, ascending_box);
			// On modifie l'état des champs
			ascending_box.setEnabled(false);
			if(index > 1)
			{
				columns_combo.setEnabled(false);
			}
		}
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: makeCommandPanel
	*
	* Description:
	* Cette méthode est chargée de la construction du panneau qui sera placé
	* dans l'onglet "Commande" de la fenêtre de création de requête. Ce panneau
	* présente une zone d'affichage destinée à afficher la commande I-TOOLS
	* correspondant à la requête.
	*
	* Retourne: Un objet JPanel contenant les éléments nécessaire à l'affichage
	* des commandes.
	* ----------------------------------------------------------*/
	private JPanel makeCommandPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makeCommandPanel");

		trace_methods.beginningOfMethod();
		// On crée un panneau
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
		// On crée une zone de texte qui contiendra le texte de la commande
		NonEditableTextArea command_area = new NonEditableTextArea();
		JScrollPane command_scroll = new JScrollPane(command_area);
		// On l'ajoute au panneau
		layout.setConstraints(command_scroll, new GridBagConstraints(0, 0, 1, 1,
			1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
		panel.add(command_scroll);
		// On l'ajoute à la table des composants
		_frameComponents.put("CommandArea", command_area);
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: columnSelected
	*
	* Description:
	* Cette méthode est appelée lorsqu'un élément est sélectionné dans une des
	* listes. Les états des boutons "Ajouter", "Retirer", "Monter" et
	* "Descendre" dépendent des sélections dans les différentes listes.
	* ----------------------------------------------------------*/
	private void columnSelected()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "columnSelected");

		trace_methods.beginningOfMethod();
		// On commence par récupérer les références sur les listes et sur les
		// boutons
		JList available_list = (JList)_frameComponents.get("AvailableList");
		JList selected_list = (JList)_frameComponents.get("SelectedList");
		JButton add_button = (JButton)_frameComponents.get("AddButton");
		JButton remove_button = (JButton)_frameComponents.get("RemoveButton");
		JButton up_button = (JButton)_frameComponents.get("MoveUpButton");
		JButton down_button = (JButton)_frameComponents.get("MoveDownButton");
		// On récupère la sélection dans les listes
		int[] available_selection = available_list.getSelectedIndices();
		int[] selected_selection = selected_list.getSelectedIndices();
		// L'état du bouton Ajouter dépend d'une possible sélection dans la
		// liste des colonnes disponibles
		if(available_selection != null && available_selection.length > 0)
		{
			add_button.setEnabled(true);
		}
		else
		{
			add_button.setEnabled(false);
		}
		// L'état des autres boutons dépend d'une possible sélection dans la
		// liste des colonnes sélectionnées
		if(selected_selection != null && selected_selection.length > 0)
		{
			remove_button.setEnabled(true);
			if(selected_selection.length == 1 && selected_selection[0] > 0)
			{
				up_button.setEnabled(true);
			}
			else
			{
				up_button.setEnabled(false);
			}
			if(selected_selection.length == 1 &&
				selected_selection[0] < selected_list.getModel().getSize() - 1)
			{
				down_button.setEnabled(true);
			}
			else
			{
				down_button.setEnabled(false);
			}
		}
		else
		{
			remove_button.setEnabled(false);
			up_button.setEnabled(false);
			down_button.setEnabled(false);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: addColumns
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur clique sur le bouton
	* "Ajouter" de l'onglet "Colonnes".
	* Elle supprime les colonnes sélectionnées de la liste des colonnes
	* disponibles pour les insérer dans la liste des colonnes sélectionnées.
	* ----------------------------------------------------------*/
	private void addColumns()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "addColumns");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// On récupère les références sur les deux listes
		JList available_list = (JList)_frameComponents.get("AvailableList");
		JList selected_list = (JList)_frameComponents.get("SelectedList");
		// On récupère les modèles des listes
		DefaultListModel available_model =
			(DefaultListModel)available_list.getModel();
		DefaultListModel selected_model =
			(DefaultListModel)selected_list.getModel();
		// On récupère la liste des colonnes sélectionnées dans la liste des
		// colonnes disponibles
		Object[] selected_columns = available_list.getSelectedValues();
		// On transfère une à une les colonnes
		for(int index = 0 ; index < selected_columns.length ; index ++)
		{
			trace_debug.writeTrace("Transfert de la colonne: " +
				selected_columns[index].toString());
			selected_model.addElement(selected_columns[index]);
			available_model.removeElement(selected_columns[index]);
		}
		// On retire toute sélection dans la liste des colonnes disponibles
		available_list.clearSelection();
		columnSelected();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: removeColumns
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur clique sur le bouton
	* "Retirer" de l'onglet "Colonnes".
	* Elle supprime les colonnes sélectionnées de la liste des colonnes
	* sélectionnées pour les insérer dans la liste des colonnes disponibles.
	* ----------------------------------------------------------*/
	private void removeColumns()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "removeColumns");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// On récupère les références sur les deux listes
		JList available_list = (JList)_frameComponents.get("AvailableList");
		JList selected_list = (JList)_frameComponents.get("SelectedList");
		// On récupère les modèles des listes
		DefaultListModel available_model =
			(DefaultListModel)available_list.getModel();
		DefaultListModel selected_model =
			(DefaultListModel)selected_list.getModel();
		// On récupère la liste des colonnes sélectionnées dans la liste des
		// colonnes sélectionnées
		Object[] selected_columns = selected_list.getSelectedValues();
		// On transfère une à une les colonnes
		for(int index = 0 ; index < selected_columns.length ; index ++)
		{
			trace_debug.writeTrace("Transfert de la colonne: " +
				selected_columns[index].toString());
			available_model.addElement(selected_columns[index]);
			selected_model.removeElement(selected_columns[index]);
		}
		// On retire toute sélection dans la liste des colonnes disponibles
		selected_list.clearSelection();
		columnSelected();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: moveColumn
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur clique sur le bouton
	* "Monter" ou "Descendre" de l'onglet "Colonnes".
	* Elle déplace la colonne sélectionnée dans la liste des colonnes
	* sélectionnées suivant un sens défini par l'argument.
	*
	* Arguments:
	*  - moveUp: Un booléen indiquant si la colonne doit être montée (true) ou
	*    descendue (false).
	* ----------------------------------------------------------*/
	private void moveColumn(
		boolean moveUp
		)
	{
		int new_index;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "moveColumn");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("moveUp=" + moveUp);
		// On récupère les références sur les deux listes
		JList selected_list = (JList)_frameComponents.get("SelectedList");
		// On récupère les modèles des listes
		DefaultListModel selected_model =
			(DefaultListModel)selected_list.getModel();
		// On récupère la colonne sélectionnée dans la liste des
		// colonnes sélectionnées
		Object selected_column = selected_list.getSelectedValue();
		int selected_index = selected_list.getSelectedIndex();
		// On déplace la colonne dans le sens indiqué par l'argument
		if(moveUp == true)
		{
			new_index = selected_index - 1;
		    trace_debug.writeTrace("Montée de la colonne:" +
				selected_column.toString());
		}
		else
		{
			new_index = selected_index + 1;
		    trace_debug.writeTrace("Descente de la colonne:" +
				selected_column.toString());
		}
		// On déplace la colonne
		selected_model.removeElementAt(selected_index);
		selected_model.add(new_index, selected_column);
		// On retire toute sélection dans la liste des colonnes disponibles
		selected_list.clearSelection();
		// On repositionne la sélection comme étant le nouvel index
		selected_list.setSelectedIndex(new_index);
		columnSelected();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: criteriaColumnChanged
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a modifié la sélection
	* du nom de colonne dans une des lignes de critère. Le numéro de la ligne
	* concernée est passé en argument.
	*
	* Arguments:
	*  - row: Numéro de la ligne de critère concernée (commençant à 1).
	* ----------------------------------------------------------*/
	private void criteriaColumnChanged(
		int row
		)
	{
		boolean state = false;
		String[] common_operators = { "=", "!=", ">", ">=", "<", "<=" };
		String[] specific_operators = { "~", "!~" };

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "criteriaColumnChanged");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("row=" + row);
		// On récupère tous les éléments de la ligne
		JComboBox columns_combo =
			(JComboBox)_frameComponents.get("CriteriaColumn" + row);
		JComboBox operator_combo =
			(JComboBox)_frameComponents.get("CriteriaOperator" + row);
		JTextField value_field =
			(JTextField)_frameComponents.get("CriteriaValue" + row);
		JComboBox link_combo = null;
		if(row >= 1 && row < 4)
		{
			link_combo =
				(JComboBox)_frameComponents.get("CriteriaLink" + (row + 1));
		}
		// On regarde si une valeur a été sélectionné
		int selected_index = columns_combo.getSelectedIndex();
		if(selected_index > 0)
		{
			// On met à jour les opérateurs
			DefaultComboBoxModel model =
				(DefaultComboBoxModel)operator_combo.getModel();
			model.removeAllElements();
			// On ajoute les éléments communs
			for(int index = 0 ; index < common_operators.length ; index ++)
			{
			    model.addElement(common_operators[index]);
			}
			if(_tableDefinition.columns[selected_index - 1].type == 's')
			{
				// On ajoute les éléments spécifiques
				for(int index = 0 ; index < specific_operators.length ;
					index ++)
				{
					model.addElement(specific_operators[index]);
				}
			}
			trace_debug.writeTrace("Activation de la ligne: " + row);
			// On valide tous les champs
			state = true;
		}
		else
		{
			trace_debug.writeTrace("Désactivation de la ligne: " + row);
			// On invalide tous les champs
			state = false;
		}
		if(operator_combo.getItemCount() > 0)
		{
		    operator_combo.setSelectedIndex(0);
		}
		else
		{
			operator_combo.setSelectedIndex(-1);
		}
		operator_combo.setEnabled(state);
		value_field.setText("");
		value_field.setEnabled(state);
		if(link_combo != null)
		{
			link_combo.setSelectedIndex(-1);
			link_combo.setEnabled(state);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: criteriaLinkChanged
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a modifié la sélection
	* de l'opération de liaison dans une des lignes de critère. Le numéro de la
	* ligne concernée est passé en argument.
	*
	* Arguments:
	*  - row: Numéro de la ligne de critère concernée (commençant à 1).
	* ----------------------------------------------------------*/
	private void criteriaLinkChanged(
		int row
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "criteriaLinkChanged");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("row=" + row);
		// On récupère tous les éléments de la ligne
		JComboBox link_combo =
			(JComboBox)_frameComponents.get("CriteriaLink" + row);
		JComboBox columns_combo =
			(JComboBox)_frameComponents.get("CriteriaColumn" + row);
		// On regarde si une valeur a été sélectionné
		if(link_combo.getSelectedIndex() > 0)
		{
			trace_debug.writeTrace("Activation de la ligne: " + row);
			// On valide tous les champs
			columns_combo.setEnabled(true);
		}
		else
		{
			trace_debug.writeTrace("Désactivation de la ligne: " + row);
			// On invalide tous les champs
			columns_combo.setSelectedIndex(-1);
			columns_combo.setEnabled(false);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: sortColumnChanged
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a modifié la sélection
	* du nom de colonne dans une des lignes de tri. Le numéro de la ligne
	* concernée est passé en argument.
	*
	* Arguments:
	*  - row: Numéro de la ligne de critère concernée (commençant à 1).
	* ----------------------------------------------------------*/
	private void sortColumnChanged(
		int row
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "sortColumnChanged");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("row=" + row);
		// On récupère tous les éléments de la ligne
		JComboBox column_combo =
			(JComboBox)_frameComponents.get("SortColumn" + row);
		JCheckBox ascending_box =
			(JCheckBox)_frameComponents.get("SortAscending" + row);
		JComboBox next_column_combo = null;
		if(row < 4)
		{
			next_column_combo =
				(JComboBox)_frameComponents.get("SortColumn" + (row + 1));
		}
		// On regarde si une valeur a été sélectionné
		if(column_combo.getSelectedIndex() > 0)
		{
			trace_debug.writeTrace("Activation de la ligne: " + row);
			// On valide tous les champs
			ascending_box.setEnabled(true);
			if(next_column_combo != null)
			{
				next_column_combo.setEnabled(true);
			}
		}
		else
		{
			trace_debug.writeTrace("Désactivation de la ligne: " + row);
			// On invalide tous les champs
			ascending_box.setSelected(true);
			ascending_box.setEnabled(false);
			if(next_column_combo != null)
			{
				next_column_combo.setSelectedIndex(-1);
				next_column_combo.setEnabled(false);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: tabChanged
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur change l'onglet en cours
	* d'affichage. Elle vérifie si l'onglet sélectionné correspond à l'onglet
	* "Commande", et, si c'est le cas, vérifie la validité de la requête, via
	* la classe RequestFactory.
	* ----------------------------------------------------------*/
	private void tabChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "tabChanged");

		trace_methods.beginningOfMethod();
		// Récupération de la référence sur la barre à onglets
		JTabbedPane tabbed_pane =
			(JTabbedPane)_frameComponents.get("TabbedPane");
		NonEditableTextArea command_area =
			(NonEditableTextArea)_frameComponents.get("CommandArea");
		if(tabbed_pane.getSelectedIndex() != 3)
		{
			// Il ne s'agit pas de l'onglet commande, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On tente de construire la requête
		RequestDefinition request =
			RequestFactory.buildRequest(_frameComponents, _tableDefinition,
			getMainWindowInterface());
		// Si la définition de la requête existe, on affiche la commande, sinon
		// on affiche un message.
		if(request == null)
		{
			command_area.setText(
				MessageManager.getMessage("&ERR_RequestIsIncomplete"));
		}
		else
		{
			command_area.setText(RequestFactory.buildCommand(
				((GenericTreeObjectNode)getSelectedNode()).getTableName(),
				request));
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: executeRequest
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le bouton
	* "Exécuter...". Elle fait appel à la méthode executeRequest() de la classe
	* RequestFactory afin d'exécuter le processeur "DisplayTable".
	* ----------------------------------------------------------*/
	private void executeRequest()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "executeRequest");

		trace_methods.beginningOfMethod();
		// On sélectionne l'onglet n°3
		// Récupération de la référence sur la barre à onglets
		JTabbedPane tabbed_pane =
			(JTabbedPane)_frameComponents.get("TabbedPane");
		tabbed_pane.setSelectedIndex(3);
		// On tente de construire la requête
		RequestDefinition request =
			RequestFactory.buildRequest(_frameComponents, _tableDefinition,
			null);
		// On va vérifier que la requête est valide
		if(request == null)
		{
			// La requête n'est pas valide, on sort
			trace_methods.endOfMethod();
			return;
		}
		try
		{
			// On exécute le processeur d'exécution de requête
			GenericTreeObjectNode selected_node =
				(GenericTreeObjectNode)getSelectedNode();
			RequestFactory.executeRequest(selected_node.getTableName(),
				request, selected_node, getMainWindowInterface());
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de l'exécution du processeur: " +
				exception.getMessage());
			// Il y a eu une erreur, on affiche un message
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotExecuteRequest", exception);
		}
		trace_methods.endOfMethod();
	}
}