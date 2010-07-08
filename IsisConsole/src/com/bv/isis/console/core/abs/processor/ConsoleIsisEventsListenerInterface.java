/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/ConsoleIsisEventsListenerInterface.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface d'écouteur d'événements I-SIS
* DATE:        04/10/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ConsoleIsisEventsListenerInterface.java,v $
* Revision 1.2  2009/01/14 12:26:28  tz
* Classe déplacée dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.1  2005/10/07 08:44:57  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.abs.processor;

//
//Imports système
//

//
//Imports du projet
//
import com.bv.isis.corbacom.IsisEventsListenerInterfaceOperations;

/*----------------------------------------------------------
* Nom: ConsoleIsisEventsListenerInterface
* 
* Description:
* Cette interface est une spécialisation de l'interface 
* IsisEventsListenerInterfaceOperations, laquelle contient les méthodes 
* associées à l'interface IsisEventsListenerInterface.
* Une méthode est ajoutée aux méthodes de l'interface 
* IsisEventsListenerInterfaceOperations, la méthode clearEvents(), permettant 
* de supprimer tous les événements enregistrés par les différents "écouteurs" 
* des événements I-SIS.
* ----------------------------------------------------------*/
public interface ConsoleIsisEventsListenerInterface
	extends IsisEventsListenerInterfaceOperations
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: clearEvents
	* 
	* Description:
	* Cette méthode est destinée à permettre la suppression de tous les 
	* événements I-SIS enregistrés par les "écouteurs" d'événements I-SIS. La 
	* gestion de cet événement dépend de la nature de la classe 
	* d'implémentation de l'interface.
	* ----------------------------------------------------------*/
	public void clearEvents();
}
