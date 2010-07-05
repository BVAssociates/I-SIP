/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentVariable.java,v $
* $Revision: 1.13 $
*
* ------------------------------------------------------------
* DESCRIPTION: D�finition des variables d'un composant
* DATE:        06/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      impl.processor.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.implementation.model;

//
//Imports syst�me
//

import javax.swing.text.StyledDocument;

//
// Imports du Projet
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
 * Nom: ComponentVariable
 * 
 * Description:
 * Cette classe mod�lise une variable associ�e � un composant I-SIS. 
 * Elle se caract�rise par un nom et une description. Elle est 
 * utilis�e lors du processus de cr�ation ou de modification 
 * d'un composant dans la console.
 * ----------------------------------------------------------*/
public class ComponentVariable implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentVariable
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public ComponentVariable() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "ComponentVariable");
		trace_methods.beginningOfMethod();
		
		_variableName = "";
		_description = "";
		_defaultValue = "";
		_flag = ' ';
		
		_newDescription = "";
		_newDefaultValue = "";
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: getVariableName
	 * 
	 * Description:
	 * Cette m�thode retourne le nom associ� a la variable.
	 * 
	 * Retourne: Une chaine de caractere : Le nom de la variable.
	 * ----------------------------------------------------------*/
	public String getVariableName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getVariableName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _variableName;
	}
	
	/*----------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette m�thode retourne la chaine de caracteres 'description'
	 * associ� au composant.
	 * 
	 * Retourne: Une chaine de caractere : La description.
	 * ----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
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
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _flag;
	}
	
	/*----------------------------------------------------------
	 * Nom: setVariableName
	 * 
	 * Description:
	 * Cette m�thode modifie le nom de la variable en lui donnant 
	 * celui passe en parametre.
	 * 
	 * Arguments:
	 * - name: Le nouveau nom de la variable
 	 * ----------------------------------------------------------*/
	public void setVariableName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentTransition", "setVariableName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
		
		_variableName = name;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setDescription
	 * 
	 * Description:
	 * Cette m�thode modifie la description du composant en lui 
	 * donnant celle passee en parametre.
	 * 
	 * Arguments:
	 * - description: La nouvelle description du composant
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
	 * Nom: getDefaultValue
	 * 
	 * Description:
	 * Cette m�thode retourne la valeur par d�faut '_defaultValue'
	 * associ� au composant.
	 * 
	 * Retourne: Une chaine de caractere : La valeur par d�faut.
	 * ----------------------------------------------------------*/
	public String getDefaultValue() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getDefaultValue");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _defaultValue;
	}
	
	/*----------------------------------------------------------
	 * Nom: setDefaultValue
	 * 
	 * Description:
	 * Cette m�thode modifie la valeur par d�faut du composant en lui 
	 * donnant celle passee en parametre.
	 * 
	 * Arguments:
	 *  - value: La nouvelle valeur par d�faut du composant
	 * ----------------------------------------------------------*/
	public void setDefaultValue(String value) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("value=" + value);
		
		_defaultValue = value;
		
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
				"ComponentVariable", "setFlag");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("flag=" + flag);
		
		_flag = flag;
		
		trace_methods.endOfMethod();
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
				"ComponentVariable", "getNewDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newDescription;
	}

	/*----------------------------------------------------------
	 * Nom: getNewDefaultValue
	 * 
	 * Description:
	 * Cette m�thode retourne la nouvelle valeur par d�faut.
	 * 
	 * Retourne : La nouvelle valeur par d�faut.
	 * ----------------------------------------------------------*/
	public String getNewDefaultValue() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "getNewDefaultValue");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newDefaultValue;
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
				"ComponentVariable", "setNewDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("description=" + description);
		
		_newDescription = description;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewDefaultValue
	 * 
	 * Description:
	 * Cette m�thode modifie la nouvelle valeur par d�faut.
	 * 
	 * Param�tre : 
	 *   - defaultValue : La nouvelle valeur par d�faut.
	 * ----------------------------------------------------------*/
	public void setNewDefaultValue(String defaultValue) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "setNewDefaultValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("defaultValue=" + defaultValue);
		
		_newDefaultValue = defaultValue;
		
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
	 * A partir du r�sultat, elle va compl�ter les champs du ComponentVariable. 
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
				"ComponentVariable", "loadData");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableVariables = "TableVariables";
		String NameVar = "Name";
		String DescVar = "Description";
		String DefaultValueVar = "DefaultValue";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		try {
			// On r�cup�re l'ensemble des noms des champs de la table
			// correspondant aux variables.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableVariables = configuration_api.getString("ComponentConfiguration." +
					"TableVariables");
			NameVar = configuration_api.getString("ComponentConfiguration." +
					"TableVariables.Name");
			DescVar = configuration_api.getString("ComponentConfiguration." +
					"TableVariables.Description");
			DefaultValueVar = configuration_api.getString("ComponentConfiguration." +
					"TableVariables.DefaultValue");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand m�me
		}
				
		// On s�lectionne les colonnes de la table qui nous interressent
		String [] column = new String[3];
		column[0] = NameVar;
		column[1] = DescVar;
		column[2] = DefaultValueVar;
			
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
			String [] result = session.getSelectResult(TableVariables, 
					column, condition, "", context);
			
			if (result != null) {
				// On construit la d�finition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableVariables);
				
				// Pour chaque ligne r�sultat de la requ�te Select
				for (int index = 1 ; index < result.length ; index++) {
					// On r�cup�re les donn�es
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On construit le ComponentVariable
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameVar))
								_variableName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DescVar))
								_description = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DefaultValueVar))
								_defaultValue = object_parameters[index2].value;
							else {
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
				"Probl�me lors du chargement des donn�es : " +
				exception.getMessage());
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_LoadData", null, exception);
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
	 * table correspondant aux variables. Puis via la classe
	 * ExecutionSurveyor, cette requ�te sera ex�cut�e. 
	 * 
	 * Param�tres :
	 *   - actionId : L'identifiant unique d'action
	 *   - context : Cette r�f�rence correspond � l'ensemble des param�tres
	 * 				"exportables" des noeuds travers�s pour atteindre le 
	 * 				noeud concern�. A cet ensemble sont ajout�s tous les 
	 * 				param�tres du noeud lui-m�me.
	 *   - seletedNode : Une r�f�rence sur le noeud s�lectionn�.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	 * sinon.
	 * ----------------------------------------------------------*/
	public boolean saveData(
		String actionId,
		IndexedList context, 
		GenericTreeObjectNode selectedNode
		) 
		throws 
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentVariable", "saveData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		
		String TableVariables = "TableVariables";
		String NameVariable = "Name";
		String DescVariable = "Description";
		String DefaultValueVariable = "DefaultValue";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		if (_flag != 'E') {
		
			try {
				ConfigurationAPI configuration_api = new ConfigurationAPI();
				configuration_api.useSection("I-SIS");
				
				// On r�cup�re l'ensemble des noms des champs de la table
				// correspondant aux variables.
				TableVariables = configuration_api.getString(
						"ComponentConfiguration.TableVariables");
				NameVariable = configuration_api.getString(
						"ComponentConfiguration.TableVariables.Name");
				DescVariable = configuration_api.getString(
						"ComponentConfiguration.TableVariables.Description");
				DefaultValueVariable = configuration_api.getString(
						"ComponentConfiguration.TableVariables.DefaultValue");
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
			String condition = NameVariable + "='" + _variableName +"'";
			
			// On lance la requ�te pour obtenir la d�finition de la table
			String [] commandSelect = session.getSelectResult(TableVariables, 
					column, condition, "", context);
			
			// On construit la d�finition de la table
			IsisTableDefinition table_definition = 
				TreeNodeFactory.buildDefinitionFromSelectResult(
						commandSelect, TableVariables);
			
			// A partir de la ligne r�sultat, on compl�te les valeurs de la 
			// requ�te
			String values = "";
			for (int index = 0 ; index < table_definition.columns.length ; index ++) {
				if (table_definition.columns[index].name.equals(NameVariable))
					values += _variableName;
				else if (table_definition.columns[index].name.equals(DescVariable)) {
					if (_flag != 'S')
						values += _newDescription;
					else
						values += _description;
				}
				else if (table_definition.columns[index].name.equals(DefaultValueVariable)) {
					if (_flag != 'S')
						values += _newDefaultValue;
					else
						values += _defaultValue;
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
					TableVariables, values, AgentSessionManager.getInstance()
								.getAgentLayerMode(selectedNode.getAgentName()));
	
			// On ex�cute la requ�te
			ExecutionSurveyor surveyor = new ExecutionSurveyor();
			
			surveyor.execute(actionId, command, selectedNode, context);
			
			// On lib�re l'utilisation de la d�finition
			manager.releaseTableDefinitionLeasing(table_definition);
			_flag = 'E';
		}
		catch (InnerException exception) 
		{
			trace_errors.writeTrace(
			"Probl�me lors d'une requ�te sur la table " + TableVariables + " !");
			// C'est une erreur, on la signale
			throw new InnerException("&ERR_Query", null, null);
		}
		}
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
				"ComponentVariable", "end");
		trace_methods.beginningOfMethod();
		
		_variableName = null;
		_description = null;
		
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
		
		String NameVar = MessageManager.getMessage(
				"&ComponentConfiguration_Name"); 
		String DescVar = MessageManager.getMessage(
				"&ComponentConfiguration_Description");
		String DefValVar = MessageManager.getMessage(
				"&ComponentConfiguration_DefaultValue");
		
		String defV = "";
		String desc = "";
		
		if (_flag == 'E' || _flag == 'S') {
			defV = _defaultValue;
			desc = _description;
		} else {
			defV = _newDefaultValue;
			desc = _newDescription;
		}
		
		document.insertString(offset,  
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		document.insertString(offset, DefValVar + " : " +
				defV + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, DescVar + " : " +
				desc + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, NameVar + " : " + _variableName + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette m�thode permet de retourner une copie de l'�l�ment ComponentVariable.
	 * 
	 * Retourne :le clone de l'�l�ment du mod�le sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		ComponentVariable clone = new ComponentVariable();
		clone.setVariableName(_variableName);
		clone.setDescription(_description);
		clone.setDefaultValue(_defaultValue);
		
		clone.setNewDescription(_newDescription);
		clone.setNewDefaultValue(_newDefaultValue);
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _variableName
	 * 
	 * Description:
	 * Cet attribut maintient le nom correspondant � une variable
	 * du composant
	 * ----------------------------------------------------------*/
	private String _variableName;

	/*----------------------------------------------------------
	 * Nom: _description
	 * 
	 * Description:
	 * Cet attribut maintient une description sur le composant
	 * ----------------------------------------------------------*/
	private String _description;
	
	/*----------------------------------------------------------
	 * Nom: _defaultValue
	 * 
	 * Description:
	 * Cet attribut repr�sente, pour la variable,  une valeur par d�faut sous 
	 * forme d'une cha�ne de caract�res.
	 * ----------------------------------------------------------*/
	private String _defaultValue;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 * 
	 * Description:
	 * Cet attribut est la nouvelle description du composant saisie par 
	 * l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newDescription;
	
	/*----------------------------------------------------------
	 * Nom: _defaultValue
	 * 
	 * Description:
	 * Cet attribut est la nouvelle valeur par d�faut.
	 * ----------------------------------------------------------*/
	private String _newDefaultValue;
	
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
