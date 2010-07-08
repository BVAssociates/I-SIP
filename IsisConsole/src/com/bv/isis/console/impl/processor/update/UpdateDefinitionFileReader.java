/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/UpdateDefinitionFileReader.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'analyse de fichier de d�finition de mise � jour
* DATE:        21/12/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.update
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: UpdateDefinitionFileReader.java,v $
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
import java.util.Vector;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

//
//Imports du projet
//
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: UpdateDefinitionFileReader
* 
* Description:
* Cette classe est une classe technique charg�e de la lecture et du traitement 
* d'un fichier de mise � jour (fichier UDF).
* Elle est charg�e d'effectuer la lecture et l'analyse du fichier, de v�rifier 
* la compatibilit� de la version actuelle de la Console avec la ou les 
* versions de r�f�rence du fichier de d�finition, et de d�clencher le 
* traitement de chacune des entr�es d�finies dans le fichier.
* ----------------------------------------------------------*/
class UpdateDefinitionFileReader
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: UpdateDefinitionFileReader
	* 
	* Description:
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
	* ----------------------------------------------------------*/
	public UpdateDefinitionFileReader()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateDefinitionFileReader", "UpdateDefinitionFileReader");

		trace_methods.beginningOfMethod();
		_entries = new Vector();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: processFile
	* 
	* Description:
	* Cette m�thode est charg�e du traitement d'un fichier de d�finition de 
	* mise � jour.
	* Tout d'abord, elle v�rifie l'existence d'un fichier "entries.udf" dans 
	* le r�pertoire temporaire dont le chemin est pass� en argument.
	* Puis, elle d�clenche la lecture et l'analyse du fichier, par le biais de 
	* la m�thode readFile().
	* Ensuite, elle effectue la v�rification de la compatibilit� de la version 
	* courante de la Console I-SIS avec les sp�cifications de la d�finition, 
	* via la m�thode checkRelease().
	* Et finalement, elle lance le traitement de chaque entr�e du fichier de 
	* d�finition par le biais de la m�thode processEntries().
	* 
	* Si un probl�me survient pendant le traitement du fichier de d�finition 
	* ou des entr�es, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - temporaryDirectory: Le chemin du r�pertoire temporaire o� se trouve 
	*    le fichier de d�finition,
	*  - destinationDirectory: Le chemin du r�pertoire de destination de base 
	*    pour les mises � jour,
	*  - progressInterface: Une r�f�rence sur un objet UpdateProgressInterface 
	*    permettant le suivi de la progression.
	* 
	* Retourne: true si la Console doit �tre red�marr�e apr�s l'installation 
	* de la mise � jour, ou false dans le cas contraire.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public boolean processFile(
		String temporaryDirectory,
		String destinationDirectory,
		UpdateProgressInterface progressInterface
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateDefinitionFileReader", "processFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		File udf_file = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("temporaryDirectory=" + temporaryDirectory);
		trace_arguments.writeTrace("destinationDirectory=" + 
			destinationDirectory);
		trace_arguments.writeTrace("progressInterface=" + progressInterface);
		// On v�rifie la validit� des arguments
		if(temporaryDirectory == null || 
			temporaryDirectory.equals("") == true || 
			destinationDirectory == null ||
			destinationDirectory.equals("") == true ||
			progressInterface == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On va construire le chemin du fichier de d�finition de la mise 
		// � jour
		udf_file = new File(temporaryDirectory, "entries.udf");
		trace_debug.writeTrace("udf_file=" + udf_file.getAbsolutePath());
		// On va v�rifier que le fichier de d�finition est pr�sent
		if(udf_file.exists() == false || udf_file.isFile() == false)
		{
			trace_errors.writeTrace("Le fichier de d�finition de la mise � " +
				"jour '" + udf_file.getAbsolutePath() + "' n'existe pas ou est" +
				" un r�pertoire !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_UDFFileNonExistentOrIsDirectory", 
				udf_file.getAbsolutePath(), null); 
		}
		trace_debug.writeTrace("Le fichier de d�finition existe");
		// On va d�clencher la lecture du fichier
		readFile(udf_file);
		// On va v�rifier la compatibilit� des versions
		if(checkRelease() == false)
		{
			trace_errors.writeTrace("La mise � jour ne s'applique pas � " +
				"la version actuelle de la Console I-SIS !");
			// Les versions ne sont pas compatibles, on va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_UpdateNotApplicable", null, null);
		}
		// On va d�clencher le traitement des entr�es
		processEntries(temporaryDirectory, destinationDirectory, 
			progressInterface);
		// On peut sortir
		trace_methods.endOfMethod();
		return _rebootAfterwards;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _upgradableReleases
	* 
	* Description:
	* Cet attribut maintient une chaine de caract�res contenant la liste des 
	* versions de r�f�rences lues depuis le fichier de d�finition de la mise � 
	* jour.
	* ----------------------------------------------------------*/
	private String _upgradableReleases;

	/*----------------------------------------------------------
	* Nom: _rebootAfterwards
	* 
	* Description:
	* Cet attribut maintient un bool�en indiquant si la Console doit �tre 
	* red�marr�e apr�s l'installation de la mise � jour. L'information est lue 
	* depuis le fichier de d�finition de la mise � jour.
	* ----------------------------------------------------------*/
	private boolean _rebootAfterwards;

	/*----------------------------------------------------------
	* Nom: _updateDescription
	* 
	* Description:
	* Cet attribut maintient une chaine de caract�res contenant la description 
	* de la mise � jour lues depuis le fichier de d�finition de celle-ci.
	* ----------------------------------------------------------*/
	private String _updateDescription;

	/*----------------------------------------------------------
	* Nom: _entries
	* 
	* Description:
	* Cet attribut maintient une r�f�rence sur un vecteur destin� � contenir 
	* les entr�es � traiter pour effectuer la mise � jour.
	* ----------------------------------------------------------*/
	private Vector _entries;

	/*----------------------------------------------------------
	* Nom: readFile
	* 
	* Description:
	* Cette m�thode est charg�e de la lecture du fichier de d�finition de la 
	* mise � jour et de son analyse, fichier repr�sent� par l'argument.
	* Toute ligne du fichier de d�finition vide ou de commentaire (commen�ant 
	* par '#') est ignor�e.
	* L'analyse consite � r�cup�rer dans le fichier des informations d�finies 
	* par des mots-cl�s d�termin�s. Ces mots-cl�s doivent �tre situ�s en d�but 
	* de ligne.
	* Les mots-cl�s sont les suivants :
	*  - UpgradableReleases: Cha�ne de caract�res correspondant � la liste des 
	*    versions de Consoles I-SIS auxquelles s'appliquent la mise � jour 
	*    (voir la m�thode checkRelease() pour plus de d�tails),
	*  - RebootAfterwards: Bool�en indiquant si la Console I-SIS doit �tre 
	*    red�marr�e apr�s l'installation ou non,
	*  - UpdateDescription: Cha�ne de caract�res d�crivant la nature de la 
	*    mise � jour,
	*  - Entry: Cha�ne de caract�res indiquant le chemin relatif d'un des 
	*    fichiers extrait du fichier de mise � jour et devant �tre install�. 
	*    L'attribut _entries est enrichi � partir de ces informations.
	* 
	* Si une erreur survient lors de l'analyse du fichier de d�finition, 
	* l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - definitionFile: Une r�f�rence sur un objet File correspondant au 
	*    fichier de d�finition de la mise � jour.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private void readFile(
		File definitionFile
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateDefinitionFileReader", "readFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_io = TraceAPI.declareTraceIO("Console");
		boolean reboot_tag_found = false;
		BufferedReader reader = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("definitionFile=" + definitionFile);
		// On v�rifie la validit� de l'argument
		if(definitionFile == null)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		try
		{
			String line = null;
			
			// On va lire le fichier
			reader =  new BufferedReader(new FileReader(definitionFile));
			// Lecture du contenu du fichier ligne par ligne
			while(true)
			{
				line = reader.readLine();
				if(line == null)
				{
					trace_debug.writeTrace("Fin du fichier atteinte");
					break;
				}
				trace_io.writeTrace("Lu depuis le fichier: '" + line + "'");
				// S'il s'agit d'une ligne vide ou d'une ligne de commentaire, on
				// passe � la suivante
				if(line.equals("") == true || line.startsWith("#") == true)
				{
					continue;
				}
				// On va d�couper la ligne en sous-cha�ne sur l'espace
				UtilStringTokenizer tokenizer = new UtilStringTokenizer(line, " ");
				String keyword = tokenizer.nextToken();
				trace_debug.writeTrace("keyword=" + keyword); 
				// On v�rifie le mot-cl�
				if(keyword.equals("UpgradableReleases") == true)
				{
					// On valorise l'attribut _upgradableReleases
					_upgradableReleases = tokenizer.nextToken();
					continue;
				}
				if(keyword.equals("RebootAfterwards") == true)
				{
					// On valorise l'attribut _rebootAfterwards
					_rebootAfterwards = 
						Boolean.valueOf(tokenizer.nextToken()).booleanValue();
					reboot_tag_found = true;
					continue;
				}
				if(keyword.equals("UpdateDescription") == true)
				{
					// On valorise l'attribut _updateDescription
					_updateDescription = line.substring(keyword.length() + 1);
					continue;
				}
				if(keyword.equals("Entry") == true)
				{
					// On valorise l'attribut _entries
					_entries.add(line.substring(keyword.length() + 1));
					continue;
				}
				// Si on arrive l�, c'est qu'il s'agit d'un mot-cl� inconnu
				trace_errors.writeTrace("Mot-cl� inconnu: " + keyword);
				// On va lever une exception
				trace_methods.endOfMethod();
				throw new InnerException("&ERR_UnknownKeywordInUDF", keyword, 
					null);
			}
			// On peut fermer le fichier
			reader.close();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors de l'analyse du fichier de " +
				"d�finition: " + exception.getMessage());
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
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ErrorWhileAnalysingUDF", 
				exception.getMessage(), exception);
		}
		trace_debug.writeTrace("_upgradableReleases=" + _upgradableReleases);
		trace_debug.writeTrace("_rebootAfterwards=" + _rebootAfterwards);
		trace_debug.writeTrace("reboot_tag_found=" + reboot_tag_found);
		trace_debug.writeTrace("_updateDescription=" + _updateDescription);
		trace_debug.writeTrace("_entries size=" + _entries.size());
		// On v�rifie que les informations importantes sont pr�sentes
		if(_upgradableReleases == null || 
			_upgradableReleases.equals("") == true ||
			reboot_tag_found == false ||
			_updateDescription == null || 
			_updateDescription.equals("") == true ||
			_entries.size() == 0)
		{
			trace_errors.writeTrace("Au moins une des informations obligatoires" +
				" n'est pas pr�sente dans le fichier de d�finition !");
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_MandatoryInformationMissing", 
				definitionFile.getAbsolutePath(), null);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: checkRelease
	* 
	* Description:
	* Cette m�thode est charg�e de v�rifier que la mise � jour peut �tre 
	* install�e sur la Console I-SIS. Pour que cela soit possible, il faut que 
	* la version courante de la Console I-SIS apparaisse dans la liste 
	* contenue dans l'attribut _upgradableReleases.
	* 
	* Retourne: true si la mise � jour peut s'appliquer � la version actuelle 
	* de la Console I-SIS, false sinon.
	* ----------------------------------------------------------*/
	private boolean checkRelease()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateDefinitionFileReader", "checkRelease");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		String console_release = null;
		
		trace_methods.beginningOfMethod();
		// On r�cup�re la version actuelle de la Console
		UtilStringTokenizer tokenizer = 
			new UtilStringTokenizer(MessageManager.getMessage("&AD_Version"), 
			" ");
		console_release = tokenizer.getToken(1);
		trace_debug.writeTrace("console_release=" + console_release);
		// On d�coupe la liste des versions concern�es par le s�parateur ','
		tokenizer = new UtilStringTokenizer(_upgradableReleases, ",");
		// On v�rifie la version actuelle par rapport � chacune des versions
		// de r�f�rence
		while(tokenizer.hasMoreTokens() == true)
		{
			String reference_release = tokenizer.nextToken();
			trace_debug.writeTrace("reference_release=" + reference_release);
			if(reference_release.equalsIgnoreCase(console_release) == true)
			{
				trace_debug.writeTrace("Les versions sont compatibles");
				// On peut sortir
				trace_debug.endOfMethod();
				return true;
			}
		}
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: processEntries
	* 
	* Description:
	* Cette m�thode est charg�e d'effectuer le traitement des entr�es du 
	* fichier de mise � jour. Une entr�e correspond � un fichier extrait de 
	* l'archive.
	* Pour chacune des entr�es, un processeur sera instanci� par le biais de 
	* la classe EntryProcessorFactory, puis le traitement sera d�l�gu� � la 
	* classe d'impl�mentation via la m�thode processEntry().
	* 
	* Si une erreur survient lors du traitement d'une entr�e, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - temporaryDirectory: Le chemin du r�pertoire temporaire de base o� ont 
	*    �t� extraits les fichiers � traiter,
	*  - destinationDirectory: Le chemin du r�pertoire de base d'installation 
	*    de la Console I-SIS o� doivent �tre install�s les fichiers � traiter,
	*  - progressInterface: Une r�f�rence sur un objet UpdateProgressInterface 
	*    permettant le suivi de la progression.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private void processEntries(
		String temporaryDirectory,
		String destinationDirectory,
		UpdateProgressInterface progressInterface
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"UpdateDefinitionFileReader", "processEntries");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		int number_of_entries = 0;
		int counter = 0;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("temporaryDirectory=" + temporaryDirectory);
		trace_arguments.writeTrace("destinationDirectory=" + 
			destinationDirectory);
		trace_arguments.writeTrace("progressInterface=" + progressInterface);
		// On v�rifie la validit� des arguments
		if(temporaryDirectory == null || 
			temporaryDirectory.equals("") == true || 
			destinationDirectory == null ||
			destinationDirectory.equals("") == true ||
			progressInterface == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		progressInterface.updateProgress(_updateDescription, null, 0, 0);
		number_of_entries = _entries.size();
		// On va traiter les entr�es une par une
		while(_entries.size() > 0)
		{
			File entry_file = null;
			
			String entry = (String)_entries.elementAt(0);
			_entries.remove(0);
			trace_debug.writeTrace("Traitement de l'entr�e: " + entry);
			progressInterface.updateProgress(null, entry, 0, 
				(100 * counter) / number_of_entries);
			// On va r�cup�rer un processeur pour cette entr�e
			EntryProcessorInterface entry_processor = 
				EntryProcessorFactory.getProcessorForEntry(entry);
			// On traite l'entr�e
			entry_processor.processEntry(entry, 
				entry_file = new File(temporaryDirectory, entry), 
				destinationDirectory, progressInterface);
			counter++;
		}
		progressInterface.updateProgress(null, null, 0, 100);
		trace_methods.endOfMethod();
	}
}
