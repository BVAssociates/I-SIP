/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/UpdateProgressInterface.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de suivi de la progression de la mise à jour
* DATE:        26/10/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.update
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: UpdateProgressInterface.java,v $
* Revision 1.1  2005/12/23 13:23:43  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.update;

//
//Imports système
//

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: UpdateProgressInterface
* 
* Description:
* Cette interface permet aux classes techniques de mise à jour de la Console 
* I-SIS de communiquer avec le processeur de tâche.
* Elle ne définit que la méthode updateProgress.
* ----------------------------------------------------------*/
interface UpdateProgressInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: updateProgress
	* 
	* Description:
	* Cette méthode permet de mettre à jour les champs de la zone de 
	* progression à partir des informations passées en argument.
	* 
	* Arguments:
	*  - operation: L'intitulé de l'opération en cours, ou null,
	*  - file: Le nom du fichier en cours de traitement, ou null,
	*  - fileProgress: La progression du traitement du fichier,
	*  - globalProgress: La progression globale.
 	* ----------------------------------------------------------*/
 	public void updateProgress(
 		String operation,
 		String file,
 		int fileProgress,
 		int globalProgress
 		);
}
