package com.bv.isis.console.impl.processor.appcfg.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.bv.core.gui.IconLoader;
import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationStates;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationTransitionServices;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationTransitions;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;

public class ApplicationTransitionsPanel extends AbstractPanel {

	public ApplicationTransitionsPanel(WindowInterface window, 
			ApplicationGroupTransitionsPanel parentPanel) {
		
		_window = window;
		_parentPanel = parentPanel;
		
		makePanel();
		
		_transitionsServices = null;
		
		_subPanel = new ApplicationTransitionsServicesPanel(window, this);
		
	}
	
	
	public void beforeDisplay(ArrayList<ModelInterface> modele) {

	}

	public void loadTransitionsArray(ArrayList<PortalApplicationTransitions> array) {
		
		_tableModel = new TransitionsTableModel();
		_startStateComboBox.removeAllItems();
		
		ArrayList<ModelInterface> model = _window.getModel();
		if (model != null) {
			for ( int index = 0 ; index < model.size() ; index++) {
				if (model.get(index) instanceof PortalApplicationStates) {
					PortalApplicationStates state = (PortalApplicationStates) model.get(index);
					_startStateComboBox.addItem(state.getStateName());
				}
			}
		}
		
		if (array != null) {
			for ( int index = 0 ; index < array.size() ; index++) {
				PortalApplicationTransitions trans = array.get(index);
					if (trans.getFlag() != 'S')
						_tableModel.addElement(trans.clone());
					else
						_tableModel.addDeleteElement(trans.clone());
			}
		}
		_transitionsTable.setModel( _tableModel );
		
		if (_tableModel.getRowCount() == 0)
			_validateButton.setEnabled(false);
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
		return tabModels;
	}

	public void saveTransitionsServices(ArrayList<PortalApplicationTransitionServices> array) {
		_transitionsServices = array;
		fieldsHaveChanged();
	}
	
	
	private ArrayList<PortalApplicationTransitionServices> _transitionsServices;
	
	private ApplicationGroupTransitionsPanel _parentPanel;
	
	private ApplicationTransitionsServicesPanel _subPanel;
	
	private JTable _transitionsTable;
	
	private JComboBox _startStateComboBox;
	
	private JButton _applyButton;
	
	private JButton _removeButton;
	
	private JButton _newButton;
	
	private JButton _subDefButton;
	
	private JButton _cancelButton;
	
	private JButton _validateButton;
	
	
	
	
	private class TransitionsTableModel extends AbstractTableModel {
		
		/*------------------------------------------------------------
		 * Nom : _titles
		 * 
		 * Description :
		 * Ce tableau de String représente les titres des colonnes de la
		 * JTable.
		 * ------------------------------------------------------------*/
		private String [] _titles;
		
		/*------------------------------------------------------------
		 * Nom : _datas
		 * 
		 * Description :
		 * Cet attribut est un tableau de ComponentConfig.
		 * Chaque élément du tableau représente une ligne de la JTable
		 * et contient les informations d'un fichier.
		 * ------------------------------------------------------------*/
		private ArrayList<PortalApplicationTransitions> _datas;
		
		private ArrayList<PortalApplicationTransitions> _deleteDatas;
		
		/*------------------------------------------------------------
		 * Nom : StatesTableModel
		 * 
		 * Description :
		 * Cette méthode est le contructeur de la classe.
		 * Son rôle est de créer les deux tableaux de titres et de données.
		 * Pour le tableau des titres, elle le remplira à partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/		
		public TransitionsTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ServicesTableModel", "ConfigTableModel");
			trace_methods.beginningOfMethod();
			
			// Création du tableau des titres
			_titles = new String [1];
			// La première colonne représentera le nom du fichier
			_titles[0] = MessageManager.getMessage("&AppCfg_Name");
			
			// Création du tableau de données
			_datas = new ArrayList<PortalApplicationTransitions>();
			_deleteDatas = new ArrayList<PortalApplicationTransitions>();
			
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
					"ConfigTableModel", "getColumnCount");
			trace_methods.beginningOfMethod();
			
			if (_titles != null) {
				trace_methods.endOfMethod();
				return _titles.length;
			}
			else {
				trace_methods.endOfMethod();
				return 0;
			}
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
					"ConfigTableModel", "getRowCount");
			trace_methods.beginningOfMethod();
			
			if (_datas != null) {
				trace_methods.endOfMethod();
				return _datas.size();
			}
			else {
				trace_methods.endOfMethod();
				return 0;
			}
		}
		
		/*------------------------------------------------------------
		 * Nom : getDatas
		 * 
		 * Description : 
 		 * Cette méthode retourne le tableau de ComponentConfig contenant
 		 * l'ensemble des informations saisies par l'utilisateur.
 		 * 
 		 * Retourne : Un tableau de ComponentConfig
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationTransitions> getDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "getDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();

			return _datas;
		}
		
		/*------------------------------------------------------------
		 * Nom : findRow
		 * 
		 * Description : 
 		 * Cette méthode recherche la présence d'un fichier dans la JTable
 		 * à partir du nom de fichier passé en paramètre et du chemin absolu
 		 * saisient. Elle retourne le numéro de la ligne correspondant 
 		 * à ce fichier si elle existe, -1 sinon.
 		 * 
 		 * Argument :
 		 *  - fileName : une chaîne de caractères correspondant au nom
 		 *    du fichier recherché
 		 *  - filePath : un chaîne de caractères correspondant au chemin absolu
 		 *    du fichier
 		 *    
 		 * Retourne : un entier : le numéro de ligne du fichier, -1 si 
 		 * elle n'existe pas.
		 * ------------------------------------------------------------*/
		public int findRow(String stateName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("stateName=" + stateName);

			if (_datas != null)
			{
				for (int index=0 ; index < _datas.size() ; index++) {
					PortalApplicationTransitions serv = _datas.get(index);
					if (serv != null && serv.getStartState() != null) {
						
						String name = serv.getStartState().getStateName();
						
						if (name.equals(stateName)){
							trace_methods.endOfMethod();
							return index;
						}
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
 		 * Le numéro de ligne correspond à un fichier particulier.
 		 * Le numéro de colonne indique le champ : 
 		 * 	- 0 pour le nom du fichier
 		 *  - 1 pour sa description
 		 *  - 2 pour son chemin absolu
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
					"ConfigTableModel", "getValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				PortalApplicationTransitions serv = _datas.get(line);
				if (serv != null) {
					if (column == 0) {
						trace_methods.endOfMethod();
						return serv.getStartState().getStateName();
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
 		 * Cette méthode retourne le nom d'une colonne de la JTable. 
 		 * 
 		 * Argument :
 		 *  - column : la colonne dont on veut le nom
 		 * 
 		 * Retourne : une chaine de caractère : le nom de la colonne.
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
 		 * Cette méthode ajoute un nouvel élément (un fichier) au 
 		 * tableau de données. 
 		 * 
 		 * Argument : 
 		 *  - element : L'élément à ajouter
		 * ------------------------------------------------------------*/
		public void addElement(PortalApplicationTransitions element){
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
 		 * Cette méthode supprime un élément (un fichier) au tableau 
 		 * de données. 
 		 * 
 		 * Argument : 
 		 *  - line : Le numéro de ligne de l'élément à supprimer
		 * ------------------------------------------------------------*/
		public void removeElement(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "removeElement");
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
 		 * Cette méthode retourne le ComponentConfig de la ligne demandée. 
 		 * 
 		 * Argument : 
 		 *  - line : Le numéro de ligne de l'élément à récupérer
 		 *  
 		 * Retourne : Le ComponentConfig de la ligne sélectionnée.
		 * ------------------------------------------------------------*/
		public PortalApplicationTransitions getElementAt(int line) {
			
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "getElementAt");
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
 		 * base, le ComponentConfig passé en paramètre. 
 		 * 
 		 * Argument : 
 		 *  - config : Le ComponentConfig qu'il faudra supprimer de la base
 		 *    de données.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(PortalApplicationTransitions service) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "addDeleteElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("config=" + service);
			
			if (_deleteDatas != null)
				_deleteDatas.add(service);
			
			trace_methods.endOfMethod();
		}
		
		/*------------------------------------------------------------
		 * Nom : removeDeleletElement
		 * 
		 * Description : 
 		 * Cette méthode retire au tableau des éléments à supprimer de la
 		 * base, le ComponentConfig d'index égal à l'index passé en paramètre. 
 		 * 
 		 * Argument : 
 		 *  - index : L'index du ComponentConfig qu'il faut retirer du tableau.
		 * ------------------------------------------------------------*/
		public void removeDeleletElement(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "removeDeleletElement");
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
 		 * base, le ComponentConfig d'index égal à l'index passé en paramètre. 
 		 * 
 		 * Argument : 
 		 *  - index : L'index du ComponentConfig qu'il faut récupérer dans le
 		 *    tableau.
 		 * 
 		 * Retourne : Le ComponentConfig supprimé de la ligne souhaitée
		 * ------------------------------------------------------------*/
		public PortalApplicationTransitions getDeleteElementAt(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "getDeleteElementAt");
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
 		 * Cette méthode retourne l'ensemble du tableau des ComponentConfig
 		 * à supprimer de la base de données. 
 		 * 
 		 * Retourne : Le tableau des ComponentConfig à supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationTransitions> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "getDeleteDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			
			return _deleteDatas;
		}
	}
	
	private TransitionsTableModel _tableModel;
	
	private void makePanel() {
		
		_tableModel = null;
		
		// On présente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(476, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(476, 20));
		firstDescriptionSentence.setText(
				MessageManager.getMessage("&AppCfg_FirstDescriptionSentenceTransitions"));
		
		_transitionsTable = new JTable(_tableModel);
		_transitionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_transitionsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});
		
		// On intégre un défilement dans le tableau
		JScrollPane scrollPaneTransTable = new JScrollPane();
		scrollPaneTransTable.setViewportView(_transitionsTable);
		scrollPaneTransTable.setMinimumSize(new Dimension(476, 143));
		scrollPaneTransTable.setMaximumSize(new Dimension(476, 143));
		
		JPanel panel1 = new JPanel();
		panel1.setMinimumSize(new Dimension(480,440));
		panel1.setMaximumSize(new Dimension(480,440));
		panel1.setPreferredSize(new Dimension(480,440));
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
	    panel1.add(scrollPaneTransTable, c1);
		
		// On définit un label pour le champs "Nom"
	    JLabel startStateLabel = new JLabel();
		startStateLabel.setText(
				MessageManager.getMessage("&AppCfg_StartState") 
				+ " :   *");
		// On définit un label pour le champs "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText(
				MessageManager.getMessage("&AppCfg_DefinitionCommandTransitionsStates") 
				+ " :   *");
		
		// champs sont obligatoires pour la saisie
		JLabel obligatoryField = new JLabel();
		obligatoryField.setText(MessageManager.getMessage("&AppCfg_ObligatoryFields"));
		obligatoryField.setMinimumSize(new Dimension(150,20));
		obligatoryField.setMaximumSize(new Dimension(150,20));
				
		_startStateComboBox = new JComboBox();
		_startStateComboBox.setPreferredSize(new Dimension(
				440 - (startStateLabel.getPreferredSize().width), 20));
		_startStateComboBox.setMinimumSize(new Dimension(
				440 - (startStateLabel.getMinimumSize().width), 20));
		_startStateComboBox.setMaximumSize(new Dimension(
				440 - (startStateLabel.getMaximumSize().width), 20));
		
		_applyButton = new JButton();
		_applyButton.setEnabled(false);
		_applyButton.setMinimumSize(new Dimension(120, 21));
		_applyButton.setMaximumSize(new Dimension(120, 21));
		_applyButton.setPreferredSize(new Dimension(120, 21));
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
		_removeButton.setPreferredSize(new Dimension(120, 21));
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
		_newButton.setPreferredSize(new Dimension(120, 21));
		_newButton.setText(MessageManager
				.getMessage("&AppCfg_NewButton"));
		_newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				onClickNewButton();
			}
		});

		_subDefButton = new JButton(IconLoader.getIcon("arrow_right"));
		_subDefButton.setMinimumSize(new Dimension(120, 21));
		_subDefButton.setMaximumSize(new Dimension(120, 21));
		_subDefButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickSubDefButton();
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
				onClickCancelButton();
			}
		});

		_validateButton = new JButton();
		_validateButton.setText(MessageManager
				.getMessage("&AppCfg_ValidateButton"));
		_validateButton.setMinimumSize(new Dimension(120, 21));
		_validateButton.setMaximumSize(new Dimension(120, 21));
		_validateButton.setPreferredSize(new Dimension(120, 21));
		_validateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickValidateButton();
			}
		});
		
		JPanel panel2 = new JPanel();
		panel2.setMinimumSize(new Dimension(480,180));
		panel2.setMaximumSize(new Dimension(480,180));
		panel2.setPreferredSize(new Dimension(480,180));
		panel2.setBorder(BorderFactory.createTitledBorder(
				MessageManager.getMessage("&AppCfg_SecondDescriptionSentence")));
				
		panel2.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.NONE;
	    c.insets = new Insets(5, 5, 5, 5);
		
	    c.gridx = 0;
	    c.gridy = 0;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    c.weightx = c.weighty = 0.0;
	    panel2.add(startStateLabel, c);
		
		c.gridx = 1;
	    c.gridy = 0;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(_startStateComboBox, c);
		
	    Box hBox1 = Box.createHorizontalBox();
		hBox1.add(descriptionLabel);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_subDefButton);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_END;
		panel2.add(hBox1, c);

		Box hBox2 = Box.createHorizontalBox();
	    hBox2.add(_newButton);
		hBox2.add(Box.createHorizontalStrut(10));
		hBox2.add(_removeButton);
		hBox2.add(Box.createHorizontalStrut(10));
		hBox2.add(_applyButton);
		hBox2.setMaximumSize(new Dimension(380,22));
		
	    c.gridx = 0;
	    c.gridy = 3;
	    c.gridwidth = 2;
	    c.gridheight = 1;
	    c.ipady = 15;
	    panel2.add(hBox2, c);

	    c.gridx = 0;
	    c.gridy = 4;
	    c.gridwidth = 2;
	    c.gridheight = 1;
	    c.ipady = 0;
	    c.anchor = GridBagConstraints.LINE_START;
	    panel2.add(obligatoryField, c);
	    
	    c1.gridx = 0;
		c1.gridy = 2;
		c1.gridwidth = 1;
		c1.gridheight = 1;
		c1.ipady = 10;
		panel1.add(panel2, c1);
		
		Box hBox3 = Box.createHorizontalBox();
		hBox3.add(_cancelButton);
		hBox3.add(Box.createHorizontalStrut(10));
		hBox3.add(_validateButton);
		hBox3.add(Box.createHorizontalStrut(12));
		hBox3.setMaximumSize(new Dimension(260, 20));

		c1.gridx = 0;
		c1.gridy = 4;
		c1.gridwidth = 3;
		c1.gridheight = 1;
		c1.anchor = GridBagConstraints.LINE_END;
		panel1.add(hBox3, c1);
	    
		setLayout(new BorderLayout());
		add(panel1, BorderLayout.CENTER);
	}
	
	
	
	private void onSelectionElement(ListSelectionEvent event) {
		
		ListSelectionModel lsm = (ListSelectionModel) event.getSource();

		int index = lsm.getMinSelectionIndex();
		if (index == -1) {
			onClickNewButton();
		} 
		else {
			PortalApplicationTransitions trans = _tableModel.getElementAt(index);
			
			_startStateComboBox.setSelectedItem(trans.getStartState().getStateName());
			if (trans.getFlag() == 'E') {
				_transitionsServices = trans.getTransitionServices();
			}
			else {
				_transitionsServices = trans.getNewTransitionServices();
			}
			
			_startStateComboBox.setEnabled(false);
			
			_applyButton.setEnabled(false);
			_removeButton.setEnabled(true);
		}
	}
	
	private void onClickApplyButton() {
		
		String startState = _startStateComboBox.getSelectedItem().toString();
				
		if (startState.equals("")) {
			System.out.println("Les champs obligatoires doivent être remplis");
		} 
		else {
			if (_transitionsServices == null) {
				System.out.println("La définition des états des services est obligatoires.");
			} 
			else {
				int selectedRow = _transitionsTable.getSelectedRow();
				if (selectedRow == -1) {
					// Nouvel élément
					if (_tableModel.findRow(startState) == -1) {
						
						ArrayList<ModelInterface> tabModel = _window.getModel();
						// On recherche l'état correspondant à celui choisit
						PortalApplicationStates state = null;
						for (int index = 0; index < tabModel.size(); index++) {
							if (tabModel.get(index) instanceof PortalApplicationStates) {
								state = (PortalApplicationStates) tabModel.get(index);
								if (state.getStateName().equals(startState))
									break;
							}
						}
						if (state != null) {
							PortalApplicationTransitions trans = new PortalApplicationTransitions();
							char flag = 'A';
							
							ArrayList<PortalApplicationTransitions> deleteElement = 
								_tableModel.getDeleteDatas();
							if (deleteElement != null) {
								for (int index = 0 ; index < deleteElement.size(); index ++) {
									
									if (deleteElement.get(index).getStartState().getStateName().equals(startState)) {
										flag = 'M';
										trans = deleteElement.get(index);
										deleteElement.remove(index);
										break;
									}
								}
							}
							
							trans.setStartState(state);
							trans.setNewTransitionServices(_transitionsServices);
							trans.setFlag(flag);
							_tableModel.addElement(trans);
							
							_transitionsServices = null;
							_transitionsTable.getSelectionModel().clearSelection();
							
							if (_startStateComboBox.getItemCount() > 0)
								_startStateComboBox.setSelectedIndex(0);
							_startStateComboBox.setEnabled(true);
							
							_validateButton.setEnabled(true);
							_applyButton.setEnabled(false);
							_removeButton.setEnabled(false);
						}
						else {
							System.out.println("Impossible de retrouver l'état de départ");
						}
					}
					else {
						System.out.println("Tentative d'ajout d'une transition existante !");
					}
				}
				else {
					// Modification d'un élément existant
					PortalApplicationTransitions trans = 
							_tableModel.getElementAt(selectedRow);
					trans.setNewTransitionServices(_transitionsServices);
					
					if (trans.getFlag() == 'E') {
						trans.setFlag('M');
					}
					_tableModel.fireTableRowsUpdated(selectedRow, selectedRow);
					
					_transitionsServices = null;
					_transitionsTable.getSelectionModel().clearSelection();
					
					if (_startStateComboBox.getItemCount() > 0)
						_startStateComboBox.setSelectedIndex(0);
					_startStateComboBox.setEnabled(true);
					
					_applyButton.setEnabled(false);
					_removeButton.setEnabled(false);					
				}
			}
		}
		
	}
	
	private void onClickRemoveButton() {
		
		int row = _transitionsTable.getSelectedRow();
		
		if ( row != -1 ) {
			PortalApplicationTransitions trans = _tableModel.getElementAt(row);
			if (trans != null ) {
				if ( trans.getFlag() == 'A' ) 
					_tableModel.removeElement(row);
				else if ( trans.getFlag() == 'E' || trans.getFlag() == 'M' ) {
					_tableModel.removeElement(row);
					trans.setFlag('S');
					_tableModel.addDeleteElement(trans);
				}
				else {
					System.out.println("Supression impossible, Flag inatendu : " + trans.getFlag());
				}
			}
			_tableModel.fireTableRowsDeleted(row, row);
		}
		_transitionsServices = null;
		_transitionsTable.getSelectionModel().clearSelection();
		
		if (_startStateComboBox.getItemCount() > 0)
			_startStateComboBox.setSelectedIndex(0);
		_startStateComboBox.setEnabled(true);
		
		if (_tableModel.getRowCount() == 0)
			_validateButton.setEnabled(false);
		
		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
		
	}
	
	private void onClickNewButton() {
		
		_transitionsServices = null;
		_transitionsTable.getSelectionModel().clearSelection();
		
		if (_startStateComboBox.getItemCount() > 0)
			_startStateComboBox.setSelectedIndex(0);
		_startStateComboBox.setEnabled(true);
		
		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
	}
	
	private void onClickSubDefButton() {
		
		if (_subPanel != null) {
			_subPanel.loadTransitionsServicesArray(_transitionsServices);
		
			_window.displayPanel(_subPanel);
			_window.enabledNextAndPreviousButton(false);
		}		
	}
	
	private void onClickCancelButton() {
		
		_window.displayPanel(_parentPanel);
		_window.enabledNextAndPreviousButton(true);
		
	}
	
	private void onClickValidateButton() {
		
		ArrayList<PortalApplicationTransitions> res = _tableModel.getDeleteDatas();
		res.addAll(_tableModel.getDatas());
		
		_parentPanel.saveTransitions(res);
		_window.displayPanel(_parentPanel);
		_window.enabledNextAndPreviousButton(true);
		
	}
	private void fieldsHaveChanged() {
		
		if (_transitionsServices == null) {
			_applyButton.setEnabled(false);
		}
		else {
			_applyButton.setEnabled(true);
		}
		
		if (_tableModel.getRowCount() > 0)
			_validateButton.setEnabled(true);
		else
			_validateButton.setEnabled(false);
		
	}
	
}
