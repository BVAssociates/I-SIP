/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/framework/view/WindowInterface.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de définitions de la fenêtre de configuration
* DATE:        04/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* Revision 1.5  2008/07/11 fc
* Ajout de la méthode getMainWindowInterfaceFromProcessorFrame()
*
* Revision 1.4  2008/06/30 fc
* Ajout de la méthode displayPanel()
*
* Revision 1.3  2008/06/25 fc
* Ajout de la méthode enabledNextAndPreviousButton()
* 
* Revision 1.2  2008/06/20 fc
* Modification du tableau de ModelInterface en 
* ArrayList<ModelInterface>
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.config.framework.view;

//
//Imports système
//
import java.util.ArrayList;

import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;

/*----------------------------------------------------------
 * Nom: WindowInterface
 * 
 * Description: 
 * Cette interface définit les méthodes pouvant être appelée par les sous 
 * panneaux telle que l'accès au modèle de données ainsi qu'une méthode 
 * pour indiquer quels panneaux afficher.
 * ----------------------------------------------------------*/
public interface WindowInterface {

	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	 * Nom: getModel
	 * 
	 * Description: 
	 * Cette méthode doit retourner le modèle de données associé à 
	 * l'assistant de paramétrage.
	 * 
	 * Retourne:
	 * Le tableau de ModelInterface
	 * ----------------------------------------------------------*/
	public ArrayList<ModelInterface> getModel();
	
	/*----------------------------------------------------------
	 * Nom: setModel
	 * 
	 * Description: 
	 * Cette méthode affecte un nouveau modèle de données a 
	 * l'assistant de paramétrage.
	 * 
	 * Paramètre:
	 *  - modele : Le nouveau tableau de modelInterface
	 * ----------------------------------------------------------*/
	public void setModel(ArrayList<ModelInterface> modele);
	
	/*----------------------------------------------------------
	 * Nom: setPanels
	 * 
	 * Description: 
	 * Cette méthode enregistre les panneaux à afficher 
	 * dans la fenêtre 
	 * 
	 * Paramètre:
	 *  - tabPanel : Le tableau de panelInterface à enregistrer
	 * ----------------------------------------------------------*/
	public void setPanel(PanelInterface [] tabPanel);
	
	/*----------------------------------------------------------
	 * Nom: enabledNextAndPreviousButton
	 * 
	 * Description: 
	 * Cette méthode permet d'activer ou de désactiver les boutons
	 * Suivant et Précédent de la fenêtre 
	 * 
	 * Paramètre:
	 *  - b : un booléen indiquant si on active ou désactive les boutons
	 * ----------------------------------------------------------*/
	public void enabledNextAndPreviousButton(boolean b);
	
	/*----------------------------------------------------------
	 * Nom: displayPanel
	 * 
	 * Description: 
	 * Cette méthode permet d'afficher un panneau sans que celui-ci ne soit
	 * présent dans la cinématique des panneaux.
	 * 
	 * Paramètre:
	 *  - panel : Le panneau a afficher
	 * ----------------------------------------------------------*/
	public void displayPanel(PanelInterface panel);
	
	/*----------------------------------------------------------
	 * Nom: getMainWindowInterfaceFromProcessorFrame
	 * 
	 * Description: 
	 * Cette méthode permet de retourner une référence sur la fenêtre
	 * principale de la console.
	 * 
	 * Retourne : Une référence sur la fenêtre principale de la console
	 * ----------------------------------------------------------*/
	public MainWindowInterface getMainWindowInterfaceFromProcessorFrame();
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
