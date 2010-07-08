/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/com/IsisEventsListenerImpl.java,v $
* $Revision: 1.14 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de transfert des notifications du Portail
* DATE:        14/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: IsisEventsListenerImpl.java,v $
* Revision 1.14  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.13  2006/11/10 15:58:26  tz
* Appel des m�thodes eventOccured() et clearEvents() dans un thread
* pour chaque interface.
*
* Revision 1.12  2006/03/07 09:30:42  tz
* M�thodes getInstance() et cleanBeforeExit() synchrones.
*
* Revision 1.11  2005/10/07 08:33:33  tz
* Utilisation de l'interface ConsoleIsisEventsListenerInterface.
* Ajout de la m�thode fireClearEvents().
*
* Revision 1.10  2005/07/01 12:23:25  tz
* Modification du composant pour les traces
*
* Revision 1.9  2004/11/03 15:20:18  tz
* Passage en design pattern Singleton,
* Gestion d'une liste de listeners.
*
* Revision 1.8  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.7  2004/10/13 14:00:57  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le,
* Renommage de StateChangeListenerImpl en IsisEventsListenerImpl.
*
* Revision 1.6  2004/10/06 07:38:34  tz
* Retransmission des notifications dans un thread.
*
* Revision 1.5  2004/07/29 12:18:48  tz
* Mise � jour de la documentation
*
* Revision 1.4  2002/08/13 13:00:30  tz
* Utilisation de JacORB
*
* Revision 1.3  2002/03/27 09:48:44  tz
* Correction du log
*
* Revision 1.2  2002/03/27 09:45:11  tz
* Modification pour adaptation au nouvel IDL
*
* Revision 1.1  2001/12/12 09:59:13  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.com;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.util.Vector;

//
// Imports du projet
//
import com.bv.isis.corbacom.IsisEventsListenerInterfacePOA;
import com.bv.isis.corbacom.IsisEventTypeEnum;
import com.bv.isis.console.core.abs.processor.ConsoleIsisEventsListenerInterface;

/*----------------------------------------------------------
* Nom: IsisEventsListenerImpl
*
* Description:
* Cette classe est une classe impl�mentant l'interface CORBA 
* IsisEventsListenerInterface.
* Son r�le est de recevoir les notifications provenant du processus Portail, et
* de les retransmettre � une des classes de l'application devant les traiter.
* La classe de traitement doit impl�menter l'interface 
* ConsoleIsisEventsListenerInterface.
*
* Si aucune classe n'est enregistr�e comme destinataire des messages, ceux-ci
* seront perdus (non trait�s).
* La classe IsisEventsListenerImpl est n�cessaire car sa r�f�rence est pass�e
* au processus Portail lors de l'ouverture d'une session (avant que la classe de
* traitement ne soit instanci�e).
*
* De plus, le langage Java ne permet pas � une classe de sp�cialiser plus d'une
* classe, mais lui permet d'impl�menter plusieurs interfaces. Cette m�thode
* permet donc d'utiliser le design pattern de d�l�guation.
*
* La classe impl�mente le design pattern Singleton de sorte � permettre 
* l'acc�s � une instance unique depuis toute autre classe de l'application. 
* ----------------------------------------------------------*/
public class IsisEventsListenerImpl
	extends IsisEventsListenerInterfacePOA
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getInstance
	* 
	* Description:
	* Cette m�thode statique fait partie du design pattern Singleton. Elle 
	* permet de r�cup�rer l'unique instance de la classe 
	* IsisEventsListenerImpl, apr�s l'avoir �ventuellement cr��e.
	* 
	* Retourne: Une r�f�rence sur l'unique instance de IsisEventsListenerImpl.
	* ----------------------------------------------------------*/
	public static synchronized IsisEventsListenerImpl getInstance()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "getInstance");

		trace_methods.beginningOfMethod();
		if(_instance == null)
		{
			_instance = new IsisEventsListenerImpl();
		}
		trace_methods.endOfMethod();
		return _instance;
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	* 
	* Description:
	* Cette m�thode statique fait partie du design pattern Singleton. Elle 
	* permet de lib�rer l'unique instance de IsisEventsListenerImpl.
	* ----------------------------------------------------------*/
	public static synchronized void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "cleanBeforeExit");

		trace_methods.beginningOfMethod();
		_instance = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: addIsisEventsListener
	* 
	* Description:
	* Cette m�thode permet d'ajouter � l'instance de IsisEventsListenerImpl 
	* une r�f�rence sur la classe impl�mentant l'interface 
	* ConsoleIsisEventsListenerInterface qui traitera les notifications en 
	* provenance du processus Portail.
	* 
	* Arguments:
	*  - isisEventsListener: Une r�f�rence sur une classe impl�mentant 
	*    l'interface ConsoleIsisEventsListenerInterface.
 	* ----------------------------------------------------------*/
	public void addIsisEventsListener(
		ConsoleIsisEventsListenerInterface isisEventsListener
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "addIsisEventsListener");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("isisEventsListener=" + isisEventsListener);
		synchronized(_listeners)
		{
			// On regarde si le listener n'est pas d�j� enregistr�
			if(_listeners.contains(isisEventsListener) == true)
			{
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// Enregistrement de la r�f�rence
			_listeners.add(isisEventsListener);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: removeIsisEventsListener
	* 
	* Description:
	* Cette m�thode permet de supprimer de l'instance de 
	* IsisEventsListenerImpl une r�f�rence sur la classe impl�mentant 
	* l'interface ConsoleIsisEventsListenerInterface qui aura �t� 
	* pr�alablement ajout�e.
	* 
	* Arguments:
	*  - isisEventsListener: Une r�f�rence sur une classe impl�mentant 
	*    l'interface ConsoleIsisEventsListenerInterface.
 	* ----------------------------------------------------------*/
 	public void removeIsisEventsListener(
		ConsoleIsisEventsListenerInterface isisEventsListener
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "removeIsisEventsListener");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("isisEventsListener=" + isisEventsListener);
		synchronized(_listeners)
		{
			// On regarde si le listener est enregistr�
			if(_listeners.contains(isisEventsListener) == false)
			{
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// Suppression de la r�f�rence
			_listeners.removeElement(isisEventsListener);
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: eventOccured
	*
	* Description:
	* Cette m�thode red�fini celle de l'interface CORBA 
	* IsisEventsListenerInterface.
	* Elle est appel�e lorsqu'un �v�nement I-SIS survient.
	* Cette m�thode appelle la m�thode eventOccured() de toutes les interfaces 
	* ConsoleIsisEventsListenerInterface (voir la m�thode 
	* addIsisEventsListener()) dans un thread s�par�, ou ne fait rien si aucune 
	* classe ne s'est enregistr�e pour recevoir les notifications.
	* 
	* Arguments:
	*  - eventType: Le type de l'�v�nement,
	*  - eventInformation: Un tableau contenant les informations sur 
	*    l'�v�nement.
	* ----------------------------------------------------------*/
	public void eventOccured(
		final IsisEventTypeEnum eventType,
		final String[] eventInformation
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "eventOccured");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("eventType=" + eventType);
		if(eventInformation != null)
		{
			trace_arguments.writeTrace("eventInformation=" + eventInformation);
		}
		// V�rification de la pr�sence d'au moins un listener
		synchronized(_listeners)
		{
			if(_listeners.size() == 0)
			{
				Trace trace_debug = TraceAPI.declareTraceDebug("Console");

				trace_debug.writeTrace(
					"Aucun listener n'est enregistr�, la notification est perdue.");
				return;
			}
			// L'appel � la m�thode eventOccured() est fait dans un thread
			// pour chaque interface
			for(int index = 0 ; index < _listeners.size() ; index ++)
			{
				final ConsoleIsisEventsListenerInterface listener = 
					(ConsoleIsisEventsListenerInterface)
					_listeners.elementAt(index);
				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						// Assure que tout cas d'erreur est g�r�
						try
						{
							listener.eventOccured(eventType, eventInformation);
						}
						catch(Throwable throwable)
						{
							Trace trace_errors = 
								TraceAPI.declareTraceErrors("Console");
			
							trace_errors.writeTrace("Erreur d�tect�e lors " +
								"du transfert de la notification: " + throwable);
						}
					}
				});
				thread.start();
			}
		}
		trace_methods.endOfMethod();
	}


	/*----------------------------------------------------------
	* Nom: fireClearEvents
	* 
	* Description:
	* Cette m�thode permet d'appeler la m�thode clearEvents() sur toutes les 
	* interfaces ConsoleIsisEventsListenerInterface qui se sont enregistr�es.
	* ----------------------------------------------------------*/
	public void fireClearEvents()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "eventOccured");

		trace_methods.beginningOfMethod();
		// V�rification de la pr�sence d'au moins un listener
		synchronized(_listeners)
		{
			if(_listeners.size() == 0)
			{
				Trace trace_debug = TraceAPI.declareTraceDebug("Console");

				trace_debug.writeTrace(
					"Aucun listener n'est enregistr�, la notification est perdue.");
				return;
			}
			for(int index = 0 ; index < _listeners.size() ; index ++)
			{
				final ConsoleIsisEventsListenerInterface listener = 
					(ConsoleIsisEventsListenerInterface)
					_listeners.elementAt(index);
				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						// Assure que tout cas d'erreur est g�r�
						try
						{
							listener.clearEvents();
						}
						catch(Throwable throwable)
						{
							Trace trace_errors = 
								TraceAPI.declareTraceErrors("Console");
				
							trace_errors.writeTrace("Erreur d�tect�e lors " +
								"du transfert de la notification: " + 
								throwable);
						}
					}
				});
				thread.start();
			}
		}
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: finalize
	*
	* Description:
	* Cette m�thode est automatiquement appel�e par le Garbage Collector de la
	* machine virtuelle Java lorsque une instance de cette classe est sur le
	* point d'�tre d�truite.
	* Elle permet de lib�rer les ressources allou�es.
	*
	* L�ve: Throwable.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "finalize");

		trace_methods.beginningOfMethod();
		// Lib�ration de la r�f�rence du listener
		_listeners.clear();
		_listeners = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _instance
	* 
	* Description:
	* Cet attribut statique fait partie du design pattern Singleton. Il 
	* maintient une r�f�rence sur l'unique instance de IsisEventsListenerImpl.
	* ----------------------------------------------------------*/
	private static IsisEventsListenerImpl _instance;

	/*----------------------------------------------------------
	* Nom: _listeners
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un objet Vector qui contiendra 
	* une liste d'interfaces ConsoleIsisEventsListenerInterface qui 
	* recevront les notifications.
	* ----------------------------------------------------------*/
	private Vector _listeners;

	/*----------------------------------------------------------
	* Nom: IsisEventsListenerImpl
	*
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. Il permet de
	* cr�er des instances de IsisEventsListenerImpl.
	* Ce constructeur pourrait ne pas �tre sp�cifi� ici, mais cela ajoute � la
	* lisibilit� g�n�rale de la classe.
	* ----------------------------------------------------------*/
	private IsisEventsListenerImpl()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "IsisEventsListenerImpl");

		trace_methods.beginningOfMethod();
		// Initialisation de la liste des listeners
		_listeners = new Vector();
		trace_methods.endOfMethod();
	}
}