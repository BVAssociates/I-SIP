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

import com.bv.core.config.ConfigurationAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.impl.processor.appcfg.ApplicationConfigurationProcessor;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationGroupTransitions;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationServices;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationTransitionServices;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationTransitions;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;

public class ApplicationServicesPanel extends AbstractPanel {
	
	public ApplicationServicesPanel(WindowInterface window) {
		_window = window;
		makePanel();
	}
	
	public void beforeDisplay(ArrayList<ModelInterface> modele) {
		
		_tableModel = new ServicesTableModel();
		if (modele != null) {
			for ( int i=0 ; i < modele.size() ; i++) {
				if ( modele.get(i) instanceof PortalApplicationServices ) {
					PortalApplicationServices serv = (PortalApplicationServices) modele.get(i);
					if (serv.getFlag() != 'S')
						_tableModel.addElement(serv);
					else
						_tableModel.addDeleteElement(serv);
				}
			}
		}
		_serviceTable.setModel( _tableModel );
		_agentBox.removeAllItems();
		_agentBox.addItem("");
		ApplicationConfigurationProcessor c = (ApplicationConfigurationProcessor) _window;

		GenericTreeObjectNode t = (GenericTreeObjectNode) c.getSelectedNode();
		try {
			ServiceSessionProxy session = new ServiceSessionProxy(t
					.getServiceSession());
			String[] selectedColumns = { "AgentName" };
			String[] result = session.getSelectResult("PortalAgents", selectedColumns,
					"", "", t.getContext(true));

			if (result != null && result.length >= 2) {
				String[] agentList = new String[result.length - 1];

				System.arraycopy(result, 1, agentList, 0,
						result.length - 1);
				for (int i = 0 ; i < agentList.length ; i++)
					_agentBox.addItem(agentList[i]);
			}
		} catch (Exception e) {
			System.out.println("Impossible de charger la table des agents : " + e);
		}			
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
		
		ArrayList<PortalApplicationServices> deletedElements = _tableModel.getDeleteDatas();
		ArrayList<PortalApplicationServices> newElements = _tableModel.getDatas();
		
		if (tabModels != null) {
			// On supprime tous les anciens fichier de configuration
			for (int index=0 ; index < tabModels.size() ; index++) {
				if (tabModels.get(index) instanceof PortalApplicationServices) {
					tabModels.remove(index);
					// On recule d'une case dans le tableau pour ne pas oublier
					// l'élément décalé
					index --;					
				}
				else if (tabModels.get(index) instanceof PortalApplicationGroupTransitions) {
					PortalApplicationGroupTransitions group = 
						(PortalApplicationGroupTransitions) tabModels.get(index);
					ArrayList<PortalApplicationTransitions> transArray = null;
					if (group.getFlag() == 'E') {
						transArray = group.getTransitions(); 
					}
					else if (group.getFlag() == 'A' || group.getFlag() == 'M') {
						transArray = group.getNewTransitions();
					}
					if (transArray != null) {
						
						for (int indexTrans = 0 ; indexTrans < transArray.size() ; indexTrans++) {
							ArrayList<PortalApplicationTransitionServices> transServArray = null;
							if (transArray.get(indexTrans).getFlag() == 'E')
								transServArray = transArray.get(indexTrans).getTransitionServices();
							else if (transArray.get(indexTrans).getFlag() == 'A' || transArray.get(indexTrans).getFlag() == 'M')
								transServArray = transArray.get(indexTrans).getNewTransitionServices();
							
							if (transServArray != null) {
								
								for (int indexTransServ = 0 ; indexTransServ < transServArray.size() ; indexTransServ++) {
									
									for (int indexDelElem = 0 ; indexDelElem < deletedElements.size() ; indexDelElem++) {
										
										if (deletedElements.get(indexDelElem).getAgentName().equals(
												transServArray.get(indexTransServ).getComposition().getService().getAgentName())
											&&
											deletedElements.get(indexDelElem).getServiceName().equals(
													transServArray.get(indexTransServ).getComposition().getService().getServiceName())
											) {
											
											// on doit supprimer la transition de service
											if (transServArray.get(indexTransServ).getFlag() == 'A') {
												transServArray.remove(indexTransServ);
												indexTransServ--;
											}
											else
												transServArray.get(indexTransServ).setFlag('S');
										}
									}
								}
							}
						}
					}
				}
			}
		}
		else
			// Le tableau était inexistant, on le crée
			tabModels = new ArrayList<ModelInterface>();
		
		// On ajoute les nouveaux fichiers au tableau de ModelInterface
		tabModels.addAll(deletedElements);
		
		for (int index = 0 ; index < newElements.size() ; index ++) {
			PortalApplicationServices serv = (PortalApplicationServices) newElements.get(index);
			if (serv.getFlag() == 'A') {
				
				String TableServicesStates = "PortalServiceStates";
				String AgentServiceStates = "AgentName";
				String ServiceServiceStates = "ServiceName";
				String NameServiceStates = "StateName";
				
				try {
					// On récupère l'ensemble des noms des champs de la table
					// correspondant aux variables.
					ConfigurationAPI configuration_api = new ConfigurationAPI();
					configuration_api.useSection("I-SIS");
					
					TableServicesStates= configuration_api.getString("PortalApplication." +
							"TableServicesStates");
					AgentServiceStates = configuration_api.getString("PortalApplication." +
							"TableServicesStates.AgentName");
					ServiceServiceStates = configuration_api.getString("PortalApplication." +
							"TableServicesStates.ServiceName");
					NameServiceStates = configuration_api.getString("PortalApplication." +
							"TableServicesStates.StateName");
				}
				catch(Exception exception) {
					//trace_errors.writeTrace(
					//	"Erreur lors de la récupération de la configuration: " +
					//	exception);
					// Il y a eu une erreur, on continue quand même
				}
								
				ApplicationConfigurationProcessor c = (ApplicationConfigurationProcessor) _window;
				GenericTreeObjectNode t = (GenericTreeObjectNode) c.getSelectedNode();
				try {
					ServiceSessionProxy session = new ServiceSessionProxy(t
							.getServiceSession());
					String[] selectedColumns = { NameServiceStates };
					String condition = AgentServiceStates + "='" + serv.getAgentName() + 
							"' AND " + ServiceServiceStates + "='" + serv.getServiceName() + "'";
					String[] result = session.getSelectResult(TableServicesStates, selectedColumns,
							condition, "", t.getContext(true));

					if (result != null && result.length >= 2) {
						String[] stateList = new String[result.length - 1];

						System.arraycopy(result, 1, stateList, 0,
								result.length - 1);
						for (int i = 0 ; i < stateList.length ; i++)
							serv.addServiceState(stateList[i]);
					}
				} catch (Exception e) {
					System.out.println("Impossible de charger la table des agents : " + e);
				}		
			}
		}
		tabModels.addAll(newElements);
		
		return tabModels;
	}

	private JTable _serviceTable;
	
	private JComboBox _agentBox;
	
	private JComboBox _serviceBox;
	
	private JButton _applyButton;
	
	private JButton _removeButton;
	
	private JButton _newButton;
	
	private class ServicesTableModel extends AbstractTableModel {
		
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
		
		private ArrayList<PortalApplicationServices> _deleteDatas;
		
		/*------------------------------------------------------------
		 * Nom : ConfigTableModel
		 * 
		 * Description :
		 * Cette méthode est le contructeur de la classe.
		 * Son rôle est de créer les deux tableaux de titres et de données.
		 * Pour le tableau des titres, elle le remplira à partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/		
		public ServicesTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ServicesTableModel", "ConfigTableModel");
			trace_methods.beginningOfMethod();
			
			// Création du tableau des titres
			_titles = new String [2];
			// La première colonne représentera le nom du fichier
			_titles[0] = MessageManager.getMessage("&AppCfg_Agent");
			// La deuxième colonne représentera la description associée
			// au fichier
			_titles[1] = MessageManager.getMessage("&AppCfg_Service");
			
			// Création du tableau de données
			_datas = new ArrayList<PortalApplicationServices>();
			_deleteDatas = new ArrayList<PortalApplicationServices>();
			
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
		public int findRow(String agentName, String serviceName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("agentName=" + agentName);
			trace_arguments.writeTrace("serviceName=" + serviceName);

			if (_datas != null)
			{
				for (int index=0 ; index < _datas.size() ; index++) {
					PortalApplicationServices serv = _datas.get(index);
					if (serv != null && serv.getAgentName() != null 
							&& serv.getServiceName() != null) {
						
						String agent = serv.getAgentName();
						String service = serv.getServiceName();
						
						if (agent.equals(agentName) && service.equals(serviceName)){
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
		
		/*------------------------------------------------------------
		 * Nom : addDeleteElement
		 * 
		 * Description : 
 		 * Cette méthode ajoute au tableau des éléments à supprimer de la
 		 * base, le PortalApplicationServices passé en paramètre. 
 		 * 
 		 * Argument : 
 		 *  - config : Le PortalApplicationServices qu'il faudra supprimer de la base
 		 *    de données.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(PortalApplicationServices service) {
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
 		 * base, le PortalApplicationServices d'index égal à l'index passé en paramètre. 
 		 * 
 		 * Argument : 
 		 *  - index : L'index du PortalApplicationServices qu'il faut retirer du tableau.
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
 		 * base, le PortalApplicationServices d'index égal à l'index passé en paramètre. 
 		 * 
 		 * Argument : 
 		 *  - index : L'index du PortalApplicationServices qu'il faut récupérer dans le
 		 *    tableau.
 		 * 
 		 * Retourne : Le PortalApplicationServices supprimé de la ligne souhaitée
		 * ------------------------------------------------------------*/
		public PortalApplicationServices getDeleteElementAt(int index) {
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
 		 * Cette méthode retourne l'ensemble du tableau des PortalApplicationServices
 		 * à supprimer de la base de données. 
 		 * 
 		 * Retourne : Le tableau des PortalApplicationServices à supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationServices> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "getDeleteDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			
			return _deleteDatas;
		}
	}
	
	private ServicesTableModel _tableModel;
	
	
	private void makePanel() {
		
		// On présente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(476, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(476, 20));
		firstDescriptionSentence.setText(
				MessageManager.getMessage("&AppCfg_FirstDescriptionSentenceServices"));
				
		_serviceTable = new JTable();
		_serviceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_serviceTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});
		
		// On intégre un défilement dans le tableau
		JScrollPane scrollPaneConfigTable = new JScrollPane();
		scrollPaneConfigTable.setViewportView(_serviceTable);
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
		
	    // On définit un label pour le champs "Agent"
	    JLabel agentLabel = new JLabel();
		agentLabel.setText(MessageManager.getMessage("&AppCfg_Agent") 
				+ " :   *");
		// On définit un label pour le champs "Service"
		JLabel serviceLabel = new JLabel();
		serviceLabel.setText(MessageManager.getMessage("&AppCfg_Service") 
				+ " :   *");
		
		// champs sont obligatoires pour la saisie
		JLabel obligatoryField = new JLabel();
		obligatoryField.setText(MessageManager.
				getMessage("&AppCfg_ObligatoryFields"));
		obligatoryField.setMinimumSize(new Dimension(150,20));
		obligatoryField.setMaximumSize(new Dimension(150,20));
				
		int maxSize = agentLabel.getPreferredSize().width;
		if ( serviceLabel.getPreferredSize().width > maxSize )
			maxSize = serviceLabel.getPreferredSize().width;
		
		agentLabel.setPreferredSize(new Dimension(maxSize,20));
		agentLabel.setMinimumSize(new Dimension(maxSize,20));
		agentLabel.setMaximumSize(new Dimension(maxSize,20));
		serviceLabel.setPreferredSize(new Dimension(maxSize,20));
		serviceLabel.setMinimumSize(new Dimension(maxSize,20));
		serviceLabel.setMaximumSize(new Dimension(maxSize,20));
		
		_agentBox = new JComboBox();
		_agentBox.setPreferredSize(new Dimension(
				440 - (agentLabel.getPreferredSize().width), 20));
		_agentBox.setMinimumSize(new Dimension(
				440 - (agentLabel.getMinimumSize().width), 20));
		_agentBox.setMaximumSize(new Dimension(
				440 - (agentLabel.getMaximumSize().width), 20));
		_agentBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onSelectionAgent();
			}
		});
		
		_serviceBox = new JComboBox();
		_serviceBox.setEnabled(false);
		_serviceBox.setPreferredSize(new Dimension(
				440 - (serviceLabel.getPreferredSize().width), 20));
		_serviceBox.setMinimumSize(new Dimension(
				440 - (serviceLabel.getMinimumSize().width), 20));
		_serviceBox.setMaximumSize(new Dimension(
				440 - (serviceLabel.getMaximumSize().width), 20));
		_serviceBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				onSelectionService();
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
	    panel2.add(agentLabel, c);
		
		c.gridx = 1;
	    c.gridy = 0;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(_agentBox, c);
		
	    c.gridx = 0;
	    c.gridy = 1;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(serviceLabel, c);
		
	    c.gridx = 1;
	    c.gridy = 1;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(_serviceBox, c);
	    
	    Box hBox1 = Box.createHorizontalBox();
	    hBox1.add(_newButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_removeButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_applyButton);
		hBox1.setMaximumSize(new Dimension(380,22));
		
	    c.gridx = 0;
	    c.gridy = 2;
	    c.gridwidth = 2;
	    c.gridheight = 1;
	    c.ipady = 15;
	    c.anchor = GridBagConstraints.LINE_END;
	    panel2.add(hBox1, c);

	    c.gridx = 0;
	    c.gridy = 3;
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

			_agentBox.setEnabled(true);
			_agentBox.setSelectedIndex(0);
			_serviceBox.setEnabled(false);
			_serviceBox.removeAllItems();
			
			_applyButton.setEnabled(false);
			_removeButton.setEnabled(false);
		} 
		else {
			PortalApplicationServices serv = _tableModel.getElementAt(index);
			
			_agentBox.setSelectedItem(serv.getAgentName());
			_agentBox.setEnabled(false);
			
			_serviceBox.setSelectedItem(serv.getAgentName());
			_serviceBox.setEnabled(false);
			
			_applyButton.setEnabled(false);
			_removeButton.setEnabled(true);
		}
	}
	
	private void onSelectionAgent() {
		
		if (_agentBox.getItemCount() > 0) {
			String selectedAgentName = _agentBox.getSelectedItem().toString();
			if (!selectedAgentName.equals(""))
			{
				_serviceBox.removeAllItems();
				ApplicationConfigurationProcessor c = (ApplicationConfigurationProcessor) _window;
	
				GenericTreeObjectNode t = (GenericTreeObjectNode) c.getSelectedNode();
				try {
					ServiceSessionProxy session = new ServiceSessionProxy(t
							.getServiceSession());
					String[] selectedColumns = { "ServiceName" };
					String[] result = session.getSelectResult("PortalServices", selectedColumns,
							"AgentName='" + selectedAgentName + "'", "", t.getContext(true));
		
					if (result != null && result.length >= 2) {
						String[] serviceList = new String[result.length - 1];
		
						System.arraycopy(result, 1, serviceList, 0,
								result.length - 1);
						for (int i = 0 ; i < serviceList.length ; i++)
							_serviceBox.addItem(serviceList[i]);
					}
					_serviceBox.setEnabled(true);
					_applyButton.setEnabled(true);
					_removeButton.setEnabled(false);
				} catch (Exception e) {
					System.out.println("Erreur lors du chargement des services : " + e);
				}
			}
			else {
				_serviceBox.setEnabled(false);
				_serviceBox.removeAllItems();
				
				_applyButton.setEnabled(false);
				_removeButton.setEnabled(false);
			}
		}
	}
	
	private void onSelectionService() {
		_applyButton.setEnabled(true);
		_removeButton.setEnabled(false);
	}
	
	private void onClickApplyButton() {
		String selectedAgentName = _agentBox.getSelectedItem().toString();
		String selectedServiceName = _serviceBox.getSelectedItem().toString();
		
		if (selectedAgentName.equals("") || selectedServiceName.equals(""))
		{
			DialogManager.displayDialog("Information", MessageManager
					.getMessage("&AppCfg_MessageInformation"),
					null, null);
		}
		else {
			int row = _serviceTable.getSelectedRow();
			if (row == -1) {
				// Nouvel élément
				
				if (_tableModel.findRow(selectedAgentName, selectedServiceName) == -1) {
					
					PortalApplicationServices serv = new PortalApplicationServices();
					char flag = 'A';
					
					ArrayList<PortalApplicationServices> deleteElement = 
						_tableModel.getDeleteDatas();
					if (deleteElement != null) {
						for (int index = 0 ; index < deleteElement.size(); index ++) {
							
							if (deleteElement.get(index).getAgentName().equals(selectedAgentName)
									&& deleteElement.get(index).getServiceName().equals(selectedServiceName)) {
								flag = 'M';
								serv = deleteElement.get(index);
								deleteElement.remove(index);
								break;
							}
						}
					}
					
					serv.setAgentName(selectedAgentName);
					serv.setServiceName(selectedServiceName);
					serv.setFlag(flag);
					_tableModel.addElement(serv);
					
					_agentBox.setEnabled(true);
					_agentBox.setSelectedIndex(0);
					_serviceBox.setEnabled(false);
					_serviceBox.removeAllItems();
					_removeButton.setEnabled(false);
					_applyButton.setEnabled(false);
				}
				else {
					System.out.println("Tentative d'ajout d'un service existant !");
				}
			}
			else {
				System.out.println("Modif d'un service impossible !");
			}
		}
	}
	
	private void onClickRemoveButton() {
		
		int row = _serviceTable.getSelectedRow();
		
		if ( row != -1 ) {
			PortalApplicationServices serv = _tableModel.getElementAt(row);
			if (serv != null ) {
				if ( serv.getFlag() == 'A' ) 
					_tableModel.removeElement(row);
				else if ( serv.getFlag() == 'E' || serv.getFlag() == 'M' ) {
					_tableModel.removeElement(row);
					serv.setFlag('S');
					_tableModel.addDeleteElement(serv);
				}
				else {
					System.out.println("Supression impossible, Flag inatendu : " + serv.getFlag());
				}
			}
			_tableModel.fireTableRowsDeleted(row, row);
		}
		_agentBox.setEnabled(true);
		_agentBox.setSelectedIndex(0);
		_serviceBox.setEnabled(false);
		_serviceBox.removeAllItems();
		
		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
	}

	private void onClickNewButton() {
		_agentBox.setEnabled(true);
		_agentBox.setSelectedIndex(0);
		_serviceBox.setEnabled(false);
		_serviceBox.removeAllItems();
		
		_applyButton.setEnabled(false);
		_removeButton.setEnabled(false);
	}
}
