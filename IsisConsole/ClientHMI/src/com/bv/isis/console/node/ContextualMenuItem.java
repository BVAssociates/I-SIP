/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/ContextualMenuItem.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'élément de menu contextuel
* DATE:        15/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ContextualMenuItem.java,v $
* Revision 1.5  2009/01/23 17:28:22  tz
* Ajout de la notion d'élément de menu correspondant à une méthode
* d'exploitation.
*
* Revision 1.4  2009/01/14 14:22:25  tz
* Prise en compte de la modification des packages.
*
* Revision 1.3  2005/07/01 12:08:36  tz
* Modification du composant pour les traces
*
* Revision 1.2  2004/11/09 15:23:18  tz
* Modification pour la gestion du rafraîchissement des menus contextuels.
*
* Revision 1.1  2004/10/22 15:36:47  tz
* Externalisation de la classe MenuFactory.IsisMenuItem.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.node;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.corbacom.IsisMethod;

/*----------------------------------------------------------
* Nom: ContextualMenuItem
* 
* Description:
* Cette classe est une spécialisation de la classe JMenuItem permettant de 
* redéfinir le comportement de l'élément de menu en cas de click dessus 
* (méthode fireActionPerformed()).
----------------------------------------------------------*/
public class ContextualMenuItem 
	extends JMenuItem
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	Nom: ContextualMenuItem
	* 
	* Description:
	* Cette méthode est un constructeur de la classe. Elle permet de 
	* définir le libellé de l'élément de menu et de lui associer la 
	* méthode qui a été utilisée pour le créer.
	* 
	* Arguments:
	*  - label: Le libellé de l'élément de menu,
	*  - associatedMethod: La méthode associée,
	*  - isMethodItem: Un booléen indiquant si l'élément de menu 
	*    correspond à une méthode d'exploitation.
   	----------------------------------------------------------*/
	public ContextualMenuItem(
		String label,
		IsisMethod associatedMethod,
		boolean isMethodItem
		)
	{
		super(label);
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "ContextualMenuItem");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("label=" + label);
		trace_arguments.writeTrace("associatedMethod=" + associatedMethod);
		trace_arguments.writeTrace("isMethodItem=" + isMethodItem);
		_associatedMethod = associatedMethod;
		_attachedProcessor = null;
		_isMethodItem = isMethodItem;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: fireActionPerformed
	* 
	* Description:
	* Cette méthode appelée par la méthode doAutomaticExplore() afin de 
	* s'assurer que l'action est exécutée en synchronisé, et non pas en 
	* parallèle.
	----------------------------------------------------------*/
	public void fireActionPerformed()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "fireActionPerformed");

		trace_methods.beginningOfMethod();
		super.fireActionPerformed(new ActionEvent(this, 0,
			"synchronize"));
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: attachProcessor
	* 
	* Description:
	* Cette méthode permet d'associer un processeur à l'élément de menu. 
	* L'association doit être effectuée au démarrage de l'exécution du 
	* processeur.
	* Une fois l'exécution du processeur terminée, celui-ci devrait être 
	* désassocié de l'élément de menu, via la méthode detachProcessor().
	* 
	* Arguments:
	*  - processorToAttach: Une référence sur l'interface ProcessorInterface 
	* du processeur à associer à l'élément de menu.
 	----------------------------------------------------------*/
 	public void attachProcessor(
 		ProcessorInterface processorToAttach
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "attachProcessor");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("processorToAttach=" + processorToAttach);
		_attachedProcessor = processorToAttach;
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: detachProcessor
	* 
	* Description:
	* Cette méthode permet de désassocier un processeur de l'élément de menu. 
	* Elle devrait être appelée à la fin de l'exécution du processeur.
	----------------------------------------------------------*/
	public void detachProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "detachProcessor");

		trace_methods.beginningOfMethod();
		_attachedProcessor = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: hasAttachedProcessor
	* 
	* Description:
	* Cette méthode permet de savoir si un processeur est attaché à l'élément 
	* de menu. 
	* 
	* Retourne: true si un processeur est attaché, false sinon.
	----------------------------------------------------------*/
	public boolean hasAttachedProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "hasAttachedProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return (_attachedProcessor != null);
	}

	/*----------------------------------------------------------
	* Nom: closeAttachedProcessor
	* 
	* Description:
	* Cette méthode permet de fermer le processeur associé à l'élément de menu. 
	* Elle est destinée à être appelée lorsqu'un noeud est détruit, afin de 
	* fermer toutes les fenêtres associées à ce noeud.
	* Elle appelle la méthode close() du processeur associé.
	----------------------------------------------------------*/
	public void closeAttachedProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "closeAttachedProcessor");

		trace_methods.beginningOfMethod();
		// On appelle la méthode close() et on détache le processeur
		if(_attachedProcessor != null)
		{
			_attachedProcessor.close();
			_attachedProcessor = null;
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isSameMethod
	* 
	* Description:
	* Cette méthode est appelée pour savoir si la méthode de l'élément de menu 
	* passé en argument est la même que celle qui a été initialement utilisée 
	* pour créer l'élément de menu.
	* La comparaison est faite sur un certain nombre de points, à savoir:
	*  - Le type de noeud concerné,
	*  - Le nom du processeur,
	*  - Les arguments d'exécution du processeur.
	* 
	* Arguments:
	*  - menuItem: L'élément de menu dont la méthode est à comparer avec la 
	*    méthode associée à l'élément de menu.
	* 
	* Retourne: true si la méthode de l'élément de menu passé en argument est 
	* apparentée à la méthode associée, false sinon.
	----------------------------------------------------------*/
	public boolean isSameMethod(
		ContextualMenuItem menuItem
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "isSameMethod");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menuItem=" + menuItem);
		// Si l'argument est null, on retourne false
		if(menuItem == null || menuItem._associatedMethod == null ||
			_associatedMethod == null)
		{
			trace_methods.endOfMethod();
			return false;
		}
		IsisMethod method = menuItem._associatedMethod;
		trace_methods.endOfMethod();
		// On compare le type de noeud, le nom du processeur et les
		// arguments
		return (_associatedMethod.nodeType.equals(method.nodeType) &&
			_associatedMethod.processor.equals(method.processor) &&
			_associatedMethod.arguments.equals(method.arguments));
	}

	/*----------------------------------------------------------
	* Nom: isSameCondition
	* 
	* Description:
	* Cette méthode est appelée pour savoir si l'attribut condition de la 
	* méthode passé de l'élément de menu passé en argument est le même que 
	* celui de la méthode qui a été initialement utilisée pour créer 
	* l'élément de menu.
	* 
	* Arguments:
	*  - menuItem: L'élément de menu dont la méthode est à utiliser pour la 
	*    comparaison.
	* 
	* Retourne: true si les deux attributs condition sont identiques, false 
	* sinon.
	----------------------------------------------------------*/
	public boolean isSameCondition(
		ContextualMenuItem menuItem
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "isSameCondition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menuItem=" + menuItem);
		// Si l'argument est null, on retourne false
		if(menuItem == null || menuItem._associatedMethod == null ||
			_associatedMethod == null)
		{
			trace_methods.endOfMethod();
			return false;
		}
		IsisMethod method = menuItem._associatedMethod;
		trace_methods.endOfMethod();
		// On compare le type de noeud, le nom du processeur et les
		// arguments
		return (_associatedMethod.condition == method.condition);
	}

	/*----------------------------------------------------------
	* Nom: setCondition
	* 
	* Description:
	* Cette méthode permet de définir la nouvelle valeur de la condition de la 
	* méthode à partir de celle passée en argument.
	* 
	* Arguments:
	*  - newCondition: La nouvelle valeur de la condition.
 	----------------------------------------------------------*/
 	public void setCondition(
 		boolean newCondition
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "setCondition");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("newCondition=" + newCondition);
		if(_associatedMethod != null)
		{
			_associatedMethod.condition = newCondition;
		}
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: isMethodItem
	* 
	* Description:
	* Cette méthode permet de savoir si l'élément de menu correspond à 
	* une méthode d'exploitation ou non.
	* 
	* Retourne: true si l'élément correspond à une méthode d'exploitation, 
	* false sinon.
 	----------------------------------------------------------*/
 	public boolean isMethodItem()
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "isMethodItem");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _isMethodItem;
 	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: finalize
	* 
	* Description:
	* Cette méthode est appelée automatiquement par le ramasse-miettes de la 
	* machine virtuelle Java lorsque un objet est sur le point d'être détruit.
	* Elle libère les ressources allouées par l'objet.
	----------------------------------------------------------*/
	protected void finalize()
		throws Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "finalize");

		trace_methods.beginningOfMethod();
		_associatedMethod = null;
		_attachedProcessor = null;
		trace_methods.endOfMethod();
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _attachedProcessor
	* 
	* Description:
	* Cet attribut maintient une référence sur un objet ProcessorInterface 
	* correspondant au processeur associé à ce noeud.
	----------------------------------------------------------*/
	private ProcessorInterface _attachedProcessor;

	/*----------------------------------------------------------
	* Nom: _associatedMethod
	* 
	* Description:
	* Cette méthode maintient une référence sur un objet IsisMethod 
	* correspondant à la méthode associée à l'élément de menu.
	----------------------------------------------------------*/
	private IsisMethod _associatedMethod;

	/*----------------------------------------------------------
	* Nom: _isMethodItem
	* 
	* Description:
	* Cet attribut maintient un booléen indiquant si l'élément de menu 
	* correspond à une méthode d'exploitation (true) ou non (false).
	----------------------------------------------------------*/
	private boolean _isMethodItem;
}
