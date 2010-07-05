/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/LabelsDialog.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de mise à jour de la table des libellés I-SIS
* DATE:        14/03/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: LabelsDialog.java,v $
* Revision 1.5  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.4  2006/11/09 12:09:13  tz
* Suppression d'un commentaire.
*
* Revision 1.3  2006/10/13 15:11:25  tz
* Gestion du mode de fonctionnement de l'Agent.
*
* Revision 1.2  2006/03/21 10:22:51  tz
* Remplacement des paramètres AvailableDictionaries.* en IClesDictionaries.*,
* Arrêt de la recherche du noeud parent lorsqu'il a été trouvé.
*
* Revision 1.1  2006/03/20 15:52:26  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin;

//
//Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.gui.IconLoader;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Vector;

//
//Imports du projet
//
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.ConsoleIconsManager;
import com.bv.isis.console.core.common.AgentLayerAbstractor;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.corbacom.IsisTableDefinition;

/*----------------------------------------------------------
* Nom: LabelsDialog
* 
* Description:
* Cette classe est une spécialisation de la classe BaseDialog chargée de 
* l'édition des libellés I-SIS, que ce soient les libellés du Portail, d'un 
* Agent ou même d'un I-CLES.
* La boîte de dialogue d'édition des libellés est subdivisées en trois zones, 
* la première permettant de définir l'identité du noeud, la deuxième 
* permettant de définir l'icône, et enfin, la dernière permettant de définir le 
* libellé du noeud.
* ----------------------------------------------------------*/
public class LabelsDialog
	extends BaseDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: LabelsDialog
	* 
	* Description:
	* Cette méthode est le seul constructeur de la classe. Elle permet 
	* d'initialiser une instance avec un certain nombre d'informations, 
	* principalement nécessaires à l'initialisation de la super-classe.
	* 
	* Si un problème survient lors de la création de l'instance, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - selectedNode: Une référence sur un objet GenericTreeObjectNode 
	*    représentant le noeud sélectionné,
	*  - action: L'identifiant unique de l'action d'administration,
	*  - dialogCaller: Une référence sur une interface DialogCallerInterface 
	*    représentant l'appelant de la boîte de dialogue.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public LabelsDialog(
		GenericTreeObjectNode selectedNode,
		String action,
		DialogCallerInterface dialogCaller
		)
		throws InnerException
	{
		super(selectedNode, action, dialogCaller);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "LabelsDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("action=" + action);
		trace_arguments.writeTrace("dialogCaller=" + dialogCaller);
		// On construit le panneau
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: validateInput
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée 
	* lorsque l'utilisateur a cliqué sur le bouton "Valider" de la boîte de 
	* dialogue.
	* La méthode vérifie les données saisies, construit la chaîne de données, 
	* puis la commande de mise à jour de la table, via la méthode execute().
	* ----------------------------------------------------------*/
	public void validateInput()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "validateInput");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		GenericTreeObjectNode selected_node = null;
		IsisTableDefinition definition = null;
		String table_type = "Table";
		String item_type = "Item";
		String node_id_field = "NodeId";
		String icon_field = "Icon";
		String label_field = "Label";
		StringBuffer node_id = new StringBuffer();
		String node_icon = null;
		String node_label = null;

		trace_methods.beginningOfMethod();
		// On va récupérer les informations depuis la configuration
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			table_type = configuration.getString("I-SIS", "TableObjectType");
			item_type = configuration.getString("InstanceObjectType");
			node_id_field = configuration.getString("Labels.NodeIdField");
			icon_field = configuration.getString("Labels.IconField");
			label_field = configuration.getString("Labels.LabelField");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " + 
				exception.getMessage());
			// On continue avec les valeurs par défaut
		}
		// On vérifie qu'il y a au moins la table concernée qui est
		// sélectionnée
		if(_tableName.getSelectedIndex() == -1)
		{
			trace_debug.writeTrace("Les données sont incomplètes !");
			// Les données ne sont pas complètes
			trace_methods.endOfMethod();
			return;			
		}
		// On va construire l'identité du noeud
		// S'il y a une valeur pour la table parent, on l'ajoute
		if(_parentTableName.getSelectedItem() != null)
		{
			node_id.append((String)_parentTableName.getSelectedItem());
			node_id.append(".");
		}
		// On ajoute le nom de la table concernée
		node_id.append((String)_tableName.getSelectedItem());
		// On ajoute le type de noeud concerné
		node_id.append(".");
		if(_instanceToggle.isSelected() == true)
		{
			node_id.append(item_type);
		}
		else
		{
			node_id.append(table_type);
		}
		// On récupère l'icône
		node_icon = _iconName.getText();
		// On récupère la chaîne de construction du noeud
		node_label = _labelString.getText();
		// On récupère la définition de la table
		selected_node = getSelectedNode();
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		try
		{
			definition = manager.getTableDefinition(
				selected_node.getAgentName(), selected_node.getIClesName(), 
				selected_node.getServiceType(), getTableName(), 
				selected_node.getContext(true), selected_node.getServiceSession());
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la récupération du " +
				"dictionnaire de la table " + getTableName() + ": " +
				exception.getMessage());
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On constuit la ligne des valeurs
		StringBuffer values = new StringBuffer();
		for(int index = 0 ; index < definition.columns.length ; index ++)
		{
			String value = "";
			String column_name = definition.columns[index].name;
			if(column_name.equals(node_id_field) == true)
			{
				value = node_id.toString();
			}
			else if(column_name.equals(icon_field) == true)
			{
				value = node_icon;
			}
			else
			{
				value = node_label;
			}
			if(index > 0)
			{
				values.append(definition.separator);
			}
			values.append(value);
		}
		// On libère l'utilisation de la définition
		manager.releaseTableDefinitionLeasing(definition);
		// On exécute la commande de mise à jour de la table
		execute(buildAdministrationCommand(values.toString()));
		hide();
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: makeFormPanel
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée 
	* lorsque le panneau de saisie des données doit être construit.
	* Dans le cas de la saisie des libellés des noeuds, le panneau est divisé 
	* en trois zones:
	*  - Une zone permettant la saisie de l'identifiant du noeud, créé via la 
	*    méthode makeNodeIdPanel(),
	*  - Une zone permettant la sélection de l'icône du noeud, créé via la 
	*    méthode makeLabelPanel(),
	*  - Une zone permettant la construction du libellé du noeud, créé via la 
	*    méthode makeIconPanel().
	* 
	* Si un problème survient lors de la construction du panneau de saisie, 
	* l'exception InnerException est levée.
	*
	* Retourne: Une référence sur un objet JPanel correspondant au panneau de 
	* saisie.
	*  
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	protected JPanel makeFormPanel()
		throws InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "makeFormsPanel");
		
		trace_methods.beginningOfMethod();
		// Message d'état
		getMainWindowInterface().setProgressMaximum(4);
		getMainWindowInterface().setStatus("&Status_BuildingDialog", null, 0);
		// On va créer les contraintes et le Layout Manager
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 
			0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(1, 1, 1, 1), 0, 0);
		// On commence par créer le panneau de saisie
		JPanel panel = new JPanel(layout);
		// On va créer la zone d'identification du noeud
		JPanel node_id_panel = makeNodeIdPanel();
		// On l'ajoute au panneau de saisie
		layout.setConstraints(node_id_panel, constraints);
		panel.add(node_id_panel);
		getMainWindowInterface().setStatus("&Status_BuildingDialog", null, 1);
		// On va créer la zone de définition de l'icône
		JPanel icon_panel = makeIconPanel();
		// On l'ajoute au panneau de saisie
		constraints.gridx ++;
		layout.setConstraints(icon_panel, constraints);
		panel.add(icon_panel);
		getMainWindowInterface().setStatus("&Status_BuildingDialog", null, 2);
		// On va créer la zone de définition du libellé
		JPanel label_panel = makeLabelPanel();
		// On l'ajoute au panneau de saisie
		constraints.gridx = 0;
		constraints.gridy ++;
		constraints.weightx = 1;
		constraints.gridwidth = 2;
		layout.setConstraints(label_panel, constraints);
		panel.add(label_panel);
		getMainWindowInterface().setStatus("&Status_BuildingDialog", null, 3);
		// On va appeler la méthode tableSelected(), pour s'assurer que tout
		// est conforme
		tableSelected();
		getMainWindowInterface().setStatus(null, null, 0);
		trace_methods.endOfMethod();
		return panel;
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: IconListCellRenderer
	* 
	* Description:
	* Cette classe imbriquée est une spécialisation de la classe 
	* DefaultListCellRenderer chargée de l'affichage des éléments de type 
	* icône. Elle redéfinit pour cela la méthode 
	* getListCellRendererComponent().
	* ----------------------------------------------------------*/
	private class IconListCellRenderer
		extends DefaultListCellRenderer
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: IconListCellRenderer
		* 
		* Description:
		* Cette méthode est le constructeur par défaut. Elle n'est présentée 
		* que pour des raisons de lisibilité.
		* ----------------------------------------------------------*/
		public IconListCellRenderer()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"IconListCellRenderer", "IconListCellRenderer");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
		}

		/*----------------------------------------------------------
		* Nom: getListCellRendererComponent
		* 
		* Description:
		* Cette méthode redéfinit celle de la super-classe. Elle est appelée 
		* afin de récupérer un objet Component gérant l'affichage d'une 
		* cellule de la liste.
		* Dans le cas de l'affichage des icônes, seule l'icône doit être 
		* affichée.
		* La valeur des éléments est un vecteur ne contenant que deux éléments 
		* (voir la classe ConsoleIconsManager): le premier étant l'icône, et 
		* le deuxième son identifiant.
		* 
		* Arguments:
		*  - list: Une référence sur un objet JList, correspondant à la liste 
		*    dont la cellule doit être "rendue",
		*  - value: Une référence sur un objet Object, correspondant à la 
		*    valeur de l'élément à "rendre",
		*  - index: L'indice de l'élément dans la liste,
		*  - isSelected: Un booléen indiquant si l'élément est sélectionné ou 
		*    non,
		*  - hasFocus: Un booléen indiquant si l'élément et la liste ont le 
		*    focus.
		* 
		* Retourne: Une référence sur un objet Component chargé du rendu de 
		* l'élément.
		* ----------------------------------------------------------*/
		public Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean hasFocus
			)
		{
			super.getListCellRendererComponent(list, value, index, isSelected,
				hasFocus);
			
			/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"IconListCellRenderer", "getListCellRendererComponent");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");*/
			Icon icon = null;

			/*trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("list=" + list);
			trace_arguments.writeTrace("value=" + value);
			trace_arguments.writeTrace("index=" + index);
			trace_arguments.writeTrace("isSelected=" + isSelected);
			trace_arguments.writeTrace("hasFocus=" + hasFocus);*/
			if(value != null && value instanceof Vector)
			{
				icon = (Icon)((Vector)value).elementAt(0);
			}
			setIcon(icon);
			setText(null);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
			//trace_methods.endOfMethod();
			return this;
		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
	};

	/*----------------------------------------------------------
	* Nom: _instanceToggle
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JRadioButton 
	* correspondant au bouton radio de sélection d'un noeud de type Item.
	* ----------------------------------------------------------*/
	private JRadioButton _instanceToggle;

	/*----------------------------------------------------------
	* Nom: _parentTableName
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JComboBox 
	* correspondant à la zone de définition de la table "parent".
	* ----------------------------------------------------------*/
	private JComboBox _parentTableName;

	/*----------------------------------------------------------
	* Nom: _tableName
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JComboBox 
	* correspondant à la zone de définition de la table concernée par le 
	* libellé.
	* ----------------------------------------------------------*/
	private JComboBox _tableName;

	/*----------------------------------------------------------
	* Nom: _iconsList
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JList correspondant à 
	* la liste des icônes utilisables pour le noeud.
	* ----------------------------------------------------------*/
	private JList _iconsList;

	/*----------------------------------------------------------
	* Nom: _iconName
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JTextField 
	* correspondant à la zone de saisie de l'identifiant de l'icône.
	* ----------------------------------------------------------*/
	private JTextField _iconName;

	/*----------------------------------------------------------
	* Nom: _parametersList
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JList correspondant à 
	* la liste des paramètres du contexte du noeud.
	* ----------------------------------------------------------*/
	private JList _parametersList;

	/*----------------------------------------------------------
	* Nom: _addButton
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JButton correspondant 
	* au bouton "Ajouter".
	* ----------------------------------------------------------*/
	private JButton _addButton;

	/*----------------------------------------------------------
	* Nom: _labelString
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JTextField 
	* correspondant à la zone de saisie de la chaîne du construction du 
	* libellé du noeud.
	* ----------------------------------------------------------*/
	private JTextField _labelString;

	/*----------------------------------------------------------
	* Nom: makeNodeIdPanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de l'une des trois zones du 
	* panneau de saisie: la zone d'identification du noeud.
	* Elle est constituée de deux boutons radio, permettant de sélectionner 
	* s'il s'agit d'un noeud Table ou d'un noeud Item, d'une boîte combo 
	* permettant la sélection ou la saisie du nom d'une table "parent", et 
	* d'une autre boîte combo permettant la sélection ou la saisie du nom de 
	* la table concernée par le libellé.
	* 
	* Retourne: Un objet JPanel correspondant à la zone de saisie de 
	* l'identifiant de noeud.
	* ----------------------------------------------------------*/
	private JPanel makeNodeIdPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "makeNodeIdPanel");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean is_restricted = false;
		String table_type = "Table";
		String item_type = "Item";
		String dictionaries_table = "IClesDictionaries";
		String dictionaries_field = "TableName";
		String dictionaries_path = "DictionaryPath";
		GenericTreeObjectNode selected_node = null;
		String[] tables_list = new String[0];

		trace_methods.beginningOfMethod();
		selected_node = getSelectedNode();
		// On va récupérer les informations depuis la configuration
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			table_type = configuration.getString("I-SIS", "TableObjectType");
			item_type = configuration.getString("InstanceObjectType");
			dictionaries_table = 
				configuration.getString("IClesDictionaries.Table");
			dictionaries_field = 
				configuration.getString("IClesDictionaries.TableField");
			dictionaries_path = 
				configuration.getString("IClesDictionaries.PathField");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " + 
				exception.getMessage());
		}
		// On va déterminer si l'édition est restreinte ou non
		is_restricted = (getParameters().indexOf("@") > -1);
		trace_debug.writeTrace("is_restricted=" + is_restricted);
		// On va créer les contraintes et le Layout Manager
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 
			0, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(1, 1, 1, 1), 0, 0);
		// On commence par créer le panneau de la zone
		JPanel panel = new JPanel(layout);
		// On crée une bordure titrée pour le panneau de la zone
		panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Admin_NodeId")));
		// On crée le libellé du champ Type de noeud
		JLabel type_label = new JLabel(MessageManager.getMessage(
			"&Admin_NodeType"));
		constraints.gridheight = 2;
		layout.setConstraints(type_label, constraints);
		panel.add(type_label);
		// On crée le libellé du champ Table parent
		JLabel parent_label = new JLabel(MessageManager.getMessage(
			"&Admin_ParentTable"));
		constraints.gridheight = 1;
		constraints.gridy += 2;
		layout.setConstraints(parent_label, constraints);
		panel.add(parent_label);
		// On crée le libellé du champ Table
		JLabel table_label = new JLabel(MessageManager.getMessage(
			"&Admin_TableName"));
		constraints.gridy ++;
		layout.setConstraints(table_label, constraints);
		panel.add(table_label);
		// On crée le bouton radio de noeud de type Item
		_instanceToggle = new JRadioButton(MessageManager.getMessage(
			"&Admin_InstanceNodeType"), true);
		// On ajoute un callback sur la sélection
		_instanceToggle.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				tableSelected();
			}
		});
		setEnterCallback(_instanceToggle);
		constraints.gridx ++;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.insets.bottom = 0;
		layout.setConstraints(_instanceToggle, constraints);
		panel.add(_instanceToggle);
		// On crée le bouton radio de noeud de type Table
		JRadioButton class_toggle = 
			new JRadioButton(MessageManager.getMessage(
			"&Admin_ClassNodeType"), false);
		// On ajoute un callback sur la sélection
		class_toggle.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				tableSelected();
			}
		});
		setEnterCallback(class_toggle);
		constraints.gridy ++;
		constraints.insets.top = 0;
		constraints.insets.bottom = 1;
		layout.setConstraints(class_toggle, constraints);
		panel.add(class_toggle);
		// On va créer un groupe pour les boutons radio
		ButtonGroup group = new ButtonGroup();
		group.add(_instanceToggle);
		group.add(class_toggle);
		// On crée la boîte combo de la table parent
		_parentTableName = new JComboBox();
		_parentTableName.setEditable(true);
		// On ajoute un callback sur la sélection
		_parentTableName.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				// On ne réagit qu'aux événements de sélection
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					tableSelected();
				}
			}
		});
		// On l'ajoute au panneau
		constraints.gridy ++;
		constraints.insets.top = 1;
		layout.setConstraints(_parentTableName, constraints);
		panel.add(_parentTableName);
		// On crée la boîte combo de la table concernée
		_tableName = new JComboBox();
		_tableName.setEnabled(!is_restricted);
		// On ajoute un callback sur la sélection
		_tableName.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				// On ne réagit qu'aux événements de sélection
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					tableSelected();
				}
			}
		});
		setEnterCallback(_tableName);
		constraints.gridy ++;
		layout.setConstraints(_tableName, constraints);
		panel.add(_tableName);
		// Si on n'est pas en mode Ajout, on va remplir les champs à partir
		// des données du noeud sélectionné
		if(getAction().equals("Insert") == false)
		{
			_parentTableName.setEnabled(false);
			_tableName.setEnabled(false);
			_instanceToggle.setEnabled(false);
			class_toggle.setEnabled(false);
			String node_identity = 
				selected_node.getObjectParameters()[0].value;
			UtilStringTokenizer tokenizer = 
				new UtilStringTokenizer(node_identity, ".");
			// Il doit y avoir au moins 2 et au plus 3 sous-chaînes
			if(tokenizer.getTokensCount() < 2 || 
				tokenizer.getTokensCount() > 3)
			{
				trace_errors.writeTrace("Identité du noeud invalide: " +
					node_identity);
				// On affiche un message à l'utilisateur
				String[] extra_info = { node_identity };
				getMainWindowInterface().showPopup("Error", 
					"&ERR_InvalidNodeIdentity", extra_info);
				// On sort
				trace_methods.endOfMethod();
				return panel;
			}
			// La dernière sous-chaîne doit correspondre à un type Table ou
			// un type Item
			String object_type = 
				tokenizer.getToken(tokenizer.getTokensCount() - 1); 
			if(object_type.equals(table_type) == true)
			{
				class_toggle.setSelected(true);
			}
			else if(object_type.equals(item_type) == true)
			{
				_instanceToggle.setSelected(true);
			}
			else
			{
				trace_errors.writeTrace("Identité du noeud invalide: " +
					node_identity);
				// On affiche un message à l'utilisateur
				String[] extra_info = { node_identity };
				getMainWindowInterface().showPopup("Error", 
					"&ERR_InvalidNodeIdentity", extra_info);
				// On sort
				trace_methods.endOfMethod();
				return panel;
			}
			if(tokenizer.getTokensCount() == 3)
			{
				// Il y a une table parent, son nom est contenu dans la
				// première sous-chaîne
				_parentTableName.addItem(tokenizer.getToken(0));
				// Le nom de la table provient de la deuxième sous-chaîne
				_tableName.addItem(tokenizer.getToken(1));
			}
			else
			{
				// Il n'y a pas de table parent, le nom de la table provient 
				// de la première sous-chaîne
				_tableName.addItem(tokenizer.getToken(0));
			}
			// On sort
			trace_methods.endOfMethod();
			return panel;
		}
		// On va tenter de pré-remplir les boîtes combo
		if(is_restricted == true)
		{
			// Dans le cas d'une restriction à la table correspondant au noeud
			// sélectionné, le nom de la table provient du noeud
			GenericTreeObjectNode parent_node = null;
			String table_name = null;
			
			table_name = selected_node.getTableName(); 
			_tableName.addItem(table_name);
			_tableName.setSelectedIndex(0);
			// On va tenter de remonter jusqu'à la table parent
			parent_node = selected_node;
			while(parent_node != null)
			{
				String parent_table = parent_node.getTableName(); 
				if(parent_table != null && parent_table.equals("") == false &&
					parent_table.equals(table_name) == false)
				{
					// On a trouvé la table parent
					_parentTableName.addItem(parent_table);
					_parentTableName.setSelectedIndex(-1);
					break;
				}
				parent_node = (GenericTreeObjectNode)parent_node.getParent();				
			}
			trace_methods.endOfMethod();
			return panel;
		}
		// Dans le cas d'une édition sans restriction, les deux boîtes 
		// combo sont pré-remplies à partir de la liste des
		// dictionnaires disponibles dans le contexte courant
		// On va récupérer la liste des dictionnaires, donc des tables, en
		// effectuant une sélection sur la table
		try
		{
			String condition = null;
			String[] selected_columns = { dictionaries_field };
			
			if(getTableName().equals("IsisLabels") == true)
			{
				String agent_layer_mode = 
				 AgentSessionManager.getInstance().getAgentLayerMode(
				 selected_node.getAgentName());
				// On va limiter la recherche des tables aux seules apparenant
				// au SIS I-SIS
				condition = dictionaries_path + "~" + 
					AgentLayerAbstractor.getVariableReference(
					agent_layer_mode, "ISIS_SIS");
			}
			ServiceSessionProxy proxy = 
				new ServiceSessionProxy(selected_node.getServiceSession());
			tables_list = proxy.getWideSelectResult(
				selected_node.getAgentName(), dictionaries_table, 
				selected_columns, condition, null, 
				selected_node.getContext(true));
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la liste des tables: " + 
				exception.getMessage());
			// On va afficher un message à l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotRetrieveTablesList" , exception);
			// On va continuer quand même
		}
		// On va remplir les deux boîtes à partir du résultat de la sélection
		for(int index = 1 ; index < tables_list.length ; index ++)
		{
			_parentTableName.addItem(tables_list[index]);
			_tableName.addItem(tables_list[index]);
		}
		_parentTableName.setSelectedIndex(-1);
		_tableName.setSelectedIndex(-1);
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: makeIconPanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de l'une des trois zones du 
	* panneau de saisie: la zone de définition de l'icône associée au noeud.
	* Elle est constituée d'une liste déroulante représentant l'ensemble des 
	* icônes de noeud disponibles, récupérée via la classe ConsoleIconsManager, 
	* et d'une zone de saisie permettant de saisir ou de modifier 
	* l'identifiant de l'icône associée au noeud.
	* 
	* Retourne: Une référence sur un objet JPanel correspondant à la zone de 
	* définition de l'icône.
	* ----------------------------------------------------------*/
	private JPanel makeIconPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "makeIconPanel");
		Vector icons_list = null;

		trace_methods.beginningOfMethod();
		// On va créer les contraintes et le Layout Manager
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 2, 1, 
			1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(1, 1, 1, 1), 0, 0);
		// On commence par créer le panneau de la zone
		JPanel panel = new JPanel(layout);
		// On crée une bordure titrée pour le panneau de la zone
		panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Admin_Icon")));
		// On récupère la liste des icônes de noeud
		icons_list = ConsoleIconsManager.getNodeIcons();
		// On va construire la liste des icônes
		_iconsList = new JList(icons_list);
		setEnterCallback(_iconsList);
		// On ne veut pas procéder au calcul des hauteurs et largeur des
		// cellules, aussi on va fournir un prototype
		Vector prototype = new Vector();
		prototype.add(IconLoader.getIcon("NodeIconPrototype"));
		prototype.add("NodeIconPrototype");
		_iconsList.setPrototypeCellValue(prototype);
		// On spécifie une orientation de layout
		_iconsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		// On va définir le nombre de lignes visibles
		int visible_rows = icons_list.size() / 10;
		if(visible_rows % 10 > 0)
		{
			visible_rows += 1;
		}
		_iconsList.setVisibleRowCount(visible_rows);
		// On spécifie le gestionnaire de rendu
		_iconsList.setCellRenderer(new IconListCellRenderer());
		// On ajoute le callback sur la sélection
		_iconsList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				iconSelected();
			}
		});
		// On va la mettre dans un JScrollPane
		JScrollPane scroll_pane = new JScrollPane(_iconsList);
		scroll_pane.setPreferredSize(new Dimension(10, 10));
		// On l'ajoute à la zone
		layout.setConstraints(scroll_pane, constraints);
		panel.add(scroll_pane);
		// On crée le label pour l'identifiant
		JLabel icon_label = new JLabel(MessageManager.getMessage(
			"&Admin_IconIdentity"));
		// On l'ajoute à la zone
		constraints.gridy ++;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 1;
		layout.setConstraints(icon_label, constraints);
		panel.add(icon_label);
		// On crée la zone de saisie de l'identifiant
		_iconName = new JTextField(15);
		setEnterCallback(_iconName);
		// On l'ajoute à la zone
		constraints.gridx ++;
		constraints.weightx = 1;
		layout.setConstraints(_iconName, constraints);
		panel.add(_iconName);
		// Si on n'est pas en mode Ajout, on va remplir les champs à partir
		// des données du noeud sélectionné
		if(getAction().equals("Insert") == false)
		{
			_iconName.setText(getSelectedNode().getObjectParameters()[1].value);
			// On va rechercher dans la liste des icônes celui qui pourrait
			// correspondre
			for(int index = 0 ; index < icons_list.size() ; index ++)
			{
				Vector icon_id_vector = (Vector)icons_list.elementAt(index);
				String icon_id = (String)icon_id_vector.elementAt(1);
				if(icon_id.equals(_iconName.getText()) == true)
				{
					_iconsList.setSelectedIndex(index);
					_iconsList.ensureIndexIsVisible(index);
					iconSelected();
					break;
				}
			}
			// Si on est en mode suppression, on doit désactiver la liste et la
			// zone de saisie
			if(getAction().equals("Remove") == true)
			{
				_iconsList.setEnabled(false);
				_iconName.setEnabled(false);
			}
		}
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: makeLabelPanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de l'une des trois zones du 
	* panneau de saisie: la zone de construction du libellé du noeud.
	* Elle est constituée d'une liste déroulante proposant l'ensemble des noms 
	* des paramètres constituant le contexte du noeud sélectionné, d'un bouton 
	* "Ajouter" permettant d'ajouter un nom d'identifiant dans la chaîne de 
	* construction du libellé, et d'une zone de saisie contenant la chaîne de 
	* construction du libellé.
	* 
	* Retourne: Une référence sur un objet JPanel correspondant à la zone de 
	* construction du libellé.
	* ----------------------------------------------------------*/
	private JPanel makeLabelPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "makeLabelPanel");

		trace_methods.beginningOfMethod();
		// On va créer les contraintes et le Layout Manager
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 3, 1, 
			1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(1, 1, 1, 1), 0, 0);
		// On commence par créer le panneau de la zone
		JPanel panel = new JPanel(layout);
		// On crée une bordure titrée pour le panneau de la zone
		panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Admin_NodeLabel")));
		// Création du label pour la chaîne de construction
		JLabel label_string = new JLabel(MessageManager.getMessage(
			"&Admin_LabelString"));
		layout.setConstraints(label_string, constraints);
		panel.add(label_string);
		// Création de la zone de saisie pour la chaîne de construction
		_labelString = new JTextField(15);
		setEnterCallback(_labelString);
		constraints.gridy ++;
		layout.setConstraints(_labelString, constraints);
		panel.add(_labelString);
		// Création du label pour les variables utilisables
		JLabel variables_label = new JLabel(MessageManager.getMessage(
			"&Admin_Variables"));
		constraints.gridy ++;
		layout.setConstraints(variables_label, constraints);
		panel.add(variables_label);
		// On va créer la liste destinée à recevoir les variables
		_parametersList = new JList(new DefaultListModel());
		setEnterCallback(_parametersList);
		// On va ajouter un callback sur la sélection d'un élément
		_parametersList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				parameterSelected();
			}
		});
		JScrollPane scroll_pane = new JScrollPane(_parametersList);
		scroll_pane.setPreferredSize(new Dimension(200, 70));
		// On l'ajoute au panneau
		constraints.gridy ++;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets.right = 2;
		layout.setConstraints(scroll_pane, constraints);
		panel.add(scroll_pane);
		// Et enfin, on crée le bouton Ajouter
		_addButton = new JButton(MessageManager.getMessage(
			"&Admin_AddButton"));
		_addButton.setEnabled(false);
		// On ajoute le callback sur click
		_addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addParameter();
			}
		});
		// On ajoute le bouton au panneau
		constraints.gridx ++;
		constraints.weightx = 0;
		constraints.insets.right = 1;
		constraints.insets.left = 2;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.NONE;
		layout.setConstraints(_addButton, constraints);
		panel.add(_addButton);
		// On ajoute un zone étirable, afin de limiter la taille de la liste
		Component horizontal_glue = Box.createHorizontalGlue();
		constraints.gridx ++;
		constraints.insets.left = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		layout.setConstraints(horizontal_glue, constraints);
		panel.add(horizontal_glue);
		// On va pré-remplir la zone de saisie si on n'est pas en insertion
		if(getAction().equals("Insert") == false)
		{
			_labelString.setText(
				getSelectedNode().getObjectParameters()[2].value);
			// Si on est en suppression, il faut désactiver les zones
			if(getAction().equals("Remove") == true)
			{
				_labelString.setEnabled(false);
				_parametersList.setEnabled(false);
			}
		}
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: tableSelected
	* 
	* Description:
	* Cette méthode est automatiquement appelée lorsque l'utilisateur a 
	* sélectionné une table dans la liste des tables concernées par le 
	* libellé.
	* Elle charge la définition de la table sélectionnée, puis construit la 
	* liste des variables utilisables à partir de la liste des colonnes de la 
	* table.
	* De plus, s'il s'agit d'une édition de libellés par rapport à un noeud 
	* déterminé, les variables constituant le contexte du noeud sont ajoutées.
	* ----------------------------------------------------------*/
	private void tableSelected()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "tableSelected");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		GenericTreeObjectNode selected_node = null;
		IsisTableDefinition definition = null;
		DefaultListModel list_model = null;
		
		trace_methods.beginningOfMethod();
		// Si la liste n'est pas encore créée, on sort
		if(_parametersList == null)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On commence par vider la liste des variables utilisables
		list_model = (DefaultListModel)_parametersList.getModel();
		list_model.removeAllElements();
		_addButton.setEnabled(false);
		// On regarde s'il y a une table sélectionnée
		if(_tableName.getSelectedIndex() == -1)
		{
			trace_debug.writeTrace("Aucune table n'est sélectionnée");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		selected_node = getSelectedNode();
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		getMainWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, this);
		// Si le noeud concerné est de type instance, il faut que l'on
		// ajoute les paramètres de la table sélectionnée
		if(_instanceToggle.isSelected() == true)
		{
			trace_debug.writeTrace("Noeud de type Item");
			String table_name = (String)_tableName.getSelectedItem();
			trace_debug.writeTrace("Chargement du dictionnaire de la table " +
				table_name);
			try
			{
				definition = manager.getTableDefinition(
					selected_node.getAgentName(), 
					selected_node.getIClesName(), 
					selected_node.getServiceType(), table_name, 
					selected_node.getContext(true),
					selected_node.getServiceSession());
			}
			catch(InnerException exception)
			{
				trace_errors.writeTrace(
					"Impossible de charger le dictionnaire de la table " + 
					table_name + ": " + exception.getMessage());
				getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
				// On va afficher un message à l'utilisateur et sortir
				getMainWindowInterface().showPopupForException(
					"&ERR_CannotLoadTableDictionary", exception);
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// On va ajouter l'ensemble des noms des colonnes
			for(int index = 0 ; index < definition.columns.length ; index ++)
			{
				trace_debug.writeTrace("Ajout du paramètre: " +
					definition.columns[index].name);
				list_model.addElement(definition.columns[index].name);
			}
			// On libère la définition de la table
			manager.releaseTableDefinitionLeasing(definition);
		}
		// S'il y a une table parent sélectionnée, on va ajouter les 
		// paramètres qui constituent sa clé primaire
		if(_parentTableName.getSelectedItem() != null && 
			_parentTableName.getSelectedItem().toString().equals("") == false)
		{  
			String table_name = (String)_parentTableName.getSelectedItem();
			trace_debug.writeTrace("Chargement du dictionnaire de la table " +
				table_name);
			try
			{
				definition = manager.getTableDefinition(
					selected_node.getAgentName(), 
					selected_node.getIClesName(), 
					selected_node.getServiceType(), table_name, 
					selected_node.getContext(true),
					selected_node.getServiceSession());
			}
			catch(InnerException exception)
			{
				trace_errors.writeTrace(
					"Impossible de charger le dictionnaire de la table " + 
					table_name + ": " + exception.getMessage());
				getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
				// On va afficher un message à l'utilisateur et sortir
				getMainWindowInterface().showPopupForException(
					"&ERR_CannotLoadTableDictionary", exception);
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// On va ajouter l'ensemble des noms des colonnes
			for(int index = 0 ; index < definition.key.length ; index ++)
			{
				trace_debug.writeTrace("Ajout du paramètre: " +
					definition.key[index]);
				list_model.addElement(definition.key[index]);
			}
			// On libère la définition de la table
			manager.releaseTableDefinitionLeasing(definition);
		}
		getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, this);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: iconSelected
	* 
	* Description:
	* Cette méthode est automatiquement appelée lorsque l'utilisateur 
	* sélectionne ou désélectionne un élément dans la liste des icônes.
	* Le contenu de la zone de saisie de l'identifiant de l'icône est mise à 
	* jour en fonction de la sélection.
	* ----------------------------------------------------------*/
	private void iconSelected()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "iconSelected");
		int selected_icon;
		
		trace_methods.beginningOfMethod();
		selected_icon = _iconsList.getSelectedIndex();
		if(selected_icon > -1)
		{
			// Il y a une icône sélectionnée, on va placer son identifiant 
			// dans la zone de saisie
			String icon_id = (String) 
				((Vector)_iconsList.getSelectedValue()).elementAt(1);
			_iconName.setText(icon_id);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: parameterSelected
	* 
	* Description:
	* Cette méthode est automatiquement appelée lorsque l'utilisateur 
	* sélectionne ou désélectionne un élément dans la liste des paramètres du 
	* contexte.
	* L'état du bouton "Ajouter" est mis à jour en fonction de la sélection.
	* ----------------------------------------------------------*/
	private void parameterSelected()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "parameterSelected");

		trace_methods.beginningOfMethod();
		// On met à jour l'état du bouton Ajouter en fonction de l'existence
		// d'une sélection dans la liste des paramètres
		_addButton.setEnabled(_parametersList.getSelectedIndex() != -1);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: addParameter
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le bouton 
	* "Ajouter".
	* Le nom du paramètre sélectionné dans la liste est ajouté dans la zone de 
	* saisie, à la position du curseur, suivant la forme "%[<paramètre>]".
	* ----------------------------------------------------------*/
	private void addParameter()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LabelsDialog", "addParameter");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String parameter_name = null;

		trace_methods.beginningOfMethod();
		// On vérifie qu'un paramètre est bien sélectionné dans la liste
		if(_parametersList.getSelectedIndex() == -1)
		{
			_addButton.setEnabled(false);
			trace_methods.endOfMethod();
			return;
		}
		// On va récupérer le nom du paramètre sélectionné
		parameter_name = (String)_parametersList.getSelectedValue();
		trace_debug.writeTrace("Ajout du paramètre " + parameter_name +
			" à la chaîne de construction");
		try
		{
			_labelString.getDocument().insertString(_labelString.getCaretPosition(),
				"%[" + parameter_name + "]", null);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de l'insertion du paramètre: " +
				exception.getMessage());
			// On ne fait rien
		}
		trace_methods.endOfMethod();
	}
}
