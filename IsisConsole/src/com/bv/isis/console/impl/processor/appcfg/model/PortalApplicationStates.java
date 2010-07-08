/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits réservés.
 * ------------------------------------------------------------
 *
 * $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/appcfg/model
 * $Revision: 1.3 $
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
// Imports système
//
import java.util.ArrayList;

import javax.swing.text.StyledDocument;

//
// Imports du projet
//
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

/*----------------------------------------------------------
 * Nom: PortalApplicationStates
 * 
 * Description:
 * Cette classe représente un état d'une application.
 * Cette classe conserve en mémoire un nom pour l'état,
 * une desription et une liste d'états de services.
 * 
 * Elle est utilisée lors du processus de création ou de 
 * modification d'une application dans la console.
 * ----------------------------------------------------------*/
public class PortalApplicationStates implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: PortalApplicationStates
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public PortalApplicationStates() {
		
		_stateName = "";
		_description = "";
		_flag = ' ';
		
		_newDescription = "";
		
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
	public void display(StyledDocument styledDocument, int offset)
			throws Exception {

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
	 * PortalApplicationStates. 
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
				"portalApplicationStates", "loadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableStates = "TableStates";
		String NameState = "StateName";
		String DescState = "Description";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		
		try {
			
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux états de l'application.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableStates = 
				configuration_api.getString("PortalApplication.TableStates");
			NameState = 
				configuration_api.getString("PortalApplication.TableStates.StateName");
			DescState = 
				configuration_api.getString("PortalApplication.Tablestates.Description");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
		
		// On sélectionne les colonnes de la table qui nous interressent
		String [] column = new String[2];
		column[0] = NameState;
		column[1] = DescState;
			
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
						
					// On construit le PortalApplicationStates
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameState))
								_stateName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DescState))
								_description = object_parameters[index2].value;
							else 
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
	 * Nom: getStateName
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé à l'état
	 * 
	 * Retourne: Une chaine de caractere : Le nom dé l'état.
	 * ----------------------------------------------------------*/
	public String getStateName() {
		return _stateName;
	}

	/*----------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé à la description 
	 * de l'état
	 * 
	 * Retourne: Une chaine de caractere : La description de l'état.
	 * ----------------------------------------------------------*/
	public String getDescription() {
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
		return _flag;
	}

	/*----------------------------------------------------------
	 * Nom: getSpecificStates
	 * 
	 * Description:
	 * Cette méthode retourne la liste des états des services associée
	 * à l'état de l'application
	 * 
	 * Retourne: Un tableau : la liste des états des services.
	 * ----------------------------------------------------------*/
	public ArrayList<PortalApplicationServicesSpecificStates> getSpecificStates() {
		return _specificStates;
	}
	
	/*----------------------------------------------------------
	 * Nom: setStateName
	 * 
	 * Description:
	 * Cette méthode modifie le nom de l'état en lui donnant 
	 * celui passé en parametre.
	 * 
	 * Arguments:
	 * - name: Le nouveau nom de l'état
	 * ----------------------------------------------------------*/
	public void setStateName(String name) {
		_stateName = name;
	}

	/*----------------------------------------------------------
	 * Nom: setDescription
	 * 
	 * Description:
	 * Cette méthode modifie la description de l'état en lui donnant 
	 * celui passé en parametre.
	 * 
	 * Arguments:
	 * - description: La nouvelle description
	 * ----------------------------------------------------------*/
	public void setDescription(String description) {
		_description = description;
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
		_flag = flag;
	}

	/*----------------------------------------------------------
	 * Nom: setSpecificStates
	 * 
	 * Description:
	 * Cette méthode modifie la liste des états des services caractérisant
	 * l'état de l'application courant.
	 * 
	 * Paramètre : 
	 *   - states : la nouvelle liste d'états de services.
	 * ----------------------------------------------------------*/
	public void setSpecificStates(
			ArrayList<PortalApplicationServicesSpecificStates> states) {
		_specificStates = states;
	}
	
	/*----------------------------------------------------------
	 * Nom: getNewDescription
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé à la nouvelle description 
	 * de l'état saisie par l'utilisateur
	 * 
	 * Retourne: Une chaine de caractere : La description de l'état.
	 * ----------------------------------------------------------*/
	public String getNewDescription() {
		return _newDescription;
	}
	
	/*----------------------------------------------------------
	 * Nom: setNewDescription
	 * 
	 * Description:
	 * Cette méthode modifie la nouvelle description de l'état en lui donnant 
	 * celle passé en parametre.
	 * 
	 * Arguments:
	 * - description: La nouvelle description
	 * ----------------------------------------------------------*/
	public void setNewDescription(String description) {
		_newDescription = description;
	}

	/*----------------------------------------------------------
	 * Nom: getNewSpecificStates
	 * 
	 * Description:
	 * Cette méthode retourne la nouvelle liste des états des services associée
	 * à l'état de l'application
	 * 
	 * Retourne: Un tableau : la liste des états des services.
	 * ----------------------------------------------------------*/
	public ArrayList<PortalApplicationServicesSpecificStates> getNewSpecificStates() {
		return _newSpecificStates;
	}
	
	/*----------------------------------------------------------
	 * Nom: setNewSpecificStates
	 * 
	 * Description:
	 * Cette méthode modifie la nouvelle liste des états des services 
	 * caractérisant l'état de l'application courant.
	 * 
	 * Paramètre : 
	 *   - states : la nouvelle liste d'états de services.
	 * ----------------------------------------------------------*/
	public void setNewSpecificStates(
			ArrayList<PortalApplicationServicesSpecificStates> states) {
		_newSpecificStates = states;
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
		
		PortalApplicationStates clone = new PortalApplicationStates();
		clone.setStateName(_stateName);
		clone.setDescription(_description);
		clone.setSpecificStates(_specificStates);
		
		clone.setNewDescription(_newDescription);
		clone.setNewSpecificStates(_newSpecificStates);
		clone.setFlag(_flag);
		
		return clone;
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _stateName
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur le nom de l'état
	 * ----------------------------------------------------------*/
	private String _stateName;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur la description de l'état
	 * ----------------------------------------------------------*/
	private String _description;

	/*----------------------------------------------------------
	 * Nom: _flag
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur l'état courant de l'état
	 * ----------------------------------------------------------*/
	private char _flag;
	
	/*----------------------------------------------------------
	 * Nom: _specificStates
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur la liste des états de services
	 * de l'état d'application
	 * ----------------------------------------------------------*/
	private ArrayList<PortalApplicationServicesSpecificStates> _specificStates;
	
	/*----------------------------------------------------------
	 * Nom: _newDescription
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur la nouvelle description de l'état
	 * ----------------------------------------------------------*/
	private String _newDescription;
	
	/*----------------------------------------------------------
	 * Nom: _newSpecificStates
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur la nouvelle liste des états 
	 * de services de l'état d'application
	 * ----------------------------------------------------------*/
	private ArrayList<PortalApplicationServicesSpecificStates> _newSpecificStates;
	
}
