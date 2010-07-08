/*------------------------------------------------------------
* Copyright (c) 2003 par BV Associates. Tous droits réservés.
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
* Mise à jour du modèle.
*
* Revision 1.1  2004/07/29 12:08:22  tz
* Ajout de la classe
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.display;

//
//Imports système
//

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: DisplayProcessorInterface
*
* Description:
* Cette interface est destinée à permettre l'interaction entre le gestionnaire
* de clicks dans le tableau (TablePopupTrigger) et la classe implémentant
* l'affichage du tableau.
* Elle définit les méthodes isKeyPresent() et getTheMainWindowInterface().
* ----------------------------------------------------------*/
public interface DisplayProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: isKeyPresent
	*
	* Description:
	* Cette méthode permet de savoir si toutes les colonnes représentant la clé
	* de la table affichée sont présentes. Si tel est le cas, le menu contextuel
	* peut être affiché.
	*
	* Retourne: Un booléen indiquant si la clé est présente (true), ou non.
	* ----------------------------------------------------------*/
	public boolean isKeyPresent();

	/*----------------------------------------------------------
	* Nom: getTheMainWindowInterface
	*
	* Description:
	* Cette méthode permet de récupérer une référence sur l'interface de la 
	* fenêtre principale (comme dans la classe ProcessorFrame).
	*
	* Retourne: Une référence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public MainWindowInterface getTheMainWindowInterface();
}
