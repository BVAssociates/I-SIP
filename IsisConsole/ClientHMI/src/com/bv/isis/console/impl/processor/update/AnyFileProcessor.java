/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
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
// D�claration du package
package com.bv.isis.console.impl.processor.update;

//
// Imports syst�me
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
* Cette classe est une classe d'impl�mentation permettant le traitement d'une 
* entr�e quelconque du fichier de configuration des mises � jour.
* Son objectif est de copier le contenu du fichier source vers un fichier 
* destination (voir la m�thode processEntry()).
* Elle impl�mente l'interface EntryProcessorInterface.
* ----------------------------------------------------------*/
class AnyFileProcessor 
	implements EntryProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: AnyFileProcessor
	* 
	* Description:
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface EntryProcessorInterface. 
	* Elle est appel�e pour d�clencher le traitement d'une entr�e du fichier 
	* de configuration des mises � jour.
	* Dans le cas pr�sent (fichier quelconque), la m�thode cr�e ou ouvre le 
	* fichier destination, dont le nom est constitu� � partir du r�pertoire de 
	* destination et du nom de l'entr�e, puis copie le contenu du fichier 
	* source vers le fichier de destination, via la m�thode copyStream().
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
		// On v�rifie la validit� des arguments
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
	* Cette m�thode permet de copier int�gralement le contenu du stream en 
	* entr�e (inputStream) vers le stream en sortie (outputStream).
	* 
	* Si une erreur survient, l'exception Exception est lev�e.
	* 
	* Arguments:
	*  - inputStream: Le stream en entr�e,
	*  - outputStream: Le stream en sortie,
	*  - progressInterface: Une r�f�rence sur un objet UpdateProgressInterface 
	*    permettant d'indiquer la progression de la copie.
	* 
	* L�ve: Exception.
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
		// On v�rifie la validit� des arguments
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
		// On va r�cup�rer la taille du stream
		total_bytes = inputStream.available();
		// On va copier par blocks de 4 Ko
		while(true)
		{
			int n_read = inputStream.read(data);
			trace_debug.writeTrace("n_read=" + n_read);
			if(n_read <= 0)
			{
				// On est arriv� � la fin du fichier, on sort
				break;
			}
			outputStream.write(data, 0, n_read);
			// On calcule le taux de progression
			current_bytes += n_read;
			int progress = (100 * current_bytes) / total_bytes;
			// On met � jour la progression
			progressInterface.updateProgress(null, null, progress, -1);
		}
		progressInterface.updateProgress(null, null, 100, -1);
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
}
