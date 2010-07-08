/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
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
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.main.
*
* Revision 1.2  2004/10/13 14:03:48  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.1.1.1  2001/11/14 08:41:01  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.abs.main;

//
// Imports syst�me
//

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: ProcessInterface
*
* Description:
* Cette interface permet � certaines classes de communiquer avec le
* processus de l'application sans avoir � conna�tre son impl�mentation.
*
* Une classe peut par le biais de cette interface:
* - arr�ter proprement l'application.
* ----------------------------------------------------------*/
public interface ProcessInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: stop
	*
	* Description:
	* Cette m�thode permet de signaler au processus de l'application
	* qu'il doit s'arr�ter. La classe d'impl�mentation devra terminer
	* proprement l'ex�cution de l'application en s'assurant de la
	* lib�ration de toutes les ressources allou�es.
	* ----------------------------------------------------------*/
	public void stop();
}