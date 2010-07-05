/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/com/ExecutionListenerImpl.java,v $
* $Revision: 1.9 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de transfert des évènements d'exécution d'une procédure
* DATE:        14/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ExecutionListenerImpl.java,v $
* Revision 1.9  2005/07/01 12:23:38  tz
* Modification du composant pour les traces
*
* Revision 1.8  2004/11/23 15:44:25  tz
* Mise en commentaire des traces.
*
* Revision 1.7  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.6  2004/10/13 14:01:16  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.5  2004/10/06 07:38:38  tz
* Retransmission des notifications dans un thread.
*
* Revision 1.4  2004/07/29 12:18:56  tz
* Mise à jour de la documentation
*
* Revision 1.3  2002/08/13 13:00:30  tz
* Utilisation de JacORB
*
* Revision 1.2  2002/03/27 09:45:11  tz
* Renommage InuitExecutionListenerImpl en ExecutionListenerImpl
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

//
// Imports du projet
//
import com.bv.isis.corbacom.ExecutionListenerInterfacePOA;
import com.bv.isis.corbacom.ExecutionListenerInterfaceOperations;

/*----------------------------------------------------------
* Nom: ExecutionListenerImpl
*
* Description:
* Cette classe est une classe implémentant l'interface CORBA
* ExecutionListener. Son rôle est de recevoir les notifications relatives
* à l'exécution d'une procédure sur un Agent, et de les retransmettre à une des
* classes de l'application devant les traiter.
* La classe de traitement doit implémenter l'interface
* ExecutionListenerInterfaceOperations, définie lors de la projection de l'IDL en
* Java (par le compilateur IDL to Java de JacORB).
*
* Si aucune classe n'est enregistrée comme destinataire des messages, ceux-ci
* seront perdus (non traités).
*
* Le langage Java ne permet pas à une classe de spécialiser plus d'une classe,
* mais lui permet d'implémenter plusieurs interfaces. Cette méthode permet donc
* d'utiliser le design pattern de déléguation.
* ----------------------------------------------------------*/
public class ExecutionListenerImpl
	extends ExecutionListenerInterfacePOA
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ExecutionListenerImpl
	*
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Il permet de
	* créer des instances de ExecutionListenerImpl.
	* Ce constructeur pourrait ne pas être spécifié ici, mais cela ajoute à la
	* lisibilité générale de la classe.
	* ----------------------------------------------------------*/
	public ExecutionListenerImpl()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionListenerImpl", "ExecutionListenerImpl");

		trace_methods.beginningOfMethod();
		_listener = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setExecutionListener
	*
	* Description:
	* Cette méthode permet de fournir à l'instance de ExecutionListenerImpl
	* la référence sur la classe implémentant l'interface
	* ExecutionListenerInterfaceOperations qui traitera les notifications relatives
	* à l'exécution d'une procédure.
	*
	* Arguments:
	*  - executionListener: Une référence sur une classe implémentant
	*    l'interface ExecutionListenerInterfaceOperations.
	* ----------------------------------------------------------*/
	public void setExecutionListener(
		ExecutionListenerInterfaceOperations executionListener
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionListenerImpl", "setExecutionListener");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("executionListener=" +
			executionListener);
		// Enregistrement de la référence
		_listener = executionListener;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: receiveDataFromOutputStream
	*
	* Description:
	* Cette méthode redéfini celle de l'interface CORBA ExecutionListener.
	* Elle est appelée lorsque des données sont émises par la procédure sur sa
	* sortie standard.
	* Cette méthode appelle la méthode receiveDataFromOutputStream() de
	* l'interface ExecutionListenerInterfaceOperations (voir la méthode
	* setExecutionListener()), ou ne fait rien si aucune classe ne s'est
	* enregistrée pour recevoir les notifications.
	*
	* Arguments:
	*  - data: Les données émises par la procédure sur sa sortie standard.
	* ----------------------------------------------------------*/
	public void receiveDataFromOutputStream(
		final String data
		)
	{
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionListenerImpl", "receiveDataFromOutputStream");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);*/
		// Vérification de la présence d'un listener
		if(_listener == null)
		{
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");

			trace_debug.writeTrace("Aucun listener n'est enregistré, la notification est perdue.");
			return;
		}
		final ExecutionListenerInterfaceOperations listener = _listener;
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{ 
				// Assure que tout cas d'erreur est géré
				try
				{
					listener.receiveDataFromOutputStream(data);
				}
				catch(Throwable throwable)
				{
				    Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
					trace_errors.writeTrace("Erreur détectée lors du transfert de la notification: " +
						throwable);
				}
			}
		});
		thread.start();
		//trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: receiveDataFromErrorStream
	*
	* Description:
	* Cette méthode redéfini celle de l'interface CORBA ExecutionListener.
	* Elle est appelée lorsque des données sont émises par la procédure sur sa
	* sortie d'erreur.
	* Cette méthode appelle la méthode receiveDataFromErrorStream() de
	* l'interface ExecutionListenerInterfaceOperations (voir la méthode
	* setExecutionListener()), ou ne fait rien si aucune classe ne s'est
	* enregistrée pour recevoir les notifications.
	*
	* Arguments:
	*  - data: Les données émises par la procédure sur sa sortie d'erreur.
	* ----------------------------------------------------------*/
	public void receiveDataFromErrorStream(
		final String data
		)
	{
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionListenerImpl", "receiveDataFromErrorStream");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);*/
		// Vérification de la présence d'un listener
		if(_listener == null)
		{
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");

			trace_debug.writeTrace("Aucun listener n'est enregistré, la notification est perdue.");
			return;
		}
		final ExecutionListenerInterfaceOperations listener = _listener;
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{ 
				// Assure que tout cas d'erreur est géré
				try
				{
					listener.receiveDataFromErrorStream(data);
				}
				catch(Throwable throwable)
				{
				    Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
					trace_errors.writeTrace("Erreur détectée lors du transfert de la notification: " +
						throwable);
				}
			}
		});
		thread.start();
		//trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: executionTerminated
	*
	* Description:
	* Cette méthode redéfini celle de l'interface CORBA ExecutionListener.
	* Elle est appelée lorsque la procédure a terminé son exécution.
	* Cette méthode appelle la méthode executionTerminated() de l'interface
	* ExecutionListenerInterfaceOperations (voir la méthode
	* setExecutionListener()), ou ne fait rien si aucune classe ne s'est
	* enregistrée pour recevoir les notifications.
	*
	* Arguments:
	*  - exitValue: Le code retour de la procédure.
	* ----------------------------------------------------------*/
	public void executionTerminated(
		final int exitValue
		)
	{
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionListenerImpl", "executionTerminated");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("exitValue=" + exitValue);*/
		// Vérification de la présence d'un listener
		if(_listener == null)
		{
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");

			trace_debug.writeTrace("Aucun listener n'est enregistré, la notification est perdue.");
			return;
		}
		final ExecutionListenerInterfaceOperations listener = _listener;
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{ 
				// Assure que tout cas d'erreur est géré
				try
				{
					listener.executionTerminated(exitValue);
				}
				catch(Throwable throwable)
				{
				    Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
					trace_errors.writeTrace("Erreur détectée lors du transfert de la notification: " +
						throwable);
				}
			}
		});
		thread.start();
		//trace_methods.endOfMethod();
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
			"ExecutionListenerImpl", "finalize");

		trace_methods.beginningOfMethod();
		// Libération de la référence du listener
		_listener = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _listener
	*
	* Description:
	* Cet attribut maintient la référence sur la classe implémentant l'interface
	* ExecutionInterfaceListenerOperations qui recevra les notifications.
	* ----------------------------------------------------------*/
	private ExecutionListenerInterfaceOperations _listener;
}