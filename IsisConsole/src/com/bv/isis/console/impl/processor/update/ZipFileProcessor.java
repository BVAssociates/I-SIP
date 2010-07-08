/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/ZipFileProcessor.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de traitement d'une archive ZIP
* DATE:        26/10/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.update
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ZipFileProcessor.java,v $
* Revision 1.3  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2008/02/13 16:02:25  tz
* Prise en compte des entrées de Zip de type répertoire.
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
import com.bv.core.message.MessageManager;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

//
//Imports du projet
//
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: ZipFileProcessor
* 
* Description:
* Cette classe est une classe d'implémentation permettant le traitement d'une 
* entrée de type archive ZIP du fichier de configuration des mises à jour.
* Son objectif est d'extraire tout le contenu de l'archive source vers un 
* répertoire de destination.
* Elle dérive de la classe AnyFileProcessor.
* ----------------------------------------------------------*/
public class ZipFileProcessor 
	extends AnyFileProcessor
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ZipFileProcessor
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
	* ----------------------------------------------------------*/
	public ZipFileProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ZipFileProcessor", "ZipFileProcessor");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: processEntry
	* 
	* Description:
	* Cette méthode redéfinit celle de la classe AnyFileProcessor. Elle est 
	* appelée pour déclencher le traitement d'une entrée du fichier de 
	* configuration des mises à jour.
	* Dans le cas présent (archive ZIP), la méthode extrait chaque fichier de 
	* l'archive vers le répertoire de destination, dont le chemin est 
	* construit à partir du résultat de la méthode getExpandDirectory().
	* Pour chaque fichier de l'archive, la méthode crée ou ouvre en écrasement 
	* un fichier, et le contenu du fichier de l'archive est copié via la 
	* méthode copyStream() de la super-classe.
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
			"ZipFileProcessor", "processEntry");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		ZipFile zip_file = null;
			
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
				"Au moins des arguments n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On affiche la progression
		progressInterface.updateProgress(
			MessageManager.getMessage("&Update_UnzippingFile"), entryName, 0, 
			0);
		try
		{
			int index = 0;
			
			// On va construire le chemin d'expansion
			String expand_directory = getExpandDirectory(entryName, 
				destinationDirectory);
			// On va ouvrir l'archive
			zip_file = new ZipFile(entryFile);
			// On va récupérer le contenu de l'archive
			Enumeration entries = zip_file.entries();
			// On va traiter les entrées une par une
			while(entries.hasMoreElements() == true)
			{
				ZipEntry entry = (ZipEntry)entries.nextElement();
				String file_name = entry.getName();
				trace_debug.writeTrace("file_name=" + file_name);
				// On saute les répertoires
				if(entry.isDirectory() == true) {
					trace_debug.writeTrace("Il s'agit d'un répertoire");
					continue;
				}
				// On met à jour la progression
				progressInterface.updateProgress(null, file_name, 0, -1);
				// On va construire un fichier pour cette entrée
				getExpandDirectory(file_name, expand_directory);
				File destination_file = new File(expand_directory,
					file_name);
				// On ouvre les streams
				InputStream input_stream = zip_file.getInputStream(entry);
				FileOutputStream output_stream =
					new FileOutputStream(destination_file, false);
				// On copie le fichier
				copyStream(input_stream, output_stream, progressInterface);
				// On affiche la progression
				index ++;
				int progress = (100 * index) / zip_file.size(); 
				progressInterface.updateProgress(null, null, 0, progress);
				input_stream.close();
				output_stream.close();
			}
			// On ferme l'archive
			zip_file.close();
			// On met à jour la progression
			progressInterface.updateProgress(null, null, 0, 100);
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors du traitement du fichier " +
				entryFile.getAbsolutePath() + ": " + exception.getMessage());
			if(zip_file != null)
			{
				try
				{
					zip_file.close();
				}
				catch(Exception e)
				{
					// On ne fait rien
				}
			}
			// On renvoie une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_CannotProcessFile", 
				exception.getMessage(), exception);
		}
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: getExpandDirectory
	* 
	* Description:
	* Cette méthode permet de construire le chemin du répertoire d'expansion 
	* de l'entrée passée en argument. Ce chemin correspond à la concaténation 
	* du répertoire de destination de base, passé en argument, et de la partie 
	* chemin du nom de l'entrée (tout ce qui se trouve avant le dernier '/').
	* Si le répertoire d'expansion n'existe pas, il est créé.
	* 
	* Si un problème survient lors de la création du répertoire, l'exception 
	* Exception est levée.
	* 
	* Arguments:
	*  - entryName: le nom de l'entrée,
	*  - destinationDirectory: Le répertoire de destination de base.
	* 
	* Retourne: Le chemin du répertoire d'expansion.
	* 
	* Lève: Exception.
	* ----------------------------------------------------------*/
	private String getExpandDirectory(
		String entryName,
		String destinationDirectory
		)
		throws
			Exception
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ZipFileProcessor", "getExpandDirectory");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		int slash_position = 0;
		File expand_directory = null;
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("entryName=" + entryName);
		trace_arguments.writeTrace("destinationDirectory=" + 
			destinationDirectory);
		// On vérifie la validité des arguments
		if(entryName == null || entryName.equals("") == true ||
			destinationDirectory.equals("") == true)
		{
			trace_errors.writeTrace(
				"Au moins des arguments n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new Exception(
				MessageManager.getMessage("&ERR_InvalidArgument"));
		}
		// On cherche la position du dernier '/'
		slash_position = entryName.lastIndexOf('/');
		trace_debug.writeTrace("slash_position=" + slash_position);
		if(slash_position >= 0)
		{
			expand_directory = new File(destinationDirectory,
				entryName.substring(0, slash_position));
		}
		else
		{
			expand_directory = new File(destinationDirectory);
		}
		trace_debug.writeTrace("expand_directory=" +
			expand_directory.getAbsolutePath());
		// On teste l'existence du répertoire
		if(expand_directory.exists() == false && 
			expand_directory.mkdirs() == false)
		{
			trace_errors.writeTrace(
				"Impossible de créer le répertoire d'expansion");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new Exception(
				MessageManager.getMessage("&ERR_CannotCreateExpandDirectory"));
		}
		trace_methods.endOfMethod();
		return expand_directory.getAbsolutePath();
	}
}
