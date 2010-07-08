/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
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
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.11  2006/03/07 09:31:12  tz
* Mise à jour par rapport à l'état du noeud.
*
* Revision 1.10  2005/10/07 08:31:01  tz
* Changement mineur.
*
* Revision 1.9  2005/07/01 12:20:16  tz
* Modification du composant pour les traces
*
* Revision 1.8  2004/11/02 08:56:47  tz
* Gestion de l'état du noeud.
*
* Revision 1.7  2004/10/22 15:40:29  tz
* Adaptation pour la nouvelle interface ProcessorInterface.
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.5  2004/10/13 13:56:53  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2004/07/29 12:16:35  tz
* Suppression d'imports inutiles
*
* Revision 1.3  2002/04/05 15:50:16  tz
* Cloture itération IT1.2
*
* Revision 1.2  2002/03/27 09:51:20  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.1  2001/12/19 09:58:35  tz
* Cloture itération IT1.0.0
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.close;

//
// Imports système
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
* Cette classe implémente le processeur de tâche exécuté lorsque un noeud
* graphique est fermé.
* La fermeture d'un noeud signifie que la portion d'arbre partant de ce noeud
* est réduite graphiquement, et que tous les noeuds enfants sont détruits. La
* destruction des noeuds fils est effectuée via la méthode close() du noeud à
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
	* Cette méthode est le constructeur de la classe. Elle n'est présente que
	* pour des raisons de lisibilité.
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée lors de l'initialisation et de l'exécution du processeur.
	* Elle va appeler la méthode close() sur le noeud sélectionné, et informer
	* le modèles des données de l'arbre de la suppression des noeuds.
	*
	* Si une erreur est détectée pendant la phase d'initialisation, l'exception
	* InnerException doit être levée.
	*
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface
	*    permettant au processeur de communiquer avec la fenêtre principale de
	*    l'application. Cet argument ne doit pas être nul,
	*  - menuItem: Une référence sur l'option de menu qui a déclenché
	*    l'exécution du processeur de tâche. Cet attribut peut être nul,
	*  - parameters: Une chaîne de caractères contenant les paramètres
	*    d'exécution du processeur. Cet attribut peut être nul,
	*  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	*  - selectedNode: Une référence sur l'objet graphique sur lequel le
	*    processeur doit exécuter son traitement. Cet attribut peut être nul.
	*
	* Lève: InnerException.
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
		// Tout d'abord, on vérifie l'intégrité des paramètres
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
		// On vérifie l'état du noeud
		if(selected_node.getNodeState() == 
			GenericTreeObjectNode.NodeStateEnum.OPENED)
		{
			selected_node.close(false);
			// On force la mise à jour des états du menu
			MenuFactory.updateMenuItemsState(selected_node);
			// On informe l'arbre de la fermeture du noeud
			windowInterface.getTreeInterface().nodeStructureChanged(selected_node);
		}
		else
		{
			trace_errors.writeTrace("Le noeud n'est pas dans le bon état");
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette méthode redéfini celle de l'interface ProcessorInterface. Elle est
	* appelée lorsque l'exécution du processeur doit être arrêtée.
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour effectuer le pré-chargement du processeur.
	* Pour ce processeur, la méthode ne fait rien.
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur a été configuré ou s'il a 
	* besoin d'une configuration.
	* Pour ce processeur, aucune configuration n'est nécessaire.
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer les panneaux de configuration du processeur.
	* Pour ce processeur, aucune configuration n'est nécessaire.
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un noeud 
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un élément 
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur peut être invoqué hors d'un 
	* noeud d'exploration ou d'un élément d'un tableau.
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer la description du processeur.
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer l'intitulé de l'élément de menu associé.
	* Ce processeur n'étant pas global, cette méthode ne sera pas appelée.
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
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
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