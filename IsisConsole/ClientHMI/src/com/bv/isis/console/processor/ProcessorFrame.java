/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
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
* Appel � setVisible() dans le thread AWT.
*
* Revision 1.23  2008/07/17 13:34:14  tz
* Ajustement de la taille des sous-fen�tres vis � vis de la dimension de
* la zone d'affichage uniquement pour celles qui sont redimensionnables.
*
* Revision 1.22  2008/01/31 16:42:02  tz
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.21  2006/03/20 15:51:14  tz
* Ajout de la m�thode getParameters().
*
* Revision 1.20  2005/12/23 13:25:37  tz
* Les sous-fen�tre disposent du bouton de fermeture.
* Utilisation d'un adapteur anonyme au lieu d'une interface InternalFrameListener.
*
* Revision 1.19  2005/10/12 14:25:11  tz
* Am�lioration de la g�n�ration du titre de la sous-fen�tre.
*
* Revision 1.18  2005/10/07 13:39:40  tz
* Ajout du nom de l'Agent dans le titre de la fen�tre
*
* Revision 1.17  2005/10/07 08:14:54  tz
* Gestion de la fermeture des sous-fen�tres par click sur le bouton en haut � droite,
* Gestion des titres des sous-fen�tres.
*
* Revision 1.16  2005/07/01 12:02:01  tz
* Modification du composant pour les traces
*
* Revision 1.15  2004/11/23 15:37:10  tz
* Suppression de commentaires
*
* Revision 1.14  2004/11/09 15:20:20  tz
* R�activation d'un �l�ment de menu seulement si la condition de la
* m�thode associ�e vaut true.
*
* Revision 1.13  2004/10/22 15:32:47  tz
* Modification pour adaptation � la nouvelle interface ProcessorInterface
*
* Revision 1.12  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.11  2004/10/13 13:52:37  tz
* Renommage du package inuit -> isis,
* Mise � jour du mod�le.
*
* Revision 1.10  2004/10/06 07:28:22  tz
* Am�lioration de la gestion des dimensions
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
* Les sous-fen�tres peuvent �tre maximis�es
*
* Revision 1.4  2002/04/17 07:59:42  tz
* Positionnement des dimensions min et max des fen�tres.
*
* Revision 1.3  2002/04/05 15:50:07  tz
* Cloture it�ration IT1.2
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.1  2001/12/12 09:58:32  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.processor;

//
// Imports syst�me
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
* Cette classe abstraite est une classe de base pour les processeurs de t�che
* graphiques. Un processeur graphique est un processeur n�cessitant l'affichage
* d'une sous-fen�tre dans la fen�tre principale.
* Cette classe d�rive de la classe JInternalFrame et impl�mente l'interface
* ProcessorInterface.
*
* Elle r�alise les op�rations de base n�cessaires � un processeur graphique
* afin qu'il puisse �tre affich� dans la fen�tre principale. Elle d�fini
* �galement des m�thodes permettant � une classe sp�cialis�e de r�cup�rer les
* r�f�rences de l'interface MainWindowInterface, et du noeud s�lectionn�. Elle
* permet �galement de modifier l'�tat de l'item de menu par lequel le processeur
* a �t� lanc�.
*
* Une classe d'impl�mentation d'un processeur graphique doit au moins red�finir 
* les m�thodes run(), close(), getDescription(), duplicate().
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
	* Cette m�thode est le seul constructeur de la classe. Elle permet de
	* construire une instance de JInternalFrame avec des attributs par d�faut
	* (redimensionnable, iconifiable, maximisable).
	* 
	* Arguments:
	*  - closable: Un bool�en indiquant si la sous-fen�tre doit pouvoir �tre 
	*    ferm�e par son menu syst�me (true) ou non (false).
 	* ----------------------------------------------------------*/
	public ProcessorFrame(
		boolean closable
		)
	{
		// La sous-fen�tre doit pouvoir �tre redimensionn�e, iconifi�e,
		// maximis�e.
		super("", true, closable, true, true);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "ProcessorFrame");

		trace_methods.beginningOfMethod();
		// On d�finit la m�thode de fermeture par d�faut => rien
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		// Poisitionnement de l'�couteur d'�v�nements de sous-fen�tres
		addInternalFrameListener(new InternalFrameAdapter()
		{
			public void internalFrameClosing(InternalFrameEvent event)
			{
				// On appelle la m�thode close()
				close();
			}
		});
		// On positionne la couleur d'arri�re plan par d�faut (celle d'un
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle est
	* appel�e par le ProcessManager afin d'ex�cuter le processeur.
	* Cette m�thode doit �tre red�finie dans la classe sp�cialis�e, mais doit
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
	* 	    // La premi�re chose est d'appeler la m�thode run de la
	*       // super-classe
	* 	    super.run(windowInterface, menuItem, parameters,
	*           preprocessing, postprocessing, selectedNode);
	* 	    // Traitement sp�cifique
	* 	    ...
	*		// Une fois que le traitement est fait, on d�clenche l'affichage
	*		display();
	*   }
	*   ...
	*
	* Les arguments windowInterface, menuItem et selectedNode sont conserv�s en
	* m�moire par cette classe. Ils ne doivent pas �tre �galement conserv�s par
	* la classe sp�cialis�e.
	*
	* Si un probl�me est d�tect� durant la phase d'ex�cution, l'exception
	* InnerException doit �tre lev�e.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface
	*    permettant au processeur d'interagir avec la fen�tre principale,
	*  - menuItem: Une r�f�rence sur l'objet JMenuItem par lequel le processeur
	*    a �t� ex�cut�. Cet argument peut �tre nul,
	*  - parameters: Une cha�ne de caract�re contenant des param�tres sp�cifiques
	*    au processeur. Cet argument peut �tre nul,
	*  - preprocessing: Une cha�ne de caract�res contenant des instructions de
	*    pr�processing. Cet argument peut �tre nul,
	*  - postprocessing: Une cha�ne de caract�res contenant des instructions 
	*    de postprocessing. Cet argument peut �tre nul,
	*  - selectedNode: Une r�f�rence sur le noeud s�lectionn�. Cet argument peut
	*    �tre nul.
	*
	* L�ve: InnerException.
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
		// V�rification de la validit� de l'argument windowInterface
		if(windowInterface == null)
		{
			// Cela ne devrait pas arriver
			throw new InnerException("&ERR_ProcessorInitialisationError",
				"&ERR_InvalidArgument", null);
		}
		// Enregistrement des r�f�rences
		_windowInterface = windowInterface;
		_menuItem = menuItem;
		_selectedNode = selectedNode;
		_parameters = parameters;
		// D�sactivation de l'item de menu
		setMenuItemState(false);
		// Si l'item de menu est non null, on utilise son ic�ne comme ic�ne
		// de fen�tre par d�faut
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
	* Cette m�thode permet de r�cup�rer la r�f�rence sur l'interface
	* MainWindowInterface, telle qu'elle a �t� fournie � la m�thode
	* run().
	*
	* Retourne: Une r�f�rence sur l'interface MainWindowInterface.
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
	* Cette m�thode permet de r�cup�rer une r�f�rence sur le noeud graphique
	* s�lectionn� lors de l'appel du processeur. Si aucun noeud n'a �t�
	* s�lectionn�, ou si le processeur est un processeur global, cette m�thode
	* retournera null.
	*
	* Retourne: Une r�f�rence sur un objet DefaultMutableTreeNode correspondant
	* au noeud s�lectionn�, ou null.
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
	* Cette m�thode permet de modifier l'�tat de l'item de menu qui a permis
	* l'ex�cution du processeur. Par d�faut, cet item est d�sactiv� par la
	* m�thode run() de la classe. Il n'est donc pas n�cessaire de le
	* d�sactiver une nouvelle fois dans la classe sp�cialis�e.
	*
	* Arguments:
	*  - enabled: Un bool�en indiquant si l'item doit �tre activ� (true) ou
	*    d�sactiv� (false).
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
		// Si l'item de menu existe, on fixe son �tat
		if(_menuItem != null)
		{
			// L'item de menu est-il de type ContextualMenuItem ?
			if(_menuItem instanceof ContextualMenuItem)
			{
				ContextualMenuItem contextual_item = 
					(ContextualMenuItem)_menuItem;
				// Suivant que enabled vaut true ou false, on d�sassocie ou
				// on associe respectivement le processeur � l'�l�ment de
				// menu
				if(enabled == false)
				{
					contextual_item.attachProcessor(this); 
					_menuItem.setEnabled(enabled);
				}
				else
				{
					contextual_item.detachProcessor();
					// On v�rifie que l'on peut activer l'�l�ment
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
	* Cette m�thode permet � une classe d'impl�mentation d'un processeur
	* graphique (sp�cialisation cette classe) d'ajouter sa sous-fen�tre dans la
	* zone d'affichage correspondante. Cette m�thode doit �tre appel�e par la
	* sous-classe uniquement lorsque la fen�tre doit �tre ajout�e (une fois que
	* toutes les initialisation sont faites, voir la description de la m�thode
	* run()).
	* ----------------------------------------------------------*/
	public final void display()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "display");
		int width;
		int height;

		trace_methods.beginningOfMethod();
		// On v�rifie que la sous-fen�tre a les bonnes dimensions
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
		// On positionne la dimension de la fen�tre
		setSize(width, height);
		// On ajoute la sous-fen�tre dans la zone d'affichage
		_windowInterface.getDesktopPane().add(this);
		// On affiche la sous-fen�tre
		show();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Cette 
	* m�thode peut �tre appel�e pour fermer les processeurs graphiques ouverts 
	* lors de la fermeture de la fen�tre principale, par exemple.
	* Elle doit �tre red�finie dans la classe sp�cialis�e afin d'effectuer les 
	* op�rations n�cessaires � la fermeture (lib�ration des ressources).
	* La m�thode de la classe sp�cialis�e doit appeler la m�thode de cette 
	* classe afin de d�truire la sous-fen�tre. Cet appel doit �tre effectu� � 
	* la fin de la m�thode close() de la sous-classe.
	* Exemple:
	*   ...
	*   public void close()
	*   {
	*	    // Traitement sp�cifique
	*	    ...
	*	    // Appel de la m�thode de la super-classe
	*	    super.close();
	*   }
	*   ...
	* ----------------------------------------------------------*/
	public void close()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"ProcessorFrame", "close");

		trace_methods.beginningOfMethod();
		// On masque la sous-fen�tre
		// On s'assure d'�tre dans le thread de gestion des �v�nements
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
					"dans le thread de gestion des �v�nements : " +
					exception);
			}
		}
		// On r�active l'item de menu
		setMenuItemState(true);
		// On supprime les r�f�rences
		_windowInterface = null;
		_menuItem = null;
		_selectedNode = null;
		// On la d�truit.
		dispose();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: setFrameIcon
	*
	* Description:
	* Cette m�thode permet de positionner une ic�ne pass�e en argument pour la
	* sous-fen�tre. Si l'ic�ne est de type ImageIcon, un clone de celle-ci sera
	* cr�� afin de ne pas d�naturer l'ic�ne d'origine.
	* En effet, si l'ic�ne utilis�e n'est pas aux bonnes dimensions, elle sera
	* redimensionn�e automatiquement, ce qui pourrait poser des probl�mes.
	*
	* Arguments:
	*  - icon: L'ic�ne � placer dans la barre de titre de la sous-fen�tre.
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
			// On va cr�er un clone de l'ic�ne, de sorte � ne pas modifier
			// l'original (la taille de l'ic�ne peut �tre adapt�e pour
			// �tre affich� dans la barre de titre)
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* permet de d�finir un comportement par d�faut pour les processeurs 
	* graphiques.
	* La m�thode ne fait aucune action.
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* permet de d�finir un comportement par d�faut pour les processeurs 
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* permet de d�finir un comportement par d�faut pour les processeurs 
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* permet de d�finir un comportement par d�faut pour les processeurs 
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* permet de d�finir un comportement par d�faut pour les processeurs 
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* permet de d�finir un comportement par d�faut pour les processeurs 
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
	* Cette m�thode red�finit celle de l'interface ProcessorInterface. Elle 
	* permet de d�finir un comportement par d�faut pour les processeurs 
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
	* Cette m�thode permet de positionner l'intitul� de la sous-fen�tre.
	* Elle ajoute automatiquement au titre pass� en argument le libell� du 
	* noeud concern� par la sous-fen�tre, si celui-ci existe, entre 
	* parenth�ses.
	* Si le libell� du noeud est d�j� contenu dans le titre pass� en 
	* argument, il n'est pas ajout�.
	* 
	* Arguments:
	*  - title: Le titre de la sous-fen�tre.
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
 			trace_debug.writeTrace("Il y a un noeud associ�");
			GenericTreeObjectNode object_node = 
				(GenericTreeObjectNode)_selectedNode;
			// On r�cup�re l'objet IsisNodeLabel associ� au noeud
			IsisNodeLabel node_label = object_node.getLabel();
			// S'il n'y a pas de libell�, on va en construire un
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
					// Pour les autres noeuds, on prend la valeur de la cl�
					label = object_node.getKey();
				}
			}
			else
			{
				label = node_label.label;
			}
			agent_name = object_node.getAgentName();
			// On va regarder s'il faut ajouter le libell� du noeud
			if(label != null && full_title.indexOf(label) == -1)
			{
				trace_debug.writeTrace(
					"Le libell� du noeud n'est pas dans le titre");
				// Le libell� du noeud n'est pas dans le titre, on va
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
					// libell� du noeud, on va l'ajouter
					title = title + " - " + agent_name;
				}
				else if(title == null || title.equals("") == true)
				{
					// Le nom de l'Agent n'est pas dans le titre et il n'y a
					// pas de libell� du noeud
					title = agent_name;
				}
			}
			// S'il y a quelque chose � ajouter au titre, on le fait
			if(title != null && title.equals("") == false)
			{
				full_title = full_title + " (" + title + ")";
			}
 		}
 		trace_debug.writeTrace("full_title=" + full_title);
 		// On appelle la m�thode de m�me nom des niveaux sup�rieurs
 		super.setTitle(full_title);
		trace_methods.endOfMethod();
 	}


	/*----------------------------------------------------------
	* Nom: getParameters
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la cha�ne repr�sentant les param�tres 
	* d'ex�cution du processeur.
	* 
	* Retourne: Les param�tres d'ex�cution du processeur.
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
	* Cet attribut maintient une r�f�rence sur l'interface MainWindowInterface
	* qui a �t� pass�e en argument � la m�thode run().
	* ----------------------------------------------------------*/
	private MainWindowInterface _windowInterface;

	/*----------------------------------------------------------
	* Nom: _selectedNode
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'objet DefaultMutableTreeNode
	* qui a �t� pass� en argument � la m�thode run().
	* ----------------------------------------------------------*/
	private DefaultMutableTreeNode _selectedNode;

	/*----------------------------------------------------------
	* Nom: _menuItem
	*
	* Description:
	* Cet attribut maintient une r�f�rence sur l'objet JMenuItem qui a �t�
	* pass� en argument � la m�thode run().
	* ----------------------------------------------------------*/
	private JMenuItem _menuItem;

	/*----------------------------------------------------------
	* Nom: _parameters
	* 
	* Description:
	* Cet attribut maintient la cha�ne repr�sentant les param�tres d'ex�cution 
	* du processeur.
	* ----------------------------------------------------------*/
	String _parameters;
}
