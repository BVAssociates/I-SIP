/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/TablePopupTrigger.java,v $
* $Revision: 1.15 $
*
* ------------------------------------------------------------
* DESCRIPTION: Gestionnaire de click de souris dans le tableau
* DATE:        02/01/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: TablePopupTrigger.java,v $
* Revision 1.15  2009/03/05 09:41:48  jy
* Modifier la façcon de récupérer un objet de GenericTreeObjectNode.
*
* Revision 1.14  2009/01/14 14:23:16  tz
* Prise en compte de la modification des packages.
*
* Revision 1.13  2009/01/08 15:26:51  tz
* Correction de la fiche FS#580.
*
* Revision 1.12  2007/03/23 15:27:23  tz
* Correction de la fiche Hotline/0164.
*
* Revision 1.11  2006/03/09 15:01:16  tz
* Sélection d'une ligne du tableau sur click droit.
*
* Revision 1.10  2005/07/01 12:15:33  tz
* Modification du composant pour les traces
* Utilisation du modèle de données trié
*
* Revision 1.9  2004/10/22 15:39:57  tz
* Affichage du menu contextuel dans le thread Swing.
*
* Revision 1.8  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.7  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.6  2004/07/29 12:09:21  tz
* Suppression d'imports inutiles
* Mise à jour de la documentation
* Utilisation de DisplayProcessorInterface
*
* Revision 1.5  2002/12/26 12:55:14  tz
* Chargement du menu contextuel dans un thread
*
* Revision 1.4  2002/08/13 13:13:50  tz
* Ajout méthode d'accès au processeur d'affichage
*
* Revision 1.3  2002/06/19 12:16:49  tz
* Modification pour processeur d'administration
*
* Revision 1.2  2002/03/27 09:51:02  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.1  2002/01/07 15:51:47  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.display;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.table.TableModel;
import java.awt.Cursor;
import java.awt.Point;

//
// Imports du projet
//
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.MenuFactory;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.gui.SortedTableModel;

/*----------------------------------------------------------
* Nom: TablePopupTrigger
*
* Description:
* Cette classe est destinée à gérer les actions que l'utilisateur va effectuer
* avec la souris dans le tableau des objets. Il dérive de MouseAdapter afin de
* redéfinir la méthode qui est appelée lorsqu'un des boutons de la souris est
* relaché (voir la méthode mouseReleased() pour plus de détails).
* ----------------------------------------------------------*/
public class TablePopupTrigger
	extends MouseAdapter
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: TablePopupTrigger
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle permet de fournir 
	* au trigger une référence sur l'interface du processeur de tâche ayant 
	* instancié le trigger.
	*
	* Arguments:
	*  - processor: Une référence sur l'interface DisplayProcessorInterface.
	* ----------------------------------------------------------*/
	public TablePopupTrigger(
		DisplayProcessorInterface processor
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TablePopupTrigger", "TablePopupTrigger");

		trace_methods.beginningOfMethod();
		_processor = processor;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: mouseReleased
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est appelée lorsque
	* l'utilisateur a relaché l'un des boutons de la souris.
	* Si le bouton relaché correspond au bouton droit de la souris, la méthode
	* va récupérer le noeud graphique correspondant à la ligne sélectionnée.
	* Ensuite, elle va récupérer le menu d'exploitation de ce noeud (voir la
	* méthode getContextualMenu()).
	*
	* Arguments:
	*  - event: Une référence sur une instance de MouseEvent contenant les
	*    informations relatives à la souris.
	* ----------------------------------------------------------*/
	public void mouseReleased(
		MouseEvent event
		)
	{
		final MouseEvent theEvent = event;

		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				GenericTreeObjectNode object_node = null;
				MainWindowInterface window_interface = 
					_processor.getTheMainWindowInterface();

				Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				    "TablePopupTrigger", "mouseReleased");
				Trace trace_arguments =
					TraceAPI.declareTraceArguments("Console");
				Trace trace_debug = TraceAPI.declareTraceDebug("Console");

				trace_methods.beginningOfMethod();
				trace_arguments.writeTrace("theEvent=" + theEvent);
				window_interface.setCurrentCursor(Cursor.WAIT_CURSOR, null);
				// D'abord, on regarde que c'est un click droit
				if(SwingUtilities.isRightMouseButton(theEvent) == false)
				{
					trace_debug.writeTrace("Ce n'est pas un click droit !");
					// Ce n'est pas un click droit, on sort
					window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
					trace_methods.endOfMethod();
					return;
				}
				// on regarde si la clé est présente
				if(_processor.isKeyPresent() == false)
				{
					trace_debug.writeTrace("La clé n'est pas présente !");
					// La clé n'est pas présente, on sort
					window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
					trace_methods.endOfMethod();
					return;
				}
				// Ensuite, on regarde s'il y a une ligne sous le curseur
				JTable table = (JTable)theEvent.getSource();
				Point point = new Point(theEvent.getX(), theEvent.getY());
				int selected_row = table.rowAtPoint(point);
				int selected_column = table.columnAtPoint(point);
				if(selected_row == -1 || selected_column == -1)
				{
					trace_debug.writeTrace(
						"Il n'y a pas de cellule sous le curseur !");
					// Il n'y a pas de ligne sous le curseur, on sort
					window_interface.setCurrentCursor(
							Cursor.DEFAULT_CURSOR, null);
					trace_methods.endOfMethod();
					return;
				}
				// On va sélectionner la ligne sous le curseur
				table.changeSelection(selected_row, selected_column, false, 
						false);
				// Maintenant, il faut récupérer le noeud correspondant au rang
				// sélectionné, via le modèle de données
				TableModel table_model = table.getModel();
				DisplayDataModel model = null;
				if(table_model instanceof SortedTableModel)
				{
					SortedTableModel sorted_model = 
						(SortedTableModel)table_model;
					model = (DisplayDataModel)(sorted_model).getModel();
					// On doit également effectuer une conversion de l'indice
					// entre la vue et le modèle
					selected_row = sorted_model.modelIndex(selected_row);
				}
				else
				{
					model = (DisplayDataModel)table.getModel();
				}
				object_node = model.getParametersForRow(selected_row);
				// On récupère le menu contextuel pour ce noeud
				JMenu contextual_menu = getContextualMenu(object_node,
					window_interface);
				// On vérifie que le menu contient au moins un élément
				if(contextual_menu == null ||
					contextual_menu.getItemCount() == 0)
				{
					trace_debug.writeTrace("Il n'y a pas de menu à afficher !");
					// Il n'y a pas de menu à afficher, on sort
					window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
					window_interface.setStatus(null, null, 0);
					trace_methods.endOfMethod();
					return;
				}
				// Il y a un menu à afficher, on l'affiche dans le tableau
				// aux coordonnées du click
				final JPopupMenu popup_menu = contextual_menu.getPopupMenu();
				final JTable the_table = table;
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{
						public void run()
						{
							popup_menu.show(the_table, theEvent.getX(), 
								theEvent.getY());
						}
					});
				}
				catch(Exception e)
				{
					Trace trace_errors = 
						TraceAPI.declareTraceErrors("Console");
						
					trace_errors.writeTrace("Erreur lors de l'affichage du menu" +
						" contextuel: " + e);
				}
				window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
				trace_methods.endOfMethod();
			}
		});
		t.start();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: getDisplayProcessor
	*
	* Description:
	* Cette méthode permet de récupérer la référence de l'interface
	* DisplayProcessorInterface auquel le PopupTrigger est associé. Elle 
	* retourne la valeur de l'attribut _processor.
	*
	* Retourne: La référence sur l'interface DisplayProcessorInterface associée.
	* ----------------------------------------------------------*/
	protected DisplayProcessorInterface getDisplayProcessor()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TablePopupTrigger", "getDisplayProcessor");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _processor;
	}

	/*----------------------------------------------------------
	* Nom: finalize
	*
	* Description:
	* Cette méthode est automatiquement appelée par le Garbage Collector de la
	* machine virtuelle Java lorsque l'objet est sur le point d'être détruit.
	* Elle permet de libérer les ressources allouées.
	*
	* Lève: Throwable.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TablePopupTrigger", "finalize");

		trace_methods.beginningOfMethod();
		_processor = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getContextualMenu
	*
	* Description:
	* Cette méthode permet de récupérer le menu contextuel relatif au noeud qui
	* a été sélectionné dans le tableau. Elle fait appel à la méthode
	* createContextualMenu() de la classe MenuFactory.
	*
	* Arguments:
	*  - selectedNode: Une référence sur le noeud qui a été sélectionné,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface.
	*
	* Retourne: Une référence sur un objet JMenu contenant le menu contextuel
	* du noeud.
	* ----------------------------------------------------------*/
	protected JMenu getContextualMenu(
		GenericTreeObjectNode selectedNode,
		MainWindowInterface windowInterface
		)
	{
		JMenu contextual_menu = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"TablePopupTrigger", "getContextualMenu");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// On commence par regarder s'il y a un menu attaché au noeud
		contextual_menu = selectedNode.getMenu();
		if(contextual_menu != null)
		{
			trace_methods.endOfMethod();
			return contextual_menu;
		}
		// On appelle la méthode createContextualMenu() de la classe
		// MenuFactory, et on retourne le résultat
		contextual_menu = MenuFactory.createContextualMenu(selectedNode, false,
			windowInterface);
		selectedNode.setMenu(contextual_menu);
		trace_methods.endOfMethod();
		return contextual_menu;
	}

	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _processor
	*
	* Description:
	* Cet attribut contient la référence sur l'interface DisplayProcessorInterface 
	* ayant instanciée le trigger. Cette référence est nécessaire afin de 
	* retrouver le noeud graphique d'origine ainsi que la référence sur 
	* l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	private DisplayProcessorInterface _processor;
}