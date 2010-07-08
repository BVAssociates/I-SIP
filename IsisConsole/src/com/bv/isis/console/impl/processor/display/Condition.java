/*------------------------------------------------------------
* Copyright (c) 2009 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/Condition.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe permet de représenter une sous-condition.
* DATE:        27/02/2009
* AUTEUR:      Jing You
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: Condition.java,v $
* Revision 1.1  2009/03/05 15:57:38  jy
* La classe Condition permet de présenter une sous-condition, elle se charge
* de vérifier la sous-condition et de créer un lien vers la sous-condition suivante
* si elle existe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.display;
//
//Imports système
//

//
//Imports du projet
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableColumn;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: Condition
*
* Description:
* Cette classe permet de représenter une sous-condition.
* Elle se charge de définir une condition comme:
* -un nom de colonne,
* -un opérateur,
* -une valeur expectée.
* Elle permet de vérifier chaque sous-condition en comparant avec des données 
* d'une instance de GenericTreeObjectNode.
* Pour cela, elle définit une méthode matches().
* Elle aussi se charge de créer un lien vers la sous-condition suivante, soit 
* OR, soit AND.
* Pour cela, elle crée une méthode addLink().
* ----------------------------------------------------------*/
public class Condition 
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: Condition
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle se charge de découper
	* la condition en 3 parties:
	* - un nom de colonne,
	* - un opérateur,
	* - une valeur expectée.
	* S'il n'y aucun opérateur(=, <, >, etc.) dans une condition ou le nom de 
	* colonne dans la condition n'existe pas dans le tableau de IsisTableColumn
	* , l'exception InnerException doit être levée.
	* 
	* Arguments:
	* - condition: La condition à découper.
	* - isisTableDefinition: Une référence sur un objet de IsisTableDefinition
	* 	qui est initialisé dans la méthode reloadData() de la classe 
	* 	DisplayProcessor.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public Condition(
		String condition,
		IsisTableDefinition isisTableDefinition
		)
		throws 
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Condition", "Condition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		_columnName = null;
		_operator = null;
		_expectedValue = null;
		_andLink = true;
		_type = 0;
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("condition=" + condition);
		trace_arguments.writeTrace("isisTableDefinition=" + 
			isisTableDefinition);
		IsisTableColumn[] isis_table_column = isisTableDefinition.columns;
		int position_temp = -1;
		for(int index = 0 ; index < _operators.length ; index ++) {
			int position = condition.indexOf(_operators[index]);
			if(position != -1) {
				_columnName = condition.substring(0, position).trim();
				// Est-ce que columnName existe ?
				// Stocker le type
				for(int j = 0; j<isis_table_column.length; j++)
				{
					if(isis_table_column[j].name.equals(_columnName)){	
						_type = isis_table_column[j].type;
					}
				}
				if(_type == 0) {
					trace_errors.writeTrace(
						"Le nom de la colonne dans la condition n'existe pas.");
					trace_methods.endOfMethod();
					throw new InnerException("&ERR_ColumnNotExistent",  
						null, null);
				}
				_operator = _operators[index];
				_expectedValue = condition.substring(position + 
					_operator.length(), condition.length()).trim();
				position_temp = position;
				break;
			}
		}
		if(position_temp == -1) {
			trace_errors.writeTrace(
			"Il n'y a pas de opérateur (>=, etc) dans la condition!");
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_NoOperatorInCondition",
				null, null);
		}
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: addLink
	*
	* Description:
	* Cette méthode se charge de créer un lien de type AND ou OR vers la 
	* condition suivante en fonction de la valeur de l'argument typeLink.
	* Si l'attribut _nextCondtion est null(c'est à dire qu'il n'y a pas de 
	* condition suivie), on crée une nouvelle instance de Condition comme la 
	* condition suivante de la condition passant en argument.
	* Sinon(c'est à dire qu'une condition suivie est deja créée), on doit 
	* créer une nouvelle instance Condition comme la condition suivante de 
	* _nextCondition(de la condition courante), en appelant 
	* _nextCondition.addLink().
	* S'il n'y aucun opérateur(=, <, >, etc.) dans une condition, l'exception 
	* InnerException doit être levée.
	* 
	* Arguments:
	* - condition: Une chaine de caractères représentant la condition à lier 
	* 	à la condition courante,
	* - typeLink: Le type de lien, false pour OR, true pour AND.
	* - isisTableDefinition: Une référence sur un objet de IsisTableDefinition
	* 	qui est initialisé dans la méthode reloadData() de la classe 
	* 	DisplayProcessor.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public void addLink(
		String condition, 
		boolean typeLink,
		IsisTableDefinition isisTableDefinition
		) 
		throws 
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Condition", "addLink");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("condition=" + condition);
		trace_arguments.writeTrace("typeLink=" + typeLink);
		trace_arguments.writeTrace("isisTableDefinition=" + 
			isisTableDefinition);
		if(_nextCondition == null) 
		{
			_nextCondition = new Condition(condition, isisTableDefinition);
			_andLink = typeLink;
		}
		else 
		{
			_nextCondition.addLink(condition, typeLink, isisTableDefinition);
		}
		trace_methods.endOfMethod();
		
	}
	
	/*----------------------------------------------------------
	* Nom: matches
	*
	* Description:
	* Cette méthode se charge de tester la condition.
	* 
	* Arguments:
	* - GenericTreeObjectNode: Donées à tester.
	* 
	* Retourne: True si la condition est vérifiée, false sinon.
	* ----------------------------------------------------------*/
	public boolean matches(
		GenericTreeObjectNode genericTreeObjectNode
		) 
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Condition", "matches");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean matches = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("genericTreeObjectNode=" + 
			genericTreeObjectNode);
		if(genericTreeObjectNode == null) {
			// tracer l'erreur
			trace_errors.writeTrace("L'objet de GenericTreeObjectNode est null.");
			trace_methods.endOfMethod();
			return false;
		}
		IsisParameter[] object_isis_parameters  = 
			genericTreeObjectNode.getObjectParameters();
		for(int i = 0; i<object_isis_parameters.length; i++)
		{
			if(object_isis_parameters[i].name.equals(_columnName)){
				//On fait des tests en fonction de type de colonne.
				switch(_type){
					case 's':
						matches = matchesString(
							object_isis_parameters[i].value, _expectedValue);
						break;
					case 'n':
					case 'p':
					case 'd':
						matches = matchesNumeric(
							object_isis_parameters[i].value, _expectedValue);
						break;
					case 'b':
						matches = matchesBoolean(
							object_isis_parameters[i].value, _expectedValue);
						break;
					default:
						trace_errors.writeTrace("Type inconnu: " + _type);
						// On n'a rien à faire
						break;
				}
				if(matches == true && _andLink == false){
					return matches;
				}
				if(_nextCondition != null){
					boolean condition_matches = _nextCondition.matches(
						genericTreeObjectNode);
					if(_andLink == true){
						matches &= condition_matches;
					}
					else{
						matches |= condition_matches;
					}
				}
			}
		}
		trace_methods.endOfMethod();
		return matches;
	}
	
	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: finalize
	* 
	* Description:
	* Cette méthode redéfinit cela de la classe Object. Elle permet de récupérer
	* des mémoires.
	* Si un problème est détecté pendant la récupération de mémoire, un 
	* exception Throaable doit être jetée.
	* 
	* Toutes les exceptions jétée par cette méthode sont ignorée.
	* 
	* Lève:
	* Throwable
	* ----------------------------------------------------------*/
	protected void finalize() 
		throws 
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "setIconName");
	
		trace_methods.beginningOfMethod();
		_nextCondition = null;
		trace_methods.endOfMethod();
	}
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _columnName
	* 
	* Description:
	* Cet attribut est une String indiquant le nom de colonne dans une 
	* condition.
	* ----------------------------------------------------------*/
	private String _columnName;
	
	/*----------------------------------------------------------
	* Nom: _operator
	* 
	* Description:
	* Cet attribut est une String indiquant l'opérateur dans une condition.
	* ----------------------------------------------------------*/
	private String _operator;
	
	/*----------------------------------------------------------
	* Nom: _expectedValue
	* 
	* Description:
	* Cet attribut est une String indiquant la valeur expectée dans une 
	* condition.
	* ----------------------------------------------------------*/
	private String _expectedValue;
	
	/*----------------------------------------------------------
	* Nom: _andLink
	* 
	* Description:
	* Cet attribut est une boolean indiquant si le type de lien est de type AND
	*  ou non.
	* ----------------------------------------------------------*/
	private boolean _andLink;
	
	/*----------------------------------------------------------
	* Nom: _nextCondition
	* 
	* Description:
	* Cet attribut est une référence sur une condition suivante.
	* ----------------------------------------------------------*/
	private Condition _nextCondition;
	
	/*----------------------------------------------------------
	* Nom: _operators
	* 
	* Description:
	* Cet attribut est un tableau de String indiquant l'ensemble des opérateurs.
	* ----------------------------------------------------------*/
	private static String[] _operators = { ">=", ">", "<=", "<", "!=", "=~", 
		"!~", "="};
	
	/*----------------------------------------------------------
	* Nom: _type
	* 
	* Description:
	* Cet attribut maintient une référence sur l'attribut type d'un objet 
	* IsisTableDefinition initialisé dans la méthode reloadData() de la classe 
	* DisplayProcessor.
	* ----------------------------------------------------------*/
	private char _type;

	/*----------------------------------------------------------
	* Nom: matchesString
	*
	* Description:
	* Cette méthode se charge de tester la condition dont le type de colonne
	* est du type String.
	* 
	* Arguments:
	* - value: Une chaine de caratères indiquant la donées d'un objet 
	* 	IsisParameter à tester.
	* - expectedValue: Une chaine de caratères indiquant la valeur expectée 
	* 	dans la condition.
	* 
	* Retourne: True si la condition est vérifiée, false sinon.
	* ----------------------------------------------------------*/
	private boolean matchesString(
		String value, 
		String expectedValue
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"Condition", "matchesString");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("expectedValue=" + expectedValue);
		boolean matches = false;
		if(_operator.equalsIgnoreCase("=~")){
			if(value.indexOf(expectedValue) != -1){
				matches = true;
			}
		}
		else if(_operator.equals("!~")){
			if(value.indexOf(expectedValue) == -1){
				matches = true;
			}
		}
		else if(_operator.equals("=")) {
			if(expectedValue.equalsIgnoreCase(value)){
				matches = true;
			}
		}
		else if(_operator.equals("!=")) {
			if(expectedValue.equalsIgnoreCase(value) == false){
				matches = true;
			}
		}
		else if(_operator.equals(">")){
			if(value.compareToIgnoreCase(expectedValue) > 0){
				matches = true;
			}
		}
		else if(_operator.equals("<")) {
			if(value.compareToIgnoreCase(expectedValue) < 0){
				matches = true;
			}
		}
		else if(_operator.equals(">=")) {
			if(value.compareToIgnoreCase(expectedValue) > 0 ||
				expectedValue.equalsIgnoreCase(value)){
				matches = true;
			}
		}
		else if(_operator.equals("<=")) {
			if(value.compareToIgnoreCase(expectedValue) < 0 ||
				expectedValue.equalsIgnoreCase(value)){
				matches = true;
			}
		}
		trace_methods.endOfMethod();
		return matches;
	}
	
	/*----------------------------------------------------------
	* Nom: matchesNumeric
	*
	* Description:
	* Cette méthode se charge de tester la condition dont le type de colonne
	* est du type Numeric, de type Percent et de type Date.
	* 
	* Arguments:
	* - value: Une chaine de caratères indiquant la donées d'un objet 
	* 	IsisParameter à tester.
	* - expectedValue: Une chaine de caratères indiquant la valeur expectée 
	* 	dans la condition.
	* 
	* Retourne: True si la condition est vérifiée, false sinon.
	* ----------------------------------------------------------*/
	private boolean matchesNumeric(
		String value, 
		String expectedValue
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Condition", "matchesNumericAndPercent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean matches = false;
		Long long_value;
		Long expected_value;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("expectedValue=" + expectedValue);
		try {
			long_value = Long.parseLong(value);
			expected_value = Long.parseLong(expectedValue);
		}
		catch(Exception e) {
			trace_errors.writeTrace(e.toString());
			trace_methods.endOfMethod();
			return false;
		}
		if(_operator.equals(">=")){
			if(long_value >= expected_value){
				matches = true;
			}
		}
		else if(_operator.equals("<=")){
			if(long_value <= expected_value){
				matches = true;
			}
		}
		else if(_operator.equals("=")) {
			if(long_value == expected_value){
				matches = true;
			}
		}
		else if(_operator.equals("!=")) {
			if(long_value != expected_value){
				matches = true;
			}
		}
		else if(_operator.equals(">")) {
			if(long_value > expected_value){
				matches = true;
			}
		}
		else if(_operator.equals("<")) {
			if(long_value < expected_value){
				matches = true;
			}
		}
		trace_methods.endOfMethod();
		return matches;
	}
	
	/*----------------------------------------------------------
	* Nom: matchesBoolean
	*
	* Description:
	* Cette méthode se charge de tester la condition dont le type de colonne
	* est du type Boolean.
	* 
	* Arguments:
	* - value: Une chaine de caratères indiquant la donées d'un objet 
	* 	IsisParameter à tester.
	* - expectedValue: Une chaine de caratères indiquant la valeur expectée 
	* 	dans la condition.
	* 
	* Retourne: True si la condition est vérifiée, false sinon.
	* ----------------------------------------------------------*/
	private boolean matchesBoolean(
		String value, 
		String expectedValue
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Condition", "matchesBoolean");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean matches = false;
		boolean boolean_value = false;
		boolean boolean_expected = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("value=" + value);
		trace_arguments.writeTrace("expectedValue=" + expectedValue);
		if(value.equals("1") == true || 
			value.equalsIgnoreCase("OUI") == true ||
			value.equalsIgnoreCase("O") == true ||
			value.equalsIgnoreCase("TRUE") == true ||
			value.equalsIgnoreCase("T") == true ||
			value.equalsIgnoreCase("VRAI") == true ||
			value.equalsIgnoreCase("V") == true) {
			boolean_value = true;
		}
		if(expectedValue.equals("1") == true || 
			expectedValue.equalsIgnoreCase("OUI") == true ||
			expectedValue.equalsIgnoreCase("O") == true ||
			expectedValue.equalsIgnoreCase("TRUE") == true ||
			expectedValue.equalsIgnoreCase("T") == true ||
			expectedValue.equalsIgnoreCase("VRAI") == true ||
			expectedValue.equalsIgnoreCase("V") == true) {
			boolean_expected = true;
		}
		if(_operator.equals("=")){
			matches = (boolean_value == boolean_expected);
		}
		else if(_operator.equals("!=")){
			matches = (boolean_value != boolean_expected);
		}
		trace_methods.endOfMethod();
		return matches;
	}
}
