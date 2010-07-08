/*------------------------------------------------------------
* Copyright (c) 2009 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/MaskRule.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Règle de masque d'affichage.
* DATE:        27/02/2009
* AUTEUR:      Jing You
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MaskRule.java,v $
* Revision 1.1  2009/03/05 15:58:35  jy
* La classe MaskRule se charge de présenter un masque dans le fichier XML
* de description des masques d'affichage.
* Un masque constitue une condition (à découper en sous-conditions) et
* l'ensemble de paramètres d'affichage à modifier.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.display;

//
//Imports système
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
* Cette classe représente un masque (une règle) dans le fichier XML de 
* description des masques d'affichage.
* Un masque constitue une condition (à découper en sous-conditions) et 
* l'ensemble de paramètres d'affichage à modifier.
* Une condition est une chaine de caractères logiquement séparant par les 
* opérateurs OR ou/et AND. 
* L'ensemble de paramètres possibles dans une masque:
*  - La couleur de police,
*  - La couleur du fond,
*  - L'inversion de la couleur du fond et la couleur de police ou non,
*  - Le style en italique ou non,
*  - Une icône (nom et colonne).
* 
* Dans son constructeur, il y a des instanciations de classe Condition, qui 
* représente une sous-condition séparant par les opérateurs OR ou AND dans la 
* condition.
* Elle permet de découper une condition en plusieur sous-conditions, pour cela, 
* elle définit une méthode getSplit(String).
* Elle aussi se charge de tester des conditions, pour cela, elle crée une 
* méthode testCondition() qui appele la fonction matches() de la classe 
* Condition.
* ----------------------------------------------------------*/
public class MaskRule 
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: MaskRule
	*
	* Description:
	* Cette méthode est le constructeur de la classe. 
	* Elle se charge de découper une condition en appelant la méthode 
	* getSplit(), et de instancier de la classe Condition.
	* 
	* Arguments:
	* - condition: La condition de type String à découper.
	* - isisTableDefinition: Une référence sur un objet de IsisTableDefinition
	* 	qui est initialisé dans la méthode reloadData() de la classe 
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
		 //On parcourt le tableau jusqu'au dernière opérateur OR ou AND. 
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
		 //Ajouter la dernière sous-condition.
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
	* Cette méthode est le constructeur de la classe.
	* Elle se charge de créer un nouveau objet de MaskRule en copiant des 
	* données d'un objet MaskRule existant, sauf qu'on redonne la valeur de
	* variable _isConditionTested et de _conditionMatched à false.
	* 
	* Arguments:
	* - maskRule: Un objet de type MaskRule à copier, sauf qu'on redonne la 
	* valeur de variable _isConditionTested et de _conditionMatched à false.
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
	* Cette méthode se charge de tester les conditions, en appelant la méthode
	* matches() la classe Condition.
	* 
	* Arguments:
	* - genericTreeObjectNode: L'objet de GenericTreeObjectNode qui contient 
	* 	des données d'une ligne affichiés dans la table.
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
	* Cette méthode se charge de découper la condition passée en paramètre par
	* espace, et de retourner un tableau de sous-conditions et des opérateurs
	*  OR et AND.
	* 
	* Arguments:
	* - condition: Une chaine de caractères.
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
		// Boucle sur l'ensemble des éléments
		while(tokenizer.hasMoreTokens() == true) {
			// Récupération de l'élément suivant
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
				// On place la valeur récupérée dans le vecteur
				elements.add(value_string);
				elements.add(next_token);
				value = new StringBuffer("");
			} 
			else {
				// On stocke la valeur. Eventuellement on l'ajoute à la valeur 
				// précédente.
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
	* Cette méthode permet de récupérer la couleur de la police d'une règle
	* définit dans un fichier XML de description des masques d'affichage.
	* 
	* Retourne: String représentant la couleur de police.
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
	* Cette méthode permet de récupérer la couleur du fond d'une règle définit
	* dans le fichier XML de description des masques d'affichage.
	* 
	* Retourne: String représentant la couleur du fond.
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
	* Cette méthode permet de récupérer la valeur boolean de variable 
	* _isReverted, pour déterminer si on inverse la couleur du fond et celle
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
	* Cette méthode permet de récupérer la valeur boolean de variable _isItalic,
	* pour déterminer que un style italique sera appliqué ou non.
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
	* Cette méthode permet de récupérer le nom de l'image.
	* 
	* Retourne: Une chaine de caractères représentant le nom de l'image.
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
	* Cette méthode permet de récupérer le nom de colonne.
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
	* Cette méthode permet de récupérer la valeur booléen de variable
	* _isConditionTested, qui se charge de vérifier qu'une condition est testée
	* ou non.
	* 
	* Retourne: True si la condition est deja testée, false sinon.
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
	* Cette méthode permet savoir si la condition, contenue dans l'attribut
	* _condition, est vérifiée ou non.
	* Elle retourne la valeur de l'attribut _doesConditionMatch.
	* 
	* Retourne: True si la condition est vérifiée, false sinon.
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
	* Cette méthode se charge de définir la couleur de police d'un MaskRule.
	* 
	* Arguments:
	* - color: Une chaine de caractères représentant la couleur de police.
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
	* Cette méthode se charge de définir la couleur du fond d'un MaskRule.
	* 
	* Arguments:
	* - backgroundColor: Une chaine de caractères représentant la  
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
	* Cette méthode se charge de définir une variable boolean isReverted, 
	* qui détermine de inverser la couleur du fond et la souleur de police 
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
	* Cette méthode se charge de définir la valeur de variable _isItalic, 
	* qui détermine la police est de style italic ou non.
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
	* Cette méthode se charge de définir le nom de l'image d'un MaskRule et
	* de définir le nom de colonne dont une cellule va afficher une image.
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
	* Cette méthode redéfinit cela de la classe Object. Elle permet de 
	* récupérer des mémoires.
	* Si un problème est détecté pendant la récupération de mémoire, un 
	* exception Throaable doit être jetée.
	* 
	* Toutes les exceptions jétée par cette méthode sont ignoréée.
	* 
	* Lève:
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
	* Cet attribut maintient une référence sur une sous-condition
	* (une instance de classe Condition).
	* ----------------------------------------------------------*/
	private Condition _condition;
	
	/*----------------------------------------------------------
	* Nom: _isReverted
	* 
	* Description:
	* Cet attribut est un booléen indiquant si on inverse la couleur 
	* du fond et la couleur de police ou non.
	* ----------------------------------------------------------*/
	private boolean _isReverted;
	
	/*----------------------------------------------------------
	* Nom: _isItalic
	* 
	* Description:
	* Cet attribut est un booléen indiquant si le style doit être 
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
	* Cet attribut est un booléen indiquant si la condition est 
	* testée pour une ligne ou non.
	* L'attribut est positionné dans la méthode testCondition().
	* ----------------------------------------------------------*/
	private boolean _isConditionTested;
	
	/*----------------------------------------------------------
	* Nom: _doesConditionMatched
	* 
	* Description:
	* Cet attribut est un booléen indiquant si la condition est vérifiée ou non. 
	* L'attribut est positionné dans la méthode testCondition().
	* ----------------------------------------------------------*/
	private boolean _doesConditionMatch;
	
}
