/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/framework/model/ModelInterface.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de d�finitions du mod�le de donn�es
* DATE:        04/06/2008
* AUTEUR:      F. Cossard - H. Doghmi
* PROJET:      I-SIS
* GROUPE:      processor.impl.config
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* Revision 1.2  2008/06/25 fc
* Modification de la classe loadData()
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.config.framework.model;

//
//Imports syst�me
//

//
//Imports du projet
//
import javax.swing.text.StyledDocument;

import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.IsisParameter;

/*----------------------------------------------------------
* Nom: ModelInterface
* 
* Description: 
* Cette interface d�finit les m�thodes que doivent impl�menter les classes 
* du mod�le de donn�es tel que l'enregistrement ou la sauvegarde des donn�es. 
* ----------------------------------------------------------*/
public interface ModelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: loadData
	* 
	* Description: 
	* Cette m�thode charge le mod�le de donn�es � partir de la base 
	* de donn�es. 
	* 
	* Arguments :
	*  - session : Une r�f�rence sur une session de service ouverte pour
	*  effectuer les requ�tes de lecture
	*  - context : Cette r�f�rence correspond � l'ensemble des param�tres
	* 	"exportables" des noeuds travers�s pour atteindre le 
	* 	noeud concern�. A cet ensemble sont ajout�s tous les 
	* 	param�tres du noeud lui-m�me.
	*  - valuesOfTableKey : Un tableau d'IsisParameter contenant les noms
	*  et valeurs des variables composant la cl� de la table.
	* 
	* Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	* sinon.
	* 
	* L�ve: InnerException
    * ----------------------------------------------------------*/
	public boolean loadData(
		ServiceSessionProxy session, 
		IndexedList context, 
		IsisParameter[] valuesOfTableKey
		) 
		throws 
			InnerException;
	
	/*----------------------------------------------------------
	* Nom: saveData
	* 
	* Description: 
	* Cette m�thode enregistre le mod�le de donn�es en base 
	* de donn�es.
	* 
	* Arguments :
	*  - actionId : Le num�ro unique d'action
	*  - context : Cette r�f�rence correspond � l'ensemble des param�tres
	* 	"exportables" des noeuds travers�s pour atteindre le noeud concern�.
	*  A cet ensemble sont ajout�s tous les param�tres du noeud lui-m�me.
	*  - seletedNode : Une r�f�rence sur le noeud s�lectionn�.
	* 
	* Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	* sinon.
	* 
	* L�ve: InnerException
	* ----------------------------------------------------------*/
	public boolean saveData(
		String actionId,
		IndexedList context, 
		GenericTreeObjectNode selectedNode
		)
		throws 
			InnerException;
	
	/*----------------------------------------------------------
	* Nom: end
	* 
	* Description: 
	* Cette m�thode est appel�e lors de la destruction de l'assistant. Elle est 
	* utilis�e pour lib�rer l'espace m�moire utilis� par les variables des
	* classes impl�mentant cette interface.
	* 
	* Retourne : Vrai (true) si tout c'est bien pass�, faux (false) 
	* sinon.
	* ----------------------------------------------------------*/
	public boolean end();
	
	/*----------------------------------------------------------
	* Nom: display
	* 
	* Description: 
	* Cette m�thode est appel�e lors que l'on souhaite afficher les donn�es
	* d'un composant. Elle prend en argument un StyledDocument dans 
	* lequel on ajoute les informations que l'on souhaite afficher et un point 
	* d'entr�e DANS CE DOCUMENT.
	* 
	* Argument :
	*  - styledDocument : le document dans lequel on souhaite afficher
	*  les donn�es du composant.
	*  - offset : un entier indiquant ou �crire dans le document.
	*  
	* L�ve: Exception.
	* ----------------------------------------------------------*/
	public void display(
		StyledDocument styledDocument, 
		int offset
		)
		throws
			Exception ;
	
	/*----------------------------------------------------------
	* Nom: clone
	* 
	* Description: 
	* Cette m�thode permet de retourner une copie de l'�l�ment du mod�le.
	* 
	* Retourne :le clone de l'�l�ment du mod�le sous le type ModelInterface.
	* ----------------------------------------------------------*/
	public ModelInterface clone() ;
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
