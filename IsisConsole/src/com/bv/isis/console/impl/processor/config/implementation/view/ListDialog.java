/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/ListDialog.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Popup de sélection à l'aide d'une liste
* DATE:        24/07/2008
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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// 
// Imports du projet
//
import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*------------------------------------------------------------
 * Nom: ListDialog
 * 
 * Description: 
 * Cette classe est une spécialisation de classe JDialog. 
 * Elle est destinée à permettre l'affichage d'une fenêtre sous 
 * la forme d'une popup contenant une liste à sélection multiple.
 * ------------------------------------------------------------*/
public class ListDialog extends JDialog {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ListDialog
	* 
	* Description:
	* Constructeur. Il permet d'initialiser une instance de la classe.
	* 
	* Arguments:
	*   - window : une référence sur la fenêtre principale de l'application,
	*  			cette référence est utilisée pour que la popup soit bloquée
	*  			au dessus de la fenêtre.
	*   - text : Une chaîne de caractères explicative a afficher.
 	* ----------------------------------------------------------*/
 	public ListDialog( 
			MainWindowInterface window, 
			String text
			) 
	{
		// On rend la popup bloquante
 		super((Frame) window, text, true);
 		
 		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
 				"ListDialog", "ListDialog");
 		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

 		trace_methods.beginningOfMethod();
 		trace_arguments.writeTrace("window=" + window);
 		trace_arguments.writeTrace("text=" + text);
 			
 		_validatedReleased = false;
		// On construit la fenêtre
 		makePanel();
 		
 		trace_methods.endOfMethod();		
	}
	
 	/*----------------------------------------------------------
	* Nom: getSelectedInfo
	* 
	* Description:
	* Cette méthode est appelée lorsque l'on souhaite afficher la fenêtre.
	* Celle-ci s'ouvre sous la forme d'une popup bloquante.
	* Elle retourne une chaîne de caractères : la concaténation des éléments
	* de la liste sélectionée
	* Elle prends en paramètres un tableau de chaînes de caractères correspondant
	* aux valeurs à afficher dans la liste.
	* 
	* Argument :
	*   - values : Un tableau de chaînes de caractères à afficher dans la liste.
	*   
	* Retourne : Une chaîne de caractères : La concaténation des éléments 
	* 			sélectionés.
	* ----------------------------------------------------------*/
	public String getSelectedInfo(String [] values, String oldValue) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
 				"ListDialog", "getSelectedInfo");
 		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

 		trace_methods.beginningOfMethod();
 		trace_arguments.writeTrace("values=" + values);
 		
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
		
		// On ajoute les éléments du tableau d'entrée dans la liste
		for (int index = 0 ; index < values.length ; index++) 
			_modelList.addElement(values[index]);
			
		
		if (!oldValue.equals("")) {
			
			String [] oldValues = oldValue.split(",");
			int [] selectedIndices = new int [oldValues.length];
			int indexIndices = 0;
			for ( ; indexIndices < selectedIndices.length ; indexIndices++)
				selectedIndices[indexIndices] = -1;
			indexIndices = 0;
		
			for (int index = 0 ; index < values.length ; index ++) {
				for (int index2 = 0 ; index2 < oldValues.length ; index2++)
					if (values[index].equals(oldValues[index2]))		
						selectedIndices[indexIndices++] = index;
		
			_valuesList.setSelectedIndices(selectedIndices);
			}
		}
		// On affiche la fenêtre (bloquante)
		setVisible(true);
		
		// Si on arrive ici, la boîte de dialogue peut être détruite
		dispose();
		if (_validatedReleased == true) {
			
			// On récupère la liste des éléments sélectionés
			Object[] res = _valuesList.getSelectedValues();
			// On construit la chaîne résultat
			String resu = "";
			for (int index = 0 ; index < res.length ; index ++) {
				resu += res[index].toString();
				
				if (index != res.length-1)
					resu += ",";
			}
			trace_methods.endOfMethod();
			return resu;
		}
		trace_methods.endOfMethod();
		return "";
	}
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _modelList
	 * 
	 * Description :
	 * Cette attribut maintient une référence vers l'esnemble des données
	 * affichées dans la liste de la fenêtre.
	 * ------------------------------------------------------------*/
	private DefaultListModel _modelList;
	
	/*------------------------------------------------------------
	 * Nom : _valuesList
	 * 
	 * Description :
	 * Cette attribut maintient une référence sur la JList.
	 * ------------------------------------------------------------*/
	private JList _valuesList;
	
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
	 * Nom : _validatedReleased
	 * 
	 * Description :
	 * Cette attribut maintient une référence sur un booléen. Celui ci 
	 * est a vrai (true) si l'utilisateur a cliquer sur le bouton Valider
	 * à faux (false) sinon.
	 * ------------------------------------------------------------*/
	private boolean _validatedReleased;
	
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
 				"ListDialog", "makePanel");
 		trace_methods.beginningOfMethod();
 		
 		// On dimensionne la fenêtre
		setSize(new Dimension(275, 220));
		
		GridBagLayout bag_layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
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
		
		_modelList = new DefaultListModel();
		_valuesList = new JList(_modelList);
		_valuesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		_valuesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				changeValidateButtonStatus();
			}
		});
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(_valuesList);
		scrollPane.setMinimumSize(new Dimension(258, 130));
		scrollPane.setMaximumSize(new Dimension(258, 130));
		scrollPane.setPreferredSize(new Dimension(258, 130));
		
		central_panel.add(scrollPane, constraints);
		
		_validateButton = new JButton();
		_validateButton.setText(MessageManager.getMessage(
				"&ComponentConfiguration_ValidateButton"));
		_validateButton.setMinimumSize(new Dimension(100, 22));
		_validateButton.setMaximumSize(new Dimension(100, 22));
		_validateButton.setPreferredSize(new Dimension(100, 22));
		_validateButton.setEnabled(false);
		_validateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				// L'utilisateur a choisit de sauvegarder les données choisies
				setReleased(true);
			}
		});
		
		_cancelButton = new JButton();
		_cancelButton.setText(MessageManager.getMessage(
				"&ComponentConfiguration_CancelButton"));
		_cancelButton.setMinimumSize(new Dimension(100, 22));
		_cancelButton.setMaximumSize(new Dimension(100, 22));
		_cancelButton.setPreferredSize(new Dimension(100, 22));
		_cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				// L'utilisateur a choisit de quitter sans sauvegarder
				setReleased(false);
			}
		});
		
		constraints.insets = new Insets(5, 29, 5, 5);
	    constraints.gridy = 1;
		constraints.gridwidth = 1;
		central_panel.add(_cancelButton, constraints);
		
		constraints.insets = new Insets(5, 5, 5, 29);
	    constraints.gridx = 1;
		central_panel.add(_validateButton, constraints);
		
		getContentPane().add(central_panel, BorderLayout.CENTER);
		
		trace_methods.endOfMethod();
	}	
	
	/*------------------------------------------------------------
	 * Nom : changeValidateButtonStatus
	 * 
	 * Description :
	 * Cette méthode est appelée lors de la sélection ou de la désélection
	 * d'un élément dans la liste.
	 * Si un élément est sélectionné, on active le bouton valider ;
	 * si aucun élément n'est sélectioné, on le désactive.
	 * ------------------------------------------------------------*/
	private void changeValidateButtonStatus() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
 				"ListDialog", "changeValidateButtonStatus");
 		trace_methods.beginningOfMethod();
 		
		if (_valuesList.getSelectedValues().length == 0)
			_validateButton.setEnabled(false);
		else
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
	* 	- b : un booleen
	* ----------------------------------------------------------*/
	private void setReleased(boolean bool) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
 				"ListDialog", "setReleased");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

 		trace_methods.beginningOfMethod();
 		trace_arguments.writeTrace("bool=" + bool);
 		
		_validatedReleased = bool;
		setVisible(false);
		
		trace_methods.endOfMethod();
	}
}
