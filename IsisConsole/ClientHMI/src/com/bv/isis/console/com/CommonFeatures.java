/*------------------------------------------------------------
* Copyright (c) 2002 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/CommonFeatures.java,v $
* $Revision: 1.13 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de factorisation de méthodes
* DATE:        15/03/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: CommonFeatures.java,v $
* Revision 1.13  2009/01/14 14:21:58  tz
* Prise en compte de la modification des packages.
*
* Revision 1.12  2006/11/03 10:25:48  tz
* Traitement des exceptions FileNotReachableException et
* FileNotCreatableException.
*
* Revision 1.11  2005/07/05 15:04:03  tz
* Traitement des exceptions FileNotFoundException,
* FileNotReadableException et FileNotWritableException.
*
* Revision 1.10  2005/07/01 12:29:26  tz
* Modification du composant pour les traces
*
* Revision 1.9  2004/11/09 15:26:50  tz
* Modification d'un commentaire.
*
* Revision 1.8  2004/11/05 10:43:42  tz
* Traitement des exceptions de type InnerException.
*
* Revision 1.7  2004/10/13 14:03:32  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.6  2004/07/29 12:24:42  tz
* Mise à jour de la documentation
*
* Revision 1.5  2003/12/08 14:37:55  tz
* Mise à jour du modèle
*
* Revision 1.4  2003/03/17 16:49:49  tz
* Correction de la fiche Inuit/105
*
* Revision 1.3  2003/03/07 16:22:54  tz
* Prise en compte du mécanisme de log métier
*
* Revision 1.2  2002/04/05 15:47:03  tz
* Cloture itération IT1.2
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
import com.bv.core.message.MessageManager;
import org.omg.CORBA.COMM_FAILURE;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Vector;

//
// Imports du projet
//
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.ExecutionException;
import com.bv.isis.corbacom.InvalidLoginException;
import com.bv.isis.corbacom.InvalidPasswordException;
import com.bv.isis.corbacom.NoSuchAgentException;
import com.bv.isis.corbacom.NoSuchServiceException;
import com.bv.isis.corbacom.NoUserAccessException;
import com.bv.isis.corbacom.ProcessStateException;
import com.bv.isis.corbacom.UnknownException;
import com.bv.isis.corbacom.InvalidIdlRevisionException;
import com.bv.isis.corbacom.FileNotFoundException;
import com.bv.isis.corbacom.FileNotReadableException;
import com.bv.isis.corbacom.FileNotWritableException;
import com.bv.isis.corbacom.FileNotReachableException;
import com.bv.isis.corbacom.FileNotCreatableException;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;

/*----------------------------------------------------------
* Nom: CommonFeatures
*
* Description:
* Cette classe abstraite est une classe technique permettant de factoriser des
* méthodes qui sont communes à plusieurs classes du package.
* Ces méthodes consistent à construire une exception InnerException en fonction
* d'une autre exception (méthode processException()), à transformer une
* liste indexée en un tableau d'IsisParameter nécessaire à l'exécution
* de requêtes sur les sessions de service (méthode buildParametersArray()), et
* enfin à construire un tableau de chaînes à partir du découpage d'une chaîne
* en utilisant le retour à la ligne comme caractère de séparation (méthode
* buildArrayFromString()).
* ----------------------------------------------------------*/
public abstract class CommonFeatures
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: buildParametersArray
	*
	* Description:
	* Cette méthode statique permet de créer un tableau d'IsisParameter
	* à partir des valeurs contenues dans la liste indexée passée en paramètre.
	* Les valeurs de la liste indexée sont des instances d'IsisParameter.
	* Ce tableau est utilisé en tant qu'argument de la majorité des méthodes de
	* l'interface ServiceSessionInterface.
	*
	* Argument:
	*  - context: Une référence sur une liste indexée dont les données sont à
	*    convertir en tableau.
	*
	* Retourne: Le tableau d'IsisParameter résultant de la conversion.
	* ----------------------------------------------------------*/
	public static IsisParameter[] buildParametersArray(
		IndexedList context
		)
	{
		IsisParameter[] parameters_array = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CommonFeatures", "buildParametersArray");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("context=" + context);
		// Vérification de l'intégrité de l'argument
		if(context == null)
		{
			trace_methods.endOfMethod();
			return parameters_array;
		}
		// Récupération des valeurs de la table de Hash (qui sont des instances
		// de IsisParameter)
		parameters_array = (IsisParameter[])context.toArray(
			new IsisParameter[0]);
		// On affiche le contenu de chaque paramètre (pour débug)
		for(int index = 0 ; index < parameters_array.length ; index ++)
		{
			trace_debug.writeTrace("Paramètre: " + parameters_array[index].name +
				"=" + parameters_array[index].value);
		}
		trace_methods.endOfMethod();
		return parameters_array;
	}

	/*----------------------------------------------------------
	* Nom: processException
	*
	* Description:
	* Cette méthode statique permet de factoriser le code de traitement des
	* erreurs qui peuvent être attrapées lors de communications au travers du
	* bus CORBA.
	* Elle construit une nouvelle exception InnerException contenant des
	* informations dépendant de l'exception qui a été reçue.
	*
	* Arguments:
	*  - message: Un message indiquant la requête qui a levé l'exception,
	*  - exception: Une référence sur l'exception qui a été récupérée.
	*
	* Retourne: Une exception InnerException contenant les informations
	* pertinentes à propos de l'erreur.
	* ----------------------------------------------------------*/
	public static InnerException processException(
		String message,
		Exception exception
		)
	{
		InnerException inner_exception = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CommonFeatures", "processException");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("message=" + message);
		trace_arguments.writeTrace("exception=" + exception);
		trace_debug.writeTrace("Exception de type " + exception.getClass().getName());
		trace_errors.writeTrace("Erreur lors " + message + ": " +
			exception);
		// Test des types de l'exception
		if(exception instanceof ExecutionException)
		{ // Exception d'exécution de procédure
			ExecutionException execution_exception =
				(ExecutionException)exception;
			trace_debug.writeTrace("Commande: " + execution_exception.command);
			trace_debug.writeTrace("Message d'erreur: " +
				execution_exception.errorMessage);
			// Construction de la raison de l'erreur
			String[] extra_info = {execution_exception.command};
			String reason = MessageManager.fillInMessage(
				MessageManager.getMessage("&ERR_ExecutionException"),
				extra_info);
			// Construction de l'exception
			inner_exception = new InnerException(reason,
				execution_exception.errorMessage, exception);
		}
		else if(exception instanceof InvalidLoginException)
		{ // Exception d'identifiant incorrect
			InvalidLoginException login_exception =
				(InvalidLoginException)exception;
			trace_debug.writeTrace("Utilisateur inconnu: " +
				login_exception.userName);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_InvalidLogin",
				login_exception.userName, exception);
		}
		else if(exception instanceof InvalidPasswordException)
		{ // Exception de mot de passe incorrect
			InvalidPasswordException password_exception =
				(InvalidPasswordException)exception;
			trace_debug.writeTrace("Mot de passe incorrect: " +
				password_exception.userName);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_InvalidPassword",
				password_exception.userName, exception);
		}
		else if(exception instanceof NoSuchAgentException)
		{ // Exception d'Agent inconnu
			NoSuchAgentException agent_exception =
				(NoSuchAgentException)exception;
			trace_debug.writeTrace("Agent inconnu: " +
				agent_exception.agentName);
			// Construction de la raison de l'erreur
			String[] extra_info = {agent_exception.agentName};
			String reason = MessageManager.fillInMessage(
				MessageManager.getMessage("&ERR_UnknownAgent"), extra_info);
			// Construction de l'exception
			inner_exception = new InnerException(reason, null, exception);
		}
		else if(exception instanceof NoSuchServiceException)
		{ // Exception de service inconnu
			NoSuchServiceException service_exception =
				(NoSuchServiceException)exception;
			trace_debug.writeTrace("Service inconnu: " +
				service_exception.serviceName + ", type: " +
				service_exception.serviceType + ", I-CLES: " +
				service_exception.iClesName);
			// Construction de la raison de l'erreur
			String[] extra_info = {service_exception.serviceName,
				service_exception.serviceType, service_exception.iClesName };
			String reason = MessageManager.fillInMessage(
				MessageManager.getMessage("&ERR_UnknownService"), extra_info);
			// Construction de l'exception
			inner_exception = new InnerException(reason, null, exception);
		}
		else if(exception instanceof NoUserAccessException)
		{ // Exception d'absence d'accès pour l'utilisateur
			NoUserAccessException access_exception =
				(NoUserAccessException)exception;
			trace_debug.writeTrace("Aucun accès utilisateur: " +
				access_exception.userName + ", service: " +
				access_exception.serviceName + ", type: " +
				access_exception.serviceType + ", I-CLES: " +
				access_exception.iClesName);
			// Construction de la raison de l'erreur
			String[] extra_info = {access_exception.userName,
				access_exception.serviceName, access_exception.serviceType,
				access_exception.iClesName };
			String reason = MessageManager.fillInMessage(
				MessageManager.getMessage("&ERR_NoUserAccess"), extra_info);
			// Construction de l'exception
			inner_exception = new InnerException(reason, null, exception);
		}
		else if(exception instanceof ProcessStateException)
		{ // Exception d'état de processus fils incorrect
			ProcessStateException process_exception =
				(ProcessStateException)exception;
			trace_debug.writeTrace("Problème de process: " +
				process_exception.reason);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_ProcessException",
				process_exception.reason, exception);
		}
		else if(exception instanceof UnknownException)
		{ // Exception inconnue
			UnknownException unknown_exception =
				(UnknownException)exception;
			trace_debug.writeTrace("Problème: " + unknown_exception.reason);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_UnknownProblem",
				unknown_exception.reason, exception);
		}
		else if(exception instanceof InvalidIdlRevisionException)
		{ // Exception d'erreur de version d'IDL
			InvalidIdlRevisionException revision_exception =
				(InvalidIdlRevisionException)exception;
			trace_debug.writeTrace("Erreur de version d'IDL: " + 
				revision_exception.expectedRevision);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_InvalidIdlRevision",
				revision_exception.expectedRevision, exception);
		}
		else if(exception instanceof FileNotFoundException)
		{ // Exception de fichier introuvable 
			FileNotFoundException file_exception =
				(FileNotFoundException)exception;
			trace_debug.writeTrace("Fichier introuvable: " +
				file_exception.fileName);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_FileDoesNotExist",
				file_exception.fileName, exception);
		}
		else if(exception instanceof FileNotReadableException)
		{ // Exception de fichier illisible pour l'utilisateur
			FileNotReadableException file_exception =
				(FileNotReadableException)exception;
			trace_debug.writeTrace("Fichier illisible: " +
				file_exception.fileName);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_FileIsNotReadable",
				file_exception.fileName, exception);
		}
		else if(exception instanceof FileNotWritableException)
		{ // Exception de fichier non inscriptible
			FileNotWritableException file_exception = 
				(FileNotWritableException)exception;
			trace_debug.writeTrace("Fichier non inscriptible: " +
				file_exception.fileName);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_FileIsNotWritable",
				file_exception.fileName, exception);
		}
		else if(exception instanceof FileNotReachableException)
		{ // Exception de fichier non accessible
			FileNotReachableException reach_exception =
				(FileNotReachableException)exception;
			trace_debug.writeTrace("Fichier non accessible: " +
				reach_exception.fileName);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_FileIsNotReachable",
				reach_exception.fileName, exception);
		}
		else if(exception instanceof FileNotCreatableException)
		{ // Exception de fichier non accessible
			FileNotCreatableException create_exception =
				(FileNotCreatableException)exception;
			trace_debug.writeTrace("Fichier non créable: " +
				create_exception.fileName);
			// Construction de l'exception
			inner_exception = new InnerException("&ERR_FileIsNotCreatable",
				create_exception.fileName, exception);
		}
		else if(exception instanceof COMM_FAILURE)
		{ // Exception d'erreur de communication CORBA
			Trace trace_error = TraceAPI.declareTraceErrors("Console");
			trace_error.writeTrace(
				"Erreur de communication avec un autre processus: " +
				exception);
			// Il s'agit d'une exception de communication
			inner_exception = new InnerException(exception.getMessage(), null,
				exception);
		}
		else if(exception instanceof InnerException)
		{ // Exception interne
			trace_debug.writeTrace("Erreur interne: " + 
				exception.getMessage());
			inner_exception = (InnerException)exception;
		}
		else
		{ // Exception non répertoriée
		    // Tout autre type d'exception, le message est standard
			trace_debug.writeTrace("message=" + exception.toString());
			inner_exception = new InnerException(exception.getClass().getName(),
				exception.getMessage(), exception);
		}
		trace_methods.endOfMethod();
		return inner_exception;
	}

	/*----------------------------------------------------------
	* Nom: buildArrayFromString
	*
	* Description:
	* Cette méthode convertit une chaîne de caractères en un tableau de chaînes,
	* en découpant la chaîne passée en argument sur la présence du caractère
	* de retour à la ligne.
	* Cette méthode n'est utilisée que pour construire un tableau nécessaire
	* au service de log lors de l'enregistrement d'un message (les caractères
	* '{' et '}' sont ajoutés en début et en fin de tableau).
	*
	* Arguments:
	*  - theString: La chaîne à convertir.
	*
	* Retourne: Un tableau de chaînes de caractères résultant de la conversion.
	* ----------------------------------------------------------*/
	public static String[] buildArrayFromString(
		String theString
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CommonFeatures", "buildArrayFromString");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("theString=" + theString);
		if(theString == null || theString.equals("") == true)
		{
			return new String[0];
		}
		Vector array = new Vector();
		BufferedReader reader = new BufferedReader(new StringReader(theString));
		// On ajoute le caractère d'ouverture
		array.add(MessageManager.getMessage("&LOG_OpeningQuote"));
		while(true)
		{
			try
			{
				// On lit une ligne
				String line = reader.readLine();
				if(line == null)
				{
					// C'est la fin, on sort de la boucle
					break;
				}
			    array.add("\t" + line);
			}
			catch(Exception e)
			{
				// On sort de la boucle
				break;
			}
		}
		// On ajoute le caractère de fermeture
		array.add(MessageManager.getMessage("&LOG_ClosingQuote"));
		trace_methods.endOfMethod();
		// On retourne un tableau de chaînes
		return (String[])array.toArray(new String[0]);
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}