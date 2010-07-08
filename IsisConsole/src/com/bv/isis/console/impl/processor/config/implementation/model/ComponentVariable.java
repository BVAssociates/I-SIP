/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentVariable.java,v $
* $Revision: 1.13 $
*
* ------------------------------------------------------------
* DESCRIPTION: Définition des variables d'un composant
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
// Imports du Projet
//
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.admin.AdministrationCommandFactory;
import com.bv.isis.console.impl.processor.admin.ExecutionSurveyor;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableDefinition;

/*----------------------------------------------------------
 * Nom: ComponentVariable
 * 
 * Description:
 * Cette classe modélise une variable associée à un composant I-SIS. 
 * Elle se caractérise par un nom et une description. Elle est 
 * utilisée lors du processus de création ou de modification 
 * d'un composant dans la console.
 * ----------------------------------------------------------*/
public class ComponentVariable implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentVariable
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public ComponentVariable() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "ComponentVariable");
		trace_methods.beginningOfMethod();
		
		_variableName = "";
		_description = "";
		_defaultValue = "";
		_flag = ' ';
		
		_newDescription = "";
		_newDefaultValue = "";
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: getVariableName
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé a la variable.
	 * 
	 * Retourne: Une chaine de caractere : Le nom de la variable.
	 * ----------------------------------------------------------*/
	public String getVariableName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getVariableName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _variableName;
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
				"ComponentVariable", "getDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _description;
	}
	
	/*----------------------------------------------------------
	 * Nom: getFlag
	 * 
	 * Description:
	 * Cette méthode retourne l'état courant.
	 * 
	 * Retourne: Un caractère, l'état courant.
	 * ----------------------------------------------------------*/
	public char getFlag() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _flag;
	}
	
	/*----------------------------------------------------------
	 * Nom: setVariableName
	 * 
	 * Description:
	 * Cette méthode modifie le nom de la variable en lui donnant 
	 * celui passe en parametre.
	 * 
	 * Arguments:
	 * - name: Le nouveau nom de la variable
 	 * ----------------------------------------------------------*/
	public void setVariableName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setVariableName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
		
		_variableName = name;
		
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
				"ComponentTransition", "setDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		
		_description = description;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: getDefaultValue
	 * 
	 * Description:
	 * Cette méthode retourne la valeur par défaut '_defaultValue'
	 * associé au composant.
	 * 
	 * Retourne: Une chaine de caractere : La valeur par défaut.
	 * ----------------------------------------------------------*/
	public String getDefaultValue() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getDefaultValue");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _defaultValue;
	}
	
	/*----------------------------------------------------------
	 * Nom: setDefaultValue
	 * 
	 * Description:
	 * Cette méthode modifie la valeur par défaut du composant en lui 
	 * donnant celle passee en parametre.
	 * 
	 * Arguments:
	 *  - value: La nouvelle valeur par défaut du composant
	 * ----------------------------------------------------------*/
	public void setDefaultValue(String value) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("value=" + value);
		
		_defaultValue = value;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setFlag
	 * 
	 * Description:
	 * Cette méthode modifie l'état courant avec celui passé en entrée.
	 * 
	 * Paramètre : 
	 *   - flag : Le nouvel état courant.
	 * ----------------------------------------------------------*/
	public void setFlag(char flag) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "setFlag");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("flag=" + flag);
		
		_flag = flag;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: getNewDescription
	 * 
	 * Description:
	 * Cette méthode retourne la nouvelle description saisie.
	 * 
	 * Retourne : La nouvelle description.
	 * ----------------------------------------------------------*/
	public String getNewDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getNewDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newDescription;
	}

	/*----------------------------------------------------------
	 * Nom: getNewDefaultValue
	 * 
	 * Description:
	 * Cette méthode retourne la nouvelle valeur par défaut.
	 * 
	 * Retourne : La nouvelle valeur par défaut.
	 * ----------------------------------------------------------*/
	public String getNewDefaultValue() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getNewDefaultValue");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newDefaultValue;
	}

	/*----------------------------------------------------------
	 * Nom: setNewDescription
	 * 
	 * Description:
	 * Cette méthode modifie la nouvelle description.
	 * 
	 * Paramètre : 
	 *   - description : La nouvelle description.
	 * ----------------------------------------------------------*/
	public void setNewDescription(String description) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "setNewDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("description=" + description);
		
		_newDescription = description;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewDefaultValue
	 * 
	 * Description:
	 * Cette méthode modifie la nouvelle valeur par défaut.
	 * 
	 * Paramètre : 
	 *   - defaultValue : La nouvelle valeur par défaut.
	 * ----------------------------------------------------------*/
	public void setNewDefaultValue(String defaultValue) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "setNewDefaultValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("defaultValue=" + defaultValue);
		
		_newDefaultValue = defaultValue;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: loadData
	 * 
	 * Description:
	 * Cette méthode charge le modèle de données à partir de la base 
	 * de données. Elle redéfinit la méthode LoadData de l'interface 
	 * ModelInterface. Après avoir récupérer les noms de la table et des 
	 * champs, cette méthode va effectuer une requête de sélection sur la
	 * table avec comme condition, les champs de la clé passés en paramètre.
	 * A partir du résultat, elle va compléter les champs du ComponentVariable. 
	 * 
	 * Paramètres :
	 *   - session : Une référence sur une session de service ouverte pour
	 *   			effectuer les requêtes de lecture
	 *   - context : Cette référence correspond à l'ensemble des paramètres
	 * 				"exportables" des noeuds traversés pour atteindre le 
	 * 				noeud concerné. A cet ensemble sont ajoutés tous les 
	 * 				paramètres du noeud lui-même.
	 *   - valuesOfTableKey : Un tableau d'IsisParameter contenant les noms
	 *   			et valeurs des variables composant la clé de la table.
	 * 
	 * Retourne: vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public boolean loadData(ServiceSessionProxy session, 
			IndexedList context, IsisParameter[] valuesOfTableKey) 
			throws InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "loadData");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableVariables = "TableVariables";
		String NameVar = "Name";
		String DescVar = "Description";
		String DefaultValueVar = "DefaultValue";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		try {
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux variables.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableVariables = configuration_api.getString("ComponentConfiguration." +
					"TableVariables");
			NameVar = configuration_api.getString("ComponentConfiguration." +
					"TableVariables.Name");
			DescVar = configuration_api.getString("ComponentConfiguration." +
					"TableVariables.Description");
			DefaultValueVar = configuration_api.getString("ComponentConfiguration." +
					"TableVariables.DefaultValue");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
				
		// On sélectionne les colonnes de la table qui nous interressent
		String [] column = new String[3];
		column[0] = NameVar;
		column[1] = DescVar;
		column[2] = DefaultValueVar;
			
		// On crée la condition en fonction des noms et valeurs de la clé de 
		// la table.
		String condition = "";
		for (int index=0 ; index < valuesOfTableKey.length ; index ++) {
			condition += valuesOfTableKey[index].name + "='" + 
							valuesOfTableKey[index].value + "'";
			
			if (index != (valuesOfTableKey.length - 1))
				condition += " AND ";
		}
			
		try {
			// On lance la requête
			String [] result = session.getSelectResult(TableVariables, 
					column, condition, "", context);
			
			if (result != null) {
				// On construit la définition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableVariables);
				
				// Pour chaque ligne résultat de la requête Select
				for (int index = 1 ; index < result.length ; index++) {
					// On récupère les données
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On construit le ComponentVariable
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameVar))
								_variableName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DescVar))
								_description = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DefaultValueVar))
								_defaultValue = object_parameters[index2].value;
							else {
								// Une des colonnes retournées n'est pas bonne, 
								// on signale l'erreur
								trace_errors.writeTrace( "Problème : " + 
										object_parameters[index2] + "innatendu !");
								
							}
						}
					}
					// On libère l'utilisation de la définition
					manager.releaseTableDefinitionLeasing(table_definition);
				}
			}
		}
		catch (InnerException exception)
		{
			trace_errors.writeTrace(
				"Problème lors du chargement des données : " +
				exception.getMessage());
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_LoadData", null, exception);
		}
		
		_flag = 'E';
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	 * Nom: saveData
	 * 
	 * Description: 
	 * Cette méthode sauvegarde le modèle dans les tables. 
	 * Elle implémente la méthode de la classe ModelInterface. Après avoir
	 * récupérer les noms des champs des tables, cette méthode va construire
	 * la requête d'insertion ou de modification d'une données de la 
	 * table correspondant aux variables. Puis via la classe
	 * ExecutionSurveyor, cette requête sera exécutée. 
	 * 
	 * Paramètres :
	 *   - actionId : L'identifiant unique d'action
	 *   - context : Cette référence correspond à l'ensemble des paramètres
	 * 				"exportables" des noeuds traversés pour atteindre le 
	 * 				noeud concerné. A cet ensemble sont ajoutés tous les 
	 * 				paramètres du noeud lui-même.
	 *   - seletedNode : Une référence sur le noeud sélectionné.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public boolean saveData(
		String actionId,
		IndexedList context, 
		GenericTreeObjectNode selectedNode
		) 
		throws 
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "saveData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		
		String TableVariables = "TableVariables";
		String NameVariable = "Name";
		String DescVariable = "Description";
		String DefaultValueVariable = "DefaultValue";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		if (_flag != 'E') {
		
			try {
				ConfigurationAPI configuration_api = new ConfigurationAPI();
				configuration_api.useSection("I-SIS");
				
				// On récupère l'ensemble des noms des champs de la table
				// correspondant aux variables.
				TableVariables = configuration_api.getString(
						"ComponentConfiguration.TableVariables");
				NameVariable = configuration_api.getString(
						"ComponentConfiguration.TableVariables.Name");
				DescVariable = configuration_api.getString(
						"ComponentConfiguration.TableVariables.Description");
				DefaultValueVariable = configuration_api.getString(
						"ComponentConfiguration.TableVariables.DefaultValue");
			}
			catch(Exception exception) {
				trace_errors.writeTrace(
					"Erreur lors de la récupération de la configuration: " +
					exception);
				// Il y a eu une erreur, on continue quand même
			}	
		
		try {
			// On ouvre une session de service à partir du noeud sélectionné
			ServiceSessionProxy session = new ServiceSessionProxy(
					selectedNode.getServiceSession());
			
			// On sélectionne toutes les colonnes de la table
			String [] column = { "" };
			// On construit la condition en fonction de la clé de la table
			String condition = NameVariable + "='" + _variableName +"'";
			
			// On lance la requête pour obtenir la définition de la table
			String [] commandSelect = session.getSelectResult(TableVariables, 
					column, condition, "", context);
			
			// On construit la définition de la table
			IsisTableDefinition table_definition = 
				TreeNodeFactory.buildDefinitionFromSelectResult(
						commandSelect, TableVariables);
			
			// A partir de la ligne résultat, on complète les valeurs de la 
			// requête
			String values = "";
			for (int index = 0 ; index < table_definition.columns.length ; index ++) {
				if (table_definition.columns[index].name.equals(NameVariable))
					values += _variableName;
				else if (table_definition.columns[index].name.equals(DescVariable)) {
					if (_flag != 'S')
						values += _newDescription;
					else
						values += _description;
				}
				else if (table_definition.columns[index].name.equals(DefaultValueVariable)) {
					if (_flag != 'S')
						values += _newDefaultValue;
					else
						values += _defaultValue;
				}
				else
					trace_errors.writeTrace("Problème lors de la création des " +
							"valeurs à sauvegardées : " + 
							table_definition.columns[index].name + "innatendu !");
					
				if (index != (table_definition.columns.length - 1))
					values += table_definition.separator;
			}
			
			// Comme on a effectué une requête de sélection sur la clé de la 
			// table, si le résultat comprends plus d'une ligne, c'est que 
			// l'élément dans la table existait déjà, donc on souhaite modifier
			// cette élément
			String InsertOrReplace = "";
			if (_flag == 'M')
				InsertOrReplace = "Replace";
			// Sinon, c'est que l'élément n'existait pas
			else if (_flag == 'A')
				InsertOrReplace = "Insert";
			else 
				InsertOrReplace = "Remove";
				
			// On construit la requête de création ou de modification
			String command = AdministrationCommandFactory.
			buildAdministrationCommand(InsertOrReplace, 
					TableVariables, values, AgentSessionManager.getInstance()
								.getAgentLayerMode(selectedNode.getAgentName()));
	
			// On exécute la requête
			ExecutionSurveyor surveyor = new ExecutionSurveyor();
			
			surveyor.execute(actionId, command, selectedNode, context);
			
			// On libère l'utilisation de la définition
			manager.releaseTableDefinitionLeasing(table_definition);
			_flag = 'E';
		}
		catch (InnerException exception) 
		{
			trace_errors.writeTrace(
			"Problème lors d'une requête sur la table " + TableVariables + " !");
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_Query", null, null);
		}
		}
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
				"ComponentVariable", "end");
		trace_methods.beginningOfMethod();
		
		_variableName = null;
		_description = null;
		
		trace_methods.endOfMethod();
		
		return true;
	}
	
	/*----------------------------------------------------------
	 * Nom: display
	 * 
	 * Description: 
	 * Cette méthode est appelée lors que l'on souhaite afficher les données
	 * d'un composant. Elle prends en argument un StyledDocument dans 
	 * lequel on ajoute les informations que l'on souhaite afficher à la 
	 * position spécifié par l'offset.
	 * 
	 * Argument :
	 *   - styledDocument : le document dans lequel on souhaite afficher
	 *     les données du composant.
	 *   - offset : La position dans le StyledDocument ou on veut insérer 
	 *     les données
	 * ----------------------------------------------------------*/
	public void display(StyledDocument document, int offset) throws Exception {
		
		String NameVar = MessageManager.getMessage(
				"&ComponentConfiguration_Name"); 
		String DescVar = MessageManager.getMessage(
				"&ComponentConfiguration_Description");
		String DefValVar = MessageManager.getMessage(
				"&ComponentConfiguration_DefaultValue");
		
		String defV = "";
		String desc = "";
		
		if (_flag == 'E' || _flag == 'S') {
			defV = _defaultValue;
			desc = _description;
		} else {
			defV = _newDefaultValue;
			desc = _newDescription;
		}
		
		document.insertString(offset,  
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		document.insertString(offset, DefValVar + " : " +
				defV + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, DescVar + " : " +
				desc + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, NameVar + " : " + _variableName + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément ComponentVariable.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		ComponentVariable clone = new ComponentVariable();
		clone.setVariableName(_variableName);
		clone.setDescription(_description);
		clone.setDefaultValue(_defaultValue);
		
		clone.setNewDescription(_newDescription);
		clone.setNewDefaultValue(_newDefaultValue);
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _variableName
	 * 
	 * Description:
	 * Cet attribut maintient le nom correspondant à une variable
	 * du composant
	 * ----------------------------------------------------------*/
	private String _variableName;

	/*----------------------------------------------------------
	 * Nom: _description
	 * 
	 * Description:
	 * Cet attribut maintient une description sur le composant
	 * ----------------------------------------------------------*/
	private String _description;
	
	/*----------------------------------------------------------
	 * Nom: _defaultValue
	 * 
	 * Description:
	 * Cet attribut représente, pour la variable,  une valeur par défaut sous 
	 * forme d'une chaîne de caractères.
	 * ----------------------------------------------------------*/
	private String _defaultValue;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 * 
	 * Description:
	 * Cet attribut est la nouvelle description du composant saisie par 
	 * l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newDescription;
	
	/*----------------------------------------------------------
	 * Nom: _defaultValue
	 * 
	 * Description:
	 * Cet attribut est la nouvelle valeur par défaut.
	 * ----------------------------------------------------------*/
	private String _newDefaultValue;
	
	/*----------------------------------------------------------
	 * Nom: _flag
	 *
	 * Description:
	 * Cet attribut représente un état associé au composant vis à vis
	 * de la base de données. 
	 * Celui-ci peut prendre 4 valeurs : 
	 *   - 'A' pour un élément à ajouter dans la base de données.
	 *   - 'E' pour une entrée non modifiée depuis la base de données.
	 *   - 'M' pour une entrée de la base de données mais modifiée par
	 *     l'utilisateur.
	 *   - 'S' pour une entrée de la base à supprimer.
	 * ----------------------------------------------------------*/
	private char _flag;
}
