/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
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
* Modifier la fa�con de r�cup�rer un objet de GenericTreeObjectNode.
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
* S�lection d'une ligne du tableau sur click droit.
*
* Revision 1.10  2005/07/01 12:15:33  tz
* Modification du composant pour les traces
* Utilisation du mod�le de donn�es tri�
*
* Revision 1.9  2004/10/22 15:39:57  tz
* Affichage du menu contextuel dans le thread Swing.
*
* Revision 1.8  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.7  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.6  2004/07/29 12:09:21  tz
* Suppression d'imports inutiles
* Mise � jour de la documentation
* Utilisation de DisplayProcessorInterface
*
* Revision 1.5  2002/12/26 12:55:14  tz
* Chargement du menu contextuel dans un thread
*
* Revision 1.4  2002/08/13 13:13:50  tz
* Ajout m�thode d'acc�s au processeur d'affichage
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
// D�claration du package
package com.bv.isis.console.impl.processor.display;

//
// Imports syst�me
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
* Cette classe est destin�e � g�rer les actions que l'utilisateur va effectuer
* avec la souris dans le tableau des objets. Il d�rive de MouseAdapter afin de
* red�finir la m�thode qui est appel�e lorsqu'un des boutons de la souris est
* relach� (voir la m�thode mouseReleased() pour plus de d�tails).
* ----------------------------------------------------------*/
public class TablePopupTrigger
	extends MouseAdapter
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: TablePopupTrigger
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle permet de fournir 
	* au trigger une r�f�rence sur l'interface du processeur de t�che ayant 
	* instanci� le trigger.
	*
	* Arguments:
	*  - processor: Une r�f�rence sur l'interface DisplayProcessorInterface.
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
	* Cette m�thode red�finit celle de la super-classe. Elle est appel�e lorsque
	* l'utilisateur a relach� l'un des boutons de la souris.
	* Si le bouton relach� correspond au bouton droit de la souris, la m�thode
	* va r�cup�rer le noeud graphique correspondant � la ligne s�lectionn�e.
	* Ensuite, elle va r�cup�rer le menu d'exploitation de ce noeud (voir la
	* m�thode getContextualMenu()).
	*
	* Arguments:
	*  - event: Une r�f�rence sur une instance de MouseEvent contenant les
	*    informations relatives � la souris.
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
				// on regarde si la cl� est pr�sente
				if(_processor.isKeyPresent() == false)
				{
					trace_debug.writeTrace("La cl� n'est pas pr�sente !");
					// La cl� n'est pas pr�sente, on sort
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
				// On va s�lectionner la ligne sous le curseur
				table.changeSelection(selected_row, selected_column, false, 
						false);
				// Maintenant, il faut r�cup�rer le noeud correspondant au rang
				// s�lectionn�, via le mod�le de donn�es
				TableModel table_model = table.getModel();
				DisplayDataModel model = null;
				if(table_model instanceof SortedTableModel)
				{
					SortedTableModel sorted_model = 
						(SortedTableModel)table_model;
					model = (DisplayDataModel)(sorted_model).getModel();
					// On doit �galement effectuer une conversion de l'indice
					// entre la vue et le mod�le
					selected_row = sorted_model.modelIndex(selected_row);
				}
				else
				{
					model = (DisplayDataModel)table.getModel();
				}
				object_node = model.getParametersForRow(selected_row);
				// On r�cup�re le menu contextuel pour ce noeud
				JMenu contextual_menu = getContextualMenu(object_node,
					window_interface);
				// On v�rifie que le menu contient au moins un �l�ment
				if(contextual_menu == null ||
					contextual_menu.getItemCount() == 0)
				{
					trace_debug.writeTrace("Il n'y a pas de menu � afficher !");
					// Il n'y a pas de menu � afficher, on sort
					window_interface.setCurrentCursor(Cursor.DEFAULT_CURSOR, null);
					window_interface.setStatus(null, null, 0);
					trace_methods.endOfMethod();
					return;
				}
				// Il y a un menu � afficher, on l'affiche dans le tableau
				// aux coordonn�es du click
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
	* Cette m�thode permet de r�cup�rer la r�f�rence de l'interface
	* DisplayProcessorInterface auquel le PopupTrigger est associ�. Elle 
	* retourne la valeur de l'attribut _processor.
	*
	* Retourne: La r�f�rence sur l'interface DisplayProcessorInterface associ�e.
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
	* Cette m�thode est automatiquement appel�e par le Garbage Collector de la
	* machine virtuelle Java lorsque l'objet est sur le point d'�tre d�truit.
	* Elle permet de lib�rer les ressources allou�es.
	*
	* L�ve: Throwable.
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
	* Cette m�thode permet de r�cup�rer le menu contextuel relatif au noeud qui
	* a �t� s�lectionn� dans le tableau. Elle fait appel � la m�thode
	* createContextualMenu() de la classe MenuFactory.
	*
	* Arguments:
	*  - selectedNode: Une r�f�rence sur le noeud qui a �t� s�lectionn�,
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface.
	*
	* Retourne: Une r�f�rence sur un objet JMenu contenant le menu contextuel
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
		// On commence par regarder s'il y a un menu attach� au noeud
		contextual_menu = selectedNode.getMenu();
		if(contextual_menu != null)
		{
			trace_methods.endOfMethod();
			return contextual_menu;
		}
		// On appelle la m�thode createContextualMenu() de la classe
		// MenuFactory, et on retourne le r�sultat
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
	* Cet attribut contient la r�f�rence sur l'interface DisplayProcessorInterface 
	* ayant instanci�e le trigger. Cette r�f�rence est n�cessaire afin de 
	* retrouver le noeud graphique d'origine ainsi que la r�f�rence sur 
	* l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	private DisplayProcessorInterface _processor;
}