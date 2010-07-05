/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/NonEditableTextArea.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation de zone de texte non éditable
* DATE:        18/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: NonEditableTextArea.java,v $
* Revision 1.8  2009/01/14 14:18:35  tz
* Classe déplacée dans le package com.bv.isis.console.core.gui.
*
* Revision 1.7  2006/11/03 10:29:35  tz
* La classe dérive de SearchableTextArea.
*
* Revision 1.6  2005/10/07 08:17:04  tz
* Implémentation de l'interface SearchableComponentInterface et utilisation
* de DocumentSearcher.
*
* Revision 1.5  2005/07/01 12:03:24  tz
* Modification du composant pour les traces
* Ajout des méthode setLastSearchPosition() et getLastSearchPosition()
*
* Revision 1.4  2004/10/22 15:33:15  tz
* Prise en compte du Ctrl+C
*
* Revision 1.3  2004/10/13 13:53:26  tz
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
import java.awt.event.KeyEvent;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: NonEditableTextArea
* 
* Description:
* Cette classe est une spécialisation de la classe SearchableTextArea afin de 
* créer une zone de texte que ne soit pas éditable.
* Pour cela, elle redéfini la méthode processKeyEvent(), afin de supprimer 
* toute réaction aux appuis sur les touches.
* ----------------------------------------------------------*/
public class NonEditableTextArea 
	extends SearchableTextArea
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NonEditableTextArea
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présentée que
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public NonEditableTextArea()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NonEditableTextArea", "NonEditableTextArea");

		trace_methods.beginningOfMethod();
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
	* aucune réaction, dans tous les cas sauf un appui sur Ctrl+C ou d'une 
	* touche de direction, où l'événement est traité par la super-classe.
	* 
	* Arguments:
	*  - event: L'événement d'appui sur une touche.
	* ----------------------------------------------------------*/
	protected void processKeyEvent(
		KeyEvent event
		)
	{
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NonEditableTextArea", "processKeyEvent");
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