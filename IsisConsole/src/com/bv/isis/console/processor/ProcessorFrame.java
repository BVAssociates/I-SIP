/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/processor/ProcessorFrame.java,v $
* $Revision: 1.26 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de base des processeurs graphiques
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ProcessorFrame.java,v $
* Revision 1.26  2009/01/23 17:27:34  tz
* Prise en compte de la modification du constructeur d'objet
* ContextualMenuItem.
*
* Revision 1.25  2009/01/14 14:23:03  tz
* Prise en compte de la modification des packages.
*
* Revision 1.24  2008/08/05 15:54:05  tz
* Appel à setVisible() dans le thread AWT.
*
* Revision 1.23  2008/07/17 13:34:14  tz
* Ajustement de la taille des sous-fenêtres vis à vis de la dimension de
* la zone d'affichage uniquement pour celles qui sont redimensionnables.
*
* Revision 1.22  2008/01/31 16:42:02  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.21  2006/03/20 15:51:14  tz
* Ajout de la méthode getParameters().
*
* Revision 1.20  2005/12/23 13:25:37  tz
* Les sous-fenêtre disposent du bouton de fermeture.
* Utilisation d'un adapteur anonyme au lieu d'une interface InternalFrameListener.
*
* Revision 1.19  2005/10/12 14:25:11  tz
* Amélioration de la génération du titre de la sous-fenêtre.
*
* Revision 1.18  2005/10/07 13:39:40  tz
* Ajout du nom de l'Agent dans le titre de la fenêtre
*
* Revision 1.17  2005/10/07 08:14:54  tz
* Gestion de la fermeture des sous-fenêtres par click sur le bouton en haut à droite,
* Gestion des titres des sous-fenêtres.
*
* Revision 1.16  2005/07/01 12:02:01  tz
* Modification du composant pour les traces
*
* Revision 1.15  2004/11/23 15:37:10  tz
* Suppression de commentaires
*
* Revision 1.14  2004/11/09 15:20:20  tz
* Réactivation d'un élément de menu seulement si la condition de la
* méthode associée vaut true.
*
* Revision 1.13  2004/10/22 15:32:47  tz
* Modification pour adaptation à la nouvelle interface ProcessorInterface
*
* Revision 1.12  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.11  2004/10/13 13:52:37  tz
* Renommage du package inuit -> isis,
* Mise à jour du modèle.
*
* Revision 1.10  2004/10/06 07:28:22  tz
* Amélioration de la gestion des dimensions
*
* Revision 1.9  2004/07/29 11:59:52  tz
* Suppression d'imports inutiles
*
* Revision 1.8  2003/12/08 15:13:52  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.6.2.1  2003/10/27 16:52:06  tz
* Suppression de l'interdiction de maximisation
*
* Revision 1.7  2003/12/08 14:34:09  tz
* Suppression de la non-maximisation
*
* Revision 1.6  2003/03/17 16:50:16  tz
* Correction de la fiche Inuit/106
*
* Revision 1.5  2003/03/07 16:18:59  tz
* Les sous-fenêtres peuvent être maximisées
*
* Revision 1.4  2002/04/17 07:59:42  tz
* Positionnement des dimensions min et max des fenêtres.
*
* Revision 1.3  2002/04/05 15:50:07  tz
* Cloture itération IT1.2
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture itération IT1.0.1
*
* Revision 1.1  2001/12/12 09:58:32  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.processor;

//
// Imports système
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.help.CSH;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.ContextualMenuItem;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;
import com.bv.isis.corbacom.IsisMethod;
import com.bv.isis.corbacom.IsisNodeLabel;

/*----------------------------------------------------------
* Nom: ProcessorFrame
*
* Description:
* Cette classe abstraite est une classe de base pour les processeurs de tâche
* graphiques. Un processeur graphique est un processeur nécessitant l'affichage
* d'une sous-fenêtre dans la fenêtre principale.
* Cette classe dérive de la classe JInternalFrame et implémente l'interface
* ProcessorInterface.
*
* Elle réalise les opérations de base nécessaires à un processeur graphique
* afin qu'il puisse être affiché dans la fenêtre principale. Elle défini
* également des méthodes permettant à une classe spécialisée de récupérer les
* références de l'interface MainWindowInterface, et du noeud sélectionné. Elle
* permet également de modifier l'état de l'item de menu par lequel le processeur
* a été lancé.
*
* Une classe d'implémentation d'un processeur graphique doit au moins redéfinir 
* les méthodes run(), close(), getDescription(), duplicate().
* ----------------------------------------------------------*/
public abstract class ProcessorFrame
	extends JInternalFrame
	implements
		ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: ProcessorFrame
	*
	* Description:
	* Cette méthode est le seul constructeur de la classe. Elle permet de
	* construire une instance de JInternalFrame avec des attributs par défaut
	* (redimensionnable, iconifiable, maximisable).
	* 
	* Arguments:
	*  - closable: Un booléen indiquant si la sous-fenêtre doit pouvoir être 
	*    fermée par son menu système (true) ou non (false).
 	* ----------------------------------------------------------*/
	public ProcessorFrame(
		boolean closable
		)
	{
		// La sous-fenêtre doit pouvoir être redimensionnée, iconifiée,
		// maximisée.
		super("", true, closable, true, true);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "ProcessorFrame");

		trace_methods.beginningOfMethod();
		// On définit la méthode de fermeture par défaut => rien
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		// Poisitionnement de l'écouteur d'événements de sous-fenêtres
		addInternalFrameListener(new InternalFrameAdapter()
		{
			public void internalFrameClosing(InternalFrameEvent event)
			{
				// On appelle la méthode close()
				close();
			}
		});
		// On positionne la couleur d'arrière plan par défaut (celle d'un
		// bouton)
		JButton button = new JButton();
		setBackground(button.getBackground());
		// On positionne l'identifiant pour l'aide en ligne
		String help_id = getClass().getName();
		help_id = help_id.substring(help_id.lastIndexOf('.') + 1);
		CSH.setHelpIDString(this, help_id);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle est
	* appelée par le ProcessManager afin d'exécuter le processeur.
	* Cette méthode doit être redéfinie dans la classe spécialisée, mais doit
	* appeler celle-ci en premier. Exemple:
	*   ...
	*   public void run(
	* 	    MainWindowInterface windowInterface,
	* 	    JMenuItem menuItem,
	* 	    String parameters,
	*       String preprocessing,
	*       String postprocessing,
	* 	    DefaultMutableTreeNode selectedNode
	* 	    ) throws InnerException
	*   {
	* 	    // La première chose est d'appeler la méthode run de la
	*       // super-classe
	* 	    super.run(windowInterface, menuItem, parameters,
	*           preprocessing, postprocessing, selectedNode);
	* 	    // Traitement spécifique
	* 	    ...
	*		// Une fois que le traitement est fait, on déclenche l'affichage
	*		display();
	*   }
	*   ...
	*
	* Les arguments windowInterface, menuItem et selectedNode sont conservés en
	* mémoire par cette classe. Ils ne doivent pas être également conservés par
	* la classe spécialisée.
	*
	* Si un problème est détecté durant la phase d'exécution, l'exception
	* InnerException doit être levée.
	*
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface
	*    permettant au processeur d'interagir avec la fenêtre principale,
	*  - menuItem: Une référence sur l'objet JMenuItem par lequel le processeur
	*    a été exécuté. Cet argument peut être nul,
	*  - parameters: Une chaîne de caractère contenant des paramètres spécifiques
	*    au processeur. Cet argument peut être nul,
	*  - preprocessing: Une chaîne de caractères contenant des instructions de
	*    préprocessing. Cet argument peut être nul,
	*  - postprocessing: Une chaîne de caractères contenant des instructions 
	*    de postprocessing. Cet argument peut être nul,
	*  - selectedNode: Une référence sur le noeud sélectionné. Cet argument peut
	*    être nul.
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
			"ProcessorFrame", "run");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("windowInterface=" + windowInterface);
		trace_arguments.writeTrace("menuItem=" + menuItem);
		trace_arguments.writeTrace("parameters=" + parameters);
		trace_arguments.writeTrace("preprocessing=" + preprocessing);
		trace_arguments.writeTrace("postprocessing=" + postprocessing);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		// Vérification de la validité de l'argument windowInterface
		if(windowInterface == null)
		{
			// Cela ne devrait pas arriver
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// Enregistrement des références
		_windowInterface = windowInterface;
		_menuItem = menuItem;
		_selectedNode = selectedNode;
		_parameters = parameters;
		// Désactivation de l'item de menu
		setMenuItemState(false);
		// Si l'item de menu est non null, on utilise son icône comme icône
		// de fenêtre par défaut
		if(_menuItem != null)
		{
			setFrameIcon(_menuItem.getIcon());
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getMainWindowInterface
	*
	* Description:
	* Cette méthode permet de récupérer la référence sur l'interface
	* MainWindowInterface, telle qu'elle a été fournie à la méthode
	* run().
	*
	* Retourne: Une référence sur l'interface MainWindowInterface.
	* ----------------------------------------------------------*/
	public final MainWindowInterface getMainWindowInterface()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "getMainWindowInterface");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _windowInterface;
	}

	/*----------------------------------------------------------
	* Nom: getSelectedNode
	*
	* Description:
	* Cette méthode permet de récupérer une référence sur le noeud graphique
	* sélectionné lors de l'appel du processeur. Si aucun noeud n'a été
	* sélectionné, ou si le processeur est un processeur global, cette méthode
	* retournera null.
	*
	* Retourne: Une référence sur un objet DefaultMutableTreeNode correspondant
	* au noeud sélectionné, ou null.
	* ----------------------------------------------------------*/
	public final DefaultMutableTreeNode getSelectedNode()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "getSelectedNode");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _selectedNode;
	}

	/*----------------------------------------------------------
	* Nom: setMenuItemState
	*
	* Description:
	* Cette méthode permet de modifier l'état de l'item de menu qui a permis
	* l'exécution du processeur. Par défaut, cet item est désactivé par la
	* méthode run() de la classe. Il n'est donc pas nécessaire de le
	* désactiver une nouvelle fois dans la classe spécialisée.
	*
	* Arguments:
	*  - enabled: Un booléen indiquant si l'item doit être activé (true) ou
	*    désactivé (false).
	* ----------------------------------------------------------*/
	public final void setMenuItemState(
		boolean enabled
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "setMenuItemState");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("enabled=" + enabled);
		// Si l'item de menu existe, on fixe son état
		if(_menuItem != null)
		{
			// L'item de menu est-il de type ContextualMenuItem ?
			if(_menuItem instanceof ContextualMenuItem)
			{
				ContextualMenuItem contextual_item = 
					(ContextualMenuItem)_menuItem;
				// Suivant que enabled vaut true ou false, on désassocie ou
				// on associe respectivement le processeur à l'élément de
				// menu
				if(enabled == false)
				{
					contextual_item.attachProcessor(this); 
					_menuItem.setEnabled(enabled);
				}
				else
				{
					contextual_item.detachProcessor();
					// On vérifie que l'on peut activer l'élément
					IsisMethod method = new IsisMethod();
					method.condition = true;
					ContextualMenuItem item = 
						new ContextualMenuItem(null, method, false);
					if(contextual_item.isSameCondition(item) == true)
					{
						_menuItem.setEnabled(enabled);
					}
				}
			}
			else
			{
				_menuItem.setEnabled(enabled);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: display
	*
	* Description:
	* Cette méthode permet à une classe d'implémentation d'un processeur
	* graphique (spécialisation cette classe) d'ajouter sa sous-fenêtre dans la
	* zone d'affichage correspondante. Cette méthode doit être appelée par la
	* sous-classe uniquement lorsque la fenêtre doit être ajoutée (une fois que
	* toutes les initialisation sont faites, voir la description de la méthode
	* run()).
	* ----------------------------------------------------------*/
	public final void display()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "display");
		int width;
		int height;

		trace_methods.beginningOfMethod();
		// On vérifie que la sous-fenêtre a les bonnes dimensions
		Dimension desktop_size = _windowInterface.getDesktopPane().getSize();
		Dimension preferred_size = getPreferredSize();
		if(preferred_size.width < 200)
		{
			width = 200;
		}
		else if(preferred_size.width > desktop_size.width - 15 &&
			isResizable() == true)
		{
			width = desktop_size.width - 15;
		}
		else
		{
			width = preferred_size.width;
		}
		if(preferred_size.height < 150)
		{
			height = 150;
		}
		else if(preferred_size.height > desktop_size.height - 15 &&
			isResizable() == true)
		{
			height = desktop_size.height - 15;
		}
		else
		{
			height = preferred_size.height;
		}
		// On positionne la dimension de la fenêtre
		setSize(width, height);
		// On ajoute la sous-fenêtre dans la zone d'affichage
		_windowInterface.getDesktopPane().add(this);
		// On affiche la sous-fenêtre
		show();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Cette 
	* méthode peut être appelée pour fermer les processeurs graphiques ouverts 
	* lors de la fermeture de la fenêtre principale, par exemple.
	* Elle doit être redéfinie dans la classe spécialisée afin d'effectuer les 
	* opérations nécessaires à la fermeture (libération des ressources).
	* La méthode de la classe spécialisée doit appeler la méthode de cette 
	* classe afin de détruire la sous-fenêtre. Cet appel doit être effectué à 
	* la fin de la méthode close() de la sous-classe.
	* Exemple:
	*   ...
	*   public void close()
	*   {
	*	    // Traitement spécifique
	*	    ...
	*	    // Appel de la méthode de la super-classe
	*	    super.close();
	*   }
	*   ...
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "close");

		trace_methods.beginningOfMethod();
		// On masque la sous-fenêtre
		// On s'assure d'être dans le thread de gestion des événements
		// graphiques
		if(SwingUtilities.isEventDispatchThread() == true) {
			setVisible(false);
		}
		else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						setVisible(false);
					}
				});
			}
			catch(Exception exception) {
				Trace trace_errors = TraceAPI.declareTraceErrors("Console");
				
				trace_errors.writeTrace("Erreur lors de l'invocation " +
					"dans le thread de gestion des événements : " +
					exception);
			}
		}
		// On réactive l'item de menu
		setMenuItemState(true);
		// On supprime les références
		_windowInterface = null;
		_menuItem = null;
		_selectedNode = null;
		// On la détruit.
		dispose();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setFrameIcon
	*
	* Description:
	* Cette méthode permet de positionner une icône passée en argument pour la
	* sous-fenêtre. Si l'icône est de type ImageIcon, un clone de celle-ci sera
	* créé afin de ne pas dénaturer l'icône d'origine.
	* En effet, si l'icône utilisée n'est pas aux bonnes dimensions, elle sera
	* redimensionnée automatiquement, ce qui pourrait poser des problèmes.
	*
	* Arguments:
	*  - icon: L'icône à placer dans la barre de titre de la sous-fenêtre.
	* ----------------------------------------------------------*/
	public final void setFrameIcon(Icon icon)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
		    "ProcessorFrame", "setFrameIcon");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("icon=" + icon);
		if(icon != null && icon instanceof ImageIcon)
		{
			// On va créer un clone de l'icône, de sorte à ne pas modifier
			// l'original (la taille de l'icône peut être adaptée pour
			// être affiché dans la barre de titre)
			ImageIcon image_icon = (ImageIcon)icon;
			Icon frame_icon = new ImageIcon(image_icon.getImage());
			super.setFrameIcon(frame_icon);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* permet de définir un comportement par défaut pour les processeurs 
	* graphiques.
	* La méthode ne fait aucune action.
	* ----------------------------------------------------------*/
	public void preLoad()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "preLoad");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* permet de définir un comportement par défaut pour les processeurs 
	* graphiques.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isConfigured()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "isConfigured");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* permet de définir un comportement par défaut pour les processeurs 
	* graphiques.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public ConfigurationPanelInterface[] getConfigurationPanels()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "getConfigurationPanels");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* permet de définir un comportement par défaut pour les processeurs 
	* graphiques.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "isTreeCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* permet de définir un comportement par défaut pour les processeurs 
	* graphiques.
	* 
	* Retourne: true.
	* ----------------------------------------------------------*/
	public boolean isTableCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "isTableCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return true;
	}

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* permet de définir un comportement par défaut pour les processeurs 
	* graphiques.
	* 
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "isGlobalCapable");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode redéfinit celle de l'interface ProcessorInterface. Elle 
	* permet de définir un comportement par défaut pour les processeurs 
	* graphiques.
	* 
	* Retourne: null.
	* ----------------------------------------------------------*/
	public String getMenuLabel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "getMenuLabel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return null;
	}

	/*----------------------------------------------------------
	* Nom: setTitle
	* 
	* Description:
	* Cette méthode permet de positionner l'intitulé de la sous-fenêtre.
	* Elle ajoute automatiquement au titre passé en argument le libellé du 
	* noeud concerné par la sous-fenêtre, si celui-ci existe, entre 
	* parenthèses.
	* Si le libellé du noeud est déjà contenu dans le titre passé en 
	* argument, il n'est pas ajouté.
	* 
	* Arguments:
	*  - title: Le titre de la sous-fenêtre.
 	* ----------------------------------------------------------*/
 	public void setTitle(
 		String title
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "internalFrameOpened");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String full_title = title;
		String label = null;
		String agent_name = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("title=" + title);
		title = null;
 		if(_selectedNode != null)
 		{
 			trace_debug.writeTrace("Il y a un noeud associé");
			GenericTreeObjectNode object_node = 
				(GenericTreeObjectNode)_selectedNode;
			// On récupère l'objet IsisNodeLabel associé au noeud
			IsisNodeLabel node_label = object_node.getLabel();
			// S'il n'y a pas de libellé, on va en construire un
			if(node_label == null)
			{
				if(object_node instanceof GenericTreeClassNode)
				{
					// Pour un noeud de type GenericTreeClassNode, on prend le
					// nom de la table
					label = object_node.getTableName();
				}
				else
				{
					// Pour les autres noeuds, on prend la valeur de la clé
					label = object_node.getKey();
				}
			}
			else
			{
				label = node_label.label;
			}
			agent_name = object_node.getAgentName();
			// On va regarder s'il faut ajouter le libellé du noeud
			if(label != null && full_title.indexOf(label) == -1)
			{
				trace_debug.writeTrace(
					"Le libellé du noeud n'est pas dans le titre");
				// Le libellé du noeud n'est pas dans le titre, on va
				// l'ajouter
				title = label;
			}
			// On va regarder s'il faut ajouter le nom de l'Agent
			if(agent_name != null && full_title.indexOf(agent_name) == -1)
			{
				trace_debug.writeTrace(
					"Le nom de l'Agent n'est pas dans le titre");
				if(title != null && title.equals("") == false &&
					title.indexOf(agent_name) == -1)
				{
					// Le nom de l'Agent n'est pas dans le titre, ni dans le
					// libellé du noeud, on va l'ajouter
					title = title + " - " + agent_name;
				}
				else if(title == null || title.equals("") == true)
				{
					// Le nom de l'Agent n'est pas dans le titre et il n'y a
					// pas de libellé du noeud
					title = agent_name;
				}
			}
			// S'il y a quelque chose à ajouter au titre, on le fait
			if(title != null && title.equals("") == false)
			{
				full_title = full_title + " (" + title + ")";
			}
 		}
 		trace_debug.writeTrace("full_title=" + full_title);
 		// On appelle la méthode de même nom des niveaux supérieurs
 		super.setTitle(full_title);
		trace_methods.endOfMethod();
 	}


	/*----------------------------------------------------------
	* Nom: getParameters
	* 
	* Description:
	* Cette méthode permet de récupérer la chaîne représentant les paramètres 
	* d'exécution du processeur.
	* 
	* Retourne: Les paramètres d'exécution du processeur.
	* ----------------------------------------------------------*/
	public String getParameters()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "getParameters");
			
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _parameters;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _windowInterface
	*
	* Description:
	* Cet attribut maintient une référence sur l'interface MainWindowInterface
	* qui a été passée en argument à la méthode run().
	* ----------------------------------------------------------*/
	private MainWindowInterface _windowInterface;

	/*----------------------------------------------------------
	* Nom: _selectedNode
	*
	* Description:
	* Cet attribut maintient une référence sur l'objet DefaultMutableTreeNode
	* qui a été passé en argument à la méthode run().
	* ----------------------------------------------------------*/
	private DefaultMutableTreeNode _selectedNode;

	/*----------------------------------------------------------
	* Nom: _menuItem
	*
	* Description:
	* Cet attribut maintient une référence sur l'objet JMenuItem qui a été
	* passé en argument à la méthode run().
	* ----------------------------------------------------------*/
	private JMenuItem _menuItem;

	/*----------------------------------------------------------
	* Nom: _parameters
	* 
	* Description:
	* Cet attribut maintient la chaîne représentant les paramètres d'exécution 
	* du processeur.
	* ----------------------------------------------------------*/
	String _parameters;
}
