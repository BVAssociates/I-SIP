/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/ConsoleConfigurationPanel.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Panneau de configuration du look-and-feel Console 
* DATE:        22/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ConsoleConfigurationPanel.java,v $
* Revision 1.5  2009/01/15 16:52:22  tz
* Correction de la fiche FS#460.
* Modification de la mise en page du panneau de configuration.
*
* Revision 1.4  2009/01/14 14:22:11  tz
* Prise en compte de la modification des packages.
*
* Revision 1.3  2007/09/24 10:39:27  tz
* Ajout de la configuration de l'orientation des fenêtres.
*
* Revision 1.2  2005/07/01 12:25:19  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/10/22 15:42:53  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.gui;

//
//Imports système
//
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.BorderFactory;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Frame;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.prefs.PreferencesAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.gui.IconLoader;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: ConsoleConfigurationPanel
* 
* Description:
* Cette classe est une spécialisation de la classe JPanel correspondant au 
* panneau de configuration de la Console. Elle permet de construire un 
* panneau de configuration pour définir l'apparence (look and feel) de la 
* Console, l'orientation des zones d'affichages, ainsi que le mode 
* d'affichage des entrées de systèmes de fichiers distants.
* Elle implémente l'interface ConfigurationPanelInterface afin de permettre 
* son intégration dans la boîte de configuration de la Console.
* ----------------------------------------------------------*/
public class ConsoleConfigurationPanel
	extends JPanel
	implements ConfigurationPanelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ConsoleConfigurationPanel
	* 
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Elle 
	* construit le panneau de configuration de l'apparence de la Console 
	* en appelant la méthode makePanel().
	* ----------------------------------------------------------*/
	public ConsoleConfigurationPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConsoleConfigurationPanel", "ConsoleConfigurationPanel");
		
		trace_methods.beginningOfMethod();
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: validateConfiguration
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appelée pour valider la configuration.
	* Dans le cas présent, la configuration est toujours valide.
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
			"ConsoleConfigurationPanel", "validateConfiguration");
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
	* Elle est appelée pour enregistrer la configuration.
	* Dans un premier temps, la configuration est contrôlée par un appel à la 
	* méthode validateConfiguration(), puis elle est enregistrée dans le 
	* fichier de préférences dans la section "GUI".
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
			"ConsoleConfigurationPanel", "storeConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String laf_name;
		String previous_laf_name = null;
		String laf_impl;
		int previous_orientation = 0;
		int orientation = 0;
		boolean display_as_list = true;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// Tout d'abord, on va vérifier la configuration
		if(validateConfiguration(windowInterface) == false)
		{
			trace_methods.endOfMethod();
			return false;
		}
		// Lecture des préférences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("GUI");
			previous_laf_name = preferences.getString("LookAndFeel");
			previous_orientation = preferences.getInt("Orientation");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		laf_name = (String)_lookAndFeels.getSelectedItem();
		if(laf_name.equals(previous_laf_name) == false)
		{
			// On va essayer de récupérer le nom de la classe d'implémentation
			if(laf_name.equals("System") == true)
			{
				laf_impl = UIManager.getSystemLookAndFeelClassName();
			}
			else if(laf_name.equals("Metal") == true)
			{
				laf_impl = UIManager.getCrossPlatformLookAndFeelClassName();
			}
			else
			{
				try
				{
					ConfigurationAPI configuration = new ConfigurationAPI();
					laf_impl = configuration.getString("LAF", laf_name + 
						".Impl");
				}
				catch(Exception exception)
				{
					trace_errors.writeTrace("Impossible de trouver " + 
						"l'implémentation du look-and-feel: " + exception);
					windowInterface.showPopupForException(
						"&ERR_LAFHasNoImplementation", exception);
					trace_methods.endOfMethod();
					return false;
				}
			}
			// On va positionner le nouveau look-and-feel
			try
			{
				UIManager.setLookAndFeel(laf_impl);
				SwingUtilities.updateComponentTreeUI((Frame)windowInterface);
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace(
					"Erreur lors du positionnement du look and feel: " +
					exception.getMessage());
				// On va en informer l'utilisateur
				windowInterface.showPopupForException("&ERR_CannotSetLAF",
					exception);
			}
		}
		// On récupère l'orientation
		if(_horizontalView.isSelected() == false)
		{
			orientation = 1;
		}
		// Récupération du mode d'affichage de l'explorateur de systèmes de
		// fichiers distants
		display_as_list = _displayAsList.isSelected();
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			// Ajout de la section GUI
			preferences.addSection("GUI");
			preferences.set("LookAndFeel", laf_name);
			preferences.set("Orientation", orientation);
			preferences.set("FS.DisplayAsList", display_as_list);
			// Ecriture des préférences
			preferences.write();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de l'enregistrement des " +
				"préférences:" + exception);
			// On va en informer l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotSaveConfiguration",
				exception);
			trace_methods.endOfMethod();
			return false;
		}
		if(orientation != previous_orientation)
		{
			// On va informer l'utilisateur qu'il faudra redémarrer
			// la Console pour prendre en compte les modifications
			windowInterface.showPopup("Information", "&ShouldReboot", null);
		}
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getPanelTitle
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface MainWindowInterface. 
	* Elle est appelée pour récupérer l'intitulé du panneau de configuration.
	* 
	* Retourne: L'intitulé du panneau de configuration.
	* ----------------------------------------------------------*/
	public String getPanelTitle()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConsoleConfigurationPanel", "getPanelTitle");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&MainWindow_PanelTitle");
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _lookAndFeels
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JComboBox contenant la 
	* liste des look-and-feel pouvant être appliqués à la Console.
	* ----------------------------------------------------------*/
	private JComboBox _lookAndFeels;

	/*----------------------------------------------------------
	* Nom: _horizontalView
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JRadioButton 
	* représentant l'orientation horizontale des différentes parties de la 
	* Console (arbre d'exploration et zone d'affichage des sous-fenêtres).
	* ----------------------------------------------------------*/
	private JRadioButton _horizontalView;

	/*----------------------------------------------------------
	* Nom: _displayAsList
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JRadioButton 
	* représentant l'affichage des entrées de système de fichiers distant 
	* sous forme de liste.
	* ----------------------------------------------------------*/
	private JRadioButton _displayAsList;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode construit le panneau de configuration de l'apparence de 
	* la Console. Elle construit une zone de sélection du look-and-feel de la 
	* Console, ainsi que les boutons radio de sélection de l'orientation des 
	* affichages.
	* La liste des look-and-feel applicables est extraite de la configuration.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ConsoleConfigurationPanel", "makePanel");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String look_and_feel_name = null;
		int orientation = 0;
		String available_look_and_feels = "System,Metal";
		String laf_list_name = null;
		boolean display_as_list = true;
		
		trace_methods.beginningOfMethod();
		// Lecture des préférences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("GUI");
			look_and_feel_name = preferences.getString("LookAndFeel");
			orientation = preferences.getInt("Orientation");
			display_as_list = preferences.getBoolean("FS.DisplayAsList");
		}
		catch(Exception exception)
		{
			// On ne fait rien
			if(look_and_feel_name == null)
			{
				look_and_feel_name = "Metal";
			}
		}
		// On récupère le type de plate-forme
		if(System.getProperty("os.name").indexOf("indows") != -1)
		{
			// On est sur Windows
			laf_list_name="WindowsList";
		}
		else
		{
			laf_list_name="LinuxList";
		}
		trace_debug.writeTrace("laf_list_name=" + laf_list_name);
		// On récupère la liste des look-and-feel depuis la configuration
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			available_look_and_feels = 
				configuration.getString("LAF", laf_list_name);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la récupération de la " +
				"liste des look-and-feel: " + exception);
		}

		// Création du Layout Manager
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0,
			1, 1, 0, 0, GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
		setLayout(bag_layout);
		// Création du label pour le Look and Feel
		JLabel laf_label = new JLabel(
			MessageManager.getMessage("&MainWindow_SelectLAF"));
		bag_layout.setConstraints(laf_label, constraints);
		add(laf_label);
		// Création de la liste des look-and-feel
		_lookAndFeels = new JComboBox();
		_lookAndFeels.setEditable(false);
		UtilStringTokenizer tokenizer = 
			new UtilStringTokenizer(available_look_and_feels, ",");
		while(tokenizer.hasMoreTokens() == true)
		{
			String laf_name = tokenizer.nextToken();
			_lookAndFeels.addItem(laf_name);
			if(laf_name.equals(look_and_feel_name) == true)
			{
				_lookAndFeels.setSelectedIndex(
					_lookAndFeels.getItemCount() - 1);
			}
		}
		constraints.gridx ++;
		constraints.weightx = 1;
		constraints.gridwidth = 2;
		bag_layout.setConstraints(_lookAndFeels, constraints);
		add(_lookAndFeels);
		if(_lookAndFeels.getSelectedIndex() == -1)
		{
			_lookAndFeels.setSelectedIndex(0);
		}
		// On va ajouter les éléments pour la disposition, en commençant
		// par le label
		JLabel orientation_label = new JLabel(
				MessageManager.getMessage("&MainWindow_SelectOrientation"));
		constraints.gridx = 0;
		constraints.gridy ++;
		constraints.gridwidth = 1;
		//constraints.gridheight = 2;
		constraints.weightx = 0;
		bag_layout.setConstraints(orientation_label, constraints);
		add(orientation_label);
		// On crée les boutons radio pour l'orientation
		_horizontalView = new JRadioButton(MessageManager.getMessage(
				"&MainWindow_SelectHorizontalPosition"));
		constraints.gridx ++;
		constraints.gridheight = 1;
		bag_layout.setConstraints(_horizontalView, constraints);
		add(_horizontalView);
		JRadioButton vertical_view = new JRadioButton(
				MessageManager.getMessage("&MainWindow_SelectVerticalPosition"));
		constraints.gridy ++;
		bag_layout.setConstraints(vertical_view, constraints);
		add(vertical_view);
		// On va créer un groupe de boutons
		ButtonGroup group = new ButtonGroup();
		group.add(_horizontalView);
		group.add(vertical_view);
		// On crée un JLabel pour afficher l'icône associée au choix
		// de l'orientation
		final JLabel icon_label = new JLabel(IconLoader.getIcon("HorizontalView"));
		// On ajoute les ActionListener sur les boutons radio
		_horizontalView.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(((JRadioButton)e.getSource()).isSelected() == true)
				{
					icon_label.setIcon(IconLoader.getIcon("HorizontalView"));
				}
			}
		});
		vertical_view.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(((JRadioButton)e.getSource()).isSelected() == true)
				{
					icon_label.setIcon(IconLoader.getIcon("VerticalView"));
				}
			}
		});
		if(orientation == 0)
		{
			_horizontalView.setSelected(true);
		}
		else
		{
			vertical_view.setSelected(true);
			icon_label.setIcon(IconLoader.getIcon("VerticalView"));
		}
		constraints.gridy --;
		constraints.gridx ++;
		constraints.gridheight = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 1;
		bag_layout.setConstraints(icon_label, constraints);
		add(icon_label);
		// On ajoute les boutons radio pour le mode d'affichage des
		// systèmes de fichiers distants
		JLabel display_mode_label = new JLabel(
			MessageManager.getMessage("&MainWindow_SelectDisplayMode"));
		constraints.gridx = 0;
		constraints.gridy += 2;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		bag_layout.setConstraints(display_mode_label, constraints);
		add(display_mode_label);
		// Création des boutons
		_displayAsList = new JRadioButton(MessageManager.getMessage(
				"&MainWindow_DisplayAsList"));
		constraints.gridx ++;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		bag_layout.setConstraints(_displayAsList, constraints);
		add(_displayAsList);
		JRadioButton display_as_table = new JRadioButton(
				MessageManager.getMessage("&MainWindow_DisplayAsTable"));
		constraints.gridy ++;
		bag_layout.setConstraints(display_as_table, constraints);
		add(display_as_table);
		// On va créer un groupe de boutons
		ButtonGroup display_group = new ButtonGroup();
		display_group.add(_displayAsList);
		display_group.add(display_as_table);
		if(display_as_list == true)
		{
			_displayAsList.setSelected(true);
		}
		else
		{
			display_as_table.setSelected(true);
		}
		// On va ajouter une glue verticale pour s'assurer que tout est
		// "collé" en haut
		Component vertical_glue = Box.createVerticalGlue();
		constraints.gridx = 0;
		constraints.gridy ++;
		constraints.weighty = 1;
		bag_layout.setConstraints(vertical_glue, constraints);
		add(vertical_glue);
		trace_methods.endOfMethod();
	}
}
