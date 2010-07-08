/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/UndoableTextPane.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation de zone de texte "annulable"
* DATE:        19/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.edition
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: UndoableTextPane.java,v $
* Revision 1.2  2009/01/14 14:19:45  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.gui.
*
* Revision 1.1  2006/11/03 10:28:48  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.gui;

//
// Imports syst�me
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
* Cette classe est une sp�cialisation de la classe SearchableTextPane 
* permettant la gestion des actions d'annulation et de r�p�tition des 
* t�ches d'�dition.
* Elle sp�cialise donc la classe SearchableTextPane, et impl�mente les 
* interfaces UndoableComponentInterface et UndoableEditListener afin de, 
* respectivement, permettre une int�raction depuis la fen�tre principale, 
* et recevoir les �v�nements d'action d'�dition pouvant �tre annul�e.
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
	* Cette m�thode est le constructeur de la classe.
	* Elle permet de cr�er le gestionnaire d'actions d'annulation/r�p�tition, 
	* via une instance de UndoManager, et de l'associer au document pass� en 
	* argument.
	* 
	* Arguments:
	*  - document: Le mod�le de document � associer � la zone de texte.
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
		// On va cr�er le gestionnaire d'actions
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
	* Cette m�thode r�d�finit celle de l'interface UndoableComponentInterface. 
	* Elle permet de savoir si une action d'annulation est possible.
	* Elle appelle la m�thode de m�me nom du gestionnaire d'actions.
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
	* Cette m�thode r�d�finit celle de l'interface UndoableComponentInterface. 
	* Elle permet de savoir si une action de r�p�tition est possible.
	* Elle appelle la m�thode de m�me nom du gestionnaire d'actions.
	* 
	* Retourne: true si une action de r�p�tition est possible, false sinon.
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
	* Cette m�thode r�d�finit celle de l'interface UndoableComponentInterface. 
	* Elle est appel�e afin de d�clencher l'action d'annulation d'une t�che 
	* d'�dition.
	* Elle appelle la m�thode de m�me nom du gestionnaire d'actions.
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
				"l'�dition: " + exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: redo
	* 
	* Description:
	* Cette m�thode r�d�finit celle de l'interface UndoableComponentInterface. 
	* Elle est appel�e afin de d�clencher l'action de r�p�tition d'une t�che 
	* d'�dition.
	* Elle appelle la m�thode de m�me nom du gestionnaire d'actions.
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
			trace_errors.writeTrace("Erreur lors de la r�p�tition de " + 
				"l'�dition: " + exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: discardAllEdits
	* 
	* Description:
	* Cette m�thode est charg�e de supprimer l'ensemble des enregistrements 
	* d'actions pouvant �tre annul�es.
	* Elle appelle la m�thode de m�me nom sur le gestionnaire d'actions.
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
	* Cette m�thode red�finit celle de l'interface UndoableEditListener. 
	* Elle est appel�e � chaque fois qu'une action d'�dition pouvant �tre 
	* annul�e est effectu�e sur le document.
	* La m�thode appelle la m�thode de m�me nom sur le gestionnaire d'actions.
	* 
	* Arguments:
	*  - undoableEditEvent: L'�v�nement d'action d'�dition pouvant �tre 
	*    annul�e.
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
	* Cet attribut maintient une r�f�rence sur un objet UndoManager, charg� 
	* de g�rer les actions d'annulation/r�p�tition sur le document.
	* ----------------------------------------------------------*/
	private UndoManager _undoManager;
}
