/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentTransition.java,v $
* $Revision: 1.12 $
*
* ------------------------------------------------------------
* DESCRIPTION: Définition des spécificités d'une transition entre états
* DATE:        06/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      impl.processor.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
*
* Revision 1.2  2008/06/27 11:38:00  fcd
* Ajout d'un attribut Executive User et de ses méthodes d'accès
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
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.IsisTableDefinition;

/*----------------------------------------------------------
 * Nom: ComponentTransition
 * 
 * Description:
 * Cette classe modélise la partie spécifique d'une transition entre
 * deux états d'un composant I-SIS. Elle se caractérise par un état de
 * départ (pouvant être quelconque), une commande, un type de commande
 * (lancement d'un démon ou non) et d'un système de contrôle du démon si 
 * besoin est (interval de contrôle et délais maximum d'exécution).
 * Cette classe est utilisée lors du processus de création ou de 
 * modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class ComponentTransition {
	
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentTransition
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public ComponentTransition() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "ComponentTransition");
		trace_methods.beginningOfMethod();
		
		_command = "";
		_commandType = "";
		_checkInterval = 0;
		_checkTimeOut = 0;
		_flag = ' ';
		_previousFlag = ' ';
		
		_newCommand = "";
		_newCommandType = "";
		_newCheckInterval = 0;
		_newCheckTimeOut = 0;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: getStartState
	 * 
	 * Description:
	 * Cette méthode retourne l'état de départ de la transition.
	 * 
	 * Retourne: Un ComponentStates : L'état de départ.
	 * ----------------------------------------------------------*/
	public ComponentStates getStartState() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getStartState");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _startState;
	}
	
	/*----------------------------------------------------------
	 * Nom: getCommand
	 * 
	 * Description:
	 * Cette méthode retourne la chaine de caracteres 'command'
	 * associée a la transition.
	 * 
	 * Retourne: Une chaine de caractere : La commande de la transition.
	 * ----------------------------------------------------------*/
	public String getCommand() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getCommand");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _command;
	}
	
	/*----------------------------------------------------------
	 * Nom: getCommandType
	 * 
	 * Description:
	 * Cette méthode retourne le type de la commande de la transition.
	 * 
	 * Retourne: Une chaîne de caractère : le type de la commande.
	 * ----------------------------------------------------------*/
	public String getCommandType() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getCommandType");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _commandType;
	}
	
	/*----------------------------------------------------------
	 * Nom: getCheckInterval
	 * 
	 * Description:
	 * Cette méthode retourne le délais de contrôle du démon.
	 * 
	 * Retourne: Un entier : Le délais de contrôle.
	 * ----------------------------------------------------------*/
	public int getCheckInterval() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getCheckInterval");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _checkInterval;
	}
	
	/*----------------------------------------------------------
	 * Nom: getCheckTimeOut
	 *  
	 * Description:
	 * Cette méthode retourne le délais maximum d'exécution du démon.
	 * 
	 * Retourne: Un entier : Le délais maximum d'exécution.
  	 * ----------------------------------------------------------*/
	public int getCheckTimeOut() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getCheckTimeOut");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _checkTimeOut;
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
				"ComponentTransition", "getFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _flag;
	}
	
	/*----------------------------------------------------------
	 * Nom: getPreviousFlag
	 * 
	 * Description:
	 * Cette méthode retourne l'ancien état courant.
	 * 
	 * Retourne: Un caractère, l'ancien état courant.
	 * ----------------------------------------------------------*/
	public char getPreviousFlag() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getPreviousFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _previousFlag;
	}
	
	/*----------------------------------------------------------
	 * Nom: setStartState
	 * 
	 * Description:
	 * Cette méthode modifie l'état de départ de la transition.
	 * 
	 * Arguments: 
	 *  - startState: Le nouvel état d'arrivée.
	 * ----------------------------------------------------------*/
	public void setStartState(ComponentStates startState) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setStartState");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("startState=" + startState);
		
		if (startState != _startState)
			_startState = startState;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setCommand
	 * 
	 * Description:
	 * Cette méthode modifie la commande de la transition en lui 
	 * donnant celle passée en parametre.
	 * 
	 * Arguments:
	 *  - command: La nouvelle commande de la transition
	 * ----------------------------------------------------------*/
	public void setCommand(String command) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setCommand");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("command=" + command);
		
		_command = command;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setCommandType
	 * 
	 * Description:
	 * Cette méthode modifie le type de la commande de la 
	 * transition en lui donnant celui passé en parametre.
	 * 
	 * Arguments:
	 *  - commandType: Le nouveau type.
	 * ----------------------------------------------------------*/
	public void setCommandType(String commandType) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setCommandType");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("commandType=" + commandType);
		
		_commandType = commandType;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setCheckInterval
	 * 
	 * Description:
	 * Cette méthode modifie le délais de contrôle du démon.
	 * 
	 * Arguments:
	 *  - checkInterval: Le nouveau délais de contrôle.
	 * ----------------------------------------------------------*/
	public void setCheckInterval(int checkInterval) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setCheckInterval");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("checkInterval=" + checkInterval);
		
		_checkInterval = checkInterval;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setCheckTimeOut
	 * 
	 * Description:
	 * Cette méthode modifie le délais maximum d'exécution du démon.
	 * 
	 * Arguments:
	 *  - checkTimeOut: Le nouveau délais maximum.
	 * ----------------------------------------------------------*/
	public void setCheckTimeOut(int checkTimeOut) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setCheckTimeOut");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("checkTimeOut=" + checkTimeOut);
		
		_checkTimeOut = checkTimeOut;
		
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
				"ComponentTransition", "setFlag");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("flag=" + flag);
		
		_flag = flag;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setPreviousFlag
	 * 
	 * Description:
	 * Cette méthode modifie l'ancien état courant de la transition.
	 * 
	 * Paramètre : 
	 *   - flag : Le nouvel état a sauvegarder.
	 * ----------------------------------------------------------*/
	public void setPreviousFlag(char flag) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setPreviousFlag");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("flag=" + flag);
		
		_previousFlag = flag;
		
		trace_methods.endOfMethod();
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
				"ComponentTransition", "getNewCommand");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newCommand;
	}

	/*----------------------------------------------------------
	 * Nom: getNewCommandType
	 * 
	 * Description:
	 * Cette méthode retourne le nouveau type de commande saisie.
	 * 
	 * Retourne : Le nouveau type.
	 * ----------------------------------------------------------*/
	public String getNewCommandType() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getNewCommandType");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newCommandType;
	}

	/*----------------------------------------------------------
	 * Nom: getNewCheckInterval
	 * 
	 * Description:
	 * Cette méthode retourne le nouvel interval de contrôle.
	 * 
	 * Retourne : Le nouvel interval de contrôle.
	 * ----------------------------------------------------------*/
	public int getNewCheckInterval() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getNewCheckInterval");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newCheckInterval;
	}

	/*----------------------------------------------------------
	 * Nom: getNewCheckTimeOut
	 * 
	 * Description:
	 * Cette méthode retourne le nouveau délai maximum d'exécution.
	 * 
	 * Retourne : Le nouveau délai.
	 * ----------------------------------------------------------*/
	public int getNewCheckTimeOut() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getNewCheckTimeOut");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newCheckTimeOut;
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
				"ComponentTransition", "setNewCommand");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("command=" + command);
		
		_newCommand = command;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewCommandType
	 * 
	 * Description:
	 * Cette méthode modifie le nouveau type de commande.
	 * 
	 * Paramètre : 
	 *   - commande : Le nouveau type de commande.
	 * ----------------------------------------------------------*/
	public void setNewCommandType(String commandType) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setNewCommandType");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("commandType=" + commandType);
		
		_newCommandType = commandType;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewCheckInterval
	 * 
	 * Description:
	 * Cette méthode modifie le nouvel interval de controle.
	 * 
	 * Paramètre : 
	 *   - commande : Le nouvel interval.
	 * ----------------------------------------------------------*/
	public void setNewCheckInterval(int checkInterval) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setNewCheckInterval");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("checkInterval=" + checkInterval);
		
		_newCheckInterval = checkInterval;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewCheckTimeOut
	 * 
	 * Description:
	 * Cette méthode modifie le nouvel délai maximum d'exécution.
	 * 
	 * Paramètre : 
	 *   - commande : Le nouveau délai maximum d'exécution.
	 * ----------------------------------------------------------*/
	public void setNewCheckTimeOut(int checkTimeOut) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setNewCheckTimeOut");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("checkTimeOut=" + checkTimeOut);
		
		_newCheckTimeOut = checkTimeOut;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: end
	 * 
	 * Description: 
	 * Cette méthode est appelée lors de la destruction de l'assistant par la 
	 * classe ComponentTransitionGroups. Elle est utiliser pour libèrer 
	 * l'espace mémoire utilisé par les variables des classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public boolean end() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "end");
		trace_methods.beginningOfMethod();

		_startState = null;
		_command = null;
		_commandType = null;
		_checkInterval = 0;
		_checkTimeOut = 0;

		trace_methods.endOfMethod();
		
		return true;
	}

	/*----------------------------------------------------------
	 * Nom: loadData
	 * 
	 * Description:
	 * Cette méthode charge le modèle de données à partir de la base 
	 * de données. 
	 * Après avoir récupérer les noms de la table et des champs, cette 
	 * méthode va effectuer une requête de sélection sur la table avec 
	 * comme condition, le nom de l'état d'arrivée passé en paramètre.
	 * A partir du résultat, elle va compléter les champs du 
	 * ComponentTransition. 
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
	 *   - endStateName : Une référence sur le nom de l'état d'arrivée du 
	 *   			groupe de transitions associé à cette transition.
	 * 
	 * Retourne: vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	/*public boolean loadData(ServiceSessionProxy session,
			IndexedList context, String endStateName) 
			throws InnerException {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "loadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("endStateName=" + endStateName);
		
		IsisTableDefinition table_definition;
		String TableTransitions = "TableTransitions";
		String StartStateTransition = "StartState";
		String CommandTransition = "Command";
		String CommandTypeTransition = "CommandType";
		String CheckIntervalTransition = "CheckInterval";
		String CheckTimeOutTransition = "TimeOut";
		
		String EndStateTransition = "EndState";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		
		try {
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux transitions spécifiques
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableTransitions = configuration_api.getString(
					"ComponentConfiguration.TableTransitions");
			StartStateTransition = configuration_api.getString(
					"ComponentConfiguration.TableTransitions.StartState");
			CommandTransition = configuration_api.getString(
					"ComponentConfiguration.TableTransitions.Command");
			CommandTypeTransition = configuration_api.getString(
					"ComponentConfiguration.TableTransitions.CommandType");
			CheckIntervalTransition = configuration_api.getString(
					"ComponentConfiguration.TableTransitions.CheckInterval");
			CheckTimeOutTransition = configuration_api.getString(
					"ComponentConfiguration.TableTransitions.TimeOut");
			
			EndStateTransition = configuration_api.getString(
					"ComponentConfiguration.TableTransitionGroups.EndState");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
			
		// On sélectionne les colonnes de la table qui nous interressent
		String [] column = new String[5];
		column[0] = StartStateTransition;
		column[1] = CommandTransition;
		column[2] = CommandTypeTransition;
		column[3] = CheckIntervalTransition;
		column[4] = CheckTimeOutTransition;
		
		// On crée la condition en fonction du nom et de la valeur de l'état
		// d'arrivée de la transition.
		String condition = EndStateTransition + "='" + endStateName + "'";
		
		try {
			// On lance la requête
			String [] result = session.getSelectResult(TableTransitions, 
					column, condition, "", context);
		
			if (result != null) {
				// On construit la définition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableTransitions);
				
				// Pour chaque ligne résultat de la requête Select
				for (int index = 1 ; index < result.length ; index++) {
					// On récupère les données
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On construit le ComponentTransition
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(CommandTransition))
								_command = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(CommandTypeTransition))
								_commandType = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(CheckIntervalTransition))
								_checkInterval = Integer.parseInt(object_parameters[index2].value);
							else if (object_parameters[index2].name.equals(CheckTimeOutTransition))
								_checkTimeOut = Integer.parseInt(object_parameters[index2].value);
							else if ( !object_parameters[index2].name.equals(StartStateTransition) )
								System.out.println("Erreur, " + object_parameters[index] + " innatendu.");
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
	}*/
	
	/*----------------------------------------------------------
	 * Nom: saveData
	 * 
	 * Cette méthode sauvegarde le modèle dans les tables. Après avoir
	 * récupérer les noms des champs des tables des transitions, cette méthode 
	 * va construire la requête d'insertion ou de modification d'une données 
	 * de la table correspondant aux transitions. Puis via la classe
	 * ExecutionSurveyor, cette requête sera exécutée. 
	 * 
	 * Paramètres :
	 *   - actionId : L'identifiant unique de l'action
	 *   - context : Cette référence correspond à l'ensemble des paramètres
	 * 				"exportables" des noeuds traversés pour atteindre le 
	 * 				noeud concerné. A cet ensemble sont ajoutés tous les 
	 * 				paramètres du noeud lui-même.
	 *   - seletedNode : Une référence sur le noeud sélectionné.
	 *   - endStateName : Une référece sur le nom de l'état d'arrivée du 
	 *   			groupe de transitions correspondant à cette transition.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public void saveData(
		String actionId, 
		IndexedList context, 
		GenericTreeObjectNode selectedNode,
		String endStateName
		) 
		throws 
			InnerException {
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "saveData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("endStateName=" + endStateName);
		
		String EndStateTransitionGroup = "EndState";
		
		String TableTransitions = "TableTransitions";
		String StartStateTransition = "StartState";
		String CommandTransition = "Command";
		String CommandTypeTransition = "CommandType";
		String CheckIntervalTransition = "CheckInterval";
		String CheckTimeOutTransition = "TimeOut";
		String AnyState = "*";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		
		if (_flag != 'E') {
		
			try {
				ConfigurationAPI configuration_api = new ConfigurationAPI();
				configuration_api.useSection("I-SIS");
				
				// On récupère l'ensemble des noms des champs de la table
				// correspondant aux transitions.
				EndStateTransitionGroup = configuration_api.getString(
						"ComponentConfiguration.TableTransitionGroups.EndState");
				
				TableTransitions = configuration_api.getString(
						"ComponentConfiguration.TableTransitions");
				StartStateTransition = configuration_api.getString(
						"ComponentConfiguration.TableTransitions.StartState");
				CommandTransition = configuration_api.getString(
						"ComponentConfiguration.TableTransitions.Command");
				CommandTypeTransition = configuration_api.getString(
						"ComponentConfiguration.TableTransitions.CommandType");
				CheckIntervalTransition = configuration_api.getString(
						"ComponentConfiguration.TableTransitions.CheckInterval");
				CheckTimeOutTransition = configuration_api.getString(
						"ComponentConfiguration.TableTransitions.TimeOut");
				
				AnyState = configuration_api.getString("ComponentConfiguration." +
						"AnyState");
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
				String condition = EndStateTransitionGroup + "='" + endStateName 
								+"' AND " + StartStateTransition + "='";
				// Pour l'état de départ, l'état 'Quelconque' n'existe pas
				// en table, c'est le caractère * qui le remplace
				if (_startState.getStateName().equals(
					MessageManager.getMessage("&ComponentConfiguration_AnyState")))
					condition += AnyState + "'";
				else {
					condition += _startState.getStateName() + "'";
				}
						
				// On lance la requête pour obtenir la définition de la table
				String [] commandSelect = session.getSelectResult(TableTransitions, 
						column, condition, "", context);
				
				// On construit la définition de la table
				IsisTableDefinition table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
							commandSelect, TableTransitions);
				
				// A partir de la ligne résultat, on complète les valeurs de la 
				// requête
				String values = "";
				for (int index=0 ; index < table_definition.columns.length ; index++) {
					if (table_definition.columns[index].name.equals(EndStateTransitionGroup))
						values += endStateName;
					else if (table_definition.columns[index].name.equals(StartStateTransition)) {
						if (_startState.getStateName().equals(
							MessageManager.getMessage("&ComponentConfiguration_AnyState")))
							values += AnyState;
						else
							values += _startState.getStateName();
					}
					else if (table_definition.columns[index].name.equals(CommandTransition)) {
						if (_flag != 'S') {
							values += _newCommand;
						}
						else {
							values += _command;
						}
					}
					else if (table_definition.columns[index].name.equals(CommandTypeTransition)){
						if (_flag != 'S') 
							values += _newCommandType;
						else
							values += _commandType;
					}
					else if (table_definition.columns[index].name.equals(CheckIntervalTransition)) {
						if (_flag != 'S')
							values += _newCheckInterval;
						else
							values += _checkInterval;
					}
					else if (table_definition.columns[index].name.equals(CheckTimeOutTransition)) {
						if (_flag != 'S')
							values += _newCheckTimeOut;
						else
							values += _checkTimeOut;
					}
					
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
						TableTransitions, values, AgentSessionManager.getInstance()
									.getAgentLayerMode(selectedNode.getAgentName()));
		
				// On exécute la requête
				ExecutionSurveyor surveyor = new ExecutionSurveyor();
				
				surveyor.execute(actionId, command, selectedNode, context);
				_flag = 'E';
				
				// On libère l'utilisation de la définition
				manager.releaseTableDefinitionLeasing(table_definition);
			}
			catch (InnerException exception) 
			{
				trace_errors.writeTrace(
						"Problème lors d'une requête sur la table " + 
						TableTransitions + " !");
				// C'est une erreur, on la signale
				throw new InnerException("&ERR_Query", null, null);
			}
		}
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
		
		String StartStateTrans = MessageManager.getMessage(
				"&ComponentConfiguration_StartState"); 
		String CommandTrans = MessageManager.getMessage(
				"&ComponentConfiguration_Command");
		String CommandTypeTrans = MessageManager.getMessage(
				"&ComponentConfiguration_CommandType");
		String IntervalTrans = MessageManager.getMessage(
				"&ComponentConfiguration_CheckInterval");
		String TimeOutTrans = MessageManager.getMessage(
				"&ComponentConfiguration_CheckTimeOut");
		
		int timO = 0;
		int inte = 0;
		String comT = "";
		String comm = "";
		
		if( _flag == 'E' || _flag == 'S' ) {
			timO = _checkTimeOut;
			inte = _checkInterval;
			comT = _commandType;
			comm = _command;
		} else {
			timO = _newCheckTimeOut;
			inte = _newCheckInterval;
			comT = _newCommandType;
			comm = _newCommand;
		}
		
		document.insertString(offset,  
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		
		document.insertString(offset, TimeOutTrans + " : " +
				timO + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, IntervalTrans + " : " +
				inte + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, CommandTypeTrans + " : " +
				comT + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, CommandTrans + " : " +
				comm + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, StartStateTrans + " : " + 
				_startState.getStateName() + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
	}

	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément 
	 * ComponentTransition.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ComponentTransition.
	 * ----------------------------------------------------------*/
	public ComponentTransition clone() {
		
		ComponentTransition clone = new ComponentTransition();
		clone.setStartState(_startState);
		clone.setCommand(_command);
		clone.setCommandType(_commandType);
		clone.setCheckInterval(_checkInterval);
		clone.setCheckTimeOut(_checkTimeOut);
		
		clone.setNewCommand(_newCommand);
		clone.setNewCommandType(_newCommandType);
		clone.setNewCheckInterval(_newCheckInterval);
		clone.setNewCheckTimeOut(_newCheckTimeOut);
		
		clone.setPreviousFlag(_previousFlag);
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _startState
	 *
	 * Description:
	 * Cet attribut maintient une reference sur l'état de départ 
	 * de la transition.
	 * ----------------------------------------------------------*/
	private ComponentStates _startState;
	
	/*----------------------------------------------------------
	 * Nom: _command
	 *
	 * Description:
	 * Cet attribut maintient la commande associée à la transition 
	 * depuis l'état de départ spécifié.
	 * ----------------------------------------------------------*/
	private String _command;
	
	/*----------------------------------------------------------
	 * Nom: _commandType
	 *
	 * Description:
	 * Cet attribut maintient le type de la commande (lancement d'un
	 * démon, ...).
	 * ----------------------------------------------------------*/
	private String _commandType;
	
	/*----------------------------------------------------------
	 * Nom: _checkInterval
	 *
	 * Description:
	 * Cet attribut indique le délais de controle du démon en cas de 
	 * d'execution du processus en tant que tel.
	 * ----------------------------------------------------------*/
	private int _checkInterval;
	
	/*----------------------------------------------------------
	 * Nom: _checkTimeOut
	 *
	 * Description:
	 * Cet attribut indique le délais maximum d'exécution du démon.
	 * ----------------------------------------------------------*/
	private int _checkTimeOut;

	/*----------------------------------------------------------
	 * Nom: _command
	 *
	 * Description:
	 * Cet attribut est une seconde commande associée à la transition 
	 * depuis l'état de départ spécifié. Il s'agit de la nouvelle valeur
	 * saisie par l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newCommand;
	
	/*----------------------------------------------------------
	 * Nom: _commandType
	 *
	 * Description:
	 * Cet attribut est un second type de la commande correspondant à 
	 * ka nouvelle valeur saisie par l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newCommandType;
	
	/*----------------------------------------------------------
	 * Nom: _checkInterval
	 *
	 * Description:
	 * Cet attribut indique la nouvelle valeur du délais de controle 
	 * du démon en cas de d'execution du processus en tant que tel.
	 * ----------------------------------------------------------*/
	private int _newCheckInterval;
	
	/*----------------------------------------------------------
	 * Nom: _checkTimeOut
	 *
	 * Description:
	 * Cet attribut indique la nouvelle valeur du délais maximum 
	 * d'exécution du démon.
	 * ----------------------------------------------------------*/
	private int _newCheckTimeOut;
 
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
	
	/*----------------------------------------------------------
	 * Nom: _previousFlag
	 *
	 * Description:
	 * Cet attribut représente l'ancien état associé au composant vis à vis
	 * de la base de données. 
	 * Le bouton 'Annuler' de l'écran de paramétrage SpecificTransition-
	 * DescriptionPanel oblige à conserver l'état avant modification de la 
	 * transition au cas ou l'utilisateur décide de ne pas conserver les 
	 * modifications apportées dans cet écran.
	 * ----------------------------------------------------------*/
	private char _previousFlag;
}
