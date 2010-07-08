/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentManagementAction.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Définition des actions d'exploitation d'un composant
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
 * Nom: ComponentManagementActions
 * 
 * Description:
 * Cette classe modélise une action d'exploitation d'un composant I-SIS. 
 * Elle se caractérise par un nom, une description et une commande. 
 * Elle est utilisée lors du processus de création ou de 
 * modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class ComponentManagementAction implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentManagementAction
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public ComponentManagementAction() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "ComponentManagementAction");
		trace_methods.beginningOfMethod();
		
		_actionName = "";
		_description = "";
		_command = "";
		_responsabilities = "";
		_flag = ' ';
		
		_newDescription = "";
		_newCommand = "";
		_newResponsabilities = "";
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: getActionName
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé a l'action.
	 * 
	 * Retourne: Une chaine de caracteres : Le nom de l'action.
	 * ----------------------------------------------------------*/
	public String getActionName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "getActionName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _actionName;
	}
	
	/*----------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette méthode retourne la chaine de caracteres 'description'
	 * associé a l'action.
	 * 
	 * Retourne: Une chaine de caractere : La description.
	 * ----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "getDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _description;
	}
	
	/*----------------------------------------------------------
	 * Nom: getCommand
	 * 
	 * Description:
	 * Cette méthode retourne la chaine de caracteres 'command'
	 * associée a l'action.
	 * 
	 * Retourne: Une chaine de caractere : La commande de l'action.
	 * ----------------------------------------------------------*/
	public String getCommand() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "getCommand");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _command;
	}
	
	/*----------------------------------------------------------
	 * Nom: getResponsabilities
	 * 
	 * Description:
	 * Cette méthode retourne la liste des responsabilités associe a 
	 * l'action d'exploitation
	 * 
	 * Retourne: La liste des responsabilités.
	 * ----------------------------------------------------------*/
	public String getResponsabilities() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "getResponsabilities");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _responsabilities;
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
				"ComponentManagementAction", "getFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _flag;
	}
	
	/*----------------------------------------------------------
	 * Nom: setActionName
	 * 
	 * Description:
	 * Cette méthode modifie le nom de l'action en lui donnant 
	 * celui passe en parametre.
	 * 
	 * Arguments:
	 *  - name: Le nouveau nom de l'action
	 * ----------------------------------------------------------*/
	public void setActionName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "setActionName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
		
		_actionName = name;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setDescription
	 * 
	 * Description:
	 * Cette méthode modifie la description de l'action en lui 
	 * donnant celle passée en parametre.
	 * 
	 * Arguments:
	 *  - description: La nouvelle description de l'action
	 * ----------------------------------------------------------*/
	public void setDescription(String description) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "setDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + description);
		
		_description = description;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setCommand
	 * 
	 * Description:
	 * Cette méthode modifie la commande de l'action en lui 
	 * donnant celle passée en parametre.
	 * 
	 * Arguments:
	 *  - command: La nouvelle commande de l'action
	 * ----------------------------------------------------------*/
	public void setCommand(String command) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "setCommand");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + command);
		
		_command = command;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setResponsabilities
	 * 
	 * Description:
	 * Cette méthode modifie la liste des responsabilités par celle 
	 * passée en paramètre.
	 * 
	 * Arguments: 
	 *  - resp : la nouvelle liste des responsabilités.
	 * ----------------------------------------------------------*/
	public void setResponsabilities(String resp) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "setResponsabilities");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("resp=" + resp);

		_responsabilities = resp;
		
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
				"ComponentManagementAction", "setFlag");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("flag=" + flag);

		_flag = flag;
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
				"ComponentManagementAction", "getNewDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newDescription;
	}

	/*----------------------------------------------------------
	 * Nom: getNewCommand
	 * 
	 * Description:
	 * Cette méthode retourne la nouvelle commande saisie.
	 * 
	 * Retourne : La nouvelle commande.
	 * ----------------------------------------------------------*/
	public String getNewCommand() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "getNewCommand");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newCommand;
	}

	/*----------------------------------------------------------
	 * Nom: getNewResponsabilities
	 * 
	 * Description:
	 * Cette méthode retourne la nouvelle liste des responsabilités
	 * saisie.
	 * 
	 * Retourne : La nouvelle liste.
	 * ----------------------------------------------------------*/
	public String getNewResponsabilities() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "getNewResponsabilities");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newResponsabilities;
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
				"ComponentManagementAction", "setNewDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);

		_newDescription = description;

		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewCommand
	 * 
	 * Description:
	 * Cette méthode modifie la nouvelle commande.
	 * 
	 * Paramètre : 
	 *   - commande : La nouvelle commande.
	 * ----------------------------------------------------------*/
	public void setNewCommand(String command) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "setNewCommand");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("command=" + command);

		_newCommand = command;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewResponsabilities
	 * 
	 * Description:
	 * Cette méthode modifie la nouvelle liste des responsabilités.
	 * 
	 * Paramètre : 
	 *   - responsabilities : La nouvelle liste.
	 * ----------------------------------------------------------*/
	public void setNewResponsabilities(String responsabilities) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "setNewResponsabilities");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("responsabilities=" + responsabilities);

		_newResponsabilities = responsabilities;
		
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
	 * A partir du résultat, elle va compléter les champs du 
	 * ComponentManagementAction. 
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
				"ComponentManagementAction", "loadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableActions = "TableActions";
		String NameAction = "Name";
		String DescAction = "Description";
		String CommandAction = "Command";
		String ResponsAction = "Responsabilities";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		
		try {
			
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux actions d'exploitation
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableActions = configuration_api.getString("ComponentConfiguration." +
					"TableActions");
			NameAction = configuration_api.getString("ComponentConfiguration." +
					"TableActions.Name");
			DescAction = configuration_api.getString("ComponentConfiguration." +
					"TableActions.Description");
			CommandAction = configuration_api.getString("ComponentConfiguration." +
					"TableActions.Command");
			ResponsAction = configuration_api.getString("ComponentConfiguration." +
					"TableActions.Responsabilities");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
			
		// On sélectionne les colonnes de la table qui nous interressent
		String []column = new String[4];
		column[0] = NameAction;
		column[1] = DescAction;
		column[2] = CommandAction;
		column[3] = ResponsAction;
			
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
			String [] result = session.getSelectResult(TableActions, 
					column, condition, "", context);
			
			if (result != null) {
				// On construit la définition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableActions);
				
				// Pour chaque ligne résultat de la requête Select
				for (int index = 1 ; index < result.length ; index++) {
					// On récupère les données
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On construit le ComponentManagementAction
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameAction))
								_actionName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DescAction))
								_description = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(CommandAction))
								_command = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(ResponsAction))
								_responsabilities = object_parameters[index2].value;
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
	 * table correspondant aux actions d'exploitation. Puis via la classe
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
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentManagementAction", "saveData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		
		String TableActions = "TableActions";
		String NameAction = "Name";
		String DescAction = "Description";
		String CommandAction = "Command";
		String ResponsAction = "Responsabilities";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		if (_flag != 'E') {
		
		try {
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux actions d'exploitation.
			TableActions = configuration_api.getString("ComponentConfiguration." +
						"TableActions");
			NameAction = configuration_api.getString("ComponentConfiguration." +
						"TableActions.Name");
			DescAction = configuration_api.getString("ComponentConfiguration." +
						"TableActions.Description");
			CommandAction = configuration_api.getString("ComponentConfiguration." +
						"TableActions.Command");
			ResponsAction = configuration_api.getString("ComponentConfiguration." +
						"TableActions.Responsabilities");
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
			String condition = NameAction + "='" + _actionName +"'";
			
			// On lance la requête pour obtenir la définition de la table
			String [] commandSelect = session.getSelectResult(TableActions, 
					column, condition, "", context);
			
			// On construit la définition de la table
			IsisTableDefinition table_definition = 
				TreeNodeFactory.buildDefinitionFromSelectResult(
						commandSelect, TableActions);
			
			// A partir de la ligne résultat, on complète les valeurs de la 
			// requête
			String values = "";
			
			for (int index = 0 ; index < table_definition.columns.length ; index ++) {
				if (table_definition.columns[index].name.equals(NameAction))
					values += _actionName;
				else if (table_definition.columns[index].name.equals(DescAction)) {
					if (_flag != 'S')
						values += _newDescription;
					else
						values += _description;
				}
				else if (table_definition.columns[index].name.equals(CommandAction)) {
					if (_flag != 'S')
						values += _newCommand;
					else
						values += _command;
				}
				else if (table_definition.columns[index].name.equals(ResponsAction)) {
					if (_flag != 'S')
						values += _newResponsabilities;
					else
						values += _responsabilities;
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
			TableActions, values, AgentSessionManager.getInstance()
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
				"Problème lors d'une requête sur la table " + TableActions + " !");
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_Query", null, null);
		}
		
		trace_methods.endOfMethod();
		}
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
				"ComponentManagementAction", "end");
		trace_methods.beginningOfMethod();

		_actionName = null;
		_description = null;
		_command = null;

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
		
		String NameAction = MessageManager.getMessage(
				"&ComponentConfiguration_Name"); 
		String DescAction = MessageManager.getMessage(
				"&ComponentConfiguration_Description");
		String CommandAction = MessageManager.getMessage(
				"&ComponentConfiguration_Command");
		String RespAction = MessageManager.getMessage(
				"&ComponentConfiguration_Responsabilities");
		
		String resp = "";
		String comm = "";
		String desc = "";
		
		if( _flag == 'E' || _flag == 'S') {
			resp = _responsabilities;
			comm = _command;
			desc = _description;
		}
		else if ( (_flag != 'E' || _flag != 'S') && !_newResponsabilities.equals("")) {
			resp = _newResponsabilities;
			comm = _newCommand;
			desc = _newDescription;
		}
		
		document.insertString(offset,  
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		document.insertString(offset, RespAction + " : " +
				resp + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, CommandAction + " : " +
				comm + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, DescAction + " : " +
				desc + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, NameAction + " : " + _actionName + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément 
	 * ComponentManagementAction.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		ComponentManagementAction clone = new ComponentManagementAction();
		clone.setActionName(_actionName);
		clone.setDescription(_description);
		clone.setCommand(_command);
		clone.setResponsabilities(_responsabilities);
		clone.setNewDescription(_newDescription);
		clone.setNewCommand(_newCommand);
		clone.setNewResponsabilities(_newResponsabilities);
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _actionName
	 *
	 * Description:
	 * Cet attribut maintient le nom correspondant à une action
	 * d'exploitation du composant
	 * ----------------------------------------------------------*/
	private String _actionName;

	/*----------------------------------------------------------
	 * Nom: _description
	 *
	 * Description:
	 * Cet attribut maintient une description sur le composant
	 * ----------------------------------------------------------*/
	private String _description;

	/*----------------------------------------------------------
	 * Nom: _command
	 *
	 * Description:
	 * Cette attribut maintient la commande associee a l'action 
	 * d'exploitation
	 * ----------------------------------------------------------*/
	private String _command;

	/*----------------------------------------------------------
	 * Nom : Responsabilities
	 * 
	 * Description :
	 * Ce champ correspond à une liste de responsabilités, dont la 
	 * liste exhaustive peut être récupérée au niveau de la table resp, 
	 * dont chaque élément est séparé des autres par une virgule.
	 * Ce champ doit avoir une valeur, même s'il la liste ne contient 
	 * qu'une seule responsabilté.
	 * ----------------------------------------------------------*/
	private String _responsabilities;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 *
	 * Description:
	 * Cet attribut est une seconde description sur l'action d'exploitation. 
	 * Il s'agit de la nouvelle valeur saisie par l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newDescription;

	/*----------------------------------------------------------
	 * Nom: _command
	 *
	 * Description:
	 * Cet attribut est une seconde commande de l'action d'exploitation. 
	 * Il s'agit de la nouvelle valeur saisie par l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newCommand;

	/*----------------------------------------------------------
	 * Nom : Responsabilities
	 * 
	 * Description :
	 * Ce champ correspond à une liste de responsabilités, dont la 
	 * liste exhaustive peut être récupérée au niveau de la table resp, 
	 * dont chaque élément est séparé des autres par une virgule.
	 * Ce champ doit avoir une valeur, même s'il la liste ne contient 
	 * qu'une seule responsabilté.
	 * Il s'agit de la nouvelle valeur saisie par l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newResponsabilities;
	
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
