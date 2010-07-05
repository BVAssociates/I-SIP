/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentTransitionGroups.java,v $
* $Revision: 1.15 $
*
* ------------------------------------------------------------
* DESCRIPTION: Définition de transation vers un état d'arrivée
* DATE:        05/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      impl.processor.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ComponentTransitionGroups.java,v $
* Revision 1.15  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.14  2008/09/12 16:09:49  fcd
* Ajout de la méthode Clone()
*
* Revision 1.13  2008/08/25 10:55:52  tz
* Ajout de l'argument actionId à saveData().
*
* Revision 1.12  2008/08/11 13:50:02  fcd
* *** empty log message ***
*
* Revision 1.11  2008/08/08 14:10:51  fcd
* *** empty log message ***
*
* Revision 1.10  2008/08/07 17:06:45  fcd
* *** empty log message ***
*
* Revision 1.9  2008/08/04 16:37:31  fcd
* *** empty log message ***
*
* Revision 1.8  2008/08/01 16:31:33  fcd
* *** empty log message ***
*
* Revision 1.7  2008/07/31 16:33:02  fcd
* *** empty log message ***
*
* Revision 1.6  2008/07/30 16:27:25  fcd
* *** empty log message ***
*
* Revision 1.5  2008/07/30 11:10:11  fcd
* *** empty log message ***
*
* Revision 1.4  2008/07/17 10:42:04  fcd
* Gestion de la suppression en base
*
* Revision 1.3  2008/07/11 09:54:32  fcd
* *** empty log message ***
*
* Revision 1.2  2008/06/20 09:28:11  fcd
* Ajout de la méthode getSpecificTransition()
*
* Revision 1.2  2008/06/19 15:27:32  fcd
* Ajout du champ _responsabilities et de ses accesseurs
* 
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.config.implementation.model;

//
//Imports système
//
import java.util.ArrayList;

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
 * Nom: ComponentsTransitionGroups
 * 
 * Description:
 * Cette classe modélise un ensemble de transitions entre deux états 
 * d'un composant I-SIS. Elle se caractérise par un état d'arrivée, 
 * un nom, une description et un changement de niveau fonctionnel. 
 * A l'aide du tableau de ComponentTransition, on spécifie chaque
 * transition du groupe. 
 * Cette classe est utilisée lors du processus de création ou de 
 * modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class ComponentTransitionGroups implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentTransitionGroups
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public ComponentTransitionGroups() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransitionGroups", "ComponentTransitionGroups");
		trace_methods.beginningOfMethod();
		
		_transitionName = "";
		_description = "";
		_responsabilities = "";
		_transitionType = ' ';
		_flag = ' ';
		
		_newTransitionName = "";
		_newDescription = "";
		_newResponsabilities = "";
		_newTransitionType = ' ';
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: getEndState
	 * 
	 * Description:
	 * Cette méthode retourne l'état d'arrivée de la transition.
	 * 
	 * Retourne: Un ComponentStates : L'état d'arrivée.
	 * ----------------------------------------------------------*/
	public ComponentStates getEndState() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransitionGroups", "getEndState");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _endState;
	}
	
	/*----------------------------------------------------------
	 * Nom: getTransitionName
	 * 
	 * Description:
	 * Cette méthode retourne le nom de la transition.
	 * 
	 * Retourne: Une chaîne de caracteres : Le nom de la transition.
	 * ----------------------------------------------------------*/
	public String getTransitionName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransitionGroups", "getTransitionName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _transitionName;
	}
	
	/*----------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette méthode retourne la chaine de caracteres 'description'
	 * associé a la transition.
	 * 
	 * Retourne: Une chaine de caractere : La description.
	 * ----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransitionGroups", "getDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _description;
	}
	
	/*----------------------------------------------------------
	 * Nom: getTransitionType
	 * 
	 * Description:
	 * Cette méthode retourne le caractere 'type' associe a la 
	 * transition
	 * 
	 * Retourne: Un caractere : Le type de la transition.
	 * ----------------------------------------------------------*/
	public char getTransitionType() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransitionGroups", "getTransitionType");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _transitionType;
	}
	
	/*----------------------------------------------------------
	 * Nom: getSpecificTransition
	 * 
	 * Description:
	 * Cette méthode retourne un tableau de ComponentTransition 
	 * correspondant à la liste des transition spécifiques pour ce 
	 * groupe.
	 * 
	 * Retourne: La liste des transition spécifiques.
	 * ----------------------------------------------------------*/
	public ArrayList<ComponentTransition> getSpecificTransition() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransitionGroups", "getTransitionType");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _specificTransitionTable;
	}
	
	/*----------------------------------------------------------
	 * Nom: getResponsabilities
	 * 
	 * Description:
	 * Cette méthode retourne la liste des responsabilités associe a la 
	 * transition
	 * 
	 * Retourne: La liste des responsabilités.
	 * ----------------------------------------------------------*/
	public String getResponsabilities() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransitionGroups", "getResponsabilities");
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
				"ComponentTransitionGroups", "getFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _flag;
	}
	
	/*----------------------------------------------------------
	 * Nom: setEndState
	 * 
	 * Description:
	 * Cette méthode modifie l'état d'arrivée de la transition.
	 * 
	 * Arguments: 
	 *  - endState: Le nouvel état d'arrivée.
	 * ----------------------------------------------------------*/
	public void setEndState(ComponentStates endState) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setEndState");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("endState=" + endState);
		
		if ((endState != null ) && (endState != _endState)) {
			_endState = endState;
		}
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setTransitionName
	 * 
	 * Description:
	 * Cette méthode modifie le nom de la transition.
	 * 
	 * Arguments: 
	 *  - name: Le nouveau nom de la transition.
	 * ----------------------------------------------------------*/
	public void setTransitionName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setTransitionName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
		
		_transitionName = name;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setDescription
	 * 
	 * Description:
	 * Cette méthode modifie la description de la transition en lui 
	 * donnant celle passée en parametre.
	 * 
	 * Arguments:
	 *  - description: La nouvelle description du composant
	 * ----------------------------------------------------------*/
	public void setDescription(String description) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		
		_description = description;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setTransitionType
	 * 
	 * Description:
	 * Cette méthode modifie le type de la transition en lui 
	 * donnant celui passé en parametre.
	 * 
	 * Arguments:
	 *  - transitionType: Le nouveau type de transition
	 * ----------------------------------------------------------*/
	public void setTransitionType(char transitionType) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setTransitionType");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("transitionType=" + transitionType);
		
		_transitionType = transitionType;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setSpecificTransition
	 * 
	 * Description:
	 * Cette méthode modifie le tableau de ComponentTransition 
	 * correspondant à la liste des transition spécifiques pour ce 
	 * groupe par celui passé en paramètre.
	 * 
	 * Arguments: 
	 *  - transitionArray : La nouvelle liste des transition spécifiques.
	 * ----------------------------------------------------------*/
	public void setSpecificTransition(
			ArrayList<ComponentTransition> transitionArray) {

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setSpecificTransition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("transitionArray=" + transitionArray);

		_specificTransitionTable = transitionArray;
		
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
				"ComponentTransition", "setResponsabilities");
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
	 * Elle modifie également les états des transitions spécifiques si on
	 * demande la suppression du groupe courant. Chaque transition spécifique 
	 * sera détruite.
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
		
		// On positionne le flag du groupe
		_flag = flag;
		
		// Pour chaque transitions, on pose le flag à 'S' si celui du groupe vaut 'S'
		if (flag == 'S' && _specificTransitionTable != null) {
			for(int index = 0; index < _specificTransitionTable.size();index++) {
				ComponentTransition c = _specificTransitionTable.get(index);
				// En cas d'un élément qui venait d'être créer, on le supprime
				if (c.getFlag() == 'A') {
					_specificTransitionTable.remove(index);
					index --;
				}
				// Si l'élement existait, on le supprimera de la base de données
				else {
					c.setFlag('S');
				}
			}
		}
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: getNewTransitionName
	 * 
	 * Description:
	 * Cette méthode retourne le nouveau nom de la transition.
	 * 
	 * Retourne : Le nouveau nom.
	 * ----------------------------------------------------------*/
	public String getNewTransitionName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getNewTransitionName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _newTransitionName;
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
				"ComponentTransition", "getNewDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _newDescription;
	}

	/*----------------------------------------------------------
	 * Nom: getNewTransitionType
	 * 
	 * Description:
	 * Cette méthode retourne le nouveau type de la transition.
	 * 
	 * Retourne : Le nouveau type.
	 * ----------------------------------------------------------*/
	public char getNewTransitionType() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getNewTransitionType");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _newTransitionType;
	}

	/*----------------------------------------------------------
	 * Nom: getNewSpecificTransition
	 * 
	 * Description:
	 * Cette méthode retourne le tableau des transitions spécifiques
	 * que l'utilisateur a spécifié via saisie.
	 * 
	 * Retourne : Le tableau de ComponentTransition saisie.
	 * ----------------------------------------------------------*/
	public ArrayList<ComponentTransition> getNewSpecificTransition() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "getNewSpecificTransition");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _newSpecificTransitionTable;
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
				"ComponentTransition", "getNewResponsabilities");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();

		return _newResponsabilities;
	}

	/*----------------------------------------------------------
	 * Nom: setNewTransitionName
	 * 
	 * Description:
	 * Cette méthode modifie le nom de la transition saisie.
	 * 
	 * Paramètre : 
	 *   - transitionName : Le nouveau nom de la transition.
	 * ----------------------------------------------------------*/
	public void setNewTransitionName(String transitionName) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setNewTransitionName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("transitionName=" + transitionName);

		_newTransitionName = transitionName;
		
		trace_methods.endOfMethod();
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
				"ComponentTransition", "setNewDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);

		_newDescription = description;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewTransitionType
	 * 
	 * Description:
	 * Cette méthode modifie le type de la transition saisie.
	 * 
	 * Paramètre : 
	 *   - transitionType : Le nouveau type de la transition.
	 * ----------------------------------------------------------*/
	public void setNewTransitionType(char transitionType) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setNewTransitionType");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("transitionType=" + transitionType);

		_newTransitionType = transitionType;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewTransitionType
	 * 
	 * Description:
	 * Cette méthode modifie le tableau des éléments spécifiques saisis
	 * par l'utilisateur.
	 * 
	 * Paramètre : 
	 *   - transitionType : Le nouveau type de la transition.
	 * ----------------------------------------------------------*/
	public void setNewSpecificTransition(
			ArrayList<ComponentTransition> specificTransitionTable) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setNewSpecificTransition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("specificTransitionTable=" + 
				specificTransitionTable);

		_newSpecificTransitionTable = specificTransitionTable;
		
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
				"ComponentTransition", "setNewResponsabilities");
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
	 * ComponentTransitionGroups. 
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
				"ComponentTransitionGroups", "loadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableTransitionGroup = "TableTransitionGroups";
		String EndStateTransitionGroup = "EndState";
		String NameTransitionGroup = "Name";
		String DescriptionTransitionGroup = "Description";
		String TypeTransitionGroup = "Type";
		String ResponsabilitiesTransitionGroup = "Responsabilities";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		
		try {
			
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux groupes de transitions.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableTransitionGroup = 
				configuration_api.getString("ComponentConfiguration.TableTransitionGroups");
			EndStateTransitionGroup = 
				configuration_api.getString("ComponentConfiguration.TableTransitionGroups.EndState");
			NameTransitionGroup = 
				configuration_api.getString("ComponentConfiguration.TableTransitionGroups.Name");
			DescriptionTransitionGroup = 
				configuration_api.getString("ComponentConfiguration.TableTransitionGroups.Description");
			TypeTransitionGroup = 
				configuration_api.getString("ComponentConfiguration.TableTransitionGroups.Type");
			ResponsabilitiesTransitionGroup = 
				configuration_api.getString("ComponentConfiguration.TableTransitionGroups.Responsabilities");
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
						
					// On construit le ComponentTransitionGroups
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameTransitionGroup))
								_transitionName = object_parameters[index2].value;
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

	/*----------------------------------------------------------
	 * Nom: saveData
	 * 
	 * Description:
	 * Cette méthode sauvegarde le modèle dans les tables. 
	 * Elle implémente la méthode de la classe ModelInterface. Après avoir
	 * récupérer les noms des champs des tables, cette méthode va construire
	 * la requête d'insertion ou de modification d'une données de la 
	 * table correspondant aux groupes de transitions. Puis via la classe
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
				"ComponentTransitionGroups", "saveData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		
		String TableTransitionGroups = "TableTransitionGroups";
		String EndStateTransitionGroup = "EndState";
		String NameTransitionGroup = "Name";
		String DescriptionTransitionGroup = "Description";
		String TypeTransitionGroup = "Type";
		String ResponsabilitiesTransitionGroup = "Responsabilities";		
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		if (_flag != 'E') {
			try {
				ConfigurationAPI configuration_api = new ConfigurationAPI();
				configuration_api.useSection("I-SIS");
				
				// On récupère l'ensemble des noms des champs de la table
				// correspondant aux groupes de transitions.
				TableTransitionGroups = 
					configuration_api.getString("ComponentConfiguration." +
							"TableTransitionGroups");
				EndStateTransitionGroup = 
					configuration_api.getString("ComponentConfiguration." +
							"TableTransitionGroups.EndState");
				NameTransitionGroup = 
					configuration_api.getString("ComponentConfiguration." +
							"TableTransitionGroups.Name");
				DescriptionTransitionGroup = 
					configuration_api.getString("ComponentConfiguration." +
							"TableTransitionGroups.Description");
				TypeTransitionGroup = 
					configuration_api.getString("ComponentConfiguration." +
							"TableTransitionGroups.Type");
				ResponsabilitiesTransitionGroup = 
					configuration_api.getString("ComponentConfiguration." +
							"TableTransitionGroups.Responsabilities");
			}
			catch(Exception exception) {
				trace_errors.writeTrace(
					"Erreur lors de la récupération de la configuration: " +
					exception);
				// Il y a eu une erreur, on continue quand même
			}
			
			String [] commandSelect;
			IsisTableDefinition table_definition;
			try {
				// On ouvre une session de service à partir du noeud sélectionné
				ServiceSessionProxy session = new ServiceSessionProxy(
						selectedNode.getServiceSession());
				
				// On sélectionne toutes les colonnes de la table
				String [] column = { "" };
				// On construit la condition en fonction de la clé de la table
				String condition = EndStateTransitionGroup + "='" + 
									_endState.getStateName() +"'";
				
				// On lance la requête pour obtenir la définition de la table
				commandSelect = session.getSelectResult(TableTransitionGroups, 
										column, condition, "", context);
				
				// On construit la définition de la table
				table_definition = TreeNodeFactory.buildDefinitionFromSelectResult(
										commandSelect, TableTransitionGroups);
				// A partir de la ligne résultat, on complète les valeurs de la 
				// requête
				String values = "";
				for (int index = 0 ; index < table_definition.columns.length ; index ++) {
					if (table_definition.columns[index].name.equals(EndStateTransitionGroup))
						values += _endState.getStateName();
					else if (table_definition.columns[index].name.equals(NameTransitionGroup)) {
						if (_flag != 'S') {
							values += _newTransitionName;
						}
						else {
							values += _transitionName;
						}
					}
					else if (table_definition.columns[index].name.equals(DescriptionTransitionGroup)) {
						if (_flag != 'S') {
							values += _newDescription;
						}
						else {
							values += _description;
						}
					}
					else if (table_definition.columns[index].name.equals(TypeTransitionGroup)) {
						if (_flag != 'S') {
							values += _newTransitionType;
						}
						else {
							values += _transitionType;
						}
					}
					else if (table_definition.columns[index].name.equals(ResponsabilitiesTransitionGroup)) {
						if (_flag != 'S') {
							values += _newResponsabilities;
						}
						else {
							values += _responsabilities;
						}
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
						TableTransitionGroups, values, AgentSessionManager.getInstance()
									.getAgentLayerMode(selectedNode.getAgentName()));
				
				// On exécute la requête
				ExecutionSurveyor surveyor = new ExecutionSurveyor();
				
				surveyor.execute(actionId, command, selectedNode, context);
				
				// On libère l'utilisation de la définition
				manager.releaseTableDefinitionLeasing(table_definition);
				
			}
			catch (InnerException exception) 
			{
				trace_errors.writeTrace(
						"Problème lors d'une requête sur la table " + 
						TableTransitionGroups + " !");
				// C'est une erreur, on la signale
				throw new InnerException("&ERR_Query", null, null);
			}
			
			// Une fois la sauvegarde du groupes de transitions effectués, 
			// on appelle la méthode de sauvegarde pour les transitions spécifiques
			try {
				if (_specificTransitionTable != null && _flag == 'S')
					for (int index=0 ; index < _specificTransitionTable.size() ; index++) {
						_specificTransitionTable.get(index).saveData(
							actionId, context, selectedNode, 
							_endState.getStateName());
				}
				
				if (_newSpecificTransitionTable != null && (_flag == 'A' || _flag == 'M'))
					for (int index=0 ; index < _newSpecificTransitionTable.size() ; index++) {
						_newSpecificTransitionTable.get(index).saveData(
							actionId, context, selectedNode, 
							_endState.getStateName());
				}
				
			}
			catch (InnerException e) 
			{
				trace_errors.writeTrace(
						"Problème lors d'une requête sur la sous table des " +
						"transitions !");
				// C'est une erreur, on la signale
				throw new InnerException("&ERR_Query", null, null);
			}
		}
		_flag = 'E';
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
				"ComponentTransition", "end");
		trace_methods.beginningOfMethod();
		
		_endState = null;
		_transitionName = null;
		_description = null;
		_transitionType = 0;
		
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
		
		String NameGroup = MessageManager.getMessage(
				"&ComponentConfiguration_Name"); 
		String DescGroup = MessageManager.getMessage(
				"&ComponentConfiguration_Description");
		String EndStateGroup = MessageManager.getMessage(
				"&ComponentConfiguration_EndState");
		String RespGroup = MessageManager.getMessage(
				"&ComponentConfiguration_Responsabilities");
		String TypeGroup = MessageManager.getMessage(
				"&ComponentConfiguration_TransitionType");

		char traT = ' ';
		String resp = "";
		String desc = "";
		String traN = "";
		
		if( _flag == 'E' || _flag == 'S' ) {
			traT = _transitionType;
			resp = _responsabilities;
			desc = _description;
			traN = _transitionName;
		} else {
			traT = _newTransitionType;
			resp = _newResponsabilities;
			desc = _newDescription;
			traN = _newTransitionName;
		}
		
		document.insertString(offset,"---------------------------" +
				"------------------------------" +
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));

		ArrayList<ComponentTransition> tableToElementsToAdd = new ArrayList<ComponentTransition>();
		ArrayList<ComponentTransition> tableToElementsToDel = new ArrayList<ComponentTransition>();
		ArrayList<ComponentTransition> tableToElementsToUpd = new ArrayList<ComponentTransition>();
		
		// Affichage des transitions spécifiques
		if( (_flag == 'E' || _flag == 'S') && ( _specificTransitionTable != null ) ) {
			for (int index = 0 ; index < _specificTransitionTable.size() ; index ++) {
				ComponentTransition c = _specificTransitionTable.get(index);
				if (c.getFlag() == 'A')
					tableToElementsToAdd.add(c);
				else if (c.getFlag() == 'M')
					tableToElementsToUpd.add(c);
				else if (c.getFlag() == 'S')
					tableToElementsToDel.add(c);
			}
		}else if( (_flag == 'A' || _flag == 'M') && ( _newSpecificTransitionTable != null ) ) {
			for (int index = 0 ; index < _newSpecificTransitionTable.size() ; index ++) {
				ComponentTransition c = _newSpecificTransitionTable.get(index);
				if (c.getFlag() == 'A')
					tableToElementsToAdd.add(c);
				else if (c.getFlag() == 'M')
					tableToElementsToUpd.add(c);
				else if (c.getFlag() == 'S')
					tableToElementsToDel.add(c);
			}
		}
	
		for (int index =  0 ; index < tableToElementsToDel.size() ; index ++) {
			tableToElementsToDel.get(index).display(document, offset);							
		}
		document.insertString(offset, 
				"A supprimer : " + System.getProperty("line.separator", "\n"),
				document.getStyle("italic"));

		for (int index =  0 ; index < tableToElementsToUpd.size() ; index ++) {
			tableToElementsToUpd.get(index).display(document, offset);							
		}
		document.insertString(offset, 
				"A modifier : " + System.getProperty("line.separator", "\n"),
				document.getStyle("italic"));
		
		for (int index =  0 ; index < tableToElementsToAdd.size() ; index ++) {
			tableToElementsToAdd.get(index).display(document, offset);							
		}	
		document.insertString(offset, 
				"A ajouter : " + System.getProperty("line.separator", "\n"),
				document.getStyle("italic"));
		
		document.insertString(offset, "---- Liste des transitions " +
				"spécifiques ----" +  
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		
		// Une fois les transitions affichées, on affiche les informations 
		// du groupe.
		document.insertString(offset, TypeGroup + " : " + traT + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, RespGroup + " : " + resp + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, DescGroup + " : " +
				desc + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, EndStateGroup + " : " + 
				_endState.getStateName() + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, NameGroup + " : " + traN + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément 
	 * ComponentTransitionGroups.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		ArrayList<ComponentTransition> transitionClone = new ArrayList<ComponentTransition>();
		for (int index = 0 ; index < _specificTransitionTable.size() ; index ++) {
			transitionClone.add(_specificTransitionTable.get(index).clone());
		}
		
		ArrayList<ComponentTransition> newTransitionClone = new ArrayList<ComponentTransition>();
		for (int index = 0 ; index < _newSpecificTransitionTable.size() ; index ++) {
			transitionClone.add(_newSpecificTransitionTable.get(index).clone());
		}
		
		ComponentTransitionGroups clone = new ComponentTransitionGroups();
		clone.setEndState(_endState);
		clone.setDescription(_description);
		clone.setTransitionName(_transitionName);
		clone.setResponsabilities(_responsabilities);
		clone.setTransitionType(_transitionType);
		clone.setSpecificTransition(transitionClone);
		
		clone.setNewDescription(_newDescription);
		clone.setNewTransitionName(_newTransitionName);
		clone.setNewResponsabilities(_newResponsabilities);
		clone.setNewTransitionType(_newTransitionType);
		clone.setNewSpecificTransition(newTransitionClone);
		
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _endState
	 *
	 * Description:
	 * Cet attribut maintient une reference sur l'état d'arrivée 
	 * de la transition.
	 * ----------------------------------------------------------*/
	private ComponentStates _endState;
	
	/*----------------------------------------------------------
	 * Nom: _transitionName
	 *
	 * Description:
 	 * Cet attribut maintient le nom correspondant à une transition
	 * du composant.
	 * ----------------------------------------------------------*/
	private String _transitionName;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 *
	 * Description:
	 * Cet attribut maintient une description sur la transition.
	 * ----------------------------------------------------------*/
	private String _description;
	
	/*----------------------------------------------------------
	 * Nom: _transitionType
	 *
	 * Description:
	 * Cet attribut maintient le niveau fonctionnel de la 
	 * transition.
	 * ----------------------------------------------------------*/
	private char _transitionType;
	
	/*----------------------------------------------------------
	 * Nom: _specificTransitionTable
	 *
	 * Description:
	 * Cet attribut maintient un tableau sur les informations 
	 * spécifiques des transition vers l'état cible actuel.
	 * ----------------------------------------------------------*/
	private ArrayList<ComponentTransition> _specificTransitionTable;

	/*----------------------------------------------------------
	 * Nom : Responsabilities
	 * 
	 * Description :
	 * Ce champ correspond à une liste de responsabilités, 
	 * dont chaque élément est séparé des autres par une virgule.
	 * ----------------------------------------------------------*/
	private String _responsabilities;
	
	/*----------------------------------------------------------
	 * Nom: _transitionName
	 *
	 * Description:
 	 * Cet attribut est le nouveau nom correspondant à une transition
	 * du composant.
	 * ----------------------------------------------------------*/
	private String _newTransitionName;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 *
	 * Description:
	 * Cet attribut est la nouvelle description de la transition.
	 * ----------------------------------------------------------*/
	private String _newDescription;
	
	/*----------------------------------------------------------
	 * Nom: _transitionType
	 *
	 * Description:
	 * Cet attribut est le nouveau niveau fonctionnel de la 
	 * transition.
	 * ----------------------------------------------------------*/
	private char _newTransitionType;
	
	/*----------------------------------------------------------
	 * Nom: _specificTransitionTable
	 *
	 * Description:
	 * Cet attribut maintient un tableau sur les nouvelles informations 
	 * spécifiques des transition vers l'état cible actuel.
	 * ----------------------------------------------------------*/
	private ArrayList<ComponentTransition> _newSpecificTransitionTable;

	/*----------------------------------------------------------
	 * Nom : Responsabilities
	 * 
	 * Description :
	 * Ce champ correspond à la nouvelle liste de responsabilités, 
	 * dont chaque élément est séparé des autres par une virgule.
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
