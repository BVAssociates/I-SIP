/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/DateCellRenderer.java,v $
* $Revision: 1.11 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire d'affichage des données de type date
* DATE:        28/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DateCellRenderer.java,v $
* Revision 1.11  2009/03/05 09:31:20  jy
* Cette classe est modifiée comme une spécialisation de la classe DisplayCellRenderer.
*
* Revision 1.10  2009/01/15 16:53:39  tz
* Format de construction de la date en fichier d'internationalisation.
*
* Revision 1.9  2005/07/06 10:06:04  tz
* Prévention du cas où value est null.
*
* Revision 1.8  2005/07/01 12:16:52  tz
* Modification du composant pour les traces
*
* Revision 1.7  2004/11/23 15:42:05  tz
* Correction de l'affichage des champs date.
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.5  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2004/07/29 12:11:47  tz
* Mise à jour de la documentation
*
* Revision 1.3  2002/11/19 08:43:10  tz
* Correction de la fiche Inuit/73.
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
import com.bv.core.message.MessageManager;
import javax.swing.JTable;
import java.awt.Component;
import java.util.Locale;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: DateCellRenderer
*
* Description:
* Cette classe est une spécialisation de la classe DisplayCellRenderer
* chargée de l'affichage des cellules contenant des dates.
* Les colonnes d'une table contenant des dates sont identifiées par le suffixe
* 'd' dans l'attribut size.
* Les cellules contenant des dates ont un affichage dépendant de la langue:
*  - en français, le format est DD/MM/AAAA hh:mm:ss,
*  - en anglais, le format est MM/DD/AAAA hh:mm:ss.
* ----------------------------------------------------------*/
class DateCellRenderer
	extends DisplayCellRenderer
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DateCellRenderer
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présentée que
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public DateCellRenderer()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DateCellRenderer", "DateCellRenderer");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTableCellRendererComponent
	*
	* Description:
	* Cette méthode redéfini celle de la super-classe. Elle est appelée à chaque
	* fois qu'une cellule de type date doit être affichée.
	* Elle permet de récupérer le composant graphique chargé de l'affichage de
	* la cellule. Dans le cas présent, il s'agit d'un JLabel aligné à gauche
	* contenant la date dans un format dépendant de la langue.
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
		boolean is_numerical = true;
		String the_value = "";
		// Les traces sont désactivées, cette méthode étant appelée trop souvent
		/*
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DateCellRenderer", "getTableCellRendererComponent");
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
		if(value != null)
		{
			the_value = value.toString();
		}
		for(int index = 0 ; index < the_value.length() ; index ++)
		{
			char the_char = the_value.charAt(index);
			if(the_char < '0' || the_char > '9')
			{
				is_numerical = false;
				break;
			}
		}
		// On découpe la valeur en différentes portions. La date est fournie
		// dans le format AAAAMMDDhhmmss
		if(the_value.length() == 14 && is_numerical == true)
		{
			String year = the_value.substring(0, 4);
			String month = the_value.substring(4, 6);
			String day = the_value.substring(6, 8);
			String hour = the_value.substring(8, 10);
			String minutes = the_value.substring(10, 12);
			String seconds = the_value.substring(12);
			// On va créer un objet Date
			try {
				GregorianCalendar calendar = new GregorianCalendar(
					Integer.parseInt(year), Integer.parseInt(month) - 1, 
					Integer.parseInt(day), Integer.parseInt(hour), 
					Integer.parseInt(minutes), Integer.parseInt(seconds));
				SimpleDateFormat formatter = new SimpleDateFormat(
					MessageManager.getMessage("&DateFormat"));
				setText(formatter.format(calendar.getTime()));
			}
			catch(Exception exception) {
				// On formate la chaîne de date suivant la langue courante
				if(Locale.getDefault().getLanguage().equalsIgnoreCase("fr") == true)
				{
					// La langue courante est le français
					setText(day + "/" + month + "/" + year + " " + hour + ":" +
						minutes + ":" + seconds);
				}
				else
				{
					// La langue courante est l'anglais
					setText(month + "/" + day + "/" + year + " " + hour + ":" +
						minutes + ":" + seconds);
				}
			}
		}
		//trace_methods.endOfMethod();
		return this;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}