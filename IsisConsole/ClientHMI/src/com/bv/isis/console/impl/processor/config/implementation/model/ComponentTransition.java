/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentTransition.java,v $
* $Revision: 1.12 $
*
* ------------------------------------------------------------
* DESCRIPTION: D�finition des sp�cificit�s d'une transition entre �tats
* DATE:        06/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      impl.processor.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
*
* Revision 1.2  2008/06/27 11:38:00  fcd
* Ajout d'un attribut Executive User et de ses m�thodes d'acc�s
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.implementation.model;

//
// Imports syst�me
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
 * Cette classe mod�lise la partie sp�cifique d'une transition entre
 * deux �tats d'un composant I-SIS. Elle se caract�rise par un �tat de
 * d�part (pouvant �tre quelconque), une commande, un type de commande
 * (lancement d'un d�mon ou non) et d'un syst�me de contr�le du d�mon si 
 * besoin est (interval de contr�le et d�lais maximum d'ex�cution).
 * Cette classe est utilis�e lors du processus de cr�ation ou de 
 * modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class ComponentTransition {
	
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentTransition
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
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
	 * Cette m�thode retourne l'�tat de d�part de la transition.
	 * 
	 * Retourne: Un ComponentStates : L'�tat de d�part.
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
	 * Cette m�thode retourne la chaine de caracteres 'command'
	 * associ�e a la transition.
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
	 * Cette m�thode retourne le type de la commande de la transition.
	 * 
	 * Retourne: Une cha�ne de caract�re : le type de la commande.
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
	 * Cette m�thode retourne le d�lais de contr�le du d�mon.
	 * 
	 * Retourne: Un entier : Le d�lais de contr�le.
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
	 * Cette m�thode retourne le d�lais maximum d'ex�cution du d�mon.
	 * 
	 * Retourne: Un entier : Le d�lais maximum d'ex�cution.
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
	 * Cette m�thode retourne l'�tat courant.
	 * 
	 * Retourne: Un caract�re, l'�tat courant.
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
	 * Cette m�thode retourne l'ancien �tat courant.
	 * 
	 * Retourne: Un caract�re, l'ancien �tat courant.
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
	 * Cette m�thode modifie l'�tat de d�part de la transition.
	 * 
	 * Arguments: 
	 *  - startState: Le nouvel �tat d'arriv�e.
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
	 * Cette m�thode modifie la commande de la transition en lui 
	 * donnant celle pass�e en parametre.
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
	 * Cette m�thode modifie le type de la commande de la 
	 * transition en lui donnant celui pass� en parametre.
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
	 * Cette m�thode modifie le d�lais de contr�le du d�mon.
	 * 
	 * Arguments:
	 *  - checkInterval: Le nouveau d�lais de contr�le.
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
	 * Cette m�thode modifie le d�lais maximum d'ex�cution du d�mon.
	 * 
	 * Arguments:
	 *  - checkTimeOut: Le nouveau d�lais maximum.
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
	 * Cette m�thode modifie l'�tat courant avec celui pass� en entr�e.
	 * 
	 * Param�tre : 
	 *   - flag : Le nouvel �tat courant.
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
	 * Cette m�thode modifie l'ancien �tat courant de la transition.
	 * 
	 * Param�tre : 
	 *   - flag : Le nouvel �tat a sauvegarder.
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
	 * Cette m�thode retourne la nouvelle commande saisie.
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
	 * Cette m�thode retourne le nouveau type de commande saisie.
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
	 * Cette m�thode retourne le nouvel interval de contr�le.
	 * 
	 * Retourne : Le nouvel interval de contr�le.
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
	 * Cette m�thode retourne le nouveau d�lai maximum d'ex�cution.
	 * 
	 * Retourne : Le nouveau d�lai.
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
	 * Cette m�thode modifie la nouvelle commande.
	 * 
	 * Param�tre : 
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
	 * Cette m�thode modifie le nouveau type de commande.
	 * 
	 * Param�tre : 
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
	 * Cette m�thode modifie le nouvel interval de controle.
	 * 
	 * Param�tre : 
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
	 * Cette m�thode modifie le nouvel d�lai maximum d'ex�cution.
	 * 
	 * Param�tre : 
	 *   - commande : Le nouveau d�lai maximum d'ex�cution.
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
	 * Cette m�thode est appel�e lors de la destruction de l'assistant par la 
	 * classe ComponentTransitionGroups. Elle est utiliser pour lib�rer 
	 * l'espace m�moire utilis� par les variables des classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
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
	 * Cette m�thode charge le mod�le de donn�es � partir de la base 
	 * de donn�es. 
	 * Apr�s avoir r�cup�rer les noms de la table et des champs, cette 
	 * m�thode va effectuer une requ�te de s�lection sur la table avec 
	 * comme condition, le nom de l'�tat d'arriv�e pass� en param�tre.
	 * A partir du r�sultat, elle va compl�ter les champs du 
	 * ComponentTransition. 
	 * 
	 * Param�tres :
	 *   - session : Une r�f�rence sur une session de service ouverte pour
	 *   			effectuer les requ�tes de lecture
	 *   - context : Cette r�f�rence correspond � l'ensemble des param�tres
	 * 				"exportables" des noeuds travers�s pour atteindre le 
	 * 				noeud concern�. A cet ensemble sont ajout�s tous les 
	 * 				param�tres du noeud lui-m�me.
	 *   - valuesOfTableKey : Un tableau d'IsisParameter contenant les noms
	 *   			et valeurs des variables composant la cl� de la table.
	 *   - endStateName : Une r�f�rence sur le nom de l'�tat d'arriv�e du 
	 *   			groupe de transitions associ� � cette transition.
	 * 
	 * Retourne: vrai (true) si tout c'est bien pass�, faux (false) 
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
			// On r�cup�re l'ensemble des noms des champs de la table
			// correspondant aux transitions sp�cifiques
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
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand m�me
		}
			
		// On s�lectionne les colonnes de la table qui nous interressent
		String [] column = new String[5];
		column[0] = StartStateTransition;
		column[1] = CommandTransition;
		column[2] = CommandTypeTransition;
		column[3] = CheckIntervalTransition;
		column[4] = CheckTimeOutTransition;
		
		// On cr�e la condition en fonction du nom et de la valeur de l'�tat
		// d'arriv�e de la transition.
		String condition = EndStateTransition + "='" + endStateName + "'";
		
		try {
			// On lance la requ�te
			String [] result = session.getSelectResult(TableTransitions, 
					column, condition, "", context);
		
			if (result != null) {
				// On construit la d�finition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableTransitions);
				
				// Pour chaque ligne r�sultat de la requ�te Select
				for (int index = 1 ; index < result.length ; index++) {
					// On r�cup�re les donn�es
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
					// On lib�re l'utilisation de la d�finition
					manager.releaseTableDefinitionLeasing(table_definition);
				}
			}
		}
		catch (InnerException exception)
		{
			trace_errors.writeTrace(
			"Probl�me lors du chargement des donn�es !");
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
	 * Cette m�thode sauvegarde le mod�le dans les tables. Apr�s avoir
	 * r�cup�rer les noms des champs des tables des transitions, cette m�thode 
	 * va construire la requ�te d'insertion ou de modification d'une donn�es 
	 * de la table correspondant aux transitions. Puis via la classe
	 * ExecutionSurveyor, cette requ�te sera ex�cut�e. 
	 * 
	 * Param�tres :
	 *   - actionId : L'identifiant unique de l'action
	 *   - context : Cette r�f�rence correspond � l'ensemble des param�tres
	 * 				"exportables" des noeuds travers�s pour atteindre le 
	 * 				noeud concern�. A cet ensemble sont ajout�s tous les 
	 * 				param�tres du noeud lui-m�me.
	 *   - seletedNode : Une r�f�rence sur le noeud s�lectionn�.
	 *   - endStateName : Une r�f�rece sur le nom de l'�tat d'arriv�e du 
	 *   			groupe de transitions correspondant � cette transition.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
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
				
				// On r�cup�re l'ensemble des noms des champs de la table
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
					"Erreur lors de la r�cup�ration de la configuration: " +
					exception);
				// Il y a eu une erreur, on continue quand m�me
			}
			
			try {
				// On ouvre une session de service � partir du noeud s�lectionn�
				ServiceSessionProxy session = new ServiceSessionProxy(
						selectedNode.getServiceSession());
				
				// On s�lectionne toutes les colonnes de la table
				String [] column = { "" };
				
				// On construit la condition en fonction de la cl� de la table
				String condition = EndStateTransitionGroup + "='" + endStateName 
								+"' AND " + StartStateTransition + "='";
				// Pour l'�tat de d�part, l'�tat 'Quelconque' n'existe pas
				// en table, c'est le caract�re * qui le remplace
				if (_startState.getStateName().equals(
					MessageManager.getMessage("&ComponentConfiguration_AnyState")))
					condition += AnyState + "'";
				else {
					condition += _startState.getStateName() + "'";
				}
						
				// On lance la requ�te pour obtenir la d�finition de la table
				String [] commandSelect = session.getSelectResult(TableTransitions, 
						column, condition, "", context);
				
				// On construit la d�finition de la table
				IsisTableDefinition table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
							commandSelect, TableTransitions);
				
				// A partir de la ligne r�sultat, on compl�te les valeurs de la 
				// requ�te
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
				
	
				// Comme on a effectu� une requ�te de s�lection sur la cl� de la 
				// table, si le r�sultat comprends plus d'une ligne, c'est que 
				// l'�l�ment dans la table existait d�j�, donc on souhaite modifier
				// cette �l�ment
				String InsertOrReplace = "";
				if (_flag == 'M')
					InsertOrReplace = "Replace";
				// Sinon, c'est que l'�l�ment n'existait pas
				else if (_flag == 'A')
					InsertOrReplace = "Insert";
				else 
					InsertOrReplace = "Remove";
					
				// On construit la requ�te de cr�ation ou de modification
				String command = AdministrationCommandFactory.
				buildAdministrationCommand(InsertOrReplace, 
						TableTransitions, values, AgentSessionManager.getInstance()
									.getAgentLayerMode(selectedNode.getAgentName()));
		
				// On ex�cute la requ�te
				ExecutionSurveyor surveyor = new ExecutionSurveyor();
				
				surveyor.execute(actionId, command, selectedNode, context);
				_flag = 'E';
				
				// On lib�re l'utilisation de la d�finition
				manager.releaseTableDefinitionLeasing(table_definition);
			}
			catch (InnerException exception) 
			{
				trace_errors.writeTrace(
						"Probl�me lors d'une requ�te sur la table " + 
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
	 * Cette m�thode est appel�e lors que l'on souhaite afficher les donn�es
	 * d'un composant. Elle prends en argument un StyledDocument dans 
	 * lequel on ajoute les informations que l'on souhaite afficher � la 
	 * position sp�cifi� par l'offset.
	 * 
	 * Argument :
	 *   - styledDocument : le document dans lequel on souhaite afficher
	 *     les donn�es du composant.
	 *   - offset : La position dans le StyledDocument ou on veut ins�rer 
	 *     les donn�es
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
	 * Cette m�thode permet de retourner une copie de l'�l�ment 
	 * ComponentTransition.
	 * 
	 * Retourne :le clone de l'�l�ment du mod�le sous le type ComponentTransition.
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
	 * Cet attribut maintient une reference sur l'�tat de d�part 
	 * de la transition.
	 * ----------------------------------------------------------*/
	private ComponentStates _startState;
	
	/*----------------------------------------------------------
	 * Nom: _command
	 *
	 * Description:
	 * Cet attribut maintient la commande associ�e � la transition 
	 * depuis l'�tat de d�part sp�cifi�.
	 * ----------------------------------------------------------*/
	private String _command;
	
	/*----------------------------------------------------------
	 * Nom: _commandType
	 *
	 * Description:
	 * Cet attribut maintient le type de la commande (lancement d'un
	 * d�mon, ...).
	 * ----------------------------------------------------------*/
	private String _commandType;
	
	/*----------------------------------------------------------
	 * Nom: _checkInterval
	 *
	 * Description:
	 * Cet attribut indique le d�lais de controle du d�mon en cas de 
	 * d'execution du processus en tant que tel.
	 * ----------------------------------------------------------*/
	private int _checkInterval;
	
	/*----------------------------------------------------------
	 * Nom: _checkTimeOut
	 *
	 * Description:
	 * Cet attribut indique le d�lais maximum d'ex�cution du d�mon.
	 * ----------------------------------------------------------*/
	private int _checkTimeOut;

	/*----------------------------------------------------------
	 * Nom: _command
	 *
	 * Description:
	 * Cet attribut est une seconde commande associ�e � la transition 
	 * depuis l'�tat de d�part sp�cifi�. Il s'agit de la nouvelle valeur
	 * saisie par l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newCommand;
	
	/*----------------------------------------------------------
	 * Nom: _commandType
	 *
	 * Description:
	 * Cet attribut est un second type de la commande correspondant � 
	 * ka nouvelle valeur saisie par l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newCommandType;
	
	/*----------------------------------------------------------
	 * Nom: _checkInterval
	 *
	 * Description:
	 * Cet attribut indique la nouvelle valeur du d�lais de controle 
	 * du d�mon en cas de d'execution du processus en tant que tel.
	 * ----------------------------------------------------------*/
	private int _newCheckInterval;
	
	/*----------------------------------------------------------
	 * Nom: _checkTimeOut
	 *
	 * Description:
	 * Cet attribut indique la nouvelle valeur du d�lais maximum 
	 * d'ex�cution du d�mon.
	 * ----------------------------------------------------------*/
	private int _newCheckTimeOut;
 
	/*----------------------------------------------------------
	 * Nom: _flag
	 *
	 * Description:
	 * Cet attribut repr�sente un �tat associ� au composant vis � vis
	 * de la base de donn�es. 
	 * Celui-ci peut prendre 4 valeurs : 
	 *   - 'A' pour un �l�ment � ajouter dans la base de donn�es.
	 *   - 'E' pour une entr�e non modifi�e depuis la base de donn�es.
	 *   - 'M' pour une entr�e de la base de donn�es mais modifi�e par
	 *     l'utilisateur.
	 *   - 'S' pour une entr�e de la base � supprimer.
	 * ----------------------------------------------------------*/
	private char _flag;
	
	/*----------------------------------------------------------
	 * Nom: _previousFlag
	 *
	 * Description:
	 * Cet attribut repr�sente l'ancien �tat associ� au composant vis � vis
	 * de la base de donn�es. 
	 * Le bouton 'Annuler' de l'�cran de param�trage SpecificTransition-
	 * DescriptionPanel oblige � conserver l'�tat avant modification de la 
	 * transition au cas ou l'utilisateur d�cide de ne pas conserver les 
	 * modifications apport�es dans cet �cran.
	 * ----------------------------------------------------------*/
	private char _previousFlag;
}
