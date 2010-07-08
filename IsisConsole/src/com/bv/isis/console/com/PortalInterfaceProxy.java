/*------------------------------------------------------------
* Copyright (c) 2002 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/PortalInterfaceProxy.java,v $
* $Revision: 1.19 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de manipulation de l'interface Portail
* DATE:        15/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: PortalInterfaceProxy.java,v $
* Revision 1.19  2009/01/14 14:21:58  tz
* Prise en compte de la modification des packages.
*
* Revision 1.18  2007/09/24 10:38:05  tz
* Ajout de la gestion du ré-essai sur échec.
*
* Revision 1.17  2006/11/03 10:26:31  tz
* Vérification de l'enregistrement de la Console.
*
* Revision 1.16  2006/08/11 13:33:31  tz
* Adaptation à corba-R2_1_0-AL-1_0.
*
* Revision 1.15  2006/03/07 09:25:34  tz
* Méthodes getInstance(), cleanBeforeExit() et isInitialized() synchrones.
*
* Revision 1.14  2005/12/23 13:14:16  tz
* Méthode getFileReader() publique.
*
* Revision 1.13  2005/10/07 08:43:15  tz
* Suppression des méthodes addIsisEventsListener()
* et removeIsisEventsListener().
*
* Revision 1.12  2005/07/05 15:04:40  tz
* Ajout des méthodes getStoreFileList() et getFileReader().
*
* Revision 1.11  2005/07/01 12:28:36  tz
* Modification du composant pour les traces
*
* Revision 1.10  2004/11/09 15:27:30  tz
* Suppression de l'appel systématique à IORFinder.cleanBeforeExit(),
* et à IsisEventsListenerImpl.cleanBeforeExit().
*
* Revision 1.9  2004/11/03 15:22:42  tz
* Ajout des méthodes addIsisEventsListener() et removeIsisEventsListener(),
* Suppression de la méthode setIsisEventsListener(),
* Utilisation de IsisEventsListenerImpl en Singleton.
*
* Revision 1.8  2004/11/02 09:10:52  tz
* Polling périodique de l'état du Portail.
*
* Revision 1.7  2004/10/13 14:03:08  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle,
* Renommage de MasterInterfaceProxy en PortalInterfaceProxy.
*
* Revision 1.6  2004/10/06 07:42:00  tz
* Test de l'existence de l'interface Portail via la méthode _non_existent().
*
* Revision 1.5  2004/07/29 12:23:29  tz
* Utilisation de Portal* au lieu de Master*
* Mise à jour de la documentation
*
* Revision 1.4  2003/12/08 14:37:32  tz
* Mise à jour du modèle
*
* Revision 1.3  2003/03/07 16:22:54  tz
* Prise en compte du mécanisme de log métier
*
* Revision 1.2  2002/08/26 09:49:32  tz
* Utilisation de JacORB.
* Ajout de la méthode isInitialized.
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
import com.bv.core.config.ConfigurationAPI;
import java.util.TimerTask;
import java.util.Timer;

//
// Imports du projet
//
import com.bv.isis.console.impl.com.IsisEventsListenerImpl;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.corbacom.AgentInterface;
import com.bv.isis.corbacom.PortalInterface;
import com.bv.isis.corbacom.IsisEventsListenerInterfaceHelper;
import com.bv.isis.corbacom.IDL_REVISION;
import com.bv.isis.corbacom.IsisEventTypeEnum;
import com.bv.isis.corbacom.StoreTypeEnum;
import com.bv.isis.corbacom.FileReaderInterface;
import com.bv.isis.corbacom.FileSystemEntry;
import com.bv.isis.console.com.LogServiceProxy;

/*----------------------------------------------------------
* Nom: PortalInterfaceProxy
*
* Description:
* Cette classe est une classe technique chargée de la récupération de la
* référence de l'interface PortalInterface (via la classe IORFinder), et de
* l'exécution de requêtes sur celle-ci. Elle permet d'effectuer des requêtes
* sur l'interface PortalInterface sans avoir à gérer les multiples exceptions
* qui peuvent être levées lors de ces requêtes (elles sont toutes converties
* en InnerException, voir la classe CommonFeatures).
* Cette classe implémente le canevas de conception (Design Pattern) Singleton
* de sorte à ce qu'il n'y ait qu'une seule et unique instance tout au long du
* cycle de vie de l'application, et que celle-ci soit accessible par toute
* classe de l'application.
* Elle spécialise la classe TimerTask de sorte à permettre le polling 
* périodique de la présence du Portail, déclenché à partir du moment où le 
* Portail a été contacté, jusqu'au moment où la Console est fermée (voir la 
* méthode run()).
* ----------------------------------------------------------*/
public class PortalInterfaceProxy
	extends TimerTask
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getInstance
	*
	* Description:
	* Cette méthode statique fait partie intégrante du canevas de conception
	* Singleton. Elle permet de récupérer l'unique instance de la classe, et de
	* la créer si nécessaire.
	*
	* Si un problème survient lors de la création de l'instance, l'exception
	* InnerException est levée.
	*
	* Retourne: L'unique instance de PortalInterfaceProxy.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static synchronized PortalInterfaceProxy getInstance()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "getInstance");

		trace_methods.beginningOfMethod();
		// Est-ce que l'instance existe déjà ?
		if(_instance == null)
		{
			// Il faut la construire
			_instance = new PortalInterfaceProxy();
		}
		trace_methods.endOfMethod();
		return _instance;
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	*
	* Description:
	* Cette méthode statique fait partie intégrante du canevas de conception
	* Singleton. Elle permet de libérer l'unique instance de la classe et, par
	* conséquent, de libérer toutes les ressources qui ont été allouées par
	* celle-ci.
	*
	* C'est dans cette méthode que la référence sur l'interface PortalInterface
	* est libérée, que la Console est désenregistrée en tant que "listener" des
	* arrêts du Portail (uniquement si le Portail ne s'est pas arrêté), et enfin, 
	* que l'interface sur le service de log est libérée, via la classe 
	* LogServiceProxy.
	* 
	* Arguments:
	*  - portalStopped: Un booléen indiquant si le Portail s'est arrêté ou non.
	* ----------------------------------------------------------*/
	public static synchronized void cleanBeforeExit(
		boolean portalStopped
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "cleanBeforeExit");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		if(_instance != null)
		{
			// On arrête le timer de polling
			if(_instance._pollingTimer != null)
			{
				_instance._pollingTimer.cancel();
			}
			// On désenregistre le listener seulement s'il ne s'agit
			// pas d'un arrêt du Portail
			if(portalStopped == false)
			{
				IsisEventsListenerImpl listener = 
					IsisEventsListenerImpl.getInstance();
				try
				{
					_instance._portalInterface.removeIsisEventsListener(
						IsisEventsListenerInterfaceHelper.narrow(
						IORFinder.servantToReference(listener)),
						LogServiceProxy.getIpAddress());
					trace_events.writeTrace(
						"Console désenregistrée du Portail");
				}
				catch(Exception exception)
				{
					CommonFeatures.processException("du désenregistrement",
						exception);
				}
			}
			// Il faut libérer les références
			_instance._portalInterface = null;
			_instance = null;
		}
		LogServiceProxy.clearLogServiceInterface();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isInitialized
	*
	* Description:
	* Cette méthode statique permet de savoir si la classe a été initialisée,
	* c'est-à-dire que l'unique instance existe déjà.
	*
	* Retourne: true si la classe a été intialisée, false sinon.
	* ----------------------------------------------------------*/
	public static synchronized boolean isInitialized()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "isInitialized");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _instance != null;
	}

	/*----------------------------------------------------------
	* Nom: getUsersNames
	*
	* Description:
	* Cette méthode permet de récupérer la liste des utilisateurs déclarés du
	* système I-SIS. Elle fait le relais avec la méthode de même nom de
	* l'interface PortalInterface.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Retourne: Un tableau de chaînes contenant l'ensemble des identifiants des
	* utilisateurs déclarés.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public String[] getUsersNames()
		throws
			InnerException
	{
		String[] users_names = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "getUsersNames");

		trace_methods.beginningOfMethod();
		// On vérifie que le Portail est OK
		checkPortalAvailability(true);
		// On appelle la méthode getUsersNames sur l'interface du Portail
		try
		{
			users_names = _portalInterface.getUsersNames();
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération des noms des utilisateurs", exception);
		}
		trace_methods.endOfMethod();
		return users_names;
	}

	/*----------------------------------------------------------
	* Nom: getAgentInterface
	*
	* Description:
	* Cette méthode permet de récupérer la référence de l'interface
	* AgentInterface d'un Agent. Elle fait le relais avec la méthode de même
	* nom de l'interface PortalInterface.
	*
	* Si un problème quelconque survient, l'exception InnerException est levée.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent dont on souhaite récupérer l'interface.
	*
	* Retourne: Une référence sur l'interface AgentInterface de l'Agent.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public AgentInterface getAgentInterface(
		String agentName
		)
		throws
			InnerException
	{
		AgentInterface agent_interface = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "getAgentInterface");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentName=" + agentName);
		// On vérifie que le Portail est OK
		checkPortalAvailability(true);
		// On appelle la méthode getAgentInterface sur l'interface du Portail
		try
		{
			agent_interface = _portalInterface.getAgentInterface(agentName);
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération de l'interface Agent", exception);
		}
		trace_methods.endOfMethod();
		return agent_interface;
	}

	/*----------------------------------------------------------
	* Nom: getStoreFileList
	* 
	* Description:
	* Cette méthode permet de récupérer la liste des fichiers présents dans le 
	* dépôt des Consoles au niveau du Portail. Elle fait le relais avec la 
	* méthode de même nom de l'interface PortalInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Retourne: Un tableau d'objets FileSystemEntry contenant la liste des 
	* fichiers présents dans le dépôt des Consoles sur le Portail.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public FileSystemEntry[] getStoreFileList()
		throws 
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "getStoreFileList");
		FileSystemEntry[] file_list = null;

		trace_methods.beginningOfMethod();
		// On vérifie que le Portail est OK
		checkPortalAvailability(true);
		// On appelle la méthode getStoreFileList() sur l'interface du Portail
		// pour le dépôt des Consoles
		try
		{
			file_list = 
				_portalInterface.getStoreFileList(StoreTypeEnum.CONSOLE_STORE);
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération de la liste des fichiers du dépôt", 
				exception);
		}
		trace_methods.endOfMethod();
		return file_list;
	}

	/*----------------------------------------------------------
	* Nom: getFileReader
	* 
	* Description:
	* Cette méthode permet de récupérer la référence de l'interface 
	* FileReaderInterface permettant la lecture du fichier dont le chemin est 
	* passé en argument. Elle fait le relais avec la méthode de même nom de 
	* l'interface PortalInterface.
	* 
	* Si un problème quelconque survient, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - fileName: Le chemin du fichier pour lequel on souhaite récupérer une 
	*    interface FileReaderInterface.
	* 
	* Retourne: Une référence sur une interface FileReaderInterface de lecture 
	* de fichier.
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
			"PortalInterfaceProxy", "getFileReader");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		FileReaderInterface reader_interface = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("fileName=" + fileName);
		// On vérifie que le Portail est OK
		checkPortalAvailability(true);
		// On appelle la méthode getFileReader() sur l'interface du Portail
		try
		{
			reader_interface = _portalInterface.getFileReader(fileName);
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la récupération de l'interface de lecture de fichier", 
				exception);
		}
		trace_methods.endOfMethod();
		return reader_interface;
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode est appelée périodiquement par un Timer (voir le 
	* constructeur) afin d'effectuer un polling périodique de la présence du 
	* Portail. La vérification de la présence du Portail est effectuée via 
	* la méthode checkPortalAvailability().
	* ----------------------------------------------------------*/
	public void run()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "run");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		// On vérifie la présence du Portail (quelle que soit la valeur de
		// l'attribut _portalInterface)
		try
		{
			trace_events.writeTrace("Vérification de l'état du Portail");
			checkPortalAvailability(true);
		}
		catch(Exception exception)
		{
			// On ne fait rien
			trace_events.writeTrace("Le Portail n'est plus disponible");
		}
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _instance
	*
	* Description:
	* Cet attribut statique fait partie intégrante du canevas de conception
	* Singleton. Il maintient une référence sur l'unique instance de la classe.
	* ----------------------------------------------------------*/
	private static PortalInterfaceProxy _instance;

	/*----------------------------------------------------------
	* Nom: _portalInterface
	*
	* Description:
	* Cet attribut maintient une référence sur l'interface PortalInterface
	* nécessaire à la communication avec le Portail du système I-SIS.
	* ----------------------------------------------------------*/
	private PortalInterface _portalInterface;

	/*----------------------------------------------------------
	* Nom: _pollingTimer
	* 
	* Description:
	* Cet attribut maintient une référence sur le timer utilisé pour le 
	* polling périodique de la présence du Portail.
	* ----------------------------------------------------------*/
	private Timer _pollingTimer;

	/*----------------------------------------------------------
	* Nom: PortalInterfaceProxy
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle est placée en zone
	* privée conformément au canevas de conception Singleton. Elle récupère et
	* stocke la référence sur l'interface PortalInterface, via la classe
	* IORFinder.
	* De plus, la Console est enregistrée en tant que "listener" des événements
	* I-SIS, et la référence sur l'interface du service de log est récupérée,
	* et est positionnée au niveau de la classe LogServiceProxy.
	* Un timer est créé afin de réaliser un polling périodique de la présence 
	* du Portail. La période du polling est définie par le paramètre de 
	* configuration "Console.Polling.Period".
	*
	* Si un problème survient lors de la récupération de la référence de
	* l'interface PortalInterface, l'exception InnerException est levée.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	private PortalInterfaceProxy()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "PortalInterfaceProxy");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int polling_period = 5;

		trace_methods.beginningOfMethod();
		// Tout d'abord, il faut récupérer la référence de l'interface
		// PortalInterface
		_portalInterface = IORFinder.lookupPortal();
		// Ensuite, il faut récupérer l'instance de IsisEventsListenerImpl
		IsisEventsListenerImpl listener = IsisEventsListenerImpl.getInstance();
		// On crée le timer de polling
		// On va commencer par récupérer la période de polling
		try
		{
			ConfigurationAPI configuration = new ConfigurationAPI();
			polling_period = configuration.getInt("Console", 
				"Polling.Period");
		}
		catch(Exception exception)
		{
			// On ne fait rien
		}
		trace_debug.writeTrace("polling_period=" + polling_period);
		_pollingTimer = new Timer();
		_pollingTimer.scheduleAtFixedRate(this, polling_period * 60 * 1000,
			polling_period * 60 * 1000);
		// On l'enregistre comme receveur des notifications
		try
		{
			_portalInterface.registerIsisEventsListener(
				IsisEventsListenerInterfaceHelper.narrow(
				IORFinder.servantToReference(listener)),
				LogServiceProxy.getIpAddress(),
				IDL_REVISION.value);
			LogServiceProxy.setLogServiceInterface(
				_portalInterface.getLogServiceInterface());
			trace_events.writeTrace("Console enregistrée sur le Portail");
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur lors de l'enregistrement
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"l'enregistrement du listener", exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkPortalAvailability
	*
	* Description:
	* Cette méthode permet de vérifier que la référence de l'interface
	* PortalInterface a bien été récupérée, et que le processus Portail
	* est disponible (en appelant la méthode _non_existent() sur l'interface).
	* Si le Portail devient indisponible, un événement d'arrêt du Portail va 
	* être généré et transmis via la référence sur l'objet 
	* IsisEventsListenerImpl.
	*
	* Si un problème survient lors de la récupération de l'état du Portail,
	* l'exception InnerException est levée.
	*
	* Arguments:
 	*  - recheckIfFail: Un booléen indiquant si la présence du Portail doit 
 	*    être revérifiée en cas d'erreur.
 	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	private void checkPortalAvailability(
		boolean recheckIfFail
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "checkPortalAvailability");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("recheckIfFail=" + recheckIfFail);
		IsisEventsListenerImpl listener = IsisEventsListenerImpl.getInstance();
		// Est-ce que l'interface Portail est disponible ?
		if(_portalInterface == null)
		{
			trace_errors.writeTrace("Aucune interface Portail");
			// On génère un événement
			listener.eventOccured(IsisEventTypeEnum.PORTAL_STOPPED, null);
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", "_portalInterface",
				null);
		}
		// Est-ce que le Portail est Ok
		try
		{
			if(_portalInterface._non_existent() == true)
			{
				trace_events.writeTrace("Le Portail n'est plus disponible");
				// On génère un événement
				listener.eventOccured(IsisEventTypeEnum.PORTAL_STOPPED, null);
				// Le Portail n'est pas OK, il faut lever une exception
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_PortalNotAvailable", null, null);
			}
			// On va regarder si la Console est toujours enregistrée
			if(_portalInterface.isIsisEventsListenerRegistered(
				IsisEventsListenerInterfaceHelper.narrow(
				IORFinder.servantToReference(listener))) == false)
			{
				trace_events.writeTrace("La Console n'est plus enregistrée");
				// La Console n'est plus enregistrée, on va procéder au
				// ré-enregistrement
				_portalInterface.registerIsisEventsListener(
					IsisEventsListenerInterfaceHelper.narrow(
					IORFinder.servantToReference(listener)),
					LogServiceProxy.getIpAddress(),
					IDL_REVISION.value);
			}
		}
		catch(Exception exception)
		{
			if(recheckIfFail == true)
			{
				// On va attendre 20 ms
				try
				{
					Thread.sleep(20);
				}
				catch(Exception e)
				{
					// On ne fait rien
				}
				// On révérifie la présence du Portail
				checkPortalAvailability(false);
			}
			else
			{
				trace_events.writeTrace("Le Portail n'est plus joignable");
				// On génère un événement
				listener.eventOccured(IsisEventTypeEnum.PORTAL_STOPPED, null);
				// Il y a eu une erreur
				trace_methods.endOfMethod();
				throw CommonFeatures.processException(
						"la récupération de l'état du Portail", exception);
			}
		}
		trace_methods.endOfMethod();
	}
}