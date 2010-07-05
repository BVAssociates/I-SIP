/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
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
* Classe d�plac�e dans le package com.bv.isis.console.core.abs.gui.
*
* Revision 1.6  2004/10/13 14:03:57  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.5  2004/07/29 12:25:40  tz
* Mise � jour de la documentation
*
* Revision 1.4  2003/12/08 14:38:22  tz
* Mise � jour du mod�le
*
* Revision 1.3  2002/02/04 10:54:24  tz
* Cloture it�ration IT1.0.1
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
import javax.swing.tree.DefaultMutableTreeNode;

//
// Imports du projet
//

/*----------------------------------------------------------
* Nom: TreeInterface
*
* Description:
* Cette interface permet � certaines classes de communiquer avec l'arbre
* d'affichage des noeuds graphiques sans avoir � conna�tre
* l'impl�mentation.
* Par le biais de cette interface, une classe peut:
*  - Signaler l'ajout de noeuds fils � un noeud graphique, ce qui permet
*    la mise � jour de l'affichage de l'arbre,
*  - Signaler la suppression de noeuds fils d'un noeud graphique, ce qui
*    permet la mise � jour de l'affichage de l'arbre,
*  - Signaler la modification de la structure compl�te d'une portion d'arbre, 
*    ce qui permet la mise � jour de l'affichage de l'arbre,
*  - d�clencher l'expansion ou la r�tractation d'une portion de l'arbre,
*  - conna�tre l'�tat d'expansion d'un noeud.
* ----------------------------------------------------------*/
public interface TreeInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: nodesWereInserted
	*
	* Description:
	* Cette m�thode permet de signaler � l'objet charg� de l'affichage
	* de l'arbre des noeuds graphiques que de nouveaux noeuds fils ont �t�
	* ajout�s � un noeud graphique.
	* Il est n�cessaire d'appeler cette m�thode apr�s tout ajout afin que
	* l'arbre affich� refl�te la nouvelle structure des donn�es.
	*
	* Arguments:
	*  - parent: Une r�f�rence sur l'objet graphique p�re auquel ont �t�
	*    ajout�s les nouveaux noeuds,
	*  - indexes: Un tableau d'entier indiquant les indices des noeuds
	*    enfants qui ont �t� ajout�s (l'indice commen�ant � 0).
	* ----------------------------------------------------------*/
	public void nodesWereInserted(
		DefaultMutableTreeNode parent,
		int[] indexes
		);

	/*----------------------------------------------------------
	* Nom: nodesWereRemoved
	*
	* Description:
	* Cette m�thode permet de signaler � l'objet charg� de l'affichage de
	* l'arbre des noeuds graphiques que des noeuds fils ont �t� supprim�s
	* d'un noeud graphique.
	* Il est n�cessaire d'appeler cette m�thode apr�s toute suppression
	* afin que l'arbre affich� refl�te la nouvelle structure des donn�es.
	*
	* Arguments:
	*  - parent: Une r�f�rence sur l'objet graphique p�re duquel ont �t�
	*    supprim�s les noeuds,
	*  - indexes: Un tableau d'entier indiquant les indices des noeuds
	*    enfants qui ont �t� supprim�s (l'indice commen�ant � 0),
	*  - removedNodes: Un tableau de r�f�rences sur les noeuds graphiques
	*    enfants ayant �t� supprim�s.
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
	* Cette m�thode permet de signaler � l'objet charg� de l'affichage de
	* l'arbre des noeuds graphiques qu'un noeud doit �tre �tendu (expanded).
	* Il est n�cessaire d'appeler cette m�thode afin que la portion d'arbre
	* soit effectivement �tendue.
	*
	* Arguments:
	*  - nodeToExpand: Une r�f�rence sur le noeud graphique � �tendre.
	* ----------------------------------------------------------*/
	public void expandNode(
		DefaultMutableTreeNode nodeToExpand
		);

	/*----------------------------------------------------------
	* Nom: collapseNode
	*
	* Description:
	* Cette m�thode permet de signaler � l'objet charg� de l'affichage de
	* l'arbre des noeuds graphiques qu'un noeud doit �tre r�duit (collapsed).
	* Il est n�cessaire d'appeler cette m�thode afin que la portion d'arbre
	* soit effectivement r�duite.
	*
	* Arguments:
	*  - nodeToCollapse: Une r�f�rence sur le noeud graphique � r�duire.
	* ----------------------------------------------------------*/
	public void collapseNode(
		DefaultMutableTreeNode nodeToCollapse
		);

	/*----------------------------------------------------------
	* Nom: nodeStructureChanged
	*
	* Description:
	* Cette m�thode permet de signaler � l'objet charg� de l'affichage de
	* l'arbre des noeuds graphiques que la structure d'un noeud graphique a
	* chang� (d� � l'ajout et � la suppression de noeuds enfants).
	* Il est n�cessaire d'appeler cette m�thode afin que la portion d'arbre
	* soit mise � jour.
	*
	* Arguments:
	*  - node: Une r�f�rence sur le noeud graphique dont la structure a chang�.
	* ----------------------------------------------------------*/
	public void nodeStructureChanged(
		DefaultMutableTreeNode node
		);

	/*----------------------------------------------------------
	* Nom: isNodeExpanded
	*
	* Description:
	* Cette m�thode permet d'interroger l'objet charg� de l'affichage de
	* l'arbre des noeuds graphiques afin de savoir si un noeud pass� en
	* argument est �tendu ou non.
	*
	* Arguments:
	*  - node: Une r�f�rence sur le noeud graphique dont on souhaite conna�tre
	*    son �tat d'expansion.
	*
	* Retourne: true si le noeud est �tendu, false sinon.
	* ----------------------------------------------------------*/
	public boolean isNodeExpanded(
		DefaultMutableTreeNode node
		);
}