/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/AdministratorPopupTrigger.java,v $
* $Revision: 1.9 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de traitement des clicks
* DATE:        11/06/2002
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: AdministratorPopupTrigger.java,v $
* Revision 1.9  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.8  2005/07/01 12:22:34  tz
* Modification du composant pour les traces
*
* Revision 1.7  2004/10/22 15:41:00  tz
* Modification des chaînes de messages.
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.5  2004/10/13 14:00:07  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2004/07/29 12:18:21  tz
* Suppression d'imports inutiles
* Remplacement de Master* par Portal*
*
* Revision 1.3  2002/11/22 15:30:32  tz
* Cloture IT1.0.7
* Bouton "Modifier" disponible sauf pour les table AgentICles et MasterApplicationComponents
*
* Revision 1.2  2002/06/27 14:13:04  tz
* Ajout des processeurs d'administration des I-CLE et des Agents
*
* Revision 1.1  2002/06/19 12:17:52  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.bv.core.message.MessageManager;

//
// Imports du projet
//
import com.bv.isis.console.impl.processor.display.TablePopupTrigger;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: AdministratorPopupTrigger
*
* Description:
* Cette classe est une sous-classe de la classe TablePopupTrigger. Elle
* redéfinit la méthode getContextualMenu() afin de construire un menu contextuel
* d'administration uniquement.
* ----------------------------------------------------------*/
public class AdministratorPopupTrigger
	extends TablePopupTrigger
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: AdministratorPopupTrigger
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle permet de construire
	* une instance de TablePopupTrigger (la super-classe).
	*
	* Arguments:
	*  - processor: Une référence sur la classe processeur d'administration.
	* ----------------------------------------------------------*/
	public AdministratorPopupTrigger(
		AdministrationProcessor processor
		)
	{
		super(processor);
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministratorPopupTrigger", "AdministratorPopupTrigger");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("processor=" + processor);
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: getContextualMenu
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe TablePopupTrigger. Elle
	* est appelée lorsque l'utilisateur a effectué un click droit sur une ligne
	* de données du tableau. Elle est chargée de construire un menu permettant
	* à l'utilisateur d'ajouter, de modifier ou de supprimer des données, et,
	* par conséquent, des objets.
	*
	* Arguments:
	*  - selectedNode: Une référence sur le noeud correspondant à la ligne
	*    sélectionnée dans le tableau,
	*  - windowInterface: Une référence sur l'interface MainWindowInterface.
	*
	* Retourne: Un objet JMenu contenant le menu contextuel.
	* ----------------------------------------------------------*/
	protected JMenu getContextualMenu(
		GenericTreeObjectNode selectedNode,
		MainWindowInterface windowInterface
		)
	{
		JMenu contextual_menu = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"AdministratorPopupTrigger", "getContextualMenu");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		// On crée le menu contextuel
		contextual_menu = new JMenu();
		final AdministrationProcessor processor =
			(AdministrationProcessor)getDisplayProcessor();
		final GenericTreeObjectNode selected_node = selectedNode;
		// Création de l'élément "Ajouter"
		JMenuItem add_item = new JMenuItem(
			MessageManager.getMessage("&Admin_Add"));
		// On ajoute le callback sur l'item
		add_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Thread processing_thread = new Thread(new Runnable()
				{
					public void run()
					{
						processor.addItem();
					}
				});
				processing_thread.start();
			}
		});
		// On ajoute l'item au menu contextuel
		contextual_menu.add(add_item);
		// Création de l'élément "Modifier"
		JMenuItem modify_item = new JMenuItem(
			MessageManager.getMessage("&Admin_Modify"));
		// On ajoute le callback sur l'item
		modify_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Thread processing_thread = new Thread(new Runnable()
				{
					public void run()
					{
						processor.modifyItem(selected_node);
					}
				});
				processing_thread.start();
			}
		});
		// On l'ajoute au menu contextuel
		contextual_menu.add(modify_item);
		String table_name = selected_node.getTableName();
		if(table_name.equals("AgentICles") == true ||
			table_name.equals("PortalrApplicationComponents") == true)
		{
			modify_item.setEnabled(false);
		}
		// Création de l'item "Supprimer"
		JMenuItem remove_item = new JMenuItem(
			MessageManager.getMessage("&Admin_Remove"));
		// On ajoute le callback sur l'item
		remove_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Thread processing_thread = new Thread(new Runnable()
				{
					public void run()
					{
						processor.removeItem(selected_node);
					}
				});
				processing_thread.start();
			}
		});
		// On l'ajoute au menu contextuel
		contextual_menu.add(remove_item);
		contextual_menu.addSeparator();
		// Création de l'item "Recharger"
		JMenuItem reload_item = new JMenuItem(
			MessageManager.getMessage("&Admin_Reload"));
		// On ajoute le callback sur l'item
		reload_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				processor.reloadData(false);
			}
		});
		// On l'ajoute au menu contextuel
		contextual_menu.add(reload_item);
		trace_methods.endOfMethod();
		return contextual_menu;
	}

	// ******************* PRIVE **********************
}