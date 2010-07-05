/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentLog.java,v $
* $Revision: 1.11 $
*
* ------------------------------------------------------------
* DESCRIPTION: Définition des fichiers de log
* DATE:        01/07/2008
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
// Imports système
//

import javax.swing.text.StyledDocument;

//
// Imports du projet
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
 * Nom: ComponentLog
 * 
 * Description:
 * Cette classe représente un fichier de log d'un composant I-SIS. 
 * A un nom de fichier, on associe une description, une racine et un modèle. 
 * Elle est utilisée lors du processus de création ou de 
 * modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class ComponentLog implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentLog
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public ComponentLog() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "ComponentLog");
		trace_methods.beginningOfMethod();
		
		_logName = "";
		_description = "";
		_root = "";
		_pattern = "";
		_flag = ' ';
		
		_newDescription = "";
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: getLogName
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé au fichier.
	 * 
	 * Retourne: Une chaine de caracteres : Le nom du fichier.
	 * ----------------------------------------------------------*/
	public String getLogName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "getLogName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _logName;
	}
	
	/*----------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette méthode retourne la chaine de caracteres 'description'
	 * associé au fichier.
	 * 
	 * Retourne: Une chaine de caractere : La description.
	 * ----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "getDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _description;
	}

	/*----------------------------------------------------------
	 * Nom: getRoot
	 * 
	 * Description:
	 * Cette méthode retourne la racine associée au fichier.
	 * 
	 * Retourne: Une chaine de caractere : Le chemin absolu.
	 * ----------------------------------------------------------*/
	public String getRoot() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "getRoot");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _root;
	}

	/*----------------------------------------------------------
	 * Nom: getPattern
	 * 
	 * Description:
	 * Cette méthode retourne le modèle associé au fichier.
	 * 
	 * Retourne: Une chaine de caractere : Le chemin absolu.
	 * ----------------------------------------------------------*/
	public String getPattern() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "getPattern");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _pattern;
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
				"ComponentLog", "getFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _flag;
	}
	
	/*----------------------------------------------------------
	 * Nom: setLogName
	 * 
	 * Description:
	 * Cette méthode modifie le nom du fichier en lui donnant 
	 * celui passe en parametre.
	 * 
	 * Arguments:
	 *  - name: Le nouveau nom du fichier.
	 * ----------------------------------------------------------*/
	public void setLogName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "setLogName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
		
		_logName = name;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setDescription
	 * 
	 * Description:
	 * Cette méthode modifie la description du fichier en lui 
	 * donnant celle passée en parametre.
	 * 
	 * Arguments:
	 *  - description: La nouvelle description de l'action
	 * ----------------------------------------------------------*/
	public void setDescription(String description) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "setDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		
		_description = description;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setRoot
	 * 
	 * Description:
	 * Cette méthode modifie la racine du fichier en lui 
	 * donnant celui passé en parametre.
	 * 
	 * Arguments:
	 *  - root: La nouvelle racine du fichier.
	 * ----------------------------------------------------------*/
	public void setRoot(String root) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "setRoot");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("root=" + root);
		
		_root = root;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setPattern
	 * 
	 * Description:
	 * Cette méthode modifie le modèle du fichier en lui 
	 * donnant celui passé en parametre.
	 * 
	 * Arguments:
	 *  - pattern: Le nouveau modèle du fichier.
	 * ----------------------------------------------------------*/
	public void setPattern(String pattern) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "setPattern");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("pattern=" + pattern);
		
		_pattern = pattern;
		
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
				"ComponentLog", "setFlag");
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
				"ComponentLog", "getNewDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newDescription;
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
				"ComponentLog", "setNewDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		
		_newDescription = description;
		
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
	 * A partir du résultat, elle va compléter les champs du ComponentLog. 
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
	public boolean loadData(ServiceSessionProxy session, IndexedList context,
			 IsisParameter[] valuesOfTableKey) throws InnerException {
	
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "loadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableLog = "TableLog";
		String NameLog = "Name";
		String DescLog = "Description";
		String RootLog = "Root";
		String PatternLog = "Pattern";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		try {
			
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux fichiers de log.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
		
			TableLog = configuration_api.getString("ComponentConfiguration." +
					"TableLog");
			NameLog = configuration_api.getString("ComponentConfiguration." +
					"TableLog.Name");
			DescLog = configuration_api.getString("ComponentConfiguration." +
					"TableLog.Description");
			RootLog = configuration_api.getString("ComponentConfiguration." +
					"TableLog.Root");
			PatternLog = configuration_api.getString("ComponentConfiguration." +
					"TableLog.Pattern");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
			
		// On sélectionne les colonnes de la table qui nous interressent
		String []column = new String[4];
		column[0] = NameLog;
		column[1] = DescLog;
		column[2] = RootLog;
		column[3] = PatternLog;
			
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
			String [] result = session.getSelectResult(TableLog, 
					column, condition, "", context);
			
			if (result != null) {
				// On construit la définition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableLog);
		
				// Pour chaque ligne résultat de la requête Select
				for (int index = 1 ; index < result.length ; index++) {
					// On récupère les données
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On construit le ComponentLog
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameLog))
								_logName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DescLog))
								_description = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(RootLog))
								_root = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(PatternLog))
								_pattern = object_parameters[index2].value;
							else {
								// Une des colonnes retournées n'est pas bonne, 
								// on signale l'erreur
								trace_errors.writeTrace( "Problème : " + 
										object_parameters[index2] + "innatendu !");
								
							}
						}
					}					
				}
				// On libère l'utilisation de la définition
				manager.releaseTableDefinitionLeasing(table_definition);
			}
		}
		catch (InnerException exception)
		{
			trace_errors.writeTrace(
			"Problème lors du chargement des données !");
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_LoadData", null, null);
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
	 * table correspondant aux fichiers de log. Puis via la classe
	 * ExecutionSurveyor, cette requête sera exécutée. 
	 * 
	 * Paramètres :
	 *   - actionId : Le numéro unique d'action
	 *   - context : Cette référence correspond à l'ensemble des paramètres
	 * 				"exportables" des noeuds traversés pour atteindre le 
	 * 				noeud concerné. A cet ensemble sont ajoutés tous les 
	 * 				paramètres du noeud lui-même.
	 *   - seletedNode : Une référence sur le noeud sélectionné.
	 * 
	 * Retourne: vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public boolean saveData(
		String actionId,
		IndexedList context, 
		GenericTreeObjectNode selectedNode
		) 
		throws 
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentLog", "saveData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		String TableLog = "TableLog";
		String NameLog = "Name";
		String DescLog = "Description";
		String RootLog = "Root";
		String PatternLog = "Pattern";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		if (_flag != 'E') {
		
		
		try {
			
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux fichiers de log.
			TableLog = configuration_api.getString("ComponentConfiguration." +
						"TableLog");
			NameLog = configuration_api.getString("ComponentConfiguration." +
						"TableLog.Name");
			DescLog = configuration_api.getString("ComponentConfiguration." +
						"TableLog.Description");
			RootLog = configuration_api.getString("ComponentConfiguration." +
						"TableLog.Root");
			PatternLog = configuration_api.getString("ComponentConfiguration." +
						"TableLog.Pattern");
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
			String condition = NameLog + "='" + _logName +"' AND " +
							RootLog + "='" + _root + "' AND " +
							PatternLog + "='" + _pattern + "'";
			
			// On lance la requête pour obtenir la définition de la table
			String [] commandSelect = session.getSelectResult(TableLog, 
					column, condition, "", context);
			
			// On construit la définition de la table
			IsisTableDefinition table_definition = 
				TreeNodeFactory.buildDefinitionFromSelectResult(
						commandSelect, TableLog);
			
			// A partir de la ligne résultat, on complète les valeurs de la 
			// requête
			String values = "";
			for (int index = 0 ; index < table_definition.columns.length ; index ++) {
				if (table_definition.columns[index].name.equals(NameLog))
					values += _logName;
				else if (table_definition.columns[index].name.equals(DescLog)) {
					if (_flag != 'S')
						values += _newDescription;
					else
						values += _description;
				}
				else if (table_definition.columns[index].name.equals(RootLog))
					values += _root;
				else if (table_definition.columns[index].name.equals(PatternLog))
					values += _pattern;
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
					TableLog, values, AgentSessionManager.getInstance()
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
				"Problème lors d'une requête sur la table " + TableLog + " !");
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
				"ComponentLog", "end");
		trace_methods.beginningOfMethod();
		
		_logName = null;
		_description = null;
		_root = null;
		_pattern = null;
		
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
		
		String NameLog = MessageManager.getMessage(
				"&ComponentConfiguration_Name"); 
		String DescLog = MessageManager.getMessage(
				"&ComponentConfiguration_Description");
		String RootLog = MessageManager.getMessage(
				"&ComponentConfiguration_Root");
		String PatternLog = MessageManager.getMessage(
				"&ComponentConfiguration_Pattern");
		
		document.insertString(offset,  
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		
		document.insertString(offset, PatternLog + " : "+  
				_pattern + System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		
		document.insertString(offset, RootLog + " : "+  
				_root + System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		
		if( (_flag == 'E' || _flag == 'S') &&  !_description.equals("") ) {
			document.insertString(offset, DescLog + " : " +
					_description + System.getProperty("line.separator", "\n"), 
					document.getStyle("regular"));
		}
		else if ( (_flag != 'E' || _flag != 'S') && !_newDescription.equals("")) {
			document.insertString(offset, DescLog + " : " +
					_newDescription + System.getProperty("line.separator", "\n"), 
					document.getStyle("regular"));
		}
		
		document.insertString(offset, NameLog + " : " + _logName + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément ComponentLog.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		ComponentLog clone = new ComponentLog();
		clone.setLogName(_logName);
		clone.setDescription(_description);
		clone.setPattern(_pattern);
		clone.setRoot(_root);
		clone.setNewDescription(_newDescription);
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _logName
	 *
	 * Description:
	 * Cet attribut maintient le nom correspondant à un fichier
	 * de log du composant.
	 * ----------------------------------------------------------*/
	private String _logName;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 *
	 * Description:
	 * Cet attribut maintient une description sur le fichier de log.
	 * ----------------------------------------------------------*/
	private String _description;
	
	/*----------------------------------------------------------
	 * Nom: _root
	 *
	 * Description:
	 * Cet attribut maintient la racine associée a un fichier 
	 * de log.
	 * ----------------------------------------------------------*/
	private String _root;
	
	/*----------------------------------------------------------
	 * Nom: _pattern
	 *
	 * Description:
	 * Cet attribut maintient le modèle associé a un fichier 
	 * de log.
	 * ----------------------------------------------------------*/
	private String _pattern;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 *
	 * Description:
	 * Cet attribut est une seconde description sur le fichier de 
	 * log. Il s'agit de la nouvelle valeur saisie par l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newDescription;
		
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
