/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/update/EntryProcessorFactory.java,v $
* $Revision: 1.1 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de fabrication d'objets de traitement
* DATE:        26/10/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.update
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: EntryProcessorFactory.java,v $
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

//
//Imports du projet
//

/*----------------------------------------------------------
* Nom: EntryProcessorFactory
* 
* Description:
* Cette classe abstraite est une classe technique charg�e de la construction 
* d'objets charg�s du traitement des entr�es du fichier de d�finition de la 
* mise � jour. Suivant le type d'entr�e, un objet de nature diff�rente peut 
* �tre instanci�.
* Cette classe ne dispose que d'une seule m�thode statique: la m�thode 
* getProcessorForEntry().
* ----------------------------------------------------------*/
abstract class EntryProcessorFactory
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getProcessorForEntry
	* 
	* Description:
	* Cette m�thode statique permet de r�cup�rer un objet 
	* EntryProcessorInterface charg� du traitement de l'entr�e dont le nom 
	* est pass� en argument.
	* Si l'entr�e est :
	*  - Un fichier zip (extension .zip), un objet ZipFileProcessor sera 
	*    instanci� et retourn�,
	*  - Un fichier de patch de fichier de configuration (extension 
	*    .conf_patch), un objet ConfigFileProcessor sera instanci� et retourn�,
	*  - Un fichier de patch de fichier lax (extension .lax_patch), un objet 
	*    LaxFileProcessor sera instanci� et retourn�,
	*  - Dans les autres cas, un objet AnyFileProcessor sera instanci� et 
	*    retourn�.
	* 
	* Arguments:
	*  - entryName: Le nom de l'entr�e pour laquelle il faut r�cup�rer un 
	*    objet de traitement.
	* 
	* Retourne: Une r�f�rence sur un objet EntryProcessorInterface charg� du 
	* traitement de l'entr�e.
	* ----------------------------------------------------------*/
	public static EntryProcessorInterface getProcessorForEntry(
		String entryName
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EntryProcessorFactory", "getProcessorForEntry");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceArguments("Console");
		EntryProcessorInterface processor_interface = null;
		int dot_position = 0;
		String entry_extention = null;
		
		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("entryName=" + entryName);
		// On v�rifie la validit� de l'argument
		if(entryName == null || entryName.equals("") == true)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return processor_interface;
		}
		// On va r�cup�rer l'extension du fichier
		dot_position = entryName.lastIndexOf('.');
		trace_debug.writeTrace("dot_position=" + dot_position);
		if(dot_position > -1)
		{
			entry_extention = entryName.substring(dot_position + 1);
			trace_debug.writeTrace("entry_extention=" + entry_extention);
			// On va instancier l'objet de traitement en fonction de
			// l'extension
			if(entry_extention.equalsIgnoreCase("zip") == true)
			{
				trace_debug.writeTrace("C'est une archive ZIP");
				// On va instancier un objet ZipFileProcessor
				processor_interface = new ZipFileProcessor();
			}
			else if(entry_extention.equalsIgnoreCase("ini_patch") == true)
			{
				trace_debug.writeTrace(
					"C'est un patch de fichier de configuration");
				// On va instancier un objet ConfigFileProcessor
				processor_interface = new ConfigFileProcessor();
			}
			else if(entry_extention.equalsIgnoreCase("lax_patch") == true)
			{
				trace_debug.writeTrace("C'est un patch de fichier LAX");
				// On va instancier un objet LaxFileProcessor
				processor_interface = new LaxFileProcessor();
			}
			// Dans les autres cas, on ne fait rien pour instancier un objet
			// de traitement par d�faut
		}
		if(processor_interface == null)
		{
			trace_debug.writeTrace(
				"Cr�ation d'un objet de traitement par d�faut");
			// On va instancier un objet AnyFileProcessor
			processor_interface = new AnyFileProcessor();
		}
		trace_methods.endOfMethod();
		return processor_interface;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
