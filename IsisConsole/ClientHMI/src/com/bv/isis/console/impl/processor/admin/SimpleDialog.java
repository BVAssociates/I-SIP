/*------------------------------------------------------------
* Copyright (c) 2004 par BV Associates. Tous droits r�serv�s.
* ------------------------------------------------------------
*
* $Source: /cvs/inuit/ClientHMI/src/com/bv/isis/console/impl/processor/admin/SimpleDialog.java,v $
* $Revision: 1.8 $
*
* ------------------------------------------------------------
* DESCRIPTION: Processeur d'administration d'une table quelconque
* DATE:        08/03/2006
* AUTEUR:      T. Zumbiehl
* PROJET:      I-SIS
* GROUPE:      processor.impl.admin
* ------------------------------------------------------------
* CONTROLE DES MODIFICATIONS
*
* $Log: SimpleDialog.java,v $
* Revision 1.8  2009/01/14 14:23:15  tz
* Prise en compte de la modification des packages.
*
* Revision 1.7  2008/02/13 16:26:09  tz
* Gestion des valeurs absentes sur liste d�roulante.
* Gestion des cas de valeurs nulles.
*
* Revision 1.6  2008/01/31 16:45:33  tz
* Gestion de la position de la colonne dans la cl� �trang�re.
*
* Revision 1.5  2008/01/16 15:47:56  tz
* Renvoi de l'erreur lors de la construction du panneau.
*
* Revision 1.4  2008/01/08 17:38:46  tz
* Ajout de la m�thode createConditionForForeignKey().
* Gestion du filtrage des valeurs des listes d�roulantes lorsque :
*  - la cl� �trang�re pointe sur la table parent,
*  - la cl� �trang�re contient la cl� primaire de la table.
*
* Revision 1.3  2007/12/28 17:40:59  tz
* Sortie de fonction sur erreur.
*
* Revision 1.2  2007/12/07 10:31:40  tz
* Evolution pour la prise en compte des FKEY.
* Cr�ation des DialogObject.
* Gestion des v�rifications.
*
* Revision 1.1  2006/03/08 14:07:43  tz
* Ajout de la classe.
*
*
* ------------------------------------------------------------*/
// D�claration du package
package com.bv.isis.console.impl.processor.admin;

//
// Imports syst�me
//
import com.bv.core.trace.TraceAPI;
import com.bv.core.trace.Trace;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.Box;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;

//
// Imports du projet
//
import com.bv.isis.console.node.GenericTreeObjectNode;
import com.bv.isis.console.core.common.InnerException;
import com.bv.isis.console.core.common.IndexedList;
import com.bv.isis.console.com.TableDefinitionManager;
import com.bv.isis.console.com.ServiceSessionProxy;
import com.bv.isis.corbacom.IsisTableDefinition;
import com.bv.isis.corbacom.IsisTableColumn;
import com.bv.isis.corbacom.IsisForeignKey;
import com.bv.isis.corbacom.ServiceSessionInterface;
import com.bv.isis.corbacom.IsisParameter;

/*----------------------------------------------------------
* Nom: SimpleDialog
* 
* Description:
* Cette classe est une sp�cialisation de la classe BaseDialog charg�e de 
* permettre l'administration d'une table quelconque.
* Elle est destin�e � permettre la saisie des informations constituant les 
* champs d'une ligne de donn�es de la table � �diter sous forme d'un ensemble 
* de champs de saisie simples, ou de listes pr�-remplies dans le cas de tables 
* disposant de cl�s �trang�res.
* Elle ne red�finit que les m�thodes validateInput() et makeFormPanel().
* ----------------------------------------------------------*/
public class SimpleDialog 
	extends BaseDialog
{
	// ******************* PUBLIC **********************
	/*----------------------------------------------------------
	* Nom: SimpleDialog
	* 
	* Description:
	* Constructeur. Il permet d'initialiser une instance de la classe.
	* 
	* Si une erreur survient lors de l'initialisation de l'instance, 
	* l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - selectedNode: Une r�f�rence sur un objet GenericTreeObjectNode,
	*  - action: Une cha�ne contenant l'action destin�e � �tre ex�cut�e,
	*  - dialogCaller: Une r�f�rence sur l'interface DialogCallerInterface.
	* 
	* L�ve: InnerException.
 	* ----------------------------------------------------------*/
 	public SimpleDialog(
 		GenericTreeObjectNode selectedNode,
 		String action,
 		DialogCallerInterface dialogCaller
 		)
 		throws
 			InnerException {
 		super(selectedNode, action, dialogCaller);

		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "SimpleDialog");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("action=" + action);
		trace_arguments.writeTrace("dialogCaller=" + dialogCaller);
		_columnObject = new Hashtable();
		_keyObjects = null;
		// On construit le panneau
		makePanel();
		trace_methods.endOfMethod();

 	}

	/*----------------------------------------------------------
	* Nom: validateInput
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe. Elle est appel�e 
	* lorsque l'utilisateur a cliqu� sur le bouton "Valider".
	* Elle va v�rifier la validit� des donn�es saisies en appelant la m�thode 
	* checkValue() pour chacun des objets DialogObject associ� aux champs, 
	* puis va construire la cha�ne de donn�es, via un appel � la m�thode 
	* buildAdministrationCommand(), et enfin va commander la mise � jour de la 
	* table, via la m�thode execute().
	* ----------------------------------------------------------*/
	public void validateInput() {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "validateInput");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		StringBuffer values = null;
		IsisTableDefinition definition = null;

		trace_methods.beginningOfMethod();
		values = new StringBuffer();
		// On va commencer par r�cup�rer le dictionnaire de la table
		GenericTreeObjectNode selected_node = getSelectedNode();
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		try
		{
			definition = 
				manager.getTableDefinition(selected_node.getAgentName(), 
				selected_node.getIClesName(), selected_node.getServiceType(), 
				getTableName(), selected_node.getContext(true), 
				selected_node.getServiceSession());
		}
		catch(InnerException exception)
		{
			// Ca ne devrait normalement pas arriver
			trace_errors.writeTrace("Erreur lors de la r�cup�ration de la " +
				"d�finition de la table " + getTableName() + ": " + 
				exception.getMessage());
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		try {
			// On va traiter les colonnes une par une
			for(int index = 0 ; index < definition.columns.length ; index ++)
			{
				DialogObject dialog_object = null;
				String value = null;
				
				IsisTableColumn column = definition.columns[index];
				trace_debug.writeTrace("Traitement de la colonne " + 
					column.name);
				// On va r�cup�rer l'objet de dialogue associ� � la colonne
				dialog_object = (DialogObject)_columnObject.get(column.name);
				// Si l'objet de dialogue est null, on arr�te
				if(dialog_object == null) {
					trace_errors.writeTrace("L'objet de dialogue pour la " +
						"colonne " + column.name + " est null !");
					values = null;
					String[] extra_info = { column.name };
					getMainWindowInterface().showPopup("Error", 
						"&ERR_NoDialogObjectForColumn", extra_info);
					trace_methods.endOfMethod();
					return;
				}
				// On va v�rifier si la valeur est correcte
				if(dialog_object.checkValue() == false) {
					String[] extra_infos = { column.name };
					// On va afficher un message d'erreur
					getMainWindowInterface().showPopup("Error",
						"&ERR_InvalidValue", extra_infos);
					// On arr�te
					values = null;
					trace_methods.endOfMethod();
					return;
				}
				// On va ajouter la valeur du champ
				if(index > 0)
				{
					// On ajoute le s�parateur
					values.append(definition.separator);
				}
				value = dialog_object.getValue();
				trace_debug.writeTrace("value=" + value);
				values.append(value);
			}
			// On peut construire la commande et l'ex�cuter
			if(values != null) {
				trace_debug.writeTrace("values=" + values);
				execute(buildAdministrationCommand(values.toString()));
			}
		}
		catch(Exception e) {
			manager.releaseTableDefinitionLeasing(definition);
			trace_errors.writeTrace("Erreur lors de la validation des " +
				"donn�es: " + e.getMessage());
			// On va afficher l'erreur � l'utilisateur
			getMainWindowInterface().showPopupForException(
				"&ERR_ErrorWhileValidatingData", e);
		}
		hide();
		trace_methods.endOfMethod();
	}

	// ****************** PROTEGE *********************
	/*----------------------------------------------------------
	* Nom: makeFormPanel
	* 
	* Description:
	* Cette m�thode red�finit celle de la super-classe. Elle est appel�e 
	* lorsque le panneau de saisie des donn�es doit �tre construit.
	* Dans le cas de la saisie des donn�es d'une table quelconque, le panneau 
	* est construit de sorte � ce qu'il y ait une zone de saisie par colonne 
	* de la table dans la majorit� des cas, une liste d�roulante pour les 
	* bool�ens et �galement pour les colonnes composant une cl� �trang�re vers 
	* une autre table, via la m�thode createObjectForForeignKey().
	* 
	* Si une erreur survient lors de la cr�ation du panneau de saisie, 
	* l'exception InnerException est lev�e.
	* 
	* Retourne: Une r�f�rence sur le panneau contenant le formulaire de saisie.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	protected JPanel makeFormPanel()
		throws
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "makeFormPanel");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		int counter = 0;

		trace_methods.beginningOfMethod();
		// Message d'�tat
		getMainWindowInterface().setProgressMaximum(2);
		getMainWindowInterface().setStatus("&Status_BuildingDialog", null, 0);
		// On va construire un panneau avec un GridBagLayout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1,
			0, 50, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(1, 1, 1, 1), 0, 0);
		JPanel panel = new JPanel(layout);
		// On r�cup�re le dictionnaire de la table
		GenericTreeObjectNode selected_node = getSelectedNode();
		TableDefinitionManager manager = TableDefinitionManager.getInstance();
		IsisTableDefinition definition = manager.getTableDefinition(
			selected_node.getAgentName(), selected_node.getIClesName(),
			selected_node.getServiceType(), getTableName(),
			selected_node.getContext(true), selected_node.getServiceSession());
		try {
			// On va cr�er la liste des objets de cl�s
			_keyObjects = new DialogObject[definition.foreignKeys.length];
			// On va traiter les colonnes une par une
			for(int index = 0 ; index < definition.columns.length ; index ++)
			{
				DialogObject parent_object = null;
				DialogObject dialog_object = null;
				int column_index_in_key = -1;
				boolean is_editable = true;
				
				IsisTableColumn column = definition.columns[index];
				trace_debug.writeTrace("Traitement de la colonne: " +
					column.name);
				// On va regarder si la colonne est dans une cl� �trang�re
				for(int loop = 0 ; loop < definition.foreignKeys.length ; 
					loop ++) {
					boolean is_new_object = false;
					
					IsisForeignKey foreign_key = definition.foreignKeys[loop];
					column_index_in_key = getColumnIndexInForeignKey(column, 
						foreign_key); 
					if(column_index_in_key != -1) {
						// La colonne est dans une cl� �trang�re, on va
						// construire un objet si ce n'est pas d�j� fait
						if(_keyObjects[loop] == null) {
							_keyObjects[loop] = createObjectForForeignKey(
								foreign_key, selected_node, column, 
								definition);
							is_new_object = true;
						}
						// Si la colonne est seule dans la cl� �trang�re, il ne
						// sera pas n�cessaire de fabriquer un objet 
						// suppl�mentaire pour elle
						if(foreign_key.links.length == 1) {
							dialog_object = _keyObjects[loop];
						}
						else {
							// Sinon, il faut cr�er un objet qui sera rattach� 
							// � celui repr�sentant la cl� �trang�re
							parent_object = _keyObjects[loop];
							is_editable = false;
							if(is_new_object == true) {
								// On ajoute le composant au panneau
								addComponentInPanel(
									parent_object.getEditionComponent(), panel,
									layout, constraints, counter++, false, 
									null);
							}
						}
						break;
					}
				}
				if(dialog_object == null) {
					// Il faut cr�er un objet de saisie pour la colonne
					dialog_object = new DialogObject(column.type, is_editable,
						isColumnInKey(column, definition), 
						isColumnInNotNull(column, definition));
					if(parent_object != null) {
						// On ajoute l'objet comme rattach� � celui 
						// repr�sentant la cl� �trang�re
						parent_object.addKeyField(dialog_object, 
							column_index_in_key);
					}
				}
				// On ajoute le composant au panneau
				addComponentInPanel(dialog_object.getEditionComponent(), panel,
					layout, constraints, counter++, true, column.name);
				// On l'ajoute � la table de Hash
				_columnObject.put(column.name, dialog_object);
			}
			getMainWindowInterface().setStatus("&Status_BuildingDialog", 
				null, 1);
			// On va g�rer le positionnement des valeurs en fonction de l'action
			manageAction(definition);
		}
		catch(InnerException exception) {
			Trace trace_errors = TraceAPI.declareTraceErrors("Console");
			
			trace_errors.writeTrace("Erreur lors de la construction du " +
				"panneau de saisie: " + exception);
			// On renvoie l'exception
			throw exception;
		}
		manager.releaseTableDefinitionLeasing(definition);
		getMainWindowInterface().setStatus(null, null, 0);
		trace_methods.endOfMethod();
		return panel;
	}

	/*----------------------------------------------------------
	* Nom: finalize
	* 
	* Description:
	* Cette m�thode est appel�e automatiquement par le ramasse miettes de Java 
	* lorsque un objet est sur le point d'�tre d�truit. Elle permet de lib�rer 
	* les ressources.
	* ----------------------------------------------------------*/
	protected void finalize()
		throws
			Throwable {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "finalize");

		trace_methods.beginningOfMethod();
		_columnObject.clear();
		_columnObject = null;
		_keyObjects = null;
		trace_methods.endOfMethod();
	}
	
	// ******************* PRIVE **********************
	/*----------------------------------------------------------
	* Nom: _columnObject
	* 
	* Description:
	* Cet attribut maintient une r�f�rence param�tr�e sur un objet 
	* DialogObject correspondant � une zone de saisie pour une colonne. Le 
	* param�trage correspond au nom de la colonne.
	* Cet r�f�rence param�tr�e est impl�ment�e sous la forme d'une table de 
	* hash.
	* ----------------------------------------------------------*/
	private Hashtable _columnObject;

	/*----------------------------------------------------------
	* Nom: _keyObjects
	* 
	* Description:
	* Cet attribut maintient une liste d'objet DialogObject correspondant aux 
	* cl�s �trang�res de la table. La liste est impl�ment�e sous forme d'un 
	* tableau.
	* ----------------------------------------------------------*/
	private DialogObject[] _keyObjects;

	/*----------------------------------------------------------
	* Nom: createObjectForForeignKey
	* 
	* Description:
	* Cette m�thode est charg�e de la construction d'un DialogObject associ� 
	* � une d�claration de cl� �trang�re.
	* La d�finition de la cl� �trang�re est utilis�e afin de constituer la 
	* liste des colonnes s�lectionn�es, ainsi que pour r�cup�rer le nom de la 
	* table �trang�re.
	* La s�lection est effectu�e sur ces donn�es, et est utilis�e pour cr�er 
	* l'objet DialogObject repr�sentant la cl� �trang�re.
	* Les objets DialogObject correspondant aux colonnes constituant la cl� 
	* �trang�res doivent �tre associ�s � celui repr�sentant la cl� afin de 
	* permettre la mise � jour automatique des valeurs lors de la s�lection 
	* d'un �l�ment dans la liste d�roulante.
	* 
	* Si un probl�me survient lors de l'ex�cution de la s�lection, l'exception 
	* InnerException est lev�e.
	* 
	* Arguments:
	*  - foreignKey: La cl� �trang�re pour laquelle un objet DialogObject doit 
	*    �tre cr��,
	*  - selectedNode: Le noeud d'exploration permettant l'ex�cution de la 
	*    s�lection,
	*  - column: Une r�f�rence sur un objet IsisTableColumn correspondant � la 
	*    colonne concern�e,
	*  - definition: Une r�f�rence sur un objet IsisTableDefinition 
	*    correspondant � la table concern�e.
	* 
	* Retourne: Une r�f�rence sur un objet DialogObject correspondant � la cl� 
	* �trang�re.
	* 
	* L�ve: InnerException.
	* ----------------------------------------------------------*/
	private DialogObject createObjectForForeignKey(
		IsisForeignKey foreignKey,
		GenericTreeObjectNode selectedNode,
		IsisTableColumn column,
		IsisTableDefinition definition
		)
		throws
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "createObjectForForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		DialogObject dialog_object = null;
		String[] selected_columns;
		String[] result = null;
		String[] values = null;
		ServiceSessionInterface service_session = null;
		IndexedList node_context = null;
		IsisTableDefinition foreign_definition = null;
		char column_type = 0;
		boolean alone_in_key = false;
		String values_separator = null;
		boolean is_in_key = false;
		boolean is_in_not_null = false;
		String parent_table_name = null;
		GenericTreeObjectNode parent_node = null; 

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("foreignKey=" + foreignKey);
		trace_arguments.writeTrace("selectedNode=" + selectedNode);
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("definition=" + definition);
		// On va v�rifier que les arguments sont valides
		if(foreignKey == null || foreignKey.links == null || 
			foreignKey.links.length == 0 || selectedNode == null) {
			trace_errors.writeTrace("Au moins un des arguments n'est pas" +
				" valide");
			// On l�ve une exception
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		// On va construire la liste des colonnes
		if(foreignKey.links.length == 1) {
			alone_in_key = true;
			if(column == null || definition == null) {
				trace_errors.writeTrace("L'argument column n'est pas valide");
				// On l�ve une exception
				throw new InnerException("&ERR_InvalidArgument", null, null);
			}
			column_type = column.type;
			is_in_key = isColumnInKey(column, definition);
			is_in_not_null = isColumnInNotNull(column, definition);
		}
		service_session = selectedNode.getServiceSession();
		node_context = selectedNode.getContext(true);
		if(alone_in_key == false) {
			// Il faut r�cup�rer le dictionnaire de la table distante afin d'en 
			// conna�tre le s�parateur
			TableDefinitionManager definition_manager = 
				TableDefinitionManager.getInstance();
			foreign_definition = definition_manager.getTableDefinition(
				selectedNode.getAgentName(), selectedNode.getIClesName(), 
				selectedNode.getServiceType(), foreignKey.foreignTableName, 
				node_context, service_session);
			values_separator = foreign_definition.separator;
			definition_manager.releaseTableDefinitionLeasing(
				foreign_definition);
		}
		// On va regarder si on doit filtrer les r�sultats
		if(getAction().equals("Insert") == true || 
			getAction().equals("Replace") == true) {
			parent_node = getSelectedNode();
			while(parent_node != null) {
				// On recherche le premier nom de table qui ne soit pas 
				// identique � la table courante
				parent_table_name = parent_node.getTableName();
				if(parent_table_name == null || 
					parent_table_name.equals("") == true ||
					parent_table_name.equals(getTableName()) == false) {
					break;
				}
				parent_node = (GenericTreeObjectNode)parent_node.getParent();
			}
		}
		if(parent_table_name != null && 
			parent_table_name.equals("") == false && 
			foreignKey.foreignTableName.equals(parent_table_name) == true) {
			// La cl� �trang�re pointe sur la table parent, il faut n'afficher 
			// que les donn�es correspondant � l'objet parent
			IsisParameter[] parent_parameters = 
				parent_node.getObjectParameters();
			values = new String[1];
			// On va construire la valeur de la cl� � partir des donn�es du
			// parent
			StringBuffer temp_value = new StringBuffer();
			for(int index = 0 ; index < foreignKey.links.length ; index ++) {
				if(index > 0) {
					temp_value.append(values_separator);
				}
				temp_value.append(getParameterValue(
					foreignKey.links[index].foreignColumnName, 
					parent_parameters));
			}
			values[0] = temp_value.toString();
		}
		else {
			String condition = "";
			
			selected_columns = new String[foreignKey.links.length];
			for(int index = 0 ; index < foreignKey.links.length ; index ++) {
				selected_columns[index] = 
					foreignKey.links[index].foreignColumnName;
			}
			if(getAction().equals("Replace") == true) {
				// Pour la modification, on filtre �ventuellement les donn�es
				condition = createConditionForForeignKey(foreignKey,
					definition.key, node_context);
			}
			// On va ex�cuter la s�lection des donn�es sur la table �trang�re
			ServiceSessionProxy session_proxy = 
				new ServiceSessionProxy(service_session);
			result = session_proxy.getWideSelectResult(
				selectedNode.getAgentName(), foreignKey.foreignTableName, 
				selected_columns, condition, "", node_context);
			// On va cr�er le tableau des valeurs en ne prenant qu'� partir du
			// deuxi�me �l�ment
			values = new String[result.length - 1];
			System.arraycopy(result, 1, values, 0, values.length);
		}
		// On peut maintenant cr�er l'objet DialogObject associ�
		dialog_object = new DialogObject(column_type, values,
			values_separator, foreignKey.links.length, alone_in_key, true, 
			is_in_key, is_in_not_null);
		trace_methods.endOfMethod();
		return dialog_object;
	}

	/*----------------------------------------------------------
	* Nom: isColumnInKey
	* 
	* Description:
	* Cette m�thode permet de savoir si la colonne pass�e argument fait partie 
	* de la cl� primaire de la table dont la d�finition est pass�e en second 
	* argument.
	* La v�rification est effectu�e en recherchant la pr�sence de la colonne 
	* dans le champ 'key' de la d�finition.
	* 
	* Arguments:
	*  - column: Une r�f�rence sur un objet IsisTableColumn correspondant � la 
	*    colonne concern�e,
	*  - definition: Une r�f�rence sur un objet IsisTableDefinition 
	*    correspondant � la table concern�e.
	* 
	* Retourne: true si la colonne est r�f�renc�e dans la cl� de la table, 
	* false sinon.
	* ----------------------------------------------------------*/
	private boolean isColumnInKey(
		IsisTableColumn column,
		IsisTableDefinition definition
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "isColumnInKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean is_in_key = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("definition=" + definition);
		// On v�rifie la validit� des arguments
		if(column == null || definition == null) {
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return is_in_key;
		}
		// Normalement, il y a au moins un champ dans la cl� primaire
		if(definition.key == null || definition.key.length == 0) {
			trace_debug.writeTrace("La table n'a pas de cl� primaire");
			// La table n'a pas de cl� primaire, on peut sortir
			trace_methods.endOfMethod();
			return is_in_key;
		}
		// On va parcourir les colonnes de la cl� pour regarder si la colonne
		// en fait partie
		for(int index = 0 ; index < definition.key.length ; index ++) {
			if(definition.key[index].equals(column.name) == true) {
				trace_debug.writeTrace("La colonne a �t� trouv�e dans " +
					"la cl� primaire");
				is_in_key = true;
				// On peut arr�ter la recherche
				break;
			}
		}
		if(is_in_key == false) {
			trace_debug.writeTrace("La colonne n'a pas �t� trouv�e dans la " +
				"cl� primaire");
		}
		trace_methods.endOfMethod();
		return is_in_key;
	}

	/*----------------------------------------------------------
	* Nom: isColumnInNotNull
	* 
	* Description:
	* Cette m�thode permet de savoir si la colonne pass�e argument fait partie 
	* des champs non nuls de la table dont la d�finition est pass�e en second 
	* argument.
	* La v�rification est effectu�e en recherchant la pr�sence de la colonne 
	* dans le champ 'notNull' de la d�finition.
	* 
	* Arguments:
	*  - column: Une r�f�rence sur un objet IsisTableColumn correspondant � la 
	*    colonne concern�e,
	*  - definition: Une r�f�rence sur un objet IsisTableDefinition 
	*    correspondant � la table concern�e.
	* 
	* Retourne: true si la colonne est r�f�renc�e dans les champs non nuls, 
	* false sinon.
	* ----------------------------------------------------------*/
	private boolean isColumnInNotNull(
		IsisTableColumn column,
		IsisTableDefinition definition
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "isColumnInNotNull");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		boolean is_in_not_null = false;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("definition=" + definition);
		// On v�rifie la validit� des arguments
		if(column == null || definition == null) {
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return is_in_not_null;
		}
		// Normalement, il y a au moins un champ dans la cl� primaire
		if(definition.notNull == null || definition.notNull.length == 0) {
			trace_debug.writeTrace("La table n'a pas de champs non nuls");
			// La table n'a pas de cl� primaire, on peut sortir
			trace_methods.endOfMethod();
			return is_in_not_null;
		}
		// On va parcourir les colonnes de la cl� pour regarder si la colonne
		// en fait partie
		for(int index = 0 ; index < definition.notNull.length ; index ++) {
			if(definition.notNull[index].equals(column.name) == true) {
				trace_debug.writeTrace("La colonne a �t� trouv�e dans " +
					"les champs non nuls");
				is_in_not_null = true;
				// On peut arr�ter la recherche
				break;
			}
		}
		if(is_in_not_null == false) {
			trace_debug.writeTrace("La colonne n'a pas �t� trouv�e dans les " +
				"champs non nuls");
		}
		trace_methods.endOfMethod();
		return is_in_not_null;
	}

	/*----------------------------------------------------------
	* Nom: getColumnIndexInForeignKey
	* 
	* Description:
	* Cette m�thode permet de conna�tre la position de la colonne pass�e 
	* argument dans la cl� �trang�re dont la d�finition est pass�e en second 
	* argument.
	* La v�rification est effectu�e en recherchant la pr�sence de la colonne 
	* dans le champ 'localColumnName' des liens de la d�finition.
	* 
	* Arguments:
	*  - column: Une r�f�rence sur un objet IsisTableColumn correspondant � la 
	*    colonne concern�e,
	*  - foreignKey: Une r�f�rence sur un objet IsisForeignKey correspondant � 
	*    la cl� �trang�re concern�e.
	* 
	* Retourne: -1 si la colonne ne fait pas partie de la cl� �trang�re, sinon 
	* la position de celle-ci.
	* ----------------------------------------------------------*/
	private int getColumnIndexInForeignKey(
		IsisTableColumn column,
		IsisForeignKey foreignKey
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "getColumnIndexInForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		int field_position = -1;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("column=" + column);
		trace_arguments.writeTrace("foreignKey=" + foreignKey);
		// On v�rifie la validit� des arguments
		if(column == null || foreignKey == null || 
			foreignKey.links == null || foreignKey.links.length == 0) { 
			trace_errors.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			// On sort
			trace_methods.endOfMethod();
			return field_position;
		}
		// On va parcourir les liens de la cl� pour regarder si la colonne
		// en fait partie
		for(int index = 0 ; index < foreignKey.links.length ; index ++) {
			if(foreignKey.links[index].localColumnName.equals(column.name) == true) {
				trace_debug.writeTrace("La colonne a �t� trouv�e dans " +
					"la cl� �trang�re");
				field_position = index;
				// On peut arr�ter la recherche
				break;
			}
		}
		if(field_position == -1) {
			trace_debug.writeTrace("La colonne n'a pas �t� trouv�e dans la " +
				"cl� �trang�re");
		}
		trace_methods.endOfMethod();
		return field_position;
	}

	/*----------------------------------------------------------
	* Nom: addComponentInPanel
	* 
	* Description:
	* Cette m�thode permet de g�rer l'insertion du composant graphique pass� 
	* en argument dans le panneau de saisie.
	* Si l'argument 'setLabel' est positionn� � true, un libell� est tout 
	* d'abord ajout� dans la zone de gauche, contenant le libell� 'label'.
	* Ensuite, le composant graphique est positionn� en zone de droite.
	* L'ensemble est plac� sur la ligne d'incide 'index'.
	* 
	* Arguments:
	*  - component: Une r�f�rence sur un objet JComponent correspondant au 
	*    composant graphique � positionner,
	*  - panel: Le panneau dans lequel positionner le composant,
	*  - layout: Une r�f�rence sur le LayoutManager du panneau,
	*  - constraints: Une r�f�rence sur les contraintes de positionnement,
	*  - index: L'indice de la ligne d'insertion du composant,
	*  - setLabel: Un bool�en indiquant si un libell� doit �tre construit,
	*  - label: Le libell� � construire.
 	* ----------------------------------------------------------*/
	private void addComponentInPanel(
		JComponent component,
		JPanel panel,
		GridBagLayout layout,
		GridBagConstraints constraints,
		int index,
		boolean setLabel,
		String label
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "addComponentInPanel");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Component label_component = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("component=" + component);
		trace_arguments.writeTrace("panel=" + panel);
		trace_arguments.writeTrace("layout=" + layout);
		trace_arguments.writeTrace("constraints=" + constraints);
		trace_arguments.writeTrace("index=" + index);
		trace_arguments.writeTrace("setLabel=" + setLabel);
		trace_arguments.writeTrace("label=" + label);
		// On v�rifie la validit� des arguments
		if(component == null || panel == null || layout == null ||
			constraints == null) {
			trace_errors.writeTrace("Au moins un argument n'est pas valide");
			// On sort
			trace_methods.endOfMethod();
			return;
		}
		// On fabrique le libell� du champ si n�cessaire
		if(setLabel == true) {
			label_component = new JLabel(label + " :");
		}
		else {
			label_component = Box.createHorizontalGlue();
		}
		// On fixe les contraintes
		constraints.weightx = 0;
		constraints.gridx = 0;
		constraints.gridy = index;
		// On l'ajoute au panneau
		layout.setConstraints(label_component, constraints);
		panel.add(label_component);
		// On positionne la validation par entr�e
		setEnterCallback(component);
		// On fixe les contraintes
		constraints.weightx = 1.0;
		constraints.gridx = 1;
		// On l'ajoute au panneau
		layout.setConstraints(component, constraints);
		panel.add(component);
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: manageAction
	* 
	* Description:
	* Cette m�thode est charg�e de g�rer le postionnement des valeurs dans les 
	* diff�rentes zones en fonction de l'action concern�e.
	* Dans le cas d'un ajout, les valeurs sont �ventuellement pr�lev�es depuis 
	* le contexte du noeud, ou plus exactement des parents.
	* Dans le cas d'une modification ou d'une suppression, les valeurs sont 
	* pr�lev�es depuis les param�tres du noeud.
	* 
	* Si une erreur survient, l'exception InnerException est lev�e.
	* 
	* Arguments:
	*  - definition: Une r�f�rence sur un objet IsisTableDefinition 
	*    correspondant � la d�finition de la table.
	*
	* L�ve: InnerException.
 	* ----------------------------------------------------------*/
	private void manageAction(
		IsisTableDefinition definition
		)
		throws
			InnerException {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "manageAction");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_errors = TraceAPI.declareTraceErrors("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String action = null;
		IsisParameter[] values = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("definition=" + definition);
		// On v�rifie la validit� de l'argument
		if(definition == null) {
			trace_errors.writeTrace("L'argument n'est pas valide !");
			// On l�ve une exception
			trace_methods.endOfMethod();
			throw new InnerException("&ERR_InvalidArgument", null, null);
		}
		action = getAction();
		trace_debug.writeTrace("action=" + action);
		if(action.equals("Insert") == true) {
			values = (IsisParameter[])getSelectedNode().getContext(
					true).toArray(new IsisParameter[0]);
		}
		else {
			values = getSelectedNode().getObjectParameters();
		}
		// On va traiter les cl�s �trang�res une par une
		for(int index = 0 ; index < definition.foreignKeys.length ; index ++) {
			IsisForeignKey foreign_key = definition.foreignKeys[index];
			DialogObject dialog_object = _keyObjects[index];
			StringBuffer key_value = new StringBuffer();
			boolean fkey_in_key = true;
			boolean set_enabled = true;
			
			trace_debug.writeTrace("Traitement de la cl�: " + index);
			if(dialog_object == null) {
				continue;
			}
			// On va tenter de construire la valeur de la cl�
			for(int loop = 0 ; loop < foreign_key.links.length ; loop ++) {
				String column_name = foreign_key.links[loop].localColumnName;
				String column_value = null;
				
				if(loop != 0) {
					key_value.append(dialog_object.getKeyValuesSepatator());
				}
				trace_debug.writeTrace("Traitement de la colonne de cl�: " +
					column_name);
				column_value = getParameterValue(column_name, values);
				trace_debug.writeTrace("column_value=" + column_value);
				if(column_value == null) {
					// Il y a au moins une colonne de vide dans la cl�, on ne
					// positionnera pas de valeur
					key_value = null;
					break;
				}
				key_value.append(column_value);
				if(fkey_in_key == true) {
					if(isColumnInKey(new IsisTableColumn(column_name, 1, 's'),
						definition) == false) {
						fkey_in_key = false;
					}
				}
			}
			if(key_value == null) {
				dialog_object.setValue(null, false);
			}
			else {
				// On n'ajoute la valeur que dans le cas de la suppression
				boolean add_if_missing = action.equals("Remove");
				dialog_object.setValue(key_value.toString(), add_if_missing);
			}
			if(action.equals("Remove") == true || (
				action.equals("Replace") == true && fkey_in_key == true)) {
				set_enabled = false;
			}
			dialog_object.getEditionComponent().setEnabled(set_enabled);
		}
		// On va traiter les colonnes une par une
		for(int index = 0 ; index < definition.columns.length ; index ++)
		{
			DialogObject dialog_object = null;
			String column_value = null;
			boolean next_column = false;
			boolean set_enabled = true;
			
			IsisTableColumn column = definition.columns[index];
			trace_debug.writeTrace("Traitement de la colonne: " +
				column.name);
			// On va regarder si la colonne est dans une cl� �trang�re
			for(int loop = 0 ; loop < definition.foreignKeys.length ; 
				loop ++) {
				IsisForeignKey foreign_key = definition.foreignKeys[loop];
				if(getColumnIndexInForeignKey(column, foreign_key) != -1) {
					// La colonne est dans une cl� �trang�re, on passe � la 
					// suivante
					next_column = true;
					break;
				}
			}
			if(next_column == true) {
				continue;
			}
			// On va r�cup�rer l'objet de repr�sentation depuis la
			// table de Hash
			dialog_object = (DialogObject)_columnObject.get(column.name);
			// On va r�cup�rer �ventuellement la valeur
			column_value = getParameterValue(column.name, values);
			if(dialog_object != null) {
				// Il y a une valeur, on va la positionner
				dialog_object.setValue(column_value, false);
				if(action.equals("Remove") == true || 
					(action.equals("Replace") == true &&
					isColumnInKey(column, definition) == true)) {
					set_enabled = false;
				}
				dialog_object.getEditionComponent().setEnabled(set_enabled);
			}
		}
		trace_methods.endOfMethod();
	}

	/*----------------------------------------------------------
	* Nom: getParameterValue
	* 
	* Description:
	* Cette m�thode est charg�e de retourner la valeur �ventuelle du param�tre 
	* dont le nom est pass� en argument en recherchant sa pr�sence dans le 
	* tableau pass� en second argument.
	* Si le param�tre est trouv�, sa valeur est retourn�e. Sinon, la m�thode 
	* retourne null.
	* 
	* Arguments:
	*  - parameterName: Nom du param�tre dont on souhaite r�cup�rer la valeur,
	*  - parametersArray: Un tableau de IsisParameter dans lequel chercher la 
	*    pr�sence du param�tre.
	* 
	* Retourne: La valeur du param�tre, ou null.
 	* ----------------------------------------------------------*/
	private String getParameterValue(
		String parameterName,
		IsisParameter[] parametersArray
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "getParameterValue");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		String value = null;

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("parameterName=" + parameterName);
		trace_arguments.writeTrace("parametersArray=" + parametersArray);
		// On v�rifie la validit� des arguments
		if(parameterName == null || parameterName.equals("") == true ||
			parametersArray == null || parametersArray.length == 0) {
			trace_debug.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			return value;
		}
		// On va rechercher le param�tre dans le tableau
		for(int index = 0 ; index < parametersArray.length ; index ++) {
			if(parametersArray[index].name.equals(parameterName) == true) {
				value = parametersArray[index].value;
				trace_debug.writeTrace("Param�tre trouv�. Valeur: " + value);
				// Le param�tre a �t� trouv�, on peut arr�ter de chercher
				break;
			}
		}
		trace_methods.endOfMethod();
		return value;
	}

	/*----------------------------------------------------------
	* Nom: createConditionForForeignKey
	* 
	* Description:
	* Cette m�thode est charg�e de construire une condition de filtrage des 
	* donn�es en fonction d'une cl� �trang�re, d'une cl� primaire et d'un 
	* contexte.
	* Cette condition de s�lection est construite en prenant, pour chaque 
	* colonne de la cl� appartenant � la cl� �trang�re, la valeur depuis le 
	* contexte et le nom de la colonne distante.
	* 
	* Arguments:
	*  - foreignKey: La d�finition de la cl� �trang�re,
	*  - key: La d�finition de la cl� primaire,
	*  - context: Le contexte fournissant les valeurs de filtrage.
	* 
	* Retourne: Une cha�ne correspondant � la condition de filtrage, ou une 
	* cha�ne vide si aucune condition ne peut �tre �tablie.
	* ----------------------------------------------------------*/
	private String createConditionForForeignKey(
		IsisForeignKey foreignKey,
		String[] key,
		IndexedList context
		) {
		Trace trace_methods = TraceAPI.declareTraceMethods("Console",
			"SimpleDialog", "createConditionForForeignKey");
		Trace trace_arguments = TraceAPI.declareTraceArguments("Console");
		Trace trace_debug = TraceAPI.declareTraceDebug("Console");
		StringBuffer condition = new StringBuffer();

		trace_methods.beginningOfMethod();
		trace_arguments.writeTrace("foreignKey=" + foreignKey);
		trace_arguments.writeTrace("key=" + key);
		trace_arguments.writeTrace("context=" + context);
		// On v�rifie la validit� des arguments
		if(key == null || key.length == 0 || foreignKey == null ||
			foreignKey.links == null || foreignKey.links.length == 0 ||
			context == null) {
			trace_debug.writeTrace(
				"Au moins un des arguments n'est pas valide !");
			trace_methods.endOfMethod();
			return "";
		}
		// On va rechercher la pr�sence d'au moins une des colonnes de la cl�
		// dans la cl� �trang�re
		for(int index = 0 ; index < key.length ; index ++) {
			trace_debug.writeTrace("Traitement de la colonne: " + key[index]);
			for(int loop = 0 ; loop < foreignKey.links.length ; loop ++) {
				if(key[index].equals(
					foreignKey.links[loop].localColumnName) == true) {
					trace_debug.writeTrace("La colonne de la cl� est " +
						"pr�sente dans la cl� �trang�re");
					// La colonne de la cl� est pr�sente, on va ajouter une
					// condition sur la colonne distante
					if(condition.length() > 0) {
						condition.append(" AND ");
					}
					condition.append(foreignKey.links[loop].foreignColumnName);
					condition.append("=");
					IsisParameter parameter = (IsisParameter)context.get(key[index]);
					if(parameter != null) {
						condition.append(parameter.value);
					}
					break;
				}
			}
		}
		trace_debug.writeTrace("condition='" + condition.toString() + "'");
		trace_methods.endOfMethod();
		return condition.toString();
	}
}
