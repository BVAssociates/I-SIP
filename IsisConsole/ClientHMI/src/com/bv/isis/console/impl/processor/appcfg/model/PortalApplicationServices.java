/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
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
// D�claration du package
package com.bv.isis.console.impl.processor.appcfg.model;

//
//Imports syst�me
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
 * Cette classe repr�sente un service d'une application.
 * Cette classe conserve en m�moire un nom pour le service
 * et le nom de l'agent sur lequel se trouve le service et une
 * liste d'�tats pour le service.
 * Elle est utilis�e lors du processus de cr�ation ou de 
 * modification d'une application dans la console.
 * ----------------------------------------------------------*/
public class PortalApplicationServices implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: PortalApplicationServices
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
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
	 * Cette m�thode retourne le nom associ� au service
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
	 * Cette m�thode retourne le nom associ� � l'agent
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
	 * Cette m�thode retourne l'�tat courant.
	 * 
	 * Retourne: Un caract�re, l'�tat courant.
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
	 * Cette m�thode retourne la liste des �tats du service.
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
	 * Cette m�thode modifie le nom du service en lui donnant 
	 * celui pass� en parametre.
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
	 * Cette m�thode modifie le nom de l'agent en lui donnant 
	 * celui pass� en parametre.
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
	 * Cette m�thode modifie l'�tat courant avec celui pass� en entr�e.
	 * 
	 * Param�tre : 
	 *   - flag : Le nouvel �tat courant.
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
	 * Cette m�thode retourne la nouvelle liste des �tats du service.
	 * 
	 * Retourne : La nouvelle liste des �tats.
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
	 * Cette m�thode impl�mente la m�thode de l'interface ModelInterface.
	 * Cette m�thode est appel�e lors de la destruction de l'assistant. Elle est 
	 * utiliser pour lib�rer l'espace m�moire utilis� par les variables des
	 * classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public boolean end() {
		return true;
	}

	
	/*----------------------------------------------------------
	 * Nom: loadData
	 * 
	 * Description:
	 * Cette m�thode charge le mod�le de donn�es � partir de la base 
	 * de donn�es. Elle red�finit la m�thode LoadData de l'interface 
	 * ModelInterface. Apr�s avoir r�cup�rer les noms de la table et des 
	 * champs, cette m�thode va effectuer une requ�te de s�lection sur la
	 * table avec comme condition, les champs de la cl� pass�s en param�tre.
	 * A partir du r�sultat, elle va compl�ter les champs du 
	 * PortalApplicationServices. 
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
	 * 
	 * Retourne: vrai (true) si tout c'est bien pass�, faux (false) 
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
			// On r�cup�re l'ensemble des noms des champs de la table
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
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand m�me
		}
				
		// On s�lectionne les colonnes de la table qui nous interressent
		String [] column = new String[2];
		column[0] = AgentService;
		column[1] = NameService;
			
		// On cr�e la condition en fonction des noms et valeurs de la cl� de 
		// la table.
		String condition = "";
		for (int index=0 ; index < valuesOfTableKey.length ; index ++) {
			condition += valuesOfTableKey[index].name + "='" + 
							valuesOfTableKey[index].value + "'";
			
			if (index != (valuesOfTableKey.length - 1))
				condition += " AND ";
		}
			
		try {
			// On lance la requ�te
			String [] result = session.getSelectResult(TableServices, 
					column, condition, "", context);
			
			if (result != null) {
				// On construit la d�finition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableServices);
				
				// Pour chaque ligne r�sultat de la requ�te Select
				for (int index = 1 ; index < result.length ; index++) {
					// On r�cup�re les donn�es
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
								// Une des colonnes retourn�es n'est pas bonne, 
								// on signale l'erreur
								trace_errors.writeTrace( "Probl�me : " + 
										object_parameters[index2] + "innatendu !");
								
							}
						}
					}
					
				}
				// On lib�re l'utilisation de la d�finition
				manager.releaseTableDefinitionLeasing(table_definition);
				
			}
		}
		catch (InnerException exception)
		{
			trace_errors.writeTrace(
				"Probl�me lors du chargement des donn�es : " +
				exception.getMessage());
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_LoadData", null, exception);
		}
		
		_flag = 'E';
		
		
		// On charge l'ensemble des �tats du service depuis la table
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
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand m�me
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
	
					// On ner�cup�re que le nom de l'�tat
					System.arraycopy(result, 1, stateList, 0,
									result.length - 1);
					// On sauvegarde le nom de l'�tat
					for (int i = 0 ; i < stateList.length ; i++)
						this.addServiceState(stateList[i]);
			}
		}
		catch (InnerException exception)
		{
			trace_errors.writeTrace(
				"Probl�me lors du chargement des donn�es : " +
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
	 * Cette m�thode sauvegarde le mod�le dans les tables. 
	 * Elle impl�mente la m�thode de la classe ModelInterface. Apr�s avoir
	 * r�cup�rer les noms des champs des tables, cette m�thode va construire
	 * la requ�te d'insertion ou de modification d'une donn�es de la 
	 * table correspondant aux fichiers de configuration. Puis via la classe
	 * ExecutionSurveyor, cette requ�te sera ex�cut�e. 
	 * 
	 * Param�tres :
	 *   - actionId : Le num�ro unique d'action
	 *   - context : Cette r�f�rence correspond � l'ensemble des param�tres
	 * 				"exportables" des noeuds travers�s pour atteindre le 
	 * 				noeud concern�. A cet ensemble sont ajout�s tous les 
	 * 				param�tres du noeud lui-m�me.
	 *   - seletedNode : Une r�f�rence sur le noeud s�lectionn�.
	 * 
	 * Retourne: vrai (true) si tout c'est bien pass�, faux (false) 
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
	 * Cette m�thode permet d'ajouter 1 �tat � un service. 
	 * Le nom de l'�tat est pass� en param�tre. Avant de l'ajouter, 
	 * on v�rifie qu'il n'existe pas un �tat portant d�j� le m�me nom.
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
	 * Cette m�thode permet de retourner une copie de l'�l�ment 
	 * PortalApplicationServices.
	 * 
	 * Retourne :le clone de l'�l�ment du mod�le sous le type ModelInterface.
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
	 * Cet attribut maintient une r�f�rence sur le nom du service
	 * ----------------------------------------------------------*/
	private String _serviceName;

	/*----------------------------------------------------------
	 * Nom: _agentName
	 * 
	 * Description: 
	 * Cet attribut maintient une r�f�rence sur le nom de l'agent ou
	 * est ex�cut� de service
	 * ----------------------------------------------------------*/
	private String _agentName;

	/*----------------------------------------------------------
	 * Nom: _servicesStates
	 * 
	 * Description: 
	 * Cet attribut maintient une r�f�rence sur la liste des �tats de 
	 * l'application.
	 * ----------------------------------------------------------*/
	private ArrayList<PortalApplicationServicesStates> _servicesStates;

	/*----------------------------------------------------------
	 * Nom: _servicesStates
	 * 
	 * Description: 
	 * Cet attribut maintient une r�f�rence sur la nouvelle liste des 
	 * �tats de l'application � associ� au service.
	 * ----------------------------------------------------------*/
	private ArrayList<PortalApplicationServicesStates> _newServicesStates;

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
}
