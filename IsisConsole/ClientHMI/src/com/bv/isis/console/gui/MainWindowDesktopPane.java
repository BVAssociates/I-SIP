/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/MainWindowDesktopPane.java,v $
* $Revision: 1.15 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation de la zone d'affichage des sous-fenêtres
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MainWindowDesktopPane.java,v $
* Revision 1.15  2009/01/14 14:22:11  tz
* Prise en compte de la modification des packages.
*
* Revision 1.14  2008/10/09 13:34:05  tz
* Correction de la fiche FS#516.
*
* Revision 1.13  2008/08/05 15:51:36  tz
* Suppression de l'attribut _counter.
* Modification de la méthode de positionnement des sous-fenêtres.
*
* Revision 1.12  2005/07/01 12:24:50  tz
* Modification du composant pour les traces
*
* Revision 1.11  2004/11/23 15:44:56  tz
* Traitement du cas où la frame n'est pas visible.
*
* Revision 1.10  2004/10/22 15:44:48  tz
* Modification de la méthode de récupération du propriétaire du focus.
*
* Revision 1.9  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.8  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.7  2004/10/06 07:40:17  tz
* Décalage des frames lors de leur premier affichage.
*
* Revision 1.6  2004/07/29 12:19:37  tz
* Suppression d'imports inutiles
*
* Revision 1.5  2003/12/08 15:13:52  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.4.2.1  2003/10/27 16:56:17  tz
* Ajout de l'option de menu "Réorganiser"
*
* Revision 1.4  2002/04/17 08:00:19  tz
* Gestion des fenêtres iconifiées.
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
import javax.swing.JDesktopPane;
import javax.swing.event.InternalFrameListener;
import java.awt.Component;
import java.awt.Point;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.JCheckBoxMenuItem;
import com.bv.core.gui.KeyMapper;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.KeyboardFocusManager;
import javax.swing.JMenuItem;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.processor.ProcessorFrame;

/*----------------------------------------------------------
* Nom: MainWindowDesktopPane
*
* Description:
* Cette classe représente la zone d'affichage des sous-fenêtres. Elle étend la
* classe JDesktopPane afin d'y ajouter un comportement particulier. Elle
* implémente l'interface InternalFrameListener.
*
* Cette classe implémente un comportement particulier lors des évènements
* suivants:
*  - Une sous-fenêtre est ajoutée,
*  - Une sous-fenêtre prend le focus,
*  - Une sous-fenêtre est retirée.
*
* Cette classe gère un menu qui sera ajouté à la barre de menu de la fenêtre
* principale. Ce menu permet de gérér les sous-fenêtres (changement de fenêtre
* active...).
* ----------------------------------------------------------*/
class MainWindowDesktopPane
	extends JDesktopPane
	implements InternalFrameListener
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: MainWindowDesktopPane
	*
	* Description:
	* Cette méthode est le seul constructeur de la classe. Elle crée l'instance
	* de JMenu correspondant au menu "Fenêtres" de la fenêtre principale.
	* ----------------------------------------------------------*/
	public MainWindowDesktopPane()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "MainWindowDesktopPane");

		trace_methods.beginningOfMethod();
		rebuildMenu();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: add
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe JDesktopPane. Elle est
	* appelée lorsqu'un composant graphique est ajouté à la zone d'affichage.
	* Si le composant est une sous-fenêtre, la méthode va appeler la méthode
	* rebuildMenu() afin de reconstruire le menu "Fenêtre" pour y intégrer la
	* nouvelle sous-fenêtre.
	*
	* Arguments:
	*  - component: Une référence sur l'objet Component qui est ajouté à la
	*    zone d'affichage.
	*
	* Retourne: La référence sur le composant ajouté.
	* ----------------------------------------------------------*/
	public Component add(
		Component component
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "add");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Point frame_position = new Point(0, 0);

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("component=" + component);
		// On appelle la méthode de la super-classe
		super.add(component);
		// Le composant à ajouter doit être une sous-fenêtre
		if(component instanceof JInternalFrame)
		{
			JInternalFrame internal_frame = (JInternalFrame)component;
			// On récupère toutes les autres frames
			JInternalFrame[] frames = getAllFrames();
			// On va tester toutes les positions jusqu'à en trouver une qui ne 
			// soit pas déjà occupée
			for(int counter = 0 ; counter < 10 ; counter ++) {
				boolean current_position_used = false;
				
				frame_position.x = (counter % 5) * 15;
				frame_position.y = counter * 15;
				for(int loop = 0 ; loop < frames.length ; loop ++) {
					Point existing_frame_position = frames[loop].getLocation();
					if(existing_frame_position.equals(
						frame_position) == true) {
						// La position est déjà utilisée par au moins une 
						// sous-fenêtre, il va falloir en utiliser une autre
						current_position_used = true;
						break;
					}
				}
				if(current_position_used == false) {
					// Si la position courante n'est pas utilisée, il
					// faut positionner la sous-fenêtre à celle-ci
					break;
				}
			}
			internal_frame.setLocation(frame_position);
			// On positionne le listener
			internal_frame.addInternalFrameListener(this);
			// On reconstruit le menu
			rebuildMenu();
		}
		trace_methods.endOfMethod();
		return component;
	}

	/*----------------------------------------------------------
	* Nom: internalFrameActivated
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface InternalFrameListener. Elle
	* est appelée lorsqu'une sous-fenêtre devient active (prend le focus).
	* Elle appelle la méthode rebuildMenu() afin de mettre à jour le menu
	* "Fenêtre" de la fenêtre principale.
	*
	* Arguments:
	*  - event: Une référence sur une instance de InternalFrameEvent contenant
	*    les informations relatives à la sous-fenêtre.
	* ----------------------------------------------------------*/
	public void internalFrameActivated(
		InternalFrameEvent event
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "internalFrameActivated");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		// On reconstruit le menu
		rebuildMenu();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: internalFrameClosed
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface InternalFrameListener. Elle
	* est appelée lorsqu'une sous-fenêtre a été fermée.
	* Elle appelle la méthode rebuildMenu() afin de mettre à jour le menu
	* "Fenêtre" de la fenêtre principale.
	*
	* Arguments:
	*  - event: Une référence sur une instance de InternalFrameEvent contenant
	*    les informations relatives à la sous-fenêtre.
	* ----------------------------------------------------------*/
	public void internalFrameClosed(
		InternalFrameEvent event
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "internalFrameClosed");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		// On reconstruit le menu
		rebuildMenu();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: internalFrameClosing
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface InternalFrameListener. Elle
	* est appelée lorsqu'une sous-fenêtre est sur le point d'être fermée.
	* Elle n'a aucun comportement, elle n'est redéfinie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une référence sur une instance de InternalFrameEvent contenant
	*    les informations relatives à la sous-fenêtre.
	* ----------------------------------------------------------*/
	public void internalFrameClosing(
		InternalFrameEvent event
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "internalFrameClosing");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: internalFrameDeactivated
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface InternalFrameListener. Elle
	* est appelée lorsqu'une sous-fenêtre est sur désactivée (perd le focus).
	* Elle n'a aucun comportement, elle n'est redéfinie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une référence sur une instance de InternalFrameEvent contenant
	*    les informations relatives à la sous-fenêtre.
	* ----------------------------------------------------------*/
	public void internalFrameDeactivated(
		InternalFrameEvent event
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "internalFrameDeactivated");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: internalFrameDeiconified
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface InternalFrameListener. Elle
	* est appelée lorsqu'une sous-fenêtre est désiconifiée.
	* Elle n'a aucun comportement, elle n'est redéfinie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une référence sur une instance de InternalFrameEvent contenant
	*    les informations relatives à la sous-fenêtre.
	* ----------------------------------------------------------*/
	public void internalFrameDeiconified(
		InternalFrameEvent event
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "internalFrameDeiconified");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		// On reconstruit le menu
		rebuildMenu();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: internalFrameIconified
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface InternalFrameListener. Elle
	* est appelée lorsqu'une sous-fenêtre est iconifiée.
	* Elle n'a aucun comportement, elle n'est redéfinie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une référence sur une instance de InternalFrameEvent contenant
	*    les informations relatives à la sous-fenêtre.
	* ----------------------------------------------------------*/
	public void internalFrameIconified(
		InternalFrameEvent event
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "internalFrameIconified");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		// On reconstruit le menu
		rebuildMenu();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: internalFrameOpened
	*
	* Description:
	* Cette méthode redéfinit celle de l'interface InternalFrameListener. Elle
	* est appelée lorsqu'une sous-fenêtre a été ouverte.
	* Elle n'a aucun comportement, elle n'est redéfinie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une référence sur une instance de InternalFrameEvent contenant
	*    les informations relatives à la sous-fenêtre.
	* ----------------------------------------------------------*/
	public void internalFrameOpened(
		InternalFrameEvent event
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "internalFrameOpened");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("event=" + event);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getMenu
	*
	* Description:
	* Cette méthode permet de récupérer la référence sur le menu "Fenêtres" qui
	* est créé et géré par cette classe (voir la méthode rebuildMenu()).
	*
	* Retourne: La référence sur l'instance de JMenu correspondant au menu
	* "Fenêtres".
	* ----------------------------------------------------------*/
	public JMenu getMenu()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "getMenu");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return _menu;
	}

	/*----------------------------------------------------------
	* Nom: closeOpenedFrames
	*
	* Description:
	* Cette méthode permet de fermer toutes les sous-fenêtres étant ouvertes.
	* Elle doit être appelée à la fermeture de la fenêtre principale afin de
	* s'assurer de la fermeture de toutes les fenêtres ouvertes.
	* ----------------------------------------------------------*/
	public void closeOpenedFrames()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "closeOpenedFrames");

		trace_methods.beginningOfMethod();
		// On récupère la liste des fenêtres ouvertes
		JInternalFrame[] frames = getAllFrames();
		// On va les fermer une par une
		for(int index = 0 ; index < frames.length ; index ++)
		{
			// On teste si la sous-fenêtre est un processeur
			if(frames[index] instanceof ProcessorInterface)
			{
				// On ferme la sous-fenêtre
				((ProcessorInterface)frames[index]).close();
			}
			else
			{
				// Pour les autres, on les cache et on les détruit
				frames[index].setVisible(false);
				frames[index].dispose();
			}
		}
		trace_methods.endOfMethod();
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
			"MainWindowDesktopPane", "cleanBeforeExit");

		trace_methods.beginningOfMethod();
		// Il faut également détruire toutes les sous-fenêtres
		closeOpenedFrames();
		// Suppression du menu
		_menu.removeAll();
		_menu = null;
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getFocusOwner
	*
	* Description:
	* Cette méthode permet de récupérer le composant ayant le focus dans la
	* sous-fenêtre active. Si aucune sous-fenêtre n'est active, ou qu'aucun
	* composant n'a le focus, la méthode retourne null.
	*
	* Retourne: Une référence sur le composant ayant le focus, ou null.
	* ----------------------------------------------------------*/
	public Component getFocusOwner()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "getFocusOwner");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// La première chose est de récupérer la fenêtre qui est active
		Component frame = getSelectedFrame();
		trace_debug.writeTrace("frame=" + frame);
		if(frame == null || !(frame instanceof ProcessorFrame))
		{
			// Aucune fenêtre n'est active, on sort
			trace_methods.endOfMethod();
			return null;
		}
		// Maintenant, on cherche à récupérer le composant qui a le focus
		KeyboardFocusManager focus_manager = 
			KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component component = focus_manager.getPermanentFocusOwner();
		trace_debug.writeTrace("component=" + component);
		trace_methods.endOfMethod();
		return component;
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _menu
	*
	* Description:
	* Cet attribut maintient une référence sur l'instance de MainWindowMenu
	* correspondant au menu "Fenêtres" étant géré par cette classe.
	* ----------------------------------------------------------*/
	private MainWindowMenu _menu;
	
	/*----------------------------------------------------------
	* Nom: activateFrame
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur clique sur un élément 
	* du menu "Fenêtres" correspondant à l'une des sous-fenêtres.
	* Elle désiconifie et active la sous-fenêtre correspondante.
	*
	* Arguments:
	*  - frame: La référence sur la sous-fenêtre à activer.
	* ----------------------------------------------------------*/
	private void activateFrame(
		JInternalFrame frame
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "activateFrame");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("frame=" + frame);
		// On passe la fenêtre concernée en premier plan
		if(frame != null)
		{
			try
			{
			    frame.setIcon(false);
			}
			catch(Exception exception)
			{
			}
			frame.toFront();
			// On passe le focus à cette fenêtre
			try
			{
			    frame.setSelected(true);
			}
			catch(Exception exception)
			{
				Trace trace_errors = TraceAPI.declareTraceErrors("Console");

				trace_errors.writeTrace(
					"Erreur lors de l'activation de la sous-fenêtre: " +
					exception);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: rebuildMenu
	*
	* Description:
	* Cette méthode reconstruit le menu "Fenêtres", destiné à être ajouté à la
	* barre de menu de la fenêtre principale, en fonction des sous-fenêtres
	* ayant été ajoutées, supprimées ou activées.
	* ----------------------------------------------------------*/
	private void rebuildMenu()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "rebuildMenu");

		trace_methods.beginningOfMethod();
		if(_menu == null)
		{
			// Création du menu
			_menu = new MainWindowMenu("Windows");
		}
		// On supprime tous les éléments précédents
		_menu.removeAll();
		// Ajout de l'élément 'Réorganiser'
		JMenuItem reorganize_item = new MainWindowMenuItem("Reorganize");
		// Ajout du callback sur click
		reorganize_item.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				reorganizeFrames();
			}
		});
		_menu.add(reorganize_item);
		// Ajout d'un séparateur
		_menu.addSeparator();
		// Récupération de la liste des sous-fenêtres
		JInternalFrame[] frames = getAllFrames();
		for(int index = 0 ; index < frames.length ; index ++)
		{
			final JInternalFrame the_frame = frames[index];
			if(the_frame.isVisible() == false)
			{
				continue;
			}
			// Création d'un item de menu avec le nom de la fenêtre
			JCheckBoxMenuItem item = new JCheckBoxMenuItem("" + index +
				" " + the_frame.getTitle(), false);
			item.setIcon(the_frame.getFrameIcon());
			// Si l'index est inférieur à 10, ajouter un mnémonique
			if(index < 10)
			{
				item.setMnemonic(KeyMapper.getKey("" + index));
			}
			// Ajout du callback sur la sélection de l'item
			item.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					// Appel de la méthode activateFrame
					activateFrame(the_frame);
				}
			});
			// Si la fenêtre est active, cocher la case
			if(the_frame.isSelected() == true)
			{
				item.setState(true);
				// On désactive également l'item si la fenêtre n'est pas
				// iconifiée
				if(the_frame.isIcon() == false)
				{
				    item.setEnabled(false);
				}
			}
			// Ajout de l'item au menu
			_menu.add(item);
		}
		// Si le menu n'a aucun item, on le grise
		if(_menu.getItemCount() == 0)
		{
			_menu.setEnabled(false);
		}
		else
		{
			_menu.setEnabled(true);
		}
		trace_methods.endOfMethod();
	}
	
	/*----------------------------------------------------------
	* Nom: reorganizeFrames
	*
	* Description:
	* Cette méthode est appelée lorsque l'utilisateur a cliqué sur l'élément
	* de réorganisation du menu 'Fenêtres'. La réorganisation des fenêtres
	* consiste à les mettre en cascade.
	* ----------------------------------------------------------*/
	private void reorganizeFrames()
	{
		int pos_x = 0;
		int pos_y = 0;
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "reorganizeFrames");

		trace_methods.beginningOfMethod();
		// On récupère toutes les frames
		JInternalFrame[] frames = getAllFrames();
		// On repositionne chaque frame
		for(int index = frames.length - 1 ; index >=0  ; index --)
		{
			JInternalFrame the_frame = frames[index];
			the_frame.setLocation(pos_x, pos_y);
			// On désiconifie la fenêtre (au cas où), et on la fait passer en
			// avant plan
			try
			{
				the_frame.setIcon(false);
			}
			catch(Exception exception)
			{
			}
			the_frame.toFront();
			// On incrémente les positions
			pos_x += 15;
			pos_y += 15;
		}
		trace_methods.endOfMethod();
	}
}