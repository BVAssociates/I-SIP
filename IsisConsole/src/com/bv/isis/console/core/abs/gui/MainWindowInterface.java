/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/gui/MainWindowInterface.java,v $
* $Revision: 1.11 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de la fen�tre principale de l'application
* DATE:        13/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: MainWindowInterface.java,v $
* Revision 1.11  2009/01/14 12:24:40  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.gui.
*
* Revision 1.10  2005/12/23 13:13:32  tz
* Ajout de la m�thode exitWindow().
*
* Revision 1.9  2005/10/07 08:45:39  tz
* Ajout de la m�thode closeSession().
*
* Revision 1.8  2005/07/01 12:00:44  tz
* Modification de la m�thode setCursor
*
* Revision 1.7  2004/10/13 14:03:57  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.6  2003/12/08 14:38:30  tz
* Mise � jour du mod�le
*
* Revision 1.5  2003/03/10 15:43:00  tz
* Ajout de la m�thode isConnected()
*
* Revision 1.4  2002/11/19 08:44:59  tz
* Gestion de la progression de la t�che.
*
* Revision 1.3  2002/03/27 09:40:15  tz
* Ajout de la m�thode setConnected
*
* Revision 1.2  2001/12/19 09:59:17  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1.1.1  2001/11/14 08:41:01  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.abs.gui;

//
// Imports syst�me
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
* Cette interface permet � certaines classes de communiquer avec la
* fen�tre principale de l'application sans conna�tre la classe
* d'impl�mentation.
* Par le biais de cette interface, une classe peut:
*  - r�cup�rer l'objet repr�sentant la zone d'affichage des sous-fen�tres,
*  - r�cup�rer l'interface de l'arbre des noeuds graphiques,
*  - afficher une information dans la zone d'�tat de l'interface graphique,
*  - afficher une bo�te de dialogue � l'utilisateur,
*  - r�cup�rer ou modifier l'�tat de la connexion,
*  - modifier le curseur.
* ----------------------------------------------------------*/
public interface MainWindowInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: getDesktopPane
	*
	* Description:
	* Cette m�thode permet � une classe de r�cup�rer la r�f�rence de
	* l'objet repr�sentant la zone d'affichage des sous-fen�tres.
	*
	* Retourne: Une r�f�rence sur un objet JDesktopPane, ou null.
	* ----------------------------------------------------------*/
	public JDesktopPane getDesktopPane();

	/*----------------------------------------------------------
	* Nom: getTreeInterface
	*
	* Description:
	* Cette m�thode permet de r�cup�rer une r�f�rence sur l'interface
	* TreeInterface, permettant de communiquer avec l'arbre d'affichage
	* des noeuds graphiques.
	*
	* Retourne: Une r�f�rence sur un objet TreeInterface, ou null.
	* ----------------------------------------------------------*/
	public TreeInterface getTreeInterface();

	/*----------------------------------------------------------
	* Nom: setStatus
	*
	* Description:
	* Cette m�thode permet d'afficher un message dans la barre d'�tat de
	* l'interface graphique. La barre d'�tat est la zone d'affichage
	* situ�e en bas de la fen�tre principale de l'application.
	* Si le message � afficher est nul, le message par d�faut est affich�
	* ("Connect�" ou "Non connect�").
	*
	* Arguments:
	*  - status: La cha�ne de caract�res � afficher dans la barre d'�tat,
	*  - extraInformation: Des informations compl�mentaires � ajouter dans la
	*    cha�ne d'�tat,
	*  - progress: Un entier indiquant le niveau de progression (afin de mettre
	*    � jour la barre de progression de la barre d'�tat).
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
	* Cette m�thode permet de d�finir la valeur repr�sentant la valeur maximale
	* pour la barre de progression. Cette valeur permet d'indiquer � quelle
	* valeur la progression est totale.
	*
	* Arguments:
	*  - maximum: Un entier repr�sentant la valeur maximum de progression.
	* ----------------------------------------------------------*/
	public void setProgressMaximum(
		int maximum
		);

	/*----------------------------------------------------------
	* Nom: showPopup
	*
	* Description:
	* Cette m�thode permet � une classe d'afficher une bo�te de dialogue
	* � l'utilisateur via la fen�tre principale de l'application.
	* L'affichage d'une bo�te de dialogue peut �tre n�cessaire en cas
	* d'erreur, ou en cas de demande de choix � l'utilisateur.
	*
	* Arguments:
	*  - popupType: Le type de bo�te de dialogue � afficher. Ce type
	*    correspond aux types d�finis pour la m�thode displayDialog() de
	*    la classe MessageManager de la librairie BVCore/Java,
	*  - message: Le message � afficher dans la bo�te de dialogue,
	*  - extraInfo: Un tableau de String contenant des informations
	*    compl�mentaires.
	*
	* Retourne: Un entier repr�sentant le bouton sur lequel l'utilisateur
	* a cliqu� pour fermer la bo�te de dialogue. Cette valeur correspond
	* aux types d�finis dans la classe JOptionPane.
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
	* A l'instar de la m�thode showPopup(), cette m�thode permet � une classe
	* d'afficher une bo�te d'erreur � l'utilisateur lorsqu'une exception a �t�
	* rencontr�e. Elle permet d'afficher une bo�te d'erreur normalis�e.
	*
	* Arguments:
	*  - message: Le message principal de l'erreur,
	*  - exception: Une r�f�rence sur l'exception qui a �t� captur�e et qui
	*    contient des informations compl�mentaires.
	* ----------------------------------------------------------*/
	public void showPopupForException(
		String message,
		Exception exception
		);

	/*----------------------------------------------------------
	* Nom: setCurrentCursor
	*
	* Description:
	* Cette m�thode permet � une classe de modifier le curseur de la souris
	* pour la fen�tre principale. Elle est surtout utilis�e pour mettre le
	* curseur d'attente lors du d�marrage d'un processeur, et de replacer le
	* curseur normal ensuite.
	* Optionnellement, le curseur est �galement positionn� sur le composant 
	* pass� en argument.
	* 
	* Argument:
	*  - cursor: Un entier indiquant le type de cursor a utiliser,
	*  - component: Une r�f�rence sur un composant optionnel sur lequel le 
	*    curseur va �tre positionn�.
	* ----------------------------------------------------------*/
	public void setCurrentCursor(
		int cursor,
		Component component
		);

	/*----------------------------------------------------------
	* Nom: setConnected
	*
	* Description:
	* Cette m�thode permet d'indiquer � la fen�tre principale si la Console est
	* connect�e ou non avec le syst�me I-SIS. En fait, il s'agit d'indiquer si
	* la session a �t� ouverte sur le Portail.
	*
	* Arguments:
	*  - isConnected: Indique si la Console est connect�e (true) ou non (false),
	*  - showProfileLabel: Indique si l'intitul� du profil doit �tre affich� ou 
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
	* Cette m�thode permet de savoir si la session a �t� ouverte sur le Portail.
	*
	* Retourne: true si la session est ouverte, false sinon.
	* ----------------------------------------------------------*/
	public boolean isConnected();

	/*----------------------------------------------------------
	* Nom: closeSession
	* 
	* Description:
	* Cette m�thode permet de fermer la session avec le Portail, et de 
	* provoquer le d�senregistrement de la Console sur celui-ci.
	* 
	* Arguments:
	*  - portalStopped: Indique si la fermeture est d�e � un arr�t du Portail 
	*    (true) ou non (false).
 	* ----------------------------------------------------------*/
 	public void closeSession(
 		boolean portalStopped
 		);

	/*----------------------------------------------------------
	* Nom: exitWindow
	* 
	* Description:
	* Cette m�thode permet de fermer la fen�tre principale, et donc de quitter 
	* l'application.
	* 
	* Arguments:
	*  - confirm: Indique si une confirmation doit �tre demand�e (true) ou non 
	*    (false).
	*  - portalStopped: Indique si la fermeture est d�e � un arr�t du Portail 
	*    (true) ou non (false).
	* ----------------------------------------------------------*/
	public void exitWindow(
		boolean confirm,
		boolean portalStopped
		);
}