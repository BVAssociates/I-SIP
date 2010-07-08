/*------------------------------------------------------------
* Copyright (c) 2002 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/ServiceSessionProxy.java,v $
* $Revision: 1.17 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de manipulation des sessions de service
* DATE:        15/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ServiceSessionProxy.java,v $
* Revision 1.17  2009/01/14 14:21:58  tz
* Prise en compte de la modification des packages.
*
* Revision 1.16  2007/08/03 15:27:32  tz
* Suppression de '*' dans la liste des colonnes lors d'une sélection.
*
* Revision 1.15  2006/11/09 11:57:15  tz
* Correction de la méthode buildDirectoryList().
*
* Revision 1.14  2006/11/03 10:27:10  tz
* Ajout des méthodes getFileSystemRoots() et getDirectoryEntries().
*
* Revision 1.13  2006/10/13 15:07:50  tz
* Gestion du mode de fonctionnement de l'Agent.
*
* Revision 1.12  2005/10/07 08:42:45  tz
* Suppression du order_by systématique
*
* Revision 1.11  2005/07/06 10:03:33  tz
* Correction d'un bug dans getWideSelectResult().
*
* Revision 1.10  2005/07/05 15:08:14  tz
* Utilisation de searchFile() dans getTableDefinitionFilePath(),
* Ajout des méthodes getExecutableFilePath(), getDataFilePath(),
* getControlPanelFilePath(), getFileReader(), getFileWriter(),
* getExecutableDirectories(), getDictionaryDirectories(), getDataDirectories(),
* getControlPanelDirectories(), buildDirectoryList().
*
* Revision 1.9  2005/07/01 12:28:23  tz
* Modification du composant pour les traces
*
* Revision 1.8  2004/11/23 15:45:47  tz
* Adaptation pour corba-R1_1_1-AL-1_1.
*
* Revision 1.7  2004/11/09 15:28:17  tz
* Ajout de la méthode getWideSelectResult() et de la classe
* embarquée SelectExecutionListener.
*
* Revision 1.6  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.5  2004/07/29 12:22:52  tz
* Utilisation de ICles* au lieu de icles*
* Mise à jour de la documentation
*
* Revision 1.4  2003/12/08 14:37:21  tz
* Mise à jour du modèle
*
* Revision 1.3  2003/03/07 16:22:54  tz
* Prise en compte du mécanisme de log métier
*
* Revision 1.2  2002/04/05 15:47:03  tz
* Cloture itération IT1.2
*
* Revision 1.1  2002/03/27 09:41:17  tz
* Modification pour prise en compte nouvel IDL
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.com;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.config.ConfigurationAPI;
import com.bv.core.util.UtilStringTokenizer;
import java.util.Vector;

//
// Imports du projet
//
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.ExecutionContextInterface;
import com.bv.isis.corbacom.ExecutionListenerInterface;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisMethod;
import com.bv.isis.corbacom.ExecutionListenerInterfaceHelper;
import com.bv.isis.corbacom.ExecutionListenerInterfaceOperations;
import com.bv.isis.corbacom.SearchTypeEnum;
import com.bv.isis.corbacom.FileReaderInterface;
import com.bv.isis.corbacom.FileWriterInterface;
import com.bv.isis.corbacom.FileSystemEntry;
import com.bv.isis.corbacom.EntryTypeEnum;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.AgentLayerAbstractor;
import com.bv.isis.console.impl.com.ExecutionListenerImpl;

/*----------------------------------------------------------
* Nom: ServiceSessionProxy
*
* Description:
* Cette classe est une classe technique chargée de la centralisation des
* requêtes sur une session de service ouverte au préalable. Elle permet
* d'effectuer des requêtes sur une session de service sans avoir à gérer les
* multiples exceptions qui peuvent être levées lors de ces requêtes (elles
* sont toutes converties en InnerException, voir la classe CommonFeatures).
* Elle permet également de transformer la liste indexée représentant le
* contexte d'un objet en un tableau d'IsisParameter, nécessaire à la
* plupart des requêtes (via la classe CommonFeatures).
* ----------------------------------------------------------*/
public class ServiceSessionProxy
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ServiceSessionProxy
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle permet de fournir à
	* l'instance une référence sur l'interface ServiceSessionInterface à
	* travers laquelle toutes les requêtes seront envoyées.
	*
	* Si la session n'est pas valide, l'exception InnerException est levée.
	*
	* Arguments:
	*  - serviceSession: Une référence sur la session de service à utiliser
	*    pour les requêtes.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public ServiceSessionProxy(
		ServiceSessionInterface serviceSession
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "ServiceSessionProxy");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		// On vérifie la validité de l'argument
		if(serviceSession == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Argument non valide: " + serviceSession);
			// L'argument n'est pas valide
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", "serviceSession",
				null);
		}
		// On enregistre la référence
		_serviceSession = serviceSession;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: openServiceSession
	*
	* Description:
	* Cette méthode permet d'ouvrir une session de service sur un service
	* "serviceName" appartenant au I-CLES "iClesName", à partir d'une session 
	* de service déjà ouverte. Elle a le rôle de relais avec la méthode de 
	* même nom de l'interface ServiceSessionInterface.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Arguments:
	*  - actionId: L'identifiant de l'action,
	*  - iClesName: Nom du I-CLES auquel appartient le service,
	*  - serviceName: Le nom du service sur lequel on veut ouvrir une session,
	*  - serviceType: Le type du service.
	*
	* Retourne: Une référence sur l'interface ServiceSessionInterface
	* représentant la nouvelle session de service.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public ServiceSessionInterface openServiceSession(
		String actionId,
		String iClesName,
		String serviceName,
		String serviceType
		)
		throws
			InnerException
	{
		ServiceSessionInterface service_session = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "openServiceSession");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceName=" + serviceName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		// On tente d'ouvrir la session
		try
		{
			service_session = _serviceSession.openServiceSession(actionId,
				iClesName, serviceName, serviceType);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de l'ouverture de session
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de l'ouverture de session de service", exception);
		}
		trace_methods.endOfMethod();
		return service_session;
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette méthode permet de fermer une session de service préalablement
	* ouverte. Elle a le rôle de relais avec la méthode de même nom de
	* l'interface ServiceSessionInterface. La fermeture d'une session de
	* service est nécessaire afin de libérer les ressources allouées par
	* celle-ci du côté Agent.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent sur lequel la session est fermée,
	*  - serviceName: Le nom du service sur lequel la session est fermée,
	*  - serviceType: Le type du service sur lequel la session est fermée,
	*  - iClesName: Le nom du I-CLES sur lequel la session est fermée.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public void close(
		String agentName,
		String serviceName,
		String serviceType,
		String iClesName
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "close");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("serviceName=" + serviceName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("iClesName=" + iClesName);
	    String user_name = PasswordManager.getInstance().getUserName();
		// On va générer un log
		String action_id = LogServiceProxy.getActionIdentifier(agentName,
			"&LOG_ServiceSessionClosing", null, serviceName, serviceType,
			iClesName);
		// On génère le premier log
		String[] message_data = new String[4];
		message_data[0] =
			MessageManager.getMessage("&LOG_ServiceSessionClosing");
		message_data[1] = MessageManager.getMessage("&LOG_AgentName") +
			agentName;
		message_data[2] = MessageManager.getMessage("&LOG_UserName") +
			user_name;
		message_data[3] = MessageManager.getMessage("&LOG_ServiceSessionId") +
			getSessionIdentifier();
		LogServiceProxy.addMessageForAction(action_id, message_data);
		// On tente de fermer la session
		try
		{
			_serviceSession.close(action_id);
			// On génère le log de bonne fermeture
			message_data = new String[2];
			message_data[0] =
				MessageManager.getMessage("&LOG_SessionClosingResult");
			message_data[1] =
				MessageManager.getMessage("&LOG_SessionClosingSuccessfull");
			LogServiceProxy.addMessageForAction(action_id, message_data);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la fermeture de session
			trace_methods.endOfMethod();
			InnerException inner_exception = CommonFeatures.processException(
				"de la fermeture de la session de service", exception);
			// On récupère le tableau du détail de l'erreur
			String[] error_message = CommonFeatures.buildArrayFromString(
				inner_exception.getDetails());
			message_data = new String[error_message.length + 2];
			message_data[0] =
				MessageManager.getMessage("&LOG_SessionClosingResult");
			message_data[1] =
				MessageManager.getMessage("&LOG_SessionClosingFailed");
			for(int index = 0 ; index < error_message.length ; index ++)
			{
				message_data[index + 2] = error_message[index];
			}
			// On log le message
			LogServiceProxy.addMessageForAction(action_id, message_data);
			throw inner_exception;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTableDefinition
	*
	* Description:
	* Cette méthode permet de récupérer la définition d'une table (voir
	* IsisTableDefinition), à partir d'une session de service déjà ouverte.
	* Elle a le rôle de relais avec la méthode de même nom de l'interface
	* ServiceSessionInterface.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Arguments:
	*  - tableName: Nom de la table dont on souhaite récupérer la définition,
	*  - context: Une référence sur une liste indexée contenant le contexte de
	*    recherche du dictionnaire.
	*
	* Retourne: Une référence sur un objet IsisTableDefinition contenant la
	* définition de la table.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public IsisTableDefinition getTableDefinition(
		String tableName,
		IndexedList context
		)
		throws
			InnerException
	{
		IsisTableDefinition table_definition = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getTableDefinition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("context=" + context);
		// On convertit le contexte en environnement
		IsisParameter[] environment =
			CommonFeatures.buildParametersArray(context);
		// On tente de récupérer la définition de la table
		try
		{
			table_definition = _serviceSession.getTableDefinition(tableName,
				environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération de la définition
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération de la définition", exception);
		}
		trace_methods.endOfMethod();
		return table_definition;
	}

	/*----------------------------------------------------------
	* Nom: getSelectResult
	*
	* Description:
	* Cette méthode permet de récupérer le flux brut de résultat d'exécution
	* d'un Select, à partir d'une session de service déjà ouverte. Elle a le
	* rôle de relais avec la méthode de même nom de l'interface
	* ServiceSessionInterface.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Arguments:
	*  - tableName: Nom de la table sur laquelle le Select doit être exécuté,
	*  - selectedColumns: Un tableau de chaînes contenant la liste des colonnes
	*    sélectionnées,
	*  - condition: La condition de sélection des données (filtre) sous forme
	*    de chaîne pré-construite (sans clause Where),
	*  - sortOrder: L'ordre de tri des données sous forme de chaîne
	*    pré-construite,
	*  - context: Une référence sur une liste indexée contenant le contexte du
	*    noeud à partir duquel la session est ouverte.
	*
	* Retourne: Un tableau de chaîne de caractères correspondant au flux brut
	* de résultat d'exécution du Select.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String[] getSelectResult(
		String tableName,
		String[] selectedColumns,
		String condition,
		String sortOrder,
		IndexedList context
		)
		throws
			InnerException
	{
		String[] select_result = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getSelectResult");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("selectedColumns=" + selectedColumns);
		trace_arguments.writeTrace("condition=" + condition);
		trace_arguments.writeTrace("sortOrder=" + sortOrder);
		trace_arguments.writeTrace("context=" + context);
		// On convertit le contexte en environnement
		IsisParameter[] environment =
			CommonFeatures.buildParametersArray(context);
		// On tente de récupérer le résultat du Select
		try
		{
			select_result = _serviceSession.getSelectResult(tableName,
				selectedColumns, condition, sortOrder, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de l'exécution du Select
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de l'exécution du Select", exception);
		}
		trace_methods.endOfMethod();
		return select_result;
	}

	/*----------------------------------------------------------
	* Nom: getWideSelectResult
	* 
	* Description:
	* Cette méthode permet de récupérer le flux brut de résultat d'exécution 
	* d'un Select sur un large jeu de données, à partir d'une session de 
	* service déjà ouverte. Elle crée une instance de SelectExecutionListener, 
	* construit la commande de sélection, demande un contexte d'exécution (via 
	* la méthode getExecutionContext()), puis lance l'exécution de la commande 
	* de sélection. Si la valeur de retour est différente de 0 ou 201, une 
	* exception est levée. Sinon, le contenu du buffer de sortie standard est 
	* converti en tableau de chaînes.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - agentName: Nom de l'Agent sur lequel le Select doit être exécuté,
	*  - tableName: Nom de la table sur laquelle le Select doit être exécuté,
	*  - selectedColumns: Un tableau de chaînes contenant la liste des 
	*    colonnes sélectionnées,
	*  - condition: La condition de sélection des données (filtre) sous forme 
	*    de chaîne pré-construite (sans clause Where),
	*  - sortOrder: L'ordre de tri des données sous forme de chaîne 
	*    pré-construite,
	*  - context: Une référence sur une liste indexée contenant le contexte du 
	*    noeud à partir duquel la session est ouverte.
	* 
	* Retourne: Un tableau de chaîne de caractères correspondant au flux brut 
	* de résultat d'exécution du Select.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String[] getWideSelectResult(
		String agentName,
		String tableName,
		String[] selectedColumns,
		String condition,
		String sortOrder,
		IndexedList context
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getWideSelectResult");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String[] select_result = null;
		String agent_layer_mode = null;
		String command_pattern_parameter = null;
		String select_command_pattern = "Select '%1' FROM %2";
		String where_clause_pattern = "WHERE \"%3\"";
		String order_clause_pattern = "ORDER_BY %4";
		String default_sort_statement = "_NONE_";
		StringBuffer selected_columns = new StringBuffer("");
		StringBuffer complete_pattern = new StringBuffer();
		String select_command = null;
		Vector select_lines = new Vector();
		String sort_order = sortOrder;
		String line_separator;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("selectedColumns=" + selectedColumns);
		trace_arguments.writeTrace("condition=" + condition);
		trace_arguments.writeTrace("sortOrder=" + sortOrder);
		trace_arguments.writeTrace("context=" + context);
		// On récupère le mode de fonctionnement de l'Agent
		agent_layer_mode = 
			AgentSessionManager.getInstance().getAgentLayerMode(agentName);
		command_pattern_parameter = "SelectCommand.Pattern." +
			agent_layer_mode;
		trace_debug.writeTrace("command_pattern_parameter=" +
			command_pattern_parameter); 
		// On tente de récupérer les paramètres de configuration
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			configuration.useSection("I-TOOLS");
			select_command_pattern = 
				configuration.getString(command_pattern_parameter);
			where_clause_pattern = 
				configuration.getString("WhereClause.Pattern");
			order_clause_pattern =
				configuration.getString("OrderClause.Pattern");
			default_sort_statement =
				configuration.getString("DefaultSortStatement");
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la récupération de la " +
				"configuration: " + exception);
			// On continue quand même
		}
		trace_debug.writeTrace("select_command_pattern=" + 
			select_command_pattern);
		trace_debug.writeTrace("where_clause_pattern=" + 
			where_clause_pattern);
		trace_debug.writeTrace("order_clause_pattern=" +
			order_clause_pattern);
		trace_debug.writeTrace("defaut_sort_statement=" + 
			default_sort_statement);
		// On constitue la liste des colonnes sélectionnées
		if(selectedColumns != null && selectedColumns.length != 0)
		{
			for(int index = 0 ; index < selectedColumns.length ; index ++)
			{
				if(index > 0)
				{
					selected_columns.append(',');
				}
				selected_columns.append(selectedColumns[index]);
			}
		}
		// On va créer la commande
		complete_pattern.append(select_command_pattern);
		// Si la condition existe, on la place dans la commande
		if(condition != null && condition.equals("") == false)
		{
			complete_pattern.append(' ');
			complete_pattern.append(where_clause_pattern);
		}
		// S'il y a un ordre de tri, on le place dans la commande
		if(sort_order != null && sort_order.equals("") == false)
		{
			complete_pattern.append(' ');
			complete_pattern.append(order_clause_pattern);
		}
		else
		{
			// Y a-t-il une chaîne permettant de définir un tri
			// par défaut
			if(default_sort_statement != null &&
				default_sort_statement.equals("") == false &&
				default_sort_statement.equals("_NONE_") == false)
			{
				complete_pattern.append(' ');
				complete_pattern.append(default_sort_statement);
			}
		}
		String[] extra_informations = {
			selected_columns.toString(),
			tableName,
			condition,
			sort_order
		};
		select_command = 
			MessageManager.fillInMessage(complete_pattern.toString(), 
			extra_informations);
		trace_debug.writeTrace("select_command=" + select_command);
		// On va créer une instance de SelectExecutionListener pour
		// suivre l'exécution
		SelectExecutionListener select_listener = 
			new SelectExecutionListener();
		// On va créer une instance de ExecutionListenerImpl
		ExecutionListenerImpl execution_listener = new ExecutionListenerImpl();
		execution_listener.setExecutionListener(select_listener);
		// On crée une référence sur le listener d'exécution
		ExecutionListenerInterface listener =
			ExecutionListenerInterfaceHelper.narrow(
			IORFinder.servantToReference(execution_listener));
		// On va demander un contexte d'exécution
		ExecutionContextInterface select_context = 
			getExecutionContext("avoid", listener, select_command, context);
		// On lance l'exécution
		try
		{
			// On va passer une taille de buffer de 10 ko
			select_context.execute("avoid", 10240);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération du contexte
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de l'exécution du Select", exception);
		}
		// On va s'assurer que la procédure s'est terminée (on ne sait jamais)
		while(select_listener.getExitValue() == -1)
		{
			try
			{
				Thread.sleep(50);
			}
			catch(Exception exception)
			{
				// On ne fait rien
			}
		}
		// On vérifie que l'exécution est correcte
		if(select_listener.isExecutionOk() == false)
		{
			String error = select_listener.getErrorBuffer().toString();
			trace_errors.writeTrace("Erreur lors de l'exécution du Select: " +
				error);
			// On va lever une exception
			throw new InnerException("&ERR_CannotExecuteWideSelect", error, 
				null);
		}
		// On va lire le contenu du buffer de sortie standard
		String output =  select_listener.getOutputBuffer().toString();
		trace_debug.writeTrace("output='" + output + "'");
		trace_debug.writeTrace("error='" + 
			select_listener.getErrorBuffer().toString() + "'");
		if(agent_layer_mode.equalsIgnoreCase("Windows") == true)
		{
			line_separator = "\r\n";
		}
		else
		{
			line_separator = "\n";
		}
		while(true)
		{
			int position = output.indexOf(line_separator);
			if(position == -1)
			{
				if(output.equals("") == false)
				{
					select_lines.add(output);
				}
				break;
			}
			String line = output.substring(0, position);
			output = output.substring(position + line_separator.length());
			select_lines.add(line);
		}
		select_result = (String[])select_lines.toArray(new String[0]);
		trace_methods.endOfMethod();
		return select_result;
	}

	/*----------------------------------------------------------
	* Nom: getExecutionContext
	*
	* Description:
	* Cette méthode permet de récupérer le contexte d'exécution d'une commande,
	* à partir d'une session de service déjà ouverte. Elle a le rôle de relais
	* avec la méthode de même nom de l'interface ServiceSessionInterface.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Arguments:
	*  - actionId: L'identifiant de l'action,
	*  - listener: Une référence sur une interface ExecutionListenerInterface,
	*  - command: La commande complète à exécuter,
	*  - context: Une référence sur une liste indexée contenant le contexte du
	*    noeud à partir duquel la session est ouverte.
	*
	* Retourne: Une référence sur l'interface ExecutionContextInterface
	* permettant d'interagir avec la commande à exécuter.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public ExecutionContextInterface getExecutionContext(
		String actionId,
		ExecutionListenerInterface listener,
		String command,
		IndexedList context
		)
		throws
			InnerException
	{
		ExecutionContextInterface execution_context = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getExecutionContext");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		//trace_arguments.writeTrace("listener=" + listener);
		trace_arguments.writeTrace("command=" + command);
		trace_arguments.writeTrace("context=" + context);
		// On convertit le contexte en environnement
		IsisParameter[] environment =
			CommonFeatures.buildParametersArray(context);
		// On tente de récupérer le contexte d'exécution
		try
		{
			execution_context = _serviceSession.getExecutionContext(actionId,
				listener, command, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération du contexte
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération du contexte", exception);
		}
		trace_methods.endOfMethod();
		return execution_context;
	}

	/*----------------------------------------------------------
	* Nom: getEnvironment
	*
	* Description:
	* Cette méthode permet de récupérer l'environnement de la session de
	* service ouverte. Elle a le rôle de relais avec la méthode de même nom de
	* l'interface ServiceSessionInterface.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Retourne: Un tableau d'IsisParameter contenant tout l'environnement
	* spécifique à la session de service.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public IsisParameter[] getEnvironment()
		throws InnerException
	{
		IsisParameter[] environment = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getEnvironment");

		trace_methods.beginningOfMethod();
		// On tente de récupérer l'environnement
		try
		{
			environment = _serviceSession.getEnvironment();
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération de l'environnement
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération de l'environnement", exception);
		}
		trace_methods.endOfMethod();
		return environment;
	}

	/*----------------------------------------------------------
	* Nom: getExecutableFilePath
	* 
	* Description:
	* Cette méthode permet de récupérer le chemin d'un exécutable, à partir 
	* d'une session de service déjà ouverte. Elle a le rôle de relais avec la 
	* méthode searchFile() de l'interface ServiceSessionInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - executableName: Le nom de l'exécutable dont on souhaite connaître le 
	*    chemin,
	*  - context: Une référence sur une liste indexée contenant le contexte du 
	*    noeud à partir duquel la session est ouverte.
	* 
	* Retourne: Une chaîne contenant le chemin complet de l'exécutable.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String getExecutableFilePath(
		String executableName,
		IndexedList context
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getExecutableFilePath");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String file_path = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("executableName=" + executableName);
		trace_arguments.writeTrace("context=" + context);
		// On convertit le contexte en environnement
		IsisParameter[] environment =
			CommonFeatures.buildParametersArray(context);
		try
		{
			// On tente de récupérer le chemin de l'exécutable
			file_path = _serviceSession.searchFile(executableName,
				SearchTypeEnum.SEARCH_FOR_BIN, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération du chemin
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération du chemin de l'exécutable", exception);
		}
		trace_methods.endOfMethod();
		return file_path;
	}

	/*----------------------------------------------------------
	* Nom: getDefinitionFilePath
	* 
	* Description:
	* Cette méthode permet de récupérer le chemin du dictionnaire d'une table, 
	* à partir d'une session de service déjà ouverte. Elle a le rôle de relais 
	* avec la méthode searchFile() de l'interface ServiceSessionInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - tableName: Le nom de la table pour laquelle on souhaite connaître le 
	*    chemin du dictionnaire,
	*  - context: Une référence sur une liste indexée contenant le contexte du 
	*    noeud à partir duquel la session est ouverte.
	* 
	* Retourne: Une chaîne contenant le chemin complet du dictionnaire.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String getDefinitionFilePath(
		String tableName,
		IndexedList context
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getDefinitionFilePath");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String file_path = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("context=" + context);
		// On convertit le contexte en environnement
		IsisParameter[] environment =
			CommonFeatures.buildParametersArray(context);
		try
		{
			// On tente de récupérer le chemin du dictionnaire
			file_path = _serviceSession.searchFile(tableName,
				SearchTypeEnum.SEARCH_FOR_DEF, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération du chemin
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération du chemin du dictionnaire", exception);
		}
		trace_methods.endOfMethod();
		return file_path;
	}

	/*----------------------------------------------------------
	* Nom: getDataFilePath
	* 
	* Description:
	* Cette méthode permet de récupérer le chemin du fichier de données d'une 
	* table, à partir d'une session de service déjà ouverte. Elle a le rôle de 
	* relais avec la méthode searchFile() de l'interface 
	* ServiceSessionInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - tableName: Le nom de la table pour laquelle on souhaite connaître le 
	*    chemin du fichier de données,
	*  - context: Une référence sur une liste indexée contenant le contexte du 
	*    noeud à partir duquel la session est ouverte.
	* 
	* Retourne: Une chaîne contenant le chemin complet du fichier de données.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String getDataFilePath(
		String tableName,
		IndexedList context
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getDataFilePath");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String file_path = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("context=" + context);
		// On convertit le contexte en environnement
		IsisParameter[] environment =
			CommonFeatures.buildParametersArray(context);
		try
		{
			// On tente de récupérer le chemin du fichier de données
			file_path = _serviceSession.searchFile(tableName,
				SearchTypeEnum.SEARCH_FOR_TAB, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération du chemin
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération du chemin du fichier de données", 
				exception);
		}
		trace_methods.endOfMethod();
		return file_path;
	}

	/*----------------------------------------------------------
	* Nom: getControlPanelFilePath
	* 
	* Description:
	* Cette méthode permet de récupérer le chemin du panneau de commandes 
	* d'une table, à partir d'une session de service déjà ouverte. Elle a le 
	* rôle de relais avec la méthode searchFile() de l'interface 
	* ServiceSessionInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - tableName: Le nom de la table pour laquelle on souhaite connaître le 
	*    chemin du panneau de commandes,
	*  - context: Une référence sur une liste indexée contenant le contexte du 
	*    noeud à partir duquel la session est ouverte.
	* 
	* Retourne: Une chaîne contenant le chemin complet du panneau de commandes.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String getControlPanelFilePath(
		String tableName,
		IndexedList context
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getControlPanelFilePath");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String file_path = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("context=" + context);
		// On convertit le contexte en environnement
		IsisParameter[] environment =
			CommonFeatures.buildParametersArray(context);
		try
		{
			// On tente de récupérer le chemin du panneau de commandes
			file_path = _serviceSession.searchFile(tableName,
				SearchTypeEnum.SEARCH_FOR_PCI, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération du chemin
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération du chemin du panneau de commandes", 
				exception);
		}
		trace_methods.endOfMethod();
		return file_path;
	}

	/*----------------------------------------------------------
	* Nom: getMethods
	*
	* Description:
	* Cette méthode permet de récupérer toutes les méthodes associées à une
	* table, à partir d'une session de service déjà ouverte. Elle a le rôle de
	* relais avec la méthode de même nom de l'interface ServiceSessionInterface.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Arguments:
	*  - tableName: Le nom de la table pour laquelle on souhaite récupérer les
	*    méthodes,
	*  - context: Une référence sur une liste indexée contenant le contexte du
	*    noeud à partir duquel la session est ouverte.
	*
	* Retourne: Un tableau d'IsisMethod contenant toutes les méthodes de la
	* table.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public IsisMethod[] getMethods(
		String tableName,
		IndexedList context
		)
		throws
			InnerException
	{
		IsisMethod[] methods = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getMethods");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("context=" + context);
		// On convertit le contexte en environnement
		IsisParameter[] environment =
			CommonFeatures.buildParametersArray(context);
		// On tente de récupérer les méthodes
		try
		{
			methods = _serviceSession.getMethods(tableName, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération des méthodes
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération des méthodes", exception);
		}
		trace_methods.endOfMethod();
		return methods;
	}

	/*----------------------------------------------------------
	* Nom: getUserResponsabilities
	*
	* Description:
	* Cette méthode permet de récupérer les responsabilités de l'utilisateur
	* vis à vis de la session de service ouverte. Elle a le rôle de relais avec
	* la méthode de même nom de l'interface ServiceSessionInterface.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Retourne: Un tableau de chaînes contenant l'ensemble des responsabilités
	* de l'utilisateur.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String[] getUserResponsabilities()
		throws
			InnerException
	{
		String[] responsabilities = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getUserResponsabilities");

		trace_methods.beginningOfMethod();
		// On tente de récupérer les responsabilités
		try
		{
			responsabilities = _serviceSession.getUserResponsabilities();
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération des responsabilités
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération des responsabilités", exception);
		}
		trace_methods.endOfMethod();
		return responsabilities;
	}

	/*----------------------------------------------------------
	* Nom: getSessionIdentifier
	*
	* Description:
	* Cette méthode permet de récupérer le numéro unique de session. Elle a le
	* rôle de relais avec la méthode de même nom de l'interface
	* ServiceSessionInterface.
	*
	* Retourne: Le numéro unique de session, sous forme de chaîne de caractères.
	* ----------------------------------------------------------*/
	public String getSessionIdentifier()
	{
		String session_id = "undefined";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getSessionIdentifier");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// On tente de récupérer le numéro de session
		try
		{
			session_id = _serviceSession.getSessionIdentifier();
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération du numéro de session
			trace_errors.writeTrace(
				"Erreur lors de la récupération du numéro de session: " +
				exception);
		}
		trace_methods.endOfMethod();
		return session_id;
	}

	/*----------------------------------------------------------
	* Nom: getFileReader
	* 
	* Description:
	* Cette méthode permet de récupérer une interface de lecture de fichier 
	* FileReaderInterface permettant la lecture du fichier dont le chemin est 
	* passé en argument. Elle a le rôle de relais avec la méthode de même nom 
	* de l'interface ServiceSessionInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - fileName: Le chemin du fichier pour lequel on souhaite récupérer une 
	*    interface de lecture.
	* 
	* Retourne: Une référence sur l'interface FileReaderInterface permettant 
	* la lecture du fichier.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public FileReaderInterface getFileReader(
		String fileName
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getFileReaderInterface");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		FileReaderInterface reader = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("fileName=" + fileName);
		try
		{
			// On tente de récupérer l'interface de lecture
			reader = _serviceSession.getFileReader(fileName);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération de l'interface
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération de l'interface de lecture", exception);
		}
		trace_methods.endOfMethod();
		return reader;
	}

	/*----------------------------------------------------------
	* Nom: getFileWriter
	* 
	* Description:
	* Cette méthode permet de récupérer une interface d'écriture de fichier 
	* FileWriterInterface permettant l'écriture du fichier dont le chemin est 
	* passé en argument. Elle a le rôle de relais avec la méthode de même nom 
	* de l'interface ServiceSessionInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - fileName: Le chemin du fichier pour lequel on souhaite récupérer une 
	*    interface d'écriture.
	* 
	* Retourne: Une référence sur l'interface FileWriterInterface permettant 
	* l'écriture du fichier.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public FileWriterInterface getFileWriter(
		String fileName
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getFileWriterInterface");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		FileWriterInterface writer = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("fileName=" + fileName);
		try
		{
			// On tente de récupérer l'interface d'écriture
			writer = _serviceSession.getFileWriter(fileName);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération de l'interface
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération de l'interface d'écriture", exception);
		}
		trace_methods.endOfMethod();
		return writer;
	}

	/*----------------------------------------------------------
	* Nom: getFileSystemRoots
	* 
	* Description:
	* Cette méthode permet de récupérer un tableau d'objets FileSystemEntry 
	* correspondant à l'ensemble des racines du système de fichiers d'un 
	* Agent ou du Portail.
	* Elle a le rôle de relais avec la méthode de même nom de l'interface 
	* ServiceSessionInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est 
	* levée.
	* 
	* Retourne: Un tableau de FileSystemEntry contenant l'ensemble des 
	* racines du système de fichiers de la plate-forme distante.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public FileSystemEntry[] getFileSystemRoots()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getFileSystemRoots");
		FileSystemEntry[] roots = null;

		trace_methods.beginningOfMethod();
		try
		{
			// On tente de récupérer les racines du système de fichiers
			roots = _serviceSession.getFileSystemRoots();
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération de l'interface
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération des racines du système de fichiers", 
				exception);
		}
		trace_methods.endOfMethod();
		return roots;
	}

	/*----------------------------------------------------------
	* Nom: getDirectoryEntries
	* 
	* Description:
	* Cette méthode permet de récupérer un tableau d'objets FileSystemEntry 
	* correspondant à l'ensemble des entrées du répertoire, dont le nom est 
	* passé en argument, éventuellement filtré suivant le type passé en 
	* argument.
	* Elle a le rôle de relais avec la méthode de même nom de l'interface 
	* ServiceSessionInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - directoryName: Le nom du répertoire dont on souhaite récupérer les 
	*    entrées,
	*  - entryType: Le type d'entrée que l'on souhaite récupérer.
	* 
	* Retourne: Un tableau de FileSystemEntry contenant l'ensemble des entrées 
	* du répertoire du système de fichiers de la plate-forme distante.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public FileSystemEntry[] getDirectoryEntries(
		String directoryName,
		EntryTypeEnum entryType
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getDirectoryEntries");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		FileSystemEntry[] entries = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("directoryName=" + directoryName);
		trace_arguments.writeTrace("entryType=" + entryType);
		try
		{
			// On tente de récupérer les entrées du répertoire
			entries = _serviceSession.getDirectoryEntries(directoryName,
				entryType);
		}
		catch(Exception exception)
		{
			// Il y a eu un problème lors de la récupération de l'interface
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération des entrées du répertoire " +
				directoryName, exception);
		}
		trace_methods.endOfMethod();
		return entries;
	}

	/*----------------------------------------------------------
	* Nom: getExecutableDirectories
	* 
	* Description:
	* Cette méthode permet de récupérer la liste des répertoires pouvant 
	* contenir des fichiers exécutables.
	* La liste des répertoires est extraite de l'environnement du service, via 
	* la méthode getEnvironment(), en utilisant le paramètre de configuration 
	* "I-TOOLS.ExecutablesVariable".
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent.
	*
	* Retourne: Un tableau de chaînes de caractères contenant les répertoires 
	* de recherche des fichiers exécutables pour la session.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String[] getExecutableDirectories(
		String agentLayerMode
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getExecutableDirectories");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String executables_variable = "PATH";
		String[] directories = null;
		
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		try
		{
			// On commence par récupérer la valeur du paramètre de 
			// configuration
			ConfigurationAPI configuration = new ConfigurationAPI();
			executables_variable = configuration.getString("I-TOOLS",
				"ExecutablesVariable");
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de la récupération du " +
				"paramètre de configuration: " + e.getMessage());
			// On continue avec la valeur par défaut
		}
		// On construit la liste des répertoires
		directories = buildDirectoryList(executables_variable, agentLayerMode,
			true);
		trace_methods.endOfMethod();
		return directories;
	}

	/*----------------------------------------------------------
	* Nom: getDictionaryDirectories
	* 
	* Description:
	* Cette méthode permet de récupérer la liste des répertoires pouvant 
	* contenir des dictionnaires I-TOOLS.
	* La liste des répertoires est extraite de l'environnement du service, via 
	* la méthode getEnvironment(), en utilisant le paramètre de configuration 
	* "I-TOOLS.DictionariesVariable".
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent.
	* 
	* Retourne: Un tableau de chaînes de caractères contenant les répertoires 
	* de recherche des dictionnaires I-TOOLS pour la session.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String[] getDictionaryDirectories(
		String agentLayerMode
		)
		throws 
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getDictionaryDirectories");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String dictionaries_variable = "BV_DEFPATH";
		String[] directories = null;
		
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		try
		{
			// On commence par récupérer la valeur du paramètre de 
			// configuration
			ConfigurationAPI configuration = new ConfigurationAPI();
			dictionaries_variable = configuration.getString("I-TOOLS",
				"DictionariesVariable");
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de la récupération du " +
				"paramètre de configuration: " + e.getMessage());
			// On continue avec la valeur par défaut
		}
		// On construit la liste des répertoires
		directories = buildDirectoryList(dictionaries_variable, 
			agentLayerMode, false);
		trace_methods.endOfMethod();
		return directories;
	}

	/*----------------------------------------------------------
	* Nom: getDataDirectories
	* 
	* Description:
	* Cette méthode permet de récupérer la liste des répertoires pouvant 
	* contenir des fichiers de données des tables I-TOOLS.
	* La liste des répertoires est extraite de l'environnement du service, via 
	* la méthode getEnvironment(), en utilisant le paramètre de configuration 
	* "I-TOOLS.DataFilesVariable".
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent.
	* 
	* Retourne: Un tableau de chaînes de caractères contenant les répertoires 
	* de recherche des fichiers de données pour la session.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String[] getDataDirectories(
		String agentLayerMode
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getDataDirectories");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String files_variable = "BV_TABPATH";
		String[] directories = null;
		
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		try
		{
			// On commence par récupérer la valeur du paramètre de 
			// configuration
			ConfigurationAPI configuration = new ConfigurationAPI();
			files_variable = configuration.getString("I-TOOLS",
				"DataFilesVariable");
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de la récupération du " +
				"paramètre de configuration: " + e.getMessage());
			// On continue avec la valeur par défaut
		}
		// On construit la liste des répertoires
		directories = buildDirectoryList(files_variable, agentLayerMode,
			false);
		trace_methods.endOfMethod();
		return directories;
	}

	/*----------------------------------------------------------
	* Nom: getControlPanelDirectories
	* 
	* Description:
	* Cette méthode permet de récupérer la liste des répertoires pouvant 
	* contenir des panneaux de commandes I-SIS.
	* La liste des répertoires est extraite de l'environnement du service, via 
	* la méthode getEnvironment(), en utilisant le paramètre de configuration 
	* "I-TOOLS.ControlPanelsVariable".
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent.
	* 
	* Retourne: Un tableau de chaînes de caractères contenant les répertoires 
	* de recherche des panneaux de commandes pour la session.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String[] getControlPanelDirectories(
		String agentLayerMode
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getControlPanelDirectories");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String panels_variable = "BV_PCIPATH";
		String[] directories = null;
		
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		try
		{
			// On commence par récupérer la valeur du paramètre de 
			// configuration
			ConfigurationAPI configuration = new ConfigurationAPI();
			panels_variable = configuration.getString("I-TOOLS",
				"ControlPanelsVariable");
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de la récupération du " +
				"paramètre de configuration: " + e.getMessage());
			// On continue avec la valeur par défaut
		}
		// On construit la liste des répertoires
		directories = buildDirectoryList(panels_variable, agentLayerMode,
			false);
		trace_methods.endOfMethod();
		return directories;
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: finalize
	*
	* Description:
	* Cette méthode est automatiquement appelée par le ramasse miettes de la
	* machine virtuelle java lorsqu'un objet est sur le point d'être détruit.
	* Elle permet de libérer toutes les ressources allouées par une instance.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "finalize");

		trace_methods.beginningOfMethod();
		// On libère la référence sur l'interface
		_serviceSession = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: SelectExecutionListener
	* 
	* Description:
	* Cette classe est une classe technique chargée de permettre l'exécution 
	* de sélection dans des tables présentant de larges données. Pour cela, 
	* au lieu d'utiliser la méthode getSelectResult() de l'interface 
	* ServiceSessionInterface, il est nécessaire de passer par un contexte 
	* d'exécution.
	* Pour cela, la classe implémente l'interface 
	* ExecutionListenerInterfaceOperations, afin de permettre son utilisation 
	* avec la classe ExecutionListenerImpl.
	* ----------------------------------------------------------*/
	private class SelectExecutionListener
		implements ExecutionListenerInterfaceOperations
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: SelectExecutionListener
		* 
		* Description:
		* Cette méthode est le constructeur par défaut. Elle n'est présentée 
		* que pour des raisons de lisibilité.
		* ----------------------------------------------------------*/
		public SelectExecutionListener()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "SelectExecutionListener");

			trace_methods.beginningOfMethod();
			_outputBuffer = new StringBuffer();
			_errorBuffer = new StringBuffer();
			_exitValue = -1;
			trace_methods.endOfMethod();
		}

		/*----------------------------------------------------------
		* Nom: receiveDataFromOutputStream
		* 
		* Description:
		* Cette méthode redéfinit celle de l'interface 
		* ExecutionListenerInterfaceOperations. Elle est appelée lorsque des 
		* données ont été émises par la procédure sur sa sortie standard.
		* La méthode ajoute les données au buffer de sortie standard.
		* 
		* Arguments:
		*  - data: Des données émises sur la sortie standard de la procédure.
 		* ----------------------------------------------------------*/
 		public void receiveDataFromOutputStream(
 			String data
 			)
 		{
			/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "receiveDataFromOutputStream");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("data=" + data);*/
			_outputBuffer.append(data);
			//trace_methods.endOfMethod();
 		}

		/*----------------------------------------------------------
		* Nom: receiveDataFromErrorStream
		* 
		* Description:
		* Cette méthode redéfinit celle de l'interface 
		* ExecutionListenerInterfaceOperations. Elle est appelée lorsque des 
		* données ont été émises par la procédure sur sa sortie d'erreur.
		* La méthode ajoute les données au buffer de sortie d'erreur.
		* 
		* Arguments:
		*  - data: Des données émises sur la sortie d'erreur de la procédure.
 		* ----------------------------------------------------------*/
 		public void receiveDataFromErrorStream(
 			String data
 			)
 		{
			/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "receiveDataFromErrorStream");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("data=" + data);*/
			_errorBuffer.append(data);
			//trace_methods.endOfMethod();
 		}

		/*----------------------------------------------------------
		* Nom: executionTerminated
		* 
		* Description:
		* Cette méthode redéfinit celle de l'interface 
		* ExecutionListenerInterfaceOperations. Elle est appelée lorsque la 
		* procédure a terminé son exécution.
		* La méthode stocke la valeur de retour de la procédure dans 
		* l'attribut correspondant.
		* 
		* Arguments:
		*  - exitValue: La valeur de retour de la procédure.
 		* ----------------------------------------------------------*/
 		public void executionTerminated(
 			int exitValue
 			)
 		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "executionTerminated");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");

			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("exitValue=" + exitValue);
			trace_debug.writeTrace("_outputBuffer='" + _outputBuffer + "'");
			trace_debug.writeTrace("_errorBuffer='" + _errorBuffer + "'");
			_exitValue = exitValue;
			trace_methods.endOfMethod();
 		}

		/*----------------------------------------------------------
		* Nom: getOutputBuffer
		* 
		* Description:
		* Cette méthode permet de récupérer le contenu du buffer de la sortie 
		* standard.
		* 
		* Retourne: Le buffer de la sortie standard.
		* ----------------------------------------------------------*/
		public StringBuffer getOutputBuffer()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "getOutputBuffer");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _outputBuffer;
		}

		/*----------------------------------------------------------
		* Nom: getErrorBuffer
		* 
		* Description:
		* Cette méthode permet de récupérer le contenu du buffer de la sortie 
		* d'erreur.
		* 
		* Retourne: Le buffer de la sortie d'erreur.
		* ----------------------------------------------------------*/
		public StringBuffer getErrorBuffer()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "getErrorBuffer");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _errorBuffer;
		}

		/*----------------------------------------------------------
		* Nom: getExitValue
		* 
		* Description:
		* Cette méthode permet de récupérer la valeur de retour de la 
		* procédure.
		* 
		* Retourne: La valeur de retour de la procédure.
		* ----------------------------------------------------------*/
		public int getExitValue()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "getExitValue");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _exitValue;
		}

		/*----------------------------------------------------------
		* Nom: isExecutionOk
		* 
		* Description:
		* Cette méthode permet de savoir si l'exécution s'est bien passée ou 
		* non. Elle teste la valeur du code de retour d'exécution de la 
		* procédure. Si celui-ci vaut 0 ou 201, on considère que l'exécution 
		* s'est bien déroulée.
		* 
		* Retourne: true si l'exécution s'est bien passée, false sinon.
		* ----------------------------------------------------------*/
		public boolean isExecutionOk()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "isExecutionOk");
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			int ok_exit_value = 0;
			int warning_exit_value = 201;

			trace_methods.beginningOfMethod();
			// On tente de récupérer les paramètres de configuration
			try
			{
				ConfigurationAPI configuration = new ConfigurationAPI();
				configuration.useSection("I-TOOLS");
				ok_exit_value = configuration.getInt("ReturnCode.Ok");
				warning_exit_value = configuration.getInt("ReturnCode.Warning");
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace("Erreur lors de la récupération de la " +
					"configuration: " + exception);
				// On continue quand même
			}
			trace_methods.endOfMethod();
			return (_exitValue == ok_exit_value || 
				_exitValue == warning_exit_value);
		}

		// ****************** PROTEGE *********************
		/*----------------------------------------------------------
		* Nom: finalize
		*
		* Description:
		* Cette méthode est automatiquement appelée par le ramasse miettes de la
		* machine virtuelle java lorsqu'un objet est sur le point d'être détruit.
		* Elle permet de libérer toutes les ressources allouées par une instance.
		* ----------------------------------------------------------*/
		protected void finalize()
			throws
				Throwable
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "finalize");

			trace_methods.beginningOfMethod();
			_outputBuffer = null;
			_errorBuffer = null;
			trace_methods.endOfMethod();
		}

		// ******************* PRIVE **********************
		/*----------------------------------------------------------
		* Nom: _exitValue
		* 
		* Description:
		* Cet attribut est chargé de stocker la valeur de retour de la 
		* procédure.
		* ----------------------------------------------------------*/
		private int _exitValue;

		/*----------------------------------------------------------
		* Nom: _outputBuffer
		* 
		* Description:
		* Cet attribut maintient une référence sur un objet StringBuffer 
		* destiné à contenir les données qui ont été émises par la procédure 
		* sur sa sortie standard.
		* ----------------------------------------------------------*/
		private StringBuffer _outputBuffer;

		/*----------------------------------------------------------
		* Nom: _errorBuffer
		* 
		* Description:
		* Cet attribut maintient une référence sur un objet StringBuffer 
		* destiné à contenir les données qui ont été émises par la procédure 
		* sur sa sortie d'erreur.
		* ----------------------------------------------------------*/
		private StringBuffer _errorBuffer;
	}
	
	/*----------------------------------------------------------
	* Nom: _serviceSession
	*
	* Description:
	* Cet attribut maintient une référence sur une interface
	* ServiceSessionInterface à travers laquelle toutes les requêtes seront
	* transmises à un Agent.
	* ----------------------------------------------------------*/
	private ServiceSessionInterface _serviceSession;

	/*----------------------------------------------------------
	* Nom: buildDirectoryList
	* 
	* Description:
	* Cette méthode est chargée de construire une liste de répertoires à 
	* partir de la valeur du paramètre I-SIS extrait de l'environnement de la 
	* session de service, dont le nom est passé en argument.
	* La liste est construite en découpant la valeur du paramètre suivant le 
	* séparateur ':'.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - parameterName: Le nom du paramètre d'où extraire la liste des 
	*    répertoires,
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent,
	*  - tryToGuessFirst: Un booléen indiquant si la méthode doit chercher le 
	*    caractère de séparation (true) ou non (false).
	* 
	* Retourne: Un tableau de chaînes de caractères contenant les répertoires 
	* contenu dans la valeur du paramètre dont le nom est passé en argument.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	private String[] buildDirectoryList(
		String parameterName,
		String agentLayerMode,
		boolean tryToGuessFirst
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "buildDirectoryList");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String[] directories = null;
		String path_separator = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parameterName=" + parameterName);
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		trace_arguments.writeTrace("tryToGuessFirst=" + tryToGuessFirst);
		// On commence par récupérer l'environnement de la session de service
		IsisParameter[] service_environment = getEnvironment();
		// On va rechercher le paramètre correspondant au nom passé en 
		// argument
		for(int index = 0 ; index < service_environment.length ; index ++)
		{
			IsisParameter parameter = service_environment[index]; 
			if(parameter.name.equals(parameterName) == true)
			{
				trace_debug.writeTrace("Paramètre trouvé dans l'environnement");
				trace_debug.writeTrace("Valeur: " + parameter.value);
				if(tryToGuessFirst == true)
				{
					String value_start = parameter.value.substring(1, 3);
					if(value_start.equals(":\\") == true ||
						value_start.equals(":/") == true)
					{
						// C'est le seul cas où on est sûr d'être sur 
						// Windows
						path_separator = ";";
					}
				}
				if(path_separator == null)
				{
					path_separator = AgentLayerAbstractor.getPathSeparator(
						agentLayerMode);
				}
				// On va découper la valeur à partir du séparateur ':'
				UtilStringTokenizer tokenizer = 
					new UtilStringTokenizer(parameter.value, path_separator);
				// On va instancier le tableau de chaînes à partir du nombre de 
				// sous-chaînes
				directories = new String[tokenizer.getTokensCount()];
				// On va remplir la liste des répertoires
				int loop = 0;
				while(tokenizer.hasMoreTokens() == true)
				{
					directories[loop] = tokenizer.nextToken();
					trace_debug.writeTrace("Répertoire: " + directories[loop]);
					loop ++;
				}
				// On peut sortir
				trace_methods.endOfMethod();
				return directories;
			}
		}
		trace_errors.writeTrace("Paramètre non trouvé dans l'environnement");
		// On lève une exception
		trace_methods.endOfMethod();
		throw new InnerException("&ERR_ParameterNotInEnvironment", 
			parameterName, null);
	}
}