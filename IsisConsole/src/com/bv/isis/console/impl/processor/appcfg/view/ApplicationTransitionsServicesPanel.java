package com.bv.isis.console.impl.processor.appcfg.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationGroupTransitions;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationServices;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationServicesSpecificStates;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationServicesStates;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationStates;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationTransitionServices;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationTransitions;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;

public class ApplicationTransitionsServicesPanel extends AbstractPanel {

	public ApplicationTransitionsServicesPanel(WindowInterface window,
			ApplicationTransitionsPanel parentPanel) {
		
		_window = window;
		_parentPanel= parentPanel;
		
		makePanel();
	}
	
	public void beforeDisplay(ArrayList<ModelInterface> modele) {
		
	}

	public void loadTransitionsServicesArray(
			ArrayList<PortalApplicationTransitionServices> array) {
		
		_tableModel = new TransServTableModel();
		
		ArrayList<ModelInterface> model = _window.getModel();
		if (model != null) {
			
			_agentServBox.removeAllItems();
			_agentServBox.addItem("");
			for ( int index = 0 ; index < model.size() ; index++) {
				if (model.get(index) instanceof PortalApplicationServices) {
					PortalApplicationServices service = (PortalApplicationServices) model.get(index);
					_agentServBox.addItem(service.getAgentName() + "/" + service.getServiceName());
				}
			}
		}
		
		if (array != null) {
			for ( int index = 0 ; index < array.size() ; index++) {
				PortalApplicationTransitionServices trans = array.get(index);
					if (trans.getFlag() != 'S')
						_tableModel.addElement(trans.clone());
					else
						_tableModel.addDeleteElement(trans.clone());
			}
		}
		_transServTable.setModel( _tableModel );
		
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
	
	
	private ApplicationTransitionsPanel _parentPanel;
	
	private JTable _transServTable;
	
	private JComboBox _agentServBox;
	
	private JComboBox _endStateBox;
	
	private JCheckBox _critBox;
	
	private JFormattedTextField _orderTextField;
	
	private JButton _newButton;
	
	private JButton _removeButton;
	
	private JButton _applyButton;
	
	private JButton _cancelButton;
	
	private JButton _validateButton;
	
	
	
	
	
	private class TransServTableModel extends DefaultTableModel {
		
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
		private ArrayList<PortalApplicationTransitionServices> _datas;
		
		private ArrayList<PortalApplicationTransitionServices> _deleteDatas;
		
		/*------------------------------------------------------------
		 * Nom : StatesTableModel
		 * 
		 * Description :
		 * Cette méthode est le contructeur de la classe.
		 * Son rôle est de créer les deux tableaux de titres et de données.
		 * Pour le tableau des titres, elle le remplira à partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/		
		public TransServTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"TransServTableModel", "TransServTableModel");
			trace_methods.beginningOfMethod();
			
			// Création du tableau des titres
			_titles = new String [5];
			// La première colonne représentera le nom du fichier
			_titles[0] = MessageManager.getMessage("&AppCfg_Agent");
			_titles[1] = MessageManager.getMessage("&AppCfg_Service");
			_titles[2] = MessageManager.getMessage("&AppCfg_EndState");
			_titles[3] = MessageManager.getMessage("&AppCfg_Critic");
			_titles[4] = MessageManager.getMessage("&AppCfg_Order");
			
			// Création du tableau de données
			_datas = new ArrayList<PortalApplicationTransitionServices>();
			_deleteDatas = new ArrayList<PortalApplicationTransitionServices>();
			
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
		public ArrayList<PortalApplicationTransitionServices> getDatas() {
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
		public int findRow(String agentName, String servName, String stateName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("agentName=" + agentName);
			trace_arguments.writeTrace("servName=" + servName);
			trace_arguments.writeTrace("stateName=" + stateName);

			if (_datas != null)
			{
				for (int index=0 ; index < _datas.size() ; index++) {
					PortalApplicationTransitionServices transServ = _datas.get(index);
					if (transServ != null && transServ.getComposition() != null) {
						
						PortalApplicationServicesSpecificStates speStates = transServ.getComposition();
						
						if (speStates.getService().getAgentName().equals(agentName)
								&& speStates.getService().getServiceName().equals(servName)
								&& speStates.getServiceState().getStateServiceName().equals(stateName)){
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
				PortalApplicationTransitionServices serv = _datas.get(line);
				if (serv != null) {
					if (column == 0) {
						trace_methods.endOfMethod();
						return serv.getComposition().getService().getAgentName();
					}
					else if (column == 1) {
						trace_methods.endOfMethod();
						return serv.getComposition().getService().getServiceName();
					}
					else if (column == 2) {
						trace_methods.endOfMethod();
						return serv.getComposition().getServiceState().getStateServiceName();
					}
					else if (column == 3) {
						if (serv.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return serv.getCritical();
						}
						else {
							trace_methods.endOfMethod();
							return serv.getNewCritical();
						}
					}
					else if (column == 4) {
						if (serv.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return serv.getOrder();
						}
						else {
							trace_methods.endOfMethod();
							return serv.getNewOrder();
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
		public void addElement(PortalApplicationTransitionServices element){
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
		public PortalApplicationTransitionServices getElementAt(int line) {
			
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
		public void addDeleteElement(PortalApplicationTransitionServices service) {
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
		public PortalApplicationTransitionServices getDeleteElementAt(int index) {
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
		public ArrayList<PortalApplicationTransitionServices> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "getDeleteDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			
			return _deleteDatas;
		}
	}
	
	private TransServTableModel _tableModel;
	
	
	private void makePanel() {
		
		// On présente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(475, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(475, 20));
		firstDescriptionSentence.setText(
				MessageManager.getMessage("&AppCfg_FirstDescriptionSentenceTransitionsServices"));
				
		_tableModel = new TransServTableModel();
		_transServTable = new JTable(_tableModel);
		_transServTable
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_transServTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});

		// On intégre un défilement dans le tableau des transitions spécifiques
		JScrollPane scrollPaneSpecificTransition = new JScrollPane();
		scrollPaneSpecificTransition.setViewportView(_transServTable);
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
		JLabel agentServLabel = new JLabel();
		agentServLabel.setText(MessageManager
				.getMessage("&AppCfg_AgentService")
				+ " :   *");

		// on définit un label pour le champs "Commande"
		JLabel endStateLabel = new JLabel();
		endStateLabel.setText(MessageManager
				.getMessage("&AppCfg_EndState")
				+ " :   *");

		// on définit un label pour le champs "Type de commande"
		JLabel critLabel = new JLabel();
		critLabel.setText(MessageManager
				.getMessage("&AppCfg_Critic")
				+ " : ");

		// on définit un label pour le champs "Intervalle de contrôle"
		JLabel orderLabel = new JLabel();
		orderLabel.setText(MessageManager
				.getMessage("&AppCfg_Order")
				+ " :   *");

		// On définit un label pour indiquer que l'asterisque en fin des noms de
		// champs sont obligatoires pour la saisie.
		JLabel obligatoryFieldLabel = new JLabel();
		obligatoryFieldLabel.setMinimumSize(new Dimension(150, 20));
		obligatoryFieldLabel.setMaximumSize(new Dimension(150, 20));
		obligatoryFieldLabel.setText(MessageManager
				.getMessage("&AppCfg_ObligatoryFields"));

		int maxSize = agentServLabel.getPreferredSize().width;
		if (endStateLabel.getPreferredSize().width > maxSize)
			maxSize = endStateLabel.getPreferredSize().width;
		if (critLabel.getPreferredSize().width > maxSize)
			maxSize = critLabel.getPreferredSize().width;
		if (orderLabel.getPreferredSize().width > maxSize)
			maxSize = orderLabel.getPreferredSize().width;

		agentServLabel.setPreferredSize(new Dimension(maxSize, 20));
		agentServLabel.setMinimumSize(new Dimension(maxSize, 20));
		agentServLabel.setMaximumSize(new Dimension(maxSize, 20));

		endStateLabel.setPreferredSize(new Dimension(maxSize, 20));
		endStateLabel.setMinimumSize(new Dimension(maxSize, 20));
		endStateLabel.setMaximumSize(new Dimension(maxSize, 20));

		critLabel.setPreferredSize(new Dimension(maxSize, 20));
		critLabel.setMinimumSize(new Dimension(maxSize, 20));
		critLabel.setMaximumSize(new Dimension(maxSize, 20));

		orderLabel.setPreferredSize(new Dimension(maxSize, 20));
		orderLabel.setMinimumSize(new Dimension(maxSize, 20));
		orderLabel.setMaximumSize(new Dimension(maxSize, 20));

		_agentServBox = new JComboBox();
		setEnterCallback(_agentServBox);
		_agentServBox.setPreferredSize(new Dimension(455 - (agentServLabel
				.getPreferredSize().width), 20));
		_agentServBox.setMinimumSize(new Dimension(455 - (agentServLabel
				.getMinimumSize().width), 20));
		_agentServBox.setMaximumSize(new Dimension(455 - (agentServLabel
				.getMaximumSize().width), 20));
		_agentServBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onSelectionAgent();
			}
		});
		
		_endStateBox = new JComboBox();
		setEnterCallback(_endStateBox);
		_endStateBox.setEnabled(false);
		_endStateBox.setPreferredSize(new Dimension(455 - (endStateLabel
				.getPreferredSize().width), 20));
		_endStateBox.setMinimumSize(new Dimension(455 - (endStateLabel
				.getMinimumSize().width), 20));
		_endStateBox.setMaximumSize(new Dimension(455 - (endStateLabel
				.getMaximumSize().width), 20));

		_critBox = new JCheckBox();
		setEnterCallback(_critBox);
		_critBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fieldsHaveChanged();
			}
		});
		
		_orderTextField = new JFormattedTextField(NumberFormat.getInstance());
		setEnterCallback(_orderTextField);
		_orderTextField.setPreferredSize(new Dimension(455 - (orderLabel
				.getPreferredSize().width), 20));
		_orderTextField.setMinimumSize(new Dimension(455 - (orderLabel
				.getMinimumSize().width), 20));
		_orderTextField.setMaximumSize(new Dimension(455 - (orderLabel
				.getMaximumSize().width), 20));

		_newButton = new JButton();
		_newButton.setText(MessageManager
				.getMessage("&AppCfg_NewButton"));
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
				.getMessage("&AppCfg_RemoveButton"));
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
				.getMessage("&AppCfg_ApplyButton"));
		_applyButton.setMinimumSize(new Dimension(120, 21));
		_applyButton.setMaximumSize(new Dimension(120, 21));
		_applyButton.setPreferredSize(new Dimension(120, 21));
		_applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickApplyButton();
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
		panel2.setMinimumSize(new Dimension(480, 236));
		panel2.setMaximumSize(new Dimension(480, 236));
		panel2.setPreferredSize(new Dimension(480, 236));
		panel2.setBorder(BorderFactory.createTitledBorder((
				MessageManager.getMessage("&AppCfg_SecondDescriptionSentence"))));
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
		panel2.add(agentServLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel2.add(_agentServBox, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(endStateLabel, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(_endStateBox, c);

		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(critLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel2.add(_critBox, c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.gridheight = 1;
		panel2.add(orderLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		c.gridheight = 1;
		panel2.add(_orderTextField, c);

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
	}
	
	private void onSelectionAgent() {
		
		if (_agentServBox.getItemCount() > 0) {
			String selectedName = _agentServBox.getSelectedItem().toString();
			if (!selectedName.equals(""))
			{
				// selected[0] = agentName;
				// selected[1] = serviceName
				String [] selected = selectedName.split("/");				
				if (selected.length == 2) {
				
					_endStateBox.removeAllItems();
					ArrayList<ModelInterface> model = _window.getModel();
					if (model != null) {
						
						for (int index = 0 ; index < model.size(); index++) {
							if (model.get(index) instanceof PortalApplicationServices) {
								PortalApplicationServices serv = (PortalApplicationServices) model.get(index);
								
								if (serv.getAgentName().equals(selected[0])
										&& serv.getServiceName().equals(selected[1])) {
									
									ArrayList<PortalApplicationServicesStates> array = serv.getServiceState();
									if (array != null) {
										for (int i = 0 ; i < array.size() ; i++) {
											_endStateBox.addItem(array.get(i).getStateServiceName());
										}
									}
									else {
										System.out.println("Impossible de charger les états du " +
												"service sélectionner, tableau vide");
									}
								}
							}
						}
					}
					_endStateBox.setEnabled(true);
				}
				else {
					System.out.println("Erreur dans le format Agent/Service");
				}
			}
			else {
				_endStateBox.setEnabled(false);
				_endStateBox.removeAllItems();
			}
		}
	}
	
	private void onSelectionElement(ListSelectionEvent event) {
		
		ListSelectionModel lsm = (ListSelectionModel) event.getSource();

		int index = lsm.getMinSelectionIndex();
		if (index == -1) {
			onClickNewButton();
		} 
		else {
			PortalApplicationTransitionServices serv = _tableModel.getElementAt(index);
			
			_agentServBox.setSelectedItem(
					serv.getComposition().getService().getAgentName() 
					+ "/" 
					+ serv.getComposition().getService().getServiceName());
			_agentServBox.setEnabled(false);
			
			_endStateBox.setSelectedItem(serv.getComposition().getServiceState().getStateServiceName());
			_endStateBox.setEnabled(false);
			
			if (serv.getFlag() == 'E') {
				_critBox.setSelected(serv.getCritical());
				_orderTextField.setValue(serv.getOrder());
			}
			else {
				_critBox.setSelected(serv.getNewCritical());
				_orderTextField.setValue(serv.getNewOrder());
			}
			_applyButton.setEnabled(false);
			_removeButton.setEnabled(true);
		}
		
		
	}
	
	private void onClickApplyButton() {
		
		String agentServ = _agentServBox.getSelectedItem().toString();
		String endState = _endStateBox.getSelectedItem().toString();
		boolean crit = _critBox.isSelected();
		int order = 0;
		if (!_orderTextField.getText().equals(""))
			order = Integer.parseInt(_orderTextField.getText());
		else 
			System.out.println("order, champ obligatoire");
		
		if (agentServ.equals(""))
			System.out.println("Agent/Service, champ obligatoire");
		else {
			
			String [] selAgentServ = agentServ.split("/"); 
			if (selAgentServ.length == 2) {
			
				if (endState.equals("")) {
					System.out.println("Etat d'arrivée, champ obligatoire");
				} 
				else {
					/*
					 * 1 : Vérifier qu'il n'y ai pas de transitions avec le même état
					 * d'arrivée 2 : Mettre à jour le tableau 3 : Effacer les champs 4 :
					 * actualiser le tableau
					 */
					int row = _tableModel.findRow(selAgentServ[0], selAgentServ[1], endState);

					// Aucune ligne du tableau n'a été sélectionner
					if (row == -1) {

						// Ce nouveau groupe peut porter sur le même état qu'un ancien groupe
						// précédemment supprimé mais toujours existant en base. Il s'agit
						// donc de mettre à jour cette donnée et non de la créer.
						// On recherche donc si dans les éléments précedemment supprimés, il 
						// n'existe pas un groupe avec le même état d'arrivée
						char flag = 'A';
						PortalApplicationTransitionServices transServ = new PortalApplicationTransitionServices();

						ArrayList<PortalApplicationTransitionServices> deleteElement = _tableModel.getDeleteDatas();
						
						if (deleteElement != null) {
							for (int index = 0; index < deleteElement.size(); index++) {
								// Si ce groupe existe, on le récupère, et on va le 
								// mettre à jour
								PortalApplicationServicesSpecificStates compo = 
										deleteElement.get(index).getComposition();
								if (compo.getService().getAgentName().equals(selAgentServ[0])
										&& compo.getService().getServiceName().equals(selAgentServ[1])
										&& compo.getServiceState().getStateServiceName().equals(endState)) {
									flag = 'M';
									transServ = deleteElement.get(index);
									deleteElement.remove(index);
									break;
								}
							}
						}
						
						if (flag == 'A') {
							
							PortalApplicationServices serv = null;
							PortalApplicationServicesStates servState = null;
							
							// On parcours le model pour retrouver le service selectionner
							// Ainsi que son etat selectionne
							ArrayList<ModelInterface> model = _window.getModel();
							for( int index = 0 ; index < model.size() ; index ++) {
								
								// Test sur la classe des services
								if (model.get(index) instanceof PortalApplicationServices) {
									serv = (PortalApplicationServices) model.get(index);
									
									// Si c'est le bon service sur le bon agent
									if (serv.getAgentName().equals(selAgentServ[0])
											&& serv.getServiceName().equals(selAgentServ[1])) {
										
										// On recherche l'etat du service selectionne
										ArrayList<PortalApplicationServicesStates> states = serv.getServiceState();
										for (int indexState = 0 ; indexState < states.size() ; indexState++) {
											
											if (states.get(indexState).getStateServiceName().equals(endState)) {
												servState = states.get(indexState);
												break;
											}
										}
										if (servState != null) {
											break;
										}
									}
									else {
										serv = null;
									}
								}
							}
							
							if (serv != null && servState != null) {
								PortalApplicationServicesSpecificStates compo = 
									new PortalApplicationServicesSpecificStates();
								compo.setService(serv);
								compo.setServiceState(servState);
								
								transServ.setComposition(compo);
							}
							else {
								System.out.println("Impossible de retrouver l'agent, le service et l'état selectionné");
							}
						}
						
						transServ.setNewCritical(crit);
						transServ.setNewOrder(order);
						transServ.setFlag(flag);

						_tableModel.addElement(transServ);

						_agentServBox.setSelectedIndex(0);
						_agentServBox.setEnabled(true);
						_endStateBox.setEnabled(false);
						_endStateBox.removeAllItems();
						_critBox.setSelected(false);
						_orderTextField.setText("");			
						
						_transServTable.getSelectionModel().clearSelection();
						
						_validateButton.setEnabled(true);
						
						_applyButton.setEnabled(false);
						_removeButton.setEnabled(false);
					}
					else {
						// La transition existe donc on la met à jour
						int selectedRow = _transServTable.getSelectedRow();

						if (selectedRow == -1) {
							DialogManager.displayDialog("Information",MessageManager
									.getMessage("&AppCfg_MessageInformationTransitionsServices2"),
									null, null);
						} 
						else {
							PortalApplicationTransitionServices transServ = _tableModel.getElementAt(row);

							transServ.setNewCritical(crit);
							transServ.setNewOrder(order);

							if (transServ.getFlag() == 'E')
								transServ.setFlag('M');

							// On recharge le tableau et on le réaffiche
							_tableModel.fireTableRowsUpdated(row, row);
							
							// On efface les champs de saisie
							_agentServBox.setSelectedIndex(0);
							_agentServBox.setEnabled(true);
							_endStateBox.setEnabled(false);
							_endStateBox.removeAllItems();
							_critBox.setSelected(false);
							_orderTextField.setText("");			
							
							_transServTable.getSelectionModel().clearSelection();
							
							_applyButton.setEnabled(false);
							_removeButton.setEnabled(false);
						}
					}
				}
			}
			else {
				System.out.println("Erreur lors de la récupération du nom de l'agent et du service");
			}
		}
	}
	
	private void onClickRemoveButton() {
		
		int row = _transServTable.getSelectedRow();
		
		if ( row != -1 ) {
			PortalApplicationTransitionServices trans = _tableModel.getElementAt(row);
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

		_transServTable.getSelectionModel().clearSelection();
		
		if (_agentServBox.getItemCount() > 0)
			_agentServBox.setSelectedIndex(0);
		_agentServBox.setEnabled(true);
		
		_endStateBox.removeAllItems();
		_endStateBox.setEnabled(false);
		
		_critBox.setSelected(false);
		_orderTextField.setText("");
		
		_transServTable.getSelectionModel().clearSelection();
		
		if (_tableModel.getRowCount() == 0)
			_validateButton.setEnabled(false);
		
		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
		
	}
	
	private void onClickNewButton() {
		_agentServBox.setSelectedIndex(0);
		_agentServBox.setEnabled(true);
		_endStateBox.setEnabled(false);
		_endStateBox.removeAllItems();
		_critBox.setSelected(false);
		_orderTextField.setText("");			
		
		_transServTable.getSelectionModel().clearSelection();
		
		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
	}
	
	private void onClickCancelButton() {
		
		_window.displayPanel(_parentPanel);
		/*
		for (int i = 0; i < _tableModel.getDeleteDatas().size(); i++) {
			ComponentTransition c = _tableModel.getDeleteElementAt(i);
			c.setFlag(c.getPreviousFlag());
		}
		*/
		//_tableModel.removeAll();
		
	}
	
	private void onClickValidateButton() {
		
		ArrayList<PortalApplicationTransitionServices> res = _tableModel.getDeleteDatas();
		res.addAll(_tableModel.getDatas());
		
		_parentPanel.saveTransitionsServices(res);
		_window.displayPanel(_parentPanel);
		
	}
	
	private void fieldsHaveChanged() {
		
		if (_agentServBox.getSelectedIndex() != 0 && !_orderTextField.getText().equals("")) {
			_applyButton.setEnabled(true);
		}
		else {
			_applyButton.setEnabled(false);
		}
		
		if (_tableModel.getRowCount() > 0)
			_validateButton.setEnabled(true);
		else
			_validateButton.setEnabled(false);		
	}
}
