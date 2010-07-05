/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/MainWindowTree.java,v $
* $Revision: 1.13 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation de l'arbre d'exploration
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MainWindowTree.java,v $
* Revision 1.13  2009/01/14 14:22:11  tz
* Prise en compte de la modification des packages.
*
* Revision 1.12  2008/07/17 15:58:50  tz
* Réglage de la hauteur des lignes en fonction de la taille des icônes.
*
* Revision 1.11  2005/07/01 12:24:19  tz
* Modification du composant pour les traces
*
* Revision 1.10  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.9  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.8  2004/10/06 07:39:48  tz
* Ré-affichage du contenu de l'arbre en différé.
*
* Revision 1.7  2004/07/29 12:19:24  tz
* Suppression d'imports inutiles
*
* Revision 1.6  2003/03/07 16:22:04  tz
* Ajout de l'auto-exploration
*
* Revision 1.5  2002/04/05 15:47:21  tz
* Cloture itération IT1.2
*
* Revision 1.4  2002/03/27 09:42:06  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.3  2002/02/04 10:54:24  tz
* Cloture itération IT1.0.1
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
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.basic.BasicTreeUI;

import com.bv.core.config.ConfigurationAPI;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.SwingUtilities;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.TreeInterface;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;

/*----------------------------------------------------------
* Nom: MainWindowTree
*
* Description:
* Cette classe représente l'arbre de navigation chargé de représenter les objets
* de production des plates-formes. Elle est une spécialisation de la classe
* JTree, en implémentant l'interface TreeSelectionListener.
* Cette spécialisation permet d'ajouter un comportement particulier lors de
* l'évènement suivant:
*  - Sélection d'un noeud.
*
* Cette classe implémente également l'interface TreeInterface, permettant de
* mettre à jour l'arbre lors de l'ajout ou de la suppression de noeuds.
* ----------------------------------------------------------*/
class MainWindowTree
	extends JTree
	implements TreeInterface,
			   TreeSelectionListener
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: MainWindowTree
	*
	* Description:
	* Cette méthode est le seul constructeur de la classe. Elle permet de
	* fournir une référence sur l'interface MainWindowInterface.
	* Lors de sa construction, l'arbre va régler des paramètres d'affichage et
	* de sélection. Il va également s'assigner un gestionnaire de rendu de
	* cellule spécialisé via une instance de IsisTreeCellRenderer, et un
	* gestionnaire de menu popup, via une instance de TreePopupTrigger.
	*
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface,
	*  - rootNode: Une référence sur l'objet graphique racine de l'arbre.
	* ----------------------------------------------------------*/
	public MainWindowTree(
		MainWindowInterface windowInterface,
		DefaultMutableTreeNode rootNode
		)
	{
		super(rootNode);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "MainWindowTree");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		int icons_size = 13;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("rootNode=" + rootNode);
		_windowInterface = windowInterface;
		// Récupération de la taille des icônes depuis la configuration
		try {
			ConfigurationAPI configuration = new ConfigurationAPI();
			icons_size = configuration.getInt("GUI", "NodeIcons.Size");
		}
		catch(Exception e) {
			// On ne fait rien
		}
		// Création du "trigger" et assignation
		TreePopupTrigger trigger = new TreePopupTrigger();
		addMouseListener(trigger);
		// Ajout de "this" en tant que TreeSelectionListener
		addTreeSelectionListener(this);
		// Création du gestionnaire de rendu et assignation
		IsisTreeCellRenderer renderer = new IsisTreeCellRenderer();
		setCellRenderer(renderer);
		// Réglage des propriétés de l'arbre
		// Réglage des indentations
		BasicTreeUI tree_ui = (BasicTreeUI)getUI();
		tree_ui.setRightChildIndent(10);
		tree_ui.setLeftChildIndent(6);
		// Réglage de la hauteur d'un rang
		setRowHeight(icons_size + 3);
		// Masquage du noeud racine
		setRootVisible(false);
		setShowsRootHandles(true);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: nodesWereInserted
	*
	* Description:
	* Cette méthode rédéfinit celle de l'interface TreeInterface. Elle est
	* appelée lorsque des noeuds ont été ajoutés à un noeud graphique.
	* La méthode informe le modèle de données de l'arbre que des noeuds ont été
	* ajoutés, afin de mettre à jour l'arbre.
	*
	* Arguments:
	*  - parent: Une référence sur une instance de DefaultMutableTreeNode
	*    correspond au noeud auquel ont été ajoutés les nouveaux noeuds,
	*  - indexes: Un tableau d'entier indiquant les indexes des noeuds qui ont
	*    été ajoutés.
	* ----------------------------------------------------------*/
	public void nodesWereInserted(
		final DefaultMutableTreeNode parent,
		final int[] indexes
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "nodesWereInserted");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parent=" + parent);
		trace_arguments.writeTrace("indexes=" + indexes);
		// On doit informer le modèle de données du changement.
		// Récupération du modèle (de type standard)
		final DefaultTreeModel model = (DefaultTreeModel)getModel();
		if(SwingUtilities.isEventDispatchThread() == true)
		{
			// Notification du modèle du changement
			model.nodesWereInserted(parent, indexes);
		}
		else
		{
			try
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						// Notification du modèle du changement
						model.nodesWereInserted(parent, indexes);
					}
				});
			}
			catch(Exception exception)
			{
				// Aucune chance que cette exception n'arrive
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: nodesWereRemoved
	*
	* Description:
	* Cette méthode rédéfinit celle de l'interface TreeInterface. Elle est
	* appelée lorsque des noeuds ont été supprimés d'un noeud graphique.
	* La méthode informe le modèle de données de l'arbre que des noeuds ont été
	* supprimés, afin de mettre à jour l'arbre.
	*
	* Arguments:
	*  - parent: Une référence sur une instance de DefaultMutableTreeNode
	*    correspond au noeud duquel ont été supprimés les nouveaux noeuds,
	*  - indexes: Un tableau d'entier indiquant les indexes des noeuds qui ont
	*    été supprimés,
	*  - removedNodes: Un tableau d'instances de DefaultMutableTreeNode
	*    correspondants aux noeuds qui ont été supprimés.
	* ----------------------------------------------------------*/
	public void nodesWereRemoved(
		final DefaultMutableTreeNode parent,
		final int[] indexes,
		final DefaultMutableTreeNode[] removedNodes
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "nodesWereRemoved");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parent=" + parent);
		trace_arguments.writeTrace("indexes=" + indexes);
		trace_arguments.writeTrace("removedNodes=" + removedNodes);
		// On doit informer le modèle de données du changement.
		// Récupération du modèle (de type standard)
		final DefaultTreeModel model = (DefaultTreeModel)getModel();
		if(SwingUtilities.isEventDispatchThread() == true)
		{
			// Notification du modèle du changement
			model.nodesWereRemoved(parent, indexes, removedNodes);
		}
		else
		{
			try
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						// Notification du modèle du changement
						model.nodesWereRemoved(parent, indexes, removedNodes);
					}
				});
			}
			catch(Exception exception)
			{
				// Aucune chance que cette exception n'arrive
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: expandNode
	*
	* Description:
	* Cette méthode rédéfinit celle de l'interface TreeInterface. Elle est
	* appelée lorsqu'un noeud doit être étendu.
	* La méthode appelle la méthode expandPath() définie dans la classe JTree.
	*
	* Arguments:
	*  - nodeToExpand: Une référence sur le noeud graphique à étendre.
	* ----------------------------------------------------------*/
	public void expandNode(
		final DefaultMutableTreeNode nodeToExpand
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "expandNode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("nodeToExpand=" + nodeToExpand);
		if(SwingUtilities.isEventDispatchThread() == true)
		{
		    expandPath(new TreePath(nodeToExpand.getPath()));
		}
		else
		{
			try
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						expandPath(new TreePath(nodeToExpand.getPath()));
					}
				});
			}
			catch(Exception exception)
			{
				// Aucune chance que cette exception n'arrive
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: collapseNode
	*
	* Description:
	* Cette méthode rédéfinit celle de l'interface TreeInterface. Elle est
	* appelée lorsqu'un noeud doit être réduit.
	* La méthode appelle la méthode collapsePath() définie dans la classe JTree.
	*
	* Arguments:
	*  - nodeToCollapse: Une référence sur le noeud graphique à étendre.
	* ----------------------------------------------------------*/
	public void collapseNode(
		final DefaultMutableTreeNode nodeToCollapse
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "collapseNode");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("nodeToCollapse=" + nodeToCollapse);
		if(SwingUtilities.isEventDispatchThread() == true)
		{
		    collapsePath(new TreePath(nodeToCollapse.getPath()));
		}
		else
		{
			try
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						collapsePath(new TreePath(nodeToCollapse.getPath()));
					}
				});
			}
			catch(Exception exception)
			{
				// Aucune chance que cette exception n'arrive
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: nodeStructureChanged
	*
	* Description:
	* Cette méthode redéfini celle de l'interface TreeInterface. Elle est
	* appelée lorsque la structure d'un noeud graphique a changé (dû à l'ajout
	* et à la suppression de noeuds enfants).
	*
	* Arguments:
	*  - node: Une référence sur le noeud graphique dont la structure a changé.
	* ----------------------------------------------------------*/
	public void nodeStructureChanged(
		final DefaultMutableTreeNode node
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "nodeStructureChanged");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("node=" + node);
		if(SwingUtilities.isEventDispatchThread() == true)
		{
		    ((DefaultTreeModel)getModel()).nodeStructureChanged(node);
		}
		else
		{
			try
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						((DefaultTreeModel)getModel()).nodeStructureChanged(
							node);
					}
				});
			}
			catch(Exception exception)
			{
				// Aucune chance que cette exception n'arrive
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isNodeExpanded
	*
	* Description:
	* Cette méthode redéfini celle de l'interface TreeInterface. Elle est
	* appelée afin de savoir si le noeud passé en argument est étendu ou non.
	*
	* Arguments:
	*  - node: Une référence sur le noeud graphique dont on souhaite connaître
	*    l'état d'expansion.
	*
	* Retourne: true si le noeud est étendu, false sinon.
	* ----------------------------------------------------------*/
	public boolean isNodeExpanded(
		DefaultMutableTreeNode node
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "isNodeExpanded");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("node=" + node);
		trace_methods.endOfMethod();
		return isExpanded(new TreePath(node.getPath()));
	}

	/*----------------------------------------------------------
	* Nom: valueChanged
	*
	* Description:
	* Cette méthode redéfini celle de l'interface TreeSelectionListener. Elle
	* est appelée lorsqu'un noeud a été sélectionné, ou déselectionné.
	* A l'heure actuelle, cette méthode n'effectue aucun traitement particulier.
	*
	* Argument:
	*  - event: Une référence sur une instance de TreeSelectionEvent contenant
	*    les informations relatives à la sélection.
	* ----------------------------------------------------------*/
	public void valueChanged(
		TreeSelectionEvent event
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "valueChanged");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getWindowInterface
	*
	* Description:
	* Cette méthode permet de récupérer une référence sur l'interface
	* MainWindowInterface. Cette référence est utilisée afin d'exécuter les
	* processeurs de tâche.
	*
	* Retourne: Une référence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public MainWindowInterface getWindowInterface()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "getWindowInterface");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _windowInterface;
	}

	/*----------------------------------------------------------
	* Nom: cleanBeforeExit
	*
	* Description:
	* Cette méthode permet de libérer les ressources allouées. Elle doit être
	* appelée lors de la fermeture de la fenêtre principale, à l'arrêt de
	* l'application.
	* ----------------------------------------------------------*/
	public void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowTree", "cleanBeforeExit");

		trace_methods.beginningOfMethod();
		_windowInterface = null;
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _windowInterface
	*
	* Description:
	* Cet attribut maintient une référence sur l'interface MainWindowInterface.
	* Il est nécessaire pour exécuter le processeur d'expansion, et pour
	* interagir avec la fenêtre principale.
	* ----------------------------------------------------------*/
	private MainWindowInterface _windowInterface;
}