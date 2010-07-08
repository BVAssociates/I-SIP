/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
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
* Mise au propre des traces de méthodes.
*
* Revision 1.2  2004/10/13 13:56:36  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.1  2004/10/06 07:34:41  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.define;

//
// Imports système
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
* Cette classe est une spécialisation de la classe DefaultTableCellRenderer
* chargée de l'affichage des cellules contenant des ordres de tri.
* Une icône est utilisée pour indiquer le caractère ascendant ou descandant
* du tri, et une valeur numérique indique le numéro d'ordre.
* ----------------------------------------------------------*/
public class SortCellRenderer
	extends DefaultTableCellRenderer
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: SortCellRenderer
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présentée que
	* pour des raisons de lisibilité.
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
	* Cette méthode redéfini celle de la super-classe. Elle est appelée à
	* chaque fois qu'une cellule doit être affichée.
	* Elle permet de récupérer le composant graphique chargé de l'affichage de
	* la cellule. Dans le cas présent, il s'agit d'un JLabel contenant une 
	* icône dépendant du caractère ascendant ou descendant du tri, ainsi qu'un
	* chiffre indiquant l'ordre de la colonne dans le tri.
	*
	* Arguments:
	*  - table: La table contenant les cellules à afficher,
	*  - value: La valeur à afficher dans la cellule,
	*  - isSelected: indique si la cellule est sélectionnée,
	*  - hasFocus: indique si la cellule a le focus,
	*  - row: indique le numéro de rang de la cellule,
	*  - column: indique le numéro de colonne de la cellule.
	*
	* Retourne: Une instance de composant graphique chargé de l'affichage de la
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
		// Les traces sont désactivées, cette méthode étant appelée trop souvent
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
		// On appelle la méthode de la super classe
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
		// Si la valeur est négative, on utilise l'icône descendant
		else if(the_value.intValue() < 0)
		{
			setText("" + -1 * the_value.intValue());
			setIcon(IconLoader.getIcon("DESC"));
		}
		// On utilise l'icône ascendant
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