package com.bv.isip;



import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;

public class FormComponentFactory  {

    public FormComponentFactory(GenericTreeObjectNode selected_node)
    {
        _selected_node=selected_node;
    }
    
    
    /**
	* Cette méthode est l'usine de
	* construire le JPanel contenant les champs.
    *
    * @return JPanel a inserer dans la JFrame
    */
    protected FormComponentInterface makeComponent(String component_name, String field_name)
            throws InnerException
    {
        FormComponentInterface form_value;

        if (component_name.equals("Label")) {
            form_value = new FormComponentLabel();
        } else if (component_name.equals("Edit")) {
            form_value = new FormComponentEdit();
        } else if (component_name.equals("List")) {
            form_value = new FormComponentList(_selected_node, field_name);
        } else if (component_name.equals("ListProject")) {
            form_value = new FormComponentListProject(_selected_node, field_name);
        } else if (component_name.equals("EditMulti")) {
            form_value = new FormComponentEditArea();
        } else if (component_name.equals("Bool")) {
            form_value = new FormComponentBool();
        } else {
            throw new InnerException("Type " + component_name + " non reconnu", "Erreur", null);
        }

        return form_value;
    }

    private GenericTreeObjectNode _selected_node;
}
