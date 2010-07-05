/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/implementation/model/ComponentConfig.java,v $
* $Revision: 1.11 $
*
* ------------------------------------------------------------
* DESCRIPTION: D�finition des fichiers de configuration
* DATE:        30/06/2008
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
// Imports syst�me
//

import javax.swing.text.StyledDocument;

//
//Imports du projet
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
 * Nom: ComponentConfig
 * 
 * Description:
 * Cette classe repr�sente un fichier de configuration d'un composant I-SIS. 
 * A un nom de fichier, on associe une description et un chemin absolu. 
 * Elle est utilis�e lors du processus de cr�ation ou de 
 * modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class ComponentConfig implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: ComponentConfig
	 * 
	 * Description:
	 * Cette m�thode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public ComponentConfig() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "ComponentConfig");
		trace_methods.beginningOfMethod();
		
		_fileName = "";
		_description = "";
		_absolutePath = "";
		_flag = ' ';
		
		_newDescription = "";
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: getFileName
	 * 
	 * Description:
	 * Cette m�thode retourne le nom associ� au fichier.
	 * 
	 * Retourne: Une chaine de caracteres : Le nom du fichier.
	 * ----------------------------------------------------------*/
	public String getFileName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "getFileName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _fileName;
	}
	
	/*----------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette m�thode retourne la chaine de caracteres 'description'
	 * associ� au fichier.
	 * 
	 * Retourne: Une chaine de caractere : La description.
	 * ----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "getDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _description;
	}
	
	/*----------------------------------------------------------
	 * Nom: getAbsolutePath
	 * 
	 * Description:
	 * Cette m�thode retourne le chemin absolu associ� au fichier.
	 * 
	 * Retourne: Une chaine de caractere : Le chemin absolu.
	 * ----------------------------------------------------------*/
	public String getAbsolutePath() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "getAbsolutePath");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _absolutePath;
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
				"ComponentConfig", "getFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _flag;
	}
	
	/*----------------------------------------------------------
	 * Nom: setFileName
	 * 
	 * Description:
	 * Cette m�thode modifie le nom du fichier en lui donnant 
	 * celui passe en parametre.
	 * 
	 * Arguments:
	 *  - name: Le nouveau nom du fichier.
	 * ----------------------------------------------------------*/
	public void setFileName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "setFileName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
		
		_fileName = name;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setDescription
	 * 
	 * Description:
	 * Cette m�thode modifie la description du fichier en lui 
	 * donnant celle pass�e en parametre.
	 * 
	 * Arguments:
	 *  - description: La nouvelle description de l'action
	 * ----------------------------------------------------------*/
	public void setDescription(String description) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "setDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		
		_description = description;
		
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	 * Nom: setAbsolutePath
	 * 
	 * Description:
	 * Cette m�thode modifie le chemin absolu du fichier en lui 
	 * donnant celui pass� en parametre.
	 * 
	 * Arguments:
	 *  - path: Le nouveau chemin du fichier.
	 * ----------------------------------------------------------*/
	public void setAbsolutePath(String path) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "setAbsolutePath");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("path=" + path);
		
		_absolutePath = path;
		
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
				"ComponentConfig", "setFlag");
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
	 * Cette m�thode retourne la nouvelle description saisie par 
	 * l'utilisateur.
	 * 
	 * Retourne : La nouvelle description.
	 * ----------------------------------------------------------*/
	public String getNewDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "getNewDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newDescription;
	}

	/*----------------------------------------------------------
	 * Nom: setNewDescription
	 * 
	 * Description:
	 * Cette m�thode modifie la nouvelle description saisie par 
	 * l'utilisateur.
	 * 
	 * Param�tre : 
	 *   - description : La nouvelle description.
	 * ----------------------------------------------------------*/
	public void setNewDescription(String description) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "setNewDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		
		_newDescription = description;
		
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
	 * A partir du r�sultat, elle va compl�ter les champs du ComponentConfig. 
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
	public boolean loadData( ServiceSessionProxy session,  
			IndexedList context, IsisParameter[] valuesOfTableKey ) 
		throws InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "loadData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableConfig = "TableConfig";
		String NameConfig = "Name";
		String DescConfig = "Description";
		String PathConfig = "AbsolutePath";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		
		try {
			
			// On r�cup�re l'ensemble des noms des champs de la table
			// correspondant aux fichiers de configuration.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
		
			TableConfig = 
				configuration_api.getString("ComponentConfiguration.TableConfig");
			NameConfig = 
				configuration_api.getString("ComponentConfiguration.TableConfig.Name");
			DescConfig = 
				configuration_api.getString("ComponentConfiguration.TableConfig.Description");
			PathConfig = 
				configuration_api.getString("ComponentConfiguration.TableConfig.AbsolutePath");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand m�me
		}
		// On s�lectionne les colonnes de la table qui nous interressent
		String []column = new String[3];
		column[0] = NameConfig;
		column[1] = DescConfig;
		column[2] = PathConfig;
		
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
			String [] result = session.getSelectResult(TableConfig, 
					column, condition, "", context);
			
			if (result != null) {
				// On construit la d�finition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableConfig);
		
				// Pour chaque ligne r�sultat de la requ�te Select
				for (int index = 1 ; index < result.length ; index++) {
					// On r�cup�re les donn�es
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On construit le ComponentConfig
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameConfig))
								_fileName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DescConfig))
								_description = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(PathConfig))
								_absolutePath = object_parameters[index2].value;
							else {
								// Une des colonnes retourn�es n'est pas bonne, 
								// on signale l'erreur
								trace_errors.writeTrace( "Probl�me : " + 
										object_parameters[index2] + "innatendu !");
								
							}
						}
					}					
				}
				
				manager.releaseTableDefinitionLeasing(table_definition);
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
	public boolean saveData(
		String actionId,
		IndexedList context, 
		GenericTreeObjectNode selectedNode
		) 
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"ComponentConfig", "saveData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		
		if (_flag != 'E') {
		
			String TableConfig = "TableConfig";
			String NameConfig = "Name";
			String DescConfig = "Description";
			String PathConfig = "AbsolutePath";
			TableDefinitionManager manager = TableDefinitionManager.getInstance();
			
			try {
				ConfigurationAPI configuration_api = new ConfigurationAPI();
				configuration_api.useSection("I-SIS");
				
				// On r�cup�re l'ensemble des noms des champs de la table
				// correspondant aux fichiers de configuration.
				TableConfig = configuration_api.getString("ComponentConfiguration." +
							"TableConfig");
				NameConfig = configuration_api.getString("ComponentConfiguration." +
							"TableConfig.Name");
				DescConfig = configuration_api.getString("ComponentConfiguration." +
							"TableConfig.Description");
				PathConfig = configuration_api.getString("ComponentConfiguration." +
							"TableConfig.AbsolutePath");
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
				String condition = NameConfig + "='" + _fileName +"' AND " +
						PathConfig + "='" + _absolutePath + "'";
				
				// On lance la requ�te pour obtenir la d�finition de la table
				String [] commandSelect = session.getSelectResult(TableConfig, 
						column, condition, "", context);
				
				// On construit la d�finition de la table
				IsisTableDefinition table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
							commandSelect, TableConfig);
				
				// A partir de la ligne r�sultat, on compl�te les valeurs de la 
				// requ�te
				
				String values = "";
				for (int index = 0 ; index < table_definition.columns.length ; index ++) {
					if (table_definition.columns[index].name.equals(NameConfig))
						values += _fileName;
					else if (table_definition.columns[index].name.equals(DescConfig)) {
						if (_flag == 'S')
							values += _description;
						else
							values += _newDescription;
					}
					else if (table_definition.columns[index].name.equals(PathConfig))
						values += _absolutePath;
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
				TableConfig, values, AgentSessionManager.getInstance()
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
					"Probl�me lors d'une requ�te sur la table " + TableConfig + " !");
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
				"ComponentConfig", "end");
		trace_methods.beginningOfMethod();
		
		_fileName = null;
		_description = null;
		_absolutePath = null;
		
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
		
		String NameFile = MessageManager.getMessage(
				"&ComponentConfiguration_Name"); 
		String DescFile = MessageManager.getMessage(
				"&ComponentConfiguration_Description");
		String PathFile = MessageManager.getMessage(
				"&ComponentConfiguration_AbsolutePath");
		
		document.insertString(offset, System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		
		document.insertString(offset, PathFile + " : "+  
				_absolutePath + System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		
		if( (_flag == 'E' || _flag == 'S') &&  !_description.equals("") ) {
				document.insertString(offset, DescFile + " : " +
						_description + System.getProperty("line.separator", "\n"), 
						document.getStyle("regular"));
		}
		else if ( (_flag != 'E' || _flag != 'S') && !_newDescription.equals("")) {
			document.insertString(offset, DescFile + " : " +
					_newDescription + System.getProperty("line.separator", "\n"), 
					document.getStyle("regular"));
		}
		
		document.insertString(offset, NameFile + " : " + _fileName + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		
		
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette m�thode permet de retourner une copie de l'�l�ment ComponentConfig.
	 * 
	 * Retourne :le clone de l'�l�ment du mod�le sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		ComponentConfig clone = new ComponentConfig();
		clone.setFileName(_fileName);
		clone.setDescription(_description);
		clone.setAbsolutePath(_absolutePath);
		clone.setNewDescription(_newDescription);
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _fileName
	 *
	 * Description:
	 * Cet attribut maintient le nom correspondant � un fichier
	 * de configuration du composant.
	 * ----------------------------------------------------------*/
	private String _fileName;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 *
	 * Description:
	 * Cet attribut maintient une description sur le fichier de 
	 * configuration.
	 * ----------------------------------------------------------*/
	private String _description;
	
	/*----------------------------------------------------------
	 * Nom: _absolutePath
	 *
	 * Description:
	 * Cet attribut maintient le chemin abolu associe a un fichier 
	 * de configuration.
	 * ----------------------------------------------------------*/
	private String _absolutePath;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 *
	 * Description:
	 * Cet attribut est une seconde description sur le fichier de 
	 * configuration. Il s'agit de la nouvelle valeur saisie par 
	 * l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newDescription;
	
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
