/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/menu/RefreshMenu.java,v $
* $Revision: 1.5 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur de rafra�chissement de menu contextuel
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
* Ajout de l'argument postprocessing � la m�thode run().
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
// D�claration du package
package com.bv.isis.console.impl.processor.menu;

//
// Imports syst�me
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
* Cette classe impl�mente le processeur de t�che charg� du rafra�chissement 
* des menus contextuels des noeuds (ou des �l�ments de tableau). N'�tant pas 
* un processeur graphique, cette classe impl�mente l'interface 
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
	* Cette m�thode est le constructeur par d�faut. Elle n'est pr�sent�e que 
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour effectuer un pr�-chargement du processeur.
	* Dans ce cas, la m�thode ne fait rien.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur a besoin d'une configuration et 
	* s'il est configur�.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer les panneaux de configuration du processeur.
	* Pour ce processeur, aucune configuration n'est n�cessaire.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un noeud 
	* d'exploration.
	* Pour ce processeur, seule l'invocation via le menu "Outils" de la 
	* Console n'est pas autoris�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via un 
	* �l�ment de tableau.
	* Pour ce processeur, seule l'invocation via le menu "Outils" de la 
	* Console n'est pas autoris�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour savoir si le processeur peut �tre invoqu� via le menu 
	* "Outils" de la Console.
	* Pour ce processeur, seule l'invocation via le menu "Outils" de la 
	* Console n'est pas autoris�e.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer la description du processeur.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer l'intitul� de l'�l�ment du menu "Outils".
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle  
	* est appel�e lors de l'ex�cution du processeur.
	* La m�thode va r�cup�rer le menu associ� au noeud, et demand� la cr�ation 
	* d'un nouveau menu contextuel, via la m�thode createContextualMenu() de 
	* la classe MenuFactory. Ensuite, la m�thode va v�rifier si des m�thodes 
	* ont �t� ajout�es ou retir�es, et effectuer les op�rations 
	* correspondantes, puis la m�thode va v�rifier les �tats des m�thodes � 
	* partir de l'attribut condition.
	* 
	* Si une erreur est d�tect�e pendant la phase d'initialisation, l'exception 
	* InnerException doit �tre lev�e.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface 
	*    permettant au processeur de communiquer avec la fen�tre principale 
	*    de l'application,
	*  - menuItem: Une r�f�rence sur l'option de menu qui a d�clench� 
	*    l'ex�cution du processeur de t�che,
	*  - parameters: Une cha�ne de caract�res contenant les param�tres 
	*    d'ex�cution du processeur,
	*  - preprocessing: Une cha�ne contenant des instructions de pr�processing,
	*  - postprocessing: Une cha�ne contenant des instructions de postprocessing,
	*  - selectedNode: Une r�f�rence sur l'objet graphique sur lequel le 
	*    processeur doit ex�cuter son traitement.
	* 
	* L�ve: InnerException.
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
		// On va v�rifier la validit� des arguments
		if(windowInterface == null || selectedNode == null ||
			!(selectedNode instanceof GenericTreeObjectNode) ||
			parameters == null || parameters.equals("") == true)
		{
			trace_errors.writeTrace("Au moins des arguments n'est pas valide !");
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// On caste le noeud
		selected_node = (GenericTreeObjectNode)selectedNode;
		reference_menu = selected_node.getMenu();
		// On regarde si le menu est pour un noeud
		is_for_tree = parameters.equals("true");
		// On va demander la cr�ation du nouveau menu
		new_menu = MenuFactory.createContextualMenu(selected_node, 
			is_for_tree, windowInterface);
		// On repositionne l'ancien menu
		selected_node.setMenu(reference_menu);
		windowInterface.setProgressMaximum(1);
		windowInterface.setStatus(
			MessageManager.getMessage("&Status_RefreshingMenu"), null, 0);
		// On met � jour le menu
		checkMenus(reference_menu, new_menu);
		windowInterface.setStatus(null, null, 0);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	* 
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour fermer le processeur.
	* Dans ce cas, la m�thode ne fait rien.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* est appel�e pour r�cup�rer un double du processeur.
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
	* Cette m�thode est charg�e de comparer les deux menus pass�s en argument. 
	* Elle regarde si de nouveaux �l�ments ont �t� ajout�s, ou si des �l�ments 
	* ont �t� retir�s, ou enfin, si les �tats des �l�ments identiques ont 
	* chang�.
	* 
	* Arguments:
	*  - referenceMenu: Le menu de r�f�rence sur lequel les modifications sont 
	*    apport�es,
	*  - newMenu: Le menu permettant de savoir si des modifications ont �t� 
	*    apport�es ou non.
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
		// On v�rifie la validit� des arguments
		if(referenceMenu == null || newMenu == null)
		{
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On va placer tous les �l�ments du menu de r�f�rence dans la liste
		// des �l�ments � supprimer
		for(index = 0 ; index < referenceMenu.getItemCount() ; index ++)
		{
			JMenuItem item = referenceMenu.getItem(index);
			if(item != null)
			{
				elements_to_remove.add(referenceMenu.getItem(index));
			}
		}
		// On va passer chaque �l�ment du nouveau menu
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
			// On va rechercher le m�me �l�ment dans le menu de r�f�rence
			boolean found = false;
			for(int count = 0 ; count < elements_to_remove.size() ; count ++)
			{
				Object old_element = elements_to_remove.elementAt(count);
				if(old_element == null)
				{
					continue;
				}
				trace_debug.writeTrace("old_element=" + old_element);
				// Si les deux �l�ments sont de type JMenu et qu'ils ont le m�me
				// libell�, ont va appeler la m�me m�thode
				if((new_element instanceof JMenu) && 
					(old_element instanceof JMenu) &&
					((JMenu)new_element).getText().equals(
					((JMenu)old_element).getText()) == true)
				{
					trace_debug.writeTrace("M�me menu");
					found = true;
					checkMenus((JMenu)old_element, (JMenu)new_element);
					// On va retirer old_element de la liste des �l�ments � 
					// supprimer
					elements_to_remove.removeElementAt(count);
					break;
				}
				// Sinon, on regarde si les deux �l�ments sont de type 
				// ContextualMenuItem et que les m�thodes se ressemblent
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
					trace_debug.writeTrace("M�me �l�ment");
					found = true;
					// On va retirer old_element de la liste des �l�ments � 
					// supprimer
					elements_to_remove.removeElementAt(count);
					// C'est le m�me �l�ment de menu, il faut comparer les
					// �tats
					if(old_item.isSameCondition(new_item) == true)
					{
						// La condition est identique, on passe au suivant
						trace_debug.writeTrace("Condition identique");
						break;
					}
					old_item.setCondition(new_item.isEnabled());
					// Si l'ancien �l�ment �tait activ�, ou qu'il �tait 
					// d�sactiv� sans processeur attach�, on change son �tat
					if(old_item.isEnabled() == true ||
						(old_item.isEnabled() == false && 
						old_item.hasAttachedProcessor() == false))
					{
						old_item.setEnabled(new_item.isEnabled());
					}
					break;
				}
			}
			// Si aucun �l�ment n'a �t� trouv�, il faut ajouter le nouveau
			// dans le menu de r�f�rence
			if(found == false)
			{
				trace_debug.writeTrace("Ajout de l'�l�ment: " + new_element);
				referenceMenu.add((JMenuItem)new_element, index);
			}
		}
		// Les �l�ments qui sont encore pr�sents dans la liste des
		// �l�ments � supprimer sont � supprimer
		while(elements_to_remove.size() > 0)
		{
			Object element = elements_to_remove.elementAt(0);
			if(element == null)
			{
				continue;
			}
			trace_debug.writeTrace("Suppression de l'�l�ment: " + element);
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
