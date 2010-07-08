/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/gui/MainWindowMenu.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe d'impl�mentation de menu
* DATE:        19/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MainWindowMenu.java,v $
* Revision 1.6  2005/07/01 12:24:34  tz
* Modification du composant pour les traces
*
* Revision 1.5  2004/10/22 15:45:12  tz
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
import javax.swing.JMenu;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.core.message.MessageManager;
import com.bv.core.gui.KeyMapper;
import java.awt.event.KeyEvent;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: MainWindowMenu
*
* Description:
* Cette classe est une sp�cialisation de la classe JMenu.
* Elle g�re automatiquement les labels et les touches de mn�monique depuis 
* le fichier de messages.
* ----------------------------------------------------------*/
class MainWindowMenu
	extends JMenu
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: MainWindowMenu
	*
	* Description:
	* Cette m�thode et le seul constructeur de la classe. Elle d�fini un menu
	* ayant un label et un mn�monique en fonction de l'identifiant pass� en
	* argument.
	* Ces deux informations sont extraites du fichier de messages, via le
	* service de messages de la librairie BVCore/Java.
	*
	* Arguments:
	*  - menuId: L'identifiant du menu permettant de retrouver le label et le
	*    mn�monique.
	* ----------------------------------------------------------*/
	public MainWindowMenu(
		String menuId
		)
	{
		String menu_label;
		int menu_mnemonic;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"MainWindowMenu", "MainWindowMenu");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("menuId=" + menuId);
		// R�cup�ration du label du menu
		menu_label = MessageManager.getMessage("&MW_Menu_" + menuId);
		// R�cup�ration du mn�monique du menu
		menu_mnemonic = KeyMapper.getKey(MessageManager.getMessage("&MW_Mnemonic_" + menuId));
		// Positionnement du label du menu
		setText(menu_label);
		// Ajout du mn�monique, s'il existe
		if(menu_mnemonic != KeyEvent.VK_UNDEFINED)
		{
			trace_debug.writeTrace("Ajout d'un mn�monique au menu " + menuId);
			setMnemonic(menu_mnemonic);
		}
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}