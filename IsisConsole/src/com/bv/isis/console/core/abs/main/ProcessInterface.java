/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/main/ProcessInterface.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface du processus de l'application
* DATE:        13/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.main
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ProcessInterface.java,v $
* Revision 1.3  2009/01/14 12:25:37  tz
* Classe déplacée dans le package com.bv.isis.console.core.abs.main.
*
* Revision 1.2  2004/10/13 14:03:48  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.1.1.1  2001/11/14 08:41:01  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.abs.main;

//
// Imports système
//

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: ProcessInterface
*
* Description:
* Cette interface permet à certaines classes de communiquer avec le
* processus de l'application sans avoir à connaître son implémentation.
*
* Une classe peut par le biais de cette interface:
* - arrêter proprement l'application.
* ----------------------------------------------------------*/
public interface ProcessInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: stop
	*
	* Description:
	* Cette méthode permet de signaler au processus de l'application
	* qu'il doit s'arrêter. La classe d'implémentation devra terminer
	* proprement l'exécution de l'application en s'assurant de la
	* libération de toutes les ressources allouées.
	* ----------------------------------------------------------*/
	public void stop();
}