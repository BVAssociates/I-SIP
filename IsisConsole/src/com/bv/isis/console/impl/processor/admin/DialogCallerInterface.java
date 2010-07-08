/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/DialogCallerInterface.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de liaison avec la classe d'appel des dialogues
* DATE:        08/03/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DialogCallerInterface.java,v $
* Revision 1.3  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2006/03/20 15:52:44  tz
* Ajout de la méthode getTheParameters().
*
* Revision 1.1  2006/03/08 14:07:34  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports système
//

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: DialogCallerInterface
* 
* Description:
* Cette interface permet de définir un conteneur qui sera passé aux 
* constructeurs des différentes boites de dialogue. Elle propose des méthodes 
* d'accès à certaines informations.
* Elle permet, en fait, de rendre possible un échange entre la classe 
* BaseDialog et la classe AdministrationProcessor.
* ----------------------------------------------------------*/
public interface DialogCallerInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getTheTableName
	* 
	* Description:
	* Cette méthode permet de récupérer le nom de la table sujet de 
	* l'administration.
	* 
	* Retourne: Le nom de la table administrée.
	* ----------------------------------------------------------*/
	public String getTheTableName();

	/*----------------------------------------------------------
	* Nom: getTheMainWindowInterface
	* 
	* Description:
	* Cette méthode permet de récupérer une référence sur l'interface 
	* MainWindowInterface représentant la fenêtre principale de la Console.
	* 
	* Retourne: Une référence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public MainWindowInterface getTheMainWindowInterface();

	/*----------------------------------------------------------
	* Nom: getTheActionId
	* 
	* Description:
	* Cette méthode permet de récupérer le numéro unique de l'action 
	* d'administration de la table.
	* 
	* Retourne: Le numéro unique de l'action d'administration.
	* ----------------------------------------------------------*/
	public String getTheActionId();

	/*----------------------------------------------------------
	* Nom: getTheParameters
	* 
	* Description:
	* Cette méthode permet de récupérer les paramètres d'exécution du 
	* processeur, tels qu'ils ont été définis au niveau de la méthode I-SIS.
	* 
	* Retourne: Les paramètres d'exécution du processeur.
	* ----------------------------------------------------------*/
	public String getTheParameters();

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
