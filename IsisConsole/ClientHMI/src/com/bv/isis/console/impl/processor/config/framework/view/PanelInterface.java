/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/framework/view/PanelInterface.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de définitions des sous panneaux de la fenêtre de configuration
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
// Déclaration du package
package com.bv.isis.console.impl.processor.config.framework.view;

//
//Imports système
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
* Cette interface dispose de méthodes de gestion des panneaux tel que la 
* mise à jour ainsi que le comportement avant et après l'affichage. 
* ----------------------------------------------------------*/
public interface PanelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: beforeDisplay
	* 
	* Description: 
	* Cette méthode initialise les données du panneau à partir du 
	* modèle de données.
	* 
	* Arguments :
	*  - tableModels : Le tableau de ModelInterface à partir duquel 
	*  récupérer les données à afficher.
	*----------------------------------------------------------*/
	public void beforeDisplay(
		ArrayList<ModelInterface> tableModels
		);
	
	/*----------------------------------------------------------
	* Nom: afterDisplay
	* 
	* Description: 
	* Cette méthode définit le comportement du panneau après son affichage dans la fenêtre. 
	* 
	* Retourne : Vrai (true) si tout c'est bien passé, faux (false)
	* sinon.
	*----------------------------------------------------------*/
	public boolean afterDisplay();
		
	/*----------------------------------------------------------
	* Nom: beforeHide
	* 
	* Description:
	* Cette méthode définit le comportement du panneau avant d'être caché.
	* Elle contrôle les données saisies par l'utilisateur ainsi que leurs 
	* validités.
	* 
	* Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	* sinon.
	*----------------------------------------------------------*/
	public boolean beforeHide();

	/*----------------------------------------------------------
	* Nom: afterHide
	* 
	* Description: 
	* Cette méthode définit le comportement du panneau après avoir été 
	* caché.
	* 
	* Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	* sinon.
	*----------------------------------------------------------*/
	public boolean afterHide();
		
	/*----------------------------------------------------------
	* Nom: end
	* 
	* Description: 
	* Cette méthode est appelée lors de la destruction de l'assistant. 
	* Elle est utilisée pour libèrer l'espace mémoire utilisé par les 
	* variables des classes implémentant cette interface.
	* 
	* Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	* sinon.
	*----------------------------------------------------------*/
	public boolean end();
		
	/*----------------------------------------------------------
	* Nom: update
	* 
	* Description: 
	* Cette méthode est appelée par la méthode displayNextPanel() de la classe
	* AbstractWindow. A partir du modèle de données passé en paramètre, elle 
	* doit se charger de le mettre à jour à l'aide des données saisies par 
	* l'utilisateur. 
	* Une fois terminée, elle retourne le nouveau modèle de donées.
	* 
	* Arguments : 
	*  - tableModels : Le modèle de données avant modification.
	*  
	* Retourne : Le nouveau modèle de données.
	*----------------------------------------------------------*/
	public ArrayList<ModelInterface> update(
		ArrayList<ModelInterface> tableModels
		);
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
