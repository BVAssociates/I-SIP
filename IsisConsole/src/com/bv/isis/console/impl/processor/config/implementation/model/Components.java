/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
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
// Déclaration du package
package com.bv.isis.console.impl.processor.config.implementation.model;

//
//Imports système
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
 * Cette classe modélise un composant I-SIS. Elle se caractérise 
 * par un nom et une description. Elle est utilisée lors du processus
 * de création ou de modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class Components implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: Components
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
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
	 * Cette méthode retourne le nom associé au composant.
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
	 * Cette méthode retourne la chaine de caracteres 'description'
	 * associé au composant.
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
	 * Cette méthode modifie le nom du composant en lui donnant celui
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
	 * Cette méthode modifie la description du composant en lui 
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
	 * Cette méthode charge le modèle de données à partir 
	 * de la base de données. 
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
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
	 * Cette méthode enregistre le modèle de données en base 
	 * de données.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false)
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
	 * Cette méthode implémente la méthode de l'interface ModelInterface.
	 * Cette méthode est appelée lors de la destruction de l'assistant. Elle est 
	 * utiliser pour libèrer l'espace mémoire utilisé par les variables des
	 * classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
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
	 * Cette méthode est appelée lors que l'on souhaite afficher les données
	 * d'un composant. Elle prends en argument un DefaultStyledDocument dans 
	 * lequel on ajoute les informations que l'on souhaite afficher.
	 * 
	 * Argument :
	 *   - styledDocument : e document dans lequel on souhaite afficher
	 *     les données du composant.
	 * ----------------------------------------------------------*/
	public void display(StyledDocument styledDocument, int offset) throws Exception {
			
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément Components.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ModelInterface.
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
	 * Cet attribut maintient le nom correspondant à un composant
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
