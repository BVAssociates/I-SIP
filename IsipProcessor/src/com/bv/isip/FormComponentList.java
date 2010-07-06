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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 *
 * @author BAUCHART
 */
public class FormComponentList extends JPanel
        implements FormComponentInterface {


    public FormComponentList(GenericTreeObjectNode node, String field,IsisTableDefinition definition)
            throws InnerException
    {
        super();
        _component = new JComboBox();
        _selectedNode = node;
        _tableDefinition = definition;

        GridBagConstraints contraints=new GridBagConstraints();
        contraints.fill=GridBagConstraints.BOTH;
        contraints.anchor=GridBagConstraints.EAST;
        contraints.gridx=0;
        contraints.gridy=0;
        contraints.weightx=1;
        
        setLayout(new GridBagLayout());
        add(_component,contraints);
        init(node,field);

    }


    public void init(GenericTreeObjectNode node,String field) throws InnerException
    {

        ComboBoxModel datamodel=new DefaultComboBoxModel(getOptionsForeign(node, _tableDefinition,field));
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

    
    /**
     * Interroge la table li�e par clef �trang�re et recup�re la liste des
     * clefs
     *
     * @param le champ
     * @return Liste de clefs
     */
    private String[] getOptionsForeign(GenericTreeObjectNode selectedNode, IsisTableDefinition definition,String field_name)
            throws InnerException
    {

        // recherche de la table+champ li� dans la definition
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
                    "Le champ "+field_name+" n'a pas de table li�e par clef �trang�re",
                    null);
        }

        // On recupere l'objet ServiceSession
        ServiceSessionInterface service_session = selectedNode.getServiceSession();
        IndexedList context = selectedNode.getContext(true);

        // On recupere le Proxy associ�
        ServiceSessionProxy session_proxy = new ServiceSessionProxy(service_session);
        // On va chercher les informations dans la table li� par clef etrang�re
        String[] result = session_proxy.getSelectResult(_foreignTable, new String[] {_foreignColumn}, "", "", context);

        //Quirk! suppression entete+ajout etat ""
        result[0]="";

        return result;
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
		// On cr�e un objet ExecutionSurveyor
		ExecutionSurveyor surveyor = new ExecutionSurveyor();
		// On lance l'ex�cution
		try
		{
		    surveyor.execute(actionId, command, _selectedNode, null);
			// On va g�n�rer un message de log
			message = new String[2];
			message[0] = MessageManager.getMessage("&LOG_AdministrationCommand") +
				command;
			message[1] = MessageManager.getMessage("&LOG_CommandResult") +
				MessageManager.getMessage("&LOG_CommandSuccessful");
		}
		catch(InnerException exception)
		{
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
	}

    /**
     * stocke la reference vers la combobox
     */
    protected JComboBox _component;

    /**
     * stocke la reference vers la combobox
     */
    protected GenericTreeObjectNode _selectedNode;
    
    /**
     * stocke la reference vers la definition de la table en cours
     */
    private IsisTableDefinition _tableDefinition;

    /**
     * stocke la table utilis�e pour la liste
     */
    protected String _foreignTable;

    /**
     * stocke les colonnes utilis�es pour la liste
     */
    protected String _foreignColumn;

    /**
     * stocke le champ en cours d'edition
     */
    protected String _field;

    
    /**
     * Classe interne stockant une liste renvoy�e par un Select
     * Dans un JOptionPane, permet d'afficher les champs, puis permet de
     * recuperer uniquement la clef de l'element selectionn�
     */
    class IsisParameterOption
    {

        /**
         * Constructeur de IsisParameterOption
         *
         * @param fields ligne renvoy�e par un Select. Doit contenir les
         * clefs primaire en premier. Ils seront supprim�s de l'affichage
         * @param definition la IsisTableDefinition corresondante � fields
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
         * methode utilis�e dans le JOptionPane pour afficher le texte
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
         * recup�re la clef sous forme de texte
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
