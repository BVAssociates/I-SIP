/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/define/SortCellRenderer.java,v $
* $Revision: 1.4 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire d'affichage des ordres de tri
* DATE:        26/08/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.define
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SortCellRenderer.java,v $
* Revision 1.4  2005/07/01 12:19:08  tz
* Modification du composant pour les traces
*
* Revision 1.3  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.2  2004/10/13 13:56:36  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.1  2004/10/06 07:34:41  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.define;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;
import javax.swing.JLabel;
import com.bv.core.gui.IconLoader;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: SortCellRenderer
*
* Description:
* Cette classe est une sp�cialisation de la classe DefaultTableCellRenderer
* charg�e de l'affichage des cellules contenant des ordres de tri.
* Une ic�ne est utilis�e pour indiquer le caract�re ascendant ou descandant
* du tri, et une valeur num�rique indique le num�ro d'ordre.
* ----------------------------------------------------------*/
public class SortCellRenderer
	extends DefaultTableCellRenderer
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: SortCellRenderer
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public SortCellRenderer()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SortCellRenderer", "SortCellRenderer");

		trace_methods.beginningOfMethod();
		setHorizontalAlignment(JLabel.CENTER);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTableCellRendererComponent
	*
	* Description:
	* Cette m�thode red�fini celle de la super-classe. Elle est appel�e �
	* chaque fois qu'une cellule doit �tre affich�e.
	* Elle permet de r�cup�rer le composant graphique charg� de l'affichage de
	* la cellule. Dans le cas pr�sent, il s'agit d'un JLabel contenant une 
	* ic�ne d�pendant du caract�re ascendant ou descendant du tri, ainsi qu'un
	* chiffre indiquant l'ordre de la colonne dans le tri.
	*
	* Arguments:
	*  - table: La table contenant les cellules � afficher,
	*  - value: La valeur � afficher dans la cellule,
	*  - isSelected: indique si la cellule est s�lectionn�e,
	*  - hasFocus: indique si la cellule a le focus,
	*  - row: indique le num�ro de rang de la cellule,
	*  - column: indique le num�ro de colonne de la cellule.
	*
	* Retourne: Une instance de composant graphique charg� de l'affichage de la
	* cellule.
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
		// Les traces sont d�sactiv�es, cette m�thode �tant appel�e trop souvent
		/*
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BooleanCellRenderer", "getTableCellRendererComponent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("table=" + table);
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("isSelected=" + isSelected);
		trace_arguments.writeTrace("hasFocus=" + hasFocus);
		trace_arguments.writeTrace("row=" + row);
		trace_arguments.writeTrace("column=" + column);
		*/
		// On appelle la m�thode de la super classe
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
			row, column);
		// Si la valeur n'est pas de type Integer, on sort
		if(!(value instanceof Integer))
		{
			//trace_methods.endOfMethod();
			return this;
		}
		Integer the_value = (Integer)value;
		// Si la valeur vaut 0, il n'y a pas de tri
		if(the_value.intValue() == 0)
		{
			setText("");
			setIcon(null);
		}
		// Si la valeur est n�gative, on utilise l'ic�ne descendant
		else if(the_value.intValue() < 0)
		{
			setText("" + -1 * the_value.intValue());
			setIcon(IconLoader.getIcon("DESC"));
		}
		// On utilise l'ic�ne ascendant
		else
		{
			setIcon(IconLoader.getIcon("ASC"));
		}
		//trace_methods.endOfMethod();
		return this;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}