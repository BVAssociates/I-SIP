/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/ConfigurationDialog.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Bo�te de configuration de la Console
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
* Taille forc�e pour la fen�tre de configuration.
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
* Mise au propre des traces de m�thodes.
*
* Revision 1.4  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
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
// D�claration du package
package com.bv.isis.console.gui;

//
// Imports syst�me
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
* Cette classe repr�sente la bo�te de dialogue qui est affich�e � l'utilisateur 
* afin qu'il puisse configurer la Console I-SIS. Cette bo�te de dialogue 
* permet � l'utilisateur de saisir les informations de configuration de tous 
* les processeurs d�clar�s.
* ----------------------------------------------------------*/
public class ConfigurationDialog
	extends JDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ConfigurationDialog
	* 
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. Il est le 
	* seul constructeur qui puisse �tre utilis�.
	* Elle appelle la m�thode makePanel afin de construire la bo�te de 
	* dialogue qui sera affich�e � l'utilisateur.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface 
	*    correspondant � la fen�tre principale de l'application.
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
		// Construction de la bo�te de configuration
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getConfiguration
	* 
	* Description:
	* Cette m�thode est appel�e pour d�clencher la proc�dure de configuration 
	* de la Console.
	* Elle affiche la bo�te de dialogue et reste bloqu�e jusqu'� ce que 
	* celle-ci soit cach�e (par la m�thode validateInput() ou par la 
	* m�thode cancelInput()).
	* 
	* Retourne: Si l'utilisateur a valid� la configuration, la m�thode retourne 
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
		// Affichage de la fen�tre (bloquant)
		show();
		// Lorsque l'on arrive ici, c'est que l'utilisateur a fait un choix
		// Destruction de la bo�te
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
	* valid� la configuration. Sa valeur est retourn�e par la m�thode
	* getConfiguration().
	* ----------------------------------------------------------*/
	private boolean _validated;

	/*----------------------------------------------------------
	* Nom: _windowInterface
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur l'interface MainWindowInterface 
	* de la fen�tre principale.
	* ----------------------------------------------------------*/
	private MainWindowInterface _windowInterface;

	/*----------------------------------------------------------
	* Nom: _tabbedPane
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JTabbedPane qui 
	* contiendra les panneaux de commande des diff�rents processeurs.
	* ----------------------------------------------------------*/
	private JTabbedPane _tabbedPane;

	/*----------------------------------------------------------
	* Nom: validateInput
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur clique sur le bouton 
	* "Valider" de la bo�te de dialogue.
	* Elle va appeler, pour chaque panneau de la barre � onglet, la m�thode 
	* storeConfiguration() qui permet de v�rifier les donn�es et de les 
	* enregistrer.
	* Si, pour un panneau, la m�thode retourne false, l'onglet correspondant 
	* va �tre activ�, et la fen�tre de configuration va rester active.
	* Si tous les panneaux sont valid�s et enregistr�s, l'attribut _validated 
	* va �tre positionn� � true, et la fen�tre va �tre cach�e, lib�rant ainsi 
	* la m�thode show().
	* ----------------------------------------------------------*/
	private void validateInput()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"ConfigurationDialog", "validateInput");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// On va v�rifier les donn�es et les enregistrer
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
		// Masquage de la bo�te (lib�ration de l'appel de la m�thode show())
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: cancelInput
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur clique sur le bouton
	* "Annuler" de la bo�te d'identification.
	* Elle positionne le drapeau _validated � false et masque la fen�tre afin
	* de lib�rer l'ex�cution de la m�thode show().
	* ----------------------------------------------------------*/
	private void cancelInput()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"ConfigurationDialog", "cancelInput");

		trace_methods.beginningOfMethod();
		// Marquage de l'annulation de l'identification
		_validated = false;
		// Masquage de la bo�te (lib�ration de l'appel de la m�thode show())
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette m�thode est utilis�e pour instancier et ajouter tous les objets 
	* graphiques qui font partie de la bo�te de configuration. Elle est 
	* appel�e par le constructeur de la classe.
	* Elle va cr�er un objet TabbedPane qui sera charg� de contenir tous les 
	* panneaux de configuration des processeurs. La liste des processeurs 
	* va �tre r�cup�r�e via la m�thode getAllProcessors() de la classe 
	* ProcessorManager.
	* Pour chaque processeur, le panneau de configuration sera r�cup�r� via 
	* la m�thode getConfigurationPanel(), et un nouvel onglet sera cr�� 
	* pour ce panneau.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"ConfigurationDialog", "makePanel");

		trace_methods.beginningOfMethod();
		getContentPane().setLayout(new BorderLayout());
		// On va cr�er la barre � onglets
		_tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		// On va r�cup�rer la liste des processeurs
		ProcessorInterface[] processors = ProcessorManager.getAllProcessors();
		for(int index = 0 ; index < processors.length ; index ++)
		{
			// Pour chaque processeur, on va r�cup�rer les panneaux de 
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
		// On pr�s�lectionne le premier onglet
		_tabbedPane.setSelectedIndex(0);
		getContentPane().add(_tabbedPane, BorderLayout.CENTER);
		// Cr�ation du paneau des boutons
		JPanel buttons_panel = new JPanel();
		new BoxLayout(buttons_panel, BoxLayout.X_AXIS);
		buttons_panel.add(Box.createHorizontalGlue());
		// Cr�ation du bouton Valider
		JButton validate_button = new JButton(
			MessageManager.getMessage("&Button_Validate"));
		validate_button.setDefaultCapable(true);
		// Ajout du callback sur click
		validate_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Appel de la m�thode idoine
				validateInput();
			}
		});
		buttons_panel.add(validate_button);
		// Cr�ation du bouton Annuler
		JButton cancel_button = new JButton(
			MessageManager.getMessage("&Button_Cancel"));
		// Ajout du callback sur click
		cancel_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Appel de la m�thode idoine
				cancelInput();
			}
		});
		buttons_panel.add(cancel_button);
		buttons_panel.add(Box.createHorizontalGlue());
		// Redimensionnement des boutons pour qu'ils aient la m�me taille
		// R�cup�ration de la largeur des boutons
		int validate_button_width = validate_button.getPreferredSize().width;
		int cancel_button_width = cancel_button.getPreferredSize().width;
		// R�cup�ration de la hauteur des boutons
		int button_height = validate_button.getPreferredSize().height;
		// Calcul de la largeur maximale des boutons
		int max_width = Math.max(validate_button_width, cancel_button_width);
		// Positionnement de la taille pr�f�r�e des boutons en fonction de
		// la valeur calcul�e
		Dimension buttons_size = new Dimension(max_width, button_height);
		validate_button.setPreferredSize(buttons_size);
		cancel_button.setPreferredSize(buttons_size);
		// Ajout du paneau des boutons au paneau central
		getContentPane().add(buttons_panel, BorderLayout.SOUTH);
		trace_methods.endOfMethod();
	}
}