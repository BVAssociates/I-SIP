/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/gui/DocumentSearcher.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de recherche dans un document
* DATE:        27/09/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DocumentSearcher.java,v $
* Revision 1.2  2009/01/14 14:18:05  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.gui.
*
* Revision 1.1  2005/10/07 08:17:52  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.gui;

//
//Imports syst�me
//
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import com.bv.core.message.MessageManager;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.SearchableComponentInterface;
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: DocumentSearcher
* 
* Description:
* Cette classe abstraite est une classe technique charg�e de la recherche de 
* cha�nes de caract�res dans un document et d'en enregistrer le contenu dans 
* un fichier.
* Pour ce faire, elle propose trois m�thode statiques.
* ----------------------------------------------------------*/
abstract class DocumentSearcher
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: searchData
	* 
	* Description:
	* Cette m�thode statique est charg�e d'effectuer une recherche d'une 
	* cha�ne de caract�res pass�e en argument dans un document �galement pass� 
	* en argument.
	* La recherche dans le document est effectu�e par blocs de 1 Ko, pour 
	* �viter les probl�mes de m�moire dans le cas de gros fichiers.
	* Si une occurence de la cha�ne est trouv�e dans le document, celle-ci est 
	* indiqu�e par une s�lection dans le document, ou plut�t dans le composant 
	* de texte, pass� en argument. La position de cette occurence est �galement 
	* enregistr�e.
	* 
	* Arguments:
	*  - stringToSearchFor: La cha�ne � rechercher dans le document,
	*  - document: Une r�f�rence sur un objet Document dans lequel rechercher 
	*    la chaine,
	*  - searchableComponent: Une r�f�rence sur un objet SearchableComponent 
	*    permettant l'enregistrement �ventuel de la position de la premi�re 
	*    occurence,
	*  - textComponent: Une r�f�rence sur un objet JTextComponent permettant 
	*    la s�lection de l'occurence de la cha�ne.
	* 
	* Retourne: true si la cha�ne a �t� trouv�e dans le document, false sinon.
	* ----------------------------------------------------------*/
	public static boolean searchData(
		String stringToSearchFor,
		Document document,
		SearchableComponentInterface searchableComponent,
		JTextComponent textComponent
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DocumentSearcher", "searchData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String text = null;
		int block_size = 1024;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("stringToSearchFor=" + stringToSearchFor);
		trace_arguments.writeTrace("document=" + document);
		trace_arguments.writeTrace("searchableComponent=" + 
			searchableComponent);
		trace_arguments.writeTrace("textComponent=" + textComponent);
		// On v�rifie la validit� des arguments
		if(stringToSearchFor == null || stringToSearchFor.equals("") == true ||
			document == null || searchableComponent == null ||
			textComponent == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			return false;
		}
		// Pour les gros fichiers, on ne peut pas r�cup�rer tout le texte �
		// la fois. Il vaut mieux proc�der par blocs.
		int offset = 0;
		int document_length = document.getLength();
		trace_debug.writeTrace("document_length=" + document_length);
		trace_debug.writeTrace("offset=" + offset);
		while(offset < document_length)
		{
			int size = Math.min(block_size, document_length - offset);
			trace_debug.writeTrace("size=" + size);
			try
			{
				// On r�cup�re le texte
				text = document.getText(offset, size);
			}
			catch(Exception e)
			{
				trace_errors.writeTrace("Erreur lors de la r�cup�ration " +
					"du texte de la zone: " + e);
				// On sort
				trace_methods.endOfMethod();
				return false;
			}
			trace_debug.writeTrace("text=" + text);
			// Si le texte de la zone est vide, il n'y a pas de recherche
			if(text == null || text.length() == 0)
			{
				trace_methods.endOfMethod();
				return false;
			}
			// On cherche la position de la cha�ne dans le texte
			int start = text.indexOf(stringToSearchFor);
			trace_debug.writeTrace("start=" + start);
			// Si la valeur est -1, on va passer au bloc suivant
			if(start == -1)
			{
				// On d�cale de 1 bloc moins la taille de la cha�ne
				// � rechercher, au cas o� elle serait dans la coupure
				offset += block_size - stringToSearchFor.length();
				trace_debug.writeTrace("offset=" + offset);
				continue;
			}
			start += offset;
			trace_debug.writeTrace("Occurence trouv�e � " + start);
			// On calcule la position de fin de la cha�ne pour la s�lection
			int end = start + stringToSearchFor.length();
			// On positionne le caret, et on s�lectionne la cha�ne
			textComponent.setCaretPosition(end);
			textComponent.setSelectionStart(start);
			textComponent.setSelectionEnd(end);
			// On enregistre la position de la derni�re recherche dans le
			// composant
			searchableComponent.setLastSearchPosition(end);
			// On peut sortir
			trace_methods.endOfMethod();
			return true;
		}
		// Si on arrive ici, c'est que la cha�ne n'est pas dans
		// le texte.
		trace_debug.writeTrace("Occurence non trouv�e");
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: searchAgain
	* 
	* Description:
	* Cette m�thode statique est charg�e de poursuivre une recherche d'une 
	* cha�ne de caract�res pass�e en argument dans un document �galement pass� 
	* en argument.
	* La recherche dans le document est effectu�e par blocs de 1 Ko, pour 
	* �viter les probl�mes de m�moire dans le cas de gros fichiers. Cette 
	* recherche est d�marr� � partir de la derni�re position enregistr�e, et 
	* non pas � partir du d�but du document.
	* Si une occurence de la cha�ne est trouv�e dans le document, celle-ci est 
	* indiqu�e par une s�lection dans le document, ou plut�t dans le composant 
	* de texte, pass� en argument. La position de cette occurence est �galement 
	* enregistr�e.
	* 
	* Arguments:
	*  - stringToSearchFor: La cha�ne � rechercher dans le document,
	*  - document: Une r�f�rence sur un objet Document dans lequel rechercher 
	*    la chaine,
	*  - searchableComponent: Une r�f�rence sur un objet SearchableComponent 
	*    permettant l'enregistrement �ventuel de la position de la premi�re occurence,
	*  - textComponent: Une r�f�rence sur un objet JTextComponent permettant 
	*    la s�lection de l'occurence de la cha�ne.
	* 
	* Retourne: true si une occurence de la cha�ne a �t� trouv�e dans 
	* le document, false sinon.
	* ----------------------------------------------------------*/
	public static boolean searchAgain(
		String stringToSearchFor,
		Document document,
		SearchableComponentInterface searchableComponent,
		JTextComponent textComponent
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DocumentSearcher", "searchData");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String text = null;
		int last_position;
		boolean rewinded = false;
		int block_size = 1024;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("stringToSearchFor=" + stringToSearchFor);
		trace_arguments.writeTrace("document=" + document);
		trace_arguments.writeTrace("searchableComponent=" + 
			searchableComponent);
		trace_arguments.writeTrace("textComponent=" + textComponent);
		// On v�rifie la validit� des arguments
		if(stringToSearchFor == null || stringToSearchFor.equals("") == true ||
			document == null || searchableComponent == null ||
			textComponent == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			return false;
		}
		last_position = searchableComponent.getLastSearchPosition();
		// Pour les gros fichiers, on ne peut pas r�cup�rer tout le texte �
		// la fois. Il vaut mieux proc�der par blocs.
		int offset = last_position;
		int document_length = document.getLength();
		trace_debug.writeTrace("document_length=" + document_length);
		trace_debug.writeTrace("offset=" + offset);
		while(true)
		{
			int size = Math.min(block_size, document_length - offset);
			trace_debug.writeTrace("size=" + size);
			try
			{
				// On r�cup�re le texte
				text = document.getText(offset, size);
			}
			catch(Exception e)
			{
				trace_errors.writeTrace("Erreur lors de la r�cup�ration " +
					"du texte de la zone: " + e);
				// On sort
				trace_methods.endOfMethod();
				return false;
			}
			trace_debug.writeTrace("text=" + text);
			// Si le texte de la zone est vide, il n'y a pas de recherche
			if(text == null || text.length() == 0)
			{
				trace_methods.endOfMethod();
				return false;
			}
			// On cherche la position de la cha�ne dans le texte
			int start = text.indexOf(stringToSearchFor);
			trace_debug.writeTrace("start=" + start);
			// Si la valeur est -1, on va passer au bloc suivant
			if(start == -1)
			{
				// On d�cale de 1 bloc moins la taille de la cha�ne
				// � rechercher, au cas o� elle serait dans la coupure
				offset += block_size - stringToSearchFor.length();
				trace_debug.writeTrace("offset=" + offset);
				// Si on est arriv� � la fin du document et qu l'on n'a 
				// jamais rembobin�, il faut repartir de z�ro
				if(offset >= document_length && rewinded == false)
				{
					offset = 0;
					rewinded = true;
					trace_debug.writeTrace("Rembobinage");
				}
				// Si on a pass� la pr�c�dente position et que l'on
				// a rembobin�, on arr�te la recherche
				else if(offset >= last_position && rewinded == true)
				{
					// On sort de la boucle
					break;
				}
				continue;
			}
			start += offset;
			trace_debug.writeTrace("Occurence trouv�e � " + start);
			// On calcule la position de fin de la cha�ne pour la s�lection
			int end = start + stringToSearchFor.length();
			// On positionne le caret, et on s�lectionne la cha�ne
			textComponent.setCaretPosition(end);
			textComponent.setSelectionStart(start);
			textComponent.setSelectionEnd(end);
			// On enregistre la position de la derni�re recherche dans le
			// composant
			searchableComponent.setLastSearchPosition(end);
			// On peut sortir
			trace_methods.endOfMethod();
			return true;
		}
		// Si on arrive ici, c'est que la cha�ne n'est pas dans
		// le texte.
		trace_debug.writeTrace("Occurence non trouv�e");
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: saveDataToFile
	* 
	* Description:
	* Cette m�thode statique est charg�e d'effectuer l'enregistrement du 
	* contenu d'un document pass� en argument dans un fichier, �galement pass� 
	* en argument.
	* L'enregistrement du contenu du document est effectu� par blocs de 5 Ko, 
	* pour �viter les probl�mes de m�moire dans le cas de gros fichiers.
	* 
	* Arguments:
	*  - file: Une r�f�rence sur un objet File correspondant au fichier dans 
	*    lequel enregistrer le contenu du document,
	*  - document: Une r�f�rence sur un objet Document dont le contenu doit 
	*    �tre enregistr�,
	*  - mainWindowInterface: Une r�f�rence sur un objet MainWindowInterface.
 	* ----------------------------------------------------------*/
 	public static void saveDataToFile(
 		File file,
 		Document document,
 		MainWindowInterface mainWindowInterface
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DocumentSearcher", "saveDataToFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String text = null;
		int block_size = 10 * 1024;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("file=" + file);
		trace_arguments.writeTrace("document=" + document);
		trace_arguments.writeTrace("mainWindowInterface=" + 
			mainWindowInterface);
		// On v�rifie la validit� des arguments
		if(file == null || document == null || mainWindowInterface == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		try
		{
			int offset = 0;
			int document_length = document.getLength();
			trace_debug.writeTrace("document_length=" + document_length);
			trace_debug.writeTrace("offset=" + offset);
			// On ouvre le fichier en �criture
			BufferedWriter writer = new BufferedWriter(
				new FileWriter(file, false));
			// Pour les gros fichiers, on ne peut pas r�cup�rer tout le texte �
			// la fois. Il vaut mieux proc�der par blocs.
			while(offset < document_length)
			{
				int last_pos = 0;
				String line;

				int size = Math.min(block_size, document_length - offset);
				trace_debug.writeTrace("size=" + size);
				// On r�cup�re le texte
				text = document.getText(offset, size);
				trace_debug.writeTrace("text=" + text);
				// Si le texte de la zone est vide, il n'y a pas 
				// d'enregistrement
				if(text == null || text.length() == 0)
				{
					trace_methods.endOfMethod();
					return;
				}
				while(true)
				{
					int pos = text.indexOf("\n", last_pos);
					if(pos == -1)
					{
						line = text.substring(last_pos);
					}
					else
					{
						line = text.substring(last_pos, pos);
					}
					if(line.length() > 0 && 
						line.charAt(line.length() - 1) == '\r')
					{
						line = line.substring(0, line.length() - 1);
					}
					writer.write(line);
					if(pos == -1)
					{
						break;
					}
					writer.newLine();
					last_pos = pos + 1;
				}
				// On d�cale par blocks
				offset += block_size;
				trace_debug.writeTrace("offset=" + offset);
			}
			writer.close();
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de l'enregistrement dans " +
				"le fichier: " + e);
			// On va afficher un message � l'utilisateur
			InnerException exception = new InnerException(
				e.getMessage(), null, null);
			mainWindowInterface.showPopupForException(
				MessageManager.getMessage("&ERR_CannotSaveToFile"),
				exception);
		}
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
