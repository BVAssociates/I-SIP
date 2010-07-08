/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/NumericCellRenderer.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire d'affichage des donn�es de type num�rique
* DATE:        28/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: NumericCellRenderer.java,v $
* Revision 1.6  2009/03/05 09:40:37  jy
* Remplacer DefaultTableCellRenderer par DisplayCellRenderer.
*
* Revision 1.5  2005/07/01 12:15:51  tz
* Modification du composant pour les traces
*
* Revision 1.4  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.3  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
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
import java.awt.Component;
import javax.swing.JLabel;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: NumericCellRenderer
*
* Description:
* Cette classe est une sp�cialisation de la classe DisplayCellRenderer
* charg�e de l'affichage des cellules contenant des valeurs num�riques.
* Les colonnes d'une table contenant des valeurs num�riques sont identifi�es par
* le suffixe 'n' dans l'attribut size.
* Les cellules contenant des valeurs num�riques ont un affichage align� �
* droite.
* ----------------------------------------------------------*/
class NumericCellRenderer
	extends DisplayCellRenderer
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NumericCellRenderer
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public NumericCellRenderer()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NumericCellRenderer", "NumericCellRenderer");

		trace_methods.beginningOfMethod();
		// On positionne un alignement � droite
		setHorizontalAlignment(JLabel.RIGHT);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTableCellRendererComponent
	*
	* Description:
	* Cette m�thode red�finit celle de la super-classe. Elle est appel�e �
	* chaque fois qu'une cellule de type num�rique doit �tre affich�e.
	* Elle permet de r�cup�rer le composant graphique charg� de l'affichage de
	* la cellule. Dans le cas pr�sent, il s'agit d'un JLabel align� � droite
	* (d�finit dans le constructeur). Cette m�thode ne fait qu'appeler la m�thode
	* de la super-classe.
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
			"NumericCellRenderer", "getTableCellRendererComponent");
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
		// On conserve le comportement par d�faut
		//trace_methods.endOfMethod();
		return this;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}