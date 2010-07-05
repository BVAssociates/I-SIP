/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/SearchableComponentInterface.java,v $
* $Revision: 1.3 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de composant pour recherche
* DATE:        27/09/2005
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SearchableComponentInterface.java,v $
* Revision 1.3  2009/01/14 12:27:13  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.2  2008/02/15 14:12:41  tz
* Ajout de la m�thode getFileFilters().
*
* Revision 1.1  2005/10/07 08:44:57  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.abs.processor;

//
//Imports syst�me
//
import java.io.File;
import javax.swing.filechooser.FileFilter;

//
//Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;


/*----------------------------------------------------------
* Nom: SearchableComponentInterface
* 
* Description:
* Cette interface permet de d�finir les m�thodes d'un composant de processeur 
* graphique dans lequel une recherche sur une cha�ne de caract�res peut �tre 
* effectu�e, ou dont le contenu peut �tre enregistr� dans un fichier.
* Elle d�finit les m�thodes permettant de :
*  - initier une recherche sur une cha�ne,
*  - rechercher la prochaine occurence d'une cha�ne,
*  - enregistrer la position de la derni�re occurence trouv�e d'une cha�ne,
*  - r�cup�rer la position de la derni�re occurence trouv�e d'une cha�ne,
*  - enregistrer le contenu du composant dans un fichier.
* ----------------------------------------------------------*/
public interface SearchableComponentInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: searchData
	* 
	* Description:
	* Cette m�thode permet d'initier une nouvelle recherche � partir d'une cha�ne pass�e en argument.
	* La nouvelle recherche est effectu�e � partir du d�but des donn�es, et non pas � partir de la position de la derni�re occurence trouv�e.
	* Si une occurence de la cha�ne est trouv�e, sa position est enregistr�e.
	* 
	* Arguments:
	*  - stringToSearchFor: La cha�ne � rechercher dans les donn�es.
	* 
	* Retourne: true si la cha�ne a �t� trouv�e dans les donn�es, false sinon.
	* ----------------------------------------------------------*/
	public boolean searchData(
		String stringToSearchFor
		);

	/*----------------------------------------------------------
	* Nom: searchAgain
	* 
	* Description:
	* Cette m�thode permet de poursuivre une recherche de la cha�ne passs�e en 
	* argument � partir de la position de la derni�re occurence trouv�e, et 
	* non pas depuis le d�but des donn�es.
	* La recherche reprend depuis le d�but des donn�es (rembobinage), pour 
	* terminer � la derni�re position connue.
	* 
	* Arguments:
	*  - stringToSearchFor: La cha�ne � rechercher dans les donn�es.
	* 
	* Retourne: true si une occurence de la cha�ne a �t� trouv�e, false sinon.
	* ----------------------------------------------------------*/
	public boolean searchAgain(
		String stringToSearchFor
		);

	/*----------------------------------------------------------
	* Nom: setLastSearchPosition
	* 
	* Description:
	* Cette m�thode permet de d�finir la derni�re position de la recherche 
	* d'une cha�ne de caract�res dans la zone de texte.
	* 
	* Arguments:
	*  - lastSearchPosition: La derni�re position de la recherche dans la zone 
	*    de texte.
 	* ----------------------------------------------------------*/
 	public void setLastSearchPosition(
 		int lastSearchPosition
 		);

	/*----------------------------------------------------------
	* Nom: getLastSearchPosition
	* 
	* Description:
	* Cette m�thode permet de conna�tre la derni�re position de la recherche 
	* d'une cha�ne de caract�res dans la zone de texte.
	* 
	* Retourne: La derni�re position de la recherche dans la zone de texte.
	* ----------------------------------------------------------*/
	public int getLastSearchPosition();

	/*----------------------------------------------------------
	* Nom: saveDataToFile
	* 
	* Description:
	* Cette m�thode permet d'enregistrer les donn�es du composant vers un 
	* fichier pass� en argument. L'enregistrement se fait en mode texte.
	* Le fichier est �cras� s'il existe d�j�.
	* 
	* Arguments:
	*  - file: Une r�f�rence sur un objet File correspondant au fichier 
	*    d'enregistrement des donn�es,
	*  - mainWindowInterface: Une r�f�rence sur l'interface 
	*    MainWindowInterface.
 	* ----------------------------------------------------------*/
 	public void saveDataToFile(
 		File file,
 		MainWindowInterface mainWindowInterface
 		);

	/*----------------------------------------------------------
	* Nom: getFileFilters
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer la liste des extensions de fichier 
	* pr�f�r�es, autres que "toutes", sous la forme d'un tableau de FileFilter.
	* 
	* Retourne: Un tableau de FileFilter.
 	* ----------------------------------------------------------*/
 	public FileFilter[] getFileFilters();
}
