/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/DateValueChecker.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de vérification de valeur date
* DATE:        14/11/2007
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin.checkers
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DateValueChecker.java,v $
* Revision 1.2  2007/12/28 17:41:13  tz
* La date peut avoir une valeur nulle.
*
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
import java.text.SimpleDateFormat;

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: DateValueChecker
* 
* Description:
* Cette classe est chargée de tester que les valeurs correspondent à des 
* dates. Elle dérive de la classe NumericalValueChecker.
* ----------------------------------------------------------*/
class DateValueChecker 
	extends NumericalValueChecker {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DateValueChecker
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
	* ----------------------------------------------------------*/
	public DateValueChecker() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DateValueChecker", "DateValueChecker");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkValueIsCorrect
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe NumericalValueChecker. 
	* Elle est appelée afin de vérifier que la valeur correspond à une date. 
	* Pour cela, elle ne doit correspondre à une valeur numérique 
	* (vérification réalisée par la super-classe) composée de 14 chiffres.
	* La vérification de la correspondance entre la série de chiffre est une 
	* date est confiée à la classe SimpleDateFormat.
	* 
	* Arguments:
	*  - value: La valeur à vérifier sous forme de chaîne de caractères.
	* 
	* Retourne: true si la valeur est une date, false sinon.
	* ----------------------------------------------------------*/
	public boolean checkValueIsCorrect(
		String value
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DateValueChecker", "checkValueIsCorrect");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean value_is_correct = true;
		SimpleDateFormat date_format = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		// On vérifie si la valeur est nulle
		ValueCheckInterface not_null = CheckerFactory.createChecker('!');
		if(not_null.checkValueIsCorrect(value) == false) {
			// Il s'agit d'une valeur nulle, la date est supposée bonne
			trace_methods.endOfMethod();
			return value_is_correct;
		}
		value_is_correct = super.checkValueIsCorrect(value);
		if(value_is_correct == false) {
			// Il ne s'agit pas d'une valeur numérique, on peut sortir
			trace_methods.endOfMethod();
			return value_is_correct;
		}
		// Il ne reste plus qu'à essayer de parser la chaine au format I-TOOLS
		date_format = new SimpleDateFormat("yyyyMMddkkmmss");
		// On demande un parsage strict
		date_format.setLenient(false);
		try {
			date_format.parse(value);
		}
		catch(Exception e) {
			trace_errors.writeTrace("Erreur lors du parsage de la valeur: " + 
				e);
			// Si on arrive ici, c'est que la date n'est pas bonne
			value_is_correct = false;
		}
		trace_methods.endOfMethod();
		return value_is_correct;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
