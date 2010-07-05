/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/ValueCheckInterface.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de contr�le de valeur
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
// D�claration du package
package com.bv.isis.console.impl.processor.admin.checkers;

//
//Imports syst�me
//

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: ValueCheckInterface
* 
* Description:
* Cette interface permet de d�finir un conteneur pour les classes de 
* v�rification des valeurs en fonction de contraintes.
* La v�rification de la valeur est impl�ment�e via la m�thode 
* checkValueIsCorrect().
* ----------------------------------------------------------*/
public interface ValueCheckInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: checkValueIsCorrect
	* 
	* Description:
	* Cette m�thode permet de contr�ler que la valeur pass�e en argument est 
	* valable suivant la contrainte que la classe d'impl�mentation est 
	* suppos�e v�rifier.
	* 
	* Arguments:
	*  - value: La valeur � v�rifier sous forme de cha�ne de caract�res.
	* 
	* Retourne: true si la valeur est correcte, false sinon.
	* ----------------------------------------------------------*/
	public boolean checkValueIsCorrect(
			String value
			);

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
