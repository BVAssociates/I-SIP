/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/UndoableComponentInterface.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de composant pour annulation/répétition
* DATE:        19/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: UndoableComponentInterface.java,v $
* Revision 1.2  2009/01/14 12:27:41  tz
* Classe déplacée dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.1  2006/11/03 10:24:59  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.abs.processor;

//
//Imports système
//

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: UndoableComponentInterface
* 
* Description:
* Cette interface représente un composant de processeur graphique capable de 
* gérer des actions d'annulation (undo) ou de répétition (redo) d'édition.
* Elle propose des méthodes permettant :
*  - de savoir si des actions d'annulation sont possibles,
*  - de savoir si des actions de répétition sont possibles,
*  - d'exécuter une opération d'annulation,
*  - d'exécuter une opération de répétition.
* ----------------------------------------------------------*/
public interface UndoableComponentInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: canUndo
	* 
	* Description:
	* Cette méthode permet de savoir si une action d'annulation (undo) est 
	* possible.
	* 
	* Retourne: true si une opération d'annulation est possible, false sinon.
	* ----------------------------------------------------------*/
	public boolean canUndo();

	/*----------------------------------------------------------
	* Nom: canRedo
	* 
	* Description:
	* Cette méthode permet de savoir si une opération de répétition (redo) 
	* est possible.
	* 
	* Retourne: true si une opération de répétition est possible, false sinon.
	* ----------------------------------------------------------*/
	public boolean canRedo();

	/*----------------------------------------------------------
	* Nom: undo
	* 
	* Description:
	* Cette méthode est chargée de lancer l'action d'annulation d'une tâche 
	* d'édition prélablement effectuée. Une tâche annulée peut être répétée 
	* via un appel à la méthode redo().
	* ----------------------------------------------------------*/
	public void undo();

	/*----------------------------------------------------------
	* Nom: redo
	* 
	* Description:
	* Cette méthode est chargée de lancer l'action de répétition d'une tâche 
	* d'édition préalablement annulée, via un appel à la méthode undo().
	* ----------------------------------------------------------*/
	public void redo();
}
