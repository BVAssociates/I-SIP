/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/com/ExecutionListenerImpl.java,v $
* $Revision: 1.9 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de transfert des �v�nements d'ex�cution d'une proc�dure
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
* Mise au propre des traces de m�thodes.
*
* Revision 1.6  2004/10/13 14:01:16  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.5  2004/10/06 07:38:38  tz
* Retransmission des notifications dans un thread.
*
* Revision 1.4  2004/07/29 12:18:56  tz
* Mise � jour de la documentation
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
// D�claration du package
package com.bv.isis.console.impl.com;

//
// Imports syst�me
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
* Cette classe est une classe impl�mentant l'interface CORBA
* ExecutionListener. Son r�le est de recevoir les notifications relatives
* � l'ex�cution d'une proc�dure sur un Agent, et de les retransmettre � une des
* classes de l'application devant les traiter.
* La classe de traitement doit impl�menter l'interface
* ExecutionListenerInterfaceOperations, d�finie lors de la projection de l'IDL en
* Java (par le compilateur IDL to Java de JacORB).
*
* Si aucune classe n'est enregistr�e comme destinataire des messages, ceux-ci
* seront perdus (non trait�s).
*
* Le langage Java ne permet pas � une classe de sp�cialiser plus d'une classe,
* mais lui permet d'impl�menter plusieurs interfaces. Cette m�thode permet donc
* d'utiliser le design pattern de d�l�guation.
* ----------------------------------------------------------*/
public class ExecutionListenerImpl
	extends ExecutionListenerInterfacePOA
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ExecutionListenerImpl
	*
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. Il permet de
	* cr�er des instances de ExecutionListenerImpl.
	* Ce constructeur pourrait ne pas �tre sp�cifi� ici, mais cela ajoute � la
	* lisibilit� g�n�rale de la classe.
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
	* Cette m�thode permet de fournir � l'instance de ExecutionListenerImpl
	* la r�f�rence sur la classe impl�mentant l'interface
	* ExecutionListenerInterfaceOperations qui traitera les notifications relatives
	* � l'ex�cution d'une proc�dure.
	*
	* Arguments:
	*  - executionListener: Une r�f�rence sur une classe impl�mentant
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
		// Enregistrement de la r�f�rence
		_listener = executionListener;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: receiveDataFromOutputStream
	*
	* Description:
	* Cette m�thode red�fini celle de l'interface CORBA ExecutionListener.
	* Elle est appel�e lorsque des donn�es sont �mises par la proc�dure sur sa
	* sortie standard.
	* Cette m�thode appelle la m�thode receiveDataFromOutputStream() de
	* l'interface ExecutionListenerInterfaceOperations (voir la m�thode
	* setExecutionListener()), ou ne fait rien si aucune classe ne s'est
	* enregistr�e pour recevoir les notifications.
	*
	* Arguments:
	*  - data: Les donn�es �mises par la proc�dure sur sa sortie standard.
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
		// V�rification de la pr�sence d'un listener
		if(_listener == null)
		{
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");

			trace_debug.writeTrace("Aucun listener n'est enregistr�, la notification est perdue.");
			return;
		}
		final ExecutionListenerInterfaceOperations listener = _listener;
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{ 
				// Assure que tout cas d'erreur est g�r�
				try
				{
					listener.receiveDataFromOutputStream(data);
				}
				catch(Throwable throwable)
				{
				    Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
					trace_errors.writeTrace("Erreur d�tect�e lors du transfert de la notification: " +
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
	* Cette m�thode red�fini celle de l'interface CORBA ExecutionListener.
	* Elle est appel�e lorsque des donn�es sont �mises par la proc�dure sur sa
	* sortie d'erreur.
	* Cette m�thode appelle la m�thode receiveDataFromErrorStream() de
	* l'interface ExecutionListenerInterfaceOperations (voir la m�thode
	* setExecutionListener()), ou ne fait rien si aucune classe ne s'est
	* enregistr�e pour recevoir les notifications.
	*
	* Arguments:
	*  - data: Les donn�es �mises par la proc�dure sur sa sortie d'erreur.
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
		// V�rification de la pr�sence d'un listener
		if(_listener == null)
		{
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");

			trace_debug.writeTrace("Aucun listener n'est enregistr�, la notification est perdue.");
			return;
		}
		final ExecutionListenerInterfaceOperations listener = _listener;
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{ 
				// Assure que tout cas d'erreur est g�r�
				try
				{
					listener.receiveDataFromErrorStream(data);
				}
				catch(Throwable throwable)
				{
				    Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
					trace_errors.writeTrace("Erreur d�tect�e lors du transfert de la notification: " +
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
	* Cette m�thode red�fini celle de l'interface CORBA ExecutionListener.
	* Elle est appel�e lorsque la proc�dure a termin� son ex�cution.
	* Cette m�thode appelle la m�thode executionTerminated() de l'interface
	* ExecutionListenerInterfaceOperations (voir la m�thode
	* setExecutionListener()), ou ne fait rien si aucune classe ne s'est
	* enregistr�e pour recevoir les notifications.
	*
	* Arguments:
	*  - exitValue: Le code retour de la proc�dure.
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
		// V�rification de la pr�sence d'un listener
		if(_listener == null)
		{
			Trace trace_debug = TraceAPI.declareTraceDebug("Console");

			trace_debug.writeTrace("Aucun listener n'est enregistr�, la notification est perdue.");
			return;
		}
		final ExecutionListenerInterfaceOperations listener = _listener;
		Thread thread = new Thread(new Runnable()
		{
			public void run()
			{ 
				// Assure que tout cas d'erreur est g�r�
				try
				{
					listener.executionTerminated(exitValue);
				}
				catch(Throwable throwable)
				{
				    Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		
					trace_errors.writeTrace("Erreur d�tect�e lors du transfert de la notification: " +
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
			"ExecutionListenerImpl", "finalize");

		trace_methods.beginningOfMethod();
		// Lib�ration de la r�f�rence du listener
		_listener = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _listener
	*
	* Description:
	* Cet attribut maintient la r�f�rence sur la classe impl�mentant l'interface
	* ExecutionInterfaceListenerOperations qui recevra les notifications.
	* ----------------------------------------------------------*/
	private ExecutionListenerInterfaceOperations _listener;
}