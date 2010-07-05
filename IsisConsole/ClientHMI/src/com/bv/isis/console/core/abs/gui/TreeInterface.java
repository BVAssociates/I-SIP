/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/core/abs/gui/TreeInterface.java,v $
* $Revision: 1.7 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de l'arbre d'exploration
* DATE:        13/11/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      abs.gui
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: TreeInterface.java,v $
* Revision 1.7  2009/01/14 12:24:53  tz
* Classe déplacée dans le package com.bv.isis.console.core.abs.gui.
*
* Revision 1.6  2004/10/13 14:03:57  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.5  2004/07/29 12:25:40  tz
* Mise à jour de la documentation
*
* Revision 1.4  2003/12/08 14:38:22  tz
* Mise à jour du modèle
*
* Revision 1.3  2002/02/04 10:54:24  tz
* Cloture itération IT1.0.1
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
import javax.swing.tree.DefaultMutableTreeNode;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: TreeInterface
*
* Description:
* Cette interface permet à certaines classes de communiquer avec l'arbre
* d'affichage des noeuds graphiques sans avoir à connaître
* l'implémentation.
* Par le biais de cette interface, une classe peut:
*  - Signaler l'ajout de noeuds fils à un noeud graphique, ce qui permet
*    la mise à jour de l'affichage de l'arbre,
*  - Signaler la suppression de noeuds fils d'un noeud graphique, ce qui
*    permet la mise à jour de l'affichage de l'arbre,
*  - Signaler la modification de la structure complète d'une portion d'arbre, 
*    ce qui permet la mise à jour de l'affichage de l'arbre,
*  - déclencher l'expansion ou la rétractation d'une portion de l'arbre,
*  - connaître l'état d'expansion d'un noeud.
* ----------------------------------------------------------*/
public interface TreeInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: nodesWereInserted
	*
	* Description:
	* Cette méthode permet de signaler à l'objet chargé de l'affichage
	* de l'arbre des noeuds graphiques que de nouveaux noeuds fils ont été
	* ajoutés à un noeud graphique.
	* Il est nécessaire d'appeler cette méthode après tout ajout afin que
	* l'arbre affiché reflète la nouvelle structure des données.
	*
	* Arguments:
	*  - parent: Une référence sur l'objet graphique père auquel ont été
	*    ajoutés les nouveaux noeuds,
	*  - indexes: Un tableau d'entier indiquant les indices des noeuds
	*    enfants qui ont été ajoutés (l'indice commençant à 0).
	* ----------------------------------------------------------*/
	public void nodesWereInserted(
		DefaultMutableTreeNode parent,
		int[] indexes
		);

	/*----------------------------------------------------------
	* Nom: nodesWereRemoved
	*
	* Description:
	* Cette méthode permet de signaler à l'objet chargé de l'affichage de
	* l'arbre des noeuds graphiques que des noeuds fils ont été supprimés
	* d'un noeud graphique.
	* Il est nécessaire d'appeler cette méthode après toute suppression
	* afin que l'arbre affiché reflète la nouvelle structure des données.
	*
	* Arguments:
	*  - parent: Une référence sur l'objet graphique père duquel ont été
	*    supprimés les noeuds,
	*  - indexes: Un tableau d'entier indiquant les indices des noeuds
	*    enfants qui ont été supprimés (l'indice commençant à 0),
	*  - removedNodes: Un tableau de références sur les noeuds graphiques
	*    enfants ayant été supprimés.
	* ----------------------------------------------------------*/
	public void nodesWereRemoved(
		DefaultMutableTreeNode parent,
		int[] indexes,
		DefaultMutableTreeNode[] removedNodes
		);

	/*----------------------------------------------------------
	* Nom: expandNode
	*
	* Description:
	* Cette méthode permet de signaler à l'objet chargé de l'affichage de
	* l'arbre des noeuds graphiques qu'un noeud doit être étendu (expanded).
	* Il est nécessaire d'appeler cette méthode afin que la portion d'arbre
	* soit effectivement étendue.
	*
	* Arguments:
	*  - nodeToExpand: Une référence sur le noeud graphique à étendre.
	* ----------------------------------------------------------*/
	public void expandNode(
		DefaultMutableTreeNode nodeToExpand
		);

	/*----------------------------------------------------------
	* Nom: collapseNode
	*
	* Description:
	* Cette méthode permet de signaler à l'objet chargé de l'affichage de
	* l'arbre des noeuds graphiques qu'un noeud doit être réduit (collapsed).
	* Il est nécessaire d'appeler cette méthode afin que la portion d'arbre
	* soit effectivement réduite.
	*
	* Arguments:
	*  - nodeToCollapse: Une référence sur le noeud graphique à réduire.
	* ----------------------------------------------------------*/
	public void collapseNode(
		DefaultMutableTreeNode nodeToCollapse
		);

	/*----------------------------------------------------------
	* Nom: nodeStructureChanged
	*
	* Description:
	* Cette méthode permet de signaler à l'objet chargé de l'affichage de
	* l'arbre des noeuds graphiques que la structure d'un noeud graphique a
	* changé (dû à l'ajout et à la suppression de noeuds enfants).
	* Il est nécessaire d'appeler cette méthode afin que la portion d'arbre
	* soit mise à jour.
	*
	* Arguments:
	*  - node: Une référence sur le noeud graphique dont la structure a changé.
	* ----------------------------------------------------------*/
	public void nodeStructureChanged(
		DefaultMutableTreeNode node
		);

	/*----------------------------------------------------------
	* Nom: isNodeExpanded
	*
	* Description:
	* Cette méthode permet d'interroger l'objet chargé de l'affichage de
	* l'arbre des noeuds graphiques afin de savoir si un noeud passé en
	* argument est étendu ou non.
	*
	* Arguments:
	*  - node: Une référence sur le noeud graphique dont on souhaite connaître
	*    son état d'expansion.
	*
	* Retourne: true si le noeud est étendu, false sinon.
	* ----------------------------------------------------------*/
	public boolean isNodeExpanded(
		DefaultMutableTreeNode node
		);
}