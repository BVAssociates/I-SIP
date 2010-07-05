//------------------------------------------------------------
// Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
//------------------------------------------------------------
//
// FICHIER: VariableDescriptionPanel
// VERSION: 1.0
//
//----------------------------------------------------------
// DESCRIPTION: Troisi�me panneau de l'assistant
// DATE: 4/06/08
// AUTEUR: Hicham Doghmi & Florent Cossard
// PROJET: All-In
// GROUPE: view
//----------------------------------------------------------

package com.bv.isis.console.impl.processor.config.implementation.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.bv.core.gui.IconLoader;
import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.impl.processor.config.implementation.ComponentConfigurationProcessor;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentStates;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentTransition;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentTransitionGroups;
import com.bv.isis.console.node.GenericTreeObjectNode;

public class GenericTransitionDescriptionPanel extends AbstractPanel {

	private static final long serialVersionUID = 1L;

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: SpecificTransitionDescriptionPanel
	 * 
	 * Description: 
	 * Cette classe impl�mente la panneau qui permet de d�crire 
	 * les transitions possibles vers un �tat d'arriv�e.
	 * ----------------------------------------------------------*/
	public GenericTransitionDescriptionPanel(WindowInterface mainWindow) {
		super();

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"GenericTransitionDescriptionPanel",
				"GenericTransitionDescriptionPanel");
		trace_methods.beginningOfMethod();

		_window = mainWindow;

		// Cr�ation de l'interface graphique
		makePanel();
		_parentPanel = new SpecificTransitionDescriptionPanel(_window, this);
		trace_methods.endOfMethod();
	}

	/*-----------------------------------------------------------
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
				"GenericTransitionDescriptionPanel", "beforeDisplay");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("modele=" + modele);

		_tableModel = new TransitionGroupsTableModel();

		_endStateComboBox.removeAllItems();

		if (modele != null) {
			for (int i = 0; i < modele.size(); i++) {
				if (modele.get(i) instanceof ComponentTransitionGroups) {
					ComponentTransitionGroups c = (ComponentTransitionGroups) modele
							.get(i);
					if (c.getFlag() != 'S')
						_tableModel.addElement(c);
					else
						_tableModel.addDeleteElement(c);
				} else if (modele.get(i) instanceof ComponentStates) {
					ComponentStates c = (ComponentStates) modele.get(i);
					if (c.getFlag() != 'S'
						&& !c.getStateName().equals(
						MessageManager.getMessage("&ComponentConfiguration_AnyState")))
						_endStateComboBox.addItem(c.getStateName());
				}
			}
			_genericTransitionTable.setModel(_tableModel);
		}
		trace_methods.endOfMethod();
	}

	// ----------------------------------------------------------
	//	
	// Nom: afterDisplay

	// Description:
	// Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	// Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	// 
	// Retourne : Vrai (true) si tout c'est bien pass�, faux (false sinon)
	// ----------------------------------------------------------
	public boolean afterDisplay() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"GenericTransitionDescriptionPanel", "afterDisplay");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	/*----------------------------------------------------------
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
	 * ----------------------------------------------------------*/
	public boolean beforeHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"GenericTransitionDescriptionPanel", "beforeHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	// ----------------------------------------------------------
	//	
	// Nom: afterHide

	// Description:
	// Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	// Cette m�thode d�finit le comportement du panneau apr�s avoir �t� cach�.
	// Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	//
	// Retourne : Vrai (true) si tout c'est bien pass�, faux (false sinon)
	// ----------------------------------------------------------
	public boolean afterHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"GenericTransitionDescriptionPanel", "afterHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	// ----------------------------------------------------------
	//	
	// Nom: end

	// Description:
	// Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	// Cette m�thode est appel�e lors de la destruction de l'assistant. Elle est
	// utiliser pour lib�rer l'espace m�moire utilis� par les variables des
	// classes.
	//
	// Retourne : Vrai (true) si tout c'est bien pass�, faux (false sinon)
	// ----------------------------------------------------------
	public boolean end() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"GenericTransitionDescriptionPanel", "end");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	/*------------------------------------------------------------
	 * Nom: update
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle est appel�e par la classe AbstractWindow. Elle est en charge 
	 * de m�moriser les donn�es saisis par l'utilisateur dans le mod�le 
	 * de donn�es pass�s en param�tre.
	 * 
	 * Param�tre : 
	 *  - tabModels : Le mod�le de donn�es avant modification.
	 * 
	 * Retourne : Le nouveau mod�le de donn�es.
	 * ------------------------------------------------------------*/
	public ArrayList<ModelInterface> update(ArrayList<ModelInterface> tabModels) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"GenericTransitionDescriptionPanel", "update");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tabModels=" + tabModels);

		ArrayList<ComponentTransitionGroups> deletedElements = _tableModel
				.getDeleteDatas();
		ArrayList<ComponentTransitionGroups> newElements = _tableModel
				.getDatas();

		if (tabModels != null) {

			// On supprime tout les groupes de transitions
			for (int index1 = 0; index1 < tabModels.size(); index1++) {
				if (tabModels.get(index1) instanceof ComponentTransitionGroups) {
					tabModels.remove(index1);
					// On recule d'une case dans le tableau pour ne pas oublier
					// l'�l�ment d�cal�
					index1--;
				}
			}
		} else
			// Le tableau �tait inexistant, on le cr�e
			tabModels = new ArrayList<ModelInterface>();

		// On ajoute les nouvelles transitions au tableau de ModelInterface
		tabModels.addAll(deletedElements);
		tabModels.addAll(newElements);

		trace_methods.endOfMethod();
		return tabModels;
	}

	/*----------------------------------------------------------
	 * Nom: saveComponentTransition
	 *
	 * Description:
	 * 
	 * Cette m�thode est appell�e par la classe SpecificTransitionDescription.
	 * Elle m�morise le tableau de ComponentTransition.
	 * ----------------------------------------------------------*/
	public void saveComponentTransition() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"GenericTransitionDescriptionPanel", "saveComponentTransition");
		trace_methods.beginningOfMethod();

		_tab = _parentPanel.getTransition();

		trace_methods.endOfMethod();
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
	public void fieldsHaveChanged() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"LoginDialog", "fieldsHaveChanged");

		trace_methods.beginningOfMethod();
		boolean bool = false;

		if (_superiorButton.isSelected())
			bool = true;
		else if (_inferiorButton.isSelected())
			bool = true;
		else if (_identicalButton.isSelected())
			bool = true;

		if (_nameTextField.getText().equals("") || 
			_responsabilitiesTextField.getText().equals("") || 
			bool == false) {
			_applyButton.setEnabled(false);
			return;
		}
		// Ok, on peut valider le bouton
		_applyButton.setEnabled(true);

		trace_methods.endOfMethod();
	}
	
	// ****************** PROTEGE *********************
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

					onClickApplyButton();
				} else
					fieldsHaveChanged();
			}
		});
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	protected TransitionGroupsTableModel _tableModel;

	/*------------------------------------------------------------
	 * Nom : _parentPanel
	 * 
	 * Description :
	 * Cet attribut maintient une r�f�rence sur le panneau sp�cifique
	 * des transitions que va appeller ce panneau.
	 * ------------------------------------------------------------*/
	protected SpecificTransitionDescriptionPanel _parentPanel;

	protected ArrayList<ComponentTransition> _tab;

	/*------------------------------------------------------------
	 * Nom : _genericTransitionTable
	 * 
	 * Description :
	 * Cet attribut repr�sente un tableau r�capitulant l'ensemble
	 * des transitions d�finis par l'utilisateur.
	 * ------------------------------------------------------------*/
	protected JTable _genericTransitionTable;

	/*------------------------------------------------------------
	 * Nom : _superiorButton
	 * 
	 * Description :
	 * Cet attribut d�finit le bouton
	 * "Action de d�marage" dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	protected JRadioButton _superiorButton;

	/*------------------------------------------------------------
	 * Nom : _identicalButton
	 * 
	 * Description :
	 * Cet attribut d�finit le bouton "Autre" dans le sous 
	 * panneau de saisie.
	 * ------------------------------------------------------------*/
	protected JRadioButton _identicalButton;

	/*------------------------------------------------------------
	 * Nom : _inferiorButton
	 * 
	 * Description :
	 * Cet attribut d�finit le bouton
	 * "Action d'arr�t" dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	protected JRadioButton _inferiorButton;

	/*------------------------------------------------------------
	 * Nom : _arrowRight
	 * 
	 * Description :
	 * Cet attribut d�finit un label pour le bouton
	 * qui donne acc�s au panneau  "SpecificTransitionDescriptionPanel"
	 * ------------------------------------------------------------*/
	protected JButton _arrowRight;

	/*------------------------------------------------------------
	 * Nom : _nameTextField
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie du nom
	 * de la transition
	 * ------------------------------------------------------------*/
	protected JTextField _nameTextField;

	/*------------------------------------------------------------
	 * Nom : _descriptionTextField
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie de la description
	 * de la transition
	 * ------------------------------------------------------------*/
	protected JTextField _descriptionTextField;

	/*------------------------------------------------------------
	 * Nom : _responsabilitiesTextField;
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie des responsabilit�s
	 * de la transition
	 * ------------------------------------------------------------*/
	protected JTextField _responsabilitiesTextField;

	/*------------------------------------------------------------
	 * Nom : _responsabilitiesButton
	 * 
	 * Description :
	 * 
	 * Cet attribut est utilis� pour le bouton qui donne acc�es 
	 * a une liste de responsabilit�s
	 * ------------------------------------------------------------*/
	protected JButton _responsabilitiesButton;

	/*----------------------------------------------------------
	 * Nom : _endStateComboBox
	 * 
	 * Description :
	 * Cette attribut d�finit une liste d�roulante pour le choix
	 * de l'�tat d'arriv�e dans le sous panneau de saisie.
	 * ----------------------------------------------------------*/
	protected JComboBox _endStateComboBox;

	/*------------------------------------------------------------
	 * Nom : _applyButton
	 * 
	 * Description :
	 * Cet attribut est utilis� pour le bouton appliquer.
	 * ------------------------------------------------------------*/
	protected JButton _applyButton;

	/*------------------------------------------------------------
	 * Nom : _removeButton
	 * 
	 * Description :
	 * Cet attribut est utilis� pour le bouton supprimer.
	 * ------------------------------------------------------------*/
	protected JButton _removeButton;

	/*------------------------------------------------------------
	 * Nom : _newButton
	 * 
	 * Description :
	 * Cet attribut d�finit le boutton utilis� pour
	 * effacer la ligne s�lectionn�e dans le tableau des variables.
	 * ------------------------------------------------------------*/
	protected JButton _newButton;

	private class TransitionGroupsTableModel extends AbstractTableModel {

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
		 * Cet attribut est un tableau de ComponentManagementAction.
		 * Chaque �l�ment du tableau repr�sente une ligne de la JTable
		 * et contient les informations d'une action.
		 * ------------------------------------------------------------*/
		private ArrayList<ComponentTransitionGroups> _datas;

		private ArrayList<ComponentTransitionGroups> _deleteDatas;

		/*------------------------------------------------------------
		 * Nom : TransitionGroupsTableModel
		 * 
		 * Description :
		 * Cette m�thode est le contructeur de la classe.
		 * Son r�le est de cr�er les deux tableaux de titres et de donn�es.
		 * Pour le tableau des titres, elle le remplira � partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/
		public TransitionGroupsTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "TransitionGroupsTableModel");
			trace_methods.beginningOfMethod();

			// Cr�ation du tableau des titres
			_titles = new String[5];
			// La premi�re colonne repr�sentera le nom de la transition
			_titles[0] = MessageManager
					.getMessage("&ComponentConfiguration_Name");
			// La deuxi�me colonne repr�sentera l'�tat d'arriv�e associ�
			// � la transition
			_titles[1] = MessageManager
					.getMessage("&ComponentConfiguration_EndState");
			// La troisi�me colonne repr�sentera la description associ�e
			// � la transition
			_titles[2] = MessageManager
					.getMessage("&ComponentConfiguration_Description");
			// La quatri�me colonne repr�sentera le niveau fonctionnel associ�
			// � la transition
			_titles[3] = MessageManager
					.getMessage("&ComponentConfiguration_TransitionType");
			// La quatri�me colonne repr�sentera le niveau fonctionnel associ�
			// � la transition
			_titles[4] = MessageManager
					.getMessage("&ComponentConfiguration_Responsabilities");

			// Cr�ation du tableau de donn�es
			_datas = new ArrayList<ComponentTransitionGroups>();
			_deleteDatas = new ArrayList<ComponentTransitionGroups>();

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getColumnCount
		 * 
		 * Description : 
		 * Cette m�thode retourne le nombre de colonnes de la JTable. 
		 * Ce nombre est donn� par la dimmension du tableaux des titres.
		 * 
		 * Retourne : un entier : le nombre de colonne.
		 * ------------------------------------------------------------*/
		public int getColumnCount() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "getColumnCount");
			trace_methods.beginningOfMethod();

			if (_titles != null)
				return _titles.length;
			else
				trace_methods.endOfMethod();
			return 0;
		}

		/*------------------------------------------------------------
		 * Nom : getRowCount
		 * 
		 * Description : 
		 * Cette m�thode retourne le nombre de lignes de la JTable. 
		 * Ce nombre est donn� par la dimmension du tableaux des donn�es.
		 * 
		 * Retourne : un entier : le nombre de lignes.
		 * ------------------------------------------------------------*/
		public int getRowCount() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "getRowCount");
			trace_methods.beginningOfMethod();

			if (_datas != null)
				return _datas.size();
			else
				trace_methods.endOfMethod();
			return 0;
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
		public ArrayList<ComponentTransitionGroups> getDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "getDatas");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _datas;
		}

		/*------------------------------------------------------------
		 * Nom : findRow
		 * 
		 * Description : 
		 * Cette m�thode recherche la pr�sence d'une transition dans la JTable
		 * � partir du nom de la transition pass� en param�tre. Elle retourne
		 * le num�ro de la ligne correspondant � cet action si elle existe,
		 * -1 sinon.
		 * 
		 * Argument :
		 *  - transitionName : une cha�ne de caract�res corespondant au nom
		 *    de l'action recherch�
		 *    
		 * Retourne : un entier : le num�ro de ligne de la transition, -1 si 
		 * elle n'existe pas.
		 * ------------------------------------------------------------*/
		public int findRow(String transitionName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("transitionName=" + transitionName);

			if (_datas != null) {
				for (int index = 0; index < _datas.size(); index++) {
					if (_datas.get(index).getEndState().getStateName().equals(
							transitionName)) {
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
		 * 	- 0 pour le nom de la transition
		 *  - 1 pour son �tat d'arriv�
		 *  - 2 pour sa description
		 *  - 3 pour son niveau fonctionel
		 *  - 4 pour ses responsabilit�s 
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
					"TransitionGroupsTableModel", "getValue");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				ComponentTransitionGroups ctg = _datas.get(line);
				if (ctg != null) {
					if (column == 0) {
						if (ctg.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return ctg.getTransitionName();
						} else {
							trace_methods.endOfMethod();
							return ctg.getNewTransitionName();
						}
					} else if (column == 1) {
						trace_methods.endOfMethod();
						return ctg.getEndState().getStateName();
					} else if (column == 2) {
						if (ctg.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return ctg.getDescription();
						} else {
							trace_methods.endOfMethod();
							return ctg.getNewDescription();
						}
					} else if (column == 3) {
						if (ctg.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return ctg.getTransitionType();
						} else {
							trace_methods.endOfMethod();
							return ctg.getNewTransitionType();
						}

					} else {
						if (ctg.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return ctg.getResponsabilities();
						} else {
							trace_methods.endOfMethod();
							return ctg.getNewResponsabilities();
						}
					}

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
		 *  - 0 pour le nom de la transition
		 *  - 2 pour sa description
		 *  - 3 pour son niveau fonctionnel
		 *  - 4 pour ses responsabilit�s
		 *  La colonne 1 ne peut �tre modifi�e.
		 * 
		 * Arguments :
		 *  - obj : la nouvelle valeur de la cellule
		 *  - line : le num�ro de ligne
		 *  - column : la num�ro de la colonne
		 * ------------------------------------------------------------*/
		public void setValueAt(Object obj, int line, int column) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "setValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("obj=" + obj);
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				ComponentTransitionGroups ctg = _datas.get(line);
				if (ctg != null) {
					if (column == 0)
						ctg.setTransitionName(obj.toString());
					else if (column == 2)
						ctg.setDescription(obj.toString());
					else if (column == 3)
						ctg.setTransitionType(obj.toString().charAt(0));
					else if (column == 4)
						ctg.setResponsabilities(obj.toString());
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
		public String getColumnName(int col) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "getColumnName");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("col=" + col);

			if (_titles != null && _titles.length > col)
				return _titles[col];

			trace_methods.endOfMethod();
			return "";
		}

		/*------------------------------------------------------------
		 * Nom : addElement
		 * 
		 * Description : 
		 * Cette m�thode ajoute un nouvel �l�ment au tableau de donn�es. 
		 * 
		 * Argument : 
		 *  - element : L'�l�ment � ajouter
		 * ------------------------------------------------------------*/
		public void addElement(ComponentTransitionGroups element) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "addElement");
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
					"TransitionGroupsTableModel", "removeElement");
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
					"TransitionGroupsTableModel", "removeAll");

			_datas.clear();
			super.fireTableDataChanged();

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getElementAt
		 * 
		 * Description : 
		 * Cette m�thode retourne le ComponentTransitionGroups de la ligne 
		 * demand�e. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � r�cup�rer
		 *  
		 * Retourne : Le ComponentTransitionGroups de la ligne s�lectionn�e.
		 * ------------------------------------------------------------*/
		public ComponentTransitionGroups getElementAt(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "getElementAt");
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
		 * base, le ComponentTransitionGroups pass� en param�tre. 
		 * 
		 * Argument : 
		 *  - group : Le ComponentTransitionGroups qu'il faudra supprimer 
		 *    de la base de donn�es.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(ComponentTransitionGroups group) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "addDeleteElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("group=" + group);

			if (_deleteDatas != null)
				_deleteDatas.add(group);

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : removeDeleletElement
		 * 
		 * Description : 
		 * Cette m�thode retire au tableau des �l�ments � supprimer de la
		 * base, le ComponentTransitionGroups d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentTransitionGroups qu'il faut retirer 
		 *  du tableau.
		 * ------------------------------------------------------------*/
		public void removeDeleletElement(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "removeDeleletElement");
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
		 * base, le ComponentTransitionGroups d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentTransitionGroups qu'il faut r�cup�rer 
		 *    dans le tableau.
		 * 
		 * Retourne : Le ComponentTransitionGroups supprim� de la ligne 
		 * souhait�e.
		 * ------------------------------------------------------------*/
		public ComponentTransitionGroups getDeleteElementAt(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "getDeleteElementAt");
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
		 * ComponentTransitionGroups � supprimer de la base de donn�es. 
		 * 
		 * Retourne : Le tableau des ComponentTransitionGroups � supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentTransitionGroups> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "getDeleteDatas");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();

			return _deleteDatas;
		}

	}

	/*------------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette m�thode est appel�e lors de la construction du panneau.
	 * Son r�le est d'instancier les composants graphiques du panneau 
	 * et de les positionner.
	 * ------------------------------------------------------------*/
	private void makePanel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"TransitionGroupsTableModel", "makePanel");

		_tableModel = null;

		_genericTransitionTable = new JTable(_tableModel);
		_genericTransitionTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_genericTransitionTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});

		// On int�gre un d�filement dans le tableau des groupes de transitions
		JScrollPane scrollPaneGenericTransitionTable = new JScrollPane();
		scrollPaneGenericTransitionTable
				.setViewportView(_genericTransitionTable);

		// On pr�sente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(450, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(450, 20));
		firstDescriptionSentence
				.setText(MessageManager
						.getMessage("&ComponentConfiguration_FirstDescriptionSentenceGenericTransition"));

		// On d�finit un label pour le champ "Nom"
		JLabel nameLabel = new JLabel();
		nameLabel.setMinimumSize(new Dimension(120, 20));
		nameLabel.setMaximumSize(new Dimension(120, 20));
		nameLabel.setPreferredSize(new Dimension(120, 20));
		nameLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Name")
				+ " :   *");
		// On d�finit un label pour le champ "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setMinimumSize(new Dimension(120, 20));
		descriptionLabel.setMaximumSize(new Dimension(120, 20));
		descriptionLabel.setPreferredSize(new Dimension(120, 20));
		descriptionLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Description")
				+ " : ");

		// On d�finit un label pour le champ "�tat d'arriv�e"
		JLabel endStateLabel = new JLabel();
		endStateLabel.setMinimumSize(new Dimension(120, 20));
		endStateLabel.setMaximumSize(new Dimension(120, 20));
		endStateLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_EndState")
				+ " : ");

		// On d�finit un label pour le champ "Responsabilit�s"
		JLabel responsabilitiesLabel = new JLabel();
		responsabilitiesLabel.setMinimumSize(new Dimension(120, 20));
		responsabilitiesLabel.setMaximumSize(new Dimension(120, 20));
		responsabilitiesLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Responsabilities")
				+ " :  *");

		// On d�finit le label "Changement de niveau fonctionnel"
		JLabel changeTransitionTypeLabel = new JLabel();
		changeTransitionTypeLabel.setMinimumSize(new Dimension(450, 20));
		changeTransitionTypeLabel.setMaximumSize(new Dimension(450, 20));
		changeTransitionTypeLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_ChangeTransitionType")
				+ " : *");

		// On d�finit le label "D�finition des commandes de transitions"
		JLabel definitionCommandTransitionLabel = new JLabel();
		definitionCommandTransitionLabel
				.setMinimumSize(new Dimension(450, 20));
		definitionCommandTransitionLabel
				.setMaximumSize(new Dimension(450, 20));
		definitionCommandTransitionLabel
				.setText(MessageManager
						.getMessage("&ComponentConfiguration_DefinitionCommandTransition")
						+ " : *");

		// On d�finit un label pour le bouton "Action de d�marage"
		JLabel superiorLabel = new JLabel(IconLoader.getIcon("arrow_up"));

		// On d�finit un label pour le bouton "Action d'arr�t"
		JLabel inferiorLabel = new JLabel(IconLoader.getIcon("arrow_down"));

		// On d�finit un label pour le bouton "Action de d�marage"
		JLabel identicalLabel = new JLabel(IconLoader.getIcon("arrow_rotate"));

		// On d�finit un label pour indiquer que l'asterisque en fin des noms
		// de champs sont obligatoires pour la saisie.
		JLabel obligatoryFieldLabel = new JLabel();
		obligatoryFieldLabel.setMinimumSize(new java.awt.Dimension(200, 20));
		obligatoryFieldLabel.setMaximumSize(new java.awt.Dimension(200, 20));
		obligatoryFieldLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_ObligatoryFields"));

		_nameTextField = new JTextField(20);
		setEnterCallback(_nameTextField);
		_nameTextField.setMinimumSize(new Dimension(330, 20));
		_nameTextField.setMaximumSize(new Dimension(330, 20));
		_nameTextField.setPreferredSize(new Dimension(330, 20));

		_endStateComboBox = new JComboBox();
		setEnterCallback(_endStateComboBox);
		_endStateComboBox.setMinimumSize(new Dimension(330, 20));
		_endStateComboBox.setMaximumSize(new Dimension(330, 20));
		_endStateComboBox.setPreferredSize(new Dimension(330, 20));

		_descriptionTextField = new JTextField();
		setEnterCallback(_descriptionTextField);
		_descriptionTextField.setMinimumSize(new Dimension(328, 20));
		_descriptionTextField.setMaximumSize(new Dimension(328, 20));
		_descriptionTextField.setPreferredSize(new Dimension(328, 20));

		_responsabilitiesTextField = new JTextField();
		setEnterCallback(_responsabilitiesTextField);
		_responsabilitiesTextField.setMinimumSize(new Dimension(280, 20));
		_responsabilitiesTextField.setMaximumSize(new Dimension(280, 20));
		_responsabilitiesTextField.setPreferredSize(new Dimension(280, 20));

		_responsabilitiesButton = new JButton();
		_responsabilitiesButton.setMinimumSize(new Dimension(37, 22));
		_responsabilitiesButton.setMaximumSize(new Dimension(37, 22));
		_responsabilitiesButton.setPreferredSize(new Dimension(37, 22));
		_responsabilitiesButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_ResponsabilitiesButton"));
		_responsabilitiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickResponsabilitiesButton();
			}
		});

		_applyButton = new JButton();
		_applyButton.setEnabled(false);
		_applyButton.setMinimumSize(new Dimension(120, 21));
		_applyButton.setMaximumSize(new Dimension(120, 21));
		_applyButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_ApplyButton"));
		_applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickApplyButton();
			}
		});

		_removeButton = new JButton();
		_removeButton.setEnabled(false);
		_removeButton.setMinimumSize(new Dimension(120, 21));
		_removeButton.setMaximumSize(new Dimension(120, 21));
		_removeButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_RemoveButton"));
		_removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickRemoveButton();
			}

		});

		_newButton = new JButton();
		_newButton.setMinimumSize(new Dimension(120, 21));
		_newButton.setMaximumSize(new Dimension(120, 21));
		_newButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_NewButton"));
		_newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickNewButton();
			}
		});

		_superiorButton = new JRadioButton(MessageManager
				.getMessage("&ComponentConfiguration_SuperiorButton"));
		setEnterCallback(_superiorButton);
		_superiorButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				fieldsHaveChanged();
			}
		});

		_inferiorButton = new JRadioButton(MessageManager
				.getMessage("&ComponentConfiguration_InferiorButton"));
		setEnterCallback(_inferiorButton);
		_inferiorButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				fieldsHaveChanged();
			}
		});

		_identicalButton = new JRadioButton(MessageManager
				.getMessage("&ComponentConfiguration_IdenticalButton"));
		setEnterCallback(_identicalButton);
		_identicalButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				fieldsHaveChanged();
			}
		});

		ButtonGroup groupButton = new ButtonGroup();
		groupButton.add(_superiorButton);
		groupButton.add(_inferiorButton);
		groupButton.add(_identicalButton);

		_arrowRight = new JButton(IconLoader.getIcon("arrow_right"));
		_arrowRight.setMinimumSize(new Dimension(120, 21));
		_arrowRight.setMaximumSize(new Dimension(120, 21));
		_arrowRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickArrowButton();
			}
		});

		Box hBox1 = Box.createHorizontalBox();
		hBox1.setMinimumSize(new Dimension(490, 20));
		hBox1.setMaximumSize(new Dimension(490, 20));
		hBox1.add(Box.createHorizontalStrut(5));
		hBox1.add(firstDescriptionSentence);
		hBox1.add(Box.createHorizontalStrut(10));

		Box hBox2 = Box.createHorizontalBox();
		hBox2.setMinimumSize(new Dimension(490, 100));
		hBox2.setMaximumSize(new Dimension(490, 100));
		hBox2.add(Box.createHorizontalStrut(5));
		hBox2.add(scrollPaneGenericTransitionTable);
		hBox2.add(Box.createHorizontalStrut(5));

		Box hBox3 = Box.createHorizontalBox();
		hBox3.setMinimumSize(new Dimension(490, 20));
		hBox3.setMaximumSize(new Dimension(490, 20));
		hBox3.add(Box.createHorizontalStrut(5));
		hBox3.add(nameLabel);
		hBox3.add(Box.createHorizontalStrut(10));
		hBox3.add(_nameTextField);
		hBox3.add(Box.createHorizontalStrut(5));

		Box hBox4 = Box.createHorizontalBox();
		hBox4.setMinimumSize(new Dimension(490, 20));
		hBox4.setMaximumSize(new Dimension(490, 20));
		hBox4.add(Box.createHorizontalStrut(5));
		hBox4.add(descriptionLabel);
		hBox4.add(Box.createHorizontalStrut(10));
		hBox4.add(_descriptionTextField);
		hBox4.add(Box.createHorizontalStrut(5));

		Box hBox5 = Box.createHorizontalBox();
		hBox5.setMinimumSize(new Dimension(490, 21));
		hBox5.setMaximumSize(new Dimension(490, 21));
		hBox5.add(Box.createHorizontalStrut(90));
		hBox5.add(_newButton);
		hBox5.add(Box.createHorizontalStrut(10));
		hBox5.add(_removeButton);
		hBox5.add(Box.createHorizontalStrut(10));
		hBox5.add(_applyButton);
		hBox5.add(Box.createHorizontalStrut(5));

		Box hBox6 = Box.createHorizontalBox();
		hBox6.setMinimumSize(new Dimension(490, 20));
		hBox6.setMaximumSize(new Dimension(490, 20));
		hBox6.add(Box.createHorizontalStrut(5));
		hBox6.add(endStateLabel);
		hBox6.add(Box.createHorizontalStrut(10));
		hBox6.add(_endStateComboBox);
		hBox6.add(Box.createHorizontalStrut(5));

		Box hBox66 = Box.createHorizontalBox();
		hBox66.setMinimumSize(new Dimension(490, 22));
		hBox66.setMaximumSize(new Dimension(490, 22));
		hBox66.add(Box.createHorizontalStrut(5));
		hBox66.add(responsabilitiesLabel);
		hBox66.add(Box.createHorizontalStrut(10));
		hBox66.add(_responsabilitiesTextField);
		hBox66.add(Box.createHorizontalStrut(10));
		hBox66.add(_responsabilitiesButton);
		hBox66.add(Box.createHorizontalStrut(5));

		Box hBox8 = Box.createHorizontalBox();
		hBox8.setMinimumSize(new Dimension(490, 20));
		hBox8.setMaximumSize(new Dimension(490, 20));
		hBox8.add(Box.createHorizontalStrut(5));
		hBox8.add(changeTransitionTypeLabel);

		Box hBox9 = Box.createHorizontalBox();
		hBox9.setMinimumSize(new Dimension(490, 20));
		hBox9.setMaximumSize(new Dimension(490, 20));
		hBox9.add(Box.createHorizontalStrut(130));
		hBox9.add(definitionCommandTransitionLabel);
		hBox9.add(Box.createHorizontalStrut(10));
		hBox9.add(_arrowRight);
		hBox9.add(Box.createHorizontalStrut(10));

		Box hBox88 = Box.createHorizontalBox();
		hBox88.setMinimumSize(new Dimension(490, 20));
		hBox88.setMaximumSize(new Dimension(490, 20));
		hBox88.add(Box.createHorizontalStrut(25));
		hBox88.add(superiorLabel);
		hBox88.add(_superiorButton);
		hBox88.add(Box.createHorizontalStrut(30));
		hBox88.add(inferiorLabel);
		hBox88.add(_inferiorButton);
		hBox88.add(Box.createHorizontalStrut(30));
		hBox88.add(identicalLabel);
		hBox88.add(_identicalButton);
		hBox88.add(Box.createHorizontalStrut(10));

		Box hBox7 = Box.createHorizontalBox();
		hBox7.setMinimumSize(new Dimension(490, 20));
		hBox7.setMaximumSize(new Dimension(490, 20));
		hBox7.add(Box.createHorizontalStrut(15));
		hBox7.add(obligatoryFieldLabel);

		Box vBox1 = Box.createVerticalBox();
		vBox1.setMinimumSize(new Dimension(486, 150));
		vBox1.setMaximumSize(new Dimension(486, 150));
		vBox1.add(Box.createVerticalStrut(14));
		vBox1.add(hBox1);
		vBox1.add(Box.createVerticalStrut(15));
		vBox1.add(hBox2);

		Box vBox2 = Box.createVerticalBox();
		vBox2.setMinimumSize(new Dimension(480, 285));
		vBox2.setMaximumSize(new Dimension(480, 285));
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox3);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox6);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox4);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox66);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox8);
		vBox2.add(hBox88);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox9);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox5);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox7);
		vBox2
				.setBorder(BorderFactory
						.createTitledBorder((MessageManager
								.getMessage("&ComponentConfiguration_SecondDescriptionSentence"))));

		Box vBox3 = Box.createVerticalBox();
		// vBox3.setMinimumSize(new Dimension(490, 20));
		// vBox3.setMaximumSize(new Dimension(490, 20));
		vBox3.add(vBox1);
		vBox3.add(Box.createVerticalStrut(5));
		vBox3.add(vBox2);
		vBox3.add(Box.createVerticalStrut(5));

		setLayout(new BorderLayout());
		add(vBox3, BorderLayout.NORTH);
		
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
	private void onClickApplyButton() {

		String name = _nameTextField.getText();
		String endState = _endStateComboBox.getSelectedItem().toString();
		String description = _descriptionTextField.getText();
		String responsabilit�s = _responsabilitiesTextField.getText();

		char res = ' ';
		if (_superiorButton.isSelected())
			res = '+';
		else if (_inferiorButton.isSelected())
			res = '-';
		else if (_identicalButton.isSelected())
			res = '=';

		if (name.equals("") || endState.equals("")
				|| responsabilit�s.equals("") || res == ' ') {
			DialogManager.displayDialog("Information", MessageManager
					.getMessage("&ComponentConfiguration_MessageInformation"),
					null, null);
		} else {
			/*
			 * 1 : V�rifier qu'il n'y ai pas de transitions avec le m�me �tat
			 * d'arriv�e 2 : Mettre � jour le tableau 3 : Effacer les champs 4 :
			 * actualiser le tableau
			 */
			if (_tab == null) {
				DialogManager.displayDialog("Information", MessageManager
						.getMessage("&ComponentConfiguration_MessageInformationGenericTransition1"),
						null, null);

			} 
			else {
				int row = _tableModel.findRow(endState);

				// Aucune ligne du tableau n'a �t� s�lectionner
				if (row == -1) {

					ArrayList<ModelInterface> tabModel = _window.getModel();
					// On recherche l'�tat correspondant � celui choisit
					ComponentStates cs = null;
					for (int index = 0; index < tabModel.size(); index++) {
						if (tabModel.get(index) instanceof ComponentStates) {
							cs = (ComponentStates) tabModel.get(index);
							if (cs.getStateName().equals(endState))
								break;
						}
					}
					//Une fois l'�tat d�termin�, on cr�e le ComponentTransitionGroups
					if (cs != null && cs.getStateName() != null
							&& cs.getStateName().equals(endState)) {

						// Ce nouveau groupe peut porter sur le m�me �tat qu'un ancien groupe
						// pr�c�demment supprim� mais toujours existant en base. Il s'agit
						// donc de mettre � jour cette donn�e et non de la cr�er.
						// On recherche donc si dans les �l�ments pr�cedemment supprim�s, il 
						// n'existe pas un groupe avec le m�me �tat d'arriv�e
						char flag = 'A';
						ComponentTransitionGroups ctg = new ComponentTransitionGroups();

						ArrayList<ComponentTransitionGroups> deleteElement = _tableModel
								.getDeleteDatas();
						if (deleteElement != null) {
							for (int index = 0; index < deleteElement.size(); index++) {
								// Si ce groupe existe, on le r�cup�re, et on va le 
								// mettre � jour
								if (deleteElement.get(index).getEndState()
										.getStateName().equals(endState)) {
									flag = 'M';
									ctg = deleteElement.get(index);
									deleteElement.remove(index);
									break;
								}
							}
						}
						// Dans le cas d'une modification
						if (flag == 'M') {
							// il est n�cessaire de v�rifier si les nouvelles transitions 
							// sp�cifiques de ce groupe ne correspondent pas � d'anciennes
							// transitions sp�cifiques de l'ancien groupe. Si oui, il faut 
							// les mettre � jour.
							for (int indexTab = 0; indexTab < _tab.size(); indexTab++) {
								ComponentTransition newTransition = _tab.get(indexTab);

								ArrayList<ComponentTransition> array = ctg.getSpecificTransition();
								if (array != null) {
									for (int indexArray = 0; indexArray < array.size(); indexArray++) {
										ComponentTransition oldTransition = array.get(indexArray);
										if (newTransition.getStartState().getStateName().equals(
												oldTransition.getStartState().getStateName())) {
											newTransition.setCommand(oldTransition.getCommand());
											newTransition.setCommandType(oldTransition.getCommandType());
											newTransition.setCheckInterval(oldTransition.getCheckInterval());
											newTransition.setCheckTimeOut(oldTransition.getCheckTimeOut());
											newTransition.setFlag('M');
											array.remove(indexArray);
											indexArray--;
										}
									}
								}
								_tab.addAll(array);
							}
						}

						ctg.setNewTransitionName(name);
						ctg.setEndState(cs);
						ctg.setNewDescription(description);
						ctg.setNewTransitionType(res);
						ctg.setNewResponsabilities(responsabilit�s);
						ctg.setNewSpecificTransition(_tab);
						ctg.setFlag(flag);

						_tableModel.addElement(ctg);

						_nameTextField.setText("");
						_endStateComboBox.setSelectedIndex(0);
						_descriptionTextField.setText("");
						_responsabilitiesTextField.setText("");
						_genericTransitionTable.getSelectionModel()
								.clearSelection();

						_removeButton.setEnabled(false);
						_applyButton.setEnabled(false);
						_inferiorButton.setSelected(true);
					}
				} 
				else {
					// La transition existe donc on la met � jour
					int selectedRow = _genericTransitionTable.getSelectedRow();

					if (selectedRow == -1) {
						DialogManager.displayDialog("Information",MessageManager
								.getMessage("&ComponentConfiguration_MessageInformationGenericTransition2"),
								null, null);
					} 
					else {
						ComponentTransitionGroups ctg = _tableModel.getElementAt(row);

						ctg.setNewSpecificTransition(_tab);
						ctg.setNewTransitionName(name);
						ctg.setNewDescription(description);
						ctg.setNewTransitionType(res);
						ctg.setNewResponsabilities(responsabilit�s);

						if (ctg.getFlag() == 'E')
							ctg.setFlag('M');

						// On recharge le tableau et on le r�affiche
						_tableModel.fireTableRowsUpdated(row, row);

						// On efface les champs de saisie
						_nameTextField.setText("");
						_endStateComboBox.setSelectedIndex(0);
						_descriptionTextField.setText("");
						_responsabilitiesTextField.setText("");
						_genericTransitionTable.getSelectionModel().clearSelection();
						_removeButton.setEnabled(false);
						_applyButton.setEnabled(false);
						_inferiorButton.setSelected(true);
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

		int selectedRow = _genericTransitionTable.getSelectedRow();

		// Si une ligne du tableau est s�lectionn�e
		if (selectedRow != -1) {
			ComponentTransitionGroups c = _tableModel.getElementAt(selectedRow);
			if (c != null) {
				if (c.getFlag() == 'A')
					_tableModel.removeElement(selectedRow);
				else if (c.getFlag() == 'E' || c.getFlag() == 'M') {
					_tableModel.removeElement(selectedRow);
					c.setFlag('S');
					_tableModel.addDeleteElement(c);
				} else {
					// A gros probl�me
				}
				_tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
			}
			// On efface les champs de saisies
			_nameTextField.setText("");
			_endStateComboBox.setSelectedIndex(0);
			_descriptionTextField.setText("");
			_responsabilitiesTextField.setText("");

			_genericTransitionTable.getSelectionModel().clearSelection();

			_inferiorButton.setSelected(true);
			_applyButton.setEnabled(false);
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
		_nameTextField.setText("");
		_endStateComboBox.setSelectedIndex(0);
		_descriptionTextField.setText("");
		_responsabilitiesTextField.setText("");
		_genericTransitionTable.repaint();
		_genericTransitionTable.getSelectionModel().clearSelection();

		_inferiorButton.setSelected(true);

		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
	}

	private void onClickArrowButton() {

		_parentPanel.loadTransitionArray(_tab);

		_window.displayPanel(_parentPanel);
		_window.enabledNextAndPreviousButton(false);

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

			ComponentTransitionGroups ctg = new ComponentTransitionGroups();

			_tab = ctg.getSpecificTransition();

			_nameTextField.setText("");
			_endStateComboBox.setEnabled(true);
			_endStateComboBox.setSelectedIndex(0);
			_descriptionTextField.setText("");
			_responsabilitiesTextField.setText("");

			_inferiorButton.setSelected(true);

			_applyButton.setEnabled(false);
			_removeButton.setEnabled(false);

		} else {

			ComponentTransitionGroups ctg = new ComponentTransitionGroups();
			ctg = _tableModel.getElementAt(index);
			char res = ' ';
			if (ctg.getFlag() == 'E') {
				_nameTextField.setText(ctg.getTransitionName());
				_endStateComboBox.setSelectedItem(ctg.getEndState()
						.getStateName());
				_descriptionTextField.setText(ctg.getDescription());
				_responsabilitiesTextField.setText(ctg.getResponsabilities());
				res = ctg.getTransitionType();
				_tab = ctg.getSpecificTransition();
			} else {
				_nameTextField.setText(ctg.getNewTransitionName());
				_endStateComboBox.setSelectedItem(ctg.getEndState()
						.getStateName());
				_descriptionTextField.setText(ctg.getNewDescription());
				_responsabilitiesTextField
						.setText(ctg.getNewResponsabilities());
				res = ctg.getNewTransitionType();
				_tab = ctg.getNewSpecificTransition();
			}

			if (res == '+')
				_superiorButton.setSelected(true);
			else if (res == '-')
				_inferiorButton.setSelected(true);
			else if (res == '=')
				_identicalButton.setSelected(true);

			_endStateComboBox.setEnabled(false);
			_applyButton.setEnabled(true);
			_removeButton.setEnabled(true);

		}

	}

	/*------------------------------------------------------------
	 * Nom : onClickResponsabilitiesButton
	 * 
	 * Description :
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * le bouton � c�t� du champ des Responsabilit�s. Son r�le est 
	 * d'ouvrir une bo�te de dialogue � l'utilisateur dans laquelle il
	 * pourra s�lectionner les diff�rentes responsabilit�s.
	 * Cette m�thode interroge le champ 'Responsability' de la table 
	 * 'resp' et affiche les informations r�cup�r�es.
	 *------------------------------------------------------------*/
	private void onClickResponsabilitiesButton() {
		ComponentConfigurationProcessor c = (ComponentConfigurationProcessor) _window;

		GenericTreeObjectNode t = (GenericTreeObjectNode) c.getSelectedNode();
		try {
			ServiceSessionProxy session = new ServiceSessionProxy(t
					.getServiceSession());
			String[] selectedColumns = { "Responsability" };
			String[] result = session.getSelectResult("resp", selectedColumns,
					"", "", t.getContext(true));

			if (result != null && result.length >= 2) {
				String[] responsabilityList = new String[result.length - 1];

				System.arraycopy(result, 1, responsabilityList, 0,
						result.length - 1);

				ListDialog dialog = new ListDialog(
					_window.getMainWindowInterfaceFromProcessorFrame(),
					MessageManager.getMessage(
					"&ComponentConfiguration_ResponsabilitiesList"));

				String valuesList = dialog.getSelectedInfo(responsabilityList,
						_responsabilitiesTextField.getText());
				if (!valuesList.equals("")) {
					_responsabilitiesTextField.setText(valuesList);

				}
			}
		} catch (Exception e) {

		}
	}
}
