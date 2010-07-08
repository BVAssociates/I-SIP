/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/SelectionDialog.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Popup de sélection à l'aide de boutons radios
* DATE:        07/07/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.config.implementation.view;

// 
// Imports système
//
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

//
// Imports du projet
//
import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*------------------------------------------------------------
 * Nom: SelectionDialog
 * 
 * Description: 
 * Cette classe est une spécialisation de classe JDialog. 
 * Elle est destinée à permettre l'affichage d'une fenêtre sous 
 * la forme d'une popup contenant un nombre N de boutons radios 
 * et d'une zone de saisie.
 * ------------------------------------------------------------*/
public class SelectionDialog extends JDialog {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: SimpleDialog
	* 
	* Description:
	* Constructeur. Il permet d'initialiser une instance de la classe.
	* 
	* Arguments:
	*   - window : une référence sur la fenêtre principale de l'application,
	*  			cette référence est utilisée pour que la popup soit bloquée
	*  			au dessus de la fenêtre.
	*   - datas : Un ArrayList de String []. Ce tableau contient pour chaque
	*  			élément un tableau de chaînes de caractères où la première
	*  			contient le texte a afficher à côté du bouton radio, la 
	*  			seconde l'expression régulière qui lui est associé et 
	*  			en troisième, un caractère séparateur si besoin est.
	*   - text : Une chaîne de caractères explicative a afficher.
 	* ----------------------------------------------------------*/
 	public SelectionDialog( 
			MainWindowInterface window, 
			ArrayList<String []> datas, 
			String text
			) 
	{
		// On rend la popup bloquante
 		super((Frame) window, text, true);
		 		
 		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
 				"SelectionDialog", "SelectionDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("window=" + window);
		trace_arguments.writeTrace("datas=" + datas);
		trace_arguments.writeTrace("text=" + text);
			
		// On garde en mémoire un tableau contenant les textes à afficher
		// et les expressions régulières associées
		_radioDatasTable = datas;
		_validatedReleased = false;
		// On construite la fenêtre.
		makePanel();
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: getSelectedInfo
	* 
	* Description:
	* Cette méthode est appelée lorsque l'on souhaite afficher la fenêtre.
	* Celle-ci s'ouvre sous la forme d'une popup bloquante.
	* Elle retourne une chaîne de caractères : l'expression régulière associée
	* à la valeur de la zone de saisie.
	* Elle prends en paramètres l'ancienne expression régulière afin de 
	* réafficher la fenêtre comme elle l'était avant ou une chaîne vide sinon.
	* 
	* Argument :
	*   - oldValue : l'ancienne chaîne de l'expression régulière et de la zone
	*  			de saisie. Cette chaîne peut être vide.
	*   
	* Retourne : Une chaîne de caractères : l'expression régulière associée à 
	*			la zone de saisie ou une chaîne vide si annulation
	* ----------------------------------------------------------*/
	public String getSelectedInfo(String oldValue) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectionDialog", "getSelectedInfo");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("oldValue=" + oldValue);
		
		// La fenêtre ne peut être redimenssionnée
		setResizable(false);
		// Récupération de la taille de l'écran
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen_size = toolkit.getScreenSize();
		// Récupération de la taille de la fenêtre
		Dimension window_size = getSize();
		// Calcul des coordonnées de la fenêtre
		int x_position = (screen_size.width - window_size.width) / 2;
		int y_position = (screen_size.height - window_size.height) / 2;
		// Déplacement de la fenêtre
		setLocation(x_position, y_position);
		
		// On découpe la chaîne passée en entrée pour retrouver l'expression
		// régulière et la zone de saisie.
		String [] value = oldValue.split(" ");
		if (value != null && value.length == 2) {
			boolean trouve = false;
			// On recherche le bouton radio qui avait été sélectionné et
			// on le recoche
			Enumeration<AbstractButton> e = _groupRadioButton.getElements();
			do {
				RadioButton buttonRadio = (RadioButton) e.nextElement();
				if (buttonRadio.getRegularExpression().equals(value[0])) {
					buttonRadio.setSelected(true);
					
					if ( !buttonRadio.getSeparator().equals("")	&& 
							value[1].contains(buttonRadio.getSeparator()) )
					{
						value[1] = value[1].substring(1, value[1].length() - 1);
					}
					
					// On remet à jour la zone de saisie
					_inputAreaTextField.setText(value[1]);
					trouve = true;
				}
			}while( e.hasMoreElements() && trouve == false );
		}
		// On affiche la fenêtre (bloquante)
		setVisible(true);
		
		// Si on arrive ici, la boîte de dialogue peut être détruite
		dispose();
		
		if (_validatedReleased) {
			// Si l'utilisateur à cliqué sur le bouton "Valider", on construit la 
			// chaîne de retour.
			if ( !_inputAreaTextField.getText().equals("")) {
				
				Enumeration<AbstractButton> e = _groupRadioButton.getElements();
				do {
					RadioButton b = (RadioButton) e.nextElement();
					if (b.isSelected()) {
						trace_methods.endOfMethod();
						return b.getRegularExpression() + " " + b.getSeparator() 
								+ _inputAreaTextField.getText() + b.getSeparator();
					}
				}while( e.hasMoreElements() );
			}
		}
		trace_methods.endOfMethod();
		return oldValue;
	}
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom: SelectionDialog
	 * 
	 * Description: 
	 * Cette classe est une spécialisation de classe JRadioButton. 
	 * Elle est destinée à permettre l'affichage d'une bouton radio 
	 * avec un texte qui lui est associé mais aussi de conserver en 
	 * mémoire une expression régulière et un séparateur associés à ce
	 * bouton radio.
	 * ------------------------------------------------------------*/
	private class RadioButton extends JRadioButton {

		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: RadioButton
		* 
		* Description:
		* Constructeur. Il permet d'initialiser une instance de la classe.
		* 
		* Arguments:
		*   - text : Une chaine de caractères a afficher a côté du bouton
		*  		radio.
		*   - regularExpression : Une chaîne de caractères correspondant
		*  		au texte a retourner si le bouton est sélectioné.
		*   - separator : Un caractère séparateur qui entourera la valeur 
		*  		une fois retournée.
		* ----------------------------------------------------------*/
		public RadioButton(
				String text, 
				String regularExpression, 
				String separator) {
			// On crée le bouton radio avec le texte passé en paramètre
			super(text);
			
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"RadioButton", "RadioButton");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("text=" + text);
			trace_arguments.writeTrace("regularExpression=" + regularExpression);
			trace_arguments.writeTrace("separator=" + separator);
			
			_separator = separator;
			_regularExpression = regularExpression;
			
			trace_methods.endOfMethod();
		}

		/*----------------------------------------------------------
		 * Nom: getRegularExpression
		 * 
		 * Description:
		 * Cette méthode retourne la chaine de caracteres correspondant
		 * à l'expression régulière.
		 * 
		 * Retourne: Une chaine de caractere : L'expression régulière.
		 * ----------------------------------------------------------*/
		public String getRegularExpression() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"RadioButton", "getRegularExpression");
			
			trace_methods.endOfMethod();
			return _regularExpression;
		}
		
		/*----------------------------------------------------------
		 * Nom: getSeparator
		 * 
		 * Description:
		 * Cette méthode retourne la chaine de caracteres correspondant
		 * au séparateur.
		 * 
		 * Retourne: Une chaine de caractere : Le séparateur.
		 * ----------------------------------------------------------*/
		public String getSeparator() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"RadioButton", "getSeparator");
			
			trace_methods.endOfMethod();
			return _separator;
		}

		/*------------------------------------------------------------
		 * Nom : _regularExpression
		 * 
		 * Description :
		 * Cette attribut maintient une référence sur une l'expression 
		 * régulière.
		 * ------------------------------------------------------------*/
		private String _regularExpression;
		
		/*------------------------------------------------------------
		 * Nom : _separator
		 * 
		 * Description :
		 * Cette attribut maintient une référence sur une le séparateur.
		 * ------------------------------------------------------------*/
		private String _separator;
	}

	/*------------------------------------------------------------
	 * Nom : _radioDatasTable
	 * 
	 * Description :
	 * Cette attribut maintient une référence vers le tableau contenant
	 * l'ensemble des textes à afficher et des expressions régulières qui 
	 * leurs sont associés.
	 * ------------------------------------------------------------*/
	private ArrayList<String []> _radioDatasTable;
	
	/*------------------------------------------------------------
	 * Nom : _groupRadioButton
	 * 
	 * Description :
	 * Cette attribut maintient une référence sur un groupe de boutons.
	 * Un group de boutons permet qu'il n'y ai qu'un seul bouton radio
	 * de sélectionné à la fois.
	 * Cette également via cet attribut que l'on accède aux boutons 
	 * radio de la fenêtre.
	 * ------------------------------------------------------------*/
	private ButtonGroup _groupRadioButton;
	
	/*------------------------------------------------------------
	 * Nom : _groupRadioButton
	 * 
	 * Description :
	 * Cette attribut maintient une référence sur une zone de saisie.
	 * Il est utilisé pour récupérer la valeur saisie par l'utilisateur.
	 * ------------------------------------------------------------*/
	private JTextField _inputAreaTextField;
	
	/*------------------------------------------------------------
	 * Nom : _validatedReleased
	 * 
	 * Description :
	 * Cette attribut maintient une référence sur un booléen. Celui ci 
	 * est a vrai (true) si l'utilisateur a cliquer sur le bouton Valider
	 * à faux (false) sinon.
	 * ------------------------------------------------------------*/
	private boolean _validatedReleased;
	
	/*------------------------------------------------------------
	 * Nom : _validateButton
	 * 
	 * Description :
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * enregistrer les informations saisies et pour fermer la popup.
	 * ------------------------------------------------------------*/
	private JButton _validateButton;
	
	/*------------------------------------------------------------
	 * Nom : _cancelButton
	 * 
	 * Description :
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * annuler les informations saisies et pour fermer la popup.
	 * ------------------------------------------------------------*/
	private JButton _cancelButton;
	
	/*------------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette méthode est appelé lors de la construction de la fenêtre.
	 * Son rôle est d'instancier les composants graphiques de la fenêtre
	 * et de les positionner.
	 * ------------------------------------------------------------*/
	private void makePanel() {
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectionDialog", "makePanel");
		trace_methods.beginningOfMethod();

		// On donne une taille fixe à la fenêtre
		setSize(new Dimension(275, 60+30*_radioDatasTable.size()));
		
		// On positionne le texte en haut de la fenêtre et les boutons 
		// radios en dessous.
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel button_panel = new JPanel(bag_layout);
		button_panel.setBorder(BorderFactory.createTitledBorder(""));
		constraints.fill = GridBagConstraints.HORIZONTAL;
	    constraints.insets = new Insets(5, 5, 5, 5);
	    constraints.ipadx = 0;
	    constraints.ipady = 0;
	   
	    constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.insets = new Insets(2, 2, 2, 2);
		
		_groupRadioButton = new ButtonGroup();
		constraints.anchor = GridBagConstraints.WEST;
		
		JLabel valueLabel = new JLabel("Value", SwingConstants.CENTER);
		
		valueLabel.setMinimumSize(new Dimension(60,20));
		valueLabel.setMaximumSize(new Dimension(60,20));
		valueLabel.setPreferredSize(new Dimension(60,20));
		
		_inputAreaTextField = new JTextField();
		_inputAreaTextField.setMinimumSize(new Dimension(60,20));
		_inputAreaTextField.setMaximumSize(new Dimension(60,20));
		_inputAreaTextField.setPreferredSize(new Dimension(60,20));
		_inputAreaTextField.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				// Appel de la méthode idoine
				fieldsHaveChanged();
			}
		});
		
		// Création des boutons radios et de la zone de saisie
		if (_radioDatasTable != null && _radioDatasTable.size() > 0) {
			for (int index=0 ; index < _radioDatasTable.size() ; index++) {
				constraints.gridy = index;
				// On positionne le texte de la zone de saisie au dessus de 
				// celle-ci
				if (index == (Math.ceil(_radioDatasTable.size()/2))-1) {
					constraints.anchor = GridBagConstraints.EAST;
					constraints.gridx = 1;
					constraints.gridwidth = 1;
					button_panel.add(valueLabel, constraints);
					
					constraints.anchor = GridBagConstraints.WEST;
					constraints.gridx = 0;
				}
				// On positionne la zone de saisie à la moitié du nombre
				// de boutons radios environ.
				if (index == Math.ceil(_radioDatasTable.size()/2)) {
					constraints.anchor = GridBagConstraints.EAST;
					constraints.gridx = 1;
					constraints.gridwidth = 1;
					button_panel.add(_inputAreaTextField, constraints);
					
					constraints.anchor = GridBagConstraints.WEST;
					constraints.gridx = 0;
				}
				
				// Création et positionnement des boutons radios
				String [] data = _radioDatasTable.get(index);
				if (data != null && data.length > 1) {
					String expr = data[1];
					String separator = "";
					if (data.length > 2) {
						separator = data[2];
					}
					
					RadioButton radioButton = new RadioButton(
							(_radioDatasTable.get(index))[0], expr, separator );
					radioButton.setMinimumSize(new Dimension(120 * constraints.gridwidth,20));
					radioButton.setMaximumSize(new Dimension(120 * constraints.gridwidth,20));
					radioButton.setPreferredSize(new Dimension(120 * constraints.gridwidth,20));
					radioButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							fieldsHaveChanged();
						}
					});
					_groupRadioButton.add(radioButton);
					button_panel.add(radioButton, constraints);
				}
				constraints.gridwidth = 2;
			}		
		}
		
		JPanel central_panel = new JPanel(bag_layout);

		constraints.fill = GridBagConstraints.NONE;
	    constraints.insets = new Insets(5, 5, 5, 5);
	    constraints.ipadx = 0;
	    constraints.ipady = 0;
	   
	    constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.insets = new Insets(2, 2, 2, 2);
		
		// On posisitonne en bas de la fenêtre, les 2 boutons "Valider" 
		// et "Annuler"
		_validateButton = new JButton();
		_validateButton.setMinimumSize(new Dimension(100, 21));
		_validateButton.setMaximumSize(new Dimension(100, 21));
		_validateButton.setPreferredSize(new Dimension(100, 21));
		_validateButton.setText(MessageManager.getMessage(
				"&ComponentConfiguration_ValidateButton"));
		_validateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setReleased(true);
			}
		});
		_validateButton.setEnabled(false);
		
		_cancelButton = new JButton();
		_cancelButton.setMinimumSize(new Dimension(100, 21));
		_cancelButton.setMaximumSize(new Dimension(100, 21));
		_cancelButton.setPreferredSize(new Dimension(100, 21));
		_cancelButton.setText(MessageManager.getMessage(
				"&ComponentConfiguration_CancelButton"));
		_cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				setReleased(false);
			}
		});
		
		central_panel.add(button_panel, constraints);
		
		constraints.insets = new Insets(5, 20, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		central_panel.add(_cancelButton, constraints);
		
		constraints.gridx = 1;
		constraints.insets = new Insets(5, 5, 5, 20);
		central_panel.add(_validateButton, constraints);
		
		// Ajout du paneau central au content pane
		getContentPane().add(central_panel, BorderLayout.CENTER);
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: fieldsHaveChanged
	*
	* Description:
	* Cette méthode est appelée lorsque la sélection a changé dans la liste des
	* boutons radios ou lorsque le zone de saisie a été modifiée.
	* Elle permet de mettre à jour l'état du bouton "Valider" en fonction de la
	* présence ou non des informations.
	* ----------------------------------------------------------*/
	private void fieldsHaveChanged() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"SelectionDialog", "fieldsHaveChanged");
		trace_methods.beginningOfMethod();
		
		// Vérification de la présence des données
		String value = new String(_inputAreaTextField.getText());
		ButtonModel b = _groupRadioButton.getSelection();
		if(value == null || value.equals("") == true 
				|| b == null || !b.isSelected())
		{
			// Une case n'a pas été coché ou il n'y a rien dans la 
			// zone de saisie.
			_validateButton.setEnabled(false);
			trace_methods.endOfMethod();
			return;
		}
		// Ok, on peut valider le bouton
		_validateButton.setEnabled(true);
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: setReleased
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliquer sur un des
	* deux boutons de la fenêtre. Elle cache la fenetre et positionne 
	* le boolean _validatedReleased a la valeur passée en paramètre.
	* 
	* Paramètre :
	*	- b : un booleen
	* ----------------------------------------------------------*/
	private void setReleased(boolean b) {
		_validatedReleased = b;
		setVisible(false);
	}
}
