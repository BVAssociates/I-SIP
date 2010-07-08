/*------------------------------------------------------------
* Copyright (c) 2002 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/AgentSessionManager.java,v $
* $Revision: 1.17 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des sessions Agent
* DATE:        15/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: AgentSessionManager.java,v $
* Revision 1.17  2009/01/14 14:21:58  tz
* Prise en compte de la modification des packages.
*
* Revision 1.16  2008/01/31 16:39:06  tz
* Ajout d'un traitement sur nom d'Agent nul.
*
* Revision 1.15  2006/10/13 15:07:34  tz
* Gestion du mode de fonctionnement de l'Agent.
*
* Revision 1.14  2005/10/07 08:44:33  tz
* Changement mineur.
*
* Revision 1.13  2005/07/01 12:29:39  tz
* Modification du composant pour les traces
*
* Revision 1.12  2004/11/23 15:46:22  tz
* Adaptation pour corba-R1_1_2-AL-1_0.
*
* Revision 1.11  2004/11/02 09:10:09  tz
* Utilisation de ObjectLeasingHolder.
*
* Revision 1.10  2004/10/22 15:45:50  tz
* Ajout de la m�thode releaseAgentSessionNoClose().
*
* Revision 1.9  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.8  2004/07/29 12:25:00  tz
* Utilisation de ICles* au lieu de icles*
* Mise � jour de la documentation
*
* Revision 1.7  2003/12/08 15:13:51  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.6  2003/12/08 14:38:01  tz
* Mise � jour du mod�le
*
* Revision 1.5.2.1  2003/10/27 16:56:36  tz
* Support du domaine d'authentification
*
* Revision 1.5  2003/03/07 16:22:54  tz
* Prise en compte du m�canisme de log m�tier
*
* Revision 1.4  2002/08/26 09:50:27  tz
* Cloture IT1.5
*
* Revision 1.3  2002/06/19 12:19:51  tz
* Suppression de la trace affichant le mot de passe
* Ajout du release de la session agent
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
import java.util.Hashtable;
import com.bv.core.message.MessageManager;

//
// Imports du projet
//
import com.bv.isis.corbacom.AgentSessionInterface;
import com.bv.isis.corbacom.AgentInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.com.LogServiceProxy;

/*----------------------------------------------------------
* Nom: AgentSessionManager
*
* Description:
* Cette classe est une classe technique charg�e de stocker les diff�rentes
* sessions Agent ouvertes, et de g�rer les ouvertures de sessions sur les
* Agents. Il ne doit y avoir qu'une seule session ouverte sur un Agent donn�
* pour un utilisateur donn�.
* Cette classe impl�mente le canevas de conception (Design Pattern) Singleton
* de sorte � ce qu'il n'y ait qu'une seule instance durant tout le cycle de vie
* de l'application, et que celle-ci soit accessible par toute classe de
* l'application.
* ----------------------------------------------------------*/
public class AgentSessionManager
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getInstance
	*
	* Description:
	* Cette m�thode statique fait partie int�grante du canevas de conception
	* Singleton. Elle permet de r�cup�rer l'unique instance de la classe, et
	* �ventuellement de la cr�er.
	*
	* Retourne: Une r�f�rence sur l'unique instance de la classe.
	* ----------------------------------------------------------*/
	public static AgentSessionManager getInstance()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionManager", "getInstance");

		trace_methods.beginningOfMethod();
		// Il faut construire l'instance, si elle est nulle
		if(_instance == null)
		{
			_instance = new AgentSessionManager();
		}
		trace_methods.endOfMethod();
		return _instance;
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	*
	* Description:
	* Cette m�thode statique fait partie int�grante du canevas de conception
	* Singleton. Elle permet de lib�rer l'unique instance de la classe, et, par
	* cons�quent, toutes les ressources allou�es par celle-ci.
	* ----------------------------------------------------------*/
	public static void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionManager", "cleanBeforeExit");

		trace_methods.beginningOfMethod();
		// Il faut lib�rer l'instance
		if(_instance != null)
		{
			// Il faut aussi nettoyer la liste des leasings
			_instance._leasings.clear();
			_instance._leasings = null;
			_instance = null;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: openAgentSession
	*
	* Description:
	* Cette m�thode permet d'ouvrir une nouvelle session Agent sur un agent
	* d�termin�. Elle fait le relais avec la m�thode de m�me nom de l'interface
	* AgentInterface.
	* Si l'ouverture de session est effectu�e, une nouvelle instance de
	* AgentSessionLeasingHolder est cr��e pour cette session, et est ajout�e �
	* la liste des sessions Agent.
	*
	* Si un probl�me survient lors de l'ouverture de la session, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - actionId: Identifiant de l'action,
	*  - agentName: Le nom de l'Agent sur lequel on souhaite ouvrir une session,
	*  - userName: L'identifiant de l'utilisateur d�sirant ouvrir la session,
	*  - password: Le mot de passe de l'utilisateur sur l'Agent.
	*
	* Retourne: Une r�f�rence sur l'interface AgentSessionInterface
	* correspondant � la session Agent ouverte.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public synchronized AgentSessionInterface openAgentSession(
		String actionId,
		String agentName,
		String userName,
		String password
		)
		throws
			InnerException
	{
		AgentSessionInterface session_interface = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionManager", "openAgentSession");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("userName=" + userName);
		//trace_arguments.writeTrace("password=" + password);
		// On commence par regarder s'il n'y a pas d�j� de session
		session_interface = getAgentSession(agentName);
		if(session_interface != null)
		{
			trace_debug.writeTrace("Il existe d�j� une session sur l'agent: " +
				agentName);
			trace_methods.endOfMethod();
			return session_interface;
		}
		trace_debug.writeTrace("Il n'existe pas de session sur l'agent: " +
			agentName);
		try
		{
			// Il va falloir ouvrir une nouvelle session
			PortalInterfaceProxy portal_proxy = PortalInterfaceProxy.getInstance();
			// Il faut r�cup�rer l'interface de l'agent
			AgentInterface agent_interface =
				portal_proxy.getAgentInterface(agentName);
			// Il faut ensuite ouvrir la session
			session_interface =	agent_interface.openAgentSession(actionId,
				userName, password, LogServiceProxy.getIpAddress());
			// On r�cup�re le mode de la couche d'ex�cution de l'Agent
			String agent_layer_mode = agent_interface.getLayerMode();
			// On va cr�er un objet AgentSessionObject
			AgentSessionObject agent_object = 
				new AgentSessionObject(session_interface, agent_layer_mode);
			// On cr�e une instance de ObjectLeasingHolder et on l'ajoute
			// � la table des sessions
			_leasings.put(agentName,
				new	ObjectLeasingHolder(agent_object));
		}
		catch(InnerException exception)
		{
			// On ferme la session si elle existe
			if(session_interface != null)
			{
				try
				{
					session_interface.close(actionId);
				}
				catch(Exception ex)
				{
				}
			}
			// Il faut la renvoyer
			throw exception;
		}
		catch(Exception exception)
		{
			// On ferme la session si elle existe
			if(session_interface != null)
			{
				try
				{
					session_interface.close(actionId);
				}
				catch(Exception ex)
				{
				}
			}
			// Une exception a �t� d�tect�e, il faut la traiter
			throw CommonFeatures.processException(
				"l'ouverture de session agent",	exception);
		}
		trace_methods.endOfMethod();
		return session_interface;
	}

	/*----------------------------------------------------------
	* Nom: getAgentSession
	*
	* Description:
	* Cette m�thode permet de r�cup�rer la r�f�rence d'une session Agent
	* pr�alablement ouverte en fonction du nom de l'Agent concern�.
	* Le nombre de leasings de la session Agent est incr�ment�.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent pour lequel on souhaite r�cup�rer la
	*    session.
	*
	* Retourne: Une r�f�rence sur l'interface AgentSessionInterface, ou null si
	* aucune session n'a �t� ouverte sur l'Agent.
	* ----------------------------------------------------------*/
	public synchronized AgentSessionInterface getAgentSession(
		String agentName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionManager", "getAgentSession");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		// Est-ce qu'il existe une session pour cet agent
		if(_leasings.containsKey(agentName) == false)
		{
			trace_debug.writeTrace("Il n'y a pas de session pour l'agent " +
				agentName);
			// Il n'y a pas de session pour cet agent
			trace_methods.endOfMethod();
			return null;
		}
		ObjectLeasingHolder leasing_holder =
			(ObjectLeasingHolder)_leasings.get(agentName);
		AgentSessionObject agent_object = 
			(AgentSessionObject)leasing_holder.getLeasedObject();
		// On ajoute un leasing sur l'Agent
		leasing_holder.addLeasing();
		trace_methods.endOfMethod();
		return agent_object.getAgentSession();
	}

	/*----------------------------------------------------------
	* Nom: getAgentLayerMode
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer le mode de la couche d'ex�cution de 
	* l'Agent sur lequel une session a �t� pr�alablement ouverte en fonction 
	* de son nom.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent pour lequel on souhaite r�cup�rer le 
	*    mode de la couche d'ex�cution.
	* 
	* Retourne: Le mode de la couche d'ex�cution de l'Agent, ou null si 
	* aucune session n'a �t� ouverte sur celui-ci.
	* ----------------------------------------------------------*/
	public synchronized String getAgentLayerMode(
		String agentName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionManager", "getAgentLayerMode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		// Est-ce qu'il existe une session pour cet agent
		if(agentName == null || _leasings.containsKey(agentName) == false)
		{
			trace_debug.writeTrace("Il n'y a pas de session pour l'agent " +
				agentName);
			// Il n'y a pas de session pour cet agent
			trace_methods.endOfMethod();
			return null;
		}
		ObjectLeasingHolder leasing_holder =
			(ObjectLeasingHolder)_leasings.get(agentName);
		AgentSessionObject agent_object = 
			(AgentSessionObject)leasing_holder.getLeasedObject();
		trace_methods.endOfMethod();
		return agent_object.getAgentLayerMode();
	}

	/*----------------------------------------------------------
	* Nom: releaseAgentSession
	*
	* Description:
	* Cette m�thode permet de lib�rer un leasing sur une session Agent. Elle
	* d�cr�mente le nombre de leasings de la classe AgentSessionLeasingHolder.
	* Si le nombre de leasings est nul (m�thode shouldBeClosed() de la classe
	* AgentSessionLeasingHolder retournant true), la session agent est ferm�e,
	* et l'enregistrement correspondant est supprim� de la table des sessions
	* Agent ouvertes.
	* Il est important d'appeler cette m�thode afin de lib�rer les ressources
	* allou�es par une session Agent.
	*
	* Si une erreur survient lors de la fermeture de la session Agent,
	* l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent pour lequel on souhaite lib�rer un
	*    leasing,
	*  - serviceName: Le nom du service Agent � fermer,
	*  - serviceType: Le type du service Agent � fermer,
	*  - iClesName: Le nom du I-CLES Agent � fermer.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public synchronized void releaseAgentSession(
		String agentName,
		String serviceName,
		String serviceType,
		String iClesName
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionManager", "releaseAgentSession");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("serviceName=" + serviceName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		// Est-ce qu'il existe une session pour cet agent
		if(_leasings.containsKey(agentName) == false)
		{
			trace_debug.writeTrace("Il n'y a pas de session pour l'agent " +
				agentName);
			// Il n'y a pas de session pour cet agent
			trace_methods.endOfMethod();
			return;
		}
		trace_debug.writeTrace("Il y a une session pour l'agent: " + agentName);
		ObjectLeasingHolder leasing_holder =
			(ObjectLeasingHolder)_leasings.get(agentName);
		// On lib�re le leasing
		leasing_holder.releaseLeasing();
		// Est-ce que la session doit �tre ferm�e
		if(leasing_holder.isFreeOfLeasing() == false)
		{
			trace_methods.endOfMethod();
			return;
		}
		String user_name = PasswordManager.getInstance().getUserName();
		AgentSessionInterface agent_session = getAgentSession(agentName);
		// Il faut fermer la session
		trace_debug.writeTrace("La session pour l'agent doit �tre ferm�e: " +
			agentName);
		// On va g�n�rer un log
		String action_id = LogServiceProxy.getActionIdentifier(agentName,
			"&LOG_AgentSessionClosing", null, serviceName, serviceType,
			iClesName);
		// On g�n�re le premier log
		String[] message_data = new String[4];
		message_data[0] =
			MessageManager.getMessage("&LOG_AgentSessionClosing");
		message_data[1] = MessageManager.getMessage("&LOG_AgentName") +
			agentName;
		message_data[2] = MessageManager.getMessage("&LOG_UserName") +
			user_name;
		try
		{
		    message_data[3] =
				MessageManager.getMessage("&LOG_AgentSessionId") +
			    agent_session.getSessionIdentifier();
		}
		catch(Exception e)
		{
			message_data[3] =
				MessageManager.getMessage("&LOG_AgentSessionId") +
			    "undefined";
		}
		LogServiceProxy.addMessageForAction(action_id, message_data);
		// On supprime la r�f�rence de la table
		_leasings.remove(agentName);
		try
		{
			agent_session.close(action_id);
			trace_events.writeTrace("L'utilisateur " + user_name +
				" a ferm� la session sur l'agent " + agentName);
			agent_session._release();
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
			// Il y a eu une erreur lors de la fermeture de la session
			trace_methods.endOfMethod();
			InnerException inner_exception =
				CommonFeatures.processException(
				"lors de la fermeture de la session", exception);
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
	* Nom: releaseAgentSessionNoClose
	* 
	* Description:
	* Cette m�thode permet de lib�rer un bail sur une session Agent sans 
	* chercher � appeler la m�thode close() dessus. Elle est appel�e 
	* lorsqu'un Agent s'est arr�t�.
	* 
	* Arguments:
	*  - agentName: Le nom de l'Agent qui s'est arr�t�. 
 	* ----------------------------------------------------------*/
 	public synchronized void releaseAgentSessionNoClose(
 		String agentName
 		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionManager", "releaseAgentSessionNoClose");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		// Est-ce qu'il existe une session pour cet agent
		if(_leasings.containsKey(agentName) == false)
		{
			trace_debug.writeTrace("Il n'y a pas de session pour l'agent " +
				agentName);
			// Il n'y a pas de session pour cet agent
			trace_methods.endOfMethod();
			return;
		}
		trace_debug.writeTrace("Il y a une session pour l'agent: " + agentName);
		trace_debug.writeTrace("La session pour l'agent doit �tre ferm�e: " +
			agentName);
		// On supprime la r�f�rence de la table
		_leasings.remove(agentName);
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: AgentSessionObject
	* 
	* Description:
	* Cette classe imbriqu�e est une classe priv�e charg�e de contenir � la 
	* fois une r�f�rence sur une interface de session Agent ainsi qu'une 
	* information relative au mode de la couche d'ex�cution.
	* Elle est destin�e � �tre utilis�e avec un objet de type 
	* ObjectLeasingsHolder.
	* ----------------------------------------------------------*/
	private class AgentSessionObject
	{
		// ******************* PUBLIC **********************
		/*----------------------------------------------------------
		* Nom: AgentSessionObject
		*
		* Description:
		* Il s'agit du seul et unique constructeur de la classe. Il permet de 
		* passer la r�f�rence sur l'interface de session Agent, ainsi que le 
		* mode de la couche d'ex�cution associ�e � l'Agent.
		* 
		* Arguments:
		*  - agentSession: La r�f�rence sur la session Agent,
		*  - agentLayerMode: Le mode de la couche d'ex�cution.
 		* ----------------------------------------------------------*/
 		public AgentSessionObject(
 			AgentSessionInterface agentSession,
 			String agentLayerMode
 			)
 		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AgentSessionObject", "AgentSessionObject");
			Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
			trace_methods.beginningOfMethod();
			trace_arguments.writeTrace("agentSession=" + agentSession);
			trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
			_agentSession = agentSession;
			_agentLayerMode = agentLayerMode;
			trace_methods.endOfMethod();
 		}

		/*----------------------------------------------------------
		* Nom: getAgentSession
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer la r�f�rence sur l'interface 
		* AgentSessionInterface associ�e � la session Agent.
		* 
		* Retourne: Une r�f�rence sur l'interface AgentSessionInterface.
		* ----------------------------------------------------------*/
		public AgentSessionInterface getAgentSession()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AgentSessionObject", "getAgentSession");
			
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _agentSession;
		}

		/*----------------------------------------------------------
		* Nom: getAgentLayerMode
		* 
		* Description:
		* Cette m�thode permet de r�cup�rer le mode de la couche d'ex�cution 
		* associ�e � l'Agent.
		* 
		* Retourne: Le mode de la couche d'ex�cution.
		* ----------------------------------------------------------*/
		public String getAgentLayerMode()
		{
			Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"AgentSessionObject", "getAgentLayerMode");
				
			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return _agentLayerMode;
		}

		// ****************** PROTEGE *********************
		// ******************* PRIVE **********************
		/*----------------------------------------------------------
		* Nom: _agentLayerMode
		* 
		* Description:
		* Cet attribut est destin� � contenir le mode de la couche d'ex�cution 
		* associ�e � l'Agent.
		* ----------------------------------------------------------*/
		private String _agentLayerMode;

		/*----------------------------------------------------------
		* Nom: _agentSession
		* 
		* Description:
		* Cet attribut maintient une r�f�rence sur une interface 
		* AgentSessionInterface correspondant � la session Agent.
		* ----------------------------------------------------------*/
		private AgentSessionInterface _agentSession;
	};
	
	/*----------------------------------------------------------
	* Nom: _instance
	*
	* Description:
	* Cet attribut statique fait partie int�grante du canevas de conception
	* Singleton. Il maintient une r�f�rence sur l'unique instance de la classe.
	* ----------------------------------------------------------*/
	private static AgentSessionManager _instance;

	/*----------------------------------------------------------
	* Nom: _leasings
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur une table de Hash charg�e de
	* contenir les sessions Agent ouvertes. La cl� de la table de Hash est le
	* nom de l'Agent, la valeur �tant une instance de ObjectLeasingHolder.
	* ----------------------------------------------------------*/
	private Hashtable _leasings;

	/*----------------------------------------------------------
	* Nom: AgentSessionManager
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle est plac�e en zone
	* priv�e conform�ment au canevas de conception Singleton.
	* Elle instancie la liste des sessions Agents ouverte.
	* ----------------------------------------------------------*/
	private AgentSessionManager()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentSessionManager", "AgentSessionManager");

		trace_methods.beginningOfMethod();
		// Il faut instancier la table des leasings
		_leasings = new Hashtable();
		trace_methods.endOfMethod();
	}
}
