/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/TableDefinitionManager.java,v $
* $Revision: 1.16 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des définitions des tables
* DATE:        14/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: TableDefinitionManager.java,v $
* Revision 1.16  2009/01/14 14:21:58  tz
* Prise en compte de la modification des packages.
*
* Revision 1.15  2008/08/11 10:45:56  tz
* Remplacement d'une trace d'erreur en une trace de débug.
*
* Revision 1.14  2006/11/09 11:57:33  tz
* Suppression du patch pour les I-TOOLS Windows.
*
* Revision 1.13  2006/10/13 15:08:15  tz
* Remplacement du séparateur dans la construction de la clé de la table
* de hash.
*
* Revision 1.12  2005/10/07 08:42:09  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.11  2005/07/01 12:28:08  tz
* Modification du composant pour les traces
*
* Revision 1.10  2004/11/02 09:11:24  tz
* Gestion des leasings des définitions.
*
* Revision 1.9  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.8  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.7  2004/07/29 12:22:24  tz
* Utilisation de Portal* au lieu de Master*
* Mise à jour de la documentation
*
* Revision 1.6  2003/12/08 14:37:09  tz
* Mise à jour du modèle
*
* Revision 1.5  2002/11/19 08:45:56  tz
* Mise en cache des définitions du Maître avec son vrai nom d'hôte.
*
* Revision 1.4  2002/04/05 15:47:02  tz
* Cloture itération IT1.2
*
* Revision 1.3  2002/03/27 09:41:00  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2001/12/19 09:59:03  tz
* Cloture itération IT1.0.0
*
* Revision 1.1  2001/11/14 17:17:34  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.com;

//
// Imports système
//
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.message.MessageManager;

//
// Imports du projet
//
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.abs.processor.TableDefinitionListener;

/*----------------------------------------------------------
* Nom: TableDefinitionManager
*
* Description:
* Cette classe est une classe technique chargée de la récupération et de la
* mise en cache des définitions des tables qui sont utilisées par l'interface
* graphique.
* La classe utilise le design pattern Singleton afin d'être accessible par
* toute classe de l'application de manière identique.
* La mise en cache des définitions des tables est nécessaire afin de réduire au
* maximum les échanges entre l'interface graphique et le Portail. En effet, il
* n'est pas concevable de devoir rechercher sur la plate-forme Portail la
* définition d'une table à chaque fois qu'on en a besoin, cela prendrait
* beaucoup trop de temps et consommerait beaucoup trop de ressources réseau.
*
* Les définitions déjà chargées sont sockées dans une table de Hash, dont la
* clé est constituée du nom de la plate-forme sur laquelle la table a été
* récupérée, et du chemin complet de cette table sur la dite plate-forme. Cela
* permet de stocker des définitions de tables définies sur un même plate-forme,
* ayant le même nom, mais ne provenant pas du même fichier (I-CLES différent, 
* par exemple).
* ----------------------------------------------------------*/
public class TableDefinitionManager
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getInstance
	*
	* Description:
	* Cette méthode statique fait partie du design pattern Singleton. Elle
	* permet de récupérer l'unique instance de la classe TableDefinitionManager.
	* Si cette instance n'existe pas, la méthode va la créer.
	*
	* Retourne: L'unique instance de TableDefinitionManager.
	* ----------------------------------------------------------*/
	public static TableDefinitionManager getInstance()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "getInstance");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		if(_instance == null)
		{
			trace_debug.writeTrace("Création de l'instance de TableDefinitionManager");
			_instance = new TableDefinitionManager();
		}
		trace_methods.endOfMethod();
		return _instance;
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	*
	* Description:
	* Cette méthode statique permet de libérer les ressources allouées pendant
	* l'exécution de l'application.
	* Elle libère toutes les références des définitions des tables ayant été
	* mises en cache, et l'objet de table de Hash servant à contenir le cache.
	* Elle libère également l'unique instance de TableDefinitionManager.
	* ----------------------------------------------------------*/
	public static void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "cleanBeforeExit");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		if(_instance != null)
		{
			trace_debug.writeTrace("Libération des références");
			// Libération des références
			_instance._definitions.clear();
			_instance._definitions = null;
			_instance._listeners.clear();
			_instance._listeners = null;
			// Libération de l'instance
			_instance = null;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTableDefinition
	*
	* Description:
	* Cette méthode permet de récupérer la définition d'une table sur une
	* plate-forme. Cette méthode doit être appelée pour une table dont la
	* définition n'a pas été chargée au préalable (sinon, il vaut mieux
	* utiliser l'autre méthode getTableDefinition()).
	* Cette méthode va tout d'abord récupérer le chemin complet du fichier
	* dictionnaire de la table (via la méthode getDefinitionFilePath() de la
	* classe SessionServiceProxy) et regarder dans le cache si cette définition 
	* n'a pas déjà été chargée.
	* Si ce n'est pas le cas, la méthode va récupérer cette définition via la
	* méthode getTableDefinition() de la classe ServiceSessionProxy, et
	* l'ajouter au cache.
	* La clé du cache est composée du nom de l'Agent, du nom du I-CLES, du 
	* type du I-CLES et enfin du chemin du dictionnaire.
	*
	* Si, pour une raison ou pour une autre, la récupération de la définition
	* de la table n'est pas possible, l'exception InnerException est levée.
	*
	* Arguments:
	*  - agentName: le nom de la plate-forme agent sur laquelle la définition
	*    doit être récupérée,
	*  - iClesName: le nom du I-CLES sur lequel la session a été ouverte,
	*  - serviceType: le type du service sur lequel la session a été ouverte,
	*  - tableName: le nom de la table dont la définition doit être récupérée,
	*  - context: une référence sur une liste indexée contenant le contexte
	*    permettant d'accéder à la définition de la table,
	*  - serviceSession: une référence sur l'interface ServiceSessionInterface
	*    représentant la session de service à utiliser pour la requête.
	*
	* Retourne: Une référence sur un object IsisTableDefinition contenant la
	* définition de la table.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public synchronized IsisTableDefinition getTableDefinition(
		String agentName,
		String iClesName,
		String serviceType,
		String tableName,
		IndexedList context,
		ServiceSessionInterface serviceSession
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "getTableDefinition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		IsisTableDefinition definition = null;
		String definition_file_path = null;
		ObjectLeasingHolder leasing_holder = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("context=" + context);
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		// Création de l'instance de ServiceSessionProxy
		ServiceSessionProxy session_proxy =
			new ServiceSessionProxy(serviceSession);
		// La première chose est de récupérer le chemin du dictionnaire de la
		// table concernée.
		definition_file_path = session_proxy.getDefinitionFilePath(tableName,
			context);
		trace_debug.writeTrace("Chemin du dictionnaire: " +
			definition_file_path);
		// Si l'agent est null, on va utiliser Portal
		if(agentName == null)
		{
			agentName = "Portal";
		}
		// On vérifie si cette table existe dans le cache
		String cache_key = agentName + ";" + iClesName + ";" + serviceType +
			";" + definition_file_path;
		trace_debug.writeTrace("cache_key=" + cache_key);
		if(_definitions.containsKey(cache_key) == true)
		{
			trace_debug.writeTrace("La définition est en cache");
			// La définition existe déjà en mémoire, on récupère l'instance
			leasing_holder = (ObjectLeasingHolder)_definitions.get(cache_key);
			leasing_holder.addLeasing();
			definition = (IsisTableDefinition)leasing_holder.getLeasedObject();
			// On va déclencher un événement
			fireTableDefinitionUseChanged(agentName, iClesName, serviceType,
				definition_file_path, leasing_holder.getNumberOfLeasings());
		}
		else
		{
			trace_debug.writeTrace("La définition n'est pas en cache");
			// La définition n'est pas dans le cache, il faut la récupérer
			// sur le serveur.
			definition = session_proxy.getTableDefinition(tableName, context);
			trace_debug.writeTrace("Ajout de la définition en cache");
			// On l'ajoute au cache
			leasing_holder = new ObjectLeasingHolder(definition);
			_definitions.put(cache_key, leasing_holder);
			// On va déclencher un événement
			fireTableDefinitionAdded(agentName, iClesName, serviceType, 
				definition_file_path);
		}
		trace_debug.writeTrace("Nombre d'utilisation de la définition " + 
			cache_key + ": " + leasing_holder.getNumberOfLeasings());
		trace_methods.endOfMethod();
		return definition;
	}

	/*----------------------------------------------------------
	* Nom: getTableDefinition
	*
	* Description:
	* Cette méthode permet de récupérer la définition d'une table ayant déjà été
	* chargée. La définition de la table est récupérée depuis le cache en
	* fonction du nom de la plate-forme, du nom du I-CLES, du type du service 
	* et du chemin complet du fichier sur celle-ci.
	*
	* Arguments:
	*  - agentName: le nom de la plate-forme sur laquelle la définition a dû
	*    être récupérée,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: le chemin complet du fichier dictionnaire de la
	*    table sur la plate-forme.
	*
	* Retourne: Une référence sur un object IsisTableDefinition contenant la
	* définition de la table, ou null.
	* ----------------------------------------------------------*/
	public synchronized IsisTableDefinition getTableDefinition(
		String agentName,
		String iClesName,
		String serviceType,
		String definitionFilePath
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "getTableDefinition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		IsisTableDefinition definition = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		// Si l'agent est null, on va utiliser Portal
		if(agentName == null)
		{
			agentName = "Portal";
		}
		// On vérifie si cette table existe dans le cache
		String cache_key = agentName + ";" + iClesName + ";" + serviceType +
			";" + definitionFilePath;
		trace_debug.writeTrace("cache_key=" + cache_key);
		if(_definitions.containsKey(cache_key) == true)
		{
			trace_debug.writeTrace("La définition est en cache");
			// La définition existe déjà en mémoire, on récupère l'instance
			ObjectLeasingHolder leasing_holder =
				(ObjectLeasingHolder)_definitions.get(cache_key);
			leasing_holder.addLeasing();
			definition = (IsisTableDefinition)leasing_holder.getLeasedObject();
			// On va déclencher un événement
			fireTableDefinitionUseChanged(agentName, iClesName, serviceType,
				definitionFilePath, leasing_holder.getNumberOfLeasings());
			trace_debug.writeTrace("Nombre d'utilisation de la définition " +
				cache_key + ": " + leasing_holder.getNumberOfLeasings());
		}
		else
		{
			trace_debug.writeTrace("La définition n'est pas en cache");
		}
		trace_methods.endOfMethod();
		return definition;
	}

	/*----------------------------------------------------------
	* Nom: getTableDefinitionKeys
	* 
	* Description:
	* Cette méthode permet à une classe de récupérer l'ensemble des clés du 
	* cache des définitions des tables. Les clés sont constituées à partir du 
	* nom de l'Agent et du chemin absolu du dictionnaire sur l'Agent avec la 
	* syntaxe suivante:
	* <Agent>:<Chemin>.
	* 
	* Retourne: Une référence sur une Enumeration contenant toutes les clés du 
	* cache des définitions.
	* ----------------------------------------------------------*/
	public synchronized Enumeration getTableDefinitionKeys()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "getTableDefinitionKeys");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _definitions.keys();
	}

	/*----------------------------------------------------------
	* Nom: getTableDefinitionLeasing
	* 
	* Description:
	* Cette méthode permet de récupérer l'objet ObjectLeasingHolder associé à 
	* la clé passée en argument. Si la clé n'existe pas dans le cache, une 
	* référence nulle est retournée.
	* 
	* Arguments:
	*  - key: La clé de l'objet ObjectLeasingHolder à récupérer.
	* 
	* Retourne: Une référence sur l'objet ObjectLeasingHolder ou null.
	* ----------------------------------------------------------*/
	public synchronized ObjectLeasingHolder getTableDefinitionLeasing(
		String key
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "getTableDefinitionLeasing");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("key=" + key);
		if(key == null)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			trace_methods.endOfMethod();
			return null;
		}
		// On regarde si la clé existe
		if(_definitions.containsKey(key) == false)
		{
			trace_errors.writeTrace("La clé n'existe pas !");
			trace_methods.endOfMethod();
			return null;
		}
		trace_methods.endOfMethod();
		// On retourne la valeur
		return (ObjectLeasingHolder)_definitions.get(key);
	}

	/*----------------------------------------------------------
	* Nom: releaseTableDefinitionLeasing
	* 
	* Description:
	* Cette méthode permet de libérer une utilisation de la définition passée 
	* en argument. Il est nécessaire d'appeler cette méthode lorsqu'une 
	* définition de table n'est plus utilisée de sorte à permettre, via un 
	* processeur graphique, de libérer les définitions qui ne sont plus 
	* utilisées.
	* 
	* Arguments:
	*  - tableDefinition: Une référence sur la définition de table dont il 
	*    faut libérer une utilisation.
 	* ----------------------------------------------------------*/
 	public synchronized void releaseTableDefinitionLeasing(
 		IsisTableDefinition tableDefinition
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "releaseTableDefinitionLeasing");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableDefinition=" + tableDefinition);
		if(tableDefinition == null)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			trace_methods.endOfMethod();
			return;
		}
		Enumeration definition_keys = _definitions.keys();
		while(definition_keys.hasMoreElements() == true)
		{
			String key = (String)definition_keys.nextElement(); 
			ObjectLeasingHolder leasing_holder = 
				(ObjectLeasingHolder)_definitions.get(key);
			IsisTableDefinition table_definition = 
				(IsisTableDefinition)leasing_holder.getLeasedObject();
			if(table_definition == tableDefinition)
			{
				trace_debug.writeTrace("La définition a été trouvée dans le " +
					"cache, libération d'un leasing");
				leasing_holder.releaseLeasing();
				trace_debug.writeTrace("Nombre d'utilisations de la " +
					"définition " + key + ": " + leasing_holder.getNumberOfLeasings());
				// On va déclencher un événement
				UtilStringTokenizer tokenizer = 
					new UtilStringTokenizer(key, ";");
				String agent_name = tokenizer.nextToken();
				String icles_name = tokenizer.nextToken();
				String service_type = tokenizer.nextToken();
				String file_path = tokenizer.nextToken();
				trace_debug.writeTrace("agent_name=" + agent_name);
				trace_debug.writeTrace("icles_name=" + icles_name);
				trace_debug.writeTrace("service_type=" + service_type);
				trace_debug.writeTrace("file_path=" + file_path);
				fireTableDefinitionUseChanged(agent_name, icles_name, 
					service_type, file_path, 
					leasing_holder.getNumberOfLeasings());
				trace_methods.endOfMethod();
				return;
			}
		}
		trace_errors.writeTrace("La définition n'a pas été trouvée en cache");
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: releaseTableDefinitionLeasing
	* 
	* Description:
	* Cette méthode permet de libérer une utilisation de la définition passée 
	* en argument. Il est nécessaire d'appeler cette méthode lorsqu'une 
	* définition de table n'est plus utilisée de sorte à permettre, via un 
	* processeur graphique, de libérer les définitions qui ne sont plus 
	* utilisées.
	* 
	* Arguments:
	*  - agentName: le nom de la plate-forme sur laquelle la définition a dû 
	*    être récupérée,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
 	*  - definitionFilePath: le chemin complet du fichier dictionnaire de la 
	*    table sur la plate-forme.
 	* ----------------------------------------------------------*/
 	public synchronized void releaseTableDefinitionLeasing(
 		String agentName,
 		String iClesName,
 		String serviceType,
 		String definitionFilePath
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "releaseTableDefinitionLeasing");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String cache_key;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		// Si l'agent est null, on va utiliser Portal
		if(agentName == null)
		{
			agentName = "Portal";
		}
		// On construit la clé
		cache_key = agentName + ";" + iClesName + ";" + serviceType +
			";" + definitionFilePath;
		// La définition est-elle en cache
		if(_definitions.containsKey(cache_key) == false)
		{
			if(definitionFilePath == null || 
				definitionFilePath.equals("") == true) {
				trace_debug.writeTrace("La définition n'est pas en cache");
			}
			else {
				trace_errors.writeTrace("La définition n'est pas en cache");
			}
			trace_methods.endOfMethod();
			return;
		}
		ObjectLeasingHolder leasing_holder = 
			(ObjectLeasingHolder)_definitions.get(cache_key);
		trace_debug.writeTrace("La définition a été trouvée dans le " +
			"cache, libération d'un leasing");
		leasing_holder.releaseLeasing();
		trace_debug.writeTrace("Nombre d'utilisations de la " +
			"définition " + cache_key + ": " + leasing_holder.getNumberOfLeasings());
		// On va déclencher un événement
		fireTableDefinitionUseChanged(agentName, iClesName, serviceType, 
			definitionFilePath, leasing_holder.getNumberOfLeasings());
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: registerListener
	* 
	* Description:
	* Cette méthode permet d'ajouter un objet TableDefinitionListener à la 
	* liste des receveurs de notifications sur les ajouts/suppressions de 
	* définitions de tables.
	* 
	* Arguments:
	*  - listener: Une référence sur l'interface TableDefinitionListener à 
	*    ajouter à la liste.
 	* ----------------------------------------------------------*/
 	public synchronized void registerListener(
 		TableDefinitionListener listener
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "registerListener");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("listener=" + listener);
		_listeners.add(listener);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: unregisterListener
	* 
	* Description:
	* Cette méthode permet de supprimer un objet TableDefinitionListener de la 
	* liste des receveurs de notifications sur les ajouts/suppressions de 
	* définitions de tables.
	* 
	* Arguments:
	*  - listener: Une référence sur l'interface TableDefinitionListener à 
	*    supprimer de la liste.
 	* ----------------------------------------------------------*/
 	public synchronized void unregisterListener(
 		TableDefinitionListener listener
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "unregisterListener");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("listener=" + listener);
		_listeners.remove(listener);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: dumpTableDefinition
	* 
	* Description:
	* Cette méthode permet de libérer la définition d'une table ayant été 
	* préalablement chargée. La définition, si elle existe, est supprimée de 
	* la liste des définitions en cache.
	* 
	* Si la définition à libérer n'a pas un nombre d'utilisations égal à 0, 
	* l'exception InnerException est levée.
	* 
	* Arguments:
	*  - agentName: le nom de la plate-forme sur laquelle la définition a dû 
	*    être récupérée,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: le chemin complet du fichier dictionnaire de la 
	*    table sur la plate-forme.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public synchronized void dumpTableDefinition(
		String agentName,
		String iClesName,
		String serviceType,
		String definitionFilePath
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "dumpTableDefinition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		// Si l'agent est null, on va utiliser Portal
		if(agentName == null)
		{
			agentName = "Portal";
		}
		// On vérifie si cette table existe dans le cache
		String cache_key = agentName + ";" + iClesName + ";" + serviceType +
			";" + definitionFilePath;
		if(_definitions.containsKey(cache_key) == false)
		{
			trace_errors.writeTrace("La définition n'est pas en cache !");
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException(
				MessageManager.getMessage("&ERR_NoDefinitionForTable"),
				cache_key, null);
		}
		trace_debug.writeTrace("La définition est en cache");
		// La définition existe déjà en mémoire, on récupère l'instance
		ObjectLeasingHolder leasing_holder =
			(ObjectLeasingHolder)_definitions.get(cache_key);
		// On vérifie que l'objet n'est plus utilisé
		if(leasing_holder.isFreeOfLeasing() == false)
		{
			trace_errors.writeTrace("La définition est encore utilisée !");
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException(
				MessageManager.getMessage("&ERR_DefinitionStillInUse"),
				cache_key, null);
		}
		// On supprime la définition
		_definitions.remove(cache_key);
		// On va déclencher l'événement de suppression
		fireTableDefinitionRemoved(agentName, iClesName, serviceType,
			definitionFilePath);
		trace_methods.endOfMethod();
	}


	/*----------------------------------------------------------
	* Nom: clear
	* 
	* Description:
	* Cette méthode permet de supprimer tous les dictionnaires en cache, avec 
	* notification des interfaces en écoute pour chaque suppression.
	* Elle est destinée à être appelée lors de l'ouverture d'une session sur 
	* un nouveau Portail.
	* ----------------------------------------------------------*/
	public synchronized void clear()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "dumpTableDefinition");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// On va récupérer la liste des clés de cache
		Enumeration cache_keys = _definitions.keys();
		// On va supprimer les dictionnaires un par un
		while(cache_keys.hasMoreElements() == true)
		{
			String cache_key = (String)cache_keys.nextElement();
			trace_debug.writeTrace("cache_key=" + cache_key);
			UtilStringTokenizer tokenizer = 
				new UtilStringTokenizer(cache_key, ";");
			String agent_name = tokenizer.nextToken();
			String icles_name = tokenizer.nextToken();
			String service_type = tokenizer.nextToken();
			String definition_file_path = tokenizer.nextToken();
			trace_debug.writeTrace("agent_name=" + agent_name);
			trace_debug.writeTrace("icles_name=" + icles_name);
			trace_debug.writeTrace("service_type=" + service_type);
			trace_debug.writeTrace("definition_file_path=" + 
				definition_file_path);
			// On commande la suppression du dictionnaire
			_definitions.remove(cache_key);
			// On informe les listeners de la suppression
			fireTableDefinitionRemoved(agent_name, icles_name, service_type,
				definition_file_path);
		}
		trace_methods.endOfMethod();
	}
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _instance
	*
	* Description:
	* Cet attribut statique contient la référence de la seule et unique instance
	* de TableDefinitionManager. Cette instance est créée lors du premier appel
	* de la méthode getInstance(), et est libérée par l'appel de la méthode
	* cleanBeforeExit().
	* ----------------------------------------------------------*/
	private static TableDefinitionManager _instance = null;

	/*----------------------------------------------------------
	* Nom: _definitions
	*
	* Description:
	* Cet attribut contient une référence sur une table de Hash qui est utilisé
	* pour stocker localement les définitions des tables ayant été chargées. Il
	* implémente le cache des définitions.
	* La clé de cette table de Hash est constitué du nom de la plate-forme sur 
	* laquelle la définition a été récupérée, du nom du I-CLES, du type du 
	* service et du chemin complet d'accès au fichier dictionnaire de la table, 
	* ces éléments étant séparés par le caractère ':'.
	* La valeur de cette table de Hash est une instance d'ObjectLeasingHolder 
	* permettant de connaître le nombre d'utilisations des définitions de table.
	* ----------------------------------------------------------*/
	private Hashtable _definitions;

	/*----------------------------------------------------------
	* Nom: _listeners
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet Vector contenant la 
	* liste des interface TableDefinitionListener s'étant enregistrées pour 
	* recevoir les notifications d'ajout ou de suppression de définitions de 
	* tables.
	* ----------------------------------------------------------*/
	private Vector _listeners;

	/*----------------------------------------------------------
	* Nom: TableDefinitionManager
	*
	* Description:
	* Cette méthode est le seul et unique constructeur de la classe. Elle a une
	* visibilité privée afin de faire partie du design pattern Singleton, de
	* sorte qu'elle ne puisse être appelée que par une méthode (statique) de la
	* classe elle-même (voir la méthode getInstance()).
	* Cette méthode construit l'objet table de Hash qui sera utilisé comme cache
	* des définitions déjà chargées.
	* ----------------------------------------------------------*/
	private TableDefinitionManager()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "TableDefinitionManager");

		trace_methods.beginningOfMethod();
		// Création de la table des définitions
		_definitions = new Hashtable();
		_listeners = new Vector();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: fireTableDefinitionAdded
	* 
	* Description:
	* Cette méthode permet de transmettre une notification d'ajout d'une 
	* définition de table dans le cache à toutes les interfaces 
	* TableDefinitionListener s'étant enregistrées.
	* Elle appelle la méthode tableDefinitionAdded() pour chaque interface.
	* 
	* Arguments:
	*  - agentName: L'Agent sur lequel la définition a été chargée,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: Le chemin absolu du dictionnaire de la table.
 	* ----------------------------------------------------------*/
 	private void fireTableDefinitionAdded(
 		String agentName,
 		String iClesName,
 		String serviceType,
 		String definitionFilePath
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "fireTableDefinitionAdded");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		// On va appeler la méthode pour chaque interface de la liste
		for(int index = 0 ; index < _listeners.size() ; index ++)
		{
			TableDefinitionListener listener =
				(TableDefinitionListener)_listeners.elementAt(index);
			listener.tableDefinitionAdded(agentName, iClesName, serviceType,
				definitionFilePath);
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: fireTableDefinitionRemoved
	* 
	* Description:
	* Cette méthode permet de transmettre une notification de suppression 
	* d'une définition de table dans le cache à toutes les interfaces 
	* TableDefinitionListener s'étant enregistrées.
	* Elle appelle la méthode tableDefinitionRemoved() pour chaque interface.
	* 
	* Arguments:
	*  - agentName: L'Agent sur lequel la définition a été chargée,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: Le chemin absolu du dictionnaire de la table.
 	* ----------------------------------------------------------*/
	private void fireTableDefinitionRemoved(
 		String agentName,
		String iClesName,
		String serviceType,
 		String definitionFilePath
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "fireTableDefinitionRemoved");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		// On va appeler la méthode pour chaque interface de la liste
		for(int index = 0 ; index < _listeners.size() ; index ++)
		{
			TableDefinitionListener listener =
				(TableDefinitionListener)_listeners.elementAt(index);
			listener.tableDefinitionRemoved(agentName, iClesName, serviceType,
				definitionFilePath);
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: fireTableDefinitionUseChanged
	*
	* Description:
	* Cette méthode permet de transmettre une notification de modification du 
	* nombre d'utilisations d'une définition de table dans le cache à toutes 
	* les interfaces TableDefinitionListener s'étant enregistrées.
	* Elle appelle la méthode tableDefinitionUseChanged() pour chaque 
	* interface.
	* 
	* Arguments:
	*  - agentName: L'Agent sur lequel la définition a été chargée,
	*  - iClesName: le nom du I-CLES auquel est apparenté le dictionnaire,
	*  - serviceType: le type du service auquel est apparenté le dictionnaire,
	*  - definitionFilePath: Le chemin absolu du dictionnaire de la table,
	*  - uses: Le nombre d'utilisation de la définition.
 	* ----------------------------------------------------------*/
	private void fireTableDefinitionUseChanged(
 		String agentName,
		String iClesName,
		String serviceType,
 		String definitionFilePath,
 		int uses
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "fireTableDefinitionUseChanged");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("definitionFilePath=" + definitionFilePath);
		trace_arguments.writeTrace("uses=" + uses);
		// On va appeler la méthode pour chaque interface de la liste
		for(int index = 0 ; index < _listeners.size() ; index ++)
		{
			TableDefinitionListener listener =
				(TableDefinitionListener)_listeners.elementAt(index);
			listener.tableDefinitionUseChanged(agentName, iClesName, 
				serviceType, definitionFilePath, uses);
		}
		trace_methods.endOfMethod();
 	}
}