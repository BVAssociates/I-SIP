package com.bv.isis.console.impl.processor.appcfg.view;

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
import com.bv.isis.console.impl.processor.appcfg.ApplicationConfigurationProcessor;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationGroupTransitions;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationStates;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationTransitions;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.impl.processor.config.implementation.view.ListDialog;
import com.bv.isis.console.node.GenericTreeObjectNode;

public class ApplicationGroupTransitionsPanel extends AbstractPanel {

	public ApplicationGroupTransitionsPanel(WindowInterface window) {
		_window = window;
		makePanel();
		
		_transitions = null;
		
		_subPanel = new ApplicationTransitionsPanel(window, this);
	}
	
	public void beforeDisplay(ArrayList<ModelInterface> modele) {
		
		_tableModel = new GroupTransitionsTableModel();
		_endStateComboBox.removeAllItems();
		
		if (modele != null) {
			for ( int i=0 ; i < modele.size() ; i++) {
				if ( modele.get(i) instanceof PortalApplicationGroupTransitions ) {
					PortalApplicationGroupTransitions group = 
						(PortalApplicationGroupTransitions) modele.get(i);
					if (group.getFlag() != 'S')
						_tableModel.addElement(group);
					else
						_tableModel.addDeleteElement(group);
				}
				else if (modele.get(i) instanceof PortalApplicationStates) {
					PortalApplicationStates c = (PortalApplicationStates) modele.get(i);
					if (c.getFlag() != 'S' && !c.getStateName().equals(
							MessageManager.getMessage("&AppCfg_AnyState"))) {
						_endStateComboBox.addItem(c.getStateName());
					}
				}
			}
		}
		_groupTransitionsTable.setModel( _tableModel );
		
	}

	public boolean afterDisplay() {
		return true;
	}

	public boolean beforeHide() {
		return true;
	}

	public boolean afterHide() {
		return true;
	}

	public boolean end() {
		return true;
	}

	public ArrayList<ModelInterface> update(ArrayList<ModelInterface> tabModels) {
		
		ArrayList<PortalApplicationGroupTransitions> newElements = _tableModel.getDatas();
		ArrayList<PortalApplicationGroupTransitions> deleteElements = _tableModel.getDeleteDatas();

		if (tabModels != null) {
			// On supprime tout les groupes de transitions
			for (int index = 0; index < tabModels.size(); index++) {
				if (tabModels.get(index) instanceof PortalApplicationGroupTransitions) {
					tabModels.remove(index);
					// On recule d'une case dans le tableau pour ne pas oublier
					// l'élément décalé
					index--;
				}
			}
		} else
			// Le tableau était inexistant, on le crée
			tabModels = new ArrayList<ModelInterface>();

		// On ajoute les nouvelles transitions au tableau de ModelInterface
		tabModels.addAll(newElements);
		tabModels.addAll(deleteElements);

		return tabModels;
	}
	
	
	protected void saveTransitions(ArrayList<PortalApplicationTransitions> array) {

		_transitions = array;
		fieldsHaveChanged();
	
	}

	/*----------------------------------------------------------
	* Nom: setEnterCallback
	*
	* Description:
	* Cette méthode permet de définir la méthode de callback en
	* cas d'appui sur la touche "Entrée" sur un objet JComponent
	* passé en argument.
	* La méthode de callback est la méthode onClickApplyButton().
	*
	* Arguments:
	*  - component: Un objet JComponent sur lequel le callback
	*    doit être défini.
	* ----------------------------------------------------------*/
	protected void setEnterCallback(JComponent component)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ConfigurationFileDescriptionPanel", "setEnterCallback");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("component=" + component);
		// On vérifie la validité de l'argument
		if(component == null)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On positionne le callback sur Entree
		component.addKeyListener(new KeyAdapter()
		{
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					onClickApplyButton();
				}
				else
				{
					fieldsHaveChanged();
				}
			}
		});
		trace_methods.endOfMethod();
	}
	
	
	private ArrayList<PortalApplicationTransitions> _transitions;
	
	
	private JTable _groupTransitionsTable;
	
	private JTextField _nameTextField;
	
	private JComboBox _endStateComboBox;
	
	private JTextField _descTextField;
	
	private JTextField _respTextField;
	
	private JButton _respButton;
	
	private JButton _applyButton;
	
	private JButton _removeButton;
	
	private JButton _newButton;

	private JRadioButton _inferiorButton;
	
	private JRadioButton _superiorButton;
	
	private JRadioButton _identicalButton;
	
	private JButton _subDefButton;
	
	private ApplicationTransitionsPanel _subPanel;

	private class GroupTransitionsTableModel extends AbstractTableModel {

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
		 * Cet attribut est un tableau de PortalApplicationGroupTransitions.
		 * Chaque élément du tableau représente une ligne de la JTable
		 * et contient les informations d'une action.
		 * ------------------------------------------------------------*/
		private ArrayList<PortalApplicationGroupTransitions> _datas;

		
		private ArrayList<PortalApplicationGroupTransitions> _deleteDatas;

		/*------------------------------------------------------------
		 * Nom : TransitionGroupsTableModel
		 * 
		 * Description :
		 * Cette méthode est le contructeur de la classe.
		 * Son rôle est de créer les deux tableaux de titres et de données.
		 * Pour le tableau des titres, elle le remplira à partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/
		public GroupTransitionsTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"GroupTransitionsTableModel", "TransitionGroupsTableModel");
			trace_methods.beginningOfMethod();

			// Création du tableau des titres
			_titles = new String[5];
			// La première colonne représentera le nom de la transition
			_titles[0] = MessageManager
					.getMessage("&AppCfg_Name");
			// La deuxième colonne représentera l'état d'arrivée associé
			// à la transition
			_titles[1] = MessageManager
					.getMessage("&AppCfg_EndState");
			// La troisième colonne représentera la description associée
			// à la transition
			_titles[2] = MessageManager
					.getMessage("&AppCfg_Description");
			// La quatrième colonne représentera le niveau fonctionnel associé
			// à la transition
			_titles[3] = MessageManager
					.getMessage("&AppCfg_TransitionType");
			// La quatrième colonne représentera le niveau fonctionnel associé
			// à la transition
			_titles[4] = MessageManager
					.getMessage("&AppCfg_Responsabilities");

			// Création du tableau de données
			_datas = new ArrayList<PortalApplicationGroupTransitions>();
			_deleteDatas = new ArrayList<PortalApplicationGroupTransitions>();

			trace_methods.endOfMethod();
		}

		/*------------------------------------------------------------
		 * Nom : getColumnCount
		 * 
		 * Description : 
		 * Cette méthode retourne le nombre de colonnes de la JTable. 
		 * Ce nombre est donné par la dimmension du tableaux des titres.
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
		 * Cette méthode retourne le nombre de lignes de la JTable. 
		 * Ce nombre est donné par la dimmension du tableaux des données.
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
		 * Cette méthode retourne le tableau de PortalApplicationGroupTransitions
		 * contenant l'ensemble des informations saisies par l'utilisateur.
		 * 
		 * Retourne : Un tableau de PortalApplicationGroupTransitions
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationGroupTransitions> getDatas() {
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
		 * Cette méthode recherche la présence d'une transition dans la JTable
		 * à partir du nom de la transition passé en paramètre. Elle retourne
		 * le numéro de la ligne correspondant à cet action si elle existe,
		 * -1 sinon.
		 * 
		 * Argument :
		 *  - transitionName : une chaîne de caractères corespondant au nom
		 *    de l'action recherché
		 *    
		 * Retourne : un entier : le numéro de ligne de la transition, -1 si 
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
		 * Cette méthode retourne la valeur de la cellule de la JTable
		 * pour la position (ligne, colonne) donnée.
		 * Le numéro de ligne correspond à une transition particulière.
		 * Le numéro de colonne indique le champ : 
		 * 	- 0 pour le nom de la transition
		 *  - 1 pour son état d'arrivé
		 *  - 2 pour sa description
		 *  - 3 pour son niveau fonctionel
		 *  - 4 pour ses responsabilités 
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
					"TransitionGroupsTableModel", "getValue");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				PortalApplicationGroupTransitions ctg = _datas.get(line);
				if (ctg != null) {
					if (column == 0) {
						if (ctg.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return ctg.getGroupTransitionName();
						} else {
							trace_methods.endOfMethod();
							return ctg.getNewGroupTransitionName();
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
		 * Cette méthode affecte une nouvelle valeur à la cellule de 
		 * la JTable pour la position (ligne, colonne) donnée.
		 * Le numéro de ligne correspond à une transition particulière.
		 * Le numéro de colonne indique le champ : 
		 *  - 0 pour le nom de la transition
		 *  - 2 pour sa description
		 *  - 3 pour son niveau fonctionnel
		 *  - 4 pour ses responsabilités
		 *  La colonne 1 ne peut être modifiée.
		 * 
		 * Arguments :
		 *  - obj : la nouvelle valeur de la cellule
		 *  - line : le numéro de ligne
		 *  - column : la numéro de la colonne
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
				PortalApplicationGroupTransitions ctg = _datas.get(line);
				if (ctg != null) {
					if (column == 0)
						ctg.setGroupTransitionName(obj.toString());
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
		 * Cette méthode retourne le nom d'une colonne de la JTable. 
		 * 
		 * Argument :
		 * 
		 * Retourne : une chaine de caractère : le nom de la colonne.
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
		 * Cette méthode ajoute un nouvel élément au tableau de données. 
		 * 
		 * Argument : 
		 *  - element : L'élément à ajouter
		 * ------------------------------------------------------------*/
		public void addElement(PortalApplicationGroupTransitions element) {
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
		 * Cette méthode supprime un élément (une transition) du tableau 
		 * de données. 
		 * 
		
		 * Argument : 
		 *  - line : Le numéro de ligne de l'élément à supprimer
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
		 * Cette méthode supprime tous les élements (les transitions) du tableau 
		 * de données. 
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
		 * Cette méthode retourne le PortalApplicationGroupTransitions de la ligne 
		 * demandée. 
		 * 
		 * Argument : 
		 *  - line : Le numéro de ligne de l'élément à récupérer
		 *  
		 * Retourne : Le PortalApplicationGroupTransitions de la ligne sélectionnée.
		 * ------------------------------------------------------------*/
		public PortalApplicationGroupTransitions getElementAt(int line) {
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
		 * Cette méthode ajoute au tableau des éléments à supprimer de la
		 * base, le PortalApplicationGroupTransitions passé en paramètre. 
		 * 
		 * Argument : 
		 *  - group : Le PortalApplicationGroupTransitions qu'il faudra supprimer 
		 *    de la base de données.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(PortalApplicationGroupTransitions group) {
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
		 * Cette méthode retire au tableau des éléments à supprimer de la
		 * base, le PortalApplicationGroupTransitions d'index égal à l'index passé 
		 * en paramètre. 
		 * 
		 * Argument : 
		 *  - index : L'index du PortalApplicationGroupTransitions qu'il faut retirer 
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
		 * Cette méthode retourne du tableau des éléments à supprimer de la
		 * base, le PortalApplicationGroupTransitions d'index égal à l'index passé 
		 * en paramètre. 
		 * 
		 * Argument : 
		 *  - index : L'index du PortalApplicationGroupTransitions qu'il faut récupérer 
		 *    dans le tableau.
		 * 
		 * Retourne : Le PortalApplicationGroupTransitions supprimé de la ligne 
		 * souhaitée.
		 * ------------------------------------------------------------*/
		public PortalApplicationGroupTransitions getDeleteElementAt(int index) {
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
		 * Cette méthode retourne l'ensemble du tableau des 
		 * PortalApplicationGroupTransitions à supprimer de la base de données. 
		 * 
		 * Retourne : Le tableau des PortalApplicationGroupTransitions à supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationGroupTransitions> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransitionGroupsTableModel", "getDeleteDatas");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();

			return _deleteDatas;
		}

	}
	
	private GroupTransitionsTableModel _tableModel;
	
	private void makePanel() {
		
		_tableModel = null;

		_groupTransitionsTable = new JTable(_tableModel);
		_groupTransitionsTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_groupTransitionsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});
		
		// On intégre un défilement dans le tableau des groupes de transitions
		JScrollPane scrollPaneGenericTransitionTable = new JScrollPane();
		scrollPaneGenericTransitionTable
				.setViewportView(_groupTransitionsTable);

		// On présente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(450, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(450, 20));
		firstDescriptionSentence
				.setText(MessageManager
						.getMessage("&AppCfg_FirstDescriptionSentenceGroupTransitions"));

		// On définit un label pour le champ "Nom"
		JLabel nameLabel = new JLabel();
		nameLabel.setMinimumSize(new Dimension(120, 20));
		nameLabel.setMaximumSize(new Dimension(120, 20));
		nameLabel.setPreferredSize(new Dimension(120, 20));
		nameLabel.setText(MessageManager
				.getMessage("&AppCfg_Name")
				+ " :   *");
		// On définit un label pour le champ "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setMinimumSize(new Dimension(120, 20));
		descriptionLabel.setMaximumSize(new Dimension(120, 20));
		descriptionLabel.setPreferredSize(new Dimension(120, 20));
		descriptionLabel.setText(MessageManager
				.getMessage("&AppCfg_Description")
				+ " :   *");

		// On définit un label pour le champ "état d'arrivée"
		JLabel endStateLabel = new JLabel();
		endStateLabel.setMinimumSize(new Dimension(120, 20));
		endStateLabel.setMaximumSize(new Dimension(120, 20));
		endStateLabel.setText(MessageManager
				.getMessage("&AppCfg_EndState")
				+ " : ");

		// On définit un label pour le champ "Responsabilités"
		JLabel responsabilitiesLabel = new JLabel();
		responsabilitiesLabel.setMinimumSize(new Dimension(120, 20));
		responsabilitiesLabel.setMaximumSize(new Dimension(120, 20));
		responsabilitiesLabel.setText(MessageManager
				.getMessage("&AppCfg_Responsabilities")
				+ " :  *");

		// On définit le label "Changement de niveau fonctionnel"
		JLabel changeTransitionTypeLabel = new JLabel();
		changeTransitionTypeLabel.setMinimumSize(new Dimension(450, 20));
		changeTransitionTypeLabel.setMaximumSize(new Dimension(450, 20));
		changeTransitionTypeLabel.setText(MessageManager
				.getMessage("&AppCfg_ChangeTransitionType")
				+ " : *");

		// On définit le label "Définition des commandes de transitions"
		JLabel definitionCommandTransitionLabel = new JLabel();
		definitionCommandTransitionLabel
				.setMinimumSize(new Dimension(450, 20));
		definitionCommandTransitionLabel
				.setMaximumSize(new Dimension(450, 20));
		definitionCommandTransitionLabel
				.setText(MessageManager
						.getMessage("&AppCfg_DefinitionCommandTransition")
						+ " :   *");

		// On définit un label pour le bouton "Action de démarage"
		JLabel superiorLabel = new JLabel(IconLoader.getIcon("arrow_up"));

		// On définit un label pour le bouton "Action d'arrêt"
		JLabel inferiorLabel = new JLabel(IconLoader.getIcon("arrow_down"));

		// On définit un label pour le bouton "Action de démarage"
		JLabel identicalLabel = new JLabel(IconLoader.getIcon("arrow_rotate"));

		// On définit un label pour indiquer que l'asterisque en fin des noms
		// de champs sont obligatoires pour la saisie.
		JLabel obligatoryFieldLabel = new JLabel();
		obligatoryFieldLabel.setMinimumSize(new java.awt.Dimension(200, 20));
		obligatoryFieldLabel.setMaximumSize(new java.awt.Dimension(200, 20));
		obligatoryFieldLabel.setText(MessageManager
				.getMessage("&AppCfg_ObligatoryFields"));

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

		_descTextField = new JTextField();
		setEnterCallback(_descTextField);
		_descTextField.setMinimumSize(new Dimension(328, 20));
		_descTextField.setMaximumSize(new Dimension(328, 20));
		_descTextField.setPreferredSize(new Dimension(328, 20));

		_respTextField = new JTextField();
		setEnterCallback(_respTextField);
		_respTextField.setMinimumSize(new Dimension(280, 20));
		_respTextField.setMaximumSize(new Dimension(280, 20));
		_respTextField.setPreferredSize(new Dimension(280, 20));

		_respButton = new JButton();
		_respButton.setMinimumSize(new Dimension(37, 22));
		_respButton.setMaximumSize(new Dimension(37, 22));
		_respButton.setPreferredSize(new Dimension(37, 22));
		_respButton.setText(MessageManager
				.getMessage("&AppCfg_ResponsabilitiesButton"));
		_respButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickResponsabilitiesButton();
			}
		});

		_applyButton = new JButton();
		_applyButton.setEnabled(false);
		_applyButton.setMinimumSize(new Dimension(120, 21));
		_applyButton.setMaximumSize(new Dimension(120, 21));
		_applyButton.setText(MessageManager
				.getMessage("&AppCfg_ApplyButton"));
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
				.getMessage("&AppCfg_RemoveButton"));
		_removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickRemoveButton();
			}

		});

		_newButton = new JButton();
		_newButton.setMinimumSize(new Dimension(120, 21));
		_newButton.setMaximumSize(new Dimension(120, 21));
		_newButton.setText(MessageManager
				.getMessage("&AppCfg_NewButton"));
		_newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickNewButton();
			}
		});

		_superiorButton = new JRadioButton(MessageManager
				.getMessage("&AppCfg_SuperiorButton"));
		setEnterCallback(_superiorButton);
		_superiorButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				fieldsHaveChanged();
			}
		});

		_inferiorButton = new JRadioButton(MessageManager
				.getMessage("&AppCfg_InferiorButton"));
		setEnterCallback(_inferiorButton);
		_inferiorButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				fieldsHaveChanged();
			}
		});

		_identicalButton = new JRadioButton(MessageManager
				.getMessage("&AppCfg_IdenticalButton"));
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

		_subDefButton = new JButton(IconLoader.getIcon("arrow_right"));
		_subDefButton.setMinimumSize(new Dimension(120, 21));
		_subDefButton.setMaximumSize(new Dimension(120, 21));
		_subDefButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickSubDefButton();
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
		hBox4.add(_descTextField);
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
		hBox66.add(_respTextField);
		hBox66.add(Box.createHorizontalStrut(10));
		hBox66.add(_respButton);
		hBox66.add(Box.createHorizontalStrut(5));

		Box hBox8 = Box.createHorizontalBox();
		hBox8.setMinimumSize(new Dimension(490, 20));
		hBox8.setMaximumSize(new Dimension(490, 20));
		hBox8.add(Box.createHorizontalStrut(5));
		hBox8.add(changeTransitionTypeLabel);

		Box hBox9 = Box.createHorizontalBox();
		hBox9.setMinimumSize(new Dimension(490, 22));
		hBox9.setMaximumSize(new Dimension(490, 22));
		hBox9.add(Box.createHorizontalStrut(130));
		hBox9.add(definitionCommandTransitionLabel);
		hBox9.add(Box.createHorizontalStrut(10));
		hBox9.add(_subDefButton);
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
		vBox2.add(Box.createVerticalStrut(5));
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
		vBox2.setBorder(BorderFactory.createTitledBorder((MessageManager
				.getMessage("&AppCfg_SecondDescriptionSentence"))));

		Box vBox3 = Box.createVerticalBox();
		vBox3.add(vBox1);
		vBox3.add(Box.createVerticalStrut(5));
		vBox3.add(vBox2);
		vBox3.add(Box.createVerticalStrut(5));

		setLayout(new BorderLayout());
		add(vBox3, BorderLayout.NORTH);
		
		
	}
	
	
	private void onSelectionElement(ListSelectionEvent event) {
		
		ListSelectionModel lsm = (ListSelectionModel) event.getSource();

		int index = lsm.getMinSelectionIndex();
		if (index == -1) {
			onClickNewButton();
		} else {

			PortalApplicationGroupTransitions group = _tableModel.getElementAt(index);
			char res = ' ';
			if (group.getFlag() == 'E') {
				_nameTextField.setText(group.getGroupTransitionName());
				_endStateComboBox.setSelectedItem(group.getEndState().getStateName());
				_descTextField.setText(group.getDescription());
				_respTextField.setText(group.getResponsabilities());
				res = group.getTransitionType();
				_transitions = group.getTransitions();
			} 
			else {
				_nameTextField.setText(group.getNewGroupTransitionName());
				_endStateComboBox.setSelectedItem(group.getEndState().getStateName());
				_descTextField.setText(group.getNewDescription());
				_respTextField.setText(group.getNewResponsabilities());
				res = group.getNewTransitionType();
				_transitions = group.getNewTransitions();
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
	
	private void onClickApplyButton() {
		
		String name = _nameTextField.getText();
		String endState = _endStateComboBox.getSelectedItem().toString();
		String description = _descTextField.getText();
		String responsabilités = _respTextField.getText();

		char res = ' ';
		if (_superiorButton.isSelected())
			res = '+';
		else if (_inferiorButton.isSelected())
			res = '-';
		else if (_identicalButton.isSelected())
			res = '=';

		if (name.equals("") || endState.equals("") || description.equals("")
				|| responsabilités.equals("") || res == ' ') {
			DialogManager.displayDialog("Information", MessageManager
					.getMessage("&AppCfg_MessageInformation"),
					null, null);
		} else {
			/*
			 * 1 : Vérifier qu'il n'y ai pas de transitions avec le même état
			 * d'arrivée 2 : Mettre à jour le tableau 3 : Effacer les champs 4 :
			 * actualiser le tableau
			 */
			if (_transitions == null) {
				DialogManager.displayDialog("Information", MessageManager
						.getMessage("&AppCfg_MessageInformationGroupTransitions1"),
						null, null);
			} 
			else {
				int row = _tableModel.findRow(endState);

				// Aucune ligne du tableau n'a été sélectionner
				if (row == -1) {

					ArrayList<ModelInterface> tabModel = _window.getModel();
					// On recherche l'état correspondant à celui choisit
					PortalApplicationStates state = null;
					for (int index = 0; index < tabModel.size(); index++) {
						if (tabModel.get(index) instanceof PortalApplicationStates) {
							state = (PortalApplicationStates) tabModel.get(index);
							if (state.getStateName().equals(endState))
								break;
						}
					}
					//Une fois l'état déterminé, on crée le PortalApplicationGroupTransitions
					if (state != null && state.getStateName() != null
							&& state.getStateName().equals(endState)) {

						// Ce nouveau groupe peut porter sur le même état qu'un ancien groupe
						// précédemment supprimé mais toujours existant en base. Il s'agit
						// donc de mettre à jour cette donnée et non de la créer.
						// On recherche donc si dans les éléments précedemment supprimés, il 
						// n'existe pas un groupe avec le même état d'arrivée
						char flag = 'A';
						PortalApplicationGroupTransitions group = new PortalApplicationGroupTransitions();

						ArrayList<PortalApplicationGroupTransitions> deleteElement = _tableModel.getDeleteDatas();
						
						if (deleteElement != null) {
							for (int index = 0; index < deleteElement.size(); index++) {
								// Si ce groupe existe, on le récupère, et on va le 
								// mettre à jour
								if (deleteElement.get(index).getEndState()
										.getStateName().equals(endState)) {
									flag = 'M';
									group = deleteElement.get(index);
									deleteElement.remove(index);
									break;
								}
							}
						}
						// Dans le cas d'une modification
						if (flag == 'M') {
							// il est nécessaire de vérifier si les nouvelles transitions 
							// spécifiques de ce groupe ne correspondent pas à d'anciennes
							// transitions spécifiques de l'ancien groupe. Si oui, il faut 
							// les mettre à jour.
							for (int indexTab = 0; indexTab < _transitions.size(); indexTab++) {
								PortalApplicationTransitions newTransition = _transitions.get(indexTab);

								ArrayList<PortalApplicationTransitions> array = group.getTransitions();
								if (array != null) {
									for (int indexArray = 0; indexArray < array.size(); indexArray++) {
										PortalApplicationTransitions oldTransition = array.get(indexArray);
										if (newTransition.getStartState().getStateName().equals(
												oldTransition.getStartState().getStateName())) {
											newTransition.setTransitionServices(oldTransition.getTransitionServices());
											newTransition.setFlag('M');
											array.remove(indexArray);
											indexArray--;
										}
									}
								}
								_transitions.addAll(array);
							}
						}

						group.setNewGroupTransitionName(name);
						group.setEndState(state);
						group.setNewDescription(description);
						group.setNewTransitionType(res);
						group.setNewResponsabilities(responsabilités);
						group.setNewTransitions(_transitions);
						group.setFlag(flag);

						_tableModel.addElement(group);

						_transitions = null;
						_nameTextField.setText("");
						if (_endStateComboBox.getItemCount() > 0)
							_endStateComboBox.setSelectedIndex(0);
						_descTextField.setText("");
						_respTextField.setText("");
						_groupTransitionsTable.getSelectionModel()
								.clearSelection();

						_removeButton.setEnabled(false);
						_applyButton.setEnabled(false);
						_inferiorButton.setSelected(true);
					}
				} 
				else {
					// La transition existe donc on la met à jour
					int selectedRow = _groupTransitionsTable.getSelectedRow();

					if (selectedRow == -1) {
						DialogManager.displayDialog("Information",MessageManager
								.getMessage("&AppCfg_MessageInformationGroupTransitions2"),
								null, null);
					} 
					else {
						PortalApplicationGroupTransitions group = _tableModel.getElementAt(row);

						group.setNewTransitions(_transitions);
						group.setNewGroupTransitionName(name);
						group.setNewDescription(description);
						group.setNewTransitionType(res);
						group.setNewResponsabilities(responsabilités);

						if (group.getFlag() == 'E')
							group.setFlag('M');

						// On recharge le tableau et on le réaffiche
						_tableModel.fireTableRowsUpdated(row, row);
						_transitions = null;
						
						// On efface les champs de saisie
						_nameTextField.setText("");
						if (_endStateComboBox.getItemCount() > 0)
							_endStateComboBox.setSelectedIndex(0);
						_descTextField.setText("");
						_respTextField.setText("");
						_groupTransitionsTable.getSelectionModel().clearSelection();
						_removeButton.setEnabled(false);
						_applyButton.setEnabled(false);
						_inferiorButton.setSelected(true);
					}
				}
			}
		}
	}
	
	private void onClickRemoveButton() {
		
		int selectedRow = _groupTransitionsTable.getSelectedRow();

		// Si une ligne du tableau est sélectionnée
		if (selectedRow != -1) {
			PortalApplicationGroupTransitions group = _tableModel.getElementAt(selectedRow);
			
			if (group != null) {
				if (group.getFlag() == 'A')
					_tableModel.removeElement(selectedRow);
				else if (group.getFlag() == 'E' || group.getFlag() == 'M') {
					_tableModel.removeElement(selectedRow);
					group.setFlag('S');
					_tableModel.addDeleteElement(group);
				} else {
					// A gros problème
				}
				_tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
			}
			// On efface les champs de saisies
			_nameTextField.setText("");
			if (_endStateComboBox.getItemCount() > 0)
				_endStateComboBox.setSelectedIndex(0);
			_descTextField.setText("");
			_respTextField.setText("");

			_groupTransitionsTable.getSelectionModel().clearSelection();
			_transitions = null;
			
			_inferiorButton.setSelected(true);
			_applyButton.setEnabled(false);
			// On désactive le bouton supprimer
			_removeButton.setEnabled(false);
		}
	}
	
	private void onClickNewButton() {
		
		_transitions = null;
		_groupTransitionsTable.getSelectionModel().clearSelection();
		
		_nameTextField.setText("");
		_endStateComboBox.setEnabled(true);
		if (_endStateComboBox.getItemCount() > 0)
			_endStateComboBox.setSelectedIndex(0);
		_descTextField.setText("");
		_respTextField.setText("");

		_inferiorButton.setSelected(true);

		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
	}
	
	private void onClickResponsabilitiesButton() {
		
		ApplicationConfigurationProcessor c = (ApplicationConfigurationProcessor) _window;

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
					"&AppCfg_ResponsabilitiesList"));

				String valuesList = dialog.getSelectedInfo(responsabilityList,
						_respTextField.getText());
				if (!valuesList.equals("")) {
					_respTextField.setText(valuesList);

				}
			}
		} catch (Exception e) {
			System.out.println("Exception survenue : " + e);
		}
	}
	
	private void onClickSubDefButton() {
		
		if (_subPanel != null) {
			_subPanel.loadTransitionsArray(_transitions);
		
			_window.displayPanel(_subPanel);
			_window.enabledNextAndPreviousButton(false);
		}
		
	}
	
	private void fieldsHaveChanged() {
		
		boolean bool = false;
		if (_superiorButton.isSelected())
			bool = true;
		else if (_inferiorButton.isSelected())
			bool = true;
		else if (_identicalButton.isSelected())
			bool = true;

		if (_nameTextField.getText().equals("") || _descTextField.getText().equals("")
				|| _respTextField.getText().equals("") || bool == false 
				|| _transitions == null) {
			_applyButton.setEnabled(false);
		}
		else {
			_applyButton.setEnabled(true);
		}	
	}
}
