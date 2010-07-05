/*------------------------------------------------------------
* Copyright (c) 2006 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/edition/ScriptEditionProcessor.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'�dition de scripts
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
* Ajout de la m�thode getTitlePrefix().
*
* Revision 1.1  2006/11/03 10:30:40  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.edition;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur charg� de l'�dition de scripts. Pour 
* cela, elle �tend la classe FileEditionProcessor, car la plupart des 
* fonctionnalit�s n�cessaires sont d�j� int�gr�es dans celle-ci.
* Les diff�rences avec l'�dition d'un fichier ordinaire sont :
*  - Le script, s'il est cr��, doit �tre positionn� en tant qu'ex�cutable,
*  - Si le script n'est r�f�renc� que sous la forme d'un nom, sans notion de 
*    chemin, il doit �tre recherch� dans la liste des chemins des ex�cutables 
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
	* Constructeur par d�faut. Il n'est pr�sent� que pour des raisons de 
	* lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe FileEditionProcessor. 
	* Elle permet de r�cup�rer le d�but de la cha�ne composant le titre de la 
	* sous-fen�tre.
	* Dans le cas pr�sent, elle retourne "Edition de script:".
	* 
	* Retourne: Le d�but de la cha�ne de titre.
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
	* Cette m�thode red�finit celle de la super-classe FileEditionProcessor. 
	* Elle est appel�e afin de r�cup�rer une description du processeur.
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
	* Cette m�thode red�finit celle de la super-classe FileEditionProcessor. 
	* Elle permet de retourner un objet �tant un double de l'objet courant.
	* 
	* Retourne: Une r�f�rence sur un nouvel objet ScriptEditionProcessor.
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
	* Cette m�thode red�finit celle de la super-classe FileEditionProcessor. 
	* Elle est charg�e de g�rer la r�f�rence au script devant �tre �dit�, 
	* celle-ci �tant pass�e en param�tre.
	* Dans le cas pr�sent, si la r�f�rence ne correspond qu'� un nom de 
	* script, sans chemin, le chemin du fichier est recherch� via la m�thode 
	* getExecutableFilePath() de la classe ServiceSessionProxy.
	* 
	* Si une erreur survient lors de la gestion de la r�f�rence, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - fileReference: La r�f�rence au script devant �tre �dit�.
	* 
	* Retourne: La r�f�rence au script devant �tre �dit�.
	* 
	* L�ve: InnerException.
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
		// On va commencer par v�rifier que l'argument est valide
		file_path = super.manageFileReference(fileReference);
		// On va maintenant regarder si le script est r�f�renc� via un
		// chemin ou non. Pour cela, on va regarder s'il la r�f�rence contient
		// le caract�re '/' ou le caract�re '\'
		if(fileReference.indexOf('/') == -1 &&
			fileReference.indexOf('\\') == -1)
		{
			GenericTreeObjectNode selected_node = null;
			ServiceSessionProxy session_proxy = null;

			trace_debug.writeTrace("Le script est r�f�renc� uniquement via " +
				"son nom, lancement de la recherche");
			// Le script n'est r�f�renc� que via son nom, on va lancer une 
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
