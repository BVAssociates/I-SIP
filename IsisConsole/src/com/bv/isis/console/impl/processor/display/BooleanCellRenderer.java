/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/BooleanCellRenderer.java,v $
* $Revision: 1.9 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire d'affichage des données de type booléen
* DATE:        28/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: BooleanCellRenderer.java,v $
* Revision 1.9  2009/03/05 09:30:47  jy
* Cette classe est modifiée comme une spécialisation de la classe DisplayCellRenderer.
*
* Revision 1.8  2005/07/06 10:05:44  tz
* Correction de la fiche Inuit/0159.
*
* Revision 1.7  2005/07/01 12:17:00  tz
* Modification du composant pour les traces
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.5  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2004/10/06 07:34:07  tz
* Valeur vraie pour un booléen: 1 ou TRUE.
*
* Revision 1.3  2002/04/05 15:50:35  tz
* Cloture itération IT1.2
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture itération IT1.0.1
*
* Revision 1.1  2001/12/28 16:31:34  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.display;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JTable;
import javax.swing.JLabel;
import java.awt.Component;
import com.bv.core.gui.IconLoader;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: BooleanCellRenderer
*
* Description:
* Cette classe est une spécialisation de la classe DisplayCellRenderer
* chargée de l'affichage des cellules contenant des valeurs booléennes.
* Les colonnes d'une table contenant des valeurs booléennes sont identifiées
* par le suffixe 'b' dans l'attribut size.
* Les cellules contenant des valeurs booléennes affichent une icône suivant la
* valeur de manière centrée.
* ----------------------------------------------------------*/
public class BooleanCellRenderer
	extends DisplayCellRenderer
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: BooleanCellRenderer
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présentée que
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public BooleanCellRenderer()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"BooleanCellRenderer", "BooleanCellRenderer");

		trace_methods.beginningOfMethod();
		// On positionne un alignement au centre
		setHorizontalAlignment(JLabel.CENTER);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTableCellRendererComponent
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée à
	* chaque fois qu'une cellule de type numérique doit être affichée.
	* Elle permet de récupérer le composant graphique chargé de l'affichage de
	* la cellule. Dans le cas présent, il s'agit d'un JLabel aligné au centre
	* (définit dans le constructeur) ne contenant qu'une icône dépendant de la
	* valeur.
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
		// On récupère la valeur
		String the_value = value.toString();
		// On teste la valeur
		if(the_value.equals("1") == true || 
			the_value.equalsIgnoreCase("OUI") == true ||
			the_value.equalsIgnoreCase("O") == true ||
			the_value.equalsIgnoreCase("TRUE") == true ||
			the_value.equalsIgnoreCase("T") == true ||
			the_value.equalsIgnoreCase("VRAI") == true ||
			the_value.equalsIgnoreCase("V") == true)
		{
			// On positionne l'icône True
			setIcon(IconLoader.getIcon("TrueIcon"));
		}
		else
		{
			// On positionne l'icône False
			setIcon(IconLoader.getIcon("FalseIcon"));
		}
		// Pas de texte
		setText("");
		//trace_methods.endOfMethod();
		return this;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}