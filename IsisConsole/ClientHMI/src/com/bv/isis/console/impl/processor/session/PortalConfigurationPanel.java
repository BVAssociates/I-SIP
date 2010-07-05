/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/session/PortalConfigurationPanel.java,v $
* $Revision: 1.7 $
*
* ------------------------------------------------------------
* DESCRIPTION: Bo�te de configuration du Portail
* DATE:        18/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.session
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: PortalConfigurationPanel.java,v $
* Revision 1.7  2009/01/15 16:53:11  tz
* Modification de la mise en page du panneau de configuration.
*
* Revision 1.6  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.5  2007/04/12 15:15:20  ml
* Nombre de profil en param�tre dans Console_config.ini
*
* Revision 1.4  2006/03/13 10:49:55  tz
* Ajout des boutons "Ajouter", "Modifier" et "Supprimer".
*
* Revision 1.3  2005/10/07 08:23:31  tz
* Gestion de la configuration des profils de Portails.
*
* Revision 1.2  2005/07/01 12:09:56  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/10/22 15:37:31  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.session;

//
//Imports syst�me
//
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.Box;
import javax.swing.border.CompoundBorder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.Properties;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.util.UtilStringTokenizer;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.gui.NonEditableTable;
import com.bv.isis.console.core.common.DialogManager;

/*----------------------------------------------------------
* Nom: PortalConfigurationPanel
* 
* Description:
* Cette classe repr�sente le panneau de configuration du processeur 
* d'ouverture de session Agent. Il permet de d�finir un certain nombre de 
* profils de configuration de plate-forme et de port d'�coute du Portail.
* Elle d�rive de la classe JPanel et red�finit l'interface 
* ConfigurationPanelInterface.
* ----------------------------------------------------------*/
public class PortalConfigurationPanel
	extends JPanel
	implements ConfigurationPanelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: PortalConfigurationPanel
	* 
	* Description:
	* Cette m�thode est le constructeur par d�faut. Elle appelle la m�thode 
	* makePanel() afin que le panneau de configuration soit construit.
	* ----------------------------------------------------------*/
	public PortalConfigurationPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "PortalConfigurationPanel");
		
		trace_methods.beginningOfMethod();
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: validateConfiguration
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appel�e afin que le panneau puisse v�rifier si les donn�es qui 
	* ont �t� saisies sont correctes ou non.
	* Dans le cas pr�sent, les donn�es sont correctes si, pour chaque ligne, 
	* l'intitul�, le nom de la plate-forme Portail et le port d'�coute ont �t� 
	* renseign�s et que le num�ro de port est une valeur num�rique comprise 
	* entre 7600 et 65000.
	* Si une des donn�es n'est pas valide, un message est affich� � 
	* l'utilisateur, et la m�thode retourne false.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface 
	*    n�cessaire � l'affichage d'un message � l'utilisateur.
	* 
	* Retourne: true si les donn�es sont valid�es, false sinon.
	* ----------------------------------------------------------*/
	public boolean validateConfiguration(
		MainWindowInterface windowInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "validateConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int row_index;
		int number_of_profiles = 0;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		
		// On va v�rifier les donn�es ligne par ligne
		for(row_index = 0 ; row_index < _profiles.getRowCount() ; row_index ++)
		{ 
			int portal_port = -1;
			
			trace_debug.writeTrace("row_index=" + row_index);
			String profile_label = 
				_profiles.getValueAt(row_index, 0).toString();
			UtilStringTokenizer tokenizer = new UtilStringTokenizer(
				_profiles.getValueAt(row_index, 1).toString(), ":");
			String portal_host = tokenizer.getToken(0);
			String portal_port_string = "";
			if(tokenizer.getTokensCount() == 2)
			{
				portal_port_string = tokenizer.getToken(1);
			}
			trace_debug.writeTrace("profile_label=" + profile_label);
			trace_debug.writeTrace("portal_host=" + portal_host);
			trace_debug.writeTrace("portal_port_string=" + portal_port_string);
			// Une ligne valide ne contient que des champs renseign�s
			if(profile_label != null && profile_label.equals("") == false &&
				portal_host != null && portal_host.equals("") == false &&
				portal_port_string != null && 
				portal_port_string.equals("") == false)
			{
				trace_debug.writeTrace("Tous les champs sont renseign�s");
				// On v�rifie que le num�ro de port est dans la bonne tranche
				try
				{
					portal_port = Integer.parseInt(portal_port_string);
				}
				catch(Exception e)
				{
					portal_port = -1;
				}
				trace_debug.writeTrace("portal_port=" + portal_port);
				if(portal_port < 7600 || portal_port > 65000)
				{
					// On va s�lectionner la ligne fautive
					_profiles.grabFocus();
					_profiles.clearSelection();
					_profiles.setRowSelectionInterval(row_index, row_index);
					windowInterface.showPopup("Error", 
						"&ERR_PortalPortInvalid", null);
					// On sort
					trace_methods.endOfMethod();
					return false;
				}
				number_of_profiles ++;
			}
			// Une ligne o� aucun champ n'est renseign� est �galement valide
			else if((profile_label == null || 
				profile_label.equals("") == true) &&
				(portal_host == null || portal_host.equals("") == true) &&
				(portal_port_string == null || 
				portal_port_string.equals("") == true))
			{
				trace_debug.writeTrace("Aucun champ n'est renseign�");
				continue;
			}
			else
			{
				String error;
				
				trace_debug.writeTrace("Au moins un champ n'est pas renseign�");
				if(profile_label == null || profile_label.equals("") == true)
				{
					error = "&ERR_ProfileLabelInvalid";
				}
				else if(portal_host == null || portal_host.equals("") == true)
				{
					error = "&ERR_PortalHostInvalid";
				}
				else
				{
					error = "&ERR_PortalPortInvalid";
				}
				// On va s�lectionner la ligne fautive
				_profiles.grabFocus();
				_profiles.clearSelection();
				_profiles.setRowSelectionInterval(row_index, row_index);
				windowInterface.showPopup("Error", error, null);
				// On sort
				trace_methods.endOfMethod();
				return false;
			}
		}
		// On doit avoir au moins un profil renseign�
		trace_debug.writeTrace("number_of_profiles=" + number_of_profiles);
		if(number_of_profiles == 0)
		{
			// On va s�lectionner la ligne fautive
			_profiles.grabFocus();
			_profiles.clearSelection();
			_profiles.setRowSelectionInterval(0, 0);
			windowInterface.showPopup("Error", 
				"&ERR_AtLeastProfileMustBeDefined", null);
			// On sort
			trace_methods.endOfMethod();
			return false;
		}
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: storeConfiguration
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appel�e afin que le panneau puisse valider et enregistrer les 
	* donn�es saisies.
	* La v�rification est effectu�e via un appel � la m�thode 
	* validateConfiguration(). Contrairement aux autres panneaux de 
	* configuration, celui-ci stocke les donn�es dans un fichier de propri�t�s, 
	* et non pas dans le fichier de pr�f�rences de l'utilisateur.
	* Le fichier de propri�t�s dans lequel sont enregistr�es les donn�es est 
	* repr�sent� par la propri�t� "PROFILES".
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface 
	* 	 n�cessaire � l'affichage d'un message � l'utilisateur.
	* 
	* Retourne: true si les donn�es sont valid�es et enregistr�es, false sinon.
	* ----------------------------------------------------------*/
	public boolean storeConfiguration(
		MainWindowInterface windowInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "storeConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_io = TraceAPI.declareTraceIO("Console");
		Vector data = new Vector();
		String properties_file;
		Properties properties;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// On v�rifie la validit� des donn�es
		if(validateConfiguration(windowInterface) == false)
		{
			return false;
		}
		
		// On va construire les propri�t�s relatives aux profils
		properties = new Properties();
		for(int index = 0 ; index < _profiles.getRowCount() ; index ++)
		{
			properties.put("Profile" + index + ".Label", 
				_profiles.getValueAt(index, 0));
			properties.put("Profile" + index + ".Configuration",
				_profiles.getValueAt(index, 1));
		}
		properties_file = System.getProperty("PROFILES");
		// On va tenter de sauvegarder les valeurs de configuration
		try
		{
			FileOutputStream output_stream = 
				new FileOutputStream(properties_file, false);
			properties.store(output_stream, null);
			output_stream.close();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'enregistrement des propri�t�s: " +
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
	* Cette m�thode red�finit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appel�e pour r�cup�rer l'intitul� du panneau de configuration.
	* 
	* Retourne: L'intitul� du panneau de configuration.
	* ----------------------------------------------------------*/
	public String getPanelTitle()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "getPanelTitle");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&CD_PortalCfgTitle");
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _profiles
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet NonEditableTable 
	* correspondant au tableau dans lequel sont affich�es les profils.
	* ----------------------------------------------------------*/
	private NonEditableTable _profiles;

	/*----------------------------------------------------------
	* Nom: _profileLabel
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JTextField charg� de 
	* contenir l'intitul� du profil.
	* ----------------------------------------------------------*/
	private JTextField _profileLabel;

	/*----------------------------------------------------------
	* Nom: _portalHost
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JTextField charg� de 
	* contenir le nom de la plate-forme Portail.
	* ----------------------------------------------------------*/
	private JTextField _portalHost;

	/*----------------------------------------------------------
	* Nom: _portalPort
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JTextField charg� de 
	* contenir le num�ro de port du Portail.
	* ----------------------------------------------------------*/
	private JTextField _portalPort;

	/*----------------------------------------------------------
	* Nom: _addButton
	* 
	* Description:
	* Cet attribut maintient une r�f�rence un objet JButton repr�sentant le 
	* bouton "Ajouter".
	* ----------------------------------------------------------*/
	private JButton _addButton;

	/*----------------------------------------------------------
	* Nom: _modifyButton
	* 
	* Description:
	* Cet attribut maintient une r�f�rence un objet JButton repr�sentant le 
	* bouton "Modifier".
	* ----------------------------------------------------------*/
	private JButton _modifyButton;

	/*----------------------------------------------------------
	* Nom: _deleteButton
	* 
	* Description:
	* Cet attribut maintient une r�f�rence un objet JButton repr�sentant le 
	* bouton "Supprimer".
	* ----------------------------------------------------------*/
	private JButton _deleteButton;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette m�thode est utilis�e pour construire le panneau de configuration 
	* du Portail. Elle construit la zone d'affichage des diff�rents profils 
	* (jusqu'� cinq profils), et les zones de saisie pour l'intitul� du 
	* profile, le nom de la plate-forme Portail et pour le port d'�coute.
	* Les donn�es sont extraites du fichier de propri�t�s d�finit par la 
	* propri�t� "PROFILES".
	* ----------------------------------------------------------*/
	
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "makePanel");
		String[][] data = null;
		
		trace_methods.beginningOfMethod();
		// On va initialiser les valeurs
		String[] columns = {
			MessageManager.getMessage("&Session_LabelColumn"),
			MessageManager.getMessage("&Session_ValueColumn")
		};
		
		// On va tenter de r�cup�rer le nombre de profils maximum dans le fichier Console_config.ini
		int number_of_profiles;
		try
		{
			ConfigurationAPI config_api = new ConfigurationAPI();
			number_of_profiles = config_api.getInt("Console","NumberOfProfiles");
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la r�cup�ration du nombre de profils maximum: " +
				exception);
			number_of_profiles = 5;
		}
		
		data = new String[number_of_profiles][2];
		// On va tenter de r�cup�rer les anciennes valeurs, s'il y en a
		String properties_file = System.getProperty("PROFILES");
		Properties properties = new Properties();
		try
		{
			FileInputStream input_stream = new FileInputStream(properties_file);
			properties.load(input_stream);
			input_stream.close();
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors du chargement du fichier de propri�t�s: " +
				exception);
		}
		
		for(int index = 0 ; index < number_of_profiles ; index ++)
		{
			data[index][0] = properties.getProperty("Profile" + 
				index + ".Label", "");
			data[index][1] = properties.getProperty("Profile" + 
				index + ".Configuration", ""); 
		}
		// Cr�ation du panneau central
		setLayout(new BorderLayout());
		// Cr�ation du tableau des profils
		_profiles = new NonEditableTable(data, columns, ":");
		_profiles.getColumnModel().getColumn(0).setPreferredWidth(100);
		_profiles.getColumnModel().getColumn(1).setPreferredWidth(250);
		_profiles.setAutoResizeMode(NonEditableTable.AUTO_RESIZE_ALL_COLUMNS);
		// On va traiter les s�lections dans le tableau
		_profiles.getSelectionModel().addListSelectionListener(
			new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent event)
			{
				// On appelle la m�thode profileSelectionChanged
				profileSelectionChanged();
			}
		});
		JScrollPane scroll_pane = new JScrollPane(_profiles);
		scroll_pane.setPreferredSize(new Dimension(10, 10));
		scroll_pane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll_pane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll_pane.setBorder(new CompoundBorder(
			BorderFactory.createEmptyBorder(2, 2, 1, 2),
			BorderFactory.createEtchedBorder()));
		// On va placer le scroll pane dans un JPanel pour lui ajouter
		// des bordures
		add(scroll_pane, BorderLayout.CENTER);
		// Cr�ation du panneau de saisie
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 
			1, 1, 0, 0, GridBagConstraints.CENTER, 
			GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
		JPanel input_panel = new JPanel(bag_layout);
		// Cr�ation du JLabel d'affichage de l'intitul�
		JLabel label_label = new JLabel(
			MessageManager.getMessage("&Session_Label"));
		bag_layout.setConstraints(label_label, constraints);
		input_panel.add(label_label);
		// Cr�ation du champ de saisie de l'intitul�
		_profileLabel = new JTextField(10);
		// On va traiter les frappes de touches
		_profileLabel.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				// On va appeler la m�thode fieldChanged
				fieldChanged();
			}
		});
		constraints.weightx = 1;
		constraints.gridx++;
		bag_layout.setConstraints(_profileLabel, constraints);
		input_panel.add(_profileLabel);
		// Cr�ation du JLabel d'affichage de la plate-forme
		JLabel host_label = new JLabel(
			MessageManager.getMessage("&Session_PortalHost"));
		constraints.gridx = 0;
		constraints.gridy++;
		constraints.weightx = 0;
		bag_layout.setConstraints(host_label, constraints);
		input_panel.add(host_label);
		// Cr�ation de la liste des identifiants
		_portalHost = new JTextField(10);
		// On va traiter les frappes de touches
		_portalHost.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				// On va appeler la m�thode fieldChanged
				fieldChanged();
			}
		});
		constraints.weightx = 1;
		constraints.gridx++;
		bag_layout.setConstraints(_portalHost, constraints);
		input_panel.add(_portalHost);
		// Cr�ation du label de num�ro de port
		JLabel port_label = new JLabel(
			MessageManager.getMessage("&Session_PortalPort"));
		constraints.gridx = 0;
		constraints.gridy++;
		constraints.weightx = 0;
		bag_layout.setConstraints(port_label, constraints);
		input_panel.add(port_label);
		// Cr�ation de la zone de saisie du mot de passe
		_portalPort = new JTextField(5);
		// On va traiter les frappes de touches
		_portalPort.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				// On va appeler la m�thode fieldChanged
				fieldChanged();
			}
		});
		constraints.gridx++;
		constraints.weightx = 1;
		bag_layout.setConstraints(_portalPort, constraints);
		input_panel.add(_portalPort);
		// On va cr�er une zone vide
		constraints.gridx++;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.gridheight = 3;
		Component strut = Box.createHorizontalStrut(3);
		bag_layout.setConstraints(strut, constraints);
		input_panel.add(strut);
		// On ajoute les boutons
		constraints.gridx++;
		constraints.gridheight = 1;
		// On commence par le bouton "Ajouter"
		_addButton = new JButton(MessageManager.getMessage("&Session_Add"));
		_addButton.setEnabled(false);
		// On ajoute le callback sur le bouton
		_addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addProfile();
			}
		});
		bag_layout.setConstraints(_addButton, constraints);
		input_panel.add(_addButton);
		// On continue par le bouton "Modifier"
		constraints.gridy++;
		_modifyButton = new JButton(MessageManager.getMessage(
			"&Session_Modify"));
		_modifyButton.setEnabled(false);
		// On ajoute le callback sur le bouton
		_modifyButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				modifyProfile();
			}
		});
		bag_layout.setConstraints(_modifyButton, constraints);
		input_panel.add(_modifyButton);
		// On termine par le bouton "Supprimer"
		constraints.gridy++;
		_deleteButton = new JButton(MessageManager.getMessage(
			"&Session_Delete"));
		_deleteButton.setEnabled(false);
		// On ajoute le callback sur le bouton
		_deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				deleteProfile();
			}
		});
		bag_layout.setConstraints(_deleteButton, constraints);
		input_panel.add(_deleteButton);
		// On ajoute un bord aux zones de saisie
		input_panel.setBorder(new CompoundBorder(
			BorderFactory.createEmptyBorder(1, 2, 2, 2),
			BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			MessageManager.getMessage("&Session_Profile"))));
		add(input_panel, BorderLayout.SOUTH);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: profileSelectionChanged
	* 
	* Description:
	* Cette m�thode est appel�e lorsque une ligne du tableau des profiles a 
	* �t� s�lectionn�e ou d�s�lectionn�e.
	* Lorsqu'une ligne a �t� s�lectionn�e, les champs de saisie sont pr�-
	* remplis avec les donn�es du profil, et les boutons "Ajouter", "Modifier" 
	* et "Supprimer" sont activ�s.
	* Sinon, toutes les zones sont effac�es, et tous les boutons sont 
	* d�sactiv�s.
	* ----------------------------------------------------------*/
	private void profileSelectionChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "profileSelectionChanged");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int selected_row;
		boolean enable = false;
		String profile_label = "";
		String portal_host = "";
		String portal_port = "";
		
		trace_methods.beginningOfMethod();
		selected_row = _profiles.getSelectedRow();
		trace_debug.writeTrace("selected_row=" + selected_row);
		// Si selected_row vaut -1, cela signifie qu'aucune ligne n'a
		// �t� s�lectionn�e
		if(selected_row > -1)
		{
			// On va r�cup�rer les valeurs � la ligne s�lectionn�e
			profile_label = _profiles.getValueAt(selected_row, 0).toString();
			UtilStringTokenizer tokenizer = new UtilStringTokenizer(
				_profiles.getValueAt(selected_row, 1).toString(), ":");
			portal_host = tokenizer.getToken(0);
			if(tokenizer.getTokensCount() == 2)
			{
				portal_port = tokenizer.getToken(1);
			}
			trace_debug.writeTrace("profile_label=" + profile_label);
			trace_debug.writeTrace("portal_host=" + portal_host);
			trace_debug.writeTrace("portal_port=" + portal_port);
			// Si tous les champs sont valoris�s, on peut activer les boutons
			if(profile_label != null && profile_label.equals("") == false &&
				portal_host != null && portal_host.equals("") == false &&
				portal_port != null && portal_port.equals("") == false)
			{
				enable = true;
			}
		}
		trace_debug.writeTrace("enable=" + enable);
		// On va positionner les valeurs des champs
		_profileLabel.setText(profile_label);
		_portalHost.setText(portal_host);
		_portalPort.setText(portal_port);
		// On va activer ou non les boutons
		_addButton.setEnabled(enable);
		_modifyButton.setEnabled(enable);
		_deleteButton.setEnabled(enable);
		// On donne le focus � la zone d'intitul�
		_profileLabel.grabFocus();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: fieldChanged
	* 
	* Description:
	* Cette m�thode est appel�e � chaque fois qu'une touche est frapp�e dans 
	* une des zones de saisie (intitul� du profil, plate-forme ou num�ro de 
	* port).
	* Si les trois zones de saisie ont une valeur, on va activer les boutons 
	* "Ajouter" et "Modifier", sinon, on va les d�sactiver.
  	* ----------------------------------------------------------*/
 	private void fieldChanged()
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "fieldChanged");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String profile = null;
		String host = null;
		String port = null;
		boolean add_enable = false;
		boolean modify_enable = false;
		
		trace_methods.beginningOfMethod();
		profile = _profileLabel.getText();
		host = _portalHost.getText();
		port = _portalPort.getText();
		if(profile != null && profile.equals("") == false &&
			host != null && host.equals("") == false &&
			port != null && port.equals("") == false)
		{
			add_enable = true; 
			if(_profiles.getSelectedRow() > -1)
			{
				modify_enable = true;
			}
		}
		_addButton.setEnabled(add_enable);
		_modifyButton.setEnabled(modify_enable);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: addProfile
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton 
	* "Ajouter".
	* Si cela est possible, le nouveau profil est ajout� dans le tableau, 
	* sinon, un message d'erreur est affich�.
	* ----------------------------------------------------------*/
	private void addProfile()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "addProfile");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String profile = null;
		String host = null;
		String port = null;
		int port_value = 0;
		int insert_index = -1;
		
		trace_methods.beginningOfMethod();
		profile = _profileLabel.getText();
		host = _portalHost.getText();
		port = _portalPort.getText();
		trace_debug.writeTrace("profile=" + profile);
		trace_debug.writeTrace("host=" + host);
		trace_debug.writeTrace("port=" + port);
		// Si au moins un des champs n'est pas valoris�s, on sort 
		if(profile == null || profile.equals("") == true ||
			host == null || host.equals("") == true ||
			port == null || port.equals("") == true)
		{
			// On appelle la m�thode fieldChanged() pour modifier
			// l'�tat des boutons
			fieldChanged();
			trace_methods.endOfMethod();
			return;
		}
		// Il faut v�rifier que le num�ro de port est bien une valeur
		// num�rique comprise entre 7600 et 65000
		try
		{
			port_value = Integer.parseInt(port);
		}
		catch(Exception e)
		{
			// On ne fait rien
		}
		if(port_value < 7600 || port_value > 65000)
		{
			trace_errors.writeTrace("Le num�ro de port n'est pas valide !");
			// On affiche un message � l'utilisateur
			DialogManager.displayDialog("Error", "&ERR_InvalidPortNumber", 
				null, this);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va rechercher la position d'insertion et v�rifier que le nom du
		// profil n'est pas d�j� utilis�
		for(int index = 0 ; index < _profiles.getRowCount() ; index ++)
		{
			String profile_label = 
				_profiles.getValueAt(index, 0).toString();
			if(profile.equals(profile_label) == true)
			{
				trace_errors.writeTrace(
					"L'intitul� du profil est d�j� utilis� !");
				// On va afficher un message � l'utilisateur
				DialogManager.displayDialog("Error", "&ERR_ProfileNameUsed", 
					null, this);
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			if(profile_label == null || profile_label.equals("") == true)
			{
				// On s'arr�te
				insert_index = index;
				break;
			}
		}
		trace_debug.writeTrace("insert_index=" + insert_index);
		// Si insert_index vaut -1, c'est qu'il n'y a plus de place
		if(insert_index == -1)
		{
			trace_errors.writeTrace("Tous les profils sont d�finis !");
			// On va afficher un message � l'utilisateur
			DialogManager.displayDialog("Error", "&ERR_AllProfilesSet", 
				null, this);
			trace_methods.endOfMethod();
			return;
		}
		// On va positionner les valeurs � la ligne d'insertion
		_profiles.setValueAt(profile, insert_index, 0);
		_profiles.setValueAt(host + ":" + port, insert_index, 1);
		// On d�selectionne toute ligne
		_profiles.clearSelection();
		profileSelectionChanged();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: modifyProfile
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton 
	* "Modifier".
	* Les informations relative au profil s�lectionn� dans le tableau sont 
	* mises � jour.
	* ----------------------------------------------------------*/
	private void modifyProfile()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "addProfile");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String profile = null;
		String host = null;
		String port = null;
		int port_value = 0;
		int selected_row = -1;
		
		trace_methods.beginningOfMethod();
		selected_row = _profiles.getSelectedRow();
		trace_debug.writeTrace("selected_row=" + selected_row);
		// Si aucune ligne n'est s�lectionn�e, on sort
		if(selected_row == -1)
		{
			profileSelectionChanged();
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re les valeurs des champs de saisie
		profile = _profileLabel.getText();
		host = _portalHost.getText();
		port = _portalPort.getText();
		trace_debug.writeTrace("profile=" + profile);
		trace_debug.writeTrace("host=" + host);
		trace_debug.writeTrace("port=" + port);
		// Si au moins un des champs n'est pas valoris�s, on sort 
		if(profile == null || profile.equals("") == true ||
			host == null || host.equals("") == true ||
			port == null || port.equals("") == true)
		{
			// On appelle la m�thode fieldChanged() pour modifier
			// l'�tat des boutons
			fieldChanged();
			trace_methods.endOfMethod();
			return;
		}
		// Il faut v�rifier que le num�ro de port est bien une valeur
		// num�rique comprise entre 7600 et 65000
		try
		{
			port_value = Integer.parseInt(port);
		}
		catch(Exception e)
		{
			// On ne fait rien
		}
		if(port_value < 7600 || port_value > 65000)
		{
			trace_errors.writeTrace("Le num�ro de port n'est pas valide !");
			// On affiche un message � l'utilisateur
			DialogManager.displayDialog("Error", "&ERR_InvalidPortNumber", 
				null, this);
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va v�rifier que le nom du profil n'est pas d�j� utilis�
		for(int index = 0 ; index < _profiles.getRowCount() ; index ++)
		{
			if(index == selected_row)
			{
				continue;
			}
			String profile_label = 
				_profiles.getValueAt(index, 0).toString();
			if(profile.equals(profile_label) == true)
			{
				trace_errors.writeTrace(
					"L'intitul� du profil est d�j� utilis� !");
				// On va afficher un message � l'utilisateur
				DialogManager.displayDialog("Error", "&ERR_ProfileNameUsed", 
					null, this);
				// On sort
				trace_methods.endOfMethod();
				return;
			}
		}
		// On va positionner les valeurs � la ligne d'insertion
		_profiles.setValueAt(profile, selected_row, 0);
		_profiles.setValueAt(host + ":" + port, selected_row, 1);
		// On d�selectionne toute ligne
		_profiles.clearSelection();
		profileSelectionChanged();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: deleteProfile
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton 
	* "Supprimer".
	* Le profil s�lectionn� est supprim� du tableau.
	* ----------------------------------------------------------*/
	private void deleteProfile()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalConfigurationPanel", "deleteProfile");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int selected_row = -1;
		
		trace_methods.beginningOfMethod();
		selected_row = _profiles.getSelectedRow();
		trace_debug.writeTrace("selected_row=" + selected_row);
		// Si aucune ligne n'est s�lectionn�e, on appelle la m�thode
		// profileSelectionChanged() de sorte � modifier l'�tat des
		// boutons
		if(selected_row == -1)
		{
			profileSelectionChanged();
			trace_methods.endOfMethod();
			return;
		}
		// On va remonter toutes les lignes en dessous de celle � supprimer
		for(; selected_row < _profiles.getRowCount() - 1 ; selected_row ++)
		{
			_profiles.setValueAt(_profiles.getValueAt(selected_row + 1, 0), 
				selected_row, 0);
			_profiles.setValueAt(_profiles.getValueAt(selected_row + 1, 1), 
				selected_row, 1);
		}
		// On r�initialise les valeurs de la derni�re ligne
		_profiles.setValueAt("", _profiles.getRowCount() - 1, 0);
		_profiles.setValueAt("", _profiles.getRowCount() - 1, 1);
		// On d�selectionne toute ligne
		_profiles.clearSelection();
		profileSelectionChanged();
		trace_methods.endOfMethod();
	}
}
