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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import javax.swing.JComponent;

/**
 *
 * Cette classe represente la configuration des champs utilisé dans un
 * IsipProcessor.
 *
 * @author BAUCHART
 * @see IsipProcessor
 *
 */
public class IsipFormConfig extends SimpleSelect
{

    /**
     * ouvre la table FORM_CONFIG qui contient les paramètres d'affichage
     * du panneau de saisie
     * 
     * @param selectedNode : noeud de l'arbre en cours d'exploration
     */
    IsipFormConfig(GenericTreeObjectNode selectedNode) throws InnerException
    {
        super(selectedNode,"FORM_CONFIG");

        Trace trace_methods = TraceAPI.declareTraceMethods("Console",
                "IsipFormConfig", "IsipFormConfig");
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
        return getValue(key,"FORM_LABEL");
    }

    /**
     * Recupere une configuration pour l'entrée donnée en parametre
     *
     * @param Name Nom de l'entrée
     * @return le type correspondant à l'entrée Name
     */
    public String getType(String key)
    {
        return getValue(key,"FORM_TYPE");
    }

    
}