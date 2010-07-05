/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/UpdateDefinitionFileReader.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'analyse de fichier de définition de mise à jour
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
// Déclaration du package
package com.bv.isis.console.impl.processor.update;

//
// Imports système
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
* Cette classe est une classe technique chargée de la lecture et du traitement 
* d'un fichier de mise à jour (fichier UDF).
* Elle est chargée d'effectuer la lecture et l'analyse du fichier, de vérifier 
* la compatibilité de la version actuelle de la Console avec la ou les 
* versions de référence du fichier de définition, et de déclencher le 
* traitement de chacune des entrées définies dans le fichier.
* ----------------------------------------------------------*/
class UpdateDefinitionFileReader
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: UpdateDefinitionFileReader
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
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
	* Cette méthode est chargée du traitement d'un fichier de définition de 
	* mise à jour.
	* Tout d'abord, elle vérifie l'existence d'un fichier "entries.udf" dans 
	* le répertoire temporaire dont le chemin est passé en argument.
	* Puis, elle déclenche la lecture et l'analyse du fichier, par le biais de 
	* la méthode readFile().
	* Ensuite, elle effectue la vérification de la compatibilité de la version 
	* courante de la Console I-SIS avec les spécifications de la définition, 
	* via la méthode checkRelease().
	* Et finalement, elle lance le traitement de chaque entrée du fichier de 
	* définition par le biais de la méthode processEntries().
	* 
	* Si un problème survient pendant le traitement du fichier de définition 
	* ou des entrées, l'exception InnerException est levée.
	* 
	* Arguments:
	*  - temporaryDirectory: Le chemin du répertoire temporaire où se trouve 
	*    le fichier de définition,
	*  - destinationDirectory: Le chemin du répertoire de destination de base 
	*    pour les mises à jour,
	*  - progressInterface: Une référence sur un objet UpdateProgressInterface 
	*    permettant le suivi de la progression.
	* 
	* Retourne: true si la Console doit être redémarrée après l'installation 
	* de la mise à jour, ou false dans le cas contraire.
	* 
	* Lève: InnerException.
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
		// On vérifie la validité des arguments
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
		// On va construire le chemin du fichier de définition de la mise 
		// à jour
		udf_file = new File(temporaryDirectory, "entries.udf");
		trace_debug.writeTrace("udf_file=" + udf_file.getAbsolutePath());
		// On va vérifier que le fichier de définition est présent
		if(udf_file.exists() == false || udf_file.isFile() == false)
		{
			trace_errors.writeTrace("Le fichier de définition de la mise à " +
				"jour '" + udf_file.getAbsolutePath() + "' n'existe pas ou est" +
				" un répertoire !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_UDFFileNonExistentOrIsDirectory", 
				udf_file.getAbsolutePath(), null); 
		}
		trace_debug.writeTrace("Le fichier de définition existe");
		// On va déclencher la lecture du fichier
		readFile(udf_file);
		// On va vérifier la compatibilité des versions
		if(checkRelease() == false)
		{
			trace_errors.writeTrace("La mise à jour ne s'applique pas à " +
				"la version actuelle de la Console I-SIS !");
			// Les versions ne sont pas compatibles, on va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_UpdateNotApplicable", null, null);
		}
		// On va déclencher le traitement des entrées
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
	* Cet attribut maintient une chaine de caractères contenant la liste des 
	* versions de références lues depuis le fichier de définition de la mise à 
	* jour.
	* ----------------------------------------------------------*/
	private String _upgradableReleases;

	/*----------------------------------------------------------
	* Nom: _rebootAfterwards
	* 
	* Description:
	* Cet attribut maintient un booléen indiquant si la Console doit être 
	* redémarrée après l'installation de la mise à jour. L'information est lue 
	* depuis le fichier de définition de la mise à jour.
	* ----------------------------------------------------------*/
	private boolean _rebootAfterwards;

	/*----------------------------------------------------------
	* Nom: _updateDescription
	* 
	* Description:
	* Cet attribut maintient une chaine de caractères contenant la description 
	* de la mise à jour lues depuis le fichier de définition de celle-ci.
	* ----------------------------------------------------------*/
	private String _updateDescription;

	/*----------------------------------------------------------
	* Nom: _entries
	* 
	* Description:
	* Cet attribut maintient une référence sur un vecteur destiné à contenir 
	* les entrées à traiter pour effectuer la mise à jour.
	* ----------------------------------------------------------*/
	private Vector _entries;

	/*----------------------------------------------------------
	* Nom: readFile
	* 
	* Description:
	* Cette méthode est chargée de la lecture du fichier de définition de la 
	* mise à jour et de son analyse, fichier représenté par l'argument.
	* Toute ligne du fichier de définition vide ou de commentaire (commençant 
	* par '#') est ignorée.
	* L'analyse consite à récupérer dans le fichier des informations définies 
	* par des mots-clés déterminés. Ces mots-clés doivent être situés en début 
	* de ligne.
	* Les mots-clés sont les suivants :
	*  - UpgradableReleases: Chaîne de caractères correspondant à la liste des 
	*    versions de Consoles I-SIS auxquelles s'appliquent la mise à jour 
	*    (voir la méthode checkRelease() pour plus de détails),
	*  - RebootAfterwards: Booléen indiquant si la Console I-SIS doit être 
	*    redémarrée après l'installation ou non,
	*  - UpdateDescription: Chaîne de caractères décrivant la nature de la 
	*    mise à jour,
	*  - Entry: Chaîne de caractères indiquant le chemin relatif d'un des 
	*    fichiers extrait du fichier de mise à jour et devant être installé. 
	*    L'attribut _entries est enrichi à partir de ces informations.
	* 
	* Si une erreur survient lors de l'analyse du fichier de définition, 
	* l'exception InnerException est levée.
	* 
	* Arguments:
	*  - definitionFile: Une référence sur un objet File correspondant au 
	*    fichier de définition de la mise à jour.
	* 
	* Lève: InnerException.
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
		// On vérifie la validité de l'argument
		if(definitionFile == null)
		{
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On lève une exception
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
				// passe à la suivante
				if(line.equals("") == true || line.startsWith("#") == true)
				{
					continue;
				}
				// On va découper la ligne en sous-chaîne sur l'espace
				UtilStringTokenizer tokenizer = new UtilStringTokenizer(line, " ");
				String keyword = tokenizer.nextToken();
				trace_debug.writeTrace("keyword=" + keyword); 
				// On vérifie le mot-clé
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
				// Si on arrive là, c'est qu'il s'agit d'un mot-clé inconnu
				trace_errors.writeTrace("Mot-clé inconnu: " + keyword);
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
				"définition: " + exception.getMessage());
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
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ErrorWhileAnalysingUDF", 
				exception.getMessage(), exception);
		}
		trace_debug.writeTrace("_upgradableReleases=" + _upgradableReleases);
		trace_debug.writeTrace("_rebootAfterwards=" + _rebootAfterwards);
		trace_debug.writeTrace("reboot_tag_found=" + reboot_tag_found);
		trace_debug.writeTrace("_updateDescription=" + _updateDescription);
		trace_debug.writeTrace("_entries size=" + _entries.size());
		// On vérifie que les informations importantes sont présentes
		if(_upgradableReleases == null || 
			_upgradableReleases.equals("") == true ||
			reboot_tag_found == false ||
			_updateDescription == null || 
			_updateDescription.equals("") == true ||
			_entries.size() == 0)
		{
			trace_errors.writeTrace("Au moins une des informations obligatoires" +
				" n'est pas présente dans le fichier de définition !");
			// On lève une exception
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
	* Cette méthode est chargée de vérifier que la mise à jour peut être 
	* installée sur la Console I-SIS. Pour que cela soit possible, il faut que 
	* la version courante de la Console I-SIS apparaisse dans la liste 
	* contenue dans l'attribut _upgradableReleases.
	* 
	* Retourne: true si la mise à jour peut s'appliquer à la version actuelle 
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
		// On récupère la version actuelle de la Console
		UtilStringTokenizer tokenizer = 
			new UtilStringTokenizer(MessageManager.getMessage("&AD_Version"), 
			" ");
		console_release = tokenizer.getToken(1);
		trace_debug.writeTrace("console_release=" + console_release);
		// On découpe la liste des versions concernées par le séparateur ','
		tokenizer = new UtilStringTokenizer(_upgradableReleases, ",");
		// On vérifie la version actuelle par rapport à chacune des versions
		// de référence
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
	* Cette méthode est chargée d'effectuer le traitement des entrées du 
	* fichier de mise à jour. Une entrée correspond à un fichier extrait de 
	* l'archive.
	* Pour chacune des entrées, un processeur sera instancié par le biais de 
	* la classe EntryProcessorFactory, puis le traitement sera délégué à la 
	* classe d'implémentation via la méthode processEntry().
	* 
	* Si une erreur survient lors du traitement d'une entrée, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - temporaryDirectory: Le chemin du répertoire temporaire de base où ont 
	*    été extraits les fichiers à traiter,
	*  - destinationDirectory: Le chemin du répertoire de base d'installation 
	*    de la Console I-SIS où doivent être installés les fichiers à traiter,
	*  - progressInterface: Une référence sur un objet UpdateProgressInterface 
	*    permettant le suivi de la progression.
	* 
	* Lève: InnerException.
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
		// On vérifie la validité des arguments
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
		// On va traiter les entrées une par une
		while(_entries.size() > 0)
		{
			File entry_file = null;
			
			String entry = (String)_entries.elementAt(0);
			_entries.remove(0);
			trace_debug.writeTrace("Traitement de l'entrée: " + entry);
			progressInterface.updateProgress(null, entry, 0, 
				(100 * counter) / number_of_entries);
			// On va récupérer un processeur pour cette entrée
			EntryProcessorInterface entry_processor = 
				EntryProcessorFactory.getProcessorForEntry(entry);
			// On traite l'entrée
			entry_processor.processEntry(entry, 
				entry_file = new File(temporaryDirectory, entry), 
				destinationDirectory, progressInterface);
			counter++;
		}
		progressInterface.updateProgress(null, null, 0, 100);
		trace_methods.endOfMethod();
	}
}
