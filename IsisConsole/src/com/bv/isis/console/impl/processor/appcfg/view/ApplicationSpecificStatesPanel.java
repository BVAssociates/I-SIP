package com.bv.isis.console.impl.processor.appcfg.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationServices;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationServicesSpecificStates;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationServicesStates;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;

public class ApplicationSpecificStatesPanel extends AbstractPanel {

	public ApplicationSpecificStatesPanel(WindowInterface window,
			ApplicationStatesPanel parentPanel) {
		_window = window;
		_parentPanel = parentPanel;
		
		makePanel();
	}
	
	public void beforeDisplay(ArrayList<ModelInterface> modele) {

	}
	
	public boolean loadStatesArray(ArrayList<ModelInterface> model,
			ArrayList<PortalApplicationServicesSpecificStates> array) {
		
			_tableModel = new SpecificStatesTableModel();
			
			if (model != null) {
			
				for (int index = 0 ; index < model.size() ; index ++) {
					if (model.get(index) instanceof PortalApplicationServices) {
						PortalApplicationServices serv = (PortalApplicationServices) model.get(index); 
					
						if (serv.getFlag() != 'S') {
							_tableModel.addElement(serv);
							
							if (array != null) {
								for (int i = 0 ; i < array.size() ; i++) {
									if (array.get(i).getService().getAgentName().equals(serv.getAgentName())
											&& array.get(i).getService().getServiceName().equals(serv.getServiceName())) {
										_tableModel.setValueAt(array.get(i).getServiceState().getStateServiceName(), _tableModel.getRowCount()-1, 2);
										break;
									}
								}
							}
						}	
					}
				}
				
				
			}
		_specificStatesTable.setModel(_tableModel);
			
		_specificStatesTable.getColumnModel().getColumn(2).setCellEditor(new ComboTableCellEditor());
		return true;
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

	
	
	private ApplicationStatesPanel _parentPanel;
	
	private JTable _specificStatesTable; 
	
	private JButton _applyButton;
	
	private JButton _cancelButton;
	
	
	private class SpecificStatesTableModel extends AbstractTableModel {
		
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
		 * Cet attribut est un tableau de PortalApplicationServices.
		 * Chaque élément du tableau représente une ligne de la JTable
		 * et contient les informations d'un fichier.
		 * ------------------------------------------------------------*/
		private ArrayList<PortalApplicationServices> _datas;
		
		private ArrayList<Integer> _selectedStates;
		
		/*------------------------------------------------------------
		 * Nom : StatesTableModel
		 * 
		 * Description :
		 * Cette méthode est le contructeur de la classe.
		 * Son rôle est de créer les deux tableaux de titres et de données.
		 * Pour le tableau des titres, elle le remplira à partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/		
		public SpecificStatesTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ServicesTableModel", "ConfigTableModel");
			trace_methods.beginningOfMethod();
			
			// Création du tableau des titres
			_titles = new String [3];
			// La première colonne représentera le nom du fichier
			_titles[0] = MessageManager.getMessage("&AppCfg_Agent");
			// La deuxième colonne représentera la description associée
			// au fichier
			_titles[1] = MessageManager.getMessage("&AppCfg_Service");
			_titles[2] = MessageManager.getMessage("&AppCfg_State");
			
			// Création du tableau de données
			_datas = new ArrayList<PortalApplicationServices>();
			_selectedStates = new ArrayList<Integer>();
			
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
 		 * Cette méthode retourne le tableau de PortalApplicationServices contenant
 		 * l'ensemble des informations saisies par l'utilisateur.
 		 * 
 		 * Retourne : Un tableau de PortalApplicationServices
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationServices> getDatas() {
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
		public int findRow(String agentName, String servName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("agentName=" + agentName);
			trace_arguments.writeTrace("servName=" + servName);

			if (_datas != null)
			{
				for (int index=0 ; index < _datas.size() ; index++) {
					PortalApplicationServices serv = _datas.get(index);
					if (serv != null && serv.getAgentName() != null 
							&& serv.getServiceName() != null) {
						
						String agent = serv.getAgentName();
						String service = serv.getServiceName();
						
						if (agent.equals(agentName) && service.equals(servName)){
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
				PortalApplicationServices serv = _datas.get(line);
				if (serv != null) {
					if (column == 0) {
						trace_methods.endOfMethod();
						return serv.getAgentName();
					}
					else if (column == 1) {
						trace_methods.endOfMethod();
						return serv.getServiceName();
					}
					else if (column == 2){
						trace_methods.endOfMethod();
						
						int state = _selectedStates.get(line);
						if (state == 0)
							return "Pas pris en compte";
						else 
							return serv.getServiceState().get(state - 1).getStateServiceName();
						
						
						//return serv.getNewDescription();
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
		public void addElement(PortalApplicationServices element){
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "addElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("element=" + element);

			if (_datas != null) {
				_datas.add(element);
				_selectedStates.add(0);
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
				_selectedStates.remove(line);
				super.fireTableRowsDeleted(line, line);
			}
			
			trace_methods.endOfMethod();
		}
		
		/*------------------------------------------------------------
		 * Nom : getElementAt
		 * 
		 * Description : 
 		 * Cette méthode retourne le PortalApplicationServices de la ligne demandée. 
 		 * 
 		 * Argument : 
 		 *  - line : Le numéro de ligne de l'élément à récupérer
 		 *  
 		 * Retourne : Le PortalApplicationServices de la ligne sélectionnée.
		 * ------------------------------------------------------------*/
		public PortalApplicationServices getElementAt(int line) {
			
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
		
		
		public boolean isCellEditable(int row, int col) {
			if (col == 2)
				return true;
			return false;
		}
		
		
		public void setValueAt(Object value, int row, int col) {
			if (col == 2) {
				if (value.toString().equals(MessageManager.
						getMessage("&AppCfg_NotTakenIntoAccount"))) {
					_selectedStates.set(row, 0);
				} 
				else {
					ArrayList<PortalApplicationServicesStates> array = _tableModel.getElementAt(row).getServiceState();
					for (int index = 0 ; index < array.size() ; index ++) {
						PortalApplicationServicesStates s = array.get(index);
						if (s.getStateServiceName().equals(value.toString())) {
							_selectedStates.set(row, index + 1);
						}
					}
				}
			}
			else {
				super.setValueAt(value, row, col);
			}
		}
	}
	
	
	
	//private DefaultTableModel _tableModel;
	private SpecificStatesTableModel _tableModel;
	
	
	
	/* Nom : ComboTableCellEditor
	 * 
	 * Description :
	 * Cette classe est utilisé pour modifier le rendu 
	 * graphique d'une cellule de la JTable
	 * En effet, la cellule présentant les états des services 
	 * doit présenter une liste déroulante pour sélectionner 
	 * l'état d'un service; pour celà, elle redéfinit la classe
	 * DefaultCellEditor
	 * 
	 */
	private class ComboTableCellEditor extends DefaultCellEditor {
		
		public ComboTableCellEditor() {
			// On crée la cellule avec une JComboBox à l'intérieur
			super(new JComboBox());
		}
	 
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			Component editor = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			JComboBox combo = (JComboBox) getComponent();
			
			PortalApplicationServices serv = _tableModel.getElementAt(row);
			
			combo.removeAllItems();
			combo.addItem(MessageManager.getMessage("&AppCfg_NotTakenIntoAccount"));
			
			ArrayList<PortalApplicationServicesStates> array = serv.getServiceState();
			for (int index = 0 ; index < array.size() ; index++)
				combo.addItem(array.get(index).getStateServiceName());
			
			if (!value.equals(""))
				combo.setSelectedItem(value);
	
			return editor;
		}
	}
	
	
	
	private void makePanel() {
		
		// On présente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(476, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(476, 20));
		firstDescriptionSentence.setText(
				MessageManager.getMessage("&AppCfg_FirstDescriptionSentenceServicesStates"));
		
		//_tableModel = new DefaultTableModel();
		_tableModel = new SpecificStatesTableModel();
		
		_specificStatesTable = new JTable(_tableModel);
		_specificStatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// On intégre un défilement dans le tableau
		JScrollPane scrollPaneConfigTable = new JScrollPane();
		scrollPaneConfigTable.setViewportView(_specificStatesTable);
		scrollPaneConfigTable.setMinimumSize(new Dimension(476, 300));
		scrollPaneConfigTable.setMaximumSize(new Dimension(476, 300));
			
		_applyButton = new JButton();
		_applyButton.setMinimumSize(new Dimension(120, 21));
		_applyButton.setMaximumSize(new Dimension(120, 21));
		_applyButton.setPreferredSize(new Dimension(120, 21));
		_applyButton.setText(MessageManager.
				getMessage("&AppCfg_ApplyButton"));
		_applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickApplyButton();
			}
		});

		_cancelButton = new JButton();
		_cancelButton.setMinimumSize(new Dimension(120, 21));
		_cancelButton.setMaximumSize(new Dimension(120, 21));
		_cancelButton.setPreferredSize(new Dimension(120, 21));
		_cancelButton.setText(MessageManager.
				getMessage("&ComponentConfiguration_CancelButton"));
		_cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickCancelButton();
			}

		});

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
	    panel1.add(scrollPaneConfigTable, c1);
	    
	    Box hBox1 = Box.createHorizontalBox();
	    hBox1.add(_cancelButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_applyButton);
		hBox1.setMaximumSize(new Dimension(380,22));
		
	    c1.gridx = 0;
	    c1.gridy = 2;
	    c1.gridwidth = 1;
	    c1.gridheight = 1;
	    c1.ipady = 15;
	    c1.anchor = GridBagConstraints.LINE_END;
	    panel1.add(hBox1, c1);

	    setLayout(new BorderLayout());
		add(panel1, BorderLayout.CENTER);
		
	}
	
	private void onClickApplyButton() {
		
		ArrayList<PortalApplicationServicesSpecificStates> servSpeStates = 
			new ArrayList<PortalApplicationServicesSpecificStates>();
		ArrayList<ModelInterface> model = _window.getModel();
		
		// On récupère chacune des lignes de la table
		for (int index = 0 ; index < _tableModel.getRowCount() ; index ++) {
			String agentSelected = _tableModel.getValueAt(index, 0).toString();
			String serviceSelected = _tableModel.getValueAt(index, 1).toString();
			String stateSelected = _tableModel.getValueAt(index, 2).toString();			
			
			if (!stateSelected.equals(MessageManager.
					getMessage("&AppCfg_NotTakenIntoAccount"))) {
				
				// Si la ligne est à prendre en compte
				// On va rechercher dans le modèle le service correspondant à la ligne
				for (int i = 0 ; i<model.size() ; i++) {
					if (model.get(i) instanceof PortalApplicationServices) {
						PortalApplicationServices serv = (PortalApplicationServices) model.get(i);
						if (serv.getAgentName().equals(agentSelected) &&
							serv.getServiceName().equals(serviceSelected)){
							
							// Pour ce service, on va rechercher l'état qui a été sélectionné
							for (PortalApplicationServicesStates state : serv.getServiceState()) {
								if (state.getStateServiceName().equals(stateSelected)) {
									
									// Une fois l'état trouvé, on crée un état spécifique 
									// qui référencera le service et son état sélectionné
									PortalApplicationServicesSpecificStates speStates =
										new PortalApplicationServicesSpecificStates();
									
									speStates.setService(serv);
									speStates.setServiceState(state);
									
									servSpeStates.add(speStates);
									break;
								}
							}
						}
					}
				}
			}
		}
		
		if (servSpeStates.size() == 0) {
			System.out.println("Attention, il faut remplir au moins 1 ligne");
		}
		else {
			_parentPanel.saveSpecificStates(servSpeStates);
			_window.displayPanel(_parentPanel);
			_window.enabledNextAndPreviousButton(true);
		}
	}
	
	private void onClickCancelButton() {
		_window.displayPanel(_parentPanel);
		_window.enabledNextAndPreviousButton(true);
	}
}
