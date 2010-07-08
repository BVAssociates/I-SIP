/*------------------------------------------------------------
* Copyright (c) 2002 par BV Associates. Tous droits r�serv�s.
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
* Suppression de '*' dans la liste des colonnes lors d'une s�lection.
*
* Revision 1.15  2006/11/09 11:57:15  tz
* Correction de la m�thode buildDirectoryList().
*
* Revision 1.14  2006/11/03 10:27:10  tz
* Ajout des m�thodes getFileSystemRoots() et getDirectoryEntries().
*
* Revision 1.13  2006/10/13 15:07:50  tz
* Gestion du mode de fonctionnement de l'Agent.
*
* Revision 1.12  2005/10/07 08:42:45  tz
* Suppression du order_by syst�matique
*
* Revision 1.11  2005/07/06 10:03:33  tz
* Correction d'un bug dans getWideSelectResult().
*
* Revision 1.10  2005/07/05 15:08:14  tz
* Utilisation de searchFile() dans getTableDefinitionFilePath(),
* Ajout des m�thodes getExecutableFilePath(), getDataFilePath(),
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
* Ajout de la m�thode getWideSelectResult() et de la classe
* embarqu�e SelectExecutionListener.
*
* Revision 1.6  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.5  2004/07/29 12:22:52  tz
* Utilisation de ICles* au lieu de icles*
* Mise � jour de la documentation
*
* Revision 1.4  2003/12/08 14:37:21  tz
* Mise � jour du mod�le
*
* Revision 1.3  2003/03/07 16:22:54  tz
* Prise en compte du m�canisme de log m�tier
*
* Revision 1.2  2002/04/05 15:47:03  tz
* Cloture it�ration IT1.2
*
* Revision 1.1  2002/03/27 09:41:17  tz
* Modification pour prise en compte nouvel IDL
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.com;

//
// Imports syst�me
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
* Cette classe est une classe technique charg�e de la centralisation des
* requ�tes sur une session de service ouverte au pr�alable. Elle permet
* d'effectuer des requ�tes sur une session de service sans avoir � g�rer les
* multiples exceptions qui peuvent �tre lev�es lors de ces requ�tes (elles
* sont toutes converties en InnerException, voir la classe CommonFeatures).
* Elle permet �galement de transformer la liste index�e repr�sentant le
* contexte d'un objet en un tableau d'IsisParameter, n�cessaire � la
* plupart des requ�tes (via la classe CommonFeatures).
* ----------------------------------------------------------*/
public class ServiceSessionProxy
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ServiceSessionProxy
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle permet de fournir �
	* l'instance une r�f�rence sur l'interface ServiceSessionInterface �
	* travers laquelle toutes les requ�tes seront envoy�es.
	*
	* Si la session n'est pas valide, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - serviceSession: Une r�f�rence sur la session de service � utiliser
	*    pour les requ�tes.
	*
	* L�ve: InnerException.
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
		// On v�rifie la validit� de l'argument
		if(serviceSession == null)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace("Argument non valide: " + serviceSession);
			// L'argument n'est pas valide
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", "serviceSession",
				null);
		}
		// On enregistre la r�f�rence
		_serviceSession = serviceSession;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: openServiceSession
	*
	* Description:
	* Cette m�thode permet d'ouvrir une session de service sur un service
	* "serviceName" appartenant au I-CLES "iClesName", � partir d'une session 
	* de service d�j� ouverte. Elle a le r�le de relais avec la m�thode de 
	* m�me nom de l'interface ServiceSessionInterface.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - actionId: L'identifiant de l'action,
	*  - iClesName: Nom du I-CLES auquel appartient le service,
	*  - serviceName: Le nom du service sur lequel on veut ouvrir une session,
	*  - serviceType: Le type du service.
	*
	* Retourne: Une r�f�rence sur l'interface ServiceSessionInterface
	* repr�sentant la nouvelle session de service.
	*
	* L�ve: InnerException.
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
			// Il y a eu un probl�me lors de l'ouverture de session
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
	* Cette m�thode permet de fermer une session de service pr�alablement
	* ouverte. Elle a le r�le de relais avec la m�thode de m�me nom de
	* l'interface ServiceSessionInterface. La fermeture d'une session de
	* service est n�cessaire afin de lib�rer les ressources allou�es par
	* celle-ci du c�t� Agent.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent sur lequel la session est ferm�e,
	*  - serviceName: Le nom du service sur lequel la session est ferm�e,
	*  - serviceType: Le type du service sur lequel la session est ferm�e,
	*  - iClesName: Le nom du I-CLES sur lequel la session est ferm�e.
	*
	* L�ve: InnerException.
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
		// On va g�n�rer un log
		String action_id = LogServiceProxy.getActionIdentifier(agentName,
			"&LOG_ServiceSessionClosing", null, serviceName, serviceType,
			iClesName);
		// On g�n�re le premier log
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
			// On g�n�re le log de bonne fermeture
			message_data = new String[2];
			message_data[0] =
				MessageManager.getMessage("&LOG_SessionClosingResult");
			message_data[1] =
				MessageManager.getMessage("&LOG_SessionClosingSuccessfull");
			LogServiceProxy.addMessageForAction(action_id, message_data);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la fermeture de session
			trace_methods.endOfMethod();
			InnerException inner_exception = CommonFeatures.processException(
				"de la fermeture de la session de service", exception);
			// On r�cup�re le tableau du d�tail de l'erreur
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
	* Cette m�thode permet de r�cup�rer la d�finition d'une table (voir
	* IsisTableDefinition), � partir d'une session de service d�j� ouverte.
	* Elle a le r�le de relais avec la m�thode de m�me nom de l'interface
	* ServiceSessionInterface.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - tableName: Nom de la table dont on souhaite r�cup�rer la d�finition,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte de
	*    recherche du dictionnaire.
	*
	* Retourne: Une r�f�rence sur un objet IsisTableDefinition contenant la
	* d�finition de la table.
	*
	* L�ve: InnerException.
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
		// On tente de r�cup�rer la d�finition de la table
		try
		{
			table_definition = _serviceSession.getTableDefinition(tableName,
				environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration de la d�finition
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration de la d�finition", exception);
		}
		trace_methods.endOfMethod();
		return table_definition;
	}

	/*----------------------------------------------------------
	* Nom: getSelectResult
	*
	* Description:
	* Cette m�thode permet de r�cup�rer le flux brut de r�sultat d'ex�cution
	* d'un Select, � partir d'une session de service d�j� ouverte. Elle a le
	* r�le de relais avec la m�thode de m�me nom de l'interface
	* ServiceSessionInterface.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - tableName: Nom de la table sur laquelle le Select doit �tre ex�cut�,
	*  - selectedColumns: Un tableau de cha�nes contenant la liste des colonnes
	*    s�lectionn�es,
	*  - condition: La condition de s�lection des donn�es (filtre) sous forme
	*    de cha�ne pr�-construite (sans clause Where),
	*  - sortOrder: L'ordre de tri des donn�es sous forme de cha�ne
	*    pr�-construite,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte du
	*    noeud � partir duquel la session est ouverte.
	*
	* Retourne: Un tableau de cha�ne de caract�res correspondant au flux brut
	* de r�sultat d'ex�cution du Select.
	*
	* L�ve: InnerException.
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
		// On tente de r�cup�rer le r�sultat du Select
		try
		{
			select_result = _serviceSession.getSelectResult(tableName,
				selectedColumns, condition, sortOrder, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de l'ex�cution du Select
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de l'ex�cution du Select", exception);
		}
		trace_methods.endOfMethod();
		return select_result;
	}

	/*----------------------------------------------------------
	* Nom: getWideSelectResult
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le flux brut de r�sultat d'ex�cution 
	* d'un Select sur un large jeu de donn�es, � partir d'une session de 
	* service d�j� ouverte. Elle cr�e une instance de SelectExecutionListener, 
	* construit la commande de s�lection, demande un contexte d'ex�cution (via 
	* la m�thode getExecutionContext()), puis lance l'ex�cution de la commande 
	* de s�lection. Si la valeur de retour est diff�rente de 0 ou 201, une 
	* exception est lev�e. Sinon, le contenu du buffer de sortie standard est 
	* converti en tableau de cha�nes.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - agentName: Nom de l'Agent sur lequel le Select doit �tre ex�cut�,
	*  - tableName: Nom de la table sur laquelle le Select doit �tre ex�cut�,
	*  - selectedColumns: Un tableau de cha�nes contenant la liste des 
	*    colonnes s�lectionn�es,
	*  - condition: La condition de s�lection des donn�es (filtre) sous forme 
	*    de cha�ne pr�-construite (sans clause Where),
	*  - sortOrder: L'ordre de tri des donn�es sous forme de cha�ne 
	*    pr�-construite,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte du 
	*    noeud � partir duquel la session est ouverte.
	* 
	* Retourne: Un tableau de cha�ne de caract�res correspondant au flux brut 
	* de r�sultat d'ex�cution du Select.
	* 
	* L�ve: InnerException.
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
		// On r�cup�re le mode de fonctionnement de l'Agent
		agent_layer_mode = 
			AgentSessionManager.getInstance().getAgentLayerMode(agentName);
		command_pattern_parameter = "SelectCommand.Pattern." +
			agent_layer_mode;
		trace_debug.writeTrace("command_pattern_parameter=" +
			command_pattern_parameter); 
		// On tente de r�cup�rer les param�tres de configuration
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
			trace_errors.writeTrace("Erreur lors de la r�cup�ration de la " +
				"configuration: " + exception);
			// On continue quand m�me
		}
		trace_debug.writeTrace("select_command_pattern=" + 
			select_command_pattern);
		trace_debug.writeTrace("where_clause_pattern=" + 
			where_clause_pattern);
		trace_debug.writeTrace("order_clause_pattern=" +
			order_clause_pattern);
		trace_debug.writeTrace("defaut_sort_statement=" + 
			default_sort_statement);
		// On constitue la liste des colonnes s�lectionn�es
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
		// On va cr�er la commande
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
			// Y a-t-il une cha�ne permettant de d�finir un tri
			// par d�faut
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
		// On va cr�er une instance de SelectExecutionListener pour
		// suivre l'ex�cution
		SelectExecutionListener select_listener = 
			new SelectExecutionListener();
		// On va cr�er une instance de ExecutionListenerImpl
		ExecutionListenerImpl execution_listener = new ExecutionListenerImpl();
		execution_listener.setExecutionListener(select_listener);
		// On cr�e une r�f�rence sur le listener d'ex�cution
		ExecutionListenerInterface listener =
			ExecutionListenerInterfaceHelper.narrow(
			IORFinder.servantToReference(execution_listener));
		// On va demander un contexte d'ex�cution
		ExecutionContextInterface select_context = 
			getExecutionContext("avoid", listener, select_command, context);
		// On lance l'ex�cution
		try
		{
			// On va passer une taille de buffer de 10 ko
			select_context.execute("avoid", 10240);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration du contexte
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de l'ex�cution du Select", exception);
		}
		// On va s'assurer que la proc�dure s'est termin�e (on ne sait jamais)
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
		// On v�rifie que l'ex�cution est correcte
		if(select_listener.isExecutionOk() == false)
		{
			String error = select_listener.getErrorBuffer().toString();
			trace_errors.writeTrace("Erreur lors de l'ex�cution du Select: " +
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
	* Cette m�thode permet de r�cup�rer le contexte d'ex�cution d'une commande,
	* � partir d'une session de service d�j� ouverte. Elle a le r�le de relais
	* avec la m�thode de m�me nom de l'interface ServiceSessionInterface.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - actionId: L'identifiant de l'action,
	*  - listener: Une r�f�rence sur une interface ExecutionListenerInterface,
	*  - command: La commande compl�te � ex�cuter,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte du
	*    noeud � partir duquel la session est ouverte.
	*
	* Retourne: Une r�f�rence sur l'interface ExecutionContextInterface
	* permettant d'interagir avec la commande � ex�cuter.
	*
	* L�ve: InnerException.
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
		// On tente de r�cup�rer le contexte d'ex�cution
		try
		{
			execution_context = _serviceSession.getExecutionContext(actionId,
				listener, command, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration du contexte
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration du contexte", exception);
		}
		trace_methods.endOfMethod();
		return execution_context;
	}

	/*----------------------------------------------------------
	* Nom: getEnvironment
	*
	* Description:
	* Cette m�thode permet de r�cup�rer l'environnement de la session de
	* service ouverte. Elle a le r�le de relais avec la m�thode de m�me nom de
	* l'interface ServiceSessionInterface.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Retourne: Un tableau d'IsisParameter contenant tout l'environnement
	* sp�cifique � la session de service.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public IsisParameter[] getEnvironment()
		throws InnerException
	{
		IsisParameter[] environment = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getEnvironment");

		trace_methods.beginningOfMethod();
		// On tente de r�cup�rer l'environnement
		try
		{
			environment = _serviceSession.getEnvironment();
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration de l'environnement
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration de l'environnement", exception);
		}
		trace_methods.endOfMethod();
		return environment;
	}

	/*----------------------------------------------------------
	* Nom: getExecutableFilePath
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le chemin d'un ex�cutable, � partir 
	* d'une session de service d�j� ouverte. Elle a le r�le de relais avec la 
	* m�thode searchFile() de l'interface ServiceSessionInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - executableName: Le nom de l'ex�cutable dont on souhaite conna�tre le 
	*    chemin,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte du 
	*    noeud � partir duquel la session est ouverte.
	* 
	* Retourne: Une cha�ne contenant le chemin complet de l'ex�cutable.
	* 
	* L�ve: InnerException.
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
			// On tente de r�cup�rer le chemin de l'ex�cutable
			file_path = _serviceSession.searchFile(executableName,
				SearchTypeEnum.SEARCH_FOR_BIN, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration du chemin
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration du chemin de l'ex�cutable", exception);
		}
		trace_methods.endOfMethod();
		return file_path;
	}

	/*----------------------------------------------------------
	* Nom: getDefinitionFilePath
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le chemin du dictionnaire d'une table, 
	* � partir d'une session de service d�j� ouverte. Elle a le r�le de relais 
	* avec la m�thode searchFile() de l'interface ServiceSessionInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - tableName: Le nom de la table pour laquelle on souhaite conna�tre le 
	*    chemin du dictionnaire,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte du 
	*    noeud � partir duquel la session est ouverte.
	* 
	* Retourne: Une cha�ne contenant le chemin complet du dictionnaire.
	* 
	* L�ve: InnerException.
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
			// On tente de r�cup�rer le chemin du dictionnaire
			file_path = _serviceSession.searchFile(tableName,
				SearchTypeEnum.SEARCH_FOR_DEF, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration du chemin
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration du chemin du dictionnaire", exception);
		}
		trace_methods.endOfMethod();
		return file_path;
	}

	/*----------------------------------------------------------
	* Nom: getDataFilePath
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le chemin du fichier de donn�es d'une 
	* table, � partir d'une session de service d�j� ouverte. Elle a le r�le de 
	* relais avec la m�thode searchFile() de l'interface 
	* ServiceSessionInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - tableName: Le nom de la table pour laquelle on souhaite conna�tre le 
	*    chemin du fichier de donn�es,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte du 
	*    noeud � partir duquel la session est ouverte.
	* 
	* Retourne: Une cha�ne contenant le chemin complet du fichier de donn�es.
	* 
	* L�ve: InnerException.
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
			// On tente de r�cup�rer le chemin du fichier de donn�es
			file_path = _serviceSession.searchFile(tableName,
				SearchTypeEnum.SEARCH_FOR_TAB, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration du chemin
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration du chemin du fichier de donn�es", 
				exception);
		}
		trace_methods.endOfMethod();
		return file_path;
	}

	/*----------------------------------------------------------
	* Nom: getControlPanelFilePath
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le chemin du panneau de commandes 
	* d'une table, � partir d'une session de service d�j� ouverte. Elle a le 
	* r�le de relais avec la m�thode searchFile() de l'interface 
	* ServiceSessionInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - tableName: Le nom de la table pour laquelle on souhaite conna�tre le 
	*    chemin du panneau de commandes,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte du 
	*    noeud � partir duquel la session est ouverte.
	* 
	* Retourne: Une cha�ne contenant le chemin complet du panneau de commandes.
	* 
	* L�ve: InnerException.
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
			// On tente de r�cup�rer le chemin du panneau de commandes
			file_path = _serviceSession.searchFile(tableName,
				SearchTypeEnum.SEARCH_FOR_PCI, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration du chemin
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration du chemin du panneau de commandes", 
				exception);
		}
		trace_methods.endOfMethod();
		return file_path;
	}

	/*----------------------------------------------------------
	* Nom: getMethods
	*
	* Description:
	* Cette m�thode permet de r�cup�rer toutes les m�thodes associ�es � une
	* table, � partir d'une session de service d�j� ouverte. Elle a le r�le de
	* relais avec la m�thode de m�me nom de l'interface ServiceSessionInterface.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - tableName: Le nom de la table pour laquelle on souhaite r�cup�rer les
	*    m�thodes,
	*  - context: Une r�f�rence sur une liste index�e contenant le contexte du
	*    noeud � partir duquel la session est ouverte.
	*
	* Retourne: Un tableau d'IsisMethod contenant toutes les m�thodes de la
	* table.
	*
	* L�ve: InnerException.
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
		// On tente de r�cup�rer les m�thodes
		try
		{
			methods = _serviceSession.getMethods(tableName, environment);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration des m�thodes
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration des m�thodes", exception);
		}
		trace_methods.endOfMethod();
		return methods;
	}

	/*----------------------------------------------------------
	* Nom: getUserResponsabilities
	*
	* Description:
	* Cette m�thode permet de r�cup�rer les responsabilit�s de l'utilisateur
	* vis � vis de la session de service ouverte. Elle a le r�le de relais avec
	* la m�thode de m�me nom de l'interface ServiceSessionInterface.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Retourne: Un tableau de cha�nes contenant l'ensemble des responsabilit�s
	* de l'utilisateur.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public String[] getUserResponsabilities()
		throws
			InnerException
	{
		String[] responsabilities = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getUserResponsabilities");

		trace_methods.beginningOfMethod();
		// On tente de r�cup�rer les responsabilit�s
		try
		{
			responsabilities = _serviceSession.getUserResponsabilities();
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration des responsabilit�s
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration des responsabilit�s", exception);
		}
		trace_methods.endOfMethod();
		return responsabilities;
	}

	/*----------------------------------------------------------
	* Nom: getSessionIdentifier
	*
	* Description:
	* Cette m�thode permet de r�cup�rer le num�ro unique de session. Elle a le
	* r�le de relais avec la m�thode de m�me nom de l'interface
	* ServiceSessionInterface.
	*
	* Retourne: Le num�ro unique de session, sous forme de cha�ne de caract�res.
	* ----------------------------------------------------------*/
	public String getSessionIdentifier()
	{
		String session_id = "undefined";

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "getSessionIdentifier");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// On tente de r�cup�rer le num�ro de session
		try
		{
			session_id = _serviceSession.getSessionIdentifier();
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration du num�ro de session
			trace_errors.writeTrace(
				"Erreur lors de la r�cup�ration du num�ro de session: " +
				exception);
		}
		trace_methods.endOfMethod();
		return session_id;
	}

	/*----------------------------------------------------------
	* Nom: getFileReader
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer une interface de lecture de fichier 
	* FileReaderInterface permettant la lecture du fichier dont le chemin est 
	* pass� en argument. Elle a le r�le de relais avec la m�thode de m�me nom 
	* de l'interface ServiceSessionInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - fileName: Le chemin du fichier pour lequel on souhaite r�cup�rer une 
	*    interface de lecture.
	* 
	* Retourne: Une r�f�rence sur l'interface FileReaderInterface permettant 
	* la lecture du fichier.
	* 
	* L�ve: InnerException.
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
			// On tente de r�cup�rer l'interface de lecture
			reader = _serviceSession.getFileReader(fileName);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration de l'interface
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration de l'interface de lecture", exception);
		}
		trace_methods.endOfMethod();
		return reader;
	}

	/*----------------------------------------------------------
	* Nom: getFileWriter
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer une interface d'�criture de fichier 
	* FileWriterInterface permettant l'�criture du fichier dont le chemin est 
	* pass� en argument. Elle a le r�le de relais avec la m�thode de m�me nom 
	* de l'interface ServiceSessionInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - fileName: Le chemin du fichier pour lequel on souhaite r�cup�rer une 
	*    interface d'�criture.
	* 
	* Retourne: Une r�f�rence sur l'interface FileWriterInterface permettant 
	* l'�criture du fichier.
	* 
	* L�ve: InnerException.
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
			// On tente de r�cup�rer l'interface d'�criture
			writer = _serviceSession.getFileWriter(fileName);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration de l'interface
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration de l'interface d'�criture", exception);
		}
		trace_methods.endOfMethod();
		return writer;
	}

	/*----------------------------------------------------------
	* Nom: getFileSystemRoots
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer un tableau d'objets FileSystemEntry 
	* correspondant � l'ensemble des racines du syst�me de fichiers d'un 
	* Agent ou du Portail.
	* Elle a le r�le de relais avec la m�thode de m�me nom de l'interface 
	* ServiceSessionInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est 
	* lev�e.
	* 
	* Retourne: Un tableau de FileSystemEntry contenant l'ensemble des 
	* racines du syst�me de fichiers de la plate-forme distante.
	* 
	* L�ve: InnerException.
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
			// On tente de r�cup�rer les racines du syst�me de fichiers
			roots = _serviceSession.getFileSystemRoots();
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration de l'interface
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration des racines du syst�me de fichiers", 
				exception);
		}
		trace_methods.endOfMethod();
		return roots;
	}

	/*----------------------------------------------------------
	* Nom: getDirectoryEntries
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer un tableau d'objets FileSystemEntry 
	* correspondant � l'ensemble des entr�es du r�pertoire, dont le nom est 
	* pass� en argument, �ventuellement filtr� suivant le type pass� en 
	* argument.
	* Elle a le r�le de relais avec la m�thode de m�me nom de l'interface 
	* ServiceSessionInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - directoryName: Le nom du r�pertoire dont on souhaite r�cup�rer les 
	*    entr�es,
	*  - entryType: Le type d'entr�e que l'on souhaite r�cup�rer.
	* 
	* Retourne: Un tableau de FileSystemEntry contenant l'ensemble des entr�es 
	* du r�pertoire du syst�me de fichiers de la plate-forme distante.
	* 
	* L�ve: InnerException.
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
			// On tente de r�cup�rer les entr�es du r�pertoire
			entries = _serviceSession.getDirectoryEntries(directoryName,
				entryType);
		}
		catch(Exception exception)
		{
			// Il y a eu un probl�me lors de la r�cup�ration de l'interface
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration des entr�es du r�pertoire " +
				directoryName, exception);
		}
		trace_methods.endOfMethod();
		return entries;
	}

	/*----------------------------------------------------------
	* Nom: getExecutableDirectories
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la liste des r�pertoires pouvant 
	* contenir des fichiers ex�cutables.
	* La liste des r�pertoires est extraite de l'environnement du service, via 
	* la m�thode getEnvironment(), en utilisant le param�tre de configuration 
	* "I-TOOLS.ExecutablesVariable".
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent.
	*
	* Retourne: Un tableau de cha�nes de caract�res contenant les r�pertoires 
	* de recherche des fichiers ex�cutables pour la session.
	* 
	* L�ve: InnerException.
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
			// On commence par r�cup�rer la valeur du param�tre de 
			// configuration
			ConfigurationAPI configuration = new ConfigurationAPI();
			executables_variable = configuration.getString("I-TOOLS",
				"ExecutablesVariable");
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de la r�cup�ration du " +
				"param�tre de configuration: " + e.getMessage());
			// On continue avec la valeur par d�faut
		}
		// On construit la liste des r�pertoires
		directories = buildDirectoryList(executables_variable, agentLayerMode,
			true);
		trace_methods.endOfMethod();
		return directories;
	}

	/*----------------------------------------------------------
	* Nom: getDictionaryDirectories
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la liste des r�pertoires pouvant 
	* contenir des dictionnaires I-TOOLS.
	* La liste des r�pertoires est extraite de l'environnement du service, via 
	* la m�thode getEnvironment(), en utilisant le param�tre de configuration 
	* "I-TOOLS.DictionariesVariable".
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent.
	* 
	* Retourne: Un tableau de cha�nes de caract�res contenant les r�pertoires 
	* de recherche des dictionnaires I-TOOLS pour la session.
	* 
	* L�ve: InnerException.
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
			// On commence par r�cup�rer la valeur du param�tre de 
			// configuration
			ConfigurationAPI configuration = new ConfigurationAPI();
			dictionaries_variable = configuration.getString("I-TOOLS",
				"DictionariesVariable");
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de la r�cup�ration du " +
				"param�tre de configuration: " + e.getMessage());
			// On continue avec la valeur par d�faut
		}
		// On construit la liste des r�pertoires
		directories = buildDirectoryList(dictionaries_variable, 
			agentLayerMode, false);
		trace_methods.endOfMethod();
		return directories;
	}

	/*----------------------------------------------------------
	* Nom: getDataDirectories
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la liste des r�pertoires pouvant 
	* contenir des fichiers de donn�es des tables I-TOOLS.
	* La liste des r�pertoires est extraite de l'environnement du service, via 
	* la m�thode getEnvironment(), en utilisant le param�tre de configuration 
	* "I-TOOLS.DataFilesVariable".
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent.
	* 
	* Retourne: Un tableau de cha�nes de caract�res contenant les r�pertoires 
	* de recherche des fichiers de donn�es pour la session.
	* 
	* L�ve: InnerException.
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
			// On commence par r�cup�rer la valeur du param�tre de 
			// configuration
			ConfigurationAPI configuration = new ConfigurationAPI();
			files_variable = configuration.getString("I-TOOLS",
				"DataFilesVariable");
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de la r�cup�ration du " +
				"param�tre de configuration: " + e.getMessage());
			// On continue avec la valeur par d�faut
		}
		// On construit la liste des r�pertoires
		directories = buildDirectoryList(files_variable, agentLayerMode,
			false);
		trace_methods.endOfMethod();
		return directories;
	}

	/*----------------------------------------------------------
	* Nom: getControlPanelDirectories
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la liste des r�pertoires pouvant 
	* contenir des panneaux de commandes I-SIS.
	* La liste des r�pertoires est extraite de l'environnement du service, via 
	* la m�thode getEnvironment(), en utilisant le param�tre de configuration 
	* "I-TOOLS.ControlPanelsVariable".
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent.
	* 
	* Retourne: Un tableau de cha�nes de caract�res contenant les r�pertoires 
	* de recherche des panneaux de commandes pour la session.
	* 
	* L�ve: InnerException.
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
			// On commence par r�cup�rer la valeur du param�tre de 
			// configuration
			ConfigurationAPI configuration = new ConfigurationAPI();
			panels_variable = configuration.getString("I-TOOLS",
				"ControlPanelsVariable");
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de la r�cup�ration du " +
				"param�tre de configuration: " + e.getMessage());
			// On continue avec la valeur par d�faut
		}
		// On construit la liste des r�pertoires
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
	* Cette m�thode est automatiquement appel�e par le ramasse miettes de la
	* machine virtuelle java lorsqu'un objet est sur le point d'�tre d�truit.
	* Elle permet de lib�rer toutes les ressources allou�es par une instance.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ServiceSessionProxy", "finalize");

		trace_methods.beginningOfMethod();
		// On lib�re la r�f�rence sur l'interface
		_serviceSession = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: SelectExecutionListener
	* 
	* Description:
	* Cette classe est une classe technique charg�e de permettre l'ex�cution 
	* de s�lection dans des tables pr�sentant de larges donn�es. Pour cela, 
	* au lieu d'utiliser la m�thode getSelectResult() de l'interface 
	* ServiceSessionInterface, il est n�cessaire de passer par un contexte 
	* d'ex�cution.
	* Pour cela, la classe impl�mente l'interface 
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
		* Cette m�thode est le constructeur par d�faut. Elle n'est pr�sent�e 
		* que pour des raisons de lisibilit�.
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
		* Cette m�thode red�finit celle de l'interface 
		* ExecutionListenerInterfaceOperations. Elle est appel�e lorsque des 
		* donn�es ont �t� �mises par la proc�dure sur sa sortie standard.
		* La m�thode ajoute les donn�es au buffer de sortie standard.
		* 
		* Arguments:
		*  - data: Des donn�es �mises sur la sortie standard de la proc�dure.
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
		* Cette m�thode red�finit celle de l'interface 
		* ExecutionListenerInterfaceOperations. Elle est appel�e lorsque des 
		* donn�es ont �t� �mises par la proc�dure sur sa sortie d'erreur.
		* La m�thode ajoute les donn�es au buffer de sortie d'erreur.
		* 
		* Arguments:
		*  - data: Des donn�es �mises sur la sortie d'erreur de la proc�dure.
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
		* Cette m�thode red�finit celle de l'interface 
		* ExecutionListenerInterfaceOperations. Elle est appel�e lorsque la 
		* proc�dure a termin� son ex�cution.
		* La m�thode stocke la valeur de retour de la proc�dure dans 
		* l'attribut correspondant.
		* 
		* Arguments:
		*  - exitValue: La valeur de retour de la proc�dure.
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
		* Cette m�thode permet de r�cup�rer le contenu du buffer de la sortie 
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
		* Cette m�thode permet de r�cup�rer le contenu du buffer de la sortie 
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
		* Cette m�thode permet de r�cup�rer la valeur de retour de la 
		* proc�dure.
		* 
		* Retourne: La valeur de retour de la proc�dure.
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
		* Cette m�thode permet de savoir si l'ex�cution s'est bien pass�e ou 
		* non. Elle teste la valeur du code de retour d'ex�cution de la 
		* proc�dure. Si celui-ci vaut 0 ou 201, on consid�re que l'ex�cution 
		* s'est bien d�roul�e.
		* 
		* Retourne: true si l'ex�cution s'est bien pass�e, false sinon.
		* ----------------------------------------------------------*/
		public boolean isExecutionOk()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"SelectExecutionListener", "isExecutionOk");
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			int ok_exit_value = 0;
			int warning_exit_value = 201;

			trace_methods.beginningOfMethod();
			// On tente de r�cup�rer les param�tres de configuration
			try
			{
				ConfigurationAPI configuration = new ConfigurationAPI();
				configuration.useSection("I-TOOLS");
				ok_exit_value = configuration.getInt("ReturnCode.Ok");
				warning_exit_value = configuration.getInt("ReturnCode.Warning");
			}
			catch(Exception exception)
			{
				trace_errors.writeTrace("Erreur lors de la r�cup�ration de la " +
					"configuration: " + exception);
				// On continue quand m�me
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
		* Cette m�thode est automatiquement appel�e par le ramasse miettes de la
		* machine virtuelle java lorsqu'un objet est sur le point d'�tre d�truit.
		* Elle permet de lib�rer toutes les ressources allou�es par une instance.
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
		* Cet attribut est charg� de stocker la valeur de retour de la 
		* proc�dure.
		* ----------------------------------------------------------*/
		private int _exitValue;

		/*----------------------------------------------------------
		* Nom: _outputBuffer
		* 
		* Description:
		* Cet attribut maintient une r�f�rence sur un objet StringBuffer 
		* destin� � contenir les donn�es qui ont �t� �mises par la proc�dure 
		* sur sa sortie standard.
		* ----------------------------------------------------------*/
		private StringBuffer _outputBuffer;

		/*----------------------------------------------------------
		* Nom: _errorBuffer
		* 
		* Description:
		* Cet attribut maintient une r�f�rence sur un objet StringBuffer 
		* destin� � contenir les donn�es qui ont �t� �mises par la proc�dure 
		* sur sa sortie d'erreur.
		* ----------------------------------------------------------*/
		private StringBuffer _errorBuffer;
	}
	
	/*----------------------------------------------------------
	* Nom: _serviceSession
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur une interface
	* ServiceSessionInterface � travers laquelle toutes les requ�tes seront
	* transmises � un Agent.
	* ----------------------------------------------------------*/
	private ServiceSessionInterface _serviceSession;

	/*----------------------------------------------------------
	* Nom: buildDirectoryList
	* 
	* Description:
	* Cette m�thode est charg�e de construire une liste de r�pertoires � 
	* partir de la valeur du param�tre I-SIS extrait de l'environnement de la 
	* session de service, dont le nom est pass� en argument.
	* La liste est construite en d�coupant la valeur du param�tre suivant le 
	* s�parateur ':'.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - parameterName: Le nom du param�tre d'o� extraire la liste des 
	*    r�pertoires,
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent,
	*  - tryToGuessFirst: Un bool�en indiquant si la m�thode doit chercher le 
	*    caract�re de s�paration (true) ou non (false).
	* 
	* Retourne: Un tableau de cha�nes de caract�res contenant les r�pertoires 
	* contenu dans la valeur du param�tre dont le nom est pass� en argument.
	* 
	* L�ve: InnerException.
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
		// On commence par r�cup�rer l'environnement de la session de service
		IsisParameter[] service_environment = getEnvironment();
		// On va rechercher le param�tre correspondant au nom pass� en 
		// argument
		for(int index = 0 ; index < service_environment.length ; index ++)
		{
			IsisParameter parameter = service_environment[index]; 
			if(parameter.name.equals(parameterName) == true)
			{
				trace_debug.writeTrace("Param�tre trouv� dans l'environnement");
				trace_debug.writeTrace("Valeur: " + parameter.value);
				if(tryToGuessFirst == true)
				{
					String value_start = parameter.value.substring(1, 3);
					if(value_start.equals(":\\") == true ||
						value_start.equals(":/") == true)
					{
						// C'est le seul cas o� on est s�r d'�tre sur 
						// Windows
						path_separator = ";";
					}
				}
				if(path_separator == null)
				{
					path_separator = AgentLayerAbstractor.getPathSeparator(
						agentLayerMode);
				}
				// On va d�couper la valeur � partir du s�parateur ':'
				UtilStringTokenizer tokenizer = 
					new UtilStringTokenizer(parameter.value, path_separator);
				// On va instancier le tableau de cha�nes � partir du nombre de 
				// sous-cha�nes
				directories = new String[tokenizer.getTokensCount()];
				// On va remplir la liste des r�pertoires
				int loop = 0;
				while(tokenizer.hasMoreTokens() == true)
				{
					directories[loop] = tokenizer.nextToken();
					trace_debug.writeTrace("R�pertoire: " + directories[loop]);
					loop ++;
				}
				// On peut sortir
				trace_methods.endOfMethod();
				return directories;
			}
		}
		trace_errors.writeTrace("Param�tre non trouv� dans l'environnement");
		// On l�ve une exception
		trace_methods.endOfMethod();
		throw new InnerException("&ERR_ParameterNotInEnvironment", 
			parameterName, null);
	}
}