/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentStates.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Définition des états d'un composant
* DATE:        06/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      impl.processor.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* Révision 1.3 15/07/2008  fcd
* Prise en compte de l'état Quelconque dans la sauvegarde
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
 * Nom: ComponentStates
 * 
 * Description:
 * Cette classe modélise l'état que peut prendre un composant I-SIS. 
 * Elle se caractérise par un nom, une description, une commande, un
 * code de retour et une chaine de retour. Elle est utilisée lors du 
 * processus de création ou de modification d'un composant dans 
 * la console.
 * ----------------------------------------------------------*/
public class ComponentStates implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentStates
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public ComponentStates() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "ComponentStates");
		trace_methods.beginningOfMethod();
		
		_stateName = "";
		_description = "";
		_command = "";
		_returnCodePattern = "";
		_returnStringPattern = "";
		_flag = ' ';
		
		_newDescription = "";
		_newCommand = "";
		_newReturnCodePattern = "";
		_newReturnStringPattern = "";
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: getStateName
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé a l'etat.
	 * 
	 * Retourne: Une chaine de caracteres : Le nom de l'etat.	
	 * ----------------------------------------------------------*/
	public String getStateName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "getStateName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _stateName;
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
				"ComponentStates", "getDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _description;
	}
	
	/*----------------------------------------------------------
	 * Nom: getCommand
	 * 
	 * Description:
	 * Cette méthode retourne la chaine de caracteres 'command'
	 * associée a l'etat.
	 * 
	 * Retourne: Une chaine de caractere : La commande de l'etat.	
	 * ----------------------------------------------------------*/
	public String getCommand() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "getCommand");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _command;
	}
	
	/*----------------------------------------------------------
	 * Nom: getReturnCodePattern
	 * 
	 * Description:
	 * Cette méthode retourne le code de retour de l'etat sous 
	 * forme d'un entier.
	 * 
	 * Retourne: Une chaîne de caractères : Le code de retour.
	 * ----------------------------------------------------------*/
	public String getReturnCodePattern() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "getReturnCodePattern");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _returnCodePattern;
	}

	/*----------------------------------------------------------
	 * Nom: getReturnStringPattern
	 * 
	 * Description:
	 * Cette méthode retourne la chaine de caracteres 'returnStringPattern'
	 * correspondant a la chaine de retrour de l'etat.
	 * 
	 * Retourne: Une chaine de caractere : La chaine de retour.	
	 * ----------------------------------------------------------*/
	public String getReturnStringPattern() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "getReturnStringPattern");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _returnStringPattern;
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
				"ComponentStates", "getFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _flag;
	}
	
	/*----------------------------------------------------------
	 * Nom: setStateName
	 * 
	 * Description:
	 * Cette méthode modifie le nom de l'etat en lui donnant 
	 * celui passe en parametre.
	 * 
	 * Arguments:
	 *  - name: Le nouveau nom de l'etat
 	 * ----------------------------------------------------------*/
	public void setStateName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "setStateName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
		
		_stateName = name;
		
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
	 *  - description: La nouvelle description du composant
	 * ----------------------------------------------------------*/
	public void setDescription(String description) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "setDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		
		_description = description;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setCommand
	 * 
	 * Description:
	 * Cette méthode modifie la commande de l'etat en lui 
	 * donnant celle passée en parametre.
	 * 
	 * Arguments:
	 *  - command: La nouvelle commande de l'etat	
	 * ----------------------------------------------------------*/
	public void setCommand(String command) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "setCommand");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("command=" + command);
		
		_command = command;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setReturnCodePattern
	 * 
	 * Description:
	 * Cette méthode modifie le code de retour de l'etat en lui 
	 * donnant celui passé en parametre.
	 * 
	 * Arguments:
	 *  - code: Le nouveau code de retour	
	 * ----------------------------------------------------------*/
	public void setReturnCodePattern(String code) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "setReturnCodePattern");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("code=" + code);
		
		_returnCodePattern = code;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setReturnStringPattern
	 * 
	 * Description:
	 * Cette méthode modifie la chaine de retour de l'etat en lui 
	 * donnant celle passée en parametre.
	 * 
	 * Arguments:
	 *  - returnStringPattern: La nouvelle chaine de retour 
 	 * ----------------------------------------------------------*/
	public void setReturnStringPattern(String returnStringPattern) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "setReturnStringPattern");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("returnStringPattern=" + returnStringPattern);
		
		_returnStringPattern = returnStringPattern;
		
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
				"ComponentStates", "setFlag");
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
				"ComponentStates", "getNewDescription");
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
				"ComponentStates", "getNewCommand");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newCommand;
	}

	/*----------------------------------------------------------
	 * Nom: getNewReturnCodePattern
	 * 
	 * Description:
	 * Cette méthode retourne le nouveau code de retour.
	 * 
	 * Retourne : Le nouveau code de retour.
	 * ----------------------------------------------------------*/
	public String getNewReturnCodePattern() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "getNewReturnCodePattern");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newReturnCodePattern;
	}

	/*----------------------------------------------------------
	 * Nom: getNewReturnStringPattern
	 * 
	 * Description:
	 * Cette méthode retourne la nouvelle chaine de retour.
	 * 
	 * Retourne : La nouvelle chaine de retour.
	 * ----------------------------------------------------------*/
	public String getNewReturnStringPattern() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "getNewReturnStringPattern");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newReturnStringPattern;
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
				"ComponentStates", "setNewDescription");
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
				"ComponentStates", "setNewCommand");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("command=" + command);
		
		_newCommand = command;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewReturnCodePattern
	 * 
	 * Description:
	 * Cette méthode modifie le nouveau code de retour.
	 * 
	 * Paramètre : 
	 *   - returnCodePattern : Le nouveau code de retour.
	 * ----------------------------------------------------------*/
	public void setNewReturnCodePattern(String returnCodePattern) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "setNewReturnCodePattern");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("returnCodePattern=" + returnCodePattern);
		
		_newReturnCodePattern = returnCodePattern;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewReturnStringPattern
	 * 
	 * Description:
	 * Cette méthode modifie la nouvelle chaine de retour.
	 * 
	 * Paramètre : 
	 *   - returnStringPattern : La nouvelle chaine de retour.
	 * ----------------------------------------------------------*/
	public void setNewReturnStringPattern(String returnStringPattern) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "setNewReturnStringPattern");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("returnStringPattern=" + returnStringPattern);
		
		_newReturnStringPattern = returnStringPattern;
		
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
	 * A partir du résultat, elle va compléter les champs du ComponentStates. 
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
				"ComponentStates", "loadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableStates = "TableStates";
		String NameState = "Name";
		String DescState = "Description";
		String CommandState = "Command";
		String ReturnCodeState = "ReturnCodePattern";
		String ReturnStringState = "ReturnStringPattern";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		try {
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux états.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableStates = configuration_api.getString("ComponentConfiguration." +
					"TableStates");
			NameState = configuration_api.getString("ComponentConfiguration." +
					"TableStates.Name");
			DescState = configuration_api.getString("ComponentConfiguration." +
					"TableStates.Description");
			CommandState = configuration_api.getString("ComponentConfiguration." +
					"TableStates.Command");
			ReturnCodeState = configuration_api.getString("ComponentConfiguration." +
					"TableStates.ReturnCodePattern");
			ReturnStringState = configuration_api.getString("ComponentConfiguration." +
					"TableStates.ReturnStringPattern");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
		
		// On sélectionne les colonnes de la table qui nous interressent
		String [] column = new String[5];
		column[0] = NameState;
		column[1] = DescState;
		column[2] = CommandState;
		column[3] = ReturnCodeState;
		column[4] = ReturnStringState;
			
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
			String [] result = session.getSelectResult(TableStates, 
					column, condition, "", context);
			
			if (result != null) {
				// On construit la définition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
							result, TableStates);
			
				// Pour chaque ligne résultat de la requête Select
				for (int index = 1 ; index < result.length ; index++) {
					// On récupère les données
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On construit le ComponentStates
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameState))
								_stateName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DescState))
								_description = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(CommandState))
								_command = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(ReturnCodeState))
								_returnCodePattern = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(ReturnStringState))
								_returnStringPattern = object_parameters[index2].value;
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
	 * table correspondant aux états. Puis via la classe
	 * ExecutionSurveyor, cette requête sera exécutée. 
	 * 
	 * Paramètres :
	 *   - actionId : L'identifiant unique de l'action
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
				"ComponentStates", "saveData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		String TableStates = "TableStates";
		String NameState = "Name";
		String DescState = "Description";
		String CommandState = "Command";
		String ReturnCodePatternState = "ReturnCodePattern";
		String ReturnStringPatternState = "ReturnStringPattern";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		if (!_stateName.equals(MessageManager.getMessage(
			"&ComponentConfiguration_AnyState")))
		{
			if (_flag != 'E') {
			
			try {
			
				ConfigurationAPI configuration_api = new ConfigurationAPI();
				configuration_api.useSection("I-SIS");
				
				// On récupère l'ensemble des noms des champs de la table
				// correspondant aux états.
				TableStates = configuration_api.getString("ComponentConfiguration." +
							"TableStates");
				NameState = configuration_api.getString("ComponentConfiguration." +
							"TableStates.Name");
				DescState = configuration_api.getString("ComponentConfiguration." +
							"TableStates.Description");
				CommandState = configuration_api.getString("ComponentConfiguration." +
							"TableStates.Command");
				ReturnCodePatternState = configuration_api.getString("" +
						"ComponentConfiguration.TableStates.ReturnCodePattern");
				ReturnStringPatternState = configuration_api.getString(
						"ComponentConfiguration.TableStates.ReturnStringPattern");
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
				String condition = NameState + "='" + _stateName +"'";
					
				
				String [] commandSelect = session.getSelectResult(TableStates, 
						column, condition, "", context);
				
				// On construit la définition de la table
				IsisTableDefinition table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
							commandSelect, TableStates);
				
				// A partir de la ligne résultat, on complète les valeurs de la 
				// requête
				String values = "";
				for (int index = 0 ; index < table_definition.columns.length ; index ++) {
					if (table_definition.columns[index].name.equals(NameState))
						values += _stateName;
					else if (table_definition.columns[index].name.equals(DescState)) {
						if (_flag != 'S')
							values += _newDescription;
						else
							values += _description;
					}
					else if (table_definition.columns[index].name.equals(CommandState)) {
						if (_flag != 'S')
							values += _newCommand;
						else
							values += _command;
					}
					else if (table_definition.columns[index].name.equals(ReturnCodePatternState)) {
						if (_flag != 'S')
							values += _newReturnCodePattern;
						else
							values += _returnCodePattern;
					}
					else if (table_definition.columns[index].name.equals(ReturnStringPatternState)) {
						if (_flag != 'S')
							values += _newReturnStringPattern;
						else
							values +=_returnStringPattern;
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
						TableStates, values, AgentSessionManager.getInstance()
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
					"Problème lors d'une requête sur la table " + TableStates + " !");
				// C'est une erreur, on la signale
				throw new InnerException("&ERR_Query", null, null);
			}
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
	 * sinon
	 * ----------------------------------------------------------*/
	public boolean end() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentStates", "end");
		trace_methods.beginningOfMethod();
		_stateName = null;
		_description = null;
		_command = null;
		_returnCodePattern = null;
		_returnStringPattern = null;
		
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
		
		String NameState = MessageManager.getMessage(
				"&ComponentConfiguration_Name"); 
		String DescState = MessageManager.getMessage(
				"&ComponentConfiguration_Description");
		String CommandState = MessageManager.getMessage(
				"&ComponentConfiguration_Command");
		String ReturnCodeState = MessageManager.getMessage(
				"&ComponentConfiguration_ReturnCodePattern");
		String ReturnStringState = MessageManager.getMessage(
				"&ComponentConfiguration_ReturnStringPattern");
		
		String retS = "";
		String retC = "";
		String comm = "";
		String desc = "";
		
		if ( _flag == 'E' || _flag == 'S' ) {
			retS = _returnStringPattern;
			retC = _returnCodePattern;
			comm = _command;
			desc = _description;
		} else {
			retS = _newReturnStringPattern;
			retC = _newReturnCodePattern;
			comm = _newCommand;
			desc = _newDescription;
		}
		
		document.insertString(offset,  
				System.getProperty("line.separator", "\r\r"),
				document.getStyle("regular"));
		
		document.insertString(offset, ReturnStringState + " : " +
				retS + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, ReturnCodeState + " : " +
				retC + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		
		document.insertString(offset, CommandState + " : " +
				comm + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		
		document.insertString(offset, DescState + " : " +
				desc + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, NameState + " : " + _stateName + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément ComponentStates.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		ComponentStates clone = new ComponentStates();
		clone.setStateName(_stateName);
		clone.setDescription(_description);
		clone.setCommand(_command);
		clone.setReturnCodePattern(_returnCodePattern);
		clone.setReturnStringPattern(_returnStringPattern);
		clone.setNewDescription(_newDescription);
		clone.setNewCommand(_newCommand);
		clone.setNewReturnCodePattern(_newReturnCodePattern);
		clone.setNewReturnStringPattern(_newReturnStringPattern);
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _StateName
	 * 
	 * Description:
	 * Cet attribut maintient le nom correspondant à un état
	 * du composant
	 * ----------------------------------------------------------*/
	private String _stateName;

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
	* Cet attribut maintient la commande associee a l'action de 
	* gestion
	* ----------------------------------------------------------*/
	private String _command;
	
	/*----------------------------------------------------------
	* Nom: _returnCodePattern
	*
	* Description:
	* Cet attribut maintient le code de retour associe a l'état 
	* ----------------------------------------------------------*/
	private String _returnCodePattern;
	
	/*----------------------------------------------------------
	* Nom: _returnStringPattern
	*
	* Description:
	* Cet attribut maintient la chaine de retour associee a
	* l'état
	* ----------------------------------------------------------*/
	private String _returnStringPattern;

	/*----------------------------------------------------------
	* Nom: _description
	*
	* Description:
	* Cet attribut est une seconde description sur le composant.
	* Il s'agit de la nouvelle valeur saisie par l'utilisateur.
	* ----------------------------------------------------------*/
	private String _newDescription;

	/*----------------------------------------------------------
	* Nom: _command
	*
	* Description:
	* Cet attribut est une seconde commande associee a l'état.
	* Il s'agit de la nouvelle valeur saisie par l'utilisateur.
	* ----------------------------------------------------------*/
	private String _newCommand;
	
	/*----------------------------------------------------------
	* Nom: _returnCodePattern
	*
	* Description:
	* Cet attribut est un second code de retour associe a l'état.
	* Il s'agit de la nouvelle valeur saisie par l'utilisateur. 
	* ----------------------------------------------------------*/
	private String _newReturnCodePattern;
	
	/*----------------------------------------------------------
	* Nom: _returnStringPattern
	*
	* Description:
	* Cet attribut est une seconde chaine de retour associee a l'état.
	* Il s'agit de la nouvelle valeur saisie par l'utilisateur.
	* ----------------------------------------------------------*/
	private String _newReturnStringPattern;
	
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
