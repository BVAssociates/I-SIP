/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/DownloadProgressInterface.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de suivi de téléchargement
* DATE:        04/07/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DownloadProgressInterface.java,v $
* Revision 1.2  2009/01/14 12:26:44  tz
* Classe déplacée dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.1  2005/10/07 08:44:57  tz
* Ajout de l'interface.
*
* Revision 1.1  2005/07/05 15:09:22  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.abs.processor;

//
// Imports système
//

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: DownloadProgressInterface
* 
* Description:
* Cette interface permet à un objet FileDownloader de communiquer à un objet 
* appelant le niveau de progression du téléchargement, via la méthode 
* setProgress().
* ----------------------------------------------------------*/
public interface DownloadProgressInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: setProgress
	* 
	* Description:
	* Cette méthode permet de définir le niveau de progression du 
	* téléchargement d'un fichier.
	* La valeur fournie en argument est exprimée en pourcentage du 
	* téléchargement effectué.
	* 
	* Arguments:
	*  - progress: Un entier exprimant le pourcentage de téléchargement 
	*    effectué.
 	* ----------------------------------------------------------*/
 	public void setProgress(
 		int progress
 		);

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
