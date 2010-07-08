/*------------------------------------------------------------
* Copyright (c) 2008 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/AdministrationCommandFactory.java,v $
* $Revision: 1.4 $
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
* Revision 1.4  2009/02/25 16:39:44  tz
* Transfert de la méthode addSlashes() vers la classe
* AgentLayerAbstractor.
*
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
import com.bv.isis.console.core.common.AgentLayerAbstractor;

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
		command.append(" values \"" + AgentLayerAbstractor.addSlashes(
			valueString, agentLayerMode) + "\"");
		// On retourne la commande
		trace_methods.endOfMethod();
		return command.toString();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
