/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
 * ------------------------------------------------------------
 *
 * $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/VariableDescriptionPanel.java,v $
 * $Revision: 1.27 $
 *
 * ------------------------------------------------------------
 * DESCRIPTION: Panneau de saisie des informations s�pcifiques d'une transition
 * DATE:        13/06/2008
 * AUTEUR:      Hicham Doghmi & Florent Cossard
 * PROJET:      I-SIS
 * GROUPE:      processor.impl.config
 * ------------------------------------------------------------*/

package com.bv.isis.console.impl.processor.config.implementation.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.*;
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
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentVariable;

public class VariableDescriptionPanel extends AbstractPanel {

	private static final long serialVersionUID = 1L;

	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: VariableDescriptionPanel
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * Elle appelle la m�thode makePanel() charg�e de la cr�ation des objets
	 * graphiques � afficher dans le panneau.	
	 * ------------------------------------------------------------*/
	public VariableDescriptionPanel(WindowInterface mainWindow) {
		super();

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"VariableDescriptionPanel", "VariableDescriptionPanel");
		trace_methods.beginningOfMethod();

		_window = mainWindow;
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
				"VariableDescriptionPanel", "beforeDisplay");
		trace_methods.beginningOfMethod();

		_tableModel = new VariableTableModel();


		if (modele != null) {
			for (int i = 0; i < modele.size(); i++) {
				if (modele.get(i) instanceof ComponentVariable) {
					ComponentVariable c = (ComponentVariable) modele.get(i);
					if (c.getFlag() != 'S')
						_tableModel.addElement(c);
					else
						_tableModel.addDeleteElement(c);
				}
			}
		}
		_variableTable.setModel(_tableModel);
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
				"VariableDescriptionPanel", "afterDisplay");

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
	 * informations saisis par l'utilisateur. Si il y a une erreur, 
	 * elle retourne faux sinon vrai.
	 * 
	 * Retourne : vrai (true) si tout est conforme, faux (false) sinon.
	 * ------------------------------------------------------------*/
	public boolean beforeHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"VariableDescriptionPanel", "beforeHide");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*------------------------------------------------------------
	 * Nom: afterHide
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
				"VariableDescriptionPanel", "afterHide");

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
				"VariableDescriptionPanel", "end");

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
				"GenericTransitionDescriptionPanel", "update");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tabModels=" + tabModels);

		ArrayList<ComponentVariable> deletedElements = _tableModel
				.getDeleteDatas();
		ArrayList<ComponentVariable> newElements = _tableModel.getDatas();

		if (tabModels != null) {

			// On supprime toutes les anciennes variables
			for (int index = 0; index < tabModels.size(); index++) {
				if (tabModels.get(index) instanceof ComponentVariable) {
					tabModels.remove(index);
					// On recule d'une case dans le tableau pour ne pas oublier
					// l'�l�ment d�cal�
					index--;
				}
			}
		} else
			// Le tableau �tait inexistant, on le cr�e
			tabModels = new ArrayList<ModelInterface>();

		// On ajoute les nouvelles variables au tableau de ModelInterface
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

	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _variableTable
	 * 
	 * Description :
	 * Cet attribut est la table ou seront mis le donn�es relatives 
	 * aux variables d'un composant: le nom, la description et la valeur
	 * par default. 
	 * ------------------------------------------------------------*/
	private JTable _variableTable;

	/*------------------------------------------------------------
	 * Nom : _textName
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie du nom de la variable
	 * ------------------------------------------------------------*/
	private JTextField _nameTextField;

	/*------------------------------------------------------------
	 * Nom : _textDescription
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie de la description de 
	 * la variable
	 * ------------------------------------------------------------*/
	private JTextField _descriptionTextField;

	/*------------------------------------------------------------
	 * Nom : _name
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie de la valeur par d�faut
	 * de la variable
	 * ------------------------------------------------------------*/
	private JTextField _defaultValueTextField;

	/*------------------------------------------------------------
	 * Nom : _applyButton
	 * 
	 * Description :
	 * Cette attribut d�finit le boutton utilis� pour
	 * enregistrer les informations saisies dans le tableau des variables.
	 * ------------------------------------------------------------*/
	private JButton _applyButton;

	/*------------------------------------------------------------
	 * Nom : _removeButton
	 * 
	 * Description :
	 * Cet attribut d�finit le boutton utilis� pour
	 * supprimer la ligne s�lectionn�e dans le tableau.
	 * ------------------------------------------------------------*/
	private JButton _removeButton;

	/*------------------------------------------------------------
	 * Nom : _newButton
	 * 
	 * Description :
	 * Cet attribut d�finit le boutton utilis� pour la saisie d'une 
	 * nouvelle variable.
	 * ------------------------------------------------------------*/
	private JButton _newButton;

	/*------------------------------------------------------------
	 * Nom : VariableTableModel
	 * 
	 * Description :
	 * Cette classe repr�sente le modele associ� � la JTable.
	 * Elle d�rive de AbstractTableModel.
	 * Elle se caract�rise de 2 tableaux:
	 *  - un pour les titres des diverses colonnes
	 *  - un pour les donn�es de chaque ligne.
	 * Chaque ligne est une instance de ComponentManagemenVariable.
	 * ------------------------------------------------------------*/
	private class VariableTableModel extends AbstractTableModel {

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
		 * Cet attribut est un tableau de ComponentVariable.
		 * Chaque �l�ment du tableau repr�sente une ligne de la JTable
		 * et contient les informations d'une variable.
		 * ------------------------------------------------------------*/
		private ArrayList<ComponentVariable> _datas;

		/*------------------------------------------------------------
		 * Nom : _datas
		 * 
		 * Description :
		 * Cet attribut est un tableau de ComponentVariable.
		 * Chaque �l�ment du tableau repr�sente un �l�ment supprim� 
		 * par l'utilisateur, gard� en m�moire pour le supprimer des tables 
		 * de donn�es.
		 * ------------------------------------------------------------*/
		private ArrayList<ComponentVariable> _deleteDatas;

		/*------------------------------------------------------------
		 * Nom : ActionTableModel
		 * 
		 * Description :
		 * Cette m�thode est le contructeur de la classe.
		 * Son r�le est de cr�er les deux tableaux de titres et de donn�es.
		 * Pour le tableau des titres, elle le remplira � partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/
		public VariableTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "VariableTableModel");
			trace_methods.beginningOfMethod();

			// Cr�ation du tableau des titres
			_titles = new String[3];
			// La premi�re colonne repr�sentera le nom de la variable
			_titles[0] = MessageManager
					.getMessage("&ComponentConfiguration_Name");
			// La deuxi�me colonne repr�sentera la description associ�e
			// � l'action
			_titles[1] = MessageManager
					.getMessage("&ComponentConfiguration_Description");
			// La troisi�me colonne repr�sentera le commande associ�e
			// � l'action
			_titles[2] = MessageManager
					.getMessage("&ComponentConfiguration_DefaultValue");

			// Cr�ation du tableau de donn�es
			_datas = new ArrayList<ComponentVariable>();
			_deleteDatas = new ArrayList<ComponentVariable>();

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getColumnCount
		 * 
		 * Description : 
		 * Cette m�thode retourne le nombre de colonnes de la JTable. 
		 * Ce nombre est donn� par la dimension du tableaux des titres.
		 * 
		 * Retourne : un entier : le nombre de colonnes.
		 * ------------------------------------------------------------*/
		public int getColumnCount() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "getColumnCount");
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
					"VariableTableModel", "getRowCount");
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
		 * Cette m�thode retourne le tableau de ComponentVariable contenant
		 * l'ensemble des informations saisies par l'utilisateur.
		 * 
		 * Retourne : Un tableau de ComponentVariable
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentVariable> getDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "getDatas");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();

			return _datas;

		}

		/*------------------------------------------------------------
		 * Nom : findRow
		 * 
		 * Description : 
		 * Cette m�thode recherche la pr�sence d'une variable dans la JTable
		 * � partir du nom de la variable pass�e en param�tre. Elle retourne
		 * le num�ro de la ligne correspondant � cette variable si elle existe,
		 * -1 sinon.
		 * 
		 * Argument :
		 *  - variableName : une cha�ne de caract�res corespondant au nom
		 *    de l'action recherch�
		 *    
		 * Retourne : un entier : le num�ro de ligne de la variable, -1 si 
		 * elle n'existe pas.
		 * ------------------------------------------------------------*/
		public int findRow(String variableName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("variableName=" + variableName);

			if (_datas != null) {
				for (int index = 0; index < _datas.size(); index++) {
					if (_datas.get(index).getVariableName()
							.equals(variableName))
						return index;
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
		 * Le num�ro de ligne correspond � une variable particuli�re.
		 * Le num�ro de colonne indique le champ : 
		 * 	- 0 pour le nom de la variable
		 *  - 1 pour sa description
		 *  - 2 pour sa valeur par default
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
					"VariableTableModel", "getValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				ComponentVariable cma = _datas.get(line);
				if (cma != null) {
					if (column == 0)
						return cma.getVariableName();
					else if (column == 1) {
						if (cma.getFlag() == 'E')
							return cma.getDescription();
						else
							return cma.getNewDescription();
					} else if (column == 2) {
						if (cma.getFlag() == 'E')
							return cma.getDefaultValue();
						else
							return cma.getNewDefaultValue();
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
		 * - col : le num�ro de colonne
		 * 
		 * Retourne : une chaine de caract�re, le nom de la colonne.
		 * ------------------------------------------------------------*/
		public String getColumnName(int col) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "getColumnName");
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
		 * Cette m�thode ajoute un nouvel �l�ment (une variable) au 
		 * tableau de donn�es. 
		 * 
		 * Argument : 
		 *  - element : L'�l�ment � ajouter
		 * ------------------------------------------------------------*/
		public void addElement(ComponentVariable element) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "addElement");
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
		 * Cette m�thode supprime un �l�ment (une variable) au tableau 
		 * de donn�es. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � supprimer
		 * ------------------------------------------------------------*/
		public void removeElement(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "removeElement");
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
		 * Cette m�thode retourne le ComponentVariable de la ligne 
		 * demand�e. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � r�cup�rer
		 *  
		 * Retourne : Le ComponentVariable de la ligne s�lectionn�e.
		 * ------------------------------------------------------------*/
		public ComponentVariable getElementAt(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "getElementAt");
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
		 * base, le ComponentVariable pass� en param�tre. 
		 * 
		 * Argument : 
		 *  - var : Le ComponentVariable qu'il faudra supprimer 
		 *    de la base de donn�es.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(ComponentVariable var) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "addDeleteElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("var=" + var);

			if (_deleteDatas != null)
				_deleteDatas.add(var);

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : removeDeleletElement
		 * 
		 * Description : 
		 * Cette m�thode retire au tableau des �l�ments � supprimer de la
		 * base, le ComponentVariable d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentVariable qu'il faut retirer 
		 *    du tableau.
		 * ------------------------------------------------------------*/
		public void removeDeleletElement(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "removeDeleletElement");
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
		 * base, le ComponentVariable d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentVariable qu'il faut r�cup�rer
		 *    dans le tableau.
		 * 
		 * Retourne : Le ComponentVariable supprim� de la ligne
		 * souhait�e.
		 * ------------------------------------------------------------*/
		public ComponentVariable getDeleteElementAt(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "getDeleteElementAt");
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
		 * ComponentVariable � supprimer de la base de donn�es. 
		 * 
		 * Retourne : Le tableau des ComponentVariable � supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentVariable> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"VariableTableModel", "getDeleteElementAt");
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
	private VariableTableModel _tableModel;

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
				"VariableDescriptionPanel", "makePanel");
		trace_methods.beginningOfMethod();

		_tableModel = null;
		_variableTable = new JTable(_tableModel);
		_variableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_variableTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {

						onSelectionElement(event);
					}

				});
		// On int�gre un d�filement dans le tableau des variables
		JScrollPane scrollPaneVariableTable = new JScrollPane();
		scrollPaneVariableTable.setViewportView(_variableTable);

		// On pr�sente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(450, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(450, 20));
		firstDescriptionSentence
				.setText(MessageManager
						.getMessage("&ComponentConfiguration_FirstDescriptionSentenceVariable"));

		// On d�finit un label pour le champs "Nom"
		JLabel nameLabel = new JLabel();
		nameLabel.setMinimumSize(new Dimension(115, 20));
		nameLabel.setMaximumSize(new Dimension(115, 20));
		nameLabel.setPreferredSize(new Dimension(115, 20));
		nameLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Name")
				+ " :   *");

		// On d�finit un label pour le champs "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setMinimumSize(new Dimension(115, 20));
		descriptionLabel.setMaximumSize(new Dimension(115, 20));
		descriptionLabel.setPreferredSize(new Dimension(115, 20));
		descriptionLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Description")
				+ " : ");

		// On d�finit un label pour le champs "Valeur par d�faut"
		JLabel defaultValueLabel = new JLabel();
		defaultValueLabel.setMinimumSize(new Dimension(115, 20));
		defaultValueLabel.setMaximumSize(new Dimension(115, 20));
		defaultValueLabel.setPreferredSize(new Dimension(115, 20));
		defaultValueLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_DefaultValue")
				+ " : ");

		// On d�finit un label pour indiquer que l'asterisque en fin des noms de
		// champs sont obligatoires pour la saisie.
		JLabel obligatoryFields = new JLabel();
		obligatoryFields.setMinimumSize(new Dimension(150, 20));
		obligatoryFields.setMaximumSize(new Dimension(150, 20));
		obligatoryFields.setText(MessageManager
				.getMessage("&ComponentConfiguration_ObligatoryFields"));

		_nameTextField = new JTextField(20);
		setEnterCallback(_nameTextField);
		_nameTextField.setMinimumSize(new Dimension(335, 20));
		_nameTextField.setMaximumSize(new Dimension(335, 20));
		_nameTextField.setPreferredSize(new Dimension(335, 20));

		_descriptionTextField = new JTextField(20);
		setEnterCallback(_descriptionTextField);
		_descriptionTextField.setMinimumSize(new Dimension(335, 20));
		_descriptionTextField.setMaximumSize(new Dimension(335, 20));
		_descriptionTextField.setPreferredSize(new Dimension(335, 20));

		_defaultValueTextField = new JFormattedTextField();
		setEnterCallback(_defaultValueTextField);
		_defaultValueTextField.setMinimumSize(new Dimension(335, 20));
		_defaultValueTextField.setMaximumSize(new Dimension(335, 20));
		_defaultValueTextField.setPreferredSize(new Dimension(335, 20));

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

		Box hBox1 = Box.createHorizontalBox();
		hBox1.setMinimumSize(new Dimension(490, 20));
		hBox1.setMaximumSize(new Dimension(490, 20));
		hBox1.add(Box.createHorizontalStrut(5));
		hBox1.add(firstDescriptionSentence);
		hBox1.add(Box.createHorizontalStrut(5));

		Box hBox2 = Box.createHorizontalBox();
		hBox2.setMinimumSize(new Dimension(490, 200));
		hBox2.setMaximumSize(new Dimension(490, 200));
		hBox2.add(Box.createHorizontalStrut(5));
		hBox2.add(scrollPaneVariableTable);
		hBox1.add(Box.createHorizontalStrut(5));

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
		hBox5.setMinimumSize(new Dimension(490, 20));
		hBox5.setMaximumSize(new Dimension(490, 20));
		hBox5.add(Box.createHorizontalStrut(5));
		hBox5.add(defaultValueLabel);
		hBox5.add(Box.createHorizontalStrut(10));
		hBox5.add(_defaultValueTextField);
		hBox5.add(Box.createHorizontalStrut(5));

		Box hBox6 = Box.createHorizontalBox();
		hBox6.setMinimumSize(new Dimension(490, 20));
		hBox6.setMaximumSize(new Dimension(490, 20));
		hBox6.add(Box.createHorizontalStrut(85));
		hBox6.add(_newButton);
		hBox6.add(Box.createHorizontalStrut(10));
		hBox6.add(_removeButton);
		hBox6.add(Box.createHorizontalStrut(10));
		hBox6.add(_applyButton);
		hBox6.add(Box.createHorizontalStrut(5));

		Box hBox7 = Box.createHorizontalBox();
		hBox7.setMinimumSize(new Dimension(490, 20));
		hBox7.setMaximumSize(new Dimension(490, 20));
		hBox7.add(Box.createHorizontalStrut(10));
		hBox7.add(obligatoryFields);

		Box vBox1 = Box.createVerticalBox();
		vBox1.setMinimumSize(new Dimension(486, 250));
		vBox1.setMaximumSize(new Dimension(486, 250));
		vBox1.setPreferredSize(new Dimension(486, 250));
		vBox1.add(Box.createVerticalStrut(14));
		vBox1.add(hBox1);
		vBox1.add(Box.createVerticalStrut(15));
		vBox1.add(hBox2);

		Box vBox2 = Box.createVerticalBox();
		vBox2.setMinimumSize(new Dimension(480, 180));
		vBox2.setMaximumSize(new Dimension(480, 180));
		vBox2.setMaximumSize(new Dimension(480, 180));
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox3);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox4);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox5);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox6);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox7);
		vBox2.setBorder(BorderFactory.createTitledBorder((MessageManager
				.getMessage("&ComponentConfiguration_SecondDescriptionSentence"))));

		Box vBox3 = Box.createVerticalBox();
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
		String description = _descriptionTextField.getText();
		String defaultValue = _defaultValueTextField.getText();

		if (name.equals("")) {
			DialogManager.displayDialog("Information", MessageManager
					.getMessage("&ComponentConfiguration_MessageInformation"),
					null, null);
		} else {
			/*
			 * 1 : V�rifier qu'il n'y a pas de variables avec le m�me nom 2 :
			 * Mettre � jour le tableau 3 : Effacer les champs 4 : actualiser le
			 * tableau
			 */

			int row = _tableModel.findRow(name);
			if (row == -1) {

				char flag = 'A';
				ComponentVariable cv = new ComponentVariable();

				ArrayList<ComponentVariable> deleteElement = _tableModel
						.getDeleteDatas();
				if (deleteElement != null) {
					for (int index = 0; index < deleteElement.size(); index++) {

						if (deleteElement.get(index).getVariableName().equals(
								name)) {
							flag = 'M';
							cv = deleteElement.get(index);
							deleteElement.remove(index);
							break;
						}
					}
				}

				cv.setVariableName(name);
				cv.setNewDescription(description);
				cv.setNewDefaultValue(defaultValue);
				cv.setFlag(flag);
				_tableModel.addElement(cv);

				// On efface les champs de saisie
				_nameTextField.setText("");
				_descriptionTextField.setText("");
				_defaultValueTextField.setText("");
				_applyButton.setEnabled(false);

			} else {

				// La variable existe donc on la met � jour
				int selectedRow = _variableTable.getSelectedRow();

				if (selectedRow == -1) {
					DialogManager.displayDialog("Information", MessageManager
							.getMessage("&ComponentConfiguration_MessageInformationVariable"),
							null, null);
				} 
				else {
					ComponentVariable cv = _tableModel.getElementAt(row);
					cv.setNewDescription(description);
					cv.setNewDefaultValue(defaultValue);
					if (cv.getFlag() == 'E')
						cv.setFlag('M');

					// On recharge le tableau et on le r�affiche
					_tableModel.fireTableRowsUpdated(row, row);

					// On efface les champs de saisie
					_nameTextField.setText("");
					_descriptionTextField.setText("");
					_defaultValueTextField.setText("");
					_variableTable.getSelectionModel().clearSelection();
					_applyButton.setEnabled(false);
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

		int selectedRow = _variableTable.getSelectedRow();

		// Si une ligne du tableau est s�lectionn�e
		if (selectedRow != -1) {
			ComponentVariable cv = _tableModel.getElementAt(selectedRow);
			if (cv != null) {
				if (cv.getFlag() == 'A')
					_tableModel.removeElement(selectedRow);
				else if (cv.getFlag() == 'E' || cv.getFlag() == 'M') {
					_tableModel.removeElement(selectedRow);
					cv.setFlag('S');
					_tableModel.addDeleteElement(cv);
				} 
				else {

				}
			}
			_tableModel.fireTableRowsDeleted(selectedRow, selectedRow);

			_nameTextField.setText("");
			_descriptionTextField.setText("");
			_defaultValueTextField.setText("");

			_variableTable.getSelectionModel().clearSelection();
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

		_nameTextField.setText("");
		_descriptionTextField.setText("");
		_defaultValueTextField.setText("");
		_variableTable.getSelectionModel().clearSelection();
		_applyButton.setEnabled(false);
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
			_defaultValueTextField.setText("");

			_applyButton.setEnabled(false);
			_removeButton.setEnabled(false);

		} else {

			ComponentVariable cv = new ComponentVariable();
			cv = _tableModel.getElementAt(index);
			_nameTextField.setText(cv.getVariableName());
			if (cv.getFlag() == 'E') {
				_descriptionTextField.setText(cv.getDescription());
				_defaultValueTextField.setText(cv.getDefaultValue());
			} else {
				_descriptionTextField.setText(cv.getNewDescription());
				_defaultValueTextField.setText(cv.getNewDefaultValue());
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

		if (_nameTextField.getText().equals("")) {
			_applyButton.setEnabled(false);
			return;
		}
		// Ok, on peut valider le bouton
		_applyButton.setEnabled(true);

		trace_methods.endOfMethod();
	}
}
