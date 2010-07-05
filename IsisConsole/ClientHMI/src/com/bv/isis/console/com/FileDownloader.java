/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/com/FileDownloader.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire de t�l�chargement de fichier
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
* T�l�chargement par blocs de 50 Ko.
*
* Revision 1.5  2006/11/03 10:26:02  tz
* Modification du groupe.
*
* Revision 1.4  2005/12/23 13:16:14  tz
* D�placement de la classe depuis le package
* com.bv.isis.console.impl.processor.update.
*
* Revision 1.3  2005/10/07 08:28:29  tz
* Gestion de la taille maximale du t�l�chargement.
*
* Revision 1.2  2005/07/06 10:07:51  tz
* Ajout d'une valeur de retour � la m�thode downloadFile().
*
* Revision 1.1  2005/07/05 15:09:08  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.com;

//
// Imports syst�me
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
* Cette classe est une classe technique charg�e d'effectuer le t�l�chargement 
* d'un fichier distant se trouvant sur le Portail ou sur un Agent vers un 
* fichier local.
* Le t�l�chargement est int�gralement g�r� par le biais de la m�thode 
* download().
* ----------------------------------------------------------*/
public abstract class FileDownloader
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: downloadFile
	* 
	* Description:
	* Cette m�thode est charg�e d'effectuer le t�l�chargement d'un fichier 
	* distant dans un fichier local.
	* La r�cup�ration du contenu du fichier distant est effectu�e par le biais 
	* de l'interface de lecture pass�e en argument. Les donn�es sont 
	* enregistr�es dans un fichier local repr�sent� par le deuxi�me argument.
	* 
	* Si un probl�me est d�tect� pendant le t�l�chargement, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - fileReader: Une r�f�rence sur un objet FileReaderInterface permettant 
	*    la lecture du contenu du fichier distant,
	*  - maximumSize: La taille maximale du fichier � t�l�charger,
	*  - localFile: Le fichier local destin� � recevoir le contenu du fichier 
	*    distant,
	*  - progressInterface: Une r�f�rence sur une interface 
	*    DownloadProgressInterface permettant de suivre la progression du 
	*    t�l�chargement.
	*
	* Retourne: -1 si le fichier n'a pas �t� t�l�charg� car il a une taille 
	* nulle, 1 si le fichier n'a pas �t� t�l�charg� car il a une taille 
	* sup�rieure � la limite, et 0 si le fichier a �t� t�l�charg�. 
	* 
	* L�ve: InnerException.
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
		// On v�rifie la validit� des arguments
		if(fileReader == null || localFile == null)
		{
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On l�ve une exception
			throw new InnerException("&ERR_CannotDownloadFile",
				"&ERR_InvalidArgument", null);
		}
		trace_debug.writeTrace("localFile=" + localFile.getAbsolutePath());
		try
		{
			// On commence par r�cup�rer la taille du fichier
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
			// On va ouvrir le fichier distant et cr�er un objet FileWriter
			// sur le fichier local
			fileReader.open();
			output_stream = new FileOutputStream(localFile, false);
			// On va t�l�charger le fichier par blocs
			while(current_size < total_file_size)
			{
				// Par d�faut, on t�l�charge par blocs de 50 Ko
				download_size = 50 * 1024;
				// On corrige la valeur si n�cessaire
				if((total_file_size - current_size) < download_size)
				{
					download_size = total_file_size - current_size;
				}
				trace_debug.writeTrace("current_size=" + current_size);
				trace_debug.writeTrace("download_size=" + download_size);
				// On r�cup�re un ensemble de ShortBlock
				ShortBlock[] blocks = 
					fileReader.readBlock(current_size, download_size);
				// On va �crire les blocs dans le fichier local
				writeBlocks(output_stream, blocks);
				// On incr�mente les compteurs
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
			// On a termin� le t�l�chargement, on peut fermer les fichiers
			fileReader.close();
			output_stream.close();
		}
		catch(Exception exception)
		{
			// On va construire une exception
			InnerException inner_exception = 
				CommonFeatures.processException("du t�l�chargement " +
				"du fichier " + localFile.getAbsolutePath(), exception);
			// On va fermer les fichiers, au cas o�
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
			// On l�ve l'exception
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
	* Cette m�thode est charg�e d'effectuer l'�criture du tableau de 
	* ShortBlock pass� en argument via l'objet FileOutputStream �galement 
	* pass� en argument.
	* 
	* Si un probl�me survient lors de l'�criture des blocs, l'exception 
	* Exception est lev�e.
	* 
	* Arguments:
	*  - outputStream: Une r�f�rence sur un objet FileOutputStream utilis� 
	*    pour �crire dans le fichier local,
	*  - blocks: Un tableau de ShortBlock � �crire dans le fichier.
	* 
	* L�ve: Exception.
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
		// On v�rifie la validit� des arguments
		if(outputStream == null || blocks == null)
		{
			trace_errors.writeTrace("Au moins un des arguments n'est pas " +
				"valide !");
			// On l�ve une exception
			throw new Exception(MessageManager.getMessage(
				"&ERR_InvalidArgument"));
		}
		// On va �crire bloc par bloc
		for(int index = 0 ; index < blocks.length ; index ++)
		{
			// On �crit le bloc dans le fichier
			outputStream.write(blocks[index].data, 0, blocks[index].size);
			outputStream.flush();
		}
		trace_debug.writeTrace("Nombre de blocs �crits: " + blocks.length);
		trace_methods.endOfMethod();
	}
}
