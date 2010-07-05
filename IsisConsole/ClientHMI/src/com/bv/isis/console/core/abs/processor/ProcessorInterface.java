/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/processor/ProcessorInterface.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de processeur de tâche
* DATE:        13/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.processor
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: ProcessorInterface.java,v $
* Revision 1.8  2009/01/14 12:27:00  tz
* Classe déplacée dans le package com.bv.isis.console.core.abs.processor.
*
* Revision 1.7  2008/01/31 16:38:28  tz
* Ajout de l'argument postprocessing à la méthode run().
*
* Revision 1.6  2004/10/22 15:46:06  tz
* Modification profonde de l'interface
*
* Revision 1.5  2004/10/13 14:03:41  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2004/07/29 12:25:25  tz
* Mise à jour de la documentation
*
* Revision 1.3  2002/04/05 15:46:49  tz
* Cloture itération IT1.2
*
* Revision 1.2  2001/12/19 09:59:10  tz
* Cloture itération IT1.0.0
*
* Revision 1.1.1.1  2001/11/14 08:41:01  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.core.abs.processor;

//
// Imports système
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
* Cette interface permet de définir les méthodes de base des classes
* d'implémentation des processeurs de tâche.
* Un processeur de tâche est une unité de traitement associée à une tâche
* particulière (comme l'expansion des noeuds graphiques, par exemple).
* Toute unité de traitement de tâche doit disposer d'une classe publique
* implémentant cette interface.
*
* Ces méthodes sont:
*  - une méthode de pré-chargement,
*  - des méthodes de gestion de la configuration,
*  - une méthode de lancement du traitement,
*  - une méthode d'arrêt du traitement de la tâche,
*  - des méthodes d'interrogation sur les modes de fonctionnement du 
*    processeur.
 * ----------------------------------------------------------*/
public interface ProcessorInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: preLoad
	* 
	* Description:
	* Cette méthode est appelée lors de l'initialisation de la Console. 
	* Elle permet de pré-charger le processeur. Les tâches de pré-chargement 
	* dépendent du processeur.
	* ----------------------------------------------------------*/
	public void preLoad();

	/*----------------------------------------------------------
	* Nom: isConfigured
	* 
	* Description:
	* Cette méthode permet de savoir si le processeur a été configuré, ou 
	* s'il a besoin d'être configuré.
	* 
	* Retourne: true si le processeur a été configuré ou qu'il n'a pas besoin 
	* d'être configuré.
	* ----------------------------------------------------------*/
	public boolean isConfigured();

	/*----------------------------------------------------------
	* Nom: getConfigurationPanels
	* 
	* Description:
	* Cette méthode permet de récupérer les différents panneaux de 
	* configuration nécessaires pour le processeur. Ces panneaux de 
	* configuration sont retournés sous forme de ConfigurationPanelInterface 
	* mais doivent être des spécialisations de la classe JPanel.
	* 
	* Retourne: Un tableau de ConfigurationPanelInterface correspondant aux 
	* différents panneaux de configuration du processeur.
	* ----------------------------------------------------------*/
	ConfigurationPanelInterface[] getConfigurationPanels();

	/*----------------------------------------------------------
	* Nom: isTreeCapable
	* 
	* Description:
	* Cette méthode permet de savoir si le processeur est capable d'être 
	* invoqué à partir d'un élément du menu contextuel d'un noeud de l'arbre 
	* d'exploration.
	* 
	* Retourne: true si le processeur peut être invoqué via un noeud 
	* d'exploration, false sinon.
	* ----------------------------------------------------------*/
	public boolean isTreeCapable();

	/*----------------------------------------------------------
	* Nom: isTableCapable
	* 
	* Description:
	* Cette méthode permet de savoir si le processeur est capable d'être 
	* invoqué à partir d'un élément d'un tableau.
	* 
	* Retourne: true si le processeur peut être invoqué via un élément d'un 
	* tableau, false sinon.
	* ----------------------------------------------------------*/
	public boolean isTableCapable();

	/*----------------------------------------------------------
	* Nom: isGlobalCapable
	* 
	* Description:
	* Cette méthode permet de savoir si le processeur est capable d'être 
	* invoqué sans sélection préalable d'un noeud d'exploration ou d'un 
	* élément de tableau, c'est-à-dire si le processeur peut être ajouté au 
	* menu "Outils" de la Console.
	* 
	* Retourne: true si le processeur peut être invoqué sans passer par un 
	* noeud d'exploration ou par un élément d'un tableau, false sinon.
	* ----------------------------------------------------------*/
	public boolean isGlobalCapable();

	/*----------------------------------------------------------
	* Nom: getDescription
	* 
	* Description:
	* Cette méthode permet de récupérer une chaîne de caractères correspondant 
	* à la description du processeur.
	* 
	* Retourne: La description du processeur.
	* ----------------------------------------------------------*/
	public String getDescription();

	/*----------------------------------------------------------
	* Nom: getMenuLabel
	* 
	* Description:
	* Cette méthode permet de récupérer une chaîne de caractères 
	* correspondant à l'intitulé de l'élément de menu à ajouter au menu 
	* "Outils" de la Console. Cette méthode n'est valable que pour les 
	* processeurs globaux (voir la méthode isGlobalCapable()).
	* 
	* Retourne: L'intitulé de l'élément de menu correspondant au processeur.
	* ----------------------------------------------------------*/
	public String getMenuLabel();

	/*----------------------------------------------------------
	* Nom: run
	*
	* Description:
	* Cette méthode permet démarrer le processeur de tâche implémentant 
	* l'interface ProcessorInterface. Elle permet de communiquer au processeur 
	* un certain nombre d'informations nécessaires à son fonctionnement.
	* Cette méthode pouvant être bloquante, elle devra être exécutée dans un 
	* thread séparé.
	*
	* Si une erreur est détectée lors de la phase d'initialisation, l'exception
	* InnerException doit être levée.
	*
	* Arguments:
	*  - windowInterface: Une référence sur l'interface MainWindowInterface
	*    permettant au processeur de communiquer avec la fenêtre
	*    principale de l'application. Cet argument ne doit pas être nul,
	*  - menuItem: Une référence sur l'option de menu qui a déclenché
	*    l'exécution du processeur de tâche. Cet attribut peut être nul,
	*  - parameters: Une chaîne de caractères contenant les paramètres
	*    d'exécution du processeur. Cet attribut peut être nul,
	*  - preprocessing: Une chaîne de caractères contenant les instructions
	*    de préprocessing. Cet attribut peut être nul,
	*  - postprocessing: Une chaîne de caractères contenant les instructions 
	*    de postprocessing. Cet attribut peut être nul,
	*  - selectedNode: Une référence sur l'objet graphique sur lequel le
	*    processeur doit exécuter son traitement. Cet attribut peut être
	*    nul.
	*
	* Lève: InnerException.
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
	* Cette méthode permet de signaler au processeur de tâche que son
	* exécution doit se terminer. La classe d'implémentation devra alors
	* terminer immédiatement son exécution.
	* ----------------------------------------------------------*/
	public void close();

	/*----------------------------------------------------------
	* Nom: duplicate
	* 
	* Description:
	* Cette méthode permet de retourner un objet ProcessorInterface étant 
	* un clone de l'objet courant.
	* 
	* Retourne: Un double de l'objet courant.
	* ----------------------------------------------------------*/
	public ProcessorInterface duplicate();
}