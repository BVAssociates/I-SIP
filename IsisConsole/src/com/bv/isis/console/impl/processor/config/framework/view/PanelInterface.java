/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/framework/view/PanelInterface.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de d�finitions des sous panneaux de la fen�tre de configuration
* DATE:        04/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
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

//
//Imports du projet
//
import com.bv.isis.console.impl.processor.config.framework.model.ModelInterface;

/*----------------------------------------------------------
* Nom: PanelInterface
*
* Description: 
* Cette interface dispose de m�thodes de gestion des panneaux tel que la 
* mise � jour ainsi que le comportement avant et apr�s l'affichage. 
* ----------------------------------------------------------*/
public interface PanelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: beforeDisplay
	* 
	* Description: 
	* Cette m�thode initialise les donn�es du panneau � partir du 
	* mod�le de donn�es.
	* 
	* Arguments :
	*  - tableModels : Le tableau de ModelInterface � partir duquel 
	*  r�cup�rer les donn�es � afficher.
	*----------------------------------------------------------*/
	public void beforeDisplay(
		ArrayList<ModelInterface> tableModels
		);
	
	/*----------------------------------------------------------
	* Nom: afterDisplay
	* 
	* Description: 
	* Cette m�thode d�finit le comportement du panneau apr�s son affichage dans la fen�tre. 
	* 
	* Retourne : Vrai (true) si tout c'est bien pass�, faux (false)
	* sinon.
	*----------------------------------------------------------*/
	public boolean afterDisplay();
		
	/*----------------------------------------------------------
	* Nom: beforeHide
	* 
	* Description:
	* Cette m�thode d�finit le comportement du panneau avant d'�tre cach�.
	* Elle contr�le les donn�es saisies par l'utilisateur ainsi que leurs 
	* validit�s.
	* 
	* Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	* sinon.
	*----------------------------------------------------------*/
	public boolean beforeHide();

	/*----------------------------------------------------------
	* Nom: afterHide
	* 
	* Description: 
	* Cette m�thode d�finit le comportement du panneau apr�s avoir �t� 
	* cach�.
	* 
	* Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	* sinon.
	*----------------------------------------------------------*/
	public boolean afterHide();
		
	/*----------------------------------------------------------
	* Nom: end
	* 
	* Description: 
	* Cette m�thode est appel�e lors de la destruction de l'assistant. 
	* Elle est utilis�e pour lib�rer l'espace m�moire utilis� par les 
	* variables des classes impl�mentant cette interface.
	* 
	* Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	* sinon.
	*----------------------------------------------------------*/
	public boolean end();
		
	/*----------------------------------------------------------
	* Nom: update
	* 
	* Description: 
	* Cette m�thode est appel�e par la m�thode displayNextPanel() de la classe
	* AbstractWindow. A partir du mod�le de donn�es pass� en param�tre, elle 
	* doit se charger de le mettre � jour � l'aide des donn�es saisies par 
	* l'utilisateur. 
	* Une fois termin�e, elle retourne le nouveau mod�le de don�es.
	* 
	* Arguments : 
	*  - tableModels : Le mod�le de donn�es avant modification.
	*  
	* Retourne : Le nouveau mod�le de donn�es.
	*----------------------------------------------------------*/
	public ArrayList<ModelInterface> update(
		ArrayList<ModelInterface> tableModels
		);
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
