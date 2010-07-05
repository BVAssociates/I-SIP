/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/framework/view/AbstractPanel.java,v $
* $Revision: 1.6 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe des sous panneaux
* DATE:        04/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* Revision 1.4  2008/06/20 fc
* Modification du tableau de ModelInterface en 
* ArrayList<ModelInterface>
* 
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.config.framework.view;

//
//Imports système
//

import javax.swing.JPanel;

//
//Imports du projet
//

/*------------------------------------------------------------
 * Nom: AbstractPannel
 *
 * Description:
 * Cette classe représente le panneau à afficher dans la fenêtre,
 * elle dérive de JPanel. 
 * ------------------------------------------------------------*/
public abstract class AbstractPanel
	extends JPanel 
	implements PanelInterface 
{
	
	private static final long serialVersionUID = 1L;
	
	// ******************* PUBLIC **********************
	/*------------------------------------------------------------
	 * Nom: _window
	 * 
	 * Description: 
	 * Cet attribut maintient une référence sur la fenêtre contenant
	 * le panneau. Celle-ci permet au panneau d'intéragir avec la 
	 * fenêtre principale.
	 *------------------------------------------------------------*/
	public WindowInterface _window;
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
