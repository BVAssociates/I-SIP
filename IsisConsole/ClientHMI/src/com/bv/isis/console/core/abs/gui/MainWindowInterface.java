/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/gui/MainWindowInterface.java,v $
* $Revision: 1.11 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de la fenêtre principale de l'application
* DATE:        13/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MainWindowInterface.java,v $
* Revision 1.11  2009/01/14 12:24:40  tz
* Classe déplacée dans le package com.bv.isis.console.core.abs.gui.
*
* Revision 1.10  2005/12/23 13:13:32  tz
* Ajout de la méthode exitWindow().
*
* Revision 1.9  2005/10/07 08:45:39  tz
* Ajout de la méthode closeSession().
*
* Revision 1.8  2005/07/01 12:00:44  tz
* Modification de la méthode setCursor
*
* Revision 1.7  2004/10/13 14:03:57  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.6  2003/12/08 14:38:30  tz
* Mise à jour du modèle
*
* Revision 1.5  2003/03/10 15:43:00  tz
* Ajout de la méthode isConnected()
*
* Revision 1.4  2002/11/19 08:44:59  tz
* Gestion de la progression de la tâche.
*
* Revision 1.3  2002/03/27 09:40:15  tz
* Ajout de la méthode setConnected
*
* Revision 1.2  2001/12/19 09:59:17  tz
* Cloture itération IT1.0.0
*
* Revision 1.1.1.1  2001/11/14 08:41:01  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.abs.gui;

//
// Imports système
//
import java.awt.Component;
import javax.swing.JDesktopPane;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: MainWindowInterface
*
* Description:
* Cette interface permet à certaines classes de communiquer avec la
* fenêtre principale de l'application sans connaître la classe
* d'implémentation.
* Par le biais de cette interface, une classe peut:
*  - récupérer l'objet représentant la zone d'affichage des sous-fenêtres,
*  - récupérer l'interface de l'arbre des noeuds graphiques,
*  - afficher une information dans la zone d'état de l'interface graphique,
*  - afficher une boîte de dialogue à l'utilisateur,
*  - récupérer ou modifier l'état de la connexion,
*  - modifier le curseur.
* ----------------------------------------------------------*/
public interface MainWindowInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getDesktopPane
	*
	* Description:
	* Cette méthode permet à une classe de récupérer la référence de
	* l'objet représentant la zone d'affichage des sous-fenêtres.
	*
	* Retourne: Une référence sur un objet JDesktopPane, ou null.
	* ----------------------------------------------------------*/
	public JDesktopPane getDesktopPane();

	/*----------------------------------------------------------
	* Nom: getTreeInterface
	*
	* Description:
	* Cette méthode permet de récupérer une référence sur l'interface
	* TreeInterface, permettant de communiquer avec l'arbre d'affichage
	* des noeuds graphiques.
	*
	* Retourne: Une référence sur un objet TreeInterface, ou null.
	* ----------------------------------------------------------*/
	public TreeInterface getTreeInterface();

	/*----------------------------------------------------------
	* Nom: setStatus
	*
	* Description:
	* Cette méthode permet d'afficher un message dans la barre d'état de
	* l'interface graphique. La barre d'état est la zone d'affichage
	* située en bas de la fenêtre principale de l'application.
	* Si le message à afficher est nul, le message par défaut est affiché
	* ("Connecté" ou "Non connecté").
	*
	* Arguments:
	*  - status: La chaîne de caractères à afficher dans la barre d'état,
	*  - extraInformation: Des informations complémentaires à ajouter dans la
	*    chaîne d'état,
	*  - progress: Un entier indiquant le niveau de progression (afin de mettre
	*    à jour la barre de progression de la barre d'état).
	* ----------------------------------------------------------*/
	public void setStatus(
		String status,
		String[] extraInformation,
		int progress
		);

	/*----------------------------------------------------------
	* Nom: setProgressMaximum
	*
	* Description:
	* Cette méthode permet de définir la valeur représentant la valeur maximale
	* pour la barre de progression. Cette valeur permet d'indiquer à quelle
	* valeur la progression est totale.
	*
	* Arguments:
	*  - maximum: Un entier représentant la valeur maximum de progression.
	* ----------------------------------------------------------*/
	public void setProgressMaximum(
		int maximum
		);

	/*----------------------------------------------------------
	* Nom: showPopup
	*
	* Description:
	* Cette méthode permet à une classe d'afficher une boîte de dialogue
	* à l'utilisateur via la fenêtre principale de l'application.
	* L'affichage d'une boîte de dialogue peut être nécessaire en cas
	* d'erreur, ou en cas de demande de choix à l'utilisateur.
	*
	* Arguments:
	*  - popupType: Le type de boîte de dialogue à afficher. Ce type
	*    correspond aux types définis pour la méthode displayDialog() de
	*    la classe MessageManager de la librairie BVCore/Java,
	*  - message: Le message à afficher dans la boîte de dialogue,
	*  - extraInfo: Un tableau de String contenant des informations
	*    complémentaires.
	*
	* Retourne: Un entier représentant le bouton sur lequel l'utilisateur
	* a cliqué pour fermer la boîte de dialogue. Cette valeur correspond
	* aux types définis dans la classe JOptionPane.
	* ----------------------------------------------------------*/
	public int showPopup(
		String popupType,
		String message,
		String[] extraInfo
		);

	/*----------------------------------------------------------
	* Nom: showPopupForException
	*
	* Description:
	* A l'instar de la méthode showPopup(), cette méthode permet à une classe
	* d'afficher une boîte d'erreur à l'utilisateur lorsqu'une exception a été
	* rencontrée. Elle permet d'afficher une boîte d'erreur normalisée.
	*
	* Arguments:
	*  - message: Le message principal de l'erreur,
	*  - exception: Une référence sur l'exception qui a été capturée et qui
	*    contient des informations complémentaires.
	* ----------------------------------------------------------*/
	public void showPopupForException(
		String message,
		Exception exception
		);

	/*----------------------------------------------------------
	* Nom: setCurrentCursor
	*
	* Description:
	* Cette méthode permet à une classe de modifier le curseur de la souris
	* pour la fenêtre principale. Elle est surtout utilisée pour mettre le
	* curseur d'attente lors du démarrage d'un processeur, et de replacer le
	* curseur normal ensuite.
	* Optionnellement, le curseur est également positionné sur le composant 
	* passé en argument.
	* 
	* Argument:
	*  - cursor: Un entier indiquant le type de cursor a utiliser,
	*  - component: Une référence sur un composant optionnel sur lequel le 
	*    curseur va être positionné.
	* ----------------------------------------------------------*/
	public void setCurrentCursor(
		int cursor,
		Component component
		);

	/*----------------------------------------------------------
	* Nom: setConnected
	*
	* Description:
	* Cette méthode permet d'indiquer à la fenêtre principale si la Console est
	* connectée ou non avec le système I-SIS. En fait, il s'agit d'indiquer si
	* la session a été ouverte sur le Portail.
	*
	* Arguments:
	*  - isConnected: Indique si la Console est connectée (true) ou non (false),
	*  - showProfileLabel: Indique si l'intitulé du profil doit être affiché ou 
	*    non.
	* ----------------------------------------------------------*/
	public void setConnected(
		boolean isConnected,
		boolean showProfileLabel
		);

	/*----------------------------------------------------------
	* Nom: isConnected
	*
	* Description:
	* Cette méthode permet de savoir si la session a été ouverte sur le Portail.
	*
	* Retourne: true si la session est ouverte, false sinon.
	* ----------------------------------------------------------*/
	public boolean isConnected();

	/*----------------------------------------------------------
	* Nom: closeSession
	* 
	* Description:
	* Cette méthode permet de fermer la session avec le Portail, et de 
	* provoquer le désenregistrement de la Console sur celui-ci.
	* 
	* Arguments:
	*  - portalStopped: Indique si la fermeture est dûe à un arrêt du Portail 
	*    (true) ou non (false).
 	* ----------------------------------------------------------*/
 	public void closeSession(
 		boolean portalStopped
 		);

	/*----------------------------------------------------------
	* Nom: exitWindow
	* 
	* Description:
	* Cette méthode permet de fermer la fenêtre principale, et donc de quitter 
	* l'application.
	* 
	* Arguments:
	*  - confirm: Indique si une confirmation doit être demandée (true) ou non 
	*    (false).
	*  - portalStopped: Indique si la fermeture est dûe à un arrêt du Portail 
	*    (true) ou non (false).
	* ----------------------------------------------------------*/
	public void exitWindow(
		boolean confirm,
		boolean portalStopped
		);
}