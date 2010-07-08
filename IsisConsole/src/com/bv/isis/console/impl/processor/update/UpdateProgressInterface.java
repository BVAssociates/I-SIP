/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/UpdateProgressInterface.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de suivi de la progression de la mise � jour
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
// D�claration du package
package com.bv.isis.console.impl.processor.update;

//
//Imports syst�me
//

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: UpdateProgressInterface
* 
* Description:
* Cette interface permet aux classes techniques de mise � jour de la Console 
* I-SIS de communiquer avec le processeur de t�che.
* Elle ne d�finit que la m�thode updateProgress.
* ----------------------------------------------------------*/
interface UpdateProgressInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: updateProgress
	* 
	* Description:
	* Cette m�thode permet de mettre � jour les champs de la zone de 
	* progression � partir des informations pass�es en argument.
	* 
	* Arguments:
	*  - operation: L'intitul� de l'op�ration en cours, ou null,
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
