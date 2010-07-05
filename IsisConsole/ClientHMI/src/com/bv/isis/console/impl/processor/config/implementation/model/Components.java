/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/Components.java,v $
* $Revision: 1.9 $
*
* ------------------------------------------------------------
* DESCRIPTION: Fichier de nommage d'un composant
* DATE:        06/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      impl.processor.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.implementation.model;

//
//Imports syst�me
//
import javax.swing.text.StyledDocument;

//
//Imports Projet
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.IsisParameter;

/*----------------------------------------------------------
 * Nom: Components
 * 
 * Description:
 * Cette classe mod�lise un composant I-SIS. Elle se caract�rise 
 * par un nom et une description. Elle est utilis�e lors du processus
 * de cr�ation ou de modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class Components implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: Components
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public Components() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Components", "Components");
		trace_methods.beginningOfMethod();
		
		_componentName = "";
		_description = "";
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: getComponentName
	 * 
	 * Description:
	 * Cette m�thode retourne le nom associ� au composant.
	 * 
	 * Retourne: Une chaine de caractere : Le nom du composant.
	 * ----------------------------------------------------------*/
	public String getComponentName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Components", "getComponentName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _componentName;
	}
	
	/*----------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette m�thode retourne la chaine de caracteres 'description'
	 * associ� au composant.
	 * 
	 * Retourne: Une chaine de caractere : La description.
	 * ----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Components", "getDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _description;
	}
	
	/*----------------------------------------------------------
	 * Nom: setComponentName
	 * 
	 * Description:
	 * Cette m�thode modifie le nom du composant en lui donnant celui
	 * passe en parametre.
	 * 
	 * Arguments:
	 *  - name: Le nouveau nom du composant
	 * ----------------------------------------------------------*/
	public void setComponentName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Components", "setComponentName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
				
		_componentName = name;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setDescription
	 * 
	 * Description:
	 * Cette m�thode modifie la description du composant en lui 
	 * donnant celle passee en parametre.
	 * 
	 * Arguments:
	 * - description: La nouvelle description du composant
	 * ----------------------------------------------------------*/
	public void setDescription(String description) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Components", "setDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		
		_description = description;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: loadData
	 * 
	 * Description: 
	 * Cette m�thode charge le mod�le de donn�es � partir 
	 * de la base de donn�es. 
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon
	 * ----------------------------------------------------------*/
	public boolean loadData(ServiceSessionProxy session, 
			IndexedList context, IsisParameter[] valuesOfTableKey) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "loadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		trace_methods.endOfMethod();
		
		return true;
	}

	/*----------------------------------------------------------
	 * Nom: saveData
	 * 
	 * Description: 
	 * Cette m�thode enregistre le mod�le de donn�es en base 
	 * de donn�es.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false)
	 * sinon
	 * ----------------------------------------------------------*/
	public boolean saveData(
		String actionId,
		IndexedList context, 
		GenericTreeObjectNode selectedNode
		)
		throws 
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "saveData");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}

	/*----------------------------------------------------------
	 * Nom: end
	 * 
	 * Description: 
	 * Cette m�thode impl�mente la m�thode de l'interface ModelInterface.
	 * Cette m�thode est appel�e lors de la destruction de l'assistant. Elle est 
	 * utiliser pour lib�rer l'espace m�moire utilis� par les variables des
	 * classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	
	public boolean end() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"Components", "setComponentName");
		trace_methods.beginningOfMethod();
		
		_componentName = null;
		_description = null;
		
		trace_methods.endOfMethod();
		
		return true;
	}

	/*----------------------------------------------------------
	 * Nom: display
	 * 
	 * Description: 
	 * Cette m�thode est appel�e lors que l'on souhaite afficher les donn�es
	 * d'un composant. Elle prends en argument un DefaultStyledDocument dans 
	 * lequel on ajoute les informations que l'on souhaite afficher.
	 * 
	 * Argument :
	 *   - styledDocument : e document dans lequel on souhaite afficher
	 *     les donn�es du composant.
	 * ----------------------------------------------------------*/
	public void display(StyledDocument styledDocument, int offset) throws Exception {
			
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette m�thode permet de retourner une copie de l'�l�ment Components.
	 * 
	 * Retourne :le clone de l'�l�ment du mod�le sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		Components clone = new Components();
		clone.setComponentName(_componentName);
		clone.setDescription(_description);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _componentName
	 * 
	 * Description:
	 * Cet attribut maintient le nom correspondant � un composant
	 * ----------------------------------------------------------*/
	private String _componentName;

	/*----------------------------------------------------------
	 * Nom: _description
	 * 
	 * Description:
	 * Cet attribut maintient une description sur le composant
	 * ----------------------------------------------------------*/
	private String _description;
}
