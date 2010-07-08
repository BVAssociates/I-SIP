/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
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
// D�claration du package
package com.bv.isis.console.impl.processor.config.framework.view;

//
//Imports syst�me
//

import javax.swing.JPanel;

//
//Imports du projet
//

/*------------------------------------------------------------
 * Nom: AbstractPannel
 *
 * Description:
 * Cette classe repr�sente le panneau � afficher dans la fen�tre,
 * elle d�rive de JPanel. 
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
	 * Cet attribut maintient une r�f�rence sur la fen�tre contenant
	 * le panneau. Celle-ci permet au panneau d'int�ragir avec la 
	 * fen�tre principale.
	 *------------------------------------------------------------*/
	public WindowInterface _window;
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
