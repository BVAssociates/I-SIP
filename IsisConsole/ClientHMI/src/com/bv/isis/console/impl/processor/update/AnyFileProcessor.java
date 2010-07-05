/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/AnyFileProcessor.java,v $
* $Revision: 1.2 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de traitement d'un fichier quelconque
* DATE:        26/10/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.update
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: AnyFileProcessor.java,v $
* Revision 1.2  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.1  2005/12/23 13:23:42  tz
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

//
//Imports du projet
//
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: AnyFileProcessor
* 
* Description:
* Cette classe est une classe d'implémentation permettant le traitement d'une 
* entrée quelconque du fichier de configuration des mises à jour.
* Son objectif est de copier le contenu du fichier source vers un fichier 
* destination (voir la méthode processEntry()).
* Elle implémente l'interface EntryProcessorInterface.
* ----------------------------------------------------------*/
class AnyFileProcessor 
	implements EntryProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: AnyFileProcessor
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
	* ----------------------------------------------------------*/
	public AnyFileProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AnyFileProcessor", "AnyFileProcessor");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: processEntry
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface EntryProcessorInterface. 
	* Elle est appelée pour déclencher le traitement d'une entrée du fichier 
	* de configuration des mises à jour.
	* Dans le cas présent (fichier quelconque), la méthode crée ou ouvre le 
	* fichier destination, dont le nom est constitué à partir du répertoire de 
	* destination et du nom de l'entrée, puis copie le contenu du fichier 
	* source vers le fichier de destination, via la méthode copyStream().
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
			"AnyFileProcessor", "processEntry");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		File destination_file = null;
		FileInputStream input_stream = null;
			
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
		// On va construire le chemin du fichier destination
		destination_file = new File(destinationDirectory, entryName);
		trace_debug.writeTrace("destination_file=" + destination_file.getAbsolutePath());
		// On affiche la progression
		progressInterface.updateProgress(
			MessageManager.getMessage("&Update_CopyingFile"), entryName, 0, 0);
		try
		{
			// On va ouvrir les streams
			input_stream = new FileInputStream(entryFile);
			FileOutputStream output_stream =
				new FileOutputStream(destination_file, false);
			// On copie le fichier
			copyStream(input_stream, output_stream, progressInterface);
			// On affiche la progression
			progressInterface.updateProgress(null, entryName, 0, 100);
			input_stream.close();
			output_stream.close();
		}
		catch(Exception exception)
		{
			trace_errors.writeTrace("Erreur lors du traitement du fichier " +
				destination_file.getAbsolutePath() + ": " + 
				exception.getMessage());
			if(input_stream != null)
			{
				try
				{
					input_stream.close();
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
	/*----------------------------------------------------------
	* Nom: copyStream
	* 
	* Description:
	* Cette méthode permet de copier intégralement le contenu du stream en 
	* entrée (inputStream) vers le stream en sortie (outputStream).
	* 
	* Si une erreur survient, l'exception Exception est levée.
	* 
	* Arguments:
	*  - inputStream: Le stream en entrée,
	*  - outputStream: Le stream en sortie,
	*  - progressInterface: Une référence sur un objet UpdateProgressInterface 
	*    permettant d'indiquer la progression de la copie.
	* 
	* Lève: Exception.
	* ----------------------------------------------------------*/
	protected void copyStream(
		InputStream inputStream,
		OutputStream outputStream,
		UpdateProgressInterface progressInterface
		)
		throws
			Exception
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AnyFileProcessor", "copyStream");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		int current_bytes = 0;
		int total_bytes = 0;
		byte[] data = new byte[4 * 1024];
			
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("inputStream=" + inputStream);
		trace_arguments.writeTrace("outputStream=" + outputStream);
		trace_arguments.writeTrace("progressInterface=" + progressInterface);
		// On vérifie la validité des arguments
		if(inputStream == null || outputStream == null || 
			progressInterface == null)
		{
			trace_errors.writeTrace(
				"Au moins des arguments n'est pas valide !");
			// On va lever une exception
			trace_methods.endOfMethod();
			throw new Exception(
				MessageManager.getMessage("&ERR_InvalidArgument"));
		}
		// On va récupérer la taille du stream
		total_bytes = inputStream.available();
		// On va copier par blocks de 4 Ko
		while(true)
		{
			int n_read = inputStream.read(data);
			trace_debug.writeTrace("n_read=" + n_read);
			if(n_read <= 0)
			{
				// On est arrivé à la fin du fichier, on sort
				break;
			}
			outputStream.write(data, 0, n_read);
			// On calcule le taux de progression
			current_bytes += n_read;
			int progress = (100 * current_bytes) / total_bytes;
			// On met à jour la progression
			progressInterface.updateProgress(null, null, progress, -1);
		}
		progressInterface.updateProgress(null, null, 100, -1);
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
}
