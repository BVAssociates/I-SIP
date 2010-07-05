/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits réservés.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/DisplayDataModel.java,v $
* $Revision: 1.9 $
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
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import javax.swing.table.DefaultTableModel;

//
// Imports du projet
//
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
		if(row < dataVector.size())
		{
			// On récupère l'objet InuitObject situé à l'index row
			GenericTreeObjectNode object_node =
				(GenericTreeObjectNode)dataVector.elementAt(row);
			// On vérifie que le numéro de colonne est correct
			IsisParameter[] object_parameters =
				object_node.getObjectParameters();
			if(object_parameters != null && column < object_parameters.length)
			{
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
	* Le rang ajouté est fourni sous forme de référence à un objet
	* GenericTreeObjectNode
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
		dataVector.add(objectNode);
		// On envoie un événement que si c'est autorisé
		if(_fireEventsOnChange == true)
		{
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
		if(row < 0 || row >= dataVector.size())
		{
			trace_methods.endOfMethod();
			return;
		}
		// On récupère l'objet InuitObject situé à l'index row
		GenericTreeObjectNode object_node =
			(GenericTreeObjectNode)dataVector.elementAt(row);
		// On supprime la donnée du vecteur
		dataVector.removeElementAt(row);
		// On envoie un événement que si c'est autorisé
		if(_fireEventsOnChange == true)
		{
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

	// ****************** PROTEGE *********************
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _fireEventsOnChange
	* 
	* Description:
	* Cet attribut est un booléen indiquant si des événements doivent être 
	* émis lors de changement (true) ou non (false).
	* ----------------------------------------------------------*/
	private boolean _fireEventsOnChange;
}