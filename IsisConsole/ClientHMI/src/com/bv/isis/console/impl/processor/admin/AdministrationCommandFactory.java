/*------------------------------------------------------------
* Copyright (c) 2008 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/AdministrationCommandFactory.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de construction de commande d'administration
* DATE:        16/06/2008
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: AdministrationCommandFactory.java,v $
* Revision 1.3  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2008/08/25 10:51:53  tz
* Ajout de back-slash pour les " sur Windows.
*
* Revision 1.1  2008/06/16 11:28:23  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin;

//
//Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.config.ConfigurationAPI;

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: AdministrationCommandFactory
* 
* Description:
* Cette classe abstraite est une classe technique chargée de la construction 
* des commandes d'administration de données, via la méthode statique 
* buildAdministrationCommand().
* ----------------------------------------------------------*/
public abstract class AdministrationCommandFactory {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: buildAdministrationCommand
	* 
	* Description:
	* Cette méthode statique permet de récupérer la commande d'administration 
	* permettant l'insertion, la modification ou la suppression de données en 
	* fonction de l'action passée en argument.
	* 
	* Arguments:
	*  - action: "Insert", "Replace" ou "Remove" en fonction de l'action 
	*    d'administration requise,
	*  - tableName: Le nom de la table concernée par la commande 
	*    d'administration,
	*  - valueString: La chaîne contenant l'ensemble des valeurs à utiliser 
	*    dans la commande d'administration,
	*  - agentLayerMode: Le type de la couche d'exécution de l'Agent.
	* 
	* Retourne : La commande d'administration, ou null.
	* ----------------------------------------------------------*/
	public static String buildAdministrationCommand(
		String action,
		String tableName,
		String valueString,
		String agentLayerMode
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationCommandFactory", "buildAdministrationCommand");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String insert_executable = "InsertAndExec";
		String replace_executable = "ReplaceAndExec";
		String remove_executable = "RemoveAndExec";
		StringBuffer command = new StringBuffer();

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("action=" + action);
		trace_arguments.writeTrace("tableName=" + tableName);
		trace_arguments.writeTrace("valueString=" + valueString);
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		// S'il n'y a pas de valeur, on sort
		if(valueString == null || valueString.equals("") == true) {
			trace_methods.endOfMethod();
			return null;
		}
		// On récupère les informations depuis la configuration
		try {
			ConfigurationAPI configuration_api = new ConfigurationAPI();
			insert_executable = configuration_api.getString("I-TOOLS",
				"InsertionExecutable");
			replace_executable =
				configuration_api.getString("ModificationExecutable");
			remove_executable =
				configuration_api.getString("RemovalExecutable");
		}
		catch(Exception exception) {
			trace_errors.writeTrace(
				"Erreur lors de la récupération de la configuration: " +
				exception);
			// Il y a eu une erreur, on continue quand même
		}
		// On va construire la commande à exécuter suivant l'action
		if(action.equals("Insert") == true || 
			action.equals("Replace") == true) {
			if(action.equals("Insert") == true) {
				command.append(insert_executable);
			}
			else {
				command.append(replace_executable);
			}
			// Si la commande est une insertion ou une modification, il faut
			// ajouter le mot clé "into" après l'action
			command.append(" into ");
		}
		else {
			command.append(remove_executable);
			// La commande est une suppression, il faut ajouter le mot clé
			// "from" après l'action
			command.append(" from ");
		}
		// On ajoute le nom de la table
		command.append(tableName);
		// Puis les valeurs
		command.append(" values \"" + 
			addSlashes(valueString, agentLayerMode) + "\"");
		// On retourne la commande
		trace_methods.endOfMethod();
		return command.toString();
	}

	/*----------------------------------------------------------
	* Nom: addSlashes
	* 
	* Description:
	* Cette méthode statique est chargée d'ajouter des caractères d'échapement 
	* '\' devant certains caractères contenus dans les valeurs afin qu'ils ne 
	* soient pas supprimés ou interprétés par le shell au moment de 
	* l'exécution.
	* Les caractères à "barrer" sont la guillemet et le dollar.
	* 
	* Arguments:
	*  - valueString: La chaîne de valeurs a analyser pour rechercher les 
	*    caractères à "barrer",
	*  - agentLayerMode: Le type de la couche d'exécution de l'Agent.
	* 
	* Retourne: La chaîne de valeurs éventuellement modifiée.
	* ----------------------------------------------------------*/
	public static String addSlashes(
		String valueString,
		String agentLayerMode
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministrationCommandFactory", "addSlashes");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		StringBuffer new_values = new StringBuffer();
		int pos;
		int start = 0;
		char[] chars_to_slash_unix = { '"', '$' };
		char[] chars_to_slash_windows = { '"' };
		char[] chars_to_slash = null;
		int char_loop = 0;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("valueString=" + valueString);
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		// S'il n'y a pas de valeur, on sort
		if(valueString == null || valueString.equals("") == true) {
			trace_methods.endOfMethod();
			return valueString;
		}
		if(agentLayerMode.equalsIgnoreCase("WINDOWS") == true) {
			chars_to_slash = chars_to_slash_windows;
		}
		else {
			chars_to_slash = chars_to_slash_unix;
		}
		// On va rechercher les caractères à barrer un par un
		while(true) {
			// On recherche la position du caractère à barrer dans
			// la chaîne
			pos = valueString.indexOf(chars_to_slash[char_loop], start);
			if(pos == -1) {
				// Il n'y a pas ou plus d'occurence du caractère à barrer
				// dans la chaîne
				new_values.append(valueString.substring(start));
				char_loop ++;
				// Y a-t-il d'autres caractères à rechercher ?
				if(char_loop == chars_to_slash.length) {
					// Il n'y a plus de caractères à rechercher, on peut
					// sortir
					break;
				}
				// On repart dans une nouvelle recherche
				valueString = new_values.toString();
				new_values = new StringBuffer();
				start = 0;
				continue;
			}
			// On a trouvé un caractère à barrer, on va lui ajouter le slash
			new_values.append(valueString.substring(start, pos));
			new_values.append("\\" + chars_to_slash[char_loop]);
			start = pos + 1;
		}
		trace_methods.endOfMethod();
		return new_values.toString();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
