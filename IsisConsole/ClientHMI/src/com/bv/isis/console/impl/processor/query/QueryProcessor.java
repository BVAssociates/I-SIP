/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/query/QueryProcessor.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de cr�ation de requ�te
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
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.11  2005/12/23 13:22:36  tz
* Correction mineure.
*
* Revision 1.10  2005/10/07 08:24:44  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.9  2005/07/01 12:12:12  tz
* Modification du composant pour les traces
*
* Revision 1.8  2004/11/23 15:39:35  tz
* Affichage du libell� de la table seulement si disponible.
*
* Revision 1.7  2004/11/02 08:53:32  tz
* Gestion des leasings sur les d�finitions.
*
* Revision 1.6  2004/10/22 15:37:40  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.5  2004/10/13 13:55:35  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.4  2004/10/06 07:30:09  tz
* Am�lioration de la dimension de la frame
*
* Revision 1.3  2004/07/29 12:04:45  tz
* Suppression d'imports inutiles
*
* Revision 1.2  2002/05/29 09:16:43  tz
* Correction fiches Inuit/21, Inuit/22 et Inuit/23
* Cloture R1.0.3
*
* Revision 1.1  2002/04/05 15:51:02  tz
* Cloture it�ration IT1.2
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.query;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur de t�che ex�cut� lorsque l'utilisateur
* requiert un assistant de cr�ation de requ�te. Il sp�cialise la classe
* ProcessorFrame de sorte � ce que le processeur soit un processeur graphique.
* Le processeur permet � l'utilisateur de s�lectionner les colonnes qui seront
* affich�es, de g�n�rer la condition de s�lection des donn�es, et de r�gler le
* tri des donn�es.
* Ce processeur permettra ensuite � l'utilisateur de visualiser le r�sultat de
* l'ex�cution de la requ�te, par le biais du processeur "Display", et de
* visualiser la commande de la requ�te.
* ----------------------------------------------------------*/
public class QueryProcessor
	extends ProcessorFrame
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: QueryProcessor
	*
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. Elle n'est
	* pr�sent�e que pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public QueryProcessor()
	{
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "QueryProcessor");

		trace_methods.beginningOfMethod();
		// On cr�e la table des composants graphiques
		_frameComponents = new Hashtable();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette m�thode red�fini celle de la classe ProcessorFrame. Elle est
	* appel�e par le ProcessManager afin d'initialiser et de d'ex�cuter le
	* processeur. Le panneau est construit, puis la sous-fen�tre est affich�e.
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
		// Tout d'abord, il faut v�rifier l'int�grit� des arguments
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
		// On appelle la m�thode de la super-classe
		super.run(windowInterface, menuItem, parameters, preprocessing,
			postprocessing, selectedNode);
		// On positionne le titre de la fen�tre (� partir de l'item de menu)
		setTitle(MessageManager.getMessage("&Query_Title"));
		// On r�cup�re la d�finition de la table, on consid�re qu'elle est
		// d�j� charg�e
		TableDefinitionManager definition_manager =
			TableDefinitionManager.getInstance();
		_tableDefinition = definition_manager.getTableDefinition(
			selected_node.getAgentName(), selected_node.getIClesName(),
			selected_node.getServiceType(),
			selected_node.getDefinitionFilePath());
		// On v�rifie qu'il y a bien une d�finition
		if(_tableDefinition == null)
		{
			trace_errors.writeTrace(
				"Il n'y a pas de d�finition pour la table: " +
				selected_node.getTableName());
				// On sort
				close();
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_NoDefinitionForTable", null,
					null);
		}
		// On construit le panneau de la fen�tre
		makePanel();
		// On fixe la taille minimale
		setMinimumSize(new Dimension(400, 300));
		// On affiche la fen�tre
		display();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette m�thode est appel�e lorsque le processeur graphique doit �tre ferm�.
	* Elle lib�re les ressources allou�es par celui-ci.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "close");

		trace_methods.beginningOfMethod();
		// On lib�re l'utilisation de la d�finition
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		manager.releaseTableDefinitionLeasing(_tableDefinition);
		// On vide la table des composants graphiques et on lib�re les
		// r�f�rences
		_frameComponents.clear();
		_frameComponents = null;
		_tableDefinition = null;
		// On appelle la m�thode de la super-classe
		super.close();
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer la description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
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
	* Cet attribut maintient une r�f�rence sur un objet IsisTableDefinition
	* correspondant � la d�finition de la table sur laquelle la requ�te doit
	* �tre ex�cut�e.
	* ----------------------------------------------------------*/
	private IsisTableDefinition _tableDefinition;

	/*----------------------------------------------------------
	* Nom: _frameComponents
	*
	* Description:
	* Cet attribut maintient une liste des composants graphiques construits et
	* utilis�s dans la fen�tre du processeur. La liste est impl�ment�e par une
	* table de hash dont la cl� est un identifiant (unique) du composant.
	* ----------------------------------------------------------*/
	private Hashtable _frameComponents;

	/*----------------------------------------------------------
	* Nom: makePanel
	*
	* Description:
	* Cette m�thode est charg�e de la construction de la fen�tre. La fen�tre
	* est compos�e d'une zone indiquant le nom de la table sur laquelle la
	* requ�te sera ex�cut�e, un barre � onglets permettant de s�lectionner les
	* colonnes, de composer la condition de la requ�te et de composer le tri
	* des donn�es.
	* Elle appelle la m�thode makeColumnsPanel() pour la construction de
	* l'onglet des colonnes, la m�thode makeConditionPanel() pour la
	* construction de l'onglet de condition de tri, et la m�thode
	* makeSortPanel() pour la construction de l'onglet de tri.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makePanel");

		trace_methods.beginningOfMethod();
		GenericTreeObjectNode table_node =
			(GenericTreeObjectNode)getSelectedNode();
		// On va construire la premi�re partie de la sous-fen�tre.
		// Il s'agit d'une zone ou est affich� le nom de la table
		GridBagLayout table_layout = new GridBagLayout();
		GridBagConstraints table_constraints =
			new GridBagConstraints(0, 0, 1, 1, 0, 0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0);
		JPanel table_panel = new JPanel(table_layout);
		// On cr�e le label pour le nom de la table
		JLabel table_name_label =
			new JLabel(MessageManager.getMessage("&Query_TableName"));
		table_layout.setConstraints(table_name_label, table_constraints);
		table_panel.add(table_name_label);
		if(table_node.getLabel() != null)
		{
			// On cr�e le label pour le libell� de la table
			JLabel table_label_label =
				new JLabel(MessageManager.getMessage("&Query_TableLabel"));
			table_constraints.gridy++;
			table_layout.setConstraints(table_label_label, table_constraints);
			table_panel.add(table_label_label);
		}
		// On cr�e la zone de texte qui contiendra le nom de la table
		JTextField table_name_field = new JTextField(table_node.getTableName());
		table_name_field.setEnabled(false);
		table_constraints.gridx++;
		table_constraints.gridy = 0;
		table_constraints.weightx = 1;
		table_layout.setConstraints(table_name_field, table_constraints);
		table_panel.add(table_name_field);
		if(table_node.getLabel() != null)
		{
			// On cr�e la zone de texte qui contiendra le libell� de la table
			JTextField table_label_field = 
				new JTextField(table_node.getLabel().label);
			table_label_field.setEnabled(false);
			table_constraints.gridy++;
			table_layout.setConstraints(table_label_field, table_constraints);
			table_panel.add(table_label_field);
		}
		// On ajoute une bordure � la zone
		table_panel.setBorder(BorderFactory.createTitledBorder(
			MessageManager.getMessage("&Query_Table")));
		// On ajoute ce panneau dans la zone nord de la fen�tre
		getContentPane().add(table_panel, BorderLayout.NORTH);

		// On construit la deuxi�me zone de la sous-fen�tre: la barre � onglets
		JTabbedPane tabbed_pane = new JTabbedPane();
		_frameComponents.put("TabbedPane", tabbed_pane);
		getContentPane().add(tabbed_pane, BorderLayout.CENTER);
		// On ajoute le panneau correspondant � la s�lection des colonnes
		tabbed_pane.addTab(MessageManager.getMessage("&Query_Selection"),
			makeColumnsPanel());
		// On ajoute le panneau correspondant � la condition de requ�te
		tabbed_pane.addTab(MessageManager.getMessage("&Query_Condition"),
			makeConditionPanel());
		// On ajoute le panneau correspondant � l'ordre de tri
		tabbed_pane.addTab(MessageManager.getMessage("&Query_Sort"),
			makeSortPanel());
		// On ajoute le panneau contenant la commande
		tabbed_pane.addTab(MessageManager.getMessage("&Query_Command"),
			makeCommandPanel());
		// On ajoute un listener sur la barre � onglets
		tabbed_pane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent event)
			{
				tabChanged();
			}
		});

		// On construit la troisi�me zone de la sous-fen�tre: la barre des
		// boutons
		JPanel buttons_panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
		// On cr�e le bouton d'ex�cution de la commande
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
		// On ajoute le bouton � la table des composants graphiques
		_frameComponents.put("ExecuteButton", execute_button);
		// On cr�e le bouton de fermeture de la sous-fen�tre
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
		// On cr�e un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
		layout.setConstraints(buttons_panel, new GridBagConstraints(0, 0, 1, 1,
			1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(2, 2, 2, 2), 0, 0));
		panel.add(buttons_panel);
		// On ajoute la barre de boutons dans la zone sud de la fen�tre
		getContentPane().add(panel, BorderLayout.SOUTH);

		// On red�finit la dimension de la fen�tre
		pack();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makeColumnsPanel
	*
	* Description:
	* Cette m�thode est charg�e de la construction du panneau qui sera plac�
	* dans l'onglet "Colonnes" de la fen�tre de cr�ation de requ�te. Ce panneau
	* pr�sente deux listes, une pr�sentant les colonnes qui peuvent �tre
	* s�lectionn�es, et une autre pr�sentant les colonnes s�lectionn�es.
	* Quatre boutons permettent � l'utilisateur de s�lectionner ou de
	* d�s�lectionner des colonnes, et de modifier l'ordre des colonnes dans la
	* liste des colonnes s�lectionn�es.
	*
	* Retourne: Un objet JPanel contenant les �l�ments n�cessaire � la
	* s�lection des colonnes.
	* ----------------------------------------------------------*/
	private JPanel makeColumnsPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makeColumnsPanel");

		trace_methods.beginningOfMethod();
		// Cr�ation du panneau avec un GridBagLayout
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
		// On ajoute un listener sur la s�lection
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
		// On ajoute un listener sur la s�lection
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
		// On va r�gler les dimensions des listes
		selected_scroll.setPreferredSize(available_scroll.getPreferredSize());
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: makeConditionPanel
	*
	* Description:
	* Cette m�thode est charg�e de la construction du panneau qui sera plac�
	* dans l'onglet "Condition" de la fen�tre de cr�ation de requ�te. Ce
	* panneau pr�sente quatre lignes permettant de composer une condition �
	* quatre crit�res. Chaque crit�re permet de s�lectionner la colonne
	* concern�e, une op�rande de test, une valeur, et, pour les trois premiers
	* crit�res, une op�rande de liaison.
	* Lorsqu'une ligne de crit�re est compos�e, et que l'op�rande de liaison a
	* �t� s�lectionn�, la ligne suivante est valid�e de sorte que l'utilisateur
	* puisse construire un nouveau crit�re.
	*
	* Retourne: Un objet JPanel contenant les �l�ments n�cessaire � la
	* s�lection des colonnes.
	* ----------------------------------------------------------*/
	private JPanel makeConditionPanel()
	{
		String[] links;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makeConditionPanel");

		trace_methods.beginningOfMethod();
		// On cr�e la liste des colonnes
		String[] columns = new String[_tableDefinition.columns.length + 1];
		columns[0] = "";
		for(int index = 1 ; index < columns.length ; index ++)
		{
			columns[index] = _tableDefinition.columns[index - 1].name;
		}

		// On construit le tableau des op�rateurs de liaison
		links = new String[3];
		links[0] = "";
		links[1] = MessageManager.getMessage("&Query_AndLink");
		links[2] = MessageManager.getMessage("&Query_OrLink");

		// Cr�ation du panneau avec un GridBagLayout
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
		// Label d'op�rateur
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

		// On va maintenant cr�er les quatres lignes de crit�re
		// On modifie les contraintes
		for(int index = 1 ; index < 5 ; index ++)
		{
			final int the_index = index;
		    constraints.gridx = 0;
			constraints.gridy = index;
			constraints.weightx = 0;
			// Si l'index vaut 1, on ne cr�e pas de combo de liaison
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
				// On ajoute un listener sur la s�lection
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
			// On cr�e le combo box des colonnes
			JComboBox columns_combo = new JComboBox(columns);
			// On ajoute un listener sur modification de la valeur (changement
			// de s�lection)
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
			// On ajoute le combo � la table des composants graphiques
			_frameComponents.put("CriteriaColumn" + index, columns_combo);
			// On cr�e le combo des op�rateurs
			JComboBox operators_combo =
				new JComboBox(new DefaultComboBoxModel());
			// On ajoute le combo au panneau
			constraints.gridx++;
			constraints.weightx = 0;
			layout.setConstraints(operators_combo, constraints);
			panel.add(operators_combo);
			// On ajoute le combo � la table des composants graphiques
			_frameComponents.put("CriteriaOperator" + index, operators_combo);
			// On cr�e la zone de saisie des valeurs
			JTextField value_field = new JTextField(15);
			// On l'ajoute au panneau
			constraints.gridx++;
			constraints.weightx = 0.5;
			layout.setConstraints(value_field, constraints);
			panel.add(value_field);
			// On ajoute le combo � la table des composants graphiques
			_frameComponents.put("CriteriaValue" + index, value_field);
			// On modifie l'�tat des champs
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
	* Cette m�thode est charg�e de la construction du panneau qui sera plac�
	* dans l'onglet "Tri" de la fen�tre de cr�ation de requ�te. Ce panneau
	* pr�sente quatre lignes permettant de positionner jusqu'� quatre ordres de
	* tri. Chaque ligne d'ordre de tri pr�sente une liste permettant de
	* s�lectionner la colonne participant au tri, et le mode de tri (ascendant
	* ou descendant).
	*
	* Retourne: Un objet JPanel contenant les �l�ments n�cessaire � la s�lection
	* des colonnes.
	* ----------------------------------------------------------*/
	private JPanel makeSortPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makeSortPanel");

		trace_methods.beginningOfMethod();
		// On cr�e la liste des colonnes
		String[] columns = new String[_tableDefinition.columns.length + 1];
		columns[0] = "";
		for(int index = 1 ; index < columns.length ; index ++)
		{
			columns[index] = _tableDefinition.columns[index - 1].name;
		}

		// Cr�ation du panneau avec un GridBagLayout
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

		// On va maintenant cr�er les quatres lignes de crit�re
		// On modifie les contraintes
		for(int index = 1 ; index < 5 ; index ++)
		{
			final int the_index = index;
		    constraints.gridx = 0;
			constraints.gridy = index;
			constraints.weightx = 1;
			// On cr�e le combo box des colonnes
			JComboBox columns_combo = new JComboBox(columns);
			// On ajoute un listener sur modification de la valeur (changement
			// de s�lection)
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
			// On ajoute le combo � la table des composants graphiques
			_frameComponents.put("SortColumn" + index, columns_combo);
			// On cr�e la case � cocher d'ascendance
			JCheckBox ascending_box =
				new JCheckBox(MessageManager.getMessage("&Query_Ascending"), true);
			// On ajoute la case au panneau
			constraints.gridx++;
			constraints.weightx = 0;
			layout.setConstraints(ascending_box, constraints);
			panel.add(ascending_box);
			// On ajoute le combo � la table des composants graphiques
			_frameComponents.put("SortAscending" + index, ascending_box);
			// On modifie l'�tat des champs
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
	* Cette m�thode est charg�e de la construction du panneau qui sera plac�
	* dans l'onglet "Commande" de la fen�tre de cr�ation de requ�te. Ce panneau
	* pr�sente une zone d'affichage destin�e � afficher la commande I-TOOLS
	* correspondant � la requ�te.
	*
	* Retourne: Un objet JPanel contenant les �l�ments n�cessaire � l'affichage
	* des commandes.
	* ----------------------------------------------------------*/
	private JPanel makeCommandPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "makeCommandPanel");

		trace_methods.beginningOfMethod();
		// On cr�e un panneau
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
		// On cr�e une zone de texte qui contiendra le texte de la commande
		NonEditableTextArea command_area = new NonEditableTextArea();
		JScrollPane command_scroll = new JScrollPane(command_area);
		// On l'ajoute au panneau
		layout.setConstraints(command_scroll, new GridBagConstraints(0, 0, 1, 1,
			1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
		panel.add(command_scroll);
		// On l'ajoute � la table des composants
		_frameComponents.put("CommandArea", command_area);
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: columnSelected
	*
	* Description:
	* Cette m�thode est appel�e lorsqu'un �l�ment est s�lectionn� dans une des
	* listes. Les �tats des boutons "Ajouter", "Retirer", "Monter" et
	* "Descendre" d�pendent des s�lections dans les diff�rentes listes.
	* ----------------------------------------------------------*/
	private void columnSelected()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "columnSelected");

		trace_methods.beginningOfMethod();
		// On commence par r�cup�rer les r�f�rences sur les listes et sur les
		// boutons
		JList available_list = (JList)_frameComponents.get("AvailableList");
		JList selected_list = (JList)_frameComponents.get("SelectedList");
		JButton add_button = (JButton)_frameComponents.get("AddButton");
		JButton remove_button = (JButton)_frameComponents.get("RemoveButton");
		JButton up_button = (JButton)_frameComponents.get("MoveUpButton");
		JButton down_button = (JButton)_frameComponents.get("MoveDownButton");
		// On r�cup�re la s�lection dans les listes
		int[] available_selection = available_list.getSelectedIndices();
		int[] selected_selection = selected_list.getSelectedIndices();
		// L'�tat du bouton Ajouter d�pend d'une possible s�lection dans la
		// liste des colonnes disponibles
		if(available_selection != null && available_selection.length > 0)
		{
			add_button.setEnabled(true);
		}
		else
		{
			add_button.setEnabled(false);
		}
		// L'�tat des autres boutons d�pend d'une possible s�lection dans la
		// liste des colonnes s�lectionn�es
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
	* Cette m�thode est appel�e lorsque l'utilisateur clique sur le bouton
	* "Ajouter" de l'onglet "Colonnes".
	* Elle supprime les colonnes s�lectionn�es de la liste des colonnes
	* disponibles pour les ins�rer dans la liste des colonnes s�lectionn�es.
	* ----------------------------------------------------------*/
	private void addColumns()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "addColumns");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// On r�cup�re les r�f�rences sur les deux listes
		JList available_list = (JList)_frameComponents.get("AvailableList");
		JList selected_list = (JList)_frameComponents.get("SelectedList");
		// On r�cup�re les mod�les des listes
		DefaultListModel available_model =
			(DefaultListModel)available_list.getModel();
		DefaultListModel selected_model =
			(DefaultListModel)selected_list.getModel();
		// On r�cup�re la liste des colonnes s�lectionn�es dans la liste des
		// colonnes disponibles
		Object[] selected_columns = available_list.getSelectedValues();
		// On transf�re une � une les colonnes
		for(int index = 0 ; index < selected_columns.length ; index ++)
		{
			trace_debug.writeTrace("Transfert de la colonne: " +
				selected_columns[index].toString());
			selected_model.addElement(selected_columns[index]);
			available_model.removeElement(selected_columns[index]);
		}
		// On retire toute s�lection dans la liste des colonnes disponibles
		available_list.clearSelection();
		columnSelected();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: removeColumns
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur clique sur le bouton
	* "Retirer" de l'onglet "Colonnes".
	* Elle supprime les colonnes s�lectionn�es de la liste des colonnes
	* s�lectionn�es pour les ins�rer dans la liste des colonnes disponibles.
	* ----------------------------------------------------------*/
	private void removeColumns()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "removeColumns");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// On r�cup�re les r�f�rences sur les deux listes
		JList available_list = (JList)_frameComponents.get("AvailableList");
		JList selected_list = (JList)_frameComponents.get("SelectedList");
		// On r�cup�re les mod�les des listes
		DefaultListModel available_model =
			(DefaultListModel)available_list.getModel();
		DefaultListModel selected_model =
			(DefaultListModel)selected_list.getModel();
		// On r�cup�re la liste des colonnes s�lectionn�es dans la liste des
		// colonnes s�lectionn�es
		Object[] selected_columns = selected_list.getSelectedValues();
		// On transf�re une � une les colonnes
		for(int index = 0 ; index < selected_columns.length ; index ++)
		{
			trace_debug.writeTrace("Transfert de la colonne: " +
				selected_columns[index].toString());
			available_model.addElement(selected_columns[index]);
			selected_model.removeElement(selected_columns[index]);
		}
		// On retire toute s�lection dans la liste des colonnes disponibles
		selected_list.clearSelection();
		columnSelected();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: moveColumn
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur clique sur le bouton
	* "Monter" ou "Descendre" de l'onglet "Colonnes".
	* Elle d�place la colonne s�lectionn�e dans la liste des colonnes
	* s�lectionn�es suivant un sens d�fini par l'argument.
	*
	* Arguments:
	*  - moveUp: Un bool�en indiquant si la colonne doit �tre mont�e (true) ou
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
		// On r�cup�re les r�f�rences sur les deux listes
		JList selected_list = (JList)_frameComponents.get("SelectedList");
		// On r�cup�re les mod�les des listes
		DefaultListModel selected_model =
			(DefaultListModel)selected_list.getModel();
		// On r�cup�re la colonne s�lectionn�e dans la liste des
		// colonnes s�lectionn�es
		Object selected_column = selected_list.getSelectedValue();
		int selected_index = selected_list.getSelectedIndex();
		// On d�place la colonne dans le sens indiqu� par l'argument
		if(moveUp == true)
		{
			new_index = selected_index - 1;
		    trace_debug.writeTrace("Mont�e de la colonne:" +
				selected_column.toString());
		}
		else
		{
			new_index = selected_index + 1;
		    trace_debug.writeTrace("Descente de la colonne:" +
				selected_column.toString());
		}
		// On d�place la colonne
		selected_model.removeElementAt(selected_index);
		selected_model.add(new_index, selected_column);
		// On retire toute s�lection dans la liste des colonnes disponibles
		selected_list.clearSelection();
		// On repositionne la s�lection comme �tant le nouvel index
		selected_list.setSelectedIndex(new_index);
		columnSelected();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: criteriaColumnChanged
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a modifi� la s�lection
	* du nom de colonne dans une des lignes de crit�re. Le num�ro de la ligne
	* concern�e est pass� en argument.
	*
	* Arguments:
	*  - row: Num�ro de la ligne de crit�re concern�e (commen�ant � 1).
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
		// On r�cup�re tous les �l�ments de la ligne
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
		// On regarde si une valeur a �t� s�lectionn�
		int selected_index = columns_combo.getSelectedIndex();
		if(selected_index > 0)
		{
			// On met � jour les op�rateurs
			DefaultComboBoxModel model =
				(DefaultComboBoxModel)operator_combo.getModel();
			model.removeAllElements();
			// On ajoute les �l�ments communs
			for(int index = 0 ; index < common_operators.length ; index ++)
			{
			    model.addElement(common_operators[index]);
			}
			if(_tableDefinition.columns[selected_index - 1].type == 's')
			{
				// On ajoute les �l�ments sp�cifiques
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
			trace_debug.writeTrace("D�sactivation de la ligne: " + row);
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
	* Cette m�thode est appel�e lorsque l'utilisateur a modifi� la s�lection
	* de l'op�ration de liaison dans une des lignes de crit�re. Le num�ro de la
	* ligne concern�e est pass� en argument.
	*
	* Arguments:
	*  - row: Num�ro de la ligne de crit�re concern�e (commen�ant � 1).
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
		// On r�cup�re tous les �l�ments de la ligne
		JComboBox link_combo =
			(JComboBox)_frameComponents.get("CriteriaLink" + row);
		JComboBox columns_combo =
			(JComboBox)_frameComponents.get("CriteriaColumn" + row);
		// On regarde si une valeur a �t� s�lectionn�
		if(link_combo.getSelectedIndex() > 0)
		{
			trace_debug.writeTrace("Activation de la ligne: " + row);
			// On valide tous les champs
			columns_combo.setEnabled(true);
		}
		else
		{
			trace_debug.writeTrace("D�sactivation de la ligne: " + row);
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
	* Cette m�thode est appel�e lorsque l'utilisateur a modifi� la s�lection
	* du nom de colonne dans une des lignes de tri. Le num�ro de la ligne
	* concern�e est pass� en argument.
	*
	* Arguments:
	*  - row: Num�ro de la ligne de crit�re concern�e (commen�ant � 1).
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
		// On r�cup�re tous les �l�ments de la ligne
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
		// On regarde si une valeur a �t� s�lectionn�
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
			trace_debug.writeTrace("D�sactivation de la ligne: " + row);
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
	* Cette m�thode est appel�e lorsque l'utilisateur change l'onglet en cours
	* d'affichage. Elle v�rifie si l'onglet s�lectionn� correspond � l'onglet
	* "Commande", et, si c'est le cas, v�rifie la validit� de la requ�te, via
	* la classe RequestFactory.
	* ----------------------------------------------------------*/
	private void tabChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "tabChanged");

		trace_methods.beginningOfMethod();
		// R�cup�ration de la r�f�rence sur la barre � onglets
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
		// On tente de construire la requ�te
		RequestDefinition request =
			RequestFactory.buildRequest(_frameComponents, _tableDefinition,
			getMainWindowInterface());
		// Si la d�finition de la requ�te existe, on affiche la commande, sinon
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
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton
	* "Ex�cuter...". Elle fait appel � la m�thode executeRequest() de la classe
	* RequestFactory afin d'ex�cuter le processeur "DisplayTable".
	* ----------------------------------------------------------*/
	private void executeRequest()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"QueryProcessor", "executeRequest");

		trace_methods.beginningOfMethod();
		// On s�lectionne l'onglet n�3
		// R�cup�ration de la r�f�rence sur la barre � onglets
		JTabbedPane tabbed_pane =
			(JTabbedPane)_frameComponents.get("TabbedPane");
		tabbed_pane.setSelectedIndex(3);
		// On tente de construire la requ�te
		RequestDefinition request =
			RequestFactory.buildRequest(_frameComponents, _tableDefinition,
			null);
		// On va v�rifier que la requ�te est valide
		if(request == null)
		{
			// La requ�te n'est pas valide, on sort
			trace_methods.endOfMethod();
			return;
		}
		try
		{
			// On ex�cute le processeur d'ex�cution de requ�te
			GenericTreeObjectNode selected_node =
				(GenericTreeObjectNode)getSelectedNode();
			RequestFactory.executeRequest(selected_node.getTableName(),
				request, selected_node, getMainWindowInterface());
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de l'ex�cution du processeur: " +
				exception.getMessage());
			// Il y a eu une erreur, on affiche un message
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotExecuteRequest", exception);
		}
		trace_methods.endOfMethod();
	}
}