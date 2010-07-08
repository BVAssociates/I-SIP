/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/MainWindowDesktopPane.java,v $
* $Revision: 1.15 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation de la zone d'affichage des sous-fen�tres
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
* Modification de la m�thode de positionnement des sous-fen�tres.
*
* Revision 1.12  2005/07/01 12:24:50  tz
* Modification du composant pour les traces
*
* Revision 1.11  2004/11/23 15:44:56  tz
* Traitement du cas o� la frame n'est pas visible.
*
* Revision 1.10  2004/10/22 15:44:48  tz
* Modification de la m�thode de r�cup�ration du propri�taire du focus.
*
* Revision 1.9  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.8  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.7  2004/10/06 07:40:17  tz
* D�calage des frames lors de leur premier affichage.
*
* Revision 1.6  2004/07/29 12:19:37  tz
* Suppression d'imports inutiles
*
* Revision 1.5  2003/12/08 15:13:52  tz
* Merge depuis la branche rel-1_0-maint
*
* Revision 1.4.2.1  2003/10/27 16:56:17  tz
* Ajout de l'option de menu "R�organiser"
*
* Revision 1.4  2002/04/17 08:00:19  tz
* Gestion des fen�tres iconifi�es.
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
* Cette classe repr�sente la zone d'affichage des sous-fen�tres. Elle �tend la
* classe JDesktopPane afin d'y ajouter un comportement particulier. Elle
* impl�mente l'interface InternalFrameListener.
*
* Cette classe impl�mente un comportement particulier lors des �v�nements
* suivants:
*  - Une sous-fen�tre est ajout�e,
*  - Une sous-fen�tre prend le focus,
*  - Une sous-fen�tre est retir�e.
*
* Cette classe g�re un menu qui sera ajout� � la barre de menu de la fen�tre
* principale. Ce menu permet de g�r�r les sous-fen�tres (changement de fen�tre
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
	* Cette m�thode est le seul constructeur de la classe. Elle cr�e l'instance
	* de JMenu correspondant au menu "Fen�tres" de la fen�tre principale.
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
	* Cette m�thode red�finit celle de la super-classe JDesktopPane. Elle est
	* appel�e lorsqu'un composant graphique est ajout� � la zone d'affichage.
	* Si le composant est une sous-fen�tre, la m�thode va appeler la m�thode
	* rebuildMenu() afin de reconstruire le menu "Fen�tre" pour y int�grer la
	* nouvelle sous-fen�tre.
	*
	* Arguments:
	*  - component: Une r�f�rence sur l'objet Component qui est ajout� � la
	*    zone d'affichage.
	*
	* Retourne: La r�f�rence sur le composant ajout�.
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
		// On appelle la m�thode de la super-classe
		super.add(component);
		// Le composant � ajouter doit �tre une sous-fen�tre
		if(component instanceof JInternalFrame)
		{
			JInternalFrame internal_frame = (JInternalFrame)component;
			// On r�cup�re toutes les autres frames
			JInternalFrame[] frames = getAllFrames();
			// On va tester toutes les positions jusqu'� en trouver une qui ne 
			// soit pas d�j� occup�e
			for(int counter = 0 ; counter < 10 ; counter ++) {
				boolean current_position_used = false;
				
				frame_position.x = (counter % 5) * 15;
				frame_position.y = counter * 15;
				for(int loop = 0 ; loop < frames.length ; loop ++) {
					Point existing_frame_position = frames[loop].getLocation();
					if(existing_frame_position.equals(
						frame_position) == true) {
						// La position est d�j� utilis�e par au moins une 
						// sous-fen�tre, il va falloir en utiliser une autre
						current_position_used = true;
						break;
					}
				}
				if(current_position_used == false) {
					// Si la position courante n'est pas utilis�e, il
					// faut positionner la sous-fen�tre � celle-ci
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
	* Cette m�thode red�finit celle de l'interface InternalFrameListener. Elle
	* est appel�e lorsqu'une sous-fen�tre devient active (prend le focus).
	* Elle appelle la m�thode rebuildMenu() afin de mettre � jour le menu
	* "Fen�tre" de la fen�tre principale.
	*
	* Arguments:
	*  - event: Une r�f�rence sur une instance de InternalFrameEvent contenant
	*    les informations relatives � la sous-fen�tre.
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
	* Cette m�thode red�finit celle de l'interface InternalFrameListener. Elle
	* est appel�e lorsqu'une sous-fen�tre a �t� ferm�e.
	* Elle appelle la m�thode rebuildMenu() afin de mettre � jour le menu
	* "Fen�tre" de la fen�tre principale.
	*
	* Arguments:
	*  - event: Une r�f�rence sur une instance de InternalFrameEvent contenant
	*    les informations relatives � la sous-fen�tre.
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
	* Cette m�thode red�finit celle de l'interface InternalFrameListener. Elle
	* est appel�e lorsqu'une sous-fen�tre est sur le point d'�tre ferm�e.
	* Elle n'a aucun comportement, elle n'est red�finie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une r�f�rence sur une instance de InternalFrameEvent contenant
	*    les informations relatives � la sous-fen�tre.
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
	* Cette m�thode red�finit celle de l'interface InternalFrameListener. Elle
	* est appel�e lorsqu'une sous-fen�tre est sur d�sactiv�e (perd le focus).
	* Elle n'a aucun comportement, elle n'est red�finie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une r�f�rence sur une instance de InternalFrameEvent contenant
	*    les informations relatives � la sous-fen�tre.
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
	* Cette m�thode red�finit celle de l'interface InternalFrameListener. Elle
	* est appel�e lorsqu'une sous-fen�tre est d�siconifi�e.
	* Elle n'a aucun comportement, elle n'est red�finie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une r�f�rence sur une instance de InternalFrameEvent contenant
	*    les informations relatives � la sous-fen�tre.
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
	* Cette m�thode red�finit celle de l'interface InternalFrameListener. Elle
	* est appel�e lorsqu'une sous-fen�tre est iconifi�e.
	* Elle n'a aucun comportement, elle n'est red�finie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une r�f�rence sur une instance de InternalFrameEvent contenant
	*    les informations relatives � la sous-fen�tre.
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
	* Cette m�thode red�finit celle de l'interface InternalFrameListener. Elle
	* est appel�e lorsqu'une sous-fen�tre a �t� ouverte.
	* Elle n'a aucun comportement, elle n'est red�finie que pour rendre la
	* classe instanciable.
	*
	* Arguments:
	*  - event: Une r�f�rence sur une instance de InternalFrameEvent contenant
	*    les informations relatives � la sous-fen�tre.
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
	* Cette m�thode permet de r�cup�rer la r�f�rence sur le menu "Fen�tres" qui
	* est cr�� et g�r� par cette classe (voir la m�thode rebuildMenu()).
	*
	* Retourne: La r�f�rence sur l'instance de JMenu correspondant au menu
	* "Fen�tres".
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
	* Cette m�thode permet de fermer toutes les sous-fen�tres �tant ouvertes.
	* Elle doit �tre appel�e � la fermeture de la fen�tre principale afin de
	* s'assurer de la fermeture de toutes les fen�tres ouvertes.
	* ----------------------------------------------------------*/
	public void closeOpenedFrames()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "closeOpenedFrames");

		trace_methods.beginningOfMethod();
		// On r�cup�re la liste des fen�tres ouvertes
		JInternalFrame[] frames = getAllFrames();
		// On va les fermer une par une
		for(int index = 0 ; index < frames.length ; index ++)
		{
			// On teste si la sous-fen�tre est un processeur
			if(frames[index] instanceof ProcessorInterface)
			{
				// On ferme la sous-fen�tre
				((ProcessorInterface)frames[index]).close();
			}
			else
			{
				// Pour les autres, on les cache et on les d�truit
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
	* Cette m�thode permet de lib�rer les ressources allou�es. Elle doit �tre
	* appel�e lors de la fermeture de la fen�tre principale, � l'arr�t de
	* l'application.
	* ----------------------------------------------------------*/
	public void cleanBeforeExit()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "cleanBeforeExit");

		trace_methods.beginningOfMethod();
		// Il faut �galement d�truire toutes les sous-fen�tres
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
	* Cette m�thode permet de r�cup�rer le composant ayant le focus dans la
	* sous-fen�tre active. Si aucune sous-fen�tre n'est active, ou qu'aucun
	* composant n'a le focus, la m�thode retourne null.
	*
	* Retourne: Une r�f�rence sur le composant ayant le focus, ou null.
	* ----------------------------------------------------------*/
	public Component getFocusOwner()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "getFocusOwner");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		// La premi�re chose est de r�cup�rer la fen�tre qui est active
		Component frame = getSelectedFrame();
		trace_debug.writeTrace("frame=" + frame);
		if(frame == null || !(frame instanceof ProcessorFrame))
		{
			// Aucune fen�tre n'est active, on sort
			trace_methods.endOfMethod();
			return null;
		}
		// Maintenant, on cherche � r�cup�rer le composant qui a le focus
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
	* Cet attribut maintient une r�f�rence sur l'instance de MainWindowMenu
	* correspondant au menu "Fen�tres" �tant g�r� par cette classe.
	* ----------------------------------------------------------*/
	private MainWindowMenu _menu;
	
	/*----------------------------------------------------------
	* Nom: activateFrame
	*
	* Description:
	* Cette m�thode est appel�e lorsque l'utilisateur clique sur un �l�ment 
	* du menu "Fen�tres" correspondant � l'une des sous-fen�tres.
	* Elle d�siconifie et active la sous-fen�tre correspondante.
	*
	* Arguments:
	*  - frame: La r�f�rence sur la sous-fen�tre � activer.
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
		// On passe la fen�tre concern�e en premier plan
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
			// On passe le focus � cette fen�tre
			try
			{
			    frame.setSelected(true);
			}
			catch(Exception exception)
			{
				Trace trace_errors = TraceAPI.declareTraceErrors("Console");

				trace_errors.writeTrace(
					"Erreur lors de l'activation de la sous-fen�tre: " +
					exception);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: rebuildMenu
	*
	* Description:
	* Cette m�thode reconstruit le menu "Fen�tres", destin� � �tre ajout� � la
	* barre de menu de la fen�tre principale, en fonction des sous-fen�tres
	* ayant �t� ajout�es, supprim�es ou activ�es.
	* ----------------------------------------------------------*/
	private void rebuildMenu()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "rebuildMenu");

		trace_methods.beginningOfMethod();
		if(_menu == null)
		{
			// Cr�ation du menu
			_menu = new MainWindowMenu("Windows");
		}
		// On supprime tous les �l�ments pr�c�dents
		_menu.removeAll();
		// Ajout de l'�l�ment 'R�organiser'
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
		// Ajout d'un s�parateur
		_menu.addSeparator();
		// R�cup�ration de la liste des sous-fen�tres
		JInternalFrame[] frames = getAllFrames();
		for(int index = 0 ; index < frames.length ; index ++)
		{
			final JInternalFrame the_frame = frames[index];
			if(the_frame.isVisible() == false)
			{
				continue;
			}
			// Cr�ation d'un item de menu avec le nom de la fen�tre
			JCheckBoxMenuItem item = new JCheckBoxMenuItem("" + index +
				" " + the_frame.getTitle(), false);
			item.setIcon(the_frame.getFrameIcon());
			// Si l'index est inf�rieur � 10, ajouter un mn�monique
			if(index < 10)
			{
				item.setMnemonic(KeyMapper.getKey("" + index));
			}
			// Ajout du callback sur la s�lection de l'item
			item.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					// Appel de la m�thode activateFrame
					activateFrame(the_frame);
				}
			});
			// Si la fen�tre est active, cocher la case
			if(the_frame.isSelected() == true)
			{
				item.setState(true);
				// On d�sactive �galement l'item si la fen�tre n'est pas
				// iconifi�e
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
	* Cette m�thode est appel�e lorsque l'utilisateur a cliqu� sur l'�l�ment
	* de r�organisation du menu 'Fen�tres'. La r�organisation des fen�tres
	* consiste � les mettre en cascade.
	* ----------------------------------------------------------*/
	private void reorganizeFrames()
	{
		int pos_x = 0;
		int pos_y = 0;
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowDesktopPane", "reorganizeFrames");

		trace_methods.beginningOfMethod();
		// On r�cup�re toutes les frames
		JInternalFrame[] frames = getAllFrames();
		// On repositionne chaque frame
		for(int index = frames.length - 1 ; index >=0  ; index --)
		{
			JInternalFrame the_frame = frames[index];
			the_frame.setLocation(pos_x, pos_y);
			// On d�siconifie la fen�tre (au cas o�), et on la fait passer en
			// avant plan
			try
			{
				the_frame.setIcon(false);
			}
			catch(Exception exception)
			{
			}
			the_frame.toFront();
			// On incr�mente les positions
			pos_x += 15;
			pos_y += 15;
		}
		trace_methods.endOfMethod();
	}
}