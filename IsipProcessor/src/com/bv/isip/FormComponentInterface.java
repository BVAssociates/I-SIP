package com.bv.isip;

import com.bv.isis.console.core.common.InnerException;

/**
 * Interface entre EditFormProcessor et ses diff�rents composants qui compose
 * le panel
 * @author BAUCHART
 */
public interface FormComponentInterface {

 
    /**
     * Retourne la valeur choisie ou entr�e par l'utilisateur
     *
     * @return la valeur entr�e dans ce composant
     */
    public String getText();

    /**
     * Initialise la valeur du composant.
     * Si ce composant n'est pas en mesure de d�finir cette valeur, une
     * exception est lev�e. (ex: liste d�roulante)
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
