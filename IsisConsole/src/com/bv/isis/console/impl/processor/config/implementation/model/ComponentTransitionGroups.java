/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentTransitionGroups.java,v $
* $Revision: 1.15 $
*
* ------------------------------------------------------------
* DESCRIPTION: D�finition de transation vers un �tat d'arriv�e
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
* Ajout de la m�thode Clone()
*
* Revision 1.13  2008/08/25 10:55:52  tz
* Ajout de l'argument actionId � saveData().
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
* Ajout de la m�thode getSpecificTransition()
*
* Revision 1.2  2008/06/19 15:27:32  fcd
* Ajout du champ _responsabilities et de ses accesseurs
* 
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.implementation.model;

//
//Imports syst�me
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
 * Cette classe mod�lise un ensemble de transitions entre deux �tats 
 * d'un composant I-SIS. Elle se caract�rise par un �tat d'arriv�e, 
 * un nom, une description et un changement de niveau fonctionnel. 
 * A l'aide du tableau de ComponentTransition, on sp�cifie chaque
 * transition du groupe. 
 * Cette classe est utilis�e lors du processus de cr�ation ou de 
 * modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class ComponentTransitionGroups implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentTransitionGroups
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
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
	 * Cette m�thode retourne l'�tat d'arriv�e de la transition.
	 * 
	 * Retourne: Un ComponentStates : L'�tat d'arriv�e.
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
	 * Cette m�thode retourne le nom de la transition.
	 * 
	 * Retourne: Une cha�ne de caracteres : Le nom de la transition.
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
	 * Cette m�thode retourne la chaine de caracteres 'description'
	 * associ� a la transition.
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
	 * Cette m�thode retourne le caractere 'type' associe a la 
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
	 * Cette m�thode retourne un tableau de ComponentTransition 
	 * correspondant � la liste des transition sp�cifiques pour ce 
	 * groupe.
	 * 
	 * Retourne: La liste des transition sp�cifiques.
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
	 * Cette m�thode retourne la liste des responsabilit�s associe a la 
	 * transition
	 * 
	 * Retourne: La liste des responsabilit�s.
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
	 * Cette m�thode retourne l'�tat courant.
	 * 
	 * Retourne: Un caract�re, l'�tat courant.
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
	 * Cette m�thode modifie l'�tat d'arriv�e de la transition.
	 * 
	 * Arguments: 
	 *  - endState: Le nouvel �tat d'arriv�e.
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
	 * Cette m�thode modifie le nom de la transition.
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
	 * Cette m�thode modifie la description de la transition en lui 
	 * donnant celle pass�e en parametre.
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
	 * Cette m�thode modifie le type de la transition en lui 
	 * donnant celui pass� en parametre.
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
	 * Cette m�thode modifie le tableau de ComponentTransition 
	 * correspondant � la liste des transition sp�cifiques pour ce 
	 * groupe par celui pass� en param�tre.
	 * 
	 * Arguments: 
	 *  - transitionArray : La nouvelle liste des transition sp�cifiques.
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
	 * Cette m�thode modifie la liste des responsabilit�s par celle 
	 * pass�e en param�tre.
	 * 
	 * Arguments: 
	 *  - resp : la nouvelle liste des responsabilit�s.
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
	 * Cette m�thode modifie l'�tat courant avec celui pass� en entr�e.
	 * Elle modifie �galement les �tats des transitions sp�cifiques si on
	 * demande la suppression du groupe courant. Chaque transition sp�cifique 
	 * sera d�truite.
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
		
		// On positionne le flag du groupe
		_flag = flag;
		
		// Pour chaque transitions, on pose le flag � 'S' si celui du groupe vaut 'S'
		if (flag == 'S' && _specificTransitionTable != null) {
			for(int index = 0; index < _specificTransitionTable.size();index++) {
				ComponentTransition c = _specificTransitionTable.get(index);
				// En cas d'un �l�ment qui venait d'�tre cr�er, on le supprime
				if (c.getFlag() == 'A') {
					_specificTransitionTable.remove(index);
					index --;
				}
				// Si l'�lement existait, on le supprimera de la base de donn�es
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
	 * Cette m�thode retourne le nouveau nom de la transition.
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
	 * Cette m�thode retourne la nouvelle description saisie.
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
	 * Cette m�thode retourne le nouveau type de la transition.
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
	 * Cette m�thode retourne le tableau des transitions sp�cifiques
	 * que l'utilisateur a sp�cifi� via saisie.
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
	 * Cette m�thode retourne la nouvelle liste des responsabilit�s
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
	 * Cette m�thode modifie le nom de la transition saisie.
	 * 
	 * Param�tre : 
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
	 * Cette m�thode modifie la nouvelle description.
	 * 
	 * Param�tre : 
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
	 * Cette m�thode modifie le type de la transition saisie.
	 * 
	 * Param�tre : 
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
	 * Cette m�thode modifie le tableau des �l�ments sp�cifiques saisis
	 * par l'utilisateur.
	 * 
	 * Param�tre : 
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
	 * Cette m�thode modifie la nouvelle liste des responsabilit�s.
	 * 
	 * Param�tre : 
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
	 * Cette m�thode charge le mod�le de donn�es � partir de la base 
	 * de donn�es. Elle red�finit la m�thode LoadData de l'interface 
	 * ModelInterface. Apr�s avoir r�cup�rer les noms de la table et des 
	 * champs, cette m�thode va effectuer une requ�te de s�lection sur la
	 * table avec comme condition, les champs de la cl� pass�s en param�tre.
	 * A partir du r�sultat, elle va compl�ter les champs du 
	 * ComponentTransitionGroups. 
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
			
			// On r�cup�re l'ensemble des noms des champs de la table
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
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand m�me
		}
		
		// On s�lectionne les colonnes de la table qui nous interressent
		String [] column = new String[5];
		column[0] = EndStateTransitionGroup;
		column[1] = NameTransitionGroup;
		column[2] = DescriptionTransitionGroup;
		column[3] = TypeTransitionGroup;
		column[4] = ResponsabilitiesTransitionGroup;
			
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
			String [] result = session.getSelectResult(TableTransitionGroup, 
					column, condition, "", context);
		
			if (result != null) {
				// On construit la d�finition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
							result, TableTransitionGroup);
				// Pour chaque ligne r�sultat de la requ�te Select
				for (int index = 1 ; index < result.length ; index++) {
					// On r�cup�re les donn�es
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
								// Une des colonnes retourn�es n'est pas bonne, 
								// on signale l'erreur
								trace_errors.writeTrace( "Probl�me : " + 
									object_parameters[index2] + "innatendu !");
							}
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
	}

	/*----------------------------------------------------------
	 * Nom: saveData
	 * 
	 * Description:
	 * Cette m�thode sauvegarde le mod�le dans les tables. 
	 * Elle impl�mente la m�thode de la classe ModelInterface. Apr�s avoir
	 * r�cup�rer les noms des champs des tables, cette m�thode va construire
	 * la requ�te d'insertion ou de modification d'une donn�es de la 
	 * table correspondant aux groupes de transitions. Puis via la classe
	 * ExecutionSurveyor, cette requ�te sera ex�cut�e. 
	 * 
	 * Param�tres :
	 *   - actionId : L'identifiant unique de l'action
	 *   - context : Cette r�f�rence correspond � l'ensemble des param�tres
	 * 				"exportables" des noeuds travers�s pour atteindre le 
	 * 				noeud concern�. A cet ensemble sont ajout�s tous les 
	 * 				param�tres du noeud lui-m�me.
	 *   - seletedNode : Une r�f�rence sur le noeud s�lectionn�.
	 * 
	 * Retourne: vrai (true) si tout c'est bien pass�, faux (false) 
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
				
				// On r�cup�re l'ensemble des noms des champs de la table
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
					"Erreur lors de la r�cup�ration de la configuration: " +
					exception);
				// Il y a eu une erreur, on continue quand m�me
			}
			
			String [] commandSelect;
			IsisTableDefinition table_definition;
			try {
				// On ouvre une session de service � partir du noeud s�lectionn�
				ServiceSessionProxy session = new ServiceSessionProxy(
						selectedNode.getServiceSession());
				
				// On s�lectionne toutes les colonnes de la table
				String [] column = { "" };
				// On construit la condition en fonction de la cl� de la table
				String condition = EndStateTransitionGroup + "='" + 
									_endState.getStateName() +"'";
				
				// On lance la requ�te pour obtenir la d�finition de la table
				commandSelect = session.getSelectResult(TableTransitionGroups, 
										column, condition, "", context);
				
				// On construit la d�finition de la table
				table_definition = TreeNodeFactory.buildDefinitionFromSelectResult(
										commandSelect, TableTransitionGroups);
				// A partir de la ligne r�sultat, on compl�te les valeurs de la 
				// requ�te
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
						trace_errors.writeTrace("Probl�me lors de la cr�ation des " +
								"valeurs � sauvegard�es : " + 
								table_definition.columns[index].name + "innatendu !");
						
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
						TableTransitionGroups, values, AgentSessionManager.getInstance()
									.getAgentLayerMode(selectedNode.getAgentName()));
				
				// On ex�cute la requ�te
				ExecutionSurveyor surveyor = new ExecutionSurveyor();
				
				surveyor.execute(actionId, command, selectedNode, context);
				
				// On lib�re l'utilisation de la d�finition
				manager.releaseTableDefinitionLeasing(table_definition);
				
			}
			catch (InnerException exception) 
			{
				trace_errors.writeTrace(
						"Probl�me lors d'une requ�te sur la table " + 
						TableTransitionGroups + " !");
				// C'est une erreur, on la signale
				throw new InnerException("&ERR_Query", null, null);
			}
			
			// Une fois la sauvegarde du groupes de transitions effectu�s, 
			// on appelle la m�thode de sauvegarde pour les transitions sp�cifiques
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
						"Probl�me lors d'une requ�te sur la sous table des " +
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
	 * Cette m�thode impl�mente la m�thode de l'interface ModelInterface.
	 * Cette m�thode est appel�e lors de la destruction de l'assistant. Elle est
	 * utiliser pour lib�rer l'espace m�moire utilis� par les variables des
	 * classes.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
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
		
		// Affichage des transitions sp�cifiques
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
				"sp�cifiques ----" +  
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		
		// Une fois les transitions affich�es, on affiche les informations 
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
	 * Cette m�thode permet de retourner une copie de l'�l�ment 
	 * ComponentTransitionGroups.
	 * 
	 * Retourne :le clone de l'�l�ment du mod�le sous le type ModelInterface.
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
	 * Cet attribut maintient une reference sur l'�tat d'arriv�e 
	 * de la transition.
	 * ----------------------------------------------------------*/
	private ComponentStates _endState;
	
	/*----------------------------------------------------------
	 * Nom: _transitionName
	 *
	 * Description:
 	 * Cet attribut maintient le nom correspondant � une transition
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
	 * sp�cifiques des transition vers l'�tat cible actuel.
	 * ----------------------------------------------------------*/
	private ArrayList<ComponentTransition> _specificTransitionTable;

	/*----------------------------------------------------------
	 * Nom : Responsabilities
	 * 
	 * Description :
	 * Ce champ correspond � une liste de responsabilit�s, 
	 * dont chaque �l�ment est s�par� des autres par une virgule.
	 * ----------------------------------------------------------*/
	private String _responsabilities;
	
	/*----------------------------------------------------------
	 * Nom: _transitionName
	 *
	 * Description:
 	 * Cet attribut est le nouveau nom correspondant � une transition
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
	 * sp�cifiques des transition vers l'�tat cible actuel.
	 * ----------------------------------------------------------*/
	private ArrayList<ComponentTransition> _newSpecificTransitionTable;

	/*----------------------------------------------------------
	 * Nom : Responsabilities
	 * 
	 * Description :
	 * Ce champ correspond � la nouvelle liste de responsabilit�s, 
	 * dont chaque �l�ment est s�par� des autres par une virgule.
	 * ----------------------------------------------------------*/
	private String _newResponsabilities;
	
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
