/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/SortedTableModel.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Mod�le de tri des donn�es d'un tableau
* DATE:        11/01/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SortedTableModel.java,v $
* Revision 1.2  2009/01/14 14:19:33  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.gui.
*
* Revision 1.1  2005/07/01 12:01:15  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.gui;

//
//Imports syst�me
//
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.Icon;
import java.util.Vector;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: SortedTableModel
* 
* Description:
* Cette classe est une classe technique charg�e de permettre le tri des 
* donn�es d'un tableau en cliquant sur une ent�te de colonne de celui-ci. 
* Son but est d'�tre plac� entre le mod�le des donn�es et la vue (la JTable). 
* Elle est inspir�e des classe TableSorter du Tutorial Swing.
* Cette classe ne maintient pas une copie des donn�es, elle maintient une 
* table de mappage entre les indexes de la vue et ceux du mod�le. A chaque 
* fois qu'une requ�te est effectu�e au niveau du mod�le tri� (comme 
* getValueAt() par exemple), celle-ci est retransmise au mod�le source une 
* fois qu'une translation de l'index de rang ait �t� effectu�e, via la table 
* de mappage.
* Cette classe utilise des classes embarqu�es, permettant de g�rer les 
* directives de tri, d'impl�menter l'algorithme de tri, de r�agir aux clicks 
* de souris, aux modifications du mod�le source ou encore de modifier les 
* ent�tes de colonnes pour faire appara�tre le tri.
* ----------------------------------------------------------*/
public class SortedTableModel
	extends AbstractTableModel
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DESCENDING
	* 
	* Description:
	* Cette valeur constante d�finit la valeur pour un tri descendant.
	* ----------------------------------------------------------*/
	public static final int DESCENDING = -1;

	/*----------------------------------------------------------
	* Nom: NOT_SORTED
	* 
	* Description:
	* Cette valeur constante d�finit la valeur pour aucun tri.
	* ----------------------------------------------------------*/
	public static final int NOT_SORTED = 0;

	/*----------------------------------------------------------
	* Nom: ASCENDING
	* 
	* Description:
	* Cette valeur constante d�finit la valeur pour un tri ascendant.
	* ----------------------------------------------------------*/
	public static final int ASCENDING = 1;

	/*----------------------------------------------------------
	* Nom: NO_SELECTION
	* 
	* Description:
	* Cette valeur constante d�finit la valeur indiquant aucune colonne 
	* s�lectionn�e.
	* ----------------------------------------------------------*/
	public static final int NO_SELECTION = -1;

	/*----------------------------------------------------------
	* Nom: COMPARABLE_COMPARATOR
	* 
	* Description:
	* Cette valeur constante correspond � un comparateur pour les objets dont 
	* la classe impl�mente l'interface Comparable. Elle est charg�e 
	* d'impl�menter l'algorithme de comparaison � partir de la m�thode 
	* compareTo().
	* Si les objets pass�s en arguments sont de type String, et qu'ils 
	* repr�sentent des valeurs num�riques, la comparaison est effectu�e sur 
	* les valeurs num�riques, et pas sur les cha�nes.
	* ----------------------------------------------------------*/
	public static final Comparator COMPARABLE_COMPARATOR = new Comparator()
	{
		public int compare(Object object1, Object object2)
		{
			if(object1 instanceof String && object2 instanceof String)
			{
				String string1 = (String)object1;
				String string2 = (String)object2;
				// Est-ce des valeurs num�riques ?
				try
				{
					Double double1 = Double.valueOf(string1);
					Double double2 = Double.valueOf(string2);
					// Ce sont des valeurs num�riques, on va effectuer la 
					// comparaison sur les doubles
					return double1.compareTo(double2);
				}
				catch(Exception e)
				{
					// Ce ne sont pas des valeurs num�riques, on va effectuer
					// la comparaison des cha�nes
					return string1.compareTo(string2); 
				}
			}
			return ((Comparable)object1).compareTo(object2);
		}
	};

	/*----------------------------------------------------------
	* Nom: LEXICAL_COMPARATOR
	* 
	* Description:
	* Cette valeur constante correspond � un comparateur pour les objets dont 
	* la classe n'impl�mente pas l'interface Comparable. Elle est charg�e 
	* d'impl�menter l'algorithme de comparaison en comparant les conversions 
	* sous forme de cha�nes de caract�res des objets.
	* ----------------------------------------------------------*/
	public static final Comparator LEXICAL_COMPARATOR = new Comparator()
	{
		public int compare(Object object1, Object object2)
		{
			return object1.toString().compareTo(object2.toString());
		}
	};

	/*----------------------------------------------------------
	* Nom: SortedTableModel
	* 
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. Elle cr�e un 
	* mod�le vide. Il sera n�cessaire d'associer le mod�le source 
	* ult�rieurement via la m�thode setModel().
	* ----------------------------------------------------------*/
	public SortedTableModel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "SortedTableModel");
			
		trace_methods.beginningOfMethod();
		// On cr�e le gestionnaire de souris et le gestionnaire de mod�le
		_mouseListener = new MouseHandler();
		_modelListener = new TableModelHandler();
		// On cr�e les listes
		_columnComparators = new HashMap();
		_sortingColumns = new ArrayList();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SortedTableModel
	* 
	* Description:
	* Cette m�thode est un constructeur de la classe permettant d'instancier 
	* le mod�le en lui assignant d�s le d�part le mod�le source.
	* 
	* Arguments:
	*  - model: Une r�f�rence sur un objet TableModel correspondant au mod�le 
	*    source.
  	* ----------------------------------------------------------*/
 	public SortedTableModel(
 		TableModel model
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "SortedTableModel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("model=" + model);
		// On cr�e le gestionnaire de souris et le gestionnaire de mod�le
		_mouseListener = new MouseHandler();
		_modelListener = new TableModelHandler();
		// On cr�e les listes
		_columnComparators = new HashMap();
		_sortingColumns = new ArrayList();
		// On positionne le mod�le
		setModel(model);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: SortedTableModel
	* 
	* Description:
	* Cette m�thode est un constructeur de la classe permettant de cr�er un 
	* mod�le par d�faut � partir des donn�es pass�es en argument. Le mod�le 
	* par d�faut est une instance de DefaultTableModel.
	* 
	* Arguments:
	*  - data: Un tableau d'objets � deux dimensions contenant l'ensemble des 
	*    valeurs du tableau (rangs et colonnes),
	*  - columnNames: Un tableau d'objets � une dimension correspondant aux 
	*    noms des colonnes.
  	* ----------------------------------------------------------*/
	public SortedTableModel(
		Object[][] data,
		Object[] columnNames
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "SortedTableModel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);
		trace_arguments.writeTrace("columnNames=" + columnNames);
		// On cr�e le gestionnaire de souris et le gestionnaire de mod�le
		_mouseListener = new MouseHandler();
		_modelListener = new TableModelHandler();
		// On cr�e les listes
		_columnComparators = new HashMap();
		_sortingColumns = new ArrayList();
		// On positionne le mod�le � partir d'un mod�le par d�faut
		setModel(new DefaultTableModel(data, columnNames));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SortedTableModel
	* 
	* Description:
	* Cette m�thode est un constructeur de la classe permettant de cr�er un 
	* mod�le par d�faut � partir des donn�es pass�es en argument. Le mod�le 
	* par d�faut est une instance de DefaultTableModel.
	* 
	* Arguments:
	*  - data: Un vecteur contenant les donn�es, o� chaque �l�ment correspond 
	*    � une liste et contient un autre vecteur correspondant aux valeurs 
	*    des colonnes pour la ligne,
	*  - columnNames: Un vecteur contenant les noms des colonnes.
  	* ----------------------------------------------------------*/
	public SortedTableModel(
		Vector data,
		Vector columnNames
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "SortedTableModel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);
		trace_arguments.writeTrace("columnNames=" + columnNames);
		// On cr�e le gestionnaire de souris et le gestionnaire de mod�le
		_mouseListener = new MouseHandler();
		_modelListener = new TableModelHandler();
		// On cr�e les listes
		_columnComparators = new HashMap();
		_sortingColumns = new ArrayList();
		// On positionne le mod�le � partir d'un mod�le par d�faut
		setModel(new DefaultTableModel(data, columnNames));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getModel
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la r�f�rence sur le mod�le source du 
	* mod�le tri�.
	* 
	* Retourne: Une r�f�rence sur un objet TableModel correspondant au mod�le 
	* source.
	* ----------------------------------------------------------*/
	public TableModel getModel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getModel");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _model;
	}

	/*----------------------------------------------------------
	* Nom: setModel
	* 
	* Description:
	* Cette m�thode permet de positionner le mod�le source du mod�le tri�. 
	* Elle positionne un gestionnaire d'�v�nement sur le mod�le source (afin 
	* de r�agir aux modifications), efface toute notion de tri et signale un 
	* changement du mod�le � la vue (la JTable).
	* 
	* Arguments:
	*  - model: Une r�f�rence sur un objet TableModel correspondant au mod�le 
	*    source.
  	* ----------------------------------------------------------*/
 	public void setModel(
 		TableModel model
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "setModel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("model=" + model);
		// On se retire en tant que gestionnaire de mod�le si l'ancien mod�le
		// existe
		if(_model != null)
		{
			_model.removeTableModelListener(_modelListener);
		}
		// On enregistre le nouveau mod�le
		_model = model;
		// On s'ajoute en tant que gestionnaire du nouveau mod�le
		if(_model != null)
		{
			_model.addTableModelListener(_modelListener);
		}
		// On efface le tri et on signale un changement de structure de table
		clearSortingState();
		fireTableStructureChanged();
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: getTableHeader
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer l'objet JTableHeader correspondant � 
	* l'ent�te du tableau.
	* 
	* Retourne: Une r�f�rence sur un objet JTableHeader.
	* ----------------------------------------------------------*/
	public JTableHeader getTableHeader()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getTableHeader");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _tableHeader;
	}

	/*----------------------------------------------------------
	* Nom: setTableHeader
	* 
	* Description:
	* Cette m�thode permet de positionner la r�f�rence sur l'ent�te du tableau. 
	* Cette r�f�rence est n�cessaire afin que le mod�le puisse �tre inform� des 
	* clicks dans les ent�tes de colonnes, et qu'un gestionnaire de rendu 
	* puisse �tre affect� � l'ent�te.
	* 
	* Arguments:
	*  - tableHeader : Une r�f�rence sur l'ent�te du tableau.
 	* ----------------------------------------------------------*/
 	public void setTableHeader(
 		JTableHeader tableHeader
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "setTableHeader");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableHeader=" + tableHeader);
		// On se retire en tant que gestionnaire de souris si l'ancien ent�te
		// existe
		if(_tableHeader != null)
		{
			_tableHeader.removeMouseListener(_mouseListener);
			// Si le gestionnaire de rendu n'est pas le gestionnaire par d�faut,
			// on va repositionner celui par d�faut
			TableCellRenderer cell_renderer = _tableHeader.getDefaultRenderer();
			if(cell_renderer instanceof SortableHeaderRenderer)
			{
				SortableHeaderRenderer sortable_renderer =
					(SortableHeaderRenderer)cell_renderer; 
				_tableHeader.setDefaultRenderer(
					sortable_renderer.getTableCellRenderer());
			}
		}
		// On enregistre le nouvel ent�te
		_tableHeader = tableHeader;
		// On s'ajoute en tant que gestionnaire de souris du nouvel ent�te
		if(_tableHeader != null)
		{
			_tableHeader.addMouseListener(_mouseListener);
			// On va �galement enregistrer le gestionnaire de rendu
			TableCellRenderer sortable_renderer = 
				new SortableHeaderRenderer(_tableHeader.getDefaultRenderer());
			_tableHeader.setDefaultRenderer(sortable_renderer);
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: isSorting
	* 
	* Description:
	* Cette m�thode permet de savoir si les donn�es sont tri�es ou non. Les 
	* donn�es sont tri�es si au moins une colonne est d�finie comme colonne de 
	* tri.
	* 
	* Retourne: true si les donn�es sont tri�es, false sinon.
	* ----------------------------------------------------------*/
	public boolean isSorting()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "isSorting");
		boolean is_sorting = false;
			
		trace_methods.beginningOfMethod();
		// Il n'y a tri en cours que si la liste des colonnes tri�es n'est
		// pas vide
		is_sorting = (_sortingColumns.size() > 0);
		trace_methods.endOfMethod();
		return is_sorting;
	}

	/*----------------------------------------------------------
	* Nom: getSortingStatus
	* 
	* Description:
	* Cette m�thode permet de conna�tre l'�tat de tri de la colonne dont 
	* l'indice est pass� en argument.
	* 
	* Arguments:
	*  - column: L'indice de la colonne dont on veut conna�tre l'�tat de tri.
	* 
	* Retourne : ASCENDING si la colonne est tri�e en mode ascending, 
	* DESCENDING si la colonne est tri�e en mode descendant, sinon NOT_SORTED.
	* ----------------------------------------------------------*/
	public int getSortingStatus(
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getSortingStatus");
		int sorting_status = NOT_SORTED;
			
		trace_methods.beginningOfMethod();
		// L'�tat de tri de la colonne correspond � la direction de la
		// directive de tri pour celle-ci
		sorting_status = getSortDirective(column).getDirection();
		trace_methods.endOfMethod();
		return sorting_status;
	}

	/*----------------------------------------------------------
	* Nom: setSortingStatus
	* 
	* Description:
	* Cette m�thode permet de positionner l'�tat de tri de la colonne dont 
	* l'indice est pass�e en argument. L'�tat de tri peut valoir ASCENDING 
	* pour un tri ascendant, DESCENDING pour un tri descendant, et NOT_SORTED 
	* pour aucun tri.
	* 
	* Arguments:
	*  - column: L'indice de la colonne pour laquelle on veut positionner 
	*    l'�tat de tri,
	*  - status: L'�tat de tri de la colonne.
 	* ----------------------------------------------------------*/
 	public void setSortingStatus(
 		int column,
 		int status
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "setSortingStatus");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("status=" + status);
		// On commence par essayer de r�cup�rer la directive de tri pour cette
		// colonne
		SortDirective directive = getSortDirective(column);
		// Si la directive est vide et que l'�tat est diff�rent de NOT_SORTED, 
		// on va en ajouter une nouvelle
		if(directive == EMPTY_DIRECTIVE && status != NOT_SORTED)
		{
			trace_debug.writeTrace("Ajout d'une directive");
			_sortingColumns.add(new SortDirective(column, status));
		}
		else if(directive != EMPTY_DIRECTIVE)
		{
			// Si le nouvel �tat est NOT_SORTED, on peut retirer la directive
			if(status == NOT_SORTED)
			{
				trace_debug.writeTrace("Suppression de la directive");
				_sortingColumns.remove(directive);
			}
			else
			{
				trace_debug.writeTrace("Modification de la directive");
				// On va modifier l'�tat de tri sur la directive
				directive.setDirection(status);
			}
		}
		// On va signaler un changement d'�tat de tri
		sortingStatusChanged();
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: getSortingColumns
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer un it�rateur sur la liste des colonnes 
	* tri�es.
	* 
	* Retourne: Un objet Iterator sur la liste des colonnes tri�es.
	* ----------------------------------------------------------*/
	public Iterator getSortingColumns()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getSortingColumns");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		// On retourne l'it�rateur sur la liste
		return _sortingColumns.iterator();
	}

	/*----------------------------------------------------------
	* Nom: setColumnComparator
	* 
	* Description:
	* Cette m�thode permet de fixer un comparateur qui sera utilis� pour 
	* comparer deux valeurs d'une m�me classe (d'un m�me type). Le comparateur 
	* a un r�le essentiel puis c'est � lui qu'est d�l�gu�e la comparaison des 
	* valeurs.
	* Le comparateur est associ�e � une classe au niveau de la table des 
	* comparateurs (voir l'attribut _columnComparator).
	* 
	* Arguments:
	*  - type: Une classe � laquelle on veut associer un comparateur,
	*  - comparator: Une instance de Comparator � associer � la classe.
 	* ----------------------------------------------------------*/
 	public void setColumnComparator(
 		Class type,
 		Comparator comparator
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "setColumnComparator");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("type=" + type);
		trace_arguments.writeTrace("comparator=" + comparator);
		// Si le comparateur est null, on va supprimer le type de la table
		// des comparateurs
		if(comparator == null)
		{
			trace_debug.writeTrace("Suppression du comparateur pour le type: " + 
				type);
			_columnComparators.remove(type);
		}
		else
		{
			// On ajoute le comparateur dans la table des comparateurs
			if(comparator == COMPARABLE_COMPARATOR)
			{
				trace_debug.writeTrace("Assignation du comparateur " +
					"COMPARABLE_COMPARATOR au type: " + type);
			}
			else if(comparator == LEXICAL_COMPARATOR)
			{
				trace_debug.writeTrace("Assignation du comparateur " +
					"LEXICAL_COMPARATOR au type: " + type);
			}
			else
			{
				trace_debug.writeTrace("Assignation d'un comparateur " +
					"inconnu au type: " + type);
			}
			_columnComparators.put(type, comparator);
		}
		trace_methods.endOfMethod();
  	}

	/*----------------------------------------------------------
	* Nom: modelIndex
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la valeur de l'indice de rang du 
	* mod�le source � partir de l'indice de rang de la vue. Elle utilise 
	* pour cela le tableau de Row.
	* 
	* Arguments:
	*  - viewIndex: L'indice de rang dans la vue.
	* 
	* Retourne: L'indice de rang dans le mod�le source.
	* ----------------------------------------------------------*/
	public synchronized int modelIndex(
		int viewIndex
		)
	{
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "modelIndex");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");*/
		int model_index = NO_SELECTION;
			
		/*trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("viewIndex=" + viewIndex);*/
		// Si viewIndex correspond � NO_SELECTION, on retourne NO_SELECTION
		if(viewIndex == NO_SELECTION)
		{
			//trace_methods.endOfMethod();
			return model_index;
		}
		// On va r�cup�rer le tableau de Row correspondant � la translation
		// entre les indices de la vue et les indices du mod�le
		Row[] view_to_model = getViewToModel();
		// On va r�cup�rer l'indice correspondant au rang viewIndex
		if(viewIndex < view_to_model.length)
		{
			model_index = view_to_model[viewIndex].getModelIndex();
		}
		else
		{
			model_index = 0;
		}
		//trace_debug.writeTrace("model_index=" + model_index);
		//trace_methods.endOfMethod();
		return model_index;
	}

	/*----------------------------------------------------------
	* Nom: viewIndex
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la valeur de l'indice de rang de la 
	* vue � partir de l'indice de rang du mod�le source. Elle utilise pour 
	* cela le tableau de translation d'indices.
	* 
	* Arguments:
	*  - modelIndex: L'indice de rang dans le mod�le source.
	* 
	* Retourne: L'indice de rang dans la vue.
	* ----------------------------------------------------------*/
	public synchronized int viewIndex(
		int modelIndex
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "viewIndex");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int view_index = NO_SELECTION;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("modelIndex=" + modelIndex);
		// Si modelIndex correspond � NO_SELECTION, on retourne NO_SELECTION
		if(modelIndex == NO_SELECTION)
		{
			trace_methods.endOfMethod();
			return view_index;
		}
		// On va r�cup�rer le tableau d'entiers correspondant � la translation
		// entre les indices du mod�le et les indices de la vue
		int[] model_to_view = getModelToView(true);
		// On va r�cup�rer l'indice correspondant au rang modelIndex
		view_index = model_to_view[modelIndex];
		trace_debug.writeTrace("view_index=" + view_index);
		trace_methods.endOfMethod();
		return view_index;
	}

	/*----------------------------------------------------------
	* Nom: getRowCount
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface TableModel (impl�ment�e par 
	* la classe AbstractTableModel). Elle permet de r�cup�rer le nombre de 
	* rangs du mod�le.
	* Elle appelle la m�thode de m�me nom sur le mod�le source.
	* 
	* Retourne: Le nombre de rangs de donn�es du mod�le.
	* ----------------------------------------------------------*/
	public int getRowCount()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getRowCount");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int row_count = 0;
			
		trace_methods.beginningOfMethod();
		// Si le mod�le est non null, on r�cup�re le nombre de rangs
		if(_model != null)
		{
			row_count = _model.getRowCount();
		}
		else
		{
			trace_debug.writeTrace("Le mod�le n'est pas positionn� !");
		}
		trace_methods.endOfMethod();
		return row_count;
	}

	/*----------------------------------------------------------
	* Nom: getColumnCount
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface TableModel (impl�ment�e par 
	* la classe AbstractTableModel). Elle permet de r�cup�rer le nombre de 
	* colonnes du mod�le.
	* Elle appelle la m�thode de m�me nom sur le mod�le source.
	* 
	* Retourne: Le nombre de colonnes de donn�es du mod�le.
	* ----------------------------------------------------------*/
	public int getColumnCount()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getColumnCount");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int column_count = 0;
			
		trace_methods.beginningOfMethod();
		// Si le mod�le est non null, on r�cup�re le nombre de colonnes
		if(_model != null)
		{
			column_count = _model.getColumnCount();
		}
		else
		{
			trace_debug.writeTrace("Le mod�le n'est pas positionn� !");
		}
		trace_methods.endOfMethod();
		return column_count;
	}

	/*----------------------------------------------------------
	* Nom: getColumnName
	* 
	* Description:
	* Cette m�thode red�finit celle de la classe AbstractTableModel. Elle 
	* permet de r�cup�rer le nom de la colonne dont l'indice est pass� en 
	* argument.
	* Elle appelle la m�thode de m�me nom sur le mod�le source.
	* 
	* Arguments:
	*  - column: L'indice de la colonne dont on souhaite r�cup�rer le nom.
	* 
	* Retourne: Le nom de la colonne � l'indice column.
	* ----------------------------------------------------------*/
	public String getColumnName(
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getColumnName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String column_name = null;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		// Si le mod�le est non null, on r�cup�re le nom de la colonne
		if(_model != null)
		{
			column_name = _model.getColumnName(column);
		}
		else
		{
			trace_debug.writeTrace("Le mod�le n'est pas positionn� !");
		}
		trace_methods.endOfMethod();
		return column_name;
	}

	/*----------------------------------------------------------
	* Nom: getColumnClass
	* 
	* Description:
	* Cette m�thode red�finit celle de la classe AbstractTableModel. Elle 
	* permet de r�cup�rer la classe d'objet de la colonne dont l'indice est 
	* pass� en argument.
	* Elle appelle la m�thode de m�me nom sur le mod�le source.
	* 
	* Arguments:
	*  - column: L'indice de la colonne dont on souhaite r�cup�rer la classe 
	*    d'objet.
	* 
	* Retourne: La classe des objets situ� � la colonne column.
	* ----------------------------------------------------------*/
	public Class getColumnClass(
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getColumnClass");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Class column_class = null;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		// Si le mod�le est non null, on r�cup�re le nom de la colonne
		if(_model != null)
		{
			column_class = _model.getColumnClass(column);
			// Si la classe retourn�e est Object.class, il s'agit sans
			// doute du mod�le par d�faut (DefaultTableModel)
			if(column_class == Object.class && _model.getRowCount() > 0)
			{
				// S'il y a au moins un rang, on va r�cup�rer la classe
				// de la valeur du premier rang
				Object value = _model.getValueAt(0, column);
				if(value != null)
				{
					column_class = value.getClass();
				}
			}
		}
		else
		{
			trace_debug.writeTrace("Le mod�le n'est pas positionn� !");
		}
		trace_methods.endOfMethod();
		return column_class;
	}

	/*----------------------------------------------------------
	* Nom: isCellEditable
	* 
	* Description:
	* Cette m�thode red�finit celle de la classe AbstractTableModel. Elle 
	* permet de savoir si une cellule, dont le coordonn�es sont pass�es en 
	* argument, peut �tre �dit�e ou non.
	* Elle appelle la m�thode de m�me nom sur le mod�le source.
	* 
	* Arguments:
	*  - row: Le num�ro de rang de la cellule,
	*  - column: Le num�ro de colonne de la cellule.
	* 
	* Retourne: true si la cellule est �ditable, false sinon.
	* ----------------------------------------------------------*/
	public boolean isCellEditable(
		int row,
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "isCellEditable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		boolean is_editable = false;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("row=" + row);
		trace_arguments.writeTrace("column=" + column);
		// Si le mod�le est non null, on r�cup�re le caract�re �ditable
		// de la cellule
		if(_model != null)
		{
			is_editable = _model.isCellEditable(row, column);
		}
		else
		{
			trace_debug.writeTrace("Le mod�le n'est pas positionn� !");
		}
		trace_methods.endOfMethod();
		return is_editable;
	}

	/*----------------------------------------------------------
	* Nom: getValueAt
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface TableModel (impl�ment�e par 
	* la classe AbstractTableModel). Elle permet de r�cup�rer la valeur 
	* correspondant au rang row et � la colonne column.
	* La m�thode effectue un mapping entre l'indice de rang de la vue (row), 
	* et l'indice du rang dans le mod�le source.
	* 
	* Arguments:
	*  - row: L'indice du rang de la cellule dont on veut la valeur,
	*  - column: L'indice de la colonne de la cellule dont on veut la valeur.
	* 
	* Retourne: Une r�f�rence sur un Object correspondant � la valeur de la 
	* cellule.
	* ----------------------------------------------------------*/
	public Object getValueAt(
		int row,
		int column
		)
	{
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getValueAt");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");*/
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Object value = null;
			
		/*trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("row=" + row);
		trace_arguments.writeTrace("column=" + column);*/
		// Si le mod�le est non null, on r�cup�re la valeur de la cellule
		if(_model != null)
		{
			if(row < _model.getRowCount())
			{
				// La valeur est r�cup�r�e apr�s translation du num�ro de rang
				int translated_row = modelIndex(row);
				value = _model.getValueAt(translated_row, column);
			}
			else
			{
				// On est en dehors des valeurs, on retourne null
				value = null;
			}
		}
		else
		{
			trace_debug.writeTrace("Le mod�le n'est pas positionn� !");
		}
		//trace_methods.endOfMethod();
		return value;
	}

	/*----------------------------------------------------------
	* Nom: setValueAt
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface TableModel (impl�ment�e par 
	* la classe AbstractTableModel). Elle permet de d�finir une nouvelle 
	* valeur pour la cellule correspondant au rang row et � la colonne column.
	* La m�thode effectue un mapping entre l'indice de rang de la vue (row), 
	* et l'indice du rang dans le mod�le source.
	* 
	* Arguments:
	*  - value: Un objet contenant la nouvelle valeur de la cellule,
	*  - row: L'indice du rang de la cellule dont on veut la valeur,
	*  - column: L'indice de la colonne de la cellule dont on veut la valeur.
 	* ----------------------------------------------------------*/
 	public void setValueAt(
 		Object value,
 		int row,
 		int column
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getValueAt");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("row=" + row);
		trace_arguments.writeTrace("column=" + column);
		// Si le mod�le est non null, on positionne la valeur de la cellule
		if(_model != null)
		{
			// La valeur est positionn�e apr�s translation du num�ro de rang
			int translated_row = modelIndex(row);
			_model.setValueAt(value, translated_row, column);
		}
		else
		{
			trace_debug.writeTrace("Le mod�le n'est pas positionn� !");
		}
		trace_methods.endOfMethod();
 	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: getHeaderRendererIcon
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer une ic�ne � utiliser dans l'affichage 
	* de l'ent�te de la colonne, dont l'indice est pass� en argument, en 
	* fonction de son �tat de tri. Si la colonne est non tri�e, aucune ic�ne 
	* ne sera retourn�e. Sinon, une fl�che descendante ou ascendante sera 
	* retourn�e (gr�ce � la classe embarqu�e Arrow).
	* 
	* Arguments:
	*  - column: L'indice de la colonne pour laquelle on veut r�cup�rer une 
	*    ic�ne,
	*  - size: La taille de l'ic�ne � retourner.
	* 
	* Retourne: Une r�f�rence sur un objet Icon � utiliser lors de l'affichage 
	* de l'ent�te de la colonne.
	* ----------------------------------------------------------*/
	protected Icon getHeaderRendererIcon(
		int column,
		int size
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getHeaderRendererIcon");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Icon icon = null;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("size=" + size);
		// On va essayer de r�cup�rer la directive pour la colonne
		SortDirective directive = getSortDirective(column);
		// Si la directive n'est pas vide, on retourne une nouvelle instance
		// de Arrow
		if(directive != EMPTY_DIRECTIVE)
		{
			icon = new Arrow(directive.getDirection() == DESCENDING, size,
				_sortingColumns.indexOf(directive));
		}
		trace_methods.endOfMethod();
		return icon;
	}

	/*----------------------------------------------------------
	* Nom: getComparator
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer un objet Comparator impl�mentant la 
	* comparaison entre deux valeurs d'une m�me colonne. Ce Comparator est 
	* n�cessaire pour le tri des lignes du tableau.
	* 
	* Arguments:
	*  - column: L'indice de la colonne pour laquelle on veut r�cup�rer un 
	*    comparateur.
	* 
	* Retourne: Une r�f�rence sur un objet Comparator � utiliser pour la 
	* colonne.
	* ----------------------------------------------------------*/
	protected Comparator getComparator(
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getComparator");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Comparator comparator = null;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		// On va commencer par r�cup�rer la classe de la colonne
		Class column_class = getColumnClass(column);
		trace_debug.writeTrace("column_class=" + column_class);
		// On va r�cup�rer le comparateur associ� au type dans la table
		// des comparateurs
		comparator = (Comparator)_columnComparators.get(column_class);
		if(comparator == null)
		{
			// Il n'y a pas de comparateur associ� au type, on va en retourner un
			// par d�faut
			trace_debug.writeTrace("Aucun comparateur associ� au type");
			// On va commencer par v�rifier si la classe impl�mente l'interface
			// Comparable
			if(Comparable.class.isAssignableFrom(column_class) == true)
			{
				// On va utiliser le comparateur COMPARABLE_COMPARATOR
				trace_debug.writeTrace("Utilisation du comparateur " +
					"COMPARABLE_COMPARATOR");
				comparator = COMPARABLE_COMPARATOR;
			}
			else
			{
				// On va utiliser le comparateur LEXICAL_COMPARATOR
				trace_debug.writeTrace("Utilisation du comparateur " +
					"LEXICAL_COMPARATOR");
				comparator = LEXICAL_COMPARATOR;
			}
		}
		trace_methods.endOfMethod();
		return comparator;
	}


	/*----------------------------------------------------------
	* Nom: getModelToView
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer un tableau d'entiers correspondant au 
	* mappage entre les rangs du mod�le et ceux de la vue. Si le tri n'a pas 
	* �t� effectu�, celui-ci est ex�cut� via un appel � la m�thode 
	* getViewToModel().
	* 
	* Arguments:
	*  - sortIfRequired: Un bool�en indiquant si le tri doit �tre effectu� si 
	*    cela n'a pas d�j� �t� fait (true) ou non (false).
	* 
	* Retourne: Un tableau d'entiers correspondant au mappage entre les rangs 
	* du mod�le et ceux de la vue.
	* ----------------------------------------------------------*/
	protected synchronized int[] getModelToView(
		boolean sortIfRequired
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getModelToView");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("sortIfRequired=" + sortIfRequired);
		// Si _modelToView est null, et qu'il est possible de retrier les
		// donn�es, on le fait
		if(_modelToView == null && sortIfRequired == true)
		{
			// La ligne suivante provoquera l'ex�cution du tri
			int length = getViewToModel().length;
			_modelToView = new int[length];
			// On va enregistrer les donn�es de translation dans la table
			for(int index = 0 ; index < length ; index ++)
			{
				// L'appel � la m�thode modelIndex va convertir l'indice de 
				// la vue (index) en celui du mod�le
				_modelToView[modelIndex(index)] = index; 
			}
		}
		trace_methods.endOfMethod();
		return _modelToView;
	}

	/*----------------------------------------------------------
	* Nom: clearSortingState
	* 
	* Description:
	* Cette m�thode permet de r�-initialiser le tri des donn�es. Cela implique 
	* que le tri sera r�-effectu� lors du prochain acc�s aux donn�es du mod�le.
	* ----------------------------------------------------------*/
	protected synchronized void clearSortingState()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "clearSortingState");
			
		trace_methods.beginningOfMethod();
		// On va r�-initialiser les tables de translation
		_viewToModel = null;
		_modelToView = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: cancelSorting
	* 
	* Description:
	* Cette m�thode permet d'annuler tout tri dans les donn�es. L'annulation 
	* est effectu�e en supprimant toute colonne dans la liste des colonnes � 
	* trier, et efface le r�sultat du tri.
	* ----------------------------------------------------------*/
	protected synchronized void cancelSorting()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "cancelSorting");
			
		trace_methods.beginningOfMethod();
		// On va supprimer toutes les colonnes de la liste des colonnes tri�es
		_sortingColumns.clear();
		// On va signaler un changement dans les �tats de tri
		sortingStatusChanged();
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: Row
	* 
	* Description:
	* Cette classe embarqu�e est charg�e d'encapsuler le mappage entre un 
	* indice de rang du mod�le source et celui de la vue. De plus, elle 
	* impl�mente l'interface Comparable afin de permettre le tri des colonnes 
	* par son biais.
	* ----------------------------------------------------------*/
	private class Row
		implements Comparable
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: Row
		* 
		* Description:
		* Ce constructeur permet de d�finir un Row � partir de l'indice de 
		* rang dans le mod�le source.
		* 
		* Arguments:
		*  - modelIndex: L'indice de rang dans le mod�le source.
 		* ----------------------------------------------------------*/
 		public Row(
 			int modelIndex
 			)
 		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Row", "Row");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("modelIndex=" + modelIndex);
			_modelIndex = modelIndex;
			trace_methods.endOfMethod();
 		}

		/*----------------------------------------------------------
		* Nom: compareTo
		* 
		* Description:
		* Cette m�thode red�finit celle de l'interface Comparable. Elle permet 
		* de comparer l'objet courant avec un objet pass� en argument.
		* Dans le cas pr�sent, la m�thode est charg�e de comparer la ligne 
		* repr�sent�e par l'objet courant, avec la ligne repr�sent�e par 
		* l'objet Row pass� en argument.
		* La comparaison est principalement effectu�e par le biais d'un 
		* Comparator d�finit pour la colonne (voir la m�thode getComparator()).
		* 
		* Arguments:
		*  - object: Un objet � comparer avec l'objet courant.
		* 
		* Retourne : 0 si les deux objets ont une valeur identiques, -1 si 
		* l'objet courant a une valuer inf�rieure, et 1 si l'objet courant � 
		* une valeur sup�rieure.
		* ----------------------------------------------------------*/
		public int compareTo(
			Object object
			)
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Row", "compareTo");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("object=" + object);
			// On commence par v�rifier que l'objet pass� en argument est de 
			// type Row
			if(!(object instanceof Row))
			{
				trace_errors.writeTrace("L'objet n'est pas de type Row !");
				// On sort
				trace_methods.endOfMethod();
				return 0;
			}
			// On va r�cup�rer l'indice du mod�le associ� au Row pass� en
			// argument
			int row1 = _modelIndex;
			int row2 = ((Row)object)._modelIndex;
			trace_debug.writeTrace("row1=" + row1);
			trace_debug.writeTrace("row2=" + row2);
			// On va it�rer sur les colonnes tri�es
			TableModel model = getModel();
			Iterator iterator = getSortingColumns();
			while(iterator.hasNext() == true)
			{
				int comparison = 0;
				
				// On r�cup�re la directive
				SortDirective directive = (SortDirective)iterator.next();
				// On r�cup�re le num�ro de colonne associ� � la directive
				int column = directive.getColumn();
				int direction = directive.getDirection();
				trace_debug.writeTrace("column=" + column + ",direction=" + 
					direction);
				// On va r�cup�rer les valeurs pour les deux rangs
				Object object1 = model.getValueAt(row1, column);
				Object object2 = model.getValueAt(row2, column);
				// On va tout d'abord comparer les valeurs nulles
				if(object1 == null && object2 == null)
				{
					// On consid�re les valeurs comme identiques
					comparison = 0;
				}
				else if(object1 == null)
				{
					// On consid�re null comme inf�rieur � toute valeur
					comparison = -1;
				}
				else if(object2 == null)
				{
					comparison = 1;
				}
				else
				{
					// On va utiliser le comparateur associ� � la colonne
					Comparator comparator = getComparator(column);
					comparison = comparator.compare(object1, object2);
				}
				trace_debug.writeTrace("comparison=" + comparison);
				// Si la comparaison est diff�rente de 0 (valeurs diff�rentes), 
				// ce n'est pas n�cessaire de poursuivre
				if(comparison != 0)
				{
					if(direction == DESCENDING)
					{
						// Le tri est descendant, on inverse la valeur du r�sultat
						comparison *= -1;
					}
					// On sort
					trace_debug.writeTrace("comparison=" + comparison);
					trace_methods.endOfMethod();
					return comparison;
				}
			}
			trace_debug.writeTrace("comparison=" + 0);
			trace_methods.endOfMethod();
			// Si on arrive ici, c'est que les valeurs sont identiques
			return 0;
		}

		/*----------------------------------------------------------
		* Nom: getModelIndex
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer l'indice de rang dans le mod�le 
		* source associ� au Row.
		* 
		* Retourne: L'indice du rang dans le mod�le source.
		* ----------------------------------------------------------*/
		public int getModelIndex()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Row", "getModelIndex");
			
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _modelIndex;
		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
		/*----------------------------------------------------------
		* Nom: _modelIndex
		* 
		* Description:
		* Cet attribut maintient l'indice de rang du mod�le source 
		* correspondant � cette instance de Row.
		* ----------------------------------------------------------*/
		private int _modelIndex;
	};

	/*----------------------------------------------------------
	* Nom: TableModelHandler
	* 
	* Description:
	* Cette classe embarqu�e est charg�e de g�rer les �v�nements concernant le 
	* mod�le source. Pour cela, elle impl�mente l'interface TableModelListener.
	* Tous les �v�nements re�us du mod�le sont examin�s, parfois manipul�s, 
	* puis pass�s aux listeners du mod�le (typiquement la table elle-m�me). Si 
	* un changement du mod�le a invalid� l'ordre des rangs du tri, cela est 
	* pris en compte et le tri sera de nouveau effectu� la prochaine fois 
	* qu'une donn�e sera acc�d�e.
	* ----------------------------------------------------------*/
	private class TableModelHandler
		implements TableModelListener
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: TableModelHandler
		* 
		* Description:
		* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
		* lisibilit�.
		* ----------------------------------------------------------*/
		public TableModelHandler()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"TableModelHandler", "TableModelHandler");
			
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
		}

		/*----------------------------------------------------------
		* Nom: tableChanged
		* 
		* Description:
		* Cette m�thode red�finit celle de l'interface TableModelListener. 
		* Elle est appel�e � chaque fois qu'un �v�nement survient sur le 
		* mod�le source.
		* Si aucun tri n'est en cours, l'�v�nement est retransmis aux 
		* listeners.
		* Si la structure de la table a chang�e, on annule le tri.
		* Un �v�nement de cellule est �mis dans un cas bien pr�cis, permettant 
		* de ne pas retrier syst�matiquement lorsque cela n'est pas n�cessaire.
		* Sinon, les donn�es doivent �tre retri�es, et l'�v�nement est 
		* retransmis.
		* 
		* Arguments:
		*  - event: L'�v�nement de changement du mod�le source.
 		* ----------------------------------------------------------*/
 		public void tableChanged(
 			TableModelEvent event
 			)
 		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"TableModelHandler", "tableChanged");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("event=" + event);
			// Si on n'est pas en train de trier quoi que ce soit, on 
			// retransmet directement l'�v�nement
			if(isSorting() == false)
			{
				trace_debug.writeTrace("Pas de tri");
				// On va effacer le r�sultat de tri (au cas o�)
				clearSortingState();
				fireTableChanged(event);
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// S'il s'agit d'un �v�nement de structure, on va annuler le tri, 
			// des colonnes pouvant avoir �t� ajout�es ou supprim�es depuis
			// le mod�le
			if(event.getFirstRow() == TableModelEvent.HEADER_ROW)
			{
				trace_debug.writeTrace("Modification de structure du mod�le");
				cancelSorting();
				fireTableChanged(event);
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// On peut �ventuellement cr�er un �v�nement de cellule afin 
			// d'�viter de relancer un tri syst�matique lorsqu'une cellule
			// non tri�e est modifi�e. Il faut alors que les conditions 
			// suivantes soient v�rifi�es:
			//  1. Les modifications ne s'appliquent qu'� un seul rang,
			//  2. Les modifications ne s'appliquent qu'� une seule colonne,
			//  3. Il n'y a pas de tri sur cette colonne,
			//  4. Une recherche invers�e ne donnera pas lieu � un tri.
			int column = event.getColumn();
			int[] model_to_view = getModelToView(false);
			if(event.getFirstRow() == event.getLastRow() && 
				column != TableModelEvent.ALL_COLUMNS &&
				getSortingStatus(column) == NOT_SORTED &&
				model_to_view != null)
			{
				trace_debug.writeTrace("Ev�nement de cellule");
				// On va r�cup�rer l'indice du rang dans la vue
				int view_index = model_to_view[event.getFirstRow()];
				// On va cr�er un �v�nement de cellule
				fireTableChanged(new TableModelEvent(SortedTableModel.this,
					view_index, view_index, column, event.getType()));
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// Sinon, quelque chose est arriv� aux donn�es qui pourrait avoir
			// invalid� le r�sultat du tri. On va effacer le r�sultat pour que 
			// le tri soit de nouveau effectu� lors du prochain acc�s aux donn�es
			trace_debug.writeTrace("Ev�nement de donn�es");
			clearSortingState();
			fireTableDataChanged();
			trace_methods.endOfMethod();
 		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
	};
	

	/*----------------------------------------------------------
	* Nom: MouseHandler
	* 
	* Description:
	* Cette classe embarqu�e est charg�e de g�rer les �v�nements de la souris 
	* sur l'ent�te du tableau. Pour cela, elle red�finit la classe 
	* MouseAdapter.
	* Elle permet de positionner l'�tat de tri pour la colonne s�lectionn�e de 
	* mani�re cyclique entre les valeurs NOT_SORTED, DESCENDING et ASCENDING.
	* ----------------------------------------------------------*/
	private class MouseHandler
		extends MouseAdapter
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: MouseHandler
		* 
		* Description:
		* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
		* lisibilit�.
		* ----------------------------------------------------------*/
		public MouseHandler()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MouseHandler", "MouseHandler");
			
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
		}

		/*----------------------------------------------------------
		* Nom: mouseClicked
		* 
		* Description:
		* Cette m�thode red�finit celle de la super-classe MouseAdapter. Elle 
		* est appel�e � chaque fois qu'un click de souris est effectu� sur 
		* l'ent�te du tableau.
		* Elle r�cup�rer l'indice de la colonne sur laquelle le click a eu 
		* lieu, et positionne l'�tat de tri en effectuant un cycle sur les 
		* valeurs NOT_SORTED, DESCENDING, et ASCENDING.
		* 
		* Arguments:
		*  - event: L'�v�nement de click de souris.
 		* ----------------------------------------------------------*/
		public void mouseClicked(
			MouseEvent event
			)
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MouseHandler", "mouseClicked");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("event=" + event);
			// On va r�cup�rer l'ent�te de table sur lequel l'�v�nement a eu 
			// lieu, puis l'indice de la colonne s�lectionn�e
			JTableHeader table_header = (JTableHeader)event.getSource();
			TableColumnModel column_model = table_header.getColumnModel();
			int view_column = column_model.getColumnIndexAtX(event.getX());
			trace_debug.writeTrace("view_column=" + view_column);
			// On va r�cup�rer l'indice r�el dans le mod�le de la colonne
			int model_column = 
				column_model.getColumn(view_column).getModelIndex();
			trace_debug.writeTrace("model_column=" + model_column);
			// Si aucune colonne n'a �t� s�lectionn�e, on sort
			if(model_column == NO_SELECTION)
			{
				trace_methods.endOfMethod();
				return; 
			}
			// On va r�cup�rer l'�tat de tri de la colonne
			int status = getSortingStatus(model_column);
			// Si la touche CTRL n'est pas enfonc�e, on va d�finir une
			// nouvelle liste de colonnes
			if(event.isControlDown() == false)
			{
				cancelSorting();
			}
			else
			{
				trace_debug.writeTrace("Touche CTRL enfonc�e");
			}
			// On va maintenant modifier la valeur de l'�tat en fonction de
			// l'�tat de la touche SHIFT
			int offset = 1;
			if(event.isShiftDown() == true)
			{
				trace_debug.writeTrace("Touche SHIFT enfonc�e");
				offset = -1;
			}
			status += offset;
			// La ligne suivante permet de s'assurer que l'on obtient
			// une valeur comprise entre -1 et 1.
			status = (status + 4) % 3 - 1;
			trace_debug.writeTrace("status=" + status);
			// On va red�finir l'�tat de tri de la colonne
			setSortingStatus(model_column, status);
			trace_methods.endOfMethod();
		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
	};

	/*----------------------------------------------------------
	* Nom: Arrow
	* 
	* Description:
	* Cette classe embarqu�e est charg�e de la repr�sentation de l'�tat de tri 
	* des colonnes du tableau. Elle impl�mente l'interface Icon afin d'�tre 
	* utilis�e lors de l'affichage des ent�tes de colonnes.
	* ----------------------------------------------------------*/
	private class Arrow
		implements Icon
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: Arrow
		* 
		* Description:
		* Cette m�thode permet de construire un objet Arrow en lui indiquant 
		* le mode de tri des donn�es de la colonne, la dimension que l'ic�ne 
		* doit avoir, et enfin la priorit� de la colonne dans le tri global.
		* 
		* Arguments:
		*  - descending: Un bool�en indiquant si le tri des donn�es est 
		*    descendant (true) ou non (false),
		*  - size: La taille de la zone d'affichage de l'ic�ne d'�tat,
		*  - priority: La priorit� de la colonne dans le tri global.
 		* ----------------------------------------------------------*/
 		public Arrow(
 			boolean descending,
 			int size,
 			int priority
 			)
 		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Arrow", "Arrow");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("descending=" + descending);
			trace_arguments.writeTrace("size=" + size);
			trace_arguments.writeTrace("priority=" + priority);
			_descending = descending;
			_size = size;
			_priority = priority;
			trace_methods.endOfMethod();
 		}

		/*----------------------------------------------------------
		* Nom: paintIcon
		* 
		* Description:
		* Cette m�thode red�finit celle de l'interface Icon. Elle est appel�e 
		* � chaque fois que l'ic�ne doit �tre d�ssin�e.
		* Le dessin de l'ic�ne d�pend du mode de tri (orientation de la 
		* fl�che), de la taille de la zone d'affichage, et enfin de la 
		* priorit� de la colonne dans le tri global (moins la priorit� est 
		* importante et moins la fl�che est grande).
		* 
		* Arguments:
		*  - component: Une r�f�rence sur un objet Component permettant 
		*    �ventuellement de r�cup�rer des propri�t�s telles que la couleur 
		*    de fond,
		*  - graphics: Une r�f�rence sur un objet Graphics permettant le 
		*    dessin 2D,
		*  - x: La composante horizontale des coordonn�es du point de dessin,
		*  - y: La composante verticale des coordonn�es du point de dessin.
 		* ----------------------------------------------------------*/
 		public void paintIcon(
 			Component component,
 			Graphics graphics,
 			int x,
 			int y
 			)
 		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Arrow", "paintIcon");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			Color color = Color.GRAY;
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("component=" + component);
			trace_arguments.writeTrace("graphics=" + graphics);
			trace_arguments.writeTrace("x=" + x);
			trace_arguments.writeTrace("y=" + y);
			// On r�cup�re la couleur de fond du composant
			if(component != null)
			{
				color = component.getBackground();
			}
			// Dans le cas d'un tri sur plusieurs colonnes, chaque fl�che
			// sera 20% plus petit que le pr�c�dent
			int dx = (int)(_size / 2 * Math.pow(0.8, _priority));
			int dy = dx;
			if(_descending == true)
			{
				dy = -dx;
			}
			// On va aligner grossi�rement la fl�che avec la ligne de base de
			// la police de caract�res
			y = y + 5 * _size / 6;
			int shift = 1;
			if(_descending == false)
			{
				y = y - dy;
				shift = -1;
			}
			graphics.translate(x, y);
			// On va tracer la diagonale de droite
			graphics.setColor(color.darker());
			graphics.drawLine(dx / 2, dy, 0, 0);
			graphics.drawLine(dx / 2, dy + shift, 0, shift);
			// On va tracer la diagonale de gauche
			graphics.setColor(color.brighter());
			graphics.drawLine(dx / 2, dy, dx, 0);
			graphics.drawLine(dx / 2, dy + shift, dx, shift);
			// On va maintenant tracer la ligne horizontale
			if(_descending == true)
			{
				graphics.setColor(color.darker().darker());
			}
			else
			{
				graphics.setColor(color.brighter().brighter());
			}
			graphics.drawLine(dx, 0, 0, 0);
			graphics.translate(-x, -y);
			trace_methods.endOfMethod();
 		}

		/*----------------------------------------------------------
		* Nom: getIconWidth
		* 
		* Description:
		* Cette m�thode red�finit celle de l'interface Icon. Elle permet de 
		* r�cup�rer la largeur de l'ic�ne.
		* 
		* Retourne: La largeur de l'ic�ne.
		* ----------------------------------------------------------*/
		public int getIconWidth()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Arrow", "getIconWidth");
			
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _size;
		}

		/*----------------------------------------------------------
		* Nom: getIconHeight
		* 
		* Description:
		* Cette m�thode red�finit celle de l'interface Icon. Elle permet de 
		* r�cup�rer la hauteur de l'ic�ne.
		* 
		* Retourne: La hauteur de l'ic�ne.
		* ----------------------------------------------------------*/
		public int getIconHeight()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Arrow", "getIconHeight");
			
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _size;
		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
		/*----------------------------------------------------------
		* Nom: _descending
		* 
		* Description:
		* Cet attribut maintient un bool�en indiquant si l'�tat de tri est 
		* descendant (true) ou non (false).
		* ----------------------------------------------------------*/
		private boolean _descending;

		/*----------------------------------------------------------
		* Nom: _size
		* 
		* Description:
		* Cet attribut correspond � la dimension que doit avoir l'ic�ne pour 
		* obtenir un affichage optimal de l'indicateur d'�tat de tri dans 
		* l'ent�te de colonne.
		* ----------------------------------------------------------*/
		private int _size;

		/*----------------------------------------------------------
		* Nom: _priority
		* 
		* Description:
		* Cet attribut maintient une valeur correspondant � la priorit� de la 
		* colonne dans le tri des donn�es du tableau. Plus la valeur est 
		* petite, et plus la colonne est prioritaire. Il est utilis� dans le 
		* calcul de la dimension de l'ic�ne d'�tat de tri.
		* ----------------------------------------------------------*/
		private int _priority;
	};

	/*----------------------------------------------------------
	* Nom: SortableHeaderRenderer
	* 
	* Description:
	* Cette classe embarqu�e est charg�e de g�rer le rendu des ent�tes de 
	* colonnes du tableau. Pour cela, elle impl�mente l'interface 
	* TableCellRenderer, afin d'�tre associ�es aux cellules repr�sentant les 
	* ent�tes.
	* Elle utilise un autre objet TableCellRenderer pour la majeure partie du 
	* travail de rendu. Elle y associe �ventuellement une ic�ne (voir la 
	* classe Arrow) dans le cas o� la colonne � rendre soit incluse dans la 
	* liste des colonnes tri�es.
	* ----------------------------------------------------------*/
	private class SortableHeaderRenderer
		implements TableCellRenderer
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: SortableHeaderRenderer
		* 
		* Description:
		* Cette m�thode permet de cr�er l'instance de SortableHeaderRenderer en 
		* lui fournissant une r�f�rence sur un objet TableCellRenderer 
		* correspondant au gestionnaire de rendu par d�faut.
		* 
		* Arguments:
		*  - tableCellRenderer: Une r�f�rence sur le gestionnaire de rendu par 
		*    d�faut de l'ent�te de colonne.
 		* ----------------------------------------------------------*/
 		public SortableHeaderRenderer(
 			TableCellRenderer tableCellRenderer
 			)
 		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SortableHeaderRenderer", "SortableHeaderRenderer");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("tableCellRenderer=" + tableCellRenderer);
			_tableCellRenderer = tableCellRenderer;
			trace_methods.endOfMethod();
 		}

		/*----------------------------------------------------------
		* Nom: getTableCellRendererComponent
		* 
		* Description:
		* Cette m�thode red�finit celle de l'interface TableCellRenderer. Elle 
		* est appel�e lorsqu'une cellule du tableau doit �tre rendue 
		* (affich�e).
		* Elle va appeler la m�thode de m�me nom sur le gestionnaire par 
		* d�faut, puis �ventuellement y associer une ic�ne correspondant � 
		* l'�tat de tri si la colonne � rendre fait partie des colonnes tri�es.
		* 
		* Arguments:
		*  - table: Une r�f�rence sur la JTable dans laquelle la cellule doit 
		*    �tre rendue,
		*  - value: Un objet correspondant � la valeur de la cellule � rendre,
		*  - isSelected: Un bool�en indiquant si la cellule est s�lectionn�e,
		*  - hasFocus: Un bool�en indiquant si la cellule a le focus,
		*  - row: Le num�ro de rang de la cellule � rendre,
		*  - column: Le num�ro de colonne de la cellule � rendre.
		* 
		* Retourne: Une r�f�rence sur un objet Component permettant le rendu 
		* de la cellule.
		* ----------------------------------------------------------*/
		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column
			)
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SortableHeaderRenderer", "getTableCellRendererComponent");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			Component component = null;
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("table=" + table);
			trace_arguments.writeTrace("value=" + value);
			trace_arguments.writeTrace("isSelected=" + isSelected);
			trace_arguments.writeTrace("hasFocus=" + hasFocus);
			trace_arguments.writeTrace("row=" + row);
			trace_arguments.writeTrace("column=" + column);
			// S'il n'y a pas de gestionnaire de rendu par d�faut, on sort
			if(_tableCellRenderer == null)
			{
				trace_methods.endOfMethod();
				return component;
			}
			// On va r�cup�rer le composant permettant le rendu via le 
			// gestionnaire par d�faut
			component = _tableCellRenderer.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
			// Si le composant est un JLabel, on pourra �ventuellement lui
			// ajouter une ic�ne
			if(component instanceof JLabel)
			{
				JLabel label = (JLabel)component;
				// On va ajuster l'alignement � gauche
				label.setHorizontalTextPosition(JLabel.LEFT);
				// On va r�cup�rer le num�ro de colonne dans le mod�le
				int model_column = table.convertColumnIndexToModel(column);
				// On va positionner une ic�ne en fonction de l'indice de la 
				// colonne
				label.setIcon(getHeaderRendererIcon(model_column, 
					label.getFont().getSize()));
			}
			trace_methods.endOfMethod();
			return component;
		}

		/*----------------------------------------------------------
		* Nom: getTableCellRenderer
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer l'objet TableCellRenderer qui 
		* �tait positionn� par d�faut sur l'ent�te de la table.
		* 
		* Retourne: Une r�f�rence sur un objet TableCellRenderer.
		* ----------------------------------------------------------*/
		public TableCellRenderer getTableCellRenderer()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SortableHeaderRenderer", "getTableCellRenderer");
			
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _tableCellRenderer;
		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
		/*----------------------------------------------------------
		* Nom: _tableCellRenderer
		* 
		* Description:
		* Cet attribut maintient une r�f�rence sur un objet TableCellRenderer 
		* correspondant au gestionnaire de rendu par d�faut pour l'ent�te de 
		* colonne dans le tableau.
		* ----------------------------------------------------------*/
		private TableCellRenderer _tableCellRenderer;
	};

	/*----------------------------------------------------------
	* Nom: SortDirective
	* 
	* Description:
	* Cette classe embarqu�e est charg�e d'encapsuler une directive de tri 
	* pour une colonne. La directive de tri sp�cifie la direction (ascendante 
	* ou descendante) du tri pour cette colonne.
	* ----------------------------------------------------------*/
	private static class SortDirective
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: SortDirective
		* 
		* Description:
		* Ce constructeur permet d'instancier une nouvelle directive pour une 
		* colonne dont l'indice est pass� en argument en sp�cifiant la 
		* direction du tri.
		* 
		* Arguments:
		*  - column: L'indice de la colonne sur laquelle s'applique cette 
		*    directive,
		*  - direction: La direction de tri pour la colonne.
 		* ----------------------------------------------------------*/
 		public SortDirective(
 			int column,
 			int direction
 			)
 		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SortDirective", "SortDirective");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("column=" + column);
			trace_arguments.writeTrace("direction=" + direction);
			_column = column;
			_direction = direction;
			trace_methods.endOfMethod();
 		}

		/*----------------------------------------------------------
		* Nom: getColumn
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer l'indice de la colonne sur 
		* laquelle s'applique la directive.
		* 
		* Retourne: L'indice de la colonne.
		* ----------------------------------------------------------*/
		public int getColumn()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SortDirective", "getColumn");
			
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _column;
		}

		/*----------------------------------------------------------
		* Nom: getDirection
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer la direction du tri correspondant 
		* � cette directive.
		* 
		* Retourne: La direction du tri.
		* ----------------------------------------------------------*/
		public int getDirection()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SortDirective", "getDirection");
			
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _direction;
		}

		/*----------------------------------------------------------
		* Nom: setDirection
		* 
		* Description:
		* Cette m�thode permet de sp�cifier une nouvelle direction du tri pour 
		* la directive.
		* 
		* Arguments:
		*  - direction: La direction de tri pour la colonne.
 		* ----------------------------------------------------------*/
 		public void setDirection(
 			int direction
 			)
 		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SortDirective", "setDirection");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("direction=" + direction);
			_direction = direction;
			trace_methods.endOfMethod();
 		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
		/*----------------------------------------------------------
		* Nom: _column
		* 
		* Description:
		* Cet attribut maintient un entier correspondant � l'indice de la 
		* colonne pour laquelle cette directive s'applique.
		* ----------------------------------------------------------*/
		private int _column;

		/*----------------------------------------------------------
		* Nom: _direction
		* 
		* Description:
		* Cet attribut maintient un entier correspondant � la direction du tri 
		* pour cette directive. Elle peut prendre les valeurs ASCENDING, 
		* DESCENDING et NOT_SORTED.
		* ----------------------------------------------------------*/
		private int _direction;
	};

	/*----------------------------------------------------------
	* Nom: EMPTY_DIRECTIVE
	* 
	* Description:
	* Cette valeur constante est une r�f�rence sur un objet SortDirective 
	* correspondant � une directive vide.
	* ----------------------------------------------------------*/
	private static final SortDirective EMPTY_DIRECTIVE = 
		new SortDirective(NO_SELECTION, NOT_SORTED);

	/*----------------------------------------------------------
	* Nom: _modelToView
	* 
	* Description:
	* Cet attribut maintient un tableau d'entiers permettant le mappage direct 
	* entre les indices du mod�le source vers les indices de la vue.
	* ----------------------------------------------------------*/
	private int[] _modelToView;

	/*----------------------------------------------------------
	* Nom: _model
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur le mod�le des donn�es source. 
	* Il est n�cessaire afin d'acc�der aux �l�ments du mod�le apr�s 
	* translation des indices de rang.
	* ----------------------------------------------------------*/
	private TableModel _model;

	/*----------------------------------------------------------
	* Nom: _viewToModel
	* 
	* Description:
	* Cet attribut est un tableau de Row correspondant au tableau de mappage 
	* entre les indices de rangs de la vue et ceux du mod�le.
	* ----------------------------------------------------------*/
	private Row[] _viewToModel;

	/*----------------------------------------------------------
	* Nom: _tableHeader
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet JTableHeader 
	* correspondant � l'ent�te du tableau.
	* ----------------------------------------------------------*/
	private JTableHeader _tableHeader;

	/*----------------------------------------------------------
	* Nom: _mouseListener
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet MouseListener 
	* correspondant au gestionnaire de souris qui doit �tre associ� � 
	* l'ent�te du tableau afin de r�agir aux clicks de souris.
	* ----------------------------------------------------------*/
	private MouseListener _mouseListener;

	/*----------------------------------------------------------
	* Nom: _modelListener
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet TableModelListener qui 
	* doit �tre associ� au mod�le afin d'�tre inform� de chaque changement au 
	* niveau du mod�le source.
	* ----------------------------------------------------------*/
	private TableModelListener _modelListener;

	/*----------------------------------------------------------
	* Nom: _columnComparators
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur une HashMap correspondant � la 
	* liste des comparateurs pouvant �tre utilis�s pour la comparaison des 
	* colonnes d'un tableau. La cl� de la table est la classe des objets, et 
	* la valeur est une instance de Comparator � utiliser pour la comparaison.
	* ----------------------------------------------------------*/
	private HashMap _columnComparators;

	/*----------------------------------------------------------
	* Nom: _sortingColumns
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet ArrayList 
	* correspondant � la liste des colonnes servant au tri.
	* ----------------------------------------------------------*/
	private ArrayList _sortingColumns;

	/*----------------------------------------------------------
	* Nom: getSortDirective
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la directive de tri pour la colonne 
	* dont l'indice est pass� en argument.
	* Si la colonne n'a pas de directive, la directive EMPTY_DIRECTIVE est 
	* retourn�e.
	* 
	* Arguments:
	*  - column: L'indice de la colonne pour laquelle on veut r�cup�rer la 
	*    directive de tri.
	* 
	* Retourne: Une r�f�rence sur un objet SortDirective correspondant � la 
	* directive de tri pour la colonne.
	* ----------------------------------------------------------*/
	private SortDirective getSortDirective(
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getSortDirective");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		// On va rechercher dans la liste des colonnes tri�es celle qui
		// correspond � la m�me colonne
		for(int index = 0 ; index < _sortingColumns.size() ; index ++)
		{
			SortDirective directive = 
				(SortDirective)_sortingColumns.get(index);
			if(directive.getColumn() == column)
			{
				trace_debug.writeTrace("Directive trouv�e");
				// On va la retourner
				trace_methods.endOfMethod();
				return directive; 
			}
		}
		// Si on arrive ici, c'est que l'on n'a pas trouv� de directive
		// pour la colonne. On va retourner la directive vide.
		trace_debug.writeTrace("Directive non trouv�e");
		trace_methods.endOfMethod();
		return EMPTY_DIRECTIVE;
	}

	/*----------------------------------------------------------
	* Nom: sortingStatusChanged
	* 
	* Description:
	* Cette m�thode est destin�e � �tre appel�e � chaque fois qu'un �tat de 
	* tri a chang�. Elle r�-initialise le tri, �met un �v�nement de changement 
	* des donn�es et commande le r�-affichage de l'ent�te du tableau.
	* ----------------------------------------------------------*/
	private void sortingStatusChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "sortingStatusChanged");
			
		trace_methods.beginningOfMethod();
		// On va r�-initialiser le r�sultat du tri
		clearSortingState();
		// On va signaler un changement des donn�es (pour r�affichage)
		fireTableDataChanged();
		// On va forcer l'ent�te � se r�afficher, s'il y en a un
		if(_tableHeader != null)
		{
			_tableHeader.repaint();
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getViewToModel
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer un tableau de Row correspondant au 
	* mappage entre les rangs de la vue et ceux du mod�le. Si le tri n'a pas 
	* �t� effectu� au pr�alable, il est execut�.
	* 
	* Retourne: Un tableau de Row correspondant au mappage entre les rangs de 
	* la vue et ceux du mod�le.
	* ----------------------------------------------------------*/
	private synchronized Row[] getViewToModel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getViewToModel");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
			
		trace_methods.beginningOfMethod();
		// S'il n'y pas de tableau de translation, on va le cr�er
		if(_viewToModel == null)
		{
			trace_debug.writeTrace("Re-cr�ation du tableau de translation");
			int row_count = _model.getRowCount();
			// On va instancier le tableau
			_viewToModel = new Row[row_count];
			// On va remplir le tableau avec les valeurs par d�faut
			for(int index = 0 ; index < row_count ; index ++)
			{
				_viewToModel[index] = new Row(index);
			}
			// S'il y a un tri � effectuer, on va le lancer
			if(isSorting() == true)
			{
				trace_debug.writeTrace("Ex�cution du tri");
				// On va utiliser la m�thode sort() de la classe Arrays
				Arrays.sort(_viewToModel);
			}
		}
		trace_methods.endOfMethod();
		return _viewToModel;
	}
}
