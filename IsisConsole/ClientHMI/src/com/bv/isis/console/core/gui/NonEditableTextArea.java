/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/NonEditableTextArea.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation de zone de texte non �ditable
* DATE:        18/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: NonEditableTextArea.java,v $
* Revision 1.8  2009/01/14 14:18:35  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.gui.
*
* Revision 1.7  2006/11/03 10:29:35  tz
* La classe d�rive de SearchableTextArea.
*
* Revision 1.6  2005/10/07 08:17:04  tz
* Impl�mentation de l'interface SearchableComponentInterface et utilisation
* de DocumentSearcher.
*
* Revision 1.5  2005/07/01 12:03:24  tz
* Modification du composant pour les traces
* Ajout des m�thode setLastSearchPosition() et getLastSearchPosition()
*
* Revision 1.4  2004/10/22 15:33:15  tz
* Prise en compte du Ctrl+C
*
* Revision 1.3  2004/10/13 13:53:26  tz
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
* Cette classe est une sp�cialisation de la classe SearchableTextArea afin de 
* cr�er une zone de texte que ne soit pas �ditable.
* Pour cela, elle red�fini la m�thode processKeyEvent(), afin de supprimer 
* toute r�action aux appuis sur les touches.
* ----------------------------------------------------------*/
public class NonEditableTextArea 
	extends SearchableTextArea
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NonEditableTextArea
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe. Elle permet de d�finir
	* le comportement du composant lors de l'appui sur une touche.
	* Cette m�thode ne fait absolument rien, afin que la zone de texte n'ait
	* aucune r�action, dans tous les cas sauf un appui sur Ctrl+C ou d'une 
	* touche de direction, o� l'�v�nement est trait� par la super-classe.
	* 
	* Arguments:
	*  - event: L'�v�nement d'appui sur une touche.
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