/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/SessionTreeObjectNode.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de représentation d'un noeud de service
* DATE:        18/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SessionTreeObjectNode.java,v $
* Revision 1.14  2009/01/14 14:22:26  tz
* Prise en compte de la modification des packages.
*
* Revision 1.13  2005/10/12 14:26:01  tz
* Changement mineur.
*
* Revision 1.12  2005/10/07 11:26:43  tz
* Modification de la méthode setAgentAndSession().
*
* Revision 1.11  2005/10/07 08:18:47  tz
* Ajout des données relatives aux Agents, I-CLES et types de services
* pour les noeuds et les dictionnaires.
*
* Revision 1.10  2005/07/01 12:05:07  tz
* Modification du composant pour les traces
* Correction du traitement de l'événement d'arrêt d'Agent
*
* Revision 1.9  2004/11/02 08:47:23  tz
* Gestion de l'état du noeud,
* Amélioration du traitement des événements.
*
* Revision 1.8  2004/10/22 15:34:31  tz
* Ajout des méthodes getAgentSessionId(), getServiceSessionId() et
* forwardEvent().
*
* Revision 1.7  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.6  2004/10/13 13:54:33  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.5  2004/07/29 12:01:03  tz
* Traitement de la destruction sur arrêt du Portail
*
* Revision 1.4  2003/03/07 16:19:29  tz
* Prise en compte du mécanisme de log métier
* Ajout de l'auto-exploration
*
* Revision 1.3  2002/06/19 12:14:51  tz
* Ajout du release de la session de service.
*
* Revision 1.2  2002/04/05 15:49:49  tz
* Cloture itération IT1.2
*
* Revision 1.1  2002/03/27 09:42:20  tz
* Modification pour prise en compte nouvel IDL
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.node;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;

//
// Imports du projet
//
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.AgentSessionManager;
import com.bv.isis.console.com.PasswordManager;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.corbacom.IsisEventTypeEnum;
import com.bv.isis.console.core.abs.gui.TreeInterface;

/*----------------------------------------------------------
* Nom: SessionTreeObjectNode
*
* Description:
* Cette classe est une spécialisation de la classe GenericTreeObjectNode
* correspondant à un noeud généré par l'ouverture d'une session sur le Portail
* ou sur un Agent. Elle permet de maintenir et de fournir le nom de l'Agent,
* ainsi que la référence sur la session de service ayant été ouverte lors de la
* création du noeud.
* ----------------------------------------------------------*/
public class SessionTreeObjectNode
	extends GenericTreeObjectNode
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: SessionTreeObjectNode
	*
	* Description:
	* Cette méthode est le seul constructeur de la classe. Elle permet de
	* construire la partie GenericTreeObjectNode de l'objet à partir d'un autre
	* objet déjà construit.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent à partir duquel l'objet 
	*    GenericTreeObjectNode a été construit,
	*  - iClesName: Le nom du I-CLES auquel est apparenté le noeud,
	*  - serviceType: Le type du service auquel est apparenté le noeud,
 	*  - originalTreeNode: L'objet GenericTreeObjectNode servant à la
	*    construction de l'objet SessionTreeObjectNode.
	* ----------------------------------------------------------*/
	public SessionTreeObjectNode(
		String agentName,
		String iClesName,
		String serviceType,
		GenericTreeObjectNode originalTreeNode
		)
	{
		super(originalTreeNode.getObjectParameters(),
			originalTreeNode.getKey(), agentName, iClesName, serviceType,
			originalTreeNode.getDefinitionFilePath(),
			originalTreeNode.getTableName());

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "SessionTreeObjectNode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("originalTreeNode=" + originalTreeNode);
		// On stocke les informations
		_agentName = agentName;
		_serviceType = serviceType;
		_iClesName = iClesName;
		// Il faut copier le libellé
		setLabel(originalTreeNode.getLabel());
		// Il faut également déplacer les enfants
		while(originalTreeNode.getChildCount() > 0)
		{
			add((GenericTreeObjectNode)originalTreeNode.getChildAt(0));
			originalTreeNode.remove(0);
		}
		// On remplace le noeud fils au niveau du noeud père
		GenericTreeObjectNode parent_node =
			(GenericTreeObjectNode)originalTreeNode.getParent();
		if(parent_node != null)
		{
			parent_node.remove(originalTreeNode);
			parent_node.add(this);
		}
		setNodeState(originalTreeNode.getNodeState());
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setAgentAndSession
	*
	* Description:
	* Cette méthode permet de fournir à l'objet SessionTreeObjectNode le nom de
	* l'agent sur lequel la session de service a été ouverte ainsi que la
	* référence sur la session.
	*
	* Arguments:
	*  - serviceSession: Une référence sur l'interface ServiceSessionInterface
	*    correspondant à la session ouverte,
	*  - serviceName: Le nom du service sur lequel la session a été ouverte,
	*  - agentSessionId: L'identifiant de la session Agent.
	* ----------------------------------------------------------*/
	public void setAgentAndSession(
		ServiceSessionInterface serviceSession,
		String serviceName,
		String agentSessionId
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "setAgentAndSession");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("serviceSession=" + serviceSession);
		trace_arguments.writeTrace("serviceName=" + serviceName);
		trace_arguments.writeTrace("agentSessionId=" + agentSessionId);
		// On enregistre les données
		_serviceSession = serviceSession;
		_serviceName = serviceName;
		_agentSessionId = agentSessionId;
		// On récupère l'identifiant de la session de service
		try
		{
			ServiceSessionProxy service_proxy = 
				new ServiceSessionProxy(serviceSession);
			_serviceSessionId = service_proxy.getSessionIdentifier();
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace("Erreur lors de la récupération de " +
				"l'identifiant de la session de service: " + exception);
			// On positionne un faux identifiant
			_serviceSessionId = "unknown";
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getAgentName
	*
	* Description:
	* Cette méthode redéfinit la méthode de la super-classe. Elle permet de
	* fournir le nom de l'agent sur lequel la session a été ouverte.
	*
	* Retourne: Le nom de l'agent (valeur de l'attribut _agentName).
	* ----------------------------------------------------------*/
	public String getAgentName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "getAgentName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _agentName;
	}

	/*----------------------------------------------------------
	* Nom: getServiceSession
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle permet de récupérer
	* une référence sur la session de service à travers laquelle toutes les
	* requêtes doivent être transmises.
	*
	* Retourne: Une référence sur l'interface ServiceSessionInterface (valeur
	* de l'attribut _serviceSession).
	* ----------------------------------------------------------*/
	public ServiceSessionInterface getServiceSession()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "getServiceSession");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _serviceSession;
	}

	/*----------------------------------------------------------
	* Nom: destroy
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Son unique intérêt est
	* de libérer les ressources spécifiques aux noeuds de type
	* ServiceTreeObjectNode. Elle appelle la méthode de la super-classe, ferme
	* la session de service qui a été ouverte, et libère un leasing sur la session
	* agent.
	* 
	* Arguments:
	*  - portalStopped: Un booléen indiquant si la destruction est causée par
	*    l'arrêt du Portail (true) ou non (false).
	* ----------------------------------------------------------*/
	public void destroy(
		boolean portalStopped
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "destroy");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("portalStopped=" + portalStopped);
		// On appelle la méthode de la super-classe
		super.destroy(portalStopped);
		// Si portalStopped vaut true, il ne faut pas fermer la session, ni
		// libérer le leasing
		if(portalStopped == true)
		{
			_closeSessionOnDestroy = false;
			_releaseAgentLeasingOnDestroy = false;
		}
		// On commence par fermer la session de service
		if(_serviceSession != null && _closeSessionOnDestroy == true)
		{
			trace_debug.writeTrace("Fermeture de la session");
			try
			{
				ServiceSessionProxy proxy =
					new ServiceSessionProxy(_serviceSession);
				proxy.close(getAgentName(), getServiceName(), getServiceType(),
					getIClesName());
				trace_events.writeTrace("L'utilisateur " +
					PasswordManager.getInstance().getUserName() +
					" a fermé une session de service");
				_serviceSession._release();
				_serviceSession = null;
			}
			catch(InnerException exception)
			{
				trace_errors.writeTrace(
					"Erreur lors de la fermeture de la session: " +
					exception.getMessage());
			}
		}
		// On libère la référence
		_serviceSession = null;
		if(_releaseAgentLeasingOnDestroy == true)
		{
			// On libère un leasing sur la session Agent
			AgentSessionManager session_manager = AgentSessionManager.getInstance();
			try
			{
				session_manager.releaseAgentSession(_agentName, getServiceName(),
					getServiceType(), getIClesName());
			}
			catch(InnerException exception)
			{
				trace_errors.writeTrace(
					"Erreur lors de la libération d'un leasing: " +
					exception.getMessage());
			}
		}
		_agentName = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setCloseSessionOnDestroy
	*
	* Description:
	* Cette méthode permet d'indiquer au noeud si il doit fermer la session de
	* service qui lui est attachée lors de sa destruction. Positionne
	* l'attribut _closeSessionOnDestroy.
	*
	* Arguments:
	*  - closeSessionOnDestroy: Indique si la session de service attachée au
	*    noeud doit être fermée lors de la destruction du noeud (true) ou non
	*    (false).
	* ----------------------------------------------------------*/
	public void setCloseSessionOnDestroy(
		boolean closeSessionOnDestroy
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "setCloseSessionOnDestroy");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("closeSessionOnDestroy=" +
			closeSessionOnDestroy);
		_closeSessionOnDestroy = closeSessionOnDestroy;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setReleaseAgentLeasingOnDestroy
	*
	* Description:
	* Cette méthode permet d'indiquer au noeud si il doit libérer un leasing
	* sur la session Agent lors de sa destruction. Positionne
	* l'attribut _releaseAgentLeasingOnDestroy.
	*
	* Arguments:
	*  - releaseAgentLeasingOnDestroy: Indique si le noeud doit libérer un
	*    leasing sur la session Agent lors de sa destruction (true) ou non
	*    (false).
	* ----------------------------------------------------------*/
	public void setReleaseAgentLeasingOnDestroy(
		boolean releaseAgentLeasingOnDestroy
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "setReleaseAgentLeasingOnDestroy");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("releaseAgentLeasingOnDestroy=" +
			releaseAgentLeasingOnDestroy);
		_releaseAgentLeasingOnDestroy = releaseAgentLeasingOnDestroy;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getServiceName
	*
	* Description:
	* Cette méthode redéfinit la méthode de la super-classe. Elle permet de
	* fournir le nom du service sur lequel la session a été ouverte.
	*
	* Retourne: Le nom du service (valeur de l'attribut _serviceName).
	* ----------------------------------------------------------*/
	public String getServiceName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "SessionTreeObjectNode", "getServiceName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _serviceName;
	}

	/*----------------------------------------------------------
	* Nom: getServiceType
	*
	* Description:
	* Cette méthode redéfinit la méthode de la super-classe. Elle permet de
	* fournir le type du service sur lequel la session a été ouverte.
	*
	* Retourne: Le type du service (valeur de l'attribut _serviceType).
	* ----------------------------------------------------------*/
	public String getServiceType()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "SessionTreeObjectNode", "getServiceType");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _serviceType;
	}

	/*----------------------------------------------------------
	* Nom: getIClesName
	*
	* Description:
	* Cette méthode redéfinit la méthode de la super-classe. Elle permet de
	* fournir le nom du I-CLES sur lequel la session a été ouverte.
	*
	* Retourne: Le nom du I-CLES (valeur de l'attribut _iClesName).
	* ----------------------------------------------------------*/
	public String getIClesName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "getIClesName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _iClesName;
	}

	/*----------------------------------------------------------
	* Nom: getAgentSessionId
	* 
	* Description:
	* Cette méthode redéfinit la méthode de la super-classe. Elle permet de 
	* fournir l'identifiant de la session Agent associée à ce noeud.
	* 
	* Retourne: L'identifiant de la session Agent (valeur de l'attribut 
	* _agentSessionId).
	* ----------------------------------------------------------*/
	public String getAgentSessionId()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "getAgentSessionId");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _agentSessionId;
	}
	/*----------------------------------------------------------
	* Nom: getServiceSessionId
	* 
	* Description:
	* Cette méthode redéfinit la méthode de la super-classe. Elle permet de 
	* fournir l'identifiant de la session de service associée à ce noeud.
	* 
	* Retourne: L'identifiant de la session de service (valeur de l'attribut 
	* _serviceSessionId).
	* ----------------------------------------------------------*/
	public String getServiceSessionId()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "getServiceSessionId");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _serviceSessionId;
	}

	/*----------------------------------------------------------
	* Nom: forwardEvent
	* 
	* Description:
	* Cette méthode permet de transmettre, et éventuellement de traiter, un 
	* événement I-SIS reçu par la Console. Seuls les événements concernant 
	* l'arrêt d'un Agent ou la fermeture d'une session (Agent ou de service) 
	* doivent être transmis par ce biais.
	* Si le noeud est capable de traiter l'événement, elle le fait et sort. 
	* Sinon, l'événement est retransmis à tous les noeuds enfants du noeud 
	* courant.
	* 
	* Arguments:
	*  - eventType: Le type de l'événement I-SIS,
	*  - eventInformation: Un tableau de chaînes de caractères contenant les 
	*    informations sur l'événement,
	*  - treeInterface: Une référence sur l'interface TreeInterface.
	* 
	* Retourne: true si l'événement a été traité, false sinon.
	* ----------------------------------------------------------*/
	public boolean forwardEvent(
		IsisEventTypeEnum eventType,
		String[] eventInformation,
		TreeInterface treeInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SessionTreeObjectNode", "forwardEvent");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean event_processed = false;
		GenericTreeObjectNode parent_node = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("eventType=" + eventType);
		trace_arguments.writeTrace("eventInformation=" + eventInformation);
		// Si on est en cours de destruction, on sort
		if(getNodeState() == NodeStateEnum.DESTROYING)
		{
			trace_methods.endOfMethod();
			return false;
		}
		// Tout d'abord, on vérifie que l'événement est du bon type
		if(eventType != IsisEventTypeEnum.A_SESSION_CLOSED &&
			eventType != IsisEventTypeEnum.S_SESSION_CLOSED &&
			eventType != IsisEventTypeEnum.AGENT_STOPPED ||
			treeInterface == null)
		{
			trace_errors.writeTrace("Cet événement n'est pas traité !");
			// On sort
			trace_methods.endOfMethod();
			return event_processed;
		}
		parent_node = (GenericTreeObjectNode)this.getParent();
		String agent_name = eventInformation[0];
		trace_debug.writeTrace("_agentName=" + _agentName);
		trace_debug.writeTrace("agent_name=" + agent_name);
		// On va regarder si l'événement concerne ce noeud
		if(eventType == IsisEventTypeEnum.AGENT_STOPPED)
		{
			if(_agentName.equals(agent_name) == false)
			{
				trace_debug.writeTrace("L'arrêt de l'Agent ne me " +
					"concerne pas !");
				// On renvoie l'événement aux enfants
				event_processed = super.forwardEvent(eventType, eventInformation,
					treeInterface);
				trace_methods.endOfMethod();
				return event_processed;
			}
			trace_debug.writeTrace("L'arrêt de l'Agent me concerne !");
			// Le noeud est concerné, on commande la fermeture du noeud
			// parent
			parent_node.close(true);
			treeInterface.nodeStructureChanged(parent_node);
			trace_methods.endOfMethod();
			return true;
		}
		String agent_session = eventInformation[3];
		String service_session = eventInformation[eventInformation.length - 2];
		if(eventType == IsisEventTypeEnum.A_SESSION_CLOSED &&
			_agentName.equals(agent_name) == true &&
			_agentSessionId.equals(agent_session) == true)
		{
			trace_debug.writeTrace("La fermeture de la session Agent me " +
				"concerne !");
			// On doit s'assurer que le leasing Agent a bien été retiré
			AgentSessionManager session_manager = AgentSessionManager.getInstance();
			session_manager.releaseAgentSessionNoClose(_agentName);
			// Le noeud est concerné, on commande la fermeture du noeud
			// parent
			// Si le noeud correspond à la session Portail, il faut obliger les noeuds
			// enfants à fermer leurs sessions
			if(_agentName.equals("Portal") == true)
			{
				setCloseSessionOnDestroy(false);
				setReleaseAgentLeasingOnDestroy(false);
				parent_node.close(false);
			}
			else
			{
				parent_node.close(true);
			}
			event_processed = true;
			treeInterface.nodeStructureChanged(parent_node);
		}
		else if(eventType == IsisEventTypeEnum.S_SESSION_CLOSED &&
			_agentName.equals(agent_name) == true &&
			(_serviceSessionId.equals(service_session) == true ||
			_agentSessionId.equals(service_session) == true))
		{
			trace_debug.writeTrace("La fermeture de la session de service me " +
				"concerne !");
			// On libère un leasing sur la session Agent si besoin
			if(_releaseAgentLeasingOnDestroy == true)
			{
				AgentSessionManager session_manager = 
					AgentSessionManager.getInstance();
				try
				{
					session_manager.releaseAgentSession(_agentName, 
						getServiceName(), getServiceType(), getIClesName());
				}
				catch(InnerException exception)
				{
					trace_errors.writeTrace(
						"Erreur lors de la libération d'un leasing: " +
						exception.getMessage());
				}
			}
			// Le noeud est concerné, on commande la fermeture du noeud
			// parent
			// Si le noeud correspond à la session Portail, il faut obliger les noeuds
			// enfants à fermer leurs sessions
			if(_agentName.equals("Portal") == true)
			{
				setCloseSessionOnDestroy(false);
				setReleaseAgentLeasingOnDestroy(false);
				parent_node.close(false);
			}
			else
			{
				parent_node.close(true);
			}
			event_processed = true;
			treeInterface.nodeStructureChanged(parent_node);
		}
		else
		{
			event_processed = super.forwardEvent(eventType, eventInformation,
				treeInterface);
		}
		trace_methods.endOfMethod();
		return event_processed;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _agentName
	*
	* Description:
	* Cet attribut contient le nom de l'Agent sur lequel la session de service
	* correspondant au noeud graphique a été ouverte. Le nom de l'Agent est
	* également nécessaire lors de la fermeture du noeud, afin de libérer un
	* leasing sur la session agent.
	* ----------------------------------------------------------*/
	private String _agentName;

	/*----------------------------------------------------------
	* Nom: _closeSessionOnDestroy
	*
	* Description:
	* Cet attribut indique si la session de service attachée au noeud doit être
	* fermée lors de la destruction du noeud. Une session de service sur le
	* service I-SIS d'un Agent ne doit pas être fermée (elle le sera
	* automatiquement à la fermeture de la session Agent).
	* ----------------------------------------------------------*/
	private boolean _closeSessionOnDestroy;

	/*----------------------------------------------------------
	* Nom: _releaseAgentLeasingOnDestroy
	*
	* Description:
	* Cet attribut indique si le leasing sur la session Agent doit être libérée
	* lors de la destruction du noeud. Le leasing ne doit pas être libéré dans
	* le cas d'une session de service ouverte sans changement d'Agent.
	* ----------------------------------------------------------*/
	private boolean _releaseAgentLeasingOnDestroy;

	/*----------------------------------------------------------
	* Nom: _serviceSession
	*
	* Description:
	* Cet attribut maintient une référence sur l'interface
	* ServiceSessionInterface correspondant à la session de service ayant été
	* ouverte lors de la création du noeud.
	* ----------------------------------------------------------*/
	private ServiceSessionInterface _serviceSession;

	/*----------------------------------------------------------
	* Nom: _serviceName
	*
	* Description:
	* Cet attribut contient le nom du service sur lequel la session de service
	* correspondant au noeud graphique a été ouverte.
	* ----------------------------------------------------------*/
	private String _serviceName;

	/*----------------------------------------------------------
	* Nom: _serviceType
	*
	* Description:
	* Cet attribut contient le type du service sur lequel la session de service
	* correspondant au noeud graphique a été ouverte.
	* ----------------------------------------------------------*/
	private String _serviceType;

	/*----------------------------------------------------------
	* Nom: _iClesName
	*
	* Description:
	* Cet attribut contient le nom du I-CLES sur lequel la session de service
	* correspondant au noeud graphique a été ouverte.
	* ----------------------------------------------------------*/
	private String _iClesName;

	/*----------------------------------------------------------
	* Nom: _agentSessionId
	* 
	* Description:
	* Cet attribut contient le numéro d'identification de la session Agent 
	* associée à ce noeud.
	* ----------------------------------------------------------*/
	private String _agentSessionId;

	/*----------------------------------------------------------
	* Nom: _serviceSessionId
	*	
	* Description:
	* Cet attribut contient le numéro d'identification de la session de 
	* service associée à ce noeud.
	* ----------------------------------------------------------*/
	private String _serviceSessionId;
}