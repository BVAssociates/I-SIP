/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/TreePopupTrigger.java,v $
* $Revision: 1.16 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des clicks de l'utilisateur dans l'arbre
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: TreePopupTrigger.java,v $
* Revision 1.16  2006/03/07 09:27:37  tz
* Sortie lors d'une erreur d'auto-exploration.
*
* Revision 1.15  2005/10/07 08:33:50  tz
* Changement mineur.
*
* Revision 1.14  2005/07/01 12:24:01  tz
* Modification du composant pour les traces
*
* Revision 1.13  2004/11/02 09:06:02  tz
* Mise à jour de l'état des éléments du menu contextuel.
*
* Revision 1.12  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.11  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.10  2004/10/06 07:39:12  tz
* Affichage du menu contextuel dans le thread swing.
*
* Revision 1.9  2004/07/29 12:19:13  tz
* Suppression d'imports inutiles
*
* Revision 1.8  2003/12/08 14:36:28  tz
* Amélioration de l'auto-exploration.
*
* Revision 1.7  2003/03/07 16:22:01  tz
* Ajout de l'auto-exploration
*
* Revision 1.6  2002/12/26 12:56:24  tz
* Chargement du menu contextuel dans un thread
*
* Revision 1.5  2002/11/19 08:46:46  tz
* Gestion de la progression de la tâche.
*
* Revision 1.4  2002/08/13 13:17:07  tz
* Vérification de la position du menu.
* Curseur -> sablier lors du chargement du menu.
*
* Revision 1.3  2002/03/27 09:42:06  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2001/12/19 09:58:49  tz
* Cloture itération IT1.0.0
*
* Revision 1.1  2001/11/19 17:07:54  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.gui;

//
// Imports système
//
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.tree.TreePath;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import java.awt.Cursor;

//
// Imports du projet
//
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.MenuFactory;

/*----------------------------------------------------------
* Nom: TreePopupTrigger
*
* Description:
* Cette classe est destinée à gérer les actions que l'utilisateur va effectuer
* avec la souris dans l'arbre d'exploration. Il dérive de MouseAdapter afin de
* redéfinir la méthode qui est appelée lorsqu'un des boutons de la souris est
* relaché (voir la méthode mouseRelease() pour plus de détails).
* ----------------------------------------------------------*/
class TreePopupTrigger
	extends MouseAdapter
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: TreePopupTrigger
	*
	* Description:
	* Cette méthode est le constructeur par défaut de la classe. Elle n'est
	* présentée que pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public TreePopupTrigger()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TreePopupTrigger", "TreePopupTrigger");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: mouseReleased
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée lorsque
	* l'utilisateur a relaché l'un des boutons de la souris.
	* La méthode va récupérer le noeud graphique se trouvant sous la souris et va le
	* sélectionner.
	* Si l'utilisateur a double-cliqué sur le noeud, une tentative d'exploration
	* automatique du noeud est déclenchée, via la méthode doAutomaticExplore() de
	* la classe MenuFactory.
	* Si le click a été effectué avec le bouton droit ou que l'exploration 
	* automatique n'a pas été possible, le menu contextuel est affiché via la
	* méthode showMenu().
	*
	* Arguments:
	*  - event: Une référence sur une instance de MouseEvent contenant les
	*    informations relatives à la souris.
	* ----------------------------------------------------------*/
	public void mouseReleased(
		MouseEvent theEvent
		)
	{
		final MouseEvent event = theEvent;

		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				    "TreePopupTrigger", "mouseReleased");
				Trace trace_arguments =
					TraceAPI.declareTraceArguments("Console");
				Trace trace_debug = TraceAPI.declareTraceDebug("Console");

				trace_methods.beginningOfMethod();
				trace_arguments.writeTrace("event=" + event);
				// On récupère l'arbre
				MainWindowTree tree = (MainWindowTree)event.getSource();
				// On récupère les coordonnées du click
				int x = event.getX();
				int y = event.getY();
				trace_debug.writeTrace("Les coordonnées du click sont: " +
					x + "," + y);
				// On regarde s'il y a un noeud en dessous de la souris
				TreePath path = tree.getPathForLocation(x, y);
				if(path == null)
				{
					trace_debug.writeTrace(
						"Il n'y a pas de noeud sous la souris !");
					// Il n'y a rien, on sort
					trace_methods.endOfMethod();
					return;
				}
				// On sélectionne le noeud et on le récupère
				tree.setSelectionPath(path);
				GenericTreeObjectNode selected_object =
					(GenericTreeObjectNode)path.getLastPathComponent();
				if(selected_object == null)
				{
					trace_debug.writeTrace(
						"Il n'y a pas de noeud sous la souris !");
					// Il n'y a aucun objet, on sort
					trace_methods.endOfMethod();
					return;
				}
				trace_debug.writeTrace("Noeud sélectionné: " +
					selected_object);
				// On regarde si l'évènement correspond à un relachement du
				// bouton droit
				if(SwingUtilities.isRightMouseButton(event) == true)
				{
					trace_debug.writeTrace(
						"L'utilisateur a cliqué avec le bouton droit.");
					// On appelle la méthode d'affichage du menu
					showMenu(selected_object, tree, x, y);
				}
				// On regarde si l'évènement correspond à un double-clik sur
				// le bouton gauche
				if(SwingUtilities.isLeftMouseButton(event) == true &&
					event.getClickCount() == 2)
				{
					tree.getWindowInterface().setCurrentCursor(
						Cursor.WAIT_CURSOR, null);
					boolean explored = false;
					// On va tout d'abord tenter de déclencher l'exploration
					// automatique
					try
					{
						explored = MenuFactory.doAutomaticExplore(
							tree.getWindowInterface(), selected_object, false, 0);
					}
					catch(Exception exception)
					{
						Trace trace_errors =
							TraceAPI.declareTraceErrors("Console");
						trace_errors.writeTrace(
							"Erreur lors de l'exploration automatique: " +
							exception.getMessage());
						tree.getWindowInterface().setCurrentCursor(
							Cursor.DEFAULT_CURSOR, null);
						// On affiche un message d'erreur
						tree.getWindowInterface().showPopupForException(
							"&ERR_ErrorWhileExploringAutomatically", exception);
						tree.getWindowInterface().setStatus(null, null, 0);
						trace_methods.endOfMethod();
						return;
					}
					tree.getWindowInterface().setCurrentCursor(
						Cursor.DEFAULT_CURSOR, null);
					if(explored == false)
					{
						// L'exploration n'a pas eu lieu, pour une raison
						// quelconque. On va afficher le menu contextuel.
						showMenu(selected_object, tree, x, y);
					}
				}
				trace_methods.endOfMethod();
			}
		});
		t.start();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: showMenu
	*
	* Description:
	* Cette méthode est appelée par la méthode mouseReleased() lorsque le
	* menu contextuel d'un noeud doit être affiché. Le menu d'exploitation de 
	* ce noeud (voir la méthode getMenu() de la classe GenericTreeObjectNode)
	* est récupéré. Si aucun menu n'a été précédemment ouvert, celui-ci sera 
	* récupéré par la méthode createContextualMenu() de la classe MenuFactory.
	* Enfin, le menu contextuel sera affiché.
	*
	* Arguments:
	*  - selectedNode: Le noeud sélectionné,
	*  - tree: Une référence sur l'arbre d'exploration,
	*  - x: La position de la souris au moment du click,
	*  - y: La position de la souris au moment du click.
	* ----------------------------------------------------------*/
	private void showMenu(
		GenericTreeObjectNode selectedNode,
		MainWindowTree tree,
		int x,
		int y
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TreePopupTrigger", "showMenu");
		Trace trace_arguments =
			TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("tree=" + tree);
		// On va positionner le curseur -> sablier
		tree.getWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, null);
		// Est-ce qu'il y a un menu attaché au noeud ?
		JMenu menu = selectedNode.getMenu();
		if(menu == null)
		{
			// On construit le menu pour ce noeud
			menu = MenuFactory.createContextualMenu(selectedNode,
				true, tree.getWindowInterface());
		}
		if(menu == null || menu.getItemCount() == 0)
		{
			trace_debug.writeTrace("Aucun item dans le menu !");
			// On va positionner le curseur -> défaut
			tree.getWindowInterface().setCurrentCursor(
				Cursor.DEFAULT_CURSOR, null);
			tree.getWindowInterface().setStatus(null, null, 0);
			// Il n'y a pas de menu, on sort
			trace_methods.endOfMethod();
			return;
		}
		// On va rafraîchir l'état des éléments du menu contextuel
		MenuFactory.updateMenuItemsState(selectedNode);
		// On crée le menu popup et on l'affiche dans l'arbre
		final JPopupMenu popup_menu = menu.getPopupMenu();
		// On va récupérer la hauteur du menu
		int menu_height = popup_menu.getPreferredSize().height;
		// On vérifie que le menu entre dans l'arbre
		if(menu_height + y >= tree.getHeight())
		{
			// Il faut remonter la position d'affichage du menu
			y = tree.getHeight() - menu_height - 20;
		}
		// On va positionner le curseur -> défaut
		tree.getWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
		tree.getWindowInterface().setStatus(null, null, 0);
		if(SwingUtilities.isEventDispatchThread() == true)
		{
			popup_menu.show(tree, x, y);
		}
		else
		{
			try
			{
				final int the_x = x;
				final int the_y = y;
				final MainWindowTree the_tree = tree;
				 
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						popup_menu.show(the_tree, the_x, the_y);
					}
				});
			}
			catch(Exception e)
			{
				// Rien à faire
			}
		}
	}
}