/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/node/ContextualMenuItem.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'�l�ment de menu contextuel
* DATE:        15/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      node
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ContextualMenuItem.java,v $
* Revision 1.5  2009/01/23 17:28:22  tz
* Ajout de la notion d'�l�ment de menu correspondant � une m�thode
* d'exploitation.
*
* Revision 1.4  2009/01/14 14:22:25  tz
* Prise en compte de la modification des packages.
*
* Revision 1.3  2005/07/01 12:08:36  tz
* Modification du composant pour les traces
*
* Revision 1.2  2004/11/09 15:23:18  tz
* Modification pour la gestion du rafra�chissement des menus contextuels.
*
* Revision 1.1  2004/10/22 15:36:47  tz
* Externalisation de la classe MenuFactory.IsisMenuItem.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.node;

//
// Imports syst�me
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
* Cette classe est une sp�cialisation de la classe JMenuItem permettant de 
* red�finir le comportement de l'�l�ment de menu en cas de click dessus 
* (m�thode fireActionPerformed()).
----------------------------------------------------------*/
public class ContextualMenuItem 
	extends JMenuItem
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	Nom: ContextualMenuItem
	* 
	* Description:
	* Cette m�thode est un constructeur de la classe. Elle permet de 
	* d�finir le libell� de l'�l�ment de menu et de lui associer la 
	* m�thode qui a �t� utilis�e pour le cr�er.
	* 
	* Arguments:
	*  - label: Le libell� de l'�l�ment de menu,
	*  - associatedMethod: La m�thode associ�e,
	*  - isMethodItem: Un bool�en indiquant si l'�l�ment de menu 
	*    correspond � une m�thode d'exploitation.
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
	* Cette m�thode appel�e par la m�thode doAutomaticExplore() afin de 
	* s'assurer que l'action est ex�cut�e en synchronis�, et non pas en 
	* parall�le.
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
	* Cette m�thode permet d'associer un processeur � l'�l�ment de menu. 
	* L'association doit �tre effectu�e au d�marrage de l'ex�cution du 
	* processeur.
	* Une fois l'ex�cution du processeur termin�e, celui-ci devrait �tre 
	* d�sassoci� de l'�l�ment de menu, via la m�thode detachProcessor().
	* 
	* Arguments:
	*  - processorToAttach: Une r�f�rence sur l'interface ProcessorInterface 
	* du processeur � associer � l'�l�ment de menu.
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
	* Cette m�thode permet de d�sassocier un processeur de l'�l�ment de menu. 
	* Elle devrait �tre appel�e � la fin de l'ex�cution du processeur.
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
	* Cette m�thode permet de savoir si un processeur est attach� � l'�l�ment 
	* de menu. 
	* 
	* Retourne: true si un processeur est attach�, false sinon.
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
	* Cette m�thode permet de fermer le processeur associ� � l'�l�ment de menu. 
	* Elle est destin�e � �tre appel�e lorsqu'un noeud est d�truit, afin de 
	* fermer toutes les fen�tres associ�es � ce noeud.
	* Elle appelle la m�thode close() du processeur associ�.
	----------------------------------------------------------*/
	public void closeAttachedProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ContextualMenuItem", "closeAttachedProcessor");

		trace_methods.beginningOfMethod();
		// On appelle la m�thode close() et on d�tache le processeur
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
	* Cette m�thode est appel�e pour savoir si la m�thode de l'�l�ment de menu 
	* pass� en argument est la m�me que celle qui a �t� initialement utilis�e 
	* pour cr�er l'�l�ment de menu.
	* La comparaison est faite sur un certain nombre de points, � savoir:
	*  - Le type de noeud concern�,
	*  - Le nom du processeur,
	*  - Les arguments d'ex�cution du processeur.
	* 
	* Arguments:
	*  - menuItem: L'�l�ment de menu dont la m�thode est � comparer avec la 
	*    m�thode associ�e � l'�l�ment de menu.
	* 
	* Retourne: true si la m�thode de l'�l�ment de menu pass� en argument est 
	* apparent�e � la m�thode associ�e, false sinon.
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
	* Cette m�thode est appel�e pour savoir si l'attribut condition de la 
	* m�thode pass� de l'�l�ment de menu pass� en argument est le m�me que 
	* celui de la m�thode qui a �t� initialement utilis�e pour cr�er 
	* l'�l�ment de menu.
	* 
	* Arguments:
	*  - menuItem: L'�l�ment de menu dont la m�thode est � utiliser pour la 
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
	* Cette m�thode permet de d�finir la nouvelle valeur de la condition de la 
	* m�thode � partir de celle pass�e en argument.
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
	* Cette m�thode permet de savoir si l'�l�ment de menu correspond � 
	* une m�thode d'exploitation ou non.
	* 
	* Retourne: true si l'�l�ment correspond � une m�thode d'exploitation, 
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
	* Cette m�thode est appel�e automatiquement par le ramasse-miettes de la 
	* machine virtuelle Java lorsque un objet est sur le point d'�tre d�truit.
	* Elle lib�re les ressources allou�es par l'objet.
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
	* Cet attribut maintient une r�f�rence sur un objet ProcessorInterface 
	* correspondant au processeur associ� � ce noeud.
	----------------------------------------------------------*/
	private ProcessorInterface _attachedProcessor;

	/*----------------------------------------------------------
	* Nom: _associatedMethod
	* 
	* Description:
	* Cette m�thode maintient une r�f�rence sur un objet IsisMethod 
	* correspondant � la m�thode associ�e � l'�l�ment de menu.
	----------------------------------------------------------*/
	private IsisMethod _associatedMethod;

	/*----------------------------------------------------------
	* Nom: _isMethodItem
	* 
	* Description:
	* Cet attribut maintient un bool�en indiquant si l'�l�ment de menu 
	* correspond � une m�thode d'exploitation (true) ou non (false).
	----------------------------------------------------------*/
	private boolean _isMethodItem;
}
