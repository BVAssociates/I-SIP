/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bv.isip;

import com.bv.isis.console.core.common.InnerException;
import com.bv.isip.utils.Base64;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author BAUCHART
 */
class FormComponentEditArea extends JPanel
        implements FormComponentInterface
{

    public FormComponentEditArea()
    {
        super();
        _component = new JTextArea("");
        _component.setRows(5);

        setLayout(new GridLayout(1, 1));
        add(new JScrollPane(_component));
    }

    public String getText()
    {
        byte[] text = _component.getText().getBytes();
        return Base64.encodeBytes(text);
    }

    public void setText(String value) throws InnerException
    {
        _component.setText(new String(Base64.decode(value)));
    }
    private JTextArea _component;
}
