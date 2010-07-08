/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
 * ------------------------------------------------------------
 *
 * $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/appcfg/view
 * $Revision: 1.4 $
 *
 * ------------------------------------------------------------
 * DESCRIPTION: 
 * DATE:        29/08/2008
 * AUTEUR:      Hicham Doghmi
 * PROJET:      I-SIS
 * GROUPE:      
 * ------------------------------------------------------------*/
package com.bv.isis.console.impl.processor.appcfg.view;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
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
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.appcfg.model.PortalApplicationDoc;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractPanel;
import com.bv.isis.console.impl.processor.config.framework.view.WindowInterface;
import com.bv.isis.console.processor.ProcessorManager;




public class ApplicationDocPanel extends AbstractPanel {

	private static final long serialVersionUID = 1L;

	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: ApplicationDocPanel
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * Elle appelle la m�thode makePanel() charg�e de la cr�ation des objets
	 * graphiques � afficher dans le panneau.	
	 * ------------------------------------------------------------*/
	public ApplicationDocPanel(WindowInterface mainWindow) {
		super();

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ApplicationDocPanel", "ApplicationDocPanel");
		trace_methods.beginningOfMethod();

		_window = mainWindow;
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
	 *  - modele : Le tableau de ModelInterface.
	 * ------------------------------------------------------------*/
	public void beforeDisplay(ArrayList<ModelInterface> modele) {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ApplicationDocPanel", "beforeDisplay");
		trace_methods.beginningOfMethod();

		_tableModel = new ApplicationDocTableModel();


		if (modele != null) {
			for (int i = 0; i < modele.size(); i++) {
				if (modele.get(i) instanceof PortalApplicationDoc) {
					PortalApplicationDoc c = (PortalApplicationDoc) modele.get(i);
					if (c.getFlag() != 'S')
						_tableModel.addElement(c);
					else
						_tableModel.addDeleteElement(c);
				}
			}
		}
		_applicationDocTable.setModel(_tableModel);
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
				"ApplicationDocPanel", "afterDisplay");

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
	 * informations saisis par l'utilisateur. Si il y a une erreur, 
	 * elle retourne faux sinon vrai.
	 * 
	 * Retourne : vrai (true) si tout est conforme, faux (false) sinon.
	 * ------------------------------------------------------------*/
	public boolean beforeHide() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ApplicationDocPanel", "beforeHide");

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
				"ApplicationDocPanel", "afterHide");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}
	
	/*----------------------------------------------------------
	 * Nom: end
	 * 
	 * Description:
	 * Cette m�thode impl�mente la m�thode de l'interface PanelInterface.
	 * Cette m�thode est appel�e lors de la destruction de l'assistant. 
	 * Elle est utilis�e pour lib�rer l'espace m�moire utilis� par 
	 * les variables des classes.
	 *
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false sinon)
	 * ----------------------------------------------------------*/
	public boolean end() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ApplicationDocPanel", "end");

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
				"ApplicationDocPanel", "update");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tabModels=" + tabModels);

		ArrayList<PortalApplicationDoc> deletedElements = _tableModel
				.getDeleteDatas();
		ArrayList<PortalApplicationDoc> newElements = _tableModel.getDatas();

		if (tabModels != null) {

			// On supprime toutes les anciennes variables
			for (int index = 0; index < tabModels.size(); index++) {
				if (tabModels.get(index) instanceof PortalApplicationDoc) {
					tabModels.remove(index);
					// On recule d'une case dans le tableau pour ne pas oublier
					// l'�l�ment d�cal�
					index--;
				}
			}
		} else
			// Le tableau �tait inexistant, on le cr�e
			tabModels = new ArrayList<ModelInterface>();

		// On ajoute les nouvelles variables au tableau de ModelInterface
		tabModels.addAll(deletedElements);
		tabModels.addAll(newElements);

		trace_methods.endOfMethod();
		return tabModels;
	}
	
	/*----------------------------------------------------------
	 * Nom: setEnterCallback
	 *
	 * Description:
	 * Cette m�thode permet de d�finir la m�thode de callback en
	 * cas d'appui sur la touche "Entr�e" sur un objet JComponent
	 * pass� en argument.
	 * La m�thode de callback est la m�thode validateInput().
	 *
	 * Arguments:
	 *  - component: Un objet JComponent sur lequel le callback
	 *    doit �tre d�fini.
	 * ----------------------------------------------------------*/
	protected void setEnterCallback(JComponent component) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ApplicationDocPanel", "setEnterCallback");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("component=" + component);
		// On v�rifie la validit� de l'argument
		if (component == null) {
			trace_methods.endOfMethod();
			return;
		}
		// On positionne le callback sur Entree
		component.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					onClickApplyButton();
				} else
					fieldsHaveChanged();
			}
		});
		trace_methods.endOfMethod();
	}
	
	// ******************* PRIVE **********************
	/*------------------------------------------------------------
	 * Nom : _applicationDocTable
	 * 
	 * Description :
	 * Cet attribut est la table ou seront mis le donn�es relatives 
	 * aux variables d'un composant: le nom, la description et la valeur
	 * par default. 
	 * ------------------------------------------------------------*/
	private JTable _applicationDocTable;

	/*------------------------------------------------------------
	 * Nom : _nameTextField
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie du nom du document
	 * ------------------------------------------------------------*/
	private JTextField _nameTextField;

	/*------------------------------------------------------------
	 * Nom : _descriptionTextField;
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie de la description du 
	 * document
	 * ------------------------------------------------------------*/
	private JTextField _descriptionTextField;
	
	/*------------------------------------------------------------
	 * Nom : _urlTextField
	 * 
	 * Description :
	 * Cet attribut est utilis� pour la saisie de l'url du document
	 * ------------------------------------------------------------*/
	private JTextField  _urlTextField;
	
	/*------------------------------------------------------------
	 * Nom : _urlButton
	 * 
	 * Description :
	 * Cette attribut d�finit le boutton utilis� pour
	 * l'adresse de l'url dans un navigateur.
	 * ------------------------------------------------------------*/
	private JButton _urlButton;
	
	/*------------------------------------------------------------
	 * Nom : _applyButton
	 * 
	 * Description :
	 * Cette attribut d�finit le boutton utilis� pour
	 * enregistrer les informations saisies dans le tableau des variables.
	 * ------------------------------------------------------------*/
	private JButton _applyButton;

	/*------------------------------------------------------------
	 * Nom : _removeButton
	 * 
	 * Description :
	 * Cet attribut d�finit le boutton utilis� pour
	 * supprimer la ligne s�lectionn�e dans le tableau.
	 * ------------------------------------------------------------*/
	private JButton _removeButton;

	/*------------------------------------------------------------
	 * Nom : _newButton
	 * 
	 * Description :
	 * Cet attribut d�finit le boutton utilis� pour la saisie d'une 
	 * nouvelle variable.
	 * ------------------------------------------------------------*/
	private JButton _newButton;
	
	private class ApplicationDocTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		/*------------------------------------------------------------
		 * Nom : _titles
		 * 
		 * Description :
		 * Ce tableau de String repr�sente les titres des colonnes de la
		 * JTable.
		 * ------------------------------------------------------------*/
		private String[] _titles;

		/*------------------------------------------------------------
		 * Nom : _datas
		 * 
		 * Description :
		 * Cet attribut est un tableau de PortalApplicationDoc.
		 * Chaque �l�ment du tableau repr�sente une ligne de la JTable
		 * et contient les informations d'une variable.
		 * ------------------------------------------------------------*/
		private ArrayList<PortalApplicationDoc> _datas;
		
		/*------------------------------------------------------------
		 * Nom : _deleteDatas
		 * 
		 * Description :
		 * Cet attribut est un tableau de PortalApplicationDoc.
		 * Chaque �l�ment du tableau repr�sente un �l�ment supprim� 
		 * par l'utilisateur, gard� en m�moire pour le supprimer des tables 
		 * de donn�es.
		 * ------------------------------------------------------------*/
		private ArrayList<PortalApplicationDoc> _deleteDatas;
		
		/*------------------------------------------------------------
		 * Nom : VariableTableModel()
		 * 
		 * Description :
		 * Cette m�thode est le contructeur de la classe.
		 * Son r�le est de cr�er les deux tableaux de titres et de donn�es.
		 * Pour le tableau des titres, elle le remplira � partir du 
		 * fichier de configuration.
		 * ------------------------------------------------------------*/
		public ApplicationDocTableModel() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "ApplicationDocTableModel");
			trace_methods.beginningOfMethod();

			// Cr�ation du tableau des titres
			_titles = new String[3];
			// La premi�re colonne repr�sentera le nom du document
			_titles[0] = MessageManager
					.getMessage("&AppCfg_Name");
			// La deuxi�me colonne repr�sentera la description du document
			_titles[1] = MessageManager
					.getMessage("&AppCfg_Description");
			// La troisi�me colonne repr�sentera le commande du document
			_titles[2] = MessageManager
					.getMessage("&AppCfg_Url");

			// Cr�ation du tableau de donn�es
			_datas = new ArrayList<PortalApplicationDoc>();
			_deleteDatas = new ArrayList<PortalApplicationDoc>();

			trace_methods.endOfMethod();
		}
		
		/*------------------------------------------------------------
		 * Nom : getColumnCount
		 * 
		 * Description : 
		 * Cette m�thode retourne le nombre de colonnes de la JTable. 
		 * Ce nombre est donn� par la dimension du tableaux des titres.
		 * 
		 * Retourne : un entier : le nombre de colonnes.
		 * ------------------------------------------------------------*/
		public int getColumnCount() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "getColumnCount");
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
		 * Cette m�thode retourne le nombre de lignes de la JTable. 
		 * Ce nombre est donn� par la dimmension du tableaux des donn�es.
		 * 
		 * Retourne : un entier, le nombre de lignes.
		 * ------------------------------------------------------------*/
		public int getRowCount() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "getRowCount");
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
		 * Cette m�thode retourne le tableau de PortalApplicationDoc 
		 * contenant l'ensemble des informations saisies par l'utilisateur.
		 * 
		 * Retourne : Un tableau de PortalApplicationDoc
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationDoc> getDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "getDatas");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
								
			return _datas;
			
		}
		
		/*------------------------------------------------------------
		 * Nom : findRow
		 * 
		 * Description : 
		 * Cette m�thode recherche la pr�sence d'un dossier d'exploitation 
		 * dans la JTable � partir du nom du dossier d'exploitation pass�e 
		 * en param�tre. Elle retourne le num�ro de la ligne correspondant 
		 * � ce dossier d'exploitation si il existe, -1 sinon.
		 * 
		 * Argument :
		 *  - docName : une cha�ne de caract�res corespondant au nom
		 *    du dossier d'exploitation recherch�.
		 *    
		 * Retourne : un entier : le num�ro de ligne du dossier d'exploitation, 
		 * -1 si elle n'existe pas.
		 * ------------------------------------------------------------*/
		public int findRow(String docName) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "findRow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("docName=" + docName);

			if (_datas != null) {
				for (int index = 0; index < _datas.size(); index++) {
					if (_datas.get(index).getDocName()
							.equals(docName))
						return index;
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
		 * Le num�ro de ligne correspond � un dossier d'exploitation
		 * Le num�ro de colonne indique le champ : 
		 * 	- 0 pour le nom du dossier d'exploitation
		 *  - 1 pour sa description
		 *  - 2 pour son url
		 * 
		 * Arguments :
		 *  - line : le num�ro de ligne
		 *  - column : la num�ro de la colonne
		 *    
		 * Retourne : l'�lement � la position souhait�e, null si rien 
		 * n'est trouv�.
		 * ------------------------------------------------------------*/
		public Object getValueAt(int line, int column) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "getValueAt");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("line=" + line);
			trace_arguments.writeTrace("column=" + column);

			if (_datas != null && _datas.size() > line) {
				PortalApplicationDoc pad = _datas.get(line);
				if (pad != null) {
					if (column == 0)
						return pad.getDocName();
					else if (column == 1) {
						if (pad.getFlag() == 'E')
							return pad.getDescription();
						else
							return pad.getNewDescription();
					} else if (column == 2) {
						if (pad.getFlag() == 'E')
							return pad.getUrl();
						else
							return pad.getNewUrl();
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
		 * - col : le num�ro de colonne
		 * 
		 * Retourne : une chaine de caract�re, le nom de la colonne.
		 * ------------------------------------------------------------*/
		public String getColumnName(int col) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "getColumnName");
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
		 * Cette m�thode ajoute un nouvel �l�ment (un dossier d'exploitation) 
		 * au tableau de donn�es. 
		 * 
		 * Argument : 
		 *  - element : L'�l�ment � ajouter
		 * ------------------------------------------------------------*/
		public void addElement(PortalApplicationDoc element) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "addElement");
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
		 * Cette m�thode supprime un �l�ment (un dossier d'exploitation) 
		 * au tableau de donn�es. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � supprimer
		 * ------------------------------------------------------------*/
		public void removeElement(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "removeElement");
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
		 * Cette m�thode retourne le PortalApplicationDoc de la ligne 
		 * demand�e. 
		 * 
		 * Argument : 
		 *  - line : Le num�ro de ligne de l'�l�ment � r�cup�rer
		 *  
		 * Retourne : Le PortalApplicationDoc de la ligne s�lectionn�e.
		 * ------------------------------------------------------------*/
		public PortalApplicationDoc getElementAt(int line) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "getElementAt");
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
		 * base, le PortalApplicationDoc pass� en param�tre. 
		 * 
		 * Argument : 
		 *  - var : Le PortalApplicationDoc qu'il faudra supprimer 
		 *    de la base de donn�es.
		 * ------------------------------------------------------------*/
		public void addDeleteElement(PortalApplicationDoc pad) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "addDeleteElement");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("pad=" + pad);

			if (_deleteDatas != null)
				_deleteDatas.add(pad);

			trace_methods.endOfMethod();
		}
		
		/*------------------------------------------------------------
		 * Nom : removeDeleletElement
		 * 
		 * Description : 
		 * Cette m�thode retire au tableau des �l�ments � supprimer de la
		 * base, le PortalApplicationDoc d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du PortalApplicationDoc qu'il faut retirer 
		 *    du tableau.
		 * ------------------------------------------------------------*/
		public void removeDeleletElement(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "removeDeleletElement");
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
		 * base, le PortalApplicationDoc d'index �gal � l'index pass� 
		 * en param�tre. 
		 * 
		 * Argument : 
		 *  - index : L'index du PortalApplicationDoc qu'il faut r�cup�rer
		 *    dans le tableau.
		 * 
		 * Retourne : Le PortalApplicationDoc supprim� de la ligne
		 * souhait�e.
		 * ------------------------------------------------------------*/
		public PortalApplicationDoc getDeleteElementAt(int index) {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "getDeleteElementAt");
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
		 * Cette m�thode retourne l'ensemble du tableau des 
		 * PortalApplicationDoc � supprimer de la base de donn�es. 
		 * 
		 * Retourne : Le tableau des PortalApplicationDoc � supprimer.
		 * ------------------------------------------------------------*/
		public ArrayList<PortalApplicationDoc> getDeleteDatas() {
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"ApplicationDocTableModel", "getDeleteElementAt");
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
	private ApplicationDocTableModel _tableModel;

	/*------------------------------------------------------------
	 * Nom : makePanel
	 * 
	 * Description :
	 * Cette m�thode est appel�e lors de la construction du panneau.
	 * Son r�le est d'instancier les composants graphiques du panneau 
	 * et de les positionner.
	 * ------------------------------------------------------------*/
	private void makePanel() {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ApplicationDocPanel", "makePanel");
		trace_methods.beginningOfMethod();

		_tableModel = null;
		_applicationDocTable = new JTable(_tableModel);
		_applicationDocTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_applicationDocTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						onSelectionElement(event);
					}
				});
		// On int�gre un d�filement dans le tableau des variables
		JScrollPane scrollPaneVariableTable = new JScrollPane();
		scrollPaneVariableTable.setViewportView(_applicationDocTable);

		// On pr�sente le panneau au moyen d'une phrase descriptive
		JLabel firstDescriptionSentence = new JLabel();
		firstDescriptionSentence.setMinimumSize(new Dimension(480, 20));
		firstDescriptionSentence.setMaximumSize(new Dimension(480, 20));
		firstDescriptionSentence
				.setText(MessageManager
						.getMessage("&AppCfg_FirstDescriptionSentenceApplicationDoc"));

		// On d�finit un label pour le champs "Nom"
		JLabel nameLabel = new JLabel();
		nameLabel.setMinimumSize(new Dimension(95, 20));
		nameLabel.setMaximumSize(new Dimension(95, 20));
		nameLabel.setPreferredSize(new Dimension(95, 20));
		nameLabel.setText(MessageManager
				.getMessage("&AppCfg_Name")
				+ " :   *");

		// On d�finit un label pour le champs "Description"
		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setMinimumSize(new Dimension(95, 20));
		descriptionLabel.setMaximumSize(new Dimension(95, 20));
		descriptionLabel.setPreferredSize(new Dimension(95, 20));
		descriptionLabel.setText(MessageManager
				.getMessage("&AppCfg_Description")
				+ " :   *");
		
		// On d�finit un label pour le champs "URL"
		JLabel urlLabel = new JLabel();
		urlLabel.setMinimumSize(new Dimension(95, 20));
		urlLabel.setMaximumSize(new Dimension(95, 20));
		urlLabel.setPreferredSize(new Dimension(95, 20));
		urlLabel.setText(MessageManager.getMessage("&AppCfg_Url")
				+ " :   *");
		
		
		// On d�finit un label pour indiquer que l'asterisque en fin des noms de
		// champs sont obligatoires pour la saisie.
		JLabel obligatoryFields = new JLabel();
		obligatoryFields.setMinimumSize(new Dimension(150, 20));
		obligatoryFields.setMaximumSize(new Dimension(150, 20));
		obligatoryFields.setText(MessageManager
				.getMessage("&AppCfg_ObligatoryFields"));

		_nameTextField = new JTextField(20);
		setEnterCallback(_nameTextField);
		_nameTextField.setMinimumSize(new Dimension(355, 20));
		_nameTextField.setMaximumSize(new Dimension(355, 20));
		_nameTextField.setPreferredSize(new Dimension(355, 20));

		_descriptionTextField = new JTextField(20);
		setEnterCallback(_descriptionTextField);
		_descriptionTextField.setMinimumSize(new Dimension(355, 20));
		_descriptionTextField.setMaximumSize(new Dimension(355, 20));
		_descriptionTextField.setPreferredSize(new Dimension(355, 20));

		_urlTextField = new JFormattedTextField();
		setEnterCallback(_urlTextField);
		_urlTextField.setMinimumSize(new Dimension(270, 20));
		_urlTextField.setMaximumSize(new Dimension(270, 20));
		_urlTextField.setPreferredSize(new Dimension(300, 20));

		_urlButton = new JButton();
		_urlButton.setMinimumSize(new Dimension(60, 21));
		_urlButton.setMaximumSize(new Dimension(60, 21));
		_urlButton.setText(MessageManager
				.getMessage("&AppCfg_UrlButton"));
		_urlButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onClickUrlButton();
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

		Box hBox1 = Box.createHorizontalBox();
		hBox1.setMinimumSize(new Dimension(480, 20));
		hBox1.setMaximumSize(new Dimension(480, 20));
		hBox1.add(Box.createHorizontalStrut(5));
		hBox1.add(firstDescriptionSentence);
		hBox1.add(Box.createHorizontalStrut(5));

		Box hBox2 = Box.createHorizontalBox();
		hBox2.setMinimumSize(new Dimension(480, 200));
		hBox2.setMaximumSize(new Dimension(480, 200));
		hBox2.setPreferredSize(new Dimension(480, 200));
		hBox2.add(Box.createHorizontalStrut(5));
		hBox2.add(scrollPaneVariableTable);
		hBox2.add(Box.createHorizontalStrut(5));

		Box hBox3 = Box.createHorizontalBox();
		hBox3.setMinimumSize(new Dimension(480, 20));
		hBox3.setMaximumSize(new Dimension(480, 20));
		hBox3.add(Box.createHorizontalStrut(5));
		hBox3.add(nameLabel);
		hBox3.add(Box.createHorizontalStrut(10));
		hBox3.add(_nameTextField);
		hBox3.add(Box.createHorizontalStrut(5));

		Box hBox4 = Box.createHorizontalBox();
		hBox4.setMinimumSize(new Dimension(480, 20));
		hBox4.setMaximumSize(new Dimension(480, 20));
		hBox4.add(Box.createHorizontalStrut(5));
		hBox4.add(descriptionLabel);
		hBox4.add(Box.createHorizontalStrut(10));
		hBox4.add(_descriptionTextField);
		hBox4.add(Box.createHorizontalStrut(5));

		Box hBox5 = Box.createHorizontalBox();
		hBox5.setMinimumSize(new Dimension(480, 20));
		hBox5.setMaximumSize(new Dimension(480, 20));
		hBox5.add(Box.createHorizontalStrut(5));
		hBox5.add(urlLabel);
		hBox5.add(Box.createHorizontalStrut(10));
		hBox5.add(_urlTextField);
		hBox5.add(Box.createHorizontalStrut(10));
		hBox5.add(_urlButton);		
		hBox5.add(Box.createHorizontalStrut(5));

		Box hBox6 = Box.createHorizontalBox();
		hBox6.setMinimumSize(new Dimension(480, 20));
		hBox6.setMaximumSize(new Dimension(480, 20));
		hBox6.add(Box.createHorizontalStrut(85));
		hBox6.add(_newButton);
		hBox6.add(Box.createHorizontalStrut(10));
		hBox6.add(_removeButton);
		hBox6.add(Box.createHorizontalStrut(10));
		hBox6.add(_applyButton);
		hBox6.add(Box.createHorizontalStrut(5));

		Box hBox7 = Box.createHorizontalBox();
		hBox7.setMinimumSize(new Dimension(480, 20));
		hBox7.setMaximumSize(new Dimension(480, 20));
		hBox7.add(Box.createHorizontalStrut(10));
		hBox7.add(obligatoryFields);

		Box vBox1 = Box.createVerticalBox();
		vBox1.setMinimumSize(new Dimension(480, 250));
		vBox1.setMaximumSize(new Dimension(480, 250));
		vBox1.setPreferredSize(new Dimension(480, 250));
		vBox1.add(Box.createVerticalStrut(14));
		vBox1.add(hBox1);
		vBox1.add(Box.createVerticalStrut(15));
		vBox1.add(hBox2);

		Box vBox2 = Box.createVerticalBox();
		vBox2.setMinimumSize(new Dimension(480, 180));
		vBox2.setMaximumSize(new Dimension(480, 180));
		vBox2.setMaximumSize(new Dimension(480, 180));
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox3);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox4);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox5);
		vBox2.add(Box.createVerticalStrut(10));
		vBox2.add(hBox6);
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

		String name = _nameTextField.getText();
		String description = _descriptionTextField.getText();
		String url = _urlTextField.getText();

		if (name.equals("")) {
			DialogManager.displayDialog("Information", MessageManager
					.getMessage("&AppCfg_MessageInformation"),
					null, null);
		} else {
			/*
			 * 1 : V�rifier qu'il n'y a pas de dossiers d'exploitations avec le m�me nom 
			 * 2 : Mettre � jour le tableau 3 : Effacer les champs 4 : actualiser le
			 * tableau
			 */

			int row = _tableModel.findRow(name);
			if (row == -1) {

				char flag = 'A';
				PortalApplicationDoc pad = new PortalApplicationDoc();

				ArrayList<PortalApplicationDoc> deleteElement = _tableModel
						.getDeleteDatas();
				if (deleteElement != null) {
					for (int index = 0; index < deleteElement.size(); index++) {

						if (deleteElement.get(index).getDocName().equals(
								name)) {
							flag = 'M';
							pad = deleteElement.get(index);
							deleteElement.remove(index);
							break;
						}
					}
				}

				pad.setDocName(name);
				pad.setNewDescription(description);
				pad.setNewUrl(url);
				pad.setFlag(flag);
				_tableModel.addElement(pad);

				// On efface les champs de saisie
				_nameTextField.setText("");
				_descriptionTextField.setText("");
				_urlTextField.setText("");
				_applyButton.setEnabled(false);

			} else {

				// La variable existe donc on la met � jour
				int selectedRow = _applicationDocTable.getSelectedRow();

				if (selectedRow == -1) {
					DialogManager.displayDialog("Information", MessageManager
							.getMessage("&AppCfg_MessageInformationDoc"),
							null, null);
				} 
				else {
					PortalApplicationDoc pad = _tableModel.getElementAt(row);
					pad.setNewDescription(description);
					pad.setNewUrl(url);
					if (pad.getFlag() == 'E')
						pad.setFlag('M');

					// On recharge le tableau et on le r�affiche
					_tableModel.fireTableRowsUpdated(row, row);

					// On efface les champs de saisie
					_nameTextField.setText("");
					_descriptionTextField.setText("");
					_urlTextField.setText("");
					_applicationDocTable.getSelectionModel().clearSelection();
					_applyButton.setEnabled(false);
				}
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

		int selectedRow = _applicationDocTable.getSelectedRow();

		// Si une ligne du tableau est s�lectionn�e
		if (selectedRow != -1) {
			PortalApplicationDoc pad = _tableModel.getElementAt(selectedRow);
			if (pad != null) {
				if (pad.getFlag() == 'A')
					_tableModel.removeElement(selectedRow);
				else if (pad.getFlag() == 'E' || pad.getFlag() == 'M') {
					_tableModel.removeElement(selectedRow);
					pad.setFlag('S');
					_tableModel.addDeleteElement(pad);
				} 
				else {

				}
			}
			_tableModel.fireTableRowsDeleted(selectedRow, selectedRow);

			_nameTextField.setText("");
			_descriptionTextField.setText("");
			_urlTextField.setText("");

			_applicationDocTable.getSelectionModel().clearSelection();
			_applyButton.setEnabled(false);
			// On d�sactive le bouton supprimer
			_removeButton.setEnabled(false);
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

		_nameTextField.setText("");
		_descriptionTextField.setText("");
		_urlTextField.setText("");
		_applicationDocTable.getSelectionModel().clearSelection();
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
		ListSelectionModel lsm = (ListSelectionModel) event.getSource();

		int index = lsm.getMinSelectionIndex();
		if (index == -1) {
			_nameTextField.setText("");
			_nameTextField.setEnabled(true);
			_descriptionTextField.setText("");
			_urlTextField.setText("");

			_applyButton.setEnabled(false);
			_removeButton.setEnabled(false);

		} else {

			PortalApplicationDoc cv = new PortalApplicationDoc();
			cv = _tableModel.getElementAt(index);
			_nameTextField.setText(cv.getDocName());
			if (cv.getFlag() == 'E') {
				_descriptionTextField.setText(cv.getDescription());
				_urlTextField.setText(cv.getUrl());
			} else {
				_descriptionTextField.setText(cv.getNewDescription());
				_urlTextField.setText(cv.getNewUrl());
			}
			_nameTextField.setEnabled(false);

			_applyButton.setEnabled(true);
			_removeButton.setEnabled(true);
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
	private void fieldsHaveChanged() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"LoginDialog", "fieldsHaveChanged");

		trace_methods.beginningOfMethod();

		if (_nameTextField.getText().equals("")) {
			_applyButton.setEnabled(false);
			return;
		}
		// Ok, on peut valider le bouton
		_applyButton.setEnabled(true);

		trace_methods.endOfMethod();
	}

	private void onClickUrlButton() {
		
		String url = _urlTextField.getText();
		if (!url.equals("")) {
			
			// Petit pb, si je met null pour le noeud s�lectionn�, on se 
			// retrouve a devoir saisir a la main l'url. 
			// Viens du code de OpenUrlProcessor, ligne 198
			
			try {
				ProcessorManager.executeProcessor("OpenURL", 
						_window.getMainWindowInterfaceFromProcessorFrame(), 
						null, url, null, null, null, false, false);
			}
			catch (InnerException exc) {
				System.out.println("Exception : " + exc);
			}
		}
	}
}
