/*------------------------------------------------------------
* Copyright (c) 2007 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/checkers/CheckerFactory.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de construction de vérificateurs de valeurs
* DATE:        14/11/2007
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin.checkers
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: CheckerFactory.java,v $
* Revision 1.1  2007/12/07 10:32:06  tz
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
* Nom: CheckerFactory
* 
* Description:
* Cette classe abstraite est une classe technique chargée de la construction 
* d'objets de vérifications de valeurs.
* Ces objets sont construits via la méthode createChecker() en fonction du 
* paramètre passé en argument.
* ----------------------------------------------------------*/
public abstract class CheckerFactory {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: createChecker
	* 
	* Description:
	* Cette méthode statique permet de construire un objet de vérification de 
	* valeurs, implémentant l'interface ValueCheckInterface, en fonction de 
	* l'argument.
	* L'objet implémentant l'interface dépend du type de colonne concernée :
	*  - Type 'd': DateValueChecker,
	*  - Type 'n': NumericalValueChecker,
	*  - Type 'p': NumericalValueChecker,
	*  - Type 'b': FakeValueChecker,
	*  - Type 's': FakeValueChecker,
	*  - Type '!': NotNullValueChecker.
	* 
	* Arguments:
	*  - columnType: Le type de colonne pour laquelle créer un objet de 
	*    vérification de valeur.
	* 
	* Retourne: Une référence sur un objet ValueCheckInterface.
	* ----------------------------------------------------------*/
	public static ValueCheckInterface createChecker(
		char columnType
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"CheckerFactory", "createChecker");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		ValueCheckInterface check_interface = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("columnType=" + columnType);
		// On va créer le bon objet de vérification de valeur en fonction du
		// type de la colonne
		switch(columnType) {
		case 'd':
			check_interface = new DateValueChecker();
			break;
		case 'n':
		case 'p':
			check_interface = new NumericalValueChecker();
			break;
		case 'b':
		case 's':
			check_interface = new FakeValueChecker();
			break;
		case '!':
			check_interface = new NotNullValueChecker();
			break;
		default:
			trace_errors.writeTrace("Type de colonne invalide: " + columnType);
		}
		trace_methods.endOfMethod();
		return check_interface;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
