package com.bv.isis.console.impl.processor.appcfg.model;

import java.util.ArrayList;

import javax.swing.text.StyledDocument;

import com.bv.core.config.ConfigurationAPI;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableDefinition;

public class PortalApplicationGroupTransitions implements ModelInterface {

	public PortalApplicationGroupTransitions() {
		
	}
	
	public void display(StyledDocument styledDocument, int offset)
			throws Exception {

	}

	public boolean end() {
		return true;
	}

	public boolean loadData(ServiceSessionProxy session, IndexedList context,
			IsisParameter[] valuesOfTableKey) throws InnerException {
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationGroupTransitions", "loadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableTransitionGroup = "PortaApplicationTransitionGroups";
		String EndStateTransitionGroup = "EndName";
		String NameTransitionGroup = "GroupTransitionName";
		String DescriptionTransitionGroup = "Description";
		String TypeTransitionGroup = "TransitionType";
		String ResponsabilitiesTransitionGroup = "Responsabilities";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		
		try {
			
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux groupes de transitions.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableTransitionGroup = 
				configuration_api.getString("PortalApplication.TableTransitionGroups");
			EndStateTransitionGroup = 
				configuration_api.getString("PortalApplication.TableTransitionGroups.EndState");
			NameTransitionGroup = 
				configuration_api.getString("PortalApplication.TableTransitionGroups.Name");
			DescriptionTransitionGroup = 
				configuration_api.getString("PortalApplication.TableTransitionGroups.Description");
			TypeTransitionGroup = 
				configuration_api.getString("PortalApplication.TableTransitionGroups.Type");
			ResponsabilitiesTransitionGroup = 
				configuration_api.getString("PortalApplication.TableTransitionGroups.Responsabilities");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
		
		// On sélectionne les colonnes de la table qui nous interressent
		String [] column = new String[5];
		column[0] = EndStateTransitionGroup;
		column[1] = NameTransitionGroup;
		column[2] = DescriptionTransitionGroup;
		column[3] = TypeTransitionGroup;
		column[4] = ResponsabilitiesTransitionGroup;
			
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
			String [] result = session.getSelectResult(TableTransitionGroup, 
					column, condition, "", context);
		
			if (result != null) {
				// On construit la définition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
							result, TableTransitionGroup);
				// Pour chaque ligne résultat de la requête Select
				for (int index = 1 ; index < result.length ; index++) {
					// On récupère les données
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
						
					// On construit le PortalApplicationGroupTransitions
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameTransitionGroup))
								_groupTransitionName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DescriptionTransitionGroup))
								_description = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(TypeTransitionGroup))
								_transitionType = object_parameters[index2].value.charAt(0);
							else if (object_parameters[index2].name.equals(ResponsabilitiesTransitionGroup))
								_responsabilities = object_parameters[index2].value;
							else if (!object_parameters[index2].name.equals(EndStateTransitionGroup))
							{		
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

	public boolean saveData(String actionId, IndexedList context,
			GenericTreeObjectNode selectedNode) throws InnerException {
		return true;
	}



	public PortalApplicationStates getEndState() {
		return _endState;
	}

	public void setEndState(PortalApplicationStates state) {
		_endState = state;
	}

	public String getGroupTransitionName() {
		return _groupTransitionName;
	}

	public void setGroupTransitionName(String transitionName) {
		_groupTransitionName = transitionName;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public char getTransitionType() {
		return _transitionType;
	}

	public void setTransitionType(char transitionType) {
		_transitionType = transitionType;
	}

	public String getResponsabilities() {
		return _responsabilities;
	}

	public void setResponsabilities(String responsabilities) {
		_responsabilities = responsabilities;
	}

	public ArrayList<PortalApplicationTransitions> getTransitions() {
		return _transitions;
	}

	public void setTransitions(ArrayList<PortalApplicationTransitions> transitions) {
		_transitions = transitions;
	}
	
	public String getNewGroupTransitionName() {
		return _newGroupTransitionName;
	}

	public void setNewGroupTransitionName(String transitionName) {
		_newGroupTransitionName = transitionName;
	}

	public String getNewDescription() {
		return _newDescription;
	}

	public void setNewDescription(String description) {
		_newDescription = description;
	}

	public char getNewTransitionType() {
		return _newTransitionType;
	}

	public void setNewTransitionType(char transitionType) {
		_newTransitionType = transitionType;
	}

	public String getNewResponsabilities() {
		return _newResponsabilities;
	}

	public void setNewResponsabilities(String responsabilities) {
		_newResponsabilities = responsabilities;
	}

	public ArrayList<PortalApplicationTransitions> getNewTransitions() {
		return _newTransitions;
	}

	public void setNewTransitions(ArrayList<PortalApplicationTransitions> transitions) {
		_newTransitions = transitions;
	}	
	
	public char getFlag() {
		return _flag;
	}
	
	public void setFlag(char flag) {
		_flag = flag;
		
		// Pour chaque transitions, on pose le flag à 'S' si celui du groupe vaut 'S'
		if (flag == 'S' && _transitions != null) {
			for(int index = 0; index < _transitions.size();index++) {
				PortalApplicationTransitions trans = _transitions.get(index);
				// En cas d'un élément qui venait d'être créer, on le supprime
				if (trans.getFlag() == 'A') {
					_transitions.remove(index);
					index --;
				}
				// Si l'élement existait, on le supprimera de la base de données
				else {
					trans.setFlag('S');
				}
			}
		}
	}
	
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément 
	 * PortalApplicationGroupTransitions.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		ArrayList<PortalApplicationTransitions> transitionsClone = new ArrayList<PortalApplicationTransitions>();
		if (_transitions !=  null) {
			for (int index = 0 ; index < _transitions.size() ; index ++) { 
				transitionsClone.add(_transitions.get(index).clone());
			}
		}
		else {
			transitionsClone = null;
		}
		
		ArrayList<PortalApplicationTransitions> newTransitionsClone = new ArrayList<PortalApplicationTransitions>();
		if (_newTransitions != null) {
			for (int index = 0 ; index < _newTransitions.size() ; index ++) { 
				transitionsClone.add(_newTransitions.get(index).clone());
			}
		}
		else {
			newTransitionsClone = null;
		}
		
		PortalApplicationGroupTransitions clone = new PortalApplicationGroupTransitions();
		clone.setEndState(_endState);
		clone.setDescription(_description);
		clone.setGroupTransitionName(_groupTransitionName);
		clone.setTransitionType(_transitionType);
		clone.setResponsabilities(_responsabilities);
		clone.setTransitions(transitionsClone);
		
		clone.setNewDescription(_newDescription);
		clone.setNewGroupTransitionName(_newGroupTransitionName);
		clone.setNewTransitionType(_newTransitionType);
		clone.setNewResponsabilities(_newResponsabilities);
		clone.setNewTransitions(newTransitionsClone);
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	
	
	private PortalApplicationStates _endState;
	
	private String _groupTransitionName;
	
	private String _description;
	
	private char _transitionType;
	
	private String _responsabilities;
	
	private ArrayList<PortalApplicationTransitions> _transitions;
	
	private String _newGroupTransitionName;
	
	private String _newDescription;
	
	private char _newTransitionType;
	
	private String _newResponsabilities;
	
	private ArrayList<PortalApplicationTransitions> _newTransitions;

	
	
	private char _flag;
}
