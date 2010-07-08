/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/LogServiceProxy.java,v $
* $Revision: 1.7 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des messages de log
* DATE:        27/02/2003
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: LogServiceProxy.java,v $
* Revision 1.7  2005/10/07 08:43:24  tz
* Changement mineur.
*
* Revision 1.6  2005/07/01 12:29:08  tz
* Modification du composant pour les traces
*
* Revision 1.5  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.4  2004/10/06 07:42:23  tz
* Nettoyage de l'interface du service de log sur erreur.
*
* Revision 1.3  2004/07/29 12:24:00  tz
* Utilisation de ICles* au lieu de icles*
* Mise � jour de la documentation
* Remplacement de Explorer par Console
*
* Revision 1.2  2003/06/10 13:58:34  tz
* Utilisation du nouveau vocabulaire
*
* Revision 1.1  2003/03/07 16:22:54  tz
* Prise en compte du m�canisme de log m�tier
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
import com.bv.core.message.MessageManager;
import java.net.InetAddress;

//
// Imports du projet
//
import com.bv.isis.corbacom.LogServiceInterface;

/*----------------------------------------------------------
* Nom: LogServiceProxy
*
* Description:
* Cette classe abstraite est une classe technique charg�e de la centralisation
* des �missions des messages de log sur le service de log. Elle permet
* d'effectuer �missions sans avoir � g�rer la r�cup�ration de l'interface du
* service de log, ou m�me sans avoir � se soucier des erreurs qui pourraient
* survenir.
*
* Elle propose trois m�thodes publiques, permettant:
*  - d'obtenir un num�ro d'identification d'action, et de g�n�rer un message
*    dans le fichier de log principal,
*  - de g�n�rer des logs d'activit� dans les fichiers de log sp�cifiques aux
*    actions,
*  - de r�cup�rer l'adresse IP de la Console.
*
* Elle propose deux m�thodes prot�g�es, permettant de positionner ou
* d�positionner la r�f�rence sur l'interface du service de log, laquelle est
* g�r�e par une autre classe.
* ----------------------------------------------------------*/
public abstract class LogServiceProxy
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getActionIdentifier
	*
	* Description:
	* Cette m�thode statique permet de r�cup�rer un identifiant unique d'action,
	* qui est transmis par le service de log du syst�me I-SIS. De plus, un
	* message sera enregistr� par le service de log dans le fichier principal,
	* � partir des informations pass�es en argument.
	* Cette m�thode effectue le relais avec la m�thode de m�me nom de
	* l'interface LogServiceInterface, si elle existe.
	*
	* Arguments:
	*  - targetHost: Le nom de la plate-forme sur laquelle l'action sera
	*    ex�cut�e,
	*  - actionMessage: Un message libre indiquant la nature de l'action qui va
	*    �tre ex�cut�e,
	*  - userName: Le nom de l'utilisateur, peut-�tre null. Dans ce cas, le nom
	*    de l'utilisateur sera r�cup�r� depuis le PasswordManager,
	*  - serviceName: Le nom du service sur lequel l'action sera ex�cut�e,
	*  - serviceType: Le type du service sur lequel l'action sera ex�cut�e,
	*  - iClesName: Le nom du I-CLES sur lequel l'action sera ex�cut�e.
	*
	* Retourne: Un num�ro d'identification unique d'action, qui devra ensuite
	* �tre utilis� pour tout nouvel enregistrement de messages de log.
	* ----------------------------------------------------------*/
	public static synchronized String getActionIdentifier(
		String targetHost,
		String actionMessage,
		String userName,
		String serviceName,
		String serviceType,
		String iClesName
		)
	{
		String action_id = "backup";
		String message = actionMessage;
		String user_name;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LogServiceProxy", "getActionIdentifier");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("targetHost=" + targetHost);
		trace_arguments.writeTrace("actionMessage=" + actionMessage);
		trace_arguments.writeTrace("serviceName=" + serviceName);
		trace_arguments.writeTrace("serviceType=" + serviceType);
		trace_arguments.writeTrace("iClesName=" + iClesName);
		// On v�rifie de la validit� des arguments
		if(targetHost == null || targetHost.equals("") == true ||
			actionMessage == null || actionMessage.equals("") == true ||
			serviceName == null || serviceName.equals("") == true ||
			serviceType == null || serviceType.equals("") == true ||
			iClesName == null || iClesName.equals("") == true)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide");
			// Dans ce cas, on retourne "backup"
			trace_methods.endOfMethod();
			return action_id;
		}
		// On v�rifie qu'il y a une r�f�rence sur l'interface du service de log
		if(_logServiceInterface == null)
		{
			trace_errors.writeTrace("Aucune r�f�rence sur le service de log");
			// Dans ce cas, on retourne "backup"
			trace_methods.endOfMethod();
			return action_id;
		}
		// On traduit �ventuellement le message
		if(actionMessage.startsWith("&") == true)
		{
			message = MessageManager.getMessage(actionMessage);
		}
		if(userName == null)
		{
		    // On r�cup�re le nom de l'utilisateur
		    user_name = PasswordManager.getInstance().getUserName();
		}
		else
		{
			user_name = userName;
		}
		// On appelle la m�thode de l'interface _logServiceInterface
		try
		{
			action_id = _logServiceInterface.getActionIdentifier(user_name, 
				serviceName, serviceType, iClesName, "Console", _ipAddress,	
				targetHost, message);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de r�cup�ration de " +
				"l'identifiant d'action: " + exception);
			// On enl�ve la r�f�rence � l'interface LogServiceInterface
			clearLogServiceInterface();
			// On retournera la valeur de secours: "backup"
		}
		trace_methods.endOfMethod();
		return action_id;
	}

	/*----------------------------------------------------------
	* Nom: addMessageForAction
	*
	* Description:
	* Cette m�thode statique permet d'enregistrer un message d'activit� ayant
	* un rapport avec une action, dont l'identifiant unique est pass� en
	* argument. Ce message est repr�sent� par un tableau de cha�nes de
	* caract�res, permettant d'utiliser un format libre.
	* Cette m�thode fait le relais avec la m�thode de m�me nom de l'interface
	* LogServiceInterface, si elle existe.
	*
	* Arguments:
	*  - actionIdentifier: L'identifiant de l'action,
	*  - message: Un tableau de cha�nes contenant le message � inscrire dans le
	*    log d'action.
	* ----------------------------------------------------------*/
	public static synchronized void addMessageForAction(
		String actionIdentifier,
		String[] message
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LogServiceProxy", "addMessageForAction");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("actionIdentifier=" + actionIdentifier);
		trace_arguments.writeTrace("message=" + message);
		// On v�rifie de la validit� des arguments
		if(actionIdentifier == null || 
			actionIdentifier.equals("") == true ||
			message == null || message.length == 0)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On v�rifie qu'il y a une r�f�rence sur l'interface du service de log
		if(_logServiceInterface == null)
		{
			trace_errors.writeTrace("Aucune r�f�rence sur le service de log");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On appelle la m�thode de l'interface _logServiceInterface
		try
		{
			_logServiceInterface.addConsoleMessageForAction(actionIdentifier,
				_ipAddress, message);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'�mission d'un message de log: " + exception);
			// On enl�ve la r�f�rence � l'interface LogServiceInterface
			clearLogServiceInterface();
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getIpAddress
	*
	* Description:
	* Cette m�thode statique permet de r�cup�rer l'adresse IP de la Console.
	* Si celle-ci n'est pas d�finie, elle est tout d'abord construite.
	*
	* Retourne: L'adresse IP de la Console.
	* ----------------------------------------------------------*/
	public static synchronized String getIpAddress()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LogServiceProxy", "getIpAddress");
			
		trace_methods.beginningOfMethod();
		if(_ipAddress == null)
		{
			// On va r�cup�rer l'adresse IP de la Console
			try
			{
				InetAddress my_address = InetAddress.getLocalHost();
				_ipAddress = my_address.getHostAddress();
			}
			catch(Exception exception)
			{
				Trace trace_errors = TraceAPI.declareTraceErrors("Console");

				trace_errors.writeTrace(
					"Erreur lors de la r�cup�ration de l'adresse IP: " +
					exception);
				// On va positionner une valeur par d�faut
				_ipAddress = "0.0.0.0";
			}
		}
		trace_methods.endOfMethod();
		return _ipAddress;
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: setLogServiceInterface
	*
	* Description:
	* Cette m�thode statique permet de positionner la r�f�rence sur l'interface
	* LogServiceInterface, qui sera utilis�e pour la transmission de messages
	* de log.
	*
	* Arguments:
	*  - logServiceInterface: Une r�f�rence sur l'interface LogServiceInterface.
	* ----------------------------------------------------------*/
	protected static synchronized void setLogServiceInterface(
		LogServiceInterface logServiceInterface
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LogServiceProxy", "setLogServiceInterface");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("logServiceInterface=" +
			logServiceInterface);
		// On positionne la r�f�rence sur l'interface du service de log
		_logServiceInterface = logServiceInterface;
		// On s'assure que l'adresse IP est fix�e
		getIpAddress();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: clearLogServiceInterface
	*
	* Description:
	* Cette m�thode statique permet de d�positionner la r�f�rence sur
	* l'interface LogServiceInterface, maintenue dans l'attribut
	* _logServiceInterface. Elle doit �tre appel�e lorsque la connexion avec le
	* Portail est ferm�e ou perdue.
	* ----------------------------------------------------------*/
	protected static synchronized void clearLogServiceInterface()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LogServiceProxy", "clearLogServiceInterface");

		trace_methods.beginningOfMethod();
		// On lib�re les r�f�rences
		_logServiceInterface = null;
		_ipAddress = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _ipAddress
	*
	* Description:
	* Cet attribut statique a pour but de maintenir l'adresse IP de la Console,
	* d�termin�e au moment du positionnement de l'interface du service de log.
	* ----------------------------------------------------------*/
	private static String _ipAddress = null;

	/*----------------------------------------------------------
	* Nom: _logServiceInterface
	*
	* Description:
	* Cet attribut statique maintient une r�f�rence sur l'interface
	* LogServiceInterface qui sera utilis�e pour la transmission des messages
	* de log.
	* ----------------------------------------------------------*/
	private static LogServiceInterface _logServiceInterface = null;
}