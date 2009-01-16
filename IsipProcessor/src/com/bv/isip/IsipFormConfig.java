/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bv.isip;

import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.IsisTableColumn;

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
    IsipFormConfig(GenericTreeObjectNode selectedNode,String tableConfig)
            throws InnerException
    {
        super(selectedNode,tableConfig);
        
        
        IsisTableColumn[] form_columns = getDefinition().columns;

        if (_columns.length != form_columns.length) {
            throw new InnerException("",
                    "La table passée en parametre n'a pas les champs attendus",
                    null);
        }
        //TODO columns can be in different order
        for (int i=0; i < _columns.length; i++) {
            if ( ! form_columns[i].name.equals(_columns[i])) {
                throw new InnerException("",
                    "La table passée en parametre n'a pas les champs attendus",
                    null);
            }
        }


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

    private final String[] _columns=new String[] {"FORM_FIELD","FORM_LABEL","FORM_TYPE"};
    
}