/*------------------------------------------------------------
* Copyright (c) 2009 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/MaskRule.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: R�gle de masque d'affichage.
* DATE:        27/02/2009
* AUTEUR:      Jing You
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MaskRule.java,v $
* Revision 1.1  2009/03/05 15:58:35  jy
* La classe MaskRule se charge de pr�senter un masque dans le fichier XML
* de description des masques d'affichage.
* Un masque constitue une condition (� d�couper en sous-conditions) et
* l'ensemble de param�tres d'affichage � modifier.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.display;

//
//Imports syst�me
//
import java.util.Vector;

//
//Imports du projet
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.IsisTableDefinition;

/*----------------------------------------------------------
* Nom: MaskRule
* Description:
* Cette classe repr�sente un masque (une r�gle) dans le fichier XML de 
* description des masques d'affichage.
* Un masque constitue une condition (� d�couper en sous-conditions) et 
* l'ensemble de param�tres d'affichage � modifier.
* Une condition est une chaine de caract�res logiquement s�parant par les 
* op�rateurs OR ou/et AND. 
* L'ensemble de param�tres possibles dans une masque:
*  - La couleur de police,
*  - La couleur du fond,
*  - L'inversion de la couleur du fond et la couleur de police ou non,
*  - Le style en italique ou non,
*  - Une ic�ne (nom et colonne).
* 
* Dans son constructeur, il y a des instanciations de classe Condition, qui 
* repr�sente une sous-condition s�parant par les op�rateurs OR ou AND dans la 
* condition.
* Elle permet de d�couper une condition en plusieur sous-conditions, pour cela, 
* elle d�finit une m�thode getSplit(String).
* Elle aussi se charge de tester des conditions, pour cela, elle cr�e une 
* m�thode testCondition() qui appele la fonction matches() de la classe 
* Condition.
* ----------------------------------------------------------*/
public class MaskRule 
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: MaskRule
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. 
	* Elle se charge de d�couper une condition en appelant la m�thode 
	* getSplit(), et de instancier de la classe Condition.
	* 
	* Arguments:
	* - condition: La condition de type String � d�couper.
	* - isisTableDefinition: Une r�f�rence sur un objet de IsisTableDefinition
	* 	qui est initialis� dans la m�thode reloadData() de la classe 
	* 	DisplayProcessor.
	* ----------------------------------------------------------*/
	public MaskRule(
		String condition,
		IsisTableDefinition isisTableDefinition
		)
		throws 
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "MaskRule");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("condition=" + condition);
		trace_arguments.writeTrace("isisTableDefinition=" + 
			isisTableDefinition);
		_condition = null;
		_isConditionTested = false;
		_doesConditionMatch = false;
		
		 int index = 1;
		 String[] condition_array = getSplit(condition);
		 boolean is_and = true;
		 //S'il existe qu'une seule sous-condition.
		 if(condition_array.length == 1) {
			 if(_condition == null) {
				 _condition = new Condition(condition_array[0], 
					isisTableDefinition);
			  }
		 }
		 //On parcourt le tableau jusqu'au derni�re op�rateur OR ou AND. 
		 while(index < condition_array.length) {
			  String condition_element = condition_array[index - 1];
			  if(_condition == null) {
				  _condition = new Condition(condition_element, 
				      isisTableDefinition);
			  }
			  if(condition_array[index].equalsIgnoreCase("or") == true) {
				  is_and = false;
			  }
			  if(condition_array[index].equalsIgnoreCase("and") == true) {
				  is_and = true;
			  }
			  else {
				  if(index+1<condition_array.length && 
				      condition_array[index+1].equals("") == false) {
					  _condition.addLink(condition_array[index+1], is_and, 
							  isisTableDefinition);
				  }
				  else {
					  trace_errors.writeTrace("Syntax erreur de condition.");
					  trace_methods.endOfMethod();
					  throw new InnerException("&ERR_SyntaxErrOfCondition", 
						null, null);
				  }
			  }
			  index += 2;
		 }
		 //Ajouter la derni�re sous-condition.
		 if(index-2 >=0 && index-1 < condition_array.length) {
			 if(condition_array[index-1].equalsIgnoreCase("or") == false 
			     && condition_array[index-1].equalsIgnoreCase("and") == false) {
				 if(condition_array[index-2].equalsIgnoreCase("or") == true) {
					 is_and = false;
				 }
				 if(condition_array[index-2].equalsIgnoreCase("and") == true) {
				     is_and = true;
				 }
				 if(_condition == null) {
				     _condition = new Condition(condition_array[index-1], 
				    		 isisTableDefinition);
				 }
				 else {
					 _condition.addLink(condition_array[index-1], is_and, 
							 isisTableDefinition); 
				 }
			 }
			 else {
				 trace_errors.writeTrace("Syntax erreur de condition.");
				 trace_methods.endOfMethod();
				 throw new InnerException("&ERR_SyntaxErrOfCondition", 
					null, null);
			 }
		 }
		 trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: MaskRule
	*
	* Description:
	* Cette m�thode est le constructeur de la classe.
	* Elle se charge de cr�er un nouveau objet de MaskRule en copiant des 
	* donn�es d'un objet MaskRule existant, sauf qu'on redonne la valeur de
	* variable _isConditionTested et de _conditionMatched � false.
	* 
	* Arguments:
	* - maskRule: Un objet de type MaskRule � copier, sauf qu'on redonne la 
	* valeur de variable _isConditionTested et de _conditionMatched � false.
	* ----------------------------------------------------------*/
	 public MaskRule(
		MaskRule maskRule
		)
	 {
		 Trace trace_methods = TraceAPI.declareTraceMethods("Console",
					"MaskRule", "MaskRule");
		 Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		 trace_methods.beginningOfMethod();
		 trace_arguments.writeTrace("maskRule=" + maskRule);
		 _color = maskRule._color;
		 _backgroundColor = maskRule._backgroundColor;
		 _condition = maskRule._condition;
		 _isReverted = maskRule._isReverted;
		 _isItalic = maskRule._isItalic;
		 _iconName = maskRule._iconName;
		 _iconField = maskRule._iconField;
		 _isConditionTested = false;
		 _doesConditionMatch = false;
		 trace_methods.endOfMethod();
	 }
	 
	/*----------------------------------------------------------
	* Nom: testCondition
	*
	* Description:
	* Cette m�thode se charge de tester les conditions, en appelant la m�thode
	* matches() la classe Condition.
	* 
	* Arguments:
	* - genericTreeObjectNode: L'objet de GenericTreeObjectNode qui contient 
	* 	des donn�es d'une ligne affichi�s dans la table.
	* ----------------------------------------------------------*/
	public void testCondition(
		GenericTreeObjectNode genericTreeObjectNode
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "testCondition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("genericTreeObjectNode=" + 
			genericTreeObjectNode);
		_isConditionTested = true;
		if(_condition != null) {
			_doesConditionMatch = _condition.matches(genericTreeObjectNode);
			_condition = null;
		}
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: getSplit
	*
	* Description:
	* Cette m�thode se charge de d�couper la condition pass�e en param�tre par
	* espace, et de retourner un tableau de sous-conditions et des op�rateurs
	*  OR et AND.
	* 
	* Arguments:
	* - condition: Une chaine de caract�res.
	* 
	* Retourne: Un tableau de String.
	* ----------------------------------------------------------*/
	public String[] getSplit(
		String condition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "getSplit");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("condition=" + condition);
		Vector elements = new Vector();
		UtilStringTokenizer tokenizer = new UtilStringTokenizer(condition, " ");
		StringBuffer value = new StringBuffer("");
		String value_string = "";
		// Boucle sur l'ensemble des �l�ments
		while(tokenizer.hasMoreTokens() == true) {
			// R�cup�ration de l'�l�ment suivant
			String next_token = tokenizer.nextToken();
			if(next_token.equalsIgnoreCase("or") == true || 
					next_token.equalsIgnoreCase("and") == true ) {
				if(value.length() !=0) {
					if(value.lastIndexOf("'") == value.length()-1 
						|| value.lastIndexOf("\"") == value.length()-1) {
						if(value.lastIndexOf("'") == value.length()-1) {
							value = value.replace(value.indexOf("'"), 
								value.indexOf("'")+1, "");
						}
						if(value.lastIndexOf("\"") == value.length()-1) {
							value = value.replace(value.indexOf("\""), 
								value.indexOf("\"")+1, "");
						}
						value = value.delete(value.length()-1, value.length());
					}
					if(value.indexOf("\\") != -1) {
						value = value.replace(value.indexOf("\\"), 
							value.indexOf("\\")+1, "");
					}
				}
				value_string = value.toString();
				// On place la valeur r�cup�r�e dans le vecteur
				elements.add(value_string);
				elements.add(next_token);
				value = new StringBuffer("");
			} 
			else {
				// On stocke la valeur. Eventuellement on l'ajoute � la valeur 
				// pr�c�dente.
				value = value.append(" " + next_token);
				if(tokenizer.hasMoreTokens() == false) {
					if(value.length() !=0) {
						if(value.lastIndexOf("'") == value.length()-1 
							|| value.lastIndexOf("\"") == value.length()-1) {
							if(value.lastIndexOf("'") == value.length()-1) {
								value = value.replace(value.indexOf("'"), 
									value.indexOf("'")+1, "");
							}
							if(value.lastIndexOf("\"") == value.length()-1) {
								value = value.replace(value.indexOf("\""), 
									value.indexOf("\"")+1, "");
							}
							value = value.delete(value.length()-1, 
								value.length());
						}
						if(value.indexOf("\\") != -1) {
							value = value.replace(value.indexOf("\\"), 
								value.indexOf("\\")+1, "");
						}
					}
					value_string = value.toString();
					elements.add(value_string);
				}
			}
		}
		trace_methods.endOfMethod();
		return (String[])elements.toArray(new String[0]);
	}
	
	/*----------------------------------------------------------
	* Nom: getColor
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la couleur de la police d'une r�gle
	* d�finit dans un fichier XML de description des masques d'affichage.
	* 
	* Retourne: String repr�sentant la couleur de police.
	* ----------------------------------------------------------*/
	public String getColor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "getColor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _color;
	}
	
	/*----------------------------------------------------------
	* Nom: getBackgroundColor
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la couleur du fond d'une r�gle d�finit
	* dans le fichier XML de description des masques d'affichage.
	* 
	* Retourne: String repr�sentant la couleur du fond.
	* ----------------------------------------------------------*/
	public String getBackgroundColor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "getBackgroundColor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _backgroundColor;
	}
	
	/*----------------------------------------------------------
	* Nom: isReverted
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la valeur boolean de variable 
	* _isReverted, pour d�terminer si on inverse la couleur du fond et celle
	* de police ou non.
	*
	* Retourne: True si on inversera la couleur de police et celle du fond,
	* false sinon.
	* ----------------------------------------------------------*/
	public boolean isReverted()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "isReverted");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _isReverted;
	}
	
	/*----------------------------------------------------------
	* Nom: isItalic
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la valeur boolean de variable _isItalic,
	* pour d�terminer que un style italique sera appliqu� ou non.
	* 
	* Retourne: True si le style sera en italique, false sinon.
	* ----------------------------------------------------------*/
	public boolean isItalic()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "isItalic");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _isItalic;
	}
	
	/*----------------------------------------------------------
	* Nom: getIconName
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le nom de l'image.
	* 
	* Retourne: Une chaine de caract�res repr�sentant le nom de l'image.
	* ----------------------------------------------------------*/
	public String getIconName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "getIconName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _iconName;
	}
	
	/*----------------------------------------------------------
	* Nom: getIconField
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le nom de colonne.
	* 
	* Retourne: String du nom de colonne.
	* ----------------------------------------------------------*/
	public String getIconField()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "getIconField");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _iconField;
	}
	
	/*----------------------------------------------------------
	* Nom: isConditionTested
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la valeur bool�en de variable
	* _isConditionTested, qui se charge de v�rifier qu'une condition est test�e
	* ou non.
	* 
	* Retourne: True si la condition est deja test�e, false sinon.
	* ----------------------------------------------------------*/
	public boolean isConditionTested()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "isConditionTested");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _isConditionTested;
	}
	
	/*----------------------------------------------------------
	* Nom: conditionMatched
	* 
	* Description:
	* Cette m�thode permet savoir si la condition, contenue dans l'attribut
	* _condition, est v�rifi�e ou non.
	* Elle retourne la valeur de l'attribut _doesConditionMatch.
	* 
	* Retourne: True si la condition est v�rifi�e, false sinon.
	* ----------------------------------------------------------*/
	public boolean doesConditionMatch()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "doesConditionMatch");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _doesConditionMatch;
	}
	
	/*----------------------------------------------------------
	* Nom: setColor
	* 
	* Description:
	* Cette m�thode se charge de d�finir la couleur de police d'un MaskRule.
	* 
	* Arguments:
	* - color: Une chaine de caract�res repr�sentant la couleur de police.
	* ----------------------------------------------------------*/
	public void setColor(
		String color
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "setColor");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("color=" + color);
		_color = color;
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: setBackgroundColor
	* 
	* Description:
	* Cette m�thode se charge de d�finir la couleur du fond d'un MaskRule.
	* 
	* Arguments:
	* - backgroundColor: Une chaine de caract�res repr�sentant la  
	* couleur du fond.
	* ----------------------------------------------------------*/
	public void setBackgroundColor(
		String backgroundColor
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "setBackgroundColor");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("backgroundColor=" + backgroundColor);
		_backgroundColor = backgroundColor;
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: setReverted
	* 
	* Description:
	* Cette m�thode se charge de d�finir une variable boolean isReverted, 
	* qui d�termine de inverser la couleur du fond et la souleur de police 
	* ou non.
	* 
	* Arguments:
	* - isReverted: Variable boolean.
	* ----------------------------------------------------------*/
	public void setReverted(
			boolean isReverted
			)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "setReverted");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("isReverted=" + isReverted);
		_isReverted = isReverted;
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: setItalic
	* 
	* Description:
	* Cette m�thode se charge de d�finir la valeur de variable _isItalic, 
	* qui d�termine la police est de style italic ou non.
	* 
	* Arguments:
	* - italic: Variable boolean.
	* ----------------------------------------------------------*/
	public void setItalic(
		boolean italic
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "setItalic");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("italic=" + italic);
		_isItalic = italic;
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: setIcon
	* 
	* Description:
	* Cette m�thode se charge de d�finir le nom de l'image d'un MaskRule et
	* de d�finir le nom de colonne dont une cellule va afficher une image.
	* 
	* Argumets:
	* - iconName: Le nom de l'image.
	* - iconField: Le nom de colonne pour l'image.
	* ----------------------------------------------------------*/
	public void setIcon(
		String iconName,
		String iconField
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "setIconName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("iconName=" + iconName);
		trace_arguments.writeTrace("iconField=" + iconField);
		_iconName = iconName;
		_iconField = iconField;
		trace_methods.endOfMethod();
	}
	
	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: finalize
	* 
	* Description:
	* Cette m�thode red�finit cela de la classe Object. Elle permet de 
	* r�cup�rer des m�moires.
	* Si un probl�me est d�tect� pendant la r�cup�ration de m�moire, un 
	* exception Throaable doit �tre jet�e.
	* 
	* Toutes les exceptions j�t�e par cette m�thode sont ignor��e.
	* 
	* L�ve:
	* Throwable
	* 
	* ----------------------------------------------------------*/
	protected void finalize() 
		throws 
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"MaskRule", "setIconName");
		trace_methods.beginningOfMethod();
		_condition = null;
		trace_methods.endOfMethod();
	}
	
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _color
	* 
	* Description:
	* Cet attribut est une String indiquant la couleur de police.
	* ----------------------------------------------------------*/
	private String _color;
	
	/*----------------------------------------------------------
	* Nom: _backgroundColor
	* 
	* Description:
	* Cet attribut est une String indiquant la couleur du fond.
	* ----------------------------------------------------------*/
	private String _backgroundColor;
	
	/*----------------------------------------------------------
	* Nom: _condition
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur une sous-condition
	* (une instance de classe Condition).
	* ----------------------------------------------------------*/
	private Condition _condition;
	
	/*----------------------------------------------------------
	* Nom: _isReverted
	* 
	* Description:
	* Cet attribut est un bool�en indiquant si on inverse la couleur 
	* du fond et la couleur de police ou non.
	* ----------------------------------------------------------*/
	private boolean _isReverted;
	
	/*----------------------------------------------------------
	* Nom: _isItalic
	* 
	* Description:
	* Cet attribut est un bool�en indiquant si le style doit �tre 
	* en italic ou non.
	* ----------------------------------------------------------*/
	private boolean _isItalic;
	
	/*----------------------------------------------------------
	* Nom: _iconName
	* 
	* Description:
	* Cet attribut est une String indiquant le nom d'une image.
	* ----------------------------------------------------------*/
	private String _iconName;
	
	/*----------------------------------------------------------
	* Nom: _iconField
	* 
	* Description:
	* Cet attribut est une String indiquant le nom d'une colonne.
	* ----------------------------------------------------------*/
	private String _iconField;
	
	/*----------------------------------------------------------
	* Nom: _isConditionTested
	* 
	* Description:
	* Cet attribut est un bool�en indiquant si la condition est 
	* test�e pour une ligne ou non.
	* L'attribut est positionn� dans la m�thode testCondition().
	* ----------------------------------------------------------*/
	private boolean _isConditionTested;
	
	/*----------------------------------------------------------
	* Nom: _doesConditionMatched
	* 
	* Description:
	* Cet attribut est un bool�en indiquant si la condition est v�rifi�e ou non. 
	* L'attribut est positionn� dans la m�thode testCondition().
	* ----------------------------------------------------------*/
	private boolean _doesConditionMatch;
	
}
