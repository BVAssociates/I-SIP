/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/NumericalValueChecker.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de vérification de valeur numérique
* DATE:        14/11/2007
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin.checkers
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: NumericalValueChecker.java,v $
* Revision 1.2  2008/02/13 16:24:06  tz
* Gestion de la valeur nulle.
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

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: NumericalValueChecker
* 
* Description:
* Cette classe est chargée de vérifier que les valeurs correspondent à des 
* valeurs numériques.
* Elle implémente l'interface ValueCheckInterface.
* ----------------------------------------------------------*/
class NumericalValueChecker 
	implements ValueCheckInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NumericalValueChecker
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
	* ----------------------------------------------------------*/
	public NumericalValueChecker() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"NumericalValueChecker", "NumericalValueChecker");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkValueIsCorrect
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ValueCheckInterface. Elle 
	* est appelée afin de vérifier que la valeur correspond à une valeur 
	* numérique. Pour cela, elle ne doit être composée que de caractères 
	* numériques sans virgule ou signe, le point est autorisé.
	* 
	* Arguments:
	*  - value: La valeur à vérifier sous forme de chaîne de caractères.
	* 
	* Retourne: true si la valeur est numérique, false sinon.
	* ----------------------------------------------------------*/
	public boolean checkValueIsCorrect(String value) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"NumericalValueChecker", "checkValueIsCorrect");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean value_is_correct = true;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		// On vérifie si la valeur est nulle
		ValueCheckInterface not_null = CheckerFactory.createChecker('!');
		if(not_null.checkValueIsCorrect(value) == false) {
			// Il s'agit d'une valeur nulle, la date est supposée bonne
			trace_methods.endOfMethod();
			return value_is_correct;
		}
		for(int index = 0 ; index < value.length() ; index ++) {
			char the_char = value.charAt(index);
			if(Character.isDigit(the_char) == false && the_char != '.') {
				value_is_correct = false;
				break;
			}
		}
		trace_methods.endOfMethod();
		return value_is_correct;
	}

	/*----------------------------------------------------------
	* Nom: checkValueIsInRange
	* 
	* Description:
	* Cette méthode est chargée de vérifier que la valeur passée en premier 
	* argument, une fois convertie en valeur numérique est bien supérieure ou 
	* égale à la borne inférieure, et inférieure ou égale à la borne 
	* supérieure.
	* 
	* Arguments:
	*  - value: La valeur à tester, sous forme de chaîne de caractères,
	*  - lowRangeValue: La borne inférieure de valeur,
	*  - highRangeValue: La borne supérieure de valeur.
	* 
	* Retourne: true si la valeur est dans les bornes, false sinon.
	* ----------------------------------------------------------*/
	public boolean checkValueIsInRange(
		String value,
		float lowRangeValue,
		float highRangeValue
		) {	
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"NumericalValueChecker", "checkValueIsInRange");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean value_is_correct = false;
		float float_value = 0;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("lowRangeValue=" + lowRangeValue);
		trace_arguments.writeTrace("highRangeValue=" + highRangeValue);
		try {
			float_value = Float.parseFloat(value);
			if(float_value >= lowRangeValue && float_value <= highRangeValue) {
				value_is_correct = true;
			}
		}
		catch(Exception e) {
			trace_errors.writeTrace("Erreur lors du parsage de la valeur: " + 
				e);
			// Si on reçoit l'exception, c'est que la valeur n'est pas un float
		}
		trace_methods.endOfMethod();
		return value_is_correct;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
