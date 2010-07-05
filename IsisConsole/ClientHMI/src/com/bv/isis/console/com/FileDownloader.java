/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/FileDownloader.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire de téléchargement de fichier
* DATE:        04/07/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      com
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: FileDownloader.java,v $
* Revision 1.8  2009/01/14 14:21:58  tz
* Prise en compte de la modification des packages.
*
* Revision 1.7  2008/06/12 15:49:48  tz
* Suppression d'un import inutile.
*
* Revision 1.6  2008/02/19 15:54:29  tz
* Téléchargement par blocs de 50 Ko.
*
* Revision 1.5  2006/11/03 10:26:02  tz
* Modification du groupe.
*
* Revision 1.4  2005/12/23 13:16:14  tz
* Déplacement de la classe depuis le package
* com.bv.isis.console.impl.processor.update.
*
* Revision 1.3  2005/10/07 08:28:29  tz
* Gestion de la taille maximale du téléchargement.
*
* Revision 1.2  2005/07/06 10:07:51  tz
* Ajout d'une valeur de retour à la méthode downloadFile().
*
* Revision 1.1  2005/07/05 15:09:08  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.com;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import java.io.File;
import java.io.FileOutputStream;

//
//Imports du projet
//
import com.bv.isis.corbacom.FileReaderInterface;
import com.bv.isis.corbacom.ShortBlock;
import com.bv.isis.console.core.abs.processor.DownloadProgressInterface;
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: FileDownloader
* 
* Description:
* Cette classe est une classe technique chargée d'effectuer le téléchargement 
* d'un fichier distant se trouvant sur le Portail ou sur un Agent vers un 
* fichier local.
* Le téléchargement est intégralement géré par le biais de la méthode 
* download().
* ----------------------------------------------------------*/
public abstract class FileDownloader
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: downloadFile
	* 
	* Description:
	* Cette méthode est chargée d'effectuer le téléchargement d'un fichier 
	* distant dans un fichier local.
	* La récupération du contenu du fichier distant est effectuée par le biais 
	* de l'interface de lecture passée en argument. Les données sont 
	* enregistrées dans un fichier local représenté par le deuxième argument.
	* 
	* Si un problème est détecté pendant le téléchargement, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - fileReader: Une référence sur un objet FileReaderInterface permettant 
	*    la lecture du contenu du fichier distant,
	*  - maximumSize: La taille maximale du fichier à télécharger,
	*  - localFile: Le fichier local destiné à recevoir le contenu du fichier 
	*    distant,
	*  - progressInterface: Une référence sur une interface 
	*    DownloadProgressInterface permettant de suivre la progression du 
	*    téléchargement.
	*
	* Retourne: -1 si le fichier n'a pas été téléchargé car il a une taille 
	* nulle, 1 si le fichier n'a pas été téléchargé car il a une taille 
	* supérieure à la limite, et 0 si le fichier a été téléchargé. 
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	public static int downloadFile(
		FileReaderInterface fileReader,
		long maximumSize,
		File localFile,
		DownloadProgressInterface progressInterface
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileDownloader", "downloadFile");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		FileOutputStream output_stream = null;
		long total_file_size = 0;
		long current_size = 0;
		long download_size = 0;

		trace_methods.beginningOfMethod();		
		trace_arguments.writeTrace("fileReader=" + fileReader);
		trace_arguments.writeTrace("maximumSize=" + maximumSize);
		trace_arguments.writeTrace("localFile=" + localFile);
		trace_arguments.writeTrace("progressInterface=" + progressInterface);
		// On vérifie la validité des arguments
		if(fileReader == null || localFile == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On lève une exception
			throw new InnerException("&ERR_CannotDownloadFile",
				"&ERR_InvalidArgument", null);
		}
		trace_debug.writeTrace("localFile=" + localFile.getAbsolutePath());
		try
		{
			// On commence par récupérer la taille du fichier
			total_file_size = fileReader.getFileSize();
			trace_debug.writeTrace("total_file_size=" + total_file_size);
			// Si la taille du fichier est nulle, on sort
			if(total_file_size == 0)
			{
				trace_methods.endOfMethod();
				return -1;
			}
			// Si la taille du fichier est trop grande, on sort
			if(total_file_size > maximumSize)
			{
				trace_methods.endOfMethod();
				return 1;
			}
			// On va ouvrir le fichier distant et créer un objet FileWriter
			// sur le fichier local
			fileReader.open();
			output_stream = new FileOutputStream(localFile, false);
			// On va télécharger le fichier par blocs
			while(current_size < total_file_size)
			{
				// Par défaut, on télécharge par blocs de 50 Ko
				download_size = 50 * 1024;
				// On corrige la valeur si nécessaire
				if((total_file_size - current_size) < download_size)
				{
					download_size = total_file_size - current_size;
				}
				trace_debug.writeTrace("current_size=" + current_size);
				trace_debug.writeTrace("download_size=" + download_size);
				// On récupère un ensemble de ShortBlock
				ShortBlock[] blocks = 
					fileReader.readBlock(current_size, download_size);
				// On va écrire les blocs dans le fichier local
				writeBlocks(output_stream, blocks);
				// On incrémente les compteurs
				current_size += download_size;
				// Si l'interface de progression existe, on l'informe de
				// l'avancement
				if(progressInterface != null)
				{
					progressInterface.setProgress(
						(int)((current_size * 100) / total_file_size));
				}
				trace_debug.writeTrace("current_size=" + current_size);
			}
			// On a terminé le téléchargement, on peut fermer les fichiers
			fileReader.close();
			output_stream.close();
		}
		catch(Exception exception)
		{
			// On va construire une exception
			InnerException inner_exception = 
				CommonFeatures.processException("du téléchargement " +
				"du fichier " + localFile.getAbsolutePath(), exception);
			// On va fermer les fichiers, au cas où
			try
			{
				fileReader.close();
				if(output_stream != null)
				{
					output_stream.close();
				}
			}
			catch(Exception e)
			{
				// On ne fait rien
			}
			// On lève l'exception
			trace_methods.endOfMethod();
			throw inner_exception;
		}
		trace_methods.endOfMethod();
		return 0;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: writeBlocks
	* 
	* Description:
	* Cette méthode est chargée d'effectuer l'écriture du tableau de 
	* ShortBlock passé en argument via l'objet FileOutputStream également 
	* passé en argument.
	* 
	* Si un problème survient lors de l'écriture des blocs, l'exception 
	* Exception est levée.
	* 
	* Arguments:
	*  - outputStream: Une référence sur un objet FileOutputStream utilisé 
	*    pour écrire dans le fichier local,
	*  - blocks: Un tableau de ShortBlock à écrire dans le fichier.
	* 
	* Lève: Exception.
	* ----------------------------------------------------------*/
	private static void writeBlocks(
		FileOutputStream outputStream,
		ShortBlock[] blocks
		)
		throws
			Exception
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"FileDownloader", "writeBlocks");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("outputStream=" + outputStream);
		trace_arguments.writeTrace("blocks=" + blocks);
		// On vérifie la validité des arguments
		if(outputStream == null || blocks == null)
		{
			trace_errors.writeTrace("Au moins un des arguments n'est pas " +
				"valide !");
			// On lève une exception
			throw new Exception(MessageManager.getMessage(
				"&ERR_InvalidArgument"));
		}
		// On va écrire bloc par bloc
		for(int index = 0 ; index < blocks.length ; index ++)
		{
			// On écrit le bloc dans le fichier
			outputStream.write(blocks[index].data, 0, blocks[index].size);
			outputStream.flush();
		}
		trace_debug.writeTrace("Nombre de blocs écrits: " + blocks.length);
		trace_methods.endOfMethod();
	}
}
