/*------------------------------------------------------------
* Copyright (c) 2002 par BV Associates. Tous droits r�serv�s.
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
* Ajout de la gestion du r�-essai sur �chec.
*
* Revision 1.17  2006/11/03 10:26:31  tz
* V�rification de l'enregistrement de la Console.
*
* Revision 1.16  2006/08/11 13:33:31  tz
* Adaptation � corba-R2_1_0-AL-1_0.
*
* Revision 1.15  2006/03/07 09:25:34  tz
* M�thodes getInstance(), cleanBeforeExit() et isInitialized() synchrones.
*
* Revision 1.14  2005/12/23 13:14:16  tz
* M�thode getFileReader() publique.
*
* Revision 1.13  2005/10/07 08:43:15  tz
* Suppression des m�thodes addIsisEventsListener()
* et removeIsisEventsListener().
*
* Revision 1.12  2005/07/05 15:04:40  tz
* Ajout des m�thodes getStoreFileList() et getFileReader().
*
* Revision 1.11  2005/07/01 12:28:36  tz
* Modification du composant pour les traces
*
* Revision 1.10  2004/11/09 15:27:30  tz
* Suppression de l'appel syst�matique � IORFinder.cleanBeforeExit(),
* et � IsisEventsListenerImpl.cleanBeforeExit().
*
* Revision 1.9  2004/11/03 15:22:42  tz
* Ajout des m�thodes addIsisEventsListener() et removeIsisEventsListener(),
* Suppression de la m�thode setIsisEventsListener(),
* Utilisation de IsisEventsListenerImpl en Singleton.
*
* Revision 1.8  2004/11/02 09:10:52  tz
* Polling p�riodique de l'�tat du Portail.
*
* Revision 1.7  2004/10/13 14:03:08  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le,
* Renommage de MasterInterfaceProxy en PortalInterfaceProxy.
*
* Revision 1.6  2004/10/06 07:42:00  tz
* Test de l'existence de l'interface Portail via la m�thode _non_existent().
*
* Revision 1.5  2004/07/29 12:23:29  tz
* Utilisation de Portal* au lieu de Master*
* Mise � jour de la documentation
*
* Revision 1.4  2003/12/08 14:37:32  tz
* Mise � jour du mod�le
*
* Revision 1.3  2003/03/07 16:22:54  tz
* Prise en compte du m�canisme de log m�tier
*
* Revision 1.2  2002/08/26 09:49:32  tz
* Utilisation de JacORB.
* Ajout de la m�thode isInitialized.
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
* Cette classe est une classe technique charg�e de la r�cup�ration de la
* r�f�rence de l'interface PortalInterface (via la classe IORFinder), et de
* l'ex�cution de requ�tes sur celle-ci. Elle permet d'effectuer des requ�tes
* sur l'interface PortalInterface sans avoir � g�rer les multiples exceptions
* qui peuvent �tre lev�es lors de ces requ�tes (elles sont toutes converties
* en InnerException, voir la classe CommonFeatures).
* Cette classe impl�mente le canevas de conception (Design Pattern) Singleton
* de sorte � ce qu'il n'y ait qu'une seule et unique instance tout au long du
* cycle de vie de l'application, et que celle-ci soit accessible par toute
* classe de l'application.
* Elle sp�cialise la classe TimerTask de sorte � permettre le polling 
* p�riodique de la pr�sence du Portail, d�clench� � partir du moment o� le 
* Portail a �t� contact�, jusqu'au moment o� la Console est ferm�e (voir la 
* m�thode run()).
* ----------------------------------------------------------*/
public class PortalInterfaceProxy
	extends TimerTask
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getInstance
	*
	* Description:
	* Cette m�thode statique fait partie int�grante du canevas de conception
	* Singleton. Elle permet de r�cup�rer l'unique instance de la classe, et de
	* la cr�er si n�cessaire.
	*
	* Si un probl�me survient lors de la cr�ation de l'instance, l'exception
	* InnerException est lev�e.
	*
	* Retourne: L'unique instance de PortalInterfaceProxy.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public static synchronized PortalInterfaceProxy getInstance()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "getInstance");

		trace_methods.beginningOfMethod();
		// Est-ce que l'instance existe d�j� ?
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
	* Cette m�thode statique fait partie int�grante du canevas de conception
	* Singleton. Elle permet de lib�rer l'unique instance de la classe et, par
	* cons�quent, de lib�rer toutes les ressources qui ont �t� allou�es par
	* celle-ci.
	*
	* C'est dans cette m�thode que la r�f�rence sur l'interface PortalInterface
	* est lib�r�e, que la Console est d�senregistr�e en tant que "listener" des
	* arr�ts du Portail (uniquement si le Portail ne s'est pas arr�t�), et enfin, 
	* que l'interface sur le service de log est lib�r�e, via la classe 
	* LogServiceProxy.
	* 
	* Arguments:
	*  - portalStopped: Un bool�en indiquant si le Portail s'est arr�t� ou non.
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
			// On arr�te le timer de polling
			if(_instance._pollingTimer != null)
			{
				_instance._pollingTimer.cancel();
			}
			// On d�senregistre le listener seulement s'il ne s'agit
			// pas d'un arr�t du Portail
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
						"Console d�senregistr�e du Portail");
				}
				catch(Exception exception)
				{
					CommonFeatures.processException("du d�senregistrement",
						exception);
				}
			}
			// Il faut lib�rer les r�f�rences
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
	* Cette m�thode statique permet de savoir si la classe a �t� initialis�e,
	* c'est-�-dire que l'unique instance existe d�j�.
	*
	* Retourne: true si la classe a �t� intialis�e, false sinon.
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
	* Cette m�thode permet de r�cup�rer la liste des utilisateurs d�clar�s du
	* syst�me I-SIS. Elle fait le relais avec la m�thode de m�me nom de
	* l'interface PortalInterface.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Retourne: Un tableau de cha�nes contenant l'ensemble des identifiants des
	* utilisateurs d�clar�s.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public String[] getUsersNames()
		throws
			InnerException
	{
		String[] users_names = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "getUsersNames");

		trace_methods.beginningOfMethod();
		// On v�rifie que le Portail est OK
		checkPortalAvailability(true);
		// On appelle la m�thode getUsersNames sur l'interface du Portail
		try
		{
			users_names = _portalInterface.getUsersNames();
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration des noms des utilisateurs", exception);
		}
		trace_methods.endOfMethod();
		return users_names;
	}

	/*----------------------------------------------------------
	* Nom: getAgentInterface
	*
	* Description:
	* Cette m�thode permet de r�cup�rer la r�f�rence de l'interface
	* AgentInterface d'un Agent. Elle fait le relais avec la m�thode de m�me
	* nom de l'interface PortalInterface.
	*
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	*
	* Arguments:
	*  - agentName: Le nom de l'Agent dont on souhaite r�cup�rer l'interface.
	*
	* Retourne: Une r�f�rence sur l'interface AgentInterface de l'Agent.
	*
	* L�ve: InnerException.
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
		// On v�rifie que le Portail est OK
		checkPortalAvailability(true);
		// On appelle la m�thode getAgentInterface sur l'interface du Portail
		try
		{
			agent_interface = _portalInterface.getAgentInterface(agentName);
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration de l'interface Agent", exception);
		}
		trace_methods.endOfMethod();
		return agent_interface;
	}

	/*----------------------------------------------------------
	* Nom: getStoreFileList
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la liste des fichiers pr�sents dans le 
	* d�p�t des Consoles au niveau du Portail. Elle fait le relais avec la 
	* m�thode de m�me nom de l'interface PortalInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Retourne: Un tableau d'objets FileSystemEntry contenant la liste des 
	* fichiers pr�sents dans le d�p�t des Consoles sur le Portail.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public FileSystemEntry[] getStoreFileList()
		throws 
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "getStoreFileList");
		FileSystemEntry[] file_list = null;

		trace_methods.beginningOfMethod();
		// On v�rifie que le Portail est OK
		checkPortalAvailability(true);
		// On appelle la m�thode getStoreFileList() sur l'interface du Portail
		// pour le d�p�t des Consoles
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
				"de la r�cup�ration de la liste des fichiers du d�p�t", 
				exception);
		}
		trace_methods.endOfMethod();
		return file_list;
	}

	/*----------------------------------------------------------
	* Nom: getFileReader
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la r�f�rence de l'interface 
	* FileReaderInterface permettant la lecture du fichier dont le chemin est 
	* pass� en argument. Elle fait le relais avec la m�thode de m�me nom de 
	* l'interface PortalInterface.
	* 
	* Si un probl�me quelconque survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - fileName: Le chemin du fichier pour lequel on souhaite r�cup�rer une 
	*    interface FileReaderInterface.
	* 
	* Retourne: Une r�f�rence sur une interface FileReaderInterface de lecture 
	* de fichier.
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
			"PortalInterfaceProxy", "getFileReader");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		FileReaderInterface reader_interface = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("fileName=" + fileName);
		// On v�rifie que le Portail est OK
		checkPortalAvailability(true);
		// On appelle la m�thode getFileReader() sur l'interface du Portail
		try
		{
			reader_interface = _portalInterface.getFileReader(fileName);
		}
		catch(Exception exception)
		{
			// Il y a eu une erreur
			trace_methods.endOfMethod();
			throw CommonFeatures.processException(
				"de la r�cup�ration de l'interface de lecture de fichier", 
				exception);
		}
		trace_methods.endOfMethod();
		return reader_interface;
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette m�thode est appel�e p�riodiquement par un Timer (voir le 
	* constructeur) afin d'effectuer un polling p�riodique de la pr�sence du 
	* Portail. La v�rification de la pr�sence du Portail est effectu�e via 
	* la m�thode checkPortalAvailability().
	* ----------------------------------------------------------*/
	public void run()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"PortalInterfaceProxy", "run");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		// On v�rifie la pr�sence du Portail (quelle que soit la valeur de
		// l'attribut _portalInterface)
		try
		{
			trace_events.writeTrace("V�rification de l'�tat du Portail");
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
	* Cet attribut statique fait partie int�grante du canevas de conception
	* Singleton. Il maintient une r�f�rence sur l'unique instance de la classe.
	* ----------------------------------------------------------*/
	private static PortalInterfaceProxy _instance;

	/*----------------------------------------------------------
	* Nom: _portalInterface
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'interface PortalInterface
	* n�cessaire � la communication avec le Portail du syst�me I-SIS.
	* ----------------------------------------------------------*/
	private PortalInterface _portalInterface;

	/*----------------------------------------------------------
	* Nom: _pollingTimer
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur le timer utilis� pour le 
	* polling p�riodique de la pr�sence du Portail.
	* ----------------------------------------------------------*/
	private Timer _pollingTimer;

	/*----------------------------------------------------------
	* Nom: PortalInterfaceProxy
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle est plac�e en zone
	* priv�e conform�ment au canevas de conception Singleton. Elle r�cup�re et
	* stocke la r�f�rence sur l'interface PortalInterface, via la classe
	* IORFinder.
	* De plus, la Console est enregistr�e en tant que "listener" des �v�nements
	* I-SIS, et la r�f�rence sur l'interface du service de log est r�cup�r�e,
	* et est positionn�e au niveau de la classe LogServiceProxy.
	* Un timer est cr�� afin de r�aliser un polling p�riodique de la pr�sence 
	* du Portail. La p�riode du polling est d�finie par le param�tre de 
	* configuration "Console.Polling.Period".
	*
	* Si un probl�me survient lors de la r�cup�ration de la r�f�rence de
	* l'interface PortalInterface, l'exception InnerException est lev�e.
	*
	* L�ve: InnerException.
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
		// Tout d'abord, il faut r�cup�rer la r�f�rence de l'interface
		// PortalInterface
		_portalInterface = IORFinder.lookupPortal();
		// Ensuite, il faut r�cup�rer l'instance de IsisEventsListenerImpl
		IsisEventsListenerImpl listener = IsisEventsListenerImpl.getInstance();
		// On cr�e le timer de polling
		// On va commencer par r�cup�rer la p�riode de polling
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
			trace_events.writeTrace("Console enregistr�e sur le Portail");
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
	* Cette m�thode permet de v�rifier que la r�f�rence de l'interface
	* PortalInterface a bien �t� r�cup�r�e, et que le processus Portail
	* est disponible (en appelant la m�thode _non_existent() sur l'interface).
	* Si le Portail devient indisponible, un �v�nement d'arr�t du Portail va 
	* �tre g�n�r� et transmis via la r�f�rence sur l'objet 
	* IsisEventsListenerImpl.
	*
	* Si un probl�me survient lors de la r�cup�ration de l'�tat du Portail,
	* l'exception InnerException est lev�e.
	*
	* Arguments:
 	*  - recheckIfFail: Un bool�en indiquant si la pr�sence du Portail doit 
 	*    �tre rev�rifi�e en cas d'erreur.
 	*
	* L�ve: InnerException.
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
			// On g�n�re un �v�nement
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
				// On g�n�re un �v�nement
				listener.eventOccured(IsisEventTypeEnum.PORTAL_STOPPED, null);
				// Le Portail n'est pas OK, il faut lever une exception
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_PortalNotAvailable", null, null);
			}
			// On va regarder si la Console est toujours enregistr�e
			if(_portalInterface.isIsisEventsListenerRegistered(
				IsisEventsListenerInterfaceHelper.narrow(
				IORFinder.servantToReference(listener))) == false)
			{
				trace_events.writeTrace("La Console n'est plus enregistr�e");
				// La Console n'est plus enregistr�e, on va proc�der au
				// r�-enregistrement
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
				// On r�v�rifie la pr�sence du Portail
				checkPortalAvailability(false);
			}
			else
			{
				trace_events.writeTrace("Le Portail n'est plus joignable");
				// On g�n�re un �v�nement
				listener.eventOccured(IsisEventTypeEnum.PORTAL_STOPPED, null);
				// Il y a eu une erreur
				trace_methods.endOfMethod();
				throw CommonFeatures.processException(
						"la r�cup�ration de l'�tat du Portail", exception);
			}
		}
		trace_methods.endOfMethod();
	}
}