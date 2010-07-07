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
        _component.setColumns(30);
        _component.setLineWrap(true);
        _component.setWrapStyleWord(true);

       JScrollPane  areaScroll = new JScrollPane(_component,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
       
       //rustine pour forcer le widget à se redimensionner
       setLayout(new GridLayout(1, 1));
       
       add(areaScroll);
    }

    public String getText()
    {
        byte[] text = _component.getText().getBytes();
        if (text.length == 0) {
            return "";
        }
        return Base64.encodeBytes(text,Base64.DONT_BREAK_LINES | Base64.GZIP);
    }

    public void setText(String value) throws InnerException
    {
        byte[] decoded=Base64.decode(value);
        if (decoded==null) {
            throw new InnerException("Impossible d'afficher le champ",
                    "Problème lors du décodage de la valeur",
                    null);
        }
        _component.setText(new String(decoded));
    }
    
    public double getWeighty() {
        return 3;
    }
    
    private JTextArea _component;
}
