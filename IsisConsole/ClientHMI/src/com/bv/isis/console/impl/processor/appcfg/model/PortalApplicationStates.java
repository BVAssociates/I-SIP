/*------------------------------------------------------------
 * Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
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
// D�claration du package
package com.bv.isis.console.impl.processor.appcfg.model;

//
// Imports syst�me
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
 * Cette classe repr�sente un �tat d'une application.
 * Cette classe conserve en m�moire un nom pour l'�tat,
 * une desription et une liste d'�tats de services.
 * 
 * Elle est utilis�e lors du processus de cr�ation ou de 
 * modification d'une application dans la console.
 * ----------------------------------------------------------*/
public class PortalApplicationStates implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: PortalApplicationStates
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
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
	public void display(StyledDocument styledDocument, int offset)
			throws Exception {

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
	 * PortalApplicationStates. 
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
			
			// On r�cup�re l'ensemble des noms des champs de la table
			// correspondant aux �tats de l'application.
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
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand m�me
		}
		
		// On s�lectionne les colonnes de la table qui nous interressent
		String [] column = new String[2];
		column[0] = NameState;
		column[1] = DescState;
			
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
			String [] result = session.getSelectResult(TableStates, 
					column, condition, "", context);
		
			if (result != null) {
				// On construit la d�finition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
							result, TableStates);
				// Pour chaque ligne r�sultat de la requ�te Select
				for (int index = 1 ; index < result.length ; index++) {
					// On r�cup�re les donn�es
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
	 * Nom: getStateName
	 * 
	 * Description:
	 * Cette m�thode retourne le nom associ� � l'�tat
	 * 
	 * Retourne: Une chaine de caractere : Le nom d� l'�tat.
	 * ----------------------------------------------------------*/
	public String getStateName() {
		return _stateName;
	}

	/*----------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette m�thode retourne le nom associ� � la description 
	 * de l'�tat
	 * 
	 * Retourne: Une chaine de caractere : La description de l'�tat.
	 * ----------------------------------------------------------*/
	public String getDescription() {
		return _description;
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
		return _flag;
	}

	/*----------------------------------------------------------
	 * Nom: getSpecificStates
	 * 
	 * Description:
	 * Cette m�thode retourne la liste des �tats des services associ�e
	 * � l'�tat de l'application
	 * 
	 * Retourne: Un tableau : la liste des �tats des services.
	 * ----------------------------------------------------------*/
	public ArrayList<PortalApplicationServicesSpecificStates> getSpecificStates() {
		return _specificStates;
	}
	
	/*----------------------------------------------------------
	 * Nom: setStateName
	 * 
	 * Description:
	 * Cette m�thode modifie le nom de l'�tat en lui donnant 
	 * celui pass� en parametre.
	 * 
	 * Arguments:
	 * - name: Le nouveau nom de l'�tat
	 * ----------------------------------------------------------*/
	public void setStateName(String name) {
		_stateName = name;
	}

	/*----------------------------------------------------------
	 * Nom: setDescription
	 * 
	 * Description:
	 * Cette m�thode modifie la description de l'�tat en lui donnant 
	 * celui pass� en parametre.
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
	 * Cette m�thode modifie l'�tat courant avec celui pass� en entr�e.
	 * 
	 * Param�tre : 
	 *   - flag : Le nouvel �tat courant.
	 * ----------------------------------------------------------*/
	public void setFlag(char flag) {
		_flag = flag;
	}

	/*----------------------------------------------------------
	 * Nom: setSpecificStates
	 * 
	 * Description:
	 * Cette m�thode modifie la liste des �tats des services caract�risant
	 * l'�tat de l'application courant.
	 * 
	 * Param�tre : 
	 *   - states : la nouvelle liste d'�tats de services.
	 * ----------------------------------------------------------*/
	public void setSpecificStates(
			ArrayList<PortalApplicationServicesSpecificStates> states) {
		_specificStates = states;
	}
	
	/*----------------------------------------------------------
	 * Nom: getNewDescription
	 * 
	 * Description:
	 * Cette m�thode retourne le nom associ� � la nouvelle description 
	 * de l'�tat saisie par l'utilisateur
	 * 
	 * Retourne: Une chaine de caractere : La description de l'�tat.
	 * ----------------------------------------------------------*/
	public String getNewDescription() {
		return _newDescription;
	}
	
	/*----------------------------------------------------------
	 * Nom: setNewDescription
	 * 
	 * Description:
	 * Cette m�thode modifie la nouvelle description de l'�tat en lui donnant 
	 * celle pass� en parametre.
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
	 * Cette m�thode retourne la nouvelle liste des �tats des services associ�e
	 * � l'�tat de l'application
	 * 
	 * Retourne: Un tableau : la liste des �tats des services.
	 * ----------------------------------------------------------*/
	public ArrayList<PortalApplicationServicesSpecificStates> getNewSpecificStates() {
		return _newSpecificStates;
	}
	
	/*----------------------------------------------------------
	 * Nom: setNewSpecificStates
	 * 
	 * Description:
	 * Cette m�thode modifie la nouvelle liste des �tats des services 
	 * caract�risant l'�tat de l'application courant.
	 * 
	 * Param�tre : 
	 *   - states : la nouvelle liste d'�tats de services.
	 * ----------------------------------------------------------*/
	public void setNewSpecificStates(
			ArrayList<PortalApplicationServicesSpecificStates> states) {
		_newSpecificStates = states;
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
	 * Cet attribut maintient une r�f�rence sur le nom de l'�tat
	 * ----------------------------------------------------------*/
	private String _stateName;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 * 
	 * Description: 
	 * Cet attribut maintient une r�f�rence sur la description de l'�tat
	 * ----------------------------------------------------------*/
	private String _description;

	/*----------------------------------------------------------
	 * Nom: _flag
	 * 
	 * Description: 
	 * Cet attribut maintient une r�f�rence sur l'�tat courant de l'�tat
	 * ----------------------------------------------------------*/
	private char _flag;
	
	/*----------------------------------------------------------
	 * Nom: _specificStates
	 * 
	 * Description: 
	 * Cet attribut maintient une r�f�rence sur la liste des �tats de services
	 * de l'�tat d'application
	 * ----------------------------------------------------------*/
	private ArrayList<PortalApplicationServicesSpecificStates> _specificStates;
	
	/*----------------------------------------------------------
	 * Nom: _newDescription
	 * 
	 * Description: 
	 * Cet attribut maintient une r�f�rence sur la nouvelle description de l'�tat
	 * ----------------------------------------------------------*/
	private String _newDescription;
	
	/*----------------------------------------------------------
	 * Nom: _newSpecificStates
	 * 
	 * Description: 
	 * Cet attribut maintient une r�f�rence sur la nouvelle liste des �tats 
	 * de services de l'�tat d'application
	 * ----------------------------------------------------------*/
	private ArrayList<PortalApplicationServicesSpecificStates> _newSpecificStates;
	
}
