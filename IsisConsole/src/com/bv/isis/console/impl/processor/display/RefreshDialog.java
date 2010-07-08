/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/RefreshDialog.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Bo�te de dialogue du rechargement automatique
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
// D�claration du package
package com.bv.isis.console.impl.processor.display;

//
//Imports syst�me
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
* Cette classe est une sp�cialisation de la classe JDialog impl�mentant une 
* bo�te de dialogue permet d'activer ou de d�sactiver le rechargement 
* automatique du contenu du tableau, et la p�riode de rechargement.
* L'affichage de la bo�te de dialogue et la r�cup�ration de la saisie sont 
* effectu�s par le biais de la m�thode getPeriod().
* ----------------------------------------------------------*/
public class RefreshDialog 
	extends JDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: RefreshDialog
	* 
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle appelle la m�thode 
	* makePanel() afin de construire la bo�te de dialogue � afficher.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface.
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
		// Construction de la bo�te d'identification
		makePanel();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getPeriod
	* 
	* Description:
	* Cette m�thode permet d'obtenir l'affichage de la bo�te de dialogue, et 
	* ensuite le r�sultat de la saisie.
	* Le champ et la case � cocher sont pr�-remplis � partir de la valeur 
	* originale de la p�riode pass�e en argument.
	* La valeur retourn�e est d�finie comme suit:
	*  - Valeur n�gative ou nulle: Le rechargement automatique n'est pas 
	*    activ�,
	*  - Valeur positive: Le rechargement automatique est activ� avec une 
	*    p�riode correspondant � la valeur en secondes.
	* 
	* L'ex�cution de cette m�thode est bloqu�e jusqu'� ce que la bo�te de 
	* dialogue soit masqu�e, c'est-�-dire que l'utilisateur ait valid� ou 
	* annul�.
	* 
	* Arguments:
	*  - originalPeriod: La p�riode originale,
	*  - component: Une r�f�rence sur un object Component afin de positionner 
	*    la bo�te de dialogue par rapport � celui-ci.
	* 
	* Retourne: La valeur de la nouvelle p�riode.
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
		// On va pr�-remplir les champs � partir de la valeur pass�e en
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
		// Redimmensionnement de la bo�te
		pack();
		setResizable(false);
		setLocationRelativeTo(component);
		// Affichage de la fen�tre (bloquant)
		show();
		// Lorsque l'on arrive ici, c'est que l'utilisateur a fait un choix
		// Destruction de la bo�te
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
	* Cet attribut maintient une r�f�rence sur un entier correspondant � la 
	* p�riode d�finie par l'utilisateur.
	* ----------------------------------------------------------*/
	private int _period;

	/*----------------------------------------------------------
	* Nom: _activate
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JCheckBox 
	* correspondant � la case d'activation/d�sactivation du rechargement 
	* automatique.
	* ----------------------------------------------------------*/
	private JCheckBox _activate;

	/*----------------------------------------------------------
	* Nom: _periodField
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JTextField 
	* correspondant � la zone de saisie de la p�riode de rechargement.
	* ----------------------------------------------------------*/
	private JTextField _periodField;

	/*----------------------------------------------------------
	* Nom: makePanel
	* 
	* Description:
	* Cette m�thode est charg�e de la construction de la bo�te de dialogue.
	* Elle cr�e une case � cocher permettant d'activer ou de d�sactiver le 
	* rechargement automatique, et une zone de saisie afin de d�finir la 
	* p�riode de rechargement.
	* ----------------------------------------------------------*/
	private void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"RefreshDialog", "makePanel");

		trace_methods.beginningOfMethod();
		// Cr�ation du paneau central
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel central_panel = new JPanel(bag_layout);
		// Pr�paration des contraintes
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.insets = new Insets(2, 2, 2, 2);
		// Cr�ation du JLabel d'activation
		JLabel activate_label = new JLabel(MessageManager.getMessage(
			"&DisplayReload_Activate"));
		bag_layout.setConstraints(activate_label, constraints);
		central_panel.add(activate_label);
		// Cr�ation du JLabel de la periode
		JLabel period_label = new JLabel(MessageManager.getMessage(
			"&DisplayReload_Period"));
		constraints.gridy ++;
		bag_layout.setConstraints(period_label, constraints);
		central_panel.add(period_label);
		// Cr�ation de la case � cocher d'activation
		_activate = new JCheckBox();
		constraints.gridy = 0;
		constraints.gridx ++;
		bag_layout.setConstraints(_activate, constraints);
		central_panel.add(_activate);
		// Ajout du callback sur Entr�e
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
		// Cr�ation de la zone de saisie de la p�riode
		_periodField = new JTextField(5);
		_periodField.setEnabled(false);
		constraints.gridy ++;
		bag_layout.setConstraints(_periodField, constraints);
		central_panel.add(_periodField);
		// Ajout du callback sur Entr�e
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
		// On ajoute un callback sur le changement d'�tat de la case � cocher
		_activate.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				_periodField.setEnabled(_activate.isSelected());
			}
		});
		// Cr�ation du paneau des boutons
		JPanel buttons_panel = new JPanel(new FlowLayout(JLabel.HORIZONTAL));
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
				validatePeriod();
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
				cancelPeriod();
			}
		});
		buttons_panel.add(cancel_button);
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
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton 
	* "Valider".
	* Elle red�finie la valeur de l'attribut _period en fonction de la valeur 
	* de la p�riode d�finie dans la zone _period et de la valeur de la case � 
	* cocher _activate.
	* Ensuite, la fen�tre est cach�e afin de lib�rer l'ex�cution de la m�thode 
	* getPeriod().
	* ----------------------------------------------------------*/
	private void validatePeriod()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"RefreshDialog", "validatePeriod");
		int period_value = 0;

		trace_methods.beginningOfMethod();
		// On va d�finir la nouvelle p�riode en fonction de ce qui a �t�
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
			// On continue quand m�me
		}
		if(_activate.isSelected() == false)
		{
			period_value *= -1;
		}
		_period = period_value;
		// On masque la bo�te de dialogue de sorte � lib�rer la
		// m�thode getPeriod().
		hide();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: cancelPeriod
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur le bouton 
	* "Annuler".
	* Elle masque la bo�te de dialogue afin de lib�rer l'ex�cution de la 
	* m�thode getPeriod().
	* ----------------------------------------------------------*/
	private void cancelPeriod()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"RefreshDialog", "cancelPeriod");

		trace_methods.beginningOfMethod();
		// On masque la bo�te de dialogue de sorte � lib�rer la
		// m�thode getPeriod().
		hide();
		trace_methods.endOfMethod();
	}
}
