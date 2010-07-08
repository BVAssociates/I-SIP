/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/BooleanCellRenderer.java,v $
* $Revision: 1.9 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire d'affichage des donn�es de type bool�en
* DATE:        28/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: BooleanCellRenderer.java,v $
* Revision 1.9  2009/03/05 09:30:47  jy
* Cette classe est modifi�e comme une sp�cialisation de la classe DisplayCellRenderer.
*
* Revision 1.8  2005/07/06 10:05:44  tz
* Correction de la fiche Inuit/0159.
*
* Revision 1.7  2005/07/01 12:17:00  tz
* Modification du composant pour les traces
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.5  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.4  2004/10/06 07:34:07  tz
* Valeur vraie pour un bool�en: 1 ou TRUE.
*
* Revision 1.3  2002/04/05 15:50:35  tz
* Cloture it�ration IT1.2
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.1  2001/12/28 16:31:34  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.display;

//
// Imports syst�me
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
* Cette classe est une sp�cialisation de la classe DisplayCellRenderer
* charg�e de l'affichage des cellules contenant des valeurs bool�ennes.
* Les colonnes d'une table contenant des valeurs bool�ennes sont identifi�es
* par le suffixe 'b' dans l'attribut size.
* Les cellules contenant des valeurs bool�ennes affichent une ic�ne suivant la
* valeur de mani�re centr�e.
* ----------------------------------------------------------*/
public class BooleanCellRenderer
	extends DisplayCellRenderer
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: BooleanCellRenderer
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe. Elle est appel�e �
	* chaque fois qu'une cellule de type num�rique doit �tre affich�e.
	* Elle permet de r�cup�rer le composant graphique charg� de l'affichage de
	* la cellule. Dans le cas pr�sent, il s'agit d'un JLabel align� au centre
	* (d�finit dans le constructeur) ne contenant qu'une ic�ne d�pendant de la
	* valeur.
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
		// On r�cup�re la valeur
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
			// On positionne l'ic�ne True
			setIcon(IconLoader.getIcon("TrueIcon"));
		}
		else
		{
			// On positionne l'ic�ne False
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