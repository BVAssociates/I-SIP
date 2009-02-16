package com.bv.isip;

import com.bv.isis.console.core.common.InnerException;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author BAUCHART
 */
public class FormComponentLabel extends JPanel
        implements FormComponentInterface {

    public FormComponentLabel()
    {
        super();
        //_component = new JLabel();
        _component = new JTextField();
        //_component.setEnabled(false);
        _component.setEditable(false);

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

 
    private JTextField _component;

}
