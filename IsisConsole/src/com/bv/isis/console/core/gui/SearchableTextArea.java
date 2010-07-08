/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/SearchableTextArea.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation de zone de texte "recherchable"
* DATE:        17/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SearchableTextArea.java,v $
* Revision 1.3  2009/01/14 14:19:13  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.gui.
*
* Revision 1.2  2008/02/15 14:15:26  tz
* Impl�mentation de la m�thode getFileFilters().
*
* Revision 1.1  2006/11/03 10:28:48  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.gui;

//
// Imports syst�me
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
* Cette classe est une sp�cialisation de la classe JTextArea afin de cr�er 
* une zone de texte dans laquelle l'utilisateur peut rechercher des cha�nes 
* de caract�res ou enregistrer les donn�es dans des fichiers.
* Pour cela, elle impl�mente l'interface SearchableComponentInterface.
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
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet de d�marrer une nouvelle 
	* recherche sur une cha�ne pass�e en argument.
	* Elle appelle la m�thode de m�me nom de la classe DocumentSearcher.
	* 
	* Arguments:
	*  - stringToSearchFor: La cha�ne � rechercher dans le texte contenu dans 
	*    la zone de texte.
	* 
	* Retourne: true si une occurence de la cha�ne a �t� trouv�e, false sinon.
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet de poursuivre une recherche 
	* sur une cha�ne pass�e en argument. La recherche doit �tre poursuivie � 
	* partir de la position de la derni�re occurence trouv�e.
	* Elle appelle la m�thode de m�me nom de la classe DocumentSearcher.
	* 
	* Arguments:
	*  - stringToSearchFor: La cha�ne de caract�res � rechercher dans le 
	*    document contenu dans la zone de texte.
	* 
	* Retourne: true si une occurence de la cha�ne a �t� trouv�e, false sinon.
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet de d�finir la derni�re 
	* position de la recherche d'une cha�ne de caract�res dans la zone de 
	* texte.
	* 
	* Arguments:
	*  - lastSearchPosition: La derni�re position de la recherche dans la zone 
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet de conna�tre la derni�re 
	* position de la recherche d'une cha�ne de caract�res dans la zone de 
	* texte.
	* 
	* Retourne: La derni�re position de la recherche dans la zone de texte.
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle permet d'enregistrer le contenu de la 
	* zone de texte dans un fichier, pass� en argument.
	* Elle appelle la m�thode de m�me nom de la classe DocumentSearcher.
	* 
	* Arguments:
	*  - file: Une r�f�rence sur un objet File correspond au fichier de 
	*    destination,
	*  - mainWindowInterface: Une r�f�rence sur un objet MainWindowInterface.
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
	* Cette m�thode red�finit celle de l'interface 
	* SearchableComponentInterface. Elle est appel�e afin de r�cup�rer la 
	* liste des extensions de fichier pr�f�r�es, autres que "toutes", sous 
	* la forme d'un tableau de FileFilter.
	* 
	* Dans le cas d'une zone de texte, il n'y a pas d'extension 
	* suppl�mentaire.
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
	* Cet attribut permet de stocker la position de la derni�re recherche 
	* d'une cha�ne dans la zone de texte (voir les m�thodes 
	* setLastSearchPosition() et getLastSearchPosition()).
	* ----------------------------------------------------------*/
	private int _lastSearchPosition;
}