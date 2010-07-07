package com.bv.isip;

import com.bv.isis.console.core.common.InnerException;

/**
 * Interface entre EditFormProcessor et ses différents composants qui compose
 * le panel
 * @author BAUCHART
 */
public interface FormComponentInterface {

 
    /**
     * Retourne la valeur choisie ou entrée par l'utilisateur
     *
     * @return la valeur entrée dans ce composant
     */
    public String getText();

    /**
     * Initialise la valeur du composant.
     * Si ce composant n'est pas en mesure de définir cette valeur, une
     * exception est levée. (ex: liste déroulante)
     *
     * @param text
     * @throws InnerException
     */
    public void setText(String value)
            throws InnerException;
    
    /**
     * Renvoie le ratio en hauteur du widget pour un GridBagLayout
     * 
     * @return ratio
     */
    public double getWeighty();
    
}
