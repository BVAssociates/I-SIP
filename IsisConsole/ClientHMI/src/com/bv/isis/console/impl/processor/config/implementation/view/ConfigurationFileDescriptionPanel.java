/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/view/ConfigurationFileDescriptionPanel.java,v $
* $Revision: 1.16 $
*
* ------------------------------------------------------------
* DESCRIPTION: Panneau de saisie des fichiers de configuration
* DATE:        30/06/2008
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
// Imports Syst�me
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
// Imports du projet
//
import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.common.DialogManager;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentConfig;

/*------------------------------------------------------------
 * Nom: ConfigurationDescriptionPanel
 * 
 * Description: 
 * Cette classe impl�mente le panneau qui permet de d�finir 
 * les fichiers de configuration d'un composant I-SIS.
 * ------------------------------------------------------------*/
public class ConfigurationFileDescriptionPanel extends AbstractPanel {

	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: ConfigurationFileDescriptionPanel
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * Elle appelle la m�thode makePanel() charg�e de la cr�ation des objets
	 * graphiques a afficher dans le panneau.		
	 * ------------------------------------------------------------*/
	public ConfigurationFileDescriptionPanel(WindowInterface window) {
		super();
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ConfigurationFileDescriptionPanel", 
				"ConfigurationFileDescriptionPanel");
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
				"ConfigurationFileDescriptionPanel", "beforeDisplay");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("modele=" + modele);
		
		_tableModel = new ConfigTableModel();
				
		if (modele != null) {
			for ( int i=0 ; i < modele.size() ; i++) {
				if ( modele.get(i) instanceof ComponentConfig ) {
					ComponentConfig c = (ComponentConfig) modele.get(i);
					if (c.getFlag() != 'S')
						_tableModel.addElement(c);
					else
						_tableModel.addDeleteElement(c);
				}
			}
		}
		_configTable.setModel( _tableModel );	
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
				"ConfigurationFileDescriptionPanel", "afterDisplay");
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
				"ConfigurationFileDescriptionPanel", "beforeHide");
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
				"ConfigurationFileDescriptionPanel", "afterHide");
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
				"ConfigurationFileDescriptionPanel", "end");
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
				"ConfigurationFileDescriptionPanel", "update");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tabModels=" + tabModels);
		
		ArrayList<ComponentConfig> deletedElements = _tableModel.getDeleteDatas();
		ArrayList<ComponentConfig> newElements = _tableModel.getDatas();
		
		if (tabModels != null) {
			
			// On supprime tous les anciens fichier de configuration
			for (int index=0 ; index < tabModels.size() ; index++) {
				if (tabModels.get(index) instanceof ComponentConfig) {
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
	
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _descriptionSentence
	 * 
	 * Description :
	 * Cette attribut est utilis� pour �crire un phrase descriptive
	 * en haut du panneau.
	 * ------------------------------------------------------------*/
	private JLabel _firstDescriptionSentence;
	
	/*------------------------------------------------------------
	 * Nom : _configTable
	 * 
	 * Description :
	 * Cette attribut repr�sente un tableau r�capitulant l'ensemble
	 * des fichier de configuration d�finis par l'utilisateur.
	 * ------------------------------------------------------------*/
	private JTable _configTable;
	
	/*------------------------------------------------------------
	 * Nom : _nameTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour le nom d'un
	 * fichier de configuration dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _nameTextField;
	
	/*------------------------------------------------------------
	 * Nom : _descriptionTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour la description
	 * d'un fichier de configuration dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _descriptionTextField;
	
	/*------------------------------------------------------------
	 * Nom : _pathTextField
	 * 
	 * Description :
	 * Cette attribut d�finit un champ de saisie pour le chemin absolu
	 * d'un fichier de configuration dans le sous panneau de saisie.
	 * ------------------------------------------------------------*/
	private JTextField _pathTextField;
	
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
	 * Nom : ConfigTableModel
	 * 
	 * Description :
	 * Cette classe repr�sente le modele associ� � la JTable.
	 * Elle d�rive de AbstractTableModel.
	 * Elle se caract�rise de 2 tableaux:
	 *  - un pour les titres des diverses colonnes
	 *  - un pour les donn�es de chaque ligne.
	 * Chaque ligne est une instance de ComponentConfig.
	 * ------------------------------------------------------------*/
	private class ConfigTableModel extends AbstractTableModel {
		
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
		private ArrayList<ComponentConfig> _datas;
		
		private ArrayList<ComponentConfig> _deleteDatas;
		
		/*------------------------------------------------------------
		 * Nom : ConfigTableModel
		 * 
		 * Description :
		 * Cette m�thode est le contructeur de la classe.
		 * Son r�le est de cr�er les deux tableaux de titres et de donn�es.
		 * Pour le tableau des titres, elle le remplira � partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/		
		public ConfigTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "ConfigTableModel");
			trace_methods.beginningOfMethod();
			
			// Cr�ation du tableau des titres
			_titles = new String [3];
			// La premi�re colonne repr�sentera le nom du fichier
			_titles[0] = 
				MessageManager.getMessage("&ComponentConfiguration_Name");
			// La deuxi�me colonne repr�sentera la description associ�e
			// au fichier
			_titles[1] = 
				MessageManager.getMessage("&ComponentConfiguration_Description");
			// La troisi�me colonne repr�sentera le chemin absolu du fichier
			_titles[2] = 
				MessageManager.getMessage("&ComponentConfiguration_AbsolutePath");
			
			// Cr�ation du tableau de donn�es
			_datas = new ArrayList<ComponentConfig>();
			_deleteDatas = new ArrayList<ComponentConfig>();
			
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
		public ArrayList<ComponentConfig> getDatas() {
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
		public int findRow(String fileName, String filePath) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("fileName=" + fileName);
			trace_arguments.writeTrace("path=" + filePath);

			if (_datas != null)
			{
				for (int index=0 ; index < _datas.size() ; index++) {
					ComponentConfig cc = _datas.get(index);
					if (cc != null && cc.getFileName() != null 
							&& cc.getAbsolutePath() != null) {
						
						String name = cc.getFileName();
						String path = cc.getAbsolutePath();
						
						if (name.equals(fileName) && path.equals(filePath)){
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
				ComponentConfig cc = _datas.get(line);
				if (cc != null) {
					if (column == 0) {
						trace_methods.endOfMethod();
						return cc.getFileName();
					}
					else if (column == 1) {
						if (cc.getFlag() == 'E') {
							trace_methods.endOfMethod();
							return cc.getDescription();
						}
						else {
							trace_methods.endOfMethod();
							return cc.getNewDescription();
						}
					}
					else if (column == 2) {
						trace_methods.endOfMethod();
						return cc.getAbsolutePath();
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
		public void addElement(ComponentConfig element){
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
		public ComponentConfig getElementAt(int line) {
			
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
		public void addDeleteElement(ComponentConfig config) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "addDeleteElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("config=" + config);
			
			if (_deleteDatas != null)
				_deleteDatas.add(config);
			
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
		public ComponentConfig getDeleteElementAt(int index) {
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
		public ArrayList<ComponentConfig> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ConfigTableModel", "getDeleteDatas");
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
	private ConfigTableModel _tableModel;
	
	/*------------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette m�thode est appel� lors de la construction du panneau.
	 * Son r�le est d'instancier les composants graphiques du panneau et de les
	 * positionner.
	 * ------------------------------------------------------------*/
	private void makePanel(){
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ConfigurationFileDescriptionPanel", "makePanel");
		trace_methods.beginningOfMethod();
		
		_tableModel = null;
		
		// On pr�sente le panneau au moyen d'une phrase descriptive
		_firstDescriptionSentence = new JLabel();
		_firstDescriptionSentence.setMinimumSize(new Dimension(476, 20));
		_firstDescriptionSentence.setMaximumSize(new Dimension(476, 20));
		_firstDescriptionSentence.setText(
				MessageManager.getMessage("&ComponentConfiguration_FirstDescriptionSentenceConfigurationFile"));
		
		_configTable = new JTable(_tableModel);
		_configTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_configTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});
		
		// On int�gre un d�filement dans le tableau
		JScrollPane scrollPaneConfigTable = new JScrollPane();
		scrollPaneConfigTable.setViewportView(_configTable);
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
	    panel1.add(_firstDescriptionSentence, c1);
	    
	    c1.ipady = 10;
	    c1.gridx = 0;
	    c1.gridy = 1;
	    c1.gridwidth = 1;
	    c1.gridheight = 1;
	    panel1.add(scrollPaneConfigTable, c1);
		
		// On d�finit un label pour le champs "Nom"
	    JLabel nameLabel = new JLabel();
		nameLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_Name") 
				+ " :   *");
		// On d�finit un label pour le champs "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_Description") 
				+ " : ");
		// On d�finit un label pour le champs "Chemin absolu"
		JLabel pathLabel = new JLabel();
		pathLabel.setText(
				MessageManager.getMessage("&ComponentConfiguration_AbsolutePath")
				+ " :   *");
		
		// champs sont obligatoires pour la saisie
		JLabel obligatoryField = new JLabel();
		obligatoryField.setText(MessageManager.getMessage("&ComponentConfiguration_ObligatoryFields"));
		obligatoryField.setMinimumSize(new Dimension(150,20));
		obligatoryField.setMaximumSize(new Dimension(150,20));
				
		int maxSize = nameLabel.getPreferredSize().width;
		if ( descriptionLabel.getPreferredSize().width > maxSize )
			maxSize = descriptionLabel.getPreferredSize().width;
		if ( pathLabel.getPreferredSize().width > maxSize )
			maxSize = pathLabel.getPreferredSize().width;
		
		nameLabel.setPreferredSize(new Dimension(maxSize,20));
		nameLabel.setMinimumSize(new Dimension(maxSize,20));
		nameLabel.setMaximumSize(new Dimension(maxSize,20));
		descriptionLabel.setPreferredSize(new Dimension(maxSize,20));
		descriptionLabel.setMinimumSize(new Dimension(maxSize,20));
		descriptionLabel.setMaximumSize(new Dimension(maxSize,20));
		pathLabel.setPreferredSize(new Dimension(maxSize,20));
		pathLabel.setMinimumSize(new Dimension(maxSize,20));
		pathLabel.setMaximumSize(new Dimension(maxSize,20));
		
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

		_pathTextField = new JTextField();
		setEnterCallback(_pathTextField);
		_pathTextField.setPreferredSize(new Dimension(
				440 - (pathLabel.getPreferredSize().width), 20));
		_pathTextField.setMinimumSize(new Dimension(
				440 - (pathLabel.getMinimumSize().width), 20));
		_pathTextField.setMaximumSize(new Dimension(
				440 - (pathLabel.getMaximumSize().width), 20));
		
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
		
		_newButton = new JButton();
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
		
		JPanel panel2 = new JPanel();
		panel2.setMinimumSize(new Dimension(480,180));
		panel2.setMaximumSize(new Dimension(480,180));
		panel2.setPreferredSize(new Dimension(480,180));
		panel2.setBorder(BorderFactory.createTitledBorder(MessageManager.getMessage("&ComponentConfiguration_SecondDescriptionSentence")));
		
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
	    panel2.add(pathLabel, c);
		
	    c.gridx = 1;
	    c.gridy = 2;
	    c.gridwidth = 1;
	    c.gridheight = 1;
	    panel2.add(_pathTextField, c);

	    Box hBox1 = Box.createHorizontalBox();
	    hBox1.add(_newButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_removeButton);
		hBox1.add(Box.createHorizontalStrut(10));
		hBox1.add(_applyButton);
		hBox1.setMaximumSize(new Dimension(380,22));
		
	    c.gridx = 0;
	    c.gridy = 3;
	    c.gridwidth = 2;
	    c.gridheight = 1;
	    c.ipady = 15;
	    c.anchor = GridBagConstraints.LINE_END;
	    panel2.add(hBox1, c);

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
		int row = _configTable.getSelectedRow();
		
		if ( row != -1 ) {
			ComponentConfig cc = _tableModel.getElementAt(row);
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
		_pathTextField.setText("");
		_pathTextField.setEnabled(true);
		_configTable.getSelectionModel().clearSelection();
		
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
		String fileName = _nameTextField.getText();
		String fileDescription = _descriptionTextField.getText();
		String filePath = _pathTextField.getText();
		
		// Si un champ obligatoire n'est pas rempli, erreur
		if ( fileName.equals("") || filePath.equals("") ) {
			DialogManager.displayDialog("Information", MessageManager
					.getMessage("&ComponentConfiguration_MessageInformation"),
					null, null);
		}
		else {
			// Dans un premier temps, on va rechercher la ligne du 
			// tableau associ�e au nom du fichier si il existe
			int row = _configTable.getSelectedRow();
			
			if ( row == -1 ) {
				// Si celui-ci n'�tait pas pr�sent dans le tableau,
				// c'est donc un nouveau fichier ; on le cr�e et on 
				// l'ajoute
				if ( _tableModel.findRow(fileName, filePath) == -1) {
					char flag = 'A';
					ComponentConfig cc = new ComponentConfig();
					
					ArrayList<ComponentConfig> deleteElement = 
						_tableModel.getDeleteDatas();
					if (deleteElement != null) {
						for (int index = 0 ; index < deleteElement.size(); index ++) {
							
							if (deleteElement.get(index).getFileName().equals(fileName)
									&& deleteElement.get(index).getAbsolutePath().equals(filePath)) {
								flag = 'M';
								cc = deleteElement.get(index);
								deleteElement.remove(index);
								break;
							}
						}
					}
					
					cc.setFileName(fileName);
					cc.setNewDescription(fileDescription);
					cc.setAbsolutePath(filePath);
					cc.setFlag(flag);
					_tableModel.addElement(cc);
					
					// On efface les champs de saisies
					_nameTextField.setText("");
					_nameTextField.setEnabled(true);
					_descriptionTextField.setText("");
					_pathTextField.setText("");		
					_pathTextField.setEnabled(true);
					_configTable.repaint();
					_configTable.getSelectionModel().clearSelection();
					
					_removeButton.setEnabled(false);
					_applyButton.setEnabled(false);
				}
				else {
					DialogManager.displayDialog("Information", MessageManager
							.getMessage("&ComponentConfiguration_MessageInformationLofDescription"),
							null, null);
				}
			}
			else {
				ComponentConfig cc = _tableModel.getElementAt(row);
				cc.setNewDescription(fileDescription);
				if (cc.getFlag() == 'E')
					cc.setFlag('M');
					
				_tableModel.fireTableRowsUpdated(row, row);
				
				// On efface les champs de saisies
				_nameTextField.setText("");
				_nameTextField.setEnabled(true);
				_descriptionTextField.setText("");
				_pathTextField.setText("");		
				_pathTextField.setEnabled(true);
				_configTable.repaint();
				_configTable.getSelectionModel().clearSelection();
				
				_removeButton.setEnabled(false);
				_applyButton.setEnabled(false);
			}
		}
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
		_pathTextField.setText("");		
		_pathTextField.setEnabled(true);
		_configTable.repaint();
		_configTable.getSelectionModel().clearSelection();
		
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
			_pathTextField.setText("");
			_pathTextField.setEnabled(true);
			
			_removeButton.setEnabled(false);
			_applyButton.setEnabled(false);
		}
		else {
			ComponentConfig cc = _tableModel.getElementAt(index);
			_nameTextField.setText(cc.getFileName());
			_pathTextField.setText(cc.getAbsolutePath());
			if (cc.getFlag() == 'E')
				_descriptionTextField.setText(cc.getDescription());
			else
				_descriptionTextField.setText(cc.getNewDescription());
			_nameTextField.setEnabled(false);
			_pathTextField.setEnabled(false);
			
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
			"ConfigurationFileDescriptionPanel", "fieldsHaveChanged");

		trace_methods.beginningOfMethod();
		// V�rification du contenu du champ Nom
		if(_nameTextField.getText().equals("") 
				|| _pathTextField.getText().equals(""))
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
