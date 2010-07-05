/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/RefreshDialog.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Boîte de dialogue du rechargement automatique
* DATE:        09/03/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: RefreshDialog.java,v $
* Revision 1.2  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.1  2006/03/09 13:38:09  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.display;

//
//Imports système
//
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import com.bv.core.message.MessageManager;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: RefreshDialog
* 
* Description:
* Cette classe est une spécialisation de la classe JDialog implémentant une 
* boîte de dialogue permet d'activer ou de désactiver le rechargement 
* automatique du contenu du tableau, et la période de rechargement.
* L'affichage de la boîte de dialogue et la récupération de la saisie sont 
* effectués par le biais de la méthode getPeriod().
* ----------------------------------------------------------*/
public class RefreshDialog 
	extends JDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: RefreshDialog
	* 
	* Description:
	* Cette méthode est le constructeur de la classe. Elle appelle la méthode 
	* makePanel() afin de construire la boîte de dialogue à afficher.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface.
 	* ----------------------------------------------------------*/
 	public RefreshDialog(
 		MainWindowInterface windowInterface
 		)
 	{
		super((Frame)windowInterface, 
			MessageManager.getMessage("&DisplayReload_Title"), true);
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"RefreshDialog", "RefreshDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		// Construction de la boîte d'identification
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getPeriod
	* 
	* Description:
	* Cette méthode permet d'obtenir l'affichage de la boîte de dialogue, et 
	* ensuite le résultat de la saisie.
	* Le champ et la case à cocher sont pré-remplis à partir de la valeur 
	* originale de la période passée en argument.
	* La valeur retournée est définie comme suit:
	*  - Valeur négative ou nulle: Le rechargement automatique n'est pas 
	*    activé,
	*  - Valeur positive: Le rechargement automatique est activé avec une 
	*    période correspondant à la valeur en secondes.
	* 
	* L'exécution de cette méthode est bloquée jusqu'à ce que la boîte de 
	* dialogue soit masquée, c'est-à-dire que l'utilisateur ait validé ou 
	* annulé.
	* 
	* Arguments:
	*  - originalPeriod: La période originale,
	*  - component: Une référence sur un object Component afin de positionner 
	*    la boîte de dialogue par rapport à celui-ci.
	* 
	* Retourne: La valeur de la nouvelle période.
	* ----------------------------------------------------------*/
	public int getPeriod(
		int originalPeriod,
		Component component
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"RefreshDialog", "getPeriod");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("originalPeriod=" + originalPeriod);
		trace_arguments.writeTrace("component=" + component);
		_period = originalPeriod;
		// On va pré-remplir les champs à partir de la valeur passée en
		// argument
		if(originalPeriod > 0)
		{
			_activate.setSelected(true);
			_periodField.setText("" + originalPeriod);
		}
		else
		{
			_activate.setSelected(false);
			_periodField.setText("" + -1 * originalPeriod);
		}
		// Redimmensionnement de la boîte
		pack();
		setResizable(false);
		setLocationRelativeTo(component);
		// Affichage de la fenêtre (bloquant)
		show();
		// Lorsque l'on arrive ici, c'est que l'utilisateur a fait un choix
		// Destruction de la boîte
		dispose();
		trace_methods.endOfMethod();
		return _period;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _period
	* 
	* Description:
	* Cet attribut maintient une référence sur un entier correspondant à la 
	* période définie par l'utilisateur.
	* ----------------------------------------------------------*/
	private int _period;

	/*----------------------------------------------------------
	* Nom: _activate
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JCheckBox 
	* correspondant à la case d'activation/désactivation du rechargement 
	* automatique.
	* ----------------------------------------------------------*/
	private JCheckBox _activate;

	/*----------------------------------------------------------
	* Nom: _periodField
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JTextField 
	* correspondant à la zone de saisie de la période de rechargement.
	* ----------------------------------------------------------*/
	private JTextField _periodField;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette méthode est chargée de la construction de la boîte de dialogue.
	* Elle crée une case à cocher permettant d'activer ou de désactiver le 
	* rechargement automatique, et une zone de saisie afin de définir la 
	* période de rechargement.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"RefreshDialog", "makePanel");

		trace_methods.beginningOfMethod();
		// Création du paneau central
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel central_panel = new JPanel(bag_layout);
		// Préparation des contraintes
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		// Création du JLabel d'activation
		JLabel activate_label = new JLabel(MessageManager.getMessage(
			"&DisplayReload_Activate"));
		bag_layout.setConstraints(activate_label, constraints);
		central_panel.add(activate_label);
		// Création du JLabel de la periode
		JLabel period_label = new JLabel(MessageManager.getMessage(
			"&DisplayReload_Period"));
		constraints.gridy ++;
		bag_layout.setConstraints(period_label, constraints);
		central_panel.add(period_label);
		// Création de la case à cocher d'activation
		_activate = new JCheckBox();
		constraints.gridy = 0;
		constraints.gridx ++;
		bag_layout.setConstraints(_activate, constraints);
		central_panel.add(_activate);
		// Ajout du callback sur Entrée
		_activate.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					validatePeriod();
				}
			}
		});
		// Création de la zone de saisie de la période
		_periodField = new JTextField(5);
		_periodField.setEnabled(false);
		constraints.gridy ++;
		bag_layout.setConstraints(_periodField, constraints);
		central_panel.add(_periodField);
		// Ajout du callback sur Entrée
		_periodField.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					validatePeriod();
				}
			}
		});
		// On ajoute un callback sur le changement d'état de la case à cocher
		_activate.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				_periodField.setEnabled(_activate.isSelected());
			}
		});
		// Création du paneau des boutons
		JPanel buttons_panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
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
				validatePeriod();
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
				cancelPeriod();
			}
		});
		buttons_panel.add(cancel_button);
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
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weighty = 0;
		bag_layout.setConstraints(buttons_panel, constraints);
		central_panel.add(buttons_panel);
		// Ajout du paneau central au content pane
		getContentPane().add(central_panel, BorderLayout.CENTER);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: validatePeriod
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le bouton 
	* "Valider".
	* Elle redéfinie la valeur de l'attribut _period en fonction de la valeur 
	* de la période définie dans la zone _period et de la valeur de la case à 
	* cocher _activate.
	* Ensuite, la fenêtre est cachée afin de libérer l'exécution de la méthode 
	* getPeriod().
	* ----------------------------------------------------------*/
	private void validatePeriod()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"RefreshDialog", "validatePeriod");
		int period_value = 0;

		trace_methods.beginningOfMethod();
		// On va définir la nouvelle période en fonction de ce qui a été
		// saisi
		try
		{
			period_value = Integer.parseInt(_periodField.getText());
		}
		catch(Exception exception)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Erreur sur la valeur saisie (" +
				_periodField.getText() + "): " + exception.getMessage());
			// On continue quand même
		}
		if(_activate.isSelected() == false)
		{
			period_value *= -1;
		}
		_period = period_value;
		// On masque la boîte de dialogue de sorte à libérer la
		// méthode getPeriod().
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: cancelPeriod
	* 
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur le bouton 
	* "Annuler".
	* Elle masque la boîte de dialogue afin de libérer l'exécution de la 
	* méthode getPeriod().
	* ----------------------------------------------------------*/
	private void cancelPeriod()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"RefreshDialog", "cancelPeriod");

		trace_methods.beginningOfMethod();
		// On masque la boîte de dialogue de sorte à libérer la
		// méthode getPeriod().
		hide();
		trace_methods.endOfMethod();
	}
}
