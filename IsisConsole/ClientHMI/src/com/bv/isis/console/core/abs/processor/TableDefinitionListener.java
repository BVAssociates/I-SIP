/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/TableDefinitionListener.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface d'�coute des d�finitions de tables
* DATE:        27/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: TableDefinitionListener.java,v $
* Revision 1.2  2009/01/14 12:27:25  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.1  2005/10/07 08:44:57  tz
* Ajout de l'interface.
*
* Revision 1.1  2004/11/02 09:11:51  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.abs.processor;

//
// Imports syst�me
//

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: TableDefinitionListener
* 
* Description:
* Cette interface permet � une classe de gestion des d�finitions des tables 
* I-TOOLS de signaler � une autre classe (impl�mentant l'interface) que de 
* nouvelles d�finitions ont �t� charg�es, que des d�finitions ont �t� 
* supprim�es, ou encore que le nombre d'utilisations d'une table a chang�.
* ----------------------------------------------------------*/
public interface TableDefinitionListener
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: tableDefinitionAdded
	* 
	* Description:
	* Cette m�thode permet au gestionnaire des d�finitions de tables de 
	* signaler qu'une nouvelle d�finition a �t� ajout�e au cache.
	* La nouvelle d�finition est identifi�e par les arguments.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent sur lequel la d�finition a �t� charg�e,
	*  - iClesName: le nom du I-CLES auquel est apparent� le dictionnaire,
	*  - serviceType: le type du service auquel est apparent� le dictionnaire,
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
	* Cette m�thode permet au gestionnaire des d�finitions de tables de 
	* signaler qu'une d�finition pr�c�demment charg�e en cache a �t� 
	* supprim�e.
	* La d�finition supprim�e est identifi�e par les arguments.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent sur lequel la d�finition a �t� charg�e,
	*  - iClesName: le nom du I-CLES auquel est apparent� le dictionnaire,
	*  - serviceType: le type du service auquel est apparent� le dictionnaire,
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
	* Cette m�thode permet au gestionnaire des d�finitions de tables de 
	* signaler que le nombre d'utilisations d'une d�finition pr�c�demment 
	* charg�e en cache a chang�.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent sur lequel la d�finition a �t� charg�e,
	*  - iClesName: le nom du I-CLES auquel est apparent� le dictionnaire,
	*  - serviceType: le type du service auquel est apparent� le dictionnaire,
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
