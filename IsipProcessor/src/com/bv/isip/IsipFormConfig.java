/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bv.isip;

import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;

/**
 *
 * Cette classe represente la configuration des champs utilis� dans un
 * IsipProcessor.
 *
 * @author BAUCHART
 * @see IsipProcessor
 *
 */
public class IsipFormConfig extends SimpleSelect
{

    /**
     * ouvre la table FORM_CONFIG qui contient les param�tres d'affichage
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
     * Recupere une configuration pour l'entr�e donn�e en parametre
     * 
     * @param Name Nom de l'entr�e
     * @return le label correspondant � l'entr�e Name
     */
    public String getLabel(String key)
    {
        return getValue(key,"FORM_LABEL");
    }

    /**
     * Recupere une configuration pour l'entr�e donn�e en parametre
     *
     * @param Name Nom de l'entr�e
     * @return le type correspondant � l'entr�e Name
     */
    public String getType(String key)
    {
        return getValue(key,"FORM_TYPE");
    }

    
}