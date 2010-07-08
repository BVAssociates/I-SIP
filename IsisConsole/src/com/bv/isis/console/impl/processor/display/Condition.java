/*------------------------------------------------------------
* Copyright (c) 2009 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/Condition.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe permet de repr�senter une sous-condition.
* DATE:        27/02/2009
* AUTEUR:      Jing You
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: Condition.java,v $
* Revision 1.1  2009/03/05 15:57:38  jy
* La classe Condition permet de pr�senter une sous-condition, elle se charge
* de v�rifier la sous-condition et de cr�er un lien vers la sous-condition suivante
* si elle existe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.display;
//
//Imports syst�me
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
* Cette classe permet de repr�senter une sous-condition.
* Elle se charge de d�finir une condition comme:
* -un nom de colonne,
* -un op�rateur,
* -une valeur expect�e.
* Elle permet de v�rifier chaque sous-condition en comparant avec des donn�es 
* d'une instance de GenericTreeObjectNode.
* Pour cela, elle d�finit une m�thode matches().
* Elle aussi se charge de cr�er un lien vers la sous-condition suivante, soit 
* OR, soit AND.
* Pour cela, elle cr�e une m�thode addLink().
* ----------------------------------------------------------*/
public class Condition 
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: Condition
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle se charge de d�couper
	* la condition en 3 parties:
	* - un nom de colonne,
	* - un op�rateur,
	* - une valeur expect�e.
	* S'il n'y aucun op�rateur(=, <, >, etc.) dans une condition ou le nom de 
	* colonne dans la condition n'existe pas dans le tableau de IsisTableColumn
	* , l'exception InnerException doit �tre lev�e.
	* 
	* Arguments:
	* - condition: La condition � d�couper.
	* - isisTableDefinition: Une r�f�rence sur un objet de IsisTableDefinition
	* 	qui est initialis� dans la m�thode reloadData() de la classe 
	* 	DisplayProcessor.
	* 
	* L�ve: InnerException.
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
			"Il n'y a pas de op�rateur (>=, etc) dans la condition!");
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
	* Cette m�thode se charge de cr�er un lien de type AND ou OR vers la 
	* condition suivante en fonction de la valeur de l'argument typeLink.
	* Si l'attribut _nextCondtion est null(c'est � dire qu'il n'y a pas de 
	* condition suivie), on cr�e une nouvelle instance de Condition comme la 
	* condition suivante de la condition passant en argument.
	* Sinon(c'est � dire qu'une condition suivie est deja cr��e), on doit 
	* cr�er une nouvelle instance Condition comme la condition suivante de 
	* _nextCondition(de la condition courante), en appelant 
	* _nextCondition.addLink().
	* S'il n'y aucun op�rateur(=, <, >, etc.) dans une condition, l'exception 
	* InnerException doit �tre lev�e.
	* 
	* Arguments:
	* - condition: Une chaine de caract�res repr�sentant la condition � lier 
	* 	� la condition courante,
	* - typeLink: Le type de lien, false pour OR, true pour AND.
	* - isisTableDefinition: Une r�f�rence sur un objet de IsisTableDefinition
	* 	qui est initialis� dans la m�thode reloadData() de la classe 
	* 	DisplayProcessor.
	* 
	* L�ve: InnerException.
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
	* Cette m�thode se charge de tester la condition.
	* 
	* Arguments:
	* - GenericTreeObjectNode: Don�es � tester.
	* 
	* Retourne: True si la condition est v�rifi�e, false sinon.
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
						// On n'a rien � faire
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
	* Cette m�thode red�finit cela de la classe Object. Elle permet de r�cup�rer
	* des m�moires.
	* Si un probl�me est d�tect� pendant la r�cup�ration de m�moire, un 
	* exception Throaable doit �tre jet�e.
	* 
	* Toutes les exceptions j�t�e par cette m�thode sont ignor�e.
	* 
	* L�ve:
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
	* Cet attribut est une String indiquant l'op�rateur dans une condition.
	* ----------------------------------------------------------*/
	private String _operator;
	
	/*----------------------------------------------------------
	* Nom: _expectedValue
	* 
	* Description:
	* Cet attribut est une String indiquant la valeur expect�e dans une 
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
	* Cet attribut est une r�f�rence sur une condition suivante.
	* ----------------------------------------------------------*/
	private Condition _nextCondition;
	
	/*----------------------------------------------------------
	* Nom: _operators
	* 
	* Description:
	* Cet attribut est un tableau de String indiquant l'ensemble des op�rateurs.
	* ----------------------------------------------------------*/
	private static String[] _operators = { ">=", ">", "<=", "<", "!=", "=~", 
		"!~", "="};
	
	/*----------------------------------------------------------
	* Nom: _type
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur l'attribut type d'un objet 
	* IsisTableDefinition initialis� dans la m�thode reloadData() de la classe 
	* DisplayProcessor.
	* ----------------------------------------------------------*/
	private char _type;

	/*----------------------------------------------------------
	* Nom: matchesString
	*
	* Description:
	* Cette m�thode se charge de tester la condition dont le type de colonne
	* est du type String.
	* 
	* Arguments:
	* - value: Une chaine de carat�res indiquant la don�es d'un objet 
	* 	IsisParameter � tester.
	* - expectedValue: Une chaine de carat�res indiquant la valeur expect�e 
	* 	dans la condition.
	* 
	* Retourne: True si la condition est v�rifi�e, false sinon.
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
	* Cette m�thode se charge de tester la condition dont le type de colonne
	* est du type Numeric, de type Percent et de type Date.
	* 
	* Arguments:
	* - value: Une chaine de carat�res indiquant la don�es d'un objet 
	* 	IsisParameter � tester.
	* - expectedValue: Une chaine de carat�res indiquant la valeur expect�e 
	* 	dans la condition.
	* 
	* Retourne: True si la condition est v�rifi�e, false sinon.
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
	* Cette m�thode se charge de tester la condition dont le type de colonne
	* est du type Boolean.
	* 
	* Arguments:
	* - value: Une chaine de carat�res indiquant la don�es d'un objet 
	* 	IsisParameter � tester.
	* - expectedValue: Une chaine de carat�res indiquant la valeur expect�e 
	* 	dans la condition.
	* 
	* Retourne: True si la condition est v�rifi�e, false sinon.
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
