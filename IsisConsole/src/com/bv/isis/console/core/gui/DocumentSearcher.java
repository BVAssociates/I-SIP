/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
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
* Classe déplacée dans le package com.bv.isis.console.core.gui.
*
* Revision 1.1  2005/10/07 08:17:52  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.gui;

//
//Imports système
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
* Cette classe abstraite est une classe technique chargée de la recherche de 
* chaînes de caractères dans un document et d'en enregistrer le contenu dans 
* un fichier.
* Pour ce faire, elle propose trois méthode statiques.
* ----------------------------------------------------------*/
abstract class DocumentSearcher
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: searchData
	* 
	* Description:
	* Cette méthode statique est chargée d'effectuer une recherche d'une 
	* chaîne de caractères passée en argument dans un document également passé 
	* en argument.
	* La recherche dans le document est effectuée par blocs de 1 Ko, pour 
	* éviter les problèmes de mémoire dans le cas de gros fichiers.
	* Si une occurence de la chaîne est trouvée dans le document, celle-ci est 
	* indiquée par une sélection dans le document, ou plutôt dans le composant 
	* de texte, passé en argument. La position de cette occurence est également 
	* enregistrée.
	* 
	* Arguments:
	*  - stringToSearchFor: La chaîne à rechercher dans le document,
	*  - document: Une référence sur un objet Document dans lequel rechercher 
	*    la chaine,
	*  - searchableComponent: Une référence sur un objet SearchableComponent 
	*    permettant l'enregistrement éventuel de la position de la première 
	*    occurence,
	*  - textComponent: Une référence sur un objet JTextComponent permettant 
	*    la sélection de l'occurence de la chaîne.
	* 
	* Retourne: true si la chaîne a été trouvée dans le document, false sinon.
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
		// On vérifie la validité des arguments
		if(stringToSearchFor == null || stringToSearchFor.equals("") == true ||
			document == null || searchableComponent == null ||
			textComponent == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			return false;
		}
		// Pour les gros fichiers, on ne peut pas récupérer tout le texte à
		// la fois. Il vaut mieux procéder par blocs.
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
				// On récupère le texte
				text = document.getText(offset, size);
			}
			catch(Exception e)
			{
				trace_errors.writeTrace("Erreur lors de la récupération " +
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
			// On cherche la position de la chaîne dans le texte
			int start = text.indexOf(stringToSearchFor);
			trace_debug.writeTrace("start=" + start);
			// Si la valeur est -1, on va passer au bloc suivant
			if(start == -1)
			{
				// On décale de 1 bloc moins la taille de la chaîne
				// à rechercher, au cas où elle serait dans la coupure
				offset += block_size - stringToSearchFor.length();
				trace_debug.writeTrace("offset=" + offset);
				continue;
			}
			start += offset;
			trace_debug.writeTrace("Occurence trouvée à " + start);
			// On calcule la position de fin de la chaîne pour la sélection
			int end = start + stringToSearchFor.length();
			// On positionne le caret, et on sélectionne la chaîne
			textComponent.setCaretPosition(end);
			textComponent.setSelectionStart(start);
			textComponent.setSelectionEnd(end);
			// On enregistre la position de la dernière recherche dans le
			// composant
			searchableComponent.setLastSearchPosition(end);
			// On peut sortir
			trace_methods.endOfMethod();
			return true;
		}
		// Si on arrive ici, c'est que la chaîne n'est pas dans
		// le texte.
		trace_debug.writeTrace("Occurence non trouvée");
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: searchAgain
	* 
	* Description:
	* Cette méthode statique est chargée de poursuivre une recherche d'une 
	* chaîne de caractères passée en argument dans un document également passé 
	* en argument.
	* La recherche dans le document est effectuée par blocs de 1 Ko, pour 
	* éviter les problèmes de mémoire dans le cas de gros fichiers. Cette 
	* recherche est démarré à partir de la dernière position enregistrée, et 
	* non pas à partir du début du document.
	* Si une occurence de la chaîne est trouvée dans le document, celle-ci est 
	* indiquée par une sélection dans le document, ou plutôt dans le composant 
	* de texte, passé en argument. La position de cette occurence est également 
	* enregistrée.
	* 
	* Arguments:
	*  - stringToSearchFor: La chaîne à rechercher dans le document,
	*  - document: Une référence sur un objet Document dans lequel rechercher 
	*    la chaine,
	*  - searchableComponent: Une référence sur un objet SearchableComponent 
	*    permettant l'enregistrement éventuel de la position de la première occurence,
	*  - textComponent: Une référence sur un objet JTextComponent permettant 
	*    la sélection de l'occurence de la chaîne.
	* 
	* Retourne: true si une occurence de la chaîne a été trouvée dans 
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
		// On vérifie la validité des arguments
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
		// Pour les gros fichiers, on ne peut pas récupérer tout le texte à
		// la fois. Il vaut mieux procéder par blocs.
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
				// On récupère le texte
				text = document.getText(offset, size);
			}
			catch(Exception e)
			{
				trace_errors.writeTrace("Erreur lors de la récupération " +
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
			// On cherche la position de la chaîne dans le texte
			int start = text.indexOf(stringToSearchFor);
			trace_debug.writeTrace("start=" + start);
			// Si la valeur est -1, on va passer au bloc suivant
			if(start == -1)
			{
				// On décale de 1 bloc moins la taille de la chaîne
				// à rechercher, au cas où elle serait dans la coupure
				offset += block_size - stringToSearchFor.length();
				trace_debug.writeTrace("offset=" + offset);
				// Si on est arrivé à la fin du document et qu l'on n'a 
				// jamais rembobiné, il faut repartir de zéro
				if(offset >= document_length && rewinded == false)
				{
					offset = 0;
					rewinded = true;
					trace_debug.writeTrace("Rembobinage");
				}
				// Si on a passé la précédente position et que l'on
				// a rembobiné, on arrête la recherche
				else if(offset >= last_position && rewinded == true)
				{
					// On sort de la boucle
					break;
				}
				continue;
			}
			start += offset;
			trace_debug.writeTrace("Occurence trouvée à " + start);
			// On calcule la position de fin de la chaîne pour la sélection
			int end = start + stringToSearchFor.length();
			// On positionne le caret, et on sélectionne la chaîne
			textComponent.setCaretPosition(end);
			textComponent.setSelectionStart(start);
			textComponent.setSelectionEnd(end);
			// On enregistre la position de la dernière recherche dans le
			// composant
			searchableComponent.setLastSearchPosition(end);
			// On peut sortir
			trace_methods.endOfMethod();
			return true;
		}
		// Si on arrive ici, c'est que la chaîne n'est pas dans
		// le texte.
		trace_debug.writeTrace("Occurence non trouvée");
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: saveDataToFile
	* 
	* Description:
	* Cette méthode statique est chargée d'effectuer l'enregistrement du 
	* contenu d'un document passé en argument dans un fichier, également passé 
	* en argument.
	* L'enregistrement du contenu du document est effectué par blocs de 5 Ko, 
	* pour éviter les problèmes de mémoire dans le cas de gros fichiers.
	* 
	* Arguments:
	*  - file: Une référence sur un objet File correspondant au fichier dans 
	*    lequel enregistrer le contenu du document,
	*  - document: Une référence sur un objet Document dont le contenu doit 
	*    être enregistré,
	*  - mainWindowInterface: Une référence sur un objet MainWindowInterface.
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
		// On vérifie la validité des arguments
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
			// On ouvre le fichier en écriture
			BufferedWriter writer = new BufferedWriter(
				new FileWriter(file, false));
			// Pour les gros fichiers, on ne peut pas récupérer tout le texte à
			// la fois. Il vaut mieux procéder par blocs.
			while(offset < document_length)
			{
				int last_pos = 0;
				String line;

				int size = Math.min(block_size, document_length - offset);
				trace_debug.writeTrace("size=" + size);
				// On récupère le texte
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
				// On décale par blocks
				offset += block_size;
				trace_debug.writeTrace("offset=" + offset);
			}
			writer.close();
		}
		catch(Exception e)
		{
			trace_errors.writeTrace("Erreur lors de l'enregistrement dans " +
				"le fichier: " + e);
			// On va afficher un message à l'utilisateur
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
