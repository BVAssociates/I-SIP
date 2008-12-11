package com.bv.isip;

import com.bv.isis.corbacom.IsisParameter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.abs.gui.MainWindowInterface;
import com.bv.isis.console.abs.processor.ProcessorInterface;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.processor.ProcessorFrame;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class IsipProcessor extends ProcessorFrame {

    /**
     * Isip contructor
     *
     * @param  closeable
     */
	public IsipProcessor() {
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsipTest", "IsipTest");

        _fieldObject=new Hashtable();

       	trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}
	
	

	public ProcessorInterface duplicate() {
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"IsipTest", "duplicate");
		
		trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
		return new IsipProcessor();
	}

	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"IsipTest", "getDescription");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return MessageManager.getMessage("&DisplayProcessorDescription");
	}
	
    @Override
	public void run (
			MainWindowInterface windowInterface,
			JMenuItem menuItem,
			String parameters,
			String preprocessing,
			String postprocessing,
			DefaultMutableTreeNode selectedNode
			)
			throws
				InnerException
	{

    	super.run(windowInterface, menuItem, parameters, preprocessing, postprocessing, selectedNode);


        _FormConfiguration = new IsipFormConfig((GenericTreeObjectNode)selectedNode,"FORM_CONFIG");
        
		setTitle(menuItem.getText());
		makePanel();
        pack();
		display();
	}

    /**
	* Cette m�thode est appel�e par le constructeur de la classe afin de
	* construire le JPanel contenant les champs.
    *
    * @return JPanel a inserer dans la JFrame
    */
    protected JPanel makeFormPanel()
    {
        JPanel form_panel = new JPanel();
        
        form_panel.setLayout(new GridBagLayout());

        int position = 0;
        for (Enumeration field=_FormConfiguration.keys(); field.hasMoreElements();)
        {
            String formId=(String) field.nextElement();

            String formType=_FormConfiguration.getType(formId);
            String formLabel = _FormConfiguration.getLabel(formId);

            //construction du champ
            if (formType.equals("Invisible")) {
                continue;
            } else if (formType.equals("Label")) {
                //on increment le compteur de position
                position++;
                
                // on ajoute le label
                JLabel form_entry = new JLabel(formLabel);
                GridBagConstraints constraintLabel = new GridBagConstraints();
                constraintLabel.gridx = 0;
                constraintLabel.gridy = position;
                constraintLabel.weightx = 0.9;
                constraintLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintLabel.insets = new java.awt.Insets(10, 10, 10, 10);
                form_panel.add(form_entry, constraintLabel);

                //on ajoute la valeur
                JTextField form_value = new JTextField();
                GridBagConstraints constraintValue = new GridBagConstraints();
                constraintValue.gridx = 1;
                constraintValue.gridy = position;
                constraintValue.weightx = 1.0;
                constraintValue.anchor = java.awt.GridBagConstraints.WEST;
                constraintValue.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintValue.insets = new java.awt.Insets(10, 10, 10, 10);
                form_panel.add(form_value, constraintValue);
                
                //on stock le champ qui sera affich� puis modifi�
                _fieldObject.put(formId, form_value);
            }
        }

        return form_panel;
    }

    /**
	* Cette m�thode est appel�e par le constructeur de la classe afin de
	* construire le JPanel contenant les champs.
    */
    protected JPanel makeButtonPanel()
    {
        // On  cr�er le bouton Valider
		JButton validate_button =
			new JButton("Valider");
		// On ajoute le callback sur le bouton
		validate_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode de validation
				validateInput();
			}
		});
		// On cr�e un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(1, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		layout.setConstraints(validate_button, constraints);
		button_panel.add(validate_button);

		// Maintenant, on va cr�er le bouton Fermer
		JButton close_button =
			new JButton("Fermer");
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la m�thode de fermeture
				close();
			}
		});
	   constraints = new GridBagConstraints(0, 0, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(3, 0, 3, 0), 0, 0);
		layout.setConstraints(close_button, constraints);
		button_panel.add(close_button);

        return button_panel;
    }


    /**
	* Cette m�thode est appel�e par le constructeur de la classe afin de
	* construire la bo�te de dialogue d'administration.
    */
	protected void makePanel()
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsipProcessor", "makePanel");

        // Creation du panneau de saisie
        JPanel form_panel = makeFormPanel();
        getContentPane().add(form_panel, BorderLayout.CENTER);

        JPanel button_panel = makeButtonPanel();
		// On place ce panneau dans la zone sud
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		// On redimensionne la fen�tre
		setSize(800, 800);

        populateFormPanel();

		trace_methods.endOfMethod();
	}

    private void populateFormPanel()
    {
        //recuperation des donn�es du noeud courant
        IsisParameter[] data=((GenericTreeObjectNode)getSelectedNode()).getObjectParameters();

        for (int i=0; i < data.length; i++)
        {
            if (_fieldObject.containsKey(data[i].name))
            {
                ((JTextField) _fieldObject.get(data[i].name)).setText(data[i].value);
            }
        }
    }

    /**
    * Elle est appel�e lorsque l'utilisateur a cliqu� sur le bouton "Valider".
	*/
    public void validateInput() {

    }

    /**
	* Cet attribut maintient une r�f�rence param�tr�e sur un objet
	* DialogObject correspondant � une zone de saisie pour une colonne. Le
	* param�trage correspond au nom de la colonne.
	* Cet r�f�rence param�tr�e est impl�ment�e sous la forme d'une table de
	* hash.
	*/
	private Hashtable _fieldObject;

    private IsipFormConfig _FormConfiguration;
}
