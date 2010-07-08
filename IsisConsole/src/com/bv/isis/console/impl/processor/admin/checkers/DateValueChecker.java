/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/DateValueChecker.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de v�rification de valeur date
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
// D�claration du package
package com.bv.isis.console.impl.processor.admin.checkers;

//
//Imports syst�me
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
* Cette classe est charg�e de tester que les valeurs correspondent � des 
* dates. Elle d�rive de la classe NumericalValueChecker.
* ----------------------------------------------------------*/
class DateValueChecker 
	extends NumericalValueChecker {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DateValueChecker
	* 
	* Description:
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe NumericalValueChecker. 
	* Elle est appel�e afin de v�rifier que la valeur correspond � une date. 
	* Pour cela, elle ne doit correspondre � une valeur num�rique 
	* (v�rification r�alis�e par la super-classe) compos�e de 14 chiffres.
	* La v�rification de la correspondance entre la s�rie de chiffre est une 
	* date est confi�e � la classe SimpleDateFormat.
	* 
	* Arguments:
	*  - value: La valeur � v�rifier sous forme de cha�ne de caract�res.
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
		// On v�rifie si la valeur est nulle
		ValueCheckInterface not_null = CheckerFactory.createChecker('!');
		if(not_null.checkValueIsCorrect(value) == false) {
			// Il s'agit d'une valeur nulle, la date est suppos�e bonne
			trace_methods.endOfMethod();
			return value_is_correct;
		}
		value_is_correct = super.checkValueIsCorrect(value);
		if(value_is_correct == false) {
			// Il ne s'agit pas d'une valeur num�rique, on peut sortir
			trace_methods.endOfMethod();
			return value_is_correct;
		}
		// Il ne reste plus qu'� essayer de parser la chaine au format I-TOOLS
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
