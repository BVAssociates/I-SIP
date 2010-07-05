/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/common/InnerException.java,v $
* $Revision: 1.7 $
*
* ------------------------------------------------------------
* DESCRIPTION: Exception de l'application
* DATE:        15/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      common
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: InnerException.java,v $
* Revision 1.7  2009/01/14 14:17:22  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.common.
*
* Revision 1.6  2007/12/07 10:29:00  tz
* Prise en compte du cas o� reason et/ou details est nul.
*
* Revision 1.5  2007/10/23 11:54:06  tz
* Traduction des messages de l'exception.
*
* Revision 1.4  2005/07/01 12:27:36  tz
* Modification du composant pour les traces
*
* Revision 1.3  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.2  2004/10/13 14:02:34  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.1  2001/11/16 08:55:44  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.common;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.message.MessageManager;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: InnerException
*
* Description:
* Cette exception est utilis�e pour signaler un probl�me � l'int�rieur de
* l'application. Elle permet de d�finir la raison de la lev�e de l'exception
* et d'un �ventuel d�tail.
* ----------------------------------------------------------*/
public class InnerException
	extends Exception
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: InnerException
	*
	* Description:
	* Cette m�thode est le constructeur de l'exception. Elle permet de
	* sp�cifier la raison de la lev�e de l'exception, et l'�ventuel d�tail sur
	* l'erreur.
	*
	* Arguments:
	*  - reason: La raison de la lev�e de l'exception,
	*  - details: Le d�tail �ventuel sur l'erreur,
	*  - originalException: L'exception d'origine, s'il y en a une.
	* ----------------------------------------------------------*/
	public InnerException(
		String reason,
		String details,
		Exception originalException
		)
	{
		super();
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"InnerException", "InnerException");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("reason=" + reason);
		trace_arguments.writeTrace("details=" + details);
		trace_arguments.writeTrace("originalException=" + originalException);
		if(reason != null && reason.startsWith("&") == true) {
			_reason = MessageManager.getMessage(reason);
		}
		else {
			_reason = reason;
		}
		if(details != null && details.startsWith("&") == true) {
			_details = MessageManager.getMessage(details);
		}
		else {
			_details = details;
		}
		if(originalException != null)
		{
			UtilStringTokenizer tokenizer = new UtilStringTokenizer(
				originalException.getClass().getName(), ".");
			_originalExceptionClassName =
				tokenizer.getToken(tokenizer.getTokensCount() - 1);
		}
		else
		{
			_originalExceptionClassName = "";
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getReason
	*
	* Description:
	* Cette m�thode permet de r�cup�rer la raison de la lev�e de l'exception,
	* telle qu'elle a �t� fournie au constructeur.
	*
	* Retourne: La raison de la lev�e de l'exception.
	* ----------------------------------------------------------*/
	public String getReason()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"InnerException", "getReason");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _reason;
	}

	/*----------------------------------------------------------
	* Nom: getDetails
	*
	* Description:
	* Cette m�thode permet de r�cup�rer le d�tail �ventuel de l'erreur ayant
	* caus� la lev�e de l'exception, telle qu'il a �t� fournie au constructeur.
	*
	* Retourne: Le d�tail �ventuel de l'erreur.
	* ----------------------------------------------------------*/
	public String getDetails()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"InnerException", "getDetails");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _details;
	}

	/*----------------------------------------------------------
	* Nom: getOriginalExceptionClassName
	*
	* Description:
	* Cette m�thode permet de r�cup�rer le nom de la classe de l'exception
	* d'origine. S'il n'y a pas d'exception d'origine, elle retourne une cha�ne
	* vide.
	*
	* Retourne: Le nom de l'exception d'origine ou une cha�ne vide.
	* ----------------------------------------------------------*/
	public String getOriginalExceptionClassName()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"InnerException", "getOriginalExceptionClassName");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _originalExceptionClassName;
	}

	/*----------------------------------------------------------
	* Nom: getMessage
	*
	* Description:
	* Cette m�thode permet de r�cup�rer le message de l'exception, constitu� de
	* la raison de la lev�e, et de l'�ventuel d�tail de l'erreur.
	*
	* Retourne: Le message de l'exception.
	* ----------------------------------------------------------*/
	public String getMessage()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"InnerException", "getMessage");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _reason + " (" + _details + ")";
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _reason
	*
	* Description:
	* Cet attribut contient un message d�crivant la raison de la lev�e de
	* l'exception.
	* ----------------------------------------------------------*/
	private String _reason;

	/*----------------------------------------------------------
	* Nom: _details
	*
	* Description:
	* Cet attribut contient un message contenant le d�tail de l'erreur ayant
	* caus�e la lev�e de l'exception.
	* ----------------------------------------------------------*/
	private String _details;

	/*----------------------------------------------------------
	* Nom: _originalExceptionClassName
	*
	* Description:
	* Cet attribut contient le nom de la classe de l'exception d'origine, s'il y
	* en a une.
	* ----------------------------------------------------------*/
	private String _originalExceptionClassName;
}