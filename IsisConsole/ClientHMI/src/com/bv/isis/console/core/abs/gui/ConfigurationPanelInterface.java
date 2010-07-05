/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/gui/ConfigurationPanelInterface.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface des panneaux de configuration
* DATE:        15/10/2004
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ConfigurationPanelInterface.java,v $
* Revision 1.3  2009/01/14 12:23:57  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.gui.
*
* Revision 1.2  2004/11/02 09:12:15  tz
* Modification de la date de copyright.
*
* Revision 1.1  2004/10/22 15:46:22  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.abs.gui;

//
// Imports syst�me
//

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: ConfigurationPanelInterface
* 
* Description:
* Cette interface permet de d�finir les m�thodes de communication entre la 
* fen�tre de configuration de la Console et les panneaux de configuration 
* des processeurs.
* ----------------------------------------------------------*/
public interface ConfigurationPanelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: validateConfiguration
	* 
	* Description:
	* Cette m�thode permet de demander au panneau de configuration d'un 
	* processeur de valider ou non les donn�es qu'il contient.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface 
	* n�cessaire pour afficher des bo�tes de dialogue.
	* 
	* Retourne: true si le panneau de configuration valide son contenu, false 
	* sinon.
	* ----------------------------------------------------------*/
	public boolean validateConfiguration(
		MainWindowInterface windowInterface
		);

	/*----------------------------------------------------------
	* Nom: storeConfiguration
	* 
	* Description:
	* Cette m�thode permet de demander au panneau de configuration d'un 
	* processeur d'enregistrer les donn�es de configuration qu'il contient.
	* 
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface 
	* n�cessaire pour afficher des bo�tes de dialogue.
	* 
	* Retourne: true si le panneau de configuration a enregistr� son contenu, 
	* false sinon.
	* ----------------------------------------------------------*/
	public boolean storeConfiguration(
		MainWindowInterface windowInterface
		);

	/*----------------------------------------------------------
	* Nom: getPanelTitle
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer une cha�ne de caract�res correspondant 
	* � l'intitul� du panneau de configuration.
	* 
	* Retourne: L'intitul� du panneau de configuration.
	* ----------------------------------------------------------*/
	public String getPanelTitle();
}