//------------------------------------------------------------
// Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
//------------------------------------------------------------
//
// FICHIER: VariableDescriptionPanel
// VERSION: 1.0
//
//----------------------------------------------------------
// DESCRIPTION: Troisi�me panneau de l'assistant
// DATE: 04/06/08
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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentTransitionGroups;

public class StateDescriptionPanel extends AbstractPanel {

	private static final long serialVersionUID = 1L;

	// ******************* PUBLIC **********************

	/*------------------------------------------------------------
	 * Nom: StateDescriptionPanel
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * Elle appelle la m�thode makePanel() charg�e de la cr�ation des objets
	 * graphiques a afficher dans le panneau.	
	 * ------------------------------------------------------------*/
	public StateDescriptionPanel(WindowInterface mainWindow) {
		super();

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"StateDescriptionPanel", "StateDescriptionPanel");
		trace_methods.beginningOfMethod();

		_window = mainWindow;
		// Cr�ation de l'interface graphique
		makePanel();

		trace_methods.endOfMethod();

	}

	/*------------------------------------------------------------
	 * Nom: beforeDisplay
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle est appel�e par la classe AbstractWindow lorsque le panneau doit �tre
	 * affich�. A partir du mod�le de donn�es pass� en param�tre, elle se charge
	 * de pr�-renseigner les champs du panneau.
	 * 
	 * Param�tres :
	 *  - modele : Le tableau de ModelInterface.
	 * ------------------------------------------------------------*/
	public void beforeDisplay(ArrayList<ModelInterface> modele) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"StateDescriptionPanel", "beforeDisplay");
		trace_methods.beginningOfMethod();

		_tableModel = new StateTableModel();

		if (modele != null) {
			for (int i = 0; i < modele.size(); i++) {
				if (modele.get(i) instanceof ComponentStates) {
				ComponentStates c = (ComponentStates) modele.get(i);
					if (!c.getStateName().equals(MessageManager.getMessage(
						"&ComponentConfiguration_AnyState")) == true)
						if (c.getFlag() != 'S')
							_tableModel.addElement(c);
						else
							_tableModel.addDeleteElement(c);
				}
			}
		}
		_stateTable.setModel(_tableModel);
		trace_methods.endOfMethod();
	}

	/*------------------------------------------------------------
	 * Nom: afterDisplay
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean afterDisplay() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"StateDescriptionPanel", "afterDisplay");

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
	 * ------------------------------------------------------------*/
	public boolean beforeHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"StateDescriptionPanel", "beforeHide");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	/*------------------------------------------------------------
	 * Nom: beforeHide
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Cette m�thode d�finit le comportement du panneau apr�s avoir �t� cach�.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean afterHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"StateDescriptionPanel", "afterHide");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	/*----------------------------------------------------------
	 * Nom: end
	 * 
	 * Description:
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Cette m�thode est appel�e lors de la destruction de l'assistant. 
	 * Elle est utilis�e pour lib�rer l'espace m�moire utilis� par 
	 * les variables des classes.
	 *
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false sinon)
	 * ----------------------------------------------------------*/
	public boolean end() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"StateDescriptionPanel", "end");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*------------------------------------------------------------
	 * Nom: update
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle est appel�e par la classe AbstractWindow et est en charge de 
	 * m�moriser les donn�es saisis par l'utilisateur dans le mod�le de 
	 * donn�es pass�s en param�tre.
	 * 
	 * Param�tre : 
	 *  - tabModels : Le mod�le de donn�es avant modification.
	 * 
	 * Retourne : Le nouveau mod�le de donn�es.
	 * ------------------------------------------------------------*/
	public ArrayList<ModelInterface> update(ArrayList<ModelInterface> tabModels) {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"StateDescriptionPanel", "update");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tabModels=" + tabModels);

		ArrayList<ComponentStates> deletedElements = _tableModel
				.getDeleteDatas();
		ArrayList<ComponentStates> newElements = _tableModel.getDatas();

		if (tabModels != null) {
			// On supprime toutes les anciens �tats
			for (int index = 0; index < tabModels.size(); index++) {
				if (tabModels.get(index) instanceof ComponentStates) {
					ComponentStates c = (ComponentStates) tabModels.get(index);
					if (!c.getStateName().equals(MessageManager.getMessage(
						"&ComponentConfiguration_AnyState")) == true) {
						tabModels.remove(index);
						// On recule d'une case dans le tableau pour ne pas
						// oublier l'�l�ment d�cal�
						index--;
					}
				}
				// Les groupes de transitions et transitions d�pendent des �tats
				// sp�cifi�s
				// ici donc on doit v�rifier leur conformit� avec les �tats
				// saisies
				else if (tabModels.get(index) instanceof ComponentTransitionGroups) {
					ComponentTransitionGroups ctg = (ComponentTransitionGroups) tabModels
							.get(index);
					if (ctg.getFlag() != 'S') {
						// Pour un groupe de transitions, si son �tat d'arriv�e
						// est un �tat supprim�
						// on supprime ce groupe de transition
						for (int indexDeleteElem = 0; indexDeleteElem < deletedElements
								.size(); indexDeleteElem++) {
							if (deletedElements.get(indexDeleteElem)
									.getStateName().equals(
											ctg.getEndState().getStateName())) {
								if (ctg.getFlag() == 'A') {
									tabModels.remove(index);
									index--;
								} else {
									ctg.setFlag('S');
								}
							}
							// Si l'�tat d'arriv� du groupe ne correspond pas,
							// on v�rifie
							// si l'une de ses transitions sp�cifiques ne part
							// pas d'un �tat supprim�
							else {
								ArrayList<ComponentTransition> array = null;
								if (ctg.getFlag() == 'E')
									array = ctg.getSpecificTransition();
								else
									array = ctg.getNewSpecificTransition();
								if (array != null) {
									// Si c'est le cas, on supprime la
									// transition sp�cifiques
									for (int indexArray = 0; indexArray < array
											.size(); indexArray++) {
										ComponentTransition ct = array
												.get(indexArray);
										if (deletedElements
												.get(indexDeleteElem)
												.getStateName()
												.equals(
													ct.getStartState()
															.getStateName())) {
											if (ct.getFlag() == 'A') {
												array.remove(indexArray);
												indexArray--;
											} else {
												ct.setFlag('S');
											}

											// On met � jour le groupe de
											// transitions
											if (ctg.getFlag() == 'E') {
												ctg.setNewTransitionName(ctg
														.getTransitionName());
												ctg.setNewDescription(ctg
														.getDescription());
												ctg.setNewTransitionType(ctg
														.getTransitionType());
												ctg.setNewResponsabilities(ctg
														.getResponsabilities());
												ctg.setFlag('M');
											}
										}
									}
									// Une fois toutes les modifications faites,
									// on met � jour le
									// groupe de transitions
									ctg.setNewSpecificTransition(array);
								}
							}
						}
					}
				}
			}
		} else
			// Le tableau �tait inexistant, on le cr�e
			tabModels = new ArrayList<ModelInterface>();

		// On ajoute les nouveaux �tats au tableau de ModelInterface
		tabModels.addAll(deletedElements);
		tabModels.addAll(newElements);

		trace_methods.endOfMethod();
		return tabModels;
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

					onClickApplyButton();
				} else
					fieldsHaveChanged();
			}
		});

		trace_methods.endOfMethod();
	}

	// ****************** PRIVE *********************

	/*------------------------------------------------------------
	 * Nom : _variableTable
	 * 
	 * Description :
	 * Cet attribut est la table ou seront mis le donn�es relatives 
	 * aux variables d'un composant: le nom, la description et la valeur
	 * par default. 
	 * ------------------------------------------------------------*/
	private JTable _stateTable;

	/*------------------------------------------------------------
	 * Nom : _nameTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour le nom d'un
	 * �tat dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _nameTextField;

	/*------------------------------------------------------------
	 * Nom : _descriptionTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour la description
	 * d'un �tat dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _descriptionTextField;

	/*------------------------------------------------------------
	 * Nom : _commandTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour la commande
	 * d'un �tat dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _commandTextField;

	/*------------------------------------------------------------
	 * Nom : _returnStringPatternTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour la chaine
	 * de retour d'un �tat dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _returnStringPatternTextField;

	/*------------------------------------------------------------
	 * Nom : _descriptionTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour le code de retour
	 * d'un �tat dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _returnCodePatternTextField;

	/*------------------------------------------------------------
	 * Nom : _applyButton
	 * 
	 * Description :
	 * Cet attribut d�finit un boutton, utilis� pour
	 * enregistrer les informations saisies dans le sous panneau de 
	 * saisie, dans le tableau des �tats.
	 * ------------------------------------------------------------*/
	private JButton _applyButton;

	/*------------------------------------------------------------
	 * Nom : _removeButton
	 * 
	 * Description :
	 * Cet attribut d�finit un boutton, utilis� pour
	 * supprimer la ligne s�lectionner dans le tableau.
	 * ------------------------------------------------------------*/
	private JButton _removeButton;

	/*------------------------------------------------------------
	 * Nom : _newButton
	 * 
	 * Description :
	 * Cet attribut d�finit le boutton utilis� pour la saisie d'un 
	 * nouveau �tat.
	 * ------------------------------------------------------------*/
	private JButton _newButton;

	/*------------------------------------------------------------
	 * Nom : _returnStringButton
	 * 
	 * Description :
	 * Cet attribut d�finit un boutton, utilis� pour d�finir l'expression
	 * r�guli�re associ� a la chaine de retour
	 * ------------------------------------------------------------*/
	private JButton _returnStringButton;

	/*------------------------------------------------------------
	 * Nom : _returnCodeButton
	 * 
	 * Description :
	 * Cet attribut d�finit un boutton, utilis� pour d�finir l'expression
	 * r�guli�re associ� au code de retour
	 * ------------------------------------------------------------*/
	private JButton _returnCodeButton;

	private class StateTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;
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
		private ArrayList<ComponentStates> _datas;

		/*------------------------------------------------------------
		 * Nom : _datas
		 * 
		 * Description :
		 * Cet attribut est un tableau de ComponentVariable.
		 * Chaque �l�ment du tableau repr�sente un �l�ment supprim� 
		 * par l'utilisateur, gard� en m�moire pour le supprimer des tables 
		 * de donn�es.
		 * ------------------------------------------------------------*/
		private ArrayList<ComponentStates> _deleteDatas;

		/*------------------------------------------------------------
		 * Nom : StateTableModel
		 * 
		 * Description :
		 * Cette m�thode est le contructeur de la classe.
		 * Son r�le est de cr�er les deux tableaux de titres et de donn�es.
		 * Pour le tableau des titres, elle le remplira � partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/
		public StateTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "StateTableModel");
			trace_methods.beginningOfMethod();

			// Cr�ation du tableau des titres
			_titles = new String[5];
			// La premi�re colonne repr�sentera le nom de l'�tat
			_titles[0] = MessageManager
					.getMessage("&ComponentConfiguration_Name");
			// La deuxi�me colonne repr�sentera la description associ�e
			// � l'�tat
			_titles[1] = MessageManager
					.getMessage("&ComponentConfiguration_Description");
			// La troisi�me colonne repr�sentera le commande associ�e
			// � l'�tat
			_titles[2] = MessageManager
					.getMessage("&ComponentConfiguration_Command");
			// La quatri�me colonne repr�sentera le code de retour associ�e
			// � l'�tat
			_titles[3] = MessageManager
					.getMessage("&ComponentConfiguration_ReturnCodePattern");
			// La cinqui�me colonne repr�sentera le code de retour associ�e
			// � l'�tat
			_titles[4] = MessageManager
					.getMessage("&ComponentConfiguration_ReturnStringPattern");

			// Cr�ation du tableau de donn�es
			_datas = new ArrayList<ComponentStates>();
			_deleteDatas = new ArrayList<ComponentStates>();
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
					"StateTableModel", "getColumnCount");
			trace_methods.beginningOfMethod();

			if (_titles != null)
				return _titles.length;
			else
				return 0;
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
					"StateTableModel", "getRowCount");
			trace_methods.beginningOfMethod();

			if (_datas != null) {
				trace_methods.endOfMethod();
				return _datas.size();
			} else
				trace_methods.endOfMethod();
			return 0;
		}

		/*------------------------------------------------------------
		 * Nom : getDatas
		 * 
		 * Description : 
		 * Cette m�thode retourne le tableau de ComponentStates contenant
		 * l'ensemble des informations saisies par l'utilisateur.
		 * 
		 * Retourne : Un tableau de ComponentTransition
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentStates> getDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "getDatas");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();

			return _datas;
		}

		/*------------------------------------------------------------
		 * Nom : findRow
		 * 
		 * Description : 
		 * Cette m�thode recherche la pr�sence d'un �tat dans la JTable
		 * � partir du nom de l'�tat pass� en param�tre. Elle retourne
		 * le num�ro de la ligne correspondant � cet �tat si elle existe,
		 * -1 sinon.
		 * 
		 * Argument :
		 *  - actionName : une cha�ne de caract�res corespondant au nom
		 *    de l'�tat recherch�
		 *    
		 * Retourne : un entier : le num�ro de ligne de l'�tat, -1 si 
		 * elle n'existe pas.
		 * ------------------------------------------------------------*/
		public int findRow(String stateName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("stateName=" + stateName);

			if (_datas != null) {
				for (int index = 0; index < _datas.size(); index++) {
					if (_datas.get(index).getStateName().equals(stateName)) {
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
		 * Le num�ro de ligne correspond � un �tat particulier.
		 * Le num�ro de colonne indique le champ : 
		 * 	- 0 pour le nom de l'�tat
		 *  - 1 pour sa description
		 *  - 2 pour sa commande
		 *  - 3 pour son code de retour
		 *  - 4 pour sa chaine de retour
		 * 
		 * Arguments :
		 *  - line : le num�ro de ligne
		 *  - column : la num�ro de la colonne
		 *    
		 * Retourne : l'�lement � la position souhait�e, null si rien 
		 * n'est trouv�.
		 * ------------------------------------------------------------*/
		public Object getValueAt(int line, int column) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "getValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				ComponentStates cs = _datas.get(line);
				if (cs != null) {
					if (column == 0) {
						trace_methods.endOfMethod();
						return cs.getStateName();
					} else if (column == 1) {
						if (cs.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return cs.getDescription();
						} else {
							trace_methods.endOfMethod();
							return cs.getNewDescription();
						}
					} else if (column == 2) {
						if (cs.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return cs.getCommand();
						} else {
							trace_methods.endOfMethod();
							return cs.getNewCommand();
						}
					} else if (column == 3) {
						if (cs.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return cs.getReturnCodePattern();
						} else {
							trace_methods.endOfMethod();
							return cs.getNewReturnCodePattern();
						}
					} else {
						if (cs.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return cs.getReturnStringPattern();
						} else {
							trace_methods.endOfMethod();
							return cs.getNewReturnStringPattern();
						}
					}
				}
			}
			trace_methods.endOfMethod();
			return null;
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
					"StateTableModel", "getColumnName");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("col=" + col);

			if (_titles != null && _titles.length > col) {
				trace_methods.endOfMethod();
				return _titles[col];
			}

			trace_methods.endOfMethod();
			return "";
		}

		/*------------------------------------------------------------
		 * Nom : addElement
		 * 
		 * Description : 
		 * Cette m�thode ajoute un nouvel �l�ment (un �tat) au 
		 * tableau de donn�es. 
		 * 
		 * Argument : 
		 *  - element : L'�l�ment � ajouter
		 * ------------------------------------------------------------*/
		public void addElement(ComponentStates element) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "addElement");
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
		 * Cette m�thode supprime un �l�ment (un �tat) au tableau 
		 * de donn�es. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � supprimer
		 * ------------------------------------------------------------*/
		public void removeElement(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "removeElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);

			if (_datas != null) {
				_datas.remove(line);
				super.fireTableRowsDeleted(line, line);
			}
			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getElementAt
		 * 
		 * Description : 
		 * Cette m�thode retourne le ComponentStates de la ligne 
		 * demand�e. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � r�cup�rer
		 *  
		 * Retourne : Le ComponentStates de la ligne s�lectionn�e.
		 * ------------------------------------------------------------*/
		public ComponentStates getElementAt(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "getElementAt");
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
		 * base, le ComponentStates pass� en param�tre. 
		 * 
		 * Argument : 
		 *  - state : Le ComponentStates qu'il faudra supprimer 
		 *    de la base de donn�es.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(ComponentStates state) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "addDeleteElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("state=" + state);

			if (_deleteDatas != null)
				_deleteDatas.add(state);

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : removeDeleletElement
		 * 
		 * Description : 
		 * Cette m�thode retire au tableau des �l�ments � supprimer de la
		 * base, le ComponentStates d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentStates qu'il faut retirer 
		 *    du tableau.
		 * ------------------------------------------------------------*/
		public void removeDeleletElement(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "removeDeleletElement");
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
		 * base, le ComponentStates d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentStates qu'il faut r�cup�rer
		 *    dans le tableau.
		 * 
		 * Retourne : Le ComponentStates supprim� de la ligne
		 * souhait�e.
		 * ------------------------------------------------------------*/
		public ComponentStates getDeleteElementAt(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "getDeleteElementAt");
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
		 * ComponentStates � supprimer de la base de donn�es. 
		 * 
		 * Retourne : Le tableau des ComponentStates � supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentStates> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"StateTableModel", "getDeleteDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _deleteDatas;
		}
	}

	/*------------------------------------------------------------
	 * Nom : _tableModel
	 * 
	 * Description :
	 * Cette attribut maintient une r�f�rence sur le mod�le associ� �
	 * la JTable.
	 * ------------------------------------------------------------*/
	private StateTableModel _tableModel;

	private void makePanel() {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"StateDescriptionPanel", "makePanel");
		trace_methods.beginningOfMethod();

		_tableModel = null;
		_stateTable = new JTable(_tableModel);
		_stateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_stateTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {

						onSelectionElement(event);
					}
				});
		// On int�gre un d�filement dans le tableau des �tats
		JScrollPane scrollPaneStateTable = new JScrollPane();
		scrollPaneStateTable.setViewportView(_stateTable);

		// On pr�sente le panneau au moyen d'une phrase descriptive
		JLabel firstdescriptionSentence = new JLabel();
		firstdescriptionSentence.setMinimumSize(new Dimension(450, 20));
		firstdescriptionSentence.setMaximumSize(new Dimension(450, 20));
		firstdescriptionSentence
				.setText(MessageManager
						.getMessage("&ComponentConfiguration_FirstDescriptionSentenceState"));

		// On d�finit un label pour le champ "Nom"
		JLabel nameLabel = new JLabel();
		nameLabel.setMinimumSize(new Dimension(115, 20));
		nameLabel.setMaximumSize(new Dimension(115, 20));
		nameLabel.setPreferredSize(new Dimension(115, 20));
		nameLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Name")
				+ " :   *");

		// On d�finit un label pour le champ "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setMinimumSize(new Dimension(115, 20));
		descriptionLabel.setMaximumSize(new Dimension(115, 20));
		descriptionLabel.setPreferredSize(new Dimension(115, 20));
		descriptionLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Description")
				+ " : ");
		// On d�finit un label pour le champ "commande"
		JLabel commandLabel = new JLabel();
		commandLabel.setMinimumSize(new Dimension(115, 20));
		commandLabel.setMaximumSize(new Dimension(115, 20));
		commandLabel.setPreferredSize(new Dimension(115, 20));
		commandLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Command")
				+ " :   *");

		// On d�finit un label pour le champ "code de retour"
		JLabel returnCodePatternLabel = new JLabel();
		returnCodePatternLabel.setMinimumSize(new Dimension(115, 20));
		returnCodePatternLabel.setMaximumSize(new Dimension(115, 20));
		returnCodePatternLabel.setPreferredSize(new Dimension(115, 20));
		returnCodePatternLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_ReturnCodePattern")
				+ " :   *");

		// On d�finit un label pour le champ "chaine de retour"
		JLabel returnStringPatternLabel = new JLabel();
		returnStringPatternLabel.setMinimumSize(new Dimension(115, 20));
		returnStringPatternLabel.setMaximumSize(new Dimension(115, 20));
		returnStringPatternLabel.setPreferredSize(new Dimension(115, 20));
		returnStringPatternLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_ReturnStringPattern")
				+ " : ");

		// On d�finit un label pour indiquer que l'asterisque en fin des noms
		// de champs sont obligatoires pour la saisie.
		JLabel obligatoryFieldsLabel = new JLabel();
		obligatoryFieldsLabel.setMinimumSize(new java.awt.Dimension(200, 20));
		obligatoryFieldsLabel.setMaximumSize(new java.awt.Dimension(200, 20));
		obligatoryFieldsLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_ObligatoryFields"));

		_nameTextField = new JTextField(20);
		setEnterCallback(_nameTextField);
		_nameTextField.setMinimumSize(new Dimension(360, 20));
		_nameTextField.setMaximumSize(new Dimension(360, 20));
		_nameTextField.setPreferredSize(new Dimension(360, 20));

		_descriptionTextField = new JTextField(20);
		setEnterCallback(_descriptionTextField);
		_descriptionTextField.setMinimumSize(new Dimension(370, 20));
		_descriptionTextField.setMaximumSize(new Dimension(370, 20));
		_descriptionTextField.setPreferredSize(new Dimension(370, 20));

		_commandTextField = new JTextField(20);
		setEnterCallback(_commandTextField);
		_commandTextField.setMinimumSize(new Dimension(370, 20));
		_commandTextField.setMaximumSize(new Dimension(370, 20));
		_commandTextField.setPreferredSize(new Dimension(370, 20));

		_returnCodePatternTextField = new JTextField(20);
		setEnterCallback(_returnCodePatternTextField);
		_returnCodePatternTextField.setMinimumSize(new Dimension(370, 20));
		_returnCodePatternTextField.setMaximumSize(new Dimension(370, 20));
		_returnCodePatternTextField.setPreferredSize(new Dimension(370, 20));

		_returnStringPatternTextField = new JTextField(20);
		setEnterCallback(_returnStringPatternTextField);
		_returnStringPatternTextField.setMinimumSize(new Dimension(370, 20));
		_returnStringPatternTextField.setMaximumSize(new Dimension(370, 20));
		_returnStringPatternTextField.setPreferredSize(new Dimension(370, 20));

		_applyButton = new JButton();
		_applyButton.setEnabled(false);
		_applyButton.setMinimumSize(new Dimension(120, 21));
		_applyButton.setMaximumSize(new Dimension(120, 21));
		_applyButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_ApplyButton"));
		_applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickApplyButton();
				_applyButton.setEnabled(false);
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

		_returnCodeButton = new JButton();
		_returnCodeButton.setMinimumSize(new Dimension(30, 22));
		_returnCodeButton.setMaximumSize(new Dimension(30, 22));
		_returnCodeButton.setText("...");
		_returnCodeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickReturnCodeButton();
			}
		});

		_returnStringButton = new JButton();
		_returnStringButton.setMinimumSize(new Dimension(30, 22));
		_returnStringButton.setMaximumSize(new Dimension(30, 22));
		_returnStringButton.setText("...");
		_returnStringButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickReturnStringButton();
			}
		});

		Box hBox1 = Box.createHorizontalBox();
		hBox1.setMinimumSize(new java.awt.Dimension(490, 20));
		hBox1.setMaximumSize(new java.awt.Dimension(490, 20));
		hBox1.add(Box.createHorizontalStrut(5));
		hBox1.add(firstdescriptionSentence);
		hBox1.add(Box.createHorizontalStrut(5));

		Box hBox2 = Box.createHorizontalBox();
		hBox2.setMinimumSize(new Dimension(490, 120));
		hBox2.setMaximumSize(new Dimension(490, 120));
		hBox2.add(Box.createHorizontalStrut(5));
		hBox2.add(scrollPaneStateTable);
		hBox2.add(Box.createHorizontalStrut(5));

		Box hBox4 = Box.createHorizontalBox();
		hBox4.setMinimumSize(new Dimension(490, 20));
		hBox4.setMaximumSize(new Dimension(490, 20));
		hBox4.add(Box.createHorizontalStrut(5));
		hBox4.add(nameLabel);
		hBox4.add(Box.createHorizontalStrut(10));
		hBox4.add(_nameTextField);
		hBox4.add(Box.createHorizontalStrut(5));

		Box hBox5 = Box.createHorizontalBox();
		hBox5.setMinimumSize(new Dimension(490, 20));
		hBox5.setMaximumSize(new Dimension(490, 20));
		hBox5.add(Box.createHorizontalStrut(5));
		hBox5.add(descriptionLabel);
		hBox5.add(Box.createHorizontalStrut(10));
		hBox5.add(_descriptionTextField);
		hBox5.add(Box.createHorizontalStrut(5));

		Box hBox6 = Box.createHorizontalBox();
		hBox6.setMinimumSize(new Dimension(490, 21));
		hBox6.setMaximumSize(new Dimension(490, 21));
		hBox6.add(Box.createHorizontalStrut(5));
		hBox6.add(commandLabel);
		hBox6.add(Box.createHorizontalStrut(10));
		hBox6.add(_commandTextField);
		hBox6.add(Box.createHorizontalStrut(5));

		Box hBox7 = Box.createHorizontalBox();
		hBox7.setMinimumSize(new Dimension(490, 22));
		hBox7.setMaximumSize(new Dimension(490, 22));
		hBox7.add(Box.createHorizontalStrut(5));
		hBox7.add(returnCodePatternLabel);
		hBox7.add(Box.createHorizontalStrut(10));
		hBox7.add(_returnCodePatternTextField);
		hBox7.add(Box.createHorizontalStrut(10));
		hBox7.add(_returnCodeButton);
		hBox7.add(Box.createHorizontalStrut(5));

		Box hBox8 = Box.createHorizontalBox();
		hBox8.setMinimumSize(new Dimension(490, 22));
		hBox8.setMaximumSize(new Dimension(490, 22));
		hBox8.add(Box.createHorizontalStrut(5));
		hBox8.add(returnStringPatternLabel);
		hBox8.add(Box.createHorizontalStrut(10));
		hBox8.add(_returnStringPatternTextField);
		hBox8.add(Box.createHorizontalStrut(10));
		hBox8.add(_returnStringButton);
		hBox8.add(Box.createHorizontalStrut(5));

		Box hBox9 = Box.createHorizontalBox();
		hBox9.setMinimumSize(new Dimension(490, 21));
		hBox9.setMaximumSize(new Dimension(490, 21));
		hBox9.add(Box.createHorizontalStrut(90));
		hBox9.add(_newButton);
		hBox9.add(Box.createHorizontalStrut(10));
		hBox9.add(_removeButton);
		hBox9.add(Box.createHorizontalStrut(10));
		hBox9.add(_applyButton);
		hBox9.add(Box.createHorizontalStrut(5));

		Box hBox10 = Box.createHorizontalBox();
		hBox10.setMinimumSize(new Dimension(490, 20));
		hBox10.setMaximumSize(new Dimension(490, 20));
		hBox10.add(Box.createHorizontalStrut(10));
		hBox10.add(obligatoryFieldsLabel);

		Box vBox1 = Box.createVerticalBox();
		vBox1.setMinimumSize(new Dimension(486, 180));
		vBox1.setMaximumSize(new Dimension(486, 180));
		vBox1.setPreferredSize(new Dimension(486, 180));
		vBox1.add(Box.createVerticalStrut(14));
		vBox1.add(hBox1);
		vBox1.add(Box.createVerticalStrut(15));
		vBox1.add(hBox2);

		Box vBox = Box.createVerticalBox();
		vBox.setMinimumSize(new Dimension(480, 245));
		vBox.setMaximumSize(new Dimension(480, 245));
		vBox.setPreferredSize(new Dimension(480, 245));
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox4);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox5);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox6);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox7);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox8);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox9);
		vBox.add(Box.createVerticalStrut(10));
		vBox.add(hBox10);
		vBox
				.setBorder(BorderFactory
						.createTitledBorder((MessageManager
								.getMessage("&ComponentConfiguration_SecondDescriptionSentence"))));

		Box vBox3 = Box.createVerticalBox();
		vBox3.add(vBox1);
		vBox3.add(Box.createVerticalStrut(5));
		vBox3.add(vBox);
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
		String description = _descriptionTextField.getText();
		String command = _commandTextField.getText();
		String returnCodePattern = _returnCodePatternTextField.getText();
		String returnStringPattern = _returnStringPatternTextField.getText();

		if (name.equals("") || command.equals("")) {
			DialogManager.displayDialog("Information", MessageManager
					.getMessage("&ComponentConfiguration_MessageInformation"),
					null, null);
		} else {
			/*
			 * 1 : V�rifier qu'il n'y ai pas d'�tats avec le m�me nom 2 : Mettre
			 * � jour le tableau 3 : Effacer les champs 4 : actualiser le
			 * tableau
			 */

			int row = _tableModel.findRow(name);
			if (row == -1) {

				char flag = 'A';
				ComponentStates cs = new ComponentStates();

				ArrayList<ComponentStates> deleteElement = _tableModel
						.getDeleteDatas();
				if (deleteElement != null) {
					for (int index = 0; index < deleteElement.size(); index++) {

						if (deleteElement.get(index).getStateName()
								.equals(name)) {
							flag = 'M';
							cs = deleteElement.get(index);
							deleteElement.remove(index);
							break;
						}
					}
				}

				cs.setStateName(name);
				cs.setNewDescription(description);
				cs.setNewCommand(command);
				cs.setNewReturnCodePattern(returnCodePattern);
				cs.setNewReturnStringPattern(returnStringPattern);
				cs.setFlag(flag);
				_tableModel.addElement(cs);

				_nameTextField.setText("");
				_descriptionTextField.setText("");
				_commandTextField.setText("");
				_returnCodePatternTextField.setText("0");
				_returnStringPatternTextField.setText("");

			} else {

				// L'�tat existe donc on la met � jour
				int selectedRow = _stateTable.getSelectedRow();

				if (selectedRow == -1) {
					DialogManager
							.displayDialog(
									"Information",
									MessageManager
											.getMessage("&ComponentConfiguration_MessageInformationState"),
									null, null);
				} else {
					ComponentStates cs = _tableModel.getElementAt(row);
					cs.setNewDescription(description);
					cs.setNewCommand(command);
					cs.setNewReturnCodePattern(returnCodePattern);
					cs.setNewReturnStringPattern(returnStringPattern);

					if (cs.getFlag() == 'E')
						cs.setFlag('M');

					// On recharge le tableau et on le r�affiche
					_tableModel.fireTableRowsUpdated(row, row);

					// On efface les champs de saisie
					_nameTextField.setText("");
					_descriptionTextField.setText("");
					_commandTextField.setText("");
					_returnCodePatternTextField.setText("0");
					_returnStringPatternTextField.setText("");
					_stateTable.getSelectionModel().clearSelection();

				}
			}
		}
	}

	/*------------------------------------------------------------
	 * Nom : onClickClearButton
	 * 
	 * Description :
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * le bouton 'Effacer' de la zone de saisie. Son r�le est de 
	 * supprimer la ligne s�lectionn�e du tableau.
	 *------------------------------------------------------------*/
	private void onClickRemoveButton() {

		int selectedRow = _stateTable.getSelectedRow();

		// Si une ligne du tableau est s�lectionn�e
		if (selectedRow != -1) {

			ComponentStates cc = _tableModel.getElementAt(selectedRow);
			if (cc != null) {
				if (cc.getFlag() == 'A')
					_tableModel.removeElement(selectedRow);
				else if (cc.getFlag() == 'E' || cc.getFlag() == 'M') {
					_tableModel.removeElement(selectedRow);
					cc.setFlag('S');
					_tableModel.addDeleteElement(cc);
				} else {

				}
			}
			_nameTextField.setText("");
			_descriptionTextField.setText("");
			_commandTextField.setText("");
			_returnCodePatternTextField.setText("");
			_returnStringPatternTextField.setText("");

			_stateTable.getSelectionModel().clearSelection();

			// On d�sactive le bouton supprimer
			_removeButton.setEnabled(false);

		}
	}

	/*------------------------------------------------------------
	 * Nom : onClickClearButton
	 * 
	 * Description :
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * le bouton 'Nouveau' de la zone de saisie. Son r�le est d'effacer 
	 * le contenu de la zone de saisie ainsi que la ligne s�lectionner 
	 * dans le tableau afin de permettre une nouvelle saisie.
	 *------------------------------------------------------------*/
	private void onClickNewButton() {

		_nameTextField.setText("");
		_descriptionTextField.setText("");
		_commandTextField.setText("");
		_returnCodePatternTextField.setText("");
		_returnStringPatternTextField.setText("");

		_stateTable.getSelectionModel().clearSelection();

	}

	private void onClickReturnCodeButton() {

		ArrayList<String[]> infos = new ArrayList<String[]>();
		String[] value = new String[2];
		value[0] = MessageManager.getMessage(
			"&ComponentConfiguration_EqualTo");
		value[1] = "==";
		infos.add(value);
		String[] value1 = new String[2];
		value1[0] = MessageManager.getMessage(
			"&ComponentConfiguration_NotEqualTo");
		value1[1] = "!=";
		infos.add(value1);
		String[] value2 = new String[2];
		value2[0] = MessageManager.getMessage(
			"&ComponentConfiguration_GreaterThan");
		value2[1] = ">";
		infos.add(value2);
		String[] value3 = new String[2];
		value3[0] = MessageManager.getMessage(
			"&ComponentConfiguration_GreaterOrEqualTo");
		value3[1] = ">=";
		infos.add(value3);
		String[] value4 = new String[2];
		value4[0] = MessageManager.getMessage(
			"&ComponentConfiguration_LessThan");
		value4[1] = "<";
		infos.add(value4);
		String[] value5 = new String[2];
		value5[0] = MessageManager.getMessage(
			"&ComponentConfiguration_LessOrEqualTo");
		value5[1] = "<=";
		infos.add(value5);

		SelectionDialog dialog = new SelectionDialog(
			_window.getMainWindowInterfaceFromProcessorFrame(), infos,
			MessageManager.getMessage(
			"&ComponentConfiguration_ReturnCodePattern"));
		_returnCodePatternTextField.setText(
			dialog.getSelectedInfo(_returnCodePatternTextField.getText()));
		fieldsHaveChanged();
	}

	private void onClickReturnStringButton() {
		ArrayList<String[]> infos = new ArrayList<String[]>();
		String[] value = new String[3];
		value[0] = MessageManager.getMessage(
			"&ComponentConfiguration_EqualTo");
		value[1] = "eq";
		value[2] = "\"";
		infos.add(value);
		String[] value1 = new String[3];
		value1[0] = MessageManager.getMessage(
			"&ComponentConfiguration_NotEqualTo");
		value1[1] = "ne";
		value1[2] = "\"";
		infos.add(value1);
		String[] value2 = new String[3];
		value2[0] = MessageManager.getMessage(
			"&ComponentConfiguration_Contains");
		value2[1] = "=~";
		value2[2] = "/";
		infos.add(value2);
		String[] value3 = new String[3];
		value3[0] = MessageManager.getMessage(
			"&ComponentConfiguration_NotContains");
		value3[1] = "!~";
		value3[2] = "/";
		infos.add(value3);

		SelectionDialog dialog = new SelectionDialog(
			_window.getMainWindowInterfaceFromProcessorFrame(), infos,
			MessageManager.getMessage(
			"&ComponentConfiguration_ReturnStringPattern"));
		_returnStringPatternTextField.setText(
			dialog.getSelectedInfo(_returnStringPatternTextField.getText()));
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
			_nameTextField.setText("");
			_nameTextField.setEnabled(true);
			_descriptionTextField.setText("");
			_commandTextField.setText("");
			_returnCodePatternTextField.setText("");
			_returnStringPatternTextField.setText("");

			_applyButton.setEnabled(false);
			_removeButton.setEnabled(false);

		} else {

			ComponentStates cs = _tableModel.getElementAt(index);
			_nameTextField.setText(cs.getStateName());
			if (cs.getFlag() == 'E') {
				_descriptionTextField.setText(cs.getDescription());
				_commandTextField.setText(cs.getCommand());
				_returnCodePatternTextField.setText(cs.getReturnCodePattern());
				_returnStringPatternTextField.setText(cs
						.getReturnStringPattern());
			} else {
				_descriptionTextField.setText(cs.getNewDescription());
				_commandTextField.setText(cs.getNewCommand());
				_returnCodePatternTextField.setText(cs
						.getNewReturnCodePattern());
				_returnStringPatternTextField.setText(cs
						.getNewReturnStringPattern());
			}
			_nameTextField.setEnabled(false);
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

		if (_nameTextField.getText().equals("")
				|| _commandTextField.getText().equals("")
				|| _returnCodePatternTextField.getText().equals("")) {

			_applyButton.setEnabled(false);
			return;
		}
		// Ok, on peut valider le bouton
		_applyButton.setEnabled(true);
		trace_methods.endOfMethod();
	}

}
