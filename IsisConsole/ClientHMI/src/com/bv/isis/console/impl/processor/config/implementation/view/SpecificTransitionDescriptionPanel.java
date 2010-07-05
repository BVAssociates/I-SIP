/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits réservés.
 * ------------------------------------------------------------
 *
 * $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/SpecificTransitionDescriptionPanel.java,v $
 * $Revision: 1.26 $
 *
 * ------------------------------------------------------------
 * DESCRIPTION: Panneau de saisie des informations sépcifiques d'une transition
 * DATE:        13/06/2008
 * AUTEUR:      F. Cossard - H. Doghmi
 * PROJET:      I-SIS
 * GROUPE:      processor.impl.config
 * ------------------------------------------------------------
 * CONTROLE DES MODIFICATIONS
 *
 * Révision 1.9  2008/07/11 fcd
 * Modification des champs de saisie pour l'interval de controle
 * et de délai maximum pour n'accepter que les nombres
 *
 * Revision 1.5  2008/07/01 14:00:00  fcd
 * Mise en commentaire de la partie concernant le bouton d'exploration
 * de la commande.
 *
 * Revision 1.4  2008/06/27 11:30:00  fcd
 * Ajout des traces, modification du contructeur
 * Ajout d'un attribut référencant le panneau appelant
 *
 * ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.config.implementation.view;

//
// Imports système
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
 * Cette classe implémente la panneau qui permet de décrire 
 * un ensemble de transitions vers un état précédemment défini.
 * ----------------------------------------------------------*/
public class SpecificTransitionDescriptionPanel extends AbstractPanel {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: SpecificDescriptionDescriptionPanel
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * Elle appelle la méthode makePanel() chargée de la création des objets
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
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Elle est appelée par la classe AbstractWindow lorsque le panneau doit être
	 * affiché. A partir du modèle de données passé en paramètre, elle se charge
	 * de pré-renseigner les champs du panneau.
	 * 
	 * Paramètres :
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
	 * Cette méthode est appelée par le panneau GenericTransitionGroup
	 * lorsqu'il demande l'affichage des informations spécifiques d'une 
	 * transition. Elle se charge de charger la JTable à partir du 
	 * tableabu de données passé en paramètre.
	 * 
	 * Paramètres :
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
		// Une transition doit au moins être spécifiée.
		if (_tableModel.getRowCount() == 0)
			_validateButton.setEnabled(false);

		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: afterDisplay
	 * 
	 * Description: 
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Elle n'est présentée que pour des raisons de lisibilité.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
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
	 * Cette méthode définit le comportement du panneau avant d'être caché.
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Elle est en charge de vérifier la validité et la conformité des 
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
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Cette méthode définit le comportement du panneau après avoir été caché.
	 * Elle n'est présentée que pour des raisons de lisibilité.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
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
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Cette méthode est appelée lors de la destruction de l'assistant. Elle est 
	 * utiliser pour libèrer l'espace mémoire utilisé par les variables des
	 * classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
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
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Dans le cas de ce panneau, elle ne fait aucun traitement de particulier,
	 * car elle ne fait pas à proprement parlé de la cinématique de 
	 * l'application.
	 * 
	 * Paramètre : 
	 *  - tabModels : Le modèle de données avant modification.
	 * 
	 * Retourne : Le modèle de données inchangé.
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
	 * Cette méthode retourne le tableau de données contenu dans la
	 * JTable.
	 * Cette méthode est appelée lorsque le panneau GenericTransitionGroup
	 * souhaite mettre à jour son propre tableau de ComponentTransition.
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
	 * Cette méthode permet de définir la méthode de callback en
	 * cas d'appui sur la touche "Entrée" sur un objet JComponent
	 * passé en argument.
	 * La méthode de callback est la méthode validateInput().
	 *
	 * Arguments:
	 *  - component: Un objet JComponent sur lequel le callback
	 *    doit être défini.
	 * ----------------------------------------------------------*/
	protected void setEnterCallback(JComponent component) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"BaseDialog", "setEnterCallback");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("component=" + component);
		// On vérifie la validité de l'argument
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
	 * Cette attribut maintient une référence sur le panneau générique
	 * des transitions ayant appelé ce panneau.
	 * ------------------------------------------------------------*/
	private GenericTransitionDescriptionPanel _parentPanel;

	/*------------------------------------------------------------
	 * Nom : _specificTransitionTable
	 * 
	 * Description :
	 * Cette attribut représente un tableau récapitulant l'ensemble
	 * des transitions définis par l'utilisateur.
	 * ------------------------------------------------------------*/
	private JTable _specificTransitionTable;

	/*----------------------------------------------------------
	 * Nom : _startStateBox
	 * 
	 * Description :
	 * Cette attribut définit une liste déroulante pour le choix
	 * de l'état de départ dans le sous panneau de saisie.
	 * ----------------------------------------------------------*/
	private JComboBox _startStateBox;

	/*------------------------------------------------------------
	 * Nom : _commandTextField
	 * 
	 * Description :
	 * Cette attribut définit un champ de saisie pour la commande
	 * d'une action d'exploitation dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _commandTextField;

	/*----------------------------------------------------------
	 * Nom : _commandTypeBox
	 * 
	 * Description :
	 * Cette attribut définit une liste déroulante pour le choix
	 * du type de commande dans le sous panneau de saisie.
	 * ----------------------------------------------------------*/
	private JTextField _commandTypeTextField;

	/*----------------------------------------------------------
	 * Nom : _checkIntervalTextField
	 * 
	 * Description :
	 * Cette attribut définit un champ de saisie pour l'interval
	 * de controle d'un démon dans le sous panneau de saisie.
	 * ----------------------------------------------------------*/
	private JFormattedTextField _checkIntervalTextField;

	/*----------------------------------------------------------
	 * Nom : _checkTimeOutTextField
	 * 
	 * Description :
	 * Cette attribut définit un champ de saisie pour le Time Out
	 * de controle d'un démon dans le sous panneau de saisie.
	 * ----------------------------------------------------------*/
	private JFormattedTextField _checkTimeOutTextField;

	/*------------------------------------------------------------
	 * Nom : _commandButton
	 * 
	 * Description :
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * afficher une fenêtre de recherche afin de récupérer le chemin
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
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * supprimer la ligne sélectionner dans le tableau .
	 * ------------------------------------------------------------*/
	private JButton _removeButton;

	/*------------------------------------------------------------
	 * Nom : _applyButton
	 * 
	 * Description :
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * enregistrer les informations saisies dans le sous panneau de 
	 * saisie, dans le tableau des actions.
	 * ------------------------------------------------------------*/
	private JButton _applyButton;

	/*----------------------------------------------------------
	 * Nom : _cancelButton
	 * 
	 * Description :
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * annuler la saisie des informations spécifiques liées à 
	 * une transition vers un état d'arrivée.
	 * ----------------------------------------------------------*/
	private JButton _cancelButton;

	/*----------------------------------------------------------
	 * Nom : _validateButton
	 * 
	 * Description :
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * valider la saisie des informations spécifiques liées à 
	 * une transition vers un état d'arrivée.
	 * ----------------------------------------------------------*/
	private JButton _validateButton;

	/*------------------------------------------------------------
	 * Nom : TransitionTableModel
	 * 
	 * Description :
	 * Cette classe représente le modele associé à la JTable.
	 * Elle dérive de AbstractTableModel.
	 * Elle se caractérise de 2 tableaux:
	 *  - un pour les titres des diverses colonnes
	 *  - un pour les données de chaque ligne.
	 * Chaque ligne est une instance de ComponentTransition.
	 * ------------------------------------------------------------*/
	private class TransitionTableModel extends AbstractTableModel {

		/*------------------------------------------------------------
		 * Nom : _titles
		 * 
		 * Description :
		 * Ce tableau de String représente les titres des colonnes de la
		 * JTable.
		 * ------------------------------------------------------------*/
		private String[] _titles;

		/*------------------------------------------------------------
		 * Nom : _datas
		 * 
		 * Description :
		 * Cet attribut est un tableau de ComponentTransition.
		 * Chaque élément du tableau représente une ligne de la JTable
		 * et contient les informations d'une action.
		 * ------------------------------------------------------------*/
		private ArrayList<ComponentTransition> _datas;

		private ArrayList<ComponentTransition> _deleteDatas;

		/*------------------------------------------------------------
		 * Nom : TransitionTableModel
		 * 
		 * Description :
		 * Cette méthode est le contructeur de la classe.
		 * Son rôle est de créer les deux tableaux de titres et de données.
		 * Pour le tableau des titres, elle le remplira à partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/
		public TransitionTableModel() {

			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionTableModel", "TransitionTableModel");
			trace_methods.beginningOfMethod();

			// Création du tableau des titres
			_titles = new String[5];
			// La première colonne représentera l'état de départ de la
			// transition
			_titles[0] = MessageManager
					.getMessage("&ComponentConfiguration_StartState");
			// La deuxième colonne représentera la commande associée
			// à la transition
			_titles[1] = MessageManager
					.getMessage("&ComponentConfiguration_Command");
			// La troisième colonne représentera le type de la commande
			// associée à la transition
			_titles[2] = MessageManager
					.getMessage("&ComponentConfiguration_CommandType");
			// La quatrième colonne représentera l'interval de contrôle
			// associée à la transition
			_titles[3] = MessageManager
					.getMessage("&ComponentConfiguration_CheckInterval");
			// La cinquième colonne représentera le time out
			// associée à la transition
			_titles[4] = MessageManager
					.getMessage("&ComponentConfiguration_CheckTimeOut");
			// La dernière colonne représentera l'Executive User de la
			// transition
			/*
			 * _titles[5] =
			 * MessageManager.getMessage("&ComponentConfiguration_ExecutiveUser");
			 */

			// Création du tableau de données
			_datas = new ArrayList<ComponentTransition>();
			_deleteDatas = new ArrayList<ComponentTransition>();

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getColumnCount
		 * 
		 * Description : 
		 * Cette méthode retourne le nombre de colonnes de la JTable. 
		 * Ce nombre est donné par la dimension du tableaux des titres.
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
		 * Cette méthode retourne le nombre de lignes de la JTable. 
		 * Ce nombre est donné par la dimension du tableaux des données.
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
		 * Cette méthode retourne le tableau de ComponentTransition contenant
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
		 * Cette méthode recherche la présence d'une transition dans la JTable
		 * à partir du nom de l'état de départ passé en paramètre. Elle retourne
		 * le numéro de la ligne correspondant à la transition si elle existe,
		 * -1 sinon.
		 * 
		 * Argument :
		 *  - startStateName : une chaîne de caractères corespondant au nom
		 *    de l'état de la transition recherchée
		 *    
		 * Retourne : un entier, le numéro de ligne de la transition, -1 si 
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
		 * Cette méthode retourne la valeur de la cellule de la JTable
		 * pour la position (ligne, colonne) donnée.
		 * Le numéro de ligne correspond à une transition particulière.
		 * Le numéro de colonne indique le champ : 
		 * 	- 0 pour le nom de l'état de départ
		 *  - 1 pour la commande
		 *  - 2 pour le type de la commande
		 *  - 3 pour l'interval de contrôle
		 *  - 4 pour le Time Out
		 * 
		 * Arguments :
		 *  - line : le numéro de ligne
		 *  - column : la numéro de la colonne
		 *    
		 * Retourne : l'élement à la position souhaité, null si rien 
		 * n'est trouvé.
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
		 * Cette méthode affecte une nouvelle valeur à la cellule de 
		 * la JTable pour la position (ligne, colonne) donnée.
		 * Le numéro de ligne correspond à une transition particulière.
		 * Le numéro de colonne indique le champ : 
		 *  - 1 pour la commande
		 *  - 2 pour le type de la commande
		 *  - 3 pour l'interval de contrôle
		 *  - 4 pour le Time Out
		 *  - 5 pour l'Executive User
		 * La colonne 0 ne peut être modifiée.
		 * 
		 * Arguments :
		 *  - obj : la nouvelle valeur de la cellule
		 *  - line : le numéro de ligne
		 *  - column : la numéro de la colonne
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
		 * Cette méthode retourne le nom d'une colonne de la JTable. 
		 * 
		 * Argument :
		 * 
		 * Retourne : une chaine de caractère : le nom de la colonne.
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
		 * Cette méthode ajoute un nouvel élément (une transition) au 
		 * tableau de données. 
		 * 
		 * Argument : 
		 *  - element : L'élément à ajouter
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
		 * Cette méthode supprime un élément (une transition) du tableau 
		 * de données. 
		 * 
		 * Argument : 
		 *  - line : Le numéro de ligne de l'élément à supprimer
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
		 * Cette méthode supprime tous les élements (les transitions) du tableau 
		 * de données. 
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
		 * Cette méthode retourne le ComponentTransition de la ligne 
		 * demandée. 
		 * 
		 * Argument : 
		 *  - line : Le numéro de ligne de l'élément à récupérer
		 *  
		 * Retourne : Le ComponentTransition de la ligne sélectionnée.
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
		 * Cette méthode ajoute au tableau des éléments à supprimer de la
		 * base, le ComponentTransition passé en paramètre. 
		 * 
		 * Argument : 
		 *  - transi : Le ComponentTransition qu'il faudra supprimer 
		 *    de la base de données.
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
		 * Cette méthode retire au tableau des éléments à supprimer de la
		 * base, le ComponentTransition d'index égal à l'index passé 
		 * en paramètre. 
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
		 * Cette méthode retourne du tableau des éléments à supprimer de la
		 * base, le ComponentTransition d'index égal à l'index passé 
		 * en paramètre. 
		 * 
		 * Argument : 
		 *  - index : L'index du ComponentTransition qu'il faut récupérer
		 *    dans le tableau.
		 * 
		 * Retourne : Le ComponentTransition supprimé de la ligne
		 * souhaitée.
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
		 * Cette méthode retourne l'ensemble du tableau des 
		 * ComponentTransition à supprimer de la base de données. 
		 * 
		 * Retourne : Le tableau des ComponentTransition à supprimer.
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
	 * Cette attribut maintient une référence vers le modele associé 
	 * à la JTable.
	 * ------------------------------------------------------------*/
	private TransitionTableModel _tableModel;

	/*----------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette méthode est appelé lors de la construction du panneau.
	 * Son rôle est d'instancier les composants graphiques du panneau 
	 * et de les positionner.
	 * ----------------------------------------------------------*/
	private void makePanel() {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SpecificTransitionDescriptionPanel", "makePanel");
		trace_methods.beginningOfMethod();

		// On présente le panneau au moyen d'une phrase descriptive
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

		// On intégre un défilement dans le tableau des transitions spécifiques
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

		// on définit un label pour le champs "Etat de départ"
		JLabel startStateLabel = new JLabel();
		startStateLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_StartState")
				+ " :   *");

		// on définit un label pour le champs "Commande"
		JLabel commandLabel = new JLabel();
		commandLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_Command")
				+ " :   *");

		// on définit un label pour le champs "Type de commande"
		JLabel commandTypeLabel = new JLabel();
		commandTypeLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_CommandType")
				+ " :   *");

		// on définit un label pour le champs "Intervalle de contrôle"
		JLabel checkIntervalLabel = new JLabel();
		checkIntervalLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_CheckInterval")
				+ " : ");

		// on définit un label pour le champs "Délai maximum"
		JLabel checkTimeOutLabel = new JLabel();
		checkTimeOutLabel.setText(MessageManager
				.getMessage("&ComponentConfiguration_CheckTimeOut")
				+ " : ");

		// On définit un label pour indiquer que l'asterisque en fin des noms de
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
				// Ouverture d'une fenêtre de recherche d'un fichier
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
		 * Décommenter cette partie de code pour prendre en compte le bouton
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
	 * Cette méthode est appelée lorsque l'utilisateur a cliqué sur 
	 * le bouton 'Appliquer' de la zone de saisie. Son rôle est de 
	 * contrôler les informations saisies par l'utilisateur et de les
	 * enregistrer dans la table si tout est correcte.
	 *------------------------------------------------------------*/
	private void onClickButtonApply() {
		// On souhaite enregistrer les données saisies
		if (_commandTextField.getText().equals("")) {
			DialogManager.displayDialog("Information", 
				"&ComponentConfiguration_MessageInformation", null, null);
		} else {
			// On récupère les champs
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
			// tableau associée au nom de l'état de départ si
			// elle existe.

			if (command.equals("") || commandType.equals("")) {
				DialogManager.displayDialog("Information", 
					"&ComponentConfiguration_MessageInformation", null, null);
			} else {
				int row = _tableModel.findRow(startStateName);

				if (row == -1) {
					// Cette ligne n'existe pas, c'est une nouvelle
					// transition.
					// On recherche l'état associé au nom sélectionné

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
						// On crée la nouvelle transition
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

						// Une transition a été saisie, on débloque le bouton
						// vers
						// l'écran précédent.
						if (!_validateButton.isEnabled())
							_validateButton.setEnabled(true);
					} else {
						DialogManager.displayDialog("Information",
							"&ERR_ErrorWhileSavingDataNoSelectedState",
							null, null);
					}
				} else {
					// La transition existe donc on la met à jour
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

						// On recharge le tableau et on le réaffiche
						_tableModel.fireTableRowsUpdated(row, row);

						// On efface les champs de saisie
						_startStateBox.setSelectedIndex(0);
						_commandTextField.setText("");
						_commandTypeTextField.setText("");
						_checkIntervalTextField.setText("");
						_checkTimeOutTextField.setText("");

						_specificTransitionTable.getSelectionModel()
								.clearSelection();

						// Une transition a été saisie, on débloque le bouton
						// vers
						// l'écran précédent.
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
	 * Cette méthode est appelée lorsque l'utilisateur a cliqué sur 
	 * le bouton 'Effacer' de la zone de saisie. Son rôle est de 
	 * supprimer la ligne sélectionnée du tableau.
	 *------------------------------------------------------------*/
	private void onClickRemoveButton() {

		// On récupère la ligne sélectionnée du tableau si il y
		// en a une.
		int selectedRow = _specificTransitionTable.getSelectedRow();

		// Si une ligne du tableau est sélectionnée
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
					// A gros problème
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

			// Une transition doit au moins être spécifiée. On bloque
			// donc le retour à l'écran précédent si aucune transition
			// n'a été spécifiée.
			if (_tableModel.getRowCount() == 0)
				_validateButton.setEnabled(false);
			else if (!_validateButton.isEnabled())
				_validateButton.setEnabled(true);

			// On désactive le bouton supprimer
			_removeButton.setEnabled(false);
		}

	}

	/*------------------------------------------------------------
	 * Nom : onClickNewButton
	 * 
	 * Description :
	 * Cette méthode est appelée lorsque l'utilisateur a cliqué sur 
	 * le bouton 'Nouveau' de la zone de saisie. Son rôle est d'effacer 
	 * le contenu de la zone de saisie ainsi que la ligne sélectionner 
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

		// Une transition doit au moins être spécifiée. On bloque
		// donc le retour à l'écran précédent si aucune transition
		// n'a été spécifiée.
		if (_tableModel.getRowCount() == 0)
			_validateButton.setEnabled(false);
		else if (!_validateButton.isEnabled())
			_validateButton.setEnabled(true);

		// On désactive le bouton supprimer
		_removeButton.setEnabled(false);

	}

	/*------------------------------------------------------------
	 * Nom : onSelectionElement
	 * 
	 * Description :
	 * Cette méthode est appelée lorsque l'utilisateur a cliqué sur 
	 * une ligne du tableau. Son rôle est de pré remplir les champs
	 * de saisie pour permettre la modification de ceux-ci si ils ne
	 * font pas partit de la clé primaire.
	 * Cette méthode est également appelée en cas de déselection d'une
	 * ligne dans la table, elle efface alors la sélection et le 
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
	 * Cette méthode est appelée lorsque les champs ont changés dans 
	 * la zone de saisie des données.
	 * Elle permet de mettre à jour l'état du bouton "Appliquer" en 
	 * fonction de la présence ou non des informations.
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
