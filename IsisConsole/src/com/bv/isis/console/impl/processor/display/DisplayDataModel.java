/*------------------------------------------------------------
* Copyright (c) 2001 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/display/DisplayDataModel.java,v $
* $Revision: 1.10 $
*
* ------------------------------------------------------------
* DESCRIPTION: Classe de gestion des donn�es du tableau
* DATE:        28/12/2001
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      impl.processor.display
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: DisplayDataModel.java,v $
* Revision 1.10  2009/03/05 09:37:17  jy
* Cr�ation d'une classe interne DataObject.
* Une instance de DataObject contient un objet de GenericTreeObjectNode et une copie de l'ensemble de masques originaux.
*
* Revision 1.9  2005/07/06 10:06:32  tz
* Ajout de la m�thode setFireEventOnChange() et de
* l'attribut _fireEventsOnChange.
*
* Revision 1.8  2005/07/01 12:16:35  tz
* Modification du composant pour les traces
*
* Revision 1.7  2004/11/02 08:55:32  tz
* Ajout de la m�thode removeRow().
*
* Revision 1.6  2004/10/14 07:09:23  tz
* Mise au propre des traces de m�thodes.
*
* Revision 1.5  2004/10/13 13:56:17  tz
* Renommage du package inuit -> isis,
* Adaptation pour corba R1.1.1,
* Mise � jour du mod�le.
*
* Revision 1.4  2002/06/19 12:16:49  tz
* Modification pour processeur d'administration
*
* Revision 1.3  2002/03/27 09:51:02  tz
* Modification pour prise en compte nouvel IDL
*
* Revision 1.2  2002/02/04 10:54:25  tz
* Cloture it�ration IT1.0.1
*
* Revision 1.1  2001/12/28 16:31:34  tz
* Import initial
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.display;

//
// Imports syst�me
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
* Cette classe est une sp�cialisation de la classe DefaultTableModel charg�e de
* g�rer les donn�es � afficher dans le tableau. Les donn�es sont des instances
* de GenericTreeObjectNode. L'ensemble des donn�es est g�r� par la super-classe,
* via un vecteur.
* Elle red�finit les m�thodes isCellEditable() et getValueAt() afin d'�tre
* compatible avec les besoins.
* Les donn�es sont ajout�es via la m�thode add().
* ----------------------------------------------------------*/
public class DisplayDataModel
	extends DefaultTableModel
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: DisplayDataModel
	*
	* Description:
	* Cette m�thode est le constructeur de la classe. Elle n'est pr�sent�e que
	* pour des raisons de lisibilit�.
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
	* Cette m�thode red�finit celle de la super-classe. Elle permet de savoir si
	* une cellule (identifi�e par ses coordonn�es) peut �tre �dit�e. Elle
	* retourne syst�matiquement false.
	*
	* Arguments:
	*  - row: Num�ro de rang de la cellule,
	*  - column: Num�ro de colonne de la cellule.
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
	* Cette m�thode red�finit celle de la super-classe. Elle est utilis�e pour
	* r�cup�rer la valeur qui doit �tre affich�e dans la cellule dont les
	* coordonn�es sont pass�es en argument.
	* La m�thode retourne la valeur du param�tre d'index column du noeud
	* graphique dont le rang est row.
	*
	* Arguments:
	*  - row: Num�ro de rang de la donn�e � r�cup�rer,
	*  - column: Num�ro de colonne de la donn�e � r�cup�rer.
	*
	* Retourne: Un objet contenant la valeur de la donn�e � afficher.
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
		// On v�rifie que le num�ro de rang est correct
		if(row < dataVector.size()) {
			// On r�cup�re l'objet InuitObject situ� � l'index row
			GenericTreeObjectNode object_node =
				((DataObject)dataVector.elementAt(row)).getGenericTreeObjectNode();
			// On v�rifie que le num�ro de colonne est correct
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
	* Cette m�thode permet d'ajouter un rang aux donn�es g�r�es par le mod�le.
	* Le rang ajout� est fourni sous forme de r�f�rence � un objet DataObject.
	* On cr�e un objet de DataObject en encapsulant objectNode passant en 
	* param�tre et une copie de l'ensemble de MaskRule originaux.
	*
	* Arguments:
	*  - objectNode: Une r�f�rence sur l'instance de GenericTreeObjectNode �
	*    ajouter au mod�le de donn�es.
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
		// On ajoute la donn�e au vecteur
		DataObject data_object = new DataObject(objectNode, _originalMaskRules);
		dataVector.add(data_object);
		//dataVector.add(objectNode);
		// On envoie un �v�nement que si c'est autoris�
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
	* Cette m�thode red�finit celle de la super-classe DefaultDataModel. Elle 
	* est appel�e lorsqu'une ligne de donn�es doit �tre supprim�e.
	* Elle d�truit le noeud correspondant � la ligne de donn�es et appelle la 
	* m�thode de la super-classe.
	* 
	* Arguments:
	*  - row: L'indice de la ligne de donn�es � supprimer.
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
		// On v�rifie que l'indice de rang est correct
		if(row < 0 || row >= dataVector.size()) {
			trace_methods.endOfMethod();
			return;
		}
		// On r�cup�re l'objet InuitObject situ� � l'index row
		GenericTreeObjectNode object_node =
			((DataObject)dataVector.elementAt(row)).getGenericTreeObjectNode();
		// On supprime la donn�e du vecteur
		dataVector.removeElementAt(row);
		// On envoie un �v�nement que si c'est autoris�
		if(_fireEventsOnChange == true) {
			fireTableRowsDeleted(row, row);
		}
		// On d�truit le noeud
		object_node.destroy(true);
		trace_methods.endOfMethod();
 	}

	/*----------------------------------------------------------
	* Nom: setFireEventsOnChange
	* 
	* Description:
	* Cette m�thode permet de d�finir si des �v�nements seront �mis ou non 
	* lors des modifications des donn�es.
	* 
	* Arguments:
	*  - fireEventsOnChange: Un bool�en indiquant si les �v�nements doivent 
	*    �tre �mis (true) ou non (false).
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
	* Cette m�thode permet de stocker dans un table _originalMaskRule 
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
	* Cette m�thode se charge de r�cup�rer une copie de _originalMaskRule pour
	* une ligne du tableau.
	* 
	* Arguments:
	* - row: Indiquant le num�ro de la ligne du tableau.
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
	* Cette m�thode permet de r�cup�rer un objet de GenericTreeObjectNode 
	* dans la ligne sp�cifi�e.
	* 
	* Arguments:
	* - row:	Indiquant le num�ro de la ligne de l'objet pass� en 
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
     * Cette classe est une classe priv�e de DiplayDataMoel.
     * Elle permet de repr�senter une instance de GenericTreeObjectNode et une 
     * copie de l'ensemble de MaskRule pour le tableau.
     * ----------------------------------------------------------*/
     private class DataObject
     {
     	// ******************* PUBLIC **********************
     	/*----------------------------------------------------------
     	* Nom: DataObject
     	*
     	* Description:
     	* Cette m�thode est le constructeur de la classe. 
     	* Elle se charge de copier une r�f�rence d'un objet GenericTreeObjectNode 
     	* et de copier et stocker des donn�es de l'ensemble de MaskRule originaux.
     	* 
     	* Arguments:
     	* - genericTreeObjectNode: Un objet de GenericTreeObjectNode � copier.
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
     	* Cette m�thode se charge de r�cup�rer un objet de 
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
     	* Cette m�thode se charge de r�cup�rer un tableau de MaskRule dans un 
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
     	* repr�sentee des donn�es pour une ligne de la table.
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
	* Cet attribut est un bool�en indiquant si des �v�nements doivent �tre �mis
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