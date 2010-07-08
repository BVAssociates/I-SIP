/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/NonEditableTable.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation de table non �ditable
* DATE:        18/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: NonEditableTable.java,v $
* Revision 1.10  2009/01/14 14:18:24  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.gui.
*
* Revision 1.9  2006/11/03 10:29:12  tz
* La classe d�rive de SearchableTable.
*
* Revision 1.8  2005/10/12 14:25:32  tz
* G�n�ration d'un fichier XSD pour les fichiers XML.
*
* Revision 1.7  2005/10/07 08:17:39  tz
* Impl�mentation de l'interface SearchableComponentInterface.
* Traitement des formats csv et xml.
*
* Revision 1.6  2005/07/01 12:03:59  tz
* Modification du composant pour les traces
* Ajout des m�thode setLastSearchPosition(), getLastSearchPosition(),
* getSeparator() et getSelectedRow().
*
* Revision 1.5  2004/11/03 15:16:18  tz
* Ajout du mode de s�lection par d�faut.
*
* Revision 1.4  2004/11/02 08:40:24  tz
* Ajout d'un nouveau constructeur
*
* Revision 1.3  2004/10/13 13:53:34  tz
* Renommage du package inuit -> isis
*
* Revision 1.2  2002/04/05 15:50:07  tz
* Cloture it�ration IT1.2
*
* Revision 1.1  2002/03/27 09:42:36  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.gui;

//
// Imports syst�me
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
* Cette classe est une sp�cialisation de la classe SearchableTable afin de 
* cr�er un tableau qui ne puisse pas �tre �dit�.
* Pour cela, elle red�fini la m�thode isCellEditable().
* ----------------------------------------------------------*/
public class NonEditableTable
	extends SearchableTable
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NonEditableTable
	*
	* Description:
	* Cette m�thode est l'un des constructeurs de la classe. Elle permet de
	* construire un tableau � partir d'un tableau (� deux dimensions) de
	* valeurs et d'un tableau repr�sentant les intitul�s des colonnes.
	*
	* Arguments:
	*  - data: Un tableau � deux dimensions contenant les valeurs � afficher
	*    dans le tableau,
	*  - columns: Un tableau � une dimension contenant les intitul�s des
	*    colonnes,
	*  - separator: Le s�parateur de la d�finition de la table. 
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
	* Cette m�thode est l'un des constructeurs de la classe. Elle permet de
	* construire un tableau � partir d'un mod�le de donn�es et d'un mod�le de
	* colonnes.
	*
	* Arguments:
	*  - dataModel: Une r�f�rence sur un objet TableModel d�finissant le mod�le
	*    des donn�es,
	*  - columnModel: Une r�f�rence sur un objet TableColumnModel d�finissant
	*    le mod�le des colonnes,
	*  - separator: Le s�parateur de la d�finition de la table.
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
	* Cette m�thode est l'un des constructeurs de la classe. Elle permet de 
	* construire un tableau � partir d'un mod�le de donn�es.
	* 
	* Arguments:
	*  - dataModel: Une r�f�rence sur un objet TableModel d�finissant le 
	*    mod�le des donn�es et le mod�le des colonnes,
	*  - separator: Le s�parateur de la d�finition de la table.
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
	* Cette m�thode est l'un des constructeurs de la classe. Elle permet de 
	* construire un tableau � partir d'un vecteur contenant les donn�es et 
	* d'un vecteur contenant les noms des colonnes.
	* 
	* Arguments:
	*  - rowData: Une r�f�rence sur un objet Vector contenant les donn�es,
	*  - columnNames: Une r�f�rence sur un objet Vector contenant les noms 
	*    des colonnes,
	*  - separator: Le s�parateur de la d�finition de la table.
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
	* Cette m�thode red�fini celle de la super-classe. Elle permet de savoir si
	* une cellule peut �tre �dit�e ou non.
	* Elle retourne syst�matiquement false afin de rendre le tableau non
	* �ditable.
	*
	* Arguments:
	*  - row: Num�ro de rang de la cellule concern�e,
	*  - column: Num�ro de colonne de la cellule concern�e.
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