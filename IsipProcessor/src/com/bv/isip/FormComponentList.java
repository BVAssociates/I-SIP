package com.bv.isip;

import com.bv.core.message.MessageManager;
import com.bv.isis.console.com.CommonFeatures;
import com.bv.isis.console.com.LogServiceProxy;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.impl.processor.admin.ExecutionSurveyor;
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.corbacom.IsisForeignKeyLink;
import com.bv.isis.corbacom.IsisParameter;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.ServiceSessionInterface;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author BAUCHART
 */
public class FormComponentList extends JPanel
        implements FormComponentInterface {


    public FormComponentList(GenericTreeObjectNode node, String field,boolean admin)
            throws InnerException
    {
        super();
        _component = new JComboBox();
        _selectedNode = node;

        GridBagConstraints contraints=new GridBagConstraints();
        contraints.fill=GridBagConstraints.BOTH;
        contraints.anchor=GridBagConstraints.EAST;
        contraints.gridx=0;
        contraints.gridy=0;
        contraints.weightx=1;
        
        setLayout(new GridBagLayout());
        add(_component,contraints);
        init(node,field);

        if (admin) {
            GridBagConstraints contraints_button = new GridBagConstraints();
            contraints_button.fill = GridBagConstraints.VERTICAL;
            contraints_button.gridx = 1;
            contraints_button.gridy = 0;
            add(makeAdministrate(),contraints_button);
        }
    }


    public void init(GenericTreeObjectNode node,String field) throws InnerException
    {
        IsisParameter[] parameter_list=node.getObjectParameters();
        IsisParameter parameter=null;
        String table_name;

        for (int i=0; i < parameter_list.length; i++) {
            if (parameter_list[i].name.equals(field)) {
                parameter=parameter_list[i];
            }
        }
        table_name=node.getTableName();
        TableDefinitionManager def_cache=TableDefinitionManager.getInstance();
        IsisTableDefinition definition = def_cache.getTableDefinition(node.getAgentName(), node.getIClesName(), node.getServiceType(), node.getDefinitionFilePath());
        def_cache.releaseTableDefinitionLeasing(definition);

        if (parameter == null) {
            throw new InnerException("Erreur pendant la constuction du panneau",
                    "Impossible de trouver le champ "+field+" dans le noeud selectionné",
                    null);
        }

        ComboBoxModel datamodel=new DefaultComboBoxModel(getOptionsForeign(node, parameter, definition));
        _component.setModel(datamodel);
    }

    public String getText()
    {
        return _component.getSelectedItem().toString();
    }

    public void setText(String value) throws InnerException
    {
        //TODO : what if value is not in list?
        boolean found_value=false;
        for(int i=0; i < _component.getItemCount(); i++)  {
            if (_component.getItemAt(i).equals(value)) {
                   found_value=true;
            }
        }

        if (!found_value) {
            _component.addItem(value);
        }
        _component.setSelectedItem(value);
    }

    private JComponent makeAdministrate()
    {
        JButton admin_button=new JButton("+");
        admin_button.setToolTipText("Ajouter");

        admin_button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                newOption();
            }
        });
        return admin_button;
    }

    private void newOption()
    {
        String new_option = JOptionPane.showInputDialog(this, "Ajouter l'entrée :");

        boolean found_value = false;
        for (int i = 0; i < _component.getItemCount(); i++) {
            if (_component.getItemAt(i).equals(new_option)) {
                found_value = true;
            }
        }

        if (found_value) {
            //TODO print a warning?
            _component.setSelectedItem(new_option);
        }
        else {
            try {
                if (new_option != null) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    String date_auto = dateFormat.format(new Date());
                    int result = JOptionPane.showConfirmDialog(this,
                            "Etes vous sur de vouloir créér le projet : \"" + new_option + "\" ?",
                            "Creation de projet",
                            JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        execute("Insert INTO PROJECT_TYPE VALUES \"" + new_option + "@@" + date_auto + "@\"");
                        _component.addItem(new_option);
                        _component.setSelectedItem(new_option);
                    }
                }
            } catch (InnerException ex) {
                //TODO
            }
        }
    }

    
    /**
     * Interroge la table liée par clef étrangère et recupère la liste des
     * clefs
     *
     * @param le champ
     * @return Liste de clefs
     */
    private String[] getOptionsForeign(GenericTreeObjectNode selectedNode, IsisParameter parameter, IsisTableDefinition definition)
            throws InnerException
    {

        String field_name=parameter.name;

        // recherche de la table+champ lié dans la definition
        for (int i=0; i < definition.foreignKeys.length; i++) {
            IsisForeignKeyLink[] fkeys=definition.foreignKeys[i].links;
            for (int j=0; j< fkeys.length; j++) {
                if (fkeys[j].localColumnName.equals(field_name)) {
                    _foreignTable=definition.foreignKeys[i].foreignTableName;
                    _foreignColumn=fkeys[j].foreignColumnName;

                }
            }
        }

        if (_foreignTable == null || _foreignTable.equals("") || _foreignColumn.equals("")) {
            throw new InnerException("",
                    "Le champ "+field_name+" n'a pas de table liée par clef étrangère",
                    null);
        }

        // On recupere l'objet ServiceSession
        ServiceSessionInterface service_session = selectedNode.getServiceSession();
        IndexedList context = selectedNode.getContext(true);

        // On recupere le Proxy associé
        ServiceSessionProxy session_proxy = new ServiceSessionProxy(service_session);
        // On va chercher les informations dans la table lié par clef etrangère
        String[] result = session_proxy.getSelectResult(_foreignTable, new String[] {_foreignColumn}, "", "", context);

        //Quirk! suppression entete+ajout etat ""
        result[0]="";

        return result;
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


        //recuperation de l'ID'
        String actionId =
                LogServiceProxy.getActionIdentifier(_selectedNode.getAgentName(),
                "Mise a jour champ", null, _selectedNode.getServiceName(),
                _selectedNode.getServiceType(), _selectedNode.getIClesName());

		// S'il n'y a pas de valeur, on sort
		if(command == null || command.equals("") == true)
		{
			return;
		}
		// On crée un objet ExecutionSurveyor
		ExecutionSurveyor surveyor = new ExecutionSurveyor();
		// On lance l'exécution
		try
		{
		    surveyor.execute(actionId, command, _selectedNode, null);
			// On va générer un message de log
			message = new String[2];
			message[0] = MessageManager.getMessage("&LOG_AdministrationCommand") +
				command;
			message[1] = MessageManager.getMessage("&LOG_CommandResult") +
				MessageManager.getMessage("&LOG_CommandSuccessful");
		}
		catch(InnerException exception)
		{
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
	}

    /**
     * stocke la reference vers la combobox
     */
    private JComboBox _component;

    /**
     * stocke la reference vers la combobox
     */
    private GenericTreeObjectNode _selectedNode;

    /**
     * stocke la table utilisée pour la liste
     */
    private String _foreignTable;

    /**
     * stocke les colonnes utilisées pour la liste
     */
    private String _foreignColumn;

    /**
     * stocke le champ en cours d'edition
     */
    private String _field;

    
    /**
     * Classe interne stockant une liste renvoyée par un Select
     * Dans un JOptionPane, permet d'afficher les champs, puis permet de
     * recuperer uniquement la clef de l'element selectionné
     */
    class IsisParameterOption
    {

        /**
         * Constructeur de IsisParameterOption
         *
         * @param fields ligne renvoyée par un Select. Doit contenir les
         * clefs primaire en premier. Ils seront supprimés de l'affichage
         * @param definition la IsisTableDefinition corresondante à fields
         */
        IsisParameterOption(IsisParameter[] fields, IsisTableDefinition definition)
        {
            _fields = fields;
            _definition = definition;

            StringBuilder temp_key = new StringBuilder();
            String sep = "";
            for (int i = 0; i < _definition.key.length; i++) {
                temp_key.append(sep);
                temp_key.append(fields[i].value);
                sep = _definition.separator;
            }
            _key = temp_key.toString();
        }

        /**
         * methode utilisée dans le JOptionPane pour afficher le texte
         */
        @Override
        public String toString()
        {
            StringBuilder return_string = new StringBuilder();
            String sep = "";



            for (int i = _definition.key.length; i < _fields.length; i++) {
                return_string.append(sep);
                return_string.append(_fields[i].value);
                sep = _definition.separator;
            }
            return return_string.toString();
        }

        /**
         * recupère la clef sous forme de texte
         */
        public String getKey()
        {
            return _key;
        }

        public boolean equals(IsisParameterOption object) {
            return getKey().equals(object.getKey());
        }
        
        private String _key;
        private IsisParameter[] _fields;
        private IsisTableDefinition _definition;
    }
}
