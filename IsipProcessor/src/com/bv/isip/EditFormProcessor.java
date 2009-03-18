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
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import com.bv.core.message.MessageManager;
import com.bv.core.trace.Trace;
import com.bv.core.trace.TraceAPI;
import com.bv.isis.console.core.abs.gui.MainWindowInterface;
import com.bv.isis.console.core.abs.processor.ProcessorInterface;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.admin.ExecutionSurveyor;
import com.bv.isis.console.node.GenericTreeClassNode;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.node.LabelFactory;
import com.bv.isis.console.node.TreeNodeFactory;
import com.bv.isis.console.processor.ProcessorFrame;
import com.bv.isis.corbacom.IsisForeignKeyLink;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.ServiceSessionInterface;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import org.jacorb.transaction.Sleeper;

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

        if (parameters.equals("")) {
            throw new InnerException("", "Ce processeur prend un parametre : Table", null);
        }

        _FormConfiguration = new EditFormConfig((GenericTreeObjectNode)selectedNode,parameters);

        initTable();
        
		setTitle(menuItem.getText());
		makePanel();


        populateFormPanel(true);
        
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
            throws InnerException
    {
        JPanel form_panel = new JPanel();
        JComponent form_value;
        
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
                //On cr�� le composant, mais il ne sera pas ajouter au JPanel
                form_value = new FormComponentLabel();
                //on stock le champ qui sera affich� puis modifi�
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
                constraintLabel.weightx = 0.9;
                constraintLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintLabel.insets = new java.awt.Insets(5, 5, 5, 5);
                form_panel.add(form_entry, constraintLabel);

                //on creer le panel de saisie pour le champ
                form_value=(JComponent) component_factory.makeComponent(formType, formId);

                GridBagConstraints constraintValue = new GridBagConstraints();
                constraintValue.gridx = 1;
                constraintValue.gridy = position;
                constraintValue.weightx = 1.0;
                constraintValue.anchor = java.awt.GridBagConstraints.WEST;
                constraintValue.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintValue.insets = new java.awt.Insets(5, 5, 5, 5);
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
			new JButton("Appliquer");
		// On ajoute le callback sur le bouton
		validate_button.addActionListener(new ActionListener()
		{
			         public void actionPerformed(ActionEvent event)
            {
                try {
                    getMainWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, getContentPane());
                    // On appelle la m�thode de validation
                    validateInput();
                } catch (InnerException execption) {
                    getMainWindowInterface().showPopupForException(
                            "Erreur lors de la mise � jour", execption);
                } finally {
                    getMainWindowInterface().setCurrentCursor(Cursor.DEFAULT_CURSOR, getContentPane());
                    close();
                }
            }
		});
		// On cr�e un panneau avec un GridBagLayout
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
         */

        // Maintenant, on va cr�er le bouton Fermer
		JButton cancel_button =
			new JButton("Reset");
		// On ajoute le callback sur le bouton
		cancel_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
                try {
                    getMainWindowInterface().setCurrentCursor(Cursor.WAIT_CURSOR, getContentPane());
                    // On appelle la m�thode de refresh
                    populateFormPanel(true);
                } catch (InnerException ex) {
                    getMainWindowInterface().showPopupForException(
				"Erreur lors de la mise � jour", ex);
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
	* Cette m�thode est appel�e par le constructeur de la classe afin de
	* construire la bo�te de dialogue d'administration.
    */
	protected void makePanel() throws InnerException
	{
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"EditFormProcessor", "makePanel");
        
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();

        // Creation du panneau de saisie
        JPanel form_panel = makeFormPanel();
        jScrollPane1.setViewportView(form_panel);
        getContentPane().add(jScrollPane1, BorderLayout.CENTER);

        JPanel button_panel = makeButtonPanel();
		// On place ce panneau dans la zone sud
		getContentPane().add(button_panel, BorderLayout.SOUTH);
		// On redimensionne la fen�tre
		//setPreferredSize(new Dimension(400, 400));

		trace_methods.endOfMethod();
	}

    /**
     * Remplit les champs du formulaire. Si refresh est false, on utilise le
     * contexte du noeud pour r�cuperer les donn�es. Si refresh est true, on
     * va chercher les donn�es sur le disque.
     *
     * @param refresh rafraichir depuis le disque
     * @return une tableau de IsisParameter
     *
     * @throws com.bv.isis.console.common.InnerException
     */
    protected IsisParameter[] populateFormPanel(boolean refresh)
            throws InnerException
    {
        // Variable qui stockera les valeurs � afficher
        IsisParameter[] data;
        
        if (refresh) {
            //recuperation des donn�es depuis la table
            SimpleSelect HistoTable=
                    new SimpleSelect(getSelectedNode(), _tableDefinition.tableName,new String[] {""}, _select_condition);
            data=HistoTable.getFirst();
            if (data == null) {
                throw new InnerException("Les informations ont chang�s pendant l'edition", "Veuiller fermer et recommencer ", null);
            }
        } else {
            //recuperation des donn�es dans le noeud courant
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
     * Fonction pr�vue pour contourner le problemes des accents dans le Administrate
     * Probl�me depuis r�solu
     *
     * @param phrase
     * @return
     */
    @Deprecated
    public String removeAccents(String phrase) {
        String PLAIN_ASCII =
      "AaEeIiOoUu"    // grave
    + "AaEeIiOoUuYy"  // acute
    + "AaEeIiOoUuYy"  // circumflex
    + "AaOoNn"        // tilde
    + "AaEeIiOoUuYy"  // umlaut
    + "Aa"            // ring
    + "Cc"            // cedilla
    + "OoUu"          // double acute
    ;

    String UNICODE =
 "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
+ "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
+ "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
+ "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
+ "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
+ "\u00C5\u00E5"
+ "\u00C7\u00E7"
+ "\u0150\u0151\u0170\u0171"
;
       if (phrase == null) return null;
       StringBuffer sb = new StringBuffer();
       int n = phrase.length();
       for (int i = 0; i < n; i++) {
          char c = phrase.charAt(i);
          int pos = UNICODE.indexOf(c);
          if (pos > -1){
              sb.append(PLAIN_ASCII.charAt(pos));
          }
          else {
              sb.append(c);
          }
       }
       return sb.toString();


    }

    /**
     * Lit les donn�es du formulaire et retourne un tableau de IsisParamter.
     * Ne retourne que les valeurs des champs �ditables et les clefs primaires.
     *
     * @return un tableau de IsisParameter
     */
    public IsisParameter[] getFormPanelData()
    {
        IsisParameter[] data_from=((GenericTreeObjectNode)getSelectedNode()).getObjectParameters();
        ArrayList<IsisParameter> data=new ArrayList<IsisParameter>();
 
        char sep=data_from[0].quoteCharacter;

        int j=0;
        for(int i=0; i < data_from.length; i++)
        {
            JComponent textBox = _fieldObject.get(data_from[i].name);
            if (textBox instanceof FormComponentInterface) {
                data.add(new IsisParameter(data_from[i].name,
                        ((FormComponentInterface) textBox).getText() ,
                        sep));
            } else {
                data.add(data_from[i]);
            }

        }
        return data.toArray(new IsisParameter[0]);
    }

    /**
    * Elle est appel�e lorsque l'utilisateur a cliqu� sur le bouton "Valider".
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
            //on recupere � nouveau les donn�es de la table � jour
            data=populateFormPanel(true);
            
         } catch (InnerException ex) {
            // La popup a d�j� �t� lanc�e par ExecutionSurveyor
            //getMainWindowInterface().showPopupForException(
            //        "Erreur lors de l'execution de la commande", ex);

             // TODO traiter l'erreur
             return;
        }

        try {
            refreshLabel((GenericTreeObjectNode)node.getParent().getParent(),true);

            // rafraichi les noeuds "fr�res" si on met � jour une clef
            if (TreeNodeFactory.getValueOfParameter(data, "TABLE_KEY").equals(
                    TreeNodeFactory.getValueOfParameter(data, "FIELD_VALUE"))) {

                Enumeration enum_node = ((GenericTreeObjectNode) node.getParent()).children();
                while (enum_node.hasMoreElements()) {
                    refreshLabel((GenericTreeObjectNode) enum_node.nextElement(), true);
                }
            }
            
        } catch (InnerException exception) {
            Trace trace_errors = TraceAPI.declareTraceErrors("Console");

			trace_errors.writeTrace(
				"Erreur lors de la modification du Label: " +
				exception.getMessage());
            throw new InnerException("Erreur lors de la modification du Label", exception.getMessage(), exception);
        }
        //averti l'interface que le contenu a chang�
        getMainWindowInterface().getTreeInterface().nodeStructureChanged(node);

        
    }

    /**
     * Recuprere les donn�e d'un noeud Item. Si ce noeud � chang�, on met
     * � jour le Label et on rafraichi l'arbre.
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
                select_condition.append(definition.key[k] + "=" + ((IsisParameter) node.getContext(true).get(definition.key[k])).value);
            }
            
            SimpleSelect HistoTable =
                    new SimpleSelect(getSelectedNode(), definition.tableName, new String[]{""}, select_condition.toString());
            data = HistoTable.getFirst();

            if (data != null) {
                IsisParameter[] data_node = node.getObjectParameters();
                //On met les nouvelles donn�es dans le node
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
    protected void initTable()
            throws InnerException
    {
        // Ce processeur ne fonctionne pas sur les noeud Table
        if (getSelectedNode() instanceof GenericTreeClassNode) {
            throw new InnerException("", "", null);
        }
        
        // get the primary keys for current table
        TableDefinitionManager def_cache = TableDefinitionManager.getInstance();
        GenericTreeObjectNode node = (GenericTreeObjectNode)getSelectedNode();
        _tableDefinition= def_cache.getTableDefinition(node.getAgentName(),node.getIClesName(), node.getServiceType(), node.getDefinitionFilePath());
        def_cache.releaseTableDefinitionLeasing(_tableDefinition);

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
	* Cette m�thode est destin�e � �tre appel�e par les sous-classes afin
	* d'ex�cuter une commande (quelle qu'elle soit).
	* La m�thode instancie un objet ExecutionSurveyor qui est r�ellement charg�
	* de toute la proc�dure d'ex�cution de la commande et d'attendre la fin de
	* celle-ci.
	* Si une erreur est d�tect�e, la m�thode g�re l'affichage d'une fen�tre
	* indiquant la nature du probl�me � l'utilisateur.
	*
	* @param  command: Une cha�ne contenant la commande � ex�cuter.
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
		// On cr�e un objet ExecutionSurveyor
		ExecutionSurveyor surveyor = new ExecutionSurveyor();
		// On lance l'ex�cution
		try
		{
		    surveyor.execute(actionId, command, selectedNode, null);
			// On va g�n�rer un message de log
			message = new String[2];
			message[0] = MessageManager.getMessage("&LOG_AdministrationCommand") +
				command;
			message[1] = MessageManager.getMessage("&LOG_CommandResult") +
				MessageManager.getMessage("&LOG_CommandSuccessful");
		}
		catch(InnerException exception)
		{
			trace_errors.writeTrace(
				"Erreur lors de l'ex�cution de la commande: " + command);
			trace_errors.writeTrace("L'erreur est: " + exception.getMessage());
			// On va afficher le message � l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_CannotExecuteCommand", exception);
			// On va g�n�rer un message de log
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
		// On g�n�re la trace de log
		LogServiceProxy.addMessageForAction(actionId, message);
		trace_methods.endOfMethod();
	}

    /**
     * Interroge la table li�e par clef �trang�re et recup�re la liste des
     * clefs
     *
     * @param le champ
     * @return Liste de clefs
     */
    private String[] getFieldList(String field_name) throws InnerException
    {
        String foreign_table="";
        String foreign_column="";

        // recherche de la table+champ li� dans la definition
        for (int i=0; i < _tableDefinition.foreignKeys.length; i++) {
            IsisForeignKeyLink[] fkeys=_tableDefinition.foreignKeys[i].links;
            for (int j=0; j< fkeys.length; j++) {
                if (fkeys[i].localColumnName.equals(field_name)) {
                    foreign_table=_tableDefinition.foreignKeys[i].foreignTableName;
                    foreign_column=fkeys[i].foreignColumnName;

                }
            }
        }

        if (foreign_table.equals("") || foreign_column.equals("")) {
            throw new InnerException("",
                    "Le champ "+field_name+" n'a pas de table li�e par clef �trang�re",
                    null);
        }
        
        // On recupere l'objet ServiceSession
        GenericTreeObjectNode selectedNode = (GenericTreeObjectNode) getSelectedNode();
        ServiceSessionInterface service_session = selectedNode.getServiceSession();
        IndexedList context = selectedNode.getContext(true);

        // On recupere le Proxy associ�
        ServiceSessionProxy session_proxy = new ServiceSessionProxy(service_session);
        // On va chercher les informations dans la table li� par clef etrang�re
        String[] result = session_proxy.getSelectResult(foreign_table, new String[] {foreign_column}, "", "", context);

        //Quirk! suppression entete+ajout etat ""
        result[0]="";
        
        return result;
    }



    /**
	* Cet attribut maintient une r�f�rence param�tr�e sur un objet
	* JComponent correspondant � une zone de saisie pour une colonne. Le
	* param�trage correspond au nom de la colonne.
	* Cet r�f�rence param�tr�e est impl�ment�e sous la forme d'une table de
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
    //TODO pr�voir l'ajout/suppression?
    protected final String replaceCommand="ReplaceAndExec.pl";

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
