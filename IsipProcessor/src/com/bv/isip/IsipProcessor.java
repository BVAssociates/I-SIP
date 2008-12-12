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
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.common.IndexedList;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.corbacom.ServiceSessionInterface;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;

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
     * Methode makeFormPanel deleguant la génération du JPanel à une classe
     * externe
     *
     * @return le JPanel central à inserer
     */
    protected JPanel makeFormPanel2()
    {
        return new IsipPanel();
    }


    /**
	* Cette méthode est appelée par le constructeur de la classe afin de
	* construire le JPanel contenant les champs.
    *
    * @return JPanel a inserer dans la JFrame
    */
    protected JPanel makeFormPanel()
            throws InnerException
    {
        JPanel form_panel = new JPanel();
        JComponent form_value;

        form_panel.setLayout(new GridBagLayout());

        int position = 0;
        for (Iterator<String> field = _FormConfiguration.keysIterator(); field.hasNext();) {
            String formId = field.next();

            String formType = _FormConfiguration.getType(formId);
            String formLabel = _FormConfiguration.getLabel(formId);

            //construction du champ
            if (formType.equals("Invisible")) {
                continue;
            } else {


                //on increment le compteur de position
                position++;

                // on ajoute le label
                JLabel form_entry = new JLabel(formLabel,SwingConstants.RIGHT);
                GridBagConstraints constraintLabel = new GridBagConstraints();
                constraintLabel.gridx = 0;
                constraintLabel.gridy = position;
                constraintLabel.weightx = 0.9;
                constraintLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintLabel.insets = new java.awt.Insets(5, 5, 5, 5);
                form_panel.add(form_entry, constraintLabel);

                if (formType.equals("Label")) {
                    //on ajoute la valeur
                    form_value = new JLabel();
                    
                }
                else if(formType.equals("Edit"))
                {
                    form_value = new JTextField("###");
                }
                else if(formType.equals("List"))
                {
                    form_value = new JComboBox(getStatusList());
                }
                else
                    throw new InnerException("Type " + formType + " non reconnu", "Erreur", null);

                GridBagConstraints constraintValue = new GridBagConstraints();
                constraintValue.gridx = 1;
                constraintValue.gridy = position;
                constraintValue.weightx = 1.0;
                constraintValue.anchor = java.awt.GridBagConstraints.WEST;
                constraintValue.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintValue.insets = new java.awt.Insets(5, 5, 5, 5);
                form_panel.add(form_value, constraintValue);

                //on stock le champ qui sera affiché puis modifié
                _fieldObject.put(formId, form_value);
            }
        }

        return form_panel;
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
	protected void makePanel() throws InnerException
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

        populateFormPanel();

		trace_methods.endOfMethod();
	}

    private void populateFormPanel()
    {
        //recuperation des données du noeud courant
        IsisParameter[] data=((GenericTreeObjectNode)getSelectedNode()).getObjectParameters();

        for (int i = 0; i < data.length; i++) {
            if (_fieldObject.containsKey(data[i].name)) {
                JComponent textBox = _fieldObject.get(data[i].name);
                if (textBox instanceof JTextField) {
                    ((JTextField) textBox).setText(data[i].value);
                } else if (textBox instanceof JLabel) {
                    ((JLabel) textBox).setText(data[i].value);
                } else if (textBox instanceof JComboBox) {
                    ((JComboBox)textBox).setSelectedItem(data[i].value);
                }
            }
        }
    }

    /**
    * Elle est appelée lorsque l'utilisateur a cliqué sur le bouton "Valider".
	*/
    public void validateInput() {

    }

    /**
     * Interroge la table STATUS et recupère les differents status
     *
     * @return Liste de Status
     */
    private String[] getStatusList() throws InnerException
    {
        // On recupere l'objet ServiceSession
        GenericTreeObjectNode selectedNode = (GenericTreeObjectNode) getSelectedNode();
        ServiceSessionInterface service_session = selectedNode.getServiceSession();
        IndexedList context = selectedNode.getContext(true);

        // On recupere le Proxy associé
        ServiceSessionProxy session_proxy = new ServiceSessionProxy(service_session);
        // On va chercher les informations dans la table FORM_CONFIG
        String[] result = session_proxy.getSelectResult("ETAT", new String[] {"Name"}, "", "", context);

        //Quirk! suppression entete+ajout etat ""
        result[0]="";
        
        return result;
    }


    /**
	* Cet attribut maintient une référence paramétrée sur un objet
	* JComponent correspondant à une zone de saisie pour une colonne. Le
	* paramétrage correspond au nom de la colonne.
	* Cet référence paramétrée est implémentée sous la forme d'une table de
	* hash.
	*/
	private Hashtable<String,JComponent> _fieldObject;

    private IsipFormConfig _FormConfiguration;
}
