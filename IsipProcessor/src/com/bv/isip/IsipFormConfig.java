/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bv.isip;

import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.common.IndexedList;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.ServiceSessionInterface;

/**
 *
 * Cette classe represente la configuration des champs utilisé dans un
 * IsipProcessor.
 *
 * @author BAUCHART
 * @see IsipProcessor
 *
 */
public class IsipFormConfig extends IndexedList
{

    /**
     * Constructeur prenant en parametre le noeud selectionné
     * 
     * @param selectedNode : noeud de l'arbre en cours d'exploration
     * @param tableName  : nom de la table qui contient la configuration
     */
    IsipFormConfig(GenericTreeObjectNode selectedNode, String tableName) throws InnerException
    {
        super();
        Trace trace_methods = TraceAPI.declareTraceMethods("Console",
                "IsipFormConfig", "IsipFormConfig");

        // On recupere l'objet ServiceSession
        ServiceSessionInterface service_session = selectedNode.getServiceSession();
        IndexedList context = selectedNode.getContext(true);

        // On recupere le Proxy associé
        ServiceSessionProxy session_proxy = new ServiceSessionProxy(service_session);
        // On va chercher les informations dans la table FORM_CONFIG
        String[] columns = {""};
        String[] result = session_proxy.getSelectResult(tableName, columns, "", "", context);

        // On calcule la definition à partir du Select
        IsisTableDefinition form_definition = TreeNodeFactory.buildDefinitionFromSelectResult(result, tableName);

        for (int i = 1; i < result.length; i++) {
            //On tranforme une ligne du Select en IsisParameter
            IsisParameter[] resultLine = TreeNodeFactory.buildParametersFromSelectResult(result, i, form_definition);
            //On recupere la valeur de la clef pour une ligne
            String key = TreeNodeFactory.buildKeyFromSelectResult(resultLine, form_definition);

            //on stock la ligne
            put(key, resultLine);

        }

        trace_methods.endOfMethod();
    }

    /**
     * Recupere une configuration pour l'entrée donnée en parametre
     * 
     * @param Name Nom de l'entrée
     * @return le label correspondant à l'entrée Name
     */
    public String getLabel(String key)
    {
        return TreeNodeFactory.getValueOfParameter((IsisParameter[])get(key),"FORM_LABEL");
    }

    /**
     * Recupere une configuration pour l'entrée donnée en parametre
     *
     * @param Name Nom de l'entrée
     * @return le type correspondant à l'entrée Name
     */
    public String getType(String key)
    {
        return TreeNodeFactory.getValueOfParameter((IsisParameter[])get(key),"FORM_TYPE");
    }

    /*@Override*/
    public IsisParameter[] get(String key)
    {
        return (IsisParameter[]) super.get(key);
    }
    

   
}