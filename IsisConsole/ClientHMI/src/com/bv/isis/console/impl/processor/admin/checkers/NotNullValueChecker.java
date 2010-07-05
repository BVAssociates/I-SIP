/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/NotNullValueChecker.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de vérification de valeur non nulle
* DATE:        14/11/2007
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin.checkers
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: NotNullValueChecker.java,v $
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
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: NotNullValueChecker
* 
* Description:
* Cette classe est chargée de la vérification de la contrainte de non-nullité 
* de la valeur.
* Elle implémente l'interface ValueCheckInterface.
* ----------------------------------------------------------*/
class NotNullValueChecker 
	implements ValueCheckInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NotNullValueChecker
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
	* ----------------------------------------------------------*/
	public NotNullValueChecker() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NotNullValueChecker", "NotNullValueChecker");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkValueIsCorrect
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ValueCheckInterface. Elle 
	* est appelée afin de vérifier que la valeur respecte la contrainte de 
	* non-nullité.
	* La valeur doit être non nulle, c'est à dire avoir une valeur non vide.
	* 
	* Arguments:
	*  - value: La valeur à vérifier sous forme de chaîne de caractères.
	* 
	* Retourne: true si la valeur est non nulle, false sinon.
	* ----------------------------------------------------------*/
	public boolean checkValueIsCorrect(String value) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"NotNullValueChecker", "checkValueIsCorrect");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean value_is_correct = true;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		// On vérifie que la valeur est non nulle
		if(value == null || value.equals("") == true) {
			// La valeur est nulle, on retourne false
			value_is_correct = false;
		}
		trace_methods.endOfMethod();
		return value_is_correct;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
