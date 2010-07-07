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
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.admin.ExecutionSurveyor;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.LabelFactory;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.corbacom.IsisTableDefinition;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class EditFormProcessor extends ProcessorFrame {
    
    /**
     * Isip contructor
     *
     * @param  closeable
     */
	public EditFormProcessor() {
		super(true);
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"IsipTest", "IsipTest");

        _fieldObject=new Hashtable<String, JComponent>();

       	trace_methods.beginningOfMethod();
		trace_methods.endOfMethod();
	}

	
	
	public ProcessorInterface duplicate() {
		
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"IsipTest", "duplicate");
		
		trace_methods.beginningOfMethod();

		trace_methods.endOfMethod();
		return new EditFormProcessor();
	}

	public String getDescription() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
				"IsipTest", "getDescription");

			trace_methods.beginningOfMethod();
			trace_methods.endOfMethod();
			return MessageManager.getMessage("&DisplayProcessorDescription");
	}
	
    @Override
    public void run(
            MainWindowInterface windowInterface,
            JMenuItem menuItem,
            String parameters,
            String preprocessing,
            String postprocessing,
            DefaultMutableTreeNode selectedNode)
            throws
            InnerException {

        super.run(windowInterface, menuItem, parameters, preprocessing, postprocessing, selectedNode);

        if (parameters.equals("")) {
            throw new InnerException("", "Ce processeur prend un parametre : Table", null);
        }

        // Prepare le traitement des arguments du processeur
        String[] parameters_array = parameters.split("@");

        _FormConfiguration = new EditFormConfig((GenericTreeObjectNode) selectedNode, parameters_array[0]);

        // initialise la definition avec des parametres optionnels
        initTable(parameters_array);

        setTitle(menuItem.getText());
        makePanel();


        populateFormPanel(true);

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
            throws InnerException
    {
        JPanel form_panel = new JPanel();
       
        FormComponentFactory component_factory=new FormComponentFactory((GenericTreeObjectNode)getSelectedNode());

        form_panel.setLayout(new GridBagLayout());

        int position = 0;
        for (Iterator<String> field = _FormConfiguration.keysIterator(); field.hasNext();) {
            String formId = field.next();

            //TODO verifier que le champ existe dans le context ou dans la table

            String formType = _FormConfiguration.getType(formId);
            String formLabel = _FormConfiguration.getLabel(formId);

            //construction du champ
            if (formType.equals("Invisible")) {
                //On créé le composant, mais il ne sera pas ajouter au JPanel
                FormComponentLabel form_value = new FormComponentLabel();
                //on stock le champ qui sera affiché puis modifié
                _fieldObject.put(formId, form_value);

            } else if (formType.equals("Separator")) {
                JSeparator form_sep = new JSeparator();
                //on increment le compteur de position
                position++;
                GridBagConstraints constraint = new GridBagConstraints();
                constraint.gridx = 0;
                constraint.gridy = position;
                constraint.gridwidth = 2;
                constraint.fill = GridBagConstraints.HORIZONTAL;
                form_panel.add(form_sep,constraint);
            } else {


                //on increment le compteur de position
                position++;

                // on ajoute le label
                JLabel form_entry = new JLabel(formLabel,SwingConstants.RIGHT);
                GridBagConstraints constraintLabel = new GridBagConstraints();
                constraintLabel.gridx = 0;
                constraintLabel.gridy = position;
                constraintLabel.weightx = 0.1;
                constraintLabel.anchor = GridBagConstraints.WEST;
                constraintLabel.fill = GridBagConstraints.BOTH;
                constraintLabel.insets = new java.awt.Insets(5, 5, 5, 5);
                form_panel.add(form_entry, constraintLabel);

                //on creer le panel de saisie pour le champ
                FormComponentInterface form_value= component_factory.makeComponent(formType, formId, _tableDefinition);

                GridBagConstraints constraintValue = new GridBagConstraints();
                constraintValue.gridx = 1;
                constraintValue.gridy = position;
                constraintValue.weightx = 0.9;
                constraintValue.weighty = form_value.getWeighty();
                constraintValue.anchor = GridBagConstraints.EAST;
                constraintValue.fill = GridBagConstraints.BOTH;
                constraintValue.insets = new java.awt.Insets(5, 5, 5, 5);
                                
                form_panel.add( (JComponent) form_value, constraintValue);
                
                //on stock le champ qui sera affiché puis modifié
                 _fieldObject.put( formId, (JComponent) form_value);
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
			new JButton("Appliquer");
		// On ajoute le callback sur le bouton
		validate_button.addActionListener(new ActionListener()
		{
			         public void actionPerformed(ActionEvent event)
            {
                try {
                    getMainWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, getContentPane());
                    // On appelle la méthode de validation
                    validateInput();
                } catch (InnerException execption) {
                    getMainWindowInterface().showPopupForException(
                            "Erreur lors de la mise à jour", execption);
                } finally {
                    getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, getContentPane());
                    close();
                }
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
    protected void makePanel() throws InnerException {
        Trace trace_methods = TraceAPI.declareTraceMethods("Console",
                "EditFormProcessor", "makePanel");

        // Creation du panneau de saisie
        JPanel form_panel = makeFormPanel();
        getContentPane().add(form_panel, BorderLayout.CENTER);

        JPanel button_panel = makeButtonPanel();
        // On place ce panneau dans la zone sud
        getContentPane().add(button_panel, BorderLayout.SOUTH);
        // On redimensionne la fenêtre
        //setPreferredSize(new Dimension(400, 400));

        trace_methods.endOfMethod();
    }

    /**
     * Remplit les champs du formulaire. Si refresh est false, on utilise le
     * contexte du noeud pour récuperer les données. Si refresh est true, on
     * va chercher les données sur le disque.
     *
     * @param refresh rafraichir depuis le disque
     * @return une tableau de IsisParameter
     *
     * @throws com.bv.isis.console.common.InnerException
     */
    protected IsisParameter[] populateFormPanel(boolean refresh)
            throws InnerException
    {
        // Variable qui stockera les valeurs à afficher
        IsisParameter[] data;
        
        if (refresh) {
            //recuperation des données depuis la table
            SimpleSelect HistoTable=
                    new SimpleSelect(getSelectedNode(), _tableDefinition.tableName,new String[] {""}, _select_condition);
            data=HistoTable.getFirst();
            if (data == null) {
                throw new InnerException("Les informations ont changés pendant l'edition", "Veuiller fermer et recommencer ", null);
            }
        } else {
            //recuperation des données dans le noeud courant
            GenericTreeObjectNode node =(GenericTreeObjectNode) getSelectedNode();
            if (node.getTableName().equals(_tableDefinition.tableName)) {
                data = node.getObjectParameters();
            }
            else {
                throw new InnerException("","Impossible d'obtenir les informations du noeud", null);
            }
        }

        for (int i = 0; i < data.length; i++) {

            if (_fieldObject.containsKey(data[i].name)) {
                JComponent textBox = _fieldObject.get(data[i].name);
                if (textBox instanceof JTextField) {
                    ((JTextField) textBox).setText(data[i].value);
                } else if (textBox instanceof JTextArea) {
                    String multiligne=data[i].value.replaceAll("#n","\n");
                    ((JTextArea) textBox).setText(multiligne);
                } else if (textBox instanceof JLabel) {
                    ((JLabel) textBox).setText(data[i].value);
                } else if (textBox instanceof JComboBox) {
                    ((JComboBox) textBox).setSelectedItem(data[i].value);
                    
                } else if (textBox instanceof FormComponentInterface) {
                    ((FormComponentInterface)textBox).setText(data[i].value);
                }
            }
        }

        return data;
    }
    

    /**
     * Lit les données du formulaire et retourne un tableau de IsisParamter.
     * Rretourne toutes les valeurs des champs.
     *
     * @return un tableau de IsisParameter
     */
    public IsisParameter[] getFormPanelData()
    {
        IsisParameter[] data_from=((GenericTreeObjectNode)getSelectedNode()).getObjectParameters();
        
        ArrayList<IsisParameter> data=new ArrayList<IsisParameter>();
 
        //char sep=data_from[0].quoteCharacter;

        for(int i=0; i < _tableDefinition.columns.length; i++)
        {
            String field_name = _tableDefinition.columns[i].name;
            String field_value;
            
            JComponent textBox = _fieldObject.get(field_name);
            if (textBox instanceof FormComponentInterface) {
                // recupere la valeur dans le widget
                field_value = ((FormComponentInterface) textBox).getText();
            } else {
                //sinon utilise la valeur dans le contexte
                field_value = TreeNodeFactory.getValueOfParameter(data_from, field_name);
            }
            
            data.add(new IsisParameter(field_name, field_value ,'"'));

        }
        
        return data.toArray(new IsisParameter[0]);
    }

    /**
    * Elle est appelée lorsque l'utilisateur a cliqué sur le bouton "Valider".
	*/
    public void validateInput()
            throws InnerException
    {
        IsisParameter[] data;
        IsisParameter[] data_node;
        
        
        
        GenericTreeObjectNode node = ((GenericTreeObjectNode) getSelectedNode());

        data = getFormPanelData();
        data_node = node.getObjectParameters();

        String tableName=_tableDefinition.tableName;
        StringBuffer command = new StringBuffer();

        command.append(replaceCommand+" into "+tableName +" values \"");

        boolean first=true;
        for (int i = 0; i < data.length; i++) {
            if (!first)
            {
                command.append(_tableDefinition.separator);
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
            // La popup a déjà été lancée par ExecutionSurveyor
            //getMainWindowInterface().showPopupForException(
            //        "Erreur lors de l'execution de la commande", ex);

             // TODO traiter l'erreur
             return;
        }

        refreshLabel(node, true);
        
        //averti l'interface que le contenu a changé
        getMainWindowInterface().getTreeInterface().nodeStructureChanged(node);

        
    }

    /**
     * Recuprere les donnée d'un noeud Item. Si ce noeud à changé, on met
     * à jour le Label et on rafraichi l'arbre.
     *
     * @param node
     */
    protected void refreshLabel(GenericTreeObjectNode node, boolean refresh)
            throws InnerException
    {
        // get the primary keys for current table
        TableDefinitionManager def_cache = TableDefinitionManager.getInstance();
        IsisTableDefinition definition = def_cache.getTableDefinition(node.getAgentName(), node.getIClesName(), node.getServiceType(), node.getDefinitionFilePath());
        def_cache.releaseTableDefinitionLeasing(definition);
        StringBuilder select_condition = new StringBuilder();
        
        

        IsisParameter[] data;
        
        if (refresh) {
            // Construction de la condition du Select pour ne recuperer que
            // la ligne correspondante aux clefs
            for (int k = 0; k < definition.key.length; k++) {
                if (!select_condition.toString().equals("")) {
                    select_condition.append(" AND ");
                }
                String value=((IsisParameter) node.getContext(true).get(definition.key[k])).value;
                select_condition.append(definition.key[k] + "='" + value + "'");
            }
            
            SimpleSelect HistoTable =
                    new SimpleSelect(getSelectedNode(), definition.tableName, new String[]{""}, select_condition.toString());
            data = HistoTable.getFirst();

            if (data != null) {
                IsisParameter[] data_node = node.getObjectParameters();
                //On met les nouvelles données dans le node
                for (int i = 0; i < data.length; i++) {
                    data_node[i].value = TreeNodeFactory.getValueOfParameter(data, data[i].name);
                }
            }
            else {
                //TODO : should show to the user that node does not exists anymore
                return;
            }
        }

               
        try {
            
            // recalcul du label avec le nouveau context
            LabelFactory.createLabel(node.getAgentName(), node.getIClesName(),
                    node.getServiceType(), node, node.getContext(true));

        } catch (InnerException exception) {
            Trace trace_errors = TraceAPI.declareTraceErrors("Console");

            trace_errors.writeTrace(
                    "Erreur lors de la modification du Label: " +
                    exception.getMessage());
            throw new InnerException("Erreur lors de la modification du Label", exception.getMessage(), exception);
        }
        
    }

    
    /**
     * Initialise le membre _tableDefinition avec la definition extraite
     * du node en cours d'exploration
     */
    protected void initTable( String[] parameters_array )
            throws InnerException
    {
        // Ce processeur ne fonctionne pas sur les noeud Table
        if (getSelectedNode() instanceof GenericTreeClassNode) {
            throw new InnerException("", "", null);
        }
        
        //traite les parametres (aucun pour l'instant)
        //if ( parameters_array.lengh > 0 ) {
        //}
        
        // get the primary keys for current table
        TableDefinitionManager def_cache = TableDefinitionManager.getInstance();
        GenericTreeObjectNode node = (GenericTreeObjectNode)getSelectedNode();
        IsisTableDefinition definition= def_cache.getTableDefinition(node.getAgentName(),node.getIClesName(), node.getServiceType(), node.getDefinitionFilePath());
        
        // "clone" manuel de la definition
        _tableDefinition = new IsisTableDefinition(
                definition.tableName,
                definition.definitionFilePath,
                definition.source,
                definition.separator,
                definition.header,
                definition.type,
                definition.owner,
                definition.key,
                definition.sort,
                definition.notNull,
                definition.columns,
                definition.foreignKeys,
                definition.labels) ;
                
        def_cache.releaseTableDefinitionLeasing(definition);
        

        // Construction de la condition du Select pour ne recuperer que
        // la ligne correspondante aux clefs
        for (int k = 0; k < _tableDefinition.key.length; k++) {
            if (!_select_condition.equals("")) {
                _select_condition += " AND ";
            }
            _select_condition += _tableDefinition.key[k] + "=" + ((IsisParameter) node.getContext(true).get(_tableDefinition.key[k])).value;
        }
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
			"EditFormProcessor", "execute");
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
	* Cet attribut maintient une référence paramétrée sur un objet
	* JComponent correspondant à une zone de saisie pour une colonne. Le
	* paramétrage correspond au nom de la colonne.
	* Cet référence paramétrée est implémentée sous la forme d'une table de
	* hash.
	*/
	protected Hashtable<String,JComponent> _fieldObject;

    /**
     * Membre contenant une definition de la configuration permettant d'afficher
     * les differentes boites de dialogues
     */
    protected EditFormConfig _FormConfiguration;

    /**
     * Constante stockant la commande d'insertion
     */
    //TODO prévoir l'ajout/suppression?
    protected final String replaceCommand="ReplaceAndExec";

     /**
     * Membre stockant la definition de la table en cours d'edition
     */
    protected IsisTableDefinition _tableDefinition;

    /**
     * Membre stockant la condition permettant de retrouver la ligne en cours
     * d'edition
     */
    protected  String _select_condition="";
}
