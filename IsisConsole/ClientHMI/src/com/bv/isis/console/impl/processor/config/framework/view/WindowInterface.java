/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/framework/view/WindowInterface.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de d�finitions de la fen�tre de configuration
* DATE:        04/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* Revision 1.5  2008/07/11 fc
* Ajout de la m�thode getMainWindowInterfaceFromProcessorFrame()
*
* Revision 1.4  2008/06/30 fc
* Ajout de la m�thode displayPanel()
*
* Revision 1.3  2008/06/25 fc
* Ajout de la m�thode enabledNextAndPreviousButton()
* 
* Revision 1.2  2008/06/20 fc
* Modification du tableau de ModelInterface en 
* ArrayList<ModelInterface>
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.framework.view;

//
//Imports syst�me
//
import java.util.ArrayList;

import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;

/*----------------------------------------------------------
 * Nom: WindowInterface
 * 
 * Description: 
 * Cette interface d�finit les m�thodes pouvant �tre appel�e par les sous 
 * panneaux telle que l'acc�s au mod�le de donn�es ainsi qu'une m�thode 
 * pour indiquer quels panneaux afficher.
 * ----------------------------------------------------------*/
public interface WindowInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: getModel
	 * 
	 * Description: 
	 * Cette m�thode doit retourner le mod�le de donn�es associ� � 
	 * l'assistant de param�trage.
	 * 
	 * Retourne:
	 * Le tableau de ModelInterface
	 * ----------------------------------------------------------*/
	public ArrayList<ModelInterface> getModel();
	
	/*----------------------------------------------------------
	 * Nom: setModel
	 * 
	 * Description: 
	 * Cette m�thode affecte un nouveau mod�le de donn�es a 
	 * l'assistant de param�trage.
	 * 
	 * Param�tre:
	 *  - modele : Le nouveau tableau de modelInterface
	 * ----------------------------------------------------------*/
	public void setModel(ArrayList<ModelInterface> modele);
	
	/*----------------------------------------------------------
	 * Nom: setPanels
	 * 
	 * Description: 
	 * Cette m�thode enregistre les panneaux � afficher 
	 * dans la fen�tre 
	 * 
	 * Param�tre:
	 *  - tabPanel : Le tableau de panelInterface � enregistrer
	 * ----------------------------------------------------------*/
	public void setPanel(PanelInterface [] tabPanel);
	
	/*----------------------------------------------------------
	 * Nom: enabledNextAndPreviousButton
	 * 
	 * Description: 
	 * Cette m�thode permet d'activer ou de d�sactiver les boutons
	 * Suivant et Pr�c�dent de la fen�tre 
	 * 
	 * Param�tre:
	 *  - b : un bool�en indiquant si on active ou d�sactive les boutons
	 * ----------------------------------------------------------*/
	public void enabledNextAndPreviousButton(boolean b);
	
	/*----------------------------------------------------------
	 * Nom: displayPanel
	 * 
	 * Description: 
	 * Cette m�thode permet d'afficher un panneau sans que celui-ci ne soit
	 * pr�sent dans la cin�matique des panneaux.
	 * 
	 * Param�tre:
	 *  - panel : Le panneau a afficher
	 * ----------------------------------------------------------*/
	public void displayPanel(PanelInterface panel);
	
	/*----------------------------------------------------------
	 * Nom: getMainWindowInterfaceFromProcessorFrame
	 * 
	 * Description: 
	 * Cette m�thode permet de retourner une r�f�rence sur la fen�tre
	 * principale de la console.
	 * 
	 * Retourne : Une r�f�rence sur la fen�tre principale de la console
	 * ----------------------------------------------------------*/
	public MainWindowInterface getMainWindowInterfaceFromProcessorFrame();
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
