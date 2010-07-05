/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
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
* Ajout de la m�thode getTheParameters().
*
* Revision 1.1  2006/03/08 14:07:34  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports syst�me
//

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: DialogCallerInterface
* 
* Description:
* Cette interface permet de d�finir un conteneur qui sera pass� aux 
* constructeurs des diff�rentes boites de dialogue. Elle propose des m�thodes 
* d'acc�s � certaines informations.
* Elle permet, en fait, de rendre possible un �change entre la classe 
* BaseDialog et la classe AdministrationProcessor.
* ----------------------------------------------------------*/
public interface DialogCallerInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getTheTableName
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le nom de la table sujet de 
	* l'administration.
	* 
	* Retourne: Le nom de la table administr�e.
	* ----------------------------------------------------------*/
	public String getTheTableName();

	/*----------------------------------------------------------
	* Nom: getTheMainWindowInterface
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer une r�f�rence sur l'interface 
	* MainWindowInterface repr�sentant la fen�tre principale de la Console.
	* 
	* Retourne: Une r�f�rence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public MainWindowInterface getTheMainWindowInterface();

	/*----------------------------------------------------------
	* Nom: getTheActionId
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le num�ro unique de l'action 
	* d'administration de la table.
	* 
	* Retourne: Le num�ro unique de l'action d'administration.
	* ----------------------------------------------------------*/
	public String getTheActionId();

	/*----------------------------------------------------------
	* Nom: getTheParameters
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer les param�tres d'ex�cution du 
	* processeur, tels qu'ils ont �t� d�finis au niveau de la m�thode I-SIS.
	* 
	* Retourne: Les param�tres d'ex�cution du processeur.
	* ----------------------------------------------------------*/
	public String getTheParameters();

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
