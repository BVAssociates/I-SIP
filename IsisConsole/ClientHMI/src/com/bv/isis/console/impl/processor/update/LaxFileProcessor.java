/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/LaxFileProcessor.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de traitement d'un patch de fichier LAX
* DATE:        22/12/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.update
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: LaxFileProcessor.java,v $
* Revision 1.2  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.1  2005/12/23 13:23:43  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.update;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.util.UtilStringTokenizer;
import com.bv.core.message.MessageManager;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Vector;

//
//Imports du projet
//
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: LaxFileProcessor
* 
* Description:
* Cette classe est une classe d'implémentation permettant le traitement d'une 
* entrée de type patch de fichier LAX du fichier de configuration des mises à 
* jour.
* Son objectif est de lire le contenu du fichier de patch, de lire le contenu 
* du fichier LAX, d'appliquer les modifications au contenu du fichier LAX, 
* puis de réécrire ce dernier en y incluant les modifications.
* Elle implémente l'interface EntryProcessorInterface.
* ----------------------------------------------------------*/
public class LaxFileProcessor 
	implements EntryProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: LaxFileProcessor
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
	* ----------------------------------------------------------*/
	public LaxFileProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LaxFileProcessor", "LaxFileProcessor");

		trace_methods.beginningOfMethod();
		_patchContent = new Vector();
		_fileContent = new Vector();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: processEntry
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface EntryProcessorInterface. 
	* Elle est appelée pour déclencher le traitement d'une entrée du fichier 
	* de configuration des mises à jour.
	* Dans le cas présent (patch de fichier LAX), il s'agit de lire le fichier 
	* de patch (via la méthode readPatchFile()), lire le fichier à patcher 
	* (via la méthode readFileToBePatched()), d'appliquer le patch (via la 
	* méthode patchFile()) et enfin d'écrire le contenu modifié dans le 
	* fichier à patcher (via la méthode writePatchedFile()).
	* 
	* Si un problème survient pendant le traitement de l'entrée, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - entryName: Le nom (et chemin relatif) de l'entrée,
	*  - entryFile: Une référence sur un objet File représentant le fichier 
	*    physique de l'entrée,
	*  - destinationDirectory: Le répertoire de base de destination,
	*  - progressInterface: Une référence sur un objet UpdateProgressInterface 
	*    permet d'indiquer la progression du traitement de l'entrée.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public void processEntry(
		String entryName,
		File entryFile,
		String destinationDirectory,
		UpdateProgressInterface progressInterface
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LaxFileProcessor", "processEntry");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		File file_to_patch = null;
		int cut_position = -1;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("entryName=" + entryName);
		trace_arguments.writeTrace("entryFile=" + entryFile);
		trace_arguments.writeTrace("destinationDirectory=" + 
			destinationDirectory);
		trace_arguments.writeTrace("progressInterface=" + progressInterface);
		// On vérifie la validité des arguments
		if(entryName == null || entryName.equals("") == true ||
			entryFile == null || destinationDirectory == null ||
			destinationDirectory.equals("") == true ||
			progressInterface == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On va construire le chemin du fichier à patcher
		cut_position = entryName.lastIndexOf("_patch");
		if(cut_position != -1)
		{
			file_to_patch = new File(destinationDirectory,
				entryName.substring(0, cut_position));
		}
		else
		{
			file_to_patch = new File(destinationDirectory,
				entryName);
		}
		trace_debug.writeTrace("file_to_be_patched=" +
			file_to_patch.getAbsolutePath());
		progressInterface.updateProgress(null, entryName, 0, -1);
		// Lecture du fichier de patch
		readPatchFile(entryFile);
		progressInterface.updateProgress(null, null, 25, -1);
		// Lecture du fichier à patcher
		readFileToPatch(file_to_patch);
		progressInterface.updateProgress(null, null, 50, -1);
		// Application du patch
		patchFile();
		progressInterface.updateProgress(null, null, 75, -1);
		// Enregistrement des modifications dans le fichier
		writePatchedFile(file_to_patch);
		progressInterface.updateProgress(null, null, 100, -1);
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: _patchContent
	* 
	* Description:
	* Cet attribut maintient une référence sur un vecteur chargé de contenir 
	* les éléments du fichier de patch.
	* ----------------------------------------------------------*/
	protected Vector _patchContent;

	/*----------------------------------------------------------
	* Nom: _fileContent
	* 
	* Description:
	* Cet attribut maintient une référence sur un object Vecteur destiné à 
	* contenir toutes les lignes du fichier à patcher.
	* ----------------------------------------------------------*/
	protected Vector _fileContent;

	/*----------------------------------------------------------
	* Nom: readPatchFile
	* 
	* Description:
	* Cette méthode est chargée de lire le fichier de patch (représenté par 
	* l'argument), et d'en stocker le contenu dans un vecteur, représenté par 
	* l'attribut _patchContent.
	* Dans le cas du fichier LAX, les informations lues depuis le fichier de 
	* patch sont stockées telles qu'elles dans le vecteur pour être 
	* directement exploitées.
	* Les lignes vides et les lignes de commentaire (commençant par '#') sont 
	* ignorées.
	* 
	* Si une erreur survient lors de la lecture du fichier de patch, 
	* l'exception InnerException est levée.
	* 
	* Arguments:
	*  - patchFile: Une référence sur un objet File correspondant au fichier 
	*    de patch.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	protected void readPatchFile(
		File patchFile
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LaxFileProcessor", "readPatchFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_io = TraceAPI.declareTraceIO("Console");
		BufferedReader reader = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("patchFile=" + patchFile);
		// On vérifie la validité de l'argument
		if(patchFile == null)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		try
		{
			String line = null;
			
			reader = new BufferedReader(new FileReader(patchFile));
			while(true)
			{
				line = reader.readLine();
				if(line == null)
				{
					break;
				}
				trace_io.writeTrace("Lu depuis le fichier: '" + line + "'");
				// Si la ligne est vide ou qu'il s'agit d'une ligne de 
				// commentaire, on l'ignore
				if(line.equals("") == true || line.startsWith("#") == true)
				{
					continue;
				}
				// On ajoute la ligne au vecteur
				_patchContent.add(line);
			}
			reader.close();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la lecture du fichier " +
				patchFile.getAbsolutePath() + ": " + exception.getMessage());
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch(Exception e)
				{
					// On ne fait rien
				}
			}
			// On va lever une nouvelle exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ErrorWhileReadingPatchFile", 
				exception.getMessage(), exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: patchFile
	* 
	* Description:
	* Cette méthode est chargée d'appliquer le patch, préalablement lu, au 
	* fichier à patcher, également préalablement chargé.
	* Chaque ligne du fichier patch est décomposé en 2 ou 3 champs, suivant la 
	* syntaxe:
	* <Mot-clé> <Paramètre> [<Valeur>]
	* 
	* Trois types d'opérations peuvent être effectuées sur un fichier LAX:
	*  - L'ajout de paramètre,
	*  - La modification de paramètre,
	*  - La suppression de paramètre.
	* 
	* L'ajout de paramètre est signifié par le mot-clé "ADD". Une valeur doit 
	* être fournie en troisième paramètre.
	* La modification de paramètre est signifiée par le mot-clé "UPDATE". Une 
	* valeur doit être fournie en troisième paramètre. La valeur est soit 
	* fixe, soit relative à l'ancienne valeur (en utilisant la chaîne 
	* "%[value]").
	* La suppression de paramètre est signifiée par le mot-clé "REMOVE". 
	* Aucune valeur n'est nécessaire en troisième paramètre.
	* 
	* Si un problème survient pendant l'application du patch, l'exception 
	* InnerException est levée.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	protected void patchFile()
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LaxFileProcessor", "patchFile");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		// On va appliquer l'ensemble des patchs
		while(_patchContent.size() > 0)
		{
			String instruction = null;
			String parameter_name = null;
			String value = null;
			String patch_instruction = (String)_patchContent.elementAt(0);
			_patchContent.remove(0);
			trace_debug.writeTrace("patch_instruction=" + patch_instruction);
			// Découpage de la ligne suivant le séparateur ' '
			UtilStringTokenizer tokenizer = 
				new UtilStringTokenizer(patch_instruction, " ");
			// Il doit y avoir au moins deux sous-chaînes
			if(tokenizer.getTokensCount() < 2)
			{
				trace_errors.writeTrace(
					"Le format de la ligne de patch n'est pas valide !");
				// On va lever une exception
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_IncorrectPatchInstruction", 
					patch_instruction, null);
			}
			// On récupère de toute façon l'instruction et le nom du paramètre
			instruction = tokenizer.nextToken();
			parameter_name = tokenizer.nextToken();
			trace_debug.writeTrace("instruction=" + instruction);
			trace_debug.writeTrace("parameter_name=" + parameter_name);
			// Suivant la valeur de l'instruction, on effectue le traitement
			// adéquat
			if(instruction.equalsIgnoreCase("ADD") == true)
			{
				trace_debug.writeTrace("Ajout d'un paramètre");
				// On récupère la valeur
				value = patch_instruction.substring(instruction.length() +
					parameter_name.length() + 2);
				trace_debug.writeTrace("value=" + value);
				// On vérifie si le paramètre n'existe pas déjà
				if(getParameterPosition(parameter_name, 0, -1) != -1)
				{
					// On passe au suivant
					continue;
				}
				// On ajoute une ligne à la fin du vecteur
				_fileContent.add(parameter_name + "=" + value);
				// On passe au suivant
				continue;
			}
			if(instruction.equalsIgnoreCase("UPDATE") == true)
			{
				trace_debug.writeTrace("Modification d'un paramètre");
				// On récupère la valeur
				value = patch_instruction.substring(instruction.length() +
					parameter_name.length() + 2);
				// On récupère la position du paramètre
				int position = getParameterPosition(parameter_name, 0, -1);
				// Si la position n'est pas valide, on passe au suivant
				if(position == -1)
				{
					continue;
				}
				String old_value = getParameterValue(position);
				String new_value = MessageManager.replaceString(value, 
					"%[value]", old_value);
				trace_debug.writeTrace("value=" + value);
				trace_debug.writeTrace("old_value=" + old_value);
				trace_debug.writeTrace("new_value=" + new_value);
				// On remplace la ligne dans le vecteur
				_fileContent.remove(position);
				_fileContent.insertElementAt(parameter_name + "=" +
					new_value, position);
				// On passe au suivant
				continue;
			}
			if(instruction.equalsIgnoreCase("REMOVE") == true)
			{
				trace_debug.writeTrace("Suppression d'un paramètre");
				// On récupère la position du paramètre
				int position = getParameterPosition(parameter_name, 0, -1);
				// Si la position est valide, on supprime l'élément à
				// cette position
				if(position != -1)
				{
					_fileContent.remove(position);
				}
				// On passe au suivant
				continue;
			}
			// Si on arrive ici, c'est que l'instruction n'est pas 
			// correcte
			trace_errors.writeTrace("Instruction incorrecte: " +
				instruction);
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_IncorrectPatchInstruction", 
				patch_instruction, null);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getParameterPosition
	* 
	* Description:
	* Cette méthode est chargée de scruter le contenu du fichier à patcher, 
	* contenu dans l'attribut _fileContent, à la recherche d'un paramètre dont 
	* le nom est passé en argument.
	* Chaque élément du vecteur _fileContent est une chaîne de caractère 
	* représentant une ligne du fichier LAX. Les paramètres sont identifiables 
	* grâce à la présence du caractère '=' dans la ligne, suivant la syntaxe 
	* <Parametre>=<Valeur>.
	* 
	* Arguments:
	*  - parameterName: Le nom du paramètre dont la position dans le vecteur 
	*    est requise,
	*  - startFrom: La position de départ pour la recherche,
	*  - endAt: L'éventuelle position de fin de recherche.
	* 
	* Retourne: La position du paramètre dans le vecteur, ou -1 si celui-ci ne 
	* peut pas être trouvé.
	* ----------------------------------------------------------*/
	protected int getParameterPosition(
		String parameterName,
		int startFrom,
		int endAt
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LaxFileProcessor", "getParameterPosition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parameterName=" + parameterName);
		trace_arguments.writeTrace("startFrom=" + startFrom);
		trace_arguments.writeTrace("endAt=" + endAt);
		// On vérifie que la position de départ est valide
		if(startFrom < 0 || startFrom >= _fileContent.size())
		{
			trace_debug.writeTrace("La position de départ est hors limites !");
			// On sort
			trace_methods.endOfMethod();
			return -1;
		}
		// On vérifie si la position de fin est positionnée et est bien
		// positionnée
		if(endAt == -1 || endAt >= _fileContent.size())
		{
			endAt = _fileContent.size() - 1;
		}
		// On va regarder dans chaque élément du vecteur _patchContent si une
		// des lignes commence par "<ParameterName>="
		for(int index = startFrom ; index <= endAt ; index ++)
		{
			String element = (String)_fileContent.elementAt(index);
			if(element.equals("") == true || element.startsWith("#") == true)
			{
				// On passe au suivant
				continue;
			}
			trace_debug.writeTrace("Vérification de l'élément: " +
				element);
			if(element.startsWith(parameterName + "=") == true)
			{
				trace_debug.writeTrace("Paramètre trouvé à la position: " +
					index);
				// On peut sortir
				trace_methods.endOfMethod();
				return index;
			}
		}
		// Si on arrive ici, c'est que l'on n'a pas trouvé le paramètre
		trace_debug.writeTrace("Paramètre non trouvé");
		trace_methods.endOfMethod();
		return -1;
	}

	/*----------------------------------------------------------
	* Nom: getParameterValue
	* 
	* Description:
	* Cette méthode est chargée d'extraire et de retourner la valeur associée 
	* à un paramètre. Suivant la syntaxe d'une ligne de paramètre 
	* (<Parametre>=<Valeur>), la valeur est la portion de chaîne se trouvant 
	* après le caractère '='.
	* La ligne de paramètre est identifiée par sa position dans le vecteur.
	* 
	* Arguments:
	*  - parameterPosition: La position de la ligne de paramètre dans le 
	*    vecteur.
	* 
	* Retourne: Une chaîne de caractères contenant la valeur du paramètre.
	* ----------------------------------------------------------*/
	protected String getParameterValue(
		int parameterPosition
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LaxFileProcessor", "getParameterValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String value = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parameterPosition=" + parameterPosition);
		// On vérifie que la position est valide
		if(parameterPosition < 0 || parameterPosition >= _fileContent.size())
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("argument hors limites !");
			// On sort
			trace_methods.endOfMethod();
			return value;
		}
		// On va récupérer l'élément à la position indiquée
		String element = (String)_fileContent.elementAt(parameterPosition);
		trace_debug.writeTrace("element=" + element);
		int equal_position = element.indexOf('=');
		trace_debug.writeTrace("equal_position=" + equal_position);
		if(equal_position != -1)
		{
			value = element.substring(equal_position + 1);
		}
		trace_debug.writeTrace("value=" + value);
		trace_methods.endOfMethod();
		return value;
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: readFileToPatch
	* 
	* Description:
	* Cette méthode est chargée de la lecture du contenu du fichier à patcher. 
	* Celle-ci consiste à lire le fichier ligne par ligne, et à stocker 
	* celles-ci dans un vecteur, sous forme de chaînes de caractères, 
	* représenté par l'attribut _fileContent.
	* Au préalable, la méthode vérifie que le fichier à patcher peut être lu 
	* et écrit.
	* 
	* Si une erreur survient pendant la lecture du fichier à patcher, 
	* l'exception InnerException est levée.
	* 
	* Arguments:
	*  - fileToPatch: Une référence sur un objet File correspondant au 
	*    fichier à patcher.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	private void readFileToPatch(
		File fileToPatch
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LaxFileProcessor", "readFileToPatch");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_io = TraceAPI.declareTraceIO("Console");
		BufferedReader reader = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("fileToPatch=" + fileToPatch);
		// On vérifie la validité de l'argument
		if(fileToPatch == null)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On vérifie que le fichier est accessible en lecture et en écriture
		if(fileToPatch.exists() == false || fileToPatch.isFile() == false ||
			fileToPatch.canRead() == false || fileToPatch.canWrite() == false)
		{
			trace_errors.writeTrace(
				"Le fichier " + fileToPatch.getAbsolutePath() +
				"est soit absent, soit un répertoire, n'est pas lisible, " +
				"ou encore n'est pas inscriptible !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_FileToPatchIncorrect", 
				fileToPatch.getAbsolutePath(), null);
		}
		try
		{
			String line = null;
			
			reader = new BufferedReader(new FileReader(fileToPatch));
			while(true)
			{
				line = reader.readLine();
				if(line == null)
				{
					break;
				}
				trace_io.writeTrace("Lu depuis le fichier: '" + line + "'");
				// On ajoute la ligne au vecteur
				_fileContent.add(line);
			}
			reader.close();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de la lecture du fichier " +
				fileToPatch.getAbsolutePath() + ": " + exception.getMessage());
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch(Exception e)
				{
					// On ne fait rien
				}
			}
			// On va lever une nouvelle exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ErrorWhileReadingFileToPatch", 
				exception.getMessage(), exception);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: writePatchedFile
	* 
	* Description:
	* Cette méthode est chargée d'écrire le contenu modifié dans le fichier à 
	* patcher. Pour cela, elle va enregistrer chacune des chaînes de 
	* caractères contenues dans l'attribut _fileContent dans le fichier sur 
	* une nouvelle ligne.
	* 
	* Si un problème survient lors de l'écriture du fichier, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - patchedFile: Une référence sur un objet File correspondant au fichier 
	*    à écrire.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	private void writePatchedFile(
		File patchedFile
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"LaxFileProcessor", "writePatchedFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_io = TraceAPI.declareTraceIO("Console");
		BufferedWriter writer = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("patchedFile=" + patchedFile);
		// On vérifie la validité de l'argument
		if(patchedFile == null)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		try
		{
			writer = new BufferedWriter(new FileWriter(patchedFile));
			while(_fileContent.size() > 0)
			{
				String line = (String)_fileContent.elementAt(0);
				writer.write(line);
				writer.newLine();
				trace_io.writeTrace("Ecrit dans le fichier: '" +
					line + "'");
				_fileContent.remove(0);
			}
			writer.close();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de l'écriture du fichier " +
				patchedFile.getAbsolutePath() + ": " + exception.getMessage());
			if(writer != null)
			{
				try
				{
					writer.close();
				}
				catch(Exception e)
				{
					// On ne fait rien
				}
			}
			// On va lever une nouvelle exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ErrorWhileWritingPatchedFile", 
				exception.getMessage(), exception);
		}
		trace_methods.endOfMethod();
	}
}
