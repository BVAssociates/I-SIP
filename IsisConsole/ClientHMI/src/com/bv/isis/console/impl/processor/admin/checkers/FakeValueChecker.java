/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/FakeValueChecker.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de non vérification de contrainte
* DATE:        14/11/2007
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin.checkers
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: FakeValueChecker.java,v $
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
* Nom: FakeValueChecker
* 
* Description:
* Cette classe est chargée d'implémenter l'absence de contrôle.
* Elle implémente l'interface ValueCheckInterface.
* ----------------------------------------------------------*/
class FakeValueChecker 
	implements ValueCheckInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: FakeValueChecker
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
	* ----------------------------------------------------------*/
	public FakeValueChecker() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"FakeValueChecker", "FakeValueChecker");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkValueIsCorrect
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ValueCheckInterface. Elle 
	* est appelée afin de vérifier que la valeur respecte la contrainte 
	* associé à la classe.
	* Dans le cas présent, il s'agit d'implémenter l'absence de contrainte, 
	* aussi la valeur est toujours correcte, quelle qu'elle soit.
	* 
	* Arguments:
	*  - value: La valeur à vérifier sous forme de chaîne de caractères.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean checkValueIsCorrect(String value) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"FakeValueChecker", "checkValueIsCorrect");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		trace_methods.endOfMethod();
		return true;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
