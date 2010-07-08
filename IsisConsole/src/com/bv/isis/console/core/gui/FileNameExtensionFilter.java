/*------------------------------------------------------------
* Copyright (c) 2008 par BV Associates. Tous droits r�serv�s.
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
* Classe d�plac�e dans le package com.bv.isis.console.core.gui.
*
* Revision 1.1  2008/02/15 14:14:48  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.gui;

//
//Imports syst�me
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
* Cette classe est une sp�cialisation de la classe FileFilter afin de 
* permettre la gestion simple du filtrage des fichiers par extension.
* Elle red�finit les m�thodes accept() et getDescription(), auxquelles 
* sont ajout�es la m�thode getExtension().
* ----------------------------------------------------------*/
public class FileNameExtensionFilter 
	extends FileFilter {
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: FileNameExtensionFilter
	*
	* Description:
	* Ce constructeur est le seul � pouvoir �tre utilis�. Il permet de 
	* d�finir l'extension associ�e au filtre et sa description.
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
	* Cette m�thode red�finit celle de la super-classe FileFilter. Elle 
	* est appel�e afin de savoir si le fichier pass� en argument est 
	* accept� ou non par le filtre.
	* Le fichier est accept� par le filtre lorsque le fichier pass� en 
	* argument dispose d'une extension correspondant � celle qui est 
	* associ�e � ce filtre.
	* 
	* Arguments:
	*  - file: Le fichier � tester vis � vis du filtre.
	* 
	* Retourne: true si le fichier est accept� par le filtre, false sinon.
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
		// On v�rifie la validit� de l'argument
		if(file == null) {
			//trace_methods.endOfMethod();
			return false;
		}
		// S'il s'agit d'un r�pertoire on retourne vrai pour qu'il soit
		// affich�
		if(file.isDirectory() == true) {
			//trace_debug.writeTrace("Il s'agit d'un r�pertoire");
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
	* Cette m�thode red�finit celle de la super-classe FileFilter. Elle 
	* permet de r�cup�rer la description du filtre.
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
	* Cette m�thode permet de r�cup�rer l'extension de filtrage associ�e.
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
	* Cet attribut maintient l'extension associ�e au filtre, telle que 
	* pass�e au constructeur.
	* ----------------------------------------------------------*/
	private String _extension;

	/*----------------------------------------------------------
	* Nom: _description
	* 
	* Description:
	* Cet attribut maintient la description du filtre, telle que pass�e au 
	* constructeur.
	* ----------------------------------------------------------*/
	private String _description;

	/*----------------------------------------------------------
	* Nom: FileNameExtensionFilter
	* 
	* Description:
	* Ce constructeur ne doit pas �tre utilis�. Il n'est pr�sent� que pour 
	* des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	private FileNameExtensionFilter() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileNameExtensionFilter", "FileNameExtensionFilter");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}
}
