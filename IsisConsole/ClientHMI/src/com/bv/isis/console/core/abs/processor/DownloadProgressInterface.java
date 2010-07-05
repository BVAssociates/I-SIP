/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/DownloadProgressInterface.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de suivi de t�l�chargement
* DATE:        04/07/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DownloadProgressInterface.java,v $
* Revision 1.2  2009/01/14 12:26:44  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.1  2005/10/07 08:44:57  tz
* Ajout de l'interface.
*
* Revision 1.1  2005/07/05 15:09:22  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.abs.processor;

//
// Imports syst�me
//

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: DownloadProgressInterface
* 
* Description:
* Cette interface permet � un objet FileDownloader de communiquer � un objet 
* appelant le niveau de progression du t�l�chargement, via la m�thode 
* setProgress().
* ----------------------------------------------------------*/
public interface DownloadProgressInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: setProgress
	* 
	* Description:
	* Cette m�thode permet de d�finir le niveau de progression du 
	* t�l�chargement d'un fichier.
	* La valeur fournie en argument est exprim�e en pourcentage du 
	* t�l�chargement effectu�.
	* 
	* Arguments:
	*  - progress: Un entier exprimant le pourcentage de t�l�chargement 
	*    effectu�.
 	* ----------------------------------------------------------*/
 	public void setProgress(
 		int progress
 		);

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
