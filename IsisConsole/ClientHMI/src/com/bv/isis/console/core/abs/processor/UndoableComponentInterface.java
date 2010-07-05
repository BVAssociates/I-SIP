/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/UndoableComponentInterface.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de composant pour annulation/r�p�tition
* DATE:        19/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: UndoableComponentInterface.java,v $
* Revision 1.2  2009/01/14 12:27:41  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.1  2006/11/03 10:24:59  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.abs.processor;

//
//Imports syst�me
//

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: UndoableComponentInterface
* 
* Description:
* Cette interface repr�sente un composant de processeur graphique capable de 
* g�rer des actions d'annulation (undo) ou de r�p�tition (redo) d'�dition.
* Elle propose des m�thodes permettant :
*  - de savoir si des actions d'annulation sont possibles,
*  - de savoir si des actions de r�p�tition sont possibles,
*  - d'ex�cuter une op�ration d'annulation,
*  - d'ex�cuter une op�ration de r�p�tition.
* ----------------------------------------------------------*/
public interface UndoableComponentInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: canUndo
	* 
	* Description:
	* Cette m�thode permet de savoir si une action d'annulation (undo) est 
	* possible.
	* 
	* Retourne: true si une op�ration d'annulation est possible, false sinon.
	* ----------------------------------------------------------*/
	public boolean canUndo();

	/*----------------------------------------------------------
	* Nom: canRedo
	* 
	* Description:
	* Cette m�thode permet de savoir si une op�ration de r�p�tition (redo) 
	* est possible.
	* 
	* Retourne: true si une op�ration de r�p�tition est possible, false sinon.
	* ----------------------------------------------------------*/
	public boolean canRedo();

	/*----------------------------------------------------------
	* Nom: undo
	* 
	* Description:
	* Cette m�thode est charg�e de lancer l'action d'annulation d'une t�che 
	* d'�dition pr�lablement effectu�e. Une t�che annul�e peut �tre r�p�t�e 
	* via un appel � la m�thode redo().
	* ----------------------------------------------------------*/
	public void undo();

	/*----------------------------------------------------------
	* Nom: redo
	* 
	* Description:
	* Cette m�thode est charg�e de lancer l'action de r�p�tition d'une t�che 
	* d'�dition pr�alablement annul�e, via un appel � la m�thode undo().
	* ----------------------------------------------------------*/
	public void redo();
}
