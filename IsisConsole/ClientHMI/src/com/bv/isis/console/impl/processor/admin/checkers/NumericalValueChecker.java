/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/NumericalValueChecker.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de v�rification de valeur num�rique
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
// D�claration du package
package com.bv.isis.console.impl.processor.admin.checkers;

//
//Imports syst�me
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
* Cette classe est charg�e de v�rifier que les valeurs correspondent � des 
* valeurs num�riques.
* Elle impl�mente l'interface ValueCheckInterface.
* ----------------------------------------------------------*/
class NumericalValueChecker 
	implements ValueCheckInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NumericalValueChecker
	* 
	* Description:
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface ValueCheckInterface. Elle 
	* est appel�e afin de v�rifier que la valeur correspond � une valeur 
	* num�rique. Pour cela, elle ne doit �tre compos�e que de caract�res 
	* num�riques sans virgule ou signe, le point est autoris�.
	* 
	* Arguments:
	*  - value: La valeur � v�rifier sous forme de cha�ne de caract�res.
	* 
	* Retourne: true si la valeur est num�rique, false sinon.
	* ----------------------------------------------------------*/
	public boolean checkValueIsCorrect(String value) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"NumericalValueChecker", "checkValueIsCorrect");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean value_is_correct = true;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		// On v�rifie si la valeur est nulle
		ValueCheckInterface not_null = CheckerFactory.createChecker('!');
		if(not_null.checkValueIsCorrect(value) == false) {
			// Il s'agit d'une valeur nulle, la date est suppos�e bonne
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
	* Cette m�thode est charg�e de v�rifier que la valeur pass�e en premier 
	* argument, une fois convertie en valeur num�rique est bien sup�rieure ou 
	* �gale � la borne inf�rieure, et inf�rieure ou �gale � la borne 
	* sup�rieure.
	* 
	* Arguments:
	*  - value: La valeur � tester, sous forme de cha�ne de caract�res,
	*  - lowRangeValue: La borne inf�rieure de valeur,
	*  - highRangeValue: La borne sup�rieure de valeur.
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
			// Si on re�oit l'exception, c'est que la valeur n'est pas un float
		}
		trace_methods.endOfMethod();
		return value_is_correct;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
