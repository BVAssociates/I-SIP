/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/appcfg/model
* $Revision: 1.4 $
*
* ------------------------------------------------------------
* DESCRIPTION: 
* DATE:        29/08/2008
* AUTEUR:      Hicham Doghmi
* PROJET:      I-SIS
* GROUPE:      
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* ------------------------------------------------------------*/
// Déclaration du package

package com.bv.isis.console.impl.processor.appcfg.model;

//Imports système
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
 * Nom: PortalApplicationDoc
 * 
 * Description:
 * Cette classe modélise un dossier d'exploitation associée 
 * à un composant I-SIS. Elle se caractérise par un nom et 
 * une description. Elle est utilisée lors du processus de 
 * création ou de modification d'un composant dans la console.
 * ----------------------------------------------------------*/
public class PortalApplicationDoc implements ModelInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: PortalApplicationDoc
	 * 
	 * Description:
	 * Cette méthode est le constructeur de la classe. 
	 * ----------------------------------------------------------*/
	public PortalApplicationDoc() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "PortalApplicationDoc");
		trace_methods.beginningOfMethod();
		
		_docName = "";
		_description = "";
		_url = "";
		_flag = ' ';
		
		_newDescription = "";
		_newUrl = "";
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: getDocName
	 * 
	 * Description:
	 * Cette méthode retourne le nom associé a la variable.
	 * 
	 * Retourne: Une chaine de caractere : Le nom de la variable.
	 * ----------------------------------------------------------*/
	public String getDocName() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "getDocName");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _docName;
	}
	
	/*----------------------------------------------------------
	 * Nom: getDescription
	 * 
	 * Description:
	 * Cette méthode retourne la chaine de caracteres 'description'
	 * associé au composant.
	 * 
	 * Retourne: Une chaine de caractere : La description.
	 * ----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "getDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
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
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "getFlag");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _flag;
	}
	
	/*----------------------------------------------------------
	 * Nom: getUrl
	 * 
	 * Description:
	 * Cette méthode retourne la valeur par défaut '_url'
	 * associé au composant.
	 * 
	 * Retourne: Une chaine de caractere : La valeur par défaut.
	 * ----------------------------------------------------------*/
	public String getUrl() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "getUrl");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _url;
	}
	
	/*----------------------------------------------------------
	 * Nom: setDocName
	 * 
	 * Description:
	 * Cette méthode modifie le nom de la variable en lui donnant 
	 * celui passe en parametre.
	 * 
	 * Arguments:
	 * - name: Le nouveau nom de la variable
 	 * ----------------------------------------------------------*/
	public void setDocName(String name) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "setDocName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("name=" + name);
		
		_docName = name;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setDescription
	 * 
	 * Description:
	 * Cette méthode modifie la description du composant en lui 
	 * donnant celle passee en parametre.
	 * 
	 * Arguments:
	 * - description: La nouvelle description du composant
 	 * ----------------------------------------------------------*/
	public void setDescription(String description) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "setDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		
		_description = description;
		
		trace_methods.endOfMethod();
	}
	

	
	/*----------------------------------------------------------
	 * Nom: setUrl
	 * 
	 * Description:
	 * Cette méthode modifie la valeur par défaut du composant en lui 
	 * donnant celle passee en parametre.
	 * 
	 * Arguments:
	 *  - value: La nouvelle valeur par défaut du composant
	 * ----------------------------------------------------------*/
	public void setUrl(String url) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "setUrl");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("url=" + url);
		
		_url = url;
		
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
				"PortalApplicationDoc", "setFlag") ;
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
	 * Cette méthode retourne la nouvelle description saisie.
	 * 
	 * Retourne : La nouvelle description.
	 * ----------------------------------------------------------*/
	public String getNewDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "getNewDescription");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newDescription;
	}

	/*----------------------------------------------------------
	 * Nom: getNewUrl
	 * 
	 * Description:
	 * Cette méthode retourne la nouvelle valeur par défaut.
	 * 
	 * Retourne : La nouvelle valeur par défaut.
	 * ----------------------------------------------------------*/
	public String getNewUrl() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "getNewUrl");
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		
		return _newUrl;
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
				"PortalApplicationDoc", "setNewDescription");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("description=" + description);
		
		_newDescription = description;
		
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	 * Nom: setNewUrl
	 * 
	 * Description:
	 * Cette méthode modifie la nouvelle valeur par défaut.
	 * 
	 * Paramètre : 
	 *   - defaultValue : La nouvelle valeur par défaut.
	 * ----------------------------------------------------------*/
	public void setNewUrl(String url) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"PortalApplicationDoc", "setNewUrl");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		trace_methods.beginningOfMethod();
		
		trace_arguments.writeTrace("url=" + url);
		
		_newUrl = url;
		
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
	 * A partir du résultat, elle va compléter les champs du PortalApplicationDoc. 
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
				"PortalApplicationDoc", "loadData");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("session=" + session);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("valuesOfTableKey=" + valuesOfTableKey);
		
		IsisTableDefinition table_definition;
		String TableDoc = "TableDoc";
		String NameDoc = "DocName";
		String DescDoc = "Description";
		String UrlDoc = "Url";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		try {
			// On récupère l'ensemble des noms des champs de la table
			// correspondant aux fichiers.
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			configuration_api.useSection("I-SIS");
			
			TableDoc= configuration_api.getString("PortalApplication." +
					"TableDoc");
			NameDoc = configuration_api.getString("PortalApplication." +
					"TableDoc.DocName");
			DescDoc = configuration_api.getString("PortalApplication." +
					"TableDoc.Description");
			UrlDoc = configuration_api.getString("PortalApplication." +
					"TableDoc.Url");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
				
		// On sélectionne les colonnes de la table qui nous interressent
		String [] column = new String[3];
		column[0] = NameDoc;
		column[1] = DescDoc;
		column[2] = UrlDoc;
			
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
			String [] result = session.getSelectResult(TableDoc, 
					column, condition, "", context);
			
			if (result != null) {
				// On construit la définition de la table
				table_definition = 
					TreeNodeFactory.buildDefinitionFromSelectResult(
					result, TableDoc);
				
				// Pour chaque ligne résultat de la requête Select
				for (int index = 1 ; index < result.length ; index++) {
					// On récupère les données
					IsisParameter[] object_parameters =
						TreeNodeFactory.buildParametersFromSelectResult(
						result, index, table_definition);
					
					// On construit le PortalApplicationDoc
					for (int index2 = 0 ; index2 < object_parameters.length ; index2++) {
						if (!object_parameters[index2].value.equals("")) {
							if (object_parameters[index2].name.equals(NameDoc))
								_docName = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(DescDoc))
								_description = object_parameters[index2].value;
							else if (object_parameters[index2].name.equals(UrlDoc))
								_url = object_parameters[index2].value;
							else {
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
				"Problème lors du chargement des données : " +
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
	 * Cette méthode sauvegarde le modèle dans les tables. 
	 * Elle implémente la méthode de la classe ModelInterface. Après avoir
	 * récupérer les noms des champs des tables, cette méthode va construire
	 * la requête d'insertion ou de modification d'une données de la 
	 * table correspondant aux variables. Puis via la classe
	 * ExecutionSurveyor, cette requête sera exécutée. 
	 * 
	 * Paramètres :
	 *   - actionId : L'identifiant unique d'action
	 *   - context : Cette référence correspond à l'ensemble des paramètres
	 * 				"exportables" des noeuds traversés pour atteindre le 
	 * 				noeud concerné. A cet ensemble sont ajoutés tous les 
	 * 				paramètres du noeud lui-même.
	 *   - seletedNode : Une référence sur le noeud sélectionné.
	 * 
	 * Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
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
				"PortalApplicationDoc", "saveData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		
		String TableDoc = "PortalApplicationDocs";
		String NameDocument = "DocName";
		String DescDocument = "Description";
		String UrlDocument = "Url";
		String ApplicationName = "ApplicationName";
		TableDefinitionManager manager = TableDefinitionManager.getInstance();

		if (_flag != 'E') {
		
			try {
				ConfigurationAPI configuration_api = new ConfigurationAPI();
				configuration_api.useSection("I-SIS");
				
				// On récupère l'ensemble des noms des champs de la table
				// correspondant aux variables.
				TableDoc = configuration_api.getString(
						"PortalApplication.TableDoc");
				NameDocument = configuration_api.getString(
						"PortalApplication.TableDoc.Name");
				DescDocument = configuration_api.getString(
						"PortalApplication.TableDoc.Description");
				UrlDocument = configuration_api.getString(
						"PortalApplication.TableDoc.Url");
				ApplicationName = configuration_api.getString(
						"PortalApplication.TableDoc.ApplicationName");
			}
			catch(Exception exception) {
				trace_errors.writeTrace(
					"Erreur lors de la récupération de la configuration: " +
					exception);
				// Il y a eu une erreur, on continue quand même
			}	
		
		try {
			// On ouvre une session de service à partir du noeud sélectionné
			ServiceSessionProxy session = new ServiceSessionProxy(
					selectedNode.getServiceSession());
			
			String appliName = selectedNode.getKey();
			
			// On sélectionne toutes les colonnes de la table
			String [] column = { "" };
			// On construit la condition en fonction de la clé de la table
			String condition = NameDocument + "='" + _docName +"' AND "
					+ ApplicationName + "='" + appliName + "'";
			
			// On lance la requête pour obtenir la définition de la table
			String [] commandSelect = session.getSelectResult(TableDoc, 
					column, condition, "", context);
			
			// On construit la définition de la table
			IsisTableDefinition table_definition = 
				TreeNodeFactory.buildDefinitionFromSelectResult(
						commandSelect, TableDoc);
			
			// A partir de la ligne résultat, on complète les valeurs de la 
			// requête
			String values = "";
			for (int index = 0 ; index < table_definition.columns.length ; index ++) {
				if (table_definition.columns[index].name.equals(NameDocument))
					values += _docName;
				else if (table_definition.columns[index].name.equals(DescDocument)) {
					if (_flag != 'S')
						values += _newDescription;
					else
						values += _description;
				}
				else if (table_definition.columns[index].name.equals(UrlDocument)) {
					if (_flag != 'S')
						values += _newUrl;
					else
						values += _url;
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
			// cet élément
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
					TableDoc, values, AgentSessionManager.getInstance()
								.getAgentLayerMode(selectedNode.getAgentName()));
	
			// On exécute la requête
			ExecutionSurveyor surveyor = new ExecutionSurveyor();
			
			surveyor.execute(actionId, command, selectedNode, context);
			
			// On libère l'utilisation de la définition
			manager.releaseTableDefinitionLeasing(table_definition);
			_flag = 'E';
		}
		catch (InnerException exception) 
		{
			trace_errors.writeTrace(
			"Problème lors d'une requête sur la table " + TableDoc + " !");
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
				"PortalApplicationDoc", "end");
		trace_methods.beginningOfMethod();
		
		_docName = null;
		_description = null;
		
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
		
		String NameDoc = MessageManager.getMessage(
				"&AppCfg_Name"); 
		String DescDoc = MessageManager.getMessage(
				"&AppCfg_Description");
		String UrlDoc = MessageManager.getMessage(
				"&AppCfg_Url");
		
		String url = "";
		String desc = "";
		
		if (_flag == 'E' || _flag == 'S') {
			url = _url;
			desc = _description;
		} else {
			url = _newUrl;
			desc = _newDescription;
		}
		
		document.insertString(offset,  
				System.getProperty("line.separator", "\n"),
				document.getStyle("regular"));
		document.insertString(offset, UrlDoc + " : " +
				url + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, DescDoc + " : " +
				desc + System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
		document.insertString(offset, NameDoc + " : " + _docName + 
				System.getProperty("line.separator", "\n"), 
				document.getStyle("regular"));
	}
	
	/*----------------------------------------------------------
	 * Nom: clone
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une copie de l'élément PortalApplicationDoc.
	 * 
	 * Retourne :le clone de l'élément du modèle sous le type ModelInterface.
	 * ----------------------------------------------------------*/
	public ModelInterface clone() {
		
		PortalApplicationDoc clone = new PortalApplicationDoc();
		clone.setDocName(_docName);
		clone.setDescription(_description);
		clone.setUrl(_url);
		clone.setNewDescription(_newDescription);
		clone.setNewUrl(_newUrl);
		clone.setFlag(_flag);
		
		return clone;		
	}
	
	// ******************* PROTECTED **********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	 * Nom: _variableName
	 * 
	 * Description:
	 * Cet attribut maintient le nom correspondant à une variable
	 * du composant
	 * ----------------------------------------------------------*/
	private String _docName;

	/*----------------------------------------------------------
	 * Nom: _description
	 * 
	 * Description:
	 * Cet attribut maintient une description sur le composant
	 * ----------------------------------------------------------*/
	private String _description;
	
	/*----------------------------------------------------------
	 * Nom: _url
	 * 
	 * Description:
	 * Cet attribut représente, pour la variable,  une valeur par défaut sous 
	 * forme d'une chaîne de caractères.
	 * ----------------------------------------------------------*/
	private String _url;
	
	/*----------------------------------------------------------
	 * Nom: _description
	 * 
	 * Description:
	 * Cet attribut est la nouvelle description du document d'exploitation
	 * saisie par l'utilisateur.
	 * ----------------------------------------------------------*/
	private String _newDescription;
	
	/*----------------------------------------------------------
	 * Nom: _newUrl
	 * 
	 * Description:
	 * Cet attribut est la nouvelle url.
	 * ----------------------------------------------------------*/
	private String _newUrl;
	
	/*----------------------------------------------------------
	 * Nom: _flag
	 *
	 * Description:
	 * Cet attribut représente un état associé au document d'xploitation
	 * vis à vis
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
