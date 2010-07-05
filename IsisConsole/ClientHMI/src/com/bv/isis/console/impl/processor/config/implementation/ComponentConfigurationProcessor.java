/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/ComponentConfigurationProcessor.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de paramétrage d'un composant I-SIS
* DATE:        04/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.config.implementation;

//
//Imports système
//
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.impl.processor.config.framework.view.AbstractWindow;
import com.bv.isis.console.impl.processor.config.framework.view.PanelInterface;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentConfig;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentLog;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentManagementAction;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentStates;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentTransition;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentTransitionGroups;
import com.bv.isis.console.impl.processor.config.implementation.model.ComponentVariable;
import com.bv.isis.console.impl.processor.config.implementation.view.ConfigurationFileDescriptionPanel;
import com.bv.isis.console.impl.processor.config.implementation.view.GenericTransitionDescriptionPanel;
import com.bv.isis.console.impl.processor.config.implementation.view.LogDescriptionPanel;
import com.bv.isis.console.impl.processor.config.implementation.view.ManagementActionDescriptionPanel;
import com.bv.isis.console.impl.processor.config.implementation.view.PresentationPanel;
import com.bv.isis.console.impl.processor.config.implementation.view.StateDescriptionPanel;
import com.bv.isis.console.impl.processor.config.implementation.view.SummaryPanel;
import com.bv.isis.console.impl.processor.config.implementation.view.VariableDescriptionPanel;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.console.processor.ProcessingHandler;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableDefinition;

/*------------------------------------------------------------
 * Nom: ComponentConfigurationProcessor
 * 
 * Description:
 * Cette classe implémente le processeur de tâche chargé de l'exécution 
 * d'une procédure de paramétrage de composants. Son but est de créer
 * un composant à partir de diverses informations saisis par l'utilisateur.
 * Une fois la saisie terminée, le processeur construit le composant.
 * ------------------------------------------------------------*/
public class ComponentConfigurationProcessor extends AbstractWindow {

	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: ComponentConfigurationProcessor
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ------------------------------------------------------------*/
	public ComponentConfigurationProcessor() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", 
				"ComponentConfigurationProcessor");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}
	
	/*------------------------------------------------------------
	 * Nom: run
	 * 
	 * Description:
	 * Cette méthode redéfinit celle de la classe ProcessorFrame. Elle est appelée
	 * par le ProcessManager afin d'initialiser et de d'exécuter le processeur.
	 * La sous-fenêtre est construite, affichée, et l'exécution de la procédure est
	 * déclenchée.
	 * A la fin de l'exécution, un message indiquant cette fin, et le code de 
	 * retour de la procédure est affiché à l'utilisateur.
	 * 
	 * Si un problème est détecté durant la phase d'initialisation, l'exception
	 * InnerException doit être levée.
	 * 
	 * Arguments:
	 *  - windowInterface: Une référence sur l'interface MainWindowInterface
	 *    permettant au processeur d'interagir avec la fenêtre principale,
	 *  - menuItem: Une référence sur l'objet JMenuItem par lequel le processeur
	 *    a été exécuté. Cet argument peut être nul,
	 *  - parameters: Une chaîne de caractère contenant des paramètres
	 *    spécifiques au processeur. Cet argument peut être nul,
	 *  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	 *  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	 *  - selectedNode: Une référence sur le noeud sélectionné. 
	 *    
	 * Lève: InnerException.
	 * ------------------------------------------------------------*/
	public void run(
			MainWindowInterface windowInterface,
			JMenuItem menuItem,
			String parameters,
			String preprocessing,
			String postprocessing,
			DefaultMutableTreeNode selectedNode
			) throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// On appelle la méthode de la super classe
		super.run(windowInterface, menuItem, parameters,
				preprocessing, postprocessing, selectedNode);
		
		windowInterface.setStatus("&Status_BuildingAssistant", null, 0);
		windowInterface.setProgressMaximum(3);

		// On construit la sous fenêtre de l'assistant
		makePanel();
		
		// On définit la liste des panneaux à afficher et leurs ordres
		// d'apparition
		_panelTable = new PanelInterface [8];
		_panelTable[0] = new PresentationPanel(this);
		_panelTable[1] = new VariableDescriptionPanel(this);
		_panelTable[2] = new StateDescriptionPanel(this);
		_panelTable[3] = new GenericTransitionDescriptionPanel(this); 
		_panelTable[4] = new ManagementActionDescriptionPanel(this);
		_panelTable[5] = new ConfigurationFileDescriptionPanel(this);
		_panelTable[6] = new LogDescriptionPanel(this);
		_panelTable[7] = new SummaryPanel(this);
		
		// On va maintenant définir le modèle de données du composant à 
		// partir des tables de la base de données
		_modelTable = new ArrayList<ModelInterface> ();
		
		windowInterface.setStatus("&Status_EndBuildingAssistant", null, 1);
		
		// On vérifie la validité de l'argument
		if(selectedNode == null ||
		   !(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Pas de noeud sélectionné !");
			// Il n'y a pas de noeud sélectionné, c'est une erreur
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		_selectedNode =
			(GenericTreeObjectNode)selectedNode;

		// On positionne le contexte pour accéder aux tables
		_context = _selectedNode.getContext(true);
		ProcessingHandler.handleProcessingStatement(
			preprocessing, _context, windowInterface, 
			AgentSessionManager.getInstance().getAgentLayerMode(
			_selectedNode.getAgentName()), 
			_selectedNode.getServiceSession(), 
			(Component)windowInterface, true);
		
		ServiceSessionProxy session = new ServiceSessionProxy(
				_selectedNode.getServiceSession());
		
		windowInterface.setStatus("&Status_LoadingData", null, 2);
		try {
			// On traite en premier les variables du composant
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			// On récupère la table associée aux variables ainsi que le nom 
			// du champ associé au nom de la variable
			String TableVariables = 
				configuration_api.getString("ComponentConfiguration." +
						"TableVariables");
			String NameVariable = 
				configuration_api.getString("ComponentConfiguration." +
						"TableVariables.Name");
			
			String [] tableKey = { NameVariable };
			_modelTable.addAll(createComponent(TableVariables, tableKey , 
					session, "ComponentVariable"));
		}
		catch (Exception e)
		{
			trace_errors.writeTrace("Erreur lors du chargement " +
					"des variables");
			// Une erreur est survenue
			throw new InnerException("&ERR_LoadData", null, null);
		}
		
		// On traite les états du composant
		try {
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			// On récupère la table associée aux états ainsi que le nom 
			// du champ associé au nom de l'état
			String TableStates = 
				configuration_api.getString("ComponentConfiguration." +
						"TableStates");
			String NameState = 
				configuration_api.getString("ComponentConfiguration." +
						"TableStates.Name");
			
			String [] tableKey = { NameState };
			_modelTable.addAll(createComponent(TableStates, tableKey , 
					session, "ComponentStates"));
			
			// L'état 'Quelconque' doit être présent pour la saisie des 
			// transtions mais il n'existe pas en tant que tel dans les tables
			// Donc, on le rajoute
			ComponentStates cs = new ComponentStates();
			cs.setStateName(MessageManager.getMessage(
				"&ComponentConfiguration_AnyState"));
			cs.setDescription(configuration_api.getString(
				"ComponentConfiguration.AnyState"));
			cs.setCommand("");
			cs.setReturnCodePattern("");
			cs.setReturnStringPattern("");
			cs.setFlag('E');
			_modelTable.add(cs);
		}
		catch (Exception e)
		{
			trace_errors.writeTrace("Erreur lors du chargement des états");
			// Une erreur est survenue
			throw new InnerException("&ERR_LoadData", null, null);
		}
		
		// On traite les actions d'exploitation du composant
		try {
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			// On récupère la table associée aux actions ainsi que le nom 
			// du champ associé au nom de l'action
			String TableActions = 
				configuration_api.getString("ComponentConfiguration." +
						"TableActions");
			String NameAction = 
				configuration_api.getString("ComponentConfiguration." +
						"TableActions.Name");
			
			String [] tableKey = { NameAction };
			_modelTable.addAll(createComponent(TableActions, tableKey , 
					session, "ComponentManagementAction"));
		}
		catch (Exception e)
		{
			trace_errors.writeTrace("Erreur lors du chargement des" +
					" actions d'exploitation");
			// Une erreur est survenue
			throw new InnerException("&ERR_LoadData", null, null);
		}
		
		// On traite enfin les transitions entre états
		try {
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			// On récupère la table associée aux groupes de transitions
			String TableTransitionGroups = 
				configuration_api.getString("ComponentConfiguration." +
						"TableTransitionGroups");
			String EndStateTransitionGroup = 
				configuration_api.getString("ComponentConfiguration." +
						"TableTransitionGroups.EndState");
			
			// On sélectionne l'état d'arrivée de la transition 
			String [] column = { EndStateTransitionGroup };
			String [] result = session.getSelectResult(TableTransitionGroups, 
					column, "", "", _context);
	
			if (result != null && result.length > 1) {
				
				IsisTableDefinition table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableTransitionGroups);
				
				// Pour chaque groupe, on crée un ComponentTransitionGroups 
				// que l'on charge à partir de la table
				for ( int index = 1 ; index < result.length ; index ++) {
					ComponentTransitionGroups group = 
						new ComponentTransitionGroups();
					
					IsisParameter[] valuesOfTableKey =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					group.loadData(session, _context, valuesOfTableKey);
					
					// On recherche l'état d'arrivée à partir de son nom et
					// on met à jour le groupe de transitions
					ComponentStates endState = findStateByName(result[index]);
					if (endState != null)
						group.setEndState(endState);
					
					// Une fois le groupe de transitions créés, il faut
					// remplir la partie spécifiques du groupe.
					String TableTransitions = 
						configuration_api.getString(
							"ComponentConfiguration.TableTransitions");
					String StartStateTransition = configuration_api.getString(
							"ComponentConfiguration.TableTransitions.StartState");
					String CommandTransition = configuration_api.getString(
							"ComponentConfiguration.TableTransitions.Command");
					String CommandTypeTransition = configuration_api.getString(
							"ComponentConfiguration.TableTransitions.CommandType");
					String CheckIntervalTransition = configuration_api.getString(
							"ComponentConfiguration.TableTransitions.CheckInterval");
					String CheckTimeOutTransition = configuration_api.getString(
							"ComponentConfiguration.TableTransitions.TimeOut");
					
					// On sélectionne toutes les colonnes de la table des transitions.
					String [] columnTransition = { "" };
					
					// La condition se porte sur l'état d'arrivée du groupe de transition
					// précedemment défini
					String condition = EndStateTransitionGroup + "='" + 
							result[index] + "'";
					// On exécute la sélection
					String [] resultTransition = session.getSelectResult(
							TableTransitions, columnTransition, 
							condition, "", _context);
					
					// On crée ici le futur tableau des transitions
					ArrayList<ComponentTransition> transitionArray = null;
					
					if (resultTransition != null 
							&& resultTransition.length > 1) {
						// On récupère la définition de la table des 
						// transitions
						IsisTableDefinition table_definition_transition = 
							TreeNodeFactory.buildDefinitionFromSelectResult(
									resultTransition, TableTransitions);
						
						transitionArray = 
							new ArrayList<ComponentTransition>();
					
						// Pour chaque transition de la table, on crée un 
						// ComponentTransition que l'on charge à partir des
						// données de la table précedemment récupérées.
						for ( int indexTransition = 1 ; 
							indexTransition < resultTransition.length ; 
							indexTransition ++) {
							
							// On construit un tableau de type IsiParameter
							// contenant les informations d'un ComponentTransition
							IsisParameter[] object_parameters =
								TreeNodeFactory.
								buildParametersFromSelectResult(
									resultTransition, indexTransition, 
									table_definition_transition);
							
							// On construit le ComponentTransition à l'aide
							// du tableau précédent
							ComponentTransition transition = 
								new ComponentTransition();
							
							for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
								if (!object_parameters[index2].value.equals("")) {
									if (object_parameters[index2].name.equals(CommandTransition))
										transition.setCommand(object_parameters[index2].value);
									else if (object_parameters[index2].name.equals(CommandTypeTransition))
										transition.setCommandType(object_parameters[index2].value);
									else if (object_parameters[index2].name.equals(CheckIntervalTransition))
										transition.setCheckInterval(Integer.parseInt(object_parameters[index2].value));
									else if (object_parameters[index2].name.equals(CheckTimeOutTransition))
										transition.setCheckTimeOut(Integer.parseInt(object_parameters[index2].value));
									else if ( !object_parameters[index2].name.equals(StartStateTransition) 
											&& !object_parameters[index2].name.equals(EndStateTransitionGroup) )
										System.out.println("Erreur, " + object_parameters[index2].name + " innatendu.");
								}
							}
							
							// On recherche l'état de départ
							// en tenant compte de l'état Quelconque
							String nameState = object_parameters[0].value;
							if (nameState.equals(configuration_api.
									getString("ComponentConfiguration.AnyState")))
								nameState = MessageManager.getMessage(
									"&ComponentConfiguration_AnyState");
							ComponentStates startState = findStateByName(
									nameState);
							if (startState != null)
								transition.setStartState(startState);
							
							transition.setFlag('E');
							
							// Une transition sépcifique à été définie
							transitionArray.add(transition);
						}
					}
					// Un ensemble de transitions sépcifiques à été définie
					// On met à jour le groupe
					group.setSpecificTransition(transitionArray);
					_modelTable.add(group);
				}
			}
		}
		catch (Exception e)
		{
			trace_errors.writeTrace("Erreur lors du chargement des" +
					" transitions");
			// Une erreur est survenue
			throw new InnerException("&ERR_LoadData", null, null);
		}

		// On traite les fichiers de configuration du composant
		try {
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			// On récupère la table associée aux fichiers ainsi que les 
			// champs de la clé
			String TableConfig = 
				configuration_api.getString("ComponentConfiguration." +
						"TableConfig");
			String NameConfig = 
				configuration_api.getString("ComponentConfiguration." +
						"TableConfig.Name");
			String PathConfig = 
				configuration_api.getString("ComponentConfiguration." +
						"TableConfig.AbsolutePath");
			
			String [] tableKey = { NameConfig, PathConfig };
			_modelTable.addAll(createComponent(TableConfig, tableKey , 
					session, "ComponentConfig"));
		}
		catch (Exception e)
		{
			trace_errors.writeTrace("Erreur lors du chargement des" +
					" fichiers de configuration");
			// Une erreur est survenue
			throw new InnerException("&ERR_LoadData", null, null);
		}

		// On traite les fichiers de log du composant
		try {
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			// On récupère la table associée aux fichiers ainsi que les 
			// champs de la clé
			String TableLog = 
				configuration_api.getString("ComponentConfiguration." +
						"TableLog");
			String NameLog = 
				configuration_api.getString("ComponentConfiguration." +
						"TableLog.Name");
			String RootLog = 
				configuration_api.getString("ComponentConfiguration." +
						"TableLog.Root");
			String PatternLog = 
				configuration_api.getString("ComponentConfiguration." +
						"TableLog.Pattern");
			
			String [] tableKey = { NameLog, RootLog, PatternLog };
			_modelTable.addAll(createComponent(TableLog, tableKey , 
					session, "ComponentLog"));
		}
		catch (Exception e)
		{
			trace_errors.writeTrace("Erreur lors du chargement des" +
					" fichiers de log");
			// Une erreur est survenue
			throw new InnerException("&ERR_LoadData", null, null);
		}
		
		windowInterface.setStatus("&Status_DisplayWindow", null, 3);
		
		// Une fois le modèle chargé, on peut afficher le premier écran
		displayFirstPanel();
		// On affiche l'assistant
		display();
		
		windowInterface.setStatus(null, null, 0);
		
		trace_methods.endOfMethod();
	}
	
	/*------------------------------------------------------------
	 * Nom: preLoad
	 * 
	 * Description:
	 * Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	 * est appelée pour effectuer un pré-chargement du processeur.
	 * ------------------------------------------------------------*/
	public void preLoad() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "preLoad");
		
		// Chargement du fichier de messages
		MessageManager.loadFile("componentwizard.mdb", "UTF8");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();		
	}
	
	/*------------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	 * est appelée pour récupérer la description du processeur.
	 * 
	 * Retourne: La description du processeur.
  	 * ------------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "getDescription");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	
		return MessageManager.getMessage("&ComponentConfigurationProcessorDescription");
	}

	/*------------------------------------------------------------
	 * Nom: duplicate
	 * 
	 * Description:
	 * Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	 * est appelée pour récupérer un double du processeur.
	 * 
	 * Retourne: Une nouvelle instance du processeur.
	 * ------------------------------------------------------------*/
	public ProcessorInterface duplicate() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	
		return new ComponentConfigurationProcessor();
	}

	/*------------------------------------------------------------
	 * Nom: close
	 * 
	 * Description:
	 * Cette méthode rédéfinit celle de la super classe. Elle est appelée lorsque
	 * la sous-fenêtre du processeur doit être fermée (par l'utilisateur ou par
	 * la fermeture de l'application).
	 * ------------------------------------------------------------*/
	public void close() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "close");
		trace_methods.beginningOfMethod();
		
		freeAndReleaseUnusedMemoryBySettingVariablesNull();
		super.close();
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ComponentConfigurationProcessor", "isGlobalCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return false;
	}
	
	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un noeud 
	* d'exploration.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* autorisée.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ComponentConfigurationProcessor", "isTreeCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un 
	* élément de tableau.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* autorisée.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ComponentConfigurationProcessor", "isTableCapable");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return false;
	}
	
	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe ProcessorFrame. Elle 
	* est appelée pour récupérer l'intitulé de l'élément du menu "Outils" 
	* associé à ce processeur.
	* Ce processeur n'étant pas global, cette méthode ne sera pas appelée.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ComponentConfigurationProcessor", "getMenuLabel");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return "ConfigurationComponent";
	}
	
	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: saveModel
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe AbstractWindow. Elle 
	* est appelée par le dernier panneau de l'application de paramétrage
	* afin de lancer la procédure de sauvegarde des données saisis dans les
	* tables de la base de données.
	* Cette méthode va parcourir le tableau contenant les données saisis
	* et va appeler pour chaque élément la méthode de sauvegarde : saveData.
	* 
	* Retourne: Vrai (true) si tout c'est bien passé, faux (false) sinon.
	* ----------------------------------------------------------*/
	protected boolean saveModel() throws InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "saveModel");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String action_id;
		
		trace_methods.beginningOfMethod();
		// On commence par récupérer le numéro d'action
		action_id = LogServiceProxy.getActionIdentifier(
			_selectedNode.getAgentName(), 
			"&LOG_AssistedComponentConfiguration",
			PasswordManager.getInstance().getUserName(), 
			_selectedNode.getServiceName(),
			_selectedNode.getServiceType(), 
			_selectedNode.getIClesName());
		// On va générer un message de log de démarrage
		String[] message = new String[2];
		message[0] = MessageManager.getMessage("&LOG_ComponentName") + 
			((IsisParameter)_context.get("ICleName")).value;
		message[1] = MessageManager.getMessage("&LOG_ComponentRelease") +
			((IsisParameter)_context.get("Version")).value;
		LogServiceProxy.addMessageForAction(action_id, message);
		message = new String[1];
		message[0] = MessageManager.getMessage(
			"&LOG_EndOfComponentConfiguration");
		if (_modelTable != null) {
			// Pour chaque élément du model, on demande la sauvegarde
			for ( int i = 0 ; i < _modelTable.size() ; i++ ) {
				// Si il y a eu un problème, on stop la sauvegarde
				try {
					if ( !_modelTable.get(i).saveData(action_id, _context, _selectedNode) ) {
						// On va générer un message de log de fin
						LogServiceProxy.addMessageForAction(action_id, message);
						return false;
					}
				}
				catch (InnerException exception) 
				{
					trace_errors.writeTrace(
						"Problème lors de la sauvegarde du modèle !");
					// On va générer un message de log de fin
					LogServiceProxy.addMessageForAction(action_id, message);
					// C'est une erreur, on la signale
					throw new InnerException("&ERR_SaveData", null, null);
				}
			}
		}
		// On va générer un message de log de fin
		LogServiceProxy.addMessageForAction(action_id, message);
		trace_methods.endOfMethod();
		return true;
	}
	
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _selectedNode
	 * 
	 * Description:
	 * Cette attribut maintient une référence sur le noeud qui a été 
	 * sélectionné lors de l'appel au processeur de paramétrage. 
	 * On utilise cet attribut lors de l'accès aux tables de la base 
	 * de données.
	 * ----------------------------------------------------------*/
	private GenericTreeObjectNode _selectedNode;
	
	/*----------------------------------------------------------
	 * Nom: _context
	 * 
	 * Description:
	 * Cette référence correspond à l'ensemble des paramètres
	 * "exportables" des noeuds traversés pour atteindre le 
	 * noeud concerné. A cet ensemble sont ajoutés tous les 
	 * paramètres du noeud lui-même ainsi que les paramètres de 
	 * pré-processing nécessaire à l'accès aux tables de la base
	 * de données.
	 * ----------------------------------------------------------*/
	private IndexedList _context;
	
	/*----------------------------------------------------------
	* Nom: findStateByName
	* 
	* Description:
	* Cette méthode est utilisée pour retrouver un état d'un composant
	* Isis : ComponentStates à partir du nom de cette état. Elle
	* est appelée lors de la construction des transitions.
	* 
	* Retourne: Le ComponentStates associé au nom recherché, null 
	* s'il n'existe pas.
	* ----------------------------------------------------------*/
	private ComponentStates findStateByName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "saveModel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
		
		trace_methods.beginningOfMethod();
		
		if (_modelTable != null) {
			ComponentStates cs = null;
			// On parcours le tableau de ModelInterface
			for (int index=0 ; index < _modelTable.size() ; index++) {
				// Si on tombe sur un ComponantStates, on vérifie si le
				// nom de l'état correspond à celui recherché
				if (_modelTable.get(index) instanceof ComponentStates) {
					cs = (ComponentStates) _modelTable.get(index);
					// Si le nom est bon, on renvoie l'état
					if (cs.getStateName().equals(name)) {
						trace_methods.endOfMethod();
						return cs;
					}
				}
			}
		}
		trace_methods.endOfMethod();
		return null;
	}
	
	/*----------------------------------------------------------
	* Nom: createComponent
	* 
	* Description:
	* Cette méthode est utilisée pour créer et chargée un ensemble 
	* de composants du modèle à partir du nom de la table dont on 
	* souhaite extraite les données. 
	* Elle est appelée lors de la construction des Composants.
	*
	* Paramètres :
	*   - tableName : Une référence sur le nom de la table à traiter.
	*   - tableKey : Un tableau de chaînes de caractères contenant le
	*   			nom des champs composant la clé de la table.
	*   - session : Une référence sur une session de service nécessaire
	*   			à l'exécution des requêtes.
	*   _componentType : Une chaîne de caractères correspondant au type
	*   			de composant associé à la table.
	* 
	* Retourne: Un tableau de ModelInterface correspondant à l'ensemble
	* des composants créés.
	* ----------------------------------------------------------*/
	private ArrayList<ModelInterface> createComponent(
			String tableName, 
			String [] tableKey,
			ServiceSessionProxy session, 
			String componentType) 
			throws InnerException {
	
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfigurationProcessor", "createComponent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("tableKey=" + tableKey);
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("componentType=" + componentType);
		
		try {
			ArrayList<ModelInterface> tableComponent = new ArrayList<ModelInterface>();
			IsisTableDefinition table_definition;
			
			// On effectue une requête de sélection sur la table à traité
			// pour en connaître sa définition, le contenu de sa clé.
			String [] result = session.getSelectResult(tableName, 
					tableKey, "", "", _context);
			
			if (result != null && result.length > 1) {
				// On récupère la définition de la table.
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, tableName);
				
				// Pour chaque données de la table, on sait qu'il s'agit
				// des valeurs de la clé de la table. On construit donc les 
				// un composant pour chaque éléments de la table.
				for ( int index = 1 ; index < result.length ; index ++) {
					IsisParameter[] valuesOfTableKey =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On instancie le composant en fonction du type demandé
					ModelInterface comp;
					if (componentType.equals("ComponentVariable")) {
						comp = new ComponentVariable();
					}
					else if (componentType.equals("ComponentStates")) {
						comp = new ComponentStates();
					}
					else if (componentType.equals("ComponentManagementAction")) {
						comp = new ComponentManagementAction();
					}
					else if (componentType.equals("ComponentConfig")) {
						comp = new ComponentConfig();
					}
					else if (componentType.equals("ComponentLog")) {
						comp = new ComponentLog();
					}
					else {
						trace_errors.writeTrace("Erreur " + componentType +
								" innatendu comme type de composant !");
						// Une erreur est survenue
						throw new InnerException("&ERR_ComponentType", null, null);
					}
					// On charge le composant à partir de la table
					comp.loadData(session, _context, valuesOfTableKey);
					tableComponent.add(comp);
				}
			}
			trace_methods.endOfMethod();
			return tableComponent;
		}
		catch (Exception e)
		{
			trace_errors.writeTrace("Erreur lors de la création des " +
					"composants associés à la table " + tableName);
			// Une erreur est survenue
			throw new InnerException("&ERR_Query", null, null);
		}
	}
}
