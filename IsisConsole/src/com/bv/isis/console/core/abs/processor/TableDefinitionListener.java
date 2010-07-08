/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/TableDefinitionListener.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface d'écoute des définitions de tables
* DATE:        27/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: TableDefinitionListener.java,v $
* Revision 1.2  2009/01/14 12:27:25  tz
* Classe déplacée dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.1  2005/10/07 08:44:57  tz
* Ajout de l'interface.
*
* Revision 1.1  2004/11/02 09:11:51  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.abs.processor;

//
// Imports système
//

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: TableDefinitionListener
* 
* Description:
* Cette interface permet à une classe de gestion des définitions des tables 
* I-TOOLS de signaler à une autre classe (implémentant l'interface) que de 
* nouvelles définitions ont été chargées, que des définitions ont été 
* supprimées, ou encore que le nombre d'utilisations d'une table a changé.
* ----------------------------------------------------------*/
public interface TableDefinitionListener
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: tableDefinitionAdded
	* 
	* Description:
	* Cette méthode permet au gestionnaire des définitions de tables de 
	* signaler qu'une nouvelle définition a été ajoutée au cache.
	* La nouvelle définition est identifiée par les arguments.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent sur lequel la définition a été chargée,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: Le chemin complet du dictionnaire de la table sur 
	*    l'Agent.
 	* ----------------------------------------------------------*/
 	public void tableDefinitionAdded(
 		String agentName,
 		String iClesName,
 		String serviceType,
 		String definitionFilePath
 		);

	/*----------------------------------------------------------
	* Nom: tableDefinitionRemoved
	* 
	* Description:
	* Cette méthode permet au gestionnaire des définitions de tables de 
	* signaler qu'une définition précédemment chargée en cache a été 
	* supprimée.
	* La définition supprimée est identifiée par les arguments.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent sur lequel la définition a été chargée,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: Le chemin complet du dictionnaire de la table sur 
	*    l'Agent.
 	* ----------------------------------------------------------*/
 	public void tableDefinitionRemoved(
 		String agentName,
		String iClesName,
		String serviceType,
 		String definitionFilePath
 		);

	/*----------------------------------------------------------
	* Nom: tableDefinitionUseChanged
	* 
	* Description:
	* Cette méthode permet au gestionnaire des définitions de tables de 
	* signaler que le nombre d'utilisations d'une définition précédemment 
	* chargée en cache a changé.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent sur lequel la définition a été chargée,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: Le chemin complet du dictionnaire de la table sur 
	*    l'Agent,
	*  - numberOfUses: Le nouveau nombre des utilisations.
 	* ----------------------------------------------------------*/
 	public void tableDefinitionUseChanged(
 		String agentName,
		String iClesName,
		String serviceType,
 		String definitionFilePath,
 		int numberOfUses
 		);
}
