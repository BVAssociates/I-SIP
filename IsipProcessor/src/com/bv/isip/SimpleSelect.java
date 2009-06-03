/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bv.isip;

import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.ServiceSessionInterface;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * Cette classe fait un appel à un Select Itools et stocke le resultat dans un
 * tableau.
 * On peut ensuite acceder au données via un Iterator
 *
 * @author BV Associates
 *
 */
public class SimpleSelect
{

    /**
     * Constructeur prenant en parametre le noeud selectionné et le nom de la
     * table.
     * 
     * @param selectedNode : noeud de l'arbre en cours d'exploration
     * @param tableName  : nom de la table qui contient la configuration
     */
    SimpleSelect(MutableTreeNode selectedNode, String tableName) throws InnerException
    {
        this(selectedNode,tableName,new String[] {""}, "");
    }

    /**
     * Constructeur prenant en parametre le noeud selectionné, le nom de la
      * table et les colonnes selectionnées.
     *
     * @param selectedNode
     * @param tableName
     * @param columnsName
     * @throws com.bv.isis.console.common.InnerException
     */
     SimpleSelect(MutableTreeNode selectedNode, String tableName,String[] columnsName) throws InnerException
    {
        this(selectedNode,tableName,columnsName, "");
    }

     /**
      * Constructeur prenant en parametre le noeud selectionné, le nom de la
      * table et les colonnes selectionnées.
      * Permet d'ajouter une condition.
      *
      * @param selectedNode
      * @param tableName
      * @param columnsName
      * @param condition
      * @throws com.bv.isis.console.common.InnerException
      */
    SimpleSelect(MutableTreeNode selectedTreeNode, String tableName,String[] columnsName,String condition)
            throws InnerException
    {
        Trace trace_methods = TraceAPI.declareTraceMethods("Console",
                "IsipTableSelect", "IsipTableSelect");

        if (!(selectedTreeNode instanceof GenericTreeObjectNode)){
            throw new InnerException("Impossible de recuperer l'environnement", "selected TreeNode is not a GenericTreeObjectNode", null);
        }
        GenericTreeObjectNode selectedIsisNode = (GenericTreeObjectNode)selectedTreeNode;

        // On recupere l'objet ServiceSession
        ServiceSessionInterface service_session = selectedIsisNode.getServiceSession();
        IndexedList context = selectedIsisNode.getContext(true);

        // On recupere le Proxy associé
        ServiceSessionProxy session_proxy = new ServiceSessionProxy(service_session);

        // On va chercher les informations dans la table (Wide par securité)
        String[] result = session_proxy.getWideSelectResult(selectedIsisNode.getAgentName(), tableName, columnsName, condition, "", context);

        // On calcule la definition à partir du Select (definition basique)
        _tableDefinition = TreeNodeFactory.buildDefinitionFromSelectResult(result, tableName);

        _tableData = new LinkedHashMap<String, IsisParameter[]>();
        
        for (int i = 1; i < result.length; i++) {
            //On tranforme une ligne du Select en IsisParameter
            IsisParameter[] resultLine = TreeNodeFactory.buildParametersFromSelectResult(result, i, _tableDefinition);
            //On recupere la valeur de la clef pour une ligne
            String key = TreeNodeFactory.buildKeyFromSelectResult(resultLine, _tableDefinition);

            //on stock la ligne
            _tableData.put(key, resultLine);

        }

        trace_methods.endOfMethod();
    }

    /**
     * Methode permettant d'acceder à la definition de la table
     * 
     * @return objet IsisTableDefinition
     */
    public IsisTableDefinition getDefinition()
    {
        return _tableDefinition;
    }

     /**
     * Methode générique pour obtenir une ligne entiere sous forme de tableau
     * de IsisParameter
     * @param key : clef associé à une ligne
     * @return tableau de IsisParameter
     */
    public IsisParameter[] getFirst()
    {
        if (keysIterator().hasNext() ) {
            return _tableData.get(keysIterator().next());
        }
        else {
            return null;
        }
    }

    /**
     * Methode générique pour obtenir une ligne entiere sous forme de tableau
     * de IsisParameter
     * @param key : clef associé à une ligne
     * @return tableau de IsisParameter
     */
    public IsisParameter[] get(String key)
    {
        return _tableData.get(key);
    }

    /**
     * Methode pour rechercher un champ particulier dans la table à partir
     * d'une clef
     *
     * @param key clef representant la ligne
     * @param field champ recherché
     * @return Valeur sous forme texte
     */
    public String getValue(String key, String field)
    {
        if (_tableData.containsKey(key)) {
            return getValue(_tableData.get(key), field);
        }
        else {
            return null;
        }
    }

    /**
     * Methode pour rechercher un champ particulier dans la table à partir
     * d'une ligne entière
     *
     * @param param ligne sous forme d'un tableau de IsisParameter
     * @param field champ recherché
     * @return Valeur sous forme texte
     */
    public String getValue(IsisParameter[] param, String field)
    {
        return TreeNodeFactory.getValueOfParameter(param, field);
    }

    /**
     * Methode retournant un Iterator sur les clef primaires de la table
     *
     * ex :
     * <PRE>
     * for (Iterator<String> field = table.keysIterator(); field.hasNext();) {
     *      String clef = field.next();
     * }
     * </PRE>
     *
     * @return un Iterator
     */
    public Iterator<String> keysIterator()
    {
        return _tableData.keySet().iterator();
    }

    /**
     * Les lignes de la table sont sauvegardés dans une table de hashage ordonnée
     */
    protected LinkedHashMap<String, IsisParameter[]> _tableData;

    /**
     * La definition est sauvegardée en mémoire
     */
    private IsisTableDefinition _tableDefinition;

}