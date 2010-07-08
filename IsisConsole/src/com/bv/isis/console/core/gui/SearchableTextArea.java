/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/SearchableTextArea.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation de zone de texte "recherchable"
* DATE:        17/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SearchableTextArea.java,v $
* Revision 1.3  2009/01/14 14:19:13  tz
* Classe déplacée dans le package com.bv.isis.console.core.gui.
*
* Revision 1.2  2008/02/15 14:15:26  tz
* Implémentation de la méthode getFileFilters().
*
* Revision 1.1  2006/11/03 10:28:48  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.gui;

//
// Imports système
//
import javax.swing.JTextArea;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.io.File;
import javax.swing.filechooser.FileFilter;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.SearchableComponentInterface;

/*----------------------------------------------------------
* Nom: SearchableTextArea
* 
* Description:
* Cette classe est une spécialisation de la classe JTextArea afin de créer 
* une zone de texte dans laquelle l'utilisateur peut rechercher des chaînes 
* de caractères ou enregistrer les données dans des fichiers.
* Pour cela, elle implémente l'interface SearchableComponentInterface.
* ----------------------------------------------------------*/
public class SearchableTextArea 
	extends JTextArea
	implements SearchableComponentInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: SearchableTextArea
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présentée que
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public SearchableTextArea()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTextArea", "SearchableTextArea");

		trace_methods.beginningOfMethod();
		_lastSearchPosition = 0;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: searchData
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet de démarrer une nouvelle 
	* recherche sur une chaîne passée en argument.
	* Elle appelle la méthode de même nom de la classe DocumentSearcher.
	* 
	* Arguments:
	*  - stringToSearchFor: La chaîne à rechercher dans le texte contenu dans 
	*    la zone de texte.
	* 
	* Retourne: true si une occurence de la chaîne a été trouvée, false sinon.
	* ----------------------------------------------------------*/
	public boolean searchData(
		String stringToSearchFor
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTextArea", "searchData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean found = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("stringToSearchFor=" + stringToSearchFor);
		found = DocumentSearcher.searchData(stringToSearchFor, getDocument(), 
			this, this);
		trace_methods.endOfMethod();
		return found;
	}

	/*----------------------------------------------------------
	* Nom: searchAgain
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet de poursuivre une recherche 
	* sur une chaîne passée en argument. La recherche doit être poursuivie à 
	* partir de la position de la dernière occurence trouvée.
	* Elle appelle la méthode de même nom de la classe DocumentSearcher.
	* 
	* Arguments:
	*  - stringToSearchFor: La chaîne de caractères à rechercher dans le 
	*    document contenu dans la zone de texte.
	* 
	* Retourne: true si une occurence de la chaîne a été trouvée, false sinon.
	* ----------------------------------------------------------*/
	public boolean searchAgain(
		String stringToSearchFor
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTextArea", "searchAgain");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		boolean found = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("stringToSearchFor=" + stringToSearchFor);
		found = DocumentSearcher.searchAgain(stringToSearchFor, getDocument(), 
			this, this);
		trace_methods.endOfMethod();
		return found;
	}

	/*----------------------------------------------------------
	* Nom: setLastSearchPosition
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet de définir la dernière 
	* position de la recherche d'une chaîne de caractères dans la zone de 
	* texte.
	* 
	* Arguments:
	*  - lastSearchPosition: La dernière position de la recherche dans la zone 
	*    de texte.
	* ----------------------------------------------------------*/
	public void setLastSearchPosition(
		int lastSearchPosition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTextArea", "setLastSearchPosition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("lastSearchPosition=" + lastSearchPosition);
		_lastSearchPosition = lastSearchPosition;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getLastSearchPosition
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet de connaître la dernière 
	* position de la recherche d'une chaîne de caractères dans la zone de 
	* texte.
	* 
	* Retourne: La dernière position de la recherche dans la zone de texte.
	* ----------------------------------------------------------*/
	public int getLastSearchPosition()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTextArea", "getLastSearchPosition");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _lastSearchPosition;
	}

	/*----------------------------------------------------------
	* Nom: saveDataToFile
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle permet d'enregistrer le contenu de la 
	* zone de texte dans un fichier, passé en argument.
	* Elle appelle la méthode de même nom de la classe DocumentSearcher.
	* 
	* Arguments:
	*  - file: Une référence sur un objet File correspond au fichier de 
	*    destination,
	*  - mainWindowInterface: Une référence sur un objet MainWindowInterface.
 	* ----------------------------------------------------------*/
 	public void saveDataToFile(
 		File file,
 		MainWindowInterface mainWindowInterface
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTextArea", "saveToDataFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("file=" + file);
		trace_arguments.writeTrace("mainWindowInterface=" + 
			mainWindowInterface);
		DocumentSearcher.saveDataToFile(file, getDocument(), 
			mainWindowInterface);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: getFileFilters
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface 
	* SearchableComponentInterface. Elle est appelée afin de récupérer la 
	* liste des extensions de fichier préférées, autres que "toutes", sous 
	* la forme d'un tableau de FileFilter.
	* 
	* Dans le cas d'une zone de texte, il n'y a pas d'extension 
	* supplémentaire.
	* 
	* Retourne: Un tableau vide.
 	* ----------------------------------------------------------*/
 	public FileFilter[] getFileFilters() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SearchableTextArea", "getFileFilters");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new FileNameExtensionFilter[0];
 	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _lastSearchPosition
	* 
	* Description:
	* Cet attribut permet de stocker la position de la dernière recherche 
	* d'une chaîne dans la zone de texte (voir les méthodes 
	* setLastSearchPosition() et getLastSearchPosition()).
	* ----------------------------------------------------------*/
	private int _lastSearchPosition;
}