/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/MainWindowMenuItem.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'implémentation des items de menu
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
* Suppression des méthodes inutiles.
*
* Revision 1.4  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.3  2004/10/13 14:02:23  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
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
* Cette classe est une spécialisation de la classe JMenuItem.
* Elle gère automatiquement les labels, les touches de mnémonique et de 
* raccourci, et enfin les icônes depuis le fichier de messages.
* ----------------------------------------------------------*/
class MainWindowMenuItem
	extends JMenuItem
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: MainWindowMenuItem
	*
	* Description:
	* Cette méthode est le seul constructeur de la classe. Elle défini un item
	* de menu ayant un label, un mnémonique, éventuellement un raccourci et une
	* icône, en fonction de l'identifiant passé en argument.
	* Les trois premières informations sont extraites du fichier de messages,
	* via le service de messages de la librairie BVCore/Java. L'icône est
	* récupérée via le service de la classe IconLoader.
	*
	* Arguments:
	*  - itemId: L'identifiant de l'item de menu permettant de retrouver le
	*    label, le mnémonique, le raccourci et l'icône.
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
		// Récupération du label de l'item
		menu_label = MessageManager.getMessage("&MW_Menu_" + itemId);
		// Récupération du mnémonique
		menu_mnemonic = KeyMapper.getKey(MessageManager.getMessage("&MW_Mnemonic_" + itemId));
		// Récupération du raccourci
		shortcut = KeyMapper.getKeyStroke(MessageManager.getMessage("&MW_Shortcut_" + itemId));
        // Récupération de l'icône
        item_icon = IconLoader.getIcon(itemId);
        if(item_icon == null)
        {
		    // Il n'y a pas d'icône, récupération du vide
            item_icon = IconLoader.getIcon("Empty");
        }
        // Positionnement du label et de l'icône
        init(menu_label, item_icon);
		// Ajout du mnémonique, s'il existe
		if(menu_mnemonic != KeyEvent.VK_UNDEFINED)
		{
			trace_debug.writeTrace("Ajout d'un mnémonique à l'item " + itemId);
			setMnemonic(menu_mnemonic);
		}
		// Ajout du raccourci, s'il existe
		if(shortcut != null)
		{
			trace_debug.writeTrace("Ajout d'un raccourci à l'item " + itemId);
			setAccelerator(shortcut);
		}
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}