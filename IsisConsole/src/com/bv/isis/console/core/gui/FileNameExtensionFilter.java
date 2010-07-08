/*------------------------------------------------------------
* Copyright (c) 2008 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/FileNameExtensionFilter.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de filtrage sur extension de fichier
* DATE:        14/02/2008
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: FileNameExtensionFilter.java,v $
* Revision 1.2  2009/01/14 14:18:15  tz
* Classe déplacée dans le package com.bv.isis.console.core.gui.
*
* Revision 1.1  2008/02/15 14:14:48  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.gui;

//
//Imports système
//
import java.io.File;
import javax.swing.filechooser.FileFilter;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: FileNameExtensionFilter
* 
* Description:
* Cette classe est une spécialisation de la classe FileFilter afin de 
* permettre la gestion simple du filtrage des fichiers par extension.
* Elle redéfinit les méthodes accept() et getDescription(), auxquelles 
* sont ajoutées la méthode getExtension().
* ----------------------------------------------------------*/
public class FileNameExtensionFilter 
	extends FileFilter {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: FileNameExtensionFilter
	*
	* Description:
	* Ce constructeur est le seul à pouvoir être utilisé. Il permet de 
	* définir l'extension associée au filtre et sa description.
	*
	* Arguments:
	*  - description: La description du filtre,
	*  - extension: L'extension de filtrage.
 	* ----------------------------------------------------------*/
	public FileNameExtensionFilter(
		String description,
		String extension
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileNameExtensionFilter", "FileNameExtensionFilter");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("description=" + description);
		trace_arguments.writeTrace("extension=" + extension);
		_description = description;
		// On s'assure que l'extension contient un point
		if(extension.charAt(0) != '.') {
			_extension = "." + extension;
		}
		else {
			_extension = extension;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: accept
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe FileFilter. Elle 
	* est appelée afin de savoir si le fichier passé en argument est 
	* accepté ou non par le filtre.
	* Le fichier est accepté par le filtre lorsque le fichier passé en 
	* argument dispose d'une extension correspondant à celle qui est 
	* associée à ce filtre.
	* 
	* Arguments:
	*  - file: Le fichier à tester vis à vis du filtre.
	* 
	* Retourne: true si le fichier est accepté par le filtre, false sinon.
	* ----------------------------------------------------------*/
	public boolean accept(
		File file
		) {
		/*Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileNameExtensionFilter", "accept");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");*/
		boolean file_accepted = false;
		String file_name = null;
		String file_extension = "";
		int dot_position = -1;
		
		//trace_methods.beginningOfMethod();
		//trace_arguments.writeTrace("file=" + file);
		// On vérifie la validité de l'argument
		if(file == null) {
			//trace_methods.endOfMethod();
			return false;
		}
		// S'il s'agit d'un répertoire on retourne vrai pour qu'il soit
		// affiché
		if(file.isDirectory() == true) {
			//trace_debug.writeTrace("Il s'agit d'un répertoire");
			//trace_methods.endOfMethod();
			return true;
		}
		file_name = file.getName();
		dot_position = file_name.lastIndexOf('.');
		if(dot_position > 0) {
			file_extension = file_name.substring(dot_position);
		}
		//trace_debug.writeTrace("file_name=" + file_name);
		//trace_debug.writeTrace("file_extension=" + file_extension);
		if(file_extension.equalsIgnoreCase(_extension) == true) {
			//trace_debug.writeTrace("Le fichier correspond au filtre");
			file_accepted = true;
		}
		else {
			//trace_debug.writeTrace("Le fichier ne correspond pas au filtre");
		}
		//trace_methods.endOfMethod();
		return file_accepted;
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe FileFilter. Elle 
	* permet de récupérer la description du filtre.
	* 
	* Retourne: La description du filtre.
	* ----------------------------------------------------------*/
	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileNameExtensionFilter", "getDescription");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _description;
	}

	/*----------------------------------------------------------
	* Nom: getExtension
	* 
	* Description:
	* Cette méthode permet de récupérer l'extension de filtrage associée.
	* 
	* Retourne: L'extension de filtrage.
	* ----------------------------------------------------------*/
	public String getExtension() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileNameExtensionFilter", "getExtension");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _extension;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _extension
	* 
	* Description:
	* Cet attribut maintient l'extension associée au filtre, telle que 
	* passée au constructeur.
	* ----------------------------------------------------------*/
	private String _extension;

	/*----------------------------------------------------------
	* Nom: _description
	* 
	* Description:
	* Cet attribut maintient la description du filtre, telle que passée au 
	* constructeur.
	* ----------------------------------------------------------*/
	private String _description;

	/*----------------------------------------------------------
	* Nom: FileNameExtensionFilter
	* 
	* Description:
	* Ce constructeur ne doit pas être utilisé. Il n'est présenté que pour 
	* des raisons de lisibilité.
	* ----------------------------------------------------------*/
	private FileNameExtensionFilter() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileNameExtensionFilter", "FileNameExtensionFilter");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}
}
