/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/SortedTableModel.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Modèle de tri des données d'un tableau
* DATE:        11/01/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SortedTableModel.java,v $
* Revision 1.2  2009/01/14 14:19:33  tz
* Classe déplacée dans le package com.bv.isis.console.core.gui.
*
* Revision 1.1  2005/07/01 12:01:15  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.gui;

//
//Imports système
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
* Cette classe est une classe technique chargée de permettre le tri des 
* données d'un tableau en cliquant sur une entête de colonne de celui-ci. 
* Son but est d'être placé entre le modèle des données et la vue (la JTable). 
* Elle est inspirée des classe TableSorter du Tutorial Swing.
* Cette classe ne maintient pas une copie des données, elle maintient une 
* table de mappage entre les indexes de la vue et ceux du modèle. A chaque 
* fois qu'une requête est effectuée au niveau du modèle trié (comme 
* getValueAt() par exemple), celle-ci est retransmise au modèle source une 
* fois qu'une translation de l'index de rang ait été effectuée, via la table 
* de mappage.
* Cette classe utilise des classes embarquées, permettant de gérer les 
* directives de tri, d'implémenter l'algorithme de tri, de réagir aux clicks 
* de souris, aux modifications du modèle source ou encore de modifier les 
* entêtes de colonnes pour faire apparaître le tri.
* ----------------------------------------------------------*/
public class SortedTableModel
	extends AbstractTableModel
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DESCENDING
	* 
	* Description:
	* Cette valeur constante définit la valeur pour un tri descendant.
	* ----------------------------------------------------------*/
	public static final int DESCENDING = -1;

	/*----------------------------------------------------------
	* Nom: NOT_SORTED
	* 
	* Description:
	* Cette valeur constante définit la valeur pour aucun tri.
	* ----------------------------------------------------------*/
	public static final int NOT_SORTED = 0;

	/*----------------------------------------------------------
	* Nom: ASCENDING
	* 
	* Description:
	* Cette valeur constante définit la valeur pour un tri ascendant.
	* ----------------------------------------------------------*/
	public static final int ASCENDING = 1;

	/*----------------------------------------------------------
	* Nom: NO_SELECTION
	* 
	* Description:
	* Cette valeur constante définit la valeur indiquant aucune colonne 
	* sélectionnée.
	* ----------------------------------------------------------*/
	public static final int NO_SELECTION = -1;

	/*----------------------------------------------------------
	* Nom: COMPARABLE_COMPARATOR
	* 
	* Description:
	* Cette valeur constante correspond à un comparateur pour les objets dont 
	* la classe implémente l'interface Comparable. Elle est chargée 
	* d'implémenter l'algorithme de comparaison à partir de la méthode 
	* compareTo().
	* Si les objets passés en arguments sont de type String, et qu'ils 
	* représentent des valeurs numériques, la comparaison est effectuée sur 
	* les valeurs numériques, et pas sur les chaînes.
	* ----------------------------------------------------------*/
	public static final Comparator COMPARABLE_COMPARATOR = new Comparator()
	{
		public int compare(Object object1, Object object2)
		{
			if(object1 instanceof String && object2 instanceof String)
			{
				String string1 = (String)object1;
				String string2 = (String)object2;
				// Est-ce des valeurs numériques ?
				try
				{
					Double double1 = Double.valueOf(string1);
					Double double2 = Double.valueOf(string2);
					// Ce sont des valeurs numériques, on va effectuer la 
					// comparaison sur les doubles
					return double1.compareTo(double2);
				}
				catch(Exception e)
				{
					// Ce ne sont pas des valeurs numériques, on va effectuer
					// la comparaison des chaînes
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
	* Cette valeur constante correspond à un comparateur pour les objets dont 
	* la classe n'implémente pas l'interface Comparable. Elle est chargée 
	* d'implémenter l'algorithme de comparaison en comparant les conversions 
	* sous forme de chaînes de caractères des objets.
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
	* Cette méthode est le constructeur par défaut de la classe. Elle crée un 
	* modèle vide. Il sera nécessaire d'associer le modèle source 
	* ultérieurement via la méthode setModel().
	* ----------------------------------------------------------*/
	public SortedTableModel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "SortedTableModel");
			
		trace_methods.beginningOfMethod();
		// On crée le gestionnaire de souris et le gestionnaire de modèle
		_mouseListener = new MouseHandler();
		_modelListener = new TableModelHandler();
		// On crée les listes
		_columnComparators = new HashMap();
		_sortingColumns = new ArrayList();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SortedTableModel
	* 
	* Description:
	* Cette méthode est un constructeur de la classe permettant d'instancier 
	* le modèle en lui assignant dès le départ le modèle source.
	* 
	* Arguments:
	*  - model: Une référence sur un objet TableModel correspondant au modèle 
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
		// On crée le gestionnaire de souris et le gestionnaire de modèle
		_mouseListener = new MouseHandler();
		_modelListener = new TableModelHandler();
		// On crée les listes
		_columnComparators = new HashMap();
		_sortingColumns = new ArrayList();
		// On positionne le modèle
		setModel(model);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: SortedTableModel
	* 
	* Description:
	* Cette méthode est un constructeur de la classe permettant de créer un 
	* modèle par défaut à partir des données passées en argument. Le modèle 
	* par défaut est une instance de DefaultTableModel.
	* 
	* Arguments:
	*  - data: Un tableau d'objets à deux dimensions contenant l'ensemble des 
	*    valeurs du tableau (rangs et colonnes),
	*  - columnNames: Un tableau d'objets à une dimension correspondant aux 
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
		// On crée le gestionnaire de souris et le gestionnaire de modèle
		_mouseListener = new MouseHandler();
		_modelListener = new TableModelHandler();
		// On crée les listes
		_columnComparators = new HashMap();
		_sortingColumns = new ArrayList();
		// On positionne le modèle à partir d'un modèle par défaut
		setModel(new DefaultTableModel(data, columnNames));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: SortedTableModel
	* 
	* Description:
	* Cette méthode est un constructeur de la classe permettant de créer un 
	* modèle par défaut à partir des données passées en argument. Le modèle 
	* par défaut est une instance de DefaultTableModel.
	* 
	* Arguments:
	*  - data: Un vecteur contenant les données, où chaque élément correspond 
	*    à une liste et contient un autre vecteur correspondant aux valeurs 
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
		// On crée le gestionnaire de souris et le gestionnaire de modèle
		_mouseListener = new MouseHandler();
		_modelListener = new TableModelHandler();
		// On crée les listes
		_columnComparators = new HashMap();
		_sortingColumns = new ArrayList();
		// On positionne le modèle à partir d'un modèle par défaut
		setModel(new DefaultTableModel(data, columnNames));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getModel
	* 
	* Description:
	* Cette méthode permet de récupérer la référence sur le modèle source du 
	* modèle trié.
	* 
	* Retourne: Une référence sur un objet TableModel correspondant au modèle 
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
	* Cette méthode permet de positionner le modèle source du modèle trié. 
	* Elle positionne un gestionnaire d'événement sur le modèle source (afin 
	* de réagir aux modifications), efface toute notion de tri et signale un 
	* changement du modèle à la vue (la JTable).
	* 
	* Arguments:
	*  - model: Une référence sur un objet TableModel correspondant au modèle 
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
		// On se retire en tant que gestionnaire de modèle si l'ancien modèle
		// existe
		if(_model != null)
		{
			_model.removeTableModelListener(_modelListener);
		}
		// On enregistre le nouveau modèle
		_model = model;
		// On s'ajoute en tant que gestionnaire du nouveau modèle
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
	* Cette méthode permet de récupérer l'objet JTableHeader correspondant à 
	* l'entête du tableau.
	* 
	* Retourne: Une référence sur un objet JTableHeader.
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
	* Cette méthode permet de positionner la référence sur l'entête du tableau. 
	* Cette référence est nécessaire afin que le modèle puisse être informé des 
	* clicks dans les entêtes de colonnes, et qu'un gestionnaire de rendu 
	* puisse être affecté à l'entête.
	* 
	* Arguments:
	*  - tableHeader : Une référence sur l'entête du tableau.
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
		// On se retire en tant que gestionnaire de souris si l'ancien entête
		// existe
		if(_tableHeader != null)
		{
			_tableHeader.removeMouseListener(_mouseListener);
			// Si le gestionnaire de rendu n'est pas le gestionnaire par défaut,
			// on va repositionner celui par défaut
			TableCellRenderer cell_renderer = _tableHeader.getDefaultRenderer();
			if(cell_renderer instanceof SortableHeaderRenderer)
			{
				SortableHeaderRenderer sortable_renderer =
					(SortableHeaderRenderer)cell_renderer; 
				_tableHeader.setDefaultRenderer(
					sortable_renderer.getTableCellRenderer());
			}
		}
		// On enregistre le nouvel entête
		_tableHeader = tableHeader;
		// On s'ajoute en tant que gestionnaire de souris du nouvel entête
		if(_tableHeader != null)
		{
			_tableHeader.addMouseListener(_mouseListener);
			// On va également enregistrer le gestionnaire de rendu
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
	* Cette méthode permet de savoir si les données sont triées ou non. Les 
	* données sont triées si au moins une colonne est définie comme colonne de 
	* tri.
	* 
	* Retourne: true si les données sont triées, false sinon.
	* ----------------------------------------------------------*/
	public boolean isSorting()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "isSorting");
		boolean is_sorting = false;
			
		trace_methods.beginningOfMethod();
		// Il n'y a tri en cours que si la liste des colonnes triées n'est
		// pas vide
		is_sorting = (_sortingColumns.size() > 0);
		trace_methods.endOfMethod();
		return is_sorting;
	}

	/*----------------------------------------------------------
	* Nom: getSortingStatus
	* 
	* Description:
	* Cette méthode permet de connaître l'état de tri de la colonne dont 
	* l'indice est passé en argument.
	* 
	* Arguments:
	*  - column: L'indice de la colonne dont on veut connaître l'état de tri.
	* 
	* Retourne : ASCENDING si la colonne est triée en mode ascending, 
	* DESCENDING si la colonne est triée en mode descendant, sinon NOT_SORTED.
	* ----------------------------------------------------------*/
	public int getSortingStatus(
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getSortingStatus");
		int sorting_status = NOT_SORTED;
			
		trace_methods.beginningOfMethod();
		// L'état de tri de la colonne correspond à la direction de la
		// directive de tri pour celle-ci
		sorting_status = getSortDirective(column).getDirection();
		trace_methods.endOfMethod();
		return sorting_status;
	}

	/*----------------------------------------------------------
	* Nom: setSortingStatus
	* 
	* Description:
	* Cette méthode permet de positionner l'état de tri de la colonne dont 
	* l'indice est passée en argument. L'état de tri peut valoir ASCENDING 
	* pour un tri ascendant, DESCENDING pour un tri descendant, et NOT_SORTED 
	* pour aucun tri.
	* 
	* Arguments:
	*  - column: L'indice de la colonne pour laquelle on veut positionner 
	*    l'état de tri,
	*  - status: L'état de tri de la colonne.
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
		// On commence par essayer de récupérer la directive de tri pour cette
		// colonne
		SortDirective directive = getSortDirective(column);
		// Si la directive est vide et que l'état est différent de NOT_SORTED, 
		// on va en ajouter une nouvelle
		if(directive == EMPTY_DIRECTIVE && status != NOT_SORTED)
		{
			trace_debug.writeTrace("Ajout d'une directive");
			_sortingColumns.add(new SortDirective(column, status));
		}
		else if(directive != EMPTY_DIRECTIVE)
		{
			// Si le nouvel état est NOT_SORTED, on peut retirer la directive
			if(status == NOT_SORTED)
			{
				trace_debug.writeTrace("Suppression de la directive");
				_sortingColumns.remove(directive);
			}
			else
			{
				trace_debug.writeTrace("Modification de la directive");
				// On va modifier l'état de tri sur la directive
				directive.setDirection(status);
			}
		}
		// On va signaler un changement d'état de tri
		sortingStatusChanged();
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: getSortingColumns
	* 
	* Description:
	* Cette méthode permet de récupérer un itérateur sur la liste des colonnes 
	* triées.
	* 
	* Retourne: Un objet Iterator sur la liste des colonnes triées.
	* ----------------------------------------------------------*/
	public Iterator getSortingColumns()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getSortingColumns");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		// On retourne l'itérateur sur la liste
		return _sortingColumns.iterator();
	}

	/*----------------------------------------------------------
	* Nom: setColumnComparator
	* 
	* Description:
	* Cette méthode permet de fixer un comparateur qui sera utilisé pour 
	* comparer deux valeurs d'une même classe (d'un même type). Le comparateur 
	* a un rôle essentiel puis c'est à lui qu'est déléguée la comparaison des 
	* valeurs.
	* Le comparateur est associée à une classe au niveau de la table des 
	* comparateurs (voir l'attribut _columnComparator).
	* 
	* Arguments:
	*  - type: Une classe à laquelle on veut associer un comparateur,
	*  - comparator: Une instance de Comparator à associer à la classe.
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
	* Cette méthode permet de récupérer la valeur de l'indice de rang du 
	* modèle source à partir de l'indice de rang de la vue. Elle utilise 
	* pour cela le tableau de Row.
	* 
	* Arguments:
	*  - viewIndex: L'indice de rang dans la vue.
	* 
	* Retourne: L'indice de rang dans le modèle source.
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
		// Si viewIndex correspond à NO_SELECTION, on retourne NO_SELECTION
		if(viewIndex == NO_SELECTION)
		{
			//trace_methods.endOfMethod();
			return model_index;
		}
		// On va récupérer le tableau de Row correspondant à la translation
		// entre les indices de la vue et les indices du modèle
		Row[] view_to_model = getViewToModel();
		// On va récupérer l'indice correspondant au rang viewIndex
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
	* Cette méthode permet de récupérer la valeur de l'indice de rang de la 
	* vue à partir de l'indice de rang du modèle source. Elle utilise pour 
	* cela le tableau de translation d'indices.
	* 
	* Arguments:
	*  - modelIndex: L'indice de rang dans le modèle source.
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
		// Si modelIndex correspond à NO_SELECTION, on retourne NO_SELECTION
		if(modelIndex == NO_SELECTION)
		{
			trace_methods.endOfMethod();
			return view_index;
		}
		// On va récupérer le tableau d'entiers correspondant à la translation
		// entre les indices du modèle et les indices de la vue
		int[] model_to_view = getModelToView(true);
		// On va récupérer l'indice correspondant au rang modelIndex
		view_index = model_to_view[modelIndex];
		trace_debug.writeTrace("view_index=" + view_index);
		trace_methods.endOfMethod();
		return view_index;
	}

	/*----------------------------------------------------------
	* Nom: getRowCount
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface TableModel (implémentée par 
	* la classe AbstractTableModel). Elle permet de récupérer le nombre de 
	* rangs du modèle.
	* Elle appelle la méthode de même nom sur le modèle source.
	* 
	* Retourne: Le nombre de rangs de données du modèle.
	* ----------------------------------------------------------*/
	public int getRowCount()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getRowCount");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int row_count = 0;
			
		trace_methods.beginningOfMethod();
		// Si le modèle est non null, on récupère le nombre de rangs
		if(_model != null)
		{
			row_count = _model.getRowCount();
		}
		else
		{
			trace_debug.writeTrace("Le modèle n'est pas positionné !");
		}
		trace_methods.endOfMethod();
		return row_count;
	}

	/*----------------------------------------------------------
	* Nom: getColumnCount
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface TableModel (implémentée par 
	* la classe AbstractTableModel). Elle permet de récupérer le nombre de 
	* colonnes du modèle.
	* Elle appelle la méthode de même nom sur le modèle source.
	* 
	* Retourne: Le nombre de colonnes de données du modèle.
	* ----------------------------------------------------------*/
	public int getColumnCount()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getColumnCount");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int column_count = 0;
			
		trace_methods.beginningOfMethod();
		// Si le modèle est non null, on récupère le nombre de colonnes
		if(_model != null)
		{
			column_count = _model.getColumnCount();
		}
		else
		{
			trace_debug.writeTrace("Le modèle n'est pas positionné !");
		}
		trace_methods.endOfMethod();
		return column_count;
	}

	/*----------------------------------------------------------
	* Nom: getColumnName
	* 
	* Description:
	* Cette méthode redéfinit celle de la classe AbstractTableModel. Elle 
	* permet de récupérer le nom de la colonne dont l'indice est passé en 
	* argument.
	* Elle appelle la méthode de même nom sur le modèle source.
	* 
	* Arguments:
	*  - column: L'indice de la colonne dont on souhaite récupérer le nom.
	* 
	* Retourne: Le nom de la colonne à l'indice column.
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
		// Si le modèle est non null, on récupère le nom de la colonne
		if(_model != null)
		{
			column_name = _model.getColumnName(column);
		}
		else
		{
			trace_debug.writeTrace("Le modèle n'est pas positionné !");
		}
		trace_methods.endOfMethod();
		return column_name;
	}

	/*----------------------------------------------------------
	* Nom: getColumnClass
	* 
	* Description:
	* Cette méthode redéfinit celle de la classe AbstractTableModel. Elle 
	* permet de récupérer la classe d'objet de la colonne dont l'indice est 
	* passé en argument.
	* Elle appelle la méthode de même nom sur le modèle source.
	* 
	* Arguments:
	*  - column: L'indice de la colonne dont on souhaite récupérer la classe 
	*    d'objet.
	* 
	* Retourne: La classe des objets situé à la colonne column.
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
		// Si le modèle est non null, on récupère le nom de la colonne
		if(_model != null)
		{
			column_class = _model.getColumnClass(column);
			// Si la classe retournée est Object.class, il s'agit sans
			// doute du modèle par défaut (DefaultTableModel)
			if(column_class == Object.class && _model.getRowCount() > 0)
			{
				// S'il y a au moins un rang, on va récupérer la classe
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
			trace_debug.writeTrace("Le modèle n'est pas positionné !");
		}
		trace_methods.endOfMethod();
		return column_class;
	}

	/*----------------------------------------------------------
	* Nom: isCellEditable
	* 
	* Description:
	* Cette méthode redéfinit celle de la classe AbstractTableModel. Elle 
	* permet de savoir si une cellule, dont le coordonnées sont passées en 
	* argument, peut être éditée ou non.
	* Elle appelle la méthode de même nom sur le modèle source.
	* 
	* Arguments:
	*  - row: Le numéro de rang de la cellule,
	*  - column: Le numéro de colonne de la cellule.
	* 
	* Retourne: true si la cellule est éditable, false sinon.
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
		// Si le modèle est non null, on récupère le caractère éditable
		// de la cellule
		if(_model != null)
		{
			is_editable = _model.isCellEditable(row, column);
		}
		else
		{
			trace_debug.writeTrace("Le modèle n'est pas positionné !");
		}
		trace_methods.endOfMethod();
		return is_editable;
	}

	/*----------------------------------------------------------
	* Nom: getValueAt
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface TableModel (implémentée par 
	* la classe AbstractTableModel). Elle permet de récupérer la valeur 
	* correspondant au rang row et à la colonne column.
	* La méthode effectue un mapping entre l'indice de rang de la vue (row), 
	* et l'indice du rang dans le modèle source.
	* 
	* Arguments:
	*  - row: L'indice du rang de la cellule dont on veut la valeur,
	*  - column: L'indice de la colonne de la cellule dont on veut la valeur.
	* 
	* Retourne: Une référence sur un Object correspondant à la valeur de la 
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
		// Si le modèle est non null, on récupère la valeur de la cellule
		if(_model != null)
		{
			if(row < _model.getRowCount())
			{
				// La valeur est récupérée après translation du numéro de rang
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
			trace_debug.writeTrace("Le modèle n'est pas positionné !");
		}
		//trace_methods.endOfMethod();
		return value;
	}

	/*----------------------------------------------------------
	* Nom: setValueAt
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface TableModel (implémentée par 
	* la classe AbstractTableModel). Elle permet de définir une nouvelle 
	* valeur pour la cellule correspondant au rang row et à la colonne column.
	* La méthode effectue un mapping entre l'indice de rang de la vue (row), 
	* et l'indice du rang dans le modèle source.
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
		// Si le modèle est non null, on positionne la valeur de la cellule
		if(_model != null)
		{
			// La valeur est positionnée après translation du numéro de rang
			int translated_row = modelIndex(row);
			_model.setValueAt(value, translated_row, column);
		}
		else
		{
			trace_debug.writeTrace("Le modèle n'est pas positionné !");
		}
		trace_methods.endOfMethod();
 	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: getHeaderRendererIcon
	* 
	* Description:
	* Cette méthode permet de récupérer une icône à utiliser dans l'affichage 
	* de l'entête de la colonne, dont l'indice est passé en argument, en 
	* fonction de son état de tri. Si la colonne est non triée, aucune icône 
	* ne sera retournée. Sinon, une flèche descendante ou ascendante sera 
	* retournée (grâce à la classe embarquée Arrow).
	* 
	* Arguments:
	*  - column: L'indice de la colonne pour laquelle on veut récupérer une 
	*    icône,
	*  - size: La taille de l'icône à retourner.
	* 
	* Retourne: Une référence sur un objet Icon à utiliser lors de l'affichage 
	* de l'entête de la colonne.
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
		// On va essayer de récupérer la directive pour la colonne
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
	* Cette méthode permet de récupérer un objet Comparator implémentant la 
	* comparaison entre deux valeurs d'une même colonne. Ce Comparator est 
	* nécessaire pour le tri des lignes du tableau.
	* 
	* Arguments:
	*  - column: L'indice de la colonne pour laquelle on veut récupérer un 
	*    comparateur.
	* 
	* Retourne: Une référence sur un objet Comparator à utiliser pour la 
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
		// On va commencer par récupérer la classe de la colonne
		Class column_class = getColumnClass(column);
		trace_debug.writeTrace("column_class=" + column_class);
		// On va récupérer le comparateur associé au type dans la table
		// des comparateurs
		comparator = (Comparator)_columnComparators.get(column_class);
		if(comparator == null)
		{
			// Il n'y a pas de comparateur associé au type, on va en retourner un
			// par défaut
			trace_debug.writeTrace("Aucun comparateur associé au type");
			// On va commencer par vérifier si la classe implémente l'interface
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
	* Cette méthode permet de récupérer un tableau d'entiers correspondant au 
	* mappage entre les rangs du modèle et ceux de la vue. Si le tri n'a pas 
	* été effectué, celui-ci est exécuté via un appel à la méthode 
	* getViewToModel().
	* 
	* Arguments:
	*  - sortIfRequired: Un booléen indiquant si le tri doit être effectué si 
	*    cela n'a pas déjà été fait (true) ou non (false).
	* 
	* Retourne: Un tableau d'entiers correspondant au mappage entre les rangs 
	* du modèle et ceux de la vue.
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
		// données, on le fait
		if(_modelToView == null && sortIfRequired == true)
		{
			// La ligne suivante provoquera l'exécution du tri
			int length = getViewToModel().length;
			_modelToView = new int[length];
			// On va enregistrer les données de translation dans la table
			for(int index = 0 ; index < length ; index ++)
			{
				// L'appel à la méthode modelIndex va convertir l'indice de 
				// la vue (index) en celui du modèle
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
	* Cette méthode permet de ré-initialiser le tri des données. Cela implique 
	* que le tri sera ré-effectué lors du prochain accès aux données du modèle.
	* ----------------------------------------------------------*/
	protected synchronized void clearSortingState()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "clearSortingState");
			
		trace_methods.beginningOfMethod();
		// On va ré-initialiser les tables de translation
		_viewToModel = null;
		_modelToView = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: cancelSorting
	* 
	* Description:
	* Cette méthode permet d'annuler tout tri dans les données. L'annulation 
	* est effectuée en supprimant toute colonne dans la liste des colonnes à 
	* trier, et efface le résultat du tri.
	* ----------------------------------------------------------*/
	protected synchronized void cancelSorting()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "cancelSorting");
			
		trace_methods.beginningOfMethod();
		// On va supprimer toutes les colonnes de la liste des colonnes triées
		_sortingColumns.clear();
		// On va signaler un changement dans les états de tri
		sortingStatusChanged();
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: Row
	* 
	* Description:
	* Cette classe embarquée est chargée d'encapsuler le mappage entre un 
	* indice de rang du modèle source et celui de la vue. De plus, elle 
	* implémente l'interface Comparable afin de permettre le tri des colonnes 
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
		* Ce constructeur permet de définir un Row à partir de l'indice de 
		* rang dans le modèle source.
		* 
		* Arguments:
		*  - modelIndex: L'indice de rang dans le modèle source.
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
		* Cette méthode redéfinit celle de l'interface Comparable. Elle permet 
		* de comparer l'objet courant avec un objet passé en argument.
		* Dans le cas présent, la méthode est chargée de comparer la ligne 
		* représentée par l'objet courant, avec la ligne représentée par 
		* l'objet Row passé en argument.
		* La comparaison est principalement effectuée par le biais d'un 
		* Comparator définit pour la colonne (voir la méthode getComparator()).
		* 
		* Arguments:
		*  - object: Un objet à comparer avec l'objet courant.
		* 
		* Retourne : 0 si les deux objets ont une valeur identiques, -1 si 
		* l'objet courant a une valuer inférieure, et 1 si l'objet courant à 
		* une valeur supérieure.
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
			// On commence par vérifier que l'objet passé en argument est de 
			// type Row
			if(!(object instanceof Row))
			{
				trace_errors.writeTrace("L'objet n'est pas de type Row !");
				// On sort
				trace_methods.endOfMethod();
				return 0;
			}
			// On va récupérer l'indice du modèle associé au Row passé en
			// argument
			int row1 = _modelIndex;
			int row2 = ((Row)object)._modelIndex;
			trace_debug.writeTrace("row1=" + row1);
			trace_debug.writeTrace("row2=" + row2);
			// On va itérer sur les colonnes triées
			TableModel model = getModel();
			Iterator iterator = getSortingColumns();
			while(iterator.hasNext() == true)
			{
				int comparison = 0;
				
				// On récupère la directive
				SortDirective directive = (SortDirective)iterator.next();
				// On récupère le numéro de colonne associé à la directive
				int column = directive.getColumn();
				int direction = directive.getDirection();
				trace_debug.writeTrace("column=" + column + ",direction=" + 
					direction);
				// On va récupérer les valeurs pour les deux rangs
				Object object1 = model.getValueAt(row1, column);
				Object object2 = model.getValueAt(row2, column);
				// On va tout d'abord comparer les valeurs nulles
				if(object1 == null && object2 == null)
				{
					// On considère les valeurs comme identiques
					comparison = 0;
				}
				else if(object1 == null)
				{
					// On considère null comme inférieur à toute valeur
					comparison = -1;
				}
				else if(object2 == null)
				{
					comparison = 1;
				}
				else
				{
					// On va utiliser le comparateur associé à la colonne
					Comparator comparator = getComparator(column);
					comparison = comparator.compare(object1, object2);
				}
				trace_debug.writeTrace("comparison=" + comparison);
				// Si la comparaison est différente de 0 (valeurs différentes), 
				// ce n'est pas nécessaire de poursuivre
				if(comparison != 0)
				{
					if(direction == DESCENDING)
					{
						// Le tri est descendant, on inverse la valeur du résultat
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
		* Cette méthode permet de récupérer l'indice de rang dans le modèle 
		* source associé au Row.
		* 
		* Retourne: L'indice du rang dans le modèle source.
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
		* Cet attribut maintient l'indice de rang du modèle source 
		* correspondant à cette instance de Row.
		* ----------------------------------------------------------*/
		private int _modelIndex;
	};

	/*----------------------------------------------------------
	* Nom: TableModelHandler
	* 
	* Description:
	* Cette classe embarquée est chargée de gérer les événements concernant le 
	* modèle source. Pour cela, elle implémente l'interface TableModelListener.
	* Tous les événements reçus du modèle sont examinés, parfois manipulés, 
	* puis passés aux listeners du modèle (typiquement la table elle-même). Si 
	* un changement du modèle a invalidé l'ordre des rangs du tri, cela est 
	* pris en compte et le tri sera de nouveau effectué la prochaine fois 
	* qu'une donnée sera accédée.
	* ----------------------------------------------------------*/
	private class TableModelHandler
		implements TableModelListener
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: TableModelHandler
		* 
		* Description:
		* Constructeur par défaut. Il n'est présenté que pour des raisons de 
		* lisibilité.
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
		* Cette méthode redéfinit celle de l'interface TableModelListener. 
		* Elle est appelée à chaque fois qu'un événement survient sur le 
		* modèle source.
		* Si aucun tri n'est en cours, l'événement est retransmis aux 
		* listeners.
		* Si la structure de la table a changée, on annule le tri.
		* Un événement de cellule est émis dans un cas bien précis, permettant 
		* de ne pas retrier systématiquement lorsque cela n'est pas nécessaire.
		* Sinon, les données doivent être retriées, et l'événement est 
		* retransmis.
		* 
		* Arguments:
		*  - event: L'événement de changement du modèle source.
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
			// retransmet directement l'événement
			if(isSorting() == false)
			{
				trace_debug.writeTrace("Pas de tri");
				// On va effacer le résultat de tri (au cas où)
				clearSortingState();
				fireTableChanged(event);
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// S'il s'agit d'un événement de structure, on va annuler le tri, 
			// des colonnes pouvant avoir été ajoutées ou supprimées depuis
			// le modèle
			if(event.getFirstRow() == TableModelEvent.HEADER_ROW)
			{
				trace_debug.writeTrace("Modification de structure du modèle");
				cancelSorting();
				fireTableChanged(event);
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// On peut éventuellement créer un événement de cellule afin 
			// d'éviter de relancer un tri systématique lorsqu'une cellule
			// non triée est modifiée. Il faut alors que les conditions 
			// suivantes soient vérifiées:
			//  1. Les modifications ne s'appliquent qu'à un seul rang,
			//  2. Les modifications ne s'appliquent qu'à une seule colonne,
			//  3. Il n'y a pas de tri sur cette colonne,
			//  4. Une recherche inversée ne donnera pas lieu à un tri.
			int column = event.getColumn();
			int[] model_to_view = getModelToView(false);
			if(event.getFirstRow() == event.getLastRow() && 
				column != TableModelEvent.ALL_COLUMNS &&
				getSortingStatus(column) == NOT_SORTED &&
				model_to_view != null)
			{
				trace_debug.writeTrace("Evénement de cellule");
				// On va récupérer l'indice du rang dans la vue
				int view_index = model_to_view[event.getFirstRow()];
				// On va créer un événement de cellule
				fireTableChanged(new TableModelEvent(SortedTableModel.this,
					view_index, view_index, column, event.getType()));
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// Sinon, quelque chose est arrivé aux données qui pourrait avoir
			// invalidé le résultat du tri. On va effacer le résultat pour que 
			// le tri soit de nouveau effectué lors du prochain accès aux données
			trace_debug.writeTrace("Evénement de données");
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
	* Cette classe embarquée est chargée de gérer les événements de la souris 
	* sur l'entête du tableau. Pour cela, elle redéfinit la classe 
	* MouseAdapter.
	* Elle permet de positionner l'état de tri pour la colonne sélectionnée de 
	* manière cyclique entre les valeurs NOT_SORTED, DESCENDING et ASCENDING.
	* ----------------------------------------------------------*/
	private class MouseHandler
		extends MouseAdapter
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: MouseHandler
		* 
		* Description:
		* Constructeur par défaut. Il n'est présenté que pour des raisons de 
		* lisibilité.
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
		* Cette méthode redéfinit celle de la super-classe MouseAdapter. Elle 
		* est appelée à chaque fois qu'un click de souris est effectué sur 
		* l'entête du tableau.
		* Elle récupérer l'indice de la colonne sur laquelle le click a eu 
		* lieu, et positionne l'état de tri en effectuant un cycle sur les 
		* valeurs NOT_SORTED, DESCENDING, et ASCENDING.
		* 
		* Arguments:
		*  - event: L'événement de click de souris.
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
			// On va récupérer l'entête de table sur lequel l'événement a eu 
			// lieu, puis l'indice de la colonne sélectionnée
			JTableHeader table_header = (JTableHeader)event.getSource();
			TableColumnModel column_model = table_header.getColumnModel();
			int view_column = column_model.getColumnIndexAtX(event.getX());
			trace_debug.writeTrace("view_column=" + view_column);
			// On va récupérer l'indice réel dans le modèle de la colonne
			int model_column = 
				column_model.getColumn(view_column).getModelIndex();
			trace_debug.writeTrace("model_column=" + model_column);
			// Si aucune colonne n'a été sélectionnée, on sort
			if(model_column == NO_SELECTION)
			{
				trace_methods.endOfMethod();
				return; 
			}
			// On va récupérer l'état de tri de la colonne
			int status = getSortingStatus(model_column);
			// Si la touche CTRL n'est pas enfoncée, on va définir une
			// nouvelle liste de colonnes
			if(event.isControlDown() == false)
			{
				cancelSorting();
			}
			else
			{
				trace_debug.writeTrace("Touche CTRL enfoncée");
			}
			// On va maintenant modifier la valeur de l'état en fonction de
			// l'état de la touche SHIFT
			int offset = 1;
			if(event.isShiftDown() == true)
			{
				trace_debug.writeTrace("Touche SHIFT enfoncée");
				offset = -1;
			}
			status += offset;
			// La ligne suivante permet de s'assurer que l'on obtient
			// une valeur comprise entre -1 et 1.
			status = (status + 4) % 3 - 1;
			trace_debug.writeTrace("status=" + status);
			// On va redéfinir l'état de tri de la colonne
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
	* Cette classe embarquée est chargée de la représentation de l'état de tri 
	* des colonnes du tableau. Elle implémente l'interface Icon afin d'être 
	* utilisée lors de l'affichage des entêtes de colonnes.
	* ----------------------------------------------------------*/
	private class Arrow
		implements Icon
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: Arrow
		* 
		* Description:
		* Cette méthode permet de construire un objet Arrow en lui indiquant 
		* le mode de tri des données de la colonne, la dimension que l'icône 
		* doit avoir, et enfin la priorité de la colonne dans le tri global.
		* 
		* Arguments:
		*  - descending: Un booléen indiquant si le tri des données est 
		*    descendant (true) ou non (false),
		*  - size: La taille de la zone d'affichage de l'icône d'état,
		*  - priority: La priorité de la colonne dans le tri global.
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
		* Cette méthode redéfinit celle de l'interface Icon. Elle est appelée 
		* à chaque fois que l'icône doit être déssinée.
		* Le dessin de l'icône dépend du mode de tri (orientation de la 
		* flèche), de la taille de la zone d'affichage, et enfin de la 
		* priorité de la colonne dans le tri global (moins la priorité est 
		* importante et moins la flèche est grande).
		* 
		* Arguments:
		*  - component: Une référence sur un objet Component permettant 
		*    éventuellement de récupérer des propriétés telles que la couleur 
		*    de fond,
		*  - graphics: Une référence sur un objet Graphics permettant le 
		*    dessin 2D,
		*  - x: La composante horizontale des coordonnées du point de dessin,
		*  - y: La composante verticale des coordonnées du point de dessin.
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
			// On récupère la couleur de fond du composant
			if(component != null)
			{
				color = component.getBackground();
			}
			// Dans le cas d'un tri sur plusieurs colonnes, chaque flèche
			// sera 20% plus petit que le précédent
			int dx = (int)(_size / 2 * Math.pow(0.8, _priority));
			int dy = dx;
			if(_descending == true)
			{
				dy = -dx;
			}
			// On va aligner grossièrement la flèche avec la ligne de base de
			// la police de caractères
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
		* Cette méthode redéfinit celle de l'interface Icon. Elle permet de 
		* récupérer la largeur de l'icône.
		* 
		* Retourne: La largeur de l'icône.
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
		* Cette méthode redéfinit celle de l'interface Icon. Elle permet de 
		* récupérer la hauteur de l'icône.
		* 
		* Retourne: La hauteur de l'icône.
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
		* Cet attribut maintient un booléen indiquant si l'état de tri est 
		* descendant (true) ou non (false).
		* ----------------------------------------------------------*/
		private boolean _descending;

		/*----------------------------------------------------------
		* Nom: _size
		* 
		* Description:
		* Cet attribut correspond à la dimension que doit avoir l'icône pour 
		* obtenir un affichage optimal de l'indicateur d'état de tri dans 
		* l'entête de colonne.
		* ----------------------------------------------------------*/
		private int _size;

		/*----------------------------------------------------------
		* Nom: _priority
		* 
		* Description:
		* Cet attribut maintient une valeur correspondant à la priorité de la 
		* colonne dans le tri des données du tableau. Plus la valeur est 
		* petite, et plus la colonne est prioritaire. Il est utilisé dans le 
		* calcul de la dimension de l'icône d'état de tri.
		* ----------------------------------------------------------*/
		private int _priority;
	};

	/*----------------------------------------------------------
	* Nom: SortableHeaderRenderer
	* 
	* Description:
	* Cette classe embarquée est chargée de gérer le rendu des entêtes de 
	* colonnes du tableau. Pour cela, elle implémente l'interface 
	* TableCellRenderer, afin d'être associées aux cellules représentant les 
	* entêtes.
	* Elle utilise un autre objet TableCellRenderer pour la majeure partie du 
	* travail de rendu. Elle y associe éventuellement une icône (voir la 
	* classe Arrow) dans le cas où la colonne à rendre soit incluse dans la 
	* liste des colonnes triées.
	* ----------------------------------------------------------*/
	private class SortableHeaderRenderer
		implements TableCellRenderer
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: SortableHeaderRenderer
		* 
		* Description:
		* Cette méthode permet de créer l'instance de SortableHeaderRenderer en 
		* lui fournissant une référence sur un objet TableCellRenderer 
		* correspondant au gestionnaire de rendu par défaut.
		* 
		* Arguments:
		*  - tableCellRenderer: Une référence sur le gestionnaire de rendu par 
		*    défaut de l'entête de colonne.
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
		* Cette méthode redéfinit celle de l'interface TableCellRenderer. Elle 
		* est appelée lorsqu'une cellule du tableau doit être rendue 
		* (affichée).
		* Elle va appeler la méthode de même nom sur le gestionnaire par 
		* défaut, puis éventuellement y associer une icône correspondant à 
		* l'état de tri si la colonne à rendre fait partie des colonnes triées.
		* 
		* Arguments:
		*  - table: Une référence sur la JTable dans laquelle la cellule doit 
		*    être rendue,
		*  - value: Un objet correspondant à la valeur de la cellule à rendre,
		*  - isSelected: Un booléen indiquant si la cellule est sélectionnée,
		*  - hasFocus: Un booléen indiquant si la cellule a le focus,
		*  - row: Le numéro de rang de la cellule à rendre,
		*  - column: Le numéro de colonne de la cellule à rendre.
		* 
		* Retourne: Une référence sur un objet Component permettant le rendu 
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
			// S'il n'y a pas de gestionnaire de rendu par défaut, on sort
			if(_tableCellRenderer == null)
			{
				trace_methods.endOfMethod();
				return component;
			}
			// On va récupérer le composant permettant le rendu via le 
			// gestionnaire par défaut
			component = _tableCellRenderer.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
			// Si le composant est un JLabel, on pourra éventuellement lui
			// ajouter une icône
			if(component instanceof JLabel)
			{
				JLabel label = (JLabel)component;
				// On va ajuster l'alignement à gauche
				label.setHorizontalTextPosition(JLabel.LEFT);
				// On va récupérer le numéro de colonne dans le modèle
				int model_column = table.convertColumnIndexToModel(column);
				// On va positionner une icône en fonction de l'indice de la 
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
		* Cette méthode permet de récupérer l'objet TableCellRenderer qui 
		* était positionné par défaut sur l'entête de la table.
		* 
		* Retourne: Une référence sur un objet TableCellRenderer.
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
		* Cet attribut maintient une référence sur un objet TableCellRenderer 
		* correspondant au gestionnaire de rendu par défaut pour l'entête de 
		* colonne dans le tableau.
		* ----------------------------------------------------------*/
		private TableCellRenderer _tableCellRenderer;
	};

	/*----------------------------------------------------------
	* Nom: SortDirective
	* 
	* Description:
	* Cette classe embarquée est chargée d'encapsuler une directive de tri 
	* pour une colonne. La directive de tri spécifie la direction (ascendante 
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
		* colonne dont l'indice est passé en argument en spécifiant la 
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
		* Cette méthode permet de récupérer l'indice de la colonne sur 
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
		* Cette méthode permet de récupérer la direction du tri correspondant 
		* à cette directive.
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
		* Cette méthode permet de spécifier une nouvelle direction du tri pour 
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
		* Cet attribut maintient un entier correspondant à l'indice de la 
		* colonne pour laquelle cette directive s'applique.
		* ----------------------------------------------------------*/
		private int _column;

		/*----------------------------------------------------------
		* Nom: _direction
		* 
		* Description:
		* Cet attribut maintient un entier correspondant à la direction du tri 
		* pour cette directive. Elle peut prendre les valeurs ASCENDING, 
		* DESCENDING et NOT_SORTED.
		* ----------------------------------------------------------*/
		private int _direction;
	};

	/*----------------------------------------------------------
	* Nom: EMPTY_DIRECTIVE
	* 
	* Description:
	* Cette valeur constante est une référence sur un objet SortDirective 
	* correspondant à une directive vide.
	* ----------------------------------------------------------*/
	private static final SortDirective EMPTY_DIRECTIVE = 
		new SortDirective(NO_SELECTION, NOT_SORTED);

	/*----------------------------------------------------------
	* Nom: _modelToView
	* 
	* Description:
	* Cet attribut maintient un tableau d'entiers permettant le mappage direct 
	* entre les indices du modèle source vers les indices de la vue.
	* ----------------------------------------------------------*/
	private int[] _modelToView;

	/*----------------------------------------------------------
	* Nom: _model
	* 
	* Description:
	* Cet attribut maintient une référence sur le modèle des données source. 
	* Il est nécessaire afin d'accéder aux éléments du modèle après 
	* translation des indices de rang.
	* ----------------------------------------------------------*/
	private TableModel _model;

	/*----------------------------------------------------------
	* Nom: _viewToModel
	* 
	* Description:
	* Cet attribut est un tableau de Row correspondant au tableau de mappage 
	* entre les indices de rangs de la vue et ceux du modèle.
	* ----------------------------------------------------------*/
	private Row[] _viewToModel;

	/*----------------------------------------------------------
	* Nom: _tableHeader
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet JTableHeader 
	* correspondant à l'entête du tableau.
	* ----------------------------------------------------------*/
	private JTableHeader _tableHeader;

	/*----------------------------------------------------------
	* Nom: _mouseListener
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet MouseListener 
	* correspondant au gestionnaire de souris qui doit être associé à 
	* l'entête du tableau afin de réagir aux clicks de souris.
	* ----------------------------------------------------------*/
	private MouseListener _mouseListener;

	/*----------------------------------------------------------
	* Nom: _modelListener
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet TableModelListener qui 
	* doit être associé au modèle afin d'être informé de chaque changement au 
	* niveau du modèle source.
	* ----------------------------------------------------------*/
	private TableModelListener _modelListener;

	/*----------------------------------------------------------
	* Nom: _columnComparators
	* 
	* Description:
	* Cet attribut maintient une référence sur une HashMap correspondant à la 
	* liste des comparateurs pouvant être utilisés pour la comparaison des 
	* colonnes d'un tableau. La clé de la table est la classe des objets, et 
	* la valeur est une instance de Comparator à utiliser pour la comparaison.
	* ----------------------------------------------------------*/
	private HashMap _columnComparators;

	/*----------------------------------------------------------
	* Nom: _sortingColumns
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet ArrayList 
	* correspondant à la liste des colonnes servant au tri.
	* ----------------------------------------------------------*/
	private ArrayList _sortingColumns;

	/*----------------------------------------------------------
	* Nom: getSortDirective
	* 
	* Description:
	* Cette méthode permet de récupérer la directive de tri pour la colonne 
	* dont l'indice est passé en argument.
	* Si la colonne n'a pas de directive, la directive EMPTY_DIRECTIVE est 
	* retournée.
	* 
	* Arguments:
	*  - column: L'indice de la colonne pour laquelle on veut récupérer la 
	*    directive de tri.
	* 
	* Retourne: Une référence sur un objet SortDirective correspondant à la 
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
		// On va rechercher dans la liste des colonnes triées celle qui
		// correspond à la même colonne
		for(int index = 0 ; index < _sortingColumns.size() ; index ++)
		{
			SortDirective directive = 
				(SortDirective)_sortingColumns.get(index);
			if(directive.getColumn() == column)
			{
				trace_debug.writeTrace("Directive trouvée");
				// On va la retourner
				trace_methods.endOfMethod();
				return directive; 
			}
		}
		// Si on arrive ici, c'est que l'on n'a pas trouvé de directive
		// pour la colonne. On va retourner la directive vide.
		trace_debug.writeTrace("Directive non trouvée");
		trace_methods.endOfMethod();
		return EMPTY_DIRECTIVE;
	}

	/*----------------------------------------------------------
	* Nom: sortingStatusChanged
	* 
	* Description:
	* Cette méthode est destinée à être appelée à chaque fois qu'un état de 
	* tri a changé. Elle ré-initialise le tri, émet un événement de changement 
	* des données et commande le ré-affichage de l'entête du tableau.
	* ----------------------------------------------------------*/
	private void sortingStatusChanged()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "sortingStatusChanged");
			
		trace_methods.beginningOfMethod();
		// On va ré-initialiser le résultat du tri
		clearSortingState();
		// On va signaler un changement des données (pour réaffichage)
		fireTableDataChanged();
		// On va forcer l'entête à se réafficher, s'il y en a un
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
	* Cette méthode permet de récupérer un tableau de Row correspondant au 
	* mappage entre les rangs de la vue et ceux du modèle. Si le tri n'a pas 
	* été effectué au préalable, il est executé.
	* 
	* Retourne: Un tableau de Row correspondant au mappage entre les rangs de 
	* la vue et ceux du modèle.
	* ----------------------------------------------------------*/
	private synchronized Row[] getViewToModel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortedTableModel", "getViewToModel");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
			
		trace_methods.beginningOfMethod();
		// S'il n'y pas de tableau de translation, on va le créer
		if(_viewToModel == null)
		{
			trace_debug.writeTrace("Re-création du tableau de translation");
			int row_count = _model.getRowCount();
			// On va instancier le tableau
			_viewToModel = new Row[row_count];
			// On va remplir le tableau avec les valeurs par défaut
			for(int index = 0 ; index < row_count ; index ++)
			{
				_viewToModel[index] = new Row(index);
			}
			// S'il y a un tri à effectuer, on va le lancer
			if(isSorting() == true)
			{
				trace_debug.writeTrace("Exécution du tri");
				// On va utiliser la méthode sort() de la classe Arrays
				Arrays.sort(_viewToModel);
			}
		}
		trace_methods.endOfMethod();
		return _viewToModel;
	}
}
