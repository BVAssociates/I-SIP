/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
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
// D�claration du package
package com.bv.isis.console.impl.processor.config.implementation.view;

// 
// Imports syst�me
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
 * Cette classe impl�mente le panneau qui permet de d�finir 
 * les fichiers de log d'un composant I-SIS.
 * ------------------------------------------------------------*/
public class LogDescriptionPanel extends AbstractPanel {

	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: LogDescriptionPanel
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * Elle appelle la m�thode makePanel() charg�e de la cr�ation des objets
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
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle est appel�e par la classe AbstractWindow lorsque le panneau doit 
	 * �tre affich�. A partir du mod�le de donn�es pass� en param�tre, elle 
	 * se charge de pr�-renseigner les champs du panneau.
	 * 
	 * Param�tres :
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
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
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
	 * Cette m�thode d�finit le comportement du panneau avant d'�tre cach�.
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Elle est en charge de v�rifier la validit� et la conformit� des 
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
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Cette m�thode d�finit le comportement du panneau apr�s avoir �t� cach�.
	 * Elle n'est pr�sent�e que pour des raisons de lisibilit�.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
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
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Cette m�thode est appel�e lors de la destruction de l'assistant. Elle est 
	 * utiliser pour lib�rer l'espace m�moire utilis� par les variables des
	 * classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
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
					// l'�l�ment d�cal�
					index --;
				}
			}
		}
		else
			// Le tableau �tait inexistant, on le cr�e
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
	* Cette m�thode permet de d�finir la m�thode de callback en
	* cas d'appui sur la touche "Entr�e" sur un objet JComponent
	* pass� en argument.
	* La m�thode de callback est la m�thode onClickApplyButton().
	*
	* Arguments:
	*  - component: Un objet JComponent sur lequel le callback
	*    doit �tre d�fini.
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
	
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _logTable
	 * 
	 * Description :
	 * Cette attribut repr�sente un tableau r�capitulant l'ensemble
	 * des fichier de logs d�finis par l'utilisateur.
	 * ------------------------------------------------------------*/
	private JTable _logTable;
	
	/*------------------------------------------------------------
	 * Nom : _textName
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie du nom de la variable
	 * ------------------------------------------------------------*/
	protected JTextField _nameTextField;
	
	/*------------------------------------------------------------
	 * Nom : _descriptionTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour la description
	 * d'un fichier de log dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _descriptionTextField;
	
	/*------------------------------------------------------------
	 * Nom : _rootTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour la racine
	 * d'un fichier de log dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _rootTextField;
	
	/*------------------------------------------------------------
	 * Nom : _patternTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour le mod�le
	 * d'un fichier de log dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _patternTextField;
	
	/*------------------------------------------------------------
	 * Nom : _newButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * permettre une nouvelle saisie.
	 * ------------------------------------------------------------*/
	private JButton _newButton;
	
	/*------------------------------------------------------------
	 * Nom : _removeButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * effacer la ligne s�lectionner dans le tableau des fichiers.
	 * ------------------------------------------------------------*/
	private JButton _removeButton;
	
	/*------------------------------------------------------------
	 * Nom : _applyButton
	 * 
	 * Description :
	 * Cette attribut d�finit un boutton. Celui-ci est utilis� pour
	 * enregistrer les informations saisies dans le sous panneau de 
	 * saisie, dans le tableau des fichiers.
	 * ------------------------------------------------------------*/
	private JButton _applyButton;

	/*------------------------------------------------------------
	 * Nom : LogTableModel
	 * 
	 * Description :
	 * Cette classe repr�sente le modele associ� � la JTable.
	 * Elle d�rive de AbstractTableModel.
	 * Elle se caract�rise de 2 tableaux:
	 *  - un pour les titres des diverses colonnes
	 *  - un pour les donn�es de chaque ligne.
	 * Chaque ligne est une instance de ComponentLog.
	 * ------------------------------------------------------------*/
	private class LogTableModel extends AbstractTableModel {
		
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
		private ArrayList<ComponentLog> _datas;
		
		private ArrayList<ComponentLog> _deleteDatas;
		
		
		/*------------------------------------------------------------
		 * Nom : ConfigTableModel
		 * 
		 * Description :
		 * Cette m�thode est le contructeur de la classe.
		 * Son r�le est de cr�er les deux tableaux de titres et de donn�es.
		 * Pour le tableau des titres, elle le remplira � partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/		
		public LogTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"LogTableModel", "LogTableModel");
			trace_methods.beginningOfMethod();
			
			// Cr�ation du tableau des titres
			_titles = new String [4];
			// La premi�re colonne repr�sentera le nom du fichier
			_titles[0] = 
				MessageManager.getMessage("&ComponentConfiguration_Name");
			// La deuxi�me colonne repr�sentera la description associ�e
			// au fichier
			_titles[1] = 
				MessageManager.getMessage("&ComponentConfiguration_Description");
			// La troisi�me colonne repr�sentera la racine du fichier
			_titles[2] = 
				MessageManager.getMessage("&ComponentConfiguration_Root");
			// La troisi�me colonne repr�sentera le mod�le du fichier
			_titles[3] = 
				MessageManager.getMessage("&ComponentConfiguration_Pattern");
			
			// Cr�ation du tableau de donn�es
			_datas = new ArrayList<ComponentLog>();
			_deleteDatas = new ArrayList<ComponentLog>();
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
 		 * Cette m�thode retourne le nombre de lignes de la JTable. 
 		 * Ce nombre est donn� par la dimmension du tableaux des donn�es.
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
 		 * Cette m�thode retourne le tableau de ComponentLog contenant
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
 		 * Cette m�thode recherche la pr�sence d'un fichier dans la JTable
 		 * � partir du nom de fichier pass� en param�tre, de sa racine et 
 		 * du mod�le saisient. Elle retourne le num�ro de la ligne correspondant 
 		 * � ce fichier si elle existe, -1 sinon.
 		 * 
 		 * Argument :
 		 *  - logName : une cha�ne de caract�res correspondant au nom
 		 *    du fichier recherch�
 		 *  - logRoot : un cha�ne de caract�res correspondant � la racine
 		 *    du fichier
 		 *  - logPattern : un cha�ne de caract�res correspondant au mod�le
 		 *    du fichier
 		 *    
 		 * Retourne : un entier : le num�ro de ligne du fichier, -1 si 
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
 		 * Cette m�thode retourne la valeur de la cellule de la JTable
 		 * pour la position (ligne, colonne) donn�e.
 		 * Le num�ro de ligne correspond � un fichier particulier.
 		 * Le num�ro de colonne indique le champ : 
 		 * 	- 0 pour le nom du fichier
 		 *  - 1 pour sa description
 		 *  - 2 pour sa racine
 		 *  - 3 pour son mod�le
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
 		 * Cette m�thode retourne le nom d'une colonne de la JTable. 
 		 * 
 		 * Argument :
 		 *  - column : la colonne dont on veut le nom
 		 * 
 		 * Retourne : une chaine de caract�re : le nom de la colonne.
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
 		 * Cette m�thode ajoute un nouvel �l�ment (un fichier) au 
 		 * tableau de donn�es. 
 		 * 
 		 * Argument : 
 		 *  - element : L'�l�ment � ajouter
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
 		 * Cette m�thode supprime un �l�ment (un fichier) au tableau 
 		 * de donn�es. 
 		 * 
 		 * Argument : 
 		 *  - line : Le num�ro de ligne de l'�l�ment � supprimer
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
 		 * Cette m�thode retourne le ComponentLog de la ligne demand�e. 
 		 * 
 		 * Argument : 
 		 *  - line : Le num�ro de ligne de l'�l�ment � r�cup�rer
 		 *  
 		 * Retourne : Le ComponentLog de la ligne s�lectionn�e.
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
 		 * Cette m�thode ajoute au tableau des �l�ments � supprimer de la
 		 * base, le ComponentLog pass� en param�tre. 
 		 * 
 		 * Argument : 
 		 *  - config : Le ComponentLog qu'il faudra supprimer de la base
 		 *    de donn�es.
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
 		 * Cette m�thode retire au tableau des �l�ments � supprimer de la
 		 * base, le ComponentLog d'index �gal � l'index pass� en param�tre. 
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
 		 * Cette m�thode retourne du tableau des �l�ments � supprimer de la
 		 * base, le ComponentLog d'index �gal � l'index pass� en param�tre. 
 		 * 
 		 * Argument : 
 		 *  - index : L'index du ComponentLog qu'il faut r�cup�rer dans le
 		 *    tableau.
 		 * 
 		 * Retourne : Le ComponentLog supprim� de la ligne souhait�e
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
 		 * Cette m�thode retourne l'ensemble du tableau des ComponentLog
 		 * � supprimer de la base de donn�es. 
 		 * 
 		 * Retourne : Le tableau des ComponentLog � supprimer.
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
	 * Cette attribut maintient une r�f�rence sur le mod�le associ� �
	 * la JTable.
	 * ------------------------------------------------------------*/
	private LogTableModel _tableModel;
	
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
				"LogDescriptionPanel", "makePanel");
		trace_methods.beginningOfMethod();
		
		// On pr�sente le panneau au moyen d'une phrase descriptive
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
		// On int�gre un d�filement dans le tableau
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
		
	    // On d�finit un label pour le champs "Nom"
	    JLabel  nameLabel = new JLabel();
		nameLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_Name") 
				+ " :   *");
		// On d�finit un label pour le champs "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_Description") 
				+ " : ");
		// On d�finit un label pour le champs "Root"
		JLabel rootLabel = new JLabel();
		rootLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_Root")
				+ " :   *");
		// On d�finit un label pour le champs "Pattern"
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
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * le bouton 'Appliquer' de la zone de saisie. Son r�le est de 
	 * contr�ler les informations saisies par l'utilisateur et de les
	 * enregistrer dans la table si tout est correcte.
	 *------------------------------------------------------------*/
	private void onClickApplyButton() {
		// On souhaite enregistrer les donn�es saisies
		// On r�cup�re les champs
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
			// tableau associ�e au nom du fichier si il existe
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
				// Si le fichier existait d�j� dans le tableau, c'est 
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
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * le bouton 'Effacer' de la zone de saisie. Son r�le est de 
	 * supprimer la ligne s�lectionn�e du tableau.
	 *------------------------------------------------------------*/
	private void onClickRemoveButton() {
		// On souhaite effacer le contenu s�lectionn�
		// On r�cup�re la ligne s�lectionn�e du tableau si il y 
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
					// A gros probl�me
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
	 * Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur 
	 * une ligne du tableau. Son r�le est de pr� remplir les champs
	 * de saisie pour permettre la modification de ceux-ci si ils ne
	 * font pas partit de la cl� primaire.
	 * Cette m�thode est �galement appel�e en cas de d�selection d'une
	 * ligne dans la table, elle efface alors la s�lection et le 
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
	* Cette m�thode est appel�e lorsque les champs ont chang�s dans 
	* la zone de saisie des donn�es.
	* Elle permet de mettre � jour l'�tat du bouton "Appliquer" en 
	* fonction de la pr�sence ou non des informations.
	* ----------------------------------------------------------*/
	private void fieldsHaveChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"LogDescriptionPanel", "fieldsHaveChanged");

		trace_methods.beginningOfMethod();
		// V�rification du contenu du champ Nom
		if(_nameTextField.getText().equals("") 
				|| _rootTextField.getText().equals("") 
				|| _patternTextField.getText().equals(""))
		{
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
