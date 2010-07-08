/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/common/AgentLayerAbstractor.java,v $
* $Revision: 1.7 $
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
* Revision 1.7  2009/02/27 11:32:36  tz
* Correction de la fiche FS#595 : Ajout des caract�res '&', '|', '<' et
* '>' dans la liste des caract�res � �chapper pour Windows.
*
* Revision 1.6  2009/02/25 16:38:23  tz
* Transfert de la m�thode addSlashes() depuis la classe
* AdministrationCommandFactory.
* Ajout des attributs _charsToEscapeForUnix et
* _charsToEscapeForWindows.
* R�solution de la fiche FS#622.
*
* Revision 1.5  2009/01/14 12:28:30  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.common.
*
* Revision 1.4  2008/02/19 15:55:11  tz
* Ajout de la m�thode getFileName().
*
* Revision 1.3  2006/11/09 11:59:31  tz
* Correction de la m�thode buildFilePath().
* Ajout de la m�thode getLineSeparator().
*
* Revision 1.2  2006/11/03 10:27:39  tz
* Ajout des m�thodes buildFilePath() et getPathSeparator().
*
* Revision 1.1  2006/10/13 15:08:35  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.common;

//
//Imports syst�me
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
* Cette classe est une classe abstraite charg�e de r�aliser des conversions en 
* fonction du mode de la couche d'ex�cution des Agent I-SIS.
* Il s'agit, par exemple, de formatter une r�f�rence � une variable 
* d'environnement correctement en fonction de la couche d'ex�cution.
* ----------------------------------------------------------*/
public abstract class AgentLayerAbstractor
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getVariableReference
	* 
	* Description:
	* Cette m�thode statique est charg�e de formatter une r�f�rence � une 
	* variable d'environnement en fonction du mode de la couche d'ex�cution 
	* d'un Agent.
	* En mode "Windows", une r�f�rence � une variable d'environnement est 
	* effectu�e via la syntaxe "%<Variable>%".
	* En mode "Unix", une r�f�rence � une variable d'environnement est 
	* effectu�e via la syntaxe "${<Variable>}".
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent,
	*  - variableName: Le nom de la variable d'environnement � r�f�rencer.
	* 
	* Retourne: Une cha�ne contenant une r�f�rence � la variable 
	* d'environnement formatt�e en fonction du mode.
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
		else // On consid�re le reste comme des Unix
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
	* Cette m�thode est charg�e de la construction d'un chemin d'un fichier 
	* en fonction du mode de la couche d'ex�cution d'un Agent.
	* En mode "Windows", le chemin est construit � partir de la formule 
	* "<directory>\<fileName>".
	* En mode "Unix", le chemin est construit � partir de la formule 
	* "<directory>/<fileName>".
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent,
	*  - directory: Le chemin du r�pertoire parent du fichier,
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
			// On regarde si le r�pertoire correspond � une racine
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
						// On va extraire le chemin du r�pertoire parent
						file_path = directory.substring(0, last_position);
					}
					else if(last_position == 2)
					{
						file_path = directory.substring(0, last_position + 1);
					}
				}
			}
		}
		else // On consid�re le reste comme des Unix
		{
			// Il se peut qu'il y ait des '\' malgr� que l'Agent
			// soit en mode Unix
			if(directory.indexOf('\\') != -1)
			{
				// On va demander un traitement style Windows
				file_path = buildFilePath("Windows", directory,
					fileName);
				trace_methods.endOfMethod();
				return file_path;
			}
			// On regarde si le r�pertoire correspond � la racine
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
						// On va extraire le chemin du r�pertoire parent
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
	* Cette m�thode statique permet de r�cup�rer la cha�ne correspondant au 
	* s�parateur de chemins dans les liste de chemins.
	* Sur les plates-formes Windows, le s�parateur de chemins est ";", tandis 
	* que sur les plates-formes Unix, le s�parateur est ":".
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent.
	* 
	* Retourne: Le s�parateur de chemins d�pendant du mode de la couche 
	* d'ex�cution.
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
	* Cette m�thode statique permet de r�cup�rer la cha�ne correspondant au 
	* s�parateur de lignes pour le type de couche d'ex�cution pass�e en 
	* argument.
	* Sur les plates-formes Windows, le s�parateur de lignes est "\r\n", 
	* tandis que sur les plates-formes Unix, le s�parateur est "\n".
	* 
	* Arguments:
	*  - agentLayerMode: Le mode de la couche d'ex�cution de l'Agent.
	* 
	* Retourne: Le s�parateur de lignes d�pendant du mode de la couche 
	* d'ex�cution.
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
	* Cette m�thode statique permet d'extraire le nom du fichier du chemin 
	* pass� en premier argument. Le caract�re de s�paration des r�pertoires 
	* d�pend du mode de l'agent, pass� en second argument.
	* Si le mode de l'agent est "Windows", le caract�re de s�paration des 
	* chemins est '\', dans le cas contraire, le caract�re vaut '/'.
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
		// On teste la validit� des arguments
		if(filePath == null || agentLayerMode == null) {
			trace_methods.endOfMethod();
			return file_name;
		}
		if(agentLayerMode.equalsIgnoreCase("WINDOWS") == true)
		{
			path_separator = '\\';
		}
		// On va chercher la derni�re position du caract�re de
		// s�paration
		last_separator_position = filePath.lastIndexOf(path_separator);
		// S'il n'y a pas de s�parateur, on retourne le chemin pass� en
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

	/*----------------------------------------------------------
	* Nom: addSlashes
	* 
	* Description:
	* Cette m�thode statique est charg�e d'ajouter des caract�res 
	* d'�chapement ('\' ou '^') devant certains caract�res contenus dans les 
	* valeurs afin qu'ils ne soient pas supprim�s ou interpr�t�s par le shell 
	* ou l'interpr�teur de commandes Windows au moment de l'ex�cution.
	* Les caract�res � �chapper sont :
	*  - Sur Unix : la guillemet et le dollar,
	*  - Sur Windows : la guillemet et le pourcent.
	* 
	* Arguments:
	*  - valueString: La cha�ne de valeurs a analyser pour rechercher les 
	*    caract�res � �chapper,
	*  - agentLayerMode: Le type de la couche d'ex�cution de l'Agent.
	* 
	* Retourne: La cha�ne de valeurs �ventuellement modifi�e.
	* ----------------------------------------------------------*/
	public static String addSlashes(
		String valueString,
		String agentLayerMode
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AgentLayerAbstractor", "addSlashes");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		StringBuffer new_values = new StringBuffer();
		int pos;
		int start = 0;
		String[][] chars_to_escape = null;
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
			chars_to_escape = _charsToEscapeForWindows;
		}
		else {
			chars_to_escape = _charsToEscapeForUnix;
		}
		// On va rechercher les caract�res � �chapper un par un
		while(true) {
			// On recherche la position du caract�re � �chapper dans
			// la cha�ne
			pos = valueString.indexOf(chars_to_escape[char_loop][0], start);
			if(pos == -1) {
				// Il n'y a pas ou plus d'occurence du caract�re � �chapper
				// dans la cha�ne
				new_values.append(valueString.substring(start));
				char_loop ++;
				// Y a-t-il d'autres caract�res � rechercher ?
				if(char_loop == chars_to_escape.length) {
					// Il n'y a plus de caract�res � rechercher, on peut
					// sortir
					break;
				}
				// On repart dans une nouvelle recherche
				valueString = new_values.toString();
				new_values = new StringBuffer();
				start = 0;
				continue;
			}
			// On a trouv� un caract�re � �chapper, on va lui ajouter le 
			// caract�re d'�chappement ad�quat
			new_values.append(valueString.substring(start, pos));
			new_values.append(chars_to_escape[char_loop][1]);
			new_values.append(chars_to_escape[char_loop][0]);
			start = pos + chars_to_escape[char_loop][0].length();
		}
		trace_methods.endOfMethod();
		return new_values.toString();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _charsToEscapeForUnix
	* 
	* Description:
	* Cet attribut statique correspond � un tableau de tableau. Chaque tableau 
	* contenu dans le premier tableau contient le caract�re � �chapper et le 
	* caract�re d'�chappement pour les plates-formes Unix.
	* ----------------------------------------------------------*/
	private static String[][] _charsToEscapeForUnix = {
		{"\"", "\\"},
		{"$", "\\"}
	};

	/*----------------------------------------------------------
	* Nom: _charsToEscapeForWindows
	* 
	* Description:
	* Cet attribut statique correspond � un tableau de tableau. Chaque tableau 
	* contenu dans le premier tableau contient le caract�re � �chapper et le 
	* caract�re d'�chappement pour les plates-formes Windows.
	* ----------------------------------------------------------*/
	private static String[][] _charsToEscapeForWindows = {
		{"\"", "\\"},
		{"%", "^%"},
		{"&", "^"},
		{"|", "^"},
		{"<", "^"},
		{">", "^"}
	};
}
