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
import com.sun.jmx.snmp.Enumerated;
import java.util.Enumeration;
import java.util.Hashtable;

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
	* Cette méthode est appelée par le constructeur de la classe afin de
	* construire le JPanel contenant les champs.
    *
    * @return JPanel a inserer dans la JFrame
    */
    protected JPanel makeFormPanel()
    {
        //JPanel form_panel = new JPanel();
        //form_panel.setLayout(new BorderLayout(10, 10));

        return new IsipPanel(_columnObject);
    }

    /**
	* Cette méthode est appelée par le constructeur de la classe afin de
	* construire le JPanel contenant les champs.
    */
    protected JPanel makeButtonPanel()
    {
        // On  créer le bouton Valider
		JButton validate_button =
			new JButton("Valider");
		// On ajoute le callback sur le bouton
		validate_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de validation
				validateInput();
			}
		});
		// On crée un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints =
			new GridBagConstraints(1, 0, 1, 1, 100, 100,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(3, 0, 3, 0), 0, 0);
		JPanel button_panel = new JPanel(layout);
		layout.setConstraints(validate_button, constraints);
		button_panel.add(validate_button);

		// Maintenant, on va créer le bouton Fermer
		JButton close_button =
			new JButton("Fermer");
		// On ajoute le callback sur le bouton
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de fermeture
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
	* Cette méthode est appelée par le constructeur de la classe afin de
	* construire la boîte de dialogue d'administration.
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
		// On redimensionne la fenêtre
		setSize(400, 400);
		trace_methods.endOfMethod();
	}

    private void populateFormPanel()
    {
        IsisParameter parameter;
    }

    /**
    * Elle est appelée lorsque l'utilisateur a cliqué sur le bouton "Valider".
	*/
    public void validateInput() {

    }

    /**
	* Cet attribut maintient une référence paramétrée sur un objet
	* DialogObject correspondant à une zone de saisie pour une colonne. Le
	* paramétrage correspond au nom de la colonne.
	* Cet référence paramétrée est implémentée sous la forme d'une table de
	* hash.
	*/
	private Hashtable _columnObject;

    private IsipFormConfig _FormConfiguration;
}
