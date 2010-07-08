/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/DisplayDataModel.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des données du tableau
* DATE:        28/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DisplayDataModel.java,v $
* Revision 1.10  2009/03/05 09:37:17  jy
* Création d'une classe interne DataObject.
* Une instance de DataObject contient un objet de GenericTreeObjectNode et une copie de l'ensemble de masques originaux.
*
* Revision 1.9  2005/07/06 10:06:32  tz
* Ajout de la méthode setFireEventOnChange() et de
* l'attribut _fireEventsOnChange.
*
* Revision 1.8  2005/07/01 12:16:35  tz
* Modification du composant pour les traces
*
* Revision 1.7  2004/11/02 08:55:32  tz
* Ajout de la méthode removeRow().
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de méthodes.
*
* Revision 1.5  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise à jour du modèle.
*
* Revision 1.4  2002/06/19 12:16:49  tz
* Modification pour processeur d'administration
*
* Revision 1.3  2002/03/27 09:51:02  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture itération IT1.0.1
*
* Revision 1.1  2001/12/28 16:31:34  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// Déclaration du package
package com.bv.isis.console.impl.processor.display;

//
// Imports système
//
import javax.swing.table.DefaultTableModel;

//
// Imports du projet
//
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.IsisParameter;

/*----------------------------------------------------------
* Nom: DisplayDataModel
*
* Description:
* Cette classe est une spécialisation de la classe DefaultTableModel chargée de
* gérer les données à afficher dans le tableau. Les données sont des instances
* de GenericTreeObjectNode. L'ensemble des données est géré par la super-classe,
* via un vecteur.
* Elle redéfinit les méthodes isCellEditable() et getValueAt() afin d'être
* compatible avec les besoins.
* Les données sont ajoutées via la méthode add().
* ----------------------------------------------------------*/
public class DisplayDataModel
	extends DefaultTableModel
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DisplayDataModel
	*
	* Description:
	* Cette méthode est le constructeur de la classe. Elle n'est présentée que
	* pour des raisons de lisibilité.
	* ----------------------------------------------------------*/
	public DisplayDataModel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDataModel", "DisplayDataModel");

		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: isCellEditable
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle permet de savoir si
	* une cellule (identifiée par ses coordonnées) peut être éditée. Elle
	* retourne systématiquement false.
	*
	* Arguments:
	*  - row: Numéro de rang de la cellule,
	*  - column: Numéro de colonne de la cellule.
	*
	* Retourne: false.
	* ----------------------------------------------------------*/
	public boolean isCellEditable(
		int row,
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDataModel", "isCellEditable");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("row=" + row);
		trace_arguments.writeTrace("column=" + column);
		trace_methods.endOfMethod();
		return false;
	}

	/*----------------------------------------------------------
	* Nom: getValueAt
	*
	* Description:
	* Cette méthode redéfinit celle de la super-classe. Elle est utilisée pour
	* récupérer la valeur qui doit être affichée dans la cellule dont les
	* coordonnées sont passées en argument.
	* La méthode retourne la valeur du paramètre d'index column du noeud
	* graphique dont le rang est row.
	*
	* Arguments:
	*  - row: Numéro de rang de la donnée à récupérer,
	*  - column: Numéro de colonne de la donnée à récupérer.
	*
	* Retourne: Un objet contenant la valeur de la donnée à afficher.
	* ----------------------------------------------------------*/
	public Object getValueAt(
		int row,
		int column
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDataModel", "getValueAt");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("row=" + row);
		trace_arguments.writeTrace("column=" + column);
		// On vérifie que le numéro de rang est correct
		if(row < dataVector.size()) {
			// On récupère l'objet InuitObject situé à l'index row
			GenericTreeObjectNode object_node =
				((DataObject)dataVector.elementAt(row)).getGenericTreeObjectNode();
			// On vérifie que le numéro de colonne est correct
			IsisParameter[] object_parameters =
				object_node.getObjectParameters();
			if(object_parameters != null && column < object_parameters.length) {
				trace_methods.endOfMethod();
				return object_parameters[column].value;
			}
		}
		trace_methods.endOfMethod();
		return "";
	}

	/*----------------------------------------------------------
	* Nom: add
	*
	* Description:
	* Cette méthode permet d'ajouter un rang aux données gérées par le modèle.
	* Le rang ajouté est fourni sous forme de référence à un objet DataObject.
	* On crée un objet de DataObject en encapsulant objectNode passant en 
	* paramètre et une copie de l'ensemble de MaskRule originaux.
	*
	* Arguments:
	*  - objectNode: Une référence sur l'instance de GenericTreeObjectNode à
	*    ajouter au modèle de données.
	* ----------------------------------------------------------*/
	public void add(
		GenericTreeObjectNode objectNode
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDataModel", "add");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("objectNode=" + objectNode);
		// On ajoute la donnée au vecteur
		DataObject data_object = new DataObject(objectNode, _originalMaskRules);
		dataVector.add(data_object);
		//dataVector.add(objectNode);
		// On envoie un événement que si c'est autorisé
		if(_fireEventsOnChange == true) {
			fireTableRowsInserted(dataVector.size() - 1, 
				dataVector.size() - 1);
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: removeRow
	* 
	* Description:
	* Cette méthode redéfinit celle de la super-classe DefaultDataModel. Elle 
	* est appelée lorsqu'une ligne de données doit être supprimée.
	* Elle détruit le noeud correspondant à la ligne de données et appelle la 
	* méthode de la super-classe.
	* 
	* Arguments:
	*  - row: L'indice de la ligne de données à supprimer.
 	* ----------------------------------------------------------*/
 	public void removeRow(
 		int row
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDataModel", "removeRow");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("row=" + row);
		// On vérifie que l'indice de rang est correct
		if(row < 0 || row >= dataVector.size()) {
			trace_methods.endOfMethod();
			return;
		}
		// On récupère l'objet InuitObject situé à l'index row
		GenericTreeObjectNode object_node =
			((DataObject)dataVector.elementAt(row)).getGenericTreeObjectNode();
		// On supprime la donnée du vecteur
		dataVector.removeElementAt(row);
		// On envoie un événement que si c'est autorisé
		if(_fireEventsOnChange == true) {
			fireTableRowsDeleted(row, row);
		}
		// On détruit le noeud
		object_node.destroy(true);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: setFireEventsOnChange
	* 
	* Description:
	* Cette méthode permet de définir si des événements seront émis ou non 
	* lors des modifications des données.
	* 
	* Arguments:
	*  - fireEventsOnChange: Un booléen indiquant si les événements doivent 
	*    être émis (true) ou non (false).
 	* ----------------------------------------------------------*/
 	public void setFireEventsOnChange(
 		boolean fireEventsOnChange
 		)
 	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"DisplayDataModel", "setFireEventsOnChange");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("fireEventsOnChange=" + fireEventsOnChange);
		_fireEventsOnChange = fireEventsOnChange;
		trace_methods.endOfMethod();
 	}
 	
 	/*----------------------------------------------------------
	* Nom: setMaskRules
	*
	* Description:
	* Cette méthode permet de stocker dans un table _originalMaskRule 
	* l'ensemble d'instances de MaskRule originaux.
	* 
	* Arguments:
	* - maskRules: Un tableau de MaskRule
	* ----------------------------------------------------------*/
	public void setMaskRules(
		MaskRule[] maskRules
		)
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"DisplayDataModel", "setMaskRules");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("maskRules= " + maskRules);
		_originalMaskRules = maskRules;
		trace_methods.endOfMethod();
	}

	 /*----------------------------------------------------------
	* Nom: getMaskRulesForRow
	*
	* Description:
	* Cette méthode se charge de récupérer une copie de _originalMaskRule pour
	* une ligne du tableau.
	* 
	* Arguments:
	* - row: Indiquant le numéro de la ligne du tableau.
	* 
	* Retourne: Un tableau de MaskRule.
	* ----------------------------------------------------------*/
    public MaskRule[] getMaskRulesForRow(
    	int row
    	)
    {
    	Trace trace_methods = TraceAPI.declareTraceMethods("Console",
    			"DisplayDataModel", "getMaskRulesForRow");
    	Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

    	trace_methods.beginningOfMethod();
    	trace_arguments.writeTrace("row=" + row);
    	if(row < dataVector.size()) {
        	DataObject data_object = (DataObject)dataVector.elementAt(row);
        	return data_object.getMaskRules();
    	}
    	trace_methods.endOfMethod();
    	return null;
    }
    
    /*----------------------------------------------------------
	* Nom: getParametersForRow
	*
	* Description:
	* Cette méthode permet de récupérer un objet de GenericTreeObjectNode 
	* dans la ligne spécifiée.
	* 
	* Arguments:
	* - row:	Indiquant le numéro de la ligne de l'objet passé en 
	* argument dans tableau.
	* 
	* Retourne: Une instance de GenericTreeObjectNode.
	* ----------------------------------------------------------*/
    public GenericTreeObjectNode getParametersForRow(
    	int row
    	)
    {
    	Trace trace_methods = TraceAPI.declareTraceMethods("Console",
    			"DisplayDataModel", "getParametersForRow");
    	Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

    	trace_methods.beginningOfMethod();
    	trace_arguments.writeTrace("row=" + row);
    	if(row < dataVector.size()) {
        	DataObject data_object = (DataObject)dataVector.elementAt(row);
        	return data_object.getGenericTreeObjectNode();
    	}
    	trace_methods.endOfMethod();
    	return null;
    	
    }
	
	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
 	
 	/*----------------------------------------------------------
     * Nom: DataObject
     *
     * Description:
     * Cette classe est une classe privée de DiplayDataMoel.
     * Elle permet de représenter une instance de GenericTreeObjectNode et une 
     * copie de l'ensemble de MaskRule pour le tableau.
     * ----------------------------------------------------------*/
     private class DataObject
     {
     	// ******************* PUBLIC **********************
     	/*----------------------------------------------------------
     	* Nom: DataObject
     	*
     	* Description:
     	* Cette méthode est le constructeur de la classe. 
     	* Elle se charge de copier une référence d'un objet GenericTreeObjectNode 
     	* et de copier et stocker des données de l'ensemble de MaskRule originaux.
     	* 
     	* Arguments:
     	* - genericTreeObjectNode: Un objet de GenericTreeObjectNode à copier.
     	* - maskRules: L'ensemble de objets de MaskRule originaux.
     	* ----------------------------------------------------------*/
     	public DataObject(
     		GenericTreeObjectNode genericTreeObjectNode, 
     		MaskRule[] maskRules
     		)
     	{
     		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
        			"DataObject", "DataObject");
        	Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

        	trace_methods.beginningOfMethod();
        	trace_arguments.writeTrace("genericTreeObjectNode=" + 
        		genericTreeObjectNode);
        	trace_arguments.writeTrace("maskRules=" + maskRules);
     		_genericTreeObjectNode = genericTreeObjectNode;
     		_maskRules = null;
     		if(maskRules != null) {
     			_maskRules = new MaskRule[maskRules.length];
     			for(int i = 0 ; i < maskRules.length ; i++) {
     				_maskRules[i] = new MaskRule(maskRules[i]);
     			}
     		}
     		trace_methods.endOfMethod();
     	}
     	
     	/*----------------------------------------------------------
     	* Nom: getGenericTreeObjectNode
     	*
     	* Description:
     	* Cette méthode se charge de récupérer un objet de 
     	* GetGenericTreeObjectNode dans le DataObjet.
     	* 
     	* Retourne: un objet de GenericTreeObjectNode.
     	* ----------------------------------------------------------*/
     	public GenericTreeObjectNode getGenericTreeObjectNode()
     	{
     		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
        			"DataObject", "getGenericTreeObjectNode");

        	trace_methods.beginningOfMethod();
        	trace_methods.endOfMethod();
     		return _genericTreeObjectNode;
     	}
     	
     	/*----------------------------------------------------------
     	* Nom: getMaskRules
     	*
     	* Description:
     	* Cette méthode se charge de récupérer un tableau de MaskRule dans un 
     	* objet de DataObjet.
     	* 
     	* Retourne: Un tableau de MaskRule.
     	* ----------------------------------------------------------*/
     	public MaskRule[] getMaskRules()
     	{
     		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
        			"DataObject", "getMaskRules");

        	trace_methods.beginningOfMethod();
        	trace_methods.endOfMethod();
     		return  _maskRules;
     	}

     	// ****************** PROTEGE *********************
     	// ******************* PRIVE **********************
     	/*----------------------------------------------------------
     	* Nom: _genericTreeObjectNode
     	* 
     	* Description:
     	* Cet attribut est une instance de GenericTreeObjectNode, qui 
     	* représentee des données pour une ligne de la table.
     	* ----------------------------------------------------------*/
     	private GenericTreeObjectNode _genericTreeObjectNode;
     	
     	/*----------------------------------------------------------
     	* Nom: _maskRules
     	* 
     	* Description:
     	* Cet attribut est une table de MaskRule indiquant une copie de 
     	* l'ensemble de MaskRule originaux.
     	* ----------------------------------------------------------*/
     	private MaskRule[] _maskRules;
     	
     }
 	
	/*----------------------------------------------------------
	* Nom: _fireEventsOnChange
	* 
	* Description:
	* Cet attribut est un booléen indiquant si des événements doivent être émis
	* lors de changement (true) ou non (false).
	* ----------------------------------------------------------*/
	private boolean _fireEventsOnChange;
	
	/*----------------------------------------------------------
	* Nom: _originalMaskRules
	*
	* Description:
	* Cet attribut est un tableau de type MaskRule, il se charge 
	* de stocker des instances de MaskRule originaux.
	* ----------------------------------------------------------*/
	private MaskRule[] _originalMaskRules;
}