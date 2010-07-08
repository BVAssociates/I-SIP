/*------------------------------------------------------------
<<<<<<< ManagementActionDescriptionPanel.java
 * Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
 * ------------------------------------------------------------
 *
 * $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/ManagementActionDescriptionPanel.java,v $
 * $Revision: 1.23 $
 *
 * ------------------------------------------------------------
 * DESCRIPTION: Panneau de saisie des actions d'exploitation
 * DATE:        11/06/2008
 * AUTEUR:      F. Cossard - H. Doghmi
 * PROJET:      I-SIS
 * GROUPE:      processor.impl.config
 * ------------------------------------------------------------
 * CONTROLE DES MODIFICATIONS
 *
 * Revision 1.3  2008/06/27 15:20:00  fcd
 * Ajout des Traces
 * 
 * Revision 1.2  2008/06/24 17:00:00  fcd
 * Ajout des champs Responsabilities et ExecutiveUser
 *
 * ------------------------------------------------------------*/

// D�claration du package
package com.bv.isis.console.impl.processor.config.implementation.view;

//
//Imports syst�me
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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.impl.processor.config.implementation.ComponentConfigurationProcessor;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentManagementAction;
import com.bv.isis.console.node.GenericTreeObjectNode;

/*------------------------------------------------------------
 * Nom: GenericTransitionDescriptionPanel
 * 
 * Description: 
 * Cette classe impl�mente le panneau qui permet de d�finir 
 * les actions d'exploitation d'un composant I-SIS.
 * ------------------------------------------------------------*/
public class ManagementActionDescriptionPanel extends AbstractPanel {

	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: ManagementActionDescriptionPanel
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * Elle appelle la m�thode makePanel() charg�e de la cr�ation des objets
	 * graphiques a afficher dans le panneau.		
	 * ------------------------------------------------------------*/
	public ManagementActionDescriptionPanel(WindowInterface window) {
		super();

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ManagementActionDescriptionPanel",
				"ManagementActionDescriptionPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("window=" + window);

		_window = window;
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
	 *  - tabModels : Le tableau de ModelInterface.
	 * ------------------------------------------------------------*/
	public void beforeDisplay(ArrayList<ModelInterface> modele) {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ManagementActionDescriptionPanel", "beforeDisplay");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("modele=" + modele);

		_tableModel = new ActionTableModel();
			
		if (modele != null) {
			for ( int i=0 ; i < modele.size() ; i++) {
				if ( modele.get(i) instanceof ComponentManagementAction ) {
					ComponentManagementAction c = 
						(ComponentManagementAction) modele.get(i);
					if (c.getFlag() != 'S')
						_tableModel.addElement(c);
					else
						_tableModel.addDeleteElement(c); 
				}

			}
		}
		_managementActionTable.setModel( _tableModel );
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
				"ManagementActionDescriptionPanel", "afterDisplay");
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
				"ManagementActionDescriptionPanel", "beforeHide");
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
				"ManagementActionDescriptionPanel", "afterHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return true;
	}

	/*------------------------------------------------------------
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
	 * ------------------------------------------------------------*/
	public boolean end() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ManagementActionDescriptionPanel", "end");
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
				"ManagementActionDescriptionPanel", "update");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tabModels=" + tabModels);

		ArrayList<ComponentManagementAction> deletedElements = _tableModel
				.getDeleteDatas();
		ArrayList<ComponentManagementAction> newElements = _tableModel
				.getDatas();

		if (tabModels != null) {

			// On supprime toutes les anciennes actions d'exploitation
			for (int index = 0; index < tabModels.size(); index++) {
				if (tabModels.get(index) instanceof ComponentManagementAction) {
					tabModels.remove(index);
					// On recule d'une case dans le tableau pour ne pas oublier
					// l'�l�ment d�cal�
					index--;
				}
			}
		} else
			// Le tableau �tait inexistant, on le cr�e
			tabModels = new ArrayList<ModelInterface>();

		// On ajoute les nouvelles actions au tableau de ModelInterface
		tabModels.addAll(deletedElements);
		tabModels.addAll(newElements);

		// _tableModel = null;
		trace_methods.endOfMethod();
		return tabModels;
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	 * Nom: setEnterCallback
	 *
	 * Description:
	 * Cette m�thode permet de d�finir la m�thode de callback en
	 * cas d'appui sur la touche "Entr�e" sur un objet JComponent
	 * pass� en argument.
	 * La m�thode de callback est la m�thode onClickApplyButton().
	 *
	 * Arguments:
	 *  - component: Un objet JComponent sur lequel le callback
	 *    doit �tre d�fini.
	 * ----------------------------------------------------------*/
	protected void setEnterCallback(JComponent component) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ManagementActionDescriptionPanel", "setEnterCallback");
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
				} else {
					fieldsHaveChanged();
				}
			}
		});
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _scrollPaneActionTable
	 * 
	 * Description :
	 * Cette attribut permet d'int�grer un d�filement dans le 
	 * tableau.
	 * ------------------------------------------------------------*/
	private JScrollPane _scrollPaneActionTable;

	/*------------------------------------------------------------
	 * Nom : _managementActionTable
	 * 
	 * Description :
	 * Cette attribut repr�sente un tableau r�capitulant l'ensemble
	 * des actions d'exploitation d�finis par l'utilisateur.
	 * ------------------------------------------------------------*/
	private JTable _managementActionTable;

	/*------------------------------------------------------------
	 * Nom : _nameTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour le nom d'une
	 * action d'exploitation dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _nameTextField;

	/*------------------------------------------------------------
	 * Nom : _descriptionTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour la description
	 * d'une action d'exploitation dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _descriptionTextField;

	/*------------------------------------------------------------
	 * Nom : _commandTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour la commande
	 * d'une action d'exploitation dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _commandTextField;

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
	 * Nom : _responsabilitiesTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour le champ 
	 * responsabilit� d'une action d'exploitation dans le sous panneau 
	 * de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _responsabilitiesTextField;

	/*------------------------------------------------------------
	 * Nom : _responsabilitiesButton
	 * 
	 * Description :
	 * ------------------------------------------------------------*/
	private JButton _responsabilitiesButton;

	/*------------------------------------------------------------
	 * Nom : _removeButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * effacer la ligne s�lectionner dans le tableau des actions.
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

	/*------------------------------------------------------------
	 * Nom : _newButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * permettre une nouvelle saisie.
	 * ------------------------------------------------------------*/
	private JButton _newButton;

	/*------------------------------------------------------------
	 * Nom : ActionTableModel
	 * 
	 * Description :
	 * Cette classe repr�sente le modele associ� � la JTable.
	 * Elle d�rive de AbstractTableModel.
	 * Elle se caract�rise de 2 tableaux:
	 *  - un pour les titres des diverses colonnes
	 *  - un pour les donn�es de chaque ligne.
	 * Chaque ligne est une instance de ComponentManagementAction.
	 * ------------------------------------------------------------*/
	private class ActionTableModel extends AbstractTableModel {

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
		private ArrayList<ComponentManagementAction> _datas;

		private ArrayList<ComponentManagementAction> _deleteDatas;

		/*------------------------------------------------------------
		 * Nom : ActionTableModel
		 * 
		 * Description :
		 * Cette m�thode est le contructeur de la classe.
		 * Son r�le est de cr�er les deux tableaux de titres et de donn�es.
		 * Pour le tableau des titres, elle le remplira � partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/
		public ActionTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "ActionTableModel");
			trace_methods.beginningOfMethod();

			// Cr�ation du tableau des titres
			_titles = new String[4];
			// La premi�re colonne repr�sentera le nom de l'action
			_titles[0] = MessageManager
					.getMessage("&ComponentConfiguration_Name");
			// La deuxi�me colonne repr�sentera la description associ�e
			// � l'action
			_titles[1] = MessageManager
					.getMessage("&ComponentConfiguration_Description");
			// La troisi�me colonne repr�sentera le commande associ�e
			// � l'action
			_titles[2] = MessageManager
					.getMessage("&ComponentConfiguration_Command");
			// La quatri�me colonne repr�sentera la liste des responsabilit�s
			_titles[3] = MessageManager.getMessage(
				"&ComponentConfiguration_Responsabilities");

			// Cr�ation du tableau de donn�es
			_datas = new ArrayList<ComponentManagementAction>();
			_deleteDatas = new ArrayList<ComponentManagementAction>();

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
					"ActionTableModel", "getColumnCount");
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
		 * Ce nombre est donn� par la dimmension du tableaux des donn�es.
		 * 
		 * Retourne : un entier : le nombre de lignes.
		 * ------------------------------------------------------------*/
		public int getRowCount() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "getRowCount");
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
		public ArrayList<ComponentManagementAction> getDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "getDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();

			return _datas;
		}

		/*------------------------------------------------------------
		 * Nom : findRow
		 * 
		 * Description : 
		 * Cette m�thode recherche la pr�sence d'une action dans la JTable
		 * � partir du nom de l'action pass� en param�tre. Elle retourne
		 * le num�ro de la ligne correspondant � cet action si elle existe,
		 * -1 sinon.
		 * 
		 * Argument :
		 *  - actionName : une cha�ne de caract�res correspondant au nom
		 *    de l'action recherch�
		 *    
		 * Retourne : un entier : le num�ro de ligne de l'action, -1 si 
		 * elle n'existe pas.
		 * ------------------------------------------------------------*/
		public int findRow(String actionName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("actionName=" + actionName);

			if (_datas != null) {
				for (int index = 0; index < _datas.size(); index++) {
					ComponentManagementAction cma = _datas.get(index);
					if (cma != null && cma.getActionName() != null)
						if (_datas.get(index).getActionName()
								.equals(actionName)) {
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
		 * Le num�ro de ligne correspond � une action particuli�re.
		 * Le num�ro de colonne indique le champ : 
		 * 	- 0 pour le nom de l'action
		 *  - 1 pour sa description
		 *  - 2 pour sa commande
		 *  - 3 pour la liste des responsabilit�s
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
					"ActionTableModel", "getValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				ComponentManagementAction cma = _datas.get(line);
				if (cma != null) {
					if (column == 0) {
						trace_methods.endOfMethod();
						return cma.getActionName();
					} else if (column == 1) {
						if (cma.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return cma.getDescription();
						} else {
							trace_methods.endOfMethod();
							return cma.getNewDescription();
						}
					} else if (column == 2) {
						if (cma.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return cma.getCommand();
						} else {
							trace_methods.endOfMethod();
							return cma.getNewCommand();
						}
					} else if (column == 3) {
						if (cma.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return cma.getResponsabilities();
						} else {
							trace_methods.endOfMethod();
							return cma.getNewResponsabilities();
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
		 *  - column : la colonne dont on veut le nom
		 * 
		 * Retourne : une chaine de caract�re : le nom de la colonne.
		 * ------------------------------------------------------------*/
		public String getColumnName(int column) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "getColumnCount");
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
		 * Cette m�thode ajoute un nouvel �l�ment (une action) au 
		 * tableau de donn�es. 
		 * 
		 * Argument : 
		 *  - element : L'�l�ment � ajouter
		 * ------------------------------------------------------------*/
		public void addElement(ComponentManagementAction element) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "addElement");
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
		 * Cette m�thode supprime un �l�ment (une action) au tableau 
		 * de donn�es. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � supprimer
		 * ------------------------------------------------------------*/
		public void removeElement(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "removeElement");
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
		 * Cette m�thode retourne le ComponentManagementAction de la ligne 
		 * demand�e. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � r�cup�rer
		 *  
		 * Retourne : Le ComponentManagementAction de la ligne s�lectionn�e.
		 * ------------------------------------------------------------*/
		public ComponentManagementAction getElementAt(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "getElementAt");
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
		 * base, le ComponentManagementAction pass� en param�tre. 
		 * 
		 * Argument : 
		 *  - action : Le ComponentManagementAction qu'il faudra supprimer 
		 *    de la base de donn�es.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(ComponentManagementAction action) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "addDeleteElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("action=" + action);

			if (_deleteDatas != null)
				_deleteDatas.add(action);

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : removeDeleletElement
		 * 
		 * Description : 
		 * Cette m�thode retire au tableau des �l�ments � supprimer de la
		 * base, le ComponentManagementAction d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentManagementAction qu'il faut retirer 
		 *    du tableau.
		 * ------------------------------------------------------------*/
		public void removeDeleletElement(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "removeDeleletElement");
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
		 * base, le ComponentManagementAction d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentManagementAction qu'il faut r�cup�rer
		 *    dans le tableau.
		 * 
		 * Retourne : Le ComponentManagementAction supprim� de la ligne
		 * souhait�e.
		 * ------------------------------------------------------------*/
		public ComponentManagementAction getDeleteElementAt(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "getDeleteElementAt");
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
		 * ComponentManagementAction � supprimer de la base de donn�es. 
		 * 
		 * Retourne : Le tableau des ComponentManagementAction � supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentManagementAction> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ActionTableModel", "getDeleteElementAt");
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
	private ActionTableModel _tableModel;

	/*------------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette m�thode est appel� lors de la construction du panneau.
	 * Son r�le est d'instancier les composants graphiques du panneau et de les
	 * positionner.
	 * ------------------------------------------------------------*/
	private void makePanel() {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ManagementActionDescriptionPanel", "makePanel");
		trace_methods.beginningOfMethod();

		_tableModel = null;

		// On pr�sente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(476, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(476, 20));
		firstDescriptionSentence
				.setText(MessageManager
						.getMessage("&ComponentConfiguration_FirstDescriptionSentenceManagementAction"));

		_managementActionTable = new JTable(_tableModel);
		_managementActionTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_managementActionTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});

		// On int�gre un d�filement dans le tableau des actions de gestions
		_scrollPaneActionTable = new JScrollPane();
		_scrollPaneActionTable.setViewportView(_managementActionTable);
		_scrollPaneActionTable.setMinimumSize(new Dimension(476, 153));
		_scrollPaneActionTable.setMaximumSize(new Dimension(476, 153));

		JPanel panel1 = new JPanel();
		panel1.setMinimumSize(new Dimension(480, 440));
		panel1.setMaximumSize(new Dimension(480, 440));
		panel1.setPreferredSize(new Dimension(480, 440));
		panel1.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.NONE;
		c1.insets = new Insets(5, 10, 5, 10);
		c1.ipadx = 0;

		c1.gridx = 0;
		c1.gridy = 0;
		c1.gridwidth = 1;
		c1.gridheight = 1;
		c1.ipady = 10;
		panel1.add(firstDescriptionSentence, c1);

		c1.ipady = 10;
		c1.gridx = 0;
		c1.gridy = 1;
		c1.gridwidth = 1;
		c1.gridheight = 1;
		panel1.add(_scrollPaneActionTable, c1);

		// On d�finit un label pour le champs "Nom"
		JLabel nameLabel = new JLabel();
		nameLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Name")
				+ " : ");

		// On d�finit un label pour le champs "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Description")
				+ " : ");
		JLabel commandLabel = new JLabel();
		commandLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Command")
				+ " :   *");
		// On d�finit un label pour le champs "Responsabilit�s"
		JLabel responsabilitiesLabel = new JLabel();
		responsabilitiesLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_" + "Responsabilities")
				+ " :   *");

		// On d�finit un label pour indiquer que l'asterisque en fin des noms de
		// champs sont obligatoires pour la saisie.
		JLabel obligatoryFieldLabel = new JLabel();
		obligatoryFieldLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_ObligatoryFields"));
		obligatoryFieldLabel.setMinimumSize(new Dimension(150, 20));
		obligatoryFieldLabel.setMaximumSize(new Dimension(150, 20));

		int maxSize = nameLabel.getPreferredSize().width;
		if (descriptionLabel.getPreferredSize().width > maxSize)
			maxSize = descriptionLabel.getPreferredSize().width;
		if (commandLabel.getPreferredSize().width > maxSize)
			maxSize = commandLabel.getPreferredSize().width;
		if (responsabilitiesLabel.getPreferredSize().width > maxSize)
			maxSize = responsabilitiesLabel.getPreferredSize().width;

		// On d�finit la taille des labels
		nameLabel.setPreferredSize(new Dimension(maxSize, 20));
		nameLabel.setMinimumSize(new Dimension(maxSize, 20));
		nameLabel.setMaximumSize(new Dimension(maxSize, 20));
		descriptionLabel.setPreferredSize(new Dimension(maxSize, 20));
		descriptionLabel.setMinimumSize(new Dimension(maxSize, 20));
		descriptionLabel.setMaximumSize(new Dimension(maxSize, 20));
		commandLabel.setPreferredSize(new Dimension(maxSize, 20));
		commandLabel.setMinimumSize(new Dimension(maxSize, 20));
		commandLabel.setMaximumSize(new Dimension(maxSize, 20));
		responsabilitiesLabel.setPreferredSize(new Dimension(maxSize, 20));
		responsabilitiesLabel.setMinimumSize(new Dimension(maxSize, 20));
		responsabilitiesLabel.setMaximumSize(new Dimension(maxSize, 20));

		_nameTextField = new JTextField();
		setEnterCallback(_nameTextField);
		_nameTextField.setPreferredSize(new Dimension(440 - (nameLabel
				.getPreferredSize().width), 20));
		_nameTextField.setMinimumSize(new Dimension(440 - (nameLabel
				.getMinimumSize().width), 20));
		_nameTextField.setMaximumSize(new Dimension(440 - (nameLabel
				.getMaximumSize().width), 20));

		_descriptionTextField = new JTextField();
		setEnterCallback(_descriptionTextField);
		_descriptionTextField.setPreferredSize(new Dimension(
				440 - (descriptionLabel.getPreferredSize().width), 20));
		_descriptionTextField.setMinimumSize(new Dimension(
				440 - (descriptionLabel.getMinimumSize().width), 20));
		_descriptionTextField.setMaximumSize(new Dimension(
				440 - (descriptionLabel.getMaximumSize().width), 20));

		_commandButton = new JButton();
		_commandButton.setMinimumSize(new Dimension(30, 22));
		_commandButton.setMaximumSize(new Dimension(30, 22));
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
		_commandTextField.setPreferredSize(new Dimension(440 - (commandLabel
				.getPreferredSize().width), 20));
		_commandTextField.setMinimumSize(new Dimension(440 - (commandLabel
				.getMinimumSize().width), 20));
		_commandTextField.setMaximumSize(new Dimension(440 - (commandLabel
				.getMaximumSize().width), 20));

		_responsabilitiesButton = new JButton();
		_responsabilitiesButton.setMinimumSize(new Dimension(30, 22));
		_responsabilitiesButton.setMaximumSize(new Dimension(30, 22));
		_responsabilitiesButton.setPreferredSize(new Dimension(30, 22));
		_responsabilitiesButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_ResponsabilitiesButton"));
		_responsabilitiesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickResponsabilitiesButton();
			}
		});

		_responsabilitiesTextField = new JTextField();
		setEnterCallback(_responsabilitiesTextField);
		_responsabilitiesTextField.setPreferredSize(new Dimension(430
				- (responsabilitiesLabel.getPreferredSize().width)
				- (_responsabilitiesButton.getPreferredSize().width) - 10, 20));
		_responsabilitiesTextField.setMinimumSize(new Dimension(
				430 - (responsabilitiesLabel.getPreferredSize().width)
				- (_responsabilitiesButton.getPreferredSize().width) - 10, 20));
		_responsabilitiesTextField.setMaximumSize(new Dimension(430
				- (responsabilitiesLabel.getPreferredSize().width)
				- (_responsabilitiesButton.getPreferredSize().width) - 10, 20));

		_removeButton = new JButton();
		_removeButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_RemoveButton"));
		_removeButton.setMinimumSize(new Dimension(120, 22));
		_removeButton.setMaximumSize(new Dimension(120, 22));
		_removeButton.setPreferredSize(new Dimension(120, 22));
		_removeButton.setEnabled(false);
		_removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickRemoveButton();
			}
		});

		_applyButton = new JButton();
		_applyButton.setText(MessageManager
				.getMessage("&ComponentConfiguration_ApplyButton"));
		_applyButton.setMinimumSize(new Dimension(120, 22));
		_applyButton.setMaximumSize(new Dimension(120, 22));
		_applyButton.setPreferredSize(new Dimension(120, 22));
		_applyButton.setEnabled(false);
		_applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickApplyButton();
			}
		});

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

		JPanel panel2 = new JPanel();
		panel2.setMinimumSize(new Dimension(480, 200));
		panel2.setMaximumSize(new Dimension(480, 200));
		panel2.setPreferredSize(new Dimension(480, 200));
		panel2
				.setBorder(BorderFactory
						.createTitledBorder((MessageManager
								.getMessage("&ComponentConfiguration_SecondDescriptionSentence"))));

		panel2.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 10, 5, 10);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = c.weighty = 0.0;
		panel2.add(nameLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel2.add(_nameTextField, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(descriptionLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel2.add(_descriptionTextField, c);

		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(commandLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel2.add(_commandTextField, c);

		/*
		 * D�commenter cette partie de code pour prendre en compte le bouton
		 * commande
		 * 
		 * c.gridx = 2; c.gridy = 2; c.gridwidth = 1; c.gridheight = 1;
		 * panel2.add(_commandButton, c);
		 */
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(responsabilitiesLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(_responsabilitiesTextField, c);

		c.gridx = 2;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(_responsabilitiesButton, c);

		Box hBox1 = Box.createHorizontalBox();
		hBox1.add(_newButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_removeButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_applyButton);
		hBox1.setMaximumSize(new Dimension(380, 20));

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_END;
		panel2.add(hBox1, c);

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_START;
		panel2.add(obligatoryFieldLabel, c);

		c1.gridx = 0;
		c1.gridy = 2;
		c1.gridwidth = 1;
		c1.gridheight = 1;
		c1.insets = new Insets(5, 10, 5, 10);
		;
		panel1.add(panel2, c1);

		setLayout(new BorderLayout());
		add(panel1, BorderLayout.CENTER);

		trace_methods.endOfMethod();
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
		// On souhaite effacer le contenu s�lectionn�
		// On r�cup�re la ligne s�lectionn�e du tableau si il y
		// en a une.
		int row = _managementActionTable.getSelectedRow();

		if (row != -1) {

			ComponentManagementAction cc = _tableModel.getElementAt(row);
			if (cc != null) {
				if (cc.getFlag() == 'A')
					_tableModel.removeElement(row);
				else if (cc.getFlag() == 'E' || cc.getFlag() == 'M') {
					_tableModel.removeElement(row);
					cc.setFlag('S');
					_tableModel.addDeleteElement(cc);
				} else {
					// A gros probl�me
				}
			}
			_tableModel.fireTableRowsDeleted(row, row);
		}
		// On efface les champs de saisies
		_nameTextField.setText("");
		_nameTextField.setEnabled(true);
		_descriptionTextField.setText("");
		_commandTextField.setText("");
		_responsabilitiesTextField.setText("");
		_managementActionTable.getSelectionModel().clearSelection();

		_removeButton.setEnabled(false);
		_applyButton.setEnabled(false);
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
		// On souhaite enregistrer les donn�es saisies
		// On r�cup�re les champs
		String name = _nameTextField.getText();
		String description = _descriptionTextField.getText();
		String command = _commandTextField.getText();
		String respons = _responsabilitiesTextField.getText();

		// Si un champ obligatoire n'est pas rempli, erreur
		if (name.equals("")) {
			DialogManager.displayDialog("Information", MessageManager
					.getMessage("&ComponentConfiguration_MessageInformation"),
					null, null);
		} 
		else {
			// Dans un premier temps, on va rechercher la ligne du
			// tableau associ�e au nom de l'action si elle existe
			int row = _managementActionTable.getSelectedRow();

			if (row == -1) {

				if (_tableModel.findRow(name) == -1) {
					char flag = 'A';
					ComponentManagementAction cma = new ComponentManagementAction();
					ArrayList<ComponentManagementAction> deleteElement = _tableModel
							.getDeleteDatas();
					if (deleteElement != null) {
						for (int index = 0; index < deleteElement.size(); index++) {
							if (deleteElement.get(index).getActionName()
									.equals(name)) {
								flag = 'M';
								cma = deleteElement.get(index);
								deleteElement.remove(index);
								break;
							}
						}
					}

					cma.setActionName(name);
					cma.setNewDescription(description);
					cma.setNewCommand(command);
					cma.setNewResponsabilities(respons);
					cma.setFlag(flag);
					_tableModel.addElement(cma);

					// On efface les champs de saisies
					_nameTextField.setText("");
					_nameTextField.setEnabled(true);
					_descriptionTextField.setText("");
					_commandTextField.setText("");
					_responsabilitiesTextField.setText("");
					_managementActionTable.repaint();
					_managementActionTable.getSelectionModel().clearSelection();

					_removeButton.setEnabled(false);

					_applyButton.setEnabled(false);
				}
				else {
					DialogManager.displayDialog("Information", MessageManager
							.getMessage("&ComponentConfiguration_MessageInformationManagementAction"),
							null, null);
				}
			}
			else {
				// Si l'action existait d�j� dans le tableau, c'est
				// une modification d'action ; on modifie les champs
				ComponentManagementAction cma = _tableModel
							.getElementAt(row);
				cma.setNewDescription(description);
				cma.setNewCommand(command);
				cma.setNewResponsabilities(respons);
				if (cma.getFlag() == 'E')
					cma.setFlag('M');

				_tableModel.fireTableRowsUpdated(row, row);

				// On efface les champs de saisies
				_nameTextField.setText("");
				_nameTextField.setEnabled(true);
				_descriptionTextField.setText("");
				_commandTextField.setText("");
				_responsabilitiesTextField.setText("");
				_managementActionTable.repaint();
				_managementActionTable.getSelectionModel()
						.clearSelection();

				_removeButton.setEnabled(false);
				_applyButton.setEnabled(false);
			}
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
		// On efface les champs de saisies
		_nameTextField.setText("");
		_nameTextField.setEnabled(true);
		_descriptionTextField.setText("");
		_commandTextField.setText("");
		_responsabilitiesTextField.setText("");
		_managementActionTable.repaint();
		_managementActionTable.getSelectionModel().clearSelection();
		_removeButton.setEnabled(false);
		_applyButton.setEnabled(false);
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

				String valuesList = dialog.getSelectedInfo(

				responsabilityList, _responsabilitiesTextField.getText());
				if (!valuesList.equals("")) {

					_responsabilitiesTextField.setText(valuesList);
					fieldsHaveChanged();
				}
			}
		} catch (Exception e) {

		}
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
			_responsabilitiesTextField.setText("");

			_removeButton.setEnabled(false);

			_applyButton.setEnabled(false);
		} else {
			ComponentManagementAction cma = _tableModel.getElementAt(index);
			_nameTextField.setText(cma.getActionName());
			if (cma.getFlag() == 'E') {
				_descriptionTextField.setText(cma.getDescription());
				_commandTextField.setText(cma.getCommand());
				_responsabilitiesTextField.setText(cma.getResponsabilities());
			} else {
				_descriptionTextField.setText(cma.getNewDescription());
				_commandTextField.setText(cma.getNewCommand());
				_responsabilitiesTextField
						.setText(cma.getNewResponsabilities());
			}
			_nameTextField.setEnabled(false);
			_removeButton.setEnabled(true);
			_applyButton.setEnabled(true);
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
				"ManagementActionDescriptionPanel", "fieldsHaveChanged");

		trace_methods.beginningOfMethod();
		// V�rification du contenu du champ Nom
		if (_nameTextField.getText().equals("")
				|| _commandTextField.getText().equals("")
				|| _responsabilitiesTextField.getText().equals("")) {
			// Aucune valeur n'est s�lectionn�e
			_applyButton.setEnabled(false);
			trace_methods.endOfMethod();
			return;
		}
		// Ok, on peut valider le bouton
		_applyButton.setEnabled(true);
		trace_methods.endOfMethod();
	}
}
