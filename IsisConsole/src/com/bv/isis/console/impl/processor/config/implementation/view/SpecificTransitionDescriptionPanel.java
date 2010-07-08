/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
 * ------------------------------------------------------------
 *
 * $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/SpecificTransitionDescriptionPanel.java,v $
 * $Revision: 1.26 $
 *
 * ------------------------------------------------------------
 * DESCRIPTION: Panneau de saisie des informations s�pcifiques d'une transition
 * DATE:        13/06/2008
 * AUTEUR:      F. Cossard - H. Doghmi
 * PROJET:      I-SIS
 * GROUPE:      processor.impl.config
 * ------------------------------------------------------------
 * CONTROLE DES MODIFICATIONS
 *
 * R�vision 1.9  2008/07/11 fcd
 * Modification des champs de saisie pour l'interval de controle
 * et de d�lai maximum pour n'accepter que les nombres
 *
 * Revision 1.5  2008/07/01 14:00:00  fcd
 * Mise en commentaire de la partie concernant le bouton d'exploration
 * de la commande.
 *
 * Revision 1.4  2008/06/27 11:30:00  fcd
 * Ajout des traces, modification du contructeur
 * Ajout d'un attribut r�f�rencant le panneau appelant
 *
 * ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.implementation.view;

//
// Imports syst�me
//
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentStates;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentTransition;

/*----------------------------------------------------------
 * Nom: SpecificTransitionDescriptionPanel
 * 
 * Description: 
 * Cette classe impl�mente la panneau qui permet de d�crire 
 * un ensemble de transitions vers un �tat pr�c�demment d�fini.
 * ----------------------------------------------------------*/
public class SpecificTransitionDescriptionPanel extends AbstractPanel {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: SpecificDescriptionDescriptionPanel
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * Elle appelle la m�thode makePanel() charg�e de la cr�ation des objets
	 * graphiques a afficher dans le panneau.		
	 * ----------------------------------------------------------*/
	public SpecificTransitionDescriptionPanel(WindowInterface window,
			GenericTransitionDescriptionPanel parentPanel) {
		super();

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel",
				"SpecificTransitionDescriptionPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("window=" + window);
		trace_arguments.writeTrace("parentPanel=" + parentPanel);

		_window = window;
		_parentPanel = parentPanel;
		makePanel();

		trace_methods.endOfMethod();

	}

	/*----------------------------------------------------------
	 * Nom: beforeDisplay
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle est appel�e par la classe AbstractWindow lorsque le panneau doit �tre
	 * affich�. A partir du mod�le de donn�es pass� en param�tre, elle se charge
	 * de pr�-renseigner les champs du panneau.
	 * 
	 * Param�tres :
	 *  - tabModels : Le tableau de ModelInterface.
	 * ----------------------------------------------------------*/
	public void beforeDisplay(ArrayList<ModelInterface> modele) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "beforeDisplay");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("modele=" + modele);

		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: loadTransitionArray
	 * 
	 * Description: 
	 * Cette m�thode est appel�e par le panneau GenericTransitionGroup
	 * lorsqu'il demande l'affichage des informations sp�cifiques d'une 
	 * transition. Elle se charge de charger la JTable � partir du 
	 * tableabu de donn�es pass� en param�tre.
	 * 
	 * Param�tres :
	 *  - transitionArray : Le tableau de ComponentTransition.
	 * ----------------------------------------------------------*/
	public void loadTransitionArray(
			ArrayList<ComponentTransition> transitionArray) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "loadTransitionArray");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("transitionArray=" + transitionArray);

		_tableModel = new TransitionTableModel();

		if (transitionArray != null) {

			for (int index = 0; index < transitionArray.size(); index++) {
				ComponentTransition c = transitionArray.get(index);
				if (c.getFlag() != 'S')
					_tableModel.addElement(c);
				else
					_tableModel.addDeleteElement(c);
			}
		}
		_specificTransitionTable.setModel(_tableModel);
		_startStateBox.removeAllItems();

		ArrayList<ModelInterface> model = _window.getModel();
		for (int index = 0; index < model.size(); index++) {
			if (model.get(index) instanceof ComponentStates) {
				ComponentStates cs = (ComponentStates) model.get(index);
				_startStateBox.addItem((String) cs.getStateName());
			}
		}
		// Une transition doit au moins �tre sp�cifi�e.
		if (_tableModel.getRowCount() == 0)
			_validateButton.setEnabled(false);

		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: afterDisplay
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public boolean afterDisplay() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "afterDisplay");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	/*------------------------------------------------------------
	 * Nom: beforeHide
	 * 
	 * Description: 
	 * Cette m�thode d�finit le comportement du panneau avant d'�tre cach�.
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle est en charge de v�rifier la validit� et la conformit� des 
	 * informations saisis par l'utilisateur. Si il y a une erreur, elle retourne
	 * faux sinon vrai.
	 * 
	 * Retourne : vrai (true) si tout est conforme, faux (false) sinon.
	 * -----------------------------------------------------------*/
	public boolean beforeHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "beforeHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	/*----------------------------------------------------------
	 * Nom: beforeHide
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Cette m�thode d�finit le comportement du panneau apr�s avoir �t� cach�.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public boolean afterHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "afterHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	/*----------------------------------------------------------
	 * Nom: end
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Cette m�thode est appel�e lors de la destruction de l'assistant. Elle est 
	 * utiliser pour lib�rer l'espace m�moire utilis� par les variables des
	 * classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public boolean end() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "end");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	/*----------------------------------------------------------
	 * Nom: update
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Dans le cas de ce panneau, elle ne fait aucun traitement de particulier,
	 * car elle ne fait pas � proprement parl� de la cin�matique de 
	 * l'application.
	 * 
	 * Param�tre : 
	 *  - tabModels : Le mod�le de donn�es avant modification.
	 * 
	 * Retourne : Le mod�le de donn�es inchang�.
	 * ----------------------------------------------------------*/
	public ArrayList<ModelInterface> update(ArrayList<ModelInterface> tabModels) {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "update");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tabModels=" + tabModels);
		
		trace_methods.endOfMethod();
		return tabModels;
	}

	/*------------------------------------------------------------
	 * Nom : getTransition
	 * 
	 * Description :
	 * Cette m�thode retourne le tableau de donn�es contenu dans la
	 * JTable.
	 * Cette m�thode est appel�e lorsque le panneau GenericTransitionGroup
	 * souhaite mettre � jour son propre tableau de ComponentTransition.
	 * ------------------------------------------------------------*/
	public ArrayList<ComponentTransition> getTransition() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "getTransition");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		ArrayList<ComponentTransition> res = _tableModel.getDeleteDatas();
		res.addAll(_tableModel.getDatas());

		return res;
	}

	/*----------------------------------------------------------
	 * Nom: setEnterCallback
	 *
	 * Description:
	 * Cette m�thode permet de d�finir la m�thode de callback en
	 * cas d'appui sur la touche "Entr�e" sur un objet JComponent
	 * pass� en argument.
	 * La m�thode de callback est la m�thode validateInput().
	 *
	 * Arguments:
	 *  - component: Un objet JComponent sur lequel le callback
	 *    doit �tre d�fini.
	 * ----------------------------------------------------------*/
	protected void setEnterCallback(JComponent component) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"BaseDialog", "setEnterCallback");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("component=" + component);
		// On v�rifie la validit� de l'argument
		if (component == null) {
			trace_methods.endOfMethod();
			return;
		}
		// On positionne le callback sur Entree
		component.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					onClickButtonApply();
				} else
					fieldsHaveChanged();
			}
		});
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _parentPanel
	 * 
	 * Description :
	 * Cette attribut maintient une r�f�rence sur le panneau g�n�rique
	 * des transitions ayant appel� ce panneau.
	 * ------------------------------------------------------------*/
	private GenericTransitionDescriptionPanel _parentPanel;

	/*------------------------------------------------------------
	 * Nom : _specificTransitionTable
	 * 
	 * Description :
	 * Cette attribut repr�sente un tableau r�capitulant l'ensemble
	 * des transitions d�finis par l'utilisateur.
	 * ------------------------------------------------------------*/
	private JTable _specificTransitionTable;

	/*----------------------------------------------------------
	 * Nom : _startStateBox
	 * 
	 * Description :
	 * Cette attribut d�finit une liste d�roulante pour le choix
	 * de l'�tat de d�part dans le sous panneau de saisie.
	 * ----------------------------------------------------------*/
	private JComboBox _startStateBox;

	/*------------------------------------------------------------
	 * Nom : _commandTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour la commande
	 * d'une action d'exploitation dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _commandTextField;

	/*----------------------------------------------------------
	 * Nom : _commandTypeBox
	 * 
	 * Description :
	 * Cette attribut d�finit une liste d�roulante pour le choix
	 * du type de commande dans le sous panneau de saisie.
	 * ----------------------------------------------------------*/
	private JTextField _commandTypeTextField;

	/*----------------------------------------------------------
	 * Nom : _checkIntervalTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour l'interval
	 * de controle d'un d�mon dans le sous panneau de saisie.
	 * ----------------------------------------------------------*/
	private JFormattedTextField _checkIntervalTextField;

	/*----------------------------------------------------------
	 * Nom : _checkTimeOutTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour le Time Out
	 * de controle d'un d�mon dans le sous panneau de saisie.
	 * ----------------------------------------------------------*/
	private JFormattedTextField _checkTimeOutTextField;

	/*------------------------------------------------------------
	 * Nom : _commandButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * afficher une fen�tre de recherche afin de r�cup�rer le chemin
	 * vers un script.
	 * ------------------------------------------------------------*/
	private JButton _commandButton;

	/*------------------------------------------------------------
	 * Nom : _executiveUserTextField
	 * 
	 * Description :
	 * ------------------------------------------------------------*/
	private JButton _newButton;

	/*------------------------------------------------------------
	 * Nom : _removeButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * supprimer la ligne s�lectionner dans le tableau .
	 * ------------------------------------------------------------*/
	private JButton _removeButton;

	/*------------------------------------------------------------
	 * Nom : _applyButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * enregistrer les informations saisies dans le sous panneau de 
	 * saisie, dans le tableau des actions.
	 * ------------------------------------------------------------*/
	private JButton _applyButton;

	/*----------------------------------------------------------
	 * Nom : _cancelButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * annuler la saisie des informations sp�cifiques li�es � 
	 * une transition vers un �tat d'arriv�e.
	 * ----------------------------------------------------------*/
	private JButton _cancelButton;

	/*----------------------------------------------------------
	 * Nom : _validateButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * valider la saisie des informations sp�cifiques li�es � 
	 * une transition vers un �tat d'arriv�e.
	 * ----------------------------------------------------------*/
	private JButton _validateButton;

	/*------------------------------------------------------------
	 * Nom : TransitionTableModel
	 * 
	 * Description :
	 * Cette classe repr�sente le modele associ� � la JTable.
	 * Elle d�rive de AbstractTableModel.
	 * Elle se caract�rise de 2 tableaux:
	 *  - un pour les titres des diverses colonnes
	 *  - un pour les donn�es de chaque ligne.
	 * Chaque ligne est une instance de ComponentTransition.
	 * ------------------------------------------------------------*/
	private class TransitionTableModel extends AbstractTableModel {

		/*------------------------------------------------------------
		 * Nom : _titles
		 * 
		 * Description :
		 * Ce tableau de String repr�sente les titres des colonnes de la
		 * JTable.
		 * ------------------------------------------------------------*/
		private String[] _titles;

		/*------------------------------------------------------------
		 * Nom : _datas
		 * 
		 * Description :
		 * Cet attribut est un tableau de ComponentTransition.
		 * Chaque �l�ment du tableau repr�sente une ligne de la JTable
		 * et contient les informations d'une action.
		 * ------------------------------------------------------------*/
		private ArrayList<ComponentTransition> _datas;

		private ArrayList<ComponentTransition> _deleteDatas;

		/*------------------------------------------------------------
		 * Nom : TransitionTableModel
		 * 
		 * Description :
		 * Cette m�thode est le contructeur de la classe.
		 * Son r�le est de cr�er les deux tableaux de titres et de donn�es.
		 * Pour le tableau des titres, elle le remplira � partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/
		public TransitionTableModel() {

			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "TransitionTableModel");
			trace_methods.beginningOfMethod();

			// Cr�ation du tableau des titres
			_titles = new String[5];
			// La premi�re colonne repr�sentera l'�tat de d�part de la
			// transition
			_titles[0] = MessageManager
					.getMessage("&ComponentConfiguration_StartState");
			// La deuxi�me colonne repr�sentera la commande associ�e
			// � la transition
			_titles[1] = MessageManager
					.getMessage("&ComponentConfiguration_Command");
			// La troisi�me colonne repr�sentera le type de la commande
			// associ�e � la transition
			_titles[2] = MessageManager
					.getMessage("&ComponentConfiguration_CommandType");
			// La quatri�me colonne repr�sentera l'interval de contr�le
			// associ�e � la transition
			_titles[3] = MessageManager
					.getMessage("&ComponentConfiguration_CheckInterval");
			// La cinqui�me colonne repr�sentera le time out
			// associ�e � la transition
			_titles[4] = MessageManager
					.getMessage("&ComponentConfiguration_CheckTimeOut");
			// La derni�re colonne repr�sentera l'Executive User de la
			// transition
			/*
			 * _titles[5] =
			 * MessageManager.getMessage("&ComponentConfiguration_ExecutiveUser");
			 */

			// Cr�ation du tableau de donn�es
			_datas = new ArrayList<ComponentTransition>();
			_deleteDatas = new ArrayList<ComponentTransition>();

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getColumnCount
		 * 
		 * Description : 
		 * Cette m�thode retourne le nombre de colonnes de la JTable. 
		 * Ce nombre est donn� par la dimension du tableaux des titres.
		 * 
		 * Retourne : un entier : le nombre de colonne.
		 * ------------------------------------------------------------*/
		public int getColumnCount() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "getColumnCount");
			trace_methods.beginningOfMethod();

			if (_titles != null) {
				trace_methods.endOfMethod();
				return _titles.length;
			} else {
				trace_methods.endOfMethod();
				return 0;
			}
		}

		/*------------------------------------------------------------
		 * Nom : getRowCount
		 * 
		 * Description : 
		 * Cette m�thode retourne le nombre de lignes de la JTable. 
		 * Ce nombre est donn� par la dimension du tableaux des donn�es.
		 * 
		 * Retourne : un entier : le nombre de lignes.
		 * ------------------------------------------------------------*/
		public int getRowCount() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "getRowCount");
			trace_methods.beginningOfMethod();

			if (_datas != null) {
				trace_methods.endOfMethod();
				return _datas.size();
			} else {
				trace_methods.endOfMethod();
				return 0;
			}
		}

		/*------------------------------------------------------------
		 * Nom : getDatas
		 * 
		 * Description : 
		 * Cette m�thode retourne le tableau de ComponentTransition contenant
		 * l'ensemble des informations saisies par l'utilisateur.
		 * 
		 * Retourne : Un tableau de ComponentTransition
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentTransition> getDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "getDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();

			return _datas;
		}

		/*------------------------------------------------------------
		 * Nom : findRow
		 * 
		 * Description : 
		 * Cette m�thode recherche la pr�sence d'une transition dans la JTable
		 * � partir du nom de l'�tat de d�part pass� en param�tre. Elle retourne
		 * le num�ro de la ligne correspondant � la transition si elle existe,
		 * -1 sinon.
		 * 
		 * Argument :
		 *  - startStateName : une cha�ne de caract�res corespondant au nom
		 *    de l'�tat de la transition recherch�e
		 *    
		 * Retourne : un entier, le num�ro de ligne de la transition, -1 si 
		 * elle n'existe pas.
		 * ------------------------------------------------------------*/
		public int findRow(String startStateName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("startStateName=" + startStateName);

			if (_datas != null) {
				for (int index = 0; index < _datas.size(); index++) {
					if (_datas.get(index).getStartState().getStateName()
							.equals(startStateName)) {
						trace_methods.endOfMethod();
						return index;
					}
				}
			}
			trace_methods.endOfMethod();
			return -1;
		}

		/*------------------------------------------------------------
		 * Nom : getValueAt
		 * 
		 * Description : 
		 * Cette m�thode retourne la valeur de la cellule de la JTable
		 * pour la position (ligne, colonne) donn�e.
		 * Le num�ro de ligne correspond � une transition particuli�re.
		 * Le num�ro de colonne indique le champ : 
		 * 	- 0 pour le nom de l'�tat de d�part
		 *  - 1 pour la commande
		 *  - 2 pour le type de la commande
		 *  - 3 pour l'interval de contr�le
		 *  - 4 pour le Time Out
		 * 
		 * Arguments :
		 *  - line : le num�ro de ligne
		 *  - column : la num�ro de la colonne
		 *    
		 * Retourne : l'�lement � la position souhait�, null si rien 
		 * n'est trouv�.
		 * ------------------------------------------------------------*/
		public Object getValueAt(int line, int column) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "getValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				ComponentTransition ct = _datas.get(line);
				if (ct != null) {
					if (column == 0) {
						trace_methods.endOfMethod();
						return ct.getStartState().getStateName();
					} else if (column == 1) {
						if (ct.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return ct.getCommand();
						} else {
							trace_methods.endOfMethod();
							return ct.getNewCommand();
						}
					} else if (column == 2) {
						if (ct.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return ct.getCommandType();
						} else {
							trace_methods.endOfMethod();
							return ct.getNewCommandType();
						}
					} else if (column == 3) {
						if (ct.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return ct.getCheckInterval();
						} else {
							trace_methods.endOfMethod();
							return ct.getNewCheckInterval();
						}
					} else if (column == 4) {
						if (ct.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return ct.getCheckTimeOut();
						} else {
							trace_methods.endOfMethod();
							return ct.getNewCheckTimeOut();
						}
					}
					/*
					 * else { trace_methods.endOfMethod(); return
					 * ct.getExecutiveUser(); }
					 */
				}
			}
			trace_methods.endOfMethod();
			return null;
		}

		/*------------------------------------------------------------
		 * Nom : setValueAt
		 * 
		 * Description : 
		 * Cette m�thode affecte une nouvelle valeur � la cellule de 
		 * la JTable pour la position (ligne, colonne) donn�e.
		 * Le num�ro de ligne correspond � une transition particuli�re.
		 * Le num�ro de colonne indique le champ : 
		 *  - 1 pour la commande
		 *  - 2 pour le type de la commande
		 *  - 3 pour l'interval de contr�le
		 *  - 4 pour le Time Out
		 *  - 5 pour l'Executive User
		 * La colonne 0 ne peut �tre modifi�e.
		 * 
		 * Arguments :
		 *  - obj : la nouvelle valeur de la cellule
		 *  - line : le num�ro de ligne
		 *  - column : la num�ro de la colonne
		 * ------------------------------------------------------------*/
		public void setValueAt(Object obj, int line, int column) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "setValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("obj=" + obj);
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column" + column);

			if (_datas != null && _datas.size() > line) {
				ComponentTransition ct = _datas.get(line);
				if (ct != null) {
					if (column == 1)
						ct.setCommand(obj.toString());
					else if (column == 2)
						ct.setCommandType(obj.toString());
					else if (column == 3)
						ct.setCheckInterval(Integer.parseInt(obj.toString()));
					else if (column == 4)
						ct.setCheckTimeOut(Integer.parseInt(obj.toString()));
					/*
					 * else if (column == 5)
					 * ct.setExecutiveUser(obj.toString());
					 */
					else
						DialogManager.displayDialog("Information", 
							MessageManager.getMessage(
							"&ERR_ErrorWhileSavingData"), null, null);
				}
			}
			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getColumnName
		 * 
		 * Description : 
		 * Cette m�thode retourne le nom d'une colonne de la JTable. 
		 * 
		 * Argument :
		 * 
		 * Retourne : une chaine de caract�re : le nom de la colonne.
		 * ------------------------------------------------------------*/
		public String getColumnName(int column) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "getColumnName");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("column=" + column);

			if (_titles != null && _titles.length > column) {
				trace_methods.endOfMethod();
				return _titles[column];
			}
			trace_methods.endOfMethod();
			return "";
		}

		/*------------------------------------------------------------
		 * Nom : addElement
		 * 
		 * Description : 
		 * Cette m�thode ajoute un nouvel �l�ment (une transition) au 
		 * tableau de donn�es. 
		 * 
		 * Argument : 
		 *  - element : L'�l�ment � ajouter
		 * ------------------------------------------------------------*/
		public void addElement(ComponentTransition element) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "addElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("element=" + element);

			if (_datas != null) {
				_datas.add(element);
				super.fireTableRowsInserted(_datas.size(), _datas.size());
			}

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : removeElement
		 * 
		 * Description : 
		 * Cette m�thode supprime un �l�ment (une transition) du tableau 
		 * de donn�es. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � supprimer
		 * ------------------------------------------------------------*/
		public void removeElement(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "removeElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);

			if (_datas != null && line < _datas.size()) {
				_datas.remove(line);
				super.fireTableRowsDeleted(line, line);
			}

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : removeAll
		 * 
		 * Description : 
		 * Cette m�thode supprime tous les �lements (les transitions) du tableau 
		 * de donn�es. 
		 * 
		 * ------------------------------------------------------------*/
		public void removeAll() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "removeAll");

			for (int i = 0; i < _datas.size(); i++)
				_datas.remove(i);
			for (int i = 0; i < _deleteDatas.size(); i++)
				_deleteDatas.remove(i);

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getElementAt
		 * 
		 * Description : 
		 * Cette m�thode retourne le ComponentTransition de la ligne 
		 * demand�e. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � r�cup�rer
		 *  
		 * Retourne : Le ComponentTransition de la ligne s�lectionn�e.
		 * ------------------------------------------------------------*/
		public ComponentTransition getElementAt(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "getElementAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);

			if (_datas != null && line < _datas.size()) {
				trace_methods.endOfMethod();
				return _datas.get(line);
			}
			trace_methods.endOfMethod();
			return null;
		}

		/*------------------------------------------------------------
		 * Nom : addDeleteElement
		 * 
		 * Description : 
		 * Cette m�thode ajoute au tableau des �l�ments � supprimer de la
		 * base, le ComponentTransition pass� en param�tre. 
		 * 
		 * Argument : 
		 *  - transi : Le ComponentTransition qu'il faudra supprimer 
		 *    de la base de donn�es.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(ComponentTransition transi) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "addDeleteElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("transi=" + transi);

			if (_deleteDatas != null)
				_deleteDatas.add(transi);
			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : removeDeleletElement
		 * 
		 * Description : 
		 * Cette m�thode retire au tableau des �l�ments � supprimer de la
		 * base, le ComponentTransition d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentTransition qu'il faut retirer 
		 *    du tableau.
		 * ------------------------------------------------------------*/
		public void removeDeleletElement(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "removeDeleletElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("index=" + index);

			if (_deleteDatas != null && index < _deleteDatas.size())
				_deleteDatas.remove(index);

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getDeleteElementAt
		 * 
		 * Description : 
		 * Cette m�thode retourne du tableau des �l�ments � supprimer de la
		 * base, le ComponentTransition d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentTransition qu'il faut r�cup�rer
		 *    dans le tableau.
		 * 
		 * Retourne : Le ComponentTransition supprim� de la ligne
		 * souhait�e.
		 * ------------------------------------------------------------*/
		public ComponentTransition getDeleteElementAt(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "getDeleteElementAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("index=" + index);

			if (_deleteDatas != null && index < _deleteDatas.size()) {
				trace_methods.endOfMethod();
				return _deleteDatas.get(index);
			}
			trace_methods.endOfMethod();
			return null;
		}

		/*------------------------------------------------------------
		 * Nom : getDeleteDatas
		 * 
		 * Description : 
		 * Cette m�thode retourne l'ensemble du tableau des 
		 * ComponentTransition � supprimer de la base de donn�es. 
		 * 
		 * Retourne : Le tableau des ComponentTransition � supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentTransition> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "getDeleteElementAt");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _deleteDatas;
		}
	}

	/*------------------------------------------------------------
	 * Nom : _tableModel
	 * 
	 * Description :
	 * Cette attribut maintient une r�f�rence vers le modele associ� 
	 * � la JTable.
	 * ------------------------------------------------------------*/
	private TransitionTableModel _tableModel;

	/*----------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette m�thode est appel� lors de la construction du panneau.
	 * Son r�le est d'instancier les composants graphiques du panneau 
	 * et de les positionner.
	 * ----------------------------------------------------------*/
	private void makePanel() {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "makePanel");
		trace_methods.beginningOfMethod();

		// On pr�sente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(475, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(475, 20));
		firstDescriptionSentence
				.setText(MessageManager
						.getMessage("&ComponentConfiguration_FirstDescriptionSentenceSpecificTransition"));

		_tableModel = new TransitionTableModel();
		_specificTransitionTable = new JTable(_tableModel);
		_specificTransitionTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_specificTransitionTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {

						onSelectionElement(event);

					}
				});

		// On int�gre un d�filement dans le tableau des transitions sp�cifiques
		JScrollPane scrollPaneSpecificTransition = new JScrollPane();
		scrollPaneSpecificTransition.setViewportView(_specificTransitionTable);
		scrollPaneSpecificTransition.setMinimumSize(new Dimension(475, 123));
		scrollPaneSpecificTransition.setMaximumSize(new Dimension(475, 123));

		JPanel panel1 = new JPanel();
		panel1.setMinimumSize(new Dimension(490, 440));
		panel1.setMaximumSize(new Dimension(490, 440));
		panel1.setPreferredSize(new Dimension(490, 440));
		panel1.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.NONE;
		c1.insets = new Insets(5, 10, 5, 10);

		c1.weightx = c1.weighty = 0.0;
		c1.gridx = 0;
		c1.gridy = 1;
		c1.gridwidth = 1;
		c1.gridheight = 1;
		panel1.add(firstDescriptionSentence, c1);

		c1.gridx = 0;
		c1.gridy = 2;
		c1.gridwidth = 1;
		c1.gridheight = 1;
		panel1.add(scrollPaneSpecificTransition, c1);

		// on d�finit un label pour le champs "Etat de d�part"
		JLabel startStateLabel = new JLabel();
		startStateLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_StartState")
				+ " :   *");

		// on d�finit un label pour le champs "Commande"
		JLabel commandLabel = new JLabel();
		commandLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Command")
				+ " :   *");

		// on d�finit un label pour le champs "Type de commande"
		JLabel commandTypeLabel = new JLabel();
		commandTypeLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_CommandType")
				+ " :   *");

		// on d�finit un label pour le champs "Intervalle de contr�le"
		JLabel checkIntervalLabel = new JLabel();
		checkIntervalLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_CheckInterval")
				+ " : ");

		// on d�finit un label pour le champs "D�lai maximum"
		JLabel checkTimeOutLabel = new JLabel();
		checkTimeOutLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_CheckTimeOut")
				+ " : ");

		// On d�finit un label pour indiquer que l'asterisque en fin des noms de
		// champs sont obligatoires pour la saisie.
		JLabel obligatoryFieldLabel = new JLabel();
		obligatoryFieldLabel.setMinimumSize(new Dimension(150, 20));
		obligatoryFieldLabel.setMaximumSize(new Dimension(150, 20));
		obligatoryFieldLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_ObligatoryFields"));

		int maxSize = startStateLabel.getPreferredSize().width;
		if (commandLabel.getPreferredSize().width > maxSize)
			maxSize = commandLabel.getPreferredSize().width;
		if (commandTypeLabel.getPreferredSize().width > maxSize)
			maxSize = commandTypeLabel.getPreferredSize().width;
		if (checkIntervalLabel.getPreferredSize().width > maxSize)
			maxSize = checkIntervalLabel.getPreferredSize().width;
		if (checkTimeOutLabel.getPreferredSize().width > maxSize)
			maxSize = checkTimeOutLabel.getPreferredSize().width;

		startStateLabel.setPreferredSize(new Dimension(maxSize, 20));
		startStateLabel.setMinimumSize(new Dimension(maxSize, 20));
		startStateLabel.setMaximumSize(new Dimension(maxSize, 20));

		commandLabel.setPreferredSize(new Dimension(maxSize, 20));
		commandLabel.setMinimumSize(new Dimension(maxSize, 20));
		commandLabel.setMaximumSize(new Dimension(maxSize, 20));

		commandTypeLabel.setPreferredSize(new Dimension(maxSize, 20));
		commandTypeLabel.setMinimumSize(new Dimension(maxSize, 20));
		commandTypeLabel.setMaximumSize(new Dimension(maxSize, 20));

		checkIntervalLabel.setPreferredSize(new Dimension(maxSize, 20));
		checkIntervalLabel.setMinimumSize(new Dimension(maxSize, 20));
		checkIntervalLabel.setMaximumSize(new Dimension(maxSize, 20));

		checkTimeOutLabel.setPreferredSize(new Dimension(maxSize, 20));
		checkTimeOutLabel.setMinimumSize(new Dimension(maxSize, 20));
		checkTimeOutLabel.setMaximumSize(new Dimension(maxSize, 20));

		_startStateBox = new JComboBox();
		setEnterCallback(_startStateBox);
		_startStateBox.setPreferredSize(new Dimension(455 - (startStateLabel
				.getPreferredSize().width), 20));
		_startStateBox.setMinimumSize(new Dimension(455 - (startStateLabel
				.getMinimumSize().width), 20));
		_startStateBox.setMaximumSize(new Dimension(455 - (startStateLabel
				.getMaximumSize().width), 20));

		_checkIntervalTextField = new JFormattedTextField(NumberFormat
				.getInstance());
		setEnterCallback(_checkIntervalTextField);
		_checkIntervalTextField.setPreferredSize(new Dimension(
				455 - (checkIntervalLabel.getPreferredSize().width), 20));
		_checkIntervalTextField.setMinimumSize(new Dimension(
				455 - (checkIntervalLabel.getMinimumSize().width), 20));
		_checkIntervalTextField.setMaximumSize(new Dimension(
				455 - (checkIntervalLabel.getMaximumSize().width), 20));

		_commandButton = new JButton();
		_commandButton.setMinimumSize(new Dimension(30, 22));
		_commandButton.setMaximumSize(new Dimension(30, 22));
		_commandButton.setPreferredSize(new Dimension(30, 22));
		_commandButton.setText("...");
		_commandButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// Ouverture d'une fen�tre de recherche d'un fichier
				JFileChooser jfc = new JFileChooser();
				int returnVal = jfc.showOpenDialog(null);

				// Clic sur le bouton Ouvrir
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = jfc.getSelectedFile();
					_commandTextField.setText(file.getAbsolutePath());
				}
			}
		});
		_commandTextField = new JTextField();
		setEnterCallback(_commandTextField);
		_commandTextField.setPreferredSize(new Dimension(455 - (commandLabel
				.getPreferredSize().width), 20));
		_commandTextField.setMinimumSize(new Dimension(455 - (commandLabel
				.getMinimumSize().width), 20));
		_commandTextField.setMaximumSize(new Dimension(455 - (commandLabel
				.getMaximumSize().width), 20));

		_commandTypeTextField = new JTextField();
		setEnterCallback(_commandTypeTextField);
		_commandTypeTextField.setPreferredSize(new Dimension(
				455 - (commandTypeLabel.getPreferredSize().width), 20));
		_commandTypeTextField.setMinimumSize(new Dimension(
				455 - (commandTypeLabel.getMinimumSize().width), 20));
		_commandTypeTextField.setMaximumSize(new Dimension(
				455 - (commandTypeLabel.getMaximumSize().width), 20));

		_checkTimeOutTextField = new JFormattedTextField(NumberFormat
				.getInstance());
		setEnterCallback(_checkTimeOutTextField);
		_checkTimeOutTextField.setPreferredSize(new Dimension(
				455 - (checkTimeOutLabel.getPreferredSize().width), 20));
		_checkTimeOutTextField.setMinimumSize(new Dimension(
				455 - (checkTimeOutLabel.getMinimumSize().width), 20));
		_checkTimeOutTextField.setMaximumSize(new Dimension(
				455 - (checkTimeOutLabel.getMaximumSize().width), 20));

		_newButton = new JButton();
		_newButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_NewButton"));
		_newButton.setMinimumSize(new Dimension(120, 21));
		_newButton.setMaximumSize(new Dimension(120, 21));
		_newButton.setPreferredSize(new Dimension(120, 21));
		_newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickNewButton();
			}
		});

		_removeButton = new JButton();
		_removeButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_RemoveButton"));
		_removeButton.setMinimumSize(new Dimension(120, 21));
		_removeButton.setMaximumSize(new Dimension(120, 21));
		_removeButton.setPreferredSize(new Dimension(120, 21));
		_removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickRemoveButton();
			}
		});

		_applyButton = new JButton();
		_applyButton.setEnabled(false);
		_applyButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_ApplyButton"));
		_applyButton.setMinimumSize(new Dimension(120, 21));
		_applyButton.setMaximumSize(new Dimension(120, 21));
		_applyButton.setPreferredSize(new Dimension(120, 21));
		_applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickButtonApply();
				_applyButton.setEnabled(false);
			}
		});

		_cancelButton = new JButton();
		_cancelButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_CancelButton"));
		_cancelButton.setMinimumSize(new Dimension(120, 21));
		_cancelButton.setMaximumSize(new Dimension(120, 21));
		_cancelButton.setPreferredSize(new Dimension(120, 21));
		_cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				_window.displayPanel(_parentPanel);
				for (int i = 0; i < _tableModel.getDeleteDatas().size(); i++) {
					ComponentTransition c = _tableModel.getDeleteElementAt(i);
					c.setFlag(c.getPreviousFlag());
				}
				_tableModel.removeAll();
				_window.enabledNextAndPreviousButton(true);
			}
		});

		_validateButton = new JButton();
		_validateButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_ValidateButton"));
		_validateButton.setMinimumSize(new Dimension(120, 21));
		_validateButton.setMaximumSize(new Dimension(120, 21));
		_validateButton.setPreferredSize(new Dimension(120, 21));
		_validateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				_parentPanel.saveComponentTransition();
				_parentPanel.fieldsHaveChanged();
				_window.displayPanel(_parentPanel);
				_window.enabledNextAndPreviousButton(true);
			}
		});

		JPanel panel2 = new JPanel();
		panel2.setMinimumSize(new Dimension(480, 236));
		panel2.setMaximumSize(new Dimension(480, 236));
		panel2.setPreferredSize(new Dimension(480, 236));
		panel2
				.setBorder(BorderFactory
						.createTitledBorder((MessageManager
								.getMessage("&ComponentConfiguration_SecondDescriptionSentence"))));
		panel2.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = c.weighty = 0.0;
		panel2.add(startStateLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel2.add(_startStateBox, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(commandLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(_commandTextField, c);

		/*
		 * D�commenter cette partie de code pour prendre en compte le bouton
		 * commande
		 * 
		 * c.gridx = 2; c.gridy = 1; c.gridwidth = 1; c.gridheight = 1;
		 * panel2.add(_commandButton, c);
		 */

		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(commandTypeLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 1;
		// panel2.add(_commandTypeBox, c);
		panel2.add(_commandTypeTextField, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(checkIntervalLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel2.add(_checkIntervalTextField, c);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(checkTimeOutLabel, c);

		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel2.add(_checkTimeOutTextField, c);

		Box hBox1 = Box.createHorizontalBox();
		hBox1.add(_newButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_removeButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_applyButton);
		hBox1.setMaximumSize(new Dimension(480, 20));

		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_END;
		panel2.add(hBox1, c);

		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_START;
		panel2.add(obligatoryFieldLabel, c);

		c1.gridx = 0;
		c1.gridy = 3;
		c1.gridwidth = 3;
		c1.gridheight = 1;
		panel1.add(panel2, c1);

		Box hBox2 = Box.createHorizontalBox();
		hBox2.add(_cancelButton);
		hBox2.add(Box.createHorizontalStrut(10));
		hBox2.add(_validateButton);
		hBox2.add(Box.createHorizontalStrut(12));
		hBox2.setMaximumSize(new Dimension(260, 20));

		c1.gridx = 0;
		c1.gridy = 4;
		c1.gridwidth = 3;
		c1.gridheight = 1;
		c1.anchor = GridBagConstraints.LINE_END;
		panel1.add(hBox2, c1);

		setLayout(new BorderLayout());
		add(panel1, BorderLayout.NORTH);

		trace_methods.endOfMethod();
	}

	/*------------------------------------------------------------
	 * Nom : onClickApplyButton
	 * 
	 * Description :
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * le bouton 'Appliquer' de la zone de saisie. Son r�le est de 
	 * contr�ler les informations saisies par l'utilisateur et de les
	 * enregistrer dans la table si tout est correcte.
	 *------------------------------------------------------------*/
	private void onClickButtonApply() {
		// On souhaite enregistrer les donn�es saisies
		if (_commandTextField.getText().equals("")) {
			DialogManager.displayDialog("Information", 
				"&ComponentConfiguration_MessageInformation", null, null);
		} else {
			// On r�cup�re les champs
			String startStateName = _startStateBox.getSelectedItem().toString();
			String command = _commandTextField.getText();
			String commandType = _commandTypeTextField.getText();
			int checkInterval = 0;

			if (!_checkIntervalTextField.getText().equals(""))
				checkInterval = Integer.parseInt(_checkIntervalTextField
						.getText());

			int checkTimeOut = 0;
			if (!_checkTimeOutTextField.getText().equals(""))
				checkTimeOut = Integer.parseInt(_checkTimeOutTextField
						.getText());

			// Dans un premier temps, on va rechercher la ligne du
			// tableau associ�e au nom de l'�tat de d�part si
			// elle existe.

			if (command.equals("") || commandType.equals("")) {
				DialogManager.displayDialog("Information", 
					"&ComponentConfiguration_MessageInformation", null, null);
			} else {
				int row = _tableModel.findRow(startStateName);

				if (row == -1) {
					// Cette ligne n'existe pas, c'est une nouvelle
					// transition.
					// On recherche l'�tat associ� au nom s�lectionn�

					ComponentStates cs = null;
					ArrayList<ModelInterface> tabModel = _window.getModel();
					for (int index = 0; index < tabModel.size(); index++) {
						if (tabModel.get(index) instanceof ComponentStates) {
							cs = (ComponentStates) tabModel.get(index);
							if (cs.getStateName().equals(startStateName))
								break;
						}
					}
					if (cs != null && cs.getStateName() != null
							&& cs.getStateName().equals(startStateName)) {
						// On cr�e la nouvelle transition
						char flag = 'A';
						ComponentTransition ct = new ComponentTransition();

						ArrayList<ComponentTransition> deleteElement = _tableModel
								.getDeleteDatas();
						if (deleteElement != null) {
							for (int index = 0; index < deleteElement.size(); index++) {

								if (deleteElement.get(index).getStartState()
										.getStateName().equals(startStateName)) {
									flag = 'M';
									ct = deleteElement.get(index);
									deleteElement.remove(index);
									break;
								}
							}
						}

						ct.setStartState(cs);
						ct.setNewCommand(command);
						ct.setNewCommandType(commandType);
						ct.setNewCheckInterval(checkInterval);
						ct.setNewCheckTimeOut(checkTimeOut);
						ct.setFlag(flag);
						_tableModel.addElement(ct);

						// On efface les champs de saisie
						_startStateBox.setSelectedIndex(0);
						_commandTextField.setText("");
						// _commandTypeBox.setSelectedIndex(0);
						_commandTypeTextField.setText("");
						_checkIntervalTextField.setText("");
						_checkTimeOutTextField.setText("");
						// _executiveUserTextField.setText("");
						_specificTransitionTable.getSelectionModel()
								.clearSelection();

						// Une transition a �t� saisie, on d�bloque le bouton
						// vers
						// l'�cran pr�c�dent.
						if (!_validateButton.isEnabled())
							_validateButton.setEnabled(true);
					} else {
						DialogManager.displayDialog("Information",
							"&ERR_ErrorWhileSavingDataNoSelectedState",
							null, null);
					}
				} else {
					// La transition existe donc on la met � jour
					int selectedRow = _specificTransitionTable.getSelectedRow();

					if (selectedRow == -1) {
						DialogManager.displayDialog("Information",
							"&ERR_AlreadyOneTransitionForThisState",
							null, null);
					} else {

						ComponentTransition ct = _tableModel.getElementAt(row);

						ct.setCommand(command);
						ct.setCommandType(commandType);
						ct.setCheckInterval(checkInterval);
						ct.setCheckTimeOut(checkTimeOut);
						if (ct.getFlag() == 'E')
							ct.setFlag('M');

						// On recharge le tableau et on le r�affiche
						_tableModel.fireTableRowsUpdated(row, row);

						// On efface les champs de saisie
						_startStateBox.setSelectedIndex(0);
						_commandTextField.setText("");
						_commandTypeTextField.setText("");
						_checkIntervalTextField.setText("");
						_checkTimeOutTextField.setText("");

						_specificTransitionTable.getSelectionModel()
								.clearSelection();

						// Une transition a �t� saisie, on d�bloque le bouton
						// vers
						// l'�cran pr�c�dent.
						if (!_validateButton.isEnabled())
							_validateButton.setEnabled(true);
					}
				}
			}
		}
	}

	/*------------------------------------------------------------
	 * Nom : onClickRemoveButton
	 * 
	 * Description :
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * le bouton 'Effacer' de la zone de saisie. Son r�le est de 
	 * supprimer la ligne s�lectionn�e du tableau.
	 *------------------------------------------------------------*/
	private void onClickRemoveButton() {

		// On r�cup�re la ligne s�lectionn�e du tableau si il y
		// en a une.
		int selectedRow = _specificTransitionTable.getSelectedRow();

		// Si une ligne du tableau est s�lectionn�e
		if (selectedRow != -1) {

			ComponentTransition cc = _tableModel.getElementAt(selectedRow);
			if (cc != null) {
				if (cc.getFlag() == 'A')
					_tableModel.removeElement(selectedRow);
				else if (cc.getFlag() == 'E' || cc.getFlag() == 'M') {
					cc.setPreviousFlag(cc.getFlag());
					_tableModel.removeElement(selectedRow);
					cc.setFlag('S');
					_tableModel.addDeleteElement(cc);
				} else {
					// A gros probl�me
				}
			}
			_tableModel.fireTableRowsDeleted(selectedRow, selectedRow);

			// On efface les champs de saisies
			_startStateBox.setSelectedIndex(0);
			_commandTextField.setText("");
			_commandTypeTextField.setText("");
			_checkIntervalTextField.setText("");
			_checkTimeOutTextField.setText("");
			_specificTransitionTable.getSelectionModel().clearSelection();

			// Une transition doit au moins �tre sp�cifi�e. On bloque
			// donc le retour � l'�cran pr�c�dent si aucune transition
			// n'a �t� sp�cifi�e.
			if (_tableModel.getRowCount() == 0)
				_validateButton.setEnabled(false);
			else if (!_validateButton.isEnabled())
				_validateButton.setEnabled(true);

			// On d�sactive le bouton supprimer
			_removeButton.setEnabled(false);
		}

	}

	/*------------------------------------------------------------
	 * Nom : onClickNewButton
	 * 
	 * Description :
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * le bouton 'Nouveau' de la zone de saisie. Son r�le est d'effacer 
	 * le contenu de la zone de saisie ainsi que la ligne s�lectionner 
	 * dans le tableau afin de permettre une nouvelle saisie.
	 *------------------------------------------------------------*/
	private void onClickNewButton() {

		// On efface les champs de saisies
		_startStateBox.setSelectedIndex(0);
		_commandTextField.setText("");
		_commandTypeTextField.setText("");
		_checkIntervalTextField.setText("");
		_checkTimeOutTextField.setText("");
		_specificTransitionTable.getSelectionModel().clearSelection();

		// Une transition doit au moins �tre sp�cifi�e. On bloque
		// donc le retour � l'�cran pr�c�dent si aucune transition
		// n'a �t� sp�cifi�e.
		if (_tableModel.getRowCount() == 0)
			_validateButton.setEnabled(false);
		else if (!_validateButton.isEnabled())
			_validateButton.setEnabled(true);

		// On d�sactive le bouton supprimer
		_removeButton.setEnabled(false);

	}

	/*------------------------------------------------------------
	 * Nom : onSelectionElement
	 * 
	 * Description :
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * une ligne du tableau. Son r�le est de pr� remplir les champs
	 * de saisie pour permettre la modification de ceux-ci si ils ne
	 * font pas partit de la cl� primaire.
	 * Cette m�thode est �galement appel�e en cas de d�selection d'une
	 * ligne dans la table, elle efface alors la s�lection et le 
	 * contenu des champs de la zone de saisie.
	 *------------------------------------------------------------*/
	private void onSelectionElement(ListSelectionEvent event) {

		ListSelectionModel lsm = (ListSelectionModel) event.getSource();

		int index = lsm.getMinSelectionIndex();
		if (index == -1) {
			_startStateBox.setEnabled(true);
			_startStateBox.setSelectedIndex(0);
			_commandTextField.setText("");
			_commandTypeTextField.setText("");
			_checkTimeOutTextField.setText("");
			_checkIntervalTextField.setText("");

			_applyButton.setEnabled(false);
			_removeButton.setEnabled(false);

		} else {

			ComponentTransition ct = _tableModel.getElementAt(index);
			_startStateBox.setSelectedItem(ct.getStartState().getStateName());

			if (ct.getFlag() == 'E') {
				_commandTextField.setText(ct.getCommand());
				_commandTypeTextField.setText(ct.getCommandType());
				_checkTimeOutTextField.setText(String.valueOf(ct
						.getCheckTimeOut()));
				_checkIntervalTextField.setText(String.valueOf(ct
						.getCheckInterval()));
			} else {
				_commandTextField.setText(ct.getNewCommand());
				_commandTypeTextField.setText(ct.getNewCommandType());
				_checkTimeOutTextField.setText(String.valueOf(ct
						.getNewCheckTimeOut()));
				_checkIntervalTextField.setText(String.valueOf(ct
						.getNewCheckInterval()));
			}

			_startStateBox.setEnabled(false);
			_applyButton.setEnabled(true);
			_removeButton.setEnabled(true);

		}
	}

	/*----------------------------------------------------------
	 * Nom: fieldsHaveChanged
	 *
	 * Description:
	 * Cette m�thode est appel�e lorsque les champs ont chang�s dans 
	 * la zone de saisie des donn�es.
	 * Elle permet de mettre � jour l'�tat du bouton "Appliquer" en 
	 * fonction de la pr�sence ou non des informations.
	 * ----------------------------------------------------------*/
	private void fieldsHaveChanged() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"LoginDialog", "fieldsHaveChanged");

		trace_methods.beginningOfMethod();

		if (_commandTextField.getText().equals("")
				|| _commandTypeTextField.getText().equals("")) {
			_applyButton.setEnabled(false);
			return;
		}
		// Ok, on peut valider le bouton
		_applyButton.setEnabled(true);
		trace_methods.endOfMethod();
	}
}
