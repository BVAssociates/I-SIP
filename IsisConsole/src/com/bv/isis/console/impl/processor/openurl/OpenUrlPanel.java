/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/openurl/OpenUrlPanel.java,v $
* $Revision: 1.4 $
*
* ------------------------------------------------------------
* DESCRIPTION: Panneau de configuration du processeur OpenUrl 
* DATE:        18/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.openurl
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: OpenUrlPanel.java,v $
* Revision 1.4  2009/01/15 16:52:48  tz
* Modification de la mise en page du panneau de configuration.
*
* Revision 1.3  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2005/07/01 12:12:40  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/10/22 15:37:58  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.openurl;

//
//Imports système
//
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.prefs.PreferencesAPI;
import com.bv.core.message.MessageManager;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: OpenUrlPanel
* 
* Description:
* Cette classe correspond au panneau de configuration du processeur d'ouverture 
* de page internet. Elle spécialise la classe JPanel et implémente l'interface 
* ConfigurationPanelInterface.
* Elle construit un panneau permettant de saisir le chemin du binaire à exécuter 
* pour ouvrir une page internet, ainsi que ses paramètres d'exécution.
* ----------------------------------------------------------*/
public class OpenUrlPanel 
	extends JPanel 
	implements ConfigurationPanelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: OpenUrlPanel
	* 
	* Description:
	* Cette méthode est le constructeur de la classe. Elle crée le panneau de 
	* configuration en appelant la méthode makePanel().
	* ----------------------------------------------------------*/
	public OpenUrlPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlPanel", "OpenUrlPanel");
		
		trace_methods.beginningOfMethod();
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: validateConfiguration
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appelée pour valider la saisie.
	* La saisie est valide si la commande contient un nom d'exécutable ainsi 
	* que le mot-clé %[url] qui indique la position de l'URL dans la ligne de 
	* commande.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    nécessaire pour l'affichage d'un message à l'utilisateur.
	* 
	* Retourne: true si les données sont valides, false sinon.
	* ----------------------------------------------------------*/
	public boolean validateConfiguration(
		MainWindowInterface windowInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlPanel", "validateConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		String command = _command.getText();
		if(command == null || command.equals("") == true)
		{
			windowInterface.showPopup("Error", "&ERR_EmptyCommand", null);
			trace_methods.endOfMethod();
			return false;
		}
		// Le panneau est valide si la commande contient au moins deux chaînes
		// et que le mot-clé %[url] est contenu dedans
		UtilStringTokenizer tokenizer = new UtilStringTokenizer(command, " ");
		if(tokenizer.getTokensCount() < 2)
		{
			windowInterface.showPopup("Error", "&ERR_CommandShouldContainBinary", null);
			trace_methods.endOfMethod();
			return false;
		}
		if(command.indexOf("%[url]") == -1)
		{		
			windowInterface.showPopup("Error", "&ERR_NoURLInCommand", null);
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
	* Cette méthode redéfinit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appelée pour enregistrer la configuration.
	* La méthode validateConfiguration() est préalablement appelée pour 
	* vérifier que la saisie est valide, puis la commande à exécuter est 
	* enregistrée dans le fichier de préférences de l'utilisateur via le 
	* paramètre "OpenURL.Browser.Command".
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    nécessaire pour l'affichage d'un message à l'utilisateur.
	* 
	* Retourne: true si les données sont valides et enregistrées, false sinon.
	* ----------------------------------------------------------*/
	public boolean storeConfiguration(
		MainWindowInterface windowInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlPanel", "storeConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// Tout d'abord, on va vérifier la configuration
		if(validateConfiguration(windowInterface) == false)
		{
			trace_methods.endOfMethod();
			return false;
		}
		// On va enregistrer la configuration
		String command = _command.getText();
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			// Ajout de la section OpenURL
			preferences.addSection("OpenURL");
			// Positionnement de la valeur du paramètre Browser.Command
			preferences.useSection("OpenURL");
			preferences.set("Browser.Command", command);
			// Ecriture des préférences
			preferences.write();
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Erreur lors de l'enregistrement des " +
				"préférences:" + exception);
			// On va en informer l'utilisateur
			windowInterface.showPopupForException("&ERR_CannotSaveConfiguration",
				exception);
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
			"OpenUrlPanel", "getPanelTitle");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&OpenUrl_CfgTitle");
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _command
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JTextField 
	* correspondant à la commande à exécuter.
	* ----------------------------------------------------------*/
	private JTextField _command;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode construit le panneau de configuration. Elle construit un 
	* panneau contenant une zone de saisie pour la commande à exécuter, et 
	* un bouton "Explorer" permettant d'ouvrir une boîte de sélection de 
	* fichier.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlPanel", "makePanel");
		String command = "";
		
		trace_methods.beginningOfMethod();
		// Lecture des préférences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			preferences.useSection("OpenURL");
			command = preferences.getString("Browser.Command");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		// Création du paneau central
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 
			2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
		setLayout(bag_layout);
		// Création du JLabel d'affichage du prompt
		JLabel prompt_label = new JLabel(
			MessageManager.getMessage("&OpenUrl_DB_Prompt"));
		bag_layout.setConstraints(prompt_label, constraints);
		add(prompt_label);
		prompt_label = new JLabel(
			MessageManager.getMessage("&OpenUrl_DB_Prompt2"));
		constraints.gridwidth = 1;
		constraints.weightx = 0;
		constraints.gridy++;
		bag_layout.setConstraints(prompt_label, constraints);
		add(prompt_label);
		// Création de la zone de saisie de la commande
		_command = new JTextField(20);
		_command.setText(command);
		constraints.gridx++;
		constraints.weightx = 1;
		bag_layout.setConstraints(_command, constraints);
		add(_command);
		// Création du bouton d'exploration
		JButton browse_button = 
			new JButton(MessageManager.getMessage("&OpenUrl_DB_Browse"));
		browse_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				browse();
			}
		});
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.NORTHEAST;
		constraints.fill = GridBagConstraints.NONE;
		bag_layout.setConstraints(browse_button, constraints);
		add(browse_button);
		// On va ajouter une glue verticale pour s'assurer que tout est
		// "collé" en haut
		Component vertical_glue = Box.createVerticalGlue();
		constraints.gridy ++;
		constraints.weighty = 1;
		bag_layout.setConstraints(vertical_glue, constraints);
		add(vertical_glue);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: browse
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le bouton 
	* "Explorer" du panneau de configuration. Elle ouvre une boîte de dialogue 
	* de sélection de fichier permettant de sélectionner le binaire 
	* d'exploration internet.
	* ----------------------------------------------------------*/
	private void browse()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"OpenUrlPanel", "browse");
		
		trace_methods.beginningOfMethod();
		JFileChooser file_chooser = new JFileChooser();
		file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		file_chooser.setFileHidingEnabled(true);
		if(file_chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION)
		{
			// L'utilisateur a annulé, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On va positionner le chemin complet du fichier dans la commande
		_command.setText(file_chooser.getSelectedFile().getAbsolutePath());
		trace_methods.endOfMethod();
	}
}
