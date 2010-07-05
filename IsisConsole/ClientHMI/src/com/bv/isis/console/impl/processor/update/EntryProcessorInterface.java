/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/EntryProcessorInterface.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de processeur de traitement d'entr�e
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
// D�claration du package
package com.bv.isis.console.impl.processor.update;

//
//Imports syst�me
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
* Cette interface permet de manipuler les classes de traitement des diff�rents 
* types de fichiers de mise � jour. Toutes les classes de traitement devront 
* donc l'impl�menter.
* Elle est destin�e � �tre manipul�e par le processeur de t�che charg� de la 
* mise � jour de la Console I-SIS.
* Elle ne d�finit qu'une seule m�thode, permettant de d�clencher le traitement 
* d'une entr�e du fichier de d�finition de la mise � jour.
* ----------------------------------------------------------*/
interface EntryProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: processEntry
	* 
	* Description:
	* Cette m�thode permet de commander le d�clenchement du traitement d'une 
	* entr�e du fichier de d�finition de la mise � jour.
	* L'entr�e est repr�sent�e par son nom, et une r�f�rence sur un objet 
	* File. Il est suppos� �tre trait� dans un r�pertoire de destination. Le 
	* traitement est suivi via l'interface de suivi de progression.
	* 
	* Si un probl�me survient pendant le traitement de l'entr�e, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - entryName: Le nom (et chemin relatif) de l'entr�e,
	*  - entryFile: Une r�f�rence sur un objet File repr�sentant le fichier 
	*    physique de l'entr�e,
	*  - destinationDirectory: Le r�pertoire de base de destination,
	*  - progressInterface: Une r�f�rence sur un objet UpdateProgressInterface 
	*    permet d'indiquer la progression du traitement de l'entr�e.
	* 
	* L�ve: InnerException.
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
