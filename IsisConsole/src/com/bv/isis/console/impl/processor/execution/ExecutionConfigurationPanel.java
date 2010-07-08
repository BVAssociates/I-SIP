/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/execution/ExecutionConfigurationPanel.java,v $
* $Revision: 1.4 $
*
* ------------------------------------------------------------
* DESCRIPTION: Boîte de configuration du processeur d'exécution
* DATE:        21/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.execution
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ExecutionConfigurationPanel.java,v $
* Revision 1.4  2009/01/15 16:52:36  tz
* Modification de la mise en page du panneau de configuration.
*
* Revision 1.3  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2005/07/01 12:14:38  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/10/22 15:39:19  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.execution;

//
//Imports système
//
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JColorChooser;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.Box;
import java.awt.image.BufferedImage;
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import com.bv.core.prefs.PreferencesAPI;
import com.bv.core.message.MessageManager;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: ExecutionConfigurationPanel
* 
* Description:
* Cette classe est une spécialisation de la classe JPanel afin de permettre 
* la configuration du processeur d'exécution de procédures. Le panneau de 
* configuration permet de sélectionner les couleurs à utiliser dans la zone 
* d'affichage des sorties de la procédure.
* Elle implémente l'interface ConfigurationPanelInterface afin de permettre 
* son intégration dans la fenêtre de configuration de la Console.
* ----------------------------------------------------------*/
public class ExecutionConfigurationPanel
	extends JPanel
	implements ConfigurationPanelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ExecutionConfigurationPanel
	* 
	* Description:
	* Cette méthode est le constructeur par défaut. Elle appelle la méthode 
	* makePanel() afin de construire le panneau.
	* ----------------------------------------------------------*/
	public ExecutionConfigurationPanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionConfigurationPanel", "ExecutionConfigurationPanel");
			
		trace_methods.beginningOfMethod();
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: validateConfiguration
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ConfigurationPanelInterface. 
	* Elle est appelée afin que le panneau contrôle que la configuration est 
	* correcte.
	* La configuration est correcte si la couleur d'affichage des informations 
	* de sortie n'est pas identique à la couleur de fond de la zone d'affichage.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    nécessaire pour afficher un message à l'utilisateur.
	* 
	* Retourne: true si la configuration est valide, false sinon.
	* ----------------------------------------------------------*/
	public boolean validateConfiguration(
		MainWindowInterface windowInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionConfigurationPanel", "validateConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		int background_color;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// On vérifie qu'aucun des bouton n'a la même couleur que le fond
		background_color = getButtonColor(_backgroundButton);
		if(getButtonColor(_outputButton) == background_color)
		{
			trace_errors.writeTrace(
				"La sortie standard a la même couleur que le fond !");
			// On affiche un message à l'utilisateur
			windowInterface.showPopup("Error", "&ERR_OutputSameColor", null);
			trace_methods.endOfMethod();
			return false;
		}
		else if(getButtonColor(_errorButton) == background_color)
		{
			trace_errors.writeTrace(
				"La sortie d'erreur a la même couleur que le fond !");
			// On affiche un message à l'utilisateur
			windowInterface.showPopup("Error", "&ERR_ErrorSameColor", null);
			trace_methods.endOfMethod();
			return false;
		}
		else if(_popupMessage.isSelected() == false && 
			getButtonColor(_terminationButton) == background_color)
		{
			trace_errors.writeTrace(
				"La fin d'exécution a la même couleur que le fond !");
			// On affiche un message à l'utilisateur
			windowInterface.showPopup("Error", "&ERR_TerminationSameColor", 
				null);
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
	* Elle est appelée afin que le panneau effectue un enregistrement de la 
	* configuration.
	* La configuration est tout d'abord contrôlée par un appel à la méthode 
	* validateConfiguration(), puis elle est enregistrée dans le fichier de 
	* préférences, section "Execution".
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
			"ExecutionConfigurationPanel", "storeConfiguration");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		int background;
		int output;
		int error;
		int termination;
		boolean show_popup;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// On commence par valider la configuration
		if(validateConfiguration(windowInterface) == false)
		{
			trace_methods.endOfMethod();
			return false;
		}
		background = getButtonColor(_backgroundButton);
		output = getButtonColor(_outputButton);
		error = getButtonColor(_errorButton);
		termination = getButtonColor(_terminationButton);
		show_popup = _popupMessage.isSelected();
		// On va enregistrer la configuration dans le fichier de préférences
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			// On ajoute la section Execution (au cas où)
			preferences.addSection("Execution");
			preferences.set("Background", background);
			preferences.set("Output", output);
			preferences.set("Error", error);
			preferences.set("Termination", termination);
			preferences.set("ShowPopup", show_popup);
			preferences.write();
		}
		catch(Exception exception)
		{
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
			"ExecutionConfigurationPanel", "getPanelTitle");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&Execution_PanelTitle");
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _backgroundButton
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JButton correspondant 
	* au bouton de sélection de la couleur de fond de la zone d'affichage.
	* ----------------------------------------------------------*/
	private JButton _backgroundButton;

	/*----------------------------------------------------------
	* Nom: _outputButton
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JButton correspondant 
	* au bouton de sélection de la couleur d'affichage des données de la 
	* sortie standard.
	* ----------------------------------------------------------*/
	private JButton _outputButton;

	/*----------------------------------------------------------
	* Nom: _errorButton
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JButton correspondant 
	* au bouton de sélection de la couleur d'affichage des données de la 
	* sortie d'erreur.
	* ----------------------------------------------------------*/
	private JButton _errorButton;

	/*----------------------------------------------------------
	* Nom: _terminationButton
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JButton correspondant 
	* au bouton de sélection de la couleur d'affichage du message de fin 
	* d'exécution.
	* ----------------------------------------------------------*/
	private JButton _terminationButton;

	/*----------------------------------------------------------
	* Nom: _popupMessage
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JRadioButton 
	* correspondant au bouton radio d'affichage d'un popup en fin 
	* d'exécution.
	* ----------------------------------------------------------*/
	private JRadioButton _popupMessage;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode est chargée de la construction du panneau de configuration 
	* du processeur d'exécution de procédure. Elle construit un panneau 
	* permettant de positionner les couleurs de fond, des données de la sortie 
	* standard et des données de la sortie d'erreur.
	* Elle permet également de décider si la fin d'exécution est signalée par 
	* une fenêtre popup, ou par un affichage dans une couleur à définir.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionConfigurationPanel", "makePanel");
		int background = Color.white.getRGB();
		int output = Color.black.getRGB();
		int error = Color.red.getRGB();
		int termination = Color.green.getRGB();
		boolean show_popup = true;
			
		trace_methods.beginningOfMethod();
		// On récupère les données de configuration
		try
		{
			PreferencesAPI preferences = new PreferencesAPI();
			// On ajoute la section Execution (au cas où)
			preferences.useSection("Execution");
			background = preferences.getInt("Background");
			output = preferences.getInt("Output");
			error = preferences.getInt("Error");
			termination = preferences.getInt("Termination");
			show_popup = preferences.getBoolean("ShowPopup");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0,
			1, 1, 1, 0, GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
		setLayout(bag_layout);
		// Création du label de couleur de fond
		JLabel background_label = new JLabel(
			MessageManager.getMessage("&Execution_BackgroundColor"));
		bag_layout.setConstraints(background_label, constraints);
		add(background_label);
		// Création du label de couleur de sortie standard
		JLabel output_label = new JLabel(
			MessageManager.getMessage("&Execution_OutputColor"));
		constraints.gridy ++;
		bag_layout.setConstraints(output_label, constraints);
		add(output_label);
		// Création du label de couleur de sortie d'erreur
		JLabel error_label = new JLabel(
			MessageManager.getMessage("&Execution_ErrorColor"));
		constraints.gridy ++;
		bag_layout.setConstraints(error_label, constraints);
		add(error_label);
		// Création du label de sélection
		JLabel selection_label = new JLabel(
			MessageManager.getMessage("&Execution_SelectionLabel"));
		constraints.gridy ++;
		constraints.gridwidth = 2;
		bag_layout.setConstraints(selection_label, constraints);
		add(selection_label);
		// Création du bouton radio d'affichage de popup
		_popupMessage = new JRadioButton(
			MessageManager.getMessage("&Execution_ShowPopup"));
		_popupMessage.setSelected(show_popup);
		_popupMessage.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				selectionChanged();
			}
		});
		constraints.gridy ++;
		bag_layout.setConstraints(_popupMessage, constraints);
		add(_popupMessage);
		// Création du bouton radio d'affichage dans la zone
		JRadioButton add_zone = new JRadioButton(
			MessageManager.getMessage("&Execution_ShowInZone"));
		add_zone.setSelected(!show_popup);
		add_zone.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				selectionChanged();
			}
		});
		constraints.gridy ++;
		constraints.gridwidth = 1;
		bag_layout.setConstraints(add_zone, constraints);
		add(add_zone);
		// Groupe de boutons
		ButtonGroup group = new ButtonGroup();
		group.add(_popupMessage);
		group.add(add_zone);
		// Création du bouton de couleur de fond
		_backgroundButton = new JButton(buildIcon(background));
		_backgroundButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				changeColor((JButton)e.getSource()); 
			}
		});
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 0;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		bag_layout.setConstraints(_backgroundButton, constraints);
		add(_backgroundButton);
		// Création du bouton de couleur de sortie standard
		_outputButton = new JButton(buildIcon(output));
		_outputButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				changeColor((JButton)e.getSource()); 
			}
		});
		constraints.gridy ++;
		bag_layout.setConstraints(_outputButton, constraints);
		add(_outputButton);
		// Création du bouton de couleur de sortie d'erreur
		_errorButton = new JButton(buildIcon(error));
		_errorButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				changeColor((JButton)e.getSource()); 
			}
		});
		constraints.gridy ++;
		bag_layout.setConstraints(_errorButton, constraints);
		add(_errorButton);
		// Création du bouton de couleur de fin d'exécution
		_terminationButton = new JButton(buildIcon(termination));
		_terminationButton.setEnabled(!show_popup);
		_terminationButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				changeColor((JButton)e.getSource()); 
			}
		});
		constraints.gridy = 5;
		bag_layout.setConstraints(_terminationButton, constraints);
		add(_terminationButton);
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
	* Nom: changeColor
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur un des 
	* boutons de sélection de couleur.
	* Une boîte de sélection de couleur est affichée, et le résultat de la 
	* sélection est positionné comme couleur de fond du bouton.
	* 
	* Arguments:
	*  - button: Le bouton sur lequel l'utilisateur a cliqué.
 	* ----------------------------------------------------------*/
 	private void changeColor(
 		JButton button
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionConfigurationPanel", "changeColor");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("button=" + button);
		// On va ouvrir une boîte de sélection des couleurs avec la couleur
		// du bouton comme couleur présélectionnée
		Color new_color = JColorChooser.showDialog(this, 
			MessageManager.getMessage("&Execution_ChooseColor"), 
			new Color(getButtonColor(button)));
		if(new_color != null)
		{
			button.setIcon(buildIcon(new_color.getRGB()));
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: selectionChanged
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'un des 
	* boutons radio du panneau. Elle positionne l'état du bouton de sélection 
	* de la couleur d'affichage du message de fin d'exécution en fonction de 
	* l'état du bouton radio _popupMessage.
	* ----------------------------------------------------------*/
	private void selectionChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionConfigurationPanel", "selectionChanged");
			
		trace_methods.beginningOfMethod();
		_terminationButton.setEnabled(!_popupMessage.isSelected());
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: buildIcon
	* 
	* Description:
	* Cette méthode permet de construire dynamiquement une icône correspondant 
	* à un rectangle de la couleur passée en argument. Le rectangle est 
	* encadré par une bordure noire de sorte à ce que la couleur puisse 
	* ressortir correctement.
	* 
	* Arguments:
	*  - rgbColor: La couleur à utiliser pour construire l'icône.
	* 
	* Retourne: L'icône construite.
	* ----------------------------------------------------------*/
	private Icon buildIcon(
		int rgbColor
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionConfigurationPanel", "buildIcon");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		int width = 30;
		int height = 20;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("rgbColor=" + rgbColor);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int i = 0 ; i < width ; i++)
		{
			for(int j = 0 ; j < height ; j++)
			{
				if(i == 0 || i == (width - 1) || j == 0 || j == (height - 1))
				{
					// La bordure est noire
					image.setRGB(i, j, 0);
				}
				else
				{
					// Le reste est de la couleur spécifiée
					image.setRGB(i, j, rgbColor);
				} 
			}
		}
		trace_methods.endOfMethod();
		return new ImageIcon(image);
	}
	
	/*----------------------------------------------------------
	* Nom: getButtonColor
	* 
	* Description:
	* Cette méthode permet de récupérer la valeur RGB de la couleur de l'icône 
	* du bouton passé en argument. Cette couleur est extraite directement de 
	* l'image de l'icône.
	* 
	* Arguments:
	*  - button: Le bouton dont on souhaite connaître la couleur représentée.
	* 
	* Retourne: La valeur RGB de la couleur du bouton.
	* ----------------------------------------------------------*/
	private int getButtonColor(
		JButton button
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionConfigurationPanel", "getButtonColor");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("button=" + button);
		if(button == null)
		{
			return 0;
		}
		ImageIcon icon = (ImageIcon)button.getIcon();
		BufferedImage image = (BufferedImage)icon.getImage();
		trace_methods.endOfMethod();
		return image.getRGB(10, 10);
	}
}
