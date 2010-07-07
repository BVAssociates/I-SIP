package com.bv.isip;

import com.bv.isis.console.core.common.InnerException;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author BAUCHART
 */
public class FormComponentEdit extends JPanel
        implements FormComponentInterface {

    public FormComponentEdit()
    {
        super();
        _component = new JTextField("");
        setLayout(new GridLayout(1, 1));
        add(_component);
    }


    public String getText()
    {
        return _component.getText();
    }

    public void setText(String value) throws InnerException
    {
        _component.setText(value);
    }

    public double getWeighty() {
        return 0.1;
    }
   
    private JTextField _component;
}
