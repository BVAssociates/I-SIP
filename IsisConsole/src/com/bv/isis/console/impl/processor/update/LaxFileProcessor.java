/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
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
// D�claration du package
package com.bv.isis.console.impl.processor.update;

//
// Imports syst�me
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
* Cette classe est une classe d'impl�mentation permettant le traitement d'une 
* entr�e de type patch de fichier LAX du fichier de configuration des mises � 
* jour.
* Son objectif est de lire le contenu du fichier de patch, de lire le contenu 
* du fichier LAX, d'appliquer les modifications au contenu du fichier LAX, 
* puis de r��crire ce dernier en y incluant les modifications.
* Elle impl�mente l'interface EntryProcessorInterface.
* ----------------------------------------------------------*/
public class LaxFileProcessor 
	implements EntryProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: LaxFileProcessor
	* 
	* Description:
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface EntryProcessorInterface. 
	* Elle est appel�e pour d�clencher le traitement d'une entr�e du fichier 
	* de configuration des mises � jour.
	* Dans le cas pr�sent (patch de fichier LAX), il s'agit de lire le fichier 
	* de patch (via la m�thode readPatchFile()), lire le fichier � patcher 
	* (via la m�thode readFileToBePatched()), d'appliquer le patch (via la 
	* m�thode patchFile()) et enfin d'�crire le contenu modifi� dans le 
	* fichier � patcher (via la m�thode writePatchedFile()).
	* 
	* Si un probl�me survient pendant le traitement de l'entr�e, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - entryName: Le nom (et chemin relatif) de l'entr�e,
	*  - entryFile: Une r�f�rence sur un objet File repr�sentant le fichier 
	*    physique de l'entr�e,
	*  - destinationDirectory: Le r�pertoire de base de destination,
	*  - progressInterface: Une r�f�rence sur un objet UpdateProgressInterface 
	*    permet d'indiquer la progression du traitement de l'entr�e.
	* 
	* L�ve: InnerException.
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
		// On v�rifie la validit� des arguments
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
		// On va construire le chemin du fichier � patcher
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
		// Lecture du fichier � patcher
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
	* Cet attribut maintient une r�f�rence sur un vecteur charg� de contenir 
	* les �l�ments du fichier de patch.
	* ----------------------------------------------------------*/
	protected Vector _patchContent;

	/*----------------------------------------------------------
	* Nom: _fileContent
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un object Vecteur destin� � 
	* contenir toutes les lignes du fichier � patcher.
	* ----------------------------------------------------------*/
	protected Vector _fileContent;

	/*----------------------------------------------------------
	* Nom: readPatchFile
	* 
	* Description:
	* Cette m�thode est charg�e de lire le fichier de patch (repr�sent� par 
	* l'argument), et d'en stocker le contenu dans un vecteur, repr�sent� par 
	* l'attribut _patchContent.
	* Dans le cas du fichier LAX, les informations lues depuis le fichier de 
	* patch sont stock�es telles qu'elles dans le vecteur pour �tre 
	* directement exploit�es.
	* Les lignes vides et les lignes de commentaire (commen�ant par '#') sont 
	* ignor�es.
	* 
	* Si une erreur survient lors de la lecture du fichier de patch, 
	* l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - patchFile: Une r�f�rence sur un objet File correspondant au fichier 
	*    de patch.
	* 
	* L�ve: InnerException.
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
		// On v�rifie la validit� de l'argument
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
	* Cette m�thode est charg�e d'appliquer le patch, pr�alablement lu, au 
	* fichier � patcher, �galement pr�alablement charg�.
	* Chaque ligne du fichier patch est d�compos� en 2 ou 3 champs, suivant la 
	* syntaxe:
	* <Mot-cl�> <Param�tre> [<Valeur>]
	* 
	* Trois types d'op�rations peuvent �tre effectu�es sur un fichier LAX:
	*  - L'ajout de param�tre,
	*  - La modification de param�tre,
	*  - La suppression de param�tre.
	* 
	* L'ajout de param�tre est signifi� par le mot-cl� "ADD". Une valeur doit 
	* �tre fournie en troisi�me param�tre.
	* La modification de param�tre est signifi�e par le mot-cl� "UPDATE". Une 
	* valeur doit �tre fournie en troisi�me param�tre. La valeur est soit 
	* fixe, soit relative � l'ancienne valeur (en utilisant la cha�ne 
	* "%[value]").
	* La suppression de param�tre est signifi�e par le mot-cl� "REMOVE". 
	* Aucune valeur n'est n�cessaire en troisi�me param�tre.
	* 
	* Si un probl�me survient pendant l'application du patch, l'exception 
	* InnerException est lev�e.
	* 
	* L�ve: InnerException.
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
			// D�coupage de la ligne suivant le s�parateur ' '
			UtilStringTokenizer tokenizer = 
				new UtilStringTokenizer(patch_instruction, " ");
			// Il doit y avoir au moins deux sous-cha�nes
			if(tokenizer.getTokensCount() < 2)
			{
				trace_errors.writeTrace(
					"Le format de la ligne de patch n'est pas valide !");
				// On va lever une exception
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_IncorrectPatchInstruction", 
					patch_instruction, null);
			}
			// On r�cup�re de toute fa�on l'instruction et le nom du param�tre
			instruction = tokenizer.nextToken();
			parameter_name = tokenizer.nextToken();
			trace_debug.writeTrace("instruction=" + instruction);
			trace_debug.writeTrace("parameter_name=" + parameter_name);
			// Suivant la valeur de l'instruction, on effectue le traitement
			// ad�quat
			if(instruction.equalsIgnoreCase("ADD") == true)
			{
				trace_debug.writeTrace("Ajout d'un param�tre");
				// On r�cup�re la valeur
				value = patch_instruction.substring(instruction.length() +
					parameter_name.length() + 2);
				trace_debug.writeTrace("value=" + value);
				// On v�rifie si le param�tre n'existe pas d�j�
				if(getParameterPosition(parameter_name, 0, -1) != -1)
				{
					// On passe au suivant
					continue;
				}
				// On ajoute une ligne � la fin du vecteur
				_fileContent.add(parameter_name + "=" + value);
				// On passe au suivant
				continue;
			}
			if(instruction.equalsIgnoreCase("UPDATE") == true)
			{
				trace_debug.writeTrace("Modification d'un param�tre");
				// On r�cup�re la valeur
				value = patch_instruction.substring(instruction.length() +
					parameter_name.length() + 2);
				// On r�cup�re la position du param�tre
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
				trace_debug.writeTrace("Suppression d'un param�tre");
				// On r�cup�re la position du param�tre
				int position = getParameterPosition(parameter_name, 0, -1);
				// Si la position est valide, on supprime l'�l�ment �
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
			// On l�ve une exception
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
	* Cette m�thode est charg�e de scruter le contenu du fichier � patcher, 
	* contenu dans l'attribut _fileContent, � la recherche d'un param�tre dont 
	* le nom est pass� en argument.
	* Chaque �l�ment du vecteur _fileContent est une cha�ne de caract�re 
	* repr�sentant une ligne du fichier LAX. Les param�tres sont identifiables 
	* gr�ce � la pr�sence du caract�re '=' dans la ligne, suivant la syntaxe 
	* <Parametre>=<Valeur>.
	* 
	* Arguments:
	*  - parameterName: Le nom du param�tre dont la position dans le vecteur 
	*    est requise,
	*  - startFrom: La position de d�part pour la recherche,
	*  - endAt: L'�ventuelle position de fin de recherche.
	* 
	* Retourne: La position du param�tre dans le vecteur, ou -1 si celui-ci ne 
	* peut pas �tre trouv�.
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
		// On v�rifie que la position de d�part est valide
		if(startFrom < 0 || startFrom >= _fileContent.size())
		{
			trace_debug.writeTrace("La position de d�part est hors limites !");
			// On sort
			trace_methods.endOfMethod();
			return -1;
		}
		// On v�rifie si la position de fin est positionn�e et est bien
		// positionn�e
		if(endAt == -1 || endAt >= _fileContent.size())
		{
			endAt = _fileContent.size() - 1;
		}
		// On va regarder dans chaque �l�ment du vecteur _patchContent si une
		// des lignes commence par "<ParameterName>="
		for(int index = startFrom ; index <= endAt ; index ++)
		{
			String element = (String)_fileContent.elementAt(index);
			if(element.equals("") == true || element.startsWith("#") == true)
			{
				// On passe au suivant
				continue;
			}
			trace_debug.writeTrace("V�rification de l'�l�ment: " +
				element);
			if(element.startsWith(parameterName + "=") == true)
			{
				trace_debug.writeTrace("Param�tre trouv� � la position: " +
					index);
				// On peut sortir
				trace_methods.endOfMethod();
				return index;
			}
		}
		// Si on arrive ici, c'est que l'on n'a pas trouv� le param�tre
		trace_debug.writeTrace("Param�tre non trouv�");
		trace_methods.endOfMethod();
		return -1;
	}

	/*----------------------------------------------------------
	* Nom: getParameterValue
	* 
	* Description:
	* Cette m�thode est charg�e d'extraire et de retourner la valeur associ�e 
	* � un param�tre. Suivant la syntaxe d'une ligne de param�tre 
	* (<Parametre>=<Valeur>), la valeur est la portion de cha�ne se trouvant 
	* apr�s le caract�re '='.
	* La ligne de param�tre est identifi�e par sa position dans le vecteur.
	* 
	* Arguments:
	*  - parameterPosition: La position de la ligne de param�tre dans le 
	*    vecteur.
	* 
	* Retourne: Une cha�ne de caract�res contenant la valeur du param�tre.
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
		// On v�rifie que la position est valide
		if(parameterPosition < 0 || parameterPosition >= _fileContent.size())
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("argument hors limites !");
			// On sort
			trace_methods.endOfMethod();
			return value;
		}
		// On va r�cup�rer l'�l�ment � la position indiqu�e
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
	* Cette m�thode est charg�e de la lecture du contenu du fichier � patcher. 
	* Celle-ci consiste � lire le fichier ligne par ligne, et � stocker 
	* celles-ci dans un vecteur, sous forme de cha�nes de caract�res, 
	* repr�sent� par l'attribut _fileContent.
	* Au pr�alable, la m�thode v�rifie que le fichier � patcher peut �tre lu 
	* et �crit.
	* 
	* Si une erreur survient pendant la lecture du fichier � patcher, 
	* l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - fileToPatch: Une r�f�rence sur un objet File correspondant au 
	*    fichier � patcher.
	* 
	* L�ve: InnerException.
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
		// On v�rifie la validit� de l'argument
		if(fileToPatch == null)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On v�rifie que le fichier est accessible en lecture et en �criture
		if(fileToPatch.exists() == false || fileToPatch.isFile() == false ||
			fileToPatch.canRead() == false || fileToPatch.canWrite() == false)
		{
			trace_errors.writeTrace(
				"Le fichier " + fileToPatch.getAbsolutePath() +
				"est soit absent, soit un r�pertoire, n'est pas lisible, " +
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
	* Cette m�thode est charg�e d'�crire le contenu modifi� dans le fichier � 
	* patcher. Pour cela, elle va enregistrer chacune des cha�nes de 
	* caract�res contenues dans l'attribut _fileContent dans le fichier sur 
	* une nouvelle ligne.
	* 
	* Si un probl�me survient lors de l'�criture du fichier, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - patchedFile: Une r�f�rence sur un objet File correspondant au fichier 
	*    � �crire.
	* 
	* L�ve: InnerException.
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
		// On v�rifie la validit� de l'argument
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
			trace_errors.writeTrace("Erreur lors de l'�criture du fichier " +
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
