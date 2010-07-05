/*------------------------------------------------------------
* Copyright (c) 2005 par BV Associates. Tous droits réservés.
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
* Classe déplacée dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.2  2008/02/15 14:12:41  tz
* Ajout de la méthode getFileFilters().
*
* Revision 1.1  2005/10/07 08:44:57  tz
* Ajout de l'interface.
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.abs.processor;

//
//Imports système
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
* Cette interface permet de définir les méthodes d'un composant de processeur 
* graphique dans lequel une recherche sur une chaîne de caractères peut être 
* effectuée, ou dont le contenu peut être enregistré dans un fichier.
* Elle définit les méthodes permettant de :
*  - initier une recherche sur une chaîne,
*  - rechercher la prochaine occurence d'une chaîne,
*  - enregistrer la position de la dernière occurence trouvée d'une chaîne,
*  - récupérer la position de la dernière occurence trouvée d'une chaîne,
*  - enregistrer le contenu du composant dans un fichier.
* ----------------------------------------------------------*/
public interface SearchableComponentInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: searchData
	* 
	* Description:
	* Cette méthode permet d'initier une nouvelle recherche à partir d'une chaîne passée en argument.
	* La nouvelle recherche est effectuée à partir du début des données, et non pas à partir de la position de la dernière occurence trouvée.
	* Si une occurence de la chaîne est trouvée, sa position est enregistrée.
	* 
	* Arguments:
	*  - stringToSearchFor: La chaîne à rechercher dans les données.
	* 
	* Retourne: true si la chaîne a été trouvée dans les données, false sinon.
	* ----------------------------------------------------------*/
	public boolean searchData(
		String stringToSearchFor
		);

	/*----------------------------------------------------------
	* Nom: searchAgain
	* 
	* Description:
	* Cette méthode permet de poursuivre une recherche de la chaîne passsée en 
	* argument à partir de la position de la dernière occurence trouvée, et 
	* non pas depuis le début des données.
	* La recherche reprend depuis le début des données (rembobinage), pour 
	* terminer à la dernière position connue.
	* 
	* Arguments:
	*  - stringToSearchFor: La chaîne à rechercher dans les données.
	* 
	* Retourne: true si une occurence de la chaîne a été trouvée, false sinon.
	* ----------------------------------------------------------*/
	public boolean searchAgain(
		String stringToSearchFor
		);

	/*----------------------------------------------------------
	* Nom: setLastSearchPosition
	* 
	* Description:
	* Cette méthode permet de définir la dernière position de la recherche 
	* d'une chaîne de caractères dans la zone de texte.
	* 
	* Arguments:
	*  - lastSearchPosition: La dernière position de la recherche dans la zone 
	*    de texte.
 	* ----------------------------------------------------------*/
 	public void setLastSearchPosition(
 		int lastSearchPosition
 		);

	/*----------------------------------------------------------
	* Nom: getLastSearchPosition
	* 
	* Description:
	* Cette méthode permet de connaître la dernière position de la recherche 
	* d'une chaîne de caractères dans la zone de texte.
	* 
	* Retourne: La dernière position de la recherche dans la zone de texte.
	* ----------------------------------------------------------*/
	public int getLastSearchPosition();

	/*----------------------------------------------------------
	* Nom: saveDataToFile
	* 
	* Description:
	* Cette méthode permet d'enregistrer les données du composant vers un 
	* fichier passé en argument. L'enregistrement se fait en mode texte.
	* Le fichier est écrasé s'il existe déjà.
	* 
	* Arguments:
	*  - file: Une référence sur un objet File correspondant au fichier 
	*    d'enregistrement des données,
	*  - mainWindowInterface: Une référence sur l'interface 
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
	* Cette méthode permet de récupérer la liste des extensions de fichier 
	* préférées, autres que "toutes", sous la forme d'un tableau de FileFilter.
	* 
	* Retourne: Un tableau de FileFilter.
 	* ----------------------------------------------------------*/
 	public FileFilter[] getFileFilters();
}
