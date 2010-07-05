/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/close/CloseProcessor.java,v $
* $Revision: 1.13 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de fermeture de noeud
* DATE:        17/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.close
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: CloseProcessor.java,v $
* Revision 1.13  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.12  2008/01/31 16:46:55  tz
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.11  2006/03/07 09:31:12  tz
* Mise � jour par rapport � l'�tat du noeud.
*
* Revision 1.10  2005/10/07 08:31:01  tz
* Changement mineur.
*
* Revision 1.9  2005/07/01 12:20:16  tz
* Modification du composant pour les traces
*
* Revision 1.8  2004/11/02 08:56:47  tz
* Gestion de l'�tat du noeud.
*
* Revision 1.7  2004/10/22 15:40:29  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.5  2004/10/13 13:56:53  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.4  2004/07/29 12:16:35  tz
* Suppression d'imports inutiles
*
* Revision 1.3  2002/04/05 15:50:16  tz
* Cloture it�ration IT1.2
*
* Revision 1.2  2002/03/27 09:51:20  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.1  2001/12/19 09:58:35  tz
* Cloture it�ration IT1.0.0
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.close;

//
// Imports syst�me
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import com.bv.core.message.MessageManager;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.MenuFactory;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;

/*----------------------------------------------------------
* Nom: CloseProcessor
*
* Description:
* Cette classe impl�mente le processeur de t�che ex�cut� lorsque un noeud
* graphique est ferm�.
* La fermeture d'un noeud signifie que la portion d'arbre partant de ce noeud
* est r�duite graphiquement, et que tous les noeuds enfants sont d�truits. La
* destruction des noeuds fils est effectu�e via la m�thode close() du noeud �
* fermer.
* ----------------------------------------------------------*/
public class CloseProcessor
	implements ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: CloseProcessor
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sente que
	* pour des raisons de lisibilit�.
	* ----------------------------------------------------------*/
	public CloseProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "CloseProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e lors de l'initialisation et de l'ex�cution du processeur.
	* Elle va appeler la m�thode close() sur le noeud s�lectionn�, et informer
	* le mod�les des donn�es de l'arbre de la suppression des noeuds.
	*
	* Si une erreur est d�tect�e pendant la phase d'initialisation, l'exception
	* InnerException doit �tre lev�e.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface
	*    permettant au processeur de communiquer avec la fen�tre principale de
	*    l'application. Cet argument ne doit pas �tre nul,
	*  - menuItem: Une r�f�rence sur l'option de menu qui a d�clench�
	*    l'ex�cution du processeur de t�che. Cet attribut peut �tre nul,
	*  - parameters: Une cha�ne de caract�res contenant les param�tres
	*    d'ex�cution du processeur. Cet attribut peut �tre nul,
	*  - preprocessing: Une cha�ne contenant des instructions de pr�processing,
	*  - postprocessing: Une cha�ne contenant des instructions de postprocessing,
	*  - selectedNode: Une r�f�rence sur l'objet graphique sur lequel le
	*    processeur doit ex�cuter son traitement. Cet attribut peut �tre nul.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public void run(
		MainWindowInterface windowInterface,
		JMenuItem menuItem,
		String parameters,
		String preprocessing,
		String postprocessing,
		DefaultMutableTreeNode selectedNode
		)
		throws
			InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Tout d'abord, on v�rifie l'int�grit� des param�tres
		if(windowInterface == null || selectedNode == null ||
		   !(selectedNode instanceof GenericTreeObjectNode))
		{
			trace_errors.writeTrace("Un des arguments n'est pas valide !");
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On ferme le noeud
		GenericTreeObjectNode selected_node =
			(GenericTreeObjectNode)selectedNode;
		// On v�rifie l'�tat du noeud
		if(selected_node.getNodeState() == 
			GenericTreeObjectNode.NodeStateEnum.OPENED)
		{
			selected_node.close(false);
			// On force la mise � jour des �tats du menu
			MenuFactory.updateMenuItemsState(selected_node);
			// On informe l'arbre de la fermeture du noeud
			windowInterface.getTreeInterface().nodeStructureChanged(selected_node);
		}
		else
		{
			trace_errors.writeTrace("Le noeud n'est pas dans le bon �tat");
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette m�thode red�fini celle de l'interface ProcessorInterface. Elle est
	* appel�e lorsque l'ex�cution du processeur doit �tre arr�t�e.
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "close");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour effectuer le pr�-chargement du processeur.
	* Pour ce processeur, la m�thode ne fait rien.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "preLoad");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur a �t� configur� ou s'il a 
	* besoin d'une configuration.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isConfigured()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "isConfigured");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer les panneaux de configuration du processeur.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "getConfigurationPanels");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un noeud 
	* d'exploration.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* possible.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "isTreeCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un �l�ment 
	* d'un tableau.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* possible.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "isTableCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� hors d'un 
	* noeud d'exploration ou d'un �l�ment d'un tableau.
	* Pour ce processeur, seule l'invocation via un noeud d'exploration est 
	* possible.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "isGlobalCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer la description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&CloseProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer l'intitul� de l'�l�ment de menu associ�.
	* Ce processeur n'�tant pas global, cette m�thode ne sera pas appel�e.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "getMenuLabel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
	* 
	* Retourne: Une nouvelle instance du processeur.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"CloseProcessor", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new CloseProcessor();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}