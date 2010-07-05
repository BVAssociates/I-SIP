/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/common/AgentLayerAbstractor.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe technique de conversion de formats
* DATE:        25/08/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      common
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: AgentLayerAbstractor.java,v $
* Revision 1.5  2009/01/14 12:28:30  tz
* Classe déplacée dans le package com.bv.isis.console.core.common.
*
* Revision 1.4  2008/02/19 15:55:11  tz
* Ajout de la méthode getFileName().
*
* Revision 1.3  2006/11/09 11:59:31  tz
* Correction de la méthode buildFilePath().
* Ajout de la méthode getLineSeparator().
*
* Revision 1.2  2006/11/03 10:27:39  tz
* Ajout des méthodes buildFilePath() et getPathSeparator().
*
* Revision 1.1  2006/10/13 15:08:35  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.common;

//
//Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: AgentLayerAbstractor
* 
* Description:
* Cette classe est une classe abstraite chargée de réaliser des conversions en 
* fonction du mode de la couche d'exécution des Agent I-SIS.
* Il s'agit, par exemple, de formatter une référence à une variable 
* d'environnement correctement en fonction de la couche d'exécution.
* ----------------------------------------------------------*/
public abstract class AgentLayerAbstractor
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getVariableReference
	* 
	* Description:
	* Cette méthode statique est chargée de formatter une référence à une 
	* variable d'environnement en fonction du mode de la couche d'exécution 
	* d'un Agent.
	* En mode "Windows", une référence à une variable d'environnement est 
	* effectuée via la syntaxe "%<Variable>%".
	* En mode "Unix", une référence à une variable d'environnement est 
	* effectuée via la syntaxe "${<Variable>}".
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent,
	*  - variableName: Le nom de la variable d'environnement à référencer.
	* 
	* Retourne: Une chaîne contenant une référence à la variable 
	* d'environnement formattée en fonction du mode.
	* ----------------------------------------------------------*/
	public static String getVariableReference(
		String agentLayerMode,
		String variableName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentLayerAbstractor", "getVariableReference");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String value;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		trace_arguments.writeTrace("variableName=" + variableName);
		// Si le mode est Windows, on va utiliser la syntaxe "%<Variable>%"
		if(agentLayerMode.equalsIgnoreCase("WINDOWS") == true)
		{
			value = "%" + variableName + "%";
		}
		else // On considère le reste comme des Unix
		{
			// On va utiliser la syntaxe "${<Variable>}"
			value = "${" + variableName + "}";
		}
		trace_debug.writeTrace("value=" + value);
		trace_methods.endOfMethod();
		return value;
	}

	/*----------------------------------------------------------
	* Nom: buildFilePath
	* 
	* Description:
	* Cette méthode est chargée de la construction d'un chemin d'un fichier 
	* en fonction du mode de la couche d'exécution d'un Agent.
	* En mode "Windows", le chemin est construit à partir de la formule 
	* "<directory>\<fileName>".
	* En mode "Unix", le chemin est construit à partir de la formule 
	* "<directory>/<fileName>".
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent,
	*  - directory: Le chemin du répertoire parent du fichier,
	*  - fileName: Le nom du fichier.
	* 
	* Retourne: Le chemin du fichier.
	* ----------------------------------------------------------*/
	public static String buildFilePath(
		String agentLayerMode,
		String directory,
		String fileName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentLayerAbstractor", "buildFilePath");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String file_path = directory;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		trace_arguments.writeTrace("directory=" + directory);
		trace_arguments.writeTrace("fileName=" + fileName);
		if(agentLayerMode.equalsIgnoreCase("WINDOWS") == true)
		{
			directory = directory.replace('/', '\\');
			file_path = directory;
			// On regarde si le répertoire correspond à une racine
			if(directory.length() == 3)
			{
				// Si on essaye de remonter d'un cran, il ne faut rien faire
				if(fileName.equals("..") == false)
				{
					// On n'a pas besoin d'ajouter les double slash
					file_path = directory + fileName;
				}
			}
			else
			{
				if(fileName.equals("..") == false)
				{
					// Si le mode est Windows, on va utiliser la syntaxe 
					// "<directory>\<fileName>"
					file_path = directory + '\\' + fileName;
				}
				else
				{
					int last_position = directory.lastIndexOf('\\');
					if(last_position > 2)
					{
						// On va extraire le chemin du répertoire parent
						file_path = directory.substring(0, last_position);
					}
					else if(last_position == 2)
					{
						file_path = directory.substring(0, last_position + 1);
					}
				}
			}
		}
		else // On considère le reste comme des Unix
		{
			// Il se peut qu'il y ait des '\' malgré que l'Agent
			// soit en mode Unix
			if(directory.indexOf('\\') != -1)
			{
				// On va demander un traitement style Windows
				file_path = buildFilePath("Windows", directory,
					fileName);
				trace_methods.endOfMethod();
				return file_path;
			}
			// On regarde si le répertoire correspond à la racine
			if(directory.equals("/") == true)
			{
				// Si on essaye de remonter d'un cran, il ne faut rien faire
				if(fileName.equals("..") == false)
				{
					// On n'a pas besoin d'ajouter le slash
					file_path = directory + fileName;
				}
			}
			else
			{
				if(fileName.equals("..") == false)
				{
					// On va utiliser la syntaxe "<directory>/<fileName>"
					file_path = directory + '/' + fileName;
				}
				else
				{
					int last_position = directory.lastIndexOf('/');
					if(last_position > 0)
					{
						// On va extraire le chemin du répertoire parent
						file_path = directory.substring(0, last_position);
					}
					else if(last_position == 0)
					{
						file_path = "/";
					}
				}
			}
		}
		trace_debug.writeTrace("file_path=" + file_path);
		trace_methods.endOfMethod();
		return file_path;
	}

	/*----------------------------------------------------------
	* Nom: getPathSeparator
	* 
	* Description:
	* Cette méthode statique permet de récupérer la chaîne correspondant au 
	* séparateur de chemins dans les liste de chemins.
	* Sur les plates-formes Windows, le séparateur de chemins est ";", tandis 
	* que sur les plates-formes Unix, le séparateur est ":".
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent.
	* 
	* Retourne: Le séparateur de chemins dépendant du mode de la couche 
	* d'exécution.
	* ----------------------------------------------------------*/
	public static String getPathSeparator(
		String agentLayerMode
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentLayerAbstractor", "getPathSeparator");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String path_separator = ":";
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		if(agentLayerMode.equalsIgnoreCase("WINDOWS") == true)
		{
			path_separator = ";";
		}
		trace_methods.endOfMethod();
		return path_separator;
	}

	/*----------------------------------------------------------
	* Nom: getLineSeparator
	* 
	* Description:
	* Cette méthode statique permet de récupérer la chaîne correspondant au 
	* séparateur de lignes pour le type de couche d'exécution passée en 
	* argument.
	* Sur les plates-formes Windows, le séparateur de lignes est "\r\n", 
	* tandis que sur les plates-formes Unix, le séparateur est "\n".
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'exécution de l'Agent.
	* 
	* Retourne: Le séparateur de lignes dépendant du mode de la couche 
	* d'exécution.
	* ----------------------------------------------------------*/
	public static String getLineSeparator(
		String agentLayerMode
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentLayerAbstractor", "getLineSeparator");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		String line_separator = "\n";
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		if(agentLayerMode.equalsIgnoreCase("WINDOWS") == true)
		{
			line_separator = "\r\n";
		}
		trace_methods.endOfMethod();
		return line_separator;
	}

	/*----------------------------------------------------------
	* Nom: getFileName
	* 
	* Description:
	* Cette méthode statique permet d'extraire le nom du fichier du chemin 
	* passé en premier argument. Le caractère de séparation des répertoires 
	* dépend du mode de l'agent, passé en second argument.
	* Si le mode de l'agent est "Windows", le caractère de séparation des 
	* chemins est '\', dans le cas contraire, le caractère vaut '/'.
	*  
	* Arguments:
	*  - filePath: Le chemin du fichier,
	*  - agentLayerMode: Le mode de l'Agent.
	* 
	* Retourne: Le nom du fichier, sans chemin.
	* ----------------------------------------------------------*/
	public static String getFileName(
		String filePath,
		String agentLayerMode
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console", 
			"AgentLayerAbstractor", "getFileName");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		char path_separator = '/';
		int last_separator_position = -1;
		String file_name = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("filePath=" + filePath);
		trace_arguments.writeTrace("agentLayerMode=" + agentLayerMode);
		// On teste la validité des arguments
		if(filePath == null || agentLayerMode == null) {
			trace_methods.endOfMethod();
			return file_name;
		}
		if(agentLayerMode.equalsIgnoreCase("WINDOWS") == true)
		{
			path_separator = '\\';
		}
		// On va chercher la dernière position du caractère de
		// séparation
		last_separator_position = filePath.lastIndexOf(path_separator);
		// S'il n'y a pas de séparateur, on retourne le chemin passé en
		// argument
		if(last_separator_position == -1) {
			file_name = filePath;
		}
		else {
			file_name = filePath.substring(last_separator_position + 1);
		}
		trace_methods.endOfMethod();
		return file_name;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
