/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/MainWindowMenuItem.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation des items de menu
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MainWindowMenuItem.java,v $
* Revision 1.6  2005/07/01 12:24:27  tz
* Modification du composant pour les traces
*
* Revision 1.5  2004/10/22 15:45:17  tz
* Suppression des m�thodes inutiles.
*
* Revision 1.4  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.3  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
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
import javax.swing.JMenuItem;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.gui.KeyMapper;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;
import com.bv.core.gui.IconLoader;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: MainWindowMenuItem
*
* Description:
* Cette classe est une sp�cialisation de la classe JMenuItem.
* Elle g�re automatiquement les labels, les touches de mn�monique et de 
* raccourci, et enfin les ic�nes depuis le fichier de messages.
* ----------------------------------------------------------*/
class MainWindowMenuItem
	extends JMenuItem
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: MainWindowMenuItem
	*
	* Description:
	* Cette m�thode est le seul constructeur de la classe. Elle d�fini un item
	* de menu ayant un label, un mn�monique, �ventuellement un raccourci et une
	* ic�ne, en fonction de l'identifiant pass� en argument.
	* Les trois premi�res informations sont extraites du fichier de messages,
	* via le service de messages de la librairie BVCore/Java. L'ic�ne est
	* r�cup�r�e via le service de la classe IconLoader.
	*
	* Arguments:
	*  - itemId: L'identifiant de l'item de menu permettant de retrouver le
	*    label, le mn�monique, le raccourci et l'ic�ne.
	* ----------------------------------------------------------*/
	public MainWindowMenuItem(
		String itemId
		)
	{
		String menu_label;
		int menu_mnemonic;
		KeyStroke shortcut = null;
        ImageIcon item_icon;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowMenuItem", "MainWindowMenuItem");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("itemId=" + itemId);
		// R�cup�ration du label de l'item
		menu_label = MessageManager.getMessage("&MW_Menu_" + itemId);
		// R�cup�ration du mn�monique
		menu_mnemonic = KeyMapper.getKey(MessageManager.getMessage("&MW_Mnemonic_" + itemId));
		// R�cup�ration du raccourci
		shortcut = KeyMapper.getKeyStroke(MessageManager.getMessage("&MW_Shortcut_" + itemId));
        // R�cup�ration de l'ic�ne
        item_icon = IconLoader.getIcon(itemId);
        if(item_icon == null)
        {
		    // Il n'y a pas d'ic�ne, r�cup�ration du vide
            item_icon = IconLoader.getIcon("Empty");
        }
        // Positionnement du label et de l'ic�ne
        init(menu_label, item_icon);
		// Ajout du mn�monique, s'il existe
		if(menu_mnemonic != KeyEvent.VK_UNDEFINED)
		{
			trace_debug.writeTrace("Ajout d'un mn�monique � l'item " + itemId);
			setMnemonic(menu_mnemonic);
		}
		// Ajout du raccourci, s'il existe
		if(shortcut != null)
		{
			trace_debug.writeTrace("Ajout d'un raccourci � l'item " + itemId);
			setAccelerator(shortcut);
		}
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}