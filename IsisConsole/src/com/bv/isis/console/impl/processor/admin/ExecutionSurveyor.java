/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/ExecutionSurveyor.java,v $
* $Revision: 1.12 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'exécution de commande
* DATE:        04/06/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ExecutionSurveyor.java,v $
* Revision 1.12  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.11  2008/06/27 09:43:01  tz
* Ajout d'un argument context à la méthode execute().
*
* Revision 1.10  2008/06/16 11:28:40  tz
* Ajout de la méthode getExecutionReturnCode().
*
* Revision 1.9  2005/07/01 12:21:36  tz
* Modification du composant pour les traces
*
* Revision 1.8  2004/11/23 15:42:55  tz
* Utilisation d'un buffer de 4 ko pour les communications.
*
* Revision 1.7  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.6  2004/10/13 14:00:07  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.5  2003/03/12 14:39:47  tz
* Prise en compte du mécanisme de log métier
*
* Revision 1.4  2003/03/07 16:21:38  tz
* Prise en compte du mécanisme de log métier
*
* Revision 1.3  2002/08/13 13:10:39  tz
* Utilisation de JacORB
*
* Revision 1.2  2002/06/27 14:13:04  tz
* Ajout des processeurs d'administration des I-CLE et des Agents
*
* Revision 1.1  2002/06/19 12:17:52  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.config.ConfigurationAPI;

//
// Imports du projet
//
import com.bv.isis.corbacom.ExecutionListenerInterfaceOperations;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.impl.com.ExecutionListenerImpl;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.corbacom.ExecutionContextInterface;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.corbacom.ExecutionListenerInterfaceHelper;
import com.bv.isis.corbacom.ExecutionListenerInterface;
import com.bv.isis.console.com.IORFinder;

/*----------------------------------------------------------
* Nom: ExecutionSurveyor
*
* Description:
* Cette classe est une classe technique chargée de l'exécution d'une commande
* et du suivi de celle-ci. Elle implémente l'interface
* ExecutionListenerInterfaceOperations afin de recevoir les notifications
* d'exécution de la commande.
* La commande est exécutée par le biais de la méthode execute().
* ----------------------------------------------------------*/
public class ExecutionSurveyor
	implements ExecutionListenerInterfaceOperations
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ExecutionSurveyor
	*
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Elle n'est
	* présentée que pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public ExecutionSurveyor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionSurveyor", "ExecutionSurveyor");

		trace_methods.beginningOfMethod();
		_errorBuffer = new StringBuffer();
		_exitValue = -1;
		_terminated = Boolean.FALSE;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: execute
	*
	* Description:
	* Cette méthode est celle par laquelle la commande, passée en argument, est
	* exécutée. Toute la mécanique de lancement et de suivi de l'exécution est
	* gérée par la méthode.
	*
	* Si la commande ne s'est pas exécutée correctement, l'exception
	* InnerException est levée.
	*
	* Arguments:
	*  - actionId: L'identifiant de l'action,
	*  - command: Une chaîne de caractères contenant la commande à exécuter,
	*  - selectedNode: Une référence sur le noeud sélectionné,
	*  - context: Le contexte d'exécution de la commande.
	*
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public void execute(
		String actionId,
		String command,
		GenericTreeObjectNode selectedNode,
		IndexedList context
		)
		throws
			InnerException
	{
		int ok_return_code = 0;
		int warning_return_code = 201;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionSurveyor", "execute");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_events = TraceAPI.declareTraceEvents("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionId=" + actionId);
		trace_arguments.writeTrace("command=" + command);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("context=" + context);
		// On va commencer par vérifier la validité des arguments
		if(command == null || command.equals("") == true ||
			selectedNode == null)
		{
			trace_errors.writeTrace("Au moins un des arguments est nul !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On va récupérer depuis la configuration les codes de retour
		// Ok et Warning
		try
		{
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			ok_return_code =
				configuration_api.getInt("I-TOOLS", "ReturnCode.Ok");
			warning_return_code =
				configuration_api.getInt("ReturnCode.Warning");
		}
		catch(Exception exception)
		{
		    trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// On continue quand même
		}
		if(context == null) {
			context = selectedNode.getContext(true);
		}
		// On va créer une instance de ExecutionListenerImpl avec "this"
		// comme listener
		ExecutionListenerImpl execution_listener = new ExecutionListenerImpl();
		execution_listener.setExecutionListener(this);
		// On va créer un proxy de session de service
		ServiceSessionProxy proxy = new ServiceSessionProxy(
			selectedNode.getServiceSession());
		// On crée une référence sur le listener d'exécution
		ExecutionListenerInterface listener =
			ExecutionListenerInterfaceHelper.narrow(
			IORFinder.servantToReference(execution_listener));
		// On va récupérer le contexte d'exécution de la commande
		ExecutionContextInterface execution_context =
			proxy.getExecutionContext(actionId, listener, command, context);
		// On va exécuter la commande et attendre sa fin
		try
		{
			trace_events.writeTrace("Exécution de la commande '" + command +
				"' sur l'agent " + selectedNode.getAgentName());
		    execution_context.execute(actionId, 4096);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'exécution de la commande: " + exception);
			InnerException new_exception = CommonFeatures.processException(
				"&ERR_ExecutionFailed", exception);
			// On lève l'exception
			trace_methods.endOfMethod();
			throw new_exception;
		}
		// On se place en attente de la fin de l'exécution
		while(true)
		{
			// On commence par attendre 50 ms
			try
			{
				Thread.sleep(50);
			}
			catch(Exception e)
			{
				// on s'en fiche
			}
			// On teste si l'exécution est terminée
			synchronized(_terminated)
			{
				if(_terminated.equals(Boolean.TRUE) == true)
				{
					// On sort de la boucle
					break;
				}
			}
		}
		trace_events.writeTrace("La commande a retourné: " + _exitValue);
		// On regarde la valeur du code de retour
		if(_exitValue != ok_return_code && _exitValue != warning_return_code)
		{
			trace_errors.writeTrace("La commande a retourné un code NOK: " +
				_exitValue);
			StringBuffer error_buffer = _errorBuffer;
			_errorBuffer = null;
			// On va créer et lever une nouvelle exception avec le contenu du
			// buffer d'erreur
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ExecutionFailed",
				error_buffer.toString(), null);
		}
		_errorBuffer = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: receiveDataFromOutputStream
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface
	* ExecutionListenerInterfaceOperations. Elle est appelée lorsque des données
	* ont été émises par la commande sur sa sortie standard.
	*
	* Arguments:
	*  - data: Une chaîne contenant les données émises par la commande.
	* ----------------------------------------------------------*/
	public void receiveDataFromOutputStream(
		String data
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionSurveyor", "receiveDataFromOutputStream");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: receiveDataFromErrorStream
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface
	* ExecutionListenerInterfaceOperations. Elle est appelée lorsque des
	* données ont été émises par la commande sur sa sortie d'erreur.
	*
	* Arguments:
	*  - data: Une chaîne contenant les données émises par la commande.
	* ----------------------------------------------------------*/
	public void receiveDataFromErrorStream(
		String data
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionSurveyor", "receiveDataFromErrorStream");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("data=" + data);
		_errorBuffer.append(data);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: executionTerminated
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface
	* ExecutionListenerInterfaceOperations. Elle est appelée lorsque
	* l'exécution de la commande s'est terminée.
	*
	* Arguments:
	*  - exitValue: Le code de sortie de la commande.
	* ----------------------------------------------------------*/
	public void executionTerminated(
		int exitValue
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionSurveyor", "executionTerminated");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("exitValue=" + exitValue);
		synchronized(_terminated)
		{
		    _exitValue = exitValue;
			_terminated = Boolean.TRUE;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getExecutionReturnCode
	* 
	* Description:
	* Cette méthode permet de récupérer le code retour de l'exécution, 
	* déclenchée par la méthode execute(). Ce code de retour est stocké 
	* dans l'attribut _exitValue.
	* 
	* Retourne: Le code retour de l'exécution de la commande.
	* ----------------------------------------------------------*/
	public int getExecutionReturnCode() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ExecutionSurveyor", "getExecutionReturnCode");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _exitValue;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _exitValue
	*
	* Description:
	* Cet attribut est destiné à maintenir le code de sortie de la commande,
	* afin de déterminer si l'exécution s'est correctement terminée ou non.
	* ----------------------------------------------------------*/
	private int _exitValue;

	/*----------------------------------------------------------
	* Nom: _errorBuffer
	*
	* Description:
	* Cet attribut maintient une référence sur un buffer chargé de recevoir les
	* données émises par la commande sur sa sortie d'erreur. Le contenu du
	* buffer sera utilisé dans le cas d'un échec de l'exécution de la commande.
	* ----------------------------------------------------------*/
	private StringBuffer _errorBuffer;

	/*----------------------------------------------------------
	* Nom: _terminated
	*
	* Description:
	* Cet attribut maintient un drapeau chargé d'indiquer si l'exécution de la
	* procédure est terminée ou non. Il est mis à jour par la méthode
	* executionTerminated().
	* ----------------------------------------------------------*/
	private Boolean _terminated;
}