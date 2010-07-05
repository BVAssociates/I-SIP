/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/TableDefinitionManager.java,v $
* $Revision: 1.16 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des d�finitions des tables
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
* Remplacement d'une trace d'erreur en une trace de d�bug.
*
* Revision 1.14  2006/11/09 11:57:33  tz
* Suppression du patch pour les I-TOOLS Windows.
*
* Revision 1.13  2006/10/13 15:08:15  tz
* Remplacement du s�parateur dans la construction de la cl� de la table
* de hash.
*
* Revision 1.12  2005/10/07 08:42:09  tz
* Ajout des donn�es relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.11  2005/07/01 12:28:08  tz
* Modification du composant pour les traces
*
* Revision 1.10  2004/11/02 09:11:24  tz
* Gestion des leasings des d�finitions.
*
* Revision 1.9  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.8  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.7  2004/07/29 12:22:24  tz
* Utilisation de Portal* au lieu de Master*
* Mise � jour de la documentation
*
* Revision 1.6  2003/12/08 14:37:09  tz
* Mise � jour du mod�le
*
* Revision 1.5  2002/11/19 08:45:56  tz
* Mise en cache des d�finitions du Ma�tre avec son vrai nom d'h�te.
*
* Revision 1.4  2002/04/05 15:47:02  tz
* Cloture it�ration IT1.2
*
* Revision 1.3  2002/03/27 09:41:00  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2001/12/19 09:59:03  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1  2001/11/14 17:17:34  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.com;

//
// Imports syst�me
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
* Cette classe est une classe technique charg�e de la r�cup�ration et de la
* mise en cache des d�finitions des tables qui sont utilis�es par l'interface
* graphique.
* La classe utilise le design pattern Singleton afin d'�tre accessible par
* toute classe de l'application de mani�re identique.
* La mise en cache des d�finitions des tables est n�cessaire afin de r�duire au
* maximum les �changes entre l'interface graphique et le Portail. En effet, il
* n'est pas concevable de devoir rechercher sur la plate-forme Portail la
* d�finition d'une table � chaque fois qu'on en a besoin, cela prendrait
* beaucoup trop de temps et consommerait beaucoup trop de ressources r�seau.
*
* Les d�finitions d�j� charg�es sont sock�es dans une table de Hash, dont la
* cl� est constitu�e du nom de la plate-forme sur laquelle la table a �t�
* r�cup�r�e, et du chemin complet de cette table sur la dite plate-forme. Cela
* permet de stocker des d�finitions de tables d�finies sur un m�me plate-forme,
* ayant le m�me nom, mais ne provenant pas du m�me fichier (I-CLES diff�rent, 
* par exemple).
* ----------------------------------------------------------*/
public class TableDefinitionManager
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getInstance
	*
	* Description:
	* Cette m�thode statique fait partie du design pattern Singleton. Elle
	* permet de r�cup�rer l'unique instance de la classe TableDefinitionManager.
	* Si cette instance n'existe pas, la m�thode va la cr�er.
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
			trace_debug.writeTrace("Cr�ation de l'instance de TableDefinitionManager");
			_instance = new TableDefinitionManager();
		}
		trace_methods.endOfMethod();
		return _instance;
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	*
	* Description:
	* Cette m�thode statique permet de lib�rer les ressources allou�es pendant
	* l'ex�cution de l'application.
	* Elle lib�re toutes les r�f�rences des d�finitions des tables ayant �t�
	* mises en cache, et l'objet de table de Hash servant � contenir le cache.
	* Elle lib�re �galement l'unique instance de TableDefinitionManager.
	* ----------------------------------------------------------*/
	public static void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "cleanBeforeExit");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		if(_instance != null)
		{
			trace_debug.writeTrace("Lib�ration des r�f�rences");
			// Lib�ration des r�f�rences
			_instance._definitions.clear();
			_instance._definitions = null;
			_instance._listeners.clear();
			_instance._listeners = null;
			// Lib�ration de l'instance
			_instance = null;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTableDefinition
	*
	* Description:
	* Cette m�thode permet de r�cup�rer la d�finition d'une table sur une
	* plate-forme. Cette m�thode doit �tre appel�e pour une table dont la
	* d�finition n'a pas �t� charg�e au pr�alable (sinon, il vaut mieux
	* utiliser l'autre m�thode getTableDefinition()).
	* Cette m�thode va tout d'abord r�cup�rer le chemin complet du fichier
	* dictionnaire de la table (via la m�thode getDefinitionFilePath() de la
	* classe SessionServiceProxy) et regarder dans le cache si cette d�finition 
	* n'a pas d�j� �t� charg�e.
	* Si ce n'est pas le cas, la m�thode va r�cup�rer cette d�finition via la
	* m�thode getTableDefinition() de la classe ServiceSessionProxy, et
	* l'ajouter au cache.
	* La cl� du cache est compos�e du nom de l'Agent, du nom du I-CLES, du 
	* type du I-CLES et enfin du chemin du dictionnaire.
	*
	* Si, pour une raison ou pour une autre, la r�cup�ration de la d�finition
	* de la table n'est pas possible, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - agentName: le nom de la plate-forme agent sur laquelle la d�finition
	*    doit �tre r�cup�r�e,
	*  - iClesName: le nom du I-CLES sur lequel la session a �t� ouverte,
	*  - serviceType: le type du service sur lequel la session a �t� ouverte,
	*  - tableName: le nom de la table dont la d�finition doit �tre r�cup�r�e,
	*  - context: une r�f�rence sur une liste index�e contenant le contexte
	*    permettant d'acc�der � la d�finition de la table,
	*  - serviceSession: une r�f�rence sur l'interface ServiceSessionInterface
	*    repr�sentant la session de service � utiliser pour la requ�te.
	*
	* Retourne: Une r�f�rence sur un object IsisTableDefinition contenant la
	* d�finition de la table.
	*
	* L�ve: InnerException.
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
		// Cr�ation de l'instance de ServiceSessionProxy
		ServiceSessionProxy session_proxy =
			new ServiceSessionProxy(serviceSession);
		// La premi�re chose est de r�cup�rer le chemin du dictionnaire de la
		// table concern�e.
		definition_file_path = session_proxy.getDefinitionFilePath(tableName,
			context);
		trace_debug.writeTrace("Chemin du dictionnaire: " +
			definition_file_path);
		// Si l'agent est null, on va utiliser Portal
		if(agentName == null)
		{
			agentName = "Portal";
		}
		// On v�rifie si cette table existe dans le cache
		String cache_key = agentName + ";" + iClesName + ";" + serviceType +
			";" + definition_file_path;
		trace_debug.writeTrace("cache_key=" + cache_key);
		if(_definitions.containsKey(cache_key) == true)
		{
			trace_debug.writeTrace("La d�finition est en cache");
			// La d�finition existe d�j� en m�moire, on r�cup�re l'instance
			leasing_holder = (ObjectLeasingHolder)_definitions.get(cache_key);
			leasing_holder.addLeasing();
			definition = (IsisTableDefinition)leasing_holder.getLeasedObject();
			// On va d�clencher un �v�nement
			fireTableDefinitionUseChanged(agentName, iClesName, serviceType,
				definition_file_path, leasing_holder.getNumberOfLeasings());
		}
		else
		{
			trace_debug.writeTrace("La d�finition n'est pas en cache");
			// La d�finition n'est pas dans le cache, il faut la r�cup�rer
			// sur le serveur.
			definition = session_proxy.getTableDefinition(tableName, context);
			trace_debug.writeTrace("Ajout de la d�finition en cache");
			// On l'ajoute au cache
			leasing_holder = new ObjectLeasingHolder(definition);
			_definitions.put(cache_key, leasing_holder);
			// On va d�clencher un �v�nement
			fireTableDefinitionAdded(agentName, iClesName, serviceType, 
				definition_file_path);
		}
		trace_debug.writeTrace("Nombre d'utilisation de la d�finition " + 
			cache_key + ": " + leasing_holder.getNumberOfLeasings());
		trace_methods.endOfMethod();
		return definition;
	}

	/*----------------------------------------------------------
	* Nom: getTableDefinition
	*
	* Description:
	* Cette m�thode permet de r�cup�rer la d�finition d'une table ayant d�j� �t�
	* charg�e. La d�finition de la table est r�cup�r�e depuis le cache en
	* fonction du nom de la plate-forme, du nom du I-CLES, du type du service 
	* et du chemin complet du fichier sur celle-ci.
	*
	* Arguments:
	*  - agentName: le nom de la plate-forme sur laquelle la d�finition a d�
	*    �tre r�cup�r�e,
	*  - iClesName: le nom du I-CLES auquel est apparent� le dictionnaire,
	*  - serviceType: le type du service auquel est apparent� le dictionnaire,
	*  - definitionFilePath: le chemin complet du fichier dictionnaire de la
	*    table sur la plate-forme.
	*
	* Retourne: Une r�f�rence sur un object IsisTableDefinition contenant la
	* d�finition de la table, ou null.
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
		// On v�rifie si cette table existe dans le cache
		String cache_key = agentName + ";" + iClesName + ";" + serviceType +
			";" + definitionFilePath;
		trace_debug.writeTrace("cache_key=" + cache_key);
		if(_definitions.containsKey(cache_key) == true)
		{
			trace_debug.writeTrace("La d�finition est en cache");
			// La d�finition existe d�j� en m�moire, on r�cup�re l'instance
			ObjectLeasingHolder leasing_holder =
				(ObjectLeasingHolder)_definitions.get(cache_key);
			leasing_holder.addLeasing();
			definition = (IsisTableDefinition)leasing_holder.getLeasedObject();
			// On va d�clencher un �v�nement
			fireTableDefinitionUseChanged(agentName, iClesName, serviceType,
				definitionFilePath, leasing_holder.getNumberOfLeasings());
			trace_debug.writeTrace("Nombre d'utilisation de la d�finition " +
				cache_key + ": " + leasing_holder.getNumberOfLeasings());
		}
		else
		{
			trace_debug.writeTrace("La d�finition n'est pas en cache");
		}
		trace_methods.endOfMethod();
		return definition;
	}

	/*----------------------------------------------------------
	* Nom: getTableDefinitionKeys
	* 
	* Description:
	* Cette m�thode permet � une classe de r�cup�rer l'ensemble des cl�s du 
	* cache des d�finitions des tables. Les cl�s sont constitu�es � partir du 
	* nom de l'Agent et du chemin absolu du dictionnaire sur l'Agent avec la 
	* syntaxe suivante:
	* <Agent>:<Chemin>.
	* 
	* Retourne: Une r�f�rence sur une Enumeration contenant toutes les cl�s du 
	* cache des d�finitions.
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
	* Cette m�thode permet de r�cup�rer l'objet ObjectLeasingHolder associ� � 
	* la cl� pass�e en argument. Si la cl� n'existe pas dans le cache, une 
	* r�f�rence nulle est retourn�e.
	* 
	* Arguments:
	*  - key: La cl� de l'objet ObjectLeasingHolder � r�cup�rer.
	* 
	* Retourne: Une r�f�rence sur l'objet ObjectLeasingHolder ou null.
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
		// On regarde si la cl� existe
		if(_definitions.containsKey(key) == false)
		{
			trace_errors.writeTrace("La cl� n'existe pas !");
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
	* Cette m�thode permet de lib�rer une utilisation de la d�finition pass�e 
	* en argument. Il est n�cessaire d'appeler cette m�thode lorsqu'une 
	* d�finition de table n'est plus utilis�e de sorte � permettre, via un 
	* processeur graphique, de lib�rer les d�finitions qui ne sont plus 
	* utilis�es.
	* 
	* Arguments:
	*  - tableDefinition: Une r�f�rence sur la d�finition de table dont il 
	*    faut lib�rer une utilisation.
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
				trace_debug.writeTrace("La d�finition a �t� trouv�e dans le " +
					"cache, lib�ration d'un leasing");
				leasing_holder.releaseLeasing();
				trace_debug.writeTrace("Nombre d'utilisations de la " +
					"d�finition " + key + ": " + leasing_holder.getNumberOfLeasings());
				// On va d�clencher un �v�nement
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
		trace_errors.writeTrace("La d�finition n'a pas �t� trouv�e en cache");
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: releaseTableDefinitionLeasing
	* 
	* Description:
	* Cette m�thode permet de lib�rer une utilisation de la d�finition pass�e 
	* en argument. Il est n�cessaire d'appeler cette m�thode lorsqu'une 
	* d�finition de table n'est plus utilis�e de sorte � permettre, via un 
	* processeur graphique, de lib�rer les d�finitions qui ne sont plus 
	* utilis�es.
	* 
	* Arguments:
	*  - agentName: le nom de la plate-forme sur laquelle la d�finition a d� 
	*    �tre r�cup�r�e,
	*  - iClesName: le nom du I-CLES auquel est apparent� le dictionnaire,
	*  - serviceType: le type du service auquel est apparent� le dictionnaire,
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
		// On construit la cl�
		cache_key = agentName + ";" + iClesName + ";" + serviceType +
			";" + definitionFilePath;
		// La d�finition est-elle en cache
		if(_definitions.containsKey(cache_key) == false)
		{
			if(definitionFilePath == null || 
				definitionFilePath.equals("") == true) {
				trace_debug.writeTrace("La d�finition n'est pas en cache");
			}
			else {
				trace_errors.writeTrace("La d�finition n'est pas en cache");
			}
			trace_methods.endOfMethod();
			return;
		}
		ObjectLeasingHolder leasing_holder = 
			(ObjectLeasingHolder)_definitions.get(cache_key);
		trace_debug.writeTrace("La d�finition a �t� trouv�e dans le " +
			"cache, lib�ration d'un leasing");
		leasing_holder.releaseLeasing();
		trace_debug.writeTrace("Nombre d'utilisations de la " +
			"d�finition " + cache_key + ": " + leasing_holder.getNumberOfLeasings());
		// On va d�clencher un �v�nement
		fireTableDefinitionUseChanged(agentName, iClesName, serviceType, 
			definitionFilePath, leasing_holder.getNumberOfLeasings());
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: registerListener
	* 
	* Description:
	* Cette m�thode permet d'ajouter un objet TableDefinitionListener � la 
	* liste des receveurs de notifications sur les ajouts/suppressions de 
	* d�finitions de tables.
	* 
	* Arguments:
	*  - listener: Une r�f�rence sur l'interface TableDefinitionListener � 
	*    ajouter � la liste.
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
	* Cette m�thode permet de supprimer un objet TableDefinitionListener de la 
	* liste des receveurs de notifications sur les ajouts/suppressions de 
	* d�finitions de tables.
	* 
	* Arguments:
	*  - listener: Une r�f�rence sur l'interface TableDefinitionListener � 
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
	* Cette m�thode permet de lib�rer la d�finition d'une table ayant �t� 
	* pr�alablement charg�e. La d�finition, si elle existe, est supprim�e de 
	* la liste des d�finitions en cache.
	* 
	* Si la d�finition � lib�rer n'a pas un nombre d'utilisations �gal � 0, 
	* l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - agentName: le nom de la plate-forme sur laquelle la d�finition a d� 
	*    �tre r�cup�r�e,
	*  - iClesName: le nom du I-CLES auquel est apparent� le dictionnaire,
	*  - serviceType: le type du service auquel est apparent� le dictionnaire,
	*  - definitionFilePath: le chemin complet du fichier dictionnaire de la 
	*    table sur la plate-forme.
	* 
	* L�ve: InnerException.
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
		// On v�rifie si cette table existe dans le cache
		String cache_key = agentName + ";" + iClesName + ";" + serviceType +
			";" + definitionFilePath;
		if(_definitions.containsKey(cache_key) == false)
		{
			trace_errors.writeTrace("La d�finition n'est pas en cache !");
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException(
				MessageManager.getMessage("&ERR_NoDefinitionForTable"),
				cache_key, null);
		}
		trace_debug.writeTrace("La d�finition est en cache");
		// La d�finition existe d�j� en m�moire, on r�cup�re l'instance
		ObjectLeasingHolder leasing_holder =
			(ObjectLeasingHolder)_definitions.get(cache_key);
		// On v�rifie que l'objet n'est plus utilis�
		if(leasing_holder.isFreeOfLeasing() == false)
		{
			trace_errors.writeTrace("La d�finition est encore utilis�e !");
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException(
				MessageManager.getMessage("&ERR_DefinitionStillInUse"),
				cache_key, null);
		}
		// On supprime la d�finition
		_definitions.remove(cache_key);
		// On va d�clencher l'�v�nement de suppression
		fireTableDefinitionRemoved(agentName, iClesName, serviceType,
			definitionFilePath);
		trace_methods.endOfMethod();
	}


	/*----------------------------------------------------------
	* Nom: clear
	* 
	* Description:
	* Cette m�thode permet de supprimer tous les dictionnaires en cache, avec 
	* notification des interfaces en �coute pour chaque suppression.
	* Elle est destin�e � �tre appel�e lors de l'ouverture d'une session sur 
	* un nouveau Portail.
	* ----------------------------------------------------------*/
	public synchronized void clear()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "dumpTableDefinition");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// On va r�cup�rer la liste des cl�s de cache
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
	* Cet attribut statique contient la r�f�rence de la seule et unique instance
	* de TableDefinitionManager. Cette instance est cr��e lors du premier appel
	* de la m�thode getInstance(), et est lib�r�e par l'appel de la m�thode
	* cleanBeforeExit().
	* ----------------------------------------------------------*/
	private static TableDefinitionManager _instance = null;

	/*----------------------------------------------------------
	* Nom: _definitions
	*
	* Description:
	* Cet attribut contient une r�f�rence sur une table de Hash qui est utilis�
	* pour stocker localement les d�finitions des tables ayant �t� charg�es. Il
	* impl�mente le cache des d�finitions.
	* La cl� de cette table de Hash est constitu� du nom de la plate-forme sur 
	* laquelle la d�finition a �t� r�cup�r�e, du nom du I-CLES, du type du 
	* service et du chemin complet d'acc�s au fichier dictionnaire de la table, 
	* ces �l�ments �tant s�par�s par le caract�re ':'.
	* La valeur de cette table de Hash est une instance d'ObjectLeasingHolder 
	* permettant de conna�tre le nombre d'utilisations des d�finitions de table.
	* ----------------------------------------------------------*/
	private Hashtable _definitions;

	/*----------------------------------------------------------
	* Nom: _listeners
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet Vector contenant la 
	* liste des interface TableDefinitionListener s'�tant enregistr�es pour 
	* recevoir les notifications d'ajout ou de suppression de d�finitions de 
	* tables.
	* ----------------------------------------------------------*/
	private Vector _listeners;

	/*----------------------------------------------------------
	* Nom: TableDefinitionManager
	*
	* Description:
	* Cette m�thode est le seul et unique constructeur de la classe. Elle a une
	* visibilit� priv�e afin de faire partie du design pattern Singleton, de
	* sorte qu'elle ne puisse �tre appel�e que par une m�thode (statique) de la
	* classe elle-m�me (voir la m�thode getInstance()).
	* Cette m�thode construit l'objet table de Hash qui sera utilis� comme cache
	* des d�finitions d�j� charg�es.
	* ----------------------------------------------------------*/
	private TableDefinitionManager()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TableDefinitionManager", "TableDefinitionManager");

		trace_methods.beginningOfMethod();
		// Cr�ation de la table des d�finitions
		_definitions = new Hashtable();
		_listeners = new Vector();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: fireTableDefinitionAdded
	* 
	* Description:
	* Cette m�thode permet de transmettre une notification d'ajout d'une 
	* d�finition de table dans le cache � toutes les interfaces 
	* TableDefinitionListener s'�tant enregistr�es.
	* Elle appelle la m�thode tableDefinitionAdded() pour chaque interface.
	* 
	* Arguments:
	*  - agentName: L'Agent sur lequel la d�finition a �t� charg�e,
	*  - iClesName: le nom du I-CLES auquel est apparent� le dictionnaire,
	*  - serviceType: le type du service auquel est apparent� le dictionnaire,
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
		// On va appeler la m�thode pour chaque interface de la liste
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
	* Cette m�thode permet de transmettre une notification de suppression 
	* d'une d�finition de table dans le cache � toutes les interfaces 
	* TableDefinitionListener s'�tant enregistr�es.
	* Elle appelle la m�thode tableDefinitionRemoved() pour chaque interface.
	* 
	* Arguments:
	*  - agentName: L'Agent sur lequel la d�finition a �t� charg�e,
	*  - iClesName: le nom du I-CLES auquel est apparent� le dictionnaire,
	*  - serviceType: le type du service auquel est apparent� le dictionnaire,
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
		// On va appeler la m�thode pour chaque interface de la liste
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
	* Cette m�thode permet de transmettre une notification de modification du 
	* nombre d'utilisations d'une d�finition de table dans le cache � toutes 
	* les interfaces TableDefinitionListener s'�tant enregistr�es.
	* Elle appelle la m�thode tableDefinitionUseChanged() pour chaque 
	* interface.
	* 
	* Arguments:
	*  - agentName: L'Agent sur lequel la d�finition a �t� charg�e,
	*  - iClesName: le nom du I-CLES auquel est apparent� le dictionnaire,
	*  - serviceType: le type du service auquel est apparent� le dictionnaire,
	*  - definitionFilePath: Le chemin absolu du dictionnaire de la table,
	*  - uses: Le nombre d'utilisation de la d�finition.
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
		// On va appeler la m�thode pour chaque interface de la liste
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