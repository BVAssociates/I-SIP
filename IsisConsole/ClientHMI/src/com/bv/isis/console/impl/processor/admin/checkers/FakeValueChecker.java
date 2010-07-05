/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/FakeValueChecker.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de non v�rification de contrainte
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
* Nom: FakeValueChecker
* 
* Description:
* Cette classe est charg�e d'impl�menter l'absence de contr�le.
* Elle impl�mente l'interface ValueCheckInterface.
* ----------------------------------------------------------*/
class FakeValueChecker 
	implements ValueCheckInterface {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: FakeValueChecker
	* 
	* Description:
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface ValueCheckInterface. Elle 
	* est appel�e afin de v�rifier que la valeur respecte la contrainte 
	* associ� � la classe.
	* Dans le cas pr�sent, il s'agit d'impl�menter l'absence de contrainte, 
	* aussi la valeur est toujours correcte, quelle qu'elle soit.
	* 
	* Arguments:
	*  - value: La valeur � v�rifier sous forme de cha�ne de caract�res.
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
