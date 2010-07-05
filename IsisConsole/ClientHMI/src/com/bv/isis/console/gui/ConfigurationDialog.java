/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/ConfigurationDialog.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Boîte de configuration de la Console
* DATE:        26/07/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ConfigurationDialog.java,v $
* Revision 1.10  2009/01/14 14:22:11  tz
* Prise en compte de la modification des packages.
*
* Revision 1.9  2008/02/21 12:05:31  tz
* Taille forcée pour la fenêtre de configuration.
*
* Revision 1.8  2005/10/07 08:34:11  tz
* Changement mineur.
*
* Revision 1.7  2005/07/01 12:25:27  tz
* Modification du composant pour les traces
*
* Revision 1.6  2004/10/22 15:43:07  tz
* Gestion des panneaux de configuration des processeurs.
*
* Revision 1.5  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.4  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.3  2004/07/29 12:21:32  tz
* Utilisation de Portal* au lieu de Master*
*
* Revision 1.2  2002/09/20 10:40:18  tz
* Utilisation du nom commercial I-SIS
*
* Revision 1.1  2002/08/13 13:14:32  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.gui;

//
// Imports système
//
import java.awt.Component;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import javax.swing.JTabbedPane;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.processor.ProcessorManager;

/*----------------------------------------------------------
* Nom: ConfigurationDialog
* 
* Description:
* Cette classe représente la boîte de dialogue qui est affichée à l'utilisateur 
* afin qu'il puisse configurer la Console I-SIS. Cette boîte de dialogue 
* permet à l'utilisateur de saisir les informations de configuration de tous 
* les processeurs déclarés.
* ----------------------------------------------------------*/
public class ConfigurationDialog
	extends JDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ConfigurationDialog
	* 
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Il est le 
	* seul constructeur qui puisse être utilisé.
	* Elle appelle la méthode makePanel afin de construire la boîte de 
	* dialogue qui sera affichée à l'utilisateur.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    correspondant à la fenêtre principale de l'application.
 	* ----------------------------------------------------------*/
	public ConfigurationDialog(
		MainWindowInterface windowInterface
		)
	{
		super((Frame)windowInterface, MessageManager.getMessage("&CD_Title"), 
			true);
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"ConfigurationDialog", "ConfigurationDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		_validated = false;
		_windowInterface = windowInterface;
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		// Construction de la boîte de configuration
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getConfiguration
	* 
	* Description:
	* Cette méthode est appelée pour déclencher la procédure de configuration 
	* de la Console.
	* Elle affiche la boîte de dialogue et reste bloquée jusqu'à ce que 
	* celle-ci soit cachée (par la méthode validateInput() ou par la 
	* méthode cancelInput()).
	* 
	* Retourne: Si l'utilisateur a validé la configuration, la méthode retourne 
	* true. Elle retourne false dans tous les autres cas.
	* ----------------------------------------------------------*/
	public boolean getConfiguration()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"ConfigurationDialog", "getConfiguration");

		trace_methods.beginningOfMethod();
		setSize(400, 300);
		setResizable(false);
		setLocationRelativeTo((Frame)_windowInterface);
		// Affichage de la fenêtre (bloquant)
		show();
		// Lorsque l'on arrive ici, c'est que l'utilisateur a fait un choix
		// Destruction de la boîte
		dispose();
		return _validated;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _validated
	*
	* Description:
	* Cet attribut est un drapeau permettant d'indiquer si l'utilisateur a
	* validé la configuration. Sa valeur est retournée par la méthode
	* getConfiguration().
	* ----------------------------------------------------------*/
	private boolean _validated;

	/*----------------------------------------------------------
	* Nom: _windowInterface
	* 
	* Description:
	* Cet attribut maintient une référence sur l'interface MainWindowInterface 
	* de la fenêtre principale.
	* ----------------------------------------------------------*/
	private MainWindowInterface _windowInterface;

	/*----------------------------------------------------------
	* Nom: _tabbedPane
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JTabbedPane qui 
	* contiendra les panneaux de commande des différents processeurs.
	* ----------------------------------------------------------*/
	private JTabbedPane _tabbedPane;

	/*----------------------------------------------------------
	* Nom: validateInput
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur clique sur le bouton 
	* "Valider" de la boîte de dialogue.
	* Elle va appeler, pour chaque panneau de la barre à onglet, la méthode 
	* storeConfiguration() qui permet de vérifier les données et de les 
	* enregistrer.
	* Si, pour un panneau, la méthode retourne false, l'onglet correspondant 
	* va être activé, et la fenêtre de configuration va rester active.
	* Si tous les panneaux sont validés et enregistrés, l'attribut _validated 
	* va être positionné à true, et la fenêtre va être cachée, libérant ainsi 
	* la méthode show().
	* ----------------------------------------------------------*/
	private void validateInput()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"ConfigurationDialog", "validateInput");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// On va vérifier les données et les enregistrer
		for(int index = 0 ; index < _tabbedPane.getTabCount() ; index ++)
		{
			Component component = _tabbedPane.getComponentAt(index);
			if(!(component instanceof ConfigurationPanelInterface))
			{
				trace_errors.writeTrace("Le panneau " + 
					_tabbedPane.getTitleAt(index) + " n'est pas du bon type !");
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			ConfigurationPanelInterface panel = (ConfigurationPanelInterface)component; 
			if(panel.storeConfiguration(_windowInterface) == false)
			{
				// On sort
				trace_methods.endOfMethod();
				return;
			}
		}
		// Marquage de la validation de l'identification
		_validated = true;
		// Masquage de la boîte (libération de l'appel de la méthode show())
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: cancelInput
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur clique sur le bouton
	* "Annuler" de la boîte d'identification.
	* Elle positionne le drapeau _validated à false et masque la fenêtre afin
	* de libérer l'exécution de la méthode show().
	* ----------------------------------------------------------*/
	private void cancelInput()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"ConfigurationDialog", "cancelInput");

		trace_methods.beginningOfMethod();
		// Marquage de l'annulation de l'identification
		_validated = false;
		// Masquage de la boîte (libération de l'appel de la méthode show())
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode est utilisée pour instancier et ajouter tous les objets 
	* graphiques qui font partie de la boîte de configuration. Elle est 
	* appelée par le constructeur de la classe.
	* Elle va créer un objet TabbedPane qui sera chargé de contenir tous les 
	* panneaux de configuration des processeurs. La liste des processeurs 
	* va être récupérée via la méthode getAllProcessors() de la classe 
	* ProcessorManager.
	* Pour chaque processeur, le panneau de configuration sera récupéré via 
	* la méthode getConfigurationPanel(), et un nouvel onglet sera créé 
	* pour ce panneau.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"ConfigurationDialog", "makePanel");

		trace_methods.beginningOfMethod();
		getContentPane().setLayout(new BorderLayout());
		// On va créer la barre à onglets
		_tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		// On va récupérer la liste des processeurs
		ProcessorInterface[] processors = ProcessorManager.getAllProcessors();
		for(int index = 0 ; index < processors.length ; index ++)
		{
			// Pour chaque processeur, on va récupérer les panneaux de 
			// configuration
			ConfigurationPanelInterface[] panels = 
				processors[index].getConfigurationPanels();
			// Si le processeur n'a pas de panneau de configuration, on passe
			// au suivant
			if(panels == null || panels.length == 0)
			{
				continue;
			}
			for(int loop = 0 ; loop < panels.length ; loop ++)
			{
				_tabbedPane.addTab(panels[loop].getPanelTitle(),
					(JPanel)panels[loop]);
			}
		}
		// On ajoute le panneau de configuration de l'apparence de la Console
		ConsoleConfigurationPanel laf_panel = new ConsoleConfigurationPanel();
		_tabbedPane.addTab(laf_panel.getPanelTitle(), (JPanel)laf_panel);
		// On présélectionne le premier onglet
		_tabbedPane.setSelectedIndex(0);
		getContentPane().add(_tabbedPane, BorderLayout.CENTER);
		// Création du paneau des boutons
		JPanel buttons_panel = new JPanel();
		new BoxLayout(buttons_panel, BoxLayout.X_AXIS);
		buttons_panel.add(Box.createHorizontalGlue());
		// Création du bouton Valider
		JButton validate_button = new JButton(
			MessageManager.getMessage("&Button_Validate"));
		validate_button.setDefaultCapable(true);
		// Ajout du callback sur click
		validate_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Appel de la méthode idoine
				validateInput();
			}
		});
		buttons_panel.add(validate_button);
		// Création du bouton Annuler
		JButton cancel_button = new JButton(
			MessageManager.getMessage("&Button_Cancel"));
		// Ajout du callback sur click
		cancel_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Appel de la méthode idoine
				cancelInput();
			}
		});
		buttons_panel.add(cancel_button);
		buttons_panel.add(Box.createHorizontalGlue());
		// Redimensionnement des boutons pour qu'ils aient la même taille
		// Récupération de la largeur des boutons
		int validate_button_width = validate_button.getPreferredSize().width;
		int cancel_button_width = cancel_button.getPreferredSize().width;
		// Récupération de la hauteur des boutons
		int button_height = validate_button.getPreferredSize().height;
		// Calcul de la largeur maximale des boutons
		int max_width = Math.max(validate_button_width, cancel_button_width);
		// Positionnement de la taille préférée des boutons en fonction de
		// la valeur calculée
		Dimension buttons_size = new Dimension(max_width, button_height);
		validate_button.setPreferredSize(buttons_size);
		cancel_button.setPreferredSize(buttons_size);
		// Ajout du paneau des boutons au paneau central
		getContentPane().add(buttons_panel, BorderLayout.SOUTH);
		trace_methods.endOfMethod();
	}
}