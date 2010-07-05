/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/edition/ScriptEditionProcessor.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'édition de scripts
* DATE:        25/10/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.edition
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ScriptEditionProcessor.java,v $
* Revision 1.3  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.2  2006/11/09 12:13:02  tz
* Ajout de la méthode getTitlePrefix().
*
* Revision 1.1  2006/11/03 10:30:40  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.edition;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.InnerException;

/*----------------------------------------------------------
* Nom: ScriptEditionProcessor
* 
* Description:
* Cette classe implémente le processeur chargé de l'édition de scripts. Pour 
* cela, elle étend la classe FileEditionProcessor, car la plupart des 
* fonctionnalités nécessaires sont déjà intégrées dans celle-ci.
* Les différences avec l'édition d'un fichier ordinaire sont :
*  - Le script, s'il est créé, doit être positionné en tant qu'exécutable,
*  - Si le script n'est référencé que sous la forme d'un nom, sans notion de 
*    chemin, il doit être recherché dans la liste des chemins des exécutables 
*    de la plate-forme Agent.
* ----------------------------------------------------------*/
public class ScriptEditionProcessor
	extends FileEditionProcessor
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ScriptEditionProcessor
	* 
	* Description:
	* Constructeur par défaut. Il n'est présenté que pour des raisons de 
	* lisibilité.
	* ----------------------------------------------------------*/
	public ScriptEditionProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ScriptEditionProcessor", "ScriptEditionProcessor");

		trace_methods.beginningOfMethod();
		setFileAsExecutable(true);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getTitlePrefix
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe FileEditionProcessor. 
	* Elle permet de récupérer le début de la chaîne composant le titre de la 
	* sous-fenêtre.
	* Dans le cas présent, elle retourne "Edition de script:".
	* 
	* Retourne: Le début de la chaîne de titre.
	* ----------------------------------------------------------*/
	public String getTitlePrefix()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ScriptEditionProcessor", "getTitlePrefix");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&EditScript_Title");
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe FileEditionProcessor. 
	* Elle est appelée afin de récupérer une description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ScriptEditionProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&ScriptEditionProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe FileEditionProcessor. 
	* Elle permet de retourner un objet étant un double de l'objet courant.
	* 
	* Retourne: Une référence sur un nouvel objet ScriptEditionProcessor.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ScriptEditionProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new ScriptEditionProcessor();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: manageFileReference
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe FileEditionProcessor. 
	* Elle est chargée de gérer la référence au script devant être édité, 
	* celle-ci étant passée en paramètre.
	* Dans le cas présent, si la référence ne correspond qu'à un nom de 
	* script, sans chemin, le chemin du fichier est recherché via la méthode 
	* getExecutableFilePath() de la classe ServiceSessionProxy.
	* 
	* Si une erreur survient lors de la gestion de la référence, l'exception 
	* InnerException est levée.
	* 
	* Arguments:
	*  - fileReference: La référence au script devant être édité.
	* 
	* Retourne: La référence au script devant être édité.
	* 
	* Lève: InnerException.
	* ----------------------------------------------------------*/
	protected String manageFileReference(
		String fileReference
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ScriptEditionProcessor", "manageFileReference");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String file_path = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("fileReference=" + fileReference);
		// On va commencer par vérifier que l'argument est valide
		file_path = super.manageFileReference(fileReference);
		// On va maintenant regarder si le script est référencé via un
		// chemin ou non. Pour cela, on va regarder s'il la référence contient
		// le caractère '/' ou le caractère '\'
		if(fileReference.indexOf('/') == -1 &&
			fileReference.indexOf('\\') == -1)
		{
			GenericTreeObjectNode selected_node = null;
			ServiceSessionProxy session_proxy = null;

			trace_debug.writeTrace("Le script est référencé uniquement via " +
				"son nom, lancement de la recherche");
			// Le script n'est référencé que via son nom, on va lancer une 
			// recherche au niveau de l'Agent
			selected_node = (GenericTreeObjectNode)getSelectedNode();
			session_proxy = 
				new ServiceSessionProxy(selected_node.getServiceSession());
			// On lance la recherche
			file_path = session_proxy.getExecutableFilePath(
					fileReference, selected_node.getContext(true));
		}
		trace_methods.endOfMethod();
		return file_path;
	}

	// ******************* PRIVE **********************
}
