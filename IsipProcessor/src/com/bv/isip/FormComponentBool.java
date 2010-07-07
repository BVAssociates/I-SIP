package com.bv.isip;

import com.bv.isis.console.core.common.InnerException;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @author BAUCHART
 */
public class FormComponentBool extends JPanel
        implements FormComponentInterface {

    public FormComponentBool()
    {
        super();
        _component = new JCheckBox();
        setLayout(new GridLayout(1, 1));
        add(_component);
    }


    public String getText()
    {
        if ( _component.getSelectedObjects() == null) {
            return "0";
        }
        else {
            return "1";
        }
    }

    public void setText(String value) throws InnerException
    {
        if ( value.matches("1|y|o")) {
            _component.setSelected(true);
        }
        else {
            _component.setSelected(false);
        }
    }

    public double getWeighty() {
        return 0.1;
    }

    private JCheckBox _component;
}
