/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits réservés.
 * ------------------------------------------------------------
 *
 * $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/appcfg/model
 * $Revision: 1.5 $
 *
 * ------------------------------------------------------------
 * DESCRIPTION: 
 * DATE:        29/08/2008
 * AUTEUR:      Florent Cossard
 * PROJET:      I-SIS
 * GROUPE:      
 * ------------------------------------------------------------
 * CONTROLE DES MODIFICATIONS
 *
 * ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.appcfg.model;

//
//Imports système
//

import java.util.ArrayList;

import javax.swing.text.StyledDocument;

//
// imports du projet
//
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.message.MessageManager;
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

/*----------------------------------------------------------
 * Nom: PortalApplicationServices
 * 
 * Description:
 * Cette classe représente un service d'une application.
 * Cette classe conserve en mémoire un nom pour le service
 * et le nom de l'agent sur lequel se trouve le service et une
 * liste d'états pour le service.
 * Elle est utilisée lors du processus de création ou de 
 * modification d'une application dans la console.
 * ----------------------------------------------------------*/
public class PortalApplicationServices implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: PortalApplicationServices
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public PortalApplicationServices() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationServices", "PortalApplicationServices");
		trace_methods.beginningOfMethod();

		_serviceName = "";
		_agentName = "";
		_flag = ' ';
		_servicesStates = new ArrayList<PortalApplicationServicesStates>();

		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: getServiceName
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé au service
	 * 
	 * Retourne: Une chaine de caractere : Le nom du service.
	 * ----------------------------------------------------------*/
	public String getServiceName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationServices", "getServiceName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _serviceName;
	}

	/*----------------------------------------------------------
	 * Nom: getAgentName
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé à l'agent
	 * 
	 * Retourne: Une chaine de caractere : Le nom de l'agent.
	 * ----------------------------------------------------------*/

	public String getAgentName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationServices", "PortalAgentName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _agentName;
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
				"PortalApplicationServices", "PortalFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _flag;
	}

	/*----------------------------------------------------------
	 * Nom: getServiceState
	 * 
	 * Description:
	 * Cette méthode retourne la liste des états du service.
	 * 
	 * Retourne: Un tableau de PortalApplicationServicesStates.
	 * ----------------------------------------------------------*/
	public ArrayList<PortalApplicationServicesStates> getServiceState() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationServices", "getServiceState");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _servicesStates;
	}

	/*----------------------------------------------------------
	 * Nom: setServiceName
	 * 
	 * Description:
	 * Cette méthode modifie le nom du service en lui donnant 
	 * celui passé en parametre.
	 * 
	 * Arguments:
	 * - name: Le nouveau nom du service
	 * ----------------------------------------------------------*/
	public void setServiceName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationServices", "setServiceName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);

		_serviceName = name;

		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setAgentName
	 * 
	 * Description:
	 * Cette méthode modifie le nom de l'agent en lui donnant 
	 * celui passé en parametre.
	 * 
	 * Arguments:
	 * - name: Le nouveau nom du service
	 * ----------------------------------------------------------*/
	public void setAgentName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationServices", "setAgentName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);

		_agentName = name;

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
				"PortalApplicationService", "setFlag");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();

		trace_arguments.writeTrace("flag=" + flag);

		_flag = flag;

		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: getNewServiceState
	 * 
	 * Description:
	 * Cette méthode retourne la nouvelle liste des états du service.
	 * 
	 * Retourne : La nouvelle liste des états.
	 * ----------------------------------------------------------*/
	public ArrayList<PortalApplicationServicesStates> getNewServiceState() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationServices", "getServiceState");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _newServicesStates;
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

		String NameService = MessageManager
				.getMessage("&AppCfg_Name");
		String NameAgent = MessageManager
				.getMessage("&AppCfg_Description");

		document.insertString(offset,  
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		document.insertString(offset, NameAgent + " : " + _agentName
				+ System.getProperty("line.separator", "\n"), document
				.getStyle("regular"));
		document.insertString(offset, NameService + " : " + _serviceName + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
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
		return true;
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
	 * PortalApplicationServices. 
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
		"PortalApplicationDoc", "loadData");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableServices = "PortalApplicationServices";
		String AgentService = "AgentName";
		String NameService = "ServiceName";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
	
		try {
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux services.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableServices= configuration_api.getString("PortalApplication." +
					"TableServices");
			AgentService = configuration_api.getString("PortalApplication." +
					"TableServices.AgentName");
			NameService = configuration_api.getString("PortalApplication." +
					"TableServices.ServiceName");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
				
		// On sélectionne les colonnes de la table qui nous interressent
		String [] column = new String[2];
		column[0] = AgentService;
		column[1] = NameService;
			
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
			String [] result = session.getSelectResult(TableServices, 
					column, condition, "", context);
			
			if (result != null) {
				// On construit la définition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableServices);
				
				// Pour chaque ligne résultat de la requête Select
				for (int index = 1 ; index < result.length ; index++) {
					// On récupère les données
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On construit le PortalApplicationServices
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(AgentService))
								_agentName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(NameService))
								_serviceName = object_parameters[index2].value;
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
				"Problème lors du chargement des données : " +
				exception.getMessage());
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_LoadData", null, exception);
		}
		
		_flag = 'E';
		
		
		// On charge l'ensemble des états du service depuis la table
		// PortalServiceStates
		String TableServicesStates = "PortalServiceStates";
		String AgentServiceStates = "AgentName";
		String ServiceServiceStates = "ServiceName";
		String NameServiceStates = "StateName";
		
		try {
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableServicesStates= configuration_api.getString("PortalApplication." +
					"TableServicesStates");
			AgentServiceStates = configuration_api.getString("PortalApplication." +
					"TableServicesStates.AgentName");
			ServiceServiceStates = configuration_api.getString("PortalApplication." +
					"TableServicesStates.ServiceName");
			NameServiceStates = configuration_api.getString("PortalApplication." +
					"TableServicesStates.StateName");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
		
		
		String[] selectedColumns = { NameServiceStates };
		String conditionStates = AgentServiceStates + "='" + _agentName + 
					"' AND " + ServiceServiceStates + "='" + _serviceName + "'";
		
		try {
			// On executte la requete
			String[] result = session.getSelectResult(TableServicesStates, selectedColumns,
						conditionStates, "", context);
	
			if (result != null && result.length >= 2) {
					String[] stateList = new String[result.length - 1];
	
					// On nerécupère que le nom de l'état
					System.arraycopy(result, 1, stateList, 0,
									result.length - 1);
					// On sauvegarde le nom de l'état
					for (int i = 0 ; i < stateList.length ; i++)
						this.addServiceState(stateList[i]);
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
	 * table correspondant aux fichiers de configuration. Puis via la classe
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
	public boolean saveData(String actionId, IndexedList context,
			GenericTreeObjectNode selectedNode) throws InnerException {
		return false;
	}

	/*----------------------------------------------------------
	 * Nom : addServiceState
	 * 
	 * Description :
	 * Cette méthode permet d'ajouter 1 état à un service. 
	 * Le nom de l'état est passé en paramètre. Avant de l'ajouter, 
	 * on vérifie qu'il n'existe pas un état portant déjà le même nom.
	 * 
	 * ----------------------------------------------------------*/
	public void addServiceState(String name) {
		if (_servicesStates == null)
			_servicesStates = new ArrayList<PortalApplicationServicesStates>();

		int index = 0;
		while (index < _servicesStates.size()) {
			if (_servicesStates.get(index).getStateServiceName().equals(name)) {
				break;
			}
			index++;
		}
		if (index == _servicesStates.size()) {
			PortalApplicationServicesStates state = new PortalApplicationServicesStates();
			state.setStateServiceName(name);
			state.setFlag('E');
			_servicesStates.add(state);
		}
	}

	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément 
	 * PortalApplicationServices.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		PortalApplicationServices clone = new PortalApplicationServices();
		clone.setAgentName(_agentName);
		clone.setServiceName(_serviceName);
		
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _serviceName
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur le nom du service
	 * ----------------------------------------------------------*/
	private String _serviceName;

	/*----------------------------------------------------------
	 * Nom: _agentName
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur le nom de l'agent ou
	 * est exécuté de service
	 * ----------------------------------------------------------*/
	private String _agentName;

	/*----------------------------------------------------------
	 * Nom: _servicesStates
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur la liste des états de 
	 * l'application.
	 * ----------------------------------------------------------*/
	private ArrayList<PortalApplicationServicesStates> _servicesStates;

	/*----------------------------------------------------------
	 * Nom: _servicesStates
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur la nouvelle liste des 
	 * états de l'application à associé au service.
	 * ----------------------------------------------------------*/
	private ArrayList<PortalApplicationServicesStates> _newServicesStates;

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
