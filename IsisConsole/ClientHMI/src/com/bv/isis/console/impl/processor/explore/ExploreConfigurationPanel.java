/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/explore/ExploreConfigurationPanel.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Boîte de configuration de l'exploration automatique
* DATE:        22/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.explore
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ExploreConfigurationPanel.java,v $
* Revision 1.5  2009/01/23 17:25:24  tz
* Correction de la fiche FS#577.
* Ajout d'une case à cocher d'activation/désactivation de la suppression
* des noeuds intermédiaires, et une autre de chargement automatique
* des menus.
*
* Revision 1.4  2009/01/15 16:52:42  tz
* Modification de la mise en page du panneau de configuration.
*
* Revision 1.3  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2005/07/01 12:14:04  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/10/22 15:38:40  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.explore;

//
//Imports système
//
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import com.bv.core.prefs.PreferencesAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.config.ConfigurationAPI;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: ExploreConfigurationPanel
* 
* Description:
* Cette classe est une spécialisation de la classe JPanel chargée de la 
* configuration de l'exploration automatique des noeuds d'exploration.
* Elle implémente l'interface ConfigurationPanelInterface de sorte à pouvoir 
* être intégrée à la boîte de configuration de la Console.
* ----------------------------------------------------------*/
public class ExploreConfigurationPanel
	extends JPanel
	implements ConfigurationPanelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ExploreConfigurationPanel
	* 
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Elle 
	* construit le panneau de configuration en appelant la méthode makePanel().
	* ----------------------------------------------------------*/
	public ExploreConfigurationPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "ExploreConfigurationPanel");
			
		trace_methods.beginningOfMethod();
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: validateConfiguration
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appelée pour vérifier que les données de configuration sont 
	* valides.
	* Dans ce cas, les données sont toujours valides.
	* 
	* Arguments:
	*  - windowInterface: Non utilisé.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean validateConfiguration(
		MainWindowInterface windowInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "validateConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: storeConfiguration
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appelée pour enregistrer les données de configuration.
	* La méthode vérifie la validité des données en appelant la méthode 
	* validateConfiguration(), puis enregistre les informations dans le 
	* fichier de préférences, section "AUTO-EXPLORE".
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    nécessaire pour afficher un message à l'utilisateur.
	* 
	* Retourne: true si la configuration a été enregistrée, false sinon.
	* ----------------------------------------------------------*/
	public boolean storeConfiguration(
		MainWindowInterface windowInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "storeConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean do_automatic_explore = false;
		boolean remove_unnecessary_nodes = true;
		boolean preload_menus = true;
		StringBuffer table_list = new StringBuffer();
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// On commence par vérifier la configuration
		if(validateConfiguration(windowInterface) == false)
		{
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		// On récupère les données de configuration
		preload_menus = _preloadMenus.isSelected();
		remove_unnecessary_nodes = _removeUnnecessaryNodes.isSelected();
		do_automatic_explore = _doAutomaticExplore.isSelected();
		DefaultListModel list_model = (DefaultListModel)_tableList.getModel();
		for(int index = 0 ; index < list_model.size() ; index ++)
		{
			if(index != 0)
			{
				table_list.append(',');
			}
			table_list.append(list_model.getElementAt(index));
		}
		// On va enregistrer la configuration dans le fichier de préférences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			// On ajoute la section Execution (au cas où)
			preferences.addSection("AUTO-EXPLORE");
			preferences.set("Enabled", do_automatic_explore);
			preferences.set("Tables", table_list.toString());
			preferences.set("RemoveUnnecessaryNodes", 
				remove_unnecessary_nodes);
			preferences.set("PreloadMenus", preload_menus);
			preferences.write();
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace(
				"Erreur lors de l'enregistrement des préférences: " +
				exception);
			// On va en informer l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotSaveConfiguration",
				exception);
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getPanelTitle
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appelée pour récupérer l'intitulé du panneau de configuration.
	* 
	* Retourne: L'intitulé du panneau de configuration.
	* ----------------------------------------------------------*/
	public String getPanelTitle()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "getPanelTitle");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&Explore_PanelTitle");
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _tableList
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JList chargé de 
	* contenir la liste des tables à explorer automatiquement.
	* ----------------------------------------------------------*/
	private JList _tableList;

	/*----------------------------------------------------------
	* Nom: _addButton
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JButton correspondant 
	* au bouton "Ajouter".
	* ----------------------------------------------------------*/
	private JButton _addButton;

	/*----------------------------------------------------------
	* Nom: _removeButton
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JButton correspondant 
	* au bouton "Retirer".
	* ----------------------------------------------------------*/
	private JButton _removeButton;

	/*----------------------------------------------------------
	* Nom: _doAutomaticExplore
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JCheckBox correspond à 
	* la case à cocher d'activation de l'exploration automatique.
	* ----------------------------------------------------------*/
	private JCheckBox _doAutomaticExplore;

	/*----------------------------------------------------------
	* Nom: _removeUnnecessaryNodes
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JCheckBox 
	* correspond à la case à cocher d'activation de la suppression des 
	* noeuds intermédiaires non nécessaires.
	* ----------------------------------------------------------*/
	private JCheckBox _removeUnnecessaryNodes;

	/*----------------------------------------------------------
	* Nom: _preloadMenus
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JCheckBox 
	* correspond à la case à cocher d'activation du chargement automatique 
	* des menus des noeuds.
	* ----------------------------------------------------------*/
	private JCheckBox _preloadMenus;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode permet de construire le panneau de configuration de 
	* l'exploration automatique. Le panneau de configuration contient une 
	* case à cocher servant à indiquer si l'exploration automatique est 
	* activée ou non, ainsi qu'une liste destinée à contenir les noms des 
	* tables à explorer automatiquement, et des boutons permettant d'ajouter 
	* ou de retirer des tables.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "makePanel");
		boolean do_automatic_explore = false;
		boolean remove_unnecessary_nodes = true;
		boolean preload_menus = true;
		String table_list = null;
			
		trace_methods.beginningOfMethod();
		// On récupère les données de configuration
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			// On utilise la section AUTO-EXPLORE
			preferences.useSection("AUTO-EXPLORE");
			do_automatic_explore = preferences.getBoolean("Enabled");
			table_list = preferences.getString("Tables");
			remove_unnecessary_nodes = 
				preferences.getBoolean("RemoveUnnecessaryNodes");
			preload_menus = preferences.getBoolean("PreloadMenus");
		}
		catch(Exception exception)
		{
			// On tente de récupérer depuis la configuration
			try
			{
				ConfigurationAPI configuration_api = new ConfigurationAPI();
				do_automatic_explore =
					configuration_api.getBoolean("AUTO-EXPLORE",
					"AutoExplore.Enabled");
				table_list = configuration_api.getString("AutoExplore.TableNames");
			}
			catch(Exception exception2)
			{
				// On ne fait rien
				table_list = "";
			}
		}
		
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0,
			2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
		setLayout(bag_layout);
		// Création de la case à cocher d'activation du chargement 
		// automatique des menus
		_preloadMenus = new JCheckBox(
			MessageManager.getMessage("&Explore_PreloadMenus"));
		_preloadMenus.setSelected(remove_unnecessary_nodes == true || 
			preload_menus == true);
		bag_layout.setConstraints(_preloadMenus, constraints);
		add(_preloadMenus);
		// Création de la case à cocher d'activation de la suppression des
		// noeuds intermédiaires
		_removeUnnecessaryNodes = new JCheckBox(
			MessageManager.getMessage("&Explore_RemoveUnnecessaryNodes"));
		_removeUnnecessaryNodes.setSelected(remove_unnecessary_nodes);
		// On ajoute le listener sur la sélection
		_removeUnnecessaryNodes.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				removeStateChanged();
			}
		});
		constraints.gridy ++;
		bag_layout.setConstraints(_removeUnnecessaryNodes, constraints);
		add(_removeUnnecessaryNodes);
		// Création de la case à cocher d'activation de l'exploration
		// automatique
		_doAutomaticExplore = new JCheckBox(
			MessageManager.getMessage("&Explore_AutoExplore"));
		_doAutomaticExplore.setSelected(do_automatic_explore);
		_doAutomaticExplore.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				exploreStateChanged();
			}
		});
		constraints.gridy ++;
		bag_layout.setConstraints(_doAutomaticExplore, constraints);
		add(_doAutomaticExplore);
		// Création du label
		JLabel label = new JLabel(
			MessageManager.getMessage("&Explore_Label"));
		constraints.gridy ++;
		bag_layout.setConstraints(label, constraints);
		add(label);
		// Création de la liste des tables
		DefaultListModel list_model = new DefaultListModel();
		UtilStringTokenizer tokenizer = 
			new UtilStringTokenizer(table_list, ",");
		while(tokenizer.hasMoreTokens() == true)
		{
			list_model.addElement(tokenizer.nextToken());
		}
		_tableList = new JList(list_model);
		_tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_tableList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				listSelectionChanged();
			}
		});
		JScrollPane list_scroll = new JScrollPane(_tableList);
		list_scroll.setBorder(BorderFactory.createEtchedBorder());
		constraints.gridwidth = 1;
		constraints.gridheight = 2;
		constraints.gridy ++;
		constraints.weighty = 1;
		constraints.fill = GridBagConstraints.BOTH;
		bag_layout.setConstraints(list_scroll, constraints);
		add(list_scroll);
		// Création du bouton "Ajouter"
		_addButton = new JButton(
			MessageManager.getMessage("&Explore_Add"));
		_addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addTable();
			}
		});
		constraints.anchor = GridBagConstraints.SOUTH;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0.5;
		constraints.gridx ++;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		bag_layout.setConstraints(_addButton, constraints);
		add(_addButton);
		// Création du bouton "Retirer"
		_removeButton = new JButton(
			MessageManager.getMessage("&Explore_Remove"));
		_removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				removeTable();
			}
		});
		constraints.gridy ++;
		constraints.anchor = GridBagConstraints.NORTH;
		bag_layout.setConstraints(_removeButton, constraints);
		add(_removeButton);
		// On s'assure de l'état de tous les éléments
		removeStateChanged();
		exploreStateChanged();
		listSelectionChanged();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: exploreStateChanged
	* 
	* Description:
	* Cette méthode est appelée lorsque l'état de la case à cocher 
	* d'exploration automatique a changé. Suivant son état, la liste et les 
	* boutons sont activés ou non.
	* ----------------------------------------------------------*/
	private void exploreStateChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "exploreStateChanged");
			
		trace_methods.beginningOfMethod();
		// On change l'état de la liste et des boutons en fonction de l'état
		// de la case à cocher
		_tableList.setEnabled(_doAutomaticExplore.isSelected());
		_addButton.setEnabled(_doAutomaticExplore.isSelected());
		_removeButton.setEnabled(_doAutomaticExplore.isSelected());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: listSelectionChanged
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur un élément 
	* de la liste des tables à explorer. Suivant qu'un élément est sélectionné 
	* ou non, l'état du bouton "Retirer" est mis à jour.
	* ----------------------------------------------------------*/
	private void listSelectionChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "listSelectionChanged");
			
		trace_methods.beginningOfMethod();
		// On modifie l'état du bouton en fonction de la sélection d'un
		// élément ou non dans la liste
		_removeButton.setEnabled(!_tableList.isSelectionEmpty());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: addTable
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le 
	* bouton "Ajouter". Elle ouvre une boîte de saisie permettant à 
	* l'utilisateur de saisir le nom d'une table à ajouter à la liste.
	* ----------------------------------------------------------*/
	private void addTable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "addTable");
			
		trace_methods.beginningOfMethod();
		// On va afficher une boîte de saisie pour le nom de la table
		String new_table = JOptionPane.showInputDialog(this, 
			MessageManager.getMessage("&Explore_TableName"), 
			_addButton.getText(), JOptionPane.QUESTION_MESSAGE);
		// Si le nom de la table est vide ou null, on sort
		if(new_table == null || new_table.equals("") == true)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le modèle, et on insère la nouvelle table
		DefaultListModel list_model = (DefaultListModel)_tableList.getModel();
		list_model.addElement(new_table);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: removeTable
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le 
	* bouton "Retirer". L'élément sélectionné dans la liste est supprimé.
	* ----------------------------------------------------------*/
	private void removeTable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "removeTable");
			
		trace_methods.beginningOfMethod();
		// Y a-t-il un élément sélectionné ?
		if(_tableList.isSelectionEmpty() == true)
		{
			// On sort
			_removeButton.setEnabled(false);
			trace_methods.endOfMethod();
			return;
		}
		// On récupère le modèle de la liste
		DefaultListModel list_model = (DefaultListModel)_tableList.getModel();
		list_model.remove(_tableList.getSelectedIndex());
		listSelectionChanged();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: removeStateChanged
	* 
	* Description:
	* Cette méthode est appelée lorsque l'état de la case à cocher de 
	* suppression des noeuds intermédiaires a changé. Suivant son état, 
	* la case à cocher de chargement automatique des menus est activée ou 
	* non.
	* ----------------------------------------------------------*/
	private void removeStateChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExploreConfigurationPanel", "removeStateChanged");
			
		trace_methods.beginningOfMethod();
		// Si la case à cocher de suppression des noeuds intermédiaire est
		// cochée, il faut cocher celle des chargements automatiques de
		// menus
		if(_removeUnnecessaryNodes.isSelected() == true) {
			_preloadMenus.setSelected(true);
		}
		// On change l'état de la case à cocher de chargement automatique
		// des menus suivant l'état de la case à cocher de suppression des
		// noeuds intermédiaires
		_preloadMenus.setEnabled(
			_removeUnnecessaryNodes.isSelected() == false);
		trace_methods.endOfMethod();
	}
}
