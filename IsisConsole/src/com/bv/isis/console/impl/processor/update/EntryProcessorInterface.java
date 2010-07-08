/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/EntryProcessorInterface.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de processeur de traitement d'entrée
* DATE:        26/10/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.update
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: EntryProcessorInterface.java,v $
* Revision 1.2  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
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
import java.io.File;

//
//Imports du projet
//
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: EntryProcessorInterface
* 
* Description:
* Cette interface permet de manipuler les classes de traitement des différents 
* types de fichiers de mise à jour. Toutes les classes de traitement devront 
* donc l'implémenter.
* Elle est destinée à être manipulée par le processeur de tâche chargé de la 
* mise à jour de la Console I-SIS.
* Elle ne définit qu'une seule méthode, permettant de déclencher le traitement 
* d'une entrée du fichier de définition de la mise à jour.
* ----------------------------------------------------------*/
interface EntryProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: processEntry
	* 
	* Description:
	* Cette méthode permet de commander le déclenchement du traitement d'une 
	* entrée du fichier de définition de la mise à jour.
	* L'entrée est représentée par son nom, et une référence sur un objet 
	* File. Il est supposé être traité dans un répertoire de destination. Le 
	* traitement est suivi via l'interface de suivi de progression.
	* 
	* Si un problème survient pendant le traitement de l'entrée, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - entryName: Le nom (et chemin relatif) de l'entrée,
	*  - entryFile: Une référence sur un objet File représentant le fichier 
	*    physique de l'entrée,
	*  - destinationDirectory: Le répertoire de base de destination,
	*  - progressInterface: Une référence sur un objet UpdateProgressInterface 
	*    permet d'indiquer la progression du traitement de l'entrée.
	* 
	* Lève: InnerException.
 	* ----------------------------------------------------------*/
 	public void processEntry(
 		String entryName,
 		File entryFile,
 		String destinationDirectory,
 		UpdateProgressInterface progressInterface
 		)
 		throws
 			InnerException;
}
