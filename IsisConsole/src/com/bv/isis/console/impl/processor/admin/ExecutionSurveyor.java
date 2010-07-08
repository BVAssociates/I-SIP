/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/ExecutionSurveyor.java,v $
* $Revision: 1.12 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'ex�cution de commande
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
* Ajout d'un argument context � la m�thode execute().
*
* Revision 1.10  2008/06/16 11:28:40  tz
* Ajout de la m�thode getExecutionReturnCode().
*
* Revision 1.9  2005/07/01 12:21:36  tz
* Modification du composant pour les traces
*
* Revision 1.8  2004/11/23 15:42:55  tz
* Utilisation d'un buffer de 4 ko pour les communications.
*
* Revision 1.7  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.6  2004/10/13 14:00:07  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.5  2003/03/12 14:39:47  tz
* Prise en compte du m�canisme de log m�tier
*
* Revision 1.4  2003/03/07 16:21:38  tz
* Prise en compte du m�canisme de log m�tier
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
// D�claration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports syst�me
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
* Cette classe est une classe technique charg�e de l'ex�cution d'une commande
* et du suivi de celle-ci. Elle impl�mente l'interface
* ExecutionListenerInterfaceOperations afin de recevoir les notifications
* d'ex�cution de la commande.
* La commande est ex�cut�e par le biais de la m�thode execute().
* ----------------------------------------------------------*/
public class ExecutionSurveyor
	implements ExecutionListenerInterfaceOperations
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ExecutionSurveyor
	*
	* Description:
	* Cette m�thode est le constructeur par d�faut de la classe. Elle n'est
	* pr�sent�e que pour des raisons de lisibilit�.
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
	* Cette m�thode est celle par laquelle la commande, pass�e en argument, est
	* ex�cut�e. Toute la m�canique de lancement et de suivi de l'ex�cution est
	* g�r�e par la m�thode.
	*
	* Si la commande ne s'est pas ex�cut�e correctement, l'exception
	* InnerException est lev�e.
	*
	* Arguments:
	*  - actionId: L'identifiant de l'action,
	*  - command: Une cha�ne de caract�res contenant la commande � ex�cuter,
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn�,
	*  - context: Le contexte d'ex�cution de la commande.
	*
	* L�ve: InnerException.
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
		// On va commencer par v�rifier la validit� des arguments
		if(command == null || command.equals("") == true ||
			selectedNode == null)
		{
			trace_errors.writeTrace("Au moins un des arguments est nul !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On va r�cup�rer depuis la configuration les codes de retour
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
				"Erreur lors de la r�cup�ration de la configuration: " +
				exception);
			// On continue quand m�me
		}
		if(context == null) {
			context = selectedNode.getContext(true);
		}
		// On va cr�er une instance de ExecutionListenerImpl avec "this"
		// comme listener
		ExecutionListenerImpl execution_listener = new ExecutionListenerImpl();
		execution_listener.setExecutionListener(this);
		// On va cr�er un proxy de session de service
		ServiceSessionProxy proxy = new ServiceSessionProxy(
			selectedNode.getServiceSession());
		// On cr�e une r�f�rence sur le listener d'ex�cution
		ExecutionListenerInterface listener =
			ExecutionListenerInterfaceHelper.narrow(
			IORFinder.servantToReference(execution_listener));
		// On va r�cup�rer le contexte d'ex�cution de la commande
		ExecutionContextInterface execution_context =
			proxy.getExecutionContext(actionId, listener, command, context);
		// On va ex�cuter la commande et attendre sa fin
		try
		{
			trace_events.writeTrace("Ex�cution de la commande '" + command +
				"' sur l'agent " + selectedNode.getAgentName());
		    execution_context.execute(actionId, 4096);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'ex�cution de la commande: " + exception);
			InnerException new_exception = CommonFeatures.processException(
				"&ERR_ExecutionFailed", exception);
			// On l�ve l'exception
			trace_methods.endOfMethod();
			throw new_exception;
		}
		// On se place en attente de la fin de l'ex�cution
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
			// On teste si l'ex�cution est termin�e
			synchronized(_terminated)
			{
				if(_terminated.equals(Boolean.TRUE) == true)
				{
					// On sort de la boucle
					break;
				}
			}
		}
		trace_events.writeTrace("La commande a retourn�: " + _exitValue);
		// On regarde la valeur du code de retour
		if(_exitValue != ok_return_code && _exitValue != warning_return_code)
		{
			trace_errors.writeTrace("La commande a retourn� un code NOK: " +
				_exitValue);
			StringBuffer error_buffer = _errorBuffer;
			_errorBuffer = null;
			// On va cr�er et lever une nouvelle exception avec le contenu du
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
	* Cette m�thode red�finit celle de l'interface
	* ExecutionListenerInterfaceOperations. Elle est appel�e lorsque des donn�es
	* ont �t� �mises par la commande sur sa sortie standard.
	*
	* Arguments:
	*  - data: Une cha�ne contenant les donn�es �mises par la commande.
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
	* Cette m�thode red�finit celle de l'interface
	* ExecutionListenerInterfaceOperations. Elle est appel�e lorsque des
	* donn�es ont �t� �mises par la commande sur sa sortie d'erreur.
	*
	* Arguments:
	*  - data: Une cha�ne contenant les donn�es �mises par la commande.
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
	* Cette m�thode red�finit celle de l'interface
	* ExecutionListenerInterfaceOperations. Elle est appel�e lorsque
	* l'ex�cution de la commande s'est termin�e.
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
	* Cette m�thode permet de r�cup�rer le code retour de l'ex�cution, 
	* d�clench�e par la m�thode execute(). Ce code de retour est stock� 
	* dans l'attribut _exitValue.
	* 
	* Retourne: Le code retour de l'ex�cution de la commande.
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
	* Cet attribut est destin� � maintenir le code de sortie de la commande,
	* afin de d�terminer si l'ex�cution s'est correctement termin�e ou non.
	* ----------------------------------------------------------*/
	private int _exitValue;

	/*----------------------------------------------------------
	* Nom: _errorBuffer
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur un buffer charg� de recevoir les
	* donn�es �mises par la commande sur sa sortie d'erreur. Le contenu du
	* buffer sera utilis� dans le cas d'un �chec de l'ex�cution de la commande.
	* ----------------------------------------------------------*/
	private StringBuffer _errorBuffer;

	/*----------------------------------------------------------
	* Nom: _terminated
	*
	* Description:
	* Cet attribut maintient un drapeau charg� d'indiquer si l'ex�cution de la
	* proc�dure est termin�e ou non. Il est mis � jour par la m�thode
	* executionTerminated().
	* ----------------------------------------------------------*/
	private Boolean _terminated;
}