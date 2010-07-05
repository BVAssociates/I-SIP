/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/LogDescriptionPanel.java,v $
* $Revision: 1.16 $
*
* ------------------------------------------------------------
* DESCRIPTION: Panneau de saisie des fichiers de log
* DATE:        01/07/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
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

//
//Imports du projet
//
import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentLog;

/*------------------------------------------------------------
 * Nom: LogDescriptionPanel
 * 
 * Description: 
 * Cette classe implémente le panneau qui permet de définir 
 * les fichiers de log d'un composant I-SIS.
 * ------------------------------------------------------------*/
public class LogDescriptionPanel extends AbstractPanel {

	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: LogDescriptionPanel
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * Elle appelle la méthode makePanel() chargée de la création des objets
	 * graphiques a afficher dans le panneau.		
	 * ------------------------------------------------------------*/
	public LogDescriptionPanel(WindowInterface window) {
		super();
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"LogDescriptionPanel", "LogDescriptionPanel");
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
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Elle est appelée par la classe AbstractWindow lorsque le panneau doit 
	 * être affiché. A partir du modèle de données passé en paramètre, elle 
	 * se charge de pré-renseigner les champs du panneau.
	 * 
	 * Paramètres :
	 *  - tabModels : Le tableau de ModelInterface.
 	 * ------------------------------------------------------------*/
	public void beforeDisplay(ArrayList<ModelInterface> modele) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"LogDescriptionPanel", "beforeDisplay");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("modele=" + modele);
		
		_tableModel = new LogTableModel();
		
		if (modele != null) {
			for ( int i=0 ; i < modele.size() ; i++) {
				if ( modele.get(i) instanceof ComponentLog ) {
					ComponentLog c = (ComponentLog) modele.get(i);
					if (c.getFlag() != 'S')
						_tableModel.addElement(c);
					else
						_tableModel.addDeleteElement(c);
				}
			}
		}
		_logTable.setModel( _tableModel );	
		trace_methods.endOfMethod();
	}

	/*------------------------------------------------------------
	 * Nom: afterDisplay
	 * 
	 * Description: 
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Elle n'est présentée que pour des raisons de lisibilité.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean afterDisplay() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"logDescriptionPanel", "afterDisplay");
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
	 * informations saisis par l'utilisateur. Si il y a une erreur, elle 
	 * retourne faux sinon vrai.
	 * 
	 * Retourne : vrai (true) si tout est conforme, faux (false) sinon.
	 * ------------------------------------------------------------*/
	public boolean beforeHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"LogDescriptionPanel", "beforeHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}

	/*------------------------------------------------------------
	 * Nom: afterHide
	 * 
	 * Description: 
	 * Cette méthode implémente la méthode de l'interface PanelInterface.
	 * Cette méthode définit le comportement du panneau après avoir été caché.
	 * Elle n'est présentée que pour des raisons de lisibilité.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ------------------------------------------------------------*/
	public boolean afterHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"LogDescriptionPanel", "afterHide");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}

	/*------------------------------------------------------------
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
	 * ------------------------------------------------------------*/
	public boolean end() {
		return true;
	}

	public ArrayList<ModelInterface> update(ArrayList<ModelInterface> tabModels) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"LogDescriptionPanel", "end");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		ArrayList<ComponentLog> deletedElements = _tableModel.getDeleteDatas();
		ArrayList<ComponentLog> newElements = _tableModel.getDatas();
		
		if (tabModels != null) {
			
			// On supprime tous les anciens fichier de log
			for (int index=0 ; index < tabModels.size() ; index++) {
				if (tabModels.get(index) instanceof ComponentLog) {
					tabModels.remove(index);
					// On recule d'une case dans le tableau pour ne pas oublier
					// l'élément décalé
					index --;
				}
			}
		}
		else
			// Le tableau était inexistant, on le crée
			tabModels = new ArrayList<ModelInterface>();
		
		// On ajoute les nouveaux fichiers au tableau de ModelInterface
		tabModels.addAll(deletedElements);
		tabModels.addAll(newElements);
		
		trace_methods.endOfMethod();
		return tabModels;
	}

	// ****************** PROTEGE *********************
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
	protected void setEnterCallback(
		JComponent component
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "LogDescriptionPanel", "setEnterCallback");
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
	
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _logTable
	 * 
	 * Description :
	 * Cette attribut représente un tableau récapitulant l'ensemble
	 * des fichier de logs définis par l'utilisateur.
	 * ------------------------------------------------------------*/
	private JTable _logTable;
	
	/*------------------------------------------------------------
	 * Nom : _textName
	 * 
	 * Description :
	 * Cet attribut est utilisé pour la saisie du nom de la variable
	 * ------------------------------------------------------------*/
	protected JTextField _nameTextField;
	
	/*------------------------------------------------------------
	 * Nom : _descriptionTextField
	 * 
	 * Description :
	 * Cette attribut définit un champ de saisie pour la description
	 * d'un fichier de log dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _descriptionTextField;
	
	/*------------------------------------------------------------
	 * Nom : _rootTextField
	 * 
	 * Description :
	 * Cette attribut définit un champ de saisie pour la racine
	 * d'un fichier de log dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _rootTextField;
	
	/*------------------------------------------------------------
	 * Nom : _patternTextField
	 * 
	 * Description :
	 * Cette attribut définit un champ de saisie pour le modèle
	 * d'un fichier de log dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _patternTextField;
	
	/*------------------------------------------------------------
	 * Nom : _newButton
	 * 
	 * Description :
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * permettre une nouvelle saisie.
	 * ------------------------------------------------------------*/
	private JButton _newButton;
	
	/*------------------------------------------------------------
	 * Nom : _removeButton
	 * 
	 * Description :
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * effacer la ligne sélectionner dans le tableau des fichiers.
	 * ------------------------------------------------------------*/
	private JButton _removeButton;
	
	/*------------------------------------------------------------
	 * Nom : _applyButton
	 * 
	 * Description :
	 * Cette attribut définit un boutton. Celui-ci est utilisé pour
	 * enregistrer les informations saisies dans le sous panneau de 
	 * saisie, dans le tableau des fichiers.
	 * ------------------------------------------------------------*/
	private JButton _applyButton;

	/*------------------------------------------------------------
	 * Nom : LogTableModel
	 * 
	 * Description :
	 * Cette classe représente le modele associé à la JTable.
	 * Elle dérive de AbstractTableModel.
	 * Elle se caractérise de 2 tableaux:
	 *  - un pour les titres des diverses colonnes
	 *  - un pour les données de chaque ligne.
	 * Chaque ligne est une instance de ComponentLog.
	 * ------------------------------------------------------------*/
	private class LogTableModel extends AbstractTableModel {
		
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
		private ArrayList<ComponentLog> _datas;
		
		private ArrayList<ComponentLog> _deleteDatas;
		
		
		/*------------------------------------------------------------
		 * Nom : ConfigTableModel
		 * 
		 * Description :
		 * Cette méthode est le contructeur de la classe.
		 * Son rôle est de créer les deux tableaux de titres et de données.
		 * Pour le tableau des titres, elle le remplira à partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/		
		public LogTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "LogTableModel");
			trace_methods.beginningOfMethod();
			
			// Création du tableau des titres
			_titles = new String [4];
			// La première colonne représentera le nom du fichier
			_titles[0] = 
				MessageManager.getMessage("&ComponentConfiguration_Name");
			// La deuxième colonne représentera la description associée
			// au fichier
			_titles[1] = 
				MessageManager.getMessage("&ComponentConfiguration_Description");
			// La troisième colonne représentera la racine du fichier
			_titles[2] = 
				MessageManager.getMessage("&ComponentConfiguration_Root");
			// La troisième colonne représentera le modèle du fichier
			_titles[3] = 
				MessageManager.getMessage("&ComponentConfiguration_Pattern");
			
			// Création du tableau de données
			_datas = new ArrayList<ComponentLog>();
			_deleteDatas = new ArrayList<ComponentLog>();
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
					"LogTableModel", "getColumnCount");
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
					"LogTableModel", "getRowCount");
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
 		 * Cette méthode retourne le tableau de ComponentLog contenant
 		 * l'ensemble des informations saisies par l'utilisateur.
 		 * 
 		 * Retourne : Un tableau de ComponentLog
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentLog> getDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "getDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();

			return _datas;
		}
		
		/*------------------------------------------------------------
		 * Nom : findRow
		 * 
		 * Description : 
 		 * Cette méthode recherche la présence d'un fichier dans la JTable
 		 * à partir du nom de fichier passé en paramètre, de sa racine et 
 		 * du modèle saisient. Elle retourne le numéro de la ligne correspondant 
 		 * à ce fichier si elle existe, -1 sinon.
 		 * 
 		 * Argument :
 		 *  - logName : une chaîne de caractères correspondant au nom
 		 *    du fichier recherché
 		 *  - logRoot : un chaîne de caractères correspondant à la racine
 		 *    du fichier
 		 *  - logPattern : un chaîne de caractères correspondant au modèle
 		 *    du fichier
 		 *    
 		 * Retourne : un entier : le numéro de ligne du fichier, -1 si 
 		 * elle n'existe pas.
		 * ------------------------------------------------------------*/
		public int findRow(String logName, String logRoot, String logPattern) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("logName=" + logName);
			trace_arguments.writeTrace("logRoot=" + logRoot);
			trace_arguments.writeTrace("logPattern=" + logPattern);
			
			if (_datas != null)
			{
				for (int index=0 ; index < _datas.size() ; index++) {
					ComponentLog cl = _datas.get(index);
					if (cl != null && cl.getLogName() != null 
							&& cl.getRoot() != null && cl.getPattern() != null) {
						
						String name = cl.getLogName();
						String root = cl.getRoot();
						String pattern = cl.getPattern();
						if (name.equals(logName) && root.equals(logRoot) 
								&& pattern.equals(logPattern)){
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
 		 *  - 2 pour sa racine
 		 *  - 3 pour son modèle
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
					"LogTableModel", "getValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				ComponentLog cl = _datas.get(line);
				if (cl != null) {
					if (column == 0) {
						trace_methods.endOfMethod();
						return cl.getLogName();
					}
					else if (column == 1) {
						if (cl.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return cl.getDescription();
						}
						else {
							trace_methods.endOfMethod();
							return cl.getNewDescription();
						}
					}
					else if (column == 2) {
						trace_methods.endOfMethod();
						return cl.getRoot();
					}
					else if (column == 3) {
						trace_methods.endOfMethod();
						return cl.getPattern();
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
					"LogTableModel", "getColumnCount");
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
		public void addElement(ComponentLog element){
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "addElement");
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
					"LogTableModel", "removeElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);

			if (_datas != null) {
				_datas.remove(line);
				super.fireTableRowsDeleted(line,line);
			}
			
			trace_methods.endOfMethod();
		}
		
		/*------------------------------------------------------------
		 * Nom : getElementAt
		 * 
		 * Description : 
 		 * Cette méthode retourne le ComponentLog de la ligne demandée. 
 		 * 
 		 * Argument : 
 		 *  - line : Le numéro de ligne de l'élément à récupérer
 		 *  
 		 * Retourne : Le ComponentLog de la ligne sélectionnée.
		 * ------------------------------------------------------------*/
		public ComponentLog getElementAt(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "getElementAt");
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
 		 * base, le ComponentLog passé en paramètre. 
 		 * 
 		 * Argument : 
 		 *  - config : Le ComponentLog qu'il faudra supprimer de la base
 		 *    de données.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(ComponentLog log) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "addDeleteElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("log=" + log);

			if (_deleteDatas != null)
				_deleteDatas.add(log);
			
			trace_methods.endOfMethod();
		}
		
		/*------------------------------------------------------------
		 * Nom : removeDeleletElement
		 * 
		 * Description : 
 		 * Cette méthode retire au tableau des éléments à supprimer de la
 		 * base, le ComponentLog d'index égal à l'index passé en paramètre. 
 		 * 
 		 * Argument : 
 		 *  - index : L'index du ComponentLog qu'il faut retirer du tableau.
		 * ------------------------------------------------------------*/
		public void removeDeleletElement(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "removeDeleletElement");
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
 		 * base, le ComponentLog d'index égal à l'index passé en paramètre. 
 		 * 
 		 * Argument : 
 		 *  - index : L'index du ComponentLog qu'il faut récupérer dans le
 		 *    tableau.
 		 * 
 		 * Retourne : Le ComponentLog supprimé de la ligne souhaitée
		 * ------------------------------------------------------------*/
		public ComponentLog getDeleteElementAt(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "getDeleteElementAt");
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
 		 * Cette méthode retourne l'ensemble du tableau des ComponentLog
 		 * à supprimer de la base de données. 
 		 * 
 		 * Retourne : Le tableau des ComponentLog à supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<ComponentLog> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "getDeleteDatas");
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _deleteDatas;
		}
	}
	
	/*------------------------------------------------------------
	 * Nom : _tableModel
	 * 
	 * Description :
	 * Cette attribut maintient une référence sur le modèle associé à
	 * la JTable.
	 * ------------------------------------------------------------*/
	private LogTableModel _tableModel;
	
	/*------------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette méthode est appelé lors de la construction du panneau.
	 * Son rôle est d'instancier les composants graphiques du panneau et de les
	 * positionner.
	 * ------------------------------------------------------------*/
	private void makePanel() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"LogDescriptionPanel", "makePanel");
		trace_methods.beginningOfMethod();
		
		// On présente le panneau au moyen d'une phrase descriptive
		JLabel descriptionSentence = new JLabel();
		descriptionSentence.setMinimumSize(new Dimension(476, 20));
		descriptionSentence.setMaximumSize(new Dimension(476, 20));
		descriptionSentence.setText(
				MessageManager.getMessage("&ComponentConfiguration_FirstDescriptionSentenceLog"));
		
		_tableModel = null;
		_logTable = new JTable(_tableModel);
		_logTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_logTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});
		// On intégre un défilement dans le tableau
		JScrollPane scrollPaneConfigTable = new JScrollPane();
		scrollPaneConfigTable.setViewportView(_logTable);
		scrollPaneConfigTable.setMinimumSize(new Dimension(476, 143));
		scrollPaneConfigTable.setMaximumSize(new Dimension(476, 143));
		
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
	    panel1.add(descriptionSentence, c1);
	    
	    c1.ipady = 10;
	    c1.gridx = 0;
	    c1.gridy = 1;
	    c1.gridwidth = 1;
	    c1.gridheight = 1;
	    panel1.add(scrollPaneConfigTable, c1);
		
	    // On définit un label pour le champs "Nom"
	    JLabel  nameLabel = new JLabel();
		nameLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_Name") 
				+ " :   *");
		// On définit un label pour le champs "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_Description") 
				+ " : ");
		// On définit un label pour le champs "Root"
		JLabel rootLabel = new JLabel();
		rootLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_Root")
				+ " :   *");
		// On définit un label pour le champs "Pattern"
		JLabel patternLabel = new JLabel();
		patternLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_Pattern")
				+ " :   *");
		
		int maxSize = nameLabel.getPreferredSize().width;
		if ( descriptionLabel.getPreferredSize().width > maxSize )
			maxSize = descriptionLabel.getPreferredSize().width;
		if ( rootLabel.getPreferredSize().width > maxSize )
			maxSize = rootLabel.getPreferredSize().width;
		if ( patternLabel.getPreferredSize().width > maxSize )
			maxSize = patternLabel.getPreferredSize().width;
		
		nameLabel.setPreferredSize(new Dimension(maxSize,20));
		nameLabel.setMinimumSize(new Dimension(maxSize,20));
		nameLabel.setMaximumSize(new Dimension(maxSize,20));
		descriptionLabel.setPreferredSize(new Dimension(maxSize,20));
		descriptionLabel.setMinimumSize(new Dimension(maxSize,20));
		descriptionLabel.setMaximumSize(new Dimension(maxSize,20));
		rootLabel.setPreferredSize(new Dimension(maxSize,20));
		rootLabel.setMinimumSize(new Dimension(maxSize,20));
		rootLabel.setMaximumSize(new Dimension(maxSize,20));
		patternLabel.setPreferredSize(new Dimension(maxSize,20));
		patternLabel.setMinimumSize(new Dimension(maxSize,20));
		patternLabel.setMaximumSize(new Dimension(maxSize,20));
		
		_nameTextField = new JTextField();
		setEnterCallback(_nameTextField);
		_nameTextField.setPreferredSize(new Dimension(
				440 - (nameLabel.getPreferredSize().width), 20));
		_nameTextField.setMinimumSize(new Dimension(
				440 - (nameLabel.getMinimumSize().width), 20));
		_nameTextField.setMaximumSize(new Dimension(
				440 - (nameLabel.getMaximumSize().width), 20));
		
		_descriptionTextField = new JTextField();
		setEnterCallback(_descriptionTextField);
		_descriptionTextField.setPreferredSize(new Dimension(
				440 - (descriptionLabel.getPreferredSize().width), 20));
		_descriptionTextField.setMinimumSize(new Dimension(
				440 - (descriptionLabel.getMinimumSize().width), 20));
		_descriptionTextField.setMaximumSize(new Dimension(
				440 - (descriptionLabel.getMaximumSize().width), 20));
		
		_rootTextField = new JTextField();
		setEnterCallback(_rootTextField);
		_rootTextField.setPreferredSize(new Dimension(
				440 - (rootLabel.getPreferredSize().width), 20));
		_rootTextField.setMinimumSize(new Dimension(
				440 - (rootLabel.getMinimumSize().width), 20));
		_rootTextField.setMaximumSize(new Dimension(
				440 - (rootLabel.getMaximumSize().width), 20));
		
		_patternTextField = new JTextField();
		setEnterCallback(_patternTextField);
		_patternTextField.setPreferredSize(new Dimension(
				440 - (patternLabel.getPreferredSize().width), 20));
		_patternTextField.setMinimumSize(new Dimension(
				440 - (patternLabel.getMinimumSize().width), 20));
		_patternTextField.setMaximumSize(new Dimension(
				440 - (patternLabel.getMaximumSize().width), 20));
		

		_removeButton = new JButton();
		_removeButton.setText(MessageManager.getMessage(
				"&ComponentConfiguration_RemoveButton"));
		_removeButton.setMinimumSize(new Dimension(120,22));
		_removeButton.setMaximumSize(new Dimension(120,22));
		_removeButton.setPreferredSize(new Dimension(120,22));
		_removeButton.setEnabled(false);
		_removeButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt){
				onClickRemoveButton();
			}
		});
		
		_applyButton = new JButton();
		_applyButton.setText(MessageManager.getMessage(
				"&ComponentConfiguration_ApplyButton"));
		_applyButton.setMinimumSize(new Dimension(120,22));
		_applyButton.setMaximumSize(new Dimension(120,22));
		_applyButton.setPreferredSize(new Dimension(120,22));
		_applyButton.setEnabled(false);
		_applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt){
				onClickApplyButton();
			}
		});
		
		_newButton = new JButton();;
		_newButton.setText(MessageManager.getMessage(
				"&ComponentConfiguration_NewButton"));
		_newButton.setMinimumSize(new Dimension(120,22));
		_newButton.setMaximumSize(new Dimension(120,22));
		_newButton.setPreferredSize(new Dimension(120,22));
		_newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt){
				onClickNewButton();
			}
		});
		
		JLabel obligatoryField = new JLabel();
		obligatoryField.setText(
				MessageManager.getMessage("&ComponentConfiguration_ObligatoryFields"));
		obligatoryField.setMinimumSize(new Dimension(150,20));
		obligatoryField.setMaximumSize(new Dimension(150,20));

		JPanel panel2 = new JPanel();
		panel2.setMinimumSize(new Dimension(480,210));
		panel2.setMaximumSize(new Dimension(480,210));
		panel2.setPreferredSize(new Dimension(480,210));
		panel2.setBorder(BorderFactory.createTitledBorder((
				MessageManager.getMessage("&ComponentConfiguration_SecondDescriptionSentence"))));
		
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
	    panel2.add(descriptionLabel, c);
		
	    c.gridx = 1;
	    c.gridy = 1;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(_descriptionTextField, c);
		
	    c.gridx = 0;
	    c.gridy = 2;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(rootLabel, c);
		
	    c.gridx = 1;
	    c.gridy = 2;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(_rootTextField, c);

	    c.gridx = 0;
	    c.gridy = 3;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(patternLabel, c);
		
	    c.gridx = 1;
	    c.gridy = 3;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(_patternTextField, c);
	    
	    Box hBox1 = Box.createHorizontalBox();
	    hBox1.add(_newButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_removeButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_applyButton);
		hBox1.setMaximumSize(new Dimension(380,22));
		
	    c.gridx = 0;
	    c.gridy = 4;
	    c.gridwidth = 2;
	    c.gridheight = 1;
	    c.ipady = 0;
	    c.anchor = GridBagConstraints.LINE_END;
	    panel2.add(hBox1, c);

	    c.gridx = 0;
	    c.gridy = 5;
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
	private void onClickApplyButton() {
		// On souhaite enregistrer les données saisies
		// On récupère les champs
		String logName = _nameTextField.getText();
		String logDescription = _descriptionTextField.getText();
		String logRoot = _rootTextField.getText();
		String logPattern = _patternTextField.getText();
		
		// Si un champ obligatoire n'est pas rempli, erreur
		if ( logName.equals("") || logRoot.equals("") 
				|| logPattern.equals("")) {
				DialogManager.displayDialog("Information", MessageManager
						.getMessage("&ComponentConfiguration_MessageInformation"),
						null, null);
		}
		else {
			// Dans un premier temps, on va rechercher la ligne du 
			// tableau associée au nom du fichier si il existe
			int row = _logTable.getSelectedRow();
			if ( row == -1 ) {
				
				if (_tableModel.findRow(logName, logRoot, logPattern) == -1) {
				
					char flag = 'A';
					ComponentLog cl = new ComponentLog();
					
					ArrayList<ComponentLog> deleteElement = 
						_tableModel.getDeleteDatas();
					if (deleteElement != null) {
						for (int index = 0 ; index < deleteElement.size(); index ++) {
							
							if (deleteElement.get(index).getLogName().equals(logName)
									&& deleteElement.get(index).getRoot().equals(logRoot)
									&& deleteElement.get(index).getPattern().equals(logPattern)) {
								flag = 'M';
								cl = deleteElement.get(index);
								deleteElement.remove(index);
								break;
							}
						}
					}
					
					cl.setLogName(logName);
					cl.setNewDescription(logDescription);
					cl.setRoot(logRoot);
					cl.setPattern(logPattern);
					cl.setFlag(flag);
					_tableModel.addElement(cl);
					
					// On efface les champs de saisies
					_nameTextField.setText("");
					_nameTextField.setEnabled(true);
					_descriptionTextField.setText("");
					_rootTextField.setText("");		
					_rootTextField.setEnabled(true);
					_patternTextField.setText("");
					_patternTextField.setEnabled(true);
					_logTable.repaint();
					_logTable.getSelectionModel().clearSelection();
					
					_removeButton.setEnabled(false);
					_applyButton.setEnabled(false);
				}
				else {
						DialogManager.displayDialog("Information", MessageManager
								.getMessage("&ComponentConfiguration_MessageInformationLogDescription"),
								null, null);
				}
			}
			else {
				// Si le fichier existait déjà dans le tableau, c'est 
				// une modification ; on modifie les champs
				ComponentLog cl = _tableModel.getElementAt(row);
				cl.setNewDescription(logDescription);
				if (cl.getFlag() == 'E')
					cl.setFlag('M');
				_tableModel.fireTableRowsUpdated(row, row);
				
				// On efface les champs de saisies
				_nameTextField.setText("");
				_nameTextField.setEnabled(true);
				_descriptionTextField.setText("");
				_rootTextField.setText("");		
				_rootTextField.setEnabled(true);
				_patternTextField.setText("");
				_patternTextField.setEnabled(true);
				_logTable.repaint();
				_logTable.getSelectionModel().clearSelection();
				
				_removeButton.setEnabled(false);
				_applyButton.setEnabled(false);
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
		// On souhaite effacer le contenu sélectionné
		// On récupère la ligne sélectionnée du tableau si il y 
		// en a une.
		int row = _logTable.getSelectedRow();
		
		if ( row != -1 ) {
			
			ComponentLog cc = _tableModel.getElementAt(row);
			if (cc != null ) {
				if ( cc.getFlag() == 'A' ) 
					_tableModel.removeElement(row);
				else if ( cc.getFlag() == 'E' || cc.getFlag() == 'M' ) {
					_tableModel.removeElement(row);
					cc.setFlag('S');
					_tableModel.addDeleteElement(cc);
				}
				else {
					// A gros problème
				}
			
			}
			_tableModel.fireTableRowsDeleted(row, row);
		}
		

		// On efface les champs de saisies
		_nameTextField.setText("");
		_nameTextField.setEnabled(true);
		_descriptionTextField.setText("");
		_rootTextField.setText("");
		_rootTextField.setEnabled(true);
		_patternTextField.setText("");
		_patternTextField.setEnabled(true);
		_logTable.getSelectionModel().clearSelection();
		
		_removeButton.setEnabled(false);
		_applyButton.setEnabled(false);
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
		_nameTextField.setText("");
		_nameTextField.setEnabled(true);
		_descriptionTextField.setText("");
		_rootTextField.setText("");
		_rootTextField.setEnabled(true);
		_patternTextField.setText("");
		_patternTextField.setEnabled(true);
		_logTable.getSelectionModel().clearSelection();
		
		_removeButton.setEnabled(false);
		_applyButton.setEnabled(false);
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
		ListSelectionModel lsm = 
			(ListSelectionModel)event.getSource();
		
		int index = lsm.getMinSelectionIndex();
		if (index == -1) {
			_nameTextField.setText("");
			_nameTextField.setEnabled(true);
			_descriptionTextField.setText("");
			_rootTextField.setText("");
			_rootTextField.setEnabled(true);
			_patternTextField.setText("");
			_patternTextField.setEnabled(true);
			
			_removeButton.setEnabled(false);
			_applyButton.setEnabled(false);
		}
		else {
			ComponentLog cl = _tableModel.getElementAt(index);
			_nameTextField.setText(cl.getLogName());
			_rootTextField.setText(cl.getRoot());
			_patternTextField.setText(cl.getPattern());
			if (cl.getFlag() == 'E')
				_descriptionTextField.setText(cl.getDescription());
			else
				_descriptionTextField.setText(cl.getNewDescription());
			_nameTextField.setEnabled(false);
			_rootTextField.setEnabled(false);
			_patternTextField.setEnabled(false);

			
			_removeButton.setEnabled(true);
			_applyButton.setEnabled(true);
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
	private void fieldsHaveChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LogDescriptionPanel", "fieldsHaveChanged");

		trace_methods.beginningOfMethod();
		// Vérification du contenu du champ Nom
		if(_nameTextField.getText().equals("") 
				|| _rootTextField.getText().equals("") 
				|| _patternTextField.getText().equals(""))
		{
			// Aucune valeur n'est sélectionnée
			_applyButton.setEnabled(false);
			trace_methods.endOfMethod();
			return;
		}
		// Ok, on peut valider le bouton
		_applyButton.setEnabled(true);
		trace_methods.endOfMethod();
	}
}
