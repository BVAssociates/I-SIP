package com.bv.isip;

import com.bv.isis.corbacom.IsisParameter;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.abs.gui.MainWindowInterface;
import com.bv.isis.console.abs.processor.ProcessorInterface;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.common.IndexedList;
import com.bv.isis.console.common.InnerException;
import com.bv.isis.console.impl.processor.admin.ExecutionSurveyor;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.corbacom.IsisNodeLabel;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.ServiceSessionInterface;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
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


        _FormConfiguration = new IsipFormConfig((GenericTreeObjectNode)selectedNode);
        
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
                //On créé le composant, mais il ne sera pas ajouter au JPanel
                form_value = new JLabel(formLabel);

            } else if (formType.equals("Separator")) {
                form_value = new JSeparator();
                //on increment le compteur de position
                position++;
                GridBagConstraints constraint = new GridBagConstraints();
                constraint.gridx = 0;
                constraint.gridy = position;
                constraint.gridwidth = 2;
                constraint.fill = GridBagConstraints.HORIZONTAL;
                form_panel.add(form_value,constraint);
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
                } else
                    throw new InnerException("Type " + formType + " non reconnu", "Erreur", null);

                GridBagConstraints constraintValue = new GridBagConstraints();
                constraintValue.gridx = 1;
                constraintValue.gridy = position;
                constraintValue.weightx = 1.0;
                constraintValue.anchor = java.awt.GridBagConstraints.WEST;
                constraintValue.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintValue.insets = new java.awt.Insets(5, 5, 5, 5);
                form_panel.add(form_value, constraintValue);

            }
                //on stock le champ qui sera affiché puis modifié
            _fieldObject.put(formId, form_value);
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
			new JButton("Appliquer");
		// On ajoute le callback sur le bouton
		validate_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				// On appelle la méthode de validation
                //sablier pendant le traitement
                getMainWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, getContentPane());
                validateInput();
                //sablier pendant le traitement
                getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, getContentPane());
            }
		});
		// On crée un panneau avec un GridBagLayout
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints =
                new GridBagConstraints(1, 0, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0);
        JPanel button_panel = new JPanel(layout);
        layout.setConstraints(validate_button, constraints);
        button_panel.add(validate_button);

	       /*
            * TODO : est-ce utile?
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
         */

        // Maintenant, on va créer le bouton Fermer
		JButton cancel_button =
			new JButton("Reset");
		// On ajoute le callback sur le bouton
		cancel_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
                try {
                    getMainWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, getContentPane());
                    // On appelle la méthode de refresh
                    populateFormPanel(true);
                } catch (InnerException ex) {
                    getMainWindowInterface().showPopupForException(
				"Erreur lors de la mise à jour", ex);
                } finally {
                    getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, getContentPane());
                }
			}
		});
	   constraints = new GridBagConstraints(2, 0, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(3, 0, 3, 0), 0, 0);
		layout.setConstraints(cancel_button, constraints);
		button_panel.add(cancel_button);

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
		setPreferredSize(new Dimension(400, 400));

        populateFormPanel(true);

		trace_methods.endOfMethod();
	}

    private IsisParameter[] populateFormPanel(boolean refresh)
            throws InnerException
    {
        // Variable qui stockera les valeurs à afficher
        IsisParameter[] data;

        if (refresh) {
            //recuperation des données depuis la table
            GenericTreeObjectNode node=(GenericTreeObjectNode) getSelectedNode();
            String field_name=((IsisParameter)node.getContext(true).get("FIELD_NAME")).value;
            SimpleSelect HistoTable=
                    //new SimpleSelect(getSelectedNode(), "FIELD_HISTO");
                    new SimpleSelect(getSelectedNode(), node.getTableName());
            data=HistoTable.get(field_name);
        } else {
            //recuperation des données du noeud courant
            data = ((GenericTreeObjectNode) getSelectedNode()).getObjectParameters();
        }

        boolean foundTableKey=false;
        for (int i = 0; i < data.length; i++) {
            
            // verification basique
            if (data[i].name.equals("TABLE_KEY"))
            {
                foundTableKey = true;
            }

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

        // verification basique
        if (!foundTableKey)
        {
            throw new InnerException("Execution Impossible sur ce noeud", "TABLE_KEY non defini", null);
        }
        return data;
    }

    public IsisParameter[] getFormPanelData()
    {
        IsisParameter[] data_from=((GenericTreeObjectNode)getSelectedNode()).getObjectParameters();
        IsisParameter[] data=new IsisParameter[data_from.length];
 
        char sep=data_from[0].quoteCharacter;
        for(int i=0; i < data_from.length; i++)
        {
            data[i]=new IsisParameter(data_from[i].name, data_from[i].value , sep);
            
            JComponent textBox = _fieldObject.get(data[i].name);
            if (textBox instanceof JTextField) {
                data[i].value=((JTextField) textBox).getText();
            } else if (textBox instanceof JLabel) {
                data[i].value=((JLabel) textBox).getText();
            } else if (textBox instanceof JComboBox) {
                data[i].value=(String) ((JComboBox) textBox).getSelectedItem();
            }

        }
        return data;
    }

    /**
    * Elle est appelée lorsque l'utilisateur a cliqué sur le bouton "Valider".
	*/
    public void validateInput()
    {
        IsisParameter[] data;
        IsisParameter[] data_node;
        
        
        
        GenericTreeObjectNode node = ((GenericTreeObjectNode) getSelectedNode());

        data = getFormPanelData();
        data_node = node.getObjectParameters();

        String tableName=node.getTableName();
        StringBuffer command = new StringBuffer();

        command.append(replaceCommand+" into "+tableName +" values \"");

        boolean first=true;
        for (int i = 0; i < data.length; i++) {
            if (!first)
            {
                //TODO : separator from definition
                command.append("@");
            }
            first = false;
            command.append(data[i].value);
        }
        command.append("\"");

        try {
            execute(command.toString());
            //on recupere à nouveau les données de la table à jour
            data=populateFormPanel(true);
            
         } catch (InnerException ex) {
            getMainWindowInterface().showPopupForException(
                    "Erreur lors de l'execution de la commande", ex);
            return;
        }

        //On met les nouvelles données dans le node
        for (int i=0; i < data.length; i++)
        {
            data_node[i].value = TreeNodeFactory.getValueOfParameter(data, data[i].name);
        }

        // changement dynamique de l'icone en cas de changement
        //node.getLabel().icon = "field_"+((String)((JComboBox)_fieldObject.get("ICON")).getSelectedItem());
        node.getLabel().icon = "field_"+TreeNodeFactory.getValueOfParameter(data_node, "ICON");
        getMainWindowInterface().getTreeInterface().nodeStructureChanged(node);
        
        //close();
    }

    /**
	* Cette méthode est destinée à être appelée par les sous-classes afin
	* d'exécuter une commande (quelle qu'elle soit).
	* La méthode instancie un objet ExecutionSurveyor qui est réellement chargé
	* de toute la procédure d'exécution de la commande et d'attendre la fin de
	* celle-ci.
	* Si une erreur est détectée, la méthode gère l'affichage d'une fenêtre
	* indiquant la nature du problème à l'utilisateur.
	*
	* @param  command: Une chaîne contenant la commande à exécuter.
	*/
	protected void execute(
		String command
		) throws InnerException
	{
		String[] message = null;

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsipProcessor", "execute");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

        // recuperation des informations relatives a l'execution
        GenericTreeObjectNode selectedNode = (GenericTreeObjectNode) getSelectedNode();


        //recuperation de l'ID'
        String actionId =
                LogServiceProxy.getActionIdentifier(selectedNode.getAgentName(),
                "Mise a jour champ", null, selectedNode.getServiceName(),
                selectedNode.getServiceType(), selectedNode.getIClesName());

        trace_methods.beginningOfMethod();
        trace_arguments.writeTrace("command=" + command);
		// S'il n'y a pas de valeur, on sort
		if(command == null || command.equals("") == true)
		{
			trace_methods.endOfMethod();
			return;
		}
		// On crée un objet ExecutionSurveyor
		ExecutionSurveyor surveyor = new ExecutionSurveyor();
		// On lance l'exécution
		try
		{
		    surveyor.execute(actionId, command, selectedNode, null);
			// On va générer un message de log
			message = new String[2];
			message[0] = MessageManager.getMessage("&LOG_AdministrationCommand") +
				command;
			message[1] = MessageManager.getMessage("&LOG_CommandResult") +
				MessageManager.getMessage("&LOG_CommandSuccessful");
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'exécution de la commande: " + command);
			trace_errors.writeTrace("L'erreur est: " + exception.getMessage());
			// On va afficher le message à l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotExecuteCommand", exception);
			// On va générer un message de log
			String[] errors =
				CommonFeatures.buildArrayFromString(exception.getMessage());
			message = new String[3 + errors.length];
			int counter = 0;
			message[counter++] =
				MessageManager.getMessage("&LOG_AdministrationCommand") +
				command;
			message[counter++] = MessageManager.getMessage("&LOG_CommandResult") +
				MessageManager.getMessage("&LOG_CommandFailed");
			message[counter++] = MessageManager.getMessage("&LOG_CommandError");
			for(int index = 0 ; index < errors.length ; index ++)
			{
				message[counter++] = errors[index];
			}

            throw exception;
		}
		// On génère la trace de log
		LogServiceProxy.addMessageForAction(actionId, message);
		trace_methods.endOfMethod();
	}

    /**
     * Interroge la table STATUS et recupère les differents etats d'un champ
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

    /**
     * Membre contenant une definition de la configuration permettant d'afficher
     * les differentes boites de dialogues
     */
    private IsipFormConfig _FormConfiguration;

    /**
     * Constante stockant la commande d'insertion
     */
    private final String replaceCommand="ReplaceAndExec_IKOS_FIELD.pl";
}
