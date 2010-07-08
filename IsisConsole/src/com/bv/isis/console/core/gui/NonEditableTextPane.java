/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/NonEditableTextPane.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation de zone de texte non éditable
* DATE:        20/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: NonEditableTextPane.java,v $
* Revision 1.5  2009/01/14 14:18:46  tz
* Classe déplacée dans le package com.bv.isis.console.core.gui.
*
* Revision 1.4  2006/11/03 10:29:45  tz
* La classe dérive de SearchableTextPane.
*
* Revision 1.3  2005/10/07 08:16:49  tz
* Implémentation de l'interface SearchableComponentInterface et utilisation
* de DocumentSearcher.
*
* Revision 1.2  2005/07/01 12:03:08  tz
* Modification du composant pour les traces
* Ajout des méthode setLastSearchPosition() et getLastSearchPosition()
*
* Revision 1.1  2004/10/22 15:33:00  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.gui;
//
// Imports système
//
import javax.swing.text.StyledDocument;
import java.awt.event.KeyEvent;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: NonEditableTextPane
* 
* Description:
* Cette classe est une spécialisation de la classe SearchableTextPane afin 
* de créer une zone de texte que ne soit pas éditable.
* Pour cela, elle redéfini la méthode processKeyEvent(), afin de supprimer 
* toute réaction aux appuis sur les touches.
* ----------------------------------------------------------*/
public class NonEditableTextPane 
	extends SearchableTextPane
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NonEditableTextPane
	* 
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présentée que 
	* pour des raisons de lisibilité.
	* 
	* Arguments:
	*  - document: Le modèle de document à associer à la zone de texte.
 	* ----------------------------------------------------------*/
 	public NonEditableTextPane(
 		StyledDocument document
 		)
 	{
 		super(document);
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NonEditableTextPane", "NonEditableTextPane");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("document=" + document);
		trace_methods.endOfMethod();
 	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: processKeyEvent
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle permet de définir
	* le comportement du composant lors de l'appui sur une touche.
	* Cette méthode ne fait absolument rien, afin que la zone de texte n'ait
	* aucune réaction, dans tous les cas sauf un appui sur Ctrl+C ou les 
	* touches de direction où l'événement est traité par la super-classe.
	* 
	* Arguments:
	*  - event: L'événement d'appui sur une touche.
	* ----------------------------------------------------------*/
	protected void processKeyEvent(
		KeyEvent event
		)
	{
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NonEditableTextPane", "processKeyEvent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");*/

		//trace_methods.beginningOfMethod();
		//trace_arguments.writeTrace("event=" + event);
		// S'il s'agit d'un Ctrl+C, ou d'une touche de direction, on effectue 
		// le traitement
		int key_code = event.getKeyCode();
		if((event.getModifiers() == 2 && key_code != 86 && key_code != 88) ||
			(key_code >= 33 && key_code <= 40) || key_code == 9)
		{
			super.processKeyEvent(event);
		}
		//trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
}
