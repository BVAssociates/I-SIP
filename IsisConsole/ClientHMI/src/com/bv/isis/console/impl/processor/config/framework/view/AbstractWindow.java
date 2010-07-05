/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/framework/view/AbstractWindow.java,v $
* $Revision: 1.12 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe principal de la fen�tre
* DATE:        04/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* Revision 1.6  2008/07/11 fc
* Modification de l'action du bouton Annuler
*
* Revision 1.4  2008/06/30 fc
* Ajout de la m�thode displayPanel()
*
* Revision 1.3  2008/06/25 fc
* Ajout de la m�thode enabledNextAndPreviousButton()
*
* Revision 1.2  2008/06/20 fc
* Modification du tableau de ModelInterface en 
* ArrayList<ModelInterface>

* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.framework.view;

//
//Imports syst�me
//
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.processor.ProcessorFrame;

/*------------------------------------------------------------
 * Nom: AbstractWindow
 * 
 * Description:
 * Cette classe repr�sente la fen�tre principale de 
 * l'assistant de param�targe. Elle impl�mente les m�thodes 
 * de ProcesseurFrame. Elle dispose aussi de m�thodes pour 
 * la cin�matique de param�trage.
 * ------------------------------------------------------------*/
public abstract class AbstractWindow extends ProcessorFrame implements
	WindowInterface {
	
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: AbstractWindow
	 *
	 * Description:
	 * Cette m�thode est le seul constructeur de la classe. Elle permet de
	 * construire une instance de JInternalFrame via ProcessorFrame.
	 * Au contructeur de ProcessorFrame, on indique que la fen�tre
	 * peut �tre ferm�e par son menu syst�me (true).
	 * ----------------------------------------------------------*/
	public AbstractWindow() {
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "AbstractWindow");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}	
	
	/*------------------------------------------------------------
     * Nom: getModel
     * 
     * Description: 
     * Cette m�thode impl�mente la m�thode de l'interface WindowInterface. Elle
     * retourne le mod�le de donn�e associ� � l'assistant de parm�trage.
     * 
     * Retourne:
     * Le mod�le de donn�es sous la forme d'une tableau de ModelInterface
     * ------------------------------------------------------------*/
	public ArrayList<ModelInterface> getModel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "getModel");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _modelTable;
	}

	/*------------------------------------------------------------
     * Nom: setModel
     * 
     * Description: 
     * Cette m�thode impl�mente la m�thode de l'interface WindowInterface. 
     * Elle affecte un nouveau mod�le de donn�es � l'assistant de param�trage.
     * 
     * Param�tre:
     *  - model : Le nouveau tableau de ModeleInterface
     * ------------------------------------------------------------*/
	public void setModel(ArrayList<ModelInterface> model) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "setModel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
	
		trace_arguments.writeTrace("model=" + model);
		
		// On ne modifie le modele que si il est diff�rent de celui existant
		if (model != _modelTable)
			_modelTable = model;		
		
		trace_methods.endOfMethod();
	}
	
	/*------------------------------------------------------------
     * Nom: setPanels
     * Description: 
     * Cette m�thode impl�mente la m�thode de l'interface WindowInterface. 
     * Elle enregistre les panneaux � afficher dans la fen�tre � l'aide 
     * de _panelTable. 
     * 
     * Param�tre:
     *  - tabPanels : Le tableau des sous fen�tres � afficher
     * ------------------------------------------------------------*/
	public void setPanel( PanelInterface [] views) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "setPanels");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("views=" + views);
		
		// On ne modifie le tableau des panneaux que si il est diff�rent 
		// de celui existant
		if (views != _panelTable)
			_panelTable = views;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: enabledNextAndPreviousButton
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface WindowInterface.
	 * Cette m�thode permet d'activer ou de d�sactiver les boutons
	 * Suivant et Pr�c�dent de la fen�tre � l'aide de la m�thode
	 * setEnable(boolean) de la classe JButton.
	 * 
	 * Param�tre:
	 *  - b : un bool�en indiquant si on active ou d�sactive les boutons
	 * ----------------------------------------------------------*/
	public void enabledNextAndPreviousButton(boolean b) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "enabledNextAndPreviousButton");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("b=" + b);
		
		_nextButton.setEnabled(b);
		_previousButton.setEnabled(b);		
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: displayPanel
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface WindowInterface.
	 * Cette m�thode permet d'afficher un panneau sans que celui-ci ne soit
	 * pr�sent dans la cin�matique des panneaux.
	 * 
	 * Param�tre:
	 *  - panel : Le panneau a afficher
	 * ----------------------------------------------------------*/
	public void displayPanel(PanelInterface panel) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "displayPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("panel=" + panel);
			
		// On appelle la m�thode repaintPanel avec le panneau pass� 
		// en param�tre.
		repaintPanel(panel);
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getMainWindowInterfaceFromProcessorFrame
	*
	* Description:
	* Cette m�thode permet de r�cup�rer la r�f�rence sur l'interface
	* MainWindowInterface depuis la classe ProcessorFrame, 
	* telle qu'elle a �t� fournie � la m�thode run().
	*
	* Retourne: Une r�f�rence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public MainWindowInterface getMainWindowInterfaceFromProcessorFrame() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "getMainWindowInterfaceFromProcessorFrame");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return getMainWindowInterface();
	}
	
	/*----------------------------------------------------------
	* Nom: getMainPanelSize
	*
	* Description:
	* Cette m�thode permet de r�cup�rer la taille du JPanel dans lequel 
	* sont ins�r�s les sous panneaux.
	*
	* Retourne: La dimension du JPanel principal.
	* ----------------------------------------------------------*/
	public Dimension getMainPanelSize() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "getMainPanelSize");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _mainPanel.getPreferredSize();
	}
	
	// ****************** PROTEGE *********************
	/*------------------------------------------------------------
     * Nom : _menuTable
     * 
     * Description :
     * Tableau de type string regroupant les informations du menu lat�ral gauche de 
     * la fen�tre de l'assistant de param�trage destin�es a �tre affich�.
     * Ces informations seront modifi�es directement par la classe sp�cialisant
     * la classe AbstractWindow.
     * ------------------------------------------------------------*/
	protected JLabel [] _menuTable;
	
	/*------------------------------------------------------------
     * Nom : _modelTable
     * 
     * Description:
     * Cet attribut regroupe l'ensemble des objets du mod�le de donn�es 
     * dans un tableau de type ModelInterface.
     * ------------------------------------------------------------*/
	protected ArrayList<ModelInterface> _modelTable;
	
	/*------------------------------------------------------------
     * Nom : _panelTable
     * 
     * Description:
     * Cet attribut regroupe l'ensemble des panneaux de l'assistant
     * dans un tableau de type PanelInterface.
     * ------------------------------------------------------------*/
	protected PanelInterface [] _panelTable;	
	
	/*------------------------------------------------------------
     * Nom: displayFirstPanel
     * 
     * Description:
     * Cette m�thode est appel�e par la classe sp�cialisant la classe 
     * AbstractWindow.
     * Son r�le est d'affich� le premier panneau du tableau de PanelInterface :
     * _panelTable. 
     * Elle ajoute au JPanel principal : _mainPanel ce panneau via la m�thode 
     * add() de la classe java.awt.Container. Puis, elle affiche ce JPanel 
     * via les m�thodes revalidate() de la classe javax.swing.JComponent et 
     * repaint() de la classe java.awt.Component. 
     * 
     * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
     * sinon.
     * ------------------------------------------------------------*/
	protected boolean displayFirstPanel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "displayFirstPanel");
		trace_methods.beginningOfMethod();
		
		if (_panelTable != null ) {
			// On initialise le num�ro du panneau courrant
			_numCurrentPanel = 0;
			if ( _panelTable.length > 0) {
				// On affiche le panneau
				(_panelTable[_numCurrentPanel]).beforeDisplay(_modelTable);
				repaintPanel(_panelTable[_numCurrentPanel]);
			}
			// On met a jour le menu lat�ral gauche
			_menuTable[0].setEnabled(true);
			_menuTable[0].setFont(new java.awt.Font("Arial", Font.BOLD, 12));
			_title.setText(_menuTable[_numCurrentPanel].getText());
		}
		
		if (_panelTable == null || _panelTable.length == 1)
			// Si il n'y a pas de panneaux afficher, on remplace le
			// boutton "Suivant" par "Terminer"
			_nextButton.setText("Terminer");
		
		// On bloque le boutton "Pr�c�dent"
		_previousButton.setEnabled(false);
		
		trace_methods.endOfMethod();
		return true;
	}
	
	/*----------------------------------------------------------
	* Nom: saveModel
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe AbstractWindow. Elle 
	* est appel�e par le dernier panneau de l'application de param�trage
	* afin de lancer la proc�dure de sauvegarde des donn�es saisis dans les
	* tables de la base de donn�es.
	* Cette m�thode va parcourir le tableau contenant les donn�es saisis
	* et va appeler pour chaque �l�ment la m�thode de sauvegarde : saveData.
	* 
	* Retourne: Vrai (true) si tout c'est bien pass�, faux (false) sinon.
	* ----------------------------------------------------------*/
	protected abstract boolean saveModel() throws InnerException ;

	/*------------------------------------------------------------
     * Nom : makePanel
     * 
     * Description :
     * Cette m�thode est appel� par la classe sp�cialisant la classe AbstractWindow.
     * Son r�le est d'instancier les composants graphiques n�cessaires � l'affichage
     * de la fen�tre d'assistant.
     * ------------------------------------------------------------*/
	protected void makePanel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "makePanel");
		trace_methods.beginningOfMethod();
		
		// La fen�tre de param�trage ne peut �tre redimensionn�e ou maximis�e
		setResizable(false);
		setMaximizable(false);
		
		// Initialisation de la dimension de la fen�tre
		setPreferredSize(new Dimension(700, 550));
		
		// Initialisation du titre de la fen�tre
		setTitle(MessageManager.getMessage("&ComponentConfiguration_Title"));
		
		// Initialisation du num�ro du panneau courant
		_numCurrentPanel = -1;
		
		// Initialisation du menu lat�ral gauche de la fen�tre
		_menuTable = new JLabel [11];
		
		String text = "";
		for (int i = 0 ; i < _menuTable.length ; i++ ) {
			_menuTable[i] = new JLabel();
			_menuTable[i].setMinimumSize(new Dimension(170,20));
			_menuTable[i].setMaximumSize(new Dimension(170,20));
			_menuTable[i].setPreferredSize(new Dimension(170,20));
			text = MessageManager.getMessage("&ComponentConfiguration_Menu" + i);
			if ( ! text.equals("&ComponentConfiguration_Menu" + i) ) {
				_menuTable[i].setText(MessageManager.getMessage("&ComponentConfiguration_Menu" + i));
			}
			else 
				_menuTable[i].setText("");
			_menuTable[i].setFont(new java.awt.Font("Arial", Font.PLAIN, 12));
			_menuTable[i].setEnabled(false);
		}
		
		// Initialisation du titre
		_title = new JLabel("", SwingConstants.RIGHT);
		_title.setMinimumSize(new Dimension(500,20));
		_title.setMaximumSize(new Dimension(500,20));
		_title.setFont(new java.awt.Font("Arial", Font.BOLD, 14));
		
		// Initialisation des bouttons
		_cancelButton = new JButton();
		_cancelButton.setMinimumSize(new Dimension(120,22));
		_cancelButton.setMaximumSize(new Dimension(120,22));
		_cancelButton.setPreferredSize(new Dimension(120,22));
		_cancelButton.setText(MessageManager.getMessage("&ComponentConfiguration_CancelButton"));
		_cancelButton.setName("butAnnuler");
		_cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickCancelButton();
			}
		});
		
		_previousButton = new JButton();
		_previousButton.setMinimumSize(new Dimension(120,22));
		_previousButton.setMaximumSize(new Dimension(120,22));
		_previousButton.setPreferredSize(new Dimension(120,22));
		_previousButton.setText(MessageManager.getMessage("&ComponentConfiguration_PreviousButton")); 
		_previousButton.setName("butPrec"); 
		_previousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				displayPreviousPanel();
			}
		});
        
		_nextButton = new JButton();
		_nextButton.setMinimumSize(new Dimension(120,22));
		_nextButton.setMaximumSize(new Dimension(120,22));
		_nextButton.setPreferredSize(new Dimension(120,22));
		_nextButton.setText(MessageManager.getMessage("&ComponentConfiguration_NextButton")); 
		_nextButton.setName("butSuiv");
		_nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				displayNextPanel();
			}
		});
		
		// Initialisation du panneau principal qui accueillera les 
		// panneaux a afficher
		_mainPanel = new JPanel();
		_mainPanel.setMinimumSize(new Dimension(500, 450));
		_mainPanel.setMaximumSize(new Dimension(500, 450));
		_mainPanel.setPreferredSize(new Dimension(500, 450));
		_mainPanel.setBorder(BorderFactory.createBevelBorder(
				BevelBorder.LOWERED));
		_mainPanel.setForeground(Color.white);

		// Positionnement des �l�ments
		Box hBox1 = Box.createHorizontalBox();
		hBox1.setMinimumSize(new Dimension(700, 15));
		hBox1.setMaximumSize(new Dimension(700, 15));
		hBox1.add(Box.createHorizontalStrut(190));
		hBox1.add(_title);
		hBox1.add(Box.createHorizontalStrut(10));
		
		Box vBox1 = Box.createVerticalBox();
		vBox1.setMinimumSize(new Dimension(170, 370));
		vBox1.setMaximumSize(new Dimension(170, 370));
		vBox1.add(Box.createVerticalStrut(27));
		for ( int i = 0 ; i < _menuTable.length ; i++ ) {
			vBox1.add(_menuTable[i]);
			vBox1.add(Box.createVerticalStrut(10));
		}
		vBox1.add(Box.createVerticalStrut(110));
		
		Box hBox2 = Box.createHorizontalBox();
		hBox2.setMinimumSize(new Dimension(700, 450));
		hBox2.setMaximumSize(new Dimension(700, 450));
		hBox2.add(Box.createHorizontalStrut(6));
		hBox2.add(vBox1);
		hBox2.add(Box.createHorizontalStrut(8));
		hBox2.add(_mainPanel);
		hBox2.add(Box.createHorizontalStrut(6));
		
		Box hBox3 = Box.createHorizontalBox();
		hBox3.setMinimumSize(new Dimension(700, 22));
		hBox3.setMaximumSize(new Dimension(700, 22));
		hBox3.add(Box.createHorizontalStrut(35));
		hBox3.add(_cancelButton);
		hBox3.add(Box.createHorizontalStrut(280));
		hBox3.add(_previousButton);
		hBox3.add(Box.createHorizontalStrut(10));
		hBox3.add(_nextButton);
		hBox3.add(Box.createHorizontalStrut(10));
		
		Box vBox2 = Box.createVerticalBox();
		vBox2.add(Box.createVerticalStrut(7));
		vBox2.add(hBox1);
		vBox2.add(Box.createVerticalStrut(5));
		vBox2.add(hBox2);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox3);
		vBox2.add(Box.createVerticalStrut(8));
		
		setLayout(new BorderLayout());
		add(vBox2,BorderLayout.CENTER);
		
		trace_methods.endOfMethod();
	}
	
	/*------------------------------------------------------------
     * Nom : freeAndReleaseUnusedMemoryBySettingVariablesNull
     * 
     * Description :
     * Cette m�thode est appel�e lors de la fermeture de l'assistant de 
     * param�trage. Elle est en charge de lib�rer l'espace m�moire des variables
     * allou�es et de les mettre a null.
     * ------------------------------------------------------------*/
	protected void freeAndReleaseUnusedMemoryBySettingVariablesNull() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "freeAndReleaseUnusedMemoryBySettingVariablesNull");
		trace_methods.beginningOfMethod();
		
		// On appelle la m�thode end() sur l'ensemble des panneaux
		if (_panelTable != null) {
			for ( int i = 0 ; i < _panelTable.length ; i++ ) {
				if ( !_panelTable[i].end() ) {
					System.out.println("Erreur lors de la " +
						"suppression des panneaux.");
					System.exit(202);
				}
			}
			_panelTable = null;
		}
		// On appelle la m�thode end() sur l'ensemble du mod�le
		if (_modelTable != null) {
			for (int i = 0 ; i < _modelTable.size() ; i++ ) {
				if (!_modelTable.get(i).end() ) {
					System.out.println("Erreur lors de la " +
						"suppression du mod�le.");
					System.exit(202);
				}
			}
		}
		
		trace_methods.endOfMethod();
	}
	
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
     * Nom : _title
     * 
     * Description :
     * Attribut de type string regroupant le titre de la fen�tre de l'assistant
     * de param�trage destin� � �tre affich�.
     * Ce titre affiche le titre du menu lat�ral gauche correspondant au panneau
     * actuellement affich�.
     * ------------------------------------------------------------*/
	private JLabel _title;
	
	/*------------------------------------------------------------
     * Nom : _cancelButton
     * 
     * Description :
     * Attribut de type javax.swing.JButton. 
     * Lors d'un clic sur ce boutton, le listener associ� � celui-ci fermera 
     * l'assistant et appelera la m�thode : 
     * freeAnsReleaseUnusedMemoryBySettingVariablesNull().
     * ------------------------------------------------------------*/
	private JButton _cancelButton;
	
	/*------------------------------------------------------------
     * Nom : _nextButton
     * 
     * Description :
     * Attribut de type javax.swing.JButton. 
     * Lors d'un clic sur ce boutton, le listener associ� � celui-ci appelera la
     * m�tode displayNextPanel() charg�e d'afficher le panneau suivant.
     * ------------------------------------------------------------*/
	private JButton _nextButton;
	
	/*------------------------------------------------------------
     * Nom : _previousButton
     * 
     * Description :
     * Attribut de type javax.swing.JButton. 
     * Lors d'un clic sur ce boutton, le listener associ� � celui-ci appelera la
     * m�tode displayPreviousPanel() charg�e d'afficher le panneau pr�c�dent.
     * ------------------------------------------------------------*/
	private JButton _previousButton;
	
	/*------------------------------------------------------------
     * Nom : _mainPanel
     * 
     * Description :
     * Attribut de type javax.swing.JPanel. 
     * Cet attribut est le conteneur principal de la fen�tre de param�trage. 
     * C'est dans avec cet attribut que seront affich�s les panneaux sp�cialisant
     * la classe AbstractPanel � l'aide de la m�thode add() de java.awt.Container.
     * ------------------------------------------------------------*/
	private JPanel _mainPanel;
	
	/*------------------------------------------------------------
     * Nom : _numCurrentPanel
     * 
     * Description:
     * Cet attribut conserve une r�f�rence sur le num�ro du panneau actuellement
     * affich� dans la sous f�netre.
     * ------------------------------------------------------------*/
	private int _numCurrentPanel;
	
	/*------------------------------------------------------------
     * Nom: displayPreviousPanel
     * 
     * Description:
     * Cette m�thode est appel�e suite � un clic sur le bouton pr�c�dent : 
     * _previousButton via le listener du bouton.
     * Elle efface le panneau actuellement affich� via la m�thode removeAll() de la
     * classe java.awt.Container. 
     * Pour le panneau pr�c�dent � afficher, elle appelle la m�thode 
     * beforeDisplay(ModelInterface[]) pour que celui-ci se charge depuis le
     * mod�le de donn�es. 
     * Elle ajoute au JPanel principal : _mainPanel ce panneau via la m�thode 
     * add() de la classe java.awt.Container. Puis, elle affiche ce JPanel 
     * via les m�thodes revalidate() de la classe javax.swing.JComponent et 
     * repaint() de la classe java.awt.Component. 
     * 
     * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
     * sinon.
     * ------------------------------------------------------------*/
	private void displayPreviousPanel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "displayPreviousPanel");
		trace_methods.beginningOfMethod();
		
		if (_numCurrentPanel > 0 ) {
			// On lance l'affichage du panneau precedent
			_numCurrentPanel -= 1;
			(_panelTable[_numCurrentPanel]).beforeDisplay(_modelTable);
			repaintPanel(_panelTable[_numCurrentPanel]);
		}
		
		// On met a jour le menu lat�ral gauche
		if ( _numCurrentPanel < (_menuTable.length - 1) ) {
			_menuTable[_numCurrentPanel + 1].setFont(new java.awt.Font("Arial", Font.PLAIN, 12));
			_menuTable[_numCurrentPanel + 1].setEnabled(false);
			_menuTable[_numCurrentPanel].setFont(new java.awt.Font("Arial", Font.BOLD, 12));
			_title.setText(_menuTable[_numCurrentPanel].getText());	
		}
		
		// On bloque le bouton pr�c�dent sur la premi�re page
		if ( _numCurrentPanel == 0 )
			_previousButton.setEnabled(false);
		
		// On modifie le boutton "Terminer" en "Suivant" si on revient sur 
		// l'avant dernier panneau
		if ( _nextButton.getText().equals("Terminer") )
			_nextButton.setText("Suivant");
		
		trace_methods.endOfMethod();
	}
	
	/*------------------------------------------------------------
     * Nom: displayNextPanel
     * 
     * Description:
     * Cette m�thode est appel�e suite � un clic sur le bouton suivant : 
     * _nextButton via le listener du bouton.
     * Tout d'abord, elle v�rifie que le panneau a �t� correctement remplit � 
     * l'aide de la valeur de retour de la m�thode beforeHide() de PanelInterface.
     * Puis si tout ce passe bien, elle appelle la m�thode repaintPanel().
     * Si il n'y a plus de panneau a afficher, elle demande la sauvegarde du mod�le 
     * de donn�es en base via la m�thode saveModel() de AbstractWindow.
     * 
     * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
     * sinon.
     * ------------------------------------------------------------*/
	private void displayNextPanel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "displayNextPanel");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		
		if (_panelTable != null) {
			if (_numCurrentPanel < (_panelTable.length - 1) ) {
				// Si le panneau est correctement renseigne
				if ( (_panelTable[_numCurrentPanel]).beforeHide() ) {
					//	On demande la sauvegarde du panneau courant
					_modelTable = 
						(_panelTable[_numCurrentPanel]).update(_modelTable);
					// On passe au panneau suivant	
					_numCurrentPanel += 1;
					(_panelTable[_numCurrentPanel]).beforeDisplay(_modelTable);
					repaintPanel(_panelTable[_numCurrentPanel]);
				}
				
				// On met a jour le menu lat�ral gauche
				if (_menuTable.length > _numCurrentPanel) {
					_menuTable[_numCurrentPanel - 1].setFont(new java.awt.Font("Arial", Font.PLAIN, 12));
					_menuTable[_numCurrentPanel].setEnabled(true);
					_menuTable[_numCurrentPanel].setFont(new java.awt.Font("Arial", Font.BOLD, 12));
					_title.setText(_menuTable[_numCurrentPanel].getText());	
				}
				
				// On r�active le bouton Pr�c�dent si on �tait sur la 
				// premi�re page
				if ( !_previousButton.isEnabled() )
					_previousButton.setEnabled(true);
				
				// On modifie le boutton "Suivant" en "Terminer" si on arrive
				// sur le dernier panneau
				if ( _numCurrentPanel == (_panelTable.length -1) )
					_nextButton.setText("Terminer");
			}
			else if ( _numCurrentPanel == (_panelTable.length - 1) )
    		{
    			// Apres la page recapitulative, on enregistre le modele
    			// et on quitte l'application
				// En cas d'erreur, on retourne le code d'erreur 202
    			try 
    			{
    				saveModel();
    			}
    			catch (InnerException exception)
    			{
    				trace_errors.writeTrace("Erreur lors de la " +
					"sauvegarde en base de la saisie : " + exception);
    			}
    			close();
    		}
		}
		else
			// Il n'y avait pas de panneau a afficher, on sort sans rien faire
			close();
		
		trace_methods.endOfMethod();
	}
		
	/*------------------------------------------------------------
     * Nom : repaintPanel()
     * 
     * Description : Cette m�thode efface le panneau actuellement affich� via la 
     * m�thode removeAll() de la classe java.awt.Container. 
     * Pour le panneau suivant � afficher, elle apelle la m�thode 
     * beforeDisplay(ModelInterface[]) pour que celui-ci se charge depuis le 
     * mod�le. 
     * Elle ajoute au JPanel principal : _mainPanel ce panneau via la m�thode 
     * add() de la classe java.awt.Container. Puis, elle affiche ce JPanel 
     * via les m�thodes revalidate() de la classe javax.swing.JComponent et 
     * repaint() de la classe java.awt.Component. 
     * ------------------------------------------------------------*/
	private void repaintPanel(PanelInterface panel) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "repaintPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("panel=" + panel);
		
		// On efface le panneau actuellement affich�
		_mainPanel.removeAll();
		// On ajoute au panneau principal le panneau a afficher
		_mainPanel.add((JPanel) panel);
		// On raffraichit le tout
		_mainPanel.revalidate();
		_mainPanel.repaint();
		
		trace_methods.endOfMethod();
	}
	
	/*------------------------------------------------------------
     * Nom : onClickCancelButton()
     * 
     * Description : Cette m�thode est appel� lorsque l'utilisateur � choisit
     * de mettre fin � l'assistant de configuration en cliquant sur le boutton
     * 'Annuler' en bas � gauche de celui-ci.
     * Cette m�thode demande confirmation � l'utilisateur avant de quitter 
     * l'assistant. 
     * ------------------------------------------------------------*/
	private void onClickCancelButton() {
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AbstractWindow", "onClickCancelButton");
		trace_methods.beginningOfMethod();
		
		String exitText = MessageManager.getMessage(
				"&ComponentConfiguration_ExitSentence");
		String exitTitle = MessageManager.getMessage(
				"&ComponentConfiguration_ExitTitle");
		
		int rep = JOptionPane.showConfirmDialog(null, exitText, exitTitle, 
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (rep == 0) {
			dispose();
			close();
		}
		trace_methods.endOfMethod();
	}
}
