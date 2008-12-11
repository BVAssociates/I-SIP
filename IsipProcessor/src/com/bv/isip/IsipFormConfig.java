/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bv.isip;

import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.common.IndexedList;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.ServiceSessionInterface;
import java.util.Hashtable;
import java.util.Iterator;

/**
 *
 * Cette classe represente la configuration des champs utilisé dans un
 * IsipProcessor.
 *
 * @author BAUCHART
 * @see IsipProcessor
 *
 */
public class IsipFormConfig 
{


    /**
     * Constructeur prenant en parametre le noeud selectionné
     * 
     * @param selectedNode : noeud de l'arbre en cours d'exploration
     * @param tableName  : nom de la table qui contient la configuration
     */
    IsipFormConfig(GenericTreeObjectNode selectedNode,String tableName) throws InnerException
    {
        // On recupere l'objet ServiceSession
        ServiceSessionInterface service_session = selectedNode.getServiceSession();
        IndexedList context = selectedNode.getContext(true);

        // On recupere le Proxy associé
        ServiceSessionProxy session_proxy = new ServiceSessionProxy(service_session);
        // On va chercher les informations dans la table FORM_CONFIG
        String[] columns= {""};
        String[] result = session_proxy.getSelectResult(tableName, columns, "", "", context);

        _configurationEntry = new IndexedList();
        
        for (int i = 1; i < result.length; i++)
        {
            String[] resultArray=result[i].split(",");
            //on stock
            _configurationEntry.put(resultArray[0], result[i]);
            
        }
    }

    /**
     * Recupere une configuration pour l'entrée donnée en parametre
     * 
     * @param Name Nom de l'entrée
     * @return le label correspondant à l'entrée Name
     */
    public String getLabel(String Name)
    {
        return parseEntry(Name)[1];
    }

     /**
     * Recupere une configuration pour l'entrée donnée en parametre
     *
     * @param Name Nom de l'entrée
     * @return le type correspondant à l'entrée Name
     */
    public String getType(String Name)
    {
        return parseEntry(Name)[2];
    }

    /**
     *  
     * @return le nombre de champ a configurer
     */
    public int getFormSize()
    {
        return _configurationEntry.size();
    }

    /**
     * Prend un String en entrée et le decoupe en tableau. Chaque element du
     * tableau renvoyé correspond à une colonne
     * @param Name
     * @return Tableau de String
     */
    private String[] parseEntry(String Name)
    {
        return ((String) _configurationEntry.get(Name)).split(",");
    }

    /**
     * Membre stockant les valeurs de la table de configuration
     */
    private IndexedList _configurationEntry;

}