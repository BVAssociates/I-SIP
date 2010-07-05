/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/UndoableTextPane.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation de zone de texte "annulable"
* DATE:        19/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.edition
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: UndoableTextPane.java,v $
* Revision 1.2  2009/01/14 14:19:45  tz
* Classe déplacée dans le package com.bv.isis.console.core.gui.
*
* Revision 1.1  2006/11/03 10:28:48  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.gui;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import javax.swing.text.StyledDocument;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.processor.UndoableComponentInterface;

/*----------------------------------------------------------
* Nom: UndoableTextPane
* 
* Description:
* Cette classe est une spécialisation de la classe SearchableTextPane 
* permettant la gestion des actions d'annulation et de répétition des 
* tâches d'édition.
* Elle spécialise donc la classe SearchableTextPane, et implémente les 
* interfaces UndoableComponentInterface et UndoableEditListener afin de, 
* respectivement, permettre une intéraction depuis la fenêtre principale, 
* et recevoir les événements d'action d'édition pouvant être annulée.
* ----------------------------------------------------------*/
public class UndoableTextPane
	extends SearchableTextPane
	implements
		UndoableComponentInterface, 
		UndoableEditListener
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: UndoableTextPane
	* 
	* Description:
	* Cette méthode est le constructeur de la classe.
	* Elle permet de créer le gestionnaire d'actions d'annulation/répétition, 
	* via une instance de UndoManager, et de l'associer au document passé en 
	* argument.
	* 
	* Arguments:
	*  - document: Le modèle de document à associer à la zone de texte.
 	* ----------------------------------------------------------*/
 	public UndoableTextPane(
 		StyledDocument document
 		)
 	{
 		super(document);
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UndoableTextPane", "UndoableTextPane");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("document=" + document);
		// On va créer le gestionnaire d'actions
		_undoManager = new UndoManager();
		_undoManager.setLimit(20);
		// On va s'ajouter en tant que listener sur le document
		document.addUndoableEditListener(this);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: canUndo
	*
	* Description:
	* Cette méthode rédéfinit celle de l'interface UndoableComponentInterface. 
	* Elle permet de savoir si une action d'annulation est possible.
	* Elle appelle la méthode de même nom du gestionnaire d'actions.
	* 
	* Retourne: true si une action d'annulation est possible, false sinon.
	* ----------------------------------------------------------*/
	public boolean canUndo()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UndoableTextPane", "canUndo");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _undoManager.canUndo();
	}

	/*----------------------------------------------------------
	* Nom: canRedo
	* 
	* Description:
	* Cette méthode rédéfinit celle de l'interface UndoableComponentInterface. 
	* Elle permet de savoir si une action de répétition est possible.
	* Elle appelle la méthode de même nom du gestionnaire d'actions.
	* 
	* Retourne: true si une action de répétition est possible, false sinon.
	* ----------------------------------------------------------*/
	public boolean canRedo()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UndoableTextPane", "canRedo");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _undoManager.canRedo();
	}

	/*----------------------------------------------------------
	* Nom: undo
	* 
	* Description:
	* Cette méthode rédéfinit celle de l'interface UndoableComponentInterface. 
	* Elle est appelée afin de déclencher l'action d'annulation d'une tâche 
	* d'édition.
	* Elle appelle la méthode de même nom du gestionnaire d'actions.
	* ----------------------------------------------------------*/
	public void undo()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UndoableTextPane", "undo");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		try
		{
			_undoManager.undo();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de l'annulation de " + 
				"l'édition: " + exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: redo
	* 
	* Description:
	* Cette méthode rédéfinit celle de l'interface UndoableComponentInterface. 
	* Elle est appelée afin de déclencher l'action de répétition d'une tâche 
	* d'édition.
	* Elle appelle la méthode de même nom du gestionnaire d'actions.
	* ----------------------------------------------------------*/
	public void redo()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UndoableTextPane", "redo");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		try
		{
			_undoManager.redo();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la répétition de " + 
				"l'édition: " + exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: discardAllEdits
	* 
	* Description:
	* Cette méthode est chargée de supprimer l'ensemble des enregistrements 
	* d'actions pouvant être annulées.
	* Elle appelle la méthode de même nom sur le gestionnaire d'actions.
	* ----------------------------------------------------------*/
	public void discardAllEdits()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UndoableTextPane", "discardAllEdits");

		trace_methods.beginningOfMethod();
		_undoManager.discardAllEdits();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: undoableEditHappened
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface UndoableEditListener. 
	* Elle est appelée à chaque fois qu'une action d'édition pouvant être 
	* annulée est effectuée sur le document.
	* La méthode appelle la méthode de même nom sur le gestionnaire d'actions.
	* 
	* Arguments:
	*  - undoableEditEvent: L'événement d'action d'édition pouvant être 
	*    annulée.
 	* ----------------------------------------------------------*/
 	public void undoableEditHappened(
 		UndoableEditEvent undoableEditEvent
 		)
 	{
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UndoableTextPane", "undoableEditHappened");*/

		//trace_methods.beginningOfMethod();
		_undoManager.addEdit(undoableEditEvent.getEdit());
		//trace_methods.endOfMethod();
 	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _undoManager
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet UndoManager, chargé 
	* de gérer les actions d'annulation/répétition sur le document.
	* ----------------------------------------------------------*/
	private UndoManager _undoManager;
}
