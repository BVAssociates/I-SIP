/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/NonEditableTable.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation de table non éditable
* DATE:        18/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: NonEditableTable.java,v $
* Revision 1.10  2009/01/14 14:18:24  tz
* Classe déplacée dans le package com.bv.isis.console.core.gui.
*
* Revision 1.9  2006/11/03 10:29:12  tz
* La classe dérive de SearchableTable.
*
* Revision 1.8  2005/10/12 14:25:32  tz
* Génération d'un fichier XSD pour les fichiers XML.
*
* Revision 1.7  2005/10/07 08:17:39  tz
* Implémentation de l'interface SearchableComponentInterface.
* Traitement des formats csv et xml.
*
* Revision 1.6  2005/07/01 12:03:59  tz
* Modification du composant pour les traces
* Ajout des méthode setLastSearchPosition(), getLastSearchPosition(),
* getSeparator() et getSelectedRow().
*
* Revision 1.5  2004/11/03 15:16:18  tz
* Ajout du mode de sélection par défaut.
*
* Revision 1.4  2004/11/02 08:40:24  tz
* Ajout d'un nouveau constructeur
*
* Revision 1.3  2004/10/13 13:53:34  tz
* Renommage du package inuit -> isis
*
* Revision 1.2  2002/04/05 15:50:07  tz
* Cloture itération IT1.2
*
* Revision 1.1  2002/03/27 09:42:36  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.gui;

//
// Imports système
//
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.util.Vector;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: NonEditableTable
* 
* Description:
* Cette classe est une spécialisation de la classe SearchableTable afin de 
* créer un tableau qui ne puisse pas être édité.
* Pour cela, elle redéfini la méthode isCellEditable().
* ----------------------------------------------------------*/
public class NonEditableTable
	extends SearchableTable
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NonEditableTable
	*
	* Description:
	* Cette méthode est l'un des constructeurs de la classe. Elle permet de
	* construire un tableau à partir d'un tableau (à deux dimensions) de
	* valeurs et d'un tableau représentant les intitulés des colonnes.
	*
	* Arguments:
	*  - data: Un tableau à deux dimensions contenant les valeurs à afficher
	*    dans le tableau,
	*  - columns: Un tableau à une dimension contenant les intitulés des
	*    colonnes,
	*  - separator: Le séparateur de la définition de la table. 
	* ----------------------------------------------------------*/
	public NonEditableTable(
		Object[][] data,
		Object[] columns,
		String separator
		)
	{
		super(data, columns, separator);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NonEditableTable", "NonEditableTable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);
		trace_arguments.writeTrace("columns=" + columns);
		trace_arguments.writeTrace("separator=" + separator);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: NonEditableTable
	*
	* Description:
	* Cette méthode est l'un des constructeurs de la classe. Elle permet de
	* construire un tableau à partir d'un modèle de données et d'un modèle de
	* colonnes.
	*
	* Arguments:
	*  - dataModel: Une référence sur un objet TableModel définissant le modèle
	*    des données,
	*  - columnModel: Une référence sur un objet TableColumnModel définissant
	*    le modèle des colonnes,
	*  - separator: Le séparateur de la définition de la table.
 	* ----------------------------------------------------------*/
	public NonEditableTable(
		TableModel dataModel,
		TableColumnModel columnModel,
		String separator
		)
	{
		super(dataModel, columnModel, separator);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NonEditableTable", "NonEditableTable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("dataModel=" + dataModel);
		trace_arguments.writeTrace("columnModel=" + columnModel);
		trace_arguments.writeTrace("separator=" + separator);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: NonEditableTable
	* 
	* Description:
	* Cette méthode est l'un des constructeurs de la classe. Elle permet de 
	* construire un tableau à partir d'un modèle de données.
	* 
	* Arguments:
	*  - dataModel: Une référence sur un objet TableModel définissant le 
	*    modèle des données et le modèle des colonnes,
	*  - separator: Le séparateur de la définition de la table.
 	* ----------------------------------------------------------*/
	public NonEditableTable(
		TableModel dataModel,
		String separator
		)
	{
		super(dataModel, separator);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NonEditableTable", "NonEditableTable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("dataModel=" + dataModel);
		trace_arguments.writeTrace("separator=" + separator);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: NonEditableTable
	* 
	* Description:
	* Cette méthode est l'un des constructeurs de la classe. Elle permet de 
	* construire un tableau à partir d'un vecteur contenant les données et 
	* d'un vecteur contenant les noms des colonnes.
	* 
	* Arguments:
	*  - rowData: Une référence sur un objet Vector contenant les données,
	*  - columnNames: Une référence sur un objet Vector contenant les noms 
	*    des colonnes,
	*  - separator: Le séparateur de la définition de la table.
 	* ----------------------------------------------------------*/
	public NonEditableTable(
		Vector rowData,
		Vector columnNames,
		String separator
		)
	{
		super(rowData, columnNames, separator);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NonEditableTable", "NonEditableTable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("rowData=" + rowData);
		trace_arguments.writeTrace("columnNames=" + columnNames);
		trace_arguments.writeTrace("separator=" + separator);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isCellEditable
	*
	* Description:
	* Cette méthode redéfini celle de la super-classe. Elle permet de savoir si
	* une cellule peut être éditée ou non.
	* Elle retourne systématiquement false afin de rendre le tableau non
	* éditable.
	*
	* Arguments:
	*  - row: Numéro de rang de la cellule concernée,
	*  - column: Numéro de colonne de la cellule concernée.
	*
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isCellEditable(
		int row,
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NonEditableTable", "isCellEditable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("row=" + row);
		trace_arguments.writeTrace("column=" + column);
		trace_methods.endOfMethod();
		return false;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}