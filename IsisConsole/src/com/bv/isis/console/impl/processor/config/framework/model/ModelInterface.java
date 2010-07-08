/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/config/framework/model/ModelInterface.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Interface de définitions du modèle de données
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
// Déclaration du package
package com.bv.isis.console.impl.processor.config.framework.model;

//
//Imports système
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
* Cette interface définit les méthodes que doivent implémenter les classes 
* du modèle de données tel que l'enregistrement ou la sauvegarde des données. 
* ----------------------------------------------------------*/
public interface ModelInterface
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: loadData
	* 
	* Description: 
	* Cette méthode charge le modèle de données à partir de la base 
	* de données. 
	* 
	* Arguments :
	*  - session : Une référence sur une session de service ouverte pour
	*  effectuer les requêtes de lecture
	*  - context : Cette référence correspond à l'ensemble des paramètres
	* 	"exportables" des noeuds traversés pour atteindre le 
	* 	noeud concerné. A cet ensemble sont ajoutés tous les 
	* 	paramètres du noeud lui-même.
	*  - valuesOfTableKey : Un tableau d'IsisParameter contenant les noms
	*  et valeurs des variables composant la clé de la table.
	* 
	* Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	* sinon.
	* 
	* Lève: InnerException
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
	* Cette méthode enregistre le modèle de données en base 
	* de données.
	* 
	* Arguments :
	*  - actionId : Le numéro unique d'action
	*  - context : Cette référence correspond à l'ensemble des paramètres
	* 	"exportables" des noeuds traversés pour atteindre le noeud concerné.
	*  A cet ensemble sont ajoutés tous les paramètres du noeud lui-même.
	*  - seletedNode : Une référence sur le noeud sélectionné.
	* 
	* Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	* sinon.
	* 
	* Lève: InnerException
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
	* Cette méthode est appelée lors de la destruction de l'assistant. Elle est 
	* utilisée pour libèrer l'espace mémoire utilisé par les variables des
	* classes implémentant cette interface.
	* 
	* Retourne : Vrai (true) si tout c'est bien passé, faux (false) 
	* sinon.
	* ----------------------------------------------------------*/
	public boolean end();
	
	/*----------------------------------------------------------
	* Nom: display
	* 
	* Description: 
	* Cette méthode est appelée lors que l'on souhaite afficher les données
	* d'un composant. Elle prend en argument un StyledDocument dans 
	* lequel on ajoute les informations que l'on souhaite afficher et un point 
	* d'entrée DANS CE DOCUMENT.
	* 
	* Argument :
	*  - styledDocument : le document dans lequel on souhaite afficher
	*  les données du composant.
	*  - offset : un entier indiquant ou écrire dans le document.
	*  
	* Lève: Exception.
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
	* Cette méthode permet de retourner une copie de l'élément du modèle.
	* 
	* Retourne :le clone de l'élément du modèle sous le type ModelInterface.
	* ----------------------------------------------------------*/
	public ModelInterface clone() ;
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
}
