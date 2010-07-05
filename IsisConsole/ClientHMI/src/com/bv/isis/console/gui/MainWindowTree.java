/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/MainWindowTree.java,v $
* $Revision: 1.13 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation de l'arbre d'exploration
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
* R�glage de la hauteur des lignes en fonction de la taille des ic�nes.
*
* Revision 1.11  2005/07/01 12:24:19  tz
* Modification du composant pour les traces
*
* Revision 1.10  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.9  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.8  2004/10/06 07:39:48  tz
* R�-affichage du contenu de l'arbre en diff�r�.
*
* Revision 1.7  2004/07/29 12:19:24  tz
* Suppression d'imports inutiles
*
* Revision 1.6  2003/03/07 16:22:04  tz
* Ajout de l'auto-exploration
*
* Revision 1.5  2002/04/05 15:47:21  tz
* Cloture it�ration IT1.2
*
* Revision 1.4  2002/03/27 09:42:06  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.3  2002/02/04 10:54:24  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.2  2001/12/19 09:58:49  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1  2001/11/19 17:07:54  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.gui;

//
// Imports syst�me
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
* Cette classe repr�sente l'arbre de navigation charg� de repr�senter les objets
* de production des plates-formes. Elle est une sp�cialisation de la classe
* JTree, en impl�mentant l'interface TreeSelectionListener.
* Cette sp�cialisation permet d'ajouter un comportement particulier lors de
* l'�v�nement suivant:
*  - S�lection d'un noeud.
*
* Cette classe impl�mente �galement l'interface TreeInterface, permettant de
* mettre � jour l'arbre lors de l'ajout ou de la suppression de noeuds.
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
	* Cette m�thode est le seul constructeur de la classe. Elle permet de
	* fournir une r�f�rence sur l'interface MainWindowInterface.
	* Lors de sa construction, l'arbre va r�gler des param�tres d'affichage et
	* de s�lection. Il va �galement s'assigner un gestionnaire de rendu de
	* cellule sp�cialis� via une instance de IsisTreeCellRenderer, et un
	* gestionnaire de menu popup, via une instance de TreePopupTrigger.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface,
	*  - rootNode: Une r�f�rence sur l'objet graphique racine de l'arbre.
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
		// R�cup�ration de la taille des ic�nes depuis la configuration
		try {
			ConfigurationAPI configuration = new ConfigurationAPI();
			icons_size = configuration.getInt("GUI", "NodeIcons.Size");
		}
		catch(Exception e) {
			// On ne fait rien
		}
		// Cr�ation du "trigger" et assignation
		TreePopupTrigger trigger = new TreePopupTrigger();
		addMouseListener(trigger);
		// Ajout de "this" en tant que TreeSelectionListener
		addTreeSelectionListener(this);
		// Cr�ation du gestionnaire de rendu et assignation
		IsisTreeCellRenderer renderer = new IsisTreeCellRenderer();
		setCellRenderer(renderer);
		// R�glage des propri�t�s de l'arbre
		// R�glage des indentations
		BasicTreeUI tree_ui = (BasicTreeUI)getUI();
		tree_ui.setRightChildIndent(10);
		tree_ui.setLeftChildIndent(6);
		// R�glage de la hauteur d'un rang
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
	* Cette m�thode r�d�finit celle de l'interface TreeInterface. Elle est
	* appel�e lorsque des noeuds ont �t� ajout�s � un noeud graphique.
	* La m�thode informe le mod�le de donn�es de l'arbre que des noeuds ont �t�
	* ajout�s, afin de mettre � jour l'arbre.
	*
	* Arguments:
	*  - parent: Une r�f�rence sur une instance de DefaultMutableTreeNode
	*    correspond au noeud auquel ont �t� ajout�s les nouveaux noeuds,
	*  - indexes: Un tableau d'entier indiquant les indexes des noeuds qui ont
	*    �t� ajout�s.
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
		// On doit informer le mod�le de donn�es du changement.
		// R�cup�ration du mod�le (de type standard)
		final DefaultTreeModel model = (DefaultTreeModel)getModel();
		if(SwingUtilities.isEventDispatchThread() == true)
		{
			// Notification du mod�le du changement
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
						// Notification du mod�le du changement
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
	* Cette m�thode r�d�finit celle de l'interface TreeInterface. Elle est
	* appel�e lorsque des noeuds ont �t� supprim�s d'un noeud graphique.
	* La m�thode informe le mod�le de donn�es de l'arbre que des noeuds ont �t�
	* supprim�s, afin de mettre � jour l'arbre.
	*
	* Arguments:
	*  - parent: Une r�f�rence sur une instance de DefaultMutableTreeNode
	*    correspond au noeud duquel ont �t� supprim�s les nouveaux noeuds,
	*  - indexes: Un tableau d'entier indiquant les indexes des noeuds qui ont
	*    �t� supprim�s,
	*  - removedNodes: Un tableau d'instances de DefaultMutableTreeNode
	*    correspondants aux noeuds qui ont �t� supprim�s.
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
		// On doit informer le mod�le de donn�es du changement.
		// R�cup�ration du mod�le (de type standard)
		final DefaultTreeModel model = (DefaultTreeModel)getModel();
		if(SwingUtilities.isEventDispatchThread() == true)
		{
			// Notification du mod�le du changement
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
						// Notification du mod�le du changement
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
	* Cette m�thode r�d�finit celle de l'interface TreeInterface. Elle est
	* appel�e lorsqu'un noeud doit �tre �tendu.
	* La m�thode appelle la m�thode expandPath() d�finie dans la classe JTree.
	*
	* Arguments:
	*  - nodeToExpand: Une r�f�rence sur le noeud graphique � �tendre.
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
	* Cette m�thode r�d�finit celle de l'interface TreeInterface. Elle est
	* appel�e lorsqu'un noeud doit �tre r�duit.
	* La m�thode appelle la m�thode collapsePath() d�finie dans la classe JTree.
	*
	* Arguments:
	*  - nodeToCollapse: Une r�f�rence sur le noeud graphique � �tendre.
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
	* Cette m�thode red�fini celle de l'interface TreeInterface. Elle est
	* appel�e lorsque la structure d'un noeud graphique a chang� (d� � l'ajout
	* et � la suppression de noeuds enfants).
	*
	* Arguments:
	*  - node: Une r�f�rence sur le noeud graphique dont la structure a chang�.
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
	* Cette m�thode red�fini celle de l'interface TreeInterface. Elle est
	* appel�e afin de savoir si le noeud pass� en argument est �tendu ou non.
	*
	* Arguments:
	*  - node: Une r�f�rence sur le noeud graphique dont on souhaite conna�tre
	*    l'�tat d'expansion.
	*
	* Retourne: true si le noeud est �tendu, false sinon.
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
	* Cette m�thode red�fini celle de l'interface TreeSelectionListener. Elle
	* est appel�e lorsqu'un noeud a �t� s�lectionn�, ou d�selectionn�.
	* A l'heure actuelle, cette m�thode n'effectue aucun traitement particulier.
	*
	* Argument:
	*  - event: Une r�f�rence sur une instance de TreeSelectionEvent contenant
	*    les informations relatives � la s�lection.
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
	* Cette m�thode permet de r�cup�rer une r�f�rence sur l'interface
	* MainWindowInterface. Cette r�f�rence est utilis�e afin d'ex�cuter les
	* processeurs de t�che.
	*
	* Retourne: Une r�f�rence sur l'interface MainWindowInterface.
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
	* Cette m�thode permet de lib�rer les ressources allou�es. Elle doit �tre
	* appel�e lors de la fermeture de la fen�tre principale, � l'arr�t de
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
	* Cet attribut maintient une r�f�rence sur l'interface MainWindowInterface.
	* Il est n�cessaire pour ex�cuter le processeur d'expansion, et pour
	* interagir avec la fen�tre principale.
	* ----------------------------------------------------------*/
	private MainWindowInterface _windowInterface;
}