/*------------------------------------------------------------
* Copyright (c) 2003 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/DisplayProcessorInterface.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface d'interaction avec le processeur d'affichage
* DATE:        19/12/2003
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DisplayProcessorInterface.java,v $
* Revision 1.3  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.1  2004/07/29 12:08:22  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.display;

//
//Imports syst�me
//

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: DisplayProcessorInterface
*
* Description:
* Cette interface est destin�e � permettre l'interaction entre le gestionnaire
* de clicks dans le tableau (TablePopupTrigger) et la classe impl�mentant
* l'affichage du tableau.
* Elle d�finit les m�thodes isKeyPresent() et getTheMainWindowInterface().
* ----------------------------------------------------------*/
public interface DisplayProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: isKeyPresent
	*
	* Description:
	* Cette m�thode permet de savoir si toutes les colonnes repr�sentant la cl�
	* de la table affich�e sont pr�sentes. Si tel est le cas, le menu contextuel
	* peut �tre affich�.
	*
	* Retourne: Un bool�en indiquant si la cl� est pr�sente (true), ou non.
	* ----------------------------------------------------------*/
	public boolean isKeyPresent();

	/*----------------------------------------------------------
	* Nom: getTheMainWindowInterface
	*
	* Description:
	* Cette m�thode permet de r�cup�rer une r�f�rence sur l'interface de la 
	* fen�tre principale (comme dans la classe ProcessorFrame).
	*
	* Retourne: Une r�f�rence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public MainWindowInterface getTheMainWindowInterface();
}
