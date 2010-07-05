/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/common/DialogManager.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'affichage des boîtes de dialogue
* DATE:        15/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      common
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DialogManager.java,v $
* Revision 1.6  2009/01/14 14:16:36  tz
* Classe déplacée dans le package com.bv.isis.console.core.common.
*
* Revision 1.5  2005/07/01 12:27:52  tz
* Modification du composant pour les traces
*
* Revision 1.4  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.3  2004/10/13 14:02:34  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.2  2001/12/19 09:58:57  tz
* Cloture itération IT1.0.0
*
* Revision 1.1  2001/11/16 08:55:44  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.common;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import java.awt.Component;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: DialogManager
*
* Description:
* Cette classe abstraite gère l'affichage de toutes les boîtes de dialogue de
* l'application. Elle propose un ensemble de méthodes statiques dépendant du
* type de boîte à afficher.
* ----------------------------------------------------------*/
public abstract class DialogManager
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: displayDialogForException
	*
	* Description:
	* Cette méthode statique permet d'afficher une boîte de dialogue normalisée
	* lors de la réception d'une exception.
	* Le contenu du message dépend de la nature de l'exception:
	*  - s'il s'agit d'une InnerException, le message est constitué de la
	*    raison, et de l'éventuel détail,
	*  - s'il s'agit d'une Exception, le message est constitué uniquement du
	*    message de l'exception.
	*
	* Arguments:
	*  - message: Le message principal de l'erreur,
	*  - parent: Une référence sur un Component afin d'afficher la boîte de
	*    dialogue au centre de celui-ci,
	*  - exception: Une référence sur l'exception contenant le message à
	*    afficher.
	* ----------------------------------------------------------*/
	public static void displayDialogForException(
		String message,
		Component parent,
		Exception exception
		)
	{
		StringBuffer error_message = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogManager", "displayDialogForException");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("message=" + message);
		trace_arguments.writeTrace("parent=" + parent);
		trace_arguments.writeTrace("exception=" + exception);
		// Test de la validité des arguments
		if(exception == null)
		{
			trace_methods.endOfMethod();
			return;
		}
		// La base du message est une traduction du message passé en argument
		error_message =
			new StringBuffer(MessageManager.getMessage(message));
		// Ajout de la chaîne raison
		error_message.append("\n");
		//error_message.append(
		//	MessageManager.getMessage("&DB_ReasonIs"));
		// Est-ce que l'exception est du type InnerException
		if(exception instanceof InnerException)
		{
			InnerException inner_exception = (InnerException)exception;
			// Ajout de la traduction de la raison du message
			if(inner_exception.getReason().startsWith("&") == true)
			{
				error_message.append(
					MessageManager.getMessage(inner_exception.getReason()));
			}
			else
			{
				error_message.append(inner_exception.getReason());
			}
			// Ajout de la chaîne détail
			error_message.append("\n\n");
			error_message.append(
				MessageManager.getMessage("&DB_ProblemDetails"));
			// Ajout de la traduction des détails
			String details = inner_exception.getDetails();
			if(details == null || details.equals("") == true)
			{
				error_message.append(
					MessageManager.getMessage("&DB_NoDetails"));
			}
			else if(details.startsWith("&") == true)
			{
				error_message.append(
					MessageManager.getMessage(details));
			}
			else
			{
				error_message.append(details);
			}
		}
		else // L'exception est d'un autre type
		{
			// Ajout de la raison
			error_message.append(exception.toString());
		}
		// Affichage de la boîte d'erreur
		MessageManager.displayDialog(error_message.toString(), null, "Error",
			parent);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: displayDialog
	*
	* Description:
	* Cette méthode statique permet d'afficher une boîte de dialogue à
	* l'utilisateur. La boîte de dialogue peut-être une boîte d'erreur
	* (popupType="Error" ou popupType="YesNoError" ou popupType="OkCancelError"),
	* une boîte d'information (popupType="Information") ou encore une boîte
	* d'interrogation (popupType="YesNoQuestion" ou  popupType="OkCancelQuestion").
	* Elle utilise le service de messages de la librairie BVCore/Java.
	*
	* Argument:
	*  - popupType: Le type de boîte de dialogue à afficher. Ce type correspond
	*    aux types définis pour la méthode displayDialog() de la classe
	*    MessageManager de la librairie BVCore/Java,
	*  - message: Le message à afficher dans la boîte de dialogue,
	*  - extraInfo: Un tableau de String contenant des informations
	*    complémentaires,
	*  - parent: Une référence sur le composant parent de la boîte de dialogue.
	*
	* Retourne: Un entier représentant le bouton sur lequel l'utilisateur a
	* cliqué pour fermer la boîte de dialogue. Cette valeur correspond aux
	* types définis dans la classe JOptionPane.
	* ----------------------------------------------------------*/
	public static int displayDialog(
		String popupType,
		String message,
		String[] extraInfo,
		Component parent
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"DialogManager", "displayDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("popupType=" + popupType);
		trace_arguments.writeTrace("message=" + message);
		trace_arguments.writeTrace("extraInfo=" + extraInfo);
		trace_arguments.writeTrace("parent=" + parent);
		// Utilisation de la classe MessageManager
		int reply = MessageManager.displayDialog(message, extraInfo,
			popupType, parent);
		trace_methods.endOfMethod();
		return reply;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}