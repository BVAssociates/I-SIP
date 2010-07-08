/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/menu/RefreshMenu.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de rafraîchissement de menu contextuel
* DATE:        08/11/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.menu
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: RefreshMenu.java,v $
* Revision 1.5  2009/01/14 14:23:17  tz
* Prise en compte de la modification des packages.
*
* Revision 1.4  2008/01/31 16:57:20  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.3  2005/10/07 08:24:58  tz
* Changement non fonctionnel
*
* Revision 1.2  2005/07/01 12:12:55  tz
* Modification du composant pour les traces
*
* Revision 1.1  2004/11/09 15:23:48  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.menu;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Vector;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.ContextualMenuItem;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.MenuFactory;

/*----------------------------------------------------------
* Nom: RefreshMenu
* 
* Description:
* Cette classe implémente le processeur de tâche chargé du rafraîchissement 
* des menus contextuels des noeuds (ou des éléments de tableau). N'étant pas 
* un processeur graphique, cette classe implémente l'interface 
* ProcessorInterface.
----------------------------------------------------------*/
public class RefreshMenu 
	implements ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: RefreshMenu
	* 
	* Description:
	* Cette méthode est le constructeur par défaut. Elle n'est présentée que 
	* pour des raisons de lisibilité.
	----------------------------------------------------------*/
	public RefreshMenu()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "RefreshMenu");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour effectuer un pré-chargement du processeur.
	* Dans ce cas, la méthode ne fait rien.
	----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "preLoad");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur a besoin d'une configuration et 
	* s'il est configuré.
	* Pour ce processeur, aucune configuration n'est nécessaire.
	* 
	* Retourne: true.
	----------------------------------------------------------*/
	public boolean isConfigured()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "isConfigured");

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
	----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "getConfigurationPanels");

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
	* Pour ce processeur, seule l'invocation via le menu "Outils" de la 
	* Console n'est pas autorisée.
	* 
	* Retourne: true.
	----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "isTreeCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via un 
	* élément de tableau.
	* Pour ce processeur, seule l'invocation via le menu "Outils" de la 
	* Console n'est pas autorisée.
	* 
	* Retourne: true.
	----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "isTableCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour savoir si le processeur peut être invoqué via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via le menu "Outils" de la 
	* Console n'est pas autorisée.
	* 
	* Retourne: false.
	----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "isGlobalCapable");

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
	----------------------------------------------------------*/
	public String getDescription()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "getDescription");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return MessageManager.getMessage("&RefreshMenuProcessorDescription");
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer l'intitulé de l'élément du menu "Outils".
	* 
	* Retourne: null.
	----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "getMenuLabel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: run
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle  
	* est appelée lors de l'exécution du processeur.
	* La méthode va récupérer le menu associé au noeud, et demandé la création 
	* d'un nouveau menu contextuel, via la méthode createContextualMenu() de 
	* la classe MenuFactory. Ensuite, la méthode va vérifier si des méthodes 
	* ont été ajoutées ou retirées, et effectuer les opérations 
	* correspondantes, puis la méthode va vérifier les états des méthodes à 
	* partir de l'attribut condition.
	* 
	* Si une erreur est détectée pendant la phase d'initialisation, l'exception 
	* InnerException doit être levée.
	* 
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface 
	*    permettant au processeur de communiquer avec la fenêtre principale 
	*    de l'application,
	*  - menuItem: Une référence sur l'option de menu qui a déclenché 
	*    l'exécution du processeur de tâche,
	*  - parameters: Une chaîne de caractères contenant les paramètres 
	*    d'exécution du processeur,
	*  - preprocessing: Une chaîne contenant des instructions de préprocessing,
	*  - postprocessing: Une chaîne contenant des instructions de postprocessing,
	*  - selectedNode: Une référence sur l'objet graphique sur lequel le 
	*    processeur doit exécuter son traitement.
	* 
	* Lève: InnerException.
	----------------------------------------------------------*/
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
			"RefreshMenu", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		GenericTreeObjectNode selected_node = null;
		JMenu reference_menu = null;
		JMenu new_menu = null;
		boolean is_for_tree = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// On va vérifier la validité des arguments
		if(windowInterface == null || selectedNode == null ||
			!(selectedNode instanceof GenericTreeObjectNode) ||
			parameters == null || parameters.equals("") == true)
		{
			trace_errors.writeTrace("Au moins des arguments n'est pas valide !");
			// On lève une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On caste le noeud
		selected_node = (GenericTreeObjectNode)selectedNode;
		reference_menu = selected_node.getMenu();
		// On regarde si le menu est pour un noeud
		is_for_tree = parameters.equals("true");
		// On va demander la création du nouveau menu
		new_menu = MenuFactory.createContextualMenu(selected_node, 
			is_for_tree, windowInterface);
		// On repositionne l'ancien menu
		selected_node.setMenu(reference_menu);
		windowInterface.setProgressMaximum(1);
		windowInterface.setStatus(
			MessageManager.getMessage("&Status_RefreshingMenu"), null, 0);
		// On met à jour le menu
		checkMenus(reference_menu, new_menu);
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour fermer le processeur.
	* Dans ce cas, la méthode ne fait rien.
	----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "close");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* est appelée pour récupérer un double du processeur.
	* 
	* Retourne: Une nouvelle instance de RefreshMenu.
	----------------------------------------------------------*/
	public ProcessorInterface duplicate()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "duplicate");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new RefreshMenu();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: checkMenus
	* 
	* Description:
	* Cette méthode est chargée de comparer les deux menus passés en argument. 
	* Elle regarde si de nouveaux éléments ont été ajoutés, ou si des éléments 
	* ont été retirés, ou enfin, si les états des éléments identiques ont 
	* changé.
	* 
	* Arguments:
	*  - referenceMenu: Le menu de référence sur lequel les modifications sont 
	*    apportées,
	*  - newMenu: Le menu permettant de savoir si des modifications ont été 
	*    apportées ou non.
 	----------------------------------------------------------*/
 	private void checkMenus(
 		JMenu referenceMenu,
 		JMenu newMenu
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"RefreshMenu", "checkMenus");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Vector elements_to_remove = new Vector();
		int index;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("referenceMenu=" + referenceMenu);
		trace_arguments.writeTrace("newMenu=" + newMenu);
		// On vérifie la validité des arguments
		if(referenceMenu == null || newMenu == null)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va placer tous les éléments du menu de référence dans la liste
		// des éléments à supprimer
		for(index = 0 ; index < referenceMenu.getItemCount() ; index ++)
		{
			JMenuItem item = referenceMenu.getItem(index);
			if(item != null)
			{
				elements_to_remove.add(referenceMenu.getItem(index));
			}
		}
		// On va passer chaque élément du nouveau menu
		index = -1;
		while(newMenu.getItemCount() > 0)
		{
			Object new_element = newMenu.getItem(0);
			index ++;
			newMenu.remove(0);
			if(new_element == null)
			{
				continue;
			}
			trace_debug.writeTrace("new_element=" + new_element);
			// On va rechercher le même élément dans le menu de référence
			boolean found = false;
			for(int count = 0 ; count < elements_to_remove.size() ; count ++)
			{
				Object old_element = elements_to_remove.elementAt(count);
				if(old_element == null)
				{
					continue;
				}
				trace_debug.writeTrace("old_element=" + old_element);
				// Si les deux éléments sont de type JMenu et qu'ils ont le même
				// libellé, ont va appeler la même méthode
				if((new_element instanceof JMenu) && 
					(old_element instanceof JMenu) &&
					((JMenu)new_element).getText().equals(
					((JMenu)old_element).getText()) == true)
				{
					trace_debug.writeTrace("Même menu");
					found = true;
					checkMenus((JMenu)old_element, (JMenu)new_element);
					// On va retirer old_element de la liste des éléments à 
					// supprimer
					elements_to_remove.removeElementAt(count);
					break;
				}
				// Sinon, on regarde si les deux éléments sont de type 
				// ContextualMenuItem et que les méthodes se ressemblent
				if((new_element instanceof ContextualMenuItem) &&
					(old_element instanceof ContextualMenuItem))
				{
					ContextualMenuItem old_item =
						(ContextualMenuItem)old_element;
					ContextualMenuItem new_item =
						(ContextualMenuItem)new_element;
					if(old_item.isSameMethod(new_item) == false)
					{
						// On continue
						continue;
					}
					trace_debug.writeTrace("Même élément");
					found = true;
					// On va retirer old_element de la liste des éléments à 
					// supprimer
					elements_to_remove.removeElementAt(count);
					// C'est le même élément de menu, il faut comparer les
					// états
					if(old_item.isSameCondition(new_item) == true)
					{
						// La condition est identique, on passe au suivant
						trace_debug.writeTrace("Condition identique");
						break;
					}
					old_item.setCondition(new_item.isEnabled());
					// Si l'ancien élément était activé, ou qu'il était 
					// désactivé sans processeur attaché, on change son état
					if(old_item.isEnabled() == true ||
						(old_item.isEnabled() == false && 
						old_item.hasAttachedProcessor() == false))
					{
						old_item.setEnabled(new_item.isEnabled());
					}
					break;
				}
			}
			// Si aucun élément n'a été trouvé, il faut ajouter le nouveau
			// dans le menu de référence
			if(found == false)
			{
				trace_debug.writeTrace("Ajout de l'élément: " + new_element);
				referenceMenu.add((JMenuItem)new_element, index);
			}
		}
		// Les éléments qui sont encore présents dans la liste des
		// éléments à supprimer sont à supprimer
		while(elements_to_remove.size() > 0)
		{
			Object element = elements_to_remove.elementAt(0);
			if(element == null)
			{
				continue;
			}
			trace_debug.writeTrace("Suppression de l'élément: " + element);
			referenceMenu.remove((JMenuItem)element);
			if(element instanceof ContextualMenuItem)
			{
				((ContextualMenuItem)element).closeAttachedProcessor();
			}
			else if(element instanceof JMenu)
			{
				JMenu menu = (JMenu)element;
				while(menu.getItemCount() > 0)
				{
					element = menu.getItem(0);
					menu.remove(0);
					if(element instanceof ContextualMenuItem)
					{
						((ContextualMenuItem)element).closeAttachedProcessor();
					}
				}
			}
			elements_to_remove.remove(0);
		}
		elements_to_remove.clear();
		trace_methods.endOfMethod();
 	}
}
