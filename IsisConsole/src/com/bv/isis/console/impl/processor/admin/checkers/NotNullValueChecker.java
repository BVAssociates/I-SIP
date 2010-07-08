/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/NotNullValueChecker.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de v�rification de valeur non nulle
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
* Nom: NotNullValueChecker
* 
* Description:
* Cette classe est charg�e de la v�rification de la contrainte de non-nullit� 
* de la valeur.
* Elle impl�mente l'interface ValueCheckInterface.
* ----------------------------------------------------------*/
class NotNullValueChecker 
	implements ValueCheckInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: NotNullValueChecker
	* 
	* Description:
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface ValueCheckInterface. Elle 
	* est appel�e afin de v�rifier que la valeur respecte la contrainte de 
	* non-nullit�.
	* La valeur doit �tre non nulle, c'est � dire avoir une valeur non vide.
	* 
	* Arguments:
	*  - value: La valeur � v�rifier sous forme de cha�ne de caract�res.
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
		// On v�rifie que la valeur est non nulle
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
