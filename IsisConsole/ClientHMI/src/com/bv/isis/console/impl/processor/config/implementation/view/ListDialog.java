/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/ListDialog.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Popup de s�lection � l'aide d'une liste
* DATE:        24/07/2008
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
 * Cette classe est une sp�cialisation de classe JDialog. 
 * Elle est destin�e � permettre l'affichage d'une fen�tre sous 
 * la forme d'une popup contenant une liste � s�lection multiple.
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
	*   - window : une r�f�rence sur la fen�tre principale de l'application,
	*  			cette r�f�rence est utilis�e pour que la popup soit bloqu�e
	*  			au dessus de la fen�tre.
	*   - text : Une cha�ne de caract�res explicative a afficher.
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
		// On construit la fen�tre
 		makePanel();
 		
 		trace_methods.endOfMethod();		
	}
	
 	/*----------------------------------------------------------
	* Nom: getSelectedInfo
	* 
	* Description:
	* Cette m�thode est appel�e lorsque l'on souhaite afficher la fen�tre.
	* Celle-ci s'ouvre sous la forme d'une popup bloquante.
	* Elle retourne une cha�ne de caract�res : la concat�nation des �l�ments
	* de la liste s�lection�e
	* Elle prends en param�tres un tableau de cha�nes de caract�res correspondant
	* aux valeurs � afficher dans la liste.
	* 
	* Argument :
	*   - values : Un tableau de cha�nes de caract�res � afficher dans la liste.
	*   
	* Retourne : Une cha�ne de caract�res : La concat�nation des �l�ments 
	* 			s�lection�s.
	* ----------------------------------------------------------*/
	public String getSelectedInfo(String [] values, String oldValue) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
 				"ListDialog", "getSelectedInfo");
 		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

 		trace_methods.beginningOfMethod();
 		trace_arguments.writeTrace("values=" + values);
 		
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
		
		// On ajoute les �l�ments du tableau d'entr�e dans la liste
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
		// On affiche la fen�tre (bloquante)
		setVisible(true);
		
		// Si on arrive ici, la bo�te de dialogue peut �tre d�truite
		dispose();
		if (_validatedReleased == true) {
			
			// On r�cup�re la liste des �l�ments s�lection�s
			Object[] res = _valuesList.getSelectedValues();
			// On construit la cha�ne r�sultat
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
	 * Cette attribut maintient une r�f�rence vers l'esnemble des donn�es
	 * affich�es dans la liste de la fen�tre.
	 * ------------------------------------------------------------*/
	private DefaultListModel _modelList;
	
	/*------------------------------------------------------------
	 * Nom : _valuesList
	 * 
	 * Description :
	 * Cette attribut maintient une r�f�rence sur la JList.
	 * ------------------------------------------------------------*/
	private JList _valuesList;
	
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
	 * Nom : _validatedReleased
	 * 
	 * Description :
	 * Cette attribut maintient une r�f�rence sur un bool�en. Celui ci 
	 * est a vrai (true) si l'utilisateur a cliquer sur le bouton Valider
	 * � faux (false) sinon.
	 * ------------------------------------------------------------*/
	private boolean _validatedReleased;
	
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
 				"ListDialog", "makePanel");
 		trace_methods.beginningOfMethod();
 		
 		// On dimensionne la fen�tre
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
				// L'utilisateur a choisit de sauvegarder les donn�es choisies
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
	 * Cette m�thode est appel�e lors de la s�lection ou de la d�s�lection
	 * d'un �l�ment dans la liste.
	 * Si un �l�ment est s�lectionn�, on active le bouton valider ;
	 * si aucun �l�ment n'est s�lection�, on le d�sactive.
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
	* Cette m�thode est appel�e lorsque l'utilisateur a cliquer sur un des
	* deux boutons de la fen�tre. Elle cache la fenetre et positionne 
	* le boolean _validatedReleased a la valeur pass�e en param�tre.
	* 
	* Param�tre :
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
