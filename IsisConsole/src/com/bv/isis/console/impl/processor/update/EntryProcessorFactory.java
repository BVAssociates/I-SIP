/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
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
// Déclaration du package
package com.bv.isis.console.impl.processor.update;

//
// Imports système
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
* Cette classe abstraite est une classe technique chargée de la construction 
* d'objets chargés du traitement des entrées du fichier de définition de la 
* mise à jour. Suivant le type d'entrée, un objet de nature différente peut 
* être instancié.
* Cette classe ne dispose que d'une seule méthode statique: la méthode 
* getProcessorForEntry().
* ----------------------------------------------------------*/
abstract class EntryProcessorFactory
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getProcessorForEntry
	* 
	* Description:
	* Cette méthode statique permet de récupérer un objet 
	* EntryProcessorInterface chargé du traitement de l'entrée dont le nom 
	* est passé en argument.
	* Si l'entrée est :
	*  - Un fichier zip (extension .zip), un objet ZipFileProcessor sera 
	*    instancié et retourné,
	*  - Un fichier de patch de fichier de configuration (extension 
	*    .conf_patch), un objet ConfigFileProcessor sera instancié et retourné,
	*  - Un fichier de patch de fichier lax (extension .lax_patch), un objet 
	*    LaxFileProcessor sera instancié et retourné,
	*  - Dans les autres cas, un objet AnyFileProcessor sera instancié et 
	*    retourné.
	* 
	* Arguments:
	*  - entryName: Le nom de l'entrée pour laquelle il faut récupérer un 
	*    objet de traitement.
	* 
	* Retourne: Une référence sur un objet EntryProcessorInterface chargé du 
	* traitement de l'entrée.
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
		// On vérifie la validité de l'argument
		if(entryName == null || entryName.equals("") == true)
		{
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return processor_interface;
		}
		// On va récupérer l'extension du fichier
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
			// de traitement par défaut
		}
		if(processor_interface == null)
		{
			trace_debug.writeTrace(
				"Création d'un objet de traitement par défaut");
			// On va instancier un objet AnyFileProcessor
			processor_interface = new AnyFileProcessor();
		}
		trace_methods.endOfMethod();
		return processor_interface;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
