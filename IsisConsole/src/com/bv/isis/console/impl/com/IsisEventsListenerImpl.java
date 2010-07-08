/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
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
* Appel des méthodes eventOccured() et clearEvents() dans un thread
* pour chaque interface.
*
* Revision 1.12  2006/03/07 09:30:42  tz
* Méthodes getInstance() et cleanBeforeExit() synchrones.
*
* Revision 1.11  2005/10/07 08:33:33  tz
* Utilisation de l'interface ConsoleIsisEventsListenerInterface.
* Ajout de la méthode fireClearEvents().
*
* Revision 1.10  2005/07/01 12:23:25  tz
* Modification du composant pour les traces
*
* Revision 1.9  2004/11/03 15:20:18  tz
* Passage en design pattern Singleton,
* Gestion d'une liste de listeners.
*
* Revision 1.8  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.7  2004/10/13 14:00:57  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle,
* Renommage de StateChangeListenerImpl en IsisEventsListenerImpl.
*
* Revision 1.6  2004/10/06 07:38:34  tz
* Retransmission des notifications dans un thread.
*
* Revision 1.5  2004/07/29 12:18:48  tz
* Mise à jour de la documentation
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
// Déclaration du package
package com.bv.isis.console.impl.com;

//
// Imports système
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
* Cette classe est une classe implémentant l'interface CORBA 
* IsisEventsListenerInterface.
* Son rôle est de recevoir les notifications provenant du processus Portail, et
* de les retransmettre à une des classes de l'application devant les traiter.
* La classe de traitement doit implémenter l'interface 
* ConsoleIsisEventsListenerInterface.
*
* Si aucune classe n'est enregistrée comme destinataire des messages, ceux-ci
* seront perdus (non traités).
* La classe IsisEventsListenerImpl est nécessaire car sa référence est passée
* au processus Portail lors de l'ouverture d'une session (avant que la classe de
* traitement ne soit instanciée).
*
* De plus, le langage Java ne permet pas à une classe de spécialiser plus d'une
* classe, mais lui permet d'implémenter plusieurs interfaces. Cette méthode
* permet donc d'utiliser le design pattern de déléguation.
*
* La classe implémente le design pattern Singleton de sorte à permettre 
* l'accès à une instance unique depuis toute autre classe de l'application. 
* ----------------------------------------------------------*/
public class IsisEventsListenerImpl
	extends IsisEventsListenerInterfacePOA
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getInstance
	* 
	* Description:
	* Cette méthode statique fait partie du design pattern Singleton. Elle 
	* permet de récupérer l'unique instance de la classe 
	* IsisEventsListenerImpl, après l'avoir éventuellement créée.
	* 
	* Retourne: Une référence sur l'unique instance de IsisEventsListenerImpl.
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
	* Cette méthode statique fait partie du design pattern Singleton. Elle 
	* permet de libérer l'unique instance de IsisEventsListenerImpl.
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
	* Cette méthode permet d'ajouter à l'instance de IsisEventsListenerImpl 
	* une référence sur la classe implémentant l'interface 
	* ConsoleIsisEventsListenerInterface qui traitera les notifications en 
	* provenance du processus Portail.
	* 
	* Arguments:
	*  - isisEventsListener: Une référence sur une classe implémentant 
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
			// On regarde si le listener n'est pas déjà enregistré
			if(_listeners.contains(isisEventsListener) == true)
			{
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// Enregistrement de la référence
			_listeners.add(isisEventsListener);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: removeIsisEventsListener
	* 
	* Description:
	* Cette méthode permet de supprimer de l'instance de 
	* IsisEventsListenerImpl une référence sur la classe implémentant 
	* l'interface ConsoleIsisEventsListenerInterface qui aura été 
	* préalablement ajoutée.
	* 
	* Arguments:
	*  - isisEventsListener: Une référence sur une classe implémentant 
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
			// On regarde si le listener est enregistré
			if(_listeners.contains(isisEventsListener) == false)
			{
				// On sort
				trace_methods.endOfMethod();
				return;
			}
			// Suppression de la référence
			_listeners.removeElement(isisEventsListener);
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: eventOccured
	*
	* Description:
	* Cette méthode redéfini celle de l'interface CORBA 
	* IsisEventsListenerInterface.
	* Elle est appelée lorsqu'un événement I-SIS survient.
	* Cette méthode appelle la méthode eventOccured() de toutes les interfaces 
	* ConsoleIsisEventsListenerInterface (voir la méthode 
	* addIsisEventsListener()) dans un thread séparé, ou ne fait rien si aucune 
	* classe ne s'est enregistrée pour recevoir les notifications.
	* 
	* Arguments:
	*  - eventType: Le type de l'événement,
	*  - eventInformation: Un tableau contenant les informations sur 
	*    l'événement.
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
		// Vérification de la présence d'au moins un listener
		synchronized(_listeners)
		{
			if(_listeners.size() == 0)
			{
				Trace trace_debug = TraceAPI.declareTraceDebug("Console");

				trace_debug.writeTrace(
					"Aucun listener n'est enregistré, la notification est perdue.");
				return;
			}
			// L'appel à la méthode eventOccured() est fait dans un thread
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
						// Assure que tout cas d'erreur est géré
						try
						{
							listener.eventOccured(eventType, eventInformation);
						}
						catch(Throwable throwable)
						{
							Trace trace_errors = 
								TraceAPI.declareTraceErrors("Console");
			
							trace_errors.writeTrace("Erreur détectée lors " +
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
	* Cette méthode permet d'appeler la méthode clearEvents() sur toutes les 
	* interfaces ConsoleIsisEventsListenerInterface qui se sont enregistrées.
	* ----------------------------------------------------------*/
	public void fireClearEvents()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "eventOccured");

		trace_methods.beginningOfMethod();
		// Vérification de la présence d'au moins un listener
		synchronized(_listeners)
		{
			if(_listeners.size() == 0)
			{
				Trace trace_debug = TraceAPI.declareTraceDebug("Console");

				trace_debug.writeTrace(
					"Aucun listener n'est enregistré, la notification est perdue.");
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
						// Assure que tout cas d'erreur est géré
						try
						{
							listener.clearEvents();
						}
						catch(Throwable throwable)
						{
							Trace trace_errors = 
								TraceAPI.declareTraceErrors("Console");
				
							trace_errors.writeTrace("Erreur détectée lors " +
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
	* Cette méthode est automatiquement appelée par le Garbage Collector de la
	* machine virtuelle Java lorsque une instance de cette classe est sur le
	* point d'être détruite.
	* Elle permet de libérer les ressources allouées.
	*
	* Lève: Throwable.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsisEventsListenerImpl", "finalize");

		trace_methods.beginningOfMethod();
		// Libération de la référence du listener
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
	* maintient une référence sur l'unique instance de IsisEventsListenerImpl.
	* ----------------------------------------------------------*/
	private static IsisEventsListenerImpl _instance;

	/*----------------------------------------------------------
	* Nom: _listeners
	*
	* Description:
	* Cet attribut maintient une référence sur un objet Vector qui contiendra 
	* une liste d'interfaces ConsoleIsisEventsListenerInterface qui 
	* recevront les notifications.
	* ----------------------------------------------------------*/
	private Vector _listeners;

	/*----------------------------------------------------------
	* Nom: IsisEventsListenerImpl
	*
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Il permet de
	* créer des instances de IsisEventsListenerImpl.
	* Ce constructeur pourrait ne pas être spécifié ici, mais cela ajoute à la
	* lisibilité générale de la classe.
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