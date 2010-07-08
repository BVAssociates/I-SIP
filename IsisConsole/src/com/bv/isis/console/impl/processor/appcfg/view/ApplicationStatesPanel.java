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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationGroupTransitions;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationServicesSpecificStates;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationStates;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationTransitions;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentTransition;

public class ApplicationStatesPanel extends AbstractPanel {

	public ApplicationStatesPanel(WindowInterface window) {
		_window = window;
		makePanel();
		
		_serviceStatesTable = null;
		
		_subPanel =  new ApplicationSpecificStatesPanel(window, this);
	}
	
	public boolean afterDisplay() {
		return true;
	}

	public boolean afterHide() {
		return true;
	}

	public void beforeDisplay(ArrayList<ModelInterface> modele) {

		_tableModel = new StatesTableModel();
		if (modele != null) {
			for ( int i=0 ; i < modele.size() ; i++) {
				if ( modele.get(i) instanceof PortalApplicationStates ) {
					PortalApplicationStates serv = (PortalApplicationStates) modele.get(i);
					if ( !serv.getStateName().equals(
							MessageManager.getMessage("&AppCfg_AnyState"))) {
						if (serv.getFlag() != 'S')
							_tableModel.addElement(serv);
						else
							_tableModel.addDeleteElement(serv);
					}
				}
			}
		}
		_stateTable.setModel( _tableModel );
		
	}

	public boolean beforeHide() {
		return true;
	}

	public boolean end() {
		return true;
	}

	public ArrayList<ModelInterface> update(ArrayList<ModelInterface> tabModels) {
		
		ArrayList<PortalApplicationStates> newElements = _tableModel.getDatas();
		ArrayList<PortalApplicationStates> deleteElements = _tableModel.getDeleteDatas();

		if (tabModels != null) {
			// On supprime tout les groupes de transitions
			for (int index = 0; index < tabModels.size(); index++) {
				if (tabModels.get(index) instanceof PortalApplicationStates) {
					PortalApplicationStates state = (PortalApplicationStates) tabModels.get(index); 
					if ( !state.getStateName().equals(
							MessageManager.getMessage("&AppCfg_AnyState"))) {
						tabModels.remove(index);
						// On recule d'une case dans le tableau pour ne pas oublier
						// l'�l�ment d�cal�
						index--;
					}
				}
				else if (tabModels.get(index) instanceof PortalApplicationGroupTransitions) {
					PortalApplicationGroupTransitions group = (PortalApplicationGroupTransitions) 
							tabModels.get(index);
					if (group.getFlag() != 'S') {
						// Pour un groupe de transitions, si son �tat d'arriv�e
						// est un �tat supprim�
						// on supprime ce groupe de transition
						for (int indexDeleteElem = 0; indexDeleteElem < deleteElements
								.size(); indexDeleteElem++) {
							if (deleteElements.get(indexDeleteElem).getStateName()
									.equals(group.getEndState().getStateName())) {
								if (group.getFlag() == 'A') {
									tabModels.remove(index);
									index--;
								} else {
									group.setFlag('S');
								}
							}
							// Si l'�tat d'arriv� du groupe ne correspond pas,
							// on v�rifie
							// si l'une de ses transitions sp�cifiques ne part
							// pas d'un �tat supprim�
							else {
								ArrayList<PortalApplicationTransitions> array = null;
								if (group.getFlag() == 'E')
									array = group.getTransitions();
								else
									array = group.getNewTransitions();
								if (array != null) {
									// Si c'est le cas, on supprime la
									// transition sp�cifiques
									for (int indexArray = 0; indexArray < array
											.size(); indexArray++) {
										PortalApplicationTransitions ct = array
												.get(indexArray);
										if (deleteElements.get(indexDeleteElem)
												.getStateName().equals(ct.getStartState().getStateName())) {
											if (ct.getFlag() == 'A') {
												array.remove(indexArray);
												indexArray--;
											} 
											else {
												ct.setFlag('S');
											}
												// On met � jour le groupe de
											// transitions
											if (group.getFlag() == 'E') {
												group.setNewGroupTransitionName(group
														.getGroupTransitionName());
												group.setNewDescription(group
														.getDescription());
												group.setNewTransitionType(group
														.getTransitionType());
												group.setNewResponsabilities(group
														.getResponsabilities());
												group.setFlag('M');
											}
										}
									}
									// Une fois toutes les modifications faites,
									// on met � jour le
									// groupe de transitions
									group.setNewTransitions(array);
								}
							}
						}
					}
				}
			}
		}
		else
			// Le tableau �tait inexistant, on le cr�e
			tabModels = new ArrayList<ModelInterface>();

		// On ajoute les nouvelles transitions au tableau de ModelInterface
		tabModels.addAll(newElements);
		tabModels.addAll(deleteElements);
		

		return tabModels;
	}
	
	public void saveSpecificStates(ArrayList<PortalApplicationServicesSpecificStates> array) {

		_serviceStatesTable = array;
		fieldsHaveChanged();
	
	}
	
	
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
	protected void setEnterCallback(JComponent component)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ConfigurationFileDescriptionPanel", "setEnterCallback");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("component=" + component);
		// On v�rifie la validit� de l'argument
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
	
	
	private ArrayList<PortalApplicationServicesSpecificStates> _serviceStatesTable;
	
	private JTable _stateTable;
	
	private JTextField _nameTextField;
	
	private JTextField _descTextField;
	
	private JButton _subDefButton;
	
	private JButton _applyButton;
	
	private JButton _removeButton;
	
	private JButton _newButton;
	
	private ApplicationSpecificStatesPanel _subPanel;


	
	private class StatesTableModel extends AbstractTableModel {
		
		/*------------------------------------------------------------
		 * Nom : _titles
		 * 
		 * Description :
		 * Ce tableau de String repr�sente les titres des colonnes de la
		 * JTable.
		 * ------------------------------------------------------------*/
		private String [] _titles;
		
		/*------------------------------------------------------------
		 * Nom : _datas
		 * 
		 * Description :
		 * Cet attribut est un tableau de ComponentConfig.
		 * Chaque �l�ment du tableau repr�sente une ligne de la JTable
		 * et contient les informations d'un fichier.
		 * ------------------------------------------------------------*/
		private ArrayList<PortalApplicationStates> _datas;
		
		private ArrayList<PortalApplicationStates> _deleteDatas;
		
		/*------------------------------------------------------------
		 * Nom : StatesTableModel
		 * 
		 * Description :
		 * Cette m�thode est le contructeur de la classe.
		 * Son r�le est de cr�er les deux tableaux de titres et de donn�es.
		 * Pour le tableau des titres, elle le remplira � partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/		
		public StatesTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ServicesTableModel", "ConfigTableModel");
			trace_methods.beginningOfMethod();
			
			// Cr�ation du tableau des titres
			_titles = new String [2];
			// La premi�re colonne repr�sentera le nom du fichier
			_titles[0] = MessageManager.getMessage("&AppCfg_Name");
			// La deuxi�me colonne repr�sentera la description associ�e
			// au fichier
			_titles[1] = MessageManager.getMessage("&AppCfg_Description");
			
			// Cr�ation du tableau de donn�es
			_datas = new ArrayList<PortalApplicationStates>();
			_deleteDatas = new ArrayList<PortalApplicationStates>();
			
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
 		 * Cette m�thode retourne le nombre de lignes de la JTable. 
 		 * Ce nombre est donn� par la dimmension du tableaux des donn�es.
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
 		 * Cette m�thode retourne le tableau de ComponentConfig contenant
 		 * l'ensemble des informations saisies par l'utilisateur.
 		 * 
 		 * Retourne : Un tableau de ComponentConfig
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationStates> getDatas() {
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
 		 * Cette m�thode recherche la pr�sence d'un fichier dans la JTable
 		 * � partir du nom de fichier pass� en param�tre et du chemin absolu
 		 * saisient. Elle retourne le num�ro de la ligne correspondant 
 		 * � ce fichier si elle existe, -1 sinon.
 		 * 
 		 * Argument :
 		 *  - fileName : une cha�ne de caract�res correspondant au nom
 		 *    du fichier recherch�
 		 *  - filePath : un cha�ne de caract�res correspondant au chemin absolu
 		 *    du fichier
 		 *    
 		 * Retourne : un entier : le num�ro de ligne du fichier, -1 si 
 		 * elle n'existe pas.
		 * ------------------------------------------------------------*/
		public int findRow(String stateName, String description) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("stateName=" + stateName);
			trace_arguments.writeTrace("description=" + description);

			if (_datas != null)
			{
				for (int index=0 ; index < _datas.size() ; index++) {
					PortalApplicationStates serv = _datas.get(index);
					if (serv != null && serv.getStateName() != null 
							&& serv.getDescription() != null) {
						
						String agent = serv.getStateName();
						String service = serv.getDescription();
						
						if (agent.equals(stateName) && service.equals(description)){
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
 		 * Cette m�thode retourne la valeur de la cellule de la JTable
 		 * pour la position (ligne, colonne) donn�e.
 		 * Le num�ro de ligne correspond � un fichier particulier.
 		 * Le num�ro de colonne indique le champ : 
 		 * 	- 0 pour le nom du fichier
 		 *  - 1 pour sa description
 		 *  - 2 pour son chemin absolu
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
					"ConfigTableModel", "getValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				PortalApplicationStates serv = _datas.get(line);
				if (serv != null) {
					if (column == 0) {
						trace_methods.endOfMethod();
						return serv.getStateName();
					}
					else if (column == 1) {
						if (serv.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return serv.getDescription();
						}
						else {
							trace_methods.endOfMethod();
							return serv.getNewDescription();
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
 		 * Cette m�thode ajoute un nouvel �l�ment (un fichier) au 
 		 * tableau de donn�es. 
 		 * 
 		 * Argument : 
 		 *  - element : L'�l�ment � ajouter
		 * ------------------------------------------------------------*/
		public void addElement(PortalApplicationStates element){
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
 		 * Cette m�thode supprime un �l�ment (un fichier) au tableau 
 		 * de donn�es. 
 		 * 
 		 * Argument : 
 		 *  - line : Le num�ro de ligne de l'�l�ment � supprimer
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
 		 * Cette m�thode retourne le ComponentConfig de la ligne demand�e. 
 		 * 
 		 * Argument : 
 		 *  - line : Le num�ro de ligne de l'�l�ment � r�cup�rer
 		 *  
 		 * Retourne : Le ComponentConfig de la ligne s�lectionn�e.
		 * ------------------------------------------------------------*/
		public PortalApplicationStates getElementAt(int line) {
			
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
 		 * Cette m�thode ajoute au tableau des �l�ments � supprimer de la
 		 * base, le ComponentConfig pass� en param�tre. 
 		 * 
 		 * Argument : 
 		 *  - config : Le ComponentConfig qu'il faudra supprimer de la base
 		 *    de donn�es.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(PortalApplicationStates service) {
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
 		 * Cette m�thode retire au tableau des �l�ments � supprimer de la
 		 * base, le ComponentConfig d'index �gal � l'index pass� en param�tre. 
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
 		 * Cette m�thode retourne du tableau des �l�ments � supprimer de la
 		 * base, le ComponentConfig d'index �gal � l'index pass� en param�tre. 
 		 * 
 		 * Argument : 
 		 *  - index : L'index du ComponentConfig qu'il faut r�cup�rer dans le
 		 *    tableau.
 		 * 
 		 * Retourne : Le ComponentConfig supprim� de la ligne souhait�e
		 * ------------------------------------------------------------*/
		public PortalApplicationStates getDeleteElementAt(int index) {
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
 		 * Cette m�thode retourne l'ensemble du tableau des ComponentConfig
 		 * � supprimer de la base de donn�es. 
 		 * 
 		 * Retourne : Le tableau des ComponentConfig � supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationStates> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "getDeleteDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			
			return _deleteDatas;
		}
	}
	
	

	
	
	
	private StatesTableModel _tableModel;
	
	private void makePanel() {
		
		// On pr�sente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(476, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(476, 20));
		firstDescriptionSentence.setText(
				MessageManager.getMessage("&AppCfg_FirstDescriptionSentenceState"));
				
		_stateTable = new JTable();
		_stateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_stateTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});
		
		// On int�gre un d�filement dans le tableau
		JScrollPane scrollPaneConfigTable = new JScrollPane();
		scrollPaneConfigTable.setViewportView(_stateTable);
		scrollPaneConfigTable.setMinimumSize(new Dimension(476, 173));
		scrollPaneConfigTable.setMaximumSize(new Dimension(476, 173));
		
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
		
	    // On d�finit un label pour le champs "Agent"
	    JLabel nameLabel = new JLabel();
		nameLabel.setText(
				MessageManager.getMessage("&AppCfg_Name") 
				+ " :   *");
		// On d�finit un label pour le champs "Service"
		JLabel descLabel = new JLabel();
		descLabel.setText(
				MessageManager.getMessage("&AppCfg_Description") 
				+ " :   *");
		
		// champs sont obligatoires pour la saisie
		JLabel obligatoryField = new JLabel();
		obligatoryField.setText(
				MessageManager.getMessage("&AppCfg_ObligatoryFields"));
		obligatoryField.setMinimumSize(new Dimension(150,20));
		obligatoryField.setMaximumSize(new Dimension(150,20));
				
		int maxSize = nameLabel.getPreferredSize().width;
		if ( descLabel.getPreferredSize().width > maxSize )
			maxSize = descLabel.getPreferredSize().width;
		
		nameLabel.setPreferredSize(new Dimension(maxSize,20));
		nameLabel.setMinimumSize(new Dimension(maxSize,20));
		nameLabel.setMaximumSize(new Dimension(maxSize,20));
		descLabel.setPreferredSize(new Dimension(maxSize,20));
		descLabel.setMinimumSize(new Dimension(maxSize,20));
		descLabel.setMaximumSize(new Dimension(maxSize,20));
	    
		_nameTextField = new JTextField();
		setEnterCallback(_nameTextField);
		_nameTextField.setPreferredSize(new Dimension(
				440 - (nameLabel.getPreferredSize().width), 20));
		_nameTextField.setMinimumSize(new Dimension(
				440 - (nameLabel.getMinimumSize().width), 20));
		_nameTextField.setMaximumSize(new Dimension(
				440 - (nameLabel.getMaximumSize().width), 20));
		_nameTextField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fieldsHaveChanged();
			}
		});
		
		_descTextField = new JTextField();
		setEnterCallback(_descTextField);
		_descTextField.setPreferredSize(new Dimension(
				440 - (descLabel.getPreferredSize().width), 20));
		_descTextField.setMinimumSize(new Dimension(
				440 - (descLabel.getMinimumSize().width), 20));
		_descTextField.setMaximumSize(new Dimension(
				440 - (descLabel.getMaximumSize().width), 20));
		
		
		JLabel defStatesServiceLabel = new JLabel();
		defStatesServiceLabel.setMinimumSize(new Dimension(250, 20));
		defStatesServiceLabel.setMaximumSize(new Dimension(250, 20));
		defStatesServiceLabel.setText(MessageManager
				.getMessage("&AppCfg_DefinitionServicesStates")
				+ " : *");
		
		_subDefButton = new JButton(IconLoader.getIcon("arrow_right"));
		_subDefButton.setMinimumSize(new Dimension(60, 21));
		_subDefButton.setMaximumSize(new Dimension(60, 21));
		_subDefButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickSubDefButton();
			}
		});

		_applyButton = new JButton();
		_applyButton.setEnabled(false);
		_applyButton.setMinimumSize(new Dimension(120, 21));
		_applyButton.setMaximumSize(new Dimension(120, 21));
		_applyButton.setPreferredSize(new Dimension(120, 21));
		_applyButton.setText(
				MessageManager.getMessage("&AppCfg_ApplyButton"));
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
		_removeButton.setText(
				MessageManager.getMessage("&AppCfg_RemoveButton"));
		_removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickRemoveButton();
			}

		});

		_newButton = new JButton();
		_newButton.setMinimumSize(new Dimension(120, 21));
		_newButton.setMaximumSize(new Dimension(120, 21));
		_newButton.setPreferredSize(new Dimension(120, 21));
		_newButton.setText(
				MessageManager.getMessage("&AppCfg_NewButton"));
		_newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickNewButton();
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
	    panel2.add(nameLabel, c);
		
		c.gridx = 1;
	    c.gridy = 0;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(_nameTextField, c);
		
	    c.gridx = 0;
	    c.gridy = 1;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(descLabel, c);
		
	    c.gridx = 1;
	    c.gridy = 1;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(_descTextField, c);
	    
	    Box hBox1 = Box.createHorizontalBox();
		hBox1.add(defStatesServiceLabel);
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
			PortalApplicationStates state = _tableModel.getElementAt(index);
			
			_nameTextField.setText(state.getStateName());
			_nameTextField.setEnabled(false);
			if (state.getFlag() == 'E') {
				_descTextField.setText(state.getDescription());
				_serviceStatesTable = state.getSpecificStates();
			}
			else {
				_descTextField.setText(state.getNewDescription());
				_serviceStatesTable = state.getNewSpecificStates();	
			}
			_descTextField.setEnabled(true);
			_applyButton.setEnabled(false);
			_removeButton.setEnabled(true);
		}
	}
	
	private void onClickApplyButton() {
		
		String nameState = _nameTextField.getText();
		String descState = _descTextField.getText();
		
		if (nameState.equals("") || descState.equals("")) {
			System.out.println("Les champs obligatoires doivent �tre remplis");
		} else {
			if (_serviceStatesTable == null) {
				System.out.println("La d�finition des �tats des services est obligatoires.");
			} 
			else {
				int selectedRow = _stateTable.getSelectedRow();
				if (selectedRow == -1) {
					// Nouvel �l�ment
					if (_tableModel.findRow(nameState, descState) == -1) {
						
						PortalApplicationStates state = new PortalApplicationStates();
						char flag = 'A';
						
						ArrayList<PortalApplicationStates> deleteElement = 
							_tableModel.getDeleteDatas();
						if (deleteElement != null) {
							for (int index = 0 ; index < deleteElement.size(); index ++) {
								
								if (deleteElement.get(index).getStateName().equals(nameState)
										&& deleteElement.get(index).getDescription().equals(descState)) {
									flag = 'M';
									state = deleteElement.get(index);
									deleteElement.remove(index);
									break;
								}
							}
						}
						
						state.setStateName(nameState);
						state.setNewDescription(descState);
						state.setNewSpecificStates(_serviceStatesTable);
						state.setFlag(flag);
						_tableModel.addElement(state);
						
						_serviceStatesTable = null;
						_stateTable.getSelectionModel().clearSelection();
						
						_nameTextField.setText("");
						_nameTextField.setEnabled(true);
						_descTextField.setText("");
						_descTextField.setEnabled(true);
									
						_applyButton.setEnabled(false);
						_removeButton.setEnabled(false);
					}
					else {
						System.out.println("Tentative d'ajout d'un service existant !");
					}
				}
				else {
					// Modification d'un �l�ment existant
					PortalApplicationStates state = 
							_tableModel.getElementAt(selectedRow);
					state.setNewDescription(descState);
					state.setNewSpecificStates(_serviceStatesTable);
					if (state.getFlag() == 'E') {
						state.setFlag('M');
					}
					_tableModel.fireTableRowsUpdated(selectedRow, selectedRow);
					
					_serviceStatesTable = null;
					_stateTable.getSelectionModel().clearSelection();
					
					_nameTextField.setText("");
					_nameTextField.setEnabled(true);
					_descTextField.setText("");
					_descTextField.setEnabled(true);
								
					_applyButton.setEnabled(false);
					_removeButton.setEnabled(false);					
				}
			}
		}
	}
	
	private void onClickRemoveButton() {
		
		int row = _stateTable.getSelectedRow();
		
		if ( row != -1 ) {
			PortalApplicationStates state = _tableModel.getElementAt(row);
			if (state != null ) {
				if ( state.getFlag() == 'A' ) 
					_tableModel.removeElement(row);
				else if ( state.getFlag() == 'E' || state.getFlag() == 'M' ) {
					_tableModel.removeElement(row);
					state.setFlag('S');
					_tableModel.addDeleteElement(state);
				}
				else {
					System.out.println("Supression impossible, Flag inatendu : " + state.getFlag());
				}
			}
			_tableModel.fireTableRowsDeleted(row, row);
		}
		_serviceStatesTable = null;
		_stateTable.getSelectionModel().clearSelection();
		
		_nameTextField.setText("");
		_nameTextField.setEnabled(true);
		_descTextField.setText("");
		_descTextField.setEnabled(true);
		
		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
	}
	
	private void onClickNewButton() {
		
		_serviceStatesTable = null;
		_stateTable.getSelectionModel().clearSelection();
		
		_nameTextField.setText("");
		_nameTextField.setEnabled(true);
		_descTextField.setText("");
		_descTextField.setEnabled(true);
					
		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
	}
	
	private void onClickSubDefButton() {
		
		if (_subPanel != null) {
			_subPanel.loadStatesArray(_window.getModel(), _serviceStatesTable);
		
			_window.displayPanel(_subPanel);
			_window.enabledNextAndPreviousButton(false);
		}
	}
	
	private void fieldsHaveChanged() {
		
		if (_nameTextField.getText().equals("") 
				|| _descTextField.getText().equals("")
				|| _serviceStatesTable == null) {
			_applyButton.setEnabled(false);
		}
		else {
			_applyButton.setEnabled(true);
		}
	}
}
