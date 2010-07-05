/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/common/IconCellRenderer.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire d'affichage des ic�nes de noeuds
* DATE:        13/03/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      common
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: IconCellRenderer.java,v $
* Revision 1.3  2009/01/14 14:16:50  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.common.
*
* Revision 1.2  2006/03/20 15:49:56  tz
* Utilisation du libell� de l'ic�ne en cas d'ic�ne vide.
*
* Revision 1.1  2006/03/13 15:13:08  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.common;

//
//Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.gui.IconLoader;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: IconCellRenderer
* 
* Description:
* Cette classe est une sp�cialisation de la classe DefaultTableCellRenderer 
* charg�e de l'affichage d'une ic�ne dans un tableau. L'ic�ne est r�cup�r�e 
* � partir de l'identifiant qui constitue la donn�e.
* ----------------------------------------------------------*/
public class IconCellRenderer
	extends DefaultTableCellRenderer
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: IconCellRenderer
	* 
	* Description:
	* Cette m�thode est le constructeur par d�faut. Elle n'est pr�sent�e que 
	* pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public IconCellRenderer()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IconCellRenderer", "IconCellRenderer");
			
		trace_methods.beginningOfMethod();
		setHorizontalAlignment(JLabel.CENTER);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTableCellRendererComponent
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe. Elle est appel�e � 
	* chaque fois qu'une cellule doit �tre affich�e.
	* Elle permet de r�cup�rer le composant graphique charg� de l'affichage de 
	* la cellule, qui est un JLabel affichant l'ic�ne et son identifiant.
	* 
	* Arguments:
	*  - table: La table contenant les cellules � afficher,
	*  - value: La valeur � afficher dans la cellule,
	*  - isSelected: indique si la cellule est s�lectionn�e,
	*  - hasFocus: indique si la cellule a le focus,
	*  - row: indique le num�ro de rang de la cellule,
	*  - column: indique le num�ro de colonne de la cellule.
	* 
	* Retourne: Une instance de composant graphique charg� de l'affichage de 
	* la cellule.
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
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IconCellRenderer", "getTableCellRendererComponent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("table=" + table);
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("isSelected=" + isSelected);
		trace_arguments.writeTrace("hasFocus=" + hasFocus);
		trace_arguments.writeTrace("row=" + row);
		trace_arguments.writeTrace("column=" + column);*/
		// On appelle la m�thode de la super-classe
		super.getTableCellRendererComponent(table, "", isSelected, 
			hasFocus, row, column);
		// En fait, il s'agit d'un JLabel, on va supprimer le texte
		String label = null;
		// On va positionner l'ic�ne correspondant � la valeur
		if(value instanceof Icon)
		{
			setIcon((Icon)value); 
		}
		else
		{
			Icon icon = IconLoader.getIcon(value.toString());
			// Si l'ic�ne est vide, on va utiliser le libell� du noeud
			if(icon == null)
			{
				label = value.toString();
			}
			setIcon(icon); 
		}
		setText(label);
		//trace_methods.endOfMethod();
		return this;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
