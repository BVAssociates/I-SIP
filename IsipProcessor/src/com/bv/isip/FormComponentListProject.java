package com.bv.isip;

import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 *
 * @author BAUCHART
 */
public class FormComponentListProject extends FormComponentList
        implements FormComponentInterface {


    public FormComponentListProject(GenericTreeObjectNode node, String field)
            throws InnerException
    {
        super(node,field);
        

        GridBagConstraints contraints_button = new GridBagConstraints();
        contraints_button.fill = GridBagConstraints.VERTICAL;
        contraints_button.gridx = 1;
        contraints_button.gridy = 0;
        add(makeAdministrate(), contraints_button);
    }



    private JComponent makeAdministrate()
    {
        JButton admin_button=new JButton("+");
        admin_button.setToolTipText("Ajouter");

        admin_button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                newOption();
            }
        });
        return admin_button;
    }

    private void newOption()
    {
        String new_option = JOptionPane.showInputDialog(this, "Ajouter l'entr�e :");

        boolean found_value = false;
        for (int i = 0; i < _component.getItemCount(); i++) {
            if (_component.getItemAt(i).equals(new_option)) {
                found_value = true;
            }
        }

        if (found_value) {
            //TODO print a warning?
            _component.setSelectedItem(new_option);
        }
        else {
            try {
                if (new_option != null) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    String date_auto = dateFormat.format(new Date());
                    int result = JOptionPane.showConfirmDialog(this,
                            "Etes vous sur de vouloir cr��r le projet : \"" + new_option + "\" ?",
                            "Creation de projet",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        execute("Insert INTO PROJECT_TYPE VALUES \"" + new_option + "@@" + date_auto + "@\"");
                        _component.addItem(new_option);
                        _component.setSelectedItem(new_option);
                    }
                }
            } catch (InnerException ex) {
                //TODO
            }
        }
    }
}
