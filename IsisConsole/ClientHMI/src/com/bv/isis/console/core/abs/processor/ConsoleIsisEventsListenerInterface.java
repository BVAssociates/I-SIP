/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/ConsoleIsisEventsListenerInterface.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface d'�couteur d'�v�nements I-SIS
* DATE:        04/10/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ConsoleIsisEventsListenerInterface.java,v $
* Revision 1.2  2009/01/14 12:26:28  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.1  2005/10/07 08:44:57  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.abs.processor;

//
//Imports syst�me
//

//
//Imports du projet
//
import com.bv.isis.corbacom.IsisEventsListenerInterfaceOperations;

/*----------------------------------------------------------
* Nom: ConsoleIsisEventsListenerInterface
* 
* Description:
* Cette interface est une sp�cialisation de l'interface 
* IsisEventsListenerInterfaceOperations, laquelle contient les m�thodes 
* associ�es � l'interface IsisEventsListenerInterface.
* Une m�thode est ajout�e aux m�thodes de l'interface 
* IsisEventsListenerInterfaceOperations, la m�thode clearEvents(), permettant 
* de supprimer tous les �v�nements enregistr�s par les diff�rents "�couteurs" 
* des �v�nements I-SIS.
* ----------------------------------------------------------*/
public interface ConsoleIsisEventsListenerInterface
	extends IsisEventsListenerInterfaceOperations
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: clearEvents
	* 
	* Description:
	* Cette m�thode est destin�e � permettre la suppression de tous les 
	* �v�nements I-SIS enregistr�s par les "�couteurs" d'�v�nements I-SIS. La 
	* gestion de cet �v�nement d�pend de la nature de la classe 
	* d'impl�mentation de l'interface.
	* ----------------------------------------------------------*/
	public void clearEvents();
}
