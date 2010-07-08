/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/ValueCheckInterface.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de contrôle de valeur
* DATE:        14/11/2007
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin.checkers
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ValueCheckInterface.java,v $
* Revision 1.1  2007/12/07 10:32:07  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin.checkers;

//
//Imports système
//

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: ValueCheckInterface
* 
* Description:
* Cette interface permet de définir un conteneur pour les classes de 
* vérification des valeurs en fonction de contraintes.
* La vérification de la valeur est implémentée via la méthode 
* checkValueIsCorrect().
* ----------------------------------------------------------*/
public interface ValueCheckInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: checkValueIsCorrect
	* 
	* Description:
	* Cette méthode permet de contrôler que la valeur passée en argument est 
	* valable suivant la contrainte que la classe d'implémentation est 
	* supposée vérifier.
	* 
	* Arguments:
	*  - value: La valeur à vérifier sous forme de chaîne de caractères.
	* 
	* Retourne: true si la valeur est correcte, false sinon.
	* ----------------------------------------------------------*/
	public boolean checkValueIsCorrect(
			String value
			);

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
