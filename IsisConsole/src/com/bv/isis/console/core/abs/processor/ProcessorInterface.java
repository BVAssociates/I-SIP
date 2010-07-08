/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/ProcessorInterface.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de processeur de t�che
* DATE:        13/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ProcessorInterface.java,v $
* Revision 1.8  2009/01/14 12:27:00  tz
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.7  2008/01/31 16:38:28  tz
* Ajout de l'argument postprocessing � la m�thode run().
*
* Revision 1.6  2004/10/22 15:46:06  tz
* Modification profonde de l'interface
*
* Revision 1.5  2004/10/13 14:03:41  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.4  2004/07/29 12:25:25  tz
* Mise � jour de la documentation
*
* Revision 1.3  2002/04/05 15:46:49  tz
* Cloture it�ration IT1.2
*
* Revision 1.2  2001/12/19 09:59:10  tz
* Cloture it�ration IT1.0.0
*
* Revision 1.1.1.1  2001/11/14 08:41:01  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.core.abs.processor;

//
// Imports syst�me
//
import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;

//
// Imports du projet
//
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.abs.gui.ConfigurationPanelInterface;

/*----------------------------------------------------------
* Nom: ProcessorInterface
*
* Description:
* Cette interface permet de d�finir les m�thodes de base des classes
* d'impl�mentation des processeurs de t�che.
* Un processeur de t�che est une unit� de traitement associ�e � une t�che
* particuli�re (comme l'expansion des noeuds graphiques, par exemple).
* Toute unit� de traitement de t�che doit disposer d'une classe publique
* impl�mentant cette interface.
*
* Ces m�thodes sont:
*  - une m�thode de pr�-chargement,
*  - des m�thodes de gestion de la configuration,
*  - une m�thode de lancement du traitement,
*  - une m�thode d'arr�t du traitement de la t�che,
*  - des m�thodes d'interrogation sur les modes de fonctionnement du 
*    processeur.
 * ----------------------------------------------------------*/
public interface ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette m�thode est appel�e lors de l'initialisation de la Console. 
	* Elle permet de pr�-charger le processeur. Les t�ches de pr�-chargement 
	* d�pendent du processeur.
	* ----------------------------------------------------------*/
	public void preLoad();

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette m�thode permet de savoir si le processeur a �t� configur�, ou 
	* s'il a besoin d'�tre configur�.
	* 
	* Retourne: true si le processeur a �t� configur� ou qu'il n'a pas besoin 
	* d'�tre configur�.
	* ----------------------------------------------------------*/
	public boolean isConfigured();

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer les diff�rents panneaux de 
	* configuration n�cessaires pour le processeur. Ces panneaux de 
	* configuration sont retourn�s sous forme de ConfigurationPanelInterface 
	* mais doivent �tre des sp�cialisations de la classe JPanel.
	* 
	* Retourne: Un tableau de ConfigurationPanelInterface correspondant aux 
	* diff�rents panneaux de configuration du processeur.
	* ----------------------------------------------------------*/
	ConfigurationPanelInterface[] getConfigurationPanels();

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette m�thode permet de savoir si le processeur est capable d'�tre 
	* invoqu� � partir d'un �l�ment du menu contextuel d'un noeud de l'arbre 
	* d'exploration.
	* 
	* Retourne: true si le processeur peut �tre invoqu� via un noeud 
	* d'exploration, false sinon.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable();

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette m�thode permet de savoir si le processeur est capable d'�tre 
	* invoqu� � partir d'un �l�ment d'un tableau.
	* 
	* Retourne: true si le processeur peut �tre invoqu� via un �l�ment d'un 
	* tableau, false sinon.
	* ----------------------------------------------------------*/
	public boolean isTableCapable();

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette m�thode permet de savoir si le processeur est capable d'�tre 
	* invoqu� sans s�lection pr�alable d'un noeud d'exploration ou d'un 
	* �l�ment de tableau, c'est-�-dire si le processeur peut �tre ajout� au 
	* menu "Outils" de la Console.
	* 
	* Retourne: true si le processeur peut �tre invoqu� sans passer par un 
	* noeud d'exploration ou par un �l�ment d'un tableau, false sinon.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable();

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer une cha�ne de caract�res correspondant 
	* � la description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription();

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette m�thode permet de r�cup�rer une cha�ne de caract�res 
	* correspondant � l'intitul� de l'�l�ment de menu � ajouter au menu 
	* "Outils" de la Console. Cette m�thode n'est valable que pour les 
	* processeurs globaux (voir la m�thode isGlobalCapable()).
	* 
	* Retourne: L'intitul� de l'�l�ment de menu correspondant au processeur.
	* ----------------------------------------------------------*/
	public String getMenuLabel();

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette m�thode permet d�marrer le processeur de t�che impl�mentant 
	* l'interface ProcessorInterface. Elle permet de communiquer au processeur 
	* un certain nombre d'informations n�cessaires � son fonctionnement.
	* Cette m�thode pouvant �tre bloquante, elle devra �tre ex�cut�e dans un 
	* thread s�par�.
	*
	* Si une erreur est d�tect�e lors de la phase d'initialisation, l'exception
	* InnerException doit �tre lev�e.
	*
	* Arguments:
	*  - windowInterface: Une r�f�rence sur l'interface MainWindowInterface
	*    permettant au processeur de communiquer avec la fen�tre
	*    principale de l'application. Cet argument ne doit pas �tre nul,
	*  - menuItem: Une r�f�rence sur l'option de menu qui a d�clench�
	*    l'ex�cution du processeur de t�che. Cet attribut peut �tre nul,
	*  - parameters: Une cha�ne de caract�res contenant les param�tres
	*    d'ex�cution du processeur. Cet attribut peut �tre nul,
	*  - preprocessing: Une cha�ne de caract�res contenant les instructions
	*    de pr�processing. Cet attribut peut �tre nul,
	*  - postprocessing: Une cha�ne de caract�res contenant les instructions 
	*    de postprocessing. Cet attribut peut �tre nul,
	*  - selectedNode: Une r�f�rence sur l'objet graphique sur lequel le
	*    processeur doit ex�cuter son traitement. Cet attribut peut �tre
	*    nul.
	*
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	public void run(
		MainWindowInterface windowInterface,
		JMenuItem menuItem,
		String parameters,
		String preprocessing,
		String postprocessing,
		DefaultMutableTreeNode selectedNode
		)
		throws
			InnerException;

	/*----------------------------------------------------------
	* Nom: close
	*
	* Description:
	* Cette m�thode permet de signaler au processeur de t�che que son
	* ex�cution doit se terminer. La classe d'impl�mentation devra alors
	* terminer imm�diatement son ex�cution.
	* ----------------------------------------------------------*/
	public void close();

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette m�thode permet de retourner un objet ProcessorInterface �tant 
	* un clone de l'objet courant.
	* 
	* Retourne: Un double de l'objet courant.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate();
}