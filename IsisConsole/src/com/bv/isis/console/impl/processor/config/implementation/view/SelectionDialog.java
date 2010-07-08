/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/SelectionDialog.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Popup de s�lection � l'aide de boutons radios
* DATE:        07/07/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.implementation.view;

// 
// Imports syst�me
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
 * Cette classe est une sp�cialisation de classe JDialog. 
 * Elle est destin�e � permettre l'affichage d'une fen�tre sous 
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
	*   - window : une r�f�rence sur la fen�tre principale de l'application,
	*  			cette r�f�rence est utilis�e pour que la popup soit bloqu�e
	*  			au dessus de la fen�tre.
	*   - datas : Un ArrayList de String []. Ce tableau contient pour chaque
	*  			�l�ment un tableau de cha�nes de caract�res o� la premi�re
	*  			contient le texte a afficher � c�t� du bouton radio, la 
	*  			seconde l'expression r�guli�re qui lui est associ� et 
	*  			en troisi�me, un caract�re s�parateur si besoin est.
	*   - text : Une cha�ne de caract�res explicative a afficher.
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
			
		// On garde en m�moire un tableau contenant les textes � afficher
		// et les expressions r�guli�res associ�es
		_radioDatasTable = datas;
		_validatedReleased = false;
		// On construite la fen�tre.
		makePanel();
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: getSelectedInfo
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'on souhaite afficher la fen�tre.
	* Celle-ci s'ouvre sous la forme d'une popup bloquante.
	* Elle retourne une cha�ne de caract�res : l'expression r�guli�re associ�e
	* � la valeur de la zone de saisie.
	* Elle prends en param�tres l'ancienne expression r�guli�re afin de 
	* r�afficher la fen�tre comme elle l'�tait avant ou une cha�ne vide sinon.
	* 
	* Argument :
	*   - oldValue : l'ancienne cha�ne de l'expression r�guli�re et de la zone
	*  			de saisie. Cette cha�ne peut �tre vide.
	*   
	* Retourne : Une cha�ne de caract�res : l'expression r�guli�re associ�e � 
	*			la zone de saisie ou une cha�ne vide si annulation
	* ----------------------------------------------------------*/
	public String getSelectedInfo(String oldValue) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectionDialog", "getSelectedInfo");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("oldValue=" + oldValue);
		
		// La fen�tre ne peut �tre redimenssionn�e
		setResizable(false);
		// R�cup�ration de la taille de l'�cran
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen_size = toolkit.getScreenSize();
		// R�cup�ration de la taille de la fen�tre
		Dimension window_size = getSize();
		// Calcul des coordonn�es de la fen�tre
		int x_position = (screen_size.width - window_size.width) / 2;
		int y_position = (screen_size.height - window_size.height) / 2;
		// D�placement de la fen�tre
		setLocation(x_position, y_position);
		
		// On d�coupe la cha�ne pass�e en entr�e pour retrouver l'expression
		// r�guli�re et la zone de saisie.
		String [] value = oldValue.split(" ");
		if (value != null && value.length == 2) {
			boolean trouve = false;
			// On recherche le bouton radio qui avait �t� s�lectionn� et
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
					
					// On remet � jour la zone de saisie
					_inputAreaTextField.setText(value[1]);
					trouve = true;
				}
			}while( e.hasMoreElements() && trouve == false );
		}
		// On affiche la fen�tre (bloquante)
		setVisible(true);
		
		// Si on arrive ici, la bo�te de dialogue peut �tre d�truite
		dispose();
		
		if (_validatedReleased) {
			// Si l'utilisateur � cliqu� sur le bouton "Valider", on construit la 
			// cha�ne de retour.
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
	 * Cette classe est une sp�cialisation de classe JRadioButton. 
	 * Elle est destin�e � permettre l'affichage d'une bouton radio 
	 * avec un texte qui lui est associ� mais aussi de conserver en 
	 * m�moire une expression r�guli�re et un s�parateur associ�s � ce
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
		*   - text : Une chaine de caract�res a afficher a c�t� du bouton
		*  		radio.
		*   - regularExpression : Une cha�ne de caract�res correspondant
		*  		au texte a retourner si le bouton est s�lection�.
		*   - separator : Un caract�re s�parateur qui entourera la valeur 
		*  		une fois retourn�e.
		* ----------------------------------------------------------*/
		public RadioButton(
				String text, 
				String regularExpression, 
				String separator) {
			// On cr�e le bouton radio avec le texte pass� en param�tre
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
		 * Cette m�thode retourne la chaine de caracteres correspondant
		 * � l'expression r�guli�re.
		 * 
		 * Retourne: Une chaine de caractere : L'expression r�guli�re.
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
		 * Cette m�thode retourne la chaine de caracteres correspondant
		 * au s�parateur.
		 * 
		 * Retourne: Une chaine de caractere : Le s�parateur.
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
		 * Cette attribut maintient une r�f�rence sur une l'expression 
		 * r�guli�re.
		 * ------------------------------------------------------------*/
		private String _regularExpression;
		
		/*------------------------------------------------------------
		 * Nom : _separator
		 * 
		 * Description :
		 * Cette attribut maintient une r�f�rence sur une le s�parateur.
		 * ------------------------------------------------------------*/
		private String _separator;
	}

	/*------------------------------------------------------------
	 * Nom : _radioDatasTable
	 * 
	 * Description :
	 * Cette attribut maintient une r�f�rence vers le tableau contenant
	 * l'ensemble des textes � afficher et des expressions r�guli�res qui 
	 * leurs sont associ�s.
	 * ------------------------------------------------------------*/
	private ArrayList<String []> _radioDatasTable;
	
	/*------------------------------------------------------------
	 * Nom : _groupRadioButton
	 * 
	 * Description :
	 * Cette attribut maintient une r�f�rence sur un groupe de boutons.
	 * Un group de boutons permet qu'il n'y ai qu'un seul bouton radio
	 * de s�lectionn� � la fois.
	 * Cette �galement via cet attribut que l'on acc�de aux boutons 
	 * radio de la fen�tre.
	 * ------------------------------------------------------------*/
	private ButtonGroup _groupRadioButton;
	
	/*------------------------------------------------------------
	 * Nom : _groupRadioButton
	 * 
	 * Description :
	 * Cette attribut maintient une r�f�rence sur une zone de saisie.
	 * Il est utilis� pour r�cup�rer la valeur saisie par l'utilisateur.
	 * ------------------------------------------------------------*/
	private JTextField _inputAreaTextField;
	
	/*------------------------------------------------------------
	 * Nom : _validatedReleased
	 * 
	 * Description :
	 * Cette attribut maintient une r�f�rence sur un bool�en. Celui ci 
	 * est a vrai (true) si l'utilisateur a cliquer sur le bouton Valider
	 * � faux (false) sinon.
	 * ------------------------------------------------------------*/
	private boolean _validatedReleased;
	
	/*------------------------------------------------------------
	 * Nom : _validateButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * enregistrer les informations saisies et pour fermer la popup.
	 * ------------------------------------------------------------*/
	private JButton _validateButton;
	
	/*------------------------------------------------------------
	 * Nom : _cancelButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * annuler les informations saisies et pour fermer la popup.
	 * ------------------------------------------------------------*/
	private JButton _cancelButton;
	
	/*------------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette m�thode est appel� lors de la construction de la fen�tre.
	 * Son r�le est d'instancier les composants graphiques de la fen�tre
	 * et de les positionner.
	 * ------------------------------------------------------------*/
	private void makePanel() {
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectionDialog", "makePanel");
		trace_methods.beginningOfMethod();

		// On donne une taille fixe � la fen�tre
		setSize(new Dimension(275, 60+30*_radioDatasTable.size()));
		
		// On positionne le texte en haut de la fen�tre et les boutons 
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
				// Appel de la m�thode idoine
				fieldsHaveChanged();
			}
		});
		
		// Cr�ation des boutons radios et de la zone de saisie
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
				// On positionne la zone de saisie � la moiti� du nombre
				// de boutons radios environ.
				if (index == Math.ceil(_radioDatasTable.size()/2)) {
					constraints.anchor = GridBagConstraints.EAST;
					constraints.gridx = 1;
					constraints.gridwidth = 1;
					button_panel.add(_inputAreaTextField, constraints);
					
					constraints.anchor = GridBagConstraints.WEST;
					constraints.gridx = 0;
				}
				
				// Cr�ation et positionnement des boutons radios
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
		
		// On posisitonne en bas de la fen�tre, les 2 boutons "Valider" 
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
	* Cette m�thode est appel�e lorsque la s�lection a chang� dans la liste des
	* boutons radios ou lorsque le zone de saisie a �t� modifi�e.
	* Elle permet de mettre � jour l'�tat du bouton "Valider" en fonction de la
	* pr�sence ou non des informations.
	* ----------------------------------------------------------*/
	private void fieldsHaveChanged() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"SelectionDialog", "fieldsHaveChanged");
		trace_methods.beginningOfMethod();
		
		// V�rification de la pr�sence des donn�es
		String value = new String(_inputAreaTextField.getText());
		ButtonModel b = _groupRadioButton.getSelection();
		if(value == null || value.equals("") == true 
				|| b == null || !b.isSelected())
		{
			// Une case n'a pas �t� coch� ou il n'y a rien dans la 
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
	* Cette m�thode est appel�e lorsque l'utilisateur a cliquer sur un des
	* deux boutons de la fen�tre. Elle cache la fenetre et positionne 
	* le boolean _validatedReleased a la valeur pass�e en param�tre.
	* 
	* Param�tre :
	*	- b : un booleen
	* ----------------------------------------------------------*/
	private void setReleased(boolean b) {
		_validatedReleased = b;
		setVisible(false);
	}
}
